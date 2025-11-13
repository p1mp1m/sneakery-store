<template>
  <div 
    class="toast-container"
    role="region"
    aria-label="Thông báo"
    aria-live="polite"
    aria-atomic="false"
  >
    <TransitionGroup 
      name="toast"
      tag="div"
      class="toast-list"
      enter-active-class="transition-all duration-300 ease-out"
      leave-active-class="transition-all duration-300 ease-in"
      enter-from-class="opacity-0 translate-x-20 scale-95"
      enter-to-class="opacity-100 translate-x-0 scale-100"
      leave-from-class="opacity-100 translate-x-0 scale-100"
      leave-to-class="opacity-0 -translate-x-20 scale-95"
    >
      <div
        v-for="(toast, index) in displayedToasts"
        :key="toast.id"
        :class="[
          'toast-item',
          {
            'toast-success': toast.type === 'success',
            'toast-error': toast.type === 'error',
            'toast-warning': toast.type === 'warning',
            'toast-info': toast.type === 'info',
            'toast-closable': toast.closable
          }
        ]"
        :role="toast.type === 'error' ? 'alert' : 'status'"
        :aria-live="toast.type === 'error' ? 'assertive' : 'polite'"
        @click="toast.closable ? removeToast(toast.id) : null"
        @keydown.enter="toast.closable ? removeToast(toast.id) : null"
        @keydown.space.prevent="toast.closable ? removeToast(toast.id) : null"
        @mouseenter="pauseToast(toast.id)"
        @mouseleave="resumeToast(toast.id)"
        @touchstart="handleTouchStart($event, toast.id)"
        @touchmove="handleTouchMove($event, toast.id)"
        @touchend="handleTouchEnd($event, toast.id)"
        :tabindex="toast.closable ? 0 : -1"
      >
        <div class="toast-icon">
          <i 
            class="material-icons"
            :aria-hidden="true"
          >
            {{ getIcon(toast.type) }}
          </i>
        </div>
        
        <div class="flex-1 min-w-0">
          <div v-if="toast.title" class="toast-title">{{ toast.title }}</div>
          <div 
            v-if="toast.message" 
            class="toast-message"
            :class="{ 'toast-message-expanded': expandedMessages[toast.id] }"
          >
            <span v-if="!expandedMessages[toast.id] && toast.message.length > 100">
              {{ toast.message.substring(0, 100) }}...
            </span>
            <span v-else>{{ toast.message }}</span>
            <button
              v-if="toast.message.length > 100"
              class="toast-expand-btn"
              @click.stop="toggleMessage(toast.id)"
              type="button"
              aria-label="Mở rộng/thu gọn tin nhắn"
            >
              {{ expandedMessages[toast.id] ? 'Thu gọn' : 'Xem thêm' }}
            </button>
          </div>
          
          <!-- Action Buttons -->
          <div 
            v-if="toast.actions && toast.actions.length > 0" 
            class="toast-actions"
          >
            <button
              v-for="(action, index) in toast.actions"
              :key="index"
              class="toast-action-btn"
              :class="{
                'toast-action-primary': action.primary !== false,
                'toast-action-secondary': action.primary === false,
                'toast-action-loading': action.loading
              }"
              @click.stop="handleAction(toast.id, action, index)"
              @keydown.enter.stop="handleAction(toast.id, action, index)"
              @keydown.space.stop.prevent="handleAction(toast.id, action, index)"
              :disabled="action.loading || action.disabled"
              :aria-label="action.label"
              type="button"
            >
              <i 
                v-if="action.loading" 
                class="material-icons toast-action-spinner"
                aria-hidden="true"
              >hourglass_empty</i>
              <span v-else>{{ action.label }}</span>
            </button>
          </div>
        </div>

        <button
          v-if="toast.closable"
          class="toast-close"
          @click.stop="removeToast(toast.id)"
          @keydown.enter.stop="removeToast(toast.id)"
          @keydown.space.stop.prevent="removeToast(toast.id)"
          aria-label="Đóng thông báo"
          type="button"
        >
          <i class="material-icons" aria-hidden="true">close</i>
        </button>

        <!-- Progress Bar -->
        <div 
          v-if="toast.duration > 0" 
          class="toast-progress-bar"
          :class="{
            'toast-progress-paused': toast.isPaused
          }"
        >
          <div 
            class="toast-progress-fill"
            :style="{ width: `${toast.progress || 100}%` }"
          ></div>
        </div>
      </div>
    </TransitionGroup>
    
    <!-- Show All Button (Mobile only) -->
    <button
      v-if="isMobile && toasts.length > maxMobileToasts"
      class="toast-show-all-btn"
      @click="showAllToasts = !showAllToasts"
      type="button"
      aria-label="Xem tất cả thông báo"
    >
      <i class="material-icons" aria-hidden="true">{{ showAllToasts ? 'expand_less' : 'expand_more' }}</i>
      <span>{{ showAllToasts ? 'Thu gọn' : `Xem tất cả (${toasts.length - maxMobileToasts} thêm)` }}</span>
    </button>
  </div>
</template>

<script>
import toastService from '@/utils/toastService'

export default {
  name: 'ToastContainer',
  data() {
    return {
      toasts: [],
      expandedMessages: {},
      showAllToasts: false,
      maxMobileToasts: 3,
      isMobile: false,
      touchStartX: null,
      touchStartY: null,
      touchStartTime: null,
      swipingToastId: null
    }
  },
  computed: {
    displayedToasts() {
      if (this.isMobile && !this.showAllToasts) {
        return this.toasts.slice(0, this.maxMobileToasts)
      }
      return this.toasts
    }
  },
  mounted() {
    this.unsubscribe = toastService.subscribe((toasts) => {
      this.toasts = toasts
    })
    this.checkMobile()
    window.addEventListener('resize', this.checkMobile)
  },
  beforeUnmount() {
    if (this.unsubscribe) {
      this.unsubscribe()
    }
    window.removeEventListener('resize', this.checkMobile)
  },
  methods: {
    removeToast(id) {
      toastService.removeToast(id)
    },
    pauseToast(id) {
      toastService.pauseToast(id)
    },
    resumeToast(id) {
      toastService.resumeToast(id)
    },
    handleAction(toastId, action, index) {
      if (action.loading || action.disabled) return
      
      // Set loading state if callback is async
      if (action.onClick) {
        const result = action.onClick()
        
        // If onClick returns a promise, set loading state
        if (result && typeof result.then === 'function') {
          const toast = this.toasts.find(t => t.id === toastId)
          if (toast && toast.actions && toast.actions[index]) {
            toast.actions[index].loading = true
            this.$forceUpdate()
            
            result
              .then(() => {
                if (toast && toast.actions && toast.actions[index]) {
                  toast.actions[index].loading = false
                }
              })
              .catch(() => {
                if (toast && toast.actions && toast.actions[index]) {
                  toast.actions[index].loading = false
                }
              })
          }
        }
      }
    },
    getIcon(type) {
      const icons = {
        success: 'check_circle',
        error: 'error',
        warning: 'warning',
        info: 'info'
      }
      return icons[type] || 'info'
    },
    toggleMessage(toastId) {
      this.expandedMessages = {
        ...this.expandedMessages,
        [toastId]: !this.expandedMessages[toastId]
      }
    },
    checkMobile() {
      this.isMobile = window.innerWidth <= 640
    },
    handleTouchStart(event, toastId) {
      if (!this.isMobile) return
      this.touchStartX = event.touches[0].clientX
      this.touchStartY = event.touches[0].clientY
      this.touchStartTime = Date.now()
      this.swipingToastId = toastId
    },
    handleTouchMove(event, toastId) {
      if (!this.isMobile || this.swipingToastId !== toastId) return
      const touchX = event.touches[0].clientX
      const touchY = event.touches[0].clientY
      const deltaX = touchX - this.touchStartX
      const deltaY = touchY - this.touchStartY
      
      // Only handle horizontal swipe
      if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 10) {
        const toastElement = event.currentTarget
        toastElement.style.transform = `translateX(${deltaX}px)`
        toastElement.style.opacity = Math.max(0, 1 - Math.abs(deltaX) / 200)
      }
    },
    handleTouchEnd(event, toastId) {
      if (!this.isMobile || this.swipingToastId !== toastId) return
      
      const touchEndX = event.changedTouches[0].clientX
      const deltaX = touchEndX - this.touchStartX
      const deltaTime = Date.now() - this.touchStartTime
      const swipeThreshold = 100
      const swipeSpeed = Math.abs(deltaX) / deltaTime
      
      const toastElement = event.currentTarget
      toastElement.style.transform = ''
      toastElement.style.opacity = ''
      
      // Dismiss if swiped far enough or fast enough
      if (Math.abs(deltaX) > swipeThreshold || (swipeSpeed > 0.5 && Math.abs(deltaX) > 50)) {
        this.removeToast(toastId)
      }
      
      this.touchStartX = null
      this.touchStartY = null
      this.touchStartTime = null
      this.swipingToastId = null
    }
  }
}
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 5rem;
  right: 1rem;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-width: 24rem;
}

.toast-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.toast-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  backdrop-filter: blur(12px) saturate(160%);
  -webkit-backdrop-filter: blur(12px) saturate(160%);
  border-left: 4px solid;
  min-width: 280px;
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.toast-item:hover {
  box-shadow: var(--shadow-xl);
  transform: translateX(-4px);
}

.toast-closable {
  cursor: pointer;
}

.toast-success {
  background: var(--bg-glass);
  border-color: var(--color-success);
}

.dark .toast-success {
  background: var(--bg-glass-dark);
}

.toast-error {
  background: var(--bg-glass);
  border-color: var(--color-error);
}

.dark .toast-error {
  background: var(--bg-glass-dark);
}

.toast-warning {
  background: var(--bg-glass);
  border-color: var(--color-warning);
}

.dark .toast-warning {
  background: var(--bg-glass-dark);
}

.toast-info {
  background: var(--bg-glass);
  border-color: var(--color-info);
}

.dark .toast-info {
  background: var(--bg-glass-dark);
}

.toast-icon {
  flex-shrink: 0;
  width: 1.5rem;
  height: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.toast-success .toast-icon {
  color: var(--color-success);
}

.toast-error .toast-icon {
  color: var(--color-error);
}

.toast-warning .toast-icon {
  color: var(--color-warning);
}

.toast-info .toast-icon {
  color: var(--color-info);
}

.toast-title {
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  color: var(--text-primary);
  margin-bottom: var(--spacing-xs);
}

.dark .toast-title {
  color: var(--text-primary-dark);
}

.toast-message {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
  word-wrap: break-word;
}

.dark .toast-message {
  color: var(--text-secondary-dark);
}

.toast-expand-btn {
  margin-left: var(--spacing-xs);
  padding: 0.125rem 0.25rem;
  font-size: var(--text-xs);
  color: var(--color-purple-600);
  background: transparent;
  border: none;
  cursor: pointer;
  text-decoration: underline;
  transition: color var(--transition-fast);
}

.dark .toast-expand-btn {
  color: var(--color-purple-400);
}

.toast-expand-btn:hover {
  color: var(--color-purple-700);
}

.dark .toast-expand-btn:hover {
  color: var(--color-purple-300);
}

.toast-close {
  flex-shrink: 0;
  width: 1.5rem;
  height: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.toast-close:hover {
  color: var(--text-primary);
  background: var(--bg-secondary);
}

.dark .toast-close:hover {
  color: var(--text-primary-dark);
  background: var(--bg-secondary-dark);
}

.toast-close:focus-visible {
  outline: 2px solid var(--color-purple-400);
  outline-offset: 2px;
}

.toast-progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: rgba(0, 0, 0, 0.1);
  overflow: hidden;
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
}

.dark .toast-progress-bar {
  background: rgba(255, 255, 255, 0.1);
}

.toast-progress-fill {
  height: 100%;
  transition: width 0.1s linear;
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
}

.toast-success .toast-progress-fill {
  background: var(--color-success);
}

.toast-error .toast-progress-fill {
  background: var(--color-error);
}

.toast-warning .toast-progress-fill {
  background: var(--color-warning);
}

.toast-info .toast-progress-fill {
  background: var(--color-info);
}

.toast-progress-paused .toast-progress-fill {
  transition: none;
  opacity: 0.7;
}

.toast-actions {
  display: flex;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-sm);
  flex-wrap: wrap;
}

.toast-action-btn {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  white-space: nowrap;
}

.toast-action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.toast-action-primary {
  background: var(--color-purple-600);
  color: white;
}

.toast-action-primary:hover:not(:disabled) {
  background: var(--color-purple-700);
}

.toast-action-secondary {
  background: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.dark .toast-action-secondary {
  background: var(--bg-secondary-dark);
  color: var(--text-primary-dark);
  border-color: var(--border-color-dark);
}

.toast-action-secondary:hover:not(:disabled) {
  background: var(--bg-tertiary);
}

.dark .toast-action-secondary:hover:not(:disabled) {
  background: var(--bg-tertiary-dark);
}

.toast-action-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.toast-show-all-btn {
  display: none;
  padding: var(--spacing-sm);
  margin-top: var(--spacing-xs);
  border-radius: var(--radius-md);
  background: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  cursor: pointer;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
}

.dark .toast-show-all-btn {
  background: var(--bg-secondary-dark);
  color: var(--text-primary-dark);
  border-color: var(--border-color-dark);
}

.toast-show-all-btn:hover {
  background: var(--bg-tertiary);
}

.dark .toast-show-all-btn:hover {
  background: var(--bg-tertiary-dark);
}

@media (max-width: 640px) {
  .toast-container {
    top: auto;
    bottom: 1rem;
    right: 1rem;
    left: 1rem;
    max-width: calc(100vw - 2rem);
  }
  
  .toast-list {
    flex-direction: column-reverse;
  }
  
  .toast-item {
    min-width: auto;
    max-width: 100%;
    touch-action: pan-y;
  }
  
  .toast-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .toast-action-btn {
    width: 100%;
    justify-content: center;
  }
  
  .toast-show-all-btn {
    display: flex;
  }
}

@media (prefers-reduced-motion: reduce) {
  .toast-item {
    transition: none;
  }
  
  .toast-item:hover {
    transform: none;
  }
}
</style>




<template>
  <div 
    class="fixed top-20 right-4 z-[9999] flex flex-col gap-2 max-w-sm"
    role="region"
    aria-label="Thông báo"
    aria-live="polite"
    aria-atomic="false"
  >
    <TransitionGroup 
      name="toast"
      tag="div"
      enter-active-class="transition-all duration-300 ease-out"
      leave-active-class="transition-all duration-300 ease-in"
      enter-from-class="opacity-0 translate-x-20 scale-95"
      enter-to-class="opacity-100 translate-x-0 scale-100"
      leave-from-class="opacity-100 translate-x-0 scale-100"
      leave-to-class="opacity-0 -translate-x-20 scale-95"
    >
      <div
        v-for="toast in toasts"
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
          <div v-if="toast.message" class="toast-message">{{ toast.message }}</div>
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
      </div>
    </TransitionGroup>
  </div>
</template>

<script>
import toastService from '@/utils/toastService'

export default {
  name: 'ToastContainer',
  data() {
    return {
      toasts: []
    }
  },
  mounted() {
    this.unsubscribe = toastService.subscribe((toasts) => {
      this.toasts = toasts
    })
  },
  beforeUnmount() {
    if (this.unsubscribe) {
      this.unsubscribe()
    }
  },
  methods: {
    removeToast(id) {
      toastService.removeToast(id)
    },
    getIcon(type) {
      const icons = {
        success: 'check_circle',
        error: 'error',
        warning: 'warning',
        info: 'info'
      }
      return icons[type] || 'info'
    }
  }
}
</script>

<style scoped>
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
}

.dark .toast-message {
  color: var(--text-secondary-dark);
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

@media (max-width: 640px) {
  .toast-item {
    min-width: auto;
    max-width: calc(100vw - 2rem);
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




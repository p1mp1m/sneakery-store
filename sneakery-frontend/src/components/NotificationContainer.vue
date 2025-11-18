<template>
  <Teleport to="body">
    <div 
      v-if="displayedNotifications.length > 0"
      class="notification-container"
      role="region"
      aria-label="Thông báo"
      aria-live="polite"
      aria-atomic="false"
    >
      <TransitionGroup
        name="notification"
        tag="div"
        class="notification-list"
      >
        <div
          v-for="notification in displayedNotifications"
          :key="notification.id"
          :class="[
            'notification-item',
            {
              'notification-success': notification.type === 'success',
              'notification-error': notification.type === 'error',
              'notification-warning': notification.type === 'warning',
              'notification-info': notification.type === 'info',
            }
          ]"
          :data-notification-id="notification.id"
          :data-notification-type="notification.type"
          :role="notification.type === 'error' ? 'alert' : 'status'"
          :aria-live="notification.type === 'error' ? 'assertive' : 'polite'"
        >
          <!-- Icon -->
          <div class="notification-icon-wrapper">
            <div class="notification-icon">
              <i 
                class="material-icons"
                :aria-hidden="true"
              >
                {{ getIcon(notification.type) }}
              </i>
            </div>
          </div>
          
          <!-- Content -->
          <div class="notification-content">
            <div class="notification-text">
              <div v-if="notification.title" class="notification-title">{{ notification.title }}</div>
              <div v-if="notification.message" class="notification-message">{{ notification.message }}</div>
            </div>
            
            <!-- Action Button -->
            <button
              v-if="notification.action"
              class="notification-action-btn"
              @click.stop="handleAction(notification.id, notification.action)"
              type="button"
              :disabled="notification.action.loading || notification.action.disabled"
            >
              <i 
                v-if="notification.action.loading" 
                class="material-icons notification-action-spinner"
                aria-hidden="true"
              >hourglass_empty</i>
              <span v-else>{{ notification.action.label }}</span>
            </button>
          </div>

          <!-- Close Button -->
          <button
            v-if="notification.closable"
            class="notification-close"
            @click.stop="removeNotification(notification.id)"
            @keydown.enter.stop="removeNotification(notification.id)"
            @keydown.space.stop.prevent="removeNotification(notification.id)"
            aria-label="Đóng thông báo"
            type="button"
          >
            <i class="material-icons" aria-hidden="true">close</i>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script>
import notificationService from '@/utils/notificationService'

export default {
  name: 'NotificationContainer',
  data() {
    return {
      notifications: []
    }
  },
  computed: {
    displayedNotifications() {
      // Hiển thị tối đa 3 notifications cùng lúc
      return this.notifications.slice(0, 3)
    }
  },
  mounted() {
    // Subscribe to notification changes
    this.unsubscribe = notificationService.subscribe((notifications) => {
      // Direct assignment for better reactivity
      this.notifications = Array.isArray(notifications) ? [...notifications] : []
      
      // Debug: Check if notifications are being received
      if (import.meta.env.DEV && notifications.length > 0) {
        console.log('🔔 NotificationContainer: Received', notifications.length, 'notifications')
        console.log('🔔 Displayed:', this.displayedNotifications.length)
      }
    })
    
    // Debug: Check if component is mounted
    if (import.meta.env.DEV) {
      console.log('✅ NotificationContainer mounted')
    }
  },
  beforeUnmount() {
    if (this.unsubscribe) {
      this.unsubscribe()
    }
  },
  methods: {
    removeNotification(id) {
      notificationService.removeNotification(id)
    },
    handleAction(notificationId, action) {
      if (action.loading || action.disabled) return
      
      // Support both 'handler' and 'onClick' for backward compatibility
      const handler = action.handler || action.onClick
      
      if (handler) {
        const result = handler()
        
        // If handler returns a promise, set loading state
        if (result && typeof result.then === 'function') {
          const notification = this.notifications.find(n => n.id === notificationId)
          if (notification && notification.action) {
            notification.action.loading = true
            this.$forceUpdate()
            
            result
              .then(() => {
                if (notification && notification.action) {
                  notification.action.loading = false
                }
              })
              .catch(() => {
                if (notification && notification.action) {
                  notification.action.loading = false
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
    }
  }
}
</script>

<style>
/* ============================================
   NOTIFICATION CONTAINER
   ============================================ */
.notification-container {
  position: fixed !important;
  top: var(--spacing-xl, 2rem) !important;
  bottom: unset !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
  z-index: var(--z-toast, 9999) !important;
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  gap: var(--spacing-sm, 0.5rem) !important;
  pointer-events: none !important;
  max-width: min(95vw, 900px) !important;
  width: auto !important;
  min-height: fit-content !important;
  visibility: visible !important;
  opacity: 1 !important;
  margin: 0 !important;
  padding: 0 !important;
}

.notification-container > * {
  pointer-events: auto;
}

/* ============================================
   NOTIFICATION LIST
   ============================================ */
.notification-list {
  display: flex !important;
  flex-direction: column !important;
  gap: var(--spacing-sm, 0.5rem) !important;
  width: auto !important;
  max-width: 100% !important;
  align-items: center !important;
  position: relative !important;
}

/* ============================================
   NOTIFICATION ITEM
   ============================================ */
.notification-item {
  display: flex !important;
  align-items: flex-start !important;
  gap: var(--spacing-md, 1rem) !important;
  min-height: 64px !important;
  height: auto !important;
  min-width: 360px !important; 
  max-width: min(90vw, 420px) !important; 
  width: 100% !important;

  padding: var(--spacing-md, 1rem) var(--spacing-lg, 1.5rem) !important;
  border-radius: var(--radius-xl, 1rem) !important;
  box-shadow: var(--shadow-xl) !important;
  
  backdrop-filter: blur(12px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(12px) saturate(180%) !important;
  border: var(--border-width-thin, 1px) solid var(--border-color-light, rgba(226, 232, 240, 1)) !important;
  box-shadow: none !important;
  background: var(--bg-glass, rgba(255, 255, 255, 0.95)) !important;

  transition: all var(--transition-base, 0.2s ease) !important;
  cursor: default !important;

  /* 👉 giữ nguyên các reset */
  position: static !important;
  overflow: visible !important;
  visibility: visible !important;
  opacity: 1 !important;
  z-index: var(--z-toast, 9999) !important;
  margin: 0 !important;
  box-sizing: border-box !important;
  transform: none !important;
  left: auto !important;
  right: auto !important;
  top: auto !important;
  bottom: auto !important;
}

.dark .notification-item {
  background: var(--bg-glass-dark, rgba(30, 41, 59, 0.95)) !important;
  border-color: var(--border-color-light-dark, rgba(51, 65, 85, 1)) !important;
}

.notification-item:hover {
  box-shadow: var(--shadow-2xl) !important;
  transform: translateY(-2px) !important;
}

/* Success variant */
.notification-success {
  border-left: 4px solid var(--color-success, #10b981) !important;
}

.notification-success .notification-icon-wrapper {
  background: var(--color-success-light, #d1fae5) !important;
}

.dark .notification-success .notification-icon-wrapper {
  background: rgba(16, 185, 129, 0.2) !important;
}

/* Error variant */
.notification-error {
  border-left: 4px solid var(--color-error, #ef4444) !important;
}

.notification-error .notification-icon-wrapper {
  background: var(--color-error-light, #fee2e2) !important;
}

.dark .notification-error .notification-icon-wrapper {
  background: rgba(239, 68, 68, 0.2) !important;
}

/* Warning variant */
.notification-warning {
  border-left: 4px solid var(--color-warning, #f59e0b) !important;
}

.notification-warning .notification-icon-wrapper {
  background: var(--color-warning-light, #fef3c7) !important;
}

.dark .notification-warning .notification-icon-wrapper {
  background: rgba(245, 158, 11, 0.2) !important;
}

/* Info variant */
.notification-info {
  border-left: 4px solid var(--color-info, #3b82f6) !important;
}

.notification-info .notification-icon-wrapper {
  background: var(--color-info-light, #dbeafe) !important;
}

.dark .notification-info .notification-icon-wrapper {
  background: rgba(59, 130, 246, 0.2) !important;
}

/* ============================================
   ICON WRAPPER & ICON
   ============================================ */
.notification-icon-wrapper {
  flex-shrink: 0;
  width: 2.5rem;
  height: 2.5rem;
  border-radius: var(--radius-lg, 0.75rem);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-base, 0.2s ease);
  margin-top: 0.125rem; /* Slight offset để align với text đầu dòng */
}

.notification-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
}

.notification-success .notification-icon {
  color: var(--color-success, #10b981);
}

.notification-error .notification-icon {
  color: var(--color-error, #ef4444);
}

.notification-warning .notification-icon {
  color: var(--color-warning, #f59e0b);
}

.notification-info .notification-icon {
  color: var(--color-info, #3b82f6);
}

/* ============================================
   CONTENT
   ============================================ */
.notification-content {
  flex: 1 1 auto !important;
  display: flex !important;
  align-items: flex-start !important;
  gap: var(--spacing-md, 1rem) !important;
  min-width: 0 !important;
  max-width: none !important;
  overflow: visible !important;
  width: auto !important;
  flex-shrink: 1 !important;
  flex-grow: 1 !important;
}

.notification-text {
  flex: 1 1 auto !important;
  min-width: 0 !important;
  max-width: none !important;
  overflow: visible !important;
  word-wrap: break-word !important;
  overflow-wrap: break-word !important;
  width: auto !important;
  flex-shrink: 1 !important;
  flex-grow: 1 !important;
}

.notification-title {
  font-weight: var(--font-semibold, 600) !important;
  font-size: var(--text-base, 1rem) !important;
  color: var(--text-primary, #1e293b) !important;
  margin-bottom: var(--spacing-xs, 0.25rem) !important;
  line-height: var(--leading-tight, 1.25) !important;
  word-wrap: break-word !important;
  overflow-wrap: break-word !important;
  word-break: break-word !important;
  hyphens: auto !important;
  -webkit-hyphens: auto !important;
  -ms-hyphens: auto !important;
  white-space: normal !important;
  overflow: visible !important;
  text-overflow: clip !important;
  max-width: none !important;
  width: auto !important;
  display: block !important;
}

.dark .notification-title {
  color: var(--text-primary-dark, #f1f5f9);
}

.notification-message {
  font-size: var(--text-sm, 0.875rem) !important;
  color: var(--text-secondary, #64748b) !important;
  line-height: var(--leading-relaxed, 1.625) !important;
  word-wrap: break-word !important;
  overflow-wrap: break-word !important;
  word-break: break-word !important;
  hyphens: auto !important;
  -webkit-hyphens: auto !important;
  -ms-hyphens: auto !important;
  white-space: normal !important;
  overflow: visible !important;
  text-overflow: clip !important;
  max-width: none !important;
  width: auto !important;
  display: block !important;
}

.dark .notification-message {
  color: var(--text-secondary-dark, #94a3b8);
}

/* ============================================
   ACTION BUTTON
   ============================================ */
.notification-action-btn {
  flex-shrink: 0;
  padding: var(--spacing-xs, 0.25rem) var(--spacing-sm, 0.5rem);
  margin-left: auto;
  font-size: var(--text-sm, 0.875rem);
  font-weight: var(--font-semibold, 600);
  color: var(--color-purple-600, #9333ea);
  background: transparent;
  border: none;
  border-radius: var(--radius-md, 0.5rem);
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease-in-out);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs, 0.25rem);
  white-space: nowrap;
  text-transform: uppercase;
  letter-spacing: var(--tracking-wide, 0.025em);
}

.dark .notification-action-btn {
  color: var(--color-purple-400, #c084fc);
}

.notification-action-btn:hover:not(:disabled) {
  background: var(--bg-purple-glass, rgba(167, 139, 250, 0.15));
  color: var(--color-purple-700, #7e22ce);
}

.dark .notification-action-btn:hover:not(:disabled) {
  background: var(--bg-purple-glass-dark, rgba(139, 92, 246, 0.2));
  color: var(--color-purple-300, #d8b4fe);
}

.notification-action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.notification-action-btn:focus-visible {
  outline: 2px solid var(--color-purple-400, #c084fc);
  outline-offset: 2px;
}

/* ============================================
   CLOSE BUTTON
   ============================================ */
.notification-close {
  flex-shrink: 0;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted, #94a3b8);
  background: transparent;
  border: none;
  border-radius: var(--radius-sm, 0.25rem);
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease-in-out);
  margin-top: 0.125rem; /* Slight offset để align với icon và text đầu dòng */
}

.notification-close:hover {
  color: var(--text-primary, #1e293b);
  background: rgba(0, 0, 0, 0.05);
}

.dark .notification-close {
  color: var(--text-muted-dark, #64748b);
}

.dark .notification-close:hover {
  color: var(--text-primary-dark, #f1f5f9);
  background: rgba(255, 255, 255, 0.1);
}

.notification-close:focus-visible {
  outline: 2px solid var(--color-purple-400, #c084fc);
  outline-offset: 2px;
}

.notification-close .material-icons {
  font-size: 1.25rem;
}

/* ============================================
   ACTION SPINNER
   ============================================ */
.notification-action-spinner {
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

/* ============================================
   TRANSITIONS & ANIMATIONS
   ============================================ */
.notification-enter-active {
  transition: all 0.3s var(--ease-out, cubic-bezier(0, 0, 0.2, 1));
}

.notification-leave-active {
  transition: all 0.25s var(--ease-in, cubic-bezier(0.4, 0, 1, 1));
}

.notification-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.notification-enter-to {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.notification-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.notification-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.notification-move {
  transition: transform 0.3s var(--ease-out, cubic-bezier(0, 0, 0.2, 1));
}

/* ============================================
   MOBILE RESPONSIVE
   ============================================ */
@media (max-width: 640px) {
  .notification-container {
    bottom: var(--spacing-sm, 0.5rem);
    left: var(--spacing-sm, 0.5rem);
    right: var(--spacing-sm, 0.5rem);
    transform: none;
    max-width: calc(100vw - 1rem);
    width: calc(100vw - 1rem);
  }

  .notification-list {
    width: 100%;
  }

  .notification-item {
    min-width: auto;
    max-width: 100%;
    width: 100%;
    padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
    border-left-width: 3px;
  }

  .notification-icon-wrapper {
    width: 2rem;
    height: 2rem;
  }

  .notification-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-xs, 0.25rem);
  }

  .notification-action-btn {
    margin-left: 0;
    margin-top: var(--spacing-xs, 0.25rem);
  }
}

/* ============================================
   REDUCED MOTION
   ============================================ */
@media (prefers-reduced-motion: reduce) {
  .notification-enter-active,
  .notification-leave-active,
  .notification-move,
  .notification-item {
    transition: none;
  }

  .notification-enter-from,
  .notification-leave-to {
    transform: translateY(0);
  }

  .notification-item:hover {
    transform: none;
  }
}

</style>

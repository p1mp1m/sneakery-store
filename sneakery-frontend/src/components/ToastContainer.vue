<template>
  <div class="toast-container">
    <TransitionGroup name="toast" tag="div">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        :class="[
          'toast-notification',
          `toast-${toast.type}`,
          { 'toast-closable': toast.closable }
        ]"
        @click="toast.closable ? removeToast(toast.id) : null"
      >
        <div class="toast-icon">
          <span class="material-icons">{{ getIcon(toast.type) }}</span>
        </div>
        
        <div class="toast-content">
          <div v-if="toast.title" class="toast-title">{{ toast.title }}</div>
          <div v-if="toast.message" class="toast-message">{{ toast.message }}</div>
        </div>

        <button
          v-if="toast.closable"
          class="toast-close"
          @click.stop="removeToast(toast.id)"
          aria-label="Đóng thông báo"
        >
          <span class="material-icons">close</span>
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
.toast-container {
  position: fixed;
  top: var(--space-6);
  right: var(--space-6);
  z-index: 9999;
  max-width: 400px;
  pointer-events: none;
}

.toast-notification {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
  padding: var(--space-4);
  margin-bottom: var(--space-3);
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  backdrop-filter: blur(10px);
  pointer-events: auto;
  cursor: pointer;
  transition: var(--transition-fast);
  position: relative;
  overflow: hidden;
}

.toast-notification::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--color-info);
}

.toast-notification:hover {
  transform: translateX(-2px);
  box-shadow: var(--shadow-xl);
}

.toast-success::before {
  background: var(--color-success);
}

.toast-error::before {
  background: var(--color-error);
}

.toast-warning::before {
  background: var(--color-warning);
}

.toast-info::before {
  background: var(--color-info);
}

.toast-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}

.toast-icon .material-icons {
  font-size: 20px;
  color: var(--text-primary);
}

.toast-success .toast-icon .material-icons {
  color: var(--color-success);
}

.toast-error .toast-icon .material-icons {
  color: var(--color-error);
}

.toast-warning .toast-icon .material-icons {
  color: var(--color-warning);
}

.toast-info .toast-icon .material-icons {
  color: var(--color-info);
}

.toast-content {
  flex: 1;
  min-width: 0;
}

.toast-title {
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
  line-height: 1.4;
}

.toast-message {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  line-height: 1.4;
}

.toast-close {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-tertiary);
}

.toast-close:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.toast-close .material-icons {
  font-size: 16px;
}

/* Animations */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.toast-move {
  transition: transform 0.3s ease;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .toast-container {
    top: var(--space-4);
    right: var(--space-4);
    left: var(--space-4);
    max-width: none;
  }
  
  .toast-notification {
    padding: var(--space-3);
  }
  
  .toast-title {
    font-size: var(--text-xs);
  }
  
  .toast-message {
    font-size: var(--text-xs);
  }
}
</style>

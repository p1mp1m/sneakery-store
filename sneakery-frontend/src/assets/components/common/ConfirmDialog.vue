<template>
  <div v-if="modelValue" class="modal-overlay" @click="handleCancel">
    <div class="confirm-dialog" :class="`confirm-dialog-${type}`" @click.stop>
      <!-- Icon -->
      <div class="dialog-icon" :class="`icon-${type}`">
        <i class="material-icons">{{ iconName }}</i>
      </div>

      <!-- Content -->
      <div class="dialog-content">
        <h3 class="dialog-title">{{ title }}</h3>
        <p class="dialog-message">{{ message }}</p>
        <p v-if="description" class="dialog-description">{{ description }}</p>
      </div>

      <!-- Actions -->
      <div class="dialog-actions">
        <button 
          @click="handleCancel" 
          class="btn btn-secondary"
          :disabled="loading"
        >
          {{ cancelText }}
        </button>
        <button 
          @click="handleConfirm" 
          class="btn"
          :class="confirmButtonClass"
          :disabled="loading"
        >
          <span v-if="loading" class="btn-loading"></span>
          <span v-else>{{ confirmText }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    default: 'warning', // 'warning', 'danger', 'info', 'success'
    validator: (value) => ['warning', 'danger', 'info', 'success'].includes(value)
  },
  title: {
    type: String,
    required: true
  },
  message: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  confirmText: {
    type: String,
    default: 'Xác nhận'
  },
  cancelText: {
    type: String,
    default: 'Hủy'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const iconName = computed(() => {
  const icons = {
    warning: 'warning',
    danger: 'error',
    info: 'info',
    success: 'check_circle'
  }
  return icons[props.type]
})

const confirmButtonClass = computed(() => {
  const classes = {
    warning: 'btn-primary',
    danger: 'btn-danger',
    info: 'btn-primary',
    success: 'btn-success'
  }
  return classes[props.type]
})

const handleConfirm = () => {
  if (!props.loading) {
    emit('confirm')
  }
}

const handleCancel = () => {
  if (!props.loading) {
    emit('update:modelValue', false)
    emit('cancel')
  }
}
</script>

<style scoped>
/* ===== MODAL OVERLAY ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease-out;
}

/* ===== CONFIRM DIALOG ===== */
.confirm-dialog {
  background: var(--white);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  max-width: 450px;
  width: 90%;
  box-shadow: var(--shadow-2xl);
  animation: slideUp 0.3s ease-out;
  text-align: center;
}

/* ===== DIALOG ICON ===== */
.dialog-icon {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--space-6);
  font-size: 40px;
  color: var(--white);
}

.icon-warning {
  background: linear-gradient(135deg, #f59e0b, #f97316);
}

.icon-danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.icon-info {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.icon-success {
  background: linear-gradient(135deg, #10b981, #059669);
}

/* ===== DIALOG CONTENT ===== */
.dialog-content {
  margin-bottom: var(--space-8);
}

.dialog-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4) 0;
}

.dialog-message {
  font-size: var(--text-base);
  color: var(--text-secondary);
  margin: 0;
  line-height: var(--leading-relaxed);
}

.dialog-description {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin: var(--space-3) 0 0 0;
  font-style: italic;
}

/* ===== DIALOG ACTIONS ===== */
.dialog-actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
}

.dialog-actions .btn {
  min-width: 120px;
  height: 48px;
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
}

/* ===== ANIMATIONS ===== */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== RESPONSIVE ===== */
@media (max-width: 640px) {
  .confirm-dialog {
    max-width: 90%;
    padding: var(--space-6);
  }

  .dialog-icon {
    width: 60px;
    height: 60px;
    font-size: 30px;
  }

  .dialog-title {
    font-size: var(--text-xl);
  }

  .dialog-actions {
    flex-direction: column;
  }

  .dialog-actions .btn {
    width: 100%;
  }
}
</style>


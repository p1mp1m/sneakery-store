<template>
  <div v-if="modelValue" class="modal-overlay" @click="handleCancel" @keydown.esc="handleCancel">
    <div ref="dialogRef" class="confirm-dialog" :class="`confirm-dialog-${type}`" @click.stop role="dialog" :aria-modal="true" :aria-labelledby="titleId">
      <!-- Icon -->
      <div class="dialog-icon" :class="`icon-${type}`">
        <i class="material-icons">{{ iconName }}</i>
      </div>

      <!-- Content -->
      <div class="dialog-content">
        <h3 :id="titleId" class="dialog-title">{{ title }}</h3>
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
import { computed, ref, watch, onMounted, onUnmounted, nextTick } from "vue";
import { useFocusManagement } from "@/composables/useFocusManagement";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  type: {
    type: String,
    default: "warning", // 'warning', 'danger', 'info', 'success'
    validator: (value) =>
      ["warning", "danger", "info", "success"].includes(value),
  },
  title: {
    type: String,
    required: true,
  },
  message: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    default: "",
  },
  confirmText: {
    type: String,
    default: "Xác nhận",
  },
  cancelText: {
    type: String,
    default: "Hủy",
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:modelValue", "confirm", "cancel"]);

// Focus management
const dialogRef = ref(null);
const { setupModalFocus, cleanupModalFocus, saveActiveElement } = useFocusManagement();
const titleId = computed(() => `confirm-dialog-title-${Math.random().toString(36).substr(2, 9)}`);

// Setup focus when modal opens
watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    saveActiveElement();
    nextTick(() => {
      if (dialogRef.value) {
        setupModalFocus(dialogRef.value);
      }
    });
  } else {
    if (dialogRef.value) {
      cleanupModalFocus(dialogRef.value);
    }
  }
});

onUnmounted(() => {
  if (dialogRef.value) {
    cleanupModalFocus(dialogRef.value);
  }
});

const iconName = computed(() => {
  const icons = {
    warning: "warning",
    danger: "error",
    info: "info",
    success: "check_circle",
  };
  return icons[props.type];
});

const confirmButtonClass = computed(() => {
  const classes = {
    warning: "btn-primary",
    danger: "btn-danger",
    info: "btn-primary",
    success: "btn-success",
  };
  return classes[props.type];
});

const handleConfirm = () => {
  if (!props.loading) {
    emit("confirm");
  }
};

const handleCancel = () => {
  if (!props.loading) {
    emit("update:modelValue", false);
    emit("cancel");
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.confirm-dialog {
  background: white;
  border-radius: 1rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  max-width: 28rem;
  width: 100%;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.dark .confirm-dialog {
  background: #1f2937;
  color: #f9fafb;
}

.dialog-icon {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.dialog-icon i {
  font-size: 1.5rem;
  color: white;
}

.icon-warning {
  background: #f59e0b;
}

.icon-danger {
  background: #ef4444;
}

.icon-info {
  background: #3b82f6;
}

.icon-success {
  background: #10b981;
}

.dialog-content {
  text-align: center;
}

.dialog-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #111827;
}

.dark .dialog-title {
  color: #f9fafb;
}

.dialog-message {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 0.5rem;
}

.dark .dialog-message {
  color: #d1d5db;
}

.dialog-description {
  font-size: 0.875rem;
  color: #9ca3af;
}

.dark .dialog-description {
  color: #9ca3af;
}

.dialog-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
  margin-top: 0.5rem;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
}

.dark .btn-secondary {
  background: #374151;
  color: #f9fafb;
}

.btn-secondary:hover:not(:disabled) {
  background: #e5e7eb;
}

.dark .btn-secondary:hover:not(:disabled) {
  background: #4b5563;
}

.btn-primary {
  background: #6366f1;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #4f46e5;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: #dc2626;
}

.btn-success {
  background: #10b981;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background: #059669;
}

.btn-loading {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid currentColor;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>


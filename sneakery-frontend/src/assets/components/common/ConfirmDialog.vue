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
import { computed } from "vue";

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
/* ===== MODAL OVERLAY ===== */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 15, 25, 0.45); /* mềm và sáng hơn */
  backdrop-filter: blur(8px) saturate(160%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.25s ease-out;
}

/* ===== CONFIRM DIALOG ===== */
.confirm-dialog {
  background: var(--surface, #fff);
  border-radius: 18px;
  padding: 2rem;
  max-width: 440px;
  width: 92%;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
  text-align: center;
  transform: translateY(20px) scale(0.98);
  animation: dialogPop 0.28s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  transition: all 0.3s ease;
}

/* ===== DIALOG ICON ===== */
.dialog-icon {
  width: 84px;
  height: 84px;
  border-radius: 50%;
  margin: 0 auto 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 0 15px rgba(255, 80, 80, 0.4);
  transform: scale(1);
  animation: iconPulse 1.2s ease-in-out infinite alternate;
}

.icon-danger {
  background: linear-gradient(145deg, #ff4444, #d92626);
}

.icon-warning {
  background: linear-gradient(145deg, #f59e0b, #f97316);
}

.icon-info {
  background: linear-gradient(145deg, #3b82f6, #2563eb);
}

.icon-success {
  background: linear-gradient(145deg, #10b981, #059669);
}

/* ===== CONTENT ===== */
.dialog-content {
  margin-bottom: 1.75rem;
}

.dialog-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #222;
  margin-bottom: 0.5rem;
}

.dialog-message {
  font-size: 1rem;
  color: #555;
  line-height: 1.5;
}

.dialog-description {
  font-size: 0.9rem;
  color: #888;
  font-style: italic;
  margin-top: 0.5rem;
}

/* ===== BUTTONS ===== */
.dialog-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.dialog-actions .btn {
  min-width: 120px;
  height: 46px;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.25s ease;
}

.btn-danger {
  background: linear-gradient(135deg, #ff4d4d, #d92626);
  color: #fff;
  box-shadow: 0 4px 10px rgba(255, 77, 77, 0.4);
}

.btn-danger:hover {
  background: linear-gradient(135deg, #ff5f5f, #e13a3a);
  box-shadow: 0 0 14px rgba(255, 90, 90, 0.55);
  transform: translateY(-2px);
}

.btn-secondary {
  background: #f4f4f4;
  color: #333;
}

.btn-secondary:hover {
  background: #e9e9e9;
  color: #000;
  transform: translateY(-2px);
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

@keyframes dialogPop {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes iconPulse {
  0% {
    transform: scale(1);
    box-shadow: 0 0 12px rgba(255, 77, 77, 0.4);
  }
  100% {
    transform: scale(1.05);
    box-shadow: 0 0 20px rgba(255, 77, 77, 0.6);
  }
}

/* ===== RESPONSIVE ===== */
@media (max-width: 640px) {
  .confirm-dialog {
    max-width: 94%;
    padding: 1.5rem;
  }

  .dialog-icon {
    width: 68px;
    height: 68px;
    font-size: 30px;
  }

  .dialog-actions {
    flex-direction: column;
  }

  .dialog-actions .btn {
    width: 100%;
  }
}
</style>

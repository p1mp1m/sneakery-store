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




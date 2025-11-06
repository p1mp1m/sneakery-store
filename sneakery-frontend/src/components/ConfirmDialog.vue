<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition-opacity duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-200"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="visible"
        class="fixed inset-0 z-[10001] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm"
        @click="handleBackdropClick"
      >
        <Transition
          enter-active-class="transition-all duration-200"
          enter-from-class="opacity-0 scale-95"
          enter-to-class="opacity-100 scale-100"
          leave-active-class="transition-all duration-200"
          leave-from-class="opacity-100 scale-100"
          leave-to-class="opacity-0 scale-95"
        >
          <div
            v-if="visible"
            class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-md w-full border border-gray-200 dark:border-gray-700"
            @click.stop
          >
            <!-- Header -->
            <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
              <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                <i class="material-icons text-xl" :class="getIconClass">{{ getIcon }}</i>
                {{ title }}
              </h3>
              <button
                v-if="showCloseButton"
                @click="handleCancel"
                class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
                type="button"
              >
                <i class="material-icons text-base">close</i>
              </button>
            </div>

            <!-- Content -->
            <div class="p-4">
              <p class="text-gray-700 dark:text-gray-300 whitespace-pre-line">{{ message }}</p>
            </div>

            <!-- Footer -->
            <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
              <button
                @click="handleCancel"
                class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium"
                type="button"
              >
                {{ cancelButtonText }}
              </button>
              <button
                @click="handleConfirm"
                class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium"
                :class="getConfirmButtonClass"
                type="button"
              >
                {{ confirmButtonText }}
              </button>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'Xác nhận'
  },
  message: {
    type: String,
    default: ''
  },
  confirmButtonText: {
    type: String,
    default: 'Đồng ý'
  },
  cancelButtonText: {
    type: String,
    default: 'Hủy'
  },
  type: {
    type: String,
    default: 'warning', // warning, danger, info
    validator: (value) => ['warning', 'danger', 'info'].includes(value)
  },
  closeOnClickModal: {
    type: Boolean,
    default: true
  },
  showCloseButton: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['confirm', 'cancel', 'update:visible'])

const handleConfirm = () => {
  emit('confirm')
  emit('update:visible', false)
}

const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}

const handleBackdropClick = () => {
  if (props.closeOnClickModal) {
    handleCancel()
  }
}

const getIcon = computed(() => {
  const icons = {
    warning: 'warning',
    danger: 'error',
    info: 'info'
  }
  return icons[props.type] || 'info'
})

const getIconClass = computed(() => {
  const classes = {
    warning: 'text-yellow-500',
    danger: 'text-red-500',
    info: 'text-blue-500'
  }
  return classes[props.type] || 'text-blue-500'
})

const getConfirmButtonClass = computed(() => {
  if (props.type === 'danger') {
    return 'from-red-500 to-red-600 hover:from-red-600 hover:to-red-700'
  }
  return ''
})
</script>


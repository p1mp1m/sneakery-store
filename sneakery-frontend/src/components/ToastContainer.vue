<template>
  <div class="fixed top-20 right-4 z-[9999] flex flex-col gap-2 max-w-sm">
    <TransitionGroup 
      name="toast"
      tag="div"
      enter-active-class="transition-all duration-300 ease-out"
      leave-active-class="transition-all duration-300 ease-in"
      enter-from-class="opacity-0 translate-x-20"
      enter-to-class="opacity-100 translate-x-0"
      leave-from-class="opacity-100 translate-x-0"
      leave-to-class="opacity-0 -translate-x-20"
    >
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="flex items-start gap-3 p-3 rounded-xl shadow-lg backdrop-blur-sm border-l-4 min-w-[280px] cursor-pointer transition-all"
        :class="{
          'bg-white dark:bg-gray-800 border-green-500': toast.type === 'success',
          'bg-white dark:bg-gray-800 border-red-500': toast.type === 'error',
          'bg-white dark:bg-gray-800 border-yellow-500': toast.type === 'warning',
          'bg-white dark:bg-gray-800 border-blue-500': toast.type === 'info'
        }"
        @click="toast.closable ? removeToast(toast.id) : null"
      >
        <i 
          class="material-icons text-xl flex-shrink-0"
          :class="{
            'text-green-500': toast.type === 'success',
            'text-red-500': toast.type === 'error',
            'text-yellow-500': toast.type === 'warning',
            'text-blue-500': toast.type === 'info'
          }"
        >
          {{ getIcon(toast.type) }}
        </i>
        
        <div class="flex-1 min-w-0">
          <div v-if="toast.title" class="font-semibold text-sm mb-1 text-gray-900 dark:text-gray-100">{{ toast.title }}</div>
          <div v-if="toast.message" class="text-xs text-gray-600 dark:text-gray-400">{{ toast.message }}</div>
        </div>

        <button
          v-if="toast.closable"
          class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 flex-shrink-0 transition-colors"
          @click.stop="removeToast(toast.id)"
          aria-label="Đóng thông báo"
        >
          <i class="material-icons text-lg">close</i>
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




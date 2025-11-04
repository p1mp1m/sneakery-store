<template>
  <div 
    v-if="visible" 
    class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/50 backdrop-blur-sm"
  >
    <div class="flex flex-col items-center justify-center">
      <div class="relative w-16 h-16">
        <div class="absolute inset-0 border-4 border-purple-500 border-t-transparent rounded-full animate-spin"></div>
        <div class="absolute inset-2 border-4 border-blue-500 border-t-transparent rounded-full animate-spin animate-reverse" style="animation-duration: 0.8s;"></div>
        <div class="absolute inset-4 border-4 border-green-500 border-t-transparent rounded-full animate-spin" style="animation-duration: 1.2s;"></div>
      </div>
      <div v-if="message" class="mt-4 text-sm text-white font-medium">{{ message }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  message: {
    type: String,
    default: 'Đang tải...'
  }
})
</script>




<template>
  <div 
    class="bg-gray-200 dark:bg-gray-700 rounded"
    :class="{
      'h-4': type === 'text',
      'h-6': type === 'title',
      'w-12 h-12': type === 'avatar' && !width && !height,
      'w-full h-48': type === 'card' && !width && !height,
      'h-10 w-24': type === 'button' && !width && !height,
      'w-full h-64': type === 'image' && !width && !height,
      'animate-pulse': animate
    }"
    :style="customStyle"
  >
    <div 
      v-if="animate"
      class="h-full bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 dark:from-gray-700 dark:via-gray-600 dark:to-gray-700 rounded animate-shimmer"
      style="background-size: 200% 100%; animation: shimmer 2s infinite;"
    ></div>
  </div>
</template>

<style scoped>
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}
</style>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'text', // text, title, avatar, card, button
    validator: (value) => ['text', 'title', 'avatar', 'card', 'button', 'image'].includes(value)
  },
  width: {
    type: String,
    default: null
  },
  height: {
    type: String,
    default: null
  },
  animate: {
    type: Boolean,
    default: true
  },
  circle: {
    type: Boolean,
    default: false
  }
})

const customStyle = computed(() => {
  const style = {}
  if (props.width) style.width = props.width
  if (props.height) style.height = props.height
  if (props.circle) style.borderRadius = '50%'
  return style
})
</script>





<template>
  <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
    <div class="flex items-center justify-between mb-3">
      <div 
        class="w-10 h-10 rounded-lg flex items-center justify-center"
        :class="{
          'bg-gradient-to-br from-purple-500 to-purple-600': variant === 'primary',
          'bg-gradient-to-br from-green-500 to-green-600': variant === 'success',
          'bg-gradient-to-br from-yellow-500 to-yellow-600': variant === 'warning',
          'bg-gradient-to-br from-red-500 to-red-600': variant === 'danger',
          'bg-gradient-to-br from-blue-500 to-blue-600': variant === 'info'
        }"
      >
        <i class="material-icons text-white text-lg">{{ icon }}</i>
      </div>
    </div>
    <div>
      <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ value }}</h3>
      <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">{{ label }}</p>
      <div 
        v-if="change !== undefined" 
        class="flex items-center gap-1 text-xs"
        :class="change >= 0 ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'"
      >
        <i class="material-icons text-sm">{{ changeIcon }}</i>
        <span>{{ Math.abs(change) }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  icon: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  label: {
    type: String,
    required: true
  },
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'success', 'warning', 'danger', 'info'].includes(value)
  },
  change: {
    type: Number,
    default: undefined
  }
})

const gradientStyle = computed(() => {
  const gradients = {
    primary: 'var(--gradient-primary)',
    success: 'var(--gradient-success)',
    warning: 'var(--gradient-warning)',
    danger: 'var(--gradient-danger)',
    info: 'var(--gradient-info)'
  }
  return gradients[props.variant]
})

const changeClass = computed(() => {
  return props.change >= 0 ? 'positive' : 'negative'
})

const changeIcon = computed(() => {
  return props.change >= 0 ? 'trending_up' : 'trending_down'
})
</script>





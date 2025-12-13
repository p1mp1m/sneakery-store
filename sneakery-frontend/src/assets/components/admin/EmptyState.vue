<template>
  <div class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
    <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
      <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">{{ icon }}</i>
    </div>
    <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">{{ title }}</h3>
    <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">{{ description }}</p>
    <slot name="action">
      <button 
        v-if="actionText" 
        @click="$emit('action')" 
        class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
      >
        <i v-if="actionIcon" class="material-icons text-base">{{ actionIcon }}</i>
        {{ actionText }}
      </button>
    </slot>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    default: 'inbox'
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  actionText: {
    type: String,
    default: ''
  },
  actionIcon: {
    type: String,
    default: 'add'
  }
})

defineEmits(['action'])
</script>





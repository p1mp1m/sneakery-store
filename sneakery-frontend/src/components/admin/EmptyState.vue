<template>
  <div class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
    <!-- Icon/Illustration -->
    <div
      class="w-20 h-20 rounded-full flex items-center justify-center mb-4"
      :class="iconBgClass"
    >
      <i v-if="icon" class="material-icons text-4xl" :class="iconClass">{{ icon }}</i>
      <svg
        v-else-if="illustration"
        class="w-16 h-16"
        viewBox="0 0 24 24"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          :d="illustration"
          :stroke="iconColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
    </div>

    <!-- Title -->
    <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2 text-center">
      {{ title }}
    </h3>

    <!-- Description -->
    <p class="text-sm text-gray-500 dark:text-gray-400 mb-6 text-center max-w-md">
      {{ description }}
    </p>

    <!-- Tips (optional) -->
    <div v-if="tips && tips.length > 0" class="mb-6 w-full max-w-md">
      <div class="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-4">
        <p class="text-xs font-semibold text-gray-700 dark:text-gray-300 mb-2 flex items-center gap-1">
          <i class="material-icons text-sm">lightbulb</i>
          Mẹo:
        </p>
        <ul class="text-xs text-gray-600 dark:text-gray-400 space-y-1">
          <li v-for="(tip, index) in tips" :key="index" class="flex items-start gap-2">
            <span class="text-purple-600 dark:text-purple-400 mt-0.5">•</span>
            <span>{{ tip }}</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- Action Buttons -->
    <div v-if="actions && actions.length > 0" class="flex flex-wrap items-center justify-center gap-2">
      <button
        v-for="(action, index) in actions"
        :key="index"
        @click="action.handler"
        :class="[
          'px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200',
          action.primary
            ? 'bg-gradient-to-r from-purple-500 to-purple-600 text-white hover:from-purple-600 hover:to-purple-700 shadow-sm'
            : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
        ]"
      >
        <i v-if="action.icon" class="material-icons text-base align-middle mr-1">{{ action.icon }}</i>
        {{ action.label }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  icon: {
    type: String,
    default: null
  },
  illustration: {
    type: String,
    default: null
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  tips: {
    type: Array,
    default: () => []
  },
  actions: {
    type: Array,
    default: () => []
  },
  variant: {
    type: String,
    default: 'default', // 'default', 'success', 'warning', 'info'
    validator: (value) => ['default', 'success', 'warning', 'info'].includes(value)
  }
});

const iconBgClass = computed(() => {
  const variants = {
    default: 'bg-purple-100 dark:bg-purple-900/30',
    success: 'bg-green-100 dark:bg-green-900/30',
    warning: 'bg-yellow-100 dark:bg-yellow-900/30',
    info: 'bg-blue-100 dark:bg-blue-900/30'
  };
  return variants[props.variant] || variants.default;
});

const iconClass = computed(() => {
  const variants = {
    default: 'text-purple-600 dark:text-purple-400',
    success: 'text-green-600 dark:text-green-400',
    warning: 'text-yellow-600 dark:text-yellow-400',
    info: 'text-blue-600 dark:text-blue-400'
  };
  return variants[props.variant] || variants.default;
});

const iconColor = computed(() => {
  const variants = {
    default: 'rgb(147, 51, 234)',
    success: 'rgb(34, 197, 94)',
    warning: 'rgb(234, 179, 8)',
    info: 'rgb(59, 130, 246)'
  };
  return variants[props.variant] || variants.default;
});
</script>


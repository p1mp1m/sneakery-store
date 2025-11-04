<template>
  <div :class="['error-state', containerClass]" :style="containerStyle">
    <div class="error-state-content">
      <!-- Icon -->
      <div :class="['error-icon', iconClass]">
        <i v-if="icon" class="material-icons">{{ icon }}</i>
        <svg v-else xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
      </div>

      <!-- Title -->
      <h2 v-if="title" class="error-title">{{ title }}</h2>

      <!-- Message -->
      <p v-if="message" class="error-message">{{ message }}</p>

      <!-- Actions -->
      <div v-if="showActions" class="error-actions">
        <button
          v-if="showRetry"
          @click="$emit('retry')"
          @keydown.enter="$emit('retry')"
          @keydown.space.prevent="$emit('retry')"
          class="error-btn-primary"
          :aria-label="retryLabel || 'Thử lại'"
        >
          <i class="material-icons" aria-hidden="true">refresh</i>
          <span>{{ retryLabel || 'Thử lại' }}</span>
        </button>
        <button
          v-if="showGoBack"
          @click="$emit('go-back')"
          @keydown.enter="$emit('go-back')"
          @keydown.space.prevent="$emit('go-back')"
          class="error-btn-secondary"
          :aria-label="goBackLabel || 'Quay lại'"
        >
          <i class="material-icons" aria-hidden="true">arrow_back</i>
          <span>{{ goBackLabel || 'Quay lại' }}</span>
        </button>
        <slot name="actions"></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    default: 'Đã xảy ra lỗi'
  },
  message: {
    type: String,
    default: 'Vui lòng thử lại sau'
  },
  icon: {
    type: String,
    default: ''
  },
  iconClass: {
    type: String,
    default: ''
  },
  showRetry: {
    type: Boolean,
    default: true
  },
  showGoBack: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: true
  },
  retryLabel: {
    type: String,
    default: 'Thử lại'
  },
  goBackLabel: {
    type: String,
    default: 'Quay lại'
  },
  containerClass: {
    type: String,
    default: ''
  },
  containerStyle: {
    type: Object,
    default: () => ({})
  }
})

defineEmits(['retry', 'go-back'])
</script>

<style scoped>
.error-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: var(--spacing-4xl) var(--spacing-xl);
}

.error-state-content {
  text-align: center;
  max-width: 500px;
}

.error-icon {
  width: 6rem;
  height: 6rem;
  margin: 0 auto var(--spacing-xl);
  border-radius: var(--radius-full);
  background: var(--color-error-light);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-error);
  font-size: 3rem;
}

.dark .error-icon {
  background: rgba(239, 68, 68, 0.2);
  color: var(--color-error);
}

.error-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
}

.dark .error-title {
  color: var(--text-primary-dark);
}

.error-message {
  font-size: var(--text-base);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xl);
  line-height: var(--leading-relaxed);
}

.dark .error-message {
  color: var(--text-secondary-dark);
}

.error-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
  flex-wrap: wrap;
}

.error-btn-primary {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-xl);
  background: var(--gradient-purple);
  color: white;
  border: none;
  border-radius: var(--radius-xl);
  font-weight: var(--font-semibold);
  font-size: var(--text-base);
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-purple);
}

.error-btn-primary:hover {
  box-shadow: var(--shadow-purple-lg);
  transform: scale(1.02);
  filter: brightness(1.05);
}

.error-btn-primary:active {
  transform: scale(0.98);
}

.error-btn-primary:focus-visible {
  outline: 3px solid var(--color-purple-400);
  outline-offset: 2px;
}

.error-btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-xl);
  background: var(--bg-primary);
  color: var(--text-primary);
  border: var(--border-width-thin) solid var(--border-color-medium);
  border-radius: var(--radius-xl);
  font-weight: var(--font-semibold);
  font-size: var(--text-base);
  cursor: pointer;
  transition: all var(--transition-base);
}

.dark .error-btn-secondary {
  background: var(--bg-primary-dark);
  color: var(--text-primary-dark);
  border-color: var(--border-color-medium-dark);
}

.error-btn-secondary:hover {
  background: var(--bg-secondary);
  border-color: var(--color-purple-300);
}

.dark .error-btn-secondary:hover {
  background: var(--bg-secondary-dark);
  border-color: var(--color-purple-600);
}

.error-btn-secondary:focus-visible {
  outline: 3px solid var(--color-purple-400);
  outline-offset: 2px;
}
</style>


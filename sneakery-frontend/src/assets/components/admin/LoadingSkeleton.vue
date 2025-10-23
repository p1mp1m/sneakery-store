<template>
  <div class="loading-skeleton" :class="[type, { 'animate': animate }]" :style="customStyle">
    <div class="skeleton-shimmer"></div>
  </div>
</template>

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

<style scoped>
.loading-skeleton {
  background: var(--dark-bg-tertiary, rgba(30, 41, 59, 0.4));
  border-radius: var(--radius-md);
  position: relative;
  overflow: hidden;
}

.loading-skeleton.animate .skeleton-shimmer {
  animation: skeleton-loading 1.5s ease-in-out infinite;
}

.skeleton-shimmer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(167, 139, 250, 0.1),
    transparent
  );
  transform: translateX(-100%);
}

@keyframes skeleton-loading {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

/* Type Variants */
.loading-skeleton.text {
  height: 16px;
  width: 100%;
  margin-bottom: var(--space-2);
}

.loading-skeleton.title {
  height: 24px;
  width: 60%;
  margin-bottom: var(--space-3);
}

.loading-skeleton.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
}

.loading-skeleton.card {
  height: 200px;
  width: 100%;
}

.loading-skeleton.button {
  height: 40px;
  width: 120px;
  border-radius: var(--radius-lg);
}

.loading-skeleton.image {
  height: 150px;
  width: 100%;
  border-radius: var(--radius-lg);
}
</style>


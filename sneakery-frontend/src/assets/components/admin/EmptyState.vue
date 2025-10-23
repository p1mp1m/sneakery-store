<template>
  <div class="empty-state animate-fade-up">
    <div class="empty-icon">
      <span class="material-icons">{{ icon }}</span>
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p class="empty-description">{{ description }}</p>
    <slot name="action">
      <button v-if="actionText" @click="$emit('action')" class="btn btn-primary">
        <span v-if="actionIcon" class="material-icons">{{ actionIcon }}</span>
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

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-16) var(--space-8);
  background: var(--card-bg);
  border: 1px dashed var(--border-primary);
  border-radius: var(--radius-2xl);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  text-align: center;
  min-height: 400px;
}

.empty-icon {
  width: 120px;
  height: 120px;
  border-radius: var(--radius-full);
  background: var(--gradient-purple-soft);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--space-4);
}

.empty-icon .material-icons {
  font-size: 4rem;
  color: var(--accent-primary);
  opacity: 0.6;
}

.empty-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.empty-description {
  font-size: var(--text-base);
  color: var(--text-tertiary);
  max-width: 400px;
  line-height: var(--leading-relaxed);
  margin: 0;
}

@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-up {
  animation: fade-up 0.6s ease-out;
}

/* Responsive */
@media (max-width: 768px) {
  .empty-state {
    padding: var(--space-12) var(--space-4);
    min-height: 300px;
  }
  
  .empty-icon {
    width: 80px;
    height: 80px;
  }
  
  .empty-icon .material-icons {
    font-size: 3rem;
  }
  
  .empty-title {
    font-size: var(--text-xl);
  }
  
  .empty-description {
    font-size: var(--text-sm);
  }
}
</style>


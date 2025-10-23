<template>
  <div class="stat-card" :class="variant">
    <div class="stat-icon" :style="{ background: gradientStyle }">
      <span class="material-icons">{{ icon }}</span>
    </div>
    <div class="stat-content">
      <div class="stat-value">{{ value }}</div>
      <div class="stat-label">{{ label }}</div>
      <div v-if="change !== undefined" class="stat-change" :class="changeClass">
        <span class="material-icons">{{ changeIcon }}</span>
        {{ Math.abs(change) }}%
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

<style scoped>
.stat-card {
  background: var(--card-bg);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: var(--shadow-card);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--gradient-purple-soft);
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--accent-primary);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.stat-icon .material-icons {
  font-size: 2rem;
  color: white;
}

.stat-content {
  flex: 1;
  position: relative;
  z-index: 1;
}

.stat-value {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: var(--space-2);
}

.stat-label {
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: var(--space-1);
}

.stat-change {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
}

.stat-change.positive {
  color: var(--success-text);
  background: var(--success-bg);
}

.stat-change.negative {
  color: var(--error-text);
  background: var(--error-bg);
}

.stat-change .material-icons {
  font-size: 1rem;
}

/* Responsive */
@media (max-width: 768px) {
  .stat-card {
    padding: var(--space-4);
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
  }
  
  .stat-icon .material-icons {
    font-size: 1.5rem;
  }
  
  .stat-value {
    font-size: var(--text-2xl);
  }
}
</style>


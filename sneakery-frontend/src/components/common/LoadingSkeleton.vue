<template>
  <div :class="['skeleton-container', containerClass]" :style="containerStyle">
    <!-- Card Skeleton -->
    <div v-if="type === 'card'" class="skeleton-card">
      <div v-if="showImage" class="skeleton-image"></div>
      <div class="skeleton-content">
        <div class="skeleton-line skeleton-title"></div>
        <div class="skeleton-line skeleton-text"></div>
        <div class="skeleton-line skeleton-text skeleton-short"></div>
      </div>
    </div>

    <!-- Table Row Skeleton -->
    <div v-else-if="type === 'table-row'" class="skeleton-table-row">
      <div v-for="i in columns" :key="i" class="skeleton-line" :style="{ width: getColumnWidth(i) }"></div>
    </div>

    <!-- List Item Skeleton -->
    <div v-else-if="type === 'list'" class="skeleton-list-item">
      <div class="skeleton-avatar"></div>
      <div class="skeleton-content flex-1">
        <div class="skeleton-line skeleton-title"></div>
        <div class="skeleton-line skeleton-text"></div>
      </div>
    </div>

    <!-- Custom Skeleton -->
    <div v-else class="skeleton-custom">
      <div v-for="i in lines" :key="i" 
        :class="['skeleton-line', { 'skeleton-title': i === 1, 'skeleton-short': i === lines }]"
        :style="{ width: i === lines ? '60%' : '100%' }"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'card', // 'card', 'table-row', 'list', 'custom'
    validator: (value) => ['card', 'table-row', 'list', 'custom'].includes(value)
  },
  lines: {
    type: Number,
    default: 3
  },
  columns: {
    type: Number,
    default: 4
  },
  showImage: {
    type: Boolean,
    default: true
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

const getColumnWidth = (index) => {
  // Vary column widths for more realistic appearance
  const widths = ['20%', '30%', '25%', '15%', '10%']
  return widths[index % widths.length]
}
</script>

<style scoped>
.skeleton-container {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.skeleton-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  border: var(--border-width-thin) solid var(--border-color-light);
  padding: var(--spacing-lg);
  overflow: hidden;
}

.dark .skeleton-card {
  background: var(--bg-primary-dark);
  border-color: var(--border-color-light-dark);
}

.skeleton-image {
  width: 100%;
  height: 200px;
  background: linear-gradient(
    90deg,
    var(--bg-secondary) 25%,
    var(--bg-tertiary) 50%,
    var(--bg-secondary) 75%
  );
  background-size: 200% 100%;
  border-radius: var(--radius-lg);
  margin-bottom: var(--spacing-md);
  animation: shimmer 1.5s infinite;
}

.dark .skeleton-image {
  background: linear-gradient(
    90deg,
    var(--bg-secondary-dark) 25%,
    var(--bg-tertiary-dark) 50%,
    var(--bg-secondary-dark) 75%
  );
  background-size: 200% 100%;
}

.skeleton-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.skeleton-line {
  height: 1rem;
  background: linear-gradient(
    90deg,
    var(--bg-secondary) 25%,
    var(--bg-tertiary) 50%,
    var(--bg-secondary) 75%
  );
  background-size: 200% 100%;
  border-radius: var(--radius-md);
  animation: shimmer 1.5s infinite;
}

.dark .skeleton-line {
  background: linear-gradient(
    90deg,
    var(--bg-secondary-dark) 25%,
    var(--bg-tertiary-dark) 50%,
    var(--bg-secondary-dark) 75%
  );
  background-size: 200% 100%;
}

.skeleton-title {
  height: 1.5rem;
  width: 70%;
  margin-bottom: var(--spacing-xs);
}

.skeleton-text {
  height: 0.875rem;
  width: 100%;
}

.skeleton-short {
  width: 60% !important;
}

.skeleton-table-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: var(--border-width-thin) solid var(--border-color-light);
}

.dark .skeleton-table-row {
  border-color: var(--border-color-light-dark);
}

.skeleton-list-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
}

.skeleton-avatar {
  width: 3rem;
  height: 3rem;
  border-radius: var(--radius-full);
  background: linear-gradient(
    90deg,
    var(--bg-secondary) 25%,
    var(--bg-tertiary) 50%,
    var(--bg-secondary) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  flex-shrink: 0;
}

.dark .skeleton-avatar {
  background: linear-gradient(
    90deg,
    var(--bg-secondary-dark) 25%,
    var(--bg-tertiary-dark) 50%,
    var(--bg-secondary-dark) 75%
  );
  background-size: 200% 100%;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

@media (prefers-reduced-motion: reduce) {
  .skeleton-container,
  .skeleton-line,
  .skeleton-image,
  .skeleton-avatar {
    animation: none;
  }
  
  .skeleton-line,
  .skeleton-image,
  .skeleton-avatar {
    background: var(--bg-secondary);
  }
  
  .dark .skeleton-line,
  .dark .skeleton-image,
  .dark .skeleton-avatar {
    background: var(--bg-secondary-dark);
  }
}
</style>


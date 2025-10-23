<template>
  <transition name="slide-down">
    <div v-if="selectedCount > 0" class="bulk-actions-bar">
      <div class="bulk-info">
        <span class="material-icons">check_circle</span>
        <span class="bulk-count">{{ selectedCount }} mục đã chọn</span>
      </div>
      
      <div class="bulk-buttons">
        <slot name="actions"></slot>
        
        <button 
          v-if="showDelete" 
          @click="$emit('delete')" 
          class="btn btn-danger btn-sm"
        >
          <span class="material-icons">delete</span>
          {{ deleteText }}
        </button>
        
        <button @click="$emit('clear')" class="btn btn-secondary btn-sm">
          <span class="material-icons">close</span>
          Bỏ chọn
        </button>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({
  selectedCount: {
    type: Number,
    required: true
  },
  showDelete: {
    type: Boolean,
    default: true
  },
  deleteText: {
    type: String,
    default: 'Xóa đã chọn'
  }
})

defineEmits(['delete', 'clear'])
</script>

<style scoped>
.bulk-actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4) var(--space-6);
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1), rgba(167, 139, 250, 0.15));
  border: 1px solid var(--accent-primary);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 4px 16px rgba(167, 139, 250, 0.2);
}

.bulk-info {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.bulk-info .material-icons {
  color: var(--accent-primary);
  font-size: 1.5rem;
}

.bulk-count {
  font-size: var(--text-base);
}

.bulk-buttons {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

/* Transition */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Responsive */
@media (max-width: 768px) {
  .bulk-actions-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .bulk-info {
    justify-content: center;
  }
  
  .bulk-buttons {
    flex-direction: column;
  }
  
  .bulk-buttons .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>


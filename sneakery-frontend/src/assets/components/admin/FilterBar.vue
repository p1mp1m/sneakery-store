<template>
  <div class="filter-bar">
    <!-- Search Box -->
    <div v-if="showSearch" class="search-box">
      <span class="material-icons search-icon">search</span>
      <input 
        type="text" 
        class="search-input"
        v-model="searchQuery"
        :placeholder="searchPlaceholder"
        @input="handleSearch"
      />
      <button 
        v-if="searchQuery" 
        @click="clearSearch" 
        class="search-clear"
      >
        <span class="material-icons">close</span>
      </button>
    </div>

    <!-- Filter Slots -->
    <div class="filters-group">
      <slot name="filters"></slot>
    </div>

    <!-- Action Buttons -->
    <div class="filter-actions">
      <slot name="actions">
        <button v-if="showReset" @click="handleReset" class="btn btn-secondary btn-sm">
          <span class="material-icons">refresh</span>
          {{ resetText }}
        </button>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { debounce } from '@/utils/debounce'

const props = defineProps({
  showSearch: {
    type: Boolean,
    default: true
  },
  searchPlaceholder: {
    type: String,
    default: 'Tìm kiếm...'
  },
  showReset: {
    type: Boolean,
    default: true
  },
  resetText: {
    type: String,
    default: 'Xóa bộ lọc'
  },
  debounceTime: {
    type: Number,
    default: 300
  }
})

const emit = defineEmits(['search', 'reset'])

const searchQuery = ref('')

const handleSearch = debounce((event) => {
  emit('search', searchQuery.value)
}, props.debounceTime)

const clearSearch = () => {
  searchQuery.value = ''
  emit('search', '')
}

const handleReset = () => {
  searchQuery.value = ''
  emit('reset')
}

// Expose for parent component
defineExpose({
  clearSearch,
  searchQuery
})
</script>

<style scoped>
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  background: var(--gradient-purple-soft);
  border-bottom: 1px solid var(--border-primary);
  flex-wrap: wrap;
}

/* Search Box */
.search-box {
  position: relative;
  flex: 1;
  min-width: 280px;
  max-width: 400px;
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  font-size: 1.25rem;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-12) var(--space-3) var(--space-12);
  border: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.6);
  border-radius: var(--radius-lg);
  font-size: var(--text-base);
  color: var(--text-primary);
  transition: all var(--transition-fast);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.search-input:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.8);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.search-input::placeholder {
  color: var(--text-quaternary);
}

.search-clear {
  position: absolute;
  right: var(--space-2);
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.search-clear:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Filters Group */
.filters-group {
  display: flex;
  gap: var(--space-3);
  flex-wrap: wrap;
  align-items: center;
}

/* Filter Actions */
.filter-actions {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

/* Responsive */
@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    max-width: none;
  }
  
  .filters-group {
    width: 100%;
  }
  
  .filter-actions {
    width: 100%;
  }
}
</style>


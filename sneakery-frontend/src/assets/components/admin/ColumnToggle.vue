<template>
  <div class="column-toggle-wrapper">
    <button
      @click="toggleDropdown"
      class="btn btn-secondary btn-column-toggle"
      :class="{ 'active': isOpen }"
      title="Hiển thị/ẩn cột"
    >
      <i class="material-icons">view_column</i>
      <span>Cột hiển thị</span>
      <i class="material-icons">{{ isOpen ? 'expand_less' : 'expand_more' }}</i>
    </button>

    <transition name="dropdown">
      <div v-if="isOpen" class="column-dropdown" v-click-outside="closeDropdown">
        <div class="dropdown-header">
          <span class="dropdown-title">Chọn cột hiển thị</span>
          <button @click="selectAll" class="btn-link">
            {{ allSelected ? 'Bỏ chọn tất cả' : 'Chọn tất cả' }}
          </button>
        </div>

        <div class="column-list">
          <label
            v-for="column in columns"
            :key="column.key"
            class="column-item"
            :class="{ 'disabled': column.required }"
          >
            <input
              type="checkbox"
              :checked="isColumnVisible(column.key)"
              @change="toggleColumn(column.key)"
              :disabled="column.required"
              class="checkbox-input"
            />
            <span class="column-label">
              {{ column.label }}
              <span v-if="column.required" class="required-badge">Bắt buộc</span>
            </span>
          </label>
        </div>

        <div class="dropdown-footer">
          <button @click="resetToDefault" class="btn btn-sm btn-secondary">
            <i class="material-icons">refresh</i>
            Mặc định
          </button>
          <button @click="closeDropdown" class="btn btn-sm btn-primary">
            <i class="material-icons">check</i>
            Áp dụng
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  columns: {
    type: Array,
    required: true
    // Format: [{ key: 'name', label: 'Tên', visible: true, required: false }, ...]
  },
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const isOpen = ref(false)

const visibleColumns = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const allSelected = computed(() => {
  const selectableColumns = props.columns.filter(c => !c.required)
  return selectableColumns.every(c => isColumnVisible(c.key))
})

const toggleDropdown = () => {
  isOpen.value = !isOpen.value
}

const closeDropdown = () => {
  isOpen.value = false
}

const isColumnVisible = (key) => {
  return visibleColumns.value.includes(key)
}

const toggleColumn = (key) => {
  const column = props.columns.find(c => c.key === key)
  if (column?.required) return

  if (isColumnVisible(key)) {
    visibleColumns.value = visibleColumns.value.filter(k => k !== key)
  } else {
    visibleColumns.value = [...visibleColumns.value, key]
  }
}

const selectAll = () => {
  if (allSelected.value) {
    // Deselect all (keep only required)
    visibleColumns.value = props.columns
      .filter(c => c.required)
      .map(c => c.key)
  } else {
    // Select all
    visibleColumns.value = props.columns.map(c => c.key)
  }
}

const resetToDefault = () => {
  visibleColumns.value = props.columns
    .filter(c => c.visible !== false)
    .map(c => c.key)
}

// Click outside directive
const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = (event) => {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value()
      }
    }
    document.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent)
  }
}
</script>

<style scoped>
.column-toggle-wrapper {
  position: relative;
}

.btn-column-toggle {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.btn-column-toggle.active {
  background: var(--gradient-primary);
  color: white;
}

.column-dropdown {
  position: absolute;
  top: calc(100% + var(--space-2));
  right: 0;
  min-width: 280px;
  background: var(--dark-bg-card, rgba(30, 41, 59, 0.95));
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  backdrop-filter: blur(10px);
  z-index: var(--z-dropdown);
  animation: slideDown 0.2s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  border-bottom: 1px solid var(--border-primary);
}

.dropdown-title {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.btn-link {
  background: none;
  border: none;
  color: var(--accent-primary);
  font-size: var(--text-xs);
  cursor: pointer;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  transition: var(--transition-fast);
}

.btn-link:hover {
  background: var(--gradient-purple-soft);
}

.column-list {
  max-height: 300px;
  overflow-y: auto;
  padding: var(--space-2);
}

.column-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
}

.column-item:hover:not(.disabled) {
  background: var(--gradient-purple-soft);
}

.column-item.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.column-label {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.required-badge {
  display: inline-block;
  padding: 2px var(--space-2);
  background: var(--accent-primary);
  color: white;
  font-size: 10px;
  border-radius: var(--radius-full);
  font-weight: var(--font-semibold);
}

.dropdown-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-2);
  padding: var(--space-4);
  border-top: 1px solid var(--border-primary);
}

/* Scrollbar */
.column-list::-webkit-scrollbar {
  width: 6px;
}

.column-list::-webkit-scrollbar-track {
  background: transparent;
}

.column-list::-webkit-scrollbar-thumb {
  background: var(--border-primary);
  border-radius: 3px;
}

.column-list::-webkit-scrollbar-thumb:hover {
  background: var(--border-hover);
}

/* Transition */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>


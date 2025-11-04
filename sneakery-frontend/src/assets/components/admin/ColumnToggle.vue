<template>
  <div class="relative">
    <button
      @click="toggleDropdown"
      class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
      :class="{ 'bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300': isOpen }"
      title="Hiển thị/ẩn cột"
    >
      <i class="material-icons text-base">view_column</i>
      <span>Cột hiển thị</span>
      <i class="material-icons text-base">{{ isOpen ? 'expand_less' : 'expand_more' }}</i>
    </button>

    <transition
      enter-active-class="transition-all duration-200 ease-out"
      enter-from-class="opacity-0 scale-95 -translate-y-2"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 scale-100 translate-y-0"
      leave-to-class="opacity-0 scale-95 -translate-y-2"
    >
      <div 
        v-if="isOpen" 
        class="absolute top-full right-0 mt-2 w-64 bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden z-50"
        v-click-outside="closeDropdown"
      >
        <div class="flex items-center justify-between p-3 border-b border-gray-200 dark:border-gray-700">
          <span class="text-sm font-semibold text-gray-900 dark:text-gray-100">Chọn cột hiển thị</span>
          <button 
            @click="selectAll" 
            class="text-xs text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 transition-colors font-medium"
          >
            {{ allSelected ? 'Bỏ chọn tất cả' : 'Chọn tất cả' }}
          </button>
        </div>

        <div class="max-h-64 overflow-y-auto p-2">
          <label
            v-for="column in columns"
            :key="column.key"
            class="flex items-center gap-2 p-2 rounded-lg cursor-pointer transition-colors"
            :class="{ 
              'opacity-50 cursor-not-allowed': column.required,
              'hover:bg-gray-50 dark:hover:bg-gray-700/50': !column.required
            }"
          >
            <input
              type="checkbox"
              :checked="isColumnVisible(column.key)"
              @change="toggleColumn(column.key)"
              :disabled="column.required"
              class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500 disabled:opacity-50"
            />
            <span class="flex-1 text-sm text-gray-700 dark:text-gray-300">
              {{ column.label }}
              <span v-if="column.required" class="ml-2 px-1.5 py-0.5 text-xs bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400 rounded">Bắt buộc</span>
            </span>
          </label>
        </div>

        <div class="flex items-center justify-end gap-2 p-3 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="resetToDefault" 
            class="flex items-center gap-1 px-3 py-1.5 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">refresh</i>
            Mặc định
          </button>
          <button 
            @click="closeDropdown" 
            class="flex items-center gap-1 px-3 py-1.5 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
          >
            <i class="material-icons text-base">check</i>
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





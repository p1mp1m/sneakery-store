<template>
  <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
    <div class="flex flex-col md:flex-row gap-4">
      <!-- Search Box -->
      <div v-if="showSearch" class="flex-1">
        <div class="relative">
          <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
          <input 
            type="text" 
            class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            v-model="searchQuery"
            :placeholder="searchPlaceholder"
            @input="handleSearch"
          />
          <button 
            v-if="searchQuery" 
            @click="clearSearch" 
            class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
          >
            <i class="material-icons text-base">close</i>
          </button>
        </div>
      </div>

      <!-- Filter Slots -->
      <div class="flex items-center gap-2">
        <slot name="filters"></slot>
      </div>

      <!-- Action Buttons -->
      <div class="flex items-center gap-2">
        <slot name="actions">
          <button 
            v-if="showReset" 
            @click="handleReset" 
            class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">refresh</i>
            {{ resetText }}
          </button>
        </slot>
      </div>
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





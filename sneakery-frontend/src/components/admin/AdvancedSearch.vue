<template>
  <div class="relative">
    <!-- Search Input -->
    <div class="relative">
      <input
        v-model="searchQuery"
        @input="handleInput"
        @focus="showSuggestions = true"
        @blur="handleBlur"
        @keydown.enter="handleSearch"
        @keydown.arrow-down="navigateSuggestions(1)"
        @keydown.arrow-up="navigateSuggestions(-1)"
        @keydown.escape="closeSuggestions"
        type="text"
        :placeholder="placeholder"
        class="w-full px-4 py-2 pl-10 pr-10 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        :aria-label="'Tìm kiếm'"
        :aria-expanded="showSuggestions"
        :aria-haspopup="true"
      />
      <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 pointer-events-none">
        search
      </i>
      <button
        v-if="searchQuery"
        @click="clearSearch"
        class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
        :aria-label="'Xóa tìm kiếm'"
      >
        <i class="material-icons text-base">close</i>
      </button>
    </div>

    <!-- Suggestions Dropdown -->
    <div
      v-if="showSuggestions && (suggestions.length > 0 || searchHistory.length > 0 || filterPresets.length > 0)"
      class="absolute z-50 w-full mt-1 bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 max-h-96 overflow-y-auto"
    >
      <!-- Search Suggestions -->
      <div v-if="suggestions.length > 0" class="p-2">
        <div class="px-2 py-1 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">
          Gợi ý
        </div>
        <button
          v-for="(suggestion, index) in suggestions"
          :key="index"
          @click="selectSuggestion(suggestion)"
          @mouseenter="selectedSuggestionIndex = index"
          class="w-full px-3 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors flex items-center gap-2"
          :class="{ 'bg-gray-100 dark:bg-gray-700': selectedSuggestionIndex === index }"
        >
          <i class="material-icons text-base text-gray-400">search</i>
          <span>{{ suggestion }}</span>
        </button>
      </div>

      <!-- Filter Presets -->
      <div v-if="filterPresets.length > 0 && !searchQuery" class="p-2 border-t border-gray-200 dark:border-gray-700">
        <div class="px-2 py-1 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">
          Bộ lọc nhanh
        </div>
        <button
          v-for="(preset, index) in filterPresets"
          :key="index"
          @click="selectPreset(preset)"
          class="w-full px-3 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors flex items-center gap-2"
        >
          <i class="material-icons text-base text-purple-500">{{ preset.icon || 'filter_list' }}</i>
          <span>{{ preset.label }}</span>
        </button>
      </div>

      <!-- Search History -->
      <div v-if="searchHistory.length > 0 && !searchQuery" class="p-2 border-t border-gray-200 dark:border-gray-700">
        <div class="flex items-center justify-between px-2 py-1">
          <div class="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">
            Lịch sử tìm kiếm
          </div>
          <button
            @click="clearHistory"
            class="text-xs text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300"
            :aria-label="'Xóa lịch sử'"
          >
            Xóa
          </button>
        </div>
        <button
          v-for="(historyItem, index) in searchHistory"
          :key="index"
          @click="selectHistory(historyItem)"
          class="w-full px-3 py-2 text-left text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors flex items-center justify-between"
        >
          <div class="flex items-center gap-2">
            <i class="material-icons text-base text-gray-400">history</i>
            <span>{{ historyItem }}</span>
          </div>
          <button
            @click.stop="removeFromHistory(index)"
            class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
            :aria-label="`Xóa ${historyItem} khỏi lịch sử`"
          >
            <i class="material-icons text-sm">close</i>
          </button>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useDebouncedSearch } from '@/composables/useDebouncedSearch'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: 'Tìm kiếm...'
  },
  debounceMs: {
    type: Number,
    default: 300
  },
  maxHistoryItems: {
    type: Number,
    default: 10
  },
  filterPresets: {
    type: Array,
    default: () => []
  },
  suggestionsFn: {
    type: Function,
    default: null
  },
  storageKey: {
    type: String,
    default: 'search_history'
  }
})

const emit = defineEmits(['update:modelValue', 'search', 'preset-selected'])

const searchQuery = ref(props.modelValue)
const showSuggestions = ref(false)
const suggestions = ref([])
const selectedSuggestionIndex = ref(-1)
const searchHistory = ref([])

// Load search history from localStorage
const loadHistory = () => {
  try {
    const stored = localStorage.getItem(props.storageKey)
    if (stored) {
      searchHistory.value = JSON.parse(stored).slice(0, props.maxHistoryItems)
    }
  } catch (error) {
    console.error('Error loading search history:', error)
  }
}

// Save search history to localStorage
const saveHistory = () => {
  try {
    localStorage.setItem(props.storageKey, JSON.stringify(searchHistory.value))
  } catch (error) {
    console.error('Error saving search history:', error)
  }
}

// Add to search history
const addToHistory = (query) => {
  if (!query || query.trim() === '') return
  
  const trimmedQuery = query.trim()
  
  // Remove if already exists
  const index = searchHistory.value.indexOf(trimmedQuery)
  if (index !== -1) {
    searchHistory.value.splice(index, 1)
  }
  
  // Add to beginning
  searchHistory.value.unshift(trimmedQuery)
  
  // Limit history size
  if (searchHistory.value.length > props.maxHistoryItems) {
    searchHistory.value = searchHistory.value.slice(0, props.maxHistoryItems)
  }
  
  saveHistory()
}

// Remove from history
const removeFromHistory = (index) => {
  searchHistory.value.splice(index, 1)
  saveHistory()
}

// Clear history
const clearHistory = () => {
  searchHistory.value = []
  saveHistory()
}

// Handle input
const handleInput = async () => {
  emit('update:modelValue', searchQuery.value)
  
  if (searchQuery.value && props.suggestionsFn) {
    try {
      suggestions.value = await props.suggestionsFn(searchQuery.value)
    } catch (error) {
      console.error('Error fetching suggestions:', error)
      suggestions.value = []
    }
  } else {
    suggestions.value = []
  }
}

// Handle blur (with delay to allow clicks)
const handleBlur = () => {
  setTimeout(() => {
    showSuggestions.value = false
    selectedSuggestionIndex.value = -1
  }, 200)
}

// Handle search
const handleSearch = () => {
  if (selectedSuggestionIndex.value >= 0 && suggestions.value[selectedSuggestionIndex.value]) {
    selectSuggestion(suggestions.value[selectedSuggestionIndex.value])
    return
  }
  
  if (searchQuery.value && searchQuery.value.trim()) {
    addToHistory(searchQuery.value.trim())
    emit('search', searchQuery.value.trim())
    showSuggestions.value = false
  }
}

// Select suggestion
const selectSuggestion = (suggestion) => {
  searchQuery.value = suggestion
  emit('update:modelValue', suggestion)
  addToHistory(suggestion)
  emit('search', suggestion)
  showSuggestions.value = false
}

// Select history item
const selectHistory = (historyItem) => {
  searchQuery.value = historyItem
  emit('update:modelValue', historyItem)
  emit('search', historyItem)
  showSuggestions.value = false
}

// Select preset
const selectPreset = (preset) => {
  emit('preset-selected', preset)
  showSuggestions.value = false
}

// Navigate suggestions with keyboard
const navigateSuggestions = (direction) => {
  const maxIndex = suggestions.value.length - 1
  selectedSuggestionIndex.value += direction
  
  if (selectedSuggestionIndex.value < 0) {
    selectedSuggestionIndex.value = maxIndex
  } else if (selectedSuggestionIndex.value > maxIndex) {
    selectedSuggestionIndex.value = 0
  }
}

// Close suggestions
const closeSuggestions = () => {
  showSuggestions.value = false
  selectedSuggestionIndex.value = -1
}

// Clear search
const clearSearch = () => {
  searchQuery.value = ''
  emit('update:modelValue', '')
  suggestions.value = []
  showSuggestions.value = false
}

// Watch modelValue changes from parent
watch(() => props.modelValue, (newValue) => {
  if (newValue !== searchQuery.value) {
    searchQuery.value = newValue
  }
})

// Load history on mount
onMounted(() => {
  loadHistory()
})
</script>


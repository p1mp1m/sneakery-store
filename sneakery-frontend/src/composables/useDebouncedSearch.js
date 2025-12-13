/**
 * Composable for debounced search with suggestions and history
 * Provides debounced search functionality with configurable delay, suggestions, and search history
 * 
 * @example
 * ```javascript
 * const { 
 *   searchQuery, 
 *   debouncedQuery, 
 *   isSearching, 
 *   suggestions,
 *   searchHistory,
 *   setSearchQuery,
 *   addToHistory,
 *   clearHistory
 * } = useDebouncedSearch(
 *   '',
 *   300,
 *   (value) => {
 *     // Perform search with debounced value
 *     performSearch(value);
 *   },
 *   {
 *     suggestionsFn: async (query) => {
 *       // Fetch suggestions from API
 *       return await fetchSuggestions(query);
 *     },
 *     storageKey: 'product_search_history',
 *     maxHistoryItems: 10
 *   }
 * );
 * ```
 * 
 * @param {string} initialValue - Initial search query value
 * @param {number} delay - Debounce delay in milliseconds (default: 300)
 * @param {Function|null} onSearch - Callback function called when search is triggered
 * @param {Object} options - Options
 * @param {Function|null} options.suggestionsFn - Async function to fetch suggestions
 * @param {string} options.storageKey - localStorage key for search history (default: 'search_history')
 * @param {number} options.maxHistoryItems - Maximum number of history items (default: 10)
 * 
 * @returns {Object} Search utilities
 * @returns {Ref<string>} returns.searchQuery - Current search query (reactive)
 * @returns {Ref<string>} returns.debouncedQuery - Debounced search query (reactive)
 * @returns {Ref<boolean>} returns.isSearching - Whether search is in progress (reactive)
 * @returns {Ref<Array>} returns.suggestions - Search suggestions (reactive)
 * @returns {Ref<Array>} returns.searchHistory - Search history (reactive)
 * @returns {Function} returns.setSearchQuery - Update search query
 * @returns {Function} returns.clearSearch - Clear search query
 * @returns {Function} returns.searchImmediately - Trigger search immediately (bypass debounce)
 * @returns {Function} returns.addToHistory - Add query to search history
 * @returns {Function} returns.removeFromHistory - Remove item from search history
 * @returns {Function} returns.clearHistory - Clear all search history
 * @returns {Function} returns.loadSuggestions - Load suggestions for current query
 */

import { ref, watch, onMounted } from 'vue';
import { debounce } from '@/utils/debounce';

export function useDebouncedSearch(initialValue = '', delay = 300, onSearch = null, options = {}) {
  const {
    suggestionsFn = null,
    storageKey = 'search_history',
    maxHistoryItems = 10
  } = options;

  const searchQuery = ref(initialValue);
  const debouncedQuery = ref(initialValue);
  const isSearching = ref(false);
  const suggestions = ref([]);
  const searchHistory = ref([]);
  const isLoadingSuggestions = ref(false);

  // Load search history from localStorage
  const loadHistory = () => {
    try {
      const stored = localStorage.getItem(storageKey);
      if (stored) {
        searchHistory.value = JSON.parse(stored).slice(0, maxHistoryItems);
      }
    } catch (error) {
      console.error('Error loading search history:', error);
    }
  };

  // Save search history to localStorage
  const saveHistory = () => {
    try {
      localStorage.setItem(storageKey, JSON.stringify(searchHistory.value));
    } catch (error) {
      console.error('Error saving search history:', error);
    }
  };

  // Add to search history
  const addToHistory = (query) => {
    if (!query || query.trim() === '') return;
    
    const trimmedQuery = query.trim();
    
    // Remove if already exists
    const index = searchHistory.value.indexOf(trimmedQuery);
    if (index !== -1) {
      searchHistory.value.splice(index, 1);
    }
    
    // Add to beginning
    searchHistory.value.unshift(trimmedQuery);
    
    // Limit history size
    if (searchHistory.value.length > maxHistoryItems) {
      searchHistory.value = searchHistory.value.slice(0, maxHistoryItems);
    }
    
    saveHistory();
  };

  // Remove from history
  const removeFromHistory = (index) => {
    searchHistory.value.splice(index, 1);
    saveHistory();
  };

  // Clear history
  const clearHistory = () => {
    searchHistory.value = [];
    saveHistory();
  };

  // Load suggestions
  const loadSuggestions = async (query) => {
    if (!suggestionsFn || !query || query.trim() === '') {
      suggestions.value = [];
      return;
    }

    isLoadingSuggestions.value = true;
    try {
      const results = await suggestionsFn(query.trim());
      suggestions.value = Array.isArray(results) ? results : [];
    } catch (error) {
      console.error('Error loading suggestions:', error);
      suggestions.value = [];
    } finally {
      isLoadingSuggestions.value = false;
    }
  };

  // Debounced suggestions loader
  const debouncedLoadSuggestions = debounce((value) => {
    loadSuggestions(value);
  }, delay);

  // Create debounced function for search
  const debouncedSearch = debounce((value) => {
    debouncedQuery.value = value;
    isSearching.value = false;
    if (onSearch && typeof onSearch === 'function') {
      onSearch(value);
    }
  }, delay);

  // Watch for changes in searchQuery
  watch(searchQuery, (newValue) => {
    isSearching.value = true;
    debouncedSearch(newValue);
    
    // Load suggestions if function provided
    if (suggestionsFn) {
      debouncedLoadSuggestions(newValue);
    }
  });

  /**
   * Update search query
   * @param {string} value - New search value
   */
  const setSearchQuery = (value) => {
    searchQuery.value = value;
  };

  /**
   * Clear search query
   */
  const clearSearch = () => {
    searchQuery.value = '';
    debouncedQuery.value = '';
    isSearching.value = false;
    suggestions.value = [];
  };

  /**
   * Trigger search immediately (bypass debounce)
   */
  const searchImmediately = () => {
    debouncedQuery.value = searchQuery.value;
    isSearching.value = false;
    if (onSearch && typeof onSearch === 'function') {
      onSearch(debouncedQuery.value);
    }
    if (searchQuery.value) {
      addToHistory(searchQuery.value);
    }
  };

  // Load history on mount
  onMounted(() => {
    loadHistory();
  });

  return {
    searchQuery,
    debouncedQuery,
    isSearching,
    suggestions,
    isLoadingSuggestions,
    searchHistory,
    setSearchQuery,
    clearSearch,
    searchImmediately,
    addToHistory,
    removeFromHistory,
    clearHistory,
    loadSuggestions
  };
}


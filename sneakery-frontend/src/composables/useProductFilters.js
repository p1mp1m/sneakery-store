/**
 * useProductFilters Composable
 * 
 * Tách logic filters và sorting từ AdminProducts.vue
 * Bao gồm: search, brand, category, status, price range, stock level, sorting
 */

import { ref, computed } from 'vue'
import { StockLevel, Status, SortField, SortDirection, SEARCH_DEBOUNCE_DELAY } from '@/utils/productConstants'

/**
 * Simple debounce function
 * 
 * @param {Function} func - Function to debounce
 * @param {number} delay - Delay in milliseconds
 * @returns {Function} Debounced function
 */
function debounce(func, delay) {
  let timeoutId
  return function (...args) {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => func.apply(this, args), delay)
  }
}

export function useProductFilters() {
  // Filter state
  const filters = ref({
    search: '',
    brandId: null,
    categoryId: null,
    status: Status.ALL,
    minPrice: null,
    maxPrice: null,
    stockLevel: StockLevel.ALL,
    sortBy: '',
    sortDirection: SortDirection.ASC
  })

  // Sort state (for UI)
  const sortBy = ref('')
  const sortOrder = ref(SortDirection.ASC)

  /**
   * Reset all filters to default
   */
  const resetFilters = () => {
    filters.value = {
      search: '',
      brandId: null,
      categoryId: null,
      status: Status.ALL,
      minPrice: null,
      maxPrice: null,
      stockLevel: StockLevel.ALL,
      sortBy: '',
      sortDirection: SortDirection.ASC
    }
    sortBy.value = ''
    sortOrder.value = SortDirection.ASC
  }

  /**
   * Apply filters (sync with API filter format)
   * 
   * @returns {Object} Clean filters object for API
   */
  const getApiFilters = () => {
    const apiFilters = {}

    if (filters.value.search?.trim()) {
      apiFilters.search = filters.value.search.trim()
    }

    if (filters.value.brandId) {
      apiFilters.brandId = filters.value.brandId
    }

    if (filters.value.categoryId) {
      apiFilters.categoryId = filters.value.categoryId
    }

    if (filters.value.status && filters.value.status !== Status.ALL) {
      apiFilters.isActive = filters.value.status === Status.ACTIVE
    }

    if (filters.value.minPrice !== null && filters.value.minPrice !== '') {
      apiFilters.minPrice = Number(filters.value.minPrice)
    }

    if (filters.value.maxPrice !== null && filters.value.maxPrice !== '') {
      apiFilters.maxPrice = Number(filters.value.maxPrice)
    }

    if (filters.value.stockLevel && filters.value.stockLevel !== StockLevel.ALL) {
      apiFilters.stockLevel = filters.value.stockLevel
    }

    if (filters.value.sortBy) {
      apiFilters.sortBy = filters.value.sortBy
      apiFilters.sortDirection = filters.value.sortDirection || SortDirection.ASC
    }

    return apiFilters
  }

  /**
   * Sort column handler
   * 
   * @param {string} column - Column name to sort
   */
  const sortColumn = (column) => {
    if (sortBy.value === column) {
      // Toggle sort order if clicking same column
      sortOrder.value = sortOrder.value === SortDirection.ASC ? SortDirection.DESC : SortDirection.ASC
    } else {
      // Set new column and default to ascending
      sortBy.value = column
      sortOrder.value = SortDirection.ASC
    }

    // Update filters
    filters.value.sortBy = sortBy.value
    filters.value.sortDirection = sortOrder.value
  }

  /**
   * Get sort icon for column header
   * 
   * @param {string} column - Column name
   * @returns {string} Icon name or empty string
   */
  const getSortIcon = (column) => {
    if (sortBy.value !== column) return ''
    return sortOrder.value === SortDirection.ASC ? 'arrow_upward' : 'arrow_downward'
  }

  /**
   * Get sort class for column header
   * 
   * @param {string} column - Column name
   * @returns {string} CSS class
   */
  const getSortClass = (column) => {
    if (sortBy.value !== column) return ''
    return sortOrder.value === SortDirection.ASC ? 'sort-asc' : 'sort-desc'
  }

  /**
   * Debounced search handler
   * This will be called when search input changes
   */
  const debouncedSearch = debounce((callback) => {
    if (callback) callback()
  }, SEARCH_DEBOUNCE_DELAY)

  /**
   * Set filter value
   * 
   * @param {string} key - Filter key
   * @param {any} value - Filter value
   */
  const setFilter = (key, value) => {
    filters.value[key] = value
  }

  /**
   * Get filter value
   * 
   * @param {string} key - Filter key
   * @returns {any} Filter value
   */
  const getFilter = (key) => {
    return filters.value[key]
  }

  /**
   * Check if any filter is active
   */
  const hasActiveFilters = computed(() => {
    return (
      filters.value.search?.trim() ||
      filters.value.brandId ||
      filters.value.categoryId ||
      filters.value.status !== Status.ALL ||
      filters.value.minPrice !== null ||
      filters.value.maxPrice !== null ||
      filters.value.stockLevel !== StockLevel.ALL ||
      filters.value.sortBy
    )
  })

  /**
   * Get active filters count
   */
  const activeFiltersCount = computed(() => {
    let count = 0
    if (filters.value.search?.trim()) count++
    if (filters.value.brandId) count++
    if (filters.value.categoryId) count++
    if (filters.value.status !== Status.ALL) count++
    if (filters.value.minPrice !== null) count++
    if (filters.value.maxPrice !== null) count++
    if (filters.value.stockLevel !== StockLevel.ALL) count++
    if (filters.value.sortBy) count++
    return count
  })

  return {
    // State
    filters,
    sortBy,
    sortOrder,

    // Computed
    hasActiveFilters,
    activeFiltersCount,

    // Methods
    resetFilters,
    getApiFilters,
    sortColumn,
    getSortIcon,
    getSortClass,
    debouncedSearch,
    setFilter,
    getFilter
  }
}


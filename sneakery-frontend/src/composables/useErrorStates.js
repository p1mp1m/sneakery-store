/**
 * Composable for managing error and empty states
 * Provides utilities for handling loading, error, and empty states in admin pages
 * 
 * @example
 * ```javascript
 * const { 
 *   loading, 
 *   error, 
 *   isEmpty, 
 *   setLoading, 
 *   setError, 
 *   clearError,
 *   reset 
 * } = useErrorStates();
 * ```
 */

import { ref, computed } from 'vue'

export function useErrorStates() {
  const loading = ref(false)
  const error = ref(null)
  const isEmpty = ref(false)

  /**
   * Set loading state
   * @param {boolean} value - Loading state
   */
  const setLoading = (value) => {
    loading.value = value
    if (value) {
      // Clear error when starting to load
      error.value = null
    }
  }

  /**
   * Set error state
   * @param {Error|string|Object} err - Error object, message, or error response
   * @param {string} defaultMessage - Default message if error doesn't have one
   */
  const setError = (err, defaultMessage = 'Đã xảy ra lỗi') => {
    loading.value = false
    
    if (typeof err === 'string') {
      error.value = {
        message: err,
        code: null,
        status: null
      }
    } else if (err?.response) {
      // Axios error response
      const response = err.response
      error.value = {
        message: response.data?.message || defaultMessage,
        code: response.data?.errorCode || null,
        status: response.status,
        validationErrors: response.data?.validationErrors || null
      }
    } else if (err?.message) {
      // Error object
      error.value = {
        message: err.message,
        code: err.code || null,
        status: err.status || null
      }
    } else {
      error.value = {
        message: defaultMessage,
        code: null,
        status: null
      }
    }
  }

  /**
   * Clear error state
   */
  const clearError = () => {
    error.value = null
  }

  /**
   * Set empty state
   * @param {boolean} value - Empty state
   */
  const setEmpty = (value) => {
    isEmpty.value = value
  }

  /**
   * Check if there's an error
   * @returns {boolean}
   */
  const hasError = computed(() => {
    return error.value !== null
  })

  /**
   * Get error message
   * @returns {string|null}
   */
  const errorMessage = computed(() => {
    return error.value?.message || null
  })

  /**
   * Get error code
   * @returns {string|null}
   */
  const errorCode = computed(() => {
    return error.value?.code || null
  })

  /**
   * Get validation errors
   * @returns {Object|null}
   */
  const validationErrors = computed(() => {
    return error.value?.validationErrors || null
  })

  /**
   * Reset all states
   */
  const reset = () => {
    loading.value = false
    error.value = null
    isEmpty.value = false
  }

  /**
   * Execute async function with error handling
   * @param {Function} fn - Async function to execute
   * @param {Object} options - Options
   * @param {boolean} options.setLoading - Set loading state (default: true)
   * @param {string} options.errorMessage - Custom error message
   * @returns {Promise<any>}
   */
  const execute = async (fn, options = {}) => {
    const { setLoading: shouldSetLoading = true, errorMessage: customErrorMessage } = options

    try {
      if (shouldSetLoading) {
        setLoading(true)
      }
      clearError()
      
      const result = await fn()
      
      if (shouldSetLoading) {
        setLoading(false)
      }
      
      return result
    } catch (err) {
      setError(err, customErrorMessage)
      if (shouldSetLoading) {
        setLoading(false)
      }
      throw err
    }
  }

  return {
    // State
    loading,
    error,
    isEmpty,
    
    // Computed
    hasError,
    errorMessage,
    errorCode,
    validationErrors,
    
    // Methods
    setLoading,
    setError,
    clearError,
    setEmpty,
    reset,
    execute
  }
}


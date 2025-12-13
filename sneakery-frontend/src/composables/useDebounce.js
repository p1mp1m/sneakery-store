import { ref, watch } from 'vue'

/**
 * Composable for debouncing function calls
 * @param {Function} fn - Function to debounce
 * @param {Number} delay - Delay in milliseconds
 * @returns {Function} Debounced function
 */
export function useDebounce(fn, delay = 300) {
  let timeoutId = null

  return function debounced(...args) {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * Composable for creating a debounced ref
 * Useful for search inputs and filters
 * @param {import('vue').Ref} source - Source ref to debounce
 * @param {Number} delay - Delay in milliseconds
 * @returns {import('vue').Ref} Debounced ref
 */
export function useDebouncedRef(source, delay = 300) {
  const debounced = ref(source.value)

  let timeoutId = null

  watch(source, (newValue) => {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => {
      debounced.value = newValue
    }, delay)
  })

  return debounced
}


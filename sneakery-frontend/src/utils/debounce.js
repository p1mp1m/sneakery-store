/**
 * Debounce utility function
 * Delays function execution until after wait milliseconds have elapsed since last call
 */
export function debounce(func, wait = 500) {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

/**
 * Throttle utility function
 * Ensures function is called at most once per specified time period
 */
export function throttle(func, wait = 500) {
  let timeout
  let lastRan
  return function executedFunction(...args) {
    if (!lastRan) {
      func(...args)
      lastRan = Date.now()
    } else {
      clearTimeout(timeout)
      timeout = setTimeout(() => {
        if (Date.now() - lastRan >= wait) {
          func(...args)
          lastRan = Date.now()
        }
      }, wait - (Date.now() - lastRan))
    }
  }
}


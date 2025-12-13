import { ref, nextTick, onMounted, onUnmounted } from 'vue'

/**
 * Composable for managing focus in modals and dialogs
 * Ensures proper focus trap and restoration
 */
export function useFocusManagement() {
  const focusableElements = ref([])
  const previousActiveElement = ref(null)

  /**
   * Get all focusable elements within a container
   * @param {HTMLElement} container - Container element
   * @returns {HTMLElement[]} Array of focusable elements
   */
  const getFocusableElements = (container) => {
    const focusableSelectors = [
      'a[href]',
      'button:not([disabled])',
      'textarea:not([disabled])',
      'input:not([disabled])',
      'select:not([disabled])',
      '[tabindex]:not([tabindex="-1"])'
    ].join(', ')

    return Array.from(container.querySelectorAll(focusableSelectors)).filter(
      (el) => {
        const style = window.getComputedStyle(el)
        return style.display !== 'none' && style.visibility !== 'hidden'
      }
    )
  }

  /**
   * Trap focus within a modal/dialog
   * @param {HTMLElement} container - Modal container element
   */
  const trapFocus = (container) => {
    if (!container) return

    focusableElements.value = getFocusableElements(container)

    if (focusableElements.value.length === 0) return

    // Save current active element
    previousActiveElement.value = document.activeElement

    // Focus first element
    const firstElement = focusableElements.value[0]
    firstElement?.focus()

    // Handle keyboard navigation
    const handleKeyDown = (e) => {
      if (e.key !== 'Tab') return

      const firstElement = focusableElements.value[0]
      const lastElement = focusableElements.value[focusableElements.value.length - 1]

      if (e.shiftKey) {
        // Shift + Tab
        if (document.activeElement === firstElement) {
          e.preventDefault()
          lastElement?.focus()
        }
      } else {
        // Tab
        if (document.activeElement === lastElement) {
          e.preventDefault()
          firstElement?.focus()
        }
      }
    }

    container.addEventListener('keydown', handleKeyDown)

    // Return cleanup function
    return () => {
      container.removeEventListener('keydown', handleKeyDown)
    }
  }

  /**
   * Restore focus to previous element
   */
  const restoreFocus = () => {
    if (previousActiveElement.value) {
      previousActiveElement.value.focus()
      previousActiveElement.value = null
    }
  }

  /**
   * Focus first element in container
   * @param {HTMLElement} container - Container element
   */
  const focusFirst = (container) => {
    nextTick(() => {
      if (!container) return
      focusableElements.value = getFocusableElements(container)
      focusableElements.value[0]?.focus()
    })
  }

  /**
   * Set ARIA attributes for modal
   * @param {HTMLElement} modal - Modal element
   * @param {HTMLElement} trigger - Trigger element (optional)
   */
  const setupModal = (modal, trigger = null) => {
    if (!modal) return

    modal.setAttribute('role', 'dialog')
    modal.setAttribute('aria-modal', 'true')
    
    if (trigger) {
      modal.setAttribute('aria-labelledby', trigger.id || 'modal-title')
    }

    // Return cleanup function
    return () => {
      restoreFocus()
    }
  }

  return {
    trapFocus,
    restoreFocus,
    focusFirst,
    setupModal,
    getFocusableElements
  }
}

/**
 * Composable for keyboard shortcuts
 */
export function useKeyboardShortcuts() {
  const shortcuts = ref(new Map())

  /**
   * Register a keyboard shortcut
   * @param {string} key - Key combination (e.g., 'ctrl+k', 'Escape')
   * @param {Function} callback - Callback function
   * @param {Object} options - Options (preventDefault, stopPropagation)
   */
  const register = (key, callback, options = {}) => {
    shortcuts.value.set(key, { callback, ...options })
  }

  /**
   * Unregister a keyboard shortcut
   * @param {string} key - Key combination
   */
  const unregister = (key) => {
    shortcuts.value.delete(key)
  }

  /**
   * Handle keydown event
   * @param {KeyboardEvent} event - Keyboard event
   */
  const handleKeyDown = (event) => {
    let key = event.key

    // Build key string
    if (event.ctrlKey || event.metaKey) key = `ctrl+${key.toLowerCase()}`
    if (event.shiftKey) key = `shift+${key.toLowerCase()}`
    if (event.altKey) key = `alt+${key.toLowerCase()}`

    const shortcut = shortcuts.value.get(key)

    if (shortcut) {
      if (shortcut.preventDefault !== false) {
        event.preventDefault()
      }
      if (shortcut.stopPropagation) {
        event.stopPropagation()
      }
      shortcut.callback(event)
    }
  }

  onMounted(() => {
    document.addEventListener('keydown', handleKeyDown)
  })

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyDown)
    shortcuts.value.clear()
  })

  return {
    register,
    unregister,
    handleKeyDown
  }
}


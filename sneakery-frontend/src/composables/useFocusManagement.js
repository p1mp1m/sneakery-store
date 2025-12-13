/**
 * Composable for managing focus in modals and dialogs
 * Ensures proper focus trapping and restoration for accessibility
 * 
 * @example
 * ```javascript
 * const { setupModalFocus, cleanupModalFocus, saveActiveElement } = useFocusManagement();
 * 
 * // When opening modal
 * saveActiveElement();
 * setupModalFocus(modalRef.value);
 * 
 * // When closing modal
 * cleanupModalFocus(modalRef.value);
 * ```
 * 
 * @returns {Object} Focus management utilities
 * @returns {Function} returns.trapFocus - Trap focus within a container
 * @returns {Function} returns.releaseFocusTrap - Release focus trap
 * @returns {Function} returns.saveActiveElement - Save currently active element
 * @returns {Function} returns.restoreFocus - Restore focus to previously active element
 * @returns {Function} returns.focusFirst - Focus first element in container
 * @returns {Function} returns.focusLast - Focus last element in container
 * @returns {Function} returns.focusElement - Focus a specific element
 * @returns {Function} returns.setupModalFocus - Setup focus management for a modal
 * @returns {Function} returns.cleanupModalFocus - Cleanup focus management for a modal
 * @returns {Function} returns.getFocusableElements - Get all focusable elements in container
 */

import { ref, nextTick, onUnmounted } from 'vue';

export function useFocusManagement() {
  const focusableElements = ref([]);
  const previousActiveElement = ref(null);
  const firstFocusableElement = ref(null);
  const lastFocusableElement = ref(null);

  /**
   * Get all focusable elements within a container
   * @param {HTMLElement} container - Container element
   * @returns {HTMLElement[]} Array of focusable elements
   */
  const getFocusableElements = (container) => {
    if (!container) return [];

    const focusableSelectors = [
      'a[href]',
      'button:not([disabled])',
      'textarea:not([disabled])',
      'input:not([disabled])',
      'select:not([disabled])',
      '[tabindex]:not([tabindex="-1"])',
      '[contenteditable="true"]'
    ].join(', ');

    const elements = Array.from(container.querySelectorAll(focusableSelectors));
    
    // Filter out hidden elements
    return elements.filter(el => {
      const style = window.getComputedStyle(el);
      return style.display !== 'none' && style.visibility !== 'hidden' && !el.hasAttribute('disabled');
    });
  };

  /**
   * Trap focus within a container (for modals)
   * @param {HTMLElement} container - Container element to trap focus in
   */
  const trapFocus = (container) => {
    if (!container) return;

    focusableElements.value = getFocusableElements(container);
    
    if (focusableElements.value.length === 0) return;

    firstFocusableElement.value = focusableElements.value[0];
    lastFocusableElement.value = focusableElements.value[focusableElements.value.length - 1];

    // Focus first element
    nextTick(() => {
      if (firstFocusableElement.value) {
        firstFocusableElement.value.focus();
      }
    });

    // Handle Tab key to cycle through focusable elements
    const handleKeyDown = (event) => {
      if (event.key !== 'Tab') return;

      if (focusableElements.value.length === 0) return;

      if (event.shiftKey) {
        // Shift + Tab: move backwards
        if (document.activeElement === firstFocusableElement.value) {
          event.preventDefault();
          lastFocusableElement.value?.focus();
        }
      } else {
        // Tab: move forwards
        if (document.activeElement === lastFocusableElement.value) {
          event.preventDefault();
          firstFocusableElement.value?.focus();
        }
      }
    };

    container.addEventListener('keydown', handleKeyDown);

    // Store handler for cleanup
    container._focusHandler = handleKeyDown;
  };

  /**
   * Release focus trap
   * @param {HTMLElement} container - Container element
   */
  const releaseFocusTrap = (container) => {
    if (!container || !container._focusHandler) return;
    container.removeEventListener('keydown', container._focusHandler);
    delete container._focusHandler;
  };

  /**
   * Save the currently active element (before opening modal)
   */
  const saveActiveElement = () => {
    previousActiveElement.value = document.activeElement;
  };

  /**
   * Restore focus to the previously active element (after closing modal)
   */
  const restoreFocus = () => {
    if (previousActiveElement.value && previousActiveElement.value.focus) {
      nextTick(() => {
        previousActiveElement.value.focus();
        previousActiveElement.value = null;
      });
    }
  };

  /**
   * Focus first element in container
   * @param {HTMLElement} container - Container element
   */
  const focusFirst = (container) => {
    if (!container) return;
    const elements = getFocusableElements(container);
    if (elements.length > 0) {
      nextTick(() => {
        elements[0].focus();
      });
    }
  };

  /**
   * Focus last element in container
   * @param {HTMLElement} container - Container element
   */
  const focusLast = (container) => {
    if (!container) return;
    const elements = getFocusableElements(container);
    if (elements.length > 0) {
      nextTick(() => {
        elements[elements.length - 1].focus();
      });
    }
  };

  /**
   * Focus a specific element
   * @param {HTMLElement|string} element - Element or selector
   */
  const focusElement = (element) => {
    if (!element) return;
    
    const el = typeof element === 'string' 
      ? document.querySelector(element)
      : element;
    
    if (el && el.focus) {
      nextTick(() => {
        el.focus();
      });
    }
  };

  /**
   * Setup focus management for a modal
   * @param {HTMLElement} modalElement - Modal element
   * @param {HTMLElement} triggerElement - Element that triggered the modal (optional)
   */
  const setupModalFocus = (modalElement, triggerElement = null) => {
    if (triggerElement) {
      saveActiveElement();
    }
    trapFocus(modalElement);
  };

  /**
   * Cleanup focus management for a modal
   * @param {HTMLElement} modalElement - Modal element
   */
  const cleanupModalFocus = (modalElement) => {
    releaseFocusTrap(modalElement);
    restoreFocus();
  };

  // Cleanup on unmount
  onUnmounted(() => {
    if (previousActiveElement.value) {
      previousActiveElement.value = null;
    }
  });

  return {
    trapFocus,
    releaseFocusTrap,
    saveActiveElement,
    restoreFocus,
    focusFirst,
    focusLast,
    focusElement,
    setupModalFocus,
    cleanupModalFocus,
    getFocusableElements
  };
}

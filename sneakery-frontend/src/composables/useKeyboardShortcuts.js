/**
 * Composable for keyboard shortcuts
 * Provides keyboard shortcut functionality with context awareness
 * 
 * @example
 * ```javascript
 * const { registerShortcut } = useKeyboardShortcuts({
 *   'ctrl+n': () => openCreateModal(),
 *   'ctrl+s': () => saveForm()
 * });
 * ```
 * 
 * @param {Object} shortcuts - Object mapping key combinations to handler functions
 * @param {string} shortcuts[keyCombo] - Key combination (e.g., 'ctrl+k', 'shift+?')
 * @param {Function} shortcuts[keyCombo] - Handler function that receives KeyboardEvent
 * 
 * @returns {Object} Keyboard shortcut utilities
 * @returns {Function} returns.registerShortcut - Register a new shortcut
 * @returns {Function} returns.unregisterShortcut - Unregister a shortcut
 */

import { onMounted, onUnmounted } from 'vue';

export function useKeyboardShortcuts(shortcuts = {}) {
  const defaultShortcuts = {
    // Quick search
    'ctrl+k': (e) => {
      e.preventDefault();
      const searchInput = document.querySelector('input[type="search"], input[placeholder*="Tìm"], input[placeholder*="Search"]');
      if (searchInput) {
        searchInput.focus();
        searchInput.select();
      }
    },
    // Escape - Close modals/dropdowns
    'escape': (e) => {
      // Close any open modals
      const modals = document.querySelectorAll('.modal, [role="dialog"]');
      modals.forEach(modal => {
        if (modal.style.display !== 'none') {
          const closeButton = modal.querySelector('[data-dismiss="modal"], .close, button[aria-label*="close" i]');
          if (closeButton) {
            closeButton.click();
          }
        }
      });
      
      // Close dropdowns
      const dropdowns = document.querySelectorAll('.dropdown-menu.show, [data-dropdown].open');
      dropdowns.forEach(dropdown => {
        const toggle = dropdown.previousElementSibling || dropdown.parentElement.querySelector('[data-toggle="dropdown"]');
        if (toggle) {
          toggle.click();
        }
      });
    },
    // Show shortcuts help
    'shift+?': (e) => {
      e.preventDefault();
      // This will be handled by the component using this composable
      if (shortcuts.onShowHelp) {
        shortcuts.onShowHelp();
      }
    }
  };

  const mergedShortcuts = { ...defaultShortcuts, ...shortcuts };

  /**
   * Normalize key combination
   * @param {string} key - Key combination (e.g., 'ctrl+k', 'escape')
   * @returns {string} Normalized key
   */
  const normalizeKey = (key) => {
    return key.toLowerCase()
      .replace(/\s+/g, '')
      .replace('cmd', 'meta')
      .replace('command', 'meta');
  };

  /**
   * Check if key combination matches
   * @param {KeyboardEvent} event - Keyboard event
   * @param {string} keyCombo - Key combination to match
   * @returns {boolean} True if matches
   */
  const matchesKey = (event, keyCombo) => {
    const normalized = normalizeKey(keyCombo);
    const parts = normalized.split('+');
    
    let ctrl = false;
    let shift = false;
    let alt = false;
    let meta = false;
    let key = '';

    parts.forEach(part => {
      if (part === 'ctrl' || part === 'control') ctrl = true;
      else if (part === 'shift') shift = true;
      else if (part === 'alt') alt = true;
      else if (part === 'meta' || part === 'cmd') meta = true;
      else key = part;
    });

    return (
      event.ctrlKey === ctrl &&
      event.shiftKey === shift &&
      event.altKey === alt &&
      event.metaKey === meta &&
      normalizeKey(event.key) === key
    );
  };

  /**
   * Handle keyboard event
   * @param {KeyboardEvent} event - Keyboard event
   */
  const handleKeyDown = (event) => {
    // Don't trigger shortcuts when typing in inputs, textareas, or contenteditable
    const target = event.target;
    if (
      target.tagName === 'INPUT' ||
      target.tagName === 'TEXTAREA' ||
      target.isContentEditable ||
      target.closest('[contenteditable="true"]')
    ) {
      // Allow escape and ctrl+k even in inputs
      if (event.key === 'Escape' || (event.ctrlKey && event.key === 'k') || (event.metaKey && event.key === 'k')) {
        // Continue processing
      } else {
        return;
      }
    }

    // Check all shortcuts
    for (const [keyCombo, handler] of Object.entries(mergedShortcuts)) {
      if (matchesKey(event, keyCombo)) {
        handler(event);
        break; // Only trigger first matching shortcut
      }
    }
  };

  /**
   * Register a new shortcut
   * @param {string} keyCombo - Key combination
   * @param {Function} handler - Handler function
   */
  const registerShortcut = (keyCombo, handler) => {
    mergedShortcuts[normalizeKey(keyCombo)] = handler;
  };

  /**
   * Unregister a shortcut
   * @param {string} keyCombo - Key combination to remove
   */
  const unregisterShortcut = (keyCombo) => {
    delete mergedShortcuts[normalizeKey(keyCombo)];
  };

  onMounted(() => {
    document.addEventListener('keydown', handleKeyDown);
  });

  onUnmounted(() => {
    document.removeEventListener('keydown', handleKeyDown);
  });

  return {
    registerShortcut,
    unregisterShortcut
  };
}

/**
 * Predefined shortcuts for admin panel
 * 
 * @type {Object<string, Function>}
 * @property {Function} 'ctrl+n' - Create new item (context-aware)
 * @property {Function} 'ctrl+s' - Save form (context-aware)
 */
export const adminShortcuts = {
  // Create new (context-aware)
  'ctrl+n': (e, context) => {
    e.preventDefault();
    if (context && context.onCreate) {
      context.onCreate();
    } else {
      // Default: find create button
      const createButton = document.querySelector('button:has-text("Thêm"), button:has-text("Tạo"), button:has-text("Create"), [aria-label*="create" i], [aria-label*="thêm" i]');
      if (createButton) {
        createButton.click();
      }
    }
  },
  // Save form
  'ctrl+s': (e, context) => {
    e.preventDefault();
    if (context && context.onSave) {
      context.onSave();
    } else {
      // Default: find save button
      const saveButton = document.querySelector('button[type="submit"]:not([disabled]), button:has-text("Lưu"), button:has-text("Save")');
      if (saveButton && !saveButton.disabled) {
        saveButton.click();
      }
    }
  }
};


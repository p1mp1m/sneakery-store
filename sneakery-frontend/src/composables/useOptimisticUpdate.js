/**
 * Composable for optimistic updates
 * Updates UI immediately before server response, then syncs with server result
 * 
 * @example
 * ```javascript
 * const { 
 *   optimisticUpdate,
 *   optimisticDelete,
 *   optimisticCreate,
 *   rollback 
 * } = useOptimisticUpdate(items, setItems);
 * 
 * // Update item optimistically
 * await optimisticUpdate(itemId, updateFn, asyncFn);
 * ```
 */

import { ref } from 'vue'
import notificationService from '@/utils/notificationService'

export function useOptimisticUpdate(items, setItems) {
  const rollbackStack = ref([])

  /**
   * Optimistically update an item in the list
   * @param {any} itemId - ID of item to update
   * @param {Function} optimisticFn - Function to update item optimistically (returns updated item)
   * @param {Function} serverFn - Async function to call server
   * @param {Object} options - Options
   * @param {boolean} options.showToast - Show success toast (default: true)
   * @param {string} options.successMessage - Success message
   * @param {string} options.errorMessage - Error message
   * @returns {Promise<any>} Server response
   */
  const optimisticUpdate = async (itemId, optimisticFn, serverFn, options = {}) => {
    const {
      showToast = true,
      successMessage = 'Cập nhật thành công',
      errorMessage = 'Cập nhật thất bại'
    } = options

    // Find original item
    const originalItem = items.value.find(item => item.id === itemId)
    if (!originalItem) {
      throw new Error(`Item with id ${itemId} not found`)
    }

    // Create optimistic update
    const optimisticItem = optimisticFn(originalItem)
    
    // Update UI immediately
    const index = items.value.findIndex(item => item.id === itemId)
    if (index !== -1) {
      items.value[index] = { ...items.value[index], ...optimisticItem }
    }

    // Save original for rollback
    rollbackStack.value.push({
      type: 'update',
      itemId,
      originalItem: { ...originalItem }
    })

    try {
      // Call server
      const result = await serverFn()
      
      // Update with server response (more accurate)
      if (result && index !== -1) {
        items.value[index] = result
      }

      if (showToast) {
        notificationService.success('Thành công', successMessage)
      }

      // Remove from rollback stack on success
      rollbackStack.value.pop()

      return result
    } catch (error) {
      // Rollback on error
      rollback(index, originalItem)
      
      if (showToast) {
        notificationService.error('Lỗi', errorMessage)
      }

      throw error
    }
  }

  /**
   * Optimistically delete an item from the list
   * @param {any} itemId - ID of item to delete
   * @param {Function} serverFn - Async function to call server
   * @param {Object} options - Options
   * @returns {Promise<any>} Server response
   */
  const optimisticDelete = async (itemId, serverFn, options = {}) => {
    const {
      showToast = true,
      successMessage = 'Xóa thành công',
      errorMessage = 'Xóa thất bại'
    } = options

    // Find original item
    const originalItem = items.value.find(item => item.id === itemId)
    if (!originalItem) {
      throw new Error(`Item with id ${itemId} not found`)
    }

    // Remove from UI immediately
    const index = items.value.findIndex(item => item.id === itemId)
    if (index !== -1) {
      items.value.splice(index, 1)
    }

    // Save original for rollback
    rollbackStack.value.push({
      type: 'delete',
      itemId,
      originalItem: { ...originalItem },
      index
    })

    try {
      // Call server
      const result = await serverFn()
      
      if (showToast) {
        notificationService.success('Thành công', successMessage)
      }

      // Remove from rollback stack on success
      rollbackStack.value.pop()

      return result
    } catch (error) {
      // Rollback on error
      if (index !== -1) {
        items.value.splice(index, 0, originalItem)
      }
      
      if (showToast) {
        notificationService.error('Lỗi', errorMessage)
      }

      throw error
    }
  }

  /**
   * Optimistically create an item in the list
   * @param {Function} optimisticFn - Function to create optimistic item (returns new item)
   * @param {Function} serverFn - Async function to call server
   * @param {Object} options - Options
   * @returns {Promise<any>} Server response
   */
  const optimisticCreate = async (optimisticFn, serverFn, options = {}) => {
    const {
      showToast = true,
      successMessage = 'Tạo thành công',
      errorMessage = 'Tạo thất bại',
      insertAt = 'start' // 'start' or 'end'
    } = options

    // Create optimistic item
    const optimisticItem = optimisticFn()
    const tempId = `temp_${Date.now()}` // Temporary ID
    
    // Add to UI immediately
    if (insertAt === 'start') {
      items.value.unshift({ ...optimisticItem, id: tempId })
    } else {
      items.value.push({ ...optimisticItem, id: tempId })
    }

    const index = insertAt === 'start' ? 0 : items.value.length - 1

    // Save for rollback
    rollbackStack.value.push({
      type: 'create',
      tempId,
      index
    })

    try {
      // Call server
      const result = await serverFn()
      
      // Replace optimistic item with server response
      if (result) {
        items.value[index] = result
      } else {
        // If server doesn't return item, remove optimistic one
        items.value.splice(index, 1)
      }

      if (showToast) {
        notificationService.success('Thành công', successMessage)
      }

      // Remove from rollback stack on success
      rollbackStack.value.pop()

      return result
    } catch (error) {
      // Rollback on error
      items.value.splice(index, 1)
      
      if (showToast) {
        notificationService.error('Lỗi', errorMessage)
      }

      throw error
    }
  }

  /**
   * Rollback last optimistic update
   */
  const rollback = (index, originalItem) => {
    if (index !== -1 && originalItem) {
      items.value[index] = originalItem
    }
  }

  /**
   * Rollback all optimistic updates
   */
  const rollbackAll = () => {
    while (rollbackStack.value.length > 0) {
      const action = rollbackStack.value.pop()
      
      if (action.type === 'update') {
        const index = items.value.findIndex(item => item.id === action.itemId)
        if (index !== -1) {
          items.value[index] = action.originalItem
        }
      } else if (action.type === 'delete') {
        items.value.splice(action.index, 0, action.originalItem)
      } else if (action.type === 'create') {
        const index = items.value.findIndex(item => item.id === action.tempId)
        if (index !== -1) {
          items.value.splice(index, 1)
        }
      }
    }
  }

  /**
   * Clear rollback stack
   */
  const clearRollbackStack = () => {
    rollbackStack.value = []
  }

  return {
    optimisticUpdate,
    optimisticDelete,
    optimisticCreate,
    rollback,
    rollbackAll,
    clearRollbackStack
  }
}


/**
 * Composable for confirmation dialogs
 * Provides a standardized way to show confirmation dialogs instead of using confirm()
 * 
 * @example
 * ```javascript
 * const { showDialog, dialogConfig, confirm, confirmDelete } = useConfirmDialog();
 * 
 * // Simple confirmation
 * const result = await confirm({
 *   title: 'Xác nhận',
 *   message: 'Bạn có chắc chắn?',
 *   onConfirm: () => { /* do something *\/ }
 * });
 * 
 * // Delete confirmation
 * await confirmDelete('Product Name', () => {
 *   deleteProduct();
 * });
 * ```
 * 
 * @returns {Object} Confirmation dialog utilities
 * @returns {Ref<boolean>} returns.showDialog - Whether dialog is visible (reactive)
 * @returns {Ref<Object>} returns.dialogConfig - Dialog configuration (reactive)
 * @returns {Function} returns.confirm - Show confirmation dialog
 * @returns {Function} returns.confirmDelete - Show delete confirmation
 * @returns {Function} returns.confirmBulkDelete - Show bulk delete confirmation
 * @returns {Function} returns.confirmUpdate - Show update confirmation
 */

import { ref } from 'vue';

export function useConfirmDialog() {
  const showDialog = ref(false);
  const dialogConfig = ref({
    title: '',
    message: '',
    description: '',
    type: 'danger', // 'danger', 'warning', 'info'
    confirmText: 'Xác nhận',
    cancelText: 'Hủy',
    onConfirm: null,
    onCancel: null
  });

  /**
   * Show confirmation dialog
   * @param {Object} config - Dialog configuration
   * @returns {Promise<boolean>} Promise that resolves to true if confirmed, false if cancelled
   */
  const confirm = (config) => {
    return new Promise((resolve) => {
      dialogConfig.value = {
        title: config.title || 'Xác nhận',
        message: config.message || '',
        description: config.description || '',
        type: config.type || 'danger',
        confirmText: config.confirmText || 'Xác nhận',
        cancelText: config.cancelText || 'Hủy',
        onConfirm: () => {
          showDialog.value = false;
          if (config.onConfirm) {
            config.onConfirm();
          }
          resolve(true);
        },
        onCancel: () => {
          showDialog.value = false;
          if (config.onCancel) {
            config.onCancel();
          }
          resolve(false);
        }
      };
      showDialog.value = true;
    });
  };

  /**
   * Show delete confirmation
   * @param {string} itemName - Name of item to delete
   * @param {Function} onConfirm - Callback when confirmed
   * @param {Function} onCancel - Callback when cancelled
   */
  const confirmDelete = (itemName, onConfirm, onCancel = null) => {
    return confirm({
      title: 'Xác nhận xóa',
      message: `Bạn có chắc chắn muốn xóa "${itemName}"?`,
      description: 'Hành động này không thể hoàn tác!',
      type: 'danger',
      confirmText: 'Xóa',
      cancelText: 'Hủy',
      onConfirm,
      onCancel
    });
  };

  /**
   * Show bulk delete confirmation
   * @param {number} count - Number of items to delete
   * @param {Function} onConfirm - Callback when confirmed
   * @param {Function} onCancel - Callback when cancelled
   */
  const confirmBulkDelete = (count, onConfirm, onCancel = null) => {
    return confirm({
      title: 'Xác nhận xóa hàng loạt',
      message: `Bạn có chắc chắn muốn xóa ${count} mục đã chọn?`,
      description: 'Hành động này không thể hoàn tác! Tất cả các mục đã chọn sẽ bị xóa vĩnh viễn.',
      type: 'danger',
      confirmText: 'Xóa tất cả',
      cancelText: 'Hủy',
      onConfirm,
      onCancel
    });
  };

  /**
   * Show update confirmation
   * @param {string} message - Confirmation message
   * @param {Function} onConfirm - Callback when confirmed
   * @param {Function} onCancel - Callback when cancelled
   */
  const confirmUpdate = (message, onConfirm, onCancel = null) => {
    return confirm({
      title: 'Xác nhận cập nhật',
      message,
      description: 'Bạn có chắc chắn muốn thực hiện thay đổi này?',
      type: 'warning',
      confirmText: 'Cập nhật',
      cancelText: 'Hủy',
      onConfirm,
      onCancel
    });
  };

  return {
    showDialog,
    dialogConfig,
    confirm,
    confirmDelete,
    confirmBulkDelete,
    confirmUpdate
  };
}


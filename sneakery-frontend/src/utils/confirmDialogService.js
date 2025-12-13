/**
 * Confirm Dialog Service
 * Service để hiển thị confirm dialog programmatically
 * Tương thích với API của ElMessageBox.confirm
 */

import { createApp, h, ref, watch } from 'vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'

class ConfirmDialogService {
  constructor() {
    this.container = null
  }

  /**
   * Tạo container cho dialog nếu chưa có
   */
  ensureContainer() {
    if (!this.container) {
      this.container = document.createElement('div')
      this.container.id = 'confirm-dialog-container'
      document.body.appendChild(this.container)
    }
  }

  /**
   * Xóa container
   */
  removeContainer() {
    if (this.container && this.container.parentNode) {
      this.container.parentNode.removeChild(this.container)
      this.container = null
    }
  }

  /**
   * Hiển thị confirm dialog
   * @param {string} message - Nội dung message
   * @param {string} title - Tiêu đề dialog
   * @param {object} options - Các tùy chọn
   * @returns {Promise} - Resolve khi confirm, reject khi cancel
   */
  confirm(message, title = 'Xác nhận', options = {}) {
    return new Promise((resolve, reject) => {
      this.ensureContainer()

      const {
        confirmButtonText = 'Đồng ý',
        cancelButtonText = 'Hủy',
        type = 'warning',
        closeOnClickModal = true,
        showCloseButton = true
      } = options

      let dialogApp = null
      let isResolved = false

      const handleConfirm = () => {
        if (!isResolved) {
          isResolved = true
          resolve('confirm')
          cleanup()
        }
      }

      const handleCancel = () => {
        if (!isResolved) {
          isResolved = true
          reject('cancel')
          cleanup()
        }
      }

      const cleanup = () => {
        if (dialogApp) {
          setTimeout(() => {
            dialogApp.unmount()
            dialogApp = null
            // Chỉ xóa container nếu không còn dialog nào
            if (this.container && this.container.children.length === 0) {
              this.removeContainer()
            }
          }, 200) // Đợi animation hoàn thành
        }
      }

      // Tạo Vue app với ConfirmDialog component
      dialogApp = createApp({
        setup() {
          const visible = ref(true)

          watch(visible, (newVal) => {
            if (!newVal) {
              handleCancel()
            }
          })

          return () =>
            h(ConfirmDialog, {
              visible: visible.value,
              title,
              message,
              confirmButtonText,
              cancelButtonText,
              type,
              closeOnClickModal,
              showCloseButton,
              onConfirm: handleConfirm,
              onCancel: handleCancel,
              'onUpdate:visible': (val) => {
                visible.value = val
              }
            })
        }
      })

      dialogApp.mount(this.container)
    })
  }
}

// Create singleton instance
const confirmDialogService = new ConfirmDialogService()

export default confirmDialogService


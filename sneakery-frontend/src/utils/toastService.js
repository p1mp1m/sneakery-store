/**
 * Toast Notification Service
 * Global toast notification system cho admin panel
 */

class ToastService {
  constructor() {
    this.toasts = []
    this.listeners = []
    this.maxToasts = 5
    this.defaultDuration = 5000
  }

  /**
   * Subscribe to toast changes
   */
  subscribe(listener) {
    this.listeners.push(listener)
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener)
    }
  }

  /**
   * Notify all listeners
   */
  notify() {
    this.listeners.forEach(listener => listener(this.toasts))
  }

  /**
   * Add a new toast
   */
  addToast(toast) {
    const id = Date.now() + Math.random()
    const newToast = {
      id,
      type: 'info',
      title: '',
      message: '',
      duration: this.defaultDuration,
      closable: true,
      ...toast
    }

    this.toasts.unshift(newToast)
    
    // Limit max toasts
    if (this.toasts.length > this.maxToasts) {
      this.toasts = this.toasts.slice(0, this.maxToasts)
    }

    this.notify()

    // Auto remove after duration
    if (newToast.duration > 0) {
      setTimeout(() => {
        this.removeToast(id)
      }, newToast.duration)
    }

    return id
  }

  /**
   * Remove toast by id
   */
  removeToast(id) {
    this.toasts = this.toasts.filter(toast => toast.id !== id)
    this.notify()
  }

  /**
   * Clear all toasts
   */
  clear() {
    this.toasts = []
    this.notify()
  }

  /**
   * Success toast
   */
  success(title, message = '', options = {}) {
    return this.addToast({
      type: 'success',
      title,
      message,
      ...options
    })
  }

  /**
   * Error toast
   */
  error(title, message = '', options = {}) {
    return this.addToast({
      type: 'error',
      title,
      message,
      duration: 8000, // Error toasts stay longer
      ...options
    })
  }

  /**
   * Warning toast
   */
  warning(title, message = '', options = {}) {
    return this.addToast({
      type: 'warning',
      title,
      message,
      ...options
    })
  }

  /**
   * Info toast
   */
  info(title, message = '', options = {}) {
    return this.addToast({
      type: 'info',
      title,
      message,
      ...options
    })
  }

  /**
   * API Error handler
   */
  apiError(error, defaultMessage = 'Đã xảy ra lỗi') {
    let title = 'Lỗi API'
    let message = defaultMessage

    if (error.response) {
      // Server responded with error status
      const status = error.response.status
      const data = error.response.data

      switch (status) {
        case 400:
          title = 'Dữ liệu không hợp lệ'
          message = data?.message || 'Vui lòng kiểm tra lại thông tin'
          break
        case 401:
          title = 'Chưa đăng nhập'
          message = 'Vui lòng đăng nhập lại'
          break
        case 403:
          title = 'Không có quyền truy cập'
          message = 'Bạn không có quyền thực hiện hành động này'
          break
        case 404:
          title = 'Không tìm thấy'
          message = data?.message || 'Dữ liệu không tồn tại'
          break
        case 422:
          title = 'Dữ liệu không hợp lệ'
          message = data?.message || 'Vui lòng kiểm tra lại thông tin'
          break
        case 500:
          title = 'Lỗi máy chủ'
          message = 'Máy chủ đang gặp sự cố, vui lòng thử lại sau'
          break
        default:
          title = `Lỗi ${status}`
          message = data?.message || defaultMessage
      }
    } else if (error.request) {
      // Network error
      title = 'Lỗi kết nối'
      message = 'Không thể kết nối đến máy chủ'
    } else {
      // Other error
      title = 'Lỗi không xác định'
      message = error.message || defaultMessage
    }

    return this.error(title, message)
  }
}

// Create singleton instance
const toastService = new ToastService()

export default toastService

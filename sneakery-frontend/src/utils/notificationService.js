/**
 * Notification Service
 * Global notification system cho Sneakery Store
 * Sử dụng CSS design tokens từ design system
 */

class NotificationService {
  constructor() {
    this.notifications = []
    this.listeners = []
    this.maxNotifications = 4
    this.defaultDuration = 4000
    this.timeouts = new Map() // Store timeout IDs for each notification
  }

  /**
   * Subscribe to notification changes
   */
  subscribe(listener) {
    this.listeners.push(listener)
    // Immediately call listener with current notifications
    listener(this.notifications)
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener)
    }
  }

  /**
   * Notify all listeners
   */
  notify() {
    this.listeners.forEach(listener => listener(this.notifications))
  }

  /**
   * Add a new notification
   */
  addNotification(notification) {
    const id = Date.now() + Math.random()
    const newNotification = {
      id,
      type: 'info',
      title: '',
      message: '',
      duration: this.defaultDuration,
      closable: true,
      action: null, // Single action button
      ...notification
    }

    // Add to beginning of array
    this.notifications.unshift(newNotification)
    
    // Limit max notifications
    if (this.notifications.length > this.maxNotifications) {
      // Remove the oldest notification
      const oldest = this.notifications.pop()
      if (oldest) {
        this.removeNotification(oldest.id)
      }
    }

    this.notify()

    // Auto remove after duration
    if (newNotification.duration > 0) {
      const timeoutId = setTimeout(() => {
        this.removeNotification(id)
      }, newNotification.duration)
      this.timeouts.set(id, timeoutId)
    }

    return id
  }

  /**
   * Remove notification by id
   */
  removeNotification(id) {
    // Clear timeout if exists
    const timeoutId = this.timeouts.get(id)
    if (timeoutId) {
      clearTimeout(timeoutId)
      this.timeouts.delete(id)
    }
    
    this.notifications = this.notifications.filter(notification => notification.id !== id)
    this.notify()
  }

  /**
   * Clear all notifications
   */
  clear() {
    // Clear all timeouts
    this.timeouts.forEach(timeoutId => clearTimeout(timeoutId))
    this.timeouts.clear()
    
    this.notifications = []
    this.notify()
  }

  /**
   * Success notification
   */
  success(title, message = '', options = {}) {
    return this.addNotification({
      type: 'success',
      title,
      message,
      ...options
    })
  }

  /**
   * Error notification
   */
  error(title, message = '', options = {}) {
    return this.addNotification({
      type: 'error',
      title,
      message,
      duration: 6000, // Error notifications stay longer
      ...options
    })
  }

  /**
   * Warning notification
   */
  warning(title, message = '', options = {}) {
    return this.addNotification({
      type: 'warning',
      title,
      message,
      ...options
    })
  }

  /**
   * Info notification
   */
  info(title, message = '', options = {}) {
    return this.addNotification({
      type: 'info',
      title,
      message,
      ...options
    })
  }

  /**
   * API Error handler
   * Hỗ trợ cả axios error object và formatted error object từ handleError
   */
  apiError(error, defaultMessage = 'Đã xảy ra lỗi') {
    let title = 'Lỗi API'
    let message = defaultMessage
    let status = null

    // Xử lý formatted error object từ handleError (có status và data)
    if (error.status && error.data) {
      status = error.status
      const data = error.data

      switch (status) {
        case 400:
          title = 'Dữ liệu không hợp lệ'
          message = data?.message || error.message || 'Vui lòng kiểm tra lại thông tin'
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
          message = data?.message || error.message || 'Dữ liệu không tồn tại'
          break
        case 409:
          title = 'Không thể thực hiện'
          message = data?.message || error.message || 'Dữ liệu đang được sử dụng ở nơi khác'
          break
        case 422:
          title = 'Dữ liệu không hợp lệ'
          message = data?.message || error.message || 'Vui lòng kiểm tra lại thông tin'
          break
        case 500:
          title = 'Lỗi máy chủ'
          message = 'Máy chủ đang gặp sự cố, vui lòng thử lại sau'
          break
        default:
          title = `Lỗi ${status}`
          message = data?.message || error.message || defaultMessage
      }
    } else if (error.response) {
      // Server responded with error status (axios error object)
      status = error.response.status
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
        case 409:
          title = 'Không thể thực hiện'
          message = data?.message || 'Dữ liệu đang được sử dụng ở nơi khác'
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
const notificationService = new NotificationService()

// Expose to window in development for testing
if (import.meta.env.DEV) {
  window.notificationService = notificationService
}

export default notificationService


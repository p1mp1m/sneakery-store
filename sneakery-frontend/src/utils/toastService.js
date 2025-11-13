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
    this.timeouts = new Map() // Store timeout IDs for each toast
    this.pausedToasts = new Set() // Track paused toasts
    this.pausedTimes = new Map() // Store remaining time when paused
    this.startTimes = new Map() // Store start time for each toast
    this.pauseStartTimes = new Map() // Store when pause started
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
      progress: 100, // Progress percentage (100% = full, 0% = empty)
      isPaused: false,
      actions: [], // Action buttons array (max 2)
      ...toast
    }

    // Limit actions to max 2
    if (newToast.actions && newToast.actions.length > 2) {
      newToast.actions = newToast.actions.slice(0, 2)
    }

    this.toasts.unshift(newToast)
    
    // Limit max toasts
    if (this.toasts.length > this.maxToasts) {
      this.toasts = this.toasts.slice(0, this.maxToasts)
    }

    this.notify()

    // Auto remove after duration
    if (newToast.duration > 0) {
      this.startProgress(id, newToast.duration)
    }

    return id
  }

  /**
   * Start progress animation for a toast
   */
  startProgress(id, duration) {
    const toast = this.toasts.find(t => t.id === id)
    if (!toast) return

    // Store start time
    this.startTimes.set(id, Date.now())
    
    const updateProgress = () => {
      if (this.pausedToasts.has(id)) {
        // Toast is paused, don't update progress
        return
      }

      const toast = this.toasts.find(t => t.id === id)
      if (!toast) return

      const startTime = this.startTimes.get(id)
      if (!startTime) return

      const now = Date.now()
      const elapsed = now - startTime
      const remaining = duration - elapsed
      
      if (remaining <= 0) {
        toast.progress = 0
        this.removeToast(id)
        return
      }

      toast.progress = (remaining / duration) * 100
      this.notify()

      // Continue animation
      const timeoutId = setTimeout(updateProgress, 16) // ~60fps
      this.timeouts.set(id, timeoutId)
    }

    // Start progress animation
    const timeoutId = setTimeout(updateProgress, 16)
    this.timeouts.set(id, timeoutId)
  }

  /**
   * Pause toast progress
   */
  pauseToast(id) {
    const toast = this.toasts.find(t => t.id === id)
    if (!toast || toast.duration <= 0) return

    if (!this.pausedToasts.has(id)) {
      this.pausedToasts.add(id)
      toast.isPaused = true
      
      // Clear current timeout
      const timeoutId = this.timeouts.get(id)
      if (timeoutId) {
        clearTimeout(timeoutId)
        this.timeouts.delete(id)
      }

      // Store pause start time
      this.pauseStartTimes.set(id, Date.now())
      
      this.notify()
    }
  }

  /**
   * Resume toast progress
   */
  resumeToast(id) {
    const toast = this.toasts.find(t => t.id === id)
    if (!toast || toast.duration <= 0) return

    if (this.pausedToasts.has(id)) {
      this.pausedToasts.delete(id)
      toast.isPaused = false
      
      // Adjust start time by adding pause duration
      const pauseStartTime = this.pauseStartTimes.get(id)
      if (pauseStartTime) {
        const pauseDuration = Date.now() - pauseStartTime
        const currentStartTime = this.startTimes.get(id)
        if (currentStartTime) {
          // Extend start time by pause duration so elapsed time doesn't include pause
          this.startTimes.set(id, currentStartTime + pauseDuration)
        }
        this.pauseStartTimes.delete(id)
      }
      
      // Continue progress animation
      const updateProgress = () => {
        if (this.pausedToasts.has(id)) {
          return
        }

        const toast = this.toasts.find(t => t.id === id)
        if (!toast) return

        const startTime = this.startTimes.get(id)
        if (!startTime) return

        const now = Date.now()
        const elapsed = now - startTime
        const remaining = toast.duration - elapsed
        
        if (remaining <= 0) {
          toast.progress = 0
          this.removeToast(id)
          return
        }

        toast.progress = (remaining / toast.duration) * 100
        this.notify()

        const timeoutId = setTimeout(updateProgress, 16)
        this.timeouts.set(id, timeoutId)
      }

      const timeoutId = setTimeout(updateProgress, 16)
      this.timeouts.set(id, timeoutId)
    }
  }

  /**
   * Remove toast by id
   */
  removeToast(id) {
    // Clear timeout if exists
    const timeoutId = this.timeouts.get(id)
    if (timeoutId) {
      clearTimeout(timeoutId)
      this.timeouts.delete(id)
    }
    
    // Clean up pause state
    this.pausedToasts.delete(id)
    this.pausedTimes.delete(id)
    this.startTimes.delete(id)
    this.pauseStartTimes.delete(id)
    
    this.toasts = this.toasts.filter(toast => toast.id !== id)
    this.notify()
  }

  /**
   * Clear all toasts
   */
  clear() {
    // Clear all timeouts
    this.timeouts.forEach(timeoutId => clearTimeout(timeoutId))
    this.timeouts.clear()
    this.pausedToasts.clear()
    this.pausedTimes.clear()
    this.startTimes.clear()
    this.pauseStartTimes.clear()
    
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
const toastService = new ToastService()

export default toastService

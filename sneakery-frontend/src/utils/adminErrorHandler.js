/**
 * Admin Error Handler Utilities
 * Standardize error handling cho admin features
 */

import notificationService from '@/utils/notificationService'

/**
 * Standard error response format từ backend
 * @typedef {Object} ErrorResponse
 * @property {string} message - Error message
 * @property {number} status - HTTP status code
 * @property {Object} data - Additional error data
 * @property {Array} errors - Validation errors (nếu có)
 */

/**
 * Parse error từ axios response
 * @param {Error} error - Axios error object
 * @returns {ErrorResponse} Standardized error response
 */
export function parseAdminError(error) {
  // Request cancelled
  if (error.isCancelled || error.message === 'Request cancelled') {
    return {
      message: 'Request cancelled',
      status: -2,
      isCancelled: true
    }
  }

  // Network error (no response)
  if (!error.response) {
    if (error.request) {
      return {
        message: 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.',
        status: 0,
        type: 'network'
      }
    }
    return {
      message: error.message || 'Có lỗi không xác định',
      status: -1,
      type: 'unknown'
    }
  }

  // Server responded with error
  const response = error.response
  const status = response.status
  const data = response.data || {}

  // Standardize error message
  let message = data.message || data.error || 'Có lỗi xảy ra từ server'

  // Handle validation errors
  if (status === 400 && data.errors) {
    const validationErrors = Array.isArray(data.errors) 
      ? data.errors 
      : Object.values(data.errors).flat()
    
    if (validationErrors.length > 0) {
      message = validationErrors[0] || message
    }
  }

  // Handle specific status codes
  switch (status) {
    case 400:
      message = message || 'Dữ liệu không hợp lệ'
      break
    case 401:
      message = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.'
      break
    case 403:
      message = 'Bạn không có quyền thực hiện thao tác này'
      break
    case 404:
      message = message || 'Không tìm thấy dữ liệu'
      break
    case 409:
      message = message || 'Dữ liệu đã tồn tại hoặc xung đột'
      break
    case 422:
      message = message || 'Dữ liệu không hợp lệ'
      break
    case 500:
      message = 'Lỗi server. Vui lòng thử lại sau.'
      break
    case 503:
      message = 'Service không khả dụng. Vui lòng thử lại sau.'
      break
    default:
      message = message || `Lỗi ${status}: ${response.statusText || 'Unknown error'}`
  }

  return {
    message,
    status,
    data,
    errors: data.errors || (data.error ? [data.error] : []),
    type: getErrorType(status)
  }
}

/**
 * Get error type từ status code
 * @param {number} status - HTTP status code
 * @returns {string} Error type
 */
function getErrorType(status) {
  if (status >= 400 && status < 500) {
    return 'client'
  }
  if (status >= 500) {
    return 'server'
  }
  return 'unknown'
}

/**
 * Extract validation errors từ error response
 * @param {ErrorResponse} errorResponse - Error response object
 * @returns {Object} Object với field names làm keys và error messages làm values
 */
export function extractValidationErrors(errorResponse) {
  if (!errorResponse.errors || !Array.isArray(errorResponse.errors)) {
    return {}
  }

  const fieldErrors = {}
  
  errorResponse.errors.forEach(error => {
    if (typeof error === 'string') {
      // Simple string error
      fieldErrors._general = error
    } else if (error.field && error.message) {
      // Field-specific error
      fieldErrors[error.field] = error.message
    } else if (error.defaultMessage) {
      // Spring validation error format
      const field = error.field || error.propertyPath || '_general'
      fieldErrors[field] = error.defaultMessage
    }
  })

  return fieldErrors
}

/**
 * Check if error is validation error
 * @param {ErrorResponse} errorResponse - Error response object
 * @returns {boolean} True if validation error
 */
export function isValidationError(errorResponse) {
  return errorResponse.status === 400 || errorResponse.status === 422
}

/**
 * Check if error is authentication error
 * @param {ErrorResponse} errorResponse - Error response object
 * @returns {boolean} True if auth error
 */
export function isAuthError(errorResponse) {
  return errorResponse.status === 401 || errorResponse.status === 403
}

/**
 * Check if error is network error
 * @param {ErrorResponse} errorResponse - Error response object
 * @returns {boolean} True if network error
 */
export function isNetworkError(errorResponse) {
  return errorResponse.status === 0 || errorResponse.type === 'network'
}

/**
 * Format error message for display
 * @param {ErrorResponse} errorResponse - Error response object
 * @param {string} defaultMessage - Default message if no message found
 * @returns {string} Formatted error message
 */
export function formatErrorMessage(errorResponse, defaultMessage = 'Có lỗi xảy ra') {
  if (!errorResponse) {
    return defaultMessage
  }

  if (errorResponse.message) {
    return errorResponse.message
  }

  if (errorResponse.errors && errorResponse.errors.length > 0) {
    return errorResponse.errors[0]
  }

  return defaultMessage
}

/**
 * Handle error và show notification
 * @param {Error} error - Error object
 * @param {Object} options - Options
 * @param {string} options.defaultMessage - Default error message
 * @param {boolean} options.showNotification - Whether to show notification (default: true)
 * @returns {ErrorResponse} Parsed error response
 */
export function handleAdminError(error, options = {}) {
  const {
    defaultMessage = 'Có lỗi xảy ra',
    showNotification = true
  } = options

  const errorResponse = parseAdminError(error)
  const message = formatErrorMessage(errorResponse, defaultMessage)

  if (showNotification) {
    // Show appropriate notification based on error type
    if (isAuthError(errorResponse)) {
      notificationService.error('Lỗi xác thực', message)
    } else if (isNetworkError(errorResponse)) {
      notificationService.warning('Lỗi kết nối', message)
    } else if (isValidationError(errorResponse)) {
      notificationService.warning('Lỗi xác thực', message)
    } else {
      notificationService.error('Lỗi', message)
    }
  }

  return errorResponse
}


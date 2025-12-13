/**
 * Composable for error handling
 * Provides consistent error handling and error message display
 * 
 * @example
 * ```javascript
 * const { handleError, error, clearError } = useErrorHandler();
 * 
 * try {
 *   await someAsyncOperation();
 * } catch (err) {
 *   handleError(err, { showToast: true, log: true });
 * }
 * ```
 * 
 * @returns {Object} Error handling utilities
 * @returns {Ref<Object|null>} returns.error - Current error object
 * @returns {Ref<Object>} returns.errors - Validation errors object
 * @returns {Object} returns.ErrorType - Error type constants
 * @returns {Function} returns.classifyError - Classify error type
 * @returns {Function} returns.getErrorMessage - Get user-friendly error message
 * @returns {Function} returns.handleError - Handle an error
 * @returns {Function} returns.handleValidationErrors - Handle validation errors
 * @returns {Function} returns.clearError - Clear current error
 * @returns {Function} returns.getFieldError - Get validation error for a field
 * @returns {Function} returns.hasFieldError - Check if field has validation error
 */

import { ref } from 'vue';
import notificationService from '@/utils/notificationService';
import logger from '@/utils/logger';

export function useErrorHandler() {
  const error = ref(null);
  const errors = ref({});

  /**
   * Error types
   */
  const ErrorType = {
    NETWORK: 'network',
    VALIDATION: 'validation',
    SERVER: 'server',
    UNAUTHORIZED: 'unauthorized',
    FORBIDDEN: 'forbidden',
    NOT_FOUND: 'not_found',
    UNKNOWN: 'unknown'
  };

  /**
   * Classify error type
   * @param {any} err - Error object
   * @returns {string} Error type
   */
  const classifyError = (err) => {
    if (err?.isCancelled) {
      return null; // Don't show cancelled request errors
    }

    if (err?.status === 0 || err?.message?.includes('Network') || err?.message?.includes('fetch')) {
      return ErrorType.NETWORK;
    }

    if (err?.status === 401) {
      return ErrorType.UNAUTHORIZED;
    }

    if (err?.status === 403) {
      return ErrorType.FORBIDDEN;
    }

    if (err?.status === 404) {
      return ErrorType.NOT_FOUND;
    }

    if (err?.status === 400 || err?.status === 422) {
      return ErrorType.VALIDATION;
    }

    if (err?.status >= 500) {
      return ErrorType.SERVER;
    }

    return ErrorType.UNKNOWN;
  };

  /**
   * Get user-friendly error message
   * @param {any} err - Error object
   * @returns {string} Error message
   */
  const getErrorMessage = (err) => {
    if (err?.isCancelled) {
      return null; // Don't show cancelled request errors
    }

    const errorType = classifyError(err);

    switch (errorType) {
      case ErrorType.NETWORK:
        return 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng của bạn.';
      
      case ErrorType.UNAUTHORIZED:
        return 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
      
      case ErrorType.FORBIDDEN:
        return 'Bạn không có quyền thực hiện thao tác này.';
      
      case ErrorType.NOT_FOUND:
        return 'Không tìm thấy tài nguyên yêu cầu.';
      
      case ErrorType.VALIDATION:
        if (err?.data?.validationErrors) {
          return 'Vui lòng kiểm tra lại thông tin đã nhập.';
        }
        return err?.message || 'Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.';
      
      case ErrorType.SERVER:
        return 'Lỗi server. Vui lòng thử lại sau hoặc liên hệ quản trị viên.';
      
      default:
        return err?.message || 'Có lỗi xảy ra. Vui lòng thử lại.';
    }
  };

  /**
   * Handle error
   * @param {any} err - Error object
   * @param {Object} options - Options { showToast: true, log: true, silent: false }
   */
  const handleError = (err, options = {}) => {
    const {
      showToast = true,
      log = true,
      silent = false,
      customMessage = null
    } = options;

    if (err?.isCancelled) {
      return; // Don't handle cancelled requests
    }

    const errorType = classifyError(err);
    const message = customMessage || getErrorMessage(err);

    error.value = {
      type: errorType,
      message,
      original: err,
      status: err?.status,
      data: err?.data
    };

    // Extract validation errors if present
    if (err?.data?.validationErrors) {
      errors.value = err.data.validationErrors;
    } else {
      errors.value = {};
    }

    if (log && !silent) {
      logger.error('Error handled:', {
        type: errorType,
        message,
        status: err?.status,
        data: err?.data
      });
    }

    if (showToast && !silent && message) {
      notificationService.apiError(err, message);
    }
  };

  /**
   * Handle validation errors
   * @param {Object} validationErrors - Validation errors object
   */
  const handleValidationErrors = (validationErrors) => {
    errors.value = validationErrors;
    error.value = {
      type: ErrorType.VALIDATION,
      message: 'Vui lòng kiểm tra lại thông tin đã nhập.',
      data: validationErrors
    };
  };

  /**
   * Clear error
   */
  const clearError = () => {
    error.value = null;
    errors.value = {};
  };

  /**
   * Get validation error for a field
   * @param {string} fieldName - Field name
   * @returns {string|null} Error message or null
   */
  const getFieldError = (fieldName) => {
    const fieldErrors = errors.value[fieldName];
    if (Array.isArray(fieldErrors) && fieldErrors.length > 0) {
      return fieldErrors[0];
    }
    if (typeof fieldErrors === 'string') {
      return fieldErrors;
    }
    return null;
  };

  /**
   * Check if field has error
   * @param {string} fieldName - Field name
   * @returns {boolean} True if field has error
   */
  const hasFieldError = (fieldName) => {
    return !!getFieldError(fieldName);
  };

  return {
    error,
    errors,
    ErrorType,
    classifyError,
    getErrorMessage,
    handleError,
    handleValidationErrors,
    clearError,
    getFieldError,
    hasFieldError
  };
}


/**
 * Composable for form validation
 * Provides reusable validation rules and validation logic
 * 
 * @example
 * ```javascript
 * const { validateField, validateForm, errors, isValid } = useFormValidation();
 * 
 * const rules = {
 *   name: [rules.required('Tên không được để trống'), rules.minLength(3)],
 *   email: [rules.required(), rules.email()]
 * };
 * 
 * const isValid = validateForm(formData, rules);
 * ```
 * 
 * @returns {Object} Validation utilities
 * @returns {Object} returns.errors - Reactive object containing field errors
 * @returns {Object} returns.touched - Reactive object tracking touched fields
 * @returns {Object} returns.rules - Available validation rules
 * @returns {Function} returns.validateField - Validate a single field
 * @returns {Function} returns.validateForm - Validate entire form
 * @returns {Function} returns.touchField - Mark field as touched
 * @returns {Function} returns.touchAll - Mark all fields as touched
 * @returns {Function} returns.reset - Reset validation state
 * @returns {Function} returns.getError - Get error for a field
 * @returns {Function} returns.hasError - Check if field has error
 * @returns {Function} returns.isTouched - Check if field is touched
 * @returns {ComputedRef<boolean>} returns.isValid - Computed property indicating if form is valid
 */

import { ref, computed } from 'vue';

export function useFormValidation() {
  const errors = ref({});
  const touched = ref({});

  /**
   * Validation rules
   */
  const rules = {
    required: (value, message = 'Trường này không được để trống') => {
      if (value === null || value === undefined || value === '') {
        return message;
      }
      if (typeof value === 'string' && value.trim() === '') {
        return message;
      }
      if (Array.isArray(value) && value.length === 0) {
        return message;
      }
      return null;
    },

    minLength: (min, message) => (value) => {
      if (!value) return null;
      const length = typeof value === 'string' ? value.trim().length : value.length;
      if (length < min) {
        return message || `Phải có ít nhất ${min} ký tự`;
      }
      return null;
    },

    maxLength: (max, message) => (value) => {
      if (!value) return null;
      const length = typeof value === 'string' ? value.trim().length : value.length;
      if (length > max) {
        return message || `Không được vượt quá ${max} ký tự`;
      }
      return null;
    },

    email: (value, message = 'Email không hợp lệ') => {
      if (!value) return null;
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(value)) {
        return message;
      }
      return null;
    },

    phone: (value, message = 'Số điện thoại không hợp lệ') => {
      if (!value) return null;
      const phoneRegex = /^[0-9]{10,11}$/;
      if (!phoneRegex.test(value.replace(/\s+/g, ''))) {
        return message;
      }
      return null;
    },

    url: (value, message = 'URL không hợp lệ') => {
      if (!value) return null;
      try {
        new URL(value);
        return null;
      } catch {
        return message;
      }
    },

    number: (value, message = 'Phải là số') => {
      if (value === null || value === undefined || value === '') return null;
      if (isNaN(Number(value))) {
        return message;
      }
      return null;
    },

    min: (min, message) => (value) => {
      if (value === null || value === undefined || value === '') return null;
      const num = Number(value);
      if (isNaN(num) || num < min) {
        return message || `Giá trị phải lớn hơn hoặc bằng ${min}`;
      }
      return null;
    },

    max: (max, message) => (value) => {
      if (value === null || value === undefined || value === '') return null;
      const num = Number(value);
      if (isNaN(num) || num > max) {
        return message || `Giá trị phải nhỏ hơn hoặc bằng ${max}`;
      }
      return null;
    },

    pattern: (regex, message) => (value) => {
      if (!value) return null;
      if (!regex.test(value)) {
        return message || 'Giá trị không đúng định dạng';
      }
      return null;
    },

    slug: (value, message = 'Slug chỉ được chứa chữ thường, số và dấu gạch ngang') => {
      if (!value) return null;
      const slugRegex = /^[a-z0-9-]+$/;
      if (!slugRegex.test(value)) {
        return message;
      }
      return null;
    },

    unique: (checkFn, message) => async (value) => {
      if (!value) return null;
      if (typeof checkFn !== 'function') return null;
      
      try {
        const isUnique = await checkFn(value);
        if (!isUnique) {
          return message || 'Giá trị này đã tồn tại';
        }
        return null;
      } catch (error) {
        // If async check fails, don't block validation
        console.error('Async validation error:', error);
        return null;
      }
    },

    custom: (validatorFn, message) => (value) => {
      if (!value) return null;
      if (typeof validatorFn !== 'function') return null;
      
      try {
        const isValid = validatorFn(value);
        if (!isValid) {
          return message || 'Giá trị không hợp lệ';
        }
        return null;
      } catch (error) {
        console.error('Custom validation error:', error);
        return message || 'Giá trị không hợp lệ';
      }
    }
  };

  /**
   * Validate a single field (supports async validation)
   * @param {string} fieldName - Field name
   * @param {any} value - Field value
   * @param {Array} fieldRules - Array of validation rules
   * @returns {Promise<string|null>|string|null} Error message or null if valid
   */
  const validateField = async (fieldName, value, fieldRules = []) => {
    if (!Array.isArray(fieldRules)) {
      fieldRules = [fieldRules];
    }

    for (const rule of fieldRules) {
      let error = null;

      if (typeof rule === 'function') {
        // Check if it's an async function
        const result = rule(value);
        if (result instanceof Promise) {
          error = await result;
        } else {
          error = result;
        }
      } else if (typeof rule === 'object' && rule !== null) {
        // Rule object: { type: 'required', message: '...', params: ... }
        const ruleFn = rules[rule.type];
        if (ruleFn) {
          if (typeof ruleFn === 'function') {
            const result = ruleFn(value, rule.message);
            if (result instanceof Promise) {
              error = await result;
            } else {
              error = result;
            }
          } else {
            // Rule with parameters (like min, max)
            const result = ruleFn(rule.params)(value, rule.message);
            if (result instanceof Promise) {
              error = await result;
            } else {
              error = result;
            }
          }
        }
      } else if (typeof rule === 'string') {
        // Simple rule name
        const ruleFn = rules[rule];
        if (ruleFn) {
          const result = ruleFn(value);
          if (result instanceof Promise) {
            error = await result;
          } else {
            error = result;
          }
        }
      }

      if (error) {
        errors.value[fieldName] = error;
        return error;
      }
    }

    // No errors
    delete errors.value[fieldName];
    return null;
  };

  /**
   * Validate entire form (supports async validation)
   * @param {Object} formData - Form data object
   * @param {Object} validationSchema - Validation schema { fieldName: [rules] }
   * @returns {Promise<boolean>} True if form is valid
   */
  const validateForm = async (formData, validationSchema) => {
    errors.value = {};
    let isValid = true;

    // Validate all fields (can be async)
    const validationPromises = Object.entries(validationSchema).map(async ([fieldName, fieldRules]) => {
      const value = formData[fieldName];
      const error = await validateField(fieldName, value, fieldRules);
      if (error) {
        isValid = false;
      }
      return { fieldName, error };
    });

    await Promise.all(validationPromises);

    return isValid;
  };

  /**
   * Mark field as touched
   * @param {string} fieldName - Field name
   */
  const touchField = (fieldName) => {
    touched.value[fieldName] = true;
  };

  /**
   * Mark all fields as touched
   */
  const touchAll = () => {
    Object.keys(errors.value).forEach(fieldName => {
      touched.value[fieldName] = true;
    });
  };

  /**
   * Reset validation state
   */
  const reset = () => {
    errors.value = {};
    touched.value = {};
  };

  /**
   * Get error for a field
   * @param {string} fieldName - Field name
   * @returns {string|null} Error message or null
   */
  const getError = (fieldName) => {
    return errors.value[fieldName] || null;
  };

  /**
   * Check if field has error
   * @param {string} fieldName - Field name
   * @returns {boolean} True if field has error
   */
  const hasError = (fieldName) => {
    return !!errors.value[fieldName];
  };

  /**
   * Check if field is touched
   * @param {string} fieldName - Field name
   * @returns {boolean} True if field is touched
   */
  const isTouched = (fieldName) => {
    return !!touched.value[fieldName];
  };

  /**
   * Check if form is valid
   * @returns {boolean} True if no errors
   */
  const isValid = computed(() => {
    return Object.keys(errors.value).length === 0;
  });

  /**
   * Validate field with backend (async)
   * Useful for checking uniqueness, etc.
   * @param {string} fieldName - Field name
   * @param {any} value - Field value
   * @param {Function} checkFn - Async function that returns true if valid, false if invalid
   * @param {string} errorMessage - Error message if validation fails
   * @returns {Promise<void>}
   */
  const validateFieldAsync = async (fieldName, value, checkFn, errorMessage = 'Giá trị không hợp lệ') => {
    if (!value) {
      delete errors.value[fieldName];
      return;
    }

    try {
      const isValid = await checkFn(value);
      if (!isValid) {
        errors.value[fieldName] = errorMessage;
      } else {
        delete errors.value[fieldName];
      }
    } catch (error) {
      console.error('Async validation error:', error);
      // Don't set error on network failure - let user submit and see server error
      delete errors.value[fieldName];
    }
  };

  /**
   * Clear validation errors for a specific field
   * @param {string} fieldName - Field name
   */
  const clearFieldError = (fieldName) => {
    delete errors.value[fieldName];
  };

  return {
    errors,
    touched,
    rules,
    validateField,
    validateForm,
    validateFieldAsync,
    touchField,
    touchAll,
    reset,
    getError,
    hasError,
    isTouched,
    isValid,
    clearFieldError
  };
}


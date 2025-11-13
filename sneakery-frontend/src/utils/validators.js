/**
 * Validation utilities
 * Common validation functions for forms
 */

/**
 * Validate Vietnamese phone number
 * Format: 10-11 digits, starting with 0 or +84
 * Examples: 0901234567, 0912345678, +84901234567
 * @param {string} phone - Phone number to validate
 * @returns {boolean} True if valid, false otherwise
 */
export function validateVietnamesePhone(phone) {
  if (!phone || typeof phone !== 'string') return false;
  
  // Remove spaces and dashes
  const cleaned = phone.trim().replace(/[\s-]/g, '');
  
  // Pattern: 10-11 digits starting with 0, or +84 followed by 9-10 digits
  const phoneRegex = /^(0[3|5|7|8|9][0-9]{8}|(\+84)[3|5|7|8|9][0-9]{8})$/;
  
  return phoneRegex.test(cleaned);
}

/**
 * Validate email format
 * @param {string} email - Email to validate
 * @returns {boolean} True if valid, false otherwise
 */
export function validateEmail(email) {
  if (!email || typeof email !== 'string') return false;
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email.trim());
}

/**
 * Validate password strength
 * Requirements:
 * - At least 6 characters
 * - At least 1 uppercase letter
 * - At least 1 lowercase letter
 * - At least 1 number
 * @param {string} password - Password to validate
 * @returns {Object} { valid: boolean, strength: string, errors: string[] }
 */
export function validatePasswordStrength(password) {
  const errors = [];
  let strength = 'weak';
  
  if (!password || password.length < 6) {
    errors.push('Mật khẩu phải có ít nhất 6 ký tự');
    return { valid: false, strength: 'weak', errors };
  }
  
  if (password.length < 8) {
    errors.push('Mật khẩu nên có ít nhất 8 ký tự');
  }
  
  if (!/[A-Z]/.test(password)) {
    errors.push('Mật khẩu phải có ít nhất 1 chữ hoa');
  }
  
  if (!/[a-z]/.test(password)) {
    errors.push('Mật khẩu phải có ít nhất 1 chữ thường');
  }
  
  if (!/[0-9]/.test(password)) {
    errors.push('Mật khẩu phải có ít nhất 1 số');
  }
  
  // Calculate strength
  let score = 0;
  if (password.length >= 8) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/[a-z]/.test(password)) score++;
  if (/[0-9]/.test(password)) score++;
  if (/[^A-Za-z0-9]/.test(password)) score++;
  
  if (score >= 4) strength = 'strong';
  else if (score >= 3) strength = 'medium';
  else strength = 'weak';
  
  return {
    valid: errors.length === 0,
    strength,
    errors
  };
}

/**
 * Validate address format
 * Checks if address is not empty and not just whitespace
 * @param {string} address - Address to validate
 * @returns {boolean} True if valid, false otherwise
 */
export function validateAddress(address) {
  if (!address || typeof address !== 'string') return false;
  return address.trim().length > 0;
}

/**
 * Format phone number for display
 * Converts +84 to 0 format
 * @param {string} phone - Phone number to format
 * @returns {string} Formatted phone number
 */
export function formatPhoneNumber(phone) {
  if (!phone) return '';
  
  // Remove spaces and dashes
  let cleaned = phone.trim().replace(/[\s-]/g, '');
  
  // Convert +84 to 0
  if (cleaned.startsWith('+84')) {
    cleaned = '0' + cleaned.substring(3);
  }
  
  return cleaned;
}


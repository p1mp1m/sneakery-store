/**
 * Sanitization Utilities
 * Functions to sanitize user input and prevent XSS attacks
 */

/**
 * Escape HTML special characters to prevent XSS
 * @param {string} text - Text to escape
 * @returns {string} - Escaped text
 */
export function escapeHtml(text) {
  if (!text) return '';
  
  const map = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;'
  };
  
  return String(text).replace(/[&<>"']/g, (char) => map[char]);
}

/**
 * Sanitize text for safe HTML rendering
 * Only allows safe HTML tags (if needed in future)
 * For now, just escapes all HTML
 * @param {string} text - Text to sanitize
 * @returns {string} - Sanitized text
 */
export function sanitizeHtml(text) {
  if (!text) return '';
  
  // For now, just escape HTML
  // In future, can use DOMPurify or similar library for more advanced sanitization
  return escapeHtml(text);
}

/**
 * Highlight search term in text safely
 * Escapes the text first, then highlights the search term
 * @param {string} text - Text to highlight in
 * @param {string} searchTerm - Search term to highlight (optional)
 * @returns {string} - HTML string with highlighted search term
 */
export function highlightSearchSafely(text, searchTerm = '') {
  if (!text) return '';
  
  // Escape the text first
  const escapedText = escapeHtml(text);
  
  // If no search term, just return escaped text
  if (!searchTerm || !searchTerm.trim()) {
    return escapedText;
  }
  
  // Escape the search term
  const escapedSearchTerm = escapeHtml(searchTerm.trim());
  
  // Create regex from escaped search term (escape regex special chars)
  const regex = new RegExp(`(${escapedSearchTerm.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
  
  // Replace with highlighted version
  return escapedText.replace(regex, '<mark class="search-highlight">$1</mark>');
}


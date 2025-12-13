import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import logger from '@/utils/logger';

/**
 * Theme Store - Quản lý theme cho toàn bộ ứng dụng
 * 
 * Hỗ trợ 3 modes:
 * - 'light': Light theme
 * - 'dark': Dark theme  
 * - 'system': Auto-detect từ system preference
 * 
 * Theme được lưu vào localStorage với key 'sneakery-theme'
 * Sync giữa admin và user qua cùng localStorage key
 */
export const useThemeStore = defineStore('theme', () => {
  // === STATE ===
  const STORAGE_KEY = 'sneakery-theme';
  
  // Lấy theme từ localStorage hoặc default 'dark'
  const getStoredTheme = () => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored && ['light', 'dark', 'system'].includes(stored)) {
        return stored;
      }
    } catch (error) {
      logger.warn('⚠️ Cannot access localStorage:', error);
    }
    return 'dark'; // Default to dark mode
  };

  const theme = ref(getStoredTheme());

  // === GETTERS ===
  
  /**
   * Detect system preference (dark or light)
   * @returns {string} 'dark' | 'light'
   */
  const getSystemPreference = () => {
    if (typeof window === 'undefined') return 'light';
    
    try {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      return prefersDark ? 'dark' : 'light';
    } catch (error) {
      logger.warn('⚠️ Cannot detect system preference:', error);
      return 'light'; // Fallback
    }
  };

  /**
   * Computed current theme (resolves 'system' to actual theme)
   * @returns {string} 'light' | 'dark'
   */
  const currentTheme = computed(() => {
    if (theme.value === 'system') {
      return getSystemPreference();
    }
    return theme.value;
  });

  /**
   * Check if current theme is dark
   * @returns {boolean}
   */
  const isDark = computed(() => currentTheme.value === 'dark');

  /**
   * Check if current theme is light
   * @returns {boolean}
   */
  const isLight = computed(() => currentTheme.value === 'light');

  // === ACTIONS ===

  /**
   * Set theme và persist vào localStorage
   * @param {string} newTheme - 'light' | 'dark' | 'system'
   */
  function setTheme(newTheme) {
    if (!['light', 'dark', 'system'].includes(newTheme)) {
      logger.error('❌ Invalid theme:', newTheme);
      return;
    }

    theme.value = newTheme;
    
    // Persist to localStorage
    try {
      localStorage.setItem(STORAGE_KEY, newTheme);
    } catch (error) {
      logger.warn('⚠️ Cannot save theme to localStorage:', error);
      // Continue without persistence (in-memory only)
    }

    // Apply theme class to document
    applyThemeClass(currentTheme.value);
  }

  /**
   * Toggle giữa light và dark (không ảnh hưởng 'system')
   * Nếu đang ở 'system', sẽ set explicit 'light' hoặc 'dark'
   */
  function toggleTheme() {
    const resolved = currentTheme.value;
    const newTheme = resolved === 'dark' ? 'light' : 'dark';
    setTheme(newTheme);
  }

  /**
   * Apply theme class to <html> element for Tailwind CSS
   * @param {string} themeToApply - 'light' | 'dark'
   */
  function applyThemeClass(themeToApply) {
    if (typeof document === 'undefined') return;
    
    const html = document.documentElement;
    html.setAttribute('data-theme', themeToApply);
    
    // For Tailwind CSS: use 'dark' class instead of 'dark-mode'
    if (themeToApply === 'dark') {
      html.classList.add('dark');
    } else {
      html.classList.remove('dark');
    }
  }

  /**
   * Initialize theme khi app start
   * Đọc từ localStorage hoặc system preference
   */
  function initTheme() {
    const stored = getStoredTheme();
    theme.value = stored;
    
    // Apply theme class ngay lập tức
    applyThemeClass(currentTheme.value);
    
    // Watch system preference changes nếu theme = 'system'
    if (theme.value === 'system') {
      watchSystemPreference();
    }
  }

  /**
   * Watch system preference changes và auto-update khi theme = 'system'
   */
  function watchSystemPreference() {
    if (typeof window === 'undefined') return;
    
    try {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      
      // Handler function
      const handleChange = (e) => {
        if (theme.value === 'system') {
          const newTheme = e.matches ? 'dark' : 'light';
          applyThemeClass(newTheme);
        }
      };
      
      // Modern browsers
      if (mediaQuery.addEventListener) {
        mediaQuery.addEventListener('change', handleChange);
      } else {
        // Fallback for older browsers
        mediaQuery.addListener(handleChange);
      }
    } catch (error) {
      logger.warn('⚠️ Cannot watch system preference:', error);
    }
  }

  // === EXPORTS ===
  return {
    // State
    theme,
    
    // Getters
    currentTheme,
    isDark,
    isLight,
    
    // Actions
    setTheme,
    toggleTheme,
    initTheme,
    applyThemeClass,
    getSystemPreference
  };
});


import { computed, watch, onMounted, onUnmounted } from 'vue';
import { useThemeStore } from '@/stores/theme';

/**
 * Theme Composable - Reactive theme management
 * 
 * Wrapper around theme store để dễ sử dụng trong components
 * 
 * @returns {object} Theme utilities và reactive state
 */
export function useTheme() {
  const themeStore = useThemeStore();

  // === Reactive State ===
  const theme = computed(() => themeStore.theme);
  const currentTheme = computed(() => themeStore.currentTheme);
  const isDark = computed(() => themeStore.isDark);
  const isLight = computed(() => themeStore.isLight);

  // === Methods ===

  /**
   * Set theme
   * @param {string} newTheme - 'light' | 'dark' | 'system'
   */
  const setTheme = (newTheme) => {
    themeStore.setTheme(newTheme);
    
    // Watch system preference nếu set to 'system'
    if (newTheme === 'system') {
      watchSystemPreference();
    }
  };

  /**
   * Toggle giữa light và dark
   */
  const toggleTheme = () => {
    themeStore.toggleTheme();
  };

  /**
   * Get current theme
   * @returns {string} 'light' | 'dark'
   */
  const getTheme = () => {
    return themeStore.currentTheme;
  };

  /**
   * Watch system preference changes
   */
  let mediaQuery = null;
  let mediaQueryHandler = null;

  const watchSystemPreference = () => {
    if (typeof window === 'undefined') return;

    // Cleanup existing listener
    if (mediaQuery && mediaQueryHandler) {
      if (mediaQuery.removeEventListener) {
        mediaQuery.removeEventListener('change', mediaQueryHandler);
      } else {
        mediaQuery.removeListener(mediaQueryHandler);
      }
    }

    try {
      mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      
      mediaQueryHandler = (e) => {
        if (themeStore.theme === 'system') {
          const newTheme = e.matches ? 'dark' : 'light';
          themeStore.applyThemeClass(newTheme);
        }
      };

      // Add listener
      if (mediaQuery.addEventListener) {
        mediaQuery.addEventListener('change', mediaQueryHandler);
      } else {
        mediaQuery.addListener(mediaQueryHandler);
      }
    } catch (error) {
      console.warn('⚠️ Cannot watch system preference:', error);
    }
  };

  // Watch theme changes để apply class
  watch(
    () => themeStore.currentTheme,
    (newTheme) => {
      themeStore.applyThemeClass(newTheme);
    },
    { immediate: true }
  );

  // Watch theme mode changes để setup/cleanup system preference watcher
  watch(
    () => themeStore.theme,
    (newMode) => {
      if (newMode === 'system') {
        watchSystemPreference();
      } else {
        // Cleanup system preference watcher
        if (mediaQuery && mediaQueryHandler) {
          if (mediaQuery.removeEventListener) {
            mediaQuery.removeEventListener('change', mediaQueryHandler);
          } else {
            mediaQuery.removeListener(mediaQueryHandler);
          }
        }
      }
    }
  );

  // Cleanup on unmount
  onUnmounted(() => {
    if (mediaQuery && mediaQueryHandler) {
      if (mediaQuery.removeEventListener) {
        mediaQuery.removeEventListener('change', mediaQueryHandler);
      } else {
        mediaQuery.removeListener(mediaQueryHandler);
      }
    }
  });

  // Setup system preference watcher on mount if needed
  onMounted(() => {
    if (themeStore.theme === 'system') {
      watchSystemPreference();
    }
  });

  return {
    // Reactive state
    theme,
    currentTheme,
    isDark,
    isLight,

    // Methods
    setTheme,
    toggleTheme,
    getTheme
  };
}


/**
 * Composable for localStorage caching with TTL (Time To Live)
 * Provides methods to cache data with expiration time
 * 
 * @example
 * ```javascript
 * const { getCache, setCache, hasCache } = useLocalStorageCache();
 * 
 * // Cache data for 5 minutes
 * setCache('user_data', userData, 5 * 60 * 1000);
 * 
 * // Retrieve cached data
 * const cached = getCache('user_data');
 * 
 * // Check if cache exists
 * if (hasCache('user_data')) {
 *   // Use cached data
 * }
 * ```
 * 
 * @returns {Object} Cache utilities
 * @returns {Function} returns.getCache - Get cached data if not expired
 * @returns {Function} returns.setCache - Set cache with TTL
 * @returns {Function} returns.removeCache - Remove specific cache
 * @returns {Function} returns.clearExpiredCaches - Clear all expired caches
 * @returns {Function} returns.clearAllCaches - Clear all caches
 * @returns {Function} returns.hasCache - Check if cache exists and is valid
 */

export function useLocalStorageCache() {
  const CACHE_PREFIX = 'sneakery_cache_';
  const DEFAULT_TTL = 5 * 60 * 1000; // 5 minutes in milliseconds

  /**
   * Get cached data if not expired
   * @param {string} key - Cache key
   * @returns {any|null} Cached data or null if expired/not found
   */
  const getCache = (key) => {
    try {
      const cacheKey = CACHE_PREFIX + key;
      const cached = localStorage.getItem(cacheKey);
      
      if (!cached) return null;
      
      const { data, timestamp, ttl } = JSON.parse(cached);
      const now = Date.now();
      const expirationTime = timestamp + (ttl || DEFAULT_TTL);
      
      // Check if cache is expired
      if (now > expirationTime) {
        // Remove expired cache
        localStorage.removeItem(cacheKey);
        return null;
      }
      
      return data;
    } catch (error) {
      console.error('Error reading cache:', error);
      return null;
    }
  };

  /**
   * Set cache with TTL
   * @param {string} key - Cache key
   * @param {any} data - Data to cache
   * @param {number} ttl - Time to live in milliseconds (default: 5 minutes)
   */
  const setCache = (key, data, ttl = DEFAULT_TTL) => {
    try {
      const cacheKey = CACHE_PREFIX + key;
      const cacheData = {
        data,
        timestamp: Date.now(),
        ttl
      };
      localStorage.setItem(cacheKey, JSON.stringify(cacheData));
    } catch (error) {
      console.error('Error setting cache:', error);
      // If storage is full, try to clear old caches
      if (error.name === 'QuotaExceededError') {
        clearExpiredCaches();
        try {
          localStorage.setItem(cacheKey, JSON.stringify(cacheData));
        } catch (retryError) {
          console.error('Failed to set cache after cleanup:', retryError);
        }
      }
    }
  };

  /**
   * Remove specific cache
   * @param {string} key - Cache key
   */
  const removeCache = (key) => {
    try {
      const cacheKey = CACHE_PREFIX + key;
      localStorage.removeItem(cacheKey);
    } catch (error) {
      console.error('Error removing cache:', error);
    }
  };

  /**
   * Clear all expired caches
   */
  const clearExpiredCaches = () => {
    try {
      const now = Date.now();
      const keysToRemove = [];
      
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.startsWith(CACHE_PREFIX)) {
          try {
            const cached = JSON.parse(localStorage.getItem(key));
            const expirationTime = cached.timestamp + (cached.ttl || DEFAULT_TTL);
            if (now > expirationTime) {
              keysToRemove.push(key);
            }
          } catch (e) {
            // Invalid cache entry, remove it
            keysToRemove.push(key);
          }
        }
      }
      
      keysToRemove.forEach(key => localStorage.removeItem(key));
    } catch (error) {
      console.error('Error clearing expired caches:', error);
    }
  };

  /**
   * Clear all caches (expired and valid)
   */
  const clearAllCaches = () => {
    try {
      const keysToRemove = [];
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.startsWith(CACHE_PREFIX)) {
          keysToRemove.push(key);
        }
      }
      keysToRemove.forEach(key => localStorage.removeItem(key));
    } catch (error) {
      console.error('Error clearing all caches:', error);
    }
  };

  /**
   * Check if cache exists and is valid
   * @param {string} key - Cache key
   * @returns {boolean} True if cache exists and is valid
   */
  const hasCache = (key) => {
    return getCache(key) !== null;
  };

  return {
    getCache,
    setCache,
    removeCache,
    clearExpiredCaches,
    clearAllCaches,
    hasCache
  };
}


/**
 * Composable for infinite scroll pagination
 * Handles loading more data when user scrolls to bottom
 * 
 * @example
 * ```javascript
 * const { isLoading, hasMore, reset } = useInfiniteScroll(
 *   async () => {
 *     const data = await fetchMoreData();
 *     return { hasMore: data.hasMore };
 *   },
 *   { threshold: 200, root: scrollContainerRef }
 * );
 * ```
 * 
 * @param {Function} loadMore - Async function to load more data
 * @param {Object} options - Configuration options
 * @param {number} options.threshold - Distance from bottom to trigger load in pixels (default: 200)
 * @param {boolean} options.enabled - Whether infinite scroll is enabled (default: true)
 * @param {HTMLElement|null} options.root - Scroll container element (null = window)
 * @param {string} options.rootMargin - Root margin for Intersection Observer
 * 
 * @returns {Object} Infinite scroll utilities
 * @returns {Ref<boolean>} returns.isLoading - Whether data is being loaded (reactive)
 * @returns {Ref<boolean>} returns.hasMore - Whether more data is available (reactive)
 * @returns {Function} returns.enable - Enable infinite scroll
 * @returns {Function} returns.disable - Disable infinite scroll
 * @returns {Function} returns.reset - Reset scroll state
 * @returns {Function} returns.setHasMore - Set hasMore state
 */

import { ref, onMounted, onUnmounted } from 'vue';

export function useInfiniteScroll(loadMore, options = {}) {
  const {
    threshold = 200, // Distance from bottom to trigger load (px)
    enabled = true,
    root = null, // Scroll container (null = window)
    rootMargin = '0px'
  } = options;

  const isLoading = ref(false);
  const hasMore = ref(true);
  const isInitialized = ref(false);

  const handleScroll = async () => {
    if (!enabled || isLoading.value || !hasMore.value) return;

    const scrollContainer = root || window;
    const scrollElement = root || document.documentElement;
    
    let scrollTop, scrollHeight, clientHeight;
    
    if (root) {
      scrollTop = scrollElement.scrollTop;
      scrollHeight = scrollElement.scrollHeight;
      clientHeight = scrollElement.clientHeight;
    } else {
      scrollTop = window.pageYOffset || document.documentElement.scrollTop;
      scrollHeight = document.documentElement.scrollHeight;
      clientHeight = window.innerHeight;
    }

    const distanceFromBottom = scrollHeight - (scrollTop + clientHeight);

    if (distanceFromBottom <= threshold) {
      isLoading.value = true;
      try {
        const result = await loadMore();
        // If loadMore returns false or hasMore property, update hasMore
        if (result === false || (result && result.hasMore === false)) {
          hasMore.value = false;
        } else if (result && result.hasMore !== undefined) {
          hasMore.value = result.hasMore;
        }
      } catch (error) {
        console.error('Error loading more data:', error);
      } finally {
        isLoading.value = false;
      }
    }
  };

  const enable = () => {
    if (!isInitialized.value) {
      const scrollContainer = root || window;
      scrollContainer.addEventListener('scroll', handleScroll);
      isInitialized.value = true;
    }
  };

  const disable = () => {
    if (isInitialized.value) {
      const scrollContainer = root || window;
      scrollContainer.removeEventListener('scroll', handleScroll);
      isInitialized.value = false;
    }
  };

  const reset = () => {
    hasMore.value = true;
    isLoading.value = false;
  };

  onMounted(() => {
    if (enabled) {
      enable();
    }
  });

  onUnmounted(() => {
    disable();
  });

  return {
    isLoading,
    hasMore,
    enable,
    disable,
    reset,
    setHasMore: (value) => { hasMore.value = value; }
  };
}


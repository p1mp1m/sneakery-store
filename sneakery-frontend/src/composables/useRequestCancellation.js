/**
 * Composable for request cancellation
 * Helps cancel ongoing requests when component unmounts or new request is made
 * 
 * @example
 * ```javascript
 * const { createCancelToken, cancelAllRequests } = useRequestCancellation();
 * 
 * const source = createCancelToken();
 * try {
 *   const response = await axios.get('/api/data', {
 *     cancelToken: source.token
 *   });
 * } catch (error) {
 *   if (axios.isCancel(error)) {
 *     console.log('Request cancelled');
 *   }
 * }
 * ```
 * 
 * @returns {Object} Request cancellation utilities
 * @returns {Function} returns.createCancelToken - Create a new cancel token source
 * @returns {Function} returns.cancelAllRequests - Cancel all pending requests
 * @returns {Function} returns.cancelRequest - Cancel a specific request
 * @returns {Function} returns.removeCancelToken - Remove cancel token from list
 */

import { ref, onUnmounted } from 'vue';
import axios from 'axios';

export function useRequestCancellation() {
  const cancelTokens = ref([]);

  /**
   * Create a cancel token for a request
   * @returns {CancelTokenSource} Axios cancel token source
   */
  const createCancelToken = () => {
    const source = axios.CancelToken.source();
    cancelTokens.value.push(source);
    return source;
  };

  /**
   * Cancel all pending requests
   */
  const cancelAllRequests = () => {
    cancelTokens.value.forEach(source => {
      if (source) {
        source.cancel('Request cancelled by component unmount');
      }
    });
    cancelTokens.value = [];
  };

  /**
   * Cancel a specific request
   * @param {CancelTokenSource} source - Cancel token source to cancel
   */
  const cancelRequest = (source) => {
    if (source) {
      source.cancel('Request cancelled');
      const index = cancelTokens.value.indexOf(source);
      if (index > -1) {
        cancelTokens.value.splice(index, 1);
      }
    }
  };

  /**
   * Remove a cancel token from the list (after request completes)
   * @param {CancelTokenSource} source - Cancel token source to remove
   */
  const removeCancelToken = (source) => {
    const index = cancelTokens.value.indexOf(source);
    if (index > -1) {
      cancelTokens.value.splice(index, 1);
    }
  };

  // Auto cleanup on unmount
  onUnmounted(() => {
    cancelAllRequests();
  });

  return {
    createCancelToken,
    cancelAllRequests,
    cancelRequest,
    removeCancelToken
  };
}


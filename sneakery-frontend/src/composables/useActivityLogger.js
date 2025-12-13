/**
 * Composable for logging admin activities
 * Automatically logs important actions to the activity log system
 * 
 * @example
 * ```javascript
 * const { logProductCreate, logProductUpdate, logProductDelete } = useActivityLogger();
 * 
 * // After creating a product
 * await logProductCreate(productId, productData);
 * 
 * // After updating a product
 * await logProductUpdate(productId, oldData, newData);
 * ```
 * 
 * @returns {Object} Activity logging utilities
 * @returns {Ref<boolean>} returns.isLogging - Whether logging is in progress (reactive)
 * @returns {Function} returns.logActivity - Log a generic activity
 * @returns {Function} returns.logProductCreate - Log product creation
 * @returns {Function} returns.logProductUpdate - Log product update
 * @returns {Function} returns.logProductDelete - Log product deletion
 * @returns {Function} returns.logBulkProductOperation - Log bulk product operations
 * @returns {Function} returns.logOrderStatusChange - Log order status change
 * @returns {Function} returns.logOrderCancel - Log order cancellation
 * @returns {Function} returns.logBulkOrderOperation - Log bulk order operations
 * @returns {Function} returns.logUserOperation - Log user operations
 * @returns {Function} returns.logExport - Log export operations
 * @returns {Function} returns.logImport - Log import operations
 * @returns {Function} returns.logAuth - Log authentication events
 */

import { ref } from 'vue';
import adminService from '@/services/adminService';
import logger from '@/utils/logger';
import { useAuthStore } from '@/stores/auth';

export function useActivityLogger() {
  const isLogging = ref(false);

  /**
   * Get current user info for logging
   */
  const getCurrentUser = () => {
    try {
      const authStore = useAuthStore();
      return authStore.user || null;
    } catch (error) {
      console.warn('Failed to get auth store:', error);
      return null;
    }
  };

  /**
   * Get client info (IP, user agent)
   */
  const getClientInfo = () => {
    return {
      ipAddress: null, // Will be captured by backend
      userAgent: navigator.userAgent || null
    };
  };

  /**
   * Log an activity
   * @param {string} action - Action type (CREATE, UPDATE, DELETE, etc.)
   * @param {string} entityType - Entity type (Product, Order, User, etc.)
   * @param {number|null} entityId - Entity ID
   * @param {object|null} oldValue - Old value (for updates)
   * @param {object|null} newValue - New value
   * @param {object} options - Additional options
   */
  const logActivity = async (action, entityType, entityId = null, oldValue = null, newValue = null, options = {}) => {
    if (isLogging.value && !options.force) {
      logger.warn('Activity logging already in progress, skipping...');
      return;
    }

    try {
      isLogging.value = true;
      const user = getCurrentUser();
      
      if (!user) {
        logger.warn('Cannot log activity: No user found');
        return;
      }

      const clientInfo = getClientInfo();
      
      const logData = {
        action,
        entityType,
        entityId,
        oldValue: oldValue ? JSON.stringify(oldValue) : null,
        newValue: newValue ? JSON.stringify(newValue) : null,
        userAgent: clientInfo.userAgent,
        ...options.metadata
      };

      // Call backend API to log activity
      // Note: Backend should handle IP address capture
      await adminService.logActivity(logData);
      
      if (options.silent !== true) {
        logger.log(`✅ Activity logged: ${action} ${entityType}${entityId ? ` #${entityId}` : ''}`);
      }
    } catch (error) {
      // Don't throw error - logging should not break the main flow
      logger.error('Failed to log activity:', error);
    } finally {
      isLogging.value = false;
    }
  };

  /**
   * Log product creation
   */
  const logProductCreate = async (productId, productData) => {
    await logActivity('CREATE', 'Product', productId, null, productData, {
      metadata: {
        productName: productData.name,
        productCode: productData.code
      }
    });
  };

  /**
   * Log product update
   */
  const logProductUpdate = async (productId, oldData, newData) => {
    await logActivity('UPDATE', 'Product', productId, oldData, newData, {
      metadata: {
        productName: newData.name || oldData.name,
        productCode: newData.code || oldData.code
      }
    });
  };

  /**
   * Log product deletion
   */
  const logProductDelete = async (productId, productData) => {
    await logActivity('DELETE', 'Product', productId, productData, null, {
      metadata: {
        productName: productData.name,
        productCode: productData.code
      }
    });
  };

  /**
   * Log bulk product operations
   */
  const logBulkProductOperation = async (action, count, productIds) => {
    await logActivity(`BULK_${action}`, 'Product', null, null, { count, productIds }, {
      metadata: {
        count,
        productIds: Array.isArray(productIds) ? productIds.join(',') : String(productIds)
      }
    });
  };

  /**
   * Log order status change
   */
  const logOrderStatusChange = async (orderId, oldStatus, newStatus, orderData = null) => {
    await logActivity('UPDATE', 'Order', orderId, { status: oldStatus }, { status: newStatus }, {
      metadata: {
        orderId,
        oldStatus,
        newStatus,
        orderTotal: orderData?.total || null
      }
    });
  };

  /**
   * Log order cancellation
   */
  const logOrderCancel = async (orderId, orderData) => {
    await logActivity('CANCEL', 'Order', orderId, orderData, { status: 'Cancelled' }, {
      metadata: {
        orderId,
        orderTotal: orderData?.total || null
      }
    });
  };

  /**
   * Log bulk order operations
   */
  const logBulkOrderOperation = async (action, count, orderIds, status = null) => {
    await logActivity(`BULK_${action}`, 'Order', null, null, { count, orderIds, status }, {
      metadata: {
        count,
        orderIds: Array.isArray(orderIds) ? orderIds.join(',') : String(orderIds),
        status
      }
    });
  };

  /**
   * Log user operations
   */
  const logUserOperation = async (action, userId, userData = null, oldData = null) => {
    await logActivity(action, 'User', userId, oldData, userData, {
      metadata: {
        userEmail: userData?.email || oldData?.email,
        userName: userData?.fullName || oldData?.fullName
      }
    });
  };

  /**
   * Log export operations
   */
  const logExport = async (entityType, format, filters = {}) => {
    await logActivity('EXPORT', entityType, null, null, { format, filters }, {
      metadata: {
        format,
        entityType
      },
      silent: true // Export is usually not critical to log prominently
    });
  };

  /**
   * Log import operations
   */
  const logImport = async (entityType, count, successCount, errorCount) => {
    await logActivity('IMPORT', entityType, null, null, { count, successCount, errorCount }, {
      metadata: {
        count,
        successCount,
        errorCount
      }
    });
  };

  /**
   * Log login/logout
   */
  const logAuth = async (action, userId, userEmail) => {
    await logActivity(action, 'Auth', userId, null, { email: userEmail }, {
      metadata: {
        userEmail
      }
    });
  };

  return {
    isLogging,
    logActivity,
    logProductCreate,
    logProductUpdate,
    logProductDelete,
    logBulkProductOperation,
    logOrderStatusChange,
    logOrderCancel,
    logBulkOrderOperation,
    logUserOperation,
    logExport,
    logImport,
    logAuth
  };
}


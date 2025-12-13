/**
 * Composable for permission-based access control
 * Provides utilities to check user permissions for specific actions
 * 
 * @example
 * ```javascript
 * const { hasPermission, hasRole, isAdmin, PERMISSIONS } = usePermissions();
 * 
 * // Check permission
 * if (hasPermission(PERMISSIONS.PRODUCT_CREATE)) {
 *   // Show create button
 * }
 * 
 * // Check role
 * if (isAdmin.value) {
 *   // Show admin features
 * }
 * ```
 * 
 * @returns {Object} Permission utilities
 * @returns {ComputedRef<string>} returns.userRole - Current user role (reactive)
 * @returns {ComputedRef<string[]>} returns.userPermissions - Current user permissions (reactive)
 * @returns {Function} returns.hasPermission - Check if user has a specific permission
 * @returns {Function} returns.hasAnyPermission - Check if user has any of the specified permissions
 * @returns {Function} returns.hasAllPermissions - Check if user has all of the specified permissions
 * @returns {Function} returns.hasRole - Check if user has a specific role
 * @returns {ComputedRef<boolean>} returns.isAdmin - Whether user is admin (reactive)
 * @returns {ComputedRef<boolean>} returns.isManagerOrAdmin - Whether user is manager or admin (reactive)
 * @returns {Function} returns.can - Check permission for action and resource
 * @returns {Object} returns.PERMISSIONS - Permission constants
 */

import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';

// Permission definitions
export const PERMISSIONS = {
  // Product permissions
  PRODUCT_CREATE: 'product:create',
  PRODUCT_UPDATE: 'product:update',
  PRODUCT_DELETE: 'product:delete',
  PRODUCT_VIEW: 'product:view',
  PRODUCT_EXPORT: 'product:export',
  PRODUCT_IMPORT: 'product:import',
  PRODUCT_BULK_UPDATE: 'product:bulk:update',
  PRODUCT_BULK_DELETE: 'product:bulk:delete',
  
  // Order permissions
  ORDER_VIEW: 'order:view',
  ORDER_UPDATE: 'order:update',
  ORDER_CANCEL: 'order:cancel',
  ORDER_DELETE: 'order:delete',
  ORDER_EXPORT: 'order:export',
  ORDER_BULK_UPDATE: 'order:bulk:update',
  
  // User permissions
  USER_VIEW: 'user:view',
  USER_CREATE: 'user:create',
  USER_UPDATE: 'user:update',
  USER_DELETE: 'user:delete',
  USER_BULK_UPDATE: 'user:bulk:update',
  
  // Category permissions
  CATEGORY_CREATE: 'category:create',
  CATEGORY_UPDATE: 'category:update',
  CATEGORY_DELETE: 'category:delete',
  
  // Brand permissions
  BRAND_CREATE: 'brand:create',
  BRAND_UPDATE: 'brand:update',
  BRAND_DELETE: 'brand:delete',
  
  // Settings permissions
  SETTINGS_VIEW: 'settings:view',
  SETTINGS_UPDATE: 'settings:update',
  
  // Activity logs
  ACTIVITY_LOGS_VIEW: 'activity:view',
  ACTIVITY_LOGS_DELETE: 'activity:delete',
  
  // Reports
  REPORTS_VIEW: 'reports:view',
  REPORTS_EXPORT: 'reports:export',
};

// Role-based permission mapping
// This can be replaced with backend permission system later
const ROLE_PERMISSIONS = {
  ADMIN: [
    // All permissions for admin
    ...Object.values(PERMISSIONS)
  ],
  MANAGER: [
    // Manager has most permissions except sensitive ones
    PERMISSIONS.PRODUCT_VIEW,
    PERMISSIONS.PRODUCT_CREATE,
    PERMISSIONS.PRODUCT_UPDATE,
    PERMISSIONS.PRODUCT_EXPORT,
    PERMISSIONS.PRODUCT_IMPORT,
    PERMISSIONS.PRODUCT_BULK_UPDATE,
    PERMISSIONS.ORDER_VIEW,
    PERMISSIONS.ORDER_UPDATE,
    PERMISSIONS.ORDER_CANCEL,
    PERMISSIONS.ORDER_EXPORT,
    PERMISSIONS.ORDER_BULK_UPDATE,
    PERMISSIONS.USER_VIEW,
    PERMISSIONS.CATEGORY_CREATE,
    PERMISSIONS.CATEGORY_UPDATE,
    PERMISSIONS.BRAND_CREATE,
    PERMISSIONS.BRAND_UPDATE,
    PERMISSIONS.ACTIVITY_LOGS_VIEW,
    PERMISSIONS.REPORTS_VIEW,
    PERMISSIONS.REPORTS_EXPORT,
  ],
  STAFF: [
    // Staff has limited permissions
    PERMISSIONS.PRODUCT_VIEW,
    PERMISSIONS.PRODUCT_UPDATE,
    PERMISSIONS.ORDER_VIEW,
    PERMISSIONS.ORDER_UPDATE,
    PERMISSIONS.ORDER_CANCEL,
    PERMISSIONS.USER_VIEW,
    PERMISSIONS.ACTIVITY_LOGS_VIEW,
  ],
  USER: [
    // Regular users have no admin permissions
  ]
};

export function usePermissions() {
  const authStore = useAuthStore();
  
  /**
   * Get user's role
   */
  const userRole = computed(() => {
    return authStore.userRole || 'USER';
  });
  
  /**
   * Get user's permissions based on role
   * In a real system, this would come from backend
   */
  const userPermissions = computed(() => {
    const role = userRole.value?.toUpperCase() || 'USER';
    return ROLE_PERMISSIONS[role] || [];
  });
  
  /**
   * Check if user has a specific permission
   * @param {string} permission - Permission to check
   * @returns {boolean}
   */
  const hasPermission = (permission) => {
    if (!permission) return false;
    return userPermissions.value.includes(permission);
  };
  
  /**
   * Check if user has any of the specified permissions
   * @param {string[]} permissions - Array of permissions to check
   * @returns {boolean}
   */
  const hasAnyPermission = (permissions) => {
    if (!permissions || permissions.length === 0) return false;
    return permissions.some(permission => hasPermission(permission));
  };
  
  /**
   * Check if user has all of the specified permissions
   * @param {string[]} permissions - Array of permissions to check
   * @returns {boolean}
   */
  const hasAllPermissions = (permissions) => {
    if (!permissions || permissions.length === 0) return false;
    return permissions.every(permission => hasPermission(permission));
  };
  
  /**
   * Check if user has a specific role
   * @param {string|string[]} roles - Role(s) to check
   * @returns {boolean}
   */
  const hasRole = (roles) => {
    const currentRole = userRole.value?.toUpperCase();
    if (Array.isArray(roles)) {
      return roles.some(role => role.toUpperCase() === currentRole);
    }
    return roles.toUpperCase() === currentRole;
  };
  
  /**
   * Check if user is admin
   */
  const isAdmin = computed(() => hasRole('ADMIN'));
  
  /**
   * Check if user is manager or admin
   */
  const isManagerOrAdmin = computed(() => hasAnyPermission([PERMISSIONS.PRODUCT_CREATE, PERMISSIONS.ORDER_UPDATE]));
  
  /**
   * Get permission check function for a specific action
   * Useful for creating reusable permission checks
   */
  const can = (action, resource) => {
    const permission = `${resource}:${action}`;
    return hasPermission(permission);
  };
  
  return {
    userRole,
    userPermissions,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    hasRole,
    isAdmin,
    isManagerOrAdmin,
    can,
    PERMISSIONS
  };
}


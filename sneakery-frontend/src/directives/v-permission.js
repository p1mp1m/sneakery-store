/**
 * Vue directive for permission-based element visibility
 * Usage: v-permission="'product:create'" or v-permission="['product:create', 'product:update']"
 */

import { usePermissions } from '@/composables/usePermissions';

export default {
  mounted(el, binding) {
    const { hasPermission, hasAnyPermission } = usePermissions();
    
    const value = binding.value;
    
    let hasAccess = false;
    
    if (Array.isArray(value)) {
      // If array, check if user has any of the permissions
      hasAccess = hasAnyPermission(value);
    } else if (typeof value === 'string') {
      // If string, check single permission
      hasAccess = hasPermission(value);
    } else if (typeof value === 'object' && value !== null) {
      // If object, check based on mode
      if (value.mode === 'all') {
        hasAccess = hasAllPermissions(value.permissions);
      } else {
        hasAccess = hasAnyPermission(value.permissions || []);
      }
    }
    
    if (!hasAccess) {
      el.style.display = 'none';
      // Store original display style to restore later
      el._originalDisplay = el.style.display;
    }
  },
  
  updated(el, binding) {
    // Re-check permissions when binding updates
    const { hasPermission, hasAnyPermission } = usePermissions();
    
    const value = binding.value;
    let hasAccess = false;
    
    if (Array.isArray(value)) {
      hasAccess = hasAnyPermission(value);
    } else if (typeof value === 'string') {
      hasAccess = hasPermission(value);
    } else if (typeof value === 'object' && value !== null) {
      if (value.mode === 'all') {
        hasAccess = hasAllPermissions(value.permissions);
      } else {
        hasAccess = hasAnyPermission(value.permissions || []);
      }
    }
    
    if (hasAccess) {
      el.style.display = el._originalDisplay || '';
    } else {
      el.style.display = 'none';
    }
  }
};


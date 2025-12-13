/**
 * Coupon Store - Pinia
 * Quản lý state của mã giảm giá (đồng bộ giữa Cart và Checkout)
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import logger from '@/utils/logger';

const COUPON_STORAGE_KEY = 'sneakery_applied_coupon';

export const useCouponStore = defineStore('coupon', () => {
  // State
  const couponCode = ref('');
  const appliedCoupon = ref(null);
  const couponError = ref('');
  
  // Computed
  const hasCoupon = computed(() => !!appliedCoupon.value);
  const couponDiscountAmount = computed(() => {
    if (!appliedCoupon.value) return 0;
    // This will be calculated in components based on subtotal
    return 0;
  });
  
  // Actions
  
  /**
   * Load coupon from localStorage
   */
  const loadCoupon = () => {
    try {
      const stored = localStorage.getItem(COUPON_STORAGE_KEY);
      if (stored) {
        const data = JSON.parse(stored);
        couponCode.value = data.code || '';
        appliedCoupon.value = data.coupon || null;
        logger.log('Coupon loaded from storage:', couponCode.value);
      }
    } catch (error) {
      logger.error('Error loading coupon from storage:', error);
      clearCoupon();
    }
  };
  
  /**
   * Save coupon to localStorage
   */
  const saveCoupon = () => {
    try {
      if (appliedCoupon.value) {
        const data = {
          code: couponCode.value,
          coupon: appliedCoupon.value,
          timestamp: Date.now()
        };
        localStorage.setItem(COUPON_STORAGE_KEY, JSON.stringify(data));
        logger.log('Coupon saved to storage:', couponCode.value);
      } else {
        localStorage.removeItem(COUPON_STORAGE_KEY);
      }
    } catch (error) {
      logger.error('Error saving coupon to storage:', error);
    }
  };
  
  /**
   * Set applied coupon
   */
  const setCoupon = (code, coupon) => {
    couponCode.value = code;
    appliedCoupon.value = coupon;
    couponError.value = '';
    saveCoupon();
    logger.log('Coupon set:', code);
  };
  
  /**
   * Clear coupon
   */
  const clearCoupon = () => {
    couponCode.value = '';
    appliedCoupon.value = null;
    couponError.value = '';
    localStorage.removeItem(COUPON_STORAGE_KEY);
    logger.log('Coupon cleared');
  };
  
  /**
   * Set coupon error
   */
  const setError = (error) => {
    couponError.value = error;
    logger.error('Coupon error:', error);
  };
  
  /**
   * Initialize - load from storage
   */
  const init = () => {
    loadCoupon();
  };
  
  return {
    // State
    couponCode,
    appliedCoupon,
    couponError,
    
    // Computed
    hasCoupon,
    couponDiscountAmount,
    
    // Actions
    setCoupon,
    clearCoupon,
    setError,
    loadCoupon,
    saveCoupon,
    init
  };
});


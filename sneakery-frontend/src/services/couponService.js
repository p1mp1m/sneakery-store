import axios from 'axios';
import { API_ENDPOINTS, buildApiUrl } from '@/config/api';
import logger from '@/utils/logger';

class CouponService {
    /**
     * Validate coupon code
     * @param {string} code - Coupon code to validate
     * @param {number} subtotal - Subtotal amount to check minOrderAmount
     * @returns {Promise<Object>} Coupon information if valid
     */
    async validateCoupon(code, subtotal = 0) {
        try {
            const response = await axios.get(
                buildApiUrl(API_ENDPOINTS.ORDERS.VALIDATE_COUPON(code))
            );
            const coupon = response.data;
            
            // Check minOrderAmount if provided
            if (subtotal > 0 && coupon.minOrderAmount) {
                if (subtotal < coupon.minOrderAmount) {
                    throw new Error(
                        `Đơn hàng tối thiểu ${this.formatCurrency(coupon.minOrderAmount)} để áp dụng mã giảm giá`
                    );
                }
            }
            
            return coupon;
        } catch (error) {
            logger.error('Error validating coupon:', error);
            
            if (error.response?.status === 404) {
                throw new Error('Mã giảm giá không tồn tại');
            } else if (error.response?.status === 400) {
                throw new Error(error.response?.data?.message || 'Mã giảm giá không hợp lệ hoặc đã hết hạn');
            } else if (error.message) {
                throw error; // Re-throw custom error messages
            } else {
                throw new Error('Không thể validate mã giảm giá');
            }
        }
    }

    /**
     * Calculate discount amount from coupon
     * @param {Object} coupon - Coupon object
     * @param {number} subtotal - Subtotal amount
     * @returns {number} Discount amount
     */
    calculateDiscount(coupon, subtotal) {
        if (!coupon || !subtotal) return 0;
        
        let discount = 0;
        
        if (coupon.discountType === 'percent') {
            // Percentage discount
            discount = subtotal * (coupon.value / 100);
            
            // Apply maxDiscountAmount if exists
            if (coupon.maxDiscountAmount && discount > coupon.maxDiscountAmount) {
                discount = coupon.maxDiscountAmount;
            }
        } else if (coupon.discountType === 'fixed') {
            // Fixed amount discount
            discount = coupon.value;
            
            // Ensure discount doesn't exceed subtotal
            if (discount > subtotal) {
                discount = subtotal;
            }
        }
        
        return Math.max(0, discount);
    }

    /**
     * Get list of active coupons
     * @returns {Promise<Array>} List of active coupons
     */
    async getActiveCoupons() {
        try {
            const response = await axios.get(
                buildApiUrl(API_ENDPOINTS.ORDERS.ACTIVE_COUPONS)
            );
            return response.data || [];
        } catch (error) {
            logger.error('Error fetching active coupons:', error);
            // Return empty array if error (don't block user flow)
            return [];
        }
    }

    /**
     * Format currency
     * @param {number} amount - Amount to format
     * @returns {string} Formatted currency string
     */
    formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND',
        }).format(amount);
    }
}

export default new CouponService();


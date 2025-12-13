/**
 * Flash Sale Store (Pinia)
 * State management cho flash sales
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import flashSaleService from '@/services/flashSaleService';
import logger from '@/utils/logger';

export const useFlashSaleStore = defineStore('flashSale', () => {
    // State
    const activeFlashSales = ref([]);
    const loading = ref(false);
    const error = ref(null);

    // Computed
    const hasActiveFlashSales = computed(() => activeFlashSales.value.length > 0);

    /**
     * Fetch active flash sales
     */
    const fetchActiveFlashSales = async () => {
        loading.value = true;
        error.value = null;

        try {
            const data = await flashSaleService.getActiveFlashSales();
            activeFlashSales.value = Array.isArray(data) ? data : [];
            logger.log(`Fetched ${activeFlashSales.value.length} active flash sales`);
            if (activeFlashSales.value.length > 0) {
                logger.log('Flash sales data:', activeFlashSales.value);
            } else {
                logger.warn('No active flash sales found. Check if flash sales exist and are within time range.');
            }
        } catch (err) {
            error.value = err.response?.data?.message || 'Không thể tải flash sales';
            logger.error('Error fetching flash sales:', err);
            activeFlashSales.value = [];
        } finally {
            loading.value = false;
        }
    };

    /**
     * Get flash sale by product ID
     */
    const getFlashSaleByProductId = async (productId) => {
        try {
            return await flashSaleService.getFlashSaleByProductId(productId);
        } catch (err) {
            logger.error(`Error fetching flash sale for product ${productId}:`, err);
            return null;
        }
    };

    /**
     * Check if product has active flash sale
     */
    const hasFlashSale = (productId) => {
        return activeFlashSales.value.some(fs => fs.productId === productId);
    };

    /**
     * Get flash sale for product from active list
     */
    const getFlashSaleForProduct = (productId) => {
        return activeFlashSales.value.find(fs => fs.productId === productId);
    };

    /**
     * Calculate discounted price
     */
    const calculateDiscountedPrice = (originalPrice, discountPercent) => {
        // Convert discountPercent to number if it's a string or BigDecimal
        const discount = typeof discountPercent === 'number' 
            ? discountPercent 
            : parseFloat(discountPercent) || 0;
        return originalPrice * (1 - discount / 100);
    };

    /**
     * Clear error
     */
    const clearError = () => {
        error.value = null;
    };

    /**
     * Reset store
     */
    const reset = () => {
        activeFlashSales.value = [];
        loading.value = false;
        error.value = null;
    };

    return {
        // State
        activeFlashSales,
        loading,
        error,

        // Computed
        hasActiveFlashSales,

        // Actions
        fetchActiveFlashSales,
        getFlashSaleByProductId,
        hasFlashSale,
        getFlashSaleForProduct,
        calculateDiscountedPrice,
        clearError,
        reset
    };
});


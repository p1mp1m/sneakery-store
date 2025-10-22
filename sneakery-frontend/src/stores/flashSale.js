/**
 * Flash Sale Store (Pinia)
 * State management cho flash sales
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import flashSaleService from '@/services/flashSaleService';

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
            activeFlashSales.value = data;
            console.log(`✅ Fetched ${data.length} active flash sales`);
        } catch (err) {
            error.value = err.response?.data?.message || 'Không thể tải flash sales';
            console.error('❌ Error fetching flash sales:', err);
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
            console.error(`❌ Error fetching flash sale for product ${productId}:`, err);
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
        return originalPrice * (1 - discountPercent / 100);
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


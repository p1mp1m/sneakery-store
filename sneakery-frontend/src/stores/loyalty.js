/**
 * Loyalty Points Store (Pinia)
 * State management cho loyalty points
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import loyaltyService from '@/services/loyaltyService';

export const useLoyaltyStore = defineStore('loyalty', () => {
    // State
    const balance = ref(0);
    const valueVnd = ref(0);
    const history = ref([]);
    const loading = ref(false);
    const error = ref(null);

    // Computed
    const hasPoints = computed(() => balance.value > 0);
    
    const formattedBalance = computed(() => {
        return balance.value.toLocaleString('vi-VN');
    });

    const formattedValue = computed(() => {
        return valueVnd.value.toLocaleString('vi-VN');
    });

    /**
     * Fetch loyalty balance
     */
    const fetchBalance = async () => {
        loading.value = true;
        error.value = null;

        try {
            const data = await loyaltyService.getBalance();
            balance.value = data.balance;
            valueVnd.value = data.valueVnd;
            console.log(`✅ Loyalty balance: ${data.balance} points = ${data.valueVnd} VND`);
        } catch (err) {
            error.value = err.response?.data?.message || 'Không thể tải điểm thưởng';
            console.error('❌ Error fetching loyalty balance:', err);
        } finally {
            loading.value = false;
        }
    };

    /**
     * Fetch loyalty history
     */
    const fetchHistory = async () => {
        loading.value = true;
        error.value = null;

        try {
            const data = await loyaltyService.getHistory();
            history.value = data;
            console.log(`✅ Fetched ${data.length} loyalty transactions`);
        } catch (err) {
            error.value = err.response?.data?.message || 'Không thể tải lịch sử điểm thưởng';
            console.error('❌ Error fetching loyalty history:', err);
        } finally {
            loading.value = false;
        }
    };

    /**
     * Calculate VND value from points
     */
    const calculateVndFromPoints = (points) => {
        return points * 1000; // 1 point = 1,000 VND
    };

    /**
     * Calculate points from VND
     */
    const calculatePointsFromVnd = (vnd) => {
        return Math.floor(vnd / 1000);
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
        balance.value = 0;
        valueVnd.value = 0;
        history.value = [];
        loading.value = false;
        error.value = null;
    };

    return {
        // State
        balance,
        valueVnd,
        history,
        loading,
        error,

        // Computed
        hasPoints,
        formattedBalance,
        formattedValue,

        // Actions
        fetchBalance,
        fetchHistory,
        calculateVndFromPoints,
        calculatePointsFromVnd,
        clearError,
        reset
    };
});


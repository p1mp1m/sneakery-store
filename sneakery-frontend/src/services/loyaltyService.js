/**
 * Loyalty Points Service
 * API client cho loyalty points
 */

import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

class LoyaltyService {
    /**
     * Lấy loyalty points balance
     */
    async getBalance() {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(
                `${API_BASE_URL}/loyalty/balance`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch loyalty balance:', error);
            throw error;
        }
    }

    /**
     * Lấy loyalty transaction history
     */
    async getHistory() {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(
                `${API_BASE_URL}/loyalty/history`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch loyalty history:', error);
            throw error;
        }
    }
}

export default new LoyaltyService();


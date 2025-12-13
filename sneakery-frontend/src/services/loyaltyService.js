/**
 * Loyalty Points Service
 * API client cho loyalty points
 */

import axios from 'axios';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';

// Lấy access token từ localStorage
const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

class LoyaltyService {
    /**
     * Lấy loyalty points balance
     */
    async getBalance() {
        try {
            const response = await axios.get(
                `${API_ENDPOINTS.LOYALTY.BASE}/balance`,
                {
                    headers: getAuthHeader()
                }
            );
            return response.data;
        } catch (error) {
            logger.error('Failed to fetch loyalty balance:', error);
            throw error;
        }
    }

    /**
     * Lấy loyalty transaction history
     */
    async getHistory() {
        try {
            const response = await axios.get(
                `${API_ENDPOINTS.LOYALTY.BASE}/history`,
                {
                    headers: getAuthHeader()
                }
            );
            return response.data;
        } catch (error) {
            logger.error('Failed to fetch loyalty history:', error);
            throw error;
        }
    }
}

export default new LoyaltyService();


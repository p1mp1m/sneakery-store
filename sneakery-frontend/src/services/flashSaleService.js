/**
 * Flash Sale Service
 * API client cho flash sales
 */

import axios from 'axios';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';

// Lấy access token từ localStorage
const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

class FlashSaleService {
    /**
     * Lấy tất cả flash sales đang active
     */
    async getActiveFlashSales() {
        try {
            const response = await axios.get(API_ENDPOINTS.FLASH_SALES.ACTIVE);
            logger.log('Flash sales API response:', response.data);
            return response.data;
        } catch (error) {
            logger.error('Failed to fetch active flash sales:', error);
            if (error.response) {
                logger.error('Response status:', error.response.status);
                logger.error('Response data:', error.response.data);
            }
            throw error;
        }
    }

    /**
     * Lấy flash sale của product
     */
    async getFlashSaleByProductId(productId) {
        try {
            const response = await axios.get(API_ENDPOINTS.FLASH_SALES.BY_PRODUCT(productId));
            return response.data;
        } catch (error) {
            if (error.response && error.response.status === 404) {
                return null; // No flash sale for this product
            }
            logger.error(`Failed to fetch flash sale for product ${productId}:`, error);
            throw error;
        }
    }

    /**
     * ADMIN: Tạo flash sale mới
     */
    async createFlashSale(flashSaleData) {
        try {
            const response = await axios.post(
                API_ENDPOINTS.ADMIN_FLASH_SALES.BASE,
                flashSaleData,
                {
                    headers: getAuthHeader()
                }
            );
            logger.log('Flash sale created successfully');
            return response.data;
        } catch (error) {
            logger.error('Failed to create flash sale:', error);
            throw error;
        }
    }

    /**
     * ADMIN: Update flash sale
     */
    async updateFlashSale(id, flashSaleData) {
        try {
            const response = await axios.put(
                API_ENDPOINTS.ADMIN_FLASH_SALES.BY_ID(id),
                flashSaleData,
                {
                    headers: getAuthHeader()
                }
            );
            logger.log(`Flash sale ${id} updated successfully`);
            return response.data;
        } catch (error) {
            logger.error(`Failed to update flash sale ${id}:`, error);
            throw error;
        }
    }

    /**
     * ADMIN: Delete flash sale
     */
    async deleteFlashSale(id) {
        try {
            await axios.delete(
                API_ENDPOINTS.ADMIN_FLASH_SALES.BY_ID(id),
                {
                    headers: getAuthHeader()
                }
            );
            logger.log(`Flash sale ${id} deleted successfully`);
        } catch (error) {
            logger.error(`Failed to delete flash sale ${id}:`, error);
            throw error;
        }
    }
}

export default new FlashSaleService();


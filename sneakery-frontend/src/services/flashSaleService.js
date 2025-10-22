/**
 * Flash Sale Service
 * API client cho flash sales
 */

import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

class FlashSaleService {
    /**
     * Lấy tất cả flash sales đang active
     */
    async getActiveFlashSales() {
        try {
            const response = await axios.get(`${API_BASE_URL}/flash-sales/active`);
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch active flash sales:', error);
            throw error;
        }
    }

    /**
     * Lấy flash sale của product
     */
    async getFlashSaleByProductId(productId) {
        try {
            const response = await axios.get(`${API_BASE_URL}/flash-sales/product/${productId}`);
            return response.data;
        } catch (error) {
            if (error.response && error.response.status === 404) {
                return null; // No flash sale for this product
            }
            console.error(`❌ Failed to fetch flash sale for product ${productId}:`, error);
            throw error;
        }
    }

    /**
     * ADMIN: Tạo flash sale mới
     */
    async createFlashSale(flashSaleData) {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.post(
                `${API_BASE_URL}/admin/flash-sales`,
                flashSaleData,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data;
        } catch (error) {
            console.error('❌ Failed to create flash sale:', error);
            throw error;
        }
    }

    /**
     * ADMIN: Update flash sale
     */
    async updateFlashSale(id, flashSaleData) {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.put(
                `${API_BASE_URL}/admin/flash-sales/${id}`,
                flashSaleData,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data;
        } catch (error) {
            console.error(`❌ Failed to update flash sale ${id}:`, error);
            throw error;
        }
    }

    /**
     * ADMIN: Delete flash sale
     */
    async deleteFlashSale(id) {
        try {
            const token = localStorage.getItem('token');
            await axios.delete(
                `${API_BASE_URL}/admin/flash-sales/${id}`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
        } catch (error) {
            console.error(`❌ Failed to delete flash sale ${id}:`, error);
            throw error;
        }
    }
}

export default new FlashSaleService();


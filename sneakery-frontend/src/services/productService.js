import axios from 'axios';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';

class ProductService {
    // Lấy danh sách sản phẩm với phân trang
    async getProducts(page = 0, size = 10) {
        try {
            const response = await axios.get(API_ENDPOINTS.PRODUCTS.BASE, {
                params: {
                    page: page,
                    size: size
                }
            });
            // Trả về response để có thể truy cập response.data ở component
            return response;
        } catch (error) {
            logger.error('Error fetching products:', error);
            throw error;
        }
    }

    // Lấy chi tiết sản phẩm theo ID
    async getProductById(id) {
        try {
            const response = await axios.get(API_ENDPOINTS.PRODUCTS.BY_ID(id));
            return response.data;
        } catch (error) {
            logger.error(`Error fetching product ${id}:`, error);
            throw error;
        }
    }

    // Tìm kiếm sản phẩm với filters
    async searchProducts(keyword = '', filters = {}) {
        try {
            const response = await axios.get(API_ENDPOINTS.PRODUCTS.SEARCH, {
                params: {
                    keyword: keyword,
                    ...filters
                }
            });
            return response.data;
        } catch (error) {
            logger.error('Error searching products:', error);
            throw error;
        }
    }
}

export default new ProductService();
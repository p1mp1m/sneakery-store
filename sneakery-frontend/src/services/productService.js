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

    // Tìm kiếm sản phẩm theo brand/category/page/size
    async searchProducts(filters = {}) {
    try {
        const response = await axios.get(API_ENDPOINTS.PRODUCTS.SEARCH, {
        params: {
            brand: filters.brand || undefined,
            category: filters.category || undefined,
            page: filters.page ?? 0,
            size: filters.size ?? 20,
        },
        });

        return response.data;
    } catch (error) {
        logger.error("Error searching products:", error);
        throw error;
    }
    }

    // Lấy sản phẩm tương tự (ưu tiên brand → category)
    async getRelatedProducts(id, brandId = null, categoryIds = [], limit = 4) {
    try {
        const response = await axios.get(
        API_ENDPOINTS.PRODUCTS.RELATED(id),
        {
            params: {
            brandId,
            categoryIds,  // backend sẽ tự map thành categoryIds=1&categoryIds=2...
            limit
            }
        }
        );

        return response.data;
    } catch (error) {
        logger.error("Error fetching related products:", error);
        return [];
    }
    }

    // Lấy tất cả hình ảnh sản phẩm (dùng cho admin)
    async getAllProductImages() {
        return axios.get('/api/products/images');
    }
}

export default new ProductService();
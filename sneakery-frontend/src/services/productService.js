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

    // Lấy sản phẩm tương tự (dựa trên brand hoặc category)
    async getRelatedProducts(productId, brandId = null, categoryIds = [], limit = 4) {
        try {
            // Fetch sản phẩm cùng brand trước, nếu không đủ thì lấy thêm sản phẩm cùng category
            const params = {
                page: 0,
                size: limit + 1 // Lấy thêm 1 để loại bỏ sản phẩm hiện tại
            };
            
            if (brandId) {
                params.brandId = brandId;
            }
            
            const response = await axios.get(API_ENDPOINTS.PRODUCTS.BASE, { params });
            let products = response.data.content || [];
            
            // Loại bỏ sản phẩm hiện tại
            products = products.filter(p => p.id !== productId);
            
            // Nếu không đủ sản phẩm cùng brand, lấy thêm sản phẩm cùng category
            if (products.length < limit && categoryIds.length > 0) {
                const categoryParams = {
                    page: 0,
                    size: limit - products.length + 1
                };
                
                // Lấy sản phẩm cùng category đầu tiên
                if (categoryIds[0]) {
                    categoryParams.categoryId = categoryIds[0];
                }
                
                const categoryResponse = await axios.get(API_ENDPOINTS.PRODUCTS.BASE, { params: categoryParams });
                const categoryProducts = categoryResponse.data.content || [];
                
                // Thêm sản phẩm cùng category (loại bỏ trùng và sản phẩm hiện tại)
                const additionalProducts = categoryProducts
                    .filter(p => p.id !== productId && !products.find(existing => existing.id === p.id))
                    .slice(0, limit - products.length);
                
                products = [...products, ...additionalProducts];
            }
            
            // Giới hạn số lượng
            return products.slice(0, limit);
        } catch (error) {
            logger.error('Error fetching related products:', error);
            // Trả về mảng rỗng nếu có lỗi
            return [];
        }
    }
}

export default new ProductService();
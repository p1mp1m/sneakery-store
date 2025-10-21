import axios from 'axios';

// Sử dụng relative path để Vite proxy có thể forward requests
const API_URL = '/api/products';

class ProductService {
    // Lấy danh sách sản phẩm với phân trang
    async getProducts(page = 0, size = 10) {
        const response = await axios.get(API_URL, {
            params: {
                page: page,
                size: size
            }
        });
        return response.data;
    }

    // Lấy chi tiết sản phẩm theo ID
    async getProductById(id) {
        const response = await axios.get(`${API_URL}/${id}`);
        return response.data;
    }

    // Tìm kiếm sản phẩm với filters
    async searchProducts(keyword = '', filters = {}) {
        const response = await axios.get(`${API_URL}/search`, {
            params: {
                keyword: keyword,
                ...filters
            }
        });
        return response.data;
    }
}

export default new ProductService();
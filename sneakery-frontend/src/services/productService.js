import axios from 'axios';

// Backend API URL
const API_URL = 'http://localhost:8080/api/products';

class ProductService {
    // Lấy danh sách sản phẩm với phân trang
    async getProducts(page = 0, size = 10) {
        const response = await axios.get(API_URL, {
            params: {
                page: page,
                size: size
            }
        });
        // Trả về response để có thể truy cập response.data ở component
        return response;
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
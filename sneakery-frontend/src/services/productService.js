import axios from 'axios';

// Sử dụng relative path để Vite proxy có thể forward requests
const API_URL = '/api/products';

class ProductService {
    // Sửa lại hàm để nhận page và size
    getProducts(page, size) {
        return axios.get(API_URL, {
            params: {
                page: page,
                size: size
            }
        });
    }
}

export default new ProductService();
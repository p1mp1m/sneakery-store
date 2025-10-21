import axios from 'axios';

// Sử dụng relative path để Vite proxy có thể forward requests
const API_URL = '/api/auth/';

class AuthService {
    // Phương thức đăng nhập
    async login(user) {
        const response = await axios.post(API_URL + 'login', {
            email: user.email,
            password: user.password
        });
        return response.data;
    }

    // Phương thức đăng ký
    async register(user) {
        const response = await axios.post(API_URL + 'register', {
            fullName: user.fullName,
            email: user.email,
            password: user.password,
            phoneNumber: user.phoneNumber
        });
        return response.data;
    }
}

export default new AuthService();
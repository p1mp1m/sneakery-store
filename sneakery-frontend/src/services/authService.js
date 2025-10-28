import axios from 'axios';

// Backend API URL
const API_URL = 'http://localhost:8080/api/auth/';

class AuthService {
    // Phương thức đăng nhập
    async login(user) {
        console.log('🔐 AuthService - Login attempt:', user.email);
        try {
            const response = await axios.post(API_URL + 'login', {
                email: user.email,
                password: user.password
            });
            console.log('✅ AuthService - Login response:', response.data);
            return response.data;
        } catch (error) {
            console.error('❌ AuthService - Login error:', error.response?.data || error.message);
            throw error;
        }
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
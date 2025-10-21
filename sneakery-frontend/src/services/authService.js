import axios from 'axios';

// Sử dụng relative path để Vite proxy có thể forward requests
const API_URL = '/api/auth/';

class AuthService {
    async login(user) {
        const response = await axios.post(API_URL + 'login', {
            email: user.email,
            password: user.password
        });
        return response.data;
    }

    // Phương thức để gọi API đăng ký
    register(user) {
        return axios.post(API_URL + 'register', {
            fullName: user.fullName,
            email: user.email,
            password: user.password,
            phoneNumber: user.phoneNumber
        });
    }
}

export default new AuthService();
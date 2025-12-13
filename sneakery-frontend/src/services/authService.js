import axios from 'axios';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';

class AuthService {
    // Phương thức đăng nhập
    async login(user) {
        logger.log('AuthService - Login attempt:', user.email);
        try {
            const response = await axios.post(API_ENDPOINTS.AUTH.LOGIN, {
                email: user.email,
                password: user.password
            });
            logger.log('AuthService - Login successful');
            return response.data;
        } catch (error) {
            logger.error('AuthService - Login error:', error.response?.data || error.message);
            throw error;
        }
    }

    // Phương thức đăng ký
    async register(user) {
        try {
            const response = await axios.post(API_ENDPOINTS.AUTH.REGISTER, {
                fullName: user.fullName,
                email: user.email,
                password: user.password,
                phoneNumber: user.phoneNumber
            });
            logger.log('AuthService - Registration successful');
            return response.data;
        } catch (error) {
            logger.error('AuthService - Registration error:', error.response?.data || error.message);
            throw error;
        }
    }

    // Phương thức đăng xuất
    async logout() {
        try {
            await axios.post(API_ENDPOINTS.AUTH.LOGOUT);
            logger.log('AuthService - Logout successful');
        } catch (error) {
            logger.error('AuthService - Logout error:', error);
            // Even if logout fails, clear local storage
            throw error;
        }
    }

    // Lấy thông tin profile
    async getProfile() {
        try {
            const response = await axios.get(API_ENDPOINTS.AUTH.PROFILE);
            return response.data;
        } catch (error) {
            logger.error('AuthService - Get profile error:', error);
            throw error;
        }
    }

    // Cập nhật profile
    async updateProfile(profileData) {
        try {
            const response = await axios.put(API_ENDPOINTS.AUTH.PROFILE, profileData);
            return response.data;
        } catch (error) {
            logger.error('AuthService - Update profile error:', error);
            throw error;
        }
    }

    // Đổi mật khẩu
    async changePassword(passwordData) {
        try {
            const response = await axios.post(API_ENDPOINTS.AUTH.CHANGE_PASSWORD, passwordData);
            return response.data;
        } catch (error) {
            logger.error('AuthService - Change password error:', error);
            throw error;
        }
    }

    // Quên mật khẩu
    async forgotPassword(email) {
        try {
            const response = await axios.post(API_ENDPOINTS.AUTH.FORGOT_PASSWORD, { email });
            return response.data;
        } catch (error) {
            logger.error('AuthService - Forgot password error:', error);
            throw error;
        }
    }

    // Reset mật khẩu
    async resetPassword(token, password) {
        try {
            const response = await axios.post(API_ENDPOINTS.AUTH.RESET_PASSWORD, {
                token,
                password
            });
            return response.data;
        } catch (error) {
            logger.error('AuthService - Reset password error:', error);
            throw error;
        }
    }
}

export default new AuthService();
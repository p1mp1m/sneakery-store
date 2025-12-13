import { defineStore } from 'pinia';
import AuthService from '@/services/authService';
import { ref, computed } from 'vue';
import logger from '@/utils/logger';

/**
 * Hàm decode JWT token (không verify signature - chỉ để đọc payload)
 */
function decodeJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (error) {
        logger.error('Error decoding JWT:', error);
        return null;
    }
}

// 'useAuthStore' là tên của store
export const useAuthStore = defineStore('auth', () => {
    // === STATE ===
    // Lấy thông tin user từ localStorage khi khởi tạo
    const storedUser = localStorage.getItem('user');
    const storedToken = localStorage.getItem('token');
    
    const user = ref(storedUser ? JSON.parse(storedUser) : null);
    const token = ref(storedToken || null);
    
    // Nếu có token nhưng không có user info, decode JWT để lấy thông tin
    if (token.value && !user.value) {
        const decoded = decodeJwt(token.value);
        if (decoded) {
            // Kiểm tra token có hết hạn không
            const currentTime = Math.floor(Date.now() / 1000);
            if (decoded.exp && decoded.exp < currentTime) {
                // Token đã hết hạn, xóa khỏi localStorage
                logger.warn('Token đã hết hạn, xóa khỏi localStorage');
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                token.value = null;
                user.value = null;
            } else {
                user.value = {
                    email: decoded.sub, // JWT standard: subject chứa email/username
                    role: decoded.role || decoded.authorities?.[0] || 'USER',
                    fullName: decoded.fullName || decoded.name,
                    accessToken: token.value
                };
                localStorage.setItem('user', JSON.stringify(user.value));
            }
        }
    }
    
    // Sync token từ user object nếu cần
    if (user.value?.accessToken && !token.value) {
        token.value = user.value.accessToken;
        localStorage.setItem('token', user.value.accessToken);
    }

    // === GETTERS ===
    const isAuthenticated = computed(() => !!token.value && !!user.value);
    const currentUser = computed(() => user.value);
    const userRole = computed(() => user.value?.role || null);

    // === ACTIONS ===
    async function login(credentials) {
        // Gọi API service để đăng nhập
        const responseData = await AuthService.login(credentials);
        logger.log('✅ Auth Store - Login response:', responseData);
        logger.log('✅ Auth Store - accessToken:', responseData.accessToken);
        
        // Kiểm tra có token không
        if (!responseData.accessToken) {
            logger.error('❌ Không có accessToken trong response!');
            throw new Error('Login failed: No token received');
        }
        
        // Lưu token
        token.value = responseData.accessToken;
        localStorage.setItem('token', responseData.accessToken);
        
        // Lưu thông tin user
        user.value = responseData;
        localStorage.setItem('user', JSON.stringify(responseData));
        
        logger.log('✅ Auth Store - Token saved to localStorage:', localStorage.getItem('token'));
        logger.log('✅ Auth Store - User saved:', user.value);
    }

    async function register(userData) {
        // Gọi API service để đăng ký
        const responseData = await AuthService.register(userData);
        
        // Tự động login sau khi đăng ký thành công
        if (responseData.accessToken) {
            token.value = responseData.accessToken;
            user.value = responseData;
            localStorage.setItem('token', responseData.accessToken);
            localStorage.setItem('user', JSON.stringify(responseData));
        }
        
        return responseData;
    }

    function logout() {
        // Xóa thông tin khỏi state và localStorage
        user.value = null;
        token.value = null;
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        logger.log('✅ Auth Store - Logged out');
    }

    // Trả về state, getters, và actions để các component khác có thể sử dụng
    return {
        user,
        token,
        isAuthenticated,
        currentUser,
        userRole,
        login,
        register,
        logout
    };
});
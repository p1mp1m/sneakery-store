import { defineStore } from 'pinia';
import AuthService from '@/services/authService';
import { ref, computed } from 'vue';

// 'useAuthStore' là tên của store, bạn có thể đặt tên bất kỳ
export const useAuthStore = defineStore('auth', () => {
    // === STATE ===
    // Lấy thông tin user từ localStorage khi khởi tạo
    const user = ref(JSON.parse(localStorage.getItem('user')));
    const token = computed(() => user.value?.accessToken);

    // === GETTERS ===
    const isAuthenticated = computed(() => !!user.value);
    const currentUser = computed(() => user.value);

    // === ACTIONS ===
    async function login(credentials) {
        // Gọi API service để đăng nhập
        const responseData = await AuthService.login(credentials);
        // console.log('Auth Store - Login response:', responseData); // Debug
        
        // Lưu thông tin vào state và localStorage
        user.value = responseData;
        localStorage.setItem('user', JSON.stringify(responseData));
        // console.log('Auth Store - User saved:', user.value); // Debug
    }

    async function register(userData) {
        // Gọi API service để đăng ký
        const response = await AuthService.register(userData);
        return response.data;
    }

    function logout() {
        // Xóa thông tin khỏi state và localStorage
        user.value = null;
        localStorage.removeItem('user');
    }

    // Trả về state, getters, và actions để các component khác có thể sử dụng
    return {
        user,
        token,
        isAuthenticated,
        currentUser,
        login,
        register,
        logout
    };
});
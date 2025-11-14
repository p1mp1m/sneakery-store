import { createApp } from 'vue'
import { createPinia } from 'pinia' // 👈 1. Import Pinia
import axios from 'axios'
import notificationService from '@/utils/notificationService'
import logger from '@/utils/logger'

// Import Tailwind CSS FIRST - before other styles
import './assets/styles/tailwind.css'

import App from './App.vue'
import router from './routers/index.js'
import vPermission from './directives/v-permission.js'

const app = createApp(App)
const pinia = createPinia() // 👈 2. Tạo một instance của Pinia

// Register global directive
app.directive('permission', vPermission)

// ============================================
// 🔐 AXIOS INTERCEPTOR - TỰ ĐỘNG GỬI JWT TOKEN
// ============================================
axios.interceptors.request.use(
  (config) => {
    // Lấy token từ localStorage
    const token = localStorage.getItem('token');
    
    // Nếu có token, thêm vào Authorization header
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// ============================================
// 🚨 AXIOS RESPONSE INTERCEPTOR - XỬ LÝ LỖI
// ============================================
axios.interceptors.response.use(
  (response) => {
    // Trả về response nếu thành công
    return response;
  },
  (error) => {
    // Xử lý lỗi 401 Unauthorized
    if (error.response && error.response.status === 401) {
      // Xóa token và redirect về login
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      
      // Chỉ redirect nếu không phải đang ở trang login/register
      if (router.currentRoute.value.path !== '/login' && 
          router.currentRoute.value.path !== '/register') {
        router.push({
          path: '/login',
          query: { redirect: router.currentRoute.value.fullPath }
        });
      }
    }
    
    // Xử lý lỗi network
    if (!error.response) {
      logger.error('❌ Network Error:', error.message);
      notificationService.error('Lỗi kết nối', 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.');
    }
    
    return Promise.reject(error);
  }
);

app.use(router)
app.use(pinia) // 👈 3. Sử dụng Pinia

// ═══════════════════════════════════════════════════════════════════════
// 🎨 THEME INITIALIZATION - Apply theme before first render
// ═══════════════════════════════════════════════════════════════════════
import { useThemeStore } from '@/stores/theme'
const themeStore = useThemeStore()
themeStore.initTheme()

// ═══════════════════════════════════════════════════════════════════════
// 🧪 EXPOSE NOTIFICATION SERVICE FOR TESTING (Development only)
// ═══════════════════════════════════════════════════════════════════════
if (import.meta.env.DEV) {
  window.notificationService = notificationService
}

app.mount('#app')
import { createApp } from 'vue'
import { createPinia } from 'pinia' // 👈 1. Import Pinia
import axios from 'axios'
import { ElMessage } from 'element-plus'

import App from './App.vue'
import router from './routers/index.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// Import custom CSS - Modular Architecture
import './assets/styles/main.css' // 🎨 Modular CSS Architecture với 25+ files

const app = createApp(App)
const pinia = createPinia() // 👈 2. Tạo một instance của Pinia

// ⚙️ Cấu hình mặc định
ElMessage.defaults = {
  offset: 20,      // cách mép trên 20px
  showClose: true, // có nút đóng
  grouping: false, // không gộp
  center: false,
  customClass: 'aurora-message'
}

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
      console.error('❌ Network Error:', error.message);
      ElMessage.error('Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.');
    }
    
    return Promise.reject(error);
  }
);

app.use(router)
app.use(ElementPlus)
app.use(pinia) // 👈 3. Sử dụng Pinia

app.mount('#app')
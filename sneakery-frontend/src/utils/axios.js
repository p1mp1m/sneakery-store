// src/utils/axios.js
import axios from "axios";
import { useAuthStore } from "@/stores/auth";
import notificationService from "@/utils/notificationService";
import router from "@/routers";
import logger from "@/utils/logger";

// ================================
// ⚙️ Tạo axios instance
// ================================
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
  timeout: 15000,
  withCredentials: false, // tránh CORS & cookie không cần thiết
});

// Cờ chống spam redirect khi 401 liên tục
let isHandling401 = false;

// ================================
// 📌 REQUEST INTERCEPTOR
// ================================
api.interceptors.request.use(
  (config) => {
    let token = null;

    try {
      const authStore = useAuthStore();
      token = authStore?.token;
    } catch (err) {
      logger.warn("AuthStore chưa sẵn sàng", err);
    }

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// ================================
// 📌 RESPONSE INTERCEPTOR
// ================================
api.interceptors.response.use(
  (response) => response,

  async (error) => {
    const status = error.response?.status;
    const originalRequest = error.config;

    // ----------------------------
    // 🛑 Lỗi mạng (server down / mất internet)
    // ----------------------------
    if (!error.response) {
      logger.error("❌ Network error:", error.message);

      notificationService.error(
        "Mạng không ổn định",
        "Không thể kết nối tới server. Vui lòng kiểm tra kết nối."
      );

      return Promise.reject(error);
    }

    // ================================
    // 🔥 401 - Unauthorized
    // ================================
    if (status === 401) {
      if (!isHandling401) {
        isHandling401 = true;

        try {
          const authStore = useAuthStore();
          authStore?.logout();

          notificationService.error("Phiên đăng nhập đã hết hạn", "Vui lòng đăng nhập lại.");

          // Không redirect nếu đang ở /login
          if (router.currentRoute.value.path !== "/login") {
            router.push({
              path: "/login",
              query: { redirect: router.currentRoute.value.fullPath },
            });
          }
        } finally {
          // Mở lại cờ sau 1s để tránh redirect liên hoàn
          setTimeout(() => (isHandling401 = false), 1000);
        }
      }

      return Promise.reject(error);
    }

    // ================================
    // 🔥 403 - Forbidden
    // ================================
    if (status === 403) {
      notificationService.error("Không có quyền truy cập", "Bạn không được phép thực hiện tác vụ này.");
      return Promise.reject(error);
    }

    // ================================
    // 🔥 404 - Not Found
    // ================================
    if (status === 404) {
      logger.warn("API 404:", originalRequest.url);
      notificationService.error("Không tìm thấy dữ liệu", "Vui lòng kiểm tra lại.");
      return Promise.reject(error);
    }

    // ================================
    // 🔥 500 - Server Error
    // ================================
    if (status === 500) {
      logger.error("Server 500:", error.response.data);
      notificationService.error(
        "Lỗi hệ thống",
        "Đã xảy ra lỗi trên máy chủ. Vui lòng thử lại sau."
      );
      return Promise.reject(error);
    }

    return Promise.reject(error);
  }
);

export default api;

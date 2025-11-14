<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <!-- Header -->
        <div class="login-header">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
        </div>

        <!-- Forgot Password Form -->
        <form class="login-form" @submit.prevent="handleForgotPassword">
          <!-- Error Message -->
          <div v-if="serverError" class="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg flex items-start gap-3">
            <i class="material-icons text-red-500 mt-0.5">error</i>
            <p class="text-sm text-red-700 dark:text-red-400 flex-1">{{ serverError }}</p>
          </div>

          <h2 class="login-title">Quên mật khẩu</h2>
          <p class="login-subtitle">Nhập email của bạn để nhận liên kết đặt lại mật khẩu.</p>

          <!-- Email Field -->
          <div class="mb-4">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl">email</i>
              <input
                v-model="forgotForm.email"
                type="email"
                placeholder="Nhập email của bạn"
                class="w-full pl-11 pr-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 dark:focus:ring-purple-400 focus:border-transparent transition-all"
                required
              />
            </div>
            <p v-if="errors.email" class="mt-1 text-sm text-red-600 dark:text-red-400">{{ errors.email }}</p>
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-purple-800 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-2"
          >
            <i v-if="loading" class="material-icons animate-spin">refresh</i>
            <span>{{ loading ? 'Đang gửi...' : 'Gửi liên kết đặt lại' }}</span>
          </button>

          <!-- Back to login -->
          <div style="text-align:center; margin-top:16px;">
            <a href="#" class="forgot-password" @click.prevent="router.push('/login')">
              ← Quay lại đăng nhập
            </a>
          </div>
        </form>
      </div>

      <!-- Welcome Section -->
      <div class="welcome-section">
        <div class="welcome-content">
          <h2 class="welcome-title">Chào mừng đến với Sneakery Store</h2>
          <p class="welcome-subtitle">Khám phá bộ sưu tập giày sneaker đa dạng và chất lượng cao</p>

          <div class="features">
            <div class="feature-item" v-for="(text, i) in features" :key="i">
              <div class="feature-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span>{{ text }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import notificationService from '@/utils/notificationService';
import axios from 'axios';
import { API_ENDPOINTS, buildApiUrl } from '@/config/api';

const router = useRouter();
const loading = ref(false);
const serverError = ref('');
const errors = ref({});

const forgotForm = ref({
  email: ''
});

const features = [
  'Sản phẩm chính hãng 100%',
  'Giao hàng nhanh chóng',
  'Hỗ trợ 24/7'
];

// Validation
const validateForm = () => {
  errors.value = {};
  
  if (!forgotForm.value.email?.trim()) {
    errors.value.email = 'Vui lòng nhập email';
    return false;
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(forgotForm.value.email)) {
    errors.value.email = 'Email không hợp lệ';
    return false;
  }
  
  return true;
};

const handleForgotPassword = async () => {
  if (!validateForm()) return;
  
  loading.value = true;
  serverError.value = '';
  errors.value = {};
  
  try {
    const { data } = await axios.post(
      buildApiUrl(API_ENDPOINTS.AUTH.FORGOT_PASSWORD),
      { email: forgotForm.value.email }
    );
    notificationService.success('Thành công', data?.message || 'Liên kết đặt lại đã được gửi!');
  } catch (error) {
    serverError.value = error.response?.data?.message || 'Không thể gửi email, thử lại.';
    notificationService.error('Lỗi', serverError.value);
  } finally {
    loading.value = false;
  }
};
</script>



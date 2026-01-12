<template>
  <div class="min-h-screen bg-gradient-to-br from-purple-600 via-purple-700 to-indigo-800 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 flex items-center justify-center p-4">
    <div class="w-full max-w-6xl grid grid-cols-1 lg:grid-cols-2 gap-8 items-center">
      <!-- Login Card -->
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl p-8 md:p-12">
        <!-- Header -->
        <div class="text-center mb-8">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="h-16 mx-auto mb-4" />
          <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100">Đăng nhập</h2>
          <p class="text-gray-600 dark:text-gray-400 mt-2">Chào mừng bạn trở lại!</p>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="handleLogin">
          <!-- Error Message -->
          <div v-if="serverError" class="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg flex items-start gap-3">
            <i class="material-icons text-red-500 mt-0.5">error</i>
            <p class="text-sm text-red-700 dark:text-red-400 flex-1">{{ serverError }}</p>
          </div>

          <!-- Email Field -->
          <div class="mb-4">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl">email</i>
              <input
                v-model="loginForm.email"
                type="email"
                placeholder="Nhập email của bạn"
                class="w-full pl-11 pr-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 dark:focus:ring-purple-400 focus:border-transparent transition-all"
                required
              />
            </div>
            <p v-if="errors.email" class="mt-1 text-sm text-red-600 dark:text-red-400">{{ errors.email }}</p>
          </div>

          <!-- Password Field -->
          <div class="mb-4">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl">lock</i>
              <input
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Nhập mật khẩu"
                class="w-full pl-11 pr-12 py-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 dark:focus:ring-purple-400 focus:border-transparent transition-all"
                required
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <i class="material-icons text-xl">{{ showPassword ? 'visibility_off' : 'visibility' }}</i>
              </button>
            </div>
            <p v-if="errors.password" class="mt-1 text-sm text-red-600 dark:text-red-400">{{ errors.password }}</p>
          </div>

          <!-- Remember Me & Forgot Password -->
          <div class="flex items-center justify-between mb-6">
            <label class="flex items-center gap-2 cursor-pointer">
              <input
                v-model="rememberMe"
                type="checkbox"
                class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500 dark:focus:ring-purple-400 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
              />
              <span class="text-sm text-gray-600 dark:text-gray-400">Ghi nhớ đăng nhập</span>
            </label>
            <a href="#" class="text-sm text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 transition-colors" @click.prevent="handleForgotPassword">
              Quên mật khẩu?
            </a>
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-purple-800 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-2"
          >
            <i v-if="loading" class="material-icons animate-spin">refresh</i>
            <span>{{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}</span>
          </button>
        </form>

        <!-- Divider -->
        <!-- <div class="relative my-6">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-gray-200 dark:border-gray-700"></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="px-4 bg-white dark:bg-gray-800 text-gray-500 dark:text-gray-400">Hoặc</span>
          </div>
        </div> -->

        <!-- Social Login -->
        <!-- <div class="mb-6">
          <GoogleButton 
            text="Đăng nhập với Google"
            :loading="false"
            @click="handleGoogleLogin"
          />
        </div> -->

        <!-- Register Link -->
        <div class="text-center">
          <p class="text-sm text-gray-600 dark:text-gray-400">
            Chưa có tài khoản? 
            <router-link to="/register" class="text-purple-600 dark:text-purple-400 font-semibold hover:text-purple-700 dark:hover:text-purple-300 transition-colors">
              Đăng ký ngay
            </router-link>
          </p>
        </div>
      </div>

      <!-- Welcome Section -->
      <div class="hidden lg:block text-white">
        <div class="space-y-6">
          <h2 class="text-4xl font-bold">Chào mừng đến với Sneakery Store</h2>
          <p class="text-lg text-white/90">Khám phá bộ sưu tập giày sneaker đa dạng và chất lượng cao</p>
          
          <div class="space-y-4 mt-8">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-white/20 backdrop-blur-sm flex items-center justify-center">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="text-lg">Sản phẩm chính hãng 100%</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-white/20 backdrop-blur-sm flex items-center justify-center">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="text-lg">Giao hàng nhanh chóng</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-white/20 backdrop-blur-sm flex items-center justify-center">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span class="text-lg">Hỗ trợ 24/7</span>
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
import { useAuthStore } from '@/stores/auth';
import notificationService from '@/utils/notificationService';
import GoogleButton from '@/assets/components/common/GoogleButton.vue';

const router = useRouter();
const authStore = useAuthStore();

// Form state
const loading = ref(false);
const serverError = ref('');
const rememberMe = ref(false);
const showPassword = ref(false);
const errors = ref({});

const loginForm = ref({
  email: '',
  password: ''
});

// Validation
const validateForm = () => {
  errors.value = {};
  
  // Email validation
  if (!loginForm.value.email?.trim()) {
    errors.value.email = 'Vui lòng nhập email';
    return false;
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(loginForm.value.email.trim())) {
    errors.value.email = 'Email không hợp lệ';
    return false;
  }
  
  // Password validation
  if (!loginForm.value.password) {
    errors.value.password = 'Vui lòng nhập mật khẩu';
    return false;
  }
  
  if (loginForm.value.password.length < 6) {
    errors.value.password = 'Mật khẩu phải có ít nhất 6 ký tự';
    return false;
  }
  
  return true;
};

// Methods
const handleLogin = async () => {
  if (!validateForm()) return;
  
  loading.value = true;
  serverError.value = '';
  errors.value = {};
  
  try {
    await authStore.login(loginForm.value);
    
    // 🔐 PHÂN QUYỀN: Redirect theo role
    const user = authStore.currentUser;
    
    if (user.role === 'ADMIN' || user.role === 'MODERATOR') {
      notificationService.success('Thành công', `Chào mừng Admin ${user.fullName}!`);
      router.push('/admin/dashboard');
    } else {
      notificationService.success('Thành công', `Chào mừng ${user.fullName}!`);
      router.push('/user/dashboard');
    }
  } catch (error) {
    serverError.value = error.response?.data?.message || 'Email hoặc mật khẩu không chính xác.';
    notificationService.warning('Cảnh báo', serverError.value);
  } finally {
    loading.value = false;
  }
};

const handleForgotPassword = () => {
   router.push({ name: 'forgot-password' });
};

const handleGoogleLogin = () => {
  notificationService.info('Thông tin','Tính năng đăng nhập Google sẽ được cập nhật sớm!');
};
</script>

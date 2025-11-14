<template>
  <div class="min-h-screen bg-gradient-to-br from-purple-600 via-purple-700 to-indigo-800 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 flex items-center justify-center p-4">
    <div class="w-full max-w-6xl grid grid-cols-1 lg:grid-cols-2 gap-8 items-center">
      <!-- Register Card -->
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl p-8 md:p-12">
        <!-- Header -->
        <div class="text-center mb-8">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="h-16 mx-auto mb-4" />
          <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100">Đăng ký</h2>
          <p class="text-gray-600 dark:text-gray-400 mt-2">Tạo tài khoản mới để bắt đầu mua sắm</p>
        </div>

        <!-- Register Form -->
        <form @submit.prevent="handleRegister">
          <!-- Error Message -->
          <div v-if="serverError" class="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg flex items-start gap-3">
            <i class="material-icons text-red-500 mt-0.5">error</i>
            <p class="text-sm text-red-700 dark:text-red-400 flex-1">{{ serverError }}</p>
          </div>

          <!-- Success Message -->
          <div v-if="successMessage" class="mb-6 p-4 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg flex items-start gap-3">
            <i class="material-icons text-green-500 mt-0.5">check_circle</i>
            <p class="text-sm text-green-700 dark:text-green-400 flex-1">{{ successMessage }}</p>
          </div>

          <!-- Full Name Field -->
          <div class="mb-4">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl">person</i>
              <input
                v-model="registerForm.fullName"
                type="text"
                placeholder="Nhập họ và tên"
                class="w-full pl-11 pr-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 dark:focus:ring-purple-400 focus:border-transparent transition-all"
                required
              />
            </div>
            <p v-if="errors.fullName" class="mt-1 text-sm text-red-600 dark:text-red-400">{{ errors.fullName }}</p>
          </div>

          <!-- Email Field -->
          <div class="mb-4">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl">email</i>
              <input
                v-model="registerForm.email"
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
                v-model="registerForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Nhập mật khẩu (ít nhất 6 ký tự)"
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
          
          <!-- Phone Number Field -->
          <div class="mb-4">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl">phone</i>
              <input
                v-model="registerForm.phoneNumber"
                type="tel"
                placeholder="Nhập số điện thoại (tùy chọn)"
                class="w-full pl-11 pr-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 dark:focus:ring-purple-400 focus:border-transparent transition-all"
              />
            </div>
            <p v-if="errors.phoneNumber" class="mt-1 text-sm text-red-600 dark:text-red-400">{{ errors.phoneNumber }}</p>
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-purple-800 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-2"
          >
            <i v-if="loading" class="material-icons animate-spin">refresh</i>
            <span>{{ loading ? 'Đang tạo tài khoản...' : 'Tạo tài khoản' }}</span>
          </button>
        </form>
        
        <!-- Divider -->
        <div class="relative my-6">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-gray-200 dark:border-gray-700"></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="px-4 bg-white dark:bg-gray-800 text-gray-500 dark:text-gray-400">Hoặc</span>
          </div>
        </div>

        <!-- Social Login -->
        <div class="mb-6">
          <GoogleButton 
            text="Đăng ký với Google"
            :loading="false"
            @click="handleGoogleLogin"
          />
        </div>

        <!-- Login Link -->
        <div class="text-center">
          <p class="text-sm text-gray-600 dark:text-gray-400">
            Đã có tài khoản? 
            <router-link to="/login" class="text-purple-600 dark:text-purple-400 font-semibold hover:text-purple-700 dark:hover:text-purple-300 transition-colors">
              Đăng nhập ngay
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

const loading = ref(false);
const serverError = ref('');
const successMessage = ref('');
const showPassword = ref(false);
const errors = ref({});

const registerForm = ref({
  fullName: '',
  email: '',
  password: '',
  phoneNumber: ''
});

// Validation
const validateForm = () => {
  errors.value = {};
  
  // Full name validation
  if (!registerForm.value.fullName?.trim()) {
    errors.value.fullName = 'Vui lòng nhập họ tên';
    return false;
  }
  
  if (registerForm.value.fullName.trim().length < 2) {
    errors.value.fullName = 'Họ tên phải có ít nhất 2 ký tự';
    return false;
  }
  
  if (registerForm.value.fullName.trim().length > 100) {
    errors.value.fullName = 'Họ tên không được vượt quá 100 ký tự';
    return false;
  }
  
  // Email validation
  if (!registerForm.value.email?.trim()) {
    errors.value.email = 'Vui lòng nhập email';
    return false;
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(registerForm.value.email.trim())) {
    errors.value.email = 'Email không đúng định dạng';
    return false;
  }
  
  // Password validation
  if (!registerForm.value.password) {
    errors.value.password = 'Vui lòng nhập mật khẩu';
    return false;
  }
  
  if (registerForm.value.password.length < 6) {
    errors.value.password = 'Mật khẩu phải có ít nhất 6 ký tự';
    return false;
  }
  
  if (registerForm.value.password.length > 128) {
    errors.value.password = 'Mật khẩu không được vượt quá 128 ký tự';
    return false;
  }
  
  // Phone number validation (optional but validate format if provided)
  if (registerForm.value.phoneNumber && registerForm.value.phoneNumber.trim()) {
    const phoneRegex = /^[0-9]{10,11}$/;
    const cleanedPhone = registerForm.value.phoneNumber.trim().replace(/[\s\-\(\)]/g, '');
    if (!phoneRegex.test(cleanedPhone)) {
      errors.value.phoneNumber = 'Số điện thoại không hợp lệ (10-11 chữ số)';
      return false;
    }
  }
  
  return true;
};

const handleRegister = async () => {
  if (!validateForm()) return;
  
  loading.value = true;
  serverError.value = '';
  successMessage.value = '';
  errors.value = {};
  
  try {
    const response = await authStore.register(registerForm.value);
    successMessage.value = response + " Tự động chuyển đến trang đăng nhập...";
    notificationService.success('Thành công', 'Đăng ký thành công!');
    
    // Chuyển trang sau 2 giây
    setTimeout(() => {
      router.push('/login');
    }, 2000);
  } catch (error) {
    serverError.value = error.response?.data?.message || 'Đã có lỗi xảy ra, vui lòng thử lại.';
    notificationService.error('Lỗi', serverError.value);
  } finally {
    loading.value = false;
  }
};

const handleGoogleLogin = () => {
  notificationService.info('Thông tin','Tính năng đăng ký Google sẽ được cập nhật sớm!');
};
</script>

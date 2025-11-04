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
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          @submit.prevent="handleLogin(loginFormRef)"
        >
          <!-- Error Message -->
          <el-alert
            v-if="serverError"
            :title="serverError"
            type="error"
            show-icon
            :closable="false"
            class="mb-6"
          />

          <!-- Email Field -->
          <el-form-item prop="email" class="mb-4">
            <el-input
              v-model="loginForm.email"
              type="email"
              placeholder="Nhập email của bạn"
              size="large"
              :prefix-icon="EmailIcon"
              clearable
            />
          </el-form-item>

          <!-- Password Field -->
          <el-form-item prop="password" class="mb-4">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="Nhập mật khẩu"
              size="large"
              :prefix-icon="LockIcon"
              show-password
              clearable
            />
          </el-form-item>

          <!-- Remember Me & Forgot Password -->
          <div class="flex items-center justify-between mb-6">
            <el-checkbox v-model="rememberMe" size="large">
              <span class="text-sm text-gray-600 dark:text-gray-400">Ghi nhớ đăng nhập</span>
            </el-checkbox>
            <a href="#" class="text-sm text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 transition-colors" @click.prevent="handleForgotPassword">
              Quên mật khẩu?
            </a>
          </div>

          <!-- Submit Button -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="w-full"
              :loading="loading"
              native-type="submit"
            >
              <span v-if="!loading">Đăng nhập</span>
              <span v-else>Đang đăng nhập...</span>
            </el-button>
          </el-form-item>
        </el-form>

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
            text="Đăng nhập với Google"
            :loading="false"
            @click="handleGoogleLogin"
          />
        </div>

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
import { ref, h } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ElMessage } from 'element-plus';
import GoogleButton from '@/assets/components/common/GoogleButton.vue';

const router = useRouter();
const authStore = useAuthStore();

// Icons
const EmailIcon = () => h('svg', {
  width: '20',
  height: '20',
  viewBox: '0 0 24 24',
  fill: 'none',
  xmlns: 'http://www.w3.org/2000/svg'
}, [
  h('path', {
    d: 'M4 4H20C21.1 4 22 4.9 22 6V18C22 19.1 21.1 20 20 20H4C2.9 20 2 19.1 2 18V6C2 4.9 2.9 4 4 4Z',
    stroke: 'currentColor',
    'stroke-width': '2',
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round'
  }),
  h('polyline', {
    points: '22,6 12,13 2,6',
    stroke: 'currentColor',
    'stroke-width': '2',
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round'
  })
]);

const LockIcon = () => h('svg', {
  width: '20',
  height: '20',
  viewBox: '0 0 24 24',
  fill: 'none',
  xmlns: 'http://www.w3.org/2000/svg'
}, [
  h('rect', {
    x: '3',
    y: '11',
    width: '18',
    height: '11',
    rx: '2',
    ry: '2',
    stroke: 'currentColor',
    'stroke-width': '2'
  }),
  h('circle', {
    cx: '12',
    cy: '7',
    r: '4',
    stroke: 'currentColor',
    'stroke-width': '2'
  })
]);

// Form state
const loginFormRef = ref(null);
const loading = ref(false);
const serverError = ref('');
const rememberMe = ref(false);

const loginForm = ref({
  email: '',
  password: ''
});

// Validation rules
const rules = {
  email: [
    { required: true, message: 'Vui lòng nhập email', trigger: 'blur' },
    { type: 'email', message: 'Email không hợp lệ', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Vui lòng nhập mật khẩu', trigger: 'blur' },
    { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự', trigger: 'blur' }
  ]
};

// Methods
const handleLogin = async (formEl) => {
  if (!formEl) return;
  
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      serverError.value = '';
      
      try {
        await authStore.login(loginForm.value);
        
        // 🔐 PHÂN QUYỀN: Redirect theo role
        const user = authStore.currentUser;
        
        if (user.role === 'ADMIN' || user.role === 'MODERATOR') {
          ElMessage.success(`Chào mừng Admin ${user.fullName}!`);
          router.push('/admin/dashboard');
        } else {
          ElMessage.success(`Chào mừng ${user.fullName}!`);
          router.push('/user/dashboard');
        }
      } catch (error) {
        serverError.value = error.response?.data?.message || 'Email hoặc mật khẩu không chính xác.';
        ElMessage.error(serverError.value);
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleForgotPassword = () => {
   router.push({ name: 'forgot-password' });
};

const handleGoogleLogin = () => {
  ElMessage.info('Tính năng đăng nhập Google sẽ được cập nhật sớm!');
};
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <!-- Header -->
        <div class="login-header">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
        </div>

        <!-- Login Form -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleLogin(loginFormRef)"
        >
          <!-- Error Message -->
          <el-alert
            v-if="serverError"
            :title="serverError"
            type="error"
            show-icon
            :closable="false"
            class="error-alert"
          />

          <!-- Email Field -->
          <el-form-item prop="email">
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
          <el-form-item prop="password">
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
          <div class="form-options">
            <el-checkbox v-model="rememberMe" size="large">
              Ghi nhớ đăng nhập
            </el-checkbox>
            <a href="#" class="forgot-password" @click.prevent="handleForgotPassword">
              Quên mật khẩu?
            </a>
          </div>

          <!-- Submit Button -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              native-type="submit"
              block
            >
              <span v-if="!loading">Đăng nhập</span>
              <span v-else>Đang đăng nhập...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- Divider -->
        <div class="divider">
          <span class="divider-text">Hoặc</span>
        </div>

        <!-- Social Login -->
        <div class="social-login">
          <GoogleButton 
            text="Đăng nhập với Google"
            :loading="false"
            @click="handleGoogleLogin"
          />
        </div>

        <!-- Register Link -->
        <div class="register-link">
          <p>Chưa có tài khoản? <router-link to="/register">Đăng ký ngay</router-link></p>
        </div>
      </div>

      <!-- Welcome Section -->
      <div class="welcome-section">
        <div class="welcome-content">
          <h2 class="welcome-title">Chào mừng đến với Sneakery Store</h2>
          <p class="welcome-subtitle">Khám phá bộ sưu tập giày sneaker đa dạng và chất lượng cao</p>
          
          <div class="features">
            <div class="feature-item">
              <div class="feature-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span>Sản phẩm chính hãng 100%</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span>Giao hàng nhanh chóng</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span>Hỗ trợ 24/7</span>
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
        // console.log('LoginPage - User after login:', user); // Debug
        // console.log('LoginPage - User role:', user?.role); // Debug
        
        if (user.role === 'ADMIN' || user.role === 'MODERATOR') {
          ElMessage.success(`Chào mừng Admin ${user.fullName}!`);
          // console.log('Redirecting to /admin/dashboard'); // Debug
          router.push('/admin/dashboard');
        } else {
          ElMessage.success(`Chào mừng ${user.fullName}!`);
          // console.log('Redirecting to /user/dashboard'); // Debug
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
  ElMessage.info('Tính năng quên mật khẩu sẽ được cập nhật sớm!');
};

const handleGoogleLogin = () => {
  ElMessage.info('Tính năng đăng nhập Google sẽ được cập nhật sớm!');
};
</script>

<style scoped>
/* ===== LOGIN PAGE ===== */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  padding-top: 40px; /* Space for fixed navbar */
}

.login-container {
  width: 100%;
  max-width: 1000px;
  background: var(--white);
  border-radius: 30px;
  padding: var(--space-8);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--border-light);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-8);
  align-items: center;
}

.login-card {
  background: transparent;
  border-radius: 0;
  padding: 0;
  box-shadow: none;
  border: none;
}

/* ===== LOGIN HEADER ===== */
.login-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.logo-image {
  width: 120px;
  height: 120px;
  margin: 0 auto var(--space-6);
  display: block;
  object-fit: contain;
}

.login-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.login-subtitle {
  color: var(--text-secondary);
  font-size: var(--text-base);
  margin: 0;
}

/* ===== LOGIN FORM ===== */
.login-form {
  margin-bottom: var(--space-8);
}

.error-alert {
  margin-bottom: var(--space-5);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
}

.forgot-password {
  color: var(--primary-color);
  text-decoration: none;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  transition: color var(--transition-fast);
}

.forgot-password:hover {
  color: var(--primary-hover);
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  color: var(--white);
  border: 1px solid #475569;
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 12px rgba(30, 41, 59, 0.3);
  transition: all var(--transition-normal);
  padding: 0 var(--space-6);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.login-button:hover {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  border-color: #334155;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(30, 41, 59, 0.4);
}

.login-button:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(30, 41, 59, 0.3);
}

/* ===== ELEMENT PLUS INPUT CUSTOMIZATION ===== */
.login-page :deep(.el-input__wrapper) {
  background-color: white;
}

.login-page :deep(.el-input__inner) {
  color: #1e293b !important; /* Màu tối cho textbox */
}

.login-page :deep(.el-input__inner::placeholder) {
  color: var(--color-gray-500) !important;
}

/* ===== DIVIDER ===== */
.divider {
  text-align: center;
  margin: var(--space-8) 0;
  border: none; /* Bỏ border */
}

.divider-text {
  color: #1e293b;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

/* ===== SOCIAL LOGIN ===== */
.social-login {
  margin-bottom: var(--space-8);
}

/* Google button styles are now handled by GoogleButton component */

/* ===== REGISTER LINK ===== */
.register-link {
  text-align: center;
}

.register-link p {
  color: #1e293b;
  font-size: var(--text-sm);
  margin: 0;
}

.register-link a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-semibold);
  transition: color var(--transition-fast);
}

.register-link a:hover {
  color: var(--primary-hover);
  text-decoration: underline;
}

/* ===== WELCOME SECTION ===== */
.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: var(--space-8);
  position: relative;
  overflow: hidden;
}

.welcome-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.welcome-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.welcome-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--white);
  margin: 0 0 var(--space-5) 0;
  line-height: var(--leading-tight);
}

.welcome-subtitle {
  font-size: var(--text-lg);
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 var(--space-8) 0;
  line-height: var(--leading-relaxed);
}

.features {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--white);
}

.feature-icon {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-icon svg {
  color: var(--white);
}

/* ===== RESPONSIVE DESIGN ===== */
@media (max-width: 768px) {
  .login-container {
    grid-template-columns: 1fr;
    max-width: 450px;
  }
  
  .login-card {
    padding: var(--space-6);
  }
  
  .logo-image {
    width: 100px;
    height: 100px;
  }
  
  .login-title {
    font-size: var(--text-2xl);
  }
  
  .login-subtitle {
    font-size: var(--text-sm);
  }
  
  .welcome-title {
    font-size: var(--text-2xl);
  }
  
  .welcome-subtitle {
    font-size: var(--text-base);
  }
}

@media (max-width: 480px) {
  .login-page {
    padding: var(--space-3);
  }
  
  .login-card {
    padding: var(--space-5);
  }
  
  .logo-image {
    width: 80px;
    height: 80px;
  }
  
  .login-title {
    font-size: var(--text-xl);
  }
  
  .form-options {
    flex-direction: column;
    gap: var(--space-4);
    align-items: flex-start;
  }
}
</style>
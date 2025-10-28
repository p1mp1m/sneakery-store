<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <!-- Header -->
        <div class="register-header">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
        </div>

        <!-- Register Form -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
          class="register-form"
        @submit.prevent="handleRegister(registerFormRef)"
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

          <!-- Success Message -->
          <el-alert
            v-if="successMessage"
            :title="successMessage"
            type="success"
            show-icon
            :closable="false"
            class="error-alert"
          />

          <!-- Full Name Field -->
          <el-form-item prop="fullName">
            <el-input
              v-model="registerForm.fullName"
              placeholder="Nhập họ và tên"
              size="large"
              :prefix-icon="UserIcon"
              clearable
            />
        </el-form-item>

          <!-- Email Field -->
          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
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
            v-model="registerForm.password"
            type="password"
            placeholder="Nhập mật khẩu (ít nhất 6 ký tự)"
            size="large"
              :prefix-icon="LockIcon"
            show-password
              clearable
          />
        </el-form-item>
        
          <!-- Phone Number Field -->
          <el-form-item prop="phoneNumber">
            <el-input
              v-model="registerForm.phoneNumber"
              placeholder="Nhập số điện thoại (tùy chọn)"
              size="large"
              :prefix-icon="PhoneIcon"
              clearable
            />
        </el-form-item>

          <!-- Submit Button -->
          <el-form-item>
          <el-button
              type="primary"
            size="large"
              class="register-button"
            :loading="loading"
              native-type="submit"
              block
          >
              <span v-if="!loading">Tạo tài khoản</span>
              <span v-else>Đang tạo tài khoản...</span>
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
            text="Đăng ký với Google"
            :loading="false"
            @click="handleGoogleLogin"
          />
        </div>

        <!-- Login Link -->
        <div class="login-link">
          <p>Đã có tài khoản? <a href="/login">Đăng nhập ngay</a></p>
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
const UserIcon = () => h('svg', {
  width: '20',
  height: '20',
  viewBox: '0 0 24 24',
  fill: 'none',
  xmlns: 'http://www.w3.org/2000/svg'
}, [
  h('path', {
    d: 'M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21',
    stroke: 'currentColor',
    'stroke-width': '2',
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round'
  }),
  h('circle', {
    cx: '12',
    cy: '7',
    r: '4',
    stroke: 'currentColor',
    'stroke-width': '2'
  })
]);

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

const PhoneIcon = () => h('svg', {
  width: '20',
  height: '20',
  viewBox: '0 0 24 24',
  fill: 'none',
  xmlns: 'http://www.w3.org/2000/svg'
}, [
  h('path', {
    d: 'M22 16.92V19.92C22.0011 20.1985 21.9441 20.4742 21.8325 20.7293C21.7209 20.9845 21.5573 21.2136 21.3521 21.4019C21.1468 21.5901 20.9046 21.7335 20.6407 21.8227C20.3769 21.9119 20.0974 21.9451 19.82 21.92C16.7428 21.5856 13.787 20.5341 11.19 18.85C8.77382 17.3147 6.72533 15.2662 5.18999 12.85C3.49997 10.2412 2.44824 7.271 2.11999 4.18C2.095 3.90347 2.12787 3.62476 2.21649 3.36162C2.30512 3.09849 2.44756 2.85669 2.63476 2.65162C2.82196 2.44655 3.0498 2.28271 3.30379 2.17052C3.55777 2.05833 3.83233 2.00026 4.10999 2H7.10999C7.59531 1.99522 8.06691 2.16708 8.43376 2.48353C8.80061 2.79999 9.04076 3.23945 9.10999 3.72C9.23662 4.68007 9.47144 5.62273 9.80999 6.53C9.94454 6.88792 9.97366 7.27691 9.89391 7.65088C9.81415 8.02485 9.62886 8.36811 9.35999 8.64L8.08999 9.91C9.51355 12.4135 11.5865 14.4864 14.09 15.91L15.36 14.64C15.6319 14.3711 15.9751 14.1858 16.3491 14.1061C16.7231 14.0263 17.1121 14.0555 17.47 14.19C18.3773 14.5286 19.3199 14.7634 20.28 14.89C20.7658 14.9596 21.2094 15.2032 21.5265 15.5735C21.8437 15.9438 22.0122 16.4196 22 16.92Z',
    stroke: 'currentColor',
    'stroke-width': '2',
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round'
  })
]);

const registerFormRef = ref(null);
const loading = ref(false);
const serverError = ref('');
const successMessage = ref('');

const registerForm = ref({
  fullName: '',
  email: '',
  password: '',
  phoneNumber: ''
});

const rules = {
  fullName: [{ required: true, message: 'Vui lòng nhập họ tên', trigger: 'blur' }],
  email: [
    { required: true, message: 'Vui lòng nhập email', trigger: 'blur' },
    { type: 'email', message: 'Email không đúng định dạng', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: 'Vui lòng nhập mật khẩu', trigger: 'blur' },
    { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự', trigger: 'blur' }
  ],
};

const handleRegister = async (formEl) => {
  if (!formEl) return;
  
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      serverError.value = '';
      successMessage.value = '';
      
      try {
        const response = await authStore.register(registerForm.value);
        successMessage.value = response + " Tự động chuyển đến trang đăng nhập...";
        ElMessage.success('Đăng ký thành công!');
        
        // Chuyển trang sau 2 giây
        setTimeout(() => {
          router.push('/login');
        }, 2000);
      } catch (error) {
        serverError.value = error.response?.data?.message || 'Đã có lỗi xảy ra, vui lòng thử lại.';
        ElMessage.error(serverError.value);
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleGoogleLogin = () => {
  ElMessage.info('Tính năng đăng ký Google sẽ được cập nhật sớm!');
};
</script>

<style scoped>
/* ===== REGISTER PAGE ===== */
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  padding-top: 40px; /* Space for fixed navbar */
}

.register-container {
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

.register-card {
  background: transparent;
  border-radius: 0;
  padding: 0;
  box-shadow: none;
  border: none;
}

/* ===== REGISTER HEADER ===== */
.register-header {
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

/* ===== REGISTER FORM ===== */
.register-form {
  margin-bottom: var(--space-8);
}

.error-alert {
  margin-bottom: var(--space-5);
}

.register-button {
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

.register-button:hover {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  border-color: #334155;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(30, 41, 59, 0.4);
}

.register-button:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(30, 41, 59, 0.3);
}

/* ===== ELEMENT PLUS INPUT CUSTOMIZATION ===== */
.register-page :deep(.el-input__wrapper) {
  background-color: white;
}

.register-page :deep(.el-input__inner) {
  color: #1e293b !important; /* Màu tối cho textbox */
}

.register-page :deep(.el-input__inner::placeholder) {
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

/* ===== LOGIN LINK ===== */
.login-link {
  text-align: center;
}

.login-link p {
  color: #1e293b;
  font-size: var(--text-sm);
  margin: 0;
}

.login-link a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-medium);
  transition: color var(--transition-fast);
}

.login-link a:hover {
  color: var(--primary-dark);
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
  color: var(--white);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
}

.feature-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  color: var(--white);
  flex-shrink: 0;
}

/* ===== RESPONSIVE DESIGN ===== */
@media (max-width: 768px) {
  .register-container {
    grid-template-columns: 1fr;
    max-width: 450px;
  }
  
  .register-page {
    padding: var(--space-4);
  }
  
  .register-card {
    padding: var(--space-6);
  }
  
  .logo-image {
    width: 100px;
    height: 100px;
  }
  
  .welcome-title {
    font-size: var(--text-2xl);
  }
  
  .welcome-subtitle {
    font-size: var(--text-base);
  }
}

@media (max-width: 480px) {
  .register-page {
    padding: var(--space-3);
  }
  
  .register-card {
    padding: var(--space-6);
  }
  
  .logo-image {
    width: 80px;
    height: 80px;
  }
  
  .register-btn,
  .social-btn {
    height: 45px;
    font-size: var(--text-sm);
  }
}
</style>
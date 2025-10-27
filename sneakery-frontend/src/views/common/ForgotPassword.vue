<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <!-- Header -->
        <div class="login-header">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
        </div>

        <!-- Forgot Password Form -->
        <el-form
          ref="forgotFormRef"
          :model="forgotForm"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleForgotPassword(forgotFormRef)"
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

          <h2 class="login-title">Quên mật khẩu</h2>
          <p class="login-subtitle">Nhập email của bạn để nhận liên kết đặt lại mật khẩu.</p>

          <!-- Email Field -->
          <el-form-item prop="email">
            <el-input
              v-model="forgotForm.email"
              type="email"
              placeholder="Nhập email của bạn"
              size="large"
              :prefix-icon="EmailIcon"
              clearable
            />
          </el-form-item>

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
              <span v-if="!loading">Gửi liên kết đặt lại</span>
              <span v-else>Đang gửi...</span>
            </el-button>
          </el-form-item>

          <!-- Back to login -->
          <div style="text-align:center; margin-top:16px;">
            <a href="#" class="forgot-password" @click.prevent="router.push('/login')">
              ← Quay lại đăng nhập
            </a>
          </div>
        </el-form>
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
import { ref, h } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const router = useRouter();
const forgotFormRef = ref(null);
const loading = ref(false);
const serverError = ref('');

const forgotForm = ref({
  email: ''
});

const rules = {
  email: [
    { required: true, message: 'Vui lòng nhập email', trigger: 'blur' },
    { type: 'email', message: 'Email không hợp lệ', trigger: 'blur' }
  ]
};

// Email icon
const EmailIcon = () =>
  h('svg', { width: '20', height: '20', viewBox: '0 0 24 24', fill: 'none' }, [
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

const features = [
  'Sản phẩm chính hãng 100%',
  'Giao hàng nhanh chóng',
  'Hỗ trợ 24/7'
];

const handleForgotPassword = async (formEl) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      serverError.value = '';
      try {
        const API_URL = import.meta.env.VITE_API_URL;
        console.log('📡 Gửi request tới:', API_URL);

        const res = await axios.post(`${API_URL}/auth/forgot-password`, {
          email: forgotForm.value.email
        });

        ElMessage.success(res.data.message || 'Đã gửi liên kết đặt lại mật khẩu!');
      } catch (error) {
        console.error(error);
        serverError.value =
          error.response?.data?.message || 'Không thể gửi email, vui lòng thử lại.';
        ElMessage.error(serverError.value);
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap');

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 1000px;
  background: #fff;
  border-radius: 30px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  align-items: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.login-card {
  padding: 40px;
}

.logo-image {
  width: 120px;
  margin: 0 auto 30px;
  display: block;
}

.login-title {
  font-family: 'Poppins', sans-serif;
  font-size: 26px;
  font-weight: 700;
  color: #1e293b;
  text-align: center;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 15px;
  color: #64748b;
  text-align: center;
  margin-bottom: 28px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-family: 'Poppins', sans-serif;
  font-weight: 600;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  color: white;
  border: none;
  border-radius: 12px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.25);
}

.login-button:hover {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
}

.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 20px;
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  box-sizing: border-box;
}

.welcome-content {
  max-width: 380px;
  text-align: left;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 12px;
  line-height: 1.3;
}

.welcome-subtitle {
  font-size: 15px;
  opacity: 0.9;
  margin-bottom: 32px;
  line-height: 1.6;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
}

.feature-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
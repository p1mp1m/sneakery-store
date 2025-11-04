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
    if (!valid) return;
    loading.value = true;
    serverError.value = '';
    try {
      const { data } = await axios.post(
        `${import.meta.env.VITE_API_URL}/auth/forgot-password`,
        { email: forgotForm.value.email }
      );
      ElMessage.success(data?.message || 'Liên kết đặt lại đã được gửi!');
    } catch (error) {
      serverError.value = error.response?.data?.message || 'Không thể gửi email, thử lại.';
      ElMessage.error(serverError.value);
    } finally {
      loading.value = false;
    }
  });
};
</script>



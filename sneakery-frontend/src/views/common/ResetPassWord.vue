<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" class="login-form" @submit.prevent="handleReset">
          <h2 class="login-title">Đặt lại mật khẩu</h2>
          <p class="login-subtitle">Nhập mật khẩu mới của bạn bên dưới.</p>

          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="Mật khẩu mới" size="large" show-password />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="Nhập lại mật khẩu" size="large" show-password />
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            native-type="submit"
            block
          >
            <span v-if="!loading">Cập nhật mật khẩu</span>
            <span v-else>Đang xử lý...</span>
          </el-button>

          <div style="text-align:center; margin-top:16px;">
            <a href="#" class="forgot-password" @click.prevent="router.push('/login')">← Quay lại đăng nhập</a>
          </div>
        </el-form>
      </div>

      <!-- Right Section -->
      <div class="welcome-section">
        <div class="welcome-content">
          <h2 class="welcome-title">Chào mừng đến với Sneakery Store</h2>
          <p class="welcome-subtitle">Cập nhật mật khẩu của bạn để bảo vệ tài khoản an toàn.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const token = route.params.token;

const formRef = ref(null);
const loading = ref(false);
const form = ref({ password: '', confirmPassword: '' });

const rules = {
  password: [
    { required: true, message: 'Vui lòng nhập mật khẩu', trigger: 'blur' },
    { min: 6, message: 'Tối thiểu 6 ký tự', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: 'Vui lòng nhập lại mật khẩu', trigger: 'blur' },
    {
      validator: (_, value, cb) =>
        value !== form.value.password ? cb(new Error('Mật khẩu không khớp')) : cb(),
      trigger: 'blur',
    },
  ],
};

const handleReset = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      await axios.post(`${import.meta.env.VITE_API_URL}/auth/reset-password`, {
        token,
        password: form.value.password,
      });
      ElMessage.success('Đặt lại mật khẩu thành công!');
      router.push('/login');
    } catch (err) {
      ElMessage.error(err.response?.data?.message || 'Liên kết đã hết hạn hoặc không hợp lệ');
    } finally {
      loading.value = false;
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

.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 20px;
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.welcome-content {
  max-width: 380px;
  text-align: left;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 12px;
}
</style>
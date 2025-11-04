<template>
  <div class="login-page">
    <div class="login-container">
      <!-- LEFT SIDE: Reset Password Form -->
      <div class="login-card">
        <div class="login-header">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleReset"
        >
          <h2 class="login-title">Đặt lại mật khẩu</h2>
          <p class="login-subtitle">Nhập mật khẩu mới của bạn bên dưới.</p>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="Mật khẩu mới"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="Nhập lại mật khẩu"
              size="large"
              show-password
            />
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
            <a href="#" class="forgot-password" @click.prevent="router.push('/login')">
              ← Quay lại đăng nhập
            </a>
          </div>
        </el-form>
      </div>

      <!-- RIGHT SIDE -->
      <div class="welcome-section">
        <div class="welcome-content">
          <h2 class="welcome-title">Chào mừng đến với Sneakery Store</h2>
          <p class="welcome-subtitle">
            Cập nhật mật khẩu của bạn để bảo vệ tài khoản an toàn.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const token = route.query.token // ✅ Lấy token từ URL query (?token=xxx)

// Nếu không có token, quay về forgot password
if (!token) {
  ElMessage.error('Liên kết không hợp lệ hoặc đã hết hạn.')
  router.push('/forgot-password')
}

const formRef = ref(null)
const loading = ref(false)
const form = ref({
  password: '',
  confirmPassword: '',
})

const rules = {
  password: [
    { required: true, message: 'Vui lòng nhập mật khẩu', trigger: 'blur' },
    { min: 6, message: 'Tối thiểu 6 ký tự', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: 'Vui lòng nhập lại mật khẩu', trigger: 'blur' },
    {
      validator: (_, value, cb) =>
        value !== form.value.password
          ? cb(new Error('Mật khẩu không khớp'))
          : cb(),
      trigger: 'blur',
    },
  ],
}

const handleReset = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true

    try {
      // ✅ gửi đúng field BE mong đợi: "newPassword"
      await axios.post(`${import.meta.env.VITE_API_URL}/auth/reset-password`, {
        token,
        newPassword: form.value.password,
      })
      ElMessage.success('Đặt lại mật khẩu thành công!')
      router.push('/login')
    } catch (err) {
      console.error(err)
      ElMessage.error(
        err.response?.data?.message ||
          'Liên kết đã hết hạn hoặc không hợp lệ'
      )
    } finally {
      loading.value = false
    }
  })
}
</script>



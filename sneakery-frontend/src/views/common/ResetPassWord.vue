<template>
  <div class="login-page">
    <div class="login-container">
      <!-- LEFT SIDE: Reset Password Form -->
      <div class="login-card">
        <div class="login-header">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
        </div>

        <form class="login-form" @submit.prevent="handleReset">
          <h2 class="login-title">Đặt lại mật khẩu</h2>
          <p class="login-subtitle">Nhập mật khẩu mới của bạn bên dưới.</p>

          <div class="mb-4">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl">lock</i>
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Mật khẩu mới"
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

          <div class="mb-4">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 text-xl">lock</i>
              <input
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="Nhập lại mật khẩu"
                class="w-full pl-11 pr-12 py-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 dark:focus:ring-purple-400 focus:border-transparent transition-all"
                required
              />
              <button
                type="button"
                @click="showConfirmPassword = !showConfirmPassword"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <i class="material-icons text-xl">{{ showConfirmPassword ? 'visibility_off' : 'visibility' }}</i>
              </button>
            </div>
            <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-600 dark:text-red-400">{{ errors.confirmPassword }}</p>
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full py-3 px-4 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-purple-800 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-2"
          >
            <i v-if="loading" class="material-icons animate-spin">refresh</i>
            <span>{{ loading ? 'Đang xử lý...' : 'Cập nhật mật khẩu' }}</span>
          </button>

          <div style="text-align:center; margin-top:16px;">
            <a href="#" class="forgot-password" @click.prevent="router.push('/login')">
              ← Quay lại đăng nhập
            </a>
          </div>
        </form>
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
import notificationService from '@/utils/notificationService'
import { API_ENDPOINTS, buildApiUrl } from '@/config/api'
import logger from '@/utils/logger'

const router = useRouter()
const route = useRoute()
const token = route.query.token // ✅ Lấy token từ URL query (?token=xxx)

// Nếu không có token, quay về forgot password
if (!token) {
  notificationService.error('Lỗi','Liên kết không hợp lệ hoặc đã hết hạn.')
  router.push('/forgot-password')
}

const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const errors = ref({})
const form = ref({
  password: '',
  confirmPassword: '',
})

// Validation
const validateForm = () => {
  errors.value = {}
  
  if (!form.value.password) {
    errors.value.password = 'Vui lòng nhập mật khẩu'
    return false
  }
  
  if (form.value.password.length < 6) {
    errors.value.password = 'Tối thiểu 6 ký tự'
    return false
  }
  
  if (!form.value.confirmPassword) {
    errors.value.confirmPassword = 'Vui lòng nhập lại mật khẩu'
    return false
  }
  
  if (form.value.password !== form.value.confirmPassword) {
    errors.value.confirmPassword = 'Mật khẩu không khớp'
    return false
  }
  
  return true
}

const handleReset = async () => {
  if (!validateForm()) return
  
  loading.value = true
  errors.value = {}

  try {
    // ✅ gửi đúng field BE mong đợi: "newPassword"
    await axios.post(buildApiUrl(API_ENDPOINTS.AUTH.RESET_PASSWORD), {
      token,
      newPassword: form.value.password,
    })
    notificationService.success('Thành công', 'Đặt lại mật khẩu thành công!')
    router.push('/login')
  } catch (err) {
    logger.error('Error resetting password:', err)
    notificationService.error('Lỗi',
      err.response?.data?.message ||
        'Liên kết đã hết hạn hoặc không hợp lệ'
    )
  } finally {
    loading.value = false
  }
}
</script>



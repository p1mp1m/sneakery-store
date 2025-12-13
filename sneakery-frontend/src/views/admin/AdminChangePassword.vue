<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">lock</i>
            Đổi mật khẩu
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Thay đổi mật khẩu đăng nhập của bạn</p>
        </div>
      </div>
    </div>

    <!-- Change Password Form -->
    <div class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <form @submit.prevent="changePassword" class="space-y-6 max-w-2xl">
        <!-- Security Notice -->
        <div class="p-4 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg">
          <div class="flex items-start gap-3">
            <i class="material-icons text-blue-600 dark:text-blue-400 text-xl">info</i>
            <div class="flex-1">
              <h3 class="text-sm font-semibold text-blue-900 dark:text-blue-100 mb-1">Lưu ý bảo mật</h3>
              <ul class="text-xs text-blue-800 dark:text-blue-200 space-y-1">
                <li>• Mật khẩu phải có ít nhất 6 ký tự</li>
                <li>• Nên sử dụng kết hợp chữ hoa, chữ thường, số và ký tự đặc biệt</li>
                <li>• Không chia sẻ mật khẩu với người khác</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- Form Fields -->
        <div class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Mật khẩu hiện tại <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                v-model="passwordForm.currentPassword"
                :type="showCurrentPassword ? 'text' : 'password'"
                class="w-full px-4 py-2 pr-10 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Nhập mật khẩu hiện tại"
                required
              />
              <button
                type="button"
                @click="showCurrentPassword = !showCurrentPassword"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <i class="material-icons text-lg">{{ showCurrentPassword ? 'visibility_off' : 'visibility' }}</i>
              </button>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Mật khẩu mới <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                v-model="passwordForm.newPassword"
                :type="showNewPassword ? 'text' : 'password'"
                class="w-full px-4 py-2 pr-10 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Nhập mật khẩu mới"
                required
              />
              <button
                type="button"
                @click="showNewPassword = !showNewPassword"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <i class="material-icons text-lg">{{ showNewPassword ? 'visibility_off' : 'visibility' }}</i>
              </button>
            </div>
            <small class="text-xs text-gray-500 dark:text-gray-400 mt-1 block">Mật khẩu phải có ít nhất 6 ký tự</small>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Xác nhận mật khẩu mới <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                v-model="passwordForm.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                class="w-full px-4 py-2 pr-10 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Nhập lại mật khẩu mới"
                required
              />
              <button
                type="button"
                @click="showConfirmPassword = !showConfirmPassword"
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
              >
                <i class="material-icons text-lg">{{ showConfirmPassword ? 'visibility_off' : 'visibility' }}</i>
              </button>
            </div>
            <div v-if="passwordForm.confirmPassword && passwordForm.newPassword !== passwordForm.confirmPassword" class="text-xs text-red-500 dark:text-red-400 mt-1">
              Mật khẩu xác nhận không khớp
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex justify-end gap-3 pt-6 border-t border-gray-200 dark:border-gray-700">
          <button
            type="button"
            @click="resetForm"
            class="px-6 py-2 border border-gray-300 dark:border-gray-600 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors font-medium"
          >
            Hủy
          </button>
          <button
            type="submit"
            :disabled="changingPassword"
            class="px-6 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all font-medium flex items-center gap-2"
          >
            <i v-if="changingPassword" class="material-icons text-base animate-spin">refresh</i>
            <span v-if="changingPassword">Đang xử lý...</span>
            <span v-else>Đổi mật khẩu</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import toastService from '@/utils/toastService'
import axios from 'axios'

const router = useRouter()
const authStore = useAuthStore()

const changingPassword = ref(false)
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const changePassword = async () => {
  // Validation
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    toastService.error('Lỗi','Mật khẩu xác nhận không khớp')
    return
  }

  if (passwordForm.newPassword.length < 6) {
    toastService.error('Lỗi','Mật khẩu phải có ít nhất 6 ký tự')
    return
  }

  try {
    changingPassword.value = true

    await axios.post(
      `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/auth/change-password`,
      {
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword
      },
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    )

    toastService.success('Thành công','Đổi mật khẩu thành công!')
    resetForm()
  } catch (error) {
    console.error('Error changing password:', error)
    toastService.error('Lỗi',error.response?.data?.message || 'Không thể đổi mật khẩu')
  } finally {
    changingPassword.value = false
  }
}

const resetForm = () => {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}
</script>


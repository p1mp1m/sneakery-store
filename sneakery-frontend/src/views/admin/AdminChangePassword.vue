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
                class="w-full px-4 py-2 pr-10 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 transition-all"
                :class="[
                  passwordForm.newPassword && passwordStrength && !passwordStrength.valid
                    ? 'border-red-300 dark:border-red-600 focus:ring-red-500 focus:border-red-500'
                    : passwordForm.newPassword && passwordStrength && passwordStrength.valid
                    ? 'border-green-300 dark:border-green-600 focus:ring-green-500 focus:border-green-500'
                    : 'border-gray-300 dark:border-gray-600 focus:ring-purple-500 focus:border-transparent'
                ]"
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
            
            <!-- Password Strength Indicator -->
            <div v-if="passwordForm.newPassword" class="mt-2 space-y-2">
              <div class="flex items-center gap-2">
                <div class="flex-1 h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                  <div 
                    class="h-full transition-all duration-300"
                    :class="[
                      passwordStrength?.strength === 'strong' ? 'bg-green-500 w-full' :
                      passwordStrength?.strength === 'medium' ? 'bg-yellow-500 w-2/3' :
                      'bg-red-500 w-1/3'
                    ]"
                  ></div>
                </div>
                <span 
                  class="text-xs font-semibold"
                  :class="[
                    passwordStrength?.strength === 'strong' ? 'text-green-600 dark:text-green-400' :
                    passwordStrength?.strength === 'medium' ? 'text-yellow-600 dark:text-yellow-400' :
                    'text-red-600 dark:text-red-400'
                  ]"
                >
                  {{ passwordStrength?.strength === 'strong' ? 'Mạnh' : passwordStrength?.strength === 'medium' ? 'Trung bình' : 'Yếu' }}
                </span>
              </div>
              
              <!-- Password Requirements -->
              <div class="space-y-1">
                <p class="text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Yêu cầu mật khẩu:</p>
                <ul class="text-xs space-y-1">
                  <li class="flex items-center gap-2" :class="passwordForm.newPassword.length >= 8 ? 'text-green-600 dark:text-green-400' : 'text-gray-500 dark:text-gray-400'">
                    <i class="material-icons text-xs">{{ passwordForm.newPassword.length >= 8 ? 'check_circle' : 'radio_button_unchecked' }}</i>
                    Ít nhất 8 ký tự
                  </li>
                  <li class="flex items-center gap-2" :class="/[A-Z]/.test(passwordForm.newPassword) ? 'text-green-600 dark:text-green-400' : 'text-gray-500 dark:text-gray-400'">
                    <i class="material-icons text-xs">{{ /[A-Z]/.test(passwordForm.newPassword) ? 'check_circle' : 'radio_button_unchecked' }}</i>
                    Ít nhất 1 chữ hoa
                  </li>
                  <li class="flex items-center gap-2" :class="/[a-z]/.test(passwordForm.newPassword) ? 'text-green-600 dark:text-green-400' : 'text-gray-500 dark:text-gray-400'">
                    <i class="material-icons text-xs">{{ /[a-z]/.test(passwordForm.newPassword) ? 'check_circle' : 'radio_button_unchecked' }}</i>
                    Ít nhất 1 chữ thường
                  </li>
                  <li class="flex items-center gap-2" :class="/[0-9]/.test(passwordForm.newPassword) ? 'text-green-600 dark:text-green-400' : 'text-gray-500 dark:text-gray-400'">
                    <i class="material-icons text-xs">{{ /[0-9]/.test(passwordForm.newPassword) ? 'check_circle' : 'radio_button_unchecked' }}</i>
                    Ít nhất 1 số
                  </li>
                </ul>
              </div>
              
              <!-- Error Messages -->
              <div v-if="passwordStrength && passwordStrength.errors.length > 0" class="space-y-1">
                <p v-for="error in passwordStrength.errors" :key="error" class="text-xs text-red-600 dark:text-red-400 flex items-center gap-1">
                  <i class="material-icons text-xs">error</i>
                  {{ error }}
                </p>
              </div>
            </div>
            <small v-else class="text-xs text-gray-500 dark:text-gray-400 mt-1 block">Mật khẩu phải có ít nhất 6 ký tự</small>
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
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import notificationService from '@/utils/notificationService'
import axios from 'axios'
import { API_ENDPOINTS, buildApiUrl } from '@/config/api'
import logger from '@/utils/logger'
import { validatePasswordStrength } from '@/utils/validators'

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

// Password strength computed
const passwordStrength = computed(() => {
  if (!passwordForm.newPassword) return null
  return validatePasswordStrength(passwordForm.newPassword)
})

const changePassword = async () => {
  // Validation
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    notificationService.error('Lỗi','Mật khẩu xác nhận không khớp')
    return
  }

  // Validate password strength
  const strength = validatePasswordStrength(passwordForm.newPassword)
  if (!strength.valid) {
    notificationService.error('Lỗi', strength.errors[0] || 'Mật khẩu không hợp lệ')
    return
  }

  try {
    changingPassword.value = true

    await axios.post(
      buildApiUrl(API_ENDPOINTS.AUTH.CHANGE_PASSWORD),
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

    notificationService.success('Thành công','Đổi mật khẩu thành công!')
    resetForm()
  } catch (error) {
    logger.error('Error changing password:', error)
    notificationService.apiError(error, 'Không thể đổi mật khẩu')
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


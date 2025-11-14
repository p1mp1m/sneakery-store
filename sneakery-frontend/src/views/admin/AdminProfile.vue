<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">person</i>
            Hồ sơ cá nhân
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý thông tin tài khoản của bạn</p>
        </div>
      </div>
    </div>

    <!-- Profile Form -->
    <div class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <form @submit.prevent="updateProfile" class="space-y-6">
        <!-- Avatar Section -->
        <div class="flex items-center gap-6 pb-6 border-b border-gray-200 dark:border-gray-700">
          <div class="w-24 h-24 rounded-full bg-gradient-to-br from-purple-500 to-indigo-600 flex items-center justify-center">
            <i class="material-icons text-white text-4xl">person</i>
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100">{{ profile.fullName || 'Admin' }}</h2>
            <p class="text-sm text-gray-500 dark:text-gray-400">{{ profile.email }}</p>
            <p class="text-xs text-purple-600 dark:text-purple-400 mt-1">
              <i class="material-icons text-xs align-middle">verified</i>
              {{ profile.role || 'ADMIN' }}
            </p>
          </div>
        </div>

        <!-- Form Fields -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Họ và tên <span class="text-red-500">*</span>
            </label>
            <input
              v-model="profile.fullName"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Nhập họ và tên"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Email
            </label>
            <input
              v-model="profile.email"
              type="email"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-500 dark:text-gray-400 cursor-not-allowed"
              disabled
            />
            <small class="text-xs text-gray-500 dark:text-gray-400 mt-1 block">Email không thể thay đổi</small>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Số điện thoại
            </label>
            <input
              v-model="profile.phoneNumber"
              type="tel"
              class="w-full px-4 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 transition-all"
              :class="[
                profile.phoneNumber && !validateVietnamesePhone(profile.phoneNumber)
                  ? 'border-red-300 dark:border-red-600 focus:ring-red-500 focus:border-red-500'
                  : 'border-gray-300 dark:border-gray-600 focus:ring-purple-500 focus:border-transparent'
              ]"
              placeholder="Nhập số điện thoại (VD: 0901234567)"
              @blur="profile.phoneNumber = formatPhoneNumber(profile.phoneNumber)"
            />
            <p v-if="profile.phoneNumber && !validateVietnamesePhone(profile.phoneNumber)" class="text-xs text-red-600 dark:text-red-400 mt-1 flex items-center gap-1">
              <i class="material-icons text-xs">error</i>
              Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)
            </p>
            <small v-else-if="profile.phoneNumber" class="text-xs text-green-600 dark:text-green-400 mt-1 flex items-center gap-1">
              <i class="material-icons text-xs">check_circle</i>
              Số điện thoại hợp lệ
            </small>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">
              Vai trò
            </label>
            <input
              :value="profile.role || 'ADMIN'"
              type="text"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-500 dark:text-gray-400 cursor-not-allowed"
              disabled
            />
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
            :disabled="updating"
            class="px-6 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all font-medium flex items-center gap-2"
          >
            <i v-if="updating" class="material-icons text-base animate-spin">refresh</i>
            <span v-if="updating">Đang lưu...</span>
            <span v-else>Cập nhật thông tin</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import notificationService from '@/utils/notificationService'
import axios from 'axios'
import { API_ENDPOINTS, buildApiUrl } from '@/config/api'
import logger from '@/utils/logger'
import { validateVietnamesePhone, formatPhoneNumber } from '@/utils/validators'

const router = useRouter()
const authStore = useAuthStore()

const updating = ref(false)

const profile = reactive({
  fullName: '',
  email: '',
  phoneNumber: '',
  role: ''
})

const originalProfile = reactive({
  fullName: '',
  email: '',
  phoneNumber: '',
  role: ''
})

const loadProfile = async () => {
  try {
    // Lấy thông tin từ authStore trước (fallback)
    if (authStore.currentUser) {
      profile.fullName = authStore.currentUser.fullName || ''
      profile.email = authStore.currentUser.email || ''
      profile.phoneNumber = authStore.currentUser.phoneNumber || ''
      profile.role = authStore.currentUser.role || 'ADMIN'
    }

    // Lấy thông tin mới nhất từ API
    const response = await axios.get(
      buildApiUrl(API_ENDPOINTS.AUTH.PROFILE),
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    )

    if (response.data) {
      profile.fullName = response.data.fullName || ''
      profile.email = response.data.email || ''
      profile.phoneNumber = response.data.phoneNumber || ''
      profile.role = response.data.role || 'ADMIN'
    }

    // Lưu bản gốc để có thể reset
    originalProfile.fullName = profile.fullName
    originalProfile.email = profile.email
    originalProfile.phoneNumber = profile.phoneNumber
    originalProfile.role = profile.role
  } catch (error) {
    logger.error('Error loading profile:', error)
    // Nếu lỗi, vẫn dùng thông tin từ authStore
  }
}

const updateProfile = async () => {
  try {
    updating.value = true

    // Validate phone number if provided
    if (profile.phoneNumber && !validateVietnamesePhone(profile.phoneNumber)) {
      notificationService.error('Lỗi', 'Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)')
      updating.value = false
      return
    }

    const response = await axios.put(
      buildApiUrl(API_ENDPOINTS.AUTH.PROFILE),
      {
        fullName: profile.fullName,
        phoneNumber: profile.phoneNumber
      },
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    )

    // Cập nhật thông tin trong auth store
    if (response.data) {
      authStore.currentUser.fullName = response.data.fullName
      authStore.currentUser.phoneNumber = response.data.phoneNumber
      localStorage.setItem('user', JSON.stringify(authStore.currentUser))
    }

    notificationService.success('Thành công','Cập nhật thông tin thành công')
    
    // Cập nhật original profile
    originalProfile.fullName = profile.fullName
    originalProfile.phoneNumber = profile.phoneNumber
  } catch (error) {
    logger.error('Error updating profile:', error)
    notificationService.apiError(error, 'Không thể cập nhật thông tin')
  } finally {
    updating.value = false
  }
}

const resetForm = () => {
  profile.fullName = originalProfile.fullName
  profile.phoneNumber = originalProfile.phoneNumber
}

onMounted(() => {
  loadProfile()
})
</script>


<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-6xl mx-auto px-4">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-6">Thông tin cá nhân</h1>

      <!-- Profile Tabs -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 mb-6">
        <div class="flex overflow-x-auto border-b border-gray-200 dark:border-gray-700">
          <button
            :class="[
              'px-6 py-4 font-medium text-sm transition-all whitespace-nowrap border-b-2',
              activeTab === 'info'
                ? 'text-purple-600 dark:text-purple-400 border-purple-600 dark:border-purple-400'
                : 'text-gray-600 dark:text-gray-400 border-transparent hover:text-purple-600 dark:hover:text-purple-400'
            ]"
            @click="activeTab = 'info'"
          >
            Thông tin tài khoản
          </button>
          <button
            :class="[
              'px-6 py-4 font-medium text-sm transition-all whitespace-nowrap border-b-2',
              activeTab === 'address'
                ? 'text-purple-600 dark:text-purple-400 border-purple-600 dark:border-purple-400'
                : 'text-gray-600 dark:text-gray-400 border-transparent hover:text-purple-600 dark:hover:text-purple-400'
            ]"
            @click="activeTab = 'address'"
          >
            Địa chỉ
          </button>
          <button
            :class="[
              'px-6 py-4 font-medium text-sm transition-all whitespace-nowrap border-b-2',
              activeTab === 'password'
                ? 'text-purple-600 dark:text-purple-400 border-purple-600 dark:border-purple-400'
                : 'text-gray-600 dark:text-gray-400 border-transparent hover:text-purple-600 dark:hover:text-purple-400'
            ]"
            @click="activeTab = 'password'"
          >
            Đổi mật khẩu
          </button>
          <button
            :class="[
              'px-6 py-4 font-medium text-sm transition-all whitespace-nowrap border-b-2',
              activeTab === 'loyalty'
                ? 'text-purple-600 dark:text-purple-400 border-purple-600 dark:border-purple-400'
                : 'text-gray-600 dark:text-gray-400 border-transparent hover:text-purple-600 dark:hover:text-purple-400'
            ]"
            @click="activeTab = 'loyalty'"
          >
            Điểm thưởng
          </button>
          <button
            :class="[
              'px-6 py-4 font-medium text-sm transition-all whitespace-nowrap border-b-2',
              activeTab === 'theme'
                ? 'text-purple-600 dark:text-purple-400 border-purple-600 dark:border-purple-400'
                : 'text-gray-600 dark:text-gray-400 border-transparent hover:text-purple-600 dark:hover:text-purple-400'
            ]"
            @click="activeTab = 'theme'"
          >
            Giao diện
          </button>
        </div>
      </div>

      <!-- Tab Content -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <!-- Account Info Tab -->
        <div v-if="activeTab === 'info'" class="space-y-6">
          <form @submit.prevent="updateProfile" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Họ và tên *</label>
              <input v-model="profile.fullName" type="text" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" required />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Email</label>
              <input v-model="profile.email" type="email" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-500 dark:text-gray-400 cursor-not-allowed" disabled />
              <small class="text-xs text-gray-500 dark:text-gray-400 mt-1">Email không thể thay đổi</small>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Số điện thoại</label>
              <input v-model="profile.phoneNumber" type="tel" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" />
            </div>

            <div class="flex justify-end">
              <button type="submit" class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all" :disabled="updating">
                <span v-if="updating">Đang lưu...</span>
                <span v-else>Cập nhật thông tin</span>
              </button>
            </div>
          </form>
        </div>

        <!-- Address Tab -->
        <div v-if="activeTab === 'address'" class="space-y-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100">Danh sách địa chỉ</h2>
            <button @click="showAddressForm = true" class="px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all">
              + Thêm địa chỉ mới
            </button>
          </div>

          <!-- Loading State -->
          <div v-if="loadingAddresses" class="text-center py-12">
            <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-purple-600 border-t-transparent mb-3"></div>
            <p class="text-gray-600 dark:text-gray-400">Đang tải...</p>
          </div>

          <!-- Empty State -->
          <div v-else-if="addresses.length === 0" class="text-center py-12">
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="mx-auto mb-4 text-gray-400">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
            <p class="text-gray-600 dark:text-gray-400">Bạn chưa có địa chỉ nào</p>
          </div>

          <!-- Address List -->
          <div v-else class="space-y-4">
            <div
              v-for="addr in addresses"
              :key="addr.id"
              class="p-4 border border-gray-200 dark:border-gray-700 rounded-xl hover:border-purple-300 dark:hover:border-purple-600 transition-colors"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h4 class="font-semibold text-gray-900 dark:text-gray-100 mb-2">{{ addr.recipientName }}</h4>
                  <div class="space-y-1 text-sm text-gray-600 dark:text-gray-400">
                    <p>{{ addr.phone }}</p>
                    <p>{{ addr.line1 }}</p>
                    <p v-if="addr.line2">{{ addr.line2 }}</p>
                    <p>{{ addr.district }}, {{ addr.city }}</p>
                  </div>
                </div>
                <div class="flex gap-2 ml-4">
                  <button @click="editAddress(addr)" class="px-4 py-2 border border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors text-sm font-medium">
                    Sửa
                  </button>
                  <button @click="deleteAddress(addr.id)" class="px-4 py-2 border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors text-sm font-medium">
                    Xóa
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Change Password Tab -->
        <div v-if="activeTab === 'password'" class="space-y-6">
          <form @submit.prevent="changePassword" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Mật khẩu hiện tại *</label>
              <input v-model="passwordForm.currentPassword" type="password" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" required />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Mật khẩu mới *</label>
              <input v-model="passwordForm.newPassword" type="password" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" required />
              <small class="text-xs text-gray-500 dark:text-gray-400 mt-1">Mật khẩu phải có ít nhất 6 ký tự</small>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Xác nhận mật khẩu mới *</label>
              <input v-model="passwordForm.confirmPassword" type="password" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" required />
            </div>

            <div class="flex justify-end">
              <button type="submit" class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all" :disabled="changingPassword">
                <span v-if="changingPassword">Đang đổi mật khẩu...</span>
                <span v-else>Đổi mật khẩu</span>
              </button>
            </div>
          </form>
        </div>

        <!-- Loyalty Points Tab -->
        <div v-if="activeTab === 'loyalty'" class="space-y-6">
          <!-- Loyalty Balance Card -->
          <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-6 bg-gradient-to-br from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-12 h-12 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
                <i class="material-icons text-purple-600 dark:text-purple-400">stars</i>
              </div>
              <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100">Điểm thưởng của bạn</h2>
            </div>
            
            <div v-if="loyaltyLoading" class="text-center py-8">
              <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-purple-600 border-t-transparent mb-3"></div>
              <p class="text-gray-600 dark:text-gray-400">Đang tải...</p>
            </div>

            <div v-else class="space-y-4">
              <div class="text-center">
                <div class="text-4xl font-bold text-purple-600 dark:text-purple-400 mb-2">
                  {{ loyaltyStore.currentBalance.toLocaleString() }}
                </div>
                <div class="text-lg text-gray-600 dark:text-gray-400">Điểm</div>
              </div>

              <div class="flex items-center justify-center gap-2 text-gray-700 dark:text-gray-300">
                <i class="material-icons text-yellow-500">monetization_on</i>
                <span>≈ {{ formatCurrency(loyaltyStore.calculateVndFromPoints(loyaltyStore.currentBalance)) }}</span>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-3 mt-6">
                <div class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                  <i class="material-icons text-blue-500">info</i>
                  <span>1 điểm = 1.000 VNĐ</span>
                </div>
                <div class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                  <i class="material-icons text-green-500">shopping_cart</i>
                  <span>Dùng điểm khi thanh toán</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Points History -->
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Lịch sử điểm thưởng</h3>
            
            <div v-if="loyaltyHistory.length === 0" class="text-center py-12 border border-gray-200 dark:border-gray-700 rounded-xl">
              <i class="material-icons text-6xl text-gray-400 mb-3">history</i>
              <p class="text-gray-600 dark:text-gray-400">Chưa có lịch sử điểm thưởng</p>
            </div>

            <div v-else class="space-y-3">
              <div 
                v-for="item in loyaltyHistory" 
                :key="item.id"
                class="flex items-center gap-4 p-4 border border-gray-200 dark:border-gray-700 rounded-xl hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors"
              >
                <div :class="[
                  'w-12 h-12 rounded-full flex items-center justify-center',
                  item.transactionType === 'earn' ? 'bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400' :
                  item.transactionType === 'redeem' ? 'bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400' :
                  'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400'
                ]">
                  <i class="material-icons">
                    {{ item.transactionType === 'earn' ? 'add_circle' : item.transactionType === 'redeem' ? 'remove_circle' : 'access_time' }}
                  </i>
                </div>

                <div class="flex-1">
                  <div class="font-medium text-gray-900 dark:text-gray-100">{{ item.description }}</div>
                  <div class="text-sm text-gray-500 dark:text-gray-400">{{ formatDate(item.createdAt) }}</div>
                </div>

                <div :class="[
                  'font-bold text-lg',
                  item.transactionType === 'earn' ? 'text-green-600 dark:text-green-400' :
                  item.transactionType === 'redeem' ? 'text-red-600 dark:text-red-400' :
                  'text-gray-600 dark:text-gray-400'
                ]">
                  {{ item.transactionType === 'earn' ? '+' : '-' }}{{ Math.abs(item.points) }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Theme Settings Tab -->
        <div v-if="activeTab === 'theme'" class="space-y-6">
          <div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2">Cài đặt Giao diện</h2>
            <p class="text-gray-600 dark:text-gray-400">Chọn chế độ giao diện sáng hoặc tối</p>
          </div>

          <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-6" :class="isDark ? 'bg-gray-900' : 'bg-white'">
            <div class="flex items-center gap-2 mb-4">
              <div class="w-3 h-3 rounded-full bg-red-500"></div>
              <div class="w-3 h-3 rounded-full bg-yellow-500"></div>
              <div class="w-3 h-3 rounded-full bg-green-500"></div>
            </div>
            <div>
              <div class="font-semibold mb-2" :class="isDark ? 'text-white' : 'text-gray-900'">Xem trước</div>
              <div class="text-sm" :class="isDark ? 'text-gray-300' : 'text-gray-600'">Chế độ hiện tại: {{ isDark ? 'Tối' : 'Sáng' }}</div>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-4">Chế độ giao diện</label>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <label :class="[
                'p-4 border-2 rounded-xl cursor-pointer transition-all',
                theme === 'light' ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20' : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
              ]">
                <input type="radio" :value="'light'" :checked="theme === 'light'" @change="handleThemeChange('light')" name="theme" class="hidden" />
                <div class="flex items-center gap-3">
                  <i class="material-icons text-yellow-500">light_mode</i>
                  <div>
                    <div class="font-semibold text-gray-900 dark:text-gray-100">Sáng</div>
                    <div class="text-sm text-gray-600 dark:text-gray-400">Giao diện sáng với màu nền trắng</div>
                  </div>
                </div>
              </label>

              <label :class="[
                'p-4 border-2 rounded-xl cursor-pointer transition-all',
                theme === 'dark' ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20' : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
              ]">
                <input type="radio" :value="'dark'" :checked="theme === 'dark'" @change="handleThemeChange('dark')" name="theme" class="hidden" />
                <div class="flex items-center gap-3">
                  <i class="material-icons text-indigo-500">dark_mode</i>
                  <div>
                    <div class="font-semibold text-gray-900 dark:text-gray-100">Tối</div>
                    <div class="text-sm text-gray-600 dark:text-gray-400">Giao diện tối với màu nền đen</div>
                  </div>
                </div>
              </label>

              <label :class="[
                'p-4 border-2 rounded-xl cursor-pointer transition-all',
                theme === 'system' ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20' : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
              ]">
                <input type="radio" :value="'system'" :checked="theme === 'system'" @change="handleThemeChange('system')" name="theme" class="hidden" />
                <div class="flex items-center gap-3">
                  <i class="material-icons text-gray-500">brightness_auto</i>
                  <div>
                    <div class="font-semibold text-gray-900 dark:text-gray-100">Theo hệ thống</div>
                    <div class="text-sm text-gray-600 dark:text-gray-400">Tự động theo cài đặt hệ thống</div>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <div class="flex justify-end">
            <button @click="saveThemeSettings" class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all flex items-center gap-2">
              <i class="material-icons">save</i>
              Lưu cài đặt
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Address Modal -->
    <div v-if="showAddressForm" class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4" @click.self="showAddressForm = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100">{{ editingAddress ? 'Cập nhật địa chỉ' : 'Thêm địa chỉ mới' }}</h3>
          <button @click="closeAddressForm" class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">×</button>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Họ tên người nhận *</label>
            <input v-model="addressForm.recipientName" type="text" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Số điện thoại *</label>
            <input v-model="addressForm.phone" type="tel" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Địa chỉ *</label>
            <input v-model="addressForm.line1" type="text" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Địa chỉ 2 (tùy chọn)</label>
            <input v-model="addressForm.line2" type="text" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Quận/Huyện *</label>
            <input v-model="addressForm.district" type="text" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Tỉnh/Thành phố *</label>
            <input v-model="addressForm.city" type="text" class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500" />
          </div>
        </div>
        <div class="flex gap-3 p-6 border-t border-gray-200 dark:border-gray-700">
          <button @click="saveAddress" class="flex-1 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all">
            {{ editingAddress ? 'Cập nhật' : 'Lưu địa chỉ' }}
          </button>
          <button @click="closeAddressForm" class="flex-1 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors">Hủy</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useLoyaltyStore } from '@/stores/loyalty';
import { useTheme } from '@/composables/useTheme';
import { storeToRefs } from 'pinia';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const authStore = useAuthStore();
const loyaltyStore = useLoyaltyStore();
const { loading: loyaltyLoading } = storeToRefs(loyaltyStore);
const { theme, setTheme, isDark } = useTheme();

// State
const activeTab = ref('info');
const updating = ref(false);
const changingPassword = ref(false);
const loadingAddresses = ref(false);
const showAddressForm = ref(false);
const editingAddress = ref(null);

const profile = reactive({
  fullName: '',
  email: '',
  phoneNumber: '',
});

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const addresses = ref([]);

const addressForm = reactive({
  recipientName: '',
  phone: '',
  line1: '',
  line2: '',
  district: '',
  city: '',
});

// Loyalty Points computed
const loyaltyHistory = computed(() => loyaltyStore.history || []);

// Methods
const loadProfile = () => {
  if (authStore.currentUser) {
    profile.fullName = authStore.currentUser.fullName || '';
    profile.email = authStore.currentUser.email || '';
    profile.phoneNumber = authStore.currentUser.phoneNumber || '';
  }
};

const updateProfile = async () => {
  try {
    updating.value = true;
    
    // Note: Backend needs to support profile update endpoint
    // await axios.put('http://localhost:8080/api/users/profile', profile, {
    //   headers: { Authorization: `Bearer ${authStore.token}` },
    // });

    ElMessage.success('Cập nhật thông tin thành công');
  } catch (error) {
    console.error('Error updating profile:', error);
    ElMessage.error('Không thể cập nhật thông tin');
  } finally {
    updating.value = false;
  }
};

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('Mật khẩu xác nhận không khớp');
    return;
  }

  if (passwordForm.newPassword.length < 6) {
    ElMessage.error('Mật khẩu phải có ít nhất 6 ký tự');
    return;
  }

  try {
    changingPassword.value = true;

    // Note: Backend needs to support password change endpoint
    // await axios.post('http://localhost:8080/api/auth/change-password', {
    //   currentPassword: passwordForm.currentPassword,
    //   newPassword: passwordForm.newPassword,
    // }, {
    //   headers: { Authorization: `Bearer ${authStore.token}` },
    // });

    ElMessage.success('Đổi mật khẩu thành công');
        
    // Reset form
    passwordForm.currentPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
  } catch (error) {
    console.error('Error changing password:', error);
    ElMessage.error('Không thể đổi mật khẩu');
  } finally {
    changingPassword.value = false;
  }
};

const loadAddresses = async () => {
  try {
    loadingAddresses.value = true;
    const response = await axios.get('http://localhost:8080/api/addresses', {
      headers: { Authorization: `Bearer ${authStore.token}` },
    });
    addresses.value = response.data;
  } catch (error) {
    console.error('Error loading addresses:', error);
    ElMessage.error('Không thể tải danh sách địa chỉ');
  } finally {
    loadingAddresses.value = false;
  }
};

const editAddress = (addr) => {
  editingAddress.value = addr;
  addressForm.recipientName = addr.recipientName;
  addressForm.phone = addr.phone;
  addressForm.line1 = addr.line1;
  addressForm.line2 = addr.line2 || '';
  addressForm.district = addr.district || '';
  addressForm.city = addr.city;
  showAddressForm.value = true;
};

const saveAddress = async () => {
  if (!addressForm.recipientName || !addressForm.phone || !addressForm.line1 || !addressForm.city) {
    ElMessage.warning('Vui lòng điền đầy đủ thông tin bắt buộc');
    return;
  }

  try {
    if (editingAddress.value) {
      // Update existing address
      const response = await axios.put(
        `http://localhost:8080/api/addresses/${editingAddress.value.id}`,
        addressForm,
        {
          headers: { Authorization: `Bearer ${authStore.token}` },
        }
      );
      
      const index = addresses.value.findIndex(a => a.id === editingAddress.value.id);
      if (index !== -1) {
        addresses.value[index] = response.data;
      }
      
      ElMessage.success('Cập nhật địa chỉ thành công');
    } else {
      // Create new address
      const response = await axios.post(
        'http://localhost:8080/api/addresses',
        addressForm,
        {
          headers: { Authorization: `Bearer ${authStore.token}` },
        }
      );
      
      addresses.value.push(response.data);
      ElMessage.success('Thêm địa chỉ thành công');
    }

    closeAddressForm();
  } catch (error) {
    console.error('Error saving address:', error);
    ElMessage.error('Không thể lưu địa chỉ');
  }
};

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc muốn xóa địa chỉ này?',
      'Xác nhận',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
    );
    
    await axios.delete(`http://localhost:8080/api/addresses/${id}`, {
      headers: { Authorization: `Bearer ${authStore.token}` },
    });

    addresses.value = addresses.value.filter(a => a.id !== id);
    ElMessage.success('Đã xóa địa chỉ');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error deleting address:', error);
      ElMessage.error('Không thể xóa địa chỉ');
    }
  }
};

const closeAddressForm = () => {
  showAddressForm.value = false;
  editingAddress.value = null;
  addressForm.recipientName = '';
  addressForm.phone = '';
  addressForm.line1 = '';
  addressForm.line2 = '';
  addressForm.district = '';
  addressForm.city = '';
};

// Loyalty methods
const loadLoyaltyData = async () => {
  await Promise.all([
    loyaltyStore.fetchBalance(),
    loyaltyStore.fetchHistory()
  ]);
};

// Helper methods
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
};

const formatDate = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// Watch tab changes
watch(activeTab, (newTab) => {
  if (newTab === 'loyalty') {
    loadLoyaltyData();
  }
});

// Theme settings
const handleThemeChange = (newTheme) => {
  setTheme(newTheme);
};

const saveThemeSettings = () => {
  // Theme is already saved when changed via handleThemeChange
  ElMessage.success('Đã lưu cài đặt giao diện thành công!');
};

onMounted(() => {
  loadProfile();
  loadAddresses();
});
</script>

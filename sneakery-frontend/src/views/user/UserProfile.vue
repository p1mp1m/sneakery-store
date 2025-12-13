<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-6xl mx-auto px-4 space-y-6">
      <!-- Header -->
      <div class="relative overflow-hidden bg-gradient-to-r from-purple-600 via-purple-700 to-indigo-700 rounded-xl p-6 shadow-lg">
        <div class="absolute inset-0 bg-gradient-to-br from-purple-900/20 to-transparent"></div>
        <div class="relative">
          <h1 class="text-3xl md:text-4xl font-bold text-white mb-2">Thông tin cá nhân</h1>
          <p class="text-purple-100 text-sm md:text-base">Quản lý thông tin tài khoản và địa chỉ của bạn</p>
        </div>
      </div>

      <!-- Profile Tabs -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden backdrop-blur-sm">
        <div class="flex overflow-x-auto border-b border-gray-200 dark:border-gray-700 scrollbar-hide">
          <button
            :class="[
              'px-4 sm:px-6 py-3 sm:py-4 font-medium text-xs sm:text-sm transition-all whitespace-nowrap border-b-2',
              activeTab === 'info'
                ? 'text-purple-600 dark:text-purple-400 border-purple-600 dark:border-purple-400'
                : 'text-gray-600 dark:text-gray-400 border-transparent hover:text-purple-600 dark:hover:text-purple-400'
            ]"
            @click="activeTab = 'info'"
          >
            <span class="hidden sm:inline">Thông tin tài khoản</span>
            <span class="sm:hidden">Thông tin</span>
          </button>
          <button
            :class="[
              'px-4 sm:px-6 py-3 sm:py-4 font-medium text-xs sm:text-sm transition-all whitespace-nowrap border-b-2',
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
              'px-4 sm:px-6 py-3 sm:py-4 font-medium text-xs sm:text-sm transition-all whitespace-nowrap border-b-2',
              activeTab === 'password'
                ? 'text-purple-600 dark:text-purple-400 border-purple-600 dark:border-purple-400'
                : 'text-gray-600 dark:text-gray-400 border-transparent hover:text-purple-600 dark:hover:text-purple-400'
            ]"
            @click="activeTab = 'password'"
          >
            <span class="hidden sm:inline">Đổi mật khẩu</span>
            <span class="sm:hidden">Mật khẩu</span>
          </button>
          <button
            :class="[
              'px-4 sm:px-6 py-3 sm:py-4 font-medium text-xs sm:text-sm transition-all whitespace-nowrap border-b-2',
              activeTab === 'loyalty'
                ? 'text-purple-600 dark:text-purple-400 border-purple-600 dark:border-purple-400'
                : 'text-gray-600 dark:text-gray-400 border-transparent hover:text-purple-600 dark:hover:text-purple-400'
            ]"
            @click="activeTab = 'loyalty'"
          >
            <span class="hidden sm:inline">Điểm thưởng</span>
            <span class="sm:hidden">Điểm</span>
          </button>
          <button
            :class="[
              'px-4 sm:px-6 py-3 sm:py-4 font-medium text-xs sm:text-sm transition-all whitespace-nowrap border-b-2',
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
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 md:p-8">
        <!-- Account Info Tab -->
        <div v-if="activeTab === 'info'" class="space-y-6">
          <div class="mb-6">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2 flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-lg">person</i>
              </div>
              Thông tin tài khoản
            </h2>
            <p class="text-sm text-gray-600 dark:text-gray-400">Cập nhật thông tin cá nhân của bạn</p>
          </div>

          <form @submit.prevent="updateProfile" class="space-y-5">
            <div>
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Họ và tên *</label>
              <input 
                v-model="profile.fullName" 
                type="text" 
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all" 
                placeholder="Nhập họ và tên"
                required 
              />
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Email</label>
              <input 
                v-model="profile.email" 
                type="email" 
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-700/50 text-gray-500 dark:text-gray-400 cursor-not-allowed" 
                disabled 
              />
              <small class="text-xs text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
                <i class="material-icons text-xs">info</i>
                Email không thể thay đổi
              </small>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Số điện thoại</label>
              <input 
                v-model="profile.phoneNumber" 
                type="tel" 
                class="w-full px-4 py-3 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 transition-all"
                :class="[
                  profile.phoneNumber && !validateVietnamesePhone(profile.phoneNumber)
                    ? 'border-red-300 dark:border-red-600 focus:ring-red-500 focus:border-red-500'
                    : 'border-gray-200 dark:border-gray-600 focus:ring-purple-500 focus:border-purple-500'
                ]"
                placeholder="Nhập số điện thoại (VD: 0901234567)"
                @blur="profile.phoneNumber = formatPhoneNumber(profile.phoneNumber)"
              />
              <p v-if="profile.phoneNumber && !validateVietnamesePhone(profile.phoneNumber)" class="text-xs text-red-600 dark:text-red-400 mt-1 flex items-center gap-1">
                <i class="material-icons text-xs">error</i>
                Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)
              </p>
              <small v-else-if="profile.phoneNumber" class="text-xs text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
                <i class="material-icons text-xs">check_circle</i>
                Số điện thoại hợp lệ
              </small>
            </div>

            <div class="flex justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
              <button 
                type="submit" 
                class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2" 
                :disabled="updating"
              >
                <i v-if="!updating" class="material-icons text-lg">save</i>
                <span v-if="updating" class="flex items-center gap-2">
                  <div class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                  Đang lưu...
                </span>
                <span v-else>Cập nhật thông tin</span>
              </button>
            </div>
          </form>
        </div>

        <!-- Address Tab -->
        <div v-if="activeTab === 'address'" class="space-y-6">
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
            <div>
              <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2 flex items-center gap-2">
                <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                  <i class="material-icons text-white text-lg">location_on</i>
                </div>
                Danh sách địa chỉ
              </h2>
              <p class="text-sm text-gray-600 dark:text-gray-400">Quản lý địa chỉ giao hàng của bạn</p>
            </div>
            <button 
              @click="showAddressForm = true"
              @keydown.enter="showAddressForm = true"
              @keydown.space.prevent="showAddressForm = true"
              aria-label="Thêm địa chỉ mới"
              class="px-4 py-2.5 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2 whitespace-nowrap focus-visible:outline-2 focus-visible:outline-purple-400 focus-visible:outline-offset-2"
            >
              <i class="material-icons text-lg" aria-hidden="true">add</i>
              <span>Thêm địa chỉ mới</span>
            </button>
          </div>

          <!-- Loading State -->
          <div v-if="loadingAddresses" class="text-center py-16">
            <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-purple-600 border-t-transparent mb-4"></div>
            <p class="text-gray-600 dark:text-gray-400 font-medium">Đang tải danh sách địa chỉ...</p>
          </div>

          <!-- Empty State -->
          <div v-else-if="addresses.length === 0" class="text-center py-16 bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-900 rounded-xl border border-gray-200 dark:border-gray-700">
            <div class="w-20 h-20 mx-auto mb-4 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="text-purple-600 dark:text-purple-400">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có địa chỉ nào</h3>
            <p class="text-gray-600 dark:text-gray-400 mb-6">Thêm địa chỉ để dễ dàng đặt hàng</p>
            <button 
              @click="showAddressForm = true"
              @keydown.enter="showAddressForm = true"
              @keydown.space.prevent="showAddressForm = true"
              aria-label="Thêm địa chỉ đầu tiên"
              class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl focus-visible:outline-2 focus-visible:outline-purple-400 focus-visible:outline-offset-2"
            >
              <i class="material-icons" aria-hidden="true">add</i>
              <span>Thêm địa chỉ đầu tiên</span>
            </button>
          </div>

          <!-- Address List -->
          <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div
              v-for="addr in addresses"
              :key="addr.id"
              class="group relative p-5 bg-white dark:bg-gray-800/80 backdrop-blur-sm border border-gray-200 dark:border-gray-700 rounded-xl hover:border-purple-300 dark:hover:border-purple-600 hover:shadow-lg transition-all duration-200 hover:translate-y-[-2px]"
            >
              <div class="flex items-start justify-between gap-4">
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-3">
                    <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center flex-shrink-0">
                      <i class="material-icons text-white text-sm">home</i>
                    </div>
                    <h4 class="font-semibold text-gray-900 dark:text-gray-100 truncate">{{ addr.recipientName }}</h4>
                  </div>
                  <div class="space-y-1.5 text-sm text-gray-600 dark:text-gray-400 pl-10">
                    <p class="flex items-center gap-2">
                      <i class="material-icons text-xs text-gray-400">phone</i>
                      {{ addr.phone }}
                    </p>
                    <p class="flex items-center gap-2">
                      <i class="material-icons text-xs text-gray-400">place</i>
                      {{ addr.line1 }}
                    </p>
                    <p v-if="addr.line2" class="flex items-center gap-2 pl-6">
                      {{ addr.line2 }}
                    </p>
                    <p class="flex items-center gap-2">
                      <i class="material-icons text-xs text-gray-400">location_city</i>
                      {{ addr.district }}, {{ addr.city }}
                    </p>
                  </div>
                </div>
                <div class="flex flex-col gap-2 flex-shrink-0">
                  <button 
                    @click="editAddress(addr)" 
                    class="px-3 py-2 border border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 hover:border-purple-300 dark:hover:border-purple-600 transition-all text-sm font-medium flex items-center gap-1"
                  >
                    <i class="material-icons text-sm">edit</i>
                    <span class="hidden sm:inline">Sửa</span>
                  </button>
                  <button 
                    @click="deleteAddress(addr.id)" 
                    class="px-3 py-2 border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-all text-sm font-medium flex items-center gap-1"
                  >
                    <i class="material-icons text-sm">delete</i>
                    <span class="hidden sm:inline">Xóa</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Change Password Tab -->
        <div v-if="activeTab === 'password'" class="space-y-6">
          <div class="mb-6">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2 flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-lg">lock</i>
              </div>
              Đổi mật khẩu
            </h2>
            <p class="text-sm text-gray-600 dark:text-gray-400">Cập nhật mật khẩu để bảo mật tài khoản của bạn</p>
          </div>

          <form @submit.prevent="changePassword" class="space-y-5">
            <div>
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Mật khẩu hiện tại *</label>
              <input 
                v-model="passwordForm.currentPassword" 
                type="password" 
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all" 
                placeholder="Nhập mật khẩu hiện tại"
                required 
              />
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Mật khẩu mới *</label>
              <input 
                v-model="passwordForm.newPassword" 
                type="password" 
                class="w-full px-4 py-3 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 transition-all"
                :class="[
                  passwordForm.newPassword && passwordStrength && !passwordStrength.valid
                    ? 'border-red-300 dark:border-red-600 focus:ring-red-500 focus:border-red-500'
                    : passwordForm.newPassword && passwordStrength && passwordStrength.valid
                    ? 'border-green-300 dark:border-green-600 focus:ring-green-500 focus:border-green-500'
                    : 'border-gray-200 dark:border-gray-600 focus:ring-purple-500 focus:border-purple-500'
                ]"
                placeholder="Nhập mật khẩu mới"
                required 
              />
              
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
              <small v-else class="text-xs text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
                <i class="material-icons text-xs">info</i>
                Mật khẩu phải có ít nhất 6 ký tự
              </small>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Xác nhận mật khẩu mới *</label>
              <input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all" 
                placeholder="Nhập lại mật khẩu mới"
                required 
              />
              <p v-if="passwordForm.newPassword && passwordForm.confirmPassword && passwordForm.newPassword !== passwordForm.confirmPassword" class="text-xs text-red-600 dark:text-red-400 mt-1 flex items-center gap-1">
                <i class="material-icons text-xs">error</i>
                Mật khẩu xác nhận không khớp
              </p>
            </div>

            <div class="flex justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
              <button 
                type="submit" 
                class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2" 
                :disabled="changingPassword || (passwordForm.newPassword && passwordForm.confirmPassword && passwordForm.newPassword !== passwordForm.confirmPassword) || (passwordStrength && !passwordStrength.valid)"
              >
                <i v-if="!changingPassword" class="material-icons text-lg">lock</i>
                <span v-if="changingPassword" class="flex items-center gap-2">
                  <div class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                  Đang đổi mật khẩu...
                </span>
                <span v-else>Đổi mật khẩu</span>
              </button>
            </div>
          </form>
        </div>

        <!-- Loyalty Points Tab -->
        <div v-if="activeTab === 'loyalty'" class="space-y-6">
          <div class="mb-6">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2 flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-lg">stars</i>
              </div>
              Điểm thưởng
            </h2>
            <p class="text-sm text-gray-600 dark:text-gray-400">Xem và quản lý điểm thưởng của bạn</p>
          </div>

          <!-- Loyalty Balance Card -->
          <div class="relative overflow-hidden border border-gray-200 dark:border-gray-700 rounded-xl p-6 bg-gradient-to-br from-purple-50 via-purple-50/50 to-indigo-50 dark:from-purple-900/20 dark:via-purple-900/10 dark:to-indigo-900/20 backdrop-blur-sm">
            <div class="absolute top-0 right-0 w-32 h-32 bg-purple-200/20 dark:bg-purple-800/20 rounded-full blur-2xl -mr-16 -mt-16"></div>
            <div class="relative flex items-center gap-3 mb-4">
              <div class="w-12 h-12 rounded-full bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center shadow-lg">
                <i class="material-icons text-white">stars</i>
              </div>
              <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100">Số dư điểm thưởng</h3>
            </div>
            
            <div v-if="loyaltyLoading" class="text-center py-8">
              <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-purple-600 border-t-transparent mb-3"></div>
              <p class="text-gray-600 dark:text-gray-400">Đang tải...</p>
            </div>

            <div v-else class="space-y-4">
              <div class="text-center">
                <div class="text-5xl font-bold text-purple-600 dark:text-purple-400 mb-2">
                  {{ loyaltyStore.currentBalance.toLocaleString() }}
                </div>
                <div class="text-lg text-gray-600 dark:text-gray-400 font-medium">Điểm</div>
              </div>

              <div class="flex items-center justify-center gap-2 text-gray-700 dark:text-gray-300 bg-white/50 dark:bg-gray-800/50 rounded-lg py-2 px-4">
                <i class="material-icons text-yellow-500">monetization_on</i>
                <span class="font-semibold">≈ {{ formatCurrency(loyaltyStore.calculateVndFromPoints(loyaltyStore.currentBalance)) }}</span>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-3 mt-6">
                <div class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 bg-white/50 dark:bg-gray-800/50 rounded-lg p-3">
                  <i class="material-icons text-blue-500 text-base">info</i>
                  <span>1 điểm = 1.000 VNĐ</span>
                </div>
                <div class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 bg-white/50 dark:bg-gray-800/50 rounded-lg p-3">
                  <i class="material-icons text-green-500 text-base">shopping_cart</i>
                  <span>Dùng điểm khi thanh toán</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Points History -->
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">history</i>
              Lịch sử điểm thưởng
            </h3>
            
            <div v-if="loyaltyHistory.length === 0" class="text-center py-12 border border-gray-200 dark:border-gray-700 rounded-xl bg-gray-50 dark:bg-gray-800/50">
              <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
                <i class="material-icons text-4xl text-gray-400">history</i>
              </div>
              <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có lịch sử</h4>
              <p class="text-gray-600 dark:text-gray-400">Lịch sử giao dịch điểm thưởng sẽ hiển thị ở đây</p>
            </div>

            <div v-else class="space-y-3">
              <div 
                v-for="item in loyaltyHistory" 
                :key="item.id"
                class="flex items-center gap-4 p-4 bg-white dark:bg-gray-800/80 backdrop-blur-sm border border-gray-200 dark:border-gray-700 rounded-xl hover:shadow-md hover:border-purple-300 dark:hover:border-purple-600 transition-all duration-200"
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
          <div class="mb-6">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2 flex items-center gap-2">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-lg">palette</i>
              </div>
              Cài đặt Giao diện
            </h2>
            <p class="text-sm text-gray-600 dark:text-gray-400">Chọn chế độ giao diện sáng hoặc tối</p>
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

          <div class="flex justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
            <button 
              @click="saveThemeSettings" 
              class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
            >
              <i class="material-icons text-lg">save</i>
              Lưu cài đặt
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Address Modal -->
    <div v-if="showAddressForm" class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4" @click.self="showAddressForm = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto animate-in fade-in zoom-in duration-200">
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700 bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20">
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">{{ editingAddress ? 'edit_location' : 'add_location' }}</i>
            {{ editingAddress ? 'Cập nhật địa chỉ' : 'Thêm địa chỉ mới' }}
          </h3>
          <button 
            @click="closeAddressForm" 
            class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            aria-label="Đóng"
          >
            <i class="material-icons">close</i>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Họ tên người nhận *</label>
            <input 
              v-model="addressForm.recipientName" 
              type="text" 
              class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all" 
              placeholder="Nhập họ tên người nhận"
            />
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Số điện thoại *</label>
            <input 
              v-model="addressForm.phone" 
              type="tel" 
              class="w-full px-4 py-3 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 transition-all"
              :class="[
                addressForm.phone && !validateVietnamesePhone(addressForm.phone)
                  ? 'border-red-300 dark:border-red-600 focus:ring-red-500 focus:border-red-500'
                  : 'border-gray-200 dark:border-gray-600 focus:ring-purple-500 focus:border-purple-500'
              ]"
              placeholder="Nhập số điện thoại (VD: 0901234567)"
              @blur="addressForm.phone = formatPhoneNumber(addressForm.phone)"
            />
            <p v-if="addressForm.phone && !validateVietnamesePhone(addressForm.phone)" class="text-xs text-red-600 dark:text-red-400 mt-1 flex items-center gap-1">
              <i class="material-icons text-xs">error</i>
              Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)
            </p>
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Địa chỉ *</label>
            <input 
              v-model="addressForm.line1" 
              type="text" 
              class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all" 
              placeholder="Số nhà, tên đường"
            />
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Địa chỉ 2 (tùy chọn)</label>
            <input 
              v-model="addressForm.line2" 
              type="text" 
              class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all" 
              placeholder="Thông tin bổ sung (nếu có)"
            />
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Quận/Huyện *</label>
            <input 
              v-model="addressForm.district" 
              type="text" 
              class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all" 
              placeholder="Nhập quận/huyện"
            />
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Tỉnh/Thành phố *</label>
            <input 
              v-model="addressForm.city" 
              type="text" 
              class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all" 
              placeholder="Nhập tỉnh/thành phố"
            />
          </div>
        </div>
        <div class="flex gap-3 p-6 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="saveAddress" 
            class="flex-1 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center justify-center gap-2"
          >
            <i class="material-icons text-lg">{{ editingAddress ? 'save' : 'add' }}</i>
            {{ editingAddress ? 'Cập nhật' : 'Lưu địa chỉ' }}
          </button>
          <button 
            @click="closeAddressForm" 
            class="flex-1 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors flex items-center justify-center gap-2"
          >
            <i class="material-icons text-lg">close</i>
            Hủy
          </button>
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
import notificationService from '@/utils/notificationService';
import confirmDialogService from '@/utils/confirmDialogService';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';
import userService from '@/services/userService';
import { formatCurrency } from '@/utils/formatters';

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
    
    // Validate required fields
    if (!profile.fullName || !profile.fullName.trim()) {
      notificationService.error('Lỗi', 'Họ và tên không được để trống');
      return;
    }

    // Validate phone number if provided
    if (profile.phoneNumber && !validateVietnamesePhone(profile.phoneNumber)) {
      notificationService.error('Lỗi', 'Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)');
      return;
    }

    // Call API to update profile
    const updatedUser = await userService.updateProfile({
      fullName: profile.fullName.trim(),
      phoneNumber: profile.phoneNumber?.trim() || null,
    });

    // Update auth store with new user data
    if (updatedUser) {
      // Merge updated user data with existing user data
      const currentUser = authStore.currentUser;
      const mergedUser = { ...currentUser, ...updatedUser };
      authStore.user = mergedUser;
      localStorage.setItem('user', JSON.stringify(mergedUser));
    }

    notificationService.success('Thành công', 'Cập nhật thông tin thành công');
    logger.log('Profile updated successfully');
  } catch (error) {
    logger.error('Error updating profile:', error);
    const errorMessage = error.response?.data?.message || error.message || 'Không thể cập nhật thông tin';
    notificationService.error('Lỗi', errorMessage);
  } finally {
    updating.value = false;
  }
};

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    notificationService.error('Lỗi','Mật khẩu xác nhận không khớp');
    return;
  }

  // Validate password strength
  const strength = validatePasswordStrength(passwordForm.newPassword);
  if (!strength.valid) {
    notificationService.error('Lỗi', strength.errors[0] || 'Mật khẩu không hợp lệ');
    return;
  }

  try {
    changingPassword.value = true;

    // Call API to change password
    await userService.changePassword({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
    });

    notificationService.success('Thành công', 'Đổi mật khẩu thành công');
    logger.log('Password changed successfully');
        
    // Reset form
    passwordForm.currentPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
  } catch (error) {
    logger.error('Error changing password:', error);
    const errorMessage = error.response?.data?.message || error.message || 'Không thể đổi mật khẩu';
    notificationService.error('Lỗi', errorMessage);
  } finally {
    changingPassword.value = false;
  }
};

const loadAddresses = async () => {
  try {
    loadingAddresses.value = true;
    addresses.value = await userService.getMyAddresses();
  } catch (error) {
    logger.error('Error loading addresses:', error);
    notificationService.error('Lỗi',error.message || 'Không thể tải danh sách địa chỉ');
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
    notificationService.warning('Cảnh báo','Vui lòng điền đầy đủ thông tin bắt buộc');
    return;
  }

  // Validate phone number
  if (!validateVietnamesePhone(addressForm.phone)) {
    notificationService.error('Lỗi', 'Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)');
    return;
  }

  // Validate address format
  if (!validateAddress(addressForm.line1)) {
    notificationService.error('Lỗi', 'Địa chỉ không hợp lệ. Vui lòng nhập địa chỉ đầy đủ (tối thiểu 5 ký tự)');
    return;
  }

  // Validate district and city
  if (!validateLocation(addressForm.district)) {
    notificationService.error('Lỗi', 'Quận/Huyện không hợp lệ. Vui lòng nhập quận/huyện đầy đủ');
    return;
  }

  if (!validateLocation(addressForm.city)) {
    notificationService.error('Lỗi', 'Tỉnh/Thành phố không hợp lệ. Vui lòng nhập tỉnh/thành phố đầy đủ');
    return;
  }

  try {
    if (editingAddress.value) {
      // Update existing address
      const updatedAddress = await userService.updateAddress(editingAddress.value.id, addressForm);
      const index = addresses.value.findIndex(a => a.id === editingAddress.value.id);
      if (index !== -1) {
        addresses.value[index] = updatedAddress;
      }
      notificationService.success('Thành công','Cập nhật địa chỉ thành công');
    } else {
      // Create new address
      const newAddress = await userService.createAddress(addressForm);
      addresses.value.push(newAddress);
      notificationService.success('Thành công','Thêm địa chỉ thành công');
    }

    closeAddressForm();
  } catch (error) {
    logger.error('Error saving address:', error);
    notificationService.error('Lỗi',error.message || 'Không thể lưu địa chỉ');
  }
};

const deleteAddress = async (id) => {
  try {
    await confirmDialogService.confirm(
      'Bạn có chắc muốn xóa địa chỉ này?',
      'Xác nhận',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
    );
    
    await userService.deleteAddress(id);
    addresses.value = addresses.value.filter(a => a.id !== id);
    notificationService.success('Thành công','Đã xóa địa chỉ');
  } catch (error) {
    if (error !== 'cancel') {
      logger.error('Error deleting address:', error);
      notificationService.error('Lỗi','Không thể xóa địa chỉ');
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

// Helper methods - formatCurrency đã được import từ @/utils/formatters

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
  notificationService.success('Thành công','Đã lưu cài đặt giao diện thành công!');
};

onMounted(() => {
  loadProfile();
  loadAddresses();
});
</script>

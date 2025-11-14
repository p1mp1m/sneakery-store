<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-7xl mx-auto px-4 space-y-6">
      <!-- Page Header -->
      <div class="relative overflow-hidden bg-gradient-to-r from-purple-600 via-purple-700 to-indigo-700 rounded-xl p-6 shadow-lg">
        <div class="absolute inset-0 bg-gradient-to-br from-purple-900/20 to-transparent"></div>
        <div class="relative">
          <h1 class="text-3xl md:text-4xl font-bold text-white mb-2 flex items-center gap-3">
            <div class="w-10 h-10 rounded-lg bg-white/20 backdrop-blur-sm flex items-center justify-center">
              <i class="material-icons text-white">payment</i>
            </div>
            Thanh toán
          </h1>
          <p class="text-purple-100 text-sm md:text-base">Hoàn tất đơn hàng của bạn</p>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="space-y-6" role="status" aria-live="polite">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div class="lg:col-span-2 space-y-6">
            <LoadingSkeleton type="custom" :lines="8" />
            <LoadingSkeleton type="custom" :lines="6" />
          </div>
          <div>
            <LoadingSkeleton type="custom" :lines="10" />
          </div>
        </div>
        <span class="sr-only">Đang tải thông tin thanh toán</span>
      </div>

      <!-- Checkout Content -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Left: Multi-Step Form -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Step Progress Indicator -->
          <div class="bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-4 sm:p-6">
            <div class="flex items-center justify-between gap-2 sm:gap-4 overflow-x-auto scrollbar-hide pb-2">
              <div
                v-for="(step, index) in steps"
                :key="step.id"
                :class="[
                  'flex items-center gap-2 sm:gap-3 cursor-pointer transition-all flex-shrink-0',
                  currentStep === index + 1 ? 'text-purple-600 dark:text-purple-400' : currentStep > index + 1 ? 'text-green-600 dark:text-green-400' : 'text-gray-400 dark:text-gray-500'
                ]"
                @click="goToStep(index + 1)"
              >
                <div :class="[
                  'w-10 h-10 sm:w-12 sm:h-12 rounded-full flex items-center justify-center font-semibold border-2 transition-all shadow-sm',
                  currentStep === index + 1 
                    ? 'bg-gradient-to-br from-purple-500 to-purple-600 text-white border-purple-600 shadow-purple-500/50' 
                    : currentStep > index + 1 
                    ? 'bg-gradient-to-br from-green-500 to-green-600 text-white border-green-600 shadow-green-500/50' 
                    : 'bg-gray-100 dark:bg-gray-700 text-gray-400 border-gray-300 dark:border-gray-600'
                ]">
                  <i v-if="currentStep > index + 1" class="material-icons text-base sm:text-lg">check</i>
                  <span v-else class="text-base sm:text-lg">{{ index + 1 }}</span>
                </div>
                <div class="hidden sm:block min-w-0">
                  <div class="font-semibold text-xs sm:text-sm whitespace-nowrap">{{ step.title }}</div>
                  <div class="text-xs text-gray-500 dark:text-gray-400 hidden md:block">{{ step.description }}</div>
                </div>
                <div v-if="index < steps.length - 1" :class="[
                  'hidden lg:block w-6 sm:w-8 h-0.5 mx-1 sm:mx-2 transition-all',
                  currentStep > index + 1 ? 'bg-green-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"></div>
              </div>
            </div>
          </div>

          <!-- Step 1: Shipping Address -->
          <div v-show="currentStep === 1" class="bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 md:p-8">
            <h2 class="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-lg">location_on</i>
              </div>
              Địa chỉ giao hàng
            </h2>

            <!-- Guest Address Form -->
            <div v-if="isGuest" class="space-y-5">
              <div>
                <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Họ tên người nhận *</label>
                <input
                  v-model="newAddress.recipientName"
                  type="text"
                  class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                  placeholder="Nguyễn Văn A"
                  required
                />
              </div>
              <div>
                <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Số điện thoại *</label>
                <input
                  v-model="newAddress.phone"
                  type="tel"
                  class="w-full px-4 py-3 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 transition-all"
                  :class="[
                    newAddress.phone && !validateVietnamesePhone(newAddress.phone)
                      ? 'border-red-300 dark:border-red-600 focus:ring-red-500 focus:border-red-500'
                      : 'border-gray-200 dark:border-gray-600 focus:ring-purple-500 focus:border-purple-500'
                  ]"
                  placeholder="0912345678"
                  required
                  @blur="newAddress.phone = formatPhoneNumber(newAddress.phone)"
                />
                <p v-if="newAddress.phone && !validateVietnamesePhone(newAddress.phone)" class="text-xs text-red-600 dark:text-red-400 mt-1 flex items-center gap-1">
                  <i class="material-icons text-xs">error</i>
                  Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)
                </p>
              </div>
              <div>
                <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Email (không bắt buộc)</label>
                <input
                  v-model="newAddress.email"
                  type="email"
                  class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                  placeholder="email@example.com"
                />
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
                  <i class="material-icons text-xs">info</i>
                  Để nhận thông tin đơn hàng qua email
                </p>
              </div>
              <div>
                <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Địa chỉ *</label>
                <input
                  v-model="newAddress.line1"
                  type="text"
                  class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                  placeholder="123 Đường ABC"
                  required
                />
              </div>
              <div>
                <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Địa chỉ bổ sung (không bắt buộc)</label>
                <input
                  v-model="newAddress.line2"
                  type="text"
                  class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                  placeholder="Căn hộ, tòa nhà..."
                />
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Quận/Huyện *</label>
                  <input
                    v-model="newAddress.district"
                    type="text"
                    class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                    placeholder="Quận 1"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Tỉnh/Thành phố *</label>
                  <input
                    v-model="newAddress.city"
                    type="text"
                    class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                    placeholder="TP. Hồ Chí Minh"
                    required
                  />
                </div>
              </div>
            </div>

            <!-- Authenticated User Address Selection -->
            <div v-else-if="addresses.length === 0" class="text-center py-16 bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-900 rounded-xl border border-gray-200 dark:border-gray-700">
              <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
                <i class="material-icons text-4xl text-purple-600 dark:text-purple-400">location_on</i>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có địa chỉ giao hàng</h3>
              <p class="text-gray-600 dark:text-gray-400 mb-6">Thêm địa chỉ để tiếp tục đặt hàng</p>
              <button 
                @click="showAddressForm = true" 
                class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02]"
              >
                <i class="material-icons text-lg">add</i>
                Thêm địa chỉ mới
              </button>
            </div>

            <div v-else class="space-y-4">
              <div
                v-for="addr in addresses"
                :key="addr.id"
                :class="[
                  'p-5 rounded-xl border-2 cursor-pointer transition-all hover:shadow-md',
                  selectedAddress === addr.id
                    ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20 shadow-md'
                    : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
                ]"
                @click="selectedAddress = addr.id"
              >
                <div class="flex items-start gap-4">
                  <div :class="[
                    'w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-1 transition-all',
                    selectedAddress === addr.id
                      ? 'bg-purple-600 border-purple-600 text-white shadow-sm'
                      : 'border-gray-300 dark:border-gray-600'
                  ]">
                    <i v-if="selectedAddress === addr.id" class="material-icons text-xs">check</i>
                  </div>
                  <div class="flex-1">
                    <h4 class="font-semibold text-gray-900 dark:text-gray-100 mb-2">{{ addr.recipientName }}</h4>
                    <div class="space-y-1.5 text-sm text-gray-600 dark:text-gray-400">
                      <div class="flex items-center gap-2">
                        <i class="material-icons text-xs text-gray-400">phone</i>
                        {{ addr.phone }}
                      </div>
                      <div class="flex items-center gap-2">
                        <i class="material-icons text-xs text-gray-400">place</i>
                        {{ addr.line1 }}
                      </div>
                      <div v-if="addr.line2" class="flex items-center gap-2 pl-6">
                        {{ addr.line2 }}
                      </div>
                      <div class="flex items-center gap-2">
                        <i class="material-icons text-xs text-gray-400">location_city</i>
                        {{ addr.district }}, {{ addr.city }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <button 
                @click="showAddressForm = true" 
                class="w-full flex items-center justify-center gap-2 px-6 py-3 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-xl text-gray-600 dark:text-gray-400 hover:border-purple-400 dark:hover:border-purple-600 hover:text-purple-600 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20 transition-all"
              >
                <i class="material-icons text-lg">add</i>
                Thêm địa chỉ mới
              </button>
            </div>

            <div class="flex justify-end mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
              <button
                @click="nextStep"
                :disabled="isGuest ? (!newAddress.recipientName || !newAddress.phone || !newAddress.line1 || !newAddress.district || !newAddress.city) : !selectedAddress"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02]"
              >
                <span>Tiếp tục</span>
                <i class="material-icons text-lg">arrow_forward</i>
              </button>
            </div>
          </div>

          <!-- Step 2: Payment Method -->
          <div v-show="currentStep === 2" class="bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 md:p-8">
            <h2 class="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-lg">payment</i>
              </div>
              Phương thức thanh toán
            </h2>

            <div class="space-y-4 mb-6">
              <div
                :class="[
                  'p-5 rounded-xl border-2 cursor-pointer transition-all hover:shadow-md',
                  paymentMethod === 'cod'
                    ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20 shadow-md'
                    : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
                ]"
                @click="paymentMethod = 'cod'"
              >
                <div class="flex items-start gap-4">
                  <div :class="[
                    'w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-1 transition-all',
                    paymentMethod === 'cod'
                      ? 'bg-purple-600 border-purple-600 text-white shadow-sm'
                      : 'border-gray-300 dark:border-gray-600'
                  ]">
                    <i v-if="paymentMethod === 'cod'" class="material-icons text-xs">check</i>
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center gap-3 mb-3">
                      <div class="w-12 h-12 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center shadow-sm">
                        <i class="material-icons text-white text-xl">money</i>
                      </div>
                      <div>
                        <h4 class="font-semibold text-gray-900 dark:text-gray-100">Thanh toán khi nhận hàng (COD)</h4>
                        <p class="text-sm text-gray-600 dark:text-gray-400">Thanh toán bằng tiền mặt khi nhận hàng</p>
                      </div>
                    </div>
                    <div class="flex flex-wrap gap-2">
                      <span class="px-2.5 py-1 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded-lg text-xs font-medium flex items-center gap-1">
                        <i class="material-icons text-xs">check_circle</i>
                        Tiện lợi
                      </span>
                      <span class="px-2.5 py-1 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded-lg text-xs font-medium flex items-center gap-1">
                        <i class="material-icons text-xs">check_circle</i>
                        Không mất phí
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div
                :class="[
                  'p-5 rounded-xl border-2 cursor-pointer transition-all hover:shadow-md',
                  paymentMethod === 'online'
                    ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20 shadow-md'
                    : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
                ]"
                @click="paymentMethod = 'online'"
              >
                <div class="flex items-start gap-4">
                  <div :class="[
                    'w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-1 transition-all',
                    paymentMethod === 'online'
                      ? 'bg-purple-600 border-purple-600 text-white shadow-sm'
                      : 'border-gray-300 dark:border-gray-600'
                  ]">
                    <i v-if="paymentMethod === 'online'" class="material-icons text-xs">check</i>
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center gap-3 mb-3">
                      <div class="w-12 h-12 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center shadow-sm">
                        <i class="material-icons text-white text-xl">credit_card</i>
                      </div>
                      <div>
                        <h4 class="font-semibold text-gray-900 dark:text-gray-100">Thanh toán trực tuyến</h4>
                        <p class="text-sm text-gray-600 dark:text-gray-400">Thanh toán qua VNPay, MoMo, thẻ ATM/Visa...</p>
                      </div>
                    </div>
                    <div class="flex flex-wrap gap-2">
                      <span class="px-2.5 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 rounded-lg text-xs font-medium flex items-center gap-1">
                        <i class="material-icons text-xs">check_circle</i>
                        Nhanh chóng
                      </span>
                      <span class="px-2.5 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 rounded-lg text-xs font-medium flex items-center gap-1">
                        <i class="material-icons text-xs">check_circle</i>
                        Bảo mật
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="flex justify-between mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
              <button 
                @click="prevStep" 
                class="flex items-center gap-2 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-all"
              >
                <i class="material-icons text-lg">arrow_back</i>
                Quay lại
              </button>
              <button 
                @click="nextStep" 
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02]"
              >
                <span>Tiếp tục</span>
                <i class="material-icons text-lg">arrow_forward</i>
              </button>
            </div>
          </div>

          <!-- Step 3: Review & Confirm -->
          <div v-show="currentStep === 3" class="bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 md:p-8">
            <h2 class="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-lg">check_circle</i>
              </div>
              Xác nhận đơn hàng
            </h2>

            <div class="space-y-4 mb-6">
              <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-5 bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-700/50 dark:to-gray-800/50">
                <div class="flex items-center justify-between mb-3">
                  <h3 class="font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                    <i class="material-icons text-purple-600 dark:text-purple-400 text-base">location_on</i>
                    Địa chỉ giao hàng
                  </h3>
                  <button 
                    @click="currentStep = 1" 
                    class="text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 text-sm font-medium transition-colors flex items-center gap-1"
                  >
                    <i class="material-icons text-sm">edit</i>
                    Sửa
                  </button>
                </div>
                <div v-if="isGuest" class="text-sm text-gray-600 dark:text-gray-400 space-y-1.5">
                  <div><strong class="text-gray-900 dark:text-gray-100">{{ newAddress.recipientName }}</strong></div>
                  <div class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">phone</i>
                    {{ newAddress.phone }}
                  </div>
                  <div v-if="newAddress.email" class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">email</i>
                    {{ newAddress.email }}
                  </div>
                  <div class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">place</i>
                    {{ newAddress.line1 }}
                  </div>
                  <div v-if="newAddress.line2" class="pl-6">{{ newAddress.line2 }}</div>
                  <div class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">location_city</i>
                    {{ newAddress.district }}, {{ newAddress.city }}
                  </div>
                </div>
                <div v-else-if="selectedAddressData" class="text-sm text-gray-600 dark:text-gray-400 space-y-1.5">
                  <div><strong class="text-gray-900 dark:text-gray-100">{{ selectedAddressData.recipientName }}</strong></div>
                  <div class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">phone</i>
                    {{ selectedAddressData.phone }}
                  </div>
                  <div class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">place</i>
                    {{ selectedAddressData.line1 }}
                  </div>
                  <div v-if="selectedAddressData.line2" class="pl-6">{{ selectedAddressData.line2 }}</div>
                  <div class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">location_city</i>
                    {{ selectedAddressData.district }}, {{ selectedAddressData.city }}
                  </div>
                </div>
              </div>

              <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-5 bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-700/50 dark:to-gray-800/50">
                <div class="flex items-center justify-between mb-3">
                  <h3 class="font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                    <i class="material-icons text-purple-600 dark:text-purple-400 text-base">payment</i>
                    Phương thức thanh toán
                  </h3>
                  <button 
                    @click="currentStep = 2" 
                    class="text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 text-sm font-medium transition-colors flex items-center gap-1"
                  >
                    <i class="material-icons text-sm">edit</i>
                    Sửa
                  </button>
                </div>
                <div class="text-sm text-gray-600 dark:text-gray-400">
                  <div class="mb-1">
                    <strong class="text-gray-900 dark:text-gray-100" v-if="paymentMethod === 'cod'">Thanh toán khi nhận hàng (COD)</strong>
                    <strong class="text-gray-900 dark:text-gray-100" v-else>Thanh toán trực tuyến</strong>
                  </div>
                  <div v-if="paymentMethod === 'cod'" class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">money</i>
                    Thanh toán bằng tiền mặt khi nhận hàng
                  </div>
                  <div v-else class="flex items-center gap-1">
                    <i class="material-icons text-xs text-gray-400">credit_card</i>
                    Thanh toán qua VNPay, MoMo, thẻ ATM/Visa...
                  </div>
                </div>
              </div>

              <!-- Coupon Code Section -->
              <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-5 bg-white dark:bg-gray-800/50">
                <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-3 flex items-center gap-2">
                  <i class="material-icons text-purple-600 dark:text-purple-400 text-base">local_offer</i>
                  Mã giảm giá (không bắt buộc)
                </h3>
                <div class="flex gap-2 mb-2">
                  <select
                    v-model="selectedCouponCode"
                    @change="onCouponSelected"
                    class="flex-1 px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all max-w-full text-sm truncate"
                    :disabled="couponApplied || loadingActiveCoupons"
                    style="max-width: 100%;"
                  >
                    <option value="">-- Chọn mã giảm giá --</option>
                    <option
                      v-for="coupon in activeCoupons"
                      :key="coupon.id"
                      :value="coupon.code"
                      :disabled="coupon.minOrderAmount && cart?.subTotal < coupon.minOrderAmount"
                      :title="getCouponFullText(coupon)"
                    >
                      {{ getCouponDisplayText(coupon) }}
                    </option>
                  </select>
                  <button
                    v-if="couponApplied"
                    @click="removeCoupon"
                    class="px-4 py-3 bg-red-600 text-white rounded-lg font-semibold hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
                  >
                    <i class="material-icons text-lg">close</i>
                    <span>Xóa</span>
                  </button>
                </div>
                <div v-if="loadingActiveCoupons" class="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400 mt-2">
                  <div class="animate-spin rounded-full h-4 w-4 border-2 border-purple-500 border-t-transparent"></div>
                  Đang tải danh sách mã giảm giá...
                </div>
                <div v-if="!loadingActiveCoupons && activeCoupons.length === 0" class="text-xs text-gray-500 dark:text-gray-400 mt-2">
                  Hiện không có mã giảm giá nào đang hoạt động
                </div>
                <div v-if="couponError" class="text-sm text-red-600 dark:text-red-400 mt-2 flex items-center gap-1 p-2 bg-red-50 dark:bg-red-900/20 rounded-lg">
                  <i class="material-icons text-base">error</i>
                  {{ couponError }}
                </div>
                <div v-if="appliedCoupon && couponDiscountAmount > 0" class="text-sm text-green-600 dark:text-green-400 mt-2 flex items-center gap-1 p-2 bg-green-50 dark:bg-green-900/20 rounded-lg">
                  <i class="material-icons text-base">check_circle</i>
                  Đã áp dụng mã giảm giá "{{ appliedCoupon.code }}": -{{ formatPrice(couponDiscountAmount) }}
                </div>
              </div>

              <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-5 bg-white dark:bg-gray-800/50">
                <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-3 flex items-center gap-2">
                  <i class="material-icons text-purple-600 dark:text-purple-400 text-base">note</i>
                  Ghi chú (không bắt buộc)
                </h3>
                <textarea
                  v-model="notes"
                  placeholder="Nhập ghi chú cho đơn hàng (nếu có)..."
                  class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all resize-none"
                  rows="4"
                ></textarea>
              </div>
            </div>

            <div class="flex justify-between pt-6 border-t border-gray-200 dark:border-gray-700">
              <button 
                @click="prevStep" 
                class="flex items-center gap-2 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors"
              >
                <i class="material-icons text-lg">arrow_back</i>
                Quay lại
              </button>
              <button
                @click="handleCheckout"
                :disabled="processing"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02]"
              >
                <div v-if="processing" class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                <i v-else class="material-icons text-lg">check_circle</i>
                <span>{{ processing ? 'Đang xử lý...' : 'Xác nhận đặt hàng' }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Right: Order Summary -->
        <div class="lg:col-span-1">
          <div class="bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 p-6 sticky top-24">
            <h2 class="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">
              <i class="material-icons text-purple-600 dark:text-purple-400">receipt_long</i>
              Đơn hàng ({{ cart?.totalItems || 0 }} sản phẩm)
            </h2>

            <!-- Cart Items -->
            <div class="space-y-3 mb-6 max-h-64 overflow-y-auto scrollbar-hide">
              <div
                v-for="item in cart?.items || []"
                :key="item.cartItemId"
                class="flex gap-3 p-3 bg-gray-50 dark:bg-gray-700/50 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
              >
                <div class="w-16 h-16 rounded-lg overflow-hidden bg-gray-100 dark:bg-gray-700 flex-shrink-0">
                  <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" class="w-full h-full object-cover" />
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-medium text-sm text-gray-900 dark:text-gray-100 truncate mb-1">{{ item.productName }}</p>
                  <div class="flex flex-wrap gap-1 mb-1">
                    <span class="px-1.5 py-0.5 bg-gray-200 dark:bg-gray-600 rounded text-xs text-gray-700 dark:text-gray-300">{{ item.size }}</span>
                    <span class="px-1.5 py-0.5 bg-gray-200 dark:bg-gray-600 rounded text-xs text-gray-700 dark:text-gray-300">{{ item.color }}</span>
                  </div>
                  <p class="text-xs text-gray-500 dark:text-gray-400">Số lượng: x{{ item.quantity }}</p>
                </div>
                <p class="font-semibold text-sm text-purple-600 dark:text-purple-400 flex-shrink-0">{{ formatPrice(item.totalPrice) }}</p>
              </div>
            </div>

            <!-- Price Breakdown -->
            <div class="space-y-3 mb-6">
                <!-- Subtotal -->
                <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                  <span class="flex items-center gap-2">
                    <i class="material-icons text-xs">inventory_2</i>
                    Tạm tính ({{ cart?.totalItems || 0 }} sản phẩm)
                  </span>
                  <span class="text-gray-900 dark:text-gray-100 font-semibold">{{ formatPrice(cart?.subTotal || 0) }}</span>
                </div>

                <!-- Coupon Discount -->
                <div v-if="appliedCoupon && couponDiscountAmount > 0" class="flex justify-between text-sm text-green-600 dark:text-green-400">
                  <span class="flex items-center gap-2">
                    <i class="material-icons text-xs">local_offer</i>
                    Giảm giá ({{ appliedCoupon.code }})
                  </span>
                  <span class="font-semibold">-{{ formatPrice(couponDiscountAmount) }}</span>
                </div>

                <!-- Loyalty Points Section (only for authenticated users) -->
              <div v-if="!isGuest" class="border-t border-gray-200 dark:border-gray-700 pt-3 mt-3">
                <div class="flex items-center justify-between mb-2">
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      id="use-points"
                      v-model="usePoints"
                      :disabled="!currentBalance || currentBalance === 0"
                      class="w-4 h-4 text-purple-600 rounded border-gray-300 focus:ring-purple-500"
                    />
                    <span class="text-sm font-medium text-gray-900 dark:text-gray-100">Sử dụng điểm thưởng</span>
                  </label>
                  <span class="text-sm text-purple-600 dark:text-purple-400 font-semibold">
                    {{ (currentBalance || 0).toLocaleString() }} điểm
                  </span>
                </div>

                <div v-if="usePoints" class="mt-3 space-y-2">
                  <div class="flex gap-2">
                    <input
                      type="number"
                      v-model.number="pointsToUse"
                      :max="maxPointsUsable"
                      :min="0"
                      class="flex-1 px-3 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                      placeholder="Nhập số điểm"
                    />
                    <button
                      @click="pointsToUse = maxPointsUsable"
                      class="px-3 py-2 bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400 rounded-lg text-xs font-medium hover:bg-purple-200 dark:hover:bg-purple-900/50 transition-colors"
                    >
                      Tối đa
                    </button>
                  </div>
                  <p class="text-xs text-gray-500 dark:text-gray-400">
                    Tối đa {{ maxPointsUsable.toLocaleString() }} điểm (≈ {{ formatPrice(maxPointsUsable * 1000) }})
                  </p>
                </div>
              </div>

                <!-- Loyalty Points Discount -->
                <div v-if="loyaltyDiscount > 0" class="flex justify-between text-sm text-green-600 dark:text-green-400">
                  <span class="flex items-center gap-2">
                    <i class="material-icons text-xs">stars</i>
                    Giảm giá từ điểm thưởng
                  </span>
                  <span class="font-semibold">-{{ formatPrice(loyaltyDiscount) }}</span>
                </div>

                <!-- Amount After Discount -->
                <div v-if="(appliedCoupon && couponDiscountAmount > 0) || loyaltyDiscount > 0" class="flex justify-between text-xs text-gray-500 dark:text-gray-500 pt-2 border-t border-gray-200 dark:border-gray-700">
                  <span class="italic">Sau giảm giá</span>
                  <span class="italic font-medium">{{ formatPrice((cart?.subTotal || 0) - couponDiscountAmount - loyaltyDiscount) }}</span>
                </div>

                <!-- VAT (10%) -->
                <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                  <span class="flex items-center gap-2">
                    <i class="material-icons text-xs">receipt</i>
                    VAT (10%)
                  </span>
                  <span class="text-gray-900 dark:text-gray-100 font-semibold">{{ formatPrice(taxAmount) }}</span>
                </div>

                <!-- Shipping Fee -->
                <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                  <span class="flex items-center gap-2">
                    <i class="material-icons text-xs">local_shipping</i>
                    Phí vận chuyển
                  </span>
                  <span :class="[
                    'font-semibold',
                    shippingFee === 0 ? 'text-green-600 dark:text-green-400' : 'text-gray-900 dark:text-gray-100'
                  ]">
                    {{ shippingFee === 0 ? 'Miễn phí' : formatPrice(shippingFee) }}
                  </span>
                </div>
              </div>

              <div class="border-t-2 border-gray-300 dark:border-gray-600 pt-4 mt-4">
                <div class="flex justify-between items-center p-4 bg-gradient-to-br from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20 rounded-lg">
                  <span class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                    <i class="material-icons text-purple-600 dark:text-purple-400">attach_money</i>
                    Tổng cộng
                  </span>
                  <span class="text-2xl font-bold text-purple-600 dark:text-purple-400">{{ formatPrice(totalAmount) }}</span>
                </div>
              </div>
            </div>

            <!-- Trust Badges -->
            <div class="grid grid-cols-1 gap-2 mb-4 p-4 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
              <div class="flex items-center gap-2 text-xs text-gray-600 dark:text-gray-400">
                <i class="material-icons text-green-500 text-base">lock</i>
                <span>Thanh toán bảo mật</span>
              </div>
              <div class="flex items-center gap-2 text-xs text-gray-600 dark:text-gray-400">
                <i class="material-icons text-blue-500 text-base">verified</i>
                <span>Chính hãng 100%</span>
              </div>
              <div class="flex items-center gap-2 text-xs text-gray-600 dark:text-gray-400">
                <i class="material-icons text-purple-500 text-base">swap_horiz</i>
                <span>Đổi trả 30 ngày</span>
              </div>
            </div>

            <p class="text-xs text-gray-500 dark:text-gray-400 text-center">
              Bằng việc đặt hàng, bạn đồng ý với 
              <a href="#" class="text-purple-600 dark:text-purple-400 hover:underline">Điều khoản sử dụng</a> của chúng tôi
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Address Modal -->
    <div v-if="showAddressForm" class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4" @click.self="showAddressForm = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto animate-in fade-in zoom-in duration-200">
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700 bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20">
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">add_location</i>
            Thêm địa chỉ mới
          </h3>
          <button 
            @click="showAddressForm = false" 
            class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            aria-label="Đóng"
          >
            <i class="material-icons">close</i>
          </button>
        </div>
        <div class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Họ tên người nhận *</label>
            <input
              v-model="newAddress.recipientName"
              type="text"
              class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
              placeholder="Nguyễn Văn A"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Số điện thoại *</label>
            <input
              v-model="newAddress.phone"
              type="tel"
              class="w-full px-4 py-2 border rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 transition-all"
              :class="[
                newAddress.phone && !validateVietnamesePhone(newAddress.phone)
                  ? 'border-red-300 dark:border-red-600 focus:ring-red-500 focus:border-red-500'
                  : 'border-gray-200 dark:border-gray-600 focus:ring-purple-500 focus:border-purple-500'
              ]"
              placeholder="0912345678"
              @blur="newAddress.phone = formatPhoneNumber(newAddress.phone)"
            />
            <p v-if="newAddress.phone && !validateVietnamesePhone(newAddress.phone)" class="text-xs text-red-600 dark:text-red-400 mt-1 flex items-center gap-1">
              <i class="material-icons text-xs">error</i>
              Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Địa chỉ *</label>
            <input
              v-model="newAddress.line1"
              type="text"
              class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
              placeholder="123 Đường ABC"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Địa chỉ bổ sung (không bắt buộc)</label>
            <input
              v-model="newAddress.line2"
              type="text"
              class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
              placeholder="Căn hộ, tòa nhà..."
            />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Quận/Huyện *</label>
              <input
                v-model="newAddress.district"
                type="text"
                class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                placeholder="Quận 1"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Tỉnh/Thành phố *</label>
              <input
                v-model="newAddress.city"
                type="text"
                class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                placeholder="TP. Hồ Chí Minh"
              />
            </div>
          </div>
        </div>
        <div class="flex gap-3 p-6 border-t border-gray-200 dark:border-gray-700">
          <button @click="saveAddress" class="flex-1 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all">Lưu địa chỉ</button>
          <button @click="showAddressForm = false" class="flex-1 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors">Hủy</button>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useLoyaltyStore } from '@/stores/loyalty';
import { useCouponStore } from '@/stores/coupon';
import { storeToRefs } from 'pinia';
import toastService from '@/utils/toastService';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';
import axios from 'axios';
import userService from '@/services/userService';
import * as guestCartService from '@/services/guestCartService';
import couponService from '@/services/couponService';
import LoadingSkeleton from '@/components/common/LoadingSkeleton.vue';
import { validateVietnamesePhone, formatPhoneNumber, validateAddress } from '@/utils/validators';
import { formatPrice } from '@/utils/formatters';

const router = useRouter();
const authStore = useAuthStore();
const loyaltyStore = useLoyaltyStore();
const couponStore = useCouponStore();
const { currentBalance } = storeToRefs(loyaltyStore);

// Check if user is authenticated
const isGuest = computed(() => !authStore.isAuthenticated);

// Multi-step data
const steps = [
  { id: 1, title: 'Địa chỉ giao hàng', description: 'Chọn nơi nhận hàng' },
  { id: 2, title: 'Thanh toán', description: 'Chọn phương thức' },
  { id: 3, title: 'Xác nhận', description: 'Hoàn tất đơn hàng' },
];

// State
const currentStep = ref(1);
const loading = ref(true);
const processing = ref(false);
const cart = ref(null);
const addresses = ref([]);
const selectedAddress = ref(null);
const paymentMethod = ref('cod');
const notes = ref('');
const applyingCoupon = ref(false);
const activeCoupons = ref([]);
const loadingActiveCoupons = ref(false);
const selectedCouponCode = ref('');

// Use coupon store state
const couponCode = computed({
  get: () => couponStore.couponCode,
  set: (value) => { couponStore.couponCode = value; }
});
const couponApplied = computed(() => couponStore.hasCoupon);
const couponError = computed(() => couponStore.couponError);
const appliedCoupon = computed(() => couponStore.appliedCoupon);
const shippingFee = ref(30000);
const showAddressForm = ref(false);
const newAddress = ref({
  recipientName: '',
  phone: '',
  line1: '',
  line2: '',
  district: '',
  city: '',
  ward: '',
  postalCode: '',
  email: '', // For guest checkout
});

// Loyalty Points
const usePoints = ref(false);
const pointsToUse = ref(0);
const maxPointsUsable = computed(() => {
  if (!cart.value) return 0;
  const maxFromBalance = currentBalance.value || 0;
  const subTotalValue = Number(cart.value.subTotal) || 0;
  const maxFromOrderValue = Math.floor((subTotalValue + Number(shippingFee.value)) / 1000); // 1 point = 1000 VND
  return Math.min(maxFromBalance, maxFromOrderValue);
});

// Computed
const loyaltyDiscount = computed(() => {
  if (!usePoints.value || pointsToUse.value === 0) return 0;
  return pointsToUse.value * 1000; // 1 point = 1000 VND
});

const couponDiscountAmount = computed(() => {
  if (!cart.value || !appliedCoupon.value) return 0;
  return couponService.calculateDiscount(appliedCoupon.value, cart.value.subTotal);
});

// Calculate VAT (10% on amount after discount)
const taxAmount = computed(() => {
  if (!cart.value) return 0;
  const subTotalValue = Number(cart.value.subTotal) || 0;
  const amountAfterDiscount = subTotalValue - Number(couponDiscountAmount.value) - Number(loyaltyDiscount.value);
  // VAT 10% on amount after discount
  return Math.max(0, amountAfterDiscount * 0.10);
});

const totalAmount = computed(() => {
  if (!cart.value) return 0;
  // Ensure subTotal is a number
  const subTotalValue = Number(cart.value.subTotal) || 0;
  
  // Backend logic:
  // 1. subtotal (giá sản phẩm)
  // 2. amountAfterDiscount = subtotal - couponDiscount - loyaltyDiscount
  // 3. taxAmount = amountAfterDiscount * 0.10 (VAT 10%)
  // 4. total = amountAfterDiscount + shippingFee + taxAmount
  
  const amountAfterDiscount = subTotalValue - Number(couponDiscountAmount.value) - Number(loyaltyDiscount.value);
  const total = amountAfterDiscount + Number(shippingFee.value) + Number(taxAmount.value);
  
  return Math.max(total, 0);
});

const selectedAddressData = computed(() => {
  return addresses.value.find((addr) => addr.id === selectedAddress.value);
});

// Methods
const goToStep = (step) => {
  if (step < currentStep.value) {
    currentStep.value = step;
  }
};

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++;
  }
};

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--;
  }
};

const fetchData = async () => {
  try {
    loading.value = true;

    if (isGuest.value) {
      // Guest checkout flow
      cart.value = await guestCartService.getGuestCart();
      
      // Guest không có addresses, chỉ cần form
      addresses.value = [];
      
      // Check if cart is empty
      if (!cart.value || !cart.value.items || cart.value.items.length === 0) {
        toastService.warning('Cảnh báo','Giỏ hàng trống. Vui lòng thêm sản phẩm trước khi checkout.');
        router.push({ name: 'Cart' });
        return;
      }
    } else {
      // Authenticated user flow
      // Fetch cart
      cart.value = await userService.getMyCart();

      // Check if cart is empty
      if (!cart.value || !cart.value.items || cart.value.items.length === 0) {
        toastService.warning('Cảnh báo','Giỏ hàng trống. Vui lòng thêm sản phẩm trước khi checkout.');
        router.push({ name: 'Cart' });
        return;
      }

      // Fetch addresses
      try {
        addresses.value = await userService.getMyAddresses() || [];

        // Auto-select first address
        if (addresses.value.length > 0) {
          selectedAddress.value = addresses.value[0].id;
        }
      } catch (error) {
        logger.error('Error fetching addresses:', error);
        addresses.value = [];
        // Không throw error vì user có thể thêm địa chỉ mới
      }
    }
  } catch (error) {
    logger.error('Error fetching data:', error);
    toastService.error('Lỗi',error.response?.data?.message || 'Không thể tải thông tin');
    
    // Redirect to cart if error
    if (error.response?.status === 404 || error.response?.status === 400) {
      router.push({ name: 'Cart' });
    }
  } finally {
    loading.value = false;
  }
};

const fetchActiveCoupons = async () => {
  try {
    loadingActiveCoupons.value = true;
    const coupons = await couponService.getActiveCoupons();
    activeCoupons.value = coupons || [];
    
    // Nếu có coupon đã apply, set selectedCouponCode
    if (appliedCoupon.value) {
      selectedCouponCode.value = appliedCoupon.value.code;
    }
  } catch (error) {
    logger.error('Error fetching active coupons:', error);
    // Không hiển thị error cho user vì không block flow
    activeCoupons.value = [];
  } finally {
    loadingActiveCoupons.value = false;
  }
};

const onCouponSelected = async () => {
  if (!selectedCouponCode.value) {
    // Nếu chọn "-- Chọn mã giảm giá --", xóa coupon
    if (couponApplied.value) {
      removeCoupon();
    }
    return;
  }

  // Apply coupon khi user chọn từ dropdown
  try {
    applyingCoupon.value = true;
    couponStore.setError('');
    
    // Validate coupon code from API
    const coupon = await couponService.validateCoupon(
      selectedCouponCode.value.trim(),
      cart.value.subTotal
    );
    
    // Store coupon info in store
    couponStore.setCoupon(selectedCouponCode.value.trim(), coupon);
    
    // Calculate discount
    const discount = couponService.calculateDiscount(coupon, cart.value.subTotal);
    
    toastService.success(
      'Thành công',
      `Đã áp dụng mã giảm giá "${coupon.code}" - Giảm ${couponService.formatCurrency(discount)}`
    );
    
    logger.log('Coupon applied:', coupon.code, 'Discount:', discount);
  } catch (error) {
    logger.error('Error applying coupon:', error);
    couponStore.setError(error.message || 'Không thể áp dụng mã giảm giá');
    couponStore.clearCoupon();
    selectedCouponCode.value = '';
    toastService.error('Lỗi', couponStore.couponError);
  } finally {
    applyingCoupon.value = false;
  }
};

const removeCoupon = () => {
  couponStore.clearCoupon();
  selectedCouponCode.value = '';
  toastService.info('Thông tin','Đã xóa mã giảm giá');
};

// Helper functions for coupon display
const getCouponDisplayText = (coupon, maxLength = 40) => {
  let text = coupon.code;
  
  // Thêm giá trị giảm giá (ngắn gọn)
  if (coupon.discountType === 'percent') {
    text += ` (${coupon.value}%`;
    if (coupon.maxDiscountAmount) {
      const maxAmount = formatPrice(coupon.maxDiscountAmount);
      // Rút ngắn formatPrice nếu quá dài
      if (maxAmount.length > 15) {
        text += ` - Max ${(coupon.maxDiscountAmount / 1000000).toFixed(0)}M`;
      } else {
        text += ` - Max ${maxAmount}`;
      }
    }
    text += ')';
  } else {
    const amount = formatPrice(coupon.value);
    // Rút ngắn formatPrice nếu quá dài
    if (amount.length > 15) {
      text += ` (${(coupon.value / 1000000).toFixed(0)}M)`;
    } else {
      text += ` (${amount})`;
    }
  }
  
  // Nếu text quá dài, truncate
  if (text.length > maxLength) {
    text = text.substring(0, maxLength - 3) + '...';
  }
  
  return text;
};

const getCouponFullText = (coupon) => {
  let text = coupon.code;
  
  if (coupon.description) {
    text += ` - ${coupon.description}`;
  }
  
  if (coupon.discountType === 'percent') {
    text += ` (${coupon.value}%`;
    if (coupon.maxDiscountAmount) {
      text += ` - Tối đa ${formatPrice(coupon.maxDiscountAmount)}`;
    }
    text += ')';
  } else {
    text += ` (${formatPrice(coupon.value)})`;
  }
  
  if (coupon.minOrderAmount) {
    text += ` - Áp dụng cho đơn từ ${formatPrice(coupon.minOrderAmount)}`;
  }
  
  return text;
};

const saveAddress = async () => {
  if (
    !newAddress.value.recipientName ||
    !newAddress.value.phone ||
    !newAddress.value.line1 ||
    !newAddress.value.district ||
    !newAddress.value.city
  ) {
    toastService.warning('Cảnh báo','Vui lòng điền đầy đủ các trường bắt buộc');
    return;
  }

  // Validate phone number
  if (!validateVietnamesePhone(newAddress.value.phone)) {
    toastService.error('Lỗi', 'Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0)');
    return;
  }

  // Validate address format
  if (!validateAddress(newAddress.value.line1)) {
    toastService.error('Lỗi', 'Địa chỉ không hợp lệ. Vui lòng nhập địa chỉ đầy đủ (tối thiểu 5 ký tự)');
    return;
  }

  // Validate district and city
  if (!validateAddress(newAddress.value.district)) {
    toastService.error('Lỗi', 'Quận/Huyện không hợp lệ. Vui lòng nhập quận/huyện đầy đủ');
    return;
  }

  if (!validateAddress(newAddress.value.city)) {
    toastService.error('Lỗi', 'Tỉnh/Thành phố không hợp lệ. Vui lòng nhập tỉnh/thành phố đầy đủ');
    return;
  }

  try {
    const response = await userService.createAddress(newAddress.value);

    addresses.value.push(response);
    selectedAddress.value = response.id;
    showAddressForm.value = false;

    // Reset form
    newAddress.value = {
      recipientName: '',
      phone: '',
      line1: '',
      line2: '',
      district: '',
      city: '',
    };

    toastService.success('Thành công','Đã thêm địa chỉ mới');
  } catch (error) {
    logger.error('Error saving address:', error);
    toastService.error('Lỗi','Không thể thêm địa chỉ');
  }
};

const handleCheckout = async () => {
  try {
    processing.value = true;

    if (isGuest.value) {
      // Guest checkout
      const sessionId = guestCartService.getSessionId();
      
      // Validate guest address form
      if (!newAddress.value.recipientName || !newAddress.value.phone || 
          !newAddress.value.line1 || !newAddress.value.district || !newAddress.value.city) {
        toastService.warning('Cảnh báo','Vui lòng điền đầy đủ thông tin địa chỉ giao hàng');
        currentStep.value = 1; // Go back to address step
        return;
      }

      const guestCheckoutData = {
        sessionId,
        recipientName: newAddress.value.recipientName,
        phone: newAddress.value.phone,
        email: newAddress.value.email || null,
        line1: newAddress.value.line1,
        line2: newAddress.value.line2 || null,
        district: newAddress.value.district,
        city: newAddress.value.city,
        ward: newAddress.value.ward || null,
        postalCode: newAddress.value.postalCode || null,
        paymentMethod: paymentMethod.value,
        customerNote: notes.value || null,
      };

      const response = await axios.post(
        API_ENDPOINTS.CART.GUEST_CHECKOUT,
        guestCheckoutData
      );

      toastService.success('Thành công','Đặt hàng thành công!');

      // Clear guest cart
      guestCartService.clearGuestCart();

      // Clear coupon after successful checkout
      couponStore.clearCoupon();
      logger.log('Coupon cleared after successful guest checkout');

      // Redirect to home or show success page
      setTimeout(() => {
        router.push({ name: 'home' }).catch(err => {
          logger.error('Navigation error after guest checkout:', err);
        });
      }, 1500);
    } else {
      // Authenticated user checkout
      const checkoutData = {
        addressShippingId: selectedAddress.value,
        addressBillingId: selectedAddress.value,
        paymentMethod: paymentMethod.value,
        couponCode: couponCode.value || null,
        customerNote: notes.value || null,
        pointsUsed: usePoints.value && pointsToUse.value > 0 ? pointsToUse.value : null,
      };

      await userService.checkout(checkoutData);
      toastService.success('Thành công','Đặt hàng thành công!');

      // Clear user cart sau khi checkout thành công
      try {
        await userService.clearCart();
        logger.log('✅ Cart đã được xóa sau khi thanh toán');
      } catch (error) {
        logger.warn('⚠️ Không thể xóa cart (có thể đã được xóa tự động):', error);
        // Không hiển thị lỗi cho user vì backend đã xóa cart rồi
      }

      // Clear coupon after successful checkout
      couponStore.clearCoupon();
      logger.log('Coupon cleared after successful checkout');

      // Redirect to orders
      setTimeout(() => {
        router.push({ name: 'UserOrders' }).catch(err => {
          logger.error('Navigation error after checkout:', err);
        });
      }, 1500);
    }
  } catch (error) {
    logger.error('Error during checkout:', error);
    toastService.error('Lỗi',error.message || error.response?.data?.message || 'Không thể đặt hàng');
  } finally {
    processing.value = false;
  }
};

// Format function is now imported from @/utils/formatters

// Watch points input
watch(pointsToUse, (newVal) => {
  if (newVal > maxPointsUsable.value) {
    pointsToUse.value = maxPointsUsable.value;
  }
  if (newVal < 0) {
    pointsToUse.value = 0;
  }
});

// Watch cart subtotal changes - revalidate coupon if needed
watch(() => cart.value?.subTotal, async (newSubtotal, oldSubtotal) => {
  // Only revalidate if subtotal actually changed and coupon is applied
  if (appliedCoupon.value && newSubtotal && newSubtotal !== oldSubtotal) {
    try {
      const coupon = await couponService.validateCoupon(
        couponCode.value,
        newSubtotal
      );
      // Update coupon in store if validation passes
      couponStore.setCoupon(couponCode.value, coupon);
      logger.log('Coupon revalidated after subtotal change');
    } catch (error) {
      // If validation fails, clear coupon
      logger.warn('Coupon validation failed after subtotal change:', error);
      couponStore.clearCoupon();
      toastService.warning('Cảnh báo', 'Mã giảm giá không còn hợp lệ với giá trị đơn hàng mới');
    }
  }
}, { immediate: false });

// Lifecycle
onMounted(async () => {
  try {
    // Initialize coupon store and load from storage
    couponStore.init();
    
    await fetchData();
    
    // Fetch active coupons for dropdown
    await fetchActiveCoupons();
    
    // Revalidate coupon if it exists and cart is loaded
    if (appliedCoupon.value && cart.value?.subTotal) {
      try {
        const coupon = await couponService.validateCoupon(
          couponCode.value,
          cart.value.subTotal
        );
        couponStore.setCoupon(couponCode.value, coupon);
        selectedCouponCode.value = couponCode.value;
        logger.log('Coupon revalidated on checkout page load');
      } catch (error) {
        logger.warn('Coupon validation failed on checkout page load:', error);
        couponStore.clearCoupon();
        selectedCouponCode.value = '';
      }
    }
    
    // Load loyalty balance (only for authenticated users)
    if (!isGuest.value && authStore.isAuthenticated) {
      try {
        await loyaltyStore.fetchBalance();
      } catch (error) {
        logger.warn('Could not fetch loyalty balance:', error);
        // Không block checkout nếu không load được balance
      }
    }
  } catch (error) {
    logger.error('Error in onMounted:', error);
  }
});
</script>

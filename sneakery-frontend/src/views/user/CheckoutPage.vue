<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-7xl mx-auto px-4">
      <!-- Page Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">Thanh toán</h1>
        <p class="text-gray-600 dark:text-gray-400">Hoàn tất đơn hàng của bạn</p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-16">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-purple-600 border-t-transparent mb-4"></div>
          <p class="text-gray-600 dark:text-gray-400">Đang tải thông tin...</p>
        </div>
      </div>

      <!-- Checkout Content -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Left: Multi-Step Form -->
        <div class="lg:col-span-2 space-y-6">
          <!-- Step Progress Indicator -->
          <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <div class="flex items-center justify-between">
              <div
                v-for="(step, index) in steps"
                :key="step.id"
                :class="[
                  'flex items-center gap-3 cursor-pointer transition-all',
                  currentStep === index + 1 ? 'text-purple-600 dark:text-purple-400' : currentStep > index + 1 ? 'text-green-600 dark:text-green-400' : 'text-gray-400 dark:text-gray-500'
                ]"
                @click="goToStep(index + 1)"
              >
                <div :class="[
                  'w-10 h-10 rounded-full flex items-center justify-center font-semibold border-2 transition-all',
                  currentStep === index + 1 
                    ? 'bg-purple-600 text-white border-purple-600' 
                    : currentStep > index + 1 
                    ? 'bg-green-600 text-white border-green-600' 
                    : 'bg-gray-100 dark:bg-gray-700 text-gray-400 border-gray-300 dark:border-gray-600'
                ]">
                  <span v-if="currentStep > index + 1">✓</span>
                  <span v-else>{{ index + 1 }}</span>
                </div>
                <div class="hidden md:block">
                  <div class="font-semibold text-sm">{{ step.title }}</div>
                  <div class="text-xs text-gray-500 dark:text-gray-400">{{ step.description }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Step 1: Shipping Address -->
          <div v-show="currentStep === 1" class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <h2 class="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              Địa chỉ giao hàng
            </h2>

            <div v-if="addresses.length === 0" class="text-center py-12">
              <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="mx-auto mb-4 text-gray-400">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              <p class="text-gray-600 dark:text-gray-400 mb-4">Bạn chưa có địa chỉ giao hàng nào</p>
              <button @click="showAddressForm = true" class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Thêm địa chỉ mới
              </button>
            </div>

            <div v-else class="space-y-4">
              <div
                v-for="addr in addresses"
                :key="addr.id"
                :class="[
                  'p-4 rounded-xl border-2 cursor-pointer transition-all',
                  selectedAddress === addr.id
                    ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20'
                    : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
                ]"
                @click="selectedAddress = addr.id"
              >
                <div class="flex items-start gap-4">
                  <div :class="[
                    'w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-1',
                    selectedAddress === addr.id
                      ? 'bg-purple-600 border-purple-600 text-white'
                      : 'border-gray-300 dark:border-gray-600'
                  ]">
                    <span v-if="selectedAddress === addr.id" class="text-xs">✓</span>
                  </div>
                  <div class="flex-1">
                    <h4 class="font-semibold text-gray-900 dark:text-gray-100 mb-2">{{ addr.recipientName }}</h4>
                    <div class="space-y-1 text-sm text-gray-600 dark:text-gray-400">
                      <div class="flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                        </svg>
                        {{ addr.phone }}
                      </div>
                      <div class="flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                          <polyline points="9 22 9 12 15 12 15 22"></polyline>
                        </svg>
                        {{ addr.line1 }}
                      </div>
                      <div v-if="addr.line2" class="flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <circle cx="12" cy="12" r="10"></circle>
                        </svg>
                        {{ addr.line2 }}
                      </div>
                      <div class="flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <circle cx="12" cy="12" r="10"></circle>
                          <line x1="2" y1="12" x2="22" y2="12"></line>
                          <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
                        </svg>
                        {{ addr.district }}, {{ addr.city }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <button @click="showAddressForm = true" class="w-full flex items-center justify-center gap-2 px-6 py-3 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-xl text-gray-600 dark:text-gray-400 hover:border-purple-400 dark:hover:border-purple-600 hover:text-purple-600 dark:hover:text-purple-400 transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Thêm địa chỉ mới
              </button>
            </div>

            <div class="flex justify-end mt-6">
              <button
                @click="nextStep"
                :disabled="!selectedAddress"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
              >
                Tiếp tục
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                  <polyline points="12 5 19 12 12 19"></polyline>
                </svg>
              </button>
            </div>
          </div>

          <!-- Step 2: Payment Method -->
          <div v-show="currentStep === 2" class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <h2 class="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
                <line x1="1" y1="10" x2="23" y2="10"></line>
              </svg>
              Phương thức thanh toán
            </h2>

            <div class="space-y-4 mb-6">
              <div
                :class="[
                  'p-4 rounded-xl border-2 cursor-pointer transition-all',
                  paymentMethod === 'cod'
                    ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20'
                    : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
                ]"
                @click="paymentMethod = 'cod'"
              >
                <div class="flex items-start gap-4">
                  <div :class="[
                    'w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-1',
                    paymentMethod === 'cod'
                      ? 'bg-purple-600 border-purple-600 text-white'
                      : 'border-gray-300 dark:border-gray-600'
                  ]">
                    <span v-if="paymentMethod === 'cod'" class="text-xs">✓</span>
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center gap-3 mb-2">
                      <div class="w-12 h-12 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-purple-600 dark:text-purple-400">
                          <line x1="12" y1="1" x2="12" y2="23"></line>
                          <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                        </svg>
                      </div>
                      <div>
                        <h4 class="font-semibold text-gray-900 dark:text-gray-100">Thanh toán khi nhận hàng (COD)</h4>
                        <p class="text-sm text-gray-600 dark:text-gray-400">Thanh toán bằng tiền mặt khi nhận hàng</p>
                      </div>
                    </div>
                    <div class="flex gap-2">
                      <span class="px-2 py-1 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded text-xs">✓ Tiện lợi</span>
                      <span class="px-2 py-1 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded text-xs">✓ Không mất phí</span>
                    </div>
                  </div>
                </div>
              </div>

              <div
                :class="[
                  'p-4 rounded-xl border-2 cursor-pointer transition-all',
                  paymentMethod === 'online'
                    ? 'border-purple-600 dark:border-purple-400 bg-purple-50 dark:bg-purple-900/20'
                    : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-600'
                ]"
                @click="paymentMethod = 'online'"
              >
                <div class="flex items-start gap-4">
                  <div :class="[
                    'w-6 h-6 rounded-full border-2 flex items-center justify-center flex-shrink-0 mt-1',
                    paymentMethod === 'online'
                      ? 'bg-purple-600 border-purple-600 text-white'
                      : 'border-gray-300 dark:border-gray-600'
                  ]">
                    <span v-if="paymentMethod === 'online'" class="text-xs">✓</span>
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center gap-3 mb-2">
                      <div class="w-12 h-12 rounded-lg bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="text-blue-600 dark:text-blue-400">
                          <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
                          <line x1="1" y1="10" x2="23" y2="10"></line>
                        </svg>
                      </div>
                      <div>
                        <h4 class="font-semibold text-gray-900 dark:text-gray-100">Thanh toán trực tuyến</h4>
                        <p class="text-sm text-gray-600 dark:text-gray-400">Thanh toán qua VNPay, MoMo, thẻ ATM/Visa...</p>
                      </div>
                    </div>
                    <div class="flex gap-2">
                      <span class="px-2 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 rounded text-xs">✓ Nhanh chóng</span>
                      <span class="px-2 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 rounded text-xs">✓ Bảo mật</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="flex justify-between mt-6">
              <button @click="prevStep" class="flex items-center gap-2 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="19" y1="12" x2="5" y2="12"></line>
                  <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                Quay lại
              </button>
              <button @click="nextStep" class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all">
                Tiếp tục
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                  <polyline points="12 5 19 12 12 19"></polyline>
                </svg>
              </button>
            </div>
          </div>

          <!-- Step 3: Review & Confirm -->
          <div v-show="currentStep === 3" class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
            <h2 class="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="9 11 12 14 22 4"></polyline>
                <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>
              </svg>
              Xác nhận đơn hàng
            </h2>

            <div class="space-y-4 mb-6">
              <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-4">
                <div class="flex items-center justify-between mb-3">
                  <h3 class="font-semibold text-gray-900 dark:text-gray-100">Địa chỉ giao hàng</h3>
                  <button @click="currentStep = 1" class="text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 text-sm font-medium">Sửa</button>
                </div>
                <div v-if="selectedAddressData" class="text-sm text-gray-600 dark:text-gray-400 space-y-1">
                  <div><strong class="text-gray-900 dark:text-gray-100">{{ selectedAddressData.recipientName }}</strong></div>
                  <div>{{ selectedAddressData.phone }}</div>
                  <div>{{ selectedAddressData.line1 }}</div>
                  <div v-if="selectedAddressData.line2">{{ selectedAddressData.line2 }}</div>
                  <div>{{ selectedAddressData.district }}, {{ selectedAddressData.city }}</div>
                </div>
              </div>

              <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-4">
                <div class="flex items-center justify-between mb-3">
                  <h3 class="font-semibold text-gray-900 dark:text-gray-100">Phương thức thanh toán</h3>
                  <button @click="currentStep = 2" class="text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 text-sm font-medium">Sửa</button>
                </div>
                <div class="text-sm text-gray-600 dark:text-gray-400">
                  <div><strong class="text-gray-900 dark:text-gray-100" v-if="paymentMethod === 'cod'">Thanh toán khi nhận hàng (COD)</strong><strong class="text-gray-900 dark:text-gray-100" v-else>Thanh toán trực tuyến</strong></div>
                  <div v-if="paymentMethod === 'cod'">Thanh toán bằng tiền mặt khi nhận hàng</div>
                  <div v-else>Thanh toán qua VNPay, MoMo, thẻ ATM/Visa...</div>
                </div>
              </div>

              <div class="border border-gray-200 dark:border-gray-700 rounded-xl p-4">
                <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-3">Ghi chú (không bắt buộc)</h3>
                <textarea
                  v-model="notes"
                  placeholder="Nhập ghi chú cho đơn hàng (nếu có)..."
                  class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  rows="4"
                ></textarea>
              </div>
            </div>

            <div class="flex justify-between">
              <button @click="prevStep" class="flex items-center gap-2 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="19" y1="12" x2="5" y2="12"></line>
                  <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                Quay lại
              </button>
              <button
                @click="handleCheckout"
                :disabled="processing"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
              >
                <svg v-if="!processing" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
                <span v-if="processing">Đang xử lý...</span>
                <span v-else>Xác nhận đặt hàng</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Right: Order Summary -->
        <div class="lg:col-span-1">
          <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 sticky top-24">
            <h2 class="flex items-center gap-2 text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="9" cy="21" r="1"></circle>
                <circle cx="20" cy="21" r="1"></circle>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
              </svg>
              Đơn hàng ({{ cart?.totalItems || 0 }} sản phẩm)
            </h2>

            <!-- Cart Items -->
            <div class="space-y-3 mb-6 max-h-64 overflow-y-auto">
              <div
                v-for="item in cart?.items || []"
                :key="item.cartItemId"
                class="flex gap-3"
              >
                <div class="w-16 h-16 rounded-lg overflow-hidden bg-gray-100 dark:bg-gray-700 flex-shrink-0">
                  <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" class="w-full h-full object-cover" />
                </div>
                <div class="flex-1 min-w-0">
                  <p class="font-medium text-sm text-gray-900 dark:text-gray-100 truncate">{{ item.productName }}</p>
                  <p class="text-xs text-gray-500 dark:text-gray-400">{{ item.size }} / {{ item.color }}</p>
                  <p class="text-xs text-gray-500 dark:text-gray-400">Số lượng: {{ item.quantity }}</p>
                </div>
                <p class="font-semibold text-sm text-gray-900 dark:text-gray-100">{{ formatPrice(item.totalPrice) }}</p>
              </div>
            </div>

            <!-- Price Breakdown -->
            <div class="space-y-3 mb-6">
              <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                <span>Tạm tính</span>
                <span class="text-gray-900 dark:text-gray-100">{{ formatPrice(cart?.subTotal || 0) }}</span>
              </div>
              <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                <span>Phí vận chuyển</span>
                <span :class="shippingFee === 0 ? 'text-green-600 dark:text-green-400' : 'text-gray-900 dark:text-gray-100'">
                  {{ shippingFee === 0 ? 'Miễn phí' : formatPrice(shippingFee) }}
                </span>
              </div>

              <!-- Loyalty Points Section -->
              <div class="border-t border-gray-200 dark:border-gray-700 pt-3 mt-3">
                <div class="flex items-center justify-between mb-2">
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      id="use-points"
                      v-model="usePoints"
                      :disabled="currentBalance === 0"
                      class="w-4 h-4 text-purple-600 rounded border-gray-300 focus:ring-purple-500"
                    />
                    <span class="text-sm font-medium text-gray-900 dark:text-gray-100">Sử dụng điểm thưởng</span>
                  </label>
                  <span class="text-sm text-purple-600 dark:text-purple-400 font-semibold">
                    {{ currentBalance.toLocaleString() }} điểm
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

              <div v-if="loyaltyDiscount > 0" class="flex justify-between text-sm text-green-600 dark:text-green-400">
                <span>Giảm giá từ điểm thưởng</span>
                <span>-{{ formatPrice(loyaltyDiscount) }}</span>
              </div>

              <div class="border-t border-gray-200 dark:border-gray-700 pt-3 mt-3">
                <div class="flex justify-between items-center">
                  <span class="text-lg font-semibold text-gray-900 dark:text-gray-100">Tổng cộng</span>
                  <span class="text-2xl font-bold text-purple-600 dark:text-purple-400">{{ formatPrice(totalAmount) }}</span>
                </div>
              </div>
            </div>

            <!-- Trust Badges -->
            <div class="grid grid-cols-1 gap-2 mb-4">
              <div class="flex items-center gap-2 text-xs text-gray-600 dark:text-gray-400">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                Thanh toán bảo mật
              </div>
              <div class="flex items-center gap-2 text-xs text-gray-600 dark:text-gray-400">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                </svg>
                Chính hãng 100%
              </div>
              <div class="flex items-center gap-2 text-xs text-gray-600 dark:text-gray-400">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="23 4 23 10 17 10"></polyline>
                  <polyline points="1 20 1 14 7 14"></polyline>
                  <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
                </svg>
                Đổi trả 30 ngày
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
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100">Thêm địa chỉ mới</h3>
          <button @click="showAddressForm = false" class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">×</button>
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
              class="w-full px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
              placeholder="0912345678"
            />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useLoyaltyStore } from '@/stores/loyalty';
import { storeToRefs } from 'pinia';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const router = useRouter();
const authStore = useAuthStore();
const loyaltyStore = useLoyaltyStore();
const { currentBalance } = storeToRefs(loyaltyStore);

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
const shippingFee = ref(30000);
const showAddressForm = ref(false);
const newAddress = ref({
  recipientName: '',
  phone: '',
  line1: '',
  line2: '',
  district: '',
  city: '',
});

// Loyalty Points
const usePoints = ref(false);
const pointsToUse = ref(0);
const maxPointsUsable = computed(() => {
  const maxFromBalance = currentBalance.value;
  const maxFromOrderValue = Math.floor((cart.value?.subTotal + shippingFee.value) / 1000); // 1 point = 1000 VND
  return Math.min(maxFromBalance, maxFromOrderValue);
});

// Computed
const loyaltyDiscount = computed(() => {
  if (!usePoints.value || pointsToUse.value === 0) return 0;
  return pointsToUse.value * 1000; // 1 point = 1000 VND
});

const totalAmount = computed(() => {
  if (!cart.value) return 0;
  const subtotal = cart.value.subTotal + shippingFee.value - loyaltyDiscount.value;
  return Math.max(subtotal, 0);
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

    // Fetch cart
    const cartRes = await axios.get('http://localhost:8080/api/cart', {
      headers: { Authorization: `Bearer ${authStore.token}` },
    });
    cart.value = cartRes.data;

    // Fetch addresses
    const addrRes = await axios.get('http://localhost:8080/api/addresses', {
      headers: { Authorization: `Bearer ${authStore.token}` },
    });
    addresses.value = addrRes.data;

    // Auto-select first address
    if (addresses.value.length > 0) {
      selectedAddress.value = addresses.value[0].id;
    }
  } catch (error) {
    console.error('Error fetching data:', error);
    ElMessage.error('Không thể tải thông tin');
  } finally {
    loading.value = false;
  }
};

const saveAddress = async () => {
  if (
    !newAddress.value.recipientName ||
    !newAddress.value.phone ||
    !newAddress.value.line1 ||
    !newAddress.value.district ||
    !newAddress.value.city
  ) {
    ElMessage.warning('Vui lòng điền đầy đủ các trường bắt buộc');
    return;
  }

  try {
    const response = await axios.post(
      'http://localhost:8080/api/addresses',
      newAddress.value,
      {
        headers: { Authorization: `Bearer ${authStore.token}` },
      }
    );

    addresses.value.push(response.data);
    selectedAddress.value = response.data.id;
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

    ElMessage.success('Đã thêm địa chỉ mới');
  } catch (error) {
    console.error('Error saving address:', error);
    ElMessage.error('Không thể thêm địa chỉ');
  }
};

const handleCheckout = async () => {
  try {
    processing.value = true;

    const checkoutData = {
      addressShippingId: selectedAddress.value,
      addressBillingId: selectedAddress.value,
      paymentMethod: paymentMethod.value,
    };

    const response = await axios.post(
      'http://localhost:8080/api/orders/checkout',
      checkoutData,
      {
        headers: { Authorization: `Bearer ${authStore.token}` },
      }
    );

    ElMessage.success('Đặt hàng thành công!');

    // Redirect to orders
    setTimeout(() => {
      router.push({ name: 'UserOrders' });
    }, 1500);
  } catch (error) {
    console.error('Error during checkout:', error);
    ElMessage.error(error.response?.data?.message || 'Không thể đặt hàng');
  } finally {
    processing.value = false;
  }
};

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(price);
};

// Watch points input
watch(pointsToUse, (newVal) => {
  if (newVal > maxPointsUsable.value) {
    pointsToUse.value = maxPointsUsable.value;
  }
  if (newVal < 0) {
    pointsToUse.value = 0;
  }
});

// Lifecycle
onMounted(async () => {
  await fetchData();
  // Load loyalty balance
  await loyaltyStore.fetchBalance();
});
</script>

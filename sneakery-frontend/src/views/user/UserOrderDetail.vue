<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-6xl mx-auto px-4 space-y-6">
      <!-- Page Header (based on modal header) -->
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden"
      >
        <div
          class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 p-6 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-30 bg-white/95 dark:bg-gray-800/95 backdrop-blur-md bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/30 dark:to-indigo-900/30"
        >
          <div class="flex items-center gap-3">
            <button
              @click="goBack"
              class="w-10 h-10 rounded-lg flex items-center justify-center text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
              aria-label="Quay lại"
              title="Quay lại"
            >
              <i class="material-icons">arrow_back</i>
            </button>

            <h3
              class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
            >
              <i class="material-icons text-purple-600 dark:text-purple-400"
                >receipt_long</i
              >
              Chi tiết đơn hàng #{{ orderId }}
            </h3>

            <span
              v-if="order"
              :class="[
                'ml-2 inline-flex items-center gap-1 px-4 py-1.5 rounded-full text-sm font-semibold',
                getStatusClass(order.status),
              ]"
            >
              <i class="material-icons text-sm">{{
                getStatusIcon(order.status)
              }}</i>
              {{ getStatusText(order.status) }}
            </span>
          </div>

          <div class="flex items-center gap-2">
            <button
              @click="refreshDetail(true)"
              :disabled="loading"
              class="flex items-center gap-2 px-4 py-2 bg-white/20 hover:bg-white/30 backdrop-blur-sm rounded-lg text-gray-800 dark:text-gray-100 font-medium transition-all disabled:opacity-50 disabled:cursor-not-allowed border border-gray-200 dark:border-gray-700"
              title="Làm mới chi tiết đơn hàng"
            >
              <i class="material-icons" :class="{ 'animate-spin': loading }"
                >refresh</i
              >
              <span class="hidden sm:inline">Làm mới</span>
            </button>
          </div>
        </div>

        <!-- Body -->
        <div class="p-6">
          <!-- Loading -->
          <div v-if="loading" class="space-y-4" role="status" aria-live="polite">
            <LoadingSkeleton v-for="n in 2" :key="n" type="detail" />
            <span class="sr-only">Đang tải chi tiết đơn hàng</span>
          </div>

          <!-- Error / Empty -->
          <div
            v-else-if="!order"
            class="text-center py-16 bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-900 rounded-xl border border-gray-200 dark:border-gray-700"
          >
            <div
              class="w-20 h-20 mx-auto mb-5 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center"
            >
              <i
                class="material-icons text-4xl text-purple-600 dark:text-purple-400"
                >error_outline</i
              >
            </div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2">
              Không thể tải đơn hàng
            </h2>
            <p class="text-gray-600 dark:text-gray-400 mb-6">
              Vui lòng thử làm mới hoặc quay lại danh sách đơn hàng.
            </p>
            <div class="flex items-center justify-center gap-3">
              <button
                @click="refreshDetail(false)"
                class="px-5 py-2.5 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl"
              >
                Thử lại
              </button>
              <button
                @click="goBack"
                class="px-5 py-2.5 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-all"
              >
                Quay lại
              </button>
            </div>
          </div>

          <!-- Content -->
          <div v-else class="space-y-6">
            <!-- Order Status Timeline -->
            <div>
              <h4
                class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
              >
                <i class="material-icons text-purple-600 dark:text-purple-400"
                  >timeline</i
                >
                Trạng thái đơn hàng
              </h4>
              <div class="space-y-4">
                <div
                  v-for="(history, index) in order.statusHistories || []"
                  :key="history.id || index"
                  class="flex items-start gap-4 p-4 bg-gray-50 dark:bg-gray-700/50 rounded-xl hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                >
                  <div class="flex flex-col items-center flex-shrink-0">
                    <div
                      :class="[
                        'w-10 h-10 rounded-full flex items-center justify-center shadow-sm',
                        index === 0
                          ? 'bg-gradient-to-br from-purple-500 to-purple-600'
                          : 'bg-gray-300 dark:bg-gray-600',
                      ]"
                    >
                      <i class="material-icons text-white text-sm">{{
                        getStatusIcon(history.status)
                      }}</i>
                    </div>
                    <div
                      v-if="index < (order.statusHistories?.length || 0) - 1"
                      class="w-0.5 h-full bg-gray-200 dark:bg-gray-600 mt-2 min-h-[40px]"
                    ></div>
                  </div>
                  <div class="flex-1 pb-4">
                    <p class="font-semibold text-gray-900 dark:text-gray-100 mb-1">
                      {{ getStatusText(history.status) }}
                    </p>
                    <p
                      class="text-sm text-gray-500 dark:text-gray-400 flex items-center gap-1"
                    >
                      <i class="material-icons text-xs">schedule</i>
                      {{ formatDateTime(history.changedAt) }}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Order Items -->
            <div>
              <h4
                class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
              >
                <i class="material-icons text-purple-600 dark:text-purple-400"
                  >shopping_cart</i
                >
                Sản phẩm
              </h4>
              <div class="space-y-3">
                <div
                  v-for="item in order.orderDetails || []"
                  :key="item.variantId"
                  class="flex items-center gap-4 p-4 bg-white dark:bg-gray-800/80 backdrop-blur-sm border border-gray-200 dark:border-gray-700 rounded-xl hover:shadow-md hover:border-purple-300 dark:hover:border-purple-600 transition-all"
                >
                  <div
                    class="w-20 h-20 rounded-lg overflow-hidden bg-gray-100 dark:bg-gray-700 flex-shrink-0"
                  >
                    <img
                      :src="
                        variantImageCache.get(item.variantId) ||
                        '/placeholder-image.png'
                      "
                      :alt="item.productName"
                      class="w-full h-full object-cover"
                    />
                  </div>
                  <div class="flex-1 min-w-0">
                    <h5
                      class="font-semibold text-gray-900 dark:text-gray-100 mb-1 truncate"
                    >
                      {{ item.productName }}
                    </h5>
                    <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">
                      {{ item.brandName }}
                    </p>
                    <p class="text-[10px] text-gray-400 dark:text-gray-500 mb-1">
                      SKU: {{ item.sku || "N/A" }}
                    </p>
                    <div class="flex flex-wrap gap-2">
                      <span
                        class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs text-gray-700 dark:text-gray-300"
                      >
                        Size: {{ item.size }}
                      </span>
                      <span
                        class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs text-gray-700 dark:text-gray-300"
                      >
                        Màu: {{ item.color }}
                      </span>
                    </div>
                  </div>
                  <div class="text-right flex-shrink-0">
                    <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">
                      Số lượng: x{{ item.quantity }}
                    </p>
                    <p
                      class="font-semibold text-lg text-purple-600 dark:text-purple-400"
                    >
                      {{ formatPrice(item.totalPrice) }}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Shipping Address -->
            <div>
              <h4
                class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
              >
                <i class="material-icons text-purple-600 dark:text-purple-400"
                  >location_on</i
                >
                Địa chỉ giao hàng
              </h4>
              <div
                v-if="order.addressShipping"
                class="p-4 border border-gray-200 dark:border-gray-700 rounded-xl bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-700/50 dark:to-gray-800/50"
              >
                <div class="flex items-start gap-3 mb-3">
                  <div
                    class="w-8 h-8 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center flex-shrink-0"
                  >
                    <i
                      class="material-icons text-purple-600 dark:text-purple-400 text-sm"
                      >person</i
                    >
                  </div>
                  <div class="flex-1">
                    <p class="font-semibold text-gray-900 dark:text-gray-100 mb-1">
                      {{ order.addressShipping.recipientName }}
                    </p>
                    <p
                      class="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1"
                    >
                      <i class="material-icons text-xs">phone</i>
                      {{ order.addressShipping.phone }}
                    </p>
                  </div>
                </div>
                <div
                  class="space-y-1 text-sm text-gray-600 dark:text-gray-400 pl-11"
                >
                  <p class="flex items-center gap-1">
                    <i class="material-icons text-xs">place</i>
                    {{ order.addressShipping.line1 }}
                  </p>
                  <p v-if="order.addressShipping.line2" class="pl-6">
                    {{ order.addressShipping.line2 }}
                  </p>
                  <p class="flex items-center gap-1">
                    <i class="material-icons text-xs">location_city</i>
                    {{ order.addressShipping.district }},
                    {{ order.addressShipping.city }}
                  </p>
                </div>
              </div>

              <div
                v-else
                class="p-4 border border-gray-200 dark:border-gray-700 rounded-xl bg-gray-50 dark:bg-gray-800 text-gray-600 dark:text-gray-400"
              >
                Không có thông tin địa chỉ giao hàng.
              </div>
            </div>

            <!-- Payment Info -->
            <div>
              <h4
                class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
              >
                <i class="material-icons text-purple-600 dark:text-purple-400"
                  >payments</i
                >
                Chi tiết thanh toán
              </h4>

              <div
                class="p-5 border border-gray-200 dark:border-gray-700 rounded-xl bg-gradient-to-br from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20 space-y-4"
              >
                <!-- Subtotal -->
                <div class="flex justify-between items-center">
                  <span class="text-gray-600 dark:text-gray-400">Tạm tính</span>
                  <span class="font-semibold text-gray-900 dark:text-gray-100">
                    {{ formatPrice(order.subtotal || 0) }}
                  </span>
                </div>

                <!-- Coupon -->
                <div
                  v-if="(order.discountAmount || 0) > 0"
                  class="flex justify-between items-center"
                >
                  <span
                    class="text-gray-600 dark:text-gray-400 flex items-center gap-2"
                  >
                    Mã giảm giá
                    <span
                      v-if="order.couponCode"
                      class="px-2 py-0.5 rounded text-xs font-bold bg-purple-200 text-purple-700 dark:bg-purple-800 dark:text-purple-300"
                    >
                      {{ order.couponCode }}
                    </span>
                  </span>
                  <span class="font-semibold text-red-600 dark:text-red-400">
                    -{{ formatPrice(order.discountAmount) }}
                  </span>
                </div>

                <!-- Loyalty Points -->
                <div
                  v-if="(order.pointsUsed || 0) > 0"
                  class="flex justify-between items-center"
                >
                  <span class="text-gray-600 dark:text-gray-400">
                    Điểm thưởng ({{ order.pointsUsed }} điểm)
                  </span>
                  <span class="font-semibold text-red-600 dark:text-red-400">
                    -{{ formatPrice(order.pointsDiscount || 0) }}
                  </span>
                </div>

                <!-- VAT -->
                <div
                  v-if="(order.taxAmount || 0) > 0"
                  class="flex justify-between items-center"
                >
                  <span class="text-gray-600 dark:text-gray-400">VAT</span>
                  <span class="font-semibold text-gray-900 dark:text-gray-100">
                    {{ formatPrice(order.taxAmount) }}
                  </span>
                </div>

                <!-- Shipping -->
                <div class="flex justify-between items-center">
                  <span class="text-gray-600 dark:text-gray-400"
                    >Phí vận chuyển</span
                  >
                  <span class="font-semibold text-gray-900 dark:text-gray-100">
                    {{ formatPrice(order.shippingFee || 0) }}
                  </span>
                </div>

                <hr class="border-gray-300 dark:border-gray-700" />

                <!-- TOTAL -->
                <div class="flex justify-between items-center">
                  <span class="text-lg font-semibold text-gray-900 dark:text-gray-100">
                    Tổng cộng
                  </span>
                  <span class="text-2xl font-bold text-purple-600 dark:text-purple-400">
                    {{ formatPrice(order.totalAmount || order.total || 0) }}
                  </span>
                </div>

                <!-- Payment Method -->
                <div
                  v-if="order.payment"
                  class="pt-4 border-t border-gray-200 dark:border-gray-700 space-y-3"
                >
                  <div class="flex justify-between items-center">
                    <span
                      class="text-gray-600 dark:text-gray-400 flex items-center gap-1"
                    >
                      <i class="material-icons text-base">credit_card</i>
                      Phương thức thanh toán
                    </span>
                    <span class="font-medium text-gray-900 dark:text-gray-100">
                      {{ getPaymentMethodText(order.payment.paymentMethod) }}
                    </span>
                  </div>

                  <div class="flex justify-between items-center">
                    <span
                      class="text-gray-600 dark:text-gray-400 flex items-center gap-1"
                    >
                      <i class="material-icons text-base">info</i>
                      Trạng thái
                    </span>
                    <span
                      :class="[
                        'inline-flex items-center gap-1 px-3 py-1 rounded-full text-sm font-medium',
                        order.payment.status === 'completed'
                          ? 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400'
                          : order.payment.status === 'pending'
                          ? 'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400'
                          : 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400',
                      ]"
                    >
                      <i class="material-icons text-sm">
                        {{
                          order.payment.status === "completed"
                            ? "check_circle"
                            : order.payment.status === "pending"
                            ? "schedule"
                            : "error"
                        }}
                      </i>
                      {{ getPaymentStatusText(order.payment.status) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Return Request Info -->
            <div v-if="order.returnRequest">
              <h4
                class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
              >
                <i class="material-icons text-orange-600 dark:text-orange-400"
                  >assignment_return</i
                >
                Thông tin hoàn trả
              </h4>
              <div
                class="p-5 border border-gray-200 dark:border-gray-700 rounded-xl bg-gradient-to-br from-orange-50 to-amber-50 dark:from-orange-900/20 dark:to-amber-900/20 space-y-4"
              >
                <div class="flex justify-between items-center">
                  <span
                    class="text-gray-600 dark:text-gray-400 flex items-center gap-2"
                  >
                    <i class="material-icons text-base">info</i>
                    Trạng thái
                  </span>
                  <span
                    :class="[
                      'inline-flex items-center gap-1 px-3 py-1 rounded-full text-sm font-medium',
                      getReturnRequestBadgeClass(order.returnRequest.status),
                    ]"
                  >
                    <i class="material-icons text-sm">{{
                      getReturnRequestIcon(order.returnRequest.status)
                    }}</i>
                    {{ getReturnRequestText(order.returnRequest.status) }}
                  </span>
                </div>

                <div
                  v-if="order.returnRequest.reason"
                  class="pt-4 border-t border-gray-200 dark:border-gray-700"
                >
                  <span
                    class="text-sm font-semibold text-gray-700 dark:text-gray-300 block mb-2"
                    >Lý do hoàn trả:</span
                  >
                  <p
                    class="text-sm text-gray-600 dark:text-gray-400 whitespace-pre-wrap"
                  >
                    {{ getReturnReasonText(order.returnRequest.reason) }}
                  </p>
                </div>

                <div
                  v-if="
                    order.returnRequest.images &&
                    order.returnRequest.images.length > 0
                  "
                  class="pt-4 border-t border-gray-200 dark:border-gray-700"
                >
                  <span
                    class="text-sm font-semibold text-gray-700 dark:text-gray-300 block mb-2"
                    >Hình ảnh đính kèm:</span
                  >
                  <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
                    <div
                      v-for="(image, index) in order.returnRequest.images"
                      :key="index"
                      class="relative group"
                    >
                      <img
                        :src="image"
                        :alt="`Return image ${index + 1}`"
                        class="w-full h-24 object-cover rounded-lg border border-gray-200 dark:border-gray-700 cursor-pointer hover:opacity-80 transition-opacity"
                        @click="viewImage(image)"
                      />
                    </div>
                  </div>
                </div>

                <!-- Refund method + bank info -->
                <div
                  v-if="order.returnRequest.returnMethod === 'refund'"
                  class="pt-4 border-t border-gray-200 dark:border-gray-700 space-y-3"
                >
                  <div class="flex justify-between items-center">
                    <span class="text-sm font-semibold text-gray-700 dark:text-gray-300">
                      Phương thức hoàn tiền:
                    </span>
                    <span class="font-medium text-gray-900 dark:text-gray-100">
                      {{ getReturnMethodText(order.returnRequest.returnMethod) }}
                    </span>
                  </div>

                  <div
                    v-if="order.returnRequest.bankName"
                    class="flex justify-between items-center"
                  >
                    <span class="text-sm text-gray-600 dark:text-gray-400">
                      Ngân hàng:
                    </span>
                    <span class="font-medium text-gray-900 dark:text-gray-100">
                      {{ order.returnRequest.bankName }}
                    </span>
                  </div>

                  <div
                    v-if="order.returnRequest.bankAccountNumber"
                    class="flex justify-between items-center"
                  >
                    <span class="text-sm text-gray-600 dark:text-gray-400">
                      Số tài khoản:
                    </span>
                    <span
                      class="font-medium text-gray-900 dark:text-gray-100 tracking-wide"
                    >
                      {{ order.returnRequest.bankAccountNumber }}
                    </span>
                  </div>

                  <div
                    v-if="order.returnRequest.bankAccountHolder"
                    class="flex justify-between items-center"
                  >
                    <span class="text-sm text-gray-600 dark:text-gray-400">
                      Chủ tài khoản:
                    </span>
                    <span
                      class="font-medium text-gray-900 dark:text-gray-100 uppercase"
                    >
                      {{ order.returnRequest.bankAccountHolder }}
                    </span>
                  </div>
                </div>

                <div
                  v-if="order.returnRequest.adminNote"
                  class="pt-4 border-t border-gray-200 dark:border-gray-700"
                >
                  <span
                    class="text-sm font-semibold text-gray-700 dark:text-gray-300 block mb-2"
                    >Ghi chú từ admin:</span
                  >
                  <p
                    class="text-sm text-gray-600 dark:text-gray-400 whitespace-pre-wrap"
                  >
                    {{ order.returnRequest.adminNote }}
                  </p>
                </div>

                <div
                  class="flex flex-col sm:flex-row sm:justify-between gap-2 pt-4 border-t border-gray-200 dark:border-gray-700"
                >
                  <span
                    class="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1"
                  >
                    <i class="material-icons text-xs">schedule</i>
                    Yêu cầu được tạo:
                    {{ formatDateTime(order.returnRequest.createdAt) }}
                  </span>
                  <span
                    v-if="order.returnRequest.approvedAt"
                    class="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1"
                  >
                    <i class="material-icons text-xs">check_circle</i>
                    Đã duyệt:
                    {{ formatDateTime(order.returnRequest.approvedAt) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Actions (based on modal footer) -->
            <div
              class="flex flex-col sm:flex-row sm:justify-end gap-3 pt-2"
            >
              <button
                v-if="canMarkAsReceived(order?.status)"
                @click="markAsReceived(order.id)"
                class="px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-xl font-semibold hover:from-green-600 hover:to-emerald-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center justify-center gap-2"
              >
                <i class="material-icons text-lg">check_circle</i>
                Đã nhận hàng
              </button>

              <button
                v-if="canRequestReturn(order?.status)"
                @click="openReturnModal"
                class="px-6 py-3 bg-gradient-to-r from-orange-500 to-amber-600 text-white rounded-xl font-semibold hover:from-orange-600 hover:to-amber-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center justify-center gap-2"
              >
                <i class="material-icons text-lg">assignment_return</i>
                Trả hàng / Hoàn tiền
              </button>

              <button
                @click="goBack"
                class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center justify-center gap-2"
              >
                <i class="material-icons text-lg">close</i>
                Quay lại
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Return Request Modal (reuse from your existing UI) -->
    <div
      v-if="showReturnModal"
      class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4"
      @click.self="showReturnModal = false"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto hide-scrollbar animate-in fade-in zoom-in duration-200"
        @click.stop
      >
        <div
          class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10"
        >
          <h3
            class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
          >
            <i class="material-icons text-orange-600 dark:text-orange-400"
              >assignment_return</i
            >
            Yêu cầu hoàn trả đơn hàng #{{ order?.id }}
          </h3>
          <button
            @click="closeReturnModal"
            class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            aria-label="Đóng"
          >
            <i class="material-icons">close</i>
          </button>
        </div>

        <div class="p-6 space-y-6">
          <div class="space-y-4">
            <div>
              <label
                class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >
                Lý do hoàn trả <span class="text-red-500">*</span>
              </label>
              <select
                v-model="returnForm.reason"
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
                :class="{
                  'border-red-500 dark:border-red-500': returnFormErrors.reason,
                }"
                required
              >
                <option value="">-- Chọn lý do --</option>
                <option value="defective">Hàng lỗi</option>
                <option value="not_as_described">Không đúng mô tả</option>
                <option value="wrong_item">Giao sai hàng</option>
                <option value="wrong_size">Sai kích cỡ</option>
                <option value="size_issue">Không vừa size</option>
                <option value="change_of_mind">Đổi ý</option>
                <option value="damaged">Hư hỏng khi vận chuyển</option>
                <option value="other">Lý do khác</option>
              </select>
              <p
                v-if="returnFormErrors.reason"
                class="text-sm text-red-600 dark:text-red-400 mt-1"
              >
                {{ returnFormErrors.reason }}
              </p>
            </div>

            <div>
              <label
                class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >
                Ghi chú (không bắt buộc)
              </label>
              <textarea
                v-model="returnForm.note"
                rows="4"
                placeholder="Mô tả chi tiết lý do hoàn trả..."
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all resize-none"
              ></textarea>
            </div>

            <div>
              <label
                class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >
                Hình ảnh đính kèm (không bắt buộc)
              </label>
              <div class="flex flex-col gap-3">
                <input
                  type="file"
                  ref="imageInput"
                  @change="handleImageSelect"
                  accept="image/*"
                  multiple
                  class="hidden"
                />
                <button
                  @click="imageInput?.click()"
                  type="button"
                  class="w-full px-4 py-3 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg text-gray-600 dark:text-gray-400 hover:border-orange-500 dark:hover:border-orange-600 hover:text-orange-600 dark:hover:text-orange-400 transition-all flex items-center justify-center gap-2"
                >
                  <i class="material-icons text-lg">add_photo_alternate</i>
                  Chọn hình ảnh
                </button>

                <div
                  v-if="returnForm.images.length > 0"
                  class="grid grid-cols-2 sm:grid-cols-3 gap-3"
                >
                  <div
                    v-for="(image, index) in returnForm.images"
                    :key="index"
                    class="relative group"
                  >
                    <img
                      :src="image"
                      :alt="`Return image ${index + 1}`"
                      class="w-full h-24 object-cover rounded-lg border border-gray-200 dark:border-gray-700"
                    />
                    <button
                      @click="removeImage(index)"
                      class="absolute top-1 right-1 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                      type="button"
                      aria-label="Xóa ảnh"
                    >
                      <i class="material-icons text-sm">close</i>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div>
              <label
                class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >
                Phương thức hoàn tiền
              </label>
              <input
                type="text"
                disabled
                :value="getReturnMethodText(returnForm.returnMethod)"
                class="w-full px-4 py-3 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-500 cursor-not-allowed"
              />
            </div>

            <div>
              <label
                class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >
                Ngân hàng <span class="text-red-500">*</span>
              </label>
              <input
                type="text"
                v-model="returnForm.bankName"
                placeholder="Tên ngân hàng..."
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-orange-500"
                :class="{
                  'border-red-500 dark:border-red-500':
                    returnFormErrors.bankName,
                }"
                required
              />
              <p v-if="returnFormErrors.bankName" class="text-sm text-red-600 mt-1">
                {{ returnFormErrors.bankName }}
              </p>
            </div>

            <div>
              <label
                class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >
                Số tài khoản <span class="text-red-500">*</span>
              </label>
              <input
                type="text"
                v-model="returnForm.bankAccountNumber"
                placeholder="Ví dụ: 0123456789"
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-orange-500"
                :class="{
                  'border-red-500 dark:border-red-500':
                    returnFormErrors.bankAccountNumber,
                }"
                required
              />
              <p
                v-if="returnFormErrors.bankAccountNumber"
                class="text-sm text-red-600 mt-1"
              >
                {{ returnFormErrors.bankAccountNumber }}
              </p>
            </div>

            <div>
              <label
                class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >
                Chủ tài khoản <span class="text-red-500">*</span>
              </label>
              <input
                type="text"
                v-model="returnForm.bankAccountHolder"
                placeholder="Tên chủ tài khoản..."
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-orange-500"
                :class="{
                  'border-red-500 dark:border-red-500':
                    returnFormErrors.bankAccountHolder,
                }"
                required
              />
              <p
                v-if="returnFormErrors.bankAccountHolder"
                class="text-sm text-red-600 mt-1"
              >
                {{ returnFormErrors.bankAccountHolder }}
              </p>
            </div>
          </div>
        </div>

        <div
          class="flex justify-end gap-3 p-6 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50"
        >
          <button
            @click="closeReturnModal"
            class="px-6 py-3 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-all flex items-center gap-2"
            type="button"
          >
            Hủy
          </button>

          <button
            @click="submitReturnRequest"
            :disabled="submittingReturn"
            class="px-6 py-3 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-xl font-semibold hover:from-orange-600 hover:to-orange-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
            type="button"
          >
            <div
              v-if="submittingReturn"
              class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"
            ></div>
            <i v-else class="material-icons text-lg">send</i>
            {{ submittingReturn ? "Đang gửi..." : "Gửi yêu cầu" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import notificationService from "@/utils/notificationService";
import confirmDialogService from "@/utils/confirmDialogService";
import userService from "@/services/userService";
import logger from "@/utils/logger";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import { formatPrice, formatDateTime } from "@/utils/formatters";

// Router
const route = useRoute();
const router = useRouter();

// Params
const orderId = computed(() => Number(route.params.id));

// State
const order = ref(null);
const loading = ref(true);

// Polling interval for detail refresh (optional)
let refreshInterval = null;

// Variant images cache
const variantImageCache = ref(new Map());

// Return Request Modal State
const showReturnModal = ref(false);
const submittingReturn = ref(false);

const returnForm = ref({
  reason: "",
  note: "",
  images: [],
  bankName: "",
  bankAccountNumber: "",
  bankAccountHolder: "",
  returnMethod: "refund",
});

const returnFormErrors = ref({});
const imageInput = ref(null);

/**
 * Status normalization (reuse from your code)
 */
const normalizeStatusForDisplay = (status) => {
  if (!status) return status;

  const statusMap = {
    pending: "Pending",
    processing: "Processing",
    shipped: "Shipped",
    delivered: "Completed",
    cancelled: "Cancelled",
    confirmed: "Confirmed",
    packed: "Packed",
    refunded: "Refunded",
    failed: "Failed",
    approved: "Approved",
    rejected: "Rejected",
    completed: "Completed",
    return_pending: "Return_Pending",
    return_approved: "Return_Approved",
    return_rejected: "Return_Rejected",
    return_completed: "Return_Completed",
  };

  return statusMap[status.toLowerCase()] || status;
};

const getNormalizedStatus = (status) => {
  return normalizeStatusForDisplay(status) || "Pending";
};

const getStatusClass = (status) => {
  const normalizedStatus = getNormalizedStatus(status);
  const statusMap = {
    Pending:
      "bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-400",
    Processing:
      "bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400",
    Confirmed:
      "bg-indigo-100 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-400",
    Packed:
      "bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400",
    Shipped: "bg-cyan-100 dark:bg-cyan-900/30 text-cyan-700 dark:text-cyan-400",
    Completed:
      "bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-400",
    Cancelled:
      "bg-rose-100 dark:bg-rose-900/30 text-rose-700 dark:text-rose-400",
    Refunded:
      "bg-orange-100 dark:bg-orange-900/30 text-orange-700 dark:text-orange-400",
    Failed: "bg-red-200 dark:bg-red-900/40 text-red-800 dark:text-red-400",
    Approved:
      "bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400",
    Rejected: "bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400",
    Return_Pending:
      "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400",
    Return_Approved:
      "bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400",
    Return_Rejected:
      "bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400",
    Return_Completed:
      "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400",
  };
  return (
    statusMap[normalizedStatus] ||
    "bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-400"
  );
};

const getStatusIcon = (status) => {
  const normalizedStatus = getNormalizedStatus(status);
  const iconMap = {
    Pending: "schedule",
    Processing: "autorenew",
    Confirmed: "check_circle_outline",
    Packed: "inventory_2",
    Shipped: "local_shipping",
    Completed: "check_circle",
    Cancelled: "cancel",
    Refunded: "undo",
    Failed: "local_shipping",
    Approved: "check_circle_outline",
    Rejected: "cancel",
    Return_Pending: "assignment_return",
    Return_Approved: "check_circle_outline",
    Return_Rejected: "cancel",
    Return_Completed: "check_circle",
  };
  return iconMap[normalizedStatus] || "help";
};

const getStatusText = (status) => {
  const normalizedStatus = getNormalizedStatus(status);
  const statusMap = {
    Pending: "Chờ xác nhận",
    Processing: "Đang xử lý",
    Confirmed: "Đã xác nhận",
    Packed: "Đã đóng gói",
    Shipped: "Đang giao hàng",
    Completed: "Hoàn thành",
    Cancelled: "Đã hủy",
    Refunded: "Đã hoàn tiền",
    Failed: "Giao hàng thất bại",
    Approved: "Đã duyệt",
    Rejected: "Đã từ chối",
    Return_Pending: "Chờ xử lý hoàn trả",
    Return_Approved: "Đã duyệt hoàn trả",
    Return_Rejected: "Từ chối hoàn trả",
    Return_Completed: "Hoàn thành hoàn trả",
  };
  return statusMap[normalizedStatus] || normalizedStatus || status;
};

const getPaymentMethodText = (method) => {
  const methodMap = {
    cod: "Thanh toán khi nhận hàng (COD)",
    online: "Thanh toán trực tuyến",
  };
  return methodMap[method] || method;
};

const getPaymentStatusText = (status) => {
  const statusMap = {
    pending: "Chờ thanh toán",
    completed: "Đã thanh toán",
    failed: "Thanh toán thất bại",
  };
  return statusMap[status] || status;
};

// Return Request helpers
const getReturnMethodText = (method) => {
  if (!method) return "Chuyển khoản ngân hàng";
  const map = { refund: "Chuyển khoản ngân hàng" };
  return map[method.toLowerCase()] || method;
};

const getReturnRequestBadgeClass = (status) => {
  if (!status)
    return "bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-400";

  const statusLower = status.toLowerCase();
  const statusMap = {
    pending:
      "bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-400",
    approved:
      "bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400",
    completed:
      "bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-400",
    rejected: "bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400",
  };
  return (
    statusMap[statusLower] ||
    "bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-400"
  );
};

const getReturnRequestIcon = (status) => {
  if (!status) return "help";
  const statusLower = status.toLowerCase();
  const iconMap = {
    pending: "schedule",
    approved: "check_circle_outline",
    completed: "check_circle",
    rejected: "cancel",
  };
  return iconMap[statusLower] || "help";
};

const getReturnRequestText = (status) => {
  if (!status) return status;
  const statusLower = status.toLowerCase();
  const statusMap = {
    pending: "Chờ xử lý",
    approved: "Đã xác nhận",
    completed: "Hoàn thành",
    rejected: "Từ chối",
  };
  return statusMap[statusLower] || status;
};

const getReturnReasonText = (reason, includeNote = true) => {
  if (!reason) return reason;

  const noteSeparator = "\n\nGhi chú:";
  const reasonCode = reason.includes(noteSeparator)
    ? reason.split(noteSeparator)[0].trim()
    : reason.trim();

  const reasonMap = {
    defective: "Hàng lỗi",
    not_as_described: "Không đúng mô tả",
    wrong_item: "Giao sai sản phẩm",
    wrong_size: "Sai kích cỡ",
    size_issue: "Không vừa size",
    change_of_mind: "Đổi ý",
    damaged: "Hư hỏng khi vận chuyển",
    other: "Lý do khác",
  };

  const vietnameseReason = reasonMap[reasonCode] || reasonCode;

  if (includeNote && reason.includes(noteSeparator)) {
    const note = reason.split(noteSeparator)[1].trim();
    return `${vietnameseReason}\n\nGhi chú: ${note}`;
  }

  return vietnameseReason;
};

/**
 * Data loading
 */
const loadVariantImage = async (variantId) => {
  if (!variantId) return "/placeholder-image.png";

  if (variantImageCache.value.has(variantId)) {
    return variantImageCache.value.get(variantId);
  }

  try {
    const images = await userService.getVariantImages(variantId);
    if (Array.isArray(images) && images.length > 0) {
      const primary = images.find((img) => img.isPrimary === true);
      const imageUrl = primary?.imageUrl || images[0].imageUrl;
      variantImageCache.value.set(variantId, imageUrl);
      return imageUrl;
    }
  } catch (error) {
    logger.error("Error loading variant image:", error);
  }

  return "/placeholder-image.png";
};

const fetchOrderDetail = async (silent = false) => {
  try {
    if (!silent) loading.value = true;

    const data = await userService.getMyOrderById(orderId.value);
    if (!data) throw new Error("Không tìm thấy dữ liệu đơn hàng");

    const normalized = {
      ...data,
      status: normalizeStatusForDisplay(data.status || "pending"),
      statusHistories: (Array.isArray(data.statusHistories)
        ? data.statusHistories.slice().reverse()
        : []
      ).map((h) => ({
        ...h,
        status: normalizeStatusForDisplay(h.status || "pending"),
      })),
      orderDetails: Array.isArray(data.orderDetails) ? data.orderDetails : [],
    };

    order.value = normalized;

    // preload images
    if (order.value.orderDetails?.length > 0) {
      order.value.orderDetails.forEach(async (item) => {
        await loadVariantImage(item.variantId);
      });
    }
  } catch (error) {
    logger.error("Error fetching order detail:", error);
    if (!silent) {
      notificationService.warning(
        "Cảnh báo",
        error.message || "Không thể tải chi tiết đơn hàng"
      );
    }
    if (!silent) order.value = null;
  } finally {
    if (!silent) loading.value = false;
  }
};

const refreshDetail = async (silent = true) => {
  await fetchOrderDetail(silent);
};

/**
 * Actions
 */
const canMarkAsReceived = (status) => getNormalizedStatus(status) === "Shipped";
const canRequestReturn = (status) => getNormalizedStatus(status) === "Completed";

const markAsReceived = async (id) => {
  if (!order.value) {
    notificationService.warning("Cảnh báo", "Không tìm thấy thông tin đơn hàng");
    return;
  }

  const total = order.value.totalAmount || order.value.total || 0;

  try {
    await confirmDialogService.confirm(
      `Bạn chắc chắn muốn thanh toán số tiền ${formatPrice(
        total
      )} cho đơn hàng này không?`,
      "Xác nhận thanh toán",
      {
        confirmButtonText: "Xác nhận",
        cancelButtonText: "Hủy",
        type: "warning",
      }
    );

    await userService.confirmOrderReceived(id);

    notificationService.success(
      "Thành công",
      `Bạn đã xác nhận thanh toán ${formatPrice(
        total
      )} và nhận hàng thành công!`
    );

    // refresh detail
    await fetchOrderDetail(true);
  } catch (error) {
    if (error !== "cancel") {
      logger.error("Error confirming order received:", error);
      const errorMessage =
        error.response?.data?.message ||
        error.message ||
        "Không thể xác nhận đã nhận hàng";
      notificationService.warning("Cảnh báo", errorMessage);
    }
  }
};

// View image
const viewImage = (url) => window.open(url, "_blank");

// Navigation
const goBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push({ name: "user-orders" }); // đổi theo tên route list của bạn
  }
};

/**
 * Return modal logic (reuse from your code)
 */
const openReturnModal = () => {
  showReturnModal.value = true;
};

const closeReturnModal = () => {
  showReturnModal.value = false;
  returnForm.value = {
    reason: "",
    note: "",
    images: [],
    bankName: "",
    bankAccountNumber: "",
    bankAccountHolder: "",
    returnMethod: "refund",
  };
  returnFormErrors.value = {};
};

const handleImageSelect = (event) => {
  const files = event.target.files;
  if (!files || files.length === 0) return;

  const currentCount = returnForm.value.images.length;

  if (currentCount >= 5) {
    notificationService.warning(
      "Giới hạn hình ảnh",
      "Bạn chỉ được tải lên tối đa 5 hình ảnh."
    );
    event.target.value = "";
    return;
  }

  const remaining = 5 - currentCount;
  const acceptedFiles = Array.from(files).slice(0, remaining);

  acceptedFiles.forEach((file) => {
    if (file.type.startsWith("image/")) {
      const reader = new FileReader();
      reader.onload = (e) => {
        returnForm.value.images.push(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  });

  if (files.length > remaining) {
    notificationService.warning(
      "Giới hạn hình ảnh",
      `Bạn chỉ có thể thêm tối đa ${remaining} hình nữa (tối đa 5 hình).`
    );
  }

  event.target.value = "";
};

const removeImage = (index) => {
  returnForm.value.images.splice(index, 1);
};

const validateReturnForm = () => {
  returnFormErrors.value = {};

  if (!returnForm.value.reason || !returnForm.value.reason.trim()) {
    returnFormErrors.value.reason = "Vui lòng chọn lý do hoàn trả";
    return false;
  }

  let valid = true;

  if (!returnForm.value.bankName.trim()) {
    returnFormErrors.value.bankName = "Vui lòng nhập tên ngân hàng";
    valid = false;
  }

  if (!returnForm.value.bankAccountNumber.trim()) {
    returnFormErrors.value.bankAccountNumber = "Vui lòng nhập số tài khoản";
    valid = false;
  } else if (!/^\d{6,20}$/.test(returnForm.value.bankAccountNumber)) {
    returnFormErrors.value.bankAccountNumber = "Số tài khoản không hợp lệ";
    valid = false;
  }

  if (!returnForm.value.bankAccountHolder.trim()) {
    returnFormErrors.value.bankAccountHolder =
      "Vui lòng nhập tên chủ tài khoản";
    valid = false;
  }

  return valid;
};

const submitReturnRequest = async () => {
  if (!validateReturnForm()) {
    notificationService.warning("Cảnh báo", "Vui lòng kiểm tra lại thông tin form");
    return;
  }

  if (!order.value) {
    notificationService.warning("Cảnh báo", "Không tìm thấy đơn hàng");
    return;
  }

  try {
    submittingReturn.value = true;

    const returnData = {
      reason: returnForm.value.reason,
      note: returnForm.value.note || null,
      images: returnForm.value.images.length > 0 ? returnForm.value.images : null,
      returnMethod: returnForm.value.returnMethod,
      bankName: returnForm.value.bankName,
      bankAccountNumber: returnForm.value.bankAccountNumber,
      bankAccountHolder: returnForm.value.bankAccountHolder,
    };

    await userService.createReturnRequest(order.value.id, returnData);

    notificationService.success(
      "Thành công",
      "Yêu cầu hoàn trả đã được gửi thành công! Chúng tôi sẽ xử lý trong thời gian sớm nhất."
    );

    closeReturnModal();

    // refresh detail to show returnRequest immediately
    await fetchOrderDetail(true);
  } catch (error) {
    logger.error("Error submitting return request:", error);
    const errorMessage =
      error.response?.data?.message ||
      error.message ||
      "Không thể gửi yêu cầu hoàn trả";
    notificationService.warning("Cảnh báo", errorMessage);

    if (error.response?.data?.validationErrors) {
      const validationErrors = error.response.data.validationErrors;
      Object.keys(validationErrors).forEach((key) => {
        returnFormErrors.value[key] = Array.isArray(validationErrors[key])
          ? validationErrors[key][0]
          : validationErrors[key];
      });
    }
  } finally {
    submittingReturn.value = false;
  }
};

/**
 * Polling (optional): refresh detail every 15s when tab visible
 */
const startPolling = () => {
  if (refreshInterval !== null) return;
  refreshInterval = setInterval(() => {
    if (document.visibilityState === "visible") {
      fetchOrderDetail(true);
    }
  }, 15000);
};

const stopPolling = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
    refreshInterval = null;
  }
};

const handleVisibilityChange = () => {
  if (document.visibilityState === "visible") {
    fetchOrderDetail(true);
    startPolling();
  } else {
    stopPolling();
  }
};

// initial load
onMounted(async () => {
  await fetchOrderDetail(false);
  startPolling();
  document.addEventListener("visibilitychange", handleVisibilityChange);
});

onUnmounted(() => {
  stopPolling();
  document.removeEventListener("visibilitychange", handleVisibilityChange);
});

// if route param changes (user navigates between details)
watch(
  () => orderId.value,
  async () => {
    order.value = null;
    variantImageCache.value = new Map();
    await fetchOrderDetail(false);
  }
);
</script>

<style scoped>
/* Ẩn scrollbar nhưng vẫn cho cuộn */
.hide-scrollbar {
  -ms-overflow-style: none; /* IE + Edge */
  scrollbar-width: none; /* Firefox */
}
.hide-scrollbar::-webkit-scrollbar {
  display: none; /* Chrome, Safari */
}
</style>

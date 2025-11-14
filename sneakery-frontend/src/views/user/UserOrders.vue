<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-6xl mx-auto px-4 space-y-6">
      <!-- Header -->
      <div class="relative overflow-hidden bg-gradient-to-r from-purple-600 via-purple-700 to-indigo-700 rounded-xl p-6 shadow-lg">
        <div class="absolute inset-0 bg-gradient-to-br from-purple-900/20 to-transparent"></div>
        <div class="relative flex items-center justify-between">
          <div>
            <h1 class="text-3xl md:text-4xl font-bold text-white mb-2 flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-white/20 backdrop-blur-sm flex items-center justify-center">
                <i class="material-icons text-white">shopping_bag</i>
              </div>
              Đơn hàng của tôi
            </h1>
            <p class="text-purple-100 text-sm md:text-base">Theo dõi và quản lý đơn hàng của bạn</p>
          </div>
          <button
            @click="fetchOrders()"
            :disabled="loading"
            class="flex items-center gap-2 px-4 py-2 bg-white/20 hover:bg-white/30 backdrop-blur-sm rounded-lg text-white font-medium transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            title="Làm mới danh sách đơn hàng"
          >
            <i class="material-icons" :class="{ 'animate-spin': loading }">refresh</i>
            <span class="hidden sm:inline">Làm mới</span>
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="space-y-4" role="status" aria-live="polite">
        <LoadingSkeleton
          v-for="n in 3"
          :key="n"
          type="list"
        />
        <span class="sr-only">Đang tải danh sách đơn hàng</span>
      </div>

      <!-- Empty State -->
      <div v-else-if="orders.length === 0" class="text-center py-20 bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-900 rounded-xl border border-gray-200 dark:border-gray-700">
        <div class="w-24 h-24 mx-auto mb-6 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
          <i class="material-icons text-5xl text-purple-600 dark:text-purple-400">shopping_bag</i>
        </div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Chưa có đơn hàng nào</h2>
        <p class="text-gray-600 dark:text-gray-400 mb-8 max-w-md mx-auto">Hãy mua sắm ngay để trải nghiệm dịch vụ của chúng tôi!</p>
        <router-link 
          to="/home/products" 
          class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02]"
        >
          <i class="material-icons text-lg">shopping_cart</i>
          Khám phá sản phẩm
        </router-link>
      </div>

      <!-- Orders List -->
      <div v-else class="space-y-4">
        <div
          v-for="order in orders"
          :key="order.id"
          class="group bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-lg hover:border-purple-300 dark:hover:border-purple-600 transition-all duration-200 hover:translate-y-[-2px]"
        >
          <!-- Order Header -->
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
            <div class="flex items-start gap-3 flex-1">
              <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center flex-shrink-0 shadow-sm">
                <i class="material-icons text-white text-lg">receipt</i>
              </div>
              <div class="flex-1 min-w-0">
                <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">Đơn hàng #{{ order.id }}</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1">
                  <i class="material-icons text-xs">schedule</i>
                  {{ formatDate(order.createdAt) }}
                </p>
                <!-- Return Request Badge -->
                <div v-if="order.returnRequest" class="mt-2">
                  <div class="flex items-center gap-2 flex-wrap">
                    <span class="inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs font-bold bg-orange-500 text-white">
                      HOÀN TRẢ
                    </span>
                    <span :class="[
                      'inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium',
                      getReturnRequestBadgeClass(order.returnRequest.status)
                    ]">
                      <i class="material-icons text-sm">{{ getReturnRequestIcon(order.returnRequest.status) }}</i>
                      {{ getReturnRequestText(order.returnRequest.status) }}
                    </span>
                  </div>
                  <p v-if="order.returnRequest.reason" class="text-xs text-gray-500 dark:text-gray-400 mt-1 ml-1 line-clamp-2">
                    {{ getReturnReasonText(order.returnRequest.reason, false) }}
                  </p>
                </div>
              </div>
            </div>
            <div>
              <span :class="[
                'inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-semibold',
                getStatusClass(order.status)
              ]">
                <i class="material-icons text-base">{{ getStatusIcon(order.status) }}</i>
                {{ getStatusText(order.status) }}
              </span>
            </div>
          </div>

          <!-- Order Items Summary -->
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4 p-4 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
            <div class="flex items-center gap-2 text-gray-600 dark:text-gray-400">
              <i class="material-icons text-base">inventory_2</i>
              <span class="font-medium">{{ order.totalItems || 0 }} sản phẩm</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-sm text-gray-600 dark:text-gray-400">Tổng tiền:</span>
              <p class="text-xl font-bold text-purple-600 dark:text-purple-400">{{ formatPrice(order.totalAmount || order.total || 0) }}</p>
            </div>
          </div>

          <!-- Order Actions -->
          <div class="flex flex-col sm:flex-row gap-3">
            <button 
              @click="viewOrderDetail(order.id)" 
              class="flex-1 px-4 py-2.5 border border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 hover:border-purple-300 dark:hover:border-purple-600 transition-all text-sm font-medium flex items-center justify-center gap-2"
            >
              <i class="material-icons text-base">visibility</i>
              Xem chi tiết
            </button>
            <button
              v-if="canReorder(order.status)"
              @click="reorder(order.id)"
              class="flex-1 px-4 py-2.5 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all text-sm shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center justify-center gap-2"
            >
              <i class="material-icons text-base">refresh</i>
              Đặt lại
            </button>
            <button
              v-if="canCancel(order.status)"
              @click="cancelOrder(order.id)"
              class="flex-1 px-4 py-2.5 border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-all text-sm font-medium flex items-center justify-center gap-2"
            >
              <i class="material-icons text-base">cancel</i>
              Hủy đơn
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Order Detail Modal -->
    <div v-if="showDetail && selectedOrder" class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4" @click.self="showDetail = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto animate-in fade-in zoom-in duration-200">
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 backdrop-blur-sm bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20">
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">receipt_long</i>
            Chi tiết đơn hàng #{{ selectedOrder.id }}
          </h3>
          <button 
            @click="showDetail = false" 
            class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            aria-label="Đóng"
          >
            <i class="material-icons">close</i>
          </button>
        </div>
        
        <div class="p-6 space-y-6">
          <!-- Order Items -->
          <div>
            <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">shopping_cart</i>
              Sản phẩm
            </h4>
            <div class="space-y-3">
              <div
                v-for="item in selectedOrder.orderDetails || []"
                :key="item.variantId"
                class="flex items-center gap-4 p-4 bg-white dark:bg-gray-800/80 backdrop-blur-sm border border-gray-200 dark:border-gray-700 rounded-xl hover:shadow-md hover:border-purple-300 dark:hover:border-purple-600 transition-all"
              >
                <div class="w-20 h-20 rounded-lg overflow-hidden bg-gray-100 dark:bg-gray-700 flex-shrink-0">
                  <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" class="w-full h-full object-cover" />
                </div>
                <div class="flex-1 min-w-0">
                  <h5 class="font-semibold text-gray-900 dark:text-gray-100 mb-1 truncate">{{ item.productName }}</h5>
                  <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">{{ item.brandName }}</p>
                  <div class="flex flex-wrap gap-2">
                    <span class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs text-gray-700 dark:text-gray-300">Size: {{ item.size }}</span>
                    <span class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs text-gray-700 dark:text-gray-300">Màu: {{ item.color }}</span>
                  </div>
                </div>
                <div class="text-right flex-shrink-0">
                  <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">Số lượng: x{{ item.quantity }}</p>
                  <p class="font-semibold text-lg text-purple-600 dark:text-purple-400">{{ formatPrice(item.totalPrice) }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Shipping Address -->
          <div>
            <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">location_on</i>
              Địa chỉ giao hàng
            </h4>
            <div v-if="selectedOrder.addressShipping" class="p-4 border border-gray-200 dark:border-gray-700 rounded-xl bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-700/50 dark:to-gray-800/50">
              <div class="flex items-start gap-3 mb-3">
                <div class="w-8 h-8 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center flex-shrink-0">
                  <i class="material-icons text-purple-600 dark:text-purple-400 text-sm">person</i>
                </div>
                <div class="flex-1">
                  <p class="font-semibold text-gray-900 dark:text-gray-100 mb-1">{{ selectedOrder.addressShipping.recipientName }}</p>
                  <p class="text-sm text-gray-600 dark:text-gray-400 flex items-center gap-1">
                    <i class="material-icons text-xs">phone</i>
                    {{ selectedOrder.addressShipping.phone }}
                  </p>
                </div>
              </div>
              <div class="space-y-1 text-sm text-gray-600 dark:text-gray-400 pl-11">
                <p class="flex items-center gap-1">
                  <i class="material-icons text-xs">place</i>
                  {{ selectedOrder.addressShipping.line1 }}
                </p>
                <p v-if="selectedOrder.addressShipping.line2" class="pl-6">{{ selectedOrder.addressShipping.line2 }}</p>
                <p class="flex items-center gap-1">
                  <i class="material-icons text-xs">location_city</i>
                  {{ selectedOrder.addressShipping.district }}, {{ selectedOrder.addressShipping.city }}
                </p>
              </div>
            </div>
          </div>

          <!-- Payment Info -->
          <div>
            <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">payment</i>
              Thanh toán
            </h4>
            <div v-if="selectedOrder.payment" class="p-5 border border-gray-200 dark:border-gray-700 rounded-xl bg-gradient-to-br from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20 space-y-4">
              <div class="flex justify-between items-center">
                <span class="text-gray-600 dark:text-gray-400 flex items-center gap-2">
                  <i class="material-icons text-base">credit_card</i>
                  Phương thức
                </span>
                <span class="font-medium text-gray-900 dark:text-gray-100">{{ getPaymentMethodText(selectedOrder.payment.method) }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-600 dark:text-gray-400 flex items-center gap-2">
                  <i class="material-icons text-base">info</i>
                  Trạng thái
                </span>
                <span :class="[
                  'inline-flex items-center gap-1 px-3 py-1 rounded-full text-sm font-medium',
                  selectedOrder.payment.status === 'completed' ? 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400' :
                  selectedOrder.payment.status === 'pending' ? 'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400' :
                  'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400'
                ]">
                  <i class="material-icons text-sm">{{ 
                    selectedOrder.payment.status === 'completed' ? 'check_circle' :
                    selectedOrder.payment.status === 'pending' ? 'schedule' :
                    'error'
                  }}</i>
                  {{ getPaymentStatusText(selectedOrder.payment.status) }}
                </span>
              </div>
              <div class="flex justify-between items-center pt-4 border-t border-gray-200 dark:border-gray-700">
                <span class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                  <i class="material-icons text-base">attach_money</i>
                  Tổng cộng
                </span>
                <span class="text-2xl font-bold text-purple-600 dark:text-purple-400">{{ formatPrice(selectedOrder.totalAmount || selectedOrder.total || 0) }}</span>
              </div>
            </div>
          </div>

          <!-- Return Request Info -->
          <div v-if="selectedOrder.returnRequest">
            <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
              <i class="material-icons text-orange-600 dark:text-orange-400">assignment_return</i>
              Thông tin hoàn trả
            </h4>
            <div class="p-5 border border-gray-200 dark:border-gray-700 rounded-xl bg-gradient-to-br from-orange-50 to-amber-50 dark:from-orange-900/20 dark:to-amber-900/20 space-y-4">
              <div class="flex justify-between items-center">
                <span class="text-gray-600 dark:text-gray-400 flex items-center gap-2">
                  <i class="material-icons text-base">info</i>
                  Trạng thái
                </span>
                <span :class="[
                  'inline-flex items-center gap-1 px-3 py-1 rounded-full text-sm font-medium',
                  getReturnRequestBadgeClass(selectedOrder.returnRequest.status)
                ]">
                  <i class="material-icons text-sm">{{ getReturnRequestIcon(selectedOrder.returnRequest.status) }}</i>
                  {{ getReturnRequestText(selectedOrder.returnRequest.status) }}
                </span>
              </div>
              <div v-if="selectedOrder.returnRequest.reason" class="pt-4 border-t border-gray-200 dark:border-gray-700">
                <span class="text-sm font-semibold text-gray-700 dark:text-gray-300 block mb-2">Lý do hoàn trả:</span>
                <p class="text-sm text-gray-600 dark:text-gray-400 whitespace-pre-wrap">{{ getReturnReasonText(selectedOrder.returnRequest.reason) }}</p>
              </div>
              <div v-if="selectedOrder.returnRequest.images && selectedOrder.returnRequest.images.length > 0" class="pt-4 border-t border-gray-200 dark:border-gray-700">
                <span class="text-sm font-semibold text-gray-700 dark:text-gray-300 block mb-2">Hình ảnh đính kèm:</span>
                <div class="grid grid-cols-3 gap-3">
                  <div
                    v-for="(image, index) in selectedOrder.returnRequest.images"
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
              <div v-if="selectedOrder.returnRequest.adminNote" class="pt-4 border-t border-gray-200 dark:border-gray-700">
                <span class="text-sm font-semibold text-gray-700 dark:text-gray-300 block mb-2">Ghi chú từ admin:</span>
                <p class="text-sm text-gray-600 dark:text-gray-400 whitespace-pre-wrap">{{ selectedOrder.returnRequest.adminNote }}</p>
              </div>
              <div class="flex justify-between items-center pt-4 border-t border-gray-200 dark:border-gray-700">
                <span class="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1">
                  <i class="material-icons text-xs">schedule</i>
                  Yêu cầu được tạo: {{ formatDateTime(selectedOrder.returnRequest.createdAt) }}
                </span>
                <span v-if="selectedOrder.returnRequest.approvedAt" class="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1">
                  <i class="material-icons text-xs">check_circle</i>
                  Đã duyệt: {{ formatDateTime(selectedOrder.returnRequest.approvedAt) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-between items-center p-6 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
          <button
            v-if="normalizeStatusForDisplay(selectedOrder.status) === 'Completed' && !selectedOrder.returnRequest"
            @click="showReturnModal = true"
            class="px-6 py-3 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-xl font-semibold hover:from-orange-600 hover:to-orange-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
          >
            <i class="material-icons text-lg">assignment_return</i>
            Hoàn trả
          </button>
          <button 
            @click="showDetail = false" 
            class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2 ml-auto"
          >
            <i class="material-icons text-lg">close</i>
            Đóng
          </button>
        </div>
      </div>
    </div>

    <!-- Return Request Modal -->
    <div v-if="showReturnModal" class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4" @click.self="showReturnModal = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto animate-in fade-in zoom-in duration-200" @click.stop>
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-orange-600 dark:text-orange-400">assignment_return</i>
            Yêu cầu hoàn trả đơn hàng #{{ selectedOrder?.id }}
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
          <!-- Return Form -->
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">
                Lý do hoàn trả <span class="text-red-500">*</span>
              </label>
              <select
                v-model="returnForm.reason"
                class="w-full px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
                :class="{ 'border-red-500 dark:border-red-500': returnFormErrors.reason }"
                required
              >
                <option value="">-- Chọn lý do --</option>
                <option value="defective">Lỗi sản phẩm</option>
                <option value="wrong_item">Giao sai hàng</option>
                <option value="size_issue">Không vừa size</option>
                <option value="changed_mind">Đổi ý</option>
                <option value="other">Khác</option>
              </select>
              <p v-if="returnFormErrors.reason" class="text-sm text-red-600 dark:text-red-400 mt-1">{{ returnFormErrors.reason }}</p>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">
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
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">
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
                  @click="$refs.imageInput.click()"
                  type="button"
                  class="w-full px-4 py-3 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg text-gray-600 dark:text-gray-400 hover:border-orange-500 dark:hover:border-orange-600 hover:text-orange-600 dark:hover:text-orange-400 transition-all flex items-center justify-center gap-2"
                >
                  <i class="material-icons text-lg">add_photo_alternate</i>
                  Chọn hình ảnh
                </button>
                <div v-if="returnForm.images.length > 0" class="grid grid-cols-3 gap-3">
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
                    >
                      <i class="material-icons text-sm">close</i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-end gap-3 p-6 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
          <button 
            @click="closeReturnModal" 
            class="px-6 py-3 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-xl font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-all flex items-center gap-2"
          >
            Hủy
          </button>
          <button
            @click="submitReturnRequest"
            :disabled="submittingReturn"
            class="px-6 py-3 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-xl font-semibold hover:from-orange-600 hover:to-orange-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
          >
            <div v-if="submittingReturn" class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
            <i v-else class="material-icons text-lg">send</i>
            {{ submittingReturn ? 'Đang gửi...' : 'Gửi yêu cầu' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, onActivated } from 'vue';
import { useAuthStore } from '@/stores/auth';
import toastService from '@/utils/toastService';
import confirmDialogService from '@/utils/confirmDialogService';
import userService from '@/services/userService';
import logger from '@/utils/logger';
import LoadingSkeleton from '@/components/common/LoadingSkeleton.vue';
import { formatPrice, formatDate, formatDateTime } from '@/utils/formatters';

const authStore = useAuthStore();

// State
const orders = ref([]);
const loading = ref(true);
const showDetail = ref(false);
const selectedOrder = ref(null);
let refreshInterval = null; // Polling interval

// Return Request Modal State
const showReturnModal = ref(false);
const submittingReturn = ref(false);
const returnForm = ref({
  reason: '',
  note: '',
  images: [] // Array of image URLs (base64 or file URLs)
});
const returnFormErrors = ref({});
const imageInput = ref(null);

// Normalize status từ backend format sang frontend format để hiển thị
const normalizeStatusForDisplay = (status) => {
  if (!status) return status;
  
  const statusMap = {
    'pending': 'Pending',
    'processing': 'Processing',
    'shipped': 'Shipped',
    'delivered': 'Completed', // Backend dùng "delivered" nhưng frontend hiển thị "Completed"
    'cancelled': 'Cancelled',
    'confirmed': 'Confirmed',
    'packed': 'Packed',
    'refunded': 'Refunded'
  };
  
  return statusMap[status.toLowerCase()] || status;
};

// Methods
const fetchOrders = async (silent = false) => {
  try {
    if (!silent) loading.value = true;
    const data = await userService.getMyOrders();
    
    // Normalize status từ backend format sang frontend format
    orders.value = data.map(order => ({
      ...order,
      status: normalizeStatusForDisplay(order.status)
    }));
    
    // Update selectedOrder nếu đang mở chi tiết
    if (showDetail.value && selectedOrder.value) {
      const updatedOrder = orders.value.find(o => o.id === selectedOrder.value.id);
      if (updatedOrder) {
        // Giữ nguyên returnRequest và statusHistories từ selectedOrder nếu có
        // Chỉ update các thông tin cơ bản từ order list
        selectedOrder.value = {
          ...selectedOrder.value,
          ...updatedOrder,
          status: normalizeStatusForDisplay(updatedOrder.status),
          // Giữ nguyên returnRequest từ selectedOrder (chi tiết) nếu có, nếu không thì lấy từ updatedOrder
          returnRequest: selectedOrder.value.returnRequest || updatedOrder.returnRequest
        };
      }
    }
  } catch (error) {
    logger.error('Error fetching orders:', error);
    if (!silent) {
      toastService.error('Lỗi', error.message || 'Không thể tải danh sách đơn hàng');
    }
  } finally {
    if (!silent) loading.value = false;
  }
};

const viewOrderDetail = async (orderId) => {
  try {
    const data = await userService.getMyOrderById(orderId);
    // Normalize status khi fetch chi tiết
    selectedOrder.value = {
      ...data,
      status: normalizeStatusForDisplay(data.status),
      // Normalize status histories nếu có
      statusHistories: data.statusHistories?.map(history => ({
        ...history,
        status: normalizeStatusForDisplay(history.status)
      })) || []
    };
    showDetail.value = true;
  } catch (error) {
    logger.error('Error fetching order detail:', error);
    toastService.error('Lỗi',error.message || 'Không thể tải chi tiết đơn hàng');
  }
};

// Handle window focus - refresh khi focus lại window
const handleFocus = () => {
  fetchOrders(true); // Silent refresh
};

// Start polling - auto refresh mỗi 15 giây
const startPolling = () => {
  // Clear existing interval nếu có
  if (refreshInterval) {
    clearInterval(refreshInterval);
  }
  
  // Chỉ poll khi trang visible
  refreshInterval = setInterval(() => {
    if (document.visibilityState === 'visible') {
      fetchOrders(true); // Silent refresh
    }
  }, 15000); // 15 giây
};

// Stop polling
const stopPolling = () => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
    refreshInterval = null;
  }
};

const cancelOrder = async (orderId) => {
  try {
    await confirmDialogService.confirm(
      'Bạn có chắc muốn hủy đơn hàng này?',
      'Xác nhận hủy đơn',
      {
        confirmButtonText: 'Hủy đơn',
        cancelButtonText: 'Quay lại',
        type: 'warning',
      }
    );

    // Gọi API hủy đơn hàng
    await userService.cancelOrder(orderId);
    
    toastService.success('Thành công', 'Đã hủy đơn hàng thành công');
    
    // Refresh danh sách đơn hàng
    await fetchOrders();
    
    // Nếu đang xem chi tiết đơn hàng này, đóng modal và refresh
    if (showDetail.value && selectedOrder.value?.id === orderId) {
      showDetail.value = false;
      selectedOrder.value = null;
    }
  } catch (error) {
    if (error !== 'cancel') {
      logger.error('Error canceling order:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Không thể hủy đơn hàng';
      toastService.error('Lỗi', errorMessage);
    }
  }
};

const reorder = async (orderId) => {
  try {
    // TODO: Implement reorder functionality
    toastService.info('Thông tin','Tính năng đang được phát triển');
  } catch (error) {
    logger.error('Error reordering:', error);
    toastService.error('Lỗi','Không thể đặt lại đơn hàng');
  }
};

const canCancel = (status) => {
  const normalizedStatus = getNormalizedStatus(status);
  // Chỉ cho phép hủy khi đơn hàng đang ở trạng thái "Pending" (chờ xác nhận)
  return normalizedStatus === 'Pending';
};

const canReorder = (status) => {
  const normalizedStatus = getNormalizedStatus(status);
  return ['Completed', 'Cancelled'].includes(normalizedStatus);
};

// Normalize status trước khi lấy class/icon/text
const getNormalizedStatus = (status) => {
  return normalizeStatusForDisplay(status) || 'Pending';
};

const getStatusClass = (status) => {
  const normalizedStatus = getNormalizedStatus(status);
  const statusMap = {
    'Pending': 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-400',
    'Processing': 'bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400',
    'Confirmed': 'bg-indigo-100 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-400',
    'Packed': 'bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400',
    'Shipped': 'bg-cyan-100 dark:bg-cyan-900/30 text-cyan-700 dark:text-cyan-400',
    'Completed': 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-400',
    'Cancelled': 'bg-rose-100 dark:bg-rose-900/30 text-rose-700 dark:text-rose-400',
    'Refunded': 'bg-orange-100 dark:bg-orange-900/30 text-orange-700 dark:text-orange-400',
  };
  return statusMap[normalizedStatus] || 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-400';
};

const getStatusIcon = (status) => {
  const normalizedStatus = getNormalizedStatus(status);
  const iconMap = {
    'Pending': 'schedule',
    'Processing': 'autorenew',
    'Confirmed': 'check_circle_outline',
    'Packed': 'inventory_2',
    'Shipped': 'local_shipping',
    'Completed': 'check_circle',
    'Cancelled': 'cancel',
    'Refunded': 'undo',
  };
  return iconMap[normalizedStatus] || 'help';
};

const getStatusText = (status) => {
  const normalizedStatus = getNormalizedStatus(status);
  const statusMap = {
    'Pending': 'Chờ xác nhận',
    'Processing': 'Đang xử lý',
    'Confirmed': 'Đã xác nhận',
    'Packed': 'Đã đóng gói',
    'Shipped': 'Đang giao hàng',
    'Completed': 'Hoàn thành',
    'Cancelled': 'Đã hủy',
    'Refunded': 'Đã hoàn tiền',
  };
  return statusMap[normalizedStatus] || normalizedStatus || status;
};

const getPaymentMethodText = (method) => {
  const methodMap = {
    'cod': 'Thanh toán khi nhận hàng (COD)',
    'online': 'Thanh toán trực tuyến',
  };
  return methodMap[method] || method;
};

const getPaymentStatusText = (status) => {
  const statusMap = {
    'pending': 'Chờ thanh toán',
    'completed': 'Đã thanh toán',
    'failed': 'Thanh toán thất bại',
  };
  return statusMap[status] || status;
};

// Return Request Helper Functions
const getReturnRequestBadgeClass = (status) => {
  if (!status) return 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-400';
  
  const statusLower = status.toLowerCase();
  const statusMap = {
    'pending': 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-400',
    'approved': 'bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400',
    'processing': 'bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400',
    'packed': 'bg-indigo-100 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-400',
    'refunded': 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400',
    'rejected': 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400',
  };
  return statusMap[statusLower] || 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-400';
};

const getReturnRequestIcon = (status) => {
  if (!status) return 'help';
  
  const statusLower = status.toLowerCase();
  const iconMap = {
    'pending': 'schedule',
    'approved': 'check_circle_outline',
    'processing': 'autorenew',
    'packed': 'inventory_2',
    'refunded': 'check_circle',
    'rejected': 'cancel',
  };
  return iconMap[statusLower] || 'help';
};

const getReturnRequestText = (status) => {
  if (!status) return status;
  
  const statusLower = status.toLowerCase();
  const statusMap = {
    'pending': 'Chờ xử lý',
    'approved': 'Đã xác nhận',
    'processing': 'Đang xử lý',
    'packed': 'Đã đóng gói',
    'refunded': 'Đã hoàn tiền',
    'rejected': 'Từ chối',
  };
  return statusMap[statusLower] || status;
};

// Map return reason code to Vietnamese text
const getReturnReasonText = (reason, includeNote = true) => {
  if (!reason) return reason;
  
  // Parse reason: if it contains "\n\nGhi chú:", split it
  const noteSeparator = '\n\nGhi chú:';
  const reasonCode = reason.includes(noteSeparator) 
    ? reason.split(noteSeparator)[0].trim() 
    : reason.trim();
  
  // Map reason code to Vietnamese text
  const reasonMap = {
    'defective': 'Lỗi sản phẩm',
    'wrong_item': 'Giao sai hàng',
    'size_issue': 'Không vừa size',
    'changed_mind': 'Đổi ý',
    'other': 'Khác',
  };
  
  const vietnameseReason = reasonMap[reasonCode] || reasonCode;
  
  // If there's a note and includeNote is true, include it
  if (includeNote && reason.includes(noteSeparator)) {
    const note = reason.split(noteSeparator)[1].trim();
    return `${vietnameseReason}\n\nGhi chú: ${note}`;
  }
  
  return vietnameseReason;
};

// Get return reason note only (if exists)
const getReturnReasonNote = (reason) => {
  if (!reason) return null;
  
  const noteSeparator = '\n\nGhi chú:';
  if (reason.includes(noteSeparator)) {
    return reason.split(noteSeparator)[1].trim();
  }
  
  return null;
};

// Get return reason code only (without note)
const getReturnReasonCode = (reason) => {
  if (!reason) return reason;
  
  const noteSeparator = '\n\nGhi chú:';
  return reason.includes(noteSeparator) 
    ? reason.split(noteSeparator)[0].trim() 
    : reason.trim();
};

// View image in modal
const viewImage = (imageUrl) => {
  // TODO: Implement image viewer modal
  window.open(imageUrl, '_blank');
};

// Format functions are now imported from @/utils/formatters

// Lifecycle
onMounted(() => {
  fetchOrders();
  
  // Đăng ký event listeners để refresh khi quay lại trang
  document.addEventListener('visibilitychange', handleVisibilityChange);
  window.addEventListener('focus', handleFocus);
  
  // Start polling để auto-refresh định kỳ
  startPolling();
});

onUnmounted(() => {
  // Cleanup event listeners
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  window.removeEventListener('focus', handleFocus);
  
  // Stop polling
  stopPolling();
});

// Nếu dùng keep-alive, refresh khi quay lại route
onActivated(() => {
  fetchOrders(true); // Silent refresh
  startPolling(); // Restart polling
});

// Handle visibility change - pause/resume polling
const handleVisibilityChange = () => {
  if (document.visibilityState === 'visible') {
    fetchOrders(true); // Silent refresh khi quay lại
    startPolling(); // Resume polling
  } else {
    stopPolling(); // Pause polling khi tab không visible
  }
};

// Return Request Methods
const closeReturnModal = () => {
  showReturnModal.value = false;
  returnForm.value = {
    reason: '',
    note: '',
    images: []
  };
  returnFormErrors.value = {};
};

const handleImageSelect = (event) => {
  const files = event.target.files;
  if (!files || files.length === 0) return;

  Array.from(files).forEach(file => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = (e) => {
        returnForm.value.images.push(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  });

  // Reset input để có thể chọn lại cùng file
  event.target.value = '';
};

const removeImage = (index) => {
  returnForm.value.images.splice(index, 1);
};

const validateReturnForm = () => {
  returnFormErrors.value = {};

  if (!returnForm.value.reason || !returnForm.value.reason.trim()) {
    returnFormErrors.value.reason = 'Vui lòng chọn lý do hoàn trả';
    return false;
  }

  return true;
};

const submitReturnRequest = async () => {
  if (!validateReturnForm()) {
    toastService.warning('Cảnh báo', 'Vui lòng kiểm tra lại thông tin form');
    return;
  }

  if (!selectedOrder.value) {
    toastService.error('Lỗi', 'Không tìm thấy đơn hàng');
    return;
  }

  try {
    submittingReturn.value = true;

    // Prepare return request data (orderId is passed as path variable, not in body)
    const returnData = {
      reason: returnForm.value.reason,
      note: returnForm.value.note || null,
      images: returnForm.value.images.length > 0 ? returnForm.value.images : null
    };

    // Call API to create return request
    await userService.createReturnRequest(selectedOrder.value.id, returnData);

    toastService.success('Thành công', 'Yêu cầu hoàn trả đã được gửi thành công! Chúng tôi sẽ xử lý trong thời gian sớm nhất.');

    // Close modal and reset form
    closeReturnModal();

    // Refresh orders list
    await fetchOrders();

    // Close detail modal
    showDetail.value = false;
    selectedOrder.value = null;

  } catch (error) {
    logger.error('Error submitting return request:', error);
    const errorMessage = error.response?.data?.message || error.message || 'Không thể gửi yêu cầu hoàn trả';
    toastService.error('Lỗi', errorMessage);

    // Show validation errors if any
    if (error.response?.data?.validationErrors) {
      const validationErrors = error.response.data.validationErrors;
      Object.keys(validationErrors).forEach(key => {
        returnFormErrors.value[key] = Array.isArray(validationErrors[key]) 
          ? validationErrors[key][0] 
          : validationErrors[key];
      });
    }
  } finally {
    submittingReturn.value = false;
  }
};
</script>

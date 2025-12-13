<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
      >
        <div class="flex items-center gap-4">
          <button
            @click="$router.push('/admin/orders')"
            class="p-2 text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
            title="Quay lại"
          >
            <i class="material-icons">arrow_back</i>
          </button>
          <div>
            <h1
              class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
            >
              <i class="material-icons text-purple-600 dark:text-purple-400"
                >receipt_long</i
              >
              Chi tiết đơn hàng #{{ orderId }}
            </h1>
            <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
              Thông tin chi tiết đơn hàng
            </p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <span
            v-if="order"
            class="inline-flex items-center px-3 py-1.5 text-sm font-medium rounded-full"
            :class="getStatusBadgeClass(order.status)"
          >
            {{ getStatusLabel(order.status) }}
          </span>
          <button
            v-if="order && getNextStep(order.status)"
            @click="confirmStatusChange(order, getNextStep(order.status))"
            class="flex items-center gap-2 px-4 py-2 bg-purple-500 hover:bg-purple-600 text-white rounded-lg transition-colors text-sm font-medium"
            :title="`Chuyển sang: ${getStatusLabel(getNextStep(order.status))}`"
          >
            <i class="material-icons text-base">arrow_forward</i>
            Tiếp theo: {{ getStatusLabel(getNextStep(order.status)) }}
          </button>
          <!-- Giao hàng thất bại -->
          <button
            v-if="order && canMarkDeliveryFailed(order.status)"
            @click="confirmStatusChange(order, 'Failed')"
            class="flex items-center gap-2 px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-lg transition-colors text-sm font-medium"
            title="Đánh dấu Giao hàng thất bại"
          >
            <i class="material-icons text-base">cancel_schedule_send</i>
            Giao hàng thất bại
          </button>

          <!-- <button
            v-if="order && getPreviousStep(order.status)"
            @click="confirmStatusChange(order, getPreviousStep(order.status))"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
            :title="`Quay lại: ${getStatusLabel(
              getPreviousStep(order.status)
            )}`"
          >
            <i class="material-icons text-base">arrow_back</i>
            Quay lại: {{ getStatusLabel(getPreviousStep(order.status)) }}
          </button> -->
          <button
            v-if="order"
            @click="handlePrintInvoice(order)"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">print</i>
            In hóa đơn
          </button>
          <button
            v-if="order"
            @click="exportOrderToPDF(order)"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">picture_as_pdf</i>
            Export PDF
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="text-center">
        <div class="space-y-4" role="status" aria-live="polite">
          <LoadingSkeleton v-for="n in 6" :key="n" type="custom" :lines="3" />
          <span class="sr-only">Đang tải chi tiết đơn hàng</span>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="p-6 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-xl"
    >
      <div class="flex items-center gap-3">
        <i class="material-icons text-red-600 dark:text-red-400">error</i>
        <div>
          <h3 class="font-semibold text-red-900 dark:text-red-100">
            Lỗi khi tải đơn hàng
          </h3>
          <p class="text-sm text-red-700 dark:text-red-300 mt-1">{{ error }}</p>
        </div>
        <button
          @click="fetchOrderDetail"
          class="ml-auto px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors text-sm font-medium"
        >
          Thử lại
        </button>
      </div>
    </div>

    <!-- Order Detail Content -->
    <div v-else-if="order" class="space-y-4">
      <!-- Status Step Flow -->
      <div
        class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <h3
          class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400"
            >timeline</i
          >
          Trạng thái đơn hàng
        </h3>
        <div class="flex items-center justify-between flex-wrap gap-4">
          <div
            class="flex items-center gap-2 flex-1 min-w-0 overflow-x-auto pb-2"
          >
            <div
              v-for="(step, index) in isPOSOrder
                ? POS_STATUS_STEPS
                : ORDER_STATUS_STEPS"
              :key="step"
              class="flex items-center flex-shrink-0"
            >
              <div
                class="flex flex-col items-center gap-2"
                :class="{
                  'opacity-50': !isFailed && currentStepIndex < index,
                }"
              >
                <!-- CIRCLE ICON -->
                <div
                  class="w-12 h-12 rounded-full flex items-center justify-center text-sm font-semibold transition-all"
                  :class="
                    // Nếu đơn failed & đang là ô cuối (Hoàn thành)
                    isFailed &&
                    index ===
                      (isPOSOrder
                        ? POS_STATUS_STEPS.length
                        : ORDER_STATUS_STEPS.length) -
                        1
                      ? 'bg-rose-600 text-white shadow-lg scale-110'
                      : currentStepIndex === index
                      ? 'bg-purple-500 text-white shadow-lg scale-110'
                      : currentStepIndex > index
                      ? 'bg-emerald-500 text-white'
                      : 'bg-gray-200 dark:bg-gray-700 text-gray-500 dark:text-gray-400'
                  "
                >
                  <!-- FAILED ICON -->
                  <i
                    v-if="
                      isFailed &&
                      index ===
                        (isPOSOrder
                          ? POS_STATUS_STEPS.length
                          : ORDER_STATUS_STEPS.length) -
                          1
                    "
                    class="material-icons text-base"
                  >
                    cancel
                  </i>

                  <!-- DONE ICON -->
                  <i
                    v-else-if="currentStepIndex > index"
                    class="material-icons text-base"
                  >
                    check
                  </i>

                  <!-- STEP NUMBER -->
                  <span v-else>{{ index + 1 }}</span>
                </div>
                <span
                  class="text-xs font-medium text-center whitespace-nowrap"
                  :class="
                    isFailed &&
                    index ===
                      (isPOSOrder
                        ? POS_STATUS_STEPS.length
                        : ORDER_STATUS_STEPS.length) -
                        1
                      ? 'text-rose-600 dark:text-rose-400 font-semibold'
                      : currentStepIndex === index
                      ? 'text-purple-600 dark:text-purple-400 font-semibold'
                      : currentStepIndex > index
                      ? 'text-emerald-600 dark:text-emerald-400'
                      : 'text-gray-500 dark:text-gray-400'
                  "
                >
                  {{
                    isPOSOrder
                      ? index === 0
                        ? currentStepIndex === 1
                          ? "Đã thanh toán"
                          : "Chờ thanh toán"
                        : "Hoàn thành"
                      : isFailed && index === ORDER_STATUS_STEPS.length - 1
                      ? "Giao hàng thất bại"
                      : getStatusLabel(step)
                  }}
                </span>
              </div>
              <!-- ARROW -->
              <i
                v-if="
                  index <
                  (isPOSOrder
                    ? POS_STATUS_STEPS.length
                    : ORDER_STATUS_STEPS.length) -
                    1
                "
                class="material-icons mx-2"
                :class="{
                  'text-emerald-400': currentStepIndex > index,
                  'text-purple-400': currentStepIndex === index,
                }"
                >arrow_forward</i
              >
            </div>
          </div>
          <!-- ⭐ UI cảnh báo điểm không đủ -->
          <div
            v-if="
              getNormalizedStatusValue(order.status) === 'Processing' &&
              getNextStep(order.status) === 'Packed' &&
              isInsufficientPoints
            "
            class="w-full mt-3 flex items-start gap-2 p-3 rounded-lg border border-amber-300 bg-amber-50 dark:bg-amber-900/20 dark:border-amber-600 text-amber-700 dark:text-amber-300 text-sm"
          >
            <i class="material-icons text-base">warning</i>
            <div>
              <p class="font-semibold">Không thể chuyển trạng thái</p>
              <p>
                Khách đã dùng
                <strong>{{ order.pointsUsed }}</strong> điểm nhưng chỉ còn
                <strong>{{ order.customerPointBalance }}</strong> điểm.
                <br />
                Không thể chuyển sang bước <strong>Đã đóng gói</strong>.
              </p>
            </div>
          </div>
        </div>
        <!-- Return Status Flow -->
        <div
          v-if="order?.returnRequest"
          class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 mt-4"
        >
          <h3
            class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
          >
            <i class="material-icons text-purple-600 dark:text-purple-400"
              >undo</i
            >
            Trạng thái đổi / trả hàng
          </h3>

          <div
            class="flex items-center gap-2 flex-1 min-w-0 overflow-x-auto pb-2"
          >
            <div
              v-for="(step, index) in [
                'Return_Pending',
                'Return_Approved',
                'Return_Completed',
              ]"
              :key="step"
              class="flex items-center flex-shrink-0"
            >
              <div
                class="flex flex-col items-center gap-2"
                :class="{ 'opacity-50': returnStepIndex < index }"
              >
                <!-- CIRCLE -->
                <div
                  class="w-12 h-12 rounded-full flex items-center justify-center text-sm font-semibold transition-all"
                  :class="
                    // REJECTED MODE
                    isReturnRejected && index === 1
                      ? 'bg-rose-600 text-white shadow-lg scale-110'
                      : isReturnRejected && index === 2
                      ? 'bg-rose-600 text-white shadow-lg scale-110'
                      : // COMPLETED NORMAL
                      returnStepIndex === index
                      ? 'bg-purple-500 text-white shadow-lg scale-110'
                      : returnStepIndex > index
                      ? 'bg-emerald-500 text-white'
                      : 'bg-gray-200 dark:bg-gray-700 text-gray-500 dark:text-gray-400'
                  "
                >
                  <!-- REJECTED ICON -->
                  <i
                    v-if="isReturnRejected && index >= 1"
                    class="material-icons text-base"
                  >
                    cancel
                  </i>

                  <!-- DONE ICON -->
                  <i
                    v-else-if="returnStepIndex > index"
                    class="material-icons text-base"
                  >
                    check
                  </i>

                  <!-- NUMBER -->
                  <span v-else>{{ index + 1 }}</span>
                </div>

                <!-- LABEL -->
                <span
                  class="text-xs font-medium text-center whitespace-nowrap"
                  :class="
                    isReturnRejected && index >= 1
                      ? 'text-rose-600 dark:text-rose-400 font-semibold'
                      : returnStepIndex === index
                      ? 'text-purple-600 dark:text-purple-400 font-semibold'
                      : returnStepIndex > index
                      ? 'text-emerald-600 dark:text-emerald-400'
                      : 'text-gray-500 dark:text-gray-400'
                  "
                >
                  {{
                    index === 0
                      ? "Chờ xử lý"
                      : index === 1
                      ? isReturnRejected
                        ? "Đã từ chối"
                        : "Đã duyệt"
                      : isReturnRejected
                      ? "Kết thúc (Từ chối)"
                      : "Hoàn tất"
                  }}
                </span>
              </div>

              <!-- ARROW -->
              <i
                v-if="index < 2"
                class="material-icons mx-2"
                :class="{
                  'text-emerald-400':
                    returnStepIndex > index && !isReturnRejected,
                  'text-rose-400': isReturnRejected && index >= 0,
                  'text-purple-400':
                    returnStepIndex === index && !isReturnRejected,
                  'text-gray-300 dark:text-gray-600': returnStepIndex < index,
                }"
              >
                arrow_forward
              </i>
            </div>
          </div>
        </div>
      </div>

      <!-- Customer & Shipping Combined Section (with internal titles) -->
      <div
        class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <h3
          class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-6 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400"
            >person</i
          >
          Thông tin khách hàng & giao hàng
        </h3>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-10">
          <!-- LEFT: Customer Info -->
          <div>
            <h4
              class="text-sm font-semibold text-gray-800 dark:text-gray-200 mb-3 flex items-center gap-2"
            >
              <i class="material-icons text-purple-500 text-base"
                >account_circle</i
              >
              Thông tin khách hàng
            </h4>

            <div class="space-y-3">
              <div>
                <span class="text-sm text-gray-600 dark:text-gray-400"
                  >Tên khách hàng:</span
                >
                <p
                  class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
                >
                  {{ order.customerName || "N/A" }}
                </p>
              </div>

              <div>
                <span class="text-sm text-gray-600 dark:text-gray-400"
                  >Email:</span
                >
                <p
                  class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
                >
                  {{ order.customerEmail || "N/A" }}
                </p>
              </div>

              <div>
                <span class="text-sm text-gray-600 dark:text-gray-400"
                  >Ngày đặt:</span
                >
                <p
                  class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
                >
                  {{ formatDateTime(order.createdAt) }}
                </p>
              </div>

              <div>
                <span class="text-sm text-gray-600 dark:text-gray-400"
                  >Tổng tiền:</span
                >
                <p
                  class="text-xl font-bold text-purple-600 dark:text-purple-400 mt-1"
                >
                  {{ formatCurrency(order.totalAmount) }}
                </p>
              </div>
            </div>
          </div>

          <!-- RIGHT: Shipping Info -->
          <div v-if="order.addressShipping">
            <h4
              class="text-sm font-semibold text-gray-800 dark:text-gray-200 mb-3 flex items-center gap-2"
            >
              <i class="material-icons text-purple-500 text-base"
                >local_shipping</i
              >
              Địa chỉ giao hàng
            </h4>

            <div class="space-y-1 text-gray-700 dark:text-gray-300">
              <p class="font-medium">
                {{ order.addressShipping.recipientName }}
              </p>
              <p>{{ order.addressShipping.phone }}</p>
              <p>{{ order.addressShipping.line1 }}</p>
              <p v-if="order.addressShipping.line2">
                {{ order.addressShipping.line2 }}
              </p>

              <p>
                {{ order.addressShipping.ward }},
                {{ order.addressShipping.district }},
                {{ order.addressShipping.city }}
              </p>

              <p v-if="order.addressShipping.postalCode">
                Mã bưu điện: {{ order.addressShipping.postalCode }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Order Items -->
      <div
        class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <h3
          class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400"
            >shopping_cart</i
          >
          Sản phẩm đã đặt
        </h3>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead
              class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600"
            >
              <tr>
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                >
                  Sản phẩm
                </th>
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                >
                  SKU
                </th>
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                >
                  Size
                </th>
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                >
                  Màu
                </th>
                <th
                  class="px-4 py-3 text-right text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                >
                  Số lượng
                </th>
                <th
                  class="px-4 py-3 text-right text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                >
                  Đơn giá
                </th>
                <th
                  class="px-4 py-3 text-right text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                >
                  Thành tiền
                </th>
              </tr>
            </thead>

            <!-- BODY -->
            <tbody
              class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700"
            >
              <tr
                v-for="(item, index) in order.orderDetails || []"
                :key="index"
                class="hover:bg-gray-50 dark:hover:bg-gray-900/50"
              >
                <td class="px-4 py-4">
                  <div class="flex items-center gap-3">
                    <img
                      v-if="item.imageUrl"
                      :src="item.imageUrl"
                      :alt="item.productName"
                      class="w-16 h-16 object-cover rounded-lg"
                    />
                    <div>
                      <div
                        class="text-sm font-medium text-gray-900 dark:text-gray-100"
                      >
                        {{ item.productName }}
                      </div>
                      <div class="text-xs text-gray-500 dark:text-gray-400">
                        {{ item.brandName || "N/A" }}
                      </div>
                    </div>
                  </div>
                </td>

                <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">
                  {{ item.sku }}
                </td>

                <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">
                  {{ item.size }}
                </td>

                <td class="px-4 py-4">
                  <div class="flex items-center gap-2">
                    <span
                      class="w-4 h-4 rounded-full border border-gray-300 dark:border-gray-600"
                      :style="{ backgroundColor: getColorHex(item.color) }"
                    ></span>
                    <span class="text-sm text-gray-900 dark:text-gray-100">
                      {{ item.color }}
                    </span>
                  </div>
                </td>

                <td
                  class="px-4 py-4 text-right text-sm text-gray-900 dark:text-gray-100"
                >
                  {{ item.quantity }}
                </td>

                <td
                  class="px-4 py-4 text-right text-sm text-gray-900 dark:text-gray-100"
                >
                  {{ formatCurrency(item.unitPrice) }}
                </td>

                <td
                  class="px-4 py-4 text-right text-sm font-semibold text-gray-900 dark:text-gray-100"
                >
                  {{ formatCurrency(item.totalPrice) }}
                </td>
              </tr>
            </tbody>

            <!-- FOOTER — ORDER TOTAL SUMMARY -->
            <tfoot class="bg-gray-50 dark:bg-gray-700/50">
              <tr>
                <td colspan="7" class="px-4 py-2">
                  <div
                    class="max-w-md ml-auto space-y-1 text-sm text-gray-700 dark:text-gray-300"
                  >
                    <div class="flex justify-between">
                      <span>Tạm tính:</span>
                      <span>{{ formatCurrency(order.subtotal) }}</span>
                    </div>

                    <div
                      v-if="order.discountAmount > 0"
                      class="flex justify-between text-amber-600 dark:text-amber-400"
                    >
                      <span>Giảm giá coupon:</span>
                      <span>- {{ formatCurrency(order.discountAmount) }}</span>
                    </div>

                    <div
                      v-if="order.pointsUsed > 0"
                      class="flex justify-between text-blue-600 dark:text-blue-400"
                    >
                      <span>Điểm thưởng ({{ order.pointsUsed }} điểm):</span>
                      <span>- {{ formatCurrency(order.pointsDiscount) }}</span>
                    </div>

                    <div class="flex justify-between">
                      <span>Phí vận chuyển:</span>
                      <span>{{ formatCurrency(order.shippingFee) }}</span>
                    </div>

                    <div class="flex justify-between">
                      <span>VAT:</span>
                      <span>{{ formatCurrency(order.taxAmount) }}</span>
                    </div>

                    <div
                      class="flex justify-between text-lg font-bold text-purple-600 dark:text-purple-400 pt-2 border-t border-gray-300 dark:border-gray-600"
                    >
                      <span>Tổng tiền:</span>
                      <span>{{ formatCurrency(order.totalAmount) }}</span>
                    </div>
                  </div>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>

      <!-- Payment Info -->
      <div
        v-if="order.payment"
        class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <h3
          class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400"
            >payment</i
          >
          Thông tin thanh toán
        </h3>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <!-- Payment Method -->
          <div>
            <span class="text-sm text-gray-600 dark:text-gray-400"
              >Phương thức:</span
            >
            <p
              class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
            >
              {{ getPaymentMethodLabel(order.payment.paymentMethod) }}
            </p>
          </div>

          <!-- Payment Status -->
          <div>
            <span class="text-sm text-gray-600 dark:text-gray-400"
              >Trạng thái:</span
            >
            <p
              class="text-base font-medium mt-1 inline-flex items-center px-2 py-1 rounded-full text-xs"
              :class="getPaymentStatusClass(order.payment.status)"
            >
              {{ getPaymentStatusLabel(order.payment.status) }}
            </p>
          </div>

          <!-- Payment Amount -->
          <div>
            <span class="text-sm text-gray-600 dark:text-gray-400"
              >Số tiền thanh toán:</span
            >
            <p
              class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
            >
              {{ formatCurrency(order.payment.amount) }}
            </p>
          </div>

          <!-- Paid At -->
          <div v-if="order.payment.paidAt">
            <span class="text-sm text-gray-600 dark:text-gray-400"
              >Ngày thanh toán:</span
            >
            <p
              class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
            >
              {{ formatDateTime(order.payment.paidAt) }}
            </p>
          </div>
        </div>
      </div>
      <!-- Return Request -->
      <div
        v-if="order.returnRequest"
        class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <h3
          class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400">
            undo
          </i>
          Yêu cầu hoàn tiền / trả hàng
        </h3>

        <!-- Info Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Left -->
          <div class="space-y-4">
            <div>
              <span class="text-sm text-gray-600 dark:text-gray-400"
                >Người yêu cầu:</span
              >
              <p
                class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
              >
                {{ order.returnRequest.customerName }}
              </p>
            </div>

            <div>
              <span class="text-sm text-gray-600 dark:text-gray-400"
                >Email:</span
              >
              <p
                class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
              >
                {{ order.returnRequest.customerEmail }}
              </p>
            </div>

            <div>
              <span class="text-sm text-gray-600 dark:text-gray-400"
                >Ngày gửi:</span
              >
              <p
                class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1"
              >
                {{ formatDateTime(order.returnRequest.createdAt) }}
              </p>
            </div>

            <div>
              <span class="text-sm text-gray-600 dark:text-gray-400"
                >Trạng thái:</span
              >
              <p
                class="inline-flex items-center px-3 py-1.5 text-sm font-medium mt-1 rounded-full"
                :class="getStatusBadgeClass(order.returnRequest.status)"
              >
                {{ getStatusLabel(order.returnRequest.status) }}
              </p>
            </div>
          </div>

          <!-- Right -->
          <div>
            <span class="text-sm text-gray-600 dark:text-gray-400">Lý do:</span>

            <!-- Lý do -->
            <p
              class="text-base font-medium text-gray-900 dark:text-gray-100 mt-1 bg-gray-50 dark:bg-gray-700/50 p-3 rounded-md"
            >
              {{ getReturnReason(order.returnRequest.reason) }}
            </p>

            <!-- Ghi chú khách hàng -->
            <div
              v-if="getReturnCustomerNote(order.returnRequest.reason)"
              class="mt-2"
            >
              <span class="text-xs text-gray-500 dark:text-gray-400"
                >Ghi chú của khách:</span
              >
              <p
                class="text-sm font-medium text-gray-900 dark:text-gray-100 mt-1 bg-amber-50 dark:bg-amber-900/20 p-2 rounded-md border border-amber-200 dark:border-amber-700"
              >
                {{ getReturnCustomerNote(order.returnRequest.reason) }}
              </p>
            </div>
          </div>
        </div>

        <!-- Images -->
        <div class="mt-6">
          <span class="text-sm text-gray-600 dark:text-gray-400"
            >Hình ảnh đính kèm:</span
          >

          <div
            v-if="
              order.returnRequest.images &&
              order.returnRequest.images.length > 0
            "
            class="flex gap-3 flex-wrap mt-3"
          >
            <img
              v-for="(img, i) in order.returnRequest.images"
              :key="i"
              :src="img"
              class="w-32 h-32 object-cover rounded-lg cursor-pointer border border-gray-200 dark:border-gray-700 hover:scale-105 transition-all"
              @click="openImagePreview(img)"
            />
          </div>

          <p v-else class="text-sm text-gray-500 dark:text-gray-400 mt-1">
            Không có hình ảnh
          </p>
        </div>
        <!-- Bank Refund Info -->
        <div
          v-if="order.returnRequest.returnMethod === 'refund'"
          class="mt-6 p-4 bg-blue-50 dark:bg-blue-900/30 rounded-lg border border-blue-200 dark:border-blue-700"
        >
          <span
            class="block text-sm font-semibold text-blue-700 dark:text-blue-300 mb-3"
          >
            Thông tin hoàn tiền qua ngân hàng
          </span>

          <!-- Phương thức -->
          <div class="flex justify-between text-sm py-1">
            <span class="text-gray-600 dark:text-gray-400">Phương thức:</span>
            <span class="font-medium text-gray-900 dark:text-gray-100">
              {{ getReturnMethodText(order.returnRequest.returnMethod) }}
            </span>
          </div>

          <!-- Ngân hàng -->
          <div
            v-if="order.returnRequest.bankName"
            class="flex justify-between text-sm py-1"
          >
            <span class="text-gray-600 dark:text-gray-400">Ngân hàng:</span>
            <span class="font-medium text-gray-900 dark:text-gray-100">
              {{ order.returnRequest.bankName }}
            </span>
          </div>

          <!-- Số tài khoản -->
          <div
            v-if="order.returnRequest.bankAccountNumber"
            class="flex justify-between text-sm py-1"
          >
            <span class="text-gray-600 dark:text-gray-400">Số tài khoản:</span>
            <span
              class="font-semibold text-gray-900 dark:text-gray-100 tracking-wide"
            >
              {{ order.returnRequest.bankAccountNumber }}
            </span>
          </div>

          <!-- Chủ tài khoản -->
          <div
            v-if="order.returnRequest.bankAccountHolder"
            class="flex justify-between text-sm py-1"
          >
            <span class="text-gray-600 dark:text-gray-400">Chủ tài khoản:</span>
            <span
              class="font-semibold text-gray-900 dark:text-gray-100 uppercase"
            >
              {{ order.returnRequest.bankAccountHolder }}
            </span>
          </div>
        </div>

        <!-- Admin Note -->
        <div v-if="order.returnRequest.adminNote" class="mt-6">
          <span class="text-sm text-gray-600 dark:text-gray-400"
            >Ghi chú của admin:</span
          >
          <p
            class="text-base mt-1 p-3 bg-gray-50 dark:bg-gray-700/50 rounded-md"
          >
            {{ order.returnRequest.adminNote }}
          </p>
        </div>

        <!-- Action Buttons -->
        <div class="mt-6 flex gap-3">
          <button
            v-if="order.returnRequest.status === 'pending'"
            @click="approveReturn(order.returnRequest.id)"
            class="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg flex items-center gap-2"
          >
            <i class="material-icons text-base">check_circle</i>
            Duyệt yêu cầu
          </button>

          <button
            v-if="order.returnRequest.status === 'pending'"
            @click="rejectReturn(order.returnRequest.id)"
            class="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-lg flex items-center gap-2"
          >
            <i class="material-icons text-base">cancel</i>
            Từ chối yêu cầu
          </button>

          <button
            v-if="order.returnRequest.status === 'approved'"
            @click="openReturnModal(order.returnRequest.id)"
            class="px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg flex items-center gap-2"
          >
            <i class="material-icons text-base">check</i>
            Hoàn tất xử lý
          </button>
        </div>
      </div>

      <!-- Status History -->
      <div
        v-if="order.statusHistories && order.statusHistories.length > 0"
        class="p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <h3
          class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400"
            >history</i
          >
          Lịch sử thay đổi trạng thái
        </h3>
        <div class="space-y-4">
          <div
            v-for="(history, index) in sortedStatusHistories"
            :key="history.id"
            class="flex items-start gap-4 pb-4 border-b border-gray-200 dark:border-gray-700 last:border-0"
            :class="{ 'opacity-60': index > 0 }"
          >
            <div
              class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
              :class="
                index === 0
                  ? 'bg-purple-100 dark:bg-purple-900/30'
                  : 'bg-gray-100 dark:bg-gray-700'
              "
            >
              <i
                class="material-icons text-sm"
                :class="
                  index === 0
                    ? 'text-purple-600 dark:text-purple-400'
                    : 'text-gray-400 dark:text-gray-500'
                "
              >
                {{ index === 0 ? "check_circle" : "radio_button_unchecked" }}
              </i>
            </div>
            <div class="flex-1">
              <div class="text-sm font-medium text-gray-900 dark:text-gray-100">
                {{ getStatusLabel(history.status) }}
              </div>
              <div class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                {{ formatDateTime(history.changedAt || history.createdAt) }}
              </div>
              <div
                v-if="history.note"
                class="text-xs text-gray-500 dark:text-gray-400 mt-1 italic"
              >
                {{ history.note }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Return Complete Custom Modal -->
    <div
      v-if="showReturnModal"
      class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-xl w-full max-w-3xl shadow-xl overflow-hidden border border-gray-200 dark:border-gray-700"
      >
        <!-- Header -->
        <div
          class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700"
        >
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100">
            Xác nhận hoàn tất xử lý đổi trả
          </h2>
          <button
            class="text-gray-500 hover:text-gray-800 dark:text-gray-400 dark:hover:text-gray-200"
            @click="closeReturnModal"
            :disabled="isSubmitting"
          >
            <i class="material-icons">close</i>
          </button>
        </div>

        <!-- Body -->
        <div class="p-4 overflow-y-auto max-h-[65vh]">
          <h4
            class="text-sm font-semibold text-gray-800 dark:text-gray-200 mb-3"
          >
            Sản phẩm trong yêu cầu trả hàng
          </h4>

          <!-- Loading -->
          <div v-if="isReturnLoading" class="py-10 text-center text-gray-400">
            Đang tải dữ liệu...
          </div>

          <!-- Table -->
          <table v-else class="w-full text-sm">
            <thead
              class="text-gray-600 dark:text-gray-300 border-b dark:border-gray-700"
            >
              <tr>
                <th class="py-2 text-left">Sản phẩm</th>
                <th class="py-2 text-center">SL</th>
                <th class="py-2 text-center">SL Hỏng</th>
                <th class="py-2 text-right">Giá</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="(it, idx) in selectedReturn.items"
                :key="idx"
                class="border-b dark:border-gray-700"
              >
                <!-- PRODUCT -->
                <td class="py-2">
                  <div class="flex items-center gap-2 dark:text-gray-100">
                    <img
                      :src="it.productImage"
                      class="w-10 h-10 rounded-lg border dark:border-gray-700"
                    />
                    <div class="flex flex-col">
                      <span>{{ it.productName }}</span>
                      <span class="text-xs text-gray-500">
                        {{ it.variant }}
                      </span>
                    </div>
                  </div>
                </td>

                <!-- QUANTITY -->
                <td class="py-2 text-center dark:text-white">
                  {{ it.quantity }}
                </td>

                <!-- DAMAGED INPUT -->
                <td class="py-2 text-center">
                  <input
                    type="number"
                    v-model.number="it.damagedQuantity"
                    :disabled="
                      isSubmitting || selectedReturn.status === 'completed'
                    "
                    min="0"
                    :max="it.quantity"
                    @input="validateDamaged(it)"
                    class="w-16 px-2 py-1 text-xs text-center rounded border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                  />
                </td>

                <!-- PRICE -->
                <td class="py-2 text-right dark:text-white">
                  {{ formatPrice(it.unitPrice) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Footer -->
        <div
          class="p-4 border-t border-gray-200 dark:border-gray-700 flex justify-end gap-3"
        >
          <button
            class="px-4 py-2 rounded-lg bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300"
            @click="closeReturnModal"
            :disabled="isSubmitting"
          >
            Hủy
          </button>

          <button
            class="px-4 py-2 rounded-lg bg-purple-600 hover:bg-purple-700 text-white flex items-center gap-2 disabled:opacity-60"
            @click="confirmReturnCompletion"
            :disabled="isSubmitting || isReturnLoading"
          >
            <i class="material-icons text-base">check</i>
            <span v-if="isSubmitting">Đang xử lý...</span>
            <span v-else>Xác nhận</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Status Change Confirmation Dialog -->
    <ConfirmDialog
      v-model="showStatusConfirm"
      type="warning"
      title="Xác nhận thay đổi trạng thái"
      :message="`Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng #${
        orderToUpdate?.id
      } từ '${getStatusLabel(oldStatus)}' sang '${getStatusLabel(newStatus)}'?`"
      description="Hành động này sẽ cập nhật trạng thái đơn hàng."
      confirm-text="Xác nhận"
      cancel-text="Hủy"
      :loading="updating"
      @confirm="handleStatusUpdate"
      @cancel="handleCancelStatusChange"
    />
  </div>
  <!-- Image Preview Modal -->
  <div
    v-if="previewImage"
    class="fixed inset-0 bg-black/70 z-50 flex items-center justify-center p-4"
    @click="previewImage = null"
  >
    <img
      :src="previewImage"
      class="max-w-[90%] max-h-[90%] rounded-lg shadow-lg"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAdminStore } from "@/stores/admin";
import AdminService from "@/services/adminService";
import notificationService from "@/utils/notificationService";
import { printInvoice } from "@/utils/pdfGenerator";
import logger from "@/utils/logger";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import {
  formatPrice,
  formatCurrency,
  formatDateTime,
} from "@/utils/formatters";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";

const route = useRoute();
const router = useRouter();
const adminStore = useAdminStore();

const orderId = ref(route.params.id);
const order = ref(null);
const loading = ref(false);
const error = ref(null);

// Status change confirmation
const showStatusConfirm = ref(false);
const orderToUpdate = ref(null);
const oldStatus = ref("");
const newStatus = ref("");
const updating = ref(false);

// const order = ref(null);
// const loading = ref(false);
// const error = ref(null);

// Return modal state
const showReturnModal = ref(false);
const selectedReturn = ref({
  id: null,
  items: [],
  status: null,
});
const isReturnLoading = ref(false);
const isSubmitting = ref(false);

// Định nghĩa flow steps theo thứ tự
const ORDER_STATUS_STEPS = [
  "Pending", // 0: Chờ xử lý
  "Confirmed", // 1: Đã xác nhận
  "Processing", // 2: Đang xử lý
  "Packed", // 3: Đã đóng gói
  "Shipped", // 4: Đã gửi hàng
  "Completed", // 5: Hoàn thành
];

const POS_STATUS_STEPS = ["Pending", "Completed"];

// Normalize status
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
    rejected: "Rejected",
    approved: "Approved",
    completed: "Completed",
    return_pending: "Return_Pending",
    return_approved: "Return_Approved",
    return_rejected: "Return_Rejected",
    return_completed: "Return_Completed",
  };
  return statusMap[status.toLowerCase()] || status;
};

const previewImage = ref(null);
const openImagePreview = (img) => {
  previewImage.value = img;
};
const approveReturn = async (returnId) => {
  try {
    const res = await AdminService.approveReturn(returnId);
    notificationService.success("Đã duyệt yêu cầu");
    fetchOrderDetail();
  } catch (err) {
    notificationService.apiError(err, "Không thể duyệt yêu cầu");
  }
};

const rejectReturn = async (returnId) => {
  try {
    const res = await AdminService.rejectReturn(returnId);
    notificationService.success("Đã từ chối yêu cầu");
    fetchOrderDetail();
  } catch (err) {
    notificationService.apiError(err, "Không thể từ chối yêu cầu");
  }
};

// const markReturnCompleted = async (returnId) => {
//   try {
//     const res = await AdminService.completeReturn(returnId);
//     notificationService.success("Đã hoàn tất xử lý đổi trả");
//     fetchOrderDetail();
//   } catch (err) {
//     notificationService.apiError(err, "Không thể hoàn tất xử lý");
//   }
// };

const getNormalizedStatusValue = (status) => {
  if (!status) return "Pending";

  // ✅ Nếu là POS & đã thanh toán thì ép về Completed
  if (
    isPOSOrder.value &&
    order.value?.payment?.status?.toLowerCase() === "completed"
  ) {
    return "Completed";
  }

  const normalized = normalizeStatusForDisplay(status);
  const validOptions = [
    "Pending",
    "Processing",
    "Shipped",
    "Completed",
    "Cancelled",
    "Confirmed",
    "Packed",
    "Refunded",
    "Failed",
    "Return_Pending",
    "Return_Approved",
    "Return_Rejected",
    "Return_Completed",
  ];
  return validOptions.includes(normalized) ? normalized : "Pending";
};

const isPOSOrder = computed(() => {
  if (!order.value) return false;

  return (
    order.value.orderNumber?.startsWith("POS-") ||
    order.value.addressShipping?.line2 === "Bán tại quầy"
  );
});

// Lấy index của status trong flow
const getStatusStepIndex = (status) => {
  const normalized = getNormalizedStatusValue(status);
  return ORDER_STATUS_STEPS.indexOf(normalized);
};

// Lấy step tiếp theo (nếu có)
const getNextStep = (currentStatus) => {
  const currentIndex = getStatusStepIndex(currentStatus);
  if (currentIndex === -1 || currentIndex >= ORDER_STATUS_STEPS.length - 1) {
    return null; // Đã ở bước cuối
  }
  return ORDER_STATUS_STEPS[currentIndex + 1];
};

// Lấy step trước đó (nếu có)
const getPreviousStep = (currentStatus) => {
  const currentIndex = getStatusStepIndex(currentStatus);
  if (currentIndex <= 0) {
    return null; // Đã ở bước đầu
  }

  // Completed có thể quay về Processing
  const normalized = getNormalizedStatusValue(currentStatus);
  if (normalized === "Completed") {
    return "Processing";
  }

  return ORDER_STATUS_STEPS[currentIndex - 1];
};

// const showReturnModal = ref(false);
// const selectedReturn = ref({
//   id: null,
//   items: [],
//   status: null,
// });

// Kiểm tra xem có thể chuyển đến status mới không
// const canChangeToStatus = (currentStatus, targetStatus) => {
//   const currentIndex = getStatusStepIndex(currentStatus);
//   const newIndex = getStatusStepIndex(targetStatus);

//   if (currentIndex === -1 || newIndex === -1) {
//     return false; // Status không hợp lệ
//   }

//   // Cho phép chuyển đến step tiếp theo
//   if (newIndex === currentIndex + 1) {
//     return true;
//   }

//   // Cho phép quay lại step trước đó
//   if (newIndex === currentIndex - 1) {
//     return true;
//   }

//   // Completed có thể quay về Processing
//   if (
//     getNormalizedStatusValue(currentStatus) === "Completed" &&
//     targetStatus === "Processing"
//   ) {
//     return true;
//   }

//   return false;
// };
const canChangeToStatus = (currentStatus, targetStatus) => {
  const currentIndex = getStatusStepIndex(currentStatus);
  const newIndex = getStatusStepIndex(targetStatus);
  const normalizedCurrent = getNormalizedStatusValue(currentStatus);

  // ✅ SPECIAL: Cho phép Shipped → Cancelled (Giao hàng thất bại)
  if (normalizedCurrent === "Shipped" && targetStatus === "Failed") {
    return true;
  }

  // ✅ SPECIAL: Cho phép Shipped → Refunded (nếu bạn muốn hoàn tiền sau khi giao thất bại)
  if (normalizedCurrent === "Shipped" && targetStatus === "Refunded") {
    return true;
  }

  if (currentIndex === -1 || newIndex === -1) {
    return false;
  }

  // ✅ BLOCK CASE: Shipped → Completed khi chưa thanh toán
  if (normalizedCurrent === "Shipped" && targetStatus === "Completed") {
    const paymentStatus = order.value?.payment?.status?.toLowerCase();

    if (!paymentStatus || paymentStatus === "pending") {
      notificationService.warning(
        "Không thể hoàn thành đơn hàng",
        "Đơn hàng chưa được thanh toán. Vui lòng xác nhận thanh toán trước khi hoàn thành."
      );
      return false;
    }
  }

  // Flow chuẩn
  if (newIndex === currentIndex + 1) return true;
  if (newIndex === currentIndex - 1) return true;

  // Completed → Processing (cho rollback)
  if (normalizedCurrent === "Completed" && targetStatus === "Processing")
    return true;

  return false;
};

const openReturnModal = async (returnId) => {
  try {
    showReturnModal.value = true;
    isReturnLoading.value = true;

    const res = await AdminService.getReturnById(returnId);

    // Chuẩn hóa dữ liệu items: nếu backend chưa có damagedQuantity thì set 0
    selectedReturn.value = {
      id: res.id,
      status: res.status,
      items: (res.items || []).map((it) => ({
        ...it,
        damagedQuantity:
          typeof it.damagedQuantity === "number" ? it.damagedQuantity : 0,
      })),
    };
  } catch (err) {
    notificationService.apiError(err, "Không thể tải dữ liệu yêu cầu trả hàng");
    showReturnModal.value = false;
  } finally {
    isReturnLoading.value = false;
  }
};

const closeReturnModal = () => {
  if (isSubmitting.value) return;
  showReturnModal.value = false;
};

const validateDamaged = (item) => {
  if (item.damagedQuantity < 0 || isNaN(item.damagedQuantity)) {
    item.damagedQuantity = 0;
  }
  if (item.damagedQuantity > item.quantity) {
    item.damagedQuantity = item.quantity;
  }
};

const confirmReturnCompletion = async () => {
  if (!selectedReturn.value?.id) return;

  try {
    isSubmitting.value = true;

    const payload = {
      returnRequestId: selectedReturn.value.id,
      items: selectedReturn.value.items.map((it) => ({
        variantId: it.variantId,
        damagedQuantity: it.damagedQuantity || 0,
        goodQuantity: Math.max(it.quantity - (it.damagedQuantity || 0), 0),
      })),
    };

    await adminStore.confirmReturnConditions(payload);

    notificationService.success(
      "Xác nhận thành công",
      "Đã cập nhật tình trạng sản phẩm trả về!"
    );

    showReturnModal.value = false;
    await fetchOrderDetail();
  } catch (error) {
    notificationService.apiError(
      error,
      "Có lỗi khi xác nhận tình trạng sản phẩm"
    );
  } finally {
    isSubmitting.value = false;
  }
};

const confirmStatusChange = (order, targetStatus) => {
  try {
    if (!targetStatus) {
      return;
    }

    const currentNormalizedStatus = getNormalizedStatusValue(order.status);

    // ⭐ RULE: Không cho Processing → Packed nếu điểm sử dụng vượt quá điểm còn lại
    if (currentNormalizedStatus === "Pending" && targetStatus === "Confirmed") {
      const used = Number(order.pointsUsed || 0);
      const balance = Number(order.customerPointBalance || 0);

      if (used > balance) {
        notificationService.warning(
          "Không thể chuyển trạng thái",
          `Khách đã dùng ${used} điểm nhưng chỉ còn ${balance} điểm.\n` +
            "Không thể chuyển sang bước Đã đóng gói."
        );
        return; // ❌ STOP, không mở dialog confirm
      }
    }

    // Kiểm tra xem có thể chuyển đổi không
    if (!canChangeToStatus(currentNormalizedStatus, targetStatus)) {
      notificationService.warning(
        "Cảnh báo",
        "Không thể chuyển đổi trạng thái này. Vui lòng chuyển đổi theo thứ tự bước."
      );
      return;
    }

    // If no change, do nothing
    if (currentNormalizedStatus === targetStatus) {
      return;
    }

    // Normalize cả hai để so sánh đúng
    oldStatus.value = currentNormalizedStatus;
    newStatus.value = targetStatus;

    // Store order reference and original status
    orderToUpdate.value = { ...order }; // Clone để tránh mutation
    if (!orderToUpdate.value._originalStatus) {
      orderToUpdate.value._originalStatus = currentNormalizedStatus;
    }

    // Show confirmation dialog
    showStatusConfirm.value = true;
  } catch (error) {
    logger.error("❌ Error in confirmStatusChange:", error);
    notificationService.apiError(
      error,
      "Có lỗi xảy ra khi thay đổi trạng thái"
    );
  }
};

const handleStatusUpdate = async () => {
  const orderId = orderToUpdate.value.id;
  const previousStatus = oldStatus.value;

  try {
    updating.value = true;

    // Gọi API để cập nhật
    const updatedOrder = await adminStore.updateOrderStatus(
      orderId,
      newStatus.value
    );

    logger.log("✅ Cập nhật thành công:", updatedOrder);

    // Update order
    order.value = { ...order.value, status: newStatus.value };

    // Refresh order detail
    await fetchOrderDetail();

    notificationService.success(
      "Thành công",
      `Đã cập nhật trạng thái đơn hàng #${orderId} từ '${getStatusLabel(
        previousStatus
      )}' sang '${getStatusLabel(newStatus.value)}' thành công!`,
      { duration: 3000 }
    );

    showStatusConfirm.value = false;
  } catch (error) {
    logger.error("❌ Lỗi khi cập nhật trạng thái:", error);
    logger.error("Error details:", error.response || error.message);

    notificationService.apiError(
      error,
      "Không thể cập nhật trạng thái đơn hàng"
    );

    // Restore old status on error
    order.value = { ...order.value, status: previousStatus };
  } finally {
    updating.value = false;
  }
};

const handleCancelStatusChange = () => {
  // Restore the old status
  if (orderToUpdate.value) {
    orderToUpdate.value.status = oldStatus.value;
  }
  showStatusConfirm.value = false;
};

// Fetch order detail
const fetchOrderDetail = async () => {
  if (!orderId.value) {
    error.value = "Không tìm thấy ID đơn hàng";
    return;
  }

  try {
    loading.value = true;
    error.value = null;
    const orderDetail = await AdminService.getOrderById(orderId.value);
    order.value = orderDetail;
  } catch (err) {
    logger.error("Error fetching order detail:", err);
    error.value =
      err.message || "Không thể tải chi tiết đơn hàng. Vui lòng thử lại.";
    notificationService.apiError(err, "Không thể tải chi tiết đơn hàng");
  } finally {
    loading.value = false;
  }
};

// Format functions
// formatCurrency và formatDateTime đã được import từ @/utils/formatters

const getStatusLabel = (status) => {
  if (!status) return "N/A";
  const normalized = normalizeStatusForDisplay(status);
  const labels = {
    Pending: "Chờ xử lý",
    Processing: "Đang xử lý",
    Shipped: "Đã gửi hàng",
    Completed: "Hoàn thành",
    Cancelled: "Đã hủy",
    Confirmed: "Đã xác nhận",
    Packed: "Đã đóng gói",
    Refunded: "Đã hoàn tiền",
    Failed: "Giao hàng thất bại",
    Rejected: "Đã từ chối",
    Approved: "Đã duyệt",
    Return_Pending: "Chờ xử lý trả hàng",
    Return_Approved: "Yêu cầu đổi trả đã được duyệt",
    Return_Rejected: "Yêu cầu đổi trả đã bị từ chối",
    Return_Completed: "Đã hoàn tất đổi trả",
  };
  return labels[normalized] || normalized || status;
};

const getReturnMethodText = (method) => {
  if (!method) return "Hoàn tiền";
  return (
    {
      refund: "Hoàn tiền",
    }[method.toLowerCase()] || method
  );
};

const getStatusBadgeClass = (status) => {
  const normalized = normalizeStatusForDisplay(status);
  const classMap = {
    Pending:
      "bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400",
    Processing:
      "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400",
    Confirmed:
      "bg-indigo-100 text-indigo-800 dark:bg-indigo-900/30 dark:text-indigo-400",
    Packed:
      "bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400",
    Shipped: "bg-cyan-100 text-cyan-800 dark:bg-cyan-900/30 dark:text-cyan-400",
    Completed:
      "bg-emerald-100 text-emerald-800 dark:bg-emerald-900/30 dark:text-emerald-400",
    Cancelled:
      "bg-rose-100 text-rose-800 dark:bg-rose-900/30 dark:text-rose-400",
    Refunded:
      "bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400",
    Rejected: "bg-red-200 text-red-800 dark:bg-red-900/40 dark:text-red-400",
    Approved:
      "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400",
    Failed: "bg-rose-200 text-rose-800 dark:bg-rose-900/40 dark:text-rose-400",
    Return_Pending:
      "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400",
    Return_Approved:
      "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400",
    Return_Rejected:
      "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400",
    Return_Completed:
      "bg-teal-100 text-teal-800 dark:bg-teal-900/30 dark:text-teal-400",
  };
  return (
    classMap[normalized] ||
    "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300"
  );
};

const getReturnReason = (reason) => {
  if (!reason) return "Không xác định";

  const code = reason.split("\n")[0].trim().toLowerCase();

  const map = {
    defective: "Hàng lỗi",
    not_as_described: "Không đúng mô tả",
    wrong_item: "Giao sai sản phẩm",
    wrong_size: "Sai kích cỡ",
    size_issue: "Không vừa size",
    change_of_mind: "Đổi ý",
    damaged: "Hư hỏng khi vận chuyển",
    other: "Lý do khác",
  };

  return map[code] || "Lý do khác";
};

const getReturnCustomerNote = (reason) => {
  if (!reason) return null;
  const parts = reason.split("\n\n");
  if (parts.length > 1) {
    return parts[1].replace(/^Ghi chú:\s*/i, "").trim();
  }
  return null;
};

const getPaymentMethodLabel = (method) => {
  if (!method) return "N/A";
  const labels = {
    cod: "Thanh toán khi nhận hàng (COD)",
    vnpay: "VNPay",
    momo: "MoMo",
    zalopay: "ZaloPay",
    bank_transfer: "Chuyển khoản ngân hàng",
    credit_card: "Thẻ tín dụng",
    cash: "Tiền mặt",
    card: "Thẻ",
  };
  return labels[method.toLowerCase()] || method;
};

const getPaymentStatusLabel = (status) => {
  if (!status) return "N/A";
  const labels = {
    pending: "Chờ thanh toán",
    processing: "Đang xử lý",
    completed: "Đã thanh toán",
    failed: "Thanh toán thất bại",
    refunded: "Đã hoàn tiền",
  };
  return labels[status.toLowerCase()] || status;
};

const sortedStatusHistories = computed(() => {
  if (!order.value?.statusHistories) return [];

  return [...order.value.statusHistories].sort((a, b) => {
    const dateA = new Date(a.changedAt || a.createdAt);
    const dateB = new Date(b.changedAt || b.createdAt);
    return dateB - dateA;
  });
});

const getPaymentStatusClass = (status) => {
  if (!status)
    return "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300";
  const classMap = {
    pending:
      "bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400",
    processing:
      "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400",
    completed:
      "bg-emerald-100 text-emerald-800 dark:bg-emerald-900/30 dark:text-emerald-400",
    failed: "bg-rose-100 text-rose-800 dark:bg-rose-900/30 dark:text-rose-400",
    refunded:
      "bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400",
  };
  return (
    classMap[status.toLowerCase()] ||
    "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300"
  );
};

const returnStepIndex = computed(() => {
  if (!order.value?.returnRequest) return -1;
  const st = order.value.returnRequest.status.toLowerCase();

  if (st === "pending") return 0;
  if (st === "approved") return 1;
  if (st === "rejected") return 1; // same step index but with reject styles
  if (st === "completed") return 2;

  return 0;
});

const isReturnRejected = computed(() => {
  return order.value?.returnRequest?.status?.toLowerCase() === "rejected";
});

const isInsufficientPoints = computed(() => {
  if (!order.value) return false;
  const used = Number(order.value.pointsUsed || 0);
  const balance = Number(order.value.customerPointBalance || 0);

  return used > balance;
});

const canMarkDeliveryFailed = (status) => {
  const normalized = getNormalizedStatusValue(status);
  return normalized === "Shipped";
};

const getColorHex = (colorName) => {
  // Map một số màu phổ biến
  const colorMap = {
    Black: "#000000",
    White: "#FFFFFF",
    Gray: "#808080",
    Blue: "#0000FF",
    Green: "#00FF00",
    Red: "#FF0000",
    Yellow: "#FFFF00",
    Pink: "#FFC0CB",
    Brown: "#A52A2A",
  };
  return colorMap[colorName] || "#CCCCCC";
};

const handlePrintInvoice = (order) => {
  if (!order) {
    notificationService.warning(
      "Cảnh báo",
      "Không có thông tin đơn hàng để in"
    );
    return;
  }

  try {
    printInvoice(order);
    notificationService.success("Thành công", "Đang mở cửa sổ in hóa đơn...");
  } catch (error) {
    logger.error("Error printing invoice:", error);
    notificationService.apiError(error, "Không thể in hóa đơn");
  }
};

const isFailed = computed(() => {
  if (!order.value?.status) return false;
  return getNormalizedStatusValue(order.value.status) === "Failed";
});

const currentStepIndex = computed(() => {
  if (!order.value?.status) return -1;

  const normalized = getNormalizedStatusValue(order.value.status);

  if (isPOSOrder.value) {
    return POS_STATUS_STEPS.indexOf(normalized);
  }

  if (normalized === "Failed") {
    return ORDER_STATUS_STEPS.length - 1;
  }

  return getStatusStepIndex(order.value.status);
});

const exportOrderToPDF = (order) => {
  if (!order) {
    notificationService.warning(
      "Cảnh báo",
      "Không có thông tin đơn hàng để export"
    );
    return;
  }

  try {
    handlePrintInvoice(order);
    notificationService.success("Thành công", "Đang mở cửa sổ in hóa đơn...");
  } catch (error) {
    logger.error("Error exporting to PDF:", error);
    notificationService.apiError(error, "Không thể export PDF");
  }
};

onMounted(() => {
  fetchOrderDetail();
});
</script>

<style scoped>
/* Additional styles if needed */
</style>

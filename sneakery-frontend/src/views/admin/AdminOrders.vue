<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
      >
        <div>
          <h1
            class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
          >
            <i class="material-icons text-purple-600 dark:text-purple-400"
              >shopping_bag</i
            >
            Quản lý đơn hàng
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
            Theo dõi và cập nhật trạng thái đơn hàng
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="exportToExcel"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">download</i>
            Export Excel
          </button>
          <button
            @click="exportToPDF"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">picture_as_pdf</i>
            Export PDF
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">shopping_bag</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.totalOrders }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng đơn hàng</p>
        </div>
      </div>
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">schedule</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.pendingOrders }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Chờ xử lý</p>
        </div>
      </div>
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">refresh</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.processingOrders }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đang xử lý</p>
        </div>
      </div>
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.completedOrders }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Hoàn thành</p>
        </div>
      </div>
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-emerald-500 to-emerald-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">attach_money</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ formatCurrency(stats.todayRevenue) }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">
            Doanh thu hôm nay
          </p>
        </div>
      </div>
    </div>

    <!-- Search & Filters -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <div class="relative">
            <i
              class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"
              >search</i
            >
            <input
              v-model="filters.search"
              @input="debounceSearch"
              type="text"
              placeholder="Tìm theo mã đơn, tên hoặc email khách hàng..."
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
            <button
              v-if="filters.search"
              @click="clearSearch"
              class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
              title="Xóa tìm kiếm"
            >
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >Trạng thái</label
            >
            <select
              v-model="filters.status"
              @change="applyFilters"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            >
              <option value="">Tất cả</option>
              <option value="Pending">Chờ xử lý</option>
              <option value="Confirmed">Đã xác nhận</option>
              <option value="Processing">Đang xử lý</option>
              <option value="Packed">Đã đóng gói</option>
              <option value="Shipped">Đã gửi hàng</option>
              <option value="Completed">Hoàn thành</option>
              <option value="Cancelled">Đã hủy</option>
              <option value="Cancelled">Đã hủy</option>
              <option value="Refunded">Trả hàng/Hoàn tiền</option>
              <option value="Return_Pending">Yêu cầu trả hàng</option>
              <option value="Return_Approved">Đã duyệt trả hàng</option>
              <option value="Return_Rejected">Từ chối trả hàng</option>
              <option value="Return_Completed">Hoàn tất trả hàng</option>
            </select>
          </div>

          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >Từ ngày</label
            >
            <input
              v-model="filters.startDate"
              @change="applyFilters"
              type="date"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>

          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >Đến ngày</label
            >
            <input
              v-model="filters.endDate"
              @change="applyFilters"
              type="date"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>

          <button
            @click="resetFilters"
            class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium mt-6"
            title="Xóa tất cả bộ lọc"
          >
            <i class="material-icons text-base">refresh</i>
            Reset
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div
      v-if="loading"
      class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div class="space-y-4" role="status" aria-live="polite">
        <LoadingSkeleton v-for="n in 5" :key="n" type="list" />
        <span class="sr-only">Đang tải danh sách đơn hàng</span>
      </div>
    </div>

    <!-- Empty State -->
    <div
      v-else-if="orders.length === 0"
      class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4"
      >
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl"
          >shopping_bag</i
        >
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
        Chưa có đơn hàng nào
      </h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
        Chưa có đơn hàng nào được tạo trong hệ thống
      </p>
    </div>

    <!-- Bulk Action Bar for Orders -->
    <div
      v-show="selectedOrders.length > 0"
      class="flex items-center justify-between p-4 bg-purple-50 dark:bg-purple-900/20 rounded-xl border border-purple-200 dark:border-purple-800 mb-4"
    >
      <div
        class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300"
      >
        <i class="material-icons text-purple-600 dark:text-purple-400"
          >check_circle</i
        >
        <span
          >Đã chọn
          <strong class="font-semibold">{{ selectedOrders.length }}</strong> đơn
          hàng</span
        >
      </div>
      <div class="flex items-center gap-2">
        <select
          v-model="bulkStatus"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        >
          <option value="">-- Chọn trạng thái mới --</option>
          <option value="Pending">Chờ xử lý</option>
          <option value="Processing">Đang xử lý</option>
          <option value="Shipped">Đã gửi hàng</option>
          <option value="Completed">Hoàn thành</option>
          <option value="Cancelled">Đã hủy</option>
        </select>
        <button
          @click="bulkUpdateStatus"
          :disabled="!bulkStatus"
          class="flex items-center gap-2 px-3 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <i class="material-icons text-base">update</i>
          Cập nhật trạng thái
        </button>
        <button
          @click="clearOrderSelection"
          class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
        >
          <i class="material-icons text-base">clear</i>
          Bỏ chọn
        </button>
      </div>
    </div>

    <!-- Orders List -->
    <div
      class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden"
    >
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead
            class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600"
          >
            <tr>
              <!-- <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-10"
              >
                <input
                  type="checkbox"
                  :checked="isAllOrdersSelected"
                  @change="toggleSelectAllOrders"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </th> -->
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Mã đơn
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Khách hàng
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Tổng tiền
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Trạng thái
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Ngày đặt
              </th>
              <th
                class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody
            class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700"
          >
            <tr
              v-for="order in orders"
              :key="order.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors"
            >
              <!-- <td class="px-4 py-4">
                <input
                  type="checkbox"
                  :checked="selectedOrders.includes(order.id)"
                  @change="toggleSelectOrder(order.id)"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </td> -->
              <td class="px-4 py-4">
                <code
                  class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100"
                  >#{{ order.id }}</code
                >
              </td>
              <td class="px-4 py-4">
                <div
                  class="text-sm font-medium text-gray-900 dark:text-gray-100"
                >
                  {{ order.customerName }}
                </div>
                <div class="text-xs text-gray-500 dark:text-gray-400">
                  {{ order.customerEmail }}
                </div>
              </td>
              <td
                class="px-4 py-4 text-sm font-semibold text-gray-900 dark:text-gray-100"
              >
                {{ formatCurrency(order.totalAmount) }}
              </td>
              <td class="px-4 py-4">
                <div class="flex items-center gap-2">
                  <span
                    class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                    :class="
                      getStatusBadgeClass(
                        getNormalizedStatusValue(order.status)
                      )
                    "
                  >
                    {{ getStatusLabel(order.status) }}
                  </span>
                  <button
                    v-if="getNextStep(order.status)"
                    @click="
                      confirmStatusChange(order, getNextStep(order.status))
                    "
                    class="p-1 text-purple-600 dark:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20 rounded transition-colors"
                    :title="`Chuyển sang: ${getStatusLabel(
                      getNextStep(order.status)
                    )}`"
                  >
                    <i class="material-icons text-sm">arrow_forward</i>
                  </button>
                  <!-- <button
                    v-if="getPreviousStep(order.status)"
                    @click="confirmStatusChange(order, getPreviousStep(order.status))"
                    class="p-1 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700 rounded transition-colors"
                    :title="`Quay lại: ${getStatusLabel(getPreviousStep(order.status))}`"
                  >
                    <i class="material-icons text-sm">arrow_back</i>
                  </button> -->
                </div>
              </td>
              <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">
                {{ formatDate(order.createdAt) }}
              </td>
              <td class="px-4 py-4 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button
                    @click="viewOrderDetail(order)"
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors"
                    title="Xem chi tiết"
                    aria-label="Xem chi tiết đơn hàng"
                  >
                    <i class="material-icons text-base">visibility</i>
                  </button>
                  <button
                    @click="handlePrintInvoice(order)"
                    class="p-1.5 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded-lg transition-colors"
                    title="In hóa đơn"
                    aria-label="In hóa đơn đơn hàng"
                  >
                    <i class="material-icons text-base">print</i>
                  </button>
                  <button
                    v-if="canCancelOrder(order.status)"
                    @click="handleCancelOrder(order)"
                    class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
                    title="Hủy đơn hàng"
                    aria-label="Hủy đơn hàng"
                  >
                    <i class="material-icons text-base">cancel</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination -->
    <div
      v-if="!loading && orders.length > 0"
      class="flex items-center justify-between gap-4 px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div class="text-sm text-gray-600 dark:text-gray-400">
        Hiển thị {{ currentPage * pageSize + 1 }} -
        {{ Math.min((currentPage + 1) * pageSize, totalItems) }} trong tổng số
        {{ totalItems }} đơn hàng
      </div>
      <div class="flex items-center gap-2">
        <button
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="currentPage === 0"
          @click="changePage(currentPage - 1)"
        >
          <i class="material-icons text-base">chevron_left</i>
          Trước
        </button>
        <span class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300">
          Trang {{ currentPage + 1 }} / {{ totalPages }}
        </span>
        <button
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="currentPage >= totalPages - 1"
          @click="changePage(currentPage + 1)"
        >
          Sau
          <i class="material-icons text-base">chevron_right</i>
        </button>
      </div>
    </div>

    <!-- Order Detail Modal -->
    <div
      v-if="showDetailModal"
      class="modal-overlay"
      @click="showDetailModal = false"
    >
      <div class="modal modal-large" @click.stop>
        <div class="modal-header">
          <div>
            <h2 class="modal-title">
              <i class="material-icons">receipt_long</i>
              Chi tiết đơn hàng #{{ selectedOrder?.id }}
            </h2>
            <div
              class="order-status-badge"
              :class="`status-${selectedOrder?.status?.toLowerCase()}`"
            >
              {{ getStatusLabel(selectedOrder?.status) }}
            </div>
          </div>
          <button @click="showDetailModal = false" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>

        <div class="modal-body" v-if="selectedOrder">
          <!-- Customer Info -->
          <div class="detail-section">
            <h3 class="section-title">
              <i class="material-icons">person</i>
              Thông tin khách hàng
            </h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Tên khách hàng:</span>
                <span class="info-value">{{ selectedOrder.customerName }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Email:</span>
                <span class="info-value">{{
                  selectedOrder.customerEmail
                }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Ngày đặt:</span>
                <span class="info-value">{{
                  formatDateTime(selectedOrder.createdAt)
                }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Tổng tiền:</span>
                <span class="info-value total-amount">{{
                  formatCurrency(selectedOrder.totalAmount)
                }}</span>
              </div>
            </div>
          </div>

          <!-- Shipping Address -->
          <div class="detail-section" v-if="selectedOrder.addressShipping">
            <h3 class="section-title">
              <i class="material-icons">local_shipping</i>
              Địa chỉ giao hàng
            </h3>
            <div class="address-box">
              <p>
                <strong>{{
                  selectedOrder.addressShipping.recipientName
                }}</strong>
              </p>
              <p>{{ selectedOrder.addressShipping.phone }}</p>
              <p>{{ selectedOrder.addressShipping.line1 }}</p>
              <p v-if="selectedOrder.addressShipping.line2">
                {{ selectedOrder.addressShipping.line2 }}
              </p>
              <p>
                {{ selectedOrder.addressShipping.ward }},
                {{ selectedOrder.addressShipping.district }},
                {{ selectedOrder.addressShipping.city }}
              </p>
              <p v-if="selectedOrder.addressShipping.postalCode">
                Mã bưu điện: {{ selectedOrder.addressShipping.postalCode }}
              </p>
            </div>
          </div>

          <!-- Order Items -->
          <div class="detail-section">
            <h3 class="section-title">
              <i class="material-icons">shopping_cart</i>
              Sản phẩm đã đặt
            </h3>
            <div class="order-items-table">
              <table class="items-table">
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Size</th>
                    <th>Màu</th>
                    <th class="text-right">Số lượng</th>
                    <th class="text-right">Đơn giá</th>
                    <th class="text-right">Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(item, index) in selectedOrder.orderDetails || []"
                    :key="index"
                  >
                    <td>
                      <div class="product-info">
                        <img
                          v-if="item.imageUrl"
                          :src="item.imageUrl"
                          :alt="item.productName"
                          class="product-image"
                          loading="lazy"
                          decoding="async"
                        />
                        <div>
                          <div class="product-name">{{ item.productName }}</div>
                          <div class="product-brand">{{ item.brandName }}</div>
                        </div>
                      </div>
                    </td>
                    <td>{{ item.size }}</td>
                    <td>
                      <span
                        class="color-badge"
                        :style="{ backgroundColor: item.color }"
                      ></span>
                      {{ item.color }}
                    </td>
                    <td class="text-right">{{ item.quantity }}</td>
                    <td class="text-right">
                      {{ formatCurrency(item.unitPrice) }}
                    </td>
                    <td class="text-right fw-bold">
                      {{ formatCurrency(item.totalPrice) }}
                    </td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr class="total-row">
                    <td colspan="5" class="text-right">
                      <strong>Tổng cộng:</strong>
                    </td>
                    <td class="text-right total-price">
                      {{ formatCurrency(selectedOrder.totalAmount) }}
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>

          <!-- Payment Info -->
          <div class="detail-section" v-if="selectedOrder.payment">
            <h3 class="section-title">
              <i class="material-icons">payment</i>
              Thông tin thanh toán
            </h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Phương thức:</span>
                <span class="info-value">{{
                  getPaymentMethodLabel(selectedOrder.payment.paymentMethod)
                }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Trạng thái:</span>
                <span
                  class="info-value payment-status"
                  :class="`status-${selectedOrder.payment.status?.toLowerCase()}`"
                >
                  {{ selectedOrder.payment.status }}
                </span>
              </div>
              <div class="info-item" v-if="selectedOrder.payment.paidAt">
                <span class="info-label">Ngày thanh toán:</span>
                <span class="info-value">{{
                  formatDateTime(selectedOrder.payment.paidAt)
                }}</span>
              </div>
            </div>
          </div>

          <!-- Status History -->
          <div
            class="detail-section"
            v-if="
              selectedOrder.statusHistories &&
              selectedOrder.statusHistories.length > 0
            "
          >
            <h3 class="section-title">
              <i class="material-icons">history</i>
              Lịch sử thay đổi trạng thái
            </h3>
            <div class="status-timeline">
              <div
                v-for="(history, index) in selectedOrder.statusHistories"
                :key="history.id"
                class="timeline-item"
                :class="{ 'timeline-active': index === 0 }"
              >
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-status">
                    {{ getStatusLabel(history.status) }}
                  </div>
                  <div class="timeline-date">
                    {{ formatDateTime(history.changedAt) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button
            @click="handlePrintInvoice(selectedOrder)"
            class="btn btn-primary"
          >
            <i class="material-icons">print</i>
            In hóa đơn
          </button>
          <button
            @click="exportOrderToPDF(selectedOrder)"
            class="btn btn-secondary"
          >
            <i class="material-icons">picture_as_pdf</i>
            Export PDF
          </button>
          <button @click="showDetailModal = false" class="btn btn-secondary">
            Đóng
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

    <!-- Bulk Update Status Confirmation Dialog -->
    <ConfirmDialog
      v-model="showBulkUpdateConfirm"
      type="warning"
      title="Xác nhận cập nhật hàng loạt"
      :message="`Bạn có chắc chắn muốn cập nhật ${
        selectedOrders.length
      } đơn hàng sang trạng thái '${getStatusLabel(bulkStatus)}'?`"
      description="Hành động này sẽ cập nhật trạng thái cho tất cả đơn hàng đã chọn."
      confirm-text="Xác nhận"
      cancel-text="Hủy"
      :loading="loading"
      @confirm="bulkUpdateStatusConfirmed"
    />

    <!-- Cancel Order Confirmation Dialog -->
    <ConfirmDialog
      v-model="showCancelOrderConfirm"
      type="danger"
      title="Xác nhận hủy đơn hàng"
      :message="`Bạn có chắc chắn muốn hủy đơn hàng #${orderToCancel?.id}?`"
      description="Hành động này sẽ hủy đơn hàng và không thể hoàn tác."
      confirm-text="Hủy đơn hàng"
      cancel-text="Đóng"
      :loading="false"
      @confirm="handleCancelOrderConfirmed"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAdminStore } from "@/stores/admin";
import notificationService from "@/utils/notificationService";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";
import * as XLSX from "xlsx";
import { printInvoice } from "@/utils/pdfGenerator";
import { downloadCsv, prepareOrdersForExport } from "@/utils/exportHelpers";
import AdminService from "@/services/adminService";
import logger from "@/utils/logger";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import {
  formatPrice,
  formatCurrency,
  formatDate,
  formatDateTime,
} from "@/utils/formatters";
import { useActivityLogger } from "@/composables/useActivityLogger";

const router = useRouter();
const adminStore = useAdminStore();

// Activity logging
const { logOrderStatusChange, logOrderCancel, logBulkOrderOperation } =
  useActivityLogger();

const orders = ref([]);
const loading = ref(false);
const currentPage = ref(0);
const pageSize = ref(10);
const totalItems = ref(0);
const showDetailModal = ref(false);
const selectedOrder = ref(null);

// Bulk selection state
const selectedOrders = ref([]);
const bulkStatus = ref("");

// Search & Filter state
const filters = ref({
  search: "",
  status: "",
  startDate: "",
  endDate: "",
});
let searchTimeout = null;

// Stats state
const stats = ref({
  totalOrders: 0,
  pendingOrders: 0,
  processingOrders: 0,
  completedOrders: 0,
  todayRevenue: 0,
});

// Status change confirmation
const showStatusConfirm = ref(false);
const orderToUpdate = ref(null);
const oldStatus = ref("");
const newStatus = ref("");
const updating = ref(false);

// Bulk update confirmation
const showBulkUpdateConfirm = ref(false);

// Cancel order confirmation
const showCancelOrderConfirm = ref(false);
const orderToCancel = ref(null);

const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));

const isAllOrdersSelected = computed(() => {
  return (
    orders.value.length > 0 &&
    selectedOrders.value.length === orders.value.length
  );
});

// Bulk selection methods
const toggleSelectOrder = (orderId) => {
  const index = selectedOrders.value.indexOf(orderId);
  if (index > -1) {
    selectedOrders.value.splice(index, 1);
  } else {
    selectedOrders.value.push(orderId);
  }
};

const toggleSelectAllOrders = () => {
  if (isAllOrdersSelected.value) {
    selectedOrders.value = [];
  } else {
    selectedOrders.value = orders.value.map((o) => o.id);
  }
};

const clearOrderSelection = () => {
  selectedOrders.value = [];
  bulkStatus.value = "";
};

const canCancelOrder = (status) => {
  const normalized = getNormalizedStatusValue(status);
  const cancellableStatuses = ["Pending", "Confirmed", "Processing"];
  return cancellableStatuses.includes(normalized);
};

const bulkUpdateStatus = () => {
  if (!bulkStatus.value) {
    notificationService.warning("Cảnh báo", "Vui lòng chọn trạng thái!");
    return;
  }
  if (selectedOrders.value.length === 0) {
    notificationService.warning(
      "Cảnh báo",
      "Vui lòng chọn ít nhất một đơn hàng!"
    );
    return;
  }
  showBulkUpdateConfirm.value = true;
};

const bulkUpdateStatusConfirmed = async () => {
  showBulkUpdateConfirm.value = false;
  try {
    loading.value = true;

    // Update status for each order
    for (const orderId of selectedOrders.value) {
      await adminStore.updateOrderStatus(orderId, bulkStatus.value);
    }

    notificationService.success(
      "Thành công",
      `Đã cập nhật ${selectedOrders.value.length} đơn hàng thành công!`,
      { duration: 3000 }
    );

    // Log activity
    try {
      await logBulkOrderOperation(
        "UPDATE",
        selectedOrders.value.length,
        selectedOrders.value,
        bulkStatus.value
      );
    } catch (err) {
      console.warn("Failed to log bulk order update activity:", err);
    }

    // Clear selection and refresh list
    selectedOrders.value = [];
    bulkStatus.value = "";
    await fetchOrders();
  } catch (error) {
    logger.error("Lỗi khi cập nhật hàng loạt:", error);
    notificationService.apiError(error, "Có lỗi xảy ra khi cập nhật đơn hàng");
  } finally {
    loading.value = false;
  }
};

const fetchOrders = async () => {
  try {
    loading.value = true;

    // 1) Load trang hiện tại
    const apiFilters = {
      search: filters.value.search || undefined,
      status: filters.value.status || undefined,
      startDate: filters.value.startDate || undefined,
      endDate: filters.value.endDate || undefined,
    };

    const result = await adminStore.fetchOrders(
      currentPage.value,
      pageSize.value,
      apiFilters
    );

    const rawOrders = result.content || [];

    // Convert orders bình thường
    let pageOrders = rawOrders.map((order) => ({
      ...order,
      status: normalizeStatusForDisplay(order.status),
      _originalStatus: normalizeStatusForDisplay(order.status),
    }));

    // ⭐⭐ Chỉ ưu tiên Return_Pending trên TRANG ĐẦU TIÊN ⭐⭐
    if (currentPage.value === 0) {
      // 2) Lấy toàn bộ Return_Pending
      const specialOrders = await fetchReturnPendingOrders();

      // 3) Ghép Return_Pending lên đầu (tránh trùng lặp)
      const specialIds = specialOrders.map((o) => o.id);

      // Lọc đơn bình thường (không phải Return_Pending)
      pageOrders = pageOrders.filter((o) => !specialIds.includes(o.id));

      // Ghép vào đầu
      pageOrders = [...specialOrders, ...pageOrders];
    }

    // Set kết quả cuối
    orders.value = pageOrders;

    // Update tổng phần tử
    totalItems.value = result.totalElements || 0;

    calculateStats();
  } catch (error) {
    logger.error("Lỗi khi tải danh sách đơn hàng:", error);
    notificationService.apiError(error, "Không thể tải danh sách đơn hàng");
  } finally {
    loading.value = false;
  }
};

const fetchReturnPendingOrders = async () => {
  try {
    const result = await adminStore.fetchOrders(
      0,
      1000, // lấy tối đa trong 1 lần
      { status: "Return_Pending" }
    );

    const rawOrders = result.content || [];

    return rawOrders.map((order) => ({
      ...order,
      status: normalizeStatusForDisplay(order.status),
      _originalStatus: normalizeStatusForDisplay(order.status),
    }));
  } catch (error) {
    console.error("Error loading Return_Pending orders", error);
    return [];
  }
};

const calculateStats = () => {
  const today = new Date().toISOString().split("T")[0];
  stats.value = {
    totalOrders: totalItems.value,
    pendingOrders: orders.value.filter((o) => o.status === "Pending").length,
    processingOrders: orders.value.filter((o) => o.status === "Processing")
      .length,
    completedOrders: orders.value.filter((o) => o.status === "Completed")
      .length,
    todayRevenue: orders.value
      .filter(
        (o) =>
          o.createdAt &&
          new Date(o.createdAt).toISOString().split("T")[0] === today
      )
      .reduce((sum, o) => sum + (o.totalAmount || 0), 0),
  };
};

const debounceSearch = () => {
  if (searchTimeout) clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => {
    currentPage.value = 0; // Reset về trang đầu khi search
    fetchOrders();
  }, 500); // Debounce 500ms
};

const clearSearch = () => {
  filters.value.search = "";
  currentPage.value = 0;
  fetchOrders();
};

const applyFilters = () => {
  currentPage.value = 0; // Reset về trang đầu khi filter
  fetchOrders();
};

const resetFilters = () => {
  filters.value.search = "";
  filters.value.status = "";
  filters.value.startDate = "";
  filters.value.endDate = "";
  currentPage.value = 0;
  fetchOrders();
};

// Export to Excel
const exportToExcel = () => {
  try {
    // Chuẩn bị data để export
    const exportData = orders.value.map((order, index) => ({
      STT: index + 1,
      "Mã đơn hàng": `#${order.id}`,
      "Khách hàng": order.customerName || "N/A",
      Email: order.customerEmail || "N/A",
      "Tổng tiền": new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(order.totalAmount),
      "Trạng thái": getStatusLabel(order.status),
      "Ngày đặt": new Date(order.createdAt).toLocaleString("vi-VN"),
    }));

    // Tạo worksheet từ data
    const worksheet = XLSX.utils.json_to_sheet(exportData);

    // Tạo workbook
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Đơn hàng");

    // Tạo tên file với timestamp
    const timestamp = new Date().toISOString().slice(0, 10);
    const filename = `don-hang_${timestamp}.xlsx`;

    // Download file
    XLSX.writeFile(workbook, filename);

    notificationService.success(
      "Thành công",
      `Đã export ${exportData.length} đơn hàng thành công!`,
      { duration: 3000 }
    );
  } catch (error) {
    logger.error("Lỗi khi export Excel:", error);
    notificationService.apiError(error, "Không thể export dữ liệu");
  }
};

// Định nghĩa flow steps theo thứ tự
const ORDER_STATUS_STEPS = [
  "Pending", // 0: Chờ xử lý
  "Confirmed", // 1: Đã xác nhận
  "Processing", // 2: Đang xử lý
  "Packed", // 3: Đã đóng gói
  "Shipped", // 4: Đã gửi hàng
  "Completed", // 5: Hoàn thành
];

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

  const normalized = getNormalizedStatusValue(currentStatus);
  if (normalized === "Completed") {
    return "Processing";
  }

  return ORDER_STATUS_STEPS[currentIndex - 1];
};

// Kiểm tra xem có thể chuyển đến status mới không
const canChangeToStatus = (currentStatus, newStatus) => {
  const normalizedCurrent = getNormalizedStatusValue(currentStatus);

  // ❌ Không cho chuyển từ trạng thái Return_Pending, Return_Approved, Return_Rejected, Return_Completed
  if (
    [
      "Return_Pending",
      "Return_Approved",
      "Return_Rejected",
      "Return_Completed",
    ].includes(normalizedCurrent)
  ) {
    return false;
  }

  const currentIndex = getStatusStepIndex(currentStatus);
  const newIndex = getStatusStepIndex(newStatus);

  if (currentIndex === -1 || newIndex === -1) {
    return false; // Status không hợp lệ
  }

  // Cho phép chuyển đến step tiếp theo
  if (newIndex === currentIndex + 1) {
    return true;
  }

  // Cho phép quay lại step trước đó
  // if (newIndex === currentIndex - 1) {
  //   return true;
  // }

  // if (
  //   getNormalizedStatusValue(currentStatus) === "Completed" &&
  //   newStatus === "Processing"
  // ) {
  //   return true;
  // }

  return false;
};

const confirmStatusChange = (order, targetStatus) => {
  try {
    if (!targetStatus) {
      return;
    }

    const currentNormalizedStatus = getNormalizedStatusValue(order.status);

    // Kiểm tra xem có thể chuyển đổi không
    if (!canChangeToStatus(currentNormalizedStatus, targetStatus)) {
      toastService.warning(
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

    // Update order trong danh sách orders.value
    const orderIndex = orders.value.findIndex((o) => o.id === orderId);
    if (orderIndex !== -1) {
      orders.value[orderIndex].status = newStatus.value;
      orders.value[orderIndex]._originalStatus = newStatus.value;
    }

    // Update orderToUpdate để đảm bảo đồng bộ
    orderToUpdate.value.status = newStatus.value;
    orderToUpdate.value._originalStatus = newStatus.value;

    // Refresh danh sách đơn hàng để đảm bảo dữ liệu đồng bộ với backend
    await fetchOrders();

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
    const orderIndex = orders.value.findIndex((o) => o.id === orderId);
    if (orderIndex !== -1) {
      orders.value[orderIndex].status = previousStatus;
    }
    orderToUpdate.value.status = previousStatus;
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

// Map status từ backend format (lowercase) sang frontend format (PascalCase) để hiển thị
const normalizeStatusForDisplay = (status) => {
  if (!status) return status;

  const statusMap = {
    pending: "Pending",
    processing: "Processing",
    shipped: "Shipped",
    delivered: "Completed", // Backend dùng "delivered" nhưng frontend hiển thị "Completed"
    cancelled: "Cancelled",
    confirmed: "Confirmed",
    packed: "Packed",
    refunded: "Refunded",
    failed: "Failed",
    return_pending: "Return_Pending",
    return_approved: "Return_Approved",
    return_rejected: "Return_Rejected",
    return_completed: "Return_Completed",
  };

  return statusMap[status.toLowerCase()] || status;
};

// Get normalized status value cho select dropdown - đảm bảo luôn có giá trị hợp lệ
const getNormalizedStatusValue = (status) => {
  const normalized = normalizeStatusForDisplay(status);
  // Đảm bảo giá trị match với một trong các option values
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

// Get CSS classes cho status badge
const getStatusBadgeClass = (status) => {
  const statusClassMap = {
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
    Failed: "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400",
    Return_Pending:
      "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400",
    Return_Approved:
      "bg-lime-100 dark:bg-lime-900/30 text-lime-700 dark:text-lime-400",
    Return_Rejected:
      "bg-pink-100 dark:bg-pink-900/30 text-pink-700 dark:text-pink-400",
    Return_Completed:
      "bg-teal-100 dark:bg-teal-900/30 text-teal-700 dark:text-teal-400",
  };

  return (
    statusClassMap[status] ||
    "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300"
  );
};

const getStatusLabel = (status) => {
  // Normalize status trước khi map label
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
    Return_Pending: "Yêu cầu trả hàng",
    Return_Approved: "Đã duyệt trả hàng",
    Return_Rejected: "Từ chối trả hàng",
    Return_Completed: "Hoàn tất trả hàng",
  };
  return labels[normalized] || normalized || status;
};

const viewOrderDetail = (order) => {
  // Navigate to order detail page
  router.push(`/admin/orders/${order.id}`);
};

const handleCancelOrder = (order) => {
  orderToCancel.value = order;
  showCancelOrderConfirm.value = true;
};

const handleCancelOrderConfirmed = async () => {
  if (!orderToCancel.value) return;
  const order = orderToCancel.value;
  showCancelOrderConfirm.value = false;

  try {
    await adminStore.updateOrderStatus(order.id, "Cancelled");
    notificationService.success("Thành công", "Đã hủy đơn hàng thành công!");

    // Log activity
    try {
      await logOrderCancel(order.id, order);
    } catch (err) {
      console.warn("Failed to log order cancel activity:", err);
    }

    await fetchOrders();
    orderToCancel.value = null;
  } catch (error) {
    logger.error("Lỗi khi hủy đơn hàng:", error);
    notificationService.apiError(error, "Không thể hủy đơn hàng");
  }
};

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

const exportToPDF = () => {
  notificationService.info(
    "Thông tin",
    "Tính năng export PDF đang được phát triển..."
  );
};

const handlePrintInvoice = async (order) => {
  if (!order) {
    notificationService.warning(
      "Cảnh báo",
      "Không có thông tin đơn hàng để in"
    );
    return;
  }

  try {
    const fullOrder = await AdminService.getOrderById(order.id); // ⬅ lấy full order
    printInvoice(fullOrder); // ⬅ in bản đầy đủ
    notificationService.success("Thành công", "Đang mở cửa sổ in hóa đơn...");
  } catch (error) {
    logger.error("Lỗi khi in hóa đơn:", error);
    notificationService.apiError(error, "Không thể in hóa đơn");
  }
};

const changePage = (page) => {
  currentPage.value = page;
  fetchOrders();
};

// formatCurrency, formatDate, formatDateTime đã được import từ @/utils/formatters

const getPaymentMethodLabel = (method) => {
  const labels = {
    COD: "Thanh toán khi nhận hàng",
    BANK_TRANSFER: "Chuyển khoản ngân hàng",
    CREDIT_CARD: "Thẻ tín dụng",
    EWALLET: "Ví điện tử",
  };
  return labels[method] || method;
};

onMounted(() => {
  fetchOrders();
});
</script>

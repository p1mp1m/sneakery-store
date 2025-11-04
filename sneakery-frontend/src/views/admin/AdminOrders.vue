<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">shopping_bag</i>
            Quản lý đơn hàng
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Theo dõi và cập nhật trạng thái đơn hàng</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="exportToExcel" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">download</i>
            Export Excel
          </button>
          <button @click="exportToPDF" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">picture_as_pdf</i>
            Export PDF
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">shopping_bag</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.totalOrders }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng đơn hàng</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">schedule</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.pendingOrders }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Chờ xử lý</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">refresh</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.processingOrders }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đang xử lý</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.completedOrders }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Hoàn thành</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-emerald-500 to-emerald-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">attach_money</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatCurrency(stats.todayRevenue) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Doanh thu hôm nay</p>
        </div>
      </div>
    </div>

    <!-- Search & Filters -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
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
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Trạng thái</label>
            <select v-model="filters.status" @change="applyFilters" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="">Tất cả</option>
              <option value="Pending">Chờ xử lý</option>
              <option value="Processing">Đang xử lý</option>
              <option value="Shipped">Đã gửi hàng</option>
              <option value="Completed">Hoàn thành</option>
              <option value="Cancelled">Đã hủy</option>
            </select>
          </div>

          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Từ ngày</label>
            <input 
              v-model="filters.startDate" 
              @change="applyFilters" 
              type="date" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>

          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Đến ngày</label>
            <input 
              v-model="filters.endDate" 
              @change="applyFilters" 
              type="date" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>

          <button @click="resetFilters" class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium mt-6" title="Xóa tất cả bộ lọc">
            <i class="material-icons text-base">refresh</i>
            Reset
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
      <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải danh sách đơn hàng...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="orders.length === 0" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">shopping_bag</i>
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có đơn hàng nào</h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
        Chưa có đơn hàng nào được tạo trong hệ thống
      </p>
    </div>

    <!-- Bulk Action Bar for Orders -->
    <div v-if="selectedOrders.length > 0" class="flex items-center justify-between p-4 bg-purple-50 dark:bg-purple-900/20 rounded-xl border border-purple-200 dark:border-purple-800 mb-4">
      <div class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
        <i class="material-icons text-purple-600 dark:text-purple-400">check_circle</i>
        <span>Đã chọn <strong class="font-semibold">{{ selectedOrders.length }}</strong> đơn hàng</span>
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
    <div v-else class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-10">
                <input 
                  type="checkbox" 
                  :checked="isAllOrdersSelected"
                  @change="toggleSelectAllOrders"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Mã đơn</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Khách hàng</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Tổng tiền</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Ngày đặt</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td class="px-4 py-4">
                <input 
                  type="checkbox" 
                  :checked="selectedOrders.includes(order.id)"
                  @change="toggleSelectOrder(order.id)"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </td>
              <td class="px-4 py-4">
                <code class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100">#{{ order.id }}</code>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ order.customerName }}</div>
                <div class="text-xs text-gray-500 dark:text-gray-400">{{ order.customerEmail }}</div>
              </td>
              <td class="px-4 py-4 text-sm font-semibold text-gray-900 dark:text-gray-100">{{ formatCurrency(order.totalAmount) }}</td>
              <td class="px-4 py-4">
                <select 
                  v-model="order.status" 
                  @change="confirmStatusChange(order, $event)"
                  class="px-2 py-1 text-xs font-medium rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  :class="{
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400 border-yellow-300 dark:border-yellow-700': order.status === 'Pending',
                    'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400 border-blue-300 dark:border-blue-700': order.status === 'Processing',
                    'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400 border-purple-300 dark:border-purple-700': order.status === 'Shipped',
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400 border-green-300 dark:border-green-700': order.status === 'Completed',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400 border-red-300 dark:border-red-700': order.status === 'Cancelled'
                  }"
                >
                  <option value="Pending">Chờ xử lý</option>
                  <option value="Processing">Đang xử lý</option>
                  <option value="Shipped">Đã gửi hàng</option>
                  <option value="Completed">Hoàn thành</option>
                  <option value="Cancelled">Đã hủy</option>
                </select>
              </td>
              <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">{{ formatDate(order.createdAt) }}</td>
              <td class="px-4 py-4 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button 
                    @click="viewOrderDetail(order)" 
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors" 
                    title="Xem chi tiết"
                  >
                    <i class="material-icons text-base">visibility</i>
                  </button>
                  <button 
                    @click="handlePrintInvoice(order)" 
                    class="p-1.5 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded-lg transition-colors" 
                    title="In hóa đơn"
                  >
                    <i class="material-icons text-base">print</i>
                  </button>
                  <button 
                    v-if="order.status !== 'Cancelled'" 
                    @click="handleCancelOrder(order)" 
                    class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors" 
                    title="Hủy đơn hàng"
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
    <div v-if="!loading && orders.length > 0" class="flex items-center justify-between gap-4 px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="text-sm text-gray-600 dark:text-gray-400">
        Hiển thị {{ currentPage * pageSize + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalItems) }} trong tổng số {{ totalItems }} đơn hàng
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
    <div v-if="showDetailModal" class="modal-overlay" @click="showDetailModal = false">
      <div class="modal modal-large" @click.stop>
        <div class="modal-header">
          <div>
            <h2 class="modal-title">
              <i class="material-icons">receipt_long</i>
              Chi tiết đơn hàng #{{ selectedOrder?.id }}
            </h2>
            <div class="order-status-badge" :class="`status-${selectedOrder?.status?.toLowerCase()}`">
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
                <span class="info-value">{{ selectedOrder.customerEmail }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Ngày đặt:</span>
                <span class="info-value">{{ formatDateTime(selectedOrder.createdAt) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Tổng tiền:</span>
                <span class="info-value total-amount">{{ formatCurrency(selectedOrder.totalAmount) }}</span>
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
              <p><strong>{{ selectedOrder.addressShipping.recipientName }}</strong></p>
              <p>{{ selectedOrder.addressShipping.phone }}</p>
              <p>{{ selectedOrder.addressShipping.line1 }}</p>
              <p v-if="selectedOrder.addressShipping.line2">{{ selectedOrder.addressShipping.line2 }}</p>
              <p>{{ selectedOrder.addressShipping.ward }}, {{ selectedOrder.addressShipping.district }}, {{ selectedOrder.addressShipping.city }}</p>
              <p v-if="selectedOrder.addressShipping.postalCode">Mã bưu điện: {{ selectedOrder.addressShipping.postalCode }}</p>
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
                  <tr v-for="(item, index) in (selectedOrder.orderDetails || [])" :key="index">
                    <td>
                      <div class="product-info">
                        <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName" class="product-image" />
                        <div>
                          <div class="product-name">{{ item.productName }}</div>
                          <div class="product-brand">{{ item.brandName }}</div>
                        </div>
                      </div>
                    </td>
                    <td>{{ item.size }}</td>
                    <td>
                      <span class="color-badge" :style="{ backgroundColor: item.color }"></span>
                      {{ item.color }}
                    </td>
                    <td class="text-right">{{ item.quantity }}</td>
                    <td class="text-right">{{ formatCurrency(item.unitPrice) }}</td>
                    <td class="text-right fw-bold">{{ formatCurrency(item.totalPrice) }}</td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr class="total-row">
                    <td colspan="5" class="text-right"><strong>Tổng cộng:</strong></td>
                    <td class="text-right total-price">{{ formatCurrency(selectedOrder.totalAmount) }}</td>
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
                <span class="info-value">{{ getPaymentMethodLabel(selectedOrder.payment.paymentMethod) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Trạng thái:</span>
                <span class="info-value payment-status" :class="`status-${selectedOrder.payment.status?.toLowerCase()}`">
                  {{ selectedOrder.payment.status }}
                </span>
              </div>
              <div class="info-item" v-if="selectedOrder.payment.paidAt">
                <span class="info-label">Ngày thanh toán:</span>
                <span class="info-value">{{ formatDateTime(selectedOrder.payment.paidAt) }}</span>
              </div>
            </div>
          </div>

          <!-- Status History -->
          <div class="detail-section" v-if="selectedOrder.statusHistories && selectedOrder.statusHistories.length > 0">
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
                  <div class="timeline-status">{{ getStatusLabel(history.status) }}</div>
                  <div class="timeline-date">{{ formatDateTime(history.changedAt) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="handlePrintInvoice(selectedOrder)" class="btn btn-primary">
            <i class="material-icons">print</i>
            In hóa đơn
          </button>
          <button @click="exportOrderToPDF(selectedOrder)" class="btn btn-secondary">
            <i class="material-icons">picture_as_pdf</i>
            Export PDF
          </button>
          <button @click="showDetailModal = false" class="btn btn-secondary">Đóng</button>
        </div>
      </div>
    </div>

    <!-- Status Change Confirmation Dialog -->
    <ConfirmDialog
      v-model="showStatusConfirm"
      type="warning"
      title="Xác nhận thay đổi trạng thái"
      :message="`Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng #${orderToUpdate?.id} từ '${getStatusLabel(oldStatus)}' sang '${getStatusLabel(newStatus)}'?`"
      description="Hành động này sẽ cập nhật trạng thái đơn hàng."
      confirm-text="Xác nhận"
      cancel-text="Hủy"
      :loading="updating"
      @confirm="handleStatusUpdate"
      @cancel="handleCancelStatusChange"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import ConfirmDialog from '@/assets/components/common/ConfirmDialog.vue'
import * as XLSX from 'xlsx'
import { printInvoice } from '@/utils/pdfGenerator'
import { downloadCsv, prepareOrdersForExport } from '@/utils/exportHelpers'
import AdminService from '@/services/adminService'

const adminStore = useAdminStore()

const orders = ref([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)
const showDetailModal = ref(false)
const selectedOrder = ref(null)

// Bulk selection state
const selectedOrders = ref([])
const bulkStatus = ref('')

// Search & Filter state
const filters = ref({
  search: '',
  status: '',
  startDate: '',
  endDate: ''
})
let searchTimeout = null

// Stats state
const stats = ref({
  totalOrders: 0,
  pendingOrders: 0,
  processingOrders: 0,
  completedOrders: 0,
  todayRevenue: 0
})

// Status change confirmation
const showStatusConfirm = ref(false)
const orderToUpdate = ref(null)
const oldStatus = ref('')
const newStatus = ref('')
const updating = ref(false)

const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

const isAllOrdersSelected = computed(() => {
  return orders.value.length > 0 && selectedOrders.value.length === orders.value.length
})

// Bulk selection methods
const toggleSelectOrder = (orderId) => {
  const index = selectedOrders.value.indexOf(orderId)
  if (index > -1) {
    selectedOrders.value.splice(index, 1)
  } else {
    selectedOrders.value.push(orderId)
  }
}

const toggleSelectAllOrders = () => {
  if (isAllOrdersSelected.value) {
    selectedOrders.value = []
  } else {
    selectedOrders.value = orders.value.map(o => o.id)
  }
}

const clearOrderSelection = () => {
  selectedOrders.value = []
  bulkStatus.value = ''
}

const bulkUpdateStatus = async () => {
  if (!bulkStatus.value) {
    ElMessage.warning('Vui lòng chọn trạng thái!')
    return
  }

  if (!confirm(`Bạn có chắc chắn muốn cập nhật ${selectedOrders.value.length} đơn hàng sang trạng thái "${getStatusLabel(bulkStatus.value)}"?`)) {
    return
  }

  try {
    loading.value = true
    
    // Update status for each order
    for (const orderId of selectedOrders.value) {
      await adminStore.updateOrderStatus(orderId, bulkStatus.value)
    }
    
    ElMessage.success({
      message: `Đã cập nhật ${selectedOrders.value.length} đơn hàng thành công!`,
      duration: 3000
    })
    
    // Clear selection and refresh list
    selectedOrders.value = []
    bulkStatus.value = ''
    await fetchOrders()
  } catch (error) {
    console.error('Lỗi khi cập nhật hàng loạt:', error)
    ElMessage.error({
      message: 'Có lỗi xảy ra khi cập nhật đơn hàng!',
      duration: 3000
    })
  } finally {
    loading.value = false
  }
}

const fetchOrders = async () => {
  try {
    loading.value = true
    
    // Prepare filters for API
    const apiFilters = {
      search: filters.value.search || undefined,
      status: filters.value.status || undefined,
      startDate: filters.value.startDate || undefined,
      endDate: filters.value.endDate || undefined
    }
    
    const result = await adminStore.fetchOrders(currentPage.value, pageSize.value, apiFilters)
    orders.value = result.content || []
    totalItems.value = result.totalElements || 0
    
    // Calculate stats
    calculateStats()
  } catch (error) {
    console.error('Lỗi khi tải danh sách đơn hàng:', error)
    ElMessage.error('Không thể tải danh sách đơn hàng.')
  } finally {
    loading.value = false
  }
}

const calculateStats = () => {
  const today = new Date().toISOString().split('T')[0]
  stats.value = {
    totalOrders: totalItems.value,
    pendingOrders: orders.value.filter(o => o.status === 'Pending').length,
    processingOrders: orders.value.filter(o => o.status === 'Processing').length,
    completedOrders: orders.value.filter(o => o.status === 'Completed').length,
    todayRevenue: orders.value
      .filter(o => o.createdAt && new Date(o.createdAt).toISOString().split('T')[0] === today)
      .reduce((sum, o) => sum + (o.totalAmount || 0), 0)
  }
}

const debounceSearch = () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 0 // Reset về trang đầu khi search
    fetchOrders()
  }, 500) // Debounce 500ms
}

const clearSearch = () => {
  filters.value.search = ''
  currentPage.value = 0
  fetchOrders()
}

const applyFilters = () => {
  currentPage.value = 0 // Reset về trang đầu khi filter
  fetchOrders()
}

const resetFilters = () => {
  filters.value.search = ''
  filters.value.status = ''
  filters.value.startDate = ''
  filters.value.endDate = ''
  currentPage.value = 0
  fetchOrders()
}

// Export to Excel
const exportToExcel = () => {
  try {
    // Chuẩn bị data để export
    const exportData = orders.value.map((order, index) => ({
      'STT': index + 1,
      'Mã đơn hàng': `#${order.id}`,
      'Khách hàng': order.customerName || 'N/A',
      'Email': order.customerEmail || 'N/A',
      'Tổng tiền': new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.totalAmount),
      'Trạng thái': getStatusLabel(order.status),
      'Ngày đặt': new Date(order.createdAt).toLocaleString('vi-VN')
    }))

    // Tạo worksheet từ data
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    
    // Tạo workbook
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Đơn hàng')
    
    // Tạo tên file với timestamp
    const timestamp = new Date().toISOString().slice(0, 10)
    const filename = `don-hang_${timestamp}.xlsx`
    
    // Download file
    XLSX.writeFile(workbook, filename)
    
    ElMessage.success({
      message: `Đã export ${exportData.length} đơn hàng thành công!`,
      duration: 3000
    })
  } catch (error) {
    console.error('Lỗi khi export Excel:', error)
    ElMessage.error({
      message: 'Không thể export dữ liệu. Vui lòng thử lại!',
      duration: 3000
    })
  }
}

const confirmStatusChange = (order, event) => {
  // Get the old and new status
  const select = event.target
  oldStatus.value = order._originalStatus || order.status
  newStatus.value = select.value
  
  // If no change, do nothing
  if (oldStatus.value === newStatus.value) {
    return
  }
  
  // Store order reference and original status
  orderToUpdate.value = order
  if (!order._originalStatus) {
    order._originalStatus = oldStatus.value
  }
  
  // Show confirmation dialog
  showStatusConfirm.value = true
}

const handleStatusUpdate = async () => {
  try {
    updating.value = true
    await adminStore.updateOrderStatus(orderToUpdate.value.id, newStatus.value)
    ElMessage.success(`Đã cập nhật trạng thái đơn hàng #${orderToUpdate.value.id} thành công!`)
    
    // Update the original status
    orderToUpdate.value._originalStatus = newStatus.value
    
    showStatusConfirm.value = false
  } catch (error) {
    console.error('Lỗi khi cập nhật trạng thái:', error)
    ElMessage.error('Không thể cập nhật trạng thái. Vui lòng thử lại!')
    
    // Restore old status on error
    orderToUpdate.value.status = oldStatus.value
  } finally {
    updating.value = false
  }
}

const handleCancelStatusChange = () => {
  // Restore the old status
  if (orderToUpdate.value) {
    orderToUpdate.value.status = oldStatus.value
  }
  showStatusConfirm.value = false
}

const getStatusLabel = (status) => {
  const labels = {
    'Pending': 'Chờ xử lý',
    'Processing': 'Đang xử lý',
    'Shipped': 'Đã gửi hàng',
    'Completed': 'Hoàn thành',
    'Cancelled': 'Đã hủy'
  }
  return labels[status] || status
}


const viewOrderDetail = async (order) => {
  try {
    loading.value = true
    // Fetch full order details from API
    try {
      const orderDetail = await AdminService.getOrderById(order.id)
      if (orderDetail) {
        selectedOrder.value = orderDetail
      } else {
        selectedOrder.value = order
      }
    } catch (apiError) {
      console.warn('Không thể tải chi tiết từ API, sử dụng dữ liệu cơ bản:', apiError)
      selectedOrder.value = order
    }
    showDetailModal.value = true
  } catch (error) {
    console.error('Lỗi khi tải chi tiết đơn hàng:', error)
    selectedOrder.value = order
    showDetailModal.value = true
  } finally {
    loading.value = false
  }
}

const handleCancelOrder = async (order) => {
  if (!confirm(`Bạn có chắc chắn muốn hủy đơn hàng #${order.id}?`)) {
    return
  }
  
  try {
    await adminStore.updateOrderStatus(order.id, 'Cancelled')
    ElMessage.success('Đã hủy đơn hàng thành công!')
    await fetchOrders()
  } catch (error) {
    console.error('Lỗi khi hủy đơn hàng:', error)
    ElMessage.error('Không thể hủy đơn hàng. Vui lòng thử lại!')
  }
}

const exportOrderToPDF = (order) => {
  if (!order) {
    ElMessage.warning('Không có thông tin đơn hàng để export')
    return
  }
  
  try {
    handlePrintInvoice(order)
    ElMessage.success('Đang mở cửa sổ in hóa đơn...')
  } catch (error) {
    console.error('Error exporting to PDF:', error)
    ElMessage.error('Không thể export PDF. Vui lòng thử lại!')
  }
}

const exportToPDF = () => {
  ElMessage.info('Tính năng export PDF đang được phát triển...')
}

const handlePrintInvoice = (order) => {
  if (!order) {
    ElMessage.warning('Không có thông tin đơn hàng để in')
    return
  }
  
  try {
    printInvoice(order)
    ElMessage.success('Đang mở cửa sổ in hóa đơn...')
  } catch (error) {
    console.error('Error printing invoice:', error)
    ElMessage.error('Không thể in hóa đơn. Vui lòng thử lại!')
  }
}

const changePage = (page) => {
  currentPage.value = page
  fetchOrders()
}

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN')
}

const formatDateTime = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getPaymentMethodLabel = (method) => {
  const labels = {
    'COD': 'Thanh toán khi nhận hàng',
    'BANK_TRANSFER': 'Chuyển khoản ngân hàng',
    'CREDIT_CARD': 'Thẻ tín dụng',
    'EWALLET': 'Ví điện tử'
  }
  return labels[method] || method
}

onMounted(() => {
  fetchOrders()
})
</script>




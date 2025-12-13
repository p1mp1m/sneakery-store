<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">payment</i>
            Quản lý thanh toán
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Theo dõi và quản lý tất cả giao dịch thanh toán</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="exportPayments('csv')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_download</i>
            CSV
          </button>
          <button @click="exportPayments('json')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">code</i>
            JSON
          </button>
          <button @click="refreshPayments" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">refresh</i>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-400 to-teal-400 flex items-center justify-center">
            <i class="material-icons text-white text-lg">attach_money</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatCurrency(totalRevenue) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng doanh thu</p>
          <p class="text-xs flex items-center gap-1" :class="paymentStats.revenueTrend > 0 ? 'text-green-600 dark:text-green-400' : paymentStats.revenueTrend < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="paymentStats.revenueTrend > 0 ? 'trending_up' : paymentStats.revenueTrend < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="paymentStats.revenueTrend !== 0">{{ paymentStats.revenueTrend > 0 ? '+' : '' }}{{ paymentStats.revenueTrend.toFixed(1) }}% so với tháng trước</span>
            <span v-else>Không đổi so với tháng trước</span>
          </p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ completedPayments }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Giao dịch thành công</p>
          <p class="text-xs text-green-600 dark:text-green-400 flex items-center gap-1">
            <i class="material-icons text-sm">done</i>
            {{ Math.round(paymentStats.successRate || 0) }}% tổng số
          </p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-pink-500 to-red-500 flex items-center justify-center">
            <i class="material-icons text-white text-lg">error</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ failedPayments }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Giao dịch thất bại</p>
          <p class="text-xs text-red-600 dark:text-red-400 flex items-center gap-1">
            <i class="material-icons text-sm">trending_down</i>
            Cần xem xét
          </p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-400 to-cyan-400 flex items-center justify-center">
            <i class="material-icons text-white text-lg">pending</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ pendingPayments }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Đang xử lý</p>
          <p class="text-xs text-gray-600 dark:text-gray-400 flex items-center gap-1">
            <i class="material-icons text-sm">schedule</i>
            Chờ xử lý
          </p>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
            <input 
              type="text" 
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              v-model="searchKeyword"
              placeholder="Tìm theo mã giao dịch, đơn hàng..."
            />
            <button v-if="searchKeyword" @click="clearSearch" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>
        
        <div class="flex items-center gap-2">
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
              <i class="material-icons text-sm">filter_list</i>
              Trạng thái
            </label>
            <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterStatus">
              <option value="all">Tất cả</option>
              <option value="completed">Thành công</option>
              <option value="pending">Đang xử lý</option>
              <option value="failed">Thất bại</option>
              <option value="refunded">Đã hoàn tiền</option>
            </select>
          </div>

          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
              <i class="material-icons text-sm">payment</i>
              Phương thức
            </label>
            <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterMethod">
              <option value="all">Tất cả</option>
              <option value="cod">COD</option>
              <option value="vnpay">VNPay</option>
              <option value="momo">MoMo</option>
              <option value="zalopay">ZaloPay</option>
              <option value="bank_transfer">Chuyển khoản</option>
              <option value="credit_card">Thẻ tín dụng</option>
            </select>
          </div>

          <button class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium mt-6" @click="resetFilters">
            <i class="material-icons text-base">refresh</i>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Payments Table -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
      <div class="px-4 py-3 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between">
        <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100">Danh sách giao dịch thanh toán</h3>
        <span class="text-sm text-gray-600 dark:text-gray-400">{{ filteredPayments.length }} giao dịch</span>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex flex-col items-center justify-center p-12">
        <div class="space-y-4" role="status" aria-live="polite">
          <LoadingSkeleton
            v-for="n in 5"
            :key="n"
            type="list"
          />
          <span class="sr-only">Đang tải danh sách giao dịch</span>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredPayments.length === 0" class="flex flex-col items-center justify-center p-12">
        <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">payment</i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Không có giao dịch nào</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
          Chưa có giao dịch thanh toán nào được tìm thấy
        </p>
      </div>

      <!-- Payments Table -->
      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Mã giao dịch</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Đơn hàng</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Phương thức</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Số tiền</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thời gian</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="payment in paginatedPayments" :key="payment.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td class="px-4 py-4">
                <code class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100">
                  {{ payment.transactionId || 'N/A' }}
                </code>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ payment.orderNumber }}</div>
                <div class="text-xs text-gray-500 dark:text-gray-400">{{ formatDate(payment.createdAt) }}</div>
              </td>
              <td class="px-4 py-4">
                <div class="flex items-center gap-2">
                  <i class="material-icons text-base text-gray-600 dark:text-gray-400">{{ getPaymentMethodIcon(payment.paymentMethod) }}</i>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ getPaymentMethodName(payment.paymentMethod) }}</span>
                </div>
              </td>
              <td class="px-4 py-4 text-sm font-semibold text-gray-900 dark:text-gray-100">{{ formatCurrency(payment.amount) }}</td>
              <td class="px-4 py-4">
                <span 
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': payment.status === 'pending',
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': payment.status === 'completed',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': payment.status === 'failed',
                    'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400': payment.status === 'refunded'
                  }"
                >
                  {{ getStatusText(payment.status) }}
                </span>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm text-gray-900 dark:text-gray-100">{{ formatDateTime(payment.createdAt) }}</div>
                <div v-if="payment.paidAt" class="text-xs text-gray-500 dark:text-gray-400">
                  Thanh toán: {{ formatDateTime(payment.paidAt) }}
                </div>
              </td>
              <td class="px-4 py-4 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button 
                    @click="viewPaymentDetail(payment)" 
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors"
                    title="Xem chi tiết"
                  >
                    <i class="material-icons text-base">visibility</i>
                  </button>
                  <button 
                    v-if="payment.status === 'failed'" 
                    @click="retryPayment(payment)" 
                    class="p-1.5 text-green-600 dark:text-green-400 hover:bg-green-50 dark:hover:bg-green-900/20 rounded-lg transition-colors"
                    title="Thử lại"
                  >
                    <i class="material-icons text-base">refresh</i>
                  </button>
                  <button 
                    v-if="payment.status === 'completed'" 
                    @click="refundPayment(payment)" 
                    class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
                    title="Hoàn tiền"
                  >
                    <i class="material-icons text-base">money_off</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="!loading && filteredPayments.length > 0 && totalPages > 1" class="flex items-center justify-between gap-4 px-4 py-3 border-t border-gray-200 dark:border-gray-700">
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Hiển thị {{ currentPage * pageSize + 1 }} - {{ Math.min((currentPage + 1) * pageSize, filteredPayments.length) }} trong tổng số {{ filteredPayments.length }} giao dịch
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="currentPage === 0"
            @click="goToPage(currentPage - 1)"
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
            @click="goToPage(currentPage + 1)"
          >
            Sau
            <i class="material-icons text-base">chevron_right</i>
          </button>
        </div>
      </div>
    </div>

    <!-- Payment Detail Modal -->
    <div 
      v-if="showDetailModal" 
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 dark:bg-opacity-70 p-4"
      @click="closeDetailModal"
    >
      <div 
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        @click.stop
      >
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">payment</i>
            Chi tiết giao dịch
          </h3>
          <button 
            @click="closeDetailModal" 
            class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          >
            <i class="material-icons text-base">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="selectedPayment">
            <div class="mb-6">
              <h4 class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">Thông tin giao dịch</h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Mã giao dịch:</label>
                  <code class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-sm font-mono text-gray-900 dark:text-gray-100">
                    {{ selectedPayment.transactionId || 'N/A' }}
                  </code>
                </div>
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Đơn hàng:</label>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ selectedPayment.orderNumber }}</span>
                </div>
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Phương thức:</label>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ getPaymentMethodName(selectedPayment.paymentMethod) }}</span>
                </div>
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Số tiền:</label>
                  <span class="text-sm font-semibold text-gray-900 dark:text-gray-100">{{ formatCurrency(selectedPayment.amount) }}</span>
                </div>
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Trạng thái:</label>
                  <span 
                    class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                    :class="{
                      'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': selectedPayment.status === 'pending',
                      'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': selectedPayment.status === 'completed',
                      'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': selectedPayment.status === 'failed',
                      'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400': selectedPayment.status === 'refunded'
                    }"
                  >
                    {{ getStatusText(selectedPayment.status) }}
                  </span>
                </div>
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Thời gian tạo:</label>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ formatDateTime(selectedPayment.createdAt) }}</span>
                </div>
                <div v-if="selectedPayment.paidAt">
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Thời gian thanh toán:</label>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ formatDateTime(selectedPayment.paidAt) }}</span>
                </div>
                <div v-if="selectedPayment.refundedAt">
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Thời gian hoàn tiền:</label>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ formatDateTime(selectedPayment.refundedAt) }}</span>
                </div>
              </div>
            </div>
            
            <div v-if="selectedPayment.gatewayResponse" class="mb-6">
              <h4 class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3">Phản hồi từ cổng thanh toán</h4>
              <pre class="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg text-xs font-mono text-gray-900 dark:text-gray-100 overflow-x-auto">{{ selectedPayment.gatewayResponse }}</pre>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end gap-2 p-4 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="closeDetailModal" 
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import notificationService from '@/utils/notificationService'
import confirmDialogService from '@/utils/confirmDialogService'
import logger from '@/utils/logger'
import LoadingSkeleton from '@/components/common/LoadingSkeleton.vue'
import { formatPrice, formatCurrency, formatDate, formatDateTime } from '@/utils/formatters'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const payments = ref([])
const searchKeyword = ref('')
const filterStatus = ref('all')
const filterMethod = ref('all')
const currentPage = ref(0)
const pageSize = ref(10)
const showDetailModal = ref(false)
const selectedPayment = ref(null)

// Payment stats từ API (data thật)
const paymentStats = ref({
  totalRevenue: 0,
  completedCount: 0,
  failedCount: 0,
  pendingCount: 0,
  refundedCount: 0,
  totalCount: 0,
  successRate: 0,
  revenueTrend: 0
})

// Computed - ưu tiên dùng stats từ API
const totalPayments = computed(() => paymentStats.value.totalCount || payments.value.length)
const completedPayments = computed(() => paymentStats.value.completedCount || payments.value.filter(p => p.status === 'completed').length)
const failedPayments = computed(() => paymentStats.value.failedCount || payments.value.filter(p => p.status === 'failed').length)
const pendingPayments = computed(() => paymentStats.value.pendingCount || payments.value.filter(p => p.status === 'pending').length)
const totalRevenue = computed(() => paymentStats.value.totalRevenue || payments.value.filter(p => p.status === 'completed').reduce((sum, p) => sum + (p.amount || 0), 0))

const filteredPayments = computed(() => {
  let filtered = payments.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.transactionId?.toLowerCase().includes(keyword) ||
      p.orderNumber.toLowerCase().includes(keyword)
    )
  }

  if (filterStatus.value !== 'all') {
    filtered = filtered.filter(p => p.status === filterStatus.value)
  }

  if (filterMethod.value !== 'all') {
    filtered = filtered.filter(p => p.paymentMethod === filterMethod.value)
  }

  return filtered
})

const totalPages = computed(() => Math.ceil(filteredPayments.value.length / pageSize.value))
const paginatedPayments = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return filteredPayments.value.slice(start, end)
})

// Methods
const fetchPayments = async () => {
  loading.value = true
  try {
    // Load payment stats từ API (data thật)
    try {
      const statsResult = await adminStore.fetchPaymentStats()
      if (statsResult) {
        paymentStats.value = statsResult
        logger.log('✅ Payment stats loaded:', paymentStats.value)
      }
    } catch (error) {
      logger.warn('Payment stats API error:', error)
    }
    
    // Load từ API - chỉ dùng dữ liệu thật từ database
    const apiFilters = {}
    
    if (searchKeyword.value) {
      apiFilters.search = searchKeyword.value
    }
    
    if (filterStatus.value !== 'all') {
      apiFilters.status = filterStatus.value
    }
    
    if (filterMethod.value !== 'all') {
      apiFilters.paymentMethod = filterMethod.value
    }
    
    const result = await adminStore.fetchPayments(0, 100, apiFilters)
    payments.value = result.content || []
    
    if (payments.value.length === 0) {
      notificationService.info('Thông tin','Chưa có giao dịch thanh toán nào')
    } else {
      logger.log('✅ Payments loaded from API:', payments.value.length, 'payments')
    }
  } catch (error) {
    logger.error('Error loading payments:', error)
    notificationService.apiError(error, 'Không thể tải danh sách giao dịch')
    payments.value = []
  } finally {
    loading.value = false
  }
}

const refreshPayments = () => {
  fetchPayments()
  notificationService.success('Thành công','Đã làm mới danh sách giao dịch')
}

const clearSearch = () => {
  searchKeyword.value = ''
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = 'all'
  filterMethod.value = 'all'
  currentPage.value = 0
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

const viewPaymentDetail = (payment) => {
  selectedPayment.value = payment
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedPayment.value = null
}

const retryPayment = async (payment) => {
  try {
    await confirmDialogService.confirm(
      'Bạn có chắc chắn muốn thử lại giao dịch này?',
      'Xác nhận thử lại',
      {
        confirmButtonText: 'Thử lại',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    // Simulate retry
    payment.status = 'pending'
    notificationService.success('Thành công','Đã gửi yêu cầu thử lại giao dịch')
  } catch {
    // User cancelled
  }
}

const refundPayment = async (payment) => {
  try {
    await confirmDialogService.confirm(
      'Bạn có chắc chắn muốn hoàn tiền cho giao dịch này?',
      'Xác nhận hoàn tiền',
      {
        confirmButtonText: 'Hoàn tiền',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    // Simulate refund
    payment.status = 'refunded'
    payment.refundedAt = new Date().toISOString()
    notificationService.success('Thành công','Đã hoàn tiền thành công')
  } catch {
    // User cancelled
  }
}

const exportPayments = (format) => {
  try {
    const exportData = filteredPayments.value.map(payment => ({
      'ID': payment.id,
      'Mã giao dịch': payment.transactionId || 'N/A',
      'Đơn hàng': payment.orderNumber,
      'Phương thức': getPaymentMethodName(payment.paymentMethod),
      'Số tiền': formatCurrency(payment.amount),
      'Trạng thái': getStatusText(payment.status),
      'Thời gian tạo': formatDateTime(payment.createdAt),
      'Thời gian thanh toán': payment.paidAt ? formatDateTime(payment.paidAt) : 'N/A'
    }))

    if (format === 'csv') {
      downloadCsv('payments', exportData)
      notificationService.success('Thành công','Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('payments', exportData)
      notificationService.success('Thành công','Xuất JSON thành công!')
    }
  } catch (error) {
    logger.error('Export error:', error)
    notificationService.apiError(error, 'Có lỗi xảy ra khi xuất dữ liệu')
  }
}

// Helper functions
const getPaymentMethodIcon = (method) => {
  const icons = {
    cod: 'local_shipping',
    vnpay: 'account_balance',
    momo: 'phone_android',
    zalopay: 'phone_android',
    bank_transfer: 'account_balance_wallet',
    credit_card: 'credit_card'
  }
  return icons[method] || 'payment'
}

const getPaymentMethodName = (method) => {
  const names = {
    cod: 'COD',
    vnpay: 'VNPay',
    momo: 'MoMo',
    zalopay: 'ZaloPay',
    bank_transfer: 'Chuyển khoản',
    credit_card: 'Thẻ tín dụng'
  }
  return names[method] || method
}

const getStatusText = (status) => {
  const statuses = {
    pending: 'Đang xử lý',
    completed: 'Thành công',
    failed: 'Thất bại',
    refunded: 'Đã hoàn tiền'
  }
  return statuses[status] || status
}

// formatCurrency, formatDate, formatDateTime đã được import từ @/utils/formatters

// Lifecycle
onMounted(() => {
  fetchPayments()
})
</script>




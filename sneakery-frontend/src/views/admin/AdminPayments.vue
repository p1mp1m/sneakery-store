<template>
  <div class="admin-page admin-payments">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">payment</span>
            Quản lý thanh toán
          </h1>
          <p class="page-subtitle">Theo dõi và quản lý tất cả giao dịch thanh toán</p>
        </div>
        <div class="header-actions">
          <button @click="exportPayments('csv')" class="btn btn-secondary">
            <span class="material-icons">file_download</span>
            CSV
          </button>
          <button @click="exportPayments('json')" class="btn btn-secondary">
            <span class="material-icons">code</span>
            JSON
          </button>
          <button @click="refreshPayments" class="btn btn-primary">
            <span class="material-icons">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="stats-grid animate-fade-up">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <span class="material-icons">attach_money</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Tổng doanh thu</div>
          <div class="stat-value">{{ formatCurrency(totalRevenue) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            +12.5% so với tháng trước
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <span class="material-icons">check_circle</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Giao dịch thành công</div>
          <div class="stat-value">{{ completedPayments }}</div>
          <div class="stat-change positive">
            <span class="material-icons">done</span>
            {{ Math.round((completedPayments / totalPayments) * 100) || 0 }}% tổng số
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <span class="material-icons">error</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Giao dịch thất bại</div>
          <div class="stat-value">{{ failedPayments }}</div>
          <div class="stat-change negative">
            <span class="material-icons">trending_down</span>
            Cần xem xét
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <span class="material-icons">pending</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Đang xử lý</div>
          <div class="stat-value">{{ pendingPayments }}</div>
          <div class="stat-change neutral">
            <span class="material-icons">schedule</span>
            Chờ xử lý
          </div>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="filters-section animate-fade-up">
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">search</span>
            Tìm kiếm
          </label>
          <div class="search-box">
            <span class="material-icons search-icon">search</span>
            <input 
              type="text" 
              class="search-input" 
              v-model="searchKeyword"
              placeholder="Tìm theo mã giao dịch, đơn hàng..."
            />
            <button v-if="searchKeyword" @click="clearSearch" class="search-clear">
              <span class="material-icons">close</span>
            </button>
          </div>
        </div>
        
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">filter_list</span>
            Trạng thái
          </label>
          <select class="filter-select" v-model="filterStatus">
            <option value="all">Tất cả</option>
            <option value="completed">Thành công</option>
            <option value="pending">Đang xử lý</option>
            <option value="failed">Thất bại</option>
            <option value="refunded">Đã hoàn tiền</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">payment</span>
            Phương thức
          </label>
          <select class="filter-select" v-model="filterMethod">
            <option value="all">Tất cả</option>
            <option value="cod">COD</option>
            <option value="vnpay">VNPay</option>
            <option value="momo">MoMo</option>
            <option value="zalopay">ZaloPay</option>
            <option value="bank_transfer">Chuyển khoản</option>
            <option value="credit_card">Thẻ tín dụng</option>
          </select>
        </div>

        <div class="filter-group">
          <button class="btn btn-outline" @click="resetFilters">
            <span class="material-icons">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Payments Table -->
    <div class="table-container animate-fade-up">
      <div class="table-header">
        <h3 class="table-title">Danh sách giao dịch thanh toán</h3>
        <div class="table-actions">
          <span class="table-info">{{ filteredPayments.length }} giao dịch</span>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="filteredPayments.length === 0" class="empty-state">
        <span class="material-icons">payment</span>
        <h3>Không có giao dịch nào</h3>
        <p>Chưa có giao dịch thanh toán nào được tìm thấy</p>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Mã giao dịch</th>
              <th>Đơn hàng</th>
              <th>Phương thức</th>
              <th>Số tiền</th>
              <th>Trạng thái</th>
              <th>Thời gian</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="payment in paginatedPayments" :key="payment.id" class="payment-row">
              <td>
                <div class="transaction-id">{{ payment.transactionId || 'N/A' }}</div>
              </td>
              <td>
                <div class="order-info">
                  <div class="order-number">{{ payment.orderNumber }}</div>
                  <div class="order-date">{{ formatDate(payment.createdAt) }}</div>
                </div>
              </td>
              <td>
                <div class="payment-method">
                  <span class="method-icon">
                    <span class="material-icons">{{ getPaymentMethodIcon(payment.paymentMethod) }}</span>
                  </span>
                  <span class="method-name">{{ getPaymentMethodName(payment.paymentMethod) }}</span>
                </div>
              </td>
              <td>
                <div class="amount">{{ formatCurrency(payment.amount) }}</div>
              </td>
              <td>
                <span :class="['status-badge', `status-${payment.status}`]">
                  {{ getStatusText(payment.status) }}
                </span>
              </td>
              <td>
                <div class="payment-time">
                  <div class="time">{{ formatDateTime(payment.createdAt) }}</div>
                  <div v-if="payment.paidAt" class="paid-time">
                    Thanh toán: {{ formatDateTime(payment.paidAt) }}
                  </div>
                </div>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="viewPaymentDetail(payment)" class="btn-icon btn-view" title="Xem chi tiết">
                    <span class="material-icons">visibility</span>
                  </button>
                  <button v-if="payment.status === 'failed'" @click="retryPayment(payment)" class="btn-icon btn-retry" title="Thử lại">
                    <span class="material-icons">refresh</span>
                  </button>
                  <button v-if="payment.status === 'completed'" @click="refundPayment(payment)" class="btn-icon btn-refund" title="Hoàn tiền">
                    <span class="material-icons">money_off</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination-container">
        <button 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 0"
          class="page-btn"
        >
          <span class="material-icons">chevron_left</span>
        </button>
        
        <span class="page-info">
          Trang {{ currentPage + 1 }} / {{ totalPages }}
        </span>
        
        <button 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage === totalPages - 1"
          class="page-btn"
        >
          <span class="material-icons">chevron_right</span>
        </button>
      </div>
    </div>

    <!-- Payment Detail Modal -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="material-icons">payment</span>
            Chi tiết giao dịch
          </h3>
          <button @click="closeDetailModal" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedPayment" class="payment-detail">
            <div class="detail-section">
              <h4>Thông tin giao dịch</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>Mã giao dịch:</label>
                  <span>{{ selectedPayment.transactionId || 'N/A' }}</span>
                </div>
                <div class="detail-item">
                  <label>Đơn hàng:</label>
                  <span>{{ selectedPayment.orderNumber }}</span>
                </div>
                <div class="detail-item">
                  <label>Phương thức:</label>
                  <span>{{ getPaymentMethodName(selectedPayment.paymentMethod) }}</span>
                </div>
                <div class="detail-item">
                  <label>Số tiền:</label>
                  <span class="amount">{{ formatCurrency(selectedPayment.amount) }}</span>
                </div>
                <div class="detail-item">
                  <label>Trạng thái:</label>
                  <span :class="['status-badge', `status-${selectedPayment.status}`]">
                    {{ getStatusText(selectedPayment.status) }}
                  </span>
                </div>
                <div class="detail-item">
                  <label>Thời gian tạo:</label>
                  <span>{{ formatDateTime(selectedPayment.createdAt) }}</span>
                </div>
                <div v-if="selectedPayment.paidAt" class="detail-item">
                  <label>Thời gian thanh toán:</label>
                  <span>{{ formatDateTime(selectedPayment.paidAt) }}</span>
                </div>
                <div v-if="selectedPayment.refundedAt" class="detail-item">
                  <label>Thời gian hoàn tiền:</label>
                  <span>{{ formatDateTime(selectedPayment.refundedAt) }}</span>
                </div>
              </div>
            </div>
            
            <div v-if="selectedPayment.gatewayResponse" class="detail-section">
              <h4>Phản hồi từ cổng thanh toán</h4>
              <pre class="gateway-response">{{ selectedPayment.gatewayResponse }}</pre>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeDetailModal" class="btn btn-secondary">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAdminStore } from '@/stores/admin'

// Store
const adminStore = useAdminStore()

// State
const loading = ref(false)
const payments = ref([])
const totalItems = ref(0)
const searchKeyword = ref('')
const filterStatus = ref('all')
const filterMethod = ref('all')
const currentPage = ref(0)
const pageSize = ref(10)
const showDetailModal = ref(false)
const selectedPayment = ref(null)

// Stats
const totalRevenue = ref(0)
const completedPayments = ref(0)
const failedPayments = ref(0)
const pendingPayments = ref(0)

// Mock data removed - using real API data

// Computed - Sử dụng ref values thay vì computed cho stats
const totalPayments = computed(() => payments.value.length)

const filteredPayments = computed(() => {
  let filtered = payments.value || []

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
    const result = await adminStore.fetchPayments(currentPage.value, pageSize.value, {
      status: filterStatus.value !== 'all' ? filterStatus.value : undefined,
      paymentMethod: filterMethod.value !== 'all' ? filterMethod.value : undefined
    })
    
    payments.value = result.content || []
    totalItems.value = result.totalElements || 0
    
    // Fetch stats
    const stats = await adminStore.fetchPaymentStats()
    if (stats) {
      totalRevenue.value = stats.totalRevenue || 0
      completedPayments.value = stats.completedPayments || 0
      failedPayments.value = stats.failedPayments || 0
      pendingPayments.value = stats.pendingPayments || 0
    }
  } catch (error) {
    console.error('Lỗi khi tải giao dịch:', error)
    ElMessage.error('Không thể tải danh sách giao dịch')
  } finally {
    loading.value = false
  }
}

const refreshPayments = () => {
  fetchPayments()
  ElMessage.success('Đã làm mới danh sách giao dịch')
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
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn thử lại giao dịch này?',
      'Xác nhận thử lại',
      {
        confirmButtonText: 'Thử lại',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    await adminStore.updatePaymentStatus(payment.id, 'pending')
    ElMessage.success('Đã gửi yêu cầu thử lại giao dịch')
    fetchPayments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error retrying payment:', error)
      ElMessage.error('Không thể thử lại giao dịch')
    }
  }
}

const refundPayment = async (payment) => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn hoàn tiền cho giao dịch này?',
      'Xác nhận hoàn tiền',
      {
        confirmButtonText: 'Hoàn tiền',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    await adminStore.refundPayment(payment.id, { reason: 'Admin refund' })
    ElMessage.success('Đã hoàn tiền thành công')
    fetchPayments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error refunding payment:', error)
      ElMessage.error('Không thể hoàn tiền giao dịch')
    }
  }
}

const exportPayments = (format) => {
  try {
    const dataToExport = filteredPayments.value || []
    if (dataToExport.length === 0) {
      ElMessage.warning('Không có dữ liệu để xuất')
      return
    }
    
    const exportData = dataToExport.map(payment => ({
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
      downloadCsv(exportData, 'payments.csv')
      ElMessage.success('Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('payments', exportData)
      ElMessage.success('Xuất JSON thành công!')
    }
  } catch (error) {
    console.error('Export error:', error)
    ElMessage.error('Có lỗi xảy ra khi xuất dữ liệu!')
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

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('vi-VN')
}

const formatDateTime = (dateString) => {
  return new Date(dateString).toLocaleString('vi-VN')
}

// Lifecycle
onMounted(() => {
  fetchPayments()
})
</script>

<style scoped>
/* Page Header */
.page-header {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.title-section {
  flex: 1;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

/* Enhanced Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.stat-card {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--accent-primary), var(--accent-secondary));
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.2);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--space-4);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon .material-icons {
  font-size: 24px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  font-weight: var(--font-medium);
}

.stat-value {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.stat-change {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.stat-change.positive {
  color: var(--success-text);
}

.stat-change.negative {
  color: var(--error-text);
}

.stat-change.neutral {
  color: var(--text-secondary);
}

.stat-change .material-icons {
  font-size: 16px;
}

/* Filters Section */
.filters-section {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: var(--space-4);
  align-items: end;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: var(--space-3);
  color: var(--text-tertiary);
  font-size: 20px;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-3) var(--space-3) var(--space-10);
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: rgba(15, 23, 42, 0.4);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.6);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-clear {
  position: absolute;
  right: var(--space-3);
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.search-clear:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Table */
.table-container {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.table-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.table-info {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th {
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
  padding: var(--space-4);
  text-align: left;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-table td {
  padding: var(--space-4);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
}

.admin-table tbody tr:hover {
  background: rgba(255, 255, 255, 0.05);
}

/* Status Badges */
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-completed {
  background: var(--success-bg);
  color: var(--success-text);
}

.status-pending {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.status-failed {
  background: var(--error-bg);
  color: var(--error-text);
}

.status-refunded {
  background: var(--info-bg);
  color: var(--info-text);
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: var(--space-2);
}

.btn-icon {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.btn-icon:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.btn-view:hover {
  background: var(--info-bg);
  color: var(--info-text);
}

.btn-retry:hover {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.btn-refund:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
}

.modal-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

.modal-body {
  padding: var(--space-6);
}

.detail-section {
  margin-bottom: var(--space-6);
}

.detail-section h4 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

.detail-grid {
  display: grid;
  gap: var(--space-3);
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-2) 0;
  border-bottom: 1px solid var(--border-primary);
}

.detail-item label {
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.gateway-response {
  background: var(--bg-secondary);
  padding: var(--space-4);
  border-radius: var(--radius-md);
  font-family: monospace;
  font-size: var(--text-sm);
  color: var(--text-primary);
  white-space: pre-wrap;
  overflow-x: auto;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.page-btn {
  width: 40px;
  height: 40px;
  border: 1px solid var(--border-primary);
  background: transparent;
  color: var(--text-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.page-btn:hover:not(:disabled) {
  background: var(--bg-secondary);
  border-color: var(--accent-primary);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

/* Loading & Empty States */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12);
  color: var(--text-secondary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-primary);
  border-top: 3px solid var(--accent-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--space-4);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12);
  text-align: center;
}

.empty-state .material-icons {
  font-size: 64px;
  color: var(--text-tertiary);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.empty-state p {
  color: var(--text-secondary);
}

/* Responsive */
@media (max-width: 768px) {
  .filter-row {
    grid-template-columns: 1fr;
    gap: var(--space-3);
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .admin-table {
    font-size: var(--text-sm);
  }
  
  .admin-table th,
  .admin-table td {
    padding: var(--space-2);
  }
}
</style>

<template>
  <div class="admin-page admin-returns">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">
            <i class="material-icons">assignment_return</i>
            Quản lý Trả Hàng
          </h1>
          <p class="page-subtitle">
            <i class="material-icons">info</i>
            Quản lý yêu cầu trả hàng và hoàn tiền
          </p>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stats-card warning">
        <div class="stats-header">
          <div class="stats-icon warning">
            <i class="material-icons">pending_actions</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.pendingReturns }}</div>
            <div class="stats-label">Chờ duyệt</div>
          </div>
        </div>
      </div>
      <div class="stats-card info">
        <div class="stats-header">
          <div class="stats-icon info">
            <i class="material-icons">local_shipping</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.inTransit }}</div>
            <div class="stats-label">Đang vận chuyển</div>
          </div>
        </div>
      </div>
      <div class="stats-card success">
        <div class="stats-header">
          <div class="stats-icon success">
            <i class="material-icons">check_circle</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.completed }}</div>
            <div class="stats-label">Đã hoàn thành</div>
          </div>
        </div>
      </div>
      <div class="stats-card danger">
        <div class="stats-header">
          <div class="stats-icon danger">
            <i class="material-icons">cancel</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.rejected }}</div>
            <div class="stats-label">Từ chối</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="filters-section">
      <div class="filter-row">
        <div class="filter-group">
          <label>Tìm kiếm</label>
          <div class="search-box">
            <i class="material-icons search-icon">search</i>
            <input 
              v-model="filters.search"
              type="text" 
              class="search-input" 
              placeholder="Tìm theo mã đơn, khách hàng..."
              @input="handleSearch"
            />
          </div>
        </div>
        <div class="filter-group">
          <label>Trạng thái</label>
          <select v-model="filters.status" @change="fetchReturns" class="form-control">
            <option value="">Tất cả</option>
            <option value="pending">Chờ duyệt</option>
            <option value="approved">Đã duyệt</option>
            <option value="in_transit">Đang vận chuyển</option>
            <option value="received">Đã nhận hàng</option>
            <option value="completed">Hoàn thành</option>
            <option value="rejected">Từ chối</option>
          </select>
        </div>
        <div class="filter-group">
          <label>Lý do</label>
          <select v-model="filters.reason" @change="fetchReturns" class="form-control">
            <option value="">Tất cả</option>
            <option value="defective">Lỗi sản phẩm</option>
            <option value="wrong_item">Giao sai hàng</option>
            <option value="size_issue">Không vừa size</option>
            <option value="changed_mind">Đổi ý</option>
            <option value="other">Khác</option>
          </select>
        </div>
        <div class="filter-group">
          <label>&nbsp;</label>
          <button @click="resetFilters" class="btn btn-secondary">
            <i class="material-icons">refresh</i>
            Xóa bộ lọc
          </button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="table-container">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="returns.length === 0" class="empty-state">
        <i class="material-icons">assignment_return</i>
        <h3>Chưa có yêu cầu trả hàng</h3>
        <p>Danh sách yêu cầu trả hàng sẽ hiển thị ở đây</p>
      </div>

      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>Mã đơn hàng</th>
            <th>Khách hàng</th>
            <th>Sản phẩm</th>
            <th>Lý do</th>
            <th>Số tiền</th>
            <th>Ngày yêu cầu</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in returns" :key="item.id">
            <td>
              <strong>#{{ item.orderNumber }}</strong>
            </td>
            <td>
              <div class="customer-cell">
                <strong>{{ item.customerName }}</strong>
                <p>{{ item.customerEmail }}</p>
              </div>
            </td>
            <td>
              <div class="product-cell">
                <img :src="item.productImage" :alt="item.productName" class="product-thumb" />
                <div>
                  <strong>{{ item.productName }}</strong>
                  <p>{{ item.variant }}</p>
                  <p>SL: {{ item.quantity }}</p>
                </div>
              </div>
            </td>
            <td>
              <span class="reason-badge">{{ getReasonText(item.reason) }}</span>
              <p class="reason-note">{{ item.note }}</p>
            </td>
            <td>
              <strong class="amount">{{ formatPrice(item.refundAmount) }}</strong>
            </td>
            <td>{{ formatDate(item.createdAt) }}</td>
            <td>
              <span class="status-badge" :class="getStatusClass(item.status)">
                {{ getStatusText(item.status) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="viewReturnDetail(item)" class="btn-icon" title="Xem chi tiết">
                  <i class="material-icons">visibility</i>
                </button>
                <button 
                  v-if="item.status === 'pending'" 
                  @click="approveReturn(item)" 
                  class="btn-icon" 
                  title="Duyệt"
                >
                  <i class="material-icons">check_circle</i>
                </button>
                <button 
                  v-if="item.status === 'pending'" 
                  @click="rejectReturn(item)" 
                  class="btn-icon danger" 
                  title="Từ chối"
                >
                  <i class="material-icons">cancel</i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Detail Dialog -->
    <div v-if="showDetailDialog" class="modal-overlay" @click="showDetailDialog = false">
      <div class="modal modal-lg" @click.stop>
        <div class="modal-header">
          <h3>Chi tiết yêu cầu trả hàng #{{ selectedReturn?.id }}</h3>
          <button @click="showDetailDialog = false" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedReturn" class="return-detail">
            <div class="detail-section">
              <h4>Thông tin đơn hàng</h4>
              <p><strong>Mã đơn:</strong> #{{ selectedReturn.orderNumber }}</p>
              <p><strong>Khách hàng:</strong> {{ selectedReturn.customerName }}</p>
              <p><strong>Email:</strong> {{ selectedReturn.customerEmail }}</p>
              <p><strong>SĐT:</strong> {{ selectedReturn.customerPhone }}</p>
            </div>
            <div class="detail-section">
              <h4>Thông tin sản phẩm</h4>
              <p><strong>Sản phẩm:</strong> {{ selectedReturn.productName }}</p>
              <p><strong>Biến thể:</strong> {{ selectedReturn.variant }}</p>
              <p><strong>Số lượng:</strong> {{ selectedReturn.quantity }}</p>
              <p><strong>Giá:</strong> {{ formatPrice(selectedReturn.unitPrice) }}</p>
            </div>
            <div class="detail-section">
              <h4>Lý do trả hàng</h4>
              <p><strong>Lý do:</strong> {{ getReasonText(selectedReturn.reason) }}</p>
              <p><strong>Ghi chú:</strong> {{ selectedReturn.note || 'Không có' }}</p>
            </div>
            <div v-if="selectedReturn.images" class="detail-section">
              <h4>Hình ảnh đính kèm</h4>
              <div class="return-images">
                <img 
                  v-for="(img, index) in selectedReturn.images" 
                  :key="index"
                  :src="img" 
                  alt="Return image"
                  class="return-image"
                />
              </div>
            </div>
            <div class="detail-section">
              <h4>Thông tin hoàn tiền</h4>
              <p><strong>Số tiền:</strong> {{ formatPrice(selectedReturn.refundAmount) }}</p>
              <p><strong>Phương thức:</strong> {{ selectedReturn.refundMethod || 'Chưa xác định' }}</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showDetailDialog = false" class="btn btn-secondary">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

// State
const loading = ref(false)
const showDetailDialog = ref(false)
const selectedReturn = ref(null)
const returns = ref([])

const filters = reactive({
  search: '',
  status: '',
  reason: ''
})

const stats = reactive({
  pendingReturns: 0,
  inTransit: 0,
  completed: 0,
  rejected: 0
})

// Methods
const fetchReturns = async () => {
  try {
    loading.value = true
    // TODO: Call API
    // Mock data
    returns.value = [
      {
        id: 1,
        orderNumber: 'ORD-20250120-0001',
        customerName: 'Nguyễn Văn A',
        customerEmail: 'nguyenvana@gmail.com',
        customerPhone: '0901234567',
        productName: 'Nike Air Max 270',
        productImage: '/placeholder.png',
        variant: 'Size 42 - Đen',
        quantity: 1,
        unitPrice: 2990000,
        reason: 'defective',
        note: 'Giày bị lỗi đế',
        refundAmount: 2990000,
        status: 'pending',
        createdAt: '2025-01-20T10:30:00',
        images: ['/placeholder.png']
      }
    ]
    updateStats()
  } catch (error) {
    console.error('Lỗi tải dữ liệu:', error)
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.pendingReturns = returns.value.filter(r => r.status === 'pending').length
  stats.inTransit = returns.value.filter(r => r.status === 'in_transit').length
  stats.completed = returns.value.filter(r => r.status === 'completed').length
  stats.rejected = returns.value.filter(r => r.status === 'rejected').length
}

const handleSearch = () => {
  setTimeout(() => fetchReturns(), 300)
}

const resetFilters = () => {
  filters.search = ''
  filters.status = ''
  filters.reason = ''
  fetchReturns()
}

const viewReturnDetail = (item) => {
  selectedReturn.value = item
  showDetailDialog.value = true
}

const approveReturn = async (item) => {
  if (!confirm('Bạn có chắc muốn duyệt yêu cầu này?')) return
  // TODO: Call API
  alert('Đã duyệt yêu cầu!')
  fetchReturns()
}

const rejectReturn = async (item) => {
  const reason = prompt('Lý do từ chối:')
  if (!reason) return
  // TODO: Call API
  alert('Đã từ chối yêu cầu!')
  fetchReturns()
}

const getReasonText = (reason) => {
  const reasons = {
    defective: 'Lỗi sản phẩm',
    wrong_item: 'Giao sai hàng',
    size_issue: 'Không vừa size',
    changed_mind: 'Đổi ý',
    other: 'Khác'
  }
  return reasons[reason] || reason
}

const getStatusClass = (status) => {
  const classes = {
    pending: 'status-pending',
    approved: 'status-processing',
    in_transit: 'status-shipped',
    received: 'status-processing',
    completed: 'status-completed',
    rejected: 'status-cancelled'
  }
  return classes[status]
}

const getStatusText = (status) => {
  const statuses = {
    pending: 'Chờ duyệt',
    approved: 'Đã duyệt',
    in_transit: 'Đang vận chuyển',
    received: 'Đã nhận hàng',
    completed: 'Hoàn thành',
    rejected: 'Từ chối'
  }
  return statuses[status] || status
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const formatDate = (dateString) => {
  return new Intl.DateTimeFormat('vi-VN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(dateString))
}

// Lifecycle
onMounted(() => {
  fetchReturns()
})
</script>

<style scoped>
/* ===== ADMIN RETURNS PAGE - UNIFIED DARK THEME ===== */

/* Page Layout */
.admin-returns {
  padding: var(--space-8);
  max-width: 1600px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* Page Header */
.page-header {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-6);
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.page-title .material-icons {
  font-size: 2rem;
  color: var(--accent-primary);
}

.page-subtitle {
  color: var(--text-secondary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-base);
}

.page-subtitle .material-icons {
  font-size: 1.125rem;
  color: var(--info-text);
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stats-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stats-card.warning::before {
  background: var(--gradient-warning);
}

.stats-card.success::before {
  background: var(--gradient-success);
}

.stats-card.danger::before {
  background: var(--gradient-danger);
}

.stats-card.info::before {
  background: var(--gradient-info);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--border-hover);
}

.stats-card:hover::before {
  opacity: 1;
}

.stats-header {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.stats-icon.warning {
  background: var(--gradient-warning);
  color: white;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.3);
}

.stats-icon.success {
  background: var(--gradient-success);
  color: white;
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3);
}

.stats-icon.danger {
  background: var(--gradient-danger);
  color: white;
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.3);
}

.stats-icon.info {
  background: var(--gradient-info);
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.stats-card:hover .stats-icon {
  transform: scale(1.1) rotate(5deg);
}

.stats-icon .material-icons {
  font-size: 28px;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: var(--space-2);
}

.stats-label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Filters Section */
.filters-section {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.filter-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: var(--space-4);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-group label {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  color: var(--text-quaternary);
  font-size: 1.25rem;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-4) var(--space-3) var(--space-11);
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-base);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.search-input:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.8);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.search-input::placeholder {
  color: var(--text-quaternary);
}

.form-control {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-base);
  transition: all 0.3s ease;
  cursor: pointer;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.form-control:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.8);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  border-radius: var(--radius-lg);
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

.btn-secondary {
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-primary);
  border: 1px solid var(--border-primary);
}

.btn-secondary:hover {
  background: rgba(15, 23, 42, 0.9);
  border-color: var(--accent-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.btn .material-icons {
  font-size: 1.125rem;
}

/* Table Container */
.table-container {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* Loading State */
.loading-container {
  padding: var(--space-16);
  text-align: center;
  color: var(--text-secondary);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-container p {
  font-size: var(--text-base);
  color: var(--text-tertiary);
}

/* Empty State */
.empty-state {
  padding: var(--space-16);
  text-align: center;
  color: var(--text-tertiary);
}

.empty-state .material-icons {
  font-size: 4rem;
  margin-bottom: var(--space-4);
  opacity: 0.5;
  color: var(--text-quaternary);
}

.empty-state h3 {
  color: var(--text-primary);
  margin-bottom: var(--space-2);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
}

.empty-state p {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* Table */
.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table thead {
  background: var(--table-header-bg);
}

.admin-table th {
  padding: var(--space-4) var(--space-6);
  text-align: left;
  font-weight: var(--font-semibold);
  color: white;
  font-size: var(--text-sm);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  white-space: nowrap;
}

.admin-table tbody tr {
  border-bottom: 1px solid var(--border-primary);
  transition: all 0.2s ease;
}

.admin-table tbody tr:hover {
  background: var(--gradient-purple-soft);
  transform: scale(1.002);
}

.admin-table td {
  padding: var(--space-4) var(--space-6);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* Status Badges */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  white-space: nowrap;
}

.status-pending {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.status-processing {
  background: var(--info-bg);
  color: var(--info-text);
  border: 1px solid var(--info-border);
}

.status-shipped {
  background: rgba(59, 130, 246, 0.15);
  color: #93c5fd;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.status-completed {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.status-cancelled {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: var(--space-2);
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.btn-icon:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--accent-primary);
  color: var(--accent-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.btn-icon.danger {
  border-color: var(--error-border);
}

.btn-icon.danger:hover {
  background: var(--error-bg);
  border-color: var(--error-solid);
  color: var(--error-text);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.btn-icon .material-icons {
  font-size: 1.125rem;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-modal);
  padding: var(--space-4);
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-primary);
  max-height: 90vh;
  overflow-y: auto;
  width: 100%;
  max-width: 600px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal.modal-lg {
  max-width: 900px;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: var(--bg-card);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  z-index: 1;
}

.modal-header h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-lg);
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-text);
  transform: rotate(90deg);
}

.modal-close .material-icons {
  font-size: 1.5rem;
}

.modal-body {
  padding: var(--space-6);
  max-height: calc(90vh - 200px);
  overflow-y: auto;
}

.modal-footer {
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  position: sticky;
  bottom: 0;
  background: var(--bg-card);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* Customer Cell */
.customer-cell {
  min-width: 180px;
}

.customer-cell strong {
  display: block;
  margin-bottom: var(--space-1);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.customer-cell p {
  margin: 0;
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

/* Product Cell */
.product-cell {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.product-thumb {
  width: 50px;
  height: 50px;
  border-radius: var(--radius-md);
  object-fit: cover;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-primary);
  transition: var(--transition-normal);
}

.product-thumb:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-md);
}

.product-cell strong {
  display: block;
  margin-bottom: var(--space-1);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.product-cell p {
  margin: var(--space-1) 0;
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

/* Reason Badge */
.reason-badge {
  display: inline-block;
  padding: var(--space-1) var(--space-3);
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  transition: var(--transition-fast);
}

.reason-badge:hover {
  background: var(--warning-border);
  transform: translateY(-1px);
}

.reason-note {
  margin: var(--space-2) 0 0 0;
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-style: italic;
  line-height: var(--leading-relaxed);
}

/* Amount */
.amount {
  font-size: var(--text-xl);
  color: var(--error-text);
  font-weight: var(--font-bold);
}

/* Return Detail Modal */
.return-detail {
  padding: var(--space-4);
}

.detail-section {
  margin-bottom: var(--space-6);
  padding-bottom: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
  animation: fadeInUp 0.4s ease-out;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 var(--space-4) 0;
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.detail-section h4::before {
  content: '';
  width: 4px;
  height: 20px;
  background: var(--gradient-primary);
  border-radius: var(--radius-sm);
}

.detail-section p {
  margin: var(--space-2) 0;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
}

.detail-section p strong {
  color: var(--text-primary);
  font-weight: var(--font-medium);
  display: inline-block;
  min-width: 120px;
}

/* Return Images */
.return-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: var(--space-4);
  margin-top: var(--space-3);
}

.return-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  border: 2px solid var(--border-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.return-image:hover {
  border-color: var(--accent-primary);
  box-shadow: var(--shadow-glow-purple);
  transform: scale(1.05);
}

/* Animations */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-row {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .admin-returns {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-6);
  }

  .page-title {
    font-size: var(--text-2xl);
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .filters-section {
    padding: var(--space-4);
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .customer-cell {
    min-width: auto;
  }

  .product-cell {
    flex-direction: column;
    align-items: flex-start;
  }

  .return-images {
    grid-template-columns: repeat(2, 1fr);
  }

  .detail-section p strong {
    min-width: auto;
    display: block;
    margin-bottom: var(--space-1);
  }

  .admin-table {
    font-size: var(--text-xs);
  }

  .admin-table th,
  .admin-table td {
    padding: var(--space-3);
  }

  .modal {
    max-width: 100%;
    min-height: 100vh;
    border-radius: 0;
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: var(--space-4);
  }
}
</style>


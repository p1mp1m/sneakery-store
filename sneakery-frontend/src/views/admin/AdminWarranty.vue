<template>
  <div class="admin-page admin-warranty">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">
            <i class="material-icons">verified_user</i>
            Quản lý Bảo Hành
          </h1>
          <p class="page-subtitle">
            <i class="material-icons">info</i>
            Quản lý yêu cầu bảo hành và sửa chữa sản phẩm
          </p>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stats-card warning">
        <div class="stats-header">
          <div class="stats-icon warning">
            <i class="material-icons">schedule</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.pending }}</div>
            <div class="stats-label">Chờ xử lý</div>
          </div>
        </div>
      </div>
      <div class="stats-card info">
        <div class="stats-header">
          <div class="stats-icon info">
            <i class="material-icons">build</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.inProgress }}</div>
            <div class="stats-label">Đang sửa chữa</div>
          </div>
        </div>
      </div>
      <div class="stats-card success">
        <div class="stats-header">
          <div class="stats-icon success">
            <i class="material-icons">done_all</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.completed }}</div>
            <div class="stats-label">Hoàn thành</div>
          </div>
        </div>
      </div>
      <div class="stats-card danger">
        <div class="stats-header">
          <div class="stats-icon danger">
            <i class="material-icons">highlight_off</i>
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
              placeholder="Tìm theo mã, khách hàng, sản phẩm..."
              @input="handleSearch"
            />
          </div>
        </div>
        <div class="filter-group">
          <label>Trạng thái</label>
          <select v-model="filters.status" @change="fetchWarranties" class="form-control">
            <option value="">Tất cả</option>
            <option value="pending">Chờ xử lý</option>
            <option value="approved">Đã chấp nhận</option>
            <option value="in_progress">Đang sửa chữa</option>
            <option value="completed">Hoàn thành</option>
            <option value="rejected">Từ chối</option>
          </select>
        </div>
        <div class="filter-group">
          <label>Loại bảo hành</label>
          <select v-model="filters.type" @change="fetchWarranties" class="form-control">
            <option value="">Tất cả</option>
            <option value="repair">Sửa chữa</option>
            <option value="replace">Đổi mới</option>
            <option value="refund">Hoàn tiền</option>
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

      <div v-else-if="warranties.length === 0" class="empty-state">
        <i class="material-icons">verified_user</i>
        <h3>Chưa có yêu cầu bảo hành</h3>
        <p>Danh sách yêu cầu bảo hành sẽ hiển thị ở đây</p>
      </div>

      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>Mã BH</th>
            <th>Khách hàng</th>
            <th>Sản phẩm</th>
            <th>Vấn đề</th>
            <th>Loại BH</th>
            <th>Ngày yêu cầu</th>
            <th>Thời hạn</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in warranties" :key="item.id">
            <td>
              <strong class="warranty-code">#WTY{{ item.id.toString().padStart(6, '0') }}</strong>
            </td>
            <td>
              <div class="customer-cell">
                <strong>{{ item.customerName }}</strong>
                <p>{{ item.customerEmail }}</p>
                <p>{{ item.customerPhone }}</p>
              </div>
            </td>
            <td>
              <div class="product-cell">
                <img :src="item.productImage" :alt="item.productName" class="product-thumb" />
                <div>
                  <strong>{{ item.productName }}</strong>
                  <p>{{ item.variant }}</p>
                  <p>Mua: {{ formatDate(item.purchaseDate) }}</p>
                </div>
              </div>
            </td>
            <td>
              <p class="issue-text">{{ item.issue }}</p>
            </td>
            <td>
              <span class="warranty-type-badge" :class="getTypeBadgeClass(item.warrantyType)">
                {{ getWarrantyTypeText(item.warrantyType) }}
              </span>
            </td>
            <td>{{ formatDate(item.createdAt) }}</td>
            <td>
              <div class="warranty-period" :class="{ 'expired': isWarrantyExpired(item) }">
                <i class="material-icons">schedule</i>
                <span v-if="isWarrantyExpired(item)" class="text-danger">Hết hạn</span>
                <span v-else>{{ calculateDaysLeft(item) }} ngày</span>
              </div>
            </td>
            <td>
              <span class="status-badge" :class="getStatusClass(item.status)">
                {{ getStatusText(item.status) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="viewWarrantyDetail(item)" class="btn-icon" title="Xem chi tiết">
                  <i class="material-icons">visibility</i>
                </button>
                <button 
                  v-if="item.status === 'pending'" 
                  @click="approveWarranty(item)" 
                  class="btn-icon" 
                  title="Chấp nhận"
                >
                  <i class="material-icons">check_circle</i>
                </button>
                <button 
                  v-if="item.status === 'approved'" 
                  @click="updateStatus(item, 'in_progress')" 
                  class="btn-icon" 
                  title="Bắt đầu sửa"
                >
                  <i class="material-icons">build</i>
                </button>
                <button 
                  v-if="item.status === 'in_progress'" 
                  @click="updateStatus(item, 'completed')" 
                  class="btn-icon" 
                  title="Hoàn thành"
                >
                  <i class="material-icons">done_all</i>
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
          <h3>Chi tiết yêu cầu bảo hành #WTY{{ selectedWarranty?.id.toString().padStart(6, '0') }}</h3>
          <button @click="showDetailDialog = false" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedWarranty" class="warranty-detail">
            <div class="detail-section">
              <h4>Thông tin khách hàng</h4>
              <p><strong>Họ tên:</strong> {{ selectedWarranty.customerName }}</p>
              <p><strong>Email:</strong> {{ selectedWarranty.customerEmail }}</p>
              <p><strong>SĐT:</strong> {{ selectedWarranty.customerPhone }}</p>
            </div>
            <div class="detail-section">
              <h4>Thông tin sản phẩm</h4>
              <p><strong>Sản phẩm:</strong> {{ selectedWarranty.productName }}</p>
              <p><strong>Biến thể:</strong> {{ selectedWarranty.variant }}</p>
              <p><strong>Ngày mua:</strong> {{ formatDate(selectedWarranty.purchaseDate) }}</p>
              <p><strong>Thời hạn BH:</strong> {{ selectedWarranty.warrantyMonths }} tháng</p>
            </div>
            <div class="detail-section">
              <h4>Vấn đề & Giải pháp</h4>
              <p><strong>Mô tả vấn đề:</strong> {{ selectedWarranty.issue }}</p>
              <p><strong>Loại BH yêu cầu:</strong> {{ getWarrantyTypeText(selectedWarranty.warrantyType) }}</p>
            </div>
            <div v-if="selectedWarranty.images" class="detail-section">
              <h4>Hình ảnh</h4>
              <div class="warranty-images">
                <img 
                  v-for="(img, index) in selectedWarranty.images" 
                  :key="index"
                  :src="img" 
                  alt="Warranty image"
                  class="warranty-image"
                />
              </div>
            </div>
            <div class="detail-section">
              <h4>Trạng thái</h4>
              <div class="status-timeline">
                <div v-for="(log, index) in selectedWarranty.statusLogs" :key="index" class="timeline-item">
                  <div class="timeline-dot"></div>
                  <div class="timeline-content">
                    <strong>{{ getStatusText(log.status) }}</strong>
                    <p>{{ formatDate(log.timestamp) }}</p>
                    <p v-if="log.note">{{ log.note }}</p>
                  </div>
                </div>
              </div>
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
const selectedWarranty = ref(null)
const warranties = ref([])

const filters = reactive({
  search: '',
  status: '',
  type: ''
})

const stats = reactive({
  pending: 0,
  inProgress: 0,
  completed: 0,
  rejected: 0
})

// Methods
const fetchWarranties = async () => {
  try {
    loading.value = true
    // TODO: Call API
    // Mock data
    warranties.value = [
      {
        id: 1,
        customerName: 'Nguyễn Văn A',
        customerEmail: 'nguyenvana@gmail.com',
        customerPhone: '0901234567',
        productName: 'Nike Air Max 270',
        productImage: '/placeholder.png',
        variant: 'Size 42 - Đen',
        purchaseDate: '2024-11-15T00:00:00',
        warrantyMonths: 12,
        issue: 'Đế giày bị bong tróc sau 2 tháng sử dụng',
        warrantyType: 'repair',
        status: 'pending',
        createdAt: '2025-01-20T10:30:00',
        images: ['/placeholder.png'],
        statusLogs: [
          { status: 'pending', timestamp: '2025-01-20T10:30:00', note: 'Yêu cầu được tạo' }
        ]
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
  stats.pending = warranties.value.filter(w => w.status === 'pending').length
  stats.inProgress = warranties.value.filter(w => w.status === 'in_progress').length
  stats.completed = warranties.value.filter(w => w.status === 'completed').length
  stats.rejected = warranties.value.filter(w => w.status === 'rejected').length
}

const handleSearch = () => {
  setTimeout(() => fetchWarranties(), 300)
}

const resetFilters = () => {
  filters.search = ''
  filters.status = ''
  filters.type = ''
  fetchWarranties()
}

const viewWarrantyDetail = (item) => {
  selectedWarranty.value = item
  showDetailDialog.value = true
}

const approveWarranty = async (item) => {
  if (!confirm('Bạn có chắc muốn chấp nhận yêu cầu bảo hành này?')) return
  // TODO: Call API
  alert('Đã chấp nhận yêu cầu!')
  fetchWarranties()
}

const updateStatus = async (item, newStatus) => {
  const messages = {
    in_progress: 'Bắt đầu sửa chữa?',
    completed: 'Đánh dấu là hoàn thành?'
  }
  if (!confirm(messages[newStatus])) return
  // TODO: Call API
  alert('Đã cập nhật trạng thái!')
  fetchWarranties()
}

const isWarrantyExpired = (item) => {
  const purchaseDate = new Date(item.purchaseDate)
  const expiryDate = new Date(purchaseDate)
  expiryDate.setMonth(expiryDate.getMonth() + item.warrantyMonths)
  return expiryDate < new Date()
}

const calculateDaysLeft = (item) => {
  const purchaseDate = new Date(item.purchaseDate)
  const expiryDate = new Date(purchaseDate)
  expiryDate.setMonth(expiryDate.getMonth() + item.warrantyMonths)
  const now = new Date()
  const daysLeft = Math.ceil((expiryDate - now) / (1000 * 60 * 60 * 24))
  return daysLeft > 0 ? daysLeft : 0
}

const getWarrantyTypeText = (type) => {
  const types = {
    repair: 'Sửa chữa',
    replace: 'Đổi mới',
    refund: 'Hoàn tiền'
  }
  return types[type] || type
}

const getTypeBadgeClass = (type) => {
  const classes = {
    repair: 'badge-info',
    replace: 'badge-warning',
    refund: 'badge-success'
  }
  return classes[type]
}

const getStatusClass = (status) => {
  const classes = {
    pending: 'status-pending',
    approved: 'status-processing',
    in_progress: 'status-shipped',
    completed: 'status-completed',
    rejected: 'status-cancelled'
  }
  return classes[status]
}

const getStatusText = (status) => {
  const statuses = {
    pending: 'Chờ xử lý',
    approved: 'Đã chấp nhận',
    in_progress: 'Đang sửa',
    completed: 'Hoàn thành',
    rejected: 'Từ chối'
  }
  return statuses[status] || status
}

const formatDate = (dateString) => {
  return new Intl.DateTimeFormat('vi-VN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit'
  }).format(new Date(dateString))
}

// Lifecycle
onMounted(() => {
  fetchWarranties()
})
</script>

<style scoped>
/* ===== ADMIN WARRANTY PAGE - UNIFIED DARK THEME ===== */

/* Page Layout */
.admin-warranty {
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

/* Table headers use global admin-tables.css styles */

.admin-table tbody tr {
  border-bottom: 1px solid var(--border-primary);
  transition: all 0.2s ease;
}

.admin-table tbody tr:hover {
  background: var(--gradient-purple-soft);
  transform: scale(1.002);
}

/* Table cells use global admin-tables.css styles */

/* Warranty Code */
.warranty-code {
  font-family: var(--font-mono);
  color: var(--accent-primary);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  background: var(--gradient-purple-soft);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
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
  margin: var(--space-1) 0;
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

/* Issue Text */
.issue-text {
  max-width: 250px;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
}

/* Warranty Type Badge */
.warranty-type-badge {
  display: inline-block;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  transition: var(--transition-fast);
}

.warranty-type-badge.badge-info {
  background: var(--info-bg);
  color: var(--info-text);
  border: 1px solid var(--info-border);
}

.warranty-type-badge.badge-warning {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.warranty-type-badge.badge-success {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

/* Warranty Period */
.warranty-period {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--success-text);
  font-weight: var(--font-medium);
}

.warranty-period.expired {
  color: var(--error-text);
}

.warranty-period .material-icons {
  font-size: 1.125rem;
}

.text-danger {
  color: var(--error-text);
  font-weight: var(--font-semibold);
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

/* Action buttons use global admin-tables.css styles */

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

/* Warranty Detail */
.warranty-detail {
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
  min-width: 140px;
}

/* Warranty Images */
.warranty-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: var(--space-4);
  margin-top: var(--space-3);
}

.warranty-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  border: 2px solid var(--border-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.warranty-image:hover {
  border-color: var(--accent-primary);
  box-shadow: var(--shadow-glow-purple);
  transform: scale(1.05);
}

/* Status Timeline */
.status-timeline {
  position: relative;
  padding-left: var(--space-8);
}

.status-timeline::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: var(--gradient-primary);
  opacity: 0.3;
}

.timeline-item {
  position: relative;
  padding-bottom: var(--space-6);
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: calc(-1 * var(--space-8));
  top: 4px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: var(--accent-primary);
  border: 3px solid var(--bg-card);
  box-shadow: 0 0 0 2px var(--border-primary);
  z-index: 1;
  transition: all 0.3s ease;
}

.timeline-item:last-child .timeline-dot {
  background: var(--success-solid);
  box-shadow: 0 0 0 2px var(--success-border), 0 0 12px var(--success-solid);
}

.timeline-item:hover .timeline-dot {
  transform: scale(1.3);
  box-shadow: 0 0 0 2px var(--accent-primary), 0 0 16px var(--accent-primary);
}

.timeline-content {
  background: rgba(15, 23, 42, 0.4);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-subtle);
  transition: all 0.2s ease;
}

.timeline-content:hover {
  background: rgba(15, 23, 42, 0.6);
  border-color: var(--border-primary);
}

.timeline-content strong {
  display: block;
  margin-bottom: var(--space-1);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.timeline-content p {
  margin: var(--space-1) 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
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
  .admin-warranty {
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

  .warranty-images {
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

  .status-timeline {
    padding-left: var(--space-6);
  }

  .timeline-dot {
    left: calc(-1 * var(--space-6));
  }
}
</style>


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
/* ═══════════════════════════════════════════════════════════════════════
   ADMIN WARRANTY - WARRANTY-SPECIFIC STYLES ONLY
   All layout, stats, filters, tables, buttons, modals use Design System v2.0 global classes
   ═══════════════════════════════════════════════════════════════════════ */

/* Page header, stats, filters, loading, empty states, tables, status badges, modals, buttons, responsive all use global classes */

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

/* Animations and responsive styles use global Design System v2.0 classes */
</style>


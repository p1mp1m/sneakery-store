<template>
  <div class="admin-returns">
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
.customer-cell {
  min-width: 180px;
}

.customer-cell strong {
  display: block;
  margin-bottom: 0.25rem;
}

.customer-cell p {
  margin: 0;
  font-size: 0.75rem;
  color: #64748b;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.product-thumb {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
  background: #f7fafc;
}

.product-cell strong {
  display: block;
  margin-bottom: 0.25rem;
}

.product-cell p {
  margin: 0.125rem 0;
  font-size: 0.75rem;
  color: #64748b;
}

.reason-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  background: #fef3c7;
  color: #92400e;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
}

.reason-note {
  margin: 0.5rem 0 0 0;
  font-size: 0.75rem;
  color: #64748b;
  font-style: italic;
}

.amount {
  font-size: 1.125rem;
  color: #ef4444;
}

.return-detail {
  padding: 1rem;
}

.detail-section {
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-section h4 {
  margin: 0 0 1rem 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.detail-section p {
  margin: 0.5rem 0;
  font-size: 0.875rem;
}

.return-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
}

.return-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}
</style>


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
      <StatsCard
        icon="pending_actions"
        :value="stats.pendingReturns"
        label="Chờ duyệt"
        variant="warning"
      />
      <StatsCard
        icon="local_shipping"
        :value="stats.inTransit"
        label="Đang vận chuyển"
        variant="info"
      />
      <StatsCard
        icon="check_circle"
        :value="stats.completed"
        label="Đã hoàn thành"
        variant="success"
      />
      <StatsCard
        icon="cancel"
        :value="stats.rejected"
        label="Từ chối"
        variant="danger"
      />
    </div>

    <!-- Filters -->
    <FilterBar
      v-model:search="filters.search"
      search-placeholder="Tìm theo mã đơn, khách hàng..."
      @search="handleSearch"
      @reset="resetFilters"
    >
      <template #filters>
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">check_circle</span>
            Trạng thái
          </label>
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
          <label class="filter-label">
            <span class="material-icons">info</span>
            Lý do
          </label>
          <select v-model="filters.reason" @change="fetchReturns" class="form-control">
            <option value="">Tất cả</option>
            <option value="defective">Lỗi sản phẩm</option>
            <option value="wrong_item">Giao sai hàng</option>
            <option value="size_issue">Không vừa size</option>
            <option value="changed_mind">Đổi ý</option>
            <option value="other">Khác</option>
          </select>
        </div>
      </template>
    </FilterBar>

    <!-- Table -->
    <div class="table-container">
      <LoadingState v-if="loading" />

      <EmptyState
        v-else-if="returns.length === 0"
        icon="assignment_return"
        title="Chưa có yêu cầu trả hàng"
        description="Danh sách yêu cầu trả hàng sẽ hiển thị ở đây"
      />

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
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import StatsCard from '@/assets/components/admin/StatsCard.vue'
import FilterBar from '@/assets/components/admin/FilterBar.vue'
import LoadingState from '@/assets/components/admin/LoadingSkeleton.vue'
import EmptyState from '@/assets/components/admin/EmptyState.vue'

const adminStore = useAdminStore()

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
    
    // Prepare filters for API
    const apiFilters = {
      search: filters.search || undefined,
      status: filters.status || undefined,
      reason: filters.reason || undefined
    }
    
    const result = await adminStore.fetchReturns(0, 50, apiFilters)
    returns.value = result.content || []
    updateStats()
  } catch (error) {
    console.error('Lỗi tải dữ liệu:', error)
    ElMessage.error('Lỗi khi tải danh sách yêu cầu trả hàng')
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
  
  try {
    await adminStore.approveReturn(item.id)
    ElMessage.success('Đã duyệt yêu cầu trả hàng!')
    fetchReturns()
  } catch (error) {
    console.error('Lỗi khi duyệt yêu cầu:', error)
    ElMessage.error('Lỗi khi duyệt yêu cầu trả hàng')
  }
}

const rejectReturn = async (item) => {
  const reason = prompt('Lý do từ chối:')
  if (!reason) return
  
  try {
    await adminStore.rejectReturn(item.id, reason)
    ElMessage.success('Đã từ chối yêu cầu trả hàng!')
    fetchReturns()
  } catch (error) {
    console.error('Lỗi khi từ chối yêu cầu:', error)
    ElMessage.error('Lỗi khi từ chối yêu cầu trả hàng')
  }
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
/* ═══════════════════════════════════════════════════════════════════════
   ADMIN RETURNS - RETURN-SPECIFIC STYLES ONLY
   All layout, stats, filters, tables, buttons, modals use Design System v2.0 global classes
   ═══════════════════════════════════════════════════════════════════════ */

/* Page header, stats, filters, loading, empty states, tables, status badges, modals, buttons, responsive all use global classes */

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

/* Animations and responsive styles use global Design System v2.0 classes */
</style>


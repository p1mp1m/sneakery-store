<template>
  <div class="admin-page admin-discounts">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">
            <i class="material-icons">percent</i>
            Quản lý Mã Giảm Giá
          </h1>
          <p class="page-subtitle">
            <i class="material-icons">info</i>
            Tạo và quản lý các chương trình giảm giá, khuyến mãi
          </p>
        </div>
        <div class="header-actions">
          <button @click="showCreateDialog = true" class="btn btn-primary">
            <i class="material-icons">add</i>
            Tạo mã giảm giá
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stats-card success">
        <div class="stats-header">
          <div class="stats-icon success">
            <i class="material-icons">local_activity</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.totalCoupons }}</div>
            <div class="stats-label">Tổng mã</div>
          </div>
        </div>
      </div>
      <div class="stats-card info">
        <div class="stats-header">
          <div class="stats-icon info">
            <i class="material-icons">check_circle</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.activeCoupons }}</div>
            <div class="stats-label">Đang hoạt động</div>
          </div>
        </div>
      </div>
      <div class="stats-card warning">
        <div class="stats-header">
          <div class="stats-icon warning">
            <i class="material-icons">schedule</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.expiringSoon }}</div>
            <div class="stats-label">Sắp hết hạn</div>
          </div>
        </div>
      </div>
      <div class="stats-card danger">
        <div class="stats-header">
          <div class="stats-icon danger">
            <i class="material-icons">block</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.expiredCoupons }}</div>
            <div class="stats-label">Đã hết hạn</div>
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
              placeholder="Tìm theo mã, mô tả..."
              @input="handleSearch"
            />
          </div>
        </div>
        <div class="filter-group">
          <label>Loại giảm giá</label>
          <select v-model="filters.type" @change="fetchCoupons" class="form-control">
            <option value="">Tất cả</option>
            <option value="percent">Phần trăm (%)</option>
            <option value="amount">Số tiền cố định</option>
          </select>
        </div>
        <div class="filter-group">
          <label>Trạng thái</label>
          <select v-model="filters.status" @change="fetchCoupons" class="form-control">
            <option value="">Tất cả</option>
            <option value="active">Đang hoạt động</option>
            <option value="expired">Đã hết hạn</option>
            <option value="upcoming">Chưa bắt đầu</option>
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

      <div v-else-if="coupons.length === 0" class="empty-state">
        <i class="material-icons">local_activity</i>
        <h3>Chưa có mã giảm giá</h3>
        <p>Nhấn nút "Tạo mã giảm giá" để bắt đầu</p>
      </div>

      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>Mã</th>
            <th>Loại</th>
            <th>Giá trị</th>
            <th>Điều kiện</th>
            <th>Thời gian</th>
            <th>Lượt dùng</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="coupon in coupons" :key="coupon.id">
            <td>
              <div class="coupon-code">
                <strong>{{ coupon.code }}</strong>
                <button @click="copyCouponCode(coupon.code)" class="btn-copy">
                  <i class="material-icons">content_copy</i>
                </button>
              </div>
              <p class="coupon-description">{{ coupon.description }}</p>
            </td>
            <td>
              <span class="badge" :class="coupon.discountType === 'percent' ? 'badge-info' : 'badge-success'">
                <i class="material-icons">{{ coupon.discountType === 'percent' ? 'percent' : 'attach_money' }}</i>
                {{ coupon.discountType === 'percent' ? 'Phần trăm' : 'Số tiền' }}
              </span>
            </td>
            <td>
              <strong class="discount-value">
                {{ coupon.discountType === 'percent' ? `${coupon.value}%` : formatPrice(coupon.value) }}
              </strong>
              <div v-if="coupon.maxDiscountAmount" class="max-discount">
                Tối đa: {{ formatPrice(coupon.maxDiscountAmount) }}
              </div>
            </td>
            <td>
              <div v-if="coupon.minOrderAmount" class="condition">
                <i class="material-icons">shopping_cart</i>
                Đơn tối thiểu: {{ formatPrice(coupon.minOrderAmount) }}
              </div>
              <div v-else class="condition">
                <i class="material-icons">check_circle</i>
                Không giới hạn
              </div>
            </td>
            <td>
              <div class="time-range">
                <div class="date-item">
                  <i class="material-icons">event</i>
                  {{ formatDate(coupon.startAt) }}
                </div>
                <div class="date-item">
                  <i class="material-icons">event</i>
                  {{ formatDate(coupon.endAt) }}
                </div>
              </div>
            </td>
            <td>
              <div class="usage-stats">
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: getUsagePercentage(coupon) + '%' }"
                  ></div>
                </div>
                <span class="usage-text">
                  {{ coupon.usesCount }} / {{ coupon.maxUses || '∞' }}
                </span>
              </div>
            </td>
            <td>
              <span class="status-badge" :class="getCouponStatusClass(coupon)">
                {{ getCouponStatusText(coupon) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="editCoupon(coupon)" class="btn-icon" title="Sửa">
                  <i class="material-icons">edit</i>
                </button>
                <button @click="toggleCouponStatus(coupon)" class="btn-icon" :title="coupon.isActive ? 'Vô hiệu hóa' : 'Kích hoạt'">
                  <i class="material-icons">{{ coupon.isActive ? 'block' : 'check_circle' }}</i>
                </button>
                <button @click="deleteCoupon(coupon)" class="btn-icon danger" title="Xóa">
                  <i class="material-icons">delete</i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="pagination-container">
      <div class="pagination-info">
        Hiển thị {{ (currentPage) * pageSize + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalElements) }} trong tổng số {{ totalElements }}
      </div>
      <div class="pagination-controls">
        <button 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 0"
          class="pagination-btn"
        >
          <i class="material-icons">chevron_left</i>
        </button>
        <button 
          v-for="page in visiblePages" 
          :key="page"
          @click="goToPage(page - 1)"
          class="pagination-btn"
          :class="{ 'active': page - 1 === currentPage }"
        >
          {{ page }}
        </button>
        <button 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage >= totalPages - 1"
          class="pagination-btn"
        >
          <i class="material-icons">chevron_right</i>
        </button>
      </div>
    </div>

    <!-- Create/Edit Dialog -->
    <div v-if="showCreateDialog" class="modal-overlay" @click="closeDialog">
      <div class="modal modal-lg" @click.stop>
        <div class="modal-header">
          <h3>{{ editingCoupon ? 'Chỉnh sửa mã giảm giá' : 'Tạo mã giảm giá mới' }}</h3>
          <button @click="closeDialog" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveCoupon">
            <div class="form-row">
              <div class="form-group">
                <label class="required">Mã giảm giá</label>
                <input 
                  v-model="formData.code"
                  type="text" 
                  class="form-control" 
                  placeholder="VD: SUMMER2025"
                  required
                  :disabled="editingCoupon"
                />
                <small class="form-help">Chữ in hoa, không dấu, không khoảng trắng</small>
              </div>
              <div class="form-group">
                <label>Mô tả</label>
                <input 
                  v-model="formData.description"
                  type="text" 
                  class="form-control" 
                  placeholder="Mô tả ngắn gọn về mã giảm giá"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="required">Loại giảm giá</label>
                <select v-model="formData.discountType" class="form-control" required>
                  <option value="">-- Chọn loại --</option>
                  <option value="percent">Phần trăm (%)</option>
                  <option value="fixed">Số tiền cố định (VNĐ)</option>
                </select>
              </div>
              <div class="form-group">
                <label class="required">Giá trị giảm</label>
                <input 
                  v-model.number="formData.value"
                  type="number" 
                  min="0"
                  :max="formData.discountType === 'percent' ? 100 : undefined"
                  class="form-control" 
                  placeholder="VD: 10"
                  required
                />
                <small class="form-help">
                  {{ formData.discountType === 'percent' ? 'Từ 1 đến 100%' : 'Số tiền VNĐ' }}
                </small>
              </div>
            </div>

            <div v-if="formData.discountType === 'percent'" class="form-group">
              <label>Số tiền giảm tối đa (VNĐ)</label>
              <input 
                v-model.number="formData.maxDiscountAmount"
                type="number" 
                min="0"
                class="form-control" 
                placeholder="Để trống nếu không giới hạn"
              />
              <small class="form-help">Giới hạn số tiền giảm tối đa khi dùng %</small>
            </div>

            <div class="form-group">
              <label>Đơn hàng tối thiểu (VNĐ)</label>
              <input 
                v-model.number="formData.minOrderAmount"
                type="number" 
                min="0"
                class="form-control" 
                placeholder="Để trống nếu không giới hạn"
              />
              <small class="form-help">Giá trị đơn hàng tối thiểu để áp dụng mã</small>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="required">Ngày bắt đầu</label>
                <input 
                  v-model="formData.startAt"
                  type="datetime-local" 
                  class="form-control" 
                  required
                />
              </div>
              <div class="form-group">
                <label class="required">Ngày kết thúc</label>
                <input 
                  v-model="formData.endAt"
                  type="datetime-local" 
                  class="form-control" 
                  required
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Số lượt dùng tối đa</label>
                <input 
                  v-model.number="formData.maxUses"
                  type="number" 
                  min="1"
                  class="form-control" 
                  placeholder="Để trống nếu không giới hạn"
                />
                <small class="form-help">Tổng số lần mã có thể được sử dụng</small>
              </div>
              <div class="form-group">
                <label>Số lần dùng/người</label>
                <input 
                  v-model.number="formData.maxUsesPerUser"
                  type="number" 
                  min="1"
                  class="form-control" 
                  placeholder="1"
                />
                <small class="form-help">Mỗi khách hàng có thể dùng tối đa bao nhiêu lần</small>
              </div>
            </div>

            <div class="form-group">
              <label class="toggle-switch">
                <input v-model="formData.isActive" type="checkbox" />
                <span class="toggle-slider"></span>
                Kích hoạt ngay
              </label>
            </div>

            <div class="form-actions">
              <button type="button" @click="closeDialog" class="btn btn-secondary">
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :disabled="saving">
                <i class="material-icons">save</i>
                {{ saving ? 'Đang lưu...' : (editingCoupon ? 'Cập nhật' : 'Tạo mới') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const saving = ref(false)
const showCreateDialog = ref(false)
const editingCoupon = ref(null)
const coupons = ref([])
const currentPage = ref(0)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const filters = reactive({
  search: '',
  type: '',
  status: ''
})

const stats = reactive({
  totalCoupons: 0,
  activeCoupons: 0,
  expiringSoon: 0,
  expiredCoupons: 0
})

const formData = reactive({
  code: '',
  description: '',
  discountType: '',
  value: 0,
  maxDiscountAmount: null,
  minOrderAmount: null,
  startAt: '',
  endAt: '',
  maxUses: null,
  maxUsesPerUser: 1,
  isActive: true
})

// Computed
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(0, currentPage.value - 2)
  const end = Math.min(totalPages.value, start + 5)
  for (let i = start; i < end; i++) {
    pages.push(i + 1)
  }
  return pages
})

// Methods
const fetchCoupons = async () => {
  try {
    loading.value = true
    // TODO: Call API lấy danh sách coupon
    // Mock data
    coupons.value = [
      {
        id: 1,
        code: 'SUMMER2025',
        description: 'Giảm giá mùa hè',
        discountType: 'percent',
        value: 15,
        maxDiscountAmount: 100000,
        minOrderAmount: 500000,
        startAt: '2025-06-01T00:00:00',
        endAt: '2025-08-31T23:59:59',
        maxUses: 1000,
        usesCount: 234,
        maxUsesPerUser: 1,
        isActive: true
      },
      {
        id: 2,
        code: 'FREESHIP',
        description: 'Miễn phí vận chuyển',
        discountType: 'fixed',
        value: 30000,
        maxDiscountAmount: null,
        minOrderAmount: 300000,
        startAt: '2025-01-01T00:00:00',
        endAt: '2025-12-31T23:59:59',
        maxUses: null,
        usesCount: 567,
        maxUsesPerUser: 3,
        isActive: true
      }
    ]
    totalElements.value = coupons.value.length
    totalPages.value = Math.ceil(totalElements.value / pageSize.value)
    updateStats()
  } catch (error) {
    console.error('Lỗi tải coupons:', error)
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.totalCoupons = coupons.value.length
  stats.activeCoupons = coupons.value.filter(c => c.isActive && new Date(c.endAt) > new Date()).length
  stats.expiringSoon = coupons.value.filter(c => {
    const endDate = new Date(c.endAt)
    const now = new Date()
    const daysLeft = (endDate - now) / (1000 * 60 * 60 * 24)
    return daysLeft > 0 && daysLeft <= 7
  }).length
  stats.expiredCoupons = coupons.value.filter(c => new Date(c.endAt) < new Date()).length
}

const handleSearch = () => {
  // Debounce search
  setTimeout(() => {
    fetchCoupons()
  }, 300)
}

const resetFilters = () => {
  filters.search = ''
  filters.type = ''
  filters.status = ''
  fetchCoupons()
}

const editCoupon = (coupon) => {
  editingCoupon.value = coupon
  Object.assign(formData, {
    code: coupon.code,
    description: coupon.description,
    discountType: coupon.discountType,
    value: coupon.value,
    maxDiscountAmount: coupon.maxDiscountAmount,
    minOrderAmount: coupon.minOrderAmount,
    startAt: coupon.startAt ? coupon.startAt.substring(0, 16) : '',
    endAt: coupon.endAt ? coupon.endAt.substring(0, 16) : '',
    maxUses: coupon.maxUses,
    maxUsesPerUser: coupon.maxUsesPerUser,
    isActive: coupon.isActive
  })
  showCreateDialog.value = true
}

const saveCoupon = async () => {
  try {
    saving.value = true
    // TODO: Call API tạo/cập nhật coupon
    console.log('Save coupon:', formData)
    alert('Lưu thành công!')
    closeDialog()
    fetchCoupons()
  } catch (error) {
    console.error('Lỗi lưu coupon:', error)
    alert('Có lỗi xảy ra!')
  } finally {
    saving.value = false
  }
}

const toggleCouponStatus = async (coupon) => {
  try {
    // TODO: Call API
    coupon.isActive = !coupon.isActive
    alert(`Đã ${coupon.isActive ? 'kích hoạt' : 'vô hiệu hóa'} mã giảm giá!`)
  } catch (error) {
    console.error('Lỗi:', error)
  }
}

const deleteCoupon = async (coupon) => {
  if (!confirm(`Bạn có chắc muốn xóa mã "${coupon.code}"?`)) return
  
  try {
    // TODO: Call API
    console.log('Delete coupon:', coupon.id)
    alert('Đã xóa!')
    fetchCoupons()
  } catch (error) {
    console.error('Lỗi xóa coupon:', error)
  }
}

const copyCouponCode = (code) => {
  navigator.clipboard.writeText(code)
  alert(`Đã sao chép mã: ${code}`)
}

const closeDialog = () => {
  showCreateDialog.value = false
  editingCoupon.value = null
  Object.assign(formData, {
    code: '',
    description: '',
    discountType: '',
    value: 0,
    maxDiscountAmount: null,
    minOrderAmount: null,
    startAt: '',
    endAt: '',
    maxUses: null,
    maxUsesPerUser: 1,
    isActive: true
  })
}

const goToPage = (page) => {
  currentPage.value = page
  fetchCoupons()
}

const getCouponStatusClass = (coupon) => {
  const now = new Date()
  const start = new Date(coupon.startAt)
  const end = new Date(coupon.endAt)
  
  if (end < now) return 'status-expired'
  if (start > now) return 'status-upcoming'
  if (!coupon.isActive) return 'status-inactive'
  if (coupon.maxUses && coupon.usesCount >= coupon.maxUses) return 'status-full'
  return 'status-active'
}

const getCouponStatusText = (coupon) => {
  const now = new Date()
  const start = new Date(coupon.startAt)
  const end = new Date(coupon.endAt)
  
  if (end < now) return 'Đã hết hạn'
  if (start > now) return 'Chưa bắt đầu'
  if (!coupon.isActive) return 'Vô hiệu hóa'
  if (coupon.maxUses && coupon.usesCount >= coupon.maxUses) return 'Đã hết lượt'
  return 'Đang hoạt động'
}

const getUsagePercentage = (coupon) => {
  if (!coupon.maxUses) return 0
  return Math.min((coupon.usesCount / coupon.maxUses) * 100, 100)
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('vi-VN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

// Lifecycle
onMounted(() => {
  fetchCoupons()
})
</script>

<style scoped>
/* Reuse styles from admin-panel.css */
/* Additional specific styles */
.coupon-code {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.coupon-code strong {
  font-family: 'Courier New', monospace;
  font-size: 1rem;
  color: #667eea;
}

.btn-copy {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.btn-copy:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.btn-copy .material-icons {
  font-size: 1rem;
}

.coupon-description {
  margin: 0.25rem 0 0 0;
  font-size: 0.75rem;
  color: #64748b;
}

.discount-value {
  font-size: 1.125rem;
  color: #10b981;
}

.max-discount {
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: #64748b;
}

.condition {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.875rem;
  color: #64748b;
}

.condition .material-icons {
  font-size: 1rem;
  color: #10b981;
}

.time-range {
  font-size: 0.75rem;
}

.date-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  margin-bottom: 0.25rem;
  color: #64748b;
}

.date-item .material-icons {
  font-size: 0.875rem;
}

.usage-stats {
  min-width: 120px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #10b981 0%, #059669 100%);
  transition: width 0.3s ease;
}

.usage-text {
  font-size: 0.75rem;
  color: #64748b;
}

.status-expired {
  background: #fee2e2;
  color: #991b1b;
}

.status-upcoming {
  background: #dbeafe;
  color: #1e40af;
}

.status-inactive {
  background: #f1f5f9;
  color: #64748b;
}

.status-full {
  background: #fef3c7;
  color: #92400e;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-help {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: #64748b;
}
</style>


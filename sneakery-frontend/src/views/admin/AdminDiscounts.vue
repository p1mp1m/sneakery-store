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
import { ElMessage } from 'element-plus'
import adminService from '@/services/adminService'

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
    const response = await adminService.getCoupons(currentPage.value, pageSize.value, filters)
    
    if (response && response.content) {
      coupons.value = response.content
      totalElements.value = response.totalElements || 0
      totalPages.value = response.totalPages || 0
      updateStats()
    } else {
      coupons.value = []
      totalElements.value = 0
      totalPages.value = 0
    }
  } catch (error) {
    console.error('Lỗi tải coupons:', error)
    
    let errorMessage = 'Không thể tải danh sách mã giảm giá.'
    
    if (error.response) {
      const status = error.response.status
      if (status === 500) {
        errorMessage = '⚠️ Lỗi server 500: Backend có thể chưa khởi động hoặc database chưa có table Coupons. Vui lòng kiểm tra backend!'
      } else if (status === 404) {
        errorMessage = 'API endpoint không tồn tại. Vui lòng kiểm tra backend!'
      } else if (status === 403) {
        errorMessage = 'Không có quyền truy cập. Vui lòng đăng nhập lại!'
      }
    } else if (error.request) {
      errorMessage = '⚠️ Không thể kết nối đến backend server. Vui lòng kiểm tra:\n1. Backend đã chạy chưa?\n2. URL API có đúng không?'
    }
    
    ElMessage.error({
      message: errorMessage,
      duration: 5000,
      dangerouslyUseHTMLString: false
    })
    
    // Set empty array để tránh undefined errors
    coupons.value = []
    totalElements.value = 0
    totalPages.value = 0
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
    
    // Chuẩn bị data để gửi lên server
    const couponData = {
      code: formData.code.toUpperCase().trim(),
      description: formData.description?.trim() || null,
      discountType: formData.discountType,
      value: formData.value,
      maxDiscountAmount: formData.maxDiscountAmount || null,
      minOrderAmount: formData.minOrderAmount || null,
      startAt: formData.startAt,
      endAt: formData.endAt,
      maxUses: formData.maxUses || null,
      maxUsesPerUser: formData.maxUsesPerUser || 1,
      isActive: formData.isActive
    }
    
    if (editingCoupon.value) {
      await adminService.updateCoupon(editingCoupon.value.id, couponData)
      ElMessage.success({
        message: `Đã cập nhật mã giảm giá "${couponData.code}" thành công!`,
        duration: 3000
      })
    } else {
      await adminService.createCoupon(couponData)
      ElMessage.success({
        message: `Đã tạo mã giảm giá "${couponData.code}" thành công!`,
        duration: 3000
      })
    }
    
    closeDialog()
    fetchCoupons()
  } catch (error) {
    console.error('Lỗi lưu coupon:', error)
    
    let errorMessage = 'Có lỗi xảy ra! Vui lòng thử lại.'
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      
      if (status === 400) {
        if (data.validationErrors) {
          const firstError = Object.values(data.validationErrors)[0]
          if (firstError && firstError.length > 0) {
            errorMessage = firstError[0]
          }
        } else if (data.message) {
          errorMessage = data.message
        }
      } else if (status === 409) {
        errorMessage = 'Mã giảm giá đã tồn tại. Vui lòng chọn mã khác.'
      } else if (status === 500) {
        errorMessage = 'Lỗi server. Vui lòng liên hệ quản trị viên.'
      }
    } else if (error.request) {
      errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng!'
    }
    
    ElMessage.error({
      message: errorMessage,
      duration: 5000
    })
  } finally {
    saving.value = false
  }
}

const toggleCouponStatus = async (coupon) => {
  try {
    await adminService.toggleCouponStatus(coupon.id)
    coupon.isActive = !coupon.isActive
    ElMessage.success({
      message: `Đã ${coupon.isActive ? 'kích hoạt' : 'vô hiệu hóa'} mã giảm giá "${coupon.code}" thành công!`,
      duration: 3000
    })
  } catch (error) {
    console.error('Lỗi toggle status:', error)
    ElMessage.error({
      message: 'Không thể thay đổi trạng thái. Vui lòng thử lại!',
      duration: 3000
    })
  }
}

const deleteCoupon = async (coupon) => {
  if (!confirm(`Bạn có chắc muốn xóa mã "${coupon.code}"? Hành động này không thể hoàn tác!`)) return
  
  try {
    await adminService.deleteCoupon(coupon.id)
    ElMessage.success({
      message: `Đã xóa mã giảm giá "${coupon.code}" thành công!`,
      duration: 3000
    })
    fetchCoupons()
  } catch (error) {
    console.error('Lỗi xóa coupon:', error)
    ElMessage.error({
      message: 'Không thể xóa mã giảm giá. Vui lòng thử lại!',
      duration: 3000
    })
  }
}

const copyCouponCode = async (code) => {
  try {
    await navigator.clipboard.writeText(code)
    ElMessage.success({
      message: `Đã sao chép mã: ${code}`,
      duration: 2000
    })
  } catch (error) {
    console.error('Lỗi copy:', error)
    ElMessage.error({
      message: 'Không thể sao chép mã!',
      duration: 2000
    })
  }
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
/* ═══════════════════════════════════════════════════════════════════════
   ADMIN DISCOUNTS - COUPON-SPECIFIC STYLES ONLY
   All layout, buttons, forms, tables use Design System v2.0 global classes
   ═══════════════════════════════════════════════════════════════════════ */

/* Coupon Code */
.coupon-code {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.coupon-code strong {
  font-family: 'Courier New', monospace;
  font-size: var(--text-base);
  color: var(--accent-primary);
  font-weight: var(--font-bold);
}

.btn-copy {
  background: transparent;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: var(--transition-fast);
}

.btn-copy:hover {
  color: var(--accent-primary);
  background: var(--gradient-purple-soft);
  transform: scale(1.1);
}

.btn-copy .material-icons {
  font-size: var(--text-base);
}

.coupon-description {
  margin: var(--space-1) 0 0 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* Discount type badge uses global .badge class */

/* Discount Value */
.discount-value {
  font-size: var(--text-lg);
  color: var(--success-text);
  font-weight: var(--font-bold);
}

.max-discount {
  margin-top: var(--space-1);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* Condition */
.condition {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.condition .material-icons {
  font-size: var(--text-base);
  color: var(--success-text);
}

/* Time Range */
.time-range {
  font-size: var(--text-xs);
}

.date-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  margin-bottom: var(--space-1);
  color: var(--text-tertiary);
}

.date-item .material-icons {
  font-size: var(--text-sm);
}

/* Usage Stats */
.usage-stats {
  min-width: 120px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: var(--bg-secondary);
  border-radius: var(--radius-full);
  overflow: hidden;
  margin-bottom: var(--space-2);
  border: 1px solid var(--border-primary);
}

.progress-fill {
  height: 100%;
  background: var(--gradient-success);
  transition: var(--transition-smooth);
}

.usage-text {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
}

/* Status badges, action buttons, pagination, modals, forms, buttons, responsive all use global Design System v2.0 classes */
</style>


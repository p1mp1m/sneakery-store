<template>
  <div class="admin-page admin-coupons">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">local_offer</span>
            Quản lý Coupon
          </h1>
          <p class="page-subtitle">Quản lý mã giảm giá và khuyến mãi</p>
        </div>
        <div class="header-actions">
          <button @click="exportCoupons('csv')" class="btn btn-secondary">
            <span class="material-icons">file_download</span>
            CSV
          </button>
          <button @click="exportCoupons('json')" class="btn btn-secondary">
            <span class="material-icons">code</span>
            JSON
          </button>
          <button @click="openCreateModal" class="btn btn-primary">
            <span class="material-icons">add</span>
            Thêm Coupon
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="stats-grid animate-fade-in">
      <div class="stat-card">
        <div class="stat-icon">
          <span class="material-icons">local_offer</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Tổng Coupon</div>
          <div class="stat-value">{{ stats?.totalCoupons || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <span class="material-icons">check_circle</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Đang hoạt động</div>
          <div class="stat-value">{{ stats?.activeCoupons || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <span class="material-icons">schedule</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Sắp hết hạn</div>
          <div class="stat-value">{{ stats?.expiringSoon || 0 }}</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <span class="material-icons">trending_up</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Đã sử dụng</div>
          <div class="stat-value">{{ stats?.totalUsed || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="filters-section">
      <div class="search-box">
        <span class="material-icons search-icon">search</span>
        <input
          v-model="searchQuery"
          @input="handleSearch"
          type="text"
          placeholder="Tìm kiếm coupon..."
          class="search-input"
        />
        <button v-if="searchQuery" @click="clearSearch" class="search-clear">
          <span class="material-icons">close</span>
        </button>
      </div>
      <div class="filter-controls">
        <div class="filter-group">
          <label class="filter-label">Trạng thái:</label>
          <select v-model="filters.status" @change="loadCoupons" class="filter-select">
            <option value="all">Tất cả</option>
            <option value="active">Đang hoạt động</option>
            <option value="inactive">Không hoạt động</option>
            <option value="expired">Đã hết hạn</option>
          </select>
        </div>
        <div class="filter-group">
          <label class="filter-label">Loại giảm giá:</label>
          <select v-model="filters.discountType" @change="loadCoupons" class="filter-select">
            <option value="all">Tất cả</option>
            <option value="fixed">Giảm giá cố định</option>
            <option value="percentage">Giảm giá phần trăm</option>
          </select>
        </div>
        <button @click="resetFilters" class="btn btn-reset">
          <span class="material-icons">refresh</span>
          Reset
        </button>
      </div>
    </div>

    <!-- Coupons Table -->
    <div class="table-container">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>
      
      <div v-else-if="coupons.length === 0" class="empty-state">
        <span class="material-icons">local_offer</span>
        <h3>Chưa có coupon nào</h3>
        <p>Tạo coupon đầu tiên để bắt đầu</p>
        <button @click="openCreateModal" class="btn btn-primary">
          <span class="material-icons">add</span>
          Thêm Coupon
        </button>
      </div>

      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>Mã Coupon</th>
            <th>Mô tả</th>
            <th>Loại giảm giá</th>
            <th>Giá trị</th>
            <th>Thời gian</th>
            <th>Sử dụng</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="coupon in coupons" :key="coupon.id">
            <td>
              <div class="coupon-code">
                <code>{{ coupon.code }}</code>
                <button @click="copyCode(coupon.code)" class="btn-copy" title="Sao chép">
                  <span class="material-icons">content_copy</span>
                </button>
              </div>
            </td>
            <td>
              <div class="coupon-description">
                {{ coupon.description || 'Không có mô tả' }}
              </div>
            </td>
            <td>
              <span class="discount-type-badge" :class="coupon.discount_type">
                {{ coupon.discount_type === 'fixed' ? 'Cố định' : 'Phần trăm' }}
              </span>
            </td>
            <td>
              <div class="discount-value">
                <span v-if="coupon.discount_type === 'fixed'">
                  {{ formatCurrency(coupon.discount_value) }}
                </span>
                <span v-else>
                  {{ coupon.discount_value }}%
                </span>
                <div v-if="coupon.max_discount_amount" class="max-discount">
                  Tối đa: {{ formatCurrency(coupon.max_discount_amount) }}
                </div>
              </div>
            </td>
            <td>
              <div class="time-range">
                <div class="date-item">
                  <span class="material-icons">schedule</span>
                  {{ formatDate(coupon.start_at) }}
                </div>
                <div class="date-item">
                  <span class="material-icons">event</span>
                  {{ formatDate(coupon.end_at) }}
                </div>
              </div>
            </td>
            <td>
              <div class="usage-stats">
                <div class="usage-text">
                  {{ coupon.uses_count || 0 }}/{{ coupon.max_uses || '∞' }}
                </div>
                <div v-if="coupon.max_uses" class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: `${(coupon.uses_count / coupon.max_uses) * 100}%` }"
                  ></div>
                </div>
              </div>
            </td>
            <td>
              <span class="status-badge" :class="getStatusClass(coupon)">
                {{ getStatusText(coupon) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="viewCoupon(coupon)" class="btn-icon btn-view" title="Xem chi tiết">
                  <span class="material-icons">visibility</span>
                </button>
                <button @click="editCoupon(coupon)" class="btn-icon btn-edit" title="Chỉnh sửa">
                  <span class="material-icons">edit</span>
                </button>
                <button @click="toggleCouponStatus(coupon)" class="btn-icon btn-toggle" title="Bật/tắt">
                  <span class="material-icons">{{ coupon.is_active ? 'pause' : 'play_arrow' }}</span>
                </button>
                <button @click="deleteCoupon(coupon)" class="btn-icon btn-delete" title="Xóa">
                  <span class="material-icons">delete</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Pagination -->
      <div class="pagination-container">
        <div class="pagination-info">
          Hiển thị {{ (currentPage * pageSize) + 1 }}-{{ Math.min((currentPage + 1) * pageSize, totalItems) }} 
          trong {{ totalItems }} coupon
        </div>
        <div class="pagination-controls">
          <button 
            @click="goToPage(currentPage - 1)" 
            :disabled="currentPage === 0"
            class="page-btn"
          >
            <span class="material-icons">chevron_left</span>
          </button>
          <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
          <button 
            @click="goToPage(currentPage + 1)" 
            :disabled="currentPage >= totalPages - 1"
            class="page-btn"
          >
            <span class="material-icons">chevron_right</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content modal-lg" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="material-icons">{{ isEditMode ? 'edit' : 'add' }}</span>
            {{ isEditMode ? 'Chỉnh sửa Coupon' : 'Thêm Coupon mới' }}
          </h3>
          <button @click="closeModal" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveCoupon">
            <div class="form-row">
              <div class="form-group">
                <label class="form-label required">Mã Coupon</label>
                <input
                  v-model="formData.code"
                  type="text"
                  class="form-input"
                  placeholder="Nhập mã coupon..."
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">Mô tả</label>
                <input
                  v-model="formData.description"
                  type="text"
                  class="form-input"
                  placeholder="Mô tả coupon..."
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label required">Loại giảm giá</label>
                <select v-model="formData.discount_type" class="form-input" required>
                  <option value="fixed">Giảm giá cố định (VNĐ)</option>
                  <option value="percentage">Giảm giá phần trăm (%)</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label required">Giá trị giảm giá</label>
                <input
                  v-model="formData.discount_value"
                  type="number"
                  class="form-input"
                  placeholder="Nhập giá trị..."
                  required
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label">Giá trị đơn hàng tối thiểu</label>
                <input
                  v-model="formData.min_order_amount"
                  type="number"
                  class="form-input"
                  placeholder="0"
                />
              </div>
              <div class="form-group">
                <label class="form-label">Giá trị giảm tối đa</label>
                <input
                  v-model="formData.max_discount_amount"
                  type="number"
                  class="form-input"
                  placeholder="Không giới hạn"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label required">Ngày bắt đầu</label>
                <input
                  v-model="formData.start_at"
                  type="datetime-local"
                  class="form-input"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label required">Ngày kết thúc</label>
                <input
                  v-model="formData.end_at"
                  type="datetime-local"
                  class="form-input"
                  required
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label">Số lần sử dụng tối đa</label>
                <input
                  v-model="formData.max_uses"
                  type="number"
                  class="form-input"
                  placeholder="Không giới hạn"
                />
              </div>
              <div class="form-group">
                <label class="form-label">Số lần sử dụng mỗi user</label>
                <input
                  v-model="formData.max_uses_per_user"
                  type="number"
                  class="form-input"
                  placeholder="1"
                />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Áp dụng cho</label>
              <select v-model="formData.applicable_to" class="form-input">
                <option value="all">Tất cả sản phẩm</option>
                <option value="brand">Thương hiệu</option>
                <option value="category">Danh mục</option>
                <option value="product">Sản phẩm cụ thể</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-check">
                <input
                  v-model="formData.is_active"
                  type="checkbox"
                  class="form-check-input"
                />
                <span class="form-check-label">Kích hoạt coupon</span>
              </label>
            </div>
          </form>
        </div>
        <div class="modal-actions">
          <button @click="closeModal" class="btn btn-secondary">
            <span class="material-icons">close</span>
            Hủy
          </button>
          <button @click="saveCoupon" class="btn btn-primary" :disabled="submitting">
            <span class="material-icons">{{ isEditMode ? 'save' : 'add' }}</span>
            {{ submitting ? 'Đang lưu...' : (isEditMode ? 'Cập nhật' : 'Tạo mới') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <ConfirmDialog
      v-if="showDeleteModal"
      :show="showDeleteModal"
      title="Xác nhận xóa Coupon"
      :message="`Bạn có chắc chắn muốn xóa coupon '${couponToDelete?.code}'?`"
      confirmText="Xóa"
      cancelText="Hủy"
      :loading="deleting"
      @confirm="handleDelete"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import ConfirmDialog from '@/assets/components/common/ConfirmDialog.vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { debounce } from '@/utils/debounce'

const adminStore = useAdminStore()

// State
const coupons = ref([])
const stats = ref(null)
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const couponToDelete = ref(null)

// Search and filters
const searchQuery = ref('')
const filters = ref({
  status: 'all',
  discountType: 'all'
})

// Form data
const formData = ref({
  code: '',
  description: '',
  discount_type: 'fixed',
  discount_value: 0,
  min_order_amount: null,
  max_discount_amount: null,
  start_at: '',
  end_at: '',
  max_uses: null,
  max_uses_per_user: 1,
  applicable_to: 'all',
  applicable_id: null,
  is_active: true
})

// Computed
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

// Methods
const loadCoupons = async () => {
  try {
    loading.value = true
    
    // Prepare filters for API
    const apiFilters = {
      search: searchQuery.value || undefined,
      status: filters.value.status !== 'all' ? filters.value.status : undefined,
      discountType: filters.value.discountType !== 'all' ? filters.value.discountType : undefined
    }
    
    const response = await adminStore.fetchCoupons(currentPage.value, pageSize.value, apiFilters)
    coupons.value = response.content || []
    totalItems.value = response.totalElements || 0
  } catch (error) {
    ElMessage.error('Lỗi khi tải danh sách coupon')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    // Mock stats - replace with actual API call
    stats.value = {
      totalCoupons: 25,
      activeCoupons: 18,
      expiringSoon: 3,
      totalUsed: 156
    }
  } catch (error) {
    console.error('Error loading stats:', error)
  }
}

const handleSearch = debounce(() => {
  currentPage.value = 0
  loadCoupons()
}, 300)

const clearSearch = () => {
  searchQuery.value = ''
  currentPage.value = 0
  loadCoupons()
}

const resetFilters = () => {
  filters.value = {
    status: 'all',
    discountType: 'all'
  }
  searchQuery.value = ''
  currentPage.value = 0
  loadCoupons()
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadCoupons()
  }
}

const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    code: '',
    description: '',
    discount_type: 'fixed',
    discount_value: 0,
    min_order_amount: null,
    max_discount_amount: null,
    start_at: '',
    end_at: '',
    max_uses: null,
    max_uses_per_user: 1,
    applicable_to: 'all',
    applicable_id: null,
    is_active: true
  }
  showModal.value = true
}

const editCoupon = (coupon) => {
  isEditMode.value = true
  formData.value = { ...coupon }
  showModal.value = true
}

const viewCoupon = (coupon) => {
  // Implement view coupon details
  ElMessage.info(`Xem chi tiết coupon: ${coupon.code}`)
}

const saveCoupon = async () => {
  try {
    submitting.value = true
    if (isEditMode.value) {
      await adminStore.updateCoupon(formData.value.id, formData.value)
      ElMessage.success('Cập nhật coupon thành công')
    } else {
      await adminStore.createCoupon(formData.value)
      ElMessage.success('Tạo coupon thành công')
    }
    closeModal()
    loadCoupons()
  } catch (error) {
    ElMessage.error('Lỗi khi lưu coupon')
  } finally {
    submitting.value = false
  }
}

const toggleCouponStatus = async (coupon) => {
  try {
    await adminStore.toggleCouponStatus(coupon.id)
    ElMessage.success(`Đã ${coupon.is_active ? 'tắt' : 'bật'} coupon`)
    loadCoupons()
  } catch (error) {
    ElMessage.error('Lỗi khi thay đổi trạng thái coupon')
  }
}

const deleteCoupon = (coupon) => {
  couponToDelete.value = coupon
  showDeleteModal.value = true
}

const handleDelete = async () => {
  try {
    deleting.value = true
    await adminStore.deleteCoupon(couponToDelete.value.id)
    ElMessage.success('Xóa coupon thành công')
    showDeleteModal.value = false
    loadCoupons()
  } catch (error) {
    ElMessage.error('Lỗi khi xóa coupon')
  } finally {
    deleting.value = false
  }
}

const closeModal = () => {
  showModal.value = false
  isEditMode.value = false
}

const copyCode = (code) => {
  navigator.clipboard.writeText(code)
  ElMessage.success('Đã sao chép mã coupon')
}

const exportCoupons = (format) => {
  try {
    const data = coupons.value.map(coupon => ({
      'Mã Coupon': coupon.code,
      'Mô tả': coupon.description,
      'Loại giảm giá': coupon.discount_type === 'fixed' ? 'Cố định' : 'Phần trăm',
      'Giá trị': coupon.discount_value,
      'Ngày bắt đầu': formatDate(coupon.start_at),
      'Ngày kết thúc': formatDate(coupon.end_at),
      'Số lần sử dụng': coupon.uses_count || 0,
      'Trạng thái': getStatusText(coupon)
    }))

    if (format === 'csv') {
      downloadCsv(data, 'coupons')
    } else {
      downloadJson(data, 'coupons')
    }
    ElMessage.success(`Đã xuất danh sách coupon (${format.toUpperCase()})`)
  } catch (error) {
    ElMessage.error('Lỗi khi xuất dữ liệu')
  }
}

// Helper functions
const getStatusClass = (coupon) => {
  const now = new Date()
  const startDate = new Date(coupon.start_at)
  const endDate = new Date(coupon.end_at)

  if (!coupon.is_active) return 'status-inactive'
  if (now < startDate) return 'status-pending'
  if (now > endDate) return 'status-expired'
  return 'status-active'
}

const getStatusText = (coupon) => {
  const now = new Date()
  const startDate = new Date(coupon.start_at)
  const endDate = new Date(coupon.end_at)

  if (!coupon.is_active) return 'Không hoạt động'
  if (now < startDate) return 'Chưa bắt đầu'
  if (now > endDate) return 'Đã hết hạn'
  return 'Đang hoạt động'
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Lifecycle
onMounted(() => {
  loadCoupons()
  loadStats()
})
</script>

<style scoped>
.admin-coupons {
  padding: var(--space-6);
}

.page-header {
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.1), rgba(139, 92, 246, 0.05));
  border: 1px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  flex: 1;
}

.page-title {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.page-title .material-icons {
  color: var(--accent-primary);
  font-size: 2rem;
}

.page-subtitle {
  color: var(--text-secondary);
  margin: var(--space-2) 0 0 0;
  font-size: var(--text-base);
}

.header-actions {
  display: flex;
  gap: var(--space-3);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.stat-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  border-left: 4px solid var(--accent-primary);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  border-left-color: var(--accent-hover);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--accent-primary), var(--accent-hover));
  border-radius: var(--radius-md);
  margin-bottom: var(--space-3);
}

.stat-icon .material-icons {
  color: white;
  font-size: 1.5rem;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-1);
}

.stat-value {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.filters-section {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  margin-bottom: var(--space-6);
  backdrop-filter: blur(10px);
}

.search-box {
  position: relative;
  margin-bottom: var(--space-4);
}

.search-icon {
  position: absolute;
  left: var(--space-3);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-secondary);
  font-size: 1.25rem;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-3) var(--space-3) var(--space-10);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  font-size: var(--text-base);
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.search-clear {
  position: absolute;
  right: var(--space-3);
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: all 0.3s ease;
}

.search-clear:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
}

.filter-controls {
  display: flex;
  gap: var(--space-4);
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
}

.filter-select {
  padding: var(--space-2) var(--space-3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  font-size: var(--text-sm);
  min-width: 150px;
}

.btn-reset {
  padding: var(--space-2) var(--space-4);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.btn-reset:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: var(--accent-primary);
}

.table-container {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-8);
  color: var(--text-secondary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(167, 139, 250, 0.3);
  border-top: 3px solid var(--accent-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--space-3);
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
  padding: var(--space-8);
  text-align: center;
}

.empty-state .material-icons {
  font-size: 4rem;
  color: var(--text-secondary);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  font-size: var(--text-xl);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.empty-state p {
  color: var(--text-secondary);
  margin-bottom: var(--space-4);
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table thead {
  background: rgba(167, 139, 250, 0.1);
}

.admin-table th {
  padding: var(--space-4);
  text-align: left;
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-table td {
  padding: var(--space-4);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
}

.admin-table tbody tr:hover {
  background: rgba(167, 139, 250, 0.05);
}

.coupon-code {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.coupon-code code {
  background: rgba(167, 139, 250, 0.1);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-family: 'Courier New', monospace;
  font-weight: var(--font-semibold);
  color: var(--accent-primary);
}

.btn-copy {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: all 0.3s ease;
}

.btn-copy:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
}

.coupon-description {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.discount-type-badge {
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.discount-type-badge.fixed {
  background: rgba(34, 197, 94, 0.1);
  color: var(--color-success);
}

.discount-type-badge.percentage {
  background: rgba(59, 130, 246, 0.1);
  color: var(--color-info);
}

.discount-value {
  font-weight: var(--font-semibold);
}

.max-discount {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  margin-top: var(--space-1);
}

.time-range {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.date-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
}

.date-item .material-icons {
  font-size: 1rem;
  color: var(--text-secondary);
}

.usage-stats {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.usage-text {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--accent-primary), var(--accent-hover));
  transition: width 0.3s ease;
}

.status-badge {
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.status-badge.status-active {
  background: rgba(34, 197, 94, 0.1);
  color: var(--color-success);
}

.status-badge.status-inactive {
  background: rgba(107, 114, 128, 0.1);
  color: var(--text-secondary);
}

.status-badge.status-pending {
  background: rgba(245, 158, 11, 0.1);
  color: var(--color-warning);
}

.status-badge.status-expired {
  background: rgba(239, 68, 68, 0.1);
  color: var(--color-error);
}

.action-buttons {
  display: flex;
  gap: var(--space-1);
}

.btn-icon {
  padding: var(--space-2);
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon .material-icons {
  font-size: 1rem;
}

.btn-icon.btn-view {
  background: rgba(59, 130, 246, 0.1);
  color: var(--color-info);
}

.btn-icon.btn-view:hover {
  background: rgba(59, 130, 246, 0.2);
}

.btn-icon.btn-edit {
  background: rgba(245, 158, 11, 0.1);
  color: var(--color-warning);
}

.btn-icon.btn-edit:hover {
  background: rgba(245, 158, 11, 0.2);
}

.btn-icon.btn-toggle {
  background: rgba(167, 139, 250, 0.1);
  color: var(--accent-primary);
}

.btn-icon.btn-toggle:hover {
  background: rgba(167, 139, 250, 0.2);
}

.btn-icon.btn-delete {
  background: rgba(239, 68, 68, 0.1);
  color: var(--color-error);
}

.btn-icon.btn-delete:hover {
  background: rgba(239, 68, 68, 0.2);
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.pagination-info {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.page-btn {
  padding: var(--space-2);
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: rgba(167, 139, 250, 0.1);
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
  backdrop-filter: blur(5px);
}

.modal-content {
  background: var(--bg-primary);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  max-width: 800px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  backdrop-filter: blur(10px);
}

.modal-lg {
  max-width: 1000px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-5);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.modal-title .material-icons {
  color: var(--accent-primary);
}

.modal-close {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-sm);
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
}

.modal-body {
  padding: var(--space-5);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.form-label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

.form-label.required::after {
  content: ' *';
  color: var(--color-error);
}

.form-input {
  padding: var(--space-3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
  font-size: var(--text-base);
  transition: all 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.form-check {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.form-check-input {
  width: 18px;
  height: 18px;
}

.form-check-label {
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-5);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn {
  padding: var(--space-3) var(--space-4);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.btn-primary {
  background: linear-gradient(135deg, var(--accent-primary), var(--accent-hover));
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-secondary:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.15);
  border-color: var(--accent-primary);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .admin-coupons {
    padding: var(--space-4);
  }

  .header-content {
    flex-direction: column;
    gap: var(--space-4);
    align-items: stretch;
  }

  .header-actions {
    justify-content: center;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .filter-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .modal-content {
    width: 95%;
    margin: var(--space-4);
  }

  .modal-actions {
    flex-direction: column;
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

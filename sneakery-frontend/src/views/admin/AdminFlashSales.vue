<template>
  <div class="admin-flash-sales">
    <!-- ===== PAGE HEADER ===== -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">flash_on</span>
            Quản lí Flash Sale
          </h1>
          <p class="page-subtitle">Quản lý chương trình Flash Sale giảm giá sốc</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-primary" @click="openCreateModal">
            <span class="material-icons">add</span>
            Tạo Flash Sale
          </button>
        </div>
      </div>
    </div>

    <!-- ===== STATS GRID ===== -->
    <div class="stats-grid">
      <div class="stats-card success">
        <div class="stats-icon">
          <span class="material-icons">bolt</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ activeFlashSalesCount }}</div>
          <div class="stats-label">ĐANG DIỄN RA</div>
        </div>
      </div>
      
      <div class="stats-card warning">
        <div class="stats-icon">
          <span class="material-icons">schedule</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ upcomingFlashSalesCount }}</div>
          <div class="stats-label">SẮP DIỄN RA</div>
        </div>
      </div>
      
      <div class="stats-card danger">
        <div class="stats-icon">
          <span class="material-icons">event_busy</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ expiredFlashSalesCount }}</div>
          <div class="stats-label">ĐÃ KẾT THÚC</div>
        </div>
      </div>
      
      <div class="stats-card info">
        <div class="stats-icon">
          <span class="material-icons">shopping_cart</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ totalProductsInSale }}</div>
          <div class="stats-label">SẢN PHẨM THAM GIA</div>
        </div>
      </div>
    </div>

    <!-- ===== FILTERS ===== -->
    <div class="filters-section">
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">search</span>
            Tìm kiếm
          </label>
          <input 
            type="text" 
            class="filter-input" 
            v-model="searchKeyword"
            placeholder="Tìm theo tên sản phẩm..."
          />
        </div>
        
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">filter_list</span>
            Trạng thái
          </label>
          <select class="filter-select" v-model="filterStatus">
            <option value="all">Tất cả</option>
            <option value="active">Đang diễn ra</option>
            <option value="upcoming">Sắp diễn ra</option>
            <option value="expired">Đã kết thúc</option>
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

    <!-- ===== LOADING STATE ===== -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">Đang tải dữ liệu...</p>
    </div>

    <!-- ===== EMPTY STATE ===== -->
    <div v-else-if="filteredFlashSales.length === 0" class="empty-state">
      <span class="material-icons empty-icon">flash_on</span>
      <h3 class="empty-title">Chưa có Flash Sale nào</h3>
      <p class="empty-description">Tạo chương trình Flash Sale đầu tiên để thu hút khách hàng</p>
      <button class="btn btn-primary" @click="openCreateModal">
        <span class="material-icons">add</span>
        Tạo Flash Sale
      </button>
    </div>

    <!-- ===== FLASH SALES TABLE ===== -->
    <div v-else class="table-container">
      <table class="flash-sales-table">
        <thead>
          <tr>
            <th style="width: 80px;">ID</th>
            <th>Sản phẩm</th>
            <th style="width: 120px;">Giá gốc</th>
            <th style="width: 100px;">Giảm giá</th>
            <th style="width: 120px;">Giá Flash Sale</th>
            <th style="width: 100px;">Số lượng</th>
            <th style="width: 180px;">Thời gian</th>
            <th style="width: 130px;">Trạng thái</th>
            <th style="width: 150px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="sale in paginatedFlashSales" :key="sale.id">
            <td>{{ sale.id }}</td>
            <td>
              <div class="product-cell">
                <img 
                  :src="sale.productImage" 
                  :alt="sale.productName"
                  class="product-thumb"
                  @error="handleImageError"
                />
                <div class="product-info">
                  <div class="product-name">{{ sale.productName }}</div>
                  <div class="product-brand">{{ sale.brandName }}</div>
                </div>
              </div>
            </td>
            <td>
              <div class="price original-price">{{ formatCurrency(sale.originalPrice) }}</div>
            </td>
            <td>
              <div class="discount-badge">-{{ sale.discountPercentage }}%</div>
            </td>
            <td>
              <div class="price flash-price">{{ formatCurrency(sale.flashSalePrice) }}</div>
            </td>
            <td>
              <div class="quantity-info">
                <span class="quantity-value">{{ sale.quantity }}</span>
                <span class="quantity-label">sản phẩm</span>
              </div>
            </td>
            <td>
              <div class="time-info">
                <div class="time-row">
                  <span class="material-icons time-icon">play_arrow</span>
                  {{ formatDateTime(sale.startTime) }}
                </div>
                <div class="time-row">
                  <span class="material-icons time-icon">stop</span>
                  {{ formatDateTime(sale.endTime) }}
                </div>
              </div>
            </td>
            <td>
              <span 
                class="status-badge" 
                :class="getStatusClass(sale)"
              >
                <span class="material-icons">{{ getStatusIcon(sale) }}</span>
                {{ getStatusText(sale) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button 
                  class="btn-icon btn-edit" 
                  @click="openEditModal(sale)"
                  title="Chỉnh sửa"
                >
                  <span class="material-icons">edit</span>
                </button>
                <button 
                  class="btn-icon btn-delete" 
                  @click="confirmDelete(sale)"
                  title="Xóa"
                >
                  <span class="material-icons">delete</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ===== PAGINATION ===== -->
    <div v-if="totalPages > 1" class="pagination-container">
      <button 
        class="pagination-btn" 
        :disabled="currentPage === 1"
        @click="currentPage--"
      >
        <span class="material-icons">chevron_left</span>
        Trước
      </button>
      
      <div class="pagination-info">
        Trang {{ currentPage }} / {{ totalPages }}
      </div>
      
      <button 
        class="pagination-btn" 
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >
        Sau
        <span class="material-icons">chevron_right</span>
      </button>
    </div>

    <!-- ===== CREATE/EDIT MODAL ===== -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons">{{ isEditMode ? 'edit' : 'add' }}</span>
            {{ isEditMode ? 'Chỉnh sửa Flash Sale' : 'Tạo Flash Sale mới' }}
          </h2>
          <button class="modal-close" @click="closeModal">
            <span class="material-icons">close</span>
          </button>
        </div>
        
        <div class="modal-body">
          <form @submit.prevent="saveFlashSale">
            <div class="form-group">
              <label class="form-label required">Sản phẩm</label>
              <select class="form-input" v-model="formData.productId" required>
                <option value="">-- Chọn sản phẩm --</option>
                <option v-for="product in availableProducts" :key="product.id" :value="product.id">
                  {{ product.name }} - {{ product.brandName }} ({{ formatCurrency(product.price) }})
                </option>
              </select>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label required">Giảm giá (%)</label>
                <input 
                  type="number" 
                  class="form-input" 
                  v-model.number="formData.discountPercentage"
                  placeholder="VD: 50"
                  min="1"
                  max="99"
                  required
                />
              </div>
              
              <div class="form-group">
                <label class="form-label required">Số lượng</label>
                <input 
                  type="number" 
                  class="form-input" 
                  v-model.number="formData.quantity"
                  placeholder="VD: 100"
                  min="1"
                  required
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label required">Thời gian bắt đầu</label>
                <input 
                  type="datetime-local" 
                  class="form-input" 
                  v-model="formData.startTime"
                  required
                />
              </div>
              
              <div class="form-group">
                <label class="form-label required">Thời gian kết thúc</label>
                <input 
                  type="datetime-local" 
                  class="form-input" 
                  v-model="formData.endTime"
                  required
                />
              </div>
            </div>

            <div v-if="formData.productId && formData.discountPercentage" class="price-preview">
              <div class="preview-row">
                <span>Giá gốc:</span>
                <strong>{{ formatCurrency(selectedProduct?.price || 0) }}</strong>
              </div>
              <div class="preview-row discount-row">
                <span>Giảm {{ formData.discountPercentage }}%:</span>
                <strong class="discount-amount">-{{ formatCurrency(discountAmount) }}</strong>
              </div>
              <div class="preview-row total-row">
                <span>Giá Flash Sale:</span>
                <strong class="flash-sale-price">{{ formatCurrency(flashSalePrice) }}</strong>
              </div>
            </div>

            <div class="modal-actions">
              <button type="button" class="btn btn-outline" @click="closeModal">
                <span class="material-icons">close</span>
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :disabled="saving">
                <span class="material-icons">{{ saving ? 'hourglass_empty' : 'save' }}</span>
                {{ saving ? 'Đang lưu...' : 'Lưu' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="modal-content modal-sm" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons text-danger">warning</span>
            Xác nhận xóa
          </h2>
          <button class="modal-close" @click="showDeleteModal = false">
            <span class="material-icons">close</span>
          </button>
        </div>
        
        <div class="modal-body">
          <p>Bạn có chắc chắn muốn xóa Flash Sale này?</p>
          <p class="text-muted">Hành động này không thể hoàn tác.</p>
          
          <div class="modal-actions">
            <button class="btn btn-outline" @click="showDeleteModal = false">
              <span class="material-icons">close</span>
              Hủy
            </button>
            <button class="btn btn-danger" @click="deleteFlashSale" :disabled="deleting">
              <span class="material-icons">{{ deleting ? 'hourglass_empty' : 'delete' }}</span>
              {{ deleting ? 'Đang xóa...' : 'Xóa' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('all')
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const saleToDelete = ref(null)
const currentPage = ref(1)
const itemsPerPage = 10

// Mock products
const mockProducts = ref([
  { id: 1, name: 'Nike Air Force 1', price: 2500000, brandName: 'Nike' },
  { id: 2, name: 'Adidas Ultraboost', price: 4500000, brandName: 'Adidas' },
  { id: 3, name: 'Converse Chuck Taylor', price: 1500000, brandName: 'Converse' },
  { id: 4, name: 'Puma Suede Classic', price: 1800000, brandName: 'Puma' }
])

// Available products from API
const availableProducts = ref([])

// Form data
const formData = ref({
  id: null,
  productId: '',
  discountPercentage: 0,
  quantity: 0,
  startTime: '',
  endTime: ''
})

// Flash sales data
const flashSales = ref([])

// Computed
const selectedProduct = computed(() => {
  return availableProducts.value.find(p => p.id === formData.value.productId)
})

const discountAmount = computed(() => {
  if (!selectedProduct.value || !formData.value.discountPercentage) return 0
  return (selectedProduct.value.price * formData.value.discountPercentage) / 100
})

const flashSalePrice = computed(() => {
  if (!selectedProduct.value || !formData.value.discountPercentage) return 0
  return selectedProduct.value.price - discountAmount.value
})

const filteredFlashSales = computed(() => {
  let result = flashSales.value

  // Filter by search
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(sale => 
      sale.productName.toLowerCase().includes(keyword) ||
      sale.brandName.toLowerCase().includes(keyword)
    )
  }

  // Filter by status
  if (filterStatus.value !== 'all') {
    const now = new Date()
    result = result.filter(sale => {
      const start = new Date(sale.startTime)
      const end = new Date(sale.endTime)
      
      if (filterStatus.value === 'active') {
        return now >= start && now <= end && sale.isActive
      } else if (filterStatus.value === 'upcoming') {
        return now < start && sale.isActive
      } else if (filterStatus.value === 'expired') {
        return (now > end || !sale.isActive)
      }
      return true
    })
  }

  return result
})

const paginatedFlashSales = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredFlashSales.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredFlashSales.value.length / itemsPerPage)
})

const activeFlashSalesCount = computed(() => {
  const now = new Date()
  return flashSales.value.filter(sale => {
    const start = new Date(sale.startTime)
    const end = new Date(sale.endTime)
    return now >= start && now <= end && sale.isActive
  }).length
})

const upcomingFlashSalesCount = computed(() => {
  const now = new Date()
  return flashSales.value.filter(sale => {
    const start = new Date(sale.startTime)
    return now < start && sale.isActive
  }).length
})

const expiredFlashSalesCount = computed(() => {
  const now = new Date()
  return flashSales.value.filter(sale => {
    const end = new Date(sale.endTime)
    return now > end || !sale.isActive
  }).length
})

const totalProductsInSale = computed(() => {
  return new Set(flashSales.value.map(s => s.productId)).size
})

// Methods
const loadFlashSales = async () => {
  try {
    loading.value = true
    
    // Load flash sales from API
    const salesResult = await adminStore.fetchFlashSales()
    flashSales.value = salesResult || []
    
    // Load products for dropdown
    const productsResult = await adminStore.fetchProducts(0, 100, { isActive: true })
    availableProducts.value = productsResult.content || []
    
    console.log('✅ Flash sales loaded from API')
  } catch (error) {
    console.error('Lỗi khi tải danh sách flash sales:', error)
    ElMessage.error('Không thể tải danh sách flash sales')
  } finally {
    loading.value = false
  }
}

const getStatusClass = (sale) => {
  const now = new Date()
  const start = new Date(sale.startTime)
  const end = new Date(sale.endTime)
  
  if (!sale.isActive || now > end) return 'status-expired'
  if (now >= start && now <= end) return 'status-active'
  if (now < start) return 'status-upcoming'
  return 'status-expired'
}

const getStatusIcon = (sale) => {
  const now = new Date()
  const start = new Date(sale.startTime)
  const end = new Date(sale.endTime)
  
  if (!sale.isActive || now > end) return 'cancel'
  if (now >= start && now <= end) return 'bolt'
  if (now < start) return 'schedule'
  return 'cancel'
}

const getStatusText = (sale) => {
  const now = new Date()
  const start = new Date(sale.startTime)
  const end = new Date(sale.endTime)
  
  if (!sale.isActive || now > end) return 'Đã kết thúc'
  if (now >= start && now <= end) return 'Đang diễn ra'
  if (now < start) return 'Sắp diễn ra'
  return 'Đã kết thúc'
}

const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    id: null,
    productId: '',
    discountPercentage: 0,
    quantity: 0,
    startTime: '',
    endTime: ''
  }
  showModal.value = true
}

const openEditModal = (sale) => {
  isEditMode.value = true
  formData.value = {
    id: sale.id,
    productId: sale.productId,
    discountPercentage: sale.discountPercentage,
    quantity: sale.quantity,
    startTime: sale.startTime.slice(0, 16),
    endTime: sale.endTime.slice(0, 16)
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  formData.value = {
    id: null,
    productId: '',
    discountPercentage: 0,
    quantity: 0,
    startTime: '',
    endTime: ''
  }
}

const saveFlashSale = async () => {
  saving.value = true
  try {
    const flashSaleData = {
      productId: formData.value.productId,
      discountPercentage: formData.value.discountPercentage,
      quantity: formData.value.quantity,
      startTime: formData.value.startTime + ':00',
      endTime: formData.value.endTime + ':00'
    }
    
    if (isEditMode.value) {
      await adminStore.updateFlashSale(formData.value.id, flashSaleData)
      ElMessage.success('Cập nhật Flash Sale thành công!')
    } else {
      await adminStore.createFlashSale(flashSaleData)
      ElMessage.success('Tạo Flash Sale thành công!')
    }
    
    closeModal()
    loadFlashSales()
  } catch (error) {
    console.error('Error saving flash sale:', error)
    ElMessage.error('Lỗi khi lưu Flash Sale')
  } finally {
    saving.value = false
  }
}

const confirmDelete = (sale) => {
  saleToDelete.value = sale
  showDeleteModal.value = true
}

const deleteFlashSale = async () => {
  deleting.value = true
  try {
    await adminStore.deleteFlashSale(saleToDelete.value.id)
    showDeleteModal.value = false
    saleToDelete.value = null
    ElMessage.success('Xóa Flash Sale thành công!')
    loadFlashSales()
  } catch (error) {
    console.error('Error deleting flash sale:', error)
    ElMessage.error('Lỗi khi xóa Flash Sale')
  } finally {
    deleting.value = false
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = 'all'
  currentPage.value = 1
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const formatDateTime = (dateString) => {
  if (!dateString) return '—'
  const date = new Date(dateString)
  return date.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleImageError = (e) => {
  e.target.src = '/placeholder-image.png'
}

// Lifecycle
onMounted(() => {
  loadFlashSales()
})
</script>

<style scoped>
/* ═══ PAGE LAYOUT ═══ */
.admin-flash-sales {
  padding: var(--space-8);
  max-width: 1800px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* ═══ PAGE HEADER ═══ */
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
  margin: 0 0 var(--space-2) 0;
}

.page-title .material-icons {
  font-size: 2rem;
  color: #fbbf24;
  animation: flashPulse 1.5s ease-in-out infinite;
}

@keyframes flashPulse {
  0%, 100% { 
    transform: scale(1);
    filter: drop-shadow(0 0 8px rgba(251, 191, 36, 0.6));
  }
  50% { 
    transform: scale(1.1);
    filter: drop-shadow(0 0 16px rgba(251, 191, 36, 0.9));
  }
}

.page-subtitle {
  color: var(--text-tertiary);
  margin: 0;
  font-size: var(--text-base);
}

.header-actions {
  display: flex;
  gap: var(--space-3);
}

/* ═══ STATS GRID ═══ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stats-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  gap: var(--space-4);
  position: relative;
  overflow: hidden;
  transition: all var(--transition-smooth);
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--gradient-primary);
  transform: scaleY(0);
  transform-origin: top;
  transition: transform var(--transition-smooth);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
}

.stats-card:hover::before {
  transform: scaleY(1);
}

.stats-card.success::before {
  background: var(--gradient-success);
}

.stats-card.warning::before {
  background: var(--gradient-warning);
}

.stats-card.danger::before {
  background: var(--gradient-danger);
}

.stats-card.info::before {
  background: var(--gradient-info);
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all var(--transition-smooth);
}

.stats-card.success .stats-icon {
  background: var(--success-bg);
  color: var(--success-text);
}

.stats-card.warning .stats-icon {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.stats-card.danger .stats-icon {
  background: var(--error-bg);
  color: var(--error-text);
}

.stats-card.info .stats-icon {
  background: var(--info-bg);
  color: var(--info-text);
}

.stats-card:hover .stats-icon {
  transform: scale(1.1) rotate(5deg);
}

.stats-icon .material-icons {
  font-size: 28px;
}

.stats-content {
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
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* ═══ FILTERS ═══ */
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
  grid-template-columns: 2fr 1fr auto;
  gap: var(--space-4);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-label {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.filter-label .material-icons {
  font-size: 1rem;
  color: var(--accent-primary);
}

.filter-input,
.filter-select {
  padding: var(--space-3) var(--space-4);
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  background: rgba(15, 23, 42, 0.4);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

.filter-input:hover,
.filter-select:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.6);
}

.filter-input:focus,
.filter-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.8);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

/* ═══ LOADING & EMPTY STATES ═══ */
.loading-container {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-16) var(--space-8);
  text-align: center;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(251, 191, 36, 0.2);
  border-top-color: #fbbf24;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  color: var(--text-tertiary);
  margin: 0;
}

.empty-state {
  background: var(--bg-card);
  border: 2px dashed var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-16) var(--space-8);
  text-align: center;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.empty-icon {
  font-size: 80px;
  color: #fbbf24;
  opacity: 0.5;
  margin-bottom: var(--space-4);
}

.empty-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.empty-description {
  color: var(--text-tertiary);
  margin: 0 0 var(--space-6) 0;
}

/* ═══ TABLE ═══ */
.table-container {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  margin-bottom: var(--space-6);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.flash-sales-table {
  width: 100%;
  border-collapse: collapse;
}

.flash-sales-table thead {
  background: var(--table-header-bg);
}

.flash-sales-table th {
  padding: var(--space-4);
  text-align: left;
  font-weight: var(--font-semibold);
  color: var(--color-white);
  font-size: var(--text-sm);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.flash-sales-table tbody tr {
  border-bottom: 1px solid var(--border-primary);
  transition: all var(--transition-fast);
}

.flash-sales-table tbody tr:hover {
  background: rgba(251, 191, 36, 0.1);
  transform: translateX(2px);
}

.flash-sales-table td {
  padding: var(--space-4);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* ═══ TABLE CONTENT ═══ */
.product-cell {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.product-thumb {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  object-fit: cover;
  border: 1px solid var(--border-primary);
}

.product-info {
  flex: 1;
}

.product-name {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.product-brand {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.price {
  font-weight: var(--font-bold);
  font-size: var(--text-base);
}

.original-price {
  color: var(--text-tertiary);
  text-decoration: line-through;
}

.flash-price {
  color: #fbbf24;
  font-size: var(--text-lg);
}

.discount-badge {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: var(--color-white);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  font-weight: var(--font-bold);
  font-size: var(--text-sm);
  display: inline-block;
}

.quantity-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.quantity-value {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.quantity-label {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.time-info {
  font-size: var(--text-xs);
}

.time-row {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  margin-bottom: var(--space-1);
}

.time-icon {
  font-size: 14px;
  color: var(--text-tertiary);
}

/* ═══ STATUS BADGES ═══ */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-badge .material-icons {
  font-size: 16px;
}

.status-badge.status-active {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
  animation: pulse 2s ease-in-out infinite;
}

.status-badge.status-upcoming {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.status-badge.status-expired {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

/* ═══ ACTION BUTTONS ═══ */
.action-buttons {
  display: flex;
  gap: var(--space-2);
}

/* Action buttons use global admin-tables.css styles */

/* ═══ PAGINATION ═══ */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-primary);
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  background: rgba(251, 191, 36, 0.1);
  border: 1px solid rgba(251, 191, 36, 0.3);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.pagination-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

/* ═══ BUTTONS ═══ */
.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  text-decoration: none;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--color-white);
  box-shadow: var(--shadow-btn);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-outline {
  background: transparent;
  color: var(--text-primary);
  border: 2px solid var(--border-primary);
}

.btn-outline:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--accent-primary);
}

.btn-danger {
  background: var(--gradient-danger);
  color: var(--color-white);
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(239, 68, 68, 0.4);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ═══ MODAL ═══ */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-modal);
  padding: var(--space-4);
}

.modal-content {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  animation: modalSlideIn 0.3s ease-out;
}

.modal-content.modal-sm {
  max-width: 400px;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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

.modal-title .material-icons {
  font-size: 24px;
  color: var(--accent-primary);
}

.modal-title .text-danger {
  color: var(--error-text);
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

.modal-body {
  padding: var(--space-6);
}

.modal-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

/* ═══ FORM ═══ */
.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-4);
}

.form-group {
  margin-bottom: var(--space-4);
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-label.required::after {
  content: ' *';
  color: var(--error-text);
}

.form-input {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  background: rgba(15, 23, 42, 0.4);
  color: var(--text-primary);
  transition: all var(--transition-fast);
  font-family: inherit;
}

.form-input:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.6);
}

.form-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.8);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.price-preview {
  margin-top: var(--space-6);
  padding: var(--space-4);
  background: rgba(251, 191, 36, 0.1);
  border: 1px solid rgba(251, 191, 36, 0.3);
  border-radius: var(--radius-lg);
}

.preview-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-2) 0;
  color: var(--text-secondary);
}

.discount-row {
  color: #f59e0b;
}

.total-row {
  padding-top: var(--space-3);
  border-top: 1px solid rgba(251, 191, 36, 0.3);
  margin-top: var(--space-2);
}

.flash-sale-price {
  color: #fbbf24;
  font-size: var(--text-2xl);
}

.discount-amount {
  color: #f59e0b;
}

.text-muted {
  color: var(--text-quaternary);
  font-size: var(--text-sm);
}

/* ═══ RESPONSIVE ═══ */
@media (max-width: 768px) {
  .admin-flash-sales {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-6);
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .flash-sales-table {
    font-size: var(--text-xs);
  }

  .flash-sales-table th,
  .flash-sales-table td {
    padding: var(--space-2);
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>


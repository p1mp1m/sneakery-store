<template>
  <div class="admin-page admin-inventory">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">inventory</span>
            Quản lý kho hàng
          </h1>
          <p class="page-subtitle">Theo dõi tồn kho và lịch sử nhập/xuất hàng</p>
        </div>
        <div class="header-actions">
          <button @click="exportInventory('csv')" class="btn btn-secondary">
            <span class="material-icons">file_download</span>
            CSV
          </button>
          <button @click="exportInventory('json')" class="btn btn-secondary">
            <span class="material-icons">code</span>
            JSON
          </button>
          <button @click="openStockAdjustmentModal" class="btn btn-primary">
            <span class="material-icons">add</span>
            Điều chỉnh kho
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="stats-grid animate-fade-up">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <span class="material-icons">inventory_2</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Tổng sản phẩm</div>
          <div class="stat-value">{{ formatNumber(totalProducts) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            +{{ formatNumber(newProductsThisMonth) }} tháng này
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <span class="material-icons">check_circle</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Còn hàng</div>
          <div class="stat-value">{{ formatNumber(inStockProducts) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">done</span>
            {{ Math.round((inStockProducts / totalProducts) * 100) || 0 }}% tổng số
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <span class="material-icons">warning</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Sắp hết hàng</div>
          <div class="stat-value">{{ formatNumber(lowStockProducts) }}</div>
          <div class="stat-change neutral">
            <span class="material-icons">info</span>
            Cần nhập hàng
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <span class="material-icons">remove_shopping_cart</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Hết hàng</div>
          <div class="stat-value">{{ formatNumber(outOfStockProducts) }}</div>
          <div class="stat-change negative">
            <span class="material-icons">trending_down</span>
            Cần nhập khẩn cấp
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
              placeholder="Tìm theo tên sản phẩm, SKU..."
            />
            <button v-if="searchKeyword" @click="clearSearch" class="search-clear">
              <span class="material-icons">close</span>
            </button>
          </div>
        </div>
        
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">filter_list</span>
            Trạng thái kho
          </label>
          <select class="filter-select" v-model="filterStockStatus">
            <option value="all">Tất cả</option>
            <option value="in-stock">Còn hàng</option>
            <option value="low-stock">Sắp hết hàng</option>
            <option value="out-of-stock">Hết hàng</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">category</span>
            Thương hiệu
          </label>
          <select class="filter-select" v-model="filterBrand">
            <option value="all">Tất cả</option>
            <option value="nike">Nike</option>
            <option value="adidas">Adidas</option>
            <option value="converse">Converse</option>
            <option value="puma">Puma</option>
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

    <!-- Inventory Table -->
    <div class="table-container animate-fade-up">
      <div class="table-header">
        <h3 class="table-title">Danh sách tồn kho</h3>
        <div class="table-actions">
          <span class="table-info">{{ filteredProducts.length }} sản phẩm</span>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="filteredProducts.length === 0" class="empty-state">
        <span class="material-icons">inventory</span>
        <h3>Không có sản phẩm nào</h3>
        <p>Chưa có sản phẩm nào được tìm thấy</p>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>SKU</th>
              <th>Thương hiệu</th>
              <th>Tồn kho</th>
              <th>Giá nhập</th>
              <th>Giá bán</th>
              <th>Trạng thái</th>
              <th>Cập nhật cuối</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in paginatedProducts" :key="product.id" class="product-row">
              <td>
                <div class="product-info">
                  <div class="product-image">
                    <img v-if="product.image" :src="product.image" :alt="product.name">
                    <span v-else class="material-icons">image</span>
                  </div>
                  <div class="product-details">
                    <div class="product-name">{{ product.name }}</div>
                    <div class="product-variant">{{ product.size }} - {{ product.color }}</div>
                  </div>
                </div>
              </td>
              <td>
                <div class="sku-code">{{ product.sku }}</div>
              </td>
              <td>
                <div class="brand-info">
                  <span class="brand-name">{{ product.brandName }}</span>
                </div>
              </td>
              <td>
                <div class="stock-info">
                  <div class="stock-quantity">{{ formatNumber(product.stockQuantity) }}</div>
                  <div v-if="product.lowStockThreshold" class="stock-threshold">
                    Tối thiểu: {{ formatNumber(product.lowStockThreshold) }}
                  </div>
                </div>
              </td>
              <td>
                <div class="cost-price">{{ formatCurrency(product.costPrice) }}</div>
              </td>
              <td>
                <div class="sale-price">{{ formatCurrency(product.priceBase) }}</div>
              </td>
              <td>
                <span :class="['stock-status', getStockStatusClass(product.stockQuantity, product.lowStockThreshold)]">
                  {{ getStockStatusText(product.stockQuantity, product.lowStockThreshold) }}
                </span>
              </td>
              <td>
                <div class="update-time">
                  <div class="time">{{ formatDateTime(product.updatedAt) }}</div>
                </div>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="viewInventoryHistory(product)" class="btn-icon btn-view" title="Lịch sử">
                    <span class="material-icons">history</span>
                  </button>
                  <button @click="adjustStock(product)" class="btn-icon btn-adjust" title="Điều chỉnh">
                    <span class="material-icons">edit</span>
                  </button>
                  <button @click="restockProduct(product)" class="btn-icon btn-restock" title="Nhập hàng">
                    <span class="material-icons">add_shopping_cart</span>
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

    <!-- Stock Adjustment Modal -->
    <div v-if="showAdjustmentModal" class="modal-overlay" @click="closeAdjustmentModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="material-icons">edit</span>
            Điều chỉnh tồn kho
          </h3>
          <button @click="closeAdjustmentModal" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedProduct" class="adjustment-form">
            <div class="product-info">
              <h4>{{ selectedProduct.name }}</h4>
              <p>SKU: {{ selectedProduct.sku }}</p>
              <p>Tồn kho hiện tại: <strong>{{ formatNumber(selectedProduct.stockQuantity) }}</strong></p>
            </div>
            
            <div class="form-group">
              <label class="form-label">Loại điều chỉnh</label>
              <select v-model="adjustmentType" class="form-input">
                <option value="add">Thêm vào kho</option>
                <option value="subtract">Trừ khỏi kho</option>
                <option value="set">Đặt số lượng</option>
              </select>
            </div>
            
            <div class="form-group">
              <label class="form-label">Số lượng</label>
              <input 
                type="number" 
                v-model="adjustmentQuantity" 
                class="form-input"
                :min="adjustmentType === 'subtract' ? 0 : 1"
                :max="adjustmentType === 'subtract' ? selectedProduct.stockQuantity : undefined"
              >
            </div>
            
            <div class="form-group">
              <label class="form-label">Lý do</label>
              <textarea 
                v-model="adjustmentReason" 
                class="form-input"
                rows="3"
                placeholder="Nhập lý do điều chỉnh..."
              ></textarea>
            </div>
            
            <div v-if="adjustmentType && adjustmentQuantity" class="preview">
              <h5>Kết quả sau điều chỉnh:</h5>
              <p>
                Tồn kho hiện tại: {{ formatNumber(selectedProduct.stockQuantity) }} 
                → {{ formatNumber(calculateNewStock()) }}
              </p>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeAdjustmentModal" class="btn btn-secondary">Hủy</button>
          <button @click="confirmAdjustment" class="btn btn-primary">Xác nhận</button>
        </div>
      </div>
    </div>

    <!-- Inventory History Modal -->
    <div v-if="showHistoryModal" class="modal-overlay" @click="closeHistoryModal">
      <div class="modal-content modal-lg" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="material-icons">history</span>
            Lịch sử tồn kho
          </h3>
          <button @click="closeHistoryModal" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedProduct" class="history-content">
            <div class="product-info">
              <h4>{{ selectedProduct.name }}</h4>
              <p>SKU: {{ selectedProduct.sku }}</p>
            </div>
            
            <div class="history-list">
              <div v-for="log in inventoryHistory" :key="log.id" class="history-item">
                <div class="history-icon">
                  <span class="material-icons">{{ getChangeTypeIcon(log.changeType) }}</span>
                </div>
                <div class="history-content">
                  <div class="history-header">
                    <span class="change-type">{{ getChangeTypeText(log.changeType) }}</span>
                    <span class="change-time">{{ formatDateTime(log.createdAt) }}</span>
                  </div>
                  <div class="history-details">
                    <span class="quantity-change">
                      {{ log.quantityChange > 0 ? '+' : '' }}{{ formatNumber(log.quantityChange) }}
                    </span>
                    <span class="quantity-before">Trước: {{ formatNumber(log.quantityBefore) }}</span>
                    <span class="quantity-after">Sau: {{ formatNumber(log.quantityAfter) }}</span>
                  </div>
                  <div v-if="log.note" class="history-note">{{ log.note }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeHistoryModal" class="btn btn-secondary">Đóng</button>
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

// Stores
const adminStore = useAdminStore()

// State
const loading = ref(false)
const products = ref([])
const searchKeyword = ref('')
const filterStockStatus = ref('all')
const filterBrand = ref('all')
const currentPage = ref(0)
const pageSize = ref(10)
const showAdjustmentModal = ref(false)
const showHistoryModal = ref(false)
const selectedProduct = ref(null)
const adjustmentType = ref('add')
const adjustmentQuantity = ref(1)
const adjustmentReason = ref('')
const inventoryHistory = ref([])

// Mock data removed - using real API data

// Computed
const totalProducts = computed(() => products.value.length)
const inStockProducts = computed(() => products.value.filter(p => p.stockQuantity > 0).length)
const lowStockProducts = computed(() => products.value.filter(p => p.stockQuantity > 0 && p.stockQuantity <= p.lowStockThreshold).length)
const outOfStockProducts = computed(() => products.value.filter(p => p.stockQuantity === 0).length)
const newProductsThisMonth = computed(() => {
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  
  return products.value.filter(p => {
    const productDate = new Date(p.updatedAt)
    return productDate.getMonth() === thisMonth && productDate.getFullYear() === thisYear
  }).length
})

const filteredProducts = computed(() => {
  let filtered = products.value || []

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.name.toLowerCase().includes(keyword) ||
      p.sku.toLowerCase().includes(keyword) ||
      p.brandName.toLowerCase().includes(keyword)
    )
  }

  if (filterStockStatus.value !== 'all') {
    filtered = filtered.filter(p => {
      switch (filterStockStatus.value) {
        case 'in-stock':
          return p.stockQuantity > p.lowStockThreshold
        case 'low-stock':
          return p.stockQuantity > 0 && p.stockQuantity <= p.lowStockThreshold
        case 'out-of-stock':
          return p.stockQuantity === 0
        default:
          return true
      }
    })
  }

  if (filterBrand.value !== 'all') {
    filtered = filtered.filter(p => p.brandName.toLowerCase() === filterBrand.value)
  }

  return filtered
})

const totalPages = computed(() => Math.ceil(filteredProducts.value.length / pageSize.value))
const paginatedProducts = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return filteredProducts.value.slice(start, end)
})

// Methods
const fetchProducts = async () => {
  loading.value = true
  try {
    const result = await adminStore.fetchProducts(0, 100, { isActive: true })
    products.value = result.content || []
  } catch (error) {
    ElMessage.error('Không thể tải danh sách sản phẩm')
  } finally {
    loading.value = false
  }
}

const fetchInventoryLogs = async () => {
  loadingHistory.value = true
  try {
    const result = await adminStore.fetchInventoryLogs(0, 50, {})
    history.value = result.content || []
  } catch (error) {
    console.error('Error loading inventory logs:', error)
  } finally {
    loadingHistory.value = false
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStockStatus.value = 'all'
  filterBrand.value = 'all'
  currentPage.value = 0
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

const openStockAdjustmentModal = () => {
  selectedProduct.value = null
  adjustmentType.value = 'add'
  adjustmentQuantity.value = 1
  adjustmentReason.value = ''
  showAdjustmentModal.value = true
}

const adjustStock = (product) => {
  selectedProduct.value = product
  adjustmentType.value = 'add'
  adjustmentQuantity.value = 1
  adjustmentReason.value = ''
  showAdjustmentModal.value = true
}

const closeAdjustmentModal = () => {
  showAdjustmentModal.value = false
  selectedProduct.value = null
}

const calculateNewStock = () => {
  if (!selectedProduct.value || !adjustmentQuantity.value) return 0
  
  switch (adjustmentType.value) {
    case 'add':
      return selectedProduct.value.stockQuantity + adjustmentQuantity.value
    case 'subtract':
      return selectedProduct.value.stockQuantity - adjustmentQuantity.value
    case 'set':
      return adjustmentQuantity.value
    default:
      return selectedProduct.value.stockQuantity
  }
}

const confirmAdjustment = async () => {
  if (!adjustmentQuantity.value || adjustmentQuantity.value <= 0) {
    ElMessage.error('Vui lòng nhập số lượng hợp lệ')
    return
  }

  if (!adjustmentReason.value.trim()) {
    ElMessage.error('Vui lòng nhập lý do điều chỉnh')
    return
  }

  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn điều chỉnh tồn kho từ ${formatNumber(selectedProduct.value.stockQuantity)} thành ${formatNumber(calculateNewStock())}?`,
      'Xác nhận điều chỉnh',
      {
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    // Simulate API call
    selectedProduct.value.stockQuantity = calculateNewStock()
    selectedProduct.value.updatedAt = new Date().toISOString()
    
    ElMessage.success('Đã điều chỉnh tồn kho thành công')
    closeAdjustmentModal()
  } catch {
    // User cancelled
  }
}

const restockProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn nhập hàng cho sản phẩm ${product.name}?`,
      'Xác nhận nhập hàng',
      {
        confirmButtonText: 'Nhập hàng',
        cancelButtonText: 'Hủy',
        type: 'info'
      }
    )
    
    // Simulate restock
    product.stockQuantity += 10
    product.updatedAt = new Date().toISOString()
    
    ElMessage.success('Đã nhập hàng thành công')
  } catch {
    // User cancelled
  }
}

const viewInventoryHistory = async (product) => {
  selectedProduct.value = product
  showHistoryModal.value = true
  
  // Load inventory history from API
  loadingHistory.value = true
  try {
    const result = await adminStore.fetchInventoryLogs(0, 50, { variantId: product.id })
    inventoryHistory.value = result.content || []
  } catch (error) {
    console.error('Error loading inventory history:', error)
    inventoryHistory.value = []
  } finally {
    loadingHistory.value = false
  }
}

const closeHistoryModal = () => {
  showHistoryModal.value = false
  selectedProduct.value = null
  inventoryHistory.value = []
}

const exportInventory = (format) => {
  try {
    const dataToExport = filteredProducts.value || []
    if (dataToExport.length === 0) {
      ElMessage.warning('Không có dữ liệu để xuất')
      return
    }
    
    const exportData = dataToExport.map(product => ({
      'ID': product.id,
      'Tên sản phẩm': product.name,
      'SKU': product.sku,
      'Thương hiệu': product.brandName,
      'Kích thước': product.size,
      'Màu sắc': product.color,
      'Tồn kho': formatNumber(product.stockQuantity),
      'Ngưỡng cảnh báo': formatNumber(product.lowStockThreshold),
      'Giá nhập': formatCurrency(product.costPrice),
      'Giá bán': formatCurrency(product.priceBase),
      'Trạng thái': getStockStatusText(product.stockQuantity, product.lowStockThreshold),
      'Cập nhật cuối': formatDateTime(product.updatedAt)
    }))

    if (format === 'csv') {
      downloadCsv(exportData, 'inventory.csv')
      ElMessage.success('Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('inventory', exportData)
      ElMessage.success('Xuất JSON thành công!')
    }
  } catch (error) {
    console.error('Export error:', error)
    ElMessage.error('Có lỗi xảy ra khi xuất dữ liệu!')
  }
}

// Helper functions
const getStockStatusClass = (quantity, threshold) => {
  if (quantity === 0) return 'out-of-stock'
  if (quantity <= threshold) return 'low-stock'
  return 'in-stock'
}

const getStockStatusText = (quantity, threshold) => {
  if (quantity === 0) return 'Hết hàng'
  if (quantity <= threshold) return 'Sắp hết hàng'
  return 'Còn hàng'
}

const getChangeTypeIcon = (type) => {
  const icons = {
    restock: 'add_shopping_cart',
    sale: 'shopping_cart',
    adjustment: 'edit',
    return: 'keyboard_return',
    damaged: 'warning'
  }
  return icons[type] || 'help'
}

const getChangeTypeText = (type) => {
  const texts = {
    restock: 'Nhập hàng',
    sale: 'Bán hàng',
    adjustment: 'Điều chỉnh',
    return: 'Trả hàng',
    damaged: 'Hàng hỏng'
  }
  return texts[type] || type
}

const formatNumber = (num) => {
  return new Intl.NumberFormat('vi-VN').format(num)
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
  fetchProducts()
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

/* Product Info */
.product-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: var(--radius-md);
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image .material-icons {
  color: var(--text-tertiary);
}

.product-name {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.product-variant {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* Stock Status */
.stock-status {
  display: inline-flex;
  align-items: center;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stock-status.in-stock {
  background: var(--success-bg);
  color: var(--success-text);
}

.stock-status.low-stock {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.stock-status.out-of-stock {
  background: var(--error-bg);
  color: var(--error-text);
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

.btn-adjust:hover {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.btn-restock:hover {
  background: var(--success-bg);
  color: var(--success-text);
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
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-content.modal-lg {
  max-width: 800px;
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

.adjustment-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.form-label {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.form-input {
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--bg-secondary);
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.form-input:focus {
  outline: none;
  border-color: var(--accent-primary);
}

.preview {
  background: var(--bg-secondary);
  padding: var(--space-4);
  border-radius: var(--radius-md);
  border-left: 4px solid var(--accent-primary);
}

.preview h5 {
  margin: 0 0 var(--space-2) 0;
  color: var(--text-primary);
}

.preview p {
  margin: 0;
  color: var(--text-secondary);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

/* History */
.history-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.history-item {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  border-left: 4px solid var(--accent-primary);
}

.history-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--accent-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.history-icon .material-icons {
  color: white;
  font-size: 20px;
}

.history-content {
  flex: 1;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.change-type {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.change-time {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.history-details {
  display: flex;
  gap: var(--space-4);
  margin-bottom: var(--space-2);
}

.quantity-change {
  font-weight: var(--font-bold);
  color: var(--accent-primary);
}

.quantity-before,
.quantity-after {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.history-note {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-style: italic;
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

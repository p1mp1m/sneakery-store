<template>
  <div class="product-variants-page">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="material-icons">style</i>
            Quản Lý Biến Thể Sản Phẩm
          </h1>
          <p class="page-subtitle">Quản lý màu sắc, kích thước và tồn kho của từng biến thể sản phẩm</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-primary" @click="openAddVariantModal">
            <i class="material-icons">add</i>
            Thêm Biến Thể
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon primary">
          <i class="material-icons">inventory_2</i>
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stats.totalVariants }}</h3>
          <p class="stat-label">Tổng Biến Thể</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon success">
          <i class="material-icons">check_circle</i>
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stats.inStock }}</h3>
          <p class="stat-label">Còn Hàng</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon warning">
          <i class="material-icons">warning</i>
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stats.lowStock }}</h3>
          <p class="stat-label">Sắp Hết</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon danger">
          <i class="material-icons">remove_circle</i>
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stats.outOfStock }}</h3>
          <p class="stat-label">Hết Hàng</p>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="filters-section">
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">
            <i class="material-icons">search</i>
            Tìm kiếm
          </label>
          <input
            type="text"
            v-model="filters.search"
            placeholder="Tìm theo tên sản phẩm, SKU, màu sắc..."
            class="filter-input"
            @input="handleSearch"
          />
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <i class="material-icons">palette</i>
            Màu sắc
          </label>
          <select v-model="filters.color" class="filter-select" @change="handleFilter">
            <option value="">Tất cả màu</option>
            <option value="black">Đen</option>
            <option value="white">Trắng</option>
            <option value="red">Đỏ</option>
            <option value="blue">Xanh dương</option>
            <option value="green">Xanh lá</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <i class="material-icons">straighten</i>
            Kích thước
          </label>
          <select v-model="filters.size" class="filter-select" @change="handleFilter">
            <option value="">Tất cả size</option>
            <option value="35">35</option>
            <option value="36">36</option>
            <option value="37">37</option>
            <option value="38">38</option>
            <option value="39">39</option>
            <option value="40">40</option>
            <option value="41">41</option>
            <option value="42">42</option>
            <option value="43">43</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <i class="material-icons">inventory</i>
            Trạng thái kho
          </label>
          <select v-model="filters.stockStatus" class="filter-select" @change="handleFilter">
            <option value="">Tất cả</option>
            <option value="in_stock">Còn hàng</option>
            <option value="low_stock">Sắp hết</option>
            <option value="out_of_stock">Hết hàng</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Variants Table -->
    <div class="table-container">
      <table class="variants-table">
        <thead>
          <tr>
            <th>Hình ảnh</th>
            <th>Sản phẩm</th>
            <th>SKU</th>
            <th>Màu sắc</th>
            <th>Kích thước</th>
            <th>Giá</th>
            <th>Tồn kho</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="variant in variants" :key="variant.id" class="variant-row">
            <td>
              <img :src="variant.imageUrl || '/placeholder.png'" :alt="variant.productName" class="variant-image" />
            </td>
            <td>
              <div class="product-info">
                <span class="product-name">{{ variant.productName }}</span>
              </div>
            </td>
            <td>
              <code class="sku-code">{{ variant.sku }}</code>
            </td>
            <td>
              <div class="color-badge">
                <span class="color-dot" :style="{ backgroundColor: getColorHex(variant.color) }"></span>
                <span>{{ variant.colorName }}</span>
              </div>
            </td>
            <td>
              <span class="size-badge">{{ variant.size }}</span>
            </td>
            <td>
              <span class="price">{{ formatPrice(variant.price) }}</span>
            </td>
            <td>
              <span class="stock-quantity" :class="getStockClass(variant.stockQuantity)">
                {{ variant.stockQuantity }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="getStockStatusClass(variant.stockQuantity)">
                {{ getStockStatusLabel(variant.stockQuantity) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button class="btn-icon btn-edit" @click="editVariant(variant)" title="Chỉnh sửa">
                  <i class="material-icons">edit</i>
                </button>
                <button class="btn-icon btn-delete" @click="deleteVariant(variant)" title="Xóa">
                  <i class="material-icons">delete</i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Empty State -->
      <div v-if="variants.length === 0" class="empty-state">
        <i class="material-icons">inventory_2</i>
        <p>Không tìm thấy biến thể nào</p>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination" v-if="totalPages > 1">
      <button
        class="pagination-btn"
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)"
      >
        <i class="material-icons">chevron_left</i>
      </button>
      <span class="page-info">Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
      <button
        class="pagination-btn"
        :disabled="currentPage >= totalPages - 1"
        @click="changePage(currentPage + 1)"
      >
        <i class="material-icons">chevron_right</i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'

// ===== STATE =====
const variants = ref([])
const currentPage = ref(0)
const totalPages = ref(1)
const loading = ref(false)

const stats = reactive({
  totalVariants: 0,
  inStock: 0,
  lowStock: 0,
  outOfStock: 0
})

const filters = reactive({
  search: '',
  color: '',
  size: '',
  stockStatus: ''
})

// ===== MOCK DATA =====
const mockVariants = [
  {
    id: 1,
    productName: 'Nike Air Max 270',
    sku: 'NAM270-BLK-40',
    color: 'black',
    colorName: 'Đen',
    size: '40',
    price: 3500000,
    stockQuantity: 15,
    imageUrl: 'https://via.placeholder.com/80'
  },
  {
    id: 2,
    productName: 'Nike Air Max 270',
    sku: 'NAM270-WHT-40',
    color: 'white',
    colorName: 'Trắng',
    size: '40',
    price: 3500000,
    stockQuantity: 5,
    imageUrl: 'https://via.placeholder.com/80'
  },
  {
    id: 3,
    productName: 'Adidas Ultraboost 21',
    sku: 'AUB21-BLU-42',
    color: 'blue',
    colorName: 'Xanh dương',
    size: '42',
    price: 4200000,
    stockQuantity: 0,
    imageUrl: 'https://via.placeholder.com/80'
  },
  {
    id: 4,
    productName: 'Adidas Ultraboost 21',
    sku: 'AUB21-RED-39',
    color: 'red',
    colorName: 'Đỏ',
    size: '39',
    price: 4200000,
    stockQuantity: 25,
    imageUrl: 'https://via.placeholder.com/80'
  }
]

// ===== LIFECYCLE =====
onMounted(() => {
  loadVariants()
})

// ===== METHODS =====
const loadVariants = () => {
  // TODO: Replace with API call
  variants.value = mockVariants
  calculateStats()
}

const calculateStats = () => {
  stats.totalVariants = variants.value.length
  stats.inStock = variants.value.filter(v => v.stockQuantity > 10).length
  stats.lowStock = variants.value.filter(v => v.stockQuantity > 0 && v.stockQuantity <= 10).length
  stats.outOfStock = variants.value.filter(v => v.stockQuantity === 0).length
}

const handleSearch = () => {
  console.log('Search:', filters.search)
  // TODO: Implement search
}

const handleFilter = () => {
  console.log('Filters:', filters)
  // TODO: Implement filtering
}

const openAddVariantModal = () => {
  console.log('Open add variant modal')
  // TODO: Implement modal
}

const editVariant = (variant) => {
  console.log('Edit variant:', variant)
  // TODO: Implement edit
}

const deleteVariant = (variant) => {
  console.log('Delete variant:', variant)
  // TODO: Implement delete
}

const changePage = (page) => {
  currentPage.value = page
  loadVariants()
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const getColorHex = (color) => {
  const colorMap = {
    black: '#000000',
    white: '#FFFFFF',
    red: '#EF4444',
    blue: '#3B82F6',
    green: '#10B981'
  }
  return colorMap[color] || '#9CA3AF'
}

const getStockClass = (quantity) => {
  if (quantity === 0) return 'stock-out'
  if (quantity <= 10) return 'stock-low'
  return 'stock-ok'
}

const getStockStatusClass = (quantity) => {
  if (quantity === 0) return 'status-danger'
  if (quantity <= 10) return 'status-warning'
  return 'status-success'
}

const getStockStatusLabel = (quantity) => {
  if (quantity === 0) return 'Hết hàng'
  if (quantity <= 10) return 'Sắp hết'
  return 'Còn hàng'
}
</script>

<style scoped>
/* Import CSS từ AdminProducts.vue hoặc tạo riêng */
.product-variants-page {
  padding: 1.5rem;
  max-width: 1600px;
  margin: 0 auto;
}

/* Page Header */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
}

.title-section {
  flex: 1;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.875rem;
  font-weight: 700;
  color: white;
  margin: 0 0 0.5rem 0;
}

.page-title .material-icons {
  font-size: 2rem;
}

.page-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1rem;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.25rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  gap: 1rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-icon.danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.stat-icon .material-icons {
  color: white;
  font-size: 1.75rem;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.875rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.25rem 0;
}

.stat-label {
  color: #64748b;
  font-size: 0.875rem;
  margin: 0;
}

/* Filters */
.filters-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.filter-row {
  display: grid;
  grid-template-columns: 2fr repeat(3, 1fr);
  gap: 1rem;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: #475569;
}

.filter-label .material-icons {
  font-size: 1rem;
  color: #667eea;
}

.filter-input,
.filter-select {
  padding: 0.625rem 0.875rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9375rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: white;
}

.filter-input:focus,
.filter-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

/* Table */
.table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 1.5rem;
}

.variants-table {
  width: 100%;
  border-collapse: collapse;
}

.variants-table thead {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.variants-table th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: white;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.variants-table tbody tr {
  border-bottom: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.variants-table tbody tr:hover {
  background: #f8fafc;
}

.variants-table td {
  padding: 1rem;
  font-size: 0.9375rem;
  color: #475569;
}

.variant-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
}

.product-name {
  font-weight: 600;
  color: #1e293b;
}

.sku-code {
  background: #f1f5f9;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  font-family: 'Courier New', monospace;
  font-size: 0.875rem;
  color: #667eea;
}

.color-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.color-dot {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #e2e8f0;
}

.size-badge {
  display: inline-block;
  background: #f1f5f9;
  padding: 0.375rem 0.75rem;
  border-radius: 6px;
  font-weight: 600;
  color: #475569;
}

.price {
  font-weight: 600;
  color: #10b981;
}

.stock-quantity {
  font-weight: 700;
  font-size: 1rem;
}

.stock-ok {
  color: #10b981;
}

.stock-low {
  color: #f59e0b;
}

.stock-out {
  color: #ef4444;
}

.status-badge {
  display: inline-block;
  padding: 0.375rem 0.75rem;
  border-radius: 6px;
  font-size: 0.8125rem;
  font-weight: 600;
}

.status-success {
  background: #d1fae5;
  color: #065f46;
}

.status-warning {
  background: #fef3c7;
  color: #92400e;
}

.status-danger {
  background: #fee2e2;
  color: #991b1b;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-icon .material-icons {
  font-size: 1.125rem;
}

.btn-edit {
  background: #dbeafe;
  color: #1e40af;
}

.btn-edit:hover {
  background: #3b82f6;
  color: white;
}

.btn-delete {
  background: #fee2e2;
  color: #991b1b;
}

.btn-delete:hover {
  background: #ef4444;
  color: white;
}

/* Empty State */
.empty-state {
  padding: 4rem 2rem;
  text-align: center;
  color: #94a3b8;
}

.empty-state .material-icons {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}

.pagination-btn {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 1.5px solid #e2e8f0;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 0.9375rem;
  color: #475569;
}

/* Buttons */
.btn {
  padding: 0.625rem 1.25rem;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  font-size: 0.9375rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-primary {
  background: white;
  color: #667eea;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.3);
}

.btn .material-icons {
  font-size: 1.125rem;
}

/* Responsive */
@media (max-width: 1024px) {
  .filter-row {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>


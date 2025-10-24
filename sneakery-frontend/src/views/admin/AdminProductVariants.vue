<template>
  <div class="admin-page product-variants-page">
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
    imageUrl: '/placeholder-image.png'
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
    imageUrl: '/placeholder-image.png'
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
    imageUrl: '/placeholder-image.png'
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
    imageUrl: '/placeholder-image.png'
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
/* ═══════════════════════════════════════════════════════════════════════
   📦 PRODUCT VARIANTS PAGE - UNIFIED DARK THEME
   ═══════════════════════════════════════════════════════════════════════ */

.product-variants-page {
  padding: var(--space-8);
  max-width: 1600px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* ═══ PAGE HEADER ═══ */
.page-header {
  background: var(--card-bg);
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
  align-items: flex-start;
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
  color: var(--accent-primary);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

.page-subtitle {
  color: var(--text-tertiary);
  font-size: var(--text-base);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: var(--space-3);
  flex-shrink: 0;
}

/* ═══ STATS GRID ═══ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stat-card {
  background: var(--card-bg);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  display: flex;
  gap: var(--space-4);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  transition: all var(--transition-fast);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--accent-primary);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: var(--shadow-md);
  transition: all var(--transition-fast);
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.stat-icon.primary {
  background: var(--gradient-primary);
}

.stat-icon.success {
  background: var(--gradient-success);
}

.stat-icon.warning {
  background: var(--gradient-warning);
}

.stat-icon.danger {
  background: var(--gradient-danger);
}

.stat-icon .material-icons {
  color: var(--color-white);
  font-size: var(--text-3xl);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  line-height: 1;
}

.stat-label {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* ═══ FILTERS ═══ */
.filters-section {
  background: var(--card-bg);
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
  grid-template-columns: 2fr repeat(3, 1fr);
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
  gap: var(--space-2);
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
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  font-size: var(--text-base);
  transition: all var(--transition-fast);
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.filter-input:hover,
.filter-select:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.8);
}

.filter-input:focus,
.filter-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15),
              0 4px 16px rgba(167, 139, 250, 0.2);
}

.filter-input::placeholder {
  color: var(--text-quaternary);
}

.filter-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23a78bfa' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right var(--space-3) center;
  background-size: 20px;
  padding-right: var(--space-10);
}

/* ═══ TABLE ═══ */
.table-container {
  background: var(--card-bg);
  border-radius: var(--radius-2xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  margin-bottom: var(--space-6);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.variants-table {
  width: 100%;
  border-collapse: collapse;
  background: transparent;
}

.variants-table thead {
  background: var(--table-header-bg);
  border-bottom: 2px solid var(--border-primary);
}

/* Table headers use global admin-tables.css styles */

.variants-table tbody tr {
  background: rgba(30, 41, 59, 0.4);
  border-bottom: 1px solid var(--border-subtle);
  transition: all var(--transition-fast);
}

.variants-table tbody tr:hover {
  background: rgba(167, 139, 250, 0.1);
  transform: scale(1.002);
}

/* Table cells use global admin-tables.css styles */

.variant-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: var(--radius-md);
  border: 2px solid var(--border-primary);
  transition: all var(--transition-fast);
  cursor: pointer;
}

.variant-image:hover {
  transform: scale(1.15);
  border-color: var(--accent-primary);
  box-shadow: var(--shadow-glow-purple);
}

.product-name {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.sku-code {
  background: var(--bg-tertiary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-family: var(--font-mono);
  font-size: var(--text-sm);
  color: var(--accent-primary);
  border: 1px solid var(--border-primary);
}

.color-badge {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.color-dot {
  width: 20px;
  height: 20px;
  border-radius: var(--radius-full);
  border: 2px solid var(--border-primary);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-fast);
}

.color-badge:hover .color-dot {
  transform: scale(1.2);
  box-shadow: var(--shadow-md);
}

.size-badge {
  display: inline-block;
  background: var(--bg-tertiary);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  border: 1px solid var(--border-primary);
  min-width: 40px;
  text-align: center;
}

.price {
  font-weight: var(--font-bold);
  color: var(--success-text);
  font-size: var(--text-lg);
}

.stock-quantity {
  font-weight: var(--font-bold);
  font-size: var(--text-lg);
}

.stock-ok {
  color: var(--success-text);
}

.stock-low {
  color: var(--warning-text);
}

.stock-out {
  color: var(--error-text);
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1-5) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-success {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.status-warning {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.status-danger {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

/* ═══ ACTION BUTTONS ═══ */
.action-buttons {
  display: flex;
  gap: var(--space-2);
}

/* Action buttons use global admin-tables.css styles */

/* ═══ EMPTY STATE ═══ */
.empty-state {
  padding: var(--space-16) var(--space-8);
  text-align: center;
  color: var(--text-tertiary);
}

.empty-state .material-icons {
  font-size: 4rem;
  margin-bottom: var(--space-4);
  opacity: 0.5;
  color: var(--accent-primary);
}

.empty-state p {
  color: var(--text-secondary);
  font-size: var(--text-base);
  margin: 0;
}

/* ═══ PAGINATION ═══ */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  background: var(--card-bg);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-primary);
}

.pagination-btn {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-fast);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.pagination-btn:hover:not(:disabled) {
  background: var(--gradient-purple);
  border-color: var(--accent-primary);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: var(--text-base);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
  padding: 0 var(--space-4);
}

/* ═══ BUTTONS ═══ */
.btn {
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-lg);
  border: none;
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--color-white);
  box-shadow: var(--shadow-btn);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn .material-icons {
  font-size: var(--text-lg);
}

/* ═══ RESPONSIVE ═══ */
@media (max-width: 1024px) {
  .product-variants-page {
    padding: var(--space-6) var(--space-3);
  }
  
  .filter-row {
    grid-template-columns: 1fr 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .product-variants-page {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-5);
  }

  .header-content {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-4);
  }

  .page-title {
    font-size: var(--text-2xl);
  }
  
  .page-title .material-icons {
    font-size: 1.5rem;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .header-actions .btn {
    flex: 1;
    justify-content: center;
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .variants-table {
    font-size: var(--text-sm);
  }

  .variants-table th,
  .variants-table td {
    padding: var(--space-3) var(--space-2);
  }

  .variant-image {
    width: 48px;
    height: 48px;
  }
  
  .sku-code {
    font-size: var(--text-xs);
  }
  
  .size-badge {
    padding: var(--space-1) var(--space-2);
    font-size: var(--text-xs);
    min-width: 32px;
  }
  
  .price,
  .stock-quantity {
    font-size: var(--text-base);
  }
}

@media (max-width: 480px) {
  .variants-table th,
  .variants-table td {
    padding: var(--space-2);
    font-size: var(--text-xs);
  }
  
  .variant-image {
    width: 40px;
    height: 40px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: var(--space-1);
  }
  
}
</style>


<template>
  <div class="user-page products-page">
    <!-- Header -->
    <div class="page-header">
      <h1 style="color: #f1f5f9; font-size: 2.25rem; font-weight: 700;">Tất cả sản phẩm</h1>
      <p style="color: #94a3b8;">Khám phá bộ sưu tập giày sneaker của chúng tôi</p>
    </div>

    <!-- Filters & Products Grid -->
    <div class="products-container">
      <!-- Sidebar Filters -->
      <aside class="filters-sidebar">
        <div class="filter-section">
          <h3 class="filter-title">Thương hiệu</h3>
          <div class="filter-options">
            <label v-for="brand in brands" :key="brand" class="filter-option">
              <input type="checkbox" :value="brand" v-model="selectedBrands">
              <span>{{ brand }}</span>
            </label>
          </div>
        </div>

        <div class="filter-section">
          <h3 class="filter-title">Khoảng giá</h3>
          <div class="filter-options">
            <label v-for="range in priceRanges" :key="range.label" class="filter-option">
              <input type="radio" :value="range" v-model="selectedPriceRange" name="price">
              <span>{{ range.label }}</span>
            </label>
          </div>
        </div>

        <button @click="clearFilters" class="btn btn-outline w-full">
          Xóa bộ lọc
        </button>
      </aside>

      <!-- Products Grid -->
      <div class="products-main">
        <!-- Sort Bar -->
        <div class="sort-bar">
          <div class="sort-info">
            <span>Tìm thấy {{ totalItems }} sản phẩm</span>
          </div>
          <select v-model="sortBy" @change="fetchProducts" class="sort-select">
            <option value="">Sắp xếp</option>
            <option value="price-asc">Giá: Thấp → Cao</option>
            <option value="price-desc">Giá: Cao → Thấp</option>
            <option value="name-asc">Tên: A → Z</option>
            <option value="name-desc">Tên: Z → A</option>
            <option value="newest">Mới nhất</option>
          </select>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner-lg"></div>
          <p>Đang tải sản phẩm...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="error-container">
          <p class="text-error">{{ error }}</p>
          <button @click="fetchProducts" class="btn btn-primary">Thử lại</button>
        </div>

        <!-- Products Grid -->
        <div v-else-if="products.length > 0" class="products-grid">
          <div 
            v-for="product in products" 
            :key="product.id" 
            class="product-card"
          >
            <div class="product-image">
              <img :src="product.imageUrl" :alt="product.name" />
            </div>
            <div class="product-info">
              <p class="product-brand">{{ product.brandName }}</p>
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-price">{{ formatPrice(product.price) }}</p>
              <button class="btn btn-primary btn-sm w-full">
                Xem chi tiết
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="empty-state">
          <p class="text-lg text-secondary">Không tìm thấy sản phẩm nào</p>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination">
          <button 
            @click="prevPage" 
            :disabled="currentPage === 0"
            class="btn btn-secondary"
          >
            Trước
          </button>
          
          <span class="page-info">
            Trang {{ currentPage + 1 }} / {{ totalPages }}
          </span>
          
          <button 
            @click="nextPage" 
            :disabled="currentPage >= totalPages - 1"
            class="btn btn-secondary"
          >
            Sau
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import productService from '@/services/productService'

// State
const products = ref([])
const loading = ref(false)
const error = ref(null)
const currentPage = ref(0)
const pageSize = ref(8)
const totalPages = ref(0)
const totalItems = ref(0)
const sortBy = ref('')

// Filters
const brands = ref(['Nike', 'Adidas', 'Puma', 'New Balance', 'Vans', 'Converse'])
const selectedBrands = ref([])
const selectedPriceRange = ref(null)
const priceRanges = ref([
  { label: 'Tất cả', min: 0, max: 999999999 },
  { label: 'Dưới 1 triệu', min: 0, max: 1000000 },
  { label: '1 - 2 triệu', min: 1000000, max: 2000000 },
  { label: '2 - 5 triệu', min: 2000000, max: 5000000 },
  { label: 'Trên 5 triệu', min: 5000000, max: 999999999 }
])

// Methods
const fetchProducts = async () => {
  try {
    loading.value = true
    error.value = null
    
    const filters = {}
    if (selectedBrands.value.length > 0) {
      filters.brands = selectedBrands.value.join(',')
    }
    if (selectedPriceRange.value) {
      filters.minPrice = selectedPriceRange.value.min
      filters.maxPrice = selectedPriceRange.value.max
    }
    if (sortBy.value) {
      filters.sortBy = sortBy.value
    }
    
    const response = await productService.searchProducts('', filters)
    
    products.value = response
    totalItems.value = response.length
    totalPages.value = Math.ceil(response.length / pageSize.value)
    
  } catch (err) {
    error.value = 'Không thể tải danh sách sản phẩm. Vui lòng thử lại.'
    console.error('Error fetching products:', err)
  } finally {
    loading.value = false
  }
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { 
    style: 'currency', 
    currency: 'VND' 
  }).format(price)
}

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
  }
}

const clearFilters = () => {
  selectedBrands.value = []
  selectedPriceRange.value = null
  sortBy.value = ''
  fetchProducts()
}

// Watch filters
watch([selectedBrands, selectedPriceRange], () => {
  currentPage.value = 0
  fetchProducts()
}, { deep: true })

// Initial load
onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
/* ===== DARK THEME - ProductListPage ===== */

.products-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-6);
  background: transparent;
}

.products-container {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: var(--space-6);
}

/* Filters Sidebar - Dark */
.filters-sidebar {
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  backdrop-filter: blur(10px);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
  height: fit-content;
  position: sticky;
  top: var(--space-6);
}

.filter-section {
  margin-bottom: var(--space-6);
}

.filter-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-3);
  color: #f1f5f9;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-option {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-md);
  transition: var(--transition-fast);
  color: #e2e8f0;
}

.filter-option:hover {
  background: rgba(167, 139, 250, 0.15);
}

.filter-option input {
  cursor: pointer;
  accent-color: #a78bfa;
}

/* products-grid, product-card, product-image, product-brand đã có trong style.css */

.products-main {
  min-height: 500px;
}

.sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-4);
  backdrop-filter: blur(10px);
}

.sort-info {
  color: #94a3b8;
  font-size: var(--text-sm);
}

.sort-select {
  padding: var(--space-2) var(--space-4);
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(167, 139, 250, 0.3);
  border-radius: var(--radius-md);
  color: #f1f5f9;
  font-size: var(--text-sm);
  cursor: pointer;
  transition: var(--transition-fast);
}

.sort-select:focus {
  outline: none;
  border-color: #a78bfa;
}

.loading-container,
.error-container,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: var(--space-4);
  color: #e2e8f0;
}

.loading-container p,
.error-container p {
  color: #94a3b8;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  margin-top: var(--space-8);
}

.page-info {
  font-size: var(--text-base);
  color: #e2e8f0;
}

/* Dark Theme Overrides */
.products-page .product-card {
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  backdrop-filter: blur(10px);
}

.products-page .product-card:hover {
  background: rgba(30, 41, 59, 0.8);
  border-color: rgba(167, 139, 250, 0.4);
}

.products-page .product-brand {
  color: #94a3b8;
}

.products-page .product-name {
  color: #f1f5f9;
}

.products-page .product-price {
  color: #c4b5fd;
}

/* Responsive - Custom */
@media (max-width: 1024px) {
  .products-container {
    grid-template-columns: 1fr;
  }

  .filters-sidebar {
    position: static;
  }
}
</style>


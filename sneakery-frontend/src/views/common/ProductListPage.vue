<template>
  <div class="user-page products-page">
    <!-- Header -->
    <div class="page-header">
      <h1>Tất cả sản phẩm</h1>
      <p>Khám phá bộ sưu tập giày sneaker của chúng tôi</p>
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
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2"/>
            <path d="M12 8V12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <path d="M12 16H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <p>{{ error }}</p>
          <button @click="fetchProducts" class="btn btn-primary">
            Thử lại
          </button>
        </div>

        <!-- Products Grid -->
        <div v-else-if="products.length > 0" class="products-grid">
          <ProductCard 
            v-for="product in displayedProducts" 
            :key="product.id"
            :product="product"
          />
        </div>

        <!-- Empty State -->
        <div v-else class="empty-state">
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 3L5 21L12 18L19 21L21 3M5 3H19M8 7H16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <h3>Không tìm thấy sản phẩm</h3>
          <p>Hãy thử điều chỉnh bộ lọc hoặc mua sắm sau</p>
          <button @click="clearFilters" class="btn btn-primary">
            Xóa bộ lọc
          </button>
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
import { ref, computed, onMounted, watch } from 'vue'
import productService from '@/services/productService'
import ProductCard from '@/assets/components/products/ProductCard.vue'

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

// Computed
const displayedProducts = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return products.value.slice(start, end)
})

// Methods
const fetchProducts = async () => {
  try {
    loading.value = true
    error.value = null
    
    const response = await productService.getProducts(0, 50)
    const productData = response.data?.content || response.data || []
    
    products.value = productData
    totalItems.value = response.data?.totalElements || productData.length || 0
    totalPages.value = Math.ceil(totalItems.value / pageSize.value)
    
  } catch (err) {
    console.error('Error fetching products:', err)
    error.value = 'Không thể tải danh sách sản phẩm. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
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

.page-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.page-header h1 {
  font-size: 2.25rem;
  font-weight: 700;
  color: #f1f5f9;
  margin: 0 0 var(--space-2);
}

.page-header p {
  font-size: var(--text-lg);
  color: #94a3b8;
  margin: 0;
}

.products-container {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: var(--space-6);
}

/* Filters Sidebar */
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
  margin-bottom: var(--space-4);
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
  gap: var(--space-3);
  cursor: pointer;
  padding: var(--space-2-5) var(--space-3);
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
  width: 18px;
  height: 18px;
}

.products-main {
  min-height: 500px;
}

.sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-5);
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-6);
  backdrop-filter: blur(10px);
}

.sort-info {
  color: #94a3b8;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.sort-select {
  padding: var(--space-2-5) var(--space-4);
  background: rgba(15, 23, 42, 0.8);
  border: 1px solid rgba(167, 139, 250, 0.3);
  border-radius: var(--radius-md);
  color: #f1f5f9;
  font-size: var(--text-sm);
  cursor: pointer;
  transition: var(--transition-fast);
  font-family: inherit;
}

.sort-select:hover {
  border-color: rgba(167, 139, 250, 0.5);
}

.sort-select:focus {
  outline: none;
  border-color: #a78bfa;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.sort-select option {
  background: #1e293b;
  color: #f1f5f9;
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
  text-align: center;
}

.loading-container svg,
.error-container svg,
.empty-state svg {
  color: #64748b;
}

.loading-container p,
.error-container p {
  color: #94a3b8;
  font-size: var(--text-base);
}

.error-container h3,
.empty-state h3 {
  color: #f1f5f9;
  font-size: var(--text-xl);
  margin: 0;
}

.empty-state p {
  color: #94a3b8;
  font-size: var(--text-base);
}

.loading-spinner-lg {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: #a78bfa;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
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
  font-weight: var(--font-medium);
}

/* Responsive */
@media (max-width: 1024px) {
  .products-container {
    grid-template-columns: 1fr;
  }

  .filters-sidebar {
    position: static;
  }
  
  .page-header h1 {
    font-size: 1.75rem;
  }
}

@media (max-width: 768px) {
  .products-page {
    padding: var(--space-4);
  }
  
  .sort-bar {
    flex-direction: column;
    gap: var(--space-3);
    align-items: stretch;
  }
}
</style>

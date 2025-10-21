<template>
  <div class="products-page">
    <!-- Header -->
    <div class="page-header">
      <h1 class="text-3xl font-bold">Tất cả sản phẩm</h1>
      <p class="text-secondary">Khám phá bộ sưu tập giày sneaker của chúng tôi</p>
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
    
    const response = await productService.getProducts(currentPage.value, pageSize.value)
    
    // response.data là Page object từ Spring
    products.value = response.data.content
    totalPages.value = response.data.totalPages
    
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
}

// Watch for page changes
watch(currentPage, () => {
  fetchProducts()
})

// Initial load
onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.products-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-6);
}

.page-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.page-header h1 {
  margin-bottom: var(--space-2);
}

.products-container {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: var(--space-6);
}

/* Filters Sidebar */
.filters-sidebar {
  background: var(--bg-secondary);
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
}

.filter-option:hover {
  background: var(--bg-tertiary);
}

.filter-option input {
  cursor: pointer;
}

/* Products Grid */
.products-main {
  min-height: 500px;
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
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.product-card {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: var(--transition-normal);
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.product-image {
  width: 100%;
  height: 250px;
  overflow: hidden;
  background: var(--bg-tertiary);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: var(--space-4);
}

.product-brand {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-1);
}

.product-name {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-2);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--primary-color);
  margin-bottom: var(--space-3);
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  margin-top: var(--space-8);
}

.page-info {
  font-size: var(--text-base);
  color: var(--text-secondary);
}

/* Responsive */
@media (max-width: 1024px) {
  .products-container {
    grid-template-columns: 1fr;
  }

  .filters-sidebar {
    position: static;
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: var(--space-4);
  }

  .product-image {
    height: 200px;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>


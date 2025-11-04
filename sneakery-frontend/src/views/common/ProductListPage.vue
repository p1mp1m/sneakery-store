<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-7xl mx-auto px-4">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">Tất cả sản phẩm</h1>
        <p class="text-gray-600 dark:text-gray-400">Khám phá bộ sưu tập giày sneaker của chúng tôi</p>
      </div>

      <!-- Filters & Products Grid -->
      <div class="flex flex-col lg:flex-row gap-6">
        <!-- Sidebar Filters -->
        <aside class="w-full lg:w-64 flex-shrink-0">
          <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 sticky top-24">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Thương hiệu</h3>
            <div class="space-y-2 mb-6">
              <label v-for="brand in brands" :key="brand" class="flex items-center gap-2 cursor-pointer hover:text-purple-600 dark:hover:text-purple-400 transition-colors">
                <input type="checkbox" :value="brand" v-model="selectedBrands" class="w-4 h-4 text-purple-600 rounded border-gray-300 focus:ring-purple-500">
                <span class="text-sm text-gray-700 dark:text-gray-300">{{ brand }}</span>
              </label>
            </div>

            <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Khoảng giá</h3>
            <div class="space-y-2 mb-6">
              <label v-for="range in priceRanges" :key="range.label" class="flex items-center gap-2 cursor-pointer hover:text-purple-600 dark:hover:text-purple-400 transition-colors">
                <input type="radio" :value="range" v-model="selectedPriceRange" name="price" class="w-4 h-4 text-purple-600 border-gray-300 focus:ring-purple-500">
                <span class="text-sm text-gray-700 dark:text-gray-300">{{ range.label }}</span>
              </label>
            </div>

            <button @click="clearFilters" class="w-full px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors font-medium">
              Xóa bộ lọc
            </button>
          </div>
        </aside>

        <!-- Products Main -->
        <div class="flex-1">
          <!-- Sort Bar -->
          <div class="flex items-center justify-between mb-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-4">
            <div class="text-sm text-gray-600 dark:text-gray-400">
              Tìm thấy <span class="font-semibold text-gray-900 dark:text-gray-100">{{ totalItems }}</span> sản phẩm
            </div>
            <select v-model="sortBy" @change="fetchProducts" class="px-4 py-2 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-lg text-gray-700 dark:text-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500">
              <option value="">Sắp xếp</option>
              <option value="price-asc">Giá: Thấp → Cao</option>
              <option value="price-desc">Giá: Cao → Thấp</option>
              <option value="name-asc">Tên: A → Z</option>
              <option value="name-desc">Tên: Z → A</option>
              <option value="newest">Mới nhất</option>
            </select>
          </div>

          <!-- Loading State -->
          <div v-if="loading" class="text-center py-16">
            <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-purple-600 border-t-transparent mb-4"></div>
            <p class="text-gray-600 dark:text-gray-400">Đang tải sản phẩm...</p>
          </div>

          <!-- Error State -->
          <div v-else-if="error" class="text-center py-16 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="mx-auto mb-4 text-red-500">
              <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2"/>
              <path d="M12 8V12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <path d="M12 16H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <p class="text-gray-900 dark:text-gray-100 mb-4">{{ error }}</p>
            <button @click="fetchProducts" class="px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors">
              Thử lại
            </button>
          </div>

          <!-- Products Grid -->
          <div v-else-if="products.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-6">
            <ProductCard 
              v-for="product in displayedProducts" 
              :key="product.id"
              :product="product"
            />
          </div>

          <!-- Empty State -->
          <div v-else class="text-center py-16 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="mx-auto mb-4 text-gray-400">
              <path d="M3 3L5 21L12 18L19 21L21 3M5 3H19M8 7H16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h3 class="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-2">Không tìm thấy sản phẩm</h3>
            <p class="text-gray-600 dark:text-gray-400 mb-6">Hãy thử điều chỉnh bộ lọc hoặc mua sắm sau</p>
            <button @click="clearFilters" class="px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors">
              Xóa bộ lọc
            </button>
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="flex items-center justify-center gap-4 mt-8">
            <button 
              @click="prevPage" 
              :disabled="currentPage === 0"
              class="px-4 py-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Trước
            </button>
            
            <span class="text-sm text-gray-600 dark:text-gray-400">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
            </span>
            
            <button 
              @click="nextPage" 
              :disabled="currentPage >= totalPages - 1"
              class="px-4 py-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Sau
            </button>
          </div>
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
    
    console.log('🔄 Fetching products...')
    const response = await productService.getProducts(0, 50)
    console.log('✅ Response received:', response)
    console.log('📦 Response data:', response.data)
    
    // Handle Spring Data Page structure
    let productData = []
    let totalElements = 0
    
    if (response.data) {
      // Spring Data Page format: { content: [...], totalElements: 100, totalPages: 10, ... }
      if (Array.isArray(response.data.content)) {
        productData = response.data.content
        totalElements = response.data.totalElements || 0
      } 
      // If response.data is already an array
      else if (Array.isArray(response.data)) {
        productData = response.data
        totalElements = response.data.length
      }
      // If response.data is a single object (shouldn't happen but handle it)
      else {
        console.warn('⚠️ Unexpected response format:', response.data)
        productData = []
        totalElements = 0
      }
    }
    
    console.log('📋 Processed products:', productData.length)
    products.value = productData
    totalItems.value = totalElements
    totalPages.value = Math.ceil(totalElements / pageSize.value)
    
    if (productData.length === 0) {
      console.warn('⚠️ No products found')
    }
    
  } catch (err) {
    console.error('❌ Error fetching products:', err)
    console.error('❌ Error details:', {
      message: err.message,
      response: err.response?.data,
      status: err.response?.status,
      statusText: err.response?.statusText
    })
    
    // Better error message
    if (err.response?.status === 404) {
      error.value = 'Không tìm thấy sản phẩm nào.'
    } else if (err.response?.status === 500) {
      error.value = 'Lỗi server. Vui lòng thử lại sau.'
    } else if (err.response?.data?.message) {
      error.value = err.response.data.message
    } else {
      error.value = `Không thể tải danh sách sản phẩm: ${err.message || 'Vui lòng thử lại.'}`
    }
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

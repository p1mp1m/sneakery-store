<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Hero Banner Section -->
    <section class="relative bg-gradient-to-br from-purple-600 via-purple-700 to-indigo-800 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 overflow-hidden">
      <div class="absolute inset-0 bg-grid-pattern opacity-10"></div>
      <div class="max-w-7xl mx-auto px-4 py-12 md:py-20 lg:py-24 relative z-10">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-12 items-center">
          <!-- Hero Content -->
          <div class="text-center lg:text-left space-y-6">
            <div class="inline-flex items-center gap-2 px-4 py-2 bg-white/20 dark:bg-gray-800/30 backdrop-blur-sm rounded-full text-white text-sm font-medium animate-pulse">
              <i class="material-icons text-orange-400">local_fire_department</i>
              <span>Bán chạy nhất 2025</span>
            </div>
            
            <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-white leading-tight">
              Sneakery Store
              <span class="block bg-gradient-to-r from-yellow-300 to-orange-400 bg-clip-text text-transparent">Giày Sneaker Chính Hãng</span>
            </h1>
            
            <p class="text-lg text-white/90 dark:text-gray-300 max-w-xl mx-auto lg:mx-0">
              Bộ sưu tập giày sneaker đa dạng từ Nike, Adidas, Jordan và nhiều thương hiệu nổi tiếng khác. 
              Giá tốt nhất thị trường - Miễn phí vận chuyển - Đổi trả dễ dàng.
            </p>
            
            <div class="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
              <router-link 
                to="/home/products" 
                class="inline-flex items-center justify-center gap-2 px-6 py-3 bg-white text-purple-700 dark:bg-purple-600 dark:text-white rounded-xl font-semibold hover:bg-gray-100 dark:hover:bg-purple-700 transition-all duration-200 shadow-lg hover:shadow-xl hover:scale-105"
              >
                <i class="material-icons">shopping_bag</i>
                Mua Sắm Ngay
              </router-link>
              <router-link 
                to="/home/products" 
                class="inline-flex items-center justify-center gap-2 px-6 py-3 bg-white/10 dark:bg-gray-800/50 backdrop-blur-sm text-white border-2 border-white/30 rounded-xl font-semibold hover:bg-white/20 dark:hover:bg-gray-800/70 transition-all duration-200"
              >
                <i class="material-icons">explore</i>
                Khám Phá
              </router-link>
            </div>
            
            <!-- Trust Badges -->
            <div class="flex flex-wrap justify-center lg:justify-start gap-4 pt-4">
              <div class="flex items-center gap-2 text-white/90 text-sm">
                <i class="material-icons text-green-400">verified</i>
                <span>100% Chính Hãng</span>
              </div>
              <div class="flex items-center gap-2 text-white/90 text-sm">
                <i class="material-icons text-blue-400">local_shipping</i>
                <span>Miễn Phí Ship</span>
              </div>
              <div class="flex items-center gap-2 text-white/90 text-sm">
                <i class="material-icons text-purple-400">sync</i>
                <span>Đổi Trả 30 Ngày</span>
              </div>
              <div class="flex items-center gap-2 text-white/90 text-sm">
                <i class="material-icons text-orange-400">support_agent</i>
                <span>Hỗ Trợ 24/7</span>
              </div>
            </div>
          </div>
          
          <!-- Hero Image -->
          <div class="relative hidden lg:block">
            <div class="relative bg-white/10 dark:bg-gray-800/30 backdrop-blur-sm rounded-2xl p-8 shadow-2xl animate-float">
              <img src="@/assets/images/logo.png" alt="Sneakery Store" class="w-full h-auto rounded-xl" />
              
              <!-- Floating Stats -->
              <div class="absolute -top-4 -right-4 bg-white dark:bg-gray-800 rounded-xl p-4 shadow-lg border border-gray-200 dark:border-gray-700">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 bg-gradient-to-br from-green-400 to-green-600 rounded-lg flex items-center justify-center">
                    <i class="material-icons text-white">trending_up</i>
                  </div>
                  <div>
                    <div class="text-lg font-bold text-gray-900 dark:text-gray-100">1000+</div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">Sản phẩm</div>
                  </div>
                </div>
              </div>
              
              <div class="absolute -bottom-4 -left-4 bg-white dark:bg-gray-800 rounded-xl p-4 shadow-lg border border-gray-200 dark:border-gray-700">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 bg-gradient-to-br from-pink-400 to-pink-600 rounded-lg flex items-center justify-center">
                    <i class="material-icons text-white">star</i>
                  </div>
                  <div>
                    <div class="text-lg font-bold text-gray-900 dark:text-gray-100">4.9/5</div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">Đánh giá</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories Section -->
    <section class="py-12 md:py-16 bg-white dark:bg-gray-800">
      <div class="max-w-7xl mx-auto px-4">
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">Danh Mục Sản Phẩm</h2>
          <p class="text-gray-600 dark:text-gray-400">Khám phá bộ sưu tập theo danh mục</p>
        </div>
        
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
          <router-link 
            v-for="category in categories" 
            :key="category.id"
            :to="`/products?category=${category.slug}`"
            class="group p-4 bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 hover:border-purple-500 dark:hover:border-purple-600 hover:shadow-lg transition-all duration-200 hover:scale-105 text-center"
          >
            <div class="w-16 h-16 mx-auto mb-3 rounded-xl flex items-center justify-center bg-gradient-to-br from-purple-500 to-purple-600 group-hover:scale-110 transition-transform">
              <i class="material-icons text-white text-2xl">{{ category.icon }}</i>
            </div>
            <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-1">{{ category.name }}</h3>
            <p class="text-xs text-gray-500 dark:text-gray-400">{{ category.count }} sản phẩm</p>
          </router-link>
        </div>
      </div>
    </section>

    <!-- Flash Sale / Hot Deals -->
    <section class="py-12 md:py-16 bg-gradient-to-r from-orange-500 via-red-500 to-pink-500 dark:from-orange-600 dark:via-red-600 dark:to-pink-600">
      <div class="max-w-7xl mx-auto px-4">
        <div class="bg-white dark:bg-gray-800 rounded-2xl p-6 md:p-8 border-4 border-white/20 shadow-2xl">
          <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 bg-orange-500 rounded-xl flex items-center justify-center">
                <i class="material-icons text-white text-2xl">flash_on</i>
              </div>
              <div>
                <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100">Flash Sale</h2>
                <div class="flex items-center gap-2 mt-2">
                  <span class="text-sm text-gray-600 dark:text-gray-400">Kết thúc trong:</span>
                  <div class="flex items-center gap-1">
                    <div class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-sm font-bold text-gray-900 dark:text-gray-100">12</div>
                    <span class="text-gray-600 dark:text-gray-400">:</span>
                    <div class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-sm font-bold text-gray-900 dark:text-gray-100">34</div>
                    <span class="text-gray-600 dark:text-gray-400">:</span>
                    <div class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-sm font-bold text-gray-900 dark:text-gray-100">56</div>
                  </div>
                </div>
              </div>
            </div>
            <router-link 
              to="/home/products?sale=true" 
              class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-xl font-semibold hover:from-orange-600 hover:to-red-600 transition-all duration-200 shadow-lg hover:shadow-xl"
            >
              Xem tất cả
              <i class="material-icons">arrow_forward</i>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Products Section -->
    <section class="py-12 md:py-16 bg-gray-50 dark:bg-gray-900">
      <div class="max-w-7xl mx-auto px-4">
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">Sản Phẩm Nổi Bật</h2>
          <p class="text-gray-600 dark:text-gray-400">Những mẫu giày được yêu thích nhất</p>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div v-for="n in 8" :key="n" class="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden animate-pulse">
            <div class="h-64 bg-gray-200 dark:bg-gray-700"></div>
            <div class="p-4 space-y-3">
              <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
              <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/2"></div>
            </div>
          </div>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="text-center py-16">
          <i class="material-icons text-6xl text-red-500 mb-4">error_outline</i>
          <h3 class="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-4">{{ error }}</h3>
          <button 
            @click="fetchProducts(currentPage)" 
            class="inline-flex items-center gap-2 px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors"
          >
            <i class="material-icons">refresh</i>
            Thử lại
          </button>
        </div>

        <!-- Products Grid -->
        <div v-else-if="products.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div
            v-for="product in products"
            :key="product.id"
            class="group bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 hover:shadow-lg hover:scale-105 transition-all duration-200 overflow-hidden"
          >
            <ProductCard :product="product" />
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-16">
          <i class="material-icons text-6xl text-gray-400 mb-4">inventory_2</i>
          <h3 class="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có sản phẩm nào</h3>
          <p class="text-gray-600 dark:text-gray-400">Vui lòng quay lại sau</p>
        </div>

        <!-- Pagination -->
        <div v-if="!loading && !error && totalPages > 1" class="flex items-center justify-center gap-2 mt-8">
          <button 
            @click="handlePageChange(currentPage - 1)" 
            :disabled="currentPage === 0"
            class="px-4 py-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <i class="material-icons">chevron_left</i>
            Trước
          </button>
          
          <div class="flex gap-2">
            <button 
              v-for="page in displayPages" 
              :key="page"
              @click="handlePageChange(page - 1)"
              :class="[
                'px-4 py-2 rounded-lg font-medium transition-colors',
                currentPage === page - 1
                  ? 'bg-purple-600 text-white'
                  : 'bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700'
              ]"
            >
              {{ page }}
            </button>
          </div>
          
          <button 
            @click="handlePageChange(currentPage + 1)" 
            :disabled="currentPage >= totalPages - 1"
            class="px-4 py-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Sau
            <i class="material-icons">chevron_right</i>
          </button>
        </div>
      </div>
    </section>

    <!-- Brands Section -->
    <section class="py-12 md:py-16 bg-white dark:bg-gray-800">
      <div class="max-w-7xl mx-auto px-4">
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">Thương Hiệu Đối Tác</h2>
          <p class="text-gray-600 dark:text-gray-400">Chính hãng từ các thương hiệu hàng đầu</p>
        </div>
        
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
          <div 
            v-for="brand in brands" 
            :key="brand.id" 
            class="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-6 hover:shadow-lg hover:scale-105 transition-all duration-200 flex items-center justify-center"
          >
            <img :src="brand.logo || '/placeholder-brand.png'" :alt="brand.name" class="max-h-12 w-auto opacity-60 hover:opacity-100 transition-opacity" />
          </div>
        </div>
      </div>
    </section>

    <!-- Testimonials Section -->
    <TestimonialsSection />

    <!-- Newsletter Section -->
    <section class="py-12 md:py-16 bg-gradient-to-r from-purple-600 to-indigo-600 dark:from-gray-800 dark:to-gray-900">
      <div class="max-w-7xl mx-auto px-4">
        <div class="bg-white dark:bg-gray-800 rounded-2xl p-8 md:p-12 shadow-xl border border-gray-200 dark:border-gray-700">
          <div class="flex flex-col md:flex-row md:items-center gap-6">
            <div class="flex items-center gap-4 flex-shrink-0">
              <div class="w-16 h-16 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl flex items-center justify-center">
                <i class="material-icons text-white text-3xl">email</i>
              </div>
              <div>
                <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">Đăng Ký Nhận Tin</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">Nhận thông báo về sản phẩm mới và ưu đãi đặc biệt</p>
              </div>
            </div>
            <div class="flex-1 flex flex-col sm:flex-row gap-3">
              <div class="flex-1 relative">
                <i class="material-icons absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">mail_outline</i>
                <input 
                  type="email" 
                  placeholder="Nhập email của bạn..." 
                  v-model="newsletterEmail"
                  class="w-full pl-10 pr-4 py-3 bg-gray-50 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-900 dark:text-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                />
              </div>
              <button class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all duration-200 shadow-lg hover:shadow-xl flex items-center justify-center gap-2">
                <i class="material-icons">send</i>
                Đăng Ký
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import ProductService from '@/services/productService';
import ProductCard from '@/assets/components/products/ProductCard.vue';
import TestimonialsSection from '@/assets/components/common/TestimonialsSection.vue';

// Categories data
const categories = ref([
  { id: 1, name: 'Running', slug: 'running', icon: 'directions_run', count: 156, gradient: 'var(--gradient-purple)' },
  { id: 2, name: 'Basketball', slug: 'basketball', icon: 'sports_basketball', count: 89, gradient: 'var(--gradient-orange)' },
  { id: 3, name: 'Casual', slug: 'casual', icon: 'weekend', count: 234, gradient: 'var(--gradient-blue)' },
  { id: 4, name: 'Training', slug: 'training', icon: 'fitness_center', count: 112, gradient: 'var(--gradient-green)' },
  { id: 5, name: 'Skateboarding', slug: 'skateboarding', icon: 'skateboarding', count: 67, gradient: 'var(--gradient-pink)' },
  { id: 6, name: 'Limited Edition', slug: 'limited', icon: 'stars', count: 45, gradient: 'var(--gradient-sunset)' },
]);

// Brands data
const brands = ref([
  { id: 1, name: 'Nike', logo: null },
  { id: 2, name: 'Adidas', logo: null },
  { id: 3, name: 'Jordan', logo: null },
  { id: 4, name: 'Puma', logo: null },
  { id: 5, name: 'New Balance', logo: null },
  { id: 6, name: 'Vans', logo: null },
]);

// Products state
const products = ref([]);
const loading = ref(false);
const error = ref(null);
const currentPage = ref(0);
const pageSize = ref(12);
const totalProducts = ref(0);
const totalPages = ref(0);
const newsletterEmail = ref('');

// Computed
const displayPages = computed(() => {
  const pages = [];
  const maxPages = 5;
  let start = Math.max(0, currentPage.value - 2);
  let end = Math.min(totalPages.value, start + maxPages);
  
  if (end - start < maxPages) {
    start = Math.max(0, end - maxPages);
  }
  
  for (let i = start; i < end; i++) {
    pages.push(i + 1);
  }
  
  return pages;
});

// Methods
const fetchProducts = async (page = 0) => {
  loading.value = true;
  error.value = null;
  
  try {
    const response = await ProductService.getAllProducts(page, pageSize.value);
    products.value = response.data.content || [];
    totalProducts.value = response.data.totalElements || 0;
    totalPages.value = response.data.totalPages || 0;
  } catch (err) {
    error.value = err.message || 'Không thể tải sản phẩm. Vui lòng thử lại sau.';
    console.error('Error fetching products:', err);
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    currentPage.value = newPage;
    fetchProducts(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

// Lifecycle
onMounted(() => {
  fetchProducts(currentPage.value);
});
</script>

<style>
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}
</style>

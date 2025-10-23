<template>
  <div class="user-page ecommerce-home">
    <!-- Hero Banner Section -->
    <section class="hero-banner">
      <div class="hero-container">
        <div class="hero-content">
          <div class="hero-badge badge-enhanced badge-pulse">
            <i class="material-icons">local_fire_department</i>
            <span>Bán chạy nhất 2025</span>
                </div>
          <h1 class="hero-title">
            Sneakery Store
            <span class="gradient-text">Giày Sneaker Chính Hãng</span>
          </h1>
          <p class="hero-description">
            Bộ sưu tập giày sneaker đa dạng từ Nike, Adidas, Jordan và nhiều thương hiệu nổi tiếng khác. 
            Giá tốt nhất thị trường - Miễn phí vận chuyển - Đổi trả dễ dàng.
          </p>
          <div class="hero-actions">
            <router-link to="/products" class="btn-gradient btn-enhanced btn-hero-primary">
              <i class="material-icons">shopping_bag</i>
              Mua Sắm Ngay
            </router-link>
            <router-link to="/products" class="btn-enhanced btn-hero-secondary">
              <i class="material-icons">explore</i>
              Khám Phá
                  </router-link>
                </div>
          
          <!-- Trust Badges -->
          <div class="trust-badges">
            <div class="trust-item">
              <i class="material-icons">verified</i>
              <span>100% Chính Hãng</span>
            </div>
            <div class="trust-item">
              <i class="material-icons">local_shipping</i>
              <span>Miễn Phí Ship</span>
            </div>
            <div class="trust-item">
              <i class="material-icons">sync</i>
              <span>Đổi Trả 30 Ngày</span>
            </div>
            <div class="trust-item">
              <i class="material-icons">support_agent</i>
              <span>Hỗ Trợ 24/7</span>
            </div>
          </div>
        </div>
        
        <div class="hero-image">
          <div class="hero-image-wrapper card-enhanced animate-float">
            <img src="@/assets/images/logo.png" alt="Sneakery Store" />
            
            <!-- Floating Stats -->
            <div class="floating-stat floating-stat-1 card-enhanced">
              <div class="stat-icon" style="background: var(--gradient-green);">
                <i class="material-icons">trending_up</i>
              </div>
              <div>
                <div class="stat-value">1000+</div>
                <div class="stat-label">Sản phẩm</div>
                  </div>
                </div>
            
            <div class="floating-stat floating-stat-2 card-enhanced">
              <div class="stat-icon" style="background: var(--gradient-pink);">
                <i class="material-icons">star</i>
              </div>
              <div>
                <div class="stat-value">4.9/5</div>
                <div class="stat-label">Đánh giá</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories Section -->
    <section class="categories-section">
      <div class="container">
        <div class="section-header-simple">
          <h2>Danh Mục Sản Phẩm</h2>
          <p>Khám phá bộ sưu tập theo danh mục</p>
        </div>
        
        <div class="categories-grid">
          <router-link 
            v-for="category in categories" 
            :key="category.id"
            :to="`/products?category=${category.slug}`"
            class="category-card card-enhanced hover-lift hover-glow"
          >
            <div class="category-icon" :style="{ background: category.gradient }">
              <i class="material-icons">{{ category.icon }}</i>
            </div>
            <h3 class="category-name">{{ category.name }}</h3>
            <p class="category-count">{{ category.count }} sản phẩm</p>
          </router-link>
        </div>
      </div>
    </section>

    <!-- Flash Sale / Hot Deals -->
    <section class="flash-sale-section">
      <div class="container">
        <div class="flash-sale-header card-gradient-border">
          <div class="card-gradient-border-inner">
            <div class="flash-sale-title">
              <i class="material-icons">flash_on</i>
              <h2>Flash Sale</h2>
              <div class="sale-timer">
                <span class="timer-label">Kết thúc trong:</span>
                <div class="timer-digits">
                  <div class="timer-digit">
                    <span>12</span>
                    <small>Giờ</small>
                  </div>
                  <span class="timer-separator">:</span>
                  <div class="timer-digit">
                    <span>34</span>
                    <small>Phút</small>
                  </div>
                  <span class="timer-separator">:</span>
                  <div class="timer-digit">
                    <span>56</span>
                    <small>Giây</small>
                  </div>
                </div>
              </div>
            </div>
            <router-link to="/products?sale=true" class="view-all-link">
              Xem tất cả
              <i class="material-icons">arrow_forward</i>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Products Section -->
    <section class="products-section">
      <div class="container">
        <div class="section-header-simple">
          <h2>Sản Phẩm Nổi Bật</h2>
          <p>Những mẫu giày được yêu thích nhất</p>
      </div>

        <!-- Loading State - Dark -->
        <div v-if="loading" class="products-grid">
          <div v-for="n in 8" :key="n" class="skeleton-card-dark">
            <div class="skeleton-image-dark"></div>
            <div class="skeleton-content-dark">
              <div class="skeleton-line-dark"></div>
              <div class="skeleton-line-dark"></div>
            </div>
          </div>
        </div>

        <!-- Error State - Dark -->
        <div v-else-if="error" class="empty-state-dark">
          <i class="material-icons" style="font-size: 4rem; color: #ef4444;">error_outline</i>
          <h3>{{ error }}</h3>
          <button @click="fetchProducts(currentPage)" class="btn-retry-dark">
            <i class="material-icons">refresh</i>
            Thử lại
          </button>
    </div>

        <!-- Products Grid -->
        <div v-else-if="products.length > 0" class="products-grid">
        <div
          v-for="product in products"
          :key="product.id"
            class="product-item card-enhanced hover-lift hover-glow"
        >
          <ProductCard :product="product" />
        </div>
      </div>

        <!-- Empty State - Dark -->
        <div v-else class="empty-state-dark">
          <i class="material-icons" style="font-size: 4rem; color: #64748b;">inventory_2</i>
          <h3>Chưa có sản phẩm nào</h3>
          <p>Vui lòng quay lại sau</p>
        </div>

        <!-- Pagination -->
        <div v-if="!loading && !error && totalPages > 1" class="pagination-container">
          <button 
            @click="handlePageChange(currentPage - 1)" 
            :disabled="currentPage === 0"
            class="btn-pagination"
          >
            <i class="material-icons">chevron_left</i>
            Trước
          </button>
          
          <div class="pagination-pages">
            <button 
              v-for="page in displayPages" 
              :key="page"
              @click="handlePageChange(page - 1)"
              :class="['pagination-page', { 'active': currentPage === page - 1 }]"
            >
              {{ page }}
            </button>
          </div>
          
          <button 
            @click="handlePageChange(currentPage + 1)" 
            :disabled="currentPage >= totalPages - 1"
            class="btn-pagination"
          >
            Sau
            <i class="material-icons">chevron_right</i>
          </button>
        </div>
      </div>
    </section>

    <!-- Brands Section -->
    <section class="brands-section">
      <div class="container">
        <div class="section-header-simple">
          <h2>Thương Hiệu Đối Tác</h2>
          <p>Chính hãng từ các thương hiệu hàng đầu</p>
        </div>
        
        <div class="brands-grid">
          <div v-for="brand in brands" :key="brand.id" class="brand-item card-enhanced hover-scale">
            <img :src="brand.logo || '/placeholder-brand.png'" :alt="brand.name" />
          </div>
        </div>
      </div>
    </section>

    <!-- Newsletter Section -->
    <section class="newsletter-section">
      <div class="container">
        <div class="newsletter-card card-enhanced">
          <div class="newsletter-content">
            <div class="newsletter-icon">
              <i class="material-icons">email</i>
            </div>
            <div class="newsletter-text">
              <h3>Đăng Ký Nhận Tin</h3>
              <p>Nhận thông báo về sản phẩm mới và ưu đãi đặc biệt</p>
            </div>
            <div class="newsletter-form">
              <div class="input-wrapper">
                <i class="material-icons input-icon">mail_outline</i>
                <input 
                  type="email" 
                  placeholder="Nhập email của bạn..." 
                  class="input-enhanced input-with-icon"
                  v-model="newsletterEmail"
                />
              </div>
              <button class="btn-gradient btn-enhanced">
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

<style scoped>
/* ===== E-COMMERCE HOME - Áp dụng triệt để style.css ===== */

/* Page Background - Dark Theme - Dễ nhìn không lóa mắt */
.ecommerce-home {
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  min-height: 100vh;
  color: #e2e8f0;
}

/* Container */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
}

/* ===== HERO BANNER - Dark Theme ===== */
.hero-banner {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 50%, #1e293b 100%);
  padding: var(--space-20) 0;
  position: relative;
  overflow: hidden;
}

.hero-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 700px;
  height: 700px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.12) 50%, transparent 70%);
  border-radius: 50%;
  filter: blur(100px);
  pointer-events: none;
}

.hero-banner::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(79, 172, 254, 0.15) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
}

.hero-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-12);
  align-items: center;
}

.hero-badge {
  display: inline-flex;
  margin-bottom: var(--space-4);
}

.hero-title {
  font-size: 3.5rem;
  font-weight: var(--font-bold);
  line-height: 1.2;
  margin-bottom: var(--space-6);
  color: #f1f5f9;
}

.hero-title .gradient-text {
  display: block;
  margin-top: var(--space-2);
}

.hero-description {
  font-size: var(--text-lg);
  color: #cbd5e1;
  line-height: 1.8;
  margin-bottom: var(--space-8);
  max-width: 600px;
}

.hero-actions {
  display: flex;
  gap: var(--space-4);
  margin-bottom: var(--space-12);
  flex-wrap: wrap;
}

.btn-hero-primary,
.btn-hero-secondary {
  padding: 1rem 2rem;
  font-size: 1.125rem;
  font-weight: var(--font-semibold);
  display: inline-flex;
  align-items: center;
  gap: var(--space-3);
  text-decoration: none;
  border-radius: var(--radius-xl);
  transition: var(--transition-smooth);
  border: 2px solid transparent;
}

.btn-hero-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: var(--white);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.btn-hero-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.btn-hero-primary:hover::before {
  left: 100%;
}

.btn-hero-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
}

.btn-hero-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: #f1f5f9;
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
}

.btn-hero-secondary:hover {
  border-color: #a78bfa;
  color: #c4b5fd;
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(167, 139, 250, 0.3);
  background: rgba(167, 139, 250, 0.15);
}

.trust-badges {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
}

.trust-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: #94a3b8;
}

.trust-item .material-icons {
  color: #a78bfa;
  font-size: 1.25rem;
}

/* Hero Image - Dark */
.hero-image-wrapper {
  position: relative;
  padding: var(--space-12);
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.8) 0%, rgba(15, 23, 42, 0.6) 100%);
  backdrop-filter: blur(30px);
  border-radius: var(--radius-3xl);
  border: 1px solid rgba(167, 139, 250, 0.2);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.hero-image-wrapper img {
  width: 100%;
  height: auto;
  display: block;
}

.floating-stat {
  position: absolute;
  padding: var(--space-3) var(--space-4);
  display: flex;
  align-items: center;
  gap: var(--space-3);
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.95) 0%, rgba(15, 23, 42, 0.9) 100%);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(167, 139, 250, 0.3);
  border-radius: var(--radius-xl);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.5);
  animation: float-gentle 3s ease-in-out infinite;
}

.floating-stat-1 {
  top: 10%;
  left: -5%;
}

.floating-stat-2 {
  bottom: 15%;
  right: -5%;
  animation-delay: 0.5s;
}

.floating-stat .stat-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--white);
}

.stat-value {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: #f1f5f9;
}

.stat-label {
  font-size: var(--text-sm);
  color: #94a3b8;
}

/* ===== SECTIONS - Dark Theme ===== */
.categories-section {
  padding: var(--space-16) 0;
  background: transparent;
}

.products-section {
  padding: var(--space-16) 0;
  background: linear-gradient(180deg, transparent 0%, rgba(15, 23, 42, 0.5) 100%);
}

.flash-sale-section {
  padding: var(--space-16) 0;
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.6) 0%, rgba(15, 23, 42, 0.4) 100%);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  position: relative;
}

.flash-sale-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(167, 139, 250, 0.5), transparent);
}

.brands-section {
  padding: var(--space-16) 0;
  background: linear-gradient(180deg, rgba(30, 41, 59, 0.3) 0%, transparent 100%);
}

.newsletter-section {
  padding: var(--space-16) 0;
  background: linear-gradient(180deg, transparent 0%, rgba(15, 23, 42, 0.6) 100%);
}

.section-header-simple {
  text-align: center;
  margin-bottom: var(--space-12);
}

.section-header-simple h2 {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: #f1f5f9;
  margin-bottom: var(--space-2);
}

.section-header-simple p {
  font-size: var(--text-lg);
  color: #94a3b8;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: var(--space-6);
}

.category-card {
  padding: var(--space-6);
  text-align: center;
  text-decoration: none;
  color: inherit;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(30, 41, 59, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(167, 139, 250, 0.15);
}

.category-card:hover {
  background: rgba(30, 41, 59, 0.8);
  border-color: rgba(167, 139, 250, 0.4);
}

.category-icon {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-2xl);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--space-4);
  color: var(--white);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  transition: var(--transition-smooth);
  position: relative;
  overflow: hidden;
}

.category-icon::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transform: rotate(45deg);
  transition: all 0.6s;
}

.category-card:hover .category-icon::after {
  left: 100%;
}

.category-card:hover .category-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.25);
}

.category-icon .material-icons {
  font-size: 2.5rem;
}

.category-name {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-1);
  color: #f1f5f9;
}

.category-count {
  font-size: var(--text-sm);
  color: #94a3b8;
}

/* ===== FLASH SALE - Dark ===== */
.flash-sale-header {
  padding: 0 !important;
  margin-bottom: var(--space-8);
  background: transparent !important;
  backdrop-filter: none !important;
  border: none !important;
}

.flash-sale-title {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  flex: 1;
}

.flash-sale-title h2 {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: #f1f5f9;
}

.flash-sale-title .material-icons {
  font-size: 2.5rem;
  color: #ffa500;
}

.sale-timer {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.timer-digits {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.timer-digit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: var(--white);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  text-align: center;
  min-width: 50px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.timer-digit span {
  display: block;
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
}

.timer-digit small {
  display: block;
  font-size: var(--text-xs);
  opacity: 0.9;
}

.timer-separator {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: #e2e8f0;
}

.view-all-link {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  color: #c4b5fd;
  text-decoration: none;
  font-weight: var(--font-semibold);
}

.view-all-link:hover {
  color: #a78bfa;
}

/* ===== PRODUCTS GRID ===== */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-12);
}

.product-item {
  transition: var(--transition-smooth);
}

/* ===== BRANDS SECTION - Dark ===== */
.brands-section {
  background: transparent;
}

.brands-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: var(--space-6);
}

.brand-item {
  padding: var(--space-8);
  background: rgba(30, 41, 59, 0.5);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(167, 139, 250, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100px;
  transition: var(--transition-smooth);
}

.brand-item:hover {
  background: rgba(30, 41, 59, 0.8);
  border-color: rgba(167, 139, 250, 0.3);
  box-shadow: 0 8px 20px rgba(167, 139, 250, 0.2);
}

.brand-item img {
  max-width: 100%;
  height: auto;
  filter: grayscale(100%);
  opacity: 0.6;
  transition: var(--transition-smooth);
}

.brand-item:hover img {
  filter: grayscale(0%);
  opacity: 1;
}

/* ===== NEWSLETTER - Dark ===== */
.newsletter-section {
  background: linear-gradient(180deg, transparent 0%, rgba(15, 23, 42, 0.8) 100%);
  position: relative;
}

.newsletter-card {
  padding: var(--space-12);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: var(--white);
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.newsletter-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: rotate-slow 20s linear infinite;
}

@keyframes rotate-slow {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.newsletter-content {
  display: grid;
  grid-template-columns: auto 1fr auto;
    gap: var(--space-8);
  align-items: center;
}

.newsletter-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-2xl);
  display: flex;
  align-items: center;
  justify-content: center;
}

.newsletter-icon .material-icons {
  font-size: 3rem;
}

.newsletter-text h3 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-2);
}

.newsletter-form {
  display: flex;
  gap: var(--space-4);
  max-width: 500px;
}

.newsletter-form .input-enhanced {
  background: rgba(255, 255, 255, 0.9);
  border-color: transparent;
}

.newsletter-form .btn-gradient {
  background: var(--white);
  color: var(--primary-color);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
}

.newsletter-form .btn-gradient:hover {
  background: #fafbfd;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

/* ===== PAGINATION ===== */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
}

.pagination-pages {
  display: flex;
  gap: var(--space-2);
}

.pagination-page {
  min-width: 40px;
  height: 40px;
  border: 2px solid rgba(148, 163, 184, 0.3);
  background: rgba(30, 41, 59, 0.6);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-md);
  color: #e2e8f0;
  cursor: pointer;
  transition: var(--transition-smooth);
  font-weight: var(--font-medium);
}

.pagination-page:hover {
  border-color: #a78bfa;
  color: #c4b5fd;
  background: rgba(30, 41, 59, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(167, 139, 250, 0.3);
}

.pagination-page.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: var(--white);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);
}

/* ===== EMPTY STATE - Dark ===== */
.empty-state,
.empty-state-dark {
  text-align: center;
  padding: var(--space-20) var(--space-8);
  background: transparent;
}

.empty-state h3,
.empty-state-dark h3 {
  font-size: var(--text-2xl);
  color: #f1f5f9;
  margin: var(--space-4) 0 var(--space-2);
}

.empty-state p,
.empty-state-dark p {
  color: #94a3b8;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .hero-container {
    grid-template-columns: 1fr;
    text-align: center;
    gap: var(--space-8);
  }
  
  .hero-title {
    font-size: 2.25rem;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .btn-hero-primary,
  .btn-hero-secondary {
    flex: 1;
    min-width: 140px;
    justify-content: center;
  }
  
  .trust-badges {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-3);
  }
  
  .floating-stat {
    display: none;
  }
  
  .newsletter-content {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .newsletter-icon {
    margin: 0 auto;
  }
  
  .newsletter-form {
    flex-direction: column;
    max-width: 100%;
  }
  
  .flash-sale-title {
    flex-direction: column;
    text-align: center;
  }
  
  .sale-timer {
    margin: 0;
  }
  
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--space-4);
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 1.875rem;
  }
  
  .hero-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .btn-hero-primary,
  .btn-hero-secondary {
    width: 100%;
  }
  
  .categories-grid {
    grid-template-columns: 1fr;
  }
}

/* ===== SKELETON LOADING - Dark Theme ===== */
.skeleton-card-dark {
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-xl);
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.skeleton-image-dark {
  width: 100%;
  aspect-ratio: 1 / 1;
  background: linear-gradient(
    90deg,
    rgba(15, 23, 42, 0.6) 0%,
    rgba(30, 41, 59, 0.8) 50%,
    rgba(15, 23, 42, 0.6) 100%
  );
  background-size: 200% 100%;
  animation: shimmer-dark 1.5s infinite;
}

.skeleton-content-dark {
  padding: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.skeleton-line-dark {
  height: 16px;
  background: linear-gradient(
    90deg,
    rgba(15, 23, 42, 0.6) 0%,
    rgba(30, 41, 59, 0.8) 50%,
    rgba(15, 23, 42, 0.6) 100%
  );
  background-size: 200% 100%;
  border-radius: var(--radius-md);
  animation: shimmer-dark 1.5s infinite;
}

.skeleton-line-dark:first-child {
  width: 60%;
}

.skeleton-line-dark:last-child {
  width: 80%;
}

@keyframes shimmer-dark {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* ===== PAGINATION BUTTONS - Dark ===== */
.btn-pagination {
  padding: var(--space-3) var(--space-5);
  background: rgba(30, 41, 59, 0.6);
  border: 2px solid rgba(148, 163, 184, 0.3);
  border-radius: var(--radius-md);
  color: #e2e8f0;
  font-weight: var(--font-medium);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  transition: var(--transition-smooth);
  backdrop-filter: blur(10px);
}

.btn-pagination:hover:not(:disabled) {
  background: rgba(30, 41, 59, 0.9);
  border-color: #a78bfa;
  color: #c4b5fd;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(167, 139, 250, 0.3);
}

.btn-pagination:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-pagination .material-icons {
  font-size: 1.25rem;
}

/* ===== ERROR/RETRY BUTTON - Dark ===== */
.btn-retry-dark {
  padding: var(--space-3) var(--space-6);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: var(--radius-xl);
  color: var(--white);
  font-weight: var(--font-semibold);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  transition: var(--transition-smooth);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  margin-top: var(--space-4);
}

.btn-retry-dark:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-retry-dark .material-icons {
  font-size: 1.25rem;
}

/* ===== OVERRIDE GLOBAL CLASSES TO DARK ===== */
.ecommerce-home .card-enhanced {
  background: rgba(30, 41, 59, 0.6) !important;
  border: 1px solid rgba(167, 139, 250, 0.15) !important;
  backdrop-filter: blur(10px);
}

.ecommerce-home .card-enhanced:hover {
  background: rgba(30, 41, 59, 0.8) !important;
  border-color: rgba(167, 139, 250, 0.3) !important;
}

.ecommerce-home .card-gradient-border {
  background: transparent !important;
}

.ecommerce-home .card-gradient-border-inner {
  background: rgba(30, 41, 59, 0.6) !important;
  border: 1px solid rgba(167, 139, 250, 0.2) !important;
  backdrop-filter: blur(10px);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: var(--space-4);
}

/* ===== ENSURE ALL SECTIONS DARK ===== */
.ecommerce-home section {
  background: transparent;
}

.ecommerce-home .container {
  background: transparent;
}

.ecommerce-home .product-item {
  background: transparent;
}

/* Override any white/light backgrounds from global CSS */
.ecommerce-home * {
  box-sizing: border-box;
}
</style>

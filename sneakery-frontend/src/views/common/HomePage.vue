<template>
  <div class="home-page">
    <!-- Slideshow Section -->
    <section class="slideshow-section">
      <div class="slideshow-container">
        <div class="slideshow-wrapper">
          <div 
            v-for="(slide, index) in slides" 
            :key="index"
            class="slide"
            :class="{ 'active': currentSlide === index }"
            :style="{ background: slide.gradient }"
          >
            <div class="slide-content">
              <div class="slide-pattern"></div>
              <div class="slide-text">
                <div class="slide-logo">
                  <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
                </div>
                <h1 class="slide-title">{{ slide.title }}</h1>
                <p class="slide-subtitle">{{ slide.subtitle }}</p>
                <div class="slide-actions">
                  <router-link to="/products" class="btn btn-primary btn-large">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ slide.buttonText }}
                  </router-link>
                </div>
              </div>
              <div class="slide-image">
                <div class="image-placeholder" :style="{ background: slide.gradient }">
                  <div class="sneaker-icon">
                    <svg width="120" height="120" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 2L13.09 8.26L20 9L13.09 9.74L12 16L10.91 9.74L4 9L10.91 8.26L12 2Z" fill="currentColor"/>
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Navigation Dots -->
        <div class="slideshow-dots">
          <button 
            v-for="(slide, index) in slides" 
            :key="index"
            class="dot"
            :class="{ 'active': currentSlide === index }"
            @click="goToSlide(index)"
          ></button>
        </div>
        
        <!-- Navigation Arrows -->
        <button class="slideshow-nav prev" @click="previousSlide">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button class="slideshow-nav next" @click="nextSlide">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </section>

    <!-- Products Section -->
    <section class="products-section">
      <div class="section-header">
        <h2 class="section-title">Sản Phẩm Nổi Bật</h2>
        <p class="section-subtitle">Những mẫu giày được yêu thích nhất</p>
      </div>

    <div v-if="loading" class="loading-container">
        <div class="skeleton-grid">
          <div v-for="n in 8" :key="n" class="skeleton-card">
            <div class="skeleton-image"></div>
            <div class="skeleton-content">
              <div class="skeleton-line skeleton-title"></div>
              <div class="skeleton-line skeleton-price"></div>
            </div>
          </div>
        </div>
    </div>

      <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="error-alert" />

      <div v-if="!loading && !error" class="products-grid">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-item"
        >
          <ProductCard :product="product" />
        </div>
      </div>

      <div v-if="!loading && !error" class="pagination-container">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="totalProducts"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="handlePageChange"
          class="custom-pagination"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import ProductService from '@/services/productService';
import ProductCard from '@/components/products/ProductCard.vue';

// Slideshow data
const slides = ref([
  {
    title: 'Khám phá bộ sưu tập giày sneaker',
    subtitle: 'Những mẫu giày thời trang và chất lượng cao từ các thương hiệu nổi tiếng',
    buttonText: 'Xem tất cả sản phẩm',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: 'Nike Air Max Collection',
    subtitle: 'Trải nghiệm sự thoải mái và phong cách với dòng giày Nike Air Max mới nhất',
    buttonText: 'Khám phá Nike',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: 'Adidas Originals',
    subtitle: 'Phong cách cổ điển với công nghệ hiện đại trong bộ sưu tập Adidas Originals',
    buttonText: 'Xem Adidas',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    title: 'Jordan Retro',
    subtitle: 'Tôn vinh di sản bóng rổ với những mẫu giày Jordan Retro đầy cảm hứng',
    buttonText: 'Khám phá Jordan',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
]);

const currentSlide = ref(0);
const autoSlideInterval = ref(null);

// Thêm các biến trạng thái cho phân trang
const products = ref([]);
const loading = ref(true);
const error = ref(null);
const currentPage = ref(1);
const totalProducts = ref(0);
const pageSize = ref(8); // Phải khớp với defaultValue ở backend

// Tách logic gọi API ra một hàm riêng để có thể tái sử dụng
const fetchProducts = async (page) => {
  loading.value = true;
  error.value = null;
  try {
    // API giờ đây cần page và size. Lưu ý: API của Spring bắt đầu từ trang 0.
    const response = await ProductService.getProducts(page - 1, pageSize.value);
    products.value = response.data.content; // Danh sách sản phẩm
    totalProducts.value = response.data.totalElements; // Tổng số sản phẩm
  } catch (err) {
    console.error("Lỗi khi lấy danh sách sản phẩm:", err);
    error.value = "Không thể tải được danh sách sản phẩm.";
  } finally {
    loading.value = false;
  }
};

// Slideshow functions
const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % slides.value.length;
};

const previousSlide = () => {
  currentSlide.value = currentSlide.value === 0 ? slides.value.length - 1 : currentSlide.value - 1;
};

const goToSlide = (index) => {
  currentSlide.value = index;
};

const startAutoSlide = () => {
  autoSlideInterval.value = setInterval(() => {
    nextSlide();
  }, 5000); // Chuyển slide mỗi 5 giây
};

const stopAutoSlide = () => {
  if (autoSlideInterval.value) {
    clearInterval(autoSlideInterval.value);
    autoSlideInterval.value = null;
  }
};

// Hàm được gọi khi người dùng click chuyển trang
const handlePageChange = (newPage) => {
  currentPage.value = newPage;
  fetchProducts(newPage);
};

// Gọi API lần đầu khi component được tải
onMounted(() => {
  fetchProducts(currentPage.value);
  startAutoSlide();
});

onUnmounted(() => {
  stopAutoSlide();
});
</script>

<style scoped>
/* ===== HOME PAGE ===== */
.home-page {
  min-height: 100vh;
  padding-top: 90px; /* Space for fixed navbar */
}

/* ===== SLIDESHOW SECTION ===== */
.slideshow-section {
  position: relative;
  height: 600px;
  margin-bottom: var(--space-20);
  overflow: hidden;
}

.slideshow-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.slideshow-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  transform: translateX(100%);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
}

.slide.active {
  opacity: 1;
  transform: translateX(0);
  z-index: 2;
}

.slide-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  height: 100%;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
  position: relative;
}

.slide-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
  z-index: 1;
}

.slide-text {
  padding-right: var(--space-8);
  animation: slideInUp 0.8s ease-out 0.3s both;
  position: relative;
  z-index: 2;
}

.slide-logo {
  margin-bottom: var(--space-6);
  animation: slideInUp 0.8s ease-out 0.2s both;
}

.slide-logo .logo-image {
  width: 100px;
  height: 100px;
  object-fit: contain;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
  transition: transform var(--transition-normal);
}

.slide-logo .logo-image:hover {
  transform: scale(1.05);
}

.slide-title {
  font-size: var(--text-5xl);
  font-weight: var(--font-bold);
  color: var(--white);
  margin-bottom: var(--space-6);
  line-height: var(--leading-tight);
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

.slide-subtitle {
  font-size: var(--text-xl);
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: var(--space-8);
  line-height: var(--leading-relaxed);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.slide-actions {
  display: flex;
  gap: var(--space-4);
  animation: slideInUp 0.8s ease-out 0.5s both;
}

.btn-large {
  padding: var(--space-4) var(--space-8);
  font-size: var(--text-lg);
  border-radius: var(--radius-2xl);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  transition: all var(--transition-normal);
}

.btn-large:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.4);
}

.slide-image {
  display: flex;
  justify-content: center;
  align-items: center;
  animation: slideInScale 0.8s ease-out 0.4s both;
  position: relative;
  z-index: 2;
}

.image-placeholder {
  width: 400px;
  height: 400px;
  border-radius: var(--radius-3xl);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.image-placeholder::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.sneaker-icon {
  position: relative;
  z-index: 1;
  animation: float 3s ease-in-out infinite;
}

.sneaker-icon svg {
  color: rgba(255, 255, 255, 0.9);
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
}

/* Navigation Dots */
.slideshow-dots {
  position: absolute;
  bottom: var(--space-8);
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: var(--space-3);
  z-index: 10;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.5);
  background: transparent;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.dot:hover {
  border-color: rgba(255, 255, 255, 0.8);
  transform: scale(1.1);
}

.dot.active {
  background: var(--white);
  border-color: var(--white);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

/* Navigation Arrows */
.slideshow-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  color: var(--white);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
  backdrop-filter: blur(10px);
  z-index: 10;
}

.slideshow-nav:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.6);
  transform: translateY(-50%) scale(1.1);
}

.slideshow-nav.prev {
  left: var(--space-8);
}

.slideshow-nav.next {
  right: var(--space-8);
}

/* Animations */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInScale {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* ===== PRODUCTS SECTION ===== */
.products-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4) var(--space-20);
}

.section-header {
  text-align: center;
  margin-bottom: var(--space-16);
}

.section-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

.section-subtitle {
  font-size: var(--text-lg);
  color: var(--text-secondary);
  max-width: 600px;
  margin: 0 auto;
}

/* ===== LOADING SKELETON ===== */
.loading-container {
  margin: var(--space-8) 0;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-6);
}

.skeleton-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-4);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.skeleton-image {
  width: 100%;
  height: 200px;
  background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-4);
}

.skeleton-content {
  padding: 0 var(--space-2);
}

.skeleton-line {
  height: 16px;
  background: linear-gradient(90deg, var(--bg-tertiary) 25%, var(--bg-secondary) 50%, var(--bg-tertiary) 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: var(--radius-sm);
  margin-bottom: var(--space-2);
}

.skeleton-title {
  width: 80%;
}

.skeleton-price {
  width: 40%;
}

@keyframes skeleton-loading {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

/* ===== PRODUCTS GRID ===== */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-12);
}

.product-item {
  transition: transform var(--transition-normal);
}

.product-item:hover {
  transform: translateY(-4px);
}

/* ===== PAGINATION ===== */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: var(--space-12);
}

.custom-pagination {
  --el-pagination-bg-color: var(--bg-card);
  --el-pagination-text-color: var(--text-primary);
  --el-pagination-border-radius: var(--radius-lg);
  --el-pagination-button-bg-color: var(--bg-secondary);
  --el-pagination-button-color: var(--text-secondary);
  --el-pagination-button-hover-color: var(--primary-color);
  --el-pagination-button-hover-bg-color: var(--primary-light);
}

/* ===== ERROR ALERT ===== */
.error-alert {
  margin: var(--space-8) 0;
  border-radius: var(--radius-lg);
}

/* ===== RESPONSIVE DESIGN ===== */
@media (max-width: 768px) {
  .slideshow-section {
    height: 500px;
  }
  
  .slide-content {
    grid-template-columns: 1fr;
    text-align: center;
    gap: var(--space-8);
    padding: var(--space-8) var(--space-4);
  }
  
  .slide-text {
    padding-right: 0;
    order: 2;
  }
  
  .slide-image {
    order: 1;
  }
  
  .slide-logo .logo-image {
    width: 80px;
    height: 80px;
  }
  
  .slide-title {
    font-size: var(--text-3xl);
  }
  
  .slide-subtitle {
    font-size: var(--text-lg);
  }
  
  .image-placeholder {
    width: 250px;
    height: 250px;
  }
  
  .sneaker-icon svg {
    width: 80px;
    height: 80px;
  }
  
  .slideshow-nav {
    width: 40px;
    height: 40px;
  }
  
  .slideshow-nav.prev {
    left: var(--space-4);
  }
  
  .slideshow-nav.next {
    right: var(--space-4);
  }
  
  .skeleton-grid,
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: var(--space-4);
  }
}

@media (max-width: 480px) {
  .slideshow-section {
    height: 450px;
  }
  
  .slide-logo .logo-image {
    width: 60px;
    height: 60px;
  }
  
  .slide-title {
    font-size: var(--text-2xl);
  }
  
  .slide-subtitle {
    font-size: var(--text-base);
  }
  
  .btn-large {
    padding: var(--space-3) var(--space-6);
    font-size: var(--text-base);
  }
  
  .image-placeholder {
    width: 200px;
    height: 200px;
  }
  
  .sneaker-icon svg {
    width: 60px;
    height: 60px;
  }
  
  .slideshow-nav {
    width: 35px;
    height: 35px;
  }
  
  .slideshow-nav.prev {
    left: var(--space-2);
  }
  
  .slideshow-nav.next {
    right: var(--space-2);
  }
  
  .skeleton-grid,
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>
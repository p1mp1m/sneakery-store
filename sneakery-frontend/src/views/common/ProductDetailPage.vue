<template>
  <div class="user-page product-detail-page">
    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner-lg"></div>
      <p>Đang tải thông tin sản phẩm...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <h2>❌ Không tìm thấy sản phẩm</h2>
      <p>{{ error }}</p>
      <button @click="router.push('/products')" class="btn btn-primary">
        Quay lại danh sách sản phẩm
      </button>
    </div>

    <!-- Product Detail -->
    <div v-else-if="product" class="product-detail-container">
      <!-- Breadcrumb -->
      <nav class="breadcrumb">
        <router-link to="/">Trang chủ</router-link>
        <span class="separator">/</span>
        <router-link to="/products">Sản phẩm</router-link>
        <span class="separator">/</span>
        <span class="current">{{ product.name }}</span>
      </nav>

      <!-- Product Main Info -->
      <div class="product-main-grid">
        <!-- Product Gallery with Zoom -->
        <div class="product-gallery">
          <div class="gallery-main">
            <img 
              :src="selectedImage || product.imageUrl" 
              :alt="product.name"
              @click="openZoom"
              class="main-image-zoom"
            />
            <button class="zoom-indicator" @click="openZoom" title="Click để phóng to">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"></circle>
                <path d="m21 21-4.35-4.35"></path>
                <line x1="11" x2="11" y1="8" y2="14"></line>
                <line x1="8" x2="14" y1="11" y2="11"></line>
              </svg>
            </button>
          </div>
          
          <div class="thumbnail-images" v-if="product.variants && product.variants.length > 0">
            <img
              v-for="(variant, index) in product.variants"
              :key="index"
              :src="variant.imageUrl || product.imageUrl"
              :alt="`${product.name} - ${variant.color}`"
              :class="{ active: selectedImage === variant.imageUrl }"
              @click="selectImage(variant.imageUrl)"
            />
          </div>
        </div>

        <!-- Product Info -->
        <div class="product-info">
          <!-- Header with Brand & Rating -->
          <div class="product-header">
            <span class="product-brand-badge">{{ product.brandName }}</span>
            <div class="product-rating">
              <div class="stars">
                <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= averageRating }">★</span>
              </div>
              <span class="rating-count">({{ reviews.length }} đánh giá)</span>
            </div>
          </div>

          <h1 class="product-name">{{ product.name }}</h1>

          <!-- Price Section -->
          <div class="product-price-section">
            <span class="current-price">{{ formatPrice(currentPrice) }}</span>
            <span v-if="originalPrice > currentPrice" class="original-price">
              {{ formatPrice(originalPrice) }}
            </span>
            <span v-if="originalPrice > currentPrice" class="discount-badge-new">
              -{{ Math.round(((originalPrice - currentPrice) / originalPrice) * 100) }}%
            </span>
          </div>

          <!-- Description -->
          <div class="product-description">
            <p>{{ product.description || 'Chưa có mô tả chi tiết.' }}</p>
          </div>

          <!-- Color Selection -->
          <div class="selection-group">
            <label class="selection-label">
              Màu sắc: <span class="selected-value">{{ selectedColor || 'Chọn màu' }}</span>
            </label>
            <div class="color-options">
              <button
                v-for="color in availableColors"
                :key="color"
                :class="['color-btn-new', { active: selectedColor === color }]"
                @click="selectColor(color)"
              >
                {{ color }}
              </button>
            </div>
          </div>

          <!-- Size Selection -->
          <div class="selection-group">
            <label class="selection-label">
              Kích cỡ: <span class="selected-value">{{ selectedSize || 'Chọn size' }}</span>
            </label>
            <div class="size-options">
              <button
                v-for="size in availableSizes"
                :key="size"
                :class="['size-btn-new', { active: selectedSize === size }]"
                @click="selectSize(size)"
              >
                {{ size }}
              </button>
            </div>
          </div>

          <!-- Stock Status -->
          <div class="stock-status-section">
            <span v-if="selectedVariant && selectedVariant.stockQuantity > 0" class="stock-status in-stock">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
              Còn hàng ({{ selectedVariant.stockQuantity }} sản phẩm)
            </span>
            <span v-else-if="selectedVariant" class="stock-status out-of-stock">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="15" y1="9" x2="9" y2="15"></line>
                <line x1="9" y1="9" x2="15" y2="15"></line>
              </svg>
              Hết hàng
            </span>
            <span v-else class="stock-status select-variant">Vui lòng chọn màu và size</span>
          </div>

          <!-- Quantity -->
          <div class="quantity-section">
            <label class="selection-label">Số lượng:</label>
            <div class="quantity-controls-new">
              <button @click="decreaseQuantity" :disabled="quantity <= 1" class="qty-btn">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
              </button>
              <input v-model.number="quantity" type="number" min="1" :max="maxQuantity" class="qty-input-new" />
              <button @click="increaseQuantity" :disabled="quantity >= maxQuantity" class="qty-btn">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
              </button>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="action-buttons-new">
            <button
              @click="addToCart"
              :disabled="!canAddToCart"
              class="btn btn-gradient btn-lg btn-block btn-add-cart-new"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="9" cy="21" r="1"></circle>
                <circle cx="20" cy="21" r="1"></circle>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
              </svg>
              {{ cartButtonText }}
            </button>
            <button @click="buyNow" :disabled="!canAddToCart" class="btn btn-outline btn-lg">
              Mua ngay
            </button>
          </div>

          <!-- Product Features -->
          <div class="product-features">
            <div class="feature-item">
              <div class="feature-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                  <circle cx="12" cy="10" r="3"></circle>
                </svg>
              </div>
              <div class="feature-text">
                <h4>Giao hàng miễn phí</h4>
                <p>Cho đơn hàng trên 500K</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="23 4 23 10 17 10"></polyline>
                  <polyline points="1 20 1 14 7 14"></polyline>
                  <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
                </svg>
              </div>
              <div class="feature-text">
                <h4>Đổi trả 30 ngày</h4>
                <p>Miễn phí đổi trả</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
              </div>
              <div class="feature-text">
                <h4>Thanh toán bảo mật</h4>
                <p>SSL 256-bit</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Product Tabs -->
      <div class="product-tabs-section">
        <div class="tab-buttons-new">
          <button
            :class="['tab-btn-new', { active: activeTab === 'specs' }]"
            @click="activeTab = 'specs'"
          >
            Thông số kỹ thuật
          </button>
          <button
            :class="['tab-btn-new', { active: activeTab === 'reviews' }]"
            @click="activeTab = 'reviews'"
          >
            Đánh giá ({{ reviews.length }})
          </button>
          <button
            :class="['tab-btn-new', { active: activeTab === 'shipping' }]"
            @click="activeTab = 'shipping'"
          >
            Giao hàng & Đổi trả
          </button>
        </div>

        <div class="tab-content-new">
          <!-- Specifications Tab -->
          <div v-if="activeTab === 'specs'" class="tab-pane">
            <h3 class="tab-pane-title">Thông tin chi tiết</h3>
            <table class="specs-table">
              <tr>
                <td class="spec-label">Thương hiệu</td>
                <td class="spec-value">{{ product.brandName }}</td>
              </tr>
              <tr>
                <td class="spec-label">Danh mục</td>
                <td class="spec-value">{{ formatCategories(product.categories) }}</td>
              </tr>
              <tr>
                <td class="spec-label">Màu sắc</td>
                <td class="spec-value">{{ availableColors.join(', ') }}</td>
              </tr>
              <tr>
                <td class="spec-label">Kích cỡ</td>
                <td class="spec-value">{{ availableSizes.join(', ') }}</td>
              </tr>
              <tr>
                <td class="spec-label">Chất liệu</td>
                <td class="spec-value">Canvas, Rubber Sole</td>
              </tr>
              <tr>
                <td class="spec-label">Xuất xứ</td>
                <td class="spec-value">Chính hãng 100%</td>
              </tr>
            </table>
          </div>

          <!-- Reviews Tab -->
          <div v-if="activeTab === 'reviews'" class="tab-pane">
            <div class="reviews-header">
              <h3 class="tab-pane-title">Đánh giá từ khách hàng</h3>
              <div class="reviews-summary-card">
                <div class="summary-rating">
                  <div class="big-rating">{{ averageRating.toFixed(1) }}</div>
                  <div class="stars-large">
                    <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= averageRating }">★</span>
                  </div>
                  <div class="rating-count">{{ reviews.length }} đánh giá</div>
                </div>
              </div>
            </div>

            <div class="reviews-list" v-if="reviews.length > 0">
              <div v-for="review in reviews" :key="review.id" class="review-card">
                <div class="review-header">
                  <div class="reviewer-info">
                    <div class="reviewer-avatar">{{ review.userName.charAt(0).toUpperCase() }}</div>
                    <div class="reviewer-details">
                      <div class="reviewer-name">{{ review.userName }}</div>
                      <div class="review-date">{{ formatDate(review.createdAt) }}</div>
                    </div>
                  </div>
                  <div class="review-stars">
                    <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= review.rating }">★</span>
                  </div>
                </div>
                <div class="review-content">{{ review.comment }}</div>
              </div>
            </div>

            <div v-else class="no-reviews">
              <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg>
              <p>Chưa có đánh giá nào cho sản phẩm này.</p>
              <button class="btn btn-primary">Viết đánh giá đầu tiên</button>
            </div>
          </div>

          <!-- Shipping Tab -->
          <div v-if="activeTab === 'shipping'" class="tab-pane">
            <h3 class="tab-pane-title">Chính sách giao hàng & đổi trả</h3>
            <div class="shipping-info">
              <div class="info-section">
                <h4>📦 Giao hàng</h4>
                <ul>
                  <li>Miễn phí giao hàng cho đơn hàng trên 500.000đ</li>
                  <li>Giao hàng tiêu chuẩn: 2-4 ngày làm việc</li>
                  <li>Giao hàng nhanh: 1-2 ngày làm việc (phụ thu 30.000đ)</li>
                </ul>
              </div>
              <div class="info-section">
                <h4>🔄 Đổi trả</h4>
                <ul>
                  <li>Đổi trả miễn phí trong vòng 30 ngày</li>
                  <li>Sản phẩm chưa qua sử dụng, còn nguyên tem mác</li>
                  <li>Hoàn tiền 100% nếu sản phẩm lỗi</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Related Products (Mock data for now) -->
      <div class="related-products-section" v-if="relatedProducts.length > 0">
        <h2 class="section-title">Sản phẩm tương tự</h2>
        <div class="related-products-grid">
          <div v-for="relatedProduct in relatedProducts" :key="relatedProduct.id" class="related-product-card">
            <div class="related-product-image">
              <img :src="relatedProduct.imageUrl || product.imageUrl" :alt="relatedProduct.name" />
            </div>
            <div class="related-product-info">
              <p class="related-brand">{{ relatedProduct.brandName || product.brandName }}</p>
              <h4 class="related-name">{{ relatedProduct.name }}</h4>
              <p class="related-price">{{ formatPrice(relatedProduct.price || product.price) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Zoom Modal -->
    <div v-if="showZoom" class="zoom-modal" @click="closeZoom">
      <button class="zoom-close" @click="closeZoom">×</button>
      <div class="zoom-content" @click.stop>
        <img :src="selectedImage || product.imageUrl" :alt="product.name" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// State
const product = ref(null);
const loading = ref(true);
const error = ref('');
const selectedColor = ref('');
const selectedSize = ref('');
const selectedImage = ref('');
const quantity = ref(1);
const activeTab = ref('specs');
const showZoom = ref(false);

// Mock reviews data (replace with real API later)
const reviews = ref([
  {
    id: 1,
    userName: 'Nguyễn Văn A',
    rating: 5,
    comment: 'Sản phẩm rất đẹp và chất lượng. Đóng gói cẩn thận, giao hàng nhanh.',
    createdAt: '2024-12-15T10:30:00'
  },
  {
    id: 2,
    userName: 'Trần Thị B',
    rating: 4,
    comment: 'Giày đẹp, đúng như hình. Tuy nhiên size hơi nhỏ một chút.',
    createdAt: '2024-12-10T15:20:00'
  },
  {
    id: 3,
    userName: 'Lê Văn C',
    rating: 5,
    comment: 'Mình đã mua 3 đôi rồi, rất hài lòng về chất lượng!',
    createdAt: '2024-12-05T09:15:00'
  }
]);

// Mock related products (replace with real API later)
const relatedProducts = ref([]);

// Fetch product detail
const fetchProduct = async () => {
  try {
    loading.value = true;
    error.value = '';
    
    const response = await axios.get(
      `http://localhost:8080/api/admin/products/${route.params.id}`
    );
    
    product.value = response.data;
    
    // Auto-select first variant
    if (product.value.variants && product.value.variants.length > 0) {
      const firstVariant = product.value.variants[0];
      selectedColor.value = firstVariant.color;
      selectedSize.value = firstVariant.size;
      selectedImage.value = firstVariant.imageUrl;
    }

    // Mock related products based on same brand
    relatedProducts.value = [
      { id: 101, name: 'Product 1', price: product.value.price * 1.1 },
      { id: 102, name: 'Product 2', price: product.value.price * 0.9 },
      { id: 103, name: 'Product 3', price: product.value.price * 1.2 },
      { id: 104, name: 'Product 4', price: product.value.price * 0.8 },
    ];
  } catch (err) {
    console.error('Error fetching product:', err);
    error.value = err.response?.data?.message || 'Không thể tải thông tin sản phẩm';
  } finally {
    loading.value = false;
  }
};

// Computed
const availableColors = computed(() => {
  if (!product.value || !product.value.variants) return [];
  return [...new Set(product.value.variants.map((v) => v.color))];
});

const availableSizes = computed(() => {
  if (!product.value || !product.value.variants) return [];
  return [...new Set(product.value.variants.map((v) => v.size))];
});

const selectedVariant = computed(() => {
  if (!product.value || !product.value.variants) return null;
  return product.value.variants.find(
    (v) => v.color === selectedColor.value && v.size === selectedSize.value
  );
});

const currentPrice = computed(() => {
  if (selectedVariant.value) {
    return selectedVariant.value.priceSale || selectedVariant.value.priceBase;
  }
  return 0;
});

const originalPrice = computed(() => {
  if (selectedVariant.value) {
    return selectedVariant.value.priceBase;
  }
  return 0;
});

const maxQuantity = computed(() => {
  return selectedVariant.value?.stockQuantity || 1;
});

const canAddToCart = computed(() => {
  return selectedVariant.value && selectedVariant.value.stockQuantity > 0;
});

const cartButtonText = computed(() => {
  if (!selectedColor.value || !selectedSize.value) return 'Chọn màu và size';
  if (!selectedVariant.value) return 'Không có sẵn';
  if (selectedVariant.value.stockQuantity <= 0) return 'Hết hàng';
  return 'Thêm vào giỏ hàng';
});

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0;
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0);
  return sum / reviews.value.length;
});

// Methods
const selectImage = (imageUrl) => {
  selectedImage.value = imageUrl;
};

const selectColor = (color) => {
  selectedColor.value = color;
  // Update image
  const variant = product.value.variants.find((v) => v.color === color);
  if (variant && variant.imageUrl) {
    selectedImage.value = variant.imageUrl;
  }
};

const selectSize = (size) => {
  selectedSize.value = size;
};

const increaseQuantity = () => {
  if (quantity.value < maxQuantity.value) {
    quantity.value++;
  }
};

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const openZoom = () => {
  showZoom.value = true;
};

const closeZoom = () => {
  showZoom.value = false;
};

const addToCart = async () => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng');
    router.push('/login');
    return;
  }

  if (!canAddToCart.value) {
    ElMessage.error('Sản phẩm này hiện không có sẵn');
    return;
  }

  try {
    await axios.post(
      'http://localhost:8080/api/cart/item',
      {
        variantId: selectedVariant.value.id,
        quantity: quantity.value,
      },
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`,
        },
      }
    );

    ElMessage.success(`Đã thêm ${quantity.value} sản phẩm vào giỏ hàng`);
  } catch (err) {
    console.error('Error adding to cart:', err);
    ElMessage.error(err.response?.data?.message || 'Không thể thêm vào giỏ hàng');
  }
};

const buyNow = async () => {
  await addToCart();
  router.push('/cart');
};

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(price);
};

const formatCategories = (categories) => {
  if (!categories || categories.length === 0) return 'Chưa phân loại';
  return categories.map((c) => c.name).join(', ');
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  });
};

// Lifecycle
onMounted(() => {
  fetchProduct();
});

watch(() => route.params.id, () => {
  if (route.params.id) {
    fetchProduct();
  }
});
</script>

<style scoped>
/* ===== PRODUCT DETAIL PAGE - MODERN DARK THEME ===== */

.product-detail-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-6);
  background: transparent;
}

/* Loading & Error */
.loading-container,
.error-container {
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  color: #e2e8f0;
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

/* Breadcrumb */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-6);
  font-size: var(--text-sm);
  color: #94a3b8;
}

.breadcrumb a {
  color: #94a3b8;
  text-decoration: none;
  transition: color var(--transition-fast);
}

.breadcrumb a:hover {
  color: #a78bfa;
}

.breadcrumb .separator {
  color: #64748b;
}

.breadcrumb .current {
  color: #c4b5fd;
  font-weight: var(--font-medium);
}

/* Product Main Grid */
.product-main-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-10);
  margin-bottom: var(--space-12);
}

/* Product Gallery */
.product-gallery {
  position: sticky;
  top: var(--space-6);
  height: fit-content;
}

.gallery-main {
  position: relative;
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-xl);
  overflow: hidden;
  margin-bottom: var(--space-4);
  aspect-ratio: 1;
  cursor: zoom-in;
}

.main-image-zoom {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.gallery-main:hover .main-image-zoom {
  transform: scale(1.05);
}

.zoom-indicator {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.zoom-indicator:hover {
  background: rgba(167, 139, 250, 0.8);
  transform: scale(1.1);
}

.thumbnail-images {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-3);
}

.thumbnail-images img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: var(--radius-lg);
  cursor: pointer;
  border: 2px solid rgba(167, 139, 250, 0.15);
  background: rgba(30, 41, 59, 0.4);
  transition: all var(--transition-fast);
}

.thumbnail-images img:hover {
  border-color: rgba(167, 139, 250, 0.5);
  transform: translateY(-2px);
}

.thumbnail-images img.active {
  border-color: #a78bfa;
  box-shadow: 0 0 20px rgba(167, 139, 250, 0.4);
}

/* Product Info */
.product-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
  flex-wrap: wrap;
}

.product-brand-badge {
  display: inline-block;
  padding: var(--space-2) var(--space-4);
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  color: white;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 1px;
  border-radius: var(--radius-full);
}

.product-rating {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  color: #475569;
  font-size: 18px;
  transition: color var(--transition-fast);
}

.star.filled {
  color: #fbbf24;
}

.rating-count {
  font-size: var(--text-sm);
  color: #94a3b8;
}

.product-name {
  font-size: 2.5rem;
  font-weight: var(--font-bold);
  color: #f1f5f9;
  line-height: 1.2;
  margin: 0;
}

.product-price-section {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-wrap: wrap;
  padding: var(--space-4);
  background: rgba(30, 41, 59, 0.4);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(167, 139, 250, 0.15);
}

.current-price {
  font-size: 2.25rem;
  font-weight: var(--font-bold);
  background: linear-gradient(135deg, #a78bfa 0%, #c4b5fd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.original-price {
  font-size: var(--text-xl);
  color: #64748b;
  text-decoration: line-through;
}

.discount-badge-new {
  padding: var(--space-1) var(--space-3);
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  border-radius: var(--radius-full);
}

.product-description {
  padding: var(--space-4);
  background: rgba(30, 41, 59, 0.4);
  border-left: 3px solid #a78bfa;
  border-radius: var(--radius-md);
  color: #cbd5e1;
  line-height: 1.6;
}

/* Selection Groups */
.selection-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.selection-label {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: #f1f5f9;
}

.selected-value {
  color: #a78bfa;
  font-weight: var(--font-bold);
}

.color-options,
.size-options {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.color-btn-new,
.size-btn-new {
  min-width: 60px;
  padding: var(--space-3) var(--space-4);
  background: rgba(30, 41, 59, 0.6);
  border: 2px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-md);
  color: #e2e8f0;
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.color-btn-new:hover,
.size-btn-new:hover {
  border-color: rgba(167, 139, 250, 0.6);
  background: rgba(30, 41, 59, 0.8);
  transform: translateY(-2px);
}

.color-btn-new.active,
.size-btn-new.active {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  border-color: #a78bfa;
  color: white;
  box-shadow: 0 4px 20px rgba(167, 139, 250, 0.4);
}

/* Stock Status */
.stock-status-section {
  padding: var(--space-3);
  border-radius: var(--radius-md);
  background: rgba(30, 41, 59, 0.4);
}

.stock-status {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-weight: var(--font-semibold);
  font-size: var(--text-base);
}

.stock-status svg {
  flex-shrink: 0;
}

.stock-status.in-stock {
  color: #10b981;
}

.stock-status.out-of-stock {
  color: #ef4444;
}

.stock-status.select-variant {
  color: #64748b;
}

/* Quantity */
.quantity-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.quantity-controls-new {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  width: fit-content;
}

.qty-btn {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(30, 41, 59, 0.6);
  border: 2px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-md);
  color: #e2e8f0;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.qty-btn:hover:not(:disabled) {
  border-color: #a78bfa;
  background: rgba(167, 139, 250, 0.2);
}

.qty-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.qty-input-new {
  width: 70px;
  height: 44px;
  text-align: center;
  background: rgba(30, 41, 59, 0.6);
  border: 2px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-md);
  color: #f1f5f9;
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
}

.qty-input-new:focus {
  outline: none;
  border-color: #a78bfa;
  background: rgba(30, 41, 59, 0.8);
}

/* Action Buttons */
.action-buttons-new {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-3);
  margin-top: var(--space-2);
}

.btn-add-cart-new {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  padding: var(--space-4) var(--space-6);
}

/* Product Features */
.product-features {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-3);
  padding: var(--space-5);
  background: rgba(30, 41, 59, 0.4);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(167, 139, 250, 0.15);
}

.feature-item {
  display: flex;
  gap: var(--space-3);
  align-items: flex-start;
}

.feature-icon {
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-md);
  color: #a78bfa;
}

.feature-text h4 {
  margin: 0 0 var(--space-1);
  color: #f1f5f9;
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
}

.feature-text p {
  margin: 0;
  color: #94a3b8;
  font-size: var(--text-sm);
}

/* Product Tabs */
.product-tabs-section {
  margin-bottom: var(--space-10);
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.tab-buttons-new {
  display: flex;
  gap: 0;
  background: rgba(15, 23, 42, 0.6);
  border-bottom: 1px solid rgba(167, 139, 250, 0.15);
}

.tab-btn-new {
  flex: 1;
  padding: var(--space-4) var(--space-6);
  background: transparent;
  border: none;
  border-bottom: 3px solid transparent;
  color: #94a3b8;
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tab-btn-new:hover {
  color: #c4b5fd;
  background: rgba(167, 139, 250, 0.05);
}

.tab-btn-new.active {
  color: #a78bfa;
  border-bottom-color: #a78bfa;
  background: rgba(167, 139, 250, 0.1);
}

.tab-content-new {
  padding: var(--space-6);
}

.tab-pane {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.tab-pane-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: #f1f5f9;
  margin: 0 0 var(--space-5);
}

/* Specs Table */
.specs-table {
  width: 100%;
  border-collapse: collapse;
}

.specs-table tr {
  border-bottom: 1px solid rgba(167, 139, 250, 0.1);
}

.specs-table tr:last-child {
  border-bottom: none;
}

.specs-table td {
  padding: var(--space-4) 0;
}

.spec-label {
  width: 35%;
  color: #94a3b8;
  font-weight: var(--font-medium);
}

.spec-value {
  color: #f1f5f9;
  font-weight: var(--font-semibold);
}

/* Reviews */
.reviews-header {
  margin-bottom: var(--space-6);
}

.reviews-summary-card {
  margin-top: var(--space-4);
  padding: var(--space-6);
  background: rgba(15, 23, 42, 0.6);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(167, 139, 250, 0.15);
}

.summary-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
}

.big-rating {
  font-size: 3.5rem;
  font-weight: var(--font-bold);
  background: linear-gradient(135deg, #a78bfa 0%, #c4b5fd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stars-large .star {
  font-size: 24px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.review-card {
  padding: var(--space-5);
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.1);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
}

.review-card:hover {
  border-color: rgba(167, 139, 250, 0.3);
  transform: translateY(-2px);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-3);
}

.reviewer-info {
  display: flex;
  gap: var(--space-3);
  align-items: center;
}

.reviewer-avatar {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: var(--font-bold);
  font-size: var(--text-lg);
}

.reviewer-details {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.reviewer-name {
  font-weight: var(--font-semibold);
  color: #f1f5f9;
}

.review-date {
  font-size: var(--text-sm);
  color: #64748b;
}

.review-stars {
  display: flex;
  gap: 2px;
}

.review-content {
  color: #cbd5e1;
  line-height: 1.6;
}

.no-reviews {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-10) var(--space-6);
  color: #64748b;
}

.no-reviews svg {
  color: #475569;
}

/* Shipping Info */
.shipping-info {
  display: grid;
  gap: var(--space-6);
}

.info-section h4 {
  color: #f1f5f9;
  margin: 0 0 var(--space-3);
  font-size: var(--text-lg);
}

.info-section ul {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.info-section li {
  color: #cbd5e1;
  padding-left: var(--space-6);
  position: relative;
}

.info-section li::before {
  content: '•';
  position: absolute;
  left: var(--space-3);
  color: #a78bfa;
  font-weight: var(--font-bold);
}

/* Related Products */
.related-products-section {
  margin-top: var(--space-10);
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: #f1f5f9;
  margin: 0 0 var(--space-6);
}

.related-products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
}

.related-product-card {
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-fast);
  cursor: pointer;
}

.related-product-card:hover {
  border-color: rgba(167, 139, 250, 0.4);
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.related-product-image {
  aspect-ratio: 1;
  overflow: hidden;
  background: rgba(15, 23, 42, 0.6);
}

.related-product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.related-product-card:hover .related-product-image img {
  transform: scale(1.1);
}

.related-product-info {
  padding: var(--space-4);
}

.related-brand {
  font-size: var(--text-sm);
  color: #94a3b8;
  margin: 0 0 var(--space-1);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.related-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: #f1f5f9;
  margin: 0 0 var(--space-2);
}

.related-price {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: #a78bfa;
  margin: 0;
}

/* Zoom Modal */
.zoom-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.95);
  backdrop-filter: blur(10px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-6);
  animation: fadeIn 0.2s ease-out;
  cursor: zoom-out;
}

.zoom-close {
  position: absolute;
  top: var(--space-6);
  right: var(--space-6);
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-full);
  color: white;
  font-size: 2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
  z-index: 10001;
}

.zoom-close:hover {
  background: rgba(167, 139, 250, 0.8);
  transform: rotate(90deg);
}

.zoom-content {
  max-width: 90vw;
  max-height: 90vh;
  cursor: default;
}

.zoom-content img {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
  border-radius: var(--radius-lg);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

/* Responsive */
@media (max-width: 1024px) {
  .product-main-grid {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }

  .product-gallery {
    position: static;
  }

  .action-buttons-new {
    grid-template-columns: 1fr;
  }

  .related-products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .product-name {
    font-size: 1.75rem;
  }

  .current-price {
    font-size: 1.75rem;
  }

  .related-products-grid {
    grid-template-columns: 1fr;
  }

  .tab-buttons-new {
    flex-wrap: wrap;
  }

  .tab-btn-new {
    flex: 1 1 100%;
    border-bottom: 1px solid rgba(167, 139, 250, 0.1);
  }

  .tab-btn-new.active {
    border-bottom-color: #a78bfa;
  }
}
</style>

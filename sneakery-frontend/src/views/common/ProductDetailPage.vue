<template>
  <div class="product-detail-page">
    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
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
        <span>/</span>
        <router-link to="/products">Sản phẩm</router-link>
        <span>/</span>
        <span class="current">{{ product.name }}</span>
      </nav>

      <!-- Product Main Info -->
      <div class="product-main-grid">
        <!-- Product Images -->
        <div class="product-images">
          <div class="main-image">
            <img :src="selectedImage || product.imageUrl" :alt="product.name" />
          </div>
          <div class="thumbnail-images" v-if="product.variants && product.variants.length > 0">
            <img
              v-for="(variant, index) in product.variants"
              :key="index"
              :src="variant.imageUrl || product.imageUrl"
              :alt="`${product.name} - ${variant.color}`"
              :class="{ active: selectedImage === variant.imageUrl }"
              @click="selectedImage = variant.imageUrl"
            />
          </div>
        </div>

        <!-- Product Info -->
        <div class="product-info">
          <div class="product-brand">{{ product.brandName }}</div>
          <h1 class="product-name">{{ product.name }}</h1>

          <!-- Price -->
          <div class="product-price">
            <span class="current-price">{{ formatPrice(currentPrice) }}</span>
            <span v-if="originalPrice > currentPrice" class="original-price">
              {{ formatPrice(originalPrice) }}
            </span>
            <span v-if="originalPrice > currentPrice" class="discount-badge">
              -{{ Math.round(((originalPrice - currentPrice) / originalPrice) * 100) }}%
            </span>
          </div>

          <!-- Description -->
          <div class="product-description">
            <p>{{ product.description || 'Chưa có mô tả chi tiết.' }}</p>
          </div>

          <!-- Color Selection -->
          <div class="product-options">
            <div class="option-group">
              <label class="option-label">Màu sắc:</label>
              <div class="color-options">
                <button
                  v-for="color in availableColors"
                  :key="color"
                  :class="['color-btn', { active: selectedColor === color }]"
                  @click="selectColor(color)"
                >
                  {{ color }}
                </button>
              </div>
            </div>

            <!-- Size Selection -->
            <div class="option-group">
              <label class="option-label">Kích cỡ:</label>
              <div class="size-options">
                <button
                  v-for="size in availableSizes"
                  :key="size"
                  :class="['size-btn', { active: selectedSize === size }]"
                  @click="selectSize(size)"
                >
                  {{ size }}
                </button>
              </div>
            </div>
          </div>

          <!-- Stock Status -->
          <div class="stock-status">
            <span v-if="selectedVariant && selectedVariant.stockQuantity > 0" class="in-stock">
              ✓ Còn hàng ({{ selectedVariant.stockQuantity }} sản phẩm)
            </span>
            <span v-else-if="selectedVariant" class="out-of-stock">✗ Hết hàng</span>
            <span v-else class="select-variant">Vui lòng chọn màu và size</span>
          </div>

          <!-- Quantity -->
          <div class="quantity-selector">
            <label class="option-label">Số lượng:</label>
            <div class="quantity-controls">
              <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
              <input v-model.number="quantity" type="number" min="1" :max="maxQuantity" />
              <button @click="increaseQuantity" :disabled="quantity >= maxQuantity">+</button>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="action-buttons">
            <button
              @click="addToCart"
              :disabled="!canAddToCart"
              class="btn btn-primary btn-add-cart"
            >
              {{ cartButtonText }}
            </button>
            <button @click="buyNow" :disabled="!canAddToCart" class="btn btn-secondary">
              Mua ngay
            </button>
          </div>
        </div>
      </div>

      <!-- Product Details Tabs -->
      <div class="product-tabs">
        <div class="tab-buttons">
          <button
            :class="['tab-btn', { active: activeTab === 'specs' }]"
            @click="activeTab = 'specs'"
          >
            Thông số kỹ thuật
          </button>
          <button
            :class="['tab-btn', { active: activeTab === 'reviews' }]"
            @click="activeTab = 'reviews'"
          >
            Đánh giá (0)
          </button>
        </div>

        <div class="tab-content">
          <div v-if="activeTab === 'specs'" class="specs-content">
            <h3>Thông tin chi tiết</h3>
            <ul>
              <li><strong>Thương hiệu:</strong> {{ product.brandName }}</li>
              <li><strong>Danh mục:</strong> {{ formatCategories(product.categories) }}</li>
              <li><strong>Màu sắc:</strong> {{ availableColors.join(', ') }}</li>
              <li><strong>Kích cỡ:</strong> {{ availableSizes.join(', ') }}</li>
            </ul>
          </div>

          <div v-if="activeTab === 'reviews'" class="reviews-content">
            <p class="text-secondary">Chưa có đánh giá nào cho sản phẩm này.</p>
          </div>
        </div>
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

// Methods
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
.product-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-6);
}

/* Loading & Error */
.loading-container,
.error-container {
  text-align: center;
  padding: var(--space-8);
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto var(--space-4);
}

/* Breadcrumb */
.breadcrumb {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-6);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.breadcrumb a {
  color: var(--text-secondary);
  text-decoration: none;
}

.breadcrumb a:hover {
  color: var(--primary-color);
}

.breadcrumb .current {
  color: var(--text-primary);
  font-weight: var(--font-medium);
}

/* Product Main Grid */
.product-main-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-8);
  margin-bottom: var(--space-8);
}

/* Product Images */
.product-images {
  position: sticky;
  top: var(--space-6);
  height: fit-content;
}

.main-image {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: var(--space-4);
  aspect-ratio: 1;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-images {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-2);
}

.thumbnail-images img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: var(--radius-md);
  cursor: pointer;
  border: 2px solid transparent;
  transition: all var(--transition-fast);
}

.thumbnail-images img:hover,
.thumbnail-images img.active {
  border-color: var(--primary-color);
}

/* Product Info */
.product-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.product-brand {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: var(--font-semibold);
}

.product-name {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin: 0;
}

.product-price {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.current-price {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--primary-color);
}

.original-price {
  font-size: var(--text-xl);
  color: var(--text-tertiary);
  text-decoration: line-through;
}

.discount-badge {
  background: var(--error-color);
  color: white;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
}

.product-description {
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
}

/* Product Options */
.product-options {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.option-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.option-label {
  font-weight: var(--font-semibold);
  font-size: var(--text-base);
}

.color-options,
.size-options {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.color-btn,
.size-btn {
  padding: var(--space-2) var(--space-4);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-weight: var(--font-medium);
}

.color-btn:hover,
.size-btn:hover {
  border-color: var(--primary-color);
}

.color-btn.active,
.size-btn.active {
  border-color: var(--primary-color);
  background: var(--primary-color);
  color: white;
}

/* Stock Status */
.stock-status {
  padding: var(--space-3);
  border-radius: var(--radius-md);
  font-weight: var(--font-semibold);
}

.in-stock {
  color: var(--success-color);
}

.out-of-stock {
  color: var(--error-color);
}

.select-variant {
  color: var(--text-tertiary);
}

/* Quantity Selector */
.quantity-selector {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  width: fit-content;
}

.quantity-controls button {
  width: 40px;
  height: 40px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.quantity-controls button:hover:not(:disabled) {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.quantity-controls button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-controls input {
  width: 60px;
  height: 40px;
  text-align: center;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
}

/* Action Buttons */
.action-buttons {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-3);
  margin-top: var(--space-4);
}

.btn-add-cart {
  font-size: var(--text-lg);
  padding: var(--space-4);
}

/* Tabs */
.product-tabs {
  margin-top: var(--space-8);
}

.tab-buttons {
  display: flex;
  gap: var(--space-2);
  border-bottom: 2px solid var(--border-color);
  margin-bottom: var(--space-6);
}

.tab-btn {
  padding: var(--space-3) var(--space-6);
  border: none;
  background: none;
  cursor: pointer;
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
  transition: all var(--transition-fast);
}

.tab-btn:hover {
  color: var(--primary-color);
}

.tab-btn.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

.tab-content {
  padding: var(--space-4);
}

.specs-content h3 {
  margin-bottom: var(--space-4);
}

.specs-content ul {
  list-style: none;
  padding: 0;
}

.specs-content li {
  padding: var(--space-3) 0;
  border-bottom: 1px solid var(--border-color);
}

/* Responsive */
@media (max-width: 768px) {
  .product-main-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .product-images {
    position: static;
  }

  .action-buttons {
    grid-template-columns: 1fr;
  }
}
</style>


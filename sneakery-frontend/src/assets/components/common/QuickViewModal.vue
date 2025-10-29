<template>
  <Transition name="modal">
    <div v-if="isOpen" class="quick-view-modal" @click.self="close">
      <div class="quick-view-content">
        <button class="close-btn" @click="close">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>

        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải...</p>
        </div>

        <div v-else-if="product" class="quick-view-grid">
          <!-- Product Image -->
          <div class="quick-view-image">
            <img :src="product.imageUrl || '/placeholder-image.png'" :alt="product.name" />
          </div>

          <!-- Product Info -->
          <div class="quick-view-info">
            <span class="product-brand">{{ product.brandName }}</span>
            <h2 class="product-name">{{ product.name }}</h2>
            
            <div class="product-rating">
              <div class="stars">
                <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= (product.avgRating || 4) }">★</span>
              </div>
              <span class="rating-text">({{ product.reviewCount || 12 }} đánh giá)</span>
            </div>

            <div class="product-price">
              <span class="current-price">{{ formatPrice(product.price) }}</span>
              <span v-if="product.priceSale" class="original-price">{{ formatPrice(product.priceBase) }}</span>
            </div>

            <p class="product-description">{{ product.description || 'Sản phẩm chất lượng cao, chính hãng 100%' }}</p>

            <!-- Color Options -->
            <div v-if="product.variants && product.variants.length > 0" class="color-selection">
              <label>Màu sắc:</label>
              <div class="color-options">
                <button
                  v-for="(variant, index) in uniqueColors"
                  :key="index"
                  @click="selectedColor = variant.color"
                  :class="['color-btn', { active: selectedColor === variant.color }]"
                  :style="{ backgroundColor: getColorHex(variant.color) }"
                  :title="variant.color"
                ></button>
              </div>
            </div>

            <!-- Size Selection -->
            <div v-if="product.variants && product.variants.length > 0" class="size-selection">
              <label>Kích cỡ:</label>
              <div class="size-options">
                <button
                  v-for="size in availableSizes"
                  :key="size"
                  @click="selectedSize = size"
                  :class="['size-btn', { active: selectedSize === size }]"
                >
                  {{ size }}
                </button>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="action-buttons">
              <button @click="handleAddToCart" class="btn btn-primary btn-block">
                Thêm vào giỏ hàng
              </button>
              <button @click="handleBuyNow" class="btn btn-outline btn-block">
                Mua ngay
              </button>
            </div>

            <router-link :to="`/products/${product.slug}`" class="view-full-link">
              Xem chi tiết sản phẩm →
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import productService from '@/services/productService';

const props = defineProps({
  isOpen: Boolean,
  productId: Number
});

const emit = defineEmits(['update:isOpen', 'added-to-cart']);

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(false);
const product = ref(null);
const selectedColor = ref('');
const selectedSize = ref('');

const uniqueColors = computed(() => {
  if (!product.value?.variants) return [];
  const colors = [...new Set(product.value.variants.map(v => v.color))];
  return colors.map(color => ({
    color,
    variant: product.value.variants.find(v => v.color === color)
  }));
});

const availableSizes = computed(() => {
  if (!product.value?.variants || !selectedColor.value) return [];
  return [...new Set(
    product.value.variants
      .filter(v => v.color === selectedColor.value && v.stockQuantity > 0)
      .map(v => v.size)
  )].sort();
});

const currentVariant = computed(() => {
  if (!product.value?.variants || !selectedColor.value || !selectedSize.value) return null;
  return product.value.variants.find(
    v => v.color === selectedColor.value && v.size === selectedSize.value
  );
});

watch(() => props.isOpen, (isOpen) => {
  if (isOpen && props.productId) {
    fetchProduct();
  }
});

watch(() => props.productId, (newId) => {
  if (props.isOpen && newId) {
    fetchProduct();
  }
});

const fetchProduct = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`http://localhost:8080/api/admin/products/${props.productId}`);
    product.value = response.data;
    
    if (product.value.variants && product.value.variants.length > 0) {
      const firstVariant = product.value.variants[0];
      selectedColor.value = firstVariant.color;
      const sizes = product.value.variants.filter(v => v.color === firstVariant.color && v.stockQuantity > 0);
      if (sizes.length > 0) {
        selectedSize.value = sizes[0].size;
      }
    }
  } catch (error) {
    console.error('Error fetching product:', error);
    ElMessage.error('Không thể tải thông tin sản phẩm');
  } finally {
    loading.value = false;
  }
};

const handleAddToCart = async () => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('Vui lòng đăng nhập để thêm vào giỏ hàng');
    close();
    router.push('/login');
    return;
  }

  if (!currentVariant.value) {
    ElMessage.warning('Vui lòng chọn màu sắc và kích cỡ');
    return;
  }

  try {
    await axios.post(
      'http://localhost:8080/api/cart/item',
      {
        variantId: currentVariant.value.id,
        quantity: 1
      },
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      }
    );

    ElMessage.success('Đã thêm vào giỏ hàng');
    emit('added-to-cart');
  } catch (error) {
    console.error('Error adding to cart:', error);
    ElMessage.error(error.response?.data?.message || 'Không thể thêm vào giỏ hàng');
  }
};

const handleBuyNow = async () => {
  await handleAddToCart();
  close();
  router.push('/cart');
};

const getColorHex = (colorName) => {
  const colorMap = {
    'Đen': '#000000',
    'Trắng': '#FFFFFF',
    'Xám': '#808080',
    'Đỏ': '#FF0000',
    'Xanh dương': '#0000FF',
    'Xanh lá': '#00FF00',
    'Vàng': '#FFFF00',
    'Hồng': '#FFC0CB',
    'Nâu': '#A52A2A'
  };
  return colorMap[colorName] || '#CCCCCC';
};

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const close = () => {
  emit('update:isOpen', false);
};
</script>

<style scoped>
.quick-view-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: var(--space-4);
}

.quick-view-content {
  background: var(--bg-primary);
  border-radius: var(--radius-2xl);
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
}

.close-btn {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: var(--radius-full);
  color: var(--text-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  transition: all var(--transition-normal);
}

.close-btn:hover {
  background: var(--error-color);
  color: white;
  transform: scale(1.1);
}

.loading-state {
  padding: var(--space-16);
  text-align: center;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: #a78bfa;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.quick-view-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-8);
  padding: var(--space-8);
}

.quick-view-image {
  aspect-ratio: 1;
  background: var(--bg-secondary);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.quick-view-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.quick-view-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.product-brand {
  font-size: var(--text-sm);
  color: var(--primary-color);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: var(--font-semibold);
}

.product-name {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
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
  color: var(--text-muted);
  font-size: var(--text-lg);
}

.star.filled {
  color: #fbbf24;
}

.rating-text {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.product-price {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.current-price {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--primary-color);
}

.original-price {
  font-size: var(--text-xl);
  color: var(--text-muted);
  text-decoration: line-through;
}

.product-description {
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
}

.color-selection,
.size-selection {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.color-selection label,
.size-selection label {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.color-options,
.size-options {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.color-btn {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  border: 3px solid transparent;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.color-btn:hover {
  transform: scale(1.1);
}

.color-btn.active {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px var(--bg-primary), 0 0 0 4px var(--primary-color);
}

.size-btn {
  padding: var(--space-2) var(--space-4);
  background: var(--bg-secondary);
  border: 2px solid var(--border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  font-weight: var(--font-medium);
}

.size-btn:hover {
  border-color: var(--primary-color);
}

.size-btn.active {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: white;
}

.action-buttons {
  display: flex;
  gap: var(--space-3);
  margin-top: var(--space-4);
}

.btn-block {
  flex: 1;
}

.view-full-link {
  text-align: center;
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-medium);
  transition: color var(--transition-normal);
}

.view-full-link:hover {
  color: var(--primary-dark);
}

@media (max-width: 768px) {
  .quick-view-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
    padding: var(--space-4);
  }

  .quick-view-image {
    max-height: 300px;
  }
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>


<template>
  <Teleport to="body">
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

            <router-link :to="`/home/products/${product.id}`" class="view-full-link">
              Xem chi tiết sản phẩm →
            </router-link>
          </div>
        </div>
      </div>
    </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useCartStore } from '@/stores/cart';
import toastService from '@/utils/toastService';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';
import axios from 'axios';
import productService from '@/services/productService';

const props = defineProps({
  isOpen: Boolean,
  productId: Number
});

const emit = defineEmits(['update:isOpen', 'added-to-cart']);

const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();

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
    const response = await axios.get(API_ENDPOINTS.PRODUCTS.BY_ID(props.productId));
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
    logger.error('Error fetching product:', error);
    toastService.error('Lỗi','Không thể tải thông tin sản phẩm');
  } finally {
    loading.value = false;
  }
};

const handleAddToCart = async () => {
  if (!authStore.isAuthenticated) {
    toastService.warning('Cảnh báo','Vui lòng đăng nhập để thêm vào giỏ hàng');
    close();
    router.push('/login');
    return;
  }

  if (!currentVariant.value) {
    toastService.warning('Cảnh báo','Vui lòng chọn màu sắc và kích cỡ');
    return;
  }

  try {
    // Sử dụng cart store để thêm vào giỏ hàng
    await cartStore.addItem(currentVariant.value.id, 1);
    toastService.success('Thành công', 'Đã thêm vào giỏ hàng');
    emit('added-to-cart');
    logger.log('Product added to cart from quick view:', currentVariant.value.id);
    // Đóng modal sau khi thêm thành công
    close();
  } catch (error) {
    logger.error('Error adding to cart:', error);
    
    // Nếu lỗi 401, yêu cầu đăng nhập
    if (error.response?.status === 401) {
      toastService.warning('Cảnh báo', 'Vui lòng đăng nhập để thêm vào giỏ hàng');
      close();
      router.push('/login');
    } else {
      toastService.error('Lỗi', error.response?.data?.message || 'Không thể thêm vào giỏ hàng');
    }
  }
};

const handleBuyNow = async () => {
  try {
    await handleAddToCart();
    // handleAddToCart đã tự đóng modal, chỉ cần redirect
    router.push('/cart');
  } catch (error) {
    // Nếu lỗi, modal vẫn mở để user có thể thử lại
    logger.error('Error in buy now:', error);
  }
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
  inset: 0;
  z-index: 99999;
  background: rgba(0, 0, 0, 0.75);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.quick-view-content {
  position: relative;
  background: white;
  border-radius: 1rem;
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.dark .quick-view-content {
  background: #1f2937;
}

.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  z-index: 10;
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  background: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #374151;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.dark .close-btn {
  background: #374151;
  color: #f9fafb;
}

.close-btn:hover {
  background: #ef4444;
  color: white;
  transform: scale(1.1);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  gap: 1rem;
}

.loading-spinner {
  width: 3rem;
  height: 3rem;
  border: 4px solid #e5e7eb;
  border-top-color: #9333ea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.quick-view-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  padding: 2rem;
}

@media (max-width: 768px) {
  .quick-view-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
    padding: 1.5rem;
  }
}

.quick-view-image {
  aspect-ratio: 1;
  border-radius: 0.75rem;
  overflow: hidden;
  background: #f3f4f6;
}

.dark .quick-view-image {
  background: #374151;
}

.quick-view-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.quick-view-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.product-brand {
  font-size: 0.875rem;
  font-weight: 600;
  color: #9333ea;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.dark .product-brand {
  color: #a78bfa;
}

.product-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: #111827;
  line-height: 1.3;
}

.dark .product-name {
  color: #f9fafb;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stars {
  display: flex;
  gap: 0.125rem;
}

.star {
  font-size: 1.25rem;
  color: #d1d5db;
}

.star.filled {
  color: #fbbf24;
}

.rating-text {
  font-size: 0.875rem;
  color: #6b7280;
}

.dark .rating-text {
  color: #9ca3af;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.current-price {
  font-size: 1.5rem;
  font-weight: 700;
  color: #ef4444;
}

.dark .current-price {
  color: #f87171;
}

.original-price {
  font-size: 1.125rem;
  color: #9ca3af;
  text-decoration: line-through;
}

.product-description {
  color: #6b7280;
  line-height: 1.6;
}

.dark .product-description {
  color: #9ca3af;
}

.color-selection,
.size-selection {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.color-selection label,
.size-selection label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
}

.dark .color-selection label,
.dark .size-selection label {
  color: #f9fafb;
}

.color-options,
.size-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.color-btn {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}

.color-btn.active {
  border-color: #9333ea;
  transform: scale(1.1);
  box-shadow: 0 0 0 2px rgba(147, 51, 234, 0.2);
}

.size-btn {
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  border: 2px solid #e5e7eb;
  background: white;
  color: #374151;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dark .size-btn {
  background: #374151;
  border-color: #4b5563;
  color: #f9fafb;
}

.size-btn:hover {
  border-color: #9333ea;
  color: #9333ea;
}

.size-btn.active {
  background: #9333ea;
  border-color: #9333ea;
  color: white;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 0.5rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-primary {
  background: linear-gradient(135deg, #9333ea, #7c3aed);
  color: white;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #7c3aed, #6d28d9);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.4);
}

.btn-outline {
  background: transparent;
  border: 2px solid #9333ea;
  color: #9333ea;
}

.btn-outline:hover {
  background: #9333ea;
  color: white;
}

.btn-block {
  width: 100%;
}

.view-full-link {
  display: inline-block;
  margin-top: 1rem;
  color: #9333ea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s ease;
}

.view-full-link:hover {
  color: #7c3aed;
  text-decoration: underline;
}

/* Modal transition */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .quick-view-content,
.modal-leave-active .quick-view-content {
  transition: transform 0.3s ease;
}

.modal-enter-from .quick-view-content,
.modal-leave-to .quick-view-content {
  transform: scale(0.9);
}
</style>

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
    const response = await axios.get(API_ENDPOINTS.ADMIN_PRODUCTS.BY_ID(props.productId));
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
    await axios.post(
      API_ENDPOINTS.CART.ITEM,
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

    toastService.success('Thành công','Đã thêm vào giỏ hàng');
    emit('added-to-cart');
  } catch (error) {
    console.error('Error adding to cart:', error);
    toastService.error('Lỗi',error.response?.data?.message || 'Không thể thêm vào giỏ hàng');
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





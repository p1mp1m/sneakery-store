<template>
  <router-link :to="`/products/${product.id}`" class="product-card-link">
    <div class="product-card">
      <div class="product-image-container">
        <!-- Flash Sale Badge -->
        <FlashSaleBadge
          v-if="productFlashSale"
          :flashSale="productFlashSale"
          class="compact"
        />
        
        <img :src="product.imageUrl || '/placeholder-image.png'" class="product-image" :alt="product.name" />
        <div class="product-overlay">
          <button class="btn-icon btn-quick-view" @click="openQuickView" title="Xem nhanh">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M1 12C1 12 5 4 12 4C19 4 23 12 23 12C23 12 19 20 12 20C5 20 1 12 1 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <button class="btn-icon btn-favorite" @click="toggleFavorite" title="Yêu thích">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69364 19.0554 3.41708C18.3879 3.14052 17.6725 2.99817 16.95 2.99817C16.2275 2.99817 15.5121 3.14052 14.8446 3.41708C14.1772 3.69364 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.5783 8.50903 2.9987 7.05 2.9987C5.59096 2.9987 4.19169 3.5783 3.16 4.61C2.1283 5.6417 1.5487 7.04097 1.5487 8.5C1.5487 9.95903 2.1283 11.3583 3.16 12.39L4.22 13.45L12 21.23L19.78 13.45L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6053C22.3095 9.93789 22.4518 9.22248 22.4518 8.5C22.4518 7.77752 22.3095 7.06211 22.0329 6.39467C21.7563 5.72723 21.351 5.1208 20.84 4.61Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button class="btn-icon btn-cart" @click="addToCart">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        </div>
      </div>
    
      <div class="product-info">
      <span class="brand-name">{{ product.brandName }}</span>
      <h3 class="product-name">{{ product.name }}</h3>
      <div class="product-footer">
        <div class="price-wrapper">
          <span v-if="productFlashSale" class="price-original">{{ formatCurrency(product.price) }}</span>
          <span class="price">{{ formatCurrency(finalPrice) }}</span>
        </div>
        <button class="btn-add-cart" @click="addToCart">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 3H5L5.4 5M7 13H17L21 5H5.4M7 13L5.4 5M7 13L4.7 15.3C4.3 15.7 4.6 16.5 5.1 16.5H17M17 13V17C17 18.1 16.1 19 15 19H9C7.9 19 7 18.1 7 17V13M17 13H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </div>
    </div>
  </router-link>
</template>
  
<script setup>
import { ref, computed, onMounted } from 'vue';
import { useFlashSaleStore } from '@/stores/flashSale';
import { storeToRefs } from 'pinia';
import FlashSaleBadge from '@/assets/components/common/FlashSaleBadge.vue';

// Props
const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

// Stores
const flashSaleStore = useFlashSaleStore();
const { activeFlashSales } = storeToRefs(flashSaleStore);

// Reactive state
const isFavorite = ref(false);

// Computed
const productFlashSale = computed(() => {
  return flashSaleStore.getFlashSaleForProduct(props.product.id);
});

const finalPrice = computed(() => {
  if (productFlashSale.value) {
    return flashSaleStore.calculateDiscountedPrice(
      props.product.price,
      productFlashSale.value.discountPercent
    );
  }
  return props.product.price;
});

// Methods
const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const toggleFavorite = (event) => {
  event.preventDefault(); // Prevent navigation
  event.stopPropagation();
  isFavorite.value = !isFavorite.value;
  // TODO: Implement favorite functionality
};

const addToCart = (event) => {
  event.preventDefault(); // Prevent navigation
  event.stopPropagation();
  // TODO: Implement add to cart functionality
  // console.log('Add to cart:', product.value); // Debug
};
</script>
  


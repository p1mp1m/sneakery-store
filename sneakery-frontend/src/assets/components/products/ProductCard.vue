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
        <button class="btn-icon btn-favorite" @click="toggleFavorite">
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
  
<style scoped>
/* ═══════════════════════════════════════════════════════════════════════
   🛍️ PRODUCT CARD COMPONENT
   Component hiển thị thông tin sản phẩm với hover effects
   ═══════════════════════════════════════════════════════════════════════ */

/* ─────────────────────────────────────────────────────────────────────
   1. Card Link Wrapper
   ───────────────────────────────────────────────────────────────────── */
.product-card-link {
  display: block;
  height: 100%;
  text-decoration: none;
  color: inherit;
  transition: opacity var(--transition-fast);
}

.product-card-link:focus-visible {
  outline: 2px solid var(--primary-color);
  outline-offset: 4px;
  border-radius: var(--radius-xl);
}

/* ─────────────────────────────────────────────────────────────────────
   2. Card Container - Dark Theme
   ───────────────────────────────────────────────────────────────────── */
.product-card {
  /* Layout */
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
  overflow: hidden;
  
  /* Visual - Dark */
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(10px);
  
  /* Effects */
  transition: all var(--transition-normal);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(167, 139, 250, 0.3);
  border-color: rgba(167, 139, 250, 0.4);
  background: rgba(30, 41, 59, 0.8);
}

/* ─────────────────────────────────────────────────────────────────────
   3. Image Section
   ───────────────────────────────────────────────────────────────────── */
.product-image-container {
  position: relative;
  overflow: hidden;
  aspect-ratio: 1 / 1;
  background: rgba(15, 23, 42, 0.6);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

/* ─────────────────────────────────────────────────────────────────────
   4. Action Overlay (Quick Actions)
   ───────────────────────────────────────────────────────────────────── */
.product-overlay {
  /* Position */
  position: absolute;
  top: var(--space-3);
  right: var(--space-3);
  z-index: var(--z-10);
  
  /* Layout */
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  
  /* Effects */
  opacity: 0;
  transform: translateX(20px);
  transition: all var(--transition-normal);
}

.product-card:hover .product-overlay {
  opacity: 1;
  transform: translateX(0);
}

/* ─────────────────────────────────────────────────────────────────────
   5. Icon Buttons
   ───────────────────────────────────────────────────────────────────── */
.btn-icon {
  /* Sizing */
  width: 40px;
  height: 40px;
  
  /* Layout */
  display: flex;
  align-items: center;
  justify-content: center;
  
  /* Visual - Dark */
  background: rgba(30, 41, 59, 0.9);
  border: 1px solid rgba(167, 139, 250, 0.3);
  border-radius: var(--radius-full);
  color: #e2e8f0;
  
  /* Effects */
  cursor: pointer;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  transition: all var(--transition-normal);
}

.btn-icon:hover {
  background: rgba(30, 41, 59, 0.95);
  border-color: rgba(167, 139, 250, 0.5);
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(167, 139, 250, 0.3);
}

.btn-icon:active {
  transform: scale(0.95);
}

.btn-favorite:hover {
  color: var(--error-color);
}

.btn-cart:hover {
  color: var(--primary-color);
}

/* ─────────────────────────────────────────────────────────────────────
   6. Product Information
   ───────────────────────────────────────────────────────────────────── */
.product-info {
  /* Layout */
  display: flex;
  flex-direction: column;
  flex: 1;
  
  /* Spacing */
  padding: var(--space-4);
}

/* Brand Label - Dark */
.brand-name {
  /* Typography */
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  line-height: var(--leading-normal);
  
  /* Visual - Dark */
  color: #94a3b8;
  margin-bottom: var(--space-2);
}

/* Product Title - Dark */
.product-name {
  /* Typography */
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  line-height: var(--leading-snug);
  
  /* Visual - Dark */
  color: #f1f5f9;
  margin: 0 0 var(--space-3) 0;
  min-height: 40px;
  
  /* Text Truncation - 2 lines max */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  line-clamp: 2;
}

/* ─────────────────────────────────────────────────────────────────────
   7. Product Footer (Price & Action)
   ───────────────────────────────────────────────────────────────────── */
.product-footer {
  /* Layout */
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-3);
  margin-top: auto;
}

/* Price Display - Dark */
.price-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  flex: 1;
}

.price-original {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: #64748b;
  text-decoration: line-through;
}

.price {
  /* Typography */
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  
  /* Visual - Dark */
  color: #c4b5fd;
}

/* Add to Cart Button */
.btn-add-cart {
  /* Sizing */
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  
  /* Layout */
  display: flex;
  align-items: center;
  justify-content: center;
  
  /* Visual */
  background: var(--primary-gradient);
  border: none;
  border-radius: var(--radius-full);
  color: var(--white);
  
  /* Effects */
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}

.btn-add-cart:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-md);
}

.btn-add-cart:active {
  transform: scale(0.95);
}

/* ═══════════════════════════════════════════════════════════════════════
   📱 8. RESPONSIVE DESIGN
   ═══════════════════════════════════════════════════════════════════════ */

/* ─────────────────────────────────────────────────────────────────────
   Tablet & Below (≤768px)
   ───────────────────────────────────────────────────────────────────── */
@media (max-width: 768px) {
  /* Show overlay by default on touch devices */
  .product-overlay {
    opacity: 1;
    transform: translateX(0);
    position: static;
    flex-direction: row;
    justify-content: center;
    margin-top: var(--space-2);
  }
  
  /* Smaller icon buttons */
  .btn-icon {
    width: 36px;
    height: 36px;
  }
  
  /* Adjust padding */
  .product-info {
    padding: var(--space-3);
  }
  
  /* Smaller product name */
  .product-name {
    font-size: var(--text-sm);
    min-height: 36px;
  }
  
  /* Smaller price */
  .price {
    font-size: var(--text-base);
  }
  
  /* Smaller add to cart button */
  .btn-add-cart {
    width: 32px;
    height: 32px;
  }
}

/* ─────────────────────────────────────────────────────────────────────
   Mobile (≤480px)
   ───────────────────────────────────────────────────────────────────── */
@media (max-width: 480px) {
  /* Stack footer vertically */
  .product-footer {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-2);
  }
  
  /* Full-width cart button */
  .btn-add-cart {
    width: 100%;
    height: 40px;
    border-radius: var(--radius-lg);
  }
  
  /* Adjust card hover on mobile */
  .product-card:hover {
    transform: translateY(-4px);
  }
}
</style>
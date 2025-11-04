<template>
  <router-link :to="`/home/products/${product.id}`" class="product-card-link">
    <div class="product-card group">
      <div class="product-image-container">
        <!-- Badges -->
        <div class="product-badges">
          <span v-if="product.isNew" class="badge badge-new">Mới</span>
          <span v-if="product.isFeatured" class="badge badge-featured">Nổi bật</span>
          <span v-if="!product.inStock" class="badge badge-out-of-stock">Hết hàng</span>
        </div>

        <!-- Flash Sale Badge -->
        <FlashSaleBadge
          v-if="productFlashSale"
          :flashSale="productFlashSale"
          class="compact"
        />
        
        <img 
          :src="product.imageUrl || '/placeholder-image.png'" 
          class="product-image" 
          :alt="product.name"
          loading="lazy"
        />
        <div class="product-overlay">
          <button class="btn-icon btn-quick-view" @click.prevent="openQuickView" title="Xem nhanh">
            <i class="material-icons">visibility</i>
          </button>
          <button 
            class="btn-icon btn-favorite" 
            @click.prevent="toggleFavorite" 
            title="Yêu thích"
            :class="{ active: isFavorite }"
          >
            <i class="material-icons">{{ isFavorite ? 'favorite' : 'favorite_border' }}</i>
          </button>
          <button class="btn-icon btn-cart" @click.prevent="addToCart" title="Thêm vào giỏ">
            <i class="material-icons">shopping_cart</i>
          </button>
        </div>
      </div>
    
      <div class="product-info">
        <span class="brand-name">{{ product.brandName || 'Unknown' }}</span>
        <h3 class="product-name">{{ product.name }}</h3>
        
        <!-- Rating -->
        <div v-if="product.avgRating" class="product-rating">
          <div class="stars">
            <i 
              v-for="i in 5" 
              :key="i"
              class="material-icons star"
              :class="{ filled: i <= Math.round(product.avgRating) }"
            >
              {{ i <= Math.round(product.avgRating) ? 'star' : 'star_border' }}
            </i>
          </div>
          <span class="rating-text">
            {{ product.avgRating.toFixed(1) }}
            <span v-if="product.reviewCount" class="review-count">({{ product.reviewCount }})</span>
          </span>
        </div>
        
        <div class="product-footer">
          <div class="price-wrapper">
            <span v-if="productFlashSale && product.priceSale" class="price-original">
              {{ formatCurrency(product.price) }}
            </span>
            <span class="price">{{ formatCurrency(finalPrice) }}</span>
          </div>
          <button class="btn-add-cart" @click.prevent="addToCart">
            <i class="material-icons">add_shopping_cart</i>
          </button>
        </div>
      </div>
    </div>
  </router-link>
</template>
  
<script setup>
import { ref, computed } from 'vue';
import { useFlashSaleStore } from '@/stores/flashSale';
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
  return props.product.price || props.product.priceBase || 0;
});

// Methods
const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  const numValue = typeof value === 'string' ? parseFloat(value) : value;
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(numValue);
};

const toggleFavorite = (event) => {
  event.preventDefault();
  event.stopPropagation();
  isFavorite.value = !isFavorite.value;
  // TODO: Implement favorite functionality
};

const addToCart = (event) => {
  event.preventDefault();
  event.stopPropagation();
  // TODO: Implement add to cart functionality
};

const openQuickView = (event) => {
  event.preventDefault();
  event.stopPropagation();
  // TODO: Implement quick view functionality
};
</script>

<style scoped>
.product-card-link {
  display: block;
  text-decoration: none;
  color: inherit;
  height: 100%;
}

.product-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 1rem;
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
}

.dark .product-card {
  background: #1f2937;
}

.product-image-container {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f3f4f6;
}

.dark .product-image-container {
  background: #374151;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.group:hover .product-image {
  transform: scale(1.1);
}

.product-badges {
  position: absolute;
  top: 0.75rem;
  left: 0.75rem;
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.025em;
}

.badge-new {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.badge-featured {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.badge-out-of-stock {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

.product-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 20;
}

.group:hover .product-overlay {
  opacity: 1;
}

.btn-icon {
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
}

.btn-icon:hover {
  background: #9333ea;
  color: white;
  transform: scale(1.1);
}

.btn-icon.active {
  background: #ef4444;
  color: white;
}

.btn-icon i {
  font-size: 1.25rem;
}

.product-info {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  flex: 1;
}

.brand-name {
  font-size: 0.75rem;
  font-weight: 600;
  color: #9333ea;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.dark .brand-name {
  color: #a78bfa;
}

.product-name {
  font-size: 0.9375rem;
  font-weight: 600;
  color: #111827;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.8rem;
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
  font-size: 1rem;
  color: #d1d5db;
  transition: color 0.2s;
}

.star.filled {
  color: #fbbf24;
}

.rating-text {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
}

.dark .rating-text {
  color: #9ca3af;
}

.review-count {
  color: #9ca3af;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 0.75rem;
  border-top: 1px solid #e5e7eb;
}

.dark .product-footer {
  border-top-color: #374151;
}

.price-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.price-original {
  font-size: 0.75rem;
  color: #9ca3af;
  text-decoration: line-through;
}

.price {
  font-size: 1.125rem;
  font-weight: 700;
  color: #ef4444;
}

.dark .price {
  color: #f87171;
}

.btn-add-cart {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 0.5rem;
  background: linear-gradient(135deg, #9333ea, #7c3aed);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(147, 51, 234, 0.3);
}

.btn-add-cart:hover {
  background: linear-gradient(135deg, #7c3aed, #6d28d9);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.4);
}

.btn-add-cart i {
  font-size: 1.125rem;
}
</style>
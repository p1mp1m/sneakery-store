<template>
  <section v-if="recommendedProducts.length > 0" class="recommendations-section">
    <div class="container">
      <!-- Header -->
      <div class="section-header">
        <h2 class="section-title">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 21C16.9706 21 21 16.9706 21 12C21 7.02944 16.9706 3 12 3C7.02944 3 3 7.02944 3 12C3 16.9706 7.02944 21 12 21Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 8V12L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Gợi ý dành cho bạn
        </h2>
        <p class="section-subtitle">Dựa trên sở thích và lịch sử xem của bạn</p>
      </div>

      <!-- Products Grid -->
      <div class="products-grid">
        <ProductCard 
          v-for="product in recommendedProducts" 
          :key="product.id" 
          :product="product"
        />
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRecentlyViewed } from '@/composables/useRecentlyViewed';
import ProductCard from '@/assets/components/products/ProductCard.vue';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';
import axios from 'axios';

const { recentlyViewed } = useRecentlyViewed();
const recommendedProducts = ref([]);

onMounted(async () => {
  await loadRecommendations();
});

const loadRecommendations = async () => {
  try {
    // Lấy brand từ recently viewed
    const viewedBrands = [...new Set(recentlyViewed.value.map(p => p.brandName))];
    
    // Simple recommendation: lấy trending products
    const response = await axios.get(API_ENDPOINTS.PRODUCTS.BASE, {
      params: { page: 0, size: 8 }
    });
    recommendedProducts.value = response.data.content || [];
  } catch (error) {
    logger.error('Error loading recommendations:', error);
    recommendedProducts.value = [];
  }
};
</script>




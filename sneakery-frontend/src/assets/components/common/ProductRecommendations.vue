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
    const response = await axios.get('http://localhost:8080/api/products', {
      params: { page: 0, size: 8 }
    });
    recommendedProducts.value = response.data.content || [];
  } catch (error) {
    console.error('Error loading recommendations:', error);
    recommendedProducts.value = [];
  }
};
</script>

<style scoped>
.recommendations-section {
  padding: var(--space-16) var(--space-4);
  background: var(--bg-primary);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: var(--space-12);
}

.section-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
}

.section-title svg {
  color: var(--primary-color);
}

.section-subtitle {
  font-size: var(--text-lg);
  color: var(--text-secondary);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--space-6);
}

@media (max-width: 768px) {
  .recommendations-section {
    padding: var(--space-8) var(--space-4);
  }

  .section-title {
    font-size: var(--text-3xl);
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: var(--space-4);
  }
}
</style>

<template>
  <Transition name="modal">
    <div v-if="isOpen" class="comparison-modal" @click.self="close">
      <div class="comparison-content">
        <div class="modal-header">
          <h2 class="modal-title">So sánh sản phẩm</h2>
          <button class="close-btn" @click="close">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <!-- Comparison Table -->
          <div class="comparison-table-wrapper" v-if="products.length > 0">
            <table class="comparison-table">
              <!-- Header -->
              <thead>
                <tr>
                  <th class="sticky-col">Tính năng</th>
                  <th v-for="product in products" :key="product.id" class="product-col">
                    <button class="remove-btn" @click="removeProduct(product.id)">
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                        <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                    </button>
                    <img :src="product.imageUrl || '/placeholder-image.png'" :alt="product.name" class="product-thumb" />
                    <h3>{{ product.brandName }}</h3>
                    <p>{{ product.name }}</p>
                    <div class="product-price">{{ formatCurrency(product.price) }}</div>
                    <router-link :to="`/products/${product.id}`" class="view-product-btn">
                      Xem chi tiết
                    </router-link>
                  </th>
                </tr>
              </thead>

              <!-- Body -->
              <tbody>
                <tr>
                  <td class="feature-label">Thương hiệu</td>
                  <td v-for="product in products" :key="product.id + '_brand'">{{ product.brandName }}</td>
                </tr>
                <tr>
                  <td class="feature-label">Giá</td>
                  <td v-for="product in products" :key="product.id + '_price'">
                    {{ formatCurrency(product.price) }}
                  </td>
                </tr>
                <tr v-if="hasRating">
                  <td class="feature-label">Đánh giá</td>
                  <td v-for="product in products" :key="product.id + '_rating'">
                    <div class="rating-display">
                      <span class="stars">{{ getStarRating(product.avgRating) }}</span>
                      <span class="rating-value">({{ product.reviewCount || 0 }})</span>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td class="feature-label">Tình trạng</td>
                  <td v-for="product in products" :key="product.id + '_status'">
                    <span :class="['status-badge', product.inStock ? 'in-stock' : 'out-of-stock']">
                      {{ product.inStock ? 'Còn hàng' : 'Hết hàng' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Empty State -->
          <div v-else class="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 20C16.4183 20 20 16.4183 20 12C20 7.58172 16.4183 4 12 4C7.58172 4 4 7.58172 4 12C4 16.4183 7.58172 20 12 20Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 8V12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 16H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h3>Chưa có sản phẩm để so sánh</h3>
            <p>Tối đa 3 sản phẩm. Nhấn vào nút "So sánh" trên card sản phẩm để thêm.</p>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  isOpen: Boolean,
  products: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:isOpen', 'close', 'remove']);

const hasRating = computed(() => {
  return props.products.some(p => p.avgRating !== null && p.avgRating !== undefined);
});

const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const getStarRating = (rating) => {
  if (!rating) return '☆☆☆☆☆';
  const stars = Math.round(rating);
  return '★'.repeat(stars) + '☆'.repeat(5 - stars);
};

const removeProduct = (productId) => {
  emit('remove', productId);
};

const close = () => {
  emit('update:isOpen', false);
  emit('close');
};
</script>

<style scoped>
.comparison-modal {
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

.comparison-content {
  background: var(--bg-primary);
  border-radius: var(--radius-2xl);
  max-width: 1200px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-light);
  position: sticky;
  top: 0;
  background: var(--bg-primary);
  z-index: 10;
}

.modal-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.close-btn {
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
  transition: all var(--transition-normal);
}

.close-btn:hover {
  background: var(--error-color);
  color: white;
  transform: scale(1.1);
}

.modal-body {
  padding: var(--space-6);
}

.comparison-table-wrapper {
  overflow-x: auto;
}

.comparison-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
}

.comparison-table th {
  padding: var(--space-4);
  background: var(--primary-gradient);
  color: white;
  text-align: center;
  position: relative;
}

.sticky-col {
  position: sticky;
  left: 0;
  background: var(--bg-tertiary);
  z-index: 5;
  text-align: left;
}

.product-col {
  min-width: 220px;
  vertical-align: top;
}

.product-col button.remove-btn {
  position: absolute;
  top: var(--space-2);
  right: var(--space-2);
  width: 32px;
  height: 32px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: var(--radius-full);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
}

.product-col button.remove-btn:hover {
  background: var(--error-color);
  transform: scale(1.1);
}

.product-thumb {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  margin: var(--space-4) auto;
}

.product-col h3 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: white;
  margin: var(--space-2) 0;
}

.product-col p {
  font-size: var(--text-xs);
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: var(--space-2);
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
}

.product-price {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: white;
  margin: var(--space-2) 0;
}

.view-product-btn {
  display: inline-block;
  padding: var(--space-2) var(--space-4);
  margin-top: var(--space-3);
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--radius-md);
  color: white;
  font-size: var(--text-sm);
  text-decoration: none;
  transition: all var(--transition-normal);
}

.view-product-btn:hover {
  background: white;
  color: var(--primary-color);
}

.comparison-table td {
  padding: var(--space-4);
  border-bottom: 1px solid var(--border-light);
}

.comparison-table tbody tr:hover {
  background: rgba(167, 139, 250, 0.05);
}

.feature-label {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  background: var(--bg-secondary);
}

.rating-display {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  justify-content: center;
}

.stars {
  color: #fbbf24;
  font-size: var(--text-lg);
}

.rating-value {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.status-badge {
  display: inline-block;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.status-badge.in-stock {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.status-badge.out-of-stock {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.empty-state {
  text-align: center;
  padding: var(--space-12);
}

.empty-state svg {
  color: var(--text-muted);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.empty-state p {
  color: var(--text-secondary);
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .comparison-content {
    max-height: 95vh;
  }

  .modal-header {
    padding: var(--space-4);
  }

  .modal-title {
    font-size: var(--text-xl);
  }

  .product-col {
    min-width: 180px;
  }
}
</style>


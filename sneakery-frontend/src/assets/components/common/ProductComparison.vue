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





<template>
  <section class="testimonials-section py-16 md:py-20 bg-gray-50 dark:bg-gray-900 relative overflow-hidden">
    <!-- Background Pattern -->
    <div class="absolute inset-0 opacity-5">
      <div class="absolute inset-0" style="background-image: repeating-linear-gradient(45deg, transparent, transparent 50px, rgba(147, 51, 234, 0.1) 50px, rgba(147, 51, 234, 0.1) 100px);"></div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
      <!-- Header -->
      <div class="text-center mb-12">
        <h2 class="text-3xl md:text-4xl font-extrabold text-gray-900 dark:text-gray-100 mb-3">
          Khách hàng nói gì về chúng tôi
        </h2>
        <p class="text-lg text-gray-600 dark:text-gray-400">
          Những đánh giá chân thực từ khách hàng đã mua sắm tại Sneakery Store
        </p>
      </div>

      <!-- Testimonials Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-10">
        <div 
          v-for="(testimonial, index) in testimonials" 
          :key="testimonial.id" 
          class="testimonial-card group"
          :style="{ animationDelay: `${index * 100}ms` }"
        >
          <!-- Rating -->
          <div class="testimonial-rating">
            <i 
              v-for="i in 5" 
              :key="i"
              class="material-icons star"
              :class="{ filled: i <= testimonial.rating }"
            >
              {{ i <= testimonial.rating ? 'star' : 'star_border' }}
            </i>
          </div>

          <!-- Content -->
          <div class="testimonial-content">
            <p class="testimonial-text">"{{ testimonial.comment }}"</p>
          </div>

          <!-- Product Info -->
          <div class="testimonial-product">
            <div class="product-image-wrapper">
              <img 
                :src="testimonial.productImage || '/placeholder-image.png'" 
                :alt="testimonial.productName" 
                class="product-image"
                loading="lazy"
              />
            </div>
            <div class="product-info">
              <span class="product-brand">{{ testimonial.brandName }}</span>
              <span class="product-name">{{ testimonial.productName }}</span>
            </div>
          </div>

          <!-- User Info -->
          <div class="testimonial-user">
            <div class="user-avatar">
              {{ testimonial.userName.charAt(0).toUpperCase() }}
            </div>
            <div class="user-details">
              <span class="user-name">{{ testimonial.userName }}</span>
              <span v-if="testimonial.isVerified" class="verified-badge">
                <i class="material-icons">verified</i>
                <span>Đã xác thực</span>
              </span>
            </div>
            <span class="testimonial-date">{{ formatDate(testimonial.date) }}</span>
          </div>
        </div>
      </div>

      <!-- View All Reviews Button -->
      <div class="text-center">
        <router-link 
          to="/home/reviews" 
          class="view-all-btn inline-flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-bold text-lg hover:from-purple-700 hover:to-indigo-700 transition-all duration-300 shadow-xl hover:shadow-2xl transform hover:scale-105"
        >
          <i class="material-icons">rate_review</i>
          Xem tất cả đánh giá
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import reviewService from '@/services/reviewService';
import logger from '@/utils/logger';

const testimonials = ref([]);
const loading = ref(false);

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return date.toLocaleDateString('vi-VN', options);
};

const fetchTestimonials = async () => {
  loading.value = true;
  try {
    // Lấy tối đa 3 reviews đã được duyệt cho homepage
    const response = await reviewService.getApprovedReviews(0, 3);
    
    // Map data từ API sang format của component
    testimonials.value = (response.content || []).map(review => ({
      id: review.id,
      rating: review.rating || 5,
      comment: review.comment || review.body || '',
      userName: review.userName || 'Khách hàng',
      isVerified: review.isVerifiedPurchase || false,
      productName: review.productName || 'Sản phẩm',
      brandName: review.brandName || 'Thương hiệu',
      productImage: review.productImage || '/placeholder-image.png',
      date: review.createdAt || new Date().toISOString()
    }));
    
    logger.log('Fetched testimonials for homepage:', testimonials.value.length);
  } catch (err) {
    logger.error('Error fetching testimonials:', err);
    // Fallback to empty array if API fails
    testimonials.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchTestimonials();
});
</script>

<style scoped>
.testimonials-section {
  position: relative;
}

.testimonial-card {
  background: white;
  border-radius: 1.5rem;
  padding: 1.5rem;
  border: 2px solid #e5e7eb;
  transition: all 0.3s ease;
  animation: fade-in-up 0.6s ease-out both;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  height: 100%;
}

.dark .testimonial-card {
  background: #1f2937;
  border-color: #374151;
}

.testimonial-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  border-color: #9333ea;
}

.dark .testimonial-card:hover {
  border-color: #a78bfa;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 10px 10px -5px rgba(0, 0, 0, 0.2);
}

.testimonial-rating {
  display: flex;
  gap: 0.25rem;
}

.star {
  font-size: 1.25rem;
  color: #d1d5db;
  transition: color 0.2s;
}

.star.filled {
  color: #fbbf24;
}

.testimonial-content {
  flex: 1;
}

.testimonial-text {
  font-size: 0.9375rem;
  line-height: 1.6;
  color: #374151;
  font-style: italic;
}

.dark .testimonial-text {
  color: #d1d5db;
}

.testimonial-product {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background: #f9fafb;
  border-radius: 0.75rem;
}

.dark .testimonial-product {
  background: #374151;
}

.product-image-wrapper {
  width: 3rem;
  height: 3rem;
  border-radius: 0.5rem;
  overflow: hidden;
  flex-shrink: 0;
  background: #e5e7eb;
}

.dark .product-image-wrapper {
  background: #4b5563;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  min-width: 0;
  flex: 1;
}

.product-brand {
  font-size: 0.75rem;
  font-weight: 600;
  color: #9333ea;
  text-transform: uppercase;
}

.dark .product-brand {
  color: #a78bfa;
}

.product-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #111827;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dark .product-name {
  color: #f9fafb;
}

.testimonial-user {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid #e5e7eb;
}

.dark .testimonial-user {
  border-top-color: #374151;
}

.user-avatar {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  background: linear-gradient(135deg, #9333ea, #7c3aed);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1rem;
  flex-shrink: 0;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #111827;
}

.dark .user-name {
  color: #f9fafb;
}

.verified-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  color: #10b981;
  font-weight: 500;
}

.verified-badge i {
  font-size: 1rem;
}

.testimonial-date {
  font-size: 0.75rem;
  color: #6b7280;
  white-space: nowrap;
}

.dark .testimonial-date {
  color: #9ca3af;
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
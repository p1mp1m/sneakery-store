<template>
  <section class="testimonials-section">
    <div class="container">
      <!-- Header -->
      <div class="section-header">
        <h2 class="section-title">Khách hàng nói gì về chúng tôi</h2>
        <p class="section-subtitle">Những đánh giá chân thực từ khách hàng đã mua sắm tại Sneakery Store</p>
      </div>

      <!-- Testimonials Grid -->
      <div class="testimonials-grid">
        <div 
          v-for="testimonial in testimonials" 
          :key="testimonial.id" 
          class="testimonial-card"
        >
          <!-- Rating -->
          <div class="testimonial-rating">
            <span 
              v-for="i in 5" 
              :key="i" 
              class="star"
              :class="{ active: i <= testimonial.rating }"
            >
              ★
            </span>
          </div>

          <!-- Content -->
          <div class="testimonial-content">
            <p class="testimonial-text">"{{ testimonial.comment }}"</p>
          </div>

          <!-- Product Info -->
          <div class="testimonial-product">
            <img :src="testimonial.productImage" :alt="testimonial.productName" class="product-image" />
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
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                Đã xác thực
              </span>
            </div>
            <span class="testimonial-date">{{ formatDate(testimonial.date) }}</span>
          </div>
        </div>
      </div>

      <!-- View All Button -->
      <div class="section-footer">
        <router-link to="/reviews" class="view-all-btn">
          Xem tất cả đánh giá
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const testimonials = ref([]);

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return date.toLocaleDateString('vi-VN', options);
};

onMounted(async () => {
  try {
    // Lấy top reviews từ API
    const response = await axios.get('http://localhost:8080/api/admin/reviews', {
      params: {
        page: 0,
        size: 6,
        isApproved: true,
        minRating: 4
      }
    });

    if (response.data && response.data.content) {
      testimonials.value = response.data.content.slice(0, 6).map(review => ({
        id: review.id,
        rating: review.rating,
        comment: review.reviewText || review.body || 'Sản phẩm chất lượng tốt!',
        userName: review.userName || 'Khách hàng',
        isVerified: review.isVerifiedPurchase || false,
        productName: review.productName,
        brandName: review.brandName || '',
        productImage: review.productImage || '/placeholder-image.png',
        date: review.createdAt
      }));
    }
  } catch (error) {
    console.error('Error loading testimonials:', error);
    // Fallback data for demo
    testimonials.value = [
      {
        id: 1,
        rating: 5,
        comment: 'Giày rất đẹp, chất lượng tốt, đúng với mô tả. Giao hàng nhanh và nhân viên tư vấn nhiệt tình!',
        userName: 'Nguyễn Văn A',
        isVerified: true,
        productName: 'Nike Air Max 90',
        brandName: 'Nike',
        productImage: '/placeholder-image.png',
        date: new Date().toISOString()
      },
      {
        id: 2,
        rating: 5,
        comment: 'Sản phẩm hàng chính hãng, đóng gói cẩn thận. Đã mua nhiều đôi rồi và rất hài lòng với chất lượng!',
        userName: 'Trần Thị B',
        isVerified: true,
        productName: 'Adidas Ultraboost',
        brandName: 'Adidas',
        productImage: '/placeholder-image.png',
        date: new Date().toISOString()
      },
      {
        id: 3,
        rating: 4,
        comment: 'Giày vừa chân, thiết kế đẹp. Giá cả hợp lý so với chất lượng. Sẽ quay lại mua thêm!',
        userName: 'Lê Văn C',
        isVerified: true,
        productName: 'Jordan 1 Retro',
        brandName: 'Jordan',
        productImage: '/placeholder-image.png',
        date: new Date().toISOString()
      }
    ];
  }
});
</script>





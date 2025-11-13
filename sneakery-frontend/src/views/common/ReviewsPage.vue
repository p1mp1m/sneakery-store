<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-8 md:py-12">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Page Header -->
      <div class="text-center mb-12">
        <h1 class="text-3xl md:text-4xl font-extrabold text-gray-900 dark:text-gray-100 mb-3">
          Đánh giá từ khách hàng
        </h1>
        <p class="text-lg text-gray-600 dark:text-gray-400">
          Những đánh giá chân thực từ khách hàng đã mua sắm tại Sneakery Store
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-20">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
        <p class="mt-4 text-gray-600 dark:text-gray-400">Đang tải đánh giá...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-20">
        <div class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-xl p-6 max-w-md mx-auto">
          <i class="material-icons text-red-600 dark:text-red-400 text-5xl mb-4">error_outline</i>
          <p class="text-red-600 dark:text-red-400 font-semibold">{{ error }}</p>
        </div>
      </div>

      <!-- Reviews Grid -->
      <div v-else-if="reviews.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <div 
          v-for="review in reviews" 
          :key="review.id" 
          class="bg-white dark:bg-gray-800 rounded-2xl p-6 border-2 border-gray-200 dark:border-gray-700 hover:border-purple-500 dark:hover:border-purple-600 hover:shadow-2xl transition-all duration-300"
        >
          <!-- Rating -->
          <div class="flex items-center gap-1 mb-4">
            <i 
              v-for="i in 5" 
              :key="i"
              class="material-icons text-lg"
              :class="i <= review.rating ? 'text-yellow-400' : 'text-gray-300'"
            >
              {{ i <= review.rating ? 'star' : 'star_border' }}
            </i>
          </div>

          <!-- Content -->
          <div class="mb-4">
            <p class="text-gray-700 dark:text-gray-300 italic">"{{ review.comment }}"</p>
          </div>

          <!-- Product Info -->
          <div class="bg-gray-50 dark:bg-gray-700 rounded-xl p-4 mb-4">
            <div class="flex items-center gap-3">
              <div class="w-12 h-12 rounded-lg overflow-hidden bg-gray-200 dark:bg-gray-600 flex-shrink-0">
                <img 
                  :src="review.productImage || '/placeholder-image.png'" 
                  :alt="review.productName" 
                  class="w-full h-full object-cover"
                  loading="lazy"
                />
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-xs font-semibold text-purple-600 dark:text-purple-400 uppercase mb-1">
                  {{ review.brandName }}
                </p>
                <p class="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate">
                  {{ review.productName }}
                </p>
              </div>
            </div>
          </div>

          <!-- User Info -->
          <div class="flex items-center justify-between pt-4 border-t border-gray-200 dark:border-gray-700">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-gradient-to-br from-purple-500 to-purple-600 text-white flex items-center justify-center font-bold flex-shrink-0">
                {{ review.userName.charAt(0).toUpperCase() }}
              </div>
              <div>
                <p class="text-sm font-semibold text-gray-900 dark:text-gray-100">{{ review.userName }}</p>
                <div v-if="review.isVerified" class="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
                  <i class="material-icons text-sm">verified</i>
                  <span>Đã xác thực</span>
                </div>
              </div>
            </div>
            <span class="text-xs text-gray-500 dark:text-gray-400 whitespace-nowrap">
              {{ formatDate(review.date) }}
            </span>
          </div>
        </div>
      </div>

      <!-- Load More Button -->
      <div v-if="reviews.length > 0 && hasMore" class="text-center mt-8">
        <button
          @click="loadMore"
          :disabled="loadingMore"
          class="inline-flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-bold text-lg hover:from-purple-700 hover:to-indigo-700 transition-all duration-300 shadow-xl hover:shadow-2xl transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
        >
          <span v-if="loadingMore" class="inline-block animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></span>
          <i v-else class="material-icons">expand_more</i>
          {{ loadingMore ? 'Đang tải...' : 'Xem thêm đánh giá' }}
        </button>
      </div>

      <!-- No More Reviews Message -->
      <div v-if="reviews.length > 0 && !hasMore" class="text-center mt-8 py-6">
        <p class="text-gray-500 dark:text-gray-400 text-sm">
          <i class="material-icons text-base align-middle mr-1">check_circle</i>
          Đã hiển thị tất cả đánh giá
        </p>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20">
        <i class="material-icons text-gray-400 text-6xl mb-4">rate_review</i>
        <p class="text-gray-600 dark:text-gray-400 text-lg mb-2">Chưa có đánh giá nào</p>
        <p class="text-gray-500 dark:text-gray-500 text-sm">Hãy quay lại sau để xem đánh giá từ khách hàng</p>
        <router-link 
          to="/home/products" 
          class="inline-flex items-center gap-2 mt-6 px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors"
        >
          Xem sản phẩm
          <i class="material-icons">arrow_forward</i>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import reviewService from '@/services/reviewService';
import logger from '@/utils/logger';

const reviews = ref([]);
const loading = ref(false);
const loadingMore = ref(false);
const error = ref(null);
const hasMore = ref(true);
const currentPage = ref(0);
const pageSize = ref(6); // Load 6 reviews mỗi lần
const totalPages = ref(0);

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return date.toLocaleDateString('vi-VN', options);
};

const fetchReviews = async (page = 0, append = false) => {
  if (append) {
    loadingMore.value = true;
  } else {
    loading.value = true;
    error.value = null;
  }
  
  try {
    const response = await reviewService.getApprovedReviews(page, pageSize.value);
    
    // Map data từ API sang format của component
    const newReviews = (response.content || []).map(review => ({
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
    
    if (append) {
      reviews.value = [...reviews.value, ...newReviews];
    } else {
      reviews.value = newReviews;
    }
    
    // Update pagination info
    currentPage.value = response.number || page;
    totalPages.value = response.totalPages || 0;
    hasMore.value = !response.last && (response.number || page) < (response.totalPages || 0) - 1;
    
    logger.log(`Fetched reviews page ${page}:`, newReviews.length, `Total:`, reviews.value.length, `Has more:`, hasMore.value);
  } catch (err) {
    logger.error('Error fetching reviews:', err);
    if (!append) {
      error.value = 'Không thể tải đánh giá. Vui lòng thử lại sau.';
    }
  } finally {
    if (append) {
      loadingMore.value = false;
    } else {
      loading.value = false;
    }
  }
};

const loadMore = () => {
  if (!loadingMore.value && hasMore.value) {
    fetchReviews(currentPage.value + 1, true);
  }
};

onMounted(() => {
  fetchReviews(0, false);
});
</script>

<style scoped>
/* Additional styles if needed */
</style>


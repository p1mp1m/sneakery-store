<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center p-4">
    <div class="max-w-md w-full">
      <!-- Loading State -->
      <div v-if="loading" class="text-center bg-white dark:bg-gray-800 rounded-xl p-8 shadow-lg">
        <div class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-600 border-t-transparent mb-4"></div>
        <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-2">Đang xử lý thanh toán...</h2>
        <p class="text-gray-600 dark:text-gray-400">Vui lòng đợi trong giây lát</p>
      </div>

      <!-- Success State -->
      <div v-else-if="success" class="text-center bg-white dark:bg-gray-800 rounded-xl p-8 shadow-lg">
        <div class="w-20 h-20 mx-auto mb-4 rounded-full bg-green-100 dark:bg-green-900/30 flex items-center justify-center">
          <i class="material-icons text-green-600 dark:text-green-400 text-5xl">check_circle</i>
        </div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Thanh toán thành công!</h2>
        <p class="text-gray-600 dark:text-gray-400 mb-6">Đơn hàng của bạn đã được xác nhận và đang được xử lý.</p>
        <div class="space-y-3">
          <router-link
            :to="{ name: 'UserOrders' }"
            class="block w-full px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all"
          >
            Xem đơn hàng
          </router-link>
          <router-link
            :to="{ name: 'home' }"
            class="block w-full px-6 py-3 bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-gray-100 rounded-lg font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
          >
            Về trang chủ
          </router-link>
        </div>
      </div>

      <!-- Error State -->
      <div v-else class="text-center bg-white dark:bg-gray-800 rounded-xl p-8 shadow-lg">
        <div class="w-20 h-20 mx-auto mb-4 rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
          <i class="material-icons text-red-600 dark:text-red-400 text-5xl">error</i>
        </div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Thanh toán thất bại</h2>
        <p class="text-gray-600 dark:text-gray-400 mb-2">{{ errorMessage }}</p>
        <p class="text-sm text-gray-500 dark:text-gray-400 mb-6">Vui lòng thử lại hoặc chọn phương thức thanh toán khác.</p>
        <div class="space-y-3">
          <router-link
            :to="{ name: 'Checkout' }"
            class="block w-full px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all"
          >
            Thử lại
          </router-link>
          <router-link
            :to="{ name: 'home' }"
            class="block w-full px-6 py-3 bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-gray-100 rounded-lg font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
          >
            Về trang chủ
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import toastService from '@/utils/toastService';

const route = useRoute();
const loading = ref(true);
const success = ref(false);
const errorMessage = ref('Có lỗi xảy ra trong quá trình thanh toán');

onMounted(async () => {
  try {
    // Get all query params from VNPay callback
    const params = route.query;
    
    console.log('VNPay callback params:', params);

    // Call backend to verify payment
    const response = await axios.get('/api/payment/vnpay/callback', { params });

    if (response.data?.success) {
      success.value = true;
      toastService.success('Thành công', 'Thanh toán thành công!');
    } else {
      success.value = false;
      errorMessage.value = response.data?.message || 'Thanh toán thất bại';
    }
  } catch (error) {
    console.error('Error processing VNPay callback:', error);
    success.value = false;
    errorMessage.value = error.response?.data?.message || 'Có lỗi xảy ra trong quá trình xử lý thanh toán';
    toastService.error('Lỗi', errorMessage.value);
  } finally {
    loading.value = false;
  }
});
</script>

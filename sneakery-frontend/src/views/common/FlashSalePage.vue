<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-7xl mx-auto px-4">
      <!-- Header -->
      <div class="mb-8">
        <div class="bg-gradient-to-r from-orange-500 via-red-500 to-pink-500 dark:from-orange-600 dark:via-red-600 dark:to-pink-600 rounded-2xl p-8 shadow-xl relative overflow-hidden">
          <!-- Animated Background -->
          <div class="absolute inset-0 opacity-20">
            <div class="absolute inset-0 animate-pulse" style="background-image: repeating-linear-gradient(45deg, transparent, transparent 20px, rgba(255,255,255,0.1) 20px, rgba(255,255,255,0.1) 40px);"></div>
          </div>
          
          <div class="relative z-10 flex flex-col md:flex-row md:items-center md:justify-between gap-6">
            <div class="flex items-center gap-6">
              <div class="w-20 h-20 bg-white/20 backdrop-blur-sm rounded-2xl flex items-center justify-center shadow-xl animate-pulse">
                <i class="material-icons text-white text-4xl">bolt</i>
              </div>
              <div>
                <h1 class="text-4xl md:text-5xl font-extrabold text-white mb-3">Flash Sale</h1>
                <p class="text-white/90 text-lg">Ưu đãi đặc biệt - Giảm giá sốc</p>
              </div>
            </div>
            
            <!-- Countdown Timer -->
            <div v-if="nearestEndTime" class="flex items-center gap-4 bg-white/20 backdrop-blur-sm rounded-xl p-4 border border-white/30">
              <div class="text-white/90 text-sm font-medium">Kết thúc trong:</div>
              <div class="flex items-center gap-2">
                <div class="px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg text-white font-bold text-lg shadow-lg">
                  {{ countdown.days }}
                </div>
                <span class="text-white font-bold">:</span>
                <div class="px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg text-white font-bold text-lg shadow-lg">
                  {{ countdown.hours }}
                </div>
                <span class="text-white font-bold">:</span>
                <div class="px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg text-white font-bold text-lg shadow-lg">
                  {{ countdown.minutes }}
                </div>
                <span class="text-white font-bold">:</span>
                <div class="px-4 py-2 bg-white/30 backdrop-blur-sm rounded-lg text-white font-bold text-lg shadow-lg">
                  {{ countdown.seconds }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-20">
        <div class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-purple-600 border-t-transparent mb-4"></div>
        <p class="text-gray-600 dark:text-gray-400 font-medium">Đang tải flash sale...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-20 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="w-20 h-20 mx-auto mb-6 bg-red-100 dark:bg-red-900/30 rounded-full flex items-center justify-center">
          <i class="material-icons text-5xl text-red-500">error_outline</i>
        </div>
        <h3 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">{{ error }}</h3>
        <button 
          @click="fetchFlashSales" 
          class="inline-flex items-center gap-2 px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors"
        >
          <i class="material-icons">refresh</i>
          Thử lại
        </button>
      </div>

      <!-- Flash Sale Products -->
      <div v-else-if="flashSaleProducts.length > 0">
        <div class="mb-6 flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">
              Sản phẩm Flash Sale
            </h2>
            <p class="text-gray-600 dark:text-gray-400">
              Tìm thấy <span class="font-semibold text-purple-600 dark:text-purple-400">{{ flashSaleProducts.length }}</span> sản phẩm đang flash sale
            </p>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div
            v-for="product in flashSaleProducts"
            :key="product.id"
            class="group bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 hover:shadow-2xl hover:scale-105 transition-all duration-300 overflow-hidden transform"
          >
            <ProductCard :product="product" />
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="w-24 h-24 mx-auto mb-6 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center">
          <i class="material-icons text-5xl text-gray-400">bolt</i>
        </div>
        <h3 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Hiện chưa có Flash Sale</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">Hãy quay lại sau để xem các ưu đãi đặc biệt</p>
        <router-link 
          to="/home/products" 
          class="inline-flex items-center gap-2 px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors"
        >
          <i class="material-icons">shopping_bag</i>
          Xem tất cả sản phẩm
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useFlashSaleStore } from '@/stores/flashSale';
import productService from '@/services/productService';
import ProductCard from '@/assets/components/products/ProductCard.vue';
import logger from '@/utils/logger';

const flashSaleStore = useFlashSaleStore();

// State
const loading = ref(false);
const error = ref(null);
const flashSaleProducts = ref([]);
const countdown = ref({
  days: '00',
  hours: '00',
  minutes: '00',
  seconds: '00'
});

let countdownInterval = null;

// Computed - Get nearest end time from active flash sales
const nearestEndTime = computed(() => {
  if (!flashSaleStore.activeFlashSales || flashSaleStore.activeFlashSales.length === 0) {
    return null;
  }
  
  const now = new Date();
  const activeSales = flashSaleStore.activeFlashSales.filter(sale => {
    const start = new Date(sale.startTime);
    const end = new Date(sale.endTime);
    return now >= start && now <= end;
  });
  
  if (activeSales.length === 0) {
    return null;
  }
  
  // Find the nearest end time
  const endTimes = activeSales.map(sale => new Date(sale.endTime));
  const nearestEnd = new Date(Math.min(...endTimes));
  return nearestEnd;
});

// Methods
const fetchFlashSales = async () => {
  try {
    loading.value = true;
    error.value = null;
    
    // Fetch active flash sales
    await flashSaleStore.fetchActiveFlashSales();
    
    if (flashSaleStore.activeFlashSales.length === 0) {
      flashSaleProducts.value = [];
      return;
    }
    
    // Fetch products for each flash sale
    const productIds = flashSaleStore.activeFlashSales.map(sale => sale.productId);
    const products = [];
    
    for (const productId of productIds) {
      try {
        const product = await productService.getProductById(productId);
        if (product) {
          // Add flash sale info to product
          const flashSale = flashSaleStore.getFlashSaleForProduct(productId);
          if (flashSale) {
            product.flashSale = flashSale;
            // Ensure brandName is set from flashSale if product doesn't have it
            if (!product.brandName && !product.brand?.name && flashSale.brandName) {
              product.brandName = flashSale.brandName;
            }
            // Ensure brand object is set if we have brandName
            if (product.brandName && !product.brand) {
              product.brand = { name: product.brandName };
            }
            products.push(product);
          }
        }
      } catch (err) {
        logger.warn(`Failed to fetch product ${productId}:`, err);
      }
    }
    
    flashSaleProducts.value = products;
    
  } catch (err) {
    logger.error('Error fetching flash sales:', err);
    error.value = err.response?.data?.message || 'Không thể tải flash sale';
  } finally {
    loading.value = false;
  }
};

const updateCountdown = () => {
  if (!nearestEndTime.value) {
    countdown.value = { days: '00', hours: '00', minutes: '00', seconds: '00' };
    return;
  }
  
  const now = new Date();
  const end = new Date(nearestEndTime.value);
  const diff = end - now;
  
  if (diff <= 0) {
    countdown.value = { days: '00', hours: '00', minutes: '00', seconds: '00' };
    // Refresh flash sales when countdown ends
    fetchFlashSales();
    return;
  }
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((diff % (1000 * 60)) / 1000);
  
  countdown.value = {
    days: days.toString().padStart(2, '0'),
    hours: hours.toString().padStart(2, '0'),
    minutes: minutes.toString().padStart(2, '0'),
    seconds: seconds.toString().padStart(2, '0')
  };
};

// Lifecycle
onMounted(() => {
  fetchFlashSales();
  updateCountdown();
  countdownInterval = setInterval(updateCountdown, 1000);
});

onUnmounted(() => {
  if (countdownInterval) {
    clearInterval(countdownInterval);
  }
});
</script>

<style scoped>
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>


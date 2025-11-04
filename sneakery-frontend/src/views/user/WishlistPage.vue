<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-6xl mx-auto px-4">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <!-- Header -->
        <div class="flex items-center gap-3 mb-6 pb-6 border-b border-gray-200 dark:border-gray-700">
          <div class="w-10 h-10 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-purple-600 dark:text-purple-400">
              <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z"/>
            </svg>
          </div>
          <span class="text-2xl font-bold text-gray-900 dark:text-gray-100">Danh sách yêu thích</span>
        </div>

        <!-- Empty State -->
        <div v-if="wishlistItems.length === 0" class="text-center py-16">
          <svg width="120" height="120" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="mx-auto mb-6 text-gray-300 dark:text-gray-600">
            <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" fill="currentColor" opacity="0.3"/>
          </svg>
          <p class="text-gray-600 dark:text-gray-400 mb-6 text-lg">Danh sách yêu thích của bạn đang trống</p>
          <router-link to="/home" class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all">
            Bắt đầu mua sắm
          </router-link>
        </div>

        <div v-else>
          <!-- Wishlist Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
            <div 
              v-for="item in wishlistItems" 
              :key="item.id" 
              class="group relative bg-white dark:bg-gray-700 rounded-xl border border-gray-200 dark:border-gray-600 overflow-hidden hover:shadow-lg transition-all"
            >
              <div class="relative">
                <img :src="item.imageUrl" :alt="item.name" class="w-full h-64 object-cover" />
                <div class="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-all flex items-center justify-center gap-2 opacity-0 group-hover:opacity-100">
                  <button 
                    @click="removeFromWishlist(item)"
                    class="w-10 h-10 rounded-full bg-red-500 text-white flex items-center justify-center hover:bg-red-600 transition-colors shadow-lg"
                  >
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="3,6 5,6 21,6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                  <button 
                    @click="addToCart(item)"
                    class="w-10 h-10 rounded-full bg-purple-600 text-white flex items-center justify-center hover:bg-purple-700 transition-colors shadow-lg"
                  >
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="9" cy="21" r="1"/>
                      <circle cx="20" cy="21" r="1"/>
                      <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                    </svg>
                  </button>
                </div>
                <div v-if="!item.isActive" class="absolute top-2 right-2">
                  <span class="px-2 py-1 bg-red-500 text-white text-xs font-semibold rounded">Ngừng bán</span>
                </div>
              </div>
              
              <div class="p-4">
                <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-1 line-clamp-2">{{ item.productName }}</h3>
                <p class="text-sm text-gray-500 dark:text-gray-400 mb-2">{{ item.brandName }}</p>
                <div class="flex items-center gap-2 mb-2">
                  <span class="font-bold text-purple-600 dark:text-purple-400">{{ formatCurrency(item.price) }}</span>
                  <span v-if="item.priceSale && item.priceBase > item.priceSale" class="text-sm text-gray-400 line-through">
                    {{ formatCurrency(item.priceBase) }}
                  </span>
                </div>
                
                <div class="text-xs text-gray-500 dark:text-gray-400">
                  Đã thêm: {{ formatDate(item.addedAt) }}
                </div>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex flex-wrap gap-3 pt-6 border-t border-gray-200 dark:border-gray-700">
            <button @click="shareWishlist" :disabled="wishlistItems.length === 0" class="px-4 py-2 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center gap-2">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="18" cy="5" r="3"/>
                <circle cx="6" cy="12" r="3"/>
                <circle cx="18" cy="19" r="3"/>
                <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/>
                <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>
              </svg>
              Chia sẻ
            </button>
            <button @click="clearWishlist" :disabled="wishlistItems.length === 0" class="px-4 py-2 border border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg font-semibold hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center gap-2">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="3,6 5,6 21,6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
              Xóa tất cả
            </button>
            <button type="primary" @click="addAllToCart" :disabled="wishlistItems.length === 0" class="flex-1 px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center justify-center gap-2">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="9" cy="21" r="1"/>
                <circle cx="20" cy="21" r="1"/>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
              </svg>
              Thêm tất cả vào giỏ hàng
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Share Modal -->
    <div v-if="showShareModal" class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4" @click.self="showShareModal = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full">
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100">Chia sẻ wishlist</h3>
          <button @click="showShareModal = false" class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">×</button>
        </div>
        <div class="p-6 space-y-6">
          <p class="text-gray-600 dark:text-gray-400">Chia sẻ danh sách yêu thích của bạn với bạn bè:</p>
          
          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Link chia sẻ:</label>
            <div class="flex gap-2">
              <input v-model="shareLink" readonly class="flex-1 px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-gray-100" />
              <button @click="copyLink" class="px-4 py-2 bg-purple-600 text-white rounded-lg font-semibold hover:bg-purple-700 transition-colors">
                Copy
              </button>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-900 dark:text-gray-100 mb-3">Chia sẻ qua:</label>
            <div class="flex gap-3">
              <button @click="shareOnFacebook" class="w-12 h-12 rounded-full bg-[#1877f2] text-white flex items-center justify-center hover:bg-[#166fe5] transition-colors shadow-lg text-xl font-bold">
                f
              </button>
              <button @click="shareOnTwitter" class="w-12 h-12 rounded-full bg-[#1da1f2] text-white flex items-center justify-center hover:bg-[#1a91da] transition-colors shadow-lg">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M23 3a10.9 10.9 0 01-3.14 1.53 4.48 4.48 0 00-7.86 3v1A10.66 10.66 0 013 4s-4 9 5 13a11.64 11.64 0 01-7 2c9 5 20 0 20-11.5a4.5 4.5 0 00-.08-.83A7.72 7.72 0 0023 3z"/>
                </svg>
              </button>
              <button @click="shareOnWhatsApp" class="w-12 h-12 rounded-full bg-[#25d366] text-white flex items-center justify-center hover:bg-[#20ba5a] transition-colors shadow-lg">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413Z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
        <div class="flex justify-end p-6 border-t border-gray-200 dark:border-gray-700">
          <button @click="showShareModal = false" class="px-6 py-3 bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-gray-100 rounded-lg font-semibold hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useWishlistStore } from '@/stores/wishlist';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const wishlistStore = useWishlistStore();
const authStore = useAuthStore();

// Computed từ store
const wishlistItems = computed(() => wishlistStore.wishlistItems);
const loading = computed(() => wishlistStore.loading);

// Share state
const showShareModal = ref(false);
const shareLink = ref('');

// Load wishlist khi component mount
onMounted(async () => {
  try {
    await wishlistStore.fetchWishlist();
  } catch (error) {
    ElMessage.error('Không thể tải danh sách yêu thích');
  }
});

const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { 
    day: '2-digit', 
    month: '2-digit', 
    year: 'numeric' 
  });
};

const removeFromWishlist = async (item) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn xóa "${item.productName}" khỏi danh sách yêu thích?`,
      'Xác nhận',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
    );
    
    await wishlistStore.removeFromWishlist(item.productId);
    ElMessage.success(`Đã xóa "${item.productName}" khỏi danh sách yêu thích`);
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Không thể xóa sản phẩm');
    }
  }
};

const addToCart = (item) => {
  if (!item.isActive) {
    ElMessage.warning('Sản phẩm này đã ngừng bán');
    return;
  }
  
  // Navigate to product detail page để user chọn size
  router.push(`/home/products/${item.productId}`);
};

const addAllToCart = () => {
  const activeItems = wishlistItems.value.filter(item => item.isActive);
  
  if (activeItems.length === 0) {
    ElMessage.warning('Không có sản phẩm nào khả dụng trong danh sách yêu thích');
    return;
  }
  
  // Navigate về trang sản phẩm để thêm từng cái
  ElMessage.info('Vui lòng chọn size cho từng sản phẩm');
};

const clearWishlist = async () => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn xóa tất cả sản phẩm khỏi danh sách yêu thích?',
      'Xác nhận',
      {
        confirmButtonText: 'Xóa tất cả',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
    );
    
    await wishlistStore.clearWishlist();
    ElMessage.success('Đã xóa tất cả sản phẩm khỏi danh sách yêu thích');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Không thể xóa wishlist');
    }
  }
};

const goToHome = () => {
  router.push('/home');
};

const shareWishlist = () => {
  const userId = authStore.currentUser?.id;
  if (userId) {
    shareLink.value = `${window.location.origin}/wishlist/shared/${userId}`;
    showShareModal.value = true;
  } else {
    ElMessage.error('Vui lòng đăng nhập để chia sẻ wishlist');
  }
};

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(shareLink.value);
    ElMessage.success('Đã copy link vào clipboard');
  } catch (error) {
    ElMessage.error('Không thể copy link');
  }
};

const shareOnFacebook = () => {
  const url = encodeURIComponent(shareLink.value);
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}`, '_blank');
};

const shareOnTwitter = () => {
  const text = encodeURIComponent('Xem danh sách yêu thích của tôi trên Sneakery Store!');
  const url = encodeURIComponent(shareLink.value);
  window.open(`https://twitter.com/intent/tweet?text=${text}&url=${url}`, '_blank');
};

const shareOnWhatsApp = () => {
  const text = encodeURIComponent('Xem danh sách yêu thích của tôi: ' + shareLink.value);
  window.open(`https://wa.me/?text=${text}`, '_blank');
};
</script>

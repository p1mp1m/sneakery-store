<template>
  <button
    :class="[
      'flex items-center justify-center gap-2 transition-all duration-200 font-medium',
      circle ? 'rounded-full w-10 h-10' : 'rounded-lg px-4 py-2',
      isInWishlist 
        ? 'bg-red-500 hover:bg-red-600 text-white' 
        : 'bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-300',
      loading && 'opacity-50 cursor-not-allowed'
    ]"
    :disabled="loading"
    :title="isInWishlist ? 'Xóa khỏi yêu thích' : 'Thêm vào yêu thích'"
    @click.stop="handleToggle"
    v-bind="$attrs"
  >
    <i v-if="loading" class="material-icons animate-spin">refresh</i>
    <i v-else class="material-icons">{{ isInWishlist ? 'star' : 'star_border' }}</i>
    <span v-if="!circle">
      {{ isInWishlist ? 'Đã yêu thích' : 'Yêu thích' }}
    </span>
  </button>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import notificationService from '@/utils/notificationService'
import { useWishlistStore } from '@/stores/wishlist'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const props = defineProps({
  productId: {
    type: Number,
    required: true
  },
  circle: {
    type: Boolean,
    default: false
  }
})

const wishlistStore = useWishlistStore()
const authStore = useAuthStore()
const router = useRouter()

const loading = ref(false)

// Check if product is in wishlist
const isInWishlist = computed(() => {
  return wishlistStore.isInWishlist(props.productId)
})

// Handle toggle wishlist
const handleToggle = async () => {
  // Kiểm tra đã đăng nhập chưa
  if (!authStore.isAuthenticated) {
    notificationService.warning('Cảnh báo', 'Vui lòng đăng nhập để sử dụng tính năng này')
    router.push('/login')
    return
  }

  loading.value = true

  try {
    const result = await wishlistStore.toggleWishlist(props.productId)
    
    if (result.action === 'added') {
      notificationService.success('Thành công', 'Đã thêm vào danh sách yêu thích')
    } else {
      notificationService.success('Thành công', 'Đã xóa khỏi danh sách yêu thích')
    }
  } catch (error) {
    console.error('Error toggling wishlist:', error)
    
    const errorMessage = error.response?.data?.message || 'Có lỗi xảy ra'
    notificationService.error('Lỗi', errorMessage)
  } finally {
    loading.value = false
  }
}
</script>





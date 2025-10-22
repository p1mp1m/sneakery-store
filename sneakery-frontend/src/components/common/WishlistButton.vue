<template>
  <el-button
    :type="isInWishlist ? 'danger' : 'default'"
    :icon="isInWishlist ? StarFilled : Star"
    :circle="circle"
    :loading="loading"
    :title="isInWishlist ? 'Xóa khỏi yêu thích' : 'Thêm vào yêu thích'"
    @click.stop="handleToggle"
    v-bind="$attrs"
  >
    <template v-if="!circle">
      {{ isInWishlist ? 'Đã yêu thích' : 'Yêu thích' }}
    </template>
  </el-button>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
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
    ElMessage.warning('Vui lòng đăng nhập để sử dụng tính năng này')
    router.push('/login')
    return
  }

  loading.value = true

  try {
    const result = await wishlistStore.toggleWishlist(props.productId)
    
    if (result.action === 'added') {
      ElMessage.success('Đã thêm vào danh sách yêu thích')
    } else {
      ElMessage.success('Đã xóa khỏi danh sách yêu thích')
    }
  } catch (error) {
    console.error('Error toggling wishlist:', error)
    
    const errorMessage = error.response?.data?.message || 'Có lỗi xảy ra'
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.el-button {
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: scale(1.05);
}
</style>


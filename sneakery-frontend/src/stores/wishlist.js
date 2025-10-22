/**
 * Wishlist Store - Pinia
 * Quản lý state của wishlist
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import wishlistService from '@/services/wishlistService'

export const useWishlistStore = defineStore('wishlist', () => {
  // State
  const wishlistItems = ref([])
  const loading = ref(false)
  const error = ref(null)
  
  // Computed
  const wishlistCount = computed(() => wishlistItems.value.length)
  
  const wishlistProductIds = computed(() => 
    wishlistItems.value.map(item => item.productId)
  )
  
  const totalValue = computed(() => 
    wishlistItems.value.reduce((sum, item) => sum + (item.price || 0), 0)
  )

  // Actions
  
  /**
   * Fetch wishlist từ server
   */
  const fetchWishlist = async () => {
    loading.value = true
    error.value = null
    
    try {
      const data = await wishlistService.getWishlist()
      wishlistItems.value = data
      return data
    } catch (err) {
      error.value = err.response?.data?.message || 'Không thể tải wishlist'
      console.error('Error fetching wishlist:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Thêm sản phẩm vào wishlist
   */
  const addToWishlist = async (productId) => {
    loading.value = true
    error.value = null
    
    try {
      const data = await wishlistService.addToWishlist(productId)
      wishlistItems.value.unshift(data) // Thêm vào đầu mảng
      return data
    } catch (err) {
      error.value = err.response?.data?.message || 'Không thể thêm vào wishlist'
      console.error('Error adding to wishlist:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Xóa sản phẩm khỏi wishlist
   */
  const removeFromWishlist = async (productId) => {
    loading.value = true
    error.value = null
    
    try {
      await wishlistService.removeFromWishlist(productId)
      
      // Xóa khỏi state local
      wishlistItems.value = wishlistItems.value.filter(
        item => item.productId !== productId
      )
    } catch (err) {
      error.value = err.response?.data?.message || 'Không thể xóa khỏi wishlist'
      console.error('Error removing from wishlist:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Toggle wishlist (thêm/xóa)
   */
  const toggleWishlist = async (productId) => {
    const isInWishlist = wishlistProductIds.value.includes(productId)
    
    if (isInWishlist) {
      await removeFromWishlist(productId)
      return { action: 'removed', inWishlist: false }
    } else {
      await addToWishlist(productId)
      return { action: 'added', inWishlist: true }
    }
  }

  /**
   * Kiểm tra sản phẩm có trong wishlist không
   */
  const isInWishlist = (productId) => {
    return wishlistProductIds.value.includes(productId)
  }

  /**
   * Xóa toàn bộ wishlist
   */
  const clearWishlist = async () => {
    loading.value = true
    error.value = null
    
    try {
      await wishlistService.clearWishlist()
      wishlistItems.value = []
    } catch (err) {
      error.value = err.response?.data?.message || 'Không thể xóa wishlist'
      console.error('Error clearing wishlist:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Clear error
   */
  const clearError = () => {
    error.value = null
  }

  /**
   * Reset store
   */
  const reset = () => {
    wishlistItems.value = []
    loading.value = false
    error.value = null
  }

  return {
    // State
    wishlistItems,
    loading,
    error,
    
    // Computed
    wishlistCount,
    wishlistProductIds,
    totalValue,
    
    // Actions
    fetchWishlist,
    addToWishlist,
    removeFromWishlist,
    toggleWishlist,
    isInWishlist,
    clearWishlist,
    clearError,
    reset
  }
})


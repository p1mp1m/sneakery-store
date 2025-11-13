/**
 * Cart Store - Pinia
 * Quản lý state của giỏ hàng
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import userService from '@/services/userService';
import * as guestCartService from '@/services/guestCartService';
import logger from '@/utils/logger';

export const useCartStore = defineStore('cart', () => {
  // State
  const cart = ref(null);
  const loading = ref(false);
  const error = ref(null);
  const lastUpdated = ref(null);
  
  // Stores
  const authStore = useAuthStore();
  
  // Computed
  const cartItems = computed(() => cart.value?.items || []);
  const cartCount = computed(() => cart.value?.totalItems || 0);
  const cartSubTotal = computed(() => cart.value?.subTotal || 0);
  const isEmpty = computed(() => !cart.value || cartItems.value.length === 0);
  
  // Actions
  
  /**
   * Fetch cart từ server
   */
  const fetchCart = async () => {
    loading.value = true;
    error.value = null;
    
    try {
      if (authStore.isAuthenticated) {
        // User đã đăng nhập: lấy từ user cart
        cart.value = await userService.getMyCart();
      } else {
        // User chưa đăng nhập: lấy từ guest cart
        cart.value = await guestCartService.getGuestCart();
      }
      
      lastUpdated.value = new Date();
      logger.log('Cart fetched successfully:', cartCount.value, 'items');
      return cart.value;
    } catch (err) {
      error.value = err.response?.data?.message || 'Không thể tải giỏ hàng';
      logger.error('Error fetching cart:', err);
      
      // Nếu lỗi 401, clear cart
      if (err.response?.status === 401) {
        cart.value = null;
      }
      
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Thêm sản phẩm vào giỏ hàng
   */
  const addItem = async (variantId, quantity = 1) => {
    loading.value = true;
    error.value = null;
    
    try {
      if (authStore.isAuthenticated) {
        // User đã đăng nhập: dùng user cart
        await userService.addItemToCart({
          variantId,
          quantity
        });
        logger.log('Item added to user cart:', variantId);
      } else {
        // User chưa đăng nhập: dùng guest cart
        await guestCartService.addToGuestCart(variantId, quantity);
        logger.log('Item added to guest cart:', variantId);
      }
      
      // Refresh cart sau khi thêm
      await fetchCart();
      
      return cart.value;
    } catch (err) {
      error.value = err.response?.data?.message || 'Không thể thêm vào giỏ hàng';
      logger.error('Error adding item to cart:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Xóa sản phẩm khỏi giỏ hàng
   */
  const removeItem = async (variantId) => {
    loading.value = true;
    error.value = null;
    
    try {
      if (authStore.isAuthenticated) {
        // User đã đăng nhập: dùng user cart
        await userService.removeItemFromCart(variantId);
        logger.log('Item removed from user cart:', variantId);
      } else {
        // User chưa đăng nhập: dùng guest cart
        await guestCartService.removeFromGuestCart(variantId);
        logger.log('Item removed from guest cart:', variantId);
      }
      
      // Refresh cart sau khi xóa
      await fetchCart();
      
      return cart.value;
    } catch (err) {
      error.value = err.response?.data?.message || 'Không thể xóa khỏi giỏ hàng';
      logger.error('Error removing item from cart:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Cập nhật số lượng sản phẩm
   */
  const updateQuantity = async (variantId, quantity) => {
    if (quantity < 1) {
      // Nếu quantity < 1, xóa item
      return await removeItem(variantId);
    }
    
    // Cập nhật bằng cách thêm lại với quantity mới
    return await addItem(variantId, quantity);
  };
  
  /**
   * Xóa toàn bộ giỏ hàng
   */
  const clearCart = async () => {
    loading.value = true;
    error.value = null;
    
    try {
      if (authStore.isAuthenticated) {
        // User đã đăng nhập: xóa từng item
        if (cart.value?.items) {
          for (const item of cart.value.items) {
            await userService.removeItemFromCart(item.variantId);
          }
        }
      } else {
        // User chưa đăng nhập: clear guest cart
        guestCartService.clearGuestCart();
      }
      
      cart.value = null;
      lastUpdated.value = new Date();
      logger.log('Cart cleared');
    } catch (err) {
      error.value = err.response?.data?.message || 'Không thể xóa giỏ hàng';
      logger.error('Error clearing cart:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  /**
   * Refresh cart (force reload)
   */
  const refreshCart = async () => {
    return await fetchCart();
  };
  
  /**
   * Kiểm tra sản phẩm có trong giỏ hàng không
   */
  const isInCart = (variantId) => {
    if (!cart.value || !cart.value.items) return false;
    return cart.value.items.some(item => item.variantId === variantId);
  };
  
  /**
   * Lấy số lượng của một variant trong giỏ hàng
   */
  const getItemQuantity = (variantId) => {
    if (!cart.value || !cart.value.items) return 0;
    const item = cart.value.items.find(item => item.variantId === variantId);
    return item?.quantity || 0;
  };
  
  /**
   * Clear error
   */
  const clearError = () => {
    error.value = null;
  };
  
  /**
   * Reset store
   */
  const reset = () => {
    cart.value = null;
    loading.value = false;
    error.value = null;
    lastUpdated.value = null;
  };
  
  return {
    // State
    cart,
    loading,
    error,
    lastUpdated,
    
    // Computed
    cartItems,
    cartCount,
    cartSubTotal,
    isEmpty,
    
    // Actions
    fetchCart,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    refreshCart,
    isInCart,
    getItemQuantity,
    clearError,
    reset,
  };
});


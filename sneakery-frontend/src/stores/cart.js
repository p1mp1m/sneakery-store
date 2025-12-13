/**
 * Cart Store - Pinia
 * Quản lý state của giỏ hàng
 */

import { defineStore } from "pinia"
import { ref, computed } from "vue"
import { useAuthStore } from "@/stores/auth"
import userService from "@/services/userService"
import * as guestCartService from "@/services/guestCartService"
import logger from "@/utils/logger"
import axios from "axios"

export const useCartStore = defineStore("cart", () => {
  // ================= STATE =================
  const cart = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const lastUpdated = ref(null)

  const authStore = useAuthStore()

  // ================= COMPUTED =================
  const cartItems = computed(() => cart.value?.items || [])
  const cartCount = computed(() => cart.value?.totalItems || 0)
  const cartSubTotal = computed(() => cart.value?.subTotal || 0)
  const isEmpty = computed(() => !cart.value || cartItems.value.length === 0)

  // ================= ACTIONS =================

  /**
   * Fetch cart từ server
   */
  const fetchCart = async () => {
    loading.value = true
    error.value = null

    try {
      if (authStore.isAuthenticated) {
        cart.value = await userService.getMyCart()
      } else {
        cart.value = await guestCartService.getGuestCart()
      }

      lastUpdated.value = new Date()
      logger.log("Cart fetched successfully:", cartCount.value, "items")

      return cart.value
    } catch (err) {
      error.value =
        err.response?.data?.message || "Không thể tải giỏ hàng"
      logger.error("Error fetching cart:", err)

      if (err.response?.status === 401) {
        cart.value = null
      }

      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Thêm sản phẩm vào giỏ (CỘNG DỒN) - cho nút "Thêm vào giỏ"
   */
  const addItem = async (variantId, quantity = 1) => {
    loading.value = true
    error.value = null

    try {
      if (authStore.isAuthenticated) {
        await userService.addItemToCart({
          variantId,
          quantity
        })
      } else {
        await guestCartService.addToGuestCart(variantId, quantity)
      }

      await fetchCart()

      logger.log("Item added:", variantId)
      return cart.value
    } catch (err) {
      error.value =
        err.response?.data?.message || "Không thể thêm vào giỏ hàng"
      logger.error("Error adding item:", err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * ✅ CẬP NHẬT SỐ LƯỢNG (DÙNG CHO + / -)
   * KHÔNG còn dùng addItem nữa
   */
  const updateQuantity = async (variantId, quantity) => {
    if (quantity < 1) {
      return await removeItem(variantId)
    }

    loading.value = true
    error.value = null

    try {
      if (authStore.isAuthenticated) {
        // 👉 API mới của anh: PUT /api/cart/item
        const res = await axios.put("/api/cart/item", {
          variantId,
          quantity,
        })

        cart.value = res.data
      } else {
        await guestCartService.updateGuestCartQuantity(
          variantId,
          quantity
        )

        cart.value = await guestCartService.getGuestCart()
      }

      lastUpdated.value = new Date()
      logger.log(
        "Quantity updated:",
        variantId,
        "=>",
        quantity
      )

      return cart.value
    } catch (err) {
      error.value =
        err.response?.data?.message ||
        "Không thể cập nhật số lượng"

      logger.error("Error updating quantity:", err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Xóa sản phẩm khỏi giỏ hàng
   */
  const removeItem = async (variantId) => {
    loading.value = true
    error.value = null

    try {
      if (authStore.isAuthenticated) {
        await userService.removeItemFromCart(variantId)
      } else {
        await guestCartService.removeFromGuestCart(variantId)
      }

      await fetchCart()

      logger.log("Item removed:", variantId)
      return cart.value
    } catch (err) {
      error.value =
        err.response?.data?.message ||
        "Không thể xóa khỏi giỏ hàng"

      logger.error("Error removing item:", err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Xóa toàn bộ giỏ hàng
   */
  const clearCart = async () => {
    loading.value = true
    error.value = null

    try {
      if (authStore.isAuthenticated) {
        if (cart.value?.items) {
          for (const item of cart.value.items) {
            await userService.removeItemFromCart(item.variantId)
          }
        }
      } else {
        guestCartService.clearGuestCart()
      }

      cart.value = null
      lastUpdated.value = new Date()
      logger.log("Cart cleared")
    } catch (err) {
      error.value =
        err.response?.data?.message ||
        "Không thể xóa giỏ hàng"
      logger.error("Error clearing cart:", err)
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Refresh cart (force reload)
   */
  const refreshCart = async () => {
    return await fetchCart()
  }

  /**
   * Kiểm tra sản phẩm có trong giỏ hàng không
   */
  const isInCart = (variantId) => {
    if (!cart.value || !cart.value.items) return false
    return cart.value.items.some(
      (item) => item.variantId === variantId
    )
  }

  /**
   * Lấy số lượng của variant trong giỏ
   */
  const getItemQuantity = (variantId) => {
    if (!cart.value || !cart.value.items) return 0
    const item = cart.value.items.find(
      (item) => item.variantId === variantId
    )
    return item?.quantity || 0
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
    cart.value = null
    loading.value = false
    error.value = null
    lastUpdated.value = null
  }

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
    updateQuantity,
    removeItem,
    clearCart,
    refreshCart,
    isInCart,
    getItemQuantity,
    clearError,
    reset
  }
})

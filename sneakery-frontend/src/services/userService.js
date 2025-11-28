import axios from 'axios'

// Sử dụng relative path để Vite proxy có thể forward requests
const API_BASE_URL = '/api'

// Tạo axios instance cho user API
const userApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor để thêm JWT token
userApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor để xử lý lỗi
userApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token hết hạn hoặc không hợp lệ
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

class UserService {
  // ===== ORDERS =====
  async getMyOrders() {
    try {
      const response = await userApi.get('/orders')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getMyOrderById(orderId) {
    try {
      const response = await userApi.get(`/orders/${orderId}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async checkout(checkoutData) {
    try {
      const response = await userApi.post('/orders/checkout', checkoutData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async cancelOrder(orderId) {
    try {
      const response = await userApi.put(`/orders/${orderId}/cancel`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== RETURNS =====
  async createReturnRequest(orderId, returnData) {
    try {
      const response = await userApi.post(`/orders/${orderId}/return`, returnData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== CONFIRM RECEIVED & UPDATE PAYMENT =====
  async confirmOrderReceived(orderId) {
    try {
      const response = await userApi.put(`/orders/${orderId}/confirm-received`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== CART =====
  async getMyCart() {
    try {
      const response = await userApi.get('/cart')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async addItemToCart(itemData) {
    try {
      const response = await userApi.post('/cart/item', itemData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async removeItemFromCart(variantId) {
    try {
      const response = await userApi.delete(`/cart/item/${variantId}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateCartItemQuantity(variantId, quantity) {
    try {
      const response = await userApi.put(`/cart/item/${variantId}`, { quantity })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async clearCart() {
    try {
      const response = await userApi.delete('/cart')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== WISHLIST =====
  async getWishlist() {
    try {
      const response = await userApi.get('/wishlist')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async addToWishlist(productId) {
    try {
      const response = await userApi.post(`/wishlist/${productId}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async removeFromWishlist(productId) {
    try {
      const response = await userApi.delete(`/wishlist/${productId}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async checkInWishlist(productId) {
    try {
      const response = await userApi.get(`/wishlist/check/${productId}`)
      return response.data.inWishlist
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async countWishlistItems() {
    try {
      const response = await userApi.get('/wishlist/count')
      return response.data.count
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async clearWishlist() {
    try {
      const response = await userApi.delete('/wishlist/clear')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async toggleWishlist(productId) {
    try {
      const inWishlist = await this.checkInWishlist(productId)
      
      if (inWishlist) {
        await this.removeFromWishlist(productId)
        return { action: 'removed', inWishlist: false }
      } else {
        await this.addToWishlist(productId)
        return { action: 'added', inWishlist: true }
      }
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== REVIEWS =====
  async getProductReviews(productId) {
    try {
      const response = await userApi.get(`/products/${productId}/reviews`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createProductReview(productId, reviewData) {
    try {
      const response = await userApi.post(`/products/${productId}/reviews`, reviewData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== NOTIFICATIONS =====
  async getNotifications(page = 0, size = 20) {
    try {
      const response = await userApi.get('/notifications', {
        params: { page, size }
      })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getUnreadCount() {
    try {
      const response = await userApi.get('/notifications/unread-count')
      return response.data.count
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async markAsRead(notificationId) {
    try {
      await userApi.put(`/notifications/${notificationId}/read`)
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async markAllAsRead() {
    try {
      await userApi.put('/notifications/read-all')
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== LOYALTY =====
  async getLoyaltyBalance() {
    try {
      const response = await userApi.get('/loyalty/balance')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getLoyaltyHistory() {
    try {
      const response = await userApi.get('/loyalty/history')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== ADDRESSES =====
  async getMyAddresses() {
    try {
      const response = await userApi.get('/addresses')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getMyAddressById(addressId) {
    try {
      const response = await userApi.get(`/addresses/${addressId}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createAddress(addressData) {
    try {
      const response = await userApi.post('/addresses', addressData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateAddress(addressId, addressData) {
    try {
      const response = await userApi.put(`/addresses/${addressId}`, addressData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteAddress(addressId) {
    try {
      await userApi.delete(`/addresses/${addressId}`)
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== PROFILE =====
  async updateProfile(profileData) {
    try {
      const response = await userApi.put('/auth/profile', profileData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async changePassword(passwordData) {
    try {
      const response = await userApi.post('/auth/change-password', passwordData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getVariantImages(variantId) {
    return axios.get(`/api/variant-images/${variantId}`).then((res) => res.data);
  }

  // ===== UTILITY METHODS =====
  handleError(error) {
    if (error.response) {
      // Server responded with error status
      return {
        message: error.response.data?.message || 'Có lỗi xảy ra từ server',
        status: error.response.status,
        data: error.response.data
      }
    } else if (error.request) {
      // Request was made but no response received
      return {
        message: 'Không thể kết nối đến server',
        status: 0
      }
    } else {
      // Something else happened
      return {
        message: error.message || 'Có lỗi không xác định',
        status: -1
      }
    }
  }
}

export default new UserService()


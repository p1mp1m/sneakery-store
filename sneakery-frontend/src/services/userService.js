// src/services/userService.js
import api from "@/utils/axios";

class UserService {
  // ===== ORDERS =====
  async getMyOrders() {
    try {
      const response = await api.get('/orders');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getMyOrderById(orderId) {
    try {
      const response = await api.get(`/orders/${orderId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async checkout(checkoutData) {
    try {
      const response = await api.post('/orders/checkout', checkoutData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async cancelOrder(orderId) {
    try {
      const response = await api.put(`/orders/${orderId}/cancel`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
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

  // ===== CART =====
  async getMyCart() {
    try {
      const response = await api.get('/cart');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async addItemToCart(itemData) {
    try {
      const response = await api.post('/cart/item', itemData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async removeItemFromCart(variantId) {
    try {
      const response = await api.delete(`/cart/item/${variantId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async updateCartItemQuantity(variantId, quantity) {
    try {
      const response = await api.put(`/cart/item/${variantId}`, { quantity });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async clearCart() {
    try {
      const response = await api.delete('/cart');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }


  // ===== WISHLIST =====
  async getWishlist() {
    try {
      const response = await api.get('/wishlist');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async addToWishlist(productId) {
    try {
      const response = await api.post(`/wishlist/${productId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async removeFromWishlist(productId) {
    try {
      const response = await api.delete(`/wishlist/${productId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async checkInWishlist(productId) {
    try {
      const response = await api.get(`/wishlist/check/${productId}`);
      return response.data.inWishlist;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async countWishlistItems() {
    try {
      const response = await api.get('/wishlist/count');
      return response.data.count;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async clearWishlist() {
    try {
      const response = await api.delete('/wishlist/clear');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async toggleWishlist(productId) {
    try {
      const inWishlist = await this.checkInWishlist(productId);

      if (inWishlist) {
        await this.removeFromWishlist(productId);
        return { action: 'removed', inWishlist: false };
      } else {
        await this.addToWishlist(productId);
        return { action: 'added', inWishlist: true };
      }
    } catch (error) {
      throw this.handleError(error);
    }
  }


  // ===== REVIEWS =====
  async getProductReviews(productId) {
    try {
      const response = await api.get(`/products/${productId}/reviews`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async createProductReview(productId, reviewData) {
    try {
      const response = await api.post(`/products/${productId}/reviews`, reviewData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }


  // ===== NOTIFICATIONS =====
  async getNotifications(page = 0, size = 20) {
    try {
      const response = await api.get('/notifications', {
        params: { page, size }
      });
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getUnreadCount() {
    try {
      const response = await api.get('/notifications/unread-count');
      return response.data.count;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async markAsRead(notificationId) {
    try {
      await api.put(`/notifications/${notificationId}/read`);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async markAllAsRead() {
    try {
      await api.put('/notifications/read-all');
    } catch (error) {
      throw this.handleError(error);
    }
  }


  // ===== LOYALTY =====
  async getLoyaltyBalance() {
    try {
      const response = await api.get('/loyalty/balance');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getLoyaltyHistory() {
    try {
      const response = await api.get('/loyalty/history');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }


  // ===== ADDRESSES =====
  async getMyAddresses() {
    try {
      const response = await api.get('/addresses');
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getMyAddressById(addressId) {
    try {
      const response = await api.get(`/addresses/${addressId}`);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async createAddress(addressData) {
    try {
      const response = await api.post('/addresses', addressData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async updateAddress(addressId, addressData) {
    try {
      const response = await api.put(`/addresses/${addressId}`, addressData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async deleteAddress(addressId) {
    try {
      await api.delete(`/addresses/${addressId}`);
    } catch (error) {
      throw this.handleError(error);
    }
  }


  // ===== PROFILE =====
  async updateProfile(profileData) {
    try {
      const response = await api.put('/auth/profile', profileData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async changePassword(passwordData) {
    try {
      const response = await api.post('/auth/change-password', passwordData);
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }


  // ===== COMMON ERROR HANDLER =====
  handleError(error) {
    if (error.response) {
      return {
        message: error.response.data?.message || 'Có lỗi xảy ra từ server',
        status: error.response.status,
        data: error.response.data
      };
    } else if (error.request) {
      return {
        message: 'Không thể kết nối đến server',
        status: 0
      };
    } else {
      return {
        message: error.message || 'Có lỗi không xác định',
        status: -1
      };
    }
  }
}

export default new UserService();

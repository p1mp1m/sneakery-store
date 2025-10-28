/**
 * Wishlist Service
 * Quản lý API calls cho wishlist
 */

import axios from 'axios'

const API_URL = 'http://localhost:8080/api/wishlist'

// Lấy access token từ localStorage
const getAuthHeader = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

class WishlistService {
  /**
   * Lấy danh sách wishlist của user hiện tại
   */
  async getWishlist() {
    try {
      const response = await axios.get(API_URL, {
        headers: getAuthHeader()
      })
      return response.data
    } catch (error) {
      console.error('Error fetching wishlist:', error)
      throw error
    }
  }

  /**
   * Thêm sản phẩm vào wishlist
   */
  async addToWishlist(productId) {
    try {
      const response = await axios.post(
        `${API_URL}/${productId}`,
        {},
        {
          headers: getAuthHeader()
        }
      )
      return response.data
    } catch (error) {
      console.error('Error adding to wishlist:', error)
      throw error
    }
  }

  /**
   * Xóa sản phẩm khỏi wishlist
   */
  async removeFromWishlist(productId) {
    try {
      const response = await axios.delete(`${API_URL}/${productId}`, {
        headers: getAuthHeader()
      })
      return response.data
    } catch (error) {
      console.error('Error removing from wishlist:', error)
      throw error
    }
  }

  /**
   * Kiểm tra sản phẩm có trong wishlist không
   */
  async checkInWishlist(productId) {
    try {
      const response = await axios.get(`${API_URL}/check/${productId}`, {
        headers: getAuthHeader()
      })
      return response.data.inWishlist
    } catch (error) {
      console.error('Error checking wishlist:', error)
      return false
    }
  }

  /**
   * Đếm số sản phẩm trong wishlist
   */
  async countWishlistItems() {
    try {
      const response = await axios.get(`${API_URL}/count`, {
        headers: getAuthHeader()
      })
      return response.data.count
    } catch (error) {
      console.error('Error counting wishlist items:', error)
      return 0
    }
  }

  /**
   * Xóa toàn bộ wishlist
   */
  async clearWishlist() {
    try {
      const response = await axios.delete(`${API_URL}/clear`, {
        headers: getAuthHeader()
      })
      return response.data
    } catch (error) {
      console.error('Error clearing wishlist:', error)
      throw error
    }
  }

  /**
   * Toggle wishlist (thêm nếu chưa có, xóa nếu đã có)
   */
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
      console.error('Error toggling wishlist:', error)
      throw error
    }
  }
}

export default new WishlistService()


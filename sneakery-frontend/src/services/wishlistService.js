/**
 * Wishlist Service
 * Quản lý API calls cho wishlist
 */

import axios from 'axios'
import { API_ENDPOINTS } from '@/config/api'
import logger from '@/utils/logger'

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
      const response = await axios.get(API_ENDPOINTS.WISHLIST.BASE, {
        headers: getAuthHeader()
      })
      return response.data
    } catch (error) {
      logger.error('Error fetching wishlist:', error)
      throw error
    }
  }

  /**
   * Thêm sản phẩm vào wishlist
   */
  async addToWishlist(productId) {
    try {
      const response = await axios.post(
        `${API_ENDPOINTS.WISHLIST.BASE}/${productId}`,
        {},
        {
          headers: getAuthHeader()
        }
      )
      logger.log('Product added to wishlist:', productId)
      return response.data
    } catch (error) {
      logger.error('Error adding to wishlist:', error)
      throw error
    }
  }

  /**
   * Xóa sản phẩm khỏi wishlist
   */
  async removeFromWishlist(productId) {
    try {
      const response = await axios.delete(`${API_ENDPOINTS.WISHLIST.BASE}/${productId}`, {
        headers: getAuthHeader()
      })
      logger.log('Product removed from wishlist:', productId)
      return response.data
    } catch (error) {
      logger.error('Error removing from wishlist:', error)
      throw error
    }
  }

  /**
   * Kiểm tra sản phẩm có trong wishlist không
   */
  async checkInWishlist(productId) {
    try {
      const response = await axios.get(`${API_ENDPOINTS.WISHLIST.BASE}/check/${productId}`, {
        headers: getAuthHeader()
      })
      return response.data.inWishlist
    } catch (error) {
      logger.error('Error checking wishlist:', error)
      return false
    }
  }

  /**
   * Đếm số sản phẩm trong wishlist
   */
  async countWishlistItems() {
    try {
      const response = await axios.get(`${API_ENDPOINTS.WISHLIST.BASE}/count`, {
        headers: getAuthHeader()
      })
      return response.data.count
    } catch (error) {
      logger.error('Error counting wishlist items:', error)
      return 0
    }
  }

  /**
   * Xóa toàn bộ wishlist
   */
  async clearWishlist() {
    try {
      const response = await axios.delete(`${API_ENDPOINTS.WISHLIST.BASE}/clear`, {
        headers: getAuthHeader()
      })
      logger.log('Wishlist cleared')
      return response.data
    } catch (error) {
      logger.error('Error clearing wishlist:', error)
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
      logger.error('Error toggling wishlist:', error)
      throw error
    }
  }
}

export default new WishlistService()


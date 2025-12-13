/**
 * Review Service
 * Handles API calls related to reviews/testimonials
 */

import axios from 'axios';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';

class ReviewService {
  /**
   * Get all approved reviews with pagination (for testimonials)
   * @param {number} page - Page number (0-based, default: 0)
   * @param {number} size - Page size (default: 6)
   * @returns {Promise<Object>} Page object with reviews and pagination info
   */
  async getApprovedReviews(page = 0, size = 6) {
    try {
      const response = await axios.get(API_ENDPOINTS.REVIEWS.APPROVED, {
        params: { page, size }
      });
      logger.log(`Fetched approved reviews page ${page}:`, response.data.content?.length || 0);
      return response.data;
    } catch (error) {
      logger.error('Error fetching approved reviews:', error);
      throw error;
    }
  }

  /**
   * Get reviews for a specific product
   * @param {number} productId - Product ID
   * @returns {Promise<Array>} List of review DTOs
   */
  async getProductReviews(productId) {
    try {
      const response = await axios.get(API_ENDPOINTS.REVIEWS.BY_PRODUCT(productId));
      logger.log(`Fetched reviews for product ${productId}:`, response.data.length);
      return response.data;
    } catch (error) {
      logger.error(`Error fetching reviews for product ${productId}:`, error);
      throw error;
    }
  }
}

export default new ReviewService();


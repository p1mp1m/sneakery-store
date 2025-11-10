/**
 * Newsletter Service
 * Handles API calls related to newsletter subscriptions
 */

import axios from 'axios';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';

class NewsletterService {
  /**
   * Subscribe to newsletter
   * @param {string} email - Email address to subscribe
   * @returns {Promise<Object>} Subscription data
   */
  async subscribe(email) {
    try {
      const response = await axios.post(API_ENDPOINTS.NEWSLETTER.SUBSCRIBE, {
        email: email.trim().toLowerCase()
      });
      logger.log('Newsletter subscription successful:', response.data);
      return response.data;
    } catch (error) {
      logger.error('Error subscribing to newsletter:', error);
      throw error;
    }
  }

  /**
   * Unsubscribe from newsletter
   * @param {string} email - Email address to unsubscribe
   * @returns {Promise<Object>} Unsubscribe response
   */
  async unsubscribe(email) {
    try {
      const response = await axios.post(API_ENDPOINTS.NEWSLETTER.UNSUBSCRIBE, {
        email: email.trim().toLowerCase()
      });
      logger.log('Newsletter unsubscription successful:', response.data);
      return response.data;
    } catch (error) {
      logger.error('Error unsubscribing from newsletter:', error);
      throw error;
    }
  }
}

export default new NewsletterService();





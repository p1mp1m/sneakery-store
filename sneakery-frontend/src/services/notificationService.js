/**
 * Notification Service
 * API client cho notifications
 */

import axios from 'axios';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';

// Lấy access token từ localStorage
const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

class NotificationService {
    /**
     * Lấy notifications (pagination)
     */
    async getNotifications(page = 0, size = 20) {
        try {
            const response = await axios.get(
                API_ENDPOINTS.NOTIFICATIONS.BASE,
                {
                    params: { page, size },
                    headers: getAuthHeader()
                }
            );
            return response.data;
        } catch (error) {
            logger.error('Failed to fetch notifications:', error);
            throw error;
        }
    }

    /**
     * Đếm unread notifications
     */
    async getUnreadCount() {
        try {
            const response = await axios.get(
                `${API_ENDPOINTS.NOTIFICATIONS.BASE}/unread-count`,
                {
                    headers: getAuthHeader()
                }
            );
            return response.data.count;
        } catch (error) {
            logger.error('Failed to fetch unread count:', error);
            throw error;
        }
    }

    /**
     * Mark notification as read
     */
    async markAsRead(notificationId) {
        try {
            await axios.put(
                `${API_ENDPOINTS.NOTIFICATIONS.BASE}/${notificationId}/read`,
                null,
                {
                    headers: getAuthHeader()
                }
            );
            logger.log(`Notification ${notificationId} marked as read`);
        } catch (error) {
            logger.error(`Failed to mark notification ${notificationId} as read:`, error);
            throw error;
        }
    }

    /**
     * Mark all notifications as read
     */
    async markAllAsRead() {
        try {
            await axios.put(
                `${API_ENDPOINTS.NOTIFICATIONS.BASE}/read-all`,
                null,
                {
                    headers: getAuthHeader()
                }
            );
            logger.log('All notifications marked as read');
        } catch (error) {
            logger.error('Failed to mark all notifications as read:', error);
            throw error;
        }
    }
}

export default new NotificationService();


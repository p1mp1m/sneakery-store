/**
 * Notification Service
 * API client cho notifications
 */

import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

class NotificationService {
    /**
     * Lấy notifications (pagination)
     */
    async getNotifications(page = 0, size = 20) {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(
                `${API_BASE_URL}/notifications`,
                {
                    params: { page, size },
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data;
        } catch (error) {
            console.error('❌ Failed to fetch notifications:', error);
            throw error;
        }
    }

    /**
     * Đếm unread notifications
     */
    async getUnreadCount() {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(
                `${API_BASE_URL}/notifications/unread-count`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            return response.data.count;
        } catch (error) {
            console.error('❌ Failed to fetch unread count:', error);
            throw error;
        }
    }

    /**
     * Mark notification as read
     */
    async markAsRead(notificationId) {
        try {
            const token = localStorage.getItem('token');
            await axios.put(
                `${API_BASE_URL}/notifications/${notificationId}/read`,
                null,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
        } catch (error) {
            console.error(`❌ Failed to mark notification ${notificationId} as read:`, error);
            throw error;
        }
    }

    /**
     * Mark all notifications as read
     */
    async markAllAsRead() {
        try {
            const token = localStorage.getItem('token');
            await axios.put(
                `${API_BASE_URL}/notifications/read-all`,
                null,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
        } catch (error) {
            console.error('❌ Failed to mark all notifications as read:', error);
            throw error;
        }
    }
}

export default new NotificationService();


/**
 * Notification Store (Pinia)
 * State management cho real-time notifications
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import notificationService from '@/services/notificationService';

export const useNotificationStore = defineStore('notification', () => {
    // State
    const notifications = ref([]);
    const unreadCount = ref(0);
    const loading = ref(false);
    const error = ref(null);
    const currentPage = ref(0);
    const totalPages = ref(0);

    // Computed
    const hasUnread = computed(() => unreadCount.value > 0);
    
    const unreadNotifications = computed(() => {
        return notifications.value.filter(n => !n.isRead);
    });

    /**
     * Fetch notifications (pagination)
     */
    const fetchNotifications = async (page = 0, size = 20) => {
        loading.value = true;
        error.value = null;

        try {
            const data = await notificationService.getNotifications(page, size);
            
            if (page === 0) {
                notifications.value = data.content;
            } else {
                notifications.value.push(...data.content);
            }
            
            currentPage.value = data.number;
            totalPages.value = data.totalPages;
            
            console.log(`✅ Fetched ${data.content.length} notifications`);
        } catch (err) {
            error.value = err.response?.data?.message || 'Không thể tải thông báo';
            console.error('❌ Error fetching notifications:', err);
        } finally {
            loading.value = false;
        }
    };

    /**
     * Fetch unread count
     */
    const fetchUnreadCount = async () => {
        try {
            const count = await notificationService.getUnreadCount();
            unreadCount.value = count;
            console.log(`✅ Unread notifications: ${count}`);
        } catch (err) {
            console.error('❌ Error fetching unread count:', err);
        }
    };

    /**
     * Mark notification as read
     */
    const markAsRead = async (notificationId) => {
        try {
            await notificationService.markAsRead(notificationId);
            
            // Update local state
            const notification = notifications.value.find(n => n.id === notificationId);
            if (notification) {
                notification.isRead = true;
                notification.readAt = new Date().toISOString();
            }
            
            // Update unread count
            if (unreadCount.value > 0) {
                unreadCount.value--;
            }
            
            console.log(`✅ Marked notification ${notificationId} as read`);
        } catch (err) {
            console.error(`❌ Error marking notification ${notificationId} as read:`, err);
            throw err;
        }
    };

    /**
     * Mark all notifications as read
     */
    const markAllAsRead = async () => {
        try {
            await notificationService.markAllAsRead();
            
            // Update local state
            notifications.value.forEach(n => {
                n.isRead = true;
                n.readAt = new Date().toISOString();
            });
            
            unreadCount.value = 0;
            
            console.log('✅ Marked all notifications as read');
        } catch (err) {
            console.error('❌ Error marking all notifications as read:', err);
            throw err;
        }
    };

    /**
     * Load more notifications (pagination)
     */
    const loadMore = async () => {
        if (currentPage.value < totalPages.value - 1) {
            await fetchNotifications(currentPage.value + 1);
        }
    };

    /**
     * Refresh notifications và unread count
     */
    const refresh = async () => {
        await Promise.all([
            fetchNotifications(0),
            fetchUnreadCount()
        ]);
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
        notifications.value = [];
        unreadCount.value = 0;
        loading.value = false;
        error.value = null;
        currentPage.value = 0;
        totalPages.value = 0;
    };

    return {
        // State
        notifications,
        unreadCount,
        loading,
        error,
        currentPage,
        totalPages,

        // Computed
        hasUnread,
        unreadNotifications,

        // Actions
        fetchNotifications,
        fetchUnreadCount,
        markAsRead,
        markAllAsRead,
        loadMore,
        refresh,
        clearError,
        reset
    };
});


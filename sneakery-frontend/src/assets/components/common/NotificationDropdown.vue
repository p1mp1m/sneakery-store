<template>
  <div class="notification-dropdown" ref="dropdownRef">
    <!-- Notification Bell Button -->
    <button 
      class="notification-bell"
      @click="toggleDropdown"
      :class="{ 'has-unread': unreadCount > 0 }"
    >
      <span class="material-icons">notifications</span>
      <span v-if="unreadCount > 0" class="notification-badge">
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <!-- Dropdown Menu -->
    <transition name="dropdown">
      <div v-if="isOpen" class="dropdown-menu">
        <!-- Header -->
        <div class="dropdown-header">
          <h3>Thông báo</h3>
          <button 
            v-if="unreadCount > 0"
            @click="handleMarkAllRead" 
            class="mark-all-btn"
          >
            Đánh dấu đã đọc
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="dropdown-loading">
          <div class="loading-spinner"></div>
          <p>Đang tải thông báo...</p>
        </div>

        <!-- Notification List -->
        <div v-else-if="notifications.length > 0" class="notifications-list">
          <div 
            v-for="notification in notifications" 
            :key="notification.id"
            class="notification-item"
            :class="{ 'unread': !notification.isRead }"
            @click="handleNotificationClick(notification)"
          >
            <!-- Icon based on type -->
            <div class="notification-icon" :class="`type-${notification.type}`">
              <span class="material-icons">{{ getNotificationIcon(notification.type) }}</span>
            </div>

            <!-- Content -->
            <div class="notification-content">
              <h4>{{ notification.title }}</h4>
              <p>{{ notification.message }}</p>
              <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
            </div>

            <!-- Unread Indicator -->
            <div v-if="!notification.isRead" class="unread-indicator"></div>
          </div>

          <!-- Load More Button -->
          <button 
            v-if="hasMore" 
            @click="handleLoadMore"
            class="load-more-btn"
            :disabled="loadingMore"
          >
            {{ loadingMore ? 'Đang tải...' : 'Xem thêm' }}
          </button>
        </div>

        <!-- Empty State -->
        <div v-else class="dropdown-empty">
          <span class="material-icons">notifications_none</span>
          <p>Không có thông báo nào</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useNotificationStore } from '@/stores/notification'
import { storeToRefs } from 'pinia'

// Store
const notificationStore = useNotificationStore()
const { notifications, unreadCount, loading, hasMore } = storeToRefs(notificationStore)

// Local state
const isOpen = ref(false)
const loadingMore = ref(false)
const dropdownRef = ref(null)

// Toggle dropdown
const toggleDropdown = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value && notifications.value.length === 0) {
    notificationStore.fetchNotifications()
  }
}

// Handle click outside
const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    isOpen.value = false
  }
}

// Handle notification click
const handleNotificationClick = async (notification) => {
  if (!notification.isRead) {
    await notificationStore.markAsRead(notification.id)
  }
  
  // Navigate to link if exists
  if (notification.link) {
    window.location.href = notification.link
  }
  
  isOpen.value = false
}

// Mark all as read
const handleMarkAllRead = async () => {
  await notificationStore.markAllAsRead()
}

// Load more notifications
const handleLoadMore = async () => {
  loadingMore.value = true
  await notificationStore.loadMore()
  loadingMore.value = false
}

// Get icon based on notification type
const getNotificationIcon = (type) => {
  const icons = {
    order_status: 'shopping_bag',
    promotion: 'local_offer',
    product_restock: 'inventory',
    review_reply: 'chat',
    system: 'info'
  }
  return icons[type] || 'notifications'
}

// Format time (relative)
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const now = new Date()
  const time = new Date(timestamp)
  const diffMs = now - time
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)
  
  if (diffMins < 1) return 'Vừa xong'
  if (diffMins < 60) return `${diffMins} phút trước`
  if (diffHours < 24) return `${diffHours} giờ trước`
  if (diffDays < 7) return `${diffDays} ngày trước`
  
  return time.toLocaleDateString('vi-VN')
}

// Lifecycle
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  // Fetch unread count on mount
  notificationStore.fetchUnreadCount()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.notification-dropdown {
  position: relative;
}

.notification-bell {
  position: relative;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-lg);
  transition: var(--transition-fast);
  color: var(--text-primary);
}

.notification-bell:hover {
  background-color: var(--bg-secondary);
}

.notification-bell.has-unread .material-icons {
  color: var(--color-primary);
}

.notification-bell .material-icons {
  font-size: 24px;
}

.notification-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 10px;
  font-weight: var(--font-bold);
  padding: 2px 5px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

/* Dropdown Menu */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 380px;
  max-height: 500px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-light);
  overflow: hidden;
  z-index: var(--z-dropdown);
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-light);
}

.dropdown-header h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.mark-all-btn {
  background: transparent;
  border: none;
  color: var(--color-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  transition: var(--transition-fast);
}

.mark-all-btn:hover {
  background-color: var(--primary-light);
}

/* Notifications List */
.notifications-list {
  max-height: 420px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-4);
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: var(--transition-fast);
  position: relative;
}

.notification-item:hover {
  background-color: var(--bg-secondary);
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item.unread {
  background-color: rgba(102, 126, 234, 0.05);
}

.notification-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-icon.type-order_status {
  background-color: rgba(72, 187, 120, 0.1);
  color: var(--color-success);
}

.notification-icon.type-promotion {
  background-color: rgba(237, 137, 54, 0.1);
  color: var(--color-warning);
}

.notification-icon.type-product_restock {
  background-color: rgba(66, 153, 225, 0.1);
  color: var(--color-info);
}

.notification-icon.type-review_reply {
  background-color: rgba(102, 126, 234, 0.1);
  color: var(--color-primary);
}

.notification-icon.type-system {
  background-color: rgba(160, 174, 192, 0.1);
  color: var(--text-tertiary);
}

.notification-content {
  flex: 1;
}

.notification-content h4 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.notification-content p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-1) 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notification-time {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.unread-indicator {
  position: absolute;
  top: 50%;
  right: var(--space-4);
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  background: var(--color-primary);
  border-radius: var(--radius-full);
}

/* Load More Button */
.load-more-btn {
  width: 100%;
  padding: var(--space-3);
  background: transparent;
  border: none;
  color: var(--color-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: var(--transition-fast);
}

.load-more-btn:hover:not(:disabled) {
  background-color: var(--bg-secondary);
}

.load-more-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Loading State */
.dropdown-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-8);
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-light);
  border-top-color: var(--color-primary);
  border-radius: var(--radius-full);
  animation: spin 0.8s linear infinite;
  margin-bottom: var(--space-3);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.dropdown-loading p {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* Empty State */
.dropdown-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12) var(--space-6);
}

.dropdown-empty .material-icons {
  font-size: 48px;
  color: var(--text-tertiary);
  margin-bottom: var(--space-3);
}

.dropdown-empty p {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

/* Dropdown Transition */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* Scrollbar */
.notifications-list::-webkit-scrollbar {
  width: 6px;
}

.notifications-list::-webkit-scrollbar-track {
  background: var(--bg-secondary);
}

.notifications-list::-webkit-scrollbar-thumb {
  background: var(--border-dark);
  border-radius: 3px;
}

.notifications-list::-webkit-scrollbar-thumb:hover {
  background: var(--text-tertiary);
}

/* Responsive */
@media (max-width: 640px) {
  .dropdown-menu {
    width: calc(100vw - 32px);
    right: -50px;
  }
}
</style>


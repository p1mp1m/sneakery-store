<template>
  <div class="relative" ref="dropdownRef">
    <!-- Notification Bell Button -->
    <button 
      class="relative flex items-center justify-center w-10 h-10 rounded-lg text-white/90 bg-transparent transition-all duration-200 cursor-pointer border border-transparent hover:bg-white/10 hover:text-white hover:border-white/20"
      @click="toggleDropdown"
    >
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M18 8A6 6 0 0 0 6 8C6 11.3137 8.68629 14 12 14C15.3137 14 18 11.3137 18 8Z"/>
        <path d="M13.73 21C13.5542 21.3031 13.3019 21.5547 12.9982 21.7295C12.6946 21.9044 12.3504 21.9965 12 21.9965C11.6496 21.9965 11.3054 21.9044 11.0018 21.7295C10.6982 21.5547 10.4458 21.3031 10.27 21"/>
      </svg>
      <span v-if="unreadCount > 0" class="absolute top-0.5 right-0.5 min-w-[18px] h-[18px] px-1.5 bg-red-500 text-white text-[11px] font-semibold rounded-full flex items-center justify-center shadow-md">
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <!-- Dropdown Menu -->
    <transition
      enter-active-class="transition-all duration-200 ease-out"
      leave-active-class="transition-all duration-200 ease-in"
      enter-from-class="opacity-0 scale-95 -translate-y-2"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-from-class="opacity-100 scale-100 translate-y-0"
      leave-to-class="opacity-0 scale-95 -translate-y-2"
    >
      <div v-if="isOpen" class="absolute top-full right-0 mt-2 w-80 bg-white dark:bg-gray-800 rounded-xl shadow-2xl border border-gray-200 dark:border-gray-700 overflow-hidden z-[1000]">
        <!-- Header -->
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h3 class="font-semibold text-gray-900 dark:text-gray-100">Thông báo</h3>
          <button 
            v-if="unreadCount > 0"
            @click="handleMarkAllRead" 
            class="text-sm text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 font-medium transition-colors"
          >
            Đánh dấu đã đọc
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="flex flex-col items-center justify-center py-12">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-purple-600 border-t-transparent mb-3"></div>
          <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải thông báo...</p>
        </div>

        <!-- Notification List -->
        <div v-else-if="notifications.length > 0" class="max-h-96 overflow-y-auto">
          <div 
            v-for="notification in notifications" 
            :key="notification.id"
            class="flex items-start gap-3 p-4 hover:bg-gray-50 dark:hover:bg-gray-700 cursor-pointer transition-colors border-b border-gray-100 dark:border-gray-700 last:border-b-0"
            :class="{ 'bg-purple-50 dark:bg-purple-900/20': !notification.isRead }"
            @click="handleNotificationClick(notification)"
          >
            <!-- Icon -->
            <div class="flex-shrink-0 w-10 h-10 rounded-lg flex items-center justify-center"
              :class="{
                'bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400': notification.type === 'order_status',
                'bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400': notification.type === 'promotion',
                'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400': notification.type === 'product_restock',
                'bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400': notification.type === 'review_reply',
                'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400': notification.type === 'system'
              }"
            >
              <i class="material-icons text-lg">{{ getNotificationIcon(notification.type) }}</i>
            </div>

            <!-- Content -->
            <div class="flex-1 min-w-0">
              <h4 class="font-semibold text-sm text-gray-900 dark:text-gray-100 mb-1">{{ notification.title }}</h4>
              <p class="text-sm text-gray-600 dark:text-gray-400 mb-2 line-clamp-2">{{ notification.message }}</p>
              <span class="text-xs text-gray-500 dark:text-gray-500">{{ formatTime(notification.createdAt) }}</span>
            </div>

            <!-- Unread Indicator -->
            <div v-if="!notification.isRead" class="flex-shrink-0 w-2 h-2 bg-purple-600 rounded-full mt-2"></div>
          </div>

          <!-- Load More Button -->
          <button 
            v-if="hasMore" 
            @click="handleLoadMore"
            class="w-full px-4 py-3 text-sm font-medium text-purple-600 dark:text-purple-400 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors border-t border-gray-200 dark:border-gray-700"
            :disabled="loadingMore"
          >
            {{ loadingMore ? 'Đang tải...' : 'Xem thêm' }}
          </button>
        </div>

        <!-- Empty State -->
        <div v-else class="flex flex-col items-center justify-center py-12">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="mb-3 text-gray-400">
            <path d="M18 8A6 6 0 0 0 6 8C6 11.3137 8.68629 14 12 14C15.3137 14 18 11.3137 18 8Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M13.73 21C13.5542 21.3031 13.3019 21.5547 12.9982 21.7295C12.6946 21.9044 12.3504 21.9965 12 21.9965C11.6496 21.9965 11.3054 21.9044 11.0018 21.7295C10.6982 21.5547 10.4458 21.3031 10.27 21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <p class="text-gray-600 dark:text-gray-400">Không có thông báo nào</p>
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

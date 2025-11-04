<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Toast Notifications -->
    <transition-group 
      name="toast" 
      tag="div" 
      class="fixed top-20 right-4 z-[9999] flex flex-col gap-2 max-w-sm"
      enter-active-class="transition-all duration-300 ease-out"
      leave-active-class="transition-all duration-300 ease-in"
      enter-from-class="opacity-0 translate-x-20"
      enter-to-class="opacity-100 translate-x-0"
      leave-from-class="opacity-100 translate-x-0"
      leave-to-class="opacity-0 -translate-x-20"
    >
      <div 
        v-for="notification in notifications" 
        :key="notification.id"
        class="flex items-start gap-3 p-3 rounded-xl shadow-lg backdrop-blur-sm border-l-4 min-w-[280px]"
        :class="{
          'bg-white dark:bg-gray-800 border-green-500': notification.type === 'success',
          'bg-white dark:bg-gray-800 border-red-500': notification.type === 'error',
          'bg-white dark:bg-gray-800 border-yellow-500': notification.type === 'warning',
          'bg-white dark:bg-gray-800 border-blue-500': notification.type === 'info'
        }"
      >
        <i class="material-icons text-xl flex-shrink-0" 
          :class="{
            'text-green-500': notification.type === 'success',
            'text-red-500': notification.type === 'error',
            'text-yellow-500': notification.type === 'warning',
            'text-blue-500': notification.type === 'info'
          }"
        >{{ getNotificationIcon(notification.type) }}</i>
        <div class="flex-1 min-w-0">
          <p class="font-semibold text-sm mb-1 text-gray-900 dark:text-gray-100">{{ notification.title }}</p>
          <p class="text-xs text-gray-600 dark:text-gray-400">{{ notification.message }}</p>
        </div>
        <button class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 flex-shrink-0 transition-colors" @click="removeNotification(notification.id)">
          <i class="material-icons text-lg">close</i>
        </button>
      </div>
    </transition-group>

    <!-- Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center shadow-sm">
            <i class="material-icons text-white text-xl">dashboard</i>
          </div>
          <div>
            <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100">
              <span class="text-xs font-medium text-purple-600 dark:text-purple-400 uppercase tracking-wide">Chào mừng trở lại,</span>
              <span class="block capitalize">{{ adminUser?.email?.split('@')[0] || 'Admin' }}</span>
            </h1>
            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
              <i class="material-icons text-sm">shield</i>
              Quản lý và giám sát hệ thống Sneakery Store
            </p>
          </div>
        </div>

        <!-- Profile Card -->
        <div class="relative">
          <div class="flex items-center gap-2 p-2 bg-gray-50 dark:bg-gray-700/50 rounded-lg border border-gray-200 dark:border-gray-600">
            <div class="relative">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-sm">person</i>
              </div>
              <div class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-green-500 border-2 border-white dark:border-gray-800 rounded-full"></div>
            </div>
            <div class="flex-1 min-w-0">
              <div class="font-semibold text-sm text-gray-900 dark:text-gray-100 capitalize truncate">{{ adminUser?.email?.split('@')[0] || 'Admin' }}</div>
              <div class="text-xs text-gray-500 dark:text-gray-400">{{ currentTime }} • {{ currentDate }}</div>
            </div>
            <button class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" @click="toggleProfileMenu">
              <i class="material-icons text-gray-600 dark:text-gray-300 text-sm">{{ showProfileMenu ? 'expand_less' : 'expand_more' }}</i>
            </button>
          </div>

          <!-- Dropdown Menu -->
          <transition
            enter-active-class="transition-all duration-200 ease-out"
            leave-active-class="transition-all duration-200 ease-in"
            enter-from-class="opacity-0 scale-95 -translate-y-2"
            enter-to-class="opacity-100 scale-100 translate-y-0"
            leave-from-class="opacity-100 scale-100 translate-y-0"
            leave-to-class="opacity-0 scale-95 -translate-y-2"
          >
            <div v-if="showProfileMenu" class="absolute top-full right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden z-10">
              <a href="#" class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors" @click.prevent="handleProfileEdit">
                <i class="material-icons text-base">person_outline</i>
                <span>Hồ sơ</span>
              </a>
              <a href="#" class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors" @click.prevent="handleSettings">
                <i class="material-icons text-base">settings</i>
                <span>Cài đặt</span>
              </a>
              <a href="#" class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors" @click.prevent="handleChangePassword">
                <i class="material-icons text-base">lock</i>
                <span>Đổi mật khẩu</span>
              </a>
              <div class="border-t border-gray-200 dark:border-gray-700"></div>
              <a href="#" class="flex items-center gap-2 px-3 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors" @click.prevent="handleLogout">
                <i class="material-icons text-base">logout</i>
                <span>Đăng xuất</span>
              </a>
            </div>
          </transition>
        </div>
      </div>

      <!-- Refresh Controls -->
      <div class="flex items-center gap-2 mt-3 pt-3 border-t border-gray-200 dark:border-gray-700">
        <button @click="manualRefresh" class="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-md hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" title="Làm mới dữ liệu">
          <i class="material-icons text-sm">refresh</i>
          <span>Làm mới</span>
        </button>
        <button 
          @click="toggleAutoRefresh" 
          class="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-md transition-colors"
          :class="autoRefreshEnabled 
            ? 'text-white bg-purple-600 hover:bg-purple-700' 
            : 'text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600'"
          :title="autoRefreshEnabled ? 'Tắt tự động làm mới' : 'Bật tự động làm mới'"
        >
          <i class="material-icons text-sm" :class="{ 'animate-spin': autoRefreshEnabled }">{{ autoRefreshEnabled ? 'sync' : 'sync_disabled' }}</i>
          <span>{{ autoRefreshEnabled ? 'Tự động: Bật' : 'Tự động: Tắt' }}</span>
        </button>
        <span v-if="lastRefreshTime" class="text-xs text-gray-500 dark:text-gray-400 ml-auto">
          Cập nhật: {{ formatRelativeTime(lastRefreshTime) }}
        </span>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:scale-[1.02] transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-400 to-green-500 flex items-center justify-center">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <line x1="12" y1="1" x2="12" y2="23" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M17 5H9.5C8.11929 5 7 6.11929 7 7.5S8.11929 10 9.5 10H14.5C15.8807 10 17 11.1193 17 12.5S15.8807 15 14.5 15H7" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatCurrency(stats?.totalRevenue || 0) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng doanh thu</p>
          <div class="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
            <i class="material-icons text-sm">trending_up</i>
            <span>+12.5%</span>
          </div>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:scale-[1.02] transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center shadow-sm">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.totalOrders || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng đơn hàng</p>
          <div class="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
            <i class="material-icons text-sm">trending_up</i>
            <span>+8.2%</span>
          </div>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:scale-[1.02] transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-pink-500 to-pink-600 flex items-center justify-center shadow-sm">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.totalProducts || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Sản phẩm</p>
          <div class="flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400">
            <i class="material-icons text-sm">trending_flat</i>
            <span>Không đổi</span>
          </div>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:scale-[1.02] transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-400 to-blue-500 flex items-center justify-center shadow-sm">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="9" cy="7" r="4" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.totalUsers || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Người dùng</p>
          <div class="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
            <i class="material-icons text-sm">trending_up</i>
            <span>+15.3%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-lg">bolt</i>
          Quản lý nhanh
        </h2>
      </div>
      <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
        <router-link 
          v-for="action in quickActions" 
          :key="action.path"
          :to="action.path" 
          class="group relative p-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:border-purple-300 dark:hover:border-purple-600 hover:scale-[1.02] transition-all duration-200 text-center"
        >
          <div class="w-10 h-10 mx-auto mb-2 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center group-hover:scale-110 transition-transform">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path :d="action.icon" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="text-xs font-semibold text-gray-900 dark:text-gray-100 mb-1">{{ action.title }}</h3>
          <p class="text-[10px] text-gray-500 dark:text-gray-400 line-clamp-2">{{ action.desc }}</p>
          <span v-if="action.badge" class="absolute top-2 right-2 px-1.5 py-0.5 text-[10px] font-semibold text-white bg-purple-600 rounded">{{ action.badge }}</span>
        </router-link>
      </div>
    </div>

    <!-- Charts Section -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-lg">insights</i>
          Biểu đồ thống kê
        </h2>
        <div class="flex gap-1">
          <button 
            v-for="period in ['7d', '30d', '90d']" 
            :key="period"
            class="px-3 py-1 text-xs font-medium rounded-lg transition-all duration-200"
            :class="selectedPeriod === period
              ? 'bg-purple-600 text-white shadow-sm'
              : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'"
            @click="changePeriod(period)"
          >
            {{ period === '7d' ? '7 ngày' : period === '30d' ? '30 ngày' : '90 ngày' }}
          </button>
        </div>
      </div>
      
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-3">
        <!-- Revenue Chart -->
        <div class="lg:col-span-2 p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
          <div class="mb-2 pb-2 border-b border-gray-200 dark:border-gray-700">
            <h3 class="text-sm font-semibold text-gray-900 dark:text-gray-100">Doanh thu {{ selectedPeriod === '7d' ? '7 ngày' : selectedPeriod === '30d' ? '30 ngày' : '90 ngày' }} gần đây</h3>
            <span class="text-[10px] text-gray-500 dark:text-gray-400">Đơn vị: VNĐ</span>
          </div>
          <div class="h-48">
            <LineChart 
              :labels="revenueChart.labels"
              :datasets="revenueChart.datasets"
            />
          </div>
        </div>

        <!-- Order Status Chart -->
        <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
          <div class="mb-2 pb-2 border-b border-gray-200 dark:border-gray-700">
            <h3 class="text-sm font-semibold text-gray-900 dark:text-gray-100">Trạng thái đơn hàng</h3>
            <span class="text-[10px] text-gray-500 dark:text-gray-400">Phân bổ theo trạng thái</span>
          </div>
          <div class="h-48">
            <DoughnutChart 
              :labels="orderStatusChart.labels"
              :datasets="orderStatusChart.datasets"
            />
          </div>
        </div>

        <!-- Top Products Chart -->
        <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
          <div class="mb-2 pb-2 border-b border-gray-200 dark:border-gray-700">
            <h3 class="text-sm font-semibold text-gray-900 dark:text-gray-100">Top 5 sản phẩm bán chạy</h3>
            <span class="text-[10px] text-gray-500 dark:text-gray-400">Số lượng đã bán</span>
          </div>
          <div class="h-48">
            <BarChart 
              :labels="topProductsChart.labels"
              :datasets="topProductsChart.datasets"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-lg">history</i>
          Hoạt động gần đây
        </h2>
        <button class="flex items-center gap-1 px-3 py-1.5 text-xs font-medium text-purple-600 dark:text-purple-400 bg-purple-50 dark:bg-purple-900/20 rounded-lg hover:bg-purple-100 dark:hover:bg-purple-900/30 transition-all duration-200" @click="showNotification('info', 'Xem tất cả', 'Chức năng đang được phát triển')">
          <span>Xem tất cả</span>
          <i class="material-icons text-sm">arrow_forward</i>
        </button>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <el-skeleton v-if="loading" :rows="5" animated />
        <div v-else class="flex flex-col gap-2">
          <div 
            v-for="activity in recentActivities" 
            :key="activity.id" 
            class="flex items-start gap-2 p-2 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors group"
          >
            <div class="w-8 h-8 rounded-md flex items-center justify-center flex-shrink-0"
              :class="{
                'bg-purple-100 dark:bg-purple-900/30': activity.type === 'order',
                'bg-blue-100 dark:bg-blue-900/30': activity.type === 'user',
                'bg-pink-100 dark:bg-pink-900/30': activity.type === 'product'
              }"
            >
              <svg v-if="activity.type === 'order'" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else-if="activity.type === 'user'" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm text-gray-900 dark:text-gray-100 mb-1">{{ activity.text }}</p>
              <span class="text-xs text-gray-500 dark:text-gray-400">{{ formatRelativeTime(activity.timestamp) }}</span>
            </div>
            <button class="p-1 rounded hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors opacity-0 group-hover:opacity-100" @click="showNotification('info', 'Chi tiết', activity.text)">
              <i class="material-icons text-sm">visibility</i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAdminStore } from '@/stores/admin';
import { useAuthStore } from '@/stores/auth';
import LineChart from '@/assets/components/charts/LineChart.vue';
import BarChart from '@/assets/components/charts/BarChart.vue';
import DoughnutChart from '@/assets/components/charts/DoughnutChart.vue';

const router = useRouter();
const adminStore = useAdminStore();
const authStore = useAuthStore();

const adminUser = computed(() => adminStore.adminUser);

const loading = ref(false);
const selectedPeriod = ref('7d');
const currentTime = ref('');
const currentDate = ref('');
const notifications = ref([]);
const showProfileMenu = ref(false);
const autoRefreshEnabled = ref(true);
const lastRefreshTime = ref(null);
let notificationIdCounter = 0;
let autoRefreshInterval = null;

const stats = ref({
  totalRevenue: 0,
  totalOrders: 0,
  totalProducts: 0,
  totalUsers: 0
});

const quickActions = [
  { path: '/admin/products', title: 'Quản lý sản phẩm', desc: 'Thêm, sửa, xóa sản phẩm', icon: 'M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z', badge: null },
  { path: '/admin/orders', title: 'Quản lý đơn hàng', desc: 'Theo dõi và xử lý đơn hàng', icon: 'M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z', badge: null },
  { path: '/admin/users', title: 'Quản lý người dùng', desc: 'Xem và quản lý tài khoản', icon: 'M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21', badge: null },
  { path: '/admin/brands', title: 'Quản lý thương hiệu', desc: 'Thêm và chỉnh sửa thương hiệu', icon: 'M3 3H10V10H3V3ZM14 3H21V10H14V3ZM14 14H21V21H14V14ZM3 14H10V21H3V14Z', badge: '12' },
  { path: '/admin/categories', title: 'Quản lý danh mục', desc: 'Quản lý danh mục sản phẩm', icon: 'M4 19H20M4 15H20M4 11H20', badge: '8' },
  { path: '/admin/analytics', title: 'Phân tích', desc: 'Xem báo cáo và thống kê', icon: 'M18 20V10M12 20V4M6 20V14', badge: 'Mới' },
  { path: '/admin/inventory', title: 'Quản lý kho', desc: 'Theo dõi tồn kho', icon: 'M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z', badge: null },
  { path: '/admin/flash-sales', title: 'Flash Sale', desc: 'Quản lý khuyến mãi nhanh', icon: 'M13 2L3 14H12L11 22L21 10H12L13 2Z', badge: 'Hot' },
  { path: '/admin/discounts', title: 'Quản lý giảm giá', desc: 'Tạo và quản lý mã giảm giá', icon: 'M7 7H17M7 17H17M12 2V22M18 6L6 18M18 18L6 6', badge: 'VIP' },
  { path: '/admin/reviews', title: 'Quản lí đánh giá', desc: 'Xem và phê duyệt đánh giá', icon: 'M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z', badge: '25' },
  { path: '/admin/loyalty', title: 'Điểm thưởng', desc: 'Quản lý chương trình tích điểm', icon: 'M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z', badge: '1K+' },
  { path: '/admin/sales', title: 'Bán hàng (POS)', desc: 'Hệ thống bán hàng tại quầy', icon: 'M9 11L12 14L22 4M21 12V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H16', badge: 'Live' },
  { path: '/admin/notifications', title: 'Quản lý thông báo', desc: 'Gửi và quản lý thông báo', icon: 'M18 8A6 6 0 0 0 6 8C6 11.0909 3.90914 13 2 13H22C20.0909 13 18 11.0909 18 8ZM13.73 21C13.5542 21.3031 13.3018 21.5547 12.9982 21.7295C12.6946 21.9044 12.3504 21.9965 12 21.9965C11.6496 21.9965 11.3054 21.9044 11.0018 21.7295C10.6982 21.5547 10.4458 21.3031 10.27 21', badge: '12' },
  { path: '/admin/returns', title: 'Quản lý trả hàng', desc: 'Xử lý yêu cầu trả hàng', icon: 'M3 7V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V7M3 7L12 14L21 7M3 17L12 10L21 17M21 17V5', badge: '5' },
  { path: '/admin/payments', title: 'Thanh toán', desc: 'Theo dõi giao dịch thanh toán', icon: 'M1 4H23M1 10H23M1 4V20C1 20.5304 1.21071 21.0391 1.58579 21.4142C1.96086 21.7893 2.46957 22 3 22H21C21.5304 22 22.0391 21.7893 22.4142 21.4142C22.7893 21.0391 23 20.5304 23 20V4', badge: '88' }
];

const revenueChart = ref({
  labels: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
  datasets: [
    {
      label: 'Doanh thu',
      data: [12000000, 19000000, 15000000, 25000000, 22000000, 30000000, 28000000],
      borderColor: 'rgb(59, 130, 246)',
      backgroundColor: 'rgba(59, 130, 246, 0.1)',
      fill: true,
      tension: 0.4
    }
  ]
});

const orderStatusChart = ref({
  labels: ['Chờ xác nhận', 'Đang xử lý', 'Đang giao', 'Hoàn thành', 'Đã hủy'],
  datasets: [
    {
      data: [15, 25, 18, 120, 8],
      backgroundColor: [
        'rgba(251, 191, 36, 0.8)',
        'rgba(59, 130, 246, 0.8)',
        'rgba(168, 85, 247, 0.8)',
        'rgba(34, 197, 94, 0.8)',
        'rgba(239, 68, 68, 0.8)'
      ],
      borderColor: [
        'rgb(251, 191, 36)',
        'rgb(59, 130, 246)',
        'rgb(168, 85, 247)',
        'rgb(34, 197, 94)',
        'rgb(239, 68, 68)'
      ],
      borderWidth: 2
    }
  ]
});

const topProductsChart = ref({
  labels: ['Nike Air Max', 'Adidas Ultra Boost', 'New Balance 574', 'Converse Chuck', 'Vans Old Skool'],
  datasets: [
    {
      label: 'Số lượng bán',
      data: [85, 72, 63, 58, 49],
      backgroundColor: 'rgba(59, 130, 246, 0.8)',
      borderColor: 'rgb(59, 130, 246)',
      borderWidth: 1
    }
  ]
});

const recentActivities = ref([
  { id: 1, type: 'order', text: 'Đơn hàng mới #ORD-20241021-001 được tạo', timestamp: new Date(Date.now() - 5 * 60 * 1000) },
  { id: 2, type: 'user', text: 'Người dùng mới đăng ký: nguyenvana@gmail.com', timestamp: new Date(Date.now() - 15 * 60 * 1000) },
  { id: 3, type: 'product', text: 'Sản phẩm "Nike Air Max 270" đã hết hàng', timestamp: new Date(Date.now() - 30 * 60 * 1000) },
  { id: 4, type: 'order', text: 'Đơn hàng #ORD-20241020-045 đã được giao', timestamp: new Date(Date.now() - 60 * 60 * 1000) },
  { id: 5, type: 'user', text: 'Người dùng tranthib@gmail.com đã cập nhật hồ sơ', timestamp: new Date(Date.now() - 120 * 60 * 1000) }
]);

const showNotification = (type, title, message) => {
  const id = ++notificationIdCounter;
  notifications.value.push({ id, type, title, message });
  setTimeout(() => removeNotification(id), 5000);
};

const removeNotification = (id) => {
  const index = notifications.value.findIndex(n => n.id === id);
  if (index > -1) notifications.value.splice(index, 1);
};

const getNotificationIcon = (type) => {
  const icons = { success: 'check_circle', error: 'error', warning: 'warning', info: 'info' };
  return icons[type] || 'info';
};

const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatRelativeTime = (timestamp) => {
  const now = new Date();
  const diff = now - new Date(timestamp);
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);
  if (minutes < 1) return 'Vừa xong';
  if (minutes < 60) return `${minutes} phút trước`;
  if (hours < 24) return `${hours} giờ trước`;
  return `${days} ngày trước`;
};

const updateDateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  currentDate.value = now.toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
};

const loadDashboardData = async (silent = false) => {
  if (!silent) loading.value = true;
  try {
    const response = await adminStore.fetchDashboardStats();
    stats.value = response || { totalRevenue: 0, totalOrders: 0, totalProducts: 0, totalUsers: 0 };
    lastRefreshTime.value = new Date();
    if (!silent) showNotification('success', 'Thành công', 'Đã tải dữ liệu dashboard');
  } catch (error) {
    console.error('Error loading dashboard data:', error);
    if (!silent) showNotification('error', 'Lỗi', 'Không thể tải dữ liệu dashboard');
  } finally {
    if (!silent) loading.value = false;
  }
};

const startAutoRefresh = () => {
  if (autoRefreshInterval) clearInterval(autoRefreshInterval);
  autoRefreshInterval = setInterval(() => {
    if (autoRefreshEnabled.value) loadDashboardData(true);
  }, 30000);
};

const stopAutoRefresh = () => {
  if (autoRefreshInterval) {
    clearInterval(autoRefreshInterval);
    autoRefreshInterval = null;
  }
};

const toggleAutoRefresh = () => {
  autoRefreshEnabled.value = !autoRefreshEnabled.value;
  if (autoRefreshEnabled.value) {
    startAutoRefresh();
    showNotification('success', 'Tự động làm mới', 'Đã bật tự động làm mới mỗi 30 giây');
  } else {
    stopAutoRefresh();
    showNotification('info', 'Tự động làm mới', 'Đã tắt tự động làm mới');
  }
};

const manualRefresh = () => {
  loadDashboardData();
  showNotification('info', 'Làm mới', 'Đang tải lại dữ liệu...');
};

const changePeriod = (period) => {
  selectedPeriod.value = period;
  showNotification('info', 'Thay đổi chu kỳ', `Đang hiển thị dữ liệu ${period === '7d' ? '7 ngày' : period === '30d' ? '30 ngày' : '90 ngày'}`);
};

const toggleProfileMenu = () => {
  showProfileMenu.value = !showProfileMenu.value;
};

const handleProfileEdit = () => {
  showProfileMenu.value = false;
  showNotification('info', 'Hồ sơ', 'Chức năng đang được phát triển');
};

const handleSettings = () => {
  showProfileMenu.value = false;
  router.push('/admin/settings');
};

const handleChangePassword = () => {
  showProfileMenu.value = false;
  showNotification('info', 'Đổi mật khẩu', 'Chức năng đang được phát triển');
};

const handleLogout = () => {
  showProfileMenu.value = false;
  showNotification('success', 'Đăng xuất', 'Đang đăng xuất...');
  setTimeout(() => {
    authStore.logout();
    adminStore.reset();
    localStorage.clear();
    window.location.href = '/login';
  }, 1000);
};

let timeInterval;
let handleClickOutside;

onMounted(() => {
  loadDashboardData();
  updateDateTime();
  timeInterval = setInterval(updateDateTime, 1000);
  startAutoRefresh();
  setTimeout(() => showNotification('info', 'Chào mừng!', 'Chào mừng bạn quay trở lại Admin Dashboard'), 500);
  handleClickOutside = (e) => {
    if (!e.target.closest('.relative')) showProfileMenu.value = false;
  };
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval);
  if (handleClickOutside) document.removeEventListener('click', handleClickOutside);
  stopAutoRefresh();
});
</script>


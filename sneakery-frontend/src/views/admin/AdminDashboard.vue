<template>
  <div class="admin-page admin-dashboard">
    <!-- Notification Toast Container -->
    <transition-group name="toast" tag="div" class="toast-container">
      <div 
        v-for="notification in notifications" 
        :key="notification.id"
        class="toast-notification"
        :class="notification.type"
      >
        <i class="material-icons">{{ getNotificationIcon(notification.type) }}</i>
        <div class="toast-content">
          <p class="toast-title">{{ notification.title }}</p>
          <p class="toast-message">{{ notification.message }}</p>
        </div>
        <button class="toast-close" @click="removeNotification(notification.id)">
          <i class="material-icons">close</i>
        </button>
      </div>
    </transition-group>

    <!-- Dashboard Header -->
    <div class="dashboard-header">
      <!-- Decorative Background Elements -->
      <div class="header-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
        <div class="decoration-wave"></div> 
      </div>
      
      <div class="header-content">
      <div class="welcome-section">
          <div class="header-icon animate-fade-in">
            <i class="material-icons">dashboard</i>
          </div>
          <div class="welcome-text">
            <h1 class="welcome-title animate-slide-in">
              <span class="gradient-text">Chào mừng trở lại,</span>
              <span class="admin-name">{{ adminUser?.email?.split('@')[0] || 'Admin' }}</span>
        </h1>
            <p class="welcome-subtitle animate-slide-in delay-1">
              <i class="material-icons">shield</i>
          Quản lý và giám sát hệ thống Sneakery Store
        </p>
        <!-- Auto-refresh controls -->
        <div class="refresh-controls">
          <button @click="manualRefresh" class="btn-refresh" title="Làm mới dữ liệu">
            <i class="material-icons">refresh</i>
            <span>Làm mới</span>
          </button>
          <button 
            @click="toggleAutoRefresh" 
            class="btn-auto-refresh"
            :class="{ 'active': autoRefreshEnabled }"
            :title="autoRefreshEnabled ? 'Tắt tự động làm mới' : 'Bật tự động làm mới'"
          >
            <i class="material-icons">{{ autoRefreshEnabled ? 'sync' : 'sync_disabled' }}</i>
            <span>{{ autoRefreshEnabled ? 'Tự động: Bật' : 'Tự động: Tắt' }}</span>
          </button>
          <span v-if="lastRefreshTime" class="last-refresh">
            Cập nhật: {{ formatRelativeTime(lastRefreshTime) }}
          </span>
        </div>
      </div>
        </div>

        <div class="header-info">
          <!-- Profile Card with Menu -->
          <div class="profile-card animate-slide-in delay-2">
            <div class="profile-main">
              <div class="profile-avatar">
                <div class="avatar-wrapper">
                  <i class="material-icons">person</i>
                  <div class="avatar-status"></div>
                </div>
              </div>
              <div class="profile-details">
                <div class="profile-name">{{ adminUser?.email?.split('@')[0] || 'Admin' }}</div>
                <div class="profile-role">
                  <i class="material-icons">admin_panel_settings</i>
                  <span>{{ adminUser?.role || 'ADMIN' }}</span>
                </div>
                <div class="profile-time">
                  <i class="material-icons">schedule</i>
                  <span>{{ currentTime }}</span>
                  <span class="separator">•</span>
                  <span>{{ currentDate }}</span>
                </div>
              </div>
              <button class="profile-menu-toggle" @click="toggleProfileMenu">
                <i class="material-icons">{{ showProfileMenu ? 'expand_less' : 'expand_more' }}</i>
              </button>
            </div>
            
            <!-- Dropdown Menu -->
            <transition name="dropdown">
              <div v-if="showProfileMenu" class="profile-menu-dropdown">
                <a href="#" class="menu-item" @click.prevent="handleProfileEdit">
                  <i class="material-icons">person_outline</i>
                  <span>Hồ sơ</span>
                  <i class="material-icons arrow">chevron_right</i>
                </a>
                <a href="#" class="menu-item" @click.prevent="handleSettings">
                  <i class="material-icons">settings</i>
                  <span>Cài đặt</span>
                  <i class="material-icons arrow">chevron_right</i>
                </a>
                <a href="#" class="menu-item" @click.prevent="handleChangePassword">
                  <i class="material-icons">lock</i>
                  <span>Đổi mật khẩu</span>
                  <i class="material-icons arrow">chevron_right</i>
                </a>
                <a href="#" class="menu-item logout-item" @click.prevent="handleLogout">
                  <i class="material-icons">logout</i>
                  <span>Đăng xuất</span>
                  <i class="material-icons arrow">chevron_right</i>
                </a>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats Cards - Enhanced -->
    <div class="stats-grid">
      <div class="stat-card stat-card-enhanced animate-fade-up hover-lift" style="animation-delay: 0.1s">
        <div class="stat-icon stat-icon-enhanced revenue-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <line x1="12" y1="1" x2="12" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M17 5H9.5C8.11929 5 7 6.11929 7 7.5S8.11929 10 9.5 10H14.5C15.8807 10 17 11.1193 17 12.5S15.8807 15 14.5 15H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ formatCurrency(stats?.totalRevenue || 0) }}</h3>
          <p class="stat-label">Tổng doanh thu</p>
          <div class="stat-trend positive">
            <i class="material-icons">trending_up</i>
            <span>+12.5% so với tuần trước</span>
          </div>
        </div>
      </div>

      <div class="stat-card stat-card-enhanced animate-fade-up hover-lift" style="animation-delay: 0.2s">
        <div class="stat-icon stat-icon-enhanced orders-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats?.totalOrders || 0 }}</h3>
          <p class="stat-label">Tổng đơn hàng</p>
          <div class="stat-trend positive">
            <i class="material-icons">trending_up</i>
            <span>+8.2% so với tuần trước</span>
          </div>
        </div>
      </div>

      <div class="stat-card stat-card-enhanced animate-fade-up hover-lift" style="animation-delay: 0.3s">
        <div class="stat-icon stat-icon-enhanced products-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 6V4C16 3.46957 15.7893 2.96086 15.4142 2.58579C15.0391 2.21071 14.5304 2 14 2H10C9.46957 2 8.96086 2.21071 8.58579 2.58579C8.21071 2.96086 8 3.46957 8 4V6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats?.totalProducts || 0 }}</h3>
          <p class="stat-label">Sản phẩm</p>
          <div class="stat-trend neutral">
            <i class="material-icons">trending_flat</i>
            <span>Không thay đổi</span>
          </div>
        </div>
      </div>

      <div class="stat-card stat-card-enhanced animate-fade-up hover-lift" style="animation-delay: 0.4s">
        <div class="stat-icon stat-icon-enhanced users-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M23 21V19C22.9993 18.1137 22.7044 17.2528 22.1614 16.5523C21.6184 15.8519 20.8581 15.3516 20 15.13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 3.13C16.8604 3.35031 17.623 3.85071 18.1676 4.55232C18.7122 5.25392 19.0078 6.11683 19.0078 7.005C19.0078 7.89318 18.7122 8.75608 18.1676 9.45769C17.623 10.1593 16.8604 10.6597 16 10.88" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats?.totalUsers || 0 }}</h3>
          <p class="stat-label">Người dùng</p>
          <div class="stat-trend positive">
            <i class="material-icons">trending_up</i>
            <span>+15.3% so với tuần trước</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions animate-fade-in">
      <div class="section-header">
        <h2 class="section-title">
          <i class="material-icons">bolt</i>
          Quản lý nhanh
        </h2>
      </div>
      <div class="actions-grid">
        <router-link to="/admin/products" class="action-card card-enhanced hover-lift hover-glow">
          <div class="action-icon animate-float">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý sản phẩm</h3>
          <p>Thêm, sửa, xóa sản phẩm và biến thể</p>
          <div class="action-badge">{{ stats?.totalProducts || 0 }}</div>
        </router-link>

        <router-link to="/admin/orders" class="action-card card-enhanced hover-lift hover-glow">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý đơn hàng</h3>
          <p>Theo dõi và xử lý đơn hàng</p>
          <div class="action-badge">{{ stats?.totalOrders || 0 }}</div>
        </router-link>

        <router-link to="/admin/users" class="action-card card-enhanced hover-lift hover-glow">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý người dùng</h3>
          <p>Xem và quản lý tài khoản người dùng</p>
          <div class="action-badge">{{ stats?.totalUsers || 0 }}</div>
        </router-link>

        <router-link to="/admin/brands" class="action-card card-enhanced hover-lift hover-glow">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <rect x="14" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <rect x="14" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <rect x="3" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        </div>
          <h3>Quản lý thương hiệu</h3>
          <p>Thêm và chỉnh sửa thương hiệu</p>
          <div class="action-badge">12</div>
        </router-link>

        <router-link to="/admin/categories" class="action-card card-enhanced hover-lift hover-glow">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 19H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M4 15H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M4 11H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý danh mục</h3>
          <p>Quản lý danh mục sản phẩm</p>
          <div class="action-badge">8</div>
        </router-link>

        <router-link to="/admin/analytics" class="action-card card-enhanced hover-lift hover-glow">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 20V10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 20V4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M6 20V14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Phân tích</h3>
          <p>Xem báo cáo và thống kê</p>
          <div class="action-badge new">Mới</div>
        </router-link>
              </div>
            </div>

    <!-- Charts Section -->
    <div class="charts-section animate-fade-in">
      <div class="section-header">
        <h2 class="section-title">
          <i class="material-icons">insights</i>
          Biểu đồ thống kê
        </h2>
        <div class="chart-controls">
          <button 
            v-for="period in ['7d', '30d', '90d']" 
            :key="period"
            class="period-btn"
            :class="{ active: selectedPeriod === period }"
            @click="changePeriod(period)"
          >
            {{ period === '7d' ? '7 ngày' : period === '30d' ? '30 ngày' : '90 ngày' }}
          </button>
        </div>
      </div>
      
      <div class="charts-grid">
        <!-- Revenue Chart -->
        <div class="chart-card large shine-effect">
          <div class="chart-header">
            <h3>Doanh thu {{ selectedPeriod === '7d' ? '7 ngày' : selectedPeriod === '30d' ? '30 ngày' : '90 ngày' }} gần đây</h3>
            <span class="chart-subtitle">Đơn vị: VNĐ</span>
          </div>
          <div class="chart-container">
            <LineChart 
              :labels="revenueChart.labels"
              :datasets="revenueChart.datasets"
            />
        </div>
      </div>

        <!-- Order Status Chart -->
      <div class="chart-card shine-effect">
        <div class="chart-header">
            <h3>Trạng thái đơn hàng</h3>
            <span class="chart-subtitle">Phân bổ theo trạng thái</span>
          </div>
          <div class="chart-container">
            <DoughnutChart 
              :labels="orderStatusChart.labels"
              :datasets="orderStatusChart.datasets"
            />
          </div>
        </div>

        <!-- Top Products Chart -->
        <div class="chart-card shine-effect">
          <div class="chart-header">
            <h3>Top 5 sản phẩm bán chạy</h3>
            <span class="chart-subtitle">Số lượng đã bán</span>
          </div>
          <div class="chart-container">
            <BarChart 
              :labels="topProductsChart.labels"
              :datasets="topProductsChart.datasets"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="recent-activity animate-fade-in">
      <div class="section-header">
        <h2 class="section-title">
          <i class="material-icons">history</i>
          Hoạt động gần đây
        </h2>
        <button class="view-all-btn" @click="showNotification('info', 'Xem tất cả', 'Chức năng đang được phát triển')">
          <span>Xem tất cả</span>
          <i class="material-icons">arrow_forward</i>
        </button>
      </div>
      <div class="activity-card shine-effect">
        <el-skeleton v-if="loading" :rows="5" animated />
        <div v-else class="activity-list">
          <div v-for="activity in recentActivities" :key="activity.id" class="activity-item hover-highlight">
            <div class="activity-icon" :class="activity.type">
              <svg v-if="activity.type === 'order'" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else-if="activity.type === 'user'" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="activity-content">
              <p class="activity-text">{{ activity.text }}</p>
              <span class="activity-time">{{ formatRelativeTime(activity.timestamp) }}</span>
            </div>
            <button class="activity-action" @click="showNotification('info', 'Chi tiết', activity.text)">
              <i class="material-icons">visibility</i>
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

// Computed
const adminUser = computed(() => adminStore.adminUser);

// State
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

// Chart Data
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
        'rgba(251, 191, 36, 0.8)',  // Pending - yellow
        'rgba(59, 130, 246, 0.8)',  // Processing - blue
        'rgba(168, 85, 247, 0.8)',  // Shipping - purple
        'rgba(34, 197, 94, 0.8)',   // Completed - green
        'rgba(239, 68, 68, 0.8)'    // Cancelled - red
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
      {
        id: 1,
    type: 'order',
    text: 'Đơn hàng mới #ORD-20241021-001 được tạo',
    timestamp: new Date(Date.now() - 5 * 60 * 1000)
      },
      {
        id: 2,
    type: 'user',
    text: 'Người dùng mới đăng ký: nguyenvana@gmail.com',
    timestamp: new Date(Date.now() - 15 * 60 * 1000)
      },
      {
        id: 3,
    type: 'product',
    text: 'Sản phẩm "Nike Air Max 270" đã hết hàng',
    timestamp: new Date(Date.now() - 30 * 60 * 1000)
  },
  {
    id: 4,
    type: 'order',
    text: 'Đơn hàng #ORD-20241020-045 đã được giao',
    timestamp: new Date(Date.now() - 60 * 60 * 1000)
  },
  {
    id: 5,
    type: 'user',
    text: 'Người dùng tranthib@gmail.com đã cập nhật hồ sơ',
    timestamp: new Date(Date.now() - 120 * 60 * 1000)
  }
]);

// Notification Methods
const showNotification = (type, title, message) => {
  const id = ++notificationIdCounter;
  notifications.value.push({
    id,
    type,
    title,
    message
  });
  
  // Auto remove after 5 seconds
  setTimeout(() => {
    removeNotification(id);
  }, 5000);
};

const removeNotification = (id) => {
  const index = notifications.value.findIndex(n => n.id === id);
  if (index > -1) {
    notifications.value.splice(index, 1);
  }
};

const getNotificationIcon = (type) => {
  const icons = {
    success: 'check_circle',
    error: 'error',
    warning: 'warning',
    info: 'info'
  };
  return icons[type] || 'info';
};

// Methods
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
  currentDate.value = now.toLocaleDateString('vi-VN', { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  });
};

const loadDashboardData = async (silent = false) => {
  if (!silent) loading.value = true;
  try {
    // Fetch dashboard stats from API
    const response = await adminStore.fetchDashboardStats();
    stats.value = response || {
      totalRevenue: 0,
      totalOrders: 0,
      totalProducts: 0,
      totalUsers: 0
    };
    
    lastRefreshTime.value = new Date();
    
    if (!silent) {
      showNotification('success', 'Thành công', 'Đã tải dữ liệu dashboard');
    }
  } catch (error) {
    console.error('Error loading dashboard data:', error);
    if (!silent) {
      showNotification('error', 'Lỗi', 'Không thể tải dữ liệu dashboard');
    }
  } finally {
    if (!silent) loading.value = false;
  }
};

// Auto-refresh every 30 seconds
const startAutoRefresh = () => {
  if (autoRefreshInterval) {
    clearInterval(autoRefreshInterval);
  }
  
  autoRefreshInterval = setInterval(() => {
    if (autoRefreshEnabled.value) {
      loadDashboardData(true); // Silent refresh
    }
  }, 30000); // 30 seconds
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
    // Logout từ auth store (clear user state)
    authStore.logout();
    
    // Reset admin store
    adminStore.reset();
    
    // Clear all localStorage
    localStorage.clear();
    
    // Force reload và redirect về login
    window.location.href = '/login';
  }, 1000);
};

// Lifecycle
let timeInterval;
let handleClickOutside;

onMounted(() => {
  loadDashboardData();
  updateDateTime();
  timeInterval = setInterval(updateDateTime, 1000);
  
  // Start auto-refresh
  startAutoRefresh();
  
  // Show welcome notification
  setTimeout(() => {
    showNotification('info', 'Chào mừng!', 'Chào mừng bạn quay trở lại Admin Dashboard');
  }, 500);

  // Close profile menu when clicking outside
  handleClickOutside = (e) => {
    if (!e.target.closest('.profile-card')) {
      showProfileMenu.value = false;
    }
  };
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval);
  }
  if (handleClickOutside) {
    document.removeEventListener('click', handleClickOutside);
  }
  // Stop auto-refresh on unmount
  stopAutoRefresh();
});
</script>

<style scoped>
/* ===== TOAST NOTIFICATIONS ===== */
.toast-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 400px;
}

.toast-notification {
  background: var(--dark-bg-card);
  border-radius: var(--radius-xl);
  padding: 16px;
  box-shadow: var(--shadow-glass-lg);
  display: flex;
  align-items: flex-start;
  gap: 12px;
  border-left: 4px solid;
  min-width: 320px;
  animation: slideInRight 0.3s ease;
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
}

.toast-notification.success {
  border-color: var(--success-color);
}

.toast-notification.error {
  border-color: var(--error-color);
}

.toast-notification.warning {
  border-color: var(--warning-color);
}

.toast-notification.info {
  border-color: var(--info-color);
}

.toast-notification > i {
  flex-shrink: 0;
  font-size: 24px;
}

.toast-notification.success > i {
  color: var(--success-color);
}

.toast-notification.error > i {
  color: var(--error-color);
}

.toast-notification.warning > i {
  color: var(--warning-color);
}

.toast-notification.info > i {
  color: var(--info-color);
}

.toast-content {
  flex: 1;
  min-width: 0;
}

.toast-title {
  font-weight: 600;
  font-size: 14px;
  margin: 0 0 4px 0;
  color: var(--dark-text-primary);
}

.toast-message {
  font-size: 13px;
  margin: 0;
  color: var(--dark-text-secondary);
}

.toast-close {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--dark-text-tertiary);
  padding: 0;
  flex-shrink: 0;
  transition: color var(--transition-fast);
}

.toast-close:hover {
  color: var(--dark-text-primary);
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(-100px);
}

/* ===== ADMIN PAGE WRAPPER ===== */
.admin-page {
  max-width: 1600px; /* Giới hạn width tối đa cho màn hình lớn */
  margin: 0 auto; /* Căn giữa */
  width: 100%;
}

/* ===== ADMIN DASHBOARD ===== */
.admin-dashboard {
  max-width: 100%;
  width: 100%;
  padding: 0;
  min-height: calc(100vh - 70px);
  overflow-x: hidden;
}

/* ===== ANIMATIONS ===== */
@keyframes slideInRight {
  from {
    transform: translateX(100px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes fadeUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes bounceIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-slide-in {
  animation: slideIn 0.6s ease-out;
}

.animate-slide-in.delay-1 {
  animation-delay: 0.2s;
  opacity: 0;
  animation-fill-mode: forwards;
}

.animate-slide-in.delay-2 {
  animation-delay: 0.4s;
  opacity: 0;
  animation-fill-mode: forwards;
}

.animate-fade-up {
  animation: fadeUp 0.6s ease-out;
  opacity: 0;
  animation-fill-mode: forwards;
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

.animate-bounce-in {
  animation: bounceIn 0.8s ease-out;
}

.pulse {
  animation: pulse 2s ease-in-out infinite;
}

.spinning {
  animation: spin 1s linear infinite;
}

/* ===== DASHBOARD HEADER ===== */
.dashboard-header {
  margin-bottom: 16px; /* Giảm từ 24px → 16px */
  padding: 20px 24px;
  background: var(--dark-bg-card);
  border-radius: var(--radius-xl);
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-glass-md);
  border: 1px solid var(--dark-border-color);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
}

/* Decorative Elements */
.header-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
}

.circle-1 {
  width: 350px;
  height: 350px;
  top: -150px;
  right: -100px;
  animation: float 15s ease-in-out infinite;
}

.circle-2 {
  width: 250px;
  height: 250px;
  bottom: -100px;
  left: -50px;
  animation: float 12s ease-in-out infinite reverse;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 20%;
  animation: float 18s ease-in-out infinite;
  opacity: 0.5;
}

.decoration-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  background: linear-gradient(180deg, transparent 0%, rgba(255, 255, 255, 0.1) 100%);
  border-radius: 0 0 16px 16px;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(20px, -20px) scale(1.05);
  }
}

/* Header Content */
.header-content {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.welcome-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 48px;
  height: 48px;
  background: var(--gradient-purple-soft);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  box-shadow: var(--shadow-glass-sm);
  border: 1px solid var(--dark-border-light);
}

.header-icon i {
  font-size: 28px;
  color: var(--primary-light);
}

.welcome-text {
  color: var(--dark-text-primary);
}

.welcome-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0 0 6px 0;
  line-height: 1.2;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.gradient-text {
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 0.9rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.admin-name {
  font-size: 1.5rem;
  font-weight: 800;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  text-transform: capitalize;
}

.welcome-subtitle {
  font-size: 0.9375rem;
  opacity: 0.95;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--dark-text-secondary);
}

.welcome-subtitle i {
  font-size: 18px;
}

/* Profile Card */
.profile-card {
  background: var(--dark-bg-card);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 2px solid var(--dark-border-light);
  border-radius: var(--radius-xl);
  padding: 0;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-glass-md);
  position: relative;
  overflow: visible;
  min-width: 280px;
}

.profile-card:hover {
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--dark-border-medium);
}

.profile-main {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
}

.profile-avatar {
  position: relative;
  flex-shrink: 0;
}

.avatar-wrapper {
  width: 48px;
  height: 48px;
  background: var(--primary-gradient);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: var(--shadow-glow-purple);
  transition: all var(--transition-fast);
}

.avatar-wrapper i {
  font-size: 28px;
  color: white;
}

.avatar-status {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 16px;
  height: 16px;
  background: #10b981;
  border: 3px solid rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  box-shadow: 0 0 12px rgba(16, 185, 129, 0.8);
  animation: pulse-status 2s ease-in-out infinite;
}

@keyframes pulse-status {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.15);
  }
}

.profile-details {
  flex: 1;
  min-width: 0;
  color: var(--dark-text-primary);
}

.profile-name {
  font-size: 1rem;
  font-weight: 700;
  color: var(--dark-text-primary);
  text-transform: capitalize;
  margin-bottom: 4px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.profile-role {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 0.75rem;
  color: var(--primary-light);
  font-weight: 600;
  background: var(--gradient-purple-soft);
  padding: 3px 8px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  margin-bottom: 6px;
}

.profile-role i {
  font-size: 14px;
}

.profile-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.6875rem;
  color: var(--dark-text-secondary);
  font-weight: 500;
}

.profile-time i {
  font-size: 13px;
}

.profile-time .separator {
  margin: 0 4px;
  opacity: 0.6;
}

.profile-menu-toggle {
  background: var(--gradient-purple-soft);
  border: 1px solid var(--dark-border-light);
  border-radius: var(--radius-lg);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-fast);
  color: var(--primary-light);
  flex-shrink: 0;
  backdrop-filter: blur(10px);
}

.profile-menu-toggle:hover {
  background: var(--primary-gradient);
  transform: scale(1.05);
  box-shadow: var(--shadow-glow-purple);
  color: white;
}

.profile-menu-toggle i {
  font-size: 22px;
}

/* Dropdown Menu */
.profile-menu-dropdown {
  background: var(--dark-bg-card);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border-top: 1px solid var(--dark-border-light);
  border-radius: 0 0 var(--radius-lg) var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-glass-lg);
  border: 1px solid var(--dark-border-color);
  border-top-color: var(--dark-border-medium);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: var(--dark-text-primary);
  text-decoration: none;
  transition: all var(--transition-fast);
  cursor: pointer;
  position: relative;
  border-bottom: 1px solid var(--dark-border-color);
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item i {
  font-size: 20px;
  color: var(--primary-color);
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.menu-item span {
  flex: 1;
  font-size: 0.875rem;
  font-weight: 500;
}

.menu-item i.arrow {
  font-size: 18px;
  color: var(--dark-text-tertiary);
  opacity: 0;
  transform: translateX(-4px);
  transition: all var(--transition-fast);
}

.menu-item:hover {
  background: var(--gradient-purple-soft);
  padding-left: 20px;
}

.menu-item:hover i {
  color: var(--primary-light);
  transform: scale(1.1);
}

.menu-item:hover i.arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--primary-light);
}

.logout-item i {
  color: var(--error-color);
}

.logout-item span {
  color: var(--error-color);
  font-weight: 600;
}

.logout-item:hover {
  background: rgba(239, 68, 68, 0.15);
}

.logout-item:hover i {
  color: #dc2626;
  transform: scale(1.1);
}

.logout-item:hover span {
  color: #dc2626;
}

.logout-item:hover i.arrow {
  color: #dc2626;
}

/* Dropdown Animation */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all var(--transition-normal);
  transform-origin: top;
}

.dropdown-enter-from {
  opacity: 0;
  transform: scaleY(0.8) translateY(-10px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: scaleY(0.8) translateY(-10px);
}

/* ===== STATS GRID ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 20px;
  margin-bottom: 24px;
  margin-top: 8px; /* Tạo khoảng cách với header */
  width: 100%;
  max-width: 100%;
  position: relative;
  z-index: 1; /* Ưu tiên hơn sections bên dưới khi hover */
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.stat-card {
  background: var(--dark-bg-card);
  padding: 16px;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-glass-sm);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border: 1px solid var(--dark-border-color);
  transition: all var(--transition-slow);
  min-width: 0;
  width: 100%;
  position: relative;
  overflow: hidden;
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  text-align: center;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s;
}

.stat-card:hover::before {
  left: 100%;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--dark-border-medium);
  z-index: 10; /* Đảm bảo card hover nằm trên các card khác */
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon svg {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.revenue-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.orders-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.products-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.users-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-content h3 {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  color: var(--dark-text-primary);
  line-height: 1.2;
}

.stat-label {
  color: var(--dark-text-tertiary);
  font-size: 0.75rem;
  margin: 0;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  font-weight: 500;
}

.stat-trend i {
  font-size: 16px;
}

.stat-trend.positive {
  color: #22c55e;
}

.stat-trend.negative {
  color: #ef4444;
}

.stat-trend.neutral {
  color: #94a3b8;
}

/* ===== SECTION HEADER ===== */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--dark-text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-title i {
  color: var(--primary-color);
  font-size: 20px;
}

.view-all-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--primary-gradient);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-glow-purple);
}

.view-all-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(167, 139, 250, 0.5);
  background: var(--primary-gradient-hover);
}

/* ===== QUICK ACTIONS ===== */
.quick-actions {
  margin-bottom: 24px;
  margin-top: 16px; /* Tạo khoảng cách cho shadow của stat cards */
  width: 100%;
  max-width: 100%;
  position: relative;
  z-index: 0; /* Đảm bảo không che hover của sections phía trên */
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
  padding: 20px;
  width: 100%;
  max-width: 100%;
  position: relative;
  z-index: 1; /* Ưu tiên hơn sections bên dưới khi hover */
}

@media (max-width: 1400px) {
  .actions-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.action-card {
  background: var(--dark-bg-card);
  padding: 20px 16px;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-glass-sm);
  text-decoration: none;
  color: inherit;
  transition: all var(--transition-slow);
  border: 1px solid var(--dark-border-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  min-width: 0;
  width: 100%;
  position: relative;
  overflow: hidden;
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  min-height: 140px;
}

.action-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(167, 139, 250, 0.15), transparent);
  transform: rotate(45deg);
  transition: all 0.5s;
  z-index: 0; /* Đặt phía sau nội dung */
  pointer-events: none; /* Không chặn tương tác */
}

.action-card:hover::before {
  animation: shine 1.5s infinite;
}

@keyframes shine {
  0% {
    top: -50%;
    right: -50%;
  }
  100% {
    top: 150%;
    right: 150%;
  }
}

.hover-lift:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--dark-border-medium);
  z-index: 10; /* Đảm bảo card hover nằm trên các card khác */
}

.action-icon {
  width: 56px;
  height: 56px;
  background: var(--primary-gradient);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 16px;
  flex-shrink: 0;
  box-shadow: var(--shadow-glow-purple);
  transition: all var(--transition-fast);
  position: relative;
  z-index: 1; /* Hiển thị phía trên ::before */
}

.action-card:hover .action-icon {
  transform: scale(1.1) rotate(5deg);
}

.action-icon svg {
  width: 28px;
  height: 28px;
  flex-shrink: 0;
}

.action-card h3 {
  font-size: 0.9375rem;
  font-weight: 600;
  margin: 0 0 6px 0;
  color: var(--dark-text-primary);
  line-height: 1.3;
  position: relative;
  z-index: 1; /* Hiển thị phía trên ::before */
}

.action-card p {
  font-size: 0.6875rem;
  color: var(--dark-text-tertiary);
  margin: 0;
  line-height: 1.4;
  position: relative;
  z-index: 1; /* Hiển thị phía trên ::before */
}

.action-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: var(--primary-gradient);
  color: white;
  padding: 4px 10px;
  border-radius: var(--radius-lg);
  font-size: 11px;
  font-weight: 600;
  box-shadow: var(--shadow-glow-purple);
  z-index: 2; /* Hiển thị trên cùng */
}

.action-badge.new {
  background: linear-gradient(135deg, var(--success-color) 0%, #38f9d7 100%);
  box-shadow: 0 2px 8px rgba(52, 211, 153, 0.4);
}

/* ===== CHARTS SECTION ===== */
.charts-section {
  margin-bottom: 24px;
  margin-top: 16px; /* Tạo khoảng cách cho shadow của action cards */
  width: 100%;
  max-width: 100%;
  position: relative;
  z-index: 0; /* Đảm bảo không che hover của sections phía trên */
}

.chart-controls {
  display: flex;
  gap: 8px;
}

.period-btn {
  background: var(--dark-bg-card);
  border: 1px solid var(--dark-border-color);
  padding: 6px 16px;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  color: var(--dark-text-secondary);
  transition: all var(--transition-fast);
}

.period-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-light);
}

.period-btn.active {
  background: var(--primary-gradient);
  color: white;
  border-color: transparent;
  box-shadow: var(--shadow-glow-purple);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  width: 100%;
  max-width: 100%;
  position: relative;
  z-index: 1; /* Ưu tiên hơn sections bên dưới khi hover */
}

.chart-card {
  background: var(--dark-bg-card);
  padding: 10px;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-glass-sm);
  border: 1px solid var(--dark-border-color);
  min-width: 0;
  width: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
}

.chart-card.large {
  grid-column: span 2;
}

.shine-effect {
  position: relative;
  overflow: hidden;
}

.shine-effect::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -60%;
  width: 20%;
  height: 200%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transform: rotate(25deg);
  animation: shine-move 6s infinite;
}

@keyframes shine-move {
  0% {
    left: -60%;
  }
  20% {
    left: 120%;
  }
  100% {
    left: 120%;
  }
}

.chart-header {
  margin-bottom: 6px;
  padding-bottom: 4px;
  border-bottom: 1px solid var(--dark-border-subtle);
  flex-shrink: 0;
}

.chart-header h3 {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--dark-text-primary);
  margin: 0 0 2px 0;
  line-height: 1.2;
}

.chart-subtitle {
  font-size: 9px;
  color: var(--dark-text-tertiary);
  font-weight: 500;
  line-height: 1.1;
}

.chart-container {
  flex: 1;
  min-height: 0;
  position: relative;
  width: 100%;
  max-width: 100%;
  padding: 0;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-container canvas {
  max-width: 100%;
  height: auto !important;
}

/* ===== RECENT ACTIVITY ===== */
.recent-activity {
  margin-bottom: 24px;
  margin-top: 16px; /* Tạo khoảng cách cho shadow của chart cards */
  width: 100%;
  max-width: 100%;
  position: relative;
  z-index: 0; /* Đảm bảo không che hover của sections phía trên */
}

.activity-card {
  background: var(--dark-bg-card);
  padding: 12px;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-glass-sm);
  border: 1px solid var(--dark-border-color);
  width: 100%;
  max-width: 100%;
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.hover-highlight:hover {
  background: var(--gradient-purple-soft);
  transform: translateX(4px);
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.activity-icon svg {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.activity-icon.order {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.activity-icon.user {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.activity-icon.product {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 0.875rem;
  color: var(--dark-text-primary);
  margin: 0 0 4px 0;
  line-height: 1.4;
  font-weight: 500;
}

.activity-time {
  font-size: 11px;
  color: var(--dark-text-tertiary);
  font-weight: 500;
}

.activity-action {
  background: none;
  border: 1px solid var(--dark-border-color);
  padding: 6px;
  border-radius: var(--radius-md);
  cursor: pointer;
  color: var(--dark-text-tertiary);
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.activity-action:hover {
  background: var(--primary-gradient);
  border-color: var(--primary-color);
  color: white;
  transform: scale(1.1);
  box-shadow: var(--shadow-glow-purple);
}

/* ===== REFRESH CONTROLS ===== */
.refresh-controls {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-top: var(--space-4);
  padding: var(--space-3);
  background: rgba(167, 139, 250, 0.1);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(167, 139, 250, 0.2);
  backdrop-filter: blur(10px);
}

.btn-refresh,
.btn-auto-refresh {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-refresh:hover {
  background: var(--gradient-primary);
  border-color: var(--accent-primary);
  color: white;
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-refresh .material-icons {
  font-size: 18px;
  transition: transform var(--transition-fast);
}

.btn-refresh:hover .material-icons {
  transform: rotate(180deg);
}

.btn-auto-refresh.active {
  background: var(--gradient-primary);
  border-color: var(--accent-primary);
  color: white;
}

.btn-auto-refresh .material-icons {
  animation: spin 2s linear infinite;
}

.btn-auto-refresh:not(.active) .material-icons {
  animation: none;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.last-refresh {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin-left: auto;
}

/* ===== RESPONSIVE ===== */
@media (min-width: 769px) and (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-card.large {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .admin-page {
    max-width: 100%;
  }

  .admin-dashboard {
    padding: 0;
  }

  .toast-container {
    right: 10px;
    left: 10px;
    max-width: none;
  }

  .toast-notification {
    min-width: auto;
  }

  .dashboard-header {
    padding: 20px;
  }

  .header-content {
    flex-direction: column;
    gap: 16px;
  }

  .welcome-section {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .header-icon {
    width: 48px;
    height: 48px;
  }

  .header-icon i {
    font-size: 28px;
  }

  .welcome-title {
    font-size: 1.375rem;
  }

  .admin-name {
    font-size: 1.375rem;
  }

  .welcome-subtitle {
    font-size: 0.875rem;
    justify-content: center;
  }

  .profile-card {
    min-width: auto;
    width: 100%;
  }

  .profile-main {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .profile-details {
    align-items: center;
    text-align: center;
  }

  .profile-name {
    font-size: 1rem;
  }

  .profile-time {
    justify-content: center;
    flex-wrap: wrap;
  }

  .profile-menu-toggle {
    width: 100%;
    border-radius: 10px;
  }

  .circle-1, .circle-2, .circle-3 {
    display: none;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--space-3);
  }

  .actions-grid {
    grid-template-columns: 1fr;
    gap: var(--space-3);
  }

  .charts-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .chart-card.large {
    grid-column: span 1;
  }

  .chart-container {
    height: 180px;
  }
  
  .section-title {
    font-size: var(--text-lg);
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-2);
  }

  .chart-controls {
    width: 100%;
    justify-content: space-between;
  }

  .period-btn {
    flex: 1;
  }
}
</style>

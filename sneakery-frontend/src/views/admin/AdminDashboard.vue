<template>
  <div class="admin-dashboard">
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
      <div class="welcome-section">
        <h1 class="welcome-title animate-slide-in">
          Dashboard Admin 🚀
        </h1>
        <p class="welcome-subtitle animate-slide-in delay-1">
          Quản lý và giám sát hệ thống Sneakery Store
        </p>
        <div class="header-stats animate-slide-in delay-2">
          <div class="mini-stat">
            <i class="material-icons">access_time</i>
            <span>{{ currentTime }}</span>
          </div>
          <div class="mini-stat">
            <i class="material-icons">event</i>
            <span>{{ currentDate }}</span>
          </div>
        </div>
      </div>
      <div class="header-badge animate-bounce-in">
        <el-avatar :size="80" class="avatar pulse">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </el-avatar>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card animate-fade-up" style="animation-delay: 0.1s">
        <div class="stat-icon revenue-icon">
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

      <div class="stat-card animate-fade-up" style="animation-delay: 0.2s">
        <div class="stat-icon orders-icon">
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

      <div class="stat-card animate-fade-up" style="animation-delay: 0.3s">
        <div class="stat-icon products-icon">
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

      <div class="stat-card animate-fade-up" style="animation-delay: 0.4s">
        <div class="stat-icon users-icon">
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
        <button class="refresh-btn" @click="refreshData" :class="{ spinning: refreshing }">
          <i class="material-icons">refresh</i>
          <span>Làm mới</span>
        </button>
      </div>
      <div class="actions-grid">
        <router-link to="/admin/products" class="action-card hover-lift">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý sản phẩm</h3>
          <p>Thêm, sửa, xóa sản phẩm và biến thể</p>
          <div class="action-badge">{{ stats?.totalProducts || 0 }}</div>
        </router-link>

        <router-link to="/admin/orders" class="action-card hover-lift">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý đơn hàng</h3>
          <p>Theo dõi và xử lý đơn hàng</p>
          <div class="action-badge">{{ stats?.totalOrders || 0 }}</div>
        </router-link>

        <router-link to="/admin/users" class="action-card hover-lift">
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

        <router-link to="/admin/brands" class="action-card hover-lift">
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

        <router-link to="/admin/categories" class="action-card hover-lift">
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

        <router-link to="/admin/analytics" class="action-card hover-lift">
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
import { ref, onMounted, onUnmounted } from 'vue';
import { useAdminStore } from '@/stores/admin';
import LineChart from '@/components/charts/LineChart.vue';
import BarChart from '@/components/charts/BarChart.vue';
import DoughnutChart from '@/components/charts/DoughnutChart.vue';

const adminStore = useAdminStore();

// State
const loading = ref(false);
const refreshing = ref(false);
const selectedPeriod = ref('7d');
const currentTime = ref('');
const currentDate = ref('');
const notifications = ref([]);
let notificationIdCounter = 0;

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

const loadDashboardData = async () => {
  loading.value = true;
  try {
    await adminStore.fetchDashboardStats();
    stats.value = adminStore.dashboardStats || {
      totalRevenue: 0,
      totalOrders: 0,
      totalProducts: 0,
      totalUsers: 0
    };
    
    showNotification('success', 'Thành công', 'Đã tải dữ liệu dashboard');
  } catch (error) {
    console.error('Error loading dashboard data:', error);
    showNotification('error', 'Lỗi', 'Không thể tải dữ liệu dashboard');
  } finally {
    loading.value = false;
  }
};

const refreshData = async () => {
  refreshing.value = true;
  try {
    await loadDashboardData();
    showNotification('success', 'Đã làm mới', 'Dữ liệu đã được cập nhật');
  } catch (error) {
    showNotification('error', 'Lỗi', 'Không thể làm mới dữ liệu');
  } finally {
    setTimeout(() => {
      refreshing.value = false;
    }, 1000);
  }
};

const changePeriod = (period) => {
  selectedPeriod.value = period;
  showNotification('info', 'Thay đổi chu kỳ', `Đang hiển thị dữ liệu ${period === '7d' ? '7 ngày' : period === '30d' ? '30 ngày' : '90 ngày'}`);
};

// Lifecycle
let timeInterval;
onMounted(() => {
  loadDashboardData();
  updateDateTime();
  timeInterval = setInterval(updateDateTime, 1000);
  
  // Show welcome notification
  setTimeout(() => {
    showNotification('info', 'Chào mừng!', 'Chào mừng bạn quay trở lại Admin Dashboard');
  }, 500);
});

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval);
  }
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
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: flex-start;
  gap: 12px;
  border-left: 4px solid;
  min-width: 320px;
  animation: slideInRight 0.3s ease;
}

.toast-notification.success {
  border-color: #22c55e;
}

.toast-notification.error {
  border-color: #ef4444;
}

.toast-notification.warning {
  border-color: #f59e0b;
}

.toast-notification.info {
  border-color: #3b82f6;
}

.toast-notification > i {
  flex-shrink: 0;
  font-size: 24px;
}

.toast-notification.success > i {
  color: #22c55e;
}

.toast-notification.error > i {
  color: #ef4444;
}

.toast-notification.warning > i {
  color: #f59e0b;
}

.toast-notification.info > i {
  color: #3b82f6;
}

.toast-content {
  flex: 1;
  min-width: 0;
}

.toast-title {
  font-weight: 600;
  font-size: 14px;
  margin: 0 0 4px 0;
  color: #1e293b;
}

.toast-message {
  font-size: 13px;
  margin: 0;
  color: #64748b;
}

.toast-close {
  background: none;
  border: none;
  cursor: pointer;
  color: #94a3b8;
  padding: 0;
  flex-shrink: 0;
  transition: color 0.2s;
}

.toast-close:hover {
  color: #1e293b;
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

/* ===== ADMIN DASHBOARD ===== */
.admin-dashboard {
  max-width: 100%;
  width: 100%;
  margin: 0 auto;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #ffffff;
  position: relative;
  overflow: hidden;
  width: 100%;
  max-width: 100%;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.dashboard-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -5%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.08) 0%, transparent 70%);
  border-radius: 50%;
}

.welcome-section {
  position: relative;
  z-index: 1;
}

.welcome-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  line-height: 1.25;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  color: #ffffff;
}

.welcome-subtitle {
  font-size: 0.875rem;
  opacity: 0.9;
  margin: 0 0 12px 0;
  line-height: 1.5;
  color: #ffffff;
}

.header-stats {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.mini-stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  background: rgba(255, 255, 255, 0.15);
  padding: 6px 12px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
  color: #ffffff;
}

.mini-stat i {
  font-size: 16px;
  color: #ffffff;
}

.header-badge {
  position: relative;
  z-index: 1;
}

.header-badge .avatar {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  border: 3px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

/* ===== STATS GRID ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 10px;
  margin-bottom: 16px;
  width: 100%;
  max-width: 100%;
}

.stat-card {
  background: #ffffff;
  padding: 12px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #e2e8f0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 0;
  width: 100%;
  position: relative;
  overflow: hidden;
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
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.2);
  border-color: #e6f0ff;
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
}

.stat-content h3 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #1e293b;
  line-height: 1.2;
}

.stat-label {
  color: #64748b;
  font-size: 0.75rem;
  margin: 0 0 6px 0;
  font-weight: 500;
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
  margin-bottom: 10px;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-title i {
  color: #667eea;
  font-size: 20px;
}

.refresh-btn,
.view-all-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.refresh-btn:hover,
.view-all-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.refresh-btn.spinning i {
  animation: spin 1s linear infinite;
}

/* ===== QUICK ACTIONS ===== */
.quick-actions {
  margin-bottom: 16px;
  width: 100%;
  max-width: 100%;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 10px;
  width: 100%;
  max-width: 100%;
}

.action-card {
  background: #ffffff;
  padding: 12px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-decoration: none;
  color: inherit;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  min-width: 0;
  width: 100%;
  position: relative;
  overflow: hidden;
}

.action-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(102, 126, 234, 0.1), transparent);
  transform: rotate(45deg);
  transition: all 0.5s;
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
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.2);
  border-color: #e6f0ff;
}

.action-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  margin-bottom: 12px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.action-card:hover .action-icon {
  transform: scale(1.1) rotate(5deg);
}

.action-icon svg {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.action-card h3 {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: #1e293b;
}

.action-card p {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

.action-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(245, 87, 108, 0.3);
}

.action-badge.new {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

/* ===== CHARTS SECTION ===== */
.charts-section {
  margin-bottom: 16px;
  width: 100%;
  max-width: 100%;
}

.chart-controls {
  display: flex;
  gap: 8px;
}

.period-btn {
  background: white;
  border: 1px solid #e2e8f0;
  padding: 6px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  transition: all 0.3s;
}

.period-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.period-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  width: 100%;
  max-width: 100%;
}

.chart-card {
  background: #ffffff;
  padding: 10px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 0;
  width: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.chart-card.large {
  grid-column: span 2;
}

.chart-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
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
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.chart-header h3 {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 2px 0;
  line-height: 1.2;
}

.chart-subtitle {
  font-size: 9px;
  color: #94a3b8;
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
  margin-bottom: 16px;
  width: 100%;
  max-width: 100%;
}

.activity-card {
  background: #ffffff;
  padding: 12px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  width: 100%;
  max-width: 100%;
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
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, transparent 100%);
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
  color: #1e293b;
  margin: 0 0 4px 0;
  line-height: 1.4;
  font-weight: 500;
}

.activity-time {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
}

.activity-action {
  background: none;
  border: 1px solid #e2e8f0;
  padding: 6px;
  border-radius: 8px;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s;
  flex-shrink: 0;
}

.activity-action:hover {
  background: #667eea;
  border-color: #667eea;
  color: white;
  transform: scale(1.1);
}

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
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
    flex-direction: column;
    text-align: center;
    gap: var(--space-3);
    padding: var(--space-4);
  }

  .welcome-title {
    font-size: var(--text-xl);
  }

  .header-stats {
    justify-content: center;
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
    gap: 12px;
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

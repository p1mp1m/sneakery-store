<template>
  <div class="admin-dashboard">
    <!-- Dashboard Header -->
    <div class="dashboard-header">
      <div class="welcome-section">
        <h1 class="welcome-title">
          Dashboard Admin 🚀
        </h1>
        <p class="welcome-subtitle">
          Quản lý và giám sát hệ thống Sneakery Store
        </p>
      </div>
      <div class="header-badge">
        <el-avatar :size="80" class="avatar">
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
      <div class="stat-card">
        <div class="stat-icon revenue-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <line x1="12" y1="1" x2="12" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M17 5H9.5C8.11929 5 7 6.11929 7 7.5S8.11929 10 9.5 10H14.5C15.8807 10 17 11.1193 17 12.5S15.8807 15 14.5 15H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ formatCurrency(stats?.totalRevenue || 0) }}</h3>
          <p class="stat-label">Tổng doanh thu</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orders-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats?.totalOrders || 0 }}</h3>
          <p class="stat-label">Tổng đơn hàng</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon products-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 6V4C16 3.46957 15.7893 2.96086 15.4142 2.58579C15.0391 2.21071 14.5304 2 14 2H10C9.46957 2 8.96086 2.21071 8.58579 2.58579C8.21071 2.96086 8 3.46957 8 4V6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats?.totalProducts || 0 }}</h3>
          <p class="stat-label">Sản phẩm</p>
        </div>
      </div>

      <div class="stat-card">
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
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions">
      <h2 class="section-title">Quản lý nhanh</h2>
      <div class="actions-grid">
        <router-link to="/admin/products" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý sản phẩm</h3>
          <p>Thêm, sửa, xóa sản phẩm và biến thể</p>
        </router-link>

        <router-link to="/admin/orders" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý đơn hàng</h3>
          <p>Theo dõi và xử lý đơn hàng</p>
        </router-link>

        <router-link to="/admin/users" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý người dùng</h3>
          <p>Xem và quản lý tài khoản người dùng</p>
        </router-link>

        <router-link to="/admin/brands" class="action-card">
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
        </router-link>

        <router-link to="/admin/categories" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 19H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M4 15H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M4 11H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Quản lý danh mục</h3>
          <p>Quản lý danh mục sản phẩm</p>
        </router-link>

        <router-link to="/admin/analytics" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 20V10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 20V4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M6 20V14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Phân tích</h3>
          <p>Xem báo cáo và thống kê</p>
        </router-link>
              </div>
            </div>

    <!-- Charts Section -->
    <div class="charts-section">
      <h2 class="section-title">Biểu đồ thống kê</h2>
      
      <div class="charts-grid">
        <!-- Revenue Chart -->
        <div class="chart-card large">
          <div class="chart-header">
            <h3>Doanh thu 7 ngày gần đây</h3>
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
      <div class="chart-card">
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
        <div class="chart-card">
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
    <div class="recent-activity">
      <h2 class="section-title">Hoạt động gần đây</h2>
      <div class="activity-card">
        <el-skeleton v-if="loading" :rows="5" animated />
        <div v-else class="activity-list">
          <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAdminStore } from '@/stores/admin';
import LineChart from '@/components/charts/LineChart.vue';
import BarChart from '@/components/charts/BarChart.vue';
import DoughnutChart from '@/components/charts/DoughnutChart.vue';

const adminStore = useAdminStore();

// State
const loading = ref(false);
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
  } catch (error) {
    console.error('Error loading dashboard data:', error);
  } finally {
    loading.value = false;
  }
};

// Lifecycle
onMounted(() => {
  loadDashboardData();
});
</script>

<style scoped>
/* ===== ADMIN DASHBOARD ===== */
.admin-dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-6) var(--space-4);
  min-height: calc(100vh - 70px);
}

/* ===== DASHBOARD HEADER ===== */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-12);
  padding: var(--space-8);
  background: var(--primary-gradient);
  border-radius: var(--radius-2xl);
  color: var(--white);
  position: relative;
  overflow: hidden;
}

.dashboard-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.welcome-section {
  position: relative;
  z-index: 1;
}

.welcome-title {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  margin: 0 0 var(--space-2) 0;
  line-height: var(--leading-tight);
}

.welcome-subtitle {
  font-size: var(--text-lg);
  opacity: 0.9;
  margin: 0;
  line-height: var(--leading-relaxed);
}

.header-badge {
  position: relative;
  z-index: 1;
}

.header-badge .avatar {
  background: rgba(255, 255, 255, 0.2);
  color: var(--white);
  border: 3px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

/* ===== STATS GRID ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-16);
}

.stat-card {
  background: var(--bg-card);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  border: 1px solid var(--border-light);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow);
  border-color: var(--primary-light);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--white);
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

.stat-content h3 {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin: 0 0 var(--space-1) 0;
  color: var(--text-primary);
}

.stat-label {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  margin: 0;
  font-weight: var(--font-medium);
}

/* ===== QUICK ACTIONS ===== */
.quick-actions {
  margin-bottom: var(--space-12);
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-8) 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
}

.action-card {
  background: var(--bg-card);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  text-decoration: none;
  color: inherit;
  transition: all var(--transition-normal);
  border: 1px solid var(--border-light);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow);
  border-color: var(--primary-light);
  text-decoration: none;
  color: inherit;
}

.action-icon {
  width: 60px;
  height: 60px;
  background: var(--primary-gradient);
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--white);
  margin-bottom: var(--space-4);
}

.action-card h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin: 0 0 var(--space-2) 0;
  color: var(--text-primary);
}

.action-card p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
  line-height: var(--leading-relaxed);
}

/* ===== CHARTS SECTION ===== */
.charts-section {
  margin-bottom: var(--space-12);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-6);
}

.chart-card {
  background: var(--bg-card);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  transition: all var(--transition-normal);
}

.chart-card.large {
  grid-column: span 2;
}

.chart-card:hover {
  box-shadow: var(--shadow-md);
}

.chart-header {
  margin-bottom: var(--space-4);
  padding-bottom: var(--space-3);
  border-bottom: 1px solid var(--border-light);
}

.chart-header h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.chart-subtitle {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
}

.chart-container {
  height: 300px;
  position: relative;
}

/* ===== RECENT ACTIVITY ===== */
.recent-activity {
  margin-bottom: var(--space-12);
}

.activity-card {
  background: var(--bg-card);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: var(--space-4);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  transition: all var(--transition-normal);
}

.activity-item:hover {
  background: var(--bg-secondary);
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--white);
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
}

.activity-text {
  font-size: var(--text-base);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  line-height: var(--leading-relaxed);
}

.activity-time {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
}

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .admin-dashboard {
    padding: var(--space-4) var(--space-2);
  }

  .dashboard-header {
    flex-direction: column;
    text-align: center;
    gap: var(--space-4);
    padding: var(--space-6);
  }

  .welcome-title {
    font-size: var(--text-3xl);
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .actions-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-card.large {
    grid-column: span 1;
  }

  .chart-container {
    height: 250px;
  }
}
</style>

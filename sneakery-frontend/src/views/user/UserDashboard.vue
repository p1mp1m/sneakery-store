<template>
  <div class="user-page user-dashboard">
    <!-- Header -->
    <div class="dashboard-header">
      <div class="welcome-section">
        <h1 class="welcome-title">
          Chào mừng trở lại, {{ authStore.currentUser?.fullName || 'Người dùng' }}! 👋
        </h1>
        <p class="welcome-subtitle">
          Quản lý tài khoản và đơn hàng của bạn một cách dễ dàng
        </p>
      </div>
      <div class="user-avatar">
        <el-avatar :size="80" :src="userAvatar" class="avatar">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </el-avatar>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon orders-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ orderStats.totalOrders }}</h3>
          <p class="stat-label">Tổng đơn hàng</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon pending-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
            <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ orderStats.pendingOrders }}</h3>
          <p class="stat-label">Đang xử lý</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon completed-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ orderStats.completedOrders }}</h3>
          <p class="stat-label">Đã hoàn thành</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon total-spent-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <line x1="12" y1="1" x2="12" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M17 5H9.5C8.11929 5 7 6.11929 7 7.5S8.11929 10 9.5 10H14.5C15.8807 10 17 11.1193 17 12.5S15.8807 15 14.5 15H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-number">{{ formatCurrency(orderStats.totalSpent) }}</h3>
          <p class="stat-label">Tổng chi tiêu</p>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions">
      <h2 class="section-title">Thao tác nhanh</h2>
      <div class="actions-grid">
        <router-link to="/user/profile" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Thông tin cá nhân</h3>
          <p>Xem và chỉnh sửa thông tin tài khoản</p>
        </router-link>

        <router-link to="/user/orders" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Đơn hàng của tôi</h3>
          <p>Theo dõi trạng thái đơn hàng</p>
        </router-link>

        <router-link to="/cart" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="9" cy="21" r="1" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="20" cy="21" r="1" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M1 1H5L7.68 14.39C7.77144 14.8504 8.02191 15.264 8.38755 15.5583C8.75318 15.8526 9.2107 16.009 9.68 16H19.4C19.8693 16.009 20.3268 15.8526 20.6925 15.5583C21.0581 15.264 21.3086 14.8504 21.4 14.39L23 6H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Giỏ hàng</h3>
          <p>Xem và quản lý sản phẩm đã chọn</p>
        </router-link>

        <router-link to="/user/wishlist" class="action-card">
          <div class="action-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69364 19.0554 3.41708C18.3879 3.14052 17.6725 2.99817 16.95 2.99817C16.2275 2.99817 15.5121 3.14052 14.8446 3.41708C14.1772 3.69364 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.5783 8.50903 2.9987 7.05 2.9987C5.59096 2.9987 4.19169 3.5783 3.16 4.61C2.1283 5.6417 1.5487 7.04097 1.5487 8.5C1.5487 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6053C22.3095 9.93789 22.4518 9.22248 22.4518 8.5C22.4518 7.77752 22.3095 7.06211 22.0329 6.39467C21.7563 5.72723 21.351 5.1208 20.84 4.61Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>Danh sách yêu thích</h3>
          <p>Sản phẩm bạn đã lưu</p>
        </router-link>
      </div>
    </div>

    <!-- Recent Orders -->
    <div class="recent-orders">
      <div class="section-header">
        <h2 class="section-title">Đơn hàng gần đây</h2>
        <router-link to="/user/orders" class="view-all-link">
          Xem tất cả
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </router-link>
      </div>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <div v-else-if="recentOrders.length === 0" class="empty-state">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="empty-icon">
          <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h3>Chưa có đơn hàng nào</h3>
        <p>Bắt đầu mua sắm để xem đơn hàng của bạn ở đây</p>
        <router-link to="/home/products" class="btn btn-primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Mua sắm ngay
        </router-link>
      </div>

      <div v-else class="orders-list">
        <div v-for="order in recentOrders" :key="order.id" class="order-item">
          <div class="order-info">
            <div class="order-header">
              <h4 class="order-id">#{{ order.id }}</h4>
              <span class="order-date">{{ formatDate(order.createdAt) }}</span>
            </div>
            <div class="order-details">
              <p class="order-items">{{ order.itemCount }} sản phẩm</p>
              <p class="order-total">{{ formatCurrency(order.total) }}</p>
            </div>
          </div>
          <div class="order-status">
            <el-tag :type="getStatusType(order.status)" size="large">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- Recently Viewed Products -->
    <div class="recently-viewed">
      <div class="section-header">
        <h2 class="section-title">Sản phẩm đã xem gần đây</h2>
        <button v-if="recentlyViewed.length > 0" @click="clearRecentlyViewed" class="clear-btn">
          Xóa tất cả
        </button>
      </div>

      <div v-if="recentlyViewed.length === 0" class="empty-state-mini">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="empty-icon">
          <path d="M15 12C15 13.6569 13.6569 15 12 15C10.3431 15 9 13.6569 9 12C9 10.3431 10.3431 9 12 9C13.6569 9 15 10.3431 15 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M2.45801 12C3.73201 7.943 7.52301 5 12.0001 5C16.4781 5 20.2681 7.943 21.5421 12C20.2681 16.057 16.4781 19 12.0001 19C7.52301 19 3.73201 16.057 2.45801 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <p>Chưa xem sản phẩm nào</p>
      </div>

      <div v-else class="products-grid">
        <router-link
          v-for="product in recentlyViewed"
          :key="product.id"
          :to="`/products/${product.slug}`"
          class="product-item"
        >
          <div class="product-image-wrapper">
            <img :src="product.imageUrl || '/placeholder-image.png'" :alt="product.name" class="product-image" />
            <button
              @click.prevent="removeFromRecentlyViewed(product.id)"
              class="remove-btn"
              title="Xóa khỏi danh sách"
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-brand">{{ product.brandName }}</p>
            <p class="product-price">{{ formatCurrency(product.price) }}</p>
          </div>
        </router-link>
      </div>
    </div>

    <!-- Recommended Products -->
    <div class="recommended-products">
      <h2 class="section-title">Sản phẩm đề xuất</h2>
      <div v-if="recommendedProducts.length === 0" class="loading-container">
        <el-skeleton :rows="2" animated />
      </div>
      <div v-else class="products-grid">
        <div v-for="product in recommendedProducts" :key="product.id" class="product-item">
          <img :src="product.imageUrl || '/placeholder-image.png'" :alt="product.name" class="product-image" />
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-brand">{{ product.brandName }}</p>
            <p class="product-price">{{ formatCurrency(product.price) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRecentlyViewed } from '@/composables/useRecentlyViewed';

const authStore = useAuthStore();
const { recentlyViewed, clearAll, removeProduct } = useRecentlyViewed();

// State
const loading = ref(false);
const recentOrders = ref([]);
const recommendedProducts = ref([]);

// Computed
const userAvatar = computed(() => {
  return authStore.currentUser?.avatar || null;
});

const orderStats = ref({
  totalOrders: 0,
  pendingOrders: 0,
  completedOrders: 0,
  totalSpent: 0
});

// Methods
const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};

const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'PROCESSING': 'info',
    'SHIPPED': 'primary',
    'DELIVERED': 'success',
    'CANCELLED': 'danger'
  };
  return statusMap[status] || 'info';
};

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': 'Chờ xử lý',
    'PROCESSING': 'Đang xử lý',
    'SHIPPED': 'Đang giao',
    'DELIVERED': 'Đã giao',
    'CANCELLED': 'Đã hủy'
  };
  return statusMap[status] || status;
};

const clearRecentlyViewed = () => {
  if (confirm('Bạn có chắc muốn xóa tất cả sản phẩm đã xem?')) {
    clearAll();
  }
};

const removeFromRecentlyViewed = (productId) => {
  removeProduct(productId);
};

const loadDashboardData = async () => {
  loading.value = true;
  try {
    // Mock data - sẽ thay thế bằng API calls thực tế
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Mock order stats
    orderStats.value = {
      totalOrders: 12,
      pendingOrders: 2,
      completedOrders: 8,
      totalSpent: 2500000
    };

    // Mock recent orders
    recentOrders.value = [
      {
        id: 'ORD-001',
        createdAt: '2024-01-15T10:30:00Z',
        itemCount: 2,
        total: 1200000,
        status: 'DELIVERED'
      },
      {
        id: 'ORD-002',
        createdAt: '2024-01-14T14:20:00Z',
        itemCount: 1,
        total: 850000,
        status: 'SHIPPED'
      },
      {
        id: 'ORD-003',
        createdAt: '2024-01-13T09:15:00Z',
        itemCount: 3,
        total: 2100000,
        status: 'PROCESSING'
      }
    ];

    // Mock recommended products
    recommendedProducts.value = [
      {
        id: 1,
        name: 'Nike Air Force 1',
        brandName: 'Nike',
        price: 2500000,
        imageUrl: '/placeholder-image.png'
      },
      {
        id: 2,
        name: 'Adidas Ultraboost 22',
        brandName: 'Adidas',
        price: 3200000,
        imageUrl: '/placeholder-image.png'
      },
      {
        id: 3,
        name: 'Converse Chuck Taylor',
        brandName: 'Converse',
        price: 1200000,
        imageUrl: '/placeholder-image.png'
      }
    ];
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
/* ===== USER DASHBOARD ===== */
.user-dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: 100px var(--space-6) var(--space-8);
}

/* ===== DASHBOARD HEADER ===== */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-10);
  padding: var(--space-8);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--radius-xl);
  color: white;
  position: relative;
  overflow: hidden;
}

.dashboard-header::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.welcome-section {
  position: relative;
  z-index: 1;
}

.welcome-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 var(--space-2) 0;
  color: white;
  z-index: 1;
  position: relative;
}

.welcome-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  z-index: 1;
  position: relative;
}

.user-avatar {
  position: relative;
  z-index: 1;
}

.user-avatar .avatar {
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
  background: rgba(30, 41, 59, 0.6);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  border: 1px solid rgba(167, 139, 250, 0.15);
  transition: all 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  border-color: rgba(167, 139, 250, 0.3);
  box-shadow: 0 8px 24px rgba(167, 139, 250, 0.1);
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

.orders-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.pending-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.completed-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.total-spent-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 var(--space-1) 0;
  color: #f1f5f9;
}

.stat-label {
  color: #94a3b8;
  font-size: 14px;
  margin: 0;
  font-weight: 500;
}

/* ===== QUICK ACTIONS ===== */
.quick-actions {
  margin-bottom: var(--space-10);
}

.section-title {
  font-size: 22px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0 0 var(--space-6) 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
}

.action-card {
  background: rgba(30, 41, 59, 0.6);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(10px);
  text-decoration: none;
  color: inherit;
  transition: all 0.2s ease;
  border: 1px solid rgba(167, 139, 250, 0.15);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.action-card:hover {
  transform: translateY(-2px);
  border-color: rgba(167, 139, 250, 0.3);
  box-shadow: 0 8px 24px rgba(167, 139, 250, 0.1);
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
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 var(--space-2) 0;
  color: #f1f5f9;
}

.action-card p {
  color: #94a3b8;
  font-size: 14px;
  margin: 0;
  line-height: 1.5;
}

/* ===== RECENT ORDERS ===== */
.recent-orders {
  margin-bottom: var(--space-10);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.view-all-link {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--primary-color);
  text-decoration: none;
  font-weight: var(--font-medium);
  font-size: var(--text-sm);
  transition: color var(--transition-normal);
}

.view-all-link:hover {
  color: var(--primary-dark);
}

.loading-container {
  padding: var(--space-6);
}

.empty-state {
  text-align: center;
  padding: var(--space-16) var(--space-6);
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
}

.empty-icon {
  color: var(--text-muted);
  margin-bottom: var(--space-6);
}

.empty-state h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.empty-state p {
  color: var(--text-secondary);
  margin: 0 0 var(--space-8) 0;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-lg);
  font-weight: var(--font-semibold);
  text-decoration: none;
  transition: all var(--transition-normal);
}

.btn-primary {
  background: var(--primary-gradient);
  color: var(--white);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow);
}

.orders-list {
  background: rgba(30, 41, 59, 0.6);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(10px);
  overflow: hidden;
  border: 1px solid rgba(167, 139, 250, 0.15);
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid rgba(167, 139, 250, 0.1);
  transition: background 0.2s ease;
}

.order-item:hover {
  background: rgba(167, 139, 250, 0.05);
}

.order-item:last-child {
  border-bottom: none;
}

.order-header {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-2);
}

.order-id {
  font-size: 15px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0;
}

.order-date {
  color: #94a3b8;
  font-size: 13px;
}

.order-details {
  display: flex;
  gap: var(--space-6);
}

.order-items {
  color: #94a3b8;
  font-size: 14px;
  margin: 0;
}

.order-total {
  color: #a78bfa;
  font-size: 14px;
  font-weight: 600;
  margin: 0;
}

/* ===== RECENTLY VIEWED ===== */
.recently-viewed {
  margin-bottom: var(--space-10);
}

.clear-btn {
  padding: var(--space-2) var(--space-4);
  background: transparent;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.clear-btn:hover {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: var(--white);
}

.empty-state-mini {
  text-align: center;
  padding: var(--space-12) var(--space-6);
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-xl);
}

.empty-state-mini p {
  color: var(--text-secondary);
  margin-top: var(--space-4);
  margin: 0;
}

.empty-state-mini .empty-icon {
  color: var(--text-muted);
}

.product-image-wrapper {
  position: relative;
}

.remove-btn {
  position: absolute;
  top: var(--space-2);
  right: var(--space-2);
  width: 32px;
  height: 32px;
  background: rgba(0, 0, 0, 0.7);
  border: none;
  border-radius: var(--radius-full);
  color: var(--white);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
  opacity: 0;
}

.product-item:hover .remove-btn {
  opacity: 1;
}

.remove-btn:hover {
  background: var(--error-color);
  transform: scale(1.1);
}

/* ===== RECOMMENDED PRODUCTS ===== */
.recommended-products {
  margin-bottom: var(--space-10);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-6);
}

.product-item {
  background: rgba(30, 41, 59, 0.6);
  border-radius: var(--radius-xl);
  overflow: hidden;
  transition: all 0.2s ease;
  border: 1px solid rgba(167, 139, 250, 0.15);
  text-decoration: none;
  color: inherit;
}

.product-item:hover {
  transform: translateY(-2px);
  border-color: rgba(167, 139, 250, 0.3);
  box-shadow: 0 8px 24px rgba(167, 139, 250, 0.1);
  text-decoration: none;
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.product-info {
  padding: var(--space-4);
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0 0 var(--space-1) 0;
}

.product-brand {
  color: #94a3b8;
  font-size: 13px;
  margin: 0 0 var(--space-2) 0;
}

.product-price {
  font-size: 15px;
  font-weight: 700;
  color: #a78bfa;
  margin: 0;
}

/* ===== RESPONSIVE DESIGN ===== */
@media (max-width: 768px) {
  .user-dashboard {
    padding: var(--space-4) var(--space-3);
  }

  .dashboard-header {
    flex-direction: column;
    text-align: center;
    gap: var(--space-6);
  }

  .welcome-title {
    font-size: var(--text-2xl);
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .actions-grid {
    grid-template-columns: 1fr;
  }

  .order-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-4);
  }

  .order-details {
    flex-direction: column;
    gap: var(--space-1);
  }

  .products-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }
}

/* ===== DARK THEME OVERRIDES ===== */
.user-dashboard {
  background: transparent !important;
}

.welcome-title,
.section-title {
  color: #f1f5f9 !important;
}

.welcome-subtitle {
  color: #94a3b8 !important;
}

.stat-card {
  background: rgba(30, 41, 59, 0.6) !important;
  border: 1px solid rgba(167, 139, 250, 0.15) !important;
  backdrop-filter: blur(10px);
}

.stat-card:hover {
  border-color: rgba(167, 139, 250, 0.3) !important;
}

.stat-number {
  color: #f1f5f9 !important;
}

.stat-label {
  color: #94a3b8 !important;
}

.action-card {
  background: rgba(30, 41, 59, 0.6) !important;
  border: 1px solid rgba(167, 139, 250, 0.15) !important;
}

.action-card:hover {
  background: rgba(30, 41, 59, 0.8) !important;
  border-color: rgba(167, 139, 250, 0.3) !important;
}

.action-card h3 {
  color: #f1f5f9 !important;
}

.action-card p {
  color: #94a3b8 !important;
}

.recent-order {
  background: rgba(30, 41, 59, 0.6) !important;
  border: 1px solid rgba(167, 139, 250, 0.15) !important;
}

.order-id,
.order-date {
  color: #94a3b8 !important;
}

.order-total {
  color: #c4b5fd !important;
}

.product-card {
  background: rgba(30, 41, 59, 0.6) !important;
  border: 1px solid rgba(167, 139, 250, 0.15) !important;
}

.product-card:hover {
  border-color: rgba(167, 139, 250, 0.3) !important;
}

.product-name {
  color: #f1f5f9 !important;
}

.product-price {
  color: #c4b5fd !important;
}
</style>

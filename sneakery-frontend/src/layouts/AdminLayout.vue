<template>
  <div class="admin-layout">
    <!-- Admin Sidebar -->
    <aside class="admin-sidebar" :class="{ 'collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="brand">
          <img src="@/assets/logo.png" alt="Sneakery Store" class="logo" />
          <span v-if="!sidebarCollapsed" class="brand-text">Admin Panel</span>
        </div>
        <button 
          class="sidebar-toggle"
          @click="toggleSidebar"
        >
          <i class="material-icons">{{ sidebarCollapsed ? 'menu' : 'close' }}</i>
        </button>
      </div>

      <nav class="sidebar-nav">
        <ul class="nav-list">
          <li 
            v-for="route in adminRoutes" 
            :key="route.name"
            class="nav-item"
          >
            <router-link 
              :to="route.path" 
              class="nav-link"
              :class="{ 'active': $route.name === route.name }"
            >
              <i class="material-icons">{{ route.meta.icon }}</i>
              <span v-if="!sidebarCollapsed" class="nav-text">{{ route.meta.title }}</span>
            </router-link>
          </li>
        </ul>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info" v-if="!sidebarCollapsed">
          <div class="user-avatar">
            <i class="material-icons">person</i>
          </div>
          <div class="user-details">
            <div class="user-name">{{ adminUser?.email }}</div>
            <div class="user-role">Administrator</div>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <i class="material-icons">logout</i>
          <span v-if="!sidebarCollapsed">Đăng xuất</span>
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="admin-main">
      <!-- Top Header -->
      <header class="admin-header">
        <div class="header-left">
          <button class="mobile-menu-btn" @click="toggleSidebar">
            <i class="material-icons">menu</i>
          </button>
          <h1 class="page-title">{{ currentPageTitle }}</h1>
        </div>
        
        <div class="header-right">
          <div class="header-actions">
            <button class="action-btn" @click="toggleNotifications">
              <i class="material-icons">notifications</i>
              <span v-if="notificationCount > 0" class="notification-badge">{{ notificationCount }}</span>
            </button>
            
            <div class="user-menu">
              <button class="user-menu-btn" @click="toggleUserMenu">
                <div class="user-avatar-small">
                  <i class="material-icons">person</i>
                </div>
                <span class="user-name">{{ adminUser?.email }}</span>
                <i class="material-icons">arrow_drop_down</i>
              </button>
              
              <div v-if="showUserMenu" class="user-menu-dropdown">
                <a href="#" class="menu-item">
                  <i class="material-icons">person</i>
                  Profile
                </a>
                <a href="#" class="menu-item">
                  <i class="material-icons">settings</i>
                  Settings
                </a>
                <hr class="menu-divider">
                <a href="#" class="menu-item" @click="handleLogout">
                  <i class="material-icons">logout</i>
                  Đăng xuất
                </a>
              </div>
            </div>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="admin-content">
        <router-view />
      </main>
    </div>

    <!-- Mobile Overlay -->
    <div 
      v-if="sidebarCollapsed && isMobile" 
      class="mobile-overlay"
      @click="toggleSidebar"
    ></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()

// State
const sidebarCollapsed = ref(false)
const showUserMenu = ref(false)
const notificationCount = ref(0)
const isMobile = ref(false)

// Admin routes for sidebar
const adminRoutes = [
  { path: '/admin/dashboard', name: 'AdminDashboard', meta: { title: 'Dashboard', icon: 'dashboard' } },
  { path: '/admin/products', name: 'AdminProducts', meta: { title: 'Sản phẩm', icon: 'inventory' } },
  { path: '/admin/orders', name: 'AdminOrders', meta: { title: 'Đơn hàng', icon: 'shopping_cart' } },
  { path: '/admin/users', name: 'AdminUsers', meta: { title: 'Người dùng', icon: 'people' } },
  { path: '/admin/brands', name: 'AdminBrands', meta: { title: 'Thương hiệu', icon: 'branding_watermark' } },
  { path: '/admin/categories', name: 'AdminCategories', meta: { title: 'Danh mục', icon: 'category' } },
  { path: '/admin/analytics', name: 'AdminAnalytics', meta: { title: 'Phân tích', icon: 'analytics' } },
  { path: '/admin/settings', name: 'AdminSettings', meta: { title: 'Cài đặt', icon: 'settings' } }
]

// Computed
const currentPageTitle = computed(() => {
  const currentRoute = adminRoutes.find(r => r.name === route.name)
  return currentRoute?.meta.title || 'Admin Panel'
})

const adminUser = computed(() => adminStore.adminUser)

// Methods
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const toggleNotifications = () => {
  // TODO: Implement notifications
  ElMessage.info('Tính năng thông báo sẽ được cập nhật sớm!')
}

const handleLogout = async () => {
  try {
    adminStore.reset()
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('Đăng xuất thành công!')
    router.push('/login')
  } catch (error) {
    ElMessage.error('Có lỗi khi đăng xuất!')
  }
}

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    sidebarCollapsed.value = true
  }
}

// Lifecycle
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  
  // Check admin status
  adminStore.checkAdminStatus()
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})

// Close user menu when clicking outside
document.addEventListener('click', (e) => {
  if (!e.target.closest('.user-menu')) {
    showUserMenu.value = false
  }
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f8fafc;
}

/* ===== SIDEBAR ===== */
.admin-sidebar {
  width: 280px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 1000;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.admin-sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.brand-text {
  font-size: 1.25rem;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-toggle {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.sidebar-toggle:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* ===== NAVIGATION ===== */
.sidebar-nav {
  flex: 1;
  padding: 1rem 0;
  overflow-y: auto;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin: 0.25rem 0;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.5rem;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.2s;
  position: relative;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-link.active {
  background-color: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
  border-right: 3px solid #60a5fa;
}

.nav-link i {
  font-size: 1.25rem;
  min-width: 24px;
}

.nav-text {
  white-space: nowrap;
  font-weight: 500;
}

/* ===== SIDEBAR FOOTER ===== */
.sidebar-footer {
  padding: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-details {
  flex: 1;
}

.user-name {
  font-weight: 500;
  font-size: 0.875rem;
}

.user-role {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.6);
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background-color: rgba(239, 68, 68, 0.2);
  border-color: #ef4444;
}

/* ===== MAIN CONTENT ===== */
.admin-main {
  flex: 1;
  margin-left: 280px;
  transition: margin-left 0.3s ease;
  display: flex;
  flex-direction: column;
}

.admin-sidebar.collapsed + .admin-main {
  margin-left: 80px;
}

/* ===== HEADER ===== */
.admin-header {
  background: white;
  border-bottom: 1px solid #e2e8f0;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.action-btn {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 8px;
  color: #64748b;
  transition: all 0.2s;
}

.action-btn:hover {
  background-color: #f1f5f9;
  color: #1e293b;
}

.notification-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #ef4444;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-menu {
  position: relative;
}

.user-menu-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 8px;
  transition: all 0.2s;
}

.user-menu-btn:hover {
  background-color: #f1f5f9;
}

.user-avatar-small {
  width: 32px;
  height: 32px;
  background-color: #e2e8f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.user-menu-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  min-width: 200px;
  z-index: 1000;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  color: #374151;
  text-decoration: none;
  transition: background-color 0.2s;
}

.menu-item:hover {
  background-color: #f9fafb;
}

.menu-divider {
  border: none;
  border-top: 1px solid #e2e8f0;
  margin: 0.5rem 0;
}

/* ===== CONTENT ===== */
.admin-content {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
}

/* ===== MOBILE OVERLAY ===== */
.mobile-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .admin-sidebar {
    transform: translateX(-100%);
  }
  
  .admin-sidebar:not(.collapsed) {
    transform: translateX(0);
  }
  
  .admin-main {
    margin-left: 0;
  }
  
  .mobile-menu-btn {
    display: block;
  }
  
  .admin-header {
    padding: 1rem;
  }
  
  .admin-content {
    padding: 1rem;
  }
}

@media (max-width: 480px) {
  .admin-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
  
  .header-right {
    width: 100%;
    justify-content: space-between;
  }
}
</style>

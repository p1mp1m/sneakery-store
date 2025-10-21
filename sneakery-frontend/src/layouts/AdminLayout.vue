<template>
  <div class="admin-layout" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <!-- Nút Toggle Sidebar - Fixed Position -->
    <button 
      class="sidebar-toggle-btn"
      @click="toggleSidebar"
      :class="{ 'collapsed': sidebarCollapsed }"
      :title="sidebarCollapsed ? 'Mở rộng sidebar' : 'Thu gọn sidebar'"
    >
      <i class="material-icons">{{ sidebarCollapsed ? 'chevron_right' : 'chevron_left' }}</i>
    </button>

    <!-- Admin Sidebar -->
    <aside class="admin-sidebar" :class="{ 'collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="brand">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo" />
          <span v-if="!sidebarCollapsed" class="brand-text">Admin Panel</span>
        </div>
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
              :title="sidebarCollapsed ? route.meta.title : ''"
            >
              <i class="material-icons">{{ route.meta.icon }}</i>
              <span v-if="!sidebarCollapsed" class="nav-text">{{ route.meta.title }}</span>
            </router-link>
          </li>
        </ul>
      </nav>
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
  position: relative;
  overflow-x: hidden;
}

/* ===== NÚT TOGGLE SIDEBAR ===== */
.sidebar-toggle-btn {
  position: fixed;
  top: 20px;
  left: 240px;
  z-index: 1001;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  transform: scale(1);
}

.sidebar-toggle-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.sidebar-toggle-btn:active {
  transform: scale(0.95);
}

.sidebar-toggle-btn.collapsed {
  left: 70px;
}

.sidebar-toggle-btn i {
  font-size: 20px;
  transition: transform 0.3s ease;
}

.sidebar-toggle-btn:hover i {
  transform: scale(1.2);
}

/* ===== SIDEBAR ===== */
.admin-sidebar {
  width: 260px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 1000;
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.admin-sidebar.collapsed {
  width: 90px;
}

.sidebar-header {
  padding: 0.75rem 0.75rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 56px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.logo {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  transition: transform 0.3s ease;
}

.admin-sidebar.collapsed .logo {
  transform: scale(1.15);
  width: 36px;
  height: 36px;
}

.brand-text {
  font-size: 1rem;
  font-weight: 600;
  white-space: nowrap;
  opacity: 1;
  transition: opacity 0.3s ease;
}

.admin-sidebar.collapsed .brand-text {
  opacity: 0;
}

/* ===== NAVIGATION ===== */
.sidebar-nav {
  flex: 1;
  padding: 0.5rem 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-nav::-webkit-scrollbar {
  width: 3px;
}

.sidebar-nav::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

.sidebar-nav::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin: 0 0.375rem;
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.625rem;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  border-radius: 8px;
  overflow: visible;
  margin: 0;
  border: 1.5px solid transparent;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.nav-link.active {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.25) 0%, rgba(118, 75, 162, 0.15) 100%);
  color: #a78bfa;
  font-weight: 600;
  border-color: rgba(102, 126, 234, 0.5);
}

.nav-link i {
  font-size: 1.125rem;
  min-width: 20px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.nav-link:hover i {
  color: #c4b5fd;
  transform: scale(1.1) rotate(3deg);
  filter: drop-shadow(0 0 6px rgba(167, 139, 250, 0.5));
}

.nav-link.active i {
  color: #a78bfa;
  transform: scale(1.05);
  filter: drop-shadow(0 0 8px rgba(167, 139, 250, 0.7));
}

.nav-text {
  white-space: nowrap;
  font-weight: 500;
  font-size: 0.875rem;
  opacity: 1;
  transition: opacity 0.3s ease;
}

.admin-sidebar.collapsed .nav-text {
  opacity: 0;
  width: 0;
  display: none;
}

.admin-sidebar.collapsed .nav-link {
  justify-content: center;
  padding: 0.625rem 0.5rem;
  gap: 0;
}

.admin-sidebar.collapsed .nav-link i {
  margin: 0 auto;
  font-size: 1.25rem;
  width: 24px;
  height: 24px;
  min-width: 24px;
}

/* ===== MAIN CONTENT ===== */
.admin-main {
  flex: 1;
  margin-left: 260px;
  transition: margin-left 0.4s cubic-bezier(0.4, 0, 0.2, 1), 
              width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  width: calc(100vw - 260px);
  min-width: 0;
  max-width: calc(100vw - 260px);
}

.admin-layout.sidebar-collapsed .admin-main {
  margin-left: 90px;
  width: calc(100vw - 90px);
  max-width: calc(100vw - 90px);
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
  max-width: 100%;
  width: 100%;
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
  .sidebar-toggle-btn {
    display: none;
  }

  .admin-sidebar {
    transform: translateX(-100%);
  }
  
  .admin-sidebar:not(.collapsed) {
    transform: translateX(0);
    width: 280px;
  }
  
  .admin-main {
    margin-left: 0;
    width: 100vw;
    max-width: 100vw;
  }
  
  .admin-layout.sidebar-collapsed .admin-main {
    margin-left: 0;
    width: 100vw;
    max-width: 100vw;
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
  
  .page-title {
    font-size: 1.25rem;
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

  .user-menu-btn .user-name {
    display: none;
  }
}

/* ===== ANIMATIONS ===== */
@keyframes slideIn {
  from {
    transform: translateX(-20px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.nav-item {
  animation: slideIn 0.3s ease-out;
}

.nav-item:nth-child(1) { animation-delay: 0.05s; }
.nav-item:nth-child(2) { animation-delay: 0.1s; }
.nav-item:nth-child(3) { animation-delay: 0.15s; }
.nav-item:nth-child(4) { animation-delay: 0.2s; }
.nav-item:nth-child(5) { animation-delay: 0.25s; }
.nav-item:nth-child(6) { animation-delay: 0.3s; }
.nav-item:nth-child(7) { animation-delay: 0.35s; }
.nav-item:nth-child(8) { animation-delay: 0.4s; }

/* ===== GLOW EFFECT ===== */
@keyframes glow {
  0%, 100% {
    box-shadow: 0 2px 10px rgba(102, 126, 234, 0.25);
  }
  50% {
    box-shadow: 0 2px 16px rgba(102, 126, 234, 0.4);
  }
}

.nav-link.active {
  animation: glow 2s ease-in-out infinite;
}

/* ===== TOOLTIP WHEN COLLAPSED ===== */
.admin-sidebar.collapsed .nav-link {
  position: relative;
}

.admin-sidebar.collapsed .nav-link::after {
  content: attr(title);
  position: absolute;
  left: calc(100% + 8px);
  top: 50%;
  transform: translateY(-50%);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.375rem 0.75rem;
  border-radius: 6px;
  font-size: 0.8125rem;
  font-weight: 500;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 1001;
}

.admin-sidebar.collapsed .nav-link:hover::after {
  opacity: 1;
  left: calc(100% + 12px);
}
</style>

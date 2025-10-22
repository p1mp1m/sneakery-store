<template>
  <div class="admin-layout" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <!-- Admin Sidebar -->
    <aside class="admin-sidebar" :class="{ 'collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="brand">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo" />
        </div>
        <!-- Nút Toggle Sidebar - Trong Sidebar -->
        <button 
          class="sidebar-toggle-btn"
          @click="toggleSidebar"
          type="button"
          :title="sidebarCollapsed ? 'Mở rộng sidebar' : 'Thu gọn sidebar'"
        >
          <i class="material-icons">{{ sidebarCollapsed ? 'chevron_right' : 'chevron_left' }}</i>
        </button>
      </div>

      <nav class="sidebar-nav">
        <ul class="nav-list">
          <template v-for="route in adminRoutes" :key="route.id || route.name">
            <!-- Menu có submenu (dropdown) -->
            <li v-if="route.children" class="nav-item nav-item-parent">
              <a 
                class="nav-link nav-parent"
                :class="{ 'active': isSubmenuActive(route.children), 'open': isMenuOpen(route.id) }"
                @click.prevent="toggleMenu(route.id)"
                href="#"
              >
                <i class="material-icons">{{ route.meta.icon }}</i>
                <span v-if="!sidebarCollapsed" class="nav-text">{{ route.meta.title }}</span>
                <i v-if="!sidebarCollapsed" class="material-icons expand-icon">
                  {{ isMenuOpen(route.id) ? 'expand_less' : 'expand_more' }}
                </i>
              </a>
              
              <!-- Submenu dropdown -->
              <transition name="submenu">
                <ul v-if="isMenuOpen(route.id) && !sidebarCollapsed" class="submenu">
                  <li v-for="child in route.children" :key="child.name" class="submenu-item">
                    <router-link
                      :to="child.path"
                      class="nav-link nav-child"
                      :class="{ 'active': $route.name === child.name }"
                    >
                      <i class="material-icons">{{ child.meta.icon }}</i>
                      <span class="nav-text">{{ child.meta.title }}</span>
                    </router-link>
                  </li>
                </ul>
              </transition>
            </li>

            <!-- Menu thường (không có submenu) -->
            <li v-else class="nav-item">
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
          </template>
        </ul>
      </nav>

      <!-- Thông tin Admin ở dưới cùng -->
      <div class="sidebar-footer" v-if="!sidebarCollapsed">
        <div class="admin-info">
          <div class="admin-avatar">
            <i class="material-icons">account_circle</i>
          </div>
          <div class="admin-details">
            <div class="admin-name">Admin</div>
            <div class="admin-role">QUẢN TRỊ VIÊN</div>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="admin-main">
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
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

const route = useRoute()
const adminStore = useAdminStore()

// State
const sidebarCollapsed = ref(false)
const isMobile = ref(false)
const openMenus = ref([]) // Danh sách các menu đang mở

// Admin routes for sidebar với submenu
const adminRoutes = [
  { 
    path: '/admin/dashboard', 
    name: 'AdminDashboard', 
    meta: { title: 'Trang chủ', icon: 'home' } 
  },
  { 
    path: '/admin/sales', 
    name: 'AdminSales', 
    meta: { title: 'Bán Hàng', icon: 'shopping_cart' } 
  },
  { 
    path: '/admin/orders', 
    name: 'AdminOrders', 
    meta: { title: 'Quản lý hóa đơn', icon: 'receipt' } 
  },
  {
    id: 'products-menu',
    meta: { title: 'Quản lý sản phẩm', icon: 'inventory' },
    children: [
      { path: '/admin/products', name: 'AdminProducts', meta: { title: 'Danh sách sản phẩm', icon: 'list' } },
      { path: '/admin/product-variants', name: 'AdminProductVariants', meta: { title: 'Quản lý biến thể', icon: 'style' } },
      { path: '/admin/categories', name: 'AdminCategories', meta: { title: 'Thuộc tính sản phẩm', icon: 'tune' } }
    ]
  },
  { 
    path: '/admin/users', 
    name: 'AdminUsers', 
    meta: { title: 'Quản lý người dùng', icon: 'people' } 
  },
  { 
    path: '/admin/discounts', 
    name: 'AdminDiscounts', 
    meta: { title: 'Quản lý giảm giá', icon: 'percent' } 
  },
  { 
    path: '/admin/returns', 
    name: 'AdminReturns', 
    meta: { title: 'Quản lý trả hàng', icon: 'assignment_return' } 
  },
  { 
    path: '/admin/warranty', 
    name: 'AdminWarranty', 
    meta: { title: 'Quản lý bảo hành', icon: 'verified_user' } 
  },
  { 
    path: '/admin/analytics', 
    name: 'AdminAnalytics', 
    meta: { title: 'Thống kê', icon: 'analytics' } 
  },
  { 
    path: '/admin/notifications', 
    name: 'AdminNotifications', 
    meta: { title: 'Quản lý thông báo', icon: 'notifications' } 
  },
  { 
    path: '/admin/settings', 
    name: 'AdminSettings', 
    meta: { title: 'Quản lý hệ thống', icon: 'settings' } 
  }
]

// Methods
const toggleSidebar = () => {
  console.log('Toggle sidebar clicked! Current state:', sidebarCollapsed.value)
  sidebarCollapsed.value = !sidebarCollapsed.value
  console.log('New state:', sidebarCollapsed.value)
}

const toggleMenu = (menuId) => {
  if (!menuId) {
    console.error('toggleMenu: menuId is missing!')
    return
  }
  
  console.log('🔄 toggleMenu called for:', menuId)
  console.log('📋 Before toggle - openMenus:', JSON.stringify(openMenus.value))
  
  const index = openMenus.value.indexOf(menuId)
  if (index > -1) {
    // Đóng menu
    openMenus.value.splice(index, 1)
    console.log('❌ Menu closed:', menuId)
  } else {
    // Mở menu
    openMenus.value.push(menuId)
    console.log('✅ Menu opened:', menuId)
  }
  
  console.log('📋 After toggle - openMenus:', JSON.stringify(openMenus.value))
  console.log('🎯 isMenuOpen result:', isMenuOpen(menuId))
}

const isMenuOpen = (menuId) => {
  if (!menuId) return false
  const isOpen = openMenus.value.includes(menuId)
  return isOpen
}

// Kiểm tra xem route hiện tại có nằm trong submenu không
const isSubmenuActive = (children) => {
  if (!children) return false
  return children.some(child => child.name === route.name)
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
  
  console.log('🚀 AdminLayout mounted')
  console.log('📋 Admin routes:', adminRoutes)
  console.log('📍 Current route:', route.name)
  
  // Tự động mở menu nếu route hiện tại nằm trong submenu
  adminRoutes.forEach(menuItem => {
    if (menuItem.children) {
      console.log(`🔍 Checking menu: ${menuItem.id}`, menuItem.children)
      if (isSubmenuActive(menuItem.children)) {
        if (!openMenus.value.includes(menuItem.id)) {
          openMenus.value.push(menuItem.id)
          console.log('✅ Auto-opened menu:', menuItem.id)
        }
      }
    }
  })
  
  console.log('📊 Final openMenus:', JSON.stringify(openMenus.value))
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
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
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.8), rgba(118, 75, 162, 0.8));
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 8px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.sidebar-toggle-btn:hover {
  background: linear-gradient(135deg, rgba(118, 75, 162, 0.9), rgba(102, 126, 234, 0.9));
  border-color: rgba(255, 255, 255, 0.6);
  transform: translateY(-50%) scale(1.08);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.sidebar-toggle-btn:active {
  transform: translateY(-50%) scale(0.95);
}

.sidebar-toggle-btn:focus {
  outline: 2px solid rgba(102, 126, 234, 0.6);
  outline-offset: 2px;
}

.sidebar-toggle-btn i {
  font-size: 20px;
  transition: transform 0.3s ease;
  line-height: 1;
  pointer-events: none;
}

.sidebar-toggle-btn:hover i {
  transform: scale(1.1);
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
  padding: 1rem 0.75rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 80px;
  position: relative;
  z-index: 5;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  transition: all 0.3s ease;
  flex: 1;
}

.logo {
  width: 200px;
  height: 100px;
  border-radius: 10px;
  transition: all 0.3s ease;
  object-fit: contain;
}

.admin-sidebar.collapsed .logo {
  width: 48px;
  height: 48px;
}

.admin-sidebar.collapsed .sidebar-header {
  justify-content: center;
  padding: 1rem 0.5rem;
}

.admin-sidebar.collapsed .brand {
  justify-content: center;
}

.admin-sidebar.collapsed .sidebar-toggle-btn {
  position: absolute;
  bottom: 16px;
  right: 50%;
  transform: translateX(50%);
  top: auto;
}

.admin-sidebar.collapsed .sidebar-toggle-btn:hover {
  transform: translateX(50%) scale(1.08);
}

.admin-sidebar.collapsed .sidebar-toggle-btn:active {
  transform: translateX(50%) scale(0.95);
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

.nav-item-parent {
  margin: 0 0.375rem;
  position: relative;
  display: flex;
  flex-direction: column;
  width: calc(100% - 0.75rem);
  overflow: visible;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.625rem 0.75rem;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  border-radius: 8px;
  overflow: visible;
  margin: 0.125rem 0;
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

/* ===== SIDEBAR FOOTER ===== */
.sidebar-footer {
  padding: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.admin-info:hover {
  background: rgba(255, 255, 255, 0.05);
}

.admin-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.admin-avatar i {
  font-size: 28px;
  color: white;
}

.admin-details {
  flex: 1;
  min-width: 0;
}

.admin-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: white;
  margin-bottom: 0.125rem;
}

.admin-role {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 500;
  letter-spacing: 0.5px;
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

/* ===== CONTENT ===== */
.admin-content {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
  max-width: 100%;
  width: 100%;
  position: relative;
  z-index: 1; /* Đảm bảo content ở dưới nút toggle */
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
  
  .admin-content {
    padding: 1rem;
  }
  
  .sidebar-header {
    min-height: 70px;
  }
  
  .logo {
    width: 48px;
    height: 48px;
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
.nav-item:nth-child(9) { animation-delay: 0.45s; }
.nav-item:nth-child(10) { animation-delay: 0.5s; }
.nav-item:nth-child(11) { animation-delay: 0.55s; }
.nav-item:nth-child(12) { animation-delay: 0.6s; }
.nav-item:nth-child(13) { animation-delay: 0.65s; }

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

/* ===== DROPDOWN MENU (SUBMENU) - THIẾT KẾ ĐẸP ===== */
.nav-parent {
  cursor: pointer !important;
  position: relative;
  user-select: none;
}

.expand-icon {
  margin-left: auto;
  font-size: 1.125rem !important;
  transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  width: 20px !important;
  min-width: 20px !important;
  height: 20px !important;
  color: rgba(255, 255, 255, 0.6);
  flex-shrink: 0;
}

.nav-parent:hover .expand-icon {
  color: rgba(255, 255, 255, 0.9);
}

.nav-parent.open .expand-icon {
  transform: rotate(180deg);
  color: #a78bfa;
}

/* Submenu container - Đẹp và hiện đại */
.submenu {
  list-style: none;
  padding: 0.625rem 0.375rem;
  margin: 0.25rem 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.35) 0%, rgba(0, 0, 0, 0.25) 100%);
  border-radius: 10px;
  border: 1px solid rgba(167, 139, 250, 0.2);
  border-left: 3px solid rgba(167, 139, 250, 0.5);
  display: flex !important;
  flex-direction: column !important;
  width: 100%;
  overflow: visible;
  box-shadow: 
    inset 0 2px 8px rgba(0, 0, 0, 0.3),
    0 2px 8px rgba(0, 0, 0, 0.15);
}

.submenu-item {
  margin: 0;
  padding: 0;
  display: block !important;
  width: 100%;
  list-style: none;
}

/* Menu con - Design mới đẹp hơn */
.nav-child {
  display: flex !important;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.625rem 0.5rem 1.875rem !important;
  font-size: 0.8125rem;
  color: rgba(255, 255, 255, 0.85);
  background: transparent;
  border: 1.5px solid transparent;
  border-radius: 8px;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  text-decoration: none;
  margin: 0.1875rem 0.25rem;
  width: calc(100% - 0.5rem);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Dot indicator trước mỗi submenu item */
.nav-child::before {
  content: '';
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 5px;
  background: rgba(255, 255, 255, 0.35);
  border-radius: 50%;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Hover state - Đẹp và mượt */
.nav-child:hover {
  background: linear-gradient(90deg, rgba(167, 139, 250, 0.2) 0%, rgba(102, 126, 234, 0.12) 100%);
  border-color: rgba(167, 139, 250, 0.35);
  color: white;
  padding-left: 2rem !important;
  box-shadow: 0 2px 8px rgba(167, 139, 250, 0.2);
}

.nav-child:hover::before {
  background: linear-gradient(135deg, #a78bfa 0%, #c4b5fd 100%);
  width: 6px;
  height: 6px;
  left: 0.875rem;
  box-shadow: 0 0 12px rgba(167, 139, 250, 0.7);
}

/* Active state - Nổi bật */
.nav-child.active {
  background: linear-gradient(90deg, rgba(167, 139, 250, 0.3) 0%, rgba(102, 126, 234, 0.2) 100%);
  border-color: rgba(167, 139, 250, 0.6);
  color: #e9d5ff;
  font-weight: 600;
  box-shadow: 
    0 2px 10px rgba(167, 139, 250, 0.25),
    inset 0 1px 2px rgba(255, 255, 255, 0.15);
}

.nav-child.active::before {
  background: linear-gradient(135deg, #e9d5ff 0%, #c4b5fd 100%);
  width: 7px;
  height: 7px;
  box-shadow: 
    0 0 14px rgba(167, 139, 250, 0.9),
    0 0 24px rgba(167, 139, 250, 0.5);
}

/* Icons trong submenu */
.nav-child .material-icons {
  font-size: 1rem !important;
  width: 18px !important;
  min-width: 18px !important;
  height: 18px !important;
  color: rgba(255, 255, 255, 0.75);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-child:hover .material-icons {
  color: #c4b5fd;
  transform: scale(1.15);
}

.nav-child.active .material-icons {
  color: #e9d5ff;
  transform: scale(1.1);
  filter: drop-shadow(0 0 6px rgba(167, 139, 250, 0.7));
}

/* Text trong submenu */
.nav-child .nav-text {
  font-size: 0.8125rem;
  font-weight: 500;
  letter-spacing: 0.015em;
  transition: all 0.3s ease;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
}

.nav-child:hover .nav-text {
  letter-spacing: 0.025em;
}

.nav-child.active .nav-text {
  font-weight: 600;
  letter-spacing: 0.03em;
}

/* Transition animation cho submenu - Mượt mà */
.submenu-enter-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.submenu-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.submenu-enter-from {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
  margin-top: 0;
  margin-bottom: 0;
  transform: translateY(-8px);
}

.submenu-enter-to {
  opacity: 1;
  max-height: 500px;
  transform: translateY(0);
}

.submenu-leave-from {
  opacity: 1;
  max-height: 500px;
  transform: translateY(0);
}

.submenu-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
  margin-top: 0;
  margin-bottom: 0;
  transform: translateY(-8px);
}

/* Parent active state */
.nav-parent.active {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-color: rgba(102, 126, 234, 0.4);
}

.nav-parent.active .material-icons:first-child {
  color: #c4b5fd;
}

/* Responsive cho submenu */
.admin-sidebar.collapsed .submenu {
  display: none;
}

.admin-sidebar.collapsed .nav-parent .expand-icon {
  display: none;
}
</style>

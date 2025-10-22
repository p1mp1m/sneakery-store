<template>
  <div class="admin-layout" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <!-- Admin Sidebar -->
    <aside class="admin-sidebar" :class="{ 'collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="brand">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo" />
        </div>
      </div>

      <!-- Nút Toggle - Ngoài header -->
      <button 
        class="sidebar-toggle-btn"
        @click="toggleSidebar"
        type="button"
        :title="sidebarCollapsed ? 'Mở rộng sidebar' : 'Thu gọn sidebar'"
      >
        <i class="material-icons">{{ sidebarCollapsed ? 'chevron_right' : 'chevron_left' }}</i>
      </button>

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
  background: linear-gradient(180deg, var(--dark-bg-primary) 0%, var(--dark-bg-secondary) 100%);
  position: relative;
  overflow-x: hidden;
}

/* ===== NÚT TOGGLE SIDEBAR ===== */
.sidebar-toggle-btn {
  position: absolute;
  top: 100px;
  right: 12px;
  width: 36px;
  height: 36px;
  background: var(--primary-gradient);
  border: 2px solid var(--primary-color);
  border-radius: 50%;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 200;
  box-shadow: 
    0 4px 16px rgba(167, 139, 250, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.sidebar-toggle-btn:hover {
  background: var(--primary-gradient-hover);
  transform: scale(1.1);
  box-shadow: var(--shadow-glow-purple);
}

.sidebar-toggle-btn:active {
  transform: scale(0.95);
}

.sidebar-toggle-btn:focus {
  outline: none;
  box-shadow: 
    var(--shadow-glow-purple),
    0 0 0 3px rgba(167, 139, 250, 0.3);
}

.sidebar-toggle-btn i {
  font-size: 20px;
  transition: transform 0.3s ease;
  line-height: 1;
}

.sidebar-toggle-btn:hover i {
  transform: scale(1.1);
}

/* ===== SIDEBAR ===== */
.admin-sidebar {
  width: 260px;
  background: var(--dark-bg-card);
  color: var(--dark-text-primary);
  display: flex;
  flex-direction: column;
  transition: all var(--transition-slow);
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 1000;
  box-shadow: var(--shadow-glass-lg);
  overflow: hidden;
  border-right: 1px solid var(--dark-border-color);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
}

.admin-sidebar.collapsed {
  width: 90px;
}

.sidebar-header {
  padding: 1rem 0.75rem;
  border-bottom: 1px solid var(--dark-border-color);
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
  display: none;
}

.admin-sidebar.collapsed .brand {
  display: none;
}

.admin-sidebar.collapsed .sidebar-header {
  justify-content: center;
  padding: 0.75rem 0.5rem;
  min-height: auto;
}

/* Khi collapsed - nút ở cuối trang, giữa sidebar */
.admin-sidebar.collapsed .sidebar-toggle-btn {
  top: auto;
  bottom: 20px;
  right: 50%;
  transform: translateX(50%);
}

.admin-sidebar.collapsed .sidebar-toggle-btn:hover {
  transform: translateX(50%) scale(1.1);
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
  background: rgba(167, 139, 250, 0.05);
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-sm);
}

.sidebar-nav::-webkit-scrollbar-thumb:hover {
  background: rgba(167, 139, 250, 0.3);
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
  color: var(--dark-text-secondary);
  text-decoration: none;
  transition: all var(--transition-normal);
  position: relative;
  border-radius: var(--radius-md);
  overflow: visible;
  margin: 0.125rem 0;
  border: 1.5px solid transparent;
}

.nav-link:hover {
  background: rgba(167, 139, 250, 0.1);
  color: var(--dark-text-primary);
  border-color: var(--dark-border-light);
  box-shadow: var(--shadow-glass-sm);
}

.nav-link.active {
  background: var(--gradient-purple-soft);
  color: var(--primary-light);
  font-weight: 600;
  border-color: var(--dark-border-medium);
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
  color: var(--primary-light);
  transform: scale(1.1) rotate(3deg);
  filter: drop-shadow(0 0 6px rgba(167, 139, 250, 0.5));
}

.nav-link.active i {
  color: var(--primary-color);
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
  border-top: 1px solid var(--dark-border-color);
  background: rgba(15, 23, 42, 0.4);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.admin-info:hover {
  background: rgba(167, 139, 250, 0.1);
}

.admin-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--primary-gradient);
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
  color: var(--dark-text-primary);
  margin-bottom: 0.125rem;
}

.admin-role {
  font-size: 0.7rem;
  color: var(--dark-text-tertiary);
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
  padding: 1.25rem; /* Giảm từ 1.5rem → 1.25rem (20px) để gọn gàng hơn */
  overflow-y: auto;
  max-width: 100%;
  width: 100%;
  position: relative;
  /* Bỏ z-index để cards bên trong có thể hover đúng */
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
    padding: 0.875rem; /* Mobile: 14px */
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
  background: var(--primary-gradient);
  color: white;
  padding: 0.375rem 0.75rem;
  border-radius: var(--radius-md);
  font-size: 0.8125rem;
  font-weight: 500;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-glass-md);
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
  color: var(--dark-text-tertiary);
  flex-shrink: 0;
}

.nav-parent:hover .expand-icon {
  color: var(--dark-text-primary);
}

.nav-parent.open .expand-icon {
  transform: rotate(180deg);
  color: var(--primary-color);
}

/* Submenu container - Đẹp và hiện đại */
.submenu {
  list-style: none;
  padding: 0.625rem 0.375rem;
  margin: 0.25rem 0;
  background: var(--dark-bg-glass-dark);
  border-radius: var(--radius-lg);
  border: 1px solid var(--dark-border-light);
  border-left: 3px solid var(--primary-color);
  display: flex !important;
  flex-direction: column !important;
  width: 100%;
  overflow: visible;
  box-shadow: var(--shadow-glass-sm);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
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
  color: var(--dark-text-secondary);
  background: transparent;
  border: 1.5px solid transparent;
  border-radius: var(--radius-md);
  position: relative;
  transition: all var(--transition-normal);
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
  background: var(--dark-text-quaternary);
  border-radius: 50%;
  transition: all var(--transition-normal);
}

/* Hover state - Đẹp và mượt */
.nav-child:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--dark-border-light);
  color: var(--dark-text-primary);
  padding-left: 2rem !important;
  box-shadow: var(--shadow-glass-sm);
}

.nav-child:hover::before {
  background: var(--primary-gradient);
  width: 6px;
  height: 6px;
  left: 0.875rem;
  box-shadow: 0 0 12px rgba(167, 139, 250, 0.7);
}

/* Active state - Nổi bật */
.nav-child.active {
  background: var(--gradient-purple-soft);
  border-color: var(--dark-border-medium);
  color: var(--primary-light);
  font-weight: 600;
  box-shadow: var(--shadow-glass-md);
}

.nav-child.active::before {
  background: var(--primary-gradient);
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
  color: var(--dark-text-tertiary);
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-child:hover .material-icons {
  color: var(--primary-light);
  transform: scale(1.15);
}

.nav-child.active .material-icons {
  color: var(--primary-light);
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
  background: var(--gradient-purple-soft);
  border-color: var(--dark-border-medium);
}

.nav-parent.active .material-icons:first-child {
  color: var(--primary-light);
}

/* Responsive cho submenu */
.admin-sidebar.collapsed .submenu {
  display: none;
}

.admin-sidebar.collapsed .nav-parent .expand-icon {
  display: none;
}
</style>

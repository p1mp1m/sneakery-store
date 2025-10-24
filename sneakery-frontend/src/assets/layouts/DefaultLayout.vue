<template>
  <div id="app-layout" class="app-layout">
    <!-- Navigation Header -->
    <header class="navbar" :class="{ 'navbar-scrolled': isScrolled }">
      <div class="navbar-container">
        <!-- Logo/Brand -->
        <router-link to="/home" class="navbar-brand">
          <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
        </router-link>

        <!-- Desktop Navigation -->
        <nav class="nav-links desktop-nav">
          <router-link to="/home" class="nav-link" active-class="nav-link-active">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M9 22V12H15V22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>Trang Chủ</span>
          </router-link>
          
          <router-link to="/home/products" class="nav-link" active-class="nav-link-active">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M3 6H21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 10C16 11.0609 15.5786 12.0783 14.8284 12.8284C14.0783 13.5786 13.0609 14 12 14C10.9391 14 9.92172 13.5786 9.17157 12.8284C8.42143 12.0783 8 11.0609 8 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>Sản Phẩm</span>
          </router-link>
        </nav>

        <!-- Action Icons -->
        <div class="nav-actions" v-if="authStore.isAuthenticated">
          <!-- Wishlist Icon -->
          <router-link to="/wishlist" class="nav-icon" :class="{ 'active': $route.path === '/wishlist' }">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" stroke="currentColor" stroke-width="2" fill="none"/>
            </svg>
            <span class="badge" v-if="wishlistStore.wishlistCount > 0">{{ wishlistStore.wishlistCount }}</span>
          </router-link>

          <!-- Notification Dropdown -->
          <NotificationDropdown />

          <!-- Cart Icon -->
          <router-link to="/cart" class="nav-icon" :class="{ 'active': $route.path === '/cart' }">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 2L7 6H21L19 2H9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M7 6V20C7 20.5304 7.21071 21.0391 7.58579 21.4142C7.96086 21.7893 8.46957 22 9 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6H7Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span class="badge" v-if="false">0</span>
          </router-link>
        </div>

        <nav class="nav-links-user desktop-nav">
          <template v-if="authStore.isAuthenticated">
            <!-- Admin Panel Link -->
            <router-link v-if="isAdmin" to="/admin/dashboard" class="nav-link admin-link" active-class="nav-link-active">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Admin Panel</span>
            </router-link>
            
            <div class="user-menu">
              <button class="user-button" @click="toggleUserMenu">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>{{ authStore.currentUser?.fullName || 'Tài khoản' }}</span>
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="dropdown-arrow">
                  <path d="M6 9L12 15L18 9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
              
              <div class="user-dropdown" :class="{ 'show': showUserMenu }">
                <router-link to="/user/dashboard" class="dropdown-item" @click="closeUserMenu">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <rect x="14" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <rect x="14" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <rect x="3" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Dashboard
                </router-link>
                <router-link to="/user/profile" class="dropdown-item" @click="closeUserMenu">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Thông tin cá nhân
                </router-link>
                <router-link to="/user/orders" class="dropdown-item" @click="closeUserMenu">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Đơn hàng của tôi
                </router-link>
                <div class="dropdown-divider"></div>
                <a href="#" class="dropdown-item" @click.prevent="handleLogout">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <polyline points="16,17 21,12 16,7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Đăng xuất
                </a>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="auth-buttons">
              <router-link to="/login" class="btn btn-outline">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M15 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <polyline points="10,17 15,12 10,7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="15" y1="12" x2="3" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                Đăng nhập
              </router-link>
              <router-link to="/register" class="btn btn-primary">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16 21V19C16 17.9391 15.5786 16.9217 14.8284 16.1716C14.0783 15.4214 13.0609 15 12 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="8.5" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="20" y1="8" x2="20" y2="14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="17" y1="11" x2="23" y2="11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                Đăng ký
              </router-link>
            </div>
          </template>
        </nav>

        <!-- Mobile Menu Button -->
        <button class="mobile-menu-btn" @click="toggleMobileMenu" :class="{ 'active': showMobileMenu }">
          <span class="hamburger-line"></span>
          <span class="hamburger-line"></span>
          <span class="hamburger-line"></span>
        </button>
      </div>

      <!-- Mobile Navigation -->
      <nav class="mobile-nav" :class="{ 'show': showMobileMenu }">
        <div class="mobile-nav-content">
          <router-link to="/home" class="mobile-nav-link" @click="closeMobileMenu">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>Trang Chủ</span>
          </router-link>
          
          <router-link to="/home/products" class="mobile-nav-link" @click="closeMobileMenu">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>Sản Phẩm</span>
          </router-link>

          <template v-if="authStore.isAuthenticated">
            <router-link to="/user/dashboard" class="mobile-nav-link" @click="closeMobileMenu">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <rect x="14" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <rect x="14" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <rect x="3" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Dashboard</span>
            </router-link>
            <router-link to="/user/profile" class="mobile-nav-link" @click="closeMobileMenu">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Thông tin cá nhân</span>
            </router-link>
            <router-link to="/user/orders" class="mobile-nav-link" @click="closeMobileMenu">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Đơn hàng của tôi</span>
            </router-link>
            <a href="#" class="mobile-nav-link" @click.prevent="handleLogout">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="16,17 21,12 16,7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="21" y1="12" x2="9" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Đăng xuất</span>
            </a>
          </template>
          <template v-else>
            <router-link to="/login" class="mobile-nav-link" @click="closeMobileMenu">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M15 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Đăng nhập</span>
            </router-link>
            <router-link to="/register" class="mobile-nav-link" @click="closeMobileMenu">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 21V19C16 17.9391 15.5786 16.9217 14.8284 16.1716C14.0783 15.4214 13.0609 15 12 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="8.5" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Đăng ký</span>
            </router-link>
          </template>
        </div>
      </nav>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-brand">
            <img src="@/assets/images/logo.png" alt="Sneakery Store" class="logo-image" />
            <p class="footer-description">
              Cửa hàng giày sneaker hàng đầu với những sản phẩm chất lượng cao và thiết kế độc đáo.
            </p>
          </div>
          
          <div class="footer-links">
            <div class="footer-column">
              <h4>Liên kết</h4>
              <ul>
                <li><router-link to="/home">Trang chủ</router-link></li>
                <li><router-link to="/home/products">Sản phẩm</router-link></li>
                <li><a href="#">Về chúng tôi</a></li>
                <li><a href="#">Liên hệ</a></li>
              </ul>
            </div>
            
            <div class="footer-column">
              <h4>Hỗ trợ</h4>
              <ul>
                <li><a href="#">Trung tâm trợ giúp</a></li>
                <li><a href="#">Chính sách đổi trả</a></li>
                <li><a href="#">Vận chuyển</a></li>
                <li><a href="#">Bảo hành</a></li>
              </ul>
            </div>
            
            <div class="footer-column">
              <h4>Kết nối</h4>
              <div class="social-links">
                <a href="#" class="social-link">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M18 2H15C13.6739 2 12.4021 2.52678 11.4645 3.46447C10.5268 4.40215 10 5.67392 10 7V10H7V14H10V22H14V14H17L18 10H14V7C14 6.73478 14.1054 6.48043 14.2929 6.29289C14.4804 6.10536 14.7348 6 15 6H18V2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </a>
                <a href="#" class="social-link">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M23 3C22.0424 3.67548 20.9821 4.19211 19.86 4.53C19.2577 3.83751 18.4573 3.34669 17.567 3.12393C16.6767 2.90116 15.7395 2.95718 14.8821 3.28445C14.0247 3.61173 13.2884 4.19439 12.773 4.95372C12.2575 5.71305 11.9877 6.61232 12 7.53V8.53C10.2426 8.57557 8.50127 8.18581 6.93101 7.39525C5.36074 6.60469 4.01032 5.43866 3 4C3 4 -1 13 8 17C5.94053 18.398 3.48716 19.099 1 19C10 24 21 19 21 7.5C20.9991 7.22145 20.9723 6.94359 20.92 6.67C21.9406 5.66349 22.6608 4.39271 23 3V3Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </a>
                <a href="#" class="social-link">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect x="2" y="2" width="20" height="20" rx="5" ry="5" stroke="currentColor" stroke-width="2"/>
                    <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37Z" stroke="currentColor" stroke-width="2"/>
                    <line x1="17.5" y1="6.5" x2="17.51" y2="6.5" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </a>
              </div>
            </div>
          </div>
        </div>
        
        <div class="footer-bottom">
          <p>&copy; 2024 Sneakery Store. Tất cả quyền được bảo lưu.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useAdminStore } from '@/stores/admin';
import { useWishlistStore } from '@/stores/wishlist';
import { useRouter } from 'vue-router';
import NotificationDropdown from '@/assets/components/common/NotificationDropdown.vue';

const authStore = useAuthStore();
const adminStore = useAdminStore();
const wishlistStore = useWishlistStore();
const router = useRouter();

// Reactive state
const isScrolled = ref(false);
const showMobileMenu = ref(false);
const showUserMenu = ref(false);

// Computed
const isAdmin = computed(() => {
  if (!authStore.isAuthenticated) return false;
  
  // Check if user has admin role from JWT token
  const token = localStorage.getItem('token');
  if (!token) return false;
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.role === 'ADMIN';
  } catch (error) {
    return false;
  }
});

// Scroll handler
const handleScroll = () => {
  isScrolled.value = window.scrollY > 20;
};

// Mobile menu handlers
const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value;
  showUserMenu.value = false; // Close user menu when opening mobile menu
};

const closeMobileMenu = () => {
  showMobileMenu.value = false;
};

// User menu handlers
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
  showMobileMenu.value = false; // Close mobile menu when opening user menu
};

const closeUserMenu = () => {
  showUserMenu.value = false;
};

// Logout handler
const handleLogout = () => {
  authStore.logout();
  router.push('/login');
  showUserMenu.value = false;
  showMobileMenu.value = false;
};

// Close menus when clicking outside
const handleClickOutside = (event) => {
  if (!event.target.closest('.user-menu')) {
    showUserMenu.value = false;
  }
  if (!event.target.closest('.mobile-menu-btn') && !event.target.closest('.mobile-nav')) {
    showMobileMenu.value = false;
  }
};

// Lifecycle hooks
onMounted(async () => {
  window.addEventListener('scroll', handleScroll);
  document.addEventListener('click', handleClickOutside);
  
  // Load wishlist count nếu user đã đăng nhập
  if (authStore.isAuthenticated) {
    try {
      await wishlistStore.fetchWishlist();
    } catch (error) {
      console.error('Error loading wishlist:', error);
    }
  }
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
/* Using global CSS variables from main.css */

/* ===== APP LAYOUT ===== */
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-primary);
}

/* ===== NAVBAR ===== */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: var(--z-fixed);
  background: rgba(15, 23, 42, 0.95);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all var(--transition-normal);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.navbar-scrolled {
  background: rgba(15, 23, 42, 0.95);
  box-shadow: var(--shadow-xl);
  border-bottom-color: rgba(255, 255, 255, 0.1);
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-4);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

/* ===== BRAND ===== */
.navbar-brand {
  display: flex;
  align-items: center;
  text-decoration: none;
  transition: transform var(--transition-fast);
  border: none;
  background: none;
  padding: 0;
}

.navbar-brand .logo-image {
  width: 120px;
  height: 120px;
  object-fit: contain;
  transition: transform var(--transition-fast);
}

.navbar-brand:hover .logo-image {
  transform: scale(1.1);
}

/* ===== DESKTOP NAVIGATION ===== */
.desktop-nav {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

/* Nav Actions (Wishlist, Cart) */
.nav-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin: 0 var(--space-4);
}

.nav-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  color: var(--text-secondary);
  background: transparent;
  transition: all 0.3s ease;
  cursor: pointer;
}

.nav-icon:hover {
  background: rgba(167, 139, 250, 0.1);
  color: var(--color-primary);
  transform: scale(1.1);
}

.nav-icon.active {
  background: rgba(167, 139, 250, 0.15);
  color: var(--color-primary);
}

.nav-icon .badge {
  position: absolute;
  top: 2px;
  right: 2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--color-error);
  color: white;
  font-size: 11px;
  font-weight: 600;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  color: var(--white);
  text-decoration: none;
  font-weight: var(--font-medium);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
  position: relative;
}

.nav-link:hover {
  color: var(--primary-color);
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-link-active {
  color: var(--primary-color);
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-link.admin-link {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  font-weight: 600;
}

.nav-link.admin-link:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(245, 158, 11, 0.3);
}

.nav-link svg {
  flex-shrink: 0;
}

/* ===== AUTH BUTTONS ===== */
.auth-buttons {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-xl);
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  text-decoration: none;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all var(--transition-normal);
  white-space: nowrap;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left var(--transition-slow);
}

.btn:hover::before {
  left: 100%;
}

.btn-primary {
  background: var(--primary-gradient);
  color: var(--white);
  border-color: transparent;
  box-shadow: var(--shadow-md);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow);
  color: var(--white);
}

.btn-outline {
  background-color: transparent;
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.btn-outline:hover {
  background: rgba(255, 255, 255, 0.1);
  color: var(--white);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* ===== USER MENU ===== */
.user-menu {
  position: relative;
}

.user-button {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: none;
  border: none;
  border-radius: var(--radius-md);
  color: var(--white);
  font-weight: var(--font-medium);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.user-button:hover {
  color: var(--primary-color);
  background-color: rgba(255, 255, 255, 0.1);
}

.dropdown-arrow {
  transition: transform var(--transition-fast);
}

.user-menu:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + var(--space-1));
  right: 0;
  background: rgba(30, 41, 59, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-lg);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.5);
  min-width: 200px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all var(--transition-fast);
  z-index: var(--z-dropdown);
}

.user-dropdown.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  color: #94a3b8;
  text-decoration: none;
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
  border-radius: var(--radius-md);
  margin: var(--space-1);
}

.dropdown-item:hover {
  color: #f1f5f9;
  background: rgba(167, 139, 250, 0.15);
}

.dropdown-divider {
  height: 1px;
  background: rgba(167, 139, 250, 0.2);
  margin: var(--space-2) var(--space-4);
}

/* ===== MOBILE MENU BUTTON ===== */
.mobile-menu-btn {
  display: none;
  flex-direction: column;
  gap: 4px;
  background: none;
  border: none;
  padding: var(--space-2);
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: background-color var(--transition-fast);
}

.mobile-menu-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.hamburger-line {
  width: 24px;
  height: 2px;
  background-color: var(--white);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.mobile-menu-btn.active .hamburger-line:nth-child(1) {
  transform: rotate(45deg) translate(6px, 6px);
}

.mobile-menu-btn.active .hamburger-line:nth-child(2) {
  opacity: 0;
}

.mobile-menu-btn.active .hamburger-line:nth-child(3) {
  transform: rotate(-45deg) translate(6px, -6px);
}

/* ===== MOBILE NAVIGATION ===== */
.mobile-nav {
  position: fixed;
  top: 70px;
  left: 0;
  right: 0;
  background-color: rgba(15, 23, 42, 0.95);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: var(--shadow-lg);
  transform: translateY(-100%);
  opacity: 0;
  visibility: hidden;
  transition: all var(--transition-normal);
  z-index: var(--z-fixed);
  backdrop-filter: blur(20px);
}

.mobile-nav.show {
  transform: translateY(0);
  opacity: 1;
  visibility: visible;
}

.mobile-nav-content {
  padding: var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  color: var(--white);
  text-decoration: none;
  font-weight: var(--font-medium);
  transition: all var(--transition-fast);
}

.mobile-nav-link:hover {
  color: var(--primary-color);
  background-color: rgba(255, 255, 255, 0.1);
}

.mobile-nav-link.router-link-active {
  color: var(--primary-color);
  background-color: rgba(255, 255, 255, 0.1);
}

/* ===== MAIN CONTENT ===== */
.main-content {
  flex: 1;
  min-height: calc(100vh - 70px);
}

/* ===== FOOTER - Dark ===== */
.footer {
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.8) 0%, rgba(15, 23, 42, 0.95) 100%);
  border-top: 1px solid rgba(167, 139, 250, 0.2);
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-12) var(--space-4) var(--space-6);
}

.footer-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: var(--space-12);
  margin-bottom: var(--space-8);
}

.footer-brand {
  max-width: 400px;
}

.footer-brand .logo-image {
  width: 120px;
  height: 120px;
  object-fit: contain;
  margin-bottom: var(--space-4);
}

.footer-description {
  color: #94a3b8;
  line-height: var(--leading-relaxed);
  margin: 0;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-8);
}

.footer-column h4 {
  color: #f1f5f9;
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-4);
}

.footer-column ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-column li {
  margin-bottom: var(--space-2);
}

.footer-column a {
  color: #94a3b8;
  text-decoration: none;
  transition: color var(--transition-fast);
}

.footer-column a:hover {
  color: #c4b5fd;
}

.social-links {
  display: flex;
  gap: var(--space-3);
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-lg);
  color: #94a3b8;
  text-decoration: none;
  transition: all var(--transition-fast);
}

.social-link:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: var(--white);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.footer-bottom {
  padding-top: var(--space-6);
  border-top: 1px solid rgba(167, 139, 250, 0.2);
  text-align: center;
}

.footer-bottom p {
  color: #64748b;
  font-size: var(--text-sm);
  margin: 0;
}

/* ===== RESPONSIVE DESIGN ===== */
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }
  
  .mobile-menu-btn {
    display: flex;
  }
  
  .navbar-container {
    padding: 0 var(--space-3);
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: var(--space-8);
  }
  
  .footer-links {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }
  
  .footer-container {
    padding: var(--space-8) var(--space-3) var(--space-6);
  }
}

@media (max-width: 480px) {
  .navbar-container {
    height: 60px;
  }
  
  .main-content {
    margin-top: 60px;
    min-height: calc(100vh - 60px);
  }
  
  .mobile-nav {
    top: 60px;
  }
  
  .navbar-brand .logo-image {
    width: 80px;
    height: 80px;
  }
  
  .footer-brand .logo-image {
    width: 100px;
    height: 100px;
  }
  
  .footer-links {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }
}

/* ===== DARK MODE SUPPORT ===== */
@media (prefers-color-scheme: dark) {
  .navbar-scrolled {
    background-color: rgba(15, 23, 42, 0.98);
  }
  
  .mobile-nav {
    background-color: var(--bg-card);
  }
  
  .user-dropdown {
    background-color: var(--bg-card);
    border-color: var(--border-light);
  }
}

/* ===== ANIMATIONS ===== */
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-dropdown.show {
  animation: slideDown var(--transition-fast) ease-out;
}

/* ===== ACCESSIBILITY ===== */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* Focus styles for better accessibility */
.nav-link:focus,
.btn:focus,
.user-button:focus,
.mobile-menu-btn:focus,
.mobile-nav-link:focus {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}

/* High contrast mode support */
@media (prefers-contrast: high) {
  .navbar {
    border-bottom-width: 2px;
  }
  
  .nav-link,
  .btn,
  .user-button {
    border: 1px solid transparent;
  }
  
  .nav-link:hover,
  .btn:hover,
  .user-button:hover {
    border-color: var(--border-dark);
  }
}
</style>

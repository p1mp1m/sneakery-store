import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { adminGuard } from './adminRoutes';
import { userGuard } from './userRoutes';

// Import layouts
import DefaultLayout from '../assets/layouts/DefaultLayout.vue';

// Import các trang của bạn
import HomePage from '../views/common/HomePage.vue';
import LoginPage from '../views/common/LoginPage.vue';
import RegisterPage from '../views/common/RegisterPage.vue';

// Import admin & user routes
import adminRoutes from './adminRoutes';
import userRoutes from './userRoutes';

const routes = [
  // Default route - redirect to login
  {
    path: '/',
    redirect: '/login'
  },
  // Routes với DefaultLayout (Public pages)
  {
    path: '/home',
    component: DefaultLayout,
    children: [
      {
        path: '',
        name: 'home',
        component: HomePage,
      },
      {
        path: 'products',
        name: 'products',
        component: () => import('../views/common/ProductListPage.vue'),
      },
      {
        path: 'products/:id',
        name: 'product-detail',
        component: () => import('../views/common/ProductDetailPage.vue'),
      },
    ]
  },
  // Auth pages (no layout)
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterPage,
  },
  // User routes (Protected - chỉ dành cho user đã đăng nhập)
  ...userRoutes,
  // Admin routes (Protected - chỉ dành cho admin)
  ...adminRoutes
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// "LÍNH CANH" - Navigation Guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  const requiresAuth = to.meta.requiresAuth;
  const requiresAdmin = to.meta.requiresAdmin;
  const isUserRoute = to.meta.isUserRoute;

  // ====== 1. KIỂM TRA ADMIN ROUTES (/admin/*) ======
  if (requiresAdmin) {
    // Chỉ ADMIN/MODERATOR mới được vào
    // Nếu là USER thường → Chặn
    return adminGuard(to, from, next);
  }

  // ====== 2. KIỂM TRA USER ROUTES (/user/*) ======
  if (isUserRoute) {
    // User routes yêu cầu đăng nhập
    // Admin cũng có thể vào user routes
    return userGuard(to, from, next);
  }

  // ====== 3. KIỂM TRA LOGIN/REGISTER ======
  // Nếu đã đăng nhập mà vào login/register → redirect về dashboard tương ứng
  if ((to.name === 'login' || to.name === 'register') && authStore.isAuthenticated) {
    const user = authStore.currentUser;
    if (user && (user.role === 'ADMIN' || user.role === 'MODERATOR')) {
      next({ path: '/admin/dashboard' });
    } else {
      next({ path: '/user/dashboard' });
    }
    return;
  }

  // ====== 3.1. KIỂM TRA TRANG HOME ======
  // Nếu đã đăng nhập mà vào trang home → redirect về dashboard tương ứng
  if (to.name === 'home' && authStore.isAuthenticated) {
    const user = authStore.currentUser;
    if (user && (user.role === 'ADMIN' || user.role === 'MODERATOR')) {
      next({ path: '/admin/dashboard' });
    } else {
      next({ path: '/user/dashboard' });
    }
    return;
  } 

  // ====== 4. KIỂM TRA CÁC ROUTES CHUNG CẦN ĐĂNG NHẬP ======
  if (requiresAuth && !authStore.isAuthenticated) {
    // Chưa đăng nhập → redirect về login
    next({ 
      path: '/login',
      query: { redirect: to.fullPath }
    });
  } 
  // ====== 5. CÁC TRƯỜNG HỢP KHÁC (PUBLIC PAGES) → CHO PHÉP ======
  else {
    next();
  }
});

export default router;
import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { adminGuard } from "./adminRoutes";
import { userGuard } from "./userRoutes";

// Import layouts (lazy load)
const DefaultLayout = () => import("../assets/layouts/DefaultLayout.vue");

// Import các trang của bạn (lazy load)
const HomePage = () => import("../views/common/HomePage.vue");
const LoginPage = () => import("../views/common/LoginPage.vue");
const RegisterPage = () => import("../views/common/RegisterPage.vue");
const ForgotPassword = () => import("../views/common/ForgotPassword.vue");
const ResetPassWord = () => import("../views/common/ResetPassWord.vue");
// Import admin & user routes
import adminRoutes from "./adminRoutes";
import userRoutes from "./userRoutes";

const routes = [
  // Default route - redirect to home
  {
    path: "/",
    redirect: "/home",
  },
  // Routes với DefaultLayout (Public pages)
  {
    path: "/home",
    component: DefaultLayout,
    children: [
      {
        path: "",
        name: "home",
        component: HomePage,
      },
      {
        path: "products",
        name: "products",
        component: () => import("../views/common/ProductListPage.vue"),
      },
      {
        path: "products/:slug",
        name: "product-detail",
        component: () => import("../views/common/ProductDetailPage.vue").catch(() => {
          // Fallback nếu import thất bại
          return import("../views/common/ProductListPage.vue");
        }),
      },
      {
        path: "flash-sale",
        name: "flash-sale",
        component: () => import("../views/common/FlashSalePage.vue"),
      },
      {
        path: "reviews",
        name: "reviews",
        component: () => import("../views/common/ReviewsPage.vue"),
      },
    ],
  },
  // Auth pages (no layout)
  {
    path: "/login",
    name: "login",
    component: LoginPage,
  },
  {
    path: "/register",
    name: "register",
    component: RegisterPage,
  },
  {
    path: "/forgot-password",
    name: "forgot-password",
    component: ForgotPassword,
  },
  {
    path: "/reset-password",
    name: "reset-password",
    component: ResetPassWord,
  },
  // Cart and Checkout pages (Public - Cho phép cả guest và authenticated users)
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/user/CartPage.vue'),
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('../views/user/CheckoutPage.vue').catch(() => {
      // Fallback nếu import thất bại
      return import('../views/user/CartPage.vue');
    }),
  },
  // User routes (Protected - chỉ dành cho user đã đăng nhập)
  ...userRoutes,
  // Admin routes (Protected - chỉ dành cho admin)
  ...adminRoutes,
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
  if (
    (to.name === "login" || to.name === "register") &&
    authStore.isAuthenticated
  ) {
    const user = authStore.currentUser;
    if (user && (user.role === "ADMIN" || user.role === "MODERATOR")) {
      next({ path: "/admin/dashboard" });
    } else {
      next({ path: "/user/dashboard" });
    }
    return;
  }

  // ====== 3.1. KIỂM TRA TRANG HOME ======
  // Cho phép vào trang home bất kể đã đăng nhập hay chưa
  // (Đã bỏ redirect tự động về dashboard để user có thể xem trang home)

  // ====== 4. KIỂM TRA CÁC ROUTES CHUNG CẦN ĐĂNG NHẬP ======
  if (requiresAuth && !authStore.isAuthenticated) {
    // Chưa đăng nhập → redirect về login
    next({
      path: "/login",
      query: { redirect: to.fullPath },
    });
  }
  // ====== 5. CÁC TRƯỜNG HỢP KHÁC (PUBLIC PAGES) → CHO PHÉP ======
  else {
    next();
  }
});

export default router;

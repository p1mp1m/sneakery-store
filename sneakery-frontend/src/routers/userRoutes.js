// User Routes
import { useAuthStore } from '@/stores/auth'

// Lazy load layout
const DefaultLayout = () => import('@/assets/layouts/DefaultLayout.vue')

// User Pages
const UserDashboard = () => import('@/views/user/UserDashboard.vue')
const UserProfile = () => import('@/views/user/UserProfile.vue')
const UserOrders = () => import('@/views/user/UserOrders.vue')
const WishlistPage = () => import('@/views/user/WishlistPage.vue')
const CartPage = () => import('@/views/user/CartPage.vue')
const CheckoutPage = () => import('@/views/user/CheckoutPage.vue')

const userRoutes = [
  {
    path: '/user',
    component: DefaultLayout,
    meta: { requiresAuth: true, isUserRoute: true },
    redirect: '/user/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: UserDashboard,
        meta: { 
          requiresAuth: true,
          isUserRoute: true,
          title: 'Tổng quan',
          icon: 'dashboard'
        }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: UserProfile,
        meta: { 
          requiresAuth: true,
          isUserRoute: true,
          title: 'Hồ sơ',
          icon: 'person'
        }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: UserOrders,
        meta: { 
          requiresAuth: true,
          isUserRoute: true,
          title: 'Đơn hàng',
          icon: 'shopping_bag'
        }
      },
      {
        path: 'wishlist',
        name: 'Wishlist',
        component: WishlistPage,
        meta: { 
          requiresAuth: true,
          isUserRoute: true,
          title: 'Yêu thích',
          icon: 'favorite'
        }
      },
      {
        path: 'cart',
        name: 'UserCart',
        component: CartPage,
        meta: { 
          requiresAuth: true,
          isUserRoute: true,
          title: 'Giỏ hàng',
          icon: 'shopping_cart'
        }
      },
      {
        path: 'checkout',
        name: 'UserCheckout',
        component: CheckoutPage,
        meta: { 
          requiresAuth: true,
          isUserRoute: true,
          title: 'Thanh toán',
          icon: 'payment'
        }
      }
    ]
  }
]

// User route guard - Chặn ADMIN/MODERATOR và người chưa đăng nhập
export const userGuard = async (to, from, next) => {
  const authStore = useAuthStore()
  
  // Kiểm tra đăng nhập
  if (!authStore.isAuthenticated) {
    // Chưa đăng nhập → redirect đến login
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  // Kiểm tra role - Chặn ADMIN/MODERATOR truy cập user routes
  const user = authStore.currentUser
  
  if (user && (user.role === 'ADMIN' || user.role === 'MODERATOR')) {
    // Là ADMIN/MODERATOR → Chuyển về admin panel
    next({
      path: '/admin/dashboard',
      query: { error: 'admin_only_panel' }
    })
    return
  }
  
  // Là USER thường → cho phép truy cập
  next()
}

export default userRoutes


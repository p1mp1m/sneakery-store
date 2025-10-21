import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Admin Layout
const AdminLayout = () => import('@/layouts/AdminLayout.vue')

// Admin Pages
const AdminDashboard = () => import('@/views/admin/AdminDashboard.vue')
const AdminProducts = () => import('@/views/admin/AdminProducts.vue')
const AdminOrders = () => import('@/views/admin/AdminOrders.vue')
const AdminUsers = () => import('@/views/admin/AdminUsers.vue')
const AdminBrands = () => import('@/views/admin/AdminBrands.vue')
const AdminCategories = () => import('@/views/admin/AdminCategories.vue')
const AdminAnalytics = () => import('@/views/admin/AdminAnalytics.vue')
const AdminSettings = () => import('@/views/admin/AdminSettings.vue')

const adminRoutes = [
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAdmin: true },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: { 
          requiresAdmin: true,
          title: 'Dashboard',
          icon: 'dashboard'
        }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: AdminProducts,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý sản phẩm',
          icon: 'inventory'
        }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: AdminOrders,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý đơn hàng',
          icon: 'shopping_cart'
        }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý người dùng',
          icon: 'people'
        }
      },
      {
        path: 'brands',
        name: 'AdminBrands',
        component: AdminBrands,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý thương hiệu',
          icon: 'branding_watermark'
        }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: AdminCategories,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý danh mục',
          icon: 'category'
        }
      },
      {
        path: 'analytics',
        name: 'AdminAnalytics',
        component: AdminAnalytics,
        meta: { 
          requiresAdmin: true,
          title: 'Phân tích & Báo cáo',
          icon: 'analytics'
        }
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: AdminSettings,
        meta: { 
          requiresAdmin: true,
          title: 'Cài đặt',
          icon: 'settings'
        }
      }
    ]
  }
]

// Admin route guard - Chỉ cho phép ADMIN/MODERATOR
export const adminGuard = async (to, from, next) => {
  const authStore = useAuthStore()
  
  // Kiểm tra xem đã đăng nhập chưa
  if (!authStore.isAuthenticated) {
    // Chưa đăng nhập → redirect về login
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  // Kiểm tra role từ authStore
  const user = authStore.currentUser
  console.log('Admin Guard - Current user:', user) // Debug log
  
  if (!user || (user.role !== 'ADMIN' && user.role !== 'MODERATOR')) {
    // Không phải ADMIN/MODERATOR → Chặn
    console.log('Access denied - User role:', user?.role) // Debug log
    next({
      path: '/user/dashboard',
      query: { error: 'access_denied' }
    })
    return
  }
  
  // Là ADMIN/MODERATOR → Cho phép truy cập
  console.log('Admin access granted for user:', user.email) // Debug log
  next()
}

export default adminRoutes

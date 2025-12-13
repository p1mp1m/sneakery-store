import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Admin Layout - Preload immediately as it's always needed
const AdminLayout = () => import('@/assets/layouts/AdminLayout.vue')

// Critical routes - Preload on admin panel access
const AdminDashboard = () => import('@/views/admin/AdminDashboard.vue')
const AdminOrders = () => import('@/views/admin/AdminOrders.vue')
const AdminProducts = () => import('@/views/admin/AdminProducts.vue')

// Important routes - Lazy load with webpackChunkName for better code splitting
const AdminSales = () => import(/* webpackChunkName: "admin-sales" */ '@/views/admin/AdminSales.vue')
const AdminProductVariants = () => import(/* webpackChunkName: "admin-variants" */ '@/views/admin/AdminProductVariants.vue')
const AdminOrderDetail = () => import(/* webpackChunkName: "admin-order-detail" */ '@/views/admin/AdminOrderDetail.vue')
const AdminUsers = () => import(/* webpackChunkName: "admin-users" */ '@/views/admin/AdminUsers.vue')
// Secondary routes - Grouped by functionality for better code splitting
const AdminBrands = () => import(/* webpackChunkName: "admin-catalog" */ '@/views/admin/AdminBrands.vue')
const AdminCategories = () => import(/* webpackChunkName: "admin-catalog" */ '@/views/admin/AdminCategories.vue')
const AdminMaterials = () => import(/* webpackChunkName: "admin-catalog" */ '@/views/admin/AdminMaterials.vue')
const AdminShoeSoles = () => import(/* webpackChunkName: "admin-catalog" */ '@/views/admin/AdminShoeSoles.vue')
const AdminDiscounts = () => import(/* webpackChunkName: "admin-promotions" */ '@/views/admin/AdminDiscounts.vue')
const AdminFlashSales = () => import(/* webpackChunkName: "admin-promotions" */ '@/views/admin/AdminFlashSales.vue')
const AdminReturns = () => import(/* webpackChunkName: "admin-operations" */ '@/views/admin/AdminReturns.vue')
const AdminWarranty = () => import(/* webpackChunkName: "admin-operations" */ '@/views/admin/AdminWarranty.vue')
const AdminAnalytics = () => import(/* webpackChunkName: "admin-analytics" */ '@/views/admin/AdminAnalytics.vue')
const AdminNotifications = () => import(/* webpackChunkName: "admin-communications" */ '@/views/admin/AdminNotifications.vue')
const AdminEmailTemplates = () => import(/* webpackChunkName: "admin-communications" */ '@/views/admin/AdminEmailTemplates.vue')
const AdminSettings = () => import(/* webpackChunkName: "admin-settings" */ '@/views/admin/AdminSettings.vue')
const AdminReviews = () => import(/* webpackChunkName: "admin-reviews" */ '@/views/admin/AdminReviews.vue')
const AdminActivityLogs = () => import(/* webpackChunkName: "admin-logs" */ '@/views/admin/AdminActivityLogs.vue')
const AdminInventory = () => import(/* webpackChunkName: "admin-inventory" */ '@/views/admin/AdminInventory.vue')
const AdminLoyalty = () => import(/* webpackChunkName: "admin-loyalty" */ '@/views/admin/AdminLoyalty.vue')
const AdminPayments = () => import(/* webpackChunkName: "admin-payments" */ '@/views/admin/AdminPayments.vue')
const AdminProfile = () => import(/* webpackChunkName: "admin-profile" */ '@/views/admin/AdminProfile.vue')
const AdminChangePassword = () => import(/* webpackChunkName: "admin-profile" */ '@/views/admin/AdminChangePassword.vue')

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
        path: 'sales',
        name: 'AdminSales',
        component: AdminSales,
        meta: { 
          requiresAdmin: true,
          title: 'Bán Hàng (POS)',
          icon: 'shopping_cart'
        }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: AdminOrders,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý đơn hàng',
          icon: 'receipt'
        }
      },
      {
        path: 'orders/:id',
        name: 'AdminOrderDetail',
        component: AdminOrderDetail,
        meta: { 
          requiresAdmin: true,
          title: 'Chi tiết đơn hàng',
          icon: 'receipt_long'
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
        path: 'product-variants',
        name: 'AdminProductVariants',
        component: AdminProductVariants,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý biến thể',
          icon: 'style'
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
        path: 'materials',
        name: 'AdminMaterials',
        component: AdminMaterials,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý chất liệu',
          icon: 'layers'
        }
      },
      {
        path: 'shoesoles',
        name: 'AdminShoeSoles',
        component: AdminShoeSoles,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý loại đế giày',
          icon: 'view_day'
        }
      },
      {
        path: 'reviews',
        name: 'AdminReviews',
        component: AdminReviews,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lí đánh giá',
          icon: 'star_rate'
        }
      },
      {
        path: 'flash-sales',
        name: 'AdminFlashSales',
        component: AdminFlashSales,
        meta: { 
          requiresAdmin: true,
          title: 'Flash Sale',
          icon: 'flash_on'
        }
      },
      {
        path: 'discounts',
        name: 'AdminDiscounts',
        component: AdminDiscounts,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý giảm giá',
          icon: 'percent'
        }
      },
      {
        path: 'returns',
        name: 'AdminReturns',
        component: AdminReturns,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý trả hàng',
          icon: 'assignment_return'
        }
      },
      {
        path: 'warranty',
        name: 'AdminWarranty',
        component: AdminWarranty,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý bảo hành',
          icon: 'verified_user'
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
        path: 'notifications',
        name: 'AdminNotifications',
        component: AdminNotifications,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý thông báo',
          icon: 'notifications'
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
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: AdminProfile,
        meta: { 
          requiresAdmin: true,
          title: 'Hồ sơ',
          icon: 'person'
        }
      },
      {
        path: 'change-password',
        name: 'AdminChangePassword',
        component: AdminChangePassword,
        meta: { 
          requiresAdmin: true,
          title: 'Đổi mật khẩu',
          icon: 'lock'
        }
      },
      {
        path: 'activity-logs',
        name: 'AdminActivityLogs',
        component: AdminActivityLogs,
        meta: { 
          requiresAdmin: true,
          title: 'Nhật ký hoạt động',
          icon: 'history'
        }
      },
      {
        path: 'inventory',
        name: 'AdminInventory',
        component: AdminInventory,
        meta: { 
          requiresAdmin: true,
          title: 'Quản lý kho',
          icon: 'inventory_2'
        }
      },
      {
        path: 'loyalty',
        name: 'AdminLoyalty',
        component: AdminLoyalty,
        meta: { 
          requiresAdmin: true,
          title: 'Điểm thưởng',
          icon: 'stars'
        }
      },
      {
        path: 'payments',
        name: 'AdminPayments',
        component: AdminPayments,
        meta: { 
          requiresAdmin: true,
          title: 'Thanh toán',
          icon: 'payment'
        }
      },
      {
        path: 'email-templates',
        name: 'AdminEmailTemplates',
        component: AdminEmailTemplates,
        meta: { 
          requiresAdmin: true,
          title: 'Mẫu email',
          icon: 'email'
        }
      }
    ]
  }
]

// Admin route guard - Chỉ cho phép ADMIN/MODERATOR, chặn USER thường
export const adminGuard = async (to, from, next) => {
  const authStore = useAuthStore()
  
  // Kiểm tra đăng nhập
  if (!authStore.isAuthenticated) {
    // Chưa đăng nhập → redirect về login
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  // Kiểm tra role
  const user = authStore.currentUser
  
  if (!user || (user.role !== 'ADMIN' && user.role !== 'MODERATOR')) {
    // Không phải ADMIN/MODERATOR → Chặn và redirect về user panel
    console.warn('⛔ Access Denied: User không có quyền truy cập Admin Panel')
    next({
      path: '/user/dashboard',
      query: { error: 'admin_access_denied' }
    })
    return
  }
  
  // Là ADMIN/MODERATOR → Cho phép truy cập
  console.log('✅ Admin access granted:', user.fullName || user.userId, '- Role:', user.role)
  next()
}

export default adminRoutes

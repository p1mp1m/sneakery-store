import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Admin Layout
const AdminLayout = () => import('@/assets/layouts/AdminLayout.vue')

// Admin Pages
const AdminDashboard = () => import('@/views/admin/AdminDashboard.vue')
const AdminSales = () => import('@/views/admin/AdminSales.vue')
const AdminProducts = () => import('@/views/admin/AdminProducts.vue')
const AdminProductVariants = () => import('@/views/admin/AdminProductVariants.vue')
const AdminOrders = () => import('@/views/admin/AdminOrders.vue')
const AdminUsers = () => import('@/views/admin/AdminUsers.vue')
const AdminBrands = () => import('@/views/admin/AdminBrands.vue')
const AdminCategories = () => import('@/views/admin/AdminCategories.vue')
const AdminMaterials = () => import('@/views/admin/AdminMaterials.vue')
const AdminShoeSoles = () => import('@/views/admin/AdminShoeSoles.vue')
const AdminDiscounts = () => import('@/views/admin/AdminDiscounts.vue')
const AdminReturns = () => import('@/views/admin/AdminReturns.vue')
const AdminWarranty = () => import('@/views/admin/AdminWarranty.vue')
const AdminAnalytics = () => import('@/views/admin/AdminAnalytics.vue')
const AdminNotifications = () => import('@/views/admin/AdminNotifications.vue')
const AdminSettings = () => import('@/views/admin/AdminSettings.vue')
const AdminReviews = () => import('@/views/admin/AdminReviews.vue')
const AdminFlashSales = () => import('@/views/admin/AdminFlashSales.vue')
const AdminActivityLogs = () => import('@/views/admin/AdminActivityLogs.vue')
const AdminInventory = () => import('@/views/admin/AdminInventory.vue')
const AdminLoyalty = () => import('@/views/admin/AdminLoyalty.vue')
const AdminPayments = () => import('@/views/admin/AdminPayments.vue')
const AdminEmailTemplates = () => import('@/views/admin/AdminEmailTemplates.vue')

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

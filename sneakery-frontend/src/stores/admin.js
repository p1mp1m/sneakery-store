import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import AdminService from '@/services/adminService'

export const useAdminStore = defineStore('admin', () => {
  // ===== STATE =====
  const isAdmin = ref(false)
  const adminUser = ref(null)
  const dashboardStats = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // ===== GETTERS =====
  const isAuthenticated = computed(() => !!adminUser.value)
  const userRole = computed(() => adminUser.value?.role || 'USER')
  const canAccessAdmin = computed(() => isAdmin.value && isAuthenticated.value)

  // ===== ACTIONS =====
  
  // Check if user is admin
  const checkAdminStatus = async () => {
    try {
      loading.value = true
      error.value = null
      
      const token = localStorage.getItem('token')
      if (!token) {
        isAdmin.value = false
        adminUser.value = null
        return false
      }

      // Call API to check admin status
      const response = await AdminService.checkAdminStatus()
      isAdmin.value = response.isAdmin
      
      if (response.isAdmin) {
        adminUser.value = {
          id: response.userId,
          email: response.email || 'admin',
          role: response.role || 'ADMIN'
        }
      } else {
        adminUser.value = null
      }
      
      return response.isAdmin
    } catch (err) {
      error.value = 'Lỗi khi kiểm tra quyền admin'
      isAdmin.value = false
      adminUser.value = null
      return false
    } finally {
      loading.value = false
    }
  }

  // Get dashboard statistics
  const fetchDashboardStats = async () => {
    try {
      loading.value = true
      error.value = null
      
      const stats = await AdminService.getDashboardStats()
      dashboardStats.value = stats
      return stats
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải thống kê dashboard'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Get revenue analytics
  const fetchRevenueAnalytics = async (period = '30d') => {
    try {
      loading.value = true
      error.value = null
      
      const analytics = await AdminService.getRevenueAnalytics(period)
      return analytics
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải phân tích doanh thu'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Get order analytics
  const fetchOrderAnalytics = async (period = '30d') => {
    try {
      loading.value = true
      error.value = null
      
      const analytics = await AdminService.getOrderAnalytics(period)
      return analytics
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải phân tích đơn hàng'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Product management
  const fetchProducts = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      error.value = null
      
      const result = await AdminService.getProducts(page, size, filters)
      return result
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải danh sách sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  const createProduct = async (productData) => {
    try {
      loading.value = true
      error.value = null
      
      const product = await AdminService.createProduct(productData)
      return product
    } catch (err) {
      error.value = err.message || 'Lỗi khi tạo sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateProduct = async (id, productData) => {
    try {
      loading.value = true
      error.value = null
      
      const product = await AdminService.updateProduct(id, productData)
      return product
    } catch (err) {
      error.value = err.message || 'Lỗi khi cập nhật sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteProduct = async (id) => {
    try {
      loading.value = true
      error.value = null
      
      await AdminService.deleteProduct(id)
      return true
    } catch (err) {
      error.value = err.message || 'Lỗi khi xóa sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getProductById = async (id) => {
    try {
      loading.value = true
      error.value = null
      
      const product = await AdminService.getProductById(id)
      return product
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải chi tiết sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Advanced product features
  const importProducts = async (productList) => {
    try {
      loading.value = true
      error.value = null
      
      const result = await AdminService.importProducts(productList)
      return result
    } catch (err) {
      error.value = err.message || 'Lỗi khi import sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  const bulkUpdateProducts = async (bulkData) => {
    try {
      loading.value = true
      error.value = null
      
      const result = await AdminService.bulkUpdateProducts(bulkData)
      return result
    } catch (err) {
      error.value = err.message || 'Lỗi khi cập nhật hàng loạt'
      throw err
    } finally {
      loading.value = false
    }
  }

  const duplicateProduct = async (productId) => {
    try {
      loading.value = true
      error.value = null
      
      const product = await AdminService.duplicateProduct(productId)
      return product
    } catch (err) {
      error.value = err.message || 'Lỗi khi nhân bản sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getProductStatistics = async () => {
    try {
      loading.value = true
      error.value = null
      
      const stats = await AdminService.getProductStatistics()
      return stats
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải thống kê sản phẩm'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Order management
  const fetchOrders = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      error.value = null
      
      const result = await AdminService.getOrders(page, size, filters)
      return result
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải danh sách đơn hàng'
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateOrderStatus = async (id, status) => {
    try {
      loading.value = true
      error.value = null
      
      const order = await AdminService.updateOrderStatus(id, status)
      return order
    } catch (err) {
      error.value = err.message || 'Lỗi khi cập nhật trạng thái đơn hàng'
      throw err
    } finally {
      loading.value = false
    }
  }

  // User management
  const fetchUsers = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      error.value = null
      
      const result = await AdminService.getUsers(page, size, filters)
      return result
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải danh sách người dùng'
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateUserStatus = async (id, isActive) => {
    try {
      loading.value = true
      error.value = null
      
      const user = await AdminService.updateUserStatus(id, isActive)
      return user
    } catch (err) {
      error.value = err.message || 'Lỗi khi cập nhật trạng thái người dùng'
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateUserRole = async (id, role) => {
    try {
      loading.value = true
      error.value = null
      
      const user = await AdminService.updateUserRole(id, role)
      return user
    } catch (err) {
      error.value = err.message || 'Lỗi khi cập nhật quyền người dùng'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Brand management
  const fetchBrands = async () => {
    try {
      loading.value = true
      error.value = null
      
      const brands = await AdminService.getBrands()
      return brands
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải danh sách thương hiệu'
      throw err
    } finally {
      loading.value = false
    }
  }

  const createBrand = async (brandData) => {
    try {
      loading.value = true
      error.value = null
      
      const brand = await AdminService.createBrand(brandData)
      return brand
    } catch (err) {
      error.value = err.message || 'Lỗi khi tạo thương hiệu'
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateBrand = async (id, brandData) => {
    try {
      loading.value = true
      error.value = null
      
      const brand = await AdminService.updateBrand(id, brandData)
      return brand
    } catch (err) {
      error.value = err.message || 'Lỗi khi cập nhật thương hiệu'
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteBrand = async (id) => {
    try {
      loading.value = true
      error.value = null
      
      await AdminService.deleteBrand(id)
      return true
    } catch (err) {
      error.value = err.message || 'Lỗi khi xóa thương hiệu'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Category management
  const fetchCategories = async () => {
    try {
      loading.value = true
      error.value = null
      
      const categories = await AdminService.getCategories()
      return categories
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải danh sách danh mục'
      throw err
    } finally {
      loading.value = false
    }
  }

  const createCategory = async (categoryData) => {
    try {
      loading.value = true
      error.value = null
      
      const category = await AdminService.createCategory(categoryData)
      return category
    } catch (err) {
      error.value = err.message || 'Lỗi khi tạo danh mục'
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateCategory = async (id, categoryData) => {
    try {
      loading.value = true
      error.value = null
      
      const category = await AdminService.updateCategory(id, categoryData)
      return category
    } catch (err) {
      error.value = err.message || 'Lỗi khi cập nhật danh mục'
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteCategory = async (id) => {
    try {
      loading.value = true
      error.value = null
      
      await AdminService.deleteCategory(id)
      return true
    } catch (err) {
      error.value = err.message || 'Lỗi khi xóa danh mục'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Export/Import
  const exportData = async (type, format = 'excel') => {
    try {
      loading.value = true
      error.value = null
      
      const data = await AdminService.exportData(type, format)
      return data
    } catch (err) {
      error.value = err.message || 'Lỗi khi xuất dữ liệu'
      throw err
    } finally {
      loading.value = false
    }
  }

  const importData = async (type, file) => {
    try {
      loading.value = true
      error.value = null
      
      const result = await AdminService.importData(type, file)
      return result
    } catch (err) {
      error.value = err.message || 'Lỗi khi nhập dữ liệu'
      throw err
    } finally {
      loading.value = false
    }
  }

  // Clear error
  const clearError = () => {
    error.value = null
  }

  // Reset store
  const reset = () => {
    isAdmin.value = false
    adminUser.value = null
    dashboardStats.value = null
    loading.value = false
    error.value = null
  }

  return {
    // State
    isAdmin,
    adminUser,
    dashboardStats,
    loading,
    error,
    
    // Getters
    isAuthenticated,
    userRole,
    canAccessAdmin,
    
    // Actions
    checkAdminStatus,
    fetchDashboardStats,
    fetchRevenueAnalytics,
    fetchOrderAnalytics,
    fetchProducts,
    createProduct,
    updateProduct,
    deleteProduct,
    getProductById,
    importProducts,
    bulkUpdateProducts,
    duplicateProduct,
    getProductStatistics,
    fetchOrders,
    updateOrderStatus,
    fetchUsers,
    updateUserStatus,
    updateUserRole,
    fetchBrands,
    createBrand,
    updateBrand,
    deleteBrand,
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    exportData,
    importData,
    clearError,
    reset
  }
})

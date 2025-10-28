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
  
  // Data states
  const brands = ref([])
  const categories = ref([])
  const coupons = ref([])
  const materials = ref([]) // Chất liệu
  const soles = ref([])     // Đế giày


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
      
      const result = await AdminService.getBrands()
      brands.value = result || []
      return result
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải danh sách thương hiệu'
      brands.value = []
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
      
      const result = await AdminService.getCategories()
      categories.value = result || []
      return result
    } catch (err) {
      error.value = err.message || 'Lỗi khi tải danh sách danh mục'
      categories.value = []
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

  // ===============================
// Material management (Chất liệu)
// ===============================
const fetchMaterials = async () => {
  try {
    loading.value = true
    error.value = null

    const result = await AdminService.getMaterials()
    materials.value = result || []
    return result
  } catch (err) {
    error.value = err.message || 'Lỗi khi tải danh sách chất liệu'
    materials.value = []
    throw err
  } finally {
    loading.value = false
  }
}

const createMaterial = async (materialData) => {
  try {
    loading.value = true
    error.value = null

    const material = await AdminService.createMaterial(materialData)
    // ✅ Thêm vào danh sách hiện tại nếu cần cập nhật UI ngay
    // ✅ Cập nhật reactive list đúng cách
    if (materials.value) {
      materials.value = [...materials.value, material]
    } else {
      materials.value = [material]
    }
    return material
  } catch (err) {
    error.value = err.message || 'Lỗi khi tạo chất liệu'
    throw err
  } finally {
    loading.value = false
  }
}

const updateMaterial = async (id, materialData) => {
  try {
    loading.value = true
    error.value = null

    const updated = await AdminService.updateMaterial(id, materialData)
    // ✅ Cập nhật lại danh sách trong store
    const index = materials.value.findIndex((m) => m.id === id)
    if (index !== -1) materials.value[index] = updated
    return updated
  } catch (err) {
    error.value = err.message || 'Lỗi khi cập nhật chất liệu'
    throw err
  } finally {
    loading.value = false
  }
}

const deleteMaterial = async (id) => {
  try {
    loading.value = true
    error.value = null

    await AdminService.deleteMaterial(id)
    // ✅ Xóa trong danh sách store
    materials.value = materials.value.filter((m) => m.id !== id)
    return true
  } catch (err) {
    error.value = err.message || 'Lỗi khi xóa chất liệu'
    throw err
  } finally {
    loading.value = false
  }
}

// ================================
// Shoe Sole management (Đế giày)
// ================================
const fetchSoles = async () => {
  try {
    loading.value = true
    error.value = null

    const result = await AdminService.getSoles()
    soles.value = result || []
    return result
  } catch (err) {
    error.value = err.message || 'Lỗi khi tải danh sách loại đế'
    soles.value = []
    throw err
  } finally {
    loading.value = false
  }
}

const createSole = async (soleData) => {
  try {
    loading.value = true
    error.value = null

    const sole = await AdminService.createSole(soleData)
    // ✅ Cập nhật reactive list đúng cách
    if (soles.value) {
      soles.value = [...soles.value, soles]
    } else {
      soles.value = [soles]
    }
    return sole
  } catch (err) {
    error.value = err.message || 'Lỗi khi tạo loại đế giày'
    throw err
  } finally {
    loading.value = false
  }
}

const updateSole = async (id, soleData) => {
  try {
    loading.value = true
    error.value = null

    const updated = await AdminService.updateSole(id, soleData)
    const index = soles.value.findIndex((s) => s.id === id)
    if (index !== -1) soles.value[index] = updated
    return updated
  } catch (err) {
    error.value = err.message || 'Lỗi khi cập nhật loại đế giày'
    throw err
  } finally {
    loading.value = false
  }
}

const deleteSole = async (id) => {
  try {
    loading.value = true
    error.value = null

    await AdminService.deleteSole(id)
    soles.value = soles.value.filter((s) => s.id !== id)
    return true
  } catch (err) {
    error.value = err.message || 'Lỗi khi xóa loại đế giày'
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

  // ===== PRODUCT VARIANTS =====
  const fetchProductVariants = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getProductVariants(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching product variants:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchProductVariantStats = async () => {
    try {
      loading.value = true
      const result = await AdminService.getProductVariantStats()
      return result
    } catch (error) {
      console.error('Error fetching product variant stats:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createProductVariant = async (variantData) => {
    try {
      loading.value = true
      const result = await AdminService.createProductVariant(variantData)
      return result
    } catch (error) {
      console.error('Error creating product variant:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateProductVariant = async (id, variantData) => {
    try {
      loading.value = true
      const result = await AdminService.updateProductVariant(id, variantData)
      return result
    } catch (error) {
      console.error('Error updating product variant:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const deleteProductVariant = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.deleteProductVariant(id)
      return result
    } catch (error) {
      console.error('Error deleting product variant:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateVariantStock = async (id, stockData) => {
    try {
      loading.value = true
      const result = await AdminService.updateVariantStock(id, stockData)
      return result
    } catch (error) {
      console.error('Error updating variant stock:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== ACTIVITY LOGS =====
  const fetchActivityLogs = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getActivityLogs(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching activity logs:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const exportActivityLogs = async (format = 'csv', filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.exportActivityLogs(format, filters)
      return result
    } catch (error) {
      console.error('Error exporting activity logs:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== EMAIL TEMPLATES =====
  const fetchEmailTemplates = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getEmailTemplates(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching email templates:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createEmailTemplate = async (templateData) => {
    try {
      loading.value = true
      const result = await AdminService.createEmailTemplate(templateData)
      return result
    } catch (error) {
      console.error('Error creating email template:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateEmailTemplate = async (id, templateData) => {
    try {
      loading.value = true
      const result = await AdminService.updateEmailTemplate(id, templateData)
      return result
    } catch (error) {
      console.error('Error updating email template:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const deleteEmailTemplate = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.deleteEmailTemplate(id)
      return result
    } catch (error) {
      console.error('Error deleting email template:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const sendTestEmail = async (id, testData) => {
    try {
      loading.value = true
      const result = await AdminService.sendTestEmail(id, testData)
      return result
    } catch (error) {
      console.error('Error sending test email:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== INVENTORY =====
  const fetchInventory = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getInventory(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching inventory:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchInventoryLogs = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getInventoryLogs(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching inventory logs:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const adjustStock = async (adjustmentData) => {
    try {
      loading.value = true
      const result = await AdminService.adjustStock(adjustmentData)
      return result
    } catch (error) {
      console.error('Error adjusting stock:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== LOYALTY =====
  const fetchLoyaltyUsers = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getLoyaltyUsers(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching loyalty users:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const adjustLoyaltyPoints = async (userId, adjustmentData) => {
    try {
      loading.value = true
      const result = await AdminService.adjustLoyaltyPoints(userId, adjustmentData)
      return result
    } catch (error) {
      console.error('Error adjusting loyalty points:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchLoyaltyStats = async () => {
    try {
      loading.value = true
      const result = await AdminService.getLoyaltyStats()
      return result
    } catch (error) {
      console.error('Error fetching loyalty stats:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== PAYMENTS =====
  const fetchPayments = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getPayments(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching payments:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const refundPayment = async (id, refundData) => {
    try {
      loading.value = true
      const result = await AdminService.refundPayment(id, refundData)
      return result
    } catch (error) {
      console.error('Error refunding payment:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchPaymentStats = async () => {
    try {
      loading.value = true
      const result = await AdminService.getPaymentStats()
      return result
    } catch (error) {
      console.error('Error fetching payment stats:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== ANALYTICS (Bổ sung) =====
  const fetchProductAnalytics = async (period = '30d') => {
    try {
      loading.value = true
      const result = await AdminService.getProductAnalytics(period)
      return result
    } catch (error) {
      console.error('Error fetching product analytics:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const fetchCustomerAnalytics = async (period = '30d') => {
    try {
      loading.value = true
      const result = await AdminService.getCustomerAnalytics(period)
      return result
    } catch (error) {
      console.error('Error fetching customer analytics:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== SETTINGS =====
  const fetchSettings = async () => {
    try {
      loading.value = true
      const result = await AdminService.getSettings()
      return result
    } catch (error) {
      console.error('Error fetching settings:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateSettings = async (settingsData) => {
    try {
      loading.value = true
      const result = await AdminService.updateSettings(settingsData)
      return result
    } catch (error) {
      console.error('Error updating settings:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== POS / SALES =====
  const createPOSOrder = async (orderData) => {
    try {
      loading.value = true
      const result = await AdminService.createPOSOrder(orderData)
      return result
    } catch (error) {
      console.error('Error creating POS order:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // Clear error
  // ===== REVIEWS =====
  const fetchReviews = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getReviews(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching reviews:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateReviewStatus = async (id, isApproved) => {
    try {
      loading.value = true
      const result = await AdminService.updateReviewStatus(id, isApproved)
      return result
    } catch (error) {
      console.error('Error updating review status:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const deleteReview = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.deleteReview(id)
      return result
    } catch (error) {
      console.error('Error deleting review:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const replyToReview = async (id, replyText) => {
    try {
      loading.value = true
      const result = await AdminService.replyToReview(id, replyText)
      return result
    } catch (error) {
      console.error('Error replying to review:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== FLASH SALES =====
  const fetchFlashSales = async () => {
    try {
      loading.value = true
      const result = await AdminService.getFlashSales()
      return result
    } catch (error) {
      console.error('Error fetching flash sales:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createFlashSale = async (flashSaleData) => {
    try {
      loading.value = true
      const result = await AdminService.createFlashSale(flashSaleData)
      return result
    } catch (error) {
      console.error('Error creating flash sale:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateFlashSale = async (id, flashSaleData) => {
    try {
      loading.value = true
      const result = await AdminService.updateFlashSale(id, flashSaleData)
      return result
    } catch (error) {
      console.error('Error updating flash sale:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const deleteFlashSale = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.deleteFlashSale(id)
      return result
    } catch (error) {
      console.error('Error deleting flash sale:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== NOTIFICATIONS =====
  const fetchNotifications = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getNotifications(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching notifications:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createNotification = async (notificationData) => {
    try {
      loading.value = true
      const result = await AdminService.createNotification(notificationData)
      return result
    } catch (error) {
      console.error('Error creating notification:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateNotification = async (id, notificationData) => {
    try {
      loading.value = true
      const result = await AdminService.updateNotification(id, notificationData)
      return result
    } catch (error) {
      console.error('Error updating notification:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const deleteNotification = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.deleteNotification(id)
      return result
    } catch (error) {
      console.error('Error deleting notification:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const sendNotification = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.sendNotification(id)
      return result
    } catch (error) {
      console.error('Error sending notification:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== RETURNS =====
  const fetchReturns = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getReturns(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching returns:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateReturnStatus = async (id, status, adminNote = '') => {
    try {
      loading.value = true
      const result = await AdminService.updateReturnStatus(id, status, adminNote)
      return result
    } catch (error) {
      console.error('Error updating return status:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const processRefund = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.processRefund(id)
      return result
    } catch (error) {
      console.error('Error processing refund:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== WARRANTY =====
  const fetchWarranties = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getWarranties(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching warranties:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateWarrantyStatus = async (id, status, adminNote = '') => {
    try {
      loading.value = true
      const result = await AdminService.updateWarrantyStatus(id, status, adminNote)
      return result
    } catch (error) {
      console.error('Error updating warranty status:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const processWarranty = async (id, resolutionNote, warrantyType) => {
    try {
      loading.value = true
      const result = await AdminService.processWarranty(id, resolutionNote, warrantyType)
      return result
    } catch (error) {
      console.error('Error processing warranty:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // ===== COUPONS =====
  const fetchCoupons = async (page = 0, size = 10, filters = {}) => {
    try {
      loading.value = true
      const result = await AdminService.getCoupons(page, size, filters)
      return result
    } catch (error) {
      console.error('Error fetching coupons:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createCoupon = async (couponData) => {
    try {
      loading.value = true
      const result = await AdminService.createCoupon(couponData)
      return result
    } catch (error) {
      console.error('Error creating coupon:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateCoupon = async (id, couponData) => {
    try {
      loading.value = true
      const result = await AdminService.updateCoupon(id, couponData)
      return result
    } catch (error) {
      console.error('Error updating coupon:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const deleteCoupon = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.deleteCoupon(id)
      return result
    } catch (error) {
      console.error('Error deleting coupon:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const toggleCouponStatus = async (id) => {
    try {
      loading.value = true
      const result = await AdminService.toggleCouponStatus(id)
      return result
    } catch (error) {
      console.error('Error toggling coupon status:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

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
    brands.value = []
    categories.value = []
    coupons.value = []
  }

  return {
    // State
    isAdmin,
    adminUser,
    dashboardStats,
    loading,
    error,
    brands,
    categories,
    materials,
    soles,
    coupons,
    
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
        // Material (Chất liệu)
    fetchMaterials,
    createMaterial,
    updateMaterial,
    deleteMaterial,

    // Sole (Đế giày)
    fetchSoles,
    createSole,
    updateSole,
    deleteSole,

    exportData,
    importData,
    
    // New Actions
    fetchProductVariants,
    fetchProductVariantStats,
    createProductVariant,
    updateProductVariant,
    deleteProductVariant,
    updateVariantStock,
    fetchActivityLogs,
    exportActivityLogs,
    fetchEmailTemplates,
    createEmailTemplate,
    updateEmailTemplate,
    deleteEmailTemplate,
    sendTestEmail,
    fetchInventory,
    fetchInventoryLogs,
    adjustStock,
    fetchLoyaltyUsers,
    adjustLoyaltyPoints,
    fetchLoyaltyStats,
    fetchPayments,
    refundPayment,
    fetchPaymentStats,
    fetchProductAnalytics,
    fetchCustomerAnalytics,
    fetchSettings,
    updateSettings,
    createPOSOrder,
    
    // New Actions - Reviews
    fetchReviews,
    updateReviewStatus,
    deleteReview,
    replyToReview,
    
    // New Actions - Flash Sales
    fetchFlashSales,
    createFlashSale,
    updateFlashSale,
    deleteFlashSale,
    
    // New Actions - Notifications
    fetchNotifications,
    createNotification,
    updateNotification,
    deleteNotification,
    sendNotification,
    
    // New Actions - Returns
    fetchReturns,
    updateReturnStatus,
    processRefund,
    
    // New Actions - Warranty
    fetchWarranties,
    updateWarrantyStatus,
    processWarranty,
    
    // New Actions - Coupons
    fetchCoupons,
    createCoupon,
    updateCoupon,
    deleteCoupon,
    toggleCouponStatus,
    
    clearError,
    reset
  }
})

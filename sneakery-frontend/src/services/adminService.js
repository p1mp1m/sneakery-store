import axios from 'axios'

// Sử dụng relative path để Vite proxy có thể forward requests
const API_BASE_URL = '/api/admin'

// Tạo axios instance cho admin API
const adminApi = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor để thêm JWT token
adminApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor để xử lý lỗi
adminApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token hết hạn hoặc không hợp lệ
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

class AdminService {
  // ===== DASHBOARD & ANALYTICS =====
  async getDashboardStats() {
    try {
      const response = await adminApi.get('/dashboard/stats')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // Check admin status
  async checkAdminStatus() {
    try {
      const response = await adminApi.get('/check-admin')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getRevenueAnalytics(period = '30d') {
    try {
      const response = await adminApi.get(`/analytics/revenue?period=${period}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getOrderAnalytics(period = '30d') {
    try {
      const response = await adminApi.get(`/analytics/orders?period=${period}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== PRODUCT MANAGEMENT =====
  async getProducts(page = 0, size = 10, filters = {}) {
    try {
      // Lọc bỏ các giá trị undefined/null/empty để tránh gửi "undefined" trong URL
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/products?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getProductById(id) {
    try {
      const response = await adminApi.get(`/products/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createProduct(productData) {
    try {
      const response = await adminApi.post('/products', productData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateProduct(id, productData) {
    try {
      const response = await adminApi.put(`/products/${id}`, productData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteProduct(id) {
    try {
      const response = await adminApi.delete(`/products/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== ADVANCED PRODUCT FEATURES =====
  
  /**
   * Import sản phẩm từ Excel
   * @param {Array} productList - Danh sách sản phẩm đã parse từ Excel
   */
  async importProducts(productList) {
    try {
      const response = await adminApi.post('/products/import', productList)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  /**
   * Bulk update sản phẩm
   * @param {Object} bulkData - { productIds: [], action: '', isActive/brandId/categoryId }
   */
  async bulkUpdateProducts(bulkData) {
    try {
      const response = await adminApi.post('/products/bulk-update', bulkData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  /**
   * Nhân bản (duplicate) sản phẩm
   * @param {Number} productId - ID sản phẩm cần nhân bản
   */
  async duplicateProduct(productId) {
    try {
      const response = await adminApi.post(`/products/${productId}/duplicate`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  /**
   * Lấy thống kê sản phẩm
   */
  async getProductStatistics() {
    try {
      const response = await adminApi.get('/products/statistics')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async uploadProductImage(productId, file) {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('productId', productId)
      
      const response = await adminApi.post('/products/upload-image', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== ORDER MANAGEMENT =====
  async getOrders(page = 0, size = 10, filters = {}) {
    try {
      // Lọc bỏ các giá trị undefined/null/empty để tránh gửi "undefined" trong URL
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/orders?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getOrderById(id) {
    try {
      const response = await adminApi.get(`/orders/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateOrderStatus(id, status) {
    try {
      const response = await adminApi.put(`/orders/${id}/status`, { status })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== USER MANAGEMENT =====
  async getUsers(page = 0, size = 10, filters = {}) {
    try {
      // Lọc bỏ các giá trị undefined/null/empty để tránh gửi "undefined" trong URL
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/users?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getUserById(id) {
    try {
      const response = await adminApi.get(`/users/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateUserStatus(id, isActive) {
    try {
      const response = await adminApi.put(`/users/${id}/status`, { isActive })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateUserRole(id, role) {
    try {
      const response = await adminApi.put(`/users/${id}/role`, { role })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== BRAND MANAGEMENT =====
  async getBrands() {
    try {
      const response = await adminApi.get('/brands')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createBrand(brandData) {
    try {
      const response = await adminApi.post('/brands', brandData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateBrand(id, brandData) {
    try {
      const response = await adminApi.put(`/brands/${id}`, brandData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteBrand(id) {
    try {
      const response = await adminApi.delete(`/brands/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== CATEGORY MANAGEMENT =====
  async getCategories() {
    try {
      const response = await adminApi.get('/categories')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createCategory(categoryData) {
    try {
      const response = await adminApi.post('/categories', categoryData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateCategory(id, categoryData) {
    try {
      const response = await adminApi.put(`/categories/${id}`, categoryData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteCategory(id) {
    try {
      const response = await adminApi.delete(`/categories/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

    // ===== MATERIAL MANAGEMENT (CHẤT LIỆU) =====
  async getMaterials() {
    try {
      const response = await adminApi.get('/materials')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createMaterial(data) {
    try {
      const response = await adminApi.post('/materials', data)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateMaterial(id, data) {
    try {
      const response = await adminApi.put(`/materials/${id}`, data)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteMaterial(id) {
    try {
      const response = await adminApi.delete(`/materials/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== SOLE MANAGEMENT (ĐẾ GIÀY) =====
  async getSoles() {
    try {
      const response = await adminApi.get('/shoe-soles')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createSole(data) {
    try {
      const response = await adminApi.post('/shoe-soles', data)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateSole(id, data) {
    try {
      const response = await adminApi.put(`/shoe-soles/${id}`, data)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteSole(id) {
    try {
      const response = await adminApi.delete(`/shoe-soles/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }


  // ===== EXPORT/IMPORT =====
  async exportData(type, format = 'excel') {
    try {
      const response = await adminApi.get(`/export/${type}?format=${format}`, {
        responseType: 'blob'
      })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async importData(type, file) {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('type', type)
      
      const response = await adminApi.post('/import', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== COUPONS =====
  async getCoupons(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/coupons?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getCouponById(id) {
    try {
      const response = await adminApi.get(`/coupons/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createCoupon(couponData) {
    try {
      const response = await adminApi.post('/coupons', couponData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateCoupon(id, couponData) {
    try {
      const response = await adminApi.put(`/coupons/${id}`, couponData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteCoupon(id) {
    try {
      const response = await adminApi.delete(`/coupons/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async toggleCouponStatus(id) {
    try {
      const response = await adminApi.put(`/coupons/${id}/toggle-status`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async validateCoupon(code) {
    try {
      const response = await adminApi.get(`/coupons/validate/${code}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== RETURNS =====
  async getReturns(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/returns?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getReturnById(id) {
    try {
      const response = await adminApi.get(`/returns/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateReturnStatus(id, status, adminNote = '') {
    try {
      const response = await adminApi.put(`/returns/${id}/status`, { 
        status, 
        adminNote 
      })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async processRefund(id) {
    try {
      const response = await adminApi.post(`/returns/${id}/refund`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== WARRANTY =====
  async getWarranties(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/warranties?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getWarrantyById(id) {
    try {
      const response = await adminApi.get(`/warranties/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateWarrantyStatus(id, status, adminNote = '') {
    try {
      const response = await adminApi.put(`/warranties/${id}/status`, { 
        status, 
        adminNote 
      })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async processWarranty(id, resolutionNote, warrantyType) {
    try {
      const response = await adminApi.post(`/warranties/${id}/process`, {
        resolutionNote,
        warrantyType
      })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== NOTIFICATIONS =====
  async getNotifications(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/notifications?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getNotificationById(id) {
    try {
      const response = await adminApi.get(`/notifications/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createNotification(notificationData) {
    try {
      const response = await adminApi.post('/notifications', notificationData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateNotification(id, notificationData) {
    try {
      const response = await adminApi.put(`/notifications/${id}`, notificationData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteNotification(id) {
    try {
      const response = await adminApi.delete(`/notifications/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async sendNotification(id) {
    try {
      const response = await adminApi.post(`/notifications/${id}/send`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== REVIEWS =====
  async getReviews(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/reviews?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getReviewById(id) {
    try {
      const response = await adminApi.get(`/reviews/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateReviewStatus(id, isApproved) {
    try {
      const response = await adminApi.put(`/reviews/${id}/status?isApproved=${isApproved}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteReview(id) {
    try {
      const response = await adminApi.delete(`/reviews/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async replyToReview(id, replyText) {
    try {
      const response = await adminApi.post(`/reviews/${id}/reply`, { replyText })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== FLASH SALES =====
  async getFlashSales() {
    try {
      const response = await adminApi.get('/flash-sales')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getFlashSaleById(id) {
    try {
      const response = await adminApi.get(`/flash-sales/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createFlashSale(flashSaleData) {
    try {
      const response = await adminApi.post('/flash-sales', flashSaleData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateFlashSale(id, flashSaleData) {
    try {
      const response = await adminApi.put(`/flash-sales/${id}`, flashSaleData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteFlashSale(id) {
    try {
      const response = await adminApi.delete(`/flash-sales/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== PRODUCT VARIANTS =====
  async getProductVariants(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/product-variants?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getProductVariantStats() {
    try {
      const response = await adminApi.get('/product-variants/statistics')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getProductVariantById(id) {
    try {
      const response = await adminApi.get(`/product-variants/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createProductVariant(variantData) {
    try {
      const response = await adminApi.post('/product-variants', variantData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createMultipleProductVariants(variantList) {
  try {
    const response = await adminApi.post('/product-variants/batch', variantList);
    return response.data;
  } catch (error) {
    throw this.handleError(error);
  }
}

  async updateProductVariant(id, variantData) {
    try {
      const response = await adminApi.put(`/product-variants/${id}`, variantData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteProductVariant(id) {
    try {
      const response = await adminApi.delete(`/product-variants/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateVariantStock(id, stockData) {
    try {
      const response = await adminApi.put(`/product-variants/${id}/stock`, stockData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== ACTIVITY LOGS =====
  async getActivityLogs(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/activity-logs?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async exportActivityLogs(format = 'csv', filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        format,
        ...cleanFilters
      })
      const response = await adminApi.get(`/activity-logs/export?${params}`, {
        responseType: 'blob'
      })
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== EMAIL TEMPLATES =====
  async getEmailTemplates(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/email-templates?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getEmailTemplateById(id) {
    try {
      const response = await adminApi.get(`/email-templates/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async createEmailTemplate(templateData) {
    try {
      const response = await adminApi.post('/email-templates', templateData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateEmailTemplate(id, templateData) {
    try {
      const response = await adminApi.put(`/email-templates/${id}`, templateData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async deleteEmailTemplate(id) {
    try {
      const response = await adminApi.delete(`/email-templates/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async sendTestEmail(id, testData) {
    try {
      const response = await adminApi.post(`/email-templates/${id}/test`, testData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== INVENTORY =====
  async getInventory(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/inventory?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getInventoryLogs(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/inventory/logs?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async adjustStock(adjustmentData) {
    try {
      const response = await adminApi.post('/inventory/adjust', adjustmentData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== LOYALTY =====
  async getLoyaltyUsers(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/loyalty/users?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async adjustLoyaltyPoints(userId, adjustmentData) {
    try {
      const response = await adminApi.post(`/loyalty/users/${userId}/adjust`, adjustmentData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getLoyaltyStats() {
    try {
      const response = await adminApi.get('/loyalty/stats')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== PAYMENTS =====
  async getPayments(page = 0, size = 10, filters = {}) {
    try {
      const cleanFilters = Object.entries(filters).reduce((acc, [key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          acc[key] = value
        }
        return acc
      }, {})
      
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...cleanFilters
      })
      const response = await adminApi.get(`/payments?${params}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getPaymentById(id) {
    try {
      const response = await adminApi.get(`/payments/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async refundPayment(id, refundData) {
    try {
      const response = await adminApi.post(`/payments/${id}/refund`, refundData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getPaymentStats() {
    try {
      const response = await adminApi.get('/payments/stats')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== ANALYTICS (Bổ sung) =====
  async getProductAnalytics(period = '30d') {
    try {
      const response = await adminApi.get(`/analytics/products?period=${period}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async getCustomerAnalytics(period = '30d') {
    try {
      const response = await adminApi.get(`/analytics/customers?period=${period}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== SETTINGS =====
  async getSettings() {
    try {
      const response = await adminApi.get('/settings')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  async updateSettings(settingsData) {
    try {
      const response = await adminApi.put('/settings', settingsData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== POS / SALES =====
  async createPOSOrder(orderData) {
    try {
      const response = await adminApi.post('/pos/orders', orderData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  // ===== UTILITY METHODS =====
  handleError(error) {
    if (error.response) {
      // Server responded with error status
      return {
        message: error.response.data?.message || 'Có lỗi xảy ra từ server',
        status: error.response.status,
        data: error.response.data
      }
    } else if (error.request) {
      // Request was made but no response received
      return {
        message: 'Không thể kết nối đến server',
        status: 0
      }
    } else {
      // Something else happened
      return {
        message: error.message || 'Có lỗi không xác định',
        status: -1
      }
    }
  }
}

export default new AdminService()

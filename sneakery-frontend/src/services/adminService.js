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
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...filters
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
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...filters
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
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...filters
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

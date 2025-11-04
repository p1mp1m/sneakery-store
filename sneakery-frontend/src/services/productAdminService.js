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

/**
 * Product Admin Service
 * 
 * Service riêng cho quản lý sản phẩm (admin)
 * Tách ra từ adminService.js để code dễ maintain hơn
 */
class ProductAdminService {
  
  /**
   * Lấy danh sách sản phẩm với phân trang và filters
   * 
   * @param {number} page - Số trang (bắt đầu từ 0)
   * @param {number} size - Số items mỗi trang
   * @param {Object} filters - Object chứa các filter options
   * @returns {Promise<Object>} Page response với content và pagination info
   */
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

  /**
   * Lấy chi tiết 1 sản phẩm theo ID
   * 
   * @param {number|string} id - ID sản phẩm
   * @returns {Promise<Object>} Product detail DTO
   */
  async getProductById(id) {
    try {
      const response = await adminApi.get(`/products/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  /**
   * Tạo sản phẩm mới
   * 
   * @param {Object} productData - DTO chứa thông tin sản phẩm
   * @returns {Promise<Object>} Product detail DTO của sản phẩm vừa tạo
   */
  async createProduct(productData) {
    try {
      const response = await adminApi.post('/products', productData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  /**
   * Cập nhật sản phẩm
   * 
   * @param {number|string} id - ID sản phẩm
   * @param {Object} productData - DTO chứa thông tin mới
   * @returns {Promise<Object>} Product detail DTO đã cập nhật
   */
  async updateProduct(id, productData) {
    try {
      const response = await adminApi.put(`/products/${id}`, productData)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  /**
   * Xóa sản phẩm
   * 
   * @param {number|string} id - ID sản phẩm
   * @returns {Promise<boolean>} true nếu xóa thành công
   */
  async deleteProduct(id) {
    try {
      const response = await adminApi.delete(`/products/${id}`)
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  /**
   * Import sản phẩm từ Excel
   * 
   * @param {Array} productList - Danh sách sản phẩm đã parse từ Excel
   * @returns {Promise<Object>} Import result với success/error count
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
   * 
   * @param {Object} bulkData - { productIds: [], action: '', isActive/brandId/categoryId }
   * @returns {Promise<Object>} Bulk update result
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
   * 
   * @param {number|string} productId - ID sản phẩm cần nhân bản
   * @returns {Promise<Object>} Product detail DTO của sản phẩm mới
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
   * 
   * @returns {Promise<Object>} Product statistics DTO
   */
  async getProductStatistics() {
    try {
      const response = await adminApi.get('/products/statistics')
      return response.data
    } catch (error) {
      throw this.handleError(error)
    }
  }

  /**
   * Upload ảnh sản phẩm
   * 
   * @param {number|string} productId - ID sản phẩm
   * @param {File} file - File ảnh cần upload
   * @returns {Promise<Object>} Product image DTO
   */
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

  /**
   * Error handler
   */
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

export default new ProductAdminService()


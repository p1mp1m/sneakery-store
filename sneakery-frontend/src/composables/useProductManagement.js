/**
 * useProductManagement Composable
 * 
 * Tách logic quản lý sản phẩm (CRUD operations) từ AdminProducts.vue
 * Sử dụng productAdminService để gọi API
 */

import { ref, computed } from 'vue'
import notificationService from '@/utils/notificationService'
import productAdminService from '@/services/productAdminService'
import { LOW_STOCK_THRESHOLD } from '@/utils/productConstants'

export function useProductManagement() {
  // State
  const products = ref([])
  const loading = ref(false)
  const totalItems = ref(0)
  const currentPage = ref(0)
  const pageSize = ref(10)
  const statistics = ref(null)
  const statisticsLoading = ref(false)

  // Selected products for bulk operations
  const selectedProducts = ref([])

  /**
   * Fetch products with pagination and filters
   * 
   * @param {Object} filters - Filter options (search, brandId, categoryId, etc.)
   * @param {number} page - Page number (0-based)
   * @param {number} size - Page size
   */
  const fetchProducts = async (filters = {}, page = null, size = null) => {
    loading.value = true
    try {
      const pageNum = page !== null ? page : currentPage.value
      const pageSizeNum = size !== null ? size : pageSize.value
      
      const response = await productAdminService.getProducts(pageNum, pageSizeNum, filters)
      
      products.value = response.content || []
      totalItems.value = response.totalElements || 0
      currentPage.value = response.number || 0
      pageSize.value = response.size || 10
    } catch (error) {
      console.error('Error fetching products:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi tải danh sách sản phẩm')
      products.value = []
      totalItems.value = 0
    } finally {
      loading.value = false
    }
  }

  /**
   * Fetch product statistics
   */
  const fetchStatistics = async () => {
    statisticsLoading.value = true
    try {
      statistics.value = await productAdminService.getProductStatistics()
    } catch (error) {
      console.error('Error fetching statistics:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi tải thống kê')
    } finally {
      statisticsLoading.value = false
    }
  }

  /**
   * Get product by ID
   * 
   * @param {number|string} id - Product ID
   * @returns {Promise<Object>} Product detail
   */
  const getProductById = async (id) => {
    try {
      return await productAdminService.getProductById(id)
    } catch (error) {
      console.error('Error fetching product:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi tải chi tiết sản phẩm')
      throw error
    }
  }

  /**
   * Create new product
   * 
   * @param {Object} productData - Product data
   * @returns {Promise<Object>} Created product
   */
  const createProduct = async (productData) => {
    try {
      const product = await productAdminService.createProduct(productData)
      notificationService.success('Thành công', 'Tạo sản phẩm mới thành công!')
      return product
    } catch (error) {
      console.error('Error creating product:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi tạo sản phẩm')
      throw error
    }
  }

  /**
   * Update product
   * 
   * @param {number|string} id - Product ID
   * @param {Object} productData - Updated product data
   * @returns {Promise<Object>} Updated product
   */
  const updateProduct = async (id, productData) => {
    try {
      const product = await productAdminService.updateProduct(id, productData)
      notificationService.success('Thành công', 'Cập nhật sản phẩm thành công!')
      return product
    } catch (error) {
      console.error('Error updating product:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi cập nhật sản phẩm')
      throw error
    }
  }

  /**
   * Delete product
   * 
   * @param {number|string} id - Product ID
   * @returns {Promise<boolean>} Success status
   */
  const deleteProduct = async (id) => {
    try {
      await productAdminService.deleteProduct(id)
      notificationService.success('Thành công', 'Xóa sản phẩm thành công!')
      return true
    } catch (error) {
      console.error('Error deleting product:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi xóa sản phẩm')
      throw error
    }
  }

  /**
   * Duplicate product
   * 
   * @param {number|string} productId - Product ID to duplicate
   * @returns {Promise<Object>} Duplicated product
   */
  const duplicateProduct = async (productId) => {
    try {
      const product = await productAdminService.duplicateProduct(productId)
      notificationService.success('Thành công', 'Nhân bản sản phẩm thành công!')
      return product
    } catch (error) {
      console.error('Error duplicating product:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi nhân bản sản phẩm')
      throw error
    }
  }

  /**
   * Bulk update products
   * 
   * @param {Object} bulkData - { productIds: [], action: '', ... }
   * @returns {Promise<Object>} Bulk update result
   */
  const bulkUpdateProducts = async (bulkData) => {
    try {
      const result = await productAdminService.bulkUpdateProducts(bulkData)
      notificationService.success('Thành công', `Cập nhật hàng loạt thành công! (${result.successCount || 0} sản phẩm)`)
      return result
    } catch (error) {
      console.error('Error bulk updating products:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi cập nhật hàng loạt')
      throw error
    }
  }

  /**
   * Import products from Excel
   * 
   * @param {Array} productList - List of products to import
   * @returns {Promise<Object>} Import result
   */
  const importProducts = async (productList) => {
    try {
      const result = await productAdminService.importProducts(productList)
      notificationService.success('Thành công', `Import thành công! (${result.successCount || 0}/${result.totalCount || 0})`)
      return result
    } catch (error) {
      console.error('Error importing products:', error)
      notificationService.error('Lỗi', error.message || 'Lỗi khi import sản phẩm')
      throw error
    }
  }

  // Computed
  const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

  const isAllSelected = computed(() => {
    return products.value.length > 0 && selectedProducts.value.length === products.value.length
  })

  /**
   * Toggle select all products
   */
  const toggleSelectAll = () => {
    if (isAllSelected.value) {
      selectedProducts.value = []
    } else {
      selectedProducts.value = products.value.map(p => p.id)
    }
  }

  /**
   * Toggle select single product
   * 
   * @param {number|string} productId - Product ID
   */
  const toggleSelectProduct = (productId) => {
    const index = selectedProducts.value.indexOf(productId)
    if (index > -1) {
      selectedProducts.value.splice(index, 1)
    } else {
      selectedProducts.value.push(productId)
    }
  }

  /**
   * Stock helper functions
   */
  const getStockClass = (product) => {
    const totalStock = product.totalStock || product.variantCount || 0
    if (totalStock === 0) return 'out-of-stock'
    if (totalStock <= LOW_STOCK_THRESHOLD) return 'low-stock'
    return 'in-stock'
  }

  const getStockIcon = (product) => {
    const stockClass = getStockClass(product)
    if (stockClass === 'out-of-stock') return 'remove_shopping_cart'
    if (stockClass === 'low-stock') return 'warning'
    return 'check_circle'
  }

  const getStockText = (product) => {
    const stockClass = getStockClass(product)
    if (stockClass === 'out-of-stock') return 'Hết hàng'
    if (stockClass === 'low-stock') return 'Sắp hết'
    return 'Còn hàng'
  }

  return {
    // State
    products,
    loading,
    totalItems,
    currentPage,
    pageSize,
    statistics,
    statisticsLoading,
    selectedProducts,

    // Computed
    totalPages,
    isAllSelected,

    // Methods
    fetchProducts,
    fetchStatistics,
    getProductById,
    createProduct,
    updateProduct,
    deleteProduct,
    duplicateProduct,
    bulkUpdateProducts,
    importProducts,
    toggleSelectAll,
    toggleSelectProduct,
    getStockClass,
    getStockIcon,
    getStockText
  }
}


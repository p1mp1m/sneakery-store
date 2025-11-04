/**
 * Product Constants for Frontend
 * 
 * Chứa các constants được sử dụng trong module quản lý sản phẩm
 */

/**
 * Low stock threshold - Số lượng tồn kho tối thiểu để cảnh báo
 */
export const LOW_STOCK_THRESHOLD = 10

/**
 * Default page size cho pagination
 */
export const DEFAULT_PAGE_SIZE = 10

/**
 * Maximum number of images per product
 */
export const MAX_IMAGES_PER_PRODUCT = 10

/**
 * Maximum file size for image upload (5MB in bytes)
 */
export const MAX_IMAGE_FILE_SIZE = 5 * 1024 * 1024

/**
 * Allowed image file types
 */
export const ALLOWED_IMAGE_TYPES = [
  'image/jpeg',
  'image/jpg',
  'image/png',
  'image/webp'
]

/**
 * Allowed image file extensions
 */
export const ALLOWED_IMAGE_EXTENSIONS = ['.jpg', '.jpeg', '.png', '.webp']

/**
 * Stock level constants
 */
export const StockLevel = {
  IN_STOCK: 'in_stock',
  LOW_STOCK: 'low_stock',
  OUT_OF_STOCK: 'out_of_stock',
  ALL: 'all'
}

/**
 * Status constants
 */
export const Status = {
  ACTIVE: 'active',
  INACTIVE: 'inactive',
  ALL: 'all'
}

/**
 * Sort field constants
 */
export const SortField = {
  NAME: 'name',
  PRICE: 'price',
  STOCK: 'stock',
  ID: 'id',
  CREATED_AT: 'created_at'
}

/**
 * Sort direction constants
 */
export const SortDirection = {
  ASC: 'asc',
  DESC: 'desc'
}

/**
 * Bulk update action constants
 */
export const BulkUpdateAction = {
  UPDATE_STATUS: 'UPDATE_STATUS',
  UPDATE_BRAND: 'UPDATE_BRAND',
  ADD_CATEGORY: 'ADD_CATEGORY',
  REMOVE_CATEGORY: 'REMOVE_CATEGORY'
}

/**
 * Debounce delay for search (ms)
 */
export const SEARCH_DEBOUNCE_DELAY = 500


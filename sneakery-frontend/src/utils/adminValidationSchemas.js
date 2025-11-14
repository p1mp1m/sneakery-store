/**
 * Admin Validation Schemas
 * Đồng bộ validation rules với backend DTOs
 * 
 * Sử dụng với useFormValidation composable
 */

import { useFormValidation } from '@/composables/useFormValidation'

/**
 * Validation schema cho Product (AdminProductRequestDto)
 */
export const productValidationSchema = {
  brandId: [
    (value) => {
      if (!value || value === null || value === undefined) {
        return 'ID thương hiệu không được để trống'
      }
      return null
    }
  ],
  name: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Tên sản phẩm không được để trống'
      }
      return null
    }
  ],
  slug: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Slug không được để trống'
      }
      // Slug chỉ được chứa chữ thường, số và dấu gạch ngang
      const slugRegex = /^[a-z0-9-]+$/
      if (!slugRegex.test(value)) {
        return 'Slug chỉ được chứa chữ thường, số và dấu gạch ngang'
      }
      return null
    }
  ],
  isActive: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Trạng thái active không được để trống'
      }
      return null
    }
  ],
  categoryIds: [
    (value) => {
      if (!value || !Array.isArray(value) || value.length === 0) {
        return 'Sản phẩm phải thuộc ít nhất 1 danh mục'
      }
      return null
    }
  ],
  variants: [
    (value) => {
      if (!value || !Array.isArray(value) || value.length === 0) {
        return 'Sản phẩm phải có ít nhất 1 biến thể'
      }
      return null
    }
  ]
}

/**
 * Validation schema cho Product Variant (AdminVariantRequestDto)
 */
export const variantValidationSchema = {
  sku: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'SKU không được để trống'
      }
      return null
    }
  ],
  size: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Size không được để trống'
      }
      return null
    }
  ],
  color: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Màu không được để trống'
      }
      return null
    }
  ],
  price: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Giá gốc không được để trống'
      }
      const num = Number(value)
      if (isNaN(num) || num < 0) {
        return 'Giá gốc phải >= 0'
      }
      return null
    }
  ],
  stockQuantity: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Số lượng tồn kho không được để trống'
      }
      const num = Number(value)
      if (isNaN(num) || num < 0) {
        return 'Số lượng tồn kho phải >= 0'
      }
      return null
    }
  ]
}

/**
 * Validation schema cho User (CreateUserRequestDto)
 */
export const userValidationSchema = {
  email: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Email không được để trống'
      }
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(value)) {
        return 'Email không hợp lệ'
      }
      return null
    }
  ],
  password: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Mật khẩu không được để trống'
      }
      if (value.length < 6) {
        return 'Mật khẩu phải có ít nhất 6 ký tự'
      }
      return null
    }
  ],
  fullName: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Họ tên không được để trống'
      }
      if (value.trim().length < 2 || value.trim().length > 100) {
        return 'Họ tên phải từ 2 đến 100 ký tự'
      }
      return null
    }
  ],
  role: [
    (value) => {
      if (!value || value === null || value === undefined) {
        return 'Role không được để trống'
      }
      return null
    }
  ]
}

/**
 * Validation schema cho Coupon (CouponDto)
 */
export const couponValidationSchema = {
  code: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Mã coupon không được để trống'
      }
      return null
    }
  ],
  discountType: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Loại giảm giá không được để trống'
      }
      if (value !== 'percent' && value !== 'fixed') {
        return 'Loại giảm giá phải là "percent" hoặc "fixed"'
      }
      return null
    }
  ],
  value: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Giá trị giảm không được để trống'
      }
      const num = Number(value)
      if (isNaN(num) || num <= 0) {
        return 'Giá trị giảm phải > 0'
      }
      return null
    }
  ],
  startAt: [
    (value) => {
      if (!value || value === null || value === undefined) {
        return 'Ngày bắt đầu không được để trống'
      }
      return null
    }
  ],
  endAt: [
    (value) => {
      if (!value || value === null || value === undefined) {
        return 'Ngày kết thúc không được để trống'
      }
      return null
    }
  ]
}

/**
 * Validation schema cho Flash Sale (FlashSaleDto)
 */
export const flashSaleValidationSchema = {
  productId: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Product ID không được để trống'
      }
      return null
    }
  ],
  discountPercent: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Phần trăm giảm giá không được để trống'
      }
      const num = Number(value)
      if (isNaN(num) || num < 1) {
        return 'Phần trăm giảm giá phải >= 1'
      }
      if (num > 100) {
        return 'Phần trăm giảm giá phải <= 100'
      }
      return null
    }
  ],
  startTime: [
    (value) => {
      if (!value || value === null || value === undefined) {
        return 'Thời gian bắt đầu không được để trống'
      }
      return null
    }
  ],
  endTime: [
    (value) => {
      if (!value || value === null || value === undefined) {
        return 'Thời gian kết thúc không được để trống'
      }
      return null
    }
  ]
}

/**
 * Validation schema cho Brand (BrandDto)
 */
export const brandValidationSchema = {
  name: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Tên thương hiệu không được để trống'
      }
      return null
    }
  ],
  slug: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Slug không được để trống'
      }
      const slugRegex = /^[a-z0-9-]+$/
      if (!slugRegex.test(value)) {
        return 'Slug chỉ được chứa chữ thường, số và dấu gạch ngang'
      }
      return null
    }
  ]
}

/**
 * Validation schema cho Category
 */
export const categoryValidationSchema = {
  name: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Tên danh mục không được để trống'
      }
      return null
    }
  ],
  slug: [
    (value) => {
      if (!value || value.trim() === '') {
        return 'Slug không được để trống'
      }
      const slugRegex = /^[a-z0-9-]+$/
      if (!slugRegex.test(value)) {
        return 'Slug chỉ được chứa chữ thường, số và dấu gạch ngang'
      }
      return null
    }
  ]
}

/**
 * Validation schema cho POS Order Item (POSOrderItemDto)
 */
export const posOrderItemValidationSchema = {
  variantId: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Product Variant ID không được để trống'
      }
      return null
    }
  ],
  quantity: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Số lượng không được để trống'
      }
      const num = Number(value)
      if (isNaN(num) || num < 1) {
        return 'Số lượng phải lớn hơn 0'
      }
      return null
    }
  ],
  unitPrice: [
    (value) => {
      if (value === null || value === undefined) {
        return 'Giá đơn vị không được để trống'
      }
      const num = Number(value)
      if (isNaN(num) || num < 0) {
        return 'Giá đơn vị phải >= 0'
      }
      return null
    }
  ]
}

/**
 * Helper function để validate form với schema
 * @param {Object} formData - Form data to validate
 * @param {Object} schema - Validation schema
 * @returns {Promise<{isValid: boolean, errors: Object}>}
 */
export async function validateWithSchema(formData, schema) {
  const { validateForm, errors } = useFormValidation()
  const isValid = await validateForm(formData, schema)
  return { isValid, errors: errors.value }
}


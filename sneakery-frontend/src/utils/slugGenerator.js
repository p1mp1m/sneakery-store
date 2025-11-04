/**
 * Slug Generator Utility
 * 
 * Tạo slug từ tên (chuyển đổi tiếng Việt có dấu thành slug không dấu)
 * Sử dụng ở nhiều nơi: Product, Brand, Category, Material, ShoeSole
 * 
 * @example
 * generateSlug("Nike Air Force 1") // "nike-air-force-1"
 * generateSlug("Giày thể thao") // "giay-the-thao"
 */

/**
 * Tạo slug từ tên
 * 
 * @param {string} name - Tên cần chuyển thành slug
 * @param {boolean} allowEdit - Nếu true, cho phép chỉnh sửa slug (không tự động tạo)
 * @returns {string} Slug đã được normalize
 */
export function generateSlug(name) {
  if (!name || typeof name !== 'string') {
    return ''
  }

  return name
    .toLowerCase()
    .normalize('NFD') // Normalize để tách dấu
    .replace(/[\u0300-\u036f]/g, '') // Loại bỏ dấu (accent marks)
    .replace(/đ/g, 'd') // Thay đ → d
    .replace(/Đ/g, 'd') // Thay Đ → d
    .replace(/[^a-z0-9\s-]/g, '') // Chỉ giữ chữ thường, số, khoảng trắng, dấu gạch
    .replace(/\s+/g, '-') // Thay khoảng trắng bằng dấu gạch
    .replace(/-+/g, '-') // Gộp nhiều dấu gạch liên tiếp thành 1
    .replace(/^-+|-+$/g, '') // Xóa dấu gạch ở đầu và cuối
    .trim()
}

/**
 * Validate slug format
 * 
 * @param {string} slug - Slug cần validate
 * @returns {Object} { valid: boolean, error: string }
 */
export function validateSlug(slug) {
  if (!slug || typeof slug !== 'string') {
    return { valid: false, error: 'Slug không được để trống' }
  }

  const trimmed = slug.trim()

  if (trimmed.length === 0) {
    return { valid: false, error: 'Slug không được để trống' }
  }

  if (trimmed.length < 2) {
    return { valid: false, error: 'Slug phải có ít nhất 2 ký tự' }
  }

  if (trimmed.length > 255) {
    return { valid: false, error: 'Slug không được vượt quá 255 ký tự' }
  }

  // Chỉ cho phép chữ thường, số, dấu gạch ngang
  if (!/^[a-z0-9-]+$/.test(trimmed)) {
    return { 
      valid: false, 
      error: 'Slug chỉ được chứa chữ thường, số và dấu gạch ngang' 
    }
  }

  // Không được bắt đầu hoặc kết thúc bằng dấu gạch
  if (trimmed.startsWith('-') || trimmed.endsWith('-')) {
    return { 
      valid: false, 
      error: 'Slug không được bắt đầu hoặc kết thúc bằng dấu gạch ngang' 
    }
  }

  return { valid: true, error: null }
}

/**
 * Tạo slug unique bằng cách thêm suffix
 * 
 * @param {string} baseSlug - Slug gốc
 * @param {number} suffix - Số suffix (mặc định timestamp)
 * @returns {string} Slug với suffix
 */
export function generateUniqueSlug(baseSlug, suffix = null) {
  const suffixValue = suffix || Date.now()
  return `${baseSlug}-${suffixValue}`
}


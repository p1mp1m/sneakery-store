/**
 * Cloudinary Helper Utilities
 * 
 * Helper functions để optimize và transform Cloudinary image URLs
 */

/**
 * Kiểm tra xem URL có phải là Cloudinary URL không
 * 
 * @param {string} url - URL cần kiểm tra
 * @returns {boolean}
 */
export function isCloudinaryUrl(url) {
  if (!url || typeof url !== 'string') return false
  return url.includes('cloudinary.com')
}

/**
 * Extract public ID từ Cloudinary URL
 * 
 * @param {string} url - Cloudinary URL
 * @returns {string|null} Public ID hoặc null
 */
export function extractPublicIdFromUrl(url) {
  if (!isCloudinaryUrl(url)) return null

  try {
    const uploadIndex = url.indexOf('/upload/')
    if (uploadIndex === -1) return null

    let afterUpload = url.substring(uploadIndex + '/upload/'.length)
    // Bỏ version vXXXXX/ nếu có
    afterUpload = afterUpload.replace(/^v\d+\//, '')
    // Bỏ transformations nếu có (w_300,h_300,c_limit,q_auto,f_auto/)
    afterUpload = afterUpload.replace(/^[^/]+\//, '')
    // Bỏ extension
    afterUpload = afterUpload.replace(/\.[^.]+$/, '')
    return afterUpload
  } catch (error) {
    console.warn('⚠️ Không thể extract public ID từ URL:', url, error)
    return null
  }
}

/**
 * Tạo optimized URL với transformations
 * 
 * @param {string} originalUrl - URL gốc
 * @param {Object} options - Transformation options
 * @param {number|null} options.width - Chiều rộng (px)
 * @param {number|null} options.height - Chiều cao (px)
 * @param {string|null} options.crop - Loại crop: "limit", "fill", "fit", "scale", "thumb"
 * @param {string|null} options.quality - Chất lượng: "auto", "80", "90", "best"
 * @param {string|null} options.format - Định dạng: "auto", "webp", "jpg", "png"
 * @returns {string} Optimized URL hoặc original URL
 */
export function getOptimizedImageUrl(originalUrl, options = {}) {
  if (!originalUrl || !isCloudinaryUrl(originalUrl)) {
    return originalUrl // Return original nếu không phải Cloudinary
  }

  const {
    width = null,
    height = null,
    crop = 'limit',
    quality = 'auto',
    format = 'auto'
  } = options

  try {
    // Tìm vị trí /upload/ trong URL
    const uploadIndex = originalUrl.indexOf('/upload/')
    if (uploadIndex === -1) return originalUrl

    const baseUrl = originalUrl.substring(0, uploadIndex + '/upload/'.length)
    let afterUpload = originalUrl.substring(uploadIndex + '/upload/'.length)

    // Bỏ transformations cũ nếu có
    afterUpload = afterUpload.replace(/^[^/]+\//, '')

    // Build transformation string
    const transformations = []
    if (width) transformations.push(`w_${width}`)
    if (height) transformations.push(`h_${height}`)
    if (crop) transformations.push(`c_${crop}`)
    if (quality) transformations.push(`q_${quality}`)
    if (format) transformations.push(`f_${format}`)

    const transformationString = transformations.length > 0
      ? transformations.join(',') + '/'
      : ''

    return `${baseUrl}${transformationString}${afterUpload}`
  } catch (error) {
    console.warn('⚠️ Không thể tạo optimized URL:', error)
    return originalUrl
  }
}

/**
 * Tạo thumbnail URL (300x300)
 * 
 * @param {string} originalUrl - URL gốc
 * @returns {string} Thumbnail URL
 */
export function getThumbnailUrl(originalUrl) {
  return getOptimizedImageUrl(originalUrl, {
    width: 300,
    height: 300,
    crop: 'limit',
    quality: 'auto',
    format: 'auto'
  })
}

/**
 * Tạo medium size URL (800x800)
 * 
 * @param {string} originalUrl - URL gốc
 * @returns {string} Medium URL
 */
export function getMediumUrl(originalUrl) {
  return getOptimizedImageUrl(originalUrl, {
    width: 800,
    height: 800,
    crop: 'limit',
    quality: 'auto',
    format: 'auto'
  })
}

/**
 * Tạo large size URL (1200x1200)
 * 
 * @param {string} originalUrl - URL gốc
 * @returns {string} Large URL
 */
export function getLargeUrl(originalUrl) {
  return getOptimizedImageUrl(originalUrl, {
    width: 1200,
    height: 1200,
    crop: 'limit',
    quality: 'auto',
    format: 'auto'
  })
}

/**
 * Tạo responsive image srcset
 * 
 * @param {string} originalUrl - URL gốc
 * @param {number[]} sizes - Array các sizes: [300, 600, 1200]
 * @returns {string} Srcset string: "url1 300w, url2 600w, url3 1200w"
 */
export function generateSrcset(originalUrl, sizes = [300, 600, 1200]) {
  if (!isCloudinaryUrl(originalUrl)) {
    return originalUrl
  }

  const srcsetParts = sizes.map(size => {
    const optimizedUrl = getOptimizedImageUrl(originalUrl, {
      width: size,
      height: size,
      crop: 'limit',
      quality: 'auto',
      format: 'auto'
    })
    return `${optimizedUrl} ${size}w`
  })

  return srcsetParts.join(', ')
}

/**
 * Tạo sizes attribute cho responsive images
 * 
 * @param {Object} breakpoints - Breakpoints: { mobile: 300, tablet: 600, desktop: 1200 }
 * @returns {string} Sizes string
 */
export function generateSizes(breakpoints = { mobile: 300, tablet: 600, desktop: 1200 }) {
  const { mobile = 300, tablet = 600, desktop = 1200 } = breakpoints
  return `(max-width: 600px) ${mobile}px, (max-width: 1200px) ${tablet}px, ${desktop}px`
}

/**
 * Tạo placeholder image URL (blur, low quality)
 * 
 * @param {string} originalUrl - URL gốc
 * @returns {string} Placeholder URL
 */
export function getPlaceholderUrl(originalUrl) {
  return getOptimizedImageUrl(originalUrl, {
    width: 20,
    height: 20,
    crop: 'limit',
    quality: 'auto:low',
    format: 'auto'
  })
}

/**
 * Tạo blur-up placeholder (dùng cho progressive loading)
 * 
 * @param {string} originalUrl - URL gốc
 * @returns {string} Blur placeholder URL
 */
export function getBlurPlaceholderUrl(originalUrl) {
  return getOptimizedImageUrl(originalUrl, {
    width: 100,
    height: 100,
    crop: 'limit',
    quality: 'auto:low',
    format: 'auto',
    effect: 'blur:1000' // Blur effect
  })
}

/**
 * Validate Cloudinary URL format
 * 
 * @param {string} url - URL cần validate
 * @returns {boolean}
 */
export function validateCloudinaryUrl(url) {
  if (!url || typeof url !== 'string') return false
  return isCloudinaryUrl(url) && url.startsWith('https://')
}

/**
 * Get image dimensions từ Cloudinary URL (nếu có trong URL)
 * 
 * @param {string} url - Cloudinary URL
 * @returns {Object|null} { width, height } hoặc null
 */
export function getImageDimensions(url) {
  if (!isCloudinaryUrl(url)) return null

  try {
    const uploadIndex = url.indexOf('/upload/')
    if (uploadIndex === -1) return null

    const afterUpload = url.substring(uploadIndex + '/upload/'.length)
    const transformationMatch = afterUpload.match(/w_(\d+),h_(\d+)/)
    
    if (transformationMatch) {
      return {
        width: parseInt(transformationMatch[1], 10),
        height: parseInt(transformationMatch[2], 10)
      }
    }
  } catch (error) {
    console.warn('⚠️ Không thể lấy dimensions từ URL:', error)
  }

  return null
}

/**
 * Format image URL với fallback
 * 
 * @param {string|null|undefined} imageUrl - Image URL
 * @param {string} fallback - Fallback URL (default: '/placeholder-image.png')
 * @returns {string} Formatted URL
 */
export function formatImageUrl(imageUrl, fallback = '/placeholder-image.png') {
  if (!imageUrl || imageUrl.trim() === '') {
    return fallback
  }

  // Nếu là Cloudinary URL, return as-is
  if (isCloudinaryUrl(imageUrl)) {
    return imageUrl
  }

  // Nếu là relative path, thêm base URL nếu cần
  if (imageUrl.startsWith('/')) {
    return imageUrl
  }

  // Nếu là absolute URL, return as-is
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }

  // Fallback
  return fallback
}

export default {
  isCloudinaryUrl,
  extractPublicIdFromUrl,
  getOptimizedImageUrl,
  getThumbnailUrl,
  getMediumUrl,
  getLargeUrl,
  generateSrcset,
  generateSizes,
  getPlaceholderUrl,
  getBlurPlaceholderUrl,
  validateCloudinaryUrl,
  getImageDimensions,
  formatImageUrl
}


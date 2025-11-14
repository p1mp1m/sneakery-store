/**
 * Image optimization utilities
 * Provides functions to optimize image URLs for WebP, responsive images, and lazy loading
 */

/**
 * Convert image URL to WebP format
 * @param {string} url - Original image URL
 * @returns {string} WebP URL or original URL if conversion not possible
 */
export function toWebP(url) {
  if (!url) return url;
  
  // If URL already contains format parameter, replace it
  if (url.includes('format=')) {
    return url.replace(/format=[^&]+/, 'format=webp');
  }
  
  // If URL is from Cloudinary
  if (url.includes('cloudinary.com') || url.includes('res.cloudinary.com')) {
    // Cloudinary supports WebP via f_webp parameter
    const separator = url.includes('?') ? '&' : '?';
    return `${url}${separator}f_webp`;
  }
  
  // For other URLs, try appending format parameter
  const separator = url.includes('?') ? '&' : '?';
  return `${url}${separator}format=webp`;
}

/**
 * Generate responsive srcset for an image
 * @param {string} baseUrl - Base image URL
 * @param {number[]} widths - Array of widths for srcset
 * @returns {string} Srcset string
 */
export function generateSrcSet(baseUrl, widths = [320, 640, 768, 1024, 1280, 1920]) {
  if (!baseUrl) return '';
  
  // For Cloudinary URLs
  if (baseUrl.includes('cloudinary.com')) {
    return widths
      .map(width => {
        // Replace or add width parameter
        let widthUrl = baseUrl;
        if (widthUrl.includes('w_')) {
          widthUrl = widthUrl.replace(/w_\d+/, `w_${width}`);
        } else {
          const separator = widthUrl.includes('?') ? '&' : '?';
          widthUrl = `${widthUrl}${separator}w_${width}`;
        }
        return `${widthUrl} ${width}w`;
      })
      .join(', ');
  }
  
  // For URLs with width parameter
  if (baseUrl.includes('width=') || baseUrl.includes('w=')) {
    return widths
      .map(width => {
        const widthUrl = baseUrl.replace(/(width=|w=)\d+/, `$1${width}`);
        return `${widthUrl} ${width}w`;
      })
      .join(', ');
  }
  
  // Return empty if we can't generate srcset
  return '';
}

/**
 * Get optimized image URL with WebP support
 * @param {string} url - Original image URL
 * @param {Object} options - Optimization options
 * @param {boolean} options.webp - Enable WebP format (default: true)
 * @param {number} options.width - Desired width
 * @param {number} options.height - Desired height
 * @param {string} options.quality - Image quality (default: 'auto')
 * @returns {string} Optimized image URL
 */
export function getOptimizedImageUrl(url, options = {}) {
  if (!url) return url;
  
  const {
    webp = true,
    width = null,
    height = null,
    quality = 'auto'
  } = options;
  
  let optimizedUrl = url;
  
  // For Cloudinary URLs
  if (url.includes('cloudinary.com')) {
    const params = [];
    
    if (width) params.push(`w_${width}`);
    if (height) params.push(`h_${height}`);
    if (quality !== 'auto') params.push(`q_${quality}`);
    if (webp) params.push('f_webp');
    
    if (params.length > 0) {
      const separator = url.includes('?') ? '&' : '/';
      optimizedUrl = `${url}${separator}${params.join(',')}`;
    }
  } else {
    // For other URLs, append query parameters
    const params = new URLSearchParams();
    
    if (width) params.append('width', width);
    if (height) params.append('height', height);
    if (quality !== 'auto') params.append('quality', quality);
    if (webp) params.append('format', 'webp');
    
    if (params.toString()) {
      const separator = url.includes('?') ? '&' : '?';
      optimizedUrl = `${url}${separator}${params.toString()}`;
    }
  }
  
  return optimizedUrl;
}

/**
 * Check if browser supports WebP
 * @returns {Promise<boolean>} Promise that resolves to true if WebP is supported
 */
export function supportsWebP() {
  return new Promise((resolve) => {
    const webP = new Image();
    webP.onload = webP.onerror = () => {
      resolve(webP.height === 2);
    };
    webP.src = 'data:image/webp;base64,UklGRjoAAABXRUJQVlA4IC4AAACyAgCdASoCAAIALmk0mk0iIiIiIgBoSygABc6WWgAA/veff/0PP8bA//LwYAAA';
  });
}

/**
 * Get responsive sizes attribute
 * @param {Object} breakpoints - Breakpoint configuration
 * @returns {string} Sizes attribute string
 */
export function getResponsiveSizes(breakpoints = {
  mobile: 768,
  tablet: 1024,
  desktop: 1280
}) {
  return `(max-width: ${breakpoints.mobile}px) 100vw, (max-width: ${breakpoints.tablet}px) 50vw, (max-width: ${breakpoints.desktop}px) 33vw, 25vw`;
}

/**
 * Preload image
 * @param {string} url - Image URL to preload
 * @param {string} as - Resource type (default: 'image')
 */
export function preloadImage(url, as = 'image') {
  if (!url) return;
  
  const link = document.createElement('link');
  link.rel = 'preload';
  link.as = as;
  link.href = url;
  document.head.appendChild(link);
}

/**
 * Lazy load image using Intersection Observer
 * @param {HTMLElement} imgElement - Image element
 * @param {string} src - Image source URL
 * @param {Object} options - Observer options
 */
export function lazyLoadImage(imgElement, src, options = {}) {
  if (!imgElement || !src) return;
  
  const {
    root = null,
    rootMargin = '50px',
    threshold = 0.01
  } = options;
  
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          imgElement.src = src;
          imgElement.classList.add('loaded');
          observer.unobserve(imgElement);
        }
      });
    },
    { root, rootMargin, threshold }
  );
  
  observer.observe(imgElement);
  
  return () => observer.disconnect();
}


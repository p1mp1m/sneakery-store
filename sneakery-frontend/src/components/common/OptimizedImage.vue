<template>
  <picture>
    <!-- WebP source with fallback -->
    <source
      v-if="webpUrl"
      :srcset="webpSrcSet"
      type="image/webp"
      :sizes="sizes"
    />
    <!-- Fallback to original format -->
    <img
      :src="src"
      :srcset="srcSet"
      :sizes="sizes"
      :alt="alt"
      :loading="loading"
      :decoding="decoding"
      :class="imageClass"
      :style="imageStyle"
      @error="handleError"
      @load="handleLoad"
      ref="imgRef"
    />
  </picture>
</template>

<script setup>
/**
 * Optimized Image Component
 * Provides lazy loading, responsive images, and WebP format support
 * 
 * @example
 * ```vue
 * <OptimizedImage
 *   :src="product.imageUrl"
 *   :alt="product.name"
 *   :sizes="'(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw'"
 *   class="w-full h-full object-cover"
 * />
 * ```
 */

import { ref, computed, onMounted } from 'vue';

const props = defineProps({
  /**
   * Image source URL
   */
  src: {
    type: String,
    required: true
  },
  /**
   * Alt text for the image
   */
  alt: {
    type: String,
    default: ''
  },
  /**
   * Loading strategy: 'lazy' or 'eager'
   */
  loading: {
    type: String,
    default: 'lazy',
    validator: (value) => ['lazy', 'eager'].includes(value)
  },
  /**
   * Decoding strategy: 'async', 'sync', or 'auto'
   */
  decoding: {
    type: String,
    default: 'async',
    validator: (value) => ['async', 'sync', 'auto'].includes(value)
  },
  /**
   * Responsive image sizes attribute
   */
  sizes: {
    type: String,
    default: '(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw'
  },
  /**
   * Enable WebP format (requires backend support)
   */
  enableWebP: {
    type: Boolean,
    default: true
  },
  /**
   * Responsive breakpoints for srcset
   */
  breakpoints: {
    type: Array,
    default: () => [320, 640, 768, 1024, 1280, 1920]
  },
  /**
   * CSS classes for the image
   */
  imageClass: {
    type: String,
    default: ''
  },
  /**
   * Inline styles for the image
   */
  imageStyle: {
    type: Object,
    default: () => ({})
  },
  /**
   * Placeholder image URL while loading
   */
  placeholder: {
    type: String,
    default: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1 1"%3E%3C/svg%3E'
  }
});

const emit = defineEmits(['error', 'load']);

const imgRef = ref(null);
const isLoaded = ref(false);
const hasError = ref(false);

/**
 * Convert image URL to WebP format
 * @param {string} url - Original image URL
 * @returns {string} WebP URL or null if conversion not possible
 */
const convertToWebP = (url) => {
  if (!url || !props.enableWebP) return null;
  
  // If URL already contains format parameter, replace it
  if (url.includes('format=')) {
    return url.replace(/format=[^&]+/, 'format=webp');
  }
  
  // If URL is from Cloudinary or similar CDN
  if (url.includes('cloudinary.com') || url.includes('res.cloudinary.com')) {
    return url.replace(/\.(jpg|jpeg|png)/i, '.webp');
  }
  
  // For other URLs, try appending format parameter
  const separator = url.includes('?') ? '&' : '?';
  return `${url}${separator}format=webp`;
};

/**
 * Generate responsive srcset
 * @param {string} baseUrl - Base image URL
 * @param {boolean} isWebP - Whether to use WebP format
 * @returns {string} Srcset string
 */
const generateSrcSet = (baseUrl, isWebP = false) => {
  const url = isWebP ? convertToWebP(baseUrl) : baseUrl;
  if (!url) return '';
  
  // If URL contains width parameter, use it
  if (url.includes('w_') || url.includes('width=')) {
    return props.breakpoints
      .map(width => {
        const widthUrl = url.replace(/(w_|width=)\d+/, `$1${width}`);
        return `${widthUrl} ${width}w`;
      })
      .join(', ');
  }
  
  // For Cloudinary URLs
  if (url.includes('cloudinary.com')) {
    return props.breakpoints
      .map(width => {
        const widthUrl = url.replace(/w_\d+/, `w_${width}`) || `${url},w_${width}`;
        return `${widthUrl} ${width}w`;
      })
      .join(', ');
  }
  
  // For simple URLs, return empty (let browser handle it)
  return '';
};

/**
 * WebP URL
 */
const webpUrl = computed(() => {
  return convertToWebP(props.src);
});

/**
 * WebP srcset
 */
const webpSrcSet = computed(() => {
  return generateSrcSet(props.src, true);
});

/**
 * Original format srcset
 */
const srcSet = computed(() => {
  return generateSrcSet(props.src, false);
});

/**
 * Handle image load error
 */
const handleError = (event) => {
  hasError.value = true;
  emit('error', event);
  
  // Set placeholder if error
  if (imgRef.value && props.placeholder) {
    imgRef.value.src = props.placeholder;
  }
};

/**
 * Handle image load success
 */
const handleLoad = (event) => {
  isLoaded.value = true;
  emit('load', event);
};

onMounted(() => {
  // Preload if loading is eager
  if (props.loading === 'eager' && imgRef.value) {
    const link = document.createElement('link');
    link.rel = 'preload';
    link.as = 'image';
    link.href = props.src;
    document.head.appendChild(link);
  }
});
</script>

<style scoped>
picture {
  display: block;
  width: 100%;
  height: 100%;
}

img {
  display: block;
  max-width: 100%;
  height: auto;
}
</style>


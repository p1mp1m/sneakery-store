<template>
  <div :class="['lazy-image-container', containerClass]" :style="containerStyle">
    <!-- Loading Placeholder -->
    <div 
      v-if="!imageLoaded && !imageError" 
      :class="['lazy-image-placeholder', placeholderClass]"
      :style="placeholderStyle"
    >
      <!-- Blur placeholder image (nếu là Cloudinary) -->
      <img
        v-if="isCloudinaryUrl(src)"
        :src="placeholderSrc"
        :alt="alt"
        class="w-full h-full object-cover blur-sm opacity-50"
        aria-hidden="true"
      />
      <!-- Loading spinner -->
      <div class="absolute inset-0 flex items-center justify-center">
        <div class="animate-spin rounded-full border-2 border-gray-300 border-t-purple-600" :style="spinnerStyle"></div>
      </div>
    </div>
    
    <!-- Error State -->
    <div 
      v-if="imageError" 
      :class="['lazy-image-error', errorClass]"
      :style="errorStyle"
    >
      <div class="flex flex-col items-center justify-center w-full h-full text-gray-400">
        <i class="material-icons text-4xl mb-2">broken_image</i>
        <span class="text-xs">Không thể tải ảnh</span>
      </div>
    </div>
    
    <!-- Actual Image -->
    <img
      v-show="imageLoaded && !imageError"
      ref="imageRef"
      :src="imageSrc"
      :alt="alt"
      :class="['lazy-image', imageClass]"
      :style="imageStyle"
      @load="handleLoad"
      @error="handleError"
      loading="lazy"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { getOptimizedImageUrl, getPlaceholderUrl, formatImageUrl, isCloudinaryUrl } from '@/utils/cloudinaryHelper'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '/placeholder-image.png'
  },
  // Cloudinary optimization options
  width: {
    type: Number,
    default: null
  },
  height: {
    type: Number,
    default: null
  },
  quality: {
    type: String,
    default: 'auto'
  },
  format: {
    type: String,
    default: 'auto'
  },
  // Size presets
  size: {
    type: String,
    default: null, // 'thumbnail', 'medium', 'large'
    validator: (value) => !value || ['thumbnail', 'medium', 'large'].includes(value)
  },
  // Styling props
  containerClass: {
    type: String,
    default: ''
  },
  imageClass: {
    type: String,
    default: ''
  },
  placeholderClass: {
    type: String,
    default: 'bg-gray-100 dark:bg-gray-700'
  },
  errorClass: {
    type: String,
    default: 'bg-gray-100 dark:bg-gray-700'
  },
  containerStyle: {
    type: Object,
    default: () => ({})
  },
  imageStyle: {
    type: Object,
    default: () => ({})
  },
  placeholderStyle: {
    type: Object,
    default: () => ({})
  },
  errorStyle: {
    type: Object,
    default: () => ({})
  },
  // Spinner size
  spinnerSize: {
    type: Number,
    default: 32
  },
  // Intersection Observer options
  rootMargin: {
    type: String,
    default: '50px'
  },
  threshold: {
    type: Number,
    default: 0.01
  },
  // Immediate load (no lazy loading)
  immediate: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['load', 'error'])

const imageRef = ref(null)
const imageLoaded = ref(false)
const imageError = ref(false)
const isInView = ref(false)
const observer = ref(null)

const imageSrc = computed(() => {
  if (!props.src) return props.placeholder
  
  // Format URL với fallback
  const formattedUrl = formatImageUrl(props.src, props.placeholder)
  
  // Nếu chưa trong view, return placeholder
  if (!props.immediate && !isInView.value) {
    return props.placeholder
  }
  
  // Nếu là Cloudinary URL, optimize nó
  if (isCloudinaryUrl(formattedUrl)) {
    // Sử dụng size preset nếu có
    if (props.size === 'thumbnail') {
      return getOptimizedImageUrl(formattedUrl, { width: 300, height: 300, quality: props.quality, format: props.format })
    } else if (props.size === 'medium') {
      return getOptimizedImageUrl(formattedUrl, { width: 800, height: 800, quality: props.quality, format: props.format })
    } else if (props.size === 'large') {
      return getOptimizedImageUrl(formattedUrl, { width: 1200, height: 1200, quality: props.quality, format: props.format })
    } else if (props.width || props.height) {
      // Custom width/height
      return getOptimizedImageUrl(formattedUrl, {
        width: props.width,
        height: props.height,
        quality: props.quality,
        format: props.format
      })
    }
    // Default: optimize với auto quality và format
    return getOptimizedImageUrl(formattedUrl, { quality: props.quality, format: props.format })
  }
  
  // Không phải Cloudinary URL, return as-is
  return formattedUrl
})

const placeholderSrc = computed(() => {
  if (!props.src || !isCloudinaryUrl(props.src)) {
    return props.placeholder
  }
  // Tạo blur placeholder cho Cloudinary images
  return getPlaceholderUrl(props.src)
})

const spinnerStyle = computed(() => ({
  width: `${props.spinnerSize}px`,
  height: `${props.spinnerSize}px`
}))

const handleLoad = () => {
  imageLoaded.value = true
  imageError.value = false
  emit('load', props.src)
}

const handleError = () => {
  imageError.value = true
  imageLoaded.value = false
  emit('error', props.src)
}

const setupIntersectionObserver = () => {
  if (props.immediate) {
    isInView.value = true
    return
  }

  if (typeof IntersectionObserver === 'undefined') {
    // Fallback for browsers that don't support IntersectionObserver
    isInView.value = true
    return
  }

  observer.value = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          isInView.value = true
          // Disconnect observer once image is in view
          if (observer.value && imageRef.value) {
            observer.value.unobserve(imageRef.value)
          }
        }
      })
    },
    {
      rootMargin: props.rootMargin,
      threshold: props.threshold
    }
  )

  if (imageRef.value) {
    observer.value.observe(imageRef.value)
  }
}

const cleanupObserver = () => {
  if (observer.value && imageRef.value) {
    observer.value.unobserve(imageRef.value)
    observer.value.disconnect()
    observer.value = null
  }
}

watch(() => props.src, () => {
  // Reset state when src changes
  imageLoaded.value = false
  imageError.value = false
  isInView.value = false
  
  // Setup observer again if needed
  if (!props.immediate) {
    cleanupObserver()
    setupIntersectionObserver()
  } else {
    isInView.value = true
  }
})

onMounted(() => {
  setupIntersectionObserver()
})

onUnmounted(() => {
  cleanupObserver()
})
</script>

<style scoped>
.lazy-image-container {
  position: relative;
  overflow: hidden;
}

.lazy-image-placeholder,
.lazy-image-error {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lazy-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s ease-in-out;
}

.lazy-image[style*="opacity: 0"] {
  opacity: 0;
}

.lazy-image[style*="opacity: 1"] {
  opacity: 1;
}
</style>


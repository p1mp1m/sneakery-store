<template>
  <div class="image-upload">
    <!-- Preview Image -->
    <div v-if="preview || modelValue" class="image-preview">
      <img :src="preview || modelValue" :alt="alt" />
      <div class="preview-overlay">
        <button @click.prevent="handleRemove" class="btn-remove" type="button">
          <i class="material-icons">delete</i>
        </button>
        <button @click.prevent="triggerFileInput" class="btn-change" type="button">
          <i class="material-icons">edit</i>
        </button>
      </div>
    </div>

    <!-- Upload Area -->
    <div
      v-else
      class="upload-area"
      :class="{ 'drag-over': isDragging }"
      @click="triggerFileInput"
      @dragover.prevent="handleDragOver"
      @dragleave.prevent="handleDragLeave"
      @drop.prevent="handleDrop"
    >
      <i class="material-icons upload-icon">cloud_upload</i>
      <p class="upload-text">{{ uploadText }}</p>
      <p class="upload-hint">{{ uploadHint }}</p>
    </div>

    <!-- File Input (Hidden) -->
    <input
      ref="fileInput"
      type="file"
      accept="image/*"
      @change="handleFileSelect"
      style="display: none"
    />

    <!-- Error Message -->
    <p v-if="error" class="error-message">{{ error }}</p>

    <!-- Upload Progress -->
    <div v-if="uploading" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <p class="progress-text">Đang tải lên... {{ uploadProgress }}%</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  alt: {
    type: String,
    default: 'Image preview'
  },
  uploadText: {
    type: String,
    default: 'Nhấp hoặc kéo ảnh vào đây'
  },
  uploadHint: {
    type: String,
    default: 'Hỗ trợ: JPG, PNG, GIF (tối đa 5MB)'
  },
  maxSize: {
    type: Number,
    default: 5 * 1024 * 1024 // 5MB
  }
})

const emit = defineEmits(['update:modelValue', 'file-selected', 'file-removed'])

const fileInput = ref(null)
const preview = ref('')
const isDragging = ref(false)
const error = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)

// Watch for external changes to modelValue
watch(() => props.modelValue, (newValue) => {
  if (!newValue && preview.value) {
    preview.value = ''
  }
})

const triggerFileInput = () => {
  fileInput.value?.click()
}

const validateFile = (file) => {
  // Check file type
  if (!file.type.startsWith('image/')) {
    error.value = 'Vui lòng chọn file ảnh (JPG, PNG, GIF)'
    return false
  }

  // Check file size
  if (file.size > props.maxSize) {
    error.value = `Kích thước file vượt quá ${Math.round(props.maxSize / 1024 / 1024)}MB`
    return false
  }

  error.value = ''
  return true
}

const processFile = (file) => {
  if (!validateFile(file)) {
    return
  }

  // Create preview
  const reader = new FileReader()
  reader.onload = (e) => {
    preview.value = e.target.result
    emit('update:modelValue', e.target.result)
    emit('file-selected', file)
  }
  reader.readAsDataURL(file)

  // Simulate upload progress (for demo purposes)
  // In real app, this would be actual upload to server
  uploading.value = true
  uploadProgress.value = 0
  
  const interval = setInterval(() => {
    uploadProgress.value += 10
    if (uploadProgress.value >= 100) {
      clearInterval(interval)
      setTimeout(() => {
        uploading.value = false
        uploadProgress.value = 0
      }, 300)
    }
  }, 100)
}

const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (file) {
    processFile(file)
  }
}

const handleDragOver = () => {
  isDragging.value = true
}

const handleDragLeave = () => {
  isDragging.value = false
}

const handleDrop = (event) => {
  isDragging.value = false
  const file = event.dataTransfer.files?.[0]
  if (file) {
    processFile(file)
  }
}

const handleRemove = () => {
  preview.value = ''
  emit('update:modelValue', '')
  emit('file-removed')
  if (fileInput.value) {
    fileInput.value.value = ''
  }
  error.value = ''
}
</script>





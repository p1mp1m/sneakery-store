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

<style scoped>
.image-upload {
  width: 100%;
}

/* ===== PREVIEW ===== */
.image-preview {
  position: relative;
  width: 100%;
  max-width: 400px;
  aspect-ratio: 16 / 9;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border: 2px solid #e2e8f0;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-preview:hover .preview-overlay {
  opacity: 1;
}

.btn-remove,
.btn-change {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: none;
  background: white;
  color: #1e293b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-remove:hover {
  background: #ef4444;
  color: white;
  transform: scale(1.1);
}

.btn-change:hover {
  background: #3b82f6;
  color: white;
  transform: scale(1.1);
}

/* ===== UPLOAD AREA ===== */
.upload-area {
  width: 100%;
  max-width: 400px;
  aspect-ratio: 16 / 9;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #f8fafc;
  padding: 2rem;
}

.upload-area:hover {
  border-color: #3b82f6;
  background: #eff6ff;
}

.upload-area.drag-over {
  border-color: #3b82f6;
  background: #dbeafe;
  transform: scale(1.02);
}

.upload-icon {
  font-size: 4rem;
  color: #94a3b8;
  margin-bottom: 1rem;
}

.upload-text {
  font-size: 1rem;
  font-weight: 500;
  color: #475569;
  margin: 0 0 0.5rem 0;
}

.upload-hint {
  font-size: 0.875rem;
  color: #94a3b8;
  margin: 0;
  text-align: center;
}

/* ===== ERROR MESSAGE ===== */
.error-message {
  color: #ef4444;
  font-size: 0.875rem;
  margin: 0.75rem 0 0 0;
  font-weight: 500;
}

/* ===== UPLOAD PROGRESS ===== */
.upload-progress {
  margin-top: 1rem;
}

.progress-bar {
  width: 100%;
  max-width: 400px;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #2563eb);
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0.5rem 0 0 0;
  text-align: center;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 640px) {
  .image-preview,
  .upload-area {
    max-width: 100%;
  }

  .upload-area {
    padding: 1.5rem;
  }

  .upload-icon {
    font-size: 3rem;
  }
}
</style>


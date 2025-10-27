<template>
  <div class="upload-gallery">
    <!-- Upload zone -->
    <div
      class="upload-dropzone"
      @drop.prevent="handleDrop"
      @dragover.prevent
      @paste.prevent="handlePaste"
    >
      <input type="file" multiple accept="image/*" ref="fileInput" @change="handleFileSelect" hidden />
      <button class="btn-upload" @click="$refs.fileInput.click()">📁 Chọn ảnh</button>
      <p>Kéo thả hoặc dán ảnh từ clipboard (tối đa 10 ảnh)</p>
    </div>

    <!-- Preview list -->
    <div class="preview-list" v-if="images.length > 0">
      <div
        class="preview-item"
        v-for="(img, idx) in images"
        :key="idx"
        draggable="true"
        @dragstart="dragStart(idx)"
        @dragover.prevent
        @drop="drop(idx)"
      >
        <img :src="img.url" alt="preview" />
        <div class="preview-actions">
          <button @click="setPrimary(idx)" :class="{ active: img.isPrimary }">⭐</button>
          <button @click="removeImage(idx)">🗑</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { ref } from 'vue'
import toastService from '@/utils/toastService'

const props = defineProps({
  productId: Number
})
const emit = defineEmits(['uploaded'])

const images = ref([])
const dragIndex = ref(null)

const handleFileSelect = async (e) => {
  for (const file of e.target.files) {
    await uploadImage(file)
  }
}

const handleDrop = async (e) => {
  for (const file of e.dataTransfer.files) {
    await uploadImage(file)
  }
}

const handlePaste = async (e) => {
  const file = e.clipboardData.files[0]
  if (file) await uploadImage(file)
}

const uploadImage = async (file) => {
  if (images.value.length >= 10) return toastService.warning('Tối đa 10 ảnh')

  const fd = new FormData()
  fd.append('file', file)
  const res = await axios.post(`/api/uploads/product/${props.productId}`, fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })

  images.value.push({ ...res.data, url: res.data.imageUrl })
  emit('uploaded', res.data)
}

const setPrimary = (index) => {
  images.value.forEach((img, i) => (img.isPrimary = i === index))
}

const removeImage = async (index) => {
  const img = images.value[index]
  await axios.delete(`/api/uploads/${img.id}`)
  images.value.splice(index, 1)
}

const dragStart = (i) => (dragIndex.value = i)
const drop = (i) => {
  const moved = images.value.splice(dragIndex.value, 1)[0]
  images.value.splice(i, 0, moved)
}
</script>

<style scoped>
.upload-dropzone {
  border: 2px dashed var(--border-primary);
  padding: 20px;
  text-align: center;
  border-radius: 10px;
  background: var(--bg-secondary);
  cursor: pointer;
}
.preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}
.preview-item {
  width: 120px;
  height: 120px;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--border-primary);
}
.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.preview-actions {
  position: absolute;
  bottom: 4px;
  right: 4px;
  display: flex;
  gap: 4px;
}
.preview-actions button {
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}
.preview-actions button.active {
  background: gold;
  color: black;
}
</style>

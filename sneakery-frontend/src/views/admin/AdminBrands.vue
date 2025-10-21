<template>
  <div class="admin-brands">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Quản lý thương hiệu</h1>
        <p class="page-subtitle">Quản lý các thương hiệu giày sneaker</p>
      </div>
      <button @click="openCreateModal" class="btn btn-primary">
        <i class="material-icons">add</i>
        Thêm thương hiệu
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách thương hiệu...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="brands.length === 0" class="empty-state">
      <i class="material-icons">branding_watermark</i>
      <h3>Chưa có thương hiệu nào</h3>
      <p>Nhấn "Thêm thương hiệu" để tạo thương hiệu đầu tiên</p>
    </div>

    <!-- Brands Grid -->
    <div v-else class="brands-grid">
      <div 
        v-for="brand in brands" 
        :key="brand.id"
        class="brand-card"
      >
        <div class="brand-logo">
          <img 
            v-if="brand.logoUrl" 
            :src="brand.logoUrl" 
            :alt="brand.name"
            @error="handleImageError"
          />
          <div v-else class="logo-placeholder">
            <i class="material-icons">branding_watermark</i>
          </div>
        </div>
        
        <div class="brand-info">
          <h3 class="brand-name">{{ brand.name }}</h3>
          <p class="brand-slug">{{ brand.slug }}</p>
        </div>

        <div class="brand-actions">
          <button 
            @click="openEditModal(brand)" 
            class="btn-icon"
            title="Chỉnh sửa"
          >
            <i class="material-icons">edit</i>
          </button>
          <button 
            @click="confirmDelete(brand)" 
            class="btn-icon danger"
            title="Xóa"
          >
            <i class="material-icons">delete</i>
          </button>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            {{ isEditMode ? 'Chỉnh sửa thương hiệu' : 'Thêm thương hiệu mới' }}
          </h2>
          <button @click="closeModal" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label class="form-label required">Tên thương hiệu</label>
            <input 
              v-model="formData.name"
              type="text" 
              class="form-control"
              placeholder="Ví dụ: Nike, Adidas..."
              @input="generateSlug"
            />
            <span v-if="formErrors.name" class="form-error">{{ formErrors.name }}</span>
          </div>

          <div class="form-group">
            <label class="form-label required">Slug</label>
            <input 
              v-model="formData.slug"
              type="text" 
              class="form-control"
              placeholder="nike, adidas..."
            />
            <span v-if="formErrors.slug" class="form-error">{{ formErrors.slug }}</span>
            <span class="form-help">URL thân thiện (tự động tạo từ tên)</span>
          </div>

          <div class="form-group">
            <label class="form-label">Logo URL</label>
            <input 
              v-model="formData.logoUrl"
              type="text" 
              class="form-control"
              placeholder="https://example.com/logo.png"
            />
            <span class="form-help">Đường dẫn URL đến logo thương hiệu</span>
          </div>

          <!-- Logo Preview -->
          <div v-if="formData.logoUrl" class="logo-preview">
            <label class="form-label">Xem trước logo</label>
            <div class="preview-container">
              <img 
                :src="formData.logoUrl" 
                alt="Logo preview"
                @error="handlePreviewError"
              />
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeModal" class="btn btn-secondary">
            Hủy
          </button>
          <button 
            @click="handleSubmit" 
            class="btn btn-primary"
            :disabled="submitting"
          >
            <span v-if="submitting" class="btn-loading"></span>
            {{ submitting ? 'Đang lưu...' : (isEditMode ? 'Cập nhật' : 'Thêm mới') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="modal modal-sm" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">Xác nhận xóa</h2>
          <button @click="showDeleteModal = false" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>

        <div class="modal-body">
          <p>Bạn có chắc chắn muốn xóa thương hiệu <strong>{{ brandToDelete?.name }}</strong>?</p>
          <p class="text-error">Hành động này không thể hoàn tác!</p>
        </div>

        <div class="modal-footer">
          <button @click="showDeleteModal = false" class="btn btn-secondary">
            Hủy
          </button>
          <button 
            @click="handleDelete" 
            class="btn btn-danger"
            :disabled="deleting"
          >
            <span v-if="deleting" class="btn-loading"></span>
            {{ deleting ? 'Đang xóa...' : 'Xóa' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()

// State
const brands = ref([])
const loading = ref(false)
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const brandToDelete = ref(null)

const formData = ref({
  name: '',
  slug: '',
  logoUrl: ''
})

const formErrors = ref({})

// Methods
const fetchBrands = async () => {
  try {
    loading.value = true
    await adminStore.fetchBrands()
    brands.value = adminStore.brands
  } catch (error) {
    console.error('Lỗi khi tải danh sách thương hiệu:', error)
    alert('Không thể tải danh sách thương hiệu!')
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    name: '',
    slug: '',
    logoUrl: ''
  }
  formErrors.value = {}
  showModal.value = true
}

const openEditModal = (brand) => {
  isEditMode.value = true
  formData.value = {
    id: brand.id,
    name: brand.name,
    slug: brand.slug,
    logoUrl: brand.logoUrl || ''
  }
  formErrors.value = {}
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  formData.value = {
    name: '',
    slug: '',
    logoUrl: ''
  }
  formErrors.value = {}
}

const generateSlug = () => {
  // Tự động tạo slug từ tên (chỉ khi không phải edit mode)
  if (!isEditMode.value) {
    formData.value.slug = formData.value.name
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/đ/g, 'd')
      .replace(/[^a-z0-9\s-]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .trim()
  }
}

const validateForm = () => {
  formErrors.value = {}
  
  if (!formData.value.name || formData.value.name.trim() === '') {
    formErrors.value.name = 'Tên thương hiệu không được để trống'
  }
  
  if (!formData.value.slug || formData.value.slug.trim() === '') {
    formErrors.value.slug = 'Slug không được để trống'
  }
  
  return Object.keys(formErrors.value).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  try {
    submitting.value = true
    
    if (isEditMode.value) {
      await adminStore.updateBrand(formData.value.id, formData.value)
    } else {
      await adminStore.createBrand(formData.value)
    }
    
    await fetchBrands()
    closeModal()
    alert(isEditMode.value ? 'Cập nhật thương hiệu thành công!' : 'Thêm thương hiệu thành công!')
  } catch (error) {
    console.error('Lỗi khi lưu thương hiệu:', error)
    alert('Có lỗi xảy ra! Vui lòng thử lại.')
  } finally {
    submitting.value = false
  }
}

const confirmDelete = (brand) => {
  brandToDelete.value = brand
  showDeleteModal.value = true
}

const handleDelete = async () => {
  try {
    deleting.value = true
    await adminStore.deleteBrand(brandToDelete.value.id)
    await fetchBrands()
    showDeleteModal.value = false
    brandToDelete.value = null
    alert('Xóa thương hiệu thành công!')
  } catch (error) {
    console.error('Lỗi khi xóa thương hiệu:', error)
    alert('Không thể xóa thương hiệu này!')
  } finally {
    deleting.value = false
  }
}

const handleImageError = (event) => {
  event.target.style.display = 'none'
}

const handlePreviewError = (event) => {
  event.target.src = ''
  event.target.alt = 'Logo không hợp lệ'
}

// Lifecycle
onMounted(() => {
  fetchBrands()
})
</script>

<style scoped>
.admin-brands {
  max-width: 1200px;
  margin: 0 auto;
}

/* ===== PAGE HEADER ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 1.875rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
}

.page-subtitle {
  color: #64748b;
  margin: 0;
}

/* ===== LOADING & EMPTY STATES ===== */
.loading-container,
.empty-state {
  background: white;
  border-radius: 12px;
  padding: 3rem;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state i {
  font-size: 4rem;
  color: #cbd5e1;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: #64748b;
}

/* ===== BRANDS GRID ===== */
.brands-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.brand-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.brand-card:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.brand-logo {
  width: 100%;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  border-radius: 8px;
  overflow: hidden;
}

.brand-logo img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.logo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-placeholder i {
  font-size: 3rem;
  color: #cbd5e1;
}

.brand-info {
  flex: 1;
}

.brand-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
}

.brand-slug {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
  font-family: monospace;
}

.brand-actions {
  display: flex;
  gap: 0.5rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.btn-icon {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon:hover {
  background: #f8fafc;
  border-color: #3b82f6;
  color: #3b82f6;
}

.btn-icon.danger:hover {
  background: #fef2f2;
  border-color: #ef4444;
  color: #ef4444;
}

/* ===== MODAL ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal {
  background: white;
  border-radius: 12px;
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px rgba(0, 0, 0, 0.15);
}

.modal-sm {
  max-width: 400px;
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: #64748b;
  cursor: pointer;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
}

/* ===== FORM ===== */
.form-group {
  margin-bottom: 1.25rem;
}

.form-label {
  display: block;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
}

.form-label.required::after {
  content: ' *';
  color: #ef4444;
}

.form-control {
  width: 100%;
  padding: 0.625rem 0.875rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-error {
  display: block;
  color: #ef4444;
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

.form-help {
  display: block;
  color: #64748b;
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

.logo-preview {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.preview-container {
  width: 100%;
  height: 120px;
  background: #f8fafc;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin-top: 0.5rem;
}

.preview-container img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

/* ===== BUTTONS ===== */
.btn {
  padding: 0.625rem 1.25rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
}

.btn-secondary:hover:not(:disabled) {
  background: #e2e8f0;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: #dc2626;
}

.btn-loading {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.text-error {
  color: #ef4444;
  font-size: 0.875rem;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .brands-grid {
    grid-template-columns: 1fr;
  }
}
</style>

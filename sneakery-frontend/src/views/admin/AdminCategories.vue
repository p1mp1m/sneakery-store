<template>
  <div class="admin-categories">
    <!-- Page Header -->
    <div class="page-header">
      <div>
      <h1 class="page-title">Quản lý danh mục</h1>
        <p class="page-subtitle">Quản lý danh mục sản phẩm với cấu trúc phân cấp</p>
      </div>
      <button @click="openCreateModal" class="btn btn-primary">
        <i class="material-icons">add</i>
        Thêm danh mục
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách danh mục...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="categories.length === 0" class="empty-state">
        <i class="material-icons">category</i>
      <h3>Chưa có danh mục nào</h3>
      <p>Nhấn "Thêm danh mục" để tạo danh mục đầu tiên</p>
    </div>

    <!-- Categories List -->
    <div v-else class="categories-list">
      <div class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th>Tên danh mục</th>
              <th>Slug</th>
              <th>Danh mục cha</th>
              <th class="text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="category in rootCategories" :key="category.id">
              <tr class="category-row root">
                <td>
                  <div class="category-name">
                    <i class="material-icons">folder</i>
                    <strong>{{ category.name }}</strong>
          </div>
                </td>
                <td><code>{{ category.slug }}</code></td>
                <td><span class="badge badge-secondary">Danh mục gốc</span></td>
                <td class="text-center">
                  <div class="action-buttons">
                    <button 
                      @click="openEditModal(category)" 
                      class="btn-icon"
                      title="Chỉnh sửa"
                    >
            <i class="material-icons">edit</i>
                    </button>
                    <button 
                      @click="confirmDelete(category)" 
                      class="btn-icon danger"
                      title="Xóa"
                    >
                      <i class="material-icons">delete</i>
                    </button>
          </div>
                </td>
              </tr>
              <!-- Child categories -->
              <template v-if="getChildren(category.id).length > 0">
                <tr 
                  v-for="child in getChildren(category.id)" 
                  :key="child.id"
                  class="category-row child"
                >
                  <td>
                    <div class="category-name child-indent">
                      <i class="material-icons">subdirectory_arrow_right</i>
                      {{ child.name }}
          </div>
                  </td>
                  <td><code>{{ child.slug }}</code></td>
                  <td>{{ category.name }}</td>
                  <td class="text-center">
                    <div class="action-buttons">
                      <button 
                        @click="openEditModal(child)" 
                        class="btn-icon"
                        title="Chỉnh sửa"
                      >
                        <i class="material-icons">edit</i>
                      </button>
                      <button 
                        @click="confirmDelete(child)" 
                        class="btn-icon danger"
                        title="Xóa"
                      >
            <i class="material-icons">delete</i>
                      </button>
                    </div>
                  </td>
                </tr>
              </template>
            </template>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            {{ isEditMode ? 'Chỉnh sửa danh mục' : 'Thêm danh mục mới' }}
          </h2>
          <button @click="closeModal" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label class="form-label required">Tên danh mục</label>
            <input 
              v-model="formData.name"
              type="text" 
              class="form-control"
              placeholder="Ví dụ: Giày chạy bộ, Giày bóng rổ..."
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
              placeholder="giay-chay-bo, giay-bong-ro..."
            />
            <span v-if="formErrors.slug" class="form-error">{{ formErrors.slug }}</span>
            <span class="form-help">URL thân thiện (tự động tạo từ tên)</span>
          </div>

          <div class="form-group">
            <label class="form-label">Danh mục cha</label>
            <select v-model="formData.parentId" class="form-control">
              <option :value="null">-- Không có (danh mục gốc) --</option>
              <option 
                v-for="cat in rootCategories" 
                :key="cat.id" 
                :value="cat.id"
                :disabled="isEditMode && cat.id === formData.id"
              >
                {{ cat.name }}
              </option>
            </select>
            <span class="form-help">Để trống nếu đây là danh mục gốc</span>
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
          <p>Bạn có chắc chắn muốn xóa danh mục <strong>{{ categoryToDelete?.name }}</strong>?</p>
          <p v-if="hasChildren(categoryToDelete?.id)" class="text-warning">
            <i class="material-icons">warning</i>
            Danh mục này có danh mục con. Các danh mục con cũng sẽ bị xóa!
          </p>
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
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()

// State
const categories = ref([])
const loading = ref(false)
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const categoryToDelete = ref(null)

const formData = ref({
  name: '',
  slug: '',
  parentId: null
})

const formErrors = ref({})

// Computed
const rootCategories = computed(() => {
  return categories.value.filter(cat => !cat.parentId)
})

// Methods
const getChildren = (parentId) => {
  return categories.value.filter(cat => cat.parentId === parentId)
}

const hasChildren = (categoryId) => {
  return categories.value.some(cat => cat.parentId === categoryId)
}

const fetchCategories = async () => {
  try {
    loading.value = true
    await adminStore.fetchCategories()
    categories.value = adminStore.categories
  } catch (error) {
    console.error('Lỗi khi tải danh sách danh mục:', error)
    alert('Không thể tải danh sách danh mục!')
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    name: '',
    slug: '',
    parentId: null
  }
  formErrors.value = {}
  showModal.value = true
}

const openEditModal = (category) => {
  isEditMode.value = true
  formData.value = {
    id: category.id,
    name: category.name,
    slug: category.slug,
    parentId: category.parentId || null
  }
  formErrors.value = {}
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  formData.value = {
    name: '',
    slug: '',
    parentId: null
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
    formErrors.value.name = 'Tên danh mục không được để trống'
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
      await adminStore.updateCategory(formData.value.id, formData.value)
    } else {
      await adminStore.createCategory(formData.value)
    }
    
    await fetchCategories()
    closeModal()
    alert(isEditMode.value ? 'Cập nhật danh mục thành công!' : 'Thêm danh mục thành công!')
  } catch (error) {
    console.error('Lỗi khi lưu danh mục:', error)
    alert('Có lỗi xảy ra! Vui lòng thử lại.')
  } finally {
    submitting.value = false
  }
}

const confirmDelete = (category) => {
  categoryToDelete.value = category
  showDeleteModal.value = true
}

const handleDelete = async () => {
  try {
    deleting.value = true
    await adminStore.deleteCategory(categoryToDelete.value.id)
    await fetchCategories()
    showDeleteModal.value = false
    categoryToDelete.value = null
    alert('Xóa danh mục thành công!')
  } catch (error) {
    console.error('Lỗi khi xóa danh mục:', error)
    alert('Không thể xóa danh mục này!')
  } finally {
    deleting.value = false
  }
}

// Lifecycle
onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.admin-categories {
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

/* ===== CATEGORIES LIST ===== */
.categories-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.table-container {
  overflow-x: auto;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table thead {
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.table th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #475569;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.table td {
  padding: 1rem;
  color: #1e293b;
}

.table tbody tr {
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.2s;
}

.table tbody tr:hover {
  background: #f8fafc;
}

.category-row.root {
  background: #fefefe;
}

.category-row.child {
  background: white;
}

.category-name {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.category-name.child-indent {
  padding-left: 1.5rem;
}

.category-name i {
  color: #64748b;
  font-size: 1.25rem;
}

.text-center {
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}

.btn-icon {
  padding: 0.375rem;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
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

.badge {
  display: inline-block;
  padding: 0.25rem 0.625rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.badge-secondary {
  background: #f1f5f9;
  color: #475569;
}

code {
  background: #f1f5f9;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #1e293b;
  font-family: 'Courier New', monospace;
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

.text-error {
  color: #ef4444;
  font-size: 0.875rem;
}

.text-warning {
  color: #f59e0b;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
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

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .table {
    font-size: 0.875rem;
  }

  .table th,
  .table td {
    padding: 0.75rem 0.5rem;
  }
}
</style>

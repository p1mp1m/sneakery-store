<template>
  <div class="admin-page admin-categories">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">category</span>
            Quản lý danh mục
          </h1>
          <p class="page-subtitle">Quản lý danh mục sản phẩm với cấu trúc phân cấp</p>
        </div>
        <div class="header-actions">
          <button @click="handleExport('csv')" class="btn btn-secondary" title="Xuất CSV">
            <span class="material-icons">download</span>
            Xuất CSV
          </button>
          <button @click="handleExport('json')" class="btn btn-secondary" title="Xuất JSON">
            <span class="material-icons">file_download</span>
            Xuất JSON
          </button>
          <button @click="openCreateModal" class="btn btn-primary">
            <span class="material-icons">add</span>
            Thêm danh mục
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid animate-fade-in">
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-primary);">
          <span class="material-icons">category</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ totalCategories }}</div>
          <div class="stat-label">TỔNG DANH MỤC</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-success);">
          <span class="material-icons">folder_open</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ rootCategoriesCount }}</div>
          <div class="stat-label">DANH MỤC GỐC</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-info);">
          <span class="material-icons">subdirectory_arrow_right</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ childCategoriesCount }}</div>
          <div class="stat-label">DANH MỤC CON</div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container animate-fade-in">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách danh mục...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="!categories || categories.length === 0" class="empty-state animate-fade-up">
      <i class="material-icons">category</i>
      <h3>Chưa có danh mục nào</h3>
      <p>Nhấn "Thêm danh mục" để tạo danh mục đầu tiên</p>
    </div>

    <!-- Categories List -->
    <div v-else-if="categories && categories.length > 0" class="categories-list animate-fade-up">
      <!-- Search & Filters -->
      <div class="filters-section">
        <div class="search-box">
          <span class="material-icons search-icon">search</span>
          <input 
            type="text" 
            class="search-input" 
            v-model="searchQuery"
            placeholder="Tìm kiếm danh mục..."
          />
        </div>
        
        <div class="filter-actions">
          <button 
            v-if="selectedCategories.length > 0" 
            @click="handleBulkDelete"
            class="btn btn-danger btn-sm"
          >
            <span class="material-icons">delete</span>
            Xóa {{ selectedCategories.length }} mục
          </button>
          <button @click="expandAll" class="btn btn-secondary btn-sm">
            <span class="material-icons">unfold_more</span>
            Mở rộng tất cả
          </button>
          <button @click="collapseAll" class="btn btn-secondary btn-sm">
            <span class="material-icons">unfold_less</span>
            Thu gọn tất cả
          </button>
        </div>
      </div>

      <div class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th style="width: 40px;">
                <input 
                  type="checkbox" 
                  @change="toggleSelectAll"
                  :checked="allSelected"
                  class="checkbox-input"
                />
              </th>
              <th style="width: 40px;"></th>
              <th>Tên danh mục</th>
              <th>Slug</th>
              <th>Danh mục cha</th>
              <th class="text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="category in filteredCategories" :key="category.id">
              <tr class="category-row root" :class="{ 'selected': isSelected(category.id) }">
                <td>
                  <input 
                    type="checkbox" 
                    :checked="isSelected(category.id)"
                    @change="toggleSelect(category.id)"
                    class="checkbox-input"
                  />
                </td>
                <td>
                  <button 
                    v-if="getChildren(category.id).length > 0"
                    @click="toggleExpand(category.id)"
                    class="btn-expand"
                    :class="{ 'expanded': isExpanded(category.id) }"
                    title="Mở rộng/Thu gọn"
                  >
                    <span class="material-icons">{{ isExpanded(category.id) ? 'expand_more' : 'chevron_right' }}</span>
                  </button>
                </td>
                <td>
                  <div class="category-name">
                    <span class="material-icons">folder</span>
                    <strong v-html="highlightSearch(category.name)"></strong>
                    <span v-if="getChildren(category.id).length > 0" class="child-count">
                      ({{ getChildren(category.id).length }})
                    </span>
                  </div>
                </td>
                <td><code v-html="highlightSearch(category.slug)"></code></td>
                <td><span class="badge badge-secondary">Danh mục gốc</span></td>
                <td class="text-center">
                  <div class="action-buttons">
                    <button 
                      @click="openEditModal(category)" 
                      class="btn-icon"
                      title="Chỉnh sửa"
                    >
                      <span class="material-icons">edit</span>
                    </button>
                    <button 
                      @click="confirmDelete(category)" 
                      class="btn-icon danger"
                      title="Xóa"
                    >
                      <span class="material-icons">delete</span>
                    </button>
                  </div>
                </td>
              </tr>
              <!-- Child categories - Collapsible -->
              <template v-if="isExpanded(category.id)">
                <tr 
                  v-for="child in getChildren(category.id)" 
                  :key="child.id"
                  class="category-row child"
                  :class="{ 'selected': isSelected(child.id) }"
                >
                  <td>
                    <input 
                      type="checkbox" 
                      :checked="isSelected(child.id)"
                      @change="toggleSelect(child.id)"
                      class="checkbox-input"
                    />
                  </td>
                  <td></td>
                  <td>
                    <div class="category-name child-indent">
                      <span class="material-icons">subdirectory_arrow_right</span>
                      <span v-html="highlightSearch(child.name)"></span>
                    </div>
                  </td>
                  <td><code v-html="highlightSearch(child.slug)"></code></td>
                  <td>{{ category.name }}</td>
                  <td class="text-center">
                    <div class="action-buttons">
                      <button 
                        @click="openEditModal(child)" 
                        class="btn-icon"
                        title="Chỉnh sửa"
                      >
                        <span class="material-icons">edit</span>
                      </button>
                      <button 
                        @click="confirmDelete(child)" 
                        class="btn-icon danger"
                        title="Xóa"
                      >
                        <span class="material-icons">delete</span>
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

    <!-- Delete Confirmation Dialog -->
    <ConfirmDialog
      v-model="showDeleteModal"
      type="danger"
      title="Xác nhận xóa danh mục"
      :message="`Bạn có chắc chắn muốn xóa danh mục '${categoryToDelete?.name}'?`"
      :description="getDeleteWarning(categoryToDelete?.id)"
      confirm-text="Xóa danh mục"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="handleDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import ConfirmDialog from '@/assets/components/common/ConfirmDialog.vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'

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
const expandedCategories = ref([]) // Track expanded parent categories
const selectedCategories = ref([]) // Track selected categories for bulk actions
const searchQuery = ref('') // Search query

const formData = ref({
  name: '',
  slug: '',
  parentId: null
})

const formErrors = ref({})

// Computed - Stats
const totalCategories = computed(() => categories.value.length)

const rootCategoriesCount = computed(() => {
  return categories.value.filter(cat => !cat.parentId).length
})

const childCategoriesCount = computed(() => {
  return categories.value.filter(cat => cat.parentId).length
})

// Computed
const rootCategories = computed(() => {
  return categories.value.filter(cat => !cat.parentId)
})

// Filtered categories based on search
const filteredCategories = computed(() => {
  if (!searchQuery.value.trim()) {
    return rootCategories.value
  }
  
  const query = searchQuery.value.toLowerCase()
  return rootCategories.value.filter(cat => {
    const matchesRoot = cat.name.toLowerCase().includes(query) || 
                        cat.slug.toLowerCase().includes(query)
    
    const hasMatchingChild = getChildren(cat.id).some(child =>
      child.name.toLowerCase().includes(query) || 
      child.slug.toLowerCase().includes(query)
    )
    
    return matchesRoot || hasMatchingChild
  })
})

// Selection helpers
const allSelected = computed(() => {
  if (categories.value.length === 0) return false
  return categories.value.every(cat => selectedCategories.value.includes(cat.id))
})

const isSelected = (categoryId) => {
  return selectedCategories.value.includes(categoryId)
}

const toggleSelect = (categoryId) => {
  const index = selectedCategories.value.indexOf(categoryId)
  if (index > -1) {
    selectedCategories.value.splice(index, 1)
  } else {
    selectedCategories.value.push(categoryId)
  }
}

const toggleSelectAll = () => {
  if (allSelected.value) {
    selectedCategories.value = []
  } else {
    selectedCategories.value = categories.value.map(cat => cat.id)
  }
}

// Expand/Collapse methods
const toggleExpand = (categoryId) => {
  const index = expandedCategories.value.indexOf(categoryId)
  if (index > -1) {
    expandedCategories.value.splice(index, 1)
  } else {
    expandedCategories.value.push(categoryId)
  }
}

const isExpanded = (categoryId) => {
  return expandedCategories.value.includes(categoryId)
}

// Methods
const getChildren = (parentId) => {
  return categories.value.filter(cat => cat.parentId === parentId)
}

const hasChildren = (categoryId) => {
  return categories.value.some(cat => cat.parentId === categoryId)
}

const getDeleteWarning = (categoryId) => {
  if (hasChildren(categoryId)) {
    return '⚠️ Danh mục này có danh mục con. Các danh mục con cũng sẽ bị xóa! Hành động này không thể hoàn tác!'
  }
  return 'Hành động này không thể hoàn tác!'
}

const fetchCategories = async () => {
  try {
    loading.value = true
    const result = await adminStore.fetchCategories()
    categories.value = result.content || result || []
  } catch (error) {
    console.error('Lỗi khi tải danh sách danh mục:', error)
    ElMessage.error({
      message: 'Không thể tải danh sách danh mục. Vui lòng thử lại!',
      duration: 5000
    })
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
  
  // Validate name
  if (!formData.value.name || formData.value.name.trim() === '') {
    formErrors.value.name = 'Tên danh mục không được để trống'
  } else if (formData.value.name.trim().length < 2) {
    formErrors.value.name = 'Tên danh mục phải có ít nhất 2 ký tự'
  } else if (formData.value.name.trim().length > 100) {
    formErrors.value.name = 'Tên danh mục không được vượt quá 100 ký tự'
  }
  
  // Validate slug
  if (!formData.value.slug || formData.value.slug.trim() === '') {
    formErrors.value.slug = 'Slug không được để trống'
  } else if (!/^[a-z0-9-]+$/.test(formData.value.slug)) {
    formErrors.value.slug = 'Slug chỉ được chứa chữ thường, số và dấu gạch ngang'
  } else if (formData.value.slug.length < 2) {
    formErrors.value.slug = 'Slug phải có ít nhất 2 ký tự'
  } else if (formData.value.slug.length > 100) {
    formErrors.value.slug = 'Slug không được vượt quá 100 ký tự'
  }
  
  return Object.keys(formErrors.value).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    ElMessage.warning({
      message: 'Vui lòng kiểm tra lại thông tin form!',
      duration: 3000
    })
    return
  }

  try {
    submitting.value = true
    
    if (isEditMode.value) {
      await adminStore.updateCategory(formData.value.id, formData.value)
      ElMessage.success({
        message: `Đã cập nhật danh mục "${formData.value.name}" thành công!`,
        duration: 3000
      })
    } else {
      await adminStore.createCategory(formData.value)
      ElMessage.success({
        message: `Đã thêm danh mục "${formData.value.name}" thành công!`,
        duration: 3000
      })
    }
    
    await fetchCategories()
    closeModal()
  } catch (error) {
    console.error('Lỗi khi lưu danh mục:', error)
    
    // Handle specific error messages from server
    let errorMessage = 'Có lỗi xảy ra! Vui lòng thử lại.'
    
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      
      if (status === 400) {
        if (data.validationErrors) {
          const firstError = Object.values(data.validationErrors)[0]
          if (firstError && firstError.length > 0) {
            errorMessage = firstError[0]
          }
        } else if (data.message) {
          errorMessage = data.message
        }
      } else if (status === 409) {
        errorMessage = 'Tên hoặc slug đã tồn tại. Vui lòng chọn tên khác.'
      } else if (status === 500) {
        errorMessage = 'Lỗi server. Vui lòng liên hệ quản trị viên.'
      }
    } else if (error.request) {
      errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng!'
    }
    
    ElMessage.error({
      message: errorMessage,
      duration: 5000
    })
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
    ElMessage.success(`Đã xóa danh mục "${categoryToDelete.value.name}" thành công!`)
    await fetchCategories()
    showDeleteModal.value = false
    categoryToDelete.value = null
  } catch (error) {
    console.error('Lỗi khi xóa danh mục:', error)
    ElMessage.error('Không thể xóa danh mục này. Vui lòng thử lại!')
  } finally {
    deleting.value = false
  }
}

// Export functions
const handleExport = (format) => {
  const data = categories.value.map(cat => ({
    'ID': cat.id,
    'Tên danh mục': cat.name,
    'Slug': cat.slug,
    'Danh mục cha': cat.parentId ? categories.value.find(c => c.id === cat.parentId)?.name || '' : 'Danh mục gốc',
    'Ngày tạo': cat.createdAt || '',
  }))
  
  if (format === 'csv') {
    downloadCsv(data, `categories_${Date.now()}.csv`)
    ElMessage.success('Đã xuất file CSV thành công!')
  } else if (format === 'json') {
    downloadJson(data, `categories_${Date.now()}.json`)
    ElMessage.success('Đã xuất file JSON thành công!')
  }
}

// Search highlighting
const highlightSearch = (text) => {
  if (!searchQuery.value.trim() || !text) return text
  
  const query = searchQuery.value.trim()
  const regex = new RegExp(`(${query})`, 'gi')
  return text.replace(regex, '<mark class="search-highlight">$1</mark>')
}

// Expand/Collapse all
const expandAll = () => {
  expandedCategories.value = rootCategories.value.map(cat => cat.id)
}

const collapseAll = () => {
  expandedCategories.value = []
}

// Bulk delete
const handleBulkDelete = async () => {
  if (selectedCategories.value.length === 0) return
  
  const confirmed = confirm(`Bạn có chắc chắn muốn xóa ${selectedCategories.value.length} danh mục đã chọn?`)
  if (!confirmed) return
  
  try {
    deleting.value = true
    
    // Delete all selected categories
    for (const categoryId of selectedCategories.value) {
      await adminStore.deleteCategory(categoryId)
    }
    
    ElMessage.success(`Đã xóa ${selectedCategories.value.length} danh mục thành công!`)
    selectedCategories.value = []
    await fetchCategories()
  } catch (error) {
    console.error('Lỗi khi xóa danh mục:', error)
    ElMessage.error('Có lỗi xảy ra khi xóa danh mục. Vui lòng thử lại!')
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
/* ═══════════════════════════════════════════════════════════════════════
   📂 ADMIN CATEGORIES - UNIFIED DARK THEME
   ═══════════════════════════════════════════════════════════════════════ */

.admin-categories {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-4);
  min-height: calc(100vh - 4rem);
}

/* ═══ PAGE HEADER ═══ */
.page-header {
  background: var(--card-bg);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-6);
}

.title-section {
  flex: 1;
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.page-title .material-icons {
  font-size: 2rem;
  color: var(--accent-primary);
  animation: rotate 10s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.page-subtitle {
  color: var(--text-tertiary);
  margin: 0;
  font-size: var(--text-base);
}

.header-actions {
  display: flex;
  gap: var(--space-3);
  flex-shrink: 0;
}

/* ═══ LOADING & EMPTY STATES ═══ */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-16) var(--space-8);
  background: var(--card-bg);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-2xl);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  text-align: center;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-container p {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-16) var(--space-8);
  background: var(--card-bg);
  border: 1px dashed var(--border-primary);
  border-radius: var(--radius-2xl);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  text-align: center;
}

.empty-state i {
  font-size: 4rem;
  color: var(--accent-primary);
  opacity: 0.5;
}

.empty-state h3 {
  font-size: var(--text-xl);
  color: var(--text-primary);
  font-weight: var(--font-bold);
  margin: 0 0 var(--space-2) 0;
}

.empty-state p {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0;
}

/* ═══ STATS GRID ═══ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stat-card {
  background: var(--card-bg);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: var(--shadow-card);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--gradient-purple-soft);
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--accent-primary);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.stat-icon .material-icons {
  font-size: 2rem;
  color: white;
}

.stat-content {
  flex: 1;
  position: relative;
  z-index: 1;
}

.stat-value {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: var(--space-2);
}

.stat-label {
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

/* ═══ CATEGORIES LIST ═══ */
.categories-list {
  background: var(--card-bg);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  overflow: hidden;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* ═══ FILTERS SECTION ═══ */
.filters-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
  background: var(--gradient-purple-soft);
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 280px;
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  font-size: 1.25rem;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-4) var(--space-3) var(--space-12);
  border: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.6);
  border-radius: var(--radius-lg);
  font-size: var(--text-base);
  color: var(--text-primary);
  transition: all var(--transition-fast);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.search-input:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.8);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.search-input::placeholder {
  color: var(--text-quaternary);
}

.filter-actions {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

/* ═══ SEARCH HIGHLIGHT ═══ */
.search-highlight {
  background: rgba(234, 179, 8, 0.3);
  color: #fbbf24;
  padding: 2px 4px;
  border-radius: 4px;
  font-weight: var(--font-semibold);
}

.table-container {
  overflow-x: auto;
}

.table {
  width: 100%;
  border-collapse: collapse;
  background: transparent;
}

.table thead {
  background: var(--table-header-bg);
  border-bottom: 2px solid var(--border-primary);
}

/* Table headers use global admin-tables.css styles */

/* Table cells use global admin-tables.css styles */

.table tbody tr {
  background: rgba(30, 41, 59, 0.4);
  transition: all var(--transition-fast);
}

.table tbody tr:hover {
  background: rgba(167, 139, 250, 0.1);
  transform: scale(1.002);
}

.table tbody tr.selected {
  background: rgba(167, 139, 250, 0.2) !important;
  border-left: 3px solid var(--accent-primary) !important;
}

.category-row.root {
  background: rgba(30, 41, 59, 0.6);
  border-left: 3px solid var(--accent-primary);
}

.category-row.child {
  background: rgba(30, 41, 59, 0.3);
  border-left: 3px solid var(--text-quaternary);
}

.category-row.root:hover {
  background: rgba(167, 139, 250, 0.15);
  border-left-color: var(--accent-light);
}

.category-row.child:hover {
  background: rgba(167, 139, 250, 0.1);
  border-left-color: var(--accent-primary);
}

/* ═══ CHECKBOX INPUT ═══ */
.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--accent-primary);
  border-radius: var(--radius-sm);
}

.category-name {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.category-name.child-indent {
  padding-left: var(--space-8);
}

.category-name i {
  color: var(--accent-primary);
  font-size: 1.25rem;
  transition: transform var(--transition-fast);
}

.category-row:hover .category-name i {
  transform: scale(1.15);
}

/* ═══ EXPAND BUTTON ═══ */
.btn-expand {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-expand:hover {
  background: rgba(167, 139, 250, 0.1);
  border-color: var(--accent-primary);
  color: var(--accent-primary);
  transform: scale(1.1);
}

.btn-expand.expanded {
  background: rgba(167, 139, 250, 0.15);
  border-color: var(--accent-primary);
  color: var(--accent-primary);
}

.btn-expand .material-icons {
  font-size: 1.25rem;
  transition: transform var(--transition-fast);
}

.btn-expand.expanded .material-icons {
  transform: rotate(0deg);
}

/* ═══ CHILD COUNT BADGE ═══ */
.child-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 20px;
  padding: 0 var(--space-2);
  background: var(--accent-primary);
  color: white;
  border-radius: var(--radius-full);
  font-size: 0.7rem;
  font-weight: var(--font-bold);
  margin-left: var(--space-1);
  box-shadow: 0 2px 6px rgba(167, 139, 250, 0.3);
}

.text-center {
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: var(--space-2);
  justify-content: center;
}

/* Action buttons use global admin-tables.css styles */

.badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.badge-secondary {
  background: var(--info-bg);
  color: var(--info-text);
  border: 1px solid var(--info-border);
}

code {
  background: var(--bg-tertiary);
  color: var(--accent-primary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  font-family: var(--font-mono);
  border: 1px solid var(--border-primary);
}

/* ═══ MODAL ═══ */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-modal);
  padding: var(--space-4);
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background: var(--modal-bg);
  border-radius: var(--radius-2xl);
  border: 1px solid var(--border-primary);
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-2xl);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--gradient-purple-soft);
}

.modal-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-text);
  transform: rotate(90deg);
}

.modal-body {
  padding: var(--space-6);
}

.modal-footer {
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
  background: var(--gradient-purple-soft);
}

/* ═══ FORM ═══ */
.form-group {
  margin-bottom: var(--space-5);
}

.form-label {
  display: block;
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  font-size: var(--text-sm);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-label.required::after {
  content: ' *';
  color: var(--error-solid);
  margin-left: var(--space-1);
}

.form-control {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  border: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.6);
  border-radius: var(--radius-lg);
  font-size: var(--text-base);
  color: var(--text-primary);
  transition: all var(--transition-fast);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.form-control:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.8);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15),
              0 4px 16px rgba(167, 139, 250, 0.2);
}

.form-control::placeholder {
  color: var(--text-quaternary);
}

.form-error {
  display: block;
  color: var(--error-text);
  font-size: var(--text-xs);
  margin-top: var(--space-2);
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.form-help {
  display: block;
  color: var(--text-quaternary);
  font-size: var(--text-xs);
  margin-top: var(--space-2);
}

/* ═══ BUTTONS ═══ */
.btn {
  padding: var(--space-3) var(--space-6);
  border: none;
  border-radius: var(--radius-lg);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  white-space: nowrap;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  filter: grayscale(0.5);
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--color-white);
  box-shadow: var(--shadow-btn);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-secondary {
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-secondary);
  border: 1px solid var(--border-primary);
}

.btn-secondary:hover:not(:disabled) {
  background: rgba(15, 23, 42, 0.8);
  border-color: var(--border-hover);
  color: var(--text-primary);
}

.btn-danger {
  background: var(--gradient-danger);
  color: var(--color-white);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.25);
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(239, 68, 68, 0.35);
}

.btn-sm {
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-sm);
}

.btn-sm .material-icons {
  font-size: 1rem;
}

.btn-loading {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* ═══ ANIMATIONS ═══ */
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-fade-up {
  animation: fadeUp 0.6s ease-out;
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ═══ RESPONSIVE ═══ */
@media (max-width: 1024px) {
  .admin-categories {
    padding: var(--space-6) var(--space-3);
  }
  
  .page-header {
    padding: var(--space-6);
  }
}

@media (max-width: 768px) {
  .admin-categories {
    padding: var(--space-4);
  }
  
  .page-header {
    padding: var(--space-5);
  }
  
  .header-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .page-title {
    font-size: var(--text-2xl);
  }
  
  .page-title .material-icons {
    font-size: 1.5rem;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .header-actions .btn {
    flex: 1;
    justify-content: center;
  }

  .table {
    font-size: var(--text-sm);
  }

  .table th,
  .table td {
    padding: var(--space-3) var(--space-2);
  }
  
  .category-name.child-indent {
    padding-left: var(--space-4);
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .modal {
    max-width: 100%;
    min-height: 100vh;
    border-radius: 0;
  }
  
  .modal-footer {
    flex-direction: column-reverse;
  }
  
  .modal-footer .btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .table th,
  .table td {
    font-size: var(--text-xs);
    padding: var(--space-2);
  }
  
  code {
    font-size: var(--text-xs);
    padding: 2px var(--space-1);
  }
}
</style>

<template>
  <div class="admin-brands admin-page">
    <!-- ===== PAGE HEADER ===== -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">local_offer</span>
            Quản lý Thương hiệu
          </h1>
          <p class="page-subtitle">Quản lý danh sách thương hiệu sản phẩm</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-primary" @click="openCreateModal">
            <span class="material-icons">add</span>
            Thêm Thương hiệu
          </button>
        </div>
      </div>
    </div>

    <!-- ===== STATS GRID ===== -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-success);">
          <span class="material-icons">check_circle</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ activeBrandsCount }}</div>
          <div class="stat-label">ĐANG HOẠT ĐỘNG</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-warning);">
          <span class="material-icons">pause_circle</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ inactiveBrandsCount }}</div>
          <div class="stat-label">TẠM NGƯNG</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-info);">
          <span class="material-icons">inventory</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ brands.length }}</div>
          <div class="stat-label">TỔNG THƯƠNG HIỆU</div>
        </div>
      </div>
    </div>

    <!-- ===== FILTERS ===== -->
    <div class="content-section">
      <div class="search-filters">
        <div class="search-box">
          <span class="material-icons search-icon">search</span>
          <input 
            type="text" 
            class="search-input" 
            v-model="searchKeyword"
            placeholder="Tìm theo tên thương hiệu..."
          />
        </div>
        
        <select class="filter-select" v-model="filterStatus">
          <option value="all">Tất cả trạng thái</option>
          <option value="active">Đang hoạt động</option>
          <option value="inactive">Tạm ngưng</option>
        </select>

        <button class="btn btn-ghost" @click="resetFilters">
          <span class="material-icons">refresh</span>
          Làm mới
        </button>
      </div>
    </div>

    <!-- ===== LOADING STATE ===== -->
    <div v-if="loading" class="admin-loading">
      <div class="loading-spinner"></div>
      <p class="loading-text">Đang tải dữ liệu...</p>
    </div>

    <!-- ===== EMPTY STATE ===== -->
    <div v-else-if="filteredBrands.length === 0" class="admin-empty-state">
      <div class="empty-state-icon">
        <span class="material-icons">local_offer</span>
      </div>
      <h3 class="empty-state-title">Không có thương hiệu nào</h3>
      <p class="empty-state-description">Bắt đầu thêm thương hiệu đầu tiên cho cửa hàng của bạn</p>
      <button class="btn btn-primary" @click="openCreateModal">
        <span class="material-icons">add</span>
        Thêm Thương hiệu
      </button>
    </div>

    <!-- ===== BRANDS TABLE ===== -->
    <div v-else class="table-container">
      <table class="admin-table brands-table">
        <thead>
          <tr>
            <th style="width: 80px;">ID</th>
            <th style="width: 100px;">Logo</th>
            <th>Tên thương hiệu</th>
            <th>Slug</th>
            <th style="width: 200px;">Website</th>
            <th style="width: 150px;">Trạng thái</th>
            <th style="width: 180px;">Ngày tạo</th>
            <th style="width: 150px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="brand in paginatedBrands" :key="brand.id">
            <td>{{ brand.id }}</td>
            <td>
              <div class="brand-logo">
                <img 
                  v-if="brand.logoUrl" 
                  :src="brand.logoUrl" 
                  :alt="brand.name"
                  @error="handleImageError"
                />
                <div v-else class="logo-placeholder">
                  <span class="material-icons">local_offer</span>
                </div>
              </div>
            </td>
            <td>
              <div class="brand-info">
                <strong>{{ brand.name }}</strong>
                <p v-if="brand.description" class="brand-desc">{{ truncateText(brand.description, 50) }}</p>
              </div>
            </td>
            <td>
              <code class="code-badge">{{ brand.slug }}</code>
            </td>
            <td>
              <a 
                v-if="brand.websiteUrl" 
                :href="brand.websiteUrl" 
                target="_blank" 
                class="website-link"
              >
                <span class="material-icons">link</span>
                {{ truncateText(brand.websiteUrl, 25) }}
              </a>
              <span v-else class="text-muted">—</span>
            </td>
            <td>
              <span 
                class="status-badge" 
                :class="brand.isActive ? 'status-active' : 'status-inactive'"
              >
                <span class="material-icons">{{ brand.isActive ? 'check_circle' : 'cancel' }}</span>
                {{ brand.isActive ? 'Hoạt động' : 'Tạm ngưng' }}
              </span>
            </td>
            <td>{{ formatDate(brand.createdAt) }}</td>
            <td>
              <div class="cell-actions">
                <button 
                  class="btn-icon btn-edit" 
                  @click="openEditModal(brand)"
                  title="Chỉnh sửa"
                >
                  <span class="material-icons">edit</span>
                </button>
                <button 
                  class="btn-icon btn-delete" 
                  @click="confirmDelete(brand)"
                  title="Xóa"
                >
                  <span class="material-icons">delete</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ===== PAGINATION ===== -->
    <div v-if="totalPages > 1" class="table-pagination">
      <button 
        class="pagination-btn" 
        :disabled="currentPage === 1"
        @click="currentPage--"
      >
        <span class="material-icons">chevron_left</span>
        Trước
      </button>
      
      <div class="pagination-info">
        Trang {{ currentPage }} / {{ totalPages }}
      </div>
      
      <button 
        class="pagination-btn" 
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >
        Sau
        <span class="material-icons">chevron_right</span>
      </button>
    </div>

    <!-- ===== CREATE/EDIT MODAL ===== -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal modal-lg" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons">{{ isEditMode ? 'edit' : 'add' }}</span>
            {{ isEditMode ? 'Chỉnh sửa Thương hiệu' : 'Thêm Thương hiệu mới' }}
          </h2>
          <button class="modal-close" @click="closeModal">
            <span class="material-icons">close</span>
          </button>
        </div>
        
        <div class="modal-body">
          <form @submit.prevent="saveBrand">
            <div class="form-row">
              <div class="form-group">
                <label class="form-label required">Tên thương hiệu</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="formData.name"
                  placeholder="VD: Nike, Adidas..."
                  required
                />
              </div>
              
              <div class="form-group">
                <label class="form-label required">Slug</label>
                <input 
                  type="text" 
                  class="form-control" 
                  v-model="formData.slug"
                  placeholder="VD: nike, adidas..."
                  required
                />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">URL Logo</label>
              <input 
                type="url" 
                class="form-control" 
                v-model="formData.logoUrl"
                placeholder="https://example.com/logo.png"
              />
            </div>

            <div class="form-group">
              <label class="form-label">Website</label>
              <input 
                type="url" 
                class="form-control" 
                v-model="formData.websiteUrl"
                placeholder="https://example.com"
              />
            </div>

            <div class="form-group">
              <label class="form-label">Mô tả</label>
              <textarea 
                class="form-control" 
                v-model="formData.description"
                placeholder="Nhập mô tả về thương hiệu..."
                rows="4"
              ></textarea>
            </div>

            <div class="form-check">
              <input 
                type="checkbox" 
                class="form-check-input"
                v-model="formData.isActive"
                id="isActive"
              />
              <label class="form-check-label" for="isActive">
                Kích hoạt thương hiệu
              </label>
            </div>

            <div class="form-actions right">
              <button type="button" class="btn btn-outline" @click="closeModal">
                <span class="material-icons">close</span>
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :class="{ 'btn-loading': saving }" :disabled="saving">
                <span class="material-icons">{{ saving ? 'hourglass_empty' : 'save' }}</span>
                {{ saving ? 'Đang lưu...' : 'Lưu' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="modal modal-sm" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons" style="color: var(--color-error);">warning</span>
            Xác nhận xóa
          </h2>
          <button class="modal-close" @click="showDeleteModal = false">
            <span class="material-icons">close</span>
          </button>
        </div>
        
        <div class="modal-body">
          <p>Bạn có chắc chắn muốn xóa thương hiệu <strong>{{ brandToDelete?.name }}</strong>?</p>
          <p class="text-muted">Hành động này không thể hoàn tác.</p>
          
          <div class="form-actions right">
            <button class="btn btn-outline" @click="showDeleteModal = false">
              <span class="material-icons">close</span>
              Hủy
            </button>
            <button class="btn btn-danger" @click="deleteBrand" :class="{ 'btn-loading': deleting }" :disabled="deleting">
              <span class="material-icons">{{ deleting ? 'hourglass_empty' : 'delete' }}</span>
              {{ deleting ? 'Đang xóa...' : 'Xóa' }}
            </button>
          </div>
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
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const brands = ref([])
const searchKeyword = ref('')
const filterStatus = ref('all')
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const brandToDelete = ref(null)
const currentPage = ref(1)
const itemsPerPage = 10

// Form data
const formData = ref({
  id: null,
  name: '',
  slug: '',
  logoUrl: '',
  websiteUrl: '',
  description: '',
  isActive: true
})

// Computed
const filteredBrands = computed(() => {
  let result = brands.value

  // Filter by search
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(brand => 
      brand.name.toLowerCase().includes(keyword) ||
      brand.slug.toLowerCase().includes(keyword)
    )
  }

  // Filter by status
  if (filterStatus.value !== 'all') {
    const isActive = filterStatus.value === 'active'
    result = result.filter(brand => brand.isActive === isActive)
  }

  return result
})

const paginatedBrands = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredBrands.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredBrands.value.length / itemsPerPage)
})

const activeBrandsCount = computed(() => {
  return brands.value.filter(b => b.isActive).length
})

const inactiveBrandsCount = computed(() => {
  return brands.value.filter(b => !b.isActive).length
})

// Methods
const fetchBrands = async () => {
  loading.value = true
  try {
    await adminStore.fetchBrands()
    brands.value = adminStore.brands
  } catch (error) {
    console.error('Error fetching brands:', error)
    alert('Lỗi khi tải danh sách thương hiệu')
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    id: null,
    name: '',
    slug: '',
    logoUrl: '',
    websiteUrl: '',
    description: '',
    isActive: true
  }
  showModal.value = true
}

const openEditModal = (brand) => {
  isEditMode.value = true
  formData.value = { ...brand }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  formData.value = {
    id: null,
    name: '',
    slug: '',
    logoUrl: '',
    websiteUrl: '',
    description: '',
    isActive: true
  }
}

const saveBrand = async () => {
  saving.value = true
  try {
    if (isEditMode.value) {
      await adminStore.updateBrand(formData.value.id, formData.value)
    } else {
      await adminStore.createBrand(formData.value)
    }
    await fetchBrands()
    closeModal()
    alert(`${isEditMode.value ? 'Cập nhật' : 'Thêm'} thương hiệu thành công!`)
  } catch (error) {
    console.error('Error saving brand:', error)
    alert('Lỗi khi lưu thương hiệu')
  } finally {
    saving.value = false
  }
}

const confirmDelete = (brand) => {
  brandToDelete.value = brand
  showDeleteModal.value = true
}

const deleteBrand = async () => {
  deleting.value = true
  try {
    await adminStore.deleteBrand(brandToDelete.value.id)
    await fetchBrands()
    showDeleteModal.value = false
    brandToDelete.value = null
    alert('Xóa thương hiệu thành công!')
  } catch (error) {
    console.error('Error deleting brand:', error)
    alert('Lỗi khi xóa thương hiệu')
  } finally {
    deleting.value = false
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = 'all'
  currentPage.value = 1
}

const formatDate = (dateString) => {
  if (!dateString) return '—'
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN')
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

const handleImageError = (e) => {
  e.target.style.display = 'none'
}

// Lifecycle
onMounted(() => {
  fetchBrands()
})
</script>

<style scoped>
/* ═══════════════════════════════════════════════════════════════════
   VIEW-SPECIFIC STYLES ONLY
   All common styles (buttons, forms, tables, modals) use Design System v2.0
   ═══════════════════════════════════════════════════════════════════ */

/* Brand Logo */
.brand-logo {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--dark-bg-glass-light);
  border: 1px solid var(--dark-border-primary);
}

.brand-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.logo-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-tertiary);
  font-size: 1.5rem;
}

/* Brand Info */
.brand-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.brand-info strong {
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.brand-desc {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
  margin: 0;
}

/* Code Badge */
.code-badge {
  background: rgba(167, 139, 250, 0.1);
  color: var(--primary-light);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-family: var(--font-mono);
  font-size: var(--text-xs);
  border: 1px solid rgba(167, 139, 250, 0.2);
}

/* Website Link */
.website-link {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--primary-light);
  text-decoration: none;
  font-size: var(--text-sm);
  transition: var(--transition-fast);
}

.website-link:hover {
  color: var(--primary-color);
  text-decoration: underline;
}

.website-link .material-icons {
  font-size: 16px;
}
</style>

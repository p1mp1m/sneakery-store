<template>
  <div class="admin-brands">
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
      <div class="stats-card success">
        <div class="stats-icon">
          <span class="material-icons">check_circle</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ activeBrandsCount }}</div>
          <div class="stats-label">ĐANG HOẠT ĐỘNG</div>
        </div>
      </div>
      
      <div class="stats-card warning">
        <div class="stats-icon">
          <span class="material-icons">pause_circle</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ inactiveBrandsCount }}</div>
          <div class="stats-label">TẠM NGƯNG</div>
        </div>
      </div>
      
      <div class="stats-card info">
        <div class="stats-icon">
          <span class="material-icons">inventory</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ brands.length }}</div>
          <div class="stats-label">TỔNG THƯƠNG HIỆU</div>
        </div>
      </div>
    </div>

    <!-- ===== FILTERS ===== -->
    <div class="filters-section">
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">search</span>
            Tìm kiếm
          </label>
          <input 
            type="text" 
            class="filter-input" 
            v-model="searchKeyword"
            placeholder="Tìm theo tên thương hiệu..."
          />
        </div>
        
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">filter_list</span>
            Trạng thái
          </label>
          <select class="filter-select" v-model="filterStatus">
            <option value="all">Tất cả</option>
            <option value="active">Đang hoạt động</option>
            <option value="inactive">Tạm ngưng</option>
          </select>
        </div>

        <div class="filter-group">
          <button class="btn btn-outline" @click="resetFilters">
            <span class="material-icons">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- ===== LOADING STATE ===== -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">Đang tải dữ liệu...</p>
    </div>

    <!-- ===== EMPTY STATE ===== -->
    <div v-else-if="filteredBrands.length === 0" class="empty-state">
      <span class="material-icons empty-icon">local_offer</span>
      <h3 class="empty-title">Không có thương hiệu nào</h3>
      <p class="empty-description">Bắt đầu thêm thương hiệu đầu tiên cho cửa hàng của bạn</p>
      <button class="btn btn-primary" @click="openCreateModal">
        <span class="material-icons">add</span>
        Thêm Thương hiệu
      </button>
    </div>

    <!-- ===== BRANDS TABLE ===== -->
    <div v-else class="table-container">
      <table class="brands-table">
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
              <div class="brand-name">
                <strong>{{ brand.name }}</strong>
                <p v-if="brand.description" class="brand-desc">{{ truncateText(brand.description, 50) }}</p>
              </div>
            </td>
            <td>
              <code class="slug-code">{{ brand.slug }}</code>
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
              <div class="action-buttons">
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
    <div v-if="totalPages > 1" class="pagination-container">
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
      <div class="modal-content" @click.stop>
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
                  class="form-input" 
                  v-model="formData.name"
                  placeholder="VD: Nike, Adidas..."
                  required
                />
              </div>
              
              <div class="form-group">
                <label class="form-label required">Slug</label>
                <input 
                  type="text" 
                  class="form-input" 
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
                class="form-input" 
                v-model="formData.logoUrl"
                placeholder="https://example.com/logo.png"
              />
            </div>

            <div class="form-group">
              <label class="form-label">Website</label>
              <input 
                type="url" 
                class="form-input" 
                v-model="formData.websiteUrl"
                placeholder="https://example.com"
              />
            </div>

            <div class="form-group">
              <label class="form-label">Mô tả</label>
              <textarea 
                class="form-textarea" 
                v-model="formData.description"
                placeholder="Nhập mô tả về thương hiệu..."
                rows="4"
              ></textarea>
            </div>

            <div class="form-group">
              <label class="form-checkbox">
                <input 
                  type="checkbox" 
                  v-model="formData.isActive"
                />
                <span>Kích hoạt thương hiệu</span>
              </label>
            </div>

            <div class="modal-actions">
              <button type="button" class="btn btn-outline" @click="closeModal">
                <span class="material-icons">close</span>
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :disabled="saving">
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
      <div class="modal-content modal-sm" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons text-danger">warning</span>
            Xác nhận xóa
          </h2>
          <button class="modal-close" @click="showDeleteModal = false">
            <span class="material-icons">close</span>
          </button>
        </div>
        
        <div class="modal-body">
          <p>Bạn có chắc chắn muốn xóa thương hiệu <strong>{{ brandToDelete?.name }}</strong>?</p>
          <p class="text-muted">Hành động này không thể hoàn tác.</p>
          
          <div class="modal-actions">
            <button class="btn btn-outline" @click="showDeleteModal = false">
              <span class="material-icons">close</span>
              Hủy
            </button>
            <button class="btn btn-danger" @click="deleteBrand" :disabled="deleting">
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
/* ═══ PAGE LAYOUT ═══ */
.admin-brands {
  padding: var(--space-8);
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* ═══ PAGE HEADER ═══ */
.page-header {
  background: var(--bg-card);
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
  align-items: center;
  gap: var(--space-6);
}

.title-section {
  flex: 1;
}

.page-title {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.page-title .material-icons {
  font-size: 2rem;
  color: var(--accent-primary);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.page-subtitle {
  color: var(--text-tertiary);
  margin: 0;
  font-size: var(--text-base);
}

.header-actions {
  display: flex;
  gap: var(--space-3);
}

/* ═══ STATS GRID ═══ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stats-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  gap: var(--space-4);
  position: relative;
  overflow: hidden;
  transition: all var(--transition-smooth);
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--gradient-primary);
  transform: scaleY(0);
  transform-origin: top;
  transition: transform var(--transition-smooth);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
}

.stats-card:hover::before {
  transform: scaleY(1);
}

.stats-card.success::before {
  background: var(--gradient-success);
}

.stats-card.warning::before {
  background: var(--gradient-warning);
}

.stats-card.info::before {
  background: var(--gradient-info);
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all var(--transition-smooth);
}

.stats-card.success .stats-icon {
  background: var(--success-bg);
  color: var(--success-text);
}

.stats-card.warning .stats-icon {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.stats-card.info .stats-icon {
  background: var(--info-bg);
  color: var(--info-text);
}

.stats-card:hover .stats-icon {
  transform: scale(1.1) rotate(5deg);
}

.stats-icon .material-icons {
  font-size: 28px;
}

.stats-content {
  flex: 1;
}

.stats-value {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: var(--space-2);
}

.stats-label {
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* ═══ FILTERS ═══ */
.filters-section {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.filter-row {
  display: grid;
  grid-template-columns: 2fr 1fr auto;
  gap: var(--space-4);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-label {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.filter-label .material-icons {
  font-size: 1rem;
  color: var(--accent-primary);
}

.filter-input,
.filter-select {
  padding: var(--space-3) var(--space-4);
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  background: rgba(15, 23, 42, 0.4);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

.filter-input:hover,
.filter-select:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.6);
}

.filter-input:focus,
.filter-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.8);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

/* ═══ LOADING & EMPTY STATES ═══ */
.loading-container {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-16) var(--space-8);
  text-align: center;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  color: var(--text-tertiary);
  margin: 0;
}

.empty-state {
  background: var(--bg-card);
  border: 2px dashed var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-16) var(--space-8);
  text-align: center;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.empty-icon {
  font-size: 80px;
  color: var(--text-tertiary);
  opacity: 0.5;
  margin-bottom: var(--space-4);
}

.empty-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.empty-description {
  color: var(--text-tertiary);
  margin: 0 0 var(--space-6) 0;
}

/* ═══ TABLE ═══ */
.table-container {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  margin-bottom: var(--space-6);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.brands-table {
  width: 100%;
  border-collapse: collapse;
}

.brands-table thead {
  background: var(--table-header-bg);
}

.brands-table th {
  padding: var(--space-4);
  text-align: left;
  font-weight: var(--font-semibold);
  color: var(--color-white);
  font-size: var(--text-sm);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.brands-table tbody tr {
  border-bottom: 1px solid var(--border-primary);
  transition: all var(--transition-fast);
}

.brands-table tbody tr:hover {
  background: var(--gradient-purple-soft);
  transform: translateX(2px);
}

.brands-table td {
  padding: var(--space-4);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* ═══ TABLE CONTENT ═══ */
.brand-logo {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--border-primary);
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.logo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-purple-soft);
}

.logo-placeholder .material-icons {
  font-size: 28px;
  color: var(--accent-primary);
}

.brand-name strong {
  color: var(--text-primary);
  font-size: var(--text-base);
}

.brand-desc {
  margin: var(--space-1) 0 0 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.slug-code {
  font-family: var(--font-mono);
  font-size: var(--text-xs);
  background: var(--bg-tertiary);
  color: var(--accent-primary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-primary);
}

.website-link {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--accent-primary);
  text-decoration: none;
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
}

.website-link:hover {
  color: var(--accent-light);
  text-decoration: underline;
}

.website-link .material-icons {
  font-size: 16px;
}

.text-muted {
  color: var(--text-quaternary);
}

/* ═══ STATUS BADGES ═══ */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-badge .material-icons {
  font-size: 16px;
}

.status-badge.status-active {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.status-badge.status-inactive {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

/* ═══ ACTION BUTTONS ═══ */
.action-buttons {
  display: flex;
  gap: var(--space-2);
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-icon .material-icons {
  font-size: var(--text-lg);
}

.btn-edit {
  background: var(--info-bg);
  color: var(--info-text);
  border: 1px solid var(--info-border);
}

.btn-edit:hover {
  background: var(--info-solid);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-blue);
}

.btn-delete {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

.btn-delete:hover {
  background: var(--error-solid);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-red);
}

/* ═══ PAGINATION ═══ */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-primary);
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  background: var(--gradient-purple-soft);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.pagination-btn:hover:not(:disabled) {
  background: var(--gradient-primary);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

/* ═══ BUTTONS ═══ */
.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  text-decoration: none;
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

.btn-outline {
  background: transparent;
  color: var(--text-primary);
  border: 2px solid var(--border-primary);
}

.btn-outline:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--accent-primary);
}

.btn-danger {
  background: var(--gradient-danger);
  color: var(--color-white);
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-red);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
}

.modal-content {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  animation: modalSlideIn 0.3s ease-out;
}

.modal-content.modal-sm {
  max-width: 400px;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
}

.modal-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.modal-title .material-icons {
  font-size: 24px;
  color: var(--accent-primary);
}

.modal-title .text-danger {
  color: var(--error-text);
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

.modal-body {
  padding: var(--space-6);
}

.modal-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

/* ═══ FORM ═══ */
.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-4);
}

.form-group {
  margin-bottom: var(--space-4);
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-label.required::after {
  content: ' *';
  color: var(--error-text);
}

.form-input,
.form-textarea {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  background: rgba(15, 23, 42, 0.4);
  color: var(--text-primary);
  transition: all var(--transition-fast);
  font-family: inherit;
}

.form-input:hover,
.form-textarea:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.6);
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.8);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
  user-select: none;
}

.form-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--accent-primary);
}

.form-checkbox span {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

/* ═══ RESPONSIVE ═══ */
@media (max-width: 768px) {
  .admin-brands {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-6);
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .brands-table {
    font-size: var(--text-xs);
  }

  .brands-table th,
  .brands-table td {
    padding: var(--space-2);
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}

/* ═══ GLOW EFFECTS ═══ */
.shadow-glow-blue {
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.4);
}

.shadow-glow-red {
  box-shadow: 0 4px 20px rgba(239, 68, 68, 0.4);
}

.shadow-glow-purple {
  box-shadow: 0 4px 20px rgba(167, 139, 250, 0.4);
}
</style>

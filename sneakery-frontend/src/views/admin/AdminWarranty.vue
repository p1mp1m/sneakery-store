<template>
  <div class="admin-page admin-warranty">
    <!-- Enhanced Page Header -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">verified_user</span>
            Quản lý Bảo Hành
          </h1>
          <p class="page-subtitle">Quản lý yêu cầu bảo hành và sửa chữa sản phẩm</p>
        </div>
        
        <div class="header-actions">
          <button @click="exportWarranties('csv')" class="btn btn-secondary">
            <span class="material-icons">file_download</span>
            Xuất CSV
          </button>
          <button @click="exportWarranties('json')" class="btn btn-secondary">
            <span class="material-icons">description</span>
            Xuất JSON
          </button>
          <button @click="showBulkActions = !showBulkActions" class="btn btn-secondary">
            <span class="material-icons">checklist</span>
            Hành động hàng loạt
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="stats-grid animate-fade-up">
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-warning)">
          <span class="material-icons">schedule</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">Chờ xử lý</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-info)">
          <span class="material-icons">build</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.inProgress }}</div>
          <div class="stat-label">Đang sửa chữa</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-success)">
          <span class="material-icons">done_all</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.completed }}</div>
          <div class="stat-label">Hoàn thành</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-danger)">
          <span class="material-icons">highlight_off</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.rejected }}</div>
          <div class="stat-label">Từ chối</div>
        </div>
      </div>
    </div>
    
    <!-- Bulk Actions Bar -->
    <transition name="slide-down">
      <div v-if="showBulkActions && selectedWarranties.length > 0" class="bulk-actions-bar">
        <div class="bulk-info">
          <span class="material-icons">check_circle</span>
          <span class="bulk-count">{{ selectedWarranties.length }} mục đã chọn</span>
        </div>
        
        <div class="bulk-buttons">
          <button @click="bulkApprove" class="btn btn-success btn-sm">
            <span class="material-icons">check</span>
            Chấp nhận
          </button>
          <button @click="bulkReject" class="btn btn-danger btn-sm">
            <span class="material-icons">close</span>
            Từ chối
          </button>
          <button @click="clearSelection" class="btn btn-secondary btn-sm">
            <span class="material-icons">clear</span>
            Bỏ chọn
          </button>
        </div>
      </div>
    </transition>

    <!-- Enhanced Filters -->
    <div class="filters-section">
      <div class="filter-row">
        <div class="filter-group">
          <label>Tìm kiếm</label>
          <div class="search-box">
            <span class="material-icons search-icon">search</span>
            <input 
              v-model="filters.search"
              type="text" 
              class="search-input" 
              placeholder="Tìm theo mã, khách hàng, sản phẩm..."
              @input="handleSearch"
            />
            <button v-if="filters.search" @click="filters.search = ''" class="search-clear">
              <span class="material-icons">close</span>
            </button>
          </div>
        </div>
        <div class="filter-group">
          <label>Trạng thái</label>
          <select v-model="filters.status" @change="fetchWarranties" class="form-control">
            <option value="">Tất cả</option>
            <option value="pending">Chờ xử lý</option>
            <option value="approved">Đã chấp nhận</option>
            <option value="in_progress">Đang sửa chữa</option>
            <option value="completed">Hoàn thành</option>
            <option value="rejected">Từ chối</option>
          </select>
        </div>
        <div class="filter-group">
          <label>Loại bảo hành</label>
          <select v-model="filters.type" @change="fetchWarranties" class="form-control">
            <option value="">Tất cả</option>
            <option value="repair">Sửa chữa</option>
            <option value="replace">Đổi mới</option>
            <option value="refund">Hoàn tiền</option>
          </select>
        </div>
        <div class="filter-group">
          <label>&nbsp;</label>
          <button @click="resetFilters" class="btn btn-secondary">
            <span class="material-icons">refresh</span>
            Xóa bộ lọc
          </button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="table-container">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="warranties.length === 0" class="empty-state">
        <i class="material-icons">verified_user</i>
        <h3>Chưa có yêu cầu bảo hành</h3>
        <p>Danh sách yêu cầu bảo hành sẽ hiển thị ở đây</p>
      </div>

      <table v-else class="admin-table">
        <thead>
          <tr>
            <th v-if="showBulkActions" style="width: 50px;">
              <input 
                type="checkbox" 
                :checked="isAllSelected"
                @change="toggleSelectAll"
                class="checkbox-input"
              />
            </th>
            <th>Mã BH</th>
            <th>Khách hàng</th>
            <th>Sản phẩm</th>
            <th>Vấn đề</th>
            <th>Loại BH</th>
            <th>Ngày yêu cầu</th>
            <th>Thời hạn</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in warranties" :key="item.id">
            <td v-if="showBulkActions">
              <input 
                type="checkbox" 
                :checked="isSelected(item.id)"
                @change="toggleSelect(item.id)"
                class="checkbox-input"
              />
            </td>
            <td>
              <strong class="warranty-code">#WTY{{ item.id.toString().padStart(6, '0') }}</strong>
            </td>
            <td>
              <div class="customer-cell">
                <strong>{{ item.customerName }}</strong>
                <p>{{ item.customerEmail }}</p>
                <p>{{ item.customerPhone }}</p>
              </div>
            </td>
            <td>
              <div class="product-cell">
                <img :src="item.productImage" :alt="item.productName" class="product-thumb" />
                <div>
                  <strong>{{ item.productName }}</strong>
                  <p>{{ item.variant }}</p>
                  <p>Mua: {{ formatDate(item.purchaseDate) }}</p>
                </div>
              </div>
            </td>
            <td>
              <p class="issue-text">{{ item.issue }}</p>
            </td>
            <td>
              <span class="warranty-type-badge" :class="getTypeBadgeClass(item.warrantyType)">
                {{ getWarrantyTypeText(item.warrantyType) }}
              </span>
            </td>
            <td>{{ formatDate(item.createdAt) }}</td>
            <td>
              <div class="warranty-period" :class="{ 'expired': isWarrantyExpired(item) }">
                <i class="material-icons">schedule</i>
                <span v-if="isWarrantyExpired(item)" class="text-danger">Hết hạn</span>
                <span v-else>{{ calculateDaysLeft(item) }} ngày</span>
              </div>
            </td>
            <td>
              <span class="status-badge" :class="getStatusClass(item.status)">
                {{ getStatusText(item.status) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="viewWarrantyDetail(item)" class="btn-icon" title="Xem chi tiết">
                  <span class="material-icons">visibility</span>
                </button>
                <button 
                  v-if="item.status === 'pending'" 
                  @click="approveWarranty(item)" 
                  class="btn-icon btn-success" 
                  title="Chấp nhận"
                >
                  <span class="material-icons">check_circle</span>
                </button>
                <button 
                  v-if="item.status === 'approved'" 
                  @click="updateStatus(item, 'in_progress')" 
                  class="btn-icon btn-info" 
                  title="Bắt đầu sửa"
                >
                  <span class="material-icons">build</span>
                </button>
                <button 
                  v-if="item.status === 'in_progress'" 
                  @click="updateStatus(item, 'completed')" 
                  class="btn-icon btn-success" 
                  title="Hoàn thành"
                >
                  <span class="material-icons">done_all</span>
                </button>
                <button 
                  @click="openUploadModal(item)" 
                  class="btn-icon" 
                  title="Tải tài liệu"
                >
                  <span class="material-icons">upload_file</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Document Upload Modal -->
    <div v-if="showUploadModal" class="modal-overlay" @click="showUploadModal = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Tải tài liệu bảo hành</h3>
          <button @click="showUploadModal = false" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="upload-area" @click="triggerFileInput">
            <span class="material-icons upload-icon">cloud_upload</span>
            <p>Nhấp để tải lên hoặc kéo thả file vào đây</p>
            <p class="upload-hint">Hỗ trợ: PDF, JPG, PNG (tối đa 10MB)</p>
            <input 
              ref="fileInput" 
              type="file" 
              multiple 
              accept="image/*,.pdf"
              @change="handleFileUpload"
              style="display: none"
            />
          </div>
          
          <div v-if="uploadedFiles.length > 0" class="uploaded-files">
            <h4>Files đã tải lên:</h4>
            <div v-for="(file, index) in uploadedFiles" :key="index" class="file-item">
              <span class="material-icons file-icon">description</span>
              <span class="file-name">{{ file.name }}</span>
              <button @click="removeFile(index)" class="btn-remove-file">
                <span class="material-icons">delete</span>
              </button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showUploadModal = false" class="btn btn-secondary">Hủy</button>
          <button @click="saveDocuments" class="btn btn-primary">
            <span class="material-icons">save</span>
            Lưu tài liệu
          </button>
        </div>
      </div>
    </div>

    <!-- Detail Dialog -->
    <div v-if="showDetailDialog" class="modal-overlay" @click="showDetailDialog = false">
      <div class="modal modal-lg" @click.stop>
        <div class="modal-header">
          <h3>Chi tiết yêu cầu bảo hành #WTY{{ selectedWarranty?.id.toString().padStart(6, '0') }}</h3>
          <button @click="showDetailDialog = false" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedWarranty" class="warranty-detail">
            <div class="detail-section">
              <h4>Thông tin khách hàng</h4>
              <p><strong>Họ tên:</strong> {{ selectedWarranty.customerName }}</p>
              <p><strong>Email:</strong> {{ selectedWarranty.customerEmail }}</p>
              <p><strong>SĐT:</strong> {{ selectedWarranty.customerPhone }}</p>
            </div>
            <div class="detail-section">
              <h4>Thông tin sản phẩm</h4>
              <p><strong>Sản phẩm:</strong> {{ selectedWarranty.productName }}</p>
              <p><strong>Biến thể:</strong> {{ selectedWarranty.variant }}</p>
              <p><strong>Ngày mua:</strong> {{ formatDate(selectedWarranty.purchaseDate) }}</p>
              <p><strong>Thời hạn BH:</strong> {{ selectedWarranty.warrantyMonths }} tháng</p>
            </div>
            <div class="detail-section">
              <h4>Vấn đề & Giải pháp</h4>
              <p><strong>Mô tả vấn đề:</strong> {{ selectedWarranty.issue }}</p>
              <p><strong>Loại BH yêu cầu:</strong> {{ getWarrantyTypeText(selectedWarranty.warrantyType) }}</p>
            </div>
            <div v-if="selectedWarranty.images" class="detail-section">
              <h4>Hình ảnh</h4>
              <div class="warranty-images">
                <img 
                  v-for="(img, index) in selectedWarranty.images" 
                  :key="index"
                  :src="img" 
                  alt="Warranty image"
                  class="warranty-image"
                />
              </div>
            </div>
            <div class="detail-section">
              <h4>Trạng thái</h4>
              <div class="status-timeline">
                <div v-for="(log, index) in selectedWarranty.statusLogs" :key="index" class="timeline-item">
                  <div class="timeline-dot"></div>
                  <div class="timeline-content">
                    <strong>{{ getStatusText(log.status) }}</strong>
                    <p>{{ formatDate(log.timestamp) }}</p>
                    <p v-if="log.note">{{ log.note }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showDetailDialog = false" class="btn btn-secondary">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { debounce } from '@/utils/debounce'

// State
const loading = ref(false)
const showDetailDialog = ref(false)
const showUploadModal = ref(false)
const showBulkActions = ref(false)
const selectedWarranty = ref(null)
const warranties = ref([])
const selectedWarranties = ref([])
const uploadedFiles = ref([])
const fileInput = ref(null)

const filters = reactive({
  search: '',
  status: '',
  type: ''
})

const stats = reactive({
  pending: 0,
  inProgress: 0,
  completed: 0,
  rejected: 0
})

// Computed
const isAllSelected = computed(() => {
  return warranties.value.length > 0 && selectedWarranties.value.length === warranties.value.length
})

// Methods
const fetchWarranties = async () => {
  try {
    loading.value = true
    // TODO: Call API
    // Mock data
    warranties.value = [
      {
        id: 1,
        customerName: 'Nguyễn Văn A',
        customerEmail: 'nguyenvana@gmail.com',
        customerPhone: '0901234567',
        productName: 'Nike Air Max 270',
        productImage: '/placeholder.png',
        variant: 'Size 42 - Đen',
        purchaseDate: '2024-11-15T00:00:00',
        warrantyMonths: 12,
        issue: 'Đế giày bị bong tróc sau 2 tháng sử dụng',
        warrantyType: 'repair',
        status: 'pending',
        createdAt: '2025-01-20T10:30:00',
        images: ['/placeholder.png'],
        statusLogs: [
          { status: 'pending', timestamp: '2025-01-20T10:30:00', note: 'Yêu cầu được tạo' }
        ]
      }
    ]
    updateStats()
  } catch (error) {
    console.error('Lỗi tải dữ liệu:', error)
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.pending = warranties.value.filter(w => w.status === 'pending').length
  stats.inProgress = warranties.value.filter(w => w.status === 'in_progress').length
  stats.completed = warranties.value.filter(w => w.status === 'completed').length
  stats.rejected = warranties.value.filter(w => w.status === 'rejected').length
}

const handleSearch = debounce(() => {
  fetchWarranties()
}, 300)

const resetFilters = () => {
  filters.search = ''
  filters.status = ''
  filters.type = ''
  fetchWarranties()
}

// Selection Methods
const isSelected = (id) => {
  return selectedWarranties.value.includes(id)
}

const toggleSelect = (id) => {
  if (isSelected(id)) {
    selectedWarranties.value = selectedWarranties.value.filter(wId => wId !== id)
  } else {
    selectedWarranties.value.push(id)
  }
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedWarranties.value = []
  } else {
    selectedWarranties.value = warranties.value.map(w => w.id)
  }
}

const clearSelection = () => {
  selectedWarranties.value = []
  showBulkActions.value = false
}

// Bulk Actions
const bulkApprove = async () => {
  ElMessageBox.confirm(
    `Bạn có chắc muốn chấp nhận ${selectedWarranties.value.length} yêu cầu bảo hành?`,
    'Xác nhận',
    {
      confirmButtonText: 'Chấp nhận',
      cancelButtonText: 'Hủy',
      type: 'warning'
    }
  ).then(() => {
    // TODO: Call API
    ElMessage.success(`Đã chấp nhận ${selectedWarranties.value.length} yêu cầu!`)
    clearSelection()
    fetchWarranties()
  }).catch(() => {
    ElMessage.info('Đã hủy')
  })
}

const bulkReject = async () => {
  ElMessageBox.confirm(
    `Bạn có chắc muốn từ chối ${selectedWarranties.value.length} yêu cầu bảo hành?`,
    'Xác nhận',
    {
      confirmButtonText: 'Từ chối',
      cancelButtonText: 'Hủy',
      type: 'error'
    }
  ).then(() => {
    // TODO: Call API
    ElMessage.success(`Đã từ chối ${selectedWarranties.value.length} yêu cầu!`)
    clearSelection()
    fetchWarranties()
  }).catch(() => {
    ElMessage.info('Đã hủy')
  })
}

// Export Functions
const exportWarranties = (format) => {
  const data = warranties.value.map(w => ({
    'Mã BH': `#WTY${w.id.toString().padStart(6, '0')}`,
    'Khách hàng': w.customerName,
    'Email': w.customerEmail,
    'Điện thoại': w.customerPhone,
    'Sản phẩm': w.productName,
    'Biến thể': w.variant,
    'Vấn đề': w.issue,
    'Loại BH': getWarrantyTypeText(w.warrantyType),
    'Trạng thái': getStatusText(w.status),
    'Ngày yêu cầu': formatDate(w.createdAt),
    'Ngày mua': formatDate(w.purchaseDate)
  }))
  
  const filename = `warranties-${new Date().toISOString().split('T')[0]}`
  
  if (format === 'csv') {
    downloadCsv(data, `${filename}.csv`)
    ElMessage.success('Đã xuất file CSV!')
  } else {
    downloadJson(data, `${filename}.json`)
    ElMessage.success('Đã xuất file JSON!')
  }
}

// Document Upload
const openUploadModal = (item) => {
  selectedWarranty.value = item
  uploadedFiles.value = []
  showUploadModal.value = true
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileUpload = (event) => {
  const files = Array.from(event.target.files)
  files.forEach(file => {
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.error(`File ${file.name} quá lớn (> 10MB)`)
      return
    }
    uploadedFiles.value.push(file)
  })
}

const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1)
}

const saveDocuments = () => {
  if (uploadedFiles.value.length === 0) {
    ElMessage.warning('Vui lòng chọn ít nhất 1 file!')
    return
  }
  
  // TODO: Upload to server
  ElMessage.success(`Đã tải lên ${uploadedFiles.value.length} tài liệu!`)
  showUploadModal.value = false
  uploadedFiles.value = []
}

const viewWarrantyDetail = (item) => {
  selectedWarranty.value = item
  showDetailDialog.value = true
}

const approveWarranty = async (item) => {
  ElMessageBox.confirm(
    'Bạn có chắc muốn chấp nhận yêu cầu bảo hành này?',
    'Xác nhận',
    {
      confirmButtonText: 'Chấp nhận',
      cancelButtonText: 'Hủy',
      type: 'success'
    }
  ).then(() => {
    // TODO: Call API
    ElMessage.success('Đã chấp nhận yêu cầu!')
    fetchWarranties()
  }).catch(() => {
    ElMessage.info('Đã hủy')
  })
}

const updateStatus = async (item, newStatus) => {
  const messages = {
    in_progress: 'Bắt đầu sửa chữa?',
    completed: 'Đánh dấu là hoàn thành?'
  }
  
  ElMessageBox.confirm(
    messages[newStatus],
    'Xác nhận',
    {
      confirmButtonText: 'Xác nhận',
      cancelButtonText: 'Hủy',
      type: 'info'
    }
  ).then(() => {
    // TODO: Call API
    ElMessage.success('Đã cập nhật trạng thái!')
    fetchWarranties()
  }).catch(() => {
    ElMessage.info('Đã hủy')
  })
}

const isWarrantyExpired = (item) => {
  const purchaseDate = new Date(item.purchaseDate)
  const expiryDate = new Date(purchaseDate)
  expiryDate.setMonth(expiryDate.getMonth() + item.warrantyMonths)
  return expiryDate < new Date()
}

const calculateDaysLeft = (item) => {
  const purchaseDate = new Date(item.purchaseDate)
  const expiryDate = new Date(purchaseDate)
  expiryDate.setMonth(expiryDate.getMonth() + item.warrantyMonths)
  const now = new Date()
  const daysLeft = Math.ceil((expiryDate - now) / (1000 * 60 * 60 * 24))
  return daysLeft > 0 ? daysLeft : 0
}

const getWarrantyTypeText = (type) => {
  const types = {
    repair: 'Sửa chữa',
    replace: 'Đổi mới',
    refund: 'Hoàn tiền'
  }
  return types[type] || type
}

const getTypeBadgeClass = (type) => {
  const classes = {
    repair: 'badge-info',
    replace: 'badge-warning',
    refund: 'badge-success'
  }
  return classes[type]
}

const getStatusClass = (status) => {
  const classes = {
    pending: 'status-pending',
    approved: 'status-processing',
    in_progress: 'status-shipped',
    completed: 'status-completed',
    rejected: 'status-cancelled'
  }
  return classes[status]
}

const getStatusText = (status) => {
  const statuses = {
    pending: 'Chờ xử lý',
    approved: 'Đã chấp nhận',
    in_progress: 'Đang sửa',
    completed: 'Hoàn thành',
    rejected: 'Từ chối'
  }
  return statuses[status] || status
}

const formatDate = (dateString) => {
  return new Intl.DateTimeFormat('vi-VN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit'
  }).format(new Date(dateString))
}

// Lifecycle
onMounted(() => {
  fetchWarranties()
})
</script>

<style scoped>
/* ═══════════════════════════════════════════════════════════════════════
   ADMIN WARRANTY - Enhanced with Design System v2.0
   ═══════════════════════════════════════════════════════════════════════ */

/* Page Header Enhancements */
.page-header {
  background: var(--card-bg);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-2xl);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: var(--shadow-card);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-6);
  flex-wrap: wrap;
}

.title-section {
  flex: 1;
  min-width: 250px;
}

.header-actions {
  display: flex;
  gap: var(--space-3);
  flex-wrap: wrap;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-6);
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
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--accent-primary);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .material-icons {
  font-size: 2rem;
  color: white;
}

.stat-content {
  flex: 1;
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

/* Bulk Actions Bar */
.bulk-actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4) var(--space-6);
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1), rgba(167, 139, 250, 0.15));
  border: 1px solid var(--accent-primary);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 4px 16px rgba(167, 139, 250, 0.2);
}

.bulk-info {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.bulk-info .material-icons {
  color: var(--accent-primary);
  font-size: 1.5rem;
}

.bulk-count {
  font-size: var(--text-base);
}

.bulk-buttons {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

/* Slide Down Animation */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Search Box Enhancement */
.search-box {
  position: relative;
  width: 100%;
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
  padding: var(--space-3) var(--space-12) var(--space-3) var(--space-12);
}

.search-clear {
  position: absolute;
  right: var(--space-2);
  top: 50%;
  transform: translateY(-50%);
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

.search-clear:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Checkbox */
.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--accent-primary);
}

/* Upload Modal */
.upload-area {
  border: 2px dashed var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-12);
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-normal);
  background: rgba(15, 23, 42, 0.4);
}

.upload-area:hover {
  border-color: var(--accent-primary);
  background: rgba(167, 139, 250, 0.1);
}

.upload-icon {
  font-size: 4rem;
  color: var(--accent-primary);
  margin-bottom: var(--space-4);
}

.upload-area p {
  color: var(--text-primary);
  font-size: var(--text-base);
  margin: var(--space-2) 0;
}

.upload-hint {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.uploaded-files {
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

.uploaded-files h4 {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4) 0;
}

.file-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: rgba(15, 23, 42, 0.4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-2);
  transition: all var(--transition-fast);
}

.file-item:hover {
  background: rgba(15, 23, 42, 0.6);
  border-color: var(--border-hover);
}

.file-icon {
  color: var(--accent-primary);
  font-size: 1.5rem;
}

.file-name {
  flex: 1;
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.btn-remove-file {
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

.btn-remove-file:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Action Buttons Enhancement */
.action-buttons {
  display: flex;
  gap: var(--space-2);
  align-items: center;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-primary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-icon:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--accent-primary);
  transform: scale(1.1);
}

.btn-icon.btn-success:hover {
  background: var(--success-bg);
  border-color: var(--success-border);
  color: var(--success-text);
}

.btn-icon.btn-info:hover {
  background: var(--info-bg);
  border-color: var(--info-border);
  color: var(--info-text);
}

/* Warranty Code */
.warranty-code {
  font-family: var(--font-mono);
  color: var(--accent-primary);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  background: var(--gradient-purple-soft);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
}

/* Customer Cell */
.customer-cell {
  min-width: 180px;
}

.customer-cell strong {
  display: block;
  margin-bottom: var(--space-1);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.customer-cell p {
  margin: var(--space-1) 0;
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

/* Product Cell */
.product-cell {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.product-thumb {
  width: 50px;
  height: 50px;
  border-radius: var(--radius-md);
  object-fit: cover;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-primary);
  transition: var(--transition-normal);
}

.product-thumb:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-md);
}

.product-cell strong {
  display: block;
  margin-bottom: var(--space-1);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.product-cell p {
  margin: var(--space-1) 0;
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

/* Issue Text */
.issue-text {
  max-width: 250px;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
}

/* Warranty Type Badge */
.warranty-type-badge {
  display: inline-block;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  transition: var(--transition-fast);
}

.warranty-type-badge.badge-info {
  background: var(--info-bg);
  color: var(--info-text);
  border: 1px solid var(--info-border);
}

.warranty-type-badge.badge-warning {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.warranty-type-badge.badge-success {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

/* Warranty Period */
.warranty-period {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--success-text);
  font-weight: var(--font-medium);
}

.warranty-period.expired {
  color: var(--error-text);
}

.warranty-period .material-icons {
  font-size: 1.125rem;
}

.text-danger {
  color: var(--error-text);
  font-weight: var(--font-semibold);
}

/* Warranty Detail */
.warranty-detail {
  padding: var(--space-4);
}

.detail-section {
  margin-bottom: var(--space-6);
  padding-bottom: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
  animation: fadeInUp 0.4s ease-out;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 var(--space-4) 0;
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.detail-section h4::before {
  content: '';
  width: 4px;
  height: 20px;
  background: var(--gradient-primary);
  border-radius: var(--radius-sm);
}

.detail-section p {
  margin: var(--space-2) 0;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
}

.detail-section p strong {
  color: var(--text-primary);
  font-weight: var(--font-medium);
  display: inline-block;
  min-width: 140px;
}

/* Warranty Images */
.warranty-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: var(--space-4);
  margin-top: var(--space-3);
}

.warranty-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  border: 2px solid var(--border-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.warranty-image:hover {
  border-color: var(--accent-primary);
  box-shadow: var(--shadow-glow-purple);
  transform: scale(1.05);
}

/* Status Timeline */
.status-timeline {
  position: relative;
  padding-left: var(--space-8);
}

.status-timeline::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: var(--gradient-primary);
  opacity: 0.3;
}

.timeline-item {
  position: relative;
  padding-bottom: var(--space-6);
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: calc(-1 * var(--space-8));
  top: 4px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: var(--accent-primary);
  border: 3px solid var(--bg-card);
  box-shadow: 0 0 0 2px var(--border-primary);
  z-index: 1;
  transition: all 0.3s ease;
}

.timeline-item:last-child .timeline-dot {
  background: var(--success-solid);
  box-shadow: 0 0 0 2px var(--success-border), 0 0 12px var(--success-solid);
}

.timeline-item:hover .timeline-dot {
  transform: scale(1.3);
  box-shadow: 0 0 0 2px var(--accent-primary), 0 0 16px var(--accent-primary);
}

.timeline-content {
  background: rgba(15, 23, 42, 0.4);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-subtle);
  transition: all 0.2s ease;
}

.timeline-content:hover {
  background: rgba(15, 23, 42, 0.6);
  border-color: var(--border-primary);
}

.timeline-content strong {
  display: block;
  margin-bottom: var(--space-1);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.timeline-content p {
  margin: var(--space-1) 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* Animations and responsive styles use global Design System v2.0 classes */
</style>




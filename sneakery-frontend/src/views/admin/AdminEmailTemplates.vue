<template>
  <div class="admin-page admin-email-templates">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">email</span>
            Quản lý Email Templates
          </h1>
          <p class="page-subtitle">Tạo và quản lý các mẫu email tự động</p>
        </div>
        <div class="header-actions">
          <button @click="exportTemplates('csv')" class="btn btn-secondary">
            <span class="material-icons">file_download</span>
            CSV
          </button>
          <button @click="exportTemplates('json')" class="btn btn-secondary">
            <span class="material-icons">code</span>
            JSON
          </button>
          <button @click="openCreateModal" class="btn btn-primary">
            <span class="material-icons">add</span>
            Tạo mới
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="stats-grid animate-fade-up">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <span class="material-icons">email</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Tổng templates</div>
          <div class="stat-value">{{ formatNumber(totalTemplates) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            +{{ formatNumber(newTemplatesThisMonth) }} tháng này
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <span class="material-icons">check_circle</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Đang hoạt động</div>
          <div class="stat-value">{{ formatNumber(activeTemplates) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">done</span>
            {{ Math.round((activeTemplates / totalTemplates) * 100) || 0 }}% tổng số
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <span class="material-icons">schedule</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Đã gửi hôm nay</div>
          <div class="stat-value">{{ formatNumber(emailsSentToday) }}</div>
          <div class="stat-change neutral">
            <span class="material-icons">info</span>
            {{ formatNumber(emailsSentThisWeek) }} tuần này
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <span class="material-icons">rate_review</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Tỷ lệ mở</div>
          <div class="stat-value">{{ openRate }}%</div>
          <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            +2.3% so với tuần trước
          </div>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="filters-section animate-fade-up">
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">search</span>
            Tìm kiếm
          </label>
          <div class="search-box">
            <span class="material-icons search-icon">search</span>
            <input 
              type="text" 
              class="search-input" 
              v-model="searchKeyword"
              placeholder="Tìm theo tên template, chủ đề..."
            />
            <button v-if="searchKeyword" @click="clearSearch" class="search-clear">
              <span class="material-icons">close</span>
            </button>
          </div>
        </div>
        
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">category</span>
            Loại template
          </label>
          <select class="filter-select" v-model="filterType">
            <option value="all">Tất cả</option>
            <option value="welcome">Chào mừng</option>
            <option value="order">Đơn hàng</option>
            <option value="promotion">Khuyến mãi</option>
            <option value="notification">Thông báo</option>
            <option value="reminder">Nhắc nhở</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">toggle_on</span>
            Trạng thái
          </label>
          <select class="filter-select" v-model="filterStatus">
            <option value="all">Tất cả</option>
            <option value="active">Hoạt động</option>
            <option value="inactive">Tạm dừng</option>
            <option value="draft">Bản nháp</option>
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

    <!-- Templates Grid -->
    <div class="templates-container animate-fade-up">
      <div class="templates-header">
        <h3 class="templates-title">Danh sách Email Templates</h3>
        <div class="templates-actions">
          <span class="templates-info">{{ filteredTemplates.length }} templates</span>
          <div class="view-toggle">
            <button 
              @click="viewMode = 'grid'" 
              :class="['view-btn', { active: viewMode === 'grid' }]"
            >
              <span class="material-icons">grid_view</span>
            </button>
            <button 
              @click="viewMode = 'list'" 
              :class="['view-btn', { active: viewMode === 'list' }]"
            >
              <span class="material-icons">list</span>
            </button>
          </div>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="filteredTemplates.length === 0" class="empty-state">
        <span class="material-icons">email</span>
        <h3>Không có template nào</h3>
        <p>Chưa có email template nào được tạo</p>
      </div>

      <!-- Grid View -->
      <div v-else-if="viewMode === 'grid'" class="templates-grid">
        <div 
          v-for="template in paginatedTemplates" 
          :key="template.id" 
          class="template-card"
          @click="viewTemplate(template)"
        >
          <div class="template-header">
            <div class="template-icon">
              <span class="material-icons">{{ getTemplateIcon(template.type) }}</span>
            </div>
            <div class="template-status">
              <span :class="['status-badge', template.isActive ? 'active' : 'inactive']">
                {{ template.isActive ? 'Hoạt động' : 'Tạm dừng' }}
              </span>
            </div>
          </div>
          
          <div class="template-content">
            <h4 class="template-name">{{ template.name }}</h4>
            <p class="template-subject">{{ template.subject }}</p>
            <div class="template-type">
              <span class="type-badge">{{ getTemplateTypeText(template.type) }}</span>
            </div>
          </div>
          
          <div class="template-footer">
            <div class="template-stats">
              <div class="stat">
                <span class="material-icons">send</span>
                <span>{{ formatNumber(template.sentCount) }}</span>
              </div>
              <div class="stat">
                <span class="material-icons">visibility</span>
                <span>{{ template.openRate }}%</span>
              </div>
            </div>
            <div class="template-actions">
              <button @click.stop="editTemplate(template)" class="btn-icon btn-edit" title="Chỉnh sửa">
                <span class="material-icons">edit</span>
              </button>
              <button @click.stop="duplicateTemplate(template)" class="btn-icon btn-duplicate" title="Sao chép">
                <span class="material-icons">content_copy</span>
              </button>
              <button @click.stop="deleteTemplate(template)" class="btn-icon btn-delete" title="Xóa">
                <span class="material-icons">delete</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- List View -->
      <div v-else class="templates-list">
        <div class="list-header">
          <div class="list-column">Template</div>
          <div class="list-column">Loại</div>
          <div class="list-column">Trạng thái</div>
          <div class="list-column">Đã gửi</div>
          <div class="list-column">Tỷ lệ mở</div>
          <div class="list-column">Cập nhật</div>
          <div class="list-column">Thao tác</div>
        </div>
        
        <div 
          v-for="template in paginatedTemplates" 
          :key="template.id" 
          class="list-item"
        >
          <div class="list-cell template-cell">
            <div class="template-info">
              <div class="template-icon">
                <span class="material-icons">{{ getTemplateIcon(template.type) }}</span>
              </div>
              <div class="template-details">
                <div class="template-name">{{ template.name }}</div>
                <div class="template-subject">{{ template.subject }}</div>
              </div>
            </div>
          </div>
          
          <div class="list-cell">
            <span class="type-badge">{{ getTemplateTypeText(template.type) }}</span>
          </div>
          
          <div class="list-cell">
            <span :class="['status-badge', template.isActive ? 'active' : 'inactive']">
              {{ template.isActive ? 'Hoạt động' : 'Tạm dừng' }}
            </span>
          </div>
          
          <div class="list-cell">
            <span class="sent-count">{{ formatNumber(template.sentCount) }}</span>
          </div>
          
          <div class="list-cell">
            <span class="open-rate">{{ template.openRate }}%</span>
          </div>
          
          <div class="list-cell">
            <span class="update-time">{{ formatDate(template.updatedAt) }}</span>
          </div>
          
          <div class="list-cell">
            <div class="action-buttons">
              <button @click="viewTemplate(template)" class="btn-icon btn-view" title="Xem">
                <span class="material-icons">visibility</span>
              </button>
              <button @click="editTemplate(template)" class="btn-icon btn-edit" title="Chỉnh sửa">
                <span class="material-icons">edit</span>
              </button>
              <button @click="duplicateTemplate(template)" class="btn-icon btn-duplicate" title="Sao chép">
                <span class="material-icons">content_copy</span>
              </button>
              <button @click="deleteTemplate(template)" class="btn-icon btn-delete" title="Xóa">
                <span class="material-icons">delete</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination-container">
        <button 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 0"
          class="page-btn"
        >
          <span class="material-icons">chevron_left</span>
        </button>
        
        <span class="page-info">
          Trang {{ currentPage + 1 }} / {{ totalPages }}
        </span>
        
        <button 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage === totalPages - 1"
          class="page-btn"
        >
          <span class="material-icons">chevron_right</span>
        </button>
      </div>
    </div>

    <!-- Template Editor Modal -->
    <div v-if="showEditorModal" class="modal-overlay" @click="closeEditorModal">
      <div class="modal-content modal-xl" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="material-icons">edit</span>
            {{ isEditing ? 'Chỉnh sửa' : 'Tạo mới' }} Email Template
          </h3>
          <button @click="closeEditorModal" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="template-editor">
            <div class="editor-tabs">
              <button 
                @click="activeTab = 'basic'" 
                :class="['tab-btn', { active: activeTab === 'basic' }]"
              >
                <span class="material-icons">info</span>
                Thông tin cơ bản
              </button>
              <button 
                @click="activeTab = 'content'" 
                :class="['tab-btn', { active: activeTab === 'content' }]"
              >
                <span class="material-icons">article</span>
                Nội dung
              </button>
              <button 
                @click="activeTab = 'preview'" 
                :class="['tab-btn', { active: activeTab === 'preview' }]"
              >
                <span class="material-icons">preview</span>
                Xem trước
              </button>
            </div>

            <div class="editor-content">
              <!-- Basic Info Tab -->
              <div v-if="activeTab === 'basic'" class="tab-content">
                <div class="form-grid">
                  <div class="form-group">
                    <label class="form-label">Tên template</label>
                    <input 
                      type="text" 
                      v-model="templateForm.name" 
                      class="form-input"
                      placeholder="Nhập tên template..."
                    >
                  </div>
                  
                  <div class="form-group">
                    <label class="form-label">Loại template</label>
                    <select v-model="templateForm.type" class="form-input">
                      <option value="welcome">Chào mừng</option>
                      <option value="order">Đơn hàng</option>
                      <option value="promotion">Khuyến mãi</option>
                      <option value="notification">Thông báo</option>
                      <option value="reminder">Nhắc nhở</option>
                    </select>
                  </div>
                  
                  <div class="form-group">
                    <label class="form-label">Chủ đề email</label>
                    <input 
                      type="text" 
                      v-model="templateForm.subject" 
                      class="form-input"
                      placeholder="Nhập chủ đề email..."
                    >
                  </div>
                  
                  <div class="form-group">
                    <label class="form-label">Trạng thái</label>
                    <div class="toggle-switch">
                      <input 
                        type="checkbox" 
                        v-model="templateForm.isActive" 
                        id="template-active"
                      >
                      <label for="template-active" class="toggle-slider"></label>
                      <span class="toggle-label">{{ templateForm.isActive ? 'Hoạt động' : 'Tạm dừng' }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="form-group">
                  <label class="form-label">Mô tả</label>
                  <textarea 
                    v-model="templateForm.description" 
                    class="form-input"
                    rows="3"
                    placeholder="Nhập mô tả template..."
                  ></textarea>
                </div>
              </div>

              <!-- Content Tab -->
              <div v-if="activeTab === 'content'" class="tab-content">
                <div class="editor-toolbar">
                  <button @click="insertVariable('{{name}}')" class="toolbar-btn">
                    <span class="material-icons">person</span>
                    Tên
                  </button>
                  <button @click="insertVariable('{{email}}')" class="toolbar-btn">
                    <span class="material-icons">email</span>
                    Email
                  </button>
                  <button @click="insertVariable('{{orderNumber}}')" class="toolbar-btn">
                    <span class="material-icons">receipt</span>
                    Số đơn hàng
                  </button>
                  <button @click="insertVariable('{{total}}')" class="toolbar-btn">
                    <span class="material-icons">attach_money</span>
                    Tổng tiền
                  </button>
                </div>
                
                <div class="editor-container">
                  <textarea 
                    v-model="templateForm.body" 
                    class="editor-textarea"
                    placeholder="Nhập nội dung email..."
                  ></textarea>
                </div>
              </div>

              <!-- Preview Tab -->
              <div v-if="activeTab === 'preview'" class="tab-content">
                <div class="preview-container">
                  <div class="preview-header">
                    <h4>{{ templateForm.subject || 'Chủ đề email' }}</h4>
                    <div class="preview-meta">
                      <span>Gửi đến: {{ previewData.email }}</span>
                      <span>Ngày: {{ formatDate(new Date()) }}</span>
                    </div>
                  </div>
                  <div class="preview-body" v-html="previewContent"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeEditorModal" class="btn btn-secondary">Hủy</button>
          <button @click="saveTemplate" class="btn btn-primary">
            <span class="material-icons">save</span>
            {{ isEditing ? 'Cập nhật' : 'Tạo mới' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Template Viewer Modal -->
    <div v-if="showViewerModal" class="modal-overlay" @click="closeViewerModal">
      <div class="modal-content modal-lg" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="material-icons">visibility</span>
            {{ selectedTemplate?.name }}
          </h3>
          <button @click="closeViewerModal" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedTemplate" class="template-viewer">
            <div class="template-info">
              <div class="info-grid">
                <div class="info-item">
                  <label>Loại:</label>
                  <span>{{ getTemplateTypeText(selectedTemplate.type) }}</span>
                </div>
                <div class="info-item">
                  <label>Trạng thái:</label>
                  <span :class="['status-badge', selectedTemplate.isActive ? 'active' : 'inactive']">
                    {{ selectedTemplate.isActive ? 'Hoạt động' : 'Tạm dừng' }}
                  </span>
                </div>
                <div class="info-item">
                  <label>Đã gửi:</label>
                  <span>{{ formatNumber(selectedTemplate.sentCount) }}</span>
                </div>
                <div class="info-item">
                  <label>Tỷ lệ mở:</label>
                  <span>{{ selectedTemplate.openRate }}%</span>
                </div>
              </div>
            </div>
            
            <div class="template-preview">
              <h4>{{ selectedTemplate.subject }}</h4>
              <div class="preview-content" v-html="selectedTemplate.body"></div>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeViewerModal" class="btn btn-secondary">Đóng</button>
          <button @click="editTemplate(selectedTemplate)" class="btn btn-primary">Chỉnh sửa</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAdminStore } from '@/stores/admin'

// Store
const adminStore = useAdminStore()

// State
const loading = ref(false)
const templates = ref([])
const searchKeyword = ref('')
const filterType = ref('all')
const filterStatus = ref('all')
const currentPage = ref(0)
const pageSize = ref(12)
const viewMode = ref('grid')
const showEditorModal = ref(false)
const showViewerModal = ref(false)
const selectedTemplate = ref(null)
const isEditing = ref(false)
const activeTab = ref('basic')

const templateForm = ref({
  name: '',
  type: 'welcome',
  subject: '',
  body: '',
  description: '',
  isActive: true
})

const previewData = ref({
  name: 'Nguyễn Văn A',
  email: 'nguyenvana@example.com',
  orderNumber: 'ORD-20240125-0001',
  total: '2,500,000 đ'
})

// Mock data
const mockTemplates = ref([
  {
    id: 1,
    name: 'Chào mừng khách hàng mới',
    type: 'welcome',
    subject: 'Chào mừng bạn đến với Sneakery Store!',
    body: '<h2>Xin chào {{name}}!</h2><p>Cảm ơn bạn đã đăng ký tài khoản tại Sneakery Store.</p><p>Chúng tôi sẽ gửi cho bạn những thông tin khuyến mãi hấp dẫn nhất!</p>',
    description: 'Email chào mừng khách hàng mới đăng ký',
    isActive: true,
    sentCount: 1250,
    openRate: 68.5,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-25T10:30:00Z'
  },
  {
    id: 2,
    name: 'Xác nhận đơn hàng',
    type: 'order',
    subject: 'Xác nhận đơn hàng #{{orderNumber}}',
    body: '<h2>Đơn hàng của bạn đã được xác nhận!</h2><p>Xin chào {{name}},</p><p>Đơn hàng #{{orderNumber}} với tổng giá trị {{total}} đã được xác nhận.</p><p>Chúng tôi sẽ gửi hàng trong vòng 24h.</p>',
    description: 'Email xác nhận đơn hàng',
    isActive: true,
    sentCount: 890,
    openRate: 72.3,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-25T11:15:00Z'
  },
  {
    id: 3,
    name: 'Khuyến mãi cuối tuần',
    type: 'promotion',
    subject: 'Giảm giá 50% cuối tuần - Chỉ còn 2 ngày!',
    body: '<h2>Khuyến mãi cuối tuần!</h2><p>Xin chào {{name}},</p><p>Chỉ còn 2 ngày để hưởng ưu đãi giảm giá 50% cho tất cả sản phẩm!</p><p>Đừng bỏ lỡ cơ hội này!</p>',
    description: 'Email khuyến mãi cuối tuần',
    isActive: true,
    sentCount: 2100,
    openRate: 45.2,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-25T12:00:00Z'
  },
  {
    id: 4,
    name: 'Nhắc nhở giỏ hàng',
    type: 'reminder',
    subject: 'Bạn có sản phẩm chưa thanh toán',
    body: '<h2>Bạn có sản phẩm chưa thanh toán!</h2><p>Xin chào {{name}},</p><p>Bạn có sản phẩm trong giỏ hàng chưa thanh toán.</p><p>Hãy hoàn tất đơn hàng để không bỏ lỡ sản phẩm yêu thích!</p>',
    description: 'Email nhắc nhở giỏ hàng',
    isActive: false,
    sentCount: 450,
    openRate: 38.7,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-25T14:30:00Z'
  }
])

// Computed
const totalTemplates = computed(() => templates.value.length)
const activeTemplates = computed(() => templates.value.filter(t => t.isActive).length)
const newTemplatesThisMonth = computed(() => {
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  
  return templates.value.filter(t => {
    const templateDate = new Date(t.createdAt)
    return templateDate.getMonth() === thisMonth && templateDate.getFullYear() === thisYear
  }).length
})
const emailsSentToday = computed(() => {
  // Mock data - in real app, this would come from API
  return 125
})
const emailsSentThisWeek = computed(() => {
  // Mock data
  return 890
})
const openRate = computed(() => {
  const totalSent = templates.value.reduce((sum, t) => sum + t.sentCount, 0)
  const totalOpened = templates.value.reduce((sum, t) => sum + (t.sentCount * t.openRate / 100), 0)
  return totalSent > 0 ? Math.round((totalOpened / totalSent) * 100 * 10) / 10 : 0
})

const filteredTemplates = computed(() => {
  let filtered = templates.value || []

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(t => 
      t.name.toLowerCase().includes(keyword) ||
      t.subject.toLowerCase().includes(keyword) ||
      t.description.toLowerCase().includes(keyword)
    )
  }

  if (filterType.value !== 'all') {
    filtered = filtered.filter(t => t.type === filterType.value)
  }

  if (filterStatus.value !== 'all') {
    filtered = filtered.filter(t => {
      switch (filterStatus.value) {
        case 'active':
          return t.isActive
        case 'inactive':
          return !t.isActive
        case 'draft':
          return t.sentCount === 0
        default:
          return true
      }
    })
  }

  return filtered.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
})

const totalPages = computed(() => Math.ceil(filteredTemplates.value.length / pageSize.value))
const paginatedTemplates = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return filteredTemplates.value.slice(start, end)
})

const previewContent = computed(() => {
  let content = templateForm.value.body || ''
  
  // Replace variables with preview data
  content = content.replace(/\{\{name\}\}/g, previewData.value.name)
  content = content.replace(/\{\{email\}\}/g, previewData.value.email)
  content = content.replace(/\{\{orderNumber\}\}/g, previewData.value.orderNumber)
  content = content.replace(/\{\{total\}\}/g, previewData.value.total)
  
  return content
})

// Methods
const fetchTemplates = async () => {
  loading.value = true
  try {
    const result = await adminStore.fetchEmailTemplates(currentPage.value, pageSize.value, {})
    templates.value = result.content || []
  } catch (error) {
    ElMessage.error('Không thể tải danh sách templates')
  } finally {
    loading.value = false
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = 'all'
  filterStatus.value = 'all'
  currentPage.value = 0
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

const openCreateModal = () => {
  isEditing.value = false
  templateForm.value = {
    name: '',
    type: 'welcome',
    subject: '',
    body: '',
    description: '',
    isActive: true
  }
  activeTab.value = 'basic'
  showEditorModal.value = true
}

const editTemplate = (template) => {
  isEditing.value = true
  templateForm.value = { ...template }
  activeTab.value = 'basic'
  showEditorModal.value = true
}

const viewTemplate = (template) => {
  selectedTemplate.value = template
  showViewerModal.value = true
}

const duplicateTemplate = async (template) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn sao chép template "${template.name}"?`,
      'Xác nhận sao chép',
      {
        confirmButtonText: 'Sao chép',
        cancelButtonText: 'Hủy',
        type: 'info'
      }
    )
    
    const newTemplate = {
      ...template,
      id: Date.now(),
      name: `${template.name} (Bản sao)`,
      sentCount: 0,
      openRate: 0,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    templates.value.unshift(newTemplate)
    ElMessage.success('Đã sao chép template thành công')
  } catch {
    // User cancelled
  }
}

const deleteTemplate = async (template) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn xóa template "${template.name}"? Hành động này không thể hoàn tác.`,
      'Xác nhận xóa',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    templates.value = templates.value.filter(t => t.id !== template.id)
    ElMessage.success('Đã xóa template thành công')
  } catch {
    // User cancelled
  }
}

const closeEditorModal = () => {
  showEditorModal.value = false
  selectedTemplate.value = null
}

const closeViewerModal = () => {
  showViewerModal.value = false
  selectedTemplate.value = null
}

const insertVariable = (variable) => {
  const textarea = document.querySelector('.editor-textarea')
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = templateForm.value.body
  const before = text.substring(0, start)
  const after = text.substring(end, text.length)
  
  templateForm.value.body = before + variable + after
  
  // Set cursor position after inserted variable
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + variable.length, start + variable.length)
  }, 0)
}

const saveTemplate = async () => {
  if (!templateForm.value.name.trim()) {
    ElMessage.error('Vui lòng nhập tên template')
    return
  }
  
  if (!templateForm.value.subject.trim()) {
    ElMessage.error('Vui lòng nhập chủ đề email')
    return
  }
  
  if (!templateForm.value.body.trim()) {
    ElMessage.error('Vui lòng nhập nội dung email')
    return
  }

  try {
    if (isEditing.value) {
      // Update existing template
      const index = templates.value.findIndex(t => t.id === templateForm.value.id)
      if (index !== -1) {
        templates.value[index] = {
          ...templateForm.value,
          updatedAt: new Date().toISOString()
        }
      }
      ElMessage.success('Đã cập nhật template thành công')
    } else {
      // Create new template
      const newTemplate = {
        ...templateForm.value,
        id: Date.now(),
        sentCount: 0,
        openRate: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      templates.value.unshift(newTemplate)
      ElMessage.success('Đã tạo template thành công')
    }
    
    closeEditorModal()
  } catch (error) {
    ElMessage.error('Có lỗi xảy ra khi lưu template')
  }
}

const exportTemplates = (format) => {
  try {
    const dataToExport = filteredTemplates.value || []
    if (dataToExport.length === 0) {
      ElMessage.warning('Không có dữ liệu để xuất')
      return
    }
    
    const exportData = dataToExport.map(template => ({
      'ID': template.id,
      'Tên template': template.name,
      'Loại': getTemplateTypeText(template.type),
      'Chủ đề': template.subject,
      'Mô tả': template.description,
      'Trạng thái': template.isActive ? 'Hoạt động' : 'Tạm dừng',
      'Đã gửi': formatNumber(template.sentCount),
      'Tỷ lệ mở': `${template.openRate}%`,
      'Ngày tạo': formatDate(template.createdAt),
      'Ngày cập nhật': formatDate(template.updatedAt)
    }))

    if (format === 'csv') {
      downloadCsv(exportData, 'email-templates.csv')
      ElMessage.success('Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('email-templates', exportData)
      ElMessage.success('Xuất JSON thành công!')
    }
  } catch (error) {
    console.error('Export error:', error)
    ElMessage.error('Có lỗi xảy ra khi xuất dữ liệu!')
  }
}

// Helper functions
const getTemplateIcon = (type) => {
  const icons = {
    welcome: 'waving_hand',
    order: 'receipt',
    promotion: 'local_offer',
    notification: 'notifications',
    reminder: 'schedule'
  }
  return icons[type] || 'email'
}

const getTemplateTypeText = (type) => {
  const texts = {
    welcome: 'Chào mừng',
    order: 'Đơn hàng',
    promotion: 'Khuyến mãi',
    notification: 'Thông báo',
    reminder: 'Nhắc nhở'
  }
  return texts[type] || type
}

const formatNumber = (num) => {
  return new Intl.NumberFormat('vi-VN').format(num)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('vi-VN')
}

// Lifecycle
onMounted(() => {
  fetchTemplates()
})
</script>

<style scoped>
/* Page Header */
.page-header {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.title-section {
  flex: 1;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

/* Enhanced Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.stat-card {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
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
  height: 2px;
  background: linear-gradient(90deg, var(--accent-primary), var(--accent-secondary));
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.2);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--space-4);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon .material-icons {
  font-size: 24px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  font-weight: var(--font-medium);
}

.stat-value {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.stat-change {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.stat-change.positive {
  color: var(--success-text);
}

.stat-change.negative {
  color: var(--error-text);
}

.stat-change.neutral {
  color: var(--text-secondary);
}

.stat-change .material-icons {
  font-size: 16px;
}

/* Filters Section */
.filters-section {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: var(--space-4);
  align-items: end;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: var(--space-3);
  color: var(--text-tertiary);
  font-size: 20px;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-3) var(--space-3) var(--space-10);
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: rgba(15, 23, 42, 0.4);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.6);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-clear {
  position: absolute;
  right: var(--space-3);
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.search-clear:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Templates Container */
.templates-container {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.templates-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.templates-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.templates-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.templates-info {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.view-toggle {
  display: flex;
  gap: var(--space-1);
}

.view-btn {
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-primary);
  background: transparent;
  color: var(--text-tertiary);
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.view-btn:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.view-btn.active {
  background: var(--accent-primary);
  color: white;
  border-color: var(--accent-primary);
}

/* Templates Grid */
.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--space-4);
  padding: var(--space-6);
}

.template-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.template-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border-color: var(--accent-primary);
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.template-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--accent-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.template-icon .material-icons {
  color: white;
  font-size: 20px;
}

.template-content {
  margin-bottom: var(--space-4);
}

.template-name {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.template-subject {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-2) 0;
  line-height: 1.4;
}

.template-type {
  margin-bottom: var(--space-3);
}

.type-badge {
  display: inline-flex;
  align-items: center;
  padding: var(--space-1) var(--space-2);
  background: var(--info-bg);
  color: var(--info-text);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.template-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.template-stats {
  display: flex;
  gap: var(--space-3);
}

.stat {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.stat .material-icons {
  font-size: 16px;
}

.template-actions {
  display: flex;
  gap: var(--space-1);
}

.btn-icon {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  border-radius: var(--radius-sm);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.btn-icon:hover {
  background: var(--bg-primary);
  color: var(--text-primary);
}

.btn-edit:hover {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.btn-duplicate:hover {
  background: var(--info-bg);
  color: var(--info-text);
}

.btn-delete:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Templates List */
.templates-list {
  padding: var(--space-6);
}

.list-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr 1fr;
  gap: var(--space-4);
  padding: var(--space-3) var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-3);
}

.list-column {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.list-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr 1fr;
  gap: var(--space-4);
  padding: var(--space-4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-2);
  transition: all var(--transition-fast);
}

.list-item:hover {
  background: var(--bg-secondary);
  border-color: var(--accent-primary);
}

.list-cell {
  display: flex;
  align-items: center;
}

.template-cell {
  justify-content: flex-start;
}

.template-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.template-details {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.action-buttons {
  display: flex;
  gap: var(--space-1);
}

/* Status Badge */
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-badge.active {
  background: var(--success-bg);
  color: var(--success-text);
}

.status-badge.inactive {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Modal */
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
}

.modal-content {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-content.modal-lg {
  max-width: 800px;
}

.modal-content.modal-xl {
  max-width: 1200px;
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

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

.modal-body {
  padding: var(--space-6);
}

/* Template Editor */
.template-editor {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.editor-tabs {
  display: flex;
  gap: var(--space-2);
  border-bottom: 1px solid var(--border-primary);
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border: none;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all var(--transition-fast);
}

.tab-btn:hover {
  color: var(--text-primary);
}

.tab-btn.active {
  color: var(--accent-primary);
  border-bottom-color: var(--accent-primary);
}

.editor-content {
  min-height: 400px;
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.form-label {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.form-input {
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--bg-secondary);
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.form-input:focus {
  outline: none;
  border-color: var(--accent-primary);
}

.toggle-switch {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.toggle-slider {
  width: 40px;
  height: 20px;
  background: var(--bg-secondary);
  border-radius: 20px;
  position: relative;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.toggle-slider::after {
  content: '';
  position: absolute;
  width: 16px;
  height: 16px;
  background: white;
  border-radius: 50%;
  top: 2px;
  left: 2px;
  transition: all var(--transition-fast);
}

.toggle-switch input:checked + .toggle-slider {
  background: var(--accent-primary);
}

.toggle-switch input:checked + .toggle-slider::after {
  transform: translateX(20px);
}

.toggle-label {
  font-size: var(--text-sm);
  color: var(--text-primary);
}

/* Editor Toolbar */
.editor-toolbar {
  display: flex;
  gap: var(--space-2);
  padding: var(--space-3);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-3);
}

.toolbar-btn {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-primary);
  background: transparent;
  color: var(--text-secondary);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.toolbar-btn:hover {
  background: var(--bg-primary);
  color: var(--text-primary);
  border-color: var(--accent-primary);
}

.editor-container {
  flex: 1;
}

.editor-textarea {
  width: 100%;
  height: 300px;
  padding: var(--space-4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  background: var(--bg-secondary);
  color: var(--text-primary);
  font-family: monospace;
  font-size: var(--text-sm);
  resize: vertical;
}

.editor-textarea:focus {
  outline: none;
  border-color: var(--accent-primary);
}

/* Preview */
.preview-container {
  background: white;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.preview-header {
  background: #f8f9fa;
  padding: var(--space-4);
  border-bottom: 1px solid #e9ecef;
}

.preview-header h4 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: #333;
  margin: 0 0 var(--space-2) 0;
}

.preview-meta {
  display: flex;
  gap: var(--space-4);
  font-size: var(--text-sm);
  color: #666;
}

.preview-body {
  padding: var(--space-4);
  color: #333;
  line-height: 1.6;
}

/* Template Viewer */
.template-viewer {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.template-info {
  background: var(--bg-secondary);
  padding: var(--space-4);
  border-radius: var(--radius-md);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-3);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.info-item label {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
}

.info-item span {
  color: var(--text-primary);
}

.template-preview {
  background: white;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.template-preview h4 {
  background: #f8f9fa;
  padding: var(--space-4);
  margin: 0;
  border-bottom: 1px solid #e9ecef;
  font-size: var(--text-lg);
  color: #333;
}

.preview-content {
  padding: var(--space-4);
  color: #333;
  line-height: 1.6;
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.page-btn {
  width: 40px;
  height: 40px;
  border: 1px solid var(--border-primary);
  background: transparent;
  color: var(--text-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.page-btn:hover:not(:disabled) {
  background: var(--bg-secondary);
  border-color: var(--accent-primary);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

/* Loading & Empty States */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12);
  color: var(--text-secondary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-primary);
  border-top: 3px solid var(--accent-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--space-4);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12);
  text-align: center;
}

.empty-state .material-icons {
  font-size: 64px;
  color: var(--text-tertiary);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.empty-state p {
  color: var(--text-secondary);
}

/* Responsive */
@media (max-width: 768px) {
  .filter-row {
    grid-template-columns: 1fr;
    gap: var(--space-3);
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .templates-grid {
    grid-template-columns: 1fr;
  }
  
  .list-header,
  .list-item {
    grid-template-columns: 1fr;
    gap: var(--space-2);
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .editor-tabs {
    flex-wrap: wrap;
  }
  
  .tab-btn {
    flex: 1;
    min-width: 120px;
  }
}
</style>

<template>
  <div class="admin-page admin-activity-logs">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">history</span>
            Nhật ký hoạt động
          </h1>
          <p class="page-subtitle">Theo dõi tất cả hoạt động trong hệ thống</p>
        </div>
        <div class="header-actions">
          <button @click="exportLogs('csv')" class="btn btn-secondary">
            <span class="material-icons">file_download</span>
            CSV
          </button>
          <button @click="exportLogs('json')" class="btn btn-secondary">
            <span class="material-icons">code</span>
            JSON
          </button>
          <button @click="clearOldLogs" class="btn btn-danger">
            <span class="material-icons">delete_sweep</span>
            Dọn dẹp
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="stats-grid animate-fade-up">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <span class="material-icons">timeline</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Tổng hoạt động</div>
          <div class="stat-value">{{ formatNumber(totalLogs) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            +{{ formatNumber(todayLogs) }} hôm nay
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <span class="material-icons">person</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Người dùng hoạt động</div>
          <div class="stat-value">{{ formatNumber(activeUsers) }}</div>
          <div class="stat-change neutral">
            <span class="material-icons">info</span>
            {{ Math.round((activeUsers / totalUsers) * 100) || 0 }}% tổng số
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <span class="material-icons">warning</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Hoạt động bất thường</div>
          <div class="stat-value">{{ formatNumber(suspiciousLogs) }}</div>
          <div class="stat-change negative">
            <span class="material-icons">security</span>
            Cần kiểm tra
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <span class="material-icons">admin_panel_settings</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Hoạt động Admin</div>
          <div class="stat-value">{{ formatNumber(adminLogs) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">verified</span>
            {{ Math.round((adminLogs / totalLogs) * 100) || 0 }}% tổng số
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
              placeholder="Tìm theo người dùng, hành động, IP..."
            />
            <button v-if="searchKeyword" @click="clearSearch" class="search-clear">
              <span class="material-icons">close</span>
            </button>
          </div>
        </div>
        
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">person</span>
            Người dùng
          </label>
          <select class="filter-select" v-model="filterUser">
            <option value="all">Tất cả</option>
            <option value="admin">Admin</option>
            <option value="user">User</option>
            <option value="guest">Khách</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">category</span>
            Loại hoạt động
          </label>
          <select class="filter-select" v-model="filterAction">
            <option value="all">Tất cả</option>
            <option value="login">Đăng nhập</option>
            <option value="logout">Đăng xuất</option>
            <option value="create">Tạo mới</option>
            <option value="update">Cập nhật</option>
            <option value="delete">Xóa</option>
            <option value="view">Xem</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">date_range</span>
            Thời gian
          </label>
          <select class="filter-select" v-model="filterTimeRange">
            <option value="all">Tất cả</option>
            <option value="today">Hôm nay</option>
            <option value="week">Tuần này</option>
            <option value="month">Tháng này</option>
            <option value="year">Năm nay</option>
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

    <!-- Activity Logs Table -->
    <div class="table-container animate-fade-up">
      <div class="table-header">
        <h3 class="table-title">Nhật ký hoạt động</h3>
        <div class="table-actions">
          <span class="table-info">{{ filteredLogs.length }} hoạt động</span>
          <button @click="refreshLogs" class="btn btn-sm btn-outline">
            <span class="material-icons">refresh</span>
            Làm mới
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="filteredLogs.length === 0" class="empty-state">
        <span class="material-icons">history</span>
        <h3>Không có hoạt động nào</h3>
        <p>Chưa có hoạt động nào được ghi nhận</p>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Thời gian</th>
              <th>Người dùng</th>
              <th>Hành động</th>
              <th>Đối tượng</th>
              <th>Chi tiết</th>
              <th>IP Address</th>
              <th>User Agent</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="log in paginatedLogs" :key="log.id" class="log-row">
              <td>
                <div class="time-info">
                  <div class="time">{{ formatDateTime(log.createdAt) }}</div>
                  <div class="date">{{ formatDate(log.createdAt) }}</div>
                </div>
              </td>
              <td>
                <div class="user-info">
                  <div class="user-avatar">
                    <img v-if="log.userAvatar" :src="log.userAvatar" :alt="log.userName">
                    <span v-else class="material-icons">person</span>
                  </div>
                  <div class="user-details">
                    <div class="user-name">{{ log.userName || 'Khách' }}</div>
                    <div class="user-role">{{ log.userRole || 'Guest' }}</div>
                  </div>
                </div>
              </td>
              <td>
                <div class="action-info">
                  <span :class="['action-badge', getActionClass(log.action)]">
                    <span class="material-icons">{{ getActionIcon(log.action) }}</span>
                    {{ getActionText(log.action) }}
                  </span>
                </div>
              </td>
              <td>
                <div class="entity-info">
                  <div class="entity-type">{{ log.entityType }}</div>
                  <div v-if="log.entityId" class="entity-id">ID: {{ log.entityId }}</div>
                </div>
              </td>
              <td>
                <div class="details-info">
                  <div v-if="log.oldValue" class="old-value">
                    <strong>Cũ:</strong> {{ truncateText(log.oldValue, 50) }}
                  </div>
                  <div v-if="log.newValue" class="new-value">
                    <strong>Mới:</strong> {{ truncateText(log.newValue, 50) }}
                  </div>
                </div>
              </td>
              <td>
                <div class="ip-info">
                  <div class="ip-address">{{ log.ipAddress }}</div>
                  <div class="ip-location">{{ getLocationFromIP(log.ipAddress) }}</div>
                </div>
              </td>
              <td>
                <div class="user-agent-info">
                  <div class="browser">{{ getBrowserFromUserAgent(log.userAgent) }}</div>
                  <div class="os">{{ getOSFromUserAgent(log.userAgent) }}</div>
                </div>
              </td>
              <td>
                <span :class="['status-badge', getStatusClass(log.status)]">
                  {{ getStatusText(log.status) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
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

    <!-- Log Detail Modal -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content modal-lg" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="material-icons">info</span>
            Chi tiết hoạt động
          </h3>
          <button @click="closeDetailModal" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedLog" class="log-detail">
            <div class="detail-section">
              <h4>Thông tin cơ bản</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>Thời gian:</label>
                  <span>{{ formatDateTime(selectedLog.createdAt) }}</span>
                </div>
                <div class="detail-item">
                  <label>Người dùng:</label>
                  <span>{{ selectedLog.userName || 'Khách' }}</span>
                </div>
                <div class="detail-item">
                  <label>Vai trò:</label>
                  <span>{{ selectedLog.userRole || 'Guest' }}</span>
                </div>
                <div class="detail-item">
                  <label>Hành động:</label>
                  <span>{{ getActionText(selectedLog.action) }}</span>
                </div>
                <div class="detail-item">
                  <label>Đối tượng:</label>
                  <span>{{ selectedLog.entityType }}</span>
                </div>
                <div class="detail-item">
                  <label>ID đối tượng:</label>
                  <span>{{ selectedLog.entityId || 'N/A' }}</span>
                </div>
              </div>
            </div>

            <div v-if="selectedLog.oldValue || selectedLog.newValue" class="detail-section">
              <h4>Thay đổi dữ liệu</h4>
              <div class="change-comparison">
                <div v-if="selectedLog.oldValue" class="old-value">
                  <h5>Giá trị cũ:</h5>
                  <pre>{{ selectedLog.oldValue }}</pre>
                </div>
                <div v-if="selectedLog.newValue" class="new-value">
                  <h5>Giá trị mới:</h5>
                  <pre>{{ selectedLog.newValue }}</pre>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h4>Thông tin kỹ thuật</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>IP Address:</label>
                  <span>{{ selectedLog.ipAddress }}</span>
                </div>
                <div class="detail-item">
                  <label>Vị trí:</label>
                  <span>{{ getLocationFromIP(selectedLog.ipAddress) }}</span>
                </div>
                <div class="detail-item">
                  <label>Trình duyệt:</label>
                  <span>{{ getBrowserFromUserAgent(selectedLog.userAgent) }}</span>
                </div>
                <div class="detail-item">
                  <label>Hệ điều hành:</label>
                  <span>{{ getOSFromUserAgent(selectedLog.userAgent) }}</span>
                </div>
                <div class="detail-item">
                  <label>User Agent:</label>
                  <span class="user-agent-full">{{ selectedLog.userAgent }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeDetailModal" class="btn btn-secondary">Đóng</button>
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

// Stores
const adminStore = useAdminStore()

// State
const loading = ref(false)
const logs = ref([])
const searchKeyword = ref('')
const filterUser = ref('all')
const filterAction = ref('all')
const filterTimeRange = ref('all')
const currentPage = ref(0)
const pageSize = ref(20)
const showDetailModal = ref(false)
const selectedLog = ref(null)

// Mock data removed - using real API data

// Computed
const totalLogs = computed(() => logs.value.length)
const todayLogs = computed(() => {
  const today = new Date().toDateString()
  return logs.value.filter(log => new Date(log.createdAt).toDateString() === today).length
})
const activeUsers = computed(() => {
  const uniqueUsers = new Set(logs.value.map(log => log.userId).filter(Boolean))
  return uniqueUsers.size
})
const totalUsers = computed(() => 100) // Mock total users
const suspiciousLogs = computed(() => {
  return logs.value.filter(log => 
    log.action === 'delete' || 
    log.ipAddress.includes('192.168.1.999') || // Suspicious IP
    log.status === 'failed'
  ).length
})
const adminLogs = computed(() => {
  return (logs.value || []).filter(log => log.userRole === 'ADMIN').length
})

const filteredLogs = computed(() => {
  let filtered = logs.value || []

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(log => 
      (log.userName && log.userName.toLowerCase().includes(keyword)) ||
      log.action.toLowerCase().includes(keyword) ||
      log.entityType.toLowerCase().includes(keyword) ||
      log.ipAddress.includes(keyword)
    )
  }

  if (filterUser.value !== 'all') {
    filtered = filtered.filter(log => {
      switch (filterUser.value) {
        case 'admin':
          return log.userRole === 'ADMIN'
        case 'user':
          return log.userRole === 'USER'
        case 'guest':
          return !log.userRole
        default:
          return true
      }
    })
  }

  if (filterAction.value !== 'all') {
    filtered = filtered.filter(log => log.action === filterAction.value)
  }

  if (filterTimeRange.value !== 'all') {
    const now = new Date()
    filtered = filtered.filter(log => {
      const logDate = new Date(log.createdAt)
      switch (filterTimeRange.value) {
        case 'today':
          return logDate.toDateString() === now.toDateString()
        case 'week':
          const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
          return logDate >= weekAgo
        case 'month':
          const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
          return logDate >= monthAgo
        case 'year':
          const yearAgo = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000)
          return logDate >= yearAgo
        default:
          return true
      }
    })
  }

  return filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
})

const totalPages = computed(() => Math.ceil(filteredLogs.value.length / pageSize.value))
const paginatedLogs = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return filteredLogs.value.slice(start, end)
})

// Methods
const fetchLogs = async () => {
  loading.value = true
  try {
    console.log('🔍 Fetching activity logs...')
    const result = await adminStore.fetchActivityLogs(currentPage.value, pageSize.value, {})
    console.log('📦 API Result:', result)
    
    const activityLogDtos = result.content || []
    console.log('📊 Activity logs received:', activityLogDtos.length, activityLogDtos)
    
    // Map ActivityLogDto to frontend format
    logs.value = activityLogDtos.map(dto => ({
      id: dto.id,
      userId: dto.userId,
      userName: dto.userName,
      userRole: 'USER', // Default role - DTO doesn't provide this
      userAvatar: null,
      action: dto.action,
      entityType: dto.entityType,
      entityId: dto.entityId,
      oldValue: dto.oldValue,
      newValue: dto.newValue,
      ipAddress: dto.ipAddress,
      userAgent: dto.userAgent,
      status: 'success', // Default status
      createdAt: dto.createdAt
    }))
    
    console.log('✅ Logs mapped:', logs.value.length, 'items')
    console.log('📊 Logs sample:', logs.value.slice(0, 3))
  } catch (error) {
    console.error('❌ Error fetching logs:', error)
    ElMessage.error('Không thể tải nhật ký hoạt động: ' + (error.message || 'Unknown error'))
  } finally {
    loading.value = false
  }
}

const refreshLogs = () => {
  fetchLogs()
}

const clearSearch = () => {
  searchKeyword.value = ''
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterUser.value = 'all'
  filterAction.value = 'all'
  filterTimeRange.value = 'all'
  currentPage.value = 0
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

const viewLogDetail = (log) => {
  selectedLog.value = log
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedLog.value = null
}

const clearOldLogs = async () => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn xóa các nhật ký cũ hơn 30 ngày? Hành động này không thể hoàn tác.',
      'Xác nhận dọn dẹp',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    // Simulate API call
    const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000)
    logs.value = logs.value.filter(log => new Date(log.createdAt) > thirtyDaysAgo)
    
    ElMessage.success('Đã dọn dẹp nhật ký cũ thành công')
  } catch {
    // User cancelled
  }
}

const exportLogs = (format) => {
  try {
    const dataToExport = filteredLogs.value || []
    if (dataToExport.length === 0) {
      ElMessage.warning('Không có dữ liệu để xuất')
      return
    }
    
    const exportData = dataToExport.map(log => ({
      'ID': log.id,
      'Thời gian': formatDateTime(log.createdAt),
      'Người dùng': log.userName || 'Khách',
      'Vai trò': log.userRole || 'Guest',
      'Hành động': getActionText(log.action),
      'Đối tượng': log.entityType,
      'ID đối tượng': log.entityId || 'N/A',
      'Giá trị cũ': log.oldValue || 'N/A',
      'Giá trị mới': log.newValue || 'N/A',
      'IP Address': log.ipAddress,
      'Trình duyệt': getBrowserFromUserAgent(log.userAgent),
      'Hệ điều hành': getOSFromUserAgent(log.userAgent),
      'Trạng thái': getStatusText(log.status)
    }))

    if (format === 'csv') {
      downloadCsv(exportData, 'activity-logs.csv')
      ElMessage.success('Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('activity-logs', exportData)
      ElMessage.success('Xuất JSON thành công!')
    }
  } catch (error) {
    console.error('Export error:', error)
    ElMessage.error('Có lỗi xảy ra khi xuất dữ liệu!')
  }
}

// Helper functions
const getActionClass = (action) => {
  const classes = {
    login: 'success',
    logout: 'info',
    create: 'success',
    update: 'warning',
    delete: 'danger',
    view: 'info'
  }
  return classes[action] || 'neutral'
}

const getActionIcon = (action) => {
  const icons = {
    login: 'login',
    logout: 'logout',
    create: 'add',
    update: 'edit',
    delete: 'delete',
    view: 'visibility'
  }
  return icons[action] || 'help'
}

const getActionText = (action) => {
  const texts = {
    login: 'Đăng nhập',
    logout: 'Đăng xuất',
    create: 'Tạo mới',
    update: 'Cập nhật',
    delete: 'Xóa',
    view: 'Xem'
  }
  return texts[action] || action
}

const getStatusClass = (status) => {
  return status === 'success' ? 'success' : 'danger'
}

const getStatusText = (status) => {
  return status === 'success' ? 'Thành công' : 'Thất bại'
}

const getLocationFromIP = (ip) => {
  // Mock location data
  const locations = {
    '192.168.1.100': 'Hà Nội, Việt Nam',
    '192.168.1.101': 'TP.HCM, Việt Nam',
    '192.168.1.102': 'Đà Nẵng, Việt Nam',
    '192.168.1.103': 'Hải Phòng, Việt Nam'
  }
  return locations[ip] || 'Không xác định'
}

const getBrowserFromUserAgent = (userAgent) => {
  if (userAgent.includes('Chrome')) return 'Chrome'
  if (userAgent.includes('Firefox')) return 'Firefox'
  if (userAgent.includes('Safari')) return 'Safari'
  if (userAgent.includes('Edge')) return 'Edge'
  return 'Không xác định'
}

const getOSFromUserAgent = (userAgent) => {
  if (userAgent.includes('Windows')) return 'Windows'
  if (userAgent.includes('Mac OS')) return 'macOS'
  if (userAgent.includes('Linux')) return 'Linux'
  if (userAgent.includes('iPhone')) return 'iOS'
  if (userAgent.includes('Android')) return 'Android'
  return 'Không xác định'
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

const formatNumber = (num) => {
  return new Intl.NumberFormat('vi-VN').format(num)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('vi-VN')
}

const formatDateTime = (dateString) => {
  return new Date(dateString).toLocaleString('vi-VN')
}

// Lifecycle
onMounted(() => {
  fetchLogs()
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
  grid-template-columns: 2fr 1fr 1fr 1fr auto;
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

/* Table */
.table-container {
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.table-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.table-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.table-info {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th {
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
  padding: var(--space-4);
  text-align: left;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-table td {
  padding: var(--space-4);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  color: var(--text-primary);
}

.admin-table tbody tr:hover {
  background: rgba(255, 255, 255, 0.05);
}

/* Time Info */
.time-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.time {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.date {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* User Info */
.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar .material-icons {
  color: var(--text-tertiary);
}

.user-name {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.user-role {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* Action Info */
.action-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.action-badge.success {
  background: var(--success-bg);
  color: var(--success-text);
}

.action-badge.warning {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.action-badge.danger {
  background: var(--error-bg);
  color: var(--error-text);
}

.action-badge.info {
  background: var(--info-bg);
  color: var(--info-text);
}

.action-badge.neutral {
  background: var(--bg-secondary);
  color: var(--text-secondary);
}

.action-badge .material-icons {
  font-size: 16px;
}

/* Entity Info */
.entity-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.entity-type {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.entity-id {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* Details Info */
.details-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  max-width: 200px;
}

.old-value,
.new-value {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.old-value strong,
.new-value strong {
  color: var(--text-primary);
}

/* IP Info */
.ip-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.ip-address {
  font-family: monospace;
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.ip-location {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* User Agent Info */
.user-agent-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.browser,
.os {
  font-size: var(--text-sm);
  color: var(--text-secondary);
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

.status-badge.success {
  background: var(--success-bg);
  color: var(--success-text);
}

.status-badge.danger {
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

.log-detail {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.detail-section {
  background: var(--bg-secondary);
  padding: var(--space-4);
  border-radius: var(--radius-md);
}

.detail-section h4 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4) 0;
  border-bottom: 1px solid var(--border-primary);
  padding-bottom: var(--space-2);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-3);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.detail-item label {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
}

.detail-item span {
  color: var(--text-primary);
}

.user-agent-full {
  font-family: monospace;
  font-size: var(--text-xs);
  word-break: break-all;
}

.change-comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.old-value,
.new-value {
  background: var(--bg-primary);
  padding: var(--space-3);
  border-radius: var(--radius-md);
  border-left: 4px solid var(--border-primary);
}

.old-value {
  border-left-color: var(--error-text);
}

.new-value {
  border-left-color: var(--success-text);
}

.old-value h5,
.new-value h5 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.old-value pre,
.new-value pre {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
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
  
  .admin-table {
    font-size: var(--text-sm);
  }
  
  .admin-table th,
  .admin-table td {
    padding: var(--space-2);
  }
  
  .change-comparison {
    grid-template-columns: 1fr;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>

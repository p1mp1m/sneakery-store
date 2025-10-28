<template>
  <div class="admin-page admin-notifications">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">
            <i class="material-icons">notifications</i>
            Quản lý Thông Báo
          </h1>
          <p class="page-subtitle">
            <i class="material-icons">info</i>
            Gửi và quản lý thông báo đến khách hàng
          </p>
        </div>
        <div class="header-actions">
          <button @click="showCreateDialog = true" class="btn btn-primary">
            <i class="material-icons">add</i>
            Tạo thông báo
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stats-card success">
        <div class="stats-header">
          <div class="stats-icon success">
            <i class="material-icons">mark_email_read</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.sent }}</div>
            <div class="stats-label">Đã gửi</div>
          </div>
        </div>
      </div>
      <div class="stats-card info">
        <div class="stats-header">
          <div class="stats-icon info">
            <i class="material-icons">visibility</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.opened }}</div>
            <div class="stats-label">Đã đọc</div>
          </div>
        </div>
      </div>
      <div class="stats-card warning">
        <div class="stats-header">
          <div class="stats-icon warning">
            <i class="material-icons">schedule_send</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.scheduled }}</div>
            <div class="stats-label">Đã lên lịch</div>
          </div>
        </div>
      </div>
      <div class="stats-card danger">
        <div class="stats-header">
          <div class="stats-icon danger">
            <i class="material-icons">error</i>
          </div>
          <div class="stats-info">
            <div class="stats-value">{{ stats.failed }}</div>
            <div class="stats-label">Thất bại</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="filters-section">
      <div class="filter-row">
        <div class="filter-group">
          <label>Tìm kiếm</label>
          <div class="search-box">
            <i class="material-icons search-icon">search</i>
            <input 
              v-model="filters.search"
              type="text" 
              class="search-input" 
              placeholder="Tìm theo tiêu đề, nội dung..."
              @input="handleSearch"
            />
          </div>
        </div>
        <div class="filter-group">
          <label>Loại</label>
          <select v-model="filters.type" @change="fetchNotifications" class="form-control">
            <option value="">Tất cả</option>
            <option value="promotion">Khuyến mãi</option>
            <option value="order">Đơn hàng</option>
            <option value="system">Hệ thống</option>
            <option value="announcement">Thông báo chung</option>
          </select>
        </div>
        <div class="filter-group">
          <label>Trạng thái</label>
          <select v-model="filters.status" @change="fetchNotifications" class="form-control">
            <option value="">Tất cả</option>
            <option value="draft">Bản nháp</option>
            <option value="scheduled">Đã lên lịch</option>
            <option value="sent">Đã gửi</option>
            <option value="failed">Thất bại</option>
          </select>
        </div>
        <div class="filter-group">
          <label>&nbsp;</label>
          <button @click="resetFilters" class="btn btn-secondary">
            <i class="material-icons">refresh</i>
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

      <div v-else-if="notifications.length === 0" class="empty-state">
        <i class="material-icons">notifications</i>
        <h3>Chưa có thông báo</h3>
        <p>Nhấn nút "Tạo thông báo" để bắt đầu</p>
      </div>

      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>Tiêu đề</th>
            <th>Loại</th>
            <th>Đối tượng</th>
            <th>Thời gian gửi</th>
            <th>Tỷ lệ đọc</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in notifications" :key="item.id">
            <td>
              <div class="notification-title">
                <i class="material-icons" :style="{ color: getTypeColor(item.type) }">
                  {{ getTypeIcon(item.type) }}
                </i>
                <div>
                  <strong>{{ item.title }}</strong>
                  <p>{{ truncate(item.message, 80) }}</p>
                </div>
              </div>
            </td>
            <td>
              <span class="badge" :class="getTypeBadgeClass(item.type)">
                {{ getTypeText(item.type) }}
              </span>
            </td>
            <td>
              <div class="recipient-info">
                <i class="material-icons">{{ item.recipientType === 'all' ? 'public' : 'person' }}</i>
                <span>{{ getRecipientText(item) }}</span>
              </div>
            </td>
            <td>
              <div class="time-info">
                {{ formatDateTime(item.scheduledAt || item.sentAt) }}
              </div>
            </td>
            <td>
              <div v-if="item.status === 'sent'" class="read-rate">
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: getReadRate(item) + '%' }"
                  ></div>
                </div>
                <span>{{ item.openedCount }} / {{ item.recipientCount }} ({{ getReadRate(item) }}%)</span>
              </div>
              <span v-else>-</span>
            </td>
            <td>
              <span class="status-badge" :class="getStatusClass(item.status)">
                {{ getStatusText(item.status) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="viewNotification(item)" class="btn-icon" title="Xem chi tiết">
                  <i class="material-icons">visibility</i>
                </button>
                <button 
                  v-if="item.status === 'draft'" 
                  @click="editNotification(item)" 
                  class="btn-icon" 
                  title="Sửa"
                >
                  <i class="material-icons">edit</i>
                </button>
                <button 
                  v-if="item.status === 'draft' || item.status === 'scheduled'" 
                  @click="deleteNotification(item)" 
                  class="btn-icon danger" 
                  title="Xóa"
                >
                  <i class="material-icons">delete</i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create/Edit Dialog -->
    <div v-if="showCreateDialog" class="modal-overlay" @click="closeDialog">
      <div class="modal modal-lg" @click.stop>
        <div class="modal-header">
          <h3>{{ editingNotification ? 'Chỉnh sửa thông báo' : 'Tạo thông báo mới' }}</h3>
          <button @click="closeDialog" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveNotification">
            <div class="form-group">
              <label class="required">Loại thông báo</label>
              <select v-model="formData.type" class="form-control" required>
                <option value="">-- Chọn loại --</option>
                <option value="promotion">Khuyến mãi</option>
                <option value="order">Đơn hàng</option>
                <option value="system">Hệ thống</option>
                <option value="announcement">Thông báo chung</option>
              </select>
            </div>

            <div class="form-group">
              <label class="required">Tiêu đề</label>
              <input 
                v-model="formData.title"
                type="text" 
                class="form-control" 
                placeholder="Tiêu đề ngắn gọn, thu hút"
                required
              />
            </div>

            <div class="form-group">
              <label class="required">Nội dung</label>
              <textarea 
                v-model="formData.message"
                class="form-control" 
                rows="5"
                placeholder="Nội dung chi tiết thông báo..."
                required
              ></textarea>
            </div>

            <div class="form-group">
              <label>Link liên kết (tùy chọn)</label>
              <input 
                v-model="formData.link"
                type="url" 
                class="form-control" 
                placeholder="https://..."
              />
              <small class="form-help">Link sẽ mở khi người dùng nhấn vào thông báo</small>
            </div>

            <div class="form-group">
              <label class="required">Đối tượng nhận</label>
              <div class="radio-group">
                <label class="radio-option">
                  <input type="radio" v-model="formData.recipientType" value="all" />
                  <i class="material-icons">public</i>
                  Tất cả người dùng
                </label>
                <label class="radio-option">
                  <input type="radio" v-model="formData.recipientType" value="role" />
                  <i class="material-icons">badge</i>
                  Theo vai trò
                </label>
                <label class="radio-option">
                  <input type="radio" v-model="formData.recipientType" value="specific" />
                  <i class="material-icons">person</i>
                  Người dùng cụ thể
                </label>
              </div>
            </div>

            <div v-if="formData.recipientType === 'role'" class="form-group">
              <label>Chọn vai trò</label>
              <select v-model="formData.role" class="form-control">
                <option value="">-- Chọn --</option>
                <option value="USER">Khách hàng</option>
                <option value="ADMIN">Quản trị viên</option>
              </select>
            </div>

            <div v-if="formData.recipientType === 'specific'" class="form-group">
              <label>Email người nhận (cách nhau bởi dấu phẩy)</label>
              <textarea 
                v-model="formData.recipientEmails"
                class="form-control" 
                rows="3"
                placeholder="user1@gmail.com, user2@gmail.com"
              ></textarea>
            </div>

            <div class="form-group">
              <label>Thời gian gửi</label>
              <div class="radio-group">
                <label class="radio-option">
                  <input type="radio" v-model="formData.sendTime" value="now" />
                  <i class="material-icons">send</i>
                  Gửi ngay
                </label>
                <label class="radio-option">
                  <input type="radio" v-model="formData.sendTime" value="schedule" />
                  <i class="material-icons">schedule</i>
                  Lên lịch
                </label>
              </div>
            </div>

            <div v-if="formData.sendTime === 'schedule'" class="form-group">
              <label class="required">Thời gian gửi</label>
              <input 
                v-model="formData.scheduledAt"
                type="datetime-local" 
                class="form-control" 
                :required="formData.sendTime === 'schedule'"
              />
            </div>

            <div class="form-actions">
              <button type="button" @click="closeDialog" class="btn btn-secondary">
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :disabled="saving">
                <i class="material-icons">{{ formData.sendTime === 'now' ? 'send' : 'schedule_send' }}</i>
                {{ saving ? 'Đang xử lý...' : (formData.sendTime === 'now' ? 'Gửi ngay' : 'Lên lịch') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Detail Dialog -->
    <div v-if="showDetailDialog" class="modal-overlay" @click="showDetailDialog = false">
      <div class="modal modal-md" @click.stop>
        <div class="modal-header">
          <h3>Chi tiết thông báo</h3>
          <button @click="showDetailDialog = false" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedNotification" class="notification-detail">
            <div class="detail-item">
              <label>Loại:</label>
              <span class="badge" :class="getTypeBadgeClass(selectedNotification.type)">
                {{ getTypeText(selectedNotification.type) }}
              </span>
            </div>
            <div class="detail-item">
              <label>Tiêu đề:</label>
              <strong>{{ selectedNotification.title }}</strong>
            </div>
            <div class="detail-item">
              <label>Nội dung:</label>
              <p>{{ selectedNotification.message }}</p>
            </div>
            <div v-if="selectedNotification.link" class="detail-item">
              <label>Link:</label>
              <a :href="selectedNotification.link" target="_blank">{{ selectedNotification.link }}</a>
            </div>
            <div class="detail-item">
              <label>Đối tượng:</label>
              <p>{{ getRecipientText(selectedNotification) }}</p>
            </div>
            <div class="detail-item">
              <label>Trạng thái:</label>
              <span class="status-badge" :class="getStatusClass(selectedNotification.status)">
                {{ getStatusText(selectedNotification.status) }}
              </span>
            </div>
            <div v-if="selectedNotification.status === 'sent'" class="detail-item">
              <label>Thống kê:</label>
              <p>Đã gửi: {{ selectedNotification.recipientCount }}</p>
              <p>Đã đọc: {{ selectedNotification.openedCount }} ({{ getReadRate(selectedNotification) }}%)</p>
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
import { ref, reactive, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const saving = ref(false)
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const editingNotification = ref(null)
const selectedNotification = ref(null)
const notifications = ref([])

const filters = reactive({
  search: '',
  type: '',
  status: ''
})

const stats = reactive({
  sent: 0,
  opened: 0,
  scheduled: 0,
  failed: 0
})

const formData = reactive({
  type: '',
  title: '',
  message: '',
  link: '',
  recipientType: 'all',
  role: '',
  recipientEmails: '',
  sendTime: 'now',
  scheduledAt: ''
})

// Methods
const fetchNotifications = async () => {
  try {
    loading.value = true
    
    // Mock data cho notifications
    const mockNotifications = [
      {
        id: 1,
        type: 'order',
        title: 'Đơn hàng mới #ORD001',
        message: 'Bạn có đơn hàng mới từ khách hàng Nguyễn Văn A',
        status: 'sent',
        createdAt: '2024-01-15T10:30:00Z',
        scheduledAt: null,
        sentAt: '2024-01-15T10:30:00Z',
        openedCount: 1,
        targetUsers: 1,
        priority: 'high'
      },
      {
        id: 2,
        type: 'promotion',
        title: 'Flash Sale 50%',
        message: 'Chương trình flash sale giảm giá 50% cho tất cả sản phẩm Nike',
        status: 'scheduled',
        createdAt: '2024-01-15T09:00:00Z',
        scheduledAt: '2024-01-16T08:00:00Z',
        sentAt: null,
        openedCount: 0,
        targetUsers: 1000,
        priority: 'medium'
      },
      {
        id: 3,
        type: 'system',
        title: 'Bảo trì hệ thống',
        message: 'Hệ thống sẽ bảo trì từ 2:00 - 4:00 ngày mai',
        status: 'sent',
        createdAt: '2024-01-15T08:00:00Z',
        scheduledAt: null,
        sentAt: '2024-01-15T08:00:00Z',
        openedCount: 5,
        targetUsers: 5,
        priority: 'high'
      },
      {
        id: 4,
        type: 'marketing',
        title: 'Khuyến mãi cuối tuần',
        message: 'Giảm giá 30% cho tất cả sản phẩm Adidas cuối tuần này',
        status: 'failed',
        createdAt: '2024-01-14T15:00:00Z',
        scheduledAt: '2024-01-14T16:00:00Z',
        sentAt: null,
        openedCount: 0,
        targetUsers: 500,
        priority: 'low'
      },
      {
        id: 5,
        type: 'order',
        title: 'Đơn hàng đã hủy #ORD002',
        message: 'Đơn hàng #ORD002 đã được hủy bởi khách hàng',
        status: 'sent',
        createdAt: '2024-01-14T14:30:00Z',
        scheduledAt: null,
        sentAt: '2024-01-14T14:30:00Z',
        openedCount: 1,
        targetUsers: 1,
        priority: 'medium'
      }
    ]
    
    // Apply filters
    let filteredNotifications = mockNotifications
    
    if (filters.search) {
      filteredNotifications = filteredNotifications.filter(n => 
        n.title.toLowerCase().includes(filters.search.toLowerCase()) ||
        n.message.toLowerCase().includes(filters.search.toLowerCase())
      )
    }
    
    if (filters.type) {
      filteredNotifications = filteredNotifications.filter(n => n.type === filters.type)
    }
    
    if (filters.status) {
      filteredNotifications = filteredNotifications.filter(n => n.status === filters.status)
    }
    
    notifications.value = filteredNotifications
    updateStats()
    
    console.log('✅ Notifications loaded successfully')
  } catch (error) {
    console.error('Lỗi tải dữ liệu:', error)
    ElMessage.error('Không thể tải danh sách thông báo')
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.sent = notifications.value.filter(n => n.status === 'sent').length
  stats.opened = notifications.value.reduce((sum, n) => sum + (n.openedCount || 0), 0)
  stats.scheduled = notifications.value.filter(n => n.status === 'scheduled').length
  stats.failed = notifications.value.filter(n => n.status === 'failed').length
}

const handleSearch = () => {
  setTimeout(() => fetchNotifications(), 300)
}

const resetFilters = () => {
  filters.search = ''
  filters.type = ''
  filters.status = ''
  fetchNotifications()
}

const viewNotification = (item) => {
  selectedNotification.value = item
  showDetailDialog.value = true
}

const editNotification = (item) => {
  editingNotification.value = item
  Object.assign(formData, {
    type: item.type,
    title: item.title,
    message: item.message,
    link: item.link || '',
    recipientType: item.recipientType,
    role: item.role || '',
    recipientEmails: item.recipientEmails || '',
    sendTime: item.scheduledAt ? 'schedule' : 'now',
    scheduledAt: item.scheduledAt ? item.scheduledAt.substring(0, 16) : ''
  })
  showCreateDialog.value = true
}

const saveNotification = async () => {
  try {
    saving.value = true
    
    const notificationData = {
      type: formData.type,
      title: formData.title,
      message: formData.message,
      link: formData.link || null,
      recipientType: formData.recipientType,
      role: formData.role || null,
      recipientEmails: formData.recipientEmails || null,
      scheduledAt: formData.sendTime === 'schedule' ? formData.scheduledAt : null
    }
    
    if (editingNotification.value) {
      await adminStore.updateNotification(editingNotification.value.id, notificationData)
      ElMessage.success('Cập nhật thông báo thành công!')
    } else {
      await adminStore.createNotification(notificationData)
      ElMessage.success('Tạo thông báo thành công!')
    }
    
    closeDialog()
    fetchNotifications()
  } catch (error) {
    console.error('Lỗi lưu:', error)
    ElMessage.error('Có lỗi xảy ra khi lưu thông báo!')
  } finally {
    saving.value = false
  }
}

const deleteNotification = async (item) => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc muốn xóa thông báo này?',
      'Xác nhận xóa',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    await adminStore.deleteNotification(item.id)
    ElMessage.success('Đã xóa thông báo!')
    fetchNotifications()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Lỗi xóa:', error)
      ElMessage.error('Có lỗi xảy ra khi xóa thông báo!')
    }
  }
}

const closeDialog = () => {
  showCreateDialog.value = false
  editingNotification.value = null
  Object.assign(formData, {
    type: '',
    title: '',
    message: '',
    link: '',
    recipientType: 'all',
    role: '',
    recipientEmails: '',
    sendTime: 'now',
    scheduledAt: ''
  })
}

const getTypeIcon = (type) => {
  const icons = {
    promotion: 'local_offer',
    order: 'shopping_bag',
    system: 'settings',
    announcement: 'campaign'
  }
  return icons[type] || 'notifications'
}

const getTypeColor = (type) => {
  const colors = {
    promotion: '#10b981',
    order: '#3b82f6',
    system: '#f59e0b',
    announcement: '#8b5cf6'
  }
  return colors[type] || '#64748b'
}

const getTypeText = (type) => {
  const types = {
    promotion: 'Khuyến mãi',
    order: 'Đơn hàng',
    system: 'Hệ thống',
    announcement: 'Thông báo'
  }
  return types[type] || type
}

const getTypeBadgeClass = (type) => {
  const classes = {
    promotion: 'badge-success',
    order: 'badge-info',
    system: 'badge-warning',
    announcement: 'badge-primary'
  }
  return classes[type]
}

const getRecipientText = (item) => {
  if (item.recipientType === 'all') return 'Tất cả người dùng'
  if (item.recipientType === 'role') return `Vai trò: ${item.role}`
  if (item.recipientType === 'specific') return `${item.recipientCount} người`
  return '-'
}

const getStatusClass = (status) => {
  const classes = {
    draft: 'status-inactive',
    scheduled: 'status-pending',
    sent: 'status-completed',
    failed: 'status-cancelled'
  }
  return classes[status]
}

const getStatusText = (status) => {
  const statuses = {
    draft: 'Bản nháp',
    scheduled: 'Đã lên lịch',
    sent: 'Đã gửi',
    failed: 'Thất bại'
  }
  return statuses[status] || status
}

const getReadRate = (item) => {
  if (!item.recipientCount || item.recipientCount === 0) return 0
  return Math.round((item.openedCount / item.recipientCount) * 100)
}

const truncate = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  return new Intl.DateTimeFormat('vi-VN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(dateString))
}

// Lifecycle
onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
/* ===== ADMIN NOTIFICATIONS PAGE - UNIFIED DARK THEME ===== */

/* Page Layout */
.admin-notifications {
  padding: var(--space-8);
  max-width: 1600px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* Page Header */
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
  align-items: flex-start;
  gap: var(--space-6);
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
}

.page-subtitle {
  color: var(--text-secondary);
  margin: 0;
  font-size: var(--text-base);
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.page-subtitle .material-icons {
  font-size: 1.125rem;
  color: var(--accent-primary);
}

.header-actions {
  display: flex;
  gap: var(--space-3);
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stats-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stats-card.success::before {
  background: var(--gradient-success);
}

.stats-card.warning::before {
  background: var(--gradient-warning);
}

.stats-card.danger::before {
  background: var(--gradient-danger);
}

.stats-card.info::before {
  background: var(--gradient-info);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--border-hover);
}

.stats-card:hover::before {
  opacity: 1;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-4);
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.stats-icon.success {
  background: var(--gradient-success);
  color: white;
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3);
}

.stats-icon.warning {
  background: var(--gradient-warning);
  color: white;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.3);
}

.stats-icon.danger {
  background: var(--gradient-danger);
  color: white;
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.3);
}

.stats-icon.info {
  background: var(--gradient-info);
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.stats-card:hover .stats-icon {
  transform: scale(1.1) rotate(5deg);
}

.stats-icon .material-icons {
  font-size: 26px;
}

.stats-info {
  flex: 1;
  text-align: right;
}

.stats-value {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1;
  margin: 0 0 var(--space-2) 0;
}

.stats-label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Filters Section */
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
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: var(--space-4);
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-group label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.search-box {
  position: relative;
}

.search-icon {
  position: absolute;
  left: var(--space-3);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  font-size: 1.25rem;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-3) var(--space-3) var(--space-10);
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: all 0.3s ease;
}

.search-input:hover {
  border-color: var(--border-hover);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
  background: rgba(15, 23, 42, 0.8);
}

.search-input::placeholder {
  color: var(--text-tertiary);
}

.form-control {
  padding: var(--space-3) var(--space-4);
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: all 0.3s ease;
}

.form-control:hover {
  border-color: var(--border-hover);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
  background: rgba(15, 23, 42, 0.8);
}

/* Table Container */
.table-container {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* Admin Table */
.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table thead {
  background: var(--table-header-bg);
}

/* Table headers use global admin-tables.css styles */

.admin-table tbody tr {
  border-bottom: 1px solid var(--border-primary);
  transition: all 0.2s ease;
}

.admin-table tbody tr:hover {
  background: var(--gradient-purple-soft);
  transform: scale(1.002);
}

/* Table cells use global admin-tables.css styles */

/* Notification Title Cell */
.notification-title {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
  max-width: 400px;
}

.notification-title .material-icons {
  font-size: 1.5rem;
  flex-shrink: 0;
  margin-top: 2px;
}

.notification-title strong {
  display: block;
  margin-bottom: var(--space-1);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.notification-title p {
  margin: 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  line-height: 1.5;
}

/* Recipient Info */
.recipient-info {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
}

.recipient-info .material-icons {
  font-size: 1.125rem;
  color: var(--accent-primary);
}

/* Time Info */
.time-info {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  white-space: nowrap;
}

/* Read Rate */
.read-rate {
  min-width: 180px;
}

.read-rate span {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin-top: var(--space-1);
  display: block;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: rgba(15, 23, 42, 0.6);
  border-radius: var(--radius-full);
  overflow: hidden;
  margin-bottom: var(--space-2);
  border: 1px solid var(--border-primary);
}

.progress-fill {
  height: 100%;
  background: var(--gradient-primary);
  transition: width 0.3s ease;
  box-shadow: 0 0 8px rgba(167, 139, 250, 0.5);
}

/* Badges */
.badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  white-space: nowrap;
  transition: all 0.2s ease;
}

.badge-success {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.badge-warning {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.badge-info {
  background: var(--info-bg);
  color: var(--info-text);
  border: 1px solid var(--info-border);
}

.badge-primary {
  background: var(--gradient-purple-soft);
  color: var(--accent-primary);
  border: 1px solid var(--border-primary);
}

/* Status Badges */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  white-space: nowrap;
}

.status-completed {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.status-pending {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.status-cancelled {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

.status-inactive {
  background: rgba(100, 116, 139, 0.15);
  color: var(--text-tertiary);
  border: 1px solid var(--border-primary);
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: var(--space-2);
  align-items: center;
}

/* Action buttons use global admin-tables.css styles */

/* Loading & Empty States */
.loading-container {
  padding: var(--space-16) var(--space-8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: var(--accent-primary);
  border-radius: var(--radius-full);
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-container p {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

.empty-state {
  padding: var(--space-16) var(--space-8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
}

.empty-state .material-icons {
  font-size: 64px;
  color: var(--text-disabled);
}

.empty-state h3 {
  margin: 0;
  color: var(--text-secondary);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
}

.empty-state p {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* Modal Overlay */
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
  animation: fadeIn 0.2s ease;
}

/* Modal */
.modal {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-2xl);
  border: 1px solid var(--border-primary);
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.modal-md {
  width: 100%;
  max-width: 600px;
}

.modal-lg {
  width: 100%;
  max-width: 800px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
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
  padding: var(--space-6) var(--space-8);
  border-bottom: 1px solid var(--border-primary);
}

.modal-header h3 {
  margin: 0;
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.modal-close {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.4);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-secondary);
}

.modal-close:hover {
  background: var(--error-bg);
  border-color: var(--error-border);
  color: var(--error-text);
  transform: rotate(90deg);
}

.modal-body {
  padding: var(--space-8);
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-6) var(--space-8);
  border-top: 1px solid var(--border-primary);
}

/* Form */
.form-group {
  margin-bottom: var(--space-5);
}

.form-group label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.form-group label.required::after {
  content: ' *';
  color: var(--error-text);
}

.form-control {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-control:hover {
  border-color: var(--border-hover);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
  background: rgba(15, 23, 42, 0.8);
}

textarea.form-control {
  resize: vertical;
  min-height: 100px;
}

.form-help {
  display: block;
  margin-top: var(--space-2);
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

/* Radio Group */
.radio-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: var(--space-3);
  margin-top: var(--space-3);
}

.radio-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(15, 23, 42, 0.4);
}

.radio-option:hover {
  border-color: var(--accent-primary);
  background: var(--gradient-purple-soft);
}

.radio-option:has(input:checked) {
  border-color: var(--accent-primary);
  background: var(--gradient-purple-soft);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.radio-option input[type="radio"] {
  margin: 0;
  cursor: pointer;
}

.radio-option .material-icons {
  font-size: 2rem;
  color: var(--accent-primary);
}

/* Notification Detail */
.notification-detail {
  padding: var(--space-4);
}

.detail-item {
  margin-bottom: var(--space-5);
  padding-bottom: var(--space-4);
  border-bottom: 1px solid var(--border-primary);
}

.detail-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.detail-item label {
  display: block;
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: var(--space-2);
}

.detail-item strong {
  font-size: var(--text-base);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.detail-item p {
  margin: var(--space-1) 0 0 0;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.6;
}

.detail-item a {
  color: var(--accent-primary);
  text-decoration: none;
  font-size: var(--text-sm);
  transition: all 0.2s ease;
}

.detail-item a:hover {
  text-decoration: underline;
  color: var(--accent-light);
}

/* Form Actions */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

/* Buttons */
.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  white-space: nowrap;
}

.btn-primary {
  background: var(--gradient-primary);
  color: white;
  box-shadow: var(--shadow-btn);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-secondary);
  border: 1px solid var(--border-primary);
}

.btn-secondary:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--accent-primary);
  color: var(--accent-primary);
}

.btn .material-icons {
  font-size: 1.125rem;
}

/* Responsive */
@media (max-width: 1024px) {
  .filter-row {
    grid-template-columns: 1fr 1fr;
  }

  .filter-group:first-child {
    grid-column: 1 / -1;
  }
}

@media (max-width: 768px) {
  .admin-notifications {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-6);
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-title {
    font-size: var(--text-2xl);
  }

  .header-actions {
    width: 100%;
  }

  .header-actions .btn {
    flex: 1;
    justify-content: center;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .filter-group:first-child {
    grid-column: 1;
  }

  .table-container {
    overflow-x: auto;
  }

  .admin-table {
    min-width: 800px;
  }

  .notification-title {
    max-width: 250px;
  }

  .modal {
    margin: var(--space-4);
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: var(--space-4);
  }

  .radio-group {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>


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
    // TODO: Call API
    // Mock data
    notifications.value = [
      {
        id: 1,
        type: 'promotion',
        title: 'Flash Sale Cuối Tuần - Giảm đến 50%',
        message: 'Chương trình Flash Sale lớn nhất trong tháng! Giảm giá lên đến 50% cho tất cả sản phẩm Nike, Adidas. Nhanh tay đặt hàng ngay!',
        link: '/promotions/flash-sale',
        recipientType: 'all',
        recipientCount: 1500,
        openedCount: 876,
        status: 'sent',
        sentAt: '2025-01-20T09:00:00',
        createdAt: '2025-01-19T14:00:00'
      },
      {
        id: 2,
        type: 'system',
        title: 'Bảo trì hệ thống',
        message: 'Hệ thống sẽ bảo trì từ 2h-4h sáng ngày 22/01/2025. Vui lòng hoàn tất giao dịch trước thời gian này.',
        link: null,
        recipientType: 'all',
        recipientCount: null,
        openedCount: null,
        status: 'scheduled',
        scheduledAt: '2025-01-21T18:00:00',
        createdAt: '2025-01-20T10:00:00'
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
    // TODO: Call API
    console.log('Save notification:', formData)
    alert('Lưu thành công!')
    closeDialog()
    fetchNotifications()
  } catch (error) {
    console.error('Lỗi lưu:', error)
    alert('Có lỗi xảy ra!')
  } finally {
    saving.value = false
  }
}

const deleteNotification = async (item) => {
  if (!confirm('Bạn có chắc muốn xóa thông báo này?')) return
  // TODO: Call API
  alert('Đã xóa!')
  fetchNotifications()
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
.notification-title {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  max-width: 400px;
}

.notification-title .material-icons {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notification-title strong {
  display: block;
  margin-bottom: 0.25rem;
}

.notification-title p {
  margin: 0;
  font-size: 0.75rem;
  color: #64748b;
  line-height: 1.4;
}

.recipient-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.recipient-info .material-icons {
  font-size: 1rem;
  color: #667eea;
}

.time-info {
  font-size: 0.875rem;
  color: #64748b;
}

.read-rate {
  min-width: 150px;
}

.read-rate span {
  font-size: 0.75rem;
  color: #64748b;
  margin-top: 0.25rem;
  display: block;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 0.25rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.radio-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.radio-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.radio-option:has(input:checked) {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.radio-option input[type="radio"] {
  margin: 0;
}

.radio-option .material-icons {
  font-size: 2rem;
  color: #667eea;
}

.notification-detail {
  padding: 1rem;
}

.detail-item {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item label {
  display: block;
  font-size: 0.75rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.detail-item strong {
  font-size: 1rem;
  color: #1e293b;
}

.detail-item p {
  margin: 0.25rem 0 0 0;
  font-size: 0.875rem;
  color: #475569;
  line-height: 1.6;
}

.detail-item a {
  color: #667eea;
  text-decoration: none;
  font-size: 0.875rem;
}

.detail-item a:hover {
  text-decoration: underline;
}

.form-help {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: #64748b;
}
</style>


<template>
  <div class="admin-page admin-loyalty">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">loyalty</span>
            Quản lý điểm thưởng
          </h1>
          <p class="page-subtitle">Quản lý hệ thống điểm thưởng và chương trình khách hàng VIP</p>
        </div>
        <div class="header-actions">
          <button @click="exportLoyalty('csv')" class="btn btn-secondary">
            <span class="material-icons">file_download</span>
            CSV
          </button>
          <button @click="exportLoyalty('json')" class="btn btn-secondary">
            <span class="material-icons">code</span>
            JSON
          </button>
          <button @click="openSettingsModal" class="btn btn-primary">
            <span class="material-icons">settings</span>
            Cài đặt
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="stats-grid animate-fade-up">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <span class="material-icons">stars</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Tổng điểm đã phát</div>
          <div class="stat-value">{{ formatNumber(totalPointsEarned) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            +{{ formatNumber(pointsThisMonth) }} tháng này
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <span class="material-icons">redeem</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Điểm đã sử dụng</div>
          <div class="stat-value">{{ formatNumber(totalPointsRedeemed) }}</div>
          <div class="stat-change positive">
            <span class="material-icons">shopping_cart</span>
            {{ Math.round((totalPointsRedeemed / totalPointsEarned) * 100) || 0 }}% tỷ lệ sử dụng
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <span class="material-icons">schedule</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Điểm sắp hết hạn</div>
          <div class="stat-value">{{ formatNumber(expiringPoints) }}</div>
          <div class="stat-change neutral">
            <span class="material-icons">warning</span>
            Trong 30 ngày tới
          </div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <span class="material-icons">diamond</span>
        </div>
        <div class="stat-content">
          <div class="stat-label">Khách hàng VIP</div>
          <div class="stat-value">{{ vipCustomers }}</div>
          <div class="stat-change positive">
            <span class="material-icons">trending_up</span>
            +{{ newVipThisMonth }} tháng này
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
              placeholder="Tìm theo tên khách hàng, email..."
            />
            <button v-if="searchKeyword" @click="clearSearch" class="search-clear">
              <span class="material-icons">close</span>
            </button>
          </div>
        </div>
        
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">filter_list</span>
            Loại giao dịch
          </label>
          <select class="filter-select" v-model="filterType">
            <option value="all">Tất cả</option>
            <option value="earn">Tích điểm</option>
            <option value="redeem">Đổi điểm</option>
            <option value="expire">Hết hạn</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">diamond</span>
            Cấp độ
          </label>
          <select class="filter-select" v-model="filterLevel">
            <option value="all">Tất cả</option>
            <option value="bronze">Đồng</option>
            <option value="silver">Bạc</option>
            <option value="gold">Vàng</option>
            <option value="platinum">Bạch kim</option>
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

    <!-- Loyalty Points Table -->
    <div class="table-container animate-fade-up">
      <div class="table-header">
        <h3 class="table-title">Lịch sử điểm thưởng</h3>
        <div class="table-actions">
          <span class="table-info">{{ filteredPoints.length }} giao dịch</span>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <div v-else-if="filteredPoints.length === 0" class="empty-state">
        <span class="material-icons">loyalty</span>
        <h3>Không có giao dịch nào</h3>
        <p>Chưa có giao dịch điểm thưởng nào được tìm thấy</p>
      </div>

      <div v-else class="table-responsive">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Khách hàng</th>
              <th>Loại giao dịch</th>
              <th>Số điểm</th>
              <th>Nguồn</th>
              <th>Cấp độ</th>
              <th>Thời gian</th>
              <th>Hết hạn</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="point in paginatedPoints" :key="point.id" class="point-row">
              <td>
                <div class="customer-info">
                  <div class="customer-avatar">
                    <img v-if="point.customerAvatar" :src="point.customerAvatar" :alt="point.customerName">
                    <span v-else class="material-icons">person</span>
                  </div>
                  <div class="customer-details">
                    <div class="customer-name">{{ point.customerName }}</div>
                    <div class="customer-email">{{ point.customerEmail }}</div>
                  </div>
                </div>
              </td>
              <td>
                <span :class="['transaction-type', `type-${point.transactionType}`]">
                  <span class="material-icons">{{ getTransactionIcon(point.transactionType) }}</span>
                  {{ getTransactionText(point.transactionType) }}
                </span>
              </td>
              <td>
                <div class="points-amount">
                  <span :class="['points-value', point.transactionType === 'earn' ? 'positive' : 'negative']">
                    {{ point.transactionType === 'earn' ? '+' : '-' }}{{ formatNumber(Math.abs(point.points)) }}
                  </span>
                </div>
              </td>
              <td>
                <div class="source-info">
                  <div class="source-type">{{ getSourceText(point.earnedFromOrderId, point.redeemedInOrderId) }}</div>
                  <div v-if="point.description" class="source-description">{{ point.description }}</div>
                </div>
              </td>
              <td>
                <span :class="['level-badge', `level-${point.customerLevel}`]">
                  <span class="material-icons">{{ getLevelIcon(point.customerLevel) }}</span>
                  {{ getLevelText(point.customerLevel) }}
                </span>
              </td>
              <td>
                <div class="transaction-time">
                  <div class="time">{{ formatDateTime(point.createdAt) }}</div>
                </div>
              </td>
              <td>
                <div v-if="point.expiresAt" class="expiry-info">
                  <div class="expiry-date">{{ formatDate(point.expiresAt) }}</div>
                  <div :class="['expiry-status', getExpiryStatus(point.expiresAt)]">
                    {{ getExpiryText(point.expiresAt) }}
                  </div>
                </div>
                <span v-else class="no-expiry">Không hết hạn</span>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="viewPointDetail(point)" class="btn-icon btn-view" title="Xem chi tiết">
                    <span class="material-icons">visibility</span>
                  </button>
                  <button v-if="point.transactionType === 'earn' && canExpire(point.expiresAt)" @click="extendExpiry(point)" class="btn-icon btn-extend" title="Gia hạn">
                    <span class="material-icons">schedule</span>
                  </button>
                </div>
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

    <!-- Settings Modal -->
    <div v-if="showSettingsModal" class="modal-overlay" @click="closeSettingsModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">
            <span class="material-icons">settings</span>
            Cài đặt hệ thống điểm thưởng
          </h3>
          <button @click="closeSettingsModal" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="settings-form">
            <div class="form-group">
              <label class="form-label">Tỷ lệ tích điểm (VND → Điểm)</label>
              <input 
                type="number" 
                v-model="settings.pointsPerVnd" 
                class="form-input"
                placeholder="1000"
              >
              <div class="form-help">1 điểm = {{ settings.pointsPerVnd }} VND</div>
            </div>
            
            <div class="form-group">
              <label class="form-label">Tỷ lệ đổi điểm (Điểm → VND)</label>
              <input 
                type="number" 
                v-model="settings.vndPerPoint" 
                class="form-input"
                placeholder="100"
              >
              <div class="form-help">1 điểm = {{ settings.vndPerPoint }} VND</div>
            </div>
            
            <div class="form-group">
              <label class="form-label">Thời hạn điểm (ngày)</label>
              <input 
                type="number" 
                v-model="settings.pointsExpiryDays" 
                class="form-input"
                placeholder="365"
              >
              <div class="form-help">Điểm sẽ hết hạn sau {{ settings.pointsExpiryDays }} ngày</div>
            </div>
            
            <div class="form-group">
              <label class="form-label">Điểm tối thiểu để đổi</label>
              <input 
                type="number" 
                v-model="settings.minRedeemPoints" 
                class="form-input"
                placeholder="1000"
              >
              <div class="form-help">Khách hàng cần tối thiểu {{ settings.minRedeemPoints }} điểm để đổi</div>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeSettingsModal" class="btn btn-secondary">Hủy</button>
          <button @click="saveSettings" class="btn btn-primary">Lưu cài đặt</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { ElMessage, ElMessageBox } from 'element-plus'

// State
const loading = ref(false)
const points = ref([])
const searchKeyword = ref('')
const filterType = ref('all')
const filterLevel = ref('all')
const currentPage = ref(0)
const pageSize = ref(10)
const showSettingsModal = ref(false)
const settings = ref({
  pointsPerVnd: 1000,
  vndPerPoint: 100,
  pointsExpiryDays: 365,
  minRedeemPoints: 1000
})

// Mock data
const mockPoints = ref([
  {
    id: 1,
    customerId: 1,
    customerName: 'Nguyễn Văn A',
    customerEmail: 'nguyenvana@email.com',
    customerAvatar: null,
    customerLevel: 'gold',
    points: 500,
    transactionType: 'earn',
    earnedFromOrderId: 1001,
    redeemedInOrderId: null,
    description: 'Tích điểm từ đơn hàng #ORD-20240125-0001',
    expiresAt: '2025-01-25T00:00:00Z',
    createdAt: '2024-01-25T10:30:00Z'
  },
  {
    id: 2,
    customerId: 2,
    customerName: 'Trần Thị B',
    customerEmail: 'tranthib@email.com',
    customerAvatar: null,
    customerLevel: 'silver',
    points: -200,
    transactionType: 'redeem',
    earnedFromOrderId: null,
    redeemedInOrderId: 1002,
    description: 'Đổi điểm cho đơn hàng #ORD-20240125-0002',
    expiresAt: null,
    createdAt: '2024-01-25T11:15:00Z'
  },
  {
    id: 3,
    customerId: 3,
    customerName: 'Lê Văn C',
    customerEmail: 'levanc@email.com',
    customerAvatar: null,
    customerLevel: 'platinum',
    points: 1000,
    transactionType: 'earn',
    earnedFromOrderId: 1003,
    redeemedInOrderId: null,
    description: 'Tích điểm từ đơn hàng #ORD-20240125-0003',
    expiresAt: '2025-01-25T00:00:00Z',
    createdAt: '2024-01-25T14:20:00Z'
  },
  {
    id: 4,
    customerId: 4,
    customerName: 'Phạm Thị D',
    customerEmail: 'phamthid@email.com',
    customerAvatar: null,
    customerLevel: 'bronze',
    points: -50,
    transactionType: 'expire',
    earnedFromOrderId: null,
    redeemedInOrderId: null,
    description: 'Điểm hết hạn',
    expiresAt: null,
    createdAt: '2024-01-25T15:45:00Z'
  }
])

// Computed
const totalPointsEarned = computed(() => 
  points.value.filter(p => p.transactionType === 'earn').reduce((sum, p) => sum + p.points, 0)
)
const totalPointsRedeemed = computed(() => 
  Math.abs(points.value.filter(p => p.transactionType === 'redeem').reduce((sum, p) => sum + p.points, 0))
)
const expiringPoints = computed(() => {
  const thirtyDaysFromNow = new Date()
  thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() + 30)
  
  return points.value
    .filter(p => p.transactionType === 'earn' && p.expiresAt && new Date(p.expiresAt) <= thirtyDaysFromNow)
    .reduce((sum, p) => sum + p.points, 0)
})
const pointsThisMonth = computed(() => {
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  
  return points.value
    .filter(p => {
      const pointDate = new Date(p.createdAt)
      return p.transactionType === 'earn' && 
             pointDate.getMonth() === thisMonth && 
             pointDate.getFullYear() === thisYear
    })
    .reduce((sum, p) => sum + p.points, 0)
})
const vipCustomers = computed(() => 
  new Set(points.value.filter(p => ['gold', 'platinum'].includes(p.customerLevel)).map(p => p.customerId)).size
)
const newVipThisMonth = computed(() => {
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  
  const vipThisMonth = points.value.filter(p => {
    const pointDate = new Date(p.createdAt)
    return ['gold', 'platinum'].includes(p.customerLevel) && 
           pointDate.getMonth() === thisMonth && 
           pointDate.getFullYear() === thisYear
  })
  
  return new Set(vipThisMonth.map(p => p.customerId)).size
})

const filteredPoints = computed(() => {
  let filtered = points.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.customerName.toLowerCase().includes(keyword) ||
      p.customerEmail.toLowerCase().includes(keyword)
    )
  }

  if (filterType.value !== 'all') {
    filtered = filtered.filter(p => p.transactionType === filterType.value)
  }

  if (filterLevel.value !== 'all') {
    filtered = filtered.filter(p => p.customerLevel === filterLevel.value)
  }

  return filtered
})

const totalPages = computed(() => Math.ceil(filteredPoints.value.length / pageSize.value))
const paginatedPoints = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return filteredPoints.value.slice(start, end)
})

// Methods
const fetchPoints = async () => {
  loading.value = true
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    points.value = mockPoints.value
  } catch (error) {
    ElMessage.error('Không thể tải danh sách điểm thưởng')
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
  filterLevel.value = 'all'
  currentPage.value = 0
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

const openSettingsModal = () => {
  showSettingsModal.value = true
}

const closeSettingsModal = () => {
  showSettingsModal.value = false
}

const saveSettings = () => {
  ElMessage.success('Đã lưu cài đặt thành công')
  closeSettingsModal()
}

const viewPointDetail = (point) => {
  ElMessage.info(`Xem chi tiết giao dịch #${point.id}`)
}

const extendExpiry = async (point) => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc chắn muốn gia hạn điểm này?',
      'Xác nhận gia hạn',
      {
        confirmButtonText: 'Gia hạn',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    // Simulate extend expiry
    const newExpiry = new Date()
    newExpiry.setFullYear(newExpiry.getFullYear() + 1)
    point.expiresAt = newExpiry.toISOString()
    
    ElMessage.success('Đã gia hạn điểm thành công')
  } catch {
    // User cancelled
  }
}

const exportLoyalty = (format) => {
  try {
    const exportData = filteredPoints.value.map(point => ({
      'ID': point.id,
      'Khách hàng': point.customerName,
      'Email': point.customerEmail,
      'Loại giao dịch': getTransactionText(point.transactionType),
      'Số điểm': point.points,
      'Nguồn': getSourceText(point.earnedFromOrderId, point.redeemedInOrderId),
      'Cấp độ': getLevelText(point.customerLevel),
      'Mô tả': point.description,
      'Thời gian': formatDateTime(point.createdAt),
      'Hết hạn': point.expiresAt ? formatDate(point.expiresAt) : 'Không hết hạn'
    }))

    if (format === 'csv') {
      downloadCsv('loyalty-points', exportData)
      ElMessage.success('Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('loyalty-points', exportData)
      ElMessage.success('Xuất JSON thành công!')
    }
  } catch (error) {
    console.error('Export error:', error)
    ElMessage.error('Có lỗi xảy ra khi xuất dữ liệu!')
  }
}

// Helper functions
const getTransactionIcon = (type) => {
  const icons = {
    earn: 'add_circle',
    redeem: 'remove_circle',
    expire: 'schedule'
  }
  return icons[type] || 'help'
}

const getTransactionText = (type) => {
  const texts = {
    earn: 'Tích điểm',
    redeem: 'Đổi điểm',
    expire: 'Hết hạn'
  }
  return texts[type] || type
}

const getSourceText = (earnedFrom, redeemedIn) => {
  if (earnedFrom) return `Đơn hàng #${earnedFrom}`
  if (redeemedIn) return `Đơn hàng #${redeemedIn}`
  return 'Hệ thống'
}

const getLevelIcon = (level) => {
  const icons = {
    bronze: 'looks_one',
    silver: 'looks_two',
    gold: 'looks_3',
    platinum: 'diamond'
  }
  return icons[level] || 'help'
}

const getLevelText = (level) => {
  const texts = {
    bronze: 'Đồng',
    silver: 'Bạc',
    gold: 'Vàng',
    platinum: 'Bạch kim'
  }
  return texts[level] || level
}

const getExpiryStatus = (expiresAt) => {
  if (!expiresAt) return 'no-expiry'
  
  const now = new Date()
  const expiry = new Date(expiresAt)
  const daysLeft = Math.ceil((expiry - now) / (1000 * 60 * 60 * 24))
  
  if (daysLeft < 0) return 'expired'
  if (daysLeft <= 7) return 'expiring-soon'
  if (daysLeft <= 30) return 'expiring'
  return 'valid'
}

const getExpiryText = (expiresAt) => {
  if (!expiresAt) return 'Không hết hạn'
  
  const now = new Date()
  const expiry = new Date(expiresAt)
  const daysLeft = Math.ceil((expiry - now) / (1000 * 60 * 60 * 24))
  
  if (daysLeft < 0) return 'Đã hết hạn'
  if (daysLeft === 0) return 'Hết hạn hôm nay'
  if (daysLeft === 1) return 'Hết hạn ngày mai'
  return `Còn ${daysLeft} ngày`
}

const canExpire = (expiresAt) => {
  return expiresAt && new Date(expiresAt) > new Date()
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
  fetchPoints()
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

/* Customer Info */
.customer-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.customer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.customer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.customer-avatar .material-icons {
  color: var(--text-tertiary);
}

.customer-name {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.customer-email {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* Transaction Type */
.transaction-type {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.type-earn {
  background: var(--success-bg);
  color: var(--success-text);
}

.type-redeem {
  background: var(--info-bg);
  color: var(--info-text);
}

.type-expire {
  background: var(--warning-bg);
  color: var(--warning-text);
}

/* Points Amount */
.points-amount {
  text-align: right;
}

.points-value {
  font-weight: var(--font-bold);
  font-size: var(--text-lg);
}

.points-value.positive {
  color: var(--success-text);
}

.points-value.negative {
  color: var(--error-text);
}

/* Level Badge */
.level-badge {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
}

.level-bronze {
  background: #cd7f32;
  color: white;
}

.level-silver {
  background: #c0c0c0;
  color: #333;
}

.level-gold {
  background: #ffd700;
  color: #333;
}

.level-platinum {
  background: #e5e4e2;
  color: #333;
}

/* Expiry Status */
.expiry-status {
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
}

.expiry-status.valid {
  background: var(--success-bg);
  color: var(--success-text);
}

.expiry-status.expiring {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.expiry-status.expiring-soon {
  background: var(--error-bg);
  color: var(--error-text);
}

.expiry-status.expired {
  background: var(--error-bg);
  color: var(--error-text);
}

.no-expiry {
  color: var(--text-tertiary);
  font-style: italic;
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: var(--space-2);
}

.btn-icon {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.btn-icon:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.btn-view:hover {
  background: var(--info-bg);
  color: var(--info-text);
}

.btn-extend:hover {
  background: var(--warning-bg);
  color: var(--warning-text);
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

.settings-form {
  display: flex;
  flex-direction: column;
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

.form-help {
  font-size: var(--text-xs);
  color: var(--text-secondary);
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
}
</style>

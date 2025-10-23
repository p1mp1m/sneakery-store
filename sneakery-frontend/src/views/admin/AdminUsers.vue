<template>
  <div class="admin-page admin-users">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="material-icons">people</i>
            Quản lý người dùng
          </h1>
          <p class="page-subtitle">Quản lý phân quyền và trạng thái tài khoản</p>
        </div>
        <div class="header-actions">
          <button @click="exportToExcel" class="btn btn-secondary">
            <i class="material-icons">download</i>
            Export Excel
          </button>
        </div>
      </div>
    </div>

    <!-- Search & Filters -->
    <div class="filters-section animate-fade-up">
      <div class="search-box">
        <i class="material-icons search-icon">search</i>
        <input
          v-model="filters.search"
          @input="debounceSearch"
          type="text"
          placeholder="Tìm theo email, tên hoặc số điện thoại..."
          class="search-input"
        />
        <button
          v-if="filters.search"
          @click="clearSearch"
          class="clear-btn"
          title="Xóa tìm kiếm"
        >
          <i class="material-icons">close</i>
        </button>
      </div>

      <div class="filter-controls">
        <div class="filter-group">
          <label>Vai trò</label>
          <select v-model="filters.role" @change="applyFilters" class="filter-select">
            <option value="">Tất cả</option>
            <option value="USER">Người dùng</option>
            <option value="ADMIN">Quản trị viên</option>
            <option value="MODERATOR">Điều hành viên</option>
          </select>
        </div>

        <div class="filter-group">
          <label>Trạng thái</label>
          <select v-model="filters.isActive" @change="applyFilters" class="filter-select">
            <option value="">Tất cả</option>
            <option :value="true">Hoạt động</option>
            <option :value="false">Bị khóa</option>
          </select>
        </div>

        <button @click="resetFilters" class="btn-reset" title="Xóa tất cả bộ lọc">
          <i class="material-icons">refresh</i>
          Reset
        </button>
      </div>
    </div>
    
    <!-- Loading State -->
    <div v-if="loading" class="loading-container animate-fade-in">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách người dùng...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="users.length === 0" class="empty-state animate-fade-up">
      <i class="material-icons">people</i>
      <h3>Chưa có người dùng</h3>
    </div>

    <!-- Bulk Action Bar for Users -->
    <div v-if="selectedUsers.length > 0" class="bulk-action-bar">
      <div class="bulk-info">
        <i class="material-icons">check_circle</i>
        Đã chọn <strong>{{ selectedUsers.length }}</strong> người dùng
          </div>
      <div class="bulk-actions">
        <select v-model="bulkAction" class="bulk-action-select">
          <option value="">-- Chọn hành động --</option>
          <option value="lock">Khóa tài khoản</option>
          <option value="unlock">Mở khóa tài khoản</option>
          <option value="role-user">Đặt vai trò: USER</option>
          <option value="role-admin">Đặt vai trò: ADMIN</option>
        </select>
        <button @click="executeBulkAction" :disabled="!bulkAction" class="btn btn-primary">
          <i class="material-icons">done_all</i>
          Thực hiện
        </button>
        <button @click="clearUserSelection" class="btn btn-secondary">
          <i class="material-icons">clear</i>
          Bỏ chọn
        </button>
          </div>
        </div>

    <!-- Users List -->
    <div v-else class="table-card animate-fade-up">
      <table class="admin-table">
        <thead>
          <tr>
            <th style="width: 40px;">
              <input 
                type="checkbox" 
                :checked="isAllUsersSelected"
                @change="toggleSelectAllUsers"
                class="checkbox-input"
              />
            </th>
            <th>Họ tên</th>
            <th>Email</th>
            <th>Số điện thoại</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th class="text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>
              <input 
                type="checkbox" 
                :checked="selectedUsers.includes(user.id)"
                @change="toggleSelectUser(user.id)"
                class="checkbox-input"
              />
            </td>
            <td>{{ user.fullName }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.phoneNumber || 'N/A' }}</td>
            <td>
              <select v-model="user.role" @change="confirmRoleChange(user, $event)" class="role-select">
                <option value="USER">User</option>
                <option value="ADMIN">Admin</option>
              </select>
            </td>
            <td>
              <span :class="['status-badge', user.isActive ? 'active' : 'inactive']">
                {{ user.isActive ? 'Hoạt động' : 'Bị khóa' }}
              </span>
            </td>
            <td class="text-center">
              <button @click="confirmToggleStatus(user)" class="btn-sm" :class="user.isActive ? 'btn-danger' : 'btn-success'">
                {{ user.isActive ? 'Khóa' : 'Mở khóa' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div v-if="!loading && users.length > 0" class="pagination-container">
      <div class="pagination-info">
        Hiển thị {{ (currentPage * pageSize) + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalItems) }} 
        trong tổng số {{ totalItems }} người dùng
      </div>
      <div class="pagination">
        <button :disabled="currentPage === 0" @click="changePage(currentPage - 1)" class="page-btn">Trước</button>
        <span class="page-info">Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
        <button :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)" class="page-btn">Sau</button>
      </div>
    </div>

    <!-- Role Change Confirmation Dialog -->
    <ConfirmDialog
      v-model="showRoleConfirm"
      :type="'warning'"
      :title="'Xác nhận thay đổi vai trò'"
      :message="`Bạn có chắc chắn muốn thay đổi vai trò của ${userToUpdate?.fullName} từ '${getRoleLabel(oldRole)}' sang '${getRoleLabel(newRole)}'?`"
      :description="'Hành động này sẽ thay đổi quyền hạn của người dùng.'"
      :confirm-text="'Xác nhận'"
      :cancel-text="'Hủy'"
      :loading="updating"
      @confirm="handleRoleUpdate"
      @cancel="handleCancelRoleChange"
    />

    <!-- Status Toggle Confirmation Dialog -->
    <ConfirmDialog
      v-model="showStatusConfirm"
      :type="userToToggle?.isActive ? 'warning' : 'info'"
      :title="userToToggle?.isActive ? 'Xác nhận khóa tài khoản' : 'Xác nhận mở khóa tài khoản'"
      :message="`Bạn có chắc chắn muốn ${userToToggle?.isActive ? 'khóa' : 'mở khóa'} tài khoản của ${userToToggle?.fullName}?`"
      :description="userToToggle?.isActive ? 'Người dùng sẽ không thể đăng nhập sau khi bị khóa.' : 'Người dùng sẽ có thể đăng nhập lại sau khi mở khóa.'"
      :confirm-text="userToToggle?.isActive ? 'Khóa tài khoản' : 'Mở khóa'"
      cancel-text="Hủy"
      :loading="updating"
      @confirm="handleToggleStatus"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import * as XLSX from 'xlsx'

const adminStore = useAdminStore()
const users = ref([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)

// Bulk selection state
const selectedUsers = ref([])
const bulkAction = ref('')

// Search & Filter state
const filters = ref({
  search: '',
  role: '',
  isActive: ''
})
let searchTimeout = null

// Role change confirmation
const showRoleConfirm = ref(false)
const userToUpdate = ref(null)
const oldRole = ref('')
const newRole = ref('')
const updating = ref(false)

// Status toggle confirmation
const showStatusConfirm = ref(false)
const userToToggle = ref(null)

const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

const isAllUsersSelected = computed(() => {
  return users.value.length > 0 && selectedUsers.value.length === users.value.length
})

// Bulk selection methods
const toggleSelectUser = (userId) => {
  const index = selectedUsers.value.indexOf(userId)
  if (index > -1) {
    selectedUsers.value.splice(index, 1)
  } else {
    selectedUsers.value.push(userId)
  }
}

const toggleSelectAllUsers = () => {
  if (isAllUsersSelected.value) {
    selectedUsers.value = []
  } else {
    selectedUsers.value = users.value.map(u => u.id)
  }
}

const clearUserSelection = () => {
  selectedUsers.value = []
  bulkAction.value = ''
}

const executeBulkAction = async () => {
  if (!bulkAction.value) {
    ElMessage.warning('Vui lòng chọn hành động!')
    return
  }

  const actionMap = {
    'lock': 'khóa',
    'unlock': 'mở khóa',
    'role-user': 'đặt vai trò USER cho',
    'role-admin': 'đặt vai trò ADMIN cho'
  }

  if (!confirm(`Bạn có chắc chắn muốn ${actionMap[bulkAction.value]} ${selectedUsers.value.length} người dùng?`)) {
    return
  }

  try {
    loading.value = true
    
    for (const userId of selectedUsers.value) {
      if (bulkAction.value === 'lock') {
        await adminStore.updateUserStatus(userId, false)
      } else if (bulkAction.value === 'unlock') {
        await adminStore.updateUserStatus(userId, true)
      } else if (bulkAction.value === 'role-user') {
        await adminStore.updateUserRole(userId, 'USER')
      } else if (bulkAction.value === 'role-admin') {
        await adminStore.updateUserRole(userId, 'ADMIN')
      }
    }
    
    ElMessage.success({
      message: `Đã ${actionMap[bulkAction.value]} ${selectedUsers.value.length} người dùng thành công!`,
      duration: 3000
    })
    
    // Clear selection and refresh list
    selectedUsers.value = []
    bulkAction.value = ''
    await fetchUsers()
  } catch (error) {
    console.error('Lỗi khi thực hiện hàng loạt:', error)
    ElMessage.error({
      message: 'Có lỗi xảy ra khi thực hiện hành động!',
      duration: 3000
    })
  } finally {
    loading.value = false
  }
}

const fetchUsers = async () => {
  try {
    loading.value = true
    const result = await adminStore.fetchUsers(currentPage.value, pageSize.value, filters.value)
    users.value = result.content || []
    totalItems.value = result.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách người dùng:', error)
    ElMessage.error('Không thể tải danh sách người dùng.')
  } finally {
    loading.value = false
  }
}

const debounceSearch = () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 0 // Reset về trang đầu khi search
    fetchUsers()
  }, 500) // Debounce 500ms
}

const clearSearch = () => {
  filters.value.search = ''
  currentPage.value = 0
  fetchUsers()
}

const applyFilters = () => {
  currentPage.value = 0 // Reset về trang đầu khi filter
  fetchUsers()
}

const resetFilters = () => {
  filters.value.search = ''
  filters.value.role = ''
  filters.value.isActive = ''
  currentPage.value = 0
  fetchUsers()
}

// Export to Excel
const exportToExcel = () => {
  try {
    // Chuẩn bị data để export
    const exportData = users.value.map((user, index) => ({
      'STT': index + 1,
      'ID': user.id,
      'Họ và tên': user.fullName || 'N/A',
      'Email': user.email || 'N/A',
      'Số điện thoại': user.phoneNumber || 'N/A',
      'Vai trò': getRoleLabel(user.role),
      'Trạng thái': user.isActive ? 'Hoạt động' : 'Bị khóa'
    }))

    // Tạo worksheet từ data
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    
    // Tạo workbook
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Người dùng')
    
    // Tạo tên file với timestamp
    const timestamp = new Date().toISOString().slice(0, 10)
    const filename = `nguoi-dung_${timestamp}.xlsx`
    
    // Download file
    XLSX.writeFile(workbook, filename)
    
    ElMessage.success({
      message: `Đã export ${exportData.length} người dùng thành công!`,
      duration: 3000
    })
  } catch (error) {
    console.error('Lỗi khi export Excel:', error)
    ElMessage.error({
      message: 'Không thể export dữ liệu. Vui lòng thử lại!',
      duration: 3000
    })
  }
}

const changePage = (page) => {
  currentPage.value = page
  fetchUsers()
}

const confirmRoleChange = (user, event) => {
  const select = event.target
  oldRole.value = user._originalRole || user.role
  newRole.value = select.value
  
  if (oldRole.value === newRole.value) {
    return
  }
  
  userToUpdate.value = user
  if (!user._originalRole) {
    user._originalRole = oldRole.value
  }
  
  showRoleConfirm.value = true
}

const handleRoleUpdate = async () => {
  try {
    updating.value = true
    await adminStore.updateUserRole(userToUpdate.value.id, newRole.value)
    ElMessage.success(`Đã cập nhật vai trò của ${userToUpdate.value.fullName} thành công!`)
    
    userToUpdate.value._originalRole = newRole.value
    showRoleConfirm.value = false
  } catch (error) {
    console.error('Lỗi khi cập nhật vai trò:', error)
    ElMessage.error('Không thể cập nhật vai trò. Vui lòng thử lại!')
    
    userToUpdate.value.role = oldRole.value
  } finally {
    updating.value = false
  }
}

const handleCancelRoleChange = () => {
  if (userToUpdate.value) {
    userToUpdate.value.role = oldRole.value
  }
  showRoleConfirm.value = false
}

const getRoleLabel = (role) => {
  const labels = {
    'USER': 'Người dùng',
    'ADMIN': 'Quản trị viên',
    'MODERATOR': 'Điều hành viên'
  }
  return labels[role] || role
}

const confirmToggleStatus = (user) => {
  userToToggle.value = user
  showStatusConfirm.value = true
}

const handleToggleStatus = async () => {
  try {
    updating.value = true
    const newStatus = !userToToggle.value.isActive
    await adminStore.updateUserStatus(userToToggle.value.id, newStatus)
    
    userToToggle.value.isActive = newStatus
    ElMessage.success(`Đã ${newStatus ? 'mở khóa' : 'khóa'} tài khoản ${userToToggle.value.fullName} thành công!`)
    
    showStatusConfirm.value = false
  } catch (error) {
    console.error('Lỗi khi cập nhật trạng thái:', error)
    ElMessage.error('Không thể cập nhật trạng thái. Vui lòng thử lại!')
  } finally {
    updating.value = false
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
/* ===== ADMIN USERS PAGE - UNIFIED DARK THEME ===== */
.admin-users {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-4);
}

/* Page Header */
.page-header {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
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
}

.header-actions {
  display: flex;
  gap: var(--space-3);
}

/* Bulk Action Bar */
.bulk-action-bar {
  background: var(--gradient-primary);
  color: var(--color-white);
  padding: var(--space-4) var(--space-6);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-6);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-glow-purple);
  animation: slideInFromBottom 0.3s ease-out;
}

.bulk-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
}

.bulk-info i {
  font-size: 24px;
}

.bulk-info strong {
  font-weight: var(--font-bold);
}

.bulk-actions {
  display: flex;
  gap: var(--space-4);
  align-items: center;
}

.bulk-action-select {
  padding: var(--space-2) var(--space-4);
  border: 2px solid var(--color-white);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.2);
  color: var(--color-white);
  font-weight: var(--font-medium);
  cursor: pointer;
  min-width: 200px;
  transition: var(--transition-fast);
}

.bulk-action-select:hover {
  background: rgba(255, 255, 255, 0.3);
}

.bulk-action-select option {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.bulk-actions .btn {
  border: 2px solid var(--color-white);
  font-weight: var(--font-medium);
  background: transparent;
  color: var(--color-white);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
}

.bulk-actions .btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.bulk-actions .btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--accent-primary);
}

@keyframes slideInFromBottom {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== SEARCH & FILTERS ===== */
.filters-section {
  background: var(--bg-card);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
}

.search-box {
  position: relative;
  margin-bottom: var(--space-4);
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  font-size: var(--text-xl);
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-12) var(--space-3) var(--space-12);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  transition: var(--transition-fast);
  background: var(--bg-card);
  color: var(--text-primary);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px var(--shadow-focus-purple);
  background: var(--bg-card);
}

.search-input::placeholder {
  color: var(--text-tertiary);
}

.clear-btn {
  position: absolute;
  right: var(--space-2);
  top: 50%;
  transform: translateY(-50%);
  padding: var(--space-1-5);
  border: none;
  background: transparent;
  cursor: pointer;
  color: var(--text-tertiary);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition-fast);
}

.clear-btn:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.filter-controls {
  display: flex;
  gap: var(--space-4);
  align-items: flex-end;
  flex-wrap: wrap;
}

.filter-group {
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.filter-select {
  width: 100%;
  padding: var(--space-2-5) var(--space-3);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  background: var(--bg-card);
  color: var(--text-primary);
  cursor: pointer;
  transition: var(--transition-fast);
}

.filter-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px var(--shadow-focus-purple);
}

.btn-reset {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2-5) var(--space-4);
  border: 2px solid var(--border-primary);
  background: var(--bg-card);
  border-radius: var(--radius-md);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: var(--transition-fast);
  color: var(--text-secondary);
}

.btn-reset:hover {
  background: var(--bg-secondary);
  border-color: var(--accent-primary);
  color: var(--text-primary);
  transform: translateY(-2px);
}

.btn-reset i {
  font-size: var(--text-lg);
}

/* Loading & Empty States */
.loading-container,
.empty-state {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-12);
  text-align: center;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--border-primary);
  border-top-color: var(--accent-primary);
  border-radius: var(--radius-full);
  animation: spin 1s linear infinite;
  margin: 0 auto var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-container p {
  color: var(--text-secondary);
  margin: 0;
}

.empty-state i {
  font-size: 4rem;
  color: var(--text-tertiary);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  color: var(--text-primary);
  margin: 0;
}

/* Table */
.table-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  border: 1px solid var(--border-primary);
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table thead {
  background: var(--gradient-primary);
  border-bottom: 2px solid var(--border-primary);
}

.admin-table th {
  padding: var(--space-4);
  text-align: left;
  font-weight: var(--font-semibold);
  color: var(--color-white);
  font-size: var(--text-sm);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.admin-table td {
  padding: var(--space-4);
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-primary);
}

.admin-table tbody tr {
  transition: var(--transition-fast);
}

.admin-table tbody tr:hover {
  background: var(--gradient-purple-soft);
  transform: translateX(2px);
}

.text-center {
  text-align: center;
}

/* Role Select */
.role-select {
  padding: var(--space-2);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  background: var(--bg-card);
  color: var(--text-primary);
  cursor: pointer;
  transition: var(--transition-fast);
}

.role-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 2px var(--shadow-focus-purple);
}

/* Status Badge */
.status-badge {
  display: inline-block;
  padding: var(--space-1) var(--space-2-5);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.status-badge.active {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.status-badge.inactive {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

/* Action Buttons */
.btn-sm {
  padding: var(--space-1-5) var(--space-3);
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  transition: var(--transition-fast);
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
}

.btn-danger {
  background: var(--error-solid);
  color: var(--color-white);
}

.btn-danger:hover {
  background: var(--error-hover);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-red);
}

.btn-success {
  background: var(--success-solid);
  color: var(--color-white);
}

.btn-success:hover {
  background: var(--success-hover);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-green);
}

.btn-secondary {
  background: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-primary);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition-fast);
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  font-weight: var(--font-medium);
}

.btn-secondary:hover {
  background: var(--bg-tertiary);
  border-color: var(--accent-primary);
  transform: translateY(-2px);
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--space-6);
  padding: var(--space-4);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-primary);
}

.pagination-info {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
}

.pagination {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.page-info {
  font-size: var(--text-base);
  color: var(--text-primary);
  font-weight: var(--font-medium);
}

.page-btn {
  padding: var(--space-2) var(--space-4);
  border: 1px solid var(--border-primary);
  background: var(--bg-card);
  color: var(--text-primary);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-weight: var(--font-medium);
  transition: var(--transition-fast);
}

.page-btn:hover:not(:disabled) {
  background: var(--accent-primary);
  border-color: var(--accent-primary);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Animations */
.animate-fade-up {
  animation: fadeUp 0.5s ease-out;
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Responsive */
@media (max-width: 768px) {
  .admin-users {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-6);
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-4);
  }

  .page-title {
    font-size: var(--text-2xl);
  }

  .filters-section {
    padding: var(--space-4);
  }

  .filter-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    min-width: 100%;
  }

  .btn-reset {
    width: 100%;
    justify-content: center;
  }

  .bulk-action-bar {
    flex-direction: column;
    gap: var(--space-4);
    padding: var(--space-4);
  }

  .bulk-actions {
    flex-direction: column;
    width: 100%;
  }

  .bulk-action-select {
    width: 100%;
  }

  .bulk-actions .btn {
    width: 100%;
    justify-content: center;
  }

  .pagination-container {
    flex-direction: column;
    gap: var(--space-3);
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

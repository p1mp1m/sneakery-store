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
.admin-users{max-width:1400px;margin:0 auto;padding:2rem 1rem}
.page-header{
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom:2rem
}
.page-title{
  font-size:1.875rem;
  font-weight:700;
  color:var(--admin-text-primary);
  margin:0 0 .5rem;
  display:flex;
  align-items:center;
  gap:0.75rem
}
.page-title .material-icons{
  font-size:2rem;
  background:var(--gradient-primary);
  -webkit-background-clip:text;
  -webkit-text-fill-color:transparent;
  background-clip:text
}
.page-subtitle{color:var(--admin-text-secondary);margin:0}
.btn-export {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* Bulk Action Bar */
.bulk-action-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  animation: slideIn 0.3s ease-out;
}

.bulk-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1rem;
}

.bulk-info i {
  font-size: 24px;
}

.bulk-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.bulk-action-select {
  padding: 0.5rem 1rem;
  border: 2px solid white;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: 500;
  cursor: pointer;
  min-width: 200px;
}

.bulk-action-select option {
  background: #1e293b;
  color: white;
}

.bulk-actions .btn {
  border: 2px solid white;
  font-weight: 500;
}

.bulk-actions .btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.bulk-actions .btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #3b82f6;
}

@keyframes slideIn {
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
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-box {
  position: relative;
  margin-bottom: 1rem;
}

.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 1.25rem;
}

.search-input {
  width: 100%;
  padding: 0.75rem 3rem 0.75rem 3rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9375rem;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.clear-btn {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  padding: 0.375rem;
  border: none;
  background: none;
  cursor: pointer;
  color: #94a3b8;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.clear-btn:hover {
  background: #f1f5f9;
  color: #475569;
}

.filter-controls {
  display: flex;
  gap: 1rem;
  align-items: flex-end;
  flex-wrap: wrap;
}

.filter-group {
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #475569;
  margin-bottom: 0.5rem;
}

.filter-select {
  width: 100%;
  padding: 0.625rem 0.875rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9375rem;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.btn-reset {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1rem;
  border: 2px solid #e2e8f0;
  background: white;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: #64748b;
}

.btn-reset:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #475569;
}

.btn-reset i {
  font-size: 1.125rem;
}

.loading-container,.empty-state{background:#fff;border-radius:12px;padding:3rem;text-align:center;box-shadow:0 1px 3px rgba(0,0,0,.1)}.loading-spinner{width:48px;height:48px;border:4px solid #e2e8f0;border-top-color:#3b82f6;border-radius:50%;animation:spin 1s linear infinite;margin:0 auto 1rem}@keyframes spin{to{transform:rotate(360deg)}}.empty-state i{font-size:4rem;color:#cbd5e1;margin-bottom:1rem}.table-container{background:#fff;border-radius:12px;box-shadow:0 1px 3px rgba(0,0,0,.1);overflow:hidden}.table{width:100%;border-collapse:collapse}.table thead{background:#f8fafc;border-bottom:1px solid #e2e8f0}.table th{padding:1rem;text-align:left;font-weight:600;color:#475569;font-size:.875rem}.table td{padding:1rem;color:#1e293b;border-bottom:1px solid #f1f5f9}.table tbody tr:hover{background:#f8fafc}.text-center{text-align:center}.role-select{padding:.5rem;border:1px solid #d1d5db;border-radius:6px;font-size:.875rem}.status-badge{display:inline-block;padding:.25rem .625rem;border-radius:12px;font-size:.75rem;font-weight:500}.status-badge.active{background:#dcfce7;color:#166534}.status-badge.inactive{background:#fee2e2;color:#991b1b}.btn-sm{padding:.375rem .75rem;border:none;border-radius:6px;cursor:pointer;font-size:.75rem;font-weight:500}.btn-danger{background:#ef4444;color:#fff}.btn-success{background:#10b981;color:#fff}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>

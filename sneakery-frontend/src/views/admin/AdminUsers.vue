<template>
  <div class="admin-users">
    <div class="page-header">
      <h1 class="page-title">Quản lý người dùng</h1>
      <p class="page-subtitle">Quản lý phân quyền và trạng thái tài khoản</p>
    </div>

    <!-- Search & Filters -->
    <div class="filters-section">
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
    
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Đang tải...</p>
    </div>
    
    <div v-else-if="users.length === 0" class="empty-state">
        <i class="material-icons">people</i>
      <h3>Chưa có người dùng</h3>
          </div>

    <div v-else class="table-container">
      <table class="table">
        <thead>
          <tr>
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
      type="warning"
      title="Xác nhận thay đổi vai trò"
      :message="`Bạn có chắc chắn muốn thay đổi vai trò của ${userToUpdate?.fullName} từ \"${getRoleLabel(oldRole)}\" sang \"${getRoleLabel(newRole)}\"?`"
      description="Hành động này sẽ thay đổi quyền hạn của người dùng."
      confirm-text="Xác nhận"
      cancel-text="Hủy"
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

const adminStore = useAdminStore()
const users = ref([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)

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
.admin-users{max-width:1400px;margin:0 auto;padding:2rem 1rem}.page-header{margin-bottom:2rem}.page-title{font-size:1.875rem;font-weight:700;color:#1e293b;margin:0 0 .5rem}.page-subtitle{color:#64748b;margin:0}

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

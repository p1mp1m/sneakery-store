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

    <!-- Stats Grid -->
    <div class="stats-grid">
      <StatsCard
        icon="people"
        :value="users.length"
        label="Tổng người dùng"
        variant="primary"
      />
      <StatsCard
        icon="person"
        :value="activeUsersCount"
        label="Đang hoạt động"
        variant="success"
      />
      <StatsCard
        icon="lock"
        :value="inactiveUsersCount"
        label="Bị khóa"
        variant="warning"
      />
      <StatsCard
        icon="admin_panel_settings"
        :value="adminUsersCount"
        label="Quản trị viên"
        variant="info"
      />
    </div>

    <!-- Search & Filters -->
    <FilterBar
      v-model:search="filters.search"
      search-placeholder="Tìm theo email, tên hoặc số điện thoại..."
      @search="debounceSearch"
      @reset="resetFilters"
    >
      <template #filters>
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">admin_panel_settings</span>
            Vai trò
          </label>
          <select v-model="filters.role" @change="applyFilters" class="form-control">
            <option value="">Tất cả</option>
            <option value="USER">Người dùng</option>
            <option value="ADMIN">Quản trị viên</option>
            <option value="MODERATOR">Điều hành viên</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">lock</span>
            Trạng thái
          </label>
          <select v-model="filters.isActive" @change="applyFilters" class="form-control">
            <option value="">Tất cả</option>
            <option :value="true">Hoạt động</option>
            <option :value="false">Bị khóa</option>
          </select>
        </div>
      </template>
    </FilterBar>
    
    <!-- Loading State -->
    <LoadingState v-if="loading" />

    <!-- Empty State -->
    <EmptyState
      v-else-if="users.length === 0"
      icon="people"
      title="Chưa có người dùng"
      description="Chưa có người dùng nào trong hệ thống"
    />

    <!-- Bulk Action Bar for Users -->
    <BulkActions
      v-if="selectedUsers.length > 0"
      :selected-count="selectedUsers.length"
      :actions="bulkActions"
      v-model:selected-action="bulkAction"
      @execute="executeBulkAction"
      @clear="clearUserSelection"
    />

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
      <div class="pagination-controls">
        <button 
          :disabled="currentPage === 0" 
          @click="changePage(currentPage - 1)" 
          class="pagination-btn"
        >
          <span class="material-icons">chevron_left</span>
          Trước
        </button>
        <span class="page-info">Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
        <button 
          :disabled="currentPage >= totalPages - 1" 
          @click="changePage(currentPage + 1)" 
          class="pagination-btn"
        >
          Sau
          <span class="material-icons">chevron_right</span>
        </button>
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
import ConfirmDialog from '@/assets/components/common/ConfirmDialog.vue'
import StatsCard from '@/assets/components/admin/StatsCard.vue'
import FilterBar from '@/assets/components/admin/FilterBar.vue'
import LoadingState from '@/assets/components/admin/LoadingSkeleton.vue'
import EmptyState from '@/assets/components/admin/EmptyState.vue'
import BulkActions from '@/assets/components/admin/BulkActions.vue'
import { downloadCsv, downloadJson, exportToExcelStyled } from '@/utils/exportHelpers'

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

// Stats computed properties
const activeUsersCount = computed(() => users.value.filter(user => user.isActive).length)
const inactiveUsersCount = computed(() => users.value.filter(user => !user.isActive).length)
const adminUsersCount = computed(() => users.value.filter(user => user.role === 'ADMIN').length)

// Bulk actions configuration
const bulkActions = computed(() => [
  { value: 'lock', label: 'Khóa tài khoản', icon: 'lock' },
  { value: 'unlock', label: 'Mở khóa tài khoản', icon: 'lock_open' },
  { value: 'role-user', label: 'Đặt vai trò: USER', icon: 'person' },
  { value: 'role-admin', label: 'Đặt vai trò: ADMIN', icon: 'admin_panel_settings' }
])

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
    
    // Prepare filters for API
    const apiFilters = {
      search: filters.value.search || undefined,
      role: filters.value.role || undefined,
      isActive: filters.value.isActive !== '' ? filters.value.isActive : undefined
    }
    
    const result = await adminStore.fetchUsers(currentPage.value, pageSize.value, apiFilters)
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

// Export to CSV
const exportToExcel = () => {
  try {
    const exportData = users.value.map((user, index) => ({
      'STT': index + 1,
      'ID': user.id,
      'Họ và tên': user.fullName || 'N/A',
      'Email': user.email || 'N/A',
      'Số điện thoại': user.phoneNumber || 'N/A',
      'Vai trò': getRoleLabel(user.role),
      'Trạng thái': user.isActive ? 'Hoạt động' : 'Bị khóa'
    }))

    if (exportData.length === 0) {
      ElMessage.warning('Không có dữ liệu để xuất')
      return
    }

    // Export to styled Excel file
    exportToExcelStyled(exportData, 'nguoi-dung.xlsx', 'Người dùng')
    ElMessage.success(`Đã export ${exportData.length} người dùng thành công!`)
  } catch (error) {
    console.error('Lỗi khi export:', error)
    ElMessage.error('Không thể export dữ liệu. Vui lòng thử lại!')
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
/* ═══════════════════════════════════════════════════════════════════════
   ADMIN USERS - USER-SPECIFIC STYLES ONLY
   All layout, tables, buttons, pagination use Design System v2.0 global classes
   ═══════════════════════════════════════════════════════════════════════ */

/* Header, bulk actions, filters, tables, loading/empty states all use global Design System v2.0 classes */

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

/* Status badges, buttons, pagination, modals, animations, responsive all use global Design System v2.0 classes */
</style>

<template>
  <div class="max-w-[1600px] mx-auto w-full p-3 sm:p-4 space-y-3 sm:space-y-4">
    <!-- Page Header -->
    <div class="p-3 sm:p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-3 sm:gap-4">
        <div>
          <h1 class="text-base sm:text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">people</i>
            Quản lý người dùng
          </h1>
          <p class="text-xs sm:text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý phân quyền và trạng thái tài khoản</p>
        </div>
        <div class="flex items-center gap-2 flex-wrap">
          <button @click="exportToExcel" class="flex items-center gap-1.5 sm:gap-2 px-3 sm:px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-xs sm:text-sm font-medium">
            <i class="material-icons text-base">download</i>
            <span class="hidden sm:inline">Export Excel</span>
          </button>
          <button @click="openCreateModal" class="flex items-center gap-1.5 sm:gap-2 px-3 sm:px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-xs sm:text-sm font-medium shadow-sm">
            <i class="material-icons text-base">add</i>
            <span class="hidden sm:inline">Thêm người dùng</span>
            <span class="sm:hidden">Thêm</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">people</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ users.length }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng người dùng</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">person</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ activeUsersCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đang hoạt động</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">lock</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ inactiveUsersCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Bị khóa</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">admin_panel_settings</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ adminUsersCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Quản trị viên</p>
        </div>
      </div>
    </div>

    <!-- Search & Filters -->
    <FilterBar
      v-model:search="filters.search"
      search-placeholder="Tìm theo email, tên hoặc số điện thoại..."
      @search="debounceSearch"
      @reset="resetFilters"
    >
      <template #filters>
        <div class="flex flex-col gap-2">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">admin_panel_settings</i>
            Vai trò
          </label>
          <select v-model="filters.role" @change="applyFilters" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
            <option value="">Tất cả</option>
            <option value="USER">Người dùng</option>
            <option value="ADMIN">Quản trị viên</option>
            <option value="MODERATOR">Điều hành viên</option>
          </select>
        </div>

        <div class="flex flex-col gap-2">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">lock</i>
            Trạng thái
          </label>
          <select v-model="filters.isActive" @change="applyFilters" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
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
    <div v-else class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-10">
                <input 
                  type="checkbox" 
                  :checked="isAllUsersSelected"
                  @change="toggleSelectAllUsers"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Họ tên</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Email</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Số điện thoại</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Vai trò</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50 dark:hover:bg-gray-700/30 transition-colors">
              <td class="px-4 py-3">
                <input 
                  type="checkbox" 
                  :checked="selectedUsers.includes(user.id)"
                  @change="toggleSelectUser(user.id)"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </td>
              <td class="px-4 py-3 text-sm text-gray-900 dark:text-gray-100">{{ user.fullName }}</td>
              <td class="px-4 py-3 text-sm text-gray-900 dark:text-gray-100">{{ user.email }}</td>
              <td class="px-4 py-3 text-sm text-gray-500 dark:text-gray-400">{{ user.phoneNumber || 'N/A' }}</td>
              <td class="px-4 py-3">
                <select v-model="user.role" @change="confirmRoleChange(user, $event)" class="px-2 py-1 text-xs bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500">
                  <option value="USER">Người dùng</option>
                  <option value="ADMIN">Admin</option>
                  <option value="MODERATOR">Điều hành viên</option>
                </select>
              </td>
              <td class="px-4 py-3">
                <span class="px-2 py-1 text-xs font-medium rounded-full" :class="user.isActive ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'">
                  {{ user.isActive ? 'Hoạt động' : 'Bị khóa' }}
                </span>
              </td>
              <td class="px-4 py-3 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button @click="confirmToggleStatus(user)" class="px-3 py-1.5 text-xs font-medium rounded-lg transition-colors" :class="user.isActive ? 'bg-red-100 text-red-700 hover:bg-red-200 dark:bg-red-900/30 dark:text-red-400 dark:hover:bg-red-900/50' : 'bg-green-100 text-green-700 hover:bg-green-200 dark:bg-green-900/30 dark:text-green-400 dark:hover:bg-green-900/50'">
                    {{ user.isActive ? 'Khóa' : 'Mở khóa' }}
                  </button>
                  <button @click="confirmDelete(user)" class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors" title="Xóa người dùng">
                    <i class="material-icons text-base">delete</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="!loading && users.length > 0" class="flex flex-col sm:flex-row items-center justify-between gap-4 px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="text-sm text-gray-600 dark:text-gray-400">
        Hiển thị {{ (currentPage * pageSize) + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalItems) }} 
        trong tổng số {{ totalItems }} người dùng
      </div>
      <div class="flex items-center gap-2">
        <button 
          :disabled="currentPage === 0" 
          @click="changePage(currentPage - 1)" 
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <i class="material-icons text-base">chevron_left</i>
          Trước
        </button>
        <span class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300">Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
        <button 
          :disabled="currentPage >= totalPages - 1" 
          @click="changePage(currentPage + 1)" 
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Sau
          <i class="material-icons text-base">chevron_right</i>
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

    <!-- Delete Confirmation Dialog -->
    <ConfirmDialog
      v-model="showDeleteConfirm"
      type="danger"
      title="Xác nhận xóa người dùng"
      :message="`Bạn có chắc chắn muốn xóa người dùng '${userToDelete?.fullName}' (${userToDelete?.email})?`"
      description="Hành động này không thể hoàn tác! Người dùng sẽ bị xóa khỏi hệ thống."
      confirm-text="Xóa người dùng"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="handleDelete"
    />

    <!-- Create User Modal -->
    <div v-if="showCreateModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeCreateModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-lg w-full border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">add</i>
            Thêm người dùng mới
          </h2>
          <button @click="closeCreateModal" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>

        <div class="p-4 space-y-4">
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Email *</label>
            <input 
              v-model="newUser.email"
              type="email" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="user@example.com"
            />
            <span v-if="formErrors.email" class="text-xs text-red-600 dark:text-red-400">{{ formErrors.email }}</span>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Mật khẩu *</label>
            <input 
              v-model="newUser.password"
              type="password" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Tối thiểu 6 ký tự"
            />
            <span v-if="formErrors.password" class="text-xs text-red-600 dark:text-red-400">{{ formErrors.password }}</span>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Họ tên *</label>
            <input 
              v-model="newUser.fullName"
              type="text" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Nguyễn Văn A"
            />
            <span v-if="formErrors.fullName" class="text-xs text-red-600 dark:text-red-400">{{ formErrors.fullName }}</span>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số điện thoại</label>
            <input 
              v-model="newUser.phoneNumber"
              type="tel" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="0123456789"
            />
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Vai trò *</label>
            <select v-model="newUser.role" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="USER">Người dùng</option>
              <option value="ADMIN">Quản trị viên</option>
              <option value="MODERATOR">Điều hành viên</option>
            </select>
          </div>

          <div class="flex items-center gap-2">
            <input
              type="checkbox"
              id="isActiveNew"
              v-model="newUser.isActive"
              class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
            />
            <label for="isActiveNew" class="text-sm text-gray-700 dark:text-gray-300">
              Kích hoạt tài khoản ngay
            </label>
          </div>
        </div>

        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="closeCreateModal" class="px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
            Hủy
          </button>
          <button 
            @click="handleCreateUser" 
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="creating"
          >
            <i class="material-icons text-base" :class="{ 'animate-spin': creating }">{{ creating ? 'hourglass_empty' : 'save' }}</i>
            {{ creating ? 'Đang tạo...' : 'Tạo người dùng' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import notificationService from '@/utils/notificationService'
import { exportToExcelStyled } from '@/utils/exportHelpers'
import ConfirmDialog from '@/assets/components/common/ConfirmDialog.vue'
import FilterBar from '@/assets/components/admin/FilterBar.vue'
import LoadingState from '@/assets/components/admin/LoadingSkeleton.vue'
import EmptyState from '@/assets/components/admin/EmptyState.vue'
import BulkActions from '@/assets/components/admin/BulkActions.vue'
import logger from '@/utils/logger'

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

// Delete confirmation
const showDeleteConfirm = ref(false)
const userToDelete = ref(null)
const deleting = ref(false)

// Create user modal
const showCreateModal = ref(false)
const creating = ref(false)
const newUser = ref({
  email: '',
  password: '',
  fullName: '',
  phoneNumber: '',
  role: 'USER',
  isActive: true
})
const formErrors = ref({})

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
    notificationService.warning('Cảnh báo','Vui lòng chọn hành động!')
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
    
    notificationService.success('Thành công', `Đã ${actionMap[bulkAction.value]} ${selectedUsers.value.length} người dùng thành công!`, { duration: 3000 })
    
    // Clear selection and refresh list
    selectedUsers.value = []
    bulkAction.value = ''
    await fetchUsers()
  } catch (error) {
    logger.error('Lỗi khi thực hiện hàng loạt:', error)
    notificationService.apiError(error, 'Có lỗi xảy ra khi thực hiện hành động')
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
    logger.error('Lỗi khi tải danh sách người dùng:', error)
    notificationService.apiError(error, 'Không thể tải danh sách người dùng')
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
      notificationService.warning('Cảnh báo','Không có dữ liệu để xuất')
      return
    }

    // Export to styled Excel file
    exportToExcelStyled(exportData, 'nguoi-dung.xlsx', 'Người dùng')
    notificationService.success('Thành công',`Đã export ${exportData.length} người dùng thành công!`)
  } catch (error) {
    logger.error('Lỗi khi export:', error)
    notificationService.apiError(error, 'Không thể export dữ liệu')
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
    notificationService.success('Thành công',`Đã cập nhật vai trò của ${userToUpdate.value.fullName} thành công!`)
    
    userToUpdate.value._originalRole = newRole.value
    showRoleConfirm.value = false
  } catch (error) {
    logger.error('Lỗi khi cập nhật vai trò:', error)
    notificationService.apiError(error, 'Không thể cập nhật vai trò')
    
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
    notificationService.success('Thành công',`Đã ${newStatus ? 'mở khóa' : 'khóa'} tài khoản ${userToToggle.value.fullName} thành công!`)
    
    showStatusConfirm.value = false
  } catch (error) {
    logger.error('Lỗi khi cập nhật trạng thái:', error)
    notificationService.apiError(error, 'Không thể cập nhật trạng thái')
  } finally {
    updating.value = false
  }
}

// Delete user
const confirmDelete = (user) => {
  userToDelete.value = user
  showDeleteConfirm.value = true
}

const handleDelete = async () => {
  try {
    deleting.value = true
    await adminStore.deleteUser(userToDelete.value.id)
    notificationService.success('Thành công',`Đã xóa người dùng "${userToDelete.value.fullName}" thành công!`)
    showDeleteConfirm.value = false
    userToDelete.value = null
    await fetchUsers()
  } catch (error) {
    logger.error('Lỗi khi xóa người dùng:', error)
    notificationService.apiError(error, 'Không thể xóa người dùng')
  } finally {
    deleting.value = false
  }
}

// Create user
const openCreateModal = () => {
  newUser.value = {
    email: '',
    password: '',
    fullName: '',
    phoneNumber: '',
    role: 'USER',
    isActive: true
  }
  formErrors.value = {}
  showCreateModal.value = true
}

const closeCreateModal = () => {
  showCreateModal.value = false
  newUser.value = {
    email: '',
    password: '',
    fullName: '',
    phoneNumber: '',
    role: 'USER',
    isActive: true
  }
  formErrors.value = {}
}

const validateCreateUser = () => {
  formErrors.value = {}
  
  if (!newUser.value.email || !newUser.value.email.trim()) {
    formErrors.value.email = 'Email không được để trống'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(newUser.value.email)) {
    formErrors.value.email = 'Email không hợp lệ'
  }
  
  if (!newUser.value.password || !newUser.value.password.trim()) {
    formErrors.value.password = 'Mật khẩu không được để trống'
  } else if (newUser.value.password.length < 6) {
    formErrors.value.password = 'Mật khẩu phải có ít nhất 6 ký tự'
  }
  
  if (!newUser.value.fullName || !newUser.value.fullName.trim()) {
    formErrors.value.fullName = 'Họ tên không được để trống'
  } else if (newUser.value.fullName.trim().length < 2) {
    formErrors.value.fullName = 'Họ tên phải có ít nhất 2 ký tự'
  }
  
  return Object.keys(formErrors.value).length === 0
}

const handleCreateUser = async () => {
  if (!validateCreateUser()) {
    notificationService.warning('Cảnh báo','Vui lòng kiểm tra lại thông tin form!')
    return
  }

  try {
    creating.value = true
    await adminStore.createUser(newUser.value)
    notificationService.success('Thành công',`Đã tạo người dùng "${newUser.value.fullName}" thành công!`)
    closeCreateModal()
    await fetchUsers()
  } catch (error) {
    logger.error('Lỗi khi tạo người dùng:', error)
    notificationService.apiError(error, 'Không thể tạo người dùng')
    
    // Hiển thị lỗi cụ thể nếu có
    if (error?.response?.data?.validationErrors) {
      const validationErrors = error.response.data.validationErrors
      Object.keys(validationErrors).forEach(key => {
        formErrors.value[key] = validationErrors[key][0]
      })
    }
  } finally {
    creating.value = false
  }
}

onMounted(() => {
  fetchUsers()
})
</script>




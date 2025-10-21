<template>
  <div class="admin-users">
    <div class="page-header">
      <h1 class="page-title">Quản lý người dùng</h1>
      <p class="page-subtitle">Quản lý phân quyền và trạng thái tài khoản</p>
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
              <select v-model="user.role" @change="updateUserRole(user)" class="role-select">
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
              <button @click="toggleUserStatus(user)" class="btn-sm" :class="user.isActive ? 'btn-danger' : 'btn-success'">
                {{ user.isActive ? 'Khóa' : 'Mở khóa' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'

const adminStore = useAdminStore()
const users = ref([])
const loading = ref(false)

const fetchUsers = async () => {
  try {
    loading.value = true
    const result = await adminStore.fetchUsers()
    users.value = result.content || result || []
  } catch (error) {
    console.error('Lỗi khi tải danh sách người dùng:', error)
  } finally {
    loading.value = false
  }
}

const updateUserRole = async (user) => {
  try {
    await adminStore.updateUserRole(user.id, user.role)
    alert('Cập nhật vai trò thành công!')
  } catch (error) {
    console.error('Lỗi khi cập nhật vai trò:', error)
    alert('Không thể cập nhật vai trò!')
    fetchUsers()
  }
}

const toggleUserStatus = async (user) => {
  try {
    await adminStore.updateUserStatus(user.id, !user.isActive)
    user.isActive = !user.isActive
    alert(user.isActive ? 'Mở khóa tài khoản thành công!' : 'Khóa tài khoản thành công!')
  } catch (error) {
    console.error('Lỗi khi cập nhật trạng thái:', error)
    alert('Không thể cập nhật trạng thái!')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-users{max-width:1400px;margin:0 auto}.page-header{margin-bottom:2rem}.page-title{font-size:1.875rem;font-weight:700;color:#1e293b;margin:0 0 .5rem}.page-subtitle{color:#64748b;margin:0}.loading-container,.empty-state{background:#fff;border-radius:12px;padding:3rem;text-align:center;box-shadow:0 1px 3px rgba(0,0,0,.1)}.loading-spinner{width:48px;height:48px;border:4px solid #e2e8f0;border-top-color:#3b82f6;border-radius:50%;animation:spin 1s linear infinite;margin:0 auto 1rem}@keyframes spin{to{transform:rotate(360deg)}}.empty-state i{font-size:4rem;color:#cbd5e1;margin-bottom:1rem}.table-container{background:#fff;border-radius:12px;box-shadow:0 1px 3px rgba(0,0,0,.1);overflow:hidden}.table{width:100%;border-collapse:collapse}.table thead{background:#f8fafc;border-bottom:1px solid #e2e8f0}.table th{padding:1rem;text-align:left;font-weight:600;color:#475569;font-size:.875rem}.table td{padding:1rem;color:#1e293b;border-bottom:1px solid #f1f5f9}.table tbody tr:hover{background:#f8fafc}.text-center{text-align:center}.role-select{padding:.5rem;border:1px solid #d1d5db;border-radius:6px;font-size:.875rem}.status-badge{display:inline-block;padding:.25rem .625rem;border-radius:12px;font-size:.75rem;font-weight:500}.status-badge.active{background:#dcfce7;color:#166534}.status-badge.inactive{background:#fee2e2;color:#991b1b}.btn-sm{padding:.375rem .75rem;border:none;border-radius:6px;cursor:pointer;font-size:.75rem;font-weight:500}.btn-danger{background:#ef4444;color:#fff}.btn-success{background:#10b981;color:#fff}
</style>

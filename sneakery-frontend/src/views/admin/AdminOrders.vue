<template>
  <div class="admin-orders">
    <div class="page-header">
      <div>
      <h1 class="page-title">Quản lý đơn hàng</h1>
        <p class="page-subtitle">Theo dõi và cập nhật trạng thái đơn hàng</p>
      </div>
    </div>

    <!-- Search & Filters -->
    <div class="filters-section">
      <div class="search-box">
        <i class="material-icons search-icon">search</i>
        <input
          v-model="filters.search"
          @input="debounceSearch"
          type="text"
          placeholder="Tìm theo mã đơn, tên hoặc email khách hàng..."
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
          <label>Trạng thái</label>
          <select v-model="filters.status" @change="applyFilters" class="filter-select">
            <option value="">Tất cả</option>
            <option value="Pending">Chờ xử lý</option>
            <option value="Processing">Đang xử lý</option>
            <option value="Shipped">Đã gửi hàng</option>
            <option value="Completed">Hoàn thành</option>
            <option value="Cancelled">Đã hủy</option>
          </select>
        </div>

        <button @click="resetFilters" class="btn-reset" title="Xóa tất cả bộ lọc">
          <i class="material-icons">refresh</i>
          Reset
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách đơn hàng...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="orders.length === 0" class="empty-state">
        <i class="material-icons">shopping_cart</i>
      <h3>Chưa có đơn hàng nào</h3>
    </div>

    <!-- Orders List -->
    <div v-else class="table-container">
      <table class="table">
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Khách hàng</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
            <th>Ngày đặt</th>
            <th class="text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td><code>{{ order.id }}</code></td>
            <td>
              <div>{{ order.customerName }}</div>
              <div class="text-sm text-muted">{{ order.customerEmail }}</div>
            </td>
            <td class="fw-bold">{{ formatCurrency(order.totalAmount) }}</td>
            <td>
              <select 
                v-model="order.status" 
                @change="confirmStatusChange(order, $event)"
                class="status-select"
                :class="`status-${order.status?.toLowerCase()}`"
              >
                <option value="Pending">Chờ xử lý</option>
                <option value="Processing">Đang xử lý</option>
                <option value="Shipped">Đã gửi hàng</option>
                <option value="Completed">Hoàn thành</option>
                <option value="Cancelled">Đã hủy</option>
              </select>
            </td>
            <td>{{ formatDate(order.createdAt) }}</td>
            <td class="text-center">
              <button @click="viewOrderDetail(order)" class="btn-icon" title="Xem chi tiết">
            <i class="material-icons">visibility</i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
          </div>

    <!-- Pagination -->
    <div v-if="!loading && orders.length > 0" class="pagination-container">
      <div class="pagination-info">
        Hiển thị {{ (currentPage * pageSize) + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalItems) }} 
        trong tổng số {{ totalItems }} đơn hàng
          </div>
      <div class="pagination">
        <button :disabled="currentPage === 0" @click="changePage(currentPage - 1)" class="page-btn">Trước</button>
        <span class="page-info">Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
        <button :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)" class="page-btn">Sau</button>
          </div>
        </div>

    <!-- Order Detail Modal -->
    <div v-if="showDetailModal" class="modal-overlay" @click="showDetailModal = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">Chi tiết đơn hàng #{{ selectedOrder?.id }}</h2>
          <button @click="showDetailModal = false" class="modal-close">
            <i class="material-icons">close</i>
          </button>
          </div>
        <div class="modal-body">
          <p class="text-muted">Xem chi tiết đơn hàng (API chi tiết sẽ được tích hợp sau)</p>
          </div>
        <div class="modal-footer">
          <button @click="showDetailModal = false" class="btn btn-secondary">Đóng</button>
      </div>
    </div>
    </div>

    <!-- Status Change Confirmation Dialog -->
    <ConfirmDialog
      v-model="showStatusConfirm"
      type="warning"
      title="Xác nhận thay đổi trạng thái"
      :message="`Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng #${orderToUpdate?.id} từ \"${getStatusLabel(oldStatus)}\" sang \"${getStatusLabel(newStatus)}\"?`"
      description="Hành động này sẽ cập nhật trạng thái đơn hàng."
      confirm-text="Xác nhận"
      cancel-text="Hủy"
      :loading="updating"
      @confirm="handleStatusUpdate"
      @cancel="handleCancelStatusChange"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const adminStore = useAdminStore()

const orders = ref([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)
const showDetailModal = ref(false)
const selectedOrder = ref(null)

// Search & Filter state
const filters = ref({
  search: '',
  status: ''
})
let searchTimeout = null

// Status change confirmation
const showStatusConfirm = ref(false)
const orderToUpdate = ref(null)
const oldStatus = ref('')
const newStatus = ref('')
const updating = ref(false)

const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

const fetchOrders = async () => {
  try {
    loading.value = true
    const result = await adminStore.fetchOrders(currentPage.value, pageSize.value, filters.value)
    orders.value = result.content || []
    totalItems.value = result.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách đơn hàng:', error)
    ElMessage.error('Không thể tải danh sách đơn hàng.')
  } finally {
    loading.value = false
  }
}

const debounceSearch = () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 0 // Reset về trang đầu khi search
    fetchOrders()
  }, 500) // Debounce 500ms
}

const clearSearch = () => {
  filters.value.search = ''
  currentPage.value = 0
  fetchOrders()
}

const applyFilters = () => {
  currentPage.value = 0 // Reset về trang đầu khi filter
  fetchOrders()
}

const resetFilters = () => {
  filters.value.search = ''
  filters.value.status = ''
  currentPage.value = 0
  fetchOrders()
}

const confirmStatusChange = (order, event) => {
  // Get the old and new status
  const select = event.target
  oldStatus.value = order._originalStatus || order.status
  newStatus.value = select.value
  
  // If no change, do nothing
  if (oldStatus.value === newStatus.value) {
    return
  }
  
  // Store order reference and original status
  orderToUpdate.value = order
  if (!order._originalStatus) {
    order._originalStatus = oldStatus.value
  }
  
  // Show confirmation dialog
  showStatusConfirm.value = true
}

const handleStatusUpdate = async () => {
  try {
    updating.value = true
    await adminStore.updateOrderStatus(orderToUpdate.value.id, newStatus.value)
    ElMessage.success(`Đã cập nhật trạng thái đơn hàng #${orderToUpdate.value.id} thành công!`)
    
    // Update the original status
    orderToUpdate.value._originalStatus = newStatus.value
    
    showStatusConfirm.value = false
  } catch (error) {
    console.error('Lỗi khi cập nhật trạng thái:', error)
    ElMessage.error('Không thể cập nhật trạng thái. Vui lòng thử lại!')
    
    // Restore old status on error
    orderToUpdate.value.status = oldStatus.value
  } finally {
    updating.value = false
  }
}

const handleCancelStatusChange = () => {
  // Restore the old status
  if (orderToUpdate.value) {
    orderToUpdate.value.status = oldStatus.value
  }
  showStatusConfirm.value = false
}

const getStatusLabel = (status) => {
  const labels = {
    'Pending': 'Chờ xử lý',
    'Processing': 'Đang xử lý',
    'Shipped': 'Đã gửi hàng',
    'Completed': 'Hoàn thành',
    'Cancelled': 'Đã hủy'
  }
  return labels[status] || status
}

const viewOrderDetail = (order) => {
  selectedOrder.value = order
  showDetailModal.value = true
}

const changePage = (page) => {
  currentPage.value = page
  fetchOrders()
}

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('vi-VN')
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
/* Reuse common styles */
.admin-orders {max-width: 1400px;margin: 0 auto;padding: 2rem 1rem;}
.page-header {margin-bottom: 2rem;}
.page-title {font-size: 1.875rem;font-weight: 700;color: #1e293b;margin: 0 0 0.5rem 0;}
.page-subtitle {color: #64748b;margin: 0;}

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
.loading-container,.empty-state {background: white;border-radius: 12px;padding: 3rem;text-align: center;box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);}
.loading-spinner {width: 48px;height: 48px;border: 4px solid #e2e8f0;border-top-color: #3b82f6;border-radius: 50%;animation: spin 1s linear infinite;margin: 0 auto 1rem;}
@keyframes spin {to { transform: rotate(360deg); }}
.empty-state i {font-size: 4rem;color: #cbd5e1;margin-bottom: 1rem;}
.table-container {background: white;border-radius: 12px;box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);overflow: hidden;}
.table {width: 100%;border-collapse: collapse;}
.table thead {background: #f8fafc;border-bottom: 1px solid #e2e8f0;}
.table th {padding: 1rem;text-align: left;font-weight: 600;color: #475569;font-size: 0.875rem;}
.table td {padding: 1rem;color: #1e293b;border-bottom: 1px solid #f1f5f9;}
.table tbody tr:hover {background: #f8fafc;}
.text-center {text-align: center;}
.text-sm {font-size: 0.875rem;}
.text-muted {color: #64748b;}
.fw-bold {font-weight: 600;}
code {background: #f1f5f9;padding: 0.25rem 0.5rem;border-radius: 4px;font-size: 0.875rem;font-family: monospace;}
.status-select {padding: 0.5rem;border: 1px solid #d1d5db;border-radius: 6px;font-size: 0.875rem;font-weight: 500;}
.status-select.status-pending {background: #fef3c7;color: #92400e;}
.status-select.status-processing {background: #dbeafe;color: #1e40af;}
.status-select.status-shipped {background: #e0e7ff;color: #3730a3;}
.status-select.status-completed {background: #dcfce7;color: #166534;}
.status-select.status-cancelled {background: #fee2e2;color: #991b1b;}
.btn-icon {padding: 0.375rem;border: 1px solid #d1d5db;background: white;border-radius: 6px;cursor: pointer;}
.btn-icon:hover {background: #f8fafc;border-color: #3b82f6;color: #3b82f6;}
.pagination-container {display: flex;justify-content: space-between;align-items: center;margin-top: 1.5rem;padding: 1rem;background: white;border-radius: 8px;}
.pagination {display: flex;align-items: center;gap: 1rem;}
.page-btn {padding: 0.5rem 1rem;border: 1px solid #d1d5db;background: white;border-radius: 6px;cursor: pointer;}
.page-btn:disabled {opacity: 0.5;cursor: not-allowed;}
.modal-overlay {position: fixed;top: 0;left: 0;right: 0;bottom: 0;background: rgba(0, 0, 0, 0.5);display: flex;align-items: center;justify-content: center;z-index: 1000;}
.modal {background: white;border-radius: 12px;max-width: 600px;width: 90%;box-shadow: 0 20px 25px rgba(0, 0, 0, 0.15);}
.modal-header {padding: 1.5rem;border-bottom: 1px solid #e2e8f0;display: flex;justify-content: space-between;align-items: center;}
.modal-title {font-size: 1.25rem;font-weight: 600;margin: 0;}
.modal-close {width: 32px;height: 32px;border: none;background: none;cursor: pointer;border-radius: 6px;}
.modal-body {padding: 1.5rem;}
.modal-footer {padding: 1.5rem;border-top: 1px solid #e2e8f0;display: flex;gap: 0.75rem;justify-content: flex-end;}
.btn {padding: 0.625rem 1.25rem;border: none;border-radius: 6px;cursor: pointer;font-weight: 500;}
.btn-secondary {background: #f1f5f9;color: #475569;}
</style>

<template>
  <div class="admin-page admin-orders">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div>
        <h1 class="page-title">
          <i class="material-icons">shopping_bag</i>
          Quản lý đơn hàng
        </h1>
        <p class="page-subtitle">Theo dõi và cập nhật trạng thái đơn hàng</p>
      </div>
      <button @click="exportToExcel" class="btn btn-secondary btn-export">
        <i class="material-icons">download</i>
        Export Excel
      </button>
    </div>

    <!-- Search & Filters -->
    <div class="filters-section animate-fade-up">
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
    <div v-if="loading" class="loading-container animate-fade-in">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách đơn hàng...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="orders.length === 0" class="empty-state animate-fade-up">
      <i class="material-icons">shopping_cart</i>
      <h3>Chưa có đơn hàng nào</h3>
    </div>

    <!-- Bulk Action Bar for Orders -->
    <div v-if="selectedOrders.length > 0" class="bulk-action-bar">
      <div class="bulk-info">
        <i class="material-icons">check_circle</i>
        Đã chọn <strong>{{ selectedOrders.length }}</strong> đơn hàng
      </div>
      <div class="bulk-actions">
        <select v-model="bulkStatus" class="bulk-status-select">
          <option value="">-- Chọn trạng thái mới --</option>
          <option value="Pending">Chờ xử lý</option>
          <option value="Processing">Đang xử lý</option>
          <option value="Shipped">Đã gửi hàng</option>
          <option value="Completed">Hoàn thành</option>
          <option value="Cancelled">Đã hủy</option>
        </select>
        <button @click="bulkUpdateStatus" :disabled="!bulkStatus" class="btn btn-primary">
          <i class="material-icons">update</i>
          Cập nhật trạng thái
        </button>
        <button @click="clearOrderSelection" class="btn btn-secondary">
          <i class="material-icons">clear</i>
          Bỏ chọn
        </button>
      </div>
    </div>

    <!-- Orders List -->
    <div v-else class="table-card animate-fade-up">
      <table class="admin-table">
        <thead>
          <tr>
            <th style="width: 40px;">
              <input 
                type="checkbox" 
                :checked="isAllOrdersSelected"
                @change="toggleSelectAllOrders"
                class="checkbox-input"
              />
            </th>
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
            <td>
              <input 
                type="checkbox" 
                :checked="selectedOrders.includes(order.id)"
                @change="toggleSelectOrder(order.id)"
                class="checkbox-input"
              />
            </td>
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
          <button @click="handlePrintInvoice(selectedOrder)" class="btn btn-primary">
            <i class="material-icons">print</i>
            In hóa đơn
          </button>
          <button @click="showDetailModal = false" class="btn btn-secondary">Đóng</button>
      </div>
    </div>
    </div>

    <!-- Status Change Confirmation Dialog -->
    <ConfirmDialog
      v-model="showStatusConfirm"
      type="warning"
      title="Xác nhận thay đổi trạng thái"
      :message="`Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng #${orderToUpdate?.id} từ '${getStatusLabel(oldStatus)}' sang '${getStatusLabel(newStatus)}'?`"
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
import ConfirmDialog from '@/assets/components/common/ConfirmDialog.vue'
import * as XLSX from 'xlsx'
import { printInvoice } from '@/utils/pdfGenerator'
import { downloadCsv, prepareOrdersForExport } from '@/utils/exportHelpers'

const adminStore = useAdminStore()

const orders = ref([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)
const showDetailModal = ref(false)
const selectedOrder = ref(null)

// Bulk selection state
const selectedOrders = ref([])
const bulkStatus = ref('')

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

const isAllOrdersSelected = computed(() => {
  return orders.value.length > 0 && selectedOrders.value.length === orders.value.length
})

// Bulk selection methods
const toggleSelectOrder = (orderId) => {
  const index = selectedOrders.value.indexOf(orderId)
  if (index > -1) {
    selectedOrders.value.splice(index, 1)
  } else {
    selectedOrders.value.push(orderId)
  }
}

const toggleSelectAllOrders = () => {
  if (isAllOrdersSelected.value) {
    selectedOrders.value = []
  } else {
    selectedOrders.value = orders.value.map(o => o.id)
  }
}

const clearOrderSelection = () => {
  selectedOrders.value = []
  bulkStatus.value = ''
}

const bulkUpdateStatus = async () => {
  if (!bulkStatus.value) {
    ElMessage.warning('Vui lòng chọn trạng thái!')
    return
  }

  if (!confirm(`Bạn có chắc chắn muốn cập nhật ${selectedOrders.value.length} đơn hàng sang trạng thái "${getStatusLabel(bulkStatus.value)}"?`)) {
    return
  }

  try {
    loading.value = true
    
    // Update status for each order
    for (const orderId of selectedOrders.value) {
      await adminStore.updateOrderStatus(orderId, bulkStatus.value)
    }
    
    ElMessage.success({
      message: `Đã cập nhật ${selectedOrders.value.length} đơn hàng thành công!`,
      duration: 3000
    })
    
    // Clear selection and refresh list
    selectedOrders.value = []
    bulkStatus.value = ''
    await fetchOrders()
  } catch (error) {
    console.error('Lỗi khi cập nhật hàng loạt:', error)
    ElMessage.error({
      message: 'Có lỗi xảy ra khi cập nhật đơn hàng!',
      duration: 3000
    })
  } finally {
    loading.value = false
  }
}

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

// Export to Excel
const exportToExcel = () => {
  try {
    // Chuẩn bị data để export
    const exportData = orders.value.map((order, index) => ({
      'STT': index + 1,
      'Mã đơn hàng': `#${order.id}`,
      'Khách hàng': order.customerName || 'N/A',
      'Email': order.customerEmail || 'N/A',
      'Tổng tiền': new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.totalAmount),
      'Trạng thái': getStatusLabel(order.status),
      'Ngày đặt': new Date(order.createdAt).toLocaleString('vi-VN')
    }))

    // Tạo worksheet từ data
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    
    // Tạo workbook
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Đơn hàng')
    
    // Tạo tên file với timestamp
    const timestamp = new Date().toISOString().slice(0, 10)
    const filename = `don-hang_${timestamp}.xlsx`
    
    // Download file
    XLSX.writeFile(workbook, filename)
    
    ElMessage.success({
      message: `Đã export ${exportData.length} đơn hàng thành công!`,
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

const handlePrintInvoice = (order) => {
  if (!order) {
    ElMessage.warning('Không có thông tin đơn hàng để in')
    return
  }
  
  try {
    printInvoice(order)
    ElMessage.success('Đang mở cửa sổ in hóa đơn...')
  } catch (error) {
    console.error('Error printing invoice:', error)
    ElMessage.error('Không thể in hóa đơn. Vui lòng thử lại!')
  }
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
/* ===== LAYOUT ===== */
.admin-orders {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-4);
}

/* ===== PAGE HEADER ===== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
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

.btn-export {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

/* ===== BULK ACTION BAR ===== */
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
  animation: slideIn 0.3s ease-out;
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
  color: var(--color-white);
  font-weight: var(--font-bold);
}

.bulk-actions {
  display: flex;
  gap: var(--space-4);
  align-items: center;
}

.bulk-status-select {
  padding: var(--space-2) var(--space-4);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  backdrop-filter: blur(10px);
}

.bulk-status-select:hover {
  background: rgba(15, 23, 42, 0.8);
  border-color: var(--border-hover);
}

.bulk-status-select:focus {
  outline: none;
  background: rgba(15, 23, 42, 0.9);
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.bulk-status-select option {
  background: var(--bg-primary);
  color: var(--text-primary);
  padding: var(--space-3);
  font-weight: var(--font-medium);
}

.bulk-status-select option:hover {
  background: rgba(167, 139, 250, 0.15);
  color: var(--accent-primary);
}

.bulk-status-select option:checked {
  background: var(--accent-primary);
  color: var(--color-white);
  font-weight: var(--font-semibold);
}

.bulk-actions .btn {
  border: 2px solid rgba(255, 255, 255, 0.8);
  font-weight: var(--font-medium);
  background: rgba(255, 255, 255, 0.1);
  color: var(--color-white);
}

.bulk-actions .btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
  border-color: var(--color-white);
}

.bulk-actions .btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ===== CHECKBOX ===== */
.checkbox-input {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: var(--accent-primary);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.checkbox-input:hover {
  transform: scale(1.1);
  box-shadow: 0 0 0 2px rgba(167, 139, 250, 0.2);
}

.checkbox-input:checked {
  background: var(--gradient-primary);
  border-color: var(--accent-primary);
}

.checkbox-input:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
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
  background: var(--bg-card);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
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
  font-size: 1.25rem;
}

.search-input {
  width: 100%;
  padding: var(--space-4) var(--space-12) var(--space-4) var(--space-12);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  font-size: var(--text-base);
  color: var(--text-primary);
  background: var(--bg-primary);
  transition: all var(--transition-fast);
}

.search-input::placeholder {
  color: var(--text-muted);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: var(--bg-secondary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.clear-btn {
  position: absolute;
  right: var(--space-3);
  top: 50%;
  transform: translateY(-50%);
  padding: var(--space-2);
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  cursor: pointer;
  color: var(--text-tertiary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.clear-btn:hover {
  background: var(--error-bg);
  color: var(--error-solid);
  transform: translateY(-50%) scale(1.1);
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
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.filter-select {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  background: rgba(15, 23, 42, 0.6);
  cursor: pointer;
  transition: all var(--transition-fast);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.filter-select:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.8);
}

.filter-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.filter-select option {
  background: var(--bg-primary);
  color: var(--text-primary);
  padding: var(--space-3);
  font-weight: var(--font-medium);
}

.filter-select option:hover {
  background: rgba(167, 139, 250, 0.15);
  color: var(--accent-primary);
}

.filter-select option:checked {
  background: var(--accent-primary);
  color: var(--color-white);
  font-weight: var(--font-semibold);
}

.btn-reset {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  border: 2px solid var(--border-primary);
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  color: var(--text-secondary);
}

.btn-reset:hover {
  background: var(--bg-secondary);
  border-color: var(--border-medium);
  color: var(--text-primary);
  box-shadow: var(--shadow-sm);
}

.btn-reset i {
  font-size: 1.125rem;
}

/* ===== LOADING & EMPTY STATE ===== */
.loading-container,
.empty-state {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-12);
  text-align: center;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
}

.loading-container p,
.empty-state h3 {
  color: var(--text-primary);
  margin-top: var(--space-4);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--border-primary);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state i {
  font-size: 4rem;
  color: var(--text-muted);
  margin-bottom: var(--space-4);
}

/* ===== TABLE ===== */
.table-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  border: 1px solid var(--border-primary);
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table thead {
  background: var(--table-header-bg);
  border-bottom: 2px solid var(--border-primary);
}

/* Table headers use global admin-tables.css styles */

/* Table cells use global admin-tables.css styles */

.admin-table tbody tr {
  transition: var(--transition-fast);
}

.admin-table tbody tr:hover {
  background: var(--bg-secondary);
}

.admin-table tbody tr:last-child td {
  border-bottom: none;
}

/* ===== UTILITY CLASSES ===== */
.text-center {
  text-align: center;
}

.text-sm {
  font-size: var(--text-sm);
}

.text-muted {
  color: var(--text-muted);
}

.fw-bold {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

code {
  background: var(--bg-tertiary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  color: var(--accent-primary);
  font-weight: var(--font-medium);
}

/* ===== STATUS SELECT ===== */
.status-select {
  padding: var(--space-2) var(--space-3);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all var(--transition-fast);
  background: rgba(15, 23, 42, 0.6) !important;
  color: var(--text-primary) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.status-select:hover {
  transform: scale(1.02);
  background: rgba(15, 23, 42, 0.8) !important;
  border-color: var(--border-hover);
}

.status-select:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
  background: rgba(15, 23, 42, 0.9) !important;
  border-color: var(--accent-primary);
}

.status-select option {
  background: var(--bg-primary);
  color: var(--text-primary);
  padding: var(--space-3);
  font-weight: var(--font-medium);
  text-transform: uppercase;
}

.status-select option:hover {
  background: rgba(167, 139, 250, 0.15);
  color: var(--accent-primary);
}

.status-select option:checked {
  background: var(--accent-primary);
  color: var(--color-white);
  font-weight: var(--font-semibold);
}

/* Đồng nhất tất cả status select về màu tối */
.status-select.status-pending,
.status-select.status-processing,
.status-select.status-shipped,
.status-select.status-completed,
.status-select.status-cancelled {
  background: rgba(15, 23, 42, 0.6) !important;
  color: var(--text-primary) !important;
  border-color: var(--border-primary) !important;
}

.status-select.status-pending:hover,
.status-select.status-processing:hover,
.status-select.status-shipped:hover,
.status-select.status-completed:hover,
.status-select.status-cancelled:hover {
  background: rgba(15, 23, 42, 0.8) !important;
  border-color: var(--border-hover) !important;
  transform: scale(1.02);
}

.status-select.status-pending:focus,
.status-select.status-processing:focus,
.status-select.status-shipped:focus,
.status-select.status-completed:focus,
.status-select.status-cancelled:focus {
  background: rgba(15, 23, 42, 0.9) !important;
  border-color: var(--accent-primary) !important;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

/* Action buttons use global admin-tables.css styles */

/* ===== PAGINATION ===== */
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
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.pagination {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.page-info {
  color: var(--text-primary);
  font-weight: var(--font-medium);
  font-size: var(--text-sm);
}

.page-btn {
  padding: var(--space-3) var(--space-5);
  border: 2px solid var(--border-primary);
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  color: var(--text-primary);
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
}

.page-btn:hover:not(:disabled) {
  background: var(--gradient-primary);
  border-color: transparent;
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.page-btn:active:not(:disabled) {
  transform: translateY(0);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  color: var(--text-muted);
}

/* ===== MODAL ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-4);
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  max-width: 600px;
  width: 100%;
  box-shadow: var(--shadow-2xl);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  padding: var(--space-6);
  border-bottom: 2px solid var(--border-primary);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.modal-close {
  width: 36px;
  height: 36px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 50%;
  color: var(--text-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-solid);
  transform: rotate(90deg);
}

.modal-body {
  padding: var(--space-6);
  color: var(--text-secondary);
}

.modal-footer {
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
}

/* ===== BUTTONS ===== */
.btn {
  padding: var(--space-3) var(--space-6);
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  white-space: nowrap;
}

.btn .material-icons {
  font-size: 1.25rem;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--color-white);
  box-shadow: var(--shadow-md);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

.btn-secondary {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  border: 2px solid var(--border-primary);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--bg-secondary);
  border-color: var(--border-medium);
  box-shadow: var(--shadow-sm);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===== ANIMATIONS ===== */
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-fade-up {
  animation: fadeUp 0.5s ease-out;
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

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .admin-orders {
    padding: var(--space-4) var(--space-2);
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-4);
  }
  
  .bulk-action-bar {
    flex-direction: column;
    gap: var(--space-4);
  }
  
  .bulk-actions {
    width: 100%;
    flex-direction: column;
  }
  
  .bulk-status-select,
  .bulk-actions .btn {
    width: 100%;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: var(--space-3);
  }
}
</style>

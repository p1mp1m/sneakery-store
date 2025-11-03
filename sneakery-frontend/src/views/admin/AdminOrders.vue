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
      <div class="header-actions">
        <button @click="exportToExcel" class="btn btn-secondary btn-export">
          <i class="material-icons">download</i>
          Export Excel
        </button>
        <button @click="exportToPDF" class="btn btn-secondary btn-export">
          <i class="material-icons">picture_as_pdf</i>
          Export PDF
        </button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid animate-fade-up">
      <div class="stat-card stat-total">
        <div class="stat-icon">
          <i class="material-icons">shopping_bag</i>
        </div>
        <div class="stat-content">
          <div class="stat-label">Tổng đơn hàng</div>
          <div class="stat-value">{{ stats.totalOrders }}</div>
        </div>
      </div>
      <div class="stat-card stat-pending">
        <div class="stat-icon">
          <i class="material-icons">schedule</i>
        </div>
        <div class="stat-content">
          <div class="stat-label">Chờ xử lý</div>
          <div class="stat-value">{{ stats.pendingOrders }}</div>
        </div>
      </div>
      <div class="stat-card stat-processing">
        <div class="stat-icon">
          <i class="material-icons">refresh</i>
        </div>
        <div class="stat-content">
          <div class="stat-label">Đang xử lý</div>
          <div class="stat-value">{{ stats.processingOrders }}</div>
        </div>
      </div>
      <div class="stat-card stat-completed">
        <div class="stat-icon">
          <i class="material-icons">check_circle</i>
        </div>
        <div class="stat-content">
          <div class="stat-label">Hoàn thành</div>
          <div class="stat-value">{{ stats.completedOrders }}</div>
        </div>
      </div>
      <div class="stat-card stat-revenue">
        <div class="stat-icon">
          <i class="material-icons">attach_money</i>
        </div>
        <div class="stat-content">
          <div class="stat-label">Doanh thu hôm nay</div>
          <div class="stat-value">{{ formatCurrency(stats.todayRevenue) }}</div>
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
          <select v-model="filters.status" @change="applyFilters" class="form-control status-select">
            <option value="">Tất cả</option>
            <option value="Pending">Chờ xử lý</option>
            <option value="Processing">Đang xử lý</option>
            <option value="Shipped">Đã gửi hàng</option>
            <option value="Completed">Hoàn thành</option>
            <option value="Cancelled">Đã hủy</option>
          </select>
        </div>

        <div class="filter-group">
          <label>Từ ngày</label>
          <input 
            v-model="filters.startDate" 
            @change="applyFilters" 
            type="date" 
            class="form-control date-input"
          />
        </div>

        <div class="filter-group">
          <label>Đến ngày</label>
          <input 
            v-model="filters.endDate" 
            @change="applyFilters" 
            type="date" 
            class="form-control date-input"
          />
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
        <select v-model="bulkStatus" class="form-control status-select">
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
              <div class="action-buttons">
                <button @click="viewOrderDetail(order)" class="btn-icon btn-view" title="Xem chi tiết">
                  <i class="material-icons">visibility</i>
                </button>
                <button @click="handlePrintInvoice(order)" class="btn-icon btn-print" title="In hóa đơn">
                  <i class="material-icons">print</i>
                </button>
                <button 
                  v-if="order.status !== 'Cancelled'" 
                  @click="handleCancelOrder(order)" 
                  class="btn-icon btn-cancel" 
                  title="Hủy đơn hàng"
                >
                  <i class="material-icons">cancel</i>
                </button>
              </div>
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
      <div class="modal modal-large" @click.stop>
        <div class="modal-header">
          <div>
            <h2 class="modal-title">
              <i class="material-icons">receipt_long</i>
              Chi tiết đơn hàng #{{ selectedOrder?.id }}
            </h2>
            <div class="order-status-badge" :class="`status-${selectedOrder?.status?.toLowerCase()}`">
              {{ getStatusLabel(selectedOrder?.status) }}
            </div>
          </div>
          <button @click="showDetailModal = false" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>
        
        <div class="modal-body" v-if="selectedOrder">
          <!-- Customer Info -->
          <div class="detail-section">
            <h3 class="section-title">
              <i class="material-icons">person</i>
              Thông tin khách hàng
            </h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Tên khách hàng:</span>
                <span class="info-value">{{ selectedOrder.customerName }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Email:</span>
                <span class="info-value">{{ selectedOrder.customerEmail }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Ngày đặt:</span>
                <span class="info-value">{{ formatDateTime(selectedOrder.createdAt) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Tổng tiền:</span>
                <span class="info-value total-amount">{{ formatCurrency(selectedOrder.totalAmount) }}</span>
              </div>
            </div>
          </div>

          <!-- Shipping Address -->
          <div class="detail-section" v-if="selectedOrder.addressShipping">
            <h3 class="section-title">
              <i class="material-icons">local_shipping</i>
              Địa chỉ giao hàng
            </h3>
            <div class="address-box">
              <p><strong>{{ selectedOrder.addressShipping.recipientName }}</strong></p>
              <p>{{ selectedOrder.addressShipping.phone }}</p>
              <p>{{ selectedOrder.addressShipping.line1 }}</p>
              <p v-if="selectedOrder.addressShipping.line2">{{ selectedOrder.addressShipping.line2 }}</p>
              <p>{{ selectedOrder.addressShipping.ward }}, {{ selectedOrder.addressShipping.district }}, {{ selectedOrder.addressShipping.city }}</p>
              <p v-if="selectedOrder.addressShipping.postalCode">Mã bưu điện: {{ selectedOrder.addressShipping.postalCode }}</p>
            </div>
          </div>

          <!-- Order Items -->
          <div class="detail-section">
            <h3 class="section-title">
              <i class="material-icons">shopping_cart</i>
              Sản phẩm đã đặt
            </h3>
            <div class="order-items-table">
              <table class="items-table">
                <thead>
                  <tr>
                    <th>Sản phẩm</th>
                    <th>Size</th>
                    <th>Màu</th>
                    <th class="text-right">Số lượng</th>
                    <th class="text-right">Đơn giá</th>
                    <th class="text-right">Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in (selectedOrder.orderDetails || [])" :key="index">
                    <td>
                      <div class="product-info">
                        <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.productName" class="product-image" />
                        <div>
                          <div class="product-name">{{ item.productName }}</div>
                          <div class="product-brand">{{ item.brandName }}</div>
                        </div>
                      </div>
                    </td>
                    <td>{{ item.size }}</td>
                    <td>
                      <span class="color-badge" :style="{ backgroundColor: item.color }"></span>
                      {{ item.color }}
                    </td>
                    <td class="text-right">{{ item.quantity }}</td>
                    <td class="text-right">{{ formatCurrency(item.unitPrice) }}</td>
                    <td class="text-right fw-bold">{{ formatCurrency(item.totalPrice) }}</td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr class="total-row">
                    <td colspan="5" class="text-right"><strong>Tổng cộng:</strong></td>
                    <td class="text-right total-price">{{ formatCurrency(selectedOrder.totalAmount) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>

          <!-- Payment Info -->
          <div class="detail-section" v-if="selectedOrder.payment">
            <h3 class="section-title">
              <i class="material-icons">payment</i>
              Thông tin thanh toán
            </h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Phương thức:</span>
                <span class="info-value">{{ getPaymentMethodLabel(selectedOrder.payment.paymentMethod) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Trạng thái:</span>
                <span class="info-value payment-status" :class="`status-${selectedOrder.payment.status?.toLowerCase()}`">
                  {{ selectedOrder.payment.status }}
                </span>
              </div>
              <div class="info-item" v-if="selectedOrder.payment.paidAt">
                <span class="info-label">Ngày thanh toán:</span>
                <span class="info-value">{{ formatDateTime(selectedOrder.payment.paidAt) }}</span>
              </div>
            </div>
          </div>

          <!-- Status History -->
          <div class="detail-section" v-if="selectedOrder.statusHistories && selectedOrder.statusHistories.length > 0">
            <h3 class="section-title">
              <i class="material-icons">history</i>
              Lịch sử thay đổi trạng thái
            </h3>
            <div class="status-timeline">
              <div 
                v-for="(history, index) in selectedOrder.statusHistories" 
                :key="history.id" 
                class="timeline-item"
                :class="{ 'timeline-active': index === 0 }"
              >
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-status">{{ getStatusLabel(history.status) }}</div>
                  <div class="timeline-date">{{ formatDateTime(history.changedAt) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="handlePrintInvoice(selectedOrder)" class="btn btn-primary">
            <i class="material-icons">print</i>
            In hóa đơn
          </button>
          <button @click="exportOrderToPDF(selectedOrder)" class="btn btn-secondary">
            <i class="material-icons">picture_as_pdf</i>
            Export PDF
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
import AdminService from '@/services/adminService'

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
  status: '',
  startDate: '',
  endDate: ''
})
let searchTimeout = null

// Stats state
const stats = ref({
  totalOrders: 0,
  pendingOrders: 0,
  processingOrders: 0,
  completedOrders: 0,
  todayRevenue: 0
})

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
    
    // Prepare filters for API
    const apiFilters = {
      search: filters.value.search || undefined,
      status: filters.value.status || undefined,
      startDate: filters.value.startDate || undefined,
      endDate: filters.value.endDate || undefined
    }
    
    const result = await adminStore.fetchOrders(currentPage.value, pageSize.value, apiFilters)
    orders.value = result.content || []
    totalItems.value = result.totalElements || 0
    
    // Calculate stats
    calculateStats()
  } catch (error) {
    console.error('Lỗi khi tải danh sách đơn hàng:', error)
    ElMessage.error('Không thể tải danh sách đơn hàng.')
  } finally {
    loading.value = false
  }
}

const calculateStats = () => {
  const today = new Date().toISOString().split('T')[0]
  stats.value = {
    totalOrders: totalItems.value,
    pendingOrders: orders.value.filter(o => o.status === 'Pending').length,
    processingOrders: orders.value.filter(o => o.status === 'Processing').length,
    completedOrders: orders.value.filter(o => o.status === 'Completed').length,
    todayRevenue: orders.value
      .filter(o => o.createdAt && new Date(o.createdAt).toISOString().split('T')[0] === today)
      .reduce((sum, o) => sum + (o.totalAmount || 0), 0)
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
  filters.value.startDate = ''
  filters.value.endDate = ''
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


const viewOrderDetail = async (order) => {
  try {
    loading.value = true
    // Fetch full order details from API
    try {
      const orderDetail = await AdminService.getOrderById(order.id)
      if (orderDetail) {
        selectedOrder.value = orderDetail
      } else {
        selectedOrder.value = order
      }
    } catch (apiError) {
      console.warn('Không thể tải chi tiết từ API, sử dụng dữ liệu cơ bản:', apiError)
      selectedOrder.value = order
    }
    showDetailModal.value = true
  } catch (error) {
    console.error('Lỗi khi tải chi tiết đơn hàng:', error)
    selectedOrder.value = order
    showDetailModal.value = true
  } finally {
    loading.value = false
  }
}

const handleCancelOrder = async (order) => {
  if (!confirm(`Bạn có chắc chắn muốn hủy đơn hàng #${order.id}?`)) {
    return
  }
  
  try {
    await adminStore.updateOrderStatus(order.id, 'Cancelled')
    ElMessage.success('Đã hủy đơn hàng thành công!')
    await fetchOrders()
  } catch (error) {
    console.error('Lỗi khi hủy đơn hàng:', error)
    ElMessage.error('Không thể hủy đơn hàng. Vui lòng thử lại!')
  }
}

const exportOrderToPDF = (order) => {
  if (!order) {
    ElMessage.warning('Không có thông tin đơn hàng để export')
    return
  }
  
  try {
    handlePrintInvoice(order)
    ElMessage.success('Đang mở cửa sổ in hóa đơn...')
  } catch (error) {
    console.error('Error exporting to PDF:', error)
    ElMessage.error('Không thể export PDF. Vui lòng thử lại!')
  }
}

const exportToPDF = () => {
  ElMessage.info('Tính năng export PDF đang được phát triển...')
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

const formatDateTime = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getPaymentMethodLabel = (method) => {
  const labels = {
    'COD': 'Thanh toán khi nhận hàng',
    'BANK_TRANSFER': 'Chuyển khoản ngân hàng',
    'CREDIT_CARD': 'Thẻ tín dụng',
    'EWALLET': 'Ví điện tử'
  }
  return labels[method] || method
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
  margin-bottom: var(--space-6);
}

.header-actions {
  display: flex;
  gap: var(--space-3);
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
  transition: all 0.3s ease;
}

.btn-export:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(167, 139, 250, 0.3);
}

/* ===== STATS CARDS ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.stat-card {
  background: linear-gradient(135deg, var(--bg-card) 0%, rgba(167, 139, 250, 0.05) 100%);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-primary);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(167, 139, 250, 0.25);
  border-color: var(--accent-primary);
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.stat-total .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-pending .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-processing .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-completed .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-revenue .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
}

.stat-icon .material-icons {
  font-size: 1.75rem;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
  margin-bottom: var(--space-1);
}

.stat-value {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1.2;
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

.date-input {
  padding: var(--space-3);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  color: var(--text-primary);
  background: var(--bg-primary);
  transition: all var(--transition-fast);
  width: 100%;
}

.date-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: var(--bg-secondary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
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

.action-buttons {
  display: flex;
  gap: var(--space-2);
  justify-content: center;
  align-items: center;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(167, 139, 250, 0.1);
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(167, 139, 250, 0.2);
}

.btn-icon:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(167, 139, 250, 0.3);
}

.btn-view:hover {
  background: var(--gradient-primary);
  color: white;
  border-color: var(--accent-primary);
}

.btn-print:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

.btn-cancel:hover {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border-color: #f5576c;
}

.btn-icon .material-icons {
  font-size: 1.25rem;
}

/* ===== UTILITY CLASSES ===== */
.text-center {
  text-align: center;
}

.text-right {
  text-align: right;
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
  padding: 6px 10px;
  border: 1px solid var(--border-primary);
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  background: var(--bg-card);
  min-width: 120px;
  max-width: 140px;
  appearance: none;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='m6 8 4 4 4-4'/%3e%3c/svg%3e");
  background-position: right 6px center;
  background-repeat: no-repeat;
  background-size: 14px;
  padding-right: 28px;
  color: var(--text-primary);
}

/* Default color cho select không có status class */
.status-select:not([class*="status-"]) {
  color: var(--text-primary);
  border-color: var(--border-primary);
}

.status-select:hover {
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 2px rgba(139, 92, 246, 0.1);
}

.status-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 2px rgba(139, 92, 246, 0.15);
}

.status-select option {
  background: var(--bg-card);
  color: var(--text-primary);
  padding: 6px 10px;
  font-weight: 500;
  font-size: 13px;
}

/* Status Select Colors - Text màu tương ứng với trạng thái */
.status-select.status-pending {
  border-color: #f59e0b;
  color: #f59e0b;
}

.status-select.status-pending option {
  color: #f59e0b;
}

.status-select.status-processing {
  border-color: #3b82f6;
  color: #3b82f6;
}

.status-select.status-processing option {
  color: #3b82f6;
}

.status-select.status-shipped {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.status-select.status-shipped option {
  color: #8b5cf6;
}

.status-select.status-completed {
  border-color: #10b981;
  color: #10b981;
}

.status-select.status-completed option {
  color: #10b981;
}

.status-select.status-cancelled {
  border-color: #ef4444;
  color: #ef4444;
}

.status-select.status-cancelled option {
  color: #ef4444;
}

/* Hover effects cho từng trạng thái */
.status-select.status-pending:hover {
  border-color: #d97706;
  color: #d97706;
}

.status-select.status-processing:hover {
  border-color: #2563eb;
  color: #2563eb;
}

.status-select.status-shipped:hover {
  border-color: #7c3aed;
  color: #7c3aed;
}

.status-select.status-completed:hover {
  border-color: #059669;
  color: #059669;
}

.status-select.status-cancelled:hover {
  border-color: #dc2626;
  color: #dc2626;
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
  background: linear-gradient(135deg, var(--bg-card) 0%, rgba(167, 139, 250, 0.05) 100%);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(167, 139, 250, 0.1);
  animation: modalSlideIn 0.3s ease-out;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  overflow: hidden;
  position: relative;
}

.modal::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-primary);
  z-index: 1;
}

.modal-large {
  max-width: 900px;
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
  align-items: flex-start;
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.1) 0%, transparent 100%);
  position: relative;
  z-index: 0;
}

.modal-header > div {
  flex: 1;
}

.order-status-badge {
  display: inline-block;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  margin-top: var(--space-2);
}

.order-status-badge.status-pending {
  background: rgba(245, 158, 11, 0.2);
  color: #f59e0b;
  border: 1px solid rgba(245, 158, 11, 0.3);
}

.order-status-badge.status-processing {
  background: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.order-status-badge.status-shipped {
  background: rgba(139, 92, 246, 0.2);
  color: #8b5cf6;
  border: 1px solid rgba(139, 92, 246, 0.3);
}

.order-status-badge.status-completed {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.order-status-badge.status-cancelled {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.3);
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
  overflow-y: auto;
  background: rgba(167, 139, 250, 0.02);
  flex: 1;
}

.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-track {
  background: transparent;
}

.modal-body::-webkit-scrollbar-thumb {
  background: var(--border-primary);
  border-radius: 4px;
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background: var(--accent-primary);
}

.detail-section {
  margin-bottom: var(--space-6);
  padding-bottom: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-4) 0;
  padding-bottom: var(--space-2);
  border-bottom: 2px solid var(--border-primary);
}

.section-title .material-icons {
  font-size: 1.5rem;
  color: var(--accent-primary);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-4);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.info-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
}

.info-value {
  font-size: var(--text-base);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.info-value.total-amount {
  font-size: var(--text-xl);
  color: var(--accent-primary);
  font-weight: var(--font-bold);
}

.address-box {
  background: var(--bg-tertiary);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
  line-height: 1.8;
}

.address-box p {
  margin: 0;
  color: var(--text-primary);
}

.order-items-table {
  overflow-x: auto;
}

.items-table {
  width: 100%;
  border-collapse: collapse;
}

.items-table thead {
  background: var(--bg-tertiary);
}

.items-table th {
  padding: var(--space-3);
  text-align: left;
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  border-bottom: 2px solid var(--border-primary);
}

.items-table td {
  padding: var(--space-3);
  border-bottom: 1px solid var(--border-primary);
}

.items-table tbody tr:hover {
  background: var(--bg-secondary);
}

.product-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-primary);
}

.product-name {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.product-brand {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.color-badge {
  display: inline-block;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid var(--border-primary);
  margin-right: var(--space-2);
  vertical-align: middle;
}

.total-row {
  background: var(--bg-tertiary);
  font-weight: var(--font-bold);
}

.total-price {
  font-size: var(--text-lg);
  color: var(--accent-primary);
}

.payment-status {
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
}

.payment-status.status-paid {
  background: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.payment-status.status-pending {
  background: rgba(245, 158, 11, 0.2);
  color: #f59e0b;
}

.status-timeline {
  position: relative;
  padding-left: var(--space-6);
}

.status-timeline::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: var(--border-primary);
}

.timeline-item {
  position: relative;
  padding-bottom: var(--space-4);
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--bg-card);
  border: 3px solid var(--border-primary);
  position: absolute;
  left: -24px;
  top: 2px;
  z-index: 1;
  transition: all 0.3s ease;
}

.timeline-active .timeline-dot {
  background: var(--accent-primary);
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 4px rgba(167, 139, 250, 0.2);
}

.timeline-content {
  flex: 1;
}

.timeline-status {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.timeline-date {
  font-size: var(--text-sm);
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

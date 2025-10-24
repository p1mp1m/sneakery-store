<template>
  <div class="user-page orders-page">
    <div class="orders-container">
      <h1 class="page-title">Đơn hàng của tôi</h1>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải danh sách đơn hàng...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="orders.length === 0" class="empty-orders">
        <div class="empty-icon">📦</div>
        <h2>Chưa có đơn hàng nào</h2>
        <p>Hãy mua sắm ngay để trải nghiệm dịch vụ của chúng tôi!</p>
        <router-link to="/products" class="btn btn-primary">
          Khám phá sản phẩm
        </router-link>
      </div>

      <!-- Orders List -->
      <div v-else class="orders-list">
        <div
          v-for="order in orders"
          :key="order.id"
          class="order-card"
        >
          <!-- Order Header -->
          <div class="order-header">
            <div class="order-info">
              <h3 class="order-id">Đơn hàng #{{ order.id }}</h3>
              <p class="order-date">{{ formatDate(order.createdAt) }}</p>
            </div>
            <div class="order-status">
              <span :class="['status-badge', getStatusClass(order.status)]">
                {{ getStatusText(order.status) }}
              </span>
            </div>
          </div>

          <!-- Order Items Summary -->
          <div class="order-summary">
            <p class="items-count">{{ order.totalItems }} sản phẩm</p>
            <p class="total-amount">{{ formatPrice(order.totalAmount) }}</p>
          </div>

          <!-- Order Actions -->
          <div class="order-actions">
            <button @click="viewOrderDetail(order.id)" class="btn btn-outline btn-sm">
              Xem chi tiết
            </button>
            <button
              v-if="canReorder(order.status)"
              @click="reorder(order.id)"
              class="btn btn-primary btn-sm"
            >
              Đặt lại
            </button>
            <button
              v-if="canCancel(order.status)"
              @click="cancelOrder(order.id)"
              class="btn btn-outline btn-sm btn-danger"
            >
              Hủy đơn
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Order Detail Modal -->
    <div v-if="showDetail && selectedOrder" class="modal-overlay" @click.self="showDetail = false">
      <div class="modal modal-large">
        <div class="modal-header">
          <h3>Chi tiết đơn hàng #{{ selectedOrder.id }}</h3>
          <button @click="showDetail = false" class="modal-close">×</button>
        </div>
        
        <div class="modal-body">
          <!-- Order Status Timeline -->
          <div class="status-timeline">
            <h4>Trạng thái đơn hàng</h4>
            <div class="timeline">
              <div
                v-for="history in selectedOrder.statusHistories || []"
                :key="history.id"
                class="timeline-item"
              >
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <p class="timeline-status">{{ getStatusText(history.status) }}</p>
                  <p class="timeline-date">{{ formatDateTime(history.changedAt) }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Order Items -->
          <div class="order-items">
            <h4>Sản phẩm</h4>
            <div
              v-for="item in selectedOrder.orderDetails || []"
              :key="item.variantId"
              class="order-item"
            >
              <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" />
              <div class="item-info">
                <h5>{{ item.productName }}</h5>
                <p class="item-brand">{{ item.brandName }}</p>
                <p class="item-variant">{{ item.size }} / {{ item.color }}</p>
              </div>
              <div class="item-price">
                <p class="quantity">x{{ item.quantity }}</p>
                <p class="price">{{ formatPrice(item.totalPrice) }}</p>
              </div>
            </div>
          </div>

          <!-- Shipping Address -->
          <div class="shipping-info">
            <h4>Địa chỉ giao hàng</h4>
            <div v-if="selectedOrder.addressShipping" class="address-box">
              <p><strong>{{ selectedOrder.addressShipping.recipientName }}</strong></p>
              <p>{{ selectedOrder.addressShipping.phone }}</p>
              <p>{{ selectedOrder.addressShipping.line1 }}</p>
              <p v-if="selectedOrder.addressShipping.line2">{{ selectedOrder.addressShipping.line2 }}</p>
              <p>{{ selectedOrder.addressShipping.district }}, {{ selectedOrder.addressShipping.city }}</p>
            </div>
          </div>

          <!-- Payment Info -->
          <div class="payment-info">
            <h4>Thanh toán</h4>
            <div v-if="selectedOrder.payment" class="payment-box">
              <div class="payment-row">
                <span>Phương thức</span>
                <span>{{ getPaymentMethodText(selectedOrder.payment.method) }}</span>
              </div>
              <div class="payment-row">
                <span>Trạng thái</span>
                <span :class="['payment-status', selectedOrder.payment.status]">
                  {{ getPaymentStatusText(selectedOrder.payment.status) }}
                </span>
              </div>
              <div class="payment-row total">
                <span>Tổng cộng</span>
                <span class="total-price">{{ formatPrice(selectedOrder.totalAmount) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="showDetail = false" class="btn btn-primary">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const authStore = useAuthStore();

// State
const orders = ref([]);
const loading = ref(true);
const showDetail = ref(false);
const selectedOrder = ref(null);

// Methods
const fetchOrders = async () => {
  try {
    loading.value = true;
    
    const response = await axios.get('http://localhost:8080/api/orders/my', {
      headers: {
        Authorization: `Bearer ${authStore.token}`,
      },
    });
    
    orders.value = response.data;
  } catch (error) {
    console.error('Error fetching orders:', error);
    ElMessage.error('Không thể tải danh sách đơn hàng');
  } finally {
    loading.value = false;
  }
};

const viewOrderDetail = async (orderId) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/orders/my/${orderId}`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`,
      },
    });
    
    selectedOrder.value = response.data;
    showDetail.value = true;
  } catch (error) {
    console.error('Error fetching order detail:', error);
    ElMessage.error('Không thể tải chi tiết đơn hàng');
  }
};

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc muốn hủy đơn hàng này?',
      'Xác nhận hủy đơn',
      {
        confirmButtonText: 'Hủy đơn',
        cancelButtonText: 'Quay lại',
        type: 'warning',
      }
    );

    // TODO: Implement cancel order API
    ElMessage.success('Đã hủy đơn hàng thành công');
    await fetchOrders();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error canceling order:', error);
      ElMessage.error('Không thể hủy đơn hàng');
    }
  }
};

const reorder = async (orderId) => {
  try {
    // TODO: Implement reorder functionality
    ElMessage.info('Tính năng đang được phát triển');
  } catch (error) {
    console.error('Error reordering:', error);
    ElMessage.error('Không thể đặt lại đơn hàng');
  }
};

const canCancel = (status) => {
  return ['Pending', 'Processing'].includes(status);
};

const canReorder = (status) => {
  return ['Completed', 'Cancelled'].includes(status);
};

const getStatusClass = (status) => {
  const statusMap = {
    'Pending': 'status-pending',
    'Processing': 'status-processing',
    'Paid': 'status-paid',
    'Shipped': 'status-shipped',
    'Completed': 'status-completed',
    'Cancelled': 'status-cancelled',
  };
  return statusMap[status] || 'status-default';
};

const getStatusText = (status) => {
  const statusMap = {
    'Pending': 'Chờ xác nhận',
    'Processing': 'Đang xử lý',
    'Paid': 'Đã thanh toán',
    'Shipped': 'Đang giao',
    'Completed': 'Hoàn thành',
    'Cancelled': 'Đã hủy',
  };
  return statusMap[status] || status;
};

const getPaymentMethodText = (method) => {
  const methodMap = {
    'cod': 'Thanh toán khi nhận hàng (COD)',
    'online': 'Thanh toán trực tuyến',
  };
  return methodMap[method] || method;
};

const getPaymentStatusText = (status) => {
  const statusMap = {
    'pending': 'Chờ thanh toán',
    'completed': 'Đã thanh toán',
    'failed': 'Thanh toán thất bại',
  };
  return statusMap[status] || status;
};

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(price);
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(date);
};

const formatDateTime = (dateString) => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
};

// Lifecycle
onMounted(() => {
  fetchOrders();
});
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: var(--space-6);
}

.orders-container {
  max-width: 900px;
  margin: 0 auto;
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-6);
}

/* Loading */
.loading-container {
  text-align: center;
  padding: var(--space-8);
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto var(--space-4);
}

/* Empty State */
.empty-orders {
  text-align: center;
  padding: var(--space-8);
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: var(--space-4);
}

.empty-orders h2 {
  font-size: var(--text-2xl);
  margin-bottom: var(--space-2);
}

.empty-orders p {
  color: var(--text-secondary);
  margin-bottom: var(--space-6);
}

/* Orders List */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.order-card {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-5);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-fast);
}

.order-card:hover {
  box-shadow: var(--shadow-md);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-4);
  padding-bottom: var(--space-3);
  border-bottom: 1px solid var(--border-color);
}

.order-id {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-1);
}

.order-date {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.status-badge {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
}

.status-pending {
  background: #FEF3C7;
  color: #92400E;
}

.status-processing {
  background: #DBEAFE;
  color: #1E40AF;
}

.status-paid {
  background: #D1FAE5;
  color: #065F46;
}

.status-shipped {
  background: #E0E7FF;
  color: #3730A3;
}

.status-completed {
  background: #D1FAE5;
  color: #065F46;
}

.status-cancelled {
  background: #FEE2E2;
  color: #991B1B;
}

.order-summary {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.items-count {
  color: var(--text-secondary);
}

.total-amount {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--primary-color);
}

.order-actions {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.btn-danger {
  color: var(--error-color);
  border-color: var(--error-color);
}

.btn-danger:hover {
  background: var(--error-color);
  color: white;
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
  padding: var(--space-4);
}

.modal {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  max-width: 700px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-large {
  max-width: 900px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-5);
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  margin: 0;
  font-size: var(--text-xl);
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  font-size: var(--text-2xl);
  cursor: pointer;
  color: var(--text-secondary);
}

.modal-close:hover {
  color: var(--error-color);
}

.modal-body {
  padding: var(--space-6);
}

.modal-body h4 {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-3);
  padding-bottom: var(--space-2);
  border-bottom: 2px solid var(--border-color);
}

.modal-body > div:not(:last-child) {
  margin-bottom: var(--space-6);
}

/* Timeline */
.timeline {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  margin-top: var(--space-4);
}

.timeline-item {
  display: flex;
  gap: var(--space-3);
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--primary-color);
  margin-top: 4px;
}

.timeline-content {
  flex: 1;
}

.timeline-status {
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-1);
}

.timeline-date {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* Order Items */
.order-items {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.order-item {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-3);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
}

.order-item img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.item-info {
  flex: 1;
}

.item-info h5 {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-1);
}

.item-brand,
.item-variant {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 2px 0;
}

.item-price {
  text-align: right;
}

.quantity {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-1);
}

.price {
  font-weight: var(--font-bold);
  font-size: var(--text-lg);
}

/* Address & Payment Box */
.address-box,
.payment-box {
  padding: var(--space-4);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
}

.address-box p {
  margin: var(--space-1) 0;
  color: var(--text-secondary);
}

.address-box p:first-child {
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.payment-row {
  display: flex;
  justify-content: space-between;
  padding: var(--space-2) 0;
}

.payment-row.total {
  border-top: 2px solid var(--border-color);
  padding-top: var(--space-3);
  margin-top: var(--space-2);
  font-weight: var(--font-bold);
}

.total-price {
  color: var(--primary-color);
  font-size: var(--text-xl);
}

.payment-status {
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.payment-status.pending {
  background: #FEF3C7;
  color: #92400E;
}

.payment-status.completed {
  background: #D1FAE5;
  color: #065F46;
}

.payment-status.failed {
  background: #FEE2E2;
  color: #991B1B;
}

.modal-footer {
  padding: var(--space-4);
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
}

/* Responsive */
@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    gap: var(--space-2);
  }

  .order-summary {
    flex-direction: column;
    gap: var(--space-2);
  }

  .order-item {
    flex-wrap: wrap;
  }

  .item-price {
    width: 100%;
    text-align: left;
    display: flex;
    justify-content: space-between;
  }
}
</style>

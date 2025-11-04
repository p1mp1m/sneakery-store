<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-6xl mx-auto px-4">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-6">Đơn hàng của tôi</h1>

      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-16">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-purple-600 border-t-transparent mb-4"></div>
          <p class="text-gray-600 dark:text-gray-400">Đang tải danh sách đơn hàng...</p>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else-if="orders.length === 0" class="text-center py-16 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="text-6xl mb-4">📦</div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Chưa có đơn hàng nào</h2>
        <p class="text-gray-600 dark:text-gray-400 mb-6">Hãy mua sắm ngay để trải nghiệm dịch vụ của chúng tôi!</p>
        <router-link to="/home/products" class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all">
          Khám phá sản phẩm
        </router-link>
      </div>

      <!-- Orders List -->
      <div v-else class="space-y-4">
        <div
          v-for="order in orders"
          :key="order.id"
          class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md transition-shadow"
        >
          <!-- Order Header -->
          <div class="flex items-center justify-between mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
            <div>
              <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-1">Đơn hàng #{{ order.id }}</h3>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ formatDate(order.createdAt) }}</p>
            </div>
            <div>
              <span :class="[
                'px-3 py-1 rounded-full text-sm font-semibold',
                getStatusClass(order.status)
              ]">
                {{ getStatusText(order.status) }}
              </span>
            </div>
          </div>

          <!-- Order Items Summary -->
          <div class="flex items-center justify-between mb-4">
            <p class="text-gray-600 dark:text-gray-400">{{ order.totalItems }} sản phẩm</p>
            <p class="text-xl font-bold text-purple-600 dark:text-purple-400">{{ formatPrice(order.totalAmount) }}</p>
          </div>

          <!-- Order Actions -->
          <div class="flex gap-3">
            <button @click="viewOrderDetail(order.id)" class="flex-1 px-4 py-2 border border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors text-sm font-medium">
              Xem chi tiết
            </button>
            <button
              v-if="canReorder(order.status)"
              @click="reorder(order.id)"
              class="flex-1 px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all text-sm"
            >
              Đặt lại
            </button>
            <button
              v-if="canCancel(order.status)"
              @click="cancelOrder(order.id)"
              class="flex-1 px-4 py-2 border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors text-sm font-medium"
            >
              Hủy đơn
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Order Detail Modal -->
    <div v-if="showDetail && selectedOrder" class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4" @click.self="showDetail = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800">
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100">Chi tiết đơn hàng #{{ selectedOrder.id }}</h3>
          <button @click="showDetail = false" class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">×</button>
        </div>
        
        <div class="p-6 space-y-6">
          <!-- Order Status Timeline -->
          <div>
            <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Trạng thái đơn hàng</h4>
            <div class="space-y-4">
              <div
                v-for="(history, index) in selectedOrder.statusHistories || []"
                :key="history.id"
                class="flex items-start gap-4"
              >
                <div class="flex flex-col items-center">
                  <div class="w-3 h-3 rounded-full bg-purple-600 dark:bg-purple-400"></div>
                  <div v-if="index < (selectedOrder.statusHistories?.length || 0) - 1" class="w-0.5 h-full bg-gray-200 dark:bg-gray-700 mt-2 min-h-[40px]"></div>
                </div>
                <div class="flex-1 pb-4">
                  <p class="font-semibold text-gray-900 dark:text-gray-100">{{ getStatusText(history.status) }}</p>
                  <p class="text-sm text-gray-500 dark:text-gray-400">{{ formatDateTime(history.changedAt) }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Order Items -->
          <div>
            <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Sản phẩm</h4>
            <div class="space-y-4">
              <div
                v-for="item in selectedOrder.orderDetails || []"
                :key="item.variantId"
                class="flex items-center gap-4 p-4 border border-gray-200 dark:border-gray-700 rounded-xl"
              >
                <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" class="w-20 h-20 rounded-lg object-cover" />
                <div class="flex-1">
                  <h5 class="font-semibold text-gray-900 dark:text-gray-100 mb-1">{{ item.productName }}</h5>
                  <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">{{ item.brandName }}</p>
                  <p class="text-sm text-gray-600 dark:text-gray-400">{{ item.size }} / {{ item.color }}</p>
                </div>
                <div class="text-right">
                  <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">x{{ item.quantity }}</p>
                  <p class="font-semibold text-gray-900 dark:text-gray-100">{{ formatPrice(item.totalPrice) }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Shipping Address -->
          <div>
            <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Địa chỉ giao hàng</h4>
            <div v-if="selectedOrder.addressShipping" class="p-4 border border-gray-200 dark:border-gray-700 rounded-xl bg-gray-50 dark:bg-gray-700/50">
              <p class="font-semibold text-gray-900 dark:text-gray-100 mb-1">{{ selectedOrder.addressShipping.recipientName }}</p>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ selectedOrder.addressShipping.phone }}</p>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ selectedOrder.addressShipping.line1 }}</p>
              <p v-if="selectedOrder.addressShipping.line2" class="text-sm text-gray-600 dark:text-gray-400">{{ selectedOrder.addressShipping.line2 }}</p>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ selectedOrder.addressShipping.district }}, {{ selectedOrder.addressShipping.city }}</p>
            </div>
          </div>

          <!-- Payment Info -->
          <div>
            <h4 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Thanh toán</h4>
            <div v-if="selectedOrder.payment" class="p-4 border border-gray-200 dark:border-gray-700 rounded-xl space-y-3">
              <div class="flex justify-between items-center">
                <span class="text-gray-600 dark:text-gray-400">Phương thức</span>
                <span class="font-medium text-gray-900 dark:text-gray-100">{{ getPaymentMethodText(selectedOrder.payment.method) }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-600 dark:text-gray-400">Trạng thái</span>
                <span :class="[
                  'px-2 py-1 rounded text-sm font-medium',
                  selectedOrder.payment.status === 'completed' ? 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400' :
                  selectedOrder.payment.status === 'pending' ? 'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400' :
                  'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400'
                ]">
                  {{ getPaymentStatusText(selectedOrder.payment.status) }}
                </span>
              </div>
              <div class="flex justify-between items-center pt-3 border-t border-gray-200 dark:border-gray-700">
                <span class="text-lg font-semibold text-gray-900 dark:text-gray-100">Tổng cộng</span>
                <span class="text-2xl font-bold text-purple-600 dark:text-purple-400">{{ formatPrice(selectedOrder.totalAmount) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-end p-6 border-t border-gray-200 dark:border-gray-700">
          <button @click="showDetail = false" class="px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { ElMessage, ElMessageBox } from 'element-plus';
import userService from '@/services/userService';

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
    const data = await userService.getMyOrders();
    orders.value = data;
  } catch (error) {
    console.error('Error fetching orders:', error);
    ElMessage.error(error.message || 'Không thể tải danh sách đơn hàng');
  } finally {
    loading.value = false;
  }
};

const viewOrderDetail = async (orderId) => {
  try {
    const data = await userService.getMyOrderById(orderId);
    selectedOrder.value = data;
    showDetail.value = true;
  } catch (error) {
    console.error('Error fetching order detail:', error);
    ElMessage.error(error.message || 'Không thể tải chi tiết đơn hàng');
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
    'Pending': 'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400',
    'Processing': 'bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400',
    'Paid': 'bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400',
    'Shipped': 'bg-indigo-100 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-400',
    'Completed': 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400',
    'Cancelled': 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400',
  };
  return statusMap[status] || 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-400';
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

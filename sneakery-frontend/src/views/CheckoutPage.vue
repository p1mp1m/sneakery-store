<template>
  <div class="checkout-page">
    <div class="checkout-container">
      <h1 class="page-title">Thanh toán</h1>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải thông tin...</p>
      </div>

      <!-- Checkout Content -->
      <div v-else class="checkout-grid">
        <!-- Left: Form -->
        <div class="checkout-form">
          <!-- Shipping Address -->
          <div class="form-section">
            <h2 class="section-title">Địa chỉ giao hàng</h2>
            
            <div v-if="addresses.length === 0" class="no-address">
              <p>Bạn chưa có địa chỉ giao hàng nào</p>
              <button @click="showAddressForm = true" class="btn btn-primary">
                Thêm địa chỉ mới
              </button>
            </div>

            <div v-else class="address-list">
              <div
                v-for="addr in addresses"
                :key="addr.id"
                :class="['address-card', { selected: selectedAddress === addr.id }]"
                @click="selectedAddress = addr.id"
              >
                <div class="address-content">
                  <h4>{{ addr.recipientName }}</h4>
                  <p>{{ addr.phone }}</p>
                  <p>{{ addr.line1 }}</p>
                  <p v-if="addr.line2">{{ addr.line2 }}</p>
                  <p>{{ addr.district }}, {{ addr.city }}</p>
                </div>
                <div class="address-check">
                  <span v-if="selectedAddress === addr.id">✓</span>
                </div>
              </div>
              
              <button @click="showAddressForm = true" class="btn btn-outline btn-sm">
                + Thêm địa chỉ mới
              </button>
            </div>
          </div>

          <!-- Payment Method -->
          <div class="form-section">
            <h2 class="section-title">Phương thức thanh toán</h2>
            
            <div class="payment-methods">
              <div
                :class="['payment-card', { selected: paymentMethod === 'cod' }]"
                @click="paymentMethod = 'cod'"
              >
                <div class="payment-icon">💵</div>
                <div class="payment-content">
                  <h4>Thanh toán khi nhận hàng (COD)</h4>
                  <p>Thanh toán bằng tiền mặt khi nhận hàng</p>
                </div>
                <div class="payment-check">
                  <span v-if="paymentMethod === 'cod'">✓</span>
                </div>
              </div>

              <div
                :class="['payment-card', { selected: paymentMethod === 'online' }]"
                @click="paymentMethod = 'online'"
              >
                <div class="payment-icon">💳</div>
                <div class="payment-content">
                  <h4>Thanh toán trực tuyến</h4>
                  <p>Thanh toán qua VNPay, MoMo, thẻ ATM/Visa...</p>
                </div>
                <div class="payment-check">
                  <span v-if="paymentMethod === 'online'">✓</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Notes -->
          <div class="form-section">
            <h2 class="section-title">Ghi chú (không bắt buộc)</h2>
            <textarea
              v-model="notes"
              placeholder="Nhập ghi chú cho đơn hàng..."
              class="notes-input"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- Right: Order Summary -->
        <div class="order-summary">
          <h2>Đơn hàng ({{ cart?.totalItems || 0 }} sản phẩm)</h2>

          <!-- Cart Items -->
          <div class="summary-items">
            <div
              v-for="item in cart?.items || []"
              :key="item.cartItemId"
              class="summary-item"
            >
              <img :src="item.imageUrl || 'https://placehold.co/60'" :alt="item.productName" />
              <div class="summary-item-info">
                <p class="item-name">{{ item.productName }}</p>
                <p class="item-variant">{{ item.size }} / {{ item.color }}</p>
                <p class="item-quantity">x{{ item.quantity }}</p>
              </div>
              <p class="item-price">{{ formatPrice(item.totalPrice) }}</p>
            </div>
          </div>

          <!-- Price Breakdown -->
          <div class="price-breakdown">
            <div class="price-row">
              <span>Tạm tính</span>
              <span>{{ formatPrice(cart?.subTotal || 0) }}</span>
            </div>
            <div class="price-row">
              <span>Phí vận chuyển</span>
              <span>{{ formatPrice(shippingFee) }}</span>
            </div>
            <div class="price-divider"></div>
            <div class="price-row total">
              <span>Tổng cộng</span>
              <span>{{ formatPrice(totalAmount) }}</span>
            </div>
          </div>

          <!-- Checkout Button -->
          <button
            @click="handleCheckout"
            :disabled="!canCheckout || processing"
            class="btn btn-primary btn-checkout"
          >
            <span v-if="processing">Đang xử lý...</span>
            <span v-else>Xác nhận đặt hàng</span>
          </button>

          <p class="checkout-note">
            Bằng việc đặt hàng, bạn đồng ý với 
            <a href="#">Điều khoản sử dụng</a> của chúng tôi
          </p>
        </div>
      </div>
    </div>

    <!-- Add Address Modal (simplified) -->
    <div v-if="showAddressForm" class="modal-overlay" @click.self="showAddressForm = false">
      <div class="modal">
        <div class="modal-header">
          <h3>Thêm địa chỉ mới</h3>
          <button @click="showAddressForm = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Họ tên người nhận *</label>
            <input v-model="newAddress.recipientName" type="text" class="form-control" />
          </div>
          <div class="form-group">
            <label>Số điện thoại *</label>
            <input v-model="newAddress.phone" type="tel" class="form-control" />
          </div>
          <div class="form-group">
            <label>Địa chỉ *</label>
            <input v-model="newAddress.line1" type="text" class="form-control" />
          </div>
          <div class="form-group">
            <label>Quận/Huyện *</label>
            <input v-model="newAddress.district" type="text" class="form-control" />
          </div>
          <div class="form-group">
            <label>Tỉnh/Thành phố *</label>
            <input v-model="newAddress.city" type="text" class="form-control" />
          </div>
        </div>
        <div class="modal-footer">
          <button @click="saveAddress" class="btn btn-primary">Lưu địa chỉ</button>
          <button @click="showAddressForm = false" class="btn btn-outline">Hủy</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const router = useRouter();
const authStore = useAuthStore();

// State
const loading = ref(true);
const processing = ref(false);
const cart = ref(null);
const addresses = ref([]);
const selectedAddress = ref(null);
const paymentMethod = ref('cod');
const notes = ref('');
const shippingFee = ref(30000);
const showAddressForm = ref(false);
const newAddress = ref({
  recipientName: '',
  phone: '',
  line1: '',
  district: '',
  city: '',
});

// Computed
const totalAmount = computed(() => {
  if (!cart.value) return 0;
  return cart.value.subTotal + shippingFee.value;
});

const canCheckout = computed(() => {
  return (
    cart.value &&
    cart.value.items.length > 0 &&
    selectedAddress.value &&
    paymentMethod.value
  );
});

// Methods
const fetchData = async () => {
  try {
    loading.value = true;

    // Fetch cart
    const cartRes = await axios.get('http://localhost:8080/api/cart', {
      headers: { Authorization: `Bearer ${authStore.token}` },
    });
    cart.value = cartRes.data;

    // Fetch addresses
    const addrRes = await axios.get('http://localhost:8080/api/addresses', {
      headers: { Authorization: `Bearer ${authStore.token}` },
    });
    addresses.value = addrRes.data;

    // Auto-select first address
    if (addresses.value.length > 0) {
      selectedAddress.value = addresses.value[0].id;
    }
  } catch (error) {
    console.error('Error fetching data:', error);
    ElMessage.error('Không thể tải thông tin');
  } finally {
    loading.value = false;
  }
};

const saveAddress = async () => {
  if (!newAddress.value.recipientName || !newAddress.value.phone || !newAddress.value.line1) {
    ElMessage.warning('Vui lòng điền đầy đủ thông tin');
    return;
  }

  try {
    const response = await axios.post(
      'http://localhost:8080/api/addresses',
      newAddress.value,
      {
        headers: { Authorization: `Bearer ${authStore.token}` },
      }
    );

    addresses.value.push(response.data);
    selectedAddress.value = response.data.id;
    showAddressForm.value = false;
    
    // Reset form
    newAddress.value = {
      recipientName: '',
      phone: '',
      line1: '',
      district: '',
      city: '',
    };

    ElMessage.success('Đã thêm địa chỉ mới');
  } catch (error) {
    console.error('Error saving address:', error);
    ElMessage.error('Không thể thêm địa chỉ');
  }
};

const handleCheckout = async () => {
  if (!canCheckout.value) {
    ElMessage.warning('Vui lòng chọn địa chỉ giao hàng và phương thức thanh toán');
    return;
  }

  try {
    processing.value = true;

    const checkoutData = {
      addressShippingId: selectedAddress.value,
      addressBillingId: selectedAddress.value,
      paymentMethod: paymentMethod.value,
    };

    const response = await axios.post(
      'http://localhost:8080/api/orders/checkout',
      checkoutData,
      {
        headers: { Authorization: `Bearer ${authStore.token}` },
      }
    );

    ElMessage.success('Đặt hàng thành công!');
    
    // Redirect to order detail or orders list
    router.push({ name: 'orders' });
  } catch (error) {
    console.error('Error during checkout:', error);
    ElMessage.error(error.response?.data?.message || 'Không thể đặt hàng');
  } finally {
    processing.value = false;
  }
};

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(price);
};

// Lifecycle
onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.checkout-page {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: var(--space-6);
}

.checkout-container {
  max-width: 1200px;
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

/* Checkout Grid */
.checkout-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-6);
}

/* Form Sections */
.form-section {
  background: var(--bg-primary);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-4);
}

.section-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-4);
}

/* Address */
.no-address {
  text-align: center;
  padding: var(--space-6);
  color: var(--text-secondary);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.address-card {
  display: flex;
  justify-content: space-between;
  padding: var(--space-4);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.address-card:hover {
  border-color: var(--primary-color);
}

.address-card.selected {
  border-color: var(--primary-color);
  background: rgba(99, 102, 241, 0.05);
}

.address-content h4 {
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-2);
}

.address-content p {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  margin: var(--space-1) 0;
}

.address-check {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  color: white;
  font-weight: var(--font-bold);
}

/* Payment Methods */
.payment-methods {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.payment-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.payment-card:hover {
  border-color: var(--primary-color);
}

.payment-card.selected {
  border-color: var(--primary-color);
  background: rgba(99, 102, 241, 0.05);
}

.payment-icon {
  font-size: 40px;
}

.payment-content {
  flex: 1;
}

.payment-content h4 {
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-1);
}

.payment-content p {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.payment-check {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  color: white;
  font-weight: var(--font-bold);
}

/* Notes */
.notes-input {
  width: 100%;
  padding: var(--space-3);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: inherit;
  font-size: var(--text-base);
  resize: vertical;
}

.notes-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

/* Order Summary */
.order-summary {
  background: var(--bg-primary);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
  height: fit-content;
  position: sticky;
  top: var(--space-6);
}

.order-summary h2 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-4);
  padding-bottom: var(--space-3);
  border-bottom: 2px solid var(--border-color);
}

.summary-items {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: var(--space-4);
}

.summary-item {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-3);
  border-bottom: 1px solid var(--border-color);
}

.summary-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.summary-item-info {
  flex: 1;
}

.item-name {
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  margin-bottom: var(--space-1);
}

.item-variant,
.item-quantity {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  margin: 2px 0;
}

.item-price {
  font-weight: var(--font-semibold);
  white-space: nowrap;
}

.price-breakdown {
  margin-top: var(--space-4);
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-3);
}

.price-row.total {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--primary-color);
}

.price-divider {
  height: 1px;
  background: var(--border-color);
  margin: var(--space-3) 0;
}

.btn-checkout {
  width: 100%;
  padding: var(--space-4);
  font-size: var(--text-lg);
  margin-top: var(--space-4);
}

.checkout-note {
  text-align: center;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-top: var(--space-3);
}

.checkout-note a {
  color: var(--primary-color);
  text-decoration: none;
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
}

.modal {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
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

.form-group {
  margin-bottom: var(--space-4);
}

.form-group label {
  display: block;
  margin-bottom: var(--space-2);
  font-weight: var(--font-semibold);
}

.form-control {
  width: 100%;
  padding: var(--space-3);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
}

.form-control:focus {
  outline: none;
  border-color: var(--primary-color);
}

.modal-footer {
  padding: var(--space-4);
  border-top: 1px solid var(--border-color);
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
}

/* Responsive */
@media (max-width: 768px) {
  .checkout-grid {
    grid-template-columns: 1fr;
  }

  .order-summary {
    position: static;
  }
}
</style>


<template>
  <div class="user-page cart-page">
    <div class="cart-container">
      <h1 class="page-title">Giỏ hàng của bạn</h1>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Đang tải giỏ hàng...</p>
      </div>

      <!-- Empty Cart -->
      <div v-else-if="!cart || cart.items.length === 0" class="empty-cart">
        <div class="empty-icon">🛒</div>
        <h2>Giỏ hàng trống</h2>
        <p>Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
        <router-link to="/products" class="btn btn-primary">
          Xem sản phẩm
        </router-link>
      </div>

      <!-- Cart Items -->
      <div v-else class="cart-grid">
        <!-- Cart Items List -->
        <div class="cart-items">
          <div
            v-for="item in cart.items"
            :key="item.cartItemId"
            class="cart-item"
          >
            <div class="item-image">
              <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" />
            </div>

            <div class="item-info">
              <h3 class="item-name">{{ item.productName }}</h3>
              <p class="item-brand">{{ item.brandName }}</p>
              <div class="item-variants">
                <span class="variant-tag">Màu: {{ item.color }}</span>
                <span class="variant-tag">Size: {{ item.size }}</span>
              </div>
              <p class="item-price">{{ formatPrice(item.unitPrice) }}</p>
            </div>

            <div class="item-actions">
              <!-- Quantity Controls -->
              <div class="quantity-controls">
                <button @click="updateQuantity(item, item.quantity - 1)" :disabled="item.quantity <= 1">
                  -
                </button>
                <input v-model.number="item.quantity" type="number" min="1" readonly />
                <button @click="updateQuantity(item, item.quantity + 1)">+</button>
              </div>

              <!-- Total Price -->
              <p class="item-total">{{ formatPrice(item.totalPrice) }}</p>

              <!-- Remove Button -->
              <button @click="removeItem(item)" class="btn-remove">
                Xóa
              </button>
            </div>
          </div>
        </div>

        <!-- Cart Summary -->
        <div class="cart-summary">
          <h2>Tổng đơn hàng</h2>

          <!-- Coupon Input -->
          <div class="coupon-section">
            <div class="coupon-input-group">
              <input
                v-model="couponCode"
                type="text"
                placeholder="Nhập mã giảm giá"
                class="coupon-input"
                :disabled="couponApplied"
              />
              <button
                @click="applyCoupon"
                :disabled="!couponCode || couponApplied || applyingCoupon"
                class="btn btn-primary btn-apply-coupon"
              >
                {{ applyingCoupon ? 'Đang áp dụng...' : (couponApplied ? 'Đã áp dụng' : 'Áp dụng') }}
              </button>
            </div>
            <div v-if="couponError" class="coupon-error">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
              </svg>
              {{ couponError }}
            </div>
            <div v-if="couponApplied" class="coupon-success">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
              Đã áp dụng mã "{{ couponCode }}" (-{{ couponDiscount }}%)
              <button @click="removeCoupon" class="btn-remove-coupon">Xóa</button>
            </div>
          </div>
          
          <div class="summary-divider"></div>

          <div class="summary-row">
            <span>Tạm tính ({{ cart.totalItems }} sản phẩm)</span>
            <span>{{ formatPrice(cart.subTotal) }}</span>
          </div>

          <div v-if="couponApplied" class="summary-row discount-row">
            <span>Giảm giá (-{{ couponDiscount }}%)</span>
            <span class="discount-amount">-{{ formatPrice(discountAmount) }}</span>
          </div>

          <div class="summary-row">
            <span>Phí vận chuyển</span>
            <span :class="{ 'free-shipping': shippingFee === 0 }">
              {{ shippingFee === 0 ? 'Miễn phí' : formatPrice(shippingFee) }}
            </span>
          </div>

          <div class="summary-divider"></div>

          <div class="summary-row total-row">
            <span>Tổng cộng</span>
            <span class="total-price">{{ formatPrice(totalAmount) }}</span>
          </div>

          <button @click="proceedToCheckout" class="btn btn-gradient btn-checkout">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14"></path>
              <path d="m12 5 7 7-7 7"></path>
            </svg>
            Tiến hành thanh toán
          </button>

          <router-link to="/products" class="btn btn-outline btn-continue">
            Tiếp tục mua sắm
          </router-link>

          <!-- Free Shipping Indicator -->
          <div v-if="cart.subTotal >= 500000" class="free-shipping-indicator">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="1" y="3" width="15" height="13"></rect>
              <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
              <circle cx="5.5" cy="18.5" r="2.5"></circle>
              <circle cx="18.5" cy="18.5" r="2.5"></circle>
            </svg>
            Đơn hàng đủ điều kiện giao hàng miễn phí!
          </div>
          <div v-else class="shipping-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: shippingProgress + '%' }"></div>
            </div>
            <p>Mua thêm {{ formatPrice(500000 - cart.subTotal) }} để được giao hàng miễn phí</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const router = useRouter();
const authStore = useAuthStore();

// State
const cart = ref(null);
const loading = ref(true);
const couponCode = ref('');
const couponApplied = ref(false);
const couponDiscount = ref(0);
const couponError = ref('');
const applyingCoupon = ref(false);

// Computed
const shippingFee = computed(() => {
  // Free shipping for orders over 500K
  if (!cart.value) return 30000;
  return cart.value.subTotal >= 500000 ? 0 : 30000;
});

const shippingProgress = computed(() => {
  if (!cart.value) return 0;
  const progress = (cart.value.subTotal / 500000) * 100;
  return Math.min(progress, 100);
});

const discountAmount = computed(() => {
  if (!cart.value || !couponApplied.value) return 0;
  return (cart.value.subTotal * couponDiscount.value) / 100;
});

const totalAmount = computed(() => {
  if (!cart.value) return 0;
  return cart.value.subTotal - discountAmount.value + shippingFee.value;
});

// Methods
const fetchCart = async () => {
  try {
    loading.value = true;
    const response = await axios.get('http://localhost:8080/api/cart', {
      headers: {
        Authorization: `Bearer ${authStore.token}`,
      },
    });
    cart.value = response.data;
  } catch (error) {
    console.error('Error fetching cart:', error);
    ElMessage.error('Không thể tải giỏ hàng');
  } finally {
    loading.value = false;
  }
};

const updateQuantity = async (item, newQuantity) => {
  if (newQuantity < 1) return;

  try {
    await axios.post(
      'http://localhost:8080/api/cart/item',
      {
        variantId: item.variantId,
        quantity: newQuantity,
      },
      {
        headers: {
          Authorization: `Bearer ${authStore.token}`,
        },
      }
    );

    // Refresh cart
    await fetchCart();
    ElMessage.success('Đã cập nhật số lượng');
  } catch (error) {
    console.error('Error updating quantity:', error);
    ElMessage.error('Không thể cập nhật số lượng');
  }
};

const removeItem = async (item) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc muốn xóa "${item.productName}" khỏi giỏ hàng?`,
      'Xác nhận',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
    );

    await axios.delete(`http://localhost:8080/api/cart/item/${item.variantId}`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`,
      },
    });

    await fetchCart();
    ElMessage.success('Đã xóa sản phẩm khỏi giỏ hàng');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error removing item:', error);
      ElMessage.error('Không thể xóa sản phẩm');
    }
  }
};

const applyCoupon = async () => {
  if (!couponCode.value) return;

  try {
    applyingCoupon.value = true;
    couponError.value = '';

    // Mock coupon validation (replace with real API call later)
    // For demo: SAVE10 = 10%, SAVE20 = 20%, SAVE30 = 30%
    const validCoupons = {
      'SAVE10': 10,
      'SAVE20': 20,
      'SAVE30': 30,
      'FREESHIP': 0 // Just for free shipping demo
    };

    const code = couponCode.value.toUpperCase();
    if (validCoupons[code] !== undefined) {
      couponDiscount.value = validCoupons[code];
      couponApplied.value = true;
      ElMessage.success(`Đã áp dụng mã giảm giá ${couponDiscount.value}%`);
    } else {
      couponError.value = 'Mã giảm giá không hợp lệ hoặc đã hết hạn';
    }
  } catch (error) {
    console.error('Error applying coupon:', error);
    couponError.value = 'Không thể áp dụng mã giảm giá';
  } finally {
    applyingCoupon.value = false;
  }
};

const removeCoupon = () => {
  couponCode.value = '';
  couponApplied.value = false;
  couponDiscount.value = 0;
  couponError.value = '';
  ElMessage.info('Đã xóa mã giảm giá');
};

const proceedToCheckout = () => {
  router.push('/checkout');
};

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(price);
};

// Lifecycle
onMounted(() => {
  fetchCart();
});
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: var(--space-6);
}

.cart-container {
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

/* Empty Cart */
.empty-cart {
  text-align: center;
  padding: var(--space-8);
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: var(--space-4);
}

.empty-cart h2 {
  font-size: var(--text-2xl);
  margin-bottom: var(--space-2);
}

.empty-cart p {
  color: var(--text-secondary);
  margin-bottom: var(--space-6);
}

/* Cart Grid */
.cart-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-6);
}

/* Cart Items */
.cart-items {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.cart-item {
  display: grid;
  grid-template-columns: 150px 1fr auto;
  gap: var(--space-4);
  background: var(--bg-primary);
  padding: var(--space-4);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.item-image {
  width: 150px;
  height: 150px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-secondary);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.item-name {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  margin: 0;
}

.item-brand {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  margin: 0;
}

.item-variants {
  display: flex;
  gap: var(--space-2);
}

.variant-tag {
  background: var(--bg-secondary);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
}

.item-price {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--primary-color);
  margin: 0;
}

.item-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  gap: var(--space-3);
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.quantity-controls button {
  width: 32px;
  height: 32px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.quantity-controls button:hover:not(:disabled) {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.quantity-controls button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-controls input {
  width: 50px;
  height: 32px;
  text-align: center;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  font-weight: var(--font-semibold);
}

.item-total {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin: 0;
}

.btn-remove {
  padding: var(--space-2) var(--space-4);
  background: transparent;
  color: var(--error-color);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-weight: var(--font-medium);
  transition: all var(--transition-fast);
}

.btn-remove:hover {
  background: rgba(239, 68, 68, 0.1);
}

/* Cart Summary */
.cart-summary {
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  backdrop-filter: blur(10px);
  padding: var(--space-6);
  border-radius: var(--radius-xl);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
  height: fit-content;
  position: sticky;
  top: var(--space-6);
}

.cart-summary h2 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-5);
  color: #f1f5f9;
}

/* Coupon Section */
.coupon-section {
  margin-bottom: var(--space-4);
}

.coupon-input-group {
  display: flex;
  gap: var(--space-2);
}

.coupon-input {
  flex: 1;
  padding: var(--space-3);
  background: rgba(15, 23, 42, 0.6);
  border: 2px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-md);
  color: #f1f5f9;
  font-size: var(--text-base);
  transition: all var(--transition-fast);
}

.coupon-input:focus {
  outline: none;
  border-color: #a78bfa;
  background: rgba(15, 23, 42, 0.8);
}

.coupon-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.coupon-input::placeholder {
  color: #64748b;
}

.btn-apply-coupon {
  min-width: 100px;
  padding: var(--space-3) var(--space-4);
  font-weight: var(--font-semibold);
}

.coupon-error {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-top: var(--space-2);
  padding: var(--space-2) var(--space-3);
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: var(--radius-md);
  color: #fca5a5;
  font-size: var(--text-sm);
}

.coupon-error svg {
  flex-shrink: 0;
}

.coupon-success {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-top: var(--space-2);
  padding: var(--space-3);
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.3);
  border-radius: var(--radius-md);
  color: #6ee7b7;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.coupon-success svg {
  flex-shrink: 0;
}

.btn-remove-coupon {
  margin-left: auto;
  padding: var(--space-1) var(--space-2);
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: var(--radius-sm);
  color: #fca5a5;
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-remove-coupon:hover {
  background: rgba(239, 68, 68, 0.3);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
  font-size: var(--text-base);
  color: #cbd5e1;
}

.summary-row.discount-row {
  color: #6ee7b7;
}

.discount-amount {
  color: #6ee7b7;
  font-weight: var(--font-semibold);
}

.free-shipping {
  color: #6ee7b7;
  font-weight: var(--font-semibold);
}

.summary-divider {
  height: 1px;
  background: rgba(167, 139, 250, 0.2);
  margin: var(--space-4) 0;
}

.total-row {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  margin-top: var(--space-2);
  padding-top: var(--space-2);
}

.total-row span:first-child {
  color: #f1f5f9;
}

.total-price {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  background: linear-gradient(135deg, #a78bfa 0%, #c4b5fd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.btn-checkout {
  width: 100%;
  margin-top: var(--space-5);
  padding: var(--space-4) var(--space-5);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
}

.btn-continue {
  width: 100%;
  margin-top: var(--space-3);
  padding: var(--space-3) var(--space-5);
  text-align: center;
  text-decoration: none;
  display: block;
}

/* Free Shipping Indicator */
.free-shipping-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  margin-top: var(--space-5);
  padding: var(--space-3);
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.3);
  border-radius: var(--radius-md);
  color: #6ee7b7;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
}

.free-shipping-indicator svg {
  flex-shrink: 0;
}

/* Shipping Progress */
.shipping-progress {
  margin-top: var(--space-5);
  padding: var(--space-3);
  background: rgba(30, 41, 59, 0.4);
  border-radius: var(--radius-md);
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: rgba(15, 23, 42, 0.6);
  border-radius: var(--radius-full);
  overflow: hidden;
  margin-bottom: var(--space-2);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #a78bfa 0%, #8b5cf6 100%);
  border-radius: var(--radius-full);
  transition: width var(--transition-normal);
}

.shipping-progress p {
  margin: 0;
  font-size: var(--text-xs);
  color: #94a3b8;
  text-align: center;
}

/* Responsive */
@media (max-width: 768px) {
  .cart-grid {
    grid-template-columns: 1fr;
  }

  .cart-item {
    grid-template-columns: 100px 1fr;
    gap: var(--space-3);
  }

  .item-image {
    width: 100px;
    height: 100px;
  }

  .item-actions {
    grid-column: 1 / -1;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .cart-summary {
    position: static;
  }
}

/* Dark Theme Enhancements */
.cart-page {
  background: transparent;
}

.page-title {
  color: #f1f5f9;
}

.empty-cart {
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(167, 139, 250, 0.15);
}

.empty-cart h2 {
  color: #f1f5f9;
}

.empty-cart p {
  color: #94a3b8;
}

.cart-item {
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  backdrop-filter: blur(10px);
  transition: all var(--transition-fast);
}

.cart-item:hover {
  border-color: rgba(167, 139, 250, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.item-name {
  color: #f1f5f9;
}

.item-brand {
  color: #94a3b8;
}

.item-price {
  color: #c4b5fd;
}

.item-total {
  color: #f1f5f9;
}

.variant-tag {
  background: rgba(167, 139, 250, 0.15);
  color: #e2e8f0;
  border: 1px solid rgba(167, 139, 250, 0.2);
}

.loading-container p {
  color: #94a3b8;
}
</style>

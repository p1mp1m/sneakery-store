<template>
  <div class="cart-page">
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
              <img :src="item.imageUrl || 'https://placehold.co/150'" :alt="item.productName" />
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
          
          <div class="summary-row">
            <span>Tạm tính ({{ cart.totalItems }} sản phẩm)</span>
            <span>{{ formatPrice(cart.subTotal) }}</span>
          </div>

          <div class="summary-row">
            <span>Phí vận chuyển</span>
            <span>{{ formatPrice(shippingFee) }}</span>
          </div>

          <div class="summary-divider"></div>

          <div class="summary-row total-row">
            <span>Tổng cộng</span>
            <span class="total-price">{{ formatPrice(totalAmount) }}</span>
          </div>

          <button @click="proceedToCheckout" class="btn btn-primary btn-checkout">
            Tiến hành thanh toán
          </button>

          <router-link to="/products" class="btn btn-outline">
            Tiếp tục mua sắm
          </router-link>
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
const shippingFee = ref(30000); // Fixed shipping fee

// Computed
const totalAmount = computed(() => {
  if (!cart.value) return 0;
  return cart.value.subTotal + shippingFee.value;
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
  background: var(--bg-primary);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  height: fit-content;
  position: sticky;
  top: var(--space-6);
}

.cart-summary h2 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-4);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-3);
  font-size: var(--text-base);
}

.summary-divider {
  height: 1px;
  background: var(--border-color);
  margin: var(--space-4) 0;
}

.total-row {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
}

.total-price {
  color: var(--primary-color);
  font-size: var(--text-2xl);
}

.btn-checkout {
  width: 100%;
  margin-top: var(--space-4);
  padding: var(--space-4);
  font-size: var(--text-lg);
}

.cart-summary .btn-outline {
  width: 100%;
  margin-top: var(--space-3);
  text-align: center;
  text-decoration: none;
  display: block;
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
</style>

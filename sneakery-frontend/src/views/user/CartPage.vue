<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-7xl mx-auto px-4">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-6">Giỏ hàng của bạn</h1>

      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-16">
        <div class="text-center">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-purple-600 border-t-transparent mb-4"></div>
          <p class="text-gray-600 dark:text-gray-400">Đang tải giỏ hàng...</p>
        </div>
      </div>

      <!-- Empty Cart -->
      <div v-else-if="!cart || cart.items.length === 0" class="text-center py-16 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="text-6xl mb-4">🛒</div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Giỏ hàng trống</h2>
        <p class="text-gray-600 dark:text-gray-400 mb-6">Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
        <router-link to="/home/products" class="inline-flex items-center gap-2 px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors">
          Xem sản phẩm
        </router-link>
      </div>

      <!-- Cart Items -->
      <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Cart Items List -->
        <div class="lg:col-span-2 space-y-4">
          <div
            v-for="item in cart.items"
            :key="item.cartItemId"
            class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6"
          >
            <div class="flex gap-4">
              <div class="w-24 h-24 rounded-lg overflow-hidden bg-gray-100 dark:bg-gray-700 flex-shrink-0">
                <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" class="w-full h-full object-cover" />
              </div>

              <div class="flex-1 min-w-0">
                <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-1">{{ item.productName }}</h3>
                <p class="text-sm text-gray-500 dark:text-gray-400 mb-2">{{ item.brandName }}</p>
                <div class="flex flex-wrap gap-2 mb-2">
                  <span class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs text-gray-700 dark:text-gray-300">Màu: {{ item.color }}</span>
                  <span class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs text-gray-700 dark:text-gray-300">Size: {{ item.size }}</span>
                </div>
                <p class="font-semibold text-purple-600 dark:text-purple-400">{{ formatPrice(item.unitPrice) }}</p>
              </div>

              <div class="flex flex-col items-end gap-3">
                <!-- Quantity Controls -->
                <div class="flex items-center gap-2">
                  <button 
                    @click="updateQuantity(item, item.quantity - 1)" 
                    :disabled="item.quantity <= 1"
                    class="w-8 h-8 rounded-lg border border-gray-200 dark:border-gray-600 flex items-center justify-center hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    -
                  </button>
                  <input 
                    v-model.number="item.quantity" 
                    type="number" 
                    min="1" 
                    readonly
                    class="w-12 h-8 text-center border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100"
                  />
                  <button 
                    @click="updateQuantity(item, item.quantity + 1)"
                    class="w-8 h-8 rounded-lg border border-gray-200 dark:border-gray-600 flex items-center justify-center hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                  >
                    +
                  </button>
                </div>

                <!-- Total Price -->
                <p class="font-bold text-lg text-gray-900 dark:text-gray-100">{{ formatPrice(item.totalPrice) }}</p>

                <!-- Remove Button -->
                <button @click="removeItem(item)" class="text-red-600 dark:text-red-400 hover:text-red-700 dark:hover:text-red-300 text-sm font-medium transition-colors">
                  Xóa
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Cart Summary -->
        <div class="lg:col-span-1">
          <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 sticky top-24">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">Tổng đơn hàng</h2>

            <!-- Coupon Input -->
            <div class="mb-6">
              <div class="flex gap-2 mb-2">
                <input
                  v-model="couponCode"
                  type="text"
                  placeholder="Nhập mã giảm giá"
                  class="flex-1 px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  :disabled="couponApplied"
                />
                <button
                  @click="applyCoupon"
                  :disabled="!couponCode || couponApplied || applyingCoupon"
                  class="px-4 py-2 bg-purple-600 text-white rounded-lg font-medium hover:bg-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {{ applyingCoupon ? '...' : (couponApplied ? '✓' : 'Áp dụng') }}
                </button>
              </div>
              <div v-if="couponError" class="flex items-center gap-2 text-sm text-red-600 dark:text-red-400 mt-2">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"></circle>
                  <line x1="12" y1="8" x2="12" y2="12"></line>
                  <line x1="12" y1="16" x2="12.01" y2="16"></line>
                </svg>
                {{ couponError }}
              </div>
              <div v-if="couponApplied" class="flex items-center justify-between gap-2 text-sm bg-green-50 dark:bg-green-900/20 text-green-700 dark:text-green-400 px-3 py-2 rounded-lg mt-2">
                <div class="flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12"></polyline>
                  </svg>
                  Đã áp dụng mã "{{ couponCode }}" (-{{ couponDiscount }}%)
                </div>
                <button @click="removeCoupon" class="text-red-600 dark:text-red-400 hover:text-red-700 dark:hover:text-red-300 font-medium">Xóa</button>
              </div>
            </div>
            
            <div class="border-t border-gray-200 dark:border-gray-700 my-4"></div>

            <div class="space-y-3 mb-4">
              <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                <span>Tạm tính ({{ cart.totalItems }} sản phẩm)</span>
                <span class="text-gray-900 dark:text-gray-100">{{ formatPrice(cart.subTotal) }}</span>
              </div>

              <div v-if="couponApplied" class="flex justify-between text-sm text-green-600 dark:text-green-400">
                <span>Giảm giá (-{{ couponDiscount }}%)</span>
                <span>-{{ formatPrice(discountAmount) }}</span>
              </div>

              <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                <span>Phí vận chuyển</span>
                <span :class="shippingFee === 0 ? 'text-green-600 dark:text-green-400' : 'text-gray-900 dark:text-gray-100'">
                  {{ shippingFee === 0 ? 'Miễn phí' : formatPrice(shippingFee) }}
                </span>
              </div>
            </div>

            <div class="border-t border-gray-200 dark:border-gray-700 my-4"></div>

            <div class="flex justify-between items-center mb-6">
              <span class="text-lg font-semibold text-gray-900 dark:text-gray-100">Tổng cộng</span>
              <span class="text-2xl font-bold text-purple-600 dark:text-purple-400">{{ formatPrice(totalAmount) }}</span>
            </div>

            <button @click="proceedToCheckout" class="w-full flex items-center justify-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl mb-3">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14"></path>
                <path d="m12 5 7 7-7 7"></path>
              </svg>
              Tiến hành thanh toán
            </button>

            <router-link to="/home/products" class="w-full flex items-center justify-center px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors">
              Tiếp tục mua sắm
            </router-link>

            <!-- Free Shipping Indicator -->
            <div v-if="cart.subTotal >= 500000" class="mt-4 flex items-center gap-2 px-4 py-3 bg-green-50 dark:bg-green-900/20 text-green-700 dark:text-green-400 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="1" y="3" width="15" height="13"></rect>
                <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
                <circle cx="5.5" cy="18.5" r="2.5"></circle>
                <circle cx="18.5" cy="18.5" r="2.5"></circle>
              </svg>
              <span class="text-sm font-medium">Đơn hàng đủ điều kiện giao hàng miễn phí!</span>
            </div>
            <div v-else class="mt-4">
              <div class="h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden mb-2">
                <div class="h-full bg-gradient-to-r from-purple-500 to-indigo-500 transition-all duration-300" :style="{ width: shippingProgress + '%' }"></div>
              </div>
              <p class="text-xs text-gray-600 dark:text-gray-400 text-center">Mua thêm {{ formatPrice(500000 - cart.subTotal) }} để được giao hàng miễn phí</p>
            </div>
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
import userService from '@/services/userService';

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
    cart.value = await userService.getMyCart();
  } catch (error) {
    console.error('Error fetching cart:', error);
    ElMessage.error(error.message || 'Không thể tải giỏ hàng');
  } finally {
    loading.value = false;
  }
};

const updateQuantity = async (item, newQuantity) => {
  if (newQuantity < 1) return;

  try {
    await userService.addItemToCart({
      variantId: item.variantId,
      quantity: newQuantity,
    });
    await fetchCart();
    ElMessage.success('Đã cập nhật số lượng');
  } catch (error) {
    console.error('Error updating quantity:', error);
    ElMessage.error(error.message || 'Không thể cập nhật số lượng');
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

    await userService.removeItemFromCart(item.variantId);
    await fetchCart();
    ElMessage.success('Đã xóa sản phẩm khỏi giỏ hàng');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error removing item:', error);
      ElMessage.error(error.message || 'Không thể xóa sản phẩm');
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

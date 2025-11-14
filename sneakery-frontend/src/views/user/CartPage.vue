<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-7xl mx-auto px-4 space-y-6">
      <!-- Header -->
      <div class="relative overflow-hidden bg-gradient-to-r from-purple-600 via-purple-700 to-indigo-700 rounded-xl p-6 shadow-lg">
        <div class="absolute inset-0 bg-gradient-to-br from-purple-900/20 to-transparent"></div>
        <div class="relative">
          <h1 class="text-3xl md:text-4xl font-bold text-white mb-2 flex items-center gap-3">
            <div class="w-10 h-10 rounded-lg bg-white/20 backdrop-blur-sm flex items-center justify-center">
              <i class="material-icons text-white">shopping_cart</i>
            </div>
            Giỏ hàng của bạn
          </h1>
          <p class="text-purple-100 text-sm md:text-base" v-if="cart && cart.items.length > 0">
            {{ cart.totalItems }} sản phẩm trong giỏ hàng
          </p>
          <p class="text-purple-100 text-sm md:text-base" v-else>
            Quản lý các sản phẩm đã chọn
          </p>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="space-y-4" role="status" aria-live="polite">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div class="lg:col-span-2 space-y-4">
            <LoadingSkeleton
              v-for="n in 3"
              :key="n"
              type="list"
            />
          </div>
          <div>
            <LoadingSkeleton type="custom" :lines="5" />
          </div>
        </div>
        <span class="sr-only">Đang tải giỏ hàng</span>
      </div>

      <!-- Empty Cart -->
      <div v-else-if="!cart || cart.items.length === 0" class="text-center py-20 bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-900 rounded-xl border border-gray-200 dark:border-gray-700">
        <div class="w-24 h-24 mx-auto mb-6 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
          <i class="material-icons text-5xl text-purple-600 dark:text-purple-400">shopping_cart</i>
        </div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">Giỏ hàng trống</h2>
        <p class="text-gray-600 dark:text-gray-400 mb-8 max-w-md mx-auto">Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
        <router-link 
          to="/home/products" 
          class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02]"
        >
          <i class="material-icons text-lg">shopping_bag</i>
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
            class="group bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-5 hover:shadow-lg hover:border-purple-300 dark:hover:border-purple-600 transition-all duration-200 hover:translate-y-[-2px]"
          >
            <div class="flex flex-col sm:flex-row gap-4">
              <div class="w-full sm:w-28 h-28 rounded-lg overflow-hidden bg-gray-100 dark:bg-gray-700 flex-shrink-0">
                <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
              </div>

              <div class="flex-1 min-w-0 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                <div class="flex-1 min-w-0">
                  <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-1 line-clamp-2">{{ item.productName }}</h3>
                  <p class="text-sm text-gray-500 dark:text-gray-400 mb-2">{{ item.brandName }}</p>
                  <div class="flex flex-wrap gap-2 mb-3">
                    <span class="px-2.5 py-1 bg-gray-100 dark:bg-gray-700 rounded-lg text-xs text-gray-700 dark:text-gray-300 font-medium flex items-center gap-1">
                      <i class="material-icons text-xs">palette</i>
                      {{ item.color }}
                    </span>
                    <span class="px-2.5 py-1 bg-gray-100 dark:bg-gray-700 rounded-lg text-xs text-gray-700 dark:text-gray-300 font-medium flex items-center gap-1">
                      <i class="material-icons text-xs">straighten</i>
                      {{ item.size }}
                    </span>
                  </div>
                  <p class="font-semibold text-lg text-purple-600 dark:text-purple-400">{{ formatPrice(item.unitPrice) }}</p>
                </div>

                <div class="flex flex-col sm:flex-row sm:items-center gap-4 flex-shrink-0">
                  <!-- Quantity Controls -->
                  <div class="flex items-center gap-1 bg-gray-50 dark:bg-gray-700/50 rounded-lg p-1">
                    <button 
                      @click="updateQuantity(item, item.quantity - 1)" 
                      :disabled="item.quantity <= 1"
                      class="w-9 h-9 rounded-lg border border-gray-200 dark:border-gray-600 flex items-center justify-center hover:bg-gray-100 dark:hover:bg-gray-600 disabled:opacity-50 disabled:cursor-not-allowed transition-all focus:outline-none focus:ring-2 focus:ring-purple-500"
                      aria-label="Giảm số lượng"
                    >
                      <i class="material-icons text-base">remove</i>
                    </button>
                    <div class="w-12 h-9 flex items-center justify-center border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700">
                      <span class="text-base font-semibold text-gray-900 dark:text-gray-100">{{ item.quantity }}</span>
                    </div>
                    <button 
                      @click="updateQuantity(item, item.quantity + 1)"
                      class="w-9 h-9 rounded-lg border border-gray-200 dark:border-gray-600 flex items-center justify-center hover:bg-gray-100 dark:hover:bg-gray-600 transition-all focus:outline-none focus:ring-2 focus:ring-purple-500"
                      aria-label="Tăng số lượng"
                    >
                      <i class="material-icons text-base">add</i>
                    </button>
                  </div>

                  <!-- Total Price -->
                  <div class="text-right sm:text-left">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">Tổng</p>
                    <p class="font-bold text-xl text-gray-900 dark:text-gray-100">{{ formatPrice(item.totalPrice) }}</p>
                  </div>

                  <!-- Remove Button -->
                  <button 
                    @click="removeItem(item)" 
                    class="w-10 h-10 rounded-lg border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-all flex items-center justify-center flex-shrink-0"
                    aria-label="Xóa sản phẩm"
                  >
                    <i class="material-icons text-lg">delete</i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Cart Summary -->
        <div class="lg:col-span-1">
          <div class="bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 p-4 sm:p-6 lg:sticky lg:top-24">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-6 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">receipt</i>
              Tổng đơn hàng
            </h2>

            <!-- Coupon Input -->
            <div class="mb-6">
              <label class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">Mã giảm giá</label>
              <div class="flex gap-2 mb-2">
                <input
                  v-model="couponCode"
                  type="text"
                  placeholder="Nhập mã giảm giá"
                  class="flex-1 px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                  :disabled="couponApplied"
                />
                <button
                  @click="applyCoupon"
                  :disabled="!couponCode || couponApplied || applyingCoupon"
                  class="px-4 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
                >
                  <i v-if="!applyingCoupon && !couponApplied" class="material-icons text-lg">local_offer</i>
                  <div v-if="applyingCoupon" class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                  <span>{{ applyingCoupon ? '...' : (couponApplied ? '✓' : 'Áp dụng') }}</span>
                </button>
              </div>
              <div v-if="couponError" class="flex items-center gap-2 text-sm text-red-600 dark:text-red-400 mt-2 p-3 bg-red-50 dark:bg-red-900/20 rounded-lg">
                <i class="material-icons text-base">error</i>
                {{ couponError }}
              </div>
              <div v-if="couponApplied && appliedCoupon" class="flex items-center justify-between gap-2 text-sm bg-green-50 dark:bg-green-900/20 text-green-700 dark:text-green-400 px-4 py-3 rounded-lg mt-2 border border-green-200 dark:border-green-800">
                <div class="flex items-center gap-2">
                  <i class="material-icons text-base">check_circle</i>
                  <span class="font-medium">
                    Đã áp dụng mã "{{ appliedCoupon.code }}"
                    <span v-if="appliedCoupon.discountType === 'percent'">
                      (-{{ appliedCoupon.value }}%)
                    </span>
                    <span v-else>
                      (-{{ formatPrice(appliedCoupon.value) }})
                    </span>
                  </span>
                </div>
                <button @click="removeCoupon" class="text-red-600 dark:text-red-400 hover:text-red-700 dark:hover:text-red-300 font-medium transition-colors">
                  <i class="material-icons text-base">close</i>
                </button>
              </div>
            </div>
            
            <div class="border-t border-gray-200 dark:border-gray-700 my-4"></div>

            <div class="space-y-3 mb-4">
              <!-- Subtotal -->
              <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                <span class="flex items-center gap-2">
                  <i class="material-icons text-xs">inventory_2</i>
                  Tạm tính ({{ cart.totalItems }} sản phẩm)
                </span>
                <span class="text-gray-900 dark:text-gray-100 font-semibold">{{ formatPrice(cart.subTotal) }}</span>
              </div>

              <!-- Coupon Discount -->
              <div v-if="couponApplied && appliedCoupon" class="flex justify-between text-sm text-green-600 dark:text-green-400">
                <span class="flex items-center gap-2">
                  <i class="material-icons text-xs">local_offer</i>
                  Giảm giá ({{ appliedCoupon.code }})
                </span>
                <span class="font-semibold">-{{ formatPrice(discountAmount) }}</span>
              </div>

              <!-- Amount After Discount -->
              <div v-if="couponApplied && appliedCoupon" class="flex justify-between text-xs text-gray-500 dark:text-gray-500 pt-1 border-t border-gray-200 dark:border-gray-700">
                <span class="italic">Sau giảm giá</span>
                <span class="italic">{{ formatPrice(cart.subTotal - discountAmount) }}</span>
              </div>

              <!-- VAT (10%) -->
              <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                <span class="flex items-center gap-2">
                  <i class="material-icons text-xs">receipt</i>
                  VAT (10%)
                </span>
                <span class="text-gray-900 dark:text-gray-100 font-semibold">
                  {{ formatPrice(Math.max(0, (cart.subTotal - discountAmount) * 0.10)) }}
                </span>
              </div>

              <!-- Shipping Fee -->
              <div class="flex justify-between text-sm text-gray-600 dark:text-gray-400">
                <span class="flex items-center gap-2">
                  <i class="material-icons text-xs">local_shipping</i>
                  Phí vận chuyển
                </span>
                <span :class="[
                  'font-semibold',
                  shippingFee === 0 ? 'text-green-600 dark:text-green-400' : 'text-gray-900 dark:text-gray-100'
                ]">
                  {{ shippingFee === 0 ? 'Miễn phí' : formatPrice(shippingFee) }}
                </span>
              </div>
            </div>

            <div class="border-t-2 border-gray-300 dark:border-gray-600 my-4"></div>

            <div class="flex justify-between items-center mb-6 p-4 bg-gradient-to-br from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20 rounded-lg">
              <span class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                <i class="material-icons text-purple-600 dark:text-purple-400">attach_money</i>
                Tổng cộng
              </span>
              <span class="text-2xl font-bold text-purple-600 dark:text-purple-400">{{ formatPrice(totalAmount) }}</span>
            </div>

            <button 
              @click="proceedToCheckout" 
              class="w-full flex items-center justify-center gap-2 px-6 py-3.5 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] mb-3"
            >
              <i class="material-icons text-lg">shopping_bag</i>
              Tiến hành thanh toán
            </button>

            <router-link 
              to="/home/products" 
              class="w-full flex items-center justify-center gap-2 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-all"
            >
              <i class="material-icons text-lg">arrow_back</i>
              Tiếp tục mua sắm
            </router-link>

            <!-- Free Shipping Indicator -->
            <div v-if="cart.subTotal >= 500000" class="mt-4 flex items-center gap-2 px-4 py-3 bg-green-50 dark:bg-green-900/20 text-green-700 dark:text-green-400 rounded-lg border border-green-200 dark:border-green-800">
              <i class="material-icons text-lg">local_shipping</i>
              <span class="text-sm font-medium">Đơn hàng đủ điều kiện giao hàng miễn phí!</span>
            </div>
            <div v-else class="mt-4 p-4 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
              <div class="h-2 bg-gray-200 dark:bg-gray-600 rounded-full overflow-hidden mb-2">
                <div class="h-full bg-gradient-to-r from-purple-500 to-indigo-500 transition-all duration-300 rounded-full" :style="{ width: shippingProgress + '%' }"></div>
              </div>
              <p class="text-xs text-gray-600 dark:text-gray-400 text-center">
                <i class="material-icons text-xs align-middle">local_shipping</i>
                Mua thêm <span class="font-semibold text-purple-600 dark:text-purple-400">{{ formatPrice(500000 - cart.subTotal) }}</span> để được giao hàng miễn phí
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useCartStore } from '@/stores/cart';
import { useCouponStore } from '@/stores/coupon';
import notificationService from '@/utils/notificationService';
import confirmDialogService from '@/utils/confirmDialogService';
import logger from '@/utils/logger';
import couponService from '@/services/couponService';
import LoadingSkeleton from '@/components/common/LoadingSkeleton.vue';
import { formatPrice } from '@/utils/formatters';

const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();
const couponStore = useCouponStore();

// Local state
const applyingCoupon = ref(false);

// Use coupon store state
const couponCode = computed({
  get: () => couponStore.couponCode,
  set: (value) => { couponStore.couponCode = value; }
});
const couponApplied = computed(() => couponStore.hasCoupon);
const couponError = computed(() => couponStore.couponError);
const appliedCoupon = computed(() => couponStore.appliedCoupon);

// Computed từ cart store
const cart = computed(() => cartStore.cart);
const loading = computed(() => cartStore.loading);

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
  if (!cart.value || !couponApplied.value || !appliedCoupon.value) return 0;
  
  // Calculate discount based on coupon type
  return couponService.calculateDiscount(appliedCoupon.value, cart.value.subTotal);
});

// Calculate VAT (10% on amount after discount)
const taxAmount = computed(() => {
  if (!cart.value) return 0;
  const amountAfterDiscount = cart.value.subTotal - discountAmount.value;
  return Math.max(0, amountAfterDiscount * 0.10);
});

const totalAmount = computed(() => {
  if (!cart.value) return 0;
  // Backend logic:
  // 1. subtotal - discount = amountAfterDiscount
  // 2. VAT = amountAfterDiscount * 0.10
  // 3. total = amountAfterDiscount + shippingFee + VAT
  const amountAfterDiscount = cart.value.subTotal - discountAmount.value;
  return amountAfterDiscount + shippingFee.value + taxAmount.value;
});

// Methods
const fetchCart = async () => {
  try {
    await cartStore.fetchCart();
  } catch (error) {
    logger.error('Error fetching cart:', error);
    notificationService.error('Lỗi', error.message || 'Không thể tải giỏ hàng');
  }
};

const updateQuantity = async (item, newQuantity) => {
  if (newQuantity < 1) return;

  try {
    await cartStore.updateQuantity(item.variantId, newQuantity);
    notificationService.success('Thành công', 'Đã cập nhật số lượng');
  } catch (error) {
    logger.error('Error updating quantity:', error);
    notificationService.error('Lỗi', error.message || 'Không thể cập nhật số lượng');
  }
};

const removeItem = async (item) => {
  try {
    await confirmDialogService.confirm(
      `Bạn có chắc muốn xóa "${item.productName}" khỏi giỏ hàng?`,
      'Xác nhận',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
    );

    await cartStore.removeItem(item.variantId);
    notificationService.success('Thành công', 'Đã xóa sản phẩm khỏi giỏ hàng');
  } catch (error) {
    if (error !== 'cancel') {
      logger.error('Error removing item:', error);
      notificationService.error('Lỗi', error.message || 'Không thể xóa sản phẩm');
    }
  }
};

const applyCoupon = async () => {
  if (!couponCode.value || !cart.value) return;

  try {
    applyingCoupon.value = true;
    couponStore.setError('');

    // Validate coupon code from API
    const coupon = await couponService.validateCoupon(
      couponCode.value.trim(),
      cart.value.subTotal
    );

    // Store coupon info in store
    couponStore.setCoupon(couponCode.value.trim(), coupon);
    
    // Calculate discount
    const discount = couponService.calculateDiscount(coupon, cart.value.subTotal);
    
    notificationService.success(
      'Thành công',
      `Đã áp dụng mã giảm giá "${coupon.code}" - Giảm ${couponService.formatCurrency(discount)}`
    );
    
    logger.log('Coupon applied:', coupon.code, 'Discount:', discount);
  } catch (error) {
    logger.error('Error applying coupon:', error);
    couponStore.setError(error.message || 'Không thể áp dụng mã giảm giá');
    couponStore.clearCoupon();
  } finally {
    applyingCoupon.value = false;
  }
};

const removeCoupon = () => {
  couponStore.clearCoupon();
  notificationService.info('Thông tin','Đã xóa mã giảm giá');
};

const proceedToCheckout = async () => {
  try {
    await router.push('/checkout');
  } catch (error) {
    logger.error('Navigation error to checkout:', error);
    notificationService.error('Lỗi', 'Không thể mở trang thanh toán. Vui lòng thử lại sau.');
  }
};

// Watch cart subtotal changes - revalidate coupon if needed
watch(() => cart.value?.subTotal, async (newSubtotal) => {
  if (appliedCoupon.value && newSubtotal) {
    // Re-validate coupon when subtotal changes
    try {
      const coupon = await couponService.validateCoupon(
        couponCode.value,
        newSubtotal
      );
      // Update coupon in store if validation passes
      couponStore.setCoupon(couponCode.value, coupon);
    } catch (error) {
      // If validation fails, clear coupon
      logger.warn('Coupon validation failed after subtotal change:', error);
      couponStore.clearCoupon();
      notificationService.warning('Cảnh báo', 'Mã giảm giá không còn hợp lệ với giá trị đơn hàng mới');
    }
  }
});

// Lifecycle
onMounted(() => {
  // Initialize coupon store and load from storage
  couponStore.init();
  fetchCart();
});
</script>

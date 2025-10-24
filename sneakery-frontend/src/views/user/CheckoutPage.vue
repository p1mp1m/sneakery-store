<template>
  <div class="user-page checkout-page">
    <div class="checkout-container">
      <!-- Page Header -->
      <div class="page-header">
        <h1 class="page-title">Thanh toán</h1>
        <p class="page-subtitle">Hoàn tất đơn hàng của bạn</p>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner-lg"></div>
        <p>Đang tải thông tin...</p>
      </div>

      <!-- Checkout Content -->
      <div v-else class="checkout-grid">
        <!-- Left: Multi-Step Form -->
        <div class="checkout-steps">
          <!-- Step Progress Indicator -->
          <div class="step-progress">
            <div
              v-for="(step, index) in steps"
              :key="step.id"
              class="step-item"
              :class="{
                active: currentStep === index + 1,
                completed: currentStep > index + 1,
              }"
              @click="goToStep(index + 1)"
            >
              <div class="step-circle">
                <span v-if="currentStep > index + 1" class="step-check">✓</span>
                <span v-else>{{ index + 1 }}</span>
              </div>
              <div class="step-label">
                <div class="step-title">{{ step.title }}</div>
                <div class="step-desc">{{ step.description }}</div>
              </div>
            </div>
          </div>

          <!-- Step 1: Shipping Address -->
          <div v-show="currentStep === 1" class="form-step">
            <h2 class="step-heading">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              Địa chỉ giao hàng
            </h2>

            <div v-if="addresses.length === 0" class="no-data-state">
              <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              <p>Bạn chưa có địa chỉ giao hàng nào</p>
              <button @click="showAddressForm = true" class="btn btn-gradient">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
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
                <div class="card-radio">
                  <div class="radio-circle" :class="{ checked: selectedAddress === addr.id }">
                    <span v-if="selectedAddress === addr.id">✓</span>
                  </div>
                </div>
                <div class="card-content">
                  <h4>{{ addr.recipientName }}</h4>
                  <div class="address-details">
                    <div class="detail-item">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                      </svg>
                      {{ addr.phone }}
                    </div>
                    <div class="detail-item">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                        <polyline points="9 22 9 12 15 12 15 22"></polyline>
                      </svg>
                      {{ addr.line1 }}
                    </div>
                    <div v-if="addr.line2" class="detail-item">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                      </svg>
                      {{ addr.line2 }}
                    </div>
                    <div class="detail-item">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="2" y1="12" x2="22" y2="12"></line>
                        <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
                      </svg>
                      {{ addr.district }}, {{ addr.city }}
                    </div>
                  </div>
                </div>
              </div>

              <button @click="showAddressForm = true" class="btn btn-outline btn-add-address">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Thêm địa chỉ mới
              </button>
            </div>

            <div class="step-actions">
              <button
                @click="nextStep"
                :disabled="!selectedAddress"
                class="btn btn-gradient btn-lg btn-next"
              >
                Tiếp tục
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                  <polyline points="12 5 19 12 12 19"></polyline>
                </svg>
              </button>
            </div>
          </div>

          <!-- Step 2: Payment Method -->
          <div v-show="currentStep === 2" class="form-step">
            <h2 class="step-heading">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
                <line x1="1" y1="10" x2="23" y2="10"></line>
              </svg>
              Phương thức thanh toán
            </h2>

            <div class="payment-methods">
              <div
                :class="['payment-card', { selected: paymentMethod === 'cod' }]"
                @click="paymentMethod = 'cod'"
              >
                <div class="card-radio">
                  <div class="radio-circle" :class="{ checked: paymentMethod === 'cod' }">
                    <span v-if="paymentMethod === 'cod'">✓</span>
                  </div>
                </div>
                <div class="card-content">
                  <div class="payment-header">
                    <div class="payment-icon cod-icon">
                      <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="1" x2="12" y2="23"></line>
                        <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                      </svg>
                    </div>
                    <div>
                      <h4>Thanh toán khi nhận hàng (COD)</h4>
                      <p>Thanh toán bằng tiền mặt khi nhận hàng</p>
                    </div>
                  </div>
                  <div class="payment-features">
                    <span class="feature-badge">✓ Tiện lợi</span>
                    <span class="feature-badge">✓ Không mất phí</span>
                  </div>
                </div>
              </div>

              <div
                :class="['payment-card', { selected: paymentMethod === 'online' }]"
                @click="paymentMethod = 'online'"
              >
                <div class="card-radio">
                  <div class="radio-circle" :class="{ checked: paymentMethod === 'online' }">
                    <span v-if="paymentMethod === 'online'">✓</span>
                  </div>
                </div>
                <div class="card-content">
                  <div class="payment-header">
                    <div class="payment-icon online-icon">
                      <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
                        <line x1="1" y1="10" x2="23" y2="10"></line>
                      </svg>
                    </div>
                    <div>
                      <h4>Thanh toán trực tuyến</h4>
                      <p>Thanh toán qua VNPay, MoMo, thẻ ATM/Visa...</p>
                    </div>
                  </div>
                  <div class="payment-features">
                    <span class="feature-badge">✓ Nhanh chóng</span>
                    <span class="feature-badge">✓ Bảo mật</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="step-actions">
              <button @click="prevStep" class="btn btn-outline btn-lg">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="19" y1="12" x2="5" y2="12"></line>
                  <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                Quay lại
              </button>
              <button @click="nextStep" class="btn btn-gradient btn-lg btn-next">
                Tiếp tục
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                  <polyline points="12 5 19 12 12 19"></polyline>
                </svg>
              </button>
            </div>
          </div>

          <!-- Step 3: Review & Confirm -->
          <div v-show="currentStep === 3" class="form-step">
            <h2 class="step-heading">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="9 11 12 14 22 4"></polyline>
                <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>
              </svg>
              Xác nhận đơn hàng
            </h2>

            <div class="review-section">
              <div class="review-card">
                <div class="review-header">
                  <h3>Địa chỉ giao hàng</h3>
                  <button @click="currentStep = 1" class="btn-edit">Sửa</button>
                </div>
                <div class="review-content" v-if="selectedAddressData">
                  <div class="review-item">
                    <strong>{{ selectedAddressData.recipientName }}</strong>
                  </div>
                  <div class="review-item">{{ selectedAddressData.phone }}</div>
                  <div class="review-item">{{ selectedAddressData.line1 }}</div>
                  <div class="review-item" v-if="selectedAddressData.line2">{{ selectedAddressData.line2 }}</div>
                  <div class="review-item">{{ selectedAddressData.district }}, {{ selectedAddressData.city }}</div>
                </div>
              </div>

              <div class="review-card">
                <div class="review-header">
                  <h3>Phương thức thanh toán</h3>
                  <button @click="currentStep = 2" class="btn-edit">Sửa</button>
                </div>
                <div class="review-content">
                  <div class="review-item">
                    <strong v-if="paymentMethod === 'cod'">Thanh toán khi nhận hàng (COD)</strong>
                    <strong v-else>Thanh toán trực tuyến</strong>
                  </div>
                  <div class="review-item" v-if="paymentMethod === 'cod'">
                    Thanh toán bằng tiền mặt khi nhận hàng
                  </div>
                  <div class="review-item" v-else>
                    Thanh toán qua VNPay, MoMo, thẻ ATM/Visa...
                  </div>
                </div>
              </div>

              <div class="review-card">
                <div class="review-header">
                  <h3>Ghi chú (không bắt buộc)</h3>
                </div>
                <textarea
                  v-model="notes"
                  placeholder="Nhập ghi chú cho đơn hàng (nếu có)..."
                  class="notes-textarea"
                  rows="4"
                ></textarea>
              </div>
            </div>

            <div class="step-actions">
              <button @click="prevStep" class="btn btn-outline btn-lg">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="19" y1="12" x2="5" y2="12"></line>
                  <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                Quay lại
              </button>
              <button
                @click="handleCheckout"
                :disabled="processing"
                class="btn btn-gradient btn-lg btn-next btn-checkout-confirm"
              >
                <svg v-if="!processing" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
                <span v-if="processing">Đang xử lý...</span>
                <span v-else>Xác nhận đặt hàng</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Right: Order Summary -->
        <div class="order-summary-sticky">
          <div class="order-summary">
            <h2>
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="9" cy="21" r="1"></circle>
                <circle cx="20" cy="21" r="1"></circle>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
              </svg>
              Đơn hàng ({{ cart?.totalItems || 0 }} sản phẩm)
            </h2>

            <!-- Cart Items -->
            <div class="summary-items">
              <div
                v-for="item in cart?.items || []"
                :key="item.cartItemId"
                class="summary-item"
              >
                <div class="item-image">
                  <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" />
                </div>
                <div class="summary-item-info">
                  <p class="item-name">{{ item.productName }}</p>
                  <p class="item-variant">{{ item.size }} / {{ item.color }}</p>
                  <p class="item-quantity">Số lượng: {{ item.quantity }}</p>
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
                <span :class="{ 'free-text': shippingFee === 0 }">
                  {{ shippingFee === 0 ? 'Miễn phí' : formatPrice(shippingFee) }}
                </span>
              </div>

              <!-- Loyalty Points Section -->
              <div class="loyalty-points-section">
                <div class="loyalty-header">
                  <div class="loyalty-toggle">
                    <input
                      type="checkbox"
                      id="use-points"
                      v-model="usePoints"
                      :disabled="currentBalance === 0"
                      class="loyalty-checkbox"
                    />
                    <label for="use-points" class="loyalty-label">
                      <span class="material-icons">stars</span>
                      Sử dụng điểm thưởng
                    </label>
                  </div>
                  <span class="loyalty-balance">
                    {{ currentBalance.toLocaleString() }} điểm
                  </span>
                </div>

                <div v-if="usePoints" class="loyalty-control">
                  <div class="points-input-wrapper">
                    <input
                      type="number"
                      v-model.number="pointsToUse"
                      :max="maxPointsUsable"
                      :min="0"
                      class="points-input"
                      placeholder="Nhập số điểm"
                    />
                    <button
                      @click="pointsToUse = maxPointsUsable"
                      class="btn-use-max"
                    >
                      Dùng tối đa
                    </button>
                  </div>
                  <p class="points-info">
                    Tối đa {{ maxPointsUsable.toLocaleString() }} điểm (≈ {{ formatPrice(maxPointsUsable * 1000) }})
                  </p>
                </div>
              </div>

              <div v-if="loyaltyDiscount > 0" class="price-row discount-row">
                <span>Giảm giá từ điểm thưởng</span>
                <span class="discount-amount">-{{ formatPrice(loyaltyDiscount) }}</span>
              </div>

              <div class="price-divider"></div>
              <div class="price-row total">
                <span>Tổng cộng</span>
                <span class="total-amount">{{ formatPrice(totalAmount) }}</span>
              </div>
            </div>

            <!-- Trust Badges -->
            <div class="trust-badges">
              <div class="trust-badge">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                Thanh toán bảo mật
              </div>
              <div class="trust-badge">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                </svg>
                Chính hãng 100%
              </div>
              <div class="trust-badge">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="23 4 23 10 17 10"></polyline>
                  <polyline points="1 20 1 14 7 14"></polyline>
                  <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
                </svg>
                Đổi trả 30 ngày
              </div>
            </div>

            <p class="checkout-note">
              Bằng việc đặt hàng, bạn đồng ý với 
              <a href="#">Điều khoản sử dụng</a> của chúng tôi
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Address Modal -->
    <div v-if="showAddressForm" class="modal-overlay" @click.self="showAddressForm = false">
      <div class="modal">
        <div class="modal-header">
          <h3>Thêm địa chỉ mới</h3>
          <button @click="showAddressForm = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Họ tên người nhận *</label>
            <input
              v-model="newAddress.recipientName"
              type="text"
              class="form-control"
              placeholder="Nguyễn Văn A"
            />
          </div>
          <div class="form-group">
            <label>Số điện thoại *</label>
            <input
              v-model="newAddress.phone"
              type="tel"
              class="form-control"
              placeholder="0912345678"
            />
          </div>
          <div class="form-group">
            <label>Địa chỉ *</label>
            <input
              v-model="newAddress.line1"
              type="text"
              class="form-control"
              placeholder="123 Đường ABC"
            />
          </div>
          <div class="form-group">
            <label>Địa chỉ bổ sung (không bắt buộc)</label>
            <input
              v-model="newAddress.line2"
              type="text"
              class="form-control"
              placeholder="Căn hộ, tòa nhà..."
            />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Quận/Huyện *</label>
              <input
                v-model="newAddress.district"
                type="text"
                class="form-control"
                placeholder="Quận 1"
              />
            </div>
            <div class="form-group">
              <label>Tỉnh/Thành phố *</label>
              <input
                v-model="newAddress.city"
                type="text"
                class="form-control"
                placeholder="TP. Hồ Chí Minh"
              />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="saveAddress" class="btn btn-gradient">Lưu địa chỉ</button>
          <button @click="showAddressForm = false" class="btn btn-outline">Hủy</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useLoyaltyStore } from '@/stores/loyalty';
import { storeToRefs } from 'pinia';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const router = useRouter();
const authStore = useAuthStore();
const loyaltyStore = useLoyaltyStore();
const { currentBalance } = storeToRefs(loyaltyStore);

// Multi-step data
const steps = [
  { id: 1, title: 'Địa chỉ giao hàng', description: 'Chọn nơi nhận hàng' },
  { id: 2, title: 'Thanh toán', description: 'Chọn phương thức' },
  { id: 3, title: 'Xác nhận', description: 'Hoàn tất đơn hàng' },
];

// State
const currentStep = ref(1);
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
  line2: '',
  district: '',
  city: '',
});

// Loyalty Points
const usePoints = ref(false);
const pointsToUse = ref(0);
const maxPointsUsable = computed(() => {
  const maxFromBalance = currentBalance.value;
  const maxFromOrderValue = Math.floor((cart.value?.subTotal + shippingFee.value) / 1000); // 1 point = 1000 VND
  return Math.min(maxFromBalance, maxFromOrderValue);
});

// Computed
const loyaltyDiscount = computed(() => {
  if (!usePoints.value || pointsToUse.value === 0) return 0;
  return pointsToUse.value * 1000; // 1 point = 1000 VND
});

const totalAmount = computed(() => {
  if (!cart.value) return 0;
  const subtotal = cart.value.subTotal + shippingFee.value - loyaltyDiscount.value;
  return Math.max(subtotal, 0);
});

const selectedAddressData = computed(() => {
  return addresses.value.find((addr) => addr.id === selectedAddress.value);
});

// Methods
const goToStep = (step) => {
  if (step < currentStep.value) {
    currentStep.value = step;
  }
};

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++;
  }
};

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--;
  }
};

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
  if (
    !newAddress.value.recipientName ||
    !newAddress.value.phone ||
    !newAddress.value.line1 ||
    !newAddress.value.district ||
    !newAddress.value.city
  ) {
    ElMessage.warning('Vui lòng điền đầy đủ các trường bắt buộc');
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
      line2: '',
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

    // Redirect to orders
    setTimeout(() => {
      router.push({ name: 'orders' });
    }, 1500);
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

// Watch points input
watch(pointsToUse, (newVal) => {
  if (newVal > maxPointsUsable.value) {
    pointsToUse.value = maxPointsUsable.value;
  }
  if (newVal < 0) {
    pointsToUse.value = 0;
  }
});

// Lifecycle
onMounted(async () => {
  await fetchData();
  // Load loyalty balance
  await loyaltyStore.fetchBalance();
});
</script>

<style scoped>
/* ===== CHECKOUT PAGE - MODERN DARK THEME ===== */

.checkout-page {
  min-height: 100vh;
  background: transparent;
  padding: var(--space-6);
}

.checkout-container {
  max-width: 1400px;
  margin: 0 auto;
}

/* Page Header */
.page-header {
  margin-bottom: var(--space-8);
  text-align: center;
}

.page-title {
  font-size: 2.5rem;
  font-weight: var(--font-bold);
  color: #f1f5f9;
  margin: 0 0 var(--space-2);
}

.page-subtitle {
  font-size: var(--text-lg);
  color: #94a3b8;
  margin: 0;
}

/* Loading */
.loading-container {
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  color: #e2e8f0;
}

.loading-spinner-lg {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: #a78bfa;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Checkout Grid */
.checkout-grid {
  display: grid;
  grid-template-columns: 1fr 450px;
  gap: var(--space-8);
  align-items: start;
}

/* Step Progress */
.step-progress {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-8);
  padding: var(--space-6);
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(10px);
}

.step-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--space-3);
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
}

.step-item:not(:last-child)::after {
  content: '';
  position: absolute;
  right: calc(-50% + 40px);
  top: 20px;
  width: calc(100% - 80px);
  height: 2px;
  background: rgba(167, 139, 250, 0.2);
  z-index: 0;
}

.step-item.completed:not(:last-child)::after {
  background: linear-gradient(90deg, #a78bfa 0%, #8b5cf6 100%);
}

.step-circle {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(30, 41, 59, 0.6);
  border: 2px solid rgba(167, 139, 250, 0.3);
  font-weight: var(--font-bold);
  color: #94a3b8;
  font-size: var(--text-lg);
  transition: all var(--transition-fast);
  position: relative;
  z-index: 1;
}

.step-item.active .step-circle,
.step-item.completed .step-circle {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  border-color: #a78bfa;
  color: white;
  box-shadow: 0 4px 20px rgba(167, 139, 250, 0.4);
}

.step-check {
  font-size: 20px;
}

.step-label {
  flex: 1;
}

.step-title {
  font-weight: var(--font-semibold);
  color: #e2e8f0;
  font-size: var(--text-base);
  margin-bottom: var(--space-1);
}

.step-item.active .step-title {
  color: #a78bfa;
}

.step-desc {
  font-size: var(--text-sm);
  color: #64748b;
}

/* Form Steps */
.form-step {
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.step-heading {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: 1.75rem;
  font-weight: var(--font-bold);
  color: #f1f5f9;
  margin: 0 0 var(--space-6);
  padding: var(--space-5);
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
}

.step-heading svg {
  color: #a78bfa;
}

/* No Data State */
.no-data-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-10) var(--space-6);
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-lg);
  text-align: center;
}

.no-data-state svg {
  color: #64748b;
}

.no-data-state p {
  color: #94a3b8;
  font-size: var(--text-lg);
  margin: 0;
}

/* Address List */
.address-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.address-card {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-5);
  background: rgba(30, 41, 59, 0.4);
  border: 2px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  backdrop-filter: blur(10px);
}

.address-card:hover {
  border-color: rgba(167, 139, 250, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.address-card.selected {
  border-color: #a78bfa;
  background: rgba(167, 139, 250, 0.1);
  box-shadow: 0 0 30px rgba(167, 139, 250, 0.2);
}

.card-radio {
  flex-shrink: 0;
}

.radio-circle {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid rgba(167, 139, 250, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
  color: white;
  font-weight: var(--font-bold);
  font-size: 14px;
}

.radio-circle.checked {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  border-color: #a78bfa;
}

.card-content {
  flex: 1;
}

.card-content h4 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: #f1f5f9;
  margin: 0 0 var(--space-3);
}

.address-details {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: #cbd5e1;
  font-size: var(--text-sm);
}

.detail-item svg {
  flex-shrink: 0;
  color: #64748b;
}

.btn-add-address {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  width: 100%;
  padding: var(--space-4);
  font-weight: var(--font-semibold);
}

/* Payment Methods */
.payment-methods {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.payment-card {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-5);
  background: rgba(30, 41, 59, 0.4);
  border: 2px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  backdrop-filter: blur(10px);
}

.payment-card:hover {
  border-color: rgba(167, 139, 250, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.payment-card.selected {
  border-color: #a78bfa;
  background: rgba(167, 139, 250, 0.1);
  box-shadow: 0 0 30px rgba(167, 139, 250, 0.2);
}

.payment-header {
  display: flex;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
}

.payment-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.cod-icon {
  background: rgba(16, 185, 129, 0.15);
  color: #6ee7b7;
}

.online-icon {
  background: rgba(59, 130, 246, 0.15);
  color: #93c5fd;
}

.payment-header h4 {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: #f1f5f9;
  margin: 0 0 var(--space-1);
}

.payment-header p {
  font-size: var(--text-sm);
  color: #94a3b8;
  margin: 0;
}

.payment-features {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.feature-badge {
  padding: var(--space-1) var(--space-2);
  background: rgba(167, 139, 250, 0.15);
  border: 1px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  color: #c4b5fd;
  font-weight: var(--font-medium);
}

/* Review Section */
.review-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.review-card {
  padding: var(--space-5);
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
  padding-bottom: var(--space-3);
  border-bottom: 1px solid rgba(167, 139, 250, 0.1);
}

.review-header h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: #f1f5f9;
  margin: 0;
}

.btn-edit {
  padding: var(--space-1) var(--space-3);
  background: transparent;
  border: 1px solid rgba(167, 139, 250, 0.3);
  border-radius: var(--radius-md);
  color: #a78bfa;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-edit:hover {
  background: rgba(167, 139, 250, 0.15);
  border-color: #a78bfa;
}

.review-content {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.review-item {
  color: #cbd5e1;
  font-size: var(--text-sm);
  margin: 0;
}

.review-item strong {
  color: #f1f5f9;
  font-size: var(--text-base);
}

.notes-textarea {
  width: 100%;
  padding: var(--space-3);
  background: rgba(15, 23, 42, 0.6);
  border: 2px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-md);
  color: #f1f5f9;
  font-family: inherit;
  font-size: var(--text-base);
  resize: vertical;
  transition: all var(--transition-fast);
}

.notes-textarea:focus {
  outline: none;
  border-color: #a78bfa;
  background: rgba(15, 23, 42, 0.8);
}

.notes-textarea::placeholder {
  color: #64748b;
}

/* Step Actions */
.step-actions {
  display: flex;
  gap: var(--space-4);
  justify-content: flex-end;
  margin-top: var(--space-8);
  padding-top: var(--space-6);
  border-top: 1px solid rgba(167, 139, 250, 0.1);
}

.btn-next {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-6);
}

.btn-checkout-confirm {
  flex: 1;
}

/* Order Summary Sticky */
.order-summary-sticky {
  position: sticky;
  top: var(--space-6);
}

.order-summary {
  padding: var(--space-6);
  background: rgba(30, 41, 59, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.15);
  border-radius: var(--radius-xl);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.order-summary h2 {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: #f1f5f9;
  margin: 0 0 var(--space-5);
  padding-bottom: var(--space-4);
  border-bottom: 2px solid rgba(167, 139, 250, 0.2);
}

.order-summary h2 svg {
  color: #a78bfa;
}

.summary-items {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: var(--space-5);
  padding-right: var(--space-2);
}

.summary-items::-webkit-scrollbar {
  width: 6px;
}

.summary-items::-webkit-scrollbar-track {
  background: rgba(15, 23, 42, 0.4);
  border-radius: var(--radius-full);
}

.summary-items::-webkit-scrollbar-thumb {
  background: rgba(167, 139, 250, 0.3);
  border-radius: var(--radius-full);
}

.summary-items::-webkit-scrollbar-thumb:hover {
  background: rgba(167, 139, 250, 0.5);
}

.summary-item {
  display: flex;
  gap: var(--space-3);
  padding: var(--space-3) 0;
  border-bottom: 1px solid rgba(167, 139, 250, 0.1);
}

.summary-item:last-child {
  border-bottom: none;
}

.item-image {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(167, 139, 250, 0.1);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.summary-item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  color: #f1f5f9;
  margin: 0 0 var(--space-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-variant,
.item-quantity {
  font-size: var(--text-xs);
  color: #94a3b8;
  margin: 2px 0;
}

.item-price {
  font-weight: var(--font-semibold);
  color: #c4b5fd;
  white-space: nowrap;
  font-size: var(--text-sm);
  margin: 0;
}

/* Price Breakdown */
.price-breakdown {
  margin-top: var(--space-5);
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
  font-size: var(--text-base);
  color: #cbd5e1;
}

.free-text {
  color: #6ee7b7;
  font-weight: var(--font-semibold);
}

.price-divider {
  height: 1px;
  background: rgba(167, 139, 250, 0.2);
  margin: var(--space-4) 0;
}

.price-row.total {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  margin-top: var(--space-3);
  padding-top: var(--space-3);
}

.price-row.total span:first-child {
  color: #f1f5f9;
}

.total-amount {
  font-size: 1.75rem;
  font-weight: var(--font-bold);
  background: linear-gradient(135deg, #a78bfa 0%, #c4b5fd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Trust Badges */
.trust-badges {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-2);
  margin-top: var(--space-5);
  padding: var(--space-4);
  background: rgba(15, 23, 42, 0.4);
  border-radius: var(--radius-md);
}

.trust-badge {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: #94a3b8;
}

.trust-badge svg {
  flex-shrink: 0;
  color: #6ee7b7;
}

.checkout-note {
  text-align: center;
  font-size: var(--text-sm);
  color: #64748b;
  margin-top: var(--space-4);
}

.checkout-note a {
  color: #a78bfa;
  text-decoration: none;
  transition: color var(--transition-fast);
}

.checkout-note a:hover {
  color: #c4b5fd;
}

/* Loyalty Points Section */
.loyalty-points-section {
  margin: var(--space-4) 0;
  padding: var(--space-4);
  background: rgba(167, 139, 250, 0.05);
  border: 1px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-md);
}

.loyalty-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.loyalty-toggle {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.loyalty-checkbox {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: #a78bfa;
}

.loyalty-checkbox:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.loyalty-label {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: #f1f5f9;
  font-weight: var(--font-semibold);
  cursor: pointer;
  font-size: var(--text-sm);
}

.loyalty-label .material-icons {
  font-size: 18px;
  color: #ffd700;
}

.loyalty-balance {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-3);
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  color: white;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
}

.loyalty-control {
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.points-input-wrapper {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-2);
}

.points-input {
  flex: 1;
  padding: var(--space-2) var(--space-3);
  background: rgba(15, 23, 42, 0.6);
  border: 2px solid rgba(167, 139, 250, 0.3);
  border-radius: var(--radius-md);
  color: #f1f5f9;
  font-size: var(--text-base);
  transition: all var(--transition-fast);
}

.points-input:focus {
  outline: none;
  border-color: #a78bfa;
  background: rgba(15, 23, 42, 0.8);
}

.btn-use-max {
  padding: var(--space-2) var(--space-4);
  background: rgba(167, 139, 250, 0.2);
  border: 1px solid rgba(167, 139, 250, 0.4);
  border-radius: var(--radius-md);
  color: #c4b5fd;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.btn-use-max:hover {
  background: rgba(167, 139, 250, 0.3);
  border-color: #a78bfa;
}

.points-info {
  font-size: var(--text-xs);
  color: #94a3b8;
  margin: 0;
}

.discount-row {
  color: #6ee7b7;
}

.discount-amount {
  font-weight: var(--font-bold);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.2s ease-out;
}

.modal {
  background: rgba(30, 41, 59, 0.95);
  border: 1px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-xl);
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-5);
  border-bottom: 1px solid rgba(167, 139, 250, 0.15);
}

.modal-header h3 {
  margin: 0;
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: #f1f5f9;
}

.modal-close {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  font-size: 2rem;
  cursor: pointer;
  color: #64748b;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: rgba(239, 68, 68, 0.15);
  color: #fca5a5;
}

.modal-body {
  padding: var(--space-6);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.form-group {
  margin-bottom: var(--space-4);
}

.form-group label {
  display: block;
  margin-bottom: var(--space-2);
  font-weight: var(--font-semibold);
  color: #e2e8f0;
  font-size: var(--text-sm);
}

.form-control {
  width: 100%;
  padding: var(--space-3);
  background: rgba(15, 23, 42, 0.6);
  border: 2px solid rgba(167, 139, 250, 0.2);
  border-radius: var(--radius-md);
  color: #f1f5f9;
  font-size: var(--text-base);
  transition: all var(--transition-fast);
}

.form-control:focus {
  outline: none;
  border-color: #a78bfa;
  background: rgba(15, 23, 42, 0.8);
}

.form-control::placeholder {
  color: #64748b;
}

.modal-footer {
  padding: var(--space-5);
  border-top: 1px solid rgba(167, 139, 250, 0.15);
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
}

/* Responsive */
@media (max-width: 1200px) {
  .checkout-grid {
    grid-template-columns: 1fr;
    gap: var(--space-6);
  }

  .order-summary-sticky {
    position: static;
  }
}

@media (max-width: 768px) {
  .step-progress {
    flex-direction: column;
  }

  .step-item::after {
    display: none;
  }

  .step-actions {
    flex-direction: column-reverse;
  }

  .step-actions button {
    width: 100%;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: 2rem;
  }
}
</style>

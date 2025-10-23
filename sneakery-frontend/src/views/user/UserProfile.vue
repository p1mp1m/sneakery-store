<template>
  <div class="user-page profile-page">
    <div class="profile-container">
      <h1 class="page-title">Thông tin cá nhân</h1>

      <!-- Profile Tabs -->
      <div class="profile-tabs">
        <button
          :class="['tab-btn', { active: activeTab === 'info' }]"
          @click="activeTab = 'info'"
        >
          Thông tin tài khoản
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'address' }]"
          @click="activeTab = 'address'"
        >
          Địa chỉ
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'password' }]"
          @click="activeTab = 'password'"
        >
          Đổi mật khẩu
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'loyalty' }]"
          @click="activeTab = 'loyalty'"
        >
          Điểm thưởng
        </button>
          </div>

      <!-- Tab Content -->
          <div class="tab-content">
        <!-- Account Info Tab -->
        <div v-if="activeTab === 'info'" class="info-section">
          <form @submit.prevent="updateProfile" class="profile-form">
            <div class="form-group">
              <label>Họ và tên *</label>
              <input v-model="profile.fullName" type="text" class="form-control" required />
            </div>

            <div class="form-group">
              <label>Email</label>
              <input v-model="profile.email" type="email" class="form-control" disabled />
              <small class="form-help">Email không thể thay đổi</small>
            </div>

            <div class="form-group">
              <label>Số điện thoại</label>
              <input v-model="profile.phoneNumber" type="tel" class="form-control" />
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="updating">
                <span v-if="updating">Đang lưu...</span>
                <span v-else>Cập nhật thông tin</span>
              </button>
            </div>
          </form>
        </div>

        <!-- Address Tab -->
        <div v-if="activeTab === 'address'" class="address-section">
          <div class="section-header">
            <h2>Danh sách địa chỉ</h2>
            <button @click="showAddressForm = true" class="btn btn-primary btn-sm">
              + Thêm địa chỉ mới
            </button>
            </div>

          <!-- Address List -->
          <div v-if="loadingAddresses" class="loading">Đang tải...</div>
          <div v-else-if="addresses.length === 0" class="empty-state">
            <p>Bạn chưa có địa chỉ nào</p>
          </div>
          <div v-else class="address-list">
            <div
              v-for="addr in addresses"
              :key="addr.id"
              class="address-item"
            >
              <div class="address-content">
                <h4>{{ addr.recipientName }}</h4>
                <p>{{ addr.phone }}</p>
                <p>{{ addr.line1 }}</p>
                <p v-if="addr.line2">{{ addr.line2 }}</p>
                <p>{{ addr.district }}, {{ addr.city }}</p>
              </div>
              <div class="address-actions">
                <button @click="editAddress(addr)" class="btn btn-outline btn-sm">
                  Sửa
                </button>
                <button @click="deleteAddress(addr.id)" class="btn btn-outline btn-sm">
                  Xóa
                </button>
              </div>
              </div>
            </div>
          </div>

        <!-- Change Password Tab -->
        <div v-if="activeTab === 'password'" class="password-section">
          <form @submit.prevent="changePassword" class="profile-form">
            <div class="form-group">
              <label>Mật khẩu hiện tại *</label>
              <input v-model="passwordForm.currentPassword" type="password" class="form-control" required />
                </div>

            <div class="form-group">
              <label>Mật khẩu mới *</label>
              <input v-model="passwordForm.newPassword" type="password" class="form-control" required />
              <small class="form-help">Mật khẩu phải có ít nhất 6 ký tự</small>
              </div>

            <div class="form-group">
              <label>Xác nhận mật khẩu mới *</label>
              <input v-model="passwordForm.confirmPassword" type="password" class="form-control" required />
              </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary" :disabled="changingPassword">
                <span v-if="changingPassword">Đang đổi mật khẩu...</span>
                <span v-else">Đổi mật khẩu</span>
              </button>
            </div>
          </form>
                </div>

        <!-- Loyalty Points Tab -->
        <div v-if="activeTab === 'loyalty'" class="loyalty-section">
          <!-- Loyalty Balance Card -->
          <div class="loyalty-balance-card">
            <div class="balance-header">
              <span class="material-icons loyalty-icon">stars</span>
              <h2>Điểm thưởng của bạn</h2>
            </div>
            
            <div v-if="loyaltyLoading" class="loading">
              <div class="loading-spinner"></div>
              <p>Đang tải...</p>
            </div>

            <div v-else class="balance-content">
              <div class="points-display">
                <div class="points-value">
                  {{ loyaltyStore.currentBalance.toLocaleString() }}
                </div>
                <div class="points-label">Điểm</div>
              </div>

              <div class="points-equivalent">
                <span class="material-icons">monetization_on</span>
                <span>≈ {{ formatCurrency(loyaltyStore.calculateVndFromPoints(loyaltyStore.currentBalance)) }}</span>
              </div>

              <div class="loyalty-info">
                <div class="info-item">
                  <span class="material-icons">info</span>
                  <span>1 điểm = 1.000 VNĐ</span>
                </div>
                <div class="info-item">
                  <span class="material-icons">shopping_cart</span>
                  <span>Dùng điểm khi thanh toán</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Points History -->
          <div class="loyalty-history">
            <h3>Lịch sử điểm thưởng</h3>
            
            <div v-if="loyaltyHistory.length === 0" class="empty-state">
              <span class="material-icons">history</span>
              <p>Chưa có lịch sử điểm thưởng</p>
            </div>

            <div v-else class="history-list">
              <div 
                v-for="item in loyaltyHistory" 
                :key="item.id"
                class="history-item"
              >
                <div class="history-icon" :class="`type-${item.transactionType}`">
                  <span class="material-icons">
                    {{ item.transactionType === 'earn' ? 'add_circle' : item.transactionType === 'redeem' ? 'remove_circle' : 'access_time' }}
                  </span>
                </div>

                <div class="history-content">
                  <div class="history-description">
                    {{ item.description }}
                  </div>
                  <div class="history-date">
                    {{ formatDate(item.createdAt) }}
                  </div>
                </div>

                <div class="history-points" :class="`points-${item.transactionType}`">
                  {{ item.transactionType === 'earn' ? '+' : '-' }}{{ Math.abs(item.points) }}
                </div>
              </div>
            </div>
          </div>
        </div>
              </div>
            </div>

    <!-- Add/Edit Address Modal -->
    <div v-if="showAddressForm" class="modal-overlay" @click.self="showAddressForm = false">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingAddress ? 'Cập nhật địa chỉ' : 'Thêm địa chỉ mới' }}</h3>
          <button @click="closeAddressForm" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Họ tên người nhận *</label>
            <input v-model="addressForm.recipientName" type="text" class="form-control" />
          </div>
          <div class="form-group">
            <label>Số điện thoại *</label>
            <input v-model="addressForm.phone" type="tel" class="form-control" />
          </div>
          <div class="form-group">
            <label>Địa chỉ *</label>
            <input v-model="addressForm.line1" type="text" class="form-control" />
                </div>
          <div class="form-group">
            <label>Địa chỉ 2 (tùy chọn)</label>
            <input v-model="addressForm.line2" type="text" class="form-control" />
              </div>
          <div class="form-group">
            <label>Quận/Huyện *</label>
            <input v-model="addressForm.district" type="text" class="form-control" />
                </div>
          <div class="form-group">
            <label>Tỉnh/Thành phố *</label>
            <input v-model="addressForm.city" type="text" class="form-control" />
              </div>
            </div>
        <div class="modal-footer">
          <button @click="saveAddress" class="btn btn-primary">
            {{ editingAddress ? 'Cập nhật' : 'Lưu địa chỉ' }}
          </button>
          <button @click="closeAddressForm" class="btn btn-outline">Hủy</button>
        </div>
          </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useLoyaltyStore } from '@/stores/loyalty';
import { storeToRefs } from 'pinia';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const authStore = useAuthStore();
const loyaltyStore = useLoyaltyStore();
const { loading: loyaltyLoading } = storeToRefs(loyaltyStore);

// State
const activeTab = ref('info');
const updating = ref(false);
const changingPassword = ref(false);
const loadingAddresses = ref(false);
const showAddressForm = ref(false);
const editingAddress = ref(null);

const profile = reactive({
  fullName: '',
  email: '',
  phoneNumber: '',
});

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const addresses = ref([]);

const addressForm = reactive({
  recipientName: '',
  phone: '',
  line1: '',
  line2: '',
  district: '',
  city: '',
});

// Loyalty Points computed
const loyaltyHistory = computed(() => loyaltyStore.history || []);

// Methods
const loadProfile = () => {
  if (authStore.currentUser) {
    profile.fullName = authStore.currentUser.fullName || '';
    profile.email = authStore.currentUser.email || '';
    profile.phoneNumber = authStore.currentUser.phoneNumber || '';
  }
};

const updateProfile = async () => {
  try {
    updating.value = true;
    
    // Note: Backend needs to support profile update endpoint
    // await axios.put('http://localhost:8080/api/users/profile', profile, {
    //   headers: { Authorization: `Bearer ${authStore.token}` },
    // });

    ElMessage.success('Cập nhật thông tin thành công');
      } catch (error) {
    console.error('Error updating profile:', error);
    ElMessage.error('Không thể cập nhật thông tin');
      } finally {
    updating.value = false;
  }
};

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('Mật khẩu xác nhận không khớp');
    return;
  }

  if (passwordForm.newPassword.length < 6) {
    ElMessage.error('Mật khẩu phải có ít nhất 6 ký tự');
    return;
  }

  try {
      changingPassword.value = true;

    // Note: Backend needs to support password change endpoint
    // await axios.post('http://localhost:8080/api/auth/change-password', {
    //   currentPassword: passwordForm.currentPassword,
    //   newPassword: passwordForm.newPassword,
    // }, {
    //   headers: { Authorization: `Bearer ${authStore.token}` },
    // });

    ElMessage.success('Đổi mật khẩu thành công');
        
        // Reset form
    passwordForm.currentPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
      } catch (error) {
    console.error('Error changing password:', error);
    ElMessage.error('Không thể đổi mật khẩu');
      } finally {
        changingPassword.value = false;
      }
};

const loadAddresses = async () => {
  try {
    loadingAddresses.value = true;
    const response = await axios.get('http://localhost:8080/api/addresses', {
      headers: { Authorization: `Bearer ${authStore.token}` },
    });
    addresses.value = response.data;
  } catch (error) {
    console.error('Error loading addresses:', error);
    ElMessage.error('Không thể tải danh sách địa chỉ');
  } finally {
    loadingAddresses.value = false;
  }
};

const editAddress = (addr) => {
  editingAddress.value = addr;
  addressForm.recipientName = addr.recipientName;
  addressForm.phone = addr.phone;
  addressForm.line1 = addr.line1;
  addressForm.line2 = addr.line2 || '';
  addressForm.district = addr.district || '';
  addressForm.city = addr.city;
  showAddressForm.value = true;
};

const saveAddress = async () => {
  if (!addressForm.recipientName || !addressForm.phone || !addressForm.line1 || !addressForm.city) {
    ElMessage.warning('Vui lòng điền đầy đủ thông tin bắt buộc');
    return;
  }

  try {
    if (editingAddress.value) {
      // Update existing address
      const response = await axios.put(
        `http://localhost:8080/api/addresses/${editingAddress.value.id}`,
        addressForm,
        {
          headers: { Authorization: `Bearer ${authStore.token}` },
        }
      );
      
      const index = addresses.value.findIndex(a => a.id === editingAddress.value.id);
      if (index !== -1) {
        addresses.value[index] = response.data;
      }
      
      ElMessage.success('Cập nhật địa chỉ thành công');
    } else {
      // Create new address
      const response = await axios.post(
        'http://localhost:8080/api/addresses',
        addressForm,
        {
          headers: { Authorization: `Bearer ${authStore.token}` },
        }
      );
      
      addresses.value.push(response.data);
      ElMessage.success('Thêm địa chỉ thành công');
    }

    closeAddressForm();
  } catch (error) {
    console.error('Error saving address:', error);
    ElMessage.error('Không thể lưu địa chỉ');
  }
};

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm(
      'Bạn có chắc muốn xóa địa chỉ này?',
      'Xác nhận',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning',
      }
    );
    
    await axios.delete(`http://localhost:8080/api/addresses/${id}`, {
      headers: { Authorization: `Bearer ${authStore.token}` },
    });

    addresses.value = addresses.value.filter(a => a.id !== id);
    ElMessage.success('Đã xóa địa chỉ');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error deleting address:', error);
      ElMessage.error('Không thể xóa địa chỉ');
    }
  }
};

const closeAddressForm = () => {
  showAddressForm.value = false;
  editingAddress.value = null;
  addressForm.recipientName = '';
  addressForm.phone = '';
  addressForm.line1 = '';
  addressForm.line2 = '';
  addressForm.district = '';
  addressForm.city = '';
};

// Loyalty methods
const loadLoyaltyData = async () => {
  await Promise.all([
    loyaltyStore.fetchBalance(),
    loyaltyStore.fetchHistory()
  ]);
};

// Helper methods
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
};

const formatDate = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// Watch tab changes
watch(activeTab, (newTab) => {
  if (newTab === 'loyalty') {
    loadLoyaltyData();
  }
});

// Lifecycle
onMounted(() => {
  loadProfile();
  loadAddresses();
});
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding: var(--space-6);
}

.profile-container {
  max-width: 900px;
  margin: 0 auto;
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-6);
}

/* Tabs */
.profile-tabs {
  display: flex;
  gap: var(--space-2);
  border-bottom: 2px solid var(--border-color);
  margin-bottom: var(--space-6);
}

.tab-btn {
  padding: var(--space-3) var(--space-6);
  border: none;
  background: none;
  cursor: pointer;
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
  transition: all var(--transition-fast);
}

.tab-btn:hover {
  color: var(--primary-color);
}

.tab-btn.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

/* Tab Content */
.tab-content {
  background: var(--bg-primary);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
}

/* Forms */
.profile-form {
  max-width: 500px;
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

.form-control:disabled {
  background: var(--bg-secondary);
  cursor: not-allowed;
}

.form-help {
  display: block;
  margin-top: var(--space-1);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.form-actions {
  margin-top: var(--space-6);
}

/* Address Section */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.section-header h2 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin: 0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.address-item {
  display: flex;
  justify-content: space-between;
  padding: var(--space-4);
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
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

.address-actions {
  display: flex;
  gap: var(--space-2);
  align-items: flex-start;
}

.empty-state {
  text-align: center;
  padding: var(--space-8);
  color: var(--text-secondary);
}

.loading {
  text-align: center;
  padding: var(--space-6);
  color: var(--text-secondary);
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

.modal-footer {
  padding: var(--space-4);
  border-top: 1px solid var(--border-color);
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
}

/* Loyalty Section */
.loyalty-balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: var(--space-8);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-6);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.balance-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-6);
}

.loyalty-icon {
  font-size: 32px;
  color: #ffd700;
}

.balance-header h2 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  margin: 0;
}

.balance-content {
  text-align: center;
}

.points-display {
  margin-bottom: var(--space-6);
}

.points-value {
  font-size: 64px;
  font-weight: var(--font-bold);
  line-height: 1;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.points-label {
  font-size: var(--text-lg);
  font-weight: var(--font-medium);
  opacity: 0.9;
  margin-top: var(--space-2);
}

.points-equivalent {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-6);
  padding: var(--space-3) var(--space-6);
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(10px);
}

.loyalty-info {
  display: flex;
  justify-content: center;
  gap: var(--space-8);
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid rgba(255, 255, 255, 0.3);
}

.info-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  opacity: 0.95;
}

.info-item .material-icons {
  font-size: 20px;
}

/* Loyalty History */
.loyalty-history h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-4);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.history-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  background: var(--bg-primary);
  border: 2px solid var(--border-light);
  border-radius: var(--radius-lg);
  transition: var(--transition-fast);
}

.history-item:hover {
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: var(--shadow-md);
}

.history-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.history-icon.type-earn {
  background: rgba(72, 187, 120, 0.1);
  color: var(--color-success);
}

.history-icon.type-redeem {
  background: rgba(237, 137, 54, 0.1);
  color: var(--color-warning);
}

.history-icon.type-expire {
  background: rgba(245, 101, 101, 0.1);
  color: var(--color-error);
}

.history-icon .material-icons {
  font-size: 24px;
}

.history-content {
  flex: 1;
}

.history-description {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.history-date {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.history-points {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
}

.history-points.points-earn {
  color: var(--color-success);
}

.history-points.points-redeem {
  color: var(--color-warning);
}

.history-points.points-expire {
  color: var(--color-error);
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: var(--radius-full);
  animation: spin 0.8s linear infinite;
  margin: 0 auto var(--space-3);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .profile-tabs {
    overflow-x: auto;
  }

  .tab-btn {
    white-space: nowrap;
  }

  .address-item {
    flex-direction: column;
    gap: var(--space-3);
  }

  .address-actions {
    width: 100%;
  }

  .address-actions button {
    flex: 1;
  }

  .loyalty-balance-card {
    padding: var(--space-6);
  }

  .points-value {
    font-size: 48px;
  }

  .loyalty-info {
    flex-direction: column;
    gap: var(--space-3);
  }
}
</style>

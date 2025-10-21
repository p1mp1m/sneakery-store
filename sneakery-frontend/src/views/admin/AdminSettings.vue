<template>
  <div class="admin-settings">
    <div class="page-header">
      <h1 class="page-title">Cài đặt hệ thống</h1>
      <p class="page-subtitle">Quản lý cấu hình cửa hàng và hệ thống</p>
    </div>

    <!-- Settings Navigation Tabs -->
    <div class="settings-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        @click="activeTab = tab.id"
        :class="['tab-btn', { active: activeTab === tab.id }]"
      >
        <i class="material-icons">{{ tab.icon }}</i>
        {{ tab.label }}
      </button>
    </div>

    <!-- Store Settings -->
    <div v-if="activeTab === 'store'" class="settings-section">
      <div class="section-header">
        <h2 class="section-title">Thông tin cửa hàng</h2>
        <p class="section-subtitle">Cấu hình thông tin cơ bản của cửa hàng</p>
      </div>

      <div class="settings-card">
        <div class="form-group">
          <label class="form-label">Tên cửa hàng</label>
          <input
            v-model="storeSettings.name"
            type="text"
            class="form-control"
            placeholder="Sneakery Store"
          />
        </div>

        <div class="form-group">
          <label class="form-label">Slogan</label>
          <input
            v-model="storeSettings.slogan"
            type="text"
            class="form-control"
            placeholder="Your Perfect Sneakers Destination"
          />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">Email liên hệ</label>
            <input
              v-model="storeSettings.email"
              type="email"
              class="form-control"
              placeholder="contact@sneakerystore.com"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Số điện thoại</label>
            <input
              v-model="storeSettings.phone"
              type="tel"
              class="form-control"
              placeholder="(+84) 123-456-789"
            />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Địa chỉ</label>
          <textarea
            v-model="storeSettings.address"
            class="form-control"
            rows="3"
            placeholder="Nhập địa chỉ cửa hàng..."
          ></textarea>
        </div>

        <div class="form-actions">
          <button @click="saveStoreSettings" class="btn btn-primary">
            <i class="material-icons">save</i>
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>

    <!-- General Settings -->
    <div v-if="activeTab === 'general'" class="settings-section">
      <div class="section-header">
        <h2 class="section-title">Cài đặt chung</h2>
        <p class="section-subtitle">Cấu hình chung cho hệ thống</p>
      </div>

      <div class="settings-card">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">Đơn vị tiền tệ</label>
            <select v-model="generalSettings.currency" class="form-control">
              <option value="VND">VND - Việt Nam Đồng</option>
              <option value="USD">USD - US Dollar</option>
              <option value="EUR">EUR - Euro</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Múi giờ</label>
            <select v-model="generalSettings.timezone" class="form-control">
              <option value="Asia/Ho_Chi_Minh">Việt Nam (UTC+7)</option>
              <option value="America/New_York">New York (UTC-5)</option>
              <option value="Europe/London">London (UTC+0)</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">Ngôn ngữ mặc định</label>
            <select v-model="generalSettings.language" class="form-control">
              <option value="vi">Tiếng Việt</option>
              <option value="en">English</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Số sản phẩm mỗi trang</label>
            <input
              v-model.number="generalSettings.productsPerPage"
              type="number"
              class="form-control"
              min="4"
              max="24"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-check">
            <input
              v-model="generalSettings.maintenanceMode"
              type="checkbox"
              id="maintenance"
              class="form-check-input"
            />
            <label for="maintenance" class="form-check-label">
              Bật chế độ bảo trì (Khách hàng sẽ không truy cập được website)
            </label>
          </div>
        </div>

        <div class="form-actions">
          <button @click="saveGeneralSettings" class="btn btn-primary">
            <i class="material-icons">save</i>
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>

    <!-- Email Settings -->
    <div v-if="activeTab === 'email'" class="settings-section">
      <div class="section-header">
        <h2 class="section-title">Cài đặt Email</h2>
        <p class="section-subtitle">Cấu hình SMTP để gửi email tự động</p>
      </div>

      <div class="settings-card">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">SMTP Host</label>
            <input
              v-model="emailSettings.smtpHost"
              type="text"
              class="form-control"
              placeholder="smtp.gmail.com"
            />
          </div>

          <div class="form-group">
            <label class="form-label">SMTP Port</label>
            <input
              v-model.number="emailSettings.smtpPort"
              type="number"
              class="form-control"
              placeholder="587"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">Email gửi đi</label>
            <input
              v-model="emailSettings.fromEmail"
              type="email"
              class="form-control"
              placeholder="noreply@sneakerystore.com"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Tên hiển thị</label>
            <input
              v-model="emailSettings.fromName"
              type="text"
              class="form-control"
              placeholder="Sneakery Store"
            />
          </div>
        </div>

        <div class="form-group">
          <div class="form-check">
            <input
              v-model="emailSettings.enableNotifications"
              type="checkbox"
              id="email-notifications"
              class="form-check-input"
            />
            <label for="email-notifications" class="form-check-label">
              Gửi email thông báo cho admin khi có đơn hàng mới
            </label>
          </div>
        </div>

        <div class="form-actions">
          <button @click="saveEmailSettings" class="btn btn-primary">
            <i class="material-icons">save</i>
            Lưu thay đổi
          </button>
          <button @click="testEmail" class="btn btn-secondary">
            <i class="material-icons">email</i>
            Gửi email test
          </button>
        </div>
      </div>
    </div>

    <!-- Payment Settings -->
    <div v-if="activeTab === 'payment'" class="settings-section">
      <div class="section-header">
        <h2 class="section-title">Cài đặt Thanh toán</h2>
        <p class="section-subtitle">Quản lý các phương thức thanh toán</p>
      </div>

      <div class="settings-card">
        <div class="payment-method">
          <div class="payment-header">
            <div>
              <h3>Thanh toán khi nhận hàng (COD)</h3>
              <p>Khách hàng thanh toán tiền mặt khi nhận hàng</p>
            </div>
            <label class="switch">
              <input v-model="paymentSettings.cod.enabled" type="checkbox" />
              <span class="slider"></span>
            </label>
          </div>
        </div>

        <div class="payment-method">
          <div class="payment-header">
            <div>
              <h3>Chuyển khoản ngân hàng</h3>
              <p>Khách hàng chuyển khoản trước khi nhận hàng</p>
            </div>
            <label class="switch">
              <input v-model="paymentSettings.bankTransfer.enabled" type="checkbox" />
              <span class="slider"></span>
            </label>
          </div>

          <div v-if="paymentSettings.bankTransfer.enabled" class="payment-details">
            <div class="form-group">
              <label class="form-label">Tên ngân hàng</label>
              <input
                v-model="paymentSettings.bankTransfer.bankName"
                type="text"
                class="form-control"
                placeholder="Vietcombank"
              />
            </div>
            <div class="form-group">
              <label class="form-label">Số tài khoản</label>
              <input
                v-model="paymentSettings.bankTransfer.accountNumber"
                type="text"
                class="form-control"
                placeholder="1234567890"
              />
            </div>
            <div class="form-group">
              <label class="form-label">Chủ tài khoản</label>
              <input
                v-model="paymentSettings.bankTransfer.accountName"
                type="text"
                class="form-control"
                placeholder="SNEAKERY STORE CO., LTD"
              />
            </div>
          </div>
        </div>

        <div class="payment-method">
          <div class="payment-header">
            <div>
              <h3>Thanh toán online (VNPay, MoMo...)</h3>
              <p>Tích hợp cổng thanh toán trực tuyến</p>
            </div>
            <label class="switch">
              <input v-model="paymentSettings.online.enabled" type="checkbox" />
              <span class="slider"></span>
            </label>
          </div>

          <div v-if="paymentSettings.online.enabled" class="payment-details">
            <p class="text-muted">
              <i class="material-icons" style="vertical-align: middle; font-size: 18px;">info</i>
              Tính năng này sẽ được phát triển trong phiên bản tiếp theo
            </p>
          </div>
        </div>

        <div class="form-actions">
          <button @click="savePaymentSettings" class="btn btn-primary">
            <i class="material-icons">save</i>
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('store')

const tabs = [
  { id: 'store', label: 'Cửa hàng', icon: 'store' },
  { id: 'general', label: 'Chung', icon: 'settings' },
  { id: 'email', label: 'Email', icon: 'email' },
  { id: 'payment', label: 'Thanh toán', icon: 'payment' }
]

// Store Settings
const storeSettings = ref({
  name: 'Sneakery Store',
  slogan: 'Your Perfect Sneakers Destination',
  email: 'contact@sneakerystore.com',
  phone: '(+84) 123-456-789',
  address: '123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh'
})

// General Settings
const generalSettings = ref({
  currency: 'VND',
  timezone: 'Asia/Ho_Chi_Minh',
  language: 'vi',
  productsPerPage: 12,
  maintenanceMode: false
})

// Email Settings
const emailSettings = ref({
  smtpHost: 'smtp.gmail.com',
  smtpPort: 587,
  fromEmail: 'noreply@sneakerystore.com',
  fromName: 'Sneakery Store',
  enableNotifications: true
})

// Payment Settings
const paymentSettings = ref({
  cod: {
    enabled: true
  },
  bankTransfer: {
    enabled: true,
    bankName: 'Vietcombank',
    accountNumber: '1234567890',
    accountName: 'SNEAKERY STORE CO., LTD'
  },
  online: {
    enabled: false
  }
})

// Load settings from localStorage
const loadSettings = () => {
  const saved = localStorage.getItem('adminSettings')
  if (saved) {
    try {
      const data = JSON.parse(saved)
      if (data.store) storeSettings.value = { ...storeSettings.value, ...data.store }
      if (data.general) generalSettings.value = { ...generalSettings.value, ...data.general }
      if (data.email) emailSettings.value = { ...emailSettings.value, ...data.email }
      if (data.payment) paymentSettings.value = { ...paymentSettings.value, ...data.payment }
    } catch (error) {
      console.error('Error loading settings:', error)
    }
  }
}

// Save methods
const saveStoreSettings = () => {
  const settings = JSON.parse(localStorage.getItem('adminSettings') || '{}')
  settings.store = storeSettings.value
  localStorage.setItem('adminSettings', JSON.stringify(settings))
  ElMessage.success('Đã lưu thông tin cửa hàng thành công!')
}

const saveGeneralSettings = () => {
  const settings = JSON.parse(localStorage.getItem('adminSettings') || '{}')
  settings.general = generalSettings.value
  localStorage.setItem('adminSettings', JSON.stringify(settings))
  ElMessage.success('Đã lưu cài đặt chung thành công!')
}

const saveEmailSettings = () => {
  const settings = JSON.parse(localStorage.getItem('adminSettings') || '{}')
  settings.email = emailSettings.value
  localStorage.setItem('adminSettings', JSON.stringify(settings))
  ElMessage.success('Đã lưu cài đặt email thành công!')
}

const savePaymentSettings = () => {
  const settings = JSON.parse(localStorage.getItem('adminSettings') || '{}')
  settings.payment = paymentSettings.value
  localStorage.setItem('adminSettings', JSON.stringify(settings))
  ElMessage.success('Đã lưu cài đặt thanh toán thành công!')
}

const testEmail = () => {
  ElMessage.info('Tính năng gửi email test đang được phát triển...')
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
/* ===== ADMIN SETTINGS PAGE ===== */
.admin-settings {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  padding-top: 90px;
}

.page-header {
  margin-bottom: 2rem;
}

.page-title {
  font-size: 1.875rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem;
}

.page-subtitle {
  color: #64748b;
  margin: 0;
}

/* ===== TABS ===== */
.settings-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  border-bottom: 2px solid #e2e8f0;
  overflow-x: auto;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.875rem 1.5rem;
  border: none;
  background: none;
  color: #64748b;
  font-size: 0.9375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  white-space: nowrap;
}

.tab-btn i {
  font-size: 1.25rem;
}

.tab-btn:hover {
  color: #3b82f6;
}

.tab-btn.active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
}

/* ===== SETTINGS SECTION ===== */
.settings-section {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-header {
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 0.5rem;
}

.section-subtitle {
  color: #64748b;
  margin: 0;
  font-size: 0.9375rem;
}

.settings-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

/* ===== FORM ELEMENTS ===== */
.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: #475569;
  margin-bottom: 0.5rem;
}

.form-control {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9375rem;
  transition: all 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-check {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.form-check-input {
  width: 1.25rem;
  height: 1.25rem;
  cursor: pointer;
}

.form-check-label {
  font-size: 0.9375rem;
  color: #475569;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #e2e8f0;
}

/* ===== BUTTONS ===== */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn i {
  font-size: 1.125rem;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

/* ===== PAYMENT METHODS ===== */
.payment-method {
  padding: 1.5rem;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 1.5rem;
}

.payment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.payment-header h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 0.25rem;
}

.payment-header p {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
}

.payment-details {
  padding-top: 1.5rem;
  border-top: 1px solid #e2e8f0;
}

/* ===== TOGGLE SWITCH ===== */
.switch {
  position: relative;
  display: inline-block;
  width: 52px;
  height: 28px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #cbd5e1;
  transition: 0.3s;
  border-radius: 28px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 22px;
  width: 22px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #3b82f6;
}

input:checked + .slider:before {
  transform: translateX(24px);
}

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .settings-tabs {
    gap: 0.25rem;
  }

  .tab-btn {
    padding: 0.625rem 1rem;
    font-size: 0.875rem;
  }

  .tab-btn i {
    font-size: 1.125rem;
  }

  .settings-card {
    padding: 1.5rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }

  .payment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}
</style>

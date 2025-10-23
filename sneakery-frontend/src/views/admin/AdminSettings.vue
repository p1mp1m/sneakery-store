<template>
  <div class="admin-page admin-settings">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div>
        <h1 class="page-title">
          <i class="material-icons">settings</i>
          Cài đặt hệ thống
        </h1>
        <p class="page-subtitle">Quản lý cấu hình cửa hàng và hệ thống</p>
      </div>
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
            <label class="toggle-switch">
              <input v-model="paymentSettings.cod.enabled" type="checkbox" />
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>

        <div class="payment-method">
          <div class="payment-header">
            <div>
              <h3>Chuyển khoản ngân hàng</h3>
              <p>Khách hàng chuyển khoản trước khi nhận hàng</p>
            </div>
            <label class="toggle-switch">
              <input v-model="paymentSettings.bankTransfer.enabled" type="checkbox" />
              <span class="toggle-slider"></span>
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
            <label class="toggle-switch">
              <input v-model="paymentSettings.online.enabled" type="checkbox" />
              <span class="toggle-slider"></span>
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
/* ===== ADMIN SETTINGS PAGE - UNIFIED DARK THEME ===== */

/* Page Layout */
.admin-settings {
  padding: var(--space-8);
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* Page Header */
.page-header {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.page-title .material-icons {
  font-size: 2rem;
  color: var(--accent-primary);
}

.page-subtitle {
  color: var(--text-secondary);
  margin: 0;
  font-size: var(--text-base);
}

/* Settings Tabs */
.settings-tabs {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-8);
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-2);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  overflow-x: auto;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  background: transparent;
  border: none;
  border-radius: var(--radius-lg);
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.tab-btn .material-icons {
  font-size: 1.25rem;
}

.tab-btn:hover {
  color: var(--text-primary);
  background: rgba(167, 139, 250, 0.1);
}

.tab-btn.active {
  background: var(--gradient-primary);
  color: white;
  box-shadow: 0 2px 12px rgba(167, 139, 250, 0.3);
}

/* Settings Section */
.settings-section {
  animation: fadeIn 0.4s ease;
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

/* Section Header */
.section-header {
  margin-bottom: var(--space-6);
}

.section-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.section-subtitle {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin: 0;
}

/* Settings Card */
.settings-card {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* Form Elements */
.form-group {
  margin-bottom: var(--space-5);
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.form-control {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-control:hover {
  border-color: var(--border-hover);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
  background: rgba(15, 23, 42, 0.8);
}

.form-control::placeholder {
  color: var(--text-tertiary);
}

textarea.form-control {
  resize: vertical;
  min-height: 100px;
}

/* Form Row (2 columns) */
.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-4);
}

/* Form Check (Checkbox) */
.form-check {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
  padding: var(--space-4);
  background: rgba(15, 23, 42, 0.4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  transition: all 0.3s ease;
}

.form-check:hover {
  background: rgba(167, 139, 250, 0.05);
  border-color: var(--border-hover);
}

.form-check-input {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: var(--accent-primary);
  flex-shrink: 0;
  margin-top: 2px;
}

.form-check-label {
  flex: 1;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  user-select: none;
  line-height: 1.5;
}

/* Payment Methods */
.payment-method {
  padding: var(--space-6);
  background: rgba(15, 23, 42, 0.4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  margin-bottom: var(--space-4);
  transition: all 0.3s ease;
}

.payment-method:hover {
  background: rgba(15, 23, 42, 0.6);
  border-color: var(--border-hover);
  box-shadow: var(--shadow-md);
}

.payment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: 0;
}

.payment-header h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
}

.payment-header p {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin: 0;
}

/* Toggle Switch */
.toggle-switch {
  position: relative;
  display: inline-block;
  width: 56px;
  height: 28px;
  flex-shrink: 0;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.8);
  border: 2px solid var(--border-primary);
  transition: all 0.3s ease;
  border-radius: var(--radius-full);
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background: var(--text-quaternary);
  transition: all 0.3s ease;
  border-radius: var(--radius-full);
}

.toggle-switch input:checked + .toggle-slider {
  background: var(--gradient-primary);
  border-color: var(--accent-primary);
  box-shadow: 0 0 12px rgba(167, 139, 250, 0.4);
}

.toggle-switch input:checked + .toggle-slider:before {
  transform: translateX(28px);
  background: white;
}

.toggle-switch:hover .toggle-slider {
  border-color: var(--accent-primary);
}

/* Payment Details */
.payment-details {
  padding-top: var(--space-5);
  margin-top: var(--space-5);
  border-top: 1px solid var(--border-primary);
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    max-height: 0;
  }
  to {
    opacity: 1;
    max-height: 500px;
  }
}

.text-muted {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3);
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: var(--radius-lg);
}

.text-muted .material-icons {
  color: var(--info-text);
  font-size: 1.125rem;
}

/* Form Actions */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

/* Buttons */
.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  white-space: nowrap;
}

.btn-primary {
  background: var(--gradient-primary);
  color: white;
  box-shadow: var(--shadow-btn);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-secondary {
  background: rgba(15, 23, 42, 0.6);
  color: var(--text-secondary);
  border: 1px solid var(--border-primary);
}

.btn-secondary:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--accent-primary);
  color: var(--accent-primary);
}

.btn .material-icons {
  font-size: 1.125rem;
}

/* Animations */
.animate-fade-in {
  animation: fadeIn 0.5s ease;
}

/* Responsive */
@media (max-width: 1024px) {
  .admin-settings {
    max-width: 900px;
  }
}

@media (max-width: 768px) {
  .admin-settings {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-6);
  }

  .page-title {
    font-size: var(--text-2xl);
  }

  .settings-tabs {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .tab-btn {
    padding: var(--space-2) var(--space-4);
    font-size: var(--text-xs);
  }

  .tab-btn .material-icons {
    font-size: 1.125rem;
  }

  .settings-card {
    padding: var(--space-5);
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .payment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-4);
  }

  .toggle-switch {
    align-self: flex-start;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions .btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .settings-tabs {
    gap: var(--space-1);
    padding: var(--space-1);
  }

  .tab-btn {
    flex: 1;
    justify-content: center;
    padding: var(--space-2) var(--space-2);
  }

  .tab-btn .material-icons {
    display: none;
  }
}
</style>

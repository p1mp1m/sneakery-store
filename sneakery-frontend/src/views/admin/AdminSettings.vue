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
/* Minimal custom CSS - sử dụng chủ yếu từ admin-panel.css */

.page-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.page-title .material-icons {
  font-size: 2rem;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Form Group - override để compatibility */
.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--admin-text-secondary);
  margin-bottom: 0.5rem;
}

/* Payment Methods - Custom styles */
.payment-method {
  padding: 1.5rem;
  background: var(--admin-bg-primary);
  border: 1px solid var(--admin-border);
  border-radius: var(--admin-radius-lg);
  margin-bottom: 1.5rem;
  transition: var(--admin-transition);
}

.payment-method:hover {
  box-shadow: var(--admin-shadow-md);
}

.payment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0;
}

.payment-header h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--admin-text-primary);
  margin: 0 0 0.25rem;
}

.payment-header p {
  font-size: 0.875rem;
  color: var(--admin-text-secondary);
  margin: 0;
}

.payment-details {
  padding-top: 1.5rem;
  margin-top: 1.5rem;
  border-top: 1px solid var(--admin-border);
}

.text-muted {
  color: var(--admin-text-muted);
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .payment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}
</style>

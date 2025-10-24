<template>
  <div class="admin-page admin-settings">
    <!-- Enhanced Page Header with Glassmorphism -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">settings</span>
            Cài đặt hệ thống
          </h1>
          <p class="page-subtitle">Quản lý cấu hình cửa hàng và hệ thống</p>
        </div>
        
        <div class="header-actions">
          <button @click="showSearch = !showSearch" class="btn btn-secondary">
            <span class="material-icons">search</span>
            Tìm kiếm
          </button>
          <button @click="exportSettings" class="btn btn-secondary">
            <span class="material-icons">file_download</span>
            Xuất cấu hình
          </button>
          <button @click="importSettings" class="btn btn-secondary">
            <span class="material-icons">file_upload</span>
            Nhập cấu hình
          </button>
          <button @click="resetToDefaults" class="btn btn-danger">
            <span class="material-icons">restore</span>
            Khôi phục mặc định
          </button>
        </div>
      </div>

      <!-- Search Bar -->
      <transition name="slide-down">
        <div v-if="showSearch" class="search-bar">
          <div class="search-box">
            <span class="material-icons search-icon">search</span>
            <input 
              v-model="searchQuery" 
              type="text" 
              class="search-input"
              placeholder="Tìm kiếm cài đặt..."
              @input="handleSearch"
            />
            <button v-if="searchQuery" @click="clearSearch" class="search-clear">
              <span class="material-icons">close</span>
            </button>
          </div>
          
          <!-- Search Results -->
          <div v-if="searchResults.length > 0 && searchQuery" class="search-results">
            <div 
              v-for="result in searchResults" 
              :key="result.id"
              @click="goToSetting(result)"
              class="search-result-item"
            >
              <span class="material-icons">{{ result.icon }}</span>
              <div class="result-info">
                <strong>{{ result.title }}</strong>
                <p>{{ result.description }}</p>
              </div>
            </div>
          </div>
          
          <div v-else-if="searchQuery && searchResults.length === 0" class="no-results">
            <span class="material-icons">search_off</span>
            <p>Không tìm thấy cài đặt nào</p>
          </div>
        </div>
      </transition>
    </div>

    <!-- Enhanced Settings Navigation Tabs -->
    <div class="settings-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        @click="activeTab = tab.id"
        :class="['tab-btn', { active: activeTab === tab.id }]"
      >
        <span class="material-icons">{{ tab.icon }}</span>
        <span class="tab-label">{{ tab.label }}</span>
        <span v-if="tab.badge" class="tab-badge">{{ tab.badge }}</span>
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
            <span class="material-icons">save</span>
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
            <span class="material-icons">save</span>
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
            <span class="material-icons">save</span>
            Lưu thay đổi
          </button>
          <button @click="testEmail" class="btn btn-secondary">
            <span class="material-icons">email</span>
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
              <span class="material-icons" style="vertical-align: middle; font-size: 18px;">info</span>
              Tính năng này sẽ được phát triển trong phiên bản tiếp theo
            </p>
          </div>
        </div>

        <div class="form-actions">
          <button @click="savePaymentSettings" class="btn btn-primary">
            <span class="material-icons">save</span>
            Lưu thay đổi
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { downloadJson } from '@/utils/exportHelpers'

const adminStore = useAdminStore()

const activeTab = ref('store')
const showSearch = ref(false)
const searchQuery = ref('')
const searchResults = ref([])

const tabs = [
  { id: 'store', label: 'Cửa hàng', icon: 'store' },
  { id: 'general', label: 'Chung', icon: 'settings' },
  { id: 'email', label: 'Email', icon: 'email' },
  { id: 'payment', label: 'Thanh toán', icon: 'payment' }
]

// Searchable settings index
const settingsIndex = [
  { id: 'store-name', title: 'Tên cửa hàng', description: 'Thông tin cửa hàng', tab: 'store', icon: 'store' },
  { id: 'store-email', title: 'Email liên hệ', description: 'Thông tin cửa hàng', tab: 'store', icon: 'email' },
  { id: 'store-phone', title: 'Số điện thoại', description: 'Thông tin cửa hàng', tab: 'store', icon: 'phone' },
  { id: 'general-currency', title: 'Đơn vị tiền tệ', description: 'Cài đặt chung', tab: 'general', icon: 'attach_money' },
  { id: 'general-language', title: 'Ngôn ngữ', description: 'Cài đặt chung', tab: 'general', icon: 'language' },
  { id: 'general-maintenance', title: 'Chế độ bảo trì', description: 'Cài đặt chung', tab: 'general', icon: 'build' },
  { id: 'email-smtp', title: 'SMTP Host', description: 'Cài đặt Email', tab: 'email', icon: 'mail_outline' },
  { id: 'email-notifications', title: 'Thông báo Email', description: 'Cài đặt Email', tab: 'email', icon: 'notifications' },
  { id: 'payment-cod', title: 'Thanh toán khi nhận hàng', description: 'Cài đặt Thanh toán', tab: 'payment', icon: 'payments' },
  { id: 'payment-bank', title: 'Chuyển khoản ngân hàng', description: 'Cài đặt Thanh toán', tab: 'payment', icon: 'account_balance' },
  { id: 'payment-online', title: 'Thanh toán online', description: 'Cài đặt Thanh toán', tab: 'payment', icon: 'credit_card' }
]

// Store Settings
const storeSettings = ref({
  name: '',
  slogan: '',
  email: '',
  phone: '',
  address: ''
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
  smtpHost: '',
  smtpPort: 587,
  fromEmail: '',
  fromName: '',
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

// Load settings from localStorage (mock data)
const loadSettings = async () => {
  try {
    // Mock data cho settings
    const mockSettings = {
      store: {
        storeName: 'Sneakery Store',
        storeDescription: 'Cửa hàng giày sneaker hàng đầu Việt Nam',
        storeAddress: '123 Đường ABC, Quận 1, TP.HCM',
        storePhone: '0123456789',
        storeEmail: 'info@sneakerystore.com',
        storeWebsite: 'https://sneakerystore.com',
        storeLogo: '/logo.png',
        currency: 'VND',
        timezone: 'Asia/Ho_Chi_Minh',
        language: 'vi'
      },
      general: {
        maintenanceMode: false,
        allowRegistration: true,
        requireEmailVerification: true,
        maxLoginAttempts: 5,
        sessionTimeout: 30,
        enableNotifications: true,
        enableAnalytics: true,
        enableCookies: true,
        privacyPolicy: 'Chính sách bảo mật của Sneakery Store...',
        termsOfService: 'Điều khoản sử dụng của Sneakery Store...'
      },
      email: {
        smtpHost: 'smtp.gmail.com',
        smtpPort: 587,
        smtpUsername: 'noreply@sneakerystore.com',
        smtpPassword: '********',
        smtpEncryption: 'tls',
        fromName: 'Sneakery Store',
        fromEmail: 'noreply@sneakerystore.com',
        enableEmailNotifications: true,
        enableOrderEmails: true,
        enableMarketingEmails: false
      },
      payment: {
        enableCashOnDelivery: true,
        enableBankTransfer: true,
        enableCreditCard: true,
        enablePayPal: false,
        enableVNPay: true,
        enableMomo: true,
        enableZaloPay: false,
        defaultPaymentMethod: 'vnpay',
        currency: 'VND',
        taxRate: 10,
        shippingFee: 30000,
        freeShippingThreshold: 500000
      }
    }
    
    // Load from localStorage hoặc sử dụng mock data
    const savedSettings = localStorage.getItem('adminSettings')
    if (savedSettings) {
      const parsed = JSON.parse(savedSettings)
      if (parsed.store) storeSettings.value = { ...storeSettings.value, ...parsed.store }
      if (parsed.general) generalSettings.value = { ...generalSettings.value, ...parsed.general }
      if (parsed.email) emailSettings.value = { ...emailSettings.value, ...parsed.email }
      if (parsed.payment) paymentSettings.value = { ...paymentSettings.value, ...parsed.payment }
    } else {
      // Sử dụng mock data lần đầu
      storeSettings.value = { ...storeSettings.value, ...mockSettings.store }
      generalSettings.value = { ...generalSettings.value, ...mockSettings.general }
      emailSettings.value = { ...emailSettings.value, ...mockSettings.email }
      paymentSettings.value = { ...paymentSettings.value, ...mockSettings.payment }
    }
    
    console.log('✅ Settings loaded successfully')
  } catch (error) {
    console.error('Error loading settings:', error)
    ElMessage.error('Không thể tải cài đặt')
  }
}

// Save methods
const saveStoreSettings = async () => {
  try {
    // Save to localStorage
    const currentSettings = JSON.parse(localStorage.getItem('adminSettings') || '{}')
    currentSettings.store = storeSettings.value
    localStorage.setItem('adminSettings', JSON.stringify(currentSettings))
    
    ElMessage.success('Đã lưu thông tin cửa hàng thành công!')
  } catch (error) {
    console.error('Error saving store settings:', error)
    ElMessage.error('Không thể lưu thông tin cửa hàng')
  }
}

const saveGeneralSettings = async () => {
  try {
    // Save to localStorage
    const currentSettings = JSON.parse(localStorage.getItem('adminSettings') || '{}')
    currentSettings.general = generalSettings.value
    localStorage.setItem('adminSettings', JSON.stringify(currentSettings))
    
    ElMessage.success('Đã lưu cài đặt chung thành công!')
  } catch (error) {
    console.error('Error saving general settings:', error)
    ElMessage.error('Không thể lưu cài đặt chung')
  }
}

const saveEmailSettings = async () => {
  try {
    // Save to localStorage
    const currentSettings = JSON.parse(localStorage.getItem('adminSettings') || '{}')
    currentSettings.email = emailSettings.value
    localStorage.setItem('adminSettings', JSON.stringify(currentSettings))
    
    ElMessage.success('Đã lưu cài đặt email thành công!')
  } catch (error) {
    console.error('Error saving email settings:', error)
    ElMessage.error('Không thể lưu cài đặt email')
  }
}

const savePaymentSettings = async () => {
  try {
    // Save to localStorage
    const currentSettings = JSON.parse(localStorage.getItem('adminSettings') || '{}')
    currentSettings.payment = paymentSettings.value
    localStorage.setItem('adminSettings', JSON.stringify(currentSettings))
    
    ElMessage.success('Đã lưu cài đặt thanh toán thành công!')
  } catch (error) {
    console.error('Error saving payment settings:', error)
    ElMessage.error('Không thể lưu cài đặt thanh toán')
  }
}

const testEmail = () => {
  ElMessage.info('Tính năng gửi email test đang được phát triển...')
}

// Search functionality
const handleSearch = () => {
  if (!searchQuery.value) {
    searchResults.value = []
    return
  }
  
  const query = searchQuery.value.toLowerCase()
  searchResults.value = settingsIndex.filter(item => 
    item.title.toLowerCase().includes(query) ||
    item.description.toLowerCase().includes(query)
  )
}

const clearSearch = () => {
  searchQuery.value = ''
  searchResults.value = []
}

const goToSetting = (result) => {
  activeTab.value = result.tab
  searchQuery.value = ''
  searchResults.value = []
  showSearch.value = false
  ElMessage.success(`Đã chuyển đến: ${result.title}`)
}

// Export/Import Settings
const exportSettings = () => {
  const allSettings = {
    store: storeSettings.value,
    general: generalSettings.value,
    email: emailSettings.value,
    payment: paymentSettings.value,
    exportedAt: new Date().toISOString(),
    version: '1.0'
  }
  
  downloadJson(allSettings, `sneakery-settings-${new Date().toISOString().split('T')[0]}.json`)
  ElMessage.success('Đã xuất cấu hình thành công!')
}

const importSettings = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json'
  
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    const reader = new FileReader()
    reader.onload = (event) => {
      try {
        const data = JSON.parse(event.target.result)
        
        // Validate structure
        if (!data.store || !data.general || !data.email || !data.payment) {
          throw new Error('Invalid settings file structure')
        }
        
        ElMessageBox.confirm(
          'Bạn có chắc muốn nhập cấu hình này? Các cài đặt hiện tại sẽ bị ghi đè.',
          'Xác nhận nhập cấu hình',
          {
            confirmButtonText: 'Nhập',
            cancelButtonText: 'Hủy',
            type: 'warning'
          }
        ).then(() => {
          storeSettings.value = { ...storeSettings.value, ...data.store }
          generalSettings.value = { ...generalSettings.value, ...data.general }
          emailSettings.value = { ...emailSettings.value, ...data.email }
          paymentSettings.value = { ...paymentSettings.value, ...data.payment }
          
          // Save to localStorage
          const settings = {
            store: storeSettings.value,
            general: generalSettings.value,
            email: emailSettings.value,
            payment: paymentSettings.value
          }
          localStorage.setItem('adminSettings', JSON.stringify(settings))
          
          ElMessage.success('Đã nhập cấu hình thành công!')
        }).catch(() => {
          ElMessage.info('Đã hủy nhập cấu hình')
        })
      } catch (error) {
        console.error('Error importing settings:', error)
        ElMessage.error('Lỗi khi nhập cấu hình. Vui lòng kiểm tra file JSON.')
      }
    }
    reader.readAsText(file)
  }
  
  input.click()
}

// Reset to defaults
const resetToDefaults = () => {
  ElMessageBox.confirm(
    'Bạn có chắc muốn khôi phục tất cả cài đặt về mặc định? Hành động này không thể hoàn tác.',
    'Xác nhận khôi phục',
    {
      confirmButtonText: 'Khôi phục',
      cancelButtonText: 'Hủy',
      type: 'warning'
    }
  ).then(() => {
    // Reset all settings to defaults
    storeSettings.value = {
      name: 'Sneakery Store',
      slogan: 'Your Perfect Sneakers Destination',
      email: 'contact@sneakerystore.com',
      phone: '(+84) 123-456-789',
      address: '123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh'
    }
    
    generalSettings.value = {
      currency: 'VND',
      timezone: 'Asia/Ho_Chi_Minh',
      language: 'vi',
      productsPerPage: 12,
      maintenanceMode: false
    }
    
    emailSettings.value = {
      smtpHost: 'smtp.gmail.com',
      smtpPort: 587,
      fromEmail: 'noreply@sneakerystore.com',
      fromName: 'Sneakery Store',
      enableNotifications: true
    }
    
    paymentSettings.value = {
      cod: { enabled: true },
      bankTransfer: {
        enabled: true,
        bankName: 'Vietcombank',
        accountNumber: '1234567890',
        accountName: 'SNEAKERY STORE CO., LTD'
      },
      online: { enabled: false }
    }
    
    // Clear localStorage
    localStorage.removeItem('adminSettings')
    
    ElMessage.success('Đã khôi phục cài đặt mặc định thành công!')
  }).catch(() => {
    ElMessage.info('Đã hủy khôi phục')
  })
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
/* ═══════════════════════════════════════════════════════════════════════
   ADMIN SETTINGS - Enhanced with Design System v2.0
   ═══════════════════════════════════════════════════════════════════════ */

/* Page Header Enhancements */
.page-header {
  background: var(--card-bg);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-2xl);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: var(--shadow-card);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-6);
  flex-wrap: wrap;
}

.title-section {
  flex: 1;
  min-width: 250px;
}

.header-actions {
  display: flex;
  gap: var(--space-3);
  flex-wrap: wrap;
}

/* Search Bar */
.search-bar {
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

.search-box {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto var(--space-4);
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  font-size: 1.25rem;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: var(--space-3) var(--space-12) var(--space-3) var(--space-12);
  border: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.6);
  border-radius: var(--radius-lg);
  font-size: var(--text-base);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

.search-input:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.8);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.9);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.search-clear {
  position: absolute;
  right: var(--space-2);
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.search-clear:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

/* Search Results */
.search-results {
  background: var(--card-bg);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  max-height: 400px;
  overflow-y: auto;
  box-shadow: var(--shadow-lg);
}

.search-result-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  cursor: pointer;
  transition: all var(--transition-fast);
  border-bottom: 1px solid var(--border-primary);
}

.search-result-item:last-child {
  border-bottom: none;
}

.search-result-item:hover {
  background: var(--gradient-purple-soft);
}

.search-result-item .material-icons {
  font-size: 1.5rem;
  color: var(--accent-primary);
}

.result-info {
  flex: 1;
}

.result-info strong {
  display: block;
  color: var(--text-primary);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-1);
}

.result-info p {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0;
}

.no-results {
  text-align: center;
  padding: var(--space-12);
  color: var(--text-tertiary);
}

.no-results .material-icons {
  font-size: 3rem;
  opacity: 0.4;
  margin-bottom: var(--space-3);
}

.no-results p {
  margin: 0;
  font-size: var(--text-base);
}

/* Settings Tabs */
.settings-tabs {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-6);
  background: var(--card-bg);
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
  position: relative;
}

.tab-btn .material-icons {
  font-size: 1.25rem;
}

.tab-label {
  display: inline-block;
}

.tab-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 var(--space-1-5);
  background: var(--accent-primary);
  color: white;
  font-size: 10px;
  border-radius: var(--radius-full);
  font-weight: var(--font-bold);
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

.tab-btn.active .tab-badge {
  background: rgba(255, 255, 255, 0.3);
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

/* Slide Down Animation for Search */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Settings Sections */
.settings-section {
  animation: fadeInUp 0.5s ease-out;
}

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

.settings-card {
  background: var(--card-bg);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: var(--shadow-card);
  transition: all var(--transition-normal);
}

.settings-card:hover {
  border-color: var(--border-hover);
  box-shadow: var(--shadow-glow-purple);
}

/* Responsive Design */
@media (max-width: 1024px) {
  .header-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: var(--space-4);
  }
  
  .header-actions {
    flex-direction: column;
  }
  
  .header-actions .btn {
    width: 100%;
    justify-content: center;
  }
  
  .search-box {
    max-width: 100%;
  }
  
  .settings-tabs {
    flex-direction: column;
    align-items: stretch;
  }
  
  .tab-btn {
    width: 100%;
    justify-content: flex-start;
  }
  
  .settings-card {
    padding: var(--space-4);
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .btn {
    width: 100%;
  }
  
  .payment-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .toggle-switch {
    margin-top: var(--space-3);
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: var(--text-2xl);
  }
  
  .page-subtitle {
    font-size: var(--text-xs);
  }
  
  .tab-label {
    display: none;
  }
  
  .tab-btn {
    padding: var(--space-3);
    justify-content: center;
  }
  
  .tab-btn .material-icons {
    font-size: 1.5rem;
  }
}
</style>

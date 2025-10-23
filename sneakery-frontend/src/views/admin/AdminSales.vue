<template>
  <div class="admin-page admin-sales">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">point_of_sale</span>
            Bán Hàng (POS)
          </h1>
          <p class="page-subtitle">
            <span class="material-icons">store</span>
            Hệ thống bán hàng tại quầy - Point of Sale
          </p>
        </div>
        <div class="header-actions">
          <button @click="showShortcuts = true" class="btn btn-secondary btn-icon-text" title="Phím tắt (F1)">
            <span class="material-icons">keyboard</span>
            Phím tắt
          </button>
          <button @click="showHistory = true" class="btn btn-secondary btn-icon-text">
            <span class="material-icons">history</span>
            Lịch sử
          </button>
          <button @click="showBarcode = !showBarcode" class="btn btn-secondary btn-icon-text" :class="{ 'active': showBarcode }">
            <span class="material-icons">qr_code_scanner</span>
            Barcode
          </button>
          <button @click="resetCart" class="btn btn-secondary">
            <span class="material-icons">refresh</span>
            Làm mới
          </button>
          <button @click="showCustomerDialog = true" class="btn btn-primary">
            <span class="material-icons">person_add</span>
            Chọn khách hàng
          </button>
        </div>
      </div>

      <!-- Barcode Scanner (Collapsible) -->
      <transition name="slide-down">
        <div v-if="showBarcode" class="barcode-scanner">
          <div class="scanner-input-group">
            <span class="material-icons">qr_code_scanner</span>
            <input 
              ref="barcodeInput"
              v-model="barcodeValue"
              type="text" 
              class="barcode-input" 
              placeholder="Quét mã vạch hoặc nhập SKU..."
              @keyup.enter="handleBarcodeSearch"
            />
            <button @click="handleBarcodeSearch" class="btn btn-primary btn-sm">
              <span class="material-icons">search</span>
            </button>
          </div>
        </div>
      </transition>
    </div>

    <!-- Main Content Grid -->
    <div class="pos-grid">
      <!-- Left: Product Selection -->
      <div class="product-section">
        <!-- Search & Filters -->
        <div class="search-section">
          <div class="search-box">
            <span class="material-icons search-icon">search</span>
            <input 
              v-model="searchQuery"
              type="text" 
              class="search-input" 
              placeholder="Tìm sản phẩm (tên, SKU, barcode)..."
              @input="searchProducts"
            />
            <button v-if="searchQuery" @click="searchQuery = ''; searchProducts()" class="search-clear">
              <span class="material-icons">close</span>
            </button>
          </div>
          <div class="filter-row">
            <select v-model="filterBrand" @change="filterProducts" class="form-control">
              <option value="">Tất cả thương hiệu</option>
              <option v-for="brand in brands" :key="brand.id" :value="brand.id">
                {{ brand.name }}
              </option>
            </select>
            <select v-model="filterCategory" @change="filterProducts" class="form-control">
              <option value="">Tất cả danh mục</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>
        </div>

        <!-- Products Grid -->
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>Đang tải sản phẩm...</p>
        </div>

        <div v-else-if="products.length === 0" class="empty-state">
          <span class="material-icons">inventory_2</span>
          <h3>Không tìm thấy sản phẩm</h3>
          <p>Vui lòng thử từ khóa khác hoặc điều chỉnh bộ lọc</p>
        </div>

        <div v-else class="products-grid">
          <div 
            v-for="product in products" 
            :key="product.id"
            class="product-card animate-fade-up"
            @click="addToCart(product)"
          >
            <div class="product-image">
              <img :src="product.imageUrl || '/placeholder.png'" :alt="product.name" />
              <div class="product-stock" :class="{ 'low-stock': product.stockQuantity < 10, 'out-of-stock': product.stockQuantity === 0 }">
                <span class="material-icons">inventory</span>
                {{ product.stockQuantity }}
              </div>
              <div v-if="product.priceSale" class="product-badge-sale">
                <span class="material-icons">sell</span>
                SALE
              </div>
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ product.name }}</h4>
              <p class="product-variant">{{ product.size }} - {{ product.color }}</p>
              <div class="product-price">
                <span class="price-current">{{ formatPrice(product.priceSale || product.priceBase) }}</span>
                <span v-if="product.priceSale" class="price-old">{{ formatPrice(product.priceBase) }}</span>
              </div>
            </div>
            <button class="btn-add-cart" @click.stop="addToCart(product)" :disabled="product.stockQuantity === 0">
              <span class="material-icons">add_shopping_cart</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Right: Cart & Checkout -->
      <div class="cart-section">
        <div class="cart-container">
          <!-- Customer Info -->
          <div class="customer-info">
            <div v-if="selectedCustomer" class="customer-selected">
              <div class="customer-avatar">
                <span class="material-icons">person</span>
              </div>
              <div class="customer-details">
                <strong>{{ selectedCustomer.fullName }}</strong>
                <p>{{ selectedCustomer.email }}</p>
              </div>
              <button @click="selectedCustomer = null" class="btn-remove">
                <span class="material-icons">close</span>
              </button>
            </div>
            <div v-else class="customer-empty" @click="showCustomerDialog = true">
              <span class="material-icons">person_add_alt</span>
              <p>Chọn khách hàng (không bắt buộc)</p>
            </div>
          </div>

          <!-- Cart Items -->
          <div class="cart-header">
            <h3>Giỏ hàng ({{ cartItems.length }} sản phẩm)</h3>
            <div class="cart-header-actions">
              <button v-if="cartItems.length > 0 && !draftId" @click="saveDraft" class="btn-icon" title="Lưu nháp">
                <span class="material-icons">save</span>
              </button>
              <button v-if="cartItems.length > 0" @click="clearCart" class="btn-icon">
                <span class="material-icons">delete_outline</span>
              </button>
            </div>
          </div>

          <div v-if="cartItems.length === 0" class="cart-empty">
            <span class="material-icons">shopping_cart</span>
            <p>Giỏ hàng trống</p>
            <small>Chọn sản phẩm bên trái để thêm vào giỏ</small>
            
            <div v-if="drafts.length > 0" class="draft-list">
              <h4>Đơn nháp</h4>
              <div v-for="draft in drafts" :key="draft.id" class="draft-item" @click="loadDraft(draft)">
                <span class="material-icons">draft</span>
                <div class="draft-info">
                  <strong>{{ draft.items.length }} sản phẩm</strong>
                  <small>{{ formatPrice(draft.total) }}</small>
                </div>
                <button @click.stop="deleteDraft(draft.id)" class="btn-icon btn-sm">
                  <span class="material-icons">delete</span>
                </button>
              </div>
            </div>
          </div>

          <div v-else class="cart-items">
            <div v-for="item in cartItems" :key="item.variantId" class="cart-item">
              <img :src="item.imageUrl || '/placeholder.png'" :alt="item.productName" class="item-image" />
              <div class="item-details">
                <h4>{{ item.productName }}</h4>
                <p class="item-variant">{{ item.size }} - {{ item.color }}</p>
                <p class="item-price">{{ formatPrice(item.unitPrice) }} x {{ item.quantity }}</p>
              </div>
              <div class="item-actions">
                <div class="quantity-controls">
                  <button @click="decreaseQuantity(item)" class="btn-qty">
                    <span class="material-icons">remove</span>
                  </button>
                  <input 
                    v-model.number="item.quantity" 
                    type="number" 
                    min="1" 
                    :max="item.stockQuantity"
                    class="qty-input"
                    @change="updateQuantity(item)"
                  />
                  <button @click="increaseQuantity(item)" class="btn-qty">
                    <span class="material-icons">add</span>
                  </button>
                </div>
                <button @click="removeFromCart(item)" class="btn-remove-item">
                  <span class="material-icons">delete</span>
                </button>
              </div>
              <div class="item-total">
                {{ formatPrice(item.unitPrice * item.quantity) }}
              </div>
            </div>
          </div>

          <!-- Discount & Total -->
          <div v-if="cartItems.length > 0" class="cart-footer">
            <div class="discount-section">
              <input 
                v-model="discountCode"
                type="text" 
                class="form-control" 
                placeholder="Mã giảm giá"
              />
              <button @click="applyDiscount" class="btn btn-secondary">
                Áp dụng
              </button>
            </div>

            <div class="totals">
              <div class="total-row">
                <span>Tạm tính:</span>
                <strong>{{ formatPrice(subtotal) }}</strong>
              </div>
              <div v-if="discountAmount > 0" class="total-row discount">
                <span>Giảm giá:</span>
                <strong>-{{ formatPrice(discountAmount) }}</strong>
              </div>
              <div class="total-row final">
                <span>Tổng cộng:</span>
                <strong class="total-amount">{{ formatPrice(totalAmount) }}</strong>
              </div>
            </div>

            <!-- Payment Method -->
            <div class="payment-method">
              <label>Phương thức thanh toán:</label>
              <div class="payment-options">
                <label class="payment-option">
                  <input type="radio" v-model="paymentMethod" value="cash" />
                  <span class="material-icons">payments</span>
                  Tiền mặt
                </label>
                <label class="payment-option">
                  <input type="radio" v-model="paymentMethod" value="card" />
                  <span class="material-icons">credit_card</span>
                  Thẻ
                </label>
                <label class="payment-option">
                  <input type="radio" v-model="paymentMethod" value="transfer" />
                  <span class="material-icons">account_balance</span>
                  Chuyển khoản
                </label>
              </div>
            </div>

            <!-- Checkout Button -->
            <button 
              @click="checkout" 
              :disabled="processing"
              class="btn btn-primary btn-checkout"
            >
              <span class="material-icons">point_of_sale</span>
              {{ processing ? 'Đang xử lý...' : 'Thanh toán' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Customer Selection Dialog -->
    <div v-if="showCustomerDialog" class="modal-overlay" @click="showCustomerDialog = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Chọn khách hàng</h3>
          <button @click="showCustomerDialog = false" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <input 
            v-model="customerSearch"
            type="text" 
            class="form-control" 
            placeholder="Tìm khách hàng (tên, email, SĐT)..."
            @input="searchCustomers"
          />
          <div class="customer-list">
            <div 
              v-for="customer in customers" 
              :key="customer.id"
              class="customer-item"
              @click="selectCustomer(customer)"
            >
              <div class="customer-avatar">
                <span class="material-icons">person</span>
              </div>
              <div class="customer-info">
                <strong>{{ customer.fullName }}</strong>
                <p>{{ customer.email }}</p>
                <p>{{ customer.phoneNumber }}</p>
              </div>
              <span class="material-icons">chevron_right</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Keyboard Shortcuts Dialog -->
    <div v-if="showShortcuts" class="modal-overlay" @click="showShortcuts = false">
      <div class="modal modal-shortcuts" @click.stop>
        <div class="modal-header">
          <h3>
            <span class="material-icons">keyboard</span>
            Phím tắt
          </h3>
          <button @click="showShortcuts = false" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="shortcuts-grid">
            <div class="shortcut-item">
              <kbd>F1</kbd>
              <span>Hiển thị phím tắt</span>
            </div>
            <div class="shortcut-item">
              <kbd>F2</kbd>
              <span>Chọn khách hàng</span>
            </div>
            <div class="shortcut-item">
              <kbd>F3</kbd>
              <span>Tìm kiếm sản phẩm</span>
            </div>
            <div class="shortcut-item">
              <kbd>F4</kbd>
              <span>Barcode scanner</span>
            </div>
            <div class="shortcut-item">
              <kbd>F5</kbd>
              <span>Làm mới</span>
            </div>
            <div class="shortcut-item">
              <kbd>F9</kbd>
              <span>Thanh toán</span>
            </div>
            <div class="shortcut-item">
              <kbd>Ctrl</kbd> + <kbd>S</kbd>
              <span>Lưu nháp</span>
            </div>
            <div class="shortcut-item">
              <kbd>Ctrl</kbd> + <kbd>H</kbd>
              <span>Lịch sử giao dịch</span>
            </div>
            <div class="shortcut-item">
              <kbd>Esc</kbd>
              <span>Đóng dialog</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Transaction History Dialog -->
    <div v-if="showHistory" class="modal-overlay" @click="showHistory = false">
      <div class="modal modal-lg" @click.stop>
        <div class="modal-header">
          <h3>
            <span class="material-icons">history</span>
            Lịch sử giao dịch
          </h3>
          <button @click="exportHistory" class="btn btn-sm btn-secondary">
            <span class="material-icons">download</span>
            Xuất báo cáo
          </button>
          <button @click="showHistory = false" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="history-filters">
            <input v-model="historySearch" type="text" class="form-control" placeholder="Tìm theo mã đơn, khách hàng..." />
            <select v-model="historyFilter" class="form-control">
              <option value="today">Hôm nay</option>
              <option value="week">Tuần này</option>
              <option value="month">Tháng này</option>
            </select>
          </div>
          <div class="transaction-list">
            <div v-for="transaction in transactions" :key="transaction.id" class="transaction-item" @click="viewReceipt(transaction)">
              <div class="transaction-info">
                <strong>#{{ transaction.id }}</strong>
                <p>{{ transaction.customerName || 'Khách lẻ' }}</p>
                <small>{{ formatDateTime(transaction.createdAt) }}</small>
              </div>
              <div class="transaction-details">
                <span class="transaction-items">{{ transaction.itemCount }} sản phẩm</span>
                <span class="transaction-total">{{ formatPrice(transaction.total) }}</span>
              </div>
              <span class="material-icons">receipt_long</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Receipt Dialog -->
    <div v-if="showReceipt" class="modal-overlay" @click="showReceipt = false">
      <div class="modal modal-receipt" @click.stop>
        <div class="modal-header">
          <h3>
            <span class="material-icons">receipt_long</span>
            Hóa đơn
          </h3>
          <button @click="printReceipt" class="btn btn-sm btn-primary">
            <span class="material-icons">print</span>
            In hóa đơn
          </button>
          <button @click="showReceipt = false" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div ref="receiptContent" class="receipt-content">
            <div class="receipt-header">
              <h2>SNEAKERY STORE</h2>
              <p>123 Nguyễn Huệ, Q.1, TP.HCM</p>
              <p>Hotline: 1900 xxxx</p>
            </div>
            <div class="receipt-divider"></div>
            <div class="receipt-info">
              <div class="receipt-row">
                <span>Mã đơn:</span>
                <strong>#{{ currentReceipt?.id }}</strong>
              </div>
              <div class="receipt-row">
                <span>Ngày:</span>
                <span>{{ formatDateTime(currentReceipt?.createdAt) }}</span>
              </div>
              <div v-if="currentReceipt?.customerName" class="receipt-row">
                <span>Khách hàng:</span>
                <span>{{ currentReceipt.customerName }}</span>
              </div>
            </div>
            <div class="receipt-divider"></div>
            <div class="receipt-items">
              <div v-for="item in currentReceipt?.items" :key="item.id" class="receipt-item">
                <div class="item-name">{{ item.productName }} ({{ item.size }} - {{ item.color }})</div>
                <div class="item-line">
                  <span>{{ item.quantity }} x {{ formatPrice(item.unitPrice) }}</span>
                  <strong>{{ formatPrice(item.quantity * item.unitPrice) }}</strong>
                </div>
              </div>
            </div>
            <div class="receipt-divider"></div>
            <div class="receipt-totals">
              <div class="receipt-row">
                <span>Tạm tính:</span>
                <span>{{ formatPrice(currentReceipt?.subtotal) }}</span>
              </div>
              <div v-if="currentReceipt?.discount" class="receipt-row discount">
                <span>Giảm giá:</span>
                <span>-{{ formatPrice(currentReceipt.discount) }}</span>
              </div>
              <div class="receipt-row total">
                <strong>Tổng cộng:</strong>
                <strong>{{ formatPrice(currentReceipt?.total) }}</strong>
              </div>
              <div class="receipt-row">
                <span>Thanh toán:</span>
                <span>{{ getPaymentMethodText(currentReceipt?.paymentMethod) }}</span>
              </div>
            </div>
            <div class="receipt-footer">
              <p>Cảm ơn quý khách đã mua hàng!</p>
              <p>Hẹn gặp lại!</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import adminService from '@/services/adminService'
import { downloadCsv } from '@/utils/exportHelpers'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const processing = ref(false)
const searchQuery = ref('')
const filterBrand = ref('')
const filterCategory = ref('')
const products = ref([])
const brands = ref([])
const categories = ref([])
const cartItems = ref([])
const selectedCustomer = ref(null)
const showCustomerDialog = ref(false)
const customerSearch = ref('')
const customers = ref([])
const discountCode = ref('')
const discountAmount = ref(0)
const paymentMethod = ref('cash')

// New state for enhanced features
const showBarcode = ref(false)
const barcodeValue = ref('')
const barcodeInput = ref(null)
const showShortcuts = ref(false)
const showHistory = ref(false)
const showReceipt = ref(false)
const currentReceipt = ref(null)
const transactions = ref([])
const historySearch = ref('')
const historyFilter = ref('today')
const drafts = ref([])
const draftId = ref(null)
const receiptContent = ref(null)

// Computed
const subtotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.unitPrice * item.quantity), 0)
})

const totalAmount = computed(() => {
  return subtotal.value - discountAmount.value
})

// Methods
const fetchProducts = async () => {
  try {
    loading.value = true
    const response = await adminService.getProducts(0, 100, {
      search: searchQuery.value,
      brandId: filterBrand.value,
      categoryId: filterCategory.value,
      status: 'active'
    })
    // Flatten variants to products for POS
    const allProducts = []
    response.content.forEach(product => {
      if (product.variants && product.variants.length > 0) {
        product.variants.forEach(variant => {
          allProducts.push({
            id: product.id,
            variantId: variant.id,
            name: product.name,
            brandName: product.brandName,
            size: variant.size,
            color: variant.color,
            sku: variant.sku,
            priceBase: variant.priceBase,
            priceSale: variant.priceSale,
            stockQuantity: variant.stockQuantity,
            imageUrl: variant.imageUrl
          })
        })
      }
    })
    products.value = allProducts
  } catch (error) {
    console.error('Lỗi tải sản phẩm:', error)
  } finally {
    loading.value = false
  }
}

const fetchBrandsAndCategories = async () => {
  try {
    const [brandsRes, categoriesRes] = await Promise.all([
      adminService.getBrands(),
      adminService.getCategories()
    ])
    brands.value = brandsRes
    categories.value = categoriesRes
  } catch (error) {
    console.error('Lỗi tải brands/categories:', error)
  }
}

const searchProducts = () => {
  fetchProducts()
}

const filterProducts = () => {
  fetchProducts()
}

const addToCart = (product) => {
  const existing = cartItems.value.find(item => item.variantId === product.variantId)
  if (existing) {
    if (existing.quantity < product.stockQuantity) {
      existing.quantity++
    } else {
      alert('Không đủ hàng trong kho!')
    }
  } else {
    cartItems.value.push({
      variantId: product.variantId,
      productName: product.name,
      brandName: product.brandName,
      size: product.size,
      color: product.color,
      sku: product.sku,
      unitPrice: product.priceSale || product.priceBase,
      quantity: 1,
      stockQuantity: product.stockQuantity,
      imageUrl: product.imageUrl
    })
  }
}

const increaseQuantity = (item) => {
  if (item.quantity < item.stockQuantity) {
    item.quantity++
  } else {
    alert('Không đủ hàng trong kho!')
  }
}

const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    item.quantity--
  } else {
    removeFromCart(item)
  }
}

const updateQuantity = (item) => {
  if (item.quantity < 1) {
    item.quantity = 1
  }
  if (item.quantity > item.stockQuantity) {
    item.quantity = item.stockQuantity
    alert('Không đủ hàng trong kho!')
  }
}

const removeFromCart = (item) => {
  const index = cartItems.value.findIndex(i => i.variantId === item.variantId)
  if (index !== -1) {
    cartItems.value.splice(index, 1)
  }
}

const clearCart = () => {
  if (confirm('Bạn có chắc muốn xóa hết sản phẩm trong giỏ hàng?')) {
    cartItems.value = []
    discountCode.value = ''
    discountAmount.value = 0
  }
}

const resetCart = () => {
  clearCart()
  selectedCustomer.value = null
  paymentMethod.value = 'cash'
}

const applyDiscount = async () => {
  // TODO: Call API để validate mã giảm giá
  if (discountCode.value) {
    // Mock: Giảm 10%
    discountAmount.value = subtotal.value * 0.1
    alert('Đã áp dụng mã giảm giá!')
  }
}

const searchCustomers = async () => {
  try {
    const response = await adminService.getUsers(0, 20, {
      search: customerSearch.value
    })
    customers.value = response.content
  } catch (error) {
    console.error('Lỗi tìm khách hàng:', error)
  }
}

const selectCustomer = (customer) => {
  selectedCustomer.value = customer
  showCustomerDialog.value = false
}

const checkout = async () => {
  if (cartItems.value.length === 0) {
    alert('Giỏ hàng trống!')
    return
  }

  if (!paymentMethod.value) {
    alert('Vui lòng chọn phương thức thanh toán!')
    return
  }

  try {
    processing.value = true
    // TODO: Call API để tạo đơn hàng POS
    const orderData = {
      userId: selectedCustomer.value?.id || null,
      items: cartItems.value.map(item => ({
        variantId: item.variantId,
        quantity: item.quantity,
        unitPrice: item.unitPrice
      })),
      discountCode: discountCode.value,
      discountAmount: discountAmount.value,
      totalAmount: totalAmount.value,
      paymentMethod: paymentMethod.value,
      isPOS: true
    }
    
    console.log('Tạo đơn hàng:', orderData)
    
    alert('Thanh toán thành công!')
    resetCart()
  } catch (error) {
    console.error('Lỗi thanh toán:', error)
    alert('Có lỗi xảy ra. Vui lòng thử lại!')
  } finally {
    processing.value = false
  }
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price)
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('vi-VN')
}

const getPaymentMethodText = (method) => {
  const methods = {
    cash: 'Tiền mặt',
    card: 'Thẻ',
    transfer: 'Chuyển khoản'
  }
  return methods[method] || method
}

// Barcode Scanner
const handleBarcodeSearch = () => {
  if (!barcodeValue.value.trim()) return
  
  const product = products.value.find(p => 
    p.sku === barcodeValue.value.trim() || 
    p.barcode === barcodeValue.value.trim()
  )
  
  if (product) {
    addToCart(product)
    barcodeValue.value = ''
  } else {
    alert('Không tìm thấy sản phẩm với mã này!')
  }
}

// Draft Management
const saveDraft = () => {
  const draft = {
    id: Date.now(),
    items: [...cartItems.value],
    customer: selectedCustomer.value,
    discount: discountAmount.value,
    discountCode: discountCode.value,
    total: totalAmount.value,
    createdAt: new Date()
  }
  drafts.value.push(draft)
  localStorage.setItem('posDrafts', JSON.stringify(drafts.value))
  alert('Đã lưu đơn nháp!')
}

const loadDraft = (draft) => {
  cartItems.value = [...draft.items]
  selectedCustomer.value = draft.customer
  discountAmount.value = draft.discount
  discountCode.value = draft.discountCode
  draftId.value = draft.id
}

const deleteDraft = (id) => {
  if (confirm('Xóa đơn nháp này?')) {
    drafts.value = drafts.value.filter(d => d.id !== id)
    localStorage.setItem('posDrafts', JSON.stringify(drafts.value))
  }
}

// Transaction History
const viewReceipt = (transaction) => {
  currentReceipt.value = transaction
  showReceipt.value = true
  showHistory.value = false
}

const printReceipt = () => {
  if (!receiptContent.value) return
  const printWindow = window.open('', '_blank')
  printWindow.document.write('<html><head><title>Hóa đơn</title>')
  printWindow.document.write('<style>')
  printWindow.document.write(`
    body { font-family: Arial, sans-serif; padding: 20px; }
    .receipt-content { max-width: 400px; margin: 0 auto; }
    .receipt-header { text-align: center; margin-bottom: 20px; }
    .receipt-header h2 { margin: 0 0 10px 0; }
    .receipt-divider { border-bottom: 2px dashed #ccc; margin: 15px 0; }
    .receipt-row { display: flex; justify-content: space-between; margin: 8px 0; }
    .receipt-items { margin: 15px 0; }
    .receipt-item { margin: 10px 0; }
    .item-line { display: flex; justify-content: space-between; }
    .receipt-footer { text-align: center; margin-top: 20px; }
    .total { font-size: 1.2em; font-weight: bold; }
    .discount { color: green; }
  `)
  printWindow.document.write('</style></head><body>')
  printWindow.document.write(receiptContent.value.innerHTML)
  printWindow.document.write('</body></html>')
  printWindow.document.close()
  printWindow.print()
}

const exportHistory = () => {
  const data = transactions.value.map(t => ({
    'Mã đơn': t.id,
    'Khách hàng': t.customerName || 'Khách lẻ',
    'Số lượng SP': t.itemCount,
    'Tổng tiền': t.total,
    'Thanh toán': getPaymentMethodText(t.paymentMethod),
    'Ngày': formatDateTime(t.createdAt)
  }))
  downloadCsv(data, `pos-history-${historyFilter.value}.csv`)
}

// Keyboard Shortcuts
const handleKeyPress = (e) => {
  // F1: Show shortcuts
  if (e.key === 'F1') {
    e.preventDefault()
    showShortcuts.value = true
  }
  // F2: Select customer
  if (e.key === 'F2') {
    e.preventDefault()
    showCustomerDialog.value = true
  }
  // F3: Focus search
  if (e.key === 'F3') {
    e.preventDefault()
    document.querySelector('.search-input')?.focus()
  }
  // F4: Toggle barcode scanner
  if (e.key === 'F4') {
    e.preventDefault()
    showBarcode.value = !showBarcode.value
    if (showBarcode.value) {
      setTimeout(() => barcodeInput.value?.focus(), 100)
    }
  }
  // F5: Refresh
  if (e.key === 'F5') {
    e.preventDefault()
    resetCart()
  }
  // F9: Checkout
  if (e.key === 'F9') {
    e.preventDefault()
    if (cartItems.value.length > 0) checkout()
  }
  // Ctrl+S: Save draft
  if (e.ctrlKey && e.key === 's') {
    e.preventDefault()
    if (cartItems.value.length > 0 && !draftId.value) saveDraft()
  }
  // Ctrl+H: Show history
  if (e.ctrlKey && e.key === 'h') {
    e.preventDefault()
    showHistory.value = true
  }
  // Esc: Close dialogs
  if (e.key === 'Escape') {
    showCustomerDialog.value = false
    showShortcuts.value = false
    showHistory.value = false
    showReceipt.value = false
  }
}

// Lifecycle
onMounted(() => {
  fetchProducts()
  fetchBrandsAndCategories()
  
  // Load drafts from localStorage
  const savedDrafts = localStorage.getItem('posDrafts')
  if (savedDrafts) {
    drafts.value = JSON.parse(savedDrafts)
  }
  
  // Mock transactions for demo
  transactions.value = [
    {
      id: 1001,
      customerName: 'Nguyễn Văn A',
      itemCount: 3,
      total: 2500000,
      paymentMethod: 'cash',
      createdAt: new Date(),
      items: [],
      subtotal: 2500000,
      discount: 0
    }
  ]
  
  // Add keyboard shortcuts
  document.addEventListener('keydown', handleKeyPress)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyPress)
})
</script>

<style scoped>
/* ===== LAYOUT ===== */
.admin-sales {
  padding: var(--space-8) var(--space-4);
  max-width: 1800px;
  margin: 0 auto;
}

/* ===== PAGE HEADER ===== */
.page-header {
  background: var(--card-bg);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
  backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-6);
  flex-wrap: wrap;
}

.title-section {
  flex: 1;
  min-width: 200px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.page-title .material-icons {
  font-size: 2rem;
  color: var(--accent-primary);
}

.page-subtitle {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin: 0;
}

.page-subtitle .material-icons {
  font-size: 1.125rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  flex-wrap: wrap;
}

/* ===== BARCODE SCANNER ===== */
.barcode-scanner {
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 2px solid var(--border-primary);
}

.scanner-input-group {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  max-width: 600px;
}

.scanner-input-group .material-icons {
  font-size: 1.5rem;
  color: var(--accent-primary);
}

.barcode-input {
  flex: 1;
  padding: var(--space-3) var(--space-4);
  background: var(--bg-primary);
  border: 2px solid var(--accent-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  transition: all var(--transition-fast);
}

.barcode-input:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.2);
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

/* Slide down transition */
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

.pos-grid {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: var(--space-8);
  height: calc(100vh - 250px);
}

/* ===== PRODUCT SECTION ===== */
.product-section {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.search-section {
  margin-bottom: var(--space-6);
}

/* ===== BUTTONS ===== */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.btn .material-icons {
  font-size: 1.25rem;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--color-white);
  box-shadow: var(--shadow-md);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

.btn-secondary {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  border: 2px solid var(--border-primary);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--bg-secondary);
  border-color: var(--border-medium);
  box-shadow: var(--shadow-sm);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===== SEARCH BOX ===== */
.search-box {
  position: relative;
  margin-bottom: var(--space-4);
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-tertiary);
  pointer-events: none;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: var(--space-4) var(--space-4) var(--space-4) var(--space-12);
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-base);
  transition: all var(--transition-fast);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: var(--bg-secondary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.search-input::placeholder {
  color: var(--text-muted);
}

.filter-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-4);
}

.filter-row .form-control {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  transition: all var(--transition-fast);
  cursor: pointer;
}

.filter-row .form-control:hover {
  border-color: var(--border-medium);
  background: var(--bg-secondary);
}

.filter-row .form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: var(--bg-secondary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
  overflow-y: auto;
  padding: var(--space-2);
}

/* ===== LOADING & EMPTY STATES ===== */
.loading-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12);
  color: var(--text-tertiary);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--border-primary);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-container p {
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
  margin: 0;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12);
  text-align: center;
}

.empty-state .material-icons {
  font-size: 5rem;
  color: var(--text-muted);
  margin-bottom: var(--space-4);
  opacity: 0.5;
}

.empty-state h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.empty-state p {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin: 0;
  max-width: 300px;
}

/* ===== PRODUCT CARD ===== */
.product-card {
  background: var(--bg-card);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  border-color: var(--accent-primary);
  box-shadow: var(--shadow-glow-purple);
  transform: translateY(-4px);
}

.product-image {
  position: relative;
  width: 100%;
  height: 150px;
  margin-bottom: var(--space-3);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-tertiary);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-stock {
  position: absolute;
  top: var(--space-2);
  right: var(--space-2);
  background: var(--success-solid);
  color: var(--color-white);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  display: flex;
  align-items: center;
  gap: var(--space-1);
  box-shadow: var(--shadow-md);
  z-index: 2;
}

.product-stock.low-stock {
  background: var(--warning-solid);
}

.product-stock.out-of-stock {
  background: var(--error-solid);
}

.product-stock .material-icons {
  font-size: 0.875rem;
}

.product-badge-sale {
  position: absolute;
  top: var(--space-2);
  left: var(--space-2);
  background: linear-gradient(135deg, #ff416c 0%, #ff4b2b 100%);
  color: white;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  display: flex;
  align-items: center;
  gap: var(--space-1);
  box-shadow: var(--shadow-md);
  z-index: 2;
  animation: pulse-sale 2s infinite;
}

.product-badge-sale .material-icons {
  font-size: 0.875rem;
}

@keyframes pulse-sale {
  0%, 100% {
    box-shadow: 0 4px 12px rgba(255, 65, 108, 0.4);
  }
  50% {
    box-shadow: 0 4px 20px rgba(255, 65, 108, 0.8);
  }
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-variant {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin: 0 0 var(--space-2) 0;
}

.product-price {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.price-current {
  font-size: var(--text-base);
  font-weight: var(--font-bold);
  color: var(--accent-primary);
}

.price-old {
  font-size: var(--text-xs);
  color: var(--text-muted);
  text-decoration: line-through;
}

.btn-add-cart {
  position: absolute;
  bottom: var(--space-4);
  right: var(--space-4);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: var(--color-white);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  opacity: 0;
  box-shadow: var(--shadow-md);
}

.product-card:hover .btn-add-cart {
  opacity: 1;
}

.btn-add-cart:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-glow-purple);
}

/* ===== CART SECTION ===== */
.cart-section {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.cart-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* ===== CUSTOMER INFO ===== */
.customer-info {
  margin-bottom: var(--space-6);
  padding-bottom: var(--space-4);
  border-bottom: 2px solid var(--border-primary);
}

.customer-selected {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--gradient-purple-soft);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
}

.customer-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-white);
  flex-shrink: 0;
  box-shadow: var(--shadow-md);
}

.customer-details {
  flex: 1;
}

.customer-details strong {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.customer-details p {
  margin: var(--space-1) 0 0 0;
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.customer-empty {
  padding: var(--space-4);
  border: 2px dashed var(--border-medium);
  border-radius: var(--radius-lg);
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.customer-empty:hover {
  border-color: var(--accent-primary);
  background: var(--gradient-purple-soft);
}

.customer-empty .material-icons {
  font-size: 2rem;
  color: var(--text-muted);
}

.customer-empty p {
  margin: var(--space-2) 0 0 0;
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* ===== CART ITEMS ===== */
.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.cart-header h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.cart-header-actions {
  display: flex;
  gap: var(--space-2);
}

.btn-icon {
  background: none;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
}

.btn-icon:hover {
  background: var(--bg-secondary);
  color: var(--accent-primary);
}

.btn-icon .material-icons {
  font-size: 1.25rem;
}

.btn-clear {
  background: none;
  border: none;
  color: var(--error-solid);
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-clear:hover {
  background: var(--error-bg);
}

/* ===== DRAFT LIST ===== */
.draft-list {
  margin-top: var(--space-6);
  padding-top: var(--space-4);
  border-top: 2px dashed var(--border-primary);
}

.draft-list h4 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.draft-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--gradient-purple-soft);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  margin-bottom: var(--space-2);
}

.draft-item:hover {
  border-color: var(--accent-primary);
  transform: translateX(4px);
}

.draft-item .material-icons {
  color: var(--accent-primary);
  font-size: 1.5rem;
}

.draft-info {
  flex: 1;
}

.draft-info strong {
  display: block;
  font-size: var(--text-sm);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.draft-info small {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.btn-remove {
  background: none;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove:hover {
  background: var(--error-bg);
  color: var(--error-solid);
}

.cart-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  padding: var(--space-12);
}

.cart-empty .material-icons {
  font-size: 4rem;
  margin-bottom: var(--space-4);
}

.cart-empty p {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin: 0 0 var(--space-2) 0;
}

.cart-empty small {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  margin-bottom: var(--space-4);
}

.cart-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-3);
  transition: all var(--transition-fast);
  background: var(--bg-primary);
}

.cart-item:hover {
  border-color: var(--border-medium);
  background: var(--bg-secondary);
  box-shadow: var(--shadow-sm);
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  object-fit: cover;
  background: var(--bg-tertiary);
  flex-shrink: 0;
}

.item-details {
  flex: 1;
  min-width: 0;
}

.item-details h4 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-variant {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin: 0 0 var(--space-1) 0;
}

.item-price {
  font-size: var(--text-xs);
  color: var(--accent-primary);
  font-weight: var(--font-semibold);
  margin: 0;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  align-items: center;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  padding: var(--space-1);
}

.btn-qty {
  width: 24px;
  height: 24px;
  border: none;
  background: var(--bg-primary);
  color: var(--accent-primary);
  border-radius: var(--radius-sm);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.btn-qty:hover {
  background: var(--accent-primary);
  color: var(--color-white);
}

.btn-qty .material-icons {
  font-size: 1rem;
}

.qty-input {
  width: 40px;
  height: 24px;
  text-align: center;
  border: none;
  background: var(--bg-primary);
  color: var(--text-primary);
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  border-radius: var(--radius-sm);
}

.qty-input::-webkit-inner-spin-button,
.qty-input::-webkit-outer-spin-button {
  appearance: none;
  margin: 0;
}

.btn-remove-item {
  background: none;
  border: none;
  color: var(--error-solid);
  cursor: pointer;
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.btn-remove-item:hover {
  background: var(--error-bg);
}

.btn-remove-item .material-icons {
  font-size: 1.125rem;
}

.item-total {
  font-size: var(--text-base);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  min-width: 80px;
  text-align: right;
}

/* ===== CART FOOTER ===== */
.cart-footer {
  padding-top: var(--space-4);
  border-top: 2px solid var(--border-primary);
}

.discount-section {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.discount-section .form-control {
  flex: 1;
  padding: var(--space-3) var(--space-4);
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
}

.discount-section .form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: var(--bg-secondary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.discount-section .btn {
  padding: var(--space-3) var(--space-6);
  border-radius: var(--radius-lg);
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.totals {
  margin-bottom: var(--space-4);
}

.total-row {
  display: flex;
  justify-content: space-between;
  padding: var(--space-2) 0;
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.total-row strong {
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

.total-row.discount {
  color: var(--success-text);
}

.total-row.discount strong {
  color: var(--success-text);
}

.total-row.final {
  padding-top: var(--space-3);
  border-top: 2px solid var(--border-primary);
  font-size: var(--text-lg);
  margin-top: var(--space-2);
}

.total-amount {
  color: var(--accent-primary);
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
}

/* ===== PAYMENT METHOD ===== */
.payment-method {
  margin-bottom: var(--space-4);
}

.payment-method > label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-3);
}

.payment-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-2);
}

.payment-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-3);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.payment-option:hover {
  border-color: var(--accent-primary);
  background: var(--gradient-purple-soft);
}

.payment-option:has(input:checked) {
  border-color: var(--accent-primary);
  background: var(--gradient-purple-soft);
  color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.payment-option input[type="radio"] {
  margin: 0;
  width: 20px;
  height: 20px;
  accent-color: var(--accent-primary);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.payment-option input[type="radio"]:hover {
  transform: scale(1.1);
  box-shadow: 0 0 0 2px rgba(167, 139, 250, 0.2);
}

.payment-option input[type="radio"]:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.payment-option .material-icons {
  font-size: 1.5rem;
  color: var(--accent-primary);
}

.btn-checkout {
  width: 100%;
  padding: var(--space-4);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  background: var(--gradient-primary);
  color: var(--color-white);
  border: none;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
}

.btn-checkout:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-checkout:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===== MODAL DIALOG ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-4);
}

.modal {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  width: 100%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-2xl);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  padding: var(--space-6);
  border-bottom: 2px solid var(--border-primary);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-header h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: none;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-solid);
}

.modal-body {
  padding: var(--space-6);
  overflow-y: auto;
}

.modal-body .form-control {
  width: 100%;
  padding: var(--space-4);
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-base);
  transition: all var(--transition-fast);
}

.modal-body .form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: var(--bg-secondary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.modal-body .form-control::placeholder {
  color: var(--text-muted);
}

/* ===== CUSTOMER DIALOG ===== */
.customer-list {
  margin-top: var(--space-4);
  max-height: 400px;
  overflow-y: auto;
}

.customer-list::-webkit-scrollbar {
  width: 6px;
}

.customer-list::-webkit-scrollbar-thumb {
  background: var(--border-medium);
  border-radius: 3px;
}

.customer-list::-webkit-scrollbar-thumb:hover {
  background: var(--border-dark);
}

.customer-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-2);
  cursor: pointer;
  transition: all var(--transition-fast);
  background: var(--bg-primary);
}

.customer-item:hover {
  border-color: var(--accent-primary);
  background: var(--gradient-purple-soft);
  transform: translateX(4px);
}

.customer-item .customer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-white);
  flex-shrink: 0;
}

.customer-item .material-icons:last-child {
  color: var(--text-tertiary);
  margin-left: auto;
}

.customer-info {
  flex: 1;
  min-width: 0;
}

.customer-info strong {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.customer-info p {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin: var(--space-1) 0 0 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ===== SHORTCUTS MODAL ===== */
.modal-shortcuts .shortcuts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--space-4);
}

.shortcut-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--gradient-purple-soft);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
}

.shortcut-item:hover {
  border-color: var(--accent-primary);
  transform: translateX(4px);
}

.shortcut-item kbd {
  padding: var(--space-1) var(--space-2);
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-family: 'Courier New', monospace;
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  color: var(--accent-primary);
  box-shadow: 0 2px 0 var(--border-medium);
  min-width: 32px;
  text-align: center;
}

.shortcut-item span {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* ===== HISTORY MODAL ===== */
.modal-lg {
  max-width: 900px;
}

.history-filters {
  display: flex;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.history-filters .form-control {
  flex: 1;
}

.transaction-list {
  max-height: 500px;
  overflow-y: auto;
}

.transaction-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-3);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.transaction-item:hover {
  border-color: var(--accent-primary);
  background: var(--gradient-purple-soft);
  transform: translateX(4px);
}

.transaction-info {
  flex: 1;
}

.transaction-info strong {
  display: block;
  font-size: var(--text-base);
  color: var(--accent-primary);
  font-weight: var(--font-bold);
  margin-bottom: var(--space-1);
}

.transaction-info p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: var(--space-1) 0 0 0;
}

.transaction-info small {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
}

.transaction-details {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--space-1);
}

.transaction-items {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.transaction-total {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--accent-primary);
}

.transaction-item > .material-icons {
  color: var(--text-tertiary);
  font-size: 1.5rem;
}

/* ===== RECEIPT MODAL ===== */
.modal-receipt {
  max-width: 500px;
}

.receipt-content {
  background: white;
  color: #000;
  padding: var(--space-6);
  border-radius: var(--radius-lg);
}

.receipt-header {
  text-align: center;
  margin-bottom: var(--space-4);
}

.receipt-header h2 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: #000;
  margin: 0 0 var(--space-2) 0;
}

.receipt-header p {
  font-size: var(--text-sm);
  color: #666;
  margin: var(--space-1) 0 0 0;
}

.receipt-divider {
  border-bottom: 2px dashed #ddd;
  margin: var(--space-4) 0;
}

.receipt-info,
.receipt-totals {
  margin: var(--space-4) 0;
}

.receipt-row {
  display: flex;
  justify-content: space-between;
  margin: var(--space-2) 0;
  font-size: var(--text-sm);
}

.receipt-row strong {
  font-weight: var(--font-bold);
}

.receipt-row.discount {
  color: #22c55e;
}

.receipt-row.total {
  font-size: var(--text-lg);
  margin-top: var(--space-3);
  padding-top: var(--space-3);
  border-top: 2px solid #000;
}

.receipt-items {
  margin: var(--space-4) 0;
}

.receipt-item {
  margin: var(--space-3) 0;
}

.item-name {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: #000;
  margin-bottom: var(--space-1);
}

.item-line {
  display: flex;
  justify-content: space-between;
  font-size: var(--text-sm);
  color: #666;
}

.receipt-footer {
  text-align: center;
  margin-top: var(--space-6);
  padding-top: var(--space-4);
  border-top: 2px dashed #ddd;
}

.receipt-footer p {
  font-size: var(--text-sm);
  color: #666;
  margin: var(--space-1) 0;
}

/* ===== BUTTON VARIANTS ===== */
.btn-icon-text {
  gap: var(--space-2);
}

.btn-icon-text .material-icons {
  font-size: 1.125rem;
}

.btn.active {
  background: var(--gradient-primary);
  color: white;
  border-color: var(--accent-primary);
}

.btn-sm {
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-sm);
}

.btn-sm .material-icons {
  font-size: 1rem;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 1024px) {
  .pos-grid {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .cart-section {
    max-height: 600px;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .shortcuts-grid {
    grid-template-columns: 1fr !important;
  }
  
  .history-filters {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .admin-sales {
    padding: var(--space-4) var(--space-2);
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: var(--space-2);
  }
  
  .payment-options {
    grid-template-columns: 1fr;
  }
  
  .pos-grid {
    gap: var(--space-4);
  }
}
</style>


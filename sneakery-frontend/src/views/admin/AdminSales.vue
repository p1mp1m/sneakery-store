<template>
  <div class="admin-sales">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">point_of_sale</span>
            Hệ thống POS
          </h1>
          <p class="page-subtitle">
            <span class="material-icons">store</span>
            Bán hàng tại quầy
          </p>
        </div>
        <div class="header-actions">
          <button @click="showShortcuts = true" class="btn btn-secondary">
            <span class="material-icons">keyboard</span>
            Phím tắt
          </button>
          <button @click="showHistory = true" class="btn btn-secondary">
            <span class="material-icons">history</span>
            Lịch sử
          </button>
          <button @click="resetCart" class="btn btn-secondary">
            <span class="material-icons">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Barcode Scanner -->
    <transition name="slide-down">
      <div v-if="showBarcode" class="barcode-scanner">
        <div class="scanner-input-group">
          <span class="material-icons">qr_code_scanner</span>
          <input
            ref="barcodeInput"
            v-model="barcodeValue"
            @keyup.enter="handleBarcodeSearch"
            class="barcode-input"
            placeholder="Quét mã vạch hoặc nhập SKU..."
          />
          <button @click="showBarcode = false" class="btn btn-secondary">
            <span class="material-icons">close</span>
          </button>
        </div>
      </div>
    </transition>

    <!-- POS Grid -->
    <div class="pos-grid">
      <!-- Product Section -->
      <div class="product-section">
        <!-- Search Section -->
        <div class="search-section">
          <div class="search-box">
            <span class="material-icons search-icon">search</span>
            <input
              v-model="searchQuery"
              @input="searchProducts"
              class="search-input"
              placeholder="Tìm kiếm sản phẩm..."
            />
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
        <div v-else class="products-grid">
          <div
            v-for="product in products"
            :key="product.id"
            @click="addToCart(product)"
            class="product-card"
          >
            <div class="product-image">
              <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" />
              <span v-else class="material-icons">image</span>
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="product-brand">{{ product.brandName }}</p>
              <p class="product-price">{{ formatCurrency(product.price) }}</p>
            </div>
            <div class="product-stock" :class="{ low: product.stockQuantity < 10, out: product.stockQuantity === 0 }">
              {{ product.stockQuantity }}
            </div>
          </div>
        </div>
      </div>

      <!-- Cart Section -->
      <div class="cart-section">
        <div class="cart-header">
          <h2 class="cart-title">Giỏ hàng</h2>
          <button @click="resetCart" class="btn btn-secondary btn-sm">
            <span class="material-icons">clear</span>
          </button>
        </div>

        <!-- Cart Items -->
        <div class="cart-items">
          <div v-if="cartItems.length === 0" class="loading-container">
            <span class="material-icons" style="font-size: 3rem; margin-bottom: 1rem;">shopping_cart</span>
            <p>Giỏ hàng trống</p>
          </div>
          <div v-else>
            <div v-for="(item, index) in cartItems" :key="index" class="cart-item">
              <div class="item-image">
                <span class="material-icons">image</span>
              </div>
              <div class="item-info">
                <h4>{{ item.name }}</h4>
                <p class="item-details">{{ item.sku }}</p>
                <p class="item-price">{{ formatCurrency(item.unitPrice) }}</p>
              </div>
              <div class="item-controls">
                <div class="quantity-control">
                  <button @click="updateQuantity(index, item.quantity - 1)" class="quantity-btn">-</button>
                  <input
                    v-model.number="item.quantity"
                    @change="updateQuantity(index, item.quantity)"
                    class="quantity-input"
                    type="number"
                    min="1"
                  />
                  <button @click="updateQuantity(index, item.quantity + 1)" class="quantity-btn">+</button>
                </div>
                <button @click="removeFromCart(index)" class="remove-btn">
                  <span class="material-icons">delete</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Cart Summary -->
        <div class="cart-summary">
          <div class="summary-row">
            <span class="summary-label">Tạm tính:</span>
            <span class="summary-value">{{ formatCurrency(subtotal) }}</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">Giảm giá:</span>
            <span class="summary-value">-{{ formatCurrency(discountAmount) }}</span>
          </div>
          <div class="summary-row">
            <span class="total-label">Tổng cộng:</span>
            <span class="total-value">{{ formatCurrency(totalAmount) }}</span>
          </div>
        </div>

        <!-- Discount Section -->
        <div class="discount-section">
          <div class="discount-input">
            <input
              v-model="discountCode"
              placeholder="Mã giảm giá..."
              class="form-control"
            />
            <button @click="applyDiscount" class="btn btn-secondary">
              <span class="material-icons">local_offer</span>
            </button>
          </div>
        </div>

        <!-- Payment Section -->
        <div class="payment-section">
          <div class="payment-options">
            <button
              @click="paymentMethod = 'cash'"
              :class="{ active: paymentMethod === 'cash' }"
              class="payment-option"
            >
              <span class="material-icons">money</span>
              Tiền mặt
            </button>
            <button
              @click="paymentMethod = 'card'"
              :class="{ active: paymentMethod === 'card' }"
              class="payment-option"
            >
              <span class="material-icons">credit_card</span>
              Thẻ
            </button>
          </div>
        </div>

        <!-- Checkout Button -->
        <button
          @click="processOrder"
          :disabled="cartItems.length === 0 || processing"
          class="checkout-btn"
        >
          <span v-if="processing" class="material-icons animate-spin">autorenew</span>
          <span v-else class="material-icons">shopping_cart_checkout</span>
          {{ processing ? 'Đang xử lý...' : 'Thanh toán' }}
        </button>
      </div>
    </div>

    <!-- Receipt Modal -->
    <div v-if="showReceipt" class="modal-overlay" @click="showReceipt = false">
      <div class="receipt" @click.stop>
        <div class="receipt-header">
          <h2 class="receipt-title">Hóa đơn</h2>
          <p class="receipt-subtitle">{{ currentReceipt?.orderNumber }}</p>
        </div>
        <div class="receipt-body">
          <div v-for="item in currentReceipt?.items" :key="item.id" class="receipt-item">
            <span>{{ item.name }} x{{ item.quantity }}</span>
            <span>{{ formatCurrency(item.unitPrice * item.quantity) }}</span>
          </div>
        </div>
        <div class="receipt-footer">
          <div class="receipt-total">
            <span>Tổng cộng:</span>
            <span>{{ formatCurrency(currentReceipt?.totalAmount) }}</span>
          </div>
          <div class="receipt-actions">
            <button @click="printReceipt" class="btn btn-primary">
              <span class="material-icons">print</span>
              In hóa đơn
            </button>
            <button @click="showReceipt = false" class="btn btn-secondary">
              <span class="material-icons">close</span>
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Shortcuts Modal -->
    <div v-if="showShortcuts" class="modal-overlay" @click="showShortcuts = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons">keyboard</span>
            Phím tắt
          </h2>
          <button @click="showShortcuts = false" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="shortcuts-list">
            <div class="shortcut-item">
              <div class="shortcut-key">
                <kbd>Ctrl</kbd> + <kbd>K</kbd>
              </div>
              <div class="shortcut-description">Mở/đóng thanh tìm kiếm</div>
            </div>
            <div class="shortcut-item">
              <div class="shortcut-key">
                <kbd>Ctrl</kbd> + <kbd>B</kbd>
              </div>
              <div class="shortcut-description">Mở/đóng quét mã vạch</div>
            </div>
            <div class="shortcut-item">
              <div class="shortcut-key">
                <kbd>Enter</kbd>
              </div>
              <div class="shortcut-description">Thêm sản phẩm vào giỏ hàng</div>
            </div>
            <div class="shortcut-item">
              <div class="shortcut-key">
                <kbd>Ctrl</kbd> + <kbd>Enter</kbd>
              </div>
              <div class="shortcut-description">Thanh toán đơn hàng</div>
            </div>
            <div class="shortcut-item">
              <div class="shortcut-key">
                <kbd>Esc</kbd>
              </div>
              <div class="shortcut-description">Đóng modal/hủy thao tác</div>
            </div>
            <div class="shortcut-item">
              <div class="shortcut-key">
                <kbd>Ctrl</kbd> + <kbd>R</kbd>
              </div>
              <div class="shortcut-description">Làm mới giỏ hàng</div>
            </div>
            <div class="shortcut-item">
              <div class="shortcut-key">
                <kbd>+</kbd> / <kbd>-</kbd>
              </div>
              <div class="shortcut-description">Tăng/giảm số lượng sản phẩm</div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showShortcuts = false" class="btn btn-primary">
            <span class="material-icons">check</span>
            Đã hiểu
          </button>
        </div>
      </div>
    </div>

    <!-- History Modal -->
    <div v-if="showHistory" class="modal-overlay" @click="showHistory = false">
      <div class="modal-content modal-large" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons">history</span>
            Lịch sử bán hàng
          </h2>
          <button @click="showHistory = false" class="modal-close">
            <span class="material-icons">close</span>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="loadingHistory" class="loading-container">
            <div class="loading-spinner"></div>
            <p>Đang tải lịch sử...</p>
          </div>
          <div v-else-if="salesHistory.length === 0" class="empty-state">
            <span class="material-icons">history</span>
            <p>Chưa có lịch sử bán hàng</p>
          </div>
          <div v-else class="history-list">
            <div v-for="order in salesHistory" :key="order.id" class="history-item">
              <div class="history-icon">
                <span class="material-icons">receipt</span>
              </div>
              <div class="history-content">
                <div class="history-header">
                  <h4>{{ order.orderNumber || `Đơn hàng #${order.id}` }}</h4>
                  <span class="history-date">{{ formatDate(order.createdAt) }}</span>
                </div>
                <div class="history-details">
                  <span class="history-amount">{{ formatCurrency(order.totalAmount) }}</span>
                  <span class="history-payment">{{ getPaymentMethodLabel(order.paymentMethod) }}</span>
                </div>
                <div v-if="order.items" class="history-items">
                  <span v-for="(item, idx) in order.items.slice(0, 3)" :key="idx" class="item-tag">
                    {{ item.name }} x{{ item.quantity }}
                  </span>
                  <span v-if="order.items.length > 3" class="item-tag">+{{ order.items.length - 3 }} sản phẩm</span>
                </div>
              </div>
              <button @click="viewOrderDetails(order)" class="btn-icon btn-view" title="Xem chi tiết">
                <span class="material-icons">visibility</span>
              </button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showHistory = false" class="btn btn-secondary">
            <span class="material-icons">close</span>
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'

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
const discountCode = ref('')
const discountAmount = ref(0)
const paymentMethod = ref('cash')
const showBarcode = ref(false)
const barcodeValue = ref('')
const showReceipt = ref(false)
const currentReceipt = ref(null)
const showShortcuts = ref(false)
const showHistory = ref(false)
const salesHistory = ref([])
const loadingHistory = ref(false)

// Computed
const subtotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.unitPrice * item.quantity), 0)
})

const totalAmount = computed(() => {
  return subtotal.value - discountAmount.value
})

// Methods
const loadData = async () => {
  try {
    loading.value = true
    
    // Load products, brands, and categories in parallel
    const [productsResult, brandsResult, categoriesResult] = await Promise.all([
      adminStore.fetchProducts(0, 100, { isActive: true }),
      adminStore.fetchBrands(),
      adminStore.fetchCategories()
    ])
    
    products.value = productsResult.content || productsResult || []
    brands.value = brandsResult.content || brandsResult || []
    categories.value = categoriesResult.content || categoriesResult || []
    
  } catch (error) {
    console.error('Error loading data:', error)
    ElMessage.error('Không thể tải dữ liệu')
  } finally {
    loading.value = false
  }
}

const searchProducts = async () => {
  try {
    loading.value = true
    
    const filters = {
      isActive: true,
      search: searchQuery.value
    }
    
    const result = await adminStore.fetchProducts(0, 100, filters)
    products.value = result.content || result || []
    
  } catch (error) {
    console.error('Error searching products:', error)
    ElMessage.error('Không thể tìm kiếm sản phẩm')
  } finally {
    loading.value = false
  }
}

const filterProducts = async () => {
  try {
    loading.value = true
    
    const filters = {
      isActive: true,
      search: searchQuery.value,
      brandId: filterBrand.value,
      categoryId: filterCategory.value
    }
    
    const result = await adminStore.fetchProducts(0, 100, filters)
    products.value = result.content || result || []
    
  } catch (error) {
    console.error('Error filtering products:', error)
    ElMessage.error('Không thể lọc sản phẩm')
  } finally {
    loading.value = false
  }
}

const handleBarcodeSearch = async () => {
  if (!barcodeValue.value.trim()) return
  
  try {
    loading.value = true
    
    // Search by SKU or barcode
    const filters = {
      isActive: true,
      sku: barcodeValue.value.trim()
    }
    
    const result = await adminStore.fetchProducts(0, 10, filters)
    const foundProducts = result.content || result || []
    
    if (foundProducts.length > 0) {
      addToCart(foundProducts[0])
      barcodeValue.value = ''
      ElMessage.success(`Đã thêm ${foundProducts[0].name} vào giỏ hàng`)
    } else {
      ElMessage.warning('Không tìm thấy sản phẩm với mã này')
    }
    
  } catch (error) {
    console.error('Error searching barcode:', error)
    ElMessage.error('Không thể tìm kiếm sản phẩm')
  } finally {
    loading.value = false
  }
}

const processOrder = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('Giỏ hàng trống')
    return
  }
  
  try {
    processing.value = true
    
    const orderData = {
      items: cartItems.value.map(item => ({
        productId: item.id,
        variantId: item.variantId,
        quantity: item.quantity,
        unitPrice: item.unitPrice
      })),
      customerId: null,
      discountCode: discountCode.value || null,
      discountAmount: discountAmount.value,
      paymentMethod: paymentMethod.value,
      totalAmount: totalAmount.value,
      notes: ''
    }
    
    const result = await adminStore.createPOSOrder(orderData)
    
    // Show receipt
    currentReceipt.value = result
    showReceipt.value = true
    
    // Clear cart
    cartItems.value = []
    discountCode.value = ''
    discountAmount.value = 0
    
    ElMessage.success('Đơn hàng đã được tạo thành công')
    
  } catch (error) {
    console.error('Error processing order:', error)
    ElMessage.error('Không thể tạo đơn hàng')
  } finally {
    processing.value = false
  }
}

const resetCart = () => {
  cartItems.value = []
  discountCode.value = ''
  discountAmount.value = 0
  ElMessage.info('Đã làm mới giỏ hàng')
}

const addToCart = (product) => {
  const existingItem = cartItems.value.find(item => item.id === product.id)
  
  if (existingItem) {
    existingItem.quantity += 1
  } else {
    cartItems.value.push({
      id: product.id,
      name: product.name,
      sku: product.sku,
      unitPrice: product.price,
      quantity: 1,
      variantId: product.variants?.[0]?.id || null
    })
  }
}

const removeFromCart = (index) => {
  cartItems.value.splice(index, 1)
}

const updateQuantity = (index, quantity) => {
  if (quantity <= 0) {
    removeFromCart(index)
  } else {
    cartItems.value[index].quantity = quantity
  }
}

const applyDiscount = () => {
  // Mock discount logic - replace with actual API call
  if (discountCode.value) {
    discountAmount.value = subtotal.value * 0.1 // 10% discount
    ElMessage.success('Đã áp dụng mã giảm giá')
  }
}

const printReceipt = () => {
  if (currentReceipt.value) {
    window.print()
  }
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

const getPaymentMethodLabel = (method) => {
  const labels = {
    cash: 'Tiền mặt',
    card: 'Thẻ',
    bank: 'Chuyển khoản',
    online: 'Online'
  }
  return labels[method] || method
}

const loadSalesHistory = async () => {
  try {
    loadingHistory.value = true
    const result = await adminStore.fetchOrders(0, 50, { 
      status: 'completed',
      sortBy: 'createdAt',
      sortDirection: 'desc'
    })
    salesHistory.value = result.content || result || []
  } catch (error) {
    console.error('Error loading sales history:', error)
    ElMessage.error('Không thể tải lịch sử bán hàng')
    salesHistory.value = []
  } finally {
    loadingHistory.value = false
  }
}

const viewOrderDetails = (order) => {
  // Hiển thị chi tiết đơn hàng
  currentReceipt.value = order
  showReceipt.value = true
  showHistory.value = false
}

// Watch showHistory để load data khi mở modal
watch(showHistory, (newValue) => {
  if (newValue) {
    loadSalesHistory()
  }
})

// Keyboard shortcuts
const handleKeydown = (event) => {
  // Ctrl + K: Toggle search
  if (event.ctrlKey && event.key === 'k') {
    event.preventDefault()
    const searchInput = document.querySelector('.search-input')
    if (searchInput) {
      searchInput.focus()
    }
  }
  
  // Ctrl + B: Toggle barcode scanner
  if (event.ctrlKey && event.key === 'b') {
    event.preventDefault()
    showBarcode.value = !showBarcode.value
    if (showBarcode.value) {
      setTimeout(() => {
        const barcodeInput = document.querySelector('.barcode-input')
        if (barcodeInput) {
          barcodeInput.focus()
        }
      }, 100)
    }
  }
  
  // Ctrl + Enter: Process order
  if (event.ctrlKey && event.key === 'Enter') {
    event.preventDefault()
    if (cartItems.value.length > 0) {
      processOrder()
    }
  }
  
  // Ctrl + R: Reset cart
  if (event.ctrlKey && event.key === 'r') {
    event.preventDefault()
    resetCart()
  }
  
  // Esc: Close modals
  if (event.key === 'Escape') {
    if (showReceipt.value) {
      showReceipt.value = false
    }
    if (showShortcuts.value) {
      showShortcuts.value = false
    }
    if (showHistory.value) {
      showHistory.value = false
    }
    if (showBarcode.value) {
      showBarcode.value = false
    }
  }
}

// Load data on mount
onMounted(() => {
  loadData()
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.admin-sales {
  padding: var(--space-6);
  background: var(--bg-primary);
  min-height: 100vh;
}

.page-header {
  background: var(--gradient-purple-soft);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.1), rgba(139, 92, 246, 0.15));
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.title-section {
  flex: 1;
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

.btn-sm {
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-xs);
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
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.2);
}

.search-input::placeholder {
  color: var(--text-tertiary);
}

.filter-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.form-control {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  transition: all var(--transition-fast);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.2);
}

.form-control::placeholder {
  color: var(--text-tertiary);
}

/* ===== PRODUCTS GRID ===== */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-4);
  flex: 1;
  overflow-y: auto;
  padding-right: var(--space-2);
}

.products-grid::-webkit-scrollbar {
  width: 6px;
}

.products-grid::-webkit-scrollbar-track {
  background: transparent;
}

.products-grid::-webkit-scrollbar-thumb {
  background: var(--border-primary);
  border-radius: 3px;
}

.products-grid::-webkit-scrollbar-thumb:hover {
  background: var(--border-medium);
}

.product-card {
  background: var(--bg-card);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-4);
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
  overflow: hidden;
}

.product-card:hover {
  border-color: var(--accent-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.product-card:active {
  transform: translateY(0);
}

.product-image {
  width: 100%;
  height: 120px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-3);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image .material-icons {
  font-size: 3rem;
  color: var(--text-tertiary);
}

.product-info h3 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-brand {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin: 0 0 var(--space-2) 0;
}

.product-price {
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--accent-primary);
  margin: 0;
}

.product-stock {
  position: absolute;
  top: var(--space-2);
  right: var(--space-2);
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  padding: var(--space-1) var(--space-2);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
}

.product-stock.low {
  background: var(--warning-bg);
  color: var(--warning-text);
  border-color: var(--warning-border);
}

.product-stock.out {
  background: var(--error-bg);
  color: var(--error-text);
  border-color: var(--error-border);
}

/* ===== CART SECTION ===== */
.cart-section {
  background: var(--bg-card);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-card);
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
  padding-bottom: var(--space-4);
  border-bottom: 2px solid var(--border-primary);
}

.cart-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  margin-bottom: var(--space-6);
}

.cart-items::-webkit-scrollbar {
  width: 6px;
}

.cart-items::-webkit-scrollbar-track {
  background: transparent;
}

.cart-items::-webkit-scrollbar-thumb {
  background: var(--border-primary);
  border-radius: 3px;
}

.cart-items::-webkit-scrollbar-thumb:hover {
  background: var(--border-medium);
}

.cart-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--bg-tertiary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-3);
}

.cart-item:last-child {
  margin-bottom: 0;
}

.item-image {
  width: 50px;
  height: 50px;
  background: var(--bg-primary);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.item-image .material-icons {
  font-size: 1.5rem;
  color: var(--text-tertiary);
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-info h4 {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-1) 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-details {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  margin: 0;
}

.item-price {
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  color: var(--accent-primary);
  margin: 0;
}

.item-controls {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  padding: var(--space-1);
}

.quantity-btn {
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.quantity-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-input {
  width: 40px;
  text-align: center;
  border: none;
  background: transparent;
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
}

.quantity-input:focus {
  outline: none;
}

.remove-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: var(--error-bg);
  color: var(--error-text);
  cursor: pointer;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.remove-btn:hover {
  background: var(--error-text);
  color: var(--color-white);
}

/* ===== CART SUMMARY ===== */
.cart-summary {
  border-top: 2px solid var(--border-primary);
  padding-top: var(--space-6);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-3);
}

.summary-row:last-child {
  margin-bottom: 0;
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-primary);
}

.summary-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.summary-value {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.total-label {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.total-value {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--accent-primary);
}

/* ===== DISCOUNT SECTION ===== */
.discount-section {
  margin-bottom: var(--space-6);
}

.discount-input {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-3);
}

.discount-input input {
  flex: 1;
  padding: var(--space-3) var(--space-4);
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
}

.discount-input input:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.2);
}

.discount-input input::placeholder {
  color: var(--text-tertiary);
}

/* ===== PAYMENT SECTION ===== */
.payment-section {
  margin-bottom: var(--space-6);
}

.payment-options {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.payment-option {
  padding: var(--space-3) var(--space-4);
  background: var(--bg-primary);
  border: 2px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  text-align: center;
}

.payment-option:hover {
  border-color: var(--accent-primary);
  background: var(--gradient-purple-soft);
}

.payment-option.active {
  border-color: var(--accent-primary);
  background: var(--gradient-primary);
  color: var(--color-white);
}

/* ===== CHECKOUT BUTTON ===== */
.checkout-btn {
  width: 100%;
  padding: var(--space-4) var(--space-6);
  background: var(--gradient-primary);
  color: var(--color-white);
  border: none;
  border-radius: var(--radius-xl);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-md);
}

.checkout-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.checkout-btn:active:not(:disabled) {
  transform: translateY(0);
}

.checkout-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ===== LOADING ===== */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12);
  color: var(--text-tertiary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-primary);
  border-top: 3px solid var(--accent-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--space-4);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* ===== MODALS ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease-out;
  padding: var(--space-4);
}

.modal-content {
  background: linear-gradient(135deg, var(--bg-card) 0%, rgba(167, 139, 250, 0.05) 100%);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: 0;
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(167, 139, 250, 0.1);
  animation: slideUp 0.3s ease-out;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  overflow: hidden;
  position: relative;
}

.modal-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-primary);
  z-index: 1;
}

.modal-large {
  max-width: 900px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 2px solid var(--border-primary);
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.1) 0%, transparent 100%);
  position: relative;
  z-index: 0;
}

.modal-title {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.modal-title .material-icons {
  font-size: 1.5rem;
  color: var(--accent-primary);
}

.modal-close {
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(167, 139, 250, 0.1);
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
  border: 1px solid transparent;
}

.modal-close:hover {
  background: var(--gradient-primary);
  color: var(--color-white);
  border-color: var(--accent-primary);
  transform: rotate(90deg) scale(1.1);
  box-shadow: 0 4px 12px rgba(167, 139, 250, 0.4);
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-6);
  background: rgba(167, 139, 250, 0.02);
}

.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-track {
  background: transparent;
}

.modal-body::-webkit-scrollbar-thumb {
  background: var(--border-primary);
  border-radius: 4px;
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background: var(--accent-primary);
}

.modal-footer {
  padding: var(--space-6);
  border-top: 2px solid var(--border-primary);
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.05) 0%, transparent 100%);
}

.modal-footer .btn {
  min-width: 120px;
  font-weight: var(--font-semibold);
  transition: all 0.3s ease;
}

.modal-footer .btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(167, 139, 250, 0.3);
}

/* Shortcuts Modal */
.shortcuts-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.shortcut-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: linear-gradient(135deg, var(--bg-tertiary) 0%, rgba(167, 139, 250, 0.05) 100%);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.shortcut-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--gradient-primary);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.shortcut-item:hover {
  border-color: var(--accent-primary);
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.1) 0%, rgba(167, 139, 250, 0.05) 100%);
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(167, 139, 250, 0.2);
}

.shortcut-item:hover::before {
  transform: scaleY(1);
}

.shortcut-key {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  min-width: 140px;
  flex-shrink: 0;
}

.shortcut-key kbd {
  padding: var(--space-2) var(--space-4);
  background: linear-gradient(135deg, var(--bg-primary) 0%, rgba(167, 139, 250, 0.1) 100%);
  border: 1px solid var(--border-medium);
  border-radius: var(--radius-md);
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15), inset 0 1px 0 rgba(255, 255, 255, 0.1);
  transition: all 0.2s ease;
  font-family: 'Courier New', monospace;
  letter-spacing: 0.5px;
  position: relative;
}

.shortcut-key kbd:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(167, 139, 250, 0.3), inset 0 1px 0 rgba(255, 255, 255, 0.2);
  border-color: var(--accent-primary);
}

.shortcut-key kbd:active {
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}

.shortcut-description {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-weight: var(--font-medium);
  transition: color 0.2s ease;
}

.shortcut-item:hover .shortcut-description {
  color: var(--text-primary);
}

/* History Modal */
.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.history-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-5);
  background: linear-gradient(135deg, var(--bg-tertiary) 0%, rgba(167, 139, 250, 0.05) 100%);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.history-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: var(--gradient-primary);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.history-item:hover {
  border-color: var(--accent-primary);
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.1) 0%, rgba(167, 139, 250, 0.05) 100%);
  transform: translateX(4px);
  box-shadow: 0 8px 24px rgba(167, 139, 250, 0.25);
}

.history-item:hover::before {
  transform: scaleY(1);
}

.history-icon {
  width: 56px;
  height: 56px;
  background: var(--gradient-primary);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(167, 139, 250, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.history-icon::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transform: rotate(45deg);
  transition: transform 0.6s ease;
}

.history-item:hover .history-icon::before {
  transform: rotate(45deg) translate(100%, 100%);
}

.history-item:hover .history-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 20px rgba(167, 139, 250, 0.6), inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.history-icon .material-icons {
  font-size: 1.75rem;
  color: var(--color-white);
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.history-content {
  flex: 1;
  min-width: 0;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-2);
}

.history-header h4 {
  font-size: var(--text-base);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
  background: linear-gradient(135deg, var(--text-primary) 0%, var(--accent-primary) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.history-date {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  padding: var(--space-1) var(--space-3);
  background: rgba(167, 139, 250, 0.1);
  border-radius: var(--radius-md);
  border: 1px solid rgba(167, 139, 250, 0.2);
  font-weight: var(--font-medium);
}

.history-details {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-2);
}

.history-amount {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  background: linear-gradient(135deg, var(--accent-primary) 0%, rgba(167, 139, 250, 0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 8px rgba(167, 139, 250, 0.3);
}

.history-payment {
  font-size: var(--text-xs);
  color: var(--text-primary);
  padding: var(--space-2) var(--space-3);
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.15) 0%, rgba(167, 139, 250, 0.05) 100%);
  border-radius: var(--radius-md);
  border: 1px solid rgba(167, 139, 250, 0.3);
  font-weight: var(--font-semibold);
  transition: all 0.2s ease;
}

.history-item:hover .history-payment {
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.25) 0%, rgba(167, 139, 250, 0.15) 100%);
  border-color: var(--accent-primary);
  transform: scale(1.05);
}

.history-items {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.item-tag {
  font-size: var(--text-xs);
  color: var(--text-primary);
  padding: var(--space-2) var(--space-3);
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.1) 0%, rgba(167, 139, 250, 0.05) 100%);
  border-radius: var(--radius-md);
  border: 1px solid rgba(167, 139, 250, 0.2);
  font-weight: var(--font-medium);
  transition: all 0.2s ease;
}

.item-tag:hover {
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.2) 0%, rgba(167, 139, 250, 0.1) 100%);
  border-color: var(--accent-primary);
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(167, 139, 250, 0.2);
}

.btn-icon {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(167, 139, 250, 0.1);
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  border: 1px solid rgba(167, 139, 250, 0.2);
}

.btn-icon:hover {
  background: var(--gradient-primary);
  color: var(--color-white);
  border-color: var(--accent-primary);
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 16px rgba(167, 139, 250, 0.4);
}

.btn-icon:active {
  transform: scale(0.95);
}

.btn-view .material-icons {
  font-size: 1.25rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12);
  color: var(--text-tertiary);
  text-align: center;
}

.empty-state .material-icons {
  font-size: 5rem;
  margin-bottom: var(--space-4);
  opacity: 0.3;
  background: linear-gradient(135deg, var(--accent-primary) 0%, rgba(167, 139, 250, 0.5) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.05);
  }
}

.empty-state p {
  font-size: var(--text-base);
  margin: 0;
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.receipt {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  max-width: 400px;
  width: 100%;
  box-shadow: var(--shadow-xl);
}

.receipt-header {
  text-align: center;
  margin-bottom: var(--space-6);
  padding-bottom: var(--space-4);
  border-bottom: 2px solid var(--border-primary);
}

.receipt-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.receipt-subtitle {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin: 0;
}

.receipt-body {
  margin-bottom: var(--space-6);
}

.receipt-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-2) 0;
  border-bottom: 1px solid var(--border-primary);
}

.receipt-item:last-child {
  border-bottom: none;
}

.receipt-footer {
  border-top: 2px solid var(--border-primary);
  padding-top: var(--space-4);
}

.receipt-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

.receipt-actions {
  display: flex;
  gap: var(--space-3);
}

/* ===== ANIMATIONS ===== */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* ===== RESPONSIVE ===== */
@media (max-width: 1200px) {
  .pos-grid {
    grid-template-columns: 1fr 380px;
  }
}

@media (max-width: 1024px) {
  .pos-grid {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .product-section {
    height: 60vh;
  }
  
  .cart-section {
    height: 40vh;
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
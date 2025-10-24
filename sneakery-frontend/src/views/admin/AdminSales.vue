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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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

// Load data on mount
onMounted(() => {
  loadData()
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
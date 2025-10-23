<template>
  <div class="admin-page admin-sales">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">
            <i class="material-icons">shopping_cart</i>
            Bán Hàng (POS)
          </h1>
          <p class="page-subtitle">
            <i class="material-icons">info</i>
            Hệ thống bán hàng tại quầy - Point of Sale
          </p>
        </div>
        <div class="header-actions">
          <button @click="resetCart" class="btn btn-secondary">
            <i class="material-icons">refresh</i>
            Làm mới
          </button>
          <button @click="showCustomerDialog = true" class="btn btn-primary">
            <i class="material-icons">person_add</i>
            Chọn khách hàng
          </button>
        </div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="pos-grid">
      <!-- Left: Product Selection -->
      <div class="product-section">
        <!-- Search & Filters -->
        <div class="search-section">
          <div class="search-box">
            <i class="material-icons search-icon">search</i>
            <input 
              v-model="searchQuery"
              type="text" 
              class="search-input" 
              placeholder="Tìm sản phẩm (tên, SKU, barcode)..."
              @input="searchProducts"
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

        <div v-else-if="products.length === 0" class="empty-state">
          <i class="material-icons">inventory_2</i>
          <h3>Không tìm thấy sản phẩm</h3>
          <p>Vui lòng thử từ khóa khác hoặc điều chỉnh bộ lọc</p>
        </div>

        <div v-else class="products-grid">
          <div 
            v-for="product in products" 
            :key="product.id"
            class="product-card"
            @click="addToCart(product)"
          >
            <div class="product-image">
              <img :src="product.imageUrl || '/placeholder.png'" :alt="product.name" />
              <div class="product-stock" :class="{ 'low-stock': product.stockQuantity < 10 }">
                <i class="material-icons">inventory</i>
                {{ product.stockQuantity }}
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
            <button class="btn-add-cart" @click.stop="addToCart(product)">
              <i class="material-icons">add_shopping_cart</i>
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
                <i class="material-icons">person</i>
              </div>
              <div class="customer-details">
                <strong>{{ selectedCustomer.fullName }}</strong>
                <p>{{ selectedCustomer.email }}</p>
              </div>
              <button @click="selectedCustomer = null" class="btn-remove">
                <i class="material-icons">close</i>
              </button>
            </div>
            <div v-else class="customer-empty" @click="showCustomerDialog = true">
              <i class="material-icons">person_add_alt</i>
              <p>Chọn khách hàng (không bắt buộc)</p>
            </div>
          </div>

          <!-- Cart Items -->
          <div class="cart-header">
            <h3>Giỏ hàng ({{ cartItems.length }} sản phẩm)</h3>
            <button v-if="cartItems.length > 0" @click="clearCart" class="btn-clear">
              <i class="material-icons">delete_outline</i>
            </button>
          </div>

          <div v-if="cartItems.length === 0" class="cart-empty">
            <i class="material-icons">shopping_cart</i>
            <p>Giỏ hàng trống</p>
            <small>Chọn sản phẩm bên trái để thêm vào giỏ</small>
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
                    <i class="material-icons">remove</i>
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
                    <i class="material-icons">add</i>
                  </button>
                </div>
                <button @click="removeFromCart(item)" class="btn-remove-item">
                  <i class="material-icons">delete</i>
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
                  <i class="material-icons">payments</i>
                  Tiền mặt
                </label>
                <label class="payment-option">
                  <input type="radio" v-model="paymentMethod" value="card" />
                  <i class="material-icons">credit_card</i>
                  Thẻ
                </label>
                <label class="payment-option">
                  <input type="radio" v-model="paymentMethod" value="transfer" />
                  <i class="material-icons">account_balance</i>
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
              <i class="material-icons">point_of_sale</i>
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
            <i class="material-icons">close</i>
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
                <i class="material-icons">person</i>
              </div>
              <div class="customer-info">
                <strong>{{ customer.fullName }}</strong>
                <p>{{ customer.email }}</p>
                <p>{{ customer.phoneNumber }}</p>
              </div>
              <i class="material-icons">chevron_right</i>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import adminService from '@/services/adminService'

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

// Lifecycle
onMounted(() => {
  fetchProducts()
  fetchBrandsAndCategories()
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
  margin-bottom: var(--space-8);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-6);
  flex-wrap: wrap;
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
  gap: var(--space-3);
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
}

.product-stock.low-stock {
  background: var(--warning-solid);
}

.product-stock .material-icons {
  font-size: 0.875rem;
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

/* ===== RESPONSIVE ===== */
@media (max-width: 1024px) {
  .pos-grid {
    grid-template-columns: 1fr;
    height: auto;
  }
  
  .cart-section {
    max-height: 600px;
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


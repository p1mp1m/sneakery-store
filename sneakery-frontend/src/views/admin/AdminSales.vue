<template>
  <div class="max-w-[1920px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">point_of_sale</i>
            Hệ thống POS
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
            <i class="material-icons text-xs">store</i>
            Bán hàng tại quầy
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="showShortcuts = true" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">keyboard</i>
            Phím tắt
          </button>
          <button @click="showHistory = true" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">history</i>
            Lịch sử
          </button>
          <button @click="resetCart" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">refresh</i>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Barcode Scanner -->
    <transition
      enter-active-class="transition-all duration-300 ease-out"
      leave-active-class="transition-all duration-300 ease-in"
      enter-from-class="opacity-0 -translate-y-4"
      enter-to-class="opacity-100 translate-y-0"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 -translate-y-4"
    >
      <div v-if="showBarcode" class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="flex items-center gap-3">
          <i class="material-icons text-gray-500 dark:text-gray-400">qr_code_scanner</i>
          <input
            ref="barcodeInput"
            v-model="barcodeValue"
            @keyup.enter="handleBarcodeSearch"
            class="flex-1 px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            placeholder="Quét mã vạch hoặc nhập SKU..."
          />
          <button @click="showBarcode = false" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">close</i>
          </button>
        </div>
      </div>
    </transition>

    <!-- POS Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-[1fr_400px] gap-4">
      <!-- Product Section -->
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <!-- Search Section -->
        <div class="space-y-4 mb-4">
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500">search</i>
            <input
              v-model="searchQuery"
              @input="searchProducts"
              class="w-full pl-10 pr-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Tìm kiếm sản phẩm..."
            />
          </div>
          <div class="grid grid-cols-2 gap-3">
            <select v-model="filterBrand" @change="filterProducts" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="">Tất cả thương hiệu</option>
              <option v-for="brand in brands" :key="brand.id" :value="brand.id">
                {{ brand.name }}
              </option>
            </select>
            <select v-model="filterCategory" @change="filterProducts" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="">Tất cả danh mục</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>
        </div>

        <!-- Products Grid -->
        <div v-if="loading" class="flex flex-col items-center justify-center p-12">
          <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
          <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải sản phẩm...</p>
        </div>
        <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3 max-h-[calc(100vh-300px)] overflow-y-auto">
          <div
            v-for="product in products"
            :key="product.id"
            @click="addToCart(product)"
            class="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-900 transition-colors border border-gray-200 dark:border-gray-700"
          >
            <div class="aspect-square bg-gray-100 dark:bg-gray-700 rounded-lg mb-2 flex items-center justify-center overflow-hidden">
              <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" class="w-full h-full object-cover" />
              <i v-else class="material-icons text-gray-400 dark:text-gray-500 text-4xl">image</i>
            </div>
            <div class="space-y-1">
              <h3 class="text-sm font-medium text-gray-900 dark:text-gray-100 line-clamp-2">{{ product.name }}</h3>
              <p class="text-xs text-gray-500 dark:text-gray-400">{{ product.brandName }}</p>
              <p class="text-sm font-semibold text-purple-600 dark:text-purple-400">{{ formatCurrency(product.price) }}</p>
              <div class="flex items-center justify-between mt-1">
                <span 
                  class="text-xs px-2 py-0.5 rounded-full"
                  :class="{
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': product.stockQuantity === 0,
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': product.stockQuantity > 0 && product.stockQuantity < 10,
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': product.stockQuantity >= 10
                  }"
                >
                  {{ product.stockQuantity }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Cart Section -->
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 flex flex-col h-[calc(100vh-200px)]">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100">Giỏ hàng</h2>
          <button @click="resetCart" class="p-1.5 text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
            <i class="material-icons text-base">clear</i>
          </button>
        </div>

        <!-- Cart Items -->
        <div class="flex-1 overflow-y-auto mb-4 space-y-2">
          <div v-if="cartItems.length === 0" class="flex flex-col items-center justify-center p-12 text-center">
            <i class="material-icons text-gray-400 dark:text-gray-500 text-5xl mb-4">shopping_cart</i>
            <p class="text-sm text-gray-500 dark:text-gray-400">Giỏ hàng trống</p>
          </div>
          <div v-else class="space-y-2">
            <div v-for="(item, index) in cartItems" :key="index" class="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700">
              <div class="flex items-start gap-3">
                <div class="w-12 h-12 bg-gray-100 dark:bg-gray-700 rounded-lg flex items-center justify-center flex-shrink-0">
                  <i class="material-icons text-gray-400 dark:text-gray-500">image</i>
                </div>
                <div class="flex-1 min-w-0">
                  <h4 class="text-sm font-medium text-gray-900 dark:text-gray-100 truncate">{{ item.name }}</h4>
                  <p class="text-xs text-gray-500 dark:text-gray-400">{{ item.sku }}</p>
                  <p class="text-sm font-semibold text-purple-600 dark:text-purple-400 mt-1">{{ formatCurrency(item.unitPrice) }}</p>
                </div>
                <div class="flex flex-col items-end gap-2">
                  <div class="flex items-center gap-1">
                    <button @click="updateQuantity(index, item.quantity - 1)" class="w-7 h-7 flex items-center justify-center bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors text-sm font-medium">-</button>
                    <input
                      v-model.number="item.quantity"
                      @change="updateQuantity(index, item.quantity)"
                      class="w-12 h-7 text-center bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded text-sm text-gray-900 dark:text-gray-100"
                      type="number"
                      min="1"
                    />
                    <button @click="updateQuantity(index, item.quantity + 1)" class="w-7 h-7 flex items-center justify-center bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors text-sm font-medium">+</button>
                  </div>
                  <button @click="removeFromCart(index)" class="p-1 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded transition-colors">
                    <i class="material-icons text-base">delete</i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Cart Summary -->
        <div class="space-y-2 py-4 border-t border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-between text-sm">
            <span class="text-gray-600 dark:text-gray-400">Tạm tính:</span>
            <span class="text-gray-900 dark:text-gray-100 font-medium">{{ formatCurrency(subtotal) }}</span>
          </div>
          <div class="flex items-center justify-between text-sm">
            <span class="text-gray-600 dark:text-gray-400">Giảm giá:</span>
            <span class="text-red-600 dark:text-red-400 font-medium">-{{ formatCurrency(discountAmount) }}</span>
          </div>
          <div class="flex items-center justify-between pt-2 border-t border-gray-200 dark:border-gray-700">
            <span class="text-base font-bold text-gray-900 dark:text-gray-100">Tổng cộng:</span>
            <span class="text-lg font-bold text-purple-600 dark:text-purple-400">{{ formatCurrency(totalAmount) }}</span>
          </div>
        </div>

        <!-- Discount Section -->
        <div class="py-4 border-t border-gray-200 dark:border-gray-700">
          <div class="flex items-center gap-2">
            <input
              v-model="discountCode"
              placeholder="Mã giảm giá..."
              class="flex-1 px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
            <button @click="applyDiscount" class="p-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
              <i class="material-icons text-base">local_offer</i>
            </button>
          </div>
        </div>

        <!-- Payment Section -->
        <div class="py-4 border-t border-gray-200 dark:border-gray-700">
          <div class="grid grid-cols-2 gap-2">
            <button
              @click="paymentMethod = 'cash'"
              class="flex flex-col items-center gap-2 p-3 rounded-lg border-2 transition-all"
              :class="paymentMethod === 'cash' ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400' : 'border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:border-purple-300 dark:hover:border-purple-700'"
            >
              <i class="material-icons">money</i>
              <span class="text-xs font-medium">Tiền mặt</span>
            </button>
            <button
              @click="paymentMethod = 'card'"
              class="flex flex-col items-center gap-2 p-3 rounded-lg border-2 transition-all"
              :class="paymentMethod === 'card' ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400' : 'border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:border-purple-300 dark:hover:border-purple-700'"
            >
              <i class="material-icons">credit_card</i>
              <span class="text-xs font-medium">Thẻ</span>
            </button>
          </div>
        </div>

        <!-- Checkout Button -->
        <button
          @click="processOrder"
          :disabled="cartItems.length === 0 || processing"
          class="w-full flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <i class="material-icons text-base" :class="{ 'animate-spin': processing }">{{ processing ? 'autorenew' : 'shopping_cart_checkout' }}</i>
          {{ processing ? 'Đang xử lý...' : 'Thanh toán' }}
        </button>
      </div>
    </div>

    <!-- Receipt Modal -->
    <div v-if="showReceipt" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="showReceipt = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-md w-full border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="p-4 border-b border-gray-200 dark:border-gray-700 text-center">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100">Hóa đơn</h2>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">{{ currentReceipt?.orderNumber }}</p>
        </div>
        <div class="p-4 space-y-2 max-h-[400px] overflow-y-auto">
          <div v-for="item in currentReceipt?.items" :key="item.id" class="flex items-center justify-between py-2 border-b border-gray-200 dark:border-gray-700">
            <span class="text-sm text-gray-700 dark:text-gray-300">{{ item.name }} x{{ item.quantity }}</span>
            <span class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ formatCurrency(item.unitPrice * item.quantity) }}</span>
          </div>
        </div>
        <div class="p-4 border-t border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-between mb-4">
            <span class="text-base font-bold text-gray-900 dark:text-gray-100">Tổng cộng:</span>
            <span class="text-lg font-bold text-purple-600 dark:text-purple-400">{{ formatCurrency(currentReceipt?.totalAmount) }}</span>
          </div>
          <div class="flex items-center gap-3">
            <button @click="printReceipt" class="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium">
              <i class="material-icons text-base">print</i>
              In hóa đơn
            </button>
            <button @click="showReceipt = false" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
              <i class="material-icons text-base">close</i>
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Shortcuts Modal -->
    <div v-if="showShortcuts" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="showShortcuts = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">keyboard</i>
            Phím tắt
          </h2>
          <button @click="showShortcuts = false" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4 space-y-3">
          <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
            <div class="flex items-center gap-2">
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">Ctrl</kbd>
              <span class="text-gray-500 dark:text-gray-400">+</span>
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">K</kbd>
            </div>
            <div class="text-sm text-gray-700 dark:text-gray-300">Mở/đóng thanh tìm kiếm</div>
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
            <div class="flex items-center gap-2">
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">Ctrl</kbd>
              <span class="text-gray-500 dark:text-gray-400">+</span>
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">B</kbd>
            </div>
            <div class="text-sm text-gray-700 dark:text-gray-300">Mở/đóng quét mã vạch</div>
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
            <div class="flex items-center gap-2">
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">Enter</kbd>
            </div>
            <div class="text-sm text-gray-700 dark:text-gray-300">Thêm sản phẩm vào giỏ hàng</div>
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
            <div class="flex items-center gap-2">
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">Ctrl</kbd>
              <span class="text-gray-500 dark:text-gray-400">+</span>
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">Enter</kbd>
            </div>
            <div class="text-sm text-gray-700 dark:text-gray-300">Thanh toán đơn hàng</div>
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
            <div class="flex items-center gap-2">
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">Esc</kbd>
            </div>
            <div class="text-sm text-gray-700 dark:text-gray-300">Đóng modal/hủy thao tác</div>
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
            <div class="flex items-center gap-2">
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">Ctrl</kbd>
              <span class="text-gray-500 dark:text-gray-400">+</span>
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">R</kbd>
            </div>
            <div class="text-sm text-gray-700 dark:text-gray-300">Làm mới giỏ hàng</div>
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
            <div class="flex items-center gap-2">
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">+</kbd>
              <span class="text-gray-500 dark:text-gray-400">/</span>
              <kbd class="px-2 py-1 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded text-xs font-mono text-gray-900 dark:text-gray-100">-</kbd>
            </div>
            <div class="text-sm text-gray-700 dark:text-gray-300">Tăng/giảm số lượng sản phẩm</div>
          </div>
        </div>
        <div class="flex items-center justify-end p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="showShortcuts = false" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium">
            <i class="material-icons text-base">check</i>
            Đã hiểu
          </button>
        </div>
      </div>
    </div>

    <!-- History Modal -->
    <div v-if="showHistory" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="showHistory = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">history</i>
            Lịch sử bán hàng
          </h2>
          <button @click="showHistory = false" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="loadingHistory" class="flex flex-col items-center justify-center p-12">
            <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
            <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải lịch sử...</p>
          </div>
          <div v-else-if="salesHistory.length === 0" class="flex flex-col items-center justify-center p-12">
            <i class="material-icons text-gray-400 dark:text-gray-500 text-5xl mb-4">history</i>
            <p class="text-sm text-gray-500 dark:text-gray-400">Chưa có lịch sử bán hàng</p>
          </div>
          <div v-else class="space-y-3">
            <div v-for="order in salesHistory" :key="order.id" class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700 hover:bg-gray-100 dark:hover:bg-gray-900 transition-colors">
              <div class="flex items-start gap-4">
                <div class="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center flex-shrink-0">
                  <i class="material-icons text-purple-600 dark:text-purple-400">receipt</i>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center justify-between mb-2">
                    <h4 class="text-sm font-semibold text-gray-900 dark:text-gray-100">{{ order.orderNumber || `Đơn hàng #${order.id}` }}</h4>
                    <span class="text-xs text-gray-500 dark:text-gray-400">{{ formatDate(order.createdAt) }}</span>
                  </div>
                  <div class="flex items-center gap-4 mb-2">
                    <span class="text-sm font-semibold text-purple-600 dark:text-purple-400">{{ formatCurrency(order.totalAmount) }}</span>
                    <span class="text-xs px-2 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-400 rounded-full">{{ getPaymentMethodLabel(order.paymentMethod) }}</span>
                  </div>
                  <div v-if="order.items" class="flex flex-wrap gap-2 mt-2">
                    <span v-for="(item, idx) in order.items.slice(0, 3)" :key="idx" class="text-xs px-2 py-1 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded">
                      {{ item.name }} x{{ item.quantity }}
                    </span>
                    <span v-if="order.items.length > 3" class="text-xs px-2 py-1 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded">
                      +{{ order.items.length - 3 }} sản phẩm
                    </span>
                  </div>
                </div>
                <button @click="viewOrderDetails(order)" class="p-2 text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors" title="Xem chi tiết">
                  <i class="material-icons text-base">visibility</i>
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="showHistory = false" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">close</i>
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
    const searchInput = document.querySelector('input[placeholder="Tìm kiếm sản phẩm..."]')
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
        barcodeInput.value?.focus()
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



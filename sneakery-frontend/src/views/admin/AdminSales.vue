<template>
  <div class="min-h-screen bg-gradient-to-br from-purple-50 via-white to-blue-50 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 pb-6">
    <div class="max-w-[1920px] mx-auto w-full p-4 md:p-6 space-y-4 md:space-y-6">
      <!-- Premium Header with Gradient -->
      <div class="relative overflow-hidden p-6 bg-gradient-to-r from-purple-600 via-purple-500 to-indigo-600 rounded-2xl shadow-2xl border border-purple-200 dark:border-purple-800">
        <div class="absolute inset-0 opacity-10">
          <div class="absolute inset-0" style="background-image: repeating-linear-gradient(45deg, transparent, transparent 10px, rgba(255,255,255,0.1) 10px, rgba(255,255,255,0.1) 20px);"></div>
        </div>
        <div class="relative flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center shadow-lg">
              <i class="material-icons text-white text-3xl">point_of_sale</i>
            </div>
            <div>
              <h1 class="text-2xl font-bold text-white flex items-center gap-2 drop-shadow-lg">
                Hệ thống POS
              </h1>
              <p class="text-sm text-white/80 mt-1 flex items-center gap-1">
                <i class="material-icons text-xs">store</i>
                Bán hàng tại quầy - Thanh toán nhanh chóng
              </p>
            </div>
          </div>
          <div class="flex items-center gap-3 flex-wrap">
            <button 
              @click="showBarcode = !showBarcode" 
              class="group flex items-center gap-2 px-5 py-2.5 bg-white/10 backdrop-blur-sm text-white rounded-xl hover:bg-white/20 transition-all duration-300 text-sm font-medium shadow-lg hover:shadow-xl hover:scale-105"
            >
              <i class="material-icons text-base group-hover:rotate-12 transition-transform">qr_code_scanner</i>
              Quét mã
            </button>
            <button 
              @click="showShortcuts = true" 
              class="group flex items-center gap-2 px-5 py-2.5 bg-white/10 backdrop-blur-sm text-white rounded-xl hover:bg-white/20 transition-all duration-300 text-sm font-medium shadow-lg hover:shadow-xl hover:scale-105"
            >
              <i class="material-icons text-base">keyboard</i>
              Phím tắt
            </button>
            <button 
              @click="showHistory = true" 
              class="group flex items-center gap-2 px-5 py-2.5 bg-white/10 backdrop-blur-sm text-white rounded-xl hover:bg-white/20 transition-all duration-300 text-sm font-medium shadow-lg hover:shadow-xl hover:scale-105"
            >
              <i class="material-icons text-base">history</i>
              Lịch sử
            </button>
            <button 
              @click="resetCart" 
              class="group flex items-center gap-2 px-5 py-2.5 bg-white/10 backdrop-blur-sm text-white rounded-xl hover:bg-white/20 transition-all duration-300 text-sm font-medium shadow-lg hover:shadow-xl hover:scale-105"
            >
              <i class="material-icons text-base group-hover:rotate-180 transition-transform duration-500">refresh</i>
              Làm mới
            </button>
          </div>
        </div>
      </div>

      <!-- Barcode Scanner with Premium Design -->
      <transition
        enter-active-class="transition-all duration-500 ease-out"
        leave-active-class="transition-all duration-300 ease-in"
        enter-from-class="opacity-0 -translate-y-8 scale-95"
        enter-to-class="opacity-100 translate-y-0 scale-100"
        leave-from-class="opacity-100 translate-y-0 scale-100"
        leave-to-class="opacity-0 -translate-y-8 scale-95"
      >
        <div v-if="showBarcode" class="p-5 bg-white dark:bg-gray-800 rounded-2xl shadow-xl border border-gray-200 dark:border-gray-700 backdrop-blur-sm">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg">
              <i class="material-icons text-white text-xl">qr_code_scanner</i>
            </div>
            <input
              ref="barcodeInput"
              v-model="barcodeValue"
              @keyup.enter="handleBarcodeSearch"
              class="flex-1 px-4 py-3 bg-gray-50 dark:bg-gray-700/50 border-2 border-gray-200 dark:border-gray-600 rounded-xl text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
              placeholder="Quét mã vạch hoặc nhập SKU..."
            />
            <button 
              @click="showBarcode = false" 
              class="w-12 h-12 flex items-center justify-center bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 rounded-xl hover:bg-red-100 dark:hover:bg-red-900/20 hover:text-red-600 dark:hover:text-red-400 transition-all duration-300 hover:scale-110"
            >
              <i class="material-icons text-xl">close</i>
            </button>
          </div>
        </div>
      </transition>

      <!-- POS Grid with Enhanced Layout -->
      <div class="grid grid-cols-1 lg:grid-cols-[1fr_380px] xl:grid-cols-[1fr_420px] gap-4 lg:gap-6">
        <!-- Product Section - Premium Design -->
        <div class="p-4 md:p-6 bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm rounded-xl lg:rounded-2xl shadow-xl border border-gray-200/50 dark:border-gray-700/50">
          <!-- Enhanced Search Section -->
          <div class="space-y-3 mb-4 md:mb-6">
            <div class="relative group">
              <div class="absolute inset-0 bg-gradient-to-r from-purple-500 to-indigo-500 rounded-xl opacity-0 group-hover:opacity-10 transition-opacity duration-300 blur-xl"></div>
              <i class="material-icons absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 group-focus-within:text-purple-500 transition-colors">search</i>
              <input
                v-model="searchQuery"
                @input="searchProducts"
                class="w-full pl-11 pr-4 py-2.5 md:py-3 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-lg md:rounded-xl text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                placeholder="Tìm kiếm sản phẩm..."
              />
            </div>
            <div class="grid grid-cols-2 gap-2 md:gap-3">
              <div class="relative">
                <i class="material-icons absolute left-2.5 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-sm">category</i>
                <select 
                  v-model="filterBrand" 
                  @change="filterProducts" 
                  class="w-full pl-9 pr-3 py-2 md:py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-lg md:rounded-xl text-xs md:text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all appearance-none cursor-pointer"
                >
                  <option value="">Tất cả thương hiệu</option>
                  <option v-for="brand in brands" :key="brand.id" :value="brand.id">
                    {{ brand.name }}
                  </option>
                </select>
              </div>
              <div class="relative">
                <i class="material-icons absolute left-2.5 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-sm">label</i>
                <select 
                  v-model="filterCategory" 
                  @change="filterProducts" 
                  class="w-full pl-9 pr-3 py-2 md:py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-lg md:rounded-xl text-xs md:text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all appearance-none cursor-pointer"
                >
                  <option value="">Tất cả danh mục</option>
                  <option v-for="category in categories" :key="category.id" :value="category.id">
                    {{ category.name }}
                  </option>
                </select>
              </div>
            </div>
          </div>

          <!-- Products Grid with Premium Cards -->
          <div v-if="loading" class="flex flex-col items-center justify-center p-12">
            <div class="relative">
              <div class="w-12 h-12 border-4 border-purple-200 dark:border-purple-800 rounded-full"></div>
              <div class="w-12 h-12 border-4 border-transparent border-t-purple-600 rounded-full animate-spin absolute top-0 left-0"></div>
            </div>
            <p class="text-xs text-gray-600 dark:text-gray-400 mt-3 font-medium">Đang tải sản phẩm...</p>
          </div>
          <div v-else-if="products.length === 0" class="flex flex-col items-center justify-center p-12 text-center">
            <div class="w-20 h-20 bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-800 rounded-full flex items-center justify-center mb-4">
              <i class="material-icons text-gray-400 dark:text-gray-500 text-4xl">inventory_2</i>
            </div>
            <p class="text-sm font-medium text-gray-600 dark:text-gray-400">Không có sản phẩm</p>
            <p class="text-xs text-gray-500 dark:text-gray-500 mt-1">{{ searchQuery ? 'Thử tìm kiếm với từ khóa khác' : 'Vui lòng thử lại sau' }}</p>
          </div>
          <div v-else class="space-y-4">
            <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
              <div
                v-for="product in displayedProducts"
                :key="product.id"
                @click="addToCart(product)"
                class="group relative p-3 bg-gradient-to-br from-white to-gray-50 dark:from-gray-800 dark:to-gray-900 rounded-lg cursor-pointer transition-all duration-200 border border-gray-200 dark:border-gray-700 hover:border-purple-400 dark:hover:border-purple-600 hover:shadow-lg hover:scale-[1.02] active:scale-95"
              >
              <!-- Stock Badge -->
              <div class="absolute top-2 right-2 z-10">
                <span 
                  class="px-2 py-1 rounded-full text-xs font-semibold shadow-lg backdrop-blur-sm"
                  :class="{
                    'bg-red-500/90 text-white': getProductStock(product) === 0,
                    'bg-yellow-500/90 text-white': getProductStock(product) > 0 && getProductStock(product) < 10,
                    'bg-green-500/90 text-white': getProductStock(product) >= 10
                  }"
                >
                  {{ getProductStock(product) }}
                </span>
              </div>
              
              <!-- Product Image -->
              <div class="aspect-square bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-800 rounded-lg mb-2 flex items-center justify-center overflow-hidden relative">
                <img 
                  v-if="product.imageUrl" 
                  :src="product.imageUrl" 
                  :alt="product.name" 
                  class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" 
                  loading="lazy"
                />
                <i v-else class="material-icons text-gray-400 dark:text-gray-500 text-4xl">image</i>
                <!-- Quick Add Badge -->
                <div class="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors duration-200 flex items-center justify-center opacity-0 group-hover:opacity-100">
                  <div class="bg-white rounded-full p-2 shadow-lg">
                    <i class="material-icons text-purple-600 text-lg">add</i>
                  </div>
                </div>
              </div>
              
              <!-- Product Info -->
              <div class="space-y-1">
                <h3 class="text-xs font-semibold text-gray-900 dark:text-gray-100 line-clamp-2 group-hover:text-purple-600 dark:group-hover:text-purple-400 transition-colors leading-tight">
                  {{ product.name }}
                </h3>
                <p class="text-xs text-gray-500 dark:text-gray-400 truncate">{{ product.brandName || 'N/A' }}</p>
                <p class="text-sm font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent">
                  {{ formatCurrency(getProductPrice(product)) }}
                </p>
              </div>
              </div>
            </div>
            
            <!-- Load More Button -->
            <div v-if="hasMoreProducts" class="flex justify-center pt-2">
              <button
                @click="showAllProducts"
                class="flex items-center gap-2 px-6 py-2.5 bg-gradient-to-r from-purple-500 to-indigo-600 text-white rounded-xl hover:from-purple-600 hover:to-indigo-700 transition-all duration-300 font-medium text-sm shadow-lg hover:shadow-xl hover:scale-105"
              >
                <i class="material-icons text-lg">expand_more</i>
                Xem thêm sản phẩm ({{ products.length - displayLimit }})
              </button>
            </div>
            
            <!-- Info khi đã hiển thị tất cả -->
            <div v-if="!hasMoreProducts && products.length > 0" class="text-center pt-2">
              <p class="text-xs text-gray-500 dark:text-gray-400">
                Đang hiển thị {{ products.length }} sản phẩm
                <span v-if="searchQuery.trim() || filterBrand || filterCategory">(kết quả tìm kiếm)</span>
              </p>
            </div>
          </div>
        </div>

        <!-- Cart Section - Premium Design -->
        <div class="p-4 bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm rounded-xl lg:rounded-2xl shadow-xl border border-gray-200/50 dark:border-gray-700/50 flex flex-col">
          <!-- Cart Header -->
          <div class="flex items-center justify-between mb-3 pb-3 border-b border-gray-200 dark:border-gray-700 flex-shrink-0">
            <div class="flex items-center gap-2.5">
              <div class="w-9 h-9 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-lg flex items-center justify-center shadow-lg">
                <i class="material-icons text-white text-lg">shopping_cart</i>
              </div>
              <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100">Giỏ hàng</h2>
              <span v-if="cartItems.length > 0" class="px-2 py-0.5 bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300 rounded-full text-xs font-semibold">
                {{ cartItems.length }}
              </span>
            </div>
            <button 
              @click="resetCart" 
              class="w-8 h-8 flex items-center justify-center bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/20 hover:text-red-600 dark:hover:text-red-400 transition-all duration-300 hover:scale-110"
            >
              <i class="material-icons text-lg">clear</i>
            </button>
          </div>

          <!-- Cart Items with Premium Design -->
          <div class="mb-3 space-y-2" :class="cartItems.length === 0 ? 'py-2' : 'max-h-[250px] overflow-y-auto custom-scrollbar pr-2'">
            <div v-if="cartItems.length === 0" class="flex flex-col items-center justify-center py-3 text-center w-full">
              <div class="w-12 h-12 bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-800 rounded-full flex items-center justify-center mb-2">
                <i class="material-icons text-gray-400 dark:text-gray-500 text-2xl">shopping_cart</i>
              </div>
              <p class="text-xs font-medium text-gray-500 dark:text-gray-400">Giỏ hàng trống</p>
              <p class="text-xs text-gray-400 dark:text-gray-500 mt-0.5">Thêm sản phẩm để bắt đầu</p>
            </div>
            <transition-group name="cart-item" tag="div" class="space-y-3">
              <div 
                v-for="(item, index) in cartItems" 
                :key="`${item.id}-${index}`"
                class="group p-4 bg-gradient-to-br from-gray-50 to-white dark:from-gray-900/50 dark:to-gray-800 rounded-xl border-2 border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-700 hover:shadow-lg transition-all duration-300"
              >
                <div class="flex items-start gap-4">
                  <div class="w-16 h-16 bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-800 rounded-lg flex items-center justify-center flex-shrink-0 shadow-md">
                    <i class="material-icons text-gray-400 dark:text-gray-500 text-3xl">image</i>
                  </div>
                  <div class="flex-1 min-w-0">
                    <h4 class="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate">{{ item.name }}</h4>
                    <p class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{{ item.sku }}</p>
                    <p class="text-base font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent mt-2">
                      {{ formatCurrency(item.unitPrice * item.quantity) }}
                    </p>
                  </div>
                  <div class="flex flex-col items-end gap-3">
                    <div class="flex items-center gap-2 bg-gray-100 dark:bg-gray-700 rounded-lg p-1">
                      <button 
                        @click="updateQuantity(index, item.quantity - 1)" 
                        class="w-8 h-8 flex items-center justify-center bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/20 hover:text-red-600 dark:hover:text-red-400 transition-all duration-200 font-semibold hover:scale-110"
                      >
                        -
                      </button>
                      <input
                        v-model.number="item.quantity"
                        @change="updateQuantity(index, item.quantity)"
                        class="w-14 h-8 text-center bg-transparent border-0 text-sm font-semibold text-gray-900 dark:text-gray-100 focus:outline-none"
                        type="number"
                        min="1"
                      />
                      <button 
                        @click="updateQuantity(index, item.quantity + 1)" 
                        class="w-8 h-8 flex items-center justify-center bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-green-100 dark:hover:bg-green-900/20 hover:text-green-600 dark:hover:text-green-400 transition-all duration-200 font-semibold hover:scale-110"
                      >
                        +
                      </button>
                    </div>
                    <button 
                      @click="removeFromCart(index)" 
                      class="w-8 h-8 flex items-center justify-center bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/30 transition-all duration-200 hover:scale-110"
                    >
                      <i class="material-icons text-lg">delete</i>
                    </button>
                  </div>
                </div>
              </div>
            </transition-group>
          </div>

          <!-- Cart Summary with Premium Design -->
          <div class="space-y-1.5 py-2 border-t border-gray-200 dark:border-gray-700 flex-shrink-0">
            <div class="flex items-center justify-between text-xs">
              <span class="text-gray-600 dark:text-gray-400 font-medium">Tạm tính:</span>
              <span class="text-gray-900 dark:text-gray-100 font-semibold">{{ formatCurrency(subtotal) }}</span>
            </div>
            <div v-if="discountAmount > 0" class="flex items-center justify-between text-xs">
              <span class="text-gray-600 dark:text-gray-400 font-medium">Giảm giá:</span>
              <span class="text-red-600 dark:text-red-400 font-semibold">-{{ formatCurrency(discountAmount) }}</span>
            </div>
            <div class="flex items-center justify-between pt-1.5 border-t border-gray-200 dark:border-gray-700">
              <span class="text-sm font-bold text-gray-900 dark:text-gray-100">Tổng cộng:</span>
              <span class="text-lg font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent">
                {{ formatCurrency(totalAmount) }}
              </span>
            </div>
          </div>

          <!-- Discount Section with Premium Design -->
          <div class="py-2 border-t border-gray-200 dark:border-gray-700 flex-shrink-0">
            <label class="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1.5">Mã giảm giá</label>
            <div class="flex items-center gap-2">
              <div class="relative flex-1">
                <i class="material-icons absolute left-2.5 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-sm">local_offer</i>
                <input
                  v-model="discountCode"
                  placeholder="Nhập mã giảm giá..."
                  class="w-full pl-9 pr-3 py-1.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-lg text-xs text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
                />
              </div>
              <button 
                @click="applyDiscount" 
                class="w-9 h-9 flex items-center justify-center bg-gradient-to-br from-purple-500 to-indigo-600 text-white rounded-lg hover:from-purple-600 hover:to-indigo-700 transition-all duration-300 shadow-lg hover:shadow-xl hover:scale-110"
              >
                <i class="material-icons text-base">check</i>
              </button>
            </div>
          </div>

          <!-- Payment Section with Premium Design -->
          <div class="py-2 border-t border-gray-200 dark:border-gray-700 flex-shrink-0">
            <label class="block text-xs font-semibold text-gray-700 dark:text-gray-300 mb-1.5">Phương thức thanh toán</label>
            <div class="grid grid-cols-2 gap-2">
              <button
                @click="paymentMethod = 'cash'"
                class="group flex flex-col items-center gap-1 p-2.5 rounded-lg border-2 transition-all duration-300 hover:scale-105"
                :class="paymentMethod === 'cash' 
                  ? 'border-purple-500 bg-gradient-to-br from-purple-50 to-purple-100 dark:from-purple-900/30 dark:to-purple-800/30 shadow-md' 
                  : 'border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-700 hover:border-purple-300 dark:hover:border-purple-700'"
              >
                <div class="w-9 h-9 rounded-full flex items-center justify-center transition-all duration-300"
                  :class="paymentMethod === 'cash' 
                    ? 'bg-gradient-to-br from-purple-500 to-indigo-600 text-white shadow-md' 
                    : 'bg-gray-100 dark:bg-gray-600 text-gray-600 dark:text-gray-300 group-hover:bg-purple-100 dark:group-hover:bg-purple-900/30'"
                >
                  <i class="material-icons text-lg">money</i>
                </div>
                <span class="text-xs font-semibold" :class="paymentMethod === 'cash' ? 'text-purple-700 dark:text-purple-300' : 'text-gray-700 dark:text-gray-300'">Tiền mặt</span>
              </button>
              <button
                @click="paymentMethod = 'card'"
                class="group flex flex-col items-center gap-1 p-2.5 rounded-lg border-2 transition-all duration-300 hover:scale-105"
                :class="paymentMethod === 'card' 
                  ? 'border-purple-500 bg-gradient-to-br from-purple-50 to-purple-100 dark:from-purple-900/30 dark:to-purple-800/30 shadow-md' 
                  : 'border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-700 hover:border-purple-300 dark:hover:border-purple-700'"
              >
                <div class="w-9 h-9 rounded-full flex items-center justify-center transition-all duration-300"
                  :class="paymentMethod === 'card' 
                    ? 'bg-gradient-to-br from-purple-500 to-indigo-600 text-white shadow-md' 
                    : 'bg-gray-100 dark:bg-gray-600 text-gray-600 dark:text-gray-300 group-hover:bg-purple-100 dark:group-hover:bg-purple-900/30'"
                >
                  <i class="material-icons text-lg">credit_card</i>
                </div>
                <span class="text-xs font-semibold" :class="paymentMethod === 'card' ? 'text-purple-700 dark:text-purple-300' : 'text-gray-700 dark:text-gray-300'">Thẻ</span>
              </button>
            </div>
          </div>

          <!-- Checkout Button with Premium Design -->
          <button
            @click="processOrder"
            :disabled="cartItems.length === 0 || processing"
            class="w-full flex items-center justify-center gap-2 px-4 py-2.5 bg-gradient-to-r from-purple-600 via-purple-500 to-indigo-600 text-white rounded-xl hover:from-purple-700 hover:via-purple-600 hover:to-indigo-700 transition-all duration-300 font-bold text-sm shadow-xl hover:shadow-purple-500/50 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:shadow-xl transform hover:scale-[1.02] active:scale-[0.98] flex-shrink-0 mt-2"
          >
            <i class="material-icons text-lg" :class="{ 'animate-spin': processing }">{{ processing ? 'autorenew' : 'shopping_cart_checkout' }}</i>
            {{ processing ? 'Đang xử lý...' : 'Thanh toán ngay' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Premium Receipt Modal -->
    <transition name="modal">
      <div v-if="showReceipt" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" @click="showReceipt = false">
        <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl max-w-md w-full border border-gray-200 dark:border-gray-700 transform transition-all" @click.stop>
          <div class="p-6 border-b-2 border-gray-200 dark:border-gray-700 bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20 text-center">
            <div class="w-16 h-16 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-full flex items-center justify-center mx-auto mb-3 shadow-lg">
              <i class="material-icons text-white text-3xl">receipt</i>
            </div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100">Hóa đơn</h2>
            <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">{{ currentReceipt?.orderNumber }}</p>
          </div>
          <div class="p-6 space-y-3 max-h-[400px] overflow-y-auto custom-scrollbar">
            <div v-for="item in currentReceipt?.items" :key="item.id" class="flex items-center justify-between py-3 border-b border-gray-200 dark:border-gray-700">
              <div class="flex-1">
                <span class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ item.name }}</span>
                <p class="text-xs text-gray-500 dark:text-gray-400">x{{ item.quantity }}</p>
              </div>
              <span class="text-sm font-bold text-gray-900 dark:text-gray-100">{{ formatCurrency(item.unitPrice * item.quantity) }}</span>
            </div>
          </div>
          <div class="p-6 border-t-2 border-gray-200 dark:border-gray-700 bg-gradient-to-r from-gray-50 to-gray-100 dark:from-gray-900/50 dark:to-gray-800/50">
            <div class="flex items-center justify-between mb-6">
              <span class="text-lg font-bold text-gray-900 dark:text-gray-100">Tổng cộng:</span>
              <span class="text-2xl font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent">
                {{ formatCurrency(currentReceipt?.totalAmount) }}
              </span>
            </div>
            <div class="flex items-center gap-3">
              <button 
                @click="printReceipt" 
                class="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-purple-500 to-indigo-600 text-white rounded-xl hover:from-purple-600 hover:to-indigo-700 transition-all duration-300 text-sm font-semibold shadow-lg hover:shadow-xl hover:scale-105"
              >
                <i class="material-icons text-xl">print</i>
                In hóa đơn
              </button>
              <button 
                @click="showReceipt = false" 
                class="flex items-center gap-2 px-4 py-3 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-xl hover:bg-gray-200 dark:hover:bg-gray-600 transition-all duration-300 text-sm font-semibold hover:scale-105"
              >
                <i class="material-icons text-xl">close</i>
                Đóng
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Premium Shortcuts Modal -->
    <transition name="modal">
      <div v-if="showShortcuts" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" @click="showShortcuts = false">
        <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl max-w-2xl w-full border border-gray-200 dark:border-gray-700 transform transition-all" @click.stop>
          <div class="flex items-center justify-between p-6 border-b-2 border-gray-200 dark:border-gray-700 bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <div class="w-10 h-10 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-lg flex items-center justify-center">
                <i class="material-icons text-white">keyboard</i>
              </div>
              Phím tắt
            </h2>
            <button 
              @click="showShortcuts = false" 
              class="w-10 h-10 flex items-center justify-center bg-gray-100 dark:bg-gray-700 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 rounded-xl hover:bg-gray-200 dark:hover:bg-gray-600 transition-all duration-300 hover:scale-110"
            >
              <i class="material-icons text-xl">close</i>
            </button>
          </div>
          <div class="p-6 space-y-3 max-h-[60vh] overflow-y-auto custom-scrollbar">
            <div 
              v-for="(shortcut, index) in shortcuts" 
              :key="index"
              class="flex items-center justify-between p-4 bg-gradient-to-r from-gray-50 to-white dark:from-gray-900/50 dark:to-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-700 hover:shadow-lg transition-all duration-300"
            >
              <div class="flex items-center gap-3">
                <div class="flex items-center gap-1.5">
                  <kbd 
                    v-for="(key, keyIndex) in shortcut.keys" 
                    :key="keyIndex"
                    class="px-3 py-1.5 bg-white dark:bg-gray-700 border-2 border-gray-300 dark:border-gray-600 rounded-lg text-xs font-mono font-semibold text-gray-900 dark:text-gray-100 shadow-sm"
                  >
                    {{ key }}
                  </kbd>
                </div>
                <span class="text-gray-500 dark:text-gray-400 mx-2">→</span>
                <div class="text-sm font-medium text-gray-700 dark:text-gray-300">{{ shortcut.description }}</div>
              </div>
            </div>
          </div>
          <div class="flex items-center justify-end p-6 border-t-2 border-gray-200 dark:border-gray-700">
            <button 
              @click="showShortcuts = false" 
              class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-500 to-indigo-600 text-white rounded-xl hover:from-purple-600 hover:to-indigo-700 transition-all duration-300 text-sm font-semibold shadow-lg hover:shadow-xl hover:scale-105"
            >
              <i class="material-icons text-xl">check</i>
              Đã hiểu
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Premium History Modal -->
    <transition name="modal">
      <div v-if="showHistory" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" @click="showHistory = false">
        <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-hidden border border-gray-200 dark:border-gray-700 transform transition-all" @click.stop>
          <div class="flex items-center justify-between p-6 border-b-2 border-gray-200 dark:border-gray-700 bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20 sticky top-0 z-10">
            <h2 class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <div class="w-10 h-10 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-lg flex items-center justify-center">
                <i class="material-icons text-white">history</i>
              </div>
              Lịch sử bán hàng
            </h2>
            <button 
              @click="showHistory = false" 
              class="w-10 h-10 flex items-center justify-center bg-gray-100 dark:bg-gray-700 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 rounded-xl hover:bg-gray-200 dark:hover:bg-gray-600 transition-all duration-300 hover:scale-110"
            >
              <i class="material-icons text-xl">close</i>
            </button>
          </div>
          <div class="p-6 overflow-y-auto max-h-[calc(90vh-100px)] custom-scrollbar">
            <div v-if="loadingHistory" class="flex flex-col items-center justify-center p-16">
              <div class="relative">
                <div class="w-16 h-16 border-4 border-purple-200 dark:border-purple-800 rounded-full"></div>
                <div class="w-16 h-16 border-4 border-transparent border-t-purple-600 rounded-full animate-spin absolute top-0 left-0"></div>
              </div>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-4 font-medium">Đang tải lịch sử...</p>
            </div>
            <div v-else-if="salesHistory.length === 0" class="flex flex-col items-center justify-center p-16">
              <div class="w-24 h-24 bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-800 rounded-full flex items-center justify-center mb-4">
                <i class="material-icons text-gray-400 dark:text-gray-500 text-5xl">history</i>
              </div>
              <p class="text-base font-medium text-gray-500 dark:text-gray-400">Chưa có lịch sử bán hàng</p>
            </div>
            <div v-else class="space-y-4">
              <div 
                v-for="order in salesHistory" 
                :key="order.id" 
                @click="viewOrderDetails(order)"
                class="group p-5 bg-gradient-to-br from-gray-50 to-white dark:from-gray-900/50 dark:to-gray-800 rounded-xl border-2 border-gray-200 dark:border-gray-700 hover:border-purple-400 dark:hover:border-purple-600 hover:shadow-xl transition-all duration-300 cursor-pointer"
              >
                <div class="flex items-start gap-4">
                  <div class="w-14 h-14 bg-gradient-to-br from-purple-100 to-indigo-100 dark:from-purple-900/30 dark:to-indigo-900/30 rounded-xl flex items-center justify-center flex-shrink-0 shadow-lg">
                    <i class="material-icons text-purple-600 dark:text-purple-400 text-2xl">receipt</i>
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between mb-2">
                      <h4 class="text-base font-bold text-gray-900 dark:text-gray-100">{{ order.orderNumber || `Đơn hàng #${order.id}` }}</h4>
                      <span class="text-xs text-gray-500 dark:text-gray-400">{{ formatDate(order.createdAt) }}</span>
                    </div>
                    <div class="flex items-center gap-4 mb-3">
                      <span class="text-lg font-bold bg-gradient-to-r from-purple-600 to-indigo-600 bg-clip-text text-transparent">
                        {{ formatCurrency(order.totalAmount) }}
                      </span>
                      <span class="px-3 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-400 rounded-full text-xs font-semibold">
                        {{ getPaymentMethodLabel(order.paymentMethod) }}
                      </span>
                    </div>
                    <div v-if="order.items" class="flex flex-wrap gap-2">
                      <span 
                        v-for="(item, idx) in order.items.slice(0, 3)" 
                        :key="idx" 
                        class="px-2.5 py-1 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg text-xs font-medium"
                      >
                        {{ item.name }} x{{ item.quantity }}
                      </span>
                      <span 
                        v-if="order.items.length > 3" 
                        class="px-2.5 py-1 bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300 rounded-lg text-xs font-semibold"
                      >
                        +{{ order.items.length - 3 }} sản phẩm
                      </span>
                    </div>
                  </div>
                  <button 
                    @click.stop="viewOrderDetails(order)" 
                    class="w-10 h-10 flex items-center justify-center bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400 rounded-lg hover:bg-purple-100 dark:hover:bg-purple-900/30 hover:text-purple-600 dark:hover:text-purple-400 transition-all duration-300 hover:scale-110"
                    title="Xem chi tiết"
                  >
                    <i class="material-icons text-xl">visibility</i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
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
const barcodeInput = ref(null)
const searchTimeout = ref(null)
const displayLimit = ref(12) // Số lượng sản phẩm hiển thị ban đầu
const showingAll = ref(false) // Trạng thái có đang hiển thị tất cả không

// Shortcuts data
const shortcuts = [
  { keys: ['Ctrl', 'K'], description: 'Mở/đóng thanh tìm kiếm' },
  { keys: ['Ctrl', 'B'], description: 'Mở/đóng quét mã vạch' },
  { keys: ['Enter'], description: 'Thêm sản phẩm vào giỏ hàng' },
  { keys: ['Ctrl', 'Enter'], description: 'Thanh toán đơn hàng' },
  { keys: ['Esc'], description: 'Đóng modal/hủy thao tác' },
  { keys: ['Ctrl', 'R'], description: 'Làm mới giỏ hàng' },
  { keys: ['+', '/', '-'], description: 'Tăng/giảm số lượng sản phẩm' }
]

// Computed
const subtotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.unitPrice * item.quantity), 0)
})

const totalAmount = computed(() => {
  return subtotal.value - discountAmount.value
})

// Computed để lấy sản phẩm hiển thị (giới hạn số lượng)
const displayedProducts = computed(() => {
  if (showingAll.value || searchQuery.value.trim() || filterBrand.value || filterCategory.value) {
    // Khi search/filter hoặc đã click "Xem thêm", hiển thị tất cả
    return products.value
  }
  // Chỉ hiển thị một số sản phẩm gợi ý
  return products.value.slice(0, displayLimit.value)
})

const hasMoreProducts = computed(() => {
  return !showingAll.value && !searchQuery.value.trim() && !filterBrand.value && !filterCategory.value && products.value.length > displayLimit.value
})

// Methods
const loadData = async () => {
  try {
    loading.value = true
    
    // Load products, brands, and categories in parallel - chỉ load 20 sản phẩm đầu tiên
    const [productsResult, brandsResult, categoriesResult] = await Promise.all([
      adminStore.fetchProducts(0, 20, { isActive: true }), // Giảm từ 100 xuống 20
      adminStore.fetchBrands(),
      adminStore.fetchCategories()
    ])
    
    let productsList = productsResult.content || productsResult || []
    
    // Enrich products với variants nếu cần (fetch detail cho những product không có price)
    const productsNeedingDetails = productsList.filter(p => !p.price && !p.priceBase && !p.priceSale && (!p.variants || p.variants.length === 0))
    
    if (productsNeedingDetails.length > 0 && productsNeedingDetails.length <= 20) {
      // Chỉ fetch detail nếu số lượng không quá nhiều (tránh quá tải)
      try {
        const detailPromises = productsNeedingDetails.map(p => 
          adminStore.getProductById(p.id).catch(() => null)
        )
        const details = await Promise.all(detailPromises)
        
        // Merge details vào products
        productsList = productsList.map(product => {
          const detail = details.find(d => d && d.id === product.id)
          if (detail) {
            return {
              ...product,
              variants: detail.variants || product.variants,
              priceBase: detail.priceBase || product.priceBase,
              priceSale: detail.priceSale || product.priceSale,
              imageUrl: product.imageUrl || detail.variants?.[0]?.imageUrl || detail.imageUrl
            }
          }
          return product
        })
      } catch (error) {
        console.warn('Could not fetch product details:', error)
      }
    }
    
    products.value = productsList
    brands.value = brandsResult.content || brandsResult || []
    categories.value = categoriesResult.content || categoriesResult || []
    
    // Reset showingAll khi load lại data
    showingAll.value = false
    
  } catch (error) {
    console.error('Error loading data:', error)
    ElMessage.error('Không thể tải dữ liệu')
  } finally {
    loading.value = false
  }
}

// Debounced search function
const searchProducts = async () => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  searchTimeout.value = setTimeout(async () => {
    try {
      loading.value = true
      
      const filters = {
        isActive: true,
        search: searchQuery.value
      }
      
      const result = await adminStore.fetchProducts(0, 50, filters) // Giới hạn 50 khi search
      products.value = result.content || result || []
      showingAll.value = true // Khi search, hiển thị tất cả kết quả
      
      if (products.value.length === 0 && searchQuery.value.trim()) {
        ElMessage.info('Không tìm thấy sản phẩm nào')
      }
    } catch (error) {
      console.error('Error searching products:', error)
      ElMessage.error('Không thể tìm kiếm sản phẩm')
    } finally {
      loading.value = false
    }
  }, 300) // 300ms debounce
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
    
      const result = await adminStore.fetchProducts(0, 50, filters) // Giới hạn 50 khi filter
      products.value = result.content || result || []
      showingAll.value = true // Khi filter, hiển thị tất cả kết quả
    
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

const showAllProducts = () => {
  showingAll.value = true
  // Load thêm sản phẩm nếu cần
  if (products.value.length <= displayLimit.value) {
    loadMoreProducts()
  }
}

const loadMoreProducts = async () => {
  try {
    loading.value = true
    const result = await adminStore.fetchProducts(0, 50, { isActive: true })
    const newProducts = result.content || result || []
    // Merge với products hiện tại, loại bỏ duplicate
    const existingIds = new Set(products.value.map(p => p.id))
    const uniqueNewProducts = newProducts.filter(p => !existingIds.has(p.id))
    products.value = [...products.value, ...uniqueNewProducts]
  } catch (error) {
    console.error('Error loading more products:', error)
  } finally {
    loading.value = false
  }
}

const addToCart = (product) => {
  const productPrice = getProductPrice(product)
  if (productPrice === 0) {
    ElMessage.warning('Sản phẩm này chưa có giá. Vui lòng kiểm tra lại.')
    return
  }
  
  // Lấy variant đầu tiên nếu có
  const firstVariant = product.variants && product.variants.length > 0 ? product.variants[0] : null
  
  const existingItem = cartItems.value.find(item => 
    item.id === product.id && 
    (item.variantId === (firstVariant?.id || null))
  )
  
  if (existingItem) {
    existingItem.quantity += 1
    ElMessage.success(`Đã thêm ${product.name} vào giỏ hàng`)
  } else {
    cartItems.value.push({
      id: product.id,
      name: product.name,
      sku: firstVariant?.sku || product.sku || product.code || `SP-${product.id}`,
      unitPrice: productPrice,
      quantity: 1,
      variantId: firstVariant?.id || null
    })
    ElMessage.success(`Đã thêm ${product.name} vào giỏ hàng`)
  }
}

const removeFromCart = (index) => {
  cartItems.value.splice(index, 1)
  ElMessage.info('Đã xóa sản phẩm khỏi giỏ hàng')
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
  } else {
    ElMessage.warning('Vui lòng nhập mã giảm giá')
  }
}

const printReceipt = () => {
  if (currentReceipt.value) {
    window.print()
  }
}

const formatCurrency = (value) => {
  if (value === null || value === undefined || isNaN(value) || value === '') {
    return '0 ₫'
  }
  const numValue = Number(value)
  if (isNaN(numValue) || numValue < 0) {
    return '0 ₫'
  }
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(numValue)
}

// Helper function để lấy giá từ product
const getProductPrice = (product) => {
  // Nếu product có price trực tiếp
  if (product.price !== null && product.price !== undefined && !isNaN(product.price)) {
    return Number(product.price)
  }
  
  // Nếu có variants, lấy giá từ variant đầu tiên
  if (product.variants && product.variants.length > 0) {
    const firstVariant = product.variants[0]
    // Ưu tiên priceSale, nếu không có thì dùng priceBase
    if (firstVariant.priceSale !== null && firstVariant.priceSale !== undefined && !isNaN(firstVariant.priceSale)) {
      return Number(firstVariant.priceSale)
    }
    if (firstVariant.priceBase !== null && firstVariant.priceBase !== undefined && !isNaN(firstVariant.priceBase)) {
      return Number(firstVariant.priceBase)
    }
  }
  
  // Nếu có priceBase hoặc priceSale trực tiếp
  if (product.priceSale !== null && product.priceSale !== undefined && !isNaN(product.priceSale)) {
    return Number(product.priceSale)
  }
  if (product.priceBase !== null && product.priceBase !== undefined && !isNaN(product.priceBase)) {
    return Number(product.priceBase)
  }
  
  return 0
}

// Helper function để lấy stock quantity
const getProductStock = (product) => {
  if (product.stockQuantity !== null && product.stockQuantity !== undefined && !isNaN(product.stockQuantity)) {
    return Number(product.stockQuantity)
  }
  
  // Nếu có variants, tính tổng stock
  if (product.variants && product.variants.length > 0) {
    const totalStock = product.variants.reduce((sum, variant) => {
      const stock = variant.stockQuantity || 0
      return sum + (isNaN(stock) ? 0 : Number(stock))
    }, 0)
    return totalStock
  }
  
  if (product.totalStock !== null && product.totalStock !== undefined && !isNaN(product.totalStock)) {
    return Number(product.totalStock)
  }
  
  return 0
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
    const searchInput = document.querySelector('input[placeholder*="Tìm kiếm sản phẩm"]')
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
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
})
</script>

<style scoped>
/* Custom Scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 8px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, #9333ea, #6366f1);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, #7e22ce, #4f46e5);
}

/* Modal Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

/* Cart Item Transitions */
.cart-item-enter-active {
  transition: all 0.3s ease;
}

.cart-item-leave-active {
  transition: all 0.3s ease;
}

.cart-item-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.cart-item-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.cart-item-move {
  transition: transform 0.3s ease;
}
</style>

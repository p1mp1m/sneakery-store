<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">inventory</i>
            Quản lý kho hàng
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Theo dõi tồn kho và lịch sử nhập/xuất hàng</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="exportInventory('csv')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_download</i>
            CSV
          </button>
          <button @click="exportInventory('json')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">code</i>
            JSON
          </button>
          <button @click="openStockAdjustmentModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">add</i>
            Điều chỉnh kho
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">inventory_2</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(totalProducts) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng sản phẩm</p>
          <div class="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
            <i class="material-icons text-sm">trending_up</i>
            <span>+{{ formatNumber(newProductsThisMonth) }} tháng này</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(inStockProducts) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Còn hàng</p>
          <div class="flex items-center gap-1 text-xs text-purple-600 dark:text-purple-400">
            <i class="material-icons text-sm">done</i>
            <span>{{ Math.round((inStockProducts / totalProducts) * 100) || 0 }}% tổng số</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">warning</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(lowStockProducts) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Sắp hết hàng</p>
          <div class="flex items-center gap-1 text-xs text-yellow-600 dark:text-yellow-400">
            <i class="material-icons text-sm">info</i>
            <span>Cần nhập hàng</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">remove_shopping_cart</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(outOfStockProducts) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Hết hàng</p>
          <div class="flex items-center gap-1 text-xs text-red-600 dark:text-red-400">
            <i class="material-icons text-sm">trending_down</i>
            <span>Cần nhập khẩn cấp</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">search</i>
            Tìm kiếm
          </label>
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
            <input 
              type="text" 
              v-model="searchKeyword"
              placeholder="Tìm theo tên sản phẩm, SKU..."
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
            <button v-if="searchKeyword" @click="clearSearch" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>
        
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">filter_list</i>
            Trạng thái kho
          </label>
          <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterStockStatus">
            <option value="all">Tất cả</option>
            <option value="in-stock">Còn hàng</option>
            <option value="low-stock">Sắp hết hàng</option>
            <option value="out-of-stock">Hết hàng</option>
          </select>
        </div>

        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">category</i>
            Thương hiệu
          </label>
          <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterBrand">
            <option value="all">Tất cả</option>
            <option value="nike">Nike</option>
            <option value="adidas">Adidas</option>
            <option value="converse">Converse</option>
            <option value="puma">Puma</option>
          </select>
        </div>

        <div class="flex items-end">
          <button class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium w-full" @click="resetFilters">
            <i class="material-icons text-base">refresh</i>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Inventory Table -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100">Danh sách tồn kho</h3>
        <span class="text-sm text-gray-600 dark:text-gray-400">{{ filteredProducts.length }} sản phẩm</span>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex flex-col items-center justify-center p-12">
        <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải dữ liệu...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredProducts.length === 0" class="flex flex-col items-center justify-center p-12">
        <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">inventory</i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Không có sản phẩm nào</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 text-center">Chưa có sản phẩm nào được tìm thấy</p>
      </div>

      <!-- Table -->
      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Sản phẩm</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">SKU</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Thương hiệu</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Tồn kho</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Giá nhập</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Giá bán</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Cập nhật cuối</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="product in paginatedProducts" :key="product.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td class="px-4 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 rounded-lg bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden">
                    <img v-if="product.image" :src="product.image" :alt="product.name" class="w-full h-full object-cover">
                    <i v-else class="material-icons text-gray-400 dark:text-gray-500">image</i>
                  </div>
                  <div>
                    <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ product.name }}</div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">{{ product.size }} - {{ product.color }}</div>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900 dark:text-gray-100 font-mono">{{ product.sku }}</div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span class="text-sm text-gray-900 dark:text-gray-100">{{ product.brandName }}</span>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm font-semibold text-gray-900 dark:text-gray-100">{{ formatNumber(product.stockQuantity) }}</div>
                <div v-if="product.lowStockThreshold" class="text-xs text-gray-500 dark:text-gray-400">
                  Tối thiểu: {{ formatNumber(product.lowStockThreshold) }}
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900 dark:text-gray-100">{{ formatCurrency(product.costPrice) }}</div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ formatCurrency(product.priceBase) }}</div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span 
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': product.stockQuantity > (product.lowStockThreshold || 0),
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': product.stockQuantity > 0 && product.stockQuantity <= (product.lowStockThreshold || 0),
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': product.stockQuantity === 0
                  }"
                >
                  {{ getStockStatusText(product.stockQuantity, product.lowStockThreshold) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-400">
                {{ formatDateTime(product.updatedAt) }}
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <button @click="viewInventoryHistory(product)" class="p-1.5 text-gray-600 dark:text-gray-400 hover:text-purple-600 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20 rounded-lg transition-colors" title="Lịch sử">
                    <i class="material-icons text-base">history</i>
                  </button>
                  <button @click="adjustStock(product)" class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors" title="Điều chỉnh">
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button @click="restockProduct(product)" class="p-1.5 text-green-600 dark:text-green-400 hover:bg-green-50 dark:hover:bg-green-900/20 rounded-lg transition-colors" title="Nhập hàng">
                    <i class="material-icons text-base">add_shopping_cart</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1 && !loading" class="flex items-center justify-between gap-4 px-4 py-3 mt-4 border-t border-gray-200 dark:border-gray-700">
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Trang {{ currentPage + 1 }} / {{ totalPages }}
        </div>
        <div class="flex items-center gap-2">
          <button 
            @click="goToPage(currentPage - 1)" 
            :disabled="currentPage === 0"
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="material-icons text-base">chevron_left</i>
          </button>
          <button 
            @click="goToPage(currentPage + 1)" 
            :disabled="currentPage === totalPages - 1"
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="material-icons text-base">chevron_right</i>
          </button>
        </div>
      </div>
    </div>

    <!-- Stock Adjustment Modal -->
    <div v-if="showAdjustmentModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeAdjustmentModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">edit</i>
            Điều chỉnh tồn kho
          </h3>
          <button @click="closeAdjustmentModal" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="selectedProduct" class="space-y-4">
            <div class="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4 class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">{{ selectedProduct.name }}</h4>
              <p class="text-xs text-gray-600 dark:text-gray-400">SKU: {{ selectedProduct.sku }}</p>
              <p class="text-sm text-gray-700 dark:text-gray-300 mt-2">
                Tồn kho hiện tại: <strong>{{ formatNumber(selectedProduct.stockQuantity) }}</strong>
              </p>
            </div>
            
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Loại điều chỉnh</label>
              <select v-model="adjustmentType" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
                <option value="add">Thêm vào kho</option>
                <option value="subtract">Trừ khỏi kho</option>
                <option value="set">Đặt số lượng</option>
              </select>
            </div>
            
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số lượng</label>
              <input 
                type="number" 
                v-model="adjustmentQuantity" 
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                :min="adjustmentType === 'subtract' ? 0 : 1"
                :max="adjustmentType === 'subtract' ? selectedProduct.stockQuantity : undefined"
              >
            </div>
            
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Lý do</label>
              <textarea 
                v-model="adjustmentReason" 
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
                rows="3"
                placeholder="Nhập lý do điều chỉnh..."
              ></textarea>
            </div>
            
            <div v-if="adjustmentType && adjustmentQuantity" class="p-3 bg-purple-50 dark:bg-purple-900/20 rounded-lg border-l-4 border-purple-500">
              <h5 class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-1">Kết quả sau điều chỉnh:</h5>
              <p class="text-sm text-gray-700 dark:text-gray-300">
                Tồn kho hiện tại: {{ formatNumber(selectedProduct.stockQuantity) }} 
                → <strong>{{ formatNumber(calculateNewStock()) }}</strong>
              </p>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="closeAdjustmentModal" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
            Hủy
          </button>
          <button @click="confirmAdjustment" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200">
            Xác nhận
          </button>
        </div>
      </div>
    </div>

    <!-- Inventory History Modal -->
    <div v-if="showHistoryModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeHistoryModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">history</i>
            Lịch sử tồn kho
          </h3>
          <button @click="closeHistoryModal" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="selectedProduct" class="space-y-4">
            <div class="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4 class="text-sm font-semibold text-gray-900 dark:text-gray-100">{{ selectedProduct.name }}</h4>
              <p class="text-xs text-gray-600 dark:text-gray-400">SKU: {{ selectedProduct.sku }}</p>
            </div>
            
            <div v-if="loadingHistory" class="flex flex-col items-center justify-center p-12">
              <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
              <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải lịch sử...</p>
            </div>
            
            <div v-else-if="inventoryHistory.length === 0" class="flex flex-col items-center justify-center p-12">
              <div class="w-16 h-16 rounded-full bg-gray-100 dark:bg-gray-700 flex items-center justify-center mb-4">
                <i class="material-icons text-gray-400 dark:text-gray-500 text-3xl">history</i>
              </div>
              <p class="text-sm text-gray-500 dark:text-gray-400">Chưa có lịch sử tồn kho</p>
            </div>
            
            <div v-else class="space-y-3">
              <div v-for="log in inventoryHistory" :key="log.id" class="flex items-start gap-3 p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-900/70 transition-colors">
                <div class="w-10 h-10 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center flex-shrink-0">
                  <i class="material-icons text-purple-600 dark:text-purple-400">{{ getChangeTypeIcon(log.changeType) }}</i>
                </div>
                <div class="flex-1">
                  <div class="flex items-center justify-between mb-2">
                    <span class="text-sm font-semibold text-gray-900 dark:text-gray-100">{{ getChangeTypeText(log.changeType) }}</span>
                    <span class="text-xs text-gray-500 dark:text-gray-400">{{ formatDateTime(log.createdAt) }}</span>
                  </div>
                  <div class="flex items-center gap-4 text-xs text-gray-600 dark:text-gray-400 mb-1">
                    <span class="font-medium" :class="log.quantityChange > 0 ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'">
                      {{ log.quantityChange > 0 ? '+' : '' }}{{ formatNumber(log.quantityChange) }}
                    </span>
                    <span>Trước: {{ formatNumber(log.quantityBefore) }}</span>
                    <span>Sau: {{ formatNumber(log.quantityAfter) }}</span>
                  </div>
                  <p v-if="log.note" class="text-xs text-gray-500 dark:text-gray-400 mt-1">{{ log.note }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="closeHistoryModal" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAdminStore } from '@/stores/admin'

// Stores
const adminStore = useAdminStore()

// State
const loading = ref(false)
const products = ref([])
const searchKeyword = ref('')
const filterStockStatus = ref('all')
const filterBrand = ref('all')
const currentPage = ref(0)
const pageSize = ref(10)
const showAdjustmentModal = ref(false)
const showHistoryModal = ref(false)
const selectedProduct = ref(null)
const adjustmentType = ref('add')
const adjustmentQuantity = ref(1)
const adjustmentReason = ref('')
const inventoryHistory = ref([])
const loadingHistory = ref(false)

// Mock data removed - using real API data

// Computed
const totalProducts = computed(() => products.value.length)
const inStockProducts = computed(() => products.value.filter(p => p.stockQuantity > 0).length)
const lowStockProducts = computed(() => products.value.filter(p => p.stockQuantity > 0 && p.stockQuantity <= p.lowStockThreshold).length)
const outOfStockProducts = computed(() => products.value.filter(p => p.stockQuantity === 0).length)
const newProductsThisMonth = computed(() => {
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  
  return products.value.filter(p => {
    const productDate = new Date(p.updatedAt)
    return productDate.getMonth() === thisMonth && productDate.getFullYear() === thisYear
  }).length
})

const filteredProducts = computed(() => {
  let filtered = products.value || []

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.name.toLowerCase().includes(keyword) ||
      p.sku.toLowerCase().includes(keyword) ||
      p.brandName.toLowerCase().includes(keyword)
    )
  }

  if (filterStockStatus.value !== 'all') {
    filtered = filtered.filter(p => {
      switch (filterStockStatus.value) {
        case 'in-stock':
          return p.stockQuantity > p.lowStockThreshold
        case 'low-stock':
          return p.stockQuantity > 0 && p.stockQuantity <= p.lowStockThreshold
        case 'out-of-stock':
          return p.stockQuantity === 0
        default:
          return true
      }
    })
  }

  if (filterBrand.value !== 'all') {
    filtered = filtered.filter(p => p.brandName.toLowerCase() === filterBrand.value)
  }

  return filtered
})

const totalPages = computed(() => Math.ceil(filteredProducts.value.length / pageSize.value))
const paginatedProducts = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return filteredProducts.value.slice(start, end)
})

// Methods
const fetchProducts = async () => {
  loading.value = true
  try {
    const result = await adminStore.fetchProducts(0, 100, { isActive: true })
    products.value = result.content || []
  } catch (error) {
    ElMessage.error('Không thể tải danh sách sản phẩm')
  } finally {
    loading.value = false
  }
}

const fetchInventoryLogs = async () => {
  loadingHistory.value = true
  try {
    const result = await adminStore.fetchInventoryLogs(0, 50, {})
    inventoryHistory.value = result.content || []
  } catch (error) {
    console.error('Error loading inventory logs:', error)
  } finally {
    loadingHistory.value = false
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStockStatus.value = 'all'
  filterBrand.value = 'all'
  currentPage.value = 0
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

const openStockAdjustmentModal = () => {
  selectedProduct.value = null
  adjustmentType.value = 'add'
  adjustmentQuantity.value = 1
  adjustmentReason.value = ''
  showAdjustmentModal.value = true
}

const adjustStock = (product) => {
  selectedProduct.value = product
  adjustmentType.value = 'add'
  adjustmentQuantity.value = 1
  adjustmentReason.value = ''
  showAdjustmentModal.value = true
}

const closeAdjustmentModal = () => {
  showAdjustmentModal.value = false
  selectedProduct.value = null
}

const calculateNewStock = () => {
  if (!selectedProduct.value || !adjustmentQuantity.value) return 0
  
  switch (adjustmentType.value) {
    case 'add':
      return selectedProduct.value.stockQuantity + adjustmentQuantity.value
    case 'subtract':
      return selectedProduct.value.stockQuantity - adjustmentQuantity.value
    case 'set':
      return adjustmentQuantity.value
    default:
      return selectedProduct.value.stockQuantity
  }
}

const confirmAdjustment = async () => {
  if (!adjustmentQuantity.value || adjustmentQuantity.value <= 0) {
    ElMessage.error('Vui lòng nhập số lượng hợp lệ')
    return
  }

  if (!adjustmentReason.value.trim()) {
    ElMessage.error('Vui lòng nhập lý do điều chỉnh')
    return
  }

  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn điều chỉnh tồn kho từ ${formatNumber(selectedProduct.value.stockQuantity)} thành ${formatNumber(calculateNewStock())}?`,
      'Xác nhận điều chỉnh',
      {
        confirmButtonText: 'Xác nhận',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    // Simulate API call
    selectedProduct.value.stockQuantity = calculateNewStock()
    selectedProduct.value.updatedAt = new Date().toISOString()
    
    ElMessage.success('Đã điều chỉnh tồn kho thành công')
    closeAdjustmentModal()
  } catch {
    // User cancelled
  }
}

const restockProduct = async (product) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc chắn muốn nhập hàng cho sản phẩm ${product.name}?`,
      'Xác nhận nhập hàng',
      {
        confirmButtonText: 'Nhập hàng',
        cancelButtonText: 'Hủy',
        type: 'info'
      }
    )
    
    // Simulate restock
    product.stockQuantity += 10
    product.updatedAt = new Date().toISOString()
    
    ElMessage.success('Đã nhập hàng thành công')
  } catch {
    // User cancelled
  }
}

const viewInventoryHistory = async (product) => {
  selectedProduct.value = product
  showHistoryModal.value = true
  
  // Load inventory history from API
  loadingHistory.value = true
  try {
    const result = await adminStore.fetchInventoryLogs(0, 50, { variantId: product.id })
    inventoryHistory.value = result.content || []
  } catch (error) {
    console.error('Error loading inventory history:', error)
    inventoryHistory.value = []
  } finally {
    loadingHistory.value = false
  }
}

const closeHistoryModal = () => {
  showHistoryModal.value = false
  selectedProduct.value = null
  inventoryHistory.value = []
}

const exportInventory = (format) => {
  try {
    const dataToExport = filteredProducts.value || []
    if (dataToExport.length === 0) {
      ElMessage.warning('Không có dữ liệu để xuất')
      return
    }
    
    const exportData = dataToExport.map(product => ({
      'ID': product.id,
      'Tên sản phẩm': product.name,
      'SKU': product.sku,
      'Thương hiệu': product.brandName,
      'Kích thước': product.size,
      'Màu sắc': product.color,
      'Tồn kho': formatNumber(product.stockQuantity),
      'Ngưỡng cảnh báo': formatNumber(product.lowStockThreshold),
      'Giá nhập': formatCurrency(product.costPrice),
      'Giá bán': formatCurrency(product.priceBase),
      'Trạng thái': getStockStatusText(product.stockQuantity, product.lowStockThreshold),
      'Cập nhật cuối': formatDateTime(product.updatedAt)
    }))

    if (format === 'csv') {
      downloadCsv(exportData, 'inventory.csv')
      ElMessage.success('Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('inventory', exportData)
      ElMessage.success('Xuất JSON thành công!')
    }
  } catch (error) {
    console.error('Export error:', error)
    ElMessage.error('Có lỗi xảy ra khi xuất dữ liệu!')
  }
}

// Helper functions
const getStockStatusClass = (quantity, threshold) => {
  if (quantity === 0) return 'out-of-stock'
  if (quantity <= threshold) return 'low-stock'
  return 'in-stock'
}

const getStockStatusText = (quantity, threshold) => {
  if (quantity === 0) return 'Hết hàng'
  if (quantity <= threshold) return 'Sắp hết hàng'
  return 'Còn hàng'
}

const getChangeTypeIcon = (type) => {
  const icons = {
    restock: 'add_shopping_cart',
    sale: 'shopping_cart',
    adjustment: 'edit',
    return: 'keyboard_return',
    damaged: 'warning'
  }
  return icons[type] || 'help'
}

const getChangeTypeText = (type) => {
  const texts = {
    restock: 'Nhập hàng',
    sale: 'Bán hàng',
    adjustment: 'Điều chỉnh',
    return: 'Trả hàng',
    damaged: 'Hàng hỏng'
  }
  return texts[type] || type
}

const formatNumber = (num) => {
  return new Intl.NumberFormat('vi-VN').format(num)
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('vi-VN')
}

const formatDateTime = (dateString) => {
  return new Date(dateString).toLocaleString('vi-VN')
}

// Lifecycle
onMounted(() => {
  fetchProducts()
})
</script>




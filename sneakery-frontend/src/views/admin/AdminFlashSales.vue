<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- ===== PAGE HEADER ===== -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">flash_on</i>
            Quản lí Flash Sale
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý chương trình Flash Sale giảm giá sốc</p>
        </div>
        <div class="flex items-center gap-2">
          <button class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm" @click="openCreateModal">
            <i class="material-icons text-base">add</i>
            Tạo Flash Sale
          </button>
        </div>
      </div>
    </div>

    <!-- ===== STATS GRID ===== -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">bolt</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ activeFlashSalesCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">ĐANG DIỄN RA</p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">schedule</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ upcomingFlashSalesCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">SẮP DIỄN RA</p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">event_busy</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ expiredFlashSalesCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">ĐÃ KẾT THÚC</p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">shopping_cart</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ totalProductsInSale }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">SẢN PHẨM THAM GIA</p>
        </div>
      </div>
    </div>

    <!-- ===== FILTERS ===== -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">search</i>
            Tìm kiếm
          </label>
          <input 
            type="text" 
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            v-model="searchKeyword"
            placeholder="Tìm theo tên sản phẩm..."
          />
        </div>
        
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">filter_list</i>
            Trạng thái
          </label>
          <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterStatus">
            <option value="all">Tất cả</option>
            <option value="active">Đang diễn ra</option>
            <option value="upcoming">Sắp diễn ra</option>
            <option value="expired">Đã kết thúc</option>
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

    <!-- ===== LOADING STATE ===== -->
    <div v-if="loading" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
      <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải dữ liệu...</p>
    </div>

    <!-- ===== EMPTY STATE ===== -->
    <div v-else-if="filteredFlashSales.length === 0" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">flash_on</i>
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có Flash Sale nào</h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 text-center mb-4">Tạo chương trình Flash Sale đầu tiên để thu hút khách hàng</p>
      <button class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm" @click="openCreateModal">
        <i class="material-icons text-base">add</i>
        Tạo Flash Sale
      </button>
    </div>

    <!-- ===== FLASH SALES TABLE ===== -->
    <div v-else class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider w-20">ID</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Sản phẩm</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider w-32">Giá gốc</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider w-28">Giảm giá</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider w-32">Giá Flash Sale</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider w-28">Số lượng</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider w-48">Thời gian</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider w-36">Trạng thái</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider w-40">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="sale in paginatedFlashSales" :key="sale.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300">{{ sale.id }}</td>
              <td class="px-4 py-4">
                <div class="flex items-center gap-3">
                  <img 
                    :src="sale.productImage" 
                    :alt="sale.productName"
                    class="w-12 h-12 rounded-lg object-cover bg-gray-100 dark:bg-gray-700"
                    loading="lazy"
                    decoding="async"
                    @error="handleImageError"
                  />
                  <div class="min-w-0">
                    <div class="text-sm font-medium text-gray-900 dark:text-gray-100 truncate">{{ sale.productName }}</div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">{{ sale.brandName }}</div>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-600 dark:text-gray-400 line-through">{{ formatCurrency(sale.originalPrice) }}</div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400">
                  -{{ sale.discountPercentage }}%
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="text-sm font-semibold text-purple-600 dark:text-purple-400">{{ formatCurrency(sale.flashSalePrice) }}</div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-700 dark:text-gray-300">
                  <span class="font-medium">{{ sale.quantity }}</span>
                  <span class="text-xs text-gray-500 dark:text-gray-400 ml-1">sản phẩm</span>
                </div>
              </td>
              <td class="px-4 py-4">
                <div class="space-y-1">
                  <div class="flex items-center gap-1.5 text-xs text-gray-600 dark:text-gray-400">
                    <i class="material-icons text-sm">play_arrow</i>
                    <span>{{ formatDateTime(sale.startTime) }}</span>
                  </div>
                  <div class="flex items-center gap-1.5 text-xs text-gray-600 dark:text-gray-400">
                    <i class="material-icons text-sm">stop</i>
                    <span>{{ formatDateTime(sale.endTime) }}</span>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span 
                  class="inline-flex items-center gap-1.5 px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': getStatusClass(sale) === 'status-active',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': getStatusClass(sale) === 'status-upcoming',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': getStatusClass(sale) === 'status-expired'
                  }"
                >
                  <i class="material-icons text-sm">{{ getStatusIcon(sale) }}</i>
                  {{ getStatusText(sale) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-1">
                  <button 
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors" 
                    @click="openEditModal(sale)"
                    title="Chỉnh sửa"
                  >
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button 
                    class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors" 
                    @click="confirmDelete(sale)"
                    title="Xóa"
                  >
                    <i class="material-icons text-base">delete</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- ===== PAGINATION ===== -->
    <div v-if="totalPages > 1" class="flex items-center justify-center gap-4 p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <button 
        class="flex items-center gap-1 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        :disabled="currentPage === 1"
        @click="currentPage--"
      >
        <i class="material-icons text-base">chevron_left</i>
        Trước
      </button>
      
      <span class="text-sm text-gray-700 dark:text-gray-300">
        Trang {{ currentPage }} / {{ totalPages }}
      </span>
      
      <button 
        class="flex items-center gap-1 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >
        Sau
        <i class="material-icons text-base">chevron_right</i>
      </button>
    </div>

    <!-- ===== CREATE/EDIT MODAL ===== -->
    <div v-if="showModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">{{ isEditMode ? 'edit' : 'add' }}</i>
            {{ isEditMode ? 'Chỉnh sửa Flash Sale' : 'Tạo Flash Sale mới' }}
          </h2>
          <button class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors" @click="closeModal">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        
        <div class="p-4">
          <form @submit.prevent="saveFlashSale" class="space-y-4">
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Sản phẩm *</label>
              <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="formData.productId" required>
                <option value="">-- Chọn sản phẩm --</option>
                <option v-for="product in availableProducts" :key="product.id" :value="product.id">
                  {{ product.name }} - {{ product.brandName }} ({{ formatCurrency(product.price) }})
                </option>
              </select>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Giảm giá (%) *</label>
                <input 
                  type="number" 
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  v-model.number="formData.discountPercentage"
                  placeholder="VD: 50"
                  min="1"
                  max="99"
                  required
                />
              </div>
              
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số lượng *</label>
                <input 
                  type="number" 
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  v-model.number="formData.quantity"
                  placeholder="VD: 100"
                  min="1"
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Thời gian bắt đầu *</label>
                <input 
                  type="datetime-local" 
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  v-model="formData.startTime"
                  required
                />
              </div>
              
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Thời gian kết thúc *</label>
                <input 
                  type="datetime-local" 
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  v-model="formData.endTime"
                  required
                />
              </div>
            </div>

            <div v-if="formData.productId && formData.discountPercentage" class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700 space-y-2">
              <div class="flex items-center justify-between text-sm">
                <span class="text-gray-600 dark:text-gray-400">Giá gốc:</span>
                <strong class="text-gray-900 dark:text-gray-100">{{ formatCurrency(selectedProduct?.price || 0) }}</strong>
              </div>
              <div class="flex items-center justify-between text-sm">
                <span class="text-gray-600 dark:text-gray-400">Giảm {{ formData.discountPercentage }}%:</span>
                <strong class="text-red-600 dark:text-red-400">-{{ formatCurrency(discountAmount) }}</strong>
              </div>
              <div class="flex items-center justify-between text-sm pt-2 border-t border-gray-200 dark:border-gray-700">
                <span class="font-medium text-gray-700 dark:text-gray-300">Giá Flash Sale:</span>
                <strong class="text-lg font-bold text-purple-600 dark:text-purple-400">{{ formatCurrency(flashSalePrice) }}</strong>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
              <button type="button" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" @click="closeModal">
                <i class="material-icons text-base">close</i>
                Hủy
              </button>
              <button type="submit" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed" :disabled="saving">
                <i class="material-icons text-base" :class="{ 'animate-spin': saving }">{{ saving ? 'hourglass_empty' : 'save' }}</i>
                {{ saving ? 'Đang lưu...' : 'Lưu' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <div v-if="showDeleteModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="showDeleteModal = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-md w-full border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-red-600 dark:text-red-400">warning</i>
            Xác nhận xóa
          </h2>
          <button class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors" @click="showDeleteModal = false">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        
        <div class="p-4 space-y-4">
          <p class="text-sm text-gray-700 dark:text-gray-300">Bạn có chắc chắn muốn xóa Flash Sale này?</p>
          <p class="text-xs text-gray-500 dark:text-gray-400">Hành động này không thể hoàn tác.</p>
          
          <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
            <button class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" @click="showDeleteModal = false">
              <i class="material-icons text-base">close</i>
              Hủy
            </button>
            <button class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-red-500 to-red-600 rounded-lg hover:from-red-600 hover:to-red-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed" @click="deleteFlashSale" :disabled="deleting">
              <i class="material-icons text-base" :class="{ 'animate-spin': deleting }">{{ deleting ? 'hourglass_empty' : 'delete' }}</i>
              {{ deleting ? 'Đang xóa...' : 'Xóa' }}
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
import notificationService from '@/utils/notificationService'
import confirmDialogService from '@/utils/confirmDialogService'
import logger from '@/utils/logger'
import { formatCurrency, formatDateTime } from '@/utils/formatters'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('all')
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const saleToDelete = ref(null)
const currentPage = ref(1)
const itemsPerPage = 10

// Available products from API - chỉ dùng dữ liệu thật từ database
const availableProducts = ref([])

// Form data
const formData = ref({
  id: null,
  productId: '',
  discountPercentage: 0,
  quantity: 0,
  startTime: '',
  endTime: ''
})

// Flash sales data
const flashSales = ref([])

// Computed
const selectedProduct = computed(() => {
  return availableProducts.value.find(p => p.id === formData.value.productId)
})

const discountAmount = computed(() => {
  if (!selectedProduct.value || !formData.value.discountPercentage) return 0
  return (selectedProduct.value.price * formData.value.discountPercentage) / 100
})

const flashSalePrice = computed(() => {
  if (!selectedProduct.value || !formData.value.discountPercentage) return 0
  return selectedProduct.value.price - discountAmount.value
})

const filteredFlashSales = computed(() => {
  let result = flashSales.value

  // Filter by search
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(sale => 
      sale.productName.toLowerCase().includes(keyword) ||
      sale.brandName.toLowerCase().includes(keyword)
    )
  }

  // Filter by status
  if (filterStatus.value !== 'all') {
    const now = new Date()
    result = result.filter(sale => {
      const start = new Date(sale.startTime)
      const end = new Date(sale.endTime)
      
      if (filterStatus.value === 'active') {
        return now >= start && now <= end && sale.isActive
      } else if (filterStatus.value === 'upcoming') {
        return now < start && sale.isActive
      } else if (filterStatus.value === 'expired') {
        return (now > end || !sale.isActive)
      }
      return true
    })
  }

  return result
})

const paginatedFlashSales = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredFlashSales.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredFlashSales.value.length / itemsPerPage)
})

const activeFlashSalesCount = computed(() => {
  const now = new Date()
  return flashSales.value.filter(sale => {
    const start = new Date(sale.startTime)
    const end = new Date(sale.endTime)
    return now >= start && now <= end && sale.isActive
  }).length
})

const upcomingFlashSalesCount = computed(() => {
  const now = new Date()
  return flashSales.value.filter(sale => {
    const start = new Date(sale.startTime)
    return now < start && sale.isActive
  }).length
})

const expiredFlashSalesCount = computed(() => {
  const now = new Date()
  return flashSales.value.filter(sale => {
    const end = new Date(sale.endTime)
    return now > end || !sale.isActive
  }).length
})

const totalProductsInSale = computed(() => {
  return new Set(flashSales.value.map(s => s.productId)).size
})

// Methods
const loadFlashSales = async () => {
  try {
    loading.value = true
    
    // Load flash sales from API
    const salesResult = await adminStore.fetchFlashSales()
    const rawFlashSales = salesResult || []
    
    // Load products for dropdown and to get product prices
    const productsResult = await adminStore.fetchProducts(0, 100, { isActive: true })
    const allProducts = productsResult.content || []
    availableProducts.value = allProducts
    
    // Transform flash sale data to match UI format
    flashSales.value = rawFlashSales.map(sale => {
      // Use originalPrice from API if available (now included in FlashSaleDto)
      const originalPrice = sale.originalPrice ? Number(sale.originalPrice) : 0
      const discountPercent = sale.discountPercent ? Number(sale.discountPercent) : 0
      const discountAmount = (originalPrice * discountPercent) / 100
      const flashSalePrice = originalPrice > 0 ? originalPrice - discountAmount : 0
      
      // Find product for additional info if needed
      const product = allProducts.find(p => p.id === sale.productId)
      
      return {
        id: sale.id,
        productId: sale.productId,
        productName: sale.productName || product?.name || 'N/A',
        brandName: sale.brandName || product?.brandName || 'N/A',
        productImage: sale.imageUrl || product?.mainImageUrl || product?.imageUrl || product?.image || null,
        originalPrice: originalPrice,
        discountPercentage: discountPercent,
        flashSalePrice: flashSalePrice,
        quantity: sale.quantityLimit ?? 0,
        startTime: sale.startTime,
        endTime: sale.endTime,
        isActive: sale.isActive ?? true
      }
    })
    
    logger.log('✅ Flash sales loaded from API', flashSales.value)
  } catch (error) {
    logger.error('Lỗi khi tải danh sách flash sales:', error)
    notificationService.apiError(error, 'Không thể tải danh sách flash sales')
  } finally {
    loading.value = false
  }
}

const getStatusClass = (sale) => {
  const now = new Date()
  const start = new Date(sale.startTime)
  const end = new Date(sale.endTime)
  
  if (!sale.isActive || now > end) return 'status-expired'
  if (now >= start && now <= end) return 'status-active'
  if (now < start) return 'status-upcoming'
  return 'status-expired'
}

const getStatusIcon = (sale) => {
  const now = new Date()
  const start = new Date(sale.startTime)
  const end = new Date(sale.endTime)
  
  if (!sale.isActive || now > end) return 'cancel'
  if (now >= start && now <= end) return 'bolt'
  if (now < start) return 'schedule'
  return 'cancel'
}

const getStatusText = (sale) => {
  const now = new Date()
  const start = new Date(sale.startTime)
  const end = new Date(sale.endTime)
  
  if (!sale.isActive || now > end) return 'Đã kết thúc'
  if (now >= start && now <= end) return 'Đang diễn ra'
  if (now < start) return 'Sắp diễn ra'
  return 'Đã kết thúc'
}

const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    id: null,
    productId: '',
    discountPercentage: 0,
    quantity: 0,
    startTime: '',
    endTime: ''
  }
  showModal.value = true
}

const openEditModal = (sale) => {
  isEditMode.value = true
  // Format datetime for datetime-local input (YYYY-MM-DDTHH:mm)
  const formatDateTimeLocal = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return ''
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day}T${hours}:${minutes}`
  }
  
  formData.value = {
    id: sale.id,
    productId: sale.productId,
    discountPercentage: sale.discountPercentage || 0,
    quantity: sale.quantity || 0,
    startTime: formatDateTimeLocal(sale.startTime),
    endTime: formatDateTimeLocal(sale.endTime)
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  formData.value = {
    id: null,
    productId: '',
    discountPercentage: 0,
    quantity: 0,
    startTime: '',
    endTime: ''
  }
}

const saveFlashSale = async () => {
  saving.value = true
  try {
    // Transform to API format (discountPercent instead of discountPercentage, quantityLimit instead of quantity)
    const flashSaleData = {
      productId: Number(formData.value.productId),
      discountPercent: Number(formData.value.discountPercentage),
      quantityLimit: Number(formData.value.quantity),
      // Parse datetime-local format to ISO string
      startTime: formData.value.startTime ? new Date(formData.value.startTime).toISOString() : null,
      endTime: formData.value.endTime ? new Date(formData.value.endTime).toISOString() : null
    }
    
    if (isEditMode.value) {
      await adminStore.updateFlashSale(formData.value.id, flashSaleData)
      notificationService.success('Thành công','Cập nhật Flash Sale thành công!')
    } else {
      await adminStore.createFlashSale(flashSaleData)
      notificationService.success('Thành công','Tạo Flash Sale thành công!')
    }
    
    closeModal()
    loadFlashSales()
  } catch (error) {
    logger.error('Error saving flash sale:', error)
    notificationService.apiError(error, 'Lỗi khi lưu Flash Sale')
  } finally {
    saving.value = false
  }
}

const confirmDelete = (sale) => {
  saleToDelete.value = sale
  showDeleteModal.value = true
}

const deleteFlashSale = async () => {
  deleting.value = true
  try {
    await adminStore.deleteFlashSale(saleToDelete.value.id)
    showDeleteModal.value = false
    saleToDelete.value = null
    notificationService.success('Thành công','Xóa Flash Sale thành công!')
    loadFlashSales()
  } catch (error) {
    logger.error('Error deleting flash sale:', error)
    notificationService.apiError(error, 'Lỗi khi xóa Flash Sale')
  } finally {
    deleting.value = false
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = 'all'
  currentPage.value = 1
}

// formatCurrency và formatDateTime đã được import từ @/utils/formatters

const handleImageError = (e) => {
  e.target.src = '/placeholder-image.png'
}

// Lifecycle
onMounted(() => {
  loadFlashSales()
})
</script>





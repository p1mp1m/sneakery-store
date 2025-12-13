<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">local_offer</i>
            Quản lý Coupon
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý mã giảm giá và khuyến mãi</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="exportCoupons('csv')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_download</i>
            CSV
          </button>
          <button @click="exportCoupons('json')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">code</i>
            JSON
          </button>
          <button @click="openCreateModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">add</i>
            Thêm Coupon
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">local_offer</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.totalCoupons || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng Coupon</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.activeCoupons || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đang hoạt động</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">schedule</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.expiringSoon || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Sắp hết hạn</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">trending_up</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.totalUsed || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đã sử dụng</p>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
            <input
              v-model="searchQuery"
              @input="handleSearch"
              type="text"
              placeholder="Tìm kiếm coupon..."
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
            <button v-if="searchQuery" @click="clearSearch" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Trạng thái</label>
            <select v-model="filters.status" @change="loadCoupons" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="all">Tất cả</option>
              <option value="active">Đang hoạt động</option>
              <option value="inactive">Không hoạt động</option>
              <option value="expired">Đã hết hạn</option>
            </select>
          </div>
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Loại giảm giá</label>
            <select v-model="filters.discountType" @change="loadCoupons" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="all">Tất cả</option>
              <option value="fixed">Giảm giá cố định</option>
              <option value="percentage">Giảm giá phần trăm</option>
            </select>
          </div>
          <button @click="resetFilters" class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium mt-6">
            <i class="material-icons text-base">refresh</i>
            Reset
          </button>
        </div>
      </div>
    </div>

    <!-- Coupons Table -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
      <div v-if="loading" class="flex flex-col items-center justify-center p-12">
        <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải dữ liệu...</p>
      </div>
      
      <div v-else-if="coupons.length === 0" class="flex flex-col items-center justify-center p-12">
        <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">local_offer</i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có coupon nào</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">Tạo coupon đầu tiên để bắt đầu</p>
        <button @click="openCreateModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium">
          <i class="material-icons text-base">add</i>
          Thêm Coupon
        </button>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Mã Coupon</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Mô tả</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Loại giảm giá</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Giá trị</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thời gian</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Sử dụng</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="coupon in coupons" :key="coupon.id" class="hover:bg-gray-50 dark:hover:bg-gray-700/30 transition-colors">
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <code class="px-2 py-1 text-xs bg-purple-100 dark:bg-purple-900/30 text-purple-800 dark:text-purple-300 rounded font-mono">{{ coupon.code }}</code>
                  <button @click="copyCode(coupon.code)" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors" title="Sao chép">
                    <i class="material-icons text-base">content_copy</i>
                  </button>
                </div>
              </td>
              <td class="px-4 py-3 text-sm text-gray-900 dark:text-gray-100">{{ coupon.description || 'Không có mô tả' }}</td>
              <td class="px-4 py-3">
                <span class="px-2 py-1 text-xs font-medium rounded-full" :class="coupon.discount_type === 'fixed' ? 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400' : 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'">
                  {{ coupon.discount_type === 'fixed' ? 'Cố định' : 'Phần trăm' }}
                </span>
              </td>
              <td class="px-4 py-3">
                <div class="flex flex-col gap-1">
                  <span class="text-sm font-semibold text-gray-900 dark:text-gray-100">
                    <span v-if="coupon.discount_type === 'fixed'">{{ formatCurrency(coupon.discount_value) }}</span>
                    <span v-else>{{ coupon.discount_value }}%</span>
                  </span>
                  <div v-if="coupon.max_discount_amount" class="text-xs text-gray-500 dark:text-gray-400">
                    Tối đa: {{ formatCurrency(coupon.max_discount_amount) }}
                  </div>
                </div>
              </td>
              <td class="px-4 py-3">
                <div class="flex flex-col gap-1 text-xs">
                  <div class="flex items-center gap-1 text-gray-600 dark:text-gray-400">
                    <i class="material-icons text-sm">schedule</i>
                    {{ formatDate(coupon.start_at) }}
                  </div>
                  <div class="flex items-center gap-1 text-gray-600 dark:text-gray-400">
                    <i class="material-icons text-sm">event</i>
                    {{ formatDate(coupon.end_at) }}
                  </div>
                </div>
              </td>
              <td class="px-4 py-3">
                <div class="flex flex-col gap-1">
                  <div class="text-sm text-gray-900 dark:text-gray-100">{{ coupon.uses_count || 0 }}/{{ coupon.max_uses || '∞' }}</div>
                  <div v-if="coupon.max_uses" class="w-full h-1.5 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                    <div 
                      class="h-full bg-purple-500 transition-all duration-300" 
                      :style="{ width: `${(coupon.uses_count / coupon.max_uses) * 100}%` }"
                    ></div>
                  </div>
                </div>
              </td>
              <td class="px-4 py-3">
                <span class="px-2 py-1 text-xs font-medium rounded-full" 
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': getStatusClass(coupon) === 'status-active',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': getStatusClass(coupon) === 'status-expired',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': getStatusClass(coupon) === 'status-pending',
                    'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300': getStatusClass(coupon) === 'status-inactive'
                  }">
                  {{ getStatusText(coupon) }}
                </span>
              </td>
              <td class="px-4 py-3 text-center">
                <div class="flex items-center justify-center gap-1">
                  <button @click="viewCoupon(coupon)" class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded transition-colors" title="Xem chi tiết">
                    <i class="material-icons text-base">visibility</i>
                  </button>
                  <button @click="editCoupon(coupon)" class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded transition-colors" title="Chỉnh sửa">
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button @click="toggleCouponStatus(coupon)" class="p-1.5 text-yellow-600 dark:text-yellow-400 hover:bg-yellow-50 dark:hover:bg-yellow-900/20 rounded transition-colors" title="Bật/tắt">
                    <i class="material-icons text-base">{{ coupon.is_active ? 'pause' : 'play_arrow' }}</i>
                  </button>
                  <button @click="deleteCoupon(coupon)" class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded transition-colors" title="Xóa">
                    <i class="material-icons text-base">delete</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="!loading && coupons.length > 0" class="flex flex-col sm:flex-row items-center justify-between gap-4 px-4 py-3 border-t border-gray-200 dark:border-gray-700">
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Hiển thị {{ (currentPage * pageSize) + 1 }}-{{ Math.min((currentPage + 1) * pageSize, totalItems) }} 
          trong {{ totalItems }} coupon
        </div>
        <div class="flex items-center gap-2">
          <button 
            @click="goToPage(currentPage - 1)" 
            :disabled="currentPage === 0"
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="material-icons text-base">chevron_left</i>
          </button>
          <span class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300">{{ currentPage + 1 }} / {{ totalPages }}</span>
          <button 
            @click="goToPage(currentPage + 1)" 
            :disabled="currentPage >= totalPages - 1"
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="material-icons text-base">chevron_right</i>
          </button>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">{{ isEditMode ? 'edit' : 'add' }}</i>
            {{ isEditMode ? 'Chỉnh sửa Coupon' : 'Thêm Coupon mới' }}
          </h3>
          <button @click="closeModal" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <form @submit.prevent="saveCoupon" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Mã Coupon *</label>
                <input
                  v-model="formData.code"
                  type="text"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Nhập mã coupon..."
                  required
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Mô tả</label>
                <input
                  v-model="formData.description"
                  type="text"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Mô tả coupon..."
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Loại giảm giá *</label>
                <select v-model="formData.discount_type" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" required>
                  <option value="fixed">Giảm giá cố định (VNĐ)</option>
                  <option value="percentage">Giảm giá phần trăm (%)</option>
                </select>
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Giá trị giảm giá *</label>
                <input
                  v-model="formData.discount_value"
                  type="number"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Nhập giá trị..."
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Giá trị đơn hàng tối thiểu</label>
                <input
                  v-model="formData.min_order_amount"
                  type="number"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="0"
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Giá trị giảm tối đa</label>
                <input
                  v-model="formData.max_discount_amount"
                  type="number"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Không giới hạn"
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Ngày bắt đầu *</label>
                <input
                  v-model="formData.start_at"
                  type="datetime-local"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  required
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Ngày kết thúc *</label>
                <input
                  v-model="formData.end_at"
                  type="datetime-local"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số lần sử dụng tối đa</label>
                <input
                  v-model="formData.max_uses"
                  type="number"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Không giới hạn"
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số lần sử dụng mỗi user</label>
                <input
                  v-model="formData.max_uses_per_user"
                  type="number"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="1"
                />
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Áp dụng cho</label>
              <select v-model="formData.applicable_to" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
                <option value="all">Tất cả sản phẩm</option>
                <option value="brand">Thương hiệu</option>
                <option value="category">Danh mục</option>
                <option value="product">Sản phẩm cụ thể</option>
              </select>
            </div>

            <div class="flex items-center gap-2">
              <input
                v-model="formData.is_active"
                type="checkbox"
                class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
              />
              <label class="text-sm text-gray-700 dark:text-gray-300">Kích hoạt coupon</label>
            </div>
          </form>
        </div>
        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="closeModal" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
            <i class="material-icons text-base">close</i>
            Hủy
          </button>
          <button @click="saveCoupon" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed" :disabled="submitting">
            <i class="material-icons text-base" :class="{ 'animate-spin': submitting }">{{ isEditMode ? 'save' : 'add' }}</i>
            {{ submitting ? 'Đang lưu...' : (isEditMode ? 'Cập nhật' : 'Tạo mới') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <ConfirmDialog
      v-model="showDeleteModal"
      type="danger"
      title="Xác nhận xóa Coupon"
      :message="`Bạn có chắc chắn muốn xóa coupon '${couponToDelete?.code}'?`"
      confirm-text="Xóa"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="handleDelete"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import notificationService from '@/utils/notificationService'
import ConfirmDialog from '@/assets/components/common/ConfirmDialog.vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { debounce } from '@/utils/debounce'
import logger from '@/utils/logger'
import { formatCurrency, formatDate, formatDateTime } from '@/utils/formatters'

const adminStore = useAdminStore()

// State
const coupons = ref([])
const stats = ref(null)
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const couponToDelete = ref(null)

// Search and filters
const searchQuery = ref('')
const filters = ref({
  status: 'all',
  discountType: 'all'
})

// Form data
const formData = ref({
  code: '',
  description: '',
  discount_type: 'fixed',
  discount_value: 0,
  min_order_amount: null,
  max_discount_amount: null,
  start_at: '',
  end_at: '',
  max_uses: null,
  max_uses_per_user: 1,
  applicable_to: 'all',
  applicable_id: null,
  is_active: true
})

// Computed
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

// Methods
const loadCoupons = async () => {
  try {
    loading.value = true
    
    // Prepare filters for API
    const apiFilters = {
      search: searchQuery.value || undefined,
      status: filters.value.status !== 'all' ? filters.value.status : undefined,
      discountType: filters.value.discountType !== 'all' ? filters.value.discountType : undefined
    }
    
    const response = await adminStore.fetchCoupons(currentPage.value, pageSize.value, apiFilters)
    coupons.value = response.content || []
    totalItems.value = response.totalElements || 0
  } catch (error) {
    notificationService.apiError(error, 'Lỗi khi tải danh sách coupon')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    // Mock stats - replace with actual API call
    stats.value = {
      totalCoupons: 25,
      activeCoupons: 18,
      expiringSoon: 3,
      totalUsed: 156
    }
  } catch (error) {
    logger.error('Error loading stats:', error)
  }
}

const handleSearch = debounce(() => {
  currentPage.value = 0
  loadCoupons()
}, 300)

const clearSearch = () => {
  searchQuery.value = ''
  currentPage.value = 0
  loadCoupons()
}

const resetFilters = () => {
  filters.value = {
    status: 'all',
    discountType: 'all'
  }
  searchQuery.value = ''
  currentPage.value = 0
  loadCoupons()
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadCoupons()
  }
}

const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    code: '',
    description: '',
    discount_type: 'fixed',
    discount_value: 0,
    min_order_amount: null,
    max_discount_amount: null,
    start_at: '',
    end_at: '',
    max_uses: null,
    max_uses_per_user: 1,
    applicable_to: 'all',
    applicable_id: null,
    is_active: true
  }
  showModal.value = true
}

const editCoupon = (coupon) => {
  isEditMode.value = true
  formData.value = { ...coupon }
  showModal.value = true
}

const viewCoupon = (coupon) => {
  // Implement view coupon details
  notificationService.info('Thông tin',`Xem chi tiết coupon: ${coupon.code}`)
}

const saveCoupon = async () => {
  try {
    submitting.value = true
    if (isEditMode.value) {
      await adminStore.updateCoupon(formData.value.id, formData.value)
      notificationService.success('Thành công','Cập nhật coupon thành công')
    } else {
      await adminStore.createCoupon(formData.value)
      notificationService.success('Thành công','Tạo coupon thành công')
    }
    closeModal()
    loadCoupons()
  } catch (error) {
    notificationService.apiError(error, 'Lỗi khi lưu coupon')
  } finally {
    submitting.value = false
  }
}

const toggleCouponStatus = async (coupon) => {
  try {
    await adminStore.toggleCouponStatus(coupon.id)
    notificationService.success('Thành công',`Đã ${coupon.is_active ? 'tắt' : 'bật'} coupon`)
    loadCoupons()
  } catch (error) {
    notificationService.apiError(error, 'Lỗi khi thay đổi trạng thái coupon')
  }
}

const deleteCoupon = (coupon) => {
  couponToDelete.value = coupon
  showDeleteModal.value = true
}

const handleDelete = async () => {
  try {
    deleting.value = true
    await adminStore.deleteCoupon(couponToDelete.value.id)
    notificationService.success('Thành công','Xóa coupon thành công')
    showDeleteModal.value = false
    loadCoupons()
  } catch (error) {
    notificationService.apiError(error, 'Lỗi khi xóa coupon')
  } finally {
    deleting.value = false
  }
}

const closeModal = () => {
  showModal.value = false
  isEditMode.value = false
}

const copyCode = (code) => {
  navigator.clipboard.writeText(code)
  notificationService.success('Thành công','Đã sao chép mã coupon')
}

const exportCoupons = (format) => {
  try {
    const data = coupons.value.map(coupon => ({
      'Mã Coupon': coupon.code,
      'Mô tả': coupon.description,
      'Loại giảm giá': coupon.discount_type === 'fixed' ? 'Cố định' : 'Phần trăm',
      'Giá trị': coupon.discount_value,
      'Ngày bắt đầu': formatDate(coupon.start_at),
      'Ngày kết thúc': formatDate(coupon.end_at),
      'Số lần sử dụng': coupon.uses_count || 0,
      'Trạng thái': getStatusText(coupon)
    }))

    if (format === 'csv') {
      downloadCsv(data, 'coupons')
    } else {
      downloadJson(data, 'coupons')
    }
    notificationService.success('Thành công',`Đã xuất danh sách coupon (${format.toUpperCase()})`)
  } catch (error) {
    notificationService.apiError(error, 'Lỗi khi xuất dữ liệu')
  }
}

// Helper functions
const getStatusClass = (coupon) => {
  const now = new Date()
  const startDate = new Date(coupon.start_at)
  const endDate = new Date(coupon.end_at)

  if (!coupon.is_active) return 'status-inactive'
  if (now < startDate) return 'status-pending'
  if (now > endDate) return 'status-expired'
  return 'status-active'
}

const getStatusText = (coupon) => {
  const now = new Date()
  const startDate = new Date(coupon.start_at)
  const endDate = new Date(coupon.end_at)

  if (!coupon.is_active) return 'Không hoạt động'
  if (now < startDate) return 'Chưa bắt đầu'
  if (now > endDate) return 'Đã hết hạn'
  return 'Đang hoạt động'
}

// formatCurrency và formatDate đã được import từ @/utils/formatters

// Lifecycle
onMounted(() => {
  loadCoupons()
  loadStats()
})
</script>




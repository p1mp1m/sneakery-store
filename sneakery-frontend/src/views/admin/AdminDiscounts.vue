<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">percent</i>
            Quản lý Mã Giảm Giá
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Tạo và quản lý các chương trình giảm giá, khuyến mãi</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="handleExport('csv')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium" title="Xuất CSV">
            <i class="material-icons text-base">download</i>
            Xuất CSV
          </button>
          <button @click="handleExport('json')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium" title="Xuất JSON">
            <i class="material-icons text-base">file_download</i>
            Xuất JSON
          </button>
          <button @click="showCreateDialog = true" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">add</i>
            Tạo mã giảm giá
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">local_activity</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.totalCoupons }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng mã giảm giá</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.activeCoupons }}</h3>
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
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.expiringSoon }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Sắp hết hạn</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">block</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.expiredCoupons }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đã hết hạn</p>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
            <input
              v-model="filters.search"
              @input="handleSearch"
              type="text"
              placeholder="Tìm theo mã, mô tả..."
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
            <button v-if="filters.search" @click="filters.search = ''; fetchCoupons()" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
              <i class="material-icons text-sm">percent</i>
              Loại giảm giá
            </label>
            <select v-model="filters.type" @change="fetchCoupons" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="">Tất cả</option>
              <option value="percent">Phần trăm (%)</option>
              <option value="amount">Số tiền cố định</option>
            </select>
          </div>
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
              <i class="material-icons text-sm">check_circle</i>
              Trạng thái
            </label>
            <select v-model="filters.status" @change="fetchCoupons" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="">Tất cả</option>
              <option value="active">Đang hoạt động</option>
              <option value="expired">Đã hết hạn</option>
              <option value="upcoming">Chưa bắt đầu</option>
            </select>
          </div>
          <button @click="resetFilters" class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium mt-6">
            <i class="material-icons text-base">refresh</i>
            Reset
          </button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
      <div v-if="loading" class="flex flex-col items-center justify-center p-12">
        <div class="space-y-4" role="status" aria-live="polite">
          <LoadingSkeleton
            v-for="n in 5"
            :key="n"
            type="list"
          />
          <span class="sr-only">Đang tải dữ liệu</span>
        </div>
      </div>
      
      <div v-else-if="coupons.length === 0" class="flex flex-col items-center justify-center p-12">
        <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">local_activity</i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có mã giảm giá</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">Nhấn nút 'Tạo mã giảm giá' để bắt đầu</p>
        <button @click="showCreateDialog = true" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium">
          <i class="material-icons text-base">add</i>
          Tạo mã giảm giá
        </button>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Mã</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Loại</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Giá trị</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Điều kiện</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thời gian</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Lượt dùng</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="coupon in coupons" :key="coupon.id" class="hover:bg-gray-50 dark:hover:bg-gray-700/30 transition-colors">
              <td class="px-4 py-3">
                <div class="flex items-center gap-2 mb-1">
                  <strong class="text-sm font-semibold text-gray-900 dark:text-gray-100">{{ coupon.code }}</strong>
                  <button @click="copyCouponCode(coupon.code)" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors" title="Sao chép">
                    <i class="material-icons text-base">content_copy</i>
                  </button>
                </div>
                <p class="text-xs text-gray-500 dark:text-gray-400">{{ coupon.description }}</p>
              </td>
              <td class="px-4 py-3">
                <span class="px-2 py-1 text-xs font-medium rounded-full flex items-center gap-1 w-fit" :class="coupon.discountType === 'percent' ? 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400' : 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'">
                  <i class="material-icons text-sm">{{ coupon.discountType === 'percent' ? 'percent' : 'attach_money' }}</i>
                  {{ coupon.discountType === 'percent' ? 'Phần trăm' : 'Số tiền' }}
                </span>
              </td>
              <td class="px-4 py-3">
                <div class="flex flex-col gap-1">
                  <strong class="text-sm font-semibold text-gray-900 dark:text-gray-100">
                    {{ coupon.discountType === 'percent' ? `${coupon.value}%` : formatPrice(coupon.value) }}
                  </strong>
                  <div v-if="coupon.maxDiscountAmount" class="text-xs text-gray-500 dark:text-gray-400">
                    Tối đa: {{ formatPrice(coupon.maxDiscountAmount) }}
                  </div>
                </div>
              </td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-1 text-sm" :class="coupon.minOrderAmount ? 'text-gray-900 dark:text-gray-100' : 'text-gray-500 dark:text-gray-400'">
                  <i class="material-icons text-base">{{ coupon.minOrderAmount ? 'shopping_cart' : 'check_circle' }}</i>
                  <span>{{ coupon.minOrderAmount ? `Đơn tối thiểu: ${formatPrice(coupon.minOrderAmount)}` : 'Không giới hạn' }}</span>
                </div>
              </td>
              <td class="px-4 py-3">
                <div class="flex flex-col gap-1 text-xs">
                  <div class="flex items-center gap-1 text-gray-600 dark:text-gray-400">
                    <i class="material-icons text-sm">event</i>
                    {{ formatDate(coupon.startAt) }}
                  </div>
                  <div class="flex items-center gap-1 text-gray-600 dark:text-gray-400">
                    <i class="material-icons text-sm">event</i>
                    {{ formatDate(coupon.endAt) }}
                  </div>
                </div>
              </td>
              <td class="px-4 py-3">
                <div class="flex flex-col gap-1">
                  <div v-if="coupon.maxUses" class="w-full h-1.5 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                    <div 
                      class="h-full bg-purple-500 transition-all duration-300" 
                      :style="{ width: getUsagePercentage(coupon) + '%' }"
                    ></div>
                  </div>
                  <span class="text-sm text-gray-900 dark:text-gray-100">
                    {{ coupon.usesCount }} / {{ coupon.maxUses || '∞' }}
                  </span>
                </div>
              </td>
              <td class="px-4 py-3">
                <span class="px-2 py-1 text-xs font-medium rounded-full" 
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': getCouponStatusClass(coupon) === 'status-active',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': getCouponStatusClass(coupon) === 'status-expired',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': getCouponStatusClass(coupon) === 'status-upcoming',
                    'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300': getCouponStatusClass(coupon) === 'status-inactive' || getCouponStatusClass(coupon) === 'status-full'
                  }">
                  {{ getCouponStatusText(coupon) }}
                </span>
              </td>
              <td class="px-4 py-3 text-center">
                <div class="flex items-center justify-center gap-1">
                  <button @click="editCoupon(coupon)" class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded transition-colors" title="Sửa">
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button @click="toggleCouponStatus(coupon)" class="p-1.5 text-yellow-600 dark:text-yellow-400 hover:bg-yellow-50 dark:hover:bg-yellow-900/20 rounded transition-colors" :title="coupon.isActive ? 'Vô hiệu hóa' : 'Kích hoạt'">
                    <i class="material-icons text-base">{{ coupon.isActive ? 'block' : 'check_circle' }}</i>
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
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1 && !loading && coupons.length > 0" class="flex flex-col sm:flex-row items-center justify-between gap-4 px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="text-sm text-gray-600 dark:text-gray-400">
        Hiển thị {{ (currentPage) * pageSize + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalElements) }} trong tổng số {{ totalElements }}
      </div>
      <div class="flex items-center gap-1">
        <button 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 0"
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <i class="material-icons text-base">chevron_left</i>
        </button>
        <button 
          v-for="page in visiblePages" 
          :key="page"
          @click="goToPage(page - 1)"
          class="px-3 py-1.5 text-sm font-medium rounded-lg transition-colors"
          :class="page - 1 === currentPage 
            ? 'bg-purple-500 text-white' 
            : 'text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600'"
        >
          {{ page }}
        </button>
        <button 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage >= totalPages - 1"
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <i class="material-icons text-base">chevron_right</i>
        </button>
      </div>
    </div>

    <!-- Create/Edit Dialog -->
    <div v-if="showCreateDialog" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeDialog">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">{{ editingCoupon ? 'Chỉnh sửa mã giảm giá' : 'Tạo mã giảm giá mới' }}</h3>
          <button @click="closeDialog" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <form @submit.prevent="saveCoupon" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Mã giảm giá *</label>
                <input 
                  v-model="formData.code"
                  type="text" 
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent disabled:opacity-50 disabled:cursor-not-allowed"
                  placeholder="VD: SUMMER2025"
                  required
                  :disabled="editingCoupon"
                />
                <small class="text-xs text-gray-500 dark:text-gray-400">Chữ in hoa, không dấu, không khoảng trắng</small>
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Mô tả</label>
                <input 
                  v-model="formData.description"
                  type="text" 
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Mô tả ngắn gọn về mã giảm giá"
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Loại giảm giá *</label>
                <select v-model="formData.discountType" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" required>
                  <option value="">-- Chọn loại --</option>
                  <option value="percent">Phần trăm (%)</option>
                  <option value="fixed">Số tiền cố định (VNĐ)</option>
                </select>
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Giá trị giảm *</label>
                <input 
                  v-model.number="formData.value"
                  type="number" 
                  min="0"
                  :max="formData.discountType === 'percent' ? 100 : undefined"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="VD: 10"
                  required
                />
                <small class="text-xs text-gray-500 dark:text-gray-400">
                  {{ formData.discountType === 'percent' ? 'Từ 1 đến 100%' : 'Số tiền VNĐ' }}
                </small>
              </div>
            </div>

            <div v-if="formData.discountType === 'percent'" class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số tiền giảm tối đa (VNĐ)</label>
              <input 
                v-model.number="formData.maxDiscountAmount"
                type="number" 
                min="0"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Để trống nếu không giới hạn"
              />
              <small class="text-xs text-gray-500 dark:text-gray-400">Giới hạn số tiền giảm tối đa khi dùng %</small>
            </div>

            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Đơn hàng tối thiểu (VNĐ)</label>
              <input 
                v-model.number="formData.minOrderAmount"
                type="number" 
                min="0"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Để trống nếu không giới hạn"
              />
              <small class="text-xs text-gray-500 dark:text-gray-400">Giá trị đơn hàng tối thiểu để áp dụng mã</small>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Ngày bắt đầu *</label>
                <input 
                  v-model="formData.startAt"
                  type="datetime-local" 
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  required
                />
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Ngày kết thúc *</label>
                <input 
                  v-model="formData.endAt"
                  type="datetime-local" 
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  required
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số lượt dùng tối đa</label>
                <input 
                  v-model.number="formData.maxUses"
                  type="number" 
                  min="1"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Để trống nếu không giới hạn"
                />
                <small class="text-xs text-gray-500 dark:text-gray-400">Tổng số lần mã có thể được sử dụng</small>
              </div>
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Số lần dùng/người</label>
                <input 
                  v-model.number="formData.maxUsesPerUser"
                  type="number" 
                  min="1"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="1"
                />
                <small class="text-xs text-gray-500 dark:text-gray-400">Mỗi khách hàng có thể dùng tối đa bao nhiêu lần</small>
              </div>
            </div>

            <div class="flex items-center gap-2">
              <input
                v-model="formData.isActive"
                type="checkbox"
                class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
              />
              <label class="text-sm text-gray-700 dark:text-gray-300">Kích hoạt ngay</label>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
              <button type="button" @click="closeDialog" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
                Hủy
              </button>
              <button type="submit" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed" :disabled="saving">
                <i class="material-icons text-base" :class="{ 'animate-spin': saving }">save</i>
                {{ saving ? 'Đang lưu...' : (editingCoupon ? 'Cập nhật' : 'Tạo mới') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import notificationService from '@/utils/notificationService'
import adminService from '@/services/adminService'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import logger from '@/utils/logger'
import LoadingSkeleton from '@/components/common/LoadingSkeleton.vue'
import { formatPrice, formatCurrency, formatDate, formatDateTime } from '@/utils/formatters'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const saving = ref(false)
const showCreateDialog = ref(false)
const editingCoupon = ref(null)
const coupons = ref([])
const currentPage = ref(0)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const filters = reactive({
  search: '',
  type: '',
  status: ''
})

const stats = reactive({
  totalCoupons: 0,
  activeCoupons: 0,
  expiringSoon: 0,
  expiredCoupons: 0
})

const formData = reactive({
  code: '',
  description: '',
  discountType: '',
  value: 0,
  maxDiscountAmount: null,
  minOrderAmount: null,
  startAt: '',
  endAt: '',
  maxUses: null,
  maxUsesPerUser: 1,
  isActive: true
})

// Computed
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(0, currentPage.value - 2)
  const end = Math.min(totalPages.value, start + 5)
  for (let i = start; i < end; i++) {
    pages.push(i + 1)
  }
  return pages
})

// Methods
const fetchCoupons = async () => {
  try {
    loading.value = true
    
    // Prepare filters for API
    const apiFilters = {
      search: filters.search || undefined,
      type: filters.type || undefined,
      status: filters.status || undefined
    }
    
    const result = await adminStore.fetchCoupons(currentPage.value, pageSize.value, apiFilters)
    coupons.value = result.content || result || []
    totalElements.value = result.totalElements || 0
    totalPages.value = result.totalPages || 0
    updateStats()
  } catch (error) {
    logger.error('Lỗi tải coupons:', error)
    notificationService.apiError(error, 'Không thể tải danh sách mã giảm giá')
    
    // Set empty array để tránh undefined errors
    coupons.value = []
    totalElements.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.totalCoupons = coupons.value.length
  stats.activeCoupons = coupons.value.filter(c => c.isActive && new Date(c.endAt) > new Date()).length
  stats.expiringSoon = coupons.value.filter(c => {
    const endDate = new Date(c.endAt)
    const now = new Date()
    const daysLeft = (endDate - now) / (1000 * 60 * 60 * 24)
    return daysLeft > 0 && daysLeft <= 7
  }).length
  stats.expiredCoupons = coupons.value.filter(c => new Date(c.endAt) < new Date()).length
}

const handleSearch = () => {
  // Debounce search
  setTimeout(() => {
    fetchCoupons()
  }, 300)
}

const resetFilters = () => {
  filters.search = ''
  filters.type = ''
  filters.status = ''
  fetchCoupons()
}

const editCoupon = (coupon) => {
  editingCoupon.value = coupon
  Object.assign(formData, {
    code: coupon.code,
    description: coupon.description,
    discountType: coupon.discountType,
    value: coupon.value,
    maxDiscountAmount: coupon.maxDiscountAmount,
    minOrderAmount: coupon.minOrderAmount,
    startAt: coupon.startAt ? coupon.startAt.substring(0, 16) : '',
    endAt: coupon.endAt ? coupon.endAt.substring(0, 16) : '',
    maxUses: coupon.maxUses,
    maxUsesPerUser: coupon.maxUsesPerUser,
    isActive: coupon.isActive
  })
  showCreateDialog.value = true
}

const saveCoupon = async () => {
  try {
    saving.value = true
    
    // Chuẩn bị data để gửi lên server
    const couponData = {
      code: formData.code.toUpperCase().trim(),
      description: formData.description?.trim() || null,
      discountType: formData.discountType,
      value: formData.value,
      maxDiscountAmount: formData.maxDiscountAmount || null,
      minOrderAmount: formData.minOrderAmount || null,
      startAt: formData.startAt,
      endAt: formData.endAt,
      maxUses: formData.maxUses || null,
      maxUsesPerUser: formData.maxUsesPerUser || 1,
      isActive: formData.isActive
    }
    
    if (editingCoupon.value) {
      await adminStore.updateCoupon(editingCoupon.value.id, couponData)
      notificationService.success('Thành công',`Đã cập nhật mã giảm giá "${couponData.code}" thành công!`)
    } else {
      await adminStore.createCoupon(couponData)
      notificationService.success('Thành công',`Đã tạo mã giảm giá "${couponData.code}" thành công!`)
    }
    
    closeDialog()
    fetchCoupons()
  } catch (error) {
    logger.error('Lỗi lưu coupon:', error)
    notificationService.apiError(error, 'Có lỗi xảy ra khi lưu mã giảm giá')
  } finally {
    saving.value = false
  }
}

const toggleCouponStatus = async (coupon) => {
  try {
    await adminStore.toggleCouponStatus(coupon.id)
    coupon.isActive = !coupon.isActive
    notificationService.success('Thành công',`Đã ${coupon.isActive ? 'kích hoạt' : 'vô hiệu hóa'} mã giảm giá "${coupon.code}" thành công!`)
  } catch (error) {
    logger.error('Lỗi toggle status:', error)
    notificationService.apiError(error, 'Không thể thay đổi trạng thái')
  }
}

const deleteCoupon = async (coupon) => {
  if (!confirm(`Bạn có chắc muốn xóa mã "${coupon.code}"? Hành động này không thể hoàn tác!`)) return
  
  try {
    await adminStore.deleteCoupon(coupon.id)
    notificationService.success('Thành công',`Đã xóa mã giảm giá "${coupon.code}" thành công!`)
    fetchCoupons()
  } catch (error) {
    logger.error('Lỗi xóa coupon:', error)
    notificationService.apiError(error, 'Không thể xóa mã giảm giá')
  }
}

const copyCouponCode = async (code) => {
  try {
    await navigator.clipboard.writeText(code)
    notificationService.success('Thành công', `Đã sao chép mã: ${code}`, { duration: 2000 })
  } catch (error) {
    logger.error('Lỗi copy:', error)
    notificationService.apiError(error, 'Không thể sao chép mã')
  }
}

const closeDialog = () => {
  showCreateDialog.value = false
  editingCoupon.value = null
  Object.assign(formData, {
    code: '',
    description: '',
    discountType: '',
    value: 0,
    maxDiscountAmount: null,
    minOrderAmount: null,
    startAt: '',
    endAt: '',
    maxUses: null,
    maxUsesPerUser: 1,
    isActive: true
  })
}

const goToPage = (page) => {
  currentPage.value = page
  fetchCoupons()
}

const getCouponStatusClass = (coupon) => {
  const now = new Date()
  const start = new Date(coupon.startAt)
  const end = new Date(coupon.endAt)
  
  if (end < now) return 'status-expired'
  if (start > now) return 'status-upcoming'
  if (!coupon.isActive) return 'status-inactive'
  if (coupon.maxUses && coupon.usesCount >= coupon.maxUses) return 'status-full'
  return 'status-active'
}

const getCouponStatusText = (coupon) => {
  const now = new Date()
  const start = new Date(coupon.startAt)
  const end = new Date(coupon.endAt)
  
  if (end < now) return 'Đã hết hạn'
  if (start > now) return 'Chưa bắt đầu'
  if (!coupon.isActive) return 'Vô hiệu hóa'
  if (coupon.maxUses && coupon.usesCount >= coupon.maxUses) return 'Đã hết lượt'
  return 'Đang hoạt động'
}

const getUsagePercentage = (coupon) => {
  if (!coupon.maxUses) return 0
  return Math.min((coupon.usesCount / coupon.maxUses) * 100, 100)
}

// formatPrice và formatDate đã được import từ @/utils/formatters

// Export functions
const handleExport = (format) => {
  const data = coupons.value.map(coupon => ({
    'Mã giảm giá': coupon.code,
    'Mô tả': coupon.description || '',
    'Loại giảm giá': coupon.discountType === 'PERCENTAGE' ? 'Phần trăm' : 'Cố định',
    'Giá trị': coupon.value,
    'Số lần sử dụng': coupon.usedCount || 0,
    'Giới hạn sử dụng': coupon.maxUses || 'Không giới hạn',
    'Ngày bắt đầu': formatDate(coupon.startAt),
    'Ngày kết thúc': formatDate(coupon.endAt),
    'Trạng thái': coupon.isActive ? 'Hoạt động' : 'Ngừng hoạt động'
  }))
  
  if (format === 'csv') {
    downloadCsv(data, `discounts_${Date.now()}.csv`)
    notificationService.success('Thành công','Đã xuất file CSV thành công!')
  } else if (format === 'json') {
    downloadJson(data, `discounts_${Date.now()}.json`)
    notificationService.success('Thành công','Đã xuất file JSON thành công!')
  }
}

// Lifecycle
onMounted(() => {
  fetchCoupons()
})
</script>





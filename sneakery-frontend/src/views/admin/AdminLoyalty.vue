<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">loyalty</i>
            Quản lý điểm thưởng
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý hệ thống điểm thưởng và chương trình khách hàng VIP</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="exportLoyalty('csv')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_download</i>
            CSV
          </button>
          <button @click="exportLoyalty('json')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">code</i>
            JSON
          </button>
          <button @click="openSettingsModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">settings</i>
            Cài đặt
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">stars</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(totalPointsEarned) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng điểm đã phát</p>
          <div class="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
            <i class="material-icons text-sm">trending_up</i>
            <span>+{{ formatNumber(pointsThisMonth) }} tháng này</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">redeem</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(totalPointsRedeemed) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Điểm đã sử dụng</p>
          <div class="flex items-center gap-1 text-xs text-purple-600 dark:text-purple-400">
            <i class="material-icons text-sm">shopping_cart</i>
            <span>{{ Math.round((totalPointsRedeemed / totalPointsEarned) * 100) || 0 }}% tỷ lệ sử dụng</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-pink-500 to-pink-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">schedule</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(expiringPoints) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Điểm sắp hết hạn</p>
          <div class="flex items-center gap-1 text-xs text-yellow-600 dark:text-yellow-400">
            <i class="material-icons text-sm">warning</i>
            <span>Trong 30 ngày tới</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">diamond</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ vipCustomers }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Khách hàng VIP</p>
          <div class="flex items-center gap-1 text-xs text-blue-600 dark:text-blue-400">
            <i class="material-icons text-sm">trending_up</i>
            <span>+{{ newVipThisMonth }} tháng này</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">search</i>
            Tìm kiếm
          </label>
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
            <input 
              type="text" 
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              v-model="searchKeyword"
              placeholder="Tìm theo tên khách hàng, email..."
            />
            <button v-if="searchKeyword" @click="clearSearch" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>
        
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">filter_list</i>
            Loại giao dịch
          </label>
          <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterType">
            <option value="all">Tất cả</option>
            <option value="earn">Tích điểm</option>
            <option value="redeem">Đổi điểm</option>
            <option value="expire">Hết hạn</option>
          </select>
        </div>

        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">diamond</i>
            Cấp độ
          </label>
          <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterLevel">
            <option value="all">Tất cả</option>
            <option value="bronze">Đồng</option>
            <option value="silver">Bạc</option>
            <option value="gold">Vàng</option>
            <option value="platinum">Bạch kim</option>
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

    <!-- Loyalty Points Table -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">Lịch sử điểm thưởng</h3>
        <span class="text-sm text-gray-500 dark:text-gray-400">{{ filteredPoints.length }} giao dịch</span>
      </div>

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

      <div v-else-if="filteredPoints.length === 0" class="flex flex-col items-center justify-center p-12">
        <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">loyalty</i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Không có giao dịch nào</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 text-center">Chưa có giao dịch điểm thưởng nào được tìm thấy</p>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Khách hàng</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Loại giao dịch</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Số điểm</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Nguồn</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Cấp độ</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Thời gian</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Hết hạn</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="point in paginatedPoints" :key="point.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td class="px-4 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-gray-100 dark:bg-gray-700 flex items-center justify-center flex-shrink-0 overflow-hidden">
                    <img v-if="point.customerAvatar" :src="point.customerAvatar" :alt="point.customerName" class="w-full h-full object-cover">
                    <i v-else class="material-icons text-gray-400 dark:text-gray-500">person</i>
                  </div>
                  <div class="min-w-0">
                    <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ point.customerName }}</div>
                    <div class="text-xs text-gray-500 dark:text-gray-400 truncate">{{ point.customerEmail }}</div>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span class="inline-flex items-center gap-1.5 px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': point.transactionType === 'earn',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': point.transactionType === 'redeem',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': point.transactionType === 'expire'
                  }"
                >
                  <i class="material-icons text-sm">{{ getTransactionIcon(point.transactionType) }}</i>
                  {{ getTransactionText(point.transactionType) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span 
                  class="text-sm font-semibold"
                  :class="point.transactionType === 'earn' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'"
                >
                  {{ point.transactionType === 'earn' ? '+' : '-' }}{{ formatNumber(Math.abs(point.points)) }}
                </span>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm text-gray-700 dark:text-gray-300">{{ getSourceText(point.earnedFromOrderId, point.redeemedInOrderId) }}</div>
                <div v-if="point.description" class="text-xs text-gray-500 dark:text-gray-400 truncate max-w-xs">{{ point.description }}</div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span class="inline-flex items-center gap-1.5 px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400': point.customerLevel === 'bronze',
                    'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400': point.customerLevel === 'silver',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': point.customerLevel === 'gold',
                    'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400': point.customerLevel === 'platinum',
                    'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400': !point.customerLevel || point.customerLevel === 'regular'
                  }"
                >
                  <i class="material-icons text-sm">{{ getLevelIcon(point.customerLevel) }}</i>
                  {{ getLevelText(point.customerLevel) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-400">
                {{ formatDateTime(point.createdAt) }}
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div v-if="point.expiresAt" class="space-y-1">
                  <div class="text-sm text-gray-700 dark:text-gray-300">{{ formatDate(point.expiresAt) }}</div>
                  <div 
                    class="text-xs px-2 py-0.5 rounded-full inline-block"
                    :class="{
                      'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': getExpiryStatus(point.expiresAt) === 'expired',
                      'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': getExpiryStatus(point.expiresAt) === 'expiring-soon',
                      'bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400': getExpiryStatus(point.expiresAt) === 'expiring',
                      'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': getExpiryStatus(point.expiresAt) === 'valid'
                    }"
                  >
                    {{ getExpiryText(point.expiresAt) }}
                  </div>
                </div>
                <span v-else class="text-sm text-gray-400 dark:text-gray-500">Không hết hạn</span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-1">
                  <button @click="viewPointDetail(point)" class="p-1.5 text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors" title="Xem chi tiết">
                    <i class="material-icons text-base">visibility</i>
                  </button>
                  <button v-if="point.transactionType === 'earn' && canExpire(point.expiresAt)" @click="extendExpiry(point)" class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors" title="Gia hạn">
                    <i class="material-icons text-base">schedule</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex items-center justify-center gap-4 mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
        <button 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 0"
          class="p-2 text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <i class="material-icons text-base">chevron_left</i>
        </button>
        
        <span class="text-sm text-gray-700 dark:text-gray-300">
          Trang {{ currentPage + 1 }} / {{ totalPages }}
        </span>
        
        <button 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage === totalPages - 1"
          class="p-2 text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <i class="material-icons text-base">chevron_right</i>
        </button>
      </div>
    </div>

    <!-- Settings Modal -->
    <div v-if="showSettingsModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeSettingsModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">settings</i>
            Cài đặt hệ thống điểm thưởng
          </h3>
          <button @click="closeSettingsModal" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4 space-y-4">
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tỷ lệ tích điểm (VND → Điểm)</label>
            <input 
              type="number" 
              v-model="settings.pointsPerVnd" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="1000"
            >
            <div class="text-xs text-gray-500 dark:text-gray-400">1 điểm = {{ settings.pointsPerVnd }} VND</div>
          </div>
          
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tỷ lệ đổi điểm (Điểm → VND)</label>
            <input 
              type="number" 
              v-model="settings.vndPerPoint" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="100"
            >
            <div class="text-xs text-gray-500 dark:text-gray-400">1 điểm = {{ settings.vndPerPoint }} VND</div>
          </div>
          
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Thời hạn điểm (ngày)</label>
            <input 
              type="number" 
              v-model="settings.pointsExpiryDays" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="365"
            >
            <div class="text-xs text-gray-500 dark:text-gray-400">Điểm sẽ hết hạn sau {{ settings.pointsExpiryDays }} ngày</div>
          </div>
          
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Điểm tối thiểu để đổi</label>
            <input 
              type="number" 
              v-model="settings.minRedeemPoints" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="1000"
            >
            <div class="text-xs text-gray-500 dark:text-gray-400">Khách hàng cần tối thiểu {{ settings.minRedeemPoints }} điểm để đổi</div>
          </div>
        </div>
        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="closeSettingsModal" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
            Hủy
          </button>
          <button @click="saveSettings" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200">
            Lưu cài đặt
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import notificationService from '@/utils/notificationService'
import confirmDialogService from '@/utils/confirmDialogService'
import { useAdminStore } from '@/stores/admin'
import logger from '@/utils/logger'
import LoadingSkeleton from '@/components/common/LoadingSkeleton.vue'
import { formatDate, formatDateTime } from '@/utils/formatters'

// Stores
const adminStore = useAdminStore()

// State
const loading = ref(false)
const points = ref([])
const searchKeyword = ref('')
const filterType = ref('all')
const filterLevel = ref('all')
const currentPage = ref(0)
const pageSize = ref(10)
const showSettingsModal = ref(false)
const settings = ref({
  pointsPerVnd: 1000,
  vndPerPoint: 100,
  pointsExpiryDays: 365,
  minRedeemPoints: 1000
})

// Mock data removed - using real API data

// Computed
const totalPointsEarned = computed(() => 
  (points.value || []).filter(p => p.transactionType === 'earn').reduce((sum, p) => sum + (p.points || 0), 0)
)
const totalPointsRedeemed = computed(() => 
  Math.abs((points.value || []).filter(p => p.transactionType === 'redeem').reduce((sum, p) => sum + (p.points || 0), 0))
)
const expiringPoints = computed(() => {
  const thirtyDaysFromNow = new Date()
  thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() + 30)
  
  return (points.value || [])
    .filter(p => p.transactionType === 'earn' && p.expiresAt && new Date(p.expiresAt) <= thirtyDaysFromNow)
    .reduce((sum, p) => sum + (p.points || 0), 0)
})
const pointsThisMonth = computed(() => {
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  
  return (points.value || [])
    .filter(p => {
      if (!p.createdAt) return false
      const pointDate = new Date(p.createdAt)
      return p.transactionType === 'earn' && 
             pointDate.getMonth() === thisMonth && 
             pointDate.getFullYear() === thisYear
    })
    .reduce((sum, p) => sum + (p.points || 0), 0)
})
const vipCustomers = computed(() => 
  new Set((points.value || []).filter(p => ['gold', 'platinum'].includes(p.customerLevel)).map(p => p.customerId)).size
)
const newVipThisMonth = computed(() => {
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  
  const vipThisMonth = (points.value || []).filter(p => {
    if (!p.createdAt) return false
    const pointDate = new Date(p.createdAt)
    return ['gold', 'platinum'].includes(p.customerLevel) && 
           pointDate.getMonth() === thisMonth && 
           pointDate.getFullYear() === thisYear
  })
  
  return new Set(vipThisMonth.map(p => p.customerId)).size
})

const filteredPoints = computed(() => {
  let filtered = points.value || []

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(p => 
      (p.customerName || '').toLowerCase().includes(keyword) ||
      (p.customerEmail || '').toLowerCase().includes(keyword)
    )
  }

  if (filterType.value !== 'all') {
    filtered = filtered.filter(p => p.transactionType === filterType.value)
  }

  if (filterLevel.value !== 'all') {
    filtered = filtered.filter(p => p.customerLevel === filterLevel.value)
  }

  return filtered
})

const totalPages = computed(() => Math.ceil(filteredPoints.value.length / pageSize.value))
const paginatedPoints = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return filteredPoints.value.slice(start, end)
})

// Methods
const fetchPoints = async () => {
  loading.value = true
  try {
    logger.log('🔍 Fetching loyalty users...')
    const result = await adminStore.fetchLoyaltyUsers(currentPage.value, pageSize.value, {})
    logger.log('📦 API Result:', result)
    
    const loyaltyDtos = result?.content || []
    logger.log('📊 Loyalty DTOs received:', loyaltyDtos.length, loyaltyDtos)
    
    // Map LoyaltyDto directly to points format
    points.value = loyaltyDtos.map(dto => ({
      id: dto.id,
      customerId: dto.userId,
      customerName: dto.userName || 'Không tên',
      customerEmail: dto.userEmail || 'N/A',
      customerAvatar: null, // DTO doesn't include avatar
      customerLevel: 'regular', // Default level
      points: dto.points || 0,
      transactionType: dto.transactionType || 'unknown',
      earnedFromOrderId: null, // Not in DTO
      redeemedInOrderId: null, // Not in DTO
      description: dto.description || '',
      expiresAt: dto.expiresAt,
      createdAt: dto.createdAt || new Date().toISOString()
    }))
    
    logger.log('✅ Points mapped:', points.value.length, 'items')
    logger.log('📊 Points sample:', points.value.slice(0, 3))
  } catch (error) {
    logger.error('❌ Error fetching loyalty:', error)
    notificationService.apiError(error, 'Không thể tải danh sách điểm thưởng')
  } finally {
    loading.value = false
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = 'all'
  filterLevel.value = 'all'
  currentPage.value = 0
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

const openSettingsModal = () => {
  showSettingsModal.value = true
}

const closeSettingsModal = () => {
  showSettingsModal.value = false
}

const saveSettings = () => {
  notificationService.success('Thành công','Đã lưu cài đặt thành công')
  closeSettingsModal()
}

const viewPointDetail = (point) => {
  notificationService.info('Thông tin',`Xem chi tiết giao dịch #${point.id}`)
}

const extendExpiry = async (point) => {
  try {
    await confirmDialogService.confirm(
      'Bạn có chắc chắn muốn gia hạn điểm này?',
      'Xác nhận gia hạn',
      {
        confirmButtonText: 'Gia hạn',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    // Simulate extend expiry
    const newExpiry = new Date()
    newExpiry.setFullYear(newExpiry.getFullYear() + 1)
    point.expiresAt = newExpiry.toISOString()
    
    notificationService.success('Thành công','Đã gia hạn điểm thành công')
  } catch {
    // User cancelled
  }
}

const exportLoyalty = (format) => {
  try {
    const dataToExport = filteredPoints.value || []
    if (dataToExport.length === 0) {
      notificationService.warning('Cảnh báo','Không có dữ liệu để xuất')
      return
    }
    
    const exportData = dataToExport.map(point => ({
      'ID': point.id,
      'Khách hàng': point.customerName,
      'Email': point.customerEmail,
      'Loại giao dịch': getTransactionText(point.transactionType),
      'Số điểm': point.points,
      'Nguồn': getSourceText(point.earnedFromOrderId, point.redeemedInOrderId),
      'Cấp độ': getLevelText(point.customerLevel),
      'Mô tả': point.description,
      'Thời gian': formatDateTime(point.createdAt),
      'Hết hạn': point.expiresAt ? formatDate(point.expiresAt) : 'Không hết hạn'
    }))

    if (format === 'csv') {
      downloadCsv(exportData, 'loyalty-points.csv')
      notificationService.success('Thành công','Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('loyalty-points', exportData)
      notificationService.success('Thành công','Xuất JSON thành công!')
    }
  } catch (error) {
    logger.error('Export error:', error)
    notificationService.apiError(error, 'Có lỗi xảy ra khi xuất dữ liệu')
  }
}

// Helper functions
const getTransactionIcon = (type) => {
  const icons = {
    earn: 'add_circle',
    redeem: 'remove_circle',
    expire: 'schedule'
  }
  return icons[type] || 'help'
}

const getTransactionText = (type) => {
  const texts = {
    earn: 'Tích điểm',
    redeem: 'Đổi điểm',
    expire: 'Hết hạn'
  }
  return texts[type] || type
}

const getSourceText = (earnedFrom, redeemedIn) => {
  if (earnedFrom) return `Đơn hàng #${earnedFrom}`
  if (redeemedIn) return `Đơn hàng #${redeemedIn}`
  return 'Hệ thống'
}

const getLevelIcon = (level) => {
  const icons = {
    bronze: 'looks_one',
    silver: 'looks_two',
    gold: 'looks_3',
    platinum: 'diamond'
  }
  return icons[level] || 'help'
}

const getLevelText = (level) => {
  const texts = {
    bronze: 'Đồng',
    silver: 'Bạc',
    gold: 'Vàng',
    platinum: 'Bạch kim'
  }
  return texts[level] || level
}

const getExpiryStatus = (expiresAt) => {
  if (!expiresAt) return 'no-expiry'
  
  const now = new Date()
  const expiry = new Date(expiresAt)
  const daysLeft = Math.ceil((expiry - now) / (1000 * 60 * 60 * 24))
  
  if (daysLeft < 0) return 'expired'
  if (daysLeft <= 7) return 'expiring-soon'
  if (daysLeft <= 30) return 'expiring'
  return 'valid'
}

const getExpiryText = (expiresAt) => {
  if (!expiresAt) return 'Không hết hạn'
  
  const now = new Date()
  const expiry = new Date(expiresAt)
  const daysLeft = Math.ceil((expiry - now) / (1000 * 60 * 60 * 24))
  
  if (daysLeft < 0) return 'Đã hết hạn'
  if (daysLeft === 0) return 'Hết hạn hôm nay'
  if (daysLeft === 1) return 'Hết hạn ngày mai'
  return `Còn ${daysLeft} ngày`
}

const canExpire = (expiresAt) => {
  return expiresAt && new Date(expiresAt) > new Date()
}

const formatNumber = (num) => {
  return new Intl.NumberFormat('vi-VN').format(num)
}

// formatDate và formatDateTime đã được import từ @/utils/formatters

// Lifecycle
onMounted(() => {
  fetchPoints()
})
</script>




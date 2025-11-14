<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">star_rate</i>
            Quản lý Đánh giá
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý đánh giá sản phẩm từ khách hàng</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="exportReviews" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">download</i>
            Xuất Excel
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ approvedReviewsCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đã duyệt</p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">pending</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ pendingReviewsCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Chờ duyệt</p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">star</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ averageRating.toFixed(1) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đánh giá trung bình</p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">rate_review</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ totalReviews }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng đánh giá</p>
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
              v-model="searchKeyword"
              @input="debounceSearch"
              type="text"
              placeholder="Tìm theo sản phẩm, khách hàng..."
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
            <button 
              v-if="searchKeyword" 
              @click="clearSearch" 
              class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
              title="Xóa tìm kiếm"
            >
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>
        
        <div class="flex items-center gap-2">
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
              <i class="material-icons text-sm">filter_list</i>
              Trạng thái
            </label>
            <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterStatus" @change="applyFilters">
              <option value="all">Tất cả</option>
              <option value="pending">Chờ duyệt</option>
              <option value="approved">Đã duyệt</option>
            </select>
          </div>

          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
              <i class="material-icons text-sm">star</i>
              Đánh giá
            </label>
            <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterRating" @change="applyFilters">
              <option value="all">Tất cả</option>
              <option value="5">5 sao</option>
              <option value="4">4 sao</option>
              <option value="3">3 sao</option>
              <option value="2">2 sao</option>
              <option value="1">1 sao</option>
            </select>
          </div>

          <button class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium mt-6" @click="resetFilters">
            <i class="material-icons text-base">refresh</i>
            Reset
          </button>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
      <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải dữ liệu...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredReviews.length === 0" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">rate_review</i>
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Không có đánh giá nào</h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 text-center">Chưa có đánh giá nào từ khách hàng</p>
    </div>

    <!-- Reviews List -->
    <div v-else class="space-y-4">
      <div v-for="review in paginatedReviews" :key="review.id" class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4 mb-4">
          <div class="flex items-start gap-3">
            <img 
              :src="review.productImage" 
              :alt="review.productName"
              class="w-16 h-16 object-cover rounded-lg border border-gray-200 dark:border-gray-700"
              loading="lazy"
              decoding="async"
              @error="handleImageError"
            />
            <div>
              <h4 class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-1">{{ review.productName }}</h4>
              <div class="flex items-center gap-1">
                <i 
                  v-for="star in 5" 
                  :key="star"
                  class="material-icons text-sm"
                  :class="star <= review.rating ? 'text-yellow-400' : 'text-gray-300 dark:text-gray-600'"
                >
                  {{ star <= review.rating ? 'star' : 'star_border' }}
                </i>
                <span class="text-xs text-gray-600 dark:text-gray-400 ml-1">({{ review.rating }}/5)</span>
              </div>
            </div>
          </div>
          
          <div class="flex items-center gap-2 flex-shrink-0">
            <span 
              class="px-2 py-1 text-xs font-medium rounded-full flex items-center gap-1"
              :class="review.isApproved ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'"
            >
              <i class="material-icons text-sm">{{ review.isApproved ? 'check_circle' : 'pending' }}</i>
              {{ review.isApproved ? 'Đã duyệt' : 'Chờ duyệt' }}
            </span>
            <span v-if="review.isVerifiedPurchase" class="px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400 flex items-center gap-1">
              <i class="material-icons text-sm">verified</i>
              Đã mua hàng
            </span>
          </div>
        </div>

        <div class="space-y-3 mb-4">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
              <i class="material-icons text-purple-600 dark:text-purple-400">account_circle</i>
            </div>
            <div>
              <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ review.userName }}</div>
              <div class="text-xs text-gray-500 dark:text-gray-400">{{ formatDate(review.createdAt) }}</div>
            </div>
          </div>

          <div>
            <h5 v-if="review.title" class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2">{{ review.title }}</h5>
            <p class="text-sm text-gray-700 dark:text-gray-300 leading-relaxed">{{ review.body }}</p>
            
            <div v-if="review.images && review.images.length > 0" class="flex gap-2 mt-3">
              <img 
                v-for="(image, index) in review.images" 
                :key="index"
                :src="image" 
                :alt="`Review image ${index + 1}`"
                class="w-20 h-20 object-cover rounded-lg border border-gray-200 dark:border-gray-700 cursor-pointer hover:opacity-80 transition-opacity"
                loading="lazy"
                decoding="async"
                @error="handleImageError"
              />
            </div>
          </div>

          <div v-if="review.replyText" class="p-3 bg-purple-50 dark:bg-purple-900/20 rounded-lg border-l-4 border-purple-500">
            <div class="flex items-center gap-2 mb-2">
              <i class="material-icons text-purple-600 dark:text-purple-400 text-sm">reply</i>
              <strong class="text-sm text-gray-900 dark:text-gray-100">Phản hồi từ Admin</strong>
              <span class="text-xs text-gray-500 dark:text-gray-400 ml-auto">{{ formatDate(review.repliedAt) }}</span>
            </div>
            <p class="text-sm text-gray-700 dark:text-gray-300">{{ review.replyText }}</p>
          </div>
        </div>

        <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
          <div class="flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400">
            <span class="flex items-center gap-1">
              <i class="material-icons text-sm">thumb_up</i>
              {{ review.helpfulCount }} hữu ích
            </span>
            <span class="flex items-center gap-1">
              <i class="material-icons text-sm">thumb_down</i>
              {{ review.unhelpfulCount }}
            </span>
          </div>
          
          <div class="flex items-center gap-2">
            <button 
              v-if="!review.isApproved" 
              class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-green-700 dark:text-green-400 bg-green-50 dark:bg-green-900/20 rounded-lg hover:bg-green-100 dark:hover:bg-green-900/30 transition-colors"
              @click="approveReview(review)"
              title="Duyệt đánh giá"
            >
              <i class="material-icons text-sm">check</i>
              Duyệt
            </button>
            <button 
              class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-blue-700 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20 rounded-lg hover:bg-blue-100 dark:hover:bg-blue-900/30 transition-colors"
              @click="openReplyModal(review)"
              title="Phản hồi"
            >
              <i class="material-icons text-sm">reply</i>
              {{ review.replyText ? 'Sửa phản hồi' : 'Phản hồi' }}
            </button>
            <button 
              class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-red-700 dark:text-red-400 bg-red-50 dark:bg-red-900/20 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/30 transition-colors"
              @click="confirmDelete(review)"
              title="Xóa đánh giá"
            >
              <i class="material-icons text-sm">delete</i>
              Xóa
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1 && !loading && filteredReviews.length > 0" class="flex flex-col sm:flex-row items-center justify-between gap-4 px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="text-sm text-gray-600 dark:text-gray-400">
        Hiển thị {{ (currentPage - 1) * itemsPerPage + 1 }} - {{ Math.min(currentPage * itemsPerPage, filteredReviews.length) }} 
        trong tổng số {{ filteredReviews.length }} đánh giá
      </div>
      <div class="flex items-center gap-2">
        <button 
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="currentPage === 1"
          @click="currentPage--"
        >
          <i class="material-icons text-base">chevron_left</i>
          Trước
        </button>
        
        <span class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300">Trang {{ currentPage }} / {{ totalPages }}</span>
        
        <button 
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="currentPage === totalPages"
          @click="currentPage++"
        >
          Sau
          <i class="material-icons text-base">chevron_right</i>
        </button>
      </div>
    </div>

    <!-- Reply Modal -->
    <div v-if="showReplyModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeReplyModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">reply</i>
            Phản hồi đánh giá
          </h2>
          <button class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors" @click="closeReplyModal">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        
        <div class="p-4">
          <div class="p-3 mb-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
            <div class="flex items-center gap-1 mb-2">
              <i 
                v-for="star in 5" 
                :key="star"
                class="material-icons text-sm"
                :class="star <= selectedReview?.rating ? 'text-yellow-400' : 'text-gray-300 dark:text-gray-600'"
              >
                {{ star <= selectedReview?.rating ? 'star' : 'star_border' }}
              </i>
            </div>
            <p class="text-sm text-gray-700 dark:text-gray-300"><strong>{{ selectedReview?.userName }}</strong>: {{ selectedReview?.body }}</p>
          </div>

          <form @submit.prevent="saveReply" class="space-y-4">
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Phản hồi của bạn *</label>
              <textarea 
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
                v-model="replyText"
                placeholder="Nhập phản hồi cho khách hàng..."
                rows="5"
                required
              ></textarea>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
              <button type="button" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" @click="closeReplyModal">
                <i class="material-icons text-base">close</i>
                Hủy
              </button>
              <button type="submit" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed" :disabled="saving">
                <i class="material-icons text-base" :class="{ 'animate-spin': saving }">{{ saving ? 'hourglass_empty' : 'send' }}</i>
                {{ saving ? 'Đang gửi...' : 'Gửi phản hồi' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
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
        
        <div class="p-4">
          <p class="text-sm text-gray-700 dark:text-gray-300 mb-2">Bạn có chắc chắn muốn xóa đánh giá này?</p>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-4">Hành động này không thể hoàn tác.</p>
          
          <div class="flex items-center justify-end gap-3">
            <button class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" @click="showDeleteModal = false">
              <i class="material-icons text-base">close</i>
              Hủy
            </button>
            <button class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-red-500 rounded-lg hover:bg-red-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed" @click="deleteReview" :disabled="deleting">
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
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { debounce } from '@/utils/debounce'
import logger from '@/utils/logger'
import { formatDate } from '@/utils/formatters'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('all')
const filterRating = ref('all')
const showReplyModal = ref(false)
const showDeleteModal = ref(false)
const selectedReview = ref(null)
const reviewToDelete = ref(null)
const replyText = ref('')
const currentPage = ref(1)
const itemsPerPage = 5

// Reviews data
const reviews = ref([])
const totalReviews = ref(0)

// Computed
const filteredReviews = computed(() => {
  let result = reviews.value || []

  // Filter by search
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(review => 
      review.productName?.toLowerCase().includes(keyword) ||
      review.userName?.toLowerCase().includes(keyword) ||
      review.body?.toLowerCase().includes(keyword)
    )
  }

  // Filter by status
  if (filterStatus.value !== 'all') {
    const isApproved = filterStatus.value === 'approved'
    result = result.filter(review => review.isApproved === isApproved)
  }

  // Filter by rating
  if (filterRating.value !== 'all') {
    const rating = parseInt(filterRating.value)
    result = result.filter(review => review.rating === rating)
  }

  return result
})

const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredReviews.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredReviews.value.length / itemsPerPage)
})

const approvedReviewsCount = computed(() => {
  return reviews.value.filter(r => r.isApproved).length
})

const pendingReviewsCount = computed(() => {
  return reviews.value.filter(r => !r.isApproved).length
})

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0)
  return sum / reviews.value.length
})

// Methods
const loadReviews = async () => {
  try {
    loading.value = true
    
    // Load from API - chỉ dùng dữ liệu thật từ database
    const apiFilters = {}
    
    if (searchKeyword.value) {
      apiFilters.search = searchKeyword.value
    }
    
    if (filterStatus.value !== 'all') {
      apiFilters.isApproved = filterStatus.value === 'approved'
    }
    
    const result = await adminStore.fetchReviews(0, 50, apiFilters)
    reviews.value = result.content || []
    totalReviews.value = result.totalElements || 0
    
    if (reviews.value.length === 0) {
      notificationService.info('Thông tin','Chưa có đánh giá nào')
    } else {
      logger.log('✅ Reviews loaded from API:', reviews.value.length, 'reviews')
    }
  } catch (error) {
    logger.error('Error loading reviews:', error)
    notificationService.apiError(error, 'Lỗi khi tải danh sách đánh giá')
    reviews.value = []
    totalReviews.value = 0
  } finally {
    loading.value = false
  }
}

const approveReview = async (review) => {
  try {
    await adminStore.approveReview(review.id)
    review.isApproved = true
    notificationService.success('Thành công','Đã duyệt đánh giá thành công!')
  } catch (error) {
    logger.error('Error approving review:', error)
    notificationService.apiError(error, 'Lỗi khi duyệt đánh giá')
  }
}

const openReplyModal = (review) => {
  selectedReview.value = review
  replyText.value = review.replyText || ''
  showReplyModal.value = true
}

const closeReplyModal = () => {
  showReplyModal.value = false
  selectedReview.value = null
  replyText.value = ''
}

const saveReply = async () => {
  saving.value = true
  try {
    await adminStore.replyToReview(selectedReview.value.id, replyText.value)
    selectedReview.value.replyText = replyText.value
    selectedReview.value.repliedAt = new Date().toISOString()
    closeReplyModal()
    notificationService.success('Thành công','Phản hồi đã được gửi thành công!')
  } catch (error) {
    logger.error('Error saving reply:', error)
    notificationService.apiError(error, 'Lỗi khi gửi phản hồi')
  } finally {
    saving.value = false
  }
}

const confirmDelete = (review) => {
  reviewToDelete.value = review
  showDeleteModal.value = true
}

const deleteReview = async () => {
  deleting.value = true
  try {
    await adminStore.deleteReview(reviewToDelete.value.id)
    const index = reviews.value.findIndex(r => r.id === reviewToDelete.value.id)
    if (index > -1) {
      reviews.value.splice(index, 1)
    }
    showDeleteModal.value = false
    reviewToDelete.value = null
    notificationService.success('Thành công','Xóa đánh giá thành công!')
  } catch (error) {
    logger.error('Error deleting review:', error)
    notificationService.apiError(error, 'Lỗi khi xóa đánh giá')
  } finally {
    deleting.value = false
  }
}

const debounceSearch = debounce(() => {
  currentPage.value = 1 // Reset về trang đầu khi search
  loadReviews()
}, 500)

const clearSearch = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  loadReviews()
}

const applyFilters = () => {
  currentPage.value = 1 // Reset về trang đầu khi filter
  loadReviews()
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = 'all'
  filterRating.value = 'all'
  currentPage.value = 1
  loadReviews() // Gọi lại loadReviews sau khi reset
}

// formatDate đã được import từ @/utils/formatters

const exportReviews = () => {
  try {
    const dataToExport = filteredReviews.value || []
    if (dataToExport.length === 0) {
      notificationService.warning('Cảnh báo','Không có dữ liệu để xuất')
      return
    }
    
    const exportData = dataToExport.map(review => ({
      'ID': review.id,
      'Sản phẩm': review.productName,
      'Khách hàng': review.userName,
      'Email': review.userEmail || 'N/A',
      'Đánh giá': review.rating,
      'Tiêu đề': review.title || 'N/A',
      'Nội dung': review.body || 'N/A',
      'Trạng thái': review.isApproved ? 'Đã duyệt' : 'Chờ duyệt',
      'Xác minh': review.isVerifiedPurchase ? 'Đã xác minh' : 'Chưa xác minh',
      'Ngày tạo': formatDate(review.createdAt)
    }))
    
    downloadCsv(exportData, 'reviews.csv')
    notificationService.success('Thành công','Xuất CSV thành công!')
  } catch (error) {
    logger.error('Export error:', error)
    notificationService.apiError(error, 'Có lỗi xảy ra khi xuất dữ liệu')
  }
}

const handleImageError = (e) => {
  e.target.src = '/placeholder-image.png'
}

// Lifecycle
onMounted(() => {
  loadReviews()
})
</script>





<template>
  <div class="admin-page admin-reviews">
    <!-- ===== PAGE HEADER ===== -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">star_rate</span>
            Quản lý Đánh giá
          </h1>
          <p class="page-subtitle">Quản lý đánh giá sản phẩm từ khách hàng</p>
        </div>
        <div class="header-actions">
          <button @click="exportReviews" class="btn btn-secondary">
            <span class="material-icons">download</span>
            Xuất Excel
          </button>
        </div>
      </div>
    </div>

    <!-- ===== STATS GRID ===== -->
    <div class="stats-grid">
      <StatsCard
        icon="check_circle"
        :value="approvedReviewsCount"
        label="Đã duyệt"
        variant="success"
      />
      
      <StatsCard
        icon="pending"
        :value="pendingReviewsCount"
        label="Chờ duyệt"
        variant="warning"
      />
      
      <StatsCard
        icon="star"
        :value="averageRating.toFixed(1)"
        label="Đánh giá trung bình"
        variant="info"
      />
      
      <StatsCard
        icon="rate_review"
        :value="totalReviews"
        label="Tổng đánh giá"
        variant="primary"
      />
    </div>

    <!-- ===== FILTERS ===== -->
    <FilterBar
      v-model:search="searchKeyword"
      search-placeholder="Tìm theo sản phẩm, khách hàng..."
      @search="handleSearch"
      @reset="resetFilters"
    >
      <template #filters>
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">filter_list</span>
            Trạng thái
          </label>
          <select class="form-control" v-model="filterStatus" @change="handleSearch">
            <option value="all">Tất cả</option>
            <option value="pending">Chờ duyệt</option>
            <option value="approved">Đã duyệt</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">star</span>
            Đánh giá
          </label>
          <select class="form-control" v-model="filterRating" @change="handleSearch">
            <option value="all">Tất cả</option>
            <option value="5">5 sao</option>
            <option value="4">4 sao</option>
            <option value="3">3 sao</option>
            <option value="2">2 sao</option>
            <option value="1">1 sao</option>
          </select>
        </div>
      </template>
    </FilterBar>

    <!-- ===== LOADING STATE ===== -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">Đang tải dữ liệu...</p>
    </div>

    <!-- ===== EMPTY STATE ===== -->
    <EmptyState
      v-else-if="filteredReviews.length === 0"
      icon="rate_review"
      title="Không có đánh giá nào"
      description="Chưa có đánh giá nào từ khách hàng"
    />

    <!-- ===== REVIEWS LIST ===== -->
    <div v-else class="reviews-list">
      <div v-for="review in paginatedReviews" :key="review.id" class="review-card">
        <div class="review-header">
          <div class="review-product">
            <img 
              :src="review.productImage" 
              :alt="review.productName"
              class="product-thumb"
              @error="handleImageError"
            />
            <div class="product-info">
              <h4 class="product-name">{{ review.productName }}</h4>
              <div class="review-rating">
                <span 
                  v-for="star in 5" 
                  :key="star"
                  class="material-icons star-icon"
                  :class="star <= review.rating ? 'star-filled' : 'star-empty'"
                >
                  {{ star <= review.rating ? 'star' : 'star_border' }}
                </span>
                <span class="rating-text">({{ review.rating }}/5)</span>
              </div>
            </div>
          </div>
          
          <div class="review-status">
            <span 
              class="status-badge" 
              :class="review.isApproved ? 'status-approved' : 'status-pending'"
            >
              <span class="material-icons">{{ review.isApproved ? 'check_circle' : 'pending' }}</span>
              {{ review.isApproved ? 'Đã duyệt' : 'Chờ duyệt' }}
            </span>
            <span v-if="review.isVerifiedPurchase" class="verified-badge">
              <span class="material-icons">verified</span>
              Đã mua hàng
            </span>
          </div>
        </div>

        <div class="review-body">
          <div class="review-author">
            <div class="author-avatar">
              <span class="material-icons">account_circle</span>
            </div>
            <div class="author-info">
              <div class="author-name">{{ review.userName }}</div>
              <div class="review-date">{{ formatDate(review.createdAt) }}</div>
            </div>
          </div>

          <div class="review-content">
            <h5 v-if="review.title" class="review-title">{{ review.title }}</h5>
            <p class="review-text">{{ review.body }}</p>
            
            <div v-if="review.images && review.images.length > 0" class="review-images">
              <img 
                v-for="(image, index) in review.images" 
                :key="index"
                :src="image" 
                :alt="`Review image ${index + 1}`"
                class="review-image"
                @error="handleImageError"
              />
            </div>
          </div>

          <div v-if="review.replyText" class="admin-reply">
            <div class="reply-header">
              <span class="material-icons">reply</span>
              <strong>Phản hồi từ Admin</strong>
              <span class="reply-date">{{ formatDate(review.repliedAt) }}</span>
            </div>
            <p class="reply-text">{{ review.replyText }}</p>
          </div>
        </div>

        <div class="review-footer">
          <div class="review-helpful">
            <span class="material-icons">thumb_up</span>
            {{ review.helpfulCount }} hữu ích
            <span class="material-icons">thumb_down</span>
            {{ review.unhelpfulCount }}
          </div>
          
          <div class="review-actions">
            <button 
              v-if="!review.isApproved" 
              class="btn-action btn-approve"
              @click="approveReview(review)"
              title="Duyệt đánh giá"
            >
              <span class="material-icons">check</span>
              Duyệt
            </button>
            <button 
              class="btn-action btn-reply"
              @click="openReplyModal(review)"
              title="Phản hồi"
            >
              <span class="material-icons">reply</span>
              {{ review.replyText ? 'Sửa phản hồi' : 'Phản hồi' }}
            </button>
            <button 
              class="btn-action btn-delete"
              @click="confirmDelete(review)"
              title="Xóa đánh giá"
            >
              <span class="material-icons">delete</span>
              Xóa
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== PAGINATION ===== -->
    <div v-if="totalPages > 1" class="pagination-container">
      <div class="pagination-info">
        Hiển thị {{ (currentPage - 1) * itemsPerPage + 1 }} - {{ Math.min(currentPage * itemsPerPage, filteredReviews.length) }} 
        trong tổng số {{ filteredReviews.length }} đánh giá
      </div>
      <div class="pagination-controls">
        <button 
          class="pagination-btn" 
          :disabled="currentPage === 1"
          @click="currentPage--"
        >
          <span class="material-icons">chevron_left</span>
          Trước
        </button>
        
        <span class="page-info">Trang {{ currentPage }} / {{ totalPages }}</span>
        
        <button 
          class="pagination-btn" 
          :disabled="currentPage === totalPages"
          @click="currentPage++"
        >
          Sau
          <span class="material-icons">chevron_right</span>
        </button>
      </div>
    </div>

    <!-- ===== REPLY MODAL ===== -->
    <div v-if="showReplyModal" class="modal-overlay" @click="closeReplyModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons">reply</span>
            Phản hồi đánh giá
          </h2>
          <button class="modal-close" @click="closeReplyModal">
            <span class="material-icons">close</span>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="review-preview">
            <div class="preview-rating">
              <span 
                v-for="star in 5" 
                :key="star"
                class="material-icons star-icon"
                :class="star <= selectedReview?.rating ? 'star-filled' : 'star-empty'"
              >
                {{ star <= selectedReview?.rating ? 'star' : 'star_border' }}
              </span>
            </div>
            <p><strong>{{ selectedReview?.userName }}</strong>: {{ selectedReview?.body }}</p>
          </div>

          <form @submit.prevent="saveReply">
            <div class="form-group">
              <label class="form-label required">Phản hồi của bạn</label>
              <textarea 
                class="form-textarea" 
                v-model="replyText"
                placeholder="Nhập phản hồi cho khách hàng..."
                rows="5"
                required
              ></textarea>
            </div>

            <div class="modal-actions">
              <button type="button" class="btn btn-outline" @click="closeReplyModal">
                <span class="material-icons">close</span>
                Hủy
              </button>
              <button type="submit" class="btn btn-primary" :disabled="saving">
                <span class="material-icons">{{ saving ? 'hourglass_empty' : 'send' }}</span>
                {{ saving ? 'Đang gửi...' : 'Gửi phản hồi' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="modal-content modal-sm" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="material-icons text-danger">warning</span>
            Xác nhận xóa
          </h2>
          <button class="modal-close" @click="showDeleteModal = false">
            <span class="material-icons">close</span>
          </button>
        </div>
        
        <div class="modal-body">
          <p>Bạn có chắc chắn muốn xóa đánh giá này?</p>
          <p class="text-muted">Hành động này không thể hoàn tác.</p>
          
          <div class="modal-actions">
            <button class="btn btn-outline" @click="showDeleteModal = false">
              <span class="material-icons">close</span>
              Hủy
            </button>
            <button class="btn btn-danger" @click="deleteReview" :disabled="deleting">
              <span class="material-icons">{{ deleting ? 'hourglass_empty' : 'delete' }}</span>
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
import { ElMessage } from 'element-plus'
import StatsCard from '@/assets/components/admin/StatsCard.vue'
import FilterBar from '@/assets/components/admin/FilterBar.vue'
import EmptyState from '@/assets/components/admin/EmptyState.vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'

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

// Mock data (fallback only)
const mockReviews = ref([
  {
    id: 1,
    productId: 1,
    productName: 'Nike Air Force 1',
    productImage: '/placeholder-image.png',
    userId: 101,
    userName: 'Nguyễn Văn A',
    rating: 5,
    title: 'Sản phẩm tuyệt vời!',
    body: 'Giày rất đẹp, chất lượng tốt, đúng như mô tả. Giao hàng nhanh chóng. Rất hài lòng!',
    images: [],
    isApproved: true,
    isVerifiedPurchase: true,
    helpfulCount: 15,
    unhelpfulCount: 2,
    replyText: 'Cảm ơn bạn đã tin tưởng và ủng hộ shop! Chúc bạn sử dụng sản phẩm vui vẻ!',
    repliedAt: '2024-01-20T10:30:00',
    createdAt: '2024-01-15T14:20:00'
  },
  {
    id: 2,
    productId: 2,
    productName: 'Adidas Ultraboost',
    productImage: '/placeholder-image.png',
    userId: 102,
    userName: 'Trần Thị B',
    rating: 4,
    title: 'Tốt nhưng giá hơi cao',
    body: 'Giày êm chân, thiết kế đẹp. Tuy nhiên giá hơi cao so với mặt bằng chung.',
    images: [],
    isApproved: true,
    isVerifiedPurchase: true,
    helpfulCount: 8,
    unhelpfulCount: 1,
    replyText: null,
    repliedAt: null,
    createdAt: '2024-01-18T09:15:00'
  },
  {
    id: 3,
    productId: 3,
    productName: 'Converse Chuck Taylor',
    productImage: '/placeholder-image.png',
    userId: 103,
    userName: 'Lê Văn C',
    rating: 3,
    title: 'Bình thường',
    body: 'Sản phẩm ổn, không có gì đặc biệt. Size hơi nhỏ so với thông thường.',
    images: ['/placeholder-image.png'],
    isApproved: false,
    isVerifiedPurchase: false,
    helpfulCount: 3,
    unhelpfulCount: 0,
    replyText: null,
    repliedAt: null,
    createdAt: '2024-01-22T16:45:00'
  },
  {
    id: 4,
    productId: 1,
    productName: 'Nike Air Force 1',
    productImage: '/placeholder-image.png',
    userId: 104,
    userName: 'Phạm Thị D',
    rating: 5,
    title: 'Rất đáng mua!',
    body: 'Mình đã mua 3 đôi rồi, chất lượng luôn ổn định. Shop phục vụ tận tình.',
    images: ['/placeholder-image.png', '/placeholder-image.png'],
    isApproved: false,
    isVerifiedPurchase: true,
    helpfulCount: 0,
    unhelpfulCount: 0,
    replyText: null,
    repliedAt: null,
    createdAt: '2024-01-23T11:20:00'
  },
  {
    id: 5,
    productId: 4,
    productName: 'Puma Suede Classic',
    productImage: '/placeholder-image.png',
    userId: 105,
    userName: 'Hoàng Văn E',
    rating: 2,
    title: 'Không như mong đợi',
    body: 'Màu sắc không giống hình. Chất liệu cảm giác kém hơn bản gốc.',
    images: [],
    isApproved: true,
    isVerifiedPurchase: true,
    helpfulCount: 5,
    unhelpfulCount: 3,
    replyText: 'Xin lỗi bạn về trải nghiệm không tốt. Vui lòng liên hệ CSKH để được hỗ trợ đổi trả.',
    repliedAt: '2024-01-24T09:00:00',
    createdAt: '2024-01-23T20:30:00'
  }
])

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
    
    // Mock data cho reviews
    const mockReviews = [
      {
        id: 1,
        productId: 1,
        productName: 'Nike Air Force 1',
        productImage: '/placeholder-image.png',
        userId: 101,
        userName: 'Nguyễn Văn A',
        userEmail: 'nguyenvana@email.com',
        rating: 5,
        title: 'Sản phẩm tuyệt vời',
        body: 'Giày rất đẹp, chất lượng tốt. Tôi rất hài lòng với sản phẩm này.',
        isApproved: true,
        createdAt: '2024-01-15T10:30:00Z',
        images: ['/placeholder-image.png']
      },
      {
        id: 2,
        productId: 2,
        productName: 'Adidas Ultraboost',
        productImage: '/placeholder-image.png',
        userId: 102,
        userName: 'Trần Thị B',
        userEmail: 'tranthib@email.com',
        rating: 4,
        title: 'Tốt nhưng giá hơi cao',
        body: 'Giày êm chân, thiết kế đẹp. Tuy nhiên giá hơi cao so với mặt bằng chung.',
        isApproved: false,
        createdAt: '2024-01-14T14:20:00Z',
        images: ['/placeholder-image.png']
      },
      {
        id: 3,
        productId: 3,
        productName: 'Jordan 1 Retro',
        productImage: '/placeholder-image.png',
        userId: 103,
        userName: 'Lê Văn C',
        userEmail: 'levanc@email.com',
        rating: 5,
        title: 'Classic không bao giờ lỗi mốt',
        body: 'Jordan 1 là một trong những mẫu giày đẹp nhất mọi thời đại. Chất lượng tuyệt vời.',
        isApproved: true,
        createdAt: '2024-01-13T16:45:00Z',
        images: ['/placeholder-image.png', '/placeholder-image.png']
      },
      {
        id: 4,
        productId: 4,
        productName: 'Converse Chuck Taylor',
        productImage: '/placeholder-image.png',
        userId: 104,
        userName: 'Phạm Thị D',
        userEmail: 'phamthid@email.com',
        rating: 3,
        title: 'Bình thường',
        body: 'Giày ổn, nhưng đế hơi cứng. Cần thời gian để làm quen.',
        isApproved: false,
        createdAt: '2024-01-12T11:15:00Z',
        images: ['/placeholder-image.png']
      },
      {
        id: 5,
        productId: 5,
        productName: 'Vans Old Skool',
        productImage: '/placeholder-image.png',
        userId: 105,
        userName: 'Hoàng Văn E',
        userEmail: 'hoangvane@email.com',
        rating: 4,
        title: 'Thiết kế đẹp',
        body: 'Vans Old Skool có thiết kế rất đẹp và dễ phối đồ. Chất lượng tốt.',
        isApproved: true,
        createdAt: '2024-01-11T13:20:00Z',
        images: ['/placeholder-image.png']
      }
    ]
    
    // Load from API
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
    
    console.log('✅ Reviews loaded from API')
  } catch (error) {
    console.error('Error loading reviews:', error)
    ElMessage.error('Lỗi khi tải danh sách đánh giá')
  } finally {
    loading.value = false
  }
}

const approveReview = async (review) => {
  try {
    await adminStore.approveReview(review.id)
    review.isApproved = true
    ElMessage.success('Đã duyệt đánh giá thành công!')
  } catch (error) {
    console.error('Error approving review:', error)
    ElMessage.error('Lỗi khi duyệt đánh giá')
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
    ElMessage.success('Phản hồi đã được gửi thành công!')
  } catch (error) {
    console.error('Error saving reply:', error)
    ElMessage.error('Lỗi khi gửi phản hồi')
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
    ElMessage.success('Xóa đánh giá thành công!')
  } catch (error) {
    console.error('Error deleting review:', error)
    ElMessage.error('Lỗi khi xóa đánh giá')
  } finally {
    deleting.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadReviews()
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = 'all'
  filterRating.value = 'all'
  currentPage.value = 1
}

const formatDate = (dateString) => {
  if (!dateString) return '—'
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const exportReviews = () => {
  try {
    const dataToExport = filteredReviews.value || []
    if (dataToExport.length === 0) {
      ElMessage.warning('Không có dữ liệu để xuất')
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
    ElMessage.success('Xuất CSV thành công!')
  } catch (error) {
    console.error('Export error:', error)
    ElMessage.error('Có lỗi xảy ra khi xuất dữ liệu!')
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

<style scoped>
/* ═══════════════════════════════════════════════════════════════════════
   ADMIN REVIEWS - REVIEW-SPECIFIC STYLES ONLY
   All layout, stats, filters, tables, buttons, modals use Design System v2.0 global classes
   ═══════════════════════════════════════════════════════════════════════ */

/* Page header, stats, filters, loading, empty states, modals, buttons, pagination, responsive all use global classes */

/* ═══ REVIEWS LIST ═══ */
.reviews-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
  margin-bottom: var(--space-6);
}

.review-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  transition: all var(--transition-smooth);
}

.review-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
  background: var(--gradient-purple-soft);
}

.review-product {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.product-thumb {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  object-fit: cover;
  border: 1px solid var(--border-primary);
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: var(--text-base);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.star-icon {
  font-size: 18px;
}

.star-filled {
  color: #fbbf24;
}

.star-empty {
  color: var(--text-quaternary);
}

.rating-text {
  margin-left: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-semibold);
}

.review-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--space-2);
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-badge .material-icons {
  font-size: 16px;
}

.status-badge.status-approved {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.status-badge.status-pending {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.verified-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-2);
  background: var(--info-bg);
  color: var(--info-text);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  border: 1px solid var(--info-border);
}

.verified-badge .material-icons {
  font-size: 14px;
}

.review-body {
  padding: var(--space-6);
}

.review-author {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-4);
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--gradient-purple);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.author-avatar .material-icons {
  font-size: 32px;
  color: var(--color-white);
}

.author-info {
  flex: 1;
}

.author-name {
  font-size: var(--text-base);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.review-date {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.review-content {
  margin-top: var(--space-4);
}

.review-title {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.review-text {
  font-size: var(--text-base);
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 var(--space-4) 0;
}

.review-images {
  display: flex;
  gap: var(--space-3);
  flex-wrap: wrap;
}

.review-image {
  width: 120px;
  height: 120px;
  border-radius: var(--radius-md);
  object-fit: cover;
  border: 1px solid var(--border-primary);
  transition: all var(--transition-fast);
  cursor: pointer;
}

.review-image:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-glow-purple);
}

.admin-reply {
  margin-top: var(--space-4);
  padding: var(--space-4);
  background: var(--gradient-purple-soft);
  border-left: 3px solid var(--accent-primary);
  border-radius: var(--radius-md);
}

.reply-header {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-2);
  color: var(--accent-primary);
  font-size: var(--text-sm);
}

.reply-header .material-icons {
  font-size: 18px;
}

.reply-date {
  margin-left: auto;
  color: var(--text-tertiary);
  font-weight: var(--font-normal);
}

.reply-text {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  line-height: 1.6;
  margin: 0;
}

.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-6);
  border-top: 1px solid var(--border-primary);
  background: rgba(15, 23, 42, 0.2);
}

.review-helpful {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-tertiary);
}

.review-helpful .material-icons {
  font-size: 18px;
}

.review-actions {
  display: flex;
  gap: var(--space-2);
}

.btn-action {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-4);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-action .material-icons {
  font-size: 18px;
}

.btn-approve {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.btn-approve:hover {
  background: var(--success-solid);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.btn-reply {
  background: var(--info-bg);
  color: var(--info-text);
  border: 1px solid var(--info-border);
}

.btn-reply:hover {
  background: var(--info-solid);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-delete {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

.btn-delete:hover {
  background: var(--error-solid);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}
</style>


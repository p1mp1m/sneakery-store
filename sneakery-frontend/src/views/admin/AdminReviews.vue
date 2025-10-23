<template>
  <div class="admin-reviews">
    <!-- ===== PAGE HEADER ===== -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">star_rate</span>
            Quản lí Đánh giá
          </h1>
          <p class="page-subtitle">Quản lý đánh giá sản phẩm từ khách hàng</p>
        </div>
      </div>
    </div>

    <!-- ===== STATS GRID ===== -->
    <div class="stats-grid">
      <div class="stats-card success">
        <div class="stats-icon">
          <span class="material-icons">check_circle</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ approvedReviewsCount }}</div>
          <div class="stats-label">ĐÃ DUYỆT</div>
        </div>
      </div>
      
      <div class="stats-card warning">
        <div class="stats-icon">
          <span class="material-icons">pending</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ pendingReviewsCount }}</div>
          <div class="stats-label">CHỜ DUYỆT</div>
        </div>
      </div>
      
      <div class="stats-card info">
        <div class="stats-icon">
          <span class="material-icons">star</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ averageRating.toFixed(1) }}</div>
          <div class="stats-label">ĐÁNH GIÁ TRUNG BÌNH</div>
        </div>
      </div>
      
      <div class="stats-card primary">
        <div class="stats-icon">
          <span class="material-icons">rate_review</span>
        </div>
        <div class="stats-content">
          <div class="stats-value">{{ mockReviews.length }}</div>
          <div class="stats-label">TỔNG ĐÁNH GIÁ</div>
        </div>
      </div>
    </div>

    <!-- ===== FILTERS ===== -->
    <div class="filters-section">
      <div class="filter-row">
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">search</span>
            Tìm kiếm
          </label>
          <input 
            type="text" 
            class="filter-input" 
            v-model="searchKeyword"
            placeholder="Tìm theo sản phẩm, khách hàng..."
          />
        </div>
        
        <div class="filter-group">
          <label class="filter-label">
            <span class="material-icons">filter_list</span>
            Trạng thái
          </label>
          <select class="filter-select" v-model="filterStatus">
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
          <select class="filter-select" v-model="filterRating">
            <option value="all">Tất cả</option>
            <option value="5">5 sao</option>
            <option value="4">4 sao</option>
            <option value="3">3 sao</option>
            <option value="2">2 sao</option>
            <option value="1">1 sao</option>
          </select>
        </div>

        <div class="filter-group">
          <button class="btn btn-outline" @click="resetFilters">
            <span class="material-icons">refresh</span>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- ===== LOADING STATE ===== -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">Đang tải dữ liệu...</p>
    </div>

    <!-- ===== EMPTY STATE ===== -->
    <div v-else-if="filteredReviews.length === 0" class="empty-state">
      <span class="material-icons empty-icon">rate_review</span>
      <h3 class="empty-title">Không có đánh giá nào</h3>
      <p class="empty-description">Chưa có đánh giá nào từ khách hàng</p>
    </div>

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
      <button 
        class="pagination-btn" 
        :disabled="currentPage === 1"
        @click="currentPage--"
      >
        <span class="material-icons">chevron_left</span>
        Trước
      </button>
      
      <div class="pagination-info">
        Trang {{ currentPage }} / {{ totalPages }}
      </div>
      
      <button 
        class="pagination-btn" 
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >
        Sau
        <span class="material-icons">chevron_right</span>
      </button>
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

// Mock data - Replace with real API calls
const mockReviews = ref([
  {
    id: 1,
    productId: 1,
    productName: 'Nike Air Force 1',
    productImage: 'https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/b7d9211c-26e7-431a-ac24-b0540fb3c00f/air-force-1-07-shoes-WrLlWX.png',
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
    productImage: 'https://assets.adidas.com/images/w_600,f_auto,q_auto/1c7e3d5f5b1b4e7ba3b5af8d0126c1b7_9366/Ultraboost_Light_Shoes_White_GY9350_01_standard.jpg',
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
    productImage: 'https://www.converse.com/dw/image/v2/BCZC_PRD/on/demandware.static/-/Sites-cnv-master-catalog/default/dw3f7e9c3e/images/a_107/M9160_A_107X1.jpg',
    userId: 103,
    userName: 'Lê Văn C',
    rating: 3,
    title: 'Bình thường',
    body: 'Sản phẩm ổn, không có gì đặc biệt. Size hơi nhỏ so với thông thường.',
    images: ['https://via.placeholder.com/200'],
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
    productImage: 'https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/b7d9211c-26e7-431a-ac24-b0540fb3c00f/air-force-1-07-shoes-WrLlWX.png',
    userId: 104,
    userName: 'Phạm Thị D',
    rating: 5,
    title: 'Rất đáng mua!',
    body: 'Mình đã mua 3 đôi rồi, chất lượng luôn ổn định. Shop phục vụ tận tình.',
    images: ['https://via.placeholder.com/200', 'https://via.placeholder.com/200'],
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
    productImage: 'https://images.puma.com/image/upload/f_auto,q_auto,b_rgb:fafafa,w_600,h_600/global/374915/01/sv01/fnd/PNA/fmt/png/Suede-Classic-XXI-Sneakers',
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
  let result = mockReviews.value

  // Filter by search
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(review => 
      review.productName.toLowerCase().includes(keyword) ||
      review.userName.toLowerCase().includes(keyword) ||
      review.body.toLowerCase().includes(keyword)
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
  return mockReviews.value.filter(r => r.isApproved).length
})

const pendingReviewsCount = computed(() => {
  return mockReviews.value.filter(r => !r.isApproved).length
})

const averageRating = computed(() => {
  if (mockReviews.value.length === 0) return 0
  const sum = mockReviews.value.reduce((acc, r) => acc + r.rating, 0)
  return sum / mockReviews.value.length
})

// Methods
const approveReview = async (review) => {
  try {
    // TODO: Call API to approve review
    review.isApproved = true
    alert('Đã duyệt đánh giá thành công!')
  } catch (error) {
    console.error('Error approving review:', error)
    alert('Lỗi khi duyệt đánh giá')
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
    // TODO: Call API to save reply
    selectedReview.value.replyText = replyText.value
    selectedReview.value.repliedAt = new Date().toISOString()
    closeReplyModal()
    alert('Phản hồi đã được gửi thành công!')
  } catch (error) {
    console.error('Error saving reply:', error)
    alert('Lỗi khi gửi phản hồi')
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
    // TODO: Call API to delete review
    const index = mockReviews.value.findIndex(r => r.id === reviewToDelete.value.id)
    if (index > -1) {
      mockReviews.value.splice(index, 1)
    }
    showDeleteModal.value = false
    reviewToDelete.value = null
    alert('Xóa đánh giá thành công!')
  } catch (error) {
    console.error('Error deleting review:', error)
    alert('Lỗi khi xóa đánh giá')
  } finally {
    deleting.value = false
  }
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

const handleImageError = (e) => {
  e.target.src = 'https://via.placeholder.com/200?text=No+Image'
}

// Lifecycle
onMounted(() => {
  loading.value = true
  // TODO: Fetch reviews from API
  setTimeout(() => {
    loading.value = false
  }, 500)
})
</script>

<style scoped>
/* ═══ PAGE LAYOUT ═══ */
.admin-reviews {
  padding: var(--space-8);
  max-width: 1600px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* ═══ PAGE HEADER ═══ */
.page-header {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-6);
}

.title-section {
  flex: 1;
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
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.page-subtitle {
  color: var(--text-tertiary);
  margin: 0;
  font-size: var(--text-base);
}

/* ═══ STATS GRID ═══ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stats-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  gap: var(--space-4);
  position: relative;
  overflow: hidden;
  transition: all var(--transition-smooth);
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--gradient-primary);
  transform: scaleY(0);
  transform-origin: top;
  transition: transform var(--transition-smooth);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
}

.stats-card:hover::before {
  transform: scaleY(1);
}

.stats-card.success::before {
  background: var(--gradient-success);
}

.stats-card.warning::before {
  background: var(--gradient-warning);
}

.stats-card.info::before {
  background: var(--gradient-info);
}

.stats-card.primary::before {
  background: var(--gradient-primary);
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all var(--transition-smooth);
}

.stats-card.success .stats-icon {
  background: var(--success-bg);
  color: var(--success-text);
}

.stats-card.warning .stats-icon {
  background: var(--warning-bg);
  color: var(--warning-text);
}

.stats-card.info .stats-icon {
  background: var(--info-bg);
  color: var(--info-text);
}

.stats-card.primary .stats-icon {
  background: var(--gradient-purple-soft);
  color: var(--accent-primary);
}

.stats-card:hover .stats-icon {
  transform: scale(1.1) rotate(5deg);
}

.stats-icon .material-icons {
  font-size: 28px;
}

.stats-content {
  flex: 1;
}

.stats-value {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: var(--space-2);
}

.stats-label {
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* ═══ FILTERS ═══ */
.filters-section {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.filter-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: var(--space-4);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-label {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.filter-label .material-icons {
  font-size: 1rem;
  color: var(--accent-primary);
}

.filter-input,
.filter-select {
  padding: var(--space-3) var(--space-4);
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  background: rgba(15, 23, 42, 0.4);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

.filter-input:hover,
.filter-select:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.6);
}

.filter-input:focus,
.filter-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.8);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

/* ═══ LOADING & EMPTY STATES ═══ */
.loading-container {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-16) var(--space-8);
  text-align: center;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  color: var(--text-tertiary);
  margin: 0;
}

.empty-state {
  background: var(--bg-card);
  border: 2px dashed var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-16) var(--space-8);
  text-align: center;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.empty-icon {
  font-size: 80px;
  color: var(--text-tertiary);
  opacity: 0.5;
  margin-bottom: var(--space-4);
}

.empty-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.empty-description {
  color: var(--text-tertiary);
  margin: 0 0 var(--space-6) 0;
}

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

/* ═══ PAGINATION ═══ */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-primary);
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  background: var(--gradient-purple-soft);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.pagination-btn:hover:not(:disabled) {
  background: var(--gradient-primary);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

/* ═══ BUTTONS ═══ */
.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-6);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  text-decoration: none;
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--color-white);
  box-shadow: var(--shadow-btn);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-outline {
  background: transparent;
  color: var(--text-primary);
  border: 2px solid var(--border-primary);
}

.btn-outline:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--accent-primary);
}

.btn-danger {
  background: var(--gradient-danger);
  color: var(--color-white);
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(239, 68, 68, 0.4);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ═══ MODAL ═══ */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-modal);
  padding: var(--space-4);
}

.modal-content {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  animation: modalSlideIn 0.3s ease-out;
}

.modal-content.modal-sm {
  max-width: 400px;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
}

.modal-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.modal-title .material-icons {
  font-size: 24px;
  color: var(--accent-primary);
}

.modal-title .text-danger {
  color: var(--error-text);
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-text);
}

.modal-body {
  padding: var(--space-6);
}

.modal-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
  margin-top: var(--space-6);
  padding-top: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

.review-preview {
  padding: var(--space-4);
  background: var(--gradient-purple-soft);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-6);
}

.preview-rating {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  margin-bottom: var(--space-3);
}

/* ═══ FORM ═══ */
.form-group {
  margin-bottom: var(--space-4);
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-label.required::after {
  content: ' *';
  color: var(--error-text);
}

.form-textarea {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  border: 1.5px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  background: rgba(15, 23, 42, 0.4);
  color: var(--text-primary);
  transition: all var(--transition-fast);
  font-family: inherit;
  resize: vertical;
}

.form-textarea:hover {
  border-color: var(--border-hover);
  background: rgba(15, 23, 42, 0.6);
}

.form-textarea:focus {
  outline: none;
  border-color: var(--accent-primary);
  background: rgba(15, 23, 42, 0.8);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.text-muted {
  color: var(--text-quaternary);
  font-size: var(--text-sm);
}

/* ═══ RESPONSIVE ═══ */
@media (max-width: 768px) {
  .admin-reviews {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-6);
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .review-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-4);
  }

  .review-status {
    align-items: flex-start;
  }

  .review-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }

  .review-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>


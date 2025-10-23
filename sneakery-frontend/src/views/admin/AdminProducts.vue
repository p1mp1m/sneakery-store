<template>
  <div class="admin-page admin-products">
    <!-- =================================================================
         HEADER & STATS
         ================================================================= -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div>
          <h1 class="page-title">
            <i class="material-icons">inventory_2</i>
            Quản lý sản phẩm
          </h1>
          <p class="page-subtitle">
            <i class="material-icons">info</i>
            Quản lý sản phẩm và các biến thể (variants) - Nâng cao
          </p>
        </div>
        <div class="header-actions">
          <button @click="openImportModal" class="btn btn-success">
            <i class="material-icons">file_upload</i>
            Import Excel
          </button>
          <button @click="exportToExcel" class="btn btn-secondary btn-export">
            <i class="material-icons">download</i>
            Export Excel
          </button>
          <button @click="openCreateModal" class="btn btn-primary">
            <i class="material-icons">add</i>
            Thêm sản phẩm
          </button>
        </div>
      </div>
    </div>

    <!-- Statistics Cards -->
    <div v-if="stats" class="stats-grid animate-fade-up">
      <div class="stats-card success">
        <div class="stats-header">
          <div class="stats-info">
            <p class="stats-label">Tổng sản phẩm</p>
            <h3 class="stats-value">{{ stats.totalProducts }}</h3>
          </div>
          <div class="stats-icon success">
            <i class="material-icons">inventory</i>
          </div>
        </div>
      </div>
      <div class="stats-card info">
        <div class="stats-header">
          <div class="stats-info">
            <p class="stats-label">Tổng biến thể</p>
            <h3 class="stats-value">{{ stats.totalVariants }}</h3>
          </div>
          <div class="stats-icon info">
            <i class="material-icons">style</i>
          </div>
        </div>
      </div>
      <div class="stats-card warning">
        <div class="stats-header">
          <div class="stats-info">
            <p class="stats-label">Sắp hết hàng</p>
            <h3 class="stats-value">{{ stats.lowStockCount }}</h3>
          </div>
          <div class="stats-icon warning">
            <i class="material-icons">warning</i>
          </div>
        </div>
      </div>
      <div class="stats-card danger">
        <div class="stats-header">
          <div class="stats-info">
            <p class="stats-label">Hết hàng</p>
            <h3 class="stats-value">{{ stats.outOfStockCount }}</h3>
          </div>
          <div class="stats-icon danger">
            <i class="material-icons">remove_shopping_cart</i>
          </div>
        </div>
      </div>
    </div>

    <!-- =================================================================
         ADVANCED FILTERS
         ================================================================= -->
    <div class="filters-section animate-fade-up">
      <div class="filter-row">
        <div class="filter-group">
          <label>Tìm kiếm:</label>
          <input 
            v-model="filters.search" 
            @input="debounceSearch"
            type="text" 
            placeholder="Tìm theo tên hoặc slug..."
            class="form-control"
          />
        </div>

        <div class="filter-group">
          <label>Thương hiệu:</label>
          <select v-model="filters.brandId" @change="applyFilters" class="form-control">
            <option :value="null">Tất cả thương hiệu</option>
            <option v-for="brand in brands" :key="brand.id" :value="brand.id">
              {{ brand.name }}
            </option>
          </select>
        </div>
        
        <div class="filter-group">
          <label>Danh mục:</label>
          <select v-model="filters.categoryId" @change="applyFilters" class="form-control">
            <option :value="null">Tất cả danh mục</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <label>Trạng thái:</label>
          <select v-model="filters.status" @change="applyFilters" class="form-control">
            <option value="all">Tất cả</option>
            <option value="active">Đang bán</option>
            <option value="inactive">Ngừng bán</option>
          </select>
        </div>
      </div>

      <div class="filter-row">
        <div class="filter-group">
          <label>Giá từ (VNĐ):</label>
          <input 
            v-model.number="filters.minPrice" 
            @change="applyFilters"
            type="number" 
            placeholder="0"
            class="form-control"
          />
        </div>

        <div class="filter-group">
          <label>Giá đến (VNĐ):</label>
          <input 
            v-model.number="filters.maxPrice" 
            @change="applyFilters"
            type="number" 
            placeholder="10,000,000"
            class="form-control"
          />
        </div>

        <div class="filter-group">
          <label>Tồn kho:</label>
          <select v-model="filters.stockLevel" @change="applyFilters" class="form-control">
            <option value="all">Tất cả</option>
            <option value="in_stock">Còn hàng</option>
            <option value="low_stock">Sắp hết</option>
            <option value="out_of_stock">Hết hàng</option>
          </select>
        </div>

        <div class="filter-group">
          <label>Sắp xếp:</label>
          <select v-model="filters.sortBy" @change="applyFilters" class="form-control">
            <option value="">Mặc định</option>
            <option value="name">Tên A-Z</option>
            <option value="price">Giá thấp → cao</option>
            <option value="stock">Tồn kho thấp → cao</option>
          </select>
        </div>

        <div class="filter-actions">
          <button @click="resetFilters" class="btn btn-secondary btn-sm">
            <i class="material-icons">clear</i>
            Xóa bộ lọc
          </button>
        </div>
      </div>
    </div>

    <!-- =================================================================
         LOADING & EMPTY STATES
         ================================================================= -->
    <div v-if="loading" class="loading-container animate-fade-in">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách sản phẩm...</p>
    </div>

    <div v-else-if="products.length === 0" class="empty-state animate-fade-up">
      <i class="material-icons">inventory</i>
      <h3>Chưa có sản phẩm nào</h3>
      <p>Nhấn "Thêm sản phẩm" hoặc "Import Excel" để tạo sản phẩm đầu tiên</p>
    </div>

    <!-- =================================================================
         BULK ACTION BAR & PRODUCTS TABLE
         ================================================================= -->
    <div v-else>
      <!-- Bulk Action Bar -->
      <div v-if="selectedProducts.length > 0" class="bulk-action-bar animate-slide-in">
        <div class="bulk-info">
          <i class="material-icons">check_circle</i>
          Đã chọn <strong>{{ selectedProducts.length }}</strong> sản phẩm
        </div>
        <div class="bulk-actions">
          <button @click="openBulkUpdateModal" class="btn btn-primary btn-sm">
            <i class="material-icons">edit</i>
            Cập nhật hàng loạt
          </button>
          <button @click="bulkDelete" class="btn btn-danger btn-sm">
            <i class="material-icons">delete</i>
            Xóa {{ selectedProducts.length }} sản phẩm
          </button>
          <button @click="clearSelection" class="btn btn-secondary btn-sm">
            <i class="material-icons">clear</i>
            Bỏ chọn
          </button>
        </div>
      </div>

      <!-- Products Table -->
      <div class="table-container animate-fade-up">
        <table class="products-table">
          <thead>
            <tr>
              <th style="width: 40px;">
                <input 
                  type="checkbox" 
                  :checked="isAllSelected"
                  @change="toggleSelectAll"
                  class="checkbox-input"
                />
              </th>
              <th>Tên sản phẩm</th>
              <th>Thương hiệu</th>
              <th>Số variants</th>
              <th>Tồn kho</th>
              <th>Trạng thái</th>
              <th class="text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td>
                <input 
                  type="checkbox" 
                  :checked="selectedProducts.includes(product.id)"
                  @change="toggleSelect(product.id)"
                  class="checkbox-input"
                />
              </td>
              <td>
                <div class="product-name">{{ product.name }}</div>
                <div class="product-slug">{{ product.slug }}</div>
              </td>
              <td>{{ product.brandName || 'N/A' }}</td>
              <td>
                <span class="badge badge-info">
                  {{ product.variantCount || 0 }} variants
                </span>
              </td>
              <td>
                <span class="stock-badge" :class="getStockClass(product)">
                  <i class="material-icons">{{ getStockIcon(product) }}</i>
                  {{ getStockText(product) }}
                </span>
              </td>
              <td>
                <span :class="['status-badge', product.isActive ? 'active' : 'inactive']">
                  {{ product.isActive ? 'Đang bán' : 'Ngừng bán' }}
                </span>
              </td>
              <td class="text-center">
                <div class="action-buttons">
                  <button @click="duplicateProduct(product.id)" class="btn-icon" title="Nhân bản">
                    <i class="material-icons">content_copy</i>
                  </button>
                  <button @click="openEditModal(product)" class="btn-icon" title="Chỉnh sửa">
                    <i class="material-icons">edit</i>
                  </button>
                  <button @click="confirmDelete(product)" class="btn-icon danger" title="Xóa">
                    <i class="material-icons">delete</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="pagination-container">
        <div class="pagination-info">
          Hiển thị {{ (currentPage * pageSize) + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalItems) }} 
          trong tổng số {{ totalItems }} sản phẩm
        </div>
        <div class="pagination">
          <button 
            :disabled="currentPage === 0"
            @click="changePage(currentPage - 1)"
            class="page-btn"
          >
            <i class="material-icons">chevron_left</i>
            Trước
          </button>
          <span class="page-info">
            Trang {{ currentPage + 1 }} / {{ totalPages }}
          </span>
          <button 
            :disabled="currentPage >= totalPages - 1"
            @click="changePage(currentPage + 1)"
            class="page-btn"
          >
            Sau
            <i class="material-icons">chevron_right</i>
          </button>
        </div>
      </div>
    </div>

    <!-- =================================================================
         MODALS
         ================================================================= -->

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal modal-lg" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            {{ isEditMode ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới' }}
          </h2>
          <button @click="closeModal" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>

        <div class="modal-body">
          <!-- Basic Info -->
          <div class="section-title">Thông tin cơ bản</div>
          
          <div class="form-group">
            <label class="form-label required">Tên sản phẩm</label>
            <input 
              v-model="formData.name"
              type="text" 
              class="form-control"
              placeholder="Ví dụ: Nike Air Force 1 '07"
              @input="generateSlug"
            />
            <span v-if="formErrors.name" class="form-error">{{ formErrors.name }}</span>
          </div>
          
          <div class="form-group">
            <label class="form-label required">Slug</label>
            <input 
              v-model="formData.slug"
              type="text" 
              class="form-control"
              placeholder="nike-air-force-1-07"
            />
            <span v-if="formErrors.slug" class="form-error">{{ formErrors.slug }}</span>
            <span class="form-help">URL thân thiện (tự động tạo từ tên)</span>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label required">Thương hiệu</label>
              <select v-model="formData.brandId" class="form-control">
                <option value="">Chọn thương hiệu</option>
                <option v-for="brand in brands" :key="brand.id" :value="brand.id">
                  {{ brand.name }}
                </option>
              </select>
              <span v-if="formErrors.brandId" class="form-error">{{ formErrors.brandId }}</span>
            </div>
            
            <div class="form-group">
              <label class="form-label">Trạng thái</label>
              <select v-model="formData.isActive" class="form-control">
                <option :value="true">Đang bán</option>
                <option :value="false">Ngừng bán</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Mô tả</label>
            <textarea 
              v-model="formData.description"
              class="form-control"
              rows="3"
              placeholder="Mô tả chi tiết về sản phẩm..."
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label required">Danh mục</label>
            <div class="checkbox-group">
              <label v-for="category in categories" :key="category.id" class="checkbox-label">
                <input 
                  type="checkbox" 
                  :value="category.id"
                  v-model="formData.categoryIds"
                />
                {{ category.name }}
              </label>
            </div>
            <span v-if="formErrors.categoryIds" class="form-error">{{ formErrors.categoryIds }}</span>
          </div>
          
          <!-- Variants -->
          <div class="section-title">
            Biến thể sản phẩm (Variants)
            <button @click="addVariant" type="button" class="btn-sm btn-primary">
              <i class="material-icons">add</i>
              Thêm variant
            </button>
          </div>

          <div v-if="formData.variants.length === 0" class="empty-variants">
            <p>Chưa có variant nào. Nhấn "Thêm variant" để tạo variant đầu tiên.</p>
          </div>

          <div v-else class="variants-list">
            <div 
              v-for="(variant, index) in formData.variants" 
              :key="index"
              class="variant-card"
            >
              <div class="variant-header">
                <span class="variant-number">Variant #{{ index + 1 }}</span>
                <button 
                  @click="removeVariant(index)" 
                  type="button"
                  class="btn-icon-sm danger"
                >
                  <i class="material-icons">delete</i>
                </button>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label class="form-label required">SKU</label>
                  <input 
                    v-model="variant.sku"
                    type="text" 
                    class="form-control-sm"
                    placeholder="VD: NIKE-AF1-WHT-42"
                  />
                </div>
            
                <div class="form-group">
                  <label class="form-label required">Size</label>
                  <input 
                    v-model="variant.size"
                    type="text" 
                    class="form-control-sm"
                    placeholder="VD: 42, 43, 44"
                  />
                </div>
              
                <div class="form-group">
                  <label class="form-label required">Màu sắc</label>
                  <input 
                    v-model="variant.color"
                    type="text" 
                    class="form-control-sm"
                    placeholder="VD: Trắng, Đen"
                  />
                </div>
              </div>
            
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label required">Giá gốc (VNĐ)</label>
                  <input 
                    v-model.number="variant.priceBase"
                    type="number" 
                    class="form-control-sm"
                    min="0"
                    step="1000"
                  />
                </div>
              
                <div class="form-group">
                  <label class="form-label">Giá sale (VNĐ)</label>
                  <input 
                    v-model.number="variant.priceSale"
                    type="number" 
                    class="form-control-sm"
                    min="0"
                    step="1000"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label required">Tồn kho</label>
                  <input 
                    v-model.number="variant.stockQuantity"
                    type="number" 
                    class="form-control-sm"
                    min="0"
                  />
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">URL hình ảnh</label>
                <input 
                  v-model="variant.imageUrl"
                  type="text" 
                  class="form-control-sm"
                  placeholder="https://example.com/image.jpg"
                />
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeModal" class="btn btn-secondary">
            Hủy
          </button>
          <button 
            @click="handleSubmit" 
            class="btn btn-primary"
            :disabled="submitting"
          >
            <span v-if="submitting" class="btn-loading"></span>
            {{ submitting ? 'Đang lưu...' : (isEditMode ? 'Cập nhật' : 'Thêm mới') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Import Excel Modal -->
    <div v-if="showImportModal" class="modal-overlay" @click="closeImportModal">
      <div class="modal modal-lg" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <i class="material-icons">file_upload</i>
            Import sản phẩm từ Excel
          </h2>
          <button @click="closeImportModal" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>

        <div class="modal-body">
          <div class="import-instructions">
            <h4>Hướng dẫn:</h4>
            <ol>
              <li>Tải file mẫu Excel (nếu chưa có)</li>
              <li>Điền thông tin sản phẩm theo đúng format</li>
              <li>Upload file và xem preview</li>
              <li>Nhấn "Import" để thêm sản phẩm</li>
            </ol>
            <button @click="downloadTemplate" class="btn btn-secondary btn-sm">
              <i class="material-icons">download</i>
              Tải file mẫu Excel
            </button>
          </div>

          <div class="form-group">
            <label class="form-label">Chọn file Excel:</label>
            <input 
              type="file" 
              accept=".xlsx,.xls"
              @change="handleFileUpload"
              class="form-control"
            />
          </div>

          <div v-if="importPreview.length > 0" class="import-preview">
            <h4>Preview: {{ importPreview.length }} sản phẩm</h4>
            <div class="preview-table-container">
              <table class="preview-table">
                <thead>
                  <tr>
                    <th>Tên SP</th>
                    <th>Brand</th>
                    <th>SKU</th>
                    <th>Size</th>
                    <th>Màu</th>
                    <th>Giá</th>
                    <th>Tồn kho</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in importPreview" :key="index">
                    <td>{{ item.productName }}</td>
                    <td>{{ item.brandName }}</td>
                    <td>{{ item.sku }}</td>
                    <td>{{ item.size }}</td>
                    <td>{{ item.color }}</td>
                    <td>{{ formatCurrency(item.priceBase) }}</td>
                    <td>{{ item.stockQuantity }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeImportModal" class="btn btn-secondary">
            Hủy
          </button>
          <button 
            @click="handleImport" 
            class="btn btn-success"
            :disabled="importing || importPreview.length === 0"
          >
            <span v-if="importing" class="btn-loading"></span>
            {{ importing ? 'Đang import...' : `Import ${importPreview.length} sản phẩm` }}
          </button>
        </div>
      </div>
    </div>

    <!-- Bulk Update Modal -->
    <div v-if="showBulkUpdateModal" class="modal-overlay" @click="closeBulkUpdateModal">
      <div class="modal modal-md" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            <i class="material-icons">edit</i>
            Cập nhật hàng loạt {{ selectedProducts.length }} sản phẩm
          </h2>
          <button @click="closeBulkUpdateModal" class="modal-close">
            <i class="material-icons">close</i>
          </button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label class="form-label required">Chọn hành động:</label>
            <select v-model="bulkUpdateAction" class="form-control">
              <option value="">-- Chọn hành động --</option>
              <option value="UPDATE_STATUS">Cập nhật trạng thái</option>
              <option value="UPDATE_BRAND">Đổi thương hiệu</option>
              <option value="ADD_CATEGORY">Thêm danh mục</option>
              <option value="REMOVE_CATEGORY">Xóa danh mục</option>
            </select>
          </div>

          <div v-if="bulkUpdateAction === 'UPDATE_STATUS'" class="form-group">
            <label class="form-label">Trạng thái mới:</label>
            <select v-model="bulkUpdateValue.isActive" class="form-control">
              <option :value="true">Đang bán</option>
              <option :value="false">Ngừng bán</option>
            </select>
          </div>

          <div v-if="bulkUpdateAction === 'UPDATE_BRAND'" class="form-group">
            <label class="form-label">Thương hiệu mới:</label>
            <select v-model="bulkUpdateValue.brandId" class="form-control">
              <option value="">Chọn thương hiệu</option>
              <option v-for="brand in brands" :key="brand.id" :value="brand.id">
                {{ brand.name }}
              </option>
            </select>
          </div>

          <div v-if="bulkUpdateAction === 'ADD_CATEGORY' || bulkUpdateAction === 'REMOVE_CATEGORY'" class="form-group">
            <label class="form-label">Danh mục:</label>
            <select v-model="bulkUpdateValue.categoryId" class="form-control">
              <option value="">Chọn danh mục</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeBulkUpdateModal" class="btn btn-secondary">
            Hủy
          </button>
          <button 
            @click="handleBulkUpdate" 
            class="btn btn-primary"
            :disabled="bulkUpdating || !bulkUpdateAction"
          >
            <span v-if="bulkUpdating" class="btn-loading"></span>
            {{ bulkUpdating ? 'Đang cập nhật...' : 'Cập nhật' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Dialog -->
    <ConfirmDialog
      v-model="showDeleteModal"
      type="danger"
      title="Xác nhận xóa sản phẩm"
      :message="`Bạn có chắc chắn muốn xóa sản phẩm '${productToDelete?.name}'?`"
      description="Hành động này không thể hoàn tác!"
      confirm-text="Xóa sản phẩm"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="handleDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import * as XLSX from 'xlsx'

const adminStore = useAdminStore()

// State
const products = ref([])
const brands = ref([])
const categories = ref([])
const stats = ref(null)
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)
const showModal = ref(false)
const showDeleteModal = ref(false)
const showImportModal = ref(false)
const showBulkUpdateModal = ref(false)
const isEditMode = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const importing = ref(false)
const bulkUpdating = ref(false)
const productToDelete = ref(null)

// Bulk selection state
const selectedProducts = ref([])

// Advanced filters
const filters = ref({
  search: '',
  brandId: null,
  categoryId: null,
  status: 'all',
  minPrice: null,
  maxPrice: null,
  stockLevel: 'all',
  sortBy: '',
  sortDirection: 'asc'
})

// Import state
const importPreview = ref([])

// Bulk update state
const bulkUpdateAction = ref('')
const bulkUpdateValue = ref({
  isActive: true,
  brandId: null,
  categoryId: null
})

const formData = ref({
  name: '',
  slug: '',
  brandId: null,
  description: '',
  isActive: true,
  categoryIds: [],
  variants: []
})

const formErrors = ref({})

// Computed
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

const isAllSelected = computed(() => {
  return products.value.length > 0 && selectedProducts.value.length === products.value.length
})

// Methods
// ===== STOCK HELPERS =====
const getStockClass = (product) => {
  const totalStock = product.variantCount // Giả định backend trả về tổng stock
  if (totalStock === 0) return 'out-of-stock'
  if (totalStock <= 10) return 'low-stock'
  return 'in-stock'
}

const getStockIcon = (product) => {
  const stockClass = getStockClass(product)
  if (stockClass === 'out-of-stock') return 'remove_shopping_cart'
  if (stockClass === 'low-stock') return 'warning'
  return 'check_circle'
}

const getStockText = (product) => {
  const stockClass = getStockClass(product)
  if (stockClass === 'out-of-stock') return 'Hết hàng'
  if (stockClass === 'low-stock') return 'Sắp hết'
  return 'Còn hàng'
}

// ===== FETCH DATA =====
const fetchProducts = async () => {
  try {
    loading.value = true
    
    const result = await adminStore.fetchProducts(currentPage.value, pageSize.value, filters.value)
    products.value = result.content || []
    totalItems.value = result.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách sản phẩm:', error)
    ElMessage.error('Không thể tải danh sách sản phẩm!')
  } finally {
    loading.value = false
  }
}

const fetchBrands = async () => {
  try {
    await adminStore.fetchBrands()
    brands.value = adminStore.brands
  } catch (error) {
    console.error('Lỗi khi tải danh sách thương hiệu:', error)
  }
}

const fetchCategories = async () => {
  try {
    await adminStore.fetchCategories()
    categories.value = adminStore.categories
  } catch (error) {
    console.error('Lỗi khi tải danh sách danh mục:', error)
  }
}

const fetchStatistics = async () => {
  try {
    const response = await adminStore.getProductStatistics()
    stats.value = response
  } catch (error) {
    console.error('Lỗi khi tải thống kê:', error)
  }
}

// ===== BULK SELECTION =====
const toggleSelect = (productId) => {
  const index = selectedProducts.value.indexOf(productId)
  if (index > -1) {
    selectedProducts.value.splice(index, 1)
  } else {
    selectedProducts.value.push(productId)
  }
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedProducts.value = []
  } else {
    selectedProducts.value = products.value.map(p => p.id)
  }
}

const clearSelection = () => {
  selectedProducts.value = []
}

const bulkDelete = async () => {
  if (!confirm(`Bạn có chắc chắn muốn xóa ${selectedProducts.value.length} sản phẩm đã chọn?`)) {
    return
  }

  try {
    loading.value = true
    
    for (const productId of selectedProducts.value) {
      await adminStore.deleteProduct(productId)
    }
    
    ElMessage.success(`Đã xóa ${selectedProducts.value.length} sản phẩm thành công!`)
    selectedProducts.value = []
    await fetchProducts()
    await fetchStatistics()
  } catch (error) {
    console.error('Lỗi khi xóa hàng loạt:', error)
    ElMessage.error('Có lỗi xảy ra khi xóa sản phẩm!')
  } finally {
    loading.value = false
  }
}

// ===== MODAL ACTIONS =====
const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    name: '',
    slug: '',
    brandId: null,
    description: '',
    isActive: true,
    categoryIds: [],
    variants: []
  }
  formErrors.value = {}
  showModal.value = true
}

const openEditModal = async (product) => {
  isEditMode.value = true
  
  try {
    const detailData = await adminStore.getProductById(product.id)
    formData.value = {
      id: product.id,
      name: detailData.name || '',
      slug: detailData.slug || '',
      brandId: detailData.brandId || null,
      description: detailData.description || '',
      isActive: detailData.isActive !== undefined ? detailData.isActive : true,
      categoryIds: detailData.categories?.map(c => c.id) || [],
      variants: detailData.variants?.map(v => ({
        id: v.id,
        sku: v.sku || '',
        size: v.size || '',
        color: v.color || '',
        priceBase: v.priceBase || 0,
        priceSale: v.priceSale || null,
        stockQuantity: v.stockQuantity || 0,
        imageUrl: v.imageUrl || ''
      })) || []
    }
  } catch (error) {
    console.error('Lỗi khi tải chi tiết sản phẩm:', error)
    formData.value = {
      id: product.id,
      name: product.name || '',
      slug: product.slug || '',
      brandId: product.brandId || null,
      description: '',
      isActive: true,
      categoryIds: [],
      variants: []
    }
  }
  
  formErrors.value = {}
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  formData.value = {
    name: '',
    slug: '',
    brandId: null,
    description: '',
    isActive: true,
    categoryIds: [],
    variants: []
  }
  formErrors.value = {}
}

const generateSlug = () => {
  if (!isEditMode.value) {
    formData.value.slug = formData.value.name
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/đ/g, 'd')
      .replace(/[^a-z0-9\s-]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .trim()
  }
}

const addVariant = () => {
  formData.value.variants.push({
    sku: '',
    size: '',
    color: '',
    priceBase: 0,
    priceSale: null,
    stockQuantity: 0,
    imageUrl: ''
  })
}

const removeVariant = (index) => {
  formData.value.variants.splice(index, 1)
}

const validateForm = () => {
  formErrors.value = {}
  
  if (!formData.value.name || formData.value.name.trim() === '') {
    formErrors.value.name = 'Tên sản phẩm không được để trống'
  }
  
  if (!formData.value.slug || formData.value.slug.trim() === '') {
    formErrors.value.slug = 'Slug không được để trống'
  }
  
  if (!formData.value.brandId) {
    formErrors.value.brandId = 'Vui lòng chọn thương hiệu'
  }
  
  if (formData.value.categoryIds.length === 0) {
    formErrors.value.categoryIds = 'Vui lòng chọn ít nhất 1 danh mục'
  }
  
  if (formData.value.variants.length === 0) {
    formErrors.value.variants = 'Vui lòng thêm ít nhất 1 variant'
    return false
  }
  
  return Object.keys(formErrors.value).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    ElMessage.warning('Vui lòng kiểm tra lại thông tin form!')
    return
  }

  try {
    submitting.value = true
    
    const payload = {
      brandId: formData.value.brandId,
      name: formData.value.name,
      slug: formData.value.slug,
      description: formData.value.description || '',
      isActive: formData.value.isActive,
      categoryIds: formData.value.categoryIds,
      variants: formData.value.variants.map(v => ({
        id: v.id || undefined,
        sku: v.sku,
        size: v.size,
        color: v.color,
        priceBase: Number(v.priceBase),
        priceSale: v.priceSale ? Number(v.priceSale) : null,
        stockQuantity: Number(v.stockQuantity),
        imageUrl: v.imageUrl || null
      }))
    }
    
    if (isEditMode.value) {
      await adminStore.updateProduct(formData.value.id, payload)
      ElMessage.success(`Đã cập nhật sản phẩm "${formData.value.name}" thành công!`)
    } else {
      await adminStore.createProduct(payload)
      ElMessage.success(`Đã thêm sản phẩm "${formData.value.name}" thành công!`)
    }
    
    await fetchProducts()
    await fetchStatistics()
    closeModal()
  } catch (error) {
    console.error('Lỗi khi lưu sản phẩm:', error)
    ElMessage.error('Có lỗi xảy ra! Vui lòng thử lại.')
  } finally {
    submitting.value = false
  }
}

const confirmDelete = (product) => {
  productToDelete.value = product
  showDeleteModal.value = true
}

const handleDelete = async () => {
  try {
    deleting.value = true
    await adminStore.deleteProduct(productToDelete.value.id)
    ElMessage.success(`Đã xóa sản phẩm "${productToDelete.value.name}" thành công!`)
    await fetchProducts()
    await fetchStatistics()
    showDeleteModal.value = false
    productToDelete.value = null
  } catch (error) {
    console.error('Lỗi khi xóa sản phẩm:', error)
    ElMessage.error('Không thể xóa sản phẩm này. Vui lòng thử lại!')
  } finally {
    deleting.value = false
  }
}

// ===== IMPORT EXCEL =====
const openImportModal = () => {
  importPreview.value = []
  showImportModal.value = true
}

const closeImportModal = () => {
  showImportModal.value = false
  importPreview.value = []
}

const downloadTemplate = () => {
  const template = [
    {
      'Tên sản phẩm': 'Nike Air Force 1',
      'Slug': 'nike-air-force-1',
      'Thương hiệu': 'Nike',
      'Mô tả': 'Giày thể thao Nike Air Force 1',
      'Danh mục': 'Men,Sneakers',
      'Trạng thái': 'TRUE',
      'SKU': 'NIKE-AF1-WHT-42',
      'Size': '42',
      'Màu sắc': 'White',
      'Giá gốc': '2500000',
      'Giá sale': '2000000',
      'Tồn kho': '50',
      'URL ảnh': 'https://example.com/image.jpg'
    }
  ]
  
  const worksheet = XLSX.utils.json_to_sheet(template)
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, 'Template')
  XLSX.writeFile(workbook, 'template-import-products.xlsx')
}

const handleFileUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target.result)
      const workbook = XLSX.read(data, { type: 'array' })
      const firstSheet = workbook.Sheets[workbook.SheetNames[0]]
      const jsonData = XLSX.utils.sheet_to_json(firstSheet)
      
      importPreview.value = jsonData.map((row, index) => ({
        rowNumber: index + 2,
        productName: row['Tên sản phẩm'] || '',
        productSlug: row['Slug'] || '',
        brandName: row['Thương hiệu'] || '',
        description: row['Mô tả'] || '',
        categories: row['Danh mục'] || '',
        isActive: row['Trạng thái'] === 'TRUE',
        sku: row['SKU'] || '',
        size: row['Size'] || '',
        color: row['Màu sắc'] || '',
        priceBase: Number(row['Giá gốc']) || 0,
        priceSale: Number(row['Giá sale']) || null,
        stockQuantity: Number(row['Tồn kho']) || 0,
        imageUrl: row['URL ảnh'] || ''
      }))
      
      ElMessage.success(`Đã đọc ${importPreview.value.length} sản phẩm từ file Excel!`)
    } catch (error) {
      console.error('Lỗi khi đọc file Excel:', error)
      ElMessage.error('Không thể đọc file Excel. Vui lòng kiểm tra lại format!')
    }
  }
  reader.readAsArrayBuffer(file)
}

const handleImport = async () => {
  try {
    importing.value = true
    const result = await adminStore.importProducts(importPreview.value)
    
    ElMessage.success(`Import thành công ${result.successCount}/${result.totalRows} sản phẩm!`)
    
    if (result.errorCount > 0) {
      console.error('Import errors:', result.errorItems)
      ElMessage.warning(`Có ${result.errorCount} sản phẩm bị lỗi. Xem console để biết chi tiết.`)
    }
    
    await fetchProducts()
    await fetchStatistics()
    closeImportModal()
  } catch (error) {
    console.error('Lỗi khi import:', error)
    ElMessage.error('Không thể import sản phẩm. Vui lòng thử lại!')
  } finally {
    importing.value = false
  }
}

// ===== BULK UPDATE =====
const openBulkUpdateModal = () => {
  bulkUpdateAction.value = ''
  bulkUpdateValue.value = {
    isActive: true,
    brandId: null,
    categoryId: null
  }
  showBulkUpdateModal.value = true
}

const closeBulkUpdateModal = () => {
  showBulkUpdateModal.value = false
}

const handleBulkUpdate = async () => {
  try {
    bulkUpdating.value = true
    
    const payload = {
      productIds: selectedProducts.value,
      action: bulkUpdateAction.value,
      ...bulkUpdateValue.value
    }
    
    const result = await adminStore.bulkUpdateProducts(payload)
    ElMessage.success(`Cập nhật thành công ${result.successCount}/${result.totalRequested} sản phẩm!`)
    
    await fetchProducts()
    await fetchStatistics()
    closeBulkUpdateModal()
    clearSelection()
  } catch (error) {
    console.error('Lỗi khi bulk update:', error)
    ElMessage.error('Không thể cập nhật hàng loạt. Vui lòng thử lại!')
  } finally {
    bulkUpdating.value = false
  }
}

// ===== DUPLICATE PRODUCT =====
const duplicateProduct = async (productId) => {
  try {
    loading.value = true
    const duplicated = await adminStore.duplicateProduct(productId)
    ElMessage.success(`Đã nhân bản sản phẩm "${duplicated.name}" thành công!`)
    await fetchProducts()
    await fetchStatistics()
  } catch (error) {
    console.error('Lỗi khi nhân bản sản phẩm:', error)
    ElMessage.error('Không thể nhân bản sản phẩm. Vui lòng thử lại!')
  } finally {
    loading.value = false
  }
}

// ===== FILTERS =====
const changePage = (page) => {
  currentPage.value = page
  fetchProducts()
}

const debounceSearch = (() => {
  let timeout
  return () => {
    clearTimeout(timeout)
    timeout = setTimeout(() => {
      currentPage.value = 0
      fetchProducts()
    }, 500)
  }
})()

const applyFilters = () => {
  currentPage.value = 0
  fetchProducts()
}

const resetFilters = () => {
  filters.value = {
    search: '',
    brandId: null,
    categoryId: null,
    status: 'all',
    minPrice: null,
    maxPrice: null,
    stockLevel: 'all',
    sortBy: '',
    sortDirection: 'asc'
  }
  currentPage.value = 0
  fetchProducts()
}

// ===== EXPORT EXCEL =====
const exportToExcel = () => {
  try {
    const exportData = products.value.map((product, index) => ({
      'STT': index + 1,
      'Tên sản phẩm': product.name,
      'Slug': product.slug,
      'Thương hiệu': product.brandName || 'N/A',
      'Số lượng biến thể': product.variantCount || 0,
      'Trạng thái': product.isActive ? 'Đang bán' : 'Ngừng bán'
    }))

    const worksheet = XLSX.utils.json_to_sheet(exportData)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Sản phẩm')
    
    const timestamp = new Date().toISOString().slice(0, 10)
    const filename = `san-pham_${timestamp}.xlsx`
    
    XLSX.writeFile(workbook, filename)
    ElMessage.success(`Đã export ${exportData.length} sản phẩm thành công!`)
  } catch (error) {
    console.error('Lỗi khi export Excel:', error)
    ElMessage.error('Không thể export dữ liệu. Vui lòng thử lại!')
  }
}

// ===== HELPERS =====
const formatCurrency = (value) => {
  if (!value) return '0 đ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

// Lifecycle
onMounted(async () => {
  await Promise.all([
    fetchProducts(),
    fetchBrands(),
    fetchCategories(),
    fetchStatistics()
  ])
})
</script>

<style scoped>
/* =================================================================
   ADMIN PRODUCTS - OPTIMIZED STYLES
   ================================================================= */

/* =================================================================
   LAYOUT
   ================================================================= */
.admin-products {
  padding: var(--space-8);
  max-width: 1600px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* Smooth scrolling cho toàn bộ trang */
* {
  scroll-behavior: smooth;
}

/* =================================================================
   HEADER & STATS
   ================================================================= */
.page-header {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-8);
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
}

.page-subtitle {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  margin: 0;
}

.page-subtitle .material-icons {
  font-size: 1rem;
}

.header-actions {
  display: flex;
  gap: var(--space-3);
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stats-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  transition: var(--transition-smooth);
  position: relative;
  overflow: hidden;
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  transition: var(--transition-smooth);
}

.stats-card.success::before {
  background: var(--success-solid);
}

.stats-card.info::before {
  background: var(--info-solid);
}

.stats-card.warning::before {
  background: var(--warning-solid);
}

.stats-card.danger::before {
  background: var(--error-solid);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-elevated);
  border-color: var(--accent-primary);
}

.stats-card:hover::before {
  width: 100%;
  opacity: 0.05;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-white);
}

.stats-icon.success {
  background: var(--success-solid);
}

.stats-icon.info {
  background: var(--info-solid);
}

.stats-icon.warning {
  background: var(--warning-solid);
}

.stats-icon.danger {
  background: var(--error-solid);
}

.stats-icon .material-icons {
  font-size: var(--text-2xl);
}

.stats-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0 0 var(--space-2) 0;
}

.stats-value {
  font-size: var(--text-4xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

/* =================================================================
   FILTERS SECTION
   ================================================================= */
.filters-section {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
}

.filter-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
  margin-bottom: var(--space-4);
  align-items: end;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-group label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.filter-actions {
  display: flex;
  align-items: flex-end;
}

/* =================================================================
   BULK ACTION BAR
   ================================================================= */
.bulk-action-bar {
  background: var(--gradient-primary);
  color: var(--color-white);
  padding: var(--space-4) var(--space-6);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-6);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-glow-purple);
  animation: slideInFromBottom 0.3s ease-out;
}

@keyframes slideInFromBottom {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.bulk-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--text-sm);
}

.bulk-info .material-icons {
  font-size: 1.25rem;
}

.bulk-actions {
  display: flex;
  gap: var(--space-3);
}

/* =================================================================
   TABLE
   ================================================================= */
.table-container {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  margin-bottom: 0;
}

.products-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: auto;
}

.products-table thead {
  background: var(--table-header-bg);
}

.products-table th {
  padding: var(--space-4) var(--space-6);
  text-align: left;
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 2px solid var(--border-primary);
}

.products-table td {
  padding: var(--space-4) var(--space-6);
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-light);
}

.products-table tbody tr {
  transition: var(--transition-fast);
  cursor: pointer;
}

.products-table tbody tr:hover {
  background: var(--gradient-purple-soft);
  transform: translateX(2px);
}

.products-table tbody tr:last-child td {
  border-bottom: none;
}

.product-name {
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
}

.product-slug {
  font-size: var(--text-xs);
  color: var(--text-tertiary);
  font-family: 'Courier New', monospace;
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--accent-primary);
}

/* Stock Badge */
.stock-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-lg);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  letter-spacing: 0.025em;
}

.stock-badge.in-stock {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.stock-badge.low-stock {
  background: var(--warning-bg);
  color: var(--warning-text);
  border: 1px solid var(--warning-border);
}

.stock-badge.out-of-stock {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

.stock-badge .material-icons {
  font-size: 1rem;
}

/* Status Badge */
.status-badge {
  display: inline-block;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-lg);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  letter-spacing: 0.025em;
}

.status-badge.active {
  background: var(--success-bg);
  color: var(--success-text);
  border: 1px solid var(--success-border);
}

.status-badge.inactive {
  background: var(--error-bg);
  color: var(--error-text);
  border: 1px solid var(--error-border);
}

/* Badge */
.badge {
  display: inline-block;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-lg);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
}

.badge-info {
  background: var(--info-bg);
  color: var(--info-text);
  border: 1px solid var(--info-border);
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: var(--space-2);
  justify-content: center;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  border: none;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition-fast);
}

.btn-icon:hover {
  background: var(--accent-primary);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.btn-icon.danger:hover {
  background: var(--error-solid);
  box-shadow: var(--shadow-glow-red);
}

.btn-icon .material-icons {
  font-size: 1.125rem;
}

/* =================================================================
   PAGINATION
   ================================================================= */
.pagination-container {
  background: var(--bg-card);
  padding: var(--space-6);
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid var(--border-primary);
}

.pagination-info {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.pagination {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.page-btn {
  padding: var(--space-2) var(--space-4);
  border: 1px solid var(--border-primary);
  background: var(--bg-card);
  color: var(--text-secondary);
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  transition: var(--transition-fast);
}

.page-btn:hover:not(:disabled) {
  background: var(--accent-primary);
  color: var(--color-white);
  border-color: var(--accent-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-purple);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-btn .material-icons {
  font-size: 1.125rem;
}

.page-info {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

/* =================================================================
   MODALS
   ================================================================= */
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
  animation: fadeIn 0.2s ease-out;
}

.modal {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-elevated);
  border: 1px solid var(--border-primary);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    transform: translateY(-20px) scale(0.95);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

.modal-md {
  max-width: 600px;
  width: 100%;
}

.modal-lg {
  max-width: 900px;
  width: 100%;
}

.modal-header {
  padding: var(--space-6) var(--space-8);
  border-bottom: 1px solid var(--border-primary);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--gradient-purple-soft);
}

.modal-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.modal-title .material-icons {
  color: var(--accent-primary);
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  border: none;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition-fast);
}

.modal-close:hover {
  background: var(--error-bg);
  color: var(--error-text);
  transform: rotate(90deg);
}

.modal-body {
  padding: var(--space-8);
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  padding: var(--space-6) var(--space-8);
  border-top: 1px solid var(--border-primary);
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  background: var(--bg-secondary);
}

/* =================================================================
   FORMS
   ================================================================= */
.section-title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-6) 0;
  padding-bottom: var(--space-3);
  border-bottom: 2px solid var(--border-primary);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-group {
  margin-bottom: var(--space-6);
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.form-label.required::after {
  content: ' *';
  color: var(--error-text);
}

.form-control {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  color: var(--text-primary);
  background-color: var(--bg-card);
  transition: var(--transition-fast);
  box-sizing: border-box;
}

.form-control:hover {
  border-color: var(--border-dark);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px var(--shadow-focus-purple);
  background-color: var(--bg-card);
}

.form-control::placeholder {
  color: var(--text-tertiary);
}

.form-control-sm {
  padding: var(--space-2) var(--space-3);
  font-size: var(--text-sm);
}

.form-error {
  display: block;
  color: var(--error-text);
  font-size: var(--text-xs);
  margin-top: var(--space-2);
}

.form-help {
  display: block;
  color: var(--text-secondary);
  font-size: var(--text-xs);
  margin-top: var(--space-2);
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-4);
}

/* Checkbox Group */
.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-secondary);
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--accent-primary);
}

/* =================================================================
   VARIANTS
   ================================================================= */
.empty-variants {
  text-align: center;
  padding: var(--space-8);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
}

.variants-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.variant-card {
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  background: var(--bg-secondary);
  transition: var(--transition-fast);
}

.variant-card:hover {
  border-color: var(--accent-light);
  box-shadow: var(--shadow-card);
}

.variant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-4);
}

.variant-number {
  font-weight: var(--font-semibold);
  color: var(--accent-primary);
  font-size: var(--text-sm);
}

.btn-icon-sm {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  border: none;
  background: var(--bg-card);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition-fast);
}

.btn-icon-sm.danger {
  color: var(--error-text);
}

.btn-icon-sm.danger:hover {
  background: var(--error-solid);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow-red);
}

.btn-icon-sm .material-icons {
  font-size: 1rem;
}

/* =================================================================
   IMPORT MODAL
   ================================================================= */
.import-instructions {
  background: var(--gradient-purple-soft);
  padding: var(--space-6);
  border-radius: var(--radius-lg);
  margin-bottom: var(--space-6);
  border-left: 4px solid var(--accent-primary);
}

.import-instructions h4 {
  margin: 0 0 var(--space-4) 0;
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
}

.import-instructions ol {
  margin: 0 0 var(--space-4) var(--space-6);
  padding: 0;
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.import-instructions li {
  margin-bottom: var(--space-2);
}

.import-preview {
  margin-top: var(--space-6);
}

.import-preview h4 {
  margin: 0 0 var(--space-4) 0;
  color: var(--text-primary);
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
}

.preview-table-container {
  max-height: 400px;
  overflow: auto;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
}

.preview-table {
  width: 100%;
  border-collapse: collapse;
  font-size: var(--text-sm);
}

.preview-table thead {
  background: var(--table-header-bg);
  position: sticky;
  top: 0;
  z-index: 1;
}

.preview-table th {
  padding: var(--space-3);
  text-align: left;
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  border-bottom: 2px solid var(--border-primary);
  white-space: nowrap;
}

.preview-table td {
  padding: var(--space-3);
  border-bottom: 1px solid var(--border-light);
}

.preview-table tbody tr:hover {
  background: var(--bg-secondary);
}

/* =================================================================
   BUTTONS
   ================================================================= */
.btn {
  padding: var(--space-3) var(--space-6);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  transition: var(--transition-smooth);
  text-decoration: none;
  white-space: nowrap;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.3s ease, height 0.3s ease;
}

.btn:active::before {
  width: 200px;
  height: 200px;
}

.btn .material-icons {
  font-size: 1.125rem;
  transition: var(--transition-fast);
}

.btn:hover .material-icons {
  transform: scale(1.1);
}

.btn-primary {
  background: var(--gradient-primary);
  color: var(--color-white);
  box-shadow: var(--shadow-btn);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-btn-hover);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: var(--shadow-btn);
}

.btn-secondary {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-primary);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--bg-tertiary);
  border-color: var(--border-dark);
}

.btn-success {
  background: var(--gradient-success);
  color: var(--color-white);
  box-shadow: var(--shadow-btn);
}

.btn-success:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-btn-hover);
}

.btn-danger {
  background: var(--gradient-danger);
  color: var(--color-white);
  box-shadow: var(--shadow-btn);
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-btn-hover);
}

.btn-sm {
  padding: var(--space-2) var(--space-4);
  font-size: var(--text-xs);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

.btn-loading {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: var(--color-white);
  border-radius: var(--radius-full);
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* =================================================================
   LOADING & EMPTY STATES
   ================================================================= */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-16) var(--space-8);
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-primary);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--border-primary);
  border-top-color: var(--accent-primary);
  border-radius: var(--radius-full);
  animation: spin 0.8s linear infinite;
  margin-bottom: var(--space-4);
}

.loading-container p {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.empty-state {
  text-align: center;
  padding: var(--space-16) var(--space-8);
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-primary);
}

.empty-state .material-icons {
  font-size: 4rem;
  color: var(--text-tertiary);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
}

.empty-state p {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin: 0;
}

/* =================================================================
   ANIMATIONS & TRANSITIONS
   ================================================================= */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInFromBottom {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes modalSlideIn {
  from {
    transform: translateY(-20px) scale(0.95);
    opacity: 0;
  }
  to {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

.animate-fade-in {
  animation: fadeIn 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.animate-fade-up {
  animation: fadeUp 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  animation-fill-mode: both;
}

.animate-slide-in {
  animation: slideInFromBottom 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation-fill-mode: both;
}

/* Stagger animations cho các items */
.stats-card {
  animation-delay: calc(var(--index, 0) * 0.1s);
}

/* Performance optimization */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* =================================================================
   RESPONSIVE DESIGN
   ================================================================= */

/* Tablet Landscape (≤1200px) */
@media (max-width: 1200px) {
  .admin-products {
    padding: var(--space-6);
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .form-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* Tablet Portrait (≤768px) */
@media (max-width: 768px) {
  .admin-products {
    padding: var(--space-4);
  }

  .page-header {
    padding: var(--space-6);
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-4);
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .header-actions button {
    flex: 1;
    min-width: 140px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--space-4);
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .bulk-action-bar {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-4);
  }

  .bulk-actions {
    width: 100%;
    justify-content: stretch;
  }

  .bulk-actions button {
    flex: 1;
  }

  .table-container {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .products-table {
    min-width: 900px;
  }

  .pagination-container {
    flex-direction: column;
    gap: var(--space-4);
    text-align: center;
  }

  .pagination {
    justify-content: center;
  }

  .modal {
    margin: var(--space-4);
    max-height: calc(100vh - var(--space-8));
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: var(--space-5);
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}

/* Mobile (≤480px) */
@media (max-width: 480px) {
  .admin-products {
    padding: var(--space-3);
  }

  .page-header {
    padding: var(--space-4);
    border-radius: var(--radius-md);
  }

  .page-title {
    font-size: var(--text-xl);
  }

  .page-title .material-icons {
    font-size: var(--text-2xl);
  }

  .page-subtitle {
    font-size: var(--text-xs);
  }

  .header-actions {
    flex-direction: column;
  }

  .header-actions button {
    width: 100%;
    min-width: unset;
  }

  .stats-card {
    padding: var(--space-4);
  }

  .stats-value {
    font-size: var(--text-2xl);
  }

  .stats-icon {
    width: 40px;
    height: 40px;
  }

  .stats-icon .material-icons {
    font-size: var(--text-xl);
  }

  .filters-section {
    padding: var(--space-4);
  }

  .bulk-action-bar {
    padding: var(--space-3) var(--space-4);
  }

  .bulk-actions {
    flex-direction: column;
  }

  .products-table th,
  .products-table td {
    padding: var(--space-3) var(--space-4);
    font-size: var(--text-xs);
  }

  .pagination-container {
    padding: var(--space-4);
  }

  .page-btn {
    padding: var(--space-2) var(--space-3);
    font-size: var(--text-xs);
  }

  .page-btn span:not(.material-icons) {
    display: none;
  }

  .modal {
    margin: var(--space-2);
    max-height: calc(100vh - var(--space-4));
    border-radius: var(--radius-lg);
  }

  .modal-header {
    padding: var(--space-4);
  }

  .modal-title {
    font-size: var(--text-lg);
  }

  .modal-body {
    padding: var(--space-4);
  }

  .modal-footer {
    padding: var(--space-4);
    flex-direction: column-reverse;
  }

  .modal-footer button {
    width: 100%;
  }

  .btn {
    padding: var(--space-2) var(--space-4);
    font-size: var(--text-xs);
  }

  .btn .material-icons {
    font-size: 1rem;
  }
}
</style>


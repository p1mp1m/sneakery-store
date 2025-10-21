<template>
  <div class="admin-products">
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
   ADMIN PRODUCTS - COMPLETE STYLES
   ================================================================= */

/* Variables */
:root {
  --primary: #667eea;
  --primary-dark: #5a67d8;
  --success: #10b981;
  --warning: #f59e0b;
  --danger: #ef4444;
  --info: #3b82f6;
}

/* =================================================================
   PAGE LAYOUT
   ================================================================= */
.admin-products {
  padding: 2rem;
  max-width: 1600px;
  margin: 0 auto;
}

/* =================================================================
   HEADER & STATS
   ================================================================= */
.page-header {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 2rem;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.875rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
}

.page-title .material-icons {
  font-size: 2rem;
  color: #667eea;
}

.page-subtitle {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #64748b;
  font-size: 0.875rem;
  margin: 0;
}

.page-subtitle .material-icons {
  font-size: 1rem;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stats-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
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
  transition: width 0.3s ease;
}

.stats-card.success::before {
  background: #10b981;
}

.stats-card.info::before {
  background: #3b82f6;
}

.stats-card.warning::before {
  background: #f59e0b;
}

.stats-card.danger::before {
  background: #ef4444;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stats-icon.success {
  background: #10b981;
}

.stats-icon.info {
  background: #3b82f6;
}

.stats-icon.warning {
  background: #f59e0b;
}

.stats-icon.danger {
  background: #ef4444;
}

.stats-icon .material-icons {
  font-size: 1.5rem;
}

.stats-label {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0 0 0.5rem 0;
}

.stats-value {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

/* =================================================================
   FILTERS SECTION
   ================================================================= */
.filters-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1rem;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #475569;
}

.filter-actions {
  display: flex;
  align-items: flex-end;
}

/* =================================================================
   BULK ACTION BAR
   ================================================================= */
.bulk-action-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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
  gap: 0.75rem;
  font-size: 0.875rem;
}

.bulk-info .material-icons {
  font-size: 1.25rem;
}

.bulk-actions {
  display: flex;
  gap: 0.75rem;
}

/* =================================================================
   TABLE
   ================================================================= */
.table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.products-table {
  width: 100%;
  border-collapse: collapse;
}

.products-table thead {
  background: #f8fafc;
}

.products-table th {
  padding: 1rem 1.5rem;
  text-align: left;
  font-weight: 600;
  color: #475569;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 2px solid #e2e8f0;
}

.products-table td {
  padding: 1rem 1.5rem;
  color: #334155;
  border-bottom: 1px solid #f1f5f9;
}

.products-table tbody tr {
  transition: all 0.2s ease;
}

.products-table tbody tr:hover {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, transparent 100%);
}

.product-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.25rem;
}

.product-slug {
  font-size: 0.75rem;
  color: #94a3b8;
  font-family: 'Courier New', monospace;
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #667eea;
}

/* Stock Badge */
.stock-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.025em;
}

.stock-badge.in-stock {
  background: #dcfce7;
  color: #166534;
}

.stock-badge.low-stock {
  background: #fef3c7;
  color: #92400e;
}

.stock-badge.out-of-stock {
  background: #fee2e2;
  color: #991b1b;
}

.stock-badge .material-icons {
  font-size: 1rem;
}

/* Status Badge */
.status-badge {
  display: inline-block;
  padding: 0.375rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.025em;
}

.status-badge.active {
  background: #dcfce7;
  color: #166534;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #991b1b;
}

/* Badge */
.badge {
  display: inline-block;
  padding: 0.25rem 0.625rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.badge-info {
  background: #dbeafe;
  color: #1e40af;
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  background: #f1f5f9;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.btn-icon:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
}

.btn-icon.danger:hover {
  background: #ef4444;
  box-shadow: 0 4px 8px rgba(239, 68, 68, 0.3);
}

.btn-icon .material-icons {
  font-size: 1.125rem;
}

/* =================================================================
   PAGINATION
   ================================================================= */
.pagination-container {
  background: white;
  padding: 1.5rem;
  border-radius: 0 0 12px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #e2e8f0;
}

.pagination-info {
  color: #64748b;
  font-size: 0.875rem;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  background: white;
  color: #475569;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-btn .material-icons {
  font-size: 1.125rem;
}

.page-info {
  font-size: 0.875rem;
  font-weight: 500;
  color: #475569;
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
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
  animation: fadeIn 0.2s ease-out;
}

.modal {
  background: white;
  border-radius: 16px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 25px rgba(0, 0, 0, 0.15);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
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
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.modal-title .material-icons {
  color: #667eea;
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  background: #f1f5f9;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.modal-body {
  padding: 2rem;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  padding: 1.5rem 2rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

/* =================================================================
   FORMS
   ================================================================= */
.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 1.5rem 0;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: #475569;
  margin-bottom: 0.5rem;
}

.form-label.required::after {
  content: ' *';
  color: #ef4444;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  color: #1e293b;
  transition: all 0.2s ease;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-control-sm {
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
}

.form-error {
  display: block;
  color: #ef4444;
  font-size: 0.75rem;
  margin-top: 0.375rem;
}

.form-help {
  display: block;
  color: #64748b;
  font-size: 0.75rem;
  margin-top: 0.375rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

/* Checkbox Group */
.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #475569;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #667eea;
}

/* =================================================================
   VARIANTS
   ================================================================= */
.empty-variants {
  text-align: center;
  padding: 2rem;
  background: #f8fafc;
  border-radius: 8px;
  color: #64748b;
}

.variants-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.variant-card {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1.5rem;
  background: #f8fafc;
}

.variant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.variant-number {
  font-weight: 600;
  color: #667eea;
  font-size: 0.875rem;
}

.btn-icon-sm {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: none;
  background: #f1f5f9;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.btn-icon-sm.danger {
  color: #ef4444;
}

.btn-icon-sm.danger:hover {
  background: #ef4444;
  color: white;
}

.btn-icon-sm .material-icons {
  font-size: 1rem;
}

/* =================================================================
   IMPORT MODAL
   ================================================================= */
.import-instructions {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  border-left: 4px solid #667eea;
}

.import-instructions h4 {
  margin: 0 0 1rem 0;
  color: #1e293b;
  font-size: 1rem;
  font-weight: 600;
}

.import-instructions ol {
  margin: 0 0 1rem 1.5rem;
  padding: 0;
  color: #64748b;
  font-size: 0.875rem;
}

.import-instructions li {
  margin-bottom: 0.5rem;
}

.import-preview {
  margin-top: 1.5rem;
}

.import-preview h4 {
  margin: 0 0 1rem 0;
  color: #1e293b;
  font-size: 1rem;
  font-weight: 600;
}

.preview-table-container {
  max-height: 400px;
  overflow: auto;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.preview-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
}

.preview-table thead {
  background: #f1f5f9;
  position: sticky;
  top: 0;
  z-index: 1;
}

.preview-table th {
  padding: 0.75rem;
  text-align: left;
  font-weight: 600;
  color: #475569;
  border-bottom: 2px solid #e2e8f0;
  white-space: nowrap;
}

.preview-table td {
  padding: 0.75rem;
  border-bottom: 1px solid #f1f5f9;
}

.preview-table tbody tr:hover {
  background: #f8fafc;
}

/* =================================================================
   BUTTONS
   ================================================================= */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s ease;
  text-decoration: none;
}

.btn .material-icons {
  font-size: 1.125rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
}

.btn-secondary:hover:not(:disabled) {
  background: #e2e8f0;
}

.btn-success {
  background: #10b981;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background: #059669;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: #dc2626;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.8125rem;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-loading {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
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
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 1rem;
}

.loading-container p {
  color: #64748b;
  font-size: 0.875rem;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
}

.empty-state .material-icons {
  font-size: 4rem;
  color: #cbd5e1;
  margin-bottom: 1rem;
}

.empty-state h3 {
  color: #475569;
  margin: 0 0 0.5rem 0;
}

.empty-state p {
  color: #94a3b8;
  font-size: 0.875rem;
  margin: 0;
}

/* =================================================================
   ANIMATIONS
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

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-fade-up {
  animation: fadeUp 0.5s ease-out;
}

.animate-slide-in {
  animation: slideInFromBottom 0.3s ease-out;
}

/* =================================================================
   RESPONSIVE
   ================================================================= */
@media (max-width: 1024px) {
  .admin-products {
    padding: 1.5rem;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .admin-products {
    padding: 1rem;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
  }

  .header-actions button {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .bulk-action-bar {
    flex-direction: column;
    gap: 1rem;
  }

  .bulk-actions {
    width: 100%;
    flex-direction: column;
  }

  .bulk-actions button {
    width: 100%;
  }

  .pagination-container {
    flex-direction: column;
    gap: 1rem;
  }

  .page-btn span:not(.material-icons) {
    display: none;
  }

  .table-container {
    overflow-x: auto;
  }

  .products-table {
    min-width: 800px;
  }

  .modal {
    max-height: 95vh;
  }

  .modal-body {
    padding: 1.5rem;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .page-title {
    font-size: 1.5rem;
  }

  .stats-value {
    font-size: 1.5rem;
  }

  .modal-header {
    padding: 1rem 1.5rem;
  }

  .modal-title {
    font-size: 1.25rem;
  }

  .modal-body {
    padding: 1rem 1.5rem;
  }

  .modal-footer {
    padding: 1rem 1.5rem;
    flex-direction: column-reverse;
  }

  .modal-footer button {
    width: 100%;
  }
}
</style>


<template>
  <div class="admin-products">
    <!-- Page Header -->
    <div class="page-header animate-fade-in">
      <div class="header-content">
        <div>
          <h1 class="page-title">
            <i class="material-icons">inventory_2</i>
            Quản lý sản phẩm
          </h1>
          <p class="page-subtitle">
            <i class="material-icons">info</i>
            Quản lý sản phẩm và các biến thể (variants)
          </p>
        </div>
        <div class="header-actions">
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

    <!-- Filters -->
    <div class="filters-section animate-fade-up">
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
        <label>Trạng thái:</label>
        <select v-model="filters.status" @change="applyFilters" class="form-control">
          <option value="all">Tất cả</option>
          <option value="active">Đang bán</option>
          <option value="inactive">Ngừng bán</option>
        </select>
      </div>

      <div class="filter-actions">
        <button @click="resetFilters" class="btn btn-secondary btn-sm">
          <i class="material-icons">clear</i>
          Xóa bộ lọc
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container animate-fade-in">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách sản phẩm...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="products.length === 0" class="empty-state animate-fade-up">
      <i class="material-icons">inventory</i>
      <h3>Chưa có sản phẩm nào</h3>
      <p>Nhấn "Thêm sản phẩm" để tạo sản phẩm đầu tiên</p>
    </div>

    <!-- Products Table & Bulk Actions -->
    <div v-else>
      <!-- Bulk Action Bar -->
      <div v-if="selectedProducts.length > 0" class="bulk-action-bar">
        <div class="bulk-info">
          <i class="material-icons">check_circle</i>
          Đã chọn <strong>{{ selectedProducts.length }}</strong> sản phẩm
        </div>
        <div class="bulk-actions">
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
              <span :class="['status-badge', product.isActive ? 'active' : 'inactive']">
                {{ product.isActive ? 'Đang bán' : 'Ngừng bán' }}
              </span>
            </td>
            <td class="text-center">
              <div class="action-buttons">
                <button @click="openEditModal(product)" class="btn-icon">
                <i class="material-icons">edit</i>
              </button>
                <button @click="confirmDelete(product)" class="btn-icon danger">
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
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const productToDelete = ref(null)

// Bulk selection state
const selectedProducts = ref([])

const filters = ref({
  search: '',
  brandId: null,
  status: 'all'
})

// Debounce timer
let searchTimeout = null

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
// Bulk selection methods
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
    
    // Delete each product
    for (const productId of selectedProducts.value) {
      await adminStore.deleteProduct(productId)
    }
    
    ElMessage.success({
      message: `Đã xóa ${selectedProducts.value.length} sản phẩm thành công!`,
      duration: 3000
    })
    
    // Clear selection and refresh list
    selectedProducts.value = []
    await fetchProducts()
  } catch (error) {
    console.error('Lỗi khi xóa hàng loạt:', error)
    ElMessage.error({
      message: 'Có lỗi xảy ra khi xóa sản phẩm!',
      duration: 3000
    })
  } finally {
    loading.value = false
  }
}

// Methods
const fetchProducts = async () => {
  try {
    loading.value = true
    
    // adminService sẽ tự động filter ra undefined/null/empty
    // Chỉ cần đảm bảo không gửi giá trị 'all'
    const filterParams = {
      search: filters.value.search,
      brandId: filters.value.brandId,
      status: filters.value.status !== 'all' ? filters.value.status : undefined
    }
    
    const result = await adminStore.fetchProducts(currentPage.value, pageSize.value, filterParams)
    products.value = result.content || []
    totalItems.value = result.totalElements || 0
  } catch (error) {
    console.error('Lỗi khi tải danh sách sản phẩm:', error)
    alert('Không thể tải danh sách sản phẩm!')
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
  
  // Fetch chi tiết sản phẩm
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
  
  // Validate name
  if (!formData.value.name || formData.value.name.trim() === '') {
    formErrors.value.name = 'Tên sản phẩm không được để trống'
  } else if (formData.value.name.trim().length < 3) {
    formErrors.value.name = 'Tên sản phẩm phải có ít nhất 3 ký tự'
  } else if (formData.value.name.trim().length > 200) {
    formErrors.value.name = 'Tên sản phẩm không được vượt quá 200 ký tự'
  }
  
  // Validate slug
  if (!formData.value.slug || formData.value.slug.trim() === '') {
    formErrors.value.slug = 'Slug không được để trống'
  } else if (!/^[a-z0-9-]+$/.test(formData.value.slug)) {
    formErrors.value.slug = 'Slug chỉ được chứa chữ thường, số và dấu gạch ngang'
  } else if (formData.value.slug.length < 3) {
    formErrors.value.slug = 'Slug phải có ít nhất 3 ký tự'
  } else if (formData.value.slug.length > 200) {
    formErrors.value.slug = 'Slug không được vượt quá 200 ký tự'
  }
  
  // Validate brand
  if (!formData.value.brandId) {
    formErrors.value.brandId = 'Vui lòng chọn thương hiệu'
  }
  
  // Validate categories
  if (formData.value.categoryIds.length === 0) {
    formErrors.value.categoryIds = 'Vui lòng chọn ít nhất 1 danh mục'
  }
  
  // Validate variants
  if (formData.value.variants.length === 0) {
    formErrors.value.variants = 'Vui lòng thêm ít nhất 1 variant'
    return false
  }
  
  // Validate each variant
  for (let i = 0; i < formData.value.variants.length; i++) {
    const v = formData.value.variants[i]
    if (!v.sku || v.sku.trim() === '') {
      formErrors.value.variants = `Variant ${i + 1}: SKU không được để trống`
      return false
    }
    if (!v.size || v.size.trim() === '') {
      formErrors.value.variants = `Variant ${i + 1}: Size không được để trống`
      return false
    }
    if (!v.color || v.color.trim() === '') {
      formErrors.value.variants = `Variant ${i + 1}: Màu không được để trống`
      return false
    }
    if (!v.priceBase || Number(v.priceBase) <= 0) {
      formErrors.value.variants = `Variant ${i + 1}: Giá gốc phải lớn hơn 0`
      return false
    }
    if (v.priceSale && Number(v.priceSale) >= Number(v.priceBase)) {
      formErrors.value.variants = `Variant ${i + 1}: Giá sale phải nhỏ hơn giá gốc`
      return false
    }
    if (Number(v.stockQuantity) < 0) {
      formErrors.value.variants = `Variant ${i + 1}: Số lượng tồn kho không được âm`
      return false
    }
  }
  
  return Object.keys(formErrors.value).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    ElMessage.warning({
      message: 'Vui lòng kiểm tra lại thông tin form!',
      duration: 3000
    })
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
      ElMessage.success({
        message: `Đã cập nhật sản phẩm "${formData.value.name}" thành công!`,
        duration: 3000
      })
    } else {
      await adminStore.createProduct(payload)
      ElMessage.success({
        message: `Đã thêm sản phẩm "${formData.value.name}" thành công!`,
        duration: 3000
      })
    }
    
    await fetchProducts()
    closeModal()
  } catch (error) {
    console.error('Lỗi khi lưu sản phẩm:', error)
    
    // Handle specific error messages from server
    let errorMessage = 'Có lỗi xảy ra! Vui lòng thử lại.'
    
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      
      if (status === 400) {
        if (data.validationErrors) {
          const firstError = Object.values(data.validationErrors)[0]
          if (firstError && firstError.length > 0) {
            errorMessage = firstError[0]
          }
        } else if (data.message) {
          errorMessage = data.message
        }
      } else if (status === 409) {
        errorMessage = 'Tên, slug hoặc SKU đã tồn tại. Vui lòng chọn tên khác.'
      } else if (status === 500) {
        errorMessage = 'Lỗi server. Vui lòng liên hệ quản trị viên.'
      }
    } else if (error.request) {
      errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng!'
    }
    
    ElMessage.error({
      message: errorMessage,
      duration: 5000
    })
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
    showDeleteModal.value = false
    productToDelete.value = null
  } catch (error) {
    console.error('Lỗi khi xóa sản phẩm:', error)
    ElMessage.error('Không thể xóa sản phẩm này. Vui lòng thử lại!')
  } finally {
    deleting.value = false
  }
}

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
    status: 'all'
  }
  currentPage.value = 0
  fetchProducts()
}

// Export to Excel
const exportToExcel = () => {
  try {
    // Chuẩn bị data để export
    const exportData = products.value.map((product, index) => ({
      'STT': index + 1,
      'Tên sản phẩm': product.name,
      'Slug': product.slug,
      'Thương hiệu': product.brandName || 'N/A',
      'Số lượng biến thể': product.variantCount || 0,
      'Trạng thái': product.isActive ? 'Đang bán' : 'Ngừng bán'
    }))

    // Tạo worksheet từ data
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    
    // Tạo workbook
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Sản phẩm')
    
    // Tạo tên file với timestamp
    const timestamp = new Date().toISOString().slice(0, 10)
    const filename = `san-pham_${timestamp}.xlsx`
    
    // Download file
    XLSX.writeFile(workbook, filename)
    
    ElMessage.success({
      message: `Đã export ${exportData.length} sản phẩm thành công!`,
      duration: 3000
    })
  } catch (error) {
    console.error('Lỗi khi export Excel:', error)
    ElMessage.error({
      message: 'Không thể export dữ liệu. Vui lòng thử lại!',
      duration: 3000
    })
  }
}

// Lifecycle
onMounted(async () => {
  await Promise.all([
    fetchProducts(),
    fetchBrands(),
    fetchCategories()
  ])
})
</script>

<style scoped>
/* ===================================================================
   🎨 ADMIN PRODUCTS - MODERN CSS DESIGN SYSTEM
   ===================================================================
   Professional styling cho trang quản lý sản phẩm
   =================================================================== */

/* ===== ANIMATIONS ===== */
@keyframes slideInRight {
  from {
    transform: translateX(100px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes fadeUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes bounceIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(20px, -20px) scale(1.05);
  }
}

/* Animation Classes */
.animate-slide-in {
  animation: slideIn 0.6s ease-out;
}

.animate-slide-in.delay-1 {
  animation-delay: 0.2s;
  opacity: 0;
  animation-fill-mode: forwards;
}

.animate-slide-in.delay-2 {
  animation-delay: 0.4s;
  opacity: 0;
  animation-fill-mode: forwards;
}

.animate-fade-up {
  animation: fadeUp 0.6s ease-out;
  opacity: 0;
  animation-fill-mode: forwards;
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

.animate-bounce-in {
  animation: bounceIn 0.8s ease-out;
}

.pulse {
  animation: pulse 2s ease-in-out infinite;
}

.spinning {
  animation: spin 1s linear infinite;
}

/* ===== PAGE LAYOUT ===== */
.admin-products {
  max-width: 1400px;
  margin: 0 auto;
}

/* ===== PAGE HEADER ===== */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  padding: 2rem 2.5rem;
  border-radius: 16px;
  margin-bottom: 1.5rem;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
  animation: fadeIn 0.6s ease-out;
}

.header-content {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
}

.page-title {
  font-size: 1.875rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 0.5rem 0;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.page-subtitle {
  color: rgba(255, 255, 255, 0.95);
  margin: 0;
  font-size: 0.9375rem;
  display: flex;
  align-items: center;
  gap: 6px;
}

.page-subtitle i {
  font-size: 18px;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.btn-export {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.4);
  color: #ffffff;
  transition: all 0.3s ease;
}

.btn-export:hover {
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(255, 255, 255, 0.6);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

/* ===== BULK ACTION BAR ===== */
.bulk-action-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
  animation: slideIn 0.3s ease-out;
  position: relative;
  overflow: hidden;
}

.bulk-action-bar::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

.bulk-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1rem;
}

.bulk-info i {
  font-size: 24px;
}

.bulk-actions {
  display: flex;
  gap: 1rem;
}

.bulk-actions .btn {
  border: 2px solid #ffffff;
  font-weight: 500;
}

.bulk-actions .btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #3b82f6;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== FILTERS SECTION ===== */
.filters-section {
  background: #ffffff;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: 1rem;
  align-items: end;
  animation: fadeUp 0.6s ease-out;
  border: 1px solid rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
}

.filters-section:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  border-color: rgba(102, 126, 234, 0.2);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-weight: 500;
  color: #374151;
  font-size: 0.875rem;
}

.filter-actions {
  display: flex;
  align-items: flex-end;
}

.filter-actions .btn {
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-actions .btn i {
  font-size: 1.125rem;
}

/* ===== FORM CONTROLS ===== */
.form-control {
  padding: 0.625rem 0.875rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  width: 100%;
  transition: all 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-control-sm {
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  width: 100%;
}

/* ===== LOADING & EMPTY STATES ===== */
.loading-container,
.empty-state {
  background: #ffffff;
  border-radius: 12px;
  padding: 3rem;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state i {
  font-size: 4rem;
  color: #cbd5e1;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: #64748b;
}

/* ===== PRODUCTS TABLE ===== */
.table-container {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  animation: fadeUp 0.6s ease-out;
  border: 1px solid rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  border-color: rgba(102, 126, 234, 0.2);
}

.products-table {
  width: 100%;
  border-collapse: collapse;
}

.products-table thead {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 2px solid #e2e8f0;
}

.products-table th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #475569;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  position: relative;
}

.products-table th::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.3), transparent);
}

.products-table td {
  padding: 1rem;
  color: #1e293b;
  border-bottom: 1px solid #f1f5f9;
  transition: all 0.2s ease;
}

.products-table tbody tr {
  transition: all 0.3s ease;
}

.products-table tbody tr:hover {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.03) 0%, rgba(102, 126, 234, 0.05) 50%, rgba(102, 126, 234, 0.03) 100%);
  transform: translateX(4px);
  box-shadow: -4px 0 0 0 rgba(102, 126, 234, 0.3);
}

.product-name {
  font-weight: 500;
  color: #1e293b;
}

.product-slug {
  font-size: 0.75rem;
  color: #64748b;
  font-family: monospace;
}

/* ===== STATUS BADGES ===== */
.status-badge {
  display: inline-block;
  padding: 0.25rem 0.625rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-badge.active {
  background: #dcfce7;
  color: #166534;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #991b1b;
}

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

/* ===== ACTION BUTTONS ===== */
.text-center {
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}

.btn-icon {
  padding: 0.375rem;
  border: 1px solid #d1d5db;
  background: #ffffff;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.btn-icon::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(59, 130, 246, 0.2);
  transform: translate(-50%, -50%);
  transition: width 0.3s, height 0.3s;
}

.btn-icon:hover::before {
  width: 100%;
  height: 100%;
}

.btn-icon:hover {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border-color: #3b82f6;
  color: #3b82f6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.btn-icon.danger::before {
  background: rgba(239, 68, 68, 0.2);
}

.btn-icon.danger:hover {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  border-color: #ef4444;
  color: #ef4444;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
}

/* ===== PAGINATION ===== */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding: 1rem 1.5rem;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(102, 126, 234, 0.1);
  animation: fadeUp 0.6s ease-out;
}

.pagination-info {
  color: #64748b;
  font-size: 0.875rem;
  font-weight: 500;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  color: #64748b;
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: #ffffff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
}

.page-info {
  font-weight: 600;
  color: #1e293b;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border-radius: 8px;
}

/* ===== MODAL ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
  animation: fadeIn 0.3s ease-out;
}

.modal {
  background: #ffffff;
  border-radius: 16px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  animation: bounceIn 0.5s ease-out;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.modal-lg {
  max-width: 900px;
}

.modal-sm {
  max-width: 400px;
}

.modal-header {
  padding: 1.5rem 2rem;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 2px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
}

.modal-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.5), transparent);
}

.modal-title {
  font-size: 1.375rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.modal-title i {
  color: #667eea;
  font-size: 1.5rem;
}

.modal-close {
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(226, 232, 240, 0.5);
  color: #64748b;
  cursor: pointer;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-close:hover {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #ef4444;
  transform: rotate(90deg) scale(1.1);
}

.modal-body {
  padding: 2rem;
}

.modal-footer {
  padding: 1.5rem 2rem;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-top: 2px solid #e2e8f0;
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
}

/* ===== FORM ELEMENTS ===== */
.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-label {
  display: block;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
}

.form-label.required::after {
  content: ' *';
  color: #ef4444;
}

.form-control {
  width: 100%;
  padding: 0.625rem 0.875rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-error {
  display: block;
  color: #ef4444;
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

.form-help {
  display: block;
  color: #64748b;
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

/* ===== CATEGORIES & CHECKBOXES ===== */
.checkbox-group {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 0.75rem;
  padding: 0.75rem;
  background: #f8fafc;
  border-radius: 6px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-size: 0.875rem;
  color: #374151;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

/* ===== VARIANTS SECTION ===== */
.empty-variants {
  padding: 2rem;
  text-align: center;
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
  padding: 1rem;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.variant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
}

.variant-number {
  font-weight: 600;
  color: #1e293b;
}

.btn-icon-sm {
  padding: 0.25rem;
  border: 1px solid #d1d5db;
  background: #ffffff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-icon-sm.danger:hover {
  background: #fee2e2;
  border-color: #ef4444;
  color: #ef4444;
}

/* ===== BUTTONS ===== */
.btn {
  padding: 0.625rem 1.25rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  transition: left 0.5s;
}

.btn:hover::before {
  left: 100%;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.btn-secondary {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #475569;
  border: 1px solid #cbd5e0;
}

.btn-secondary:hover:not(:disabled) {
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e0 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.btn-danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.btn-danger:hover:not(:disabled) {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(239, 68, 68, 0.4);
}

.btn-sm {
  padding: 0.5rem 0.875rem;
  font-size: 0.75rem;
}

.btn-loading {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* ===== UTILITIES ===== */
.text-error {
  color: #ef4444;
  font-size: 0.875rem;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }

  .checkbox-group {
    grid-template-columns: 1fr;
  }

  .variant-card .form-row {
    grid-template-columns: 1fr;
  }
}

/* ===================================================================
   End of Admin Products Styles
   =================================================================== */
</style>

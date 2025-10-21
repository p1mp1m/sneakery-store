<template>
  <div class="admin-products">
    <!-- Page Header -->
    <div class="page-header">
      <div>
      <h1 class="page-title">Quản lý sản phẩm</h1>
        <p class="page-subtitle">Quản lý sản phẩm và các biến thể (variants)</p>
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

    <!-- Filters -->
    <div class="filters-section">
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
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Đang tải danh sách sản phẩm...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="products.length === 0" class="empty-state">
      <i class="material-icons">inventory</i>
      <h3>Chưa có sản phẩm nào</h3>
      <p>Nhấn "Thêm sản phẩm" để tạo sản phẩm đầu tiên</p>
    </div>

    <!-- Products Table -->
    <!-- Bulk Action Bar -->
    <div v-if="selectedProducts.length > 0" class="bulk-action-bar">
      <div class="bulk-info">
        <i class="material-icons">check_circle</i>
        Đã chọn <strong>{{ selectedProducts.length }}</strong> sản phẩm
      </div>
      <div class="bulk-actions">
        <button @click="bulkDelete" class="btn btn-danger">
          <i class="material-icons">delete</i>
          Xóa {{ selectedProducts.length }} sản phẩm
        </button>
        <button @click="clearSelection" class="btn btn-secondary">
          <i class="material-icons">clear</i>
          Bỏ chọn
        </button>
      </div>
    </div>

    <div v-else class="table-container">
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
            <td>{{ product.brand?.name || 'N/A' }}</td>
            <td>
              <span class="badge badge-info">
                {{ product.variants?.length || 0 }} variants
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
    <div v-if="!loading && products.length > 0" class="pagination-container">
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
        </button>
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
    const result = await adminStore.fetchProducts(currentPage.value, pageSize.value, filters.value)
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
      'Thương hiệu': product.brand?.name || 'N/A',
      'Số lượng biến thể': product.variants?.length || 0,
      'Trạng thái': product.isActive ? 'Đang bán' : 'Ngừng bán',
      'Mô tả': product.description || ''
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
/* Styles from previous AdminBrands/AdminCategories - use similar approach */
.admin-products {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 1.875rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
}

.page-subtitle {
  color: #64748b;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.btn-export {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* Bulk Action Bar */
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
  animation: slideIn 0.3s ease-out;
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
  border: 2px solid white;
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

.filters-section {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: 1rem;
  align-items: end;
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

.loading-container,
.empty-state {
  background: white;
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

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.products-table {
  width: 100%;
  border-collapse: collapse;
}

.products-table thead {
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.products-table th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #475569;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.products-table td {
  padding: 1rem;
  color: #1e293b;
  border-bottom: 1px solid #f1f5f9;
}

.products-table tbody tr:hover {
  background: #f8fafc;
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
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-icon:hover {
  background: #f8fafc;
  border-color: #3b82f6;
  color: #3b82f6;
}

.btn-icon.danger:hover {
  background: #fef2f2;
  border-color: #ef4444;
  color: #ef4444;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #f9fafb;
  border-color: #9ca3af;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-weight: 500;
  color: #374151;
}

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
}

.modal {
  background: white;
  border-radius: 12px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px rgba(0, 0, 0, 0.15);
}

.modal-lg {
  max-width: 900px;
}

.modal-sm {
  max-width: 400px;
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: #64748b;
  cursor: pointer;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
}

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
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-icon-sm.danger:hover {
  background: #fee2e2;
  border-color: #ef4444;
  color: #ef4444;
}

.btn {
  padding: 0.625rem 1.25rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
}

.btn-secondary:hover:not(:disabled) {
  background: #e2e8f0;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: #dc2626;
}

.btn-sm {
  padding: 0.5rem 0.875rem;
  font-size: 0.75rem;
}

.btn-loading {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.text-error {
  color: #ef4444;
  font-size: 0.875rem;
}

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
</style>

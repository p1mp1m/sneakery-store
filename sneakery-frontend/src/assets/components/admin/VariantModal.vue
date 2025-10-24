<template>
  <div v-if="isOpen" class="modal-overlay" @click="closeModal">
    <div class="modal" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">
          <i class="material-icons">{{ isEdit ? 'edit' : 'add' }}</i>
          {{ isEdit ? 'Chỉnh sửa Biến Thể' : 'Thêm Biến Thể Mới' }}
        </h3>
        <button class="modal-close" @click="closeModal">
          <i class="material-icons">close</i>
        </button>
      </div>
      
      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label required">Sản phẩm</label>
              <select v-model="formData.productId" class="form-control" required>
                <option value="">Chọn sản phẩm</option>
                <option v-for="product in products" :key="product.id" :value="product.id">
                  {{ product.name }} - {{ product.brandName }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label class="form-label required">SKU</label>
              <input
                v-model="formData.sku"
                type="text"
                class="form-control"
                placeholder="Ví dụ: NAM270-BLK-40"
                required
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label class="form-label required">Màu sắc</label>
              <input
                v-model="formData.color"
                type="text"
                class="form-control"
                placeholder="Ví dụ: black, white, red"
                required
              />
            </div>
            
            <div class="form-group">
              <label class="form-label required">Kích thước</label>
              <input
                v-model="formData.size"
                type="text"
                class="form-control"
                placeholder="Ví dụ: 40, 41, 42"
                required
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label class="form-label required">Giá gốc (VNĐ)</label>
              <input
                v-model.number="formData.priceBase"
                type="number"
                class="form-control"
                placeholder="3500000"
                min="0"
                required
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">Giá khuyến mãi (VNĐ)</label>
              <input
                v-model.number="formData.priceSale"
                type="number"
                class="form-control"
                placeholder="3000000"
                min="0"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label class="form-label required">Số lượng tồn kho</label>
              <input
                v-model.number="formData.stockQuantity"
                type="number"
                class="form-control"
                placeholder="15"
                min="0"
                required
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">Ngưỡng cảnh báo</label>
              <input
                v-model.number="formData.lowStockThreshold"
                type="number"
                class="form-control"
                placeholder="10"
                min="0"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">URL hình ảnh</label>
            <input
              v-model="formData.imageUrl"
              type="url"
              class="form-control"
              placeholder="https://example.com/image.jpg"
            />
          </div>
          
          <div class="form-group">
            <label class="form-checkbox">
              <input v-model="formData.isActive" type="checkbox" />
              <span class="checkbox-label">Kích hoạt biến thể</span>
            </label>
          </div>
        </form>
      </div>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" @click="closeModal">
          Hủy
        </button>
        <button 
          type="button" 
          class="btn btn-primary" 
          @click="handleSubmit"
          :disabled="isSubmitting"
        >
          <i class="material-icons" v-if="isSubmitting">hourglass_empty</i>
          <i class="material-icons" v-else>{{ isEdit ? 'save' : 'add' }}</i>
          {{ isSubmitting ? 'Đang xử lý...' : (isEdit ? 'Cập nhật' : 'Tạo mới') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, computed } from 'vue'
import { useAdminStore } from '@/stores/admin'
import toastService from '@/utils/toastService'

// ===== PROPS & EMITS =====
const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  variant: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

// ===== STORES & COMPOSABLES =====
const adminStore = useAdminStore()
const toast = toastService

// ===== STATE =====
const isSubmitting = ref(false)
const products = ref([])

const formData = reactive({
  productId: '',
  sku: '',
  color: '',
  size: '',
  priceBase: 0,
  priceSale: null,
  costPrice: null,
  stockQuantity: 0,
  lowStockThreshold: 10,
  weightGrams: null,
  imageUrl: '',
  isActive: true
})

// ===== COMPUTED =====
const isEdit = computed(() => !!props.variant)

// ===== LIFECYCLE =====
onMounted(async () => {
  await loadProducts()
})

// ===== WATCHERS =====
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    resetForm()
    if (props.variant) {
      populateForm(props.variant)
    }
  }
})

// ===== METHODS =====
const loadProducts = async () => {
  try {
    const result = await adminStore.fetchProducts(0, 1000) // Load all products
    products.value = result.content || []
  } catch (error) {
    console.error('Error loading products:', error)
    toast.error('Lỗi', 'Không thể tải danh sách sản phẩm')
  }
}

const resetForm = () => {
  Object.assign(formData, {
    productId: '',
    sku: '',
    color: '',
    size: '',
    priceBase: 0,
    priceSale: null,
    costPrice: null,
    stockQuantity: 0,
    lowStockThreshold: 10,
    weightGrams: null,
    imageUrl: '',
    isActive: true
  })
}

const populateForm = (variant) => {
  Object.assign(formData, {
    productId: variant.productId || '',
    sku: variant.sku || '',
    color: variant.color || '',
    size: variant.size || '',
    priceBase: variant.priceBase || 0,
    priceSale: variant.priceSale || null,
    costPrice: variant.costPrice || null,
    stockQuantity: variant.stockQuantity || 0,
    lowStockThreshold: variant.lowStockThreshold || 10,
    weightGrams: variant.weightGrams || null,
    imageUrl: variant.imageUrl || '',
    isActive: variant.isActive !== false
  })
}

const closeModal = () => {
  emit('close')
}

const handleSubmit = async () => {
  try {
    isSubmitting.value = true
    
    if (isEdit.value) {
      await adminStore.updateProductVariant(props.variant.id, formData)
      toast.success('Thành công', 'Cập nhật biến thể thành công')
    } else {
      await adminStore.createProductVariant(formData)
      toast.success('Thành công', 'Tạo biến thể mới thành công')
    }
    
    emit('success')
    closeModal()
  } catch (error) {
    console.error('Error saving variant:', error)
    toast.error('Lỗi', 'Không thể lưu biến thể')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
/* Modal styles are inherited from global admin styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal {
  background: var(--card-bg);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-2xl);
  border: 1px solid var(--border-primary);
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-primary);
}

.modal-title {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin: 0;
  color: var(--text-primary);
  font-size: var(--text-xl);
}

.modal-close {
  background: none;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: var(--space-2);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.modal-body {
  padding: var(--space-6);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-3);
  padding: var(--space-6);
  border-top: 1px solid var(--border-primary);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.form-label {
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.form-label.required::after {
  content: ' *';
  color: var(--error-text);
}

.form-control {
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  background: var(--bg-primary);
  color: var(--text-primary);
  font-size: var(--text-base);
  transition: all var(--transition-fast);
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  cursor: pointer;
}

.checkbox-label {
  color: var(--text-primary);
  font-size: var(--text-sm);
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal {
    width: 95%;
    margin: var(--space-4);
  }
}
</style>

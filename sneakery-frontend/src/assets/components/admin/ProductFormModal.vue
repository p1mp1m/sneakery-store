<template>
  <Teleport to="body">
    <div v-if="visible" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="handleClose">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-5xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">{{ isEditMode ? "edit" : "add" }}</i>
            {{ isEditMode ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới" }}
          </h2>
          <button @click="handleClose" class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
            <i class="material-icons text-base">close</i>
          </button>
        </div>

        <div class="p-6 space-y-6">
          <!-- Basic Info Section -->
          <div class="space-y-4">
            <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2 pb-2 border-b border-gray-200 dark:border-gray-700">
              <i class="material-icons text-purple-600 dark:text-purple-400 text-lg">info</i>
              Thông tin cơ bản
            </h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Tên sản phẩm -->
              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Tên sản phẩm <span class="text-red-500">*</span>
                </label>
                <input
                  :value="formData.name"
                  @input="handleNameInput"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                  placeholder="Ví dụ: Nike Air Force 1 '07"
                />
                <span v-if="formErrors.name" class="text-sm text-red-600 dark:text-red-400">{{
                  formErrors.name
                }}</span>
              </div>

              <!-- Slug -->
              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Slug <span class="text-red-500">*</span>
                </label>
                <input
                  :value="formData.slug"
                  @input="handleSlugInput"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                  placeholder="nike-air-force-1-07"
                />
                <span v-if="formErrors.slug" class="text-sm text-red-600 dark:text-red-400">{{
                  formErrors.slug
                }}</span>
                <span class="text-xs text-gray-500 dark:text-gray-400">URL thân thiện (tự động tạo từ tên)</span>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Thương hiệu -->
              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Thương hiệu <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select
                    :value="formData.brandId"
                    @change="handleBrandChange"
                    class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                  >
                    <option value="">Chọn thương hiệu</option>
                    <option
                      v-for="brand in brands"
                      :key="brand.id"
                      :value="brand.id"
                    >
                      {{ brand.name }}
                    </option>
                  </select>
                  <button
                    type="button"
                    class="px-3 py-2 bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300 rounded-lg hover:bg-purple-200 dark:hover:bg-purple-900/50 transition-colors"
                    @click="$emit('quick-add-brand')"
                    title="Thêm thương hiệu mới"
                  >
                    <i class="material-icons text-base">add</i>
                  </button>
                </div>
                <span v-if="formErrors.brandId" class="text-sm text-red-600 dark:text-red-400">{{
                  formErrors.brandId
                }}</span>
              </div>

              <!-- Trạng thái -->
              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Trạng thái
                </label>
                <select
                  :value="formData.isActive"
                  @change="handleStatusChange"
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                >
                  <option :value="true">Đang bán</option>
                  <option :value="false">Ngừng bán</option>
                </select>
              </div>
            </div>

            <!-- Chất liệu / Loại đế giày -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Chất liệu -->
              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Chất liệu <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select
                    :value="formData.materialId"
                    @change="handleMaterialChange"
                    class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                  >
                    <option value="">Chọn chất liệu</option>
                    <option
                      v-for="material in materials"
                      :key="material.id"
                      :value="material.id"
                    >
                      {{ material.name }}
                    </option>
                  </select>
                  <button
                    type="button"
                    class="px-3 py-2 bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300 rounded-lg hover:bg-purple-200 dark:hover:bg-purple-900/50 transition-colors"
                    @click="$emit('quick-add-material')"
                    title="Thêm chất liệu mới"
                  >
                    <i class="material-icons text-base">add</i>
                  </button>
                </div>
                <span v-if="formErrors.materialId" class="text-sm text-red-600 dark:text-red-400">{{
                  formErrors.materialId
                }}</span>
              </div>

              <!-- Loại đế giày -->
              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Loại đế giày <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select
                    :value="formData.shoeSoleId"
                    @change="handleSoleChange"
                    class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                  >
                    <option value="">Chọn loại đế giày</option>
                    <option v-for="sole in soles" :key="sole.id" :value="sole.id">
                      {{ sole.name }}
                    </option>
                  </select>
                  <button
                    type="button"
                    class="px-3 py-2 bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300 rounded-lg hover:bg-purple-200 dark:hover:bg-purple-900/50 transition-colors"
                    @click="$emit('quick-add-sole')"
                    title="Thêm loại đế giày mới"
                  >
                    <i class="material-icons text-base">add</i>
                  </button>
                </div>
                <span v-if="formErrors.shoeSoleId" class="text-sm text-red-600 dark:text-red-400">{{
                  formErrors.shoeSoleId
                }}</span>
              </div>
            </div>

            <!-- Mô tả -->
            <div class="space-y-2">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Mô tả
              </label>
              <textarea
                :value="formData.description"
                @input="handleDescriptionInput"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors resize-none"
                placeholder="Mô tả chi tiết về sản phẩm..."
              ></textarea>
            </div>

            <!-- Danh mục -->
            <div class="space-y-2">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Danh mục <span class="text-red-500">*</span>
              </label>
              <div class="flex flex-wrap gap-3 p-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-800/50">
                <label
                  v-for="category in childCategories"
                  :key="category.id"
                  class="flex items-center gap-2 px-3 py-1.5 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg cursor-pointer hover:bg-purple-50 dark:hover:bg-purple-900/20 hover:border-purple-300 dark:hover:border-purple-600 transition-colors"
                >
                  <input
                    type="checkbox"
                    :value="category.id"
                    :checked="formData.categoryIds.includes(category.id)"
                    @change="handleCategoryChange(category.id, $event)"
                    class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                  />
                  <span class="text-sm text-gray-700 dark:text-gray-300">{{ category.name }}</span>
                </label>
                <button
                  type="button"
                  class="flex items-center gap-2 px-3 py-1.5 bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300 border border-purple-300 dark:border-purple-600 rounded-lg hover:bg-purple-200 dark:hover:bg-purple-900/50 transition-colors"
                  @click="$emit('quick-add-category')"
                  aria-label="Thêm danh mục mới"
                  title="Thêm danh mục mới"
                >
                  <i class="material-icons text-base">add</i>
                  <span class="text-sm font-medium">Thêm</span>
                </button>
              </div>
              <span v-if="formErrors.categoryIds" class="text-sm text-red-600 dark:text-red-400">{{
                formErrors.categoryIds
              }}</span>
            </div>

            <!-- Thư viện hình ảnh -->
            <div class="space-y-2">
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Thư viện hình ảnh sản phẩm
              </label>
              <UploadGallery
                :initialImages="initialImages"
                :maxImages="maxImages"
                @change="handleImagesChange"
                @remove="handleImageRemove"
              />
              <span class="text-xs text-gray-500 dark:text-gray-400">
                Có thể tải ảnh từ máy hoặc nhập URL. Chọn ảnh "Primary" để hiển thị chính.
              </span>
            </div>
          </div>

          <!-- Variants Section -->
          <div class="space-y-4">
            <div class="flex items-center justify-between pb-2 border-b border-gray-200 dark:border-gray-700">
              <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                <i class="material-icons text-purple-600 dark:text-purple-400 text-lg">inventory</i>
                Sản phẩm chi tiết
              </h3>
              <button
                @click="handleAddVariant"
                type="button"
                class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium"
              >
                <i class="material-icons text-base">add</i>
                Thêm SPCT
              </button>
            </div>

            <div v-if="formData.variants.length === 0" class="text-center py-8 px-4 bg-gray-50 dark:bg-gray-800/50 rounded-lg border border-gray-200 dark:border-gray-700">
              <i class="material-icons text-gray-400 dark:text-gray-500 text-4xl mb-2">inventory_2</i>
              <p class="text-gray-600 dark:text-gray-400">Chưa có SPCT nào. Nhấn "Thêm SPCT" để tạo SPCT đầu tiên.</p>
            </div>

            <div v-else class="space-y-4">
              <div
                v-for="(variant, index) in formData.variants"
                :key="index"
                class="p-4 bg-gray-50 dark:bg-gray-800/50 rounded-lg border border-gray-200 dark:border-gray-700 space-y-4"
              >
                <div class="flex items-center justify-between">
                  <span class="text-sm font-semibold text-gray-900 dark:text-gray-100">Variant #{{ index + 1 }}</span>
                  <button
                    @click="handleRemoveVariant(index)"
                    type="button"
                    class="px-2 py-1 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
                  >
                    <i class="material-icons text-base">delete</i>
                  </button>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div class="space-y-2">
                    <label class="block text-xs font-medium text-gray-700 dark:text-gray-300">
                      SKU <span class="text-red-500">*</span>
                    </label>
                    <input
                      :value="variant.sku"
                      @input="handleVariantInput(index, 'sku', $event)"
                      type="text"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      placeholder="VD: NIKE-AF1-WHT-42"
                    />
                  </div>

                  <div class="space-y-2">
                    <label class="block text-xs font-medium text-gray-700 dark:text-gray-300">
                      Size <span class="text-red-500">*</span>
                    </label>
                    <input
                      :value="variant.size"
                      @input="handleVariantInput(index, 'size', $event)"
                      type="text"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      placeholder="VD: 42, 43, 44"
                    />
                  </div>

                  <div class="space-y-2">
                    <label class="block text-xs font-medium text-gray-700 dark:text-gray-300">
                      Màu sắc <span class="text-red-500">*</span>
                    </label>
                    <input
                      :value="variant.color"
                      @input="handleVariantInput(index, 'color', $event)"
                      type="text"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      placeholder="VD: Trắng, Đen"
                    />
                  </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div class="space-y-2">
                    <label class="block text-xs font-medium text-gray-700 dark:text-gray-300">
                      Giá gốc (VNĐ) <span class="text-red-500">*</span>
                    </label>
                    <input
                      :value="variant.priceBase"
                      @input="handleVariantInput(index, 'priceBase', $event)"
                      type="number"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      min="0"
                      step="1000"
                    />
                  </div>

                  <div class="space-y-2">
                    <label class="block text-xs font-medium text-gray-700 dark:text-gray-300">
                      Giá sale (VNĐ)
                    </label>
                    <input
                      :value="variant.priceSale"
                      @input="handleVariantInput(index, 'priceSale', $event)"
                      type="number"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      min="0"
                      step="1000"
                    />
                  </div>

                  <div class="space-y-2">
                    <label class="block text-xs font-medium text-gray-700 dark:text-gray-300">
                      Tồn kho <span class="text-red-500">*</span>
                    </label>
                    <input
                      :value="variant.stockQuantity"
                      @input="handleVariantInput(index, 'stockQuantity', $event)"
                      type="number"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      min="0"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800">
          <button @click="handleClose" class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium">
            Hủy
          </button>
          <button
            @click="handleSubmit"
            class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="submitting"
          >
            {{ submitting ? "Đang lưu..." : isEditMode ? "Cập nhật" : "Thêm mới" }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { generateSlug as generateSlugUtil } from '@/utils/slugGenerator'
import UploadGallery from './UploadGallery.vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  isEditMode: {
    type: Boolean,
    default: false
  },
  initialProduct: {
    type: Object,
    default: null
  },
  brands: {
    type: Array,
    default: () => []
  },
  categories: {
    type: Array,
    default: () => []
  },
  materials: {
    type: Array,
    default: () => []
  },
  soles: {
    type: Array,
    default: () => []
  },
  initialImages: {
    type: Array,
    default: () => []
  },
  maxImages: {
    type: Number,
    default: 10
  },
  formErrors: {
    type: Object,
    default: () => ({})
  },
  submitting: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits([
  'update:visible',
  'update:formData',
  'update:formErrors',
  'submit',
  'close',
  'quick-add-brand',
  'quick-add-material',
  'quick-add-sole',
  'quick-add-category'
])

// Form data - sync với parent
const formData = ref({
  name: '',
  slug: '',
  brandId: null,
  description: '',
  isActive: true,
  categoryIds: [],
  materialId: null,
  shoeSoleId: null,
  variants: []
})

// Child categories (chỉ hiển thị categories có parentId)
const childCategories = computed(() => {
  return props.categories.filter((cat) => cat.parentId != null)
})

// Watch initialProduct để load data khi edit
watch(
  () => props.initialProduct,
  (product) => {
    if (product) {
      formData.value = {
        id: product.id,
        name: product.name || '',
        slug: product.slug || '',
        brandId: product.brandId || null,
        description: product.description || '',
        isActive: product.isActive ?? true,
        categoryIds: product.categoryIds || [],
        materialId: product.materialId || null,
        shoeSoleId: product.shoeSoleId || null,
        variants: product.variants || []
      }
      // Emit sau khi load
      emit('update:formData', { ...formData.value })
    } else if (!props.isEditMode) {
      // Reset khi create mode
      formData.value = {
        name: '',
        slug: '',
        brandId: null,
        description: '',
        isActive: true,
        categoryIds: [],
        materialId: null,
        shoeSoleId: null,
        variants: []
      }
      emit('update:formData', { ...formData.value })
    }
  },
  { immediate: true }
)

// Watch formData để emit changes
watch(
  formData,
  (newData) => {
    emit('update:formData', { ...newData })
  },
  { deep: true }
)

// Handlers
const handleNameInput = (event) => {
  formData.value.name = event.target.value
  if (!props.isEditMode) {
    formData.value.slug = generateSlugUtil(event.target.value)
  }
}

const handleSlugInput = (event) => {
  formData.value.slug = event.target.value
}

const handleBrandChange = (event) => {
  formData.value.brandId = event.target.value ? Number(event.target.value) : null
}

const handleStatusChange = (event) => {
  formData.value.isActive = event.target.value === 'true'
}

const handleMaterialChange = (event) => {
  formData.value.materialId = event.target.value
    ? Number(event.target.value)
    : null
}

const handleSoleChange = (event) => {
  formData.value.shoeSoleId = event.target.value
    ? Number(event.target.value)
    : null
}

const handleDescriptionInput = (event) => {
  formData.value.description = event.target.value
}

const handleCategoryChange = (categoryId, event) => {
  if (event.target.checked) {
    if (!formData.value.categoryIds.includes(categoryId)) {
      formData.value.categoryIds.push(categoryId)
    }
  } else {
    formData.value.categoryIds = formData.value.categoryIds.filter(
      (id) => id !== categoryId
    )
  }
}

const handleImagesChange = (images) => {
  emit('images-change', images)
}

const handleImageRemove = (payload) => {
  emit('image-remove', payload)
}

const handleAddVariant = () => {
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

const handleRemoveVariant = (index) => {
  formData.value.variants.splice(index, 1)
}

const handleVariantInput = (index, field, event) => {
  const value =
    field === 'priceBase' || field === 'priceSale' || field === 'stockQuantity'
      ? Number(event.target.value) || 0
      : event.target.value
  formData.value.variants[index][field] = value
}

const handleSubmit = () => {
  emit('submit', { ...formData.value })
}

const handleClose = () => {
  emit('update:visible', false)
  emit('close')
}
</script>


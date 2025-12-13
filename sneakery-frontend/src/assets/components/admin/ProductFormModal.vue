<template>
  <Teleport to="body">
    <div
      v-if="visible"
      class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
      @click="handleClose"
    >
      <div
        ref="modalRef"
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-5xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700 scrollbar-hide"
        @click.stop
        role="dialog"
        :aria-modal="true"
        :aria-labelledby="modalTitleId"
      >
        <div
          class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10"
        >
          <h2
            :id="modalTitleId"
            class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
          >
            <i class="material-icons text-purple-600 dark:text-purple-400">{{
              isEditMode ? "edit" : "add"
            }}</i>
            {{ isEditMode ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm mới" }}
          </h2>
          <button
            @click="handleClose"
            class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
          >
            <i class="material-icons text-base">close</i>
          </button>
        </div>

        <div class="p-6 space-y-6">
          <!-- Basic Info Section -->
          <div class="space-y-4">
            <h3
              class="text-base font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2 pb-2 border-b border-gray-200 dark:border-gray-700"
            >
              <i
                class="material-icons text-purple-600 dark:text-purple-400 text-lg"
                >info</i
              >
              Thông tin cơ bản
            </h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Tên sản phẩm -->
              <div class="space-y-2">
                <label
                  class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Tên sản phẩm <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="localFormData.name"
                  @input="handleNameInput"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                  placeholder="Ví dụ: Nike Air Force 1 '07"
                />
                <span
                  v-if="formErrors.name"
                  class="text-sm text-red-600 dark:text-red-400"
                  >{{ formErrors.name }}</span
                >
              </div>

              <!-- Slug -->
              <div class="space-y-2">
                <label
                  class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Slug <span class="text-red-500">*</span>
                </label>
                <input
                  v-model="localFormData.slug"
                  @input="handleSlugInput"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                  placeholder="nike-air-force-1-07"
                />
                <span
                  v-if="formErrors.slug"
                  class="text-sm text-red-600 dark:text-red-400"
                  >{{ formErrors.slug }}</span
                >
                <span class="text-xs text-gray-500 dark:text-gray-400"
                  >URL thân thiện (tự động tạo từ tên)</span
                >
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Thương hiệu -->
              <div class="space-y-2">
                <label
                  class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Thương hiệu <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select
                    v-model="localFormData.brandId"
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
                <span
                  v-if="formErrors.brandId"
                  class="text-sm text-red-600 dark:text-red-400"
                  >{{ formErrors.brandId }}</span
                >
              </div>

              <!-- Trạng thái -->
              <div class="space-y-2">
                <label
                  class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Trạng thái
                </label>
                <select
                  v-model="localFormData.isActive"
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
                <label
                  class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Chất liệu <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select
                    v-model="localFormData.materialId"
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
                <span
                  v-if="formErrors.materialId"
                  class="text-sm text-red-600 dark:text-red-400"
                  >{{ formErrors.materialId }}</span
                >
              </div>

              <!-- Loại đế giày -->
              <div class="space-y-2">
                <label
                  class="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Loại đế giày <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select
                    v-model="localFormData.shoeSoleId"
                    @change="handleSoleChange"
                    class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                  >
                    <option value="">Chọn loại đế giày</option>
                    <option
                      v-for="sole in soles"
                      :key="sole.id"
                      :value="sole.id"
                    >
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
                <span
                  v-if="formErrors.shoeSoleId"
                  class="text-sm text-red-600 dark:text-red-400"
                  >{{ formErrors.shoeSoleId }}</span
                >
              </div>
            </div>

            <!-- Mô tả -->
            <div class="space-y-2">
              <label
                class="block text-sm font-medium text-gray-700 dark:text-gray-300"
              >
                Mô tả
              </label>
              <textarea
                v-model="localFormData.description"
                @input="handleDescriptionInput"
                rows="4"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors resize-none"
                placeholder="Mô tả chi tiết về sản phẩm..."
              ></textarea>
            </div>

            <!-- Danh mục -->
            <!-- Danh mục -->
            <div class="space-y-2">
              <label
                class="block text-sm font-medium text-gray-700 dark:text-gray-300"
              >
                Danh mục <span class="text-red-500">*</span>
              </label>
              <div
                class="flex flex-wrap gap-3 p-3 border border-gray-300 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-800/50"
              >
                <label
                  v-for="category in childCategories"
                  :key="category.id"
                  :class="[
                    'flex items-center gap-2 px-3 py-1.5 rounded-lg border cursor-pointer transition-all duration-200',
                    localFormData.categoryIds.includes(category.id)
                      ? 'bg-purple-100 border-purple-400 dark:bg-purple-900/40 dark:border-purple-600 shadow-sm'
                      : 'bg-white dark:bg-gray-700 border-gray-300 dark:border-gray-600 hover:bg-purple-50 dark:hover:bg-purple-900/30 hover:border-purple-300 dark:hover:border-purple-500',
                  ]"
                >
                  <input
                    type="radio"
                    name="singleCategory"
                    :value="category.id"
                    :checked="localFormData.categoryIds.includes(category.id)"
                    @change="(e) => handleCategoryChange(category.id, e)"
                    class="w-4 h-4 text-purple-600 border-gray-300 focus:ring-purple-500"
                  />
                  <span
                    :class="[
                      'text-sm font-medium',
                      localFormData.categoryIds.includes(category.id)
                        ? 'text-purple-700 dark:text-purple-300'
                        : 'text-gray-700 dark:text-gray-300',
                    ]"
                  >
                    {{ category.name }}
                  </span>
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
              <span
                v-if="formErrors.categoryIds"
                class="text-sm text-red-600 dark:text-red-400"
              >
                {{ formErrors.categoryIds }}
              </span>
            </div>

            <!-- Thư viện hình ảnh -->
            <div class="space-y-2">
              <label
                class="block text-sm font-medium text-gray-700 dark:text-gray-300"
              >
                Thư viện ảnh bìa sản phẩm
              </label>
              <UploadGallery
                :initialImages="initialImages"
                :maxImages="maxImages"
                @change="handleImagesChange"
                @remove="handleImageRemove"
              />
              <span class="text-xs text-gray-500 dark:text-gray-400">
                Có thể tải ảnh từ máy hoặc nhập URL.
              </span>
            </div>
          </div>

          <!-- Variants Section -->
          <div class="space-y-4">
            <div
              class="flex items-center justify-between pb-2 border-b border-gray-200 dark:border-gray-700"
            >
              <h3
                class="text-base font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2"
              >
                <i
                  class="material-icons text-purple-600 dark:text-purple-400 text-lg"
                  >inventory</i
                >
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

            <div
              v-if="localFormData.variants.length === 0"
              class="text-center py-8 px-4 bg-gray-50 dark:bg-gray-800/50 rounded-lg border border-gray-200 dark:border-gray-700"
            >
              <i
                class="material-icons text-gray-400 dark:text-gray-500 text-4xl mb-2"
                >inventory_2</i
              >
              <p class="text-gray-600 dark:text-gray-400">
                Chưa có SPCT nào. Nhấn "Thêm SPCT" để tạo SPCT đầu tiên.
              </p>
              <span
                v-if="formErrors.variants"
                class="text-red-600 dark:text-red-400 text-sm block mt-2"
              >
                {{ formErrors.variants }}
              </span>
            </div>

            <div v-else class="space-y-4">
              <div
                v-for="(variant, index) in localFormData.variants"
                :key="variant.id ?? index"
                class="p-4 bg-gray-50 dark:bg-gray-800/50 rounded-lg border border-gray-200 dark:border-gray-700 space-y-4"
              >
                <div class="flex items-center justify-between">
                  <span
                    class="text-sm font-semibold text-gray-900 dark:text-gray-100"
                    >Variant #{{ index + 1 }}</span
                  >
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
                    <label
                      class="block text-xs font-medium text-gray-700 dark:text-gray-300"
                    >
                      SKU <span class="text-red-500">*</span>
                    </label>
                    <input
                      v-model="variant.sku"
                      type="text"
                      :readonly="true"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-gray-100 dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none"
                      placeholder="VD: NIKE-AF1-WHT-42"
                    />
                    <span
                      v-if="formErrorsLocal[`variant_${index}_sku`]"
                      class="text-xs text-red-500 block"
                    >
                      {{ formErrorsLocal[`variant_${index}_sku`] }}
                    </span>
                  </div>

                  <div class="space-y-2">
                    <label
                      class="block text-xs font-medium text-gray-700 dark:text-gray-300"
                    >
                      Size <span class="text-red-500">*</span>
                    </label>
                    <input
                      v-model="variant.size"
                      type="text"
                      readonly
                      @click="openSizePicker(index)"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      placeholder="VD: 42, 43, 44"
                    />
                    <span
                      v-if="formErrorsLocal[`variant_${index}_size`]"
                      class="text-xs text-red-500 block"
                    >
                      {{ formErrorsLocal[`variant_${index}_size`] }}
                    </span>
                  </div>

                  <div class="space-y-2">
                    <label
                      class="block text-xs font-medium text-gray-700 dark:text-gray-300"
                    >
                      Màu sắc <span class="text-red-500">*</span>
                    </label>
                    <input
                      v-model="variant.color"
                      type="text"
                      readonly
                      @click="openColorPicker(index)"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      placeholder="VD: Trắng, Đen"
                    />
                    <span
                      v-if="formErrorsLocal[`variant_${index}_color`]"
                      class="text-xs text-red-500 block"
                    >
                      {{ formErrorsLocal[`variant_${index}_color`] }}
                    </span>
                  </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div class="space-y-2">
                    <label
                      class="block text-xs font-medium text-gray-700 dark:text-gray-300"
                    >
                      Giá gốc (VNĐ) <span class="text-red-500">*</span>
                    </label>
                    <input
                      v-model="variant.priceBase"
                      type="number"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      min="0"
                      step="1000"
                    />
                    <span
                      v-if="formErrorsLocal[`variant_${index}_priceBase`]"
                      class="text-xs text-red-500 block"
                    >
                      {{ formErrorsLocal[`variant_${index}_priceBase`] }}
                    </span>
                  </div>

                  <div class="space-y-2">
                    <label
                      class="block text-xs font-medium text-gray-700 dark:text-gray-300"
                    >
                      Giá khuyến mãi (VNĐ)
                    </label>
                    <input
                      v-model="variant.priceSale"
                      type="number"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      min="0"
                      step="1000"
                    />
                    <span
                      v-if="formErrorsLocal[`variant_${index}_priceSale`]"
                      class="text-xs text-red-500 block"
                    >
                      {{ formErrorsLocal[`variant_${index}_priceSale`] }}
                    </span>
                  </div>

                  <div class="space-y-2">
                    <label
                      class="block text-xs font-medium text-gray-700 dark:text-gray-300"
                    >
                      Tồn kho <span class="text-red-500">*</span>
                    </label>
                    <input
                      v-model="variant.stockQuantity"
                      type="number"
                      class="w-full px-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-colors"
                      min="0"
                    />
                    <span
                      v-if="formErrorsLocal[`variant_${index}_stockQuantity`]"
                      class="text-xs text-red-500 block"
                    >
                      {{ formErrorsLocal[`variant_${index}_stockQuantity`] }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div
          class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800"
        >
          <button
            @click="handleClose"
            class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium"
          >
            Hủy
          </button>
          <button
            @click="handleSubmit"
            class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="submitting"
          >
            {{
              submitting ? "Đang lưu..." : isEditMode ? "Cập nhật" : "Thêm mới"
            }}
          </button>
        </div>
      </div>
    </div>
    <!-- Popup chọn màu -->
    <ColorPickerPopup
      :visible="showColorPopup"
      :initialSelected="formData.color"
      @select="handleColorSelected"
      @close="showColorPopup = false"
    />

    <!-- Popup chọn size -->
    <SizePickerPopup
      :visible="showSizePopup"
      :initialSelected="selectedSizes"
      @confirm="handleSizesConfirmed"
      @close="showSizePopup = false"
    />
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onUnmounted, nextTick } from "vue";
import { generateSlug as generateSlugUtil } from "@/utils/slugGenerator";
import { useFocusManagement } from "@/composables/useFocusManagement";
import {
  generateSku,
  extractBrandCode,
  extractModelCode,
  shortenColor,
} from "@/utils/skuGenerator"; // Giữ util của bạn
import UploadGallery from "./UploadGallery.vue";
import ColorPickerPopup from "@/assets/components/common/ColorPickerPopup.vue";
import SizePickerPopup from "@/assets/components/common/SizePickerPopup.vue";

const props = defineProps({
  visible: { type: Boolean, default: false },
  isEditMode: { type: Boolean, default: false },
  initialProduct: { type: Object, default: null },
  brands: { type: Array, default: () => [] },
  categories: { type: Array, default: () => [] },
  materials: { type: Array, default: () => [] },
  soles: { type: Array, default: () => [] },
  initialImages: { type: Array, default: () => [] },
  maxImages: { type: Number, default: 10 },
  formErrors: { type: Object, default: () => ({}) },
  submitting: { type: Boolean, default: false },
  formData: {
    type: Object,
    default: () => ({
      name: "",
      slug: "",
      brandId: null,
      description: "",
      isActive: true,
      categoryIds: [],
      materialId: null,
      shoeSoleId: null,
      priceFrom: null,
      priceTo: null,
      variants: [],
    }),
  },
});

const emit = defineEmits([
  "update:visible",
  "update:formData",
  "update:formErrors",
  "submit",
  "close",
  "quick-add-brand",
  "quick-add-material",
  "quick-add-sole",
  "quick-add-category",
  "images-change",
  "image-remove",
]);

// Focus management
const modalRef = ref(null);
const { setupModalFocus, cleanupModalFocus, saveActiveElement } =
  useFocusManagement();
const modalTitleId = computed(
  () => `product-modal-title-${Math.random().toString(36).substr(2, 9)}`
);

// Setup focus when modal opens
watch(
  () => props.visible,
  (isOpen) => {
    if (isOpen) {
      saveActiveElement();
      nextTick(() => {
        if (modalRef.value) {
          setupModalFocus(modalRef.value);
        }
      });
    } else {
      if (modalRef.value) {
        cleanupModalFocus(modalRef.value);
      }
    }
  }
);

onUnmounted(() => {
  if (modalRef.value) {
    cleanupModalFocus(modalRef.value);
  }
});

// Form data - sync với parent
// Form data - sync với parent
// const localFormData = ref({ ...props.formData });
const localFormData = ref(JSON.parse(JSON.stringify(props.formData)));
const formErrorsLocal = ref({});
const showColorPopup = ref(false);
const showSizePopup = ref(false);
const colorTargetIndex = ref(null);
const sizeTargetIndex = ref(null);
const selectedSizes = ref([]);

// ===== WATCHERS =====
watch(
  () => props.initialProduct,
  (product) => {
    // Debug logs (có thể xóa sau khi test)
    // console.log("🔄 ProductFormModal - Watch initialProduct triggered:", product);
    // console.log("🔄 isEditMode:", props.isEditMode);
    if (props.isEditMode && product) {
      // 🧹 Reset sạch trước khi fill
      localFormData.value = {
        id: null,
        name: "",
        slug: "",
        brandId: null,
        description: "",
        isActive: true,
        categoryIds: [],
        materialId: null,
        shoeSoleId: null,
        variants: [],
      };

      // ✅ Fill dữ liệu từ product (clone object tránh mutate)
      const clonedProduct = JSON.parse(JSON.stringify(product));
      localFormData.value = clonedProduct;

      // Debug logs (có thể xóa sau khi test)
      // console.log("✅ ProductFormModal - Loaded edit data:", localFormData.value);
      // console.log("✅ ProductFormModal - priceFrom:", localFormData.value.priceFrom);
      // console.log("✅ ProductFormModal - priceTo:", localFormData.value.priceTo);

      emit("update:formData", { ...localFormData.value });
    }
  },
  { immediate: true, deep: true } // ✅ Đổi thành deep: true để detect nested changes
);

watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible && !props.isEditMode) {
      localFormData.value = {
        name: "",
        slug: "",
        brandId: null,
        description: "",
        isActive: true,
        categoryIds: [],
        materialId: null,
        shoeSoleId: null,
        variants: [],
      };
      emit("update:formData", { ...localFormData.value });
      console.log("🆕 Reset form for create mode");
    }
  }
);

watch(
  localFormData,
  (newVal, oldVal) => {
    if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
      emit("update:formData", { ...newVal });
    }
  },
  { deep: true }
);

watch(
  () => props.formErrors,
  (newErrors) => {
    formErrorsLocal.value = { ...newErrors };
  },
  { deep: true, immediate: true }
);

// ===== HANDLERS (product fields) =====
const handleNameInput = () => {
  if (!props.isEditMode) {
    localFormData.value.slug = generateSlugUtil(localFormData.value.name);
  }
};
const handleSlugInput = (e) => (localFormData.value.slug = e.target.value);
const handleBrandChange = (e) =>
  (localFormData.value.brandId = e.target.value
    ? Number(e.target.value)
    : null);
const handleStatusChange = (e) =>
  (localFormData.value.isActive = e.target.value === "true");
const handleMaterialChange = (e) =>
  (localFormData.value.materialId = e.target.value
    ? Number(e.target.value)
    : null);
const handleSoleChange = (e) =>
  (localFormData.value.shoeSoleId = e.target.value
    ? Number(e.target.value)
    : null);
const handleDescriptionInput = (e) =>
  (localFormData.value.description = e.target.value);

const handleCategoryChange = (categoryId, event) => {
  if (event.target.checked) {
    localFormData.value.categoryIds = [categoryId]; // chỉ cho phép 1 danh mục
  } else {
    localFormData.value.categoryIds = [];
  }
  emit("update:formData", { ...localFormData.value }); // ✅ cập nhật lên cha ngay
};

// ===== Images
const handleImagesChange = (images) => emit("images-change", images);
const handleImageRemove = (payload) => emit("image-remove", payload);

// ===== VARIANTS =====
const handleAddVariant = () => {
  localFormData.value.variants.push({
    id: props.isEditMode ? undefined : null,
    sku: "",
    size: "",
    color: "",
    priceBase: 0,
    priceSale: null,
    stockQuantity: 0,
    imageUrl: "",
  });
};

const handleRemoveVariant = (index) => {
  localFormData.value.variants.splice(index, 1);
};

// Popup mở/đóng
const openColorPicker = (index) => {
  colorTargetIndex.value = index;
  showColorPopup.value = true;
};
const openSizePicker = (index) => {
  sizeTargetIndex.value = index;
  const currentSizes = localFormData.value.variants[index].size
    ? localFormData.value.variants[index].size.split(",").map((s) => s.trim())
    : [];
  selectedSizes.value = currentSizes;
  showSizePopup.value = true;
};

// Xử lý chọn màu/size
const handleColorSelected = (color) => {
  if (colorTargetIndex.value != null) {
    localFormData.value.variants[colorTargetIndex.value].color = color.name;
    autoGenerateSku(colorTargetIndex.value);
  }
};
const handleSizesConfirmed = (sizes) => {
  if (sizeTargetIndex.value != null) {
    const sizeString = sizes.join(", ");
    localFormData.value.variants[sizeTargetIndex.value].size = sizeString;
    autoGenerateSku(sizeTargetIndex.value);
  }
};

// Regex kiểm tra SKU hợp lệ theo chuẩn: BRAND-MODEL-COLOR-SIZE
const SKU_REGEX = /^[A-Z0-9]+-[A-Z0-9]+-[A-Z]+-[0-9A-Z]+$/;

// Auto SKU cho biến thể
const autoGenerateSku = (index) => {
  const v = localFormData.value.variants[index];
  const productName = localFormData.value.name || "";

  if (productName && v.color && v.size) {
    const brandPart = extractBrandCode(productName);
    const modelPart = extractModelCode(productName);
    const colorPart = shortenColor(v.color);
    const sizePart = String(v.size).split(",")[0]?.trim() || "";
    v.sku = `${brandPart}-${modelPart}-${colorPart}-${sizePart}`;

    // 🔍 Validate SKU format & update errors
    const updatedErrors = { ...props.formErrors };
    if (!SKU_REGEX.test(v.sku)) {
      updatedErrors[`variant_${index}_sku`] =
        "SKU không hợp lệ. Vui lòng kiểm tra Size & Màu.";
    } else {
      delete updatedErrors[`variant_${index}_sku`];
    }
    formErrorsLocal.value = { ...updatedErrors };
    emit("update:formErrors", updatedErrors);
  }
};

const validateForm = () => {
  const errors = {};

  // ===== Basic Fields =====
  if (!localFormData.value.name?.trim()) {
    errors.name = "Tên sản phẩm là bắt buộc";
  }

  if (!localFormData.value.slug?.trim()) {
    errors.slug = "Slug là bắt buộc";
  }

  if (!localFormData.value.brandId) {
    errors.brandId = "Thương hiệu là bắt buộc";
  }

  if (!localFormData.value.materialId) {
    errors.materialId = "Chất liệu là bắt buộc";
  }

  if (!localFormData.value.shoeSoleId) {
    errors.shoeSoleId = "Loại đế giày là bắt buộc";
  }

  if (!localFormData.value.categoryIds.length) {
    errors.categoryIds = "Phải chọn ít nhất 1 danh mục";
  }

  // ===== Variants =====
  if (!localFormData.value.variants.length) {
    errors.variants = "Cần có ít nhất 1 sản phẩm chi tiết";
  } else {
    localFormData.value.variants.forEach((v, i) => {
      if (!v.size) errors[`variant_${i}_size`] = "Chọn size";
      if (!v.color) errors[`variant_${i}_color`] = "Chọn màu";
      if (!v.sku || !SKU_REGEX.test(v.sku)) {
        errors[`variant_${i}_sku`] = !v.sku
          ? "SKU chưa được tạo"
          : "SKU không hợp lệ. Vui lòng chọn Size & Màu đúng chuẩn.";
      }
      if (
        v.stockQuantity == null ||
        isNaN(Number(v.stockQuantity)) ||
        Number(v.stockQuantity) < 0
      ) {
        errors[`variant_${i}_stockQuantity`] =
          "Tồn kho phải là số nguyên lớn hơn hoặc bằng 0";
      }
      // ===== Validate giá =====
      if (!v.priceBase || Number(v.priceBase) <= 0) {
        errors[`variant_${i}_priceBase`] = "Giá gốc phải lớn hơn 0";
      }

      if (!v.priceSale || Number(v.priceSale) <= 0) {
        errors[`variant_${i}_priceSale`] = "Giá bán phải lớn hơn 0";
      } else if (Number(v.priceSale) >= Number(v.priceBase)) {
        errors[`variant_${i}_priceSale`] = "Giá bán phải NHỎ HƠN giá gốc";
      }
    });
  }

  emit("update:formErrors", errors);

  return Object.keys(errors).length === 0;
};

// Submit/Close
const handleSubmit = () => {
  const isValid = validateForm();
  if (!isValid) {
    console.warn("❌ Có lỗi! Không submit!");
    return;
  }

  emit("update:formData", { ...localFormData.value });
  emit("submit", { ...localFormData.value });
};

const handleClose = () => {
  emit("update:visible", false);
  emit("close");
  localFormData.value = {
    id: null,
    name: "",
    slug: "",
    brandId: null,
    description: "",
    isActive: true,
    categoryIds: [],
    materialId: null,
    shoeSoleId: null,
    priceFrom: null,
    priceTo: null,
    variants: [],
  };
};

// Computed
const childCategories = computed(() =>
  props.categories.filter((cat) => cat.parentId != null)
);
</script>

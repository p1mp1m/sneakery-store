<template>
  <div
    v-show="isOpen"
    class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
    @click="handleOverlayClose"
  >
    <div
      class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700 scrollbar-hide"
      @click.stop
      @mousedown="handleModalClick"
    >
      <div
        class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10"
      >
        <h3
          class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400">{{
            isEdit ? "edit" : "add"
          }}</i>
          {{ isEdit ? "Chỉnh sửa Biến Thể" : "Thêm Biến Thể Mới" }}
        </h3>
        <button
          type="button"
          class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
          @click="handleCancel"
        >
          <i class="material-icons text-base">close</i>
        </button>
      </div>

      <div class="p-6">
        <form @submit.prevent="handleSubmit" novalidate class="space-y-6">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >
                Sản phẩm <span class="text-red-500">*</span>
              </label>
              <template v-if="!isEdit">
                <div class="relative" ref="productSelectRef">
                  <!-- Ô nhập để tìm kiếm -->
                  <input
                    v-model="searchProductText"
                    type="text"
                    class="px-3 py-2 bg-white dark:bg-gray-700 border rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent w-full"
                    placeholder="Nhập tên sản phẩm để tìm..."
                    @focus="showProductDropdown = true"
                    @input="filterProducts"
                  />

                  <!-- Dropdown kết quả -->
                  <div
                    v-show="showProductDropdown"
                    class="absolute z-50 w-full bg-white dark:bg-gray-800 max-h-60 overflow-y-auto border border-gray-300 dark:border-gray-700 rounded-lg shadow-lg mt-1 scrollbar-hide"
                  >
                    <div
                      v-for="p in filteredProducts"
                      :key="p.id"
                      class="px-3 py-2 text-sm cursor-pointer hover:bg-purple-100 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-300"
                      @click="selectProduct(p)"
                    >
                      {{ p.code }} - {{ p.name }} - {{ p.brandName }}
                    </div>

                    <div
                      v-if="filteredProducts.length === 0"
                      class="px-3 py-2 text-sm text-gray-500 dark:text-gray-400 italic"
                    >
                      Không tìm thấy sản phẩm phù hợp
                    </div>
                  </div>
                </div>
              </template>

              <template v-else>
                <div
                  class="px-3 py-2 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg"
                >
                  <span class="text-sm text-gray-700 dark:text-gray-300">
                    {{ getProductName(formData.productId) }}
                  </span>
                </div>
              </template>
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="errors.productId"
                  class="text-xs text-red-500 dark:text-red-400 mt-1"
                >
                  {{ errors.productId }}
                </p>
              </transition>
            </div>

            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >
                SKU <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.sku"
                type="text"
                :class="[
                  'px-3 py-2 bg-white dark:bg-gray-700 border rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent',
                  errors.sku
                    ? 'border-red-500 dark:border-red-500'
                    : 'border-gray-300 dark:border-gray-600',
                ]"
                placeholder="Ví dụ: ADIDA-ULTRA22-WHI-42"
                required
                @focus="isSkuFocused = true"
                @blur="
                  isSkuFocused = false;
                  validateSku();
                "
                @input="handleSkuInput"
              />
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0"
                enter-to-class="opacity-100"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100"
                leave-to-class="opacity-0"
              >
                <small
                  v-if="isSkuFocused && !errors.sku"
                  class="text-xs text-gray-500 dark:text-gray-400 mt-1"
                >
                  SKU được <strong>tạo tự động</strong> dựa trên thông tin sản
                  phẩm, màu và size — bạn có thể chỉnh
                  <strong>thủ công</strong> nếu muốn.
                </small>
              </transition>
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="errors.skuFormat"
                  class="text-xs text-amber-500 dark:text-amber-400 mt-1"
                >
                  {{ errors.skuFormat }}
                </p>
              </transition>
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="errors.sku"
                  class="text-xs text-red-500 dark:text-red-400 mt-1"
                >
                  {{ errors.sku }}
                </p>
              </transition>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >
                Màu sắc <span class="text-red-500">*</span>
              </label>
              <div class="relative" @click="showColorPopup = true">
                <input
                  v-model="formData.color"
                  type="text"
                  :class="[
                    'px-3 py-2 pr-10 bg-white dark:bg-gray-700 border rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent cursor-pointer',
                    errors.color
                      ? 'border-red-500 dark:border-red-500'
                      : 'border-gray-300 dark:border-gray-600',
                  ]"
                  readonly
                  placeholder="Chọn màu sắc"
                  @blur="validateColor"
                />
                <i
                  class="material-icons absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 pointer-events-none"
                  >palette</i
                >
              </div>
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="errors.color"
                  class="text-xs text-red-500 dark:text-red-400 mt-1"
                >
                  {{ errors.color }}
                </p>
              </transition>
            </div>

            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >
                Kích thước <span class="text-red-500">*</span>
              </label>
              <div class="relative" @click="showSizePopup = true">
                <input
                  v-model="formData.size"
                  type="text"
                  :class="[
                    'px-3 py-2 pr-10 bg-white dark:bg-gray-700 border rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent cursor-pointer',
                    sizeError
                      ? 'border-red-500 dark:border-red-500'
                      : 'border-gray-300 dark:border-gray-600',
                  ]"
                  readonly
                  placeholder="Chọn kích thước"
                  @blur="validateSize"
                />
                <i
                  class="material-icons absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 pointer-events-none"
                  >straighten</i
                >
              </div>
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="sizeError"
                  class="text-xs text-red-500 dark:text-red-400 mt-1"
                >
                  {{ sizeError }}
                </p>
              </transition>
            </div>
            <!-- Popup chọn màu -->
            <div
              v-if="showColorPopup"
              class="fixed inset-0 z-[10000] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
              @click.self="showColorPopup = false"
            >
              <div
                class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-md w-full border border-gray-200 dark:border-gray-700"
              >
                <div class="p-4 border-b border-gray-200 dark:border-gray-700">
                  <h4
                    class="text-base font-semibold text-gray-900 dark:text-gray-100"
                  >
                    Chọn màu sắc
                  </h4>
                </div>
                <div class="p-4 grid grid-cols-4 gap-3">
                  <div
                    v-for="(c, idx) in availableColors"
                    :key="idx"
                    class="w-12 h-12 rounded-full cursor-pointer border-2 transition-all"
                    :style="{ backgroundColor: c.hex }"
                    @click="selectColor(c)"
                    :class="
                      formData.color === c.name
                        ? 'border-purple-500 ring-2 ring-purple-300 dark:ring-purple-700 scale-110'
                        : 'border-gray-300 dark:border-gray-600 hover:scale-105'
                    "
                  ></div>
                </div>
              </div>
            </div>

            <!-- Popup chọn kích thước -->
            <div
              v-if="showSizePopup"
              class="fixed inset-0 z-[10000] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
              @click.self="handleSizePopupClose"
            >
              <div
                class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-md w-full border border-gray-200 dark:border-gray-700"
              >
                <div
                  class="p-4 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between"
                >
                  <h4
                    class="text-base font-semibold text-gray-900 dark:text-gray-100"
                  >
                    Chọn kích thước
                  </h4>
                  <button
                    type="button"
                    class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
                    @click="handleSizePopupClose"
                    title="Đóng (ESC)"
                  >
                    <i class="material-icons text-base">close</i>
                  </button>
                </div>
                <div class="p-4 grid grid-cols-5 gap-2">
                  <button
                    type="button"
                    v-for="(s, idx) in availableSizes"
                    :key="idx"
                    class="px-4 py-2 text-sm font-medium rounded-lg transition-all"
                    :class="
                      selectedSizes.includes(s)
                        ? 'bg-purple-500 text-white ring-2 ring-purple-300 dark:ring-purple-700'
                        : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
                    "
                    @click="toggleSize(s)"
                  >
                    {{ s }}
                  </button>
                </div>
                <div v-if="selectedSizes.length === 0" class="px-4 pb-2">
                  <p class="text-xs text-red-500 dark:text-red-400">
                    Vui lòng chọn ít nhất một kích thước
                  </p>
                </div>
                <div
                  class="p-4 border-t border-gray-200 dark:border-gray-700 flex justify-end gap-2"
                >
                  <button
                    type="button"
                    class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-all duration-200 text-sm font-medium"
                    @click="handleSizePopupClose"
                  >
                    Hủy
                  </button>
                  <button
                    type="button"
                    class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
                    @click="confirmSizes"
                    :disabled="selectedSizes.length === 0"
                  >
                    Xác nhận
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >
                Giá gốc (VNĐ) <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="formData.priceBase"
                type="number"
                :class="[
                  'px-3 py-2 bg-white dark:bg-gray-700 border rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent',
                  errors.priceBase
                    ? 'border-red-500 dark:border-red-500'
                    : 'border-gray-300 dark:border-gray-600',
                ]"
                placeholder="3500000"
                min="0"
                required
                @blur="validatePriceBase"
                @input="validatePriceBase"
              />
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="errors.priceBase"
                  class="text-xs text-red-500 dark:text-red-400 mt-1"
                >
                  {{ errors.priceBase }}
                </p>
              </transition>
            </div>

            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300"
                >Giá khuyến mãi (VNĐ)</label
              >
              <input
                v-model.number="formData.priceSale"
                type="number"
                :class="[
                  'px-3 py-2 bg-white dark:bg-gray-700 border rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent',
                  errors.priceSale
                    ? 'border-red-500 dark:border-red-500'
                    : 'border-gray-300 dark:border-gray-600',
                ]"
                placeholder="3000000"
                min="0"
                @blur="validatePriceSale"
                @input="validatePriceSale"
              />
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="errors.priceSale"
                  class="text-xs text-red-500 dark:text-red-400 mt-1"
                >
                  {{ errors.priceSale }}
                </p>
              </transition>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >
                Số lượng tồn kho <span class="text-red-500">*</span>
              </label>
              <input
                v-model.number="formData.stockQuantity"
                type="number"
                :class="[
                  'px-3 py-2 bg-white dark:bg-gray-700 border rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent',
                  errors.stockQuantity
                    ? 'border-red-500 dark:border-red-500'
                    : 'border-gray-300 dark:border-gray-600',
                ]"
                placeholder="15"
                min="0"
                required
                @blur="validateStockQuantity"
                @input="validateStockQuantity"
              />
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="errors.stockQuantity"
                  class="text-xs text-red-500 dark:text-red-400 mt-1"
                >
                  {{ errors.stockQuantity }}
                </p>
              </transition>
            </div>

            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300"
                >Ngưỡng cảnh báo</label
              >
              <input
                v-model.number="formData.lowStockThreshold"
                type="number"
                :class="[
                  'px-3 py-2 bg-white dark:bg-gray-700 border rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent',
                  errors.lowStockThreshold
                    ? 'border-red-500 dark:border-red-500'
                    : 'border-gray-300 dark:border-gray-600',
                ]"
                placeholder="10"
                min="0"
                @blur="validateLowStockThreshold"
                @input="validateLowStockThreshold"
              />
              <transition
                enter-active-class="transition-all duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-1"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-1"
              >
                <p
                  v-if="errors.lowStockThreshold"
                  class="text-xs text-red-500 dark:text-red-400 mt-1"
                >
                  {{ errors.lowStockThreshold }}
                </p>
              </transition>
            </div>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
              >Ảnh biến thể</label
            >
            <UploadGallery
              :resetKey="resetKey"
              :initialImages="galleryInitial"
              @change="handleImagesChanged"
              @remove="handleImageRemoved"
            />
          </div>

          <div class="flex items-center gap-3">
            <input
              v-model="formData.isActive"
              type="checkbox"
              class="w-5 h-5 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
            />
            <label
              class="text-sm text-gray-700 dark:text-gray-300 cursor-pointer"
              >Kích hoạt biến thể</label
            >
          </div>
        </form>
      </div>

      <div
        class="flex items-center justify-end gap-2 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800"
      >
        <button
          type="button"
          class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          @click="handleCancel"
        >
          Hủy
        </button>
        <button
          type="button"
          class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
          @click="handleSubmit"
          :disabled="isSubmitting || hasErrors"
          :title="
            hasErrors ? 'Vui lòng sửa các lỗi trong form trước khi submit' : ''
          "
        >
          <i class="material-icons text-base" v-if="isSubmitting"
            >hourglass_empty</i
          >
          <i class="material-icons text-base" v-else>{{
            isEdit ? "save" : "add"
          }}</i>
          {{ isSubmitting ? "Đang xử lý..." : isEdit ? "Cập nhật" : "Tạo mới" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import {
  ref,
  reactive,
  watch,
  onMounted,
  onUnmounted,
  computed,
  onErrorCaptured,
} from "vue";
import { useAdminStore } from "@/stores/admin";
// import notificationService from "@/utils/notificationService";
import notificationService from "@/utils/notificationService";
import UploadGallery from "@/assets/components/admin/UploadGallery.vue";
import {
  generateSku,
  extractBrandCode,
  extractModelCode,
  shortenColor,
} from "@/utils/skuGenerator";

// ===== PROPS & EMITS =====
const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
  variant: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(["close", "success"]);

const handleOverlayClose = () => {
  if (isEdit.value) {
    // Nếu đang chỉnh sửa → reset form trước khi đóng
    resetForm();
  }
  // Dù là thêm mới hay chỉnh sửa đều đóng modal
  emit("close");
};

const handleCancel = () => {
  // ✅ Đóng popup + reset toàn bộ dữ liệu
  resetForm();
  emit("close");
};

const getProductName = (id) => {
  const p = products.value.find((p) => p.id === id);
  return p ? `${p.name} - ${p.brandName}` : "Không xác định";
};

// ===== STORE =====
const adminStore = useAdminStore();

// ===== STATE =====
const isSubmitting = ref(false);
const products = ref([]);
const isSkuFocused = ref(false);
const selectedSizes = ref([]);
const selectedImages = ref([]); // 🖼️ lưu danh sách ảnh từ UploadGallery
const resetKey = ref(0);
// ✅ cho UploadGallery
const galleryInitial = ref([]); // danh sách ảnh khởi tạo: [{ previewUrl, isPrimary, type }]
const removedImageUrls = ref([]); // lưu các URL bị xóa (ảnh DB)
const sizeError = ref(""); // Error message cho size field
const previousSizeValue = ref(""); // Lưu giá trị size trước khi mở popup

// Tìm kiếm sản phẩm
const searchProductText = ref("");
const showProductDropdown = ref(false);

const filteredProducts = ref([]);

// ✅ ref để biết vùng select sản phẩm
const productSelectRef = ref(null);

const handleModalClick = (event) => {
  // Nếu có ref và click KHÔNG nằm trong block select sản phẩm → đóng dropdown
  if (
    productSelectRef.value &&
    !productSelectRef.value.contains(event.target)
  ) {
    showProductDropdown.value = false;
  }
};

// ===== VALIDATION ERRORS =====
const errors = reactive({
  productId: "",
  sku: "",
  color: "",
  size: "",
  priceBase: "",
  priceSale: "",
  stockQuantity: "",
  lowStockThreshold: "",
  skuFormat: "",
});

const formData = reactive({
  productId: "",
  sku: "",
  color: "",
  size: "",
  priceBase: 0,
  priceSale: null,
  costPrice: null,
  stockQuantity: 0,
  lowStockThreshold: 10,
  weightGrams: null,
  imageUrl: "",
  isActive: true,
});

// ===== Popup chọn màu & kích thước =====
const showColorPopup = ref(false);
const showSizePopup = ref(false);

// Watch để lưu giá trị size trước khi mở popup
watch(showSizePopup, (isOpen) => {
  if (isOpen) {
    // Lưu giá trị hiện tại trước khi mở popup
    previousSizeValue.value = formData.size || "";
    // Parse size hiện tại thành array nếu có
    if (formData.size) {
      selectedSizes.value = formData.size
        .split(",")
        .map((s) => parseInt(s.trim()))
        .filter((s) => !isNaN(s));
    } else {
      selectedSizes.value = [];
    }
  }
});

// Xử lý ESC key để đóng popup
const handleEscKey = (event) => {
  if (event.key === "Escape") {
    if (showSizePopup.value) {
      handleSizePopupClose();
    }
    if (showColorPopup.value) {
      showColorPopup.value = false;
    }
  }
};

// Thêm event listener khi component mount
onMounted(() => {
  document.addEventListener("keydown", handleEscKey);
});

// Remove event listener khi component unmount
onUnmounted(() => {
  document.removeEventListener("keydown", handleEscKey);
});

const availableColors = [
  { name: "Black", hex: "#000000" },
  { name: "White", hex: "#ffffff" },
  { name: "Red", hex: "#ff4b4b" },
  { name: "Blue", hex: "#3b82f6" },
  { name: "Green", hex: "#22c55e" },
  { name: "Yellow", hex: "#facc15" },
  { name: "Purple", hex: "#a855f7" },
  { name: "Pink", hex: "#ec4899" },
];

const availableSizes = [35, 36, 37, 38, 39, 40, 41, 42, 43, 44];

// Màu vẫn chọn đơn
const selectColor = (color) => {
  formData.color = color.name;
  showColorPopup.value = false;
  validateColor(); // Validate khi chọn màu
};

// Multi-size logic (đã giải thích ở trên)

// const toggleSize = (size) => {
//   const i = selectedSizes.value.indexOf(size);
//   if (i > -1) selectedSizes.value.splice(i, 1);
//   else selectedSizes.value.push(size);
//   // Clear error khi user chọn size
//   if (selectedSizes.value.length > 0) {
//     sizeError.value = "";
//   }
// };
const toggleSize = (size) => {
  if (isEdit.value) {
    // ❌ không cho chọn nhiều size khi đang EDIT
    selectedSizes.value = [size];
    formData.size = String(size);
  } else {
    // CREATE: cho chọn nhiều size để tạo nhiều biến thể
    const idx = selectedSizes.value.indexOf(size);
    if (idx === -1) selectedSizes.value.push(size);
    else selectedSizes.value.splice(idx, 1);
  }
  sizeError.value = "";
};

// ===== VALIDATION FUNCTIONS =====
const validateProductId = () => {
  if (!formData.productId || formData.productId === "") {
    errors.productId = "Vui lòng chọn sản phẩm";
    return false;
  }
  errors.productId = "";
  return true;
};

const validateSku = () => {
  if (!formData.sku || formData.sku.trim() === "") {
    errors.sku = "Vui lòng nhập SKU";
    return false;
  }
  if (formData.sku.length < 3) {
    errors.sku = "SKU phải có ít nhất 3 ký tự";
    return false;
  }
  errors.sku = "";
  return true;
};

const validateSkuFormat = () => {
  const SKU_REGEX = /^[A-Z0-9]{2,10}-[A-Z0-9]{2,10}-[A-Z0-9]{2,6}-\d{1,3}$/;

  if (formData.sku && !SKU_REGEX.test(formData.sku)) {
    errors.skuFormat = "Định dạng SKU không đúng theo chuẩn hệ thống";
    return false;
  }

  errors.skuFormat = "";
  return true;
};

const validateColor = () => {
  if (!formData.color || formData.color.trim() === "") {
    errors.color = "Vui lòng chọn màu sắc";
    return false;
  }
  errors.color = "";
  return true;
};

const validateSize = () => {
  if (!formData.size || String(formData.size).trim() === "") {
    sizeError.value = "Vui lòng chọn kích thước";
    errors.size = "Vui lòng chọn kích thước";
    return false;
  }
  sizeError.value = "";
  errors.size = "";
  return true;
};

const validatePriceBase = () => {
  if (!formData.priceBase || formData.priceBase <= 0) {
    errors.priceBase = "Giá gốc phải lớn hơn 0";
    return false;
  }
  if (formData.priceBase > 1000000000) {
    errors.priceBase = "Giá gốc không được vượt quá 1 tỷ VNĐ";
    return false;
  }
  errors.priceBase = "";
  return true;
};

const validatePriceSale = () => {
  if (formData.priceSale !== null && formData.priceSale !== "") {
    if (formData.priceSale < 0) {
      errors.priceSale = "Giá khuyến mãi không được âm";
      return false;
    }
    if (formData.priceSale >= formData.priceBase) {
      errors.priceSale = "Giá khuyến mãi phải nhỏ hơn giá gốc";
      return false;
    }
    if (formData.priceSale > 1000000000) {
      errors.priceSale = "Giá khuyến mãi không được vượt quá 1 tỷ VNĐ";
      return false;
    }
  }
  errors.priceSale = "";
  return true;
};

const validateStockQuantity = () => {
  if (formData.stockQuantity === null || formData.stockQuantity === undefined) {
    errors.stockQuantity = "Vui lòng nhập số lượng tồn kho";
    return false;
  }
  if (formData.stockQuantity < 0) {
    errors.stockQuantity = "Số lượng tồn kho không được âm";
    return false;
  }
  if (!Number.isInteger(formData.stockQuantity)) {
    errors.stockQuantity = "Số lượng tồn kho phải là số nguyên";
    return false;
  }
  errors.stockQuantity = "";
  return true;
};

const validateLowStockThreshold = () => {
  if (
    formData.lowStockThreshold !== null &&
    formData.lowStockThreshold !== undefined
  ) {
    if (formData.lowStockThreshold < 0) {
      errors.lowStockThreshold = "Ngưỡng cảnh báo không được âm";
      return false;
    }
    if (!Number.isInteger(formData.lowStockThreshold)) {
      errors.lowStockThreshold = "Ngưỡng cảnh báo phải là số nguyên";
      return false;
    }
  }
  errors.lowStockThreshold = "";
  return true;
};

// Validate tất cả fields
const validateAll = () => {
  const validations = [
    validateProductId(),
    validateSku(),
    validateColor(),
    validateSize(),
    validatePriceBase(),
    validatePriceSale(),
    validateStockQuantity(),
    validateLowStockThreshold(),
  ];
  return validations.every((v) => v === true);
};

// Clear tất cả errors
const clearAllErrors = () => {
  errors.productId = "";
  errors.sku = "";
  errors.color = "";
  errors.size = "";
  errors.priceBase = "";
  errors.priceSale = "";
  errors.stockQuantity = "";
  errors.lowStockThreshold = "";
  sizeError.value = "";
};

const handleSizePopupClose = () => {
  // Nếu đóng popup mà không chọn size (hoặc bỏ hết chọn), reset về giá trị cũ
  if (selectedSizes.value.length === 0) {
    formData.size = previousSizeValue.value;
    // Validate sau khi đóng
    if (!formData.size || formData.size.trim() === "") {
      sizeError.value = "Vui lòng chọn kích thước";
    }
  }
  showSizePopup.value = false;
};

const confirmSizes = () => {
  if (selectedSizes.value.length === 0) {
    notificationService.warning(
      "Cảnh báo",
      "Vui lòng chọn ít nhất một kích thước"
    );
    sizeError.value = "Vui lòng chọn ít nhất một kích thước";
    errors.size = "Vui lòng chọn ít nhất một kích thước";
    return;
  }
  // formData.size = selectedSizes.value.join(", ");
  if (isEdit.value) {
    formData.size = selectedSizes.value[0];
  } else {
    formData.size = selectedSizes.value.map((s) => String(s)).join(", ");
  }

  sizeError.value = ""; // Clear error khi confirm thành công
  errors.size = "";
  showSizePopup.value = false;
};

// const handleImageRemoved = (url) => {
//   if (!removedImageUrls.value.includes(url)) {
//     removedImageUrls.value.push(url);
//   }
// };
const handleImageRemoved = (image) => {
  // image = { id, previewUrl, type }
  removedImageUrls.value.push(image);
};

const isEdit = computed(() => !!props.variant);

// Computed để kiểm tra xem form có lỗi không
const hasErrors = computed(() => {
  return Object.values(errors).some((error) => error !== "");
});

// ===== IMAGE UPLOAD =====
// const handleImageUploaded = (imageData) => {
//   if (imageData && imageData.imageUrl) {
//     formData.imageUrl = imageData.imageUrl;
//     notificationService.success('Thành công',{
//       message: "Đã cập nhật đường dẫn ảnh cho biến thể",
//       duration: 3000,
//     });
//   }
// };
const handleImagesChanged = (list) => {
  // list = [{ file | null, previewUrl, isPrimary, type: 'local'|'url' }, ...]
  selectedImages.value = Array.isArray(list) ? list : [];
  // console.log để kiểm tra:
  console.log("🖼️ Selected images from gallery:", selectedImages.value);
};
//Ảnh local: { file: File, previewUrl: 'blob:...', isPrimary: bool, type: 'local' }
//Ảnh URL: { file: null, previewUrl: 'https://...', isPrimary: bool, type: 'url' }

// ===== LOAD PRODUCTS =====
const loadProducts = async () => {
  try {
    const result = await adminStore.fetchProducts(0, 1000);
    products.value = result.content || [];
  } catch (error) {
    console.error("Error loading products:", error);
    notificationService.error("Lỗi", "Không thể tải danh sách sản phẩm", {
      duration: 3000,
    });
  }
};

// ===== RESET & POPULATE =====
const resetForm = () => {
  Object.assign(formData, {
    productId: "",
    sku: "",
    color: "",
    size: "",
    priceBase: 0,
    priceSale: null,
    costPrice: null,
    stockQuantity: 0,
    lowStockThreshold: 10,
    weightGrams: null,
    imageUrl: "",
    isActive: true,
  });
  // ✅ Đồng thời reset các lựa chọn popup
  selectedSizes.value = [];
  showColorPopup.value = false;
  showSizePopup.value = false;
  galleryInitial.value = [];
  resetKey.value++; // ép component con reset lại gallery
  removedImageUrls.value = [];
  sizeError.value = ""; // Reset error
  isSkuManuallyChanged.value = false;
  // isExistingSku.value = false;
  previousSizeValue.value = ""; // Reset previous value
  clearAllErrors(); // Clear tất cả validation errors
};

// const isExistingSku = ref(false);

const populateForm = (variant) => {
  Object.assign(formData, {
    productId: variant.productId || "",
    sku: variant.sku || "",
    color: variant.color || "",
    size: variant.size || "",
    priceBase: variant.priceBase || 0,
    priceSale: variant.priceSale || null,
    costPrice: variant.costPrice || null,
    stockQuantity: variant.stockQuantity || 0,
    lowStockThreshold: variant.lowStockThreshold || 10,
    weightGrams: variant.weightGrams || null,
    imageUrl: variant.imageUrl || "",
    isActive: variant.isActive !== false,
  });
  isSkuManuallyChanged.value = false;
  // isExistingSku.value = true;
  validateSkuFormat();
};

// ===== MODAL LIFECYCLE =====
const filterProducts = () => {
  const keyword = searchProductText.value.toLowerCase().trim();
  if (!keyword) {
    filteredProducts.value = products.value;
    return;
  }
  filteredProducts.value = products.value.filter((p) => {
    const name = p.name?.toLowerCase() || "";
    const brand = p.brandName?.toLowerCase() || "";
    const code = p.code?.toLowerCase() || "";

    return (
      name.includes(keyword) ||
      brand.includes(keyword) ||
      code.includes(keyword)
    );
  });
};

const selectProduct = (p) => {
  formData.productId = p.id;
  searchProductText.value = `${p.name} - ${p.brandName}`;
  showProductDropdown.value = false;
  validateProductId();
};

// Ẩn dropdown khi click ra ngoài
// document.addEventListener("click", (e) => {
//   const dropdown = document.querySelector(".product-dropdown");
//   if (!e.target.closest(".relative")) {
//     showProductDropdown.value = false;
//   }
// });

// Khi mở modal thì reset
// watch(
//   () => props.isOpen,
//   (open) => {
//     if (open) {
//       searchProductText.value = "";
//       filteredProducts.value = products.value;
//     }
//   }
// );

watch(
  () => props.isOpen,
  async (open) => {
    if (!open) return;

    await loadProducts();

    // Mặc định: rỗng (Create)
    galleryInitial.value = [];
    searchProductText.value = "";
    filteredProducts.value = products.value;
    resetKey.value++; // ép con reset sạch

    // Edit mode
    if (props.variant && props.variant.productId) {
      // 1) Nạp dữ liệu form
      populateForm(props.variant);

      // 2) Fetch ảnh sản phẩm từ DB
      // const initial = await loadProductImagesFromDB(props.variant.productId);

      // 3) Gán cho UploadGallery
      // galleryInitial.value = initial;

      // 4) Ép UploadGallery nạp lại từ initialImages
      // resetKey.value++;
    }

    if (props.variant) {
      // Edit mode: nạp form
      populateForm(props.variant);
      validateSkuFormat();

      try {
        // Lấy ảnh của product (không thay primary ở đây)
        const { data } = await axios.get(
          `/api/admin/variant-images/${props.variant.id}`
        );
        // Map về dạng UploadGallery hiểu
        galleryInitial.value = (data || []).map((it) => ({
          previewUrl: it.imageUrl,
          isPrimary: false, // Variant không có ảnh bìa
          type: "db",
          id: it.id,
          displayOrder: it.displayOrder,
        }));

        // Ép UploadGallery nạp lại từ initialImages mới
        resetKey.value++;
      } catch (err) {
        console.error("Không tải được ảnh từ DB:", err);
        notificationService.error(
          "Lỗi",
          "Không thể tải thư viện ảnh của sản phẩm"
        );
      }
    }
  }
);

// ====== AUTO GENERATE SKU ======
const isSkuManuallyChanged = ref(false);

const autoGenerateSku = () => {
  if (!formData.productId || !formData.color || !formData.size) return;

  const product = products.value.find((p) => p.id === formData.productId);
  if (!product) return;

  const brandPart = extractBrandCode(product.name);
  const modelPart = extractModelCode(product.name);
  const colorPart = shortenColor(formData.color);
  const sizePart = String(formData.size).trim();

  formData.sku = `${brandPart}-${modelPart}-${colorPart}-${sizePart}`;
  validateSkuFormat();
};

// Watch SKU để phát hiện chỉnh tay
watch(
  [() => formData.productId, () => formData.color, () => formData.size],
  ([newPid, newColor, newSize], [oldPid, oldColor, oldSize]) => {
    // Nếu user đã gõ tay vào ô SKU thì không auto nữa
    if (isSkuManuallyChanged.value) return;

    // Edit mode: lần đầu populate từ DB ("" → value) thì KHÔNG generate,
    // để giữ đúng SKU đang có trong DB
    if (isEdit.value && oldPid === "" && oldColor === "" && oldSize === "") {
      return;
    }

    // Thiếu dữ liệu thì khỏi làm
    if (!newPid || !newColor || !newSize) return;

    autoGenerateSku();
    validateSkuFormat();
  },
  { immediate: false }
);

const handleSkuInput = () => {
  isSkuManuallyChanged.value = true;
  validateSku();
  validateSkuFormat();
};

// ===== SUBMIT =====
const handleSubmit = async () => {
  try {
    // ==== Validate tất cả fields trước khi submit ====
    if (!validateAll()) {
      notificationService.warning({
        message: "Vui lòng kiểm tra và sửa các lỗi trong form",
        duration: 3000,
      });
      return;
    }

    if (!validateSkuFormat()) {
      return;
    }

    // ==== Validate ảnh ====
    if (selectedImages.value.length > 10) {
      notificationService.warning("Cảnh báo", "Tối đa 10 ảnh");
      return;
    }

    isSubmitting.value = true;

    // ==== xóa ảnh đã đánh dấu xóa (nếu có) ====
    if (removedImageUrls.value.length > 0) {
      for (const item of removedImageUrls.value) {
        if (!item?.id) continue;
        try {
          await axios.delete(`/api/admin/variant-images/${item.id}`);
          console.log("🗑️ Đã xóa ảnh ID:", item.id);
        } catch (e) {
          console.error("❌ Xóa ảnh lỗi:", item.id, e);
        }
      }
      removedImageUrls.value = [];
    }

    // ==== tính trạng thái primary hiện có ở DB (sau khi trừ ảnh đã xóa) ====
    // const dbImagesEffective = (galleryInitial.value || []).filter(
    //   (it) => !removedImageUrls.value.includes(it.previewUrl)
    // );
    // const dbHasPrimary = dbImagesEffective.some((it) => it.isPrimary === true);

    // ==== upload/lưu ảnh mới ====
    const uploadedUrls = [];
    // Lưu ý: KHÔNG tự set primary nếu dbHasPrimary === true
    //        Nếu dbHasPrimary === false (VD: sp mới/đã xóa hết), cứ gửi isPrimary theo user chọn;
    //        Nếu user không chọn, BE vẫn auto primary khi existingCount == 0.

    for (const [idx, img] of selectedImages.value.entries()) {
      // Skip ảnh đã có DB
      if (img.type === "db") continue;

      // 🛑 Nếu đang ở CREATE (props.variant == null) → không upload ảnh
      if (!isEdit.value) {
        console.warn("⏳ CREATE mode: skip upload ảnh vì chưa có variantId");
        continue;
      }

      // 🟢 EDIT MODE ⇒ chỉ khi props.variant.id tồn tại
      const variantId = props.variant?.id;
      if (!variantId) {
        console.error("❌ Không tìm thấy variantId ở EDIT MODE!");
        continue;
      }

      // 🖼 Ảnh local
      if (img.file) {
        const formUpload = new FormData();
        formUpload.append("file", img.file);
        formUpload.append("variantId", variantId);
        formUpload.append("displayOrder", String(idx));

        const res = await axios.post(
          `/api/admin/variant-images/upload`,
          formUpload,
          { headers: { "Content-Type": "multipart/form-data" } }
        );

        uploadedUrls.push(res.data?.imageUrl);
      }

      // 🖼 Ảnh URL
      else if (img.type === "url" && img.previewUrl) {
        await axios.post(`/api/admin/variant-images`, {
          variantId: variantId,
          imageUrl: img.previewUrl,
          displayOrder: idx,
        });

        uploadedUrls.push(img.previewUrl);
      }
    }

    // ==== chọn imageUrl cho variant (không ép nếu user không chọn) ====
    const selectedPrimaryIndex = selectedImages.value.findIndex(
      (i) => i.isPrimary
    );
    // if (!dbHasPrimary && selectedPrimaryIndex >= 0) {
    //   // chỉ khi DB không có primary & user có chọn primary mới
    //   formData.imageUrl = uploadedUrls[selectedPrimaryIndex] || null;
    // } // else: giữ nguyên imageUrl hiện có (nếu đang Edit) hoặc để null (Create → BE không bắt buộc)

    // ===== Nhánh 1: EDIT =====
    if (isEdit.value) {
      const variantId = props.variant?.id;
      if (!variantId) {
        notificationService.error(
          "Lỗi",
          "Không xác định được ID biến thể cần cập nhật"
        );
        return;
      }

      const payload = {
        productId: formData.productId,
        sku: formData.sku,
        color: formData.color,
        size: formData.size,
        priceBase: formData.priceBase,
        priceSale: formData.priceSale,
        stockQuantity: formData.stockQuantity,
        lowStockThreshold: formData.lowStockThreshold,
        imageUrl: formData.imageUrl || null,
        isActive: formData.isActive,
      };

      await adminStore.updateProductVariant(variantId, payload);

      notificationService.success(
        "Thành công",
        "Đã cập nhật biến thể thành công",
        {
          duration: 2500,
        }
      );

      resetForm();
      // emit("success");
      emit("success", variantId);
      emit("close");
      return;
    }

    // ===== Nhánh 2: CREATE =====

    // ==== build & gửi variants ====
    const sizes =
      selectedSizes.value.length > 0 ? selectedSizes.value : [formData.size];
    const product = products.value.find((p) => p.id === formData.productId);

    const variantList = sizes.map((size) => ({
      productId: formData.productId,
      sku: generateSku(product?.name, formData.color, size),
      color: formData.color,
      size,
      priceBase: formData.priceBase,
      priceSale: formData.priceSale,
      stockQuantity: formData.stockQuantity,
      lowStockThreshold: formData.lowStockThreshold,
      imageUrl: null, // CREATE => để null, upload xong mới gán
      isActive: formData.isActive,
    }));

    // CREATE variants -> lấy danh sách variantId
    const createdVariants = await adminStore.createMultipleProductVariants(
      variantList
    );
    // [{ id, size, ... }]

    // ==== UPLOAD ẢNH CHO CÁC VARIANT VỪA TẠO ====
    if (selectedImages.value.length > 0) {
      for (const v of createdVariants) {
        for (const [idx, img] of selectedImages.value.entries()) {
          // LOCAL FILE
          if (img.file) {
            const fd = new FormData();
            fd.append("file", img.file);
            fd.append("variantId", v.id);
            fd.append("displayOrder", String(idx));

            await axios.post("/api/admin/variant-images/upload", fd, {
              headers: { "Content-Type": "multipart/form-data" },
            });
          }

          // IMAGE URL
          else if (img.type === "url" && img.previewUrl) {
            await axios.post("/api/admin/variant-images", {
              variantId: v.id,
              imageUrl: img.previewUrl,
              displayOrder: idx,
            });
          }
        }
      }
    }

    notificationService.success(
      "Thành công",
      `Đã tạo ${variantList.length} biến thể mới`,
      { duration: 3000 }
    );

    const firstVariantId = createdVariants?.[0]?.id ?? null;
    resetForm();
    // emit("success");
    emit("success", firstVariantId);
    emit("close");
  } catch (error) {
    console.error("Error saving variants:", error);
    notificationService.error("Lỗi", "Không thể lưu biến thể");
  } finally {
    isSubmitting.value = false;
  }
};

// Trả về mảng [{ previewUrl, isPrimary, type: 'url' }]
// const loadProductImagesFromDB = async (productId) => {
//   try {
//     const { data } = await axios.get(`/api/admin/products/${productId}/images`);
//     // Kỳ vọng BE trả về: [{ id, imageUrl, isPrimary, displayOrder, ... }]
//     return (Array.isArray(data) ? data : []).map((it) => ({
//       previewUrl: it.imageUrl,
//       isPrimary: !!it.isPrimary,
//       type: "url",
//     }));
//   } catch (err) {
//     console.error("Không tải được thư viện ảnh:", err);
//     notificationService.error("Lỗi", "Không thể tải thư viện ảnh của sản phẩm");
//     return [];
//   }
// };

// ===== CLOSE MODAL =====
const closeModal = () => {
  emit("close");
};

// ===== DEBUG: BẮT LỖI RUNTIME (giúp modal không biến mất im lặng) =====
onErrorCaptured((err) => {
  console.error("⚠️ Vue runtime error:", err);
  return false;
});
</script>

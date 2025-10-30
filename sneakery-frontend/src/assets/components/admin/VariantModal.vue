<template>
  <div v-show="isOpen" class="modal-overlay" @click="handleOverlayClose">
    <div class="modal" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">
          <i class="material-icons">{{ isEdit ? "edit" : "add" }}</i>
          {{ isEdit ? "Chỉnh sửa Biến Thể" : "Thêm Biến Thể Mới" }}
        </h3>
        <button type="button" class="modal-close" @click="handleCancel">
          <i class="material-icons">close</i>
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="handleSubmit" novalidate>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label required">Sản phẩm</label>
              <template v-if="!isEdit">
                <select
                  v-model="formData.productId"
                  class="form-control"
                  required
                >
                  <option value="">Chọn sản phẩm</option>
                  <option
                    v-for="product in products"
                    :key="product.id"
                    :value="product.id"
                  >
                    {{ product.name }} - {{ product.brandName }}
                  </option>
                </select>
              </template>
              <template v-else>
                <div class="readonly-field">
                  <span class="readonly-text">
                    {{ getProductName(formData.productId) }}
                  </span>
                </div>
              </template>
            </div>

            <div class="form-group">
              <label class="form-label required">SKU</label>
              <input
                v-model="formData.sku"
                type="text"
                class="form-control"
                placeholder="Ví dụ: ADIDA-ULTRA22-WHI-42"
                required
                @focus="isSkuFocused = true"
                @blur="isSkuFocused = false"
              />
              <transition name="fade">
                <small v-if="isSkuFocused" class="sku-hint">
                  SKU được <strong>tạo tự động</strong> dựa trên thông tin sản
                  phẩm, màu và size — bạn có thể chỉnh
                  <strong>thủ công</strong> nếu muốn.
                </small>
              </transition>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label required">Màu sắc</label>
              <div class="select-box" @click="showColorPopup = true">
                <input
                  v-model="formData.color"
                  type="text"
                  class="form-control"
                  readonly
                  placeholder="Chọn màu sắc"
                />
                <span class="select-icon material-icons">palette</span>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label required">Kích thước</label>
              <div class="select-box" @click="showSizePopup = true">
                <input
                  v-model="formData.size"
                  type="text"
                  class="form-control"
                  readonly
                  placeholder="Chọn kích thước"
                />
                <span class="select-icon material-icons">straighten</span>
              </div>
            </div>
            <!-- Popup chọn màu -->
            <div
              v-if="showColorPopup"
              class="popup-overlay"
              @click.self="showColorPopup = false"
            >
              <div class="popup-panel">
                <h4>Chọn màu sắc</h4>
                <div class="color-grid">
                  <div
                    v-for="(c, idx) in availableColors"
                    :key="idx"
                    class="color-circle"
                    :style="{ backgroundColor: c.hex }"
                    @click="selectColor(c)"
                    :class="{ active: formData.color === c.name }"
                  ></div>
                </div>
              </div>
            </div>

            <!-- Popup chọn kích thước -->
            <div
              v-if="showSizePopup"
              class="popup-overlay"
              @click.self="showSizePopup = false"
            >
              <div class="popup-panel">
                <h4>Chọn kích thước</h4>
                <div class="size-grid">
                  <button
                    type="button"
                    v-for="(s, idx) in availableSizes"
                    :key="idx"
                    class="size-btn"
                    @click="toggleSize(s)"
                    :class="{ active: selectedSizes.includes(s) }"
                  >
                    {{ s }}
                  </button>
                </div>
                <div class="popup-actions">
                  <button
                    type="button"
                    class="btn btn-primary"
                    @click="confirmSizes"
                  >
                    Xác nhận
                  </button>
                </div>
              </div>
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
            <label class="form-label">Ảnh biến thể</label>
            <UploadGallery
              :resetKey="resetKey"
              :initialImages="galleryInitial"
              @change="handleImagesChanged"
              @remove="handleImageRemoved"
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
        <button type="button" class="btn btn-secondary" @click="handleCancel">
          Hủy
        </button>
        <button
          type="button"
          class="btn btn-primary"
          @click="handleSubmit"
          :disabled="isSubmitting"
        >
          <i class="material-icons" v-if="isSubmitting">hourglass_empty</i>
          <i class="material-icons" v-else>{{ isEdit ? "save" : "add" }}</i>
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
  computed,
  onErrorCaptured,
} from "vue";
import { useAdminStore } from "@/stores/admin";
import { ElMessage } from "element-plus";
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
};

// Multi-size logic (đã giải thích ở trên)

const toggleSize = (size) => {
  const i = selectedSizes.value.indexOf(size);
  if (i > -1) selectedSizes.value.splice(i, 1);
  else selectedSizes.value.push(size);
};

const confirmSizes = () => {
  if (selectedSizes.value.length === 0) {
    ElMessage.warning("Vui lòng chọn ít nhất một kích thước");
    return;
  }
  formData.size = selectedSizes.value.join(", ");
  showSizePopup.value = false;
};

const handleImageRemoved = (url) => {
  if (!removedImageUrls.value.includes(url)) {
    removedImageUrls.value.push(url);
  }
};

const isEdit = computed(() => !!props.variant);

// ===== IMAGE UPLOAD =====
// const handleImageUploaded = (imageData) => {
//   if (imageData && imageData.imageUrl) {
//     formData.imageUrl = imageData.imageUrl;
//     ElMessage.success({
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
    ElMessage.error({
      message: "Không thể tải danh sách sản phẩm",
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
  selectColor.value = null;
  showColorPopup.value = false;
  showSizePopup.value = false;
  galleryInitial.value = [];
  resetKey.value++; // ép component con reset lại gallery
  removedImageUrls.value = [];
};

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
};

// ===== MODAL LIFECYCLE =====
watch(
  () => props.isOpen,
  async (open) => {
    if (!open) return;

    await loadProducts();

    // Mặc định: rỗng (Create)
    galleryInitial.value = [];
    resetKey.value++; // ép con reset sạch

    // Edit mode
    if (props.variant && props.variant.productId) {
      // 1) Nạp dữ liệu form
      populateForm(props.variant);

      // 2) Fetch ảnh sản phẩm từ DB
      const initial = await loadProductImagesFromDB(props.variant.productId);

      // 3) Gán cho UploadGallery
      galleryInitial.value = initial;

      // 4) Ép UploadGallery nạp lại từ initialImages
      resetKey.value++;
    }

    if (props.variant) {
      // Edit mode: nạp form
      populateForm(props.variant);

      try {
        // Lấy ảnh của product (không thay primary ở đây)
        const { data } = await axios.get(
          `/api/admin/products/${props.variant.productId}/images`
        );
        // Map về dạng UploadGallery hiểu
        galleryInitial.value = (data || []).map((it) => ({
          previewUrl: it.imageUrl, // bắt buộc
          isPrimary: !!it.isPrimary, // để UploadGallery hiển thị sao
          type: "db", // ✅ phân biệt ảnh đã có trong DB
          id: it.id ?? null, // (tuỳ dùng)
        }));

        // Ép UploadGallery nạp lại từ initialImages mới
        resetKey.value++;
      } catch (err) {
        console.error("Không tải được ảnh từ DB:", err);
        ElMessage.error("Không thể tải thư viện ảnh của sản phẩm");
      }
    }
  }
);

// ====== AUTO GENERATE SKU ======
// --- Watch sinh SKU ---
watch(
  [() => formData.productId, () => formData.color, () => formData.size],
  ([pid, color, size]) => {
    if (!pid || !color || !size) return;
    const product = products.value.find((p) => p.id === pid);
    if (!product?.name) return;

    const brandPart = extractBrandCode(product.name); // ADIDA / NIKE / CONVE ...
    const modelPart = extractModelCode(product.name); // ULTRA22 / REACT55 ...
    const colorPart = shortenColor(color); // WHI / RED / BLK ...
    const sizePart = String(size).trim(); // 42

    formData.sku = `${brandPart}-${modelPart}-${colorPart}-${sizePart}`;
  }
);

// ===== SUBMIT =====
const handleSubmit = async () => {
  try {
    isSubmitting.value = true;

    // ==== validate cơ bản ====
    if (!formData.productId) return ElMessage.warning("Vui lòng chọn sản phẩm");
    if (!formData.color) return ElMessage.warning("Vui lòng chọn màu");
    if (!formData.size && selectedSizes.value.length === 0)
      return ElMessage.warning("Vui lòng chọn kích thước");
    if (selectedImages.value.length > 10)
      return ElMessage.warning("Tối đa 10 ảnh");

    // ==== xóa ảnh đã đánh dấu xóa (nếu có) ====
    if (removedImageUrls.value.length > 0) {
      for (const item of removedImageUrls.value) {
        const url =
          typeof item === "string"
            ? item
            : item?.url || item?.previewUrl || item?.imageUrl;

        if (!url) continue;

        try {
          await axios.delete(
            `/api/admin/products/${formData.productId}/images`,
            {
              data: { imageUrl: url }, // ✅ giờ gửi string đúng format
              headers: { "Content-Type": "application/json" },
            }
          );
          console.log("🗑️ Đã xóa ảnh:", url);
        } catch (e) {
          console.error("❌ Xóa ảnh lỗi:", url, e);
        }
      }
      removedImageUrls.value = [];
    }

    // ==== tính trạng thái primary hiện có ở DB (sau khi trừ ảnh đã xóa) ====
    const dbImagesEffective = (galleryInitial.value || []).filter(
      (it) => !removedImageUrls.value.includes(it.previewUrl)
    );
    const dbHasPrimary = dbImagesEffective.some((it) => it.isPrimary === true);

    // ==== upload/lưu ảnh mới ====
    const uploadedUrls = [];
    // Lưu ý: KHÔNG tự set primary nếu dbHasPrimary === true
    //        Nếu dbHasPrimary === false (VD: sp mới/đã xóa hết), cứ gửi isPrimary theo user chọn;
    //        Nếu user không chọn, BE vẫn auto primary khi existingCount == 0.

    for (const [idx, img] of selectedImages.value.entries()) {
      if (img.type === "db") continue; // ✅ ảnh đã có DB: không gửi API lần nữa
      const isPrimaryChosen = !!img.isPrimary; // chỉ tôn trọng nếu user chọn
      const willSendPrimary = dbHasPrimary ? false : isPrimaryChosen; // nếu DB đã có primary thì ép false

      if ((img.type === "local" || img.file) && img.file) {
        // ✅ resilient khi thiếu type
        const formUpload = new FormData();
        formUpload.append("file", img.file);
        formUpload.append("isPrimary", String(willSendPrimary));
        formUpload.append("displayOrder", String(idx)); // bạn muốn 0-based → giữ nguyên

        const res = await axios.post(
          `/api/admin/products/${formData.productId}/images/upload`,
          formUpload,
          { headers: { "Content-Type": "multipart/form-data" } }
        );
        uploadedUrls.push(res.data?.imageUrl);
      } else if (img.type === "url" && img.previewUrl) {
        // ✅ THÊM API này để lưu record DB cho ảnh URL (trước đây bị thiếu nên "không gọi API")
        await axios.post(
          `/api/admin/products/${formData.productId}/images`,
          {
            imageUrl: img.previewUrl,
            isPrimary: willSendPrimary, // tôn trọng quy tắc ở trên
            displayOrder: idx, // 0-based, BE +1 nội bộ
          },
          { headers: { "Content-Type": "application/json" } }
        );
        uploadedUrls.push(img.previewUrl);
      }
    }

    // ==== chọn imageUrl cho variant (không ép nếu user không chọn) ====
    const selectedPrimaryIndex = selectedImages.value.findIndex(
      (i) => i.isPrimary
    );
    if (!dbHasPrimary && selectedPrimaryIndex >= 0) {
      // chỉ khi DB không có primary & user có chọn primary mới
      formData.imageUrl = uploadedUrls[selectedPrimaryIndex] || null;
    } // else: giữ nguyên imageUrl hiện có (nếu đang Edit) hoặc để null (Create → BE không bắt buộc)

    // ===== Nhánh 1: EDIT =====
    if (isEdit.value) {
      const variantId = props.variant?.id;
      if (!variantId) {
        ElMessage.error("Không xác định được ID biến thể cần cập nhật");
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

      ElMessage.success({
        message: "Đã cập nhật biến thể thành công",
        duration: 2500,
      });

      resetForm();
      emit("success");
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
      imageUrl: formData.imageUrl || null, // không ép, có thể null
      isActive: formData.isActive,
    }));

    await adminStore.createMultipleProductVariants(variantList);

    ElMessage.success({
      message: `Đã tạo ${variantList.length} biến thể mới`,
      duration: 3000,
    });
    resetForm();
    emit("success");
    emit("close");
  } catch (error) {
    console.error("Error saving variants:", error);
    ElMessage.error("Không thể lưu biến thể");
  } finally {
    isSubmitting.value = false;
  }
};

// Trả về mảng [{ previewUrl, isPrimary, type: 'url' }]
const loadProductImagesFromDB = async (productId) => {
  try {
    const { data } = await axios.get(`/api/admin/products/${productId}/images`);
    // Kỳ vọng BE trả về: [{ id, imageUrl, isPrimary, displayOrder, ... }]
    return (Array.isArray(data) ? data : []).map((it) => ({
      previewUrl: it.imageUrl,
      isPrimary: !!it.isPrimary,
      type: "url",
    }));
  } catch (err) {
    console.error("Không tải được thư viện ảnh:", err);
    ElMessage.error("Không thể tải thư viện ảnh của sản phẩm");
    return [];
  }
};

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
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* Edge cũ */
}

.modal::-webkit-scrollbar {
  display: none; /* Chrome, Edge mới, Safari */
  scroll-behavior: smooth;
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
  content: " *";
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

.sku-hint {
  display: block;
  margin-top: 4px;
  font-size: 0.85rem;
  color: var(--text-tertiary, #aaa);
  font-style: italic;
  line-height: 1.3;
  transition: all 0.25s ease;
}

.sku-hint strong {
  color: var(--accent-primary, #b07bff);
  font-weight: 500;
}

/* Hiệu ứng mờ dần */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ====== Ô chọn (màu/size) ====== */
.select-box {
  position: relative;
  display: flex;
  align-items: center;
}

.select-icon {
  position: absolute;
  right: 12px;
  color: var(--accent-primary);
  cursor: pointer;
}

/* ====== Popup overlay ====== */
.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(6px);
}

.popup-panel {
  background: rgba(15, 23, 42, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  padding: 1.75rem 2rem;
  width: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
  animation: fadeIn 0.25s ease-in-out;
}

.popup-panel h4 {
  color: #fff;
  margin-bottom: 1.2rem;
  font-size: 1.1rem;
  text-align: center;
  font-weight: 600;
}

/* ====== Bảng màu ====== */
.color-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.color-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid var(--border-primary);
  cursor: pointer;
  transition: all 0.25s ease;
}
.color-circle:hover {
  transform: scale(1.15);
  box-shadow: 0 0 8px var(--accent-primary);
}
.color-circle.active {
  border: 2px solid var(--accent-primary);
  box-shadow: 0 0 12px var(--accent-primary);
}

/* ====== Kích thước ====== */
.size-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.size-btn {
  width: 48px;
  height: 36px;
  border-radius: 8px;
  border: 1px solid var(--border-primary);
  background: var(--bg-secondary);
  color: var(--text-primary);
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s ease;
}
.size-btn:hover {
  background: var(--accent-primary);
  color: #fff;
}
.size-btn.active {
  background: var(--gradient-primary);
  color: #fff;
  box-shadow: 0 0 8px var(--accent-primary);
}

/* ✅ Cách phần grid với nút xác nhận */
.popup-actions {
  margin-top: 1.5rem; /* 👈 tạo khoảng cách rõ ràng */
  width: 100%;
  display: flex;
  justify-content: center;
}

.btn.btn-primary {
  background: var(
    --primary-gradient,
    linear-gradient(135deg, #7b5cff, #9f7aea)
  );
  border: none;
  color: #fff;
  padding: 0.6rem 1.25rem;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.25s ease;
  cursor: pointer;
}

.btn.btn-primary:hover {
  box-shadow: 0 0 15px rgba(167, 139, 250, 0.6);
  transform: translateY(-2px);
}

@keyframes popup-fade {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.readonly-field {
  padding: var(--space-3);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  background: var(--bg-primary);
  color: var(--text-primary);
  font-size: var(--text-base);
  transition: all var(--transition-fast);
}
</style>

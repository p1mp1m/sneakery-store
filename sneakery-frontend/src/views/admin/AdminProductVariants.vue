<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
      >
        <div>
          <h1
            class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
          >
            <i class="material-icons text-purple-600 dark:text-purple-400"
              >style</i
            >
            Quản Lý Biến Thể Sản Phẩm
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
            Quản lý màu sắc, kích thước và tồn kho của từng biến thể sản phẩm
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
            @click="openAddVariantModal"
          >
            <i class="material-icons text-base">add</i>
            Thêm Biến Thể
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">inventory_2</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.totalVariants }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng Biến Thể</p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.inStock }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Còn Hàng</p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">warning</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.lowStock }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Sắp Hết</p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">remove_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.outOfStock }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Hết Hàng</p>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="flex flex-col gap-1">
          <label
            class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1"
          >
            <i class="material-icons text-sm">search</i>
            Tìm kiếm
          </label>
          <input
            type="text"
            v-model="filters.search"
            placeholder="Tìm theo tên sản phẩm..."
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            @input="debouncedSearch"
          />
        </div>

        <div class="flex flex-col gap-1">
          <label
            class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1"
          >
            <i class="material-icons text-sm">palette</i>
            Màu sắc
          </label>
          <select
            v-model="filters.color"
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            @change="handleFilter"
          >
            <option value="">Tất cả màu</option>
            <option value="Đen">Đen</option>
            <option value="Trắng">Trắng</option>
            <option value="Đỏ">Đỏ</option>
            <option value="Xanh dương">Xanh dương</option>
            <option value="Xanh lá">Xanh lá</option>
          </select>
        </div>

        <div class="flex flex-col gap-1">
          <label
            class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1"
          >
            <i class="material-icons text-sm">straighten</i>
            Kích thước
          </label>
          <select
            v-model="filters.size"
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            @change="handleFilter"
          >
            <option value="">Tất cả size</option>
            <option value="35">35</option>
            <option value="36">36</option>
            <option value="37">37</option>
            <option value="38">38</option>
            <option value="39">39</option>
            <option value="40">40</option>
            <option value="41">41</option>
            <option value="42">42</option>
            <option value="43">43</option>
          </select>
        </div>

        <div class="flex flex-col gap-1">
          <label
            class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1"
          >
            <i class="material-icons text-sm">inventory</i>
            Trạng thái kho
          </label>
          <select
            v-model="filters.stockStatus"
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            @change="handleFilter"
          >
            <option value="">Tất cả</option>
            <option value="in_stock">Còn hàng</option>
            <option value="low_stock">Sắp hết</option>
            <option value="out_of_stock">Hết hàng</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Variants Table -->
    <div
      class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden"
    >
      <!-- Loading State -->
      <div
        v-if="loading"
        class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <div
          class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"
        ></div>
        <p class="text-sm text-gray-600 dark:text-gray-400">
          Đang tải dữ liệu...
        </p>
      </div>

      <!-- Empty State -->
      <div
        v-else-if="variants.length === 0"
        class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <div
          class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4"
        >
          <i
            class="material-icons text-purple-600 dark:text-purple-400 text-3xl"
            >inventory_2</i
          >
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
          Không tìm thấy biến thể nào
        </h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
          Bắt đầu thêm biến thể đầu tiên cho sản phẩm của bạn
        </p>
        <button
          @click="openAddVariantModal"
          class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
        >
          <i class="material-icons text-base">add</i>
          Thêm Biến Thể
        </button>
      </div>

      <!-- Table -->
      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead
            class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600"
          >
            <tr>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Hình ảnh
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Sản phẩm
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                SKU
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Màu sắc
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Kích thước
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors group"
                @click="handleSort('price')"
              >
                <div class="flex items-center gap-1">
                  Giá
                  <i
                    v-if="sortBy === 'price'"
                    class="material-icons text-sm text-purple-600 dark:text-purple-400"
                  >
                    {{
                      sortDirection === "asc"
                        ? "arrow_upward"
                        : "arrow_downward"
                    }}
                  </i>
                  <i
                    v-else
                    class="material-icons text-sm text-gray-400 opacity-0 group-hover:opacity-50"
                  >
                    swap_vert
                  </i>
                </div>
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Tồn kho
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Trạng thái
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody
            class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700"
          >
            <tr
              v-for="variant in variants"
              :key="variant.id"
              :id="`variant-row-${variant.id}`"
              class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors"
            >
              <td class="px-4 py-4">
                <img
                  :src="
                    variantImages[variant.id]?.[0]?.imageUrl ||
                    variant.imageUrl ||
                    '/placeholder-image.png'
                  "
                  :alt="variant.productName"
                  class="w-16 h-16 object-cover rounded-lg border border-gray-200 dark:border-gray-700"
                />
              </td>
              <td class="px-4 py-4">
                <div class="flex flex-col">
                  <span
                    class="text-sm font-medium text-gray-900 dark:text-gray-100"
                    >{{ variant.productName }}</span
                  >
                  <span
                    v-if="variant.brandName"
                    class="text-xs text-gray-500 dark:text-gray-400"
                    >{{ variant.brandName }}</span
                  >
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <code
                  class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100"
                  >{{ variant.sku }}</code
                >
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <span
                    class="w-4 h-4 rounded-full border border-gray-300 dark:border-gray-600"
                    :style="{ backgroundColor: getColorHex(variant.color) }"
                  ></span>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    getColorName(variant.color)
                  }}</span>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400"
                  >{{ variant.size }}</span
                >
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span
                  class="text-sm font-semibold text-gray-900 dark:text-gray-100"
                  >{{ formatPrice(getCurrentPrice(variant)) }}</span
                >
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span
                  class="text-sm font-semibold"
                  :class="{
                    'text-green-600 dark:text-green-400':
                      variant.stockQuantity > 10,
                    'text-yellow-600 dark:text-yellow-400':
                      variant.stockQuantity > 0 && variant.stockQuantity <= 10,
                    'text-red-600 dark:text-red-400':
                      variant.stockQuantity === 0,
                  }"
                >
                  {{ variant.stockQuantity }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400':
                      variant.stockQuantity > 10,
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400':
                      variant.stockQuantity > 0 && variant.stockQuantity <= 10,
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400':
                      variant.stockQuantity === 0,
                  }"
                >
                  {{ getStockStatusLabel(variant.stockQuantity) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <button
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors"
                    @click="editVariant(variant)"
                    title="Chỉnh sửa"
                  >
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button
                    class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
                    @click="openDeleteConfirm(variant)"
                    title="Xóa"
                  >
                    <i class="material-icons text-base">delete</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div
        v-if="totalPages > 1 && !loading"
        class="flex items-center justify-between gap-4 px-4 py-3 mt-4 border-t border-gray-200 dark:border-gray-700"
      >
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Hiển thị {{ currentPage * pageSize + 1 }} -
          {{ Math.min((currentPage + 1) * pageSize, totalElements) }} trong tổng
          số {{ totalElements }} biến thể
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="!paginationInfo.hasPrev || loading"
            @click="changePage(currentPage - 1)"
          >
            <i class="material-icons text-base">chevron_left</i>
            Trước
          </button>
          <span class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300">
            Trang {{ paginationInfo.currentPage }} /
            {{ paginationInfo.totalPages }}
          </span>
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="!paginationInfo.hasNext || loading"
            @click="changePage(currentPage + 1)"
          >
            Sau
            <i class="material-icons text-base">chevron_right</i>
          </button>
        </div>
      </div>
    </div>

    <!-- Variant Modal -->
    <VariantModal
      :is-open="isModalOpen"
      :variant="selectedVariant"
      @close="closeModal"
      @success="handleModalSuccess"
    />

    <!-- 🔹 Popup xác nhận xóa biến thể -->
    <ConfirmDialog
      v-model="showDeleteModal"
      type="danger"
      title="Xác nhận xóa biến thể"
      :message="`Bạn có chắc chắn muốn xóa biến thể '${variantToDelete?.sku}' không?`"
      description="Hành động này không thể hoàn tác."
      confirm-text="Xóa biến thể"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="deleteVariantConfirmed"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from "vue";
import { useAdminStore } from "@/stores/admin";
import notificationService from "@/utils/notificationService";
// import notificationService from "@/utils/notificationService";
import VariantModal from "@/assets/components/admin/VariantModal.vue";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";
import { debounce } from "@/utils/debounce";
import logger from "@/utils/logger";
import { formatPrice } from "@/utils/formatters";

// ===== STORES =====
const adminStore = useAdminStore();

// ===== STATE =====
const variants = ref([]);
const currentPage = ref(0);
const pageSize = ref(10);
const totalElements = ref(0);
const totalPages = ref(1);
const loading = ref(false);
const highlightVariantId = ref(null);
const variantImages = ref({});

const stats = reactive({
  totalVariants: 0,
  inStock: 0,
  lowStock: 0,
  outOfStock: 0,
});

const filters = reactive({
  search: "",
  color: "",
  size: "",
  stockStatus: "",
});

// ===== SORT STATE =====
const sortBy = ref(null); // null, "price", "stock", "sku", etc.
const sortDirection = ref("asc"); // "asc" or "desc"

// ===== MODAL STATE =====
const isModalOpen = ref(false);
const selectedVariant = ref(null);

// ===== COMPUTED =====
const paginationInfo = computed(() => ({
  currentPage: currentPage.value + 1,
  totalPages: totalPages.value,
  totalElements: totalElements.value,
  hasNext: currentPage.value < totalPages.value - 1,
  hasPrev: currentPage.value > 0,
}));

// ===== LIFECYCLE =====
onMounted(async () => {
  await loadVariants();
  await loadStats();
});

// ===== METHODS =====
const loadVariants = async () => {
  try {
    loading.value = true;
    const result = await adminStore.fetchProductVariants(
      currentPage.value,
      pageSize.value,
      filters,
      sortBy.value,
      sortDirection.value
    );

    variants.value = result.content || [];
    totalElements.value = result.totalElements || 0;
    totalPages.value = result.totalPages || 1;
    // Load ảnh cho từng variant
    for (const v of variants.value) {
      loadVariantImages(v.id);
    }
  } catch (error) {
    logger.error("Error loading variants:", error);
    notificationService.apiError(error, "Không thể tải danh sách biến thể");
  } finally {
    loading.value = false;
  }
};

const loadVariantImages = async (variantId) => {
  try {
    const res = await adminStore.fetchVariantImages(variantId);
    variantImages.value[variantId] = res || [];
  } catch (error) {
    console.error("Error loading variant images:", error);
    variantImages.value[variantId] = [];
  }
};

const handleSort = (field) => {
  if (sortBy.value === field) {
    // Đang sort cùng field, toggle direction hoặc reset về không sort
    if (sortDirection.value === "asc") {
      // Từ asc -> desc
      sortDirection.value = "desc";
    } else if (sortDirection.value === "desc") {
      // Từ desc -> không sort (reset)
      sortBy.value = null;
      sortDirection.value = "asc";
    }
  } else {
    // Set field mới và mặc định là asc
    sortBy.value = field;
    sortDirection.value = "asc";
  }
  currentPage.value = 0; // Reset về trang đầu
  loadVariants();
};

const loadStats = async () => {
  try {
    const result = await adminStore.fetchProductVariantStats();
    stats.totalVariants = result.totalVariants || 0;
    stats.inStock = result.inStockVariants || 0;
    stats.lowStock = result.lowStockVariants || 0;
    stats.outOfStock = result.outOfStockVariants || 0;
  } catch (error) {
    logger.error("Error loading stats:", error);
    // Fallback to calculating from variants if API fails
    calculateStatsFromVariants();
  }
};

const calculateStatsFromVariants = () => {
  stats.totalVariants = variants.value.length;
  stats.inStock = variants.value.filter((v) => v.stockQuantity > 10).length;
  stats.lowStock = variants.value.filter(
    (v) => v.stockQuantity > 0 && v.stockQuantity <= 10
  ).length;
  stats.outOfStock = variants.value.filter((v) => v.stockQuantity === 0).length;
};

const handleSearch = async () => {
  currentPage.value = 0;
  await loadVariants();
};

// Debounce search để tránh gọi API quá nhiều khi user đang gõ
const debouncedSearch = debounce(handleSearch, 500);

const handleFilter = async () => {
  currentPage.value = 0;
  await loadVariants();
};

const openAddVariantModal = () => {
  selectedVariant.value = null;
  isModalOpen.value = true;
};

const editVariant = (variant) => {
  selectedVariant.value = variant;
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  selectedVariant.value = null;
};

const handleModalSuccess = async (variantId) => {
  // Lưu ID để (nếu cần) debug sau này
  highlightVariantId.value = variantId || null;

  // Sau khi tạo / update xong, nên load lại từ trang hiện tại
  await loadVariants();
  await loadStats();

  // Nếu variant nằm trong trang hiện tại thì cuộn + highlight
  if (variantId) {
    scrollToVariantRow(variantId);
  }
};

// ===== DELETE VARIANT (với ConfirmDialog) =====
const showDeleteModal = ref(false);
const deleting = ref(false);
const variantToDelete = ref(null);

const openDeleteConfirm = (variant) => {
  variantToDelete.value = variant;
  showDeleteModal.value = true;
};

const deleteVariantConfirmed = async () => {
  if (!variantToDelete.value) return;

  deleting.value = true;
  try {
    await adminStore.deleteProductVariant(variantToDelete.value.id);
    notificationService.success(
      "Thành công",
      `Đã xóa biến thể "${variantToDelete.value.sku}" thành công!`
    );
    await loadVariants();
    await loadStats();
  } catch (error) {
    logger.error("Error deleting variant:", error);
    notificationService.apiError(error, "Không thể xóa biến thể");
  } finally {
    deleting.value = false;
    showDeleteModal.value = false;
    variantToDelete.value = null;
  }
};

const changePage = async (page) => {
  currentPage.value = page;
  await loadVariants();
};

// formatPrice đã được import từ @/utils/formatters

const getColorHex = (color) => {
  const colorMap = {
    black: "#000000",
    white: "#FFFFFF",
    red: "#EF4444",
    blue: "#3B82F6",
    green: "#10B981",
    yellow: "#F59E0B",
    purple: "#8B5CF6",
    pink: "#EC4899",
    gray: "#6B7280",
    brown: "#A78BFA",
  };
  return colorMap[color?.toLowerCase()] || "#9CA3AF";
};

const getColorName = (color) => {
  const colorMap = {
    black: "Đen",
    white: "Trắng",
    red: "Đỏ",
    blue: "Xanh dương",
    green: "Xanh lá",
    yellow: "Vàng",
    purple: "Tím",
    pink: "Hồng",
    gray: "Xám",
    brown: "Nâu",
  };
  return colorMap[color?.toLowerCase()] || color || "Không xác định";
};

const getCurrentPrice = (variant) => {
  // Ưu tiên giá sale nếu có, nếu không thì dùng giá base
  return variant.priceSale || variant.priceBase || 0;
};

const getStockClass = (quantity) => {
  if (quantity === 0) return "stock-out";
  if (quantity <= 10) return "stock-low";
  return "stock-ok";
};

const getStockStatusClass = (quantity) => {
  if (quantity === 0) return "status-danger";
  if (quantity <= 10) return "status-warning";
  return "status-success";
};

const getStockStatusLabel = (quantity) => {
  if (quantity === 0) return "Hết hàng";
  if (quantity <= 10) return "Sắp hết";
  return "Còn hàng";
};

const scrollToVariantRow = async (variantId) => {
  if (!variantId) return;

  await nextTick(); // chờ DOM render xong

  const rowEl = document.getElementById(`variant-row-${variantId}`);
  if (!rowEl) return;

  // Cuộn mượt đến giữa màn hình
  rowEl.scrollIntoView({
    behavior: "smooth",
    block: "center",
  });

  // Thêm class highlight trong 3 giây
  rowEl.classList.add("row-highlight");
  setTimeout(() => {
    rowEl.classList.remove("row-highlight");
  }, 3000);
};
</script>
<style scoped>
/* Hiệu ứng vàng neon nhấp nháy */
.row-highlight {
  animation: neonFlash 1.8s ease-out forwards;
}

@keyframes neonFlash {
  0% {
    background-color: rgba(255, 255, 0, 0.9); /* neon vàng đậm */
    box-shadow: 0 0 12px rgba(255, 255, 0, 0.7);
  }
  25% {
    background-color: rgba(255, 255, 0, 0.4);
    box-shadow: 0 0 4px rgba(255, 255, 0, 0.3);
  }
  50% {
    background-color: rgba(255, 255, 0, 1);
    box-shadow: 0 0 16px rgba(255, 255, 0, 0.9);
  }
  75% {
    background-color: rgba(255, 255, 0, 0.5);
    box-shadow: 0 0 6px rgba(255, 255, 0, 0.4);
  }
  100% {
    background-color: transparent;
    box-shadow: none;
  }
}
</style>

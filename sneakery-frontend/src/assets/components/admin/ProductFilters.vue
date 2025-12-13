<template>
  <div
    class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
  >
    <!-- Filter Presets & Actions -->
    <div class="flex items-center justify-between mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
      <div class="flex items-center gap-2 flex-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300 whitespace-nowrap">Preset:</label>
        <select
          v-model="selectedPresetId"
          @change="handlePresetChange"
          class="px-3 py-1.5 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
        >
          <option value="">-- Chọn preset --</option>
          <option v-for="preset in presets" :key="preset.id" :value="preset.id">
            {{ preset.name }}
          </option>
        </select>
        <button
          @click="showSavePresetModal = true"
          class="px-2 py-1.5 text-xs bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400 rounded-lg hover:bg-purple-200 dark:hover:bg-purple-900/50 transition-colors"
          title="Lưu bộ lọc hiện tại"
        >
          <i class="material-icons text-sm">bookmark_add</i>
        </button>
      </div>
      <div class="flex items-center gap-2">
        <button
          @click="showHistoryDropdown = !showHistoryDropdown"
          class="px-2 py-1.5 text-xs bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors relative"
          title="Lịch sử bộ lọc"
        >
          <i class="material-icons text-sm">history</i>
          <span v-if="history.length > 0" class="absolute -top-1 -right-1 w-4 h-4 bg-purple-600 text-white text-[10px] rounded-full flex items-center justify-center">{{ history.length }}</span>
        </button>
        <button
          @click="exportCurrentFilters"
          class="px-2 py-1.5 text-xs bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
          title="Xuất bộ lọc"
        >
          <i class="material-icons text-sm">download</i>
        </button>
        <label class="px-2 py-1.5 text-xs bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors cursor-pointer">
          <i class="material-icons text-sm">upload</i>
          <input type="file" accept=".json" @change="handleImportFilters" class="hidden" />
        </label>
      </div>
    </div>

    <!-- History Dropdown -->
    <div v-if="showHistoryDropdown" class="absolute z-50 mt-2 w-64 bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 max-h-60 overflow-y-auto">
      <div class="p-2">
        <div class="flex items-center justify-between mb-2 px-2">
          <span class="text-xs font-semibold text-gray-700 dark:text-gray-300">Lịch sử</span>
          <button
            @click="clearHistory"
            class="text-xs text-red-600 dark:text-red-400 hover:underline"
          >
            Xóa
          </button>
        </div>
        <div v-if="history.length === 0" class="px-2 py-4 text-xs text-gray-500 dark:text-gray-400 text-center">
          Chưa có lịch sử
        </div>
        <button
          v-for="item in history"
          :key="item.id"
          @click="applyHistoryItem(item.id)"
          class="w-full text-left px-2 py-1.5 text-xs text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded transition-colors"
        >
          {{ formatHistoryItem(item) }}
        </button>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
      <!-- Search -->
      <div class="flex flex-col gap-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
          >Tìm kiếm</label
        >
        <input
          :value="filters.search"
          @input="handleSearchInput"
          type="text"
          placeholder="Tìm theo tên sản phẩm..."
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        />
      </div>

      <!-- Brand -->
      <div class="flex flex-col gap-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
          >Thương hiệu</label
        >
        <select
          :value="filters.brandId"
          @change="handleBrandChange"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        >
          <option :value="null">Tất cả thương hiệu</option>
          <option v-for="brand in brands" :key="brand.id" :value="brand.id">
            {{ brand.name }}
          </option>
        </select>
      </div>

      <!-- Category -->
      <div class="flex flex-col gap-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
          >Danh mục</label
        >
        <select
          :value="filters.categoryId"
          @change="handleCategoryChange"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        >
          <option :value="null">Tất cả danh mục</option>
          <option
            v-for="category in categories"
            :key="category.id"
            :value="category.id"
          >
            {{ category.name }}
          </option>
        </select>
      </div>

      <!-- Status -->
      <div class="flex flex-col gap-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
          >Trạng thái</label
        >
        <select
          :value="filters.status"
          @change="handleStatusChange"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        >
          <option value="all">Tất cả</option>
          <option value="active">Đang bán</option>
          <option value="inactive">Ngừng bán</option>
        </select>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
      <!-- Min Price -->
      <!-- <div class="flex flex-col gap-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Giá từ (VNĐ)</label>
        <input
          :value="filters.minPrice"
          @change="handleMinPriceChange"
          type="number"
          placeholder="0"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        />
      </div> -->

      <!-- Max Price -->
      <!-- <div class="flex flex-col gap-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Giá đến (VNĐ)</label>
        <input
          :value="filters.maxPrice"
          @change="handleMaxPriceChange"
          type="number"
          placeholder="10,000,000"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        />
      </div> -->

      <!-- Stock Level -->
      <div class="flex flex-col gap-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
          >Tồn kho</label
        >
        <select
          :value="filters.stockLevel"
          @change="handleStockLevelChange"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        >
          <option value="all">Tất cả</option>
          <option value="in_stock">Còn hàng</option>
          <option value="low_stock">Sắp hết</option>
          <option value="out_of_stock">Hết hàng</option>
        </select>
      </div>

      <!-- Sort -->
      <div class="flex flex-col gap-1">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300"
          >Sắp xếp</label
        >
        <select
          :value="filters.sortBy"
          @change="handleSortChange"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        >
          <option value="">Mặc định</option>
          <option value="name">Tên A-Z</option>
          <!-- <option value="price">Giá thấp → cao</option> -->
          <option value="stock">Tồn kho thấp → cao</option>
        </select>
      </div>

      <!-- Reset Button -->
      <div class="flex items-end">
        <button
          @click="handleReset"
          class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium w-full"
        >
          <i class="material-icons text-base">clear</i>
          Xóa bộ lọc
        </button>
      </div>
    </div>

    <!-- Save Preset Modal -->
    <Teleport to="body">
      <div
        v-if="showSavePresetModal"
        class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/50 backdrop-blur-sm"
        @click.self="showSavePresetModal = false"
      >
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full mx-4">
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
            <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">Lưu bộ lọc</h3>
            <button
              @click="showSavePresetModal = false"
              class="p-1 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
            >
              <i class="material-icons text-gray-600 dark:text-gray-300">close</i>
            </button>
          </div>
          <div class="p-4">
            <input
              v-model="presetName"
              type="text"
              placeholder="Nhập tên preset..."
              class="w-full px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
              @keyup.enter="saveCurrentPreset"
            />
          </div>
          <div class="flex items-center justify-end gap-2 p-4 border-t border-gray-200 dark:border-gray-700">
            <button
              @click="showSavePresetModal = false"
              class="px-4 py-2 text-sm text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600"
            >
              Hủy
            </button>
            <button
              @click="saveCurrentPreset"
              class="px-4 py-2 text-sm text-white bg-purple-600 rounded-lg hover:bg-purple-700"
            >
              Lưu
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useProductFilters } from "@/composables/useProductFilters";
import { useFilterPresets } from "@/composables/useFilterPresets";
import { Status, StockLevel } from "@/utils/productConstants";
import notificationService from '@/utils/notificationService';

const props = defineProps({
  filters: {
    type: Object,
    required: true,
  },
  brands: {
    type: Array,
    default: () => [],
  },
  categories: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(["update:filters", "apply", "reset"]);

// Use composable for debounced search
const { debouncedSearch } = useProductFilters();

// Filter presets
const {
  presets,
  history,
  savePreset,
  deletePreset,
  applyPreset,
  addToHistory,
  applyHistory,
  clearHistory,
  downloadFilters,
  uploadFilters
} = useFilterPresets('products');

const selectedPresetId = ref('');
const showSavePresetModal = ref(false);
const showHistoryDropdown = ref(false);
const presetName = ref('');

// Handlers
const handleSearchInput = (event) => {
  const newFilters = {
    ...props.filters,
    search: event.target.value,
  };
  emit("update:filters", newFilters);

  // Debounce search
  debouncedSearch(() => {
    emit("apply");
  });
};

const handleBrandChange = (event) => {
  const newFilters = {
    ...props.filters,
    brandId: event.target.value ? Number(event.target.value) : null,
  };
  emit("update:filters", newFilters);
  emit("apply");
};

const handleCategoryChange = (event) => {
  const newFilters = {
    ...props.filters,
    categoryId: event.target.value ? Number(event.target.value) : null,
  };
  emit("update:filters", newFilters);
  emit("apply");
};

const handleStatusChange = (event) => {
  const newFilters = {
    ...props.filters,
    status: event.target.value,
  };
  emit("update:filters", newFilters);
  emit("apply");
};

// const handleMinPriceChange = (event) => {
//   const newFilters = {
//     ...props.filters,
//     minPrice: event.target.value ? Number(event.target.value) : null,
//   };
//   emit("update:filters", newFilters);
//   emit("apply");
// };

// const handleMaxPriceChange = (event) => {
//   const newFilters = {
//     ...props.filters,
//     maxPrice: event.target.value ? Number(event.target.value) : null,
//   };
//   emit("update:filters", newFilters);
//   emit("apply");
// };

const handleStockLevelChange = (event) => {
  const newFilters = {
    ...props.filters,
    stockLevel: event.target.value,
  };
  emit("update:filters", newFilters);
  emit("apply");
};

const handleSortChange = (event) => {
  const newFilters = {
    ...props.filters,
    sortBy: event.target.value || "",
    sortDirection: event.target.value ? "asc" : "",
  };
  emit("update:filters", newFilters);
  emit("apply");
};

const handleReset = () => {
  emit("reset");
};

// Preset handlers
const handlePresetChange = () => {
  if (selectedPresetId.value) {
    const filters = applyPreset(selectedPresetId.value);
    if (filters) {
      emit("update:filters", filters);
      emit("apply");
      notificationService.success('Thành công', 'Đã áp dụng preset');
    }
    selectedPresetId.value = '';
  }
};

const saveCurrentPreset = () => {
  if (!presetName.value.trim()) {
    notificationService.warning('Cảnh báo', 'Vui lòng nhập tên preset');
    return;
  }
  try {
    savePreset(presetName.value, props.filters);
    addToHistory(props.filters);
    notificationService.success('Thành công', `Đã lưu preset "${presetName.value}"`);
    presetName.value = '';
    showSavePresetModal.value = false;
  } catch (error) {
    notificationService.apiError(error, 'Không thể lưu preset');
  }
};

const applyHistoryItem = (historyId) => {
  const filters = applyHistory(historyId);
  if (filters) {
    emit("update:filters", filters);
    emit("apply");
    showHistoryDropdown.value = false;
    notificationService.success('Thành công', 'Đã áp dụng bộ lọc từ lịch sử');
  }
};

const formatHistoryItem = (item) => {
  const date = new Date(item.timestamp);
  const timeStr = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  const filters = [];
  if (item.filters.search) filters.push(`Tìm: ${item.filters.search}`);
  if (item.filters.brandId) filters.push('Brand');
  if (item.filters.categoryId) filters.push('Category');
  if (item.filters.status && item.filters.status !== 'all') filters.push(item.filters.status);
  return `${timeStr} - ${filters.join(', ') || 'Bộ lọc'}`;
};

const exportCurrentFilters = () => {
  try {
    downloadFilters(props.filters, `filters_${new Date().toISOString().split('T')[0]}.json`);
    notificationService.success('Thành công', 'Đã xuất bộ lọc');
  } catch (error) {
    notificationService.apiError(error, 'Không thể xuất bộ lọc');
  }
};

const handleImportFilters = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  try {
    const filters = await uploadFilters(file);
    emit("update:filters", filters);
    emit("apply");
    addToHistory(filters);
    notificationService.success('Thành công', 'Đã nhập bộ lọc');
  } catch (error) {
    notificationService.apiError(error, 'Không thể nhập bộ lọc');
  } finally {
    // Reset file input
    event.target.value = '';
  }
};

// Add to history when filters are applied
const originalApply = emit;
let lastFiltersHash = '';

const checkAndAddToHistory = () => {
  const filtersHash = JSON.stringify(props.filters);
  if (filtersHash !== lastFiltersHash) {
    addToHistory(props.filters);
    lastFiltersHash = filtersHash;
  }
};
</script>

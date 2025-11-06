<template>
  <div
    class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
  >
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
          placeholder="Tìm theo tên hoặc slug..."
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
          <option value="price">Giá thấp → cao</option>
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
  </div>
</template>

<script setup>
import { useProductFilters } from "@/composables/useProductFilters";
import { Status, StockLevel } from "@/utils/productConstants";

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
</script>

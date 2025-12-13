<template>
  <div class="relative">
    <div class="relative">
      <div
        class="flex items-center gap-3 px-4 py-2 border-2 border-white/30 rounded-lg bg-white/10 backdrop-blur-sm focus-within:border-white/60 focus-within:bg-white/20 transition-all"
      >
        <svg
          class="flex-shrink-0 text-white/90"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <circle cx="11" cy="11" r="8" />
          <path d="M21 21L16.65 16.65" />
        </svg>
        <input
          type="text"
          v-model="searchQuery"
          @input="handleSearch"
          @focus="showSuggestions = true"
          @blur="handleBlur"
          placeholder="Tìm kiếm sản phẩm, thương hiệu..."
          class="flex-1 bg-transparent border-none outline-none text-white placeholder:text-white/60 text-sm"
        />
        <button
          v-if="searchQuery"
          @click="clearSearch"
          class="flex-shrink-0 text-white/70 hover:text-white transition-colors"
        >
          <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          >
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>
      </div>

      <!-- Suggestions Dropdown -->
      <transition
        enter-active-class="transition-all duration-200 ease-out"
        leave-active-class="transition-all duration-200 ease-in"
        enter-from-class="opacity-0 scale-95 -translate-y-2"
        enter-to-class="opacity-100 scale-100 translate-y-0"
        leave-from-class="opacity-100 scale-100 translate-y-0"
        leave-to-class="opacity-0 scale-95 -translate-y-2"
      >
        <div
          v-if="showSuggestions && (suggestions.length > 0 || searchQuery)"
          class="absolute top-full left-0 right-0 mt-2 bg-white dark:bg-gray-800 rounded-xl shadow-2xl border border-gray-200 dark:border-gray-700 overflow-hidden z-[9999] max-h-96 overflow-y-auto"
        >
          <!-- Quick Filters -->
          <div v-if="!searchQuery" class="p-4">
            <h3
              class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3"
            >
              Tìm kiếm phổ biến:
            </h3>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="tag in popularSearches"
                :key="tag"
                @click="searchTag(tag)"
                class="px-3 py-1.5 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg text-sm font-medium hover:bg-purple-100 dark:hover:bg-purple-900/30 hover:text-purple-600 dark:hover:text-purple-400 transition-colors"
              >
                {{ tag }}
              </button>
            </div>
          </div>

          <!-- Search Suggestions -->
          <div v-if="searchQuery" class="p-2">
            <div v-if="suggestions.length > 0" class="space-y-1">
              <h3
                class="px-3 py-2 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase"
              >
                Sản phẩm
              </h3>
              <div
                v-for="item in suggestions"
                :key="item.id"
                @click="selectSuggestion(item)"
                class="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 cursor-pointer transition-colors"
              >
                <img
                  :src="item.imageUrl || '/placeholder-image.png'"
                  :alt="item.name"
                  class="w-12 h-12 rounded-lg object-cover"
                />
                <div class="flex-1 min-w-0">
                  <div class="text-xs text-gray-500 dark:text-gray-400 mb-0.5">
                    {{ item.brandName }}
                  </div>
                  <div
                    class="text-sm font-medium text-gray-900 dark:text-gray-100 truncate"
                  >
                    {{ item.name }}
                  </div>
                  <div
                    class="text-sm font-semibold text-purple-600 dark:text-purple-400"
                  >
                    {{ formatCurrency(item.price) }}
                  </div>
                </div>
              </div>
            </div>

            <!-- No Results -->
            <div v-else class="text-center py-8">
              <svg
                width="48"
                height="48"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                class="mx-auto mb-3 text-gray-400"
              >
                <path
                  d="M21 21L15 15M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              <p class="text-gray-600 dark:text-gray-400">
                Không tìm thấy sản phẩm
              </p>
            </div>

            <!-- View All Results -->
            <router-link
              v-if="searchQuery"
              :to="`/products?search=${searchQuery}`"
              class="flex items-center justify-between px-4 py-3 mt-2 border-t border-gray-200 dark:border-gray-700 text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 font-medium text-sm transition-colors"
            >
              <span>Xem tất cả kết quả cho "{{ searchQuery }}"</span>
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M5 12H19M19 12L12 5M19 12L12 19" />
              </svg>
            </router-link>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import { debounce } from "@/utils/debounce";
import { API_ENDPOINTS } from "@/config/api";
import logger from "@/utils/logger";
import axios from "axios";

const router = useRouter();

const searchQuery = ref("");
const suggestions = ref([]);
const showSuggestions = ref(false);
const isLoading = ref(false);

const popularSearches = [
  "Nike Air Max",
  "Adidas Ultraboost",
  "Jordan 1",
  "Running Shoes",
  "Basketball",
];

const handleSearch = debounce(async () => {
  if (searchQuery.value.length < 1) {
    suggestions.value = [];
    return;
  }

  isLoading.value = true;
  try {
    const response = await axios.get(API_ENDPOINTS.PRODUCTS.BASE, {
      params: {
        page: 0,
        size: 5,
        search: searchQuery.value,
      },
    });

    suggestions.value = response.data.content || [];
  } catch (error) {
    logger.error("Error fetching suggestions:", error);
    suggestions.value = [];
  } finally {
    isLoading.value = false;
  }
}, 300);

const handleBlur = () => {
  // Delay để cho phép click vào suggestion
  setTimeout(() => {
    showSuggestions.value = false;
  }, 200);
};

const clearSearch = () => {
  searchQuery.value = "";
  suggestions.value = [];
  showSuggestions.value = false;
};

const selectSuggestion = (product) => {
  router.push(`/home/products/${product.id}`);
  showSuggestions.value = false;
  searchQuery.value = "";
};

const searchTag = (tag) => {
  searchQuery.value = tag;
  handleSearch();
};

const formatCurrency = (value) => {
  if (value === null || value === undefined) return "0 ₫";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};
</script>

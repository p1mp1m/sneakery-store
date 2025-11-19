<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-7xl mx-auto px-4">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">
          Tất cả sản phẩm
        </h1>
        <p class="text-gray-600 dark:text-gray-400">
          Khám phá bộ sưu tập giày sneaker của chúng tôi
        </p>
      </div>

      <!-- Filters & Products Grid -->
      <div class="flex flex-col lg:flex-row gap-6">
        <!-- Sidebar Filters -->
        <aside class="w-full lg:w-64 flex-shrink-0">
          <div
            class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 sticky top-24"
          >
            <h3
              class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4"
            >
              Thương hiệu
            </h3>
            <div v-if="loadingBrands" class="mb-6">
              <div class="flex items-center gap-2">
                <div
                  class="w-4 h-4 border-2 border-purple-600 border-t-transparent rounded-full animate-spin"
                ></div>
                <span class="text-sm text-gray-600 dark:text-gray-400"
                  >Đang tải...</span
                >
              </div>
            </div>
            <div
              v-else-if="brands.length > 0"
              class="flex flex-wrap gap-2 mb-6"
            >
              <button
                v-for="brand in brands"
                :key="brand"
                @click="toggleBrand(brand)"
                :class="[
                  'px-4 py-2 rounded-full text-sm font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2',
                  selectedBrands.includes(brand)
                    ? 'bg-purple-600 text-white shadow-md hover:bg-purple-700'
                    : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600',
                ]"
              >
                {{ brand }}
              </button>
            </div>
            <div v-else class="mb-6">
              <p class="text-sm text-gray-500 dark:text-gray-400">
                Không có thương hiệu nào
              </p>
            </div>

            <h3
              class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4"
            >
              Danh mục
            </h3>
            <div v-if="loadingCategories" class="mb-6">
              <div class="flex items-center gap-2">
                <div
                  class="w-4 h-4 border-2 border-purple-600 border-t-transparent rounded-full animate-spin"
                ></div>
                <span class="text-sm text-gray-600 dark:text-gray-400"
                  >Đang tải...</span
                >
              </div>
            </div>
            <div v-else-if="categoryGroups.length > 0" class="mb-6 space-y-4">
              <!-- Category Groups (Parent with Children) -->
              <div
                v-for="group in categoryGroups"
                :key="group.id"
                class="space-y-2"
              >
                <!-- Parent Category Header (Clickable) -->
                <button
                  @click="toggleParentCategory(group)"
                  :class="[
                    'w-full flex items-center gap-2 px-3 py-2 rounded-lg border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2',
                    isParentSelected(group)
                      ? 'bg-gradient-to-r from-purple-600 to-indigo-600 border-purple-600 text-white shadow-md hover:from-purple-700 hover:to-indigo-700'
                      : 'bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20 border-purple-200 dark:border-purple-800 hover:from-purple-100 hover:to-indigo-100 dark:hover:from-purple-900/30 dark:hover:to-indigo-900/30',
                  ]"
                >
                  <i
                    :class="[
                      'material-icons text-lg',
                      isParentSelected(group)
                        ? 'text-white'
                        : 'text-purple-600 dark:text-purple-400',
                    ]"
                    >{{
                      getCategoryIcon({ slug: group.slug, name: group.name })
                    }}</i
                  >
                  <span
                    :class="[
                      'font-semibold text-sm',
                      isParentSelected(group)
                        ? 'text-white'
                        : 'text-gray-900 dark:text-gray-100',
                    ]"
                    >{{ group.name }}</span
                  >
                  <span
                    :class="[
                      'ml-auto text-xs',
                      isParentSelected(group)
                        ? 'text-white/80'
                        : 'text-gray-500 dark:text-gray-400',
                    ]"
                    >({{ group.children?.length || 0 }})</span
                  >
                </button>
                <!-- Child Categories -->
                <div class="flex flex-wrap gap-2 pl-4">
                  <button
                    v-for="child in group.children"
                    :key="child.id"
                    @click="toggleCategory(child.slug)"
                    :class="[
                      'px-3 py-1.5 rounded-full text-xs font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2',
                      selectedCategories.includes(child.slug)
                        ? 'bg-purple-600 text-white shadow-md hover:bg-purple-700'
                        : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600',
                    ]"
                  >
                    {{ child.name }}
                  </button>
                </div>
              </div>
            </div>
            <div
              v-else-if="categories.length > 0"
              class="flex flex-wrap gap-2 mb-6"
            >
              <!-- Fallback: Show flat list if groups not available -->
              <button
                v-for="category in categories"
                :key="category.slug"
                @click="toggleCategory(category.slug)"
                :class="[
                  'px-4 py-2 rounded-full text-sm font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2',
                  selectedCategories.includes(category.slug)
                    ? 'bg-purple-600 text-white shadow-md hover:bg-purple-700'
                    : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600',
                ]"
              >
                {{ category.name }}
              </button>
            </div>
            <div v-else class="mb-6">
              <p class="text-sm text-gray-500 dark:text-gray-400">
                Không có danh mục nào
              </p>
            </div>

            <h3
              class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4"
            >
              Khoảng giá
            </h3>
            <div class="mb-6">
              <div class="mb-4">
                <div class="flex items-center justify-between mb-2">
                  <span class="text-sm text-gray-600 dark:text-gray-400"
                    >Từ</span
                  >
                  <span
                    class="text-sm font-semibold text-gray-900 dark:text-gray-100"
                    >{{ formatPrice(minPrice) }}</span
                  >
                </div>
                <div class="flex items-center justify-between mb-2">
                  <span class="text-sm text-gray-600 dark:text-gray-400"
                    >Đến</span
                  >
                  <span
                    class="text-sm font-semibold text-gray-900 dark:text-gray-100"
                    >{{ formatPrice(maxPrice) }}</span
                  >
                </div>
              </div>

              <!-- Range Slider -->
              <div class="relative h-8">
                <div
                  class="absolute w-full h-2 top-1/2 -translate-y-1/2 bg-gray-200 dark:bg-gray-700 rounded-lg"
                ></div>
                <div
                  v-if="minPrice > 0 || maxPrice < 20000000"
                  class="absolute h-2 top-1/2 -translate-y-1/2 bg-purple-600 rounded-lg"
                  :style="{
                    left: `${(minPrice / 20000000) * 100}%`,
                    width: `${((maxPrice - minPrice) / 20000000) * 100}%`,
                  }"
                ></div>

                <input
                  type="range"
                  :min="0"
                  :max="20000000"
                  :step="100000"
                  v-model.number="minPrice"
                  @input="handleMinPriceChange"
                  class="absolute w-full h-2 top-1/2 -translate-y-1/2 bg-transparent appearance-none cursor-pointer range-slider range-slider-min"
                  style="z-index: 10"
                />
                <input
                  type="range"
                  :min="0"
                  :max="20000000"
                  :step="100000"
                  v-model.number="maxPrice"
                  @input="handleMaxPriceChange"
                  class="absolute w-full h-2 top-1/2 -translate-y-1/2 bg-transparent appearance-none cursor-pointer range-slider range-slider-max"
                  style="z-index: 20"
                />
              </div>

              <div
                class="flex items-center justify-between mt-2 text-xs text-gray-500 dark:text-gray-400"
              >
                <span>0đ</span>
                <span>20.000.000đ</span>
              </div>
            </div>

            <button
              @click="clearFilters"
              class="w-full px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors font-medium focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2"
              aria-label="Xóa tất cả bộ lọc"
            >
              Xóa bộ lọc
            </button>
          </div>
        </aside>

        <!-- Products Main -->
        <div class="flex-1">
          <!-- Sort Bar -->
          <div
            class="flex items-center justify-between mb-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-4"
          >
            <div class="text-sm text-gray-600 dark:text-gray-400">
              Tìm thấy
              <span class="font-semibold text-gray-900 dark:text-gray-100">{{
                filteredTotalItems
              }}</span>
              sản phẩm
            </div>
            <select
              v-model="sortBy"
              @change="handleSortChange"
              class="px-4 py-2 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-lg text-gray-700 dark:text-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500"
              aria-label="Sắp xếp sản phẩm"
            >
              <option value="newest">Mới nhất</option>
              <option value="price-asc">Giá: Thấp → Cao</option>
              <option value="price-desc">Giá: Cao → Thấp</option>
              <option value="name-asc">Tên: A → Z</option>
              <option value="name-desc">Tên: Z → A</option>
            </select>
          </div>

          <!-- Loading State -->
          <div
            v-if="loading"
            class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-6"
            role="status"
            aria-live="polite"
          >
            <LoadingSkeleton
              v-for="n in 8"
              :key="n"
              type="card"
              :show-image="true"
            />
            <span class="sr-only">Đang tải danh sách sản phẩm</span>
          </div>

          <!-- Error State -->
          <div
            v-else-if="error"
            class="text-center py-16 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
          >
            <svg
              width="64"
              height="64"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              class="mx-auto mb-4 text-red-500"
            >
              <path
                d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z"
                stroke="currentColor"
                stroke-width="2"
              />
              <path
                d="M12 8V12"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
              />
              <path
                d="M12 16H12.01"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
              />
            </svg>
            <p class="text-gray-900 dark:text-gray-100 mb-4">{{ error }}</p>
            <button
              @click="fetchProducts"
              class="px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2"
              aria-label="Thử tải lại danh sách sản phẩm"
            >
              Thử lại
            </button>
          </div>

          <!-- Products Grid -->
          <div
            v-else-if="products.length > 0"
            class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-6"
          >
            <ProductCard
              v-for="product in displayedProducts"
              :key="product.id"
              :product="product"
            />
          </div>

          <!-- Empty State -->
          <div
            v-else
            class="text-center py-16 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
          >
            <svg
              width="80"
              height="80"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              class="mx-auto mb-4 text-gray-400"
            >
              <path
                d="M3 3L5 21L12 18L19 21L21 3M5 3H19M8 7H16"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
            <h3
              class="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-2"
            >
              Không tìm thấy sản phẩm
            </h3>
            <p class="text-gray-600 dark:text-gray-400 mb-6">
              Hãy thử điều chỉnh bộ lọc hoặc mua sắm sau
            </p>
            <button
              @click="clearFilters"
              class="px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2"
              aria-label="Xóa tất cả bộ lọc"
            >
              Xóa bộ lọc
            </button>
          </div>

          <!-- Pagination -->
          <div
            v-if="filteredTotalPages > 1"
            class="flex items-center justify-center gap-4 mt-8"
          >
            <button
              @click="prevPage"
              :disabled="currentPage === 0"
              class="px-4 py-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2"
              aria-label="Trang trước"
            >
              Trước
            </button>

            <span
              class="text-sm text-gray-600 dark:text-gray-400"
              aria-live="polite"
            >
              Trang {{ currentPage + 1 }} / {{ filteredTotalPages }}
            </span>

            <button
              @click="nextPage"
              :disabled="currentPage >= filteredTotalPages - 1"
              class="px-4 py-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2"
              aria-label="Trang sau"
            >
              Sau
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import productService from "@/services/productService";
import { useFlashSaleStore } from "@/stores/flashSale";
import ProductCard from "@/assets/components/products/ProductCard.vue";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import { API_ENDPOINTS } from "@/config/api";
import logger from "@/utils/logger";
import axios from "axios";
import { useProductImageStore } from "@/stores/productImages";

// Router
const route = useRoute();

// Stores
const flashSaleStore = useFlashSaleStore();

// State
const products = ref([]);
const loading = ref(false);
const error = ref(null);
const currentPage = ref(0);
const pageSize = ref(8);
const totalPages = ref(0);
const totalItems = ref(0);
const sortBy = ref("newest"); // Mặc định: Mới nhất
const productImageStore = useProductImageStore();

// Filters
const brands = ref([]);
const loadingBrands = ref(false);
const selectedBrands = ref([]);
const categories = ref([]);
const loadingCategories = ref(false);
const selectedCategories = ref([]);
const categoryGroups = ref([]); // Store category groups to map child to parent
const minPrice = ref(0);
const maxPrice = ref(20000000);

// Icon mapping for categories (same as HomePage)
const categoryIconMap = {
  // Root categories
  running: "directions_run",
  basketball: "sports_basketball",
  skateboard: "skateboarding",
  skateboarding: "skateboarding",
  lifestyle: "style",
  sneakers: "sports",

  // Child categories - Sneakers
  "high-top": "height",
  "low-top": "vertical_align_bottom",
  "mid-top": "vertical_align_center",
  "slip-on": "checkroom",

  // Child categories - Lifestyle
  casual: "style",
  streetwear: "streetview",
  retro: "history",

  // Legacy mappings
  training: "fitness_center",
  limited: "stars",
};

// Get icon for category based on slug or name
const getCategoryIcon = (category) => {
  const slug =
    category.slug?.toLowerCase() ||
    category.name?.toLowerCase().replace(/\s+/g, "-") ||
    "";
  // Try exact match first
  if (categoryIconMap[slug]) {
    return categoryIconMap[slug];
  }
  // Try partial match
  for (const [key, icon] of Object.entries(categoryIconMap)) {
    if (slug.includes(key) || key.includes(slug)) {
      return icon;
    }
  }
  // Default icon
  return "category";
};

// Computed - Get filtered and sorted products (without pagination)
const filteredAndSortedProducts = computed(() => {
  let filteredProducts = [...products.value];

  // Apply brand filter
  if (selectedBrands.value.length > 0) {
    filteredProducts = filteredProducts.filter((product) => {
      // Check both brandName and brand.name
      const productBrandName = product.brandName || product.brand?.name || "";
      return selectedBrands.value.includes(productBrandName);
    });
  }

  // Apply category filter
  if (selectedCategories.value.length > 0) {
    // Normalize slug for comparison
    const normalizeSlug = (slug) => {
      if (!slug) return "";
      return slug.toLowerCase().trim().replace(/\s+/g, "-");
    };

    const selectedSlugsNormalized = selectedCategories.value.map((slug) =>
      normalizeSlug(slug)
    );

    // Build a map of child category slugs to their parent category slugs
    const childToParentMap = new Map();
    // Build a map of parent category slugs to their child category slugs
    const parentToChildrenMap = new Map();

    // Only build maps if categoryGroups is available
    if (categoryGroups.value && categoryGroups.value.length > 0) {
      categoryGroups.value.forEach((group) => {
        const parentSlug = normalizeSlug(group.slug);
        if (group.children && Array.isArray(group.children)) {
          const childSlugs = [];
          group.children.forEach((child) => {
            const childSlug = normalizeSlug(child.slug);
            childToParentMap.set(childSlug, parentSlug);
            childSlugs.push(childSlug);
          });
          parentToChildrenMap.set(parentSlug, childSlugs);
        }
      });
    }

    filteredProducts = filteredProducts.filter((product) => {
      // Check if product has any of the selected categories
      if (product.categories && Array.isArray(product.categories)) {
        const productCategorySlugs = product.categories
          .map((cat) => normalizeSlug(cat.slug || cat.name))
          .filter(Boolean);

        // Check direct match with selected categories (both parent and child)
        const hasDirectMatch = productCategorySlugs.some((catSlug) =>
          selectedSlugsNormalized.includes(catSlug)
        );
        if (hasDirectMatch) return true;

        // Only check parent/child matches if we have categoryGroups mapping
        if (childToParentMap.size > 0 || parentToChildrenMap.size > 0) {
          // Check if product belongs to parent category of selected child categories
          // (since products might only be linked to parent, not children)
          const hasParentMatch = selectedSlugsNormalized.some(
            (selectedSlug) => {
              const parentSlug = childToParentMap.get(selectedSlug);
              if (parentSlug) {
                return productCategorySlugs.includes(parentSlug);
              }
              return false;
            }
          );
          if (hasParentMatch) return true;

          // Check if product belongs to any child category of selected parent categories
          const hasChildMatch = selectedSlugsNormalized.some((selectedSlug) => {
            const childSlugs = parentToChildrenMap.get(selectedSlug);
            if (childSlugs && childSlugs.length > 0) {
              return productCategorySlugs.some((catSlug) =>
                childSlugs.includes(catSlug)
              );
            }
            return false;
          });
          if (hasChildMatch) return true;
        }
      }

      // Fallback: check categoryIds if categories array is not available
      if (product.categoryIds && Array.isArray(product.categoryIds)) {
        // Match by category slug if we have category mapping
        const categorySlugs = categories.value
          .filter((cat) => product.categoryIds.includes(cat.id))
          .map((cat) => normalizeSlug(cat.slug));

        // Check direct match
        const hasDirectMatch = categorySlugs.some((slug) =>
          selectedSlugsNormalized.includes(slug)
        );
        if (hasDirectMatch) return true;

        // Only check parent/child matches if we have categoryGroups mapping
        if (childToParentMap.size > 0 || parentToChildrenMap.size > 0) {
          // Check parent match (selected child, product has parent)
          const hasParentMatch = selectedSlugsNormalized.some(
            (selectedSlug) => {
              const parentSlug = childToParentMap.get(selectedSlug);
              if (parentSlug) {
                return categorySlugs.includes(parentSlug);
              }
              return false;
            }
          );
          if (hasParentMatch) return true;

          // Check child match (selected parent, product has child)
          const hasChildMatch = selectedSlugsNormalized.some((selectedSlug) => {
            const childSlugs = parentToChildrenMap.get(selectedSlug);
            if (childSlugs && childSlugs.length > 0) {
              return categorySlugs.some((catSlug) =>
                childSlugs.includes(catSlug)
              );
            }
            return false;
          });
          if (hasChildMatch) return true;
        }
      }

      return false;
    });

    logger.log("Category filter applied:", {
      selectedCategories: selectedCategories.value,
      categoryGroupsCount: categoryGroups.value.length,
      childToParentMap: Object.fromEntries(childToParentMap),
      parentToChildrenMap: Object.fromEntries(parentToChildrenMap),
      filteredCount: filteredProducts.length,
    });
  }

  // Apply price range filter
  if (minPrice.value > 0 || maxPrice.value < 20000000) {
    filteredProducts = filteredProducts.filter((product) => {
      const price = product.price || product.priceBase || 0;
      return price >= minPrice.value && price <= maxPrice.value;
    });
  }

  // Apply sorting
  if (sortBy.value) {
    filteredProducts = sortProducts(filteredProducts, sortBy.value);
  }

  return filteredProducts;
});

// Computed - Total items after filtering
const filteredTotalItems = computed(() => {
  return filteredAndSortedProducts.value.length;
});

// Computed - Total pages after filtering
const filteredTotalPages = computed(() => {
  return Math.ceil(filteredTotalItems.value / pageSize.value);
});

// Computed - Apply pagination to filtered and sorted products
const displayedProducts = computed(() => {
  const start = currentPage.value * pageSize.value;
  const end = start + pageSize.value;
  return filteredAndSortedProducts.value.slice(start, end);
});

// Sort products based on sortBy value
const sortProducts = (productsList, sortOption) => {
  const sorted = [...productsList];

  switch (sortOption) {
    case "price-asc":
      return sorted.sort((a, b) => {
        const priceA = a.price || a.priceBase || 0;
        const priceB = b.price || b.priceBase || 0;
        return priceA - priceB;
      });

    case "price-desc":
      return sorted.sort((a, b) => {
        const priceA = a.price || a.priceBase || 0;
        const priceB = b.price || b.priceBase || 0;
        return priceB - priceA;
      });

    case "name-asc":
      return sorted.sort((a, b) => {
        const nameA = (a.name || "").toLowerCase();
        const nameB = (b.name || "").toLowerCase();
        return nameA.localeCompare(nameB, "vi");
      });

    case "name-desc":
      return sorted.sort((a, b) => {
        const nameA = (a.name || "").toLowerCase();
        const nameB = (b.name || "").toLowerCase();
        return nameB.localeCompare(nameA, "vi");
      });

    case "newest":
      // Sort by ID descending (assuming higher ID = newer product)
      // Or by createdAt if available
      return sorted.sort((a, b) => {
        if (a.createdAt && b.createdAt) {
          return new Date(b.createdAt) - new Date(a.createdAt);
        }
        // Fallback to ID
        return (b.id || 0) - (a.id || 0);
      });

    default:
      return sorted;
  }
};

// Methods
const fetchProducts = async () => {
  try {
    loading.value = true;
    error.value = null;

    const response = await productService.getProducts(0, 50);

    // Handle Spring Data Page structure
    let productData = [];
    let totalElements = 0;

    if (response.data) {
      // Spring Data Page format: { content: [...], totalElements: 100, totalPages: 10, ... }
      if (Array.isArray(response.data.content)) {
        productData = response.data.content;
        totalElements = response.data.totalElements || 0;
      }
      // If response.data is already an array
      else if (Array.isArray(response.data)) {
        productData = response.data;
        totalElements = response.data.length;
      }
      // If response.data is a single object (shouldn't happen but handle it)
      else {
        productData = [];
        totalElements = 0;
      }
    }

    // Normalize product data: đảm bảo có categoryIds từ categories array
    const normalizedProducts = productData.map((product) => {
      // Nếu có categories array nhưng không có categoryIds, tạo categoryIds từ categories
      if (
        product.categories &&
        Array.isArray(product.categories) &&
        (!product.categoryIds || !Array.isArray(product.categoryIds))
      ) {
        product.categoryIds = product.categories
          .map((c) => c.id || c)
          .filter((id) => id != null);
      }
      return product;
    });

    products.value = normalizedProducts;
    // totalItems and totalPages will be recalculated in computed based on filtered and sorted products
    // We keep the original total for reference
    totalItems.value = totalElements;
  } catch (err) {
    // Better error message
    if (err.response?.status === 404) {
      error.value = "Không tìm thấy sản phẩm nào.";
    } else if (err.response?.status === 500) {
      error.value = "Lỗi server. Vui lòng thử lại sau.";
    } else if (err.response?.data?.message) {
      error.value = err.response.data.message;
    } else {
      error.value = `Không thể tải danh sách sản phẩm: ${
        err.message || "Vui lòng thử lại."
      }`;
    }
  } finally {
    loading.value = false;
  }
};

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < filteredTotalPages.value - 1) {
    currentPage.value++;
  }
};

const handleSortChange = () => {
  // Reset to first page when sort changes
  currentPage.value = 0;
  // Sorting is handled in computed, no need to refetch
};

const toggleBrand = (brand) => {
  const index = selectedBrands.value.indexOf(brand);
  if (index > -1) {
    selectedBrands.value.splice(index, 1);
  } else {
    selectedBrands.value.push(brand);
  }
  handleBrandChange();
};

// Check if parent category is selected (either directly or through all children)
const isParentSelected = (group) => {
  const normalizeSlug = (slug) => {
    if (!slug) return "";
    return slug.toLowerCase().trim().replace(/\s+/g, "-");
  };

  const parentSlug = normalizeSlug(group.slug);

  // Check if parent slug is directly selected
  if (
    selectedCategories.value.some((slug) => normalizeSlug(slug) === parentSlug)
  ) {
    return true;
  }

  // Check if all children are selected
  if (
    group.children &&
    Array.isArray(group.children) &&
    group.children.length > 0
  ) {
    const allChildrenSelected = group.children.every((child) => {
      const childSlug = normalizeSlug(child.slug);
      return selectedCategories.value.some(
        (slug) => normalizeSlug(slug) === childSlug
      );
    });
    return allChildrenSelected;
  }

  return false;
};

// Toggle parent category selection
const toggleParentCategory = (group) => {
  const normalizeSlug = (slug) => {
    if (!slug) return "";
    return slug.toLowerCase().trim().replace(/\s+/g, "-");
  };

  const parentSlug = normalizeSlug(group.slug);
  const parentSlugIndex = selectedCategories.value.findIndex(
    (slug) => normalizeSlug(slug) === parentSlug
  );

  if (parentSlugIndex > -1) {
    // Parent is selected, deselect it
    selectedCategories.value.splice(parentSlugIndex, 1);

    // Also deselect all children
    if (group.children && Array.isArray(group.children)) {
      group.children.forEach((child) => {
        const childSlug = normalizeSlug(child.slug);
        const childIndex = selectedCategories.value.findIndex(
          (slug) => normalizeSlug(slug) === childSlug
        );
        if (childIndex > -1) {
          selectedCategories.value.splice(childIndex, 1);
        }
      });
    }
  } else {
    // Parent is not selected, select it
    selectedCategories.value.push(group.slug);

    // Optionally: also select all children (or keep them unselected for flexibility)
    // For now, we'll just select parent, user can manually select children if needed
  }

  handleCategoryChange();
};

const toggleCategory = (categorySlug) => {
  const index = selectedCategories.value.indexOf(categorySlug);
  if (index > -1) {
    selectedCategories.value.splice(index, 1);
  } else {
    selectedCategories.value.push(categorySlug);
  }
  handleCategoryChange();
};

const handleCategoryChange = () => {
  // Reset to first page when filter changes
  currentPage.value = 0;
  // Filtering is handled in computed, no need to refetch
};

const handleBrandChange = () => {
  // Reset to first page when filter changes
  currentPage.value = 0;
  // Filtering is handled in computed, no need to refetch
};

const handleMinPriceChange = () => {
  // Ensure minPrice doesn't exceed maxPrice
  if (minPrice.value > maxPrice.value) {
    minPrice.value = maxPrice.value;
  }
  // Ensure values are within bounds
  if (minPrice.value < 0) minPrice.value = 0;
  if (minPrice.value > 20000000) minPrice.value = 20000000;
  // Reset to first page when filter changes
  currentPage.value = 0;
  // Filtering is handled in computed, no need to refetch
};

const handleMaxPriceChange = () => {
  // Ensure maxPrice doesn't go below minPrice
  if (maxPrice.value < minPrice.value) {
    maxPrice.value = minPrice.value;
  }
  // Ensure values are within bounds
  if (maxPrice.value < 0) maxPrice.value = 0;
  if (maxPrice.value > 20000000) maxPrice.value = 20000000;
  // Reset to first page when filter changes
  currentPage.value = 0;
  // Filtering is handled in computed, no need to refetch
};

const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const fetchBrands = async () => {
  loadingBrands.value = true;
  try {
    // Try to get brands from products first (extract unique brands)
    const response = await productService.getProducts(0, 1000); // Get more products to extract brands
    let productData = [];

    if (response.data) {
      if (Array.isArray(response.data.content)) {
        productData = response.data.content;
      } else if (Array.isArray(response.data)) {
        productData = response.data;
      }
    }

    // Extract unique brands from products (check both brandName and brand.name)
    const uniqueBrands = [
      ...new Set(
        productData.map((p) => p.brandName || p.brand?.name).filter(Boolean)
      ),
    ];
    brands.value = uniqueBrands.sort((a, b) => a.localeCompare(b, "vi"));

    // If no brands found, try API endpoint
    if (brands.value.length === 0) {
      try {
        const brandsResponse = await axios.get(
          API_ENDPOINTS.ADMIN_PRODUCTS.BRANDS
        );
        if (Array.isArray(brandsResponse.data)) {
          brands.value = brandsResponse.data
            .map((b) => b.name || b)
            .filter(Boolean)
            .sort((a, b) => a.localeCompare(b, "vi"));
        }
      } catch (apiError) {
        logger.warn(
          "Could not fetch brands from API, using empty list:",
          apiError
        );
      }
    }
  } catch (error) {
    logger.error("Error fetching brands:", error);
    // Fallback to empty array
    brands.value = [];
  } finally {
    loadingBrands.value = false;
  }
};

const fetchCategories = async () => {
  loadingCategories.value = true;
  try {
    // First, fetch category groups to get parent-child mapping
    try {
      const groupsResponse = await axios.get(
        API_ENDPOINTS.PRODUCTS.CATEGORIES_GROUPS
      );
      if (
        Array.isArray(groupsResponse.data) &&
        groupsResponse.data.length > 0
      ) {
        categoryGroups.value = groupsResponse.data;
        logger.log(
          "Fetched category groups:",
          categoryGroups.value.length,
          categoryGroups.value
        );

        // Extract all child categories from groups for filter display
        const allChildren = [];
        groupsResponse.data.forEach((group) => {
          if (group.children && Array.isArray(group.children)) {
            allChildren.push(...group.children);
          }
        });

        categories.value = allChildren
          .map((cat) => ({
            id: cat.id,
            name: cat.name,
            slug: cat.slug || cat.name.toLowerCase().replace(/\s+/g, "-"),
          }))
          .sort((a, b) => a.name.localeCompare(b.name, "vi"));

        logger.log(
          "Fetched categories from groups:",
          categories.value.length,
          categories.value
        );
        return; // Success, exit early
      }
    } catch (groupsError) {
      logger.warn(
        "Could not fetch category groups, trying categories endpoint:",
        groupsError
      );
    }

    // Try public API endpoint (fallback)
    try {
      const categoriesResponse = await axios.get(
        API_ENDPOINTS.PRODUCTS.CATEGORIES
      );
      if (
        Array.isArray(categoriesResponse.data) &&
        categoriesResponse.data.length > 0
      ) {
        // Categories from public API are already filtered to child categories
        const childCategories = categoriesResponse.data.map((cat) => ({
          id: cat.id,
          name: cat.name,
          slug: cat.slug || cat.name.toLowerCase().replace(/\s+/g, "-"),
        }));
        categories.value = childCategories.sort((a, b) =>
          a.name.localeCompare(b.name, "vi")
        );
        logger.log(
          "Fetched categories from public API:",
          categories.value.length,
          categories.value
        );
        return; // Success, exit early
      }
    } catch (publicApiError) {
      logger.warn(
        "Could not fetch categories from public API, trying fallback methods:",
        publicApiError
      );
    }

    // Fallback: Try admin API endpoint
    try {
      const categoriesResponse = await axios.get(
        API_ENDPOINTS.ADMIN_PRODUCTS.CATEGORIES
      );
      if (
        Array.isArray(categoriesResponse.data) &&
        categoriesResponse.data.length > 0
      ) {
        // Filter only child categories (with parentId)
        const childCategories = categoriesResponse.data
          .filter((cat) => cat.parentId != null)
          .map((cat) => ({
            id: cat.id,
            name: cat.name,
            slug: cat.slug || cat.name.toLowerCase().replace(/\s+/g, "-"),
          }));
        categories.value = childCategories.sort((a, b) =>
          a.name.localeCompare(b.name, "vi")
        );
        logger.log(
          "Fetched categories from admin API:",
          categories.value.length,
          categories.value
        );
        return; // Success, exit early
      }
    } catch (adminApiError) {
      logger.warn(
        "Could not fetch categories from admin API, trying to extract from products:",
        adminApiError
      );
    }

    // Last fallback: Extract categories from products
    try {
      const response = await productService.getProducts(0, 1000);
      let productData = [];

      if (response.data) {
        if (Array.isArray(response.data.content)) {
          productData = response.data.content;
        } else if (Array.isArray(response.data)) {
          productData = response.data;
        }
      }

      // Extract unique categories from products
      const categoryMap = new Map();
      productData.forEach((product) => {
        // Check if product has categories array
        if (product.categories && Array.isArray(product.categories)) {
          product.categories.forEach((cat) => {
            if (cat && (cat.slug || cat.name)) {
              const slug =
                cat.slug || cat.name.toLowerCase().replace(/\s+/g, "-");
              const name = cat.name || cat.slug;
              const id = cat.id;
              if (slug && name) {
                categoryMap.set(slug, { id, name, slug });
              }
            }
          });
        }
      });

      categories.value = Array.from(categoryMap.values()).sort((a, b) =>
        a.name.localeCompare(b.name, "vi")
      );

      logger.log(
        "Extracted categories from products:",
        categories.value.length,
        categories.value
      );
    } catch (productsError) {
      logger.warn("Could not extract categories from products:", productsError);
    }

    // If still no categories found, use hardcoded fallback
    if (categories.value.length === 0) {
      logger.warn(
        "No categories found from any source, using hardcoded fallback"
      );
      categories.value = [
        { id: 1, name: "High Top", slug: "high-top" },
        { id: 2, name: "Low Top", slug: "low-top" },
        { id: 3, name: "Mid Top", slug: "mid-top" },
        { id: 4, name: "Slip On", slug: "slip-on" },
        { id: 5, name: "Casual", slug: "casual" },
        { id: 6, name: "Streetwear", slug: "streetwear" },
        { id: 7, name: "Retro", slug: "retro" },
        { id: 8, name: "Running", slug: "running" },
        { id: 9, name: "Basketball", slug: "basketball" },
        { id: 10, name: "Skateboard", slug: "skateboard" },
      ];
    }
  } catch (error) {
    logger.error("Error fetching categories:", error);
    // Fallback to hardcoded categories
    categories.value = [
      { id: 1, name: "High Top", slug: "high-top" },
      { id: 2, name: "Low Top", slug: "low-top" },
      { id: 3, name: "Mid Top", slug: "mid-top" },
      { id: 4, name: "Slip On", slug: "slip-on" },
      { id: 5, name: "Casual", slug: "casual" },
      { id: 6, name: "Streetwear", slug: "streetwear" },
      { id: 7, name: "Retro", slug: "retro" },
      { id: 8, name: "Running", slug: "running" },
      { id: 9, name: "Basketball", slug: "basketball" },
      { id: 10, name: "Skateboard", slug: "skateboard" },
    ];
  } finally {
    loadingCategories.value = false;
  }
};

const clearFilters = () => {
  selectedBrands.value = [];
  selectedCategories.value = [];
  minPrice.value = 0;
  maxPrice.value = 20000000;
  sortBy.value = "newest"; // Reset to default: Mới nhất
  currentPage.value = 0;
  // No need to refetch, filtering is handled in computed
};

// Watch route query params for brand filter
watch(
  () => route.query.brand,
  (brandParam) => {
    if (brandParam) {
      const brandName = decodeURIComponent(brandParam);
      // Apply filter even if brand is not in brands list yet
      // (brand might exist in products but not extracted yet)
      if (!selectedBrands.value.includes(brandName)) {
        selectedBrands.value = [brandName];
        currentPage.value = 0;
      }
    } else {
      // If no brand in query, clear selection
      selectedBrands.value = [];
    }
  }
);

// Watch brands array to apply filter when brands are loaded
watch(
  () => brands.value,
  (newBrands) => {
    // Apply brand filter from query params if present and brands are now loaded
    if (newBrands.length > 0 && route.query.brand) {
      const brandName = decodeURIComponent(route.query.brand);
      if (!selectedBrands.value.includes(brandName)) {
        selectedBrands.value = [brandName];
        currentPage.value = 0;
      }
    }
  }
);

// Watch route query params for category filter
watch(
  () => route.query.category,
  (categoryParam) => {
    if (categoryParam) {
      const categorySlug = decodeURIComponent(categoryParam).trim();
      logger.log("Category filter from query params:", categorySlug);
      // Apply filter even if category is not in categories list yet
      // Add to selection if not already selected (multi-select support)
      if (!selectedCategories.value.includes(categorySlug)) {
        selectedCategories.value.push(categorySlug);
        currentPage.value = 0;
        logger.log(
          "Applied category filter:",
          categorySlug,
          "Selected categories:",
          selectedCategories.value
        );
      }
    } else {
      // If no category in query, clear selection
      selectedCategories.value = [];
      logger.log("Cleared category filter");
    }
  },
  { immediate: true }
);

// Watch categoryGroups to apply filter when groups are loaded (for parent-child mapping)
watch(
  () => categoryGroups.value,
  (newGroups) => {
    // Apply category filter from query params if present and groups are now loaded
    if (newGroups.length > 0 && route.query.category) {
      const categorySlug = decodeURIComponent(route.query.category).trim();
      // Add to selection if not already selected (multi-select support)
      if (!selectedCategories.value.includes(categorySlug)) {
        selectedCategories.value.push(categorySlug);
        currentPage.value = 0;
        logger.log(
          "Applied category filter after groups loaded:",
          categorySlug,
          "Selected categories:",
          selectedCategories.value
        );
      }
    }
  }
);

// Watch categories array to apply filter when categories are loaded (fallback)
watch(
  () => categories.value,
  (newCategories) => {
    // Only apply if categoryGroups is empty (fallback scenario)
    if (
      newCategories.length > 0 &&
      categoryGroups.value.length === 0 &&
      route.query.category
    ) {
      const categorySlug = decodeURIComponent(route.query.category).trim();
      // Add to selection if not already selected (multi-select support)
      if (!selectedCategories.value.includes(categorySlug)) {
        selectedCategories.value.push(categorySlug);
        currentPage.value = 0;
        logger.log(
          "Applied category filter after categories loaded (fallback):",
          categorySlug,
          "Selected categories:",
          selectedCategories.value
        );
      }
    }
  }
);

// Initial load
onMounted(async () => {
  // Fetch flash sales if not already loaded
  if (flashSaleStore.activeFlashSales.length === 0) {
    try {
      await flashSaleStore.fetchActiveFlashSales();
    } catch (error) {
      logger.warn("Failed to fetch flash sales in ProductListPage:", error);
    }
  }

  // ⬅️ Thêm loadAll ảnh vào đây
  await Promise.all([
    fetchProducts(),
    fetchBrands(),
    fetchCategories(),
    productImageStore.loadAll(), // 🟣 LOAD TOÀN BỘ ẢNH Ở ĐÂY
  ]);

  // Apply brand filter from query params if present (after brands are loaded)
  if (route.query.brand) {
    const brandName = decodeURIComponent(route.query.brand).trim();
    logger.log("Applying brand filter on mount:", brandName);
    const normalizedBrandName = brandName.toLowerCase();
    const isAlreadySelected = selectedBrands.value.some(
      (b) => b.toLowerCase() === normalizedBrandName
    );
    if (!isAlreadySelected) {
      selectedBrands.value = [brandName];
      currentPage.value = 0;
      logger.log(
        "Applied brand filter on mount:",
        brandName,
        "Selected brands:",
        selectedBrands.value
      );
    }
  }

  // Apply category filter from query params if present (after categories are loaded)
  if (route.query.category) {
    const categorySlug = decodeURIComponent(route.query.category).trim();
    logger.log("Applying category filter on mount:", categorySlug);
    // Add to selection if not already selected (multi-select support)
    if (!selectedCategories.value.includes(categorySlug)) {
      selectedCategories.value.push(categorySlug);
      currentPage.value = 0;
      logger.log(
        "Applied category filter on mount:",
        categorySlug,
        "Selected categories:",
        selectedCategories.value
      );
    }
  }

  // Log products, brands, and categories for debugging
  logger.log("Products loaded:", products.value.length);
  logger.log("Brands loaded:", brands.value.length, brands.value);
  logger.log("Categories loaded:", categories.value.length, categories.value);
  logger.log("Selected brands:", selectedBrands.value);
  logger.log("Selected categories:", selectedCategories.value);

  // Debug: Log sample product categories
  if (products.value.length > 0) {
    const sampleProduct = products.value[0];
    logger.log("Sample product categories:", sampleProduct.categories);
    logger.log("Sample product categoryIds:", sampleProduct.categoryIds);
    logger.log("Sample product:", {
      id: sampleProduct.id,
      name: sampleProduct.name,
      categories: sampleProduct.categories,
      categoryIds: sampleProduct.categoryIds,
    });
  }
});
</script>

<style scoped>
.range-slider {
  -webkit-appearance: none;
  appearance: none;
  height: 8px;
  outline: none;
  margin: 0;
  padding: 0;
  width: 100%;
}

/* Min slider - only thumb is interactive */
.range-slider-min {
  pointer-events: none;
}

.range-slider-min::-webkit-slider-thumb {
  pointer-events: auto;
  z-index: 30;
}

.range-slider-min::-moz-range-thumb {
  pointer-events: auto;
  z-index: 30;
}

.range-slider-min::-ms-thumb {
  pointer-events: auto;
  z-index: 30;
}

/* Max slider - only thumb is interactive */
.range-slider-max {
  pointer-events: none;
}

.range-slider-max::-webkit-slider-thumb {
  pointer-events: auto;
  z-index: 30;
}

.range-slider-max::-moz-range-thumb {
  pointer-events: auto;
  z-index: 30;
}

.range-slider-max::-ms-thumb {
  pointer-events: auto;
  z-index: 30;
}

.range-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #9333ea;
  cursor: pointer;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
  margin-top: -6px;
}

.range-slider::-webkit-slider-thumb:hover {
  background: #7e22ce;
  transform: scale(1.1);
}

.range-slider::-webkit-slider-runnable-track {
  height: 8px;
  background: transparent;
  width: 100%;
}

.range-slider::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #9333ea;
  cursor: pointer;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
  margin-top: -6px;
}

.range-slider::-moz-range-thumb:hover {
  background: #7e22ce;
  transform: scale(1.1);
}

.range-slider::-moz-range-track {
  height: 8px;
  background: transparent;
  width: 100%;
}

.range-slider::-ms-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #9333ea;
  cursor: pointer;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
  margin-top: -6px;
}

.range-slider::-ms-thumb:hover {
  background: #7e22ce;
  transform: scale(1.1);
}

.range-slider::-ms-track {
  height: 8px;
  background: transparent;
  width: 100%;
}

/* Dark mode support */
.dark .range-slider::-webkit-slider-thumb {
  border-color: #1f2937;
}

.dark .range-slider::-moz-range-thumb {
  border-color: #1f2937;
}

.dark .range-slider::-ms-thumb {
  border-color: #1f2937;
}
</style>

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
              >inventory_2</i
            >
            Quản lý sản phẩm
          </h1>
          <p
            class="text-sm text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1"
          >
            <i class="material-icons text-xs">info</i>
            Quản lý sản phẩm và các biến thể (variants) - Nâng cao
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button
            v-permission="PERMISSIONS_EXPOSED.PRODUCT_IMPORT"
            @click="openImportModal"
            class="flex items-center gap-2 px-4 py-2 bg-green-500 hover:bg-green-600 text-white rounded-lg transition-colors text-sm font-medium"
            aria-label="Import sản phẩm từ file Excel"
            title="Import sản phẩm từ file Excel"
          >
            <i class="material-icons text-base" aria-hidden="true"
              >file_upload</i
            >
            Import Excel
          </button>
          <button
            v-permission="PERMISSIONS_EXPOSED.PRODUCT_EXPORT"
            @click="exportToExcel"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
            aria-label="Xuất danh sách sản phẩm ra file Excel"
            title="Xuất danh sách sản phẩm ra file Excel"
          >
            <i class="material-icons text-base" aria-hidden="true">download</i>
            Export Excel
          </button>
          <button
            v-permission="PERMISSIONS_EXPOSED.PRODUCT_CREATE"
            @click="openCreateModal"
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
            aria-label="Thêm sản phẩm mới"
            title="Thêm sản phẩm mới"
          >
            <i class="material-icons text-base" aria-hidden="true">add</i>
            Thêm sản phẩm
          </button>
        </div>
      </div>
    </div>

    <!-- Statistics Cards -->
    <div
      v-if="stats"
      class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3"
    >
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">inventory</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.totalProducts }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng sản phẩm</p>
        </div>
      </div>
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">style</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.totalVariants }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng biến thể</p>
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
            {{ stats.lowStockCount }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Sắp hết hàng</p>
        </div>
      </div>
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg"
              >remove_shopping_cart</i
            >
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.outOfStockCount }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Hết hàng</p>
        </div>
      </div>
    </div>

    <!-- Advanced Filters -->
    <ProductFilters
      :filters="filters"
      :brands="brands"
      :categories="categories"
      @update:filters="filters = $event"
      @apply="applyFilters"
      @reset="resetFilters"
    />

    <!-- =================================================================
         LOADING & EMPTY STATES
         ================================================================= -->
    <!-- Loading State -->
    <div
      v-if="loading"
      class="space-y-4"
      role="status"
      aria-live="polite"
      aria-label="Đang tải danh sách sản phẩm"
    >
      <div
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4"
      >
        <LoadingSkeleton
          v-for="n in 8"
          :key="n"
          type="card"
          :show-image="true"
        />
      </div>
      <span class="sr-only">Đang tải danh sách sản phẩm</span>
    </div>

    <div
      v-else-if="products.length === 0"
      class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4"
      >
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl"
          >inventory_2</i
        >
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
        Chưa có sản phẩm nào
      </h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
        Nhấn "Thêm sản phẩm" hoặc "Import Excel" để tạo sản phẩm đầu tiên
      </p>
      <button
        @click="openCreateModal"
        class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
      >
        <i class="material-icons text-base">add</i>
        Thêm sản phẩm
      </button>
    </div>

    <!-- =================================================================
         BULK ACTION BAR & PRODUCTS TABLE
         ================================================================= -->
    <div v-else>
      <!-- Bulk Action Bar -->
      <div
        v-if="selectedProducts.length > 0"
        class="flex items-center justify-between p-4 bg-purple-50 dark:bg-purple-900/20 rounded-xl border border-purple-200 dark:border-purple-800 mb-4"
      >
        <div
          class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400"
            >check_circle</i
          >
          <span
            >Đã chọn
            <strong class="font-semibold">{{ selectedProducts.length }}</strong>
            sản phẩm</span
          >
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="bulkExport"
            class="flex items-center gap-2 px-3 py-1.5 bg-green-500 hover:bg-green-600 text-white rounded-lg transition-colors text-sm font-medium focus:outline-none focus:ring-2 focus:ring-green-500"
            :aria-label="`Xuất ${selectedProducts.length} sản phẩm đã chọn ra file Excel`"
            title="Xuất các sản phẩm đã chọn"
          >
            <i class="material-icons text-base" aria-hidden="true">download</i>
            Xuất Excel
          </button>
          <button
            @click="openBulkUpdateModal"
            class="flex items-center gap-2 px-3 py-1.5 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors text-sm font-medium focus:outline-none focus:ring-2 focus:ring-blue-500"
            :aria-label="`Cập nhật hàng loạt ${selectedProducts.length} sản phẩm đã chọn`"
            title="Cập nhật hàng loạt"
          >
            <i class="material-icons text-base" aria-hidden="true">edit</i>
            Cập nhật hàng loạt
          </button>
          <button
            @click="bulkDelete"
            class="flex items-center gap-2 px-3 py-1.5 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors text-sm font-medium focus:outline-none focus:ring-2 focus:ring-red-500"
            :aria-label="`Xóa ${selectedProducts.length} sản phẩm đã chọn`"
            title="Xóa các sản phẩm đã chọn"
          >
            <i class="material-icons text-base" aria-hidden="true">delete</i>
            Xóa {{ selectedProducts.length }} sản phẩm
          </button>
          <button
            @click="clearSelection"
            class="flex items-center gap-2 px-3 py-1.5 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium focus:outline-none focus:ring-2 focus:ring-gray-500"
            aria-label="Bỏ chọn tất cả sản phẩm"
            title="Bỏ chọn"
          >
            <i class="material-icons text-base" aria-hidden="true">clear</i>
            Bỏ chọn
          </button>
        </div>
      </div>

      <!-- Products Table -->
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden"
        role="region"
        aria-label="Bảng danh sách sản phẩm"
      >
        <div class="overflow-x-auto">
          <table class="w-full" role="table" aria-label="Danh sách sản phẩm">
            <thead
              class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600"
              role="rowgroup"
            >
              <tr role="row">
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-10"
                  scope="col"
                >
                  <input
                    type="checkbox"
                    :checked="isAllSelected"
                    @change="toggleSelectAll"
                    class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                    :aria-label="
                      isAllSelected
                        ? 'Bỏ chọn tất cả sản phẩm'
                        : 'Chọn tất cả sản phẩm'
                    "
                    :aria-checked="isAllSelected"
                    role="checkbox"
                  />
                </th>
                <!-- 🆕 Mã sản phẩm -->
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                  scope="col"
                >
                  <button
                    @click="sortColumn('code')"
                    class="flex items-center gap-1 w-full text-left hover:bg-gray-100 dark:hover:bg-gray-700/50 px-2 py-1 rounded transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
                    aria-label="Sắp xếp theo mã sản phẩm"
                    :aria-sort="
                      sortBy === 'code'
                        ? sortOrder === 'asc'
                          ? 'ascending'
                          : 'descending'
                        : 'none'
                    "
                  >
                    <span>Mã SP</span>
                    <i class="material-icons text-sm" aria-hidden="true">{{
                      getSortIcon("code")
                    }}</i>
                  </button>
                </th>

                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                  scope="col"
                >
                  <button
                    @click="sortColumn('name')"
                    class="flex items-center gap-1 w-full text-left hover:bg-gray-100 dark:hover:bg-gray-700/50 px-2 py-1 rounded transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
                    aria-label="Sắp xếp theo tên sản phẩm"
                    :aria-sort="
                      sortBy === 'name'
                        ? sortOrder === 'asc'
                          ? 'ascending'
                          : 'descending'
                        : 'none'
                    "
                  >
                    <span>Tên sản phẩm</span>
                    <i class="material-icons text-sm" aria-hidden="true">{{
                      getSortIcon("name")
                    }}</i>
                  </button>
                </th>

                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                  scope="col"
                >
                  <button
                    @click="sortColumn('brandName')"
                    class="flex items-center gap-1 w-full text-left hover:bg-gray-100 dark:hover:bg-gray-700/50 px-2 py-1 rounded transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
                    aria-label="Sắp xếp theo thương hiệu"
                    :aria-sort="
                      sortBy === 'brandName'
                        ? sortOrder === 'asc'
                          ? 'ascending'
                          : 'descending'
                        : 'none'
                    "
                  >
                    <span>Brands</span>
                    <i class="material-icons text-sm" aria-hidden="true">{{
                      getSortIcon("brandName")
                    }}</i>
                  </button>
                </th>

                <!-- 🆕 Danh mục -->
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                  scope="col"
                >
                  <button
                    @click="sortColumn('categoryNames')"
                    class="flex items-center gap-1 w-full text-left hover:bg-gray-100 dark:hover:bg-gray-700/50 px-2 py-1 rounded transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
                    aria-label="Sắp xếp theo danh mục"
                    :aria-sort="
                      sortBy === 'categoryNames'
                        ? sortOrder === 'asc'
                          ? 'ascending'
                          : 'descending'
                        : 'none'
                    "
                  >
                    <span>Danh mục</span>
                    <i class="material-icons text-sm" aria-hidden="true">{{
                      getSortIcon("categoryNames")
                    }}</i>
                  </button>
                </th>

                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                  scope="col"
                >
                  <button
                    @click="sortColumn('variantCount')"
                    class="flex items-center gap-1 w-full text-left hover:bg-gray-100 dark:hover:bg-gray-700/50 px-2 py-1 rounded transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
                    aria-label="Sắp xếp theo số lượng biến thể"
                    :aria-sort="
                      sortBy === 'variantCount'
                        ? sortOrder === 'asc'
                          ? 'ascending'
                          : 'descending'
                        : 'none'
                    "
                  >
                    <span>Số SPCT</span>
                    <i class="material-icons text-sm" aria-hidden="true">{{
                      getSortIcon("variantCount")
                    }}</i>
                  </button>
                </th>
                <!-- 🆕 Khoảng giá -->
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700/50"
                  @click="sortColumn('priceFrom')"
                >
                  <div class="flex items-center gap-1">
                    <span>Khoảng giá</span>
                    <i class="material-icons text-sm">{{
                      getSortIcon("priceFrom")
                    }}</i>
                  </div>
                </th>
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                  scope="col"
                >
                  <button
                    @click="sortColumn('stockQuantity')"
                    class="flex items-center gap-1 w-full text-left hover:bg-gray-100 dark:hover:bg-gray-700/50 px-2 py-1 rounded transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
                    aria-label="Sắp xếp theo tồn kho"
                    :aria-sort="
                      sortBy === 'stockQuantity'
                        ? sortOrder === 'asc'
                          ? 'ascending'
                          : 'descending'
                        : 'none'
                    "
                  >
                    <span>Kho</span>
                    <i class="material-icons text-sm" aria-hidden="true">{{
                      getSortIcon("stockQuantity")
                    }}</i>
                  </button>
                </th>
                <th
                  class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                  scope="col"
                >
                  <button
                    @click="sortColumn('isActive')"
                    class="flex items-center gap-1 w-full text-left hover:bg-gray-100 dark:hover:bg-gray-700/50 px-2 py-1 rounded transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
                    aria-label="Sắp xếp theo trạng thái"
                    :aria-sort="
                      sortBy === 'isActive'
                        ? sortOrder === 'asc'
                          ? 'ascending'
                          : 'descending'
                        : 'none'
                    "
                  >
                    <span>Trạng thái</span>
                    <i class="material-icons text-sm" aria-hidden="true">{{
                      getSortIcon("isActive")
                    }}</i>
                  </button>
                </th>
                <th
                  class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
                  scope="col"
                >
                  Thao tác
                </th>
              </tr>
            </thead>

            <tbody
              class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700"
              role="rowgroup"
            >
              <tr
                v-for="product in products"
                :key="product.id"
                :data-id="product.id"
                class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors"
                role="row"
              >
                <td class="px-4 py-4" role="cell">
                  <input
                    type="checkbox"
                    :checked="selectedProducts.includes(product.id)"
                    @change="toggleSelect(product.id)"
                    class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                    :aria-label="`Chọn sản phẩm ${product.name}`"
                    :aria-checked="selectedProducts.includes(product.id)"
                    role="checkbox"
                  />
                </td>

                <!-- 🆕 Cột mã sản phẩm -->
                <td class="px-4 py-4">
                  <code
                    v-if="product.code"
                    class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100"
                    >{{ product.code }}</code
                  >
                  <span
                    v-else
                    class="text-xs text-gray-400 dark:text-gray-500 italic"
                    >Chưa có mã</span
                  >
                </td>

                <td class="px-4 py-4">
                  <div
                    class="text-sm font-medium text-gray-900 dark:text-gray-100"
                  >
                    {{ product.name }}
                  </div>
                  <div class="text-xs text-gray-500 dark:text-gray-400">
                    {{ product.slug }}
                  </div>
                </td>

                <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">
                  {{ product.brandName || "N/A" }}
                </td>

                <!-- 🆕 Cột danh mục -->
                <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">
                  <span
                    v-if="product.categories && product.categories.length > 0"
                  >
                    {{ product.categories.map((c) => c.name).join(", ") }}
                  </span>
                  <span v-else class="text-gray-400 dark:text-gray-500">—</span>
                </td>

                <td class="px-4 py-4">
                  <span
                    class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400"
                  >
                    {{ product.variantCount || 0 }} variants
                  </span>
                </td>

                <!-- 🆕 Cột khoảng giá -->
                <td class="px-4 py-4">
                  <div
                    v-if="
                      (product.priceFrom !== null &&
                        product.priceFrom !== undefined) ||
                      (product.priceTo !== null &&
                        product.priceTo !== undefined)
                    "
                    class="text-sm text-gray-900 dark:text-gray-100"
                  >
                    <div
                      v-if="
                        product.priceFrom !== null &&
                        product.priceFrom !== undefined &&
                        product.priceTo !== null &&
                        product.priceTo !== undefined
                      "
                      class="flex items-center gap-1"
                    >
                      <span
                        class="font-medium text-purple-600 dark:text-purple-400"
                        >{{ formatPriceWithoutUnit(product.priceFrom) }}</span
                      >
                      <i class="material-icons text-xs text-gray-400"
                        >arrow_forward</i
                      >
                      <span
                        class="font-medium text-purple-600 dark:text-purple-400"
                        >{{ formatPriceWithoutUnit(product.priceTo) }}</span
                      >
                    </div>
                    <div
                      v-else-if="
                        product.priceFrom !== null &&
                        product.priceFrom !== undefined
                      "
                      class="text-gray-600 dark:text-gray-400"
                    >
                      <span class="flex items-center gap-1">
                        <span>Từ</span>
                        <span
                          class="font-medium text-purple-600 dark:text-purple-400"
                          >{{ formatPriceWithoutUnit(product.priceFrom) }}</span
                        >
                      </span>
                    </div>
                    <div
                      v-else-if="
                        product.priceTo !== null &&
                        product.priceTo !== undefined
                      "
                      class="text-gray-600 dark:text-gray-400"
                    >
                      <span class="flex items-center gap-1">
                        <span>Đến</span>
                        <span
                          class="font-medium text-purple-600 dark:text-purple-400"
                          >{{ formatPriceWithoutUnit(product.priceTo) }}</span
                        >
                      </span>
                    </div>
                  </div>
                  <span
                    v-else
                    class="text-xs text-gray-400 dark:text-gray-500 italic"
                    >—</span
                  >
                </td>

                <td class="px-4 py-4">
                  <span
                    class="inline-flex items-center justify-center px-2 py-1 text-xs font-semibold rounded-full min-w-[40px]"
                    :class="{
                      'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400':
                        getTotalStock(product) > 0,
                      'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400':
                        getTotalStock(product) === 0,
                    }"
                  >
                    {{ getTotalStock(product) }}
                  </span>
                </td>

                <td class="px-4 py-4">
                  <span
                    class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                    :class="
                      product.isActive
                        ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
                        : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
                    "
                  >
                    {{ product.isActive ? "Đang bán" : "Ngừng bán" }}
                  </span>
                </td>

                <td class="px-4 py-4 text-center" role="cell">
                  <div
                    class="flex items-center justify-center gap-2"
                    role="group"
                    aria-label="Thao tác với sản phẩm"
                  >
                    <!-- <button
                      @click="duplicateProduct(product.id)"
                      class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500"
                      :aria-label="`Nhân bản sản phẩm ${product.name}`"
                      title="Nhân bản"
                    >
                      <i class="material-icons text-base" aria-hidden="true"
                        >content_copy</i
                      >
                    </button> -->
                    <button
                      v-permission="PERMISSIONS_EXPOSED.PRODUCT_UPDATE"
                      @click="openEditModal(product)"
                      class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500"
                      :aria-label="`Chỉnh sửa sản phẩm ${product.name}`"
                      title="Chỉnh sửa"
                    >
                      <i class="material-icons text-base" aria-hidden="true"
                        >edit</i
                      >
                    </button>
                    <button
                      v-permission="PERMISSIONS_EXPOSED.PRODUCT_DELETE"
                      @click="confirmDelete(product)"
                      class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-red-500"
                      :aria-label="`Xóa sản phẩm ${product.name}`"
                      title="Xóa"
                    >
                      <i class="material-icons text-base" aria-hidden="true"
                        >delete</i
                      >
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Pagination -->
      <div
        class="flex items-center justify-between gap-4 px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
      >
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Hiển thị {{ currentPage * pageSize + 1 }} -
          {{ Math.min((currentPage + 1) * pageSize, totalItems) }} trong tổng số
          {{ totalItems }} sản phẩm
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-purple-500"
            :disabled="currentPage === 0"
            @click="changePage(currentPage - 1)"
            aria-label="Trang trước"
            :aria-disabled="currentPage === 0"
          >
            <i class="material-icons text-base" aria-hidden="true"
              >chevron_left</i
            >
            Trước
          </button>
          <span
            class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300"
            aria-live="polite"
            aria-atomic="true"
          >
            Trang {{ currentPage + 1 }} / {{ totalPages }}
          </span>
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-purple-500"
            :disabled="currentPage >= totalPages - 1"
            @click="changePage(currentPage + 1)"
            aria-label="Trang sau"
            :aria-disabled="currentPage >= totalPages - 1"
          >
            Sau
            <i class="material-icons text-base" aria-hidden="true"
              >chevron_right</i
            >
          </button>
        </div>
      </div>
    </div>

    <!-- =================================================================
         MODALS
         ================================================================= -->

    <!-- Create/Edit Modal -->
    <ProductFormModal
      v-model:visible="showModal"
      v-model:formErrors="formErrors"
      v-model:formData="formData"
      :isEditMode="isEditMode"
      :initialProduct="isEditMode ? editingProduct : null"
      :brands="brands"
      :categories="categories"
      :materials="materials"
      :soles="soles"
      :initialImages="initialProductImages"
      :maxImages="MAX_IMAGES_PER_PRODUCT"
      :formErrors="formErrors"
      :submitting="isSubmitting"
      @update:visible="showModal = $event"
      @update:formData="formData = $event"
      @submit="handleSubmit"
      @close="closeModal"
      @quick-add-brand="openQuickAddBrand"
      @quick-add-material="showQuickAddMaterial = true"
      @quick-add-sole="showQuickAddSole = true"
      @quick-add-category="openCreateCategoryModal"
      @images-change="onProductImagesChange"
      @image-remove="onProductImageRemove"
    />

    <!-- =============================
     🎨 TẤT CẢ POPUP THÊM NHANH
     ============================= -->
    <Teleport to="body">
      <!-- Modal: Thêm Thương hiệu -->
      <div
        v-if="showQuickAddBrand"
        class="modal-backdrop"
        @click="closeQuickAddBrand"
      >
        <div class="modal-container" @click.stop>
          <div class="modal-header">
            <h2 class="modal-title">
              <i class="material-icons text-purple-600 dark:text-purple-400"
                >add</i
              >
              Thêm Thương hiệu mới
            </h2>
            <button @click="closeQuickAddBrand" class="modal-close-btn">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="modal-body">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="form-label">Tên thương hiệu *</label>
                <input
                  v-model="quickBrandData.name"
                  @input="generateBrandSlug"
                  type="text"
                  class="form-control"
                  placeholder="VD: Nike, Adidas..."
                />
              </div>
              <div>
                <label class="form-label">Slug *</label>
                <input
                  v-model="quickBrandData.slug"
                  type="text"
                  class="form-control"
                  placeholder="VD: nike, adidas..."
                />
              </div>
            </div>

            <div>
              <label class="form-label">URL Logo</label>
              <input
                v-model="quickBrandData.logoUrl"
                type="text"
                class="form-control"
                placeholder="/placeholder-image.png"
              />
            </div>

            <div>
              <label class="form-label">Website</label>
              <input
                v-model="quickBrandData.websiteUrl"
                type="url"
                class="form-control"
                placeholder="https://example.com"
              />
            </div>

            <div>
              <label class="form-label">Mô tả</label>
              <textarea
                v-model="quickBrandData.description"
                class="form-control"
                rows="3"
                placeholder="Nhập mô tả về thương hiệu..."
              ></textarea>
            </div>

            <label class="inline-flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                v-model="quickBrandData.isActive"
                class="accent-purple-500"
              />
              <span class="text-sm text-gray-700 dark:text-gray-300"
                >Kích hoạt thương hiệu</span
              >
            </label>
          </div>

          <div class="modal-footer">
            <button @click="closeQuickAddBrand" class="btn-secondary">
              Hủy
            </button>
            <button
              @click="saveQuickBrand"
              :disabled="savingQuickBrand"
              class="btn-primary"
            >
              <i class="material-icons text-base" v-if="!savingQuickBrand"
                >save</i
              >
              {{ savingQuickBrand ? "Đang lưu..." : "Lưu" }}
            </button>
          </div>
        </div>
      </div>

      <!-- Modal: Thêm Chất liệu -->
      <div
        v-if="showQuickAddMaterial"
        class="modal-backdrop"
        @click="closeQuickAddMaterial"
      >
        <div class="modal-container" @click.stop>
          <div class="modal-header">
            <h2 class="modal-title">
              <i class="material-icons text-purple-600 dark:text-purple-400"
                >add</i
              >
              Thêm Chất liệu mới
            </h2>
            <button @click="closeQuickAddMaterial" class="modal-close-btn">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="modal-body">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="form-label">Tên chất liệu *</label>
                <input
                  v-model="quickMaterialData.name"
                  @input="generateMaterialSlug"
                  type="text"
                  class="form-control"
                  placeholder="VD: Da tổng hợp, Vải canvas..."
                />
              </div>
              <div>
                <label class="form-label">Slug *</label>
                <input
                  v-model="quickMaterialData.slug"
                  type="text"
                  class="form-control"
                  placeholder="VD: da-tong-hop, vai-canvas..."
                />
              </div>
            </div>

            <div>
              <label class="form-label">Mô tả</label>
              <textarea
                v-model="quickMaterialData.description"
                class="form-control"
                rows="3"
                placeholder="Nhập mô tả về chất liệu..."
              ></textarea>
            </div>

            <label class="inline-flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                v-model="quickMaterialData.isActive"
                class="accent-purple-500"
              />
              <span class="text-sm text-gray-700 dark:text-gray-300"
                >Kích hoạt chất liệu</span
              >
            </label>
          </div>

          <div class="modal-footer">
            <button @click="closeQuickAddMaterial" class="btn-secondary">
              Hủy
            </button>
            <button
              @click="saveQuickMaterial"
              :disabled="savingQuickMaterial"
              class="btn-primary"
            >
              <i class="material-icons text-base" v-if="!savingQuickMaterial"
                >save</i
              >
              {{ savingQuickMaterial ? "Đang lưu..." : "Lưu" }}
            </button>
          </div>
        </div>
      </div>

      <!-- Modal: Thêm Loại đế giày -->
      <div
        v-if="showQuickAddSole"
        class="modal-backdrop"
        @click="closeQuickAddSole"
      >
        <div class="modal-container" @click.stop>
          <div class="modal-header">
            <h2 class="modal-title">
              <i class="material-icons text-purple-600 dark:text-purple-400"
                >add</i
              >
              Thêm Loại đế giày mới
            </h2>
            <button @click="closeQuickAddSole" class="modal-close-btn">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="modal-body">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="form-label">Tên loại đế *</label>
                <input
                  v-model="quickSoleData.name"
                  @input="generateSoleSlug"
                  type="text"
                  class="form-control"
                  placeholder="VD: Cao su, EVA..."
                />
              </div>
              <div>
                <label class="form-label">Slug *</label>
                <input
                  v-model="quickSoleData.slug"
                  type="text"
                  class="form-control"
                  placeholder="VD: cao-su, eva..."
                />
              </div>
            </div>

            <div>
              <label class="form-label">Mô tả</label>
              <textarea
                v-model="quickSoleData.description"
                class="form-control"
                rows="3"
                placeholder="Nhập mô tả về loại đế giày..."
              ></textarea>
            </div>

            <label class="inline-flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                v-model="quickSoleData.isActive"
                class="accent-purple-500"
              />
              <span class="text-sm text-gray-700 dark:text-gray-300"
                >Kích hoạt loại đế giày</span
              >
            </label>
          </div>

          <div class="modal-footer">
            <button @click="closeQuickAddSole" class="btn-secondary">
              Hủy
            </button>
            <button
              @click="saveQuickSole"
              :disabled="savingQuickSole"
              class="btn-primary"
            >
              <i class="material-icons text-base" v-if="!savingQuickSole"
                >save</i
              >
              {{ savingQuickSole ? "Đang lưu..." : "Lưu" }}
            </button>
          </div>
        </div>
      </div>

      <!-- Modal: Thêm Danh mục -->
      <div
        v-if="showCategoryModal"
        class="modal-backdrop"
        @click="closeCategoryModal"
      >
        <div class="modal-container" @click.stop>
          <div class="modal-header">
            <h2 class="modal-title">
              <i class="material-icons text-purple-600 dark:text-purple-400"
                >add</i
              >
              Thêm danh mục mới
            </h2>
            <button @click="closeCategoryModal" class="modal-close-btn">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="modal-body">
            <div>
              <label class="form-label">Tên danh mục *</label>
              <input
                v-model="newCategory.name"
                type="text"
                class="form-control"
                placeholder="Giày chạy bộ, Giày bóng rổ..."
                @input="generateCategorySlug"
              />
              <span v-if="categoryErrors.name" class="form-error">{{
                categoryErrors.name
              }}</span>
            </div>

            <div>
              <label class="form-label">Slug *</label>
              <input
                v-model="newCategory.slug"
                type="text"
                class="form-control"
                placeholder="giay-chay-bo, giay-bong-ro..."
              />
              <span v-if="categoryErrors.slug" class="form-error">{{
                categoryErrors.slug
              }}</span>
              <p class="text-xs text-gray-500 mt-1">
                URL thân thiện (tự động tạo từ tên)
              </p>
            </div>

            <div>
              <label class="form-label">Danh mục cha</label>
              <select v-model="newCategory.parentId" class="form-control">
                <option :value="null">-- Không có (danh mục gốc) --</option>
                <option
                  v-for="cat in rootCategories"
                  :key="cat.id"
                  :value="cat.id"
                >
                  {{ cat.name }}
                </option>
              </select>
              <p class="text-xs text-gray-500 mt-1">
                Để trống nếu đây là danh mục gốc
              </p>
            </div>
          </div>

          <div class="modal-footer">
            <button @click="closeCategoryModal" class="btn-secondary">
              Hủy
            </button>
            <button
              @click="handleCreateCategory"
              :disabled="submittingCategory"
              class="btn-primary"
            >
              {{ submittingCategory ? "Đang lưu..." : "Thêm mới" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Import Excel Modal -->
    <Teleport to="body">
      <div
        v-if="showImportModal"
        class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
        @click="closeImportModal"
      >
        <div
          class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700"
          @click.stop
        >
          <div
            class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10"
          >
            <h2
              class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
            >
              <i class="material-icons text-purple-600 dark:text-purple-400"
                >file_upload</i
              >
              Import sản phẩm từ Excel
            </h2>
            <button
              @click="closeImportModal"
              class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
            >
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="p-6 space-y-6">
            <!-- Instructions Section -->
            <div
              class="bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 rounded-lg p-4 border border-blue-200 dark:border-blue-800"
            >
              <h3
                class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-3 flex items-center gap-2"
              >
                <i
                  class="material-icons text-blue-600 dark:text-blue-400 text-lg"
                  >info</i
                >
                Hướng dẫn Import
              </h3>
              <ol
                class="space-y-2 text-sm text-gray-700 dark:text-gray-300 ml-6 list-decimal"
              >
                <li>Tải file mẫu Excel (nếu chưa có)</li>
                <li>Điền thông tin sản phẩm theo đúng format</li>
                <li>Upload file và xem preview</li>
                <li>Nhấn "Import" để thêm sản phẩm</li>
              </ol>
              <button
                @click="downloadTemplate"
                class="mt-4 flex items-center gap-2 px-4 py-2 bg-white dark:bg-gray-700 border border-blue-300 dark:border-blue-600 text-blue-700 dark:text-blue-300 rounded-lg hover:bg-blue-50 dark:hover:bg-blue-900/30 transition-colors text-sm font-medium"
              >
                <i class="material-icons text-base">download</i>
                Tải file mẫu Excel
              </button>
            </div>

            <!-- File Upload Section -->
            <div class="space-y-2">
              <label
                class="block text-sm font-medium text-gray-700 dark:text-gray-300"
              >
                Chọn file Excel:
              </label>
              <div class="relative">
                <input
                  type="file"
                  accept=".xlsx,.xls,.csv"
                  @change="handleFileUpload"
                  class="block w-full text-sm text-gray-500 dark:text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-purple-50 dark:file:bg-purple-900/30 file:text-purple-700 dark:file:text-purple-300 hover:file:bg-purple-100 dark:hover:file:bg-purple-900/50 file:cursor-pointer border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                />
                <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
                  Hỗ trợ định dạng: Excel (.xlsx, .xls) và CSV (.csv)
                </p>
              </div>
            </div>

            <!-- Validation Summary -->
            <div
              v-if="importValidation && importValidation.summary"
              class="space-y-2"
            >
              <div
                class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg border border-gray-200 dark:border-gray-700"
              >
                <div class="flex items-center gap-4">
                  <div class="text-center">
                    <div
                      class="text-2xl font-bold text-gray-900 dark:text-gray-100"
                    >
                      {{ importValidation.summary.total }}
                    </div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">
                      Tổng số
                    </div>
                  </div>
                  <div class="text-center">
                    <div
                      class="text-2xl font-bold text-green-600 dark:text-green-400"
                    >
                      {{ importValidation.summary.valid }}
                    </div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">
                      Hợp lệ
                    </div>
                  </div>
                  <div class="text-center">
                    <div
                      class="text-2xl font-bold text-red-600 dark:text-red-400"
                    >
                      {{ importValidation.summary.invalid }}
                    </div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">
                      Lỗi
                    </div>
                  </div>
                  <div class="text-center">
                    <div
                      class="text-2xl font-bold text-purple-600 dark:text-purple-400"
                    >
                      {{ importValidation.summary.validPercentage }}%
                    </div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">
                      Tỷ lệ
                    </div>
                  </div>
                </div>
                <div
                  v-if="importValidation.summary.invalid > 0"
                  class="flex items-center gap-2 text-sm text-red-600 dark:text-red-400"
                >
                  <i class="material-icons text-base">warning</i>
                  <span
                    >Có {{ importValidation.summary.invalid }} dòng bị lỗi</span
                  >
                </div>
              </div>
            </div>

            <!-- Preview Section -->
            <div v-if="importPreview.length > 0" class="space-y-3">
              <h3
                class="text-base font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2"
              >
                <i
                  class="material-icons text-green-600 dark:text-green-400 text-lg"
                  >preview</i
                >
                Preview: {{ importPreview.length }} sản phẩm
              </h3>
              <div
                class="border border-gray-200 dark:border-gray-700 rounded-lg overflow-hidden"
              >
                <div class="overflow-x-auto max-h-[400px]">
                  <table
                    class="min-w-full divide-y divide-gray-200 dark:divide-gray-700"
                  >
                    <thead class="bg-gray-50 dark:bg-gray-900 sticky top-0">
                      <tr>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          Dòng
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          Tên SP
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          Brand
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          SKU
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          Size
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          Màu
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          Giá
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          Tồn kho
                        </th>
                        <th
                          class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                        >
                          Lỗi
                        </th>
                      </tr>
                    </thead>
                    <tbody
                      class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700"
                    >
                      <tr
                        v-for="(item, index) in importPreview"
                        :key="index"
                        :class="[
                          'hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors',
                          item.errors && item.errors.length > 0
                            ? 'bg-red-50 dark:bg-red-900/10 border-l-4 border-red-500'
                            : '',
                        ]"
                      >
                        <td
                          class="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-700 dark:text-gray-300"
                        >
                          {{ item.rowNumber || index + 2 }}
                        </td>
                        <td
                          class="px-4 py-3 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100"
                        >
                          {{ item.productName }}
                        </td>
                        <td
                          class="px-4 py-3 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300"
                        >
                          {{ item.brandName }}
                        </td>
                        <td
                          class="px-4 py-3 whitespace-nowrap text-sm font-mono text-gray-700 dark:text-gray-300"
                        >
                          {{ item.sku }}
                        </td>
                        <td
                          class="px-4 py-3 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300"
                        >
                          {{ item.size }}
                        </td>
                        <td
                          class="px-4 py-3 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300"
                        >
                          {{ item.color }}
                        </td>
                        <td
                          class="px-4 py-3 whitespace-nowrap text-sm font-semibold text-gray-900 dark:text-gray-100"
                        >
                          {{ formatCurrency(item.priceBase) }}
                        </td>
                        <td
                          class="px-4 py-3 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300"
                        >
                          {{ item.stockQuantity }}
                        </td>
                        <td class="px-4 py-3 text-sm">
                          <div
                            v-if="item.errors && item.errors.length > 0"
                            class="space-y-1"
                          >
                            <div
                              v-for="(error, errorIndex) in item.errors"
                              :key="errorIndex"
                              class="text-xs text-red-600 dark:text-red-400 flex items-start gap-1"
                            >
                              <i class="material-icons text-xs mt-0.5">error</i>
                              <span>{{ error }}</span>
                            </div>
                          </div>
                          <span
                            v-else
                            class="text-xs text-green-600 dark:text-green-400 flex items-center gap-1"
                          >
                            <i class="material-icons text-xs">check_circle</i>
                            Hợp lệ
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>

          <div
            class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800"
          >
            <button
              @click="closeImportModal"
              class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium"
            >
              Hủy
            </button>
            <button
              @click="handleImport"
              class="px-4 py-2 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="
                importing ||
                importPreview.length === 0 ||
                (importValidation && importValidation.summary.valid === 0)
              "
            >
              {{
                importing
                  ? "Đang import..."
                  : `Import ${importPreview.length} sản phẩm`
              }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Bulk Update Modal -->
    <Teleport to="body">
      <div
        v-if="showBulkUpdateModal"
        class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
        @click="closeBulkUpdateModal"
      >
        <div
          class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700"
          @click.stop
        >
          <div
            class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10"
          >
            <h2
              class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
            >
              <i class="material-icons text-purple-600 dark:text-purple-400"
                >edit</i
              >
              Cập nhật hàng loạt {{ selectedProducts.length }} sản phẩm
            </h2>
            <button
              @click="closeBulkUpdateModal"
              class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
            >
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="p-6">
            <div class="form-group">
              <label class="form-label required">Chọn hành động:</label>
              <select v-model="bulkUpdateAction" class="form-control">
                <option value="">-- Chọn hành động --</option>
                <option value="UPDATE_STATUS">Cập nhật trạng thái</option>
                <option value="UPDATE_BRAND">Đổi thương hiệu</option>
                <option value="ADD_CATEGORY">Thêm danh mục</option>
                <option value="REMOVE_CATEGORY">Xóa danh mục</option>
              </select>
            </div>

            <div v-if="bulkUpdateAction === 'UPDATE_STATUS'" class="form-group">
              <label class="form-label">Trạng thái mới:</label>
              <select v-model="bulkUpdateValue.isActive" class="form-control">
                <option :value="true">Đang bán</option>
                <option :value="false">Ngừng bán</option>
              </select>
            </div>

            <div v-if="bulkUpdateAction === 'UPDATE_BRAND'" class="form-group">
              <label class="form-label">Thương hiệu mới:</label>
              <select v-model="bulkUpdateValue.brandId" class="form-control">
                <option value="">Chọn thương hiệu</option>
                <option
                  v-for="brand in brands"
                  :key="brand.id"
                  :value="brand.id"
                >
                  {{ brand.name }}
                </option>
              </select>
            </div>

            <div
              v-if="
                bulkUpdateAction === 'ADD_CATEGORY' ||
                bulkUpdateAction === 'REMOVE_CATEGORY'
              "
              class="form-group"
            >
              <label class="form-label">Danh mục:</label>
              <select v-model="bulkUpdateValue.categoryId" class="form-control">
                <option value="">Chọn danh mục</option>
                <option
                  v-for="category in categories"
                  :key="category.id"
                  :value="category.id"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>
          </div>

          <div
            class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800"
          >
            <button
              @click="closeBulkUpdateModal"
              class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium"
            >
              Hủy
            </button>
            <button
              @click="handleBulkUpdate"
              class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="bulkUpdating || !bulkUpdateAction"
            >
              {{ bulkUpdating ? "Đang cập nhật..." : "Cập nhật" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Delete Confirmation Dialog -->
    <ConfirmDialog
      v-model="showDeleteModal"
      type="danger"
      title="Xác nhận xóa sản phẩm"
      :message="`Bạn có chắc chắn muốn xóa sản phẩm '${productToDelete?.name}'?`"
      description="Hành động này không thể hoàn tác!"
      confirm-text="Xóa sản phẩm"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="handleDelete"
    />

    <!-- Bulk Delete Confirmation Dialog -->
    <ConfirmDialog
      v-model="showBulkDeleteConfirm"
      type="danger"
      title="Xác nhận xóa hàng loạt"
      :message="`Bạn có chắc chắn muốn xóa ${selectedProducts.length} sản phẩm đã chọn?`"
      description="Hành động này không thể hoàn tác! Tất cả sản phẩm đã chọn sẽ bị xóa vĩnh viễn."
      confirm-text="Xóa tất cả"
      cancel-text="Hủy"
      :loading="false"
      @confirm="bulkDeleteConfirmed"
    />

    <!-- 🆕 Action Loading Overlay - Không block toàn bộ UI -->
    <Teleport to="body">
      <div
        v-if="actionLoading"
        class="fixed top-4 right-4 z-[9999] bg-white dark:bg-gray-800 rounded-lg shadow-lg border border-purple-200 dark:border-purple-700 px-4 py-3 flex items-center gap-3"
      >
        <div
          class="w-5 h-5 border-3 border-purple-500 border-t-transparent rounded-full animate-spin"
        ></div>
        <span class="text-sm font-medium text-gray-700 dark:text-gray-300">
          Đang xử lý...
        </span>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { nextTick } from "vue";
import axios from "axios";
import { useAdminStore } from "@/stores/admin";
import notificationService from "@/utils/notificationService";
// import notificationService from "@/utils/notificationService";
import logger from "@/utils/logger";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";
// import UploadGallery from "@/assets/components/admin/UploadGallery.vue";
import ProductFilters from "@/assets/components/admin/ProductFilters.vue";
import ProductFormModal from "@/assets/components/admin/ProductFormModal.vue";
import * as XLSX from "xlsx";
import { MAX_IMAGES_PER_PRODUCT } from "@/utils/productConstants";
import { generateSlug as generateSlugUtil } from "@/utils/slugGenerator";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import EmptyState from "@/components/admin/EmptyState.vue";
import { useErrorHandler } from "@/composables/useErrorHandler";
import { useActivityLogger } from "@/composables/useActivityLogger";
import { usePermissions, PERMISSIONS } from "@/composables/usePermissions";
import { formatPrice, formatCurrency } from "@/utils/formatters";

const adminStore = useAdminStore();

// Error handling
const { handleError, clearError } = useErrorHandler();

// Activity logging
const {
  logProductCreate,
  logProductUpdate,
  logProductDelete,
  logBulkProductOperation,
  logExport,
  logImport,
} = useActivityLogger();

// Permissions - expose PERMISSIONS for template
const PERMISSIONS_EXPOSED = PERMISSIONS;

// State
const products = ref([]);
const brands = ref([]);
const categories = ref([]);
const materials = ref([]); // Danh sách chất liệu
const soles = ref([]); // Danh sách loại đế giày
const stats = ref(null);
const loading = ref(false);
const actionLoading = ref(false); // 🆕 Loading riêng cho duplicate/delete actions
const loadingProductDetail = ref(false); // 🆕 Loading riêng cho việc load chi tiết product khi edit
const currentPage = ref(0);
const sortBy = ref("id"); // Default sort column
const sortOrder = ref("desc"); // 'asc' or 'desc'
const pageSize = ref(10);
const totalItems = ref(0);
const showModal = ref(false);
const showDeleteModal = ref(false);
const showImportModal = ref(false);
const showBulkUpdateModal = ref(false);
const isEditMode = ref(false);
const submitting = ref(false);
const deleting = ref(false);
const importing = ref(false);
const bulkUpdating = ref(false);
const productToDelete = ref(null);
const isSubmitting = ref(false);
const removedImageUrls = ref([]);
const highlightedProductId = ref(null);

// 🧩 Ảnh sản phẩm
const productImages = ref([]); // danh sách ảnh hiện tại
const initialProductImages = ref([]); // để truyền vào UploadGallery
const uploadedImages = ref([]); // danh sách ảnh mới upload (local blob)
const originalImagesSnapshot = ref([]); // 🆕 lưu trạng thái ảnh DB ban đầu

// ================== STATE ==================
const showCategoryModal = ref(false);
const submittingCategory = ref(false);

const newCategory = ref({
  name: "",
  slug: "",
  parentId: null,
});

const categoryErrors = ref({});
const rootCategories = ref([]);

// ================== METHODS ==================
// setTimeout(() => {
//   nextTick(() => {
//     const el = document.querySelector(
//       `[data-id="${highlightedProductId.value}"]`
//     );
//     if (el) el.scrollIntoView({ behavior: "smooth", block: "center" });
//   });
// }, 300);

// 🟦 Mở popup
const openCreateCategoryModal = async () => {
  await loadRootCategories();
  resetCategoryForm();
  showCategoryModal.value = true;
};

// 🟩 Đóng popup
const closeCategoryModal = () => {
  showCategoryModal.value = false;
  categoryErrors.value = {};
};

// 🟨 Reset form
const resetCategoryForm = () => {
  newCategory.value = {
    name: "",
    slug: "",
    parentId: null,
  };
  categoryErrors.value = {};
};

// 🟧 Tự tạo slug từ tên
const generateCategorySlug = () => {
  newCategory.value.slug = generateSlugUtil(newCategory.value.name);
};

// 🟫 Validate dữ liệu
const validateCategoryForm = () => {
  categoryErrors.value = {};

  if (!newCategory.value.name || newCategory.value.name.trim() === "") {
    categoryErrors.value.name = "Tên danh mục không được để trống";
  } else if (newCategory.value.name.length < 2) {
    categoryErrors.value.name = "Tên danh mục phải có ít nhất 2 ký tự";
  }

  if (!newCategory.value.slug || newCategory.value.slug.trim() === "") {
    categoryErrors.value.slug = "Slug không được để trống";
  } else if (!/^[a-z0-9-]+$/.test(newCategory.value.slug)) {
    categoryErrors.value.slug =
      "Slug chỉ được chứa chữ thường, số và dấu gạch ngang";
  }

  return Object.keys(categoryErrors.value).length === 0;
};

// 🟪 Gửi request tạo mới
const handleCreateCategory = async () => {
  if (!validateCategoryForm()) return;

  try {
    submittingCategory.value = true;
    await adminStore.createCategory(newCategory.value);

    notificationService.success(
      "Thành công",
      `Đã thêm danh mục "${newCategory.value.name}" thành công!`
    );

    await fetchCategories();
    // Gán tự động danh mục vừa thêm
    const newCat = adminStore.categories.find(
      (c) => c.slug === newCategory.value.slug
    );
    if (newCat) {
      formData.value.categoryIds.push(newCat.id);
    }
    await loadRootCategories(); // reload lại danh mục cha
    closeCategoryModal();
  } catch (error) {
    logger.error("Lỗi khi thêm danh mục:", error);
    notificationService.apiError(error, "Không thể thêm danh mục");
  } finally {
    submittingCategory.value = false;
  }
};

// 🟦 Load danh mục gốc
const loadRootCategories = async () => {
  try {
    const result = await adminStore.fetchCategories();
    rootCategories.value = (result.content || result || []).filter(
      (cat) => !cat.parentId
    );
  } catch (error) {
    logger.error("Lỗi khi tải danh mục gốc:", error);
  }
};

// ================== LIFECYCLE ==================
onMounted(() => {
  loadRootCategories();
});
// ==============================
// 🔹 MATERIAL & SOLE STATE
// ==============================
const selectedMaterialName = ref("");
const selectedSoleName = ref("");

// ==============================
// 🔹 MATERIAL CHANGE HANDLER
// ==============================
function onMaterialChange(id) {
  const material = materials.value.find((m) => m.id === id);
  selectedMaterialName.value = material ? material.name : "";
}

// 🔹 SOLE CHANGE HANDLER
function onSoleChange(id) {
  const sole = soles.value.find((s) => s.id === id);
  selectedSoleName.value = sole ? sole.name : "";
}

// Bulk selection state
const selectedProducts = ref([]);

// ✅ Chỉ hiển thị danh mục con (level > 0)
// ✅ Chỉ hiển thị danh mục con (có parentId)
const childCategories = computed(() => {
  return categories.value.filter((cat) => cat.parentId != null);
});

// Advanced filters
const filters = ref({
  search: "",
  brandId: null,
  categoryId: null,
  status: "all",
  minPrice: null,
  maxPrice: null,
  stockLevel: "all",
  sortBy: "",
  sortDirection: "asc",
});

// Import state
const importPreview = ref([]);
const importValidation = ref(null);

// Bulk update state
const bulkUpdateAction = ref("");
const bulkUpdateValue = ref({
  isActive: true,
  brandId: null,
  categoryId: null,
});

const formData = ref({
  name: "",
  slug: "",
  brandId: null,
  description: "",
  isActive: true,
  categoryIds: [],
  images: [],
  mainImageUrl: null, // 🆕 Danh sách ảnh sản phẩm (gallery)
  materialId: null, // 🆕
  shoeSoleId: null, // 🆕
  variants: [],
});

const formErrors = ref({});

// Computed
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value));

const isAllSelected = computed(() => {
  return (
    products.value.length > 0 &&
    selectedProducts.value.length === products.value.length
  );
});

// Methods
// ===== STOCK HELPERS =====
const getTotalStock = (product) => {
  if (typeof product.totalStock === "number") return product.totalStock;
  return 0;
};

const getStockClass = (product) => {
  const totalStock = getTotalStock(product);
  if (totalStock === 0) return "out-of-stock";
  if (totalStock <= 10) return "low-stock";
  return "in-stock";
};

const getStockIcon = (product) => {
  const stockClass = getStockClass(product);
  if (stockClass === "out-of-stock") return "remove_shopping_cart";
  if (stockClass === "low-stock") return "warning";
  return "check_circle";
};

const getStockText = (product) => {
  const stockClass = getStockClass(product);
  if (stockClass === "out-of-stock") return "Hết hàng";
  if (stockClass === "low-stock") return "Sắp hết";
  return "Còn hàng";
};

const getStockStatusText = (product) => {
  return getStockText(product);
};

// ===== SORT FUNCTIONALITY =====
const sortColumn = (column) => {
  if (sortBy.value === column) {
    // Toggle sort order if clicking same column
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    // Set new column and default to ascending
    sortBy.value = column;
    sortOrder.value = "asc";
  }

  // Sort products locally (for better UX, could also fetch from server with sort params)
  products.value.sort((a, b) => {
    let aVal = a[column];
    let bVal = b[column];

    // Handle null/undefined
    if (aVal == null) aVal = "";
    if (bVal == null) bVal = "";

    // String comparison
    if (typeof aVal === "string") {
      aVal = aVal.toLowerCase();
      bVal = bVal.toLowerCase();
    }

    const comparison = aVal > bVal ? 1 : aVal < bVal ? -1 : 0;
    return sortOrder.value === "asc" ? comparison : -comparison;
  });
};

const getSortIcon = (column) => {
  if (sortBy.value !== column) return "unfold_more";
  return sortOrder.value === "asc" ? "arrow_upward" : "arrow_downward";
};

// ===== FETCH DATA =====
const fetchProducts = async () => {
  try {
    loading.value = true;

    // Prepare filters for API
    const apiFilters = {
      search: filters.value.search || undefined,
      brandId: filters.value.brandId || undefined,
      categoryId: filters.value.categoryId || undefined,
      status: filters.value.status !== "all" ? filters.value.status : undefined,
      minPrice: filters.value.minPrice || undefined,
      maxPrice: filters.value.maxPrice || undefined,
      stockLevel:
        filters.value.stockLevel !== "all"
          ? filters.value.stockLevel
          : undefined,
      sortBy: sortBy.value || undefined,
      sortDirection: sortOrder.value || undefined,
    };

    const result = await adminStore.fetchProducts(
      currentPage.value,
      pageSize.value,
      apiFilters
    );
    products.value = result.content || [];
    totalItems.value = result.totalElements || 0;
  } catch (error) {
    handleError(error, {
      showToast: true,
      log: true,
      customMessage: "Không thể tải danh sách sản phẩm",
    });
  } finally {
    loading.value = false;
  }
};

const fetchBrands = async () => {
  try {
    await adminStore.fetchBrands();
    brands.value = adminStore.brands;
  } catch (error) {
    logger.error("Lỗi khi tải danh sách thương hiệu:", error);
  }
};

const fetchCategories = async () => {
  try {
    await adminStore.fetchCategories();
    categories.value = adminStore.categories;
  } catch (error) {
    logger.error("Lỗi khi tải danh sách danh mục:", error);
  }
};

const fetchStatistics = async () => {
  try {
    const response = await adminStore.getProductStatistics();
    stats.value = response;
  } catch (error) {
    logger.error("Lỗi khi tải thống kê:", error);
  }
};

// ===== MATERIALS & SOLES =====
const fetchMaterials = async () => {
  try {
    await adminStore.fetchMaterials();
    materials.value = adminStore.materials;
  } catch (error) {
    logger.error("Lỗi khi tải danh sách chất liệu:", error);
  }
};

const fetchSoles = async () => {
  try {
    await adminStore.fetchSoles();
    soles.value = adminStore.soles;
  } catch (error) {
    logger.error("Lỗi khi tải danh sách loại đế giày:", error);
  }
};

// ===== BULK SELECTION =====
// 🟢 Tick từng dòng (hiệu ứng riêng cho từng hàng)
const toggleSelect = async (productId) => {
  const index = selectedProducts.value.indexOf(productId);
  if (index > -1) {
    // Bỏ chọn
    selectedProducts.value.splice(index, 1);
  } else {
    // Thêm chọn
    selectedProducts.value.push(productId);
    await nextTick();

    const row = document.querySelector(`[data-id="${productId}"]`);
    if (row) {
      row.classList.add(
        "ring-2",
        "ring-green-400",
        "bg-green-50",
        "dark:bg-green-900/30",
        "animate-pulse"
      );
      setTimeout(() => {
        row.classList.remove(
          "ring-2",
          "ring-green-400",
          "bg-green-50",
          "dark:bg-green-900/30",
          "animate-pulse"
        );
      }, 2000);
    }
  }
};

// 🟩 Tick checkbox đầu bảng (chọn tất cả)
const toggleSelectAll = async () => {
  if (isAllSelected.value) {
    selectedProducts.value = [];
  } else {
    selectedProducts.value = products.value.map((p) => p.id);
    await nextTick();

    selectedProducts.value.forEach((id) => {
      const row = document.querySelector(`[data-id="${id}"]`);
      if (row) {
        row.classList.add(
          "ring-2",
          "ring-green-400",
          "bg-green-50",
          "dark:bg-green-900/30",
          "animate-pulse"
        );
        setTimeout(() => {
          row.classList.remove(
            "ring-2",
            "ring-green-400",
            "bg-green-50",
            "dark:bg-green-900/30",
            "animate-pulse"
          );
        }, 2000);
      }
    });
  }
};

const clearSelection = () => {
  selectedProducts.value = [];
};

const showBulkDeleteConfirm = ref(false);

const bulkDelete = async () => {
  if (selectedProducts.value.length === 0) {
    notificationService.warning(
      "Cảnh báo",
      "Vui lòng chọn ít nhất một sản phẩm để xóa"
    );
    return;
  }
  showBulkDeleteConfirm.value = true;
};

const bulkDeleteConfirmed = async () => {
  let loadingToastId = null;
  try {
    const totalCount = selectedProducts.value.length;

    // Hiển thị toast "Đang xử lý..."
    loadingToastId = notificationService.info(
      "Đang xử lý...",
      `Đang xóa ${totalCount} sản phẩm...`,
      { duration: 0 }
    );

    for (const productId of selectedProducts.value) {
      await adminStore.deleteProduct(productId);
    }

    // Đóng toast loading và hiển thị toast success
    if (loadingToastId) {
      notificationService.removeNotification(loadingToastId);
    }
    notificationService.success(
      "Thành công",
      `Đã xóa ${totalCount} sản phẩm thành công!`
    );

    // Log activity
    try {
      await logBulkProductOperation(
        "DELETE",
        totalCount,
        selectedProducts.value
      );
    } catch (err) {
      logger.warn("Failed to log bulk delete activity:", err);
    }

    selectedProducts.value = [];
    await fetchProducts();
    await fetchStatistics();
  } catch (error) {
    // Đóng toast loading nếu có lỗi
    if (loadingToastId) {
      notificationService.removeNotification(loadingToastId);
    }
    handleError(error, { showToast: true, log: true });
  }
};

// ===== MODAL ACTIONS =====
const openCreateModal = () => {
  isEditMode.value = false;
  editingProduct.value = null; // ✅ để modal hiểu là "create"
  productImages.value = [];
  initialProductImages.value = [];
  uploadedImages.value = [];
  formData.value = {
    name: "",
    slug: "",
    brandId: null,
    description: "",
    images: [], // 🧹 reset hình ảnh
    mainImageUrl: null,
    isActive: true,
    categoryIds: [],
    materialId: null,
    shoeSoleId: null,
    priceFrom: null,
    priceTo: null,
    variants: [],
  };
  formErrors.value = {};
  showModal.value = true;
};

const editingProduct = ref(null);

const openEditModal = async (product) => {
  isEditMode.value = true;
  editingProduct.value = null;
  loadingProductDetail.value = true; // ✅ Dùng loading riêng, không ảnh hưởng đến bảng

  try {
    // ✅ Chỉ fetch khi dữ liệu chưa có (đã được load ở onMounted rồi)
    // Tránh gọi lại các API này vì chúng set loading.value = true trong store
    // và có thể trigger reload danh sách sản phẩm
    const fetchPromises = [];
    if (!brands.value || brands.value.length === 0) {
      fetchPromises.push(adminStore.fetchBrands?.());
    }
    if (!categories.value || categories.value.length === 0) {
      fetchPromises.push(adminStore.fetchCategories?.());
    }
    if (!materials.value || materials.value.length === 0) {
      fetchPromises.push(adminStore.fetchMaterials?.());
    }
    if (!soles.value || soles.value.length === 0) {
      fetchPromises.push(adminStore.fetchSoles?.());
    }

    // Chỉ await nếu có promise nào cần chạy
    if (fetchPromises.length > 0) {
      await Promise.all(fetchPromises);
      // Update local refs sau khi fetch
      brands.value = adminStore.brands;
      categories.value = adminStore.categories;
      materials.value = adminStore.materials;
      soles.value = adminStore.soles;
    }

    const detailData = await adminStore.getProductById(product.id);

    // Debug: Log data nhận được từ API (có thể xóa sau khi test)
    // console.log("🔍 Detail data từ API:", detailData);
    // console.log("🔍 priceFrom:", detailData.priceFrom);
    // console.log("🔍 priceTo:", detailData.priceTo);

    // ⚠️ Quan trọng: Luôn tạo object mới để Vue detect change
    editingProduct.value = JSON.parse(
      JSON.stringify({
        id: detailData.id,
        name: detailData.name || "",
        slug: detailData.slug || "",
        brandId: detailData.brandId || null,
        description: detailData.description || "",
        isActive:
          detailData.isActive !== undefined ? detailData.isActive : true,
        categoryIds: detailData.categories?.map((c) => c.id) || [],
        materialId: detailData.materialId ?? null,
        shoeSoleId: detailData.shoeSoleId ?? null,
        variants:
          detailData.variants?.map((v) => ({
            id: v.id,
            sku: v.sku || "",
            size: v.size || "",
            color: v.color || "",
            priceBase: v.priceBase || 0,
            priceSale: v.priceSale ?? null,
            stockQuantity: v.stockQuantity || 0,
            imageUrl: v.imageUrl || "",
          })) || [],
      })
    );

    // Debug logs (có thể xóa sau khi test)
    // console.log("🔍 editingProduct sau khi map:", editingProduct.value);
    // console.log("🔍 editingProduct.priceFrom:", editingProduct.value.priceFrom);
    // console.log("🔍 editingProduct.priceTo:", editingProduct.value.priceTo);

    // ✅ Đảm bảo formData cũng được cập nhật
    formData.value = { ...editingProduct.value };

    // 🟢 Bổ sung phần LOAD ẢNH từ API
    const { data: imageData } = await axios.get(
      `/api/admin/products/${product.id}/images`
    );

    // Chuẩn hóa về format UploadGallery hiểu được
    initialProductImages.value = (imageData || []).map((img) => ({
      id: img.id,
      previewUrl:
        img.imageUrl.startsWith("http") || img.imageUrl.startsWith("blob:")
          ? img.imageUrl
          : img.imageUrl?.startsWith("http")
          ? img.imageUrl
          : `${import.meta.env.VITE_API_URL || ""}${img.imageUrl}`, // Backend serve static files
      isPrimary: !!img.isPrimary,
      displayOrder: img.displayOrder ?? 0, // 🆕 giữ nguyên thứ tự từ BE
      file: null,
      type: "db", // ✅ phân biệt ảnh từ DB
    }));

    // 🧠 Thêm snapshot ban đầu để so sánh sau
    originalImagesSnapshot.value = initialProductImages.value.map((img) => ({
      id: img.id,
      isPrimary: !!img.isPrimary,
      displayOrder: img.displayOrder ?? 0,
    }));

    // Gán cho UploadGallery
    productImages.value = [...initialProductImages.value];
    formData.value.images = [...initialProductImages.value];

    // 🟢 Chỉ mở modal sau khi gán xong object mới
    showModal.value = true;
  } catch (error) {
    handleError(error, {
      showToast: true,
      log: true,
      customMessage: "Không thể tải chi tiết sản phẩm",
    });
    // Fallback: sử dụng dữ liệu cơ bản từ product
    editingProduct.value = {
      id: product.id,
      name: product.name || "",
      slug: product.slug || "",
      brandId: product.brandId || null,
      description: "",
      isActive: true,
      categoryIds: [],
      materialId: null,
      shoeSoleId: null,
      variants: [],
    };
    initialProductImages.value = [];
    productImages.value = [];
    showModal.value = true;
  } finally {
    loadingProductDetail.value = false; // ✅ Dùng loading riêng
  }
};

// 🟢 Lấy ảnh sản phẩm riêng (nếu BE có API riêng)
const fetchProductImages = async (productId) => {
  try {
    const images = await adminStore.fetchProductImages(productId);
    initialProductImages.value = images.map((img) => ({
      id: img.id,
      previewUrl: img.imageUrl,
      isPrimary: !!img.isPrimary,
      file: null,
      type: "remote",
    }));
    productImages.value = [...initialProductImages.value];
    formData.value.images = [...initialProductImages.value];
  } catch (error) {
    handleError(error, { showToast: false, log: true }); // Silent error for images
  }
};

const closeModal = () => {
  showModal.value = false;
  isEditMode.value = false;
  editingProduct.value = null;
  formErrors.value = {};
  // Reset form về mặc định — NHỚ giữ đủ key cho reactivity
  formData.value = {
    name: "",
    slug: "",
    brandId: null,
    description: "",
    isActive: true,
    categoryIds: [],
    images: [], // 🧹 reset hình ảnh
    // 🆕 reset 2 field mới
    materialId: null,
    shoeSoleId: null,
    variants: [],
  };
  // 🧹 Cleanup blob URL khi đóng modal
  productImages.value.forEach((img) => {
    if (img.file && img.previewUrl?.startsWith("blob:")) {
      URL.revokeObjectURL(img.previewUrl);
    }
  });
  productImages.value = [];
  initialProductImages.value = [];
  uploadedImages.value = [];
  formData.value.mainImageUrl = null;
  formErrors.value = {};
};

const generateSlug = () => {
  if (!isEditMode.value) {
    formData.value.slug = generateSlugUtil(formData.value.name);
  }
};

const addVariant = () => {
  formData.value.variants.push({
    sku: "",
    size: "",
    color: "",
    priceBase: 0,
    priceSale: null,
    stockQuantity: 0,
    imageUrl: "",
  });
};

const removeVariant = (index) => {
  formData.value.variants.splice(index, 1);
};

const validateForm = () => {
  formErrors.value = {};

  if (!formData.value.name || formData.value.name.trim() === "") {
    formErrors.value.name = "Tên sản phẩm không được để trống";
  }

  if (!formData.value.slug || formData.value.slug.trim() === "") {
    formErrors.value.slug = "Slug không được để trống";
  }

  if (!formData.value.brandId) {
    formErrors.value.brandId = "Vui lòng chọn thương hiệu";
  }

  if (formData.value.categoryIds.length === 0) {
    formErrors.value.categoryIds = "Vui lòng chọn ít nhất 1 danh mục";
  }

  if (formData.value.variants.length === 0) {
    formErrors.value.variants = "Vui lòng thêm ít nhất 1 variant";
    return false;
  }

  return Object.keys(formErrors.value).length === 0;
};

const handleSubmit = async (submittedData = null) => {
  // Nếu có data từ ProductFormModal, dùng data đó
  const dataToSubmit = submittedData || formData.value;
  try {
    isSubmitting.value = true;
    const updatedIds = new Set(); // 🧠 tránh update trùng

    // ==================== [1] VALIDATE CƠ BẢN ====================
    if (!formData.value.name?.trim()) {
      notificationService.warning("Cảnh báo", "Vui lòng nhập tên sản phẩm");
      return;
    }

    if (!formData.value.slug?.trim()) {
      notificationService.warning(
        "Cảnh báo",
        "Slug không được để trống (hãy nhập tên để tự sinh slug)"
      );
      return;
    }

    if (!formData.value.brandId) {
      notificationService.warning("Cảnh báo", "Vui lòng chọn thương hiệu");
      return;
    }

    if (
      !formData.value.categoryIds ||
      formData.value.categoryIds.length === 0
    ) {
      notificationService.warning(
        "Cảnh báo",
        "Vui lòng chọn ít nhất 1 danh mục"
      );
      return;
    }

    if (!formData.value.variants || formData.value.variants.length === 0) {
      notificationService.warning(
        "Cảnh báo",
        "Vui lòng thêm ít nhất 1 biến thể sản phẩm"
      );
      return;
    }

    if (productImages.value.length > MAX_IMAGES_PER_PRODUCT) {
      notificationService.warning(
        "Cảnh báo",
        `Tối đa ${MAX_IMAGES_PER_PRODUCT} ảnh cho mỗi sản phẩm`
      );
      return;
    }

    // ==================== [2] XÓA ẢNH ĐÃ GỠ ====================
    if (removedImageUrls.value?.length > 0) {
      for (const url of removedImageUrls.value) {
        try {
          await axios.delete(
            `/api/admin/products/${formData.value.id}/images`,
            {
              data: { imageUrl: url },
              headers: { "Content-Type": "application/json" },
            }
          );
        } catch (e) {
          logger.error("❌ Xóa ảnh lỗi:", url, e);
        }
      }
    }

    // ==================== [3] ẢNH HIỆN CÓ TRONG DB ====================
    // Tính danh sách ảnh DB còn lại sau khi xóa
    const removedSet = new Set(removedImageUrls.value);

    const dbImagesEffective = (initialProductImages.value || []).filter(
      (img) => !removedSet.has(img.previewUrl)
    );

    // Xem còn ảnh nào được đánh dấu là primary không
    const dbHasPrimary = dbImagesEffective.some(
      (img) => img.isPrimary === true
    );

    // Sau khi tính xong mới clear để vòng sau không bị lặp
    removedImageUrls.value = [];

    // ==================== [4] TẠO / CẬP NHẬT SẢN PHẨM ====================
    const productPayload = {
      id: dataToSubmit.id || null,
      name: dataToSubmit.name?.trim(),
      slug: dataToSubmit.slug?.trim(),
      description: dataToSubmit.description?.trim() || "",
      brandId: dataToSubmit.brandId,
      categoryIds: dataToSubmit.categoryIds,
      materialId: dataToSubmit.materialId,
      shoeSoleId: dataToSubmit.shoeSoleId,
      isActive: dataToSubmit.isActive ?? true,

      variants: dataToSubmit.variants.map((v) => ({
        id: v.id || null, // 🟢 GIỮ ID khi update (nếu variant cũ)
        sku: v.sku?.trim(),
        color: v.color?.trim(),
        size: v.size?.trim(),
        priceBase: Number(v.priceBase) || 0,
        priceSale:
          v.priceSale !== null && v.priceSale !== undefined
            ? Number(v.priceSale)
            : null,
        stockQuantity: Number(v.stockQuantity) || 0,
        imageUrl: v.imageUrl || "", // 🆕 optional - giữ để sync ảnh variant nếu có
      })),
    };

    let savedProduct = null;

    if (isEditMode.value && dataToSubmit.id) {
      const res = await axios.put(
        `/api/admin/products/${dataToSubmit.id}`,
        productPayload,
        { headers: { "Content-Type": "application/json" } }
      );
      savedProduct = res.data;
    } else {
      const res = await axios.post(`/api/admin/products`, productPayload, {
        headers: { "Content-Type": "application/json" },
      });
      savedProduct = res.data;
    }

    if (!savedProduct?.id) throw new Error("Không thể lưu sản phẩm");

    const productId = savedProduct.id;

    // ==================== [5] UPLOAD ẢNH MỚI ====================
    const uploadedUrls = [];
    const uploadedImagesMap = new Map(); // Map để lưu mapping giữa image object và URL đã upload

    // 🆕 Tính thứ tự cao nhất trong DB 1 lần duy nhất trước vòng for
    const maxDisplayOrder = Math.max(
      0,
      ...(initialProductImages.value
        ?.filter((x) => x.type === "db")
        ?.map((x) => x.displayOrder ?? 0) || [])
    );
    let uploadIndexStart = maxDisplayOrder + 1; // bắt đầu ngay sau ảnh cao nhất

    for (const [idx, img] of productImages.value.entries()) {
      if (img.type === "db") continue; // ảnh đã trong DB thì bỏ qua

      const displayOrder = uploadIndexStart++; // 🧩 tăng dần theo tổng ảnh cũ
      const isPrimaryChosen = !!img.isPrimary;

      // Nếu trong DB đã có primary thì không gửi thêm primary nữa
      const dbHasPrimary = (initialProductImages.value || []).some(
        (x) => x.isPrimary
      );
      const willSendPrimary = dbHasPrimary ? false : isPrimaryChosen;

      if ((img.type === "local" || img.file) && img.file) {
        const formUpload = new FormData();
        formUpload.append("file", img.file);
        formUpload.append("isPrimary", String(willSendPrimary));
        formUpload.append("displayOrder", String(displayOrder));

        try {
          const res = await axios.post(
            `/api/admin/products/${productId}/images/upload`,
            formUpload,
            { headers: { "Content-Type": "multipart/form-data" } }
          );
          const uploadedUrl = res.data?.imageUrl;
          uploadedUrls.push(uploadedUrl);
          // Lưu mapping để dùng sau
          uploadedImagesMap.set(img, uploadedUrl);
        } catch (err) {
          logger.error("❌ Upload ảnh local lỗi:", err);
          notificationService.apiError(err, "Upload ảnh local thất bại");
        }
      } else if (img.type === "url" && img.previewUrl) {
        try {
          const res = await axios.post(
            `/api/admin/products/${productId}/images`,
            {
              imageUrl: img.previewUrl,
              isPrimary: willSendPrimary,
              displayOrder: displayOrder,
            },
            { headers: { "Content-Type": "application/json" } }
          );
          const uploadedUrl = res.data?.imageUrl || img.previewUrl;
          uploadedUrls.push(uploadedUrl);
          // Lưu mapping để dùng sau
          uploadedImagesMap.set(img, uploadedUrl);
        } catch (err) {
          logger.error("❌ Upload ảnh URL lỗi:", err);
          notificationService.apiError(err, "Upload ảnh URL thất bại");
        }
      }
    }

    // ==================== [6] XỬ LÝ ẢNH BÌA & THỨ TỰ HIỂN THỊ ====================
    try {
      // 🔹 [6.1] Nếu ảnh mới upload được đánh dấu là ảnh bìa
      const primaryNow = productImages.value.find((i) => i.isPrimary);
      if (primaryNow && primaryNow.type !== "db") {
        // Fetch lại danh sách ảnh từ BE để lấy id thực của ảnh vừa upload
        const { data: updatedImages } = await axios.get(
          `/api/admin/products/${productId}/images`
        );
        const matched = updatedImages.find((x) =>
          x.imageUrl.includes(primaryNow.previewUrl.split("/").pop())
        );
        if (matched) {
          await axios.put(
            `/api/admin/products/${productId}/images/${matched.id}`,
            { isPrimary: true },
            { headers: { "Content-Type": "application/json" } }
          );
          logger.log(`✅ Ảnh mới upload được gán làm ảnh bìa ID=${matched.id}`);
        }
      }

      // 🔹 [6.2] Xử lý đổi ảnh bìa giữa các ảnh DB
      const currentDbImages = productImages.value.filter(
        (img) => img.type === "db" && img.id
      );
      const currentPrimary = currentDbImages.find((img) => img.isPrimary);
      const oldPrimary = originalImagesSnapshot.value.find((x) => x.isPrimary);

      // 🔸 Đảm bảo chỉ có 1 ảnh có isPrimary = true
      const duplicates = currentDbImages.filter((img) => img.isPrimary);
      if (duplicates.length > 1) {
        await Promise.all(
          duplicates
            .slice(1)
            .map((img) =>
              axios.put(
                `/api/admin/products/${productId}/images/${img.id}`,
                { isPrimary: false },
                { headers: { "Content-Type": "application/json" } }
              )
            )
        );
      }

      if (currentPrimary && oldPrimary && currentPrimary.id !== oldPrimary.id) {
        logger.log(`🔄 Đổi ảnh bìa từ ${oldPrimary.id} → ${currentPrimary.id}`);

        // 1️⃣ Bỏ cờ primary ở ảnh cũ
        await axios.put(
          `/api/admin/products/${productId}/images/${oldPrimary.id}`,
          { isPrimary: false },
          { headers: { "Content-Type": "application/json" } }
        );
        updatedIds.add(oldPrimary.id);

        // 2️⃣ Gắn cờ primary cho ảnh mới
        await axios.put(
          `/api/admin/products/${productId}/images/${currentPrimary.id}`,
          { isPrimary: true },
          { headers: { "Content-Type": "application/json" } }
        );
        updatedIds.add(currentPrimary.id);

        notificationService.success(
          "Thành công",
          "Đã cập nhật ảnh bìa thành công!"
        );
      }

      // 🔹 [6.3] Cập nhật displayOrder & isPrimary nếu thay đổi
      for (const [idx, img] of productImages.value.entries()) {
        if (img.type !== "db" || !img.id) continue;
        if (updatedIds.has(img.id)) continue; // 🚫 bỏ qua ảnh đã xử lý ở trên
        const prev = originalImagesSnapshot.value.find((x) => x.id === img.id);
        if (!prev) continue;

        // Nếu thay đổi displayOrder hoặc trạng thái primary → update
        if (prev.isPrimary !== img.isPrimary || img.displayOrder !== idx + 1) {
          try {
            await axios.put(
              `/api/admin/products/${productId}/images/${img.id}`,
              { isPrimary: img.isPrimary, displayOrder: idx + 1 },
              { headers: { "Content-Type": "application/json" } }
            );
            logger.log(
              `🆙 Update ảnh ID=${img.id} → order=${idx + 1}, primary=${
                img.isPrimary
              }`
            );
          } catch (err) {
            logger.error("❌ Update ảnh DB lỗi:", err);
          }
        }
      }

      // 🔹 [6.4] Cập nhật mainImageUrl cho sản phẩm
      const finalPrimary = productImages.value.find((i) => i.isPrimary);
      if (finalPrimary) {
        // Chỉ dùng URL hợp lệ (không phải blob URL từ file local chưa upload)
        let mainImageUrl = null;

        if (finalPrimary.type === "db" && finalPrimary.previewUrl) {
          // Ảnh từ DB - dùng previewUrl (đã là Cloudinary URL)
          mainImageUrl = finalPrimary.previewUrl;
        } else if (
          finalPrimary.type === "local" ||
          finalPrimary.type === "url"
        ) {
          // Ảnh mới upload - lấy URL từ map hoặc uploadedUrls
          if (uploadedImagesMap.has(finalPrimary)) {
            // URL từ response của upload API
            mainImageUrl = uploadedImagesMap.get(finalPrimary);
          } else if (
            finalPrimary.previewUrl &&
            !finalPrimary.previewUrl.startsWith("blob:")
          ) {
            // Nếu previewUrl không phải blob URL (ví dụ: URL từ input)
            mainImageUrl = finalPrimary.previewUrl;
          } else if (uploadedUrls.length > 0) {
            // Fallback: dùng URL đầu tiên từ uploadedUrls
            mainImageUrl = uploadedUrls[0];
          }
        }

        // Chỉ cập nhật nếu có URL hợp lệ
        if (mainImageUrl && mainImageUrl.startsWith("http")) {
          try {
            await axios.put(
              `/api/admin/products/${productId}`,
              {
                name: formData.value.name,
                slug: formData.value.slug,
                description: formData.value.description,
                brandId: formData.value.brandId,
                categoryIds: formData.value.categoryIds,
                materialId: formData.value.materialId,
                shoeSoleId: formData.value.shoeSoleId,
                isActive: formData.value.isActive,
                mainImageUrl: mainImageUrl, // 🧩 chỉ gửi URL hợp lệ
                variants: formData.value.variants.map((v) => ({
                  id: v.id || null, // 🟢 GIỮ ID khi update
                  sku: v.sku,
                  color: v.color,
                  size: v.size,
                  priceBase: v.priceBase,
                  priceSale: v.priceSale,
                  stockQuantity: v.stockQuantity,
                })),
              },
              { headers: { "Content-Type": "application/json" } }
            );

            formData.value.mainImageUrl = mainImageUrl;
            logger.log(`✅ Đã cập nhật mainImageUrl: ${mainImageUrl}`);
          } catch (err) {
            logger.error("❌ Lỗi khi cập nhật mainImageUrl:", err);
            // Không throw error để không block việc lưu sản phẩm
          }
        } else {
          logger.warn("⚠️ Không có URL hợp lệ để cập nhật mainImageUrl");
        }
      }
    } catch (err) {
      logger.error("❌ Lỗi khi xử lý ảnh bìa / thứ tự hiển thị:", err);
      notificationService.apiError(
        err,
        "Cập nhật ảnh bìa hoặc thứ tự hiển thị thất bại"
      );
    }

    // ==================== [7] LOG ACTIVITY ====================
    try {
      if (isEditMode.value) {
        await logProductUpdate(
          savedProduct.id,
          editingProduct.value,
          savedProduct
        );
      } else {
        await logProductCreate(savedProduct.id, savedProduct);
      }
    } catch (err) {
      logger.warn("Failed to log activity:", err);
    }

    // ==================== [8] THÔNG BÁO & RESET FORM ====================
    notificationService.success(
      "Thành công",
      isEditMode.value
        ? "Cập nhật sản phẩm thành công!"
        : "Tạo sản phẩm mới thành công!"
    );

    highlightedProductId.value = savedProduct.id;

    // 🧭 Gọi fetch và chờ render hoàn tất
    await fetchProducts();
    await fetchStatistics();
    await nextTick(); // 🟢 BẮT BUỘC: chờ Vue render xong bảng mới

    // 🟢 Cuộn đến hàng vừa tạo/cập nhật
    const el = document.querySelector(
      `[data-id="${highlightedProductId.value}"]`
    );
    if (el) {
      el.scrollIntoView({
        behavior: "smooth",
        block: "center",
      });
      // 🩵 Nhấp nháy nhẹ khi cuộn đến (Tailwind có sẵn animate)
      el.classList.add(
        "animate-pulse",
        "ring-2",
        "ring-green-400",
        "bg-green-50",
        "dark:bg-green-900/20"
      );
      setTimeout(() => {
        el.classList.remove(
          "animate-pulse",
          "ring-2",
          "ring-green-400",
          "bg-green-50",
          "dark:bg-green-900/20"
        );
      }, 2000);
    }

    // 🕒 Xoá highlight sau 3s
    setTimeout(() => {
      highlightedProductId.value = null;
    }, 3000);

    closeModal();
  } catch (error) {
    const msg = error?.response?.data?.message || "";

    // 🟣 Bắt lỗi SKU trùng
    if (msg.includes("SKU") && msg.includes("bị trùng lặp")) {
      notificationService.error("Lỗi trùng SKU", msg);
      formErrors.value.variants =
        "SKU bị trùng, vui lòng kiểm tra lại biến thể!";
      isSubmitting.value = false;
      return;
    }

    // 🟠 Các lỗi khác
    handleError(error, {
      showToast: true,
      log: true,
      customMessage: "Không thể lưu sản phẩm",
    });
  } finally {
    isSubmitting.value = false;
  }
};

// 🟣 Khi thay đổi ảnh trong UploadGallery (giống VariantModal.vue)
const onProductImagesChange = (images) => {
  // images = [{ file, previewUrl, isPrimary }]
  productImages.value = images;

  // Ghi nhận vào formData để khi submit gửi đúng payload
  formData.value.images = images.map((img) => ({
    file: img.file,
    previewUrl: img.previewUrl,
    isPrimary: !!img.isPrimary,
    type: img.type || "local",
  }));

  // Ảnh chính (primary) dùng làm đại diện sản phẩm
  const primary = images.find((i) => i.isPrimary);
  formData.value.mainImageUrl = primary ? primary.previewUrl : null;
};

const onProductImageRemove = (payload) => {
  // Chấp nhận cả kiểu cũ (string URL) lẫn kiểu mới (object)
  const { url } =
    typeof payload === "string" ? { url: payload } : payload || {};

  if (url && !url.startsWith("blob:")) {
    removedImageUrls.value.push(url);
    // Nếu xoá đúng ảnh đang là main → clear
    if (formData.value.mainImageUrl === url) {
      formData.value.mainImageUrl = null;
    }
  }
};

const confirmDelete = (product) => {
  productToDelete.value = product;
  showDeleteModal.value = true;
};

const handleDelete = async () => {
  let loadingToastId = null;
  try {
    deleting.value = true;
    const productName = productToDelete.value.name;

    // Hiển thị toast "Đang xử lý..."
    loadingToastId = notificationService.info(
      "Đang xử lý...",
      `Đang xóa sản phẩm "${productName}"`,
      { duration: 0 }
    );

    await adminStore.deleteProduct(productToDelete.value.id);

    // Đóng toast loading và hiển thị toast success
    if (loadingToastId) {
      notificationService.removeNotification(loadingToastId);
    }
    notificationService.success(
      "Thành công",
      `Đã xóa sản phẩm "${productName}" thành công!`
    );

    // Log activity
    try {
      await logProductDelete(productToDelete.value.id, productToDelete.value);
    } catch (err) {
      logger.warn("Failed to log delete activity:", err);
    }

    await fetchProducts();
    await fetchStatistics();
    showDeleteModal.value = false;
    productToDelete.value = null;
  } catch (error) {
    // Đóng toast loading nếu có lỗi
    if (loadingToastId) {
      notificationService.removeNotification(loadingToastId);
    }
    handleError(error, {
      showToast: true,
      log: true,
      customMessage: "Không thể xóa sản phẩm này",
    });
  } finally {
    deleting.value = false;
  }
};

// ===== QUICK ADD BRAND MODAL =====
const showQuickAddBrand = ref(false);
const savingQuickBrand = ref(false);

const quickBrandData = ref({
  name: "",
  slug: "",
  logoUrl: "",
  websiteUrl: "",
  description: "",
  isActive: true,
});

// Mở modal thêm nhanh
const openQuickAddBrand = () => {
  showQuickAddBrand.value = true;
};

// Đóng modal
const closeQuickAddBrand = () => {
  showQuickAddBrand.value = false;
  quickBrandData.value = {
    name: "",
    slug: "",
    logoUrl: "",
    websiteUrl: "",
    description: "",
    isActive: true,
  };
};

// Sinh slug tự động
const generateBrandSlug = () => {
  quickBrandData.value.slug = generateSlugUtil(quickBrandData.value.name);
};

// Lưu thương hiệu nhanh
const saveQuickBrand = async () => {
  if (!quickBrandData.value.name.trim()) {
    notificationService.warning("Cảnh báo", "Vui lòng nhập tên thương hiệu!");
    return;
  }

  try {
    savingQuickBrand.value = true;

    // 🟢 Gọi API tạo thương hiệu (qua adminStore)
    const res = await adminStore.createBrand(quickBrandData.value);

    notificationService.success(
      "Thành công",
      "Đã thêm thương hiệu mới thành công!"
    );
    showQuickAddBrand.value = false;

    // 🔄 Reload danh sách brands
    await fetchBrands();

    // 🔧 Tự chọn thương hiệu vừa thêm vào form sản phẩm (nếu đang mở)
    const newBrand = adminStore.brands.find(
      (b) => b.slug === quickBrandData.value.slug
    );
    if (newBrand) {
      formData.value.brandId = newBrand.id;
    }

    // Reset data
    quickBrandData.value = {
      name: "",
      slug: "",
      logoUrl: "",
      websiteUrl: "",
      description: "",
      isActive: true,
    };
  } catch (error) {
    logger.error("Lỗi khi thêm thương hiệu nhanh:", error);
    notificationService.apiError(error, "Không thể thêm thương hiệu");
  } finally {
    savingQuickBrand.value = false;
  }
};

// ===== QUICK ADD MATERIAL =====
const showQuickAddMaterial = ref(false);
const savingQuickMaterial = ref(false);
const quickMaterialData = ref({
  name: "",
  slug: "",
  description: "",
  isActive: true,
});

const openQuickAddMaterial = () => (showQuickAddMaterial.value = true);
const closeQuickAddMaterial = () => {
  showQuickAddMaterial.value = false;
  quickMaterialData.value = {
    name: "",
    slug: "",
    description: "",
    isActive: true,
  };
};

const generateMaterialSlug = () => {
  quickMaterialData.value.slug = generateSlugUtil(quickMaterialData.value.name);
};

const saveQuickMaterial = async () => {
  if (!quickMaterialData.value.name.trim()) {
    notificationService.warning("Cảnh báo", "Vui lòng nhập tên chất liệu!");
    return;
  }
  try {
    savingQuickMaterial.value = true;
    await adminStore.createMaterial(quickMaterialData.value);
    notificationService.success("Thành công", "Thêm chất liệu mới thành công!");

    // 🔄 Reload lại danh sách nếu có hàm fetch
    await fetchMaterials?.();

    // 🧩 Tự động gán chất liệu vừa thêm vào form
    const newMat = adminStore.materials.find(
      (m) => m.slug === quickMaterialData.value.slug
    );
    if (newMat) {
      selectedMaterialName.value = newMat.name;
      formData.value.materialId = newMat.id;
    }

    // 🔒 Đóng popup
    closeQuickAddMaterial();
  } catch (err) {
    logger.error("Lỗi khi thêm chất liệu:", err);
    notificationService.apiError(err, "Không thể thêm chất liệu");
  } finally {
    savingQuickMaterial.value = false;
  }
};

// ===== QUICK ADD SOLE =====
const showQuickAddSole = ref(false);
const savingQuickSole = ref(false);
const quickSoleData = ref({
  name: "",
  slug: "",
  description: "",
  isActive: true,
});

const openQuickAddSole = () => (showQuickAddSole.value = true);
const closeQuickAddSole = () => {
  showQuickAddSole.value = false;
  quickSoleData.value = { name: "", slug: "", description: "", isActive: true };
};

const generateSoleSlug = () => {
  quickSoleData.value.slug = generateSlugUtil(quickSoleData.value.name);
};

const saveQuickSole = async () => {
  if (!quickSoleData.value.name.trim()) {
    notificationService.warning("Cảnh báo", "Vui lòng nhập tên loại đế giày!");
    return;
  }
  try {
    savingQuickSole.value = true;
    await adminStore.createSole(quickSoleData.value);
    notificationService.success(
      "Thành công",
      "Thêm loại đế giày mới thành công!"
    );

    // 🔄 Reload lại danh sách nếu có hàm fetch
    await fetchSoles?.();

    // 🧩 Tự động gán loại đế vừa thêm vào form
    const newSole = adminStore.soles.find(
      (s) => s.slug === quickSoleData.value.slug
    );
    if (newSole) {
      selectedSoleName.value = newSole.name;
      formData.value.shoeSoleId = newSole.id;
    }

    // 🔒 Đóng popup
    closeQuickAddSole();
  } catch (err) {
    logger.error("Lỗi khi thêm loại đế giày:", err);
    notificationService.apiError(err, "Không thể thêm loại đế giày");
  } finally {
    savingQuickSole.value = false;
  }
};

// ===== IMPORT EXCEL =====
const openImportModal = () => {
  importPreview.value = [];
  showImportModal.value = true;
};

const closeImportModal = () => {
  showImportModal.value = false;
  importPreview.value = [];
  importValidation.value = null;
};

const downloadTemplate = () => {
  const template = [
    {
      "Tên sản phẩm": "Nike Air Force 1",
      Slug: "nike-air-force-1",
      "Thương hiệu": "Nike",
      "Mô tả": "Giày thể thao Nike Air Force 1",
      "Danh mục": "Men,Sneakers",
      "Trạng thái": "TRUE",
      SKU: "NIKE-AF1-WHT-42",
      Size: "42",
      "Màu sắc": "White",
      "Giá gốc": "2500000",
      "Giá sale": "2000000",
      "Tồn kho": "50",
      "URL ảnh": "/placeholder-image.png",
    },
  ];

  const worksheet = XLSX.utils.json_to_sheet(template);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, "Template");
  XLSX.writeFile(workbook, "template-import-products.xlsx");
};

const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  try {
    let jsonData = [];

    // Check file type
    const isCSV = file.name.toLowerCase().endsWith(".csv");

    if (isCSV) {
      // Parse CSV
      jsonData = await parseCSV(file);
    } else {
      // Parse Excel
      const reader = new FileReader();
      await new Promise((resolve, reject) => {
        reader.onload = (e) => {
          try {
            const data = new Uint8Array(e.target.result);
            const workbook = XLSX.read(data, { type: "array" });
            const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
            jsonData = XLSX.utils.sheet_to_json(firstSheet);
            resolve();
          } catch (error) {
            reject(error);
          }
        };
        reader.onerror = () => reject(new Error("Failed to read file"));
        reader.readAsArrayBuffer(file);
      });
    }

    // Map data to internal format
    importPreview.value = jsonData.map((row, index) => {
      const mapped = mapImportRow(row);
      mapped.rowNumber = index + 2; // +2 because Excel rows start at 1 and header is row 1
      return mapped;
    });

    // Validate data
    importValidation.value = validateImportData(importPreview.value, {
      brands: brands.value,
      categories: categories.value,
    });

    // Add errors to preview items
    importValidation.value.invalidRows.forEach((invalidRow) => {
      const previewItem = importPreview.value.find(
        (item) => item.rowNumber === invalidRow.rowNumber
      );
      if (previewItem) {
        previewItem.errors = invalidRow.errors;
      }
    });

    // Mark valid rows
    importValidation.value.validRows.forEach((validRow) => {
      const previewItem = importPreview.value.find(
        (item) =>
          item.rowNumber === validRow.rowNumber ||
          (item.productName === validRow.productName &&
            item.sku === validRow.sku)
      );
      if (previewItem && !previewItem.errors) {
        previewItem.errors = [];
      }
    });

    if (importValidation.value.summary.invalid > 0) {
      notificationService.warning(
        "Cảnh báo",
        `Đã đọc ${importPreview.value.length} sản phẩm, nhưng có ${importValidation.value.summary.invalid} dòng bị lỗi. Vui lòng kiểm tra và sửa lỗi trước khi import.`
      );
    } else {
      notificationService.success(
        "Thành công",
        `Đã đọc ${importPreview.value.length} sản phẩm từ file ${
          isCSV ? "CSV" : "Excel"
        }! Tất cả dữ liệu đều hợp lệ.`
      );
    }
  } catch (error) {
    logger.error("Lỗi khi đọc file:", error);
    notificationService.apiError(
      error,
      `Không thể đọc file ${file.name.endsWith(".csv") ? "CSV" : "Excel"}`
    );
    importPreview.value = [];
    importValidation.value = null;
  }

  // Reset file input
  event.target.value = "";
};

const handleImport = async () => {
  // Only import valid rows
  const validRows = importValidation.value
    ? importValidation.value.validRows
    : importPreview.value;

  if (validRows.length === 0) {
    notificationService.warning(
      "Cảnh báo",
      "Không có dữ liệu hợp lệ để import. Vui lòng sửa các lỗi trước."
    );
    return;
  }

  try {
    importing.value = true;

    // Log import activity
    try {
      await logImport(
        "Product",
        validRows.length,
        0,
        importValidation.value ? importValidation.value.summary.invalid : 0
      );
    } catch (err) {
      logger.warn("Failed to log import activity:", err);
    }

    const result = await adminStore.importProducts(validRows);

    notificationService.success(
      "Thành công",
      `Import thành công ${result.successCount}/${result.totalRows} sản phẩm!`
    );

    if (result.errorCount > 0) {
      logger.error("Import errors:", result.errorItems);
      notificationService.warning(
        "Cảnh báo",
        `Có ${result.errorCount} sản phẩm bị lỗi khi import. Xem console để biết chi tiết.`
      );
    }

    await fetchProducts();
    await fetchStatistics();
    closeImportModal();
  } catch (error) {
    logger.error("Lỗi khi import:", error);
    handleError(error, {
      showToast: true,
      log: true,
      customMessage: "Không thể import sản phẩm",
    });
  } finally {
    importing.value = false;
  }
};

// ===== BULK UPDATE =====
const openBulkUpdateModal = () => {
  bulkUpdateAction.value = "";
  bulkUpdateValue.value = {
    isActive: true,
    brandId: null,
    categoryId: null,
  };
  showBulkUpdateModal.value = true;
};

const closeBulkUpdateModal = () => {
  showBulkUpdateModal.value = false;
};

const handleBulkUpdate = async () => {
  try {
    bulkUpdating.value = true;

    const payload = {
      productIds: selectedProducts.value,
      action: bulkUpdateAction.value,
      ...bulkUpdateValue.value,
    };

    const result = await adminStore.bulkUpdateProducts(payload);
    notificationService.success(
      "Thành công",
      `Cập nhật thành công ${result.successCount}/${result.totalRequested} sản phẩm!`
    );

    await fetchProducts();
    await fetchStatistics();
    closeBulkUpdateModal();
    clearSelection();
  } catch (error) {
    logger.error("Lỗi khi bulk update:", error);
    notificationService.apiError(error, "Không thể cập nhật hàng loạt");
  } finally {
    bulkUpdating.value = false;
  }
};

// ===== DUPLICATE PRODUCT =====
// const duplicateProduct = async (productId) => {
//   let loadingToastId = null;
//   try {
//     // Hiển thị toast "Đang xử lý..." thay vì overlay
//     loadingToastId = notificationService.info(
//       "Đang xử lý...",
//       "Đang nhân bản sản phẩm",
//       { duration: 0 }
//     ); // duration: 0 = không tự đóng

//     const duplicated = await adminStore.duplicateProduct(productId);

//     // Đóng toast loading và hiển thị toast success
//     if (loadingToastId) {
//       notificationService.removeNotification(loadingToastId);
//     }
//     notificationService.success(
//       "Thành công",
//       `Đã nhân bản sản phẩm "${duplicated.name}" thành công!`
//     );

//     await fetchProducts();
//     await fetchStatistics();
//   } catch (error) {
//     // Đóng toast loading nếu có lỗi
//     if (loadingToastId) {
//       notificationService.removeNotification(loadingToastId);
//     }
//     logger.error("Lỗi khi nhân bản sản phẩm:", error);
//     notificationService.apiError(error, "Không thể nhân bản sản phẩm");
//   }
// };

// ===== FILTERS =====
const changePage = (page) => {
  currentPage.value = page;
  fetchProducts();
};

const debounceSearch = (() => {
  let timeout;
  return () => {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
      currentPage.value = 0;
      fetchProducts();
    }, 500);
  };
})();

const applyFilters = () => {
  currentPage.value = 0;
  fetchProducts();
};

const resetFilters = () => {
  filters.value = {
    search: "",
    brandId: null,
    categoryId: null,
    status: "all",
    minPrice: null,
    maxPrice: null,
    stockLevel: "all",
    sortBy: "",
    sortDirection: "asc",
  };
  currentPage.value = 0;
  fetchProducts();
};

// ===== EXPORT EXCEL =====
const exportToExcel = async () => {
  try {
    const exportData = products.value.map((product, index) => ({
      STT: index + 1,
      "Tên sản phẩm": product.name,
      Slug: product.slug,
      "Thương hiệu": product.brandName || "N/A",
      "Số lượng biến thể": product.variantCount || 0,
      "Trạng thái": product.isActive ? "Đang bán" : "Ngừng bán",
    }));

    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Sản phẩm");

    const timestamp = new Date().toISOString().slice(0, 10);
    const filename = `san-pham_${timestamp}.xlsx`;

    XLSX.writeFile(workbook, filename);
    notificationService.success(
      "Thành công",
      `Đã export ${exportData.length} sản phẩm thành công!`
    );

    // Log activity (fire and forget)
    logExport("Product", "xlsx", { count: exportData.length }).catch((err) => {
      logger.warn("Failed to log export activity:", err);
    });
  } catch (error) {
    logger.error("Lỗi khi export Excel:", error);
    notificationService.apiError(error, "Không thể export dữ liệu");
  }
};

// ===== BULK EXPORT =====
const bulkExport = () => {
  if (selectedProducts.value.length === 0) {
    notificationService.warning(
      "Cảnh báo",
      "Vui lòng chọn ít nhất một sản phẩm để xuất"
    );
    return;
  }

  try {
    // Get selected products data
    const selectedProductsData = products.value.filter((p) =>
      selectedProducts.value.includes(p.id)
    );

    const exportData = selectedProductsData.map((product, index) => ({
      STT: index + 1,
      "Mã SP": product.code || product.id,
      "Tên sản phẩm": product.name,
      Slug: product.slug,
      "Thương hiệu": product.brandName || "N/A",
      "Danh mục": product.categoryNames?.join(", ") || "N/A",
      "Số lượng biến thể": product.variantCount || 0,
      "Tổng tồn kho": product.stockQuantity || 0,
      "Trạng thái": product.isActive ? "Đang bán" : "Ngừng bán",
      "Ngày tạo": product.createdAt
        ? new Date(product.createdAt).toLocaleDateString("vi-VN")
        : "N/A",
    }));

    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Sản phẩm đã chọn");

    const timestamp = new Date().toISOString().slice(0, 10);
    const filename = `san-pham-da-chon_${timestamp}.xlsx`;

    XLSX.writeFile(workbook, filename);
    notificationService.success(
      "Thành công",
      `Đã export ${exportData.length} sản phẩm đã chọn thành công!`
    );
  } catch (error) {
    logger.error("Lỗi khi bulk export Excel:", error);
    notificationService.apiError(error, "Không thể export dữ liệu");
  }
};

// ===== HELPERS =====
// formatCurrency và formatPrice đã được import từ @/utils/formatters

// Format giá không có đơn vị "đ"
const formatPriceWithoutUnit = (price) => {
  if (price === null || price === undefined) return "";
  const numPrice = Number(price) || 0;
  return new Intl.NumberFormat("vi-VN", {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(numPrice);
};

// Lifecycle
onMounted(async () => {
  await Promise.all([
    fetchProducts(),
    fetchBrands(),
    fetchCategories(),
    fetchMaterials(), // ✅ mới thêm
    fetchSoles(), // ✅ mới thêm
    fetchStatistics(),
  ]);
});
</script>

<!-- =============================
     🎨 Shared Tailwind Styles
     ============================= -->
<style scoped>
.modal-backdrop {
  @apply fixed inset-0 z-[9999] flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 animate-fade-in;
}
.modal-container {
  @apply bg-white dark:bg-gray-800 rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-700
         max-w-2xl w-full max-h-[90vh] overflow-y-auto transition-all duration-300;
}
.modal-header {
  @apply flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10;
}
.modal-title {
  @apply text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2;
}
.modal-close-btn {
  @apply p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors;
}
.modal-body {
  @apply p-6 space-y-4;
}
.modal-footer {
  @apply flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800;
}
.form-label {
  @apply block mb-1 text-sm font-medium text-gray-700 dark:text-gray-300;
}
.form-control {
  @apply w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg text-gray-900 dark:text-gray-100
         bg-white dark:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all;
}
.form-error {
  @apply text-red-500 text-xs mt-1;
}
.btn-primary {
  @apply px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg 
         hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2;
}
.btn-secondary {
  @apply px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 
         rounded-lg transition-colors font-medium;
}
@keyframes fade-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
.animate-fade-in {
  animation: fade-in 0.25s ease-out;
}

@keyframes row-highlight {
  0% {
    background-color: rgba(34, 197, 94, 0.1);
  } /* green-500/10 */
  50% {
    background-color: rgba(34, 197, 94, 0.25);
  }
  100% {
    background-color: rgba(34, 197, 94, 0.1);
  }
}

.row-highlight {
  animation: row-highlight 1.5s ease-in-out 2;
}
</style>

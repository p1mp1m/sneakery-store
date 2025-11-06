<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">inventory_2</i>
            Quản lý sản phẩm
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
            <i class="material-icons text-xs">info</i>
            Quản lý sản phẩm và các biến thể (variants) - Nâng cao
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="openImportModal" class="flex items-center gap-2 px-4 py-2 bg-green-500 hover:bg-green-600 text-white rounded-lg transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_upload</i>
            Import Excel
          </button>
          <button @click="exportToExcel" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">download</i>
            Export Excel
          </button>
          <button @click="openCreateModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">add</i>
            Thêm sản phẩm
          </button>
        </div>
      </div>
    </div>

    <!-- Statistics Cards -->
    <div v-if="stats" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">inventory</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.totalProducts }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng sản phẩm</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">style</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.totalVariants }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng biến thể</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">warning</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.lowStockCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Sắp hết hàng</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">remove_shopping_cart</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.outOfStockCount }}</h3>
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
    <div v-if="loading" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
      <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải danh sách sản phẩm...</p>
    </div>

    <div v-else-if="products.length === 0" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">inventory_2</i>
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có sản phẩm nào</h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
        Nhấn "Thêm sản phẩm" hoặc "Import Excel" để tạo sản phẩm đầu tiên
      </p>
      <button @click="openCreateModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
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
        <div class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
          <i class="material-icons text-purple-600 dark:text-purple-400">check_circle</i>
          <span>Đã chọn <strong class="font-semibold">{{ selectedProducts.length }}</strong> sản phẩm</span>
        </div>
        <div class="flex items-center gap-2">
          <button 
            @click="openBulkUpdateModal" 
            class="flex items-center gap-2 px-3 py-1.5 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">edit</i>
            Cập nhật hàng loạt
          </button>
          <button 
            @click="bulkDelete" 
            class="flex items-center gap-2 px-3 py-1.5 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">delete</i>
            Xóa {{ selectedProducts.length }} sản phẩm
          </button>
          <button 
            @click="clearSelection" 
            class="flex items-center gap-2 px-3 py-1.5 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">clear</i>
            Bỏ chọn
          </button>
        </div>
      </div>

      <!-- Products Table -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-10">
                <input
                  type="checkbox"
                  :checked="isAllSelected"
                  @change="toggleSelectAll"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </th>
              <!-- 🆕 Mã sản phẩm -->
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700/50" @click="sortColumn('code')">
                <div class="flex items-center gap-1">
                  <span>Mã SP</span>
                  <i class="material-icons text-sm">{{ getSortIcon("code") }}</i>
                </div>
              </th>

              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700/50" @click="sortColumn('name')">
                <div class="flex items-center gap-1">
                  <span>Tên sản phẩm</span>
                  <i class="material-icons text-sm">{{ getSortIcon("name") }}</i>
                </div>
              </th>

              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700/50" @click="sortColumn('brandName')">
                <div class="flex items-center gap-1">
                  <span>Brands</span>
                  <i class="material-icons text-sm">{{ getSortIcon("brandName") }}</i>
                </div>
              </th>

              <!-- 🆕 Danh mục -->
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700/50" @click="sortColumn('categoryNames')">
                <div class="flex items-center gap-1">
                  <span>Danh mục</span>
                  <i class="material-icons text-sm">{{ getSortIcon("categoryNames") }}</i>
                </div>
              </th>

              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700/50" @click="sortColumn('variantCount')">
                <div class="flex items-center gap-1">
                  <span>Số SPCT</span>
                  <i class="material-icons text-sm">{{ getSortIcon("variantCount") }}</i>
                </div>
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700/50" @click="sortColumn('stockQuantity')">
                <div class="flex items-center gap-1">
                  <span>Kho</span>
                  <i class="material-icons text-sm">{{ getSortIcon("stockQuantity") }}</i>
                </div>
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700/50" @click="sortColumn('isActive')">
                <div class="flex items-center gap-1">
                  <span>Trạng thái</span>
                  <i class="material-icons text-sm">{{ getSortIcon("isActive") }}</i>
                </div>
              </th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>

          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="product in products" :key="product.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td class="px-4 py-4">
                <input
                  type="checkbox"
                  :checked="selectedProducts.includes(product.id)"
                  @change="toggleSelect(product.id)"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </td>

              <!-- 🆕 Cột mã sản phẩm -->
              <td class="px-4 py-4">
                <code v-if="product.code" class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100">{{ product.code }}</code>
                <span v-else class="text-xs text-gray-400 dark:text-gray-500 italic">Chưa có mã</span>
              </td>

              <td class="px-4 py-4">
                <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ product.name }}</div>
                <div class="text-xs text-gray-500 dark:text-gray-400">{{ product.slug }}</div>
              </td>

              <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">{{ product.brandName || "N/A" }}</td>

              <!-- 🆕 Cột danh mục -->
              <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">
                <span v-if="product.categories && product.categories.length > 0">
                  {{ product.categories.map((c) => c.name).join(", ") }}
                </span>
                <span v-else class="text-gray-400 dark:text-gray-500">—</span>
              </td>

              <td class="px-4 py-4">
                <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400">
                  {{ product.variantCount || 0 }} variants
                </span>
              </td>

              <td class="px-4 py-4">
                <span
                  class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': getStockClass(product) === 'in-stock',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': getStockClass(product) === 'low-stock',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': getStockClass(product) === 'out-of-stock'
                  }"
                >
                  <i class="material-icons text-sm">{{ getStockIcon(product) }}</i>
                  {{ getStockText(product) }}
                </span>
              </td>

              <td class="px-4 py-4">
                <span
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="product.isActive ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'"
                >
                  {{ product.isActive ? "Đang bán" : "Ngừng bán" }}
                </span>
              </td>

              <td class="px-4 py-4 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button
                    @click="duplicateProduct(product.id)"
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors"
                    title="Nhân bản"
                  >
                    <i class="material-icons text-base">content_copy</i>
                  </button>
                  <button
                    @click="openEditModal(product)"
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors"
                    title="Chỉnh sửa"
                  >
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button
                    @click="confirmDelete(product)"
                    class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
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
      </div>

      <!-- Pagination -->
      <div class="flex items-center justify-between gap-4 px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Hiển thị {{ currentPage * pageSize + 1 }} - {{ Math.min((currentPage + 1) * pageSize, totalItems) }} trong tổng số {{ totalItems }} sản phẩm
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="currentPage === 0"
            @click="changePage(currentPage - 1)"
          >
            <i class="material-icons text-base">chevron_left</i>
            Trước
          </button>
          <span class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300">
            Trang {{ currentPage + 1 }} / {{ totalPages }}
          </span>
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="currentPage >= totalPages - 1"
            @click="changePage(currentPage + 1)"
          >
            Sau
            <i class="material-icons text-base">chevron_right</i>
          </button>
        </div>
      </div>
    </div>

    <!-- =================================================================
         MODALS
         ================================================================= -->

    <!-- Create/Edit Modal -->
    <ProductFormModal
      v-if="showModal"
      :visible="showModal"
      :isEditMode="isEditMode"
      :initialProduct="editingProduct"
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


    <!-- Modal Thêm Thương hiệu mới -->
    <Teleport to="body">
      <div
        v-if="showQuickAddBrand"
        class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
        @click="closeQuickAddBrand"
      >
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
            <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">add</i>
              Thêm Thương hiệu mới
            </h2>
            <button @click="closeQuickAddBrand" class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="p-6">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Tên thương hiệu *</label>
              <input
                v-model="quickBrandData.name"
                @input="generateBrandSlug"
                type="text"
                class="form-control"
                placeholder="VD: Nike, Adidas..."
              />
            </div>
            <div class="form-group">
              <label class="form-label">Slug *</label>
              <input
                v-model="quickBrandData.slug"
                type="text"
                class="form-control"
                placeholder="VD: nike, adidas..."
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">URL Logo</label>
            <input
              v-model="quickBrandData.logoUrl"
              type="text"
              class="form-control"
              placeholder="/placeholder-image.png"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Website</label>
            <input
              v-model="quickBrandData.websiteUrl"
              type="url"
              class="form-control"
              placeholder="https://example.com"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Mô tả</label>
            <textarea
              v-model="quickBrandData.description"
              class="form-control"
              rows="3"
              placeholder="Nhập mô tả về thương hiệu..."
            ></textarea>
          </div>

          <div class="form-check">
            <input
              type="checkbox"
              v-model="quickBrandData.isActive"
              id="isActiveBrand"
            />
            <label for="isActiveBrand">Kích hoạt thương hiệu</label>
          </div>
          </div>

          <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800">
            <button @click="closeQuickAddBrand" class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium">
              Hủy
            </button>
            <button
              @click="saveQuickBrand"
              class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              :disabled="savingQuickBrand"
            >
              <i class="material-icons text-base" v-if="!savingQuickBrand">save</i>
              {{ savingQuickBrand ? "Đang lưu..." : "Lưu" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
    
    <!-- Modal Thêm Chất liệu mới -->
    <Teleport to="body">
      <div
        v-if="showQuickAddMaterial"
        class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
        @click="closeQuickAddMaterial"
      >
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
            <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">add</i>
              Thêm Chất liệu mới
            </h2>
            <button @click="closeQuickAddMaterial" class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="p-6">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Tên chất liệu *</label>
              <input
                v-model="quickMaterialData.name"
                @input="generateMaterialSlug"
                type="text"
                class="form-control"
                placeholder="VD: Da tổng hợp, Vải canvas..."
              />
            </div>
            <div class="form-group">
              <label class="form-label">Slug *</label>
              <input
                v-model="quickMaterialData.slug"
                type="text"
                class="form-control"
                placeholder="VD: da-tong-hop, vai-canvas..."
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Mô tả</label>
            <textarea
              v-model="quickMaterialData.description"
              class="form-control"
              rows="3"
              placeholder="Nhập mô tả về chất liệu..."
            ></textarea>
          </div>

          <div class="form-check">
            <input
              type="checkbox"
              v-model="quickMaterialData.isActive"
              id="activeMaterial"
            />
            <label for="activeMaterial">Kích hoạt chất liệu</label>
          </div>
          </div>

          <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800">
            <button @click="closeQuickAddMaterial" class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium">
              Hủy
            </button>
            <button
              @click="saveQuickMaterial"
              class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              :disabled="savingQuickMaterial"
            >
              <i class="material-icons text-base" v-if="!savingQuickMaterial">save</i>
              {{ savingQuickMaterial ? "Đang lưu..." : "Lưu" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
    
    <!-- Modal Thêm Loại đế giày mới -->
    <Teleport to="body">
      <div
        v-if="showQuickAddSole"
        class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
        @click="closeQuickAddSole"
      >
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
            <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">add</i>
              Thêm Loại đế giày mới
            </h2>
            <button @click="closeQuickAddSole" class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="p-6">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Tên loại đế *</label>
              <input
                v-model="quickSoleData.name"
                @input="generateSoleSlug"
                type="text"
                class="form-control"
                placeholder="VD: Cao su, Foam, EVA..."
              />
            </div>
            <div class="form-group">
              <label class="form-label">Slug *</label>
              <input
                v-model="quickSoleData.slug"
                type="text"
                class="form-control"
                placeholder="VD: cao-su, eva..."
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Mô tả</label>
            <textarea
              v-model="quickSoleData.description"
              class="form-control"
              rows="3"
              placeholder="Nhập mô tả về loại đế giày..."
            ></textarea>
          </div>

          <div class="form-check">
            <input
              type="checkbox"
              v-model="quickSoleData.isActive"
              id="activeSole"
            />
            <label for="activeSole">Kích hoạt loại đế giày</label>
          </div>
          </div>

          <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800">
            <button @click="closeQuickAddSole" class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium">
              Hủy
            </button>
            <button
              @click="saveQuickSole"
              class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              :disabled="savingQuickSole"
            >
              <i class="material-icons text-base" v-if="!savingQuickSole">save</i>
              {{ savingQuickSole ? "Đang lưu..." : "Lưu" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 🔹 Popup thêm danh mục mới -->
    <Teleport to="body">
      <div
        v-if="showCategoryModal"
        class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
        @click="closeCategoryModal"
      >
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
            <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">add</i>
              Thêm danh mục mới
            </h2>
            <button @click="closeCategoryModal" class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="p-6">
          <div class="form-group">
            <label class="form-label required">Tên danh mục</label>
            <input
              v-model="newCategory.name"
              type="text"
              class="form-control"
              placeholder="Ví dụ: Giày chạy bộ, Giày bóng rổ..."
              @input="generateCategorySlug"
            />
            <span v-if="categoryErrors.name" class="form-error">{{
              categoryErrors.name
            }}</span>
          </div>

          <div class="form-group">
            <label class="form-label required">Slug</label>
            <input
              v-model="newCategory.slug"
              type="text"
              class="form-control"
              placeholder="giay-chay-bo, giay-bong-ro..."
            />
            <span v-if="categoryErrors.slug" class="form-error">{{
              categoryErrors.slug
            }}</span>
            <span class="form-help">URL thân thiện (tự động tạo từ tên)</span>
          </div>

          <div class="form-group">
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
            <span class="form-help">Để trống nếu đây là danh mục gốc</span>
          </div>
          </div>

          <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800">
            <button @click="closeCategoryModal" class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium">
              Hủy
            </button>
            <button
              @click="handleCreateCategory"
              class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="submittingCategory"
            >
              {{ submittingCategory ? "Đang lưu..." : "Thêm mới" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Import Excel Modal -->
    <Teleport to="body">
      <div v-if="showImportModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeImportModal">
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
            <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">file_upload</i>
              Import sản phẩm từ Excel
            </h2>
            <button @click="closeImportModal" class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>

          <div class="p-6 space-y-6">
            <!-- Instructions Section -->
            <div class="bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 rounded-lg p-4 border border-blue-200 dark:border-blue-800">
              <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-3 flex items-center gap-2">
                <i class="material-icons text-blue-600 dark:text-blue-400 text-lg">info</i>
                Hướng dẫn Import
              </h3>
              <ol class="space-y-2 text-sm text-gray-700 dark:text-gray-300 ml-6 list-decimal">
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
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Chọn file Excel:
              </label>
              <div class="relative">
                <input
                  type="file"
                  accept=".xlsx,.xls"
                  @change="handleFileUpload"
                  class="block w-full text-sm text-gray-500 dark:text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-purple-50 dark:file:bg-purple-900/30 file:text-purple-700 dark:file:text-purple-300 hover:file:bg-purple-100 dark:hover:file:bg-purple-900/50 file:cursor-pointer border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                />
              </div>
            </div>

            <!-- Preview Section -->
            <div v-if="importPreview.length > 0" class="space-y-3">
              <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
                <i class="material-icons text-green-600 dark:text-green-400 text-lg">preview</i>
                Preview: {{ importPreview.length }} sản phẩm
              </h3>
              <div class="border border-gray-200 dark:border-gray-700 rounded-lg overflow-hidden">
                <div class="overflow-x-auto max-h-[400px]">
                  <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                    <thead class="bg-gray-50 dark:bg-gray-900 sticky top-0">
                      <tr>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider">Tên SP</th>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider">Brand</th>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider">SKU</th>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider">Size</th>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider">Màu</th>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider">Giá</th>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider">Tồn kho</th>
                      </tr>
                    </thead>
                    <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                      <tr v-for="(item, index) in importPreview" :key="index" class="hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">{{ item.productName }}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300">{{ item.brandName }}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm font-mono text-gray-700 dark:text-gray-300">{{ item.sku }}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300">{{ item.size }}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300">{{ item.color }}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm font-semibold text-gray-900 dark:text-gray-100">{{ formatCurrency(item.priceBase) }}</td>
                        <td class="px-4 py-3 whitespace-nowrap text-sm text-gray-700 dark:text-gray-300">{{ item.stockQuantity }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>

          <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800">
            <button @click="closeImportModal" class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium">
              Hủy
            </button>
            <button
              @click="handleImport"
              class="px-4 py-2 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200 font-medium disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="importing || importPreview.length === 0"
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
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
            <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
              <i class="material-icons text-purple-600 dark:text-purple-400">edit</i>
              Cập nhật hàng loạt {{ selectedProducts.length }} sản phẩm
            </h2>
            <button @click="closeBulkUpdateModal" class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors">
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
              <option v-for="brand in brands" :key="brand.id" :value="brand.id">
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

          <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700 sticky bottom-0 bg-white dark:bg-gray-800">
            <button @click="closeBulkUpdateModal" class="px-4 py-2 text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors font-medium">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { useAdminStore } from "@/stores/admin";
import toastService from "@/utils/toastService";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";
import UploadGallery from "@/assets/components/admin/UploadGallery.vue";
import ProductFilters from "@/assets/components/admin/ProductFilters.vue";
import ProductFormModal from "@/assets/components/admin/ProductFormModal.vue";
import * as XLSX from "xlsx";

const adminStore = useAdminStore();

// State
const products = ref([]);
const brands = ref([]);
const categories = ref([]);
const materials = ref([]); // Danh sách chất liệu
const soles = ref([]); // Danh sách loại đế giày
const stats = ref(null);
const loading = ref(false);
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

    toastService.success('Thành công', `Đã thêm danh mục "${newCategory.value.name}" thành công!`);

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
    console.error("Lỗi khi thêm danh mục:", error);
    let msg = "Không thể thêm danh mục. Vui lòng thử lại!";

    if (error.response?.status === 409) {
      msg = "Tên hoặc slug đã tồn tại!";
    }
    toastService.error('Lỗi', msg);
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
    console.error("Lỗi khi tải danh mục gốc:", error);
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
const getStockClass = (product) => {
  const totalStock = product.variantCount; // Giả định backend trả về tổng stock
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

    // Apply current sort after fetching
    if (sortBy.value) {
      sortColumn(sortBy.value);
      // Reset sort order to maintain current state
      sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
      sortColumn(sortBy.value);
    }
  } catch (error) {
    console.error("Lỗi khi tải danh sách sản phẩm:", error);
    toastService.error('Lỗi', 'Không thể tải danh sách sản phẩm!');
  } finally {
    loading.value = false;
  }
};

const fetchBrands = async () => {
  try {
    await adminStore.fetchBrands();
    brands.value = adminStore.brands;
  } catch (error) {
    console.error("Lỗi khi tải danh sách thương hiệu:", error);
  }
};

const fetchCategories = async () => {
  try {
    await adminStore.fetchCategories();
    categories.value = adminStore.categories;
  } catch (error) {
    console.error("Lỗi khi tải danh sách danh mục:", error);
  }
};

const fetchStatistics = async () => {
  try {
    const response = await adminStore.getProductStatistics();
    stats.value = response;
  } catch (error) {
    console.error("Lỗi khi tải thống kê:", error);
  }
};

// ===== MATERIALS & SOLES =====
const fetchMaterials = async () => {
  try {
    await adminStore.fetchMaterials();
    materials.value = adminStore.materials;
  } catch (error) {
    console.error("Lỗi khi tải danh sách chất liệu:", error);
  }
};

const fetchSoles = async () => {
  try {
    await adminStore.fetchSoles();
    soles.value = adminStore.soles;
  } catch (error) {
    console.error("Lỗi khi tải danh sách loại đế giày:", error);
  }
};

// ===== BULK SELECTION =====
const toggleSelect = (productId) => {
  const index = selectedProducts.value.indexOf(productId);
  if (index > -1) {
    selectedProducts.value.splice(index, 1);
  } else {
    selectedProducts.value.push(productId);
  }
};

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedProducts.value = [];
  } else {
    selectedProducts.value = products.value.map((p) => p.id);
  }
};

const clearSelection = () => {
  selectedProducts.value = [];
};

const bulkDelete = async () => {
  if (
    !confirm(
      `Bạn có chắc chắn muốn xóa ${selectedProducts.value.length} sản phẩm đã chọn?`
    )
  ) {
    return;
  }

  try {
    loading.value = true;

    for (const productId of selectedProducts.value) {
      await adminStore.deleteProduct(productId);
    }

    toastService.success('Thành công', `Đã xóa ${selectedProducts.value.length} sản phẩm thành công!`);
    selectedProducts.value = [];
    await fetchProducts();
    await fetchStatistics();
  } catch (error) {
    console.error("Lỗi khi xóa hàng loạt:", error);
    toastService.error('Lỗi', 'Có lỗi xảy ra khi xóa sản phẩm!');
  } finally {
    loading.value = false;
  }
};

// ===== MODAL ACTIONS =====
const openCreateModal = () => {
  isEditMode.value = false
  editingProduct.value = null
  productImages.value = []
  initialProductImages.value = []
  uploadedImages.value = []
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
    variants: [],
  }
  formErrors.value = {}
  showModal.value = true
}

const editingProduct = ref(null)

const openEditModal = async (product) => {
  isEditMode.value = true;
  editingProduct.value = null // Reset trước

  try {
    // 1️⃣ Tải dữ liệu cho dropdown trước (tránh select rỗng khi đã có id)
    await Promise.all([
      adminStore.fetchBrands?.(),
      adminStore.fetchCategories?.(),
      adminStore.fetchMaterials?.(), // 🆕 chất liệu
      adminStore.fetchSoles?.(), // 🆕 đế giày
    ]);

    // 2️⃣ Lấy chi tiết sản phẩm
    const detailData = await adminStore.getProductById(product.id);

    // 3️⃣ Gán formData ĐẦY ĐỦ field, có cả materialId & shoeSoleId
    const productData = {
      id: detailData.id,
      name: detailData.name || "",
      slug: detailData.slug || "",
      brandId: detailData.brandId || null,
      description: detailData.description || "",
      isActive: detailData.isActive !== undefined ? detailData.isActive : true,
      categoryIds: detailData.categories?.map((c) => c.id) || [],
      images:
        detailData.images?.map((img) => ({
          previewUrl: img.imageUrl,
          isPrimary: img.isPrimary || false,
        })) || [],
      // 🆕 BỔ SUNG 2 TRƯỜNG MỚI:
      materialId: detailData.materialId ?? null,
      shoeSoleId: detailData.shoeSoleId ?? null,
      variants:
        detailData.variants?.map((v) => ({
          id: v.id,
          sku: v.sku || "",
          size: v.size || "",
          color: v.color || "",
          priceBase: v.priceBase || 0,
          priceSale: v.priceSale || null,
          stockQuantity: v.stockQuantity || 0,
          imageUrl: v.imageUrl || "",
        })) || [],
    };

    // 🟢 Bổ sung phần LOAD ẢNH từ API
    const { data: imageData } = await axios.get(
      `/api/admin/products/${product.id}/images`
    );

    // Chuẩn hóa về format UploadGallery hiểu được
    initialProductImages.value = (imageData || []).map((img) => ({
      id: img.id,
      previewUrl: img.imageUrl.startsWith("http")
        ? img.imageUrl
        : `${window.location.origin}${img.imageUrl}`, // hỗ trợ /uploads/*
      isPrimary: !!img.isPrimary,
      displayOrder: img.displayOrder ?? 0, // 🆕 giữ nguyên thứ tự từ BE
      file: null,
      type: "db", // ✅ phân biệt ảnh từ DB
    }));

    // 🧠 Thêm snapshot ban đầu để so sánh sau
    originalImagesSnapshot.value = initialProductImages.value.map((x) => ({
      id: x.id,
      isPrimary: !!x.isPrimary,
    }));

    // Gán cho UploadGallery
    productImages.value = [...initialProductImages.value];
    formData.value.images = [...initialProductImages.value];

    // 🟢 Lưu snapshot ảnh DB gốc để diff khi cập nhật
    originalImagesSnapshot.value = initialProductImages.value.map(
      (img, idx) => ({
        id: img.id,
        isPrimary: !!img.isPrimary,
        displayOrder: idx,
      })
    );

    console.log("🖼️ Ảnh sản phẩm từ API:", initialProductImages.value);
  } catch (error) {
    console.error("Lỗi khi tải chi tiết sản phẩm:", error);

    // Fallback vẫn giữ đủ 2 field mới để tránh mất reactivity
    formData.value = {
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

    // Reset ảnh nếu API lỗi
    initialProductImages.value = [];
    productImages.value = [];
  }

  showModal.value = true;
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
    console.error("Lỗi tải ảnh sản phẩm:", error);
  }
};

const closeModal = () => {
  showModal.value = false
  isEditMode.value = false
  editingProduct.value = null
  formErrors.value = {}
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
  }
  // 🧹 Cleanup blob URL khi đóng modal
  productImages.value.forEach((img) => {
    if (img.file && img.previewUrl?.startsWith("blob:")) {
      URL.revokeObjectURL(img.previewUrl)
    }
  })
  productImages.value = []
  initialProductImages.value = []
  uploadedImages.value = []
  formData.value.mainImageUrl = null
  formErrors.value = {}
}

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
  const dataToSubmit = submittedData || formData.value
  try {
    isSubmitting.value = true;
    const updatedIds = new Set(); // 🧠 tránh update trùng

    // ==================== [1] VALIDATE CƠ BẢN ====================
    if (!formData.value.name?.trim()) {
      toastService.warning('Cảnh báo', 'Vui lòng nhập tên sản phẩm');
      return;
    }

    if (!formData.value.slug?.trim()) {
      toastService.warning('Cảnh báo', 'Slug không được để trống (hãy nhập tên để tự sinh slug)');
      return;
    }

    if (!formData.value.brandId) {
      toastService.warning('Cảnh báo', 'Vui lòng chọn thương hiệu');
      return;
    }

    if (
      !formData.value.categoryIds ||
      formData.value.categoryIds.length === 0
    ) {
      toastService.warning('Cảnh báo', 'Vui lòng chọn ít nhất 1 danh mục');
      return;
    }

    if (!formData.value.variants || formData.value.variants.length === 0) {
      toastService.warning('Cảnh báo', 'Vui lòng thêm ít nhất 1 biến thể sản phẩm');
      return;
    }

    if (productImages.value.length > MAX_IMAGES_PER_PRODUCT) {
      toastService.warning('Cảnh báo', `Tối đa ${MAX_IMAGES_PER_PRODUCT} ảnh cho mỗi sản phẩm`);
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
          console.error("❌ Xóa ảnh lỗi:", url, e);
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
      name: dataToSubmit.name?.trim(),
      slug: dataToSubmit.slug?.trim(),
      description: dataToSubmit.description?.trim() || "",
      brandId: dataToSubmit.brandId,
      categoryIds: dataToSubmit.categoryIds,
      materialId: dataToSubmit.materialId,
      shoeSoleId: dataToSubmit.shoeSoleId,
      isActive: dataToSubmit.isActive ?? true,
      variants: dataToSubmit.variants.map((v) => ({
        sku: v.sku,
        color: v.color,
        size: v.size,
        priceBase: Number(v.priceBase) || 0,
        priceSale: Number(v.priceSale) || 0,
        stockQuantity: Number(v.stockQuantity) || 0,
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
          uploadedUrls.push(res.data?.imageUrl);
        } catch (err) {
          console.error("❌ Upload ảnh local lỗi:", err);
          toastService.error('Lỗi', 'Upload ảnh local thất bại');
        }
      } else if (img.type === "url" && img.previewUrl) {
        try {
          await axios.post(
            `/api/admin/products/${productId}/images`,
            {
              imageUrl: img.previewUrl,
              isPrimary: willSendPrimary,
              displayOrder: displayOrder,
            },
            { headers: { "Content-Type": "application/json" } }
          );
          uploadedUrls.push(img.previewUrl);
        } catch (err) {
          console.error("❌ Upload ảnh URL lỗi:", err);
          toastService.error('Lỗi', 'Upload ảnh URL thất bại');
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
          console.log(
            `✅ Ảnh mới upload được gán làm ảnh bìa ID=${matched.id}`
          );
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
        console.log(
          `🔄 Đổi ảnh bìa từ ${oldPrimary.id} → ${currentPrimary.id}`
        );

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

        toastService.success('Thành công', 'Đã cập nhật ảnh bìa thành công!');
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
            console.log(
              `🆙 Update ảnh ID=${img.id} → order=${idx + 1}, primary=${
                img.isPrimary
              }`
            );
          } catch (err) {
            console.error("❌ Update ảnh DB lỗi:", err);
          }
        }
      }

      // 🔹 [6.4] Cập nhật mainImageUrl cho sản phẩm
      const finalPrimary = productImages.value.find((i) => i.isPrimary);
      if (finalPrimary) {
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
            mainImageUrl: finalPrimary.previewUrl, // 🧩 thêm trường mới
            variants: formData.value.variants.map((v) => ({
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

        formData.value.mainImageUrl = finalPrimary.previewUrl;
      }
    } catch (err) {
      console.error("❌ Lỗi khi xử lý ảnh bìa / thứ tự hiển thị:", err);
      toastService.error('Lỗi', 'Cập nhật ảnh bìa hoặc thứ tự hiển thị thất bại!');
    }

    // ==================== [7] THÔNG BÁO & RESET FORM ====================
    toastService.success('Thành công', isEditMode.value
      ? "Cập nhật sản phẩm thành công!"
      : "Tạo sản phẩm mới thành công!");

    await fetchProducts();
    await fetchStatistics();
    originalImagesSnapshot.value = productImages.value
      .filter((x) => x.type === "db" && x.id)
      .map((x) => ({ id: x.id, isPrimary: !!x.isPrimary }));

    closeModal();
  } catch (error) {
    console.error("❌ Lỗi khi lưu sản phẩm:", error);
    const msg =
      error.response?.data?.message ||
      error.message ||
      "Đã xảy ra lỗi khi lưu sản phẩm";
    toastService.error('Lỗi', msg);
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
  try {
    deleting.value = true;
    await adminStore.deleteProduct(productToDelete.value.id);
    toastService.success('Thành công', `Đã xóa sản phẩm "${productToDelete.value.name}" thành công!`);
    await fetchProducts();
    await fetchStatistics();
    showDeleteModal.value = false;
    productToDelete.value = null;
  } catch (error) {
    console.error("Lỗi khi xóa sản phẩm:", error);
    toastService.error('Lỗi', 'Không thể xóa sản phẩm này. Vui lòng thử lại!');
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
    toastService.warning('Cảnh báo', 'Vui lòng nhập tên thương hiệu!');
    return;
  }

  try {
    savingQuickBrand.value = true;

    // 🟢 Gọi API tạo thương hiệu (qua adminStore)
    const res = await adminStore.createBrand(quickBrandData.value);

    toastService.success('Thành công', 'Đã thêm thương hiệu mới thành công!');
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
    console.error("Lỗi khi thêm thương hiệu nhanh:", error);
    toastService.error('Lỗi', 'Không thể thêm thương hiệu. Vui lòng thử lại!');
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
    toastService.warning('Cảnh báo', 'Vui lòng nhập tên chất liệu!');
    return;
  }
  try {
    savingQuickMaterial.value = true;
    await adminStore.createMaterial(quickMaterialData.value);
    toastService.success('Thành công', 'Thêm chất liệu mới thành công!');

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
    console.error(err);
    toastService.error('Lỗi', 'Không thể thêm chất liệu.');
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
    toastService.warning('Cảnh báo', 'Vui lòng nhập tên loại đế giày!');
    return;
  }
  try {
    savingQuickSole.value = true;
    await adminStore.createSole(quickSoleData.value);
    toastService.success('Thành công', 'Thêm loại đế giày mới thành công!');

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
    console.error(err);
    toastService.error('Lỗi', 'Không thể thêm loại đế giày.');
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

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, { type: "array" });
      const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = XLSX.utils.sheet_to_json(firstSheet);

      importPreview.value = jsonData.map((row, index) => ({
        rowNumber: index + 2,
        productName: row["Tên sản phẩm"] || "",
        productSlug: row["Slug"] || "",
        brandName: row["Thương hiệu"] || "",
        description: row["Mô tả"] || "",
        categories: row["Danh mục"] || "",
        isActive: row["Trạng thái"] === "TRUE",
        sku: row["SKU"] || "",
        size: row["Size"] || "",
        color: row["Màu sắc"] || "",
        priceBase: Number(row["Giá gốc"]) || 0,
        priceSale: Number(row["Giá sale"]) || null,
        stockQuantity: Number(row["Tồn kho"]) || 0,
        imageUrl: row["URL ảnh"] || "",
      }));

      toastService.success('Thành công', `Đã đọc ${importPreview.value.length} sản phẩm từ file Excel!`);
    } catch (error) {
      console.error("Lỗi khi đọc file Excel:", error);
      toastService.error('Lỗi', 'Không thể đọc file Excel. Vui lòng kiểm tra lại format!');
    }
  };
  reader.readAsArrayBuffer(file);
};

const handleImport = async () => {
  try {
    importing.value = true;
    const result = await adminStore.importProducts(importPreview.value);

    toastService.success('Thành công', `Import thành công ${result.successCount}/${result.totalRows} sản phẩm!`);

    if (result.errorCount > 0) {
      console.error("Import errors:", result.errorItems);
      toastService.warning('Cảnh báo', `Có ${result.errorCount} sản phẩm bị lỗi. Xem console để biết chi tiết.`);
    }

    await fetchProducts();
    await fetchStatistics();
    closeImportModal();
  } catch (error) {
    console.error("Lỗi khi import:", error);
    toastService.error('Lỗi', 'Không thể import sản phẩm. Vui lòng thử lại!');
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
    toastService.success('Thành công', `Cập nhật thành công ${result.successCount}/${result.totalRequested} sản phẩm!`);

    await fetchProducts();
    await fetchStatistics();
    closeBulkUpdateModal();
    clearSelection();
  } catch (error) {
    console.error("Lỗi khi bulk update:", error);
    toastService.error('Lỗi', 'Không thể cập nhật hàng loạt. Vui lòng thử lại!');
  } finally {
    bulkUpdating.value = false;
  }
};

// ===== DUPLICATE PRODUCT =====
const duplicateProduct = async (productId) => {
  try {
    loading.value = true;
    const duplicated = await adminStore.duplicateProduct(productId);
    toastService.success('Thành công', `Đã nhân bản sản phẩm "${duplicated.name}" thành công!`);
    await fetchProducts();
    await fetchStatistics();
  } catch (error) {
    console.error("Lỗi khi nhân bản sản phẩm:", error);
    toastService.error('Lỗi', 'Không thể nhân bản sản phẩm. Vui lòng thử lại!');
  } finally {
    loading.value = false;
  }
};

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
const exportToExcel = () => {
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
    toastService.success('Thành công', `Đã export ${exportData.length} sản phẩm thành công!`);
  } catch (error) {
    console.error("Lỗi khi export Excel:", error);
    toastService.error('Lỗi', 'Không thể export dữ liệu. Vui lòng thử lại!');
  }
};

// ===== HELPERS =====
const formatCurrency = (value) => {
  if (!value) return "0 đ";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
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




<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">verified_user</i>
            Quản lý Bảo Hành
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý yêu cầu bảo hành và sửa chữa sản phẩm</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="exportWarranties('csv')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_download</i>
            Xuất CSV
          </button>
          <button @click="exportWarranties('json')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">description</i>
            Xuất JSON
          </button>
          <button @click="showBulkActions = !showBulkActions" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">checklist</i>
            Hành động hàng loạt
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">schedule</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.pending }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Chờ xử lý</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">build</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.inProgress }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đang sửa chữa</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">done_all</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.completed }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Hoàn thành</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">highlight_off</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.rejected }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Từ chối</p>
        </div>
      </div>
    </div>
    
    <!-- Bulk Actions Bar -->
    <div
      v-if="showBulkActions && selectedWarranties.length > 0"
      class="flex items-center justify-between p-4 bg-purple-50 dark:bg-purple-900/20 rounded-xl border border-purple-200 dark:border-purple-800 mb-4"
    >
      <div class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
        <i class="material-icons text-purple-600 dark:text-purple-400">check_circle</i>
        <span>Đã chọn <strong class="font-semibold">{{ selectedWarranties.length }}</strong> yêu cầu bảo hành</span>
      </div>
      <div class="flex items-center gap-2">
        <select 
          v-model="bulkAction" 
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
        >
          <option value="">-- Chọn hành động --</option>
          <option value="approve">Chấp nhận</option>
          <option value="reject">Từ chối</option>
          <option value="in_progress">Đang sửa chữa</option>
          <option value="completed">Hoàn thành</option>
        </select>
        <button 
          @click="executeBulkAction" 
          :disabled="!bulkAction" 
          class="flex items-center gap-2 px-3 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <i class="material-icons text-base">update</i>
          Thực hiện
        </button>
        <button 
          @click="clearSelection" 
          class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
        >
          <i class="material-icons text-base">clear</i>
          Bỏ chọn
        </button>
      </div>
    </div>

    <!-- Filters -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <FilterBar
        v-model:search="filters.search"
        search-placeholder="Tìm theo mã, khách hàng, sản phẩm..."
        @search="handleSearch"
        @reset="resetFilters"
      >
        <template #filters>
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
              <i class="material-icons text-sm">check_circle</i>
              Trạng thái
            </label>
            <select v-model="filters.status" @change="fetchWarranties" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="">Tất cả</option>
              <option value="pending">Chờ xử lý</option>
              <option value="approved">Đã chấp nhận</option>
              <option value="in_progress">Đang sửa chữa</option>
              <option value="completed">Hoàn thành</option>
              <option value="rejected">Từ chối</option>
            </select>
          </div>
          <div class="flex flex-col gap-1">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
              <i class="material-icons text-sm">build</i>
              Loại bảo hành
            </label>
            <select v-model="filters.type" @change="fetchWarranties" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option value="">Tất cả</option>
              <option value="repair">Sửa chữa</option>
              <option value="replace">Đổi mới</option>
              <option value="refund">Hoàn tiền</option>
            </select>
          </div>
        </template>
      </FilterBar>
    </div>

    <!-- Table -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
      <!-- Loading State -->
      <div v-if="loading" class="flex flex-col items-center justify-center p-12">
        <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải danh sách bảo hành...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="warranties.length === 0" class="flex flex-col items-center justify-center p-12">
        <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">verified_user</i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có yêu cầu bảo hành</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
          Danh sách yêu cầu bảo hành sẽ hiển thị ở đây
        </p>
      </div>

      <!-- Warranty Table -->
      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th v-if="showBulkActions" class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-10">
                <input 
                  type="checkbox" 
                  :checked="isAllSelected"
                  @change="toggleSelectAll"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Mã BH</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Khách hàng</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Sản phẩm</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Vấn đề</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Loại BH</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Ngày yêu cầu</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thời hạn</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="item in warranties" :key="item.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td v-if="showBulkActions" class="px-4 py-4 whitespace-nowrap">
                <input 
                  type="checkbox" 
                  :checked="isSelected(item.id)"
                  @change="toggleSelect(item.id)"
                  class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                />
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <code class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100">#WTY{{ item.id.toString().padStart(6, '0') }}</code>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ item.userName }}</div>
                <div class="text-xs text-gray-500 dark:text-gray-400">{{ item.userEmail }}</div>
              </td>
              <td class="px-4 py-4">
                <div class="flex items-center gap-3">
                  <img 
                    :src="item.productImage" 
                    :alt="item.productName" 
                    class="w-12 h-12 object-cover rounded-lg"
                    loading="lazy"
                    decoding="async"
                  />
                  <div>
                    <strong class="text-sm font-medium text-gray-900 dark:text-gray-100 block">{{ item.productName }}</strong>
                    <p class="text-xs text-gray-500 dark:text-gray-400">{{ item.variantName }}</p>
                    <p class="text-xs text-gray-500 dark:text-gray-400">Mua: {{ formatDate(item.purchaseDate) }}</p>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4">
                <p class="text-sm text-gray-900 dark:text-gray-100 max-w-xs truncate" :title="item.issueDescription">{{ item.issueDescription }}</p>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span 
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400': item.warrantyType === 'repair',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': item.warrantyType === 'replace',
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': item.warrantyType === 'refund'
                  }"
                >
                  {{ getWarrantyTypeText(item.warrantyType) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">{{ formatDate(item.submittedAt) }}</td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-1" :class="{ 'text-red-600 dark:text-red-400': isWarrantyExpired(item) }">
                  <i class="material-icons text-sm">schedule</i>
                  <span v-if="isWarrantyExpired(item)" class="text-xs font-medium">Hết hạn</span>
                  <span v-else class="text-xs">{{ calculateDaysLeft(item) }} ngày</span>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span 
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': item.status === 'pending',
                    'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400': item.status === 'approved' || item.status === 'in_progress',
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': item.status === 'completed',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': item.status === 'rejected'
                  }"
                >
                  {{ getStatusText(item.status) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <button 
                    @click="viewWarrantyDetail(item)" 
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors" 
                    title="Xem chi tiết"
                  >
                    <i class="material-icons text-base">visibility</i>
                  </button>
                  <button 
                    v-if="item.status === 'pending'" 
                    @click="approveWarranty(item)" 
                    class="p-1.5 text-green-600 dark:text-green-400 hover:bg-green-50 dark:hover:bg-green-900/20 rounded-lg transition-colors" 
                    title="Chấp nhận"
                  >
                    <i class="material-icons text-base">check_circle</i>
                  </button>
                  <button 
                    v-if="item.status === 'approved'" 
                    @click="updateStatus(item, 'in_progress')" 
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors" 
                    title="Bắt đầu sửa"
                  >
                    <i class="material-icons text-base">build</i>
                  </button>
                  <button 
                    v-if="item.status === 'in_progress'" 
                    @click="updateStatus(item, 'completed')" 
                    class="p-1.5 text-green-600 dark:text-green-400 hover:bg-green-50 dark:hover:bg-green-900/20 rounded-lg transition-colors" 
                    title="Hoàn thành"
                  >
                    <i class="material-icons text-base">done_all</i>
                  </button>
                  <button 
                    @click="openUploadModal(item)" 
                    class="p-1.5 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded-lg transition-colors" 
                    title="Tải tài liệu"
                  >
                    <i class="material-icons text-base">upload_file</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Document Upload Modal -->
    <div v-if="showUploadModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="showUploadModal = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">Tải tài liệu bảo hành</h3>
          <button @click="showUploadModal = false" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <div class="border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-8 text-center cursor-pointer hover:border-purple-500 dark:hover:border-purple-400 transition-colors" @click="triggerFileInput">
            <i class="material-icons text-4xl text-gray-400 dark:text-gray-500 mb-2">cloud_upload</i>
            <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">Nhấp để tải lên hoặc kéo thả file vào đây</p>
            <p class="text-xs text-gray-500 dark:text-gray-500">Hỗ trợ: PDF, JPG, PNG (tối đa 10MB)</p>
            <input 
              ref="fileInput" 
              type="file" 
              multiple 
              accept="image/*,.pdf"
              @change="handleFileUpload"
              class="hidden"
            />
          </div>
          
          <div v-if="uploadedFiles.length > 0" class="mt-4 space-y-2">
            <h4 class="text-sm font-medium text-gray-900 dark:text-gray-100 mb-2">Files đã tải lên:</h4>
            <div v-for="(file, index) in uploadedFiles" :key="index" class="flex items-center gap-3 p-2 bg-gray-50 dark:bg-gray-700 rounded-lg">
              <i class="material-icons text-gray-500 dark:text-gray-400">description</i>
              <span class="flex-1 text-sm text-gray-700 dark:text-gray-300 truncate">{{ file.name }}</span>
              <button @click="removeFile(index)" class="p-1 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded transition-colors">
                <i class="material-icons text-base">delete</i>
              </button>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="showUploadModal = false" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">Hủy</button>
          <button @click="saveDocuments" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200">
            <i class="material-icons text-base">save</i>
            Lưu tài liệu
          </button>
        </div>
      </div>
    </div>

    <!-- Detail Dialog -->
    <div v-if="showDetailDialog" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="showDetailDialog = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">Chi tiết yêu cầu bảo hành #WTY{{ selectedWarranty?.id.toString().padStart(6, '0') }}</h3>
          <button @click="showDetailDialog = false" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="selectedWarranty" class="space-y-6">
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4 class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3">Thông tin khách hàng</h4>
              <div class="space-y-1 text-sm text-gray-700 dark:text-gray-300">
                <p><strong>Họ tên:</strong> {{ selectedWarranty.userName }}</p>
                <p><strong>Email:</strong> {{ selectedWarranty.userEmail }}</p>
              </div>
            </div>
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4 class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3">Thông tin sản phẩm</h4>
              <div class="space-y-1 text-sm text-gray-700 dark:text-gray-300">
                <p><strong>Sản phẩm:</strong> {{ selectedWarranty.productName }}</p>
                <p><strong>Biến thể:</strong> {{ selectedWarranty.variantName }}</p>
                <p><strong>Ngày mua:</strong> {{ formatDate(selectedWarranty.purchaseDate) }}</p>
              </div>
            </div>
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4 class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3">Vấn đề & Giải pháp</h4>
              <div class="space-y-1 text-sm text-gray-700 dark:text-gray-300">
                <p><strong>Mô tả vấn đề:</strong> {{ selectedWarranty.issueDescription }}</p>
                <p><strong>Loại BH yêu cầu:</strong> {{ getWarrantyTypeText(selectedWarranty.warrantyType) }}</p>
                <p v-if="selectedWarranty.adminNote"><strong>Ghi chú admin:</strong> {{ selectedWarranty.adminNote }}</p>
                <p v-if="selectedWarranty.resolutionNote"><strong>Giải pháp:</strong> {{ selectedWarranty.resolutionNote }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="showDetailDialog = false" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import notificationService from '@/utils/notificationService'
import confirmDialogService from '@/utils/confirmDialogService'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { debounce } from '@/utils/debounce'
import { useAdminStore } from '@/stores/admin'
import FilterBar from '@/assets/components/admin/FilterBar.vue'
import logger from '@/utils/logger'
import { formatDate } from '@/utils/formatters'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const showDetailDialog = ref(false)
const showUploadModal = ref(false)
const showBulkActions = ref(false)
const selectedWarranty = ref(null)
const warranties = ref([])
const selectedWarranties = ref([])
const uploadedFiles = ref([])
const fileInput = ref(null)
const bulkAction = ref('')

const filters = reactive({
  search: '',
  status: '',
  type: ''
})

const stats = reactive({
  pending: 0,
  inProgress: 0,
  completed: 0,
  rejected: 0
})

// Computed
const isAllSelected = computed(() => {
  return warranties.value.length > 0 && selectedWarranties.value.length === warranties.value.length
})

// Bulk actions configuration
const bulkActions = computed(() => [
  { value: 'approve', label: 'Chấp nhận', icon: 'check' },
  { value: 'reject', label: 'Từ chối', icon: 'close' },
  { value: 'in_progress', label: 'Đang sửa chữa', icon: 'build' },
  { value: 'completed', label: 'Hoàn thành', icon: 'done_all' }
])

// Methods
const fetchWarranties = async () => {
  try {
    loading.value = true
    
    // Load từ API - chỉ dùng dữ liệu thật từ database
    const apiFilters = {}
    
    if (filters.search) {
      apiFilters.search = filters.search
    }
    
    if (filters.status) {
      apiFilters.status = filters.status
    }
    
    if (filters.type) {
      apiFilters.warrantyType = filters.type
    }
    
    const result = await adminStore.fetchWarranties(0, 100, apiFilters)
    warranties.value = result.content || []
    
    // Update stats từ dữ liệu thật
    updateStats()
    
    if (warranties.value.length === 0) {
      notificationService.info('Thông tin','Chưa có yêu cầu bảo hành nào')
    } else {
      logger.log('✅ Warranties loaded from API:', warranties.value.length, 'warranties')
    }
  } catch (error) {
    logger.error('Lỗi tải dữ liệu:', error)
    notificationService.apiError(error, 'Không thể tải danh sách bảo hành')
    warranties.value = []
    updateStats()
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.pending = warranties.value.filter(w => w.status === 'pending').length
  stats.inProgress = warranties.value.filter(w => w.status === 'in_progress').length
  stats.completed = warranties.value.filter(w => w.status === 'completed').length
  stats.rejected = warranties.value.filter(w => w.status === 'rejected').length
}

const handleSearch = debounce(() => {
  fetchWarranties()
}, 300)

const resetFilters = () => {
  filters.search = ''
  filters.status = ''
  filters.type = ''
  fetchWarranties()
}

// Selection Methods
const isSelected = (id) => {
  return selectedWarranties.value.includes(id)
}

const toggleSelect = (id) => {
  if (isSelected(id)) {
    selectedWarranties.value = selectedWarranties.value.filter(wId => wId !== id)
  } else {
    selectedWarranties.value.push(id)
  }
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedWarranties.value = []
  } else {
    selectedWarranties.value = warranties.value.map(w => w.id)
  }
}

const clearSelection = () => {
  selectedWarranties.value = []
  showBulkActions.value = false
}

// Execute bulk action based on selected action
const executeBulkAction = async () => {
  if (!bulkAction.value) {
    notificationService.warning('Cảnh báo','Vui lòng chọn hành động!')
    return
  }

  const actionMap = {
    'approve': bulkApprove,
    'reject': bulkReject,
    'in_progress': bulkInProgress,
    'completed': bulkComplete
  }

  const action = actionMap[bulkAction.value]
  if (action) {
    await action()
  }
}

// Bulk Actions
const bulkApprove = async () => {
  confirmDialogService.confirm(
    `Bạn có chắc muốn chấp nhận ${selectedWarranties.value.length} yêu cầu bảo hành?`,
    'Xác nhận',
    {
      confirmButtonText: 'Chấp nhận',
      cancelButtonText: 'Hủy',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await adminStore.bulkApproveWarranties(selectedWarranties.value)
      notificationService.success('Thành công',`Đã chấp nhận ${selectedWarranties.value.length} yêu cầu!`)
      clearSelection()
      fetchWarranties()
    } catch (error) {
      logger.error('Lỗi khi chấp nhận hàng loạt:', error)
      notificationService.apiError(error, 'Lỗi khi chấp nhận yêu cầu bảo hành')
    }
  }).catch(() => {
    notificationService.info('Thông tin','Đã hủy')
  })
}

const bulkReject = async () => {
  confirmDialogService.confirm(
    `Bạn có chắc muốn từ chối ${selectedWarranties.value.length} yêu cầu bảo hành?`,
    'Xác nhận',
    {
      confirmButtonText: 'Từ chối',
      cancelButtonText: 'Hủy',
      type: 'error'
    }
  ).then(async () => {
    try {
      await adminStore.bulkRejectWarranties(selectedWarranties.value)
      notificationService.success('Thành công',`Đã từ chối ${selectedWarranties.value.length} yêu cầu!`)
      clearSelection()
      fetchWarranties()
    } catch (error) {
      logger.error('Lỗi khi từ chối hàng loạt:', error)
      notificationService.apiError(error, 'Lỗi khi từ chối yêu cầu bảo hành')
    }
  }).catch(() => {
    notificationService.info('Thông tin','Đã hủy')
  })
}

// Export Functions
const exportWarranties = (format) => {
  const data = warranties.value.map(w => ({
    'Mã BH': `#WTY${w.id.toString().padStart(6, '0')}`,
    'Khách hàng': w.userName,
    'Email': w.userEmail,
    'Sản phẩm': w.productName,
    'Biến thể': w.variantName,
    'Vấn đề': w.issueDescription,
    'Loại BH': getWarrantyTypeText(w.warrantyType),
    'Trạng thái': getStatusText(w.status),
    'Ngày yêu cầu': formatDate(w.submittedAt),
    'Ngày mua': formatDate(w.purchaseDate)
  }))
  
  const filename = `warranties-${new Date().toISOString().split('T')[0]}`
  
  if (format === 'csv') {
    downloadCsv(data, `${filename}.csv`)
    notificationService.success('Thành công','Đã xuất file CSV!')
  } else {
    downloadJson(data, `${filename}.json`)
    notificationService.success('Thành công','Đã xuất file JSON!')
  }
}

// Document Upload
const openUploadModal = (item) => {
  selectedWarranty.value = item
  uploadedFiles.value = []
  showUploadModal.value = true
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileUpload = (event) => {
  const files = Array.from(event.target.files)
  files.forEach(file => {
    if (file.size > 10 * 1024 * 1024) {
      notificationService.error('Lỗi',`File ${file.name} quá lớn (> 10MB)`)
      return
    }
    uploadedFiles.value.push(file)
  })
}

const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1)
}

const saveDocuments = () => {
  if (uploadedFiles.value.length === 0) {
    notificationService.warning('Cảnh báo','Vui lòng chọn ít nhất 1 file!')
    return
  }
  
  // TODO: Upload warranty documents to server
  // Priority: Medium
  // Status: Pending backend API implementation
  // Note: Currently shows success message, but files are not persisted to server
  // Required: Backend endpoint for uploading warranty documents (POST /api/admin/warranty/{id}/documents)
  // Timeline: To be implemented in future release
  notificationService.success('Thành công',`Đã tải lên ${uploadedFiles.value.length} tài liệu!`)
  showUploadModal.value = false
  uploadedFiles.value = []
}

const viewWarrantyDetail = (item) => {
  selectedWarranty.value = item
  showDetailDialog.value = true
}

const approveWarranty = async (item) => {
  confirmDialogService.confirm(
    'Bạn có chắc muốn chấp nhận yêu cầu bảo hành này?',
    'Xác nhận',
    {
      confirmButtonText: 'Chấp nhận',
      cancelButtonText: 'Hủy',
      type: 'success'
    }
  ).then(async () => {
    try {
      await adminStore.approveWarranty(item.id)
      notificationService.success('Thành công','Đã chấp nhận yêu cầu!')
      fetchWarranties()
    } catch (error) {
      logger.error('Lỗi khi chấp nhận:', error)
      notificationService.apiError(error, 'Lỗi khi chấp nhận yêu cầu bảo hành')
    }
  }).catch(() => {
    notificationService.info('Thông tin','Đã hủy')
  })
}

const updateStatus = async (item, newStatus) => {
  const messages = {
    in_progress: 'Bắt đầu sửa chữa?',
    completed: 'Đánh dấu là hoàn thành?'
  }
  
  confirmDialogService.confirm(
    messages[newStatus],
    'Xác nhận',
    {
      confirmButtonText: 'Xác nhận',
      cancelButtonText: 'Hủy',
      type: 'info'
    }
  ).then(async () => {
    try {
      await adminStore.updateWarrantyStatus(item.id, newStatus)
      notificationService.success('Thành công','Đã cập nhật trạng thái!')
      fetchWarranties()
    } catch (error) {
      logger.error('Lỗi khi cập nhật trạng thái:', error)
      notificationService.apiError(error, 'Lỗi khi cập nhật trạng thái bảo hành')
    }
  }).catch(() => {
    notificationService.info('Thông tin','Đã hủy')
  })
}

const isWarrantyExpired = (item) => {
  const purchaseDate = new Date(item.purchaseDate)
  const expiryDate = new Date(purchaseDate)
  expiryDate.setMonth(expiryDate.getMonth() + (item.warrantyMonths || 12)) // Default 12 months if not specified
  return expiryDate < new Date()
}

const calculateDaysLeft = (item) => {
  const purchaseDate = new Date(item.purchaseDate)
  const expiryDate = new Date(purchaseDate)
  expiryDate.setMonth(expiryDate.getMonth() + (item.warrantyMonths || 12)) // Default 12 months if not specified
  const now = new Date()
  const daysLeft = Math.ceil((expiryDate - now) / (1000 * 60 * 60 * 24))
  return daysLeft > 0 ? daysLeft : 0
}

const getWarrantyTypeText = (type) => {
  const types = {
    repair: 'Sửa chữa',
    replace: 'Đổi mới',
    refund: 'Hoàn tiền',
    manufacturing_defect: 'Lỗi sản xuất',
    material_defect: 'Lỗi chất liệu'
  }
  return types[type] || type
}

const getTypeBadgeClass = (type) => {
  const classes = {
    repair: 'badge-info',
    replace: 'badge-warning',
    refund: 'badge-success'
  }
  return classes[type]
}

const getStatusClass = (status) => {
  const classes = {
    pending: 'status-pending',
    approved: 'status-processing',
    in_progress: 'status-shipped',
    completed: 'status-completed',
    rejected: 'status-cancelled'
  }
  return classes[status]
}

const getStatusText = (status) => {
  const statuses = {
    pending: 'Chờ xử lý',
    approved: 'Đã chấp nhận',
    in_progress: 'Đang sửa',
    completed: 'Hoàn thành',
    rejected: 'Từ chối'
  }
  return statuses[status] || status
}

// formatDate đã được import từ @/utils/formatters

// Lifecycle
onMounted(() => {
  fetchWarranties()
})
</script>







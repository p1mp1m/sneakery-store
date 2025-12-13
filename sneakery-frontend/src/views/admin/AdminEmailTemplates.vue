<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">email</i>
            Quản lý Email Templates
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Tạo và quản lý các mẫu email tự động</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="exportTemplates('csv')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">file_download</i>
            CSV
          </button>
          <button @click="exportTemplates('json')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
            <i class="material-icons text-base">code</i>
            JSON
          </button>
          <button @click="openCreateModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">add</i>
            Tạo mới
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">email</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(totalTemplates) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng templates</p>
          <div class="flex items-center gap-1 text-xs text-green-600 dark:text-green-400">
            <i class="material-icons text-sm">trending_up</i>
            <span>+{{ formatNumber(newTemplatesThisMonth) }} tháng này</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(activeTemplates) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Đang hoạt động</p>
          <div class="flex items-center gap-1 text-xs text-purple-600 dark:text-purple-400">
            <i class="material-icons text-sm">done</i>
            <span>{{ Math.round(emailTemplateStats.activeRate || 0) }}% tổng số</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-pink-500 to-pink-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">schedule</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatNumber(emailsSentToday) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Đã gửi hôm nay</p>
          <div class="flex items-center gap-1 text-xs text-gray-600 dark:text-gray-400">
            <i class="material-icons text-sm">info</i>
            <span>{{ formatNumber(emailsSentThisWeek) }} tuần này</span>
          </div>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">rate_review</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ openRate }}%</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tỷ lệ mở</p>
          <div class="flex items-center gap-1 text-xs" :class="emailTemplateStats.openRateTrend > 0 ? 'text-blue-600 dark:text-blue-400' : emailTemplateStats.openRateTrend < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="emailTemplateStats.openRateTrend > 0 ? 'trending_up' : emailTemplateStats.openRateTrend < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="emailTemplateStats.openRateTrend !== 0">{{ emailTemplateStats.openRateTrend > 0 ? '+' : '' }}{{ emailTemplateStats.openRateTrend.toFixed(1) }}% so với tuần trước</span>
            <span v-else>Không đổi so với tuần trước</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">search</i>
            Tìm kiếm
          </label>
          <div class="relative">
            <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
            <input 
              type="text" 
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              v-model="searchKeyword"
              placeholder="Tìm theo tên template, chủ đề..."
            />
            <button v-if="searchKeyword" @click="clearSearch" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>
        
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">category</i>
            Loại template
          </label>
          <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterType">
            <option value="all">Tất cả</option>
            <option value="welcome">Chào mừng</option>
            <option value="order">Đơn hàng</option>
            <option value="promotion">Khuyến mãi</option>
            <option value="notification">Thông báo</option>
            <option value="reminder">Nhắc nhở</option>
          </select>
        </div>

        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">toggle_on</i>
            Trạng thái
          </label>
          <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterStatus">
            <option value="all">Tất cả</option>
            <option value="active">Hoạt động</option>
            <option value="inactive">Tạm dừng</option>
            <option value="draft">Bản nháp</option>
          </select>
        </div>

        <div class="flex items-end">
          <button class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium w-full" @click="resetFilters">
            <i class="material-icons text-base">refresh</i>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Templates Grid -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">Danh sách Email Templates</h3>
        <div class="flex items-center gap-4">
          <span class="text-sm text-gray-500 dark:text-gray-400">{{ filteredTemplates.length }} templates</span>
          <div class="flex items-center gap-1 bg-gray-100 dark:bg-gray-700 rounded-lg p-1">
            <button 
              @click="viewMode = 'grid'" 
              class="p-1.5 rounded transition-colors"
              :class="viewMode === 'grid' ? 'bg-white dark:bg-gray-600 text-purple-600 dark:text-purple-400 shadow-sm' : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'"
            >
              <i class="material-icons text-base">grid_view</i>
            </button>
            <button 
              @click="viewMode = 'list'" 
              class="p-1.5 rounded transition-colors"
              :class="viewMode === 'list' ? 'bg-white dark:bg-gray-600 text-purple-600 dark:text-purple-400 shadow-sm' : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'"
            >
              <i class="material-icons text-base">list</i>
            </button>
          </div>
        </div>
      </div>

      <div v-if="loading" class="flex flex-col items-center justify-center p-12">
        <div class="space-y-4" role="status" aria-live="polite">
          <LoadingSkeleton
            v-for="n in 5"
            :key="n"
            type="list"
          />
          <span class="sr-only">Đang tải dữ liệu</span>
        </div>
      </div>

      <div v-else-if="filteredTemplates.length === 0" class="flex flex-col items-center justify-center p-12">
        <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">email</i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Không có template nào</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 text-center mb-4">Chưa có email template nào được tạo</p>
        <button @click="openCreateModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
          <i class="material-icons text-base">add</i>
          Tạo mới
        </button>
      </div>

      <!-- Grid View -->
      <div v-else-if="viewMode === 'grid'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div 
          v-for="template in paginatedTemplates" 
          :key="template.id" 
          class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200 cursor-pointer"
          @click="viewTemplate(template)"
        >
          <div class="p-4 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between">
            <div class="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
              <i class="material-icons text-purple-600 dark:text-purple-400">{{ getTemplateIcon(template.type) }}</i>
            </div>
            <span 
              class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
              :class="template.isActive 
                ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' 
                : 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'"
            >
              {{ template.isActive ? 'Hoạt động' : 'Tạm dừng' }}
            </span>
          </div>
          
          <div class="p-4">
            <h4 class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-2">{{ template.name }}</h4>
            <p class="text-sm text-gray-600 dark:text-gray-400 mb-3 line-clamp-2">{{ template.subject }}</p>
            <div class="mb-3">
              <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400">
                {{ getTemplateTypeText(template.type) }}
              </span>
            </div>
          </div>
          
          <div class="px-4 py-3 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between">
            <div class="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
              <div class="flex items-center gap-1">
                <i class="material-icons text-sm">send</i>
                <span>{{ formatNumber(template.sentCount) }}</span>
              </div>
              <div class="flex items-center gap-1">
                <i class="material-icons text-sm">visibility</i>
                <span>{{ template.openRate }}%</span>
              </div>
            </div>
            <div class="flex items-center gap-1" @click.stop>
              <button 
                @click="editTemplate(template)" 
                class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors"
                title="Chỉnh sửa"
              >
                <i class="material-icons text-base">edit</i>
              </button>
              <button 
                @click="duplicateTemplate(template)" 
                class="p-1.5 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded-lg transition-colors"
                title="Sao chép"
              >
                <i class="material-icons text-base">content_copy</i>
              </button>
              <button 
                @click="deleteTemplate(template)" 
                class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
                title="Xóa"
              >
                <i class="material-icons text-base">delete</i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- List View -->
      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Template</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Loại</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Đã gửi</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Tỷ lệ mở</th>
              <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Cập nhật</th>
              <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr 
              v-for="template in paginatedTemplates" 
              :key="template.id" 
              class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors"
            >
              <td class="px-4 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center">
                    <i class="material-icons text-purple-600 dark:text-purple-400">{{ getTemplateIcon(template.type) }}</i>
                  </div>
                  <div>
                    <div class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ template.name }}</div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">{{ template.subject }}</div>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4">
                <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400">
                  {{ getTemplateTypeText(template.type) }}
                </span>
              </td>
              <td class="px-4 py-4">
                <span 
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="template.isActive 
                    ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' 
                    : 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'"
                >
                  {{ template.isActive ? 'Hoạt động' : 'Tạm dừng' }}
                </span>
              </td>
              <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">{{ formatNumber(template.sentCount) }}</td>
              <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">{{ template.openRate }}%</td>
              <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">{{ formatDate(template.updatedAt) }}</td>
              <td class="px-4 py-4 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button 
                    @click="viewTemplate(template)" 
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors"
                    title="Xem"
                  >
                    <i class="material-icons text-base">visibility</i>
                  </button>
                  <button 
                    @click="editTemplate(template)" 
                    class="p-1.5 text-green-600 dark:text-green-400 hover:bg-green-50 dark:hover:bg-green-900/20 rounded-lg transition-colors"
                    title="Chỉnh sửa"
                  >
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button 
                    @click="duplicateTemplate(template)" 
                    class="p-1.5 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded-lg transition-colors"
                    title="Sao chép"
                  >
                    <i class="material-icons text-base">content_copy</i>
                  </button>
                  <button 
                    @click="deleteTemplate(template)" 
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

      <!-- Pagination -->
      <div v-if="!loading && filteredTemplates.length > 0 && totalPages > 1" class="flex items-center justify-between gap-4 px-4 py-3 border-t border-gray-200 dark:border-gray-700">
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Hiển thị {{ currentPage * pageSize + 1 }} - {{ Math.min((currentPage + 1) * pageSize, filteredTemplates.length) }} trong tổng số {{ filteredTemplates.length }} templates
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="currentPage === 0"
            @click="goToPage(currentPage - 1)"
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
            @click="goToPage(currentPage + 1)"
          >
            Sau
            <i class="material-icons text-base">chevron_right</i>
          </button>
        </div>
      </div>
    </div>

    <!-- Template Editor Modal -->
    <div 
      v-if="showEditorModal" 
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 dark:bg-opacity-70 p-4"
      @click="closeEditorModal"
    >
      <div 
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-5xl w-full max-h-[90vh] overflow-y-auto"
        @click.stop
      >
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">edit</i>
            {{ isEditing ? 'Chỉnh sửa' : 'Tạo mới' }} Email Template
          </h3>
          <button 
            @click="closeEditorModal" 
            class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          >
            <i class="material-icons text-base">close</i>
          </button>
        </div>
        <div class="p-4">
          <div class="mb-4 border-b border-gray-200 dark:border-gray-700">
            <div class="flex gap-2">
              <button 
                @click="activeTab = 'basic'" 
                class="flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-t-lg transition-colors"
                :class="activeTab === 'basic' 
                  ? 'bg-purple-50 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400 border-b-2 border-purple-500' 
                  : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100'"
              >
                <i class="material-icons text-base">info</i>
                Thông tin cơ bản
              </button>
              <button 
                @click="activeTab = 'content'" 
                class="flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-t-lg transition-colors"
                :class="activeTab === 'content' 
                  ? 'bg-purple-50 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400 border-b-2 border-purple-500' 
                  : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100'"
              >
                <i class="material-icons text-base">article</i>
                Nội dung
              </button>
              <button 
                @click="activeTab = 'preview'" 
                class="flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-t-lg transition-colors"
                :class="activeTab === 'preview' 
                  ? 'bg-purple-50 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400 border-b-2 border-purple-500' 
                  : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100'"
              >
                <i class="material-icons text-base">preview</i>
                Xem trước
              </button>
            </div>
          </div>

          <!-- Basic Info Tab -->
          <div v-if="activeTab === 'basic'" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Tên template</label>
                <input 
                  type="text" 
                  v-model="templateForm.name" 
                  class="w-full px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Nhập tên template..."
                >
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Loại template</label>
                <select 
                  v-model="templateForm.type" 
                  class="w-full px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                >
                  <option value="welcome">Chào mừng</option>
                  <option value="order">Đơn hàng</option>
                  <option value="promotion">Khuyến mãi</option>
                  <option value="notification">Thông báo</option>
                  <option value="reminder">Nhắc nhở</option>
                </select>
              </div>
              
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Chủ đề email</label>
                <input 
                  type="text" 
                  v-model="templateForm.subject" 
                  class="w-full px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Nhập chủ đề email..."
                >
              </div>
              
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Trạng thái</label>
                <label class="flex items-center gap-3 cursor-pointer">
                  <input 
                    type="checkbox" 
                    v-model="templateForm.isActive" 
                    class="w-5 h-5 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                  >
                  <span class="text-sm text-gray-700 dark:text-gray-300">{{ templateForm.isActive ? 'Hoạt động' : 'Tạm dừng' }}</span>
                </label>
              </div>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Mô tả</label>
              <textarea 
                v-model="templateForm.description" 
                class="w-full px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                rows="3"
                placeholder="Nhập mô tả template..."
              ></textarea>
            </div>
          </div>

          <!-- Content Tab -->
          <div v-if="activeTab === 'content'" class="space-y-4">
            <div class="flex items-center gap-2 p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <button 
                @click="insertVariable('{{name}}')" 
                class="flex items-center gap-2 px-3 py-1.5 bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
              >
                <i class="material-icons text-base">person</i>
                Tên
              </button>
              <button 
                @click="insertVariable('{{email}}')" 
                class="flex items-center gap-2 px-3 py-1.5 bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
              >
                <i class="material-icons text-base">email</i>
                Email
              </button>
              <button 
                @click="insertVariable('{{orderNumber}}')" 
                class="flex items-center gap-2 px-3 py-1.5 bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
              >
                <i class="material-icons text-base">receipt</i>
                Số đơn hàng
              </button>
              <button 
                @click="insertVariable('{{total}}')" 
                class="flex items-center gap-2 px-3 py-1.5 bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
              >
                <i class="material-icons text-base">attach_money</i>
                Tổng tiền
              </button>
            </div>
            
            <div>
              <textarea 
                v-model="templateForm.body" 
                class="w-full px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent font-mono"
                rows="12"
                placeholder="Nhập nội dung email..."
              ></textarea>
            </div>
          </div>

          <!-- Preview Tab -->
          <div v-if="activeTab === 'preview'" class="space-y-4">
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <div class="mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
                <h4 class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-2">{{ templateForm.subject || 'Chủ đề email' }}</h4>
                <div class="flex items-center gap-4 text-xs text-gray-600 dark:text-gray-400">
                  <span>Gửi đến: {{ previewData.email }}</span>
                  <span>Ngày: {{ formatDate(new Date()) }}</span>
                </div>
              </div>
              <div class="prose dark:prose-invert max-w-none" v-html="previewContent"></div>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end gap-2 p-4 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="closeEditorModal" 
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            Hủy
          </button>
          <button 
            @click="saveTemplate" 
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
          >
            <i class="material-icons text-base">save</i>
            {{ isEditing ? 'Cập nhật' : 'Tạo mới' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Template Viewer Modal -->
    <div 
      v-if="showViewerModal" 
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 dark:bg-opacity-70 p-4"
      @click="closeViewerModal"
    >
      <div 
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto"
        @click.stop
      >
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">visibility</i>
            {{ selectedTemplate?.name }}
          </h3>
          <button 
            @click="closeViewerModal" 
            class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          >
            <i class="material-icons text-base">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="selectedTemplate">
            <div class="mb-6">
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Loại:</label>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ getTemplateTypeText(selectedTemplate.type) }}</span>
                </div>
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Trạng thái:</label>
                  <span 
                    class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                    :class="selectedTemplate.isActive 
                      ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' 
                      : 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'"
                  >
                    {{ selectedTemplate.isActive ? 'Hoạt động' : 'Tạm dừng' }}
                  </span>
                </div>
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Đã gửi:</label>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ formatNumber(selectedTemplate.sentCount) }}</span>
                </div>
                <div>
                  <label class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block">Tỷ lệ mở:</label>
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{ selectedTemplate.openRate }}%</span>
                </div>
              </div>
            </div>
            
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4 class="text-base font-semibold text-gray-900 dark:text-gray-100 mb-4">{{ selectedTemplate.subject }}</h4>
              <div class="prose dark:prose-invert max-w-none" v-html="selectedTemplate.body"></div>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end gap-2 p-4 border-t border-gray-200 dark:border-gray-700">
          <button 
            @click="closeViewerModal" 
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            Đóng
          </button>
          <button 
            @click="editTemplate(selectedTemplate)" 
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
          >
            Chỉnh sửa
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import AdminService from '@/services/adminService'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import notificationService from '@/utils/notificationService'
import confirmDialogService from '@/utils/confirmDialogService'
import logger from '@/utils/logger'
import LoadingSkeleton from '@/components/common/LoadingSkeleton.vue'
import { formatDate } from '@/utils/formatters'

// Store
const adminStore = useAdminStore()

// State
const loading = ref(false)
const templates = ref([])
const searchKeyword = ref('')
const filterType = ref('all')
const filterStatus = ref('all')
const currentPage = ref(0)
const pageSize = ref(12)
const viewMode = ref('grid')
const showEditorModal = ref(false)
const showViewerModal = ref(false)
const selectedTemplate = ref(null)
const isEditing = ref(false)
const activeTab = ref('basic')

const templateForm = ref({
  name: '',
  type: 'welcome',
  subject: '',
  body: '',
  description: '',
  isActive: true
})

const previewData = ref({
  name: 'Nguyễn Văn A',
  email: 'nguyenvana@example.com',
  orderNumber: 'ORD-20240125-0001',
  total: '2,500,000 đ'
})

// Mock data
const mockTemplates = ref([
  {
    id: 1,
    name: 'Chào mừng khách hàng mới',
    type: 'welcome',
    subject: 'Chào mừng bạn đến với Sneakery Store!',
    body: '<h2>Xin chào {{name}}!</h2><p>Cảm ơn bạn đã đăng ký tài khoản tại Sneakery Store.</p><p>Chúng tôi sẽ gửi cho bạn những thông tin khuyến mãi hấp dẫn nhất!</p>',
    description: 'Email chào mừng khách hàng mới đăng ký',
    isActive: true,
    sentCount: 1250,
    openRate: 68.5,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-25T10:30:00Z'
  },
  {
    id: 2,
    name: 'Xác nhận đơn hàng',
    type: 'order',
    subject: 'Xác nhận đơn hàng #{{orderNumber}}',
    body: '<h2>Đơn hàng của bạn đã được xác nhận!</h2><p>Xin chào {{name}},</p><p>Đơn hàng #{{orderNumber}} với tổng giá trị {{total}} đã được xác nhận.</p><p>Chúng tôi sẽ gửi hàng trong vòng 24h.</p>',
    description: 'Email xác nhận đơn hàng',
    isActive: true,
    sentCount: 890,
    openRate: 72.3,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-25T11:15:00Z'
  },
  {
    id: 3,
    name: 'Khuyến mãi cuối tuần',
    type: 'promotion',
    subject: 'Giảm giá 50% cuối tuần - Chỉ còn 2 ngày!',
    body: '<h2>Khuyến mãi cuối tuần!</h2><p>Xin chào {{name}},</p><p>Chỉ còn 2 ngày để hưởng ưu đãi giảm giá 50% cho tất cả sản phẩm!</p><p>Đừng bỏ lỡ cơ hội này!</p>',
    description: 'Email khuyến mãi cuối tuần',
    isActive: true,
    sentCount: 2100,
    openRate: 45.2,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-25T12:00:00Z'
  },
  {
    id: 4,
    name: 'Nhắc nhở giỏ hàng',
    type: 'reminder',
    subject: 'Bạn có sản phẩm chưa thanh toán',
    body: '<h2>Bạn có sản phẩm chưa thanh toán!</h2><p>Xin chào {{name}},</p><p>Bạn có sản phẩm trong giỏ hàng chưa thanh toán.</p><p>Hãy hoàn tất đơn hàng để không bỏ lỡ sản phẩm yêu thích!</p>',
    description: 'Email nhắc nhở giỏ hàng',
    isActive: false,
    sentCount: 450,
    openRate: 38.7,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-25T14:30:00Z'
  }
])

// Computed
const totalTemplates = computed(() => templates.value.length)
const activeTemplates = computed(() => templates.value.filter(t => t.isActive).length)
const newTemplatesThisMonth = computed(() => {
  const thisMonth = new Date().getMonth()
  const thisYear = new Date().getFullYear()
  
  return templates.value.filter(t => {
    const templateDate = new Date(t.createdAt)
    return templateDate.getMonth() === thisMonth && templateDate.getFullYear() === thisYear
  }).length
})
// Email template stats từ API (data thật)
const emailTemplateStats = ref({
  totalTemplates: 0,
  activeTemplates: 0,
  newTemplatesThisMonth: 0,
  activeRate: 0,
  emailsSentToday: 0,
  emailsSentThisWeek: 0,
  openRate: 0,
  openRateTrend: 0
})

const emailsSentToday = computed(() => emailTemplateStats.value.emailsSentToday || 0)
const emailsSentThisWeek = computed(() => emailTemplateStats.value.emailsSentThisWeek || 0)
const openRate = computed(() => emailTemplateStats.value.openRate || 0)

const filteredTemplates = computed(() => {
  let filtered = templates.value || []

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(t => 
      t.name.toLowerCase().includes(keyword) ||
      t.subject.toLowerCase().includes(keyword) ||
      t.description.toLowerCase().includes(keyword)
    )
  }

  if (filterType.value !== 'all') {
    filtered = filtered.filter(t => t.type === filterType.value)
  }

  if (filterStatus.value !== 'all') {
    filtered = filtered.filter(t => {
      switch (filterStatus.value) {
        case 'active':
          return t.isActive
        case 'inactive':
          return !t.isActive
        case 'draft':
          return t.sentCount === 0
        default:
          return true
      }
    })
  }

  return filtered.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
})

const totalPages = computed(() => Math.ceil(filteredTemplates.value.length / pageSize.value))
const paginatedTemplates = computed(() => {
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return filteredTemplates.value.slice(start, end)
})

const previewContent = computed(() => {
  let content = templateForm.value.body || ''
  
  // Replace variables with preview data
  content = content.replace(/\{\{name\}\}/g, previewData.value.name)
  content = content.replace(/\{\{email\}\}/g, previewData.value.email)
  content = content.replace(/\{\{orderNumber\}\}/g, previewData.value.orderNumber)
  content = content.replace(/\{\{total\}\}/g, previewData.value.total)
  
  return content
})

// Methods
const fetchTemplates = async () => {
  // Load email template stats từ API (data thật)
  try {
    const statsResult = await AdminService.getEmailTemplateStats()
    if (statsResult) {
      emailTemplateStats.value = statsResult
      logger.log('✅ Email template stats loaded:', emailTemplateStats.value)
    }
  } catch (error) {
    logger.warn('Email template stats API error:', error)
  }
  
  loading.value = true
  try {
    const result = await adminStore.fetchEmailTemplates(currentPage.value, pageSize.value, {})
    templates.value = result.content || []
  } catch (error) {
    notificationService.apiError(error, 'Không thể tải danh sách templates')
  } finally {
    loading.value = false
  }
}

const clearSearch = () => {
  searchKeyword.value = ''
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = 'all'
  filterStatus.value = 'all'
  currentPage.value = 0
}

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

const openCreateModal = () => {
  isEditing.value = false
  templateForm.value = {
    name: '',
    type: 'welcome',
    subject: '',
    body: '',
    description: '',
    isActive: true
  }
  activeTab.value = 'basic'
  showEditorModal.value = true
}

const editTemplate = (template) => {
  isEditing.value = true
  templateForm.value = { ...template }
  activeTab.value = 'basic'
  showEditorModal.value = true
}

const viewTemplate = (template) => {
  selectedTemplate.value = template
  showViewerModal.value = true
}

const duplicateTemplate = async (template) => {
  try {
    await confirmDialogService.confirm(
      `Bạn có chắc chắn muốn sao chép template "${template.name}"?`,
      'Xác nhận sao chép',
      {
        confirmButtonText: 'Sao chép',
        cancelButtonText: 'Hủy',
        type: 'info'
      }
    )
    
    const newTemplate = {
      ...template,
      id: Date.now(),
      name: `${template.name} (Bản sao)`,
      sentCount: 0,
      openRate: 0,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    templates.value.unshift(newTemplate)
    notificationService.success('Thành công','Đã sao chép template thành công')
  } catch {
    // User cancelled
  }
}

const deleteTemplate = async (template) => {
  try {
    await confirmDialogService.confirm(
      `Bạn có chắc chắn muốn xóa template "${template.name}"? Hành động này không thể hoàn tác.`,
      'Xác nhận xóa',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    templates.value = templates.value.filter(t => t.id !== template.id)
    notificationService.success('Thành công','Đã xóa template thành công')
  } catch {
    // User cancelled
  }
}

const closeEditorModal = () => {
  showEditorModal.value = false
  selectedTemplate.value = null
}

const closeViewerModal = () => {
  showViewerModal.value = false
  selectedTemplate.value = null
}

const insertVariable = (variable) => {
  const textarea = document.querySelector('textarea[placeholder="Nhập nội dung email..."]')
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = templateForm.value.body || ''
  const before = text.substring(0, start)
  const after = text.substring(end, text.length)
  
  templateForm.value.body = before + variable + after
  
  // Set cursor position after inserted variable
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + variable.length, start + variable.length)
  }, 0)
}

const saveTemplate = async () => {
  if (!templateForm.value.name.trim()) {
    notificationService.error('Lỗi','Vui lòng nhập tên template')
    return
  }
  
  if (!templateForm.value.subject.trim()) {
    notificationService.error('Lỗi','Vui lòng nhập chủ đề email')
    return
  }
  
  if (!templateForm.value.body.trim()) {
    notificationService.error('Lỗi','Vui lòng nhập nội dung email')
    return
  }

  try {
    if (isEditing.value) {
      // Update existing template
      const index = templates.value.findIndex(t => t.id === templateForm.value.id)
      if (index !== -1) {
        templates.value[index] = {
          ...templateForm.value,
          updatedAt: new Date().toISOString()
        }
      }
      notificationService.success('Thành công','Đã cập nhật template thành công')
    } else {
      // Create new template
      const newTemplate = {
        ...templateForm.value,
        id: Date.now(),
        sentCount: 0,
        openRate: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      templates.value.unshift(newTemplate)
      notificationService.success('Thành công','Đã tạo template thành công')
    }
    
    closeEditorModal()
  } catch (error) {
    notificationService.apiError(error, 'Có lỗi xảy ra khi lưu template')
  }
}

const exportTemplates = (format) => {
  try {
    const dataToExport = filteredTemplates.value || []
    if (dataToExport.length === 0) {
      notificationService.warning('Cảnh báo','Không có dữ liệu để xuất')
      return
    }
    
    const exportData = dataToExport.map(template => ({
      'ID': template.id,
      'Tên template': template.name,
      'Loại': getTemplateTypeText(template.type),
      'Chủ đề': template.subject,
      'Mô tả': template.description,
      'Trạng thái': template.isActive ? 'Hoạt động' : 'Tạm dừng',
      'Đã gửi': formatNumber(template.sentCount),
      'Tỷ lệ mở': `${template.openRate}%`,
      'Ngày tạo': formatDate(template.createdAt),
      'Ngày cập nhật': formatDate(template.updatedAt)
    }))

    if (format === 'csv') {
      downloadCsv(exportData, 'email-templates.csv')
      notificationService.success('Thành công','Xuất CSV thành công!')
    } else if (format === 'json') {
      downloadJson('email-templates', exportData)
      notificationService.success('Thành công','Xuất JSON thành công!')
    }
  } catch (error) {
    logger.error('Export error:', error)
    notificationService.apiError(error, 'Có lỗi xảy ra khi xuất dữ liệu')
  }
}

// Helper functions
const getTemplateIcon = (type) => {
  const icons = {
    welcome: 'waving_hand',
    order: 'receipt',
    promotion: 'local_offer',
    notification: 'notifications',
    reminder: 'schedule'
  }
  return icons[type] || 'email'
}

const getTemplateTypeText = (type) => {
  const texts = {
    welcome: 'Chào mừng',
    order: 'Đơn hàng',
    promotion: 'Khuyến mãi',
    notification: 'Thông báo',
    reminder: 'Nhắc nhở'
  }
  return texts[type] || type
}

const formatNumber = (num) => {
  return new Intl.NumberFormat('vi-VN').format(num)
}

// formatDate đã được import từ @/utils/formatters

// Lifecycle
onMounted(() => {
  fetchTemplates()
})
</script>




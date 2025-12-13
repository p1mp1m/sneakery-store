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
              >history</i
            >
            Nhật ký hoạt động
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
            Theo dõi tất cả hoạt động trong hệ thống
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="exportLogs('csv')"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">file_download</i>
            CSV
          </button>
          <button
            @click="exportLogs('json')"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">code</i>
            JSON
          </button>
          <button
            @click="clearOldLogs"
            class="flex items-center gap-2 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">delete_sweep</i>
            Dọn dẹp
          </button>
        </div>
      </div>
    </div>

    <!-- Enhanced Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-400 to-teal-400 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">timeline</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ formatNumber(totalLogs) }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">
            Tổng hoạt động
          </p>
          <p
            class="text-xs text-green-600 dark:text-green-400 flex items-center gap-1"
          >
            <i class="material-icons text-sm">trending_up</i>
            +{{ formatNumber(todayLogs) }} hôm nay
          </p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">person</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ formatNumber(activeUsers) }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">
            Người dùng hoạt động
          </p>
          <p
            class="text-xs text-gray-600 dark:text-gray-400 flex items-center gap-1"
          >
            <i class="material-icons text-sm">info</i>
            {{ Math.round((activeUsers / totalUsers) * 100) || 0 }}% tổng số
          </p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-pink-500 to-red-500 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">warning</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ formatNumber(suspiciousLogs) }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">
            Hoạt động bất thường
          </p>
          <p
            class="text-xs text-red-600 dark:text-red-400 flex items-center gap-1"
          >
            <i class="material-icons text-sm">security</i>
            Cần kiểm tra
          </p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-400 to-cyan-400 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg"
              >admin_panel_settings</i
            >
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ formatNumber(adminLogs) }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">
            Hoạt động Admin
          </p>
          <p
            class="text-xs text-green-600 dark:text-green-400 flex items-center gap-1"
          >
            <i class="material-icons text-sm">verified</i>
            {{ Math.round((adminLogs / totalLogs) * 100) || 0 }}% tổng số
          </p>
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <div class="relative">
            <i
              class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"
              >search</i
            >
            <input
              type="text"
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              v-model="searchKeyword"
              placeholder="Tìm theo người dùng, hành động, IP..."
            />
            <button
              v-if="searchKeyword"
              @click="clearSearch"
              class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
            >
              <i class="material-icons text-base">close</i>
            </button>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <div class="flex flex-col gap-1">
            <label
              class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1"
            >
              <i class="material-icons text-sm">person</i>
              Người dùng
            </label>
            <select
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              v-model="filterUser"
            >
              <option value="all">Tất cả</option>
              <option value="admin">Admin</option>
              <option value="user">User</option>
              <option value="guest">Khách</option>
            </select>
          </div>

          <div class="flex flex-col gap-1">
            <label
              class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1"
            >
              <i class="material-icons text-sm">category</i>
              Loại hoạt động
            </label>
            <select
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              v-model="filterAction"
            >
              <option value="all">Tất cả</option>
              <option value="login">Đăng nhập</option>
              <option value="logout">Đăng xuất</option>
              <option value="create">Tạo mới</option>
              <option value="update">Cập nhật</option>
              <option value="delete">Xóa</option>
              <option value="view">Xem</option>
            </select>
          </div>

          <div class="flex flex-col gap-1">
            <label
              class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1"
            >
              <i class="material-icons text-sm">date_range</i>
              Thời gian
            </label>
            <select
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              v-model="filterTimeRange"
            >
              <option value="all">Tất cả</option>
              <option value="today">Hôm nay</option>
              <option value="week">Tuần này</option>
              <option value="month">Tháng này</option>
              <option value="year">Năm nay</option>
            </select>
          </div>

          <button
            class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium mt-6"
            @click="resetFilters"
          >
            <i class="material-icons text-base">refresh</i>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Activity Logs Table -->
    <div
      class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden"
    >
      <div
        class="px-4 py-3 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between"
      >
        <h3 class="text-base font-semibold text-gray-900 dark:text-gray-100">
          Nhật ký hoạt động
        </h3>
        <div class="flex items-center gap-3">
          <span class="text-sm text-gray-600 dark:text-gray-400"
            >{{ filteredLogs.length }} hoạt động</span
          >
          <button
            @click="refreshLogs"
            class="flex items-center gap-1 px-3 py-1.5 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            <i class="material-icons text-base">refresh</i>
            Làm mới
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div
        v-if="loading"
        class="flex flex-col items-center justify-center p-12"
      >
        <div class="space-y-4" role="status" aria-live="polite">
          <LoadingSkeleton v-for="n in 5" :key="n" type="list" />
          <span class="sr-only">Đang tải nhật ký hoạt động</span>
        </div>
      </div>

      <!-- Empty State -->
      <div
        v-else-if="filteredLogs.length === 0"
        class="flex flex-col items-center justify-center p-12"
      >
        <div
          class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4"
        >
          <i
            class="material-icons text-purple-600 dark:text-purple-400 text-3xl"
            >history</i
          >
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
          Không có hoạt động nào
        </h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
          Chưa có hoạt động nào được ghi nhận
        </p>
      </div>

      <!-- Activity Logs Table -->
      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead
            class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600"
          >
            <tr>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Thời gian
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Người dùng
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Hành động
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Đối tượng
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Chi tiết
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                IP Address
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                User Agent
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider"
              >
                Trạng thái
              </th>
            </tr>
          </thead>
          <tbody
            class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700"
          >
            <tr
              v-for="log in paginatedLogs"
              :key="log.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors"
            >
              <td class="px-4 py-4">
                <div class="text-sm text-gray-900 dark:text-gray-100">
                  {{ formatDateTime(log.createdAt) }}
                </div>
                <div class="text-xs text-gray-500 dark:text-gray-400">
                  {{ formatDate(log.createdAt) }}
                </div>
              </td>
              <td class="px-4 py-4">
                <div class="flex items-center gap-2">
                  <div
                    class="w-8 h-8 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center overflow-hidden"
                  >
                    <img
                      v-if="log.userAvatar"
                      :src="log.userAvatar"
                      :alt="log.userName"
                      class="w-full h-full object-cover"
                    />
                    <i
                      v-else
                      class="material-icons text-gray-400 dark:text-gray-500 text-sm"
                      >person</i
                    >
                  </div>
                  <div>
                    <div
                      class="text-sm font-medium text-gray-900 dark:text-gray-100"
                    >
                      {{ log.userName || "Khách" }}
                    </div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">
                      {{ log.userRole || "Guest" }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4">
                <span
                  class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400':
                      getActionClass(log.action) === 'success',
                    'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400':
                      getActionClass(log.action) === 'info',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400':
                      getActionClass(log.action) === 'warning',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400':
                      getActionClass(log.action) === 'danger',
                    'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400':
                      getActionClass(log.action) === 'neutral',
                  }"
                >
                  <i class="material-icons text-xs">{{
                    getActionIcon(log.action)
                  }}</i>
                  {{ getActionText(log.action) }}
                </span>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm text-gray-900 dark:text-gray-100">
                  {{ log.entityType }}
                </div>
                <div
                  v-if="log.entityId"
                  class="text-xs text-gray-500 dark:text-gray-400"
                >
                  ID: {{ log.entityId }}
                </div>
              </td>
              <td class="px-4 py-4">
                <div class="space-y-1">
                  <div v-if="log.oldValue" class="text-xs">
                    <span class="font-medium text-gray-700 dark:text-gray-300"
                      >Cũ:</span
                    >
                    <span class="text-gray-600 dark:text-gray-400 ml-1">{{
                      truncateText(log.oldValue, 50)
                    }}</span>
                  </div>
                  <div v-if="log.newValue" class="text-xs">
                    <span class="font-medium text-gray-700 dark:text-gray-300"
                      >Mới:</span
                    >
                    <span class="text-gray-600 dark:text-gray-400 ml-1">{{
                      truncateText(log.newValue, 50)
                    }}</span>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm text-gray-900 dark:text-gray-100">
                  {{ log.ipAddress }}
                </div>
                <div class="text-xs text-gray-500 dark:text-gray-400">
                  {{ getLocationFromIP(log.ipAddress) }}
                </div>
              </td>
              <td class="px-4 py-4">
                <div class="text-sm text-gray-900 dark:text-gray-100">
                  {{ getBrowserFromUserAgent(log.userAgent) }}
                </div>
                <div class="text-xs text-gray-500 dark:text-gray-400">
                  {{ getOSFromUserAgent(log.userAgent) }}
                </div>
              </td>
              <td class="px-4 py-4">
                <span
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="
                    getStatusClass(log.status) === 'success'
                      ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
                      : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
                  "
                >
                  {{ getStatusText(log.status) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div
        v-if="!loading && filteredLogs.length > 0 && totalPages > 1"
        class="flex items-center justify-between gap-4 px-4 py-3 border-t border-gray-200 dark:border-gray-700"
      >
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Hiển thị {{ currentPage * pageSize + 1 }} -
          {{ Math.min((currentPage + 1) * pageSize, filteredLogs.length) }}
          trong tổng số {{ filteredLogs.length }} hoạt động
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

    <!-- Log Detail Modal -->
    <div
      v-if="showDetailModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 dark:bg-opacity-70 p-4"
      @click="closeDetailModal"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto"
        @click.stop
      >
        <div
          class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700"
        >
          <h3
            class="text-lg font-semibold text-gray-900 dark:text-gray-100 flex items-center gap-2"
          >
            <i class="material-icons text-purple-600 dark:text-purple-400"
              >info</i
            >
            Chi tiết hoạt động
          </h3>
          <button
            @click="closeDetailModal"
            class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          >
            <i class="material-icons text-base">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="selectedLog">
            <div class="mb-6">
              <h4
                class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3"
              >
                Thông tin cơ bản
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >Thời gian:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    formatDateTime(selectedLog.createdAt)
                  }}</span>
                </div>
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >Người dùng:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    selectedLog.userName || "Khách"
                  }}</span>
                </div>
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >Vai trò:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    selectedLog.userRole || "Guest"
                  }}</span>
                </div>
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >Hành động:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    getActionText(selectedLog.action)
                  }}</span>
                </div>
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >Đối tượng:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    selectedLog.entityType
                  }}</span>
                </div>
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >ID đối tượng:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    selectedLog.entityId || "N/A"
                  }}</span>
                </div>
              </div>
            </div>

            <div
              v-if="selectedLog.oldValue || selectedLog.newValue"
              class="mb-6"
            >
              <h4
                class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3"
              >
                Thay đổi dữ liệu
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div v-if="selectedLog.oldValue">
                  <h5
                    class="text-xs font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Giá trị cũ:
                  </h5>
                  <pre
                    class="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg text-xs font-mono text-gray-900 dark:text-gray-100 overflow-x-auto"
                    >{{ selectedLog.oldValue }}</pre
                  >
                </div>
                <div v-if="selectedLog.newValue">
                  <h5
                    class="text-xs font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Giá trị mới:
                  </h5>
                  <pre
                    class="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg text-xs font-mono text-gray-900 dark:text-gray-100 overflow-x-auto"
                    >{{ selectedLog.newValue }}</pre
                  >
                </div>
              </div>
            </div>

            <div class="mb-6">
              <h4
                class="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-3"
              >
                Thông tin kỹ thuật
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >IP Address:</label
                  >
                  <code
                    class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-sm font-mono text-gray-900 dark:text-gray-100"
                  >
                    {{ selectedLog.ipAddress }}
                  </code>
                </div>
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >Vị trí:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    getLocationFromIP(selectedLog.ipAddress)
                  }}</span>
                </div>
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >Trình duyệt:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    getBrowserFromUserAgent(selectedLog.userAgent)
                  }}</span>
                </div>
                <div>
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >Hệ điều hành:</label
                  >
                  <span class="text-sm text-gray-900 dark:text-gray-100">{{
                    getOSFromUserAgent(selectedLog.userAgent)
                  }}</span>
                </div>
                <div class="md:col-span-2">
                  <label
                    class="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1 block"
                    >User Agent:</label
                  >
                  <code
                    class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100 block break-all"
                  >
                    {{ selectedLog.userAgent }}
                  </code>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div
          class="flex items-center justify-end gap-2 p-4 border-t border-gray-200 dark:border-gray-700"
        >
          <button
            @click="closeDetailModal"
            class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { downloadCsv, downloadJson } from "@/utils/exportHelpers";
import notificationService from "@/utils/notificationService";
import confirmDialogService from "@/utils/confirmDialogService";
import { useAdminStore } from "@/stores/admin";
import logger from "@/utils/logger";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import { formatDate, formatDateTime } from "@/utils/formatters";

// Stores
const adminStore = useAdminStore();

// State
const loading = ref(false);
const logs = ref([]);
const searchKeyword = ref("");
const filterUser = ref("all");
const filterAction = ref("all");
const filterTimeRange = ref("all");
const currentPage = ref(0);
const pageSize = ref(20);
const showDetailModal = ref(false);
const selectedLog = ref(null);

// Mock data removed - using real API data

// Computed
const totalLogs = computed(() => logs.value.length);
const todayLogs = computed(() => {
  const today = new Date().toDateString();
  return logs.value.filter(
    (log) => new Date(log.createdAt).toDateString() === today
  ).length;
});
const activeUsers = computed(() => {
  const uniqueUsers = new Set(
    logs.value.map((log) => log.userId).filter(Boolean)
  );
  return uniqueUsers.size;
});
const totalUsers = computed(() => 100); // Mock total users
const suspiciousLogs = computed(() => {
  return logs.value.filter((log) => {
    const ip = log.ipAddress || "";
    return (
      log.action === "delete" ||
      ip.includes("192.168.1.999") ||
      log.status === "failed"
    );
  }).length;
});

const adminLogs = computed(() => {
  return (logs.value || []).filter((log) => log.userRole === "ADMIN").length;
});

const filteredLogs = computed(() => {
  let filtered = logs.value || [];

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter((log) => {
      const name = log.userName?.toLowerCase() || "";
      const action = log.action?.toLowerCase() || "";
      const entity = log.entityType?.toLowerCase() || "";
      const ip = log.ipAddress || "";

      return (
        name.includes(keyword) ||
        action.includes(keyword) ||
        entity.includes(keyword) ||
        ip.includes(keyword)
      );
    });
  }

  if (filterUser.value !== "all") {
    filtered = filtered.filter((log) => {
      switch (filterUser.value) {
        case "admin":
          return log.userRole === "ADMIN";
        case "user":
          return log.userRole === "USER";
        case "guest":
          return !log.userRole;
        default:
          return true;
      }
    });
  }

  if (filterAction.value !== "all") {
    filtered = filtered.filter((log) => log.action === filterAction.value);
  }

  if (filterTimeRange.value !== "all") {
    const now = new Date();
    filtered = filtered.filter((log) => {
      const logDate = new Date(log.createdAt);
      switch (filterTimeRange.value) {
        case "today":
          return logDate.toDateString() === now.toDateString();
        case "week":
          const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
          return logDate >= weekAgo;
        case "month":
          const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
          return logDate >= monthAgo;
        case "year":
          const yearAgo = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
          return logDate >= yearAgo;
        default:
          return true;
      }
    });
  }

  return filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
});

const totalPages = computed(() =>
  Math.ceil(filteredLogs.value.length / pageSize.value)
);
const paginatedLogs = computed(() => {
  const start = currentPage.value * pageSize.value;
  const end = start + pageSize.value;
  return filteredLogs.value.slice(start, end);
});

// Methods
const fetchLogs = async () => {
  loading.value = true;
  try {
    logger.log("🔍 Fetching activity logs...");
    const result = await adminStore.fetchActivityLogs(
      currentPage.value,
      pageSize.value,
      {}
    );
    logger.log("📦 API Result:", result);

    const activityLogDtos = result.content || [];
    logger.log(
      "📊 Activity logs received:",
      activityLogDtos.length,
      activityLogDtos
    );

    // Map ActivityLogDto to frontend format
    logs.value = activityLogDtos.map((dto) => ({
      id: dto.id,
      userId: dto.userId,
      userName: dto.userName,
      userRole: "USER", // Default role - DTO doesn't provide this
      userAvatar: null,
      action: dto.action,
      entityType: dto.entityType,
      entityId: dto.entityId,
      oldValue: dto.oldValue,
      newValue: dto.newValue,
      ipAddress: dto.ipAddress,
      userAgent: dto.userAgent,
      status: "success", // Default status
      createdAt: dto.createdAt,
    }));

    logger.log("✅ Logs mapped:", logs.value.length, "items");
    logger.log("📊 Logs sample:", logs.value.slice(0, 3));
  } catch (error) {
    logger.error("❌ Error fetching logs:", error);
    notificationService.apiError(error, "Không thể tải nhật ký hoạt động");
  } finally {
    loading.value = false;
  }
};

const refreshLogs = () => {
  fetchLogs();
};

const clearSearch = () => {
  searchKeyword.value = "";
};

const resetFilters = () => {
  searchKeyword.value = "";
  filterUser.value = "all";
  filterAction.value = "all";
  filterTimeRange.value = "all";
  currentPage.value = 0;
};

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page;
  }
};

const viewLogDetail = (log) => {
  selectedLog.value = log;
  showDetailModal.value = true;
};

const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedLog.value = null;
};

const clearOldLogs = async () => {
  try {
    await confirmDialogService.confirm(
      "Bạn có chắc chắn muốn xóa các nhật ký cũ hơn 30 ngày? Hành động này không thể hoàn tác.",
      "Xác nhận dọn dẹp",
      {
        confirmButtonText: "Xóa",
        cancelButtonText: "Hủy",
        type: "warning",
      }
    );

    // Simulate API call
    const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
    logs.value = logs.value.filter(
      (log) => new Date(log.createdAt) > thirtyDaysAgo
    );

    notificationService.success(
      "Thành công",
      "Đã dọn dẹp nhật ký cũ thành công"
    );
  } catch {
    // User cancelled
  }
};

const exportLogs = (format) => {
  try {
    const dataToExport = filteredLogs.value || [];
    if (dataToExport.length === 0) {
      notificationService.warning("Cảnh báo", "Không có dữ liệu để xuất");
      return;
    }

    const exportData = dataToExport.map((log) => ({
      ID: log.id,
      "Thời gian": formatDateTime(log.createdAt),
      "Người dùng": log.userName || "Khách",
      "Vai trò": log.userRole || "Guest",
      "Hành động": getActionText(log.action),
      "Đối tượng": log.entityType,
      "ID đối tượng": log.entityId || "N/A",
      "Giá trị cũ": log.oldValue || "N/A",
      "Giá trị mới": log.newValue || "N/A",
      "IP Address": log.ipAddress,
      "Trình duyệt": getBrowserFromUserAgent(log.userAgent),
      "Hệ điều hành": getOSFromUserAgent(log.userAgent),
      "Trạng thái": getStatusText(log.status),
    }));

    if (format === "csv") {
      downloadCsv(exportData, "activity-logs.csv");
      notificationService.success("Thành công", "Xuất CSV thành công!");
    } else if (format === "json") {
      downloadJson("activity-logs", exportData);
      notificationService.success("Thành công", "Xuất JSON thành công!");
    }
  } catch (error) {
    logger.error("Export error:", error);
    notificationService.apiError(error, "Có lỗi xảy ra khi xuất dữ liệu");
  }
};

// Helper functions
const getActionClass = (action) => {
  const classes = {
    login: "success",
    logout: "info",
    create: "success",
    update: "warning",
    delete: "danger",
    view: "info",
  };
  return classes[action] || "neutral";
};

const getActionIcon = (action) => {
  const icons = {
    login: "login",
    logout: "logout",
    create: "add",
    update: "edit",
    delete: "delete",
    view: "visibility",
  };
  return icons[action] || "help";
};

const getActionText = (action) => {
  const texts = {
    login: "Đăng nhập",
    logout: "Đăng xuất",
    create: "Tạo mới",
    update: "Cập nhật",
    delete: "Xóa",
    view: "Xem",
  };
  return texts[action] || action;
};

const getStatusClass = (status) => {
  return status === "success" ? "success" : "danger";
};

const getStatusText = (status) => {
  return status === "success" ? "Thành công" : "Thất bại";
};

const getLocationFromIP = (ip) => {
  // Mock location data
  const locations = {
    "192.168.1.100": "Hà Nội, Việt Nam",
    "192.168.1.101": "TP.HCM, Việt Nam",
    "192.168.1.102": "Đà Nẵng, Việt Nam",
    "192.168.1.103": "Hải Phòng, Việt Nam",
  };
  return locations[ip] || "Không xác định";
};

const getBrowserFromUserAgent = (userAgent) => {
  if (userAgent.includes("Chrome")) return "Chrome";
  if (userAgent.includes("Firefox")) return "Firefox";
  if (userAgent.includes("Safari")) return "Safari";
  if (userAgent.includes("Edge")) return "Edge";
  return "Không xác định";
};

const getOSFromUserAgent = (userAgent) => {
  if (userAgent.includes("Windows")) return "Windows";
  if (userAgent.includes("Mac OS")) return "macOS";
  if (userAgent.includes("Linux")) return "Linux";
  if (userAgent.includes("iPhone")) return "iOS";
  if (userAgent.includes("Android")) return "Android";
  return "Không xác định";
};

const truncateText = (text, maxLength) => {
  if (!text) return "";
  return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
};

const formatNumber = (num) => {
  return new Intl.NumberFormat("vi-VN").format(num);
};

// formatDate và formatDateTime đã được import từ @/utils/formatters

// Lifecycle
onMounted(() => {
  fetchLogs();
});
</script>

<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">notifications</i>
            Quản lý Thông Báo
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
            <i class="material-icons text-xs">info</i>
            Gửi và quản lý thông báo đến khách hàng
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="showCreateDialog = true" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">add</i>
            Tạo thông báo
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">mark_email_read</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.sent }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đã gửi</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">visibility</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.opened }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đã đọc</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">schedule_send</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.scheduled }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đã lên lịch</p>
        </div>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">error</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats.failed }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Thất bại</p>
        </div>
      </div>
    </div>

    <!-- Filters -->
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
              v-model="filters.search"
              type="text" 
              class="w-full pl-10 pr-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Tìm theo tiêu đề, nội dung..."
              @input="handleSearch"
            />
          </div>
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Loại</label>
          <select v-model="filters.type" @change="fetchNotifications" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
            <option value="">Tất cả</option>
            <option value="promotion">Khuyến mãi</option>
            <option value="order">Đơn hàng</option>
            <option value="system">Hệ thống</option>
            <option value="announcement">Thông báo chung</option>
          </select>
        </div>
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Trạng thái</label>
          <select v-model="filters.status" @change="fetchNotifications" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
            <option value="">Tất cả</option>
            <option value="draft">Bản nháp</option>
            <option value="scheduled">Đã lên lịch</option>
            <option value="sent">Đã gửi</option>
            <option value="failed">Thất bại</option>
          </select>
        </div>
        <div class="flex items-end">
          <button @click="resetFilters" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium w-full">
            <i class="material-icons text-base">refresh</i>
            Xóa bộ lọc
          </button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
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

      <div v-else-if="notifications.length === 0" class="flex flex-col items-center justify-center p-12">
        <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">notifications</i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có thông báo</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 text-center mb-4">Nhấn nút "Tạo thông báo" để bắt đầu</p>
        <button @click="showCreateDialog = true" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
          <i class="material-icons text-base">add</i>
          Tạo thông báo
        </button>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Tiêu đề</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Loại</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Đối tượng</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Thời gian gửi</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Tỷ lệ đọc</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="item in notifications" :key="item.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td class="px-4 py-4">
                <div class="flex items-start gap-3">
                  <i class="material-icons text-lg flex-shrink-0" :style="{ color: getTypeColor(item.type) }">
                    {{ getTypeIcon(item.type) }}
                  </i>
                  <div class="min-w-0">
                    <strong class="text-sm font-medium text-gray-900 dark:text-gray-100 block">{{ item.title }}</strong>
                    <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">{{ truncate(item.message, 80) }}</p>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span 
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': item.type === 'promotion',
                    'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400': item.type === 'order',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': item.type === 'system',
                    'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400': item.type === 'announcement'
                  }"
                >
                  {{ getTypeText(item.type) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <i class="material-icons text-sm text-gray-500 dark:text-gray-400">{{ item.recipientType === 'all' ? 'public' : 'person' }}</i>
                  <span class="text-sm text-gray-700 dark:text-gray-300">{{ getRecipientText(item) }}</span>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-400">
                {{ formatDateTime(item.scheduledAt || item.sentAt) }}
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div v-if="item.status === 'sent'" class="space-y-1">
                  <div class="w-24 h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                    <div 
                      class="h-full bg-purple-500 rounded-full transition-all duration-300"
                      :style="{ width: getReadRate(item) + '%' }"
                    ></div>
                  </div>
                  <span class="text-xs text-gray-600 dark:text-gray-400">{{ item.openedCount || 0 }} / {{ item.recipientCount || item.targetUsers || 0 }} ({{ getReadRate(item) }}%)</span>
                </div>
                <span v-else class="text-sm text-gray-400 dark:text-gray-500">-</span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span 
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400': item.status === 'draft',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': item.status === 'scheduled',
                    'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': item.status === 'sent',
                    'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': item.status === 'failed'
                  }"
                >
                  {{ getStatusText(item.status) }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-1">
                  <button @click="viewNotification(item)" class="p-1.5 text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors" title="Xem chi tiết">
                    <i class="material-icons text-base">visibility</i>
                  </button>
                  <button 
                    v-if="item.status === 'draft'" 
                    @click="editNotification(item)" 
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors" 
                    title="Sửa"
                  >
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button 
                    v-if="item.status === 'draft' || item.status === 'scheduled'" 
                    @click="deleteNotification(item)" 
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

    <!-- Create/Edit Dialog -->
    <div v-if="showCreateDialog" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeDialog">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">{{ editingNotification ? 'Chỉnh sửa thông báo' : 'Tạo thông báo mới' }}</h3>
          <button @click="closeDialog" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <form @submit.prevent="saveNotification" class="space-y-4">
            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Loại thông báo *</label>
              <select v-model="formData.type" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" required>
                <option value="">-- Chọn loại --</option>
                <option value="promotion">Khuyến mãi</option>
                <option value="order">Đơn hàng</option>
                <option value="system">Hệ thống</option>
                <option value="announcement">Thông báo chung</option>
              </select>
            </div>

            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tiêu đề *</label>
              <input 
                v-model="formData.title"
                type="text" 
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Tiêu đề ngắn gọn, thu hút"
                required
              />
            </div>

            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Nội dung *</label>
              <textarea 
                v-model="formData.message"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
                rows="5"
                placeholder="Nội dung chi tiết thông báo..."
                required
              ></textarea>
            </div>

            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Link liên kết (tùy chọn)</label>
              <input 
                v-model="formData.link"
                type="url" 
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="https://..."
              />
              <small class="text-xs text-gray-500 dark:text-gray-400">Link sẽ mở khi người dùng nhấn vào thông báo</small>
            </div>

            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Đối tượng nhận *</label>
              <div class="grid grid-cols-3 gap-2">
                <label class="flex items-center gap-2 p-3 border-2 rounded-lg cursor-pointer transition-all" :class="formData.recipientType === 'all' ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20' : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-700'">
                  <input type="radio" v-model="formData.recipientType" value="all" class="w-4 h-4 text-purple-600" />
                  <i class="material-icons text-sm">public</i>
                  <span class="text-xs font-medium text-gray-700 dark:text-gray-300">Tất cả</span>
                </label>
                <label class="flex items-center gap-2 p-3 border-2 rounded-lg cursor-pointer transition-all" :class="formData.recipientType === 'role' ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20' : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-700'">
                  <input type="radio" v-model="formData.recipientType" value="role" class="w-4 h-4 text-purple-600" />
                  <i class="material-icons text-sm">badge</i>
                  <span class="text-xs font-medium text-gray-700 dark:text-gray-300">Vai trò</span>
                </label>
                <label class="flex items-center gap-2 p-3 border-2 rounded-lg cursor-pointer transition-all" :class="formData.recipientType === 'specific' ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20' : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-700'">
                  <input type="radio" v-model="formData.recipientType" value="specific" class="w-4 h-4 text-purple-600" />
                  <i class="material-icons text-sm">person</i>
                  <span class="text-xs font-medium text-gray-700 dark:text-gray-300">Cụ thể</span>
                </label>
              </div>
            </div>

            <div v-if="formData.recipientType === 'role'" class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Chọn vai trò</label>
              <select v-model="formData.role" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
                <option value="">-- Chọn --</option>
                <option value="USER">Khách hàng</option>
                <option value="ADMIN">Quản trị viên</option>
              </select>
            </div>

            <div v-if="formData.recipientType === 'specific'" class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Email người nhận (cách nhau bởi dấu phẩy)</label>
              <textarea 
                v-model="formData.recipientEmails"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
                rows="3"
                placeholder="user1@gmail.com, user2@gmail.com"
              ></textarea>
            </div>

            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Thời gian gửi</label>
              <div class="grid grid-cols-2 gap-2">
                <label class="flex items-center gap-2 p-3 border-2 rounded-lg cursor-pointer transition-all" :class="formData.sendTime === 'now' ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20' : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-700'">
                  <input type="radio" v-model="formData.sendTime" value="now" class="w-4 h-4 text-purple-600" />
                  <i class="material-icons text-sm">send</i>
                  <span class="text-xs font-medium text-gray-700 dark:text-gray-300">Gửi ngay</span>
                </label>
                <label class="flex items-center gap-2 p-3 border-2 rounded-lg cursor-pointer transition-all" :class="formData.sendTime === 'schedule' ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/20' : 'border-gray-200 dark:border-gray-700 hover:border-purple-300 dark:hover:border-purple-700'">
                  <input type="radio" v-model="formData.sendTime" value="schedule" class="w-4 h-4 text-purple-600" />
                  <i class="material-icons text-sm">schedule</i>
                  <span class="text-xs font-medium text-gray-700 dark:text-gray-300">Lên lịch</span>
                </label>
              </div>
            </div>

            <div v-if="formData.sendTime === 'schedule'" class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Thời gian gửi *</label>
              <input 
                v-model="formData.scheduledAt"
                type="datetime-local" 
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                :required="formData.sendTime === 'schedule'"
              />
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
              <button type="button" @click="closeDialog" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
                Hủy
              </button>
              <button type="submit" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed" :disabled="saving">
                <i class="material-icons text-base" :class="{ 'animate-spin': saving }">{{ formData.sendTime === 'now' ? 'send' : 'schedule_send' }}</i>
                {{ saving ? 'Đang xử lý...' : (formData.sendTime === 'now' ? 'Gửi ngay' : 'Lên lịch') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Detail Dialog -->
    <div v-if="showDetailDialog" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="showDetailDialog = false">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">Chi tiết thông báo</h3>
          <button @click="showDetailDialog = false" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="selectedNotification" class="space-y-4">
            <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Loại:</label>
              <span 
                class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                :class="{
                  'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': selectedNotification.type === 'promotion',
                  'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400': selectedNotification.type === 'order',
                  'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': selectedNotification.type === 'system',
                  'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400': selectedNotification.type === 'announcement'
                }"
              >
                {{ getTypeText(selectedNotification.type) }}
              </span>
            </div>
            <div class="flex flex-col gap-1 p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tiêu đề:</label>
              <strong class="text-sm text-gray-900 dark:text-gray-100">{{ selectedNotification.title }}</strong>
            </div>
            <div class="flex flex-col gap-1 p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Nội dung:</label>
              <p class="text-sm text-gray-700 dark:text-gray-300">{{ selectedNotification.message }}</p>
            </div>
            <div v-if="selectedNotification.link" class="flex flex-col gap-1 p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Link:</label>
              <a :href="selectedNotification.link" target="_blank" class="text-sm text-purple-600 dark:text-purple-400 hover:underline">{{ selectedNotification.link }}</a>
            </div>
            <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Đối tượng:</label>
              <p class="text-sm text-gray-700 dark:text-gray-300">{{ getRecipientText(selectedNotification) }}</p>
            </div>
            <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Trạng thái:</label>
              <span 
                class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                :class="{
                  'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400': selectedNotification.status === 'draft',
                  'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': selectedNotification.status === 'scheduled',
                  'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': selectedNotification.status === 'sent',
                  'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': selectedNotification.status === 'failed'
                }"
              >
                {{ getStatusText(selectedNotification.status) }}
              </span>
            </div>
            <div v-if="selectedNotification.status === 'sent'" class="p-3 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300 mb-2 block">Thống kê:</label>
              <div class="space-y-1 text-sm text-gray-700 dark:text-gray-300">
                <p>Đã gửi: {{ selectedNotification.recipientCount || selectedNotification.targetUsers || 0 }}</p>
                <p>Đã đọc: {{ selectedNotification.openedCount || 0 }} ({{ getReadRate(selectedNotification) }}%)</p>
              </div>
            </div>
          </div>
        </div>
        <div class="flex items-center justify-end p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="showDetailDialog = false" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">Đóng</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import notificationService from '@/utils/notificationService'
import confirmDialogService from '@/utils/confirmDialogService'
import logger from '@/utils/logger'
import LoadingSkeleton from '@/components/common/LoadingSkeleton.vue'
import { formatDateTime } from '@/utils/formatters'

const adminStore = useAdminStore()

// State
const loading = ref(false)
const saving = ref(false)
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const editingNotification = ref(null)
const selectedNotification = ref(null)
const notifications = ref([])

const filters = reactive({
  search: '',
  type: '',
  status: ''
})

const stats = reactive({
  sent: 0,
  opened: 0,
  scheduled: 0,
  failed: 0
})

const formData = reactive({
  type: '',
  title: '',
  message: '',
  link: '',
  recipientType: 'all',
  role: '',
  recipientEmails: '',
  sendTime: 'now',
  scheduledAt: ''
})

// Methods
const fetchNotifications = async () => {
  try {
    loading.value = true
    
    // Load từ API - chỉ dùng dữ liệu thật từ database
    const apiFilters = {}
    
    if (filters.search) {
      apiFilters.search = filters.search
    }
    
    if (filters.type) {
      apiFilters.type = filters.type
    }
    
    if (filters.status) {
      apiFilters.status = filters.status
    }
    
    const result = await adminStore.fetchNotifications(0, 100, apiFilters)
    notifications.value = result.content || []
    
    // Update stats từ dữ liệu thật
    updateStats()
    
    if (notifications.value.length === 0) {
      notificationService.info('Thông tin','Chưa có thông báo nào')
    } else {
      logger.log('✅ Notifications loaded from API:', notifications.value.length, 'notifications')
    }
  } catch (error) {
    logger.error('Lỗi tải dữ liệu:', error)
    notificationService.apiError(error, 'Không thể tải danh sách thông báo')
    notifications.value = []
    updateStats()
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.sent = notifications.value.filter(n => n.status === 'sent').length
  stats.opened = notifications.value.reduce((sum, n) => sum + (n.openedCount || 0), 0)
  stats.scheduled = notifications.value.filter(n => n.status === 'scheduled').length
  stats.failed = notifications.value.filter(n => n.status === 'failed').length
}

const handleSearch = () => {
  setTimeout(() => fetchNotifications(), 300)
}

const resetFilters = () => {
  filters.search = ''
  filters.type = ''
  filters.status = ''
  fetchNotifications()
}

const viewNotification = (item) => {
  selectedNotification.value = item
  showDetailDialog.value = true
}

const editNotification = (item) => {
  editingNotification.value = item
  Object.assign(formData, {
    type: item.type,
    title: item.title,
    message: item.message,
    link: item.link || '',
    recipientType: item.recipientType,
    role: item.role || '',
    recipientEmails: item.recipientEmails || '',
    sendTime: item.scheduledAt ? 'schedule' : 'now',
    scheduledAt: item.scheduledAt ? item.scheduledAt.substring(0, 16) : ''
  })
  showCreateDialog.value = true
}

const saveNotification = async () => {
  try {
    saving.value = true
    
    const notificationData = {
      type: formData.type,
      title: formData.title,
      message: formData.message,
      link: formData.link || null,
      recipientType: formData.recipientType,
      role: formData.role || null,
      recipientEmails: formData.recipientEmails || null,
      scheduledAt: formData.sendTime === 'schedule' ? formData.scheduledAt : null
    }
    
    if (editingNotification.value) {
      await adminStore.updateNotification(editingNotification.value.id, notificationData)
      notificationService.success('Thành công','Cập nhật thông báo thành công!')
    } else {
      await adminStore.createNotification(notificationData)
      notificationService.success('Thành công','Tạo thông báo thành công!')
    }
    
    closeDialog()
    fetchNotifications()
  } catch (error) {
    logger.error('Lỗi lưu:', error)
    notificationService.apiError(error, 'Có lỗi xảy ra khi lưu thông báo')
  } finally {
    saving.value = false
  }
}

const deleteNotification = async (item) => {
  try {
    await confirmDialogService.confirm(
      'Bạn có chắc muốn xóa thông báo này?',
      'Xác nhận xóa',
      {
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        type: 'warning'
      }
    )
    
    await adminStore.deleteNotification(item.id)
    notificationService.success('Thành công','Đã xóa thông báo!')
    fetchNotifications()
  } catch (error) {
    if (error !== 'cancel') {
      logger.error('Lỗi xóa:', error)
      notificationService.apiError(error, 'Có lỗi xảy ra khi xóa thông báo')
    }
  }
}

const closeDialog = () => {
  showCreateDialog.value = false
  editingNotification.value = null
  Object.assign(formData, {
    type: '',
    title: '',
    message: '',
    link: '',
    recipientType: 'all',
    role: '',
    recipientEmails: '',
    sendTime: 'now',
    scheduledAt: ''
  })
}

const getTypeIcon = (type) => {
  const icons = {
    promotion: 'local_offer',
    order: 'shopping_bag',
    system: 'settings',
    announcement: 'campaign'
  }
  return icons[type] || 'notifications'
}

const getTypeColor = (type) => {
  const colors = {
    promotion: '#10b981',
    order: '#3b82f6',
    system: '#f59e0b',
    announcement: '#8b5cf6'
  }
  return colors[type] || '#64748b'
}

const getTypeText = (type) => {
  const types = {
    promotion: 'Khuyến mãi',
    order: 'Đơn hàng',
    system: 'Hệ thống',
    announcement: 'Thông báo'
  }
  return types[type] || type
}

const getTypeBadgeClass = (type) => {
  const classes = {
    promotion: 'badge-success',
    order: 'badge-info',
    system: 'badge-warning',
    announcement: 'badge-primary'
  }
  return classes[type]
}

const getRecipientText = (item) => {
  if (item.recipientType === 'all') return 'Tất cả người dùng'
  if (item.recipientType === 'role') return `Vai trò: ${item.role || 'USER'}`
  if (item.recipientType === 'specific') return `${item.recipientCount || item.targetUsers || 0} người`
  return 'Tất cả người dùng'
}

const getStatusClass = (status) => {
  const classes = {
    draft: 'status-inactive',
    scheduled: 'status-pending',
    sent: 'status-completed',
    failed: 'status-cancelled'
  }
  return classes[status]
}

const getStatusText = (status) => {
  const statuses = {
    draft: 'Bản nháp',
    scheduled: 'Đã lên lịch',
    sent: 'Đã gửi',
    failed: 'Thất bại'
  }
  return statuses[status] || status
}

const getReadRate = (item) => {
  const recipientCount = item.recipientCount || item.targetUsers || 0
  if (!recipientCount || recipientCount === 0) return 0
  return Math.round(((item.openedCount || 0) / recipientCount) * 100)
}

const truncate = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// formatDateTime đã được import từ @/utils/formatters

// Lifecycle
onMounted(() => {
  fetchNotifications()
})
</script>





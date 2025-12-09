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
              >assignment_return</i
            >
            Quản lý Trả Hàng
          </h1>
          <p
            class="text-sm text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1"
          >
            <i class="material-icons text-xs">info</i>
            Quản lý yêu cầu trả hàng và hoàn tiền
          </p>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">pending_actions</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.pendingReturns }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Chờ duyệt</p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">local_shipping</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.inTransit }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">
            Đang vận chuyển
          </p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.completed }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đã hoàn thành</p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">cancel</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ stats.rejected }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Từ chối</p>
        </div>
      </div>
    </div>

    <!-- Filters -->
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
              v-model="filters.search"
              @input="handleSearch"
              type="text"
              placeholder="Tìm theo mã đơn, khách hàng..."
              class="w-full pl-10 pr-10 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
            <button
              v-if="filters.search"
              @click="
                filters.search = '';
                handleSearch();
              "
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
              <i class="material-icons text-sm">check_circle</i>
              Trạng thái
            </label>
            <select
              v-model="filters.status"
              @change="fetchReturns"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            >
              <option value="">Tất cả</option>
              <option value="pending">Chờ xử lý</option>
              <option value="approved">Đã duyệt</option>
              <!-- <option value="in_transit">Đang vận chuyển</option> -->
              <!-- <option value="received">Đã nhận hàng</option> -->
              <option value="completed">Hoàn thành</option>
              <option value="rejected">Từ chối</option>
            </select>
          </div>

          <div class="flex flex-col gap-1">
            <label
              class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1"
            >
              <i class="material-icons text-sm">info</i>
              Lý do
            </label>
            <select
              v-model="filters.reason"
              @change="fetchReturns"
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            >
              <option value="">Tất cả</option>
              <option value="defective">Sản phẩm lỗi</option>
              <option value="not_as_described">Không đúng mô tả</option>
              <option value="wrong_item">Giao sai sản phẩm</option>
              <option value="wrong_size">Sai kích cỡ</option>
              <option value="size_issue">Không vừa size</option>
              <option value="change_of_mind">Đổi ý</option>
              <option value="damaged">Hư hỏng khi vận chuyển</option>
              <option value="other">Lý do khác</option>
            </select>
          </div>

          <button
            class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium mt-6"
            @click="resetFilters"
          >
            <i class="material-icons text-base">refresh</i>
            Reset
          </button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <!-- Loading State -->
      <div
        v-if="loading"
        class="flex flex-col items-center justify-center p-12"
      >
        <div
          class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"
        ></div>
        <p class="text-sm text-gray-600 dark:text-gray-400">
          Đang tải dữ liệu...
        </p>
      </div>

      <!-- Empty State -->
      <div
        v-else-if="returns.length === 0"
        class="flex flex-col items-center justify-center p-12"
      >
        <div
          class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4"
        >
          <i
            class="material-icons text-purple-600 dark:text-purple-400 text-3xl"
            >assignment_return</i
          >
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
          Chưa có yêu cầu trả hàng
        </h3>
        <p class="text-sm text-gray-500 dark:text-gray-400 text-center">
          Danh sách yêu cầu trả hàng sẽ hiển thị ở đây
        </p>
      </div>

      <!-- Returns Table -->
      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Mã đơn hàng
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Khách hàng
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Sản phẩm
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Lý do
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Số tiền
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Ngày yêu cầu
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Trạng thái
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider"
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody
            class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700"
          >
            <tr
              v-for="item in returns"
              :key="item.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors"
            >
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex flex-col">
                  <strong
                    class="text-sm font-semibold text-gray-900 dark:text-gray-100"
                  >
                    {{ item.orderNumber }}
                  </strong>
                  <span class="text-xs text-gray-500 dark:text-gray-400">
                    #{{ item.orderId }}
                  </span>
                </div>
              </td>
              <td class="px-4 py-4">
                <div class="flex flex-col">
                  <strong
                    class="text-sm font-medium text-gray-900 dark:text-gray-100"
                    >{{ item.customerName }}</strong
                  >
                  <p class="text-xs text-gray-500 dark:text-gray-400">
                    {{ item.customerEmail }}
                  </p>
                </div>
              </td>
              <td class="px-4 py-4">
                <div
                  v-if="item.items && item.items.length > 0"
                  class="space-y-1"
                >
                  <div
                    v-for="(it, idx) in item.items.slice(0, 1)"
                    :key="idx"
                    class="flex items-center gap-3"
                  >
                    <img
                      :src="it.productImage"
                      :alt="it.productName"
                      class="w-10 h-10 object-cover rounded-lg border border-gray-200 dark:border-gray-700"
                    />
                    <div>
                      <strong
                        class="text-sm text-gray-900 dark:text-gray-100"
                        >{{ it.productName }}</strong
                      >
                      <p class="text-xs text-gray-500 dark:text-gray-400">
                        {{ it.variant }}
                      </p>
                      <p class="text-xs text-gray-500 dark:text-gray-400">
                        SL: {{ it.quantity }}
                      </p>
                    </div>
                  </div>

                  <!-- Nếu có nhiều hơn 2 sản phẩm -->
                  <p v-if="item.items.length > 1" class="text-xs text-gray-400">
                    +{{ item.items.length - 1 }} sản phẩm khác
                  </p>
                </div>

                <!-- Fallback nếu bị null -->
                <div v-else class="text-xs text-gray-400">
                  Không có sản phẩm
                </div>
              </td>
              <td class="px-4 py-4">
                <span
                  class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400"
                  >{{ getReasonText(item.reason, false) }}</span
                >
                <p
                  v-if="item.note"
                  class="text-xs text-gray-500 dark:text-gray-400 mt-1"
                >
                  {{ item.note }}
                </p>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <strong
                  class="text-sm font-semibold text-gray-900 dark:text-gray-100"
                  >{{ formatPrice(item.refundAmount) }}</strong
                >
              </td>
              <td
                class="px-4 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-400"
              >
                {{ formatDate(item.createdAt) }}
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <span
                    class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full"
                    :class="getReturnStatusBadgeClass(item.status)"
                  >
                    {{ getReturnStatusText(item.status) }}
                  </span>
                </div>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <button
                    @click="viewReturnDetail(item)"
                    class="p-1.5 text-gray-600 dark:text-gray-400 hover:text-purple-600 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20 rounded-lg transition-colors"
                    title="Xem chi tiết"
                  >
                    <i class="material-icons text-base">visibility</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Detail Dialog -->
    <div
      v-if="showDetailDialog"
      class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
      @click="showDetailDialog = false"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto scrollbar-hide border border-gray-200 dark:border-gray-700"
        @click.stop
      >
        <div
          class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10"
        >
          <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">
            Chi tiết yêu cầu trả hàng #{{ selectedReturn?.id }}
          </h3>
          <button
            @click="showDetailDialog = false"
            class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
          >
            <i class="material-icons text-xl">close</i>
          </button>
        </div>
        <div class="p-4">
          <div v-if="selectedReturn" class="space-y-6">
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4
                class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3"
              >
                Thông tin đơn hàng
              </h4>
              <div class="space-y-2 text-sm">
                <p class="text-gray-700 dark:text-gray-300">
                  <strong>Mã đơn:</strong> #{{ selectedReturn.orderNumber }}
                </p>
                <p class="text-gray-700 dark:text-gray-300">
                  <strong>Khách hàng:</strong> {{ selectedReturn.customerName }}
                </p>
                <p class="text-gray-700 dark:text-gray-300">
                  <strong>Email:</strong> {{ selectedReturn.customerEmail }}
                </p>
                <p class="text-gray-700 dark:text-gray-300">
                  <strong>SĐT:</strong> {{ selectedReturn.customerPhone }}
                </p>
              </div>
            </div>
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4
                class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3"
              >
                Sản phẩm trong yêu cầu trả hàng
              </h4>

              <table class="w-full text-sm">
                <thead
                  class="text-gray-600 dark:text-gray-300 border-b dark:border-gray-700"
                >
                  <tr>
                    <th class="py-2 text-left">Sản phẩm</th>
                    <th class="py-2 text-left">Biến thể</th>
                    <th class="py-2 text-center">SL</th>
                    <th class="py-2 text-center">SL Hỏng</th>
                    <th class="py-2 text-right">Giá</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(it, idx) in selectedReturn.items"
                    :key="idx"
                    class="border-b dark:border-gray-700"
                  >
                    <td class="py-2 dark:text-white">
                      <div class="flex items-center gap-2 dark:text-white">
                        <img
                          :src="it.productImage"
                          class="w-10 h-10 rounded-lg border dark:border-gray-700"
                        />
                        {{ it.productName }}
                      </div>
                    </td>
                    <td class="py-2 dark:text-white">{{ it.variant }}</td>
                    <td class="py-2 text-center dark:text-white">
                      {{ it.quantity }}
                    </td>
                    <!-- INPUT SL Hỏng -->
                    <td class="py-2 text-center">
                      <!-- Nếu trạng thái chưa hoàn thì cho nhập -->
                      <template v-if="selectedReturn.status !== 'completed'">
                        <input
                          type="number"
                          v-model.number="it.damagedQuantity"
                          min="0"
                          :max="it.quantity"
                          @input="validateDamaged(it)"
                          class="w-16 px-2 py-1 text-xs rounded border border-gray-300 dark:text-white dark:border-gray-600 bg-white dark:bg-gray-700 text-center"
                        />
                      </template>

                      <!-- Nếu completed: chỉ hiển thị -->
                      <template v-else>
                        <span
                          :class="[
                            'px-2 py-1 text-xs font-semibold rounded',
                            it.damagedQuantity > 0
                              ? 'text-red-600 bg-red-100 dark:bg-red-900/30 dark:text-red-300'
                              : 'text-gray-600 dark:text-gray-300',
                          ]"
                        >
                          {{ it.damagedQuantity }}
                        </span>
                      </template>
                    </td>
                    <td class="py-2 text-right dark:text-white">
                      {{ formatPrice(it.unitPrice) }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4
                class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3"
              >
                Lý do trả hàng
              </h4>
              <div class="space-y-2 text-sm">
                <p class="text-gray-700 dark:text-gray-300">
                  <strong>Lý do:</strong>
                  {{ getReasonText(selectedReturn.reason, false) }}
                </p>
                <p class="text-gray-700 dark:text-gray-300">
                  <strong>Ghi chú:</strong>
                  {{ extractNote(selectedReturn.reason) || "Không có" }}
                </p>
              </div>
            </div>
            <div
              v-if="selectedReturn.images && selectedReturn.images.length > 0"
              class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg"
            >
              <h4
                class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3"
              >
                Hình ảnh đính kèm
              </h4>
              <div class="flex gap-2 flex-wrap">
                <img
                  v-for="(img, index) in selectedReturn.images"
                  :key="index"
                  :src="img"
                  alt="Return image"
                  class="w-24 h-24 object-cover rounded-lg border border-gray-200 dark:border-gray-700 cursor-pointer hover:opacity-80 transition-opacity"
                  loading="lazy"
                  decoding="async"
                />
              </div>
            </div>
            <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
              <h4
                class="text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3"
              >
                Thông tin hoàn tiền
              </h4>

              <div class="space-y-2 text-sm">
                <!-- Số tiền hoàn -->
                <p class="text-gray-700 dark:text-gray-300">
                  <strong>Số tiền:</strong>
                  {{ formatPrice(selectedReturn.refundAmount) }}
                </p>

                <!-- Phương thức hoàn tiền -->
                <p class="text-gray-700 dark:text-gray-300">
                  <strong>Phương thức:</strong>
                  {{ getReturnMethodText(selectedReturn.returnMethod) }}
                </p>

                <!-- Ngân hàng -->
                <p
                  v-if="selectedReturn.bankName"
                  class="text-gray-700 dark:text-gray-300"
                >
                  <strong>Ngân hàng:</strong>
                  {{ selectedReturn.bankName }}
                </p>

                <!-- Số tài khoản -->
                <p
                  v-if="selectedReturn.bankAccountNumber"
                  class="text-gray-700 dark:text-gray-300"
                >
                  <strong>Số tài khoản:</strong>
                  <span class="font-mono tracking-wide">
                    {{ selectedReturn.bankAccountNumber }}
                  </span>
                </p>

                <!-- Chủ tài khoản -->
                <p
                  v-if="selectedReturn.bankAccountHolder"
                  class="text-gray-700 dark:text-gray-300"
                >
                  <strong>Chủ tài khoản:</strong>
                  <span class="uppercase">
                    {{ selectedReturn.bankAccountHolder }}
                  </span>
                </p>
              </div>
            </div>
          </div>
        </div>
        <div
          class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700"
        >
          <button
            @click="showDetailDialog = false"
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
          >
            Đóng
          </button>
          <button
            @click="submitReturnItems"
            :disabled="['completed', 'pending'].includes(selectedReturn.status)"
            :title="
              ['completed', 'pending'].includes(selectedReturn.status)
                ? 'Yêu cầu đang xử lý hoặc đã hoàn tất — không thể chỉnh sửa'
                : ''
            "
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-purple-600 dark:bg-purple-700 rounded-lg hover:bg-purple-700 dark:hover:bg-purple-600 transition-colors"
          >
            <i class="material-icons text-base">check_circle</i>
            Xác nhận
          </button>
        </div>
      </div>
    </div>

    <!-- Status Change Confirmation Dialog -->
    <ConfirmDialog
      v-model="showStatusConfirm"
      type="warning"
      title="Xác nhận thay đổi trạng thái"
      :message="`Bạn có chắc chắn muốn thay đổi trạng thái yêu cầu trả hàng #${
        returnToUpdate?.id
      } từ '${getReturnStatusText(
        oldReturnStatus
      )}' sang '${getReturnStatusText(newReturnStatus)}'?`"
      description="Hành động này sẽ cập nhật trạng thái yêu cầu trả hàng."
      confirm-text="Xác nhận"
      cancel-text="Hủy"
      :loading="updating"
      @confirm="handleReturnStatusUpdate"
      @cancel="handleCancelReturnStatusChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useAdminStore } from "@/stores/admin";
import notificationService from "@/utils/notificationService";
import logger from "@/utils/logger";
import { formatPrice, formatDate, formatDateTime } from "@/utils/formatters";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";

const adminStore = useAdminStore();

// State
const loading = ref(false);
const showDetailDialog = ref(false);
const selectedReturn = ref(null);
const returns = ref([]);

// Status change confirmation
const showStatusConfirm = ref(false);
const returnToUpdate = ref(null);
const oldReturnStatus = ref("");
const newReturnStatus = ref("");
const updating = ref(false);
const rejectReason = ref("");
const filters = reactive({
  search: "",
  status: "",
  reason: "",
});

const stats = reactive({
  pendingReturns: 0,
  inTransit: 0,
  completed: 0,
  rejected: 0,
});

// Định nghĩa flow steps cho Return
// Flow: Chờ xử lý -> Đã xác nhận -> Đang xử lý -> Đã hoàn tiền (hoặc Đã đóng gói)
const RETURN_STATUS_STEPS = {
  pending: {
    label: "Chờ xử lý",
    next: ["approved"], // Chỉ có thể chuyển đến "Đã xác nhận"
    previous: null,
    isFinal: false,
  },
  approved: {
    label: "Đã xác nhận",
    next: ["processing"], // Chỉ có thể chuyển đến "Đang xử lý"
    previous: "pending",
    isFinal: false,
  },
  processing: {
    label: "Đang xử lý",
    next: ["refunded"], // Có 2 option: Đã hoàn tiền hoặc Đã đóng gói
    previous: "approved",
    isFinal: false,
  },
  packed: {
    label: "Đã đóng gói",
    next: ["refunded"], // Chỉ có thể chuyển đến "Đã hoàn tiền"
    previous: "processing",
    isFinal: false,
  },
  refunded: {
    label: "Đã hoàn tiền",
    next: null, // Bước cuối cùng, không thể chuyển tiếp
    previous: null,
    isFinal: true,
  },
  rejected: {
    label: "Từ chối",
    next: null,
    previous: null,
    isFinal: true,
  },
};

// Methods
const fetchReturns = async () => {
  try {
    loading.value = true;

    const apiFilters = {
      search: filters.search || undefined,
      status: filters.status || undefined,
      reason: filters.reason || undefined,
    };

    // 👇 chỉ gọi adminStore, đúng yêu cầu của bạn
    const result = await adminStore.fetchReturns(0, 50, apiFilters);

    returns.value = result.content || [];
    updateStats();
  } catch (error) {
    logger.error("Lỗi tải dữ liệu:", error);
    notificationService.apiError(
      error,
      "Lỗi khi tải danh sách yêu cầu trả hàng"
    );
  } finally {
    loading.value = false;
  }
};

const updateStats = () => {
  stats.pendingReturns = returns.value.filter(
    (r) => r.status === "pending"
  ).length;
  stats.inTransit = returns.value.filter(
    (r) => r.status === "in_transit"
  ).length;
  stats.completed = returns.value.filter(
    (r) => r.status === "completed"
  ).length;
  stats.rejected = returns.value.filter((r) => r.status === "rejected").length;
};

const handleSearch = () => {
  setTimeout(() => fetchReturns(), 300);
};

const resetFilters = () => {
  filters.search = "";
  filters.status = "";
  filters.reason = "";
  fetchReturns();
};

const viewReturnDetail = (item) => {
  selectedReturn.value = item;
  showDetailDialog.value = true;
};

// Lấy các bước tiếp theo có thể chuyển đến
const getReturnNextSteps = (currentStatus) => {
  const step = RETURN_STATUS_STEPS[currentStatus];
  return step ? step.next : [];
};

// Lấy bước trước đó (nếu có)
const getReturnPreviousStep = (currentStatus) => {
  const step = RETURN_STATUS_STEPS[currentStatus];
  return step ? step.previous : null;
};

// Kiểm tra xem có thể chuyển đến status mới không
const canChangeReturnToStatus = (currentStatus, targetStatus) => {
  const step = RETURN_STATUS_STEPS[currentStatus];

  if (targetStatus === "rejected") {
    return true;
  }

  if (!step || step.isFinal) {
    return false; // Không thể chuyển từ bước cuối
  }

  // Kiểm tra targetStatus có trong danh sách next không
  if (step.next && step.next.includes(targetStatus)) {
    return true;
  }

  // Cho phép quay lại bước trước
  if (step.previous === targetStatus) {
    return true;
  }

  // Nếu đang ở "Đã đóng gói", có thể chuyển đến "Đã hoàn tiền"
  if (currentStatus === "packed" && targetStatus === "refunded") {
    return true;
  }

  return false;
};

const confirmStatusChange = (returnItem, targetStatus) => {
  try {
    if (!targetStatus) {
      return;
    }

    // Kiểm tra xem có thể chuyển đổi không
    if (!canChangeReturnToStatus(returnItem.status, targetStatus)) {
      notificationService.warning(
        "Cảnh báo",
        "Không thể chuyển đổi trạng thái này. Vui lòng chuyển đổi theo thứ tự bước."
      );
      return;
    }

    // If no change, do nothing
    if (returnItem.status === targetStatus) {
      return;
    }

    // Normalize cả hai để so sánh đúng
    oldReturnStatus.value = returnItem.status;
    newReturnStatus.value = targetStatus;

    // Store return reference
    returnToUpdate.value = { ...returnItem };

    // Show confirmation dialog
    showStatusConfirm.value = true;
  } catch (error) {
    logger.error("❌ Error in confirmStatusChange:", error);
    notificationService.apiError(
      error,
      "Có lỗi xảy ra khi thay đổi trạng thái"
    );
  }
};

const handleReturnStatusUpdate = async () => {
  const returnId = returnToUpdate.value.id;
  const previousStatus = oldReturnStatus.value;

  try {
    updating.value = true;

    await adminStore.updateReturnStatus(
      returnId,
      newReturnStatus.value,
      newReturnStatus.value === "rejected" ? rejectReason.value : ""
    );

    await fetchReturns();

    notificationService.success(
      "Thành công",
      `Đã cập nhật trạng thái yêu cầu trả hàng #${returnId} từ '${getReturnStatusText(
        previousStatus
      )}' sang '${getReturnStatusText(newReturnStatus.value)}' thành công!`
    );

    showStatusConfirm.value = false;
    rejectReason.value = ""; // reset
  } catch (error) {
    logger.error("❌ Lỗi khi cập nhật trạng thái:", error);
    notificationService.apiError(
      error,
      "Không thể cập nhật trạng thái yêu cầu trả hàng"
    );
  } finally {
    updating.value = false;
  }
};

const handleCancelReturnStatusChange = () => {
  // Restore the old status
  if (returnToUpdate.value) {
    returnToUpdate.value.status = oldReturnStatus.value;
  }
  showStatusConfirm.value = false;
};

const rejectReturn = (item) => {
  const reason = prompt("Lý do từ chối:");
  if (!reason) return;

  rejectReason.value = reason.trim();

  confirmStatusChange(item, "rejected");
};

const getReasonText = (reason, includeNote = true) => {
  if (!reason) return reason;

  // Parse reason: if it contains "\n\nGhi chú:", split it
  const noteSeparator = "\n\nGhi chú:";
  const reasonCode = reason.includes(noteSeparator)
    ? reason.split(noteSeparator)[0].trim()
    : reason.trim();

  // Map reason code to Vietnamese text
  const reasons = {
    defective: "Sản phẩm lỗi",
    not_as_described: "Không đúng mô tả",
    wrong_item: "Giao sai sản phẩm",
    wrong_size: "Sai kích cỡ",
    size_issue: "Không vừa size",
    change_of_mind: "Đổi ý",
    damaged: "Hư hỏng khi vận chuyển",
    other: "Lý do khác",
  };

  const vietnameseReason = reasons[reasonCode] || reasonCode;

  // If there's a note and includeNote is true, include it
  if (includeNote && reason.includes(noteSeparator)) {
    const note = reason.split(noteSeparator)[1].trim();
    return `${vietnameseReason}\n\nGhi chú: ${note}`;
  }

  return vietnameseReason;
};

// const canEditReturnItems = (status) => {
//   return status === "approved" || status === "processing";
// };

const validateDamaged = (it) => {
  if (!it.damagedQuantity || it.damagedQuantity < 0) {
    it.damagedQuantity = 0;
  }
  if (it.damagedQuantity > it.quantity) {
    it.damagedQuantity = it.quantity;
  }
};

const submitReturnItems = async () => {
  const payload = {
    returnRequestId: selectedReturn.value.id,
    items: selectedReturn.value.items.map((it) => {
      const damaged = it.damagedQuantity || 0;
      const good = Math.max(it.quantity - damaged, 0); // tránh số âm

      return {
        variantId: it.variantId,
        damagedQuantity: damaged,
        goodQuantity: good,
      };
    }),
  };

  try {
    await adminStore.confirmReturnConditions(payload);
    notificationService.success(
      "Xác nhận thành công",
      "Đã cập nhật tình trạng sản phẩm trả về!"
    );
    showDetailDialog.value = false;
    fetchReturns();
  } catch (error) {
    notificationService.apiError(
      error,
      "Có lỗi khi xác nhận tình trạng sản phẩm"
    );
  }
};

const getReturnMethodText = (method) => {
  if (!method) return "Chưa xác định";

  const map = {
    refund: "Hoàn tiền tài khoản ngân hàng",
    exchange: "Đổi sản phẩm",
  };

  return map[method] || "Không xác định";
};

const extractNote = (reason) => {
  if (!reason) return null;
  const parts = reason.split("\n\nGhi chú:");
  if (parts.length > 1) {
    return parts[1].trim();
  }
  return null;
};

const getReturnStatusBadgeClass = (status) => {
  const classMap = {
    pending:
      "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400",
    approved:
      "bg-indigo-100 text-indigo-800 dark:bg-indigo-900/30 dark:text-indigo-400",
    processing:
      "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400",
    packed:
      "bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400",
    refunded:
      "bg-emerald-100 text-emerald-800 dark:bg-emerald-900/30 dark:text-emerald-400",
    completed:
      "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400",
    rejected:
      "bg-rose-100 text-rose-800 dark:bg-rose-900/30 dark:text-rose-400",
  };
  return (
    classMap[status] ||
    "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300"
  );
};

const getReturnStatusText = (status) => {
  const step = RETURN_STATUS_STEPS[status];
  if (step) {
    return step.label;
  }

  // Fallback cho các status cũ nếu có
  const statuses = {
    pending: "Chờ duyệt",
    approved: "Đã xác nhận",
    processing: "Đang xử lý",
    packed: "Đã đóng gói",
    refunded: "Đã hoàn tiền",
    completed: "Hoàn thành",
    rejected: "Từ chối",
    in_transit: "Đang vận chuyển",
    received: "Đã nhận hàng",
  };
  return statuses[status] || status;
};

// Giữ lại hàm cũ để backward compatibility
const getStatusText = (status) => getReturnStatusText(status);

// formatPrice và formatDate đã được import từ @/utils/formatters

// Lifecycle
onMounted(() => {
  fetchReturns();
});
</script>

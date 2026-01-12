<template>
  <div class="max-w-7xl mx-auto px-4 py-6 space-y-6">
    <!-- Header -->
    <div
      class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6"
    >
      <div
        class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
      >
        <div class="flex items-center gap-4">
          <div
            class="w-20 h-20 rounded-full border-4 border-purple-500 dark:border-purple-600 overflow-hidden bg-gray-200 dark:bg-gray-700 flex items-center justify-center"
          >
            <img
              v-if="userAvatar"
              :src="userAvatar"
              alt="Avatar"
              class="w-full h-full object-cover"
            />
            <svg
              v-else
              width="40"
              height="40"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              class="text-gray-400"
            >
              <path
                d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <circle
                cx="12"
                cy="7"
                r="4"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100">
              Chào mừng trở lại,
              {{ authStore.currentUser?.fullName || "Người dùng" }}! 👋
            </h1>
            <p class="text-gray-600 dark:text-gray-400 mt-1">
              Quản lý tài khoản và đơn hàng của bạn một cách dễ dàng
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md hover:scale-[1.02] transition-all duration-200"
      >
        <div class="flex items-center gap-4">
          <div
            class="w-12 h-12 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center shadow-sm"
          >
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <div>
            <h3 class="text-2xl font-bold text-gray-900 dark:text-gray-100">
              {{ orderStats.totalOrders }}
            </h3>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Tổng đơn hàng
            </p>
          </div>
        </div>
      </div>

      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md hover:scale-[1.02] transition-all duration-200"
      >
        <div class="flex items-center gap-4">
          <div
            class="w-12 h-12 rounded-xl bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center shadow-sm"
          >
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <circle cx="12" cy="12" r="10" stroke="white" stroke-width="2" />
              <polyline
                points="12,6 12,12 16,14"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <div>
            <h3 class="text-2xl font-bold text-gray-900 dark:text-gray-100">
              {{ orderStats.pendingOrders }}
            </h3>
            <p class="text-sm text-gray-500 dark:text-gray-400">Đang xử lý</p>
          </div>
        </div>
      </div>

      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md hover:scale-[1.02] transition-all duration-200"
      >
        <div class="flex items-center gap-4">
          <div
            class="w-12 h-12 rounded-xl bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center shadow-sm"
          >
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <div>
            <h3 class="text-2xl font-bold text-gray-900 dark:text-gray-100">
              {{ orderStats.completedOrders }}
            </h3>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Đã hoàn thành
            </p>
          </div>
        </div>
      </div>

      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md hover:scale-[1.02] transition-all duration-200"
      >
        <div class="flex items-center gap-4">
          <div
            class="w-12 h-12 rounded-xl bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center shadow-sm"
          >
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <line
                x1="12"
                y1="1"
                x2="12"
                y2="23"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M17 5H9.5C8.11929 5 7 6.11929 7 7.5S8.11929 10 9.5 10H14.5C15.8807 10 17 11.1193 17 12.5S15.8807 15 14.5 15H7"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <div>
            <h3 class="text-2xl font-bold text-gray-900 dark:text-gray-100">
              {{ formatCurrency(orderStats.totalSpent) }}
            </h3>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Tổng chi tiêu
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div>
      <h2
        class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
      >
        <i class="material-icons text-purple-600 dark:text-purple-400">bolt</i>
        Thao tác nhanh
      </h2>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <router-link
          to="/user/profile"
          class="group p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:border-purple-500 dark:hover:border-purple-600 hover:scale-[1.02] transition-all duration-200"
        >
          <div
            class="w-12 h-12 rounded-xl bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform"
          >
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <circle
                cx="12"
                cy="7"
                r="4"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-1">
            Thông tin cá nhân
          </h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">
            Xem và chỉnh sửa thông tin tài khoản
          </p>
        </router-link>

        <router-link
          to="/user/orders"
          class="group p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:border-purple-500 dark:hover:border-purple-600 hover:scale-[1.02] transition-all duration-200"
        >
          <div
            class="w-12 h-12 rounded-xl bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform"
          >
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-1">
            Đơn hàng của tôi
          </h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">
            Theo dõi trạng thái đơn hàng
          </p>
        </router-link>

        <router-link
          to="/cart"
          class="group p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:border-purple-500 dark:hover:border-purple-600 hover:scale-[1.02] transition-all duration-200"
        >
          <div
            class="w-12 h-12 rounded-xl bg-gradient-to-br from-pink-500 to-pink-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform"
          >
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <circle
                cx="9"
                cy="21"
                r="1"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <circle
                cx="20"
                cy="21"
                r="1"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M1 1H5L7.68 14.39C7.77144 14.8504 8.02191 15.264 8.38755 15.5583C8.75318 15.8526 9.2107 16.009 9.68 16H19.4C19.8693 16.009 20.3268 15.8526 20.6925 15.5583C21.0581 15.264 21.3086 14.8504 21.4 14.39L23 6H6"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-1">
            Giỏ hàng
          </h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">
            Xem và quản lý sản phẩm đã chọn
          </p>
        </router-link>

        <router-link
          to="/user/wishlist"
          class="group p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:border-purple-500 dark:hover:border-purple-600 hover:scale-[1.02] transition-all duration-200"
        >
          <div
            class="w-12 h-12 rounded-xl bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform"
          >
            <svg
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M20.84 4.61C20.3292 4.099 19.7228 3.69364 19.0554 3.41708C18.3879 3.14052 17.6725 2.99817 16.95 2.99817C16.2275 2.99817 15.5121 3.14052 14.8446 3.41708C14.1772 3.69364 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.5783 8.50903 2.9987 7.05 2.9987C5.59096 2.9987 4.19169 3.5783 3.16 4.61C2.1283 5.6417 1.5487 7.04097 1.5487 8.5C1.5487 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6053C22.3095 9.93789 22.4518 9.22248 22.4518 8.5C22.4518 7.77752 22.3095 7.06211 22.0329 6.39467C21.7563 5.72723 21.351 5.1208 20.84 4.61Z"
                stroke="white"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
          <h3 class="font-semibold text-gray-900 dark:text-gray-100 mb-1">
            Danh sách yêu thích
          </h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">
            Sản phẩm bạn đã lưu
          </p>
        </router-link>
      </div>
    </div>

    <!-- Recent Orders -->
    <div>
      <div class="flex items-center justify-between mb-4">
        <h2
          class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400"
            >history</i
          >
          Đơn hàng gần đây
        </h2>
        <router-link
          to="/user/orders"
          class="flex items-center gap-1 text-sm font-medium text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 transition-colors"
        >
          Xem tất cả
          <svg
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M9 18L15 12L9 6"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </router-link>
      </div>

      <div
        v-if="loading"
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6"
      >
        <div class="space-y-3">
          <div v-for="i in 3" :key="i" class="space-y-2">
            <div
              class="h-4 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"
            ></div>
            <div
              class="h-3 bg-gray-200 dark:bg-gray-700 rounded w-2/3 animate-pulse"
            ></div>
          </div>
        </div>
      </div>

      <div
        v-else-if="recentOrders.length === 0"
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-12 text-center"
      >
        <svg
          width="64"
          height="64"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          class="mx-auto mb-4 text-gray-400"
        >
          <path
            d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
          Chưa có đơn hàng nào
        </h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">
          Bắt đầu mua sắm để xem đơn hàng của bạn ở đây
        </p>
        <router-link
          to="/home/products"
          class="inline-flex items-center gap-2 px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors shadow-lg hover:shadow-xl"
        >
          <svg
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
          Mua sắm ngay
        </router-link>
      </div>

      <div v-else class="space-y-3">
        <div
          v-for="order in recentOrders"
          :key="order.id"
          class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md transition-shadow"
        >
          <div
            class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
          >
            <div class="flex-1">
              <div class="flex items-center justify-between mb-2">
                <h4 class="font-semibold text-gray-900 dark:text-gray-100">
                  #{{ order.id }}
                </h4>
                <span class="text-sm text-gray-500 dark:text-gray-400">{{
                  formatDate(order.createdAt)
                }}</span>
              </div>
              <div class="flex items-center gap-4 text-sm">
                <span class="text-gray-600 dark:text-gray-400"
                  >{{ order.itemCount }} sản phẩm</span
                >
                <span class="font-semibold text-gray-900 dark:text-gray-100">{{
                  formatCurrency(order.total)
                }}</span>
              </div>
            </div>
            <div>
              <span
                :class="[
                  'px-3 py-1 rounded-full text-sm font-medium',
                  getStatusType(order.status) === 'success'
                    ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
                    : getStatusType(order.status) === 'warning'
                    ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'
                    : getStatusType(order.status) === 'danger'
                    ? 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
                    : 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400',
                ]"
              >
                {{ getStatusText(order.status) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Recently Viewed Products -->
    <div>
      <div class="flex items-center justify-between mb-4">
        <h2
          class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
        >
          <i class="material-icons text-purple-600 dark:text-purple-400"
            >visibility</i
          >
          Sản phẩm đã xem gần đây
        </h2>
        <button
          v-if="recentlyViewedProducts.length > 0 || recentlyViewed.length > 0"
          @click="clearRecentlyViewed"
          class="text-sm font-medium text-red-600 dark:text-red-400 hover:text-red-700 dark:hover:text-red-300 transition-colors flex items-center gap-1"
          aria-label="Xóa tất cả sản phẩm đã xem"
        >
          <i class="material-icons text-base">delete_outline</i>
          Xóa tất cả
        </button>
      </div>

      <!-- Loading State -->
      <div
        v-if="loadingRecentlyViewed"
        class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4"
        role="status"
        aria-live="polite"
      >
        <LoadingSkeleton
          v-for="n in 6"
          :key="n"
          type="card"
          :show-image="true"
        />
        <span class="sr-only">Đang tải sản phẩm đã xem</span>
      </div>

      <div
        v-else-if="
          recentlyViewedProducts.length === 0 && recentlyViewed.length === 0
        "
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-12 text-center"
      >
        <svg
          width="48"
          height="48"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          class="mx-auto mb-4 text-gray-400"
        >
          <path
            d="M15 12C15 13.6569 13.6569 15 12 15C10.3431 15 9 13.6569 9 12C9 10.3431 10.3431 9 12 9C13.6569 9 15 10.3431 15 12Z"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M2.45801 12C3.73201 7.943 7.52301 5 12.0001 5C16.4781 5 20.2681 7.943 21.5421 12C20.2681 16.057 16.4781 19 12.0001 19C7.52301 19 3.73201 16.057 2.45801 12Z"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
        <p class="text-gray-600 dark:text-gray-400">Chưa xem sản phẩm nào</p>
      </div>

      <div
        v-else-if="recentlyViewedProducts.length > 0"
        class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4"
      >
        <router-link
          v-for="product in paginatedRecentlyViewed"
          :key="product.id"
          :to="`/home/products/${product.slug}`"
          class="group relative bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-lg hover:scale-105 transition-all duration-200"
        >
          <div
            class="relative aspect-square overflow-hidden bg-gray-100 dark:bg-gray-700"
          >
            <img
              :src="product.imageUrl || '/placeholder-image.png'"
              :alt="product.name"
              class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
              loading="lazy"
              decoding="async"
              @error="
                (e) => {
                  if (!e.target.dataset.failed) {
                    e.target.dataset.failed = true;
                    e.target.src = '/placeholder-image.png';
                  }
                }
              "
            />

            <button
              @click.prevent="removeFromRecentlyViewed(product.id)"
              class="absolute top-2 right-2 w-8 h-8 bg-white/90 dark:bg-gray-800/90 backdrop-blur-sm rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-500 hover:text-white z-10"
              title="Xóa khỏi danh sách"
              aria-label="Xóa sản phẩm khỏi danh sách đã xem"
            >
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <line
                  x1="18"
                  y1="6"
                  x2="6"
                  y2="18"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
                <line
                  x1="6"
                  y1="6"
                  x2="18"
                  y2="18"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            </button>
          </div>
          <div class="p-3">
            <h4
              class="font-semibold text-sm text-gray-900 dark:text-gray-100 mb-1 line-clamp-2"
            >
              {{ product.name }}
            </h4>
            <p
              class="text-xs text-gray-500 dark:text-gray-400 mb-2 line-clamp-1"
            >
              {{ product.brandName }}
            </p>
            <p
              v-if="product.price > 0"
              class="font-bold text-purple-600 dark:text-purple-400 text-sm"
            >
              {{ formatCurrency(product.price) }}
            </p>
            <p v-else class="text-xs text-gray-400 dark:text-gray-500 italic">
              Liên hệ
            </p>
          </div>
        </router-link>
      </div>
      <!-- Dot Pagination -->
      <div
        v-if="rvTotalPages > 1"
        class="flex items-center justify-center gap-2 mt-4 py-3"
      >
        <button
          v-for="page in rvTotalPages"
          :key="page"
          @click="rvCurrentPage = page"
          :class="[
            'w-3 h-3 rounded-full transition-all duration-300',
            rvCurrentPage === page
              ? 'bg-purple-600 dark:bg-purple-400 w-6'
              : 'bg-gray-300 dark:bg-gray-600 hover:bg-gray-400 dark:hover:bg-gray-500',
          ]"
        ></button>
      </div>
    </div>

    <!-- Recommended Products -->
    <div>
      <h2
        class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2"
      >
        <i class="material-icons text-purple-600 dark:text-purple-400">star</i>
        Sản phẩm đề xuất
      </h2>
      <div
        v-if="recommendedProducts.length === 0"
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6"
      >
        <div class="space-y-3">
          <div v-for="i in 2" :key="i" class="space-y-2">
            <div
              class="h-4 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"
            ></div>
            <div
              class="h-3 bg-gray-200 dark:bg-gray-700 rounded w-2/3 animate-pulse"
            ></div>
          </div>
        </div>
      </div>
      <div
        v-else
        class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4"
      >
        <router-link
          v-for="product in recommendedProducts"
          :key="product.id"
          :to="`/home/products/${product.slug}`"
          class="group bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-lg hover:scale-105 transition-all duration-200"
        >
          <div
            class="aspect-square overflow-hidden bg-gray-100 dark:bg-gray-700"
          >
            <img
              :src="product.imageUrl || '/placeholder-image.png'"
              :alt="product.name"
              class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
            />
          </div>
          <div class="p-3">
            <h4
              class="font-semibold text-sm text-gray-900 dark:text-gray-100 mb-1 line-clamp-2"
            >
              {{ product.name }}
            </h4>
            <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">
              {{ product.brandName }}
            </p>
            <p class="font-bold text-purple-600 dark:text-purple-400 text-sm">
              {{ formatCurrency(product.price) }}
            </p>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useRecentlyViewed } from "@/composables/useRecentlyViewed";
import userService from "@/services/userService";
import ProductService from "@/services/productService";
import notificationService from "@/utils/notificationService";
import confirmDialogService from "@/utils/confirmDialogService";
import logger from "@/utils/logger";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import { formatPrice, formatCurrency, formatDate } from "@/utils/formatters";

const authStore = useAuthStore();
const { recentlyViewed, clearAll, removeProduct } = useRecentlyViewed();

// State
const loading = ref(false);
const recentOrders = ref([]);
const recommendedProducts = ref([]);

// Computed
const userAvatar = computed(() => {
  return authStore.currentUser?.avatar || null;
});

const orderStats = ref({
  totalOrders: 0,
  pendingOrders: 0,
  completedOrders: 0,
  totalSpent: 0,
});

// Recently viewed products state
const recentlyViewedProducts = ref([]);
const loadingRecentlyViewed = ref(false);
const productImageMap = ref(new Map());

// Methods
// formatCurrency và formatDate đã được import từ @/utils/formatters

const getStatusType = (status) => {
  const statusMap = {
    PENDING: "warning",
    PROCESSING: "info",
    SHIPPED: "primary",
    DELIVERED: "success",
    CANCELLED: "danger",
  };
  return statusMap[status] || "info";
};

const getStatusText = (status) => {
  const statusMap = {
    PENDING: "Chờ xử lý",
    PROCESSING: "Đang xử lý",
    SHIPPED: "Đang giao",
    DELIVERED: "Đã giao",
    CANCELLED: "Đã hủy",
  };
  return statusMap[status] || status;
};

// Pagination Recently Viewed
const rvCurrentPage = ref(1);
const rvPageSize = 6;

const rvTotalPages = computed(() => {
  return Math.ceil(recentlyViewedProducts.value.length / rvPageSize);
});

const paginatedRecentlyViewed = computed(() => {
  const start = (rvCurrentPage.value - 1) * rvPageSize;
  return recentlyViewedProducts.value.slice(start, start + rvPageSize);
});

// Load và enrich recently viewed products với data từ API
const loadRecentlyViewedProducts = async () => {
  if (loadingRecentlyViewed.value) return;

  if (recentlyViewed.value.length === 0) {
    recentlyViewedProducts.value = [];
    return;
  }

  loadingRecentlyViewed.value = true;

  try {
    const productIds = recentlyViewed.value.map((p) => p.id);

    const productPromises = productIds.map((id) =>
      ProductService.getProductById(id).catch(() => null)
    );

    const products = await Promise.all(productPromises);

    const newProducts = products
      .filter((p) => p !== null)
      .map((product) => {
        const price = getProductPrice(product);

        // let imageUrl = null;
        // if (product.variants?.[0]?.imageUrl) {
        //   imageUrl = product.variants[0].imageUrl;
        // } else if (product.images?.length > 0) {
        //   const img = product.images[0];
        //   imageUrl = typeof img === "string" ? img : img.url || img.imageUrl;
        // } else if (product.imageUrl) {
        //   imageUrl = product.imageUrl;
        // } else if (product.mainImage) {
        //   imageUrl = product.mainImage;
        // }
        const imageUrl =
          productImageMap.value.get(product.id) || "/placeholder-image.png";

        // return {
        //   id: product.id,
        //   name: product.name,
        //   slug: product.slug,
        //   brandName: product.brand?.name || product.brandName || "Unknown",
        //   imageUrl: imageUrl || "/placeholder-image.png",
        //   price,
        //   viewedAt:
        //     recentlyViewed.value.find((p) => p.id === product.id)?.viewedAt ||
        //     new Date().toISOString(),
        // };

        // ⭐ NEW: Lấy categories từ backend
        const categoryIds = product.categories?.map((c) => c.id) || [];
        const categoryNames = product.categories?.map((c) => c.name) || [];
        const firstCategory = categoryNames[0] || null;

        return {
          id: product.id,
          name: product.name,
          slug: product.slug,
          brandName: product.brand?.name || product.brandName || "Unknown",
          imageUrl,
          price,
          categoryIds,
          categoryNames,
          firstCategory,
          viewedAt:
            recentlyViewed.value.find((p) => p.id === product.id)?.viewedAt ||
            new Date().toISOString(),
        };
      });

    if (
      JSON.stringify(newProducts) !==
      JSON.stringify(recentlyViewedProducts.value)
    ) {
      recentlyViewedProducts.value = newProducts;
    }
  } catch (err) {
    recentlyViewedProducts.value = recentlyViewed.value.map((p) => ({
      ...p,
      imageUrl: p.imageUrl || "/placeholder-image.png",
      price: p.price || 0,
    }));
  } finally {
    loadingRecentlyViewed.value = false;
  }
};

const loadProductImages = async () => {
  try {
    const res = await ProductService.getAllProductImages();

    const map = new Map();

    res.data.forEach((item) => {
      if (item.productId && item.images?.length > 0) {
        map.set(item.productId, item.images[0]); // ảnh đầu tiên
      }
    });

    productImageMap.value = map;

    logger.log(
      "✅ Product image map loaded:",
      productImageMap.value.size,
      "items"
    );
  } catch (err) {
    logger.error("❌ Error loading product image map:", err);
  }
};

const clearRecentlyViewed = async () => {
  try {
    await confirmDialogService.confirm(
      "Bạn có chắc muốn xóa tất cả sản phẩm đã xem?",
      "Xác nhận xóa",
      {
        confirmButtonText: "Xóa tất cả",
        cancelButtonText: "Hủy",
        type: "warning",
      }
    );
    clearAll();
    recentlyViewedProducts.value = [];
    notificationService.success("Thành công", "Đã xóa tất cả sản phẩm đã xem");
  } catch (error) {
    if (error !== "cancel") {
      logger.error("Error clearing recently viewed:", error);
    }
  }
};

const removeFromRecentlyViewed = async (productId) => {
  try {
    removeProduct(productId);
    recentlyViewedProducts.value = recentlyViewedProducts.value.filter(
      (p) => p.id !== productId
    );
    notificationService.success("Thành công", "Đã xóa sản phẩm khỏi danh sách");
  } catch (error) {
    logger.error("Error removing product from recently viewed:", error);
  }
};

const loadDashboardData = async () => {
  loading.value = true;
  try {
    // Load orders từ API - chỉ dùng dữ liệu thật từ database
    try {
      // Lấy orders của user
      const allOrders = (await userService.getMyOrders()) || [];

      // Tính toán order stats từ dữ liệu thật
      const totalOrders = allOrders.length;
      const pendingOrders = allOrders.filter((o) =>
        ["pending", "processing", "confirmed", "packed", "shipped"].includes(
          o.status?.toLowerCase()
        )
      ).length;
      const completedOrders = allOrders.filter(
        (o) => o.status?.toLowerCase() === "delivered"
      ).length;
      const totalSpent = allOrders
        .filter((o) => o.status?.toLowerCase() === "delivered")
        .reduce((sum, o) => sum + (o.totalAmount || o.total || 0), 0);

      orderStats.value = {
        totalOrders,
        pendingOrders,
        completedOrders,
        totalSpent,
      };

      // Lấy 5 orders gần nhất
      recentOrders.value = allOrders
        .sort(
          (a, b) =>
            new Date(b.createdAt || b.created_at) -
            new Date(a.createdAt || a.created_at)
        )
        .slice(0, 5)
        .map((order) => ({
          id: order.orderNumber || order.id,
          createdAt: order.createdAt || order.created_at,
          itemCount: order.totalItems || order.orderDetails?.length || 0,
          total: order.totalAmount || order.total || 0,
          status: order.status?.toUpperCase() || "PENDING",
        }));

      logger.log("✅ Orders loaded from API:", totalOrders, "orders");
    } catch (error) {
      logger.error("Error loading orders:", error);
      orderStats.value = {
        totalOrders: 0,
        pendingOrders: 0,
        completedOrders: 0,
        totalSpent: 0,
      };
      recentOrders.value = [];
      if (error.response?.status !== 401) {
        notificationService.warning(
          "Cảnh báo",
          "Không thể tải thông tin đơn hàng"
        );
      }
    }

    // try {
    //   // Load recommended products từ API - lấy top sản phẩm
    //   const productsResponse = await ProductService.getProducts(0, 6);
    //   const products =
    //     productsResponse.data?.content || productsResponse.data || [];

    //   recommendedProducts.value = products.map((product) => ({
    //     id: product.id,
    //     name: product.name,
    //     brandName: product.brand?.name || product.brandName || "Unknown",
    //     price: product.price || product.priceBase || 0,
    //     imageUrl:
    //       productImageMap.value.get(product.id) || "/placeholder-image.png",
    //     slug: product.slug,
    //   }));

    //   if (recommendedProducts.value.length === 0) {
    //     notificationService.info("Thông tin", "Chưa có sản phẩm đề xuất");
    //   } else {
    //     logger.log(
    //       "✅ Recommended products loaded from API:",
    //       recommendedProducts.value.length,
    //       "products"
    //     );
    //   }
    // } catch (error) {
    //   logger.error("Error loading recommended products:", error);
    //   recommendedProducts.value = [];
    //   notificationService.warning("Cảnh báo", "Không thể tải sản phẩm đề xuất");
    // }
  } catch (error) {
    logger.error("Error loading dashboard data:", error);
    notificationService.warning("Cảnh báo", "Không thể tải dữ liệu dashboard");
  } finally {
    loading.value = false;
  }
};

// Watch recentlyViewed changes to reload products
watch(
  () => recentlyViewed.value.map((p) => p.id).join(","),
  () => {
    loadRecentlyViewedProducts();
  }
);

// Lifecycle
// onMounted(async () => {
//   await loadDashboardData();
//   // Load recently viewed products after dashboard data
//   await loadRecentlyViewedProducts();
// });

const getProductPrice = (p) => {
  if (p.variants?.length > 0) {
    const v = p.variants[0];
    return v.priceSale || v.priceBase || 0;
  }
  return p.priceSale || p.priceBase || p.price || 0;
};

// 🔥 Load recommended products theo 3 tiêu chí:
// 1) Brand → 2) Category → 3) Random fallback
const loadRecommendedProducts = async () => {
  try {
    if (!recentlyViewedProducts.value.length) {
      recommendedProducts.value = [];
      return;
    }

    // Lấy sản phẩm xem gần nhất
    const lastViewed = [...recentlyViewedProducts.value].sort(
      (a, b) => new Date(b.viewedAt) - new Date(a.viewedAt)
    )[0];

    const brand = lastViewed.brandName;
    const productId = lastViewed.id;
    const category = lastViewed.firstCategory || null; // <- bạn phải map thêm ở loadRecentlyViewed

    let results = [];

    // -------------------------
    // 1️⃣ Lọc theo BRAND
    // -------------------------
    if (brand) {
      const resBrand = await ProductService.searchProducts({
        brand: brand,
        page: 0,
        size: 20,
      });

      const brandProducts =
        resBrand?.data?.content || resBrand?.content || resBrand || [];

      results.push(...brandProducts);
    }

    // -------------------------
    // 2️⃣ Lọc theo CATEGORY
    // -------------------------
    if (results.length < 6 && category) {
      const resCate = await ProductService.searchProducts({
        category: category,
        page: 0,
        size: 20,
      });

      const cateProducts = (resCate.content || []).filter(
        (p) => p.id !== productId && !results.some((x) => x.id === p.id)
      );

      results.push(...cateProducts);
    }

    // -------------------------
    // 3️⃣ Lấy random fallback
    // -------------------------
    if (results.length < 6) {
      const resRandom = await ProductService.getProducts(0, 20);
      const randomData = resRandom.data?.content || [];

      const extra = randomData.filter(
        (p) => p.id !== productId && !results.some((x) => x.id === p.id)
      );

      results.push(...extra);
    }

    // -------------------------
    // 4️⃣ Giới hạn 6 sản phẩm
    // -------------------------
    results = results.slice(0, 6);

    // -------------------------
    // 5️⃣ Chuẩn hóa output
    // -------------------------
    recommendedProducts.value = results.map((product) => ({
      id: product.id,
      name: product.name,
      slug: product.slug,
      brandName: product.brand?.name || product.brandName,
      price: getProductPrice(product),
      imageUrl:
        productImageMap.value.get(product.id) || "/placeholder-image.png",
    }));
  } catch (error) {
    console.error("❌ Error loading recommended products:", error);
    recommendedProducts.value = [];
  }
};

watch(
  () => recentlyViewedProducts.value,
  () => loadRecommendedProducts(),
  { deep: true }
);

onMounted(async () => {
  await loadProductImages(); // ✅ Load toàn bộ ảnh trước
  await loadDashboardData();
  await loadRecentlyViewedProducts();
  await loadRecommendedProducts();
});
</script>

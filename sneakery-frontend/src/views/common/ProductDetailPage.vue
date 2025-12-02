<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <div
          class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-purple-600 border-t-transparent mb-4"
        ></div>
        <p class="text-gray-600 dark:text-gray-400">
          Đang tải thông tin sản phẩm...
        </p>
      </div>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="flex items-center justify-center min-h-screen"
    >
      <div
        class="text-center bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-12 max-w-md"
      >
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">
          ❌ Không tìm thấy sản phẩm
        </h2>
        <p class="text-gray-600 dark:text-gray-400 mb-6">{{ error }}</p>
        <button
          @click="router.push('/home/products')"
          class="px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors"
        >
          Quay lại danh sách sản phẩm
        </button>
      </div>
    </div>

    <!-- Product Detail -->
    <div v-else-if="product" class="max-w-7xl mx-auto px-4">
      <!-- Breadcrumb -->
      <nav
        class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 mb-6"
      >
        <router-link
          to="/"
          class="hover:text-purple-600 dark:hover:text-purple-400 transition-colors"
          >Trang chủ</router-link
        >
        <span>/</span>
        <router-link
          to="/home/products"
          class="hover:text-purple-600 dark:hover:text-purple-400 transition-colors"
          >Sản phẩm</router-link
        >
        <span>/</span>
        <span class="text-gray-900 dark:text-gray-100">{{ product.name }}</span>
      </nav>

      <!-- Product Main Info -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-12">
        <!-- PRODUCT GALLERY -->
        <div
          class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6"
        >
          <!-- Ảnh lớn -->
          <div
            class="relative aspect-square mb-4 rounded-lg overflow-hidden bg-gray-100 dark:bg-gray-700"
          >
            <img
              :src="activeImage || product.imageUrl"
              :alt="product.name"
              class="w-full h-full object-cover"
            />
          </div>

          <!-- Thumbnails -->
          <div class="flex gap-2 overflow-x-auto">
            <img
              v-for="(img, i) in variantImages"
              :key="i"
              :src="img.imageUrl"
              class="w-20 h-20 object-cover rounded-lg border-2 cursor-pointer transition"
              :class="
                activeImage === img.imageUrl
                  ? 'border-purple-600'
                  : 'border-gray-300 hover:border-purple-400'
              "
              @click="activeImage = img.imageUrl"
            />
          </div>
        </div>

        <!-- Product Info -->
        <div
          class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6"
        >
          <!-- Header -->
          <div class="flex items-center justify-between mb-4">
            <span
              class="px-3 py-1 bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400 rounded-lg text-sm font-semibold"
              >{{ product.brandName }}</span
            >
            <div class="flex items-center gap-2">
              <div class="flex items-center gap-1">
                <span
                  v-for="n in 5"
                  :key="n"
                  :class="[
                    'text-lg',
                    n <= averageRating ? 'text-yellow-400' : 'text-gray-300',
                  ]"
                  >★</span
                >
              </div>
              <span class="text-sm text-gray-600 dark:text-gray-400"
                >({{ reviews.length }} đánh giá)</span
              >
            </div>
          </div>

          <h1 class="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-4">
            {{ product.name }}
          </h1>

          <!-- Price Section -->
          <div class="flex items-center gap-4 mb-6">
            <span
              class="text-3xl font-bold text-purple-600 dark:text-purple-400"
              >{{ formatPrice(currentPrice) }}</span
            >
            <span
              v-if="originalPrice > currentPrice"
              class="text-xl text-gray-500 line-through"
              >{{ formatPrice(originalPrice) }}</span
            >
            <span
              v-if="originalPrice > currentPrice"
              class="px-3 py-1 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 rounded-lg text-sm font-semibold"
            >
              -{{
                Math.round(
                  ((originalPrice - currentPrice) / originalPrice) * 100
                )
              }}%
            </span>
          </div>

          <!-- Description -->
          <div class="mb-6 text-gray-600 dark:text-gray-400">
            <p>{{ product.description || "Chưa có mô tả chi tiết." }}</p>
          </div>

          <!-- Color Selection -->
          <div class="mb-6">
            <label
              class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
            >
              Màu sắc:
              <span class="text-purple-600 dark:text-purple-400">{{
                selectedColor || "Chọn màu"
              }}</span>
            </label>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="color in availableColors"
                :key="color"
                :class="[
                  'px-4 py-2 rounded-lg border-2 font-medium transition-all',
                  selectedColor === color
                    ? 'bg-purple-600 text-white border-purple-600'
                    : 'bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 border-gray-200 dark:border-gray-600 hover:border-purple-300 dark:hover:border-purple-600',
                ]"
                @click="selectColor(color)"
              >
                {{ color }}
              </button>
            </div>
          </div>

          <!-- Size Selection -->
          <div class="mb-6">
            <label
              class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
            >
              Kích cỡ:
              <span class="text-purple-600 dark:text-purple-400">{{
                selectedSize || "Chọn size"
              }}</span>
            </label>
            <div class="flex flex-wrap gap-2">
              <div class="size-options">
                <button
                  v-for="size in availableSizes"
                  :key="size"
                  @click="sizeStatus[size] === 'ok' && selectSize(size)"
                  :disabled="sizeStatus[size] !== 'ok'"
                  :class="[
                    'size-btn',
                    sizeStatus[size] === 'ok'
                      ? selectedSize === size
                        ? 'selected'
                        : 'valid'
                      : sizeStatus[size] === 'out'
                      ? 'out-stock'
                      : 'invalid',
                  ]"
                >
                  {{ size }}

                  <!-- ❌ Size hết hàng -->
                  <span v-if="sizeStatus[size] === 'out'" class="size-x"
                    >✕</span
                  >

                  <!-- Gạch chéo cho size invalid -->
                  <span
                    v-if="sizeStatus[size] === 'invalid'"
                    class="slash"
                  ></span>
                </button>
              </div>
            </div>
          </div>

          <!-- Stock Status -->
          <div class="mb-6">
            <span
              v-if="selectedVariant && selectedVariant.stockQuantity > 0"
              class="inline-flex items-center gap-2 px-4 py-2 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 rounded-lg"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
              Còn hàng ({{ selectedVariant.stockQuantity }} sản phẩm)
            </span>
            <span
              v-else-if="selectedVariant"
              class="inline-flex items-center gap-2 px-4 py-2 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 rounded-lg"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="15" y1="9" x2="9" y2="15"></line>
                <line x1="9" y1="9" x2="15" y2="15"></line>
              </svg>
              Hết hàng
            </span>
            <span
              v-else
              class="inline-flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg"
            >
              Vui lòng chọn màu và size
            </span>
          </div>

          <!-- Quantity -->
          <div class="mb-6">
            <label
              class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >Số lượng:</label
            >
            <div class="flex items-center gap-3">
              <button
                @click="decreaseQuantity"
                :disabled="quantity <= 1"
                class="w-10 h-10 rounded-lg border border-gray-200 dark:border-gray-600 flex items-center justify-center hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
              </button>
              <input
                v-model.number="quantity"
                type="number"
                min="1"
                :max="maxQuantity"
                class="w-20 h-10 text-center border border-gray-200 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
              />
              <button
                @click="increaseQuantity"
                :disabled="quantity >= maxQuantity"
                class="w-10 h-10 rounded-lg border border-gray-200 dark:border-gray-600 flex items-center justify-center hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
              </button>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-3 mb-6">
            <button
              @click="addToCart"
              :disabled="!canAddToCart"
              class="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <circle cx="9" cy="21" r="1"></circle>
                <circle cx="20" cy="21" r="1"></circle>
                <path
                  d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"
                ></path>
              </svg>
              {{ cartButtonText }}
            </button>
            <button
              @click="buyNow"
              :disabled="!canAddToCart"
              class="flex-1 px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-xl font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Mua ngay
            </button>
          </div>

          <!-- Product Features -->
          <div
            class="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-6 border-t border-gray-200 dark:border-gray-700"
          >
            <div class="flex items-center gap-3">
              <div
                class="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  class="text-purple-600 dark:text-purple-400"
                >
                  <path
                    d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"
                  ></path>
                  <circle cx="12" cy="10" r="3"></circle>
                </svg>
              </div>
              <div>
                <h4
                  class="font-semibold text-gray-900 dark:text-gray-100 text-sm"
                >
                  Giao hàng miễn phí
                </h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">
                  Cho đơn hàng trên 500K
                </p>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <div
                class="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  class="text-purple-600 dark:text-purple-400"
                >
                  <polyline points="23 4 23 10 17 10"></polyline>
                  <polyline points="1 20 1 14 7 14"></polyline>
                  <path
                    d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"
                  ></path>
                </svg>
              </div>
              <div>
                <h4
                  class="font-semibold text-gray-900 dark:text-gray-100 text-sm"
                >
                  Đổi trả 30 ngày
                </h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">
                  Miễn phí đổi trả
                </p>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <div
                class="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  class="text-purple-600 dark:text-purple-400"
                >
                  <rect
                    x="3"
                    y="11"
                    width="18"
                    height="11"
                    rx="2"
                    ry="2"
                  ></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
              </div>
              <div>
                <h4
                  class="font-semibold text-gray-900 dark:text-gray-100 text-sm"
                >
                  Thanh toán bảo mật
                </h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">
                  SSL 256-bit
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Product Tabs -->
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 mb-12"
      >
        <div class="flex border-b border-gray-200 dark:border-gray-700">
          <button
            :class="[
              'px-6 py-4 font-semibold transition-colors border-b-2',
              activeTab === 'specs'
                ? 'border-purple-600 text-purple-600 dark:text-purple-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-purple-600 dark:hover:text-purple-400',
            ]"
            @click="activeTab = 'specs'"
          >
            Thông số kỹ thuật
          </button>
          <button
            :class="[
              'px-6 py-4 font-semibold transition-colors border-b-2',
              activeTab === 'reviews'
                ? 'border-purple-600 text-purple-600 dark:text-purple-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-purple-600 dark:hover:text-purple-400',
            ]"
            @click="activeTab = 'reviews'"
          >
            Đánh giá ({{ reviews.length }})
          </button>
          <button
            :class="[
              'px-6 py-4 font-semibold transition-colors border-b-2',
              activeTab === 'shipping'
                ? 'border-purple-600 text-purple-600 dark:text-purple-400'
                : 'border-transparent text-gray-600 dark:text-gray-400 hover:text-purple-600 dark:hover:text-purple-400',
            ]"
            @click="activeTab = 'shipping'"
          >
            Giao hàng & Đổi trả
          </button>
        </div>

        <div class="p-6">
          <!-- Specifications Tab -->
          <div v-if="activeTab === 'specs'">
            <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4">
              Thông tin chi tiết
            </h3>
            <table class="w-full">
              <tbody>
                <tr class="border-b border-gray-200 dark:border-gray-700">
                  <td class="py-3 text-gray-600 dark:text-gray-400 font-medium">
                    Thương hiệu
                  </td>
                  <td class="py-3 text-gray-900 dark:text-gray-100">
                    {{ product.brandName }}
                  </td>
                </tr>
                <tr class="border-b border-gray-200 dark:border-gray-700">
                  <td class="py-3 text-gray-600 dark:text-gray-400 font-medium">
                    Danh mục
                  </td>
                  <td class="py-3 text-gray-900 dark:text-gray-100">
                    {{ formatCategories(product.categories) }}
                  </td>
                </tr>
                <tr class="border-b border-gray-200 dark:border-gray-700">
                  <td class="py-3 text-gray-600 dark:text-gray-400 font-medium">
                    Màu sắc
                  </td>
                  <td class="py-3 text-gray-900 dark:text-gray-100">
                    {{ availableColors.join(", ") }}
                  </td>
                </tr>
                <tr class="border-b border-gray-200 dark:border-gray-700">
                  <td class="py-3 text-gray-600 dark:text-gray-400 font-medium">
                    Kích cỡ
                  </td>
                  <td class="py-3 text-gray-900 dark:text-gray-100">
                    {{ availableSizes.join(", ") }}
                  </td>
                </tr>
                <tr class="border-b border-gray-200 dark:border-gray-700">
                  <td class="py-3 text-gray-600 dark:text-gray-400 font-medium">
                    Chất liệu
                  </td>
                  <td class="py-3 text-gray-900 dark:text-gray-100">
                    Canvas, Rubber Sole
                  </td>
                </tr>
                <tr>
                  <td class="py-3 text-gray-600 dark:text-gray-400 font-medium">
                    Xuất xứ
                  </td>
                  <td class="py-3 text-gray-900 dark:text-gray-100">
                    Chính hãng 100%
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Reviews Tab -->
          <div v-if="activeTab === 'reviews'">
            <div class="mb-6">
              <h3
                class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-4"
              >
                Đánh giá từ khách hàng
              </h3>
              <div
                class="bg-purple-50 dark:bg-purple-900/20 rounded-xl p-6 text-center"
              >
                <div
                  class="text-4xl font-bold text-purple-600 dark:text-purple-400 mb-2"
                >
                  {{ averageRating.toFixed(1) }}
                </div>
                <div class="flex items-center justify-center gap-1 mb-2">
                  <span
                    v-for="n in 5"
                    :key="n"
                    :class="[
                      'text-2xl',
                      n <= averageRating ? 'text-yellow-400' : 'text-gray-300',
                    ]"
                    >★</span
                  >
                </div>
                <div class="text-sm text-gray-600 dark:text-gray-400">
                  {{ reviews.length }} đánh giá
                </div>
              </div>
            </div>

            <div class="space-y-4" v-if="reviews.length > 0">
              <div
                v-for="review in reviews"
                :key="review.id"
                class="border border-gray-200 dark:border-gray-700 rounded-xl p-4"
              >
                <div class="flex items-start justify-between mb-3">
                  <div class="flex items-center gap-3">
                    <div
                      class="w-10 h-10 rounded-full bg-purple-600 text-white flex items-center justify-center font-semibold"
                    >
                      {{ review.userName.charAt(0).toUpperCase() }}
                    </div>
                    <div>
                      <div
                        class="font-semibold text-gray-900 dark:text-gray-100"
                      >
                        {{ review.userName }}
                      </div>
                      <div class="text-sm text-gray-500 dark:text-gray-400">
                        {{ formatDate(review.createdAt) }}
                      </div>
                    </div>
                  </div>
                  <div class="flex items-center gap-1">
                    <span
                      v-for="n in 5"
                      :key="n"
                      :class="[
                        'text-lg',
                        n <= review.rating
                          ? 'text-yellow-400'
                          : 'text-gray-300',
                      ]"
                      >★</span
                    >
                  </div>
                </div>
                <div class="text-gray-700 dark:text-gray-300">
                  {{ review.comment }}
                </div>
              </div>
            </div>

            <div v-else class="text-center py-12">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="64"
                height="64"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
                class="mx-auto mb-4 text-gray-400"
              >
                <path
                  d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"
                ></path>
              </svg>
              <p class="text-gray-600 dark:text-gray-400 mb-4">
                Chưa có đánh giá nào cho sản phẩm này.
              </p>
              <button
                class="px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-colors"
              >
                Viết đánh giá đầu tiên
              </button>
            </div>
          </div>

          <!-- Shipping Tab -->
          <div v-if="activeTab === 'shipping'">
            <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">
              Chính sách giao hàng & đổi trả
            </h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h4
                  class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-3"
                >
                  📦 Giao hàng
                </h4>
                <ul class="space-y-2 text-gray-600 dark:text-gray-400">
                  <li class="flex items-start gap-2">
                    <span class="text-purple-600 dark:text-purple-400">•</span>
                    <span>Miễn phí giao hàng cho đơn hàng trên 500.000đ</span>
                  </li>
                  <li class="flex items-start gap-2">
                    <span class="text-purple-600 dark:text-purple-400">•</span>
                    <span>Giao hàng tiêu chuẩn: 2-4 ngày làm việc</span>
                  </li>
                  <li class="flex items-start gap-2">
                    <span class="text-purple-600 dark:text-purple-400">•</span>
                    <span
                      >Giao hàng nhanh: 1-2 ngày làm việc (phụ thu
                      30.000đ)</span
                    >
                  </li>
                </ul>
              </div>
              <div>
                <h4
                  class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-3"
                >
                  🔄 Đổi trả
                </h4>
                <ul class="space-y-2 text-gray-600 dark:text-gray-400">
                  <li class="flex items-start gap-2">
                    <span class="text-purple-600 dark:text-purple-400">•</span>
                    <span>Đổi trả miễn phí trong vòng 30 ngày</span>
                  </li>
                  <li class="flex items-start gap-2">
                    <span class="text-purple-600 dark:text-purple-400">•</span>
                    <span>Sản phẩm chưa qua sử dụng, còn nguyên tem mác</span>
                  </li>
                  <li class="flex items-start gap-2">
                    <span class="text-purple-600 dark:text-purple-400">•</span>
                    <span>Hoàn tiền 100% nếu sản phẩm lỗi</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Related Products -->
      <div
        v-if="!loadingRelatedProducts && relatedProducts.length > 0"
        class="mb-12"
      >
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-6">
          Sản phẩm tương tự
        </h2>
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
          <ProductCard
            v-for="relatedProduct in relatedProducts"
            :key="relatedProduct.id"
            :product="relatedProduct"
          />
        </div>
      </div>

      <!-- Loading Related Products -->
      <div v-if="loadingRelatedProducts" class="mb-12">
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-6">
          Sản phẩm tương tự
        </h2>
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
          <div
            v-for="n in 4"
            :key="n"
            class="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden animate-pulse"
          >
            <div class="aspect-square bg-gray-200 dark:bg-gray-700"></div>
            <div class="p-3 space-y-2">
              <div class="h-3 bg-gray-200 dark:bg-gray-700 rounded w-1/2"></div>
              <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
              <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/2"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Zoom Modal -->
    <!-- <div
      v-if="showZoom"
      class="fixed inset-0 z-[9999] bg-black/80 backdrop-blur-sm flex items-center justify-center p-4"
      @click="closeZoom"
    >
      <button
        class="absolute top-4 right-4 w-10 h-10 bg-white rounded-full flex items-center justify-center text-gray-900 hover:bg-gray-100 transition-colors text-2xl"
        @click="closeZoom"
      >
        ×
      </button>
      <div class="max-w-4xl w-full" @click.stop>
        <img
          :src="selectedImage || product.imageUrl"
          :alt="product.name"
          class="w-full h-auto rounded-xl"
        />
      </div>
    </div> -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useCartStore } from "@/stores/cart";
import { useFlashSaleStore } from "@/stores/flashSale";
import { useRecentlyViewed } from "@/composables/useRecentlyViewed";
// import notificationService from "@/utils/notificationService";
import notificationService from "@/utils/notificationService";
import { API_ENDPOINTS } from "@/config/api";
import logger from "@/utils/logger";
import axios from "axios";
import FlashSaleBadge from "@/assets/components/common/FlashSaleBadge.vue";
import productService from "@/services/productService";
import ProductCard from "@/assets/components/products/ProductCard.vue";
import { useProductImageStore } from "@/stores/productImages";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();
const flashSaleStore = useFlashSaleStore();
const { addProduct } = useRecentlyViewed();

// State
const product = ref(null);
const productImages = ref([]); // Gallery images từ ProductImage API
const loading = ref(true);
const error = ref("");
const selectedColor = ref("");
const selectedSize = ref("");
const selectedImage = ref("");
const quantity = ref(1);
const activeTab = ref("specs");
const showZoom = ref(false);
const variantImages = ref([]);
const activeImage = ref(null);
const productImageStore = useProductImageStore();

const resetState = () => {
  product.value = null;
  productImages.value = [];
  selectedColor.value = "";
  selectedSize.value = "";
  selectedImage.value = "";
  quantity.value = 1;
  variantImages.value = [];
  activeImage.value = null;
  error.value = "";
};

// Mock reviews data (replace with real API later)
const reviews = ref([
  {
    id: 1,
    userName: "Nguyễn Văn A",
    rating: 5,
    comment:
      "Sản phẩm rất đẹp và chất lượng. Đóng gói cẩn thận, giao hàng nhanh.",
    createdAt: "2024-12-15T10:30:00",
  },
  {
    id: 2,
    userName: "Trần Thị B",
    rating: 4,
    comment: "Giày đẹp, đúng như hình. Tuy nhiên size hơi nhỏ một chút.",
    createdAt: "2024-12-10T15:20:00",
  },
  {
    id: 3,
    userName: "Lê Văn C",
    rating: 5,
    comment: "Mình đã mua 3 đôi rồi, rất hài lòng về chất lượng!",
    createdAt: "2024-12-05T09:15:00",
  },
]);

// Related products
const relatedProducts = ref([]);
const loadingRelatedProducts = ref(false);

// Fetch product detail
const fetchProduct = async () => {
  try {
    loading.value = true;
    error.value = "";

    // Fetch flash sales if not already loaded
    if (flashSaleStore.activeFlashSales.length === 0) {
      await flashSaleStore.fetchActiveFlashSales();
    }

    const response = await axios.get(
      API_ENDPOINTS.PRODUCTS.BY_SLUG(route.params.slug)
    );

    product.value = response.data;

    // Add to recently viewed
    addProduct({
      ...product.value,
      brandName: product.value.brand?.name || product.value.brandName,
    });

    // Auto-select first variant + load gallery ảnh
    if (product.value.variants && product.value.variants.length > 0) {
      const firstVariant = product.value.variants[0];

      selectedColor.value = firstVariant.color;
      selectedSize.value = firstVariant.size;

      // 🔥 Load toàn bộ ảnh của biến thể đầu tiên
      await fetchVariantImages(firstVariant.id);
    }

    // Fetch related products
    await fetchRelatedProducts();
  } catch (err) {
    logger.error("Error fetching product:", err);
    error.value =
      err.response?.data?.message || "Không thể tải thông tin sản phẩm";
  } finally {
    loading.value = false;
    window.scrollTo({ top: 0, behavior: "smooth" });
  }
};

const availableSizesForColor = computed(() => {
  if (!product.value || !selectedColor.value) return [];

  return product.value.variants
    .filter((v) => v.color === selectedColor.value)
    .map((v) => v.size);
});

const variantByColor = computed(() => {
  if (!product.value || !selectedColor.value) return [];
  return product.value.variants.filter((v) => v.color === selectedColor.value);
});

const sizeStatus = computed(() => {
  if (!product.value) return {};

  let result = {};

  // Lấy toàn bộ size của sản phẩm
  const sizes = [...new Set(product.value.variants.map((v) => v.size))];

  sizes.forEach((size) => {
    // Tìm biến thể tương ứng với màu + size
    const variant = product.value.variants.find(
      (v) => v.size === size && v.color === selectedColor.value
    );

    if (!variant) {
      result[size] = "invalid"; // ❌ Size không tồn tại theo màu
    } else if (variant.stockQuantity <= 0) {
      result[size] = "out"; // ❌ Hết hàng
    } else {
      result[size] = "ok"; // ✔ Valid
    }
  });

  return result;
});

// Computed
const availableColors = computed(() => {
  if (!product.value || !product.value.variants) return [];
  return [...new Set(product.value.variants.map((v) => v.color))];
});

const availableSizes = computed(() => {
  if (!product.value || !product.value.variants) return [];
  return [...new Set(product.value.variants.map((v) => v.size))];
});

const selectedVariant = computed(() => {
  if (!product.value || !product.value.variants) return null;
  return product.value.variants.find(
    (v) => v.color === selectedColor.value && v.size === selectedSize.value
  );
});

// Computed - Flash sale for product
const productFlashSale = computed(() => {
  if (!product.value) return null;
  return flashSaleStore.getFlashSaleForProduct(product.value.id);
});

const currentPrice = computed(() => {
  if (selectedVariant.value) {
    const basePrice =
      selectedVariant.value.priceSale || selectedVariant.value.priceBase;
    // Apply flash sale discount if available
    if (productFlashSale.value) {
      return flashSaleStore.calculateDiscountedPrice(
        basePrice,
        productFlashSale.value.discountPercent
      );
    }
    return basePrice;
  }
  return 0;
});

const originalPrice = computed(() => {
  if (selectedVariant.value) {
    return selectedVariant.value.priceBase;
  }
  return 0;
});

const maxQuantity = computed(() => {
  return selectedVariant.value?.stockQuantity || 1;
});

const canAddToCart = computed(() => {
  return selectedVariant.value && selectedVariant.value.stockQuantity > 0;
});

const cartButtonText = computed(() => {
  if (!selectedColor.value || !selectedSize.value) return "Chọn màu và size";
  if (!selectedVariant.value) return "Không có sẵn";
  if (selectedVariant.value.stockQuantity <= 0) return "Hết hàng";
  return "Thêm vào giỏ hàng";
});

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0;
  const sum = reviews.value.reduce((acc, r) => acc + r.rating, 0);
  return sum / reviews.value.length;
});

// Computed - Gallery images (sorted by displayOrder)
const galleryImages = computed(() => {
  if (productImages.value.length > 0) {
    return productImages.value
      .sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0))
      .map((img) => img.imageUrl);
  }
  return [];
});

// Computed - Optimized main image
const optimizedMainImage = computed(() => {
  const imageUrl =
    selectedImage.value ||
    product.value?.mainImageUrl ||
    product.value?.imageUrl ||
    "";
  if (!imageUrl) return "";
  return getOptimizedImageUrl(imageUrl, {
    width: 800,
    height: 800,
    quality: "auto",
    format: "auto",
  });
});

// Computed - Optimized selected image for zoom
const optimizedSelectedImage = computed(() => {
  const imageUrl =
    selectedImage.value ||
    product.value?.mainImageUrl ||
    product.value?.imageUrl ||
    "";
  if (!imageUrl) return "";
  return getOptimizedImageUrl(imageUrl, {
    width: 1200,
    height: 1200,
    quality: "auto",
    format: "auto",
  });
});

// Helper - Get optimized thumbnail
const getOptimizedThumbnail = (imageUrl) => {
  if (!imageUrl) return "";
  return getOptimizedImageUrl(imageUrl, {
    width: 80,
    height: 80,
    quality: "auto",
    format: "auto",
  });
};

// Fetch related products
const fetchRelatedProducts = async () => {
  if (!product.value) return;

  try {
    loadingRelatedProducts.value = true;

    const brandId = product.value.brand?.id || product.value.brandId;
    const categoryIds =
      product.value.categories?.map((c) => c.id) ||
      product.value.categoryIds ||
      [];

    const related = await productService.getRelatedProducts(
      product.value.id,
      brandId,
      categoryIds,
      4 // Limit to 4 products
    );

    relatedProducts.value = related;
    logger.log("Fetched related products:", related.length);
  } catch (err) {
    logger.error("Error fetching related products:", err);
    relatedProducts.value = [];
  } finally {
    loadingRelatedProducts.value = false;
  }
};

// Methods
const selectImage = (imageUrl) => {
  selectedImage.value = imageUrl;
};

const selectColor = (color) => {
  selectedColor.value = color;

  // lấy các size thuộc màu này
  const sizes = product.value.variants
    .filter((v) => v.color === color && v.stockQuantity > 0)
    .map((v) => v.size);

  if (sizes.length > 0) selectedSize.value = sizes[0];

  // tìm variant tương ứng
  const variant = product.value.variants.find(
    (v) => v.color === selectedColor.value && v.size === selectedSize.value
  );

  if (variant) fetchVariantImages(variant.id);
};

const selectSize = (size) => {
  selectedSize.value = size;

  const variant = product.value.variants.find(
    (v) => v.color === selectedColor.value && v.size === size
  );

  if (variant) fetchVariantImages(variant.id);
};

const increaseQuantity = () => {
  if (quantity.value < maxQuantity.value) {
    quantity.value++;
  }
};

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const openZoom = () => {
  showZoom.value = true;
};

const closeZoom = () => {
  showZoom.value = false;
};

const addToCart = async () => {
  if (!authStore.isAuthenticated) {
    notificationService.warning(
      "Cảnh báo",
      "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng"
    );
    router.push("/login");
    return;
  }

  if (!canAddToCart.value) {
    notificationService.error("Lỗi", "Sản phẩm này hiện không có sẵn");
    return;
  }

  try {
    // Sử dụng cart store để thêm vào giỏ hàng
    await cartStore.addItem(selectedVariant.value.id, quantity.value);
    notificationService.success(
      "Thành công",
      `Đã thêm ${quantity.value} sản phẩm vào giỏ hàng`
    );
    logger.log("Product added to cart:", selectedVariant.value.id);
  } catch (err) {
    logger.error("Error adding to cart:", err);

    // Nếu lỗi 401, yêu cầu đăng nhập
    if (err.response?.status === 401) {
      notificationService.warning(
        "Cảnh báo",
        "Vui lòng đăng nhập để thêm vào giỏ hàng"
      );
      router.push("/login");
    } else {
      notificationService.error(
        "Lỗi",
        err.response?.data?.message || "Không thể thêm vào giỏ hàng"
      );
    }
  }
};

const buyNow = async () => {
  await addToCart();
  router.push("/cart");
};

const fetchVariantImages = async (variantId) => {
  try {
    const res = await axios.get(`/api/variant-images/${variantId}`);
    variantImages.value = res.data || [];

    // Chọn ảnh đầu tiên làm ảnh lớn
    activeImage.value =
      variantImages.value[0]?.imageUrl || product.value.imageUrl;
  } catch (err) {
    console.error("Error loading variant images", err);
    variantImages.value = [];
    activeImage.value = product.value.imageUrl;
  }
};

const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const formatCategories = (categories) => {
  if (!categories || categories.length === 0) return "Chưa phân loại";
  return categories.map((c) => c.name).join(", ");
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString("vi-VN", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};

// Lifecycle
onMounted(() => {
  fetchProduct();
  productImageStore.loadAll();
});

watch(
  () => route.params.slug,
  () => {
    if (route.params.slug) {
      resetState();
      fetchProduct();
    }
  }
);
</script>
<style scoped>
.size-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.size-btn {
  position: relative;
  width: 48px;
  height: 48px;
  border-radius: 8px;
  border: 2px solid #d1d5db;
  background: white;
  font-weight: 600;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

/* ✔ Size hợp lệ, chưa chọn */
.size-btn.valid:hover {
  border-color: #9333ea;
  color: #9333ea;
}

/* ✔ Size được chọn */
.size-btn.selected {
  background: #9333ea;
  border-color: #9333ea;
  color: white;
}

/* ❌ Size hết hàng */
.size-btn.out-stock {
  cursor: not-allowed;
  opacity: 0.5;
  color: #9ca3af;
}

/* Dấu X đỏ */
.size-btn .size-x {
  position: absolute;
  top: 4px;
  right: 6px;
  color: #ef4444;
  font-weight: bold;
  font-size: 18px;
  pointer-events: none;
}

/* ❌ Size không tồn tại theo màu */
.size-btn.invalid {
  cursor: not-allowed;
  background: #f3f4f6;
  color: #9ca3af;
  opacity: 0.6;
  border-color: #e5e7eb;
  position: relative;
}

/* Gạch chéo chéo – chuẩn Nike */
.size-btn .slash {
  position: absolute;
  width: 130%;
  height: 2px;
  background: #9ca3af;
  top: 50%;
  left: -15%;
  transform: rotate(-45deg);
  pointer-events: none;
}
</style>

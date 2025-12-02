<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Hero Banner Section -->
    <section
      class="relative bg-gradient-to-br from-purple-600 via-purple-700 to-indigo-800 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 overflow-hidden"
    >
      <!-- Animated Background Pattern -->
      <div class="absolute inset-0 opacity-10">
        <div
          class="absolute inset-0"
          style="
            background-image: radial-gradient(
              circle,
              rgba(255, 255, 255, 0.1) 1px,
              transparent 1px
            );
            background-size: 50px 50px;
          "
        ></div>
      </div>

      <!-- Floating Orbs -->
      <div
        class="absolute top-20 left-10 w-72 h-72 bg-purple-400 rounded-full mix-blend-multiply filter blur-xl opacity-20 animate-blob"
      ></div>
      <div
        class="absolute top-40 right-10 w-72 h-72 bg-pink-400 rounded-full mix-blend-multiply filter blur-xl opacity-20 animate-blob animation-delay-2000"
      ></div>
      <div
        class="absolute -bottom-8 left-1/2 w-72 h-72 bg-indigo-400 rounded-full mix-blend-multiply filter blur-xl opacity-20 animate-blob animation-delay-4000"
      ></div>

      <div
        class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 md:py-20 lg:py-28 relative z-10"
      >
        <div
          class="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-16 items-center"
        >
          <!-- Hero Content -->
          <div class="text-center lg:text-left space-y-6 animate-fade-in-up">
            <div
              class="inline-flex items-center gap-2 px-4 py-2 bg-white/20 dark:bg-gray-800/40 backdrop-blur-md rounded-full text-white text-sm font-semibold shadow-lg border border-white/20 animate-pulse"
            >
              <i class="material-icons text-orange-400 animate-pulse"
                >local_fire_department</i
              >
              <span>Bán chạy nhất 2025</span>
            </div>

            <h1
              class="text-4xl md:text-5xl lg:text-7xl font-extrabold text-white leading-tight"
            >
              Sneakery Store
              <span
                class="block bg-gradient-to-r from-yellow-300 via-orange-400 to-pink-400 bg-clip-text text-transparent animate-gradient"
              >
                Giày Sneaker Chính Hãng
              </span>
            </h1>

            <p
              class="text-lg md:text-xl text-white/90 dark:text-gray-300 max-w-xl mx-auto lg:mx-0 leading-relaxed"
            >
              Bộ sưu tập giày sneaker đa dạng từ Nike, Adidas, Jordan và nhiều
              thương hiệu nổi tiếng khác.
              <span class="font-semibold">Giá tốt nhất thị trường</span> - Miễn
              phí vận chuyển - Đổi trả dễ dàng.
            </p>

            <div
              class="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start pt-2"
            >
              <router-link
                to="/home/products"
                class="group inline-flex items-center justify-center gap-2 px-8 py-4 bg-white text-purple-700 dark:bg-purple-600 dark:text-white rounded-xl font-bold text-lg hover:bg-gray-50 dark:hover:bg-purple-700 transition-all duration-300 shadow-xl hover:shadow-2xl hover:scale-105 transform"
              >
                <i class="material-icons group-hover:animate-bounce"
                  >shopping_bag</i
                >
                Mua Sắm Ngay
                <i
                  class="material-icons text-sm group-hover:translate-x-1 transition-transform"
                  >arrow_forward</i
                >
              </router-link>
              <router-link
                to="/home/products"
                class="inline-flex items-center justify-center gap-2 px-8 py-4 bg-white/10 dark:bg-gray-800/50 backdrop-blur-md text-white border-2 border-white/30 rounded-xl font-bold text-lg hover:bg-white/20 dark:hover:bg-gray-800/70 transition-all duration-300 shadow-lg hover:shadow-xl"
              >
                <i class="material-icons">explore</i>
                Khám Phá
              </router-link>
            </div>

            <!-- Trust Badges -->
            <div
              class="flex flex-wrap justify-center lg:justify-start gap-4 pt-4"
            >
              <div
                class="flex items-center gap-2 px-4 py-2 bg-white/10 dark:bg-gray-800/30 backdrop-blur-sm rounded-lg text-white/90 text-sm font-medium border border-white/20 hover:bg-white/20 transition-all duration-200"
              >
                <i class="material-icons text-green-400">verified</i>
                <span>100% Chính Hãng</span>
              </div>
              <div
                class="flex items-center gap-2 px-4 py-2 bg-white/10 dark:bg-gray-800/30 backdrop-blur-sm rounded-lg text-white/90 text-sm font-medium border border-white/20 hover:bg-white/20 transition-all duration-200"
              >
                <i class="material-icons text-blue-400">local_shipping</i>
                <span>Miễn Phí Ship</span>
              </div>
              <div
                class="flex items-center gap-2 px-4 py-2 bg-white/10 dark:bg-gray-800/30 backdrop-blur-sm rounded-lg text-white/90 text-sm font-medium border border-white/20 hover:bg-white/20 transition-all duration-200"
              >
                <i class="material-icons text-purple-400">sync</i>
                <span>Đổi Trả 30 Ngày</span>
              </div>
              <div
                class="flex items-center gap-2 px-4 py-2 bg-white/10 dark:bg-gray-800/30 backdrop-blur-sm rounded-lg text-white/90 text-sm font-medium border border-white/20 hover:bg-white/20 transition-all duration-200"
              >
                <i class="material-icons text-orange-400">support_agent</i>
                <span>Hỗ Trợ 24/7</span>
              </div>
            </div>
          </div>

          <!-- Hero Image -->
          <div class="relative hidden lg:block animate-fade-in-right">
            <div
              class="relative bg-white/10 dark:bg-gray-800/40 backdrop-blur-md rounded-3xl p-8 shadow-2xl border border-white/20 animate-float"
            >
              <img
                src="@/assets/images/logo.png"
                alt="Sneakery Store"
                class="w-full h-auto rounded-2xl"
              />

              <!-- Floating Stats Cards -->
              <div
                class="absolute -top-4 -right-4 bg-white dark:bg-gray-800 rounded-2xl p-5 shadow-2xl border border-gray-200 dark:border-gray-700 animate-bounce-slow transform hover:scale-110 transition-transform"
              >
                <div class="flex items-center gap-3">
                  <div
                    class="w-14 h-14 bg-gradient-to-br from-green-400 to-green-600 rounded-xl flex items-center justify-center shadow-lg"
                  >
                    <i class="material-icons text-white text-2xl"
                      >trending_up</i
                    >
                  </div>
                  <div>
                    <div
                      class="text-2xl font-bold text-gray-900 dark:text-gray-100"
                    >
                      1000+
                    </div>
                    <div
                      class="text-xs text-gray-500 dark:text-gray-400 font-medium"
                    >
                      Sản phẩm
                    </div>
                  </div>
                </div>
              </div>

              <div
                class="absolute -bottom-4 -left-4 bg-white dark:bg-gray-800 rounded-2xl p-5 shadow-2xl border border-gray-200 dark:border-gray-700 animate-bounce-slow-delayed transform hover:scale-110 transition-transform"
              >
                <div class="flex items-center gap-3">
                  <div
                    class="w-14 h-14 bg-gradient-to-br from-pink-400 to-pink-600 rounded-xl flex items-center justify-center shadow-lg"
                  >
                    <i class="material-icons text-white text-2xl">star</i>
                  </div>
                  <div>
                    <div
                      class="text-2xl font-bold text-gray-900 dark:text-gray-100"
                    >
                      4.9/5
                    </div>
                    <div
                      class="text-xs text-gray-500 dark:text-gray-400 font-medium"
                    >
                      Đánh giá
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories Section -->
    <section
      class="py-16 md:py-20 bg-white dark:bg-gray-800 relative overflow-hidden"
    >
      <!-- Background Pattern -->
      <div class="absolute inset-0 opacity-5">
        <div
          class="absolute inset-0"
          style="
            background-image: repeating-linear-gradient(
              45deg,
              transparent,
              transparent 10px,
              rgba(147, 51, 234, 0.1) 10px,
              rgba(147, 51, 234, 0.1) 20px
            );
          "
        ></div>
      </div>

      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div class="text-center mb-12">
          <h2
            class="text-3xl md:text-4xl font-extrabold text-gray-900 dark:text-gray-100 mb-3"
          >
            Danh Mục Sản Phẩm
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400">
            Khám phá bộ sưu tập theo danh mục yêu thích
          </p>
        </div>

        <!-- Loading State -->
        <div
          v-if="loadingCategories"
          class="flex justify-center items-center py-12"
        >
          <div class="flex items-center gap-3">
            <div
              class="w-8 h-8 border-4 border-purple-600 border-t-transparent rounded-full animate-spin"
            ></div>
            <span class="text-gray-600 dark:text-gray-400"
              >Đang tải danh mục...</span
            >
          </div>
        </div>

        <!-- Category Groups - Tab Window Design -->
        <div v-else-if="categoryGroups.length > 0" class="max-w-5xl mx-auto">
          <!-- Tab Navigation -->
          <div
            class="flex flex-wrap justify-center gap-2 mb-8 border-b border-gray-200 dark:border-gray-700 pb-4"
          >
            <button
              v-for="(group, index) in categoryGroups"
              :key="group.id"
              @click="activeCategoryTab = index"
              :class="[
                'px-6 py-3 rounded-lg font-semibold text-sm transition-all duration-300 flex items-center gap-2',
                activeCategoryTab === index
                  ? 'bg-purple-600 text-white shadow-lg scale-105'
                  : 'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700',
              ]"
            >
              <i class="material-icons text-lg">{{
                getCategoryIcon({ slug: group.slug, name: group.name })
              }}</i>
              <span>{{ group.name }}</span>
              <span
                :class="[
                  'px-2 py-0.5 rounded-full text-xs font-medium',
                  activeCategoryTab === index
                    ? 'bg-white/20 text-white'
                    : 'bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400',
                ]"
              >
                {{ group.productCount || 0 }}
              </span>
            </button>
          </div>

          <!-- Tab Content - Child Categories -->
          <div
            class="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 p-6 md:p-8 shadow-lg"
          >
            <transition name="fade" mode="out-in">
              <div
                v-if="categoryGroups[activeCategoryTab]"
                :key="activeCategoryTab"
                class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4"
              >
                <router-link
                  v-for="child in categoryGroups[activeCategoryTab].children"
                  :key="child.id"
                  :to="`/home/products?category=${child.slug}`"
                  class="group flex flex-col items-center p-4 rounded-xl border-2 border-gray-200 dark:border-gray-700 hover:border-purple-500 dark:hover:border-purple-600 hover:bg-purple-50 dark:hover:bg-purple-900/20 transition-all duration-300 hover:shadow-md"
                >
                  <div
                    class="w-12 h-12 mb-3 rounded-lg flex items-center justify-center bg-gradient-to-br from-purple-500 to-purple-600 group-hover:from-purple-600 group-hover:to-indigo-600 transition-all duration-300"
                  >
                    <i class="material-icons text-white text-xl">{{
                      getCategoryIcon(child)
                    }}</i>
                  </div>
                  <h4
                    class="font-semibold text-gray-900 dark:text-gray-100 text-sm mb-1 group-hover:text-purple-600 dark:group-hover:text-purple-400 transition-colors"
                  >
                    {{ child.name }}
                  </h4>
                  <p class="text-xs text-gray-500 dark:text-gray-400">
                    {{ child.count || 0 }} sản phẩm
                  </p>
                </router-link>
              </div>
            </transition>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-12">
          <i class="material-icons text-gray-400 text-6xl mb-4">category</i>
          <p class="text-gray-600 dark:text-gray-400">Chưa có danh mục nào</p>
        </div>
      </div>
    </section>

    <!-- Flash Sale / Hot Deals -->
    <section
      v-if="flashSaleStore.hasActiveFlashSales || flashSaleProducts.length > 0"
      class="py-16 md:py-20 bg-gradient-to-r from-orange-500 via-red-500 to-pink-500 dark:from-orange-600 dark:via-red-600 dark:to-pink-600 relative overflow-hidden"
    >
      <!-- Animated Background -->
      <div class="absolute inset-0 opacity-20">
        <div
          class="absolute inset-0 animate-pulse"
          style="
            background-image: repeating-linear-gradient(
              45deg,
              transparent,
              transparent 20px,
              rgba(255, 255, 255, 0.1) 20px,
              rgba(255, 255, 255, 0.1) 40px
            );
          "
        ></div>
      </div>

      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div
          class="bg-white dark:bg-gray-800 rounded-3xl p-8 md:p-10 border-4 border-white/30 shadow-2xl transform hover:scale-[1.02] transition-transform duration-300"
        >
          <div
            class="flex flex-col md:flex-row md:items-center md:justify-between gap-6 mb-8"
          >
            <div class="flex items-center gap-6">
              <div
                class="w-16 h-16 bg-gradient-to-br from-orange-500 to-red-500 rounded-2xl flex items-center justify-center shadow-xl animate-pulse"
              >
                <i class="material-icons text-white text-3xl">flash_on</i>
              </div>
              <div>
                <h2
                  class="text-3xl md:text-4xl font-extrabold text-gray-900 dark:text-gray-100 mb-3"
                >
                  Flash Sale
                </h2>
                <div v-if="nearestEndTime" class="flex items-center gap-3">
                  <span
                    class="text-sm font-medium text-gray-600 dark:text-gray-400"
                    >Kết thúc trong:</span
                  >
                  <div class="flex items-center gap-2">
                    <div
                      class="px-3 py-1.5 bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-600 rounded-lg text-sm font-bold text-gray-900 dark:text-gray-100 shadow-md"
                    >
                      {{ countdown.days }}
                    </div>
                    <span class="text-gray-600 dark:text-gray-400 font-bold"
                      >:</span
                    >
                    <div
                      class="px-3 py-1.5 bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-600 rounded-lg text-sm font-bold text-gray-900 dark:text-gray-100 shadow-md"
                    >
                      {{ countdown.hours }}
                    </div>
                    <span class="text-gray-600 dark:text-gray-400 font-bold"
                      >:</span
                    >
                    <div
                      class="px-3 py-1.5 bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-600 rounded-lg text-sm font-bold text-gray-900 dark:text-gray-100 shadow-md"
                    >
                      {{ countdown.minutes }}
                    </div>
                    <span class="text-gray-600 dark:text-gray-400 font-bold"
                      >:</span
                    >
                    <div
                      class="px-3 py-1.5 bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-600 rounded-lg text-sm font-bold text-gray-900 dark:text-gray-100 shadow-md"
                    >
                      {{ countdown.seconds }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <router-link
              to="/home/flash-sale"
              class="group inline-flex items-center gap-2 px-8 py-4 bg-gradient-to-r from-orange-500 to-red-500 text-white rounded-xl font-bold text-lg hover:from-orange-600 hover:to-red-600 transition-all duration-300 shadow-xl hover:shadow-2xl transform hover:scale-105"
            >
              Xem tất cả
              <i
                class="material-icons group-hover:translate-x-1 transition-transform"
                >arrow_forward</i
              >
            </router-link>
          </div>

          <!-- Flash Sale Products Grid -->
          <div
            v-if="flashSaleProducts.length > 0"
            class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6"
          >
            <div
              v-for="product in flashSaleProducts"
              :key="product.id"
              class="group bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 hover:shadow-2xl hover:scale-105 transition-all duration-300 overflow-hidden transform"
            >
              <ProductCard :product="product" />
            </div>
          </div>
          <div v-else class="text-center py-12">
            <p class="text-gray-600 dark:text-gray-400">
              Đang tải sản phẩm flash sale...
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- Products Section -->
    <section class="py-16 md:py-20 bg-gray-50 dark:bg-gray-900">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2
            class="text-3xl md:text-4xl font-extrabold text-gray-900 dark:text-gray-100 mb-3"
          >
            Sản Phẩm Nổi Bật
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400">
            Những mẫu giày được yêu thích nhất
          </p>
        </div>

        <!-- Loading State -->
        <div
          v-if="loading"
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
        >
          <div
            v-for="n in 8"
            :key="n"
            class="bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 overflow-hidden animate-pulse"
          >
            <div
              class="h-64 bg-gradient-to-br from-gray-200 to-gray-300 dark:from-gray-700 dark:to-gray-600"
            ></div>
            <div class="p-5 space-y-3">
              <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
              <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/2"></div>
              <div class="h-6 bg-gray-200 dark:bg-gray-700 rounded w-1/3"></div>
            </div>
          </div>
        </div>

        <!-- Error State -->
        <div
          v-else-if="error"
          class="text-center py-20 bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 shadow-lg"
        >
          <div
            class="w-20 h-20 mx-auto mb-6 bg-red-100 dark:bg-red-900/30 rounded-full flex items-center justify-center"
          >
            <i class="material-icons text-5xl text-red-500">error_outline</i>
          </div>
          <h3 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">
            {{ error }}
          </h3>
          <button
            @click="fetchProducts(currentPage)"
            class="inline-flex items-center gap-2 px-6 py-3 bg-purple-600 text-white rounded-xl font-semibold hover:bg-purple-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:scale-105"
          >
            <i class="material-icons">refresh</i>
            Thử lại
          </button>
        </div>

        <!-- Products Grid -->
        <div
          v-else-if="products.length > 0"
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
        >
          <div
            v-for="(product, index) in products"
            :key="product.id"
            class="group bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 hover:shadow-2xl hover:scale-105 transition-all duration-300 overflow-hidden transform"
            :style="{ animationDelay: `${index * 50}ms` }"
          >
            <ProductCard :product="product" />
          </div>
        </div>

        <!-- Empty State -->
        <div
          v-else
          class="text-center py-20 bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700"
        >
          <div
            class="w-20 h-20 mx-auto mb-6 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center"
          >
            <i class="material-icons text-5xl text-gray-400">inventory_2</i>
          </div>
          <h3 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">
            Chưa có sản phẩm nào
          </h3>
          <p class="text-gray-600 dark:text-gray-400">Vui lòng quay lại sau</p>
        </div>

        <!-- Pagination -->
        <div
          v-if="!loading && !error && totalPages > 1"
          class="flex items-center justify-center gap-2 mt-12"
        >
          <button
            @click="handlePageChange(currentPage - 1)"
            :disabled="currentPage === 0"
            class="px-5 py-2.5 bg-white dark:bg-gray-800 border-2 border-gray-200 dark:border-gray-700 rounded-xl text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-md hover:shadow-lg font-semibold"
          >
            <i class="material-icons">chevron_left</i>
            Trước
          </button>

          <div class="flex gap-2">
            <button
              v-for="page in displayPages"
              :key="page"
              @click="handlePageChange(page - 1)"
              :class="[
                'px-5 py-2.5 rounded-xl font-bold transition-all duration-200 shadow-md hover:shadow-lg',
                currentPage === page - 1
                  ? 'bg-gradient-to-r from-purple-600 to-indigo-600 text-white shadow-xl scale-110'
                  : 'bg-white dark:bg-gray-800 border-2 border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700',
              ]"
            >
              {{ page }}
            </button>
          </div>

          <button
            @click="handlePageChange(currentPage + 1)"
            :disabled="currentPage >= totalPages - 1"
            class="px-5 py-2.5 bg-white dark:bg-gray-800 border-2 border-gray-200 dark:border-gray-700 rounded-xl text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-md hover:shadow-lg font-semibold"
          >
            Sau
            <i class="material-icons">chevron_right</i>
          </button>
        </div>
      </div>
    </section>

    <!-- Brands Section -->
    <section
      class="py-16 md:py-20 bg-white dark:bg-gray-800 relative overflow-hidden"
    >
      <!-- Background Pattern -->
      <div class="absolute inset-0 opacity-5">
        <div
          class="absolute inset-0"
          style="
            background-image: repeating-linear-gradient(
              90deg,
              transparent,
              transparent 50px,
              rgba(147, 51, 234, 0.1) 50px,
              rgba(147, 51, 234, 0.1) 100px
            );
          "
        ></div>
      </div>

      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div class="text-center mb-12">
          <h2
            class="text-3xl md:text-4xl font-extrabold text-gray-900 dark:text-gray-100 mb-3"
          >
            Thương Hiệu Đối Tác
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400">
            Chính hãng từ các thương hiệu hàng đầu thế giới
          </p>
        </div>

        <div
          class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4 md:gap-6"
        >
          <router-link
            v-for="brand in brands"
            :key="brand.id"
            :to="`/home/products?brand=${encodeURIComponent(brand.name)}`"
            class="group bg-white dark:bg-gray-800 rounded-2xl border-2 border-gray-200 dark:border-gray-700 p-8 hover:border-purple-500 dark:hover:border-purple-600 hover:shadow-2xl hover:scale-110 transition-all duration-300 flex items-center justify-center transform cursor-pointer"
          >
            <div
              class="text-2xl font-bold text-gray-400 group-hover:text-purple-600 dark:group-hover:text-purple-400 transition-colors"
            >
              {{ brand.name }}
            </div>
          </router-link>
        </div>
      </div>
    </section>

    <!-- Testimonials Section -->
    <TestimonialsSection />

    <!-- Newsletter Section -->
    <section
      class="py-16 md:py-20 bg-gradient-to-r from-purple-600 via-indigo-600 to-purple-700 dark:from-gray-800 dark:via-gray-900 dark:to-gray-800 relative overflow-hidden"
    >
      <!-- Animated Background -->
      <div class="absolute inset-0 opacity-10">
        <div
          class="absolute inset-0 animate-pulse"
          style="
            background-image: repeating-linear-gradient(
              45deg,
              transparent,
              transparent 30px,
              rgba(255, 255, 255, 0.1) 30px,
              rgba(255, 255, 255, 0.1) 60px
            );
          "
        ></div>
      </div>

      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div
          class="bg-white dark:bg-gray-800 rounded-3xl p-8 md:p-12 shadow-2xl border border-gray-200 dark:border-gray-700 transform hover:scale-[1.02] transition-transform duration-300"
        >
          <div class="flex flex-col md:flex-row md:items-center gap-8">
            <div class="flex items-center gap-6 flex-shrink-0">
              <div
                class="w-20 h-20 bg-gradient-to-br from-purple-500 to-indigo-600 rounded-2xl flex items-center justify-center shadow-xl"
              >
                <i class="material-icons text-white text-4xl">email</i>
              </div>
              <div>
                <h3
                  class="text-2xl md:text-3xl font-extrabold text-gray-900 dark:text-gray-100 mb-2"
                >
                  Đăng Ký Nhận Tin
                </h3>
                <p
                  class="text-sm md:text-base text-gray-600 dark:text-gray-400"
                >
                  Nhận thông báo về sản phẩm mới và ưu đãi đặc biệt
                </p>
              </div>
            </div>
            <div class="flex-1 flex flex-col sm:flex-row gap-3">
              <div class="flex-1 relative">
                <i
                  class="material-icons absolute left-4 top-1/2 -translate-y-1/2 text-gray-400"
                  >mail_outline</i
                >
                <input
                  type="email"
                  placeholder="Nhập email của bạn..."
                  v-model="newsletterEmail"
                  @keyup.enter="handleNewsletterSubscribe"
                  class="w-full pl-12 pr-4 py-4 bg-gray-50 dark:bg-gray-700 border-2 border-gray-200 dark:border-gray-600 rounded-xl text-gray-900 dark:text-gray-100 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-200 font-medium"
                />
              </div>
              <button
                @click="handleNewsletterSubscribe"
                :disabled="subscribing || !newsletterEmail.trim()"
                class="px-8 py-4 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-bold text-lg hover:from-purple-700 hover:to-indigo-700 transition-all duration-200 shadow-xl hover:shadow-2xl transform hover:scale-105 flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
              >
                <i v-if="subscribing" class="material-icons animate-spin"
                  >refresh</i
                >
                <i v-else class="material-icons">send</i>
                {{ subscribing ? "Đang xử lý..." : "Đăng Ký" }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import ProductService from "@/services/productService";
import newsletterService from "@/services/newsletterService";
import { useFlashSaleStore } from "@/stores/flashSale";
import ProductCard from "@/assets/components/products/ProductCard.vue";
import TestimonialsSection from "@/assets/components/common/TestimonialsSection.vue";
import logger from "@/utils/logger";
import notificationService from "@/utils/notificationService";
import { API_ENDPOINTS } from "@/config/api";
import axios from "axios";
import { useProductImageStore } from "@/stores/productImages";

// Categories data - now grouped by parent
const categoryGroups = ref([]);
const loadingCategories = ref(false);
const activeCategoryTab = ref(0); // Track active tab index
const productImageStore = useProductImageStore();

// Icon mapping for categories
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
  "giay-chay-bo-nam": "directions_run",
  "giay-bong-ro-nam": "sports_basketball",
  "giay-chay-bo-nu": "directions_run",
  "giay-thoi-trang-nu": "style",
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

// Brands data
const brands = ref([]);
const loadingBrands = ref(false);

// Stores
const flashSaleStore = useFlashSaleStore();

// Products state
const products = ref([]);
const loading = ref(false);
const error = ref(null);
const currentPage = ref(0);
const pageSize = ref(12);
const totalProducts = ref(0);
const totalPages = ref(0);
const newsletterEmail = ref("");
const subscribing = ref(false);

// Flash Sale state
const flashSaleProducts = ref([]);
const countdown = ref({
  days: "00",
  hours: "00",
  minutes: "00",
  seconds: "00",
});

let countdownInterval = null;

// Computed
const displayPages = computed(() => {
  const pages = [];
  const maxPages = 5;
  let start = Math.max(0, currentPage.value - 2);
  let end = Math.min(totalPages.value, start + maxPages);

  if (end - start < maxPages) {
    start = Math.max(0, end - maxPages);
  }

  for (let i = start; i < end; i++) {
    pages.push(i + 1);
  }

  return pages;
});

// Methods
const fetchProducts = async (page = 0) => {
  loading.value = true;
  error.value = null;

  try {
    const response = await ProductService.getProducts(page, pageSize.value);

    // Handle Spring Data Page structure
    let productData = [];
    let totalElements = 0;
    let totalPagesCount = 0;

    if (response.data) {
      if (Array.isArray(response.data.content)) {
        productData = response.data.content;
        totalElements = response.data.totalElements || 0;
        totalPagesCount = response.data.totalPages || 0;
      } else if (Array.isArray(response.data)) {
        productData = response.data;
        totalElements = response.data.length;
        totalPagesCount = Math.ceil(totalElements / pageSize.value);
      } else {
        productData = [];
        totalElements = 0;
        totalPagesCount = 0;
      }
    }

    products.value = productData;
    totalProducts.value = totalElements;
    totalPages.value = totalPagesCount;
  } catch (err) {
    console.error("❌ HomePage - Error fetching products:", err);

    if (err.response?.status === 404) {
      error.value = "Không tìm thấy sản phẩm nào.";
    } else if (err.response?.status === 500) {
      error.value = "Lỗi server. Vui lòng thử lại sau.";
    } else if (err.response?.data?.message) {
      error.value = err.response.data.message;
    } else {
      error.value = `Không thể tải sản phẩm: ${
        err.message || "Vui lòng thử lại sau."
      }`;
    }
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    currentPage.value = newPage;
    fetchProducts(newPage);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }
};

// Computed - Get nearest end time from active flash sales
const nearestEndTime = computed(() => {
  if (
    !flashSaleStore.activeFlashSales ||
    flashSaleStore.activeFlashSales.length === 0
  ) {
    return null;
  }

  const now = new Date();
  const activeSales = flashSaleStore.activeFlashSales.filter((sale) => {
    const start = new Date(sale.startTime);
    const end = new Date(sale.endTime);
    return now >= start && now <= end;
  });

  if (activeSales.length === 0) {
    return null;
  }

  // Find the nearest end time
  const endTimes = activeSales.map((sale) => new Date(sale.endTime));
  const nearestEnd = new Date(Math.min(...endTimes));
  return nearestEnd;
});

const updateCountdown = () => {
  if (!nearestEndTime.value) {
    countdown.value = { hours: "00", minutes: "00", seconds: "00" };
    return;
  }

  const now = new Date();
  const end = new Date(nearestEndTime.value);
  const diff = end - now;

  if (diff <= 0) {
    countdown.value = { days: "00", hours: "00", minutes: "00", seconds: "00" };
    // Refresh flash sales when countdown ends
    fetchFlashSales();
    return;
  }

  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((diff % (1000 * 60)) / 1000);

  countdown.value = {
    days: days.toString().padStart(2, "0"),
    hours: hours.toString().padStart(2, "0"),
    minutes: minutes.toString().padStart(2, "0"),
    seconds: seconds.toString().padStart(2, "0"),
  };
};

// Fetch brands
const fetchBrands = async () => {
  loadingBrands.value = true;
  try {
    // Fetch brands from public API endpoint
    const brandsResponse = await axios.get(API_ENDPOINTS.PRODUCTS.BRANDS);
    if (Array.isArray(brandsResponse.data) && brandsResponse.data.length > 0) {
      brands.value = brandsResponse.data.map((brand) => ({
        id: brand.id,
        name: brand.name,
        slug: brand.slug,
        logo: brand.logo || null,
      }));
      logger.log(
        "Fetched brands for HomePage:",
        brands.value.length,
        brands.value
      );
    } else {
      // Fallback to empty array if no brands
      brands.value = [];
      logger.warn("No brands found from API");
    }
  } catch (error) {
    logger.error("Error fetching brands:", error);
    // Fallback to empty array if API fails
    brands.value = [];
  } finally {
    loadingBrands.value = false;
  }
};

// Fetch categories grouped by parent
const fetchCategories = async () => {
  loadingCategories.value = true;
  try {
    logger.log(
      "Fetching category groups from:",
      API_ENDPOINTS.PRODUCTS.CATEGORIES_GROUPS
    );
    // Try category groups endpoint first
    const groupsResponse = await axios.get(
      API_ENDPOINTS.PRODUCTS.CATEGORIES_GROUPS
    );
    logger.log("Category groups API response:", groupsResponse.data);
    if (Array.isArray(groupsResponse.data) && groupsResponse.data.length > 0) {
      logger.log("Found", groupsResponse.data.length, "category groups");
      // Fetch products to count by category
      try {
        logger.log("Fetching products to count categories...");
        const productsResponse = await ProductService.getProducts(0, 1000);
        let productData = [];
        if (productsResponse.data) {
          if (Array.isArray(productsResponse.data.content)) {
            productData = productsResponse.data.content;
          } else if (Array.isArray(productsResponse.data)) {
            productData = productsResponse.data;
          }
        }
        logger.log(`Fetched ${productData.length} products for counting`);

        // Normalize product data: đảm bảo có categoryIds từ categories array
        productData = productData.map((product) => {
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

        // Log sample product to check categories structure
        if (productData.length > 0) {
          logger.log("Sample product categories:", productData[0].categories);
          logger.log("Sample product categoryIds:", productData[0].categoryIds);
        }

        // Normalize slug for comparison
        const normalizeSlug = (slug) => {
          if (!slug) return "";
          return slug.toLowerCase().trim().replace(/\s+/g, "-");
        };

        // Process each group
        const processedGroups = groupsResponse.data.map((group) => {
          // Get parent category slug for matching
          const parentSlugNormalized = normalizeSlug(group.slug);

          // Count products that belong to parent category
          const parentProducts = productData.filter((product) => {
            if (product.categories && Array.isArray(product.categories)) {
              return product.categories.some((cat) => {
                const catSlug = normalizeSlug(cat.slug || cat.name);
                return catSlug === parentSlugNormalized;
              });
            }
            // Fallback: check categoryIds if available
            if (product.categoryIds && Array.isArray(product.categoryIds)) {
              return product.categoryIds.includes(group.id);
            }
            return false;
          });

          const parentProductCount = parentProducts.length;

          // Process children: check if products are directly linked to children
          // If not, all children show the parent count (since products belong to parent)
          const processedChildren = group.children.map((child) => {
            // Check if product has this specific child category
            const childSlugNormalized = normalizeSlug(child.slug);
            const directChildCount = productData.filter((product) => {
              if (product.categories && Array.isArray(product.categories)) {
                return product.categories.some((cat) => {
                  const catSlug = normalizeSlug(cat.slug || cat.name);
                  return catSlug === childSlugNormalized;
                });
              }
              if (product.categoryIds && Array.isArray(product.categoryIds)) {
                return product.categoryIds.includes(child.id);
              }
              return false;
            }).length;

            // If products are directly linked to child, use that count
            // Otherwise, use parent count (products belong to parent category)
            const finalCount =
              directChildCount > 0 ? directChildCount : parentProductCount;

            logger.log(
              `Child ${child.name}: directCount=${directChildCount}, finalCount=${finalCount}`
            );

            return {
              ...child,
              count: finalCount,
            };
          });

          // Calculate total product count for parent
          // If children have direct counts, sum them; otherwise use parent count
          const childrenWithDirectCounts = processedChildren.filter(
            (c) => c.count > 0 && c.count !== parentProductCount
          );
          const totalCount =
            childrenWithDirectCounts.length > 0
              ? processedChildren.reduce(
                  (sum, child) => sum + (child.count || 0),
                  0
                )
              : parentProductCount;

          logger.log(
            `Group ${group.name}: ${parentProductCount} products in parent, ${processedChildren.length} children`
          );

          return {
            ...group,
            children: processedChildren,
            productCount: totalCount,
          };
        });

        categoryGroups.value = processedGroups;
        logger.log(
          "Fetched category groups for HomePage:",
          categoryGroups.value.length,
          categoryGroups.value
        );
      } catch (productsError) {
        logger.warn(
          "Could not fetch products to count categories:",
          productsError
        );
        // Use groups without product counts
        categoryGroups.value = groupsResponse.data.map((group) => ({
          ...group,
          children: group.children.map((child) => ({ ...child, count: 0 })),
          productCount: 0,
        }));
      }
    } else {
      // Fallback: try regular categories endpoint
      try {
        const categoriesResponse = await axios.get(
          API_ENDPOINTS.PRODUCTS.CATEGORIES
        );
        if (
          Array.isArray(categoriesResponse.data) &&
          categoriesResponse.data.length > 0
        ) {
          // Group categories by parent manually (if we have parent info)
          // For now, just create a single group with all categories
          categoryGroups.value = [
            {
              id: 0,
              name: "Tất cả danh mục",
              slug: "all",
              productCount: 0,
              children: categoriesResponse.data.map((cat) => ({
                ...cat,
                count: 0,
              })),
            },
          ];
        }
      } catch (fallbackError) {
        logger.warn(
          "Could not fetch categories, using empty groups:",
          fallbackError
        );
        categoryGroups.value = [];
      }
    }
  } catch (error) {
    logger.error("Error fetching category groups for HomePage:", error);
    categoryGroups.value = [];
  } finally {
    loadingCategories.value = false;
  }
};

// Fetch flash sales
const fetchFlashSales = async () => {
  try {
    await flashSaleStore.fetchActiveFlashSales();

    if (flashSaleStore.activeFlashSales.length === 0) {
      flashSaleProducts.value = [];
      return;
    }

    // Fetch products for each flash sale (limit to 4 for homepage)
    const productIds = flashSaleStore.activeFlashSales
      .slice(0, 4)
      .map((sale) => sale.productId);
    const products = [];

    for (const productId of productIds) {
      try {
        const product = await ProductService.getProductById(productId);
        if (product) {
          // Add flash sale info to product
          const flashSale = flashSaleStore.getFlashSaleForProduct(productId);
          if (flashSale) {
            product.flashSale = flashSale;
            // Ensure brandName is set from flashSale if product doesn't have it
            if (
              !product.brandName &&
              !product.brand?.name &&
              flashSale.brandName
            ) {
              product.brandName = flashSale.brandName;
            }
            // Ensure brand object is set if we have brandName
            if (product.brandName && !product.brand) {
              product.brand = { name: product.brandName };
            }
            products.push(product);
          }
        }
      } catch (err) {
        logger.warn(`Failed to fetch product ${productId}:`, err);
      }
    }

    flashSaleProducts.value = products;
  } catch (err) {
    logger.error("Error fetching flash sales:", err);
  }
};

// Newsletter subscription
const handleNewsletterSubscribe = async () => {
  const email = newsletterEmail.value.trim();

  // Validate email
  if (!email) {
    notificationService.warning(
      "Vui lòng nhập email",
      "Email không được để trống"
    );
    return;
  }

  // Basic email validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    notificationService.warning(
      "Email không hợp lệ",
      "Vui lòng nhập địa chỉ email hợp lệ"
    );
    return;
  }

  subscribing.value = true;
  try {
    await newsletterService.subscribe(email);
    notificationService.success(
      "Đăng ký thành công!",
      "Cảm ơn bạn đã đăng ký nhận tin. Chúng tôi sẽ gửi thông báo về sản phẩm mới và ưu đãi đặc biệt đến email của bạn."
    );
    newsletterEmail.value = ""; // Clear input after success
  } catch (error) {
    logger.error("Error subscribing to newsletter:", error);

    // Handle different error types
    if (error.response) {
      const status = error.response.status;
      const message =
        error.response.data?.message ||
        error.response.data?.error ||
        "Đã xảy ra lỗi";

      if (status === 409) {
        // Email already subscribed
        notificationService.info(
          "Email đã đăng ký",
          "Email này đã được đăng ký nhận tin rồi. Cảm ơn bạn!"
        );
        newsletterEmail.value = ""; // Clear input
      } else if (status === 400) {
        // Bad request (invalid email)
        notificationService.warning("Email không hợp lệ", message);
      } else {
        notificationService.error("Đăng ký thất bại", message);
      }
    } else {
      notificationService.error(
        "Lỗi kết nối",
        "Không thể kết nối đến server. Vui lòng thử lại sau."
      );
    }
  } finally {
    subscribing.value = false;
  }
};

// Watch categoryGroups to reset active tab when groups change
watch(
  () => categoryGroups.value,
  (newGroups) => {
    if (newGroups.length > 0 && activeCategoryTab.value >= newGroups.length) {
      activeCategoryTab.value = 0;
    }
  }
);

// Lifecycle
onMounted(async () => {
  await Promise.all([
    fetchProducts(currentPage.value),
    productImageStore.loadAll(),
    fetchFlashSales(),
    fetchCategories(),
    fetchBrands(),
  ]);
  updateCountdown();
  countdownInterval = setInterval(updateCountdown, 1000);
});

onUnmounted(() => {
  if (countdownInterval) {
    clearInterval(countdownInterval);
  }
});
</script>

<style scoped>
@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes blob {
  0%,
  100% {
    transform: translate(0px, 0px) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fade-in-right {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes bounce-slow {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes gradient {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-blob {
  animation: blob 7s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}

.animation-delay-4000 {
  animation-delay: 4s;
}

.animate-fade-in-up {
  animation: fade-in-up 0.8s ease-out;
}

.animate-fade-in-right {
  animation: fade-in-right 0.8s ease-out 0.2s both;
}

.animate-bounce-slow {
  animation: bounce-slow 3s ease-in-out infinite;
}

.animate-bounce-slow-delayed {
  animation: bounce-slow 3s ease-in-out infinite;
  animation-delay: 1.5s;
}

.animate-gradient {
  background-size: 200% auto;
  animation: gradient 3s linear infinite;
}

.category-card {
  animation: fade-in-up 0.6s ease-out both;
  opacity: 0;
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Fade transition for tab content */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

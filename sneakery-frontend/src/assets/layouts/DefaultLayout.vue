<template>
  <div
    id="app-layout"
    class="min-h-screen flex flex-col bg-white dark:bg-gray-900"
  >
    <!-- Navigation Header -->
    <header
      class="fixed top-0 left-0 right-0 z-[100] bg-gradient-to-r from-indigo-900 via-purple-900 to-indigo-900 border-b border-purple-500/20 transition-all duration-300"
      :class="{ 'shadow-xl': isScrolled }"
    >
      <div
        class="max-w-[1400px] mx-auto px-3 sm:px-4 md:px-6 flex items-center justify-between h-[72px] gap-2 sm:gap-4"
      >
        <!-- Logo/Brand -->
        <router-link
          :to="{ name: 'home' }"
          class="flex items-center no-underline transition-opacity duration-200 flex-shrink-0 hover:opacity-90"
        >
          <img
            src="@/assets/images/logo.png"
            alt="Sneakery Store"
            class="h-16 sm:h-20 md:h-24 w-auto object-contain"
          />
        </router-link>

        <!-- Desktop Navigation -->
        <nav class="hidden md:flex items-center gap-3">
          <router-link
            to="/home/products"
            class="flex items-center gap-2 px-4 py-2 rounded-lg text-white/90 no-underline font-medium text-sm transition-all duration-200 hover:text-white hover:bg-white/10"
            active-class="text-white bg-white/20"
          >
            <svg
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path
                d="M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z"
              />
              <path d="M3 6H21" />
              <path
                d="M16 10C16 11.0609 15.5786 12.0783 14.8284 12.8284C14.0783 13.5786 13.0609 14 12 14C10.9391 14 9.92172 13.5786 9.17157 12.8284C8.42143 12.0783 8 11.0609 8 10"
              />
            </svg>
            <span>Sản Phẩm</span>
          </router-link>
        </nav>

        <!-- Enhanced Search -->
        <div class="flex-1 max-w-2xl mx-2 sm:mx-4 hidden sm:block">
          <EnhancedSearch />
        </div>

        <!-- Utility Icons -->
        <div class="flex items-center gap-1 sm:gap-2 flex-shrink-0">
          <!-- Theme Toggle Button -->
          <button
            class="flex items-center justify-center w-8 h-8 sm:w-10 sm:h-10 rounded-lg text-white/90 bg-transparent border border-transparent cursor-pointer transition-all duration-200 hover:bg-white/10 hover:text-white hover:border-white/20"
            @click="handleThemeToggle"
            type="button"
            :title="isDark ? 'Chuyển sang sáng' : 'Chuyển sang tối'"
          >
            <svg
              v-if="isDark"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="12" cy="12" r="5" />
              <line x1="12" y1="1" x2="12" y2="3" />
              <line x1="12" y1="21" x2="12" y2="23" />
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
              <line x1="1" y1="12" x2="3" y2="12" />
              <line x1="21" y1="12" x2="23" y2="12" />
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
            </svg>
            <svg
              v-else
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
            </svg>
          </button>

          <!-- Action Icons -->
          <template v-if="authStore.isAuthenticated">
            <!-- Wishlist Icon -->
            <router-link
              to="/user/wishlist"
              class="relative flex items-center justify-center w-8 h-8 sm:w-10 sm:h-10 rounded-lg text-white/90 bg-transparent transition-all duration-200 cursor-pointer border border-transparent hover:bg-white/10 hover:text-white hover:border-white/20"
              :class="{
                'bg-white/20 text-white border-white/30':
                  $route.path === '/user/wishlist',
              }"
            >
              <svg
                width="22"
                height="22"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z"
                />
              </svg>
              <span
                class="absolute top-0.5 right-0.5 min-w-[18px] h-[18px] px-1.5 bg-red-500 text-white text-[11px] font-semibold rounded-full flex items-center justify-center shadow-md"
                v-if="wishlistStore.wishlistCount > 0"
                >{{ wishlistStore.wishlistCount }}</span
              >
            </router-link>

            <!-- Notification Dropdown -->
            <NotificationDropdown />

            <!-- Orders/Cart Icon -->
            <router-link
              to="/cart"
              class="relative flex items-center justify-center w-8 h-8 sm:w-10 sm:h-10 rounded-lg text-white/90 bg-transparent transition-all duration-200 cursor-pointer border border-transparent hover:bg-white/10 hover:text-white hover:border-white/20"
              :class="{
                'bg-white/20 text-white border-white/30':
                  $route.path === '/cart',
              }"
              aria-label="Giỏ hàng"
            >
              <svg
                width="22"
                height="22"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z"
                />
              </svg>
              <span
                v-if="cartStore.cartCount > 0"
                class="absolute top-0 right-0 min-w-[18px] h-[18px] px-1.5 bg-red-500 text-white text-[11px] font-semibold rounded-full flex items-center justify-center shadow-md transform translate-x-1 -translate-y-1"
              >
                {{ cartStore.cartCount > 99 ? "99+" : cartStore.cartCount }}
              </span>
              <span v-if="cartStore.cartCount > 0" class="sr-only"
                >{{ cartStore.cartCount }} sản phẩm trong giỏ hàng</span
              >
            </router-link>
          </template>
        </div>

        <!-- User Section -->
        <nav class="hidden md:flex items-center gap-2">
          <template v-if="authStore.isAuthenticated">
            <!-- Admin Panel Link -->
            <router-link
              v-if="isAdmin"
              to="/admin/dashboard"
              class="flex items-center gap-1.5 sm:gap-2 px-2 sm:px-4 py-1.5 sm:py-2 rounded-lg bg-gradient-to-r from-amber-500 to-amber-600 text-white font-semibold text-xs sm:text-sm no-underline transition-all duration-200 hover:from-amber-600 hover:to-amber-700 hover:-translate-y-0.5 hover:shadow-lg"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M12 2L2 7L12 12L22 7L12 2Z" />
                <path d="M2 17L12 22L22 17" />
                <path d="M2 12L12 17L22 12" />
              </svg>
              <span>Admin Panel</span>
            </router-link>

            <div class="relative user-menu">
              <button
                class="flex items-center gap-1.5 sm:gap-2 px-2 sm:px-3 py-1.5 sm:py-2 bg-purple-600 hover:bg-purple-700 border border-purple-500 rounded-lg text-white font-medium text-xs sm:text-sm cursor-pointer transition-all duration-200"
                @click.stop="toggleUserMenu"
                type="button"
              >
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path
                    d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21"
                  />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <span class="hidden lg:inline">{{
                  authStore.currentUser?.fullName || "Tài khoản"
                }}</span>
                <span class="lg:hidden">Tài khoản</span>
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  class="transition-transform duration-150"
                  :class="{ 'rotate-180': showUserMenu }"
                >
                  <path d="M6 9L12 15L18 9" />
                </svg>
              </button>

              <transition
                enter-active-class="transition-all duration-200 ease-out"
                leave-active-class="transition-all duration-200 ease-in"
                enter-from-class="opacity-0 scale-95 -translate-y-2"
                enter-to-class="opacity-100 scale-100 translate-y-0"
                leave-from-class="opacity-100 scale-100 translate-y-0"
                leave-to-class="opacity-0 scale-95 -translate-y-2"
              >
                <div
                  v-if="showUserMenu"
                  class="absolute top-full right-0 mt-2 bg-white dark:bg-gray-800 backdrop-blur-xl border border-gray-200 dark:border-gray-700 rounded-xl shadow-lg min-w-[220px] p-2 z-[1000]"
                  @click.stop
                >
                  <router-link
                    to="/user/dashboard"
                    class="flex items-center gap-3 px-4 py-3 text-gray-600 dark:text-gray-300 no-underline text-sm font-medium transition-all duration-200 rounded-lg hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
                    @click="closeUserMenu"
                  >
                    <svg
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <rect
                        x="3"
                        y="3"
                        width="7"
                        height="7"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <rect
                        x="14"
                        y="3"
                        width="7"
                        height="7"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <rect
                        x="14"
                        y="14"
                        width="7"
                        height="7"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <rect
                        x="3"
                        y="14"
                        width="7"
                        height="7"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                    </svg>
                    Dashboard
                  </router-link>
                  <router-link
                    to="/user/profile"
                    class="flex items-center gap-3 px-4 py-3 text-gray-600 dark:text-gray-300 no-underline text-sm font-medium transition-all duration-200 rounded-lg hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
                    @click="closeUserMenu"
                  >
                    <svg
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
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
                    Thông tin cá nhân
                  </router-link>
                  <router-link
                    to="/user/orders"
                    class="flex items-center gap-3 px-4 py-3 text-gray-600 dark:text-gray-300 no-underline text-sm font-medium transition-all duration-200 rounded-lg hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
                    @click="closeUserMenu"
                  >
                    <svg
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                    </svg>
                    Đơn hàng của tôi
                  </router-link>
                  <div class="h-px bg-purple-200 dark:bg-purple-800 my-2"></div>
                  <a
                    href="#"
                    class="flex items-center gap-3 px-4 py-3 text-gray-600 dark:text-gray-300 no-underline text-sm font-medium transition-all duration-200 rounded-lg hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
                    @click.prevent="handleLogout"
                  >
                    <svg
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <polyline
                        points="16,17 21,12 16,7"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <line
                        x1="21"
                        y1="12"
                        x2="9"
                        y2="12"
                        stroke="currentColor"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                    </svg>
                    Đăng xuất
                  </a>
                </div>
              </transition>
            </div>
          </template>
          <template v-else>
            <div class="flex items-center gap-2">
              <router-link
                to="/login"
                class="inline-flex items-center gap-2 px-6 py-3 rounded-2xl font-semibold text-sm no-underline border-2 border-white/30 bg-transparent text-white backdrop-blur-sm cursor-pointer transition-all duration-300 hover:bg-white/10 hover:border-white/50 hover:-translate-y-0.5 hover:shadow-md"
              >
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M15 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H15"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <polyline
                    points="10,17 15,12 10,7"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <line
                    x1="15"
                    y1="12"
                    x2="3"
                    y2="12"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
                Đăng nhập
              </router-link>
              <router-link
                to="/register"
                class="inline-flex items-center gap-2 px-6 py-3 rounded-2xl font-semibold text-sm no-underline border-2 border-transparent bg-gradient-to-r from-purple-500 to-purple-600 text-white shadow-md cursor-pointer transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg hover:from-purple-600 hover:to-purple-700"
              >
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M16 21V19C16 17.9391 15.5786 16.9217 14.8284 16.1716C14.0783 15.4214 13.0609 15 12 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <circle
                    cx="8.5"
                    cy="7"
                    r="4"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <line
                    x1="20"
                    y1="8"
                    x2="20"
                    y2="14"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <line
                    x1="17"
                    y1="11"
                    x2="23"
                    y2="11"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
                Đăng ký
              </router-link>
            </div>
          </template>
        </nav>

        <!-- Mobile Menu Button -->
        <button
          class="md:hidden flex flex-col gap-1 bg-none border-none p-2 cursor-pointer rounded-lg transition-colors duration-150 hover:bg-white/10"
          @click="toggleMobileMenu"
          :class="{ active: showMobileMenu }"
        >
          <span
            class="w-6 h-0.5 bg-white rounded-full transition-all duration-150"
            :class="{ 'rotate-45 translate-y-1.5': showMobileMenu }"
          ></span>
          <span
            class="w-6 h-0.5 bg-white rounded-full transition-all duration-150"
            :class="{ 'opacity-0': showMobileMenu }"
          ></span>
          <span
            class="w-6 h-0.5 bg-white rounded-full transition-all duration-150"
            :class="{ '-rotate-45 -translate-y-1.5': showMobileMenu }"
          ></span>
        </button>
      </div>

      <!-- Mobile Navigation -->
      <nav
        class="fixed top-[72px] left-0 right-0 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 shadow-lg -translate-y-full opacity-0 invisible transition-all duration-300 z-[100] backdrop-blur-xl md:hidden"
        :class="{ 'translate-y-0 opacity-100 visible': showMobileMenu }"
      >
        <div class="p-4 flex flex-col gap-2">
          <router-link
            to="/home/products"
            class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
            @click="closeMobileMenu"
          >
            <svg
              width="20"
              height="20"
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
            <span>Sản Phẩm</span>
          </router-link>

          <template v-if="authStore.isAuthenticated">
            <router-link
              to="/user/dashboard"
              class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
              @click="closeMobileMenu"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <rect
                  x="3"
                  y="3"
                  width="7"
                  height="7"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
                <rect
                  x="14"
                  y="3"
                  width="7"
                  height="7"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
                <rect
                  x="14"
                  y="14"
                  width="7"
                  height="7"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
                <rect
                  x="3"
                  y="14"
                  width="7"
                  height="7"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              <span>Dashboard</span>
            </router-link>
            <router-link
              to="/user/profile"
              class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
              @click="closeMobileMenu"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
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
              <span>Thông tin cá nhân</span>
            </router-link>
            <router-link
              to="/user/orders"
              class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
              @click="closeMobileMenu"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              <span>Đơn hàng của tôi</span>
            </router-link>
            <router-link
              to="/user/wishlist"
              class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
              @click="closeMobileMenu"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              <span>Yêu thích</span>
            </router-link>
            <router-link
              to="/cart"
              class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
              @click="closeMobileMenu"
            >
              <svg
                width="20"
                height="20"
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
              <span>Giỏ hàng</span>
            </router-link>
            <a
              href="#"
              class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
              @click.prevent="handleLogout"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
                <polyline
                  points="16,17 21,12 16,7"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
                <line
                  x1="21"
                  y1="12"
                  x2="9"
                  y2="12"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              <span>Đăng xuất</span>
            </a>
          </template>
          <template v-else>
            <router-link
              to="/login"
              class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
              @click="closeMobileMenu"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M15 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H15"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              <span>Đăng nhập</span>
            </router-link>
            <router-link
              to="/register"
              class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-600 dark:text-gray-300 no-underline font-medium text-[15px] transition-all duration-200 hover:text-purple-500 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-900/20"
              @click="closeMobileMenu"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M16 21V19C16 17.9391 15.5786 16.9217 14.8284 16.1716C14.0783 15.4214 13.0609 15 12 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
                <circle
                  cx="8.5"
                  cy="7"
                  r="4"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              <span>Đăng ký</span>
            </router-link>
          </template>
        </div>
      </nav>
    </header>

    <!-- Main Content -->
    <main class="flex-1 min-h-[calc(100vh-72px)] mt-[72px]">
      <router-view />
    </main>

    <!-- Footer -->
    <footer
      class="bg-gray-50 dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 mt-auto"
    >
      <div class="max-w-[1200px] mx-auto px-4 py-12 md:py-6">
        <div class="grid grid-cols-1 md:grid-cols-[1fr_2fr] gap-12 mb-8">
          <div class="max-w-[400px]">
            <img
              src="@/assets/images/logo.png"
              alt="Sneakery Store"
              class="w-[120px] h-[120px] object-contain mb-4"
            />
            <p class="text-gray-500 dark:text-gray-400 leading-relaxed m-0">
              Cửa hàng giày sneaker hàng đầu với những sản phẩm chất lượng cao
              và thiết kế độc đáo.
            </p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div>
              <h4
                class="text-gray-900 dark:text-gray-100 text-lg font-semibold mb-4"
              >
                Liên kết
              </h4>
              <ul class="list-none p-0 m-0">
                <li class="mb-2">
                  <router-link
                    to="/home"
                    class="text-gray-500 dark:text-gray-400 no-underline transition-colors duration-150 hover:text-purple-400 dark:hover:text-purple-300"
                    >Trang chủ</router-link
                  >
                </li>
                <li class="mb-2">
                  <router-link
                    to="/home/products"
                    class="text-gray-500 dark:text-gray-400 no-underline transition-colors duration-150 hover:text-purple-400 dark:hover:text-purple-300"
                    >Sản phẩm</router-link
                  >
                </li>
                <li class="mb-2">
                  <a
                    href="#"
                    class="text-gray-500 dark:text-gray-400 no-underline transition-colors duration-150 hover:text-purple-400 dark:hover:text-purple-300"
                    >Về chúng tôi</a
                  >
                </li>
                <li class="mb-2">
                  <a
                    href="#"
                    class="text-gray-500 dark:text-gray-400 no-underline transition-colors duration-150 hover:text-purple-400 dark:hover:text-purple-300"
                    >Liên hệ</a
                  >
                </li>
              </ul>
            </div>

            <div>
              <h4
                class="text-gray-900 dark:text-gray-100 text-lg font-semibold mb-4"
              >
                Hỗ trợ
              </h4>
              <ul class="list-none p-0 m-0">
                <li class="mb-2">
                  <a
                    href="#"
                    class="text-gray-500 dark:text-gray-400 no-underline transition-colors duration-150 hover:text-purple-400 dark:hover:text-purple-300"
                    >Trung tâm trợ giúp</a
                  >
                </li>
                <li class="mb-2">
                  <a
                    href="#"
                    class="text-gray-500 dark:text-gray-400 no-underline transition-colors duration-150 hover:text-purple-400 dark:hover:text-purple-300"
                    >Chính sách đổi trả</a
                  >
                </li>
                <li class="mb-2">
                  <a
                    href="#"
                    class="text-gray-500 dark:text-gray-400 no-underline transition-colors duration-150 hover:text-purple-400 dark:hover:text-purple-300"
                    >Vận chuyển</a
                  >
                </li>
                <li class="mb-2">
                  <a
                    href="#"
                    class="text-gray-500 dark:text-gray-400 no-underline transition-colors duration-150 hover:text-purple-400 dark:hover:text-purple-300"
                    >Bảo hành</a
                  >
                </li>
              </ul>
            </div>

            <div>
              <h4
                class="text-gray-900 dark:text-gray-100 text-lg font-semibold mb-4"
              >
                Kết nối
              </h4>
              <div class="flex gap-3">
                <a
                  href="#"
                  class="flex items-center justify-center w-10 h-10 bg-gray-100 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-500 dark:text-gray-400 no-underline transition-all duration-150 hover:bg-gradient-to-r hover:from-purple-500 hover:to-purple-600 hover:border-transparent hover:text-white hover:-translate-y-0.5 hover:shadow-lg"
                >
                  <svg
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M18 2H15C13.6739 2 12.4021 2.52678 11.4645 3.46447C10.5268 4.40215 10 5.67392 10 7V10H7V14H10V22H14V14H17L18 10H14V7C14 6.73478 14.1054 6.48043 14.2929 6.29289C14.4804 6.10536 14.7348 6 15 6H18V2Z"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </a>
                <a
                  href="#"
                  class="flex items-center justify-center w-10 h-10 bg-gray-100 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-500 dark:text-gray-400 no-underline transition-all duration-150 hover:bg-gradient-to-r hover:from-purple-500 hover:to-purple-600 hover:border-transparent hover:text-white hover:-translate-y-0.5 hover:shadow-lg"
                >
                  <svg
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M23 3C22.0424 3.67548 20.9821 4.19211 19.86 4.53C19.2577 3.83751 18.4573 3.34669 17.567 3.12393C16.6767 2.90116 15.7395 2.95718 14.8821 3.28445C14.0247 3.61173 13.2884 4.19439 12.773 4.95372C12.2575 5.71305 11.9877 6.61232 12 7.53V8.53C10.2426 8.57557 8.50127 8.18581 6.93101 7.39525C5.36074 6.60469 4.01032 5.43866 3 4C3 4 -1 13 8 17C5.94053 18.398 3.48716 19.099 1 19C10 24 21 19 21 7.5C20.9991 7.22145 20.9723 6.94359 20.92 6.67C21.9406 5.66349 22.6608 4.39271 23 3V3Z"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                </a>
                <a
                  href="#"
                  class="flex items-center justify-center w-10 h-10 bg-gray-100 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-xl text-gray-500 dark:text-gray-400 no-underline transition-all duration-150 hover:bg-gradient-to-r hover:from-purple-500 hover:to-purple-600 hover:border-transparent hover:text-white hover:-translate-y-0.5 hover:shadow-lg"
                >
                  <svg
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <rect
                      x="2"
                      y="2"
                      width="20"
                      height="20"
                      rx="5"
                      ry="5"
                      stroke="currentColor"
                      stroke-width="2"
                    />
                    <path
                      d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37Z"
                      stroke="currentColor"
                      stroke-width="2"
                    />
                    <line
                      x1="17.5"
                      y1="6.5"
                      x2="17.51"
                      y2="6.5"
                      stroke="currentColor"
                      stroke-width="2"
                    />
                  </svg>
                </a>
              </div>
            </div>
          </div>
        </div>

        <div
          class="pt-6 border-t border-purple-200 dark:border-purple-800 text-center"
        >
          <p class="text-gray-500 dark:text-gray-400 text-sm m-0">
            &copy; 2024 Sneakery Store. Tất cả quyền được bảo lưu.
          </p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useAdminStore } from "@/stores/admin";
import { useWishlistStore } from "@/stores/wishlist";
import { useCartStore } from "@/stores/cart";
import { useRouter } from "vue-router";
import { useTheme } from "@/composables/useTheme";
import NotificationDropdown from "@/assets/components/common/NotificationDropdown.vue";
import EnhancedSearch from "@/assets/components/common/EnhancedSearch.vue";

const authStore = useAuthStore();
const adminStore = useAdminStore();
const wishlistStore = useWishlistStore();
const cartStore = useCartStore();
const router = useRouter();

// Theme
const { toggleTheme, isDark } = useTheme();

// Reactive state
const isScrolled = ref(false);
const showMobileMenu = ref(false);
const showUserMenu = ref(false);

// Theme toggle handler
const handleThemeToggle = () => {
  toggleTheme();
};

// Computed
const isAdmin = computed(() => {
  if (!authStore.isAuthenticated) return false;

  // Check if user has admin role from JWT token
  const token = localStorage.getItem("token");
  if (!token) return false;

  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.role === "ADMIN";
  } catch (error) {
    return false;
  }
});

// Scroll handler
const handleScroll = () => {
  isScrolled.value = window.scrollY > 20;
};

// Mobile menu handlers
const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value;
  showUserMenu.value = false; // Close user menu when opening mobile menu
};

const closeMobileMenu = () => {
  showMobileMenu.value = false;
};

// User menu handlers
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
  showMobileMenu.value = false; // Close mobile menu when opening user menu
};

const closeUserMenu = () => {
  showUserMenu.value = false;
};

// Logout handler
const handleLogout = () => {
  authStore.logout();
  router.push("/login");
  showUserMenu.value = false;
  showMobileMenu.value = false;
};

// Close menus when clicking outside
const handleClickOutside = (event) => {
  if (!event.target.closest(".user-menu")) {
    showUserMenu.value = false;
  }
  if (
    !event.target.closest(".mobile-menu-btn") &&
    !event.target.closest(".mobile-nav")
  ) {
    showMobileMenu.value = false;
  }
};

// Lifecycle hooks
onMounted(async () => {
  window.addEventListener("scroll", handleScroll);
  document.addEventListener("click", handleClickOutside);

  // Load wishlist count nếu user đã đăng nhập
  if (authStore.isAuthenticated) {
    try {
      await wishlistStore.fetchWishlist();
    } catch (error) {
      // Silently fail - wishlist is optional
    }
  }

  // Load cart nếu user đã đăng nhập hoặc có guest cart
  try {
    await cartStore.fetchCart();
  } catch (error) {
    // Silently fail - cart will be loaded when needed
  }
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
  document.removeEventListener("click", handleClickOutside);
});
</script>

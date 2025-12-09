<template>
  <div class="min-h-screen flex bg-gray-50 dark:bg-gray-900">
    <!-- Hamburger Menu (Mobile Only) -->
    <button
      v-if="isMobile"
      class="fixed top-4 left-4 z-[110] flex flex-col gap-1 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-2 shadow-lg cursor-pointer transition-colors duration-150 hover:bg-gray-100 dark:hover:bg-gray-700"
      @click="toggleSidebar"
      type="button"
      title="Toggle menu"
    >
      <span
        class="w-6 h-0.5 bg-gray-900 dark:bg-gray-100 rounded-full transition-all duration-150"
      ></span>
      <span
        class="w-6 h-0.5 bg-gray-900 dark:bg-gray-100 rounded-full transition-all duration-150"
      ></span>
      <span
        class="w-6 h-0.5 bg-gray-900 dark:bg-gray-100 rounded-full transition-all duration-150"
      ></span>
    </button>

    <!-- Admin Sidebar -->
    <aside
      class="fixed left-0 top-0 bottom-0 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 shadow-lg transition-all duration-300 z-[100] flex flex-col"
      :class="{
        'w-20': sidebarCollapsed && !isMobile,
        'w-[280px]':
          (!sidebarCollapsed && !isMobile) || (!sidebarCollapsed && isMobile),
        'translate-x-0': !isMobile || !sidebarCollapsed,
        '-translate-x-full': isMobile && sidebarCollapsed,
      }"
    >
      <!-- Header với Logo và Toggle Button -->
      <div
        class="flex items-center justify-between border-b border-gray-200 dark:border-gray-700 transition-all duration-300"
        :class="sidebarCollapsed ? 'p-3 justify-center' : 'p-4'"
      >
        <!-- Logo khi expanded -->
        <div
          v-if="!sidebarCollapsed"
          class="flex items-center justify-center flex-1 transition-all duration-300"
        >
          <img
            src="@/assets/images/logo.png"
            alt="Sneakery Store"
            class="h-28 w-auto object-contain transition-all duration-300"
          />
        </div>

        <!-- Toggle Button khi expanded - Desktop -->
        <button
          v-if="!isMobile && !sidebarCollapsed"
          class="flex items-center justify-center w-8 h-8 rounded-lg text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-all duration-200 flex-shrink-0"
          @click="toggleSidebar"
          type="button"
          title="Thu gọn"
        >
          <i class="material-icons text-lg">chevron_left</i>
        </button>

        <!-- Toggle Button khi collapsed - Desktop (thay thế icon hòm thư) -->
        <button
          v-if="!isMobile && sidebarCollapsed"
          class="flex items-center justify-center w-12 h-12 rounded-lg bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400 hover:bg-purple-200 dark:hover:bg-purple-900/50 transition-all duration-200 mx-auto"
          @click="toggleSidebar"
          type="button"
          title="Mở rộng"
        >
          <i class="material-icons text-2xl">chevron_right</i>
        </button>

        <!-- Toggle Button cho Mobile - trong sidebar -->
        <button
          v-if="isMobile"
          class="flex items-center justify-center w-8 h-8 rounded-lg text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors duration-150 flex-shrink-0"
          @click="toggleSidebar"
          type="button"
          title="Đóng menu"
        >
          <i class="material-icons text-xl">close</i>
        </button>
      </div>

      <nav class="flex-1 overflow-y-auto overflow-x-hidden py-4 sidebar-nav">
        <ul class="list-none p-0 m-0">
          <template v-for="route in adminRoutes" :key="route.id || route.name">
            <!-- Menu có submenu (dropdown) -->
            <li v-if="route.children" class="mb-1 relative">
              <a
                class="flex items-center rounded-lg text-gray-700 dark:text-gray-300 no-underline transition-all duration-200 cursor-pointer relative group"
                :class="{
                  'justify-center w-12 h-12 mx-auto': sidebarCollapsed,
                  'gap-3 px-4 py-3 mx-2': !sidebarCollapsed,
                  'bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400':
                    isSubmenuActive(route.children) ||
                    (sidebarCollapsed && hoveredMenuId === route.id),
                  'hover:bg-gray-100 dark:hover:bg-gray-700':
                    !isSubmenuActive(route.children) &&
                    !(sidebarCollapsed && hoveredMenuId === route.id),
                }"
                @click.prevent.stop="
                  sidebarCollapsed
                    ? (hoveredMenuId =
                        hoveredMenuId === route.id ? null : route.id)
                    : toggleMenu(route.id)
                "
                href="#"
                :title="sidebarCollapsed ? route.meta.title : ''"
              >
                <i
                  class="material-icons flex-shrink-0 transition-all duration-200"
                  :class="sidebarCollapsed ? 'text-2xl' : 'text-xl'"
                  >{{ route.meta.icon }}</i
                >
                <span
                  v-if="!sidebarCollapsed"
                  class="flex-1 font-medium text-sm transition-opacity duration-200"
                  >{{ route.meta.title }}</span
                >
                <!-- Expand icon -->
                <i
                  v-if="!sidebarCollapsed"
                  class="material-icons text-lg transition-transform duration-200 flex-shrink-0"
                  :class="{ 'rotate-180': isMenuOpen(route.id) }"
                >
                  {{ isMenuOpen(route.id) ? "expand_less" : "expand_more" }}
                </i>
              </a>

              <!-- Submenu dropdown - hiển thị khi expanded hoặc khi collapsed và được chọn -->
              <transition
                enter-active-class="transition-all duration-300 ease-out"
                enter-from-class="opacity-0 -translate-y-2"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition-all duration-200 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-2"
              >
                <ul
                  v-if="
                    (isMenuOpen(route.id) && !sidebarCollapsed) ||
                    (hoveredMenuId === route.id && sidebarCollapsed)
                  "
                  class="list-none p-0 m-0 mt-1"
                  :class="
                    sidebarCollapsed
                      ? 'pl-0 border-l-2 border-purple-300 dark:border-purple-600 ml-4'
                      : 'pl-4'
                  "
                >
                  <li
                    v-for="child in route.children"
                    :key="child.name"
                    class="mb-1"
                  >
                    <router-link
                      :to="child.path"
                      class="flex items-center rounded-lg text-gray-600 dark:text-gray-400 no-underline transition-all duration-200 text-sm relative"
                      :class="{
                        'justify-center w-10 h-10 mx-auto': sidebarCollapsed,
                        'gap-3 px-4 py-2 mx-2': !sidebarCollapsed,
                        'pl-6': !sidebarCollapsed,
                        'bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400':
                          $route.name === child.name,
                        'hover:bg-gray-100 dark:hover:bg-gray-700':
                          $route.name !== child.name,
                        'opacity-90': sidebarCollapsed,
                      }"
                      @click="isMobile && (sidebarCollapsed = true)"
                      :title="sidebarCollapsed ? child.meta.title : ''"
                    >
                      <!-- Indicator line cho expanded -->
                      <span
                        v-if="!sidebarCollapsed"
                        class="absolute left-0 w-0.5 h-6 bg-purple-300 dark:bg-purple-600 rounded-full"
                      ></span>
                      <i
                        class="material-icons flex-shrink-0"
                        :class="sidebarCollapsed ? 'text-lg' : 'text-base'"
                        >{{ child.meta.icon }}</i
                      >
                      <span v-if="!sidebarCollapsed" class="flex-1 text-sm">{{
                        child.meta.title
                      }}</span>
                    </router-link>
                  </li>
                </ul>
              </transition>
            </li>

            <!-- Menu thường (không có submenu) -->
            <li v-else class="mb-1">
              <router-link
                :to="route.path"
                class="flex items-center rounded-lg text-gray-700 dark:text-gray-300 no-underline transition-all duration-200 font-medium relative group"
                :class="{
                  'justify-center w-12 h-12 mx-auto': sidebarCollapsed,
                  'gap-3 px-4 py-3 mx-2 text-sm': !sidebarCollapsed,
                  'bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400':
                    $route.name === route.name,
                  'hover:bg-gray-100 dark:hover:bg-gray-700':
                    $route.name !== route.name,
                }"
                :title="sidebarCollapsed ? route.meta.title : ''"
                @click="isMobile && (sidebarCollapsed = true)"
              >
                <i
                  class="material-icons flex-shrink-0 transition-all duration-200"
                  :class="sidebarCollapsed ? 'text-2xl' : 'text-xl'"
                  >{{ route.meta.icon }}</i
                >
                <span
                  v-if="!sidebarCollapsed"
                  class="flex-1 transition-opacity duration-200"
                  >{{ route.meta.title }}</span
                >
              </router-link>
            </li>
          </template>
        </ul>
      </nav>

      <!-- Profile Card ở dưới cùng -->
      <div
        class="border-t border-gray-200 dark:border-gray-700 transition-all duration-300"
        :class="sidebarCollapsed ? 'p-2' : 'p-4'"
      >
        <div class="relative">
          <div
            class="flex items-center gap-2 rounded-lg bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 transition-all duration-200 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-700"
            :class="sidebarCollapsed ? 'justify-center p-2' : 'p-2'"
            @click="toggleProfileMenu"
            :title="sidebarCollapsed ? 'Admin' : ''"
          >
            <div class="relative flex-shrink-0">
              <div
                class="rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center transition-all duration-200"
                :class="sidebarCollapsed ? 'w-10 h-10' : 'w-8 h-8'"
              >
                <i
                  class="material-icons text-white"
                  :class="sidebarCollapsed ? 'text-xl' : 'text-sm'"
                  >person</i
                >
              </div>
              <div
                class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-green-500 border-2 border-white dark:border-gray-800 rounded-full"
              ></div>
            </div>
            <div v-if="!sidebarCollapsed" class="flex-1 min-w-0">
              <div
                class="font-semibold text-sm text-gray-900 dark:text-gray-100 capitalize truncate"
              >
                {{ adminUser?.email?.split("@")[0] || "Admin" }}
              </div>
              <div class="text-xs text-gray-500 dark:text-gray-400">
                {{ currentTime }} • {{ currentDate }}
              </div>
            </div>
            <button
              v-if="!sidebarCollapsed"
              class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors flex-shrink-0"
              @click.stop="toggleProfileMenu"
            >
              <i
                class="material-icons text-gray-600 dark:text-gray-300 text-sm"
                >{{ showProfileMenu ? "expand_more" : "expand_less" }}</i
              >
            </button>
          </div>

          <!-- Dropdown Menu -->
          <transition
            enter-active-class="transition-all duration-200 ease-out"
            leave-active-class="transition-all duration-200 ease-in"
            enter-from-class="opacity-0 scale-95 -translate-y-2"
            enter-to-class="opacity-100 scale-100 translate-y-0"
            leave-from-class="opacity-100 scale-100 translate-y-0"
            leave-to-class="opacity-0 scale-95 -translate-y-2"
          >
            <div
              v-if="showProfileMenu && !sidebarCollapsed"
              class="absolute bottom-full left-0 mb-2 w-full bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden z-[110]"
            >
              <a
                href="#"
                class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                @click.prevent="handleProfileEdit"
              >
                <i class="material-icons text-base">person_outline</i>
                <span>Hồ sơ</span>
              </a>
              <a
                href="#"
                class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                @click.prevent="handleSettings"
              >
                <i class="material-icons text-base">settings</i>
                <span>Cài đặt</span>
              </a>
              <a
                href="#"
                class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                @click.prevent="handleChangePassword"
              >
                <i class="material-icons text-base">lock</i>
                <span>Đổi mật khẩu</span>
              </a>
              <div class="border-t border-gray-200 dark:border-gray-700"></div>
              <a
                href="#"
                class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                @click.prevent="handleThemeToggle"
              >
                <i class="material-icons text-base">{{
                  isDark ? "light_mode" : "dark_mode"
                }}</i>
                <span>{{ isDark ? "Chế độ sáng" : "Chế độ tối" }}</span>
              </a>
              <div class="border-t border-gray-200 dark:border-gray-700"></div>
              <a
                href="#"
                class="flex items-center gap-2 px-3 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
                @click.prevent="handleLogout"
              >
                <i class="material-icons text-base">logout</i>
                <span>Đăng xuất</span>
              </a>
            </div>
          </transition>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <div
      class="flex-1 transition-all duration-300"
      :class="{
        'ml-0 md:ml-20': sidebarCollapsed,
        'ml-0 md:ml-[280px]': !sidebarCollapsed,
      }"
    >
      <!-- Page Content -->
      <main
        class="p-3 sm:p-4 lg:p-6 min-h-screen bg-gray-50 dark:bg-gray-900 w-full"
      >
        <!-- Breadcrumbs -->
        <div class="mb-4">
          <AdminBreadcrumbs />
        </div>
        <router-view />
      </main>
    </div>

    <!-- Mobile Overlay -->
    <div
      v-if="!sidebarCollapsed && isMobile"
      class="fixed inset-0 bg-black/50 z-[90] transition-opacity duration-300"
      @click="toggleSidebar"
    ></div>

    <!-- Shortcuts Help Modal -->
    <ShortcutsHelp v-model:show="showShortcutsHelp" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAdminStore } from "@/stores/admin";
import { useAuthStore } from "@/stores/auth";
import { useTheme } from "@/composables/useTheme";
import {
  useKeyboardShortcuts,
  adminShortcuts,
} from "@/composables/useKeyboardShortcuts";
import ShortcutsHelp from "@/components/admin/ShortcutsHelp.vue";
import AdminBreadcrumbs from "@/components/admin/AdminBreadcrumbs.vue";
import notificationService from "@/utils/notificationService";

const route = useRoute();
const router = useRouter();
const adminStore = useAdminStore();
const authStore = useAuthStore();

// Theme
const { toggleTheme, isDark } = useTheme();

// State
const sidebarCollapsed = ref(false);
const isMobile = ref(false);
const openMenus = ref([]); // Danh sách các menu đang mở
const hoveredMenuId = ref(null); // Menu đang được chọn khi collapsed
const popoverRef = ref(null); // Ref cho popover container
const showProfileMenu = ref(false);
const showShortcutsHelp = ref(false);
const currentTime = ref("");
const currentDate = ref("");

// Admin user
const adminUser = computed(() => adminStore.adminUser || authStore.currentUser);

// Update time and date
const updateDateTime = () => {
  const now = new Date();
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  currentTime.value = `${hours}:${minutes}`;

  const days = [
    "Chủ Nhật",
    "Thứ Hai",
    "Thứ Ba",
    "Thứ Tư",
    "Thứ Năm",
    "Thứ Sáu",
    "Thứ Bảy",
  ];
  const dayName = days[now.getDay()];
  const day = now.getDate();
  const month = now.getMonth() + 1;
  const year = now.getFullYear();
  currentDate.value = `${dayName}, ${day} tháng ${month}, ${year}`;
};

// Theme toggle handler
const handleThemeToggle = () => {
  toggleTheme();
  showProfileMenu.value = false;
};

// Handle click outside to close submenu khi collapsed
const handleClickOutside = (event) => {
  if (sidebarCollapsed.value && hoveredMenuId.value) {
    const menuItem = event.target.closest("li.relative");
    const menuLink = event.target.closest('a[href="#"]');
    const submenuLink = event.target.closest('a[href^="/admin"]');

    // Chỉ đóng nếu click bên ngoài menu item và submenu links
    if (!menuItem && !menuLink && !submenuLink) {
      hoveredMenuId.value = null;
    }
  }

  // Đóng profile menu nếu click bên ngoài
  if (showProfileMenu.value) {
    const profileCard = event.target.closest(".relative");
    if (!profileCard) {
      showProfileMenu.value = false;
    }
  }
};

// Profile menu handlers
const toggleProfileMenu = () => {
  showProfileMenu.value = !showProfileMenu.value;
};

const handleProfileEdit = () => {
  showProfileMenu.value = false;
  router.push("/admin/profile");
};

const handleSettings = () => {
  showProfileMenu.value = false;
  router.push("/admin/settings");
};

const handleChangePassword = () => {
  showProfileMenu.value = false;
  router.push("/admin/change-password");
};

const handleLogout = () => {
  showProfileMenu.value = false;
  notificationService.success("Đang đăng xuất...", "");
  setTimeout(() => {
    authStore.logout();
    adminStore.reset();
    localStorage.clear();
    window.location.href = "/login";
  }, 1000);
};

// Admin routes for sidebar với submenu
const adminRoutes = [
  {
    path: "/admin/dashboard",
    name: "AdminDashboard",
    meta: { title: "Trang chủ", icon: "home" },
  },
  {
    path: "/admin/sales",
    name: "AdminSales",
    meta: { title: "Bán Hàng", icon: "shopping_cart" },
  },
  {
    path: "/admin/orders",
    name: "AdminOrders",
    meta: { title: "Quản lý hóa đơn", icon: "receipt" },
  },
  {
    id: "products-menu",
    meta: { title: "Quản lý sản phẩm", icon: "inventory" },
    children: [
      {
        path: "/admin/products",
        name: "AdminProducts",
        meta: { title: "Danh sách sản phẩm", icon: "list" },
      },
      {
        path: "/admin/product-variants",
        name: "AdminProductVariants",
        meta: { title: "Quản lý biến thể", icon: "style" },
      },
      {
        path: "/admin/categories",
        name: "AdminCategories",
        meta: { title: "Quản lí danh mục", icon: "category" },
      },
      {
        path: "/admin/brands",
        name: "AdminBrands",
        meta: { title: "Quản lý thương hiệu", icon: "local_offer" },
      },
      {
        path: "/admin/materials",
        name: "AdminMaterials",
        meta: { title: "Quản lý chất liệu", icon: "layers" },
      },
      {
        path: "/admin/shoesoles",
        name: "AdminShoeSoles",
        meta: { title: "Quản lý loại đế giày", icon: "view_day" },
      },
      {
        path: "/admin/reviews",
        name: "AdminReviews",
        meta: { title: "Quản lí đánh giá", icon: "star_rate" },
      },
      {
        path: "/admin/flash-sales",
        name: "AdminFlashSales",
        meta: { title: "Flash Sale", icon: "flash_on" },
      },
    ],
  },
  {
    path: "/admin/users",
    name: "AdminUsers",
    meta: { title: "Quản lý người dùng", icon: "people" },
  },
  {
    path: "/admin/discounts",
    name: "AdminDiscounts",
    meta: { title: "Quản lý giảm giá", icon: "percent" },
  },
  {
    path: "/admin/returns",
    name: "AdminReturns",
    meta: { title: "Quản lý trả hàng", icon: "assignment_return" },
  },
  // {
  //   path: "/admin/warranty",
  //   name: "AdminWarranty",
  //   meta: { title: "Quản lý bảo hành", icon: "verified_user" },
  // },
  {
    path: "/admin/analytics",
    name: "AdminAnalytics",
    meta: { title: "Thống kê", icon: "analytics" },
  },
  {
    path: "/admin/notifications",
    name: "AdminNotifications",
    meta: { title: "Quản lý thông báo", icon: "notifications" },
  },
  {
    id: "management-menu",
    meta: { title: "Quản lý hệ thống", icon: "settings" },
    children: [
      {
        path: "/admin/inventory",
        name: "AdminInventory",
        meta: { title: "Quản lý kho", icon: "inventory_2" },
      },
      {
        path: "/admin/payments",
        name: "AdminPayments",
        meta: { title: "Thanh toán", icon: "payment" },
      },
      {
        path: "/admin/loyalty",
        name: "AdminLoyalty",
        meta: { title: "Điểm thưởng", icon: "stars" },
      },
      {
        path: "/admin/activity-logs",
        name: "AdminActivityLogs",
        meta: { title: "Nhật ký hoạt động", icon: "history" },
      },
      {
        path: "/admin/email-templates",
        name: "AdminEmailTemplates",
        meta: { title: "Mẫu email", icon: "email" },
      },
      {
        path: "/admin/settings",
        name: "AdminSettings",
        meta: { title: "Cài đặt", icon: "settings" },
      },
    ],
  },
];

// Methods
const toggleSidebar = () => {
  const wasCollapsed = sidebarCollapsed.value;
  sidebarCollapsed.value = !sidebarCollapsed.value;

  // Đóng tất cả submenu khi collapse sidebar
  if (!wasCollapsed && sidebarCollapsed.value) {
    openMenus.value = [];
  }
};

const toggleMenu = (menuId) => {
  if (!menuId) return;

  // Nếu collapsed, sử dụng hoveredMenuId thay vì openMenus
  if (sidebarCollapsed.value) {
    hoveredMenuId.value = hoveredMenuId.value === menuId ? null : menuId;
    return;
  }

  const index = openMenus.value.indexOf(menuId);
  if (index > -1) {
    openMenus.value.splice(index, 1);
  } else {
    openMenus.value.push(menuId);
  }
};

const isMenuOpen = (menuId) => {
  if (!menuId) return false;
  const isOpen = openMenus.value.includes(menuId);
  return isOpen;
};

// Kiểm tra xem route hiện tại có nằm trong submenu không
const isSubmenuActive = (children) => {
  if (!children) return false;
  return children.some((child) => child.name === route.name);
};

const checkMobile = () => {
  const wasMobile = isMobile.value;
  const width = window.innerWidth;

  // Nếu window nhỏ hơn 768px, luôn dùng top navigation (mobile layout)
  // Bất kể là desktop hay mobile device thật
  // Chỉ khi width >= 768px mới dùng left sidebar (desktop layout)
  isMobile.value = width < 768;

  if (isMobile.value && !wasMobile) {
    sidebarCollapsed.value = true;
  } else if (!isMobile.value && wasMobile) {
    sidebarCollapsed.value = false;
  }
};

// Function để update openMenus dựa trên route hiện tại
const updateOpenMenus = () => {
  const newOpenMenus = [];

  // Tìm menu nào chứa route hiện tại
  adminRoutes.forEach((menuItem) => {
    if (menuItem.children) {
      if (isSubmenuActive(menuItem.children)) {
        newOpenMenus.push(menuItem.id);
      }
    }
  });

  // Chỉ update nếu có thay đổi
  if (JSON.stringify(openMenus.value) !== JSON.stringify(newOpenMenus)) {
    openMenus.value = newOpenMenus;
  }
};

// Watch route changes để tự động đóng/mở menu
watch(
  () => route.name,
  () => {
    updateOpenMenus();
  }
);

// Watch sidebar collapse để tự động đóng submenu
watch(
  () => sidebarCollapsed.value,
  (collapsed) => {
    if (collapsed) {
      openMenus.value = [];
    }
  }
);

// Keyboard shortcuts
const { registerShortcut } = useKeyboardShortcuts({
  "shift+?": () => {
    showShortcutsHelp.value = true;
  },
  "ctrl+n": (e) => {
    // Context-aware: navigate to create page based on current route
    const currentPath = route.path;
    if (currentPath.includes("/products")) {
      router.push("/admin/products?action=create");
    } else if (currentPath.includes("/orders")) {
      // No create for orders
    } else if (currentPath.includes("/users")) {
      // Trigger create user modal if exists
      const createButton = document.querySelector(
        '[data-action="create-user"]'
      );
      if (createButton) createButton.click();
    }
  },
  "ctrl+s": (e) => {
    // Find and click save button in forms
    const saveButton = document.querySelector(
      'form button[type="submit"]:not([disabled])'
    );
    if (saveButton) {
      e.preventDefault();
      saveButton.click();
    }
  },
});

// Lifecycle
onMounted(() => {
  checkMobile();
  window.addEventListener("resize", checkMobile);
  document.addEventListener("click", handleClickOutside);
  updateOpenMenus();
  updateDateTime();
  setInterval(updateDateTime, 1000);
});

onUnmounted(() => {
  window.removeEventListener("resize", checkMobile);
  document.removeEventListener("click", handleClickOutside);
});
</script>

<style scoped>
.sidebar-nav {
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.sidebar-nav::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}
</style>

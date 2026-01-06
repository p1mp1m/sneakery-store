<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Header -->
    <div class="p-3 sm:p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-3 sm:gap-4">
        <div class="flex items-center gap-2 sm:gap-3">
          <div class="w-8 h-8 sm:w-10 sm:h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center shadow-sm">
            <i class="material-icons text-white text-lg sm:text-xl">dashboard</i>
          </div>
          <div>
            <h1 class="text-base sm:text-lg font-bold text-gray-900 dark:text-gray-100">
              <span class="text-xs font-medium text-purple-600 dark:text-purple-400 uppercase tracking-wide hidden sm:inline">Chào mừng trở lại,</span>
              <span class="block capitalize">{{ adminUser?.email?.split('@')[0] || 'Admin' }}</span>
            </h1>
            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
              <i class="material-icons text-sm">shield</i>
              <span class="hidden sm:inline">Quản lý và giám sát hệ thống Sneakery Store</span>
              <span class="sm:hidden">Dashboard</span>
            </p>
          </div>
        </div>

        <!-- Profile Card -->
        <div class="relative">
          <div class="flex items-center gap-2 p-2 bg-gray-50 dark:bg-gray-700/50 rounded-lg border border-gray-200 dark:border-gray-600">
            <div class="relative">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
                <i class="material-icons text-white text-sm">person</i>
              </div>
              <div class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-green-500 border-2 border-white dark:border-gray-800 rounded-full"></div>
            </div>
            <div class="flex-1 min-w-0">
              <div class="font-semibold text-sm text-gray-900 dark:text-gray-100 capitalize truncate">{{ adminUser?.email?.split('@')[0] || 'Admin' }}</div>
              <div class="text-xs text-gray-500 dark:text-gray-400">{{ currentTime }} • {{ currentDate }}</div>
            </div>
            <button class="p-1.5 rounded hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" @click="toggleProfileMenu">
              <i class="material-icons text-gray-600 dark:text-gray-300 text-sm">{{ showProfileMenu ? 'expand_less' : 'expand_more' }}</i>
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
            <div v-if="showProfileMenu" class="absolute top-full right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden z-10">
              <a href="#" class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors" @click.prevent="handleProfileEdit">
                <i class="material-icons text-base">person_outline</i>
                <span>Hồ sơ</span>
              </a>
              <a href="#" class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors" @click.prevent="handleSettings">
                <i class="material-icons text-base">settings</i>
                <span>Cài đặt</span>
              </a>
              <a href="#" class="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors" @click.prevent="handleChangePassword">
                <i class="material-icons text-base">lock</i>
                <span>Đổi mật khẩu</span>
              </a>
              <div class="border-t border-gray-200 dark:border-gray-700"></div>
              <a href="#" class="flex items-center gap-2 px-3 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors" @click.prevent="handleLogout">
                <i class="material-icons text-base">logout</i>
                <span>Đăng xuất</span>
              </a>
            </div>
          </transition>
        </div>
      </div>

      <!-- Refresh Controls -->
      <div class="flex items-center gap-2 mt-3 pt-3 border-t border-gray-200 dark:border-gray-700">
        <button 
          @click="manualRefresh" 
          class="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-md hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" 
          title="Làm mới dữ liệu"
          aria-label="Làm mới dữ liệu dashboard"
        >
          <i class="material-icons text-sm">refresh</i>
          <span>Làm mới</span>
        </button>
        <button 
          @click="toggleAutoRefresh" 
          class="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-md transition-colors"
          :class="autoRefreshEnabled 
            ? 'text-white bg-purple-600 hover:bg-purple-700' 
            : 'text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600'"
          :title="autoRefreshEnabled ? `Tắt tự động làm mới (hiện tại: ${autoRefreshIntervalSeconds}s)` : 'Bật tự động làm mới'"
          :aria-label="autoRefreshEnabled ? 'Tắt tự động làm mới dữ liệu' : 'Bật tự động làm mới dữ liệu'"
        >
          <i class="material-icons text-sm" :class="{ 'animate-spin': autoRefreshEnabled }">{{ autoRefreshEnabled ? 'sync' : 'sync_disabled' }}</i>
          <span>{{ autoRefreshEnabled ? `Tự động: ${autoRefreshIntervalSeconds}s` : 'Tự động: Tắt' }}</span>
        </button>
        <select 
          v-if="autoRefreshEnabled"
          v-model="autoRefreshIntervalSeconds"
          @change="startAutoRefresh"
          class="px-2 py-1.5 text-xs bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-md text-gray-700 dark:text-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500"
          title="Chọn khoảng thời gian tự động làm mới"
        >
          <option :value="60">60 giây</option>
          <option :value="120">120 giây</option>
          <option :value="300">5 phút</option>
          <option :value="600">10 phút</option>
        </select>
        <span v-if="lastRefreshTime" class="text-xs text-gray-500 dark:text-gray-400 ml-auto">
          Cập nhật: {{ formatRelativeTime(lastRefreshTime) }}
        </span>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:scale-[1.02] transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-400 to-green-500 flex items-center justify-center">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <line x1="12" y1="1" x2="12" y2="23" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M17 5H9.5C8.11929 5 7 6.11929 7 7.5S8.11929 10 9.5 10H14.5C15.8807 10 17 11.1193 17 12.5S15.8807 15 14.5 15H7" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatCurrency(stats?.totalRevenue || 0) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng doanh thu</p>
          <div class="flex items-center gap-1 text-xs" :class="statsTrends.revenue > 0 ? 'text-green-600 dark:text-green-400' : statsTrends.revenue < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="statsTrends.revenue > 0 ? 'trending_up' : statsTrends.revenue < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="statsTrends.revenue !== 0">{{ statsTrends.revenue > 0 ? '+' : '' }}{{ statsTrends.revenue.toFixed(1) }}%</span>
            <span v-else>Không đổi</span>
          </div>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:scale-[1.02] transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center shadow-sm">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.totalOrders || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng đơn hàng</p>
          <div class="flex items-center gap-1 text-xs" :class="statsTrends.orders > 0 ? 'text-green-600 dark:text-green-400' : statsTrends.orders < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="statsTrends.orders > 0 ? 'trending_up' : statsTrends.orders < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="statsTrends.orders !== 0">{{ statsTrends.orders > 0 ? '+' : '' }}{{ statsTrends.orders.toFixed(1) }}%</span>
            <span v-else>Không đổi</span>
          </div>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:scale-[1.02] transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-pink-500 to-pink-600 flex items-center justify-center shadow-sm">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.totalProducts || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Sản phẩm</p>
          <div class="flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400">
            <i class="material-icons text-sm">trending_flat</i>
            <span>Không đổi</span>
          </div>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:scale-[1.02] transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-400 to-blue-500 flex items-center justify-center shadow-sm">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="9" cy="7" r="4" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ stats?.totalUsers || 0 }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Người dùng</p>
          <div class="flex items-center gap-1 text-xs" :class="statsTrends.users > 0 ? 'text-green-600 dark:text-green-400' : statsTrends.users < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="statsTrends.users > 0 ? 'trending_up' : statsTrends.users < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="statsTrends.users !== 0">{{ statsTrends.users > 0 ? '+' : '' }}{{ statsTrends.users.toFixed(1) }}%</span>
            <span v-else>Không đổi</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-lg">bolt</i>
          Quản lý nhanh
        </h2>
      </div>
      <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-2 sm:gap-3">
        <router-link 
          v-for="action in quickActions" 
          :key="action.path"
          :to="action.path" 
          class="group relative p-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md hover:border-purple-300 dark:hover:border-purple-600 hover:scale-[1.02] transition-all duration-200 text-center"
        >
          <div class="w-10 h-10 mx-auto mb-2 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center group-hover:scale-110 transition-transform">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path :d="action.icon" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3 class="text-xs font-semibold text-gray-900 dark:text-gray-100 mb-1">{{ action.title }}</h3>
          <p class="text-[10px] text-gray-500 dark:text-gray-400 line-clamp-2">{{ action.desc }}</p>
          <span v-if="action.badge" class="absolute top-2 right-2 px-1.5 py-0.5 text-[10px] font-semibold text-white bg-purple-600 rounded">{{ action.badge }}</span>
        </router-link>
      </div>
    </div>

    <!-- Charts Section -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-lg">insights</i>
          Biểu đồ thống kê
        </h2>
        <div class="flex flex-wrap gap-2 items-center">
          <!-- Date Range Picker Toggle -->
          <button 
            @click="showDateRangePicker = !showDateRangePicker"
            class="flex items-center gap-1 sm:gap-1.5 px-2 sm:px-3 py-1.5 text-xs font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-md hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
            title="Chọn khoảng thời gian tùy chỉnh"
          >
            <i class="material-icons text-sm">date_range</i>
            <span class="hidden sm:inline">Tùy chỉnh</span>
          </button>
          
          <!-- Export Button -->
          <button 
            @click="exportReports"
            class="flex items-center gap-1 sm:gap-1.5 px-2 sm:px-3 py-1.5 text-xs font-medium text-white bg-green-600 hover:bg-green-700 rounded-md transition-colors"
            title="Xuất báo cáo"
          >
            <i class="material-icons text-sm">download</i>
            <span class="hidden sm:inline">Xuất báo cáo</span>
          </button>
          
          <!-- Period Buttons -->
          <div class="flex gap-1">
            <button 
              v-for="period in ['7d', '30d', '90d']" 
              :key="period"
              class="px-2 sm:px-3 py-1 text-xs font-medium rounded-lg transition-all duration-200"
              :class="selectedPeriod === period
                ? 'bg-purple-600 text-white shadow-sm'
                : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'"
              @click="changePeriod(period)"
            >
              <span class="hidden sm:inline">{{ period === '7d' ? '7 ngày' : period === '30d' ? '30 ngày' : '90 ngày' }}</span>
              <span class="sm:hidden">{{ period === '7d' ? '7d' : period === '30d' ? '30d' : '90d' }}</span>
            </button>
          </div>
        </div>
      </div>
      
      <!-- Date Range Picker (Collapsible) -->
      <transition
        enter-active-class="transition-all duration-300 ease-out"
        leave-active-class="transition-all duration-300 ease-in"
        enter-from-class="opacity-0 -translate-y-4"
        enter-to-class="opacity-100 translate-y-0"
        leave-from-class="opacity-100 translate-y-0"
        leave-to-class="opacity-0 -translate-y-4"
      >
        <div v-if="showDateRangePicker" class="mb-4">
          <DateRangePicker 
            v-model="customDateRange"
            @apply="applyCustomDateRange"
          />
        </div>
      </transition>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-3">
        <!-- Revenue Chart -->
        <div 
          ref="revenueChartRef"
          class="lg:col-span-2 p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
        >
          <div class="mb-2 pb-2 border-b border-gray-200 dark:border-gray-700">
            <h3 class="text-sm font-semibold text-gray-900 dark:text-gray-100">Doanh thu {{ selectedPeriod === '7d' ? '7 ngày' : selectedPeriod === '30d' ? '30 ngày' : '90 ngày' }} gần đây</h3>
            <span class="text-[10px] text-gray-500 dark:text-gray-400">Đơn vị: VNĐ</span>
          </div>
          <div class="h-48">
            <LineChart 
              v-if="chartsVisible.revenue"
              :labels="revenueChart.labels"
              :datasets="revenueChart.datasets"
            />
            <div v-else class="flex items-center justify-center h-full text-gray-400">
              <div class="text-center">
                <i class="material-icons text-4xl mb-2">insights</i>
                <p class="text-sm">Đang tải biểu đồ...</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Order Status Chart -->
        <div 
          ref="orderStatusChartRef"
          class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
        >
          <div class="mb-2 pb-2 border-b border-gray-200 dark:border-gray-700">
            <h3 class="text-sm font-semibold text-gray-900 dark:text-gray-100">Trạng thái đơn hàng</h3>
            <span class="text-[10px] text-gray-500 dark:text-gray-400">Phân bổ theo trạng thái</span>
          </div>
          <div class="h-48">
            <DoughnutChart 
              v-if="chartsVisible.orderStatus"
              :labels="orderStatusChart.labels"
              :datasets="orderStatusChart.datasets"
            />
            <div v-else class="flex items-center justify-center h-full text-gray-400">
              <div class="text-center">
                <i class="material-icons text-4xl mb-2">pie_chart</i>
                <p class="text-sm">Đang tải biểu đồ...</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Top Products Chart -->
        <div 
          ref="topProductsChartRef"
          class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
        >
          <div class="mb-2 pb-2 border-b border-gray-200 dark:border-gray-700">
            <h3 class="text-sm font-semibold text-gray-900 dark:text-gray-100">Top 5 sản phẩm bán chạy</h3>
            <span class="text-[10px] text-gray-500 dark:text-gray-400">Số lượng đã bán</span>
          </div>
          <div class="h-48">
            <BarChart 
              v-if="chartsVisible.topProducts"
              :labels="topProductsChart.labels"
              :datasets="topProductsChart.datasets"
            />
            <div v-else class="flex items-center justify-center h-full text-gray-400">
              <div class="text-center">
                <i class="material-icons text-4xl mb-2">bar_chart</i>
                <p class="text-sm">Đang tải biểu đồ...</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400 text-lg">history</i>
          Hoạt động gần đây
        </h2>
        <button class="flex items-center gap-1 px-3 py-1.5 text-xs font-medium text-purple-600 dark:text-purple-400 bg-purple-50 dark:bg-purple-900/20 rounded-lg hover:bg-purple-100 dark:hover:bg-purple-900/30 transition-all duration-200" @click="notificationService.info('Xem tất cả', 'Chức năng đang được phát triển')">
          <span>Xem tất cả</span>
          <i class="material-icons text-sm">arrow_forward</i>
        </button>
      </div>
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div v-if="loading" class="space-y-3">
          <div v-for="i in 5" :key="i" class="flex items-center gap-3">
            <div class="w-8 h-8 rounded-md bg-gray-200 dark:bg-gray-700 animate-pulse"></div>
            <div class="flex-1 space-y-2">
              <div class="h-4 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"></div>
              <div class="h-3 bg-gray-200 dark:bg-gray-700 rounded w-2/3 animate-pulse"></div>
            </div>
          </div>
        </div>
        <div v-else-if="recentActivities.length <= 10" class="flex flex-col gap-2">
          <div 
            v-for="activity in recentActivities" 
            :key="activity.id" 
            class="flex items-start gap-2 p-2 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors group"
          >
            <div class="w-8 h-8 rounded-md flex items-center justify-center flex-shrink-0"
              :class="{
                'bg-purple-100 dark:bg-purple-900/30': activity.type === 'order',
                'bg-blue-100 dark:bg-blue-900/30': activity.type === 'user',
                'bg-pink-100 dark:bg-pink-900/30': activity.type === 'product'
              }"
            >
              <svg v-if="activity.type === 'order'" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else-if="activity.type === 'user'" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm text-gray-900 dark:text-gray-100 mb-1">{{ activity.text }}</p>
              <span class="text-xs text-gray-500 dark:text-gray-400">{{ formatRelativeTime(activity.timestamp) }}</span>
            </div>
            <button class="p-1 rounded hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors opacity-0 group-hover:opacity-100" @click="notificationService.info('Chi tiết', activity.text)">
              <i class="material-icons text-sm">visibility</i>
            </button>
          </div>
        </div>
        <!-- Virtual List for long activity lists -->
        <VirtualList
          v-else
          :items="recentActivities"
          :item-height="60"
          :container-height="400"
          class="mt-2"
        >
          <template #default="{ item: activity, index }">
            <div 
              class="flex items-start gap-2 p-2 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors group"
            >
              <div class="w-8 h-8 rounded-md flex items-center justify-center flex-shrink-0"
                :class="{
                  'bg-purple-100 dark:bg-purple-900/30': activity.type === 'order',
                  'bg-blue-100 dark:bg-blue-900/30': activity.type === 'user',
                  'bg-pink-100 dark:bg-pink-900/30': activity.type === 'product'
                }"
              >
                <svg v-if="activity.type === 'order'" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else-if="activity.type === 'user'" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm text-gray-900 dark:text-gray-100 mb-1">{{ activity.text }}</p>
                <span class="text-xs text-gray-500 dark:text-gray-400">{{ formatRelativeTime(activity.timestamp) }}</span>
              </div>
              <button class="p-1 rounded hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors opacity-0 group-hover:opacity-100" @click="notificationService.info('Chi tiết', activity.text)">
                <i class="material-icons text-sm">visibility</i>
              </button>
            </div>
          </template>
        </VirtualList>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAdminStore } from '@/stores/admin';
import { useAuthStore } from '@/stores/auth';
import AdminService from '@/services/adminService';
import LineChart from '@/assets/components/charts/LineChart.vue';
import BarChart from '@/assets/components/charts/BarChart.vue';
import DoughnutChart from '@/assets/components/charts/DoughnutChart.vue';
import DateRangePicker from '@/assets/components/admin/DateRangePicker.vue';
import VirtualList from '@/components/admin/VirtualList.vue';
import { useLocalStorageCache } from '@/composables/useLocalStorageCache';
import logger from '@/utils/logger';
import { formatPrice, formatCurrency, formatRelativeTime } from '@/utils/formatters';
import notificationService from '@/utils/notificationService';

const router = useRouter();
const adminStore = useAdminStore();
const authStore = useAuthStore();
const { getCache, setCache } = useLocalStorageCache();

const adminUser = computed(() => adminStore.adminUser);

const loading = ref(false);
const selectedPeriod = ref('7d');
const currentTime = ref('');
const currentDate = ref('');
const showProfileMenu = ref(false);
const autoRefreshEnabled = ref(true);
const autoRefreshIntervalSeconds = ref(120); // OPTIMIZED: Tăng từ 60s lên 120s để giảm server load
const lastRefreshTime = ref(null);
const chartsVisible = ref({
  revenue: false,
  orderStatus: false,
  topProducts: false
});
let autoRefreshInterval = null;
let chartObservers = [];

const stats = ref({
  totalRevenue: 0,
  totalOrders: 0,
  totalProducts: 0,
  totalUsers: 0
});

const statsTrends = ref({
  revenue: 0,
  orders: 0,
  products: 0,
  users: 0
});

const badges = ref({});

// Quick actions template - badges sẽ được load từ API
const quickActionsTemplate = [
  { path: '/admin/products', title: 'Quản lý sản phẩm', desc: 'Thêm, sửa, xóa sản phẩm', icon: 'M21 16V8C21 7.46957 20.7893 6.96086 20.4142 6.58579C20.0391 6.21071 19.5304 6 19 6H5C4.46957 6 3.96086 6.21071 3.58579 6.58579C3.21071 6.96086 3 7.46957 3 8V16C3 16.5304 3.21071 17.0391 3.58579 17.4142C3.96086 17.7893 4.46957 18 5 18H19C19.5304 18 20.0391 17.7893 20.4142 17.4142C20.7893 17.0391 21 16.5304 21 16Z', badgeKey: null },
  { path: '/admin/orders', title: 'Quản lý đơn hàng', desc: 'Theo dõi và xử lý đơn hàng', icon: 'M16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11M5 9H19L18 21H6L5 9Z', badgeKey: null },
  { path: '/admin/users', title: 'Quản lý người dùng', desc: 'Xem và quản lý tài khoản', icon: 'M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21', badgeKey: null },
  { path: '/admin/brands', title: 'Quản lý thương hiệu', desc: 'Thêm và chỉnh sửa thương hiệu', icon: 'M3 3H10V10H3V3ZM14 3H21V10H14V3ZM14 14H21V21H14V14ZM3 14H10V21H3V14Z', badgeKey: 'brands' },
  { path: '/admin/categories', title: 'Quản lý danh mục', desc: 'Quản lý danh mục sản phẩm', icon: 'M4 19H20M4 15H20M4 11H20', badgeKey: 'categories' },
  { path: '/admin/analytics', title: 'Phân tích', desc: 'Xem báo cáo và thống kê', icon: 'M18 20V10M12 20V4M6 20V14', badgeKey: 'new' },
  { path: '/admin/inventory', title: 'Quản lý kho', desc: 'Theo dõi tồn kho', icon: 'M6 2L3 6V20C3 20.5304 3.21071 21.0391 3.58579 21.4142C3.96086 21.7893 4.46957 22 5 22H19C19.5304 22 20.0391 21.7893 20.4142 21.4142C20.7893 21.0391 21 20.5304 21 20V6L18 2H6Z', badgeKey: null },
  { path: '/admin/discounts', title: 'Quản lý giảm giá', desc: 'Tạo và quản lý mã giảm giá', icon: 'M7 7H17M7 17H17M12 2V22M18 6L6 18M18 18L6 6', badgeKey: 'vip' },
  { path: '/admin/reviews', title: 'Quản lí đánh giá', desc: 'Xem và phê duyệt đánh giá', icon: 'M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z', badgeKey: 'reviews' },
  { path: '/admin/loyalty', title: 'Điểm thưởng', desc: 'Quản lý chương trình tích điểm', icon: 'M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z', badgeKey: 'loyalty' },
  { path: '/admin/sales', title: 'Bán hàng (POS)', desc: 'Hệ thống bán hàng tại quầy', icon: 'M9 11L12 14L22 4M21 12V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H16', badgeKey: 'live' },
  { path: '/admin/notifications', title: 'Quản lý thông báo', desc: 'Gửi và quản lý thông báo', icon: 'M18 8A6 6 0 0 0 6 8C6 11.0909 3.90914 13 2 13H22C20.0909 13 18 11.0909 18 8ZM13.73 21C13.5542 21.3031 13.3018 21.5547 12.9982 21.7295C12.6946 21.9044 12.3504 21.9965 12 21.9965C11.6496 21.9965 11.3054 21.9044 11.0018 21.7295C10.6982 21.5547 10.4458 21.3031 10.27 21', badgeKey: 'notifications' },
  { path: '/admin/returns', title: 'Quản lý trả hàng', desc: 'Xử lý yêu cầu trả hàng', icon: 'M3 7V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V7M3 7L12 14L21 7M3 17L12 10L21 17M21 17V5', badgeKey: 'returns' },
  { path: '/admin/payments', title: 'Thanh toán', desc: 'Theo dõi giao dịch thanh toán', icon: 'M1 4H23M1 10H23M1 4V20C1 20.5304 1.21071 21.0391 1.58579 21.4142C1.96086 21.7893 2.46957 22 3 22H21C21.5304 22 22.0391 21.7893 22.4142 21.4142C22.7893 21.0391 23 20.5304 23 20V4', badgeKey: 'payments' }
];

// Computed quickActions với badges từ API
const quickActions = computed(() => {
  return quickActionsTemplate.map(action => {
    let badge = null;
    
    if (action.badgeKey === 'new') {
      badge = 'Mới';
    } else if (action.badgeKey === 'hot') {
      badge = 'Hot';
    } else if (action.badgeKey === 'vip') {
      badge = 'VIP';
    } else if (action.badgeKey === 'live') {
      badge = 'Live';
    } else if (action.badgeKey === 'loyalty') {
      badge = badges.value.loyalty || '1K+';
    } else if (action.badgeKey && badges.value[action.badgeKey]) {
      badge = badges.value[action.badgeKey];
    }
    
    return {
      ...action,
      badge: badge
    };
  });
});

// Khởi tạo với empty data, sẽ được load từ API
const revenueChart = ref({
  labels: [],
  datasets: [
    {
      label: 'Doanh thu',
      data: [],
      borderColor: 'rgb(59, 130, 246)',
      backgroundColor: 'rgba(59, 130, 246, 0.1)',
      fill: true,
      tension: 0.4
    }
  ]
});

const orderStatusChart = ref({
  labels: [],
  datasets: [
    {
      data: [],
      backgroundColor: [],
      borderColor: [],
      borderWidth: 2
    }
  ]
});

const topProductsChart = ref({
  labels: [],
  datasets: [
    {
      label: 'Số lượng bán',
      data: [],
      backgroundColor: 'rgba(59, 130, 246, 0.8)',
      borderColor: 'rgb(59, 130, 246)',
      borderWidth: 1
    }
  ]
});

const recentActivities = ref([]);

// formatCurrency và formatRelativeTime đã được import từ @/utils/formatters

const updateDateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  currentDate.value = now.toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
};

const loadDashboardData = async (silent = false) => {
  if (!silent) loading.value = true;
  try {
    // Check cache first
    const cacheKey = 'dashboard_stats';
    const cachedStats = getCache(cacheKey);
    
    if (cachedStats && silent) {
      // Use cached data for silent refresh
      stats.value = cachedStats;
    } else {
      // Load stats from API
      const response = await adminStore.fetchDashboardStats();
      const previousStats = { ...stats.value };
      stats.value = response || { totalRevenue: 0, totalOrders: 0, totalProducts: 0, totalUsers: 0 };
      
      // Cache the stats (TTL: 5 minutes)
      setCache(cacheKey, stats.value, 5 * 60 * 1000);
      
      // Calculate trends (so sánh với kỳ trước - ước tính từ 30 ngày trước)
      // Tạm thời tính trend đơn giản: nếu có data mới thì tính % tăng
      if (previousStats.totalRevenue > 0) {
        statsTrends.value.revenue = ((stats.value.totalRevenue - previousStats.totalRevenue) / previousStats.totalRevenue) * 100;
      }
      if (previousStats.totalOrders > 0) {
        statsTrends.value.orders = ((stats.value.totalOrders - previousStats.totalOrders) / previousStats.totalOrders) * 100;
      }
      if (previousStats.totalProducts > 0) {
        statsTrends.value.products = ((stats.value.totalProducts - previousStats.totalProducts) / previousStats.totalProducts) * 100;
      }
      if (previousStats.totalUsers > 0) {
        statsTrends.value.users = ((stats.value.totalUsers - previousStats.totalUsers) / previousStats.totalUsers) * 100;
      }
    }
    
    // Load badges
    await loadBadges();
    
    // Load charts data only if charts are visible
    if (chartsVisible.value.revenue || chartsVisible.value.orderStatus || chartsVisible.value.topProducts) {
      await loadChartsData();
    }
    
    // Load recent activities
    await loadRecentActivities();
    
    lastRefreshTime.value = new Date();
    if (!silent) notificationService.success('Thành công', 'Đã tải dữ liệu dashboard');
  } catch (error) {
    logger.error('Error loading dashboard data:', error);
    if (!silent) notificationService.apiError(error, 'Không thể tải dữ liệu dashboard');
  } finally {
    if (!silent) loading.value = false;
  }
};

const loadBadges = async () => {
  try {
    logger.log('🔄 Loading badges...');
    const badgesData = await AdminService.getDashboardBadges();
    logger.log('📊 Badges data:', badgesData);
    badges.value = badgesData || {};
  } catch (error) {
    logger.error('❌ Error loading badges:', error);
    badges.value = {};
  }
};

const loadChartsData = async () => {
  try {
    logger.log('🔄 Loading charts data for period:', selectedPeriod.value);
    
    // Load revenue chart
    const revenueData = await AdminService.getRevenueAnalytics(selectedPeriod.value);
    logger.log('📊 Revenue data:', revenueData);
    if (revenueData && revenueData.data && revenueData.data.length > 0) {
      const labels = revenueData.data.map(item => {
        const date = new Date(item.date);
        return date.toLocaleDateString('vi-VN', { weekday: 'short' });
      });
      const data = revenueData.data.map(item => item.revenue || 0);
      
      revenueChart.value = {
        labels: labels,
        datasets: [{
          label: 'Doanh thu',
          data: data,
          borderColor: 'rgb(59, 130, 246)',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          fill: true,
          tension: 0.4
        }]
      };
      logger.log('✅ Revenue chart loaded:', revenueChart.value);
    } else {
      logger.warn('⚠️ No revenue data available');
      // Set empty data
      revenueChart.value = {
        labels: [],
        datasets: [{
          label: 'Doanh thu',
          data: [],
          borderColor: 'rgb(59, 130, 246)',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          fill: true,
          tension: 0.4
        }]
      };
    }
    
    // Load order status chart
    const orderStatusData = await AdminService.getOrderStatusAnalytics();
    logger.log('📊 Order status data:', orderStatusData);
    if (orderStatusData && orderStatusData.data && orderStatusData.data.length > 0) {
      const labels = orderStatusData.data.map(item => item.label || item.status);
      const data = orderStatusData.data.map(item => item.count || 0);
      const colors = [
        'rgba(251, 191, 36, 0.8)',   // pending - yellow
        'rgba(59, 130, 246, 0.8)',   // processing - blue
        'rgba(168, 85, 247, 0.8)',   // shipped - purple
        'rgba(34, 197, 94, 0.8)',    // delivered - green
        'rgba(239, 68, 68, 0.8)',    // cancelled - red
        'rgba(251, 191, 36, 0.8)',   // confirmed - yellow
        'rgba(59, 130, 246, 0.8)',   // packed - blue
        'rgba(168, 85, 247, 0.8)'    // refunded - purple
      ];
      
      orderStatusChart.value = {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: colors.slice(0, data.length),
          borderColor: colors.slice(0, data.length).map(c => c.replace('0.8', '1')),
          borderWidth: 2
        }]
      };
      logger.log('✅ Order status chart loaded:', orderStatusChart.value);
    } else {
      logger.warn('⚠️ No order status data available');
      orderStatusChart.value = {
        labels: [],
        datasets: [{
          data: [],
          backgroundColor: [],
          borderColor: [],
          borderWidth: 2
        }]
      };
    }
    
    // Load top products chart
    const topProductsData = await AdminService.getTopProducts(selectedPeriod.value);
    logger.log('📊 Top products data:', topProductsData);
    if (topProductsData && topProductsData.topProducts && topProductsData.topProducts.length > 0) {
      const labels = topProductsData.topProducts.map(p => p.name || 'Unknown').slice(0, 5);
      const data = topProductsData.topProducts.map(p => p.totalSold || 0).slice(0, 5);
      
      topProductsChart.value = {
        labels: labels,
        datasets: [{
          label: 'Số lượng bán',
          data: data,
          backgroundColor: 'rgba(59, 130, 246, 0.8)',
          borderColor: 'rgb(59, 130, 246)',
          borderWidth: 1
        }]
      };
      logger.log('✅ Top products chart loaded:', topProductsChart.value);
    } else {
      logger.warn('⚠️ No top products data available');
      topProductsChart.value = {
        labels: [],
        datasets: [{
          label: 'Số lượng bán',
          data: [],
          backgroundColor: 'rgba(59, 130, 246, 0.8)',
          borderColor: 'rgb(59, 130, 246)',
          borderWidth: 1
        }]
      };
    }
  } catch (error) {
    logger.error('❌ Error loading charts data:', error);
    // Set empty data on error
    revenueChart.value = {
      labels: [],
      datasets: [{
        label: 'Doanh thu',
        data: [],
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        fill: true,
        tension: 0.4
      }]
    };
    orderStatusChart.value = {
      labels: [],
      datasets: [{
        data: [],
        backgroundColor: [],
        borderColor: [],
        borderWidth: 2
      }]
    };
    topProductsChart.value = {
      labels: [],
      datasets: [{
        label: 'Số lượng bán',
        data: [],
        backgroundColor: 'rgba(59, 130, 246, 0.8)',
        borderColor: 'rgb(59, 130, 246)',
        borderWidth: 1
      }]
    };
  }
};

const loadRecentActivities = async () => {
  try {
    logger.log('🔄 Loading recent activities...');
    const activitiesData = await AdminService.getRecentActivities(10);
    logger.log('📊 Activities data:', activitiesData);
    if (activitiesData && activitiesData.activities && activitiesData.activities.length > 0) {
      recentActivities.value = activitiesData.activities.map(activity => ({
        id: activity.id,
        type: activity.type || 'order',
        text: activity.text || 'Hoạt động mới',
        timestamp: activity.timestamp ? new Date(activity.timestamp) : new Date()
      }));
      logger.log('✅ Recent activities loaded:', recentActivities.value);
    } else {
      logger.warn('⚠️ No activities data available');
      recentActivities.value = [];
    }
  } catch (error) {
    logger.error('❌ Error loading recent activities:', error);
    recentActivities.value = [];
  }
};

const startAutoRefresh = () => {
  if (autoRefreshInterval) clearInterval(autoRefreshInterval);
  // OPTIMIZED: Sử dụng configurable interval thay vì hardcoded 30000ms
  const intervalMs = autoRefreshIntervalSeconds.value * 1000;
  autoRefreshInterval = setInterval(() => {
    if (autoRefreshEnabled.value) loadDashboardData(true);
  }, intervalMs);
};

const stopAutoRefresh = () => {
  if (autoRefreshInterval) {
    clearInterval(autoRefreshInterval);
    autoRefreshInterval = null;
  }
};

const toggleAutoRefresh = () => {
  autoRefreshEnabled.value = !autoRefreshEnabled.value;
  if (autoRefreshEnabled.value) {
    startAutoRefresh();
    notificationService.success('Tự động làm mới', `Đã bật tự động làm mới mỗi ${autoRefreshIntervalSeconds.value} giây`);
  } else {
    stopAutoRefresh();
    notificationService.info('Tự động làm mới', 'Đã tắt tự động làm mới');
  }
};

const manualRefresh = () => {
  loadDashboardData();
  notificationService.info('Làm mới', 'Đang tải lại dữ liệu...');
};

const changePeriod = async (period) => {
  selectedPeriod.value = period;
  loading.value = true;
  try {
    await loadChartsData();
    notificationService.info('Thay đổi chu kỳ', `Đang hiển thị dữ liệu ${period === '7d' ? '7 ngày' : period === '30d' ? '30 ngày' : '90 ngày'}`);
  } catch (error) {
    logger.error('Error changing period:', error);
    notificationService.apiError(error, 'Không thể tải dữ liệu cho chu kỳ này');
  } finally {
    loading.value = false;
  }
};

const toggleProfileMenu = () => {
  showProfileMenu.value = !showProfileMenu.value;
};

const handleProfileEdit = () => {
  showProfileMenu.value = false;
  notificationService.info('Hồ sơ', 'Chức năng đang được phát triển');
};

const handleSettings = () => {
  showProfileMenu.value = false;
  router.push('/admin/settings');
};

const handleChangePassword = () => {
  showProfileMenu.value = false;
  notificationService.info('Đổi mật khẩu', 'Chức năng đang được phát triển');
};

const handleLogout = () => {
  showProfileMenu.value = false;
  notificationService.success('Đăng xuất', 'Đang đăng xuất...');
  setTimeout(() => {
    authStore.logout();
    adminStore.reset();
    localStorage.clear();
    window.location.href = '/login';
  }, 1000);
};

const showDateRangePicker = ref(false);
const customDateRange = ref({
  start: null,
  end: null
});

const applyCustomDateRange = (range) => {
  showDateRangePicker.value = false;
  selectedPeriod.value = 'custom';
  if (range && range.start && range.end) {
    customDateRange.value = range;
    notificationService.info('Thay đổi khoảng thời gian', `Đang hiển thị dữ liệu từ ${new Date(range.start).toLocaleDateString('vi-VN')} đến ${new Date(range.end).toLocaleDateString('vi-VN')}`);
    // Reload data with custom date range
    loadDashboardData();
  }
};

const exportReports = async () => {
  try {
    notificationService.info('Xuất báo cáo', 'Đang tạo báo cáo...');
    
    // Create CSV content
    const csvContent = [
      ['Thống kê Dashboard - Sneakery Store'],
      ['Ngày xuất:', new Date().toLocaleString('vi-VN')],
      [],
      ['Chỉ số', 'Giá trị'],
      ['Tổng người dùng', stats.value.totalUsers],
      ['Tổng sản phẩm', stats.value.totalProducts],
      ['Tổng đơn hàng', stats.value.totalOrders],
      ['Tổng doanh thu', formatCurrency(stats.value.totalRevenue)]
    ];
    
    // Convert to CSV format
    const csv = csvContent.map(row => row.join(',')).join('\n');
    
    // Create and download file
    const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `dashboard-report-${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    notificationService.success('Xuất báo cáo', 'Đã xuất báo cáo thành công!');
  } catch (error) {
    logger.error('Error exporting reports:', error);
    notificationService.apiError(error, 'Không thể xuất báo cáo');
  }
};

let timeInterval;
let handleClickOutside;
const revenueChartRef = ref(null);
const orderStatusChartRef = ref(null);
const topProductsChartRef = ref(null);

// Setup Intersection Observer for lazy loading charts
const setupChartObservers = () => {
  if (!window.IntersectionObserver) {
    // Fallback: load all charts immediately if IntersectionObserver is not supported
    chartsVisible.value = { revenue: true, orderStatus: true, topProducts: true };
    loadChartsData();
    return;
  }

  const observerOptions = {
    root: null,
    rootMargin: '50px',
    threshold: 0.1
  };

  const observerCallback = (entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const chartType = entry.target.dataset.chartType;
        if (chartType && !chartsVisible.value[chartType]) {
          chartsVisible.value[chartType] = true;
          loadChartsData();
        }
      }
    });
  };

  const observer = new IntersectionObserver(observerCallback, observerOptions);

  // Observe chart containers
  if (revenueChartRef.value) {
    revenueChartRef.value.dataset.chartType = 'revenue';
    observer.observe(revenueChartRef.value);
    chartObservers.push({ observer, element: revenueChartRef.value });
  }
  if (orderStatusChartRef.value) {
    orderStatusChartRef.value.dataset.chartType = 'orderStatus';
    observer.observe(orderStatusChartRef.value);
    chartObservers.push({ observer, element: orderStatusChartRef.value });
  }
  if (topProductsChartRef.value) {
    topProductsChartRef.value.dataset.chartType = 'topProducts';
    observer.observe(topProductsChartRef.value);
    chartObservers.push({ observer, element: topProductsChartRef.value });
  }
};

onMounted(() => {
  loadDashboardData();
  updateDateTime();
  timeInterval = setInterval(updateDateTime, 1000);
  startAutoRefresh();
  
  // Setup chart observers after DOM is ready
  setTimeout(() => {
    setupChartObservers();
  }, 100);
  
  setTimeout(() => notificationService.info('Chào mừng!', 'Chào mừng bạn quay trở lại Admin Dashboard'), 500);
  handleClickOutside = (e) => {
    if (!e.target.closest('.relative')) showProfileMenu.value = false;
  };
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval);
  if (handleClickOutside) document.removeEventListener('click', handleClickOutside);
  stopAutoRefresh();
  
  // Cleanup chart observers
  chartObservers.forEach(({ observer, element }) => {
    observer.unobserve(element);
    observer.disconnect();
  });
  chartObservers = [];
});
</script>


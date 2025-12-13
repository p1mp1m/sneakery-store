<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">analytics</i>
            Phân tích & Báo cáo
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Xem chi tiết hiệu suất kinh doanh và xu hướng</p>
        </div>
        <div>
          <select v-model="selectedPeriod" @change="loadAnalytics" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
            <option value="7d">7 ngày qua</option>
            <option value="30d">30 ngày qua</option>
            <option value="90d">90 ngày qua</option>
            <option value="1y">1 năm qua</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">attach_money</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatCurrency(totalRevenue) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng doanh thu</p>
          <p class="text-xs flex items-center gap-1" :class="analyticsTrends.revenue > 0 ? 'text-green-600 dark:text-green-400' : analyticsTrends.revenue < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="analyticsTrends.revenue > 0 ? 'trending_up' : analyticsTrends.revenue < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="analyticsTrends.revenue !== 0">{{ analyticsTrends.revenue > 0 ? '+' : '' }}{{ analyticsTrends.revenue.toFixed(1) }}% so với kỳ trước</span>
            <span v-else>Không đổi so với kỳ trước</span>
          </p>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">shopping_cart</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ totalOrders.toLocaleString() }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Tổng đơn hàng</p>
          <p class="text-xs flex items-center gap-1" :class="analyticsTrends.orders > 0 ? 'text-green-600 dark:text-green-400' : analyticsTrends.orders < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="analyticsTrends.orders > 0 ? 'trending_up' : analyticsTrends.orders < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="analyticsTrends.orders !== 0">{{ analyticsTrends.orders > 0 ? '+' : '' }}{{ analyticsTrends.orders.toFixed(1) }}% so với kỳ trước</span>
            <span v-else>Không đổi so với kỳ trước</span>
          </p>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">group_add</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ newCustomers }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Khách hàng mới</p>
          <p class="text-xs flex items-center gap-1" :class="analyticsTrends.customers > 0 ? 'text-green-600 dark:text-green-400' : analyticsTrends.customers < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="analyticsTrends.customers > 0 ? 'trending_up' : analyticsTrends.customers < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="analyticsTrends.customers !== 0">{{ analyticsTrends.customers > 0 ? '+' : '' }}{{ analyticsTrends.customers.toFixed(1) }}% so với kỳ trước</span>
            <span v-else>Không đổi so với kỳ trước</span>
          </p>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-red-500 to-red-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">trending_down</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ formatCurrency(avgOrderValue) }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">Đơn hàng TB</p>
          <p class="text-xs flex items-center gap-1" :class="analyticsTrends.avgOrderValue > 0 ? 'text-green-600 dark:text-green-400' : analyticsTrends.avgOrderValue < 0 ? 'text-red-600 dark:text-red-400' : 'text-gray-500 dark:text-gray-400'">
            <i class="material-icons text-sm" :class="analyticsTrends.avgOrderValue > 0 ? 'trending_up' : analyticsTrends.avgOrderValue < 0 ? 'trending_down' : 'trending_flat'"></i>
            <span v-if="analyticsTrends.avgOrderValue !== 0">{{ analyticsTrends.avgOrderValue > 0 ? '+' : '' }}{{ analyticsTrends.avgOrderValue.toFixed(1) }}% so với kỳ trước</span>
            <span v-else>Không đổi so với kỳ trước</span>
          </p>
        </div>
      </div>
    </div>

    <!-- Biểu đồ doanh thu chi tiết -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-4">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400">show_chart</i>
          Doanh thu theo thời gian
        </h2>
        <div class="flex items-center gap-2">
          <button 
            v-for="type in ['day', 'week', 'month']" 
            :key="type"
            :class="['px-3 py-1.5 text-sm font-medium rounded-lg transition-colors', revenueChartType === type ? 'bg-purple-500 text-white' : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600']"
            @click="revenueChartType = type"
          >
            {{ type === 'day' ? 'Ngày' : type === 'week' ? 'Tuần' : 'Tháng' }}
          </button>
        </div>
      </div>
      <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
        <LineChart 
          :labels="revenueChartData.labels" 
          :datasets="revenueChartData.datasets" 
          :options="revenueChartOptions" 
        />
      </div>
    </div>

    <!-- Biểu đồ 2 cột -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <!-- Phân tích đơn hàng theo trạng thái -->
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="mb-4">
          <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">pie_chart</i>
            Đơn hàng theo trạng thái
          </h2>
        </div>
        <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
          <DoughnutChart 
            :labels="orderStatusData.labels" 
            :datasets="orderStatusData.datasets" 
            :options="doughnutOptions" 
          />
        </div>
      </div>

      <!-- Top sản phẩm bán chạy -->
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="mb-4">
          <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">emoji_events</i>
            Top sản phẩm bán chạy
          </h2>
        </div>
        <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
          <BarChart 
            :labels="topProductsData.labels" 
            :datasets="topProductsData.datasets" 
            :options="barChartOptions" 
          />
        </div>
      </div>
    </div>

    <!-- Phân tích theo danh mục -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="mb-4">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400">category</i>
          Doanh thu theo danh mục
        </h2>
      </div>
      <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
        <BarChart 
          :labels="categoryRevenueData.labels" 
          :datasets="categoryRevenueData.datasets" 
          :options="categoryChartOptions" 
        />
      </div>
    </div>

    <!-- Biểu đồ 2 cột - Phân tích khách hàng -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <!-- Khách hàng mới theo thời gian -->
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="mb-4">
          <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">person_add</i>
            Khách hàng mới
          </h2>
        </div>
        <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
          <LineChart 
            :labels="newCustomersData.labels" 
            :datasets="newCustomersData.datasets" 
            :options="lineChartOptions" 
          />
        </div>
      </div>

      <!-- Tỷ lệ chuyển đổi -->
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="mb-4">
          <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">track_changes</i>
            Tỷ lệ chuyển đổi
          </h2>
        </div>
        <div class="p-4 bg-gray-50 dark:bg-gray-900/50 rounded-lg">
          <div v-if="conversionRateData.length === 0" class="text-center py-8 text-gray-500 dark:text-gray-400">
            Đang tải dữ liệu...
          </div>
          <div v-else class="space-y-3">
            <div 
              v-for="(step, index) in conversionRateData" 
              :key="index"
              class="flex items-center gap-3"
            >
              <div 
                class="h-8 rounded-lg flex items-center justify-center text-white text-xs font-medium"
                :class="getConversionStepColor(index)"
                :style="{ width: `${step.percentage}%` }"
              >
                {{ Math.round(step.percentage) }}%
              </div>
              <span class="text-sm text-gray-700 dark:text-gray-300 whitespace-nowrap">{{ step.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng top sản phẩm chi tiết -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="mb-4">
        <h2 class="text-base font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400">format_list_numbered</i>
          Chi tiết sản phẩm bán chạy
        </h2>
      </div>
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Hạng</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Tên sản phẩm</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Danh mục</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Đã bán</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Doanh thu</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Tăng trưởng</th>
              </tr>
            </thead>
            <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              <tr v-for="(product, index) in topProductsTable" :key="index" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
                <td class="px-4 py-4">
                  <span class="inline-flex items-center justify-center w-8 h-8 rounded-full bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400 font-semibold text-sm">
                    {{ index + 1 }}
                  </span>
                </td>
                <td class="px-4 py-4 text-sm font-medium text-gray-900 dark:text-gray-100">{{ product.name }}</td>
                <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">{{ product.category }}</td>
                <td class="px-4 py-4 text-sm text-gray-900 dark:text-gray-100">{{ product.sold }}</td>
                <td class="px-4 py-4 text-sm font-semibold text-gray-900 dark:text-gray-100">{{ formatCurrency(product.revenue) }}</td>
                <td class="px-4 py-4">
                  <span 
                    class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full"
                    :class="product.growth > 0 
                      ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' 
                      : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'"
                  >
                    <i class="material-icons text-sm">{{ product.growth > 0 ? 'trending_up' : 'trending_down' }}</i>
                    {{ product.growth > 0 ? '+' : '' }}{{ product.growth || 0 }}%
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import AdminService from '@/services/adminService'
import notificationService from '@/utils/notificationService'
import LineChart from '@/assets/components/charts/LineChart.vue'
import BarChart from '@/assets/components/charts/BarChart.vue'
import DoughnutChart from '@/assets/components/charts/DoughnutChart.vue'
import logger from '@/utils/logger'
import { formatPrice, formatCurrency } from '@/utils/formatters'

const adminStore = useAdminStore()

// Refs
const loading = ref(false)
const selectedPeriod = ref('30d')
const revenueChartType = ref('day')

// Analytics data
const revenueData = ref([])
const orderData = ref([])
const productData = ref([])
const customerData = ref([])
const topProductsTable = ref([])
const conversionRateData = ref([])

// Dashboard stats từ API (tổng quan)
const dashboardStats = ref({
  totalRevenue: 0,
  totalOrders: 0,
  totalProducts: 0,
  totalUsers: 0
})

// Stats từ kỳ trước để tính trends
const previousPeriodStats = ref({
  revenue: 0,
  orders: 0,
  customers: 0,
  avgOrderValue: 0
})

// Trends
const analyticsTrends = ref({
  revenue: 0,
  orders: 0,
  customers: 0,
  avgOrderValue: 0
})

// Computed - ưu tiên dùng dashboard stats từ API, nếu không có thì tính từ analytics data
const totalRevenue = computed(() => {
  // Nếu có dashboard stats từ API thì dùng, nếu không thì tính từ revenueData
  if (dashboardStats.value.totalRevenue > 0) {
    return dashboardStats.value.totalRevenue;
  }
  return revenueData.value.reduce((sum, item) => sum + (item.revenue || 0), 0);
})

const totalOrders = computed(() => {
  // Nếu có dashboard stats từ API thì dùng, nếu không thì tính từ orderData
  if (dashboardStats.value.totalOrders > 0) {
    return dashboardStats.value.totalOrders;
  }
  return orderData.value.reduce((sum, item) => sum + (item.orders || 0), 0);
})

const newCustomers = computed(() => {
  // Tính từ customerData
  return customerData.value.reduce((sum, item) => sum + (item.newCustomers || 0), 0);
})

const avgOrderValue = computed(() => {
  const orders = totalOrders.value;
  const revenue = totalRevenue.value;
  return orders > 0 ? revenue / orders : 0;
})

// Dữ liệu biểu đồ doanh thu
const revenueChartData = computed(() => ({
  labels: revenueData.value.map(item => item.date),
  datasets: [
    {
      label: 'Doanh thu (VNĐ)',
      data: revenueData.value.map(item => item.revenue),
      borderColor: '#3b82f6',
      backgroundColor: 'rgba(59, 130, 246, 0.1)',
      fill: true,
      tension: 0.4
    }
  ]
}))

const revenueChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'top'
    },
    tooltip: {
      callbacks: {
        label: (context) => {
          return `Doanh thu: ${formatCurrency(context.parsed.y)}`
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: (value) => {
          return (value / 1000000).toFixed(0) + 'tr'
        }
      }
    }
  }
}

// Dữ liệu đơn hàng theo trạng thái (load từ API)
const orderStatusData = ref({
  labels: [],
  datasets: [{
    data: [],
    backgroundColor: [],
    borderWidth: 0
  }]
})

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right'
    }
  }
}

// Top sản phẩm bán chạy (Bar chart)
const topProductsData = computed(() => ({
  labels: topProductsTable.value.map(item => item.name),
  datasets: [{
    label: 'Số lượng bán',
    data: topProductsTable.value.map(item => item.sold),
    backgroundColor: '#3b82f6',
    borderRadius: 8
  }]
}))

const barChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'y',
  plugins: {
    legend: {
      display: false
    }
  },
  scales: {
    x: {
      beginAtZero: true
    }
  }
}

// Doanh thu theo danh mục
const categoryRevenueData = computed(() => ({
  labels: productData.value.map(item => item.category),
  datasets: [{
    label: 'Doanh thu (Triệu VNĐ)',
    data: productData.value.map(item => item.revenue),
    backgroundColor: [
      '#3b82f6',
      '#8b5cf6',
      '#ec4899',
      '#f59e0b',
      '#10b981'
    ],
    borderRadius: 8
  }]
}))

const categoryChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    }
  },
  scales: {
    y: {
      beginAtZero: true
    }
  }
}

// Khách hàng mới
const newCustomersData = computed(() => ({
  labels: customerData.value.map(item => item.date),
  datasets: [{
    label: 'Khách hàng mới',
    data: customerData.value.map(item => item.newCustomers),
    borderColor: '#10b981',
    backgroundColor: 'rgba(16, 185, 129, 0.1)',
    fill: true,
    tension: 0.4
  }]
}))

const lineChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    }
  },
  scales: {
    y: {
      beginAtZero: true
    }
  }
}

// Methods
const loadAnalytics = async () => {
  try {
    loading.value = true
    
    // Load dashboard stats từ API (tổng quan)
    try {
      const statsResult = await adminStore.fetchDashboardStats();
      if (statsResult) {
        dashboardStats.value = statsResult;
        logger.log('✅ Dashboard stats loaded:', dashboardStats.value);
      }
    } catch (error) {
      logger.warn('Dashboard stats API error:', error);
    }
    
    // Load stats compare để tính trends (so sánh với kỳ trước)
    try {
      const compareResult = await AdminService.getStatsCompare(selectedPeriod.value);
      if (compareResult && compareResult.trends) {
        analyticsTrends.value = {
          revenue: compareResult.trends.revenue || 0,
          orders: compareResult.trends.orders || 0,
          customers: compareResult.trends.customers || 0,
          avgOrderValue: compareResult.trends.avgOrderValue || 0
        };
        logger.log('✅ Stats trends loaded:', analyticsTrends.value);
      } else {
        logger.warn('Stats compare API không trả về trends');
        analyticsTrends.value = { revenue: 0, orders: 0, customers: 0, avgOrderValue: 0 };
      }
    } catch (error) {
      logger.warn('Stats compare API error:', error);
      analyticsTrends.value = { revenue: 0, orders: 0, customers: 0, avgOrderValue: 0 };
    }
    
    // Gọi API thật từ backend
    try {
      // Revenue analytics
      const revenueResult = await adminStore.fetchRevenueAnalytics(selectedPeriod.value)
      if (revenueResult && revenueResult.data) {
        revenueData.value = revenueResult.data
        logger.log('✅ Revenue analytics loaded:', revenueData.value.length, 'days');
      } else {
        logger.warn('Revenue analytics API chưa sẵn sàng')
        revenueData.value = []
      }
    } catch (error) {
      logger.warn('Revenue analytics API error:', error)
      revenueData.value = []
    }
    
    try {
      // Order analytics
      const orderResult = await adminStore.fetchOrderAnalytics(selectedPeriod.value)
      if (orderResult && orderResult.data) {
        orderData.value = orderResult.data
      } else {
        logger.warn('Order analytics API chưa sẵn sàng')
        orderData.value = []
      }
    } catch (error) {
      logger.warn('Order analytics API error:', error)
      orderData.value = []
    }
    
    try {
      // Product analytics
      const productResult = await adminStore.fetchProductAnalytics(selectedPeriod.value)
      if (productResult) {
        // Backend trả về totalProducts, topProducts, period (không có data)
        if (productResult.data) {
          productData.value = productResult.data
        } else {
          // Nếu không có data, sử dụng mảng rỗng (backend chỉ trả về topProducts)
          productData.value = []
        }
        // Extract top products if available
        if (productResult.topProducts && Array.isArray(productResult.topProducts)) {
          topProductsTable.value = productResult.topProducts
        } else {
          topProductsTable.value = []
        }
      } else {
        logger.warn('Product analytics API chưa sẵn sàng')
        productData.value = []
        topProductsTable.value = []
      }
    } catch (error) {
      logger.warn('Product analytics API error:', error)
      productData.value = []
      topProductsTable.value = []
    }
    
    try {
      // Customer analytics
      const customerResult = await adminStore.fetchCustomerAnalytics(selectedPeriod.value)
      if (customerResult && customerResult.data) {
        customerData.value = customerResult.data
      } else {
        logger.warn('Customer analytics API chưa sẵn sàng')
        customerData.value = []
      }
    } catch (error) {
      logger.warn('Customer analytics API error:', error)
      customerData.value = []
    }
    
    // Load order status data
    try {
      const orderStatusResult = await AdminService.getOrderStatusAnalytics()
      if (orderStatusResult && orderStatusResult.data && orderStatusResult.data.length > 0) {
        const labels = orderStatusResult.data.map(item => item.label || item.status)
        const data = orderStatusResult.data.map(item => item.count || 0)
        const colors = [
          '#10b981',  // delivered - green
          '#f59e0b',  // processing - orange
          '#3b82f6',  // shipped - blue
          '#ef4444',  // cancelled - red
          '#8b5cf6',  // pending - purple
          '#f59e0b',  // confirmed - orange
          '#3b82f6',  // packed - blue
          '#8b5cf6'   // refunded - purple
        ]
        
        orderStatusData.value = {
          labels: labels,
          datasets: [{
            data: data,
            backgroundColor: colors.slice(0, data.length),
            borderWidth: 0
          }]
        }
        logger.log('✅ Order status data loaded:', orderStatusData.value)
      } else {
        logger.warn('⚠️ No order status data available')
        orderStatusData.value = {
          labels: [],
          datasets: [{ data: [], backgroundColor: [], borderWidth: 0 }]
        }
      }
    } catch (error) {
      logger.error('❌ Error loading order status data:', error)
      orderStatusData.value = {
        labels: [],
        datasets: [{ data: [], backgroundColor: [], borderWidth: 0 }]
      }
    }
    
    // Load conversion rate data
    try {
      const conversionResult = await AdminService.getConversionRate()
      if (conversionResult && conversionResult.data && conversionResult.data.length > 0) {
        conversionRateData.value = conversionResult.data
        logger.log('✅ Conversion rate data loaded:', conversionRateData.value)
      } else {
        logger.warn('⚠️ No conversion rate data available')
        conversionRateData.value = []
      }
    } catch (error) {
      logger.error('❌ Error loading conversion rate data:', error)
      conversionRateData.value = []
    }
    
    // Nếu không có dữ liệu từ API, hiển thị empty state
    if (revenueData.value.length === 0 && orderData.value.length === 0) {
      notificationService.warning('Cảnh báo','Chưa có dữ liệu phân tích cho kỳ này. Vui lòng thử lại sau.')
    } else {
      logger.log('✅ Analytics data loaded successfully from API')
    }
  } catch (error) {
    logger.error('Error loading analytics:', error)
    notificationService.apiError(error, 'Không thể tải dữ liệu phân tích')
  } finally {
    loading.value = false
  }
}

// formatCurrency đã được import từ @/utils/formatters

const getConversionStepColor = (index) => {
  const colors = [
    'bg-purple-500',   // Lượt truy cập
    'bg-blue-500',     // Xem sản phẩm
    'bg-green-500',    // Thêm vào giỏ
    'bg-yellow-500',   // Thanh toán
    'bg-emerald-500'   // Hoàn thành
  ]
  return colors[index] || 'bg-gray-500'
}

// Load data on mount
onMounted(() => {
  loadAnalytics()
})
</script>




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
          <p class="text-xs text-green-600 dark:text-green-400 flex items-center gap-1">
            <i class="material-icons text-sm">trending_up</i>
            +12.5% so với kỳ trước
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
          <p class="text-xs text-green-600 dark:text-green-400 flex items-center gap-1">
            <i class="material-icons text-sm">trending_up</i>
            +8.3% so với kỳ trước
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
          <p class="text-xs text-green-600 dark:text-green-400 flex items-center gap-1">
            <i class="material-icons text-sm">trending_up</i>
            +15.7% so với kỳ trước
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
          <p class="text-xs text-red-600 dark:text-red-400 flex items-center gap-1">
            <i class="material-icons text-sm">trending_down</i>
            -2.1% so với kỳ trước
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
          <div class="space-y-3">
            <div class="flex items-center gap-3">
              <div class="w-full h-8 bg-purple-500 rounded-lg flex items-center justify-center text-white text-xs font-medium">100%</div>
              <span class="text-sm text-gray-700 dark:text-gray-300 whitespace-nowrap">Lượt truy cập: 10,000</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="h-8 bg-blue-500 rounded-lg flex items-center justify-center text-white text-xs font-medium" style="width: 60%">60%</div>
              <span class="text-sm text-gray-700 dark:text-gray-300 whitespace-nowrap">Xem sản phẩm: 6,000 (60%)</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="h-8 bg-green-500 rounded-lg flex items-center justify-center text-white text-xs font-medium" style="width: 30%">30%</div>
              <span class="text-sm text-gray-700 dark:text-gray-300 whitespace-nowrap">Thêm vào giỏ: 3,000 (30%)</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="h-8 bg-yellow-500 rounded-lg flex items-center justify-center text-white text-xs font-medium" style="width: 15%">15%</div>
              <span class="text-sm text-gray-700 dark:text-gray-300 whitespace-nowrap">Thanh toán: 1,500 (15%)</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="h-8 bg-emerald-500 rounded-lg flex items-center justify-center text-white text-xs font-medium" style="width: 12%">12%</div>
              <span class="text-sm text-gray-700 dark:text-gray-300 whitespace-nowrap">Hoàn thành: 1,200 (12%)</span>
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
import { ElMessage } from 'element-plus'
import LineChart from '@/assets/components/charts/LineChart.vue'
import BarChart from '@/assets/components/charts/BarChart.vue'
import DoughnutChart from '@/assets/components/charts/DoughnutChart.vue'

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

// Computed
const totalRevenue = computed(() => {
  return revenueData.value.reduce((sum, item) => sum + item.revenue, 0)
})

const totalOrders = computed(() => {
  return orderData.value.reduce((sum, item) => sum + item.orders, 0)
})

const newCustomers = computed(() => {
  return customerData.value.reduce((sum, item) => sum + item.newCustomers, 0)
})

const avgOrderValue = computed(() => {
  return totalOrders.value > 0 ? totalRevenue.value / totalOrders.value : 0
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

// Dữ liệu đơn hàng theo trạng thái
const orderStatusData = computed(() => ({
  labels: ['Hoàn thành', 'Đang xử lý', 'Đang giao', 'Đã hủy'],
  datasets: [{
    data: [650, 280, 230, 87],
    backgroundColor: [
      '#10b981',
      '#f59e0b',
      '#3b82f6',
      '#ef4444'
    ],
    borderWidth: 0
  }]
}))

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
    
    // TODO: Replace with actual API call when ready
    // const result = await adminStore.fetchAnalytics(selectedPeriod.value)
    
    // Mock data cho các APIs chưa sẵn sàng
    const mockRevenueData = [
      { date: '2024-01-01', revenue: 15000000 },
      { date: '2024-01-02', revenue: 18000000 },
      { date: '2024-01-03', revenue: 22000000 },
      { date: '2024-01-04', revenue: 19000000 },
      { date: '2024-01-05', revenue: 25000000 },
      { date: '2024-01-06', revenue: 28000000 },
      { date: '2024-01-07', revenue: 30000000 }
    ]
    
    const mockOrderData = [
      { date: '2024-01-01', orders: 45 },
      { date: '2024-01-02', orders: 52 },
      { date: '2024-01-03', orders: 68 },
      { date: '2024-01-04', orders: 58 },
      { date: '2024-01-05', orders: 72 },
      { date: '2024-01-06', orders: 85 },
      { date: '2024-01-07', orders: 92 }
    ]
    
    const mockProductData = [
      { category: 'Giày thể thao', revenue: 45000000 },
      { category: 'Giày chạy bộ', revenue: 32000000 },
      { category: 'Giày bóng đá', revenue: 28000000 },
      { category: 'Giày bóng rổ', revenue: 25000000 },
      { category: 'Giày lifestyle', revenue: 18000000 }
    ]
    
    const mockCustomerData = [
      { date: '2024-01-01', newCustomers: 12 },
      { date: '2024-01-02', newCustomers: 15 },
      { date: '2024-01-03', newCustomers: 18 },
      { date: '2024-01-04', newCustomers: 14 },
      { date: '2024-01-05', newCustomers: 20 },
      { date: '2024-01-06', newCustomers: 22 },
      { date: '2024-01-07', newCustomers: 25 }
    ]
    
    const mockTopProducts = [
      { name: 'Nike Air Max 270', category: 'Giày thể thao', sold: 156, revenue: 46800000, growth: 12.5 },
      { name: 'Adidas Ultraboost 22', category: 'Giày chạy bộ', sold: 142, revenue: 42600000, growth: 8.3 },
      { name: 'Jordan 1 Retro', category: 'Giày bóng rổ', sold: 128, revenue: 38400000, growth: 15.7 },
      { name: 'Converse Chuck Taylor', category: 'Giày lifestyle', sold: 115, revenue: 23000000, growth: -2.1 },
      { name: 'Vans Old Skool', category: 'Giày lifestyle', sold: 98, revenue: 19600000, growth: 5.2 }
    ]
    
    // Set mock data
    revenueData.value = mockRevenueData
    orderData.value = mockOrderData
    productData.value = mockProductData
    customerData.value = mockCustomerData
    topProductsTable.value = mockTopProducts
    
    console.log('✅ Analytics data loaded successfully')
  } catch (error) {
    console.error('Error loading analytics:', error)
    ElMessage.error('Không thể tải dữ liệu phân tích')
  } finally {
    loading.value = false
  }
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

// Load data on mount
onMounted(() => {
  loadAnalytics()
})
</script>




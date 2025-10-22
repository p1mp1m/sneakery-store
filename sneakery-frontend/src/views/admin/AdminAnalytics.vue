<template>
  <div class="admin-page admin-analytics">
    <!-- Page Header -->
    <div class="analytics-header animate-fade-in">
      <div class="header-left">
        <h1 class="page-title">
          <i class="material-icons">analytics</i>
          Phân tích & Báo cáo
        </h1>
        <p class="page-subtitle">Xem chi tiết hiệu suất kinh doanh và xu hướng</p>
      </div>
      <div class="header-right">
        <select v-model="selectedPeriod" @change="loadAnalytics" class="period-select">
          <option value="7d">7 ngày qua</option>
          <option value="30d">30 ngày qua</option>
          <option value="90d">90 ngày qua</option>
          <option value="1y">1 năm qua</option>
        </select>
      </div>
    </div>

    <!-- Stats Grid - Sử dụng stats-grid từ admin-panel.css -->
    <div class="stats-grid animate-fade-up">
      <div class="stats-card success">
        <div class="stats-header">
          <div class="stats-info">
            <p class="stats-label">Tổng doanh thu</p>
            <h3 class="stats-value">{{ formatCurrency(totalRevenue) }}</h3>
          </div>
          <div class="stats-icon success">
            <i class="material-icons">attach_money</i>
          </div>
        </div>
        <p class="stats-change positive">
          <i class="material-icons">trending_up</i>
          +12.5% so với kỳ trước
        </p>
      </div>

      <div class="stats-card info">
        <div class="stats-header">
          <div class="stats-info">
            <p class="stats-label">Tổng đơn hàng</p>
            <h3 class="stats-value">{{ totalOrders.toLocaleString() }}</h3>
          </div>
          <div class="stats-icon info">
            <i class="material-icons">shopping_cart</i>
          </div>
        </div>
        <p class="stats-change positive">
          <i class="material-icons">trending_up</i>
          +8.3% so với kỳ trước
        </p>
      </div>

      <div class="stats-card warning">
        <div class="stats-header">
          <div class="stats-info">
            <p class="stats-label">Khách hàng mới</p>
            <h3 class="stats-value">{{ newCustomers }}</h3>
          </div>
          <div class="stats-icon warning">
            <i class="material-icons">group_add</i>
          </div>
        </div>
        <p class="stats-change positive">
          <i class="material-icons">trending_up</i>
          +15.7% so với kỳ trước
        </p>
      </div>

      <div class="stats-card danger">
        <div class="stats-header">
          <div class="stats-info">
            <p class="stats-label">Đơn hàng TB</p>
            <h3 class="stats-value">{{ formatCurrency(avgOrderValue) }}</h3>
          </div>
          <div class="stats-icon danger">
            <i class="material-icons">trending_down</i>
          </div>
        </div>
        <p class="stats-change negative">
          <i class="material-icons">trending_down</i>
          -2.1% so với kỳ trước
        </p>
      </div>
    </div>

    <!-- Biểu đồ doanh thu chi tiết -->
    <div class="analytics-section animate-slide-in">
      <div class="section-header">
        <h2>
          <i class="material-icons">show_chart</i>
          Doanh thu theo thời gian
        </h2>
        <div class="chart-controls">
          <button 
            v-for="type in ['day', 'week', 'month']" 
            :key="type"
            :class="['btn-chart-type', { active: revenueChartType === type }]"
            @click="revenueChartType = type"
          >
            {{ type === 'day' ? 'Ngày' : type === 'week' ? 'Tuần' : 'Tháng' }}
          </button>
        </div>
      </div>
      <div class="chart-card large">
        <LineChart 
          :labels="revenueChartData.labels" 
          :datasets="revenueChartData.datasets" 
          :options="revenueChartOptions" 
        />
      </div>
    </div>

    <!-- Biểu đồ 2 cột -->
    <div class="charts-grid-2 animate-slide-in">
      <!-- Phân tích đơn hàng theo trạng thái -->
      <div class="analytics-section">
        <div class="section-header">
          <h2>
            <i class="material-icons">pie_chart</i>
            Đơn hàng theo trạng thái
          </h2>
        </div>
        <div class="chart-card">
          <DoughnutChart 
            :labels="orderStatusData.labels" 
            :datasets="orderStatusData.datasets" 
            :options="doughnutOptions" 
          />
        </div>
      </div>

      <!-- Top sản phẩm bán chạy -->
      <div class="analytics-section">
        <div class="section-header">
          <h2>
            <i class="material-icons">emoji_events</i>
            Top sản phẩm bán chạy
          </h2>
        </div>
        <div class="chart-card">
          <BarChart 
            :labels="topProductsData.labels" 
            :datasets="topProductsData.datasets" 
            :options="barChartOptions" 
          />
        </div>
      </div>
    </div>

    <!-- Phân tích theo danh mục -->
    <div class="analytics-section animate-slide-in">
      <div class="section-header">
        <h2>
          <i class="material-icons">category</i>
          Doanh thu theo danh mục
        </h2>
      </div>
      <div class="chart-card">
        <BarChart 
          :labels="categoryRevenueData.labels" 
          :datasets="categoryRevenueData.datasets" 
          :options="categoryChartOptions" 
        />
      </div>
    </div>

    <!-- Biểu đồ 2 cột - Phân tích khách hàng -->
    <div class="charts-grid-2 animate-slide-in">
      <!-- Khách hàng mới theo thời gian -->
      <div class="analytics-section">
        <div class="section-header">
          <h2>
            <i class="material-icons">person_add</i>
            Khách hàng mới
          </h2>
        </div>
        <div class="chart-card">
          <LineChart 
            :labels="newCustomersData.labels" 
            :datasets="newCustomersData.datasets" 
            :options="lineChartOptions" 
          />
        </div>
      </div>

      <!-- Tỷ lệ chuyển đổi -->
      <div class="analytics-section">
        <div class="section-header">
          <h2>
            <i class="material-icons">track_changes</i>
            Tỷ lệ chuyển đổi
          </h2>
        </div>
        <div class="chart-card">
          <div class="conversion-funnel">
            <div class="funnel-step">
              <div class="funnel-bar" style="width: 100%">
                <span>Lượt truy cập: 10,000</span>
              </div>
            </div>
            <div class="funnel-step">
              <div class="funnel-bar" style="width: 60%">
                <span>Xem sản phẩm: 6,000 (60%)</span>
              </div>
            </div>
            <div class="funnel-step">
              <div class="funnel-bar" style="width: 30%">
                <span>Thêm vào giỏ: 3,000 (30%)</span>
              </div>
            </div>
            <div class="funnel-step">
              <div class="funnel-bar" style="width: 15%">
                <span>Thanh toán: 1,500 (15%)</span>
              </div>
            </div>
            <div class="funnel-step">
              <div class="funnel-bar" style="width: 12%">
                <span>Hoàn thành: 1,200 (12%)</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bảng top sản phẩm chi tiết -->
    <div class="analytics-section animate-slide-in">
      <div class="section-header">
        <h2>
          <i class="material-icons">format_list_numbered</i>
          Chi tiết sản phẩm bán chạy
        </h2>
      </div>
      <div class="table-card">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Hạng</th>
              <th>Tên sản phẩm</th>
              <th>Danh mục</th>
              <th>Đã bán</th>
              <th>Doanh thu</th>
              <th>Tăng trưởng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(product, index) in topProductsTable" :key="index">
              <td><span class="rank-badge">{{ index + 1 }}</span></td>
              <td><strong>{{ product.name }}</strong></td>
              <td>{{ product.category }}</td>
              <td>{{ product.sold }}</td>
              <td>{{ formatCurrency(product.revenue) }}</td>
              <td>
                <span :class="['growth-badge', product.growth > 0 ? 'positive' : 'negative']">
                  <i class="material-icons">{{ product.growth > 0 ? 'trending_up' : 'trending_down' }}</i>
                  {{ product.growth > 0 ? '+' : '' }}{{ product.growth }}%
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import LineChart from '@/components/charts/LineChart.vue'
import BarChart from '@/components/charts/BarChart.vue'
import DoughnutChart from '@/components/charts/DoughnutChart.vue'

// Refs
const selectedPeriod = ref('30d')
const revenueChartType = ref('day')

// Mock data - Stats
const totalRevenue = ref(458750000)
const totalOrders = ref(1247)
const newCustomers = ref(156)
const avgOrderValue = ref(367890)

// Dữ liệu biểu đồ doanh thu
const revenueChartData = computed(() => ({
  labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'],
  datasets: [
    {
      label: 'Doanh thu (VNĐ)',
      data: [12000000, 19000000, 15000000, 25000000, 22000000, 30000000, 28000000, 
             18000000, 24000000, 20000000, 32000000, 27000000, 35000000, 31000000],
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
const orderStatusData = {
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
}

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
const topProductsData = {
  labels: ['Nike Air Max', 'Adidas Ultra Boost', 'Vans Old Skool', 'Converse Chuck', 'New Balance 574'],
  datasets: [{
    label: 'Số lượng bán',
    data: [245, 198, 167, 143, 128],
    backgroundColor: '#3b82f6',
    borderRadius: 8
  }]
}

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
const categoryRevenueData = {
  labels: ['Giày thể thao', 'Giày sneaker', 'Giày chạy bộ', 'Giày bóng rổ', 'Phụ kiện'],
  datasets: [{
    label: 'Doanh thu (Triệu VNĐ)',
    data: [180, 145, 98, 76, 45],
    backgroundColor: [
      '#3b82f6',
      '#8b5cf6',
      '#ec4899',
      '#f59e0b',
      '#10b981'
    ],
    borderRadius: 8
  }]
}

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
const newCustomersData = {
  labels: ['Tuần 1', 'Tuần 2', 'Tuần 3', 'Tuần 4'],
  datasets: [{
    label: 'Khách hàng mới',
    data: [32, 45, 38, 41],
    borderColor: '#10b981',
    backgroundColor: 'rgba(16, 185, 129, 0.1)',
    fill: true,
    tension: 0.4
  }]
}

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

// Bảng top sản phẩm
const topProductsTable = ref([
  { name: 'Nike Air Max 270', category: 'Giày thể thao', sold: 245, revenue: 122500000, growth: 18.5 },
  { name: 'Adidas Ultra Boost 21', category: 'Giày chạy bộ', sold: 198, revenue: 118800000, growth: 12.3 },
  { name: 'Vans Old Skool', category: 'Giày sneaker', sold: 167, revenue: 83500000, growth: -3.2 },
  { name: 'Converse Chuck Taylor', category: 'Giày sneaker', sold: 143, revenue: 57200000, growth: 8.7 },
  { name: 'New Balance 574', category: 'Giày thể thao', sold: 128, revenue: 64000000, growth: 15.4 }
])

// Methods
const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const loadAnalytics = () => {
  // console.log('Loading analytics for period:', selectedPeriod.value) // Debug
  // TODO: Call API to load analytics data
}
</script>

<style scoped>
/* Minimal custom CSS - phần lớn sử dụng từ admin-panel.css */

.page-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.page-title .material-icons {
  font-size: 2rem;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stats-change {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.stats-change .material-icons {
  font-size: 1rem;
}

/* Conversion Funnel - Custom styling */
.conversion-funnel {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1rem 0.5rem;
}

.funnel-step {
  width: 100%;
}

.funnel-bar {
  background: var(--gradient-primary);
  padding: 0.875rem 1.25rem;
  border-radius: var(--admin-radius-md);
  color: white;
  font-weight: 500;
  transition: var(--admin-transition);
  font-size: 0.9rem;
}

.funnel-bar:hover {
  transform: translateX(8px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* Rank Badge */
.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: var(--gradient-primary);
  color: white;
  border-radius: var(--radius-full);
  font-weight: 600;
  font-size: 0.875rem;
}

/* Growth Badge */
.growth-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-weight: 600;
  font-size: 0.875rem;
}

.growth-badge .material-icons {
  font-size: 1rem;
}

.growth-badge.positive {
  color: #10b981;
}

.growth-badge.negative {
  color: #ef4444;
}

/* Chart Card Special Height */
.chart-card:has(.conversion-funnel) {
  height: auto;
  min-height: 350px;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .conversion-funnel {
    padding: 0.75rem 0.25rem;
  }

  .funnel-bar {
    padding: 0.75rem 1rem;
    font-size: 0.8rem;
  }

  .rank-badge {
    width: 28px;
    height: 28px;
    font-size: 0.8rem;
  }
}
</style>

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
/* ===== ADMIN ANALYTICS PAGE - UNIFIED DARK THEME ===== */

/* Page Layout */
.admin-analytics {
  padding: var(--space-8);
  max-width: 1800px;
  margin: 0 auto;
  min-height: calc(100vh - 4rem);
}

/* Analytics Header */
.analytics-header {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-6);
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2) 0;
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.page-title .material-icons {
  font-size: 2rem;
  color: var(--accent-primary);
}

.page-subtitle {
  color: var(--text-secondary);
  margin: 0;
  font-size: var(--text-base);
}

.header-right {
  display: flex;
  gap: var(--space-3);
}

/* Period Select */
.period-select {
  padding: var(--space-3) var(--space-5);
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23a78bfa' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right var(--space-3) center;
  background-size: 20px;
  padding-right: var(--space-10);
}

.period-select:hover {
  border-color: var(--accent-primary);
  background-color: rgba(15, 23, 42, 0.8);
}

.period-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

.stats-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-primary);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stats-card.success::before {
  background: var(--gradient-success);
}

.stats-card.warning::before {
  background: var(--gradient-warning);
}

.stats-card.danger::before {
  background: var(--gradient-danger);
}

.stats-card.info::before {
  background: var(--gradient-info);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-glow-purple);
  border-color: var(--border-hover);
}

.stats-card:hover::before {
  opacity: 1;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-3);
}

.stats-info {
  flex: 1;
}

.stats-label {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  font-weight: var(--font-medium);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 var(--space-2) 0;
}

.stats-value {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  line-height: 1;
  margin: 0;
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.stats-icon.success {
  background: var(--gradient-success);
  color: white;
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3);
}

.stats-icon.warning {
  background: var(--gradient-warning);
  color: white;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.3);
}

.stats-icon.danger {
  background: var(--gradient-danger);
  color: white;
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.3);
}

.stats-icon.info {
  background: var(--gradient-info);
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.stats-card:hover .stats-icon {
  transform: scale(1.1) rotate(5deg);
}

.stats-icon .material-icons {
  font-size: 26px;
}

.stats-change {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  margin-top: var(--space-2);
}

.stats-change .material-icons {
  font-size: 1rem;
}

.stats-change.positive {
  color: var(--success-text);
}

.stats-change.negative {
  color: var(--error-text);
}

/* Analytics Section */
.analytics-section {
  margin-bottom: var(--space-8);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.section-header h2 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.section-header h2 .material-icons {
  font-size: 1.75rem;
  color: var(--accent-primary);
}

/* Chart Controls */
.chart-controls {
  display: flex;
  gap: var(--space-2);
  background: rgba(15, 23, 42, 0.6);
  padding: var(--space-1);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-primary);
}

.btn-chart-type {
  padding: var(--space-2) var(--space-4);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-chart-type:hover {
  color: var(--text-primary);
  background: rgba(167, 139, 250, 0.1);
}

.btn-chart-type.active {
  background: var(--gradient-purple);
  color: white;
  box-shadow: 0 2px 8px rgba(167, 139, 250, 0.3);
}

/* Chart Cards */
.chart-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  min-height: 300px;
  height: 350px;
}

.chart-card.large {
  height: 450px;
}

.chart-card:has(.conversion-funnel) {
  height: auto;
  min-height: 350px;
}

/* Charts Grid 2 Column */
.charts-grid-2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}

/* Conversion Funnel */
.conversion-funnel {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  padding: var(--space-4);
}

.funnel-step {
  width: 100%;
}

.funnel-bar {
  background: var(--gradient-primary);
  padding: var(--space-4) var(--space-5);
  border-radius: var(--radius-lg);
  color: white;
  font-weight: var(--font-medium);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: var(--text-sm);
  box-shadow: 0 2px 8px rgba(167, 139, 250, 0.2);
}

.funnel-bar:hover {
  transform: translateX(8px);
  box-shadow: var(--shadow-glow-purple);
}

.funnel-bar span {
  display: block;
  font-weight: var(--font-semibold);
}

/* Table Card */
.table-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* Admin Table */
.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table thead {
  background: var(--table-header-bg);
}

/* Table headers use global admin-tables.css styles */

.admin-table tbody tr {
  border-bottom: 1px solid var(--border-primary);
  transition: all 0.2s ease;
}

.admin-table tbody tr:hover {
  background: var(--gradient-purple-soft);
  transform: scale(1.002);
}

/* Table cells use global admin-tables.css styles */

.admin-table td strong {
  color: var(--text-primary);
  font-weight: var(--font-semibold);
}

/* Rank Badge */
.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: var(--gradient-primary);
  color: white;
  border-radius: var(--radius-full);
  font-weight: var(--font-bold);
  font-size: var(--text-sm);
  box-shadow: 0 2px 8px rgba(167, 139, 250, 0.3);
}

/* Growth Badge */
.growth-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  transition: all 0.2s ease;
}

.growth-badge .material-icons {
  font-size: 1.125rem;
}

.growth-badge.positive {
  color: var(--success-text);
  background: var(--success-bg);
  border: 1px solid var(--success-border);
}

.growth-badge.negative {
  color: var(--error-text);
  background: var(--error-bg);
  border: 1px solid var(--error-border);
}

.growth-badge:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-fade-up {
  animation: fadeUp 0.6s ease-out;
}

.animate-slide-in {
  animation: slideIn 0.6s ease-out;
}

/* Responsive */
@media (max-width: 1400px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1024px) {
  .charts-grid-2 {
    grid-template-columns: 1fr;
  }

  .chart-card {
    height: 320px;
  }

  .chart-card.large {
    height: 400px;
  }
}

@media (max-width: 768px) {
  .admin-analytics {
    padding: var(--space-4);
  }

  .analytics-header {
    flex-direction: column;
    align-items: flex-start;
    padding: var(--space-6);
  }

  .page-title {
    font-size: var(--text-2xl);
  }

  .header-right {
    width: 100%;
  }

  .period-select {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .stats-value {
    font-size: var(--text-2xl);
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-3);
  }

  .section-header h2 {
    font-size: var(--text-xl);
  }

  .chart-controls {
    width: 100%;
    justify-content: space-between;
  }

  .chart-card {
    height: 280px;
  }

  .chart-card.large {
    height: 320px;
  }

  .conversion-funnel {
    padding: var(--space-3);
    gap: var(--space-3);
  }

  .funnel-bar {
    padding: var(--space-3) var(--space-4);
    font-size: var(--text-xs);
  }

  .rank-badge {
    width: 32px;
    height: 32px;
    font-size: var(--text-xs);
  }

  .admin-table {
    font-size: var(--text-xs);
  }

  .admin-table th,
  .admin-table td {
    padding: var(--space-3);
  }
}
</style>

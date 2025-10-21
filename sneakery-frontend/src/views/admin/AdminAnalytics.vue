<template>
  <div class="admin-analytics">
    <!-- Header với bộ lọc thời gian -->
    <div class="analytics-header">
      <div class="header-left">
        <h1 class="page-title">📊 Phân tích & Báo cáo</h1>
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

    <!-- Tổng quan nhanh -->
    <div class="quick-stats">
      <div class="stat-card revenue">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <h3>Tổng doanh thu</h3>
          <p class="stat-value">{{ formatCurrency(totalRevenue) }}</p>
          <span class="stat-change positive">+12.5% so với kỳ trước</span>
        </div>
      </div>
      <div class="stat-card orders">
        <div class="stat-icon">📦</div>
        <div class="stat-content">
          <h3>Tổng đơn hàng</h3>
          <p class="stat-value">{{ totalOrders }}</p>
          <span class="stat-change positive">+8.3% so với kỳ trước</span>
        </div>
      </div>
      <div class="stat-card customers">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <h3>Khách hàng mới</h3>
          <p class="stat-value">{{ newCustomers }}</p>
          <span class="stat-change positive">+15.7% so với kỳ trước</span>
        </div>
      </div>
      <div class="stat-card avg-order">
        <div class="stat-icon">📈</div>
        <div class="stat-content">
          <h3>Đơn hàng TB</h3>
          <p class="stat-value">{{ formatCurrency(avgOrderValue) }}</p>
          <span class="stat-change negative">-2.1% so với kỳ trước</span>
        </div>
      </div>
    </div>

    <!-- Biểu đồ doanh thu chi tiết -->
    <div class="analytics-section">
      <div class="section-header">
        <h2>📈 Doanh thu theo thời gian</h2>
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
    <div class="charts-grid-2">
      <!-- Phân tích đơn hàng theo trạng thái -->
      <div class="analytics-section">
        <div class="section-header">
          <h2>📊 Đơn hàng theo trạng thái</h2>
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
          <h2>🏆 Top sản phẩm bán chạy</h2>
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
    <div class="analytics-section">
      <div class="section-header">
        <h2>🏷️ Doanh thu theo danh mục</h2>
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
    <div class="charts-grid-2">
      <!-- Khách hàng mới theo thời gian -->
      <div class="analytics-section">
        <div class="section-header">
          <h2>👤 Khách hàng mới</h2>
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
          <h2>🎯 Tỷ lệ chuyển đổi</h2>
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
    <div class="analytics-section">
      <div class="section-header">
        <h2>📋 Chi tiết sản phẩm bán chạy</h2>
      </div>
      <div class="table-card">
        <table class="analytics-table">
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
              <td><span class="rank">{{ index + 1 }}</span></td>
              <td><strong>{{ product.name }}</strong></td>
              <td>{{ product.category }}</td>
              <td>{{ product.sold }}</td>
              <td>{{ formatCurrency(product.revenue) }}</td>
              <td>
                <span :class="['growth', product.growth > 0 ? 'positive' : 'negative']">
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
.admin-analytics {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

/* Header */
.analytics-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
  gap: 2rem;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem;
}

.page-subtitle {
  color: #64748b;
  font-size: 1rem;
  margin: 0;
}

.header-right {
  display: flex;
  gap: 1rem;
}

.period-select {
  padding: 0.625rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  font-size: 0.875rem;
  color: #1e293b;
  cursor: pointer;
  transition: all 0.2s;
}

.period-select:hover {
  border-color: #3b82f6;
}

.period-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* Quick Stats */
.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 2.5rem;
  line-height: 1;
}

.stat-content {
  flex: 1;
}

.stat-content h3 {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0 0 0.5rem;
  font-weight: 500;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem;
}

.stat-change {
  font-size: 0.875rem;
  font-weight: 500;
}

.stat-change.positive {
  color: #10b981;
}

.stat-change.negative {
  color: #ef4444;
}

/* Analytics Section */
.analytics-section {
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.section-header h2 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.chart-controls {
  display: flex;
  gap: 0.5rem;
  background: #f1f5f9;
  padding: 0.25rem;
  border-radius: 8px;
}

.btn-chart-type {
  padding: 0.5rem 1rem;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-size: 0.875rem;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.btn-chart-type:hover {
  color: #1e293b;
}

.btn-chart-type.active {
  background: white;
  color: #3b82f6;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* Chart Cards */
.chart-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  height: 350px;
}

.chart-card.large {
  height: 400px;
}

/* Chart card với funnel cần height auto */
.chart-card:has(.conversion-funnel) {
  height: auto;
  min-height: 350px;
}

/* Charts Grid */
.charts-grid-2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
  margin-bottom: 2rem;
}

/* Conversion Funnel */
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
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  padding: 0.875rem 1.25rem;
  border-radius: 8px;
  color: white;
  font-weight: 500;
  transition: all 0.3s;
  font-size: 0.9rem;
}

.funnel-bar:hover {
  transform: translateX(8px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* Table */
.table-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
  overflow-y: visible;
}

.analytics-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 600px;
}

.analytics-table thead {
  background: #f8fafc;
}

.analytics-table th {
  padding: 1rem 1.5rem;
  text-align: left;
  font-weight: 600;
  color: #475569;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  white-space: nowrap;
}

.analytics-table td {
  padding: 1rem 1.5rem;
  border-top: 1px solid #f1f5f9;
  color: #1e293b;
  vertical-align: middle;
}

.analytics-table tbody tr {
  transition: background 0.2s;
}

.analytics-table tbody tr:hover {
  background: #f8fafc;
}

.rank {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  color: white;
  border-radius: 50%;
  font-weight: 600;
  font-size: 0.875rem;
}

.growth {
  font-weight: 600;
  font-size: 0.875rem;
}

.growth.positive {
  color: #10b981;
}

.growth.negative {
  color: #ef4444;
}

/* Responsive */
@media (max-width: 1024px) {
  .charts-grid-2 {
    grid-template-columns: 1fr;
  }

  .chart-card {
    height: 300px;
  }

  .chart-card.large {
    height: 350px;
  }
}

@media (max-width: 768px) {
  .admin-analytics {
    padding: 1rem;
  }

  .analytics-header {
    flex-direction: column;
    gap: 1rem;
  }

  .header-right {
    width: 100%;
  }

  .period-select {
    width: 100%;
  }

  .quick-stats {
    grid-template-columns: 1fr;
  }

  .chart-card {
    height: 280px;
  }

  .chart-card.large {
    height: 320px;
  }

  .conversion-funnel {
    padding: 0.75rem 0.25rem;
  }

  .funnel-bar {
    padding: 0.75rem 1rem;
    font-size: 0.8rem;
  }

  .analytics-table {
    font-size: 0.8rem;
    min-width: 500px;
  }

  .analytics-table th,
  .analytics-table td {
    padding: 0.75rem 0.75rem;
  }

  .rank {
    width: 28px;
    height: 28px;
    font-size: 0.8rem;
  }
}
</style>

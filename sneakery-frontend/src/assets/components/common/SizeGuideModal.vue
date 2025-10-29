<template>
  <Transition name="modal">
    <div v-if="isOpen" class="size-guide-modal" @click.self="close">
      <div class="size-guide-content">
        <div class="modal-header">
          <h2 class="modal-title">Hướng dẫn chọn size</h2>
          <button class="close-btn" @click="close">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <!-- Brand Selection -->
          <div class="filter-section">
            <label class="filter-label">Chọn thương hiệu:</label>
            <select v-model="selectedBrandId" @change="loadSizeCharts" class="form-control">
              <option value="">Tất cả thương hiệu</option>
              <option v-for="brand in brands" :key="brand.id" :value="brand.id">
                {{ brand.name }}
              </option>
            </select>
          </div>

          <!-- Category Selection -->
          <div class="filter-section">
            <label class="filter-label">Chọn loại giày:</label>
            <select v-model="selectedCategory" @change="loadSizeCharts" class="form-control">
              <option value="">Tất cả loại</option>
              <option v-for="category in categories" :key="category" :value="category">
                {{ category }}
              </option>
            </select>
          </div>

          <!-- Loading State -->
          <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
          </div>

          <!-- Size Chart Table -->
          <div v-else-if="sizeCharts.length > 0" class="size-chart-wrapper">
            <table class="size-chart-table">
              <thead>
                <tr>
                  <th>EU Size</th>
                  <th v-if="hasUsSize">US Size</th>
                  <th v-if="hasUkSize">UK Size</th>
                  <th v-if="hasLength">Độ dài (cm)</th>
                  <th v-if="hasWidth">Độ rộng (cm)</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="chart in sizeCharts" :key="chart.id">
                  <td class="size-value">{{ chart.size }}</td>
                  <td v-if="hasUsSize" class="size-value">{{ chart.sizeUs || '-' }}</td>
                  <td v-if="hasUkSize" class="size-value">{{ chart.sizeUk || '-' }}</td>
                  <td v-if="hasLength" class="size-value">{{ chart.lengthCm || '-' }}</td>
                  <td v-if="hasWidth" class="size-value">{{ chart.widthCm || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Empty State -->
          <div v-else class="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 20C16.4183 20 20 16.4183 20 12C20 7.58172 16.4183 4 12 4C7.58172 4 4 7.58172 4 12C4 16.4183 7.58172 20 12 20Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 8V12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 16H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h3>Chưa có bảng size</h3>
            <p>Vui lòng chọn thương hiệu và loại giày</p>
          </div>

          <!-- Tips Section -->
          <div class="tips-section">
            <h3 class="tips-title">💡 Mẹo chọn size:</h3>
            <ul class="tips-list">
              <li>Đo chân khi đã mang tất thông thường</li>
              <li>Nên đo vào cuối ngày khi chân có thể sưng nhẹ</li>
              <li>Để lại khoảng trống khoảng 0.5-1cm ở đầu ngón chân</li>
              <li>Tham khảo size chart của từng thương hiệu vì có thể khác nhau</li>
              <li>Nếu giữa 2 size, nên chọn size lớn hơn</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const props = defineProps({
  isOpen: Boolean,
  brandId: {
    type: Number,
    default: null
  },
  category: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:isOpen', 'close']);

const loading = ref(false);
const sizeCharts = ref([]);
const brands = ref([]);
const selectedBrandId = ref(props.brandId || null);
const selectedCategory = ref(props.category || '');

const categories = computed(() => {
  const cats = new Set();
  sizeCharts.value.forEach(chart => {
    if (chart.category) cats.add(chart.category);
  });
  return Array.from(cats).sort();
});

const hasUsSize = computed(() => {
  return sizeCharts.value.some(chart => chart.sizeUs);
});

const hasUkSize = computed(() => {
  return sizeCharts.value.some(chart => chart.sizeUk);
});

const hasLength = computed(() => {
  return sizeCharts.value.some(chart => chart.lengthCm);
});

const hasWidth = computed(() => {
  return sizeCharts.value.some(chart => chart.widthCm);
});

watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    selectedBrandId.value = props.brandId || null;
    selectedCategory.value = props.category || '';
    loadBrands();
    loadSizeCharts();
  }
});

watch(() => props.brandId, (newBrandId) => {
  if (newBrandId) {
    selectedBrandId.value = newBrandId;
    loadSizeCharts();
  }
});

const loadBrands = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/admin/brands');
    brands.value = response.data;
  } catch (error) {
    console.error('Error loading brands:', error);
  }
};

const loadSizeCharts = async () => {
  loading.value = true;
  try {
    const params = {};
    if (selectedBrandId.value) params.brandId = selectedBrandId.value;
    if (selectedCategory.value) params.category = selectedCategory.value;

    const response = await axios.get('http://localhost:8080/api/size-charts', { params });
    sizeCharts.value = response.data || [];
  } catch (error) {
    console.error('Error loading size charts:', error);
    ElMessage.error('Không thể tải bảng size');
  } finally {
    loading.value = false;
  }
};

const close = () => {
  emit('update:isOpen', false);
  emit('close');
};
</script>

<style scoped>
.size-guide-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: var(--space-4);
}

.size-guide-content {
  background: var(--bg-primary);
  border-radius: var(--radius-2xl);
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-6);
  border-bottom: 1px solid var(--border-light);
}

.modal-title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.close-btn {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: var(--radius-full);
  color: var(--text-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
}

.close-btn:hover {
  background: var(--error-color);
  color: white;
  transform: scale(1.1);
}

.modal-body {
  padding: var(--space-6);
}

.filter-section {
  margin-bottom: var(--space-6);
}

.filter-label {
  display: block;
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.form-control {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  color: var(--text-primary);
  font-size: var(--text-base);
}

.form-control:focus {
  outline: none;
  border-color: var(--primary-color);
}

.loading-state {
  text-align: center;
  padding: var(--space-8);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(167, 139, 250, 0.2);
  border-top-color: #a78bfa;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.size-chart-wrapper {
  margin: var(--space-6) 0;
  overflow-x: auto;
}

.size-chart-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.size-chart-table thead {
  background: var(--primary-gradient);
  color: white;
}

.size-chart-table th {
  padding: var(--space-4);
  text-align: left;
  font-weight: var(--font-semibold);
  font-size: var(--text-sm);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.size-chart-table tbody tr {
  border-bottom: 1px solid var(--border-light);
}

.size-chart-table tbody tr:last-child {
  border-bottom: none;
}

.size-chart-table tbody tr:hover {
  background: rgba(167, 139, 250, 0.1);
}

.size-value {
  padding: var(--space-3) var(--space-4);
  color: var(--text-primary);
  text-align: center;
}

.empty-state {
  text-align: center;
  padding: var(--space-12);
}

.empty-state svg {
  color: var(--text-muted);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  color: var(--text-primary);
  margin-bottom: var(--space-2);
}

.empty-state p {
  color: var(--text-secondary);
}

.tips-section {
  margin-top: var(--space-8);
  padding: var(--space-6);
  background: rgba(167, 139, 250, 0.1);
  border-radius: var(--radius-xl);
  border: 1px solid rgba(167, 139, 250, 0.2);
}

.tips-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  padding: var(--space-2) 0;
  padding-left: var(--space-6);
  position: relative;
  color: var(--text-secondary);
}

.tips-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: var(--primary-color);
  font-weight: bold;
}

@media (max-width: 768px) {
  .size-guide-content {
    max-height: 95vh;
  }

  .modal-header {
    padding: var(--space-4);
  }

  .modal-title {
    font-size: var(--text-xl);
  }

  .modal-body {
    padding: var(--space-4);
  }
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>


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
import notificationService from '@/utils/notificationService';
import { API_ENDPOINTS } from '@/config/api';
import logger from '@/utils/logger';
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
    const response = await axios.get(API_ENDPOINTS.ADMIN_PRODUCTS.BRANDS);
    brands.value = response.data;
  } catch (error) {
    logger.error('Error loading brands:', error);
  }
};

const loadSizeCharts = async () => {
  loading.value = true;
  try {
    const params = {};
    if (selectedBrandId.value) params.brandId = selectedBrandId.value;
    if (selectedCategory.value) params.category = selectedCategory.value;

    const response = await axios.get(API_ENDPOINTS.SIZE_CHARTS.BASE, { params });
    sizeCharts.value = response.data || [];
  } catch (error) {
    logger.error('Error loading size charts:', error);
    notificationService.error('Lỗi','Không thể tải bảng size');
  } finally {
    loading.value = false;
  }
};

const close = () => {
  emit('update:isOpen', false);
  emit('close');
};
</script>





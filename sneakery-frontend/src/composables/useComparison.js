import { ref, computed, watch } from 'vue';
import notificationService from '@/utils/notificationService';

const MAX_COMPARE_PRODUCTS = 3;
const STORAGE_KEY = 'sneakery_compare_products';

// Global state
const comparisonProducts = ref([]);

// Load from localStorage
const loadFromStorage = () => {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      comparisonProducts.value = JSON.parse(stored);
    }
  } catch (error) {
    console.error('Error loading comparison from storage:', error);
    comparisonProducts.value = [];
  }
};

// Save to localStorage
const saveToStorage = () => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(comparisonProducts.value));
  } catch (error) {
    console.error('Error saving comparison to storage:', error);
  }
};

// Load on init
loadFromStorage();

export function useComparison() {
  const addProduct = (product) => {
    // Check if already exists
    if (comparisonProducts.value.some(p => p.id === product.id)) {
      notificationService.warning('Cảnh báo', 'Sản phẩm đã có trong danh sách so sánh');
      return;
    }

    // Check max limit
    if (comparisonProducts.value.length >= MAX_COMPARE_PRODUCTS) {
      notificationService.warning('Cảnh báo', `Chỉ có thể so sánh tối đa ${MAX_COMPARE_PRODUCTS} sản phẩm`);
      return;
    }

    // Add product
    comparisonProducts.value.push(product);
    saveToStorage();
    notificationService.success('Thành công', 'Đã thêm vào danh sách so sánh');
  };

  const removeProduct = (productId) => {
    const index = comparisonProducts.value.findIndex(p => p.id === productId);
    if (index > -1) {
      comparisonProducts.value.splice(index, 1);
      saveToStorage();
      notificationService.success('Thành công', 'Đã xóa khỏi danh sách so sánh');
    }
  };

  const clearAll = () => {
    comparisonProducts.value = [];
    saveToStorage();
    notificationService.success('Thành công', 'Đã xóa tất cả sản phẩm khỏi danh sách so sánh');
  };

  const getProducts = () => {
    return comparisonProducts.value;
  };

  const isInComparison = (productId) => {
    return comparisonProducts.value.some(p => p.id === productId);
  };

  const canAddMore = computed(() => {
    return comparisonProducts.value.length < MAX_COMPARE_PRODUCTS;
  });

  return {
    comparisonProducts: computed(() => comparisonProducts.value),
    addProduct,
    removeProduct,
    clearAll,
    getProducts,
    isInComparison,
    canAddMore,
    count: computed(() => comparisonProducts.value.length),
    maxCount: MAX_COMPARE_PRODUCTS
  };
}


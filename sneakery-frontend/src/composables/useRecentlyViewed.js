import { ref, computed } from 'vue';

const STORAGE_KEY = 'sneakery_recently_viewed';
const MAX_ITEMS = 10;

const recentlyViewed = ref([]);

export function useRecentlyViewed() {
  // Load từ localStorage
  const loadFromStorage = () => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored) {
        recentlyViewed.value = JSON.parse(stored);
      }
    } catch (error) {
      console.error('Error loading recently viewed products:', error);
      recentlyViewed.value = [];
    }
  };

  // Lưu vào localStorage
  const saveToStorage = () => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(recentlyViewed.value));
    } catch (error) {
      console.error('Error saving recently viewed products:', error);
    }
  };

  // Thêm sản phẩm mới vào danh sách
  const addProduct = (product) => {
    if (!product || !product.id) return;

    // Loại bỏ sản phẩm trùng (nếu đã tồn tại)
    const filtered = recentlyViewed.value.filter(p => p.id !== product.id);
    
    // Thêm sản phẩm mới vào đầu danh sách
    recentlyViewed.value = [
      {
        id: product.id,
        name: product.name,
        slug: product.slug,
        brandName: product.brandName,
        imageUrl: product.imageUrl,
        price: product.price,
        viewedAt: new Date().toISOString()
      },
      ...filtered
    ].slice(0, MAX_ITEMS); // Giới hạn số lượng

    saveToStorage();
  };

  // Xóa một sản phẩm khỏi danh sách
  const removeProduct = (productId) => {
    recentlyViewed.value = recentlyViewed.value.filter(p => p.id !== productId);
    saveToStorage();
  };

  // Xóa tất cả
  const clearAll = () => {
    recentlyViewed.value = [];
    saveToStorage();
  };

  // Lấy danh sách
  const getProducts = () => {
    return recentlyViewed.value;
  };

  // Kiểm tra sản phẩm đã xem chưa
  const isViewed = (productId) => {
    return recentlyViewed.value.some(p => p.id === productId);
  };

  // Lấy số lượng sản phẩm đã xem
  const count = computed(() => recentlyViewed.value.length);

  // Khởi tạo - load từ storage
  if (recentlyViewed.value.length === 0) {
    loadFromStorage();
  }

  return {
    recentlyViewed: computed(() => recentlyViewed.value),
    count,
    addProduct,
    removeProduct,
    clearAll,
    getProducts,
    isViewed,
    loadFromStorage
  };
}


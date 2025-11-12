import { ref, computed } from 'vue';
import logger from '@/utils/logger';

const STORAGE_KEY = 'sneakery_recently_viewed';
const MAX_ITEMS = 10;

const recentlyViewed = ref([]);

// Helper function to get product price
const getProductPrice = (product) => {
  // Try to get price from variant first
  if (product.variants && product.variants.length > 0) {
    const firstVariant = product.variants[0];
    // Prefer priceSale, fallback to priceBase
    if (firstVariant.priceSale !== null && firstVariant.priceSale !== undefined && !isNaN(firstVariant.priceSale)) {
      return Number(firstVariant.priceSale);
    }
    if (firstVariant.priceBase !== null && firstVariant.priceBase !== undefined && !isNaN(firstVariant.priceBase)) {
      return Number(firstVariant.priceBase);
    }
  }
  
  // Try product level priceSale/priceBase
  if (product.priceSale !== null && product.priceSale !== undefined && !isNaN(product.priceSale)) {
    return Number(product.priceSale);
  }
  if (product.priceBase !== null && product.priceBase !== undefined && !isNaN(product.priceBase)) {
    return Number(product.priceBase);
  }
  
  // Fallback to price if provided
  if (product.price !== null && product.price !== undefined && !isNaN(product.price)) {
    return Number(product.price);
  }
  
  return 0;
};

// Helper function to get product image URL
const getProductImageUrl = (product) => {
  // Try variant image first
  if (product.variants && product.variants.length > 0 && product.variants[0].imageUrl) {
    return product.variants[0].imageUrl;
  }
  
  // Try product images array
  if (product.images && product.images.length > 0) {
    const firstImage = product.images[0];
    // Handle both object format {url: ...} and string format
    return typeof firstImage === 'string' ? firstImage : (firstImage.url || firstImage.imageUrl);
  }
  
  // Try product imageUrl
  if (product.imageUrl) {
    return product.imageUrl;
  }
  
  // Try product mainImage
  if (product.mainImage) {
    return product.mainImage;
  }
  
  return null;
};

export function useRecentlyViewed() {
  // Load từ localStorage
  const loadFromStorage = () => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored) {
        const parsed = JSON.parse(stored);
        // Validate and clean up invalid entries
        recentlyViewed.value = parsed.filter(p => p && p.id && p.name);
      }
    } catch (error) {
      logger.error('Error loading recently viewed products:', error);
      recentlyViewed.value = [];
    }
  };

  // Lưu vào localStorage
  const saveToStorage = () => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(recentlyViewed.value));
    } catch (error) {
      logger.error('Error saving recently viewed products:', error);
    }
  };

  // Thêm sản phẩm mới vào danh sách
  const addProduct = (product) => {
    if (!product || !product.id) {
      logger.warn('Cannot add product to recently viewed: invalid product', product);
      return;
    }

    // Calculate price and imageUrl
    const price = product.price !== undefined ? product.price : getProductPrice(product);
    const imageUrl = product.imageUrl || getProductImageUrl(product);

    // Loại bỏ sản phẩm trùng (nếu đã tồn tại)
    const filtered = recentlyViewed.value.filter(p => p.id !== product.id);
    
    // Thêm sản phẩm mới vào đầu danh sách
    recentlyViewed.value = [
      {
        id: product.id,
        name: product.name || 'Unknown Product',
        slug: product.slug || `product-${product.id}`,
        brandName: product.brandName || product.brand?.name || 'Unknown Brand',
        imageUrl: imageUrl || '/placeholder-image.png',
        price: price || 0,
        viewedAt: new Date().toISOString()
      },
      ...filtered
    ].slice(0, MAX_ITEMS); // Giới hạn số lượng

    saveToStorage();
    logger.log('Added product to recently viewed:', product.id, product.name);
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


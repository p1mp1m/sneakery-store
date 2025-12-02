<template>
  <router-link :to="`/home/products/${product.slug}`" class="product-card-link">
    <div class="product-card group">
      <div
        class="product-image-container relative overflow-hidden"
        @mouseenter="isHover = true"
        @mouseleave="isHover = false"
      >
        <!-- Badges -->
        <div class="product-badges">
          <span v-if="product.isNew" class="badge badge-new">Mới</span>
          <span v-if="product.isFeatured" class="badge badge-featured"
            >Nổi bật</span
          >
          <span v-if="!product.inStock" class="badge badge-out-of-stock"
            >Hết hàng</span
          >
        </div>

        <!-- Stock Badge -->
        <div
          v-if="product.totalStock !== null && product.totalStock !== undefined"
          class="stock-badge"
        >
          <span
            class="stock-badge-text"
            :class="{
              'stock-out': product.totalStock === 0 || !product.inStock,
              'stock-ok': product.totalStock > 0 && product.inStock,
            }"
          >
            {{
              product.totalStock > 0 && product.inStock
                ? "Còn hàng"
                : "Hết hàng"
            }}
          </span>
        </div>

        <!-- Flash Sale Badge -->
        <FlashSaleBadge
          v-if="productFlashSale"
          :flashSale="productFlashSale"
          class="compact"
        />

        <!-- Ảnh chính -->
        <img
          :src="mainImage"
          class="product-image product-main transition-opacity duration-300"
          :class="{ 'opacity-0': isHover }"
          :alt="product.name"
        />

        <!-- Ảnh hover -->
        <img
          v-if="hoverImage"
          :src="hoverImage"
          class="product-image product-hover absolute inset-0 transition-opacity duration-300"
          :class="{ 'opacity-100': isHover, 'opacity-0': !isHover }"
          :alt="product.name"
        />

        <!-- Overlay -->
        <div class="product-overlay">
          <button
            class="btn-icon btn-quick-view"
            @click.prevent="openQuickView"
            title="Xem nhanh"
          >
            <i class="material-icons">visibility</i>
          </button>
          <button
            class="btn-icon btn-favorite"
            @click.prevent="handleToggleFavorite"
            title="Yêu thích"
            :class="{ active: isInWishlist }"
            :disabled="isTogglingFavorite"
          >
            <i class="material-icons" v-if="!isTogglingFavorite">
              {{ isInWishlist ? "favorite" : "favorite_border" }}
            </i>
            <div
              v-else
              class="w-5 h-5 border-2 border-current border-t-transparent rounded-full animate-spin"
            ></div>
          </button>
        </div>
      </div>

      <div class="product-info">
        <span class="brand-name">{{ getBrandName() }}</span>
        <h3 class="product-name">{{ product.name }}</h3>

        <!-- Rating -->
        <div v-if="product.avgRating" class="product-rating">
          <div class="stars">
            <i
              v-for="i in 5"
              :key="i"
              class="material-icons star"
              :class="{ filled: i <= Math.round(product.avgRating) }"
            >
              {{ i <= Math.round(product.avgRating) ? "star" : "star_border" }}
            </i>
          </div>
          <span class="rating-text">
            {{ product.avgRating.toFixed(1) }}
            <span v-if="product.reviewCount" class="review-count"
              >({{ product.reviewCount }})</span
            >
          </span>
        </div>

        <div class="product-footer">
          <div class="price-wrapper">
            <span
              v-if="
                productFlashSale &&
                originalPrice > 0 &&
                finalPrice < originalPrice
              "
              class="price-original"
            >
              {{ formatCurrency(originalPrice) }}
            </span>
            <span class="price">{{ formatCurrency(finalPrice) }}</span>
          </div>
          <button
            class="btn-add-cart"
            @click.prevent="handleQuickAddToCart"
            :disabled="!canAddToCart || isAddingToCart"
            :class="{
              'opacity-50 cursor-not-allowed': !canAddToCart || isAddingToCart,
            }"
            title="Thêm nhanh vào giỏ hàng"
          >
            <i class="material-icons" v-if="!isAddingToCart"
              >add_shopping_cart</i
            >
            <div
              v-else
              class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"
            ></div>
          </button>
        </div>
      </div>
    </div>
  </router-link>

  <!-- Quick View Modal -->
  <QuickViewModal
    v-model:isOpen="showQuickView"
    :productId="product.id"
    @added-to-cart="handleQuickViewAddedToCart"
  />
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useCartStore } from "@/stores/cart";
import { useWishlistStore } from "@/stores/wishlist";
import { useFlashSaleStore } from "@/stores/flashSale";
// import notificationService from "@/utils/notificationService";
import notificationService from "@/utils/notificationService";
import logger from "@/utils/logger";
import FlashSaleBadge from "@/assets/components/common/FlashSaleBadge.vue";
import QuickViewModal from "@/assets/components/common/QuickViewModal.vue";
import { useProductImageStore } from "@/stores/productImages";

// Props
const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
});

// Router & Stores
const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();
const wishlistStore = useWishlistStore();
const flashSaleStore = useFlashSaleStore();
const productImageStore = useProductImageStore();
const isHover = ref(false);

// Reactive state
const isAddingToCart = ref(false);
const isTogglingFavorite = ref(false);
const showQuickView = ref(false);

// Fetch flash sales on mount if not already loaded
onMounted(async () => {
  if (flashSaleStore.activeFlashSales.length === 0) {
    try {
      await flashSaleStore.fetchActiveFlashSales();
    } catch (error) {
      logger.warn("Failed to fetch flash sales in ProductCard:", error);
    }
  }
});

// Computed - Check if product is in wishlist
const isInWishlist = computed(() => {
  return wishlistStore.isInWishlist(props.product.id);
});

// Computed
const productFlashSale = computed(() => {
  return flashSaleStore.getFlashSaleForProduct(props.product.id);
});

// Computed - Optimized image URL với Cloudinary
const optimizedImageUrl = computed(() => {
  // Ưu tiên mainImageUrl, sau đó imageUrl, cuối cùng placeholder
  const imageUrl =
    props.product.mainImageUrl ||
    props.product.imageUrl ||
    "/placeholder-image.png";

  // Nếu là Cloudinary URL, optimize cho thumbnail size (400x400)
  if (imageUrl && imageUrl.startsWith("http")) {
    return getOptimizedImageUrl(imageUrl, {
      width: 400,
      height: 400,
      quality: "auto",
      format: "auto",
    });
  }

  return imageUrl;
});

// Helper function to get product price from variants or direct price
const getProductPrice = () => {
  // If product has price directly
  if (
    props.product.price !== null &&
    props.product.price !== undefined &&
    !isNaN(props.product.price)
  ) {
    return Number(props.product.price);
  }

  // If has variants, get price from first available variant
  if (props.product.variants && props.product.variants.length > 0) {
    const firstVariant =
      firstAvailableVariant.value || props.product.variants[0];
    if (firstVariant) {
      // Prefer priceSale, fallback to priceBase
      if (
        firstVariant.priceSale !== null &&
        firstVariant.priceSale !== undefined &&
        !isNaN(firstVariant.priceSale)
      ) {
        return Number(firstVariant.priceSale);
      }
      if (
        firstVariant.priceBase !== null &&
        firstVariant.priceBase !== undefined &&
        !isNaN(firstVariant.priceBase)
      ) {
        return Number(firstVariant.priceBase);
      }
    }
  }

  // If has priceBase or priceSale directly
  if (
    props.product.priceSale !== null &&
    props.product.priceSale !== undefined &&
    !isNaN(props.product.priceSale)
  ) {
    return Number(props.product.priceSale);
  }
  if (
    props.product.priceBase !== null &&
    props.product.priceBase !== undefined &&
    !isNaN(props.product.priceBase)
  ) {
    return Number(props.product.priceBase);
  }

  return 0;
};

const finalPrice = computed(() => {
  const basePrice = getProductPrice();
  if (productFlashSale.value && basePrice > 0) {
    return flashSaleStore.calculateDiscountedPrice(
      basePrice,
      productFlashSale.value.discountPercent
    );
  }
  return basePrice;
});

// Get original price for display
const originalPrice = computed(() => {
  return getProductPrice();
});

// Lấy variant đầu tiên có stock > 0
const firstAvailableVariant = computed(() => {
  if (!props.product.variants || props.product.variants.length === 0) {
    return null;
  }

  // Tìm variant đầu tiên có stock > 0
  const availableVariant = props.product.variants.find(
    (v) => v.stockQuantity > 0 && v.isActive !== false
  );

  // Nếu không tìm thấy, lấy variant đầu tiên
  return availableVariant || props.product.variants[0];
});

// Kiểm tra sản phẩm có thể thêm vào giỏ hàng
const canAddToCart = computed(() => {
  // Kiểm tra stock
  if (
    props.product.totalStock !== null &&
    props.product.totalStock !== undefined
  ) {
    if (props.product.totalStock <= 0) return false;
  }

  // Kiểm tra variants
  if (props.product.variants && props.product.variants.length > 0) {
    return firstAvailableVariant.value !== null;
  }

  // Nếu không có variants, kiểm tra inStock
  return props.product.inStock !== false;
});

// Methods
const getBrandName = () => {
  // Try multiple sources for brand name
  if (props.product.brand?.name) {
    return props.product.brand.name;
  }
  if (props.product.brandName) {
    return props.product.brandName;
  }
  // Try to get from flash sale if available
  if (productFlashSale.value?.brandName) {
    return productFlashSale.value.brandName;
  }
  return "Unknown";
};

const formatCurrency = (value) => {
  if (value === null || value === undefined) return "0 ₫";
  const numValue = typeof value === "string" ? parseFloat(value) : value;
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(numValue);
};

const handleToggleFavorite = async (event) => {
  event.preventDefault();
  event.stopPropagation();

  if (!authStore.isAuthenticated) {
    notificationService.warning(
      "Cảnh báo",
      "Vui lòng đăng nhập để thêm vào yêu thích"
    );
    router.push({
      path: "/login",
      query: { redirect: router.currentRoute.value.fullPath },
    });
    return;
  }

  if (isTogglingFavorite.value) return;

  isTogglingFavorite.value = true;

  try {
    const result = await wishlistStore.toggleWishlist(props.product.id);

    if (result.action === "added") {
      notificationService.success("Thành công", "Đã thêm vào yêu thích");
      logger.log("Product added to wishlist:", props.product.id);
    } else {
      notificationService.success("Thành công", "Đã xóa khỏi yêu thích");
      logger.log("Product removed from wishlist:", props.product.id);
    }
  } catch (error) {
    logger.error("Error toggling favorite:", error);

    // Xử lý lỗi cụ thể
    if (error.response?.status === 401) {
      notificationService.warning(
        "Cảnh báo",
        "Vui lòng đăng nhập để thêm vào yêu thích"
      );
      router.push({
        path: "/login",
        query: { redirect: router.currentRoute.value.fullPath },
      });
    } else {
      notificationService.error(
        "Lỗi",
        error.response?.data?.message || "Không thể cập nhật yêu thích"
      );
    }
  } finally {
    isTogglingFavorite.value = false;
  }
};

// Quick View
const openQuickView = (event) => {
  event.preventDefault();
  event.stopPropagation();
  showQuickView.value = true;
};

const handleQuickViewAddedToCart = () => {
  // Refresh cart count after adding from quick view
  cartStore.fetchCart().catch(() => {
    // Silently fail
  });
};

// Quick Add to Cart - Thêm nhanh vào giỏ hàng (nút lớn màu xanh ở footer)
const handleQuickAddToCart = async (event) => {
  event.preventDefault();
  event.stopPropagation();

  // Kiểm tra sản phẩm có thể thêm vào giỏ hàng
  if (!canAddToCart.value) {
    notificationService.error("Lỗi", "Sản phẩm này hiện không có sẵn");
    return;
  }

  // Nếu sản phẩm có nhiều variants, redirect đến trang chi tiết để chọn
  if (props.product.variants && props.product.variants.length > 1) {
    try {
      await router.push(`/home/products/${props.product.slug}`);
    } catch (navError) {
      logger.error("Navigation error:", navError);
      notificationService.error("Lỗi", "Không thể mở trang chi tiết sản phẩm");
    }
    return;
  }

  // Lấy variant ID
  let variantId = null;
  if (props.product.variants && props.product.variants.length > 0) {
    variantId = firstAvailableVariant.value?.id;
    if (!variantId) {
      notificationService.error("Lỗi", "Không tìm thấy biến thể sản phẩm");
      return;
    }
  } else if (props.product.variantId) {
    variantId = props.product.variantId;
  } else {
    // Nếu không có variant, redirect đến trang chi tiết
    try {
      await router.push(`/home/products/${props.product.slug}`);
    } catch (navError) {
      logger.error("Navigation error:", navError);
      notificationService.error("Lỗi", "Không thể mở trang chi tiết sản phẩm");
    }
    return;
  }

  if (isAddingToCart.value) return;

  isAddingToCart.value = true;

  try {
    // Sử dụng cart store để thêm vào giỏ hàng
    await cartStore.addItem(variantId, 1);
    notificationService.success("Thành công", "Đã thêm sản phẩm vào giỏ hàng");
    logger.log("Product quick added to cart:", variantId);
  } catch (error) {
    logger.error("Error quick adding to cart:", error);

    // Xử lý lỗi cụ thể
    if (error.response?.status === 401) {
      notificationService.warning(
        "Cảnh báo",
        "Vui lòng đăng nhập để thêm vào giỏ hàng"
      );
      router.push({
        path: "/login",
        query: { redirect: router.currentRoute.value.fullPath },
      });
    } else if (error.response?.status === 400) {
      notificationService.error(
        "Lỗi",
        error.response?.data?.message ||
          "Sản phẩm không có sẵn hoặc đã hết hàng"
      );
    } else {
      notificationService.error(
        "Lỗi",
        error.response?.data?.message || "Không thể thêm vào giỏ hàng"
      );
    }
  } finally {
    isAddingToCart.value = false;
  }
};

const productImages = computed(() => {
  return productImageStore.imagesByProduct[props.product.id] || [];
});

const mainImage = computed(() => {
  return (
    productImages.value[0] ||
    props.product.mainImageUrl ||
    props.product.imageUrl ||
    "/placeholder-image.png"
  );
});

const hoverImage = computed(() => {
  return productImages.value.length > 1 ? productImages.value[1] : null;
});

// Load wishlist status on mount
onMounted(async () => {
  if (authStore.isAuthenticated) {
    try {
      await wishlistStore.fetchWishlist();
    } catch (error) {
      // Silently fail - wishlist will be loaded when needed
      logger.warn("Failed to fetch wishlist on mount:", error);
    }
  }
});

// Watch for product changes
watch(
  () => props.product.id,
  () => {
    // Product changed, wishlist status will update automatically via computed
  }
);
</script>

<style scoped>
.product-card-link {
  display: block;
  text-decoration: none;
  color: inherit;
  height: 100%;
}

.product-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 1rem;
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
}

.dark .product-card {
  background: #1f2937;
}

.product-image-container {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f3f4f6;
}

.dark .product-image-container {
  background: #374151;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.group:hover .product-image {
  transform: scale(1.1);
}

.product-badges {
  position: absolute;
  top: 0.75rem;
  left: 0.75rem;
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.025em;
}

.badge-new {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.badge-featured {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.badge-out-of-stock {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
}

.stock-badge {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 10;
}

.stock-badge-text {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.375rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.75rem;
  font-weight: 700;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(4px);
  white-space: nowrap;
  letter-spacing: 0.025em;
}

.stock-badge-text.stock-ok {
  background: linear-gradient(135deg, #10b981, #059669);
}

.stock-badge-text.stock-out {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.product-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 20;
}

.group:hover .product-overlay {
  opacity: 1;
}

.btn-icon {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  background: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #374151;
}

.btn-icon:hover {
  background: #9333ea;
  color: white;
  transform: scale(1.1);
}

.btn-icon.active {
  background: #ef4444;
  color: white;
}

.btn-icon:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}

.btn-icon:disabled:hover {
  transform: none;
  background: white;
  color: #374151;
}

.btn-icon i {
  font-size: 1.25rem;
}

.product-info {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  flex: 1;
}

.brand-name {
  font-size: 0.75rem;
  font-weight: 600;
  color: #9333ea;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.dark .brand-name {
  color: #a78bfa;
}

.product-name {
  font-size: 0.9375rem;
  font-weight: 600;
  color: #111827;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.8rem;
}

.dark .product-name {
  color: #f9fafb;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stars {
  display: flex;
  gap: 0.125rem;
}

.star {
  font-size: 1rem;
  color: #d1d5db;
  transition: color 0.2s;
}

.star.filled {
  color: #fbbf24;
}

.rating-text {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
}

.dark .rating-text {
  color: #9ca3af;
}

.review-count {
  color: #9ca3af;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 0.75rem;
  border-top: 1px solid #e5e7eb;
}

.dark .product-footer {
  border-top-color: #374151;
}

.price-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.price-original {
  font-size: 0.75rem;
  color: #9ca3af;
  text-decoration: line-through;
}

.price {
  font-size: 1.125rem;
  font-weight: 700;
  color: #ef4444;
}

.dark .price {
  color: #f87171;
}

.btn-add-cart {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 0.5rem;
  background: linear-gradient(135deg, #9333ea, #7c3aed);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(147, 51, 234, 0.3);
}

.btn-add-cart:hover {
  background: linear-gradient(135deg, #7c3aed, #6d28d9);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(147, 51, 234, 0.4);
}

.btn-add-cart i {
  font-size: 1.125rem;
}

.product-image-container {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  inset: 0;
}

.product-main {
  z-index: 5;
}

.product-hover {
  z-index: 10;
}
</style>

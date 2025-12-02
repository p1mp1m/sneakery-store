<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 py-6">
    <div class="max-w-6xl mx-auto px-4 space-y-6">
      <!-- Header -->
      <div
        class="relative overflow-hidden bg-gradient-to-r from-purple-600 via-purple-700 to-indigo-700 rounded-xl p-6 shadow-lg"
      >
        <div
          class="absolute inset-0 bg-gradient-to-br from-purple-900/20 to-transparent"
        ></div>
        <div class="relative">
          <h1
            class="text-3xl md:text-4xl font-bold text-white mb-2 flex items-center gap-3"
          >
            <div
              class="w-10 h-10 rounded-lg bg-white/20 backdrop-blur-sm flex items-center justify-center"
            >
              <i class="material-icons text-white">favorite</i>
            </div>
            Danh sách yêu thích
          </h1>
          <p class="text-purple-100 text-sm md:text-base">
            {{ wishlistItems.length }} sản phẩm yêu thích
          </p>
        </div>
      </div>

      <!-- Loading State -->
      <div
        v-if="loading"
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4"
        role="status"
        aria-live="polite"
      >
        <LoadingSkeleton
          v-for="n in 8"
          :key="n"
          type="card"
          :show-image="true"
        />
        <span class="sr-only">Đang tải danh sách yêu thích</span>
      </div>

      <!-- Empty State -->
      <div
        v-else-if="wishlistItems.length === 0"
        class="text-center py-20 bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-900 rounded-xl border border-gray-200 dark:border-gray-700"
      >
        <div
          class="w-24 h-24 mx-auto mb-6 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center"
        >
          <i
            class="material-icons text-5xl text-purple-600 dark:text-purple-400"
            >favorite_border</i
          >
        </div>
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">
          Danh sách yêu thích trống
        </h2>
        <p class="text-gray-600 dark:text-gray-400 mb-8 max-w-md mx-auto">
          Bắt đầu thêm sản phẩm vào danh sách yêu thích của bạn!
        </p>
        <router-link
          to="/home/products"
          class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02]"
        >
          <i class="material-icons text-lg">shopping_cart</i>
          Bắt đầu mua sắm
        </router-link>
      </div>

      <div v-else class="space-y-6">
        <!-- Actions Bar -->
        <div
          class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 p-4 bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl border border-gray-200 dark:border-gray-700"
        >
          <div class="flex items-center gap-2 text-gray-600 dark:text-gray-400">
            <i class="material-icons text-purple-600 dark:text-purple-400"
              >inventory_2</i
            >
            <span class="font-medium">{{ wishlistItems.length }} sản phẩm</span>
          </div>
          <div class="flex flex-wrap gap-3">
            <button
              @click="shareWishlist"
              :disabled="wishlistItems.length === 0"
              class="px-4 py-2 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
            >
              <i class="material-icons text-lg">share</i>
              <span class="hidden sm:inline">Chia sẻ</span>
            </button>
            <button
              @click="clearWishlist"
              :disabled="wishlistItems.length === 0"
              class="px-4 py-2 border border-gray-200 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg font-semibold hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center gap-2"
            >
              <i class="material-icons text-lg">delete_sweep</i>
              <span class="hidden sm:inline">Xóa tất cả</span>
            </button>
            <button
              @click="addAllToCart"
              :disabled="wishlistItems.length === 0"
              class="px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
            >
              <i class="material-icons text-lg">shopping_cart</i>
              <span class="hidden sm:inline">Thêm tất cả vào giỏ</span>
            </button>
          </div>
        </div>

        <!-- Wishlist Grid -->
        <div
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4"
        >
          <div
            v-for="item in wishlistItems"
            :key="item.id"
            class="group relative bg-white dark:bg-gray-800/80 backdrop-blur-sm rounded-xl border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-lg hover:border-purple-300 dark:hover:border-purple-600 transition-all duration-200 hover:translate-y-[-2px]"
          >
            <div
              class="relative aspect-square overflow-hidden bg-gray-100 dark:bg-gray-700"
            >
              <!-- <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.productName" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" /> -->
              <img
                :src="getProductImage(item.productId)"
                :alt="item.productName"
                class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                loading="lazy"
                decoding="async"
                @error="
                  (e) => {
                    if (!e.target.dataset.failed) {
                      e.target.dataset.failed = true;
                      e.target.src = '/placeholder-image.png';
                    }
                  }
                "
              />
              <div
                class="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-all flex items-center justify-center gap-2 opacity-0 group-hover:opacity-100"
              >
                <button
                  @click="removeFromWishlist(item)"
                  class="w-10 h-10 rounded-full bg-red-500 text-white flex items-center justify-center hover:bg-red-600 transition-colors shadow-lg"
                  aria-label="Xóa khỏi yêu thích"
                >
                  <i class="material-icons text-lg">delete</i>
                </button>
                <button
                  @click="addToCart(item)"
                  class="w-10 h-10 rounded-full bg-purple-600 text-white flex items-center justify-center hover:bg-purple-700 transition-colors shadow-lg"
                  aria-label="Thêm vào giỏ hàng"
                >
                  <i class="material-icons text-lg">shopping_cart</i>
                </button>
              </div>
              <div v-if="!item.isActive" class="absolute top-2 right-2">
                <span
                  class="px-2 py-1 bg-red-500 text-white text-xs font-semibold rounded-full shadow-lg"
                  >Ngừng bán</span
                >
              </div>
              <div class="absolute top-2 left-2">
                <div
                  class="w-8 h-8 rounded-full bg-white/90 dark:bg-gray-800/90 backdrop-blur-sm flex items-center justify-center"
                >
                  <i class="material-icons text-red-500 text-sm">favorite</i>
                </div>
              </div>
            </div>

            <div class="p-4">
              <h3
                class="font-semibold text-gray-900 dark:text-gray-100 mb-1 line-clamp-2 min-h-[3rem]"
              >
                {{ item.productName }}
              </h3>
              <p class="text-sm text-gray-500 dark:text-gray-400 mb-2">
                {{ item.brandName }}
              </p>
              <div class="flex items-center gap-2 mb-3">
                <span
                  class="font-bold text-lg text-purple-600 dark:text-purple-400"
                  >{{ formatCurrency(item.price || item.priceSale || 0) }}</span
                >
                <span
                  v-if="item.priceSale && item.priceBase > item.priceSale"
                  class="text-sm text-gray-400 line-through"
                >
                  {{ formatCurrency(item.priceBase) }}
                </span>
              </div>

              <div
                class="flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400 pt-2 border-t border-gray-200 dark:border-gray-700"
              >
                <i class="material-icons text-xs">schedule</i>
                <span>Đã thêm: {{ formatDate(item.addedAt) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Share Modal -->
    <div
      v-if="showShareModal"
      class="fixed inset-0 z-[9999] bg-black/50 backdrop-blur-sm flex items-center justify-center p-4"
      @click.self="showShareModal = false"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-md w-full animate-in fade-in zoom-in duration-200"
      >
        <div
          class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700 bg-gradient-to-r from-purple-50 to-indigo-50 dark:from-purple-900/20 dark:to-indigo-900/20"
        >
          <h3
            class="text-xl font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
          >
            <i class="material-icons text-purple-600 dark:text-purple-400"
              >share</i
            >
            Chia sẻ wishlist
          </h3>
          <button
            @click="showShareModal = false"
            class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            aria-label="Đóng"
          >
            <i class="material-icons">close</i>
          </button>
        </div>
        <div class="p-6 space-y-6">
          <p class="text-gray-600 dark:text-gray-400">
            Chia sẻ danh sách yêu thích của bạn với bạn bè:
          </p>

          <div>
            <label
              class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-2"
              >Link chia sẻ:</label
            >
            <div class="flex gap-2">
              <input
                v-model="shareLink"
                readonly
                class="flex-1 px-4 py-3 border border-gray-200 dark:border-gray-600 rounded-lg bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:outline-none"
              />
              <button
                @click="copyLink"
                class="px-4 py-3 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all shadow-lg hover:shadow-xl hover:scale-[1.02] flex items-center gap-2"
              >
                <i class="material-icons text-lg">content_copy</i>
                <span class="hidden sm:inline">Copy</span>
              </button>
            </div>
          </div>

          <div>
            <label
              class="block text-sm font-semibold text-gray-900 dark:text-gray-100 mb-3"
              >Chia sẻ qua:</label
            >
            <div class="flex gap-3 justify-center">
              <button
                @click="shareOnFacebook"
                class="w-14 h-14 rounded-full bg-[#1877f2] text-white flex items-center justify-center hover:bg-[#166fe5] transition-all shadow-lg hover:shadow-xl hover:scale-110"
                aria-label="Chia sẻ trên Facebook"
              >
                <i class="material-icons text-2xl">facebook</i>
              </button>
              <button
                @click="shareOnTwitter"
                class="w-14 h-14 rounded-full bg-[#1da1f2] text-white flex items-center justify-center hover:bg-[#1a91da] transition-all shadow-lg hover:shadow-xl hover:scale-110"
                aria-label="Chia sẻ trên Twitter"
              >
                <i class="material-icons text-2xl">share</i>
              </button>
              <button
                @click="shareOnWhatsApp"
                class="w-14 h-14 rounded-full bg-[#25d366] text-white flex items-center justify-center hover:bg-[#20ba5a] transition-all shadow-lg hover:shadow-xl hover:scale-110"
                aria-label="Chia sẻ trên WhatsApp"
              >
                <i class="material-icons text-2xl">chat</i>
              </button>
            </div>
          </div>
        </div>
        <div
          class="flex justify-end p-6 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50"
        >
          <button
            @click="showShareModal = false"
            class="px-6 py-3 bg-white dark:bg-gray-700 border border-gray-200 dark:border-gray-600 text-gray-900 dark:text-gray-100 rounded-lg font-semibold hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors flex items-center gap-2"
          >
            <i class="material-icons text-lg">close</i>
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import notificationService from "@/utils/notificationService";
import confirmDialogService from "@/utils/confirmDialogService";
import { useWishlistStore } from "@/stores/wishlist";
import { useAuthStore } from "@/stores/auth";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import { formatCurrency } from "@/utils/formatters";
import ProductService from "@/services/productService";

const router = useRouter();
const wishlistStore = useWishlistStore();
const authStore = useAuthStore();

// Computed từ store
const wishlistItems = computed(() => wishlistStore.wishlistItems);
const loading = computed(() => wishlistStore.loading);

// Share state
const showShareModal = ref(false);
const shareLink = ref("");
const productImageMap = ref(new Map());

const loadProductImages = async () => {
  try {
    const res = await ProductService.getAllProductImages();

    const map = new Map();

    res.data.forEach((item) => {
      if (item.productId && item.images?.length > 0) {
        map.set(item.productId, item.images[0]);
      }
    });

    productImageMap.value = map;

    console.log(
      "✅ Wishlist loaded image map:",
      productImageMap.value.size,
      "items"
    );
  } catch (err) {
    console.error("❌ Error loading product image map:", err);
  }
};

const getProductImage = (productId) => {
  return productImageMap.value.get(productId) || "/placeholder-image.png";
};

// Load wishlist khi component mount
// onMounted(async () => {
//   try {
//     await wishlistStore.fetchWishlist();
//   } catch (error) {
//     notificationService.error('Lỗi','Không thể tải danh sách yêu thích');
//   }
// });
onMounted(async () => {
  try {
    await Promise.all([wishlistStore.fetchWishlist(), loadProductImages()]);
  } catch (error) {
    notificationService.error("Lỗi", "Không thể tải danh sách yêu thích");
  }
});

// formatCurrency đã được import từ @/utils/formatters

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

const removeFromWishlist = async (item) => {
  try {
    await confirmDialogService.confirm(
      `Bạn có chắc chắn muốn xóa "${item.productName}" khỏi danh sách yêu thích?`,
      "Xác nhận",
      {
        confirmButtonText: "Xóa",
        cancelButtonText: "Hủy",
        type: "warning",
      }
    );

    await wishlistStore.removeFromWishlist(item.productId);
    notificationService.success(
      "Thành công",
      `Đã xóa "${item.productName}" khỏi danh sách yêu thích`
    );
  } catch (error) {
    if (error !== "cancel") {
      notificationService.error("Lỗi", "Không thể xóa sản phẩm");
    }
  }
};

const addToCart = (item) => {
  if (!item.isActive) {
    notificationService.warning("Cảnh báo", "Sản phẩm này đã ngừng bán");
    return;
  }

  // Navigate to product detail page để user chọn size
  router.push(`/home/products/${item.productSlug}`);
};

const addAllToCart = () => {
  const activeItems = wishlistItems.value.filter((item) => item.isActive);

  if (activeItems.length === 0) {
    notificationService.warning(
      "Cảnh báo",
      "Không có sản phẩm nào khả dụng trong danh sách yêu thích"
    );
    return;
  }

  // Navigate về trang sản phẩm để thêm từng cái
  notificationService.info("Thông tin", "Vui lòng chọn size cho từng sản phẩm");
};

const clearWishlist = async () => {
  try {
    await confirmDialogService.confirm(
      "Bạn có chắc chắn muốn xóa tất cả sản phẩm khỏi danh sách yêu thích?",
      "Xác nhận",
      {
        confirmButtonText: "Xóa tất cả",
        cancelButtonText: "Hủy",
        type: "warning",
      }
    );

    await wishlistStore.clearWishlist();
    notificationService.success(
      "Thành công",
      "Đã xóa tất cả sản phẩm khỏi danh sách yêu thích"
    );
  } catch (error) {
    if (error !== "cancel") {
      notificationService.error("Lỗi", "Không thể xóa wishlist");
    }
  }
};

const goToHome = () => {
  router.push("/home");
};

const shareWishlist = () => {
  const userId = authStore.currentUser?.id;
  if (userId) {
    shareLink.value = `${window.location.origin}/wishlist/shared/${userId}`;
    showShareModal.value = true;
  } else {
    notificationService.error("Lỗi", "Vui lòng đăng nhập để chia sẻ wishlist");
  }
};

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(shareLink.value);
    notificationService.success("Thành công", "Đã copy link vào clipboard");
  } catch (error) {
    notificationService.error("Lỗi", "Không thể copy link");
  }
};

const shareOnFacebook = () => {
  const url = encodeURIComponent(shareLink.value);
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}`, "_blank");
};

const shareOnTwitter = () => {
  const text = encodeURIComponent(
    "Xem danh sách yêu thích của tôi trên Sneakery Store!"
  );
  const url = encodeURIComponent(shareLink.value);
  window.open(
    `https://twitter.com/intent/tweet?text=${text}&url=${url}`,
    "_blank"
  );
};

const shareOnWhatsApp = () => {
  const text = encodeURIComponent(
    "Xem danh sách yêu thích của tôi: " + shareLink.value
  );
  window.open(`https://wa.me/?text=${text}`, "_blank");
};
</script>

<template>
  <div class="space-y-4">
    <!-- ====== MODE SWITCH ====== -->
    <div class="flex gap-2 p-1 bg-gray-100 dark:bg-gray-700 rounded-lg">
      <button
        type="button"
        class="flex-1 px-4 py-2 text-sm font-medium rounded-md transition-all"
        :class="
          mode === 'local'
            ? 'bg-white dark:bg-gray-600 text-purple-600 dark:text-purple-400 shadow-sm'
            : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
        "
        @click.prevent.stop="switchMode('local')"
      >
        📁 Upload từ máy
      </button>
      <button
        type="button"
        class="flex-1 px-4 py-2 text-sm font-medium rounded-md transition-all"
        :class="
          mode === 'url'
            ? 'bg-white dark:bg-gray-600 text-purple-600 dark:text-purple-400 shadow-sm'
            : 'text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-200'
        "
        @click.prevent.stop="switchMode('url')"
      >
        🌐 Upload từ URL
      </button>
    </div>

    <!-- ====== GALLERY LIST ====== -->
    <div
      class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3"
    >
      <!-- Danh sách ảnh -->
      <div
        class="relative group aspect-square bg-gray-100 dark:bg-gray-700 rounded-lg overflow-hidden border-2 border-gray-200 dark:border-gray-600 cursor-move"
        v-for="(img, idx) in images"
        :key="idx"
        draggable="true"
        @dragstart="dragStart(idx)"
        @dragover.prevent
        @drop="drop(idx)"
      >
        <img
          :src="getImageUrl(img.previewUrl)"
          alt="preview"
          class="w-full h-full object-cover"
        />
        <div
          class="absolute top-2 right-2 flex flex-col gap-1 opacity-0 group-hover:opacity-100 transition-opacity"
        >
          <!-- <button
            type="button"
            form="none"
            class="p-1.5 rounded-lg transition-colors"
            :class="img.isPrimary 
              ? 'bg-yellow-500 text-white hover:bg-yellow-600' 
              : 'bg-white/90 dark:bg-gray-800/90 text-gray-600 dark:text-gray-400 hover:bg-white dark:hover:bg-gray-800'"
            title="Đặt làm ảnh bìa"
            @click.prevent.stop="setPrimary(idx)"
          >
            <i class="material-icons text-base">star</i>
          </button> -->

          <button
            type="button"
            form="none"
            class="p-1.5 bg-white/90 dark:bg-gray-800/90 text-red-600 dark:text-red-400 rounded-lg hover:bg-white dark:hover:bg-gray-800 transition-colors"
            title="Xóa ảnh này"
            @click.prevent.stop="removeImage(idx)"
          >
            <i class="material-icons text-base">delete</i>
          </button>
        </div>
        <div
          v-if="img.isPrimary"
          class="absolute top-2 left-2 px-2 py-1 bg-yellow-500 text-white text-xs font-medium rounded"
        >
          Ảnh bìa
        </div>
      </div>

      <!-- Placeholder (+) -->
      <div
        v-if="mode === 'local' && images.length < maxImages"
        class="aspect-square bg-gray-50 dark:bg-gray-900/50 border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg flex flex-col items-center justify-center cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-900 transition-colors"
        @click.stop="$refs.fileInput.click()"
      >
        <i class="material-icons text-3xl text-gray-400 dark:text-gray-500 mb-2"
          >add_photo_alternate</i
        >
        <p class="text-xs text-gray-500 dark:text-gray-400">Thêm ảnh</p>
        <input
          type="file"
          multiple
          accept="image/*"
          ref="fileInput"
          @change="handleFileSelect"
          class="hidden"
        />
      </div>

      <!-- Input URL khi chọn chế độ URL -->
      <div v-if="mode === 'url'" class="col-span-full">
        <div class="flex gap-2">
          <input
            v-model="imageUrlInput"
            type="text"
            placeholder="Dán hoặc nhập URL ảnh (https://...)"
            class="flex-1 px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            @keyup.enter="handleUrlAdd"
          />
          <button
            type="button"
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
            @click="handleUrlAdd"
          >
            <i class="material-icons text-base">add</i>
            Thêm
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onUnmounted } from "vue";
import { buildApiUrl } from "@/config/api";
import notificationService from "@/utils/notificationService";
import OptimizedImage from "@/components/common/OptimizedImage.vue";

/**
 * Component này chỉ hiển thị và quản lý preview ảnh.
 * Không upload thật, chỉ emit danh sách ảnh (file/url/isPrimary) ra ngoài.
 */
const emit = defineEmits(["change", "remove"]);

const mode = ref("local");
const imageUrlInput = ref("");
const images = ref([]);
const dragIndex = ref(null);

// Helper function để convert relative URL thành absolute URL
const getImageUrl = (url) => {
  if (!url) return "";

  // Nếu đã là absolute URL (http/https) hoặc blob URL, trả về nguyên
  if (
    url.startsWith("http://") ||
    url.startsWith("https://") ||
    url.startsWith("blob:")
  ) {
    return url;
  }

  // Nếu là relative URL (bắt đầu bằng /), prefix với backend URL
  if (url.startsWith("/")) {
    return buildApiUrl(url);
  }

  return url;
};

// ✳️ hàm dùng lại để nạp initialImages
const loadFromInitial = () => {
  // cleanup blob cũ ...
  images.value = (props.initialImages || []).map((it) => ({
    id: it.id ?? null,
    file: null,
    previewUrl: it.previewUrl,
    isPrimary: !!it.isPrimary,
    type: it.type || "url",
  }));
  imageUrlInput.value = "";
  // emitChange(); // ✅ đồng bộ về parent ngay cả khi user chưa tương tác
};

const switchMode = (newMode) => {
  mode.value = newMode;
  imageUrlInput.value = "";
  if (newMode === "url") {
    notificationService.info("Thông tin", "Bạn đang ở chế độ nhập URL ảnh 🌐");
  } else {
    notificationService.info("Thông tin", "Bạn đang ở chế độ nhập Local 📁");
  }
};

const emitChange = () => {
  emit(
    "change",
    images.value.map((img, idx) => ({
      file: img.type === "local" ? img.file : null,
      previewUrl: img.previewUrl, // ✅ dùng previewUrl, không dùng 'url'
      isPrimary: !!img.isPrimary,
      displayOrder: idx,
      type: img.type, // ✅ QUAN TRỌNG
    }))
  );
};

/** ====== Upload từ Local ====== */
const handleFileSelect = (e) => {
  const files = e.target.files;
  if (!files || files.length === 0) return;
  if (images.value.length + files.length > props.maxImages)
    return notificationService.warning(
      "Cảnh báo",
      `Chỉ được chọn tối đa ${props.maxImages} ảnh`
    );
  for (const file of files) {
    const previewUrl = URL.createObjectURL(file);
    images.value.push({ file, previewUrl, isPrimary: false, type: "local" });
  }
  e.target.value = "";
  emitChange();
};

/** ====== Thêm ảnh từ URL ====== */
const handleUrlAdd = () => {
  const url = imageUrlInput.value.trim();
  if (!url)
    return notificationService.warning("Cảnh báo", "Vui lòng nhập URL ảnh");
  const isLikelyImageUrl =
    /^https?:\/\/.+/i.test(url) &&
    // chấp nhận có hoặc không đuôi, nhưng nếu có đuôi thì phải hợp lệ
    (!/\.(\w{2,5})(\?.*)?$/i.test(url) ||
      /\.(jpg|jpeg|png|gif|webp)(\?.*)?$/i.test(url));

  if (!isLikelyImageUrl) {
    return notificationService.error(
      "Lỗi",
      "URL không hợp lệ hoặc không phải ảnh"
    );
  }
  if (images.value.length >= props.maxImages)
    return notificationService.warning(
      "Cảnh báo",
      `Chỉ được chọn tối đa ${props.maxImages} ảnh`
    );

  images.value.push({
    file: null,
    previewUrl: url,
    isPrimary: false,
    type: "url",
  });
  imageUrlInput.value = "";
  emitChange();
};

/** ====== Hành động với ảnh ====== */
// const setPrimary = (index) => {
//   images.value.forEach((img, i) => (img.isPrimary = i === index));
//   emitChange();
// };

// 🗑️ Sửa removeImage: luôn emit và gửi payload chuẩn
const removeImage = (index) => {
  const img = images.value[index];
  if (img?.previewUrl?.startsWith("blob:")) {
    URL.revokeObjectURL(img.previewUrl);
  }

  images.value.splice(index, 1);
  emitChange();

  // 👉 Báo cho parent biết ảnh thật đã bị xoá khỏi preview
  if (img?.previewUrl && !img.previewUrl.startsWith("blob:")) {
    // -   emit("remove", img.previewUrl);
    emit("remove", { url: img.previewUrl, type: img.type, id: img.id ?? null });
  }
};

const dragStart = (i) => (dragIndex.value = i);
const drop = (i) => {
  if (dragIndex.value == null || dragIndex.value === i) return;
  const moved = images.value.splice(dragIndex.value, 1)[0];
  images.value.splice(i, 0, moved);
  dragIndex.value = null;
  emitChange();
};

const props = defineProps({
  maxImages: {
    type: Number,
    default: 10,
  },
  resetKey: { type: Number, default: 0 },
  // ⬇️ danh sách ảnh ban đầu: [{ previewUrl, isPrimary, type: 'url'|'local' }]
  initialImages: { type: Array, default: () => [] },
});

// ⬅️ resetKey đổi => clear & nạp initial
watch(
  () => props.resetKey,
  () => {
    images.value = [];
    imageUrlInput.value = "";
    loadFromInitial();
    emitChange(); // ✅ gọi lại khi resetKey đổi
  }
);

// ⬅️ initialImages đổi (ví dụ vừa fetch xong) => nạp lại
watch(
  () => props.initialImages,
  () => {
    loadFromInitial();
  },
  { deep: true, immediate: true }
);

onUnmounted(() => {
  for (const img of images.value) {
    if (img.previewUrl?.startsWith("blob:"))
      URL.revokeObjectURL(img.previewUrl);
  }
});

/** Dọn dẹp object URL khi thay đổi */
watch(
  images,
  (newList, oldList) => {
    const removed = oldList.filter((x) => !newList.includes(x));
    for (const img of removed) {
      if (img.previewUrl && img.previewUrl.startsWith("blob:")) {
        URL.revokeObjectURL(img.previewUrl);
      }
    }
  },
  { deep: true }
);
</script>

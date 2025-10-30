<template>
  <div class="upload-gallery">
    <!-- ====== MODE SWITCH ====== -->
    <div class="mode-switch">
      <button
        type="button"
        class="mode-btn"
        :class="{ active: mode === 'local' }"
        @click.prevent.stop="switchMode('local')"
      >
        📁 Upload từ máy
      </button>
      <button
        type="button"
        class="mode-btn"
        :class="{ active: mode === 'url' }"
        @click.prevent.stop="switchMode('url')"
      >
        🌐 Upload từ URL
      </button>
    </div>

    <!-- ====== GALLERY LIST ====== -->
    <div class="preview-list">
      <!-- Danh sách ảnh -->
      <div
        class="preview-item"
        v-for="(img, idx) in images"
        :key="idx"
        draggable="true"
        @dragstart="dragStart(idx)"
        @dragover.prevent
        @drop="drop(idx)"
      >
        <img :src="img.previewUrl" alt="preview" />
        <div class="preview-actions-top">
          <button
            type="button"
            form="none"
            class="btn-star"
            :class="{ active: img.isPrimary }"
            title="Đặt làm ảnh bìa"
            @click.prevent.stop="setPrimary(idx)"
          >
            <i class="material-icons">star</i>
          </button>

          <button
            type="button"
            form="none"
            class="btn-delete"
            title="Xóa ảnh này"
            @click.prevent.stop="removeImage(idx)"
          >
            <i class="material-icons">delete</i>
          </button>
        </div>
      </div>

      <!-- Placeholder (+) -->
      <div
        v-if="mode === 'local' && images.length < 10"
        class="upload-placeholder"
        @click.stop="$refs.fileInput.click()"
      >
        <span class="plus">+</span>
        <p>Thêm ảnh</p>
        <input
          type="file"
          multiple
          accept="image/*"
          ref="fileInput"
          @change="handleFileSelect"
          hidden
        />
      </div>

      <!-- Input URL khi chọn chế độ URL -->
      <div v-if="mode === 'url'" class="upload-url-box">
        <input
          v-model="imageUrlInput"
          type="text"
          placeholder="Dán hoặc nhập URL ảnh (https://...)"
          class="input-url"
          @keyup.enter="handleUrlAdd"
        />
        <button type="button" class="btn-upload" @click="handleUrlAdd">
          ➕ Thêm
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onUnmounted } from "vue";
// import toastService from "@/utils/toastService";
import { ElMessage } from "element-plus";

/**
 * Component này chỉ hiển thị và quản lý preview ảnh.
 * Không upload thật, chỉ emit danh sách ảnh (file/url/isPrimary) ra ngoài.
 */
const emit = defineEmits(["change", "remove"]);

const mode = ref("local");
const imageUrlInput = ref("");
const images = ref([]);
const dragIndex = ref(null);

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
    ElMessage.info("Bạn đang ở chế độ nhập URL ảnh 🌐");
  } else {
    ElMessage.info("Bạn đang ở chế độ nhập Local 📁");
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
  if (images.value.length + files.length > 10)
    return ElMessage.warning({
      message: "Chỉ được chọn tối đa 10 ảnh",
      duration: 3000,
    });
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
    return ElMessage.warning({
      message: "Vui lòng nhập URL ảnh",
      duration: 3000,
    });
  const isLikelyImageUrl =
    /^https?:\/\/.+/i.test(url) &&
    // chấp nhận có hoặc không đuôi, nhưng nếu có đuôi thì phải hợp lệ
    (!/\.(\w{2,5})(\?.*)?$/i.test(url) ||
      /\.(jpg|jpeg|png|gif|webp)(\?.*)?$/i.test(url));

  if (!isLikelyImageUrl) {
    return ElMessage.error({
      message: "URL không hợp lệ hoặc không phải ảnh",
      duration: 3000,
    });
  }
  if (images.value.length >= 10)
    return ElMessage.warning({
      message: "Chỉ được chọn tối đa 10 ảnh",
      duration: 3000,
    });

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
const setPrimary = (index) => {
  images.value.forEach((img, i) => (img.isPrimary = i === index));
  emitChange();
};

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

<style scoped>
.upload-gallery {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* ===== Mode Switch ===== */
.mode-switch {
  display: flex;
  gap: 8px;
}
.mode-btn {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid var(--border-primary);
  border-radius: 8px;
  cursor: pointer;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  transition: 0.25s;
  font-weight: 500;
}
.mode-btn.active {
  background: var(--gradient-primary);
  color: #fff;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(167, 139, 250, 0.3);
}
.mode-btn:hover:not(.active) {
  background: var(--bg-hover);
}

/* ===== Preview Grid ===== */
.preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.preview-item {
  width: 120px;
  height: 120px;
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid var(--border-primary);
  background: var(--bg-secondary);
  transition: 0.2s;
}
.preview-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}
.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.preview-actions {
  position: absolute;
  bottom: 4px;
  right: 4px;
  display: flex;
  gap: 4px;
}
.preview-actions button {
  background: rgba(0, 0, 0, 0.65);
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  padding: 2px 5px;
  transition: 0.2s;
}
.preview-actions button:hover {
  background: rgba(0, 0, 0, 0.85);
}
.preview-actions button.active {
  background: gold;
  color: black;
}

/* ===== Action buttons (⭐ & 🗑) ===== */
.preview-actions-top {
  position: absolute;
  top: 6px;
  left: 6px;
  right: 6px;
  display: flex;
  justify-content: space-between;
  pointer-events: none;
  z-index: 2;
}

/* ====== BASE BUTTON ====== */
.preview-actions-top button {
  pointer-events: all;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0.95;

  /* nền tím neon sẫm */
  background: radial-gradient(
    circle at center,
    rgba(100, 60, 200, 0.35),
    rgba(50, 20, 120, 0.25)
  );
  box-shadow: 0 0 6px rgba(140, 80, 255, 0.4),
    inset 0 0 4px rgba(100, 50, 200, 0.4);
}

/* Hover chung: tím neon sáng lên */
.preview-actions-top button:hover {
  background: radial-gradient(
    circle at center,
    rgba(160, 100, 255, 0.55),
    rgba(80, 40, 180, 0.35)
  );
  box-shadow: 0 0 10px rgba(160, 90, 255, 0.7), 0 0 18px rgba(150, 80, 255, 0.5);
  transform: scale(1.1);
}

/* ====== ICON SAO ====== */
.btn-star {
  color: #bbb;
  text-shadow: 0 0 3px rgba(255, 255, 255, 0.4);
}

/* Hover: sao vàng neon nhẹ */
.btn-star:hover {
  color: #ffde59;
  text-shadow: 0 0 6px rgba(255, 220, 90, 0.9);
}

/* Active: sao vàng neon rực, nền tím phát sáng mạnh */
.btn-star.active {
  background: radial-gradient(
    circle at center,
    rgba(160, 100, 255, 0.65),
    rgba(100, 50, 200, 0.35)
  );
  box-shadow: 0 0 12px rgba(180, 100, 255, 0.8),
    0 0 24px rgba(160, 80, 255, 0.6), 0 0 36px rgba(120, 50, 255, 0.3);
  transform: scale(1.15);
}

/* Ngôi sao vàng neon rực khi active */
.btn-star.active i {
  color: #ffeb70; /* vàng neon tươi */
  text-shadow: 0 0 8px rgba(255, 240, 100, 1), 0 0 16px rgba(255, 220, 70, 0.9),
    0 0 32px rgba(255, 210, 60, 0.8);
}

/* ====== ICON DELETE ====== */
.btn-delete {
  color: #fff;
  background: radial-gradient(
    circle at center,
    rgba(255, 60, 60, 0.8),
    rgba(180, 0, 80, 0.4)
  );
  box-shadow: 0 0 6px rgba(255, 60, 60, 0.4),
    inset 0 0 4px rgba(255, 100, 100, 0.4);
}

/* Hover: đỏ neon phát sáng */
.btn-delete:hover {
  background: radial-gradient(
    circle at center,
    rgba(255, 80, 80, 0.9),
    rgba(200, 40, 120, 0.5)
  );
  box-shadow: 0 0 12px rgba(255, 80, 80, 0.8), 0 0 22px rgba(180, 60, 255, 0.6);
  transform: scale(1.15);
}

.btn-delete:hover i {
  color: #ff6666;
  text-shadow: 0 0 6px rgba(255, 100, 100, 0.9), 0 0 12px rgba(255, 80, 80, 0.8),
    0 0 20px rgba(255, 60, 60, 0.6);
}

/* Fade-in khi hover ảnh
.preview-item .preview-actions-top {
  opacity: 0;
  transition: opacity 0.4s ease;
}
.preview-item:hover .preview-actions-top {
  opacity: 1;
} */

/* ===== Placeholder Box ===== */
.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 2px dashed var(--border-primary);
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: var(--text-muted);
  cursor: pointer;
  background: var(--bg-secondary);
  transition: 0.25s;
}
.upload-placeholder:hover {
  border-color: var(--accent-primary);
  color: var(--accent-primary);
  transform: scale(1.02);
}
.plus {
  font-size: 28px;
  font-weight: bold;
}

/* ===== URL Input ===== */
.upload-url-box {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  flex-wrap: wrap;
  margin-top: 8px;
}
.input-url {
  flex: 1;
  min-width: 250px;
  padding: 8px 10px;
  border: 1px solid var(--border-primary);
  border-radius: 8px;
  background: var(--bg-primary);
  color: var(--text-primary);
  outline: none;
  transition: 0.25s;
}
.input-url:focus {
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.15);
}
.btn-upload {
  padding: 8px 14px;
  border: none;
  border-radius: 8px;
  background: var(--gradient-primary);
  color: #fff;
  cursor: pointer;
  font-weight: 500;
  transition: 0.25s;
}
.btn-upload:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}
</style>

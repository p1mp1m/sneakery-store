<template>
  <div
    v-if="visible"
    class="fixed inset-0 z-[10000] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
    @click.self="close"
  >
    <div
      class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-md w-full border border-gray-200 dark:border-gray-700"
    >
      <!-- Header -->
      <div
        class="p-4 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between"
      >
        <div class="flex items-center gap-2">
          <i class="material-icons text-purple-500 dark:text-purple-400"
            >palette</i
          >
          <h4 class="text-base font-semibold text-gray-900 dark:text-gray-100">
            Chọn màu sắc
          </h4>
        </div>

        <button
          type="button"
          class="p-1.5 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
          @click="close"
        >
          <i class="material-icons text-base">close</i>
        </button>
      </div>

      <!-- Body -->
      <div class="p-6 grid grid-cols-4 gap-4 place-items-center">
        <div
          v-for="(c, idx) in colors"
          :key="idx"
          class="w-12 h-12 rounded-full cursor-pointer border-2 transition-all"
          :style="{ backgroundColor: c.hex }"
          @click="selectColor(c)"
          :class="
            selected === c.name
              ? 'border-purple-500 ring-2 ring-purple-300 dark:ring-purple-700 scale-110'
              : 'border-gray-300 dark:border-gray-600 hover:scale-105'
          "
        ></div>
      </div>

      <!-- Footer -->
      <div
        class="p-4 border-t border-gray-200 dark:border-gray-700 flex justify-end gap-2"
      >
        <button
          type="button"
          class="px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600"
          @click="close"
        >
          Hủy
        </button>
        <button
          type="button"
          class="px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200"
          :disabled="!selected"
          @click="confirm"
        >
          Xác nhận
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import toastService from "@/utils/toastService";

/**
 * Ánh xạ tên màu tiếng Việt ↔ tiếng Anh
 */
const COLOR_MAP = {
  đen: "Black",
  trắng: "White",
  đỏ: "Red",
  "xanh dương": "Blue",
  xanh: "Blue",
  "xanh lá": "Green",
  vàng: "Yellow",
  tím: "Purple",
  hồng: "Pink",
  cam: "Orange",
  xám: "Gray",
};

const props = defineProps({
  visible: Boolean,
  initialSelected: [String, Object],
  colors: {
    type: Array,
    default: () => [
      { name: "Black", hex: "#000000" },
      { name: "White", hex: "#ffffff" },
      { name: "Red", hex: "#ef4444" },
      { name: "Blue", hex: "#3b82f6" },
      { name: "Green", hex: "#22c55e" },
      { name: "Yellow", hex: "#facc15" },
      { name: "Purple", hex: "#a855f7" },
      { name: "Pink", hex: "#ec4899" },
      { name: "Orange", hex: "#fb923c" },
      { name: "Gray", hex: "#9ca3af" },
    ],
  },
});

const emit = defineEmits(["close", "select"]);

const selected = ref("");

// 🧠 Đồng bộ khi mở popup
watch(
  () => props.visible,
  (open) => {
    if (open) {
      // Lấy giá trị truyền vào (VD: "Đỏ", "xanh lá", hoặc object)
      let inputName = "";

      if (
        typeof props.initialSelected === "object" &&
        props.initialSelected?.name
      ) {
        inputName = props.initialSelected.name;
      } else if (typeof props.initialSelected === "string") {
        inputName = props.initialSelected;
      }

      inputName = (inputName || "").trim().toLowerCase();

      // Chuyển tiếng Việt → tiếng Anh nếu cần
      const normalized =
        COLOR_MAP[inputName] ||
        Object.keys(COLOR_MAP).find(
          (vn) => COLOR_MAP[vn].toLowerCase() === inputName
        ) ||
        "";

      // Nếu không ánh xạ được thì giữ nguyên
      selected.value =
        props.colors.find((c) =>
          [c.name.toLowerCase(), normalized.toLowerCase()].includes(
            inputName.toLowerCase()
          )
        )?.name ||
        COLOR_MAP[inputName] ||
        "";
    }
  },
  { immediate: true }
);

const selectColor = (color) => {
  selected.value = selected.value === color.name ? "" : color.name;
};

const close = () => emit("close");

const confirm = () => {
  if (!selected.value) {
    toastService.warning("Cảnh báo", "Vui lòng chọn một màu sắc");
    return;
  }
  const colorObj = props.colors.find((c) => c.name === selected.value);
  emit("select", colorObj || { name: selected.value });
  close();
};
</script>

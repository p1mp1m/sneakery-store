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
            >straighten</i
          >
          <h4 class="text-base font-semibold text-gray-900 dark:text-gray-100">
            Chọn kích thước
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
      <div class="p-4 grid grid-cols-5 gap-2">
        <button
          v-for="(s, idx) in sizes"
          :key="idx"
          type="button"
          class="px-4 py-2 text-sm font-medium rounded-lg transition-all"
          :class="
            selected === String(s)
              ? 'bg-purple-500 text-white ring-2 ring-purple-300 dark:ring-purple-700'
              : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
          "
          @click="selectSize(s)"
        >
          {{ s }}
        </button>
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

const props = defineProps({
  visible: Boolean,
  // Có thể nhận string (khi chỉ 1 size) hoặc array (từ dữ liệu cũ)
  initialSelected: [String, Array],
  sizes: {
    type: Array,
    default: () => [35, 36, 37, 38, 39, 40, 41, 42, 43, 44],
  },
});
const emit = defineEmits(["close", "confirm"]);

const selected = ref("");

// 🧠 Khi popup mở hoặc giá trị initialSelected thay đổi
watch(
  () => props.visible,
  (v) => {
    if (v) {
      // Nếu là array → lấy phần tử đầu tiên
      if (Array.isArray(props.initialSelected)) {
        selected.value = props.initialSelected[0]
          ? String(props.initialSelected[0]).trim()
          : "";
      } else {
        selected.value = props.initialSelected
          ? String(props.initialSelected).trim()
          : "";
      }
    }
  },
  { immediate: true }
);

// 🟣 Chọn size duy nhất
const selectSize = (size) => {
  const val = String(size);
  selected.value = selected.value === val ? "" : val;
};

const close = () => emit("close");

const confirm = () => {
  if (!selected.value) {
    toastService.warning("Cảnh báo", "Vui lòng chọn một kích thước");
    return;
  }
  emit("confirm", [selected.value]); // vẫn emit dạng array để tương thích với code gọi
  close();
};
</script>

<template>
  <div
    v-if="show"
    class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/50 backdrop-blur-sm"
    @click.self="close"
  >
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-2xl max-w-2xl w-full mx-4 max-h-[80vh] overflow-hidden flex flex-col">
      <!-- Header -->
      <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="material-icons text-purple-600 dark:text-purple-400">keyboard</i>
          Phím tắt bàn phím
        </h2>
        <button
          @click="close"
          class="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
          aria-label="Đóng"
        >
          <i class="material-icons text-gray-600 dark:text-gray-300">close</i>
        </button>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-4">
        <div class="space-y-4">
          <div v-for="category in shortcuts" :key="category.title" class="space-y-2">
            <h3 class="text-sm font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wide">
              {{ category.title }}
            </h3>
            <div class="space-y-1">
              <div
                v-for="shortcut in category.items"
                :key="shortcut.keys"
                class="flex items-center justify-between p-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors"
              >
                <span class="text-sm text-gray-600 dark:text-gray-400">{{ shortcut.description }}</span>
                <kbd class="px-2 py-1 text-xs font-mono bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded border border-gray-300 dark:border-gray-600">
                  {{ shortcut.keys }}
                </kbd>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="p-4 border-t border-gray-200 dark:border-gray-700">
        <p class="text-xs text-gray-500 dark:text-gray-400 text-center">
          Nhấn <kbd class="px-1.5 py-0.5 text-xs bg-gray-100 dark:bg-gray-700 rounded">Esc</kbd> để đóng
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:show', 'close']);

const shortcuts = [
  {
    title: 'Điều hướng',
    items: [
      { keys: 'Ctrl + K', description: 'Tìm kiếm nhanh' },
      { keys: 'Esc', description: 'Đóng modal/dropdown' },
      { keys: 'Shift + ?', description: 'Hiển thị phím tắt' }
    ]
  },
  {
    title: 'Thao tác',
    items: [
      { keys: 'Ctrl + N', description: 'Tạo mới (tùy theo ngữ cảnh)' },
      { keys: 'Ctrl + S', description: 'Lưu form' }
    ]
  }
];

const close = () => {
  emit('update:show', false);
  emit('close');
};
</script>

<style scoped>
kbd {
  font-family: ui-monospace, SFMono-Regular, "SF Mono", Menlo, Consolas, "Liberation Mono", monospace;
}
</style>


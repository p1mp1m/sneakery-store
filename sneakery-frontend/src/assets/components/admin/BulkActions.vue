<template>
  <transition
    enter-active-class="transition-all duration-300 ease-out"
    enter-from-class="opacity-0 transform -translate-y-2"
    enter-to-class="opacity-100 transform translate-y-0"
    leave-active-class="transition-all duration-200 ease-in"
    leave-from-class="opacity-100 transform translate-y-0"
    leave-to-class="opacity-0 transform -translate-y-2"
  >
    <div v-if="selectedCount > 0" class="flex items-center justify-between p-4 bg-purple-50 dark:bg-purple-900/20 rounded-xl border border-purple-200 dark:border-purple-800 mb-4">
      <div class="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
        <i class="material-icons text-purple-600 dark:text-purple-400">check_circle</i>
        <span>Đã chọn <strong class="font-semibold">{{ selectedCount }}</strong> mục</span>
      </div>
      
      <div class="flex items-center gap-2">
        <slot name="actions"></slot>
        
        <button 
          v-if="showDelete" 
          @click="$emit('delete')" 
          class="flex items-center gap-2 px-3 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors text-sm font-medium"
        >
          <i class="material-icons text-base">delete</i>
          {{ deleteText }}
        </button>
        
        <button 
          @click="$emit('clear')" 
          class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
        >
          <i class="material-icons text-base">close</i>
          Bỏ chọn
        </button>
      </div>
    </div>
  </transition>
</template>

<script setup>
defineProps({
  selectedCount: {
    type: Number,
    required: true
  },
  showDelete: {
    type: Boolean,
    default: true
  },
  deleteText: {
    type: String,
    default: 'Xóa đã chọn'
  }
})

defineEmits(['delete', 'clear'])
</script>





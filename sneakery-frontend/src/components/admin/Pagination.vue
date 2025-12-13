<template>
  <div
    v-if="totalPages > 0"
    class="flex flex-col sm:flex-row items-center justify-between gap-4 px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
  >
    <!-- Info Text -->
    <div class="text-sm text-gray-600 dark:text-gray-400">
      Hiển thị {{ startItem }} - {{ endItem }} trong tổng số {{ totalItems }} {{ itemLabel }}
    </div>

    <div class="flex items-center gap-3">
      <!-- Page Size Selector -->
      <div v-if="showPageSizeSelector" class="flex items-center gap-2">
        <label for="page-size-select" class="text-sm text-gray-600 dark:text-gray-400">
          Hiển thị:
        </label>
        <select
          id="page-size-select"
          :value="pageSize"
          @change="handlePageSizeChange"
          class="px-2 py-1 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500"
        >
          <option v-for="size in pageSizeOptions" :key="size" :value="size">
            {{ size }}
          </option>
        </select>
      </div>

      <!-- Pagination Controls -->
      <div class="flex items-center gap-2">
        <!-- First Page -->
        <button
          v-if="showFirstLast"
          :disabled="currentPage === 0 || loading"
          @click="goToPage(0)"
          class="p-1.5 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-purple-500"
          :aria-label="'Trang đầu'"
          :aria-disabled="currentPage === 0"
        >
          <i class="material-icons text-base" aria-hidden="true">first_page</i>
        </button>

        <!-- Previous Page -->
        <button
          :disabled="currentPage === 0 || loading"
          @click="goToPage(currentPage - 1)"
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-purple-500"
          :aria-label="'Trang trước'"
          :aria-disabled="currentPage === 0"
        >
          <i class="material-icons text-base" aria-hidden="true">chevron_left</i>
          <span v-if="showPrevNextText">Trước</span>
        </button>

        <!-- Page Numbers -->
        <div v-if="showPageNumbers" class="flex items-center gap-1">
          <button
            v-for="page in visiblePages"
            :key="page"
            @click="goToPage(page - 1)"
            class="px-3 py-1.5 text-sm font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
            :class="
              page - 1 === currentPage
                ? 'bg-purple-500 text-white'
                : 'text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600'
            "
            :aria-label="`Trang ${page}`"
            :aria-current="page - 1 === currentPage ? 'page' : undefined"
          >
            {{ page }}
          </button>
        </div>

        <!-- Page Info (if not showing page numbers) -->
        <span
          v-else
          class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300"
          aria-live="polite"
          aria-atomic="true"
        >
          Trang {{ currentPage + 1 }} / {{ totalPages }}
        </span>

        <!-- Jump to Page -->
        <div v-if="showJumpToPage" class="flex items-center gap-1">
          <label for="jump-to-page" class="text-sm text-gray-600 dark:text-gray-400 sr-only">
            Nhảy đến trang:
          </label>
          <input
            id="jump-to-page"
            type="number"
            :min="1"
            :max="totalPages"
            :value="jumpToPageInput"
            @input="handleJumpToPageInput"
            @keyup.enter="handleJumpToPage"
            class="w-16 px-2 py-1 text-sm border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-700 dark:text-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="Trang"
            :aria-label="`Nhảy đến trang (1-${totalPages})`"
          />
          <button
            @click="handleJumpToPage"
            class="px-2 py-1 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500"
            :aria-label="'Đi đến trang'"
          >
            Đi
          </button>
        </div>

        <!-- Next Page -->
        <button
          :disabled="currentPage >= totalPages - 1 || loading"
          @click="goToPage(currentPage + 1)"
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-purple-500"
          :aria-label="'Trang sau'"
          :aria-disabled="currentPage >= totalPages - 1"
        >
          <span v-if="showPrevNextText">Sau</span>
          <i class="material-icons text-base" aria-hidden="true">chevron_right</i>
        </button>

        <!-- Last Page -->
        <button
          v-if="showFirstLast"
          :disabled="currentPage >= totalPages - 1 || loading"
          @click="goToPage(totalPages - 1)"
          class="p-1.5 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-purple-500"
          :aria-label="'Trang cuối'"
          :aria-disabled="currentPage >= totalPages - 1"
        >
          <i class="material-icons text-base" aria-hidden="true">last_page</i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  // Required props
  currentPage: {
    type: Number,
    required: true,
    default: 0
  },
  totalPages: {
    type: Number,
    required: true,
    default: 0
  },
  totalItems: {
    type: Number,
    required: true,
    default: 0
  },
  pageSize: {
    type: Number,
    required: true,
    default: 10
  },
  
  // Optional props
  itemLabel: {
    type: String,
    default: 'mục'
  },
  loading: {
    type: Boolean,
    default: false
  },
  showPageSizeSelector: {
    type: Boolean,
    default: true
  },
  showPageNumbers: {
    type: Boolean,
    default: false
  },
  showJumpToPage: {
    type: Boolean,
    default: false
  },
  showFirstLast: {
    type: Boolean,
    default: false
  },
  showPrevNextText: {
    type: Boolean,
    default: true
  },
  pageSizeOptions: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  maxVisiblePages: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['page-change', 'page-size-change'])

// Computed
const startItem = computed(() => {
  if (props.totalItems === 0) return 0
  return props.currentPage * props.pageSize + 1
})

const endItem = computed(() => {
  return Math.min((props.currentPage + 1) * props.pageSize, props.totalItems)
})

const visiblePages = computed(() => {
  if (!props.showPageNumbers) return []
  
  const pages = []
  const total = props.totalPages
  const current = props.currentPage + 1
  const maxVisible = props.maxVisiblePages
  
  if (total <= maxVisible) {
    // Show all pages if total is less than max visible
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // Show pages around current page
    let start = Math.max(1, current - Math.floor(maxVisible / 2))
    let end = Math.min(total, start + maxVisible - 1)
    
    // Adjust start if we're near the end
    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1)
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i)
    }
  }
  
  return pages
})

// State
const jumpToPageInput = ref('')

// Methods
const goToPage = (page) => {
  if (page < 0 || page >= props.totalPages || page === props.currentPage) return
  emit('page-change', page)
}

const handlePageSizeChange = (event) => {
  const newSize = parseInt(event.target.value)
  if (newSize !== props.pageSize) {
    emit('page-size-change', newSize)
  }
}

const handleJumpToPageInput = (event) => {
  jumpToPageInput.value = event.target.value
}

const handleJumpToPage = () => {
  const page = parseInt(jumpToPageInput.value)
  if (!isNaN(page) && page >= 1 && page <= props.totalPages) {
    goToPage(page - 1)
    jumpToPageInput.value = ''
  }
}

// Watch currentPage to reset jump input
watch(() => props.currentPage, () => {
  jumpToPageInput.value = ''
})
</script>


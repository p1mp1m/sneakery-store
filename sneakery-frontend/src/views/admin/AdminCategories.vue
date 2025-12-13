<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">category</i>
            Quản lý danh mục
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý danh mục sản phẩm với cấu trúc phân cấp</p>
        </div>
        <div class="flex items-center gap-2">
          <button @click="handleExport('csv')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium" title="Xuất CSV">
            <i class="material-icons text-base">download</i>
            Xuất CSV
          </button>
          <button @click="handleExport('json')" class="flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium" title="Xuất JSON">
            <i class="material-icons text-base">file_download</i>
            Xuất JSON
          </button>
          <button @click="openCreateModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm">
            <i class="material-icons text-base">add</i>
            Thêm danh mục
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-500 to-purple-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">category</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ totalCategories }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">Tổng danh mục</p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">folder_open</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ rootCategoriesCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">Danh mục gốc</p>
        </div>
      </div>
      
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">subdirectory_arrow_right</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ childCategoriesCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">Danh mục con</p>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"></div>
      <p class="text-sm text-gray-600 dark:text-gray-400">Đang tải danh sách danh mục...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="!categories || categories.length === 0" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">category</i>
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Chưa có danh mục nào</h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">Nhấn "Thêm danh mục" để tạo danh mục đầu tiên</p>
      <button @click="openCreateModal" class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium">
        <i class="material-icons text-base">add</i>
        Thêm danh mục
      </button>
    </div>

    <!-- Categories List -->
    <div v-else-if="categories && categories.length > 0" class="space-y-4">
      <!-- Search & Filters -->
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="flex flex-col md:flex-row gap-4">
          <div class="flex-1">
            <div class="relative">
              <i class="material-icons absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg">search</i>
              <input 
                type="text" 
                class="w-full pl-10 pr-4 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" 
                v-model="searchQuery"
                placeholder="Tìm kiếm danh mục..."
              />
            </div>
          </div>
          
          <div class="flex items-center gap-2">
            <button 
              v-if="selectedCategories.length > 0" 
              @click="handleBulkDelete"
              class="flex items-center gap-2 px-3 py-2 bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 rounded-lg hover:bg-red-200 dark:hover:bg-red-900/50 transition-colors text-sm font-medium"
            >
              <i class="material-icons text-base">delete</i>
              Xóa {{ selectedCategories.length }} mục
            </button>
            <button @click="expandAll" class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
              <i class="material-icons text-base">unfold_more</i>
              Mở rộng tất cả
            </button>
            <button @click="collapseAll" class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium">
              <i class="material-icons text-base">unfold_less</i>
              Thu gọn tất cả
            </button>
          </div>
        </div>
      </div>

      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600">
              <tr>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-10">
                  <input 
                    type="checkbox" 
                    @change="toggleSelectAll"
                    :checked="allSelected"
                    class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                  />
                </th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-10"></th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Tên danh mục</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Slug</th>
                <th class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Danh mục cha</th>
                <th class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
              <template v-for="category in filteredCategories" :key="category.id">
                <tr class="hover:bg-gray-50 dark:hover:bg-gray-700/30 transition-colors" :class="{ 'bg-purple-50 dark:bg-purple-900/20': isSelected(category.id) }">
                  <td class="px-4 py-3">
                    <input 
                      type="checkbox" 
                      :checked="isSelected(category.id)"
                      @change="toggleSelect(category.id)"
                      class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                    />
                  </td>
                  <td class="px-4 py-3">
                    <button 
                      v-if="getChildren(category.id).length > 0"
                      @click="toggleExpand(category.id)"
                      class="p-1 text-gray-600 dark:text-gray-400 hover:text-purple-600 dark:hover:text-purple-400 transition-colors"
                      title="Mở rộng/Thu gọn"
                    >
                      <i class="material-icons text-base">{{ isExpanded(category.id) ? 'expand_more' : 'chevron_right' }}</i>
                    </button>
                  </td>
                  <td class="px-4 py-3">
                    <div class="flex items-center gap-2">
                      <i class="material-icons text-gray-500 dark:text-gray-400 text-lg">folder</i>
                      <strong class="text-sm text-gray-900 dark:text-gray-100" v-html="highlightSearch(category.name)"></strong>
                      <span v-if="getChildren(category.id).length > 0" class="text-xs text-gray-500 dark:text-gray-400">
                        ({{ getChildren(category.id).length }})
                      </span>
                    </div>
                  </td>
                  <td class="px-4 py-3">
                    <code class="px-2 py-1 text-xs bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded font-mono" v-html="highlightSearch(category.slug)"></code>
                  </td>
                  <td class="px-4 py-3">
                    <span class="px-2 py-1 text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded">Danh mục gốc</span>
                  </td>
                  <td class="px-4 py-3 text-center">
                    <div class="flex items-center justify-center gap-2">
                      <button 
                        @click="openEditModal(category)" 
                        class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded transition-colors"
                        title="Chỉnh sửa"
                      >
                        <i class="material-icons text-base">edit</i>
                      </button>
                      <button 
                        @click="confirmDelete(category)" 
                        class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded transition-colors"
                        title="Xóa"
                      >
                        <i class="material-icons text-base">delete</i>
                      </button>
                    </div>
                  </td>
                </tr>
                <!-- Child categories - Collapsible -->
                <template v-if="isExpanded(category.id)">
                  <tr 
                    v-for="child in getChildren(category.id)" 
                    :key="child.id"
                    class="hover:bg-gray-50 dark:hover:bg-gray-700/30 transition-colors"
                    :class="{ 'bg-purple-50 dark:bg-purple-900/20': isSelected(child.id) }"
                  >
                    <td class="px-4 py-3">
                      <input 
                        type="checkbox" 
                        :checked="isSelected(child.id)"
                        @change="toggleSelect(child.id)"
                        class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                      />
                    </td>
                    <td class="px-4 py-3"></td>
                    <td class="px-4 py-3">
                      <div class="flex items-center gap-2 pl-6">
                        <i class="material-icons text-gray-500 dark:text-gray-400 text-lg">subdirectory_arrow_right</i>
                        <span class="text-sm text-gray-900 dark:text-gray-100" v-html="highlightSearch(child.name)"></span>
                      </div>
                    </td>
                    <td class="px-4 py-3">
                      <code class="px-2 py-1 text-xs bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded font-mono" v-html="highlightSearch(child.slug)"></code>
                    </td>
                    <td class="px-4 py-3 text-sm text-gray-600 dark:text-gray-400">{{ category.name }}</td>
                    <td class="px-4 py-3 text-center">
                      <div class="flex items-center justify-center gap-2">
                        <button 
                          @click="openEditModal(child)" 
                          class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded transition-colors"
                          title="Chỉnh sửa"
                        >
                          <i class="material-icons text-base">edit</i>
                        </button>
                        <button 
                          @click="confirmDelete(child)" 
                          class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded transition-colors"
                          title="Xóa"
                        >
                          <i class="material-icons text-base">delete</i>
                        </button>
                      </div>
                    </td>
                  </tr>
                </template>
              </template>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-lg w-full border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100">
            {{ isEditMode ? 'Chỉnh sửa danh mục' : 'Thêm danh mục mới' }}
          </h2>
          <button @click="closeModal" class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>

        <div class="p-4 space-y-4">
          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tên danh mục *</label>
            <input 
              v-model="formData.name"
              type="text" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="Ví dụ: Giày chạy bộ, Giày bóng rổ..."
              @input="generateSlug"
            />
            <span v-if="formErrors.name" class="text-xs text-red-600 dark:text-red-400">{{ formErrors.name }}</span>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Slug *</label>
            <input 
              v-model="formData.slug"
              type="text" 
              class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              placeholder="giay-chay-bo, giay-bong-ro..."
            />
            <span v-if="formErrors.slug" class="text-xs text-red-600 dark:text-red-400">{{ formErrors.slug }}</span>
            <span class="text-xs text-gray-500 dark:text-gray-400">URL thân thiện (tự động tạo từ tên)</span>
          </div>

          <div class="flex flex-col gap-2">
            <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Danh mục cha</label>
            <select v-model="formData.parentId" class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent">
              <option :value="null">-- Không có (danh mục gốc) --</option>
              <option 
                v-for="cat in rootCategories" 
                :key="cat.id" 
                :value="cat.id"
                :disabled="isEditMode && cat.id === formData.id"
              >
                {{ cat.name }}
              </option>
            </select>
            <span class="text-xs text-gray-500 dark:text-gray-400">Để trống nếu đây là danh mục gốc</span>
          </div>
        </div>

        <div class="flex items-center justify-end gap-3 p-4 border-t border-gray-200 dark:border-gray-700">
          <button @click="closeModal" class="px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
            Hủy
          </button>
          <button 
            @click="handleSubmit" 
            class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="submitting"
          >
            <i class="material-icons text-base" :class="{ 'animate-spin': submitting }">{{ submitting ? 'hourglass_empty' : 'save' }}</i>
            {{ submitting ? 'Đang lưu...' : (isEditMode ? 'Cập nhật' : 'Thêm mới') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Dialog -->
    <ConfirmDialog
      v-model="showDeleteModal"
      type="danger"
      title="Xác nhận xóa danh mục"
      :message="`Bạn có chắc chắn muốn xóa danh mục '${categoryToDelete?.name}'?`"
      :description="getDeleteWarning(categoryToDelete?.id)"
      confirm-text="Xóa danh mục"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="handleDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import notificationService from '@/utils/notificationService'
import ConfirmDialog from '@/assets/components/common/ConfirmDialog.vue'
import { downloadCsv, downloadJson } from '@/utils/exportHelpers'
import { highlightSearchSafely } from '@/utils/sanitize'
import logger from '@/utils/logger'

const adminStore = useAdminStore()

// State
const categories = ref([])
const loading = ref(false)
const showModal = ref(false)
const showDeleteModal = ref(false)
const isEditMode = ref(false)
const submitting = ref(false)
const deleting = ref(false)
const categoryToDelete = ref(null)
const expandedCategories = ref([]) // Track expanded parent categories
const selectedCategories = ref([]) // Track selected categories for bulk actions
const searchQuery = ref('') // Search query

const formData = ref({
  name: '',
  slug: '',
  parentId: null
})

const formErrors = ref({})

// Computed - Stats
const totalCategories = computed(() => categories.value.length)

const rootCategoriesCount = computed(() => {
  return categories.value.filter(cat => !cat.parentId).length
})

const childCategoriesCount = computed(() => {
  return categories.value.filter(cat => cat.parentId).length
})

// Computed
const rootCategories = computed(() => {
  return categories.value.filter(cat => !cat.parentId)
})

// Filtered categories based on search
const filteredCategories = computed(() => {
  if (!searchQuery.value.trim()) {
    return rootCategories.value
  }
  
  const query = searchQuery.value.toLowerCase()
  return rootCategories.value.filter(cat => {
    const matchesRoot = cat.name.toLowerCase().includes(query) || 
                        cat.slug.toLowerCase().includes(query)
    
    const hasMatchingChild = getChildren(cat.id).some(child =>
      child.name.toLowerCase().includes(query) || 
      child.slug.toLowerCase().includes(query)
    )
    
    return matchesRoot || hasMatchingChild
  })
})

// Selection helpers
const allSelected = computed(() => {
  if (categories.value.length === 0) return false
  return categories.value.every(cat => selectedCategories.value.includes(cat.id))
})

const isSelected = (categoryId) => {
  return selectedCategories.value.includes(categoryId)
}

const toggleSelect = (categoryId) => {
  const index = selectedCategories.value.indexOf(categoryId)
  if (index > -1) {
    selectedCategories.value.splice(index, 1)
  } else {
    selectedCategories.value.push(categoryId)
  }
}

const toggleSelectAll = () => {
  if (allSelected.value) {
    selectedCategories.value = []
  } else {
    selectedCategories.value = categories.value.map(cat => cat.id)
  }
}

// Expand/Collapse methods
const toggleExpand = (categoryId) => {
  const index = expandedCategories.value.indexOf(categoryId)
  if (index > -1) {
    expandedCategories.value.splice(index, 1)
  } else {
    expandedCategories.value.push(categoryId)
  }
}

const isExpanded = (categoryId) => {
  return expandedCategories.value.includes(categoryId)
}

// Methods
const getChildren = (parentId) => {
  return categories.value.filter(cat => cat.parentId === parentId)
}

const hasChildren = (categoryId) => {
  return categories.value.some(cat => cat.parentId === categoryId)
}

const getDeleteWarning = (categoryId) => {
  if (hasChildren(categoryId)) {
    return '⚠️ Danh mục này có danh mục con. Các danh mục con cũng sẽ bị xóa! Hành động này không thể hoàn tác!'
  }
  return 'Hành động này không thể hoàn tác!'
}

const fetchCategories = async () => {
  try {
    loading.value = true
    const result = await adminStore.fetchCategories()
    categories.value = result.content || result || []
  } catch (error) {
    logger.error('Lỗi khi tải danh sách danh mục:', error)
    notificationService.apiError(error, 'Không thể tải danh sách danh mục')
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  isEditMode.value = false
  formData.value = {
    name: '',
    slug: '',
    parentId: null
  }
  formErrors.value = {}
  showModal.value = true
}

const openEditModal = (category) => {
  isEditMode.value = true
  formData.value = {
    id: category.id,
    name: category.name,
    slug: category.slug,
    parentId: category.parentId || null
  }
  formErrors.value = {}
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  formData.value = {
    name: '',
    slug: '',
    parentId: null
  }
  formErrors.value = {}
}

const generateSlug = () => {
  // Tự động tạo slug từ tên (chỉ khi không phải edit mode)
  if (!isEditMode.value) {
    formData.value.slug = formData.value.name
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/đ/g, 'd')
      .replace(/[^a-z0-9\s-]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .trim()
  }
}

const validateForm = () => {
  formErrors.value = {}
  
  // Validate name
  if (!formData.value.name || formData.value.name.trim() === '') {
    formErrors.value.name = 'Tên danh mục không được để trống'
  } else if (formData.value.name.trim().length < 2) {
    formErrors.value.name = 'Tên danh mục phải có ít nhất 2 ký tự'
  } else if (formData.value.name.trim().length > 100) {
    formErrors.value.name = 'Tên danh mục không được vượt quá 100 ký tự'
  }
  
  // Validate slug
  if (!formData.value.slug || formData.value.slug.trim() === '') {
    formErrors.value.slug = 'Slug không được để trống'
  } else if (!/^[a-z0-9-]+$/.test(formData.value.slug)) {
    formErrors.value.slug = 'Slug chỉ được chứa chữ thường, số và dấu gạch ngang'
  } else if (formData.value.slug.length < 2) {
    formErrors.value.slug = 'Slug phải có ít nhất 2 ký tự'
  } else if (formData.value.slug.length > 100) {
    formErrors.value.slug = 'Slug không được vượt quá 100 ký tự'
  }
  
  return Object.keys(formErrors.value).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    notificationService.warning('Cảnh báo', 'Vui lòng kiểm tra lại thông tin form!', { duration: 3000 })
    return
  }

  try {
    submitting.value = true
    
    if (isEditMode.value) {
      await adminStore.updateCategory(formData.value.id, formData.value)
      notificationService.success('Thành công', `Đã cập nhật danh mục "${formData.value.name}" thành công!`, { duration: 3000 })
    } else {
      await adminStore.createCategory(formData.value)
      notificationService.success('Thành công', `Đã thêm danh mục "${formData.value.name}" thành công!`, { duration: 3000 })
    }
    
    await fetchCategories()
    closeModal()
  } catch (error) {
    logger.error('Lỗi khi lưu danh mục:', error)
    
    // Handle specific error messages from server
    let errorMessage = 'Có lỗi xảy ra! Vui lòng thử lại.'
    
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      
      if (status === 400) {
        if (data.validationErrors) {
          const firstError = Object.values(data.validationErrors)[0]
          if (firstError && firstError.length > 0) {
            errorMessage = firstError[0]
          }
        } else if (data.message) {
          errorMessage = data.message
        }
      } else if (status === 409) {
        errorMessage = 'Tên hoặc slug đã tồn tại. Vui lòng chọn tên khác.'
      } else if (status === 500) {
        errorMessage = 'Lỗi server. Vui lòng liên hệ quản trị viên.'
      }
    } else if (error.request) {
      errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng!'
    }
    
    notificationService.apiError(error, errorMessage || 'Không thể lưu danh mục')
  } finally {
    submitting.value = false
  }
}

const confirmDelete = (category) => {
  categoryToDelete.value = category
  showDeleteModal.value = true
}

const handleDelete = async () => {
  try {
    deleting.value = true
    await adminStore.deleteCategory(categoryToDelete.value.id)
    notificationService.success('Thành công',`Đã xóa danh mục "${categoryToDelete.value.name}" thành công!`)
    await fetchCategories()
    showDeleteModal.value = false
    categoryToDelete.value = null
  } catch (error) {
    logger.error('Lỗi khi xóa danh mục:', error)
    notificationService.apiError(error, 'Không thể xóa danh mục này')
  } finally {
    deleting.value = false
  }
}

// Export functions
const handleExport = (format) => {
  const data = categories.value.map(cat => ({
    'ID': cat.id,
    'Tên danh mục': cat.name,
    'Slug': cat.slug,
    'Danh mục cha': cat.parentId ? categories.value.find(c => c.id === cat.parentId)?.name || '' : 'Danh mục gốc',
    'Ngày tạo': cat.createdAt || '',
  }))
  
  if (format === 'csv') {
    downloadCsv(data, `categories_${Date.now()}.csv`)
    notificationService.success('Thành công','Đã xuất file CSV thành công!')
  } else if (format === 'json') {
    downloadJson(data, `categories_${Date.now()}.json`)
    notificationService.success('Thành công','Đã xuất file JSON thành công!')
  }
}

// Search highlighting (with XSS protection)
const highlightSearch = (text) => {
  if (!text) return ''
  if (!searchQuery.value || !searchQuery.value.trim()) {
    // Still escape the text even if no search query
    return highlightSearchSafely(text, '')
  }
  
  return highlightSearchSafely(text, searchQuery.value)
}

// Expand/Collapse all
const expandAll = () => {
  expandedCategories.value = rootCategories.value.map(cat => cat.id)
}

const collapseAll = () => {
  expandedCategories.value = []
}

// Bulk delete
const handleBulkDelete = async () => {
  if (selectedCategories.value.length === 0) return
  
  const confirmed = confirm(`Bạn có chắc chắn muốn xóa ${selectedCategories.value.length} danh mục đã chọn?`)
  if (!confirmed) return
  
  try {
    deleting.value = true
    
    // Delete all selected categories
    for (const categoryId of selectedCategories.value) {
      await adminStore.deleteCategory(categoryId)
    }
    
    notificationService.success('Thành công',`Đã xóa ${selectedCategories.value.length} danh mục thành công!`)
    selectedCategories.value = []
    await fetchCategories()
  } catch (error) {
    logger.error('Lỗi khi xóa danh mục:', error)
    notificationService.apiError(error, 'Có lỗi xảy ra khi xóa danh mục')
  } finally {
    deleting.value = false
  }
}

// Lifecycle
onMounted(() => {
  fetchCategories()
})
</script>




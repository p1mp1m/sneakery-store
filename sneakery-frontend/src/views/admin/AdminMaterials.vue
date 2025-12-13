<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- Page Header -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">layers</i>
            Quản lý Chất liệu
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Quản lý danh sách chất liệu sản phẩm</p>
        </div>
        <div class="flex items-center gap-2">
          <button class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm" @click="openCreateModal">
            <i class="material-icons text-base">add</i>
            Thêm Chất liệu
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ activeMaterialsCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Đang hoạt động</p>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">pause_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ inactiveMaterialsCount }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tạm ngưng</p>
        </div>
      </div>

      <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200">
        <div class="flex items-center justify-between mb-3">
          <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center">
            <i class="material-icons text-white text-lg">inventory</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">{{ materials.length }}</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tổng chất liệu</p>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">search</i>
            Tìm kiếm
          </label>
          <input
            v-model="searchKeyword"
            type="text"
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            placeholder="Tìm theo tên chất liệu hoặc slug..."
          />
        </div>

        <div class="flex flex-col gap-1">
          <label class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1">
            <i class="material-icons text-sm">category</i>
            Trạng thái
          </label>
          <select class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent" v-model="filterStatus">
            <option value="all">Tất cả</option>
            <option value="active">Đang hoạt động</option>
            <option value="inactive">Tạm ngưng</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="space-y-4" role="status" aria-live="polite">
        <LoadingSkeleton
          v-for="n in 5"
          :key="n"
          type="list"
        />
        <span class="sr-only">Đang tải dữ liệu</span>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredMaterials.length === 0" class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4">
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl">layers</i>
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">Không có chất liệu nào</h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 text-center mb-4">
        Bắt đầu thêm chất liệu đầu tiên cho cửa hàng của bạn
      </p>
      <button class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm" @click="openCreateModal">
        <i class="material-icons text-base">add</i>
        Thêm Chất liệu
      </button>
    </div>

    <!-- Materials Table -->
    <div v-else class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">ID</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Tên chất liệu</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Slug</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Trạng thái</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Ngày tạo</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 dark:text-gray-300 uppercase tracking-wider">Thao tác</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="material in paginatedMaterials" :key="material.id" class="hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors">
              <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100">{{ material.id }}</td>
              <td class="px-4 py-4">
                <strong class="text-sm font-medium text-gray-900 dark:text-gray-100 block">{{ material.name }}</strong>
                <p v-if="material.description" class="text-xs text-gray-500 dark:text-gray-400 mt-1">
                  {{ truncateText(material.description, 50) }}
                </p>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <code class="px-2 py-1 bg-gray-100 dark:bg-gray-700 rounded text-xs font-mono text-gray-900 dark:text-gray-100">{{ material.slug }}</code>
              </td>
              <td class="px-4 py-4 whitespace-nowrap">
                <span
                  class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full"
                  :class="material.isActive ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400'"
                >
                  <i class="material-icons text-sm">{{ material.isActive ? "check_circle" : "cancel" }}</i>
                  {{ material.isActive ? "Hoạt động" : "Tạm ngưng" }}
                </span>
              </td>
              <td class="px-4 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-400">{{ formatDate(material.createdAt) }}</td>
              <td class="px-4 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2">
                  <button
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-colors"
                    @click="openEditModal(material)"
                    title="Chỉnh sửa"
                  >
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button
                    class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors"
                    @click="confirmDelete(material)"
                    title="Xóa"
                  >
                    <i class="material-icons text-base">delete</i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex items-center justify-between gap-4 px-4 py-3 mt-4 border-t border-gray-200 dark:border-gray-700">
        <div class="text-sm text-gray-600 dark:text-gray-400">
          Trang {{ currentPage }} / {{ totalPages }}
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="currentPage === 1"
            @click="currentPage--"
          >
            <i class="material-icons text-base">chevron_left</i>
            Trước
          </button>
          <button
            class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="currentPage === totalPages"
            @click="currentPage++"
          >
            Sau
            <i class="material-icons text-base">chevron_right</i>
          </button>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm" @click="closeModal">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700" @click.stop>
        <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
            <i class="material-icons text-purple-600 dark:text-purple-400">{{
              isEditMode ? "edit" : "add"
            }}</i>
            {{ isEditMode ? "Chỉnh sửa Chất liệu" : "Thêm Chất liệu mới" }}
          </h2>
          <button class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors" @click="closeModal">
            <i class="material-icons text-xl">close</i>
          </button>
        </div>

        <div class="p-4">
          <form @submit.prevent="saveMaterial" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Tên chất liệu *</label>
                <input
                  v-model="formData.name"
                  type="text"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="VD: Da tổng hợp, Vải canvas..."
                  @input="generateSlug"
                  required
                />
              </div>

              <div class="flex flex-col gap-2">
                <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Slug *</label>
                <input
                  v-model="formData.slug"
                  type="text"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="VD: da-tong-hop, vai-canvas..."
                  required
                />
              </div>
            </div>

            <div class="flex flex-col gap-2">
              <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Mô tả</label>
              <textarea
                v-model="formData.description"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
                placeholder="Nhập mô tả về chất liệu..."
                rows="3"
              ></textarea>
            </div>

            <div class="flex items-center gap-2">
              <input
                type="checkbox"
                id="isActive"
                v-model="formData.isActive"
                class="w-4 h-4 text-purple-600 bg-gray-100 border-gray-300 rounded focus:ring-purple-500 dark:focus:ring-purple-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
              />
              <label for="isActive" class="text-sm text-gray-700 dark:text-gray-300 cursor-pointer">
                Kích hoạt chất liệu
              </label>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
              <button type="button" class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors" @click="closeModal">
                <i class="material-icons text-base">close</i>
                Hủy
              </button>
              <button
                type="submit"
                class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="saving"
              >
                <i class="material-icons text-base" :class="{ 'animate-spin': saving }">{{
                  saving ? "hourglass_empty" : "save"
                }}</i>
                {{ saving ? "Đang lưu..." : "Lưu" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <ConfirmDialog
      v-model="showDeleteModal"
      type="danger"
      title="Xác nhận xóa chất liệu"
      :message="`Bạn có chắc chắn muốn xóa chất liệu '${materialToDelete?.name}'?`"
      description="Hành động này không thể hoàn tác."
      confirm-text="Xóa chất liệu"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="deleteMaterial"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import notificationService from '@/utils/notificationService';
import { useAdminStore } from "@/stores/admin";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";
import logger from "@/utils/logger";
import LoadingSkeleton from "@/components/common/LoadingSkeleton.vue";
import { formatDate } from "@/utils/formatters";

const adminStore = useAdminStore();

// State
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const materials = ref([]);
const searchKeyword = ref("");
const filterStatus = ref("all");
const showModal = ref(false);
const showDeleteModal = ref(false);
const isEditMode = ref(false);
const materialToDelete = ref(null);
const currentPage = ref(1);
const itemsPerPage = 10;

// Form data
const formData = ref({
  id: null,
  name: "",
  slug: "",
  description: "",
  isActive: true,
});

// Computed
const filteredMaterials = computed(() => {
  let result = materials.value;
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(
      (m) =>
        m.name.toLowerCase().includes(keyword) ||
        m.slug.toLowerCase().includes(keyword)
    );
  }
  if (filterStatus.value !== "all") {
    const isActive = filterStatus.value === "active";
    result = result.filter((m) => m.isActive === isActive);
  }
  return result;
});

const paginatedMaterials = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredMaterials.value.slice(start, end);
});

const totalPages = computed(() =>
  Math.ceil(filteredMaterials.value.length / itemsPerPage)
);

const activeMaterialsCount = computed(
  () => materials.value.filter((b) => b.isActive).length
);
const inactiveMaterialsCount = computed(
  () => materials.value.filter((b) => !b.isActive).length
);

// Methods
const fetchMaterials = async () => {
  loading.value = true;
  try {
    const result = await adminStore.fetchMaterials();
    materials.value = result.content || result || [];
  } catch (error) {
    logger.error("Error fetching materials:", error);
    notificationService.apiError(error, "Lỗi khi tải danh sách chất liệu")
  } finally {
    loading.value = false;
  }
};

const openCreateModal = () => {
  isEditMode.value = false;
  formData.value = {
    id: null,
    name: "",
    slug: "",
    description: "",
    isActive: true,
  };
  showModal.value = true;
};

const openEditModal = (material) => {
  isEditMode.value = true;
  formData.value = { ...material };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  formData.value = {
    id: null,
    name: "",
    slug: "",
    description: "",
    isActive: true,
  };
};

const generateSlug = () => {
  formData.value.slug = formData.value.name
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/đ/g, "d")
    .replace(/[^a-z0-9\s-]/g, "")
    .replace(/\s+/g, "-")
    .replace(/-+/g, "-")
    .trim();
};

const saveMaterial = async () => {
  saving.value = true;
  try {
    if (isEditMode.value) {
      await adminStore.updateMaterial(formData.value.id, formData.value);
    } else {
      await adminStore.createMaterial(formData.value);
    }
    await fetchMaterials();
    closeModal();
    notificationService.success('Thành công', `${isEditMode.value ? "Cập nhật" : "Thêm"} chất liệu thành công!`, { duration: 3000 });
  } catch (error) {
    logger.error("Error saving material:", error);
    notificationService.apiError(error, "Lỗi khi lưu chất liệu")
  } finally {
    saving.value = false;
  }
};

const confirmDelete = (material) => {
  materialToDelete.value = material;
  showDeleteModal.value = true;
};

const deleteMaterial = async () => {
  deleting.value = true;
  try {
    await adminStore.deleteMaterial(materialToDelete.value.id);
    await fetchMaterials();
    showDeleteModal.value = false;
    materialToDelete.value = null;
    notificationService.success('Thành công', "Xóa chất liệu thành công!", { duration: 3000 });
  } catch (error) {
    logger.error("Error deleting material:", error);
    notificationService.apiError(error, "Lỗi khi xóa chất liệu")
  } finally {
    deleting.value = false;
  }
};

const resetFilters = () => {
  searchKeyword.value = "";
  filterStatus.value = "all";
  currentPage.value = 1;
};

// formatDate đã được import từ @/utils/formatters

const truncateText = (text, maxLength) => {
  if (!text) return "";
  return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
};

// Lifecycle
onMounted(() => {
  fetchMaterials();
});
</script>




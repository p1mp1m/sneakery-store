<template>
  <div class="max-w-[1600px] mx-auto w-full p-4 space-y-4">
    <!-- ===== PAGE HEADER ===== -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
      >
        <div>
          <h1
            class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
          >
            <i class="material-icons text-purple-600 dark:text-purple-400"
              >straighten</i
            >
            Quản lý Kích thước
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
            Quản lý danh sách kích thước giày
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
            @click="openCreateModal"
          >
            <i class="material-icons text-base">add</i>
            Thêm Size
          </button>
        </div>
      </div>
    </div>

    <!-- ===== STATS GRID ===== -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-green-500 to-green-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">check_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ activeSizesCount }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">
            Đang hoạt động
          </p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-yellow-500 to-yellow-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">pause_circle</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ inactiveSizesCount }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">
            Tạm ngưng
          </p>
        </div>
      </div>

      <div
        class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-all duration-200"
      >
        <div class="flex items-center justify-between mb-3">
          <div
            class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center"
          >
            <i class="material-icons text-white text-lg">straighten</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ sizes.length }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">
            Tổng kích thước
          </p>
        </div>
      </div>
    </div>

    <!-- ===== FILTERS BAR ===== -->
    <div
      class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div class="grid grid-cols-1 md:grid-cols-[2fr_1fr] gap-4">
        <!-- Ô tìm kiếm -->
        <div class="flex flex-col gap-2">
          <label
            class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1 uppercase"
          >
            <i class="material-icons text-sm">search</i>
            Tìm kiếm
          </label>
          <input
            v-model="searchKeyword"
            type="text"
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            placeholder="Tìm theo tên size..."
          />
        </div>

        <!-- Ô trạng thái -->
        <div class="flex flex-col gap-2">
          <label
            class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1 uppercase"
          >
            <i class="material-icons text-sm">filter_list</i>
            Trạng thái
          </label>
          <select
            class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            v-model="filterStatus"
          >
            <option value="all">Tất cả</option>
            <option value="active">Đang hoạt động</option>
            <option value="inactive">Tạm ngưng</option>
          </select>
        </div>
      </div>
    </div>

    <!-- ===== LOADING STATE ===== -->
    <div
      v-if="loading"
      class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="w-8 h-8 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mb-4"
      ></div>
      <p class="text-sm text-gray-600 dark:text-gray-400">
        Đang tải dữ liệu...
      </p>
    </div>

    <!-- ===== EMPTY STATE ===== -->
    <div
      v-else-if="filteredSizes.length === 0"
      class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4"
      >
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl"
          >straighten</i
        >
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
        Không tìm thấy kích thước nào
      </h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
        {{
          searchKeyword || filterStatus !== "all"
            ? "Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm"
            : "Bắt đầu thêm kích thước đầu tiên"
        }}
      </p>
      <button
        v-if="!searchKeyword && filterStatus === 'all'"
        @click="openCreateModal"
        class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
      >
        <i class="material-icons text-base">add</i>
        Thêm Size
      </button>
    </div>

    <!-- Table -->
    <div
      v-else
      class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden"
    >
      <table class="w-full">
        <thead class="bg-gray-50 dark:bg-gray-700">
          <tr>
            <th
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              ID
            </th>
            <th
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Tên Size
            </th>
            <th
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Thứ tự
            </th>
            <th
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Trạng thái
            </th>
            <th
              class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
          <tr
            v-for="size in paginatedSizes"
            :key="size.id"
            class="hover:bg-gray-50 dark:hover:bg-gray-700/50"
          >
            <td
              class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100"
            >
              {{ size.id }}
            </td>
            <td
              class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-gray-100"
            >
              {{ size.name }}
            </td>
            <td
              class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-400"
            >
              {{ size.displayOrder }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="[
                  'px-2 py-1 text-xs font-medium rounded-full',
                  size.isActive
                    ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
                    : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
                ]"
              >
                {{ size.isActive ? "Hoạt động" : "Tắt" }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm">
              <button
                @click="openEditModal(size)"
                class="text-purple-600 hover:text-purple-800 dark:text-purple-400 dark:hover:text-purple-300 mr-3"
              >
                <i class="material-icons text-base">edit</i>
              </button>
              <button
                @click="toggleActive(size)"
                :class="
                  size.isActive
                    ? 'text-orange-600 hover:text-orange-800'
                    : 'text-green-600 hover:text-green-800'
                "
              >
                <i class="material-icons text-base">{{
                  size.isActive ? "visibility_off" : "visibility"
                }}</i>
              </button>
              <button
                @click="confirmDelete(size)"
                class="text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300 ml-3"
              >
                <i class="material-icons text-base">delete</i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- ===== PAGINATION ===== -->
      <div
        v-if="totalPages > 1"
        class="px-6 py-4 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between"
      >
        <div class="text-sm text-gray-500 dark:text-gray-400">
          Hiển thị {{ (currentPage - 1) * itemsPerPage + 1 }} -
          {{ Math.min(currentPage * itemsPerPage, filteredSizes.length) }} /
          {{ filteredSizes.length }} kích thước
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="currentPage = 1"
            :disabled="currentPage === 1"
            class="p-2 rounded-lg text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="material-icons text-base">first_page</i>
          </button>
          <button
            @click="currentPage--"
            :disabled="currentPage === 1"
            class="p-2 rounded-lg text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="material-icons text-base">chevron_left</i>
          </button>
          <span class="px-3 py-1 text-sm text-gray-700 dark:text-gray-300">
            Trang {{ currentPage }} / {{ totalPages }}
          </span>
          <button
            @click="currentPage++"
            :disabled="currentPage === totalPages"
            class="p-2 rounded-lg text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="material-icons text-base">chevron_right</i>
          </button>
          <button
            @click="currentPage = totalPages"
            :disabled="currentPage === totalPages"
            class="p-2 rounded-lg text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <i class="material-icons text-base">last_page</i>
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Create/Edit -->
    <div
      v-if="showModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm"
      @click.self="closeModal"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-md p-6"
      >
        <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-4">
          {{ isEdit ? "Chỉnh sửa Size" : "Thêm Size mới" }}
        </h3>
        <form @submit.prevent="handleSubmit">
          <div class="space-y-4">
            <div>
              <label
                class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
              >
                Tên Size <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.name"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Ví dụ: 42"
                required
              />
            </div>
            <div>
              <label
                class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
              >
                Thứ tự hiển thị
              </label>
              <input
                v-model.number="formData.displayOrder"
                type="number"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="0"
              />
            </div>
            <div class="flex items-center gap-2">
              <input
                v-model="formData.isActive"
                type="checkbox"
                id="isActive"
                class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
              />
              <label
                for="isActive"
                class="text-sm text-gray-700 dark:text-gray-300"
              >
                Kích hoạt
              </label>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-6">
            <button
              type="button"
              @click="closeModal"
              class="px-4 py-2 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
            >
              Hủy
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors disabled:opacity-50"
            >
              {{
                isSubmitting ? "Đang xử lý..." : isEdit ? "Cập nhật" : "Tạo mới"
              }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm"
      @click.self="showDeleteModal = false"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-md p-6"
      >
        <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100 mb-2">
          Xác nhận xóa
        </h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">
          Bạn có chắc muốn xóa size "{{ sizeToDelete?.name }}"? Hành động này
          không thể hoàn tác.
        </p>
        <div class="flex justify-end gap-3">
          <button
            @click="showDeleteModal = false"
            class="px-4 py-2 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
          >
            Hủy
          </button>
          <button
            @click="deleteSize"
            :disabled="isDeleting"
            class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
          >
            {{ isDeleting ? "Đang xóa..." : "Xóa" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import adminService from "@/services/adminService";
import notificationService from "@/utils/notificationService";

// State
const sizes = ref([]);
const loading = ref(false);
const showModal = ref(false);
const showDeleteModal = ref(false);
const isEdit = ref(false);
const isSubmitting = ref(false);
const isDeleting = ref(false);
const editingId = ref(null);
const sizeToDelete = ref(null);

// Search & Filter
const searchKeyword = ref("");
const filterStatus = ref("all");
const currentPage = ref(1);
const itemsPerPage = 10;

const formData = ref({
  name: "",
  displayOrder: 0,
  isActive: true,
});

// Computed
const filteredSizes = computed(() => {
  let result = sizes.value;

  // Filter by search
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter((size) => size.name.toLowerCase().includes(keyword));
  }

  // Filter by status
  if (filterStatus.value !== "all") {
    const isActive = filterStatus.value === "active";
    result = result.filter((size) => size.isActive === isActive);
  }

  return result;
});

const paginatedSizes = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredSizes.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(filteredSizes.value.length / itemsPerPage) || 1;
});

const activeSizesCount = computed(() => {
  return sizes.value.filter((s) => s.isActive).length;
});

const inactiveSizesCount = computed(() => {
  return sizes.value.filter((s) => !s.isActive).length;
});

// Reset page when filter changes
watch([searchKeyword, filterStatus], () => {
  currentPage.value = 1;
});

const loadSizes = async () => {
  loading.value = true;
  try {
    sizes.value = await adminService.getAllSizes();
  } catch (err) {
    notificationService.error("Lỗi", "Không thể tải danh sách size");
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const openCreateModal = () => {
  isEdit.value = false;
  editingId.value = null;
  formData.value = { name: "", displayOrder: 0, isActive: true };
  showModal.value = true;
};

const openEditModal = (size) => {
  isEdit.value = true;
  editingId.value = size.id;
  formData.value = {
    name: size.name,
    displayOrder: size.displayOrder || 0,
    isActive: size.isActive,
  };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  formData.value = { name: "", displayOrder: 0, isActive: true };
};

const handleSubmit = async () => {
  if (!formData.value.name.trim()) {
    notificationService.warning("Cảnh báo", "Vui lòng nhập tên size");
    return;
  }

  isSubmitting.value = true;
  try {
    if (isEdit.value) {
      await adminService.updateSize(editingId.value, formData.value);
      notificationService.success("Thành công", "Đã cập nhật size");
    } else {
      await adminService.createSize(formData.value);
      notificationService.success("Thành công", "Đã tạo size mới");
    }
    closeModal();
    await loadSizes();
  } catch (err) {
    notificationService.error("Lỗi", err.message || "Không thể lưu size");
  } finally {
    isSubmitting.value = false;
  }
};

const toggleActive = async (size) => {
  try {
    await adminService.toggleSizeActive(size.id);
    notificationService.success(
      "Thành công",
      `Đã ${size.isActive ? "tắt" : "bật"} size ${size.name}`
    );
    await loadSizes();
  } catch (err) {
    notificationService.error(
      "Lỗi",
      err.message || "Không thể thay đổi trạng thái"
    );
  }
};

const confirmDelete = (size) => {
  sizeToDelete.value = size;
  showDeleteModal.value = true;
};

const deleteSize = async () => {
  if (!sizeToDelete.value) return;

  isDeleting.value = true;
  try {
    await adminService.deleteSize(sizeToDelete.value.id);
    notificationService.success("Thành công", "Đã xóa size");
    showDeleteModal.value = false;
    sizeToDelete.value = null;
    await loadSizes();
  } catch (err) {
    notificationService.error("Lỗi", err.message || "Không thể xóa size");
  } finally {
    isDeleting.value = false;
  }
};

onMounted(() => {
  loadSizes();
});
</script>

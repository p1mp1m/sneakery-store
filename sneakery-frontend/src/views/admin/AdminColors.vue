<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100">
          Quản lý Màu sắc
        </h1>
        <p class="text-gray-600 dark:text-gray-400 mt-1">
          Quản lý danh sách màu sắc sản phẩm
        </p>
      </div>
      <button
        @click="openCreateModal"
        class="flex items-center gap-2 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors"
      >
        <i class="material-icons text-base">add</i>
        Thêm Màu
      </button>
    </div>

    <!-- Table -->
    <div
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
              Màu
            </th>
            <th
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Tên Màu
            </th>
            <th
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider"
            >
              Mã Hex
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
            v-for="color in colors"
            :key="color.id"
            class="hover:bg-gray-50 dark:hover:bg-gray-700/50"
          >
            <td
              class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-100"
            >
              {{ color.id }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div
                class="w-8 h-8 rounded-full border-2 border-gray-300 dark:border-gray-600"
                :style="{ backgroundColor: color.hexCode || '#808080' }"
              ></div>
            </td>
            <td
              class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-gray-100"
            >
              {{ color.name }}
            </td>
            <td
              class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-400 font-mono"
            >
              {{ color.hexCode || "-" }}
            </td>
            <td
              class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-400"
            >
              {{ color.displayOrder }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="[
                  'px-2 py-1 text-xs font-medium rounded-full',
                  color.isActive
                    ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
                    : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
                ]"
              >
                {{ color.isActive ? "Hoạt động" : "Tắt" }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm">
              <button
                @click="openEditModal(color)"
                class="text-purple-600 hover:text-purple-800 dark:text-purple-400 dark:hover:text-purple-300 mr-3"
              >
                <i class="material-icons text-base">edit</i>
              </button>
              <button
                @click="toggleActive(color)"
                :class="
                  color.isActive
                    ? 'text-orange-600 hover:text-orange-800'
                    : 'text-green-600 hover:text-green-800'
                "
              >
                <i class="material-icons text-base">{{
                  color.isActive ? "visibility_off" : "visibility"
                }}</i>
              </button>
              <button
                @click="confirmDelete(color)"
                class="text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300 ml-3"
              >
                <i class="material-icons text-base">delete</i>
              </button>
            </td>
          </tr>
          <tr v-if="colors.length === 0">
            <td
              colspan="7"
              class="px-6 py-12 text-center text-gray-500 dark:text-gray-400"
            >
              Chưa có màu nào
            </td>
          </tr>
        </tbody>
      </table>
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
          {{ isEdit ? "Chỉnh sửa Màu" : "Thêm Màu mới" }}
        </h3>
        <form @submit.prevent="handleSubmit">
          <div class="space-y-4">
            <div>
              <label
                class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
              >
                Tên Màu <span class="text-red-500">*</span>
              </label>
              <input
                v-model="formData.name"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Ví dụ: Đen"
                required
              />
            </div>
            <div>
              <label
                class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
              >
                Mã màu Hex
              </label>
              <div class="flex gap-2">
                <input
                  v-model="formData.hexCode"
                  type="text"
                  class="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-purple-500 focus:border-transparent font-mono"
                  placeholder="#000000"
                  pattern="^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
                />
                <input
                  v-model="formData.hexCode"
                  type="color"
                  class="w-12 h-10 rounded-lg border border-gray-300 dark:border-gray-600 cursor-pointer"
                />
              </div>
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
          Bạn có chắc muốn xóa màu "{{ colorToDelete?.name }}"? Hành động này
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
            @click="deleteColor"
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
import { ref, onMounted } from "vue";
import adminService from "@/services/adminService";
import notificationService from "@/utils/notificationService";

const colors = ref([]);
const showModal = ref(false);
const showDeleteModal = ref(false);
const isEdit = ref(false);
const isSubmitting = ref(false);
const isDeleting = ref(false);
const editingId = ref(null);
const colorToDelete = ref(null);

const formData = ref({
  name: "",
  hexCode: "#000000",
  displayOrder: 0,
  isActive: true,
});

const loadColors = async () => {
  try {
    colors.value = await adminService.getAllColors();
  } catch (err) {
    notificationService.error("Lỗi", "Không thể tải danh sách màu");
    console.error(err);
  }
};

const openCreateModal = () => {
  isEdit.value = false;
  editingId.value = null;
  formData.value = {
    name: "",
    hexCode: "#000000",
    displayOrder: 0,
    isActive: true,
  };
  showModal.value = true;
};

const openEditModal = (color) => {
  isEdit.value = true;
  editingId.value = color.id;
  formData.value = {
    name: color.name,
    hexCode: color.hexCode || "#000000",
    displayOrder: color.displayOrder || 0,
    isActive: color.isActive,
  };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  formData.value = {
    name: "",
    hexCode: "#000000",
    displayOrder: 0,
    isActive: true,
  };
};

const handleSubmit = async () => {
  if (!formData.value.name.trim()) {
    notificationService.warning("Cảnh báo", "Vui lòng nhập tên màu");
    return;
  }

  isSubmitting.value = true;
  try {
    if (isEdit.value) {
      await adminService.updateColor(editingId.value, formData.value);
      notificationService.success("Thành công", "Đã cập nhật màu");
    } else {
      await adminService.createColor(formData.value);
      notificationService.success("Thành công", "Đã tạo màu mới");
    }
    closeModal();
    await loadColors();
  } catch (err) {
    notificationService.error("Lỗi", err.message || "Không thể lưu màu");
  } finally {
    isSubmitting.value = false;
  }
};

const toggleActive = async (color) => {
  try {
    await adminService.toggleColorActive(color.id);
    notificationService.success(
      "Thành công",
      `Đã ${color.isActive ? "tắt" : "bật"} màu ${color.name}`
    );
    await loadColors();
  } catch (err) {
    notificationService.error(
      "Lỗi",
      err.message || "Không thể thay đổi trạng thái"
    );
  }
};

const confirmDelete = (color) => {
  colorToDelete.value = color;
  showDeleteModal.value = true;
};

const deleteColor = async () => {
  if (!colorToDelete.value) return;

  isDeleting.value = true;
  try {
    await adminService.deleteColor(colorToDelete.value.id);
    notificationService.success("Thành công", "Đã xóa màu");
    showDeleteModal.value = false;
    colorToDelete.value = null;
    await loadColors();
  } catch (err) {
    notificationService.error("Lỗi", err.message || "Không thể xóa màu");
  } finally {
    isDeleting.value = false;
  }
};

onMounted(() => {
  loadColors();
});
</script>

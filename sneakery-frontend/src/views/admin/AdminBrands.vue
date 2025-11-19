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
              >local_offer</i
            >
            Quản lý Thương hiệu
          </h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
            Quản lý danh sách thương hiệu sản phẩm
          </p>
        </div>
        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
            @click="openCreateModal"
          >
            <i class="material-icons text-base">add</i>
            Thêm Thương hiệu
          </button>
        </div>
      </div>
    </div>

    <!-- ===== STATS GRID ===== -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
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
            {{ activeBrandsCount }}
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
            {{ inactiveBrandsCount }}
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
            <i class="material-icons text-white text-lg">inventory</i>
          </div>
        </div>
        <div>
          <h3 class="text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
            {{ brands.length }}
          </h3>
          <p class="text-xs text-gray-500 dark:text-gray-400 uppercase">
            Tổng thương hiệu
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
            placeholder="Tìm theo tên thương hiệu hoặc slug..."
          />
        </div>

        <!-- Ô trạng thái -->
        <div class="flex flex-col gap-2">
          <label
            class="text-xs font-medium text-gray-700 dark:text-gray-300 flex items-center gap-1 uppercase"
          >
            <i class="material-icons text-sm">category</i>
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
      v-else-if="filteredBrands.length === 0"
      class="flex flex-col items-center justify-center p-12 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div
        class="w-16 h-16 rounded-full bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center mb-4"
      >
        <i class="material-icons text-purple-600 dark:text-purple-400 text-3xl"
          >local_offer</i
        >
      </div>
      <h3 class="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
        Không có thương hiệu nào
      </h3>
      <p class="text-sm text-gray-500 dark:text-gray-400 mb-4 text-center">
        Bắt đầu thêm thương hiệu đầu tiên cho cửa hàng của bạn
      </p>
      <button
        class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium"
        @click="openCreateModal"
      >
        <i class="material-icons text-base">add</i>
        Thêm Thương hiệu
      </button>
    </div>

    <!-- ===== BRANDS TABLE ===== -->
    <div
      v-if="!loading && filteredBrands.length > 0"
      class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden"
    >
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead
            class="bg-gray-50 dark:bg-gray-700/50 border-b border-gray-200 dark:border-gray-600"
          >
            <tr>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-[6%]"
              >
                ID
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-[12%]"
              >
                Logo
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-[20%]"
              >
                Tên thương hiệu
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-[14%]"
              >
                Slug
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-[16%]"
              >
                Website
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-[18%]"
              >
                Trạng thái
              </th>
              <th
                class="px-4 py-3 text-left text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-[14%]"
              >
                Ngày tạo
              </th>
              <th
                class="px-4 py-3 text-center text-xs font-semibold text-gray-600 dark:text-gray-300 uppercase tracking-wider w-[10%]"
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr
              v-for="brand in paginatedBrands"
              :key="brand.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-700/30 transition-colors"
            >
              <td
                class="px-4 py-3 text-sm text-gray-900 dark:text-gray-100 text-center"
              >
                {{ brand.id }}
              </td>

              <!-- Logo -->
              <td class="px-4 py-3">
                <div
                  class="w-12 h-12 rounded-lg bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden"
                >
                  <img
                    v-if="brand.logoUrl"
                    :src="brand.logoUrl"
                    :alt="brand.name"
                    @error="onLogoError($event)"
                    class="w-full h-full object-contain"
                  />
                  <i
                    v-else
                    class="material-icons text-gray-400 dark:text-gray-500"
                    >local_offer</i
                  >
                </div>
              </td>

              <!-- Tên + mô tả -->
              <td class="px-4 py-3">
                <div
                  class="font-semibold text-sm text-gray-900 dark:text-gray-100"
                >
                  {{ brand.name }}
                </div>
                <p
                  v-if="brand.description"
                  class="text-xs text-gray-500 dark:text-gray-400 mt-1"
                >
                  {{ truncateText(brand.description, 50) }}
                </p>
              </td>

              <!-- Slug -->
              <td class="px-4 py-3">
                <code
                  class="px-2 py-1 text-xs bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200 rounded font-mono"
                  >{{ brand.slug }}</code
                >
              </td>

              <!-- Website -->
              <td class="px-4 py-3">
                <a
                  v-if="brand.websiteUrl"
                  :href="brand.websiteUrl"
                  target="_blank"
                  class="flex items-center gap-1 text-xs text-purple-600 dark:text-purple-400 hover:text-purple-700 dark:hover:text-purple-300 transition-colors"
                >
                  <i class="material-icons text-sm">link</i>
                  {{ truncateText(brand.websiteUrl, 25) }}
                </a>
                <span v-else class="text-xs text-gray-400 dark:text-gray-500"
                  >—</span
                >
              </td>

              <!-- Trạng thái -->
              <td class="px-4 py-3">
                <span
                  class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full"
                  :class="
                    brand.isActive
                      ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
                      : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
                  "
                >
                  <i class="material-icons text-sm">{{
                    brand.isActive ? "check_circle" : "cancel"
                  }}</i>
                  {{ brand.isActive ? "Hoạt động" : "Tạm ngưng" }}
                </span>
              </td>

              <!-- Ngày tạo -->
              <td class="px-4 py-3 text-sm text-gray-600 dark:text-gray-400">
                {{ formatDate(brand.createdAt) }}
              </td>

              <!-- Thao tác -->
              <td class="px-4 py-3 text-center">
                <div class="flex items-center justify-center gap-2">
                  <button
                    class="p-1.5 text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded transition-colors"
                    @click="openEditModal(brand)"
                    title="Chỉnh sửa"
                  >
                    <i class="material-icons text-base">edit</i>
                  </button>
                  <button
                    class="p-1.5 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded transition-colors"
                    @click="confirmDelete(brand)"
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
    </div>

    <!-- ===== PAGINATION ===== -->
    <div
      v-if="totalPages > 1"
      class="flex items-center justify-between px-4 py-3 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700"
    >
      <div class="flex items-center gap-2">
        <button
          class="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="currentPage === 1"
          @click="currentPage--"
        >
          <i class="material-icons text-base">chevron_left</i>
          Trước
        </button>

        <span class="px-3 py-1.5 text-sm text-gray-700 dark:text-gray-300"
          >Trang {{ currentPage }} / {{ totalPages }}</span
        >

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

    <!-- ===== CREATE/EDIT MODAL ===== -->
    <div
      v-if="showModal"
      class="fixed inset-0 z-[9999] flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
      @click="closeModal"
    >
      <div
        class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-200 dark:border-gray-700"
        @click.stop
      >
        <!-- Header -->
        <div
          class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700"
        >
          <h2
            class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2"
          >
            <i class="material-icons text-purple-600 dark:text-purple-400">{{
              isEditMode ? "edit" : "add"
            }}</i>
            {{ isEditMode ? "Chỉnh sửa Thương hiệu" : "Thêm Thương hiệu mới" }}
          </h2>
          <button
            class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
            @click="closeModal"
          >
            <i class="material-icons text-xl">close</i>
          </button>
        </div>

        <!-- Body -->
        <div class="p-4">
          <form @submit.prevent="saveBrand" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Tên thương hiệu -->
              <div class="flex flex-col gap-2">
                <label
                  class="text-xs font-medium text-gray-700 dark:text-gray-300 uppercase"
                  >Tên thương hiệu *</label
                >
                <input
                  v-model="formData.name"
                  type="text"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="VD: Nike, Adidas..."
                  @input="generateSlug"
                  required
                />
              </div>

              <!-- Slug -->
              <div class="flex flex-col gap-2">
                <label
                  class="text-xs font-medium text-gray-700 dark:text-gray-300 uppercase"
                  >Slug *</label
                >
                <input
                  v-model="formData.slug"
                  type="text"
                  class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="vd: nike, adidas..."
                  required
                />
              </div>
            </div>

            <!-- Logo -->
            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300 uppercase"
                >URL Logo</label
              >
              <input
                v-model="formData.logoUrl"
                type="url"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="https://example.com/logo.png"
              />
            </div>

            <!-- Website -->
            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300 uppercase"
                >Website</label
              >
              <input
                v-model="formData.websiteUrl"
                type="url"
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="https://example.com"
              />
            </div>

            <!-- Mô tả -->
            <div class="flex flex-col gap-2">
              <label
                class="text-xs font-medium text-gray-700 dark:text-gray-300 uppercase"
                >Mô tả</label
              >
              <textarea
                class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
                v-model="formData.description"
                placeholder="Nhập mô tả về thương hiệu..."
                rows="3"
              ></textarea>
            </div>

            <!-- Kích hoạt -->
            <div class="flex items-center gap-2">
              <input
                type="checkbox"
                id="isActive"
                v-model="formData.isActive"
                class="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
              />
              <label
                for="isActive"
                class="text-sm text-gray-700 dark:text-gray-300"
              >
                Kích hoạt thương hiệu
              </label>
            </div>

            <!-- Nút hành động -->
            <div
              class="flex items-center justify-end gap-3 pt-4 border-t border-gray-200 dark:border-gray-700"
            >
              <button
                type="button"
                class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
                @click="closeModal"
              >
                <i class="material-icons text-base">close</i>
                Hủy
              </button>
              <button
                type="submit"
                class="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-purple-500 to-purple-600 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="saving"
              >
                <i
                  class="material-icons text-base"
                  :class="{ 'animate-spin': saving }"
                  >{{ saving ? "hourglass_empty" : "save" }}</i
                >
                {{ saving ? "Đang lưu..." : "Lưu" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- ===== DELETE CONFIRMATION MODAL ===== -->
    <!-- 🔹 Delete Confirmation Dialog -->
    <ConfirmDialog
      v-model="showDeleteModal"
      type="danger"
      title="Xác nhận xóa thương hiệu"
      :message="`Bạn có chắc chắn muốn xóa thương hiệu '${brandToDelete?.name}'?`"
      description="Hành động này không thể hoàn tác."
      confirm-text="Xóa thương hiệu"
      cancel-text="Hủy"
      :loading="deleting"
      @confirm="deleteBrand"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useAdminStore } from "@/stores/admin";
import notificationService from "@/utils/notificationService";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";
import logger from "@/utils/logger";
import { formatDate } from "@/utils/formatters";

const adminStore = useAdminStore();

// State
const loading = ref(false);
const saving = ref(false);
const deleting = ref(false);
const brands = ref([]);
const searchKeyword = ref("");
const filterStatus = ref("all");
const showModal = ref(false);
const showDeleteModal = ref(false);
const isEditMode = ref(false);
const brandToDelete = ref(null);
const currentPage = ref(1);
const itemsPerPage = 10;

// Form data
const formData = ref({
  id: null,
  name: "",
  slug: "",
  logoUrl: "",
  websiteUrl: "",
  description: "",
  isActive: true,
});

// Computed
const filteredBrands = computed(() => {
  let result = brands.value;

  // Filter by search
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(
      (brand) =>
        brand.name.toLowerCase().includes(keyword) ||
        brand.slug.toLowerCase().includes(keyword)
    );
  }

  // Filter by status
  if (filterStatus.value !== "all") {
    const isActive = filterStatus.value === "active";
    result = result.filter((brand) => brand.isActive === isActive);
  }

  return result;
});

const paginatedBrands = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredBrands.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(filteredBrands.value.length / itemsPerPage);
});

const activeBrandsCount = computed(() => {
  return brands.value.filter((b) => b.isActive).length;
});

const inactiveBrandsCount = computed(() => {
  return brands.value.filter((b) => !b.isActive).length;
});

// Methods
const fetchBrands = async () => {
  loading.value = true;
  try {
    const result = await adminStore.fetchBrands();
    brands.value = result.content || result || [];
  } catch (error) {
    logger.error("Error fetching brands:", error);
    notificationService.apiError(error, "Lỗi khi tải danh sách thương hiệu");
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
    logoUrl: "",
    websiteUrl: "",
    description: "",
    isActive: true,
  };
  showModal.value = true;
};

const openEditModal = (brand) => {
  isEditMode.value = true;
  formData.value = { ...brand };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  formData.value = {
    id: null,
    name: "",
    slug: "",
    logoUrl: "",
    websiteUrl: "",
    description: "",
    isActive: true,
  };
};

const generateSlug = () => {
  formData.value.slug = formData.value.name
    .toLowerCase()
    .normalize("NFD") // bỏ dấu tiếng Việt
    .replace(/[\u0300-\u036f]/g, "") // loại bỏ ký tự tổ hợp
    .replace(/đ/g, "d")
    .replace(/[^a-z0-9\s-]/g, "") // chỉ giữ chữ cái, số, dấu cách và dấu -
    .replace(/\s+/g, "-") // thay khoảng trắng bằng dấu -
    .replace(/-+/g, "-") // gộp dấu - liên tiếp
    .trim();
};

const saveBrand = async () => {
  saving.value = true;
  try {
    if (isEditMode.value) {
      await adminStore.updateBrand(formData.value.id, formData.value);
    } else {
      await adminStore.createBrand(formData.value);
    }
    await fetchBrands();
    closeModal();
    // alert(`${isEditMode.value ? "Cập nhật" : "Thêm"} thương hiệu thành công!`);
    notificationService.success(
      "Thành công",
      `${isEditMode.value ? "Cập nhật" : "Thêm"} thương hiệu thành công!`,
      { duration: 3000 }
    );
  } catch (error) {
    logger.error("Error saving brand:", error);
    notificationService.apiError(error, "Lỗi khi lưu thương hiệu");
  } finally {
    saving.value = false;
  }
};

const confirmDelete = (brand) => {
  brandToDelete.value = brand;
  showDeleteModal.value = true;
};

const deleteBrand = async () => {
  deleting.value = true;
  try {
    await adminStore.deleteBrand(brandToDelete.value.id);
    await fetchBrands();
    showDeleteModal.value = false;
    brandToDelete.value = null;
    // alert("Xóa thương hiệu thành công!");
    notificationService.success("Thành công", "Xóa thương hiệu thành công!", {
      duration: 3000,
    });
  } catch (error) {
    logger.error("Error deleting brand:", error);
    notificationService.apiError(error, "Lỗi khi xóa thương hiệu");
  } finally {
    deleting.value = false;
  }
};

const onLogoError = (e) => {
  e.target.src = "/brand-default.png"; // ảnh local
  e.target.onerror = null; // tránh lặp vô hạn
};

const resetFilters = () => {
  searchKeyword.value = "";
  filterStatus.value = "all";
  currentPage.value = 1;
};

const truncateText = (text, maxLength) => {
  if (!text) return "";
  return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
};

const handleImageError = (e) => {
  e.target.style.display = "none";
};

// Lifecycle
onMounted(() => {
  fetchBrands();
});
</script>

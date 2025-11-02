<template>
  <div class="admin-materials admin-page">
    <!-- ===== PAGE HEADER ===== -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">layers</span>
            Quản lý Chất liệu
          </h1>
          <p class="page-subtitle">Quản lý danh sách chất liệu sản phẩm</p>
        </div>
        <div class="header-actions">
          <button class="btn btn-primary" @click="openCreateModal">
            <span class="material-icons">add</span>
            Thêm Chất liệu
          </button>
        </div>
      </div>
    </div>

    <!-- ===== STATS GRID ===== -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-success)">
          <span class="material-icons">check_circle</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ activeMaterialsCount }}</div>
          <div class="stat-label">ĐANG HOẠT ĐỘNG</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-warning)">
          <span class="material-icons">pause_circle</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ inactiveMaterialsCount }}</div>
          <div class="stat-label">TẠM NGƯNG</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: var(--gradient-info)">
          <span class="material-icons">inventory</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ materials.length }}</div>
          <div class="stat-label">TỔNG CHẤT LIỆU</div>
        </div>
      </div>
    </div>

    <!-- ===== FILTERS ===== -->
    <div class="content-section">
      <div class="filters-grid">
        <!-- 🔍 Ô Tìm kiếm -->
        <div class="filter-item wide">
          <label class="filter-label">
            <span class="material-icons">search</span>
            TÌM KIẾM
          </label>
          <div class="filter-input-wrapper">
            <input
              v-model="searchKeyword"
              type="text"
              class="filter-input"
              placeholder="Tìm theo tên chất liệu hoặc slug..."
            />
          </div>
        </div>

        <!-- 📦 Ô Trạng thái -->
        <div class="filter-item">
          <label class="filter-label">
            <span class="material-icons">category</span>
            TRẠNG THÁI
          </label>
          <div class="filter-input-wrapper">
            <select class="filter-select" v-model="filterStatus">
              <option value="all">Tất cả</option>
              <option value="active">Đang hoạt động</option>
              <option value="inactive">Tạm ngưng</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== LOADING STATE ===== -->
    <div v-if="loading" class="admin-loading">
      <div class="loading-spinner"></div>
      <p class="loading-text">Đang tải dữ liệu...</p>
    </div>

    <!-- ===== EMPTY STATE ===== -->
    <div v-else-if="filteredMaterials.length === 0" class="admin-empty-state">
      <div class="empty-state-icon">
        <span class="material-icons">layers</span>
      </div>
      <h3 class="empty-state-title">Không có chất liệu nào</h3>
      <p class="empty-state-description">
        Bắt đầu thêm chất liệu đầu tiên cho cửa hàng của bạn
      </p>
      <button class="btn btn-primary" @click="openCreateModal">
        <span class="material-icons">add</span>
        Thêm Chất liệu
      </button>
    </div>

    <!-- ===== MATERIALS TABLE ===== -->
    <div v-else class="table-container">
      <table class="admin-table materials-table">
        <thead>
          <tr>
            <th style="width: 6%">ID</th>
            <th style="width: 20%">Tên chất liệu</th>
            <th style="width: 14%">Slug</th>
            <th style="width: 18%">Trạng thái</th>
            <th style="width: 14%">Ngày tạo</th>
            <th style="width: 10%">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="material in paginatedMaterials" :key="material.id">
            <td>{{ material.id }}</td>
            <td>
              <strong>{{ material.name }}</strong>
              <p v-if="material.description" class="brand-desc">
                {{ truncateText(material.description, 50) }}
              </p>
            </td>
            <td>
              <code class="code-badge">{{ material.slug }}</code>
            </td>
            <td>
              <span
                class="status-badge"
                :class="material.isActive ? 'status-active' : 'status-inactive'"
              >
                <span class="material-icons">{{
                  material.isActive ? "check_circle" : "cancel"
                }}</span>
                {{ material.isActive ? "Hoạt động" : "Tạm ngưng" }}
              </span>
            </td>
            <td>{{ formatDate(material.createdAt) }}</td>
            <td>
              <div class="cell-actions">
                <button
                  class="btn-icon btn-edit"
                  @click="openEditModal(material)"
                  title="Chỉnh sửa"
                >
                  <span class="material-icons">edit</span>
                </button>
                <button
                  class="btn-icon btn-delete"
                  @click="confirmDelete(material)"
                  title="Xóa"
                >
                  <span class="material-icons">delete</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ===== PAGINATION ===== -->
    <div v-if="totalPages > 1" class="table-pagination">
      <button
        class="pagination-btn"
        :disabled="currentPage === 1"
        @click="currentPage--"
      >
        <span class="material-icons">chevron_left</span>
        Trước
      </button>

      <div class="pagination-info">
        Trang {{ currentPage }} / {{ totalPages }}
      </div>

      <button
        class="pagination-btn"
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >
        Sau
        <span class="material-icons">chevron_right</span>
      </button>
    </div>

    <!-- ===== CREATE/EDIT MODAL ===== -->
    <div v-if="showModal" class="aurora-modal-overlay" @click="closeModal">
      <div class="aurora-modal" @click.stop>
        <!-- HEADER -->
        <div class="aurora-modal-header">
          <h2 class="modal-title">
            <span class="material-icons">{{
              isEditMode ? "edit" : "add"
            }}</span>
            {{ isEditMode ? "Chỉnh sửa Chất liệu" : "Thêm Chất liệu mới" }}
          </h2>
          <button class="modal-close" @click="closeModal">
            <span class="material-icons">close</span>
          </button>
        </div>

        <!-- BODY -->
        <div class="aurora-modal-body">
          <form @submit.prevent="saveMaterial">
            <div class="form-row">
              <!-- 🔹 TÊN CHẤT LIỆU -->
              <div class="form-group">
                <label class="form-label">TÊN CHẤT LIỆU *</label>
                <input
                  v-model="formData.name"
                  type="text"
                  class="form-input"
                  placeholder="VD: Da tổng hợp, Vải canvas..."
                  @input="generateSlug"
                  required
                />
              </div>

              <!-- 🔹 SLUG -->
              <div class="form-group">
                <label class="form-label">SLUG *</label>
                <input
                  v-model="formData.slug"
                  type="text"
                  class="form-input"
                  placeholder="VD: da-tong-hop, vai-canvas..."
                  required
                />
              </div>
            </div>

            <!-- 🔹 MÔ TẢ -->
            <div class="form-group full">
              <label class="form-label">MÔ TẢ</label>
              <textarea
                v-model="formData.description"
                class="form-textarea"
                placeholder="Nhập mô tả về chất liệu..."
                rows="3"
              ></textarea>
            </div>

            <!-- 🔹 KÍCH HOẠT -->
            <div class="form-check">
              <input
                type="checkbox"
                id="isActive"
                v-model="formData.isActive"
                class="form-check-input"
              />
              <label for="isActive" class="form-check-label"
                >Kích hoạt chất liệu</label
              >
            </div>

            <!-- 🔹 BUTTONS -->
            <div class="form-actions">
              <button type="button" class="btn btn-outline" @click="closeModal">
                <span class="material-icons">close</span>
                Hủy
              </button>
              <button
                type="submit"
                class="btn btn-primary"
                :disabled="saving"
                :class="{ 'btn-loading': saving }"
              >
                <span class="material-icons">{{
                  saving ? "hourglass_empty" : "save"
                }}</span>
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
import { ElMessage } from "element-plus";
import { useAdminStore } from "@/stores/admin";
import ConfirmDialog from "@/assets/components/common/ConfirmDialog.vue";

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
    console.error("Error fetching materials:", error);
    ElMessage.error({
      message: "Lỗi khi tải danh sách chất liệu",
      duration: 3000,
    });
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
    ElMessage.success({
      message: `${
        isEditMode.value ? "Cập nhật" : "Thêm"
      } chất liệu thành công!`,
      duration: 3000,
    });
  } catch (error) {
    console.error("Error saving material:", error);
    ElMessage.error({
      message: "Lỗi khi lưu chất liệu",
      duration: 3000,
    });
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
    ElMessage.success({
      message: "Xóa chất liệu thành công!",
      duration: 3000,
    });
  } catch (error) {
    console.error("Error deleting material:", error);
    ElMessage.error({
      message: "Lỗi khi xóa chất liệu",
      duration: 3000,
    });
  } finally {
    deleting.value = false;
  }
};

const resetFilters = () => {
  searchKeyword.value = "";
  filterStatus.value = "all";
  currentPage.value = 1;
};

const formatDate = (dateString) => {
  if (!dateString) return "—";
  const date = new Date(dateString);
  return date.toLocaleDateString("vi-VN");
};

const truncateText = (text, maxLength) => {
  if (!text) return "";
  return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
};

// Lifecycle
onMounted(() => {
  fetchMaterials();
});
</script>

<style scoped>
.page-title .material-icons {
  font-size: 2rem;
  color: var(--accent-primary);
  animation: pulse 2s ease-in-out infinite;
}

/* ===== STATS GRID (Aurora v2) ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.2rem;
  margin-top: 1.5rem;
}

/* Mỗi card thống kê */
.stat-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  padding: 1rem 1.25rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(122, 92, 255, 0.15);
}

/* Icon trái */
.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: var(
    --gradient-primary,
    linear-gradient(135deg, #7a5cff, #9f6bff)
  );
  flex-shrink: 0;
}

.stat-icon .material-icons {
  font-size: 26px;
  color: #fff;
}

/* Nội dung phải */
.stat-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  line-height: 1.3;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #fff;
}

.stat-label {
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.6);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* ===== FILTER SECTION ===== */
.content-section {
  background: var(--bg-secondary);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 1.25rem;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.03);
}

.filters-grid {
  display: flex;
  align-items: flex-end;
  gap: 1.5rem;
  flex-wrap: nowrap;
}

/* Mỗi ô lọc */
.filter-item {
  display: flex;
  flex-direction: column;
  flex: 0 0 220px; /* Mặc định kích thước cho ô trạng thái */
}

.filter-item.wide {
  flex: 1; /* Ô tìm kiếm giãn hết phần còn lại */
  min-width: 350px;
}

/* Label */
.filter-label {
  display: flex;
  align-items: center;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  letter-spacing: 0.5px;
  margin-bottom: 6px;
  text-transform: uppercase;
  gap: 4px;
}

.filter-label .material-icons {
  font-size: 16px;
  opacity: 0.8;
}

/* Ô nhập chung */
.filter-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  transition: border 0.2s ease, box-shadow 0.2s ease;
}

.filter-input-wrapper:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.25);
}

/* Input text */
.filter-input {
  width: 100%;
  background: transparent;
  border: none;
  outline: none;
  padding: 0.6rem 0.75rem;
  font-size: 14px;
  color: var(--text-primary);
}

/* Dropdown */
.filter-select {
  width: 100%;
  background: transparent;
  border: none;
  outline: none;
  padding: 0.6rem 0.75rem;
  font-size: 14px;
  color: var(--text-primary);
  appearance: none;
  cursor: pointer;
}

.filter-select option {
  color: #000;
}

/* Responsive */
@media (max-width: 768px) {
  .filters-grid {
    flex-direction: column;
    gap: 1rem;
  }

  .filter-item,
  .filter-item.wide {
    flex: 1;
    width: 100%;
  }
}

.brand-desc {
  color: var(--text-tertiary);
  font-size: var(--text-xs);
  margin: 0;
}

/* ===== OVERLAY ===== */
.aurora-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(10, 10, 20, 0.65);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

/* ===== MODAL ===== */
.aurora-modal {
  width: 480px;
  background: linear-gradient(145deg, #141726 0%, #1a1f33 100%);
  border-radius: 16px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.4);
  overflow: hidden;
  animation: fadeInUp 0.25s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== HEADER ===== */
.aurora-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #2c235a, #4a2e88);
  padding: 1rem 1.25rem;
  color: #fff;
  font-weight: 600;
}

.modal-title {
  display: flex;
  align-items: center;
  font-size: 1.1rem;
  gap: 0.5rem;
}

.modal-title .material-icons {
  font-size: 20px;
}

.modal-close {
  background: none;
  border: none;
  color: #fff;
  opacity: 0.8;
  cursor: pointer;
  transition: opacity 0.2s;
}

.modal-close:hover {
  opacity: 1;
}

/* ===== BODY ===== */
.aurora-modal-body {
  padding: 1.25rem 1.5rem 1.5rem;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-group.full {
  width: 100%;
  margin-bottom: 1rem;
}

/* Label */
.form-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary, #aeb3c1);
  margin-bottom: 6px;
}

/* Input / Textarea */
.form-input,
.form-textarea {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  padding: 0.65rem 0.75rem;
  color: #fff;
  font-size: 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #7a5cff;
  box-shadow: 0 0 0 2px rgba(122, 92, 255, 0.25);
}

/* Checkbox */
.form-check {
  display: flex;
  align-items: center;
  margin: 0.5rem 0 1.5rem;
  gap: 0.5rem;
}

.form-check-input {
  accent-color: #7a5cff;
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.form-check-label {
  color: #cfd3e1;
  font-size: 14px;
}

/* Buttons */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.btn {
  border-radius: 8px;
  padding: 0.6rem 1.1rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 14px;
  transition: all 0.2s ease;
}

/* Hủy */
.btn-outline {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #ccc;
}
.btn-outline:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* Lưu */
.btn-primary {
  background: linear-gradient(135deg, #7a5cff, #9f6bff);
  color: white;
  border: none;
  box-shadow: 0 0 12px rgba(122, 92, 255, 0.3);
}
.btn-primary:hover {
  box-shadow: 0 0 18px rgba(122, 92, 255, 0.45);
  transform: translateY(-1px);
}

/* ===== TABLE LAYOUT ===== */
.table-container {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  padding: 1rem;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  overflow: hidden;
}

/* Bảng chính */
.admin-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed; /* ⚡ Giữ cột cố định, không lệch khi text khác độ dài */
  font-size: 15.5px; /* 🔍 Tăng cỡ chữ bảng */
  line-height: 1.6;
}

.admin-table thead {
  background: rgba(255, 255, 255, 0.03);
}

.admin-table th {
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.3px;
  color: var(--text-secondary);
  padding: 1rem; /* ⬆ tăng padding cho cân đối hơn */
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

/* Dòng dữ liệu */
.admin-table td {
  padding: 1rem;
  color: #e2e6f0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  vertical-align: middle;
}

.admin-table tr:hover td {
  background: rgba(255, 255, 255, 0.03);
}

/* ===== Độ rộng các cột cố định (chuẩn cho toàn hệ thống) ===== */
.admin-table th:nth-child(1),
.admin-table td:nth-child(1) {
  text-align: center;
}

.admin-table th:nth-child(2),
.admin-table td:nth-child(2) {
  text-align: center;
}

.admin-table th:nth-child(3),
.admin-table td:nth-child(3),
.admin-table th:nth-child(4),
.admin-table td:nth-child(4),
.admin-table th:nth-child(5),
.admin-table td:nth-child(5),
.admin-table th:nth-child(6),
.admin-table td:nth-child(6) {
  text-align: center;
}

/* ===== Mô tả phụ ===== */
.desc,
.brand-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.65);
  margin-top: 3px;
  line-height: 1.4;
}

/* ===== Code badge ===== */
.code-badge {
  background: rgba(122, 92, 255, 0.12);
  color: #b49cff;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 13px;
  font-family: var(--font-mono);
}

/* ===== Status badge ===== */
.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-radius: 999px;
  padding: 6px 14px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}

.status-active {
  color: #79ffb0;
  border-color: rgba(121, 255, 176, 0.25);
}

.status-inactive {
  color: #ff8a8a;
  border-color: rgba(255, 138, 138, 0.25);
}

.status-badge .material-icons {
  font-size: 17px;
}

/* ===== Action buttons ===== */
.cell-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.btn-icon {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 6px;
  cursor: pointer;
  color: #d5d9e4;
  transition: all 0.2s ease;
}

.btn-icon:hover {
  background: rgba(122, 92, 255, 0.15);
  color: #fff;
}

/* ===== Tăng tương phản đầu bảng ===== */
.admin-table thead th {
  background: rgba(255, 255, 255, 0.02);
}

/* ===== DESCRIPTION BELOW NAME ===== */
.desc {
  color: var(--text-tertiary);
  font-size: 13px;
  margin-top: 2px;
}

/* ===== CODE BADGE ===== */
.code-badge {
  background: rgba(122, 92, 255, 0.1);
  color: #9f83ff;
  padding: 3px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-family: var(--font-mono);
}

/* ===== STATUS BADGE ===== */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 13px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.status-active {
  color: #79ffb0;
  border-color: rgba(121, 255, 176, 0.2);
}
.status-inactive {
  color: #ff8a8a;
  border-color: rgba(255, 138, 138, 0.2);
}

.status-badge .material-icons {
  font-size: 16px;
}

/* ===== ACTION BUTTONS ===== */
.cell-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.btn-icon {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 6px;
  cursor: pointer;
  color: #d5d9e4;
  transition: all 0.2s ease;
}

.btn-icon:hover {
  background: rgba(122, 92, 255, 0.15);
  color: #fff;
}
</style>

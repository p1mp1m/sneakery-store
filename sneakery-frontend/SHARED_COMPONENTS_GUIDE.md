# Shared Admin Components Usage Guide

## Overview
This guide explains how to use the newly created shared components across all admin modules for consistency and code reusability.

---

## 1. StatsCard Component

**Location:** `src/assets/components/admin/StatsCard.vue`

**Usage:**
```vue
<script setup>
import StatsCard from '@/assets/components/admin/StatsCard.vue'
</script>

<template>
  <div class="stats-grid">
    <StatsCard
      icon="local_activity"
      :value="totalCoupons"
      label="TỔNG MÃ GIẢM GIÁ"
      variant="primary"
    />
    
    <StatsCard
      icon="check_circle"
      :value="activeCoupons"
      label="ĐANG HOẠT ĐỘNG"
      variant="success"
      :change="12.5"
    />
    
    <StatsCard
      icon="schedule"
      :value="expiringSoon"
      label="SẮP HẾT HẠN"
      variant="warning"
      :change="-5.2"
    />
  </div>
</template>
```

**Props:**
- `icon` (string, required): Material icon name
- `value` (string|number, required): The stat value to display
- `label` (string, required): Label text
- `variant` (string, default: 'primary'): Color variant (primary, success, warning, danger, info)
- `change` (number, optional): Percentage change (shows trend arrow)

---

## 2. EmptyState Component

**Location:** `src/assets/components/admin/EmptyState.vue`

**Usage:**
```vue
<script setup>
import EmptyState from '@/assets/components/admin/EmptyState.vue'
</script>

<template>
  <EmptyState
    icon="local_activity"
    title="Chưa có mã giảm giá"
    description="Nhấn nút 'Tạo mã giảm giá' để bắt đầu"
    action-text="Tạo mã giảm giá"
    action-icon="add"
    @action="openCreateDialog"
  />
  
  <!-- Or with custom action slot -->
  <EmptyState
    icon="inbox"
    title="Không có dữ liệu"
    description="Danh sách trống"
  >
    <template #action>
      <button @click="handleCustomAction" class="btn btn-primary">
        Custom Action
      </button>
    </template>
  </EmptyState>
</template>
```

**Props:**
- `icon` (string, default: 'inbox'): Material icon name
- `title` (string, required): Main title
- `description` (string): Description text
- `actionText` (string): Action button text
- `actionIcon` (string, default: 'add'): Material icon for button

**Events:**
- `@action`: Emitted when action button is clicked

---

## 3. StatusBadge Component

**Location:** `src/assets/components/admin/StatusBadge.vue`

**Usage:**
```vue
<script setup>
import StatusBadge from '@/assets/components/admin/StatusBadge.vue'
</script>

<template>
  <StatusBadge status="active" text="Đang hoạt động" />
  <StatusBadge status="pending" text="Chờ xử lý" />
  <StatusBadge status="cancelled" text="Đã hủy" :show-icon="false" />
</template>
```

**Props:**
- `status` (string, required): Status type (active, inactive, pending, approved, rejected, etc.)
- `text` (string): Custom text (auto-generated if not provided)
- `showIcon` (boolean, default: true): Show/hide icon

**Available Statuses:**
- `active`, `inactive`, `pending`, `approved`, `rejected`
- `processing`, `shipped`, `delivered`, `cancelled`
- `completed`, `in-progress`, `draft`, `scheduled`, `sent`
- `failed`, `expired`, `upcoming`
- `low-stock`, `out-of-stock`, `in-stock`

---

## 4. FilterBar Component

**Location:** `src/assets/components/admin/FilterBar.vue`

**Usage:**
```vue
<script setup>
import FilterBar from '@/assets/components/admin/FilterBar.vue'

const handleSearch = (query) => {
  console.log('Search:', query)
  // Fetch data with search query
}

const handleReset = () => {
  // Reset all filters
}
</script>

<template>
  <FilterBar
    search-placeholder="Tìm kiếm theo tên, mã..."
    @search="handleSearch"
    @reset="handleReset"
  >
    <template #filters>
      <div class="filter-group">
        <label>Loại</label>
        <select v-model="filters.type" class="form-control">
          <option value="">Tất cả</option>
          <option value="percent">Phần trăm</option>
          <option value="fixed">Số tiền</option>
        </select>
      </div>
      
      <div class="filter-group">
        <label>Trạng thái</label>
        <select v-model="filters.status" class="form-control">
          <option value="">Tất cả</option>
          <option value="active">Kích hoạt</option>
          <option value="inactive">Vô hiệu hóa</option>
        </select>
      </div>
    </template>
    
    <template #actions>
      <button @click="exportData" class="btn btn-secondary btn-sm">
        <span class="material-icons">download</span>
        Xuất dữ liệu
      </button>
    </template>
  </FilterBar>
</template>
```

**Props:**
- `showSearch` (boolean, default: true): Show/hide search box
- `searchPlaceholder` (string): Placeholder text
- `showReset` (boolean, default: true): Show/hide reset button
- `resetText` (string, default: 'Xóa bộ lọc'): Reset button text
- `debounceTime` (number, default: 300): Search debounce time in ms

**Events:**
- `@search`: Emitted when search query changes (debounced)
- `@reset`: Emitted when reset button is clicked

**Slots:**
- `#filters`: Custom filter inputs
- `#actions`: Custom action buttons

---

## 5. BulkActions Component

**Location:** `src/assets/components/admin/BulkActions.vue`

**Usage:**
```vue
<script setup>
import BulkActions from '@/assets/components/admin/BulkActions.vue'

const selectedItems = ref([])

const handleBulkDelete = () => {
  // Delete selected items
}

const handleClearSelection = () => {
  selectedItems.value = []
}

const handleBulkActivate = () => {
  // Activate selected items
}
</script>

<template>
  <BulkActions
    :selected-count="selectedItems.length"
    delete-text="Xóa đã chọn"
    @delete="handleBulkDelete"
    @clear="handleClearSelection"
  >
    <template #actions>
      <button @click="handleBulkActivate" class="btn btn-success btn-sm">
        <span class="material-icons">check_circle</span>
        Kích hoạt
      </button>
      <button @click="handleBulkDeactivate" class="btn btn-warning btn-sm">
        <span class="material-icons">block</span>
        Vô hiệu hóa
      </button>
    </template>
  </BulkActions>
</template>
```

**Props:**
- `selectedCount` (number, required): Number of selected items
- `showDelete` (boolean, default: true): Show/hide delete button
- `deleteText` (string, default: 'Xóa đã chọn'): Delete button text

**Events:**
- `@delete`: Emitted when delete button is clicked
- `@clear`: Emitted when clear selection button is clicked

**Slots:**
- `#actions`: Custom bulk action buttons

---

## Complete Module Template

Here's a complete example of how to structure an admin module with all shared components:

```vue
<template>
  <div class="admin-page">
    <!-- Page Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <span class="material-icons">icon_name</span>
            Module Title
          </h1>
          <p class="page-subtitle">Module description</p>
        </div>
        <div class="header-actions">
          <button @click="handleExport('csv')" class="btn btn-secondary">
            <span class="material-icons">download</span>
            Xuất CSV
          </button>
          <button @click="openCreateDialog" class="btn btn-primary">
            <span class="material-icons">add</span>
            Tạo mới
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid animate-fade-in">
      <StatsCard
        icon="category"
        :value="stats.total"
        label="TỔNG SỐ"
        variant="primary"
      />
      <StatsCard
        icon="check_circle"
        :value="stats.active"
        label="ĐANG HOẠT ĐỘNG"
        variant="success"
      />
      <!-- Add more stat cards -->
    </div>

    <!-- Filters -->
    <FilterBar
      @search="handleSearch"
      @reset="handleReset"
    >
      <template #filters>
        <!-- Add filter inputs -->
      </template>
    </FilterBar>

    <!-- Bulk Actions (appears when items selected) -->
    <BulkActions
      :selected-count="selectedItems.length"
      @delete="handleBulkDelete"
      @clear="handleClearSelection"
    />

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Đang tải dữ liệu...</p>
    </div>

    <!-- Empty State -->
    <EmptyState
      v-else-if="items.length === 0"
      icon="inbox"
      title="Chưa có dữ liệu"
      description="Nhấn 'Tạo mới' để bắt đầu"
      action-text="Tạo mới"
      @action="openCreateDialog"
    />

    <!-- Data Table -->
    <div v-else class="table-container">
      <table class="admin-table">
        <thead>
          <tr>
            <th>
              <input type="checkbox" @change="toggleSelectAll" />
            </th>
            <!-- Table headers -->
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>
              <input type="checkbox" v-model="selectedItems" :value="item.id" />
            </td>
            <td>
              <StatusBadge :status="item.status" :text="getStatusText(item.status)" />
            </td>
            <!-- Table cells -->
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import StatsCard from '@/assets/components/admin/StatsCard.vue'
import EmptyState from '@/assets/components/admin/EmptyState.vue'
import StatusBadge from '@/assets/components/admin/StatusBadge.vue'
import FilterBar from '@/assets/components/admin/FilterBar.vue'
import BulkActions from '@/assets/components/admin/BulkActions.vue'
import { exportToCSV, exportToJSON } from '@/utils/exportHelpers'

// State
const items = ref([])
const selectedItems = ref([])
const loading = ref(false)

const stats = reactive({
  total: 0,
  active: 0
})

// Methods
const handleSearch = (query) => {
  // Implement search
}

const handleReset = () => {
  // Reset filters
}

const handleExport = (format) => {
  const data = items.value.map(item => ({
    // Map item data
  }))
  
  if (format === 'csv') {
    exportToCSV(data, `export_${Date.now()}`, [
      // Define columns
    ])
  } else {
    exportToJSON(data, `export_${Date.now()}`)
  }
}

const handleBulkDelete = () => {
  // Delete selected items
}

const handleClearSelection = () => {
  selectedItems.value = []
}
</script>

<style scoped>
/* Add custom styles */
</style>
```

---

## CSS Classes Reference

### Stats Grid
```css
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
  margin-bottom: var(--space-8);
}
```

### Page Header
```css
.page-header {
  background: var(--card-bg);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  margin-bottom: var(--space-8);
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-primary);
  backdrop-filter: blur(10px);
}
```

### Animations
```css
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-fade-up {
  animation: fadeUp 0.6s ease-out;
}
```

---

## Best Practices

1. **Consistency**: Always use shared components for stats, empty states, and status badges
2. **Props**: Provide all required props and use meaningful labels
3. **Events**: Handle component events appropriately
4. **Styling**: Rely on component's built-in styles, add custom styles only when needed
5. **Accessibility**: Components include ARIA labels and keyboard navigation
6. **Responsive**: All components are mobile-responsive by default
7. **Performance**: Components use Vue's reactivity efficiently

---

## Migration Checklist

When upgrading an existing module:

- [ ] Replace manual stats cards with `<StatsCard />`
- [ ] Replace empty state divs with `<EmptyState />`
- [ ] Replace status badges with `<StatusBadge />`
- [ ] Add `<FilterBar />` for search and filters
- [ ] Add `<BulkActions />` for bulk operations
- [ ] Import export helpers for CSV/JSON export
- [ ] Add export buttons to page header
- [ ] Update CSS to use Design System v2.0 variables
- [ ] Add glassmorphism effects (`backdrop-filter: blur(10px)`)
- [ ] Test responsive design on mobile
- [ ] Verify all animations work smoothly
- [ ] Check accessibility with keyboard navigation

---

**Note:** All components are designed to work with the Design System v2.0 and use CSS variables from the unified theme.


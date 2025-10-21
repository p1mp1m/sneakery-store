# 📚 HƯỚNG DẪN SỬ DỤNG ADMIN PANEL CSS SYSTEM

## 🎨 Tổng quan

Hệ thống CSS chuyên nghiệp cho Admin Panel của Sneakery Store với:
- ✅ **11 phần chính** được tổ chức rõ ràng
- ✅ **Responsive Design** hoàn chỉnh (Desktop, Tablet, Mobile)
- ✅ **Color System** nhất quán với gradient đẹp mắt
- ✅ **Animations** mượt mà và chuyên nghiệp
- ✅ **100+ Classes** tiện ích sẵn có

---

## 📋 Mục lục

1. [Color Palette - Bảng màu](#1-color-palette)
2. [Admin Layout - Bố cục](#2-admin-layout)
3. [Page Components - Thành phần trang](#3-page-components)
4. [Buttons & Actions - Nút bấm](#4-buttons--actions)
5. [Bulk Action Bar - Thanh hành động](#5-bulk-action-bar)
6. [Status Badges - Nhãn trạng thái](#6-status-badges)
7. [Forms & Inputs - Biểu mẫu](#7-forms--inputs)
8. [Pagination - Phân trang](#8-pagination)
9. [Responsive Design - Thiết kế đáp ứng](#9-responsive-design)
10. [Animations - Hiệu ứng](#10-animations)
11. [Utilities - Tiện ích](#11-utilities)

---

## 1. Color Palette

### Gradient chính
```css
--gradient-primary: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
--gradient-success: linear-gradient(135deg, #55efc4 0%, #00b894 100%);
--gradient-warning: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
--gradient-danger: linear-gradient(135deg, #ff7675 0%, #d63031 100%);
```

### Cách sử dụng
```html
<div style="background: var(--gradient-primary)">Gradient tím</div>
<button class="btn-primary">Nút gradient</button>
```

---

## 2. Admin Layout

### Cấu trúc cơ bản
```html
<div class="admin-layout">
  <aside class="admin-sidebar">
    <!-- Sidebar content -->
  </aside>
  <main class="admin-main">
    <!-- Main content -->
  </main>
</div>
```

### Sidebar với Navigation
```html
<div class="sidebar-logo">
  <div class="logo-text">SNEAKERY</div>
  <div class="logo-subtitle">Admin Panel</div>
</div>

<nav class="sidebar-nav">
  <router-link to="/admin/dashboard" class="nav-item">
    <i class="material-icons">dashboard</i>
    <span>Dashboard</span>
  </router-link>
  <!-- More items... -->
</nav>
```

---

## 3. Page Components

### Page Header
```html
<div class="page-header">
  <div>
    <h1 class="page-title">Quản lý Sản phẩm</h1>
    <p class="page-subtitle">Quản lý sản phẩm và variants</p>
  </div>
  <div class="header-actions">
    <button class="btn btn-export">Export</button>
    <button class="btn btn-primary">Thêm mới</button>
  </div>
</div>
```

### Stats Cards
```html
<div class="stats-grid">
  <div class="stats-card">
    <div class="stats-header">
      <div>
        <div class="stats-value">1,234</div>
        <div class="stats-label">Tổng người dùng</div>
      </div>
      <div class="stats-icon success">
        <i class="material-icons">people</i>
      </div>
    </div>
    <div class="stats-change positive">+12% so với tháng trước</div>
  </div>
  <!-- More cards... -->
</div>
```

### Data Table
```html
<div class="table-card">
  <table class="admin-table">
    <thead>
      <tr>
        <th>ID</th>
        <th>Tên</th>
        <th>Hành động</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>1</td>
        <td>Sản phẩm A</td>
        <td>
          <button class="btn-edit">Sửa</button>
          <button class="btn-delete">Xóa</button>
        </td>
      </tr>
    </tbody>
  </table>
</div>
```

### Chart Card
```html
<div class="chart-card">
  <LineChart :labels="labels" :datasets="datasets" />
</div>

<div class="chart-card large">
  <!-- Biểu đồ lớn hơn -->
</div>
```

---

## 4. Buttons & Actions

### Button Types
```html
<!-- Primary -->
<button class="btn-primary">
  <i class="material-icons">add</i>
  Thêm mới
</button>

<!-- Secondary -->
<button class="btn-secondary">Hủy</button>

<!-- Success -->
<button class="btn-success">Lưu</button>

<!-- Danger -->
<button class="btn-danger">Xóa</button>

<!-- Export -->
<button class="btn-export">
  <i class="material-icons">download</i>
  Export Excel
</button>

<!-- Edit/Delete (small) -->
<button class="btn-edit">Sửa</button>
<button class="btn-delete">Xóa</button>
```

### Button Sizes
```html
<button class="btn-primary btn-sm">Small</button>
<button class="btn-primary">Normal</button>
<button class="btn-primary btn-lg">Large</button>
```

---

## 5. Bulk Action Bar

### Cơ bản
```html
<div v-if="selectedItems.length > 0" class="bulk-action-bar">
  <div class="bulk-info">
    <i class="material-icons">check_circle</i>
    Đã chọn <strong>{{ selectedItems.length }}</strong> mục
  </div>
  <div class="bulk-actions">
    <button @click="bulkDelete">
      <i class="material-icons">delete</i>
      Xóa
    </button>
    <button @click="clearSelection">
      <i class="material-icons">clear</i>
      Bỏ chọn
    </button>
  </div>
</div>
```

### Với Select Dropdown
```html
<div class="bulk-action-bar">
  <div class="bulk-info">...</div>
  <div class="bulk-actions">
    <select class="bulk-status-select" v-model="bulkStatus">
      <option value="">Chọn trạng thái</option>
      <option value="pending">Pending</option>
      <option value="processing">Processing</option>
    </select>
    <button @click="updateStatus">Cập nhật</button>
  </div>
</div>
```

---

## 6. Status Badges

### Order Status
```html
<span class="status-badge status-pending">Pending</span>
<span class="status-badge status-processing">Processing</span>
<span class="status-badge status-shipped">Shipped</span>
<span class="status-badge status-completed">Completed</span>
<span class="status-badge status-cancelled">Cancelled</span>
```

### Active Status
```html
<span class="status-badge status-active">Active</span>
<span class="status-badge status-inactive">Inactive</span>
```

---

## 7. Forms & Inputs

### Filter Section
```html
<div class="filters-section">
  <div class="filter-group">
    <label>Tìm kiếm:</label>
    <input 
      type="text" 
      class="form-control" 
      placeholder="Nhập từ khóa..."
    />
  </div>
  
  <div class="filter-group">
    <label>Trạng thái:</label>
    <select class="form-control">
      <option value="">Tất cả</option>
      <option value="active">Active</option>
      <option value="inactive">Inactive</option>
    </select>
  </div>
  
  <div class="filter-actions">
    <button class="btn-reset">
      <i class="material-icons">clear</i>
      Reset
    </button>
  </div>
</div>
```

### Search Box với Icon
```html
<div class="search-box">
  <i class="material-icons search-icon">search</i>
  <input 
    type="text" 
    class="search-input" 
    placeholder="Tìm kiếm..."
    v-model="searchQuery"
  />
  <button v-if="searchQuery" class="clear-btn" @click="searchQuery = ''">
    <i class="material-icons">close</i>
  </button>
</div>
```

---

## 8. Pagination

```html
<div class="pagination-container">
  <div class="pagination-info">
    Hiển thị {{ startItem }} - {{ endItem }} của {{ totalItems }} mục
  </div>
  
  <div class="pagination-controls">
    <button 
      class="pagination-btn" 
      :disabled="currentPage === 0"
      @click="currentPage--"
    >
      Trước
    </button>
    
    <button 
      v-for="page in totalPages" 
      :key="page"
      class="pagination-btn"
      :class="{ active: page === currentPage + 1 }"
      @click="currentPage = page - 1"
    >
      {{ page }}
    </button>
    
    <button 
      class="pagination-btn"
      :disabled="currentPage === totalPages - 1"
      @click="currentPage++"
    >
      Sau
    </button>
  </div>
</div>
```

---

## 9. Responsive Design

### Breakpoints
```
Desktop:  > 1024px
Tablet:   768px - 1024px
Mobile:   < 768px
```

### Mobile Sidebar Toggle
```html
<!-- Thêm button toggle cho mobile -->
<button class="sidebar-toggle" @click="toggleSidebar">
  <i class="material-icons">menu</i>
</button>

<aside class="admin-sidebar" :class="{ open: sidebarOpen }">
  <!-- Sidebar content -->
</aside>
```

---

## 10. Animations

### Sử dụng classes animation
```html
<div class="stats-card animate-fade-in">...</div>
<div class="table-card animate-slide-in">...</div>
<div class="loading-indicator animate-pulse">...</div>
```

### Custom animations
```css
/* Trong component của bạn */
.my-element {
  animation: fadeIn 0.5s ease-out;
}
```

---

## 11. Utilities

### Text Alignment
```html
<div class="text-center">Canh giữa</div>
<div class="text-left">Canh trái</div>
<div class="text-right">Canh phải</div>
```

### Spacing
```html
<div class="mb-1">Margin bottom 0.5rem</div>
<div class="mb-2">Margin bottom 1rem</div>
<div class="mt-3">Margin top 1.5rem</div>
```

### Flex Utilities
```html
<div class="d-flex justify-between align-center gap-2">
  <span>Item 1</span>
  <span>Item 2</span>
</div>
```

### Empty State
```html
<div class="empty-state">
  <i class="material-icons">inventory</i>
  <h3>Chưa có dữ liệu</h3>
  <p>Nhấn "Thêm mới" để tạo mục đầu tiên</p>
</div>
```

### Loading State
```html
<div class="loading-container">
  <div class="loading-spinner"></div>
  <p>Đang tải dữ liệu...</p>
</div>
```

---

## 🎯 Best Practices

### 1. Luôn sử dụng CSS Variables
```css
/* ✅ ĐÚNG */
background: var(--gradient-primary);
color: var(--admin-text-primary);

/* ❌ SAI */
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
color: #1a1a2e;
```

### 2. Kết hợp classes thay vì viết CSS mới
```html
<!-- ✅ ĐÚNG -->
<button class="btn-primary btn-lg">
  <i class="material-icons">add</i>
  Thêm mới
</button>

<!-- ❌ SAI - Không nên viết CSS mới -->
<button style="background: linear-gradient(...); padding: 1rem 2rem;">
```

### 3. Responsive Design
```css
/* ✅ ĐÚNG - Mobile First */
.my-element {
  width: 100%;
}

@media (min-width: 768px) {
  .my-element {
    width: 50%;
  }
}
```

### 4. Sử dụng Semantic HTML
```html
<!-- ✅ ĐÚNG -->
<header class="page-header">
<main class="admin-main">
<aside class="admin-sidebar">

<!-- ❌ SAI -->
<div class="page-header">
<div class="admin-main">
```

---

## 📝 Component Examples

### Complete Admin Page Example
```vue
<template>
  <div class="admin-products">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Quản lý Sản phẩm</h1>
        <p class="page-subtitle">Quản lý sản phẩm và biến thể</p>
      </div>
      <div class="header-actions">
        <button class="btn-export" @click="exportData">
          <i class="material-icons">download</i>
          Export
        </button>
        <button class="btn-primary" @click="openCreateModal">
          <i class="material-icons">add</i>
          Thêm mới
        </button>
      </div>
    </div>

    <!-- Filters -->
    <div class="filters-section">
      <div class="filter-group">
        <label>Tìm kiếm:</label>
        <div class="search-box">
          <i class="material-icons search-icon">search</i>
          <input 
            type="text" 
            class="search-input"
            v-model="filters.search"
            placeholder="Tìm theo tên..."
          />
          <button v-if="filters.search" class="clear-btn" @click="clearSearch">
            <i class="material-icons">close</i>
          </button>
        </div>
      </div>
      
      <div class="filter-group">
        <label>Trạng thái:</label>
        <select class="form-control" v-model="filters.status">
          <option value="">Tất cả</option>
          <option value="active">Active</option>
          <option value="inactive">Inactive</option>
        </select>
      </div>
      
      <div class="filter-actions">
        <button class="btn-reset" @click="resetFilters">
          <i class="material-icons">clear</i>
          Reset
        </button>
      </div>
    </div>

    <!-- Data Table -->
    <div class="table-card">
      <table class="admin-table">
        <thead>
          <tr>
            <th>
              <input 
                type="checkbox" 
                class="checkbox-input"
                :checked="isAllSelected"
                @change="toggleSelectAll"
              />
            </th>
            <th>ID</th>
            <th>Tên sản phẩm</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>
              <input 
                type="checkbox" 
                class="checkbox-input"
                :checked="selected.includes(item.id)"
                @change="toggleSelect(item.id)"
              />
            </td>
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>
              <span :class="['status-badge', `status-${item.status}`]">
                {{ item.status }}
              </span>
            </td>
            <td>
              <button class="btn-edit" @click="edit(item)">Sửa</button>
              <button class="btn-delete" @click="remove(item)">Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination-container">
      <div class="pagination-info">
        Hiển thị {{ startItem }} - {{ endItem }} của {{ totalItems }}
      </div>
      <div class="pagination-controls">
        <button class="pagination-btn" :disabled="page === 0" @click="page--">
          Trước
        </button>
        <button 
          v-for="p in totalPages" 
          :key="p"
          class="pagination-btn"
          :class="{ active: p === page + 1 }"
          @click="page = p - 1"
        >
          {{ p }}
        </button>
        <button class="pagination-btn" :disabled="page === totalPages - 1" @click="page++">
          Sau
        </button>
      </div>
    </div>

    <!-- Bulk Actions -->
    <div v-if="selected.length > 0" class="bulk-action-bar">
      <div class="bulk-info">
        <i class="material-icons">check_circle</i>
        Đã chọn <strong>{{ selected.length }}</strong> mục
      </div>
      <div class="bulk-actions">
        <button @click="bulkDelete">
          <i class="material-icons">delete</i>
          Xóa {{ selected.length }} mục
        </button>
        <button @click="clearSelection">
          <i class="material-icons">clear</i>
          Bỏ chọn
        </button>
      </div>
    </div>
  </div>
</template>
```

---

## 🚀 Quick Start Checklist

- [ ] Import `admin-panel.css` trong `main.js`
- [ ] Sử dụng `admin-layout` cho layout chính
- [ ] Sử dụng `page-header` cho tiêu đề trang
- [ ] Sử dụng `filters-section` cho bộ lọc
- [ ] Sử dụng `table-card` + `admin-table` cho bảng
- [ ] Sử dụng `bulk-action-bar` cho hành động hàng loạt
- [ ] Sử dụng `status-badge` cho nhãn trạng thái
- [ ] Sử dụng `btn-*` classes cho nút bấm
- [ ] Test responsive trên mobile/tablet
- [ ] Kiểm tra animations hoạt động

---

## 💡 Tips & Tricks

1. **Gradient Background cho Cards**
   ```css
   .my-card::before {
     background: var(--gradient-primary);
   }
   ```

2. **Hover Effect cho Rows**
   ```css
   .admin-table tbody tr:hover {
     background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, transparent 100%);
     transform: translateX(4px);
   }
   ```

3. **Custom Icon Colors**
   ```css
   .nav-item.router-link-active .material-icons {
     color: #667eea;
     text-shadow: 0 0 10px rgba(102, 126, 234, 0.5);
   }
   ```

4. **Smooth Transitions**
   ```css
   transition: var(--admin-transition); /* 0.3s ease */
   transition: var(--admin-transition-fast); /* 0.15s ease */
   ```

---

## 📞 Support

Nếu có vấn đề hoặc câu hỏi:
1. Kiểm tra file `admin-panel.css` đã được import chưa
2. Đảm bảo sử dụng đúng class names
3. Kiểm tra console để xem có lỗi CSS không
4. Tham khảo examples trong guide này

---

**Made with ❤️ for Sneakery Store Admin Panel**


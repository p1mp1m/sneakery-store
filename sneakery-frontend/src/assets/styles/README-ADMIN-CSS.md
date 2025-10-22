# 📚 **ADMIN PANEL CSS ARCHITECTURE**

## 🎯 **Tổng Quan**

Hệ thống CSS của Admin Panel được tổ chức theo **kiến trúc phân tầng** để dễ bảo trì và tái sử dụng.

---

## 📁 **Cấu Trúc File CSS**

```
sneakery-frontend/src/assets/styles/
├── main.css                      # ✅ Global CSS - Typography, Variables, Utilities
├── components.css                # ✅ Shared Components - Buttons, Cards, Forms
├── admin-panel.css               # ✅ Admin Layout - Sidebar, Header, Navigation
├── admin-design-system.css       # 🆕 Admin Design System - Design Tokens, Shared Styles
└── admin-dashboard-shared.css    # 🆕 Dashboard Components - Stats, Charts, Profile Card
```

---

## 🎨 **Chi Tiết Từng File**

### 1. **`main.css`** - Global Foundation
**Mục đích:** CSS cơ bản cho toàn bộ ứng dụng (Admin + User)

**Bao gồm:**
- CSS Variables (Design Tokens): Colors, Spacing, Typography
- Reset CSS & Base Styles
- Typography (Headings, Paragraphs, Links)
- Utility Classes (text-center, d-flex, mb-1, etc.)
- Animations & Keyframes
- Responsive Design
- Accessibility (Focus states, Screen reader)

**Phạm vi:** Global - Áp dụng cho TẤT CẢ trang

---

### 2. **`components.css`** - Shared Components
**Mục đích:** Components tái sử dụng cho toàn ứng dụng

**Bao gồm:**
- Buttons (btn-primary, btn-secondary, btn-success, etc.)
- Cards (card, card-header, card-body)
- Forms (form-control, form-label, form-group)
- Badges (badge-primary, badge-success, etc.)
- Alerts (alert-success, alert-warning, etc.)
- Loading (loading-spinner, loading-dots, skeleton)
- Modals (modal-overlay, modal, modal-header)
- Toast Notifications
- Tables (table, table-striped)
- Avatars (avatar, avatar-sm, avatar-lg)

**Phạm vi:** Global - Áp dụng cho TẤT CẢ trang

---

### 3. **`admin-panel.css`** - Admin Layout
**Mục đích:** Layout cụ thể cho Admin Panel (Sidebar, Navigation)

**Bao gồm:**
- Admin Layout (admin-layout, admin-sidebar, admin-main)
- Sidebar Components (sidebar-logo, sidebar-nav, nav-item)
- Navigation Menu (nav-link, nav-parent, submenu)
- Page Components (page-header, stats-card, table-card)
- Buttons & Actions (btn-primary, btn-export, btn-edit, btn-delete)
- Status Badges (status-pending, status-completed, etc.)
- Bulk Action Bar
- Filters Section
- Pagination
- Responsive Design cho Admin

**Phạm vi:** Admin Only - CHỈ áp dụng cho trang admin

---

### 4. **`admin-design-system.css`** 🆕 - Design System
**Mục đích:** Hệ thống thiết kế THỐNG NHẤT cho tất cả trang admin

**Bao gồm:**
- **Design Tokens (CSS Variables):**
  - Colors: Primary, Secondary, Gradients
  - Shadows: xs, sm, md, lg, xl, 2xl
  - Spacing: xs, sm, md, base, lg, xl, 2xl, 3xl
  - Border Radius: none, sm, md, lg, xl, 2xl, full
  - Typography: Font sizes, weights, line heights
  - Transitions: fast, normal, slow, bounce

- **Shared Admin Components:**
  - `.admin-page` - Root container cho tất cả admin pages
  - `.page-header` - Header với gradient đẹp
  - `.stats-grid` & `.stat-card` - Thống kê cards
  - `.filters-section` - Bộ lọc
  - `.table-container` & `.admin-table` - Tables
  - `.btn-*` - Buttons với design system
  - `.status-badge` - Status badges
  - `.pagination` - Phân trang
  - `.loading-container` - Loading states
  - `.empty-state` - Empty states
  - `.form-*` - Form elements

**Phạm vi:** Admin Only - Áp dụng cho TẤT CẢ trang admin

**Ưu điểm:**
✅ Consistent Design - Thiết kế nhất quán
✅ Easy Maintenance - Dễ bảo trì
✅ Scalable - Dễ mở rộng
✅ Reusable - Tái sử dụng cao

---

### 5. **`admin-dashboard-shared.css`** 🆕 - Dashboard Components
**Mục đích:** Components đẹp từ AdminDashboard để TÁI SỬ DỤNG cho các trang admin khác

**Bao gồm:**
- **Dashboard Header:**
  - `.dashboard-header` - Header với decoration circles
  - `.welcome-section` - Welcome message
  - `.header-icon` - Icon với glass morphism

- **Profile Card:**
  - `.profile-card` - Card với backdrop blur
  - `.profile-avatar` - Avatar với status indicator
  - `.profile-menu-dropdown` - Dropdown menu

- **Stats Overview (Enhanced):**
  - `.stats-overview` - Grid layout cho stats
  - `.stats-card` - Card với hover effects đẹp
  - `.stats-icon` - Icon với gradient shadows
  - `.stats-trend` - Trend indicator (up/down)

- **Charts:**
  - `.charts-grid` - Grid layout cho charts
  - `.chart-container` - Container cho chart
  - `.chart-header` - Chart header

- **Animations:**
  - `.animate-fade-in`
  - `.animate-slide-in`
  - `.animate-fade-up`
  - Delays: `.delay-1`, `.delay-2`, `.delay-3`

**Phạm vi:** Admin Only - Có thể dùng ở BẤT KỲ trang admin nào

**Cách sử dụng:**
```html
<!-- Bất kỳ trang admin nào cũng có thể dùng -->
<div class="admin-page">
  <!-- Dashboard Header đẹp -->
  <div class="dashboard-header">
    <div class="header-content">
      <div class="welcome-section">
        <div class="header-icon animate-fade-in">
          <i class="material-icons">shopping_cart</i>
        </div>
        <div class="welcome-text">
          <h1 class="welcome-title">
            <span class="gradient-text">Quản lý bán hàng</span>
          </h1>
          <p class="welcome-subtitle">Point of Sale System</p>
        </div>
      </div>
    </div>
  </div>

  <!-- Stats Overview đẹp -->
  <div class="stats-overview">
    <div class="stats-card revenue">
      <div class="stats-header">
        <div class="stats-icon revenue">
          <i class="material-icons">attach_money</i>
        </div>
        <div class="stats-info">
          <div class="stats-value">50,000,000₫</div>
          <div class="stats-label">Doanh thu hôm nay</div>
        </div>
      </div>
      <div class="stats-footer">
        <div class="stats-trend up">
          <i class="material-icons">trending_up</i>
          <span>+12.5%</span>
        </div>
        <span class="stats-period">So với hôm qua</span>
      </div>
    </div>
  </div>
</div>
```

---

## 🎯 **Scoped CSS trong Vue Components**

### ❓ **Tại sao AdminDashboard.vue có CSS riêng?**

```vue
<!-- AdminDashboard.vue -->
<template>
  <div class="admin-dashboard">
    <!-- Template content -->
  </div>
</template>

<style scoped>
/* CSS này CHỈ áp dụng cho AdminDashboard.vue */
.admin-dashboard {
  padding: 2rem;
}

.custom-component {
  background: red;
}
</style>
```

**`<style scoped>` hoạt động như thế nào?**
1. Vue thêm attribute riêng vào mỗi element: `data-v-7a7a37b1`
2. CSS được compile thành:
   ```css
   .admin-dashboard[data-v-7a7a37b1] {
     padding: 2rem;
   }
   ```
3. CSS CHỈ match với elements CÓ attribute đó

**Kết quả:**
- ✅ CSS không bị conflict giữa các components
- ❌ CSS không thể tái sử dụng cho components khác
- ❌ Code bị duplicate nhiều

---

## 🚀 **Best Practices - Nên Dùng Gì?**

### ✅ **Global CSS** (main.css, admin-design-system.css, admin-dashboard-shared.css)
**Khi nào dùng:**
- Components tái sử dụng (buttons, cards, forms)
- Design system (colors, spacing, typography)
- Layout components (header, sidebar, grid)
- Utility classes (text-center, d-flex, mb-1)

**Ưu điểm:**
- Tái sử dụng cao
- Dễ bảo trì
- Consistent design
- Nhẹ hơn (không duplicate code)

---

### ⚠️ **Scoped CSS** trong Vue Components
**Khi nào dùng:**
- CSS RẤT cụ thể cho component đó
- Không cần tái sử dụng ở nơi khác
- Override styles của component con

**Nhược điểm:**
- Không tái sử dụng được
- Dễ duplicate code
- Khó maintain khi project lớn

---

## 📝 **Quy Tắc Sử dụng**

### 1. **Luôn dùng Design System trước**
```html
<!-- ✅ TỐT - Dùng class từ design system -->
<div class="admin-page">
  <div class="page-header">
    <div class="header-content">
      <div class="title-section">
        <h1 class="page-title">Quản lý sản phẩm</h1>
      </div>
    </div>
  </div>

  <div class="stats-grid">
    <div class="stat-card success">
      <div class="stat-icon success">
        <i class="material-icons">shopping_cart</i>
      </div>
      <div class="stat-content">
        <div class="stat-value">1,234</div>
        <div class="stat-label">Tổng đơn hàng</div>
      </div>
    </div>
  </div>
</div>

<!-- ❌ TỆ - Tự tạo CSS riêng -->
<div class="my-custom-page">
  <div class="my-custom-header">
    <h1>Quản lý sản phẩm</h1>
  </div>
</div>

<style scoped>
.my-custom-page { padding: 2rem; }
.my-custom-header { background: purple; }
</style>
```

### 2. **Dùng Dashboard Components cho các trang đẹp**
```html
<!-- ✅ Dùng dashboard-header cho trang quan trọng -->
<div class="dashboard-header">
  <div class="header-decoration">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>
  </div>
  <div class="header-content">
    <div class="welcome-section">
      <div class="header-icon animate-fade-in">
        <i class="material-icons">analytics</i>
      </div>
      <div class="welcome-text">
        <h1 class="welcome-title">
          <span class="gradient-text">Báo cáo & Phân tích</span>
        </h1>
      </div>
    </div>
  </div>
</div>
```

### 3. **Chỉ dùng Scoped CSS khi THỰC SỰ cần thiết**
```vue
<style scoped>
/* ✅ OK - CSS rất cụ thể cho component này */
.special-animation-only-here {
  animation: customBounce 2s infinite;
}

/* ❌ TỆ - Nên dùng từ design system */
.btn-primary {
  background: blue;  /* Đã có trong design system rồi! */
}

.stat-card {
  padding: 1rem;  /* Đã có trong design system rồi! */
}
</style>
```

---

## 📊 **Tóm Tắt**

| File | Scope | Khi nào dùng | Tái sử dụng |
|------|-------|--------------|-------------|
| `main.css` | Global (All) | Foundation, Typography, Utilities | ⭐⭐⭐⭐⭐ |
| `components.css` | Global (All) | Shared UI Components | ⭐⭐⭐⭐⭐ |
| `admin-panel.css` | Global (Admin) | Admin Layout & Sidebar | ⭐⭐⭐⭐ |
| `admin-design-system.css` | Global (Admin) | Design System cho Admin | ⭐⭐⭐⭐⭐ |
| `admin-dashboard-shared.css` | Global (Admin) | Dashboard Components đẹp | ⭐⭐⭐⭐⭐ |
| `<style scoped>` | Component Only | CSS cực kỳ cụ thể | ⭐ |

---

## 🎯 **Kết Luận**

### ❓ **CSS của `/admin/dashboard` lấy từ đâu?**

**Trước đây:**
- AdminDashboard.vue có **1,300+ dòng CSS** trong `<style scoped>`
- CSS này **CHỈ áp dụng cho AdminDashboard.vue**
- Các trang admin khác **KHÔNG thể dùng** được

**Bây giờ:**
- Đã tách CSS đẹp của Dashboard ra file `admin-dashboard-shared.css`
- **TẤT CẢ trang admin** đều có thể dùng
- Tạo `admin-design-system.css` - Design System thống nhất
- **Consistent, Reusable, Maintainable** ✅

### 🚀 **Hành động tiếp theo:**
1. ✅ Import `admin-dashboard-shared.css` vào `main.js` (Đã xong)
2. ✅ Tất cả trang admin có class `admin-page` (Đã xong)
3. 🎯 Sử dụng components từ design system thay vì tự viết CSS
4. 🎯 Giảm CSS trong `<style scoped>` của các components

---

**Last Updated:** 2025-10-22  
**Maintainer:** Sneakery Store Development Team


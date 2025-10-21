# 🎨 Assets Directory Structure

Thư mục này chứa tất cả các tài nguyên tĩnh (static assets) cho ứng dụng Sneakery Store Frontend.

## 📁 Cấu trúc thư mục

```
assets/
├── images/              # Hình ảnh, logo, icons
│   └── logo.png        # Logo chính của Sneakery Store
│
└── styles/             # CSS Stylesheets
    ├── main.css        # CSS chính - Design tokens, typography, utilities
    ├── components.css  # CSS cho components tái sử dụng
    └── admin-panel.css # CSS chuyên dụng cho Admin Panel
```

## 📝 Hướng dẫn sử dụng

### 1. Images (Hình ảnh)

**Sử dụng trong Vue components:**
```vue
<template>
  <img src="@/assets/images/logo.png" alt="Logo" />
</template>
```

**Quy tắc đặt tên:**
- Sử dụng kebab-case: `product-banner.jpg`, `user-avatar.png`
- Tên rõ ràng, mô tả nội dung: `nike-air-max-90.jpg`, `hero-background.webp`

**Định dạng được khuyến nghị:**
- Logo/Icons: `.png`, `.svg`
- Photos: `.jpg`, `.webp`
- Animations: `.gif`, `.webp`

### 2. Styles (CSS)

**Import trong `main.js`:**
```javascript
import './assets/styles/main.css'
import './assets/styles/components.css'
import './assets/styles/admin-panel.css'
```

**CSS Files:**

#### `main.css`
- CSS Variables (Design Tokens)
- Typography styles
- Global reset
- Utility classes
- Animations & Keyframes

#### `components.css`
- Button components
- Card components
- Form components
- Badge, Alert, Modal components
- Reusable UI elements

#### `admin-panel.css`
- Admin layout (sidebar, header)
- Admin-specific components
- Admin stats cards, charts
- Admin tables, forms

## 🎯 Best Practices

### Thêm hình ảnh mới
1. Đặt file vào `images/`
2. Đặt tên rõ ràng, theo quy ước
3. Tối ưu kích thước trước khi thêm
4. Sử dụng WebP cho hình ảnh web hiện đại

### Thêm CSS mới
1. **Global styles** → `main.css`
2. **Component styles** → `components.css`
3. **Admin-only styles** → `admin-panel.css`
4. **Component-specific** → Scoped styles trong `.vue` file

### Tối ưu hóa
- ✅ Sử dụng CSS Variables thay vì hard-code màu sắc
- ✅ Tái sử dụng utility classes từ `main.css`
- ✅ Compress images trước khi deploy
- ✅ Lazy load images khi cần thiết

## 📦 Thêm thư mục mới (nếu cần)

```
assets/
├── images/
├── styles/
├── fonts/          # Custom web fonts
├── icons/          # SVG icons collection
└── videos/         # Video assets
```

## 🔗 Liên quan

- [Vue 3 Assets Handling](https://vitejs.dev/guide/assets.html)
- [CSS Architecture Guide](../README.md)
- [Component System](../../components/README.md)

---

**Last Updated:** 2025-10-22
**Maintainer:** Sneakery Store Development Team


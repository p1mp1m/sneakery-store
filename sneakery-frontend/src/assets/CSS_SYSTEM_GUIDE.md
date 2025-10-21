# 🎨 Sneakery Store - CSS System Guide

## Tổng quan về hệ thống CSS hiện đại

Hệ thống CSS của Sneakery Store được thiết kế theo **Design Token System** với cấu trúc module rõ ràng, dễ maintain và scale.

---

## 📁 Cấu trúc Files

```
src/assets/
├── main.css              # Global styles, variables, utilities
├── components.css        # Component-specific styles
├── logo.png             # Brand assets
└── CSS_SYSTEM_GUIDE.md  # Documentation này
```

---

## 🎯 1. Design Token System

### 1.1 CSS Variables (Design Tokens)

Tất cả styling đều sử dụng CSS Variables để đảm bảo consistency:

```css
/* ✅ Đúng */
.my-button {
  color: var(--primary-color);
  padding: var(--space-4);
  border-radius: var(--radius-md);
}

/* ❌ Sai */
.my-button {
  color: #667eea;
  padding: 16px;
  border-radius: 8px;
}
```

### 1.2 Color Palette

#### Brand Colors
```css
--primary-color: #667eea;        /* Màu chính */
--secondary-color: #718096;      /* Màu phụ */
--accent-color: #764ba2;         /* Màu nhấn */
```

#### Semantic Colors
```css
--success-color: #48bb78;        /* Thành công */
--warning-color: #ed8936;        /* Cảnh báo */
--error-color: #f56565;          /* Lỗi */
--info-color: #4299e1;           /* Thông tin */
```

#### Neutral Colors
```css
--gray-100 đến --gray-900        /* Các mức xám */
--white, --black                 /* Trắng, đen */
```

### 1.3 Spacing Scale

```css
--space-1: 0.25rem;    /* 4px  - Very tight */
--space-2: 0.5rem;     /* 8px  - Tight */
--space-3: 0.75rem;    /* 12px - Compact */
--space-4: 1rem;       /* 16px - Base */
--space-6: 1.5rem;     /* 24px - Comfortable */
--space-8: 2rem;       /* 32px - Spacious */
--space-12: 3rem;      /* 48px - Large gap */
```

**Sử dụng:**
```css
.card {
  padding: var(--space-6);      /* 24px */
  margin-bottom: var(--space-4); /* 16px */
  gap: var(--space-3);          /* 12px */
}
```

### 1.4 Typography

#### Font Families
```css
--font-sans: 'Inter', system-ui, sans-serif;
--font-mono: 'JetBrains Mono', monospace;
```

#### Font Sizes
```css
--text-xs: 0.75rem;    /* 12px */
--text-sm: 0.875rem;   /* 14px */
--text-base: 1rem;     /* 16px - Base */
--text-lg: 1.125rem;   /* 18px */
--text-xl: 1.25rem;    /* 20px */
--text-2xl: 1.5rem;    /* 24px */
--text-3xl: 1.875rem;  /* 30px */
--text-4xl: 2.25rem;   /* 36px */
```

#### Font Weights
```css
--font-light: 300;
--font-normal: 400;
--font-medium: 500;
--font-semibold: 600;
--font-bold: 700;
```

### 1.5 Shadows

```css
--shadow-sm: ...;      /* Subtle shadow */
--shadow-md: ...;      /* Medium shadow */
--shadow-lg: ...;      /* Large shadow */
--shadow-xl: ...;      /* Extra large */
--shadow-glow: ...;    /* Glow effect */
```

### 1.6 Border Radius

```css
--radius-sm: 0.375rem;   /* 6px  - Subtle */
--radius-md: 0.5rem;     /* 8px  - Standard */
--radius-lg: 0.75rem;    /* 12px - Large */
--radius-xl: 1rem;       /* 16px - Extra large */
--radius-full: 9999px;   /* Fully rounded */
```

### 1.7 Transitions

```css
--transition-fast: 150ms;    /* Quick interactions */
--transition-normal: 250ms;  /* Standard */
--transition-slow: 350ms;    /* Deliberate */
```

---

## 🧩 2. Component Classes

### 2.1 Button Components

#### Basic Button
```html
<button class="btn btn-primary">Primary Button</button>
<button class="btn btn-secondary">Secondary Button</button>
<button class="btn btn-outline">Outline Button</button>
<button class="btn btn-ghost">Ghost Button</button>
```

#### Button Sizes
```html
<button class="btn btn-primary btn-sm">Small</button>
<button class="btn btn-primary">Medium (default)</button>
<button class="btn btn-primary btn-lg">Large</button>
<button class="btn btn-primary btn-xl">Extra Large</button>
```

#### Button States
```html
<button class="btn btn-primary" disabled>Disabled</button>
<button class="btn btn-primary btn-loading">Loading...</button>
<button class="btn btn-primary btn-block">Full Width</button>
```

### 2.2 Card Components

```html
<div class="card">
  <div class="card-header">
    <h3 class="card-title">Card Title</h3>
    <p class="card-subtitle">Card Subtitle</p>
  </div>
  
  <div class="card-body">
    <p class="card-text">Card content goes here...</p>
  </div>
  
  <div class="card-footer">
    <button class="btn btn-primary">Action</button>
  </div>
</div>
```

### 2.3 Form Components

```html
<div class="form-group">
  <label class="form-label required">Email</label>
  <input 
    type="email" 
    class="form-control" 
    placeholder="Enter your email"
  >
  <span class="form-error">This field is required</span>
  <span class="form-help">We'll never share your email</span>
</div>
```

#### Form Control Sizes
```html
<input class="form-control form-control-sm" placeholder="Small">
<input class="form-control" placeholder="Medium (default)">
<input class="form-control form-control-lg" placeholder="Large">
```

#### Form States
```html
<input class="form-control is-valid" placeholder="Valid input">
<input class="form-control is-invalid" placeholder="Invalid input">
<input class="form-control" disabled placeholder="Disabled">
```

### 2.4 Badge Components

```html
<span class="badge badge-primary">Primary</span>
<span class="badge badge-success">Success</span>
<span class="badge badge-warning">Warning</span>
<span class="badge badge-error">Error</span>
<span class="badge badge-info">Info</span>
```

### 2.5 Alert Components

```html
<div class="alert alert-success">
  <div class="alert-icon">✓</div>
  <div class="alert-content">
    <div class="alert-title">Success!</div>
    <div class="alert-message">Your action completed successfully.</div>
  </div>
</div>
```

### 2.6 Loading Components

#### Spinner
```html
<div class="loading-spinner"></div>
<div class="loading-spinner loading-spinner-sm"></div>
<div class="loading-spinner loading-spinner-lg"></div>
```

#### Dots
```html
<div class="loading-dots">
  <span></span>
  <span></span>
  <span></span>
</div>
```

#### Skeleton
```html
<div class="skeleton skeleton-text"></div>
<div class="skeleton skeleton-circle" style="width: 40px; height: 40px;"></div>
```

### 2.7 Modal Components

```html
<div class="modal-overlay">
  <div class="modal modal-md">
    <div class="modal-header">
      <h3 class="modal-title">Modal Title</h3>
      <button class="modal-close">×</button>
    </div>
    
    <div class="modal-body">
      Modal content...
    </div>
    
    <div class="modal-footer">
      <button class="btn btn-outline">Cancel</button>
      <button class="btn btn-primary">Confirm</button>
    </div>
  </div>
</div>
```

---

## 🎨 3. Utility Classes

### 3.1 Text Utilities

```html
<!-- Alignment -->
<p class="text-center">Centered text</p>
<p class="text-left">Left aligned</p>
<p class="text-right">Right aligned</p>

<!-- Transform -->
<p class="text-uppercase">UPPERCASE</p>
<p class="text-lowercase">lowercase</p>
<p class="text-capitalize">Capitalized</p>

<!-- Weight -->
<p class="font-light">Light weight</p>
<p class="font-medium">Medium weight</p>
<p class="font-bold">Bold weight</p>

<!-- Size -->
<p class="text-xs">Extra small</p>
<p class="text-sm">Small</p>
<p class="text-base">Base size</p>
<p class="text-lg">Large</p>
<p class="text-xl">Extra large</p>

<!-- Color -->
<p class="text-primary">Primary color</p>
<p class="text-success">Success color</p>
<p class="text-error">Error color</p>
```

### 3.2 Background Utilities

```html
<div class="bg-primary">Primary background</div>
<div class="bg-secondary">Secondary background</div>
<div class="bg-success">Success background</div>
```

### 3.3 Shadow Utilities

```html
<div class="shadow-sm">Small shadow</div>
<div class="shadow-md">Medium shadow</div>
<div class="shadow-lg">Large shadow</div>
<div class="shadow-xl">Extra large shadow</div>
```

### 3.4 Border Radius Utilities

```html
<div class="rounded-sm">Small radius</div>
<div class="rounded-md">Medium radius</div>
<div class="rounded-lg">Large radius</div>
<div class="rounded-xl">Extra large radius</div>
<div class="rounded-full">Fully rounded</div>
```

---

## 🎬 4. Animation Classes

```html
<div class="animate-fade-in">Fade in animation</div>
<div class="animate-slide-in">Slide in animation</div>
<div class="animate-pulse">Pulse animation</div>
<div class="animate-bounce">Bounce animation</div>
<div class="animate-spin">Spin animation (loading)</div>
```

---

## 📱 5. Responsive Design

### 5.1 Breakpoints

```css
/* Mobile First Approach */
/* Base: Mobile (< 640px) */
@media (min-width: 640px) { /* sm - Small tablets */ }
@media (min-width: 768px) { /* md - Tablets */ }
@media (min-width: 1024px) { /* lg - Laptops */ }
@media (min-width: 1280px) { /* xl - Desktops */ }
```

### 5.2 Container Sizes

```html
<div class="container">Default (1200px max)</div>
<div class="container container-sm">Small (640px)</div>
<div class="container container-md">Medium (768px)</div>
<div class="container container-lg">Large (1024px)</div>
<div class="container container-xl">Extra Large (1280px)</div>
<div class="container container-fluid">Full width</div>
```

---

## 🌙 6. Dark Mode Support

Hệ thống tự động detect dark mode preference:

```css
@media (prefers-color-scheme: dark) {
  /* Tự động áp dụng dark theme */
}
```

---

## ♿ 7. Accessibility

### 7.1 Focus States

Tất cả interactive elements đều có focus states:

```css
*:focus-visible {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}
```

### 7.2 Reduced Motion

Hỗ trợ người dùng cần giảm animation:

```css
@media (prefers-reduced-motion: reduce) {
  /* Animations disabled */
}
```

### 7.3 Screen Reader Only

```html
<span class="sr-only">This text is only for screen readers</span>
```

---

## 📝 8. Best Practices

### ✅ DO's

1. **Luôn sử dụng CSS Variables**
   ```css
   color: var(--primary-color);
   ```

2. **Sử dụng spacing scale**
   ```css
   padding: var(--space-4);
   ```

3. **Tận dụng utility classes**
   ```html
   <div class="text-center font-bold text-primary"></div>
   ```

4. **Comment rõ ràng**
   ```css
   /* Layout */
   display: flex;
   
   /* Visual */
   background: white;
   ```

5. **Nhóm properties theo logic**
   ```css
   .button {
     /* Layout */
     display: flex;
     align-items: center;
     
     /* Spacing */
     padding: var(--space-3);
     
     /* Visual */
     background: var(--primary-color);
     border-radius: var(--radius-md);
     
     /* Effects */
     transition: all var(--transition-fast);
   }
   ```

### ❌ DON'Ts

1. **Đừng hardcode values**
   ```css
   /* ❌ Bad */
   padding: 16px;
   
   /* ✅ Good */
   padding: var(--space-4);
   ```

2. **Đừng dùng !important trừ khi thực sự cần**
   ```css
   /* ❌ Bad */
   color: red !important;
   
   /* ✅ Good - Increase specificity */
   .component .element {
     color: var(--error-color);
   }
   ```

3. **Đừng lặp lại code**
   ```css
   /* ❌ Bad */
   .button-1 {
     padding: 12px 24px;
     border-radius: 8px;
   }
   .button-2 {
     padding: 12px 24px;
     border-radius: 8px;
   }
   
   /* ✅ Good */
   .btn {
     padding: var(--space-3) var(--space-6);
     border-radius: var(--radius-md);
   }
   ```

---

## 🔧 9. Customization

### 9.1 Override Variables

Tạo file custom CSS và override variables:

```css
/* custom.css */
:root {
  --primary-color: #your-brand-color;
  --font-sans: 'Your-Font', sans-serif;
}
```

### 9.2 Extend Components

```css
/* Extend existing button */
.btn-custom {
  /* Inherit from .btn */
  @extend .btn;
  
  /* Add custom styles */
  background: linear-gradient(to right, #ff0000, #00ff00);
}
```

---

## 📦 10. Component Example

### Tạo Component mới tuân theo conventions:

```vue
<template>
  <div class="my-component">
    <h2 class="my-component__title">Title</h2>
    <p class="my-component__text">Content</p>
    <button class="my-component__button btn btn-primary">
      Action
    </button>
  </div>
</template>

<style scoped>
/* ═══════════════════════════════════════════════════════════════════════
   🎯 MY COMPONENT
   Component description here
   ═══════════════════════════════════════════════════════════════════════ */

/* ─────────────────────────────────────────────────────────────────────
   1. Container
   ───────────────────────────────────────────────────────────────────── */
.my-component {
  /* Layout */
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  
  /* Spacing */
  padding: var(--space-6);
  
  /* Visual */
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  
  /* Effects */
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

/* ─────────────────────────────────────────────────────────────────────
   2. Elements
   ───────────────────────────────────────────────────────────────────── */
.my-component__title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
}

.my-component__text {
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
}

/* ═══════════════════════════════════════════════════════════════════════
   📱 RESPONSIVE
   ═══════════════════════════════════════════════════════════════════════ */
@media (max-width: 768px) {
  .my-component {
    padding: var(--space-4);
  }
}
</style>
```

---

## 📚 Resources

- [CSS Custom Properties (MDN)](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties)
- [CSS Grid Layout Guide](https://css-tricks.com/snippets/css/complete-guide-grid/)
- [Flexbox Guide](https://css-tricks.com/snippets/css/a-guide-to-flexbox/)
- [BEM Methodology](http://getbem.com/)

---

## 💡 Tips & Tricks

1. **Use DevTools để kiểm tra CSS Variables**
   - Mở DevTools → Styles panel
   - Xem computed values của CSS variables

2. **Group related properties**
   - Layout → Spacing → Visual → Effects

3. **Comment bằng tiếng Việt**
   - Dễ hiểu cho team

4. **Sử dụng emoji trong comments**
   - 🎨 Colors
   - 📏 Spacing
   - 🔤 Typography
   - 📱 Responsive

---

**Cập nhật lần cuối:** 2024
**Tác giả:** Sneakery Store Team


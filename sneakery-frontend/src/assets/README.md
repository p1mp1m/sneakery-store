# 🎨 Sneakery Store - CSS Design System

> Modern, scalable, and maintainable CSS architecture với Design Token System

---

## 📁 Files trong thư mục này

```
src/assets/
├── main.css                      # Global styles & variables
├── components.css                # Reusable component classes
├── logo.png                      # Brand assets
├── README.md                     # File này
├── CSS_SYSTEM_GUIDE.md          # 📚 Complete documentation
└── CSS_REFACTOR_CHANGELOG.md    # 📝 Changelog & migration guide
```

---

## 🚀 Quick Start

### 1. Import CSS files

```js
// main.js
import './assets/main.css'
import './assets/components.css'
```

### 2. Sử dụng CSS Variables

```css
.my-element {
  color: var(--primary-color);
  padding: var(--space-4);
  border-radius: var(--radius-md);
}
```

### 3. Sử dụng Component Classes

```html
<button class="btn btn-primary">Click me</button>
<div class="card">
  <div class="card-body">Content</div>
</div>
```

### 4. Sử dụng Utility Classes

```html
<div class="text-center font-bold text-primary">
  Centered, bold, primary text
</div>
```

---

## 📚 Documentation

| File | Mục đích |
|------|----------|
| **`CSS_SYSTEM_GUIDE.md`** | 📖 Complete usage guide, examples, best practices |
| **`CSS_REFACTOR_CHANGELOG.md`** | 📝 All changes, migration guide, metrics |

### 👉 **ĐỌC GUIDE TRƯỚC KHI SỬ DỤNG:** [`CSS_SYSTEM_GUIDE.md`](./CSS_SYSTEM_GUIDE.md)

---

## 🎯 Highlights

### ✨ Design Token System

- **100+ CSS Variables** cho colors, spacing, typography, shadows, etc.
- **Consistent & scalable** design language
- **Easy theming** - chỉ cần override variables

### 🧩 Component Library

- **50+ component classes** ready to use
- Buttons, Cards, Forms, Modals, Alerts, Loading, etc.
- **Consistent styling** across the app

### 🎨 Utility Classes

- **Comprehensive utilities** for rapid development
- Text, Colors, Spacing, Shadows, Borders, etc.
- **Mobile-first** responsive design

### ♿ Accessibility First

- WCAG 2.1 compliant
- Focus management
- Reduced motion support
- High contrast mode
- Screen reader friendly

### 🌙 Dark Mode

- Auto detect system preference
- Smooth transitions
- Optimized color palette

---

## 💡 Quick Examples

### Button Examples

```html
<!-- Variants -->
<button class="btn btn-primary">Primary</button>
<button class="btn btn-secondary">Secondary</button>
<button class="btn btn-outline">Outline</button>
<button class="btn btn-ghost">Ghost</button>

<!-- Sizes -->
<button class="btn btn-primary btn-sm">Small</button>
<button class="btn btn-primary btn-lg">Large</button>

<!-- States -->
<button class="btn btn-primary" disabled>Disabled</button>
<button class="btn btn-primary btn-loading">Loading</button>
```

### Card Example

```html
<div class="card">
  <div class="card-header">
    <h3 class="card-title">Product Name</h3>
  </div>
  <div class="card-body">
    <p class="card-text">Product description...</p>
  </div>
  <div class="card-footer">
    <button class="btn btn-primary">Buy Now</button>
  </div>
</div>
```

### Form Example

```html
<div class="form-group">
  <label class="form-label required">Email</label>
  <input type="email" class="form-control" placeholder="Enter email">
  <span class="form-error">This field is required</span>
</div>
```

---

## 🎨 CSS Variables Categories

| Category | Example Variables | Count |
|----------|------------------|-------|
| **Colors** | `--primary-color`, `--success-color` | 30+ |
| **Spacing** | `--space-1` to `--space-32` | 13 |
| **Typography** | `--text-xs` to `--text-6xl` | 10 |
| **Font Weights** | `--font-light` to `--font-black` | 8 |
| **Shadows** | `--shadow-sm` to `--shadow-2xl` | 7 |
| **Radius** | `--radius-sm` to `--radius-full` | 7 |
| **Transitions** | `--transition-fast/normal/slow` | 6 |
| **Z-Index** | `--z-dropdown` to `--z-toast` | 16 |

---

## 📱 Responsive Breakpoints

```css
/* Mobile First */
Base:     < 640px   (Mobile)
sm:       640px+    (Small tablets)
md:       768px+    (Tablets)
lg:       1024px+   (Laptops)
xl:       1280px+   (Desktops)
```

---

## 🔥 Best Practices

### ✅ DO's

```css
/* ✅ Use CSS Variables */
color: var(--primary-color);
padding: var(--space-4);

/* ✅ Use component classes */
<button class="btn btn-primary">

/* ✅ Use utility classes */
<div class="text-center font-bold">
```

### ❌ DON'Ts

```css
/* ❌ Don't hardcode values */
color: #667eea;
padding: 16px;

/* ❌ Don't use inline styles */
<button style="padding: 16px;">

/* ❌ Don't duplicate CSS */
/* Create reusable classes instead */
```

---

## 🛠️ Development Workflow

### 1. Tạo Component Mới

```vue
<template>
  <div class="my-component">
    <!-- Use existing component classes -->
    <button class="btn btn-primary">Action</button>
  </div>
</template>

<style scoped>
.my-component {
  /* Use CSS Variables */
  padding: var(--space-6);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
}
</style>
```

### 2. Style Override

```vue
<style scoped>
/* Override component classes */
.btn-primary {
  /* Customize for this component */
  background: linear-gradient(to right, red, blue);
}
</style>
```

### 3. Theme Customization

```css
/* Create theme-custom.css */
:root {
  --primary-color: #your-brand-color;
  --font-sans: 'Your-Font', sans-serif;
}
```

---

## 📖 Resources

| Resource | Link |
|----------|------|
| **Complete Guide** | [`CSS_SYSTEM_GUIDE.md`](./CSS_SYSTEM_GUIDE.md) |
| **Changelog** | [`CSS_REFACTOR_CHANGELOG.md`](../CSS_REFACTOR_CHANGELOG.md) |
| **MDN CSS Variables** | [MDN Docs](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties) |
| **CSS Grid Guide** | [CSS Tricks](https://css-tricks.com/snippets/css/complete-guide-grid/) |
| **Flexbox Guide** | [CSS Tricks](https://css-tricks.com/snippets/css/a-guide-to-flexbox/) |

---

## 🎯 Tips

1. **Use DevTools** - Inspect và xem computed CSS variables
2. **Follow conventions** - Đọc guide trước khi code
3. **Reuse existing classes** - Đừng tạo mới khi đã có sẵn
4. **Comment your code** - Giúp team dễ hiểu
5. **Test responsive** - Luôn test trên nhiều devices

---

## 🤝 Support

- 📖 Đọc [`CSS_SYSTEM_GUIDE.md`](./CSS_SYSTEM_GUIDE.md)
- 🔍 Check examples trong `ProductCard.vue`
- 💬 Hỏi team nếu cần

---

## 📊 Stats

```
✅ 100+ CSS Variables
✅ 50+ Component Classes  
✅ Comprehensive Utilities
✅ Full Accessibility Support
✅ Dark Mode Ready
✅ Mobile-First Responsive
✅ Performance Optimized
```

---

**Last Updated:** 2024  
**Version:** 2.0.0  
**Status:** ✅ Production Ready

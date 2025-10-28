# 🎨 Sneakery Frontend

> **Frontend sử dụng Vue.js 3.5 + Vite cho dự án Sneakery Store**

---

## 📋 Mục lục

1. [Giới thiệu](#giới-thiệu)
2. [Công nghệ](#công-nghệ)
3. [Cấu trúc dự án](#cấu-trúc-dự-án)
4. [Cài đặt](#cài-đặt)
5. [Chạy ứng dụng](#chạy-ứng-dụng)
6. [Pages & Routes](#pages--routes)
7. [Components](#components)
8. [State Management](#state-management)
9. [Styling](#styling)

---

## 🎯 Giới thiệu

**Sneakery Frontend** là single-page application (SPA) được xây dựng với Vue.js, cung cấp:

- 🏠 **User Interface** - Giao diện người dùng thân thiện
- 🛡️ **Admin Panel** - Quản trị toàn diện (22 admin pages)
- 🛒 **E-commerce** - Mua sắm, giỏ hàng, thanh toán
- 📱 **Responsive** - Hoạt động tốt trên mọi thiết bị
- 🌙 **Dark Theme** - Giao diện tối ưu cho mắt

---

## 🛠️ Công nghệ

### Core Framework
- **Vue.js 3.5** - Progressive JavaScript Framework
- **Vite 4.x** - Next Generation Build Tool

### State Management
- **Pinia** - State management (official)

### Routing
- **Vue Router 4** - Client-side routing

### HTTP Client
- **Axios** - Promise-based HTTP client

### Styling
- **Custom CSS** - Design system với CSS variables
- **Flexbox & Grid** - Modern layout

### Utilities
- **Composition API** - Modern Vue syntax
- **ES6+** - Modern JavaScript

---

## 📁 Cấu trúc dự án

```
sneakery-frontend/
├── src/
│   ├── views/              ← Pages (Admin, User, Common)
│   ├── components/         ← Global components
│   ├── assets/             ← Static assets
│   │   ├── components/     ← Reusable components
│   │   └── styles/         ← CSS design system
│   ├── stores/             ← Pinia stores
│   ├── services/           ← API services
│   ├── routers/            ← Route configuration
│   ├── composables/        ← Composable functions
│   └── utils/              ← Utility functions
├── public/                 ← Public assets
├── index.html              ← Entry HTML
└── vite.config.js          ← Vite configuration
```

### Views Structure

```
views/
├── admin/          22 admin pages (Dashboard, Products, Orders...)
├── user/           6 user pages (Profile, Orders, Cart...)
└── common/         5 common pages (Home, Login, Products...)
```

---

## ⚙️ Cài đặt

### Yêu cầu hệ thống

- 📦 Node.js 18+ 
- 📦 npm hoặc yarn

### Bước 1: Clone repository

```bash
git clone https://github.com/p1mp1m/sneakery-store
cd sneakery-store/sneakery-frontend
```

### Bước 2: Cài đặt dependencies

```bash
npm install
```

⏳ **Chờ 2-5 phút** để npm tải packages...

---

## 🎮 Chạy ứng dụng

### Development Mode

```bash
npm run dev
```

Website sẽ chạy tại: **http://localhost:5173**

### Build cho Production

```bash
npm run build
```

Output sẽ ở thư mục `dist/`

### Preview Production Build

```bash
npm run preview
```

---

## 🗺️ Pages & Routes

### Admin Pages (22 pages)

| Route | Page | Mô tả |
|-------|------|-------|
| `/admin` | AdminDashboard | Dashboard với thống kê |
| `/admin/products` | AdminProducts | Quản lý sản phẩm |
| `/admin/product-variants` | AdminProductVariants | Quản lý variants |
| `/admin/orders` | AdminOrders | Quản lý đơn hàng |
| `/admin/users` | AdminUsers | Quản lý users |
| `/admin/brands` | AdminBrands | Quản lý thương hiệu |
| `/admin/categories` | AdminCategories | Quản lý danh mục |
| `/admin/coupons` | AdminCoupons | Quản lý mã giảm giá |
| `/admin/flash-sales` | AdminFlashSales | Quản lý flash sale |
| `/admin/reviews` | AdminReviews | Quản lý đánh giá |
| `/admin/notifications` | AdminNotifications | Quản lý thông báo |
| `/admin/inventory` | AdminInventory | Quản lý kho |
| `/admin/payments` | AdminPayments | Quản lý thanh toán |
| `/admin/returns` | AdminReturns | Quản lý đổi trả |
| `/admin/warranty` | AdminWarranty | Quản lý bảo hành |
| `/admin/loyalty` | AdminLoyalty | Quản lý loyalty |
| `/admin/settings` | AdminSettings | Cài đặt |
| `/admin/analytics` | AdminAnalytics | Phân tích |
| `/admin/activity-logs` | AdminActivityLogs | Log hoạt động |
| `/admin/email-templates` | AdminEmailTemplates | Email templates |
| `/admin/discounts` | AdminDiscounts | Quản lý giảm giá |
| `/admin/sales` | AdminSales | Quản lý sale |

### User Pages (6 pages)

| Route | Page | Mô tả |
|-------|------|-------|
| `/user/dashboard` | UserDashboard | Dashboard user |
| `/user/profile` | UserProfile | Thông tin cá nhân |
| `/user/orders` | UserOrders | Đơn hàng của tôi |
| `/user/cart` | CartPage | Giỏ hàng |
| `/user/checkout` | CheckoutPage | Thanh toán |
| `/user/wishlist` | WishlistPage | Danh sách yêu thích |

### Common Pages (5 pages)

| Route | Page | Mô tả |
|-------|------|-------|
| `/` | HomePage | Trang chủ |
| `/login` | LoginPage | Đăng nhập |
| `/register` | RegisterPage | Đăng ký |
| `/products` | ProductListPage | Danh sách sản phẩm |
| `/products/:id` | ProductDetailPage | Chi tiết sản phẩm |

---

## 🧩 Components

### Reusable Components

#### Admin Components
- `StatsCard.vue` - Card hiển thị thống kê
- `StatusBadge.vue` - Badge trạng thái
- `LoadingSkeleton.vue` - Loading animation
- `EmptyState.vue` - Empty state
- `FilterBar.vue` - Filter & search
- `Pagination.vue` - Pagination
- `VariantModal.vue` - Modal quản lý variants
- `BulkActions.vue` - Bulk operations
- `ColumnToggle.vue` - Toggle columns

#### Common Components
- `ProductCard.vue` - Card sản phẩm
- `ImageUpload.vue` - Upload hình ảnh
- `ConfirmDialog.vue` - Dialog xác nhận
- `NotificationDropdown.vue` - Dropdown thông báo
- `WishlistButton.vue` - Button yêu thích
- `FlashSaleBadge.vue` - Badge flash sale

#### Charts
- `BarChart.vue` - Biểu đồ cột
- `LineChart.vue` - Biểu đồ đường
- `DoughnutChart.vue` - Biểu đồ tròn

### Global Components
- `FullScreenSpinner.vue` - Loading screen
- `ToastContainer.vue` - Toast notifications

---

## 🗄️ State Management (Pinia Stores)

### Stores

| Store | Mục đích |
|-------|----------|
| `auth.js` | User authentication state |
| `admin.js` | Admin data & state |
| `flashSale.js` | Flash sale data |
| `loyalty.js` | Loyalty points |
| `notification.js` | Notifications |
| `wishlist.js` | Wishlist state |

### Example Usage

```javascript
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

// Login
await authStore.login(email, password);

// Access state
const currentUser = authStore.currentUser;
const isAuthenticated = authStore.isAuthenticated;
```

---

## 🎨 Styling

### Design System Architecture

```
src/assets/styles/
├── 01-settings/          Design tokens, variables
│   ├── _theme-unified.css
│   ├── _tokens.css
│   └── _variables.css
├── 02-base/              Base styles
│   ├── _reset.css
│   ├── _typography.css
│   └── _animations.css
├── 03-layout/            Layout utilities
│   ├── _containers.css
│   └── _grid.css
├── 04-components/        Component styles
│   ├── _buttons.css
│   ├── _forms.css
│   ├── _tables.css
│   └── _modals.css
├── 05-admin/             Admin specific
│   ├── _admin-dashboard.css
│   ├── _admin-forms.css
│   └── _admin-tables.css
├── Posted-user/          User specific
│   └── _user-products.css
└── 07-themes/            Theme definitions
    └── _dark-theme.css
```

### CSS Variables

```css
:root {
  /* Colors */
  --primary-color: #a78bfa;
  --secondary-color: #8b5cf6;
  
  /* Spacing */
  --space-1: 4px;
  --space-2: 8px;
  
  /* Typography */
  --font-family: 'Inter', sans-serif;
  --text-base: 16px;
}
```

### Responsive Breakpoints

```css
/* Mobile: 0-480px */
/* Tablet: 481-768px */
@media (max-width: 768px) { ... }

/* Desktop: 769px+ */
```

---

## 🔌 API Integration

### Services

| Service | Mục đích |
|---------|----------|
| `authService.js` | Authentication API |
| `productService.js` | Product API |
| `adminService.js` | Admin API |
| `flashSaleService.js` | Flash sale API |
| `loyaltyService.js` | Loyalty API |
| `notificationService.js` | Notification API |
| `wishlistService.js` | Wishlist API |

### Example API Call

```javascript
import productService from '@/services/productService';

// Get products
const response = await productService.getProducts({
  page: 0,
  size: 10,
  brand: 'Nike'
});

// Get product by id
const product = await productService.getProductById(1);
```

---

## 🛡️ Authentication

### Login Flow

1. User nhập email/password
2. Call API `/api/auth/login`
3. Lưu JWT token vào localStorage
4. Redirect dựa vào role:
   - Admin → `/admin`
   - User → `/user/dashboard`

### Token Management

```javascript
// Store token
localStorage.setItem('token', response.data.token);

// Add to headers
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

// HTTPS Only
axios.defaults.withCredentials = true;
```

---

## 🚀 Performance Optimization

### Code Splitting

Routes được lazy-loaded:
```javascript
{
  path: '/admin',
  component: () => import('@/views/admin/AdminDashboard.vue')
}
```

### Image Optimization

- Lazy loading cho images
- Responsive images
- Optimized file formats

---

## 🧪 Testing

### Unit Tests

```bash
npm run test:unit
```

### E2E Tests

```bash
npm run test:e2e
```

### Manual Testing Checklist

- [ ] Login/Logout
- [ ] Navigation giữa các trang
- [ ] CRUD operations
- [ ] Form validation
- [ ] Responsive design
- [ ] Error handling

---

## 🔧 Development Tools

### VS Code Extensions

- **Vetur** - Vue.js support
- **ESLint** - Code linting
- **Prettier** - Code formatting
- **Volar** - Vue language support

### Browser DevTools

- Vue DevTools extension
- React DevTools (cho debugging state)

---

## 🚨 Troubleshooting

### npm install fails

```bash
# Clear cache
npm cache clean --force

# Delete node_modules
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

### Port 5173 in use

```bash
# Kill process
netstat -ano | findstr :5173
taskkill /PID <PID> /F

# Or change port in vite.config.js
server: { port: 3000 }
```

### Cannot connect to backend

**Lỗi:** `Network Error`

**Sửa:**
1. Kiểm tra backend đã chạy chưa
2. Kiểm tra CORS configuration
3. Xóa browser cache

---

## 📝 Code Guidelines

### Vue 3 Composition API

```javascript
<script setup>
import { ref, computed, onMounted } from 'vue';

const count = ref(0);
const doubleCount = computed(() => count.value * 2);

onMounted(() => {
  console.log('Component mounted');
});
</script>
```

### Naming Conventions

- **Components**: PascalCase (`ProductCard.vue`)
- **Files**: kebab-case (`user-dashboard.vue`)
- **Functions**: camelCase (`getProducts()`)
- **Constants**: UPPER_SNAKE_CASE (`API_BASE_URL`)

### File Structure

```vue
<template>
  <!-- HTML Template -->
</template>

<script setup>
  // JavaScript Logic
</script>

<style scoped>
  /* Component Styles */
</style>
```

---

## 📚 Resources

- [Vue.js Documentation](https://vuejs.org/)
- [Vite Documentation](https://vitejs.dev/)
- [Pinia Documentation](https://pinia.vuejs.org/)
- [Vue Router](https://router.vuejs.org/)
- [Axios](https://axios-http.com/)

---

## 📞 Support

Gặp vấn đề?
1. Kiểm tra phần Troubleshooting
2. Xem console errors
3. Liên hệ: pombie789456123@gmail.com

---

<div align="center">

**Made with ❤️ by Sneakery Frontend Team**

</div>


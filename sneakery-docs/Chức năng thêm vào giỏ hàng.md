# 🛒 Implementation: Thêm Sản Phẩm Vào Giỏ Hàng

## ✅ Đã Hoàn Thành

### 1. Cart Store (Mới)
**File**: `src/stores/cart.js`

Tạo cart store để quản lý state của giỏ hàng tập trung:
- ✅ Quản lý cart state (items, count, subTotal)
- ✅ Hỗ trợ cả user cart và guest cart
- ✅ Methods: `fetchCart()`, `addItem()`, `removeItem()`, `updateQuantity()`, `clearCart()`
- ✅ Computed: `cartItems`, `cartCount`, `cartSubTotal`, `isEmpty`
- ✅ Helper methods: `isInCart()`, `getItemQuantity()`

### 2. ProductCard Component
**File**: `src/assets/components/products/ProductCard.vue`

✅ **Đã implement đầy đủ:**
- ✅ Thêm vào giỏ hàng từ ProductCard
- ✅ Kiểm tra stock và variants
- ✅ Tự động redirect đến trang chi tiết nếu có nhiều variants
- ✅ Hỗ trợ cả user cart và guest cart
- ✅ Loading state khi đang thêm vào giỏ
- ✅ Disable button khi không thể thêm
- ✅ Toast notifications
- ✅ Error handling với redirect đến login nếu cần

**Logic:**
1. Kiểm tra sản phẩm có thể thêm vào giỏ hàng (stock > 0)
2. Nếu có nhiều variants (> 1), redirect đến trang chi tiết để chọn
3. Lấy variant đầu tiên có stock > 0
4. Sử dụng cart store để thêm vào giỏ hàng
5. Hiển thị toast notification
6. Tự động refresh cart

### 3. ProductDetailPage
**File**: `src/views/common/ProductDetailPage.vue`

✅ **Đã cập nhật:**
- ✅ Sử dụng cart store thay vì axios trực tiếp
- ✅ Cải thiện error handling
- ✅ Tự động redirect đến login nếu cần

### 4. QuickViewModal
**File**: `src/assets/components/common/QuickViewModal.vue`

✅ **Đã cập nhật:**
- ✅ Sử dụng cart store
- ✅ Cải thiện error handling
- ✅ Tự động redirect đến login nếu cần

### 5. CartPage
**File**: `src/views/user/CartPage.vue`

✅ **Đã cập nhật:**
- ✅ Sử dụng cart store thay vì quản lý state trực tiếp
- ✅ Tự động sync với server
- ✅ Cải thiện error handling

### 6. DefaultLayout
**File**: `src/assets/layouts/DefaultLayout.vue`

✅ **Đã cập nhật:**
- ✅ Hiển thị cart count badge trên header
- ✅ Tự động load cart khi mount
- ✅ Accessibility improvements (aria-label, sr-only)

## 🎯 Tính Năng

### 1. Thêm Vào Giỏ Hàng Từ ProductCard
- **Nếu sản phẩm có 1 variant**: Thêm trực tiếp vào giỏ hàng
- **Nếu sản phẩm có nhiều variants**: Redirect đến trang chi tiết để chọn
- **Nếu chưa đăng nhập**: Sử dụng guest cart
- **Nếu đã đăng nhập**: Sử dụng user cart

### 2. Thêm Vào Giỏ Hàng Từ ProductDetailPage
- Chọn màu sắc và size
- Chọn số lượng
- Thêm vào giỏ hàng
- Có nút "Mua ngay" để thêm và redirect đến checkout

### 3. Quản Lý Giỏ Hàng
- Xem giỏ hàng: `/cart`
- Cập nhật số lượng
- Xóa sản phẩm
- Áp dụng mã giảm giá
- Tính toán phí vận chuyển

### 4. Cart Count Badge
- Hiển thị số lượng sản phẩm trong giỏ hàng trên header
- Tự động cập nhật khi thêm/xóa sản phẩm
- Hiển thị "99+" nếu > 99 sản phẩm

## 🔧 Cách Sử Dụng

### Trong Component

```javascript
import { useCartStore } from '@/stores/cart';

const cartStore = useCartStore();

// Thêm sản phẩm vào giỏ hàng
await cartStore.addItem(variantId, quantity);

// Xóa sản phẩm khỏi giỏ hàng
await cartStore.removeItem(variantId);

// Cập nhật số lượng
await cartStore.updateQuantity(variantId, newQuantity);

// Lấy cart count
const count = cartStore.cartCount;

// Kiểm tra sản phẩm có trong giỏ hàng không
const inCart = cartStore.isInCart(variantId);
```

### Trong Template

```vue
<template>
  <!-- Hiển thị cart count -->
  <span>{{ cartStore.cartCount }}</span>
  
  <!-- Kiểm tra empty -->
  <div v-if="cartStore.isEmpty">Giỏ hàng trống</div>
  
  <!-- Hiển thị items -->
  <div v-for="item in cartStore.cartItems" :key="item.id">
    {{ item.productName }}
  </div>
</template>
```

## 📋 API Endpoints

Cart store tự động sử dụng các endpoints sau:

**User Cart** (khi đã đăng nhập):
- `GET /api/cart` - Lấy giỏ hàng
- `POST /api/cart/item` - Thêm sản phẩm
- `DELETE /api/cart/item/{variantId}` - Xóa sản phẩm

**Guest Cart** (khi chưa đăng nhập):
- `GET /api/guest/cart?sessionId={sessionId}` - Lấy giỏ hàng
- `POST /api/guest/cart/item?sessionId={sessionId}` - Thêm sản phẩm
- `DELETE /api/guest/cart/item/{variantId}?sessionId={sessionId}` - Xóa sản phẩm

## 🎨 UI/UX Improvements

1. **Loading States**: Hiển thị spinner khi đang thêm vào giỏ
2. **Disabled States**: Disable button khi không thể thêm
3. **Toast Notifications**: Thông báo thành công/lỗi
4. **Cart Count Badge**: Hiển thị số lượng trên header
5. **Error Handling**: Tự động redirect đến login nếu cần
6. **Accessibility**: Thêm aria-labels và sr-only text

## 🚀 Next Steps

1. **Test chức năng**: Test thêm sản phẩm vào giỏ hàng
2. **Test guest cart**: Test với user chưa đăng nhập
3. **Test user cart**: Test với user đã đăng nhập
4. **Test variants**: Test với sản phẩm có nhiều variants
5. **Test error cases**: Test các trường hợp lỗi

## 📝 Notes

- Cart store tự động xử lý cả user cart và guest cart
- Cart count badge tự động cập nhật khi thêm/xóa sản phẩm
- Tất cả các component đã được cập nhật để sử dụng cart store
- Code đã được tối ưu và cải thiện error handling


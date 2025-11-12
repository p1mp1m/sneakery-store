# 🧪 Hướng dẫn kiểm tra Toast Notification System

## ✅ Kiểm tra nhanh

Toast notification system đã được tích hợp vào ứng dụng. Để kiểm tra xem nó hoạt động:

### 1. Chạy ứng dụng

```bash
cd sneakery-frontend
npm run dev
```

### 2. Mở Browser Console (F12)

Trong development mode, `toastService` đã được expose vào `window`, bạn có thể test trực tiếp:

```javascript
// Test Success Toast
toastService.success('Thành công!', 'Toast notification đã hoạt động thành công!')

// Test Error Toast
toastService.error('Lỗi!', 'Đây là thông báo lỗi')

// Test Warning Toast
toastService.warning('Cảnh báo!', 'Đây là thông báo cảnh báo')

// Test Info Toast
toastService.info('Thông tin', 'Đây là thông báo thông tin')
```

### 3. Test các tính năng nâng cao

#### Progress Bar với Pause/Resume
```javascript
// Hover vào toast để pause progress bar
toastService.info('Progress Test', 'Hover vào toast để pause progress bar', {
  duration: 10000
})
```

#### Action Buttons
```javascript
toastService.success('Có hành động', 'Toast với nút hành động', {
  duration: 10000,
  actions: [
    {
      label: 'Xem chi tiết',
      handler: () => {
        alert('Bạn đã click "Xem chi tiết"')
      },
      primary: true
    },
    {
      label: 'Hủy',
      handler: () => {
        console.log('Clicked: Hủy')
      }
    }
  ]
})
```

#### API Error Handling
```javascript
// Test API Error
toastService.apiError({
  response: {
    status: 404,
    data: { message: 'Không tìm thấy tài nguyên' }
  }
}, 'Lỗi khi tải dữ liệu')
```

#### Multiple Toasts
```javascript
// Test nhiều toasts cùng lúc
for (let i = 1; i <= 5; i++) {
  setTimeout(() => {
    toastService.info(`Toast ${i}`, `Đây là toast thứ ${i}`)
  }, i * 500)
}
```

### 4. Test trong Admin Panel

1. Đăng nhập vào admin panel
2. Thực hiện các thao tác như:
   - Tạo/sửa/xóa sản phẩm
   - Quản lý đơn hàng
   - Quản lý người dùng
3. Các toast sẽ tự động hiển thị khi có thao tác thành công hoặc lỗi

### 5. Test Mobile Responsiveness

1. Mở DevTools (F12)
2. Chuyển sang chế độ mobile (Ctrl+Shift+M)
3. Test các tính năng:
   - Toast hiển thị ở bottom trên mobile
   - Swipe to dismiss
   - "Show All" button khi có nhiều toasts
   - Message truncation với expand option

## 📋 Checklist kiểm tra

- [ ] Toast hiển thị khi gọi `toastService.success()`
- [ ] Toast hiển thị khi gọi `toastService.error()`
- [ ] Toast hiển thị khi gọi `toastService.warning()`
- [ ] Toast hiển thị khi gọi `toastService.info()`
- [ ] Progress bar hoạt động và giảm dần
- [ ] Hover vào toast pause progress bar
- [ ] Mouse leave resume progress bar
- [ ] Action buttons hoạt động đúng
- [ ] Toast tự động đóng sau duration
- [ ] Click vào toast đóng được (nếu closable)
- [ ] Nhiều toasts hiển thị cùng lúc (tối đa 5)
- [ ] Mobile: Toast ở bottom
- [ ] Mobile: Swipe to dismiss hoạt động
- [ ] Mobile: "Show All" button hiển thị khi có > 3 toasts
- [ ] API error messages hiển thị đúng format

## 🐛 Troubleshooting

### Toast không hiển thị

1. Kiểm tra `ToastContainer` đã được import trong layout:
   - `AdminLayout.vue` - line 251, 261
   - `DefaultLayout.vue` - line 254, 331

2. Kiểm tra console có lỗi không

3. Kiểm tra `toastService` đã được import đúng chưa

### Progress bar không hoạt động

1. Kiểm tra toast có `duration` không (toast với duration = 0 sẽ không có progress bar)

2. Kiểm tra console có lỗi JavaScript không

### Action buttons không hoạt động

1. Kiểm tra `actions` array có đúng format không:
   ```javascript
   {
     label: 'Button Text',
     handler: () => { /* function */ },
     primary: true // optional
   }
   ```

2. Kiểm tra handler function có được định nghĩa đúng không

## 📝 Notes

- Toast service là singleton, chỉ có 1 instance trong toàn bộ ứng dụng
- Maximum 5 toasts hiển thị cùng lúc
- Default duration: 5000ms (5 giây)
- Toast mới sẽ thay thế toast cũ nếu đã đạt max toasts



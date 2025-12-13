# 📋 LOGIC THANH TOÁN VÀ QUẢN LÝ KHO

## ✅ Tổng quan logic đã triển khai:

### 1. **Client - Thanh toán VNPay**
- ✅ Thêm VNPay vào checkout page
- ✅ User chọn "Thanh toán trực tuyến" → Redirect đến VNPay
- ✅ **KHO KHÔNG BỊ TRỪ** khi checkout (tránh trừ kho khi user chưa thanh toán)
- ✅ Khi thanh toán thành công → VNPay callback → **Backend tự động trừ kho**
- ✅ User redirect về `/payment/callback` với kết quả

### 2. **Client - Thanh toán COD**
- ✅ User chọn "COD (Thanh toán khi nhận hàng)"
- ✅ **KHO BỊ TRỪ NGAY** khi checkout (tránh overselling)
- ✅ Order được tạo với payment status = "pending"
- ✅ Khi giao hàng thành công, admin cập nhật order status

### 3. **Admin - POS (Bán hàng tại quầy)**
- ✅ Admin thêm sản phẩm vào giỏ hàng
- ✅ **KHO BỊ TRỪ NGAY LẬP TỨC** khi tạo order
- ✅ Order có status = "delivered" (đã giao)
- ✅ Payment status = "completed"

### 4. **VAT (Thuế)**
- ✅ **ĐÃ XÓA VAT** khỏi tất cả tính toán
- ✅ Backend: `taxAmount = 0`
- ✅ Frontend: Không hiển thị VAT
- ✅ Total = Subtotal - Discount + Shipping

---

## 🔄 Flow chi tiết:

### A. User Checkout với VNPay:
```
1. User thêm sản phẩm vào giỏ → Checkout
2. Chọn "Thanh toán trực tuyến"
3. Click "Xác nhận đặt hàng"
   ↓
4. Backend tạo Order (status = "Pending", payment status = "pending")
   ⚠️ KHO CHƯA BỊ TRỪ
   ↓
5. Backend tạo VNPay payment URL → Return URL cho frontend
   ↓
6. Frontend redirect user đến VNPay payment page
   ↓
7. User thanh toán trên VNPay
   ↓
8a. THÀNH CÔNG:
    - VNPay gọi callback: GET /api/payment/vnpay/callback
    - Backend nhận callback, verify transaction
    - Backend GỌI orderService.processSuccessfulPayment(orderId)
      → ✅ TRỪ KHO
      → ✅ Cập nhật payment status = "completed"
      → ✅ Cập nhật order status = "Processing"
    - User redirect về /payment/callback (hiển thị success)
    
8b. THẤT BẠI:
    - VNPay return với error code
    - User redirect về /payment/callback (hiển thị failed)
    - ⚠️ KHO KHÔNG BỊ TRỪ (vì chưa thanh toán)
    - Order vẫn có status = "Pending", có thể retry thanh toán
```

### B. User Checkout với COD:
```
1. User thêm sản phẩm vào giỏ → Checkout
2. Chọn "COD (Thanh toán khi nhận hàng)"
3. Click "Xác nhận đặt hàng"
   ↓
4. Backend tạo Order (status = "Pending", payment = "cod")
   ✅ TRỪ KHO NGAY (tránh overselling)
   ↓
5. Frontend hiển thị "Đặt hàng thành công"
6. User redirect về trang Orders
   ↓
7. Khi giao hàng thành công:
   - Admin cập nhật order status = "Delivered"
   - Payment status = "completed"
```

### C. Admin POS Order:
```
1. Admin chọn sản phẩm → Thêm vào giỏ
2. Click "Thanh toán"
   ↓
3. Backend tạo POS Order
   ✅ TRỪ KHO NGAY LẬP TỨC
   ✅ Order status = "delivered"
   ✅ Payment status = "completed"
   ↓
4. In hóa đơn → Hoàn tất
```

---

## 🎯 Giải pháp cho các vấn đề:

### ❌ VẤN ĐỀ 1: Overselling (Bán quá số lượng tồn kho)
**Giải pháp:**
- COD: Trừ kho NGAY khi checkout
- VNPay: Trừ kho SAU khi thanh toán thành công
- Admin POS: Trừ kho NGAY khi tạo order

### ❌ VẤN ĐỀ 2: User không thanh toán VNPay nhưng kho đã bị trừ
**Giải pháp:**
- VNPay: KHÔNG trừ kho khi checkout
- CHỈ trừ kho khi VNPay callback với status = success

### ❌ VẤN ĐỀ 3: Trừ kho 2 lần cho COD
**Giải pháp:**
- COD: Trừ kho 1 lần duy nhất khi checkout
- Method `processSuccessfulPayment()` kiểm tra payment method:
  - Nếu online payment → Trừ kho
  - Nếu COD → Không trừ kho (đã trừ rồi)

---

## 📝 Code quan trọng:

### Backend - OrderService.java

#### 1. Checkout User/Guest:
```java
// Line ~160-180
boolean isOnlinePayment = "online".equalsIgnoreCase(requestDto.getPaymentMethod());

for (CartItem cartItem : cart.getItems()) {
    // Kiểm tra tồn kho
    if (variant.getStockQuantity() < cartItem.getQuantity()) {
        throw new ApiException("Không đủ hàng");
    }
    
    // XỬ LÝ KHO THEO PAYMENT METHOD
    if (!isOnlinePayment) {
        // COD - Trừ kho ngay
        variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
        variantRepository.save(variant);
    }
    // Online payment - KHÔNG trừ kho
}
```

#### 2. Process Successful Payment (VNPay callback):
```java
// Line ~763-820
public void processSuccessfulPayment(Long orderId) {
    Order order = orderRepository.findByIdWithDetails(orderId);
    Payment payment = order.getPayments().stream().findFirst();
    
    // Cập nhật payment status
    payment.setStatus("completed");
    payment.setPaidAt(LocalDateTime.now());
    
    // CHỈ TRỪ KHO CHO ONLINE PAYMENT
    boolean isOnlinePayment = "online".equalsIgnoreCase(payment.getPaymentMethod());
    
    if (isOnlinePayment) {
        // Trừ kho cho online payment
        for (OrderDetail detail : order.getOrderDetails()) {
            variant.setStockQuantity(variant.getStockQuantity() - detail.getQuantity());
            variantRepository.save(variant);
        }
    } else {
        // COD - Đã trừ kho rồi, không trừ lại
    }
}
```

#### 3. Admin POS Order:
```java
// AdminOrderService.java - Line ~313-315
// POS order - Trừ kho ngay
int newStock = variant.getStockQuantity() - itemDto.getQuantity();
variant.setStockQuantity(newStock);
variantRepository.save(variant);
```

---

## 🔍 Testing Checklist:

### Test Case 1: VNPay Payment
- [ ] User checkout với VNPay
- [ ] Kiểm tra kho CHƯA bị trừ sau checkout
- [ ] User thanh toán thành công trên VNPay
- [ ] Kiểm tra VNPay callback được gọi
- [ ] Kiểm tra kho ĐÃ BỊ TRỪ sau callback
- [ ] Kiểm tra payment status = "completed"
- [ ] Kiểm tra order status = "Processing"

### Test Case 2: COD Payment
- [ ] User checkout với COD
- [ ] Kiểm tra kho ĐÃ BỊ TRỪ ngay sau checkout
- [ ] Kiểm tra order được tạo thành công
- [ ] Kiểm tra payment status = "pending"

### Test Case 3: Admin POS
- [ ] Admin tạo POS order
- [ ] Kiểm tra kho ĐÃ BỊ TRỪ ngay lập tức
- [ ] Kiểm tra order status = "delivered"
- [ ] Kiểm tra payment status = "completed"

### Test Case 4: VNPay Failed Payment
- [ ] User checkout với VNPay
- [ ] User HỦY thanh toán trên VNPay
- [ ] Kiểm tra kho KHÔNG BỊ TRỪ
- [ ] Kiểm tra order status vẫn = "Pending"
- [ ] User có thể retry thanh toán

---

## ⚙️ Configuration Required:

### VNPay Sandbox Setup:
1. Đăng ký tài khoản: https://sandbox.vnpayment.vn/
2. Lấy credentials:
   - TMN_CODE
   - HASH_SECRET
3. Cập nhật `application.properties`:
```properties
payment.vnpay.url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
payment.vnpay.tmn-code=YOUR_VNPAY_TMN_CODE
payment.vnpay.hash-secret=YOUR_VNPAY_HASH_SECRET
payment.vnpay.return-url=http://localhost:5173/payment/callback
```

---

## 📊 Database Triggers:

### Inventory Log Trigger:
Database có trigger `trg_ProductVariants_InventoryLog` tự động tạo log mỗi khi `stock_quantity` thay đổi:
- Ghi lại: variant_id, quantity_change, timestamp, reason
- Tự động track mọi thay đổi inventory

---

## 🚨 Lưu ý quan trọng:

1. **KHÔNG BAO GIỜ** tin tưởng giá từ frontend - Luôn lấy giá từ database
2. **LUÔN LUÔN** kiểm tra tồn kho trước khi trừ
3. **Database trigger** tự động tạo inventory log
4. **VNPay callback** phải verify signature (TODO: implement signature verification)
5. **COD order** có thể cancel → Cần logic hoàn lại kho
6. **Failed VNPay payment** → Order vẫn pending, có thể retry

---

*Last updated: December 12, 2025*

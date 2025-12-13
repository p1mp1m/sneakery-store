# Hướng Dẫn Tích Hợp VNPay Payment Gateway

## 📋 Tổng Quan

Dự án đã tích hợp đầy đủ VNPay Payment Gateway theo tài liệu chính thức. Tài liệu này hướng dẫn cách cấu hình và sử dụng.

## 🔧 Các Bước Đã Implement

### 1. ✅ Build URL Thanh Toán (Payment URL)
- **File**: `PaymentGatewayService.java`
- **Method**: `createVNPayPaymentUrl()`
- **Chức năng**: Tạo URL thanh toán VNPay với đầy đủ parameters và secure hash (HMAC SHA512)

### 2. ✅ IPN URL (Instant Payment Notification)
- **Endpoint**: `GET /api/payment/vnpay/ipn`
- **File**: `PaymentController.java`
- **Chức năng**: 
  - Server-to-server callback từ VNPay
  - Xác thực chữ ký (signature verification)
  - Cập nhật kết quả thanh toán
  - Giảm inventory khi thanh toán thành công
  - Trả về response theo format VNPay yêu cầu (RspCode + Message)

### 3. ✅ Return URL  
- **Endpoint**: `GET /api/payment/vnpay/return`
- **File**: `PaymentController.java`
- **Chức năng**: 
  - Nhận user từ VNPay sau khi thanh toán
  - Redirect về frontend để hiển thị kết quả

### 4. ✅ Frontend Payment Flow
- **File**: `CheckoutPage.vue`
- **Chức năng**:
  - Chọn phương thức thanh toán (COD hoặc Online)
  - Khi chọn Online → Gọi API checkout
  - Backend trả về `paymentUrl`
  - Frontend redirect user đến VNPay payment gateway

## ⚙️ Cấu Hình

### 1. Đăng Ký Sandbox VNPay

Truy cập: http://sandbox.vnpayment.vn/devreg/

Điền form đăng ký và nhận email với:
- `vnp_TmnCode`: Mã định danh merchant
- `vnp_HashSecret`: Secret key để tạo checksum

### 2. Cập Nhật application.properties

```properties
# VNPay Configuration
payment.vnpay.url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
payment.vnpay.tmn-code=YOUR_VNPAY_TMN_CODE
payment.vnpay.hash-secret=YOUR_VNPAY_HASH_SECRET
payment.vnpay.return-url=http://localhost:5173/payment/callback
payment.vnpay.ipn-url=http://localhost:8080/api/payment/vnpay/ipn
```

**Thay thế**:
- `YOUR_VNPAY_TMN_CODE` → Mã TmnCode từ email VNPay
- `YOUR_VNPAY_HASH_SECRET` → Secret key từ email VNPay

### 3. Cấu Hình IPN URL với VNPay

**QUAN TRỌNG**: Sau khi thiết lập xong, gửi IPN URL cho VNPay:

```
IPN URL: http://YOUR_DOMAIN/api/payment/vnpay/ipn
```

Trong môi trường local, dùng ngrok để expose local server:
```bash
ngrok http 8080
# VNPay IPN URL: https://your-ngrok-domain.ngrok.io/api/payment/vnpay/ipn
```

## 🔐 Bảo Mật

### Checksum/Signature Verification

Mọi callback từ VNPay đều được verify chữ ký bằng HMAC SHA512:

```java
public boolean verifyVNPayCallback(Map<String, String> params) {
    // 1. Lấy vnp_SecureHash từ params
    // 2. Remove vnp_SecureHash và vnp_SecureHashType
    // 3. Sort params theo alphabet
    // 4. Build query string
    // 5. Tạo HMAC SHA512 hash
    // 6. So sánh với vnp_SecureHash
}
```

## 📱 Luồng Thanh Toán

### Luồng COD (Cash on Delivery)
```
1. User chọn COD → Checkout
2. Backend tạo order
3. Giảm inventory NGAY (tránh overselling)
4. Trả về OrderDto (không có paymentUrl)
5. Frontend hiển thị success page
```

### Luồng VNPay (Online Payment)
```
1. User chọn Online → Checkout
2. Backend tạo order
3. KHÔNG giảm inventory (chờ thanh toán)
4. Tạo VNPay payment URL
5. Trả về OrderDto với paymentUrl
6. Frontend redirect đến VNPay
7. User nhập thông tin thanh toán trên VNPay
8. VNPay gọi IPN URL (server-to-server):
   - Verify signature
   - Giảm inventory nếu thành công
   - Cập nhật payment status
   - Trả về RspCode cho VNPay
9. VNPay redirect user về Return URL
10. Frontend hiển thị kết quả
```

## 📊 Mã Lỗi VNPay

### vnp_ResponseCode

| Mã  | Mô Tả |
|-----|-------|
| 00  | Giao dịch thành công |
| 07  | Trừ tiền thành công, nghi ngờ gian lận |
| 09  | Chưa đăng ký InternetBanking |
| 10  | Xác thực không đúng quá 3 lần |
| 11  | Hết hạn chờ thanh toán |
| 12  | Thẻ/Tài khoản bị khóa |
| 13  | Sai mật khẩu OTP |
| 24  | Khách hàng hủy giao dịch |
| 51  | Tài khoản không đủ số dư |
| 65  | Vượt quá hạn mức giao dịch |
| 75  | Ngân hàng bảo trì |
| 79  | Sai mật khẩu quá số lần quy định |
| 99  | Lỗi khác |

### IPN Response Codes (Merchant → VNPay)

| RspCode | Message | Ý Nghĩa |
|---------|---------|---------|
| 00      | Confirm Success | Đã cập nhật thành công |
| 01      | Order Not Found | Không tìm thấy order |
| 02      | Order Already Confirmed | Order đã được xác nhận trước đó |
| 97      | Invalid Signature | Chữ ký không hợp lệ |
| 99      | Unknown Error | Lỗi hệ thống |

## 🧪 Test Sandbox

### Thông Tin Test

Sau khi đăng ký sandbox, VNPay cung cấp thông tin test:
- Số thẻ test
- Tên chủ thẻ
- Ngày hết hạn
- Mã OTP

### Test Scenarios

1. **Thanh Toán Thành Công**
   - Dùng thẻ test VNPay cung cấp
   - Nhập OTP đúng
   - Kiểm tra inventory giảm
   - Kiểm tra payment status = success

2. **Thanh Toán Thất Bại**
   - Dùng thẻ test
   - Nhập OTP sai hoặc hủy giao dịch
   - Kiểm tra inventory KHÔNG giảm
   - Kiểm tra payment status = failed

3. **Timeout**
   - Không hoàn tất thanh toán trong 15 phút
   - VNPay hủy giao dịch tự động

## 📝 API Endpoints

### 1. Checkout (Tạo Order + Payment URL)
```http
POST /api/orders/checkout
Authorization: Bearer {token}
Content-Type: application/json

{
  "addressShippingId": 1,
  "paymentMethod": "online",
  "couponCode": "DISCOUNT10",
  "pointsUsed": 100,
  "customerNote": "Giao giờ hành chính"
}
```

**Response** (với paymentUrl):
```json
{
  "id": 123,
  "orderNumber": "ORD-20250113-0001",
  "status": "Pending",
  "totalAmount": 1500000,
  "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=..."
}
```

### 2. VNPay IPN Callback
```http
GET /api/payment/vnpay/ipn?vnp_Amount=1500000&vnp_ResponseCode=00&...
```

**Response**:
```json
{
  "RspCode": "00",
  "Message": "Confirm Success"
}
```

### 3. VNPay Return URL
```http
GET /api/payment/vnpay/return?vnp_ResponseCode=00&vnp_TxnRef=123&...
```

**Response**: Redirect to frontend
```
→ http://localhost:5173/checkout/success?orderId=123
```

## 🚀 Production Deployment

### 1. Đăng Ký Production VNPay
- Liên hệ VNPay để đăng ký môi trường production
- Nhận TmnCode và HashSecret mới
- Cập nhật URL production

### 2. Cập Nhật Config
```properties
payment.vnpay.url=https://pay.vnpay.vn/paymentv2/vpcpay.html
payment.vnpay.tmn-code=YOUR_PRODUCTION_TMN_CODE
payment.vnpay.hash-secret=YOUR_PRODUCTION_HASH_SECRET
payment.vnpay.return-url=https://your-domain.com/payment/callback
payment.vnpay.ipn-url=https://your-domain.com/api/payment/vnpay/ipn
```

### 3. SSL Certificate
- IPN URL BẮT BUỘC phải có SSL
- VNPay không gọi HTTP endpoints

### 4. Gửi IPN URL cho VNPay
- Liên hệ VNPay support
- Cung cấp IPN URL production
- VNPay sẽ cấu hình trong hệ thống

## 🐛 Troubleshooting

### Không Nhận Được IPN Callback
1. Kiểm tra IPN URL có SSL chưa
2. Kiểm tra firewall/port có mở không
3. Xem logs backend xem có request không
4. Test IPN URL bằng Postman với VNPay params

### Invalid Signature Error
1. Kiểm tra `vnp_HashSecret` đúng chưa
2. Kiểm tra params có bị encode/decode sai không
3. Đảm bảo sort params theo alphabet
4. Log ra hashData để debug

### Inventory Không Giảm
1. Kiểm tra IPN callback có được gọi không
2. Kiểm tra `vnp_ResponseCode = 00`
3. Kiểm tra logs trong `OrderService.processSuccessfulPayment()`
4. Kiểm tra database trigger `trg_ProductVariants_InventoryLog`

### Frontend Không Redirect
1. Kiểm tra response có `paymentUrl` không
2. Kiểm tra CheckoutPage.vue logic
3. Mở Console xem có error không

## 📚 Tài Liệu Tham Khảo

- VNPay Documentation: https://sandbox.vnpayment.vn/apis/
- VNPay Sandbox: http://sandbox.vnpayment.vn/devreg/
- Code Demo: Trong email đăng ký sandbox

## ✅ Checklist

Trước khi deploy production:

- [ ] Đã test thanh toán thành công
- [ ] Đã test thanh toán thất bại
- [ ] Đã test timeout
- [ ] Inventory giảm đúng
- [ ] Payment status cập nhật đúng
- [ ] IPN signature verification hoạt động
- [ ] Return URL redirect đúng
- [ ] Email confirmation được gửi
- [ ] Logs đầy đủ để debug
- [ ] SSL certificate đã setup
- [ ] IPN URL đã gửi cho VNPay

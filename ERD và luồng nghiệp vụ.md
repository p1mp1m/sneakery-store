# 📊 ERD và Luồng Nghiệp Vụ - Sneakery Store

<div align="center">

**Tài liệu mô tả cơ sở dữ liệu và các luồng nghiệp vụ chính**

</div>

---

## 📋 Mục lục

- [1. Tổng quan Cơ sở dữ liệu](#1-tổng-quan-cơ-sở-dữ-liệu)
- [2. Entity Relationship Diagram](#2-entity-relationship-diagram-erd)
- [3. Mô tả chi tiết các bảng](#3-mô-tả-chi-tiết-các-bảng)
- [4. Các mối quan hệ chính](#4-các-mối-quan-hệ-chính)
- [5. Luồng nghiệp vụ chính](#5-luồng-nghiệp-vụ-chính)

---

## 🎯 1. Tổng quan Cơ sở dữ liệu

Database **sneakery_db** được thiết kế để hỗ trợ toàn bộ hoạt động của một hệ thống e-commerce bán giày.

### 📦 **Các nhóm bảng chính**

| Nhóm | Số bảng | Mục đích |
|------|---------|----------|
| 👥 **Người dùng** | 3 | Quản lý user, role, địa chỉ |
| 👟 **Sản phẩm** | 5 | Sản phẩm, biến thể, hình ảnh, brand, category |
| 🛒 **Giỏ hàng** | 2 | Giỏ hàng và chi tiết |
| 📦 **Đơn hàng** | 3 | Đơn hàng, chi tiết, trạng thái |
| 💳 **Thanh toán** | 2 | Giao dịch thanh toán, phương thức |
| 🎟️ **Khuyến mãi** | 3 | Discount, flash sale, loyalty points |
| ⭐ **Đánh giá** | 1 | Review sản phẩm |
| 🔔 **Thông báo** | 2 | Notification, wishlist |
| 🔄 **Trả hàng** | 1 | Quản lý return/refund |
| 🛡️ **Bảo hành** | 1 | Warranty claims |

**Tổng cộng: 24 bảng**

---

## 🗺️ 2. Entity Relationship Diagram (ERD)

### 📊 **Sơ đồ ERD Tổng quan**

```
┌─────────────────┐
│     USERS       │──┐
│  (Người dùng)   │  │
└─────────────────┘  │
                     │
         ┌───────────┴────────────────────────────┐
         │                                        │
         ↓                                        ↓
┌─────────────────┐                    ┌─────────────────┐
│     ORDERS      │                    │      CART       │
│   (Đơn hàng)    │                    │   (Giỏ hàng)    │
└─────────────────┘                    └─────────────────┘
         │                                        │
         ↓                                        ↓
┌─────────────────┐                    ┌─────────────────┐
│  ORDER_ITEMS    │                    │   CART_ITEMS    │
│  (Chi tiết ĐH)  │                    │   (Chi tiết GH) │
└─────────────────┘                    └─────────────────┘
         │                                        │
         └────────────────┬───────────────────────┘
                          ↓
                ┌─────────────────┐
                │  PRODUCT_       │
                │   VARIANTS      │──────→ ┌─────────────────┐
                │ (Biến thể SP)   │        │    PRODUCTS     │
                └─────────────────┘        │   (Sản phẩm)    │
                                           └─────────────────┘
                                                    │
                                    ┌───────────────┼───────────────┐
                                    ↓               ↓               ↓
                            ┌───────────┐   ┌───────────┐   ┌───────────┐
                            │  BRANDS   │   │CATEGORIES │   │  REVIEWS  │
                            └───────────┘   └───────────┘   └───────────┘
```

---

## 📚 3. Mô tả chi tiết các bảng

### 👥 **Nhóm Người dùng**

#### 📋 **users** (Người dùng)
Lưu thông tin tài khoản người dùng

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `user_id` | BIGINT (PK) | ID tự động tăng |
| `username` | NVARCHAR(50) | Tên đăng nhập (unique) |
| `email` | NVARCHAR(100) | Email (unique) |
| `password_hash` | NVARCHAR(255) | Mật khẩu đã mã hóa |
| `full_name` | NVARCHAR(100) | Họ tên đầy đủ |
| `phone_number` | VARCHAR(15) | Số điện thoại |
| `date_of_birth` | DATE | Ngày sinh |
| `gender` | VARCHAR(10) | Giới tính (Male/Female/Other) |
| `avatar_url` | NVARCHAR(500) | Link ảnh đại diện |
| `is_active` | BIT | Trạng thái kích hoạt |
| `created_at` | DATETIME | Thời gian tạo |
| `updated_at` | DATETIME | Thời gian cập nhật |

#### 📋 **roles** (Vai trò)
Định nghĩa các vai trò trong hệ thống

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `role_id` | BIGINT (PK) | ID vai trò |
| `role_name` | VARCHAR(50) | Tên vai trò (ADMIN, CUSTOMER) |
| `description` | NVARCHAR(255) | Mô tả vai trò |

#### 📋 **user_addresses** (Địa chỉ người dùng)
Lưu địa chỉ giao hàng của khách hàng

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `address_id` | BIGINT (PK) | ID địa chỉ |
| `user_id` | BIGINT (FK) | ID người dùng |
| `address_type` | VARCHAR(20) | Loại (Home/Office) |
| `recipient_name` | NVARCHAR(100) | Tên người nhận |
| `phone_number` | VARCHAR(15) | SĐT người nhận |
| `address_line` | NVARCHAR(255) | Địa chỉ chi tiết |
| `ward` | NVARCHAR(100) | Phường/xã |
| `district` | NVARCHAR(100) | Quận/huyện |
| `city` | NVARCHAR(100) | Tỉnh/thành phố |
| `is_default` | BIT | Địa chỉ mặc định |

---

### 👟 **Nhóm Sản phẩm**

#### 📋 **products** (Sản phẩm)
Thông tin sản phẩm chính

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `product_id` | BIGINT (PK) | ID sản phẩm |
| `product_name` | NVARCHAR(200) | Tên sản phẩm |
| `brand_id` | BIGINT (FK) | ID thương hiệu |
| `category_id` | BIGINT (FK) | ID danh mục |
| `description` | NVARCHAR(MAX) | Mô tả chi tiết |
| `base_price` | DECIMAL(10,2) | Giá gốc |
| `gender` | VARCHAR(20) | Giới tính (Male/Female/Unisex) |
| `material` | NVARCHAR(100) | Chất liệu |
| `is_featured` | BIT | Sản phẩm nổi bật |
| `is_active` | BIT | Đang bán |
| `created_at` | DATETIME | Ngày tạo |

#### 📋 **product_variants** (Biến thể sản phẩm)
⚠️ **QUAN TRỌNG:** Mỗi kết hợp Size + Màu = 1 biến thể

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `variant_id` | BIGINT (PK) | ID biến thể |
| `product_id` | BIGINT (FK) | ID sản phẩm |
| `sku` | VARCHAR(100) | Mã SKU duy nhất |
| `size` | VARCHAR(10) | Size (38, 39, 40...) |
| `color` | NVARCHAR(50) | Màu sắc |
| `stock_quantity` | INT | **Số lượng tồn kho** |
| `price` | DECIMAL(10,2) | Giá bán |
| `is_available` | BIT | Còn hàng |

**Ví dụ:**
```
Product: Nike Air Max 2024
  - Variant 1: SKU=NIKE-AIR-BLK-42, Size=42, Color=Đen, Stock=10
  - Variant 2: SKU=NIKE-AIR-BLK-43, Size=43, Color=Đen, Stock=5
  - Variant 3: SKU=NIKE-AIR-WHT-42, Size=42, Color=Trắng, Stock=8
```

#### 📋 **product_images** (Hình ảnh sản phẩm)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `image_id` | BIGINT (PK) | ID hình ảnh |
| `product_id` | BIGINT (FK) | ID sản phẩm |
| `image_url` | NVARCHAR(500) | Link hình ảnh |
| `is_primary` | BIT | Ảnh chính |
| `display_order` | INT | Thứ tự hiển thị |

#### 📋 **brands** (Thương hiệu)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `brand_id` | BIGINT (PK) | ID thương hiệu |
| `brand_name` | NVARCHAR(100) | Tên thương hiệu |
| `logo_url` | NVARCHAR(500) | Logo |
| `description` | NVARCHAR(MAX) | Mô tả |

#### 📋 **categories** (Danh mục)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `category_id` | BIGINT (PK) | ID danh mục |
| `category_name` | NVARCHAR(100) | Tên danh mục |
| `parent_category_id` | BIGINT (FK) | Danh mục cha (null = gốc) |
| `description` | NVARCHAR(500) | Mô tả |

---

### 🛒 **Nhóm Giỏ hàng**

#### 📋 **carts** (Giỏ hàng)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `cart_id` | BIGINT (PK) | ID giỏ hàng |
| `user_id` | BIGINT (FK) | ID người dùng |
| `created_at` | DATETIME | Ngày tạo |
| `updated_at` | DATETIME | Cập nhật gần nhất |

#### 📋 **cart_items** (Chi tiết giỏ hàng)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `cart_item_id` | BIGINT (PK) | ID chi tiết |
| `cart_id` | BIGINT (FK) | ID giỏ hàng |
| `variant_id` | BIGINT (FK) | ID biến thể SP |
| `quantity` | INT | Số lượng |
| `price_at_add` | DECIMAL(10,2) | Giá khi thêm vào |
| `added_at` | DATETIME | Thời gian thêm |

---

### 📦 **Nhóm Đơn hàng**

#### 📋 **orders** (Đơn hàng)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `order_id` | BIGINT (PK) | ID đơn hàng |
| `user_id` | BIGINT (FK) | ID khách hàng |
| `order_number` | VARCHAR(50) | Mã đơn hàng (unique) |
| `order_status` | VARCHAR(50) | Trạng thái đơn |
| `total_amount` | DECIMAL(10,2) | Tổng tiền |
| `shipping_fee` | DECIMAL(10,2) | Phí ship |
| `discount_amount` | DECIMAL(10,2) | Giảm giá |
| `final_amount` | DECIMAL(10,2) | Tiền thanh toán |
| `shipping_address` | NVARCHAR(500) | Địa chỉ giao hàng |
| `phone_number` | VARCHAR(15) | SĐT nhận hàng |
| `payment_method` | VARCHAR(50) | Phương thức TT |
| `created_at` | DATETIME | Ngày đặt |

**Các trạng thái đơn hàng:**
- `PENDING` - Chờ xác nhận
- `CONFIRMED` - Đã xác nhận
- `PROCESSING` - Đang xử lý
- `SHIPPING` - Đang giao hàng
- `DELIVERED` - Đã giao
- `CANCELLED` - Đã hủy
- `RETURNED` - Đã trả hàng

#### 📋 **order_items** (Chi tiết đơn hàng)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `order_item_id` | BIGINT (PK) | ID chi tiết |
| `order_id` | BIGINT (FK) | ID đơn hàng |
| `variant_id` | BIGINT (FK) | ID biến thể SP |
| `quantity` | INT | Số lượng |
| `unit_price` | DECIMAL(10,2) | Đơn giá |
| `total_price` | DECIMAL(10,2) | Thành tiền |

#### 📋 **order_status_history** (Lịch sử trạng thái)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `history_id` | BIGINT (PK) | ID lịch sử |
| `order_id` | BIGINT (FK) | ID đơn hàng |
| `old_status` | VARCHAR(50) | Trạng thái cũ |
| `new_status` | VARCHAR(50) | Trạng thái mới |
| `changed_by` | BIGINT (FK) | Người thay đổi |
| `changed_at` | DATETIME | Thời gian |
| `notes` | NVARCHAR(500) | Ghi chú |

---

### 💳 **Nhóm Thanh toán**

#### 📋 **payments** (Giao dịch thanh toán)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `payment_id` | BIGINT (PK) | ID thanh toán |
| `order_id` | BIGINT (FK) | ID đơn hàng |
| `payment_method` | VARCHAR(50) | Phương thức |
| `amount` | DECIMAL(10,2) | Số tiền |
| `status` | VARCHAR(50) | Trạng thái |
| `transaction_id` | VARCHAR(100) | Mã giao dịch |
| `payment_date` | DATETIME | Ngày thanh toán |

**Phương thức thanh toán:**
- `COD` - Tiền mặt khi nhận hàng
- `BANK_TRANSFER` - Chuyển khoản
- `MOMO` - Ví MoMo
- `ZALOPAY` - ZaloPay
- `VNPAY` - VNPay
- `CREDIT_CARD` - Thẻ tín dụng

---

### 🎟️ **Nhóm Khuyến mãi**

#### 📋 **discounts** (Mã giảm giá)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `discount_id` | BIGINT (PK) | ID mã giảm giá |
| `code` | VARCHAR(50) | Mã code (unique) |
| `discount_type` | VARCHAR(20) | PERCENTAGE / FIXED |
| `discount_value` | DECIMAL(10,2) | Giá trị giảm |
| `min_order_value` | DECIMAL(10,2) | Đơn tối thiểu |
| `max_discount` | DECIMAL(10,2) | Giảm tối đa |
| `start_date` | DATETIME | Bắt đầu |
| `end_date` | DATETIME | Kết thúc |
| `usage_limit` | INT | Giới hạn sử dụng |
| `used_count` | INT | Đã dùng |

#### 📋 **flash_sales** (Flash Sale)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `sale_id` | BIGINT (PK) | ID sale |
| `variant_id` | BIGINT (FK) | ID biến thể |
| `original_price` | DECIMAL(10,2) | Giá gốc |
| `sale_price` | DECIMAL(10,2) | Giá sale |
| `quantity_limit` | INT | Số lượng sale |
| `sold_quantity` | INT | Đã bán |
| `start_time` | DATETIME | Bắt đầu |
| `end_time` | DATETIME | Kết thúc |

#### 📋 **loyalty_points** (Điểm tích lũy)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `point_id` | BIGINT (PK) | ID |
| `user_id` | BIGINT (FK) | ID khách hàng |
| `points` | INT | Số điểm |
| `transaction_type` | VARCHAR(20) | EARN / REDEEM |
| `order_id` | BIGINT (FK) | ID đơn hàng |
| `created_at` | DATETIME | Ngày tạo |

---

### ⭐ **Nhóm Đánh giá**

#### 📋 **reviews** (Đánh giá sản phẩm)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `review_id` | BIGINT (PK) | ID đánh giá |
| `product_id` | BIGINT (FK) | ID sản phẩm |
| `user_id` | BIGINT (FK) | ID người dùng |
| `order_item_id` | BIGINT (FK) | ID chi tiết đơn hàng |
| `rating` | INT | Số sao (1-5) |
| `title` | NVARCHAR(200) | Tiêu đề |
| `content` | NVARCHAR(MAX) | Nội dung |
| `images` | NVARCHAR(MAX) | Link ảnh (JSON) |
| `is_verified_purchase` | BIT | Đã mua hàng |
| `created_at` | DATETIME | Ngày đánh giá |

---

### 🔔 **Nhóm Thông báo**

#### 📋 **notifications** (Thông báo)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `notification_id` | BIGINT (PK) | ID thông báo |
| `user_id` | BIGINT (FK) | ID người nhận |
| `title` | NVARCHAR(200) | Tiêu đề |
| `content` | NVARCHAR(MAX) | Nội dung |
| `type` | VARCHAR(50) | Loại thông báo |
| `is_read` | BIT | Đã đọc |
| `created_at` | DATETIME | Ngày tạo |

#### 📋 **wishlist** (Danh sách yêu thích)

| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| `wishlist_id` | BIGINT (PK) | ID |
| `user_id` | BIGINT (FK) | ID người dùng |
| `product_id` | BIGINT (FK) | ID sản phẩm |
| `added_at` | DATETIME | Ngày thêm |

---

## 🔗 4. Các mối quan hệ chính

### 📊 **Ma trận quan hệ**

| Bảng A | Quan hệ | Bảng B | Mô tả |
|--------|---------|--------|-------|
| `users` | **1 - N** | `user_addresses` | 1 user có nhiều địa chỉ |
| `users` | **1 - 1** | `carts` | 1 user có 1 giỏ hàng |
| `users` | **1 - N** | `orders` | 1 user có nhiều đơn hàng |
| `products` | **1 - N** | `product_variants` | 1 sản phẩm có nhiều biến thể |
| `products` | **N - 1** | `brands` | Nhiều sản phẩm thuộc 1 brand |
| `products` | **N - 1** | `categories` | Nhiều sản phẩm thuộc 1 danh mục |
| `carts` | **1 - N** | `cart_items` | 1 giỏ hàng có nhiều item |
| `orders` | **1 - N** | `order_items` | 1 đơn hàng có nhiều item |
| `product_variants` | **1 - N** | `cart_items` | 1 biến thể xuất hiện ở nhiều giỏ |
| `product_variants` | **1 - N** | `order_items` | 1 biến thể xuất hiện ở nhiều đơn |

### 🔑 **Foreign Key quan trọng**

```sql
-- Quan hệ User với Order
ALTER TABLE orders
ADD CONSTRAINT FK_orders_users
FOREIGN KEY (user_id) REFERENCES users(user_id);

-- Quan hệ Order với Order Items
ALTER TABLE order_items
ADD CONSTRAINT FK_order_items_orders
FOREIGN KEY (order_id) REFERENCES orders(order_id);

-- Quan hệ Order Items với Product Variants
ALTER TABLE order_items
ADD CONSTRAINT FK_order_items_variants
FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id);

-- Quan hệ Product Variants với Products
ALTER TABLE product_variants
ADD CONSTRAINT FK_variants_products
FOREIGN KEY (product_id) REFERENCES products(product_id);
```

---

## 🔄 5. Luồng Nghiệp Vụ Chính

### 🛒 **Luồng 1: Đặt hàng (Order Flow)**

#### **Bước 1: Khách hàng xem sản phẩm**
```
1. User vào trang web
2. Xem danh sách products
3. Click vào product để xem chi tiết
4. Chọn size, màu → Xác định variant_id
```

#### **Bước 2: Thêm vào giỏ hàng**
```sql
-- Kiểm tra tồn kho
SELECT stock_quantity
FROM product_variants
WHERE variant_id = @variant_id;

-- Nếu còn hàng → Thêm vào cart_items
INSERT INTO cart_items (cart_id, variant_id, quantity, price_at_add)
VALUES (@cart_id, @variant_id, @quantity, @current_price);
```

#### **Bước 3: Thanh toán (Checkout)**
```
1. User review giỏ hàng
2. Nhập địa chỉ giao hàng
3. Chọn phương thức thanh toán
4. Nhập mã giảm giá (nếu có)
5. Xác nhận đặt hàng
```

#### **Bước 4: Tạo đơn hàng**
```sql
-- 1. Tạo order
INSERT INTO orders (user_id, order_number, order_status, total_amount, ...)
VALUES (@user_id, @order_number, 'PENDING', @total, ...);

-- 2. Copy cart_items sang order_items
INSERT INTO order_items (order_id, variant_id, quantity, unit_price)
SELECT @new_order_id, variant_id, quantity, price_at_add
FROM cart_items
WHERE cart_id = @cart_id;

-- 3. TRỪ TỒN KHO (QUAN TRỌNG!)
UPDATE product_variants
SET stock_quantity = stock_quantity - @quantity
WHERE variant_id = @variant_id;

-- 4. Xóa giỏ hàng
DELETE FROM cart_items WHERE cart_id = @cart_id;
```

#### **Bước 5: Xử lý thanh toán**
```sql
-- Tạo payment record
INSERT INTO payments (order_id, payment_method, amount, status)
VALUES (@order_id, @method, @amount, 'PENDING');

-- Nếu COD → Status = PENDING
-- Nếu Online → Gọi payment gateway → Update status
```

#### **Bước 6: Cập nhật trạng thái đơn hàng**
```
PENDING → CONFIRMED → PROCESSING → SHIPPING → DELIVERED
```

Mỗi lần thay đổi trạng thái → Ghi vào `order_status_history`

---

### 🔄 **Luồng 2: Đổi trả hàng (Return Flow)**

#### **Điều kiện đổi trả:**
- Trong vòng 7-14 ngày
- Sản phẩm chưa qua sử dụng
- Còn nguyên tem, hộp

#### **Quy trình:**
```
1. Khách hàng tạo yêu cầu đổi trả
   → INSERT INTO returns (order_id, reason, ...)

2. Admin xem xét
   → UPDATE returns SET status = 'APPROVED'/'REJECTED'

3. Nếu APPROVED:
   - Khách gửi hàng về
   - Admin kiểm tra hàng
   - Cộng lại tồn kho
     UPDATE product_variants
     SET stock_quantity = stock_quantity + @returned_qty

4. Hoàn tiền hoặc đổi size mới
   - Nếu hoàn tiền → Tạo refund payment
   - Nếu đổi size → Tạo đơn hàng mới
```

---

### ⚡ **Luồng 3: Flash Sale**

#### **Thiết lập Flash Sale:**
```sql
-- 1. Admin tạo Flash Sale
INSERT INTO flash_sales (
    variant_id,
    original_price,
    sale_price,
    quantity_limit,
    start_time,
    end_time
)
VALUES (
    @variant_id,
    2500000, -- Giá gốc
    1500000, -- Giá sale
    50,      -- Giới hạn 50 sản phẩm
    '2025-01-01 00:00:00',
    '2025-01-01 23:59:59'
);
```

#### **Khi khách mua:**
```sql
-- 1. Kiểm tra còn slot Flash Sale không
SELECT sold_quantity, quantity_limit
FROM flash_sales
WHERE sale_id = @sale_id
  AND GETDATE() BETWEEN start_time AND end_time;

-- 2. Nếu còn slot → Cho phép mua
--    Giá lấy từ flash_sales.sale_price

-- 3. Cập nhật số lượng đã bán
UPDATE flash_sales
SET sold_quantity = sold_quantity + @quantity
WHERE sale_id = @sale_id;
```

---

### 🎟️ **Luồng 4: Sử dụng mã giảm giá**

#### **Khi checkout:**
```sql
-- 1. User nhập mã: "GIAM20"
SELECT *
FROM discounts
WHERE code = 'GIAM20'
  AND GETDATE() BETWEEN start_date AND end_date
  AND used_count < usage_limit
  AND @order_total >= min_order_value;

-- 2. Tính giảm giá
IF discount_type = 'PERCENTAGE':
    discount_amount = @order_total * (discount_value / 100)
    IF discount_amount > max_discount:
        discount_amount = max_discount

IF discount_type = 'FIXED':
    discount_amount = discount_value

-- 3. Cập nhật số lần sử dụng
UPDATE discounts
SET used_count = used_count + 1
WHERE discount_id = @discount_id;
```

---

### 👑 **Luồng 5: Tích điểm thành viên**

#### **Khi đơn hàng hoàn thành:**
```sql
-- Quy tắc: 1.000 VND = 1 điểm

-- 1. Tính điểm
SET @points = @final_amount / 1000;

-- 2. Cộng điểm cho user
INSERT INTO loyalty_points (
    user_id,
    points,
    transaction_type,
    order_id
)
VALUES (
    @user_id,
    @points,
    'EARN',
    @order_id
);
```

#### **Khi đổi điểm:**
```sql
-- 1. Kiểm tra số điểm hiện có
SELECT SUM(points)
FROM loyalty_points
WHERE user_id = @user_id;

-- 2. Trừ điểm
INSERT INTO loyalty_points (
    user_id,
    points,
    transaction_type
)
VALUES (
    @user_id,
    -@redeem_points, -- Số âm
    'REDEEM'
);
```

---

### ⭐ **Luồng 6: Đánh giá sản phẩm**

#### **Điều kiện:**
- Chỉ đánh giá được khi đã mua hàng
- Mỗi order_item chỉ đánh giá 1 lần

```sql
-- 1. Kiểm tra đã mua chưa
SELECT *
FROM order_items oi
JOIN orders o ON oi.order_id = o.order_id
WHERE o.user_id = @user_id
  AND oi.variant_id IN (
      SELECT variant_id FROM product_variants
      WHERE product_id = @product_id
  )
  AND o.order_status = 'DELIVERED';

-- 2. Tạo review
INSERT INTO reviews (
    product_id,
    user_id,
    order_item_id,
    rating,
    title,
    content,
    is_verified_purchase
)
VALUES (
    @product_id,
    @user_id,
    @order_item_id,
    @rating, -- 1-5 sao
    @title,
    @content,
    1 -- Đã mua hàng = verified
);
```

---

## 📈 **Các Trigger và Stored Procedure quan trọng**

### 🔔 **Trigger 1: Cảnh báo tồn kho thấp**
```sql
CREATE TRIGGER trg_low_stock_alert
ON product_variants
AFTER UPDATE
AS
BEGIN
    -- Nếu stock_quantity < 10 → Tạo notification cho Admin
    INSERT INTO notifications (user_id, title, content, type)
    SELECT
        (SELECT user_id FROM users WHERE role_id = 1), -- Admin
        N'Cảnh báo tồn kho',
        N'SKU ' + i.sku + N' chỉ còn ' + CAST(i.stock_quantity AS NVARCHAR) + N' sản phẩm',
        'LOW_STOCK'
    FROM inserted i
    WHERE i.stock_quantity < 10
      AND i.stock_quantity != (SELECT stock_quantity FROM deleted WHERE variant_id = i.variant_id);
END;
```

### 🔄 **Trigger 2: Tự động cập nhật stock khi hủy đơn**
```sql
CREATE TRIGGER trg_restore_stock_on_cancel
ON orders
AFTER UPDATE
AS
BEGIN
    IF UPDATE(order_status)
    BEGIN
        -- Nếu đơn hàng chuyển sang CANCELLED
        IF EXISTS (SELECT 1 FROM inserted WHERE order_status = 'CANCELLED')
        BEGIN
            -- Cộng lại số lượng vào stock
            UPDATE pv
            SET stock_quantity = stock_quantity + oi.quantity
            FROM product_variants pv
            JOIN order_items oi ON pv.variant_id = oi.variant_id
            JOIN inserted i ON oi.order_id = i.order_id
            WHERE i.order_status = 'CANCELLED';
        END
    END
END;
```

### 📊 **Stored Procedure: Thống kê doanh thu**
```sql
CREATE PROCEDURE sp_get_revenue_report
    @start_date DATE,
    @end_date DATE
AS
BEGIN
    SELECT
        CAST(created_at AS DATE) AS order_date,
        COUNT(*) AS total_orders,
        SUM(final_amount) AS total_revenue,
        AVG(final_amount) AS avg_order_value
    FROM orders
    WHERE order_status NOT IN ('CANCELLED', 'RETURNED')
      AND created_at BETWEEN @start_date AND @end_date
    GROUP BY CAST(created_at AS DATE)
    ORDER BY order_date DESC;
END;
```

---

## ✅ Checklist Tối ưu Database

### 🚀 **Performance**
- [ ] Tạo INDEX cho các cột được query nhiều
  - `products.product_name`
  - `product_variants.sku`
  - `orders.order_number`
  - `users.email`

### 🔒 **Security**
- [ ] Không lưu password dạng plaintext (dùng BCrypt)
- [ ] Phân quyền database: Read-only cho reports
- [ ] Backup database định kỳ

### 📊 **Data Integrity**
- [ ] Tất cả FK có constraint ON DELETE/UPDATE
- [ ] Kiểm tra stock_quantity >= 0
- [ ] Validate email format, phone format

---

<div align="center">

**📞 Liên hệ:** support@sneakery.com

Made with ❤️ by Sneakery Team

</div>

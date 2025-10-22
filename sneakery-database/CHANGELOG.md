# 📝 Database Changelog - Version 3.1 (Synchronized)

## 🎯 Mục đích
Đồng bộ hoàn chỉnh giữa **V2 (Enhanced)** và **V3 (Complete)**, đảm bảo không thiếu bất kỳ trường hoặc tính năng quan trọng nào.

---

## ✅ Những gì đã được thêm vào từ V2 (mà V3 đã thiếu)

### 1. **Users Table**
- ✅ `email_verification_token` - Token xác thực email
- ✅ `password_reset_token` - Token reset mật khẩu
- ✅ `password_reset_expires` - Thời gian hết hạn token
- ✅ `google_id` - OAuth Google
- ✅ `facebook_id` - OAuth Facebook
- ✅ Index: `idx_users_deleted`

### 2. **Brands Table**
- ✅ `website_url` - Website của thương hiệu
- ✅ `deleted_at` - Soft delete

### 3. **Categories Table** (QUAN TRỌNG!)
- ✅ **Nested Set Model**: `lft`, `rgt`, `level` - Cho queries phân cấp hiệu quả
- ✅ `image_url` - Hình ảnh category
- ✅ `display_order` - Thứ tự hiển thị
- ✅ `deleted_at` - Soft delete
- ✅ Index: `idx_categories_lft_rgt`

### 4. **Products Table**
- ✅ `published_at` - Thời điểm xuất bản

### 5. **Product_Variants Table**
- ✅ `cost_price` - Giá vốn (để tính profit margin)
- ✅ `weight_grams` - Trọng lượng (tính phí ship)
- ✅ `deleted_at` - Soft delete

### 6. **Addresses Table**
- ✅ `latitude` - Vĩ độ
- ✅ `longitude` - Kinh độ
- ✅ `address_type` - Loại địa chỉ (home/office/other)
- ✅ `deleted_at` - Soft delete

### 7. **Coupons Table**
- ✅ `applicable_to` - Áp dụng cho (all/brand/category/product)
- ✅ `applicable_id` - ID của đối tượng áp dụng

### 8. **Carts Table**
- ✅ `expires_at` - Thời gian hết hạn giỏ hàng
- ✅ Index: `idx_carts_session`

### 9. **Orders Table** (QUAN TRỌNG!)
- ✅ `order_number` - Mã đơn hàng (ORD-20250122-0001) - **THIẾU Ở V3!**
- ✅ `tax_amount` - Thuế
- ✅ `estimated_delivery_at` - Dự kiến giao
- ✅ `delivered_at` - Thời gian đã giao
- ✅ `cancelled_at` - Thời gian hủy
- ✅ Index: `idx_orders_number`

### 10. **Payments Table**
- ✅ `credit_card` trong enum payment_method
- ✅ `refunded_at` - Thời gian hoàn tiền
- ✅ Index: `idx_payments_transaction`

### 11. **Reviews Table**
- ✅ `image_url_1`, `image_url_2`, `image_url_3` - Hình ảnh review (giữ lại cùng với `images_json`)
- ✅ `unhelpful_count` - Số lượt không hữu ích
- ✅ `approved_by` - Admin phê duyệt
- ✅ `approved_at` - Thời gian phê duyệt
- ✅ `deleted_at` - Soft delete

---

## 🆕 Bảng mới được bổ sung (từ V2)

### **23. Notifications Table**
Thông báo cho người dùng về:
- Trạng thái đơn hàng
- Khuyến mãi
- Hàng về kho
- Admin reply review
- Thông báo hệ thống

**Columns:**
- `id`, `user_id`, `type`, `title`, `message`, `link`
- `is_read`, `read_at`, `created_at`

### **24. Inventory_Logs Table**
Theo dõi mọi thay đổi về tồn kho:
- Nhập hàng (restock)
- Bán hàng (sale)
- Trả hàng (return)
- Điều chỉnh (adjustment)
- Hư hỏng (damaged)

**Columns:**
- `id`, `variant_id`, `change_type`
- `quantity_before`, `quantity_change`, `quantity_after`
- `reference_type`, `reference_id`, `note`, `changed_by`
- `created_at`

### **25. Activity_Logs Table**
Audit trail - Theo dõi mọi hành động quan trọng:
- Action, entity_type, entity_id
- old_value, new_value
- ip_address, user_agent
- Dùng cho bảo mật và compliance

**Columns:**
- `id`, `user_id`, `action`, `entity_type`, `entity_id`
- `old_value`, `new_value`
- `ip_address`, `user_agent`, `created_at`

---

## 📊 Views (từ V2)

### **vw_ProductSummary**
View tổng hợp thông tin sản phẩm:
- Tên, slug, brand_name
- avg_rating, review_count
- min_price, max_price
- total_stock
- Tối ưu cho listing pages

### **vw_OrderSummary**
View tóm tắt đơn hàng:
- order_number, customer info
- total_amount, status
- item_count
- Tối ưu cho order management

---

## ⚙️ Stored Procedures (từ V2)

### **sp_UpdateProductRating**
Tự động cập nhật rating và review count cho sản phẩm.

**Usage:**
```sql
EXEC sp_UpdateProductRating @ProductId = 1;
```

### **sp_GenerateOrderNumber**
Tự động tạo mã đơn hàng theo format: `ORD-YYYYMMDD-####`

**Usage:**
```sql
DECLARE @OrderNumber VARCHAR(50);
EXEC sp_GenerateOrderNumber @OrderNumber OUTPUT;
-- @OrderNumber = 'ORD-20250122-0001'
```

---

## 🔔 Triggers (từ V2)

### **trg_Products_UpdateTimestamp**
Tự động cập nhật `updated_at` khi product được chỉnh sửa.

### **trg_ProductVariants_InventoryLog**
Tự động ghi log vào `Inventory_Logs` khi `stock_quantity` thay đổi.

---

## 🆕 Tính năng mới giữ lại từ V3

### **Size_Charts Table**
Bảng quy đổi size giày theo brand và category.

### **Wishlists Table**
Danh sách yêu thích của user.

### **Flash_Sales Table**
Quản lý flash sale với giới hạn số lượng và thời gian.

### **Loyalty_Points Table**
Hệ thống điểm thưởng khách hàng.

### **Return_Requests Table**
Quản lý yêu cầu trả hàng/hoàn tiền.

### **Email_Templates Table**
Template email cho notification system.

---

## 📈 Tổng kết

| Metric | V2 | V3 | V3.1 (Synchronized) |
|--------|----|----|---------------------|
| **Số bảng** | 20 | 22 | **25** ✅ |
| **Views** | 2 | 0 | **2** ✅ |
| **Stored Procedures** | 2 | 0 | **2** ✅ |
| **Triggers** | 2 | 0 | **2** ✅ |
| **Tổng index** | 50+ | 35+ | **55+** ✅ |

---

## 🎯 Lợi ích của V3.1

1. ✅ **Đầy đủ tính năng**: Bao gồm tất cả features từ V2 + V3
2. ✅ **OAuth Support**: Google & Facebook login ready
3. ✅ **Nested Set Model**: Query categories hiệu quả
4. ✅ **Audit Trail**: Đầy đủ logging (Inventory + Activity)
5. ✅ **Notification System**: Thông báo real-time
6. ✅ **Order Number**: Mã đơn hàng chuẩn nghiệp vụ
7. ✅ **Soft Delete**: Có thể khôi phục dữ liệu
8. ✅ **Performance**: Views và indexes tối ưu
9. ✅ **Compliance**: Đầy đủ audit logs cho Vietnamese e-commerce regulations

---

## 🚀 Hướng dẫn sử dụng

### Để tạo database mới:
```bash
sqlcmd -S localhost -U sa -P YourPassword -i Sneakery_DB_V3.1_Synchronized.sql
```

### Hoặc dùng SSMS:
1. Mở SQL Server Management Studio
2. File > Open > File... > Chọn `Sneakery_DB_V3.1_Synchronized.sql`
3. Ấn F5 để execute

### Test accounts:
- **Admin**: `admin@sneakery.com` / `admin123`
- **User**: `user1@gmail.com` / `user123`

---

## ⚠️ Lưu ý quan trọng

1. **Backup trước khi chạy**: Script này sẽ DROP database nếu đã tồn tại!
2. **Migration**: Nếu đang dùng V2 hoặc V3, cần tạo migration script riêng
3. **Entities cần update**: Các entities trong backend cần được cập nhật để match với schema mới

---

## 📝 Next Steps

### Backend (Phase 1):
- [ ] Update tất cả entities để match V3.1 schema
- [ ] Thêm các trường mới vào DTOs
- [ ] Implement Notification service
- [ ] Implement Activity logging
- [ ] Thêm OAuth providers (Google, Facebook)

### Frontend (Phase 1):
- [ ] Implement Notification UI
- [ ] Add geo-location picker cho addresses
- [ ] Hiển thị order_number thay vì id

---

**Created**: 2025-10-22  
**Author**: AI Assistant  
**Version**: 3.1 (Synchronized)


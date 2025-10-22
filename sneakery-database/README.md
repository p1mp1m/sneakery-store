# 🗄️ Sneakery Store Database

## 📋 Danh sách các phiên bản SQL

| File | Version | Mô tả | Khuyến nghị |
|------|---------|-------|------------|
| `Sneakery_DB_V2_Enhanced.sql` | 2.0 | Phiên bản cũ với 20 bảng, views, procedures | ⚠️ Deprecated |
| `Sneakery_DB_V3_Complete.sql` | 3.0 | Thêm Flash Sales, Loyalty, Return Requests | ⚠️ Thiếu nhiều trường |
| `Sneakery_DB_V3.1_Synchronized.sql` | **3.1** | **Phiên bản đồng bộ hoàn chỉnh** | ✅ **KHUYÊN DÙNG** |

---

## 🎯 Phiên bản đề xuất: V3.1 (Synchronized)

### Tại sao chọn V3.1?

✅ **Đầy đủ tính năng nhất**: 25 bảng bao gồm tất cả features từ V2 + V3  
✅ **OAuth Ready**: Hỗ trợ đăng nhập Google, Facebook  
✅ **Audit Trail**: Đầy đủ logging (Inventory + Activity)  
✅ **Notification System**: Thông báo real-time cho users  
✅ **Performance**: 55+ indexes, 2 views tối ưu  
✅ **E-commerce Ready**: Order number, nested categories, soft delete  

---

## 🚀 Hướng dẫn cài đặt

### Phương pháp 1: Sử dụng SSMS (SQL Server Management Studio)

1. **Mở SSMS** và kết nối đến SQL Server
2. **File > Open > File...**
3. Chọn file: `Sneakery_DB_V3.1_Synchronized.sql`
4. Ấn **F5** hoặc click **Execute**
5. Đợi script chạy xong (khoảng 10-30 giây)

### Phương pháp 2: Sử dụng sqlcmd (Command Line)

```bash
# Windows
sqlcmd -S localhost -U sa -P YourPassword -i Sneakery_DB_V3.1_Synchronized.sql

# macOS/Linux
sqlcmd -S localhost -U sa -P YourPassword -i Sneakery_DB_V3.1_Synchronized.sql
```

### Phương pháp 3: Sử dụng Azure Data Studio

1. Mở Azure Data Studio
2. Kết nối đến SQL Server
3. Mở file `Sneakery_DB_V3.1_Synchronized.sql`
4. Click **Run** hoặc ấn **F5**

---

## ⚠️ Lưu ý quan trọng

### 🔴 Script sẽ DROP database nếu đã tồn tại!

Script bắt đầu với:
```sql
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'sneakery_db')
BEGIN
    ALTER DATABASE sneakery_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE sneakery_db;
END
```

**Nếu bạn đã có dữ liệu cũ và muốn giữ lại:**
- Backup database trước khi chạy script
- Hoặc comment các dòng DROP này lại
- Hoặc tạo migration script (xem phần Migration bên dưới)

---

## 🗺️ Database Schema Overview

### 👤 User Management (3 bảng)
- `Users` - Thông tin người dùng (với OAuth support)
- `Addresses` - Địa chỉ giao hàng
- `Activity_Logs` - Audit trail

### 🛍️ Product Management (9 bảng)
- `Brands` - Thương hiệu
- `Categories` - Danh mục (Nested Set Model)
- `Products` - Sản phẩm
- `Product_Categories` - Many-to-many relationship
- `Product_Variants` - Biến thể (size, color, SKU)
- `Product_Images` - Hình ảnh sản phẩm
- `Size_Charts` - Bảng quy đổi size
- `Reviews` - Đánh giá sản phẩm
- `Wishlists` - Danh sách yêu thích

### 🛒 Shopping (4 bảng)
- `Carts` - Giỏ hàng
- `Cart_Items` - Chi tiết giỏ hàng
- `Coupons` - Mã giảm giá
- `Flash_Sales` - Flash sale campaigns

### 📦 Order Management (5 bảng)
- `Orders` - Đơn hàng
- `Order_Details` - Chi tiết đơn hàng
- `Order_Status_Histories` - Lịch sử trạng thái
- `Payments` - Thanh toán
- `Return_Requests` - Yêu cầu trả hàng

### 🎁 Loyalty & Marketing (2 bảng)
- `Loyalty_Points` - Điểm thưởng
- `Email_Templates` - Template email

### 📊 System (3 bảng)
- `Notifications` - Thông báo
- `Inventory_Logs` - Log tồn kho
- `Activity_Logs` - Log hoạt động

---

## 🔐 Test Accounts

Sau khi chạy script, bạn có thể đăng nhập với các tài khoản sau:

### Admin Account
- **Email**: `admin@sneakery.com`
- **Password**: `admin123`
- **Role**: ADMIN

### Moderator Account
- **Email**: `moderator@sneakery.com`
- **Password**: `admin123`
- **Role**: MODERATOR

### User Accounts
| Email | Password | Verified |
|-------|----------|----------|
| `user1@gmail.com` | `user123` | ✅ Yes |
| `user2@gmail.com` | `user123` | ✅ Yes |
| `user3@gmail.com` | `user123` | ❌ No |

**Note**: Tất cả passwords đều được hash bằng BCrypt.

---

## 📦 Sample Data

Script đã bao gồm sample data cho:

### Products (6 sản phẩm)
1. Nike Air Max 270
2. Nike Revolution 6
3. Adidas Ultraboost 22
4. Adidas Stan Smith
5. Puma RS-X
6. Converse Chuck Taylor All Star

### Brands (6 thương hiệu)
- Nike, Adidas, Puma, New Balance, Converse, Vans

### Categories (10 danh mục)
- Giày Thể Thao (với 5 subcategories)
- Giày Lifestyle (với 3 subcategories)

### Other Data
- 19 Product Variants (với size và màu khác nhau)
- 3 Product Images
- 5 Size Chart entries
- 3 Coupons
- 2 Addresses
- 3 Email Templates

---

## 🔄 Migration từ V2 hoặc V3

Nếu bạn đã có database V2 hoặc V3 với dữ liệu production:

### Option 1: Backup và Restore
```sql
-- 1. Backup database hiện tại
BACKUP DATABASE sneakery_db TO DISK = 'C:\Backup\sneakery_db_backup.bak'

-- 2. Chạy script V3.1 để tạo database mới

-- 3. Export/Import data từ backup
```

### Option 2: Tạo Migration Script

Tạo file `Migration_to_V3.1.sql`:

```sql
USE sneakery_db;
GO

-- Thêm các cột mới vào bảng Users
ALTER TABLE Users ADD email_verification_token VARCHAR(255);
ALTER TABLE Users ADD password_reset_token VARCHAR(255);
ALTER TABLE Users ADD password_reset_expires DATETIME2;
ALTER TABLE Users ADD google_id VARCHAR(255);
ALTER TABLE Users ADD facebook_id VARCHAR(255);

-- Thêm các cột mới vào bảng Brands
ALTER TABLE Brands ADD website_url VARCHAR(255);
ALTER TABLE Brands ADD deleted_at DATETIME2 NULL;

-- ... tiếp tục với các bảng khác

-- Tạo các bảng mới
CREATE TABLE Notifications (...);
CREATE TABLE Inventory_Logs (...);
CREATE TABLE Activity_Logs (...);

-- Tạo views
CREATE VIEW vw_ProductSummary AS ...;
CREATE VIEW vw_OrderSummary AS ...;

-- Tạo stored procedures
CREATE PROCEDURE sp_UpdateProductRating ...;
CREATE PROCEDURE sp_GenerateOrderNumber ...;

-- Tạo triggers
CREATE TRIGGER trg_Products_UpdateTimestamp ...;
CREATE TRIGGER trg_ProductVariants_InventoryLog ...;
```

### Option 3: Sử dụng EF Core Migrations (Backend)

Nếu bạn sử dụng Entity Framework Core:
```bash
cd sneakery-backend
dotnet ef migrations add UpdateToV3_1
dotnet ef database update
```

---

## 🧪 Kiểm tra Database

### Kiểm tra số lượng bảng
```sql
SELECT COUNT(*) AS TableCount 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_TYPE = 'BASE TABLE';
-- Expected: 25
```

### Kiểm tra views
```sql
SELECT * FROM INFORMATION_SCHEMA.VIEWS;
-- Expected: 2 (vw_ProductSummary, vw_OrderSummary)
```

### Kiểm tra stored procedures
```sql
SELECT name FROM sys.procedures WHERE type = 'P';
-- Expected: 2 (sp_UpdateProductRating, sp_GenerateOrderNumber)
```

### Kiểm tra triggers
```sql
SELECT name FROM sys.triggers WHERE parent_class = 1;
-- Expected: 2 (trg_Products_UpdateTimestamp, trg_ProductVariants_InventoryLog)
```

### Test login
```sql
SELECT id, email, role, is_active 
FROM Users 
WHERE email = 'admin@sneakery.com';
```

### Test product summary view
```sql
SELECT TOP 5 * FROM vw_ProductSummary;
```

---

## 📊 Performance Tips

### Indexes
Database đã có 55+ indexes được tối ưu cho:
- Foreign keys
- Lookup fields (slug, sku, code)
- Status fields (is_active, is_approved)
- Timestamp fields (created_at, deleted_at)

### Views
Sử dụng views để tránh JOIN phức tạp:
```sql
-- Thay vì JOIN nhiều bảng
SELECT * FROM vw_ProductSummary WHERE brand_name = 'Nike';

-- Thay vì
SELECT p.*, b.name, MIN(pv.price) ...
FROM Products p 
JOIN Brands b ON p.brand_id = b.id 
LEFT JOIN Product_Variants pv ON p.id = pv.product_id
WHERE b.name = 'Nike'
GROUP BY ...;
```

### Stored Procedures
Sử dụng stored procedures cho business logic phức tạp:
```sql
-- Tự động tạo order number
DECLARE @OrderNumber VARCHAR(50);
EXEC sp_GenerateOrderNumber @OrderNumber OUTPUT;
-- @OrderNumber = 'ORD-20250122-0001'

-- Tự động cập nhật rating
EXEC sp_UpdateProductRating @ProductId = 1;
```

---

## 🔧 Troubleshooting

### Lỗi: "Database is already in use"
```sql
ALTER DATABASE sneakery_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE sneakery_db;
```

### Lỗi: "Cannot drop database because it is in use"
- Đóng tất cả connections trong SSMS
- Hoặc restart SQL Server service

### Lỗi: "Login failed for user 'sa'"
- Kiểm tra username/password
- Kiểm tra SQL Server Authentication đã enable chưa

### Sample data không có
- Kiểm tra phần cuối của script
- Đảm bảo đã chạy hết script (không bị interrupt)

---

## 📚 Tài liệu tham khảo

- [SQL Server Documentation](https://docs.microsoft.com/en-us/sql/)
- [Nested Set Model](https://en.wikipedia.org/wiki/Nested_set_model)
- [BCrypt Password Hashing](https://github.com/BcryptNet/bcrypt.net)
- [E-commerce Database Design Best Practices](https://www.vertabelo.com/blog/e-commerce-database-design/)

---

## 📝 Changelog

Xem chi tiết tại: [`CHANGELOG_V3.1.md`](./CHANGELOG_V3.1.md)

### V3.1 (2025-10-22) - SYNCHRONIZED
- ✅ Đồng bộ tất cả trường từ V2 và V3
- ✅ Thêm 5 bảng mới (Notifications, Inventory_Logs, Activity_Logs từ V2)
- ✅ Thêm 2 views, 2 stored procedures, 2 triggers
- ✅ 55+ indexes để tối ưu performance
- ✅ OAuth support (Google, Facebook)
- ✅ Audit trail đầy đủ

### V3.0 (2025-10-20)
- Thêm Flash_Sales, Loyalty_Points, Return_Requests
- Thêm Size_Charts, Email_Templates
- SEO fields cho Products

### V2.0 (2025-01)
- 20 bảng core
- Views, Stored Procedures, Triggers
- BCrypt passwords
- Soft delete support

---

## 🤝 Contributing

Nếu phát hiện lỗi hoặc có đề xuất cải thiện:
1. Tạo issue trong repository
2. Hoặc tạo pull request với migration script

---

## 📞 Support

Nếu gặp vấn đề khi setup database, liên hệ:
- Email: admin@sneakery.com
- GitHub Issues: [Link to repo]

---

**Last Updated**: 2025-10-22  
**Current Version**: 3.1 (Synchronized)  
**Recommended for**: Production use ✅


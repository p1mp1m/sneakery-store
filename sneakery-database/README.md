# Sneakery E-commerce Database V2 - API Compatible

## Tổng quan
Schema và dữ liệu mẫu được tối ưu để tương thích hoàn toàn với Frontend Admin API đã được phát triển.

## Cấu trúc Files
- `1_CREATE_SCHEMA.sql` - Schema database (25 tables)
- `2_INSERT_DATA.sql` - Dữ liệu mẫu cơ bản
- `3_ADD_MORE_DATA.sql` - Thêm dữ liệu mẫu bổ sung (tùy chọn)
- `4_UPDATE_PRODUCT_IMAGES.sql` - **UPDATE ẢNH THẬT** từ Unsplash (QUAN TRỌNG!)
- `README.md` - Hướng dẫn sử dụng

## Cài đặt

### 1. Tạo Database
```sql
-- Chạy file schema trước
sqlcmd -S localhost -i 1_CREATE_SCHEMA_V2.sql
```

### 2. Thêm dữ liệu mẫu
```bash
# Chạy file dữ liệu cơ bản
sqlcmd -S localhost -i sneakery-database\2_INSERT_DATA.sql

# (Tùy chọn) Thêm dữ liệu bổ sung
sqlcmd -S localhost -i sneakery-database\3_ADD_MORE_DATA.sql
```

### 3. **UPDATE ẢNH SẢN PHẨM THẬT (QUAN TRỌNG!)**
```bash
# File này sẽ thay thế URLs ảnh giả bằng ảnh thật từ Unsplash
sqlcmd -S localhost -i sneakery-database\4_UPDATE_PRODUCT_IMAGES.sql
```

> **⚠️ Lưu ý:** Nếu website vẫn hiển thị placeholder, hãy **chạy file này** để update ảnh sản phẩm thành ảnh thật!

## Cấu trúc Database

### Tables chính
- **Users** - Quản lý người dùng (Admin, Moderator, User)
- **Brands** - Thương hiệu giày
- **Categories** - Danh mục sản phẩm (hierarchical)
- **Products** - Sản phẩm giày
- **Product_Variants** - Biến thể sản phẩm (size, color, price)
- **Product_Images** - Hình ảnh sản phẩm
- **Orders** - Đơn hàng
- **Order_Details** - Chi tiết đơn hàng
- **Payments** - Thanh toán
- **Reviews** - Đánh giá sản phẩm
- **Coupons** - Mã giảm giá
- **Flash_Sales** - Flash sale
- **Notifications** - Thông báo
- **Wishlists** - Danh sách yêu thích
- **Loyalty_Points** - Điểm tích lũy
- **Addresses** - Địa chỉ giao hàng
- **Carts** - Giỏ hàng
- **Cart_Items** - Chi tiết giỏ hàng
- **Return_Requests** - Yêu cầu trả hàng
- **Inventory_Logs** - Log tồn kho
- **Activity_Logs** - Log hoạt động
- **Email_Templates** - Mẫu email
- **Order_Status_Histories** - Lịch sử trạng thái đơn hàng
- **Size_Charts** - Bảng size giày

### Views cho Admin API
- **vw_ProductSummary** - Tóm tắt sản phẩm
- **vw_OrderSummary** - Tóm tắt đơn hàng  
- **vw_AdminDashboardStats** - Thống kê dashboard admin

### Stored Procedures
- **sp_UpdateProductRating** - Cập nhật rating sản phẩm
- **sp_GenerateOrderNumber** - Tạo mã đơn hàng

## Dữ liệu mẫu

### Users (12 users)
- 2 Admin users: `admin@sneakery.com`, `moderator@sneakery.com`
- 10 Regular users: `user1@example.com` đến `user10@example.com`
- Password mặc định: `password` (đã hash)

### Brands (10 brands)
- Nike, Adidas, Jordan, Converse, Vans, Puma, New Balance, Reebok, Under Armour, ASICS

### Categories (16 categories)
- Cấu trúc hierarchical với 4 main categories và 12 subcategories
- Giày Nam, Giày Nữ, Giày Trẻ Em, Phụ Kiện

### Products (15 products)
- 15 sản phẩm giày từ các thương hiệu khác nhau
- Đầy đủ thông tin: tên, mô tả, rating, view count, order count

### Product Variants (30 variants)
- Mỗi sản phẩm có 2-6 variants (size, color)
- Giá gốc, giá sale, tồn kho đầy đủ

### Orders (5 orders)
- 5 đơn hàng mẫu với các trạng thái khác nhau
- Đầy đủ thông tin: địa chỉ, thanh toán, chi tiết

### Coupons (5 coupons)
- WELCOME10: Giảm 10% cho khách mới
- SAVE50K: Giảm 50k cho đơn từ 1M
- NIKE20: Giảm 20% cho giày Nike
- SUMMER15: Giảm 15% mùa hè
- VIP30: Giảm 30% cho khách VIP

### Flash Sales (5 flash sales)
- 5 flash sale cho các sản phẩm khác nhau
- Giảm giá từ 15-35%

## API Compatibility

### Admin Dashboard Stats
```json
{
  "total_users": 12,
  "total_products": 15,
  "total_orders": 5,
  "total_revenue": 15640000,
  "orders_last_30d": 5,
  "revenue_last_30d": 15640000
}
```

### Product List Response
```json
{
  "content": [...],
  "totalElements": 15,
  "totalPages": 2,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false
}
```

### Order List Response
```json
{
  "content": [...],
  "totalElements": 5,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

## Kết nối với Frontend

### Database Connection String
```
Server=localhost;Database=sneakery_db;Trusted_Connection=true;
```

### API Endpoints được hỗ trợ
- `/api/admin/dashboard/stats` - Thống kê dashboard
- `/api/admin/products` - Quản lý sản phẩm
- `/api/admin/orders` - Quản lý đơn hàng
- `/api/admin/users` - Quản lý người dùng
- `/api/admin/brands` - Quản lý thương hiệu
- `/api/admin/categories` - Quản lý danh mục
- `/api/admin/coupons` - Quản lý mã giảm giá
- `/api/admin/flash-sales` - Quản lý flash sale
- `/api/admin/reviews` - Quản lý đánh giá
- `/api/admin/notifications` - Quản lý thông báo
- `/api/admin/inventory` - Quản lý tồn kho
- `/api/admin/activity-logs` - Log hoạt động

## Lưu ý quan trọng

1. **Password mặc định**: Tất cả user có password là `password` (đã được hash)
2. **Admin access**: Sử dụng `admin@sneakery.com` để đăng nhập admin
3. **API testing**: Dữ liệu mẫu đủ để test tất cả chức năng admin
4. **Performance**: Đã tối ưu indexes cho các query thường dùng
5. **Data integrity**: Đầy đủ foreign keys và constraints

## Bước tiếp theo

1. Chạy 2 file SQL để tạo database và dữ liệu
2. Cấu hình backend API để kết nối database
3. Test frontend admin với dữ liệu mẫu
4. Sau khi admin hoàn thành, chuyển sang phát triển panel người dùng

## Troubleshooting

### Lỗi thường gặp
1. **Login failed**: Kiểm tra email và password
2. **Permission denied**: Đảm bảo user có role ADMIN
3. **Data not found**: Kiểm tra dữ liệu đã được insert chưa
4. **Connection error**: Kiểm tra connection string

### Debug queries
```sql
-- Kiểm tra users
SELECT * FROM Users WHERE role = 'ADMIN';

-- Kiểm tra products
SELECT COUNT(*) FROM Products;

-- Kiểm tra orders
SELECT COUNT(*) FROM Orders;
```

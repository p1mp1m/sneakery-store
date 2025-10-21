-- ===============================================================
-- DỰ ÁN: Sneakery - Hệ thống bán giày Sneaker cao cấp
-- PHIÊN BẢN: Final
-- MÔ TẢ:
--   - Cấu trúc database (schema) đầy đủ cho SQL Server 2022
--   - Dữ liệu mẫu thực tế (Việt Nam)
--   - Có chú thích tiếng Việt mô tả từng phần
-- ===============================================================

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- ===============================================================
-- XÓA DATABASE NẾU ĐÃ TỒN TẠI VÀ TẠO MỚI
-- ===============================================================
IF DB_ID('sneakery_db') IS NOT NULL
BEGIN
    ALTER DATABASE sneakery_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE sneakery_db;
END
GO

CREATE DATABASE sneakery_db;
GO

USE sneakery_db;
GO

-- ===============================================================
-- 1️⃣ BẢNG NGƯỜI DÙNG (Users) - Lưu thông tin khách hàng
-- ===============================================================
CREATE TABLE Users (
    id BIGINT IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name NVARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    is_active BIT DEFAULT 1
);
GO

-- ===============================================================
-- 2️⃣ BẢNG THƯƠNG HIỆU (Brands)
-- ===============================================================
CREATE TABLE Brands (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(255) NOT NULL UNIQUE,
    slug VARCHAR(255) NOT NULL UNIQUE
);
GO

-- ===============================================================
-- 3️⃣ BẢNG DANH MỤC (Categories) - Dùng để phân loại sản phẩm
-- ===============================================================
CREATE TABLE Categories (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(255) NOT NULL UNIQUE,
    slug VARCHAR(255) NOT NULL UNIQUE,
    parent_id INT NULL,
    FOREIGN KEY (parent_id) REFERENCES Categories(id) ON DELETE NO ACTION
);
GO

-- ===============================================================
-- 4️⃣ BẢNG SẢN PHẨM (Products) - Thông tin sản phẩm chính
-- ===============================================================
CREATE TABLE Products (
    id BIGINT IDENTITY PRIMARY KEY,
    brand_id INT NOT NULL,
    name NVARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    is_active BIT DEFAULT 1,
    FOREIGN KEY (brand_id) REFERENCES Brands(id)
);
GO

-- ===============================================================
-- 5️⃣ BẢNG PHIÊN BẢN SẢN PHẨM (Product_Variants)
-- ===============================================================
CREATE TABLE Product_Variants (
    id BIGINT IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    size VARCHAR(10) NOT NULL,
    color NVARCHAR(100) NOT NULL,
    price_base DECIMAL(18,2) NOT NULL,
    price_sale DECIMAL(18,2) NULL,
    stock_quantity INT DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT UQ_ProductVariant UNIQUE (product_id, size, color)
);
GO

-- ===============================================================
-- 6️⃣ BẢNG LIÊN KẾT SẢN PHẨM - DANH MỤC (Product_Categories)
-- ===============================================================
CREATE TABLE Product_Categories (
    product_id BIGINT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE CASCADE
);
GO

-- ===============================================================
-- 7️⃣ BẢNG MÃ GIẢM GIÁ (Coupons)
-- ===============================================================
CREATE TABLE Coupons (
    id INT IDENTITY PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    discount_type VARCHAR(20) CHECK (discount_type IN ('percent', 'amount')),
    value DECIMAL(18,2) NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    max_uses INT,
    uses_count INT DEFAULT 0
);
GO

-- ===============================================================
-- 8️⃣ BẢNG ĐỊA CHỈ (Addresses)
-- ===============================================================
CREATE TABLE Addresses (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id BIGINT NULL,
    recipient_name NVARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    line1 NVARCHAR(255) NOT NULL,
    line2 NVARCHAR(255) NULL,
    city NVARCHAR(100) NOT NULL,
    district NVARCHAR(100) NULL,
    province NVARCHAR(100) NULL,
    postal_code VARCHAR(20) NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL
);
GO

-- ===============================================================
-- 9️⃣ BẢNG ĐƠN HÀNG (Orders)
-- ===============================================================
CREATE TABLE Orders (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id BIGINT NULL,
    coupon_id INT NULL,
    address_shipping_id BIGINT NULL,
    address_billing_id BIGINT NULL,
    total_amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('Pending','Processing','Paid','Shipped','Completed','Cancelled','Returned')) DEFAULT 'Pending',
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL,
    FOREIGN KEY (coupon_id) REFERENCES Coupons(id) ON DELETE SET NULL,
    FOREIGN KEY (address_shipping_id) REFERENCES Addresses(id) ON DELETE NO ACTION,
    FOREIGN KEY (address_billing_id) REFERENCES Addresses(id) ON DELETE NO ACTION
);
GO

-- ===============================================================
-- 🔟 BẢNG CHI TIẾT ĐƠN HÀNG (Order_Details)
-- ===============================================================
CREATE TABLE Order_Details (
    id BIGINT IDENTITY PRIMARY KEY,
    order_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES Product_Variants(id)
);
GO

-- ===============================================================
-- 11️⃣ BẢNG TRẠNG THÁI ĐƠN HÀNG (Order_Status_Histories)
-- ===============================================================
CREATE TABLE Order_Status_Histories (
    id BIGINT IDENTITY PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    changed_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE
);
GO

-- ===============================================================
-- 12️⃣ BẢNG GIỎ HÀNG (Carts)
-- ===============================================================
CREATE TABLE Carts (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id BIGINT NULL,
    session_id VARCHAR(255) NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL
);
GO

-- ===============================================================
-- 13️⃣ BẢNG CHI TIẾT GIỎ HÀNG (Cart_Items)
-- ===============================================================
CREATE TABLE Cart_Items (
    id BIGINT IDENTITY PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES Carts(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES Product_Variants(id) ON DELETE CASCADE,
    CONSTRAINT UQ_CartItem UNIQUE (cart_id, variant_id)
);
GO

-- ===============================================================
-- 14️⃣ BẢNG THANH TOÁN (Payments)
-- ===============================================================
CREATE TABLE Payments (
    id BIGINT IDENTITY PRIMARY KEY,
    order_id BIGINT NOT NULL,
    method VARCHAR(20) CHECK (method IN ('online','cod')) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('pending','completed','failed')) DEFAULT 'pending',
    paid_at DATETIME NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE
);
GO

-- ===============================================================
-- 15️⃣ BẢNG ĐÁNH GIÁ SẢN PHẨM (Reviews)
-- ===============================================================
CREATE TABLE Reviews (
    id BIGINT IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    body NVARCHAR(MAX),
    is_approved BIT DEFAULT 0,
    is_verified_purchase BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

GO

USE sneakery_db;
GO

-- ===============================================================
-- BẢNG USERS - DỮ LIỆU MẪU (30 người Việt Nam, random trạng thái)
-- ===============================================================

GO

INSERT INTO Users (email, password_hash, full_name, phone_number, is_active) VALUES
('nguyenvanphuc@gmail.com', 'p@ss9321', N'Nguyễn Văn Phúc', '0905123456', 1),
('tranthilananh@gmail.com', 'p@ss8472', N'Trần Thị Lan Anh', '0906234789', 1),
('leminhhoang@gmail.com', 'p@ss7615', N'Lê Minh Hoàng', '0907345987', 0),
('phamthianhthu@gmail.com', 'p@ss5829', N'Phạm Thị Anh Thư', '0908456123', 1),
('dangthanhdat@gmail.com', 'p@ss3247', N'Đặng Thành Đạt', '0909567345', 1),
('vuthimai@gmail.com', 'p@ss6138', N'Vũ Thị Mai', '0910678567', 0),
('hoangminhtuan@gmail.com', 'p@ss4953', N'Hoàng Minh Tuấn', '0911789789', 1),
('buitrungkien@gmail.com', 'p@ss8759', N'Bùi Trung Kiên', '0912890901', 1),
('ngothithao@gmail.com', 'p@ss2398', N'Ngô Thị Thảo', '0913902123', 1),
('doanphihung@gmail.com', 'p@ss7134', N'Đoàn Phi Hùng', '0914012345', 0),
('trinhthithu@gmail.com', 'p@ss9812', N'Trịnh Thị Thu', '0915123567', 1),
('maivanchien@gmail.com', 'p@ss3657', N'Mai Văn Chiến', '0916234789', 1),
('dinhthebao@gmail.com', 'p@ss6452', N'Đinh Thế Bảo', '0917345901', 0),
('tranngocnam@gmail.com', 'p@ss8216', N'Trần Ngọc Nam', '0918456123', 1),
('ngoquanghuy@gmail.com', 'p@ss9532', N'Ngô Quang Huy', '0919567345', 1),
('lethuydung@gmail.com', 'p@ss7321', N'Lê Thùy Dung', '0920678567', 1),
('phamthikimanh@gmail.com', 'p@ss4368', N'Phạm Thị Kim Anh', '0921789789', 0),
('nguyenthanhluan@gmail.com', 'p@ss2543', N'Nguyễn Thanh Luân', '0922890901', 1),
('hoangminhchau@gmail.com', 'p@ss6438', N'Hoàng Minh Châu', '0923902123', 1),
('vuquynhnhu@gmail.com', 'p@ss8954', N'Vũ Quỳnh Như', '0924012345', 0),
('doanthanhphong@gmail.com', 'p@ss5213', N'Đoàn Thành Phong', '0925123567', 1),
('truongthithuy@gmail.com', 'p@ss1758', N'Trương Thị Thủy', '0926234789', 1),
('buiducthanh@gmail.com', 'p@ss4786', N'Bùi Đức Thành', '0927345901', 0),
('nguyenthihuong@gmail.com', 'p@ss9327', N'Nguyễn Thị Hương', '0928456123', 1),
('tranminhkhoa@gmail.com', 'p@ss8264', N'Trần Minh Khoa', '0929567345', 1),
('levanthanh@gmail.com', 'p@ss6735', N'Lê Văn Thành', '0930678567', 1),
('phamtuananh@gmail.com', 'p@ss2349', N'Phạm Tuấn Anh', '0931789789', 0),
('hoangthuyduyen@gmail.com', 'p@ss7614', N'Hoàng Thùy Duyên', '0932890901', 1),
('vominhtri@gmail.com', 'p@ss9428', N'Võ Minh Trí', '0933902123', 1),
('dangthanhson@gmail.com', 'p@ss5137', N'Đặng Thanh Sơn', '0934012345', 0);

GO

-- ===============================================================
-- BẢNG BRANDS - DỮ LIỆU MẪU (10 thương hiệu giày)
-- ===============================================================

GO

INSERT INTO Brands (name, slug) VALUES
(N'Nike', 'nike'),
(N'Adidas', 'adidas'),
(N'Converse', 'converse'),
(N'Vans', 'vans'),
(N'Puma', 'puma'),
(N'New Balance', 'new-balance'),
(N'Reebok', 'reebok'),
(N'Jordan', 'jordan'),
(N'Asics', 'asics'),
(N'Under Armour', 'under-armour');

GO

-- ===============================================================
-- BẢNG CATEGORIES - DỮ LIỆU MẪU (10 danh mục giày)
-- ===============================================================

GO

INSERT INTO Categories (name, slug, parent_id) VALUES
(N'Sneaker Nam', 'sneaker-nam', NULL),
(N'Sneaker Nữ', 'sneaker-nu', NULL),
(N'Sneaker Unisex', 'sneaker-unisex', NULL),
(N'Giày Chạy Bộ', 'giay-chay-bo', 1),
(N'Giày Thể Thao', 'giay-the-thao', 3),
(N'Giày Cổ Cao', 'giay-co-cao', 3),
(N'Giày Cổ Thấp', 'giay-co-thap', 3),
(N'Giày Classic', 'giay-classic', 1),
(N'Giày Retro', 'giay-retro', 2),
(N'Giày Giới Hạn', 'giay-gioi-han', 3);

GO

-- ===============================================================
-- BẢNG PRODUCTS - DỮ LIỆU MẪU (20 sản phẩm)
-- ===============================================================
INSERT INTO Products (brand_id, name, slug, description, is_active) VALUES
(1, N'Nike Air Force 1', 'nike-air-force-1', N'Giày sneaker huyền thoại của Nike', 1),
(1, N'Nike Air Max 270', 'nike-air-max-270', N'Giày chạy bộ thời trang Nike', 1),
(2, N'Adidas Superstar', 'adidas-superstar', N'Giày cổ điển của Adidas', 1),
(2, N'Adidas Ultraboost', 'adidas-ultraboost', N'Giày chạy bộ êm chân', 1),
(3, N'Converse Chuck Taylor All Star', 'converse-chuck-taylor', N'Giày classic Converse', 1),
(4, N'Vans Old Skool', 'vans-old-skool', N'Giày skate Vans Old Skool', 1),
(5, N'Puma Suede Classic', 'puma-suede-classic', N'Giày Puma cổ điển', 1),
(6, N'New Balance 574', 'new-balance-574', N'Giày thể thao New Balance', 1),
(7, N'Reebok Classic Leather', 'reebok-classic-leather', N'Giày sneaker Reebok cổ điển', 1),
(8, N'Jordan 1 Retro', 'jordan-1-retro', N'Giày bóng rổ Jordan 1 Retro', 1),
(9, N'Asics Gel-Kayano', 'asics-gel-kayano', N'Giày chạy bộ Asics Gel-Kayano', 1),
(10, N'Under Armour HOVR', 'under-armour-hovr', N'Giày tập luyện Under Armour', 1),
(1, N'Nike Dunk Low', 'nike-dunk-low', N'Giày Nike Dunk Low cực hot', 1),
(2, N'Adidas NMD', 'adidas-nmd', N'Giày thể thao Adidas NMD', 1),
(3, N'Converse One Star', 'converse-one-star', N'Giày Converse One Star', 1),
(4, N'Vans Sk8-Hi', 'vans-sk8-hi', N'Giày Vans Sk8-Hi cổ cao', 1),
(5, N'Puma RS-X', 'puma-rs-x', N'Giày Puma RS-X năng động', 1),
(6, N'New Balance 990', 'new-balance-990', N'Giày New Balance cao cấp', 1),
(8, N'Jordan 4 Retro', 'jordan-4-retro', N'Giày Jordan 4 Retro nổi bật', 1),
(9, N'Asics Gel-Nimbus', 'asics-gel-nimbus', N'Giày chạy bộ Asics Gel-Nimbus', 1);

GO

-- ===============================================================
-- BẢNG PRODUCT_VARIANTS - DỮ LIỆU MẪU (màu tiếng Việt, Unicode)
-- ===============================================================
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, stock_quantity) VALUES
-- Nike Air Force 1
(1, 'NAF1-TR-38', '38', N'Trắng', 3500000, 3200000, 10),
(1, 'NAF1-TR-39', '39', N'Trắng', 3500000, 3200000, 15),
(1, 'NAF1-Đ-40', '40', N'Đen', 3500000, NULL, 8),
-- Nike Air Max 270
(2, 'NAM270-TR-38', '38', N'Trắng', 4000000, 3800000, 12),
(2, 'NAM270-Đ-41', '41', N'Đen', 4000000, NULL, 7),
-- Adidas Superstar
(3, 'ADS-SUP-TR-38', '38', N'Trắng', 3200000, 3000000, 20),
(3, 'ADS-SUP-Đ-40', '40', N'Đen', 3200000, NULL, 10),
-- Adidas Ultraboost
(4, 'ADS-UB-TR-39', '39', N'Trắng', 4500000, 4200000, 5),
(4, 'ADS-UB-XA-42', '42', N'Xám', 4500000, NULL, 7),
-- Converse Chuck Taylor
(5, 'CON-CT-TR-38', '38', N'Trắng', 1500000, 1400000, 25),
(5, 'CON-CT-Đ-39', '39', N'Đen', 1500000, NULL, 18),
-- Vans Old Skool
(6, 'VAN-OS-Đ-38', '38', N'Đen', 1800000, NULL, 15),
(6, 'VAN-OS-TR-39', '39', N'Trắng', 1800000, 1700000, 10),
-- Puma Suede Classic
(7, 'PUM-SC-Đ-40', '40', N'Đen', 2200000, NULL, 8),
(7, 'PUM-SC-Đ-41', '41', N'Đỏ', 2200000, 2000000, 12),
-- New Balance 574
(8, 'NB-574-XA-39', '39', N'Xám', 2800000, 2600000, 10),
(8, 'NB-574-XD-42', '42', N'Xanh dương', 2800000, NULL, 6),
-- Reebok Classic Leather
(9, 'RB-CL-TR-38', '38', N'Trắng', 2500000, 2300000, 14),
(9, 'RB-CL-Đ-40', '40', N'Đen', 2500000, NULL, 9),
-- Jordan 1 Retro
(10, 'J1R-Đ-41', '41', N'Đỏ', 5500000, 5000000, 7),
(10, 'J1R-Đ-42', '42', N'Đen', 5500000, NULL, 5);

GO

-- ===============================================================
-- BẢNG Product_Categories - LIÊN KẾT SẢN PHẨM & DANH MỤC
-- ===============================================================

GO

INSERT INTO Product_Categories (product_id, category_id) VALUES
-- Nike Air Force 1 -> Sneaker Nam
(1, 1),
-- Nike Air Max 270 -> Giày Chạy Bộ
(2, 4),
-- Adidas Superstar -> Sneaker Unisex
(3, 3),
-- Adidas Ultraboost -> Giày Chạy Bộ
(4, 4),
-- Converse Chuck Taylor -> Giày Classic
(5, 8),
-- Vans Old Skool -> Giày Cổ Thấp
(6, 7),
-- Puma Suede Classic -> Giày Classic
(7, 8),
-- New Balance 574 -> Giày Thể Thao
(8, 5),
-- Reebok Classic Leather -> Giày Classic
(9, 8),
-- Jordan 1 Retro -> Giày Cổ Cao
(10, 6),
-- Nike Dunk Low -> Sneaker Nam
(11, 1),
-- Adidas NMD -> Giày Thể Thao
(12, 5),
-- Converse One Star -> Giày Classic
(13, 8),
-- Vans Sk8-Hi -> Giày Cổ Cao
(14, 6),
-- Puma RS-X -> Giày Thể Thao
(15, 5),
-- New Balance 990 -> Giày Thể Thao
(16, 5),
-- Jordan 4 Retro -> Giày Cổ Cao
(17, 6),
-- Asics Gel-Kayano -> Giày Chạy Bộ
(18, 4),
-- Asics Gel-Nimbus -> Giày Chạy Bộ
(19, 4),
-- Under Armour HOVR -> Giày Thể Thao
(20, 5);

GO

INSERT INTO Coupons (code, discount_type, value, start_at, end_at, max_uses) VALUES
('SALE10', 'percent', 10, GETDATE(), DATEADD(DAY, 30, GETDATE()), 100),
('NEWYEAR50', 'amount', 50000, GETDATE(), DATEADD(DAY, 60, GETDATE()), 50),
('VIP20', 'percent', 20, GETDATE(), DATEADD(DAY, 90, GETDATE()), NULL);
GO

INSERT INTO Addresses (user_id, recipient_name, phone, line1, line2, city, district, province, postal_code) VALUES
(1, N'Nguyễn Văn Phúc', '0905123456', N'123 Lê Lợi', NULL, N'Hà Nội', N'Hai Bà Trưng', N'Hà Nội', '100000'),
(2, N'Trần Thị Lan Anh', '0906234789', N'456 Nguyễn Trãi', N'P.2', N'Hồ Chí Minh', N'Quận 5', N'HCM', '700000'),
(3, N'Lê Minh Hoàng', '0907345987', N'789 Trần Phú', NULL, N'Đà Nẵng', N'Hải Châu', N'Đà Nẵng', '550000');
GO

INSERT INTO Orders (user_id, coupon_id, address_shipping_id, address_billing_id, total_amount, status) VALUES
(1, 1, 1, 1, 3500000, 'Pending'),
(2, NULL, 2, 2, 4000000, 'Processing'),
(3, 2, 3, 3, 1500000, 'Paid');
GO

INSERT INTO Order_Details (order_id, variant_id, quantity, unit_price) VALUES
(1, 1, 1, 3200000),
(2, 4, 2, 4000000),
(3, 9, 1, 1400000);
GO

INSERT INTO Carts (user_id, session_id) VALUES
(1, NULL),
(2, NULL),
(NULL, 'sess12345');
GO

INSERT INTO Cart_Items (cart_id, variant_id, quantity) VALUES
(1, 2, 1),
(2, 5, 2),
(3, 6, 1);
GO

INSERT INTO Payments (order_id, method, amount, status, paid_at) VALUES
(1, 'online', 3200000, 'completed', GETDATE()),
(2, 'cod', 8000000, 'pending', NULL),
(3, 'online', 1400000, 'completed', GETDATE());
GO

INSERT INTO Reviews (product_id, user_id, rating, body, is_approved, is_verified_purchase) VALUES
(1, 1, 5, N'Giày đẹp, êm chân', 1, 1),
(3, 2, 4, N'Giày ổn, giá hợp lý', 1, 1),
(5, 3, 3, N'Giày ổn nhưng hơi cứng', 0, 1);
GO

INSERT INTO Order_Status_Histories (order_id, status, changed_at) VALUES
-- Đơn hàng 1
(1, N'Pending', DATEADD(HOUR, -5, GETDATE())),
(1, N'Processing', DATEADD(HOUR, -4, GETDATE())),
(1, N'Paid', DATEADD(HOUR, -3, GETDATE())),
-- Đơn hàng 2
(2, N'Pending', DATEADD(DAY, -2, GETDATE())),
(2, N'Processing', DATEADD(DAY, -1, GETDATE())),
-- Đơn hàng 3
(3, N'Pending', DATEADD(DAY, -1, GETDATE())),
(3, N'Paid', DATEADD(HOUR, -12, GETDATE())),
(3, N'Shipped', DATEADD(HOUR, -6, GETDATE())),
(3, N'Completed', DATEADD(HOUR, -1, GETDATE()));
GO


-- ✅ Kiểm tra nhanh:

SELECT COUNT(*) AS TotalUsers FROM Users;
SELECT COUNT(*) AS TotalBrands FROM Brands;
SELECT id, name, parent_id FROM Categories;
SELECT COUNT(*) AS TotalProducts FROM Products;
SELECT id, name, brand_id FROM Products;
SELECT COUNT(*) AS TotalVariants FROM Product_Variants;
SELECT * FROM Product_Variants;
SELECT COUNT(*) AS TotalProductCategory FROM Product_Categories;
SELECT * FROM Product_Categories;
SELECT * FROM Coupons;
SELECT * FROM Addresses;
SELECT * FROM Orders;
SELECT * FROM Order_Details;
SELECT * FROM Carts;
SELECT * FROM Cart_Items;
SELECT * FROM Payments;
SELECT * FROM Reviews;
SELECT * FROM Order_Status_Histories ORDER BY order_id, changed_at;






-- ✅ Kiểm tra sâu:

-- ===============================================================
-- SCRIPT KIỂM TRA TOÀN BỘ FK & IN KẾT QUẢ
-- ===============================================================

DECLARE @Errors TABLE (TableName NVARCHAR(100), FKDescription NVARCHAR(MAX));

-- 1️⃣ Product_Variants -> Products
INSERT INTO @Errors
SELECT N'Product_Variants', N'VariantID ' + CAST(pv.id AS NVARCHAR) + ' has invalid product_id ' + CAST(pv.product_id AS NVARCHAR)
FROM Product_Variants pv
LEFT JOIN Products p ON pv.product_id = p.id
WHERE p.id IS NULL;

-- 2️⃣ Product_Categories -> Products/Categories
INSERT INTO @Errors
SELECT N'Product_Categories', N'product_id ' + CAST(pc.product_id AS NVARCHAR) + ' or category_id ' + CAST(pc.category_id AS NVARCHAR) + ' invalid'
FROM Product_Categories pc
LEFT JOIN Products p ON pc.product_id = p.id
LEFT JOIN Categories c ON pc.category_id = c.id
WHERE p.id IS NULL OR c.id IS NULL;

-- 3️⃣ Orders -> Users/Coupons/Addresses
INSERT INTO @Errors
SELECT N'Orders', N'OrderID ' + CAST(o.id AS NVARCHAR) + ' has invalid FK'
FROM Orders o
LEFT JOIN Users u ON o.user_id = u.id
LEFT JOIN Coupons c ON o.coupon_id = c.id
LEFT JOIN Addresses s ON o.address_shipping_id = s.id
LEFT JOIN Addresses b ON o.address_billing_id = b.id
WHERE (o.user_id IS NOT NULL AND u.id IS NULL)
   OR (o.coupon_id IS NOT NULL AND c.id IS NULL)
   OR s.id IS NULL
   OR b.id IS NULL;

-- 4️⃣ Order_Details -> Orders/Product_Variants
INSERT INTO @Errors
SELECT N'Order_Details', N'OrderDetailID ' + CAST(od.id AS NVARCHAR) + ' has invalid FK'
FROM Order_Details od
LEFT JOIN Orders o ON od.order_id = o.id
LEFT JOIN Product_Variants pv ON od.variant_id = pv.id
WHERE o.id IS NULL OR pv.id IS NULL;

-- 5️⃣ Cart_Items -> Carts/Product_Variants
INSERT INTO @Errors
SELECT N'Cart_Items', N'CartItemID ' + CAST(ci.id AS NVARCHAR) + ' has invalid FK'
FROM Cart_Items ci
LEFT JOIN Carts c ON ci.cart_id = c.id
LEFT JOIN Product_Variants pv ON ci.variant_id = pv.id
WHERE c.id IS NULL OR pv.id IS NULL;

-- 6️⃣ Payments -> Orders
INSERT INTO @Errors
SELECT N'Payments', N'PaymentID ' + CAST(p.id AS NVARCHAR) + ' has invalid order_id ' + CAST(p.order_id AS NVARCHAR)
FROM Payments p
LEFT JOIN Orders o ON p.order_id = o.id
WHERE o.id IS NULL;

-- 7️⃣ Reviews -> Products/Users
INSERT INTO @Errors
SELECT N'Reviews', N'ReviewID ' + CAST(r.id AS NVARCHAR) + ' has invalid FK'
FROM Reviews r
LEFT JOIN Products p ON r.product_id = p.id
LEFT JOIN Users u ON r.user_id = u.id
WHERE p.id IS NULL OR u.id IS NULL;

-- 8️⃣ Order_Status_Histories -> Orders
INSERT INTO @Errors
SELECT N'Order_Status_Histories', N'StatusHistoryID ' + CAST(osh.id AS NVARCHAR) + ' has invalid order_id ' + CAST(osh.order_id AS NVARCHAR)
FROM Order_Status_Histories osh
LEFT JOIN Orders o ON osh.order_id = o.id
WHERE o.id IS NULL;

-- Kết quả
IF NOT EXISTS (SELECT 1 FROM @Errors)
    PRINT N'✅ Tất cả FK đều hợp lệ. OK!';
ELSE
    SELECT * FROM @Errors;


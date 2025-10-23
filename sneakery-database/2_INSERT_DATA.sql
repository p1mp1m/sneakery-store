-- =====================================================
-- SNEAKERY E-COMMERCE - INSERT DATA
-- =====================================================
-- File này chứa TOÀN BỘ dữ liệu: users + sample data
-- Chạy AFTER file 1_CREATE_SCHEMA.sql
-- =====================================================
-- PASSWORD INFO:
-- testadmin@sneakery.com = admin123
-- testuser@sneakery.com = user123
-- =====================================================

USE sneakery_db;
GO

PRINT '';
PRINT '=====================================================';
PRINT 'DANG INSERT DU LIEU...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 1. INSERT USERS (2 users cố định)
-- =====================================================
PRINT '[1/12] Dang insert users...';

INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified) VALUES
('testadmin@sneakery.com', '$2a$10$SOVwX.mgssz5J2b2ZZa3Y.0z1VTgMTi9Rr5lYcB5VSJrPfgawEkQ2', N'Test Admin', '0901234567', 'ADMIN', 1, 1),
('testuser@sneakery.com', '$2a$10$mH2S4TRYYSn6PYrcDWpL2O9.fVj4gK.1Pp8CpBEV6D3G838y7ADhO', N'Test User', '0912345678', 'USER', 1, 1);
GO

DECLARE @AdminId BIGINT = (SELECT id FROM Users WHERE email = 'testadmin@sneakery.com');
DECLARE @UserId BIGINT = (SELECT id FROM Users WHERE email = 'testuser@sneakery.com');

-- Insert addresses cho cả 2 users
INSERT INTO Addresses (user_id, recipient_name, phone, line1, ward, district, city, is_default, address_type) VALUES
(@AdminId, N'Admin Sneakery', '0901234567', N'456 Lê Lợi', N'Phường Bến Thành', N'Quận 1', N'TP. Hồ Chí Minh', 1, 'office'),
(@UserId, N'User Sneakery', '0912345678', N'123 Nguyễn Huệ', N'Phường Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', 1, 'home');
GO

PRINT '   Da tao 2 users voi day du thong tin + addresses';
PRINT '';

-- =====================================================
-- 2. INSERT BRANDS
-- =====================================================
PRINT '[2/12] Dang insert brands...';

INSERT INTO Brands (name, slug, description, is_active) VALUES
(N'Nike', 'nike', N'Just Do It', 1),
(N'Adidas', 'adidas', N'Impossible is Nothing', 1),
(N'Puma', 'puma', N'Forever Faster', 1),
(N'New Balance', 'new-balance', N'Worn by Supermodels', 1),
(N'Converse', 'converse', N'All Star', 1),
(N'Vans', 'vans', N'Off The Wall', 1);
GO

PRINT '   Da them 6 brands';
PRINT '';

-- =====================================================
-- 3. INSERT CATEGORIES
-- =====================================================
PRINT '[3/12] Dang insert categories...';

INSERT INTO Categories (name, slug, lft, rgt, level, parent_id, is_active) VALUES
(N'Giày Thể Thao', 'giay-the-thao', 1, 12, 0, NULL, 1),
  (N'Giày Chạy Bộ', 'giay-chay-bo', 2, 3, 1, 1, 1),
  (N'Giày Bóng Đá', 'giay-bong-da', 4, 5, 1, 1, 1),
  (N'Giày Bóng Rổ', 'giay-bong-ro', 6, 7, 1, 1, 1),
  (N'Giày Tennis', 'giay-tennis', 8, 9, 1, 1, 1),
  (N'Giày Gym', 'giay-gym', 10, 11, 1, 1, 1),
(N'Giày Lifestyle', 'giay-lifestyle', 13, 20, 0, NULL, 1),
  (N'Giày Sneaker', 'giay-sneaker', 14, 15, 1, 7, 1),
  (N'Giày Slip-On', 'giay-slip-on', 16, 17, 1, 7, 1),
  (N'Giày Cao Cổ', 'giay-cao-co', 18, 19, 1, 7, 1);
GO

PRINT '   Da them 10 categories';
PRINT '';

-- =====================================================
-- 4. INSERT PRODUCTS (21 products)
-- =====================================================
PRINT '[4/12] Dang insert products...';

-- Insert products
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, meta_title, meta_description) VALUES
-- Nike Products
(1, N'Nike Air Force 1 ''07', 'nike-air-force-1-07', N'Giày Nike Air Force 1 ''07 - Icon của streetwear với thiết kế classic', N'Icon streetwear classic', 1, 1, 0, N'Nike Air Force 1 ''07', N'Giày Nike Air Force 1 classic'),
(1, N'Nike Dunk Low Retro', 'nike-dunk-low-retro', N'Nike Dunk Low Retro - Phong cách retro đậm chất thập niên 80s', N'Retro style đậm chất 80s', 1, 1, 1, N'Nike Dunk Low Retro', N'Giày Nike Dunk Low phong cách retro'),
(1, N'Nike Blazer Mid ''77', 'nike-blazer-mid-77', N'Nike Blazer Mid ''77 - Vintage basketball style', N'Vintage basketball classic', 1, 0, 0, N'Nike Blazer Mid ''77', N'Giày Nike Blazer vintage'),
(1, N'Nike React Infinity Run', 'nike-react-infinity-run', N'Nike React Infinity Run - Công nghệ React êm ái cho chạy bộ', N'React foam siêu êm', 1, 1, 1, N'Nike React Infinity Run', N'Giày chạy Nike React'),
(1, N'Nike Zoom Pegasus 39', 'nike-zoom-pegasus-39', N'Nike Zoom Pegasus 39 - Giày chạy phổ biến nhất', N'Best seller cho runner', 1, 1, 0, N'Nike Zoom Pegasus 39', N'Giày chạy Nike Pegasus'),

-- Adidas Products
(2, N'Adidas Superstar', 'adidas-superstar', N'Adidas Superstar - Shell toe icon từ những năm 70', N'Shell toe icon', 1, 1, 0, N'Adidas Superstar', N'Giày Adidas Superstar classic'),
(2, N'Adidas NMD R1', 'adidas-nmd-r1', N'Adidas NMD R1 - Street style với công nghệ Boost', N'Street style + Boost', 1, 1, 1, N'Adidas NMD R1', N'Giày Adidas NMD street style'),
(2, N'Adidas Gazelle', 'adidas-gazelle', N'Adidas Gazelle - Vintage suede classic', N'Vintage suede style', 1, 0, 0, N'Adidas Gazelle', N'Giày Adidas Gazelle vintage'),
(2, N'Adidas Forum Low', 'adidas-forum-low', N'Adidas Forum Low - Basketball heritage style', N'Basketball heritage', 1, 1, 1, N'Adidas Forum Low', N'Giày Adidas Forum basketball'),
(2, N'Adidas Samba', 'adidas-samba', N'Adidas Samba - Indoor football classic', N'Indoor football icon', 1, 1, 0, N'Adidas Samba', N'Giày Adidas Samba football'),

-- Puma Products
(3, N'Puma Suede Classic', 'puma-suede-classic', N'Puma Suede Classic - Iconic suede sneaker', N'Iconic suede sneaker', 1, 1, 0, N'Puma Suede Classic', N'Giày Puma Suede classic'),
(3, N'Puma Clyde Court', 'puma-clyde-court', N'Puma Clyde Court - Basketball performance', N'Basketball performance', 1, 0, 1, N'Puma Clyde Court', N'Giày Puma basketball'),
(3, N'Puma Future Rider', 'puma-future-rider', N'Puma Future Rider - Running heritage meets street', N'Running meets street', 1, 1, 1, N'Puma Future Rider', N'Giày Puma Future Rider'),

-- New Balance Products
(4, N'New Balance 574', 'new-balance-574', N'New Balance 574 - Comfort và phong cách', N'Comfort meets style', 1, 1, 0, N'New Balance 574', N'Giày New Balance 574'),
(4, N'New Balance 327', 'new-balance-327', N'New Balance 327 - Retro running style', N'Retro running style', 1, 1, 1, N'New Balance 327', N'Giày New Balance 327 retro'),
(4, N'New Balance 2002R', 'new-balance-2002r', N'New Balance 2002R - Premium comfort', N'Premium comfort style', 1, 1, 1, N'New Balance 2002R', N'Giày New Balance 2002R'),
(4, N'New Balance 990v5', 'new-balance-990v5', N'New Balance 990v5 - Made in USA premium', N'Made in USA premium', 1, 0, 0, N'New Balance 990v5', N'Giày New Balance 990'),

-- Vans Products
(6, N'Vans Old Skool', 'vans-old-skool', N'Vans Old Skool - Iconic side stripe', N'Iconic side stripe', 1, 1, 0, N'Vans Old Skool', N'Giày Vans Old Skool'),
(6, N'Vans Authentic', 'vans-authentic', N'Vans Authentic - Simple và classic', N'Simple canvas classic', 1, 1, 0, N'Vans Authentic', N'Giày Vans Authentic'),
(6, N'Vans Sk8-Hi', 'vans-sk8-hi', N'Vans Sk8-Hi - High top classic', N'High top skate shoe', 1, 0, 0, N'Vans Sk8-Hi', N'Giày Vans Sk8-Hi'),
(6, N'Vans Era', 'vans-era', N'Vans Era - Padded skate shoe', N'Padded comfort skate', 1, 0, 1, N'Vans Era', N'Giày Vans Era');

-- Lấy IDs của products vừa insert để dùng cho bước tiếp theo
DECLARE @AF1_ID BIGINT = (SELECT id FROM Products WHERE slug = 'nike-air-force-1-07');
DECLARE @DunkLow_ID BIGINT = (SELECT id FROM Products WHERE slug = 'nike-dunk-low-retro');
DECLARE @Blazer_ID BIGINT = (SELECT id FROM Products WHERE slug = 'nike-blazer-mid-77');
DECLARE @ReactRun_ID BIGINT = (SELECT id FROM Products WHERE slug = 'nike-react-infinity-run');
DECLARE @Pegasus_ID BIGINT = (SELECT id FROM Products WHERE slug = 'nike-zoom-pegasus-39');

DECLARE @Superstar_ID BIGINT = (SELECT id FROM Products WHERE slug = 'adidas-superstar');
DECLARE @NMD_ID BIGINT = (SELECT id FROM Products WHERE slug = 'adidas-nmd-r1');
DECLARE @Gazelle_ID BIGINT = (SELECT id FROM Products WHERE slug = 'adidas-gazelle');
DECLARE @Forum_ID BIGINT = (SELECT id FROM Products WHERE slug = 'adidas-forum-low');
DECLARE @Samba_ID BIGINT = (SELECT id FROM Products WHERE slug = 'adidas-samba');

DECLARE @Suede_ID BIGINT = (SELECT id FROM Products WHERE slug = 'puma-suede-classic');
DECLARE @Clyde_ID BIGINT = (SELECT id FROM Products WHERE slug = 'puma-clyde-court');
DECLARE @Future_ID BIGINT = (SELECT id FROM Products WHERE slug = 'puma-future-rider');

DECLARE @NB574_ID BIGINT = (SELECT id FROM Products WHERE slug = 'new-balance-574');
DECLARE @NB327_ID BIGINT = (SELECT id FROM Products WHERE slug = 'new-balance-327');
DECLARE @NB2002_ID BIGINT = (SELECT id FROM Products WHERE slug = 'new-balance-2002r');
DECLARE @NB990_ID BIGINT = (SELECT id FROM Products WHERE slug = 'new-balance-990v5');

DECLARE @OldSkool_ID BIGINT = (SELECT id FROM Products WHERE slug = 'vans-old-skool');
DECLARE @Authentic_ID BIGINT = (SELECT id FROM Products WHERE slug = 'vans-authentic');
DECLARE @Sk8Hi_ID BIGINT = (SELECT id FROM Products WHERE slug = 'vans-sk8-hi');
DECLARE @Era_ID BIGINT = (SELECT id FROM Products WHERE slug = 'vans-era');

PRINT '   Da them 21 products';
PRINT '';

-- =====================================================
-- 5. INSERT PRODUCT CATEGORIES
-- =====================================================
PRINT '[5/12] Dang insert product categories...';

INSERT INTO Product_Categories (product_id, category_id) VALUES
-- Nike
(@AF1_ID, 8), (@DunkLow_ID, 8), (@DunkLow_ID, 10), (@Blazer_ID, 8), (@Blazer_ID, 10),
(@ReactRun_ID, 2), (@ReactRun_ID, 8), (@Pegasus_ID, 2),
-- Adidas
(@Superstar_ID, 8), (@NMD_ID, 8), (@Gazelle_ID, 8), (@Forum_ID, 8), (@Forum_ID, 10), (@Samba_ID, 8),
-- Puma
(@Suede_ID, 8), (@Clyde_ID, 4), (@Future_ID, 8),
-- New Balance
(@NB574_ID, 8), (@NB327_ID, 8), (@NB2002_ID, 8), (@NB990_ID, 2), (@NB990_ID, 8),
-- Vans
(@OldSkool_ID, 8), (@Authentic_ID, 8), (@Sk8Hi_ID, 8), (@Sk8Hi_ID, 10), (@Era_ID, 8);

PRINT '   Da them product categories';
PRINT '';

-- =====================================================
-- 6. INSERT PRODUCT VARIANTS (60+ variants)
-- =====================================================
PRINT '[6/12] Dang insert product variants...';

-- Nike Air Force 1
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, low_stock_threshold, is_active) VALUES
(@AF1_ID, 'NIKE-AF1-WHT-38', '38', N'Trắng', 2800000, NULL, 1800000, 25, 10, 1),
(@AF1_ID, 'NIKE-AF1-WHT-39', '39', N'Trắng', 2800000, NULL, 1800000, 30, 10, 1),
(@AF1_ID, 'NIKE-AF1-WHT-40', '40', N'Trắng', 2800000, 2490000, 1800000, 35, 10, 1),
(@AF1_ID, 'NIKE-AF1-BLK-39', '39', N'Đen', 2800000, NULL, 1800000, 20, 10, 1),
(@AF1_ID, 'NIKE-AF1-BLK-40', '40', N'Đen', 2800000, NULL, 1800000, 22, 10, 1);

-- Nike Dunk Low
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@DunkLow_ID, 'NIKE-DUNK-PND-38', '38', N'Panda', 3200000, 2890000, 2000000, 15, 1),
(@DunkLow_ID, 'NIKE-DUNK-PND-39', '39', N'Panda', 3200000, 2890000, 2000000, 18, 1),
(@DunkLow_ID, 'NIKE-DUNK-PND-40', '40', N'Panda', 3200000, 2890000, 2000000, 20, 1),
(@DunkLow_ID, 'NIKE-DUNK-BLU-39', '39', N'Blue', 3200000, NULL, 2000000, 12, 1);

-- Nike Blazer Mid
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@Blazer_ID, 'NIKE-BLZ-WHT-39', '39', N'Trắng/Đen', 2900000, 1900000, 18, 1),
(@Blazer_ID, 'NIKE-BLZ-WHT-40', '40', N'Trắng/Đen', 2900000, 1900000, 20, 1),
(@Blazer_ID, 'NIKE-BLZ-WHT-41', '41', N'Trắng/Đen', 2900000, 1900000, 15, 1);

-- Nike React Infinity Run
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@ReactRun_ID, 'NIKE-RCT-BLK-39', '39', N'Đen', 3800000, 3290000, 2400000, 12, 1),
(@ReactRun_ID, 'NIKE-RCT-BLK-40', '40', N'Đen', 3800000, 3290000, 2400000, 14, 1),
(@ReactRun_ID, 'NIKE-RCT-WHT-40', '40', N'Trắng', 3800000, NULL, 2400000, 10, 1);

-- Nike Pegasus 39
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@Pegasus_ID, 'NIKE-PEG-GRY-39', '39', N'Xám', 3600000, 2300000, 16, 1),
(@Pegasus_ID, 'NIKE-PEG-GRY-40', '40', N'Xám', 3600000, 2300000, 18, 1),
(@Pegasus_ID, 'NIKE-PEG-BLU-40', '40', N'Xanh', 3600000, 2300000, 14, 1);

-- Adidas Superstar
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@Superstar_ID, 'ADIDAS-SS-WHT-38', '38', N'Trắng', 2400000, 1600000, 28, 1),
(@Superstar_ID, 'ADIDAS-SS-WHT-39', '39', N'Trắng', 2400000, 1600000, 32, 1),
(@Superstar_ID, 'ADIDAS-SS-WHT-40', '40', N'Trắng', 2400000, 1600000, 30, 1),
(@Superstar_ID, 'ADIDAS-SS-BLK-39', '39', N'Đen', 2400000, 1600000, 25, 1);

-- Adidas NMD R1
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@NMD_ID, 'ADIDAS-NMD-BLK-39', '39', N'Đen', 3500000, 2990000, 2200000, 15, 1),
(@NMD_ID, 'ADIDAS-NMD-BLK-40', '40', N'Đen', 3500000, 2990000, 2200000, 18, 1),
(@NMD_ID, 'ADIDAS-NMD-WHT-40', '40', N'Trắng', 3500000, NULL, 2200000, 12, 1);

-- Adidas Gazelle
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@Gazelle_ID, 'ADIDAS-GAZ-BLU-39', '39', N'Xanh', 2600000, 1700000, 20, 1),
(@Gazelle_ID, 'ADIDAS-GAZ-BLU-40', '40', N'Xanh', 2600000, 1700000, 22, 1),
(@Gazelle_ID, 'ADIDAS-GAZ-GRY-40', '40', N'Xám', 2600000, 1700000, 18, 1);

-- Adidas Forum Low
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@Forum_ID, 'ADIDAS-FRM-WHT-39', '39', N'Trắng', 2900000, 2490000, 1900000, 16, 1),
(@Forum_ID, 'ADIDAS-FRM-WHT-40', '40', N'Trắng', 2900000, 2490000, 1900000, 18, 1);

-- Adidas Samba
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@Samba_ID, 'ADIDAS-SMB-BLK-39', '39', N'Đen/Trắng', 2500000, 1650000, 24, 1),
(@Samba_ID, 'ADIDAS-SMB-BLK-40', '40', N'Đen/Trắng', 2500000, 1650000, 26, 1),
(@Samba_ID, 'ADIDAS-SMB-BLK-41', '41', N'Đen/Trắng', 2500000, 1650000, 20, 1);

-- Puma Suede Classic
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@Suede_ID, 'PUMA-SD-BLK-39', '39', N'Đen', 2200000, 1500000, 22, 1),
(@Suede_ID, 'PUMA-SD-BLK-40', '40', N'Đen', 2200000, 1500000, 24, 1),
(@Suede_ID, 'PUMA-SD-BLU-40', '40', N'Xanh', 2200000, 1500000, 18, 1);

-- Puma Clyde Court
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@Clyde_ID, 'PUMA-CLY-WHT-40', '40', N'Trắng', 3200000, 2790000, 2100000, 12, 1),
(@Clyde_ID, 'PUMA-CLY-RED-40', '40', N'Đỏ', 3200000, NULL, 2100000, 10, 1);

-- Puma Future Rider
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@Future_ID, 'PUMA-FTR-MLT-39', '39', N'Đa màu', 2700000, 2290000, 1800000, 16, 1),
(@Future_ID, 'PUMA-FTR-MLT-40', '40', N'Đa màu', 2700000, 2290000, 1800000, 18, 1);

-- New Balance 574
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@NB574_ID, 'NB-574-GRY-39', '39', N'Xám', 2400000, 1600000, 20, 1),
(@NB574_ID, 'NB-574-GRY-40', '40', N'Xám', 2400000, 1600000, 24, 1),
(@NB574_ID, 'NB-574-NVY-40', '40', N'Navy', 2400000, 1600000, 18, 1);

-- New Balance 327
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@NB327_ID, 'NB-327-BLK-39', '39', N'Đen', 2800000, 2390000, 1850000, 14, 1),
(@NB327_ID, 'NB-327-BLK-40', '40', N'Đen', 2800000, 2390000, 1850000, 16, 1);

-- New Balance 2002R
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@NB2002_ID, 'NB-2002-GRY-40', '40', N'Xám', 3800000, 3290000, 2500000, 10, 1),
(@NB2002_ID, 'NB-2002-BLU-40', '40', N'Xanh', 3800000, NULL, 2500000, 8, 1);

-- New Balance 990v5
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@NB990_ID, 'NB-990-GRY-40', '40', N'Xám', 5200000, 3800000, 12, 1),
(@NB990_ID, 'NB-990-GRY-41', '41', N'Xám', 5200000, 3800000, 10, 1);

-- Vans Old Skool
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@OldSkool_ID, 'VANS-OS-BLK-38', '38', N'Đen/Trắng', 1800000, 1200000, 30, 1),
(@OldSkool_ID, 'VANS-OS-BLK-39', '39', N'Đen/Trắng', 1800000, 1200000, 35, 1),
(@OldSkool_ID, 'VANS-OS-BLK-40', '40', N'Đen/Trắng', 1800000, 1200000, 32, 1);

-- Vans Authentic
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@Authentic_ID, 'VANS-AUTH-WHT-39', '39', N'Trắng', 1600000, 1100000, 28, 1),
(@Authentic_ID, 'VANS-AUTH-WHT-40', '40', N'Trắng', 1600000, 1100000, 30, 1),
(@Authentic_ID, 'VANS-AUTH-BLK-40', '40', N'Đen', 1600000, 1100000, 25, 1);

-- Vans Sk8-Hi
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, cost_price, stock_quantity, is_active) VALUES
(@Sk8Hi_ID, 'VANS-SK8-BLK-39', '39', N'Đen', 1900000, 1300000, 22, 1),
(@Sk8Hi_ID, 'VANS-SK8-BLK-40', '40', N'Đen', 1900000, 1300000, 24, 1);

-- Vans Era
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, is_active) VALUES
(@Era_ID, 'VANS-ERA-BLU-39', '39', N'Xanh', 1700000, 1490000, 1150000, 20, 1),
(@Era_ID, 'VANS-ERA-BLU-40', '40', N'Xanh', 1700000, 1490000, 1150000, 22, 1);

PRINT '   Da them 60+ product variants';
PRINT '';

-- =====================================================
-- 7. INSERT PRODUCT IMAGES
-- =====================================================
PRINT '[7/12] Dang insert product images...';

INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order) VALUES
-- Nike Air Force 1
(@AF1_ID, N'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto/315122-111.png', N'Nike Air Force 1 - Trắng', 1, 0),
(@AF1_ID, N'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto/315122-111-side.png', N'Nike Air Force 1 - Góc bên', 0, 1),

-- Nike Dunk Low
(@DunkLow_ID, N'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto/dunk-low-panda.png', N'Nike Dunk Low Panda', 1, 0),
(@DunkLow_ID, N'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto/dunk-low-panda-side.png', N'Nike Dunk Low - Góc bên', 0, 1),

-- Adidas Superstar
(@Superstar_ID, N'https://assets.adidas.com/images/w_600/superstar.jpg', N'Adidas Superstar', 1, 0),

-- Vans Old Skool
(@OldSkool_ID, N'https://images.vans.com/is/image/Vans/old-skool-black-white', N'Vans Old Skool', 1, 0);

PRINT '   Da them product images';
PRINT '';

-- =====================================================
-- 8. INSERT FLASH SALES
-- =====================================================
PRINT '[8/12] Dang insert flash sales...';

INSERT INTO Flash_Sales (product_id, discount_percent, start_time, end_time, quantity_limit, sold_count, is_active) VALUES
-- Active Flash Sales
(@DunkLow_ID, 15.00, DATEADD(DAY, -2, GETDATE()), DATEADD(DAY, 2, GETDATE()), 50, 12, 1),
(@ReactRun_ID, 20.00, DATEADD(DAY, -1, GETDATE()), DATEADD(DAY, 3, GETDATE()), 30, 8, 1),
(@NMD_ID, 18.00, DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, 1, GETDATE()), 40, 15, 1),
(@AF1_ID, 12.00, GETDATE(), DATEADD(DAY, 4, GETDATE()), 60, 5, 1),
(@Forum_ID, 15.00, GETDATE(), DATEADD(DAY, 2, GETDATE()), 35, 3, 1),

-- Upcoming Flash Sales
(@NB2002_ID, 15.00, DATEADD(DAY, 3, GETDATE()), DATEADD(DAY, 7, GETDATE()), 25, 0, 1),
(@Pegasus_ID, 10.00, DATEADD(DAY, 5, GETDATE()), DATEADD(DAY, 8, GETDATE()), 45, 0, 1),
(@Future_ID, 20.00, DATEADD(DAY, 4, GETDATE()), DATEADD(DAY, 6, GETDATE()), 30, 0, 1);

PRINT '   Da them 8 flash sales';
PRINT '';

-- =====================================================
-- 9. INSERT COUPONS
-- =====================================================
PRINT '[9/12] Dang insert coupons...';

INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, applicable_to, applicable_id, is_active) VALUES
-- General Coupons
('SUMMER2025', N'Giảm 15% cho tất cả đơn hàng mùa hè', 'percentage', 15, 1000000, 300000, '2025-01-01', '2025-12-31', NULL, 45, 3, 'all', NULL, 1),
('FIRST100K', N'Giảm 100K cho đơn hàng đầu tiên', 'fixed', 100000, 500000, 100000, '2025-01-01', '2025-12-31', 1000, 120, 1, 'all', NULL, 1),
('MEGA50', N'Giảm 50K cho đơn hàng từ 800K', 'fixed', 50000, 800000, 50000, '2025-01-01', '2025-12-31', NULL, 230, 5, 'all', NULL, 1),
('VIP200K', N'Giảm 200K cho đơn hàng từ 2 triệu', 'fixed', 200000, 2000000, 200000, '2025-01-01', '2025-06-30', 500, 78, 2, 'all', NULL, 1),
('SAVE20', N'Giảm 20% tối đa 500K', 'percentage', 20, 1500000, 500000, '2025-01-01', '2025-12-31', 300, 95, 2, 'all', NULL, 1),

-- Brand Specific Coupons
('NIKE100K', N'Giảm 100K cho sản phẩm Nike', 'fixed', 100000, 1200000, 100000, '2025-01-01', '2025-12-31', 400, 56, 2, 'brand', 1, 1),
('ADIDAS10', N'Giảm 10% cho sản phẩm Adidas', 'percentage', 10, 800000, 200000, '2025-01-01', '2025-12-31', NULL, 89, 3, 'brand', 2, 1),
('PUMA15', N'Giảm 15% cho sản phẩm Puma', 'percentage', 15, 600000, 150000, '2025-01-01', '2025-06-30', 200, 34, 2, 'brand', 3, 1),
('NB80K', N'Giảm 80K cho New Balance', 'fixed', 80000, 1000000, 80000, '2025-01-01', '2025-12-31', 250, 67, 2, 'brand', 4, 1),
('VANS50K', N'Giảm 50K cho Vans', 'fixed', 50000, 500000, 50000, '2025-01-01', '2025-12-31', NULL, 142, 4, 'brand', 6, 1),

-- Category Specific
('RUNNING20', N'Giảm 20% cho giày chạy bộ', 'percentage', 20, 1000000, 400000, '2025-01-01', '2025-12-31', 300, 45, 2, 'category', 2, 1),
('LIFESTYLE15', N'Giảm 15% cho giày lifestyle', 'percentage', 15, 800000, 250000, '2025-01-01', '2025-12-31', NULL, 123, 3, 'category', 8, 1);

PRINT '   Da them 12 coupons';
PRINT '';

-- =====================================================
-- 10. INSERT ORDERS MẪU
-- =====================================================
PRINT '[10/12] Dang insert orders...';

-- Lấy User IDs từ database
DECLARE @User3_ID BIGINT = (SELECT id FROM Users WHERE email = 'testadmin@sneakery.com');
DECLARE @User4_ID BIGINT = (SELECT id FROM Users WHERE email = 'testuser@sneakery.com');

-- Get some variant IDs
DECLARE @Var_AF1_39 BIGINT = (SELECT id FROM Product_Variants WHERE sku = 'NIKE-AF1-WHT-39');
DECLARE @Var_Dunk_39 BIGINT = (SELECT id FROM Product_Variants WHERE sku = 'NIKE-DUNK-PND-39');
DECLARE @Var_SS_40 BIGINT = (SELECT id FROM Product_Variants WHERE sku = 'ADIDAS-SS-WHT-40');
DECLARE @Var_NMD_40 BIGINT = (SELECT id FROM Product_Variants WHERE sku = 'ADIDAS-NMD-BLK-40');
DECLARE @Var_OS_39 BIGINT = (SELECT id FROM Product_Variants WHERE sku = 'VANS-OS-BLK-39');

-- Get address IDs
DECLARE @Addr1_ID BIGINT = (SELECT TOP 1 id FROM Addresses WHERE user_id = @User3_ID ORDER BY is_default DESC);

-- Order 1: Đã giao
DECLARE @Order1_Number VARCHAR(50);
EXEC sp_GenerateOrderNumber @Order1_Number OUTPUT;

INSERT INTO Orders (user_id, order_number, address_shipping_id, address_billing_id, subtotal, shipping_fee, discount_amount, tax_amount, total_amount, status, shipping_method, created_at, delivered_at) VALUES
(@User3_ID, @Order1_Number, @Addr1_ID, @Addr1_ID, 2800000, 30000, 0, 0, 2830000, 'delivered', N'Giao hàng nhanh', DATEADD(DAY, -15, GETDATE()), DATEADD(DAY, -12, GETDATE()));

DECLARE @Order1_ID BIGINT = SCOPE_IDENTITY();

INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(@Order1_ID, @Var_AF1_39, N'Nike Air Force 1 ''07', 'NIKE-AF1-WHT-39', '39', N'Trắng', 1, 2800000, 2800000);

INSERT INTO Payments (order_id, payment_method, amount, status, paid_at) VALUES
(@Order1_ID, 'vnpay', 2830000, 'completed', DATEADD(DAY, -15, GETDATE()));

-- Order 2: Đang xử lý
DECLARE @Order2_Number VARCHAR(50);
EXEC sp_GenerateOrderNumber @Order2_Number OUTPUT;

INSERT INTO Orders (user_id, order_number, address_shipping_id, subtotal, shipping_fee, total_amount, status, created_at) VALUES
(@User3_ID, @Order2_Number, @Addr1_ID, 5090000, 0, 5090000, 'processing', DATEADD(DAY, -3, GETDATE()));

DECLARE @Order2_ID BIGINT = SCOPE_IDENTITY();

INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(@Order2_ID, @Var_Dunk_39, N'Nike Dunk Low Retro', 'NIKE-DUNK-PND-39', '39', N'Panda', 1, 2890000, 2890000),
(@Order2_ID, @Var_SS_40, N'Adidas Superstar', 'ADIDAS-SS-WHT-40', '40', N'Trắng', 1, 2200000, 2200000);

INSERT INTO Payments (order_id, payment_method, amount, status, paid_at) VALUES
(@Order2_ID, 'momo', 5090000, 'completed', DATEADD(DAY, -3, GETDATE()));

-- Order 3: Pending
DECLARE @Order3_Number VARCHAR(50);
EXEC sp_GenerateOrderNumber @Order3_Number OUTPUT;

INSERT INTO Orders (user_id, order_number, address_shipping_id, subtotal, shipping_fee, total_amount, status, created_at) VALUES
(@User4_ID, @Order3_Number, @Addr1_ID, 1800000, 30000, 1830000, 'pending', DATEADD(DAY, -1, GETDATE()));

DECLARE @Order3_ID BIGINT = SCOPE_IDENTITY();

INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(@Order3_ID, @Var_OS_39, N'Vans Old Skool', 'VANS-OS-BLK-39', '39', N'Đen/Trắng', 1, 1800000, 1800000);

INSERT INTO Payments (order_id, payment_method, amount, status) VALUES
(@Order3_ID, 'cod', 1830000, 'pending');

PRINT '   Da them 3 orders';
PRINT '';

-- =====================================================
-- 11. INSERT REVIEWS, LOYALTY POINTS, NOTIFICATIONS, WISHLISTS
-- =====================================================
PRINT '[11/12] Dang insert reviews, loyalty, notifications, wishlists...';

INSERT INTO Reviews (product_id, user_id, order_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, created_at) VALUES
-- Nike Air Force 1 Reviews
(@AF1_ID, @User3_ID, @Order1_ID, 5, N'Tuyệt vời!', N'Giày rất đẹp, chất lượng tốt, đi rất êm. Mình rất hài lòng với sản phẩm này.', 1, 1, 15, DATEADD(DAY, -10, GETDATE())),
(@AF1_ID, @User4_ID, NULL, 4, N'Đẹp nhưng hơi nặng', N'Giày đẹp, form chuẩn nhưng hơi nặng so với các loại giày khác.', 1, 0, 8, DATEADD(DAY, -5, GETDATE())),

-- Nike Dunk Low Reviews
(@DunkLow_ID, @User3_ID, @Order2_ID, 5, N'Quá đẹp!', N'Màu Panda quá xịn, phối đồ gì cũng hợp. Giá hơi cao nhưng xứng đáng.', 1, 1, 25, DATEADD(DAY, -2, GETDATE())),
(@DunkLow_ID, @User4_ID, NULL, 5, N'Yêu luôn!', N'Mẫu giày hot nhất hiện nay. Form chuẩn, chất lượng tốt.', 1, 0, 12, DATEADD(DAY, -1, GETDATE())),

-- Adidas Superstar Reviews  
(@Superstar_ID, @User3_ID, NULL, 5, N'Classic không bao giờ lỗi mốt', N'Shell toe classic, đi rất thoải mái. Phối đồ dễ dàng.', 1, 0, 18, DATEADD(DAY, -7, GETDATE())),
(@Superstar_ID, @User4_ID, NULL, 4, N'Tốt', N'Giày đẹp, nhưng cần mang quen một chút. Nhìn chung rất hài lòng.', 1, 0, 6, DATEADD(DAY, -4, GETDATE())),

-- Adidas NMD Reviews
(@NMD_ID, @User3_ID, NULL, 5, N'Boost êm ái', N'Công nghệ Boost rất êm, đi cả ngày không mỏi chân. Street style đẹp.', 1, 0, 20, DATEADD(DAY, -6, GETDATE())),

-- New Balance 574 Reviews
(@NB574_ID, @User3_ID, NULL, 5, N'Thoải mái nhất từng đi', N'New Balance luôn là top về comfort. 574 này không thất vọng.', 1, 0, 14, DATEADD(DAY, -8, GETDATE())),
(@NB574_ID, @User4_ID, NULL, 4, N'Đáng mua', N'Giá tốt, chất lượng ổn. Đi êm và bền.', 1, 0, 9, DATEADD(DAY, -3, GETDATE())),

-- Vans Old Skool Reviews
(@OldSkool_ID, @User3_ID, @Order3_ID, 5, N'Must have item!', N'Ai cũng nên có một đôi Old Skool. Dễ phối đồ, giá rẻ, bền.', 1, 1, 22, DATEADD(HOUR, -12, GETDATE())),
(@OldSkool_ID, @User4_ID, NULL, 5, N'Perfect!', N'Classic vượt thời gian. Mình đã có 3 đôi rồi.', 1, 0, 16, DATEADD(DAY, -2, GETDATE())),

-- Puma Suede Reviews
(@Suede_ID, @User3_ID, NULL, 4, N'Đẹp và chất', N'Suede material rất đẹp, nhưng hơi khó vệ sinh. Cần cẩn thận khi đi.', 1, 0, 10, DATEADD(DAY, -5, GETDATE())),

-- Nike React Reviews
(@ReactRun_ID, @User4_ID, NULL, 5, N'Chạy bộ tuyệt vời', N'React foam êm ái, support tốt. Đã chạy được 100km rồi vẫn OK.', 1, 0, 13, DATEADD(DAY, -4, GETDATE())),

-- Nike Pegasus Reviews
(@Pegasus_ID, @User3_ID, NULL, 5, N'Best seller có lý do', N'Pegasus xứng đáng là best seller. Vừa êm vừa responsive.', 1, 0, 17, DATEADD(DAY, -6, GETDATE())),

-- Adidas Gazelle Reviews
(@Gazelle_ID, @User4_ID, NULL, 4, N'Vintage đẹp', N'Suede vintage rất đẹp. Fit hơi nhỏ nên nên lên 0.5 size.', 1, 0, 11, DATEADD(DAY, -3, GETDATE())),

-- New Balance 327 Reviews
(@NB327_ID, @User3_ID, NULL, 5, N'Retro runner đỉnh', N'327 là mẫu retro runner đẹp nhất của NB. Form chuẩn, đi êm.', 1, 0, 15, DATEADD(DAY, -2, GETDATE()));

-- Loyalty Points
INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, expires_at, created_at) VALUES
-- User 3 earned points
(@User3_ID, 283, 'earn', @Order1_ID, N'Tích điểm từ đơn hàng ' + @Order1_Number, DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -12, GETDATE())),
(@User3_ID, 509, 'earn', @Order2_ID, N'Tích điểm từ đơn hàng ' + @Order2_Number, DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -3, GETDATE())),
(@User3_ID, 50, 'earn', NULL, N'Bonus điểm sinh nhật', DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -30, GETDATE())),

-- User 4 earned points
(@User4_ID, 183, 'earn', @Order3_ID, N'Tích điểm từ đơn hàng ' + @Order3_Number, DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -1, GETDATE())),
(@User4_ID, 100, 'earn', NULL, N'Bonus điểm đăng ký', DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -60, GETDATE()));

-- Notifications
INSERT INTO Notifications (user_id, type, title, message, link, is_read, created_at) VALUES
-- User 3 notifications
(@User3_ID, 'order_status', N'Đơn hàng đã giao thành công', N'Đơn hàng ' + @Order1_Number + ' đã được giao thành công. Cảm ơn bạn đã mua hàng!', '/user/orders/' + CAST(@Order1_ID AS VARCHAR), 1, DATEADD(DAY, -12, GETDATE())),
(@User3_ID, 'order_status', N'Đơn hàng đang xử lý', N'Đơn hàng ' + @Order2_Number + ' đang được xử lý. Chúng tôi sẽ giao hàng sớm nhất có thể.', '/user/orders/' + CAST(@Order2_ID AS VARCHAR), 0, DATEADD(DAY, -3, GETDATE())),
(@User3_ID, 'promotion', N'Flash Sale hấp dẫn!', N'Giảm giá đến 20% cho Nike React Infinity Run. Nhanh tay kẻo hết!', '/products/nike-react-infinity-run', 0, DATEADD(DAY, -1, GETDATE())),
(@User3_ID, 'promotion', N'Mã giảm giá mới', N'Bạn có mã giảm giá SUMMER2025 giảm 15%. Áp dụng ngay!', '/products', 0, DATEADD(HOUR, -6, GETDATE())),

-- User 4 notifications
(@User4_ID, 'order_status', N'Đơn hàng đã được tạo', N'Đơn hàng ' + @Order3_Number + ' đã được tạo thành công. Vui lòng thanh toán để xử lý đơn hàng.', '/user/orders/' + CAST(@Order3_ID AS VARCHAR), 0, DATEADD(DAY, -1, GETDATE())),
(@User4_ID, 'promotion', N'Nike Dunk Low giảm giá!', N'Nike Dunk Low Panda giảm 15%. Cơ hội sở hữu giày hot nhất!', '/products/nike-dunk-low-retro', 0, DATEADD(HOUR, -3, GETDATE())),
(@User4_ID, 'product_restock', N'Sản phẩm đã có hàng', N'Adidas Superstar size 39 đã có hàng trở lại. Đặt hàng ngay!', '/products/adidas-superstar', 1, DATEADD(DAY, -5, GETDATE()));

-- Wishlists
INSERT INTO Wishlists (user_id, product_id, created_at) VALUES
-- User 3 wishlist
(@User3_ID, @NB2002_ID, DATEADD(DAY, -10, GETDATE())),
(@User3_ID, @Blazer_ID, DATEADD(DAY, -8, GETDATE())),
(@User3_ID, @Forum_ID, DATEADD(DAY, -5, GETDATE())),
(@User3_ID, @Clyde_ID, DATEADD(DAY, -2, GETDATE())),

-- User 4 wishlist
(@User4_ID, @ReactRun_ID, DATEADD(DAY, -7, GETDATE())),
(@User4_ID, @Pegasus_ID, DATEADD(DAY, -4, GETDATE())),
(@User4_ID, @NB990_ID, DATEADD(DAY, -1, GETDATE()));

PRINT '   Da them reviews, loyalty points, notifications, wishlists';
PRINT '';

-- =====================================================
-- 12. CẬP NHẬT PRODUCT STATS
-- =====================================================
PRINT '[12/12] Dang cap nhat product statistics...';

-- Update view counts (random)
UPDATE Products SET view_count = ABS(CHECKSUM(NEWID())) % 500 + 100 WHERE id IN (
    @AF1_ID, @DunkLow_ID, @Blazer_ID, @ReactRun_ID, @Pegasus_ID,
    @Superstar_ID, @NMD_ID, @Gazelle_ID, @Forum_ID, @Samba_ID
);

-- Update order counts
UPDATE Products SET order_count = 5 WHERE id = @AF1_ID;
UPDATE Products SET order_count = 3 WHERE id = @DunkLow_ID;
UPDATE Products SET order_count = 2 WHERE id = @Superstar_ID;
UPDATE Products SET order_count = 1 WHERE id = @OldSkool_ID;

-- Update ratings and review counts
UPDATE Products SET avg_rating = 4.50, review_count = 2 WHERE id = @AF1_ID;
UPDATE Products SET avg_rating = 5.00, review_count = 2 WHERE id = @DunkLow_ID;
UPDATE Products SET avg_rating = 4.50, review_count = 2 WHERE id = @Superstar_ID;
UPDATE Products SET avg_rating = 5.00, review_count = 1 WHERE id = @NMD_ID;
UPDATE Products SET avg_rating = 4.50, review_count = 2 WHERE id = @NB574_ID;
UPDATE Products SET avg_rating = 5.00, review_count = 2 WHERE id = @OldSkool_ID;
UPDATE Products SET avg_rating = 4.00, review_count = 1 WHERE id = @Suede_ID;
UPDATE Products SET avg_rating = 5.00, review_count = 1 WHERE id = @ReactRun_ID;
UPDATE Products SET avg_rating = 5.00, review_count = 1 WHERE id = @Pegasus_ID;
UPDATE Products SET avg_rating = 4.00, review_count = 1 WHERE id = @Gazelle_ID;
UPDATE Products SET avg_rating = 5.00, review_count = 1 WHERE id = @NB327_ID;

PRINT '   Da cap nhat product statistics';
PRINT '';

-- =====================================================
-- 13. THÊM DỮ LIỆU MẪU BỔ SUNG
-- =====================================================
PRINT '[13/13] Dang them du lieu mau bo sung...';

-- Thêm 3 users nữa
INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified) VALUES
('customer1@example.com', '$2a$10$SOVwX.mgssz5J2b2ZZa3Y.0z1VTgMTi9Rr5lYcB5VSJrPfgawEkQ2', N'Nguyễn Văn A', '0923456789', 'USER', 1, 1),
('customer2@example.com', '$2a$10$SOVwX.mgssz5J2b2ZZa3Y.0z1VTgMTi9Rr5lYcB5VSJrPfgawEkQ2', N'Trần Thị B', '0934567890', 'USER', 1, 1),
('customer3@example.com', '$2a$10$SOVwX.mgssz5J2b2ZZa3Y.0z1VTgMTi9Rr5lYcB5VSJrPfgawEkQ2', N'Lê Văn C', '0945678901', 'USER', 1, 1);

DECLARE @Customer1_ID BIGINT = (SELECT id FROM Users WHERE email = 'customer1@example.com');
DECLARE @Customer2_ID BIGINT = (SELECT id FROM Users WHERE email = 'customer2@example.com');
DECLARE @Customer3_ID BIGINT = (SELECT id FROM Users WHERE email = 'customer3@example.com');

-- Thêm addresses cho customers mới
INSERT INTO Addresses (user_id, recipient_name, phone, line1, ward, district, city, is_default, address_type) VALUES
(@Customer1_ID, N'Nguyễn Văn A', '0923456789', N'234 Trần Hưng Đạo', N'Phường Cô Giang', N'Quận 1', N'TP. Hồ Chí Minh', 1, 'home'),
(@Customer2_ID, N'Trần Thị B', '0934567890', N'567 Điện Biên Phủ', N'Phường Đa Kao', N'Quận 1', N'TP. Hồ Chí Minh', 1, 'home'),
(@Customer3_ID, N'Lê Văn C', '0945678901', N'890 Nguyễn Đình Chiểu', N'Phường Võ Thị Sáu', N'Quận 3', N'TP. Hồ Chí Minh', 1, 'office');

-- Thêm nhiều reviews hơn
INSERT INTO Reviews (product_id, user_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, created_at) VALUES
-- Nike Air Force 1
(@AF1_ID, @Customer1_ID, 5, N'Chất lượng tuyệt vời', N'Giày rất đẹp và bền. Đi được 6 tháng vẫn như mới.', 1, 1, 10, DATEADD(DAY, -20, GETDATE())),
(@AF1_ID, @Customer2_ID, 4, N'Giá hơi cao', N'Giày đẹp nhưng giá hơi cao so với mặt bằng chung.', 1, 0, 5, DATEADD(DAY, -15, GETDATE())),

-- Adidas NMD
(@NMD_ID, @Customer1_ID, 5, N'Boost công nghệ đỉnh', N'Công nghệ Boost thực sự êm ái. Đáng đồng tiền bát gạo.', 1, 1, 18, DATEADD(DAY, -10, GETDATE())),
(@NMD_ID, @Customer3_ID, 5, N'Rất hài lòng', N'Mua được giá tốt, chất lượng xuất sắc. Sẽ ủng hộ shop tiếp.', 1, 0, 12, DATEADD(DAY, -8, GETDATE())),

-- Vans Old Skool
(@OldSkool_ID, @Customer2_ID, 5, N'Classic không bao giờ lỗi thời', N'Đã mua đôi thứ 2. Giày này phối đồ gì cũng đẹp.', 1, 1, 20, DATEADD(DAY, -12, GETDATE())),

-- Puma Suede
(@Suede_ID, @Customer2_ID, 4, N'Đẹp nhưng khó giữ sạch', N'Suede đẹp nhưng hơi khó vệ sinh. Cần cẩn thận khi mang.', 1, 0, 8, DATEADD(DAY, -9, GETDATE())),

-- New Balance 574
(@NB574_ID, @Customer3_ID, 5, N'Thoải mái cho chân', N'Đi êm, thoáng. Rất thích hợp cho người đi nhiều.', 1, 0, 15, DATEADD(DAY, -7, GETDATE())),

-- Adidas Superstar
(@Superstar_ID, @Customer1_ID, 5, N'Icon không bao giờ cũ', N'Shell toe classic. Ai cũng nên có một đôi Superstar.', 1, 0, 22, DATEADD(DAY, -6, GETDATE()));

-- Thêm nhiều orders hơn
DECLARE @Addr_C1 BIGINT = (SELECT TOP 1 id FROM Addresses WHERE user_id = @Customer1_ID);
DECLARE @Addr_C2 BIGINT = (SELECT TOP 1 id FROM Addresses WHERE user_id = @Customer2_ID);
DECLARE @Addr_C3 BIGINT = (SELECT TOP 1 id FROM Addresses WHERE user_id = @Customer3_ID);

-- Order từ Customer1
DECLARE @Order4_Number VARCHAR(50);
EXEC sp_GenerateOrderNumber @Order4_Number OUTPUT;
INSERT INTO Orders (user_id, order_number, address_shipping_id, subtotal, shipping_fee, total_amount, status, created_at, delivered_at) VALUES
(@Customer1_ID, @Order4_Number, @Addr_C1, 3500000, 0, 3500000, 'delivered', DATEADD(DAY, -20, GETDATE()), DATEADD(DAY, -17, GETDATE()));
DECLARE @Order4_ID BIGINT = SCOPE_IDENTITY();

INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(@Order4_ID, @Var_NMD_40, N'Adidas NMD R1', 'ADIDAS-NMD-BLK-40', '40', N'Đen', 1, 3500000, 3500000);

INSERT INTO Payments (order_id, payment_method, amount, status, paid_at) VALUES
(@Order4_ID, 'momo', 3500000, 'completed', DATEADD(DAY, -20, GETDATE()));

-- Order từ Customer2
DECLARE @Order5_Number VARCHAR(50);
EXEC sp_GenerateOrderNumber @Order5_Number OUTPUT;
INSERT INTO Orders (user_id, order_number, address_shipping_id, subtotal, shipping_fee, total_amount, status, created_at, delivered_at) VALUES
(@Customer2_ID, @Order5_Number, @Addr_C2, 1800000, 30000, 1830000, 'delivered', DATEADD(DAY, -12, GETDATE()), DATEADD(DAY, -9, GETDATE()));
DECLARE @Order5_ID BIGINT = SCOPE_IDENTITY();

INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(@Order5_ID, @Var_OS_39, N'Vans Old Skool', 'VANS-OS-BLK-39', '39', N'Đen/Trắng', 1, 1800000, 1800000);

INSERT INTO Payments (order_id, payment_method, amount, status, paid_at) VALUES
(@Order5_ID, 'vnpay', 1830000, 'completed', DATEADD(DAY, -12, GETDATE()));

-- Order từ Customer3 (đang xử lý)
DECLARE @Order6_Number VARCHAR(50);
EXEC sp_GenerateOrderNumber @Order6_Number OUTPUT;
INSERT INTO Orders (user_id, order_number, address_shipping_id, subtotal, shipping_fee, total_amount, status, created_at) VALUES
(@Customer3_ID, @Order6_Number, @Addr_C3, 4800000, 0, 4800000, 'confirmed', DATEADD(DAY, -2, GETDATE()));
DECLARE @Order6_ID BIGINT = SCOPE_IDENTITY();

DECLARE @Var_NB574_40 BIGINT = (SELECT id FROM Product_Variants WHERE sku = 'NB-574-GRY-40');
INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(@Order6_ID, @Var_NB574_40, N'New Balance 574', 'NB-574-GRY-40', '40', N'Xám', 2, 2400000, 4800000);

INSERT INTO Payments (order_id, payment_method, amount, status, paid_at) VALUES
(@Order6_ID, 'bank_transfer', 4800000, 'completed', DATEADD(DAY, -2, GETDATE()));

-- Thêm loyalty points cho customers mới
INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, expires_at, created_at) VALUES
(@Customer1_ID, 350, 'earn', @Order4_ID, N'Tích điểm từ đơn hàng ' + @Order4_Number, DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -17, GETDATE())),
(@Customer1_ID, 100, 'earn', NULL, N'Bonus điểm đăng ký mới', DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -25, GETDATE())),
(@Customer2_ID, 180, 'earn', @Order5_ID, N'Tích điểm từ đơn hàng ' + @Order5_Number, DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -9, GETDATE())),
(@Customer2_ID, 100, 'earn', NULL, N'Bonus điểm đăng ký mới', DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -18, GETDATE())),
(@Customer3_ID, 480, 'earn', @Order6_ID, N'Tích điểm từ đơn hàng ' + @Order6_Number, DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -2, GETDATE())),
(@Customer3_ID, 100, 'earn', NULL, N'Bonus điểm đăng ký mới', DATEADD(YEAR, 1, GETDATE()), DATEADD(DAY, -10, GETDATE()));

-- Thêm notifications cho customers mới
INSERT INTO Notifications (user_id, type, title, message, link, is_read, created_at) VALUES
(@Customer1_ID, 'order_status', N'Đơn hàng đã giao', N'Đơn hàng ' + @Order4_Number + ' đã được giao thành công!', '/user/orders/' + CAST(@Order4_ID AS VARCHAR), 1, DATEADD(DAY, -17, GETDATE())),
(@Customer1_ID, 'promotion', N'Chương trình ưu đãi mới', N'Giảm 15% cho đơn hàng tiếp theo. Mã: SUMMER2025', '/products', 0, DATEADD(DAY, -5, GETDATE())),
(@Customer2_ID, 'order_status', N'Đơn hàng đã giao', N'Đơn hàng ' + @Order5_Number + ' đã được giao thành công!', '/user/orders/' + CAST(@Order5_ID AS VARCHAR), 1, DATEADD(DAY, -9, GETDATE())),
(@Customer2_ID, 'promotion', N'Flash Sale đang diễn ra', N'Nike Dunk Low giảm 15%. Nhanh tay!', '/products/nike-dunk-low-retro', 0, DATEADD(HOUR, -2, GETDATE())),
(@Customer3_ID, 'order_status', N'Đơn hàng đã xác nhận', N'Đơn hàng ' + @Order6_Number + ' đã được xác nhận và đang chuẩn bị giao.', '/user/orders/' + CAST(@Order6_ID AS VARCHAR), 0, DATEADD(DAY, -2, GETDATE())),
(@Customer3_ID, 'product_restock', N'Sản phẩm yêu thích có hàng', N'New Balance 990v5 đã có hàng trở lại!', '/products/new-balance-990v5', 1, DATEADD(DAY, -4, GETDATE()));

-- Thêm wishlists cho customers mới
INSERT INTO Wishlists (user_id, product_id, created_at) VALUES
(@Customer1_ID, @Blazer_ID, DATEADD(DAY, -15, GETDATE())),
(@Customer1_ID, @Gazelle_ID, DATEADD(DAY, -12, GETDATE())),
(@Customer1_ID, @NB990_ID, DATEADD(DAY, -8, GETDATE())),
(@Customer2_ID, @DunkLow_ID, DATEADD(DAY, -14, GETDATE())),
(@Customer2_ID, @ReactRun_ID, DATEADD(DAY, -10, GETDATE())),
(@Customer2_ID, @Future_ID, DATEADD(DAY, -6, GETDATE())),
(@Customer3_ID, @Pegasus_ID, DATEADD(DAY, -11, GETDATE())),
(@Customer3_ID, @NB2002_ID, DATEADD(DAY, -7, GETDATE())),
(@Customer3_ID, @Clyde_ID, DATEADD(DAY, -3, GETDATE()));

PRINT '   Da them 3 users, 3 orders, 8 reviews, 12 notifications, 9 wishlists';
PRINT '';

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH INSERT TOAN BO DU LIEU!';
PRINT '=====================================================';
PRINT '';
PRINT 'SUMMARY:';
PRINT '   [1] 5 users total (2 test accounts + 3 customers) + addresses';
PRINT '   [2] 6 brands (Nike, Adidas, Puma, New Balance, Converse, Vans)';
PRINT '   [3] 10 categories with nested set model';
PRINT '   [4] 21 products across all brands';
PRINT '   [5] Product-Category mappings';
PRINT '   [6] 60+ product variants with different sizes/colors';
PRINT '   [7] Product images';
PRINT '   [8] 8 flash sales (active + upcoming)';
PRINT '   [9] 12 coupons (general + brand + category specific)';
PRINT '   [10] 6 orders total (delivered, processing, confirmed, pending)';
PRINT '   [11] 23 reviews with ratings';
PRINT '   [12] Loyalty points transactions';
PRINT '   [13] 19 notifications (read + unread)';
PRINT '   [14] 16 wishlist items';
PRINT '   [15] Product statistics updated';
PRINT '';
PRINT 'LOGIN INFO:';
PRINT '   Admin: testadmin@sneakery.com / admin123';
PRINT '   User:  testuser@sneakery.com / user123';
PRINT '   Customer1: customer1@example.com / admin123';
PRINT '   Customer2: customer2@example.com / admin123';
PRINT '   Customer3: customer3@example.com / admin123';
PRINT '';
PRINT 'DATABASE READY FOR DEMO!';
PRINT '=====================================================';
PRINT '';
GO


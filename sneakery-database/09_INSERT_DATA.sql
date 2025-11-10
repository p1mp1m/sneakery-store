-- =====================================================
-- SNEAKERY E-COMMERCE - INSERT DATA (QUY MO NHO)
-- =====================================================
-- File này insert dữ liệu mẫu cho database Sneakery
-- Quy mô nhỏ phù hợp cho development và testing
-- Thời gian: từ 1/8/2025 đến 1/11/2025
-- Tuân thủ thứ tự foreign key dependency để đảm bảo tính toàn vẹn
-- Dữ liệu được đồng bộ và thống nhất
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

BEGIN TRANSACTION; -- Wrap all inserts in a transaction for safety

PRINT '=====================================================';
PRINT 'DANG THEM DU LIEU (QUY MO NHO)...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- TIER 1: TABLES WITHOUT DEPENDENCIES
-- =====================================================
PRINT 'TIER 1: Dang them Users, Brands, Categories, Materials, Shoe_Soles, Email_Templates...';

-- 1. USERS (10 users: 2 admins + 8 regular)
INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified, created_at) VALUES
-- Admin users
('admin@sneakery.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Admin Sneakery', '0123456789', 'ADMIN', 1, 1, '2025-08-01 08:00:00'),
('moderator@sneakery.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Moderator Sneakery', '0123456790', 'MODERATOR', 1, 1, '2025-08-01 08:30:00'),
-- Regular users
('nguyen.van.a@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Nguyễn Văn An', '0987654321', 'USER', 1, 1, '2025-08-02 09:00:00'),
('tran.thi.b@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Bình', '0987654322', 'USER', 1, 1, '2025-08-02 10:00:00'),
('le.van.c@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Cường', '0987654323', 'USER', 1, 1, '2025-08-03 11:00:00'),
('pham.thi.d@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Dung', '0987654324', 'USER', 1, 1, '2025-08-03 12:00:00'),
('hoang.van.e@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Em', '0987654325', 'USER', 1, 1, '2025-08-04 13:00:00'),
('vu.thi.f@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Phương', '0987654326', 'USER', 1, 1, '2025-08-04 14:00:00'),
('dang.van.g@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn Giang', '0987654327', 'USER', 1, 1, '2025-08-05 15:00:00'),
('bui.thi.h@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Hoa', '0987654328', 'USER', 1, 1, '2025-08-05 16:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' users';

-- 2. BRANDS (6 brands)
INSERT INTO Brands (name, slug, logo_url, description, is_active) VALUES
(N'Nike', 'nike', 'https://example.com/logos/nike.png', N'Just Do It - Thương hiệu thể thao hàng đầu thế giới', 1),
(N'Adidas', 'adidas', 'https://example.com/logos/adidas.png', N'Impossible is Nothing - Thương hiệu thể thao Đức', 1),
(N'Jordan', 'jordan', 'https://example.com/logos/jordan.png', N'Air Jordan - Dòng giày bóng rổ huyền thoại', 1),
(N'Converse', 'converse', 'https://example.com/logos/converse.png', N'Chuck Taylor All Star - Giày canvas cổ điển', 1),
(N'Vans', 'vans', 'https://example.com/logos/vans.png', N'Off The Wall - Thương hiệu skateboard', 1),
(N'New Balance', 'new-balance', 'https://example.com/logos/new-balance.png', N'Made in USA - Chất lượng cao cấp', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' brands';

-- 2a. MATERIALS (6 materials)
INSERT INTO Materials (name, slug, description, is_active) VALUES
(N'Da Thật', 'da-that', N'Da thật cao cấp, bền bỉ và thoáng khí', 1),
(N'Da Tổng Hợp', 'da-tong-hop', N'Da tổng hợp có độ bền cao và dễ bảo quản', 1),
(N'Vải Canvas', 'vai-canvas', N'Vải canvas bền chắc, thoáng khí', 1),
(N'Vải Mesh', 'vai-mesh', N'Vải mesh nhẹ, thoáng khí cho giày thể thao', 1),
(N'Knit', 'knit', N'Chất liệu knit linh hoạt và hiện đại', 1),
(N'Microfiber', 'microfiber', N'Microfiber mềm mại, bền và dễ vệ sinh', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' materials';

-- 2b. SHOE_SOLES (6 types)
INSERT INTO Shoe_Soles (name, slug, description, is_active) VALUES
(N'Cao Su', 'cao-su', N'Đế cao su tự nhiên có độ bền cao', 1),
(N'EVA', 'eva', N'Đế EVA nhẹ, đàn hồi tốt', 1),
(N'TPU', 'tpu', N'Đế TPU cứng chắc và bền bỉ', 1),
(N'Air Cushion', 'air-cushion', N'Đế công nghệ Air Cushion với túi khí', 1),
(N'Boost', 'boost', N'Đế Boost với hạt TPU nén', 1),
(N'React', 'react', N'Đế React với foam đàn hồi', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' shoe soles';

-- 3. CATEGORIES (8 categories: 2 parents + 6 children)
INSERT INTO Categories (name, slug, description, parent_id, lft, rgt, level, is_active, display_order) VALUES
-- Main categories (level 0)
(N'Giày Nam', 'giay-nam', N'Giày thể thao và giày thời trang cho nam', NULL, 1, 10, 0, 1, 1),
(N'Giày Nữ', 'giay-nu', N'Giày thể thao và giày thời trang cho nữ', NULL, 11, 20, 0, 1, 2),
-- Subcategories for Men (level 1)
(N'Giày Chạy Bộ Nam', 'giay-chay-bo-nam', N'Giày chạy bộ cho nam', 1, 2, 5, 1, 1, 1),
(N'Giày Bóng Rổ Nam', 'giay-bong-ro-nam', N'Giày bóng rổ cho nam', 1, 6, 9, 1, 1, 2),
-- Subcategories for Women (level 1)
(N'Giày Chạy Bộ Nữ', 'giay-chay-bo-nu', N'Giày chạy bộ cho nữ', 2, 12, 15, 1, 1, 1),
(N'Giày Thời Trang Nữ', 'giay-thoi-trang-nu', N'Giày thời trang cho nữ', 2, 16, 19, 1, 1, 2);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' categories';

-- 4. EMAIL_TEMPLATES (3 templates)
INSERT INTO Email_Templates (template_name, subject, body, variables, is_active) VALUES
('welcome', N'Chào mừng bạn đến với Sneakery', N'<h1>Chào mừng bạn đến với Sneakery!</h1><p>Xin chào {{full_name}},</p><p>Cảm ơn bạn đã đăng ký tài khoản tại Sneakery.</p>', 'full_name,email', 1),
('order_confirmation', N'Xác nhận đơn hàng #{{order_number}}', N'<h1>Xác nhận đơn hàng</h1><p>Xin chào {{customer_name}},</p><p>Đơn hàng #{{order_number}} của bạn đã được xác nhận.</p>', 'customer_name,order_number,total_amount', 1),
('password_reset', N'Đặt lại mật khẩu Sneakery', N'<h1>Đặt lại mật khẩu</h1><p>Xin chào {{full_name}},</p><p>Bạn đã yêu cầu đặt lại mật khẩu.</p>', 'full_name,reset_link', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' email templates';

PRINT 'TIER 1 HOAN THANH!';
GO

-- =====================================================
-- TIER 2: DEPENDS ON BRANDS/CATEGORIES
-- =====================================================
PRINT '';
PRINT 'TIER 2: Dang them Products va Product_Categories...';

-- 5. PRODUCTS (20 products)
INSERT INTO Products (brand_id, product_code, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
-- Nike Products (brand_id = 1)
(1, 'SP00001', N'Nike Air Max 270', 'nike-air-max-270', N'Giày chạy bộ Nike Air Max 270 với công nghệ Air Max đột phá, mang lại cảm giác êm ái và năng động cho mỗi bước chạy.', N'Giày chạy bộ Nike Air Max 270', 1, 1, 1, 1250, 45, 4.5, 23),
(1, 'SP00002', N'Nike Air Force 1', 'nike-air-force-1', N'Giày bóng rổ cổ điển Nike Air Force 1 với thiết kế đơn giản nhưng đầy phong cách, phù hợp cho mọi hoạt động.', N'Giày bóng rổ Nike Air Force 1', 1, 1, 0, 2100, 78, 4.7, 56),
(1, 'SP00003', N'Nike Dunk Low', 'nike-dunk-low', N'Giày lifestyle Nike Dunk Low với thiết kế cổ điển và phong cách streetwear.', N'Giày lifestyle Nike Dunk Low', 1, 1, 0, 1500, 35, 4.4, 22),
(1, 'SP00004', N'Nike Pegasus 39', 'nike-pegasus-39', N'Giày chạy bộ Nike Pegasus 39 với công nghệ React foam, thoải mái và bền bỉ.', N'Giày chạy bộ Nike Pegasus 39', 1, 0, 1, 1350, 42, 4.5, 28),
-- Adidas Products (brand_id = 2)
(2, 'SP00005', N'Adidas Ultraboost 22', 'adidas-ultraboost-22', N'Giày chạy bộ Adidas Ultraboost 22 với công nghệ Boost đột phá, mang lại năng lượng và sự thoải mái tối đa.', N'Giày chạy bộ Adidas Ultraboost 22', 1, 1, 1, 1680, 67, 4.6, 42),
(2, 'SP00006', N'Adidas Stan Smith', 'adidas-stan-smith', N'Giày tennis cổ điển Adidas Stan Smith với thiết kế đơn giản, thanh lịch và dễ phối đồ.', N'Giày tennis Adidas Stan Smith', 1, 0, 0, 1950, 89, 4.4, 67),
(2, 'SP00007', N'Adidas NMD R1', 'adidas-nmd-r1', N'Giày lifestyle Adidas NMD R1 với thiết kế hiện đại và công nghệ Boost.', N'Giày lifestyle Adidas NMD R1', 1, 1, 0, 1420, 54, 4.2, 35),
(2, 'SP00008', N'Adidas Superstar', 'adidas-superstar', N'Giày sneakers cổ điển Adidas Superstar với vỏ sò đặc trưng.', N'Giày sneakers Adidas Superstar', 1, 1, 0, 1800, 42, 4.5, 28),
-- Jordan Products (brand_id = 3)
(3, 'SP00009', N'Air Jordan 1 Retro High', 'air-jordan-1-retro-high', N'Giày bóng rổ Air Jordan 1 Retro High - phiên bản tái tạo của đôi giày huyền thoại từ năm 1985.', N'Giày bóng rổ Air Jordan 1 Retro High', 1, 1, 1, 3200, 156, 4.8, 89),
(3, 'SP00010', N'Air Jordan 4 Retro', 'air-jordan-4-retro', N'Giày bóng rổ Air Jordan 4 Retro với thiết kế đặc trưng và công nghệ Air-Sole.', N'Giày bóng rổ Air Jordan 4 Retro', 1, 1, 0, 2800, 134, 4.7, 76),
(3, 'SP00011', N'Air Jordan 3 Retro', 'air-jordan-3-retro', N'Giày bóng rổ Air Jordan 3 với Elephant Print nổi tiếng.', N'Giày bóng rổ Air Jordan 3', 1, 0, 1, 2100, 89, 4.6, 52),
-- Converse Products (brand_id = 4)
(4, 'SP00012', N'Converse Chuck Taylor All Star', 'converse-chuck-taylor-all-star', N'Giày canvas cổ điển Converse Chuck Taylor All Star với thiết kế đơn giản, phù hợp cho mọi phong cách.', N'Giày canvas Converse Chuck Taylor All Star', 1, 0, 0, 1800, 112, 4.3, 45),
(4, 'SP00013', N'Converse Chuck 70', 'converse-chuck-70', N'Giày canvas Converse Chuck 70 cao cấp với chất liệu tốt hơn.', N'Giày canvas Converse Chuck 70', 1, 1, 0, 950, 24, 4.3, 14),
(4, 'SP00014', N'Converse One Star', 'converse-one-star', N'Giày lifestyle Converse One Star với thiết kế đơn giản và phong cách streetwear.', N'Giày lifestyle Converse One Star', 1, 0, 1, 950, 28, 4.1, 12),
-- Vans Products (brand_id = 5)
(5, 'SP00015', N'Vans Old Skool', 'vans-old-skool', N'Giày skateboard Vans Old Skool với thiết kế cổ điển và chất lượng bền bỉ.', N'Giày skateboard Vans Old Skool', 1, 1, 0, 1650, 73, 4.4, 38),
(5, 'SP00016', N'Vans Authentic', 'vans-authentic', N'Giày canvas Vans Authentic với thiết kế đơn giản và phong cách skateboard cổ điển.', N'Giày canvas Vans Authentic', 1, 0, 0, 1200, 45, 4.2, 25),
(5, 'SP00017', N'Vans Sk8-Hi', 'vans-sk8-hi', N'Giày skateboard Vans Sk8-Hi high-top với thiết kế cổ điển.', N'Giày skateboard Vans Sk8-Hi', 1, 1, 0, 1400, 38, 4.4, 21),
-- New Balance Products (brand_id = 6)
(6, 'SP00018', N'New Balance 550', 'new-balance-550', N'Giày sneakers New Balance 550 với thiết kế retro và cảm giác thoải mái.', N'Giày sneakers New Balance 550', 1, 1, 0, 1600, 41, 4.5, 26),
(6, 'SP00019', N'New Balance 574', 'new-balance-574', N'Giày sneakers New Balance 574 cổ điển với chất lượng cao.', N'Giày sneakers New Balance 574', 1, 1, 1, 1750, 52, 4.6, 38),
(6, 'SP00020', N'New Balance 993', 'new-balance-993', N'Giày chạy bộ New Balance 993 với công nghệ ENCAP và đệm êm ái.', N'Giày chạy bộ New Balance 993', 1, 0, 0, 980, 19, 4.3, 11);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' products';

-- 6. PRODUCT_CATEGORIES (20 relationships)
INSERT INTO Product_Categories (product_id, category_id) VALUES
-- Nike products -> Running/Basketball
(1, 3), -- Nike Air Max 270 -> Giày Chạy Bộ Nam
(2, 4), -- Nike Air Force 1 -> Giày Bóng Rổ Nam
(3, 3), -- Nike Dunk Low -> Giày Chạy Bộ Nam
(4, 3), -- Nike Pegasus 39 -> Giày Chạy Bộ Nam
-- Adidas products -> Running/Fashion
(5, 5), -- Adidas Ultraboost 22 -> Giày Chạy Bộ Nữ
(6, 6), -- Adidas Stan Smith -> Giày Thời Trang Nữ
(7, 5), -- Adidas NMD R1 -> Giày Chạy Bộ Nữ
(8, 6), -- Adidas Superstar -> Giày Thời Trang Nữ
-- Jordan products -> Basketball
(9, 4), -- Air Jordan 1 -> Giày Bóng Rổ Nam
(10, 4), -- Air Jordan 4 -> Giày Bóng Rổ Nam
(11, 4), -- Air Jordan 3 -> Giày Bóng Rổ Nam
-- Converse products -> Running/Fashion
(12, 3), -- Converse Chuck Taylor -> Giày Chạy Bộ Nam
(13, 3), -- Converse Chuck 70 -> Giày Chạy Bộ Nam
(14, 3), -- Converse One Star -> Giày Chạy Bộ Nam
-- Vans products -> Running
(15, 3), -- Vans Old Skool -> Giày Chạy Bộ Nam
(16, 3), -- Vans Authentic -> Giày Chạy Bộ Nam
(17, 3), -- Vans Sk8-Hi -> Giày Chạy Bộ Nam
-- New Balance products -> Running
(18, 3), -- New Balance 550 -> Giày Chạy Bộ Nam
(19, 3), -- New Balance 574 -> Giày Chạy Bộ Nam
(20, 3); -- New Balance 993 -> Giày Chạy Bộ Nam
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product categories';

PRINT 'TIER 2 HOAN THANH!';
GO

-- =====================================================
-- TIER 3: DEPENDS ON PRODUCTS
-- =====================================================
PRINT '';
PRINT 'TIER 3: Dang them Product_Variants, Coupons, Flash_Sales...';

-- 7. PRODUCT_VARIANTS (40 variants: 2 per product)
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, low_stock_threshold, is_active) VALUES
-- Product 1: Nike Air Max 270
(1, 'NIKE-AM270-BLK-40', '40', N'Đen', 3200000, 2800000, 2000000, 25, 5, 1),
(1, 'NIKE-AM270-BLK-41', '41', N'Đen', 3200000, 2800000, 2000000, 30, 5, 1),
-- Product 2: Nike Air Force 1
(2, 'NIKE-AF1-BLK-40', '40', N'Đen', 2500000, 2200000, 1500000, 35, 5, 1),
(2, 'NIKE-AF1-WHT-40', '40', N'Trắng', 2500000, 2200000, 1500000, 32, 5, 1),
-- Product 3: Nike Dunk Low
(3, 'NIKE-DL-BLK-40', '40', N'Đen', 2800000, 2500000, 1500000, 20, 5, 1),
(3, 'NIKE-DL-WHT-40', '40', N'Trắng', 2800000, 2500000, 1500000, 18, 5, 1),
-- Product 4: Nike Pegasus 39
(4, 'NIKE-PEG-BLK-40', '40', N'Đen', 3200000, 2900000, 2000000, 20, 5, 1),
(4, 'NIKE-PEG-BLU-40', '40', N'Xanh dương', 3200000, 2900000, 2000000, 18, 5, 1),
-- Product 5: Adidas Ultraboost 22
(5, 'ADIDAS-UB22-BLK-36', '36', N'Đen', 4500000, 4000000, 2800000, 20, 5, 1),
(5, 'ADIDAS-UB22-BLK-37', '37', N'Đen', 4500000, 4000000, 2800000, 25, 5, 1),
-- Product 6: Adidas Stan Smith
(6, 'ADIDAS-SS-WHT-36', '36', N'Trắng', 2100000, 1900000, 1200000, 30, 10, 1),
(6, 'ADIDAS-SS-WHT-37', '37', N'Trắng', 2100000, 1900000, 1200000, 28, 10, 1),
-- Product 7: Adidas NMD R1
(7, 'ADIDAS-NMD-BLK-36', '36', N'Đen', 3200000, 2900000, 2000000, 20, 5, 1),
(7, 'ADIDAS-NMD-BLK-37', '37', N'Đen', 3200000, 2900000, 2000000, 22, 5, 1),
-- Product 8: Adidas Superstar
(8, 'ADIDAS-SUP-BLK-36', '36', N'Đen', 2400000, 2200000, 1400000, 25, 5, 1),
(8, 'ADIDAS-SUP-WHT-36', '36', N'Trắng', 2400000, 2200000, 1400000, 23, 5, 1),
-- Product 9: Air Jordan 1
(9, 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 5500000, 5000000, 3500000, 15, 3, 1),
(9, 'JORDAN-1-BLK-41', '41', N'Đen/Trắng', 5500000, 5000000, 3500000, 18, 3, 1),
-- Product 10: Air Jordan 4
(10, 'JORDAN-4-BLK-40', '40', N'Đen/Xám', 4800000, 4400000, 3200000, 15, 3, 1),
(10, 'JORDAN-4-BLK-41', '41', N'Đen/Xám', 4800000, 4400000, 3200000, 18, 3, 1),
-- Product 11: Air Jordan 3
(11, 'JORDAN-3-BLK-40', '40', N'Đen/Trắng', 5200000, 4800000, 3300000, 15, 3, 1),
(11, 'JORDAN-3-BLK-41', '41', N'Đen/Trắng', 5200000, 4800000, 3300000, 17, 3, 1),
-- Product 12: Converse Chuck Taylor
(12, 'CONVERSE-CT-BLK-40', '40', N'Đen', 1200000, 1000000, 600000, 50, 10, 1),
(12, 'CONVERSE-CT-WHT-40', '40', N'Trắng', 1200000, 1000000, 600000, 45, 10, 1),
-- Product 13: Converse Chuck 70
(13, 'CONVERSE-70-BLK-40', '40', N'Đen', 1500000, 1300000, 800000, 40, 10, 1),
(13, 'CONVERSE-70-WHT-40', '40', N'Trắng', 1500000, 1300000, 800000, 35, 10, 1),
-- Product 14: Converse One Star
(14, 'CONVERSE-OS-BLK-40', '40', N'Đen', 1500000, 1300000, 800000, 40, 10, 1),
(14, 'CONVERSE-OS-BLK-41', '41', N'Đen', 1500000, 1300000, 800000, 35, 10, 1),
-- Product 15: Vans Old Skool
(15, 'VANS-OS-BLK-40', '40', N'Đen/Trắng', 1800000, 1600000, 1000000, 35, 5, 1),
(15, 'VANS-OS-BLK-41', '41', N'Đen/Trắng', 1800000, 1600000, 1000000, 40, 5, 1),
-- Product 16: Vans Authentic
(16, 'VANS-AUTH-BLK-40', '40', N'Đen', 1600000, 1400000, 900000, 30, 5, 1),
(16, 'VANS-AUTH-WHT-40', '40', N'Trắng', 1600000, 1400000, 900000, 35, 5, 1),
-- Product 17: Vans Sk8-Hi
(17, 'VANS-SK8-BLK-40', '40', N'Đen/Trắng', 1900000, 1700000, 1100000, 30, 5, 1),
(17, 'VANS-SK8-BLK-41', '41', N'Đen/Trắng', 1900000, 1700000, 1100000, 32, 5, 1),
-- Product 18: New Balance 550
(18, 'NB-550-BLK-40', '40', N'Đen', 3200000, 2900000, 2000000, 30, 5, 1),
(18, 'NB-550-GRY-40', '40', N'Xám', 3200000, 2900000, 2000000, 25, 5, 1),
-- Product 19: New Balance 574
(19, 'NB-574-BLK-40', '40', N'Đen', 2800000, 2600000, 1800000, 35, 5, 1),
(19, 'NB-574-GRY-40', '40', N'Xám', 2800000, 2600000, 1800000, 30, 5, 1),
-- Product 20: New Balance 993
(20, 'NB-993-GRY-40', '40', N'Xám', 3800000, 3500000, 2500000, 20, 5, 1),
(20, 'NB-993-GRY-41', '41', N'Xám', 3800000, 3500000, 2500000, 22, 5, 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product variants';

-- NOTE: Product_Images và Variant_Images đã được tách ra file 10_INSERT_IMAGES.sql riêng
-- Chạy file 10_INSERT_IMAGES.sql sau khi chạy file này để thêm dữ liệu ảnh

-- 9. COUPONS (5 coupons)
INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, applicable_to, is_active) VALUES
('WELCOME10', N'Giảm 10% cho khách hàng mới', 'percentage', 10.00, 500000, 500000, '2025-08-01', '2025-11-01', 1000, 45, 1, 'all', 1),
('SAVE50K', N'Giảm 50,000 VNĐ cho đơn hàng từ 1,000,000 VNĐ', 'fixed', 50000, 1000000, 50000, '2025-08-01', '2025-11-01', 500, 23, 2, 'all', 1),
('NIKE20', N'Giảm 20% cho giày Nike', 'percentage', 20.00, 2000000, 1000000, '2025-08-01', '2025-11-01', 200, 12, 1, 'brand', 1),
('SUMMER15', N'Giảm 15% cho mùa hè', 'percentage', 15.00, 800000, 300000, '2025-09-01', '2025-10-31', 300, 8, 1, 'all', 1),
('VIP30', N'Giảm 30% cho khách VIP', 'percentage', 30.00, 3000000, 2000000, '2025-08-01', '2025-11-01', 50, 3, 1, 'all', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' coupons';

-- 10. FLASH_SALES (5 flash sales)
INSERT INTO Flash_Sales (product_id, discount_percent, start_time, end_time, quantity_limit, sold_count, is_active) VALUES
(1, 25.00, '2025-08-16 00:00:00', '2025-08-16 23:59:59', 50, 12, 1),
(5, 20.00, '2025-08-16 00:00:00', '2025-08-16 23:59:59', 40, 15, 1),
(9, 15.00, '2025-08-16 00:00:00', '2025-08-16 23:59:59', 20, 5, 1),
(15, 30.00, '2025-08-17 00:00:00', '2025-08-17 23:59:59', 100, 28, 1),
(18, 25.00, '2025-08-17 00:00:00', '2025-08-17 23:59:59', 45, 22, 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' flash sales';

PRINT 'TIER 3 HOAN THANH!';
GO

-- =====================================================
-- TIER 4: DEPENDS ON USERS
-- =====================================================
PRINT '';
PRINT 'TIER 4: Dang them Addresses, Notifications, Wishlists, Carts...';

-- 11. ADDRESSES (8 addresses for users 3-6)
INSERT INTO Addresses (user_id, recipient_name, phone, line1, line2, ward, district, city, postal_code, is_default, address_type, created_at) VALUES
(3, N'Nguyễn Văn An', '0987654321', N'123 Đường Lê Lợi', N'Tầng 5, Chung cư ABC', N'Phường Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2025-08-05 10:00:00'),
(3, N'Nguyễn Văn An', '0987654321', N'456 Đường Nguyễn Huệ', N'Căn hộ 302', N'Phường Đa Kao', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 0, 'office', '2025-08-05 11:00:00'),
(4, N'Trần Thị Bình', '0987654322', N'789 Đường Cách Mạng Tháng 8', N'Căn hộ 205', N'Phường 10', N'Quận 3', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2025-08-06 12:00:00'),
(4, N'Trần Thị Bình', '0987654322', N'321 Đường Võ Văn Tần', N'Tầng 4', N'Phường 6', N'Quận 3', N'TP. Hồ Chí Minh', '700000', 0, 'office', '2025-08-06 13:00:00'),
(5, N'Lê Văn Cường', '0987654323', N'654 Đường Điện Biên Phủ', N'Căn hộ 101', N'Phường 25', N'Quận Bình Thạnh', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2025-08-07 14:00:00'),
(5, N'Lê Văn Cường', '0987654323', N'111 Đường Hoàng Văn Thụ', N'Tầng 8', N'Phường 4', N'Quận Phú Nhuận', N'TP. Hồ Chí Minh', '700000', 0, 'office', '2025-08-07 15:00:00'),
(6, N'Phạm Thị Dung', '0987654324', N'222 Đường Lê Đức Thọ', N'Căn hộ 403', N'Phường Đông Hưng Thuận', N'Quận 12', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2025-08-08 16:00:00'),
(6, N'Phạm Thị Dung', '0987654324', N'333 Đường Nguyễn Thái Sơn', N'Tầng 12', N'Phường 3', N'Quận Gò Vấp', N'TP. Hồ Chí Minh', '700000', 0, 'office', '2025-08-08 17:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' addresses';

-- 12. NOTIFICATIONS (10 notifications)
INSERT INTO Notifications (user_id, type, title, message, link, is_read, created_at) VALUES
(3, 'system', N'Chào mừng bạn đến với Sneakery', N'Cảm ơn bạn đã đăng ký tài khoản!', '/', 1, '2025-08-05 10:00:00'),
(4, 'promotion', N'Khuyến mãi mới', N'Giảm giá 20% cho tất cả giày. Áp dụng đến hết tháng 10.', '/products', 0, '2025-08-16 09:00:00'),
(5, 'product_restock', N'Sản phẩm đã có hàng trở lại', N'Nike Air Max 270 màu đen đã có hàng trở lại!', '/products/1', 0, '2025-08-16 10:00:00'),
(6, 'review_reply', N'Admin đã trả lời đánh giá của bạn', N'Cảm ơn bạn đã đánh giá sản phẩm Adidas Stan Smith!', '/products/6', 1, '2025-08-16 14:00:00'),
(3, 'order_status', N'Đơn hàng đang được xử lý', N'Đơn hàng của bạn đang được chuẩn bị.', '/orders', 0, '2025-08-16 11:00:00'),
(4, 'order_status', N'Đơn hàng đã được giao', N'Đơn hàng của bạn đã được giao thành công.', '/orders', 1, '2025-08-17 16:00:00'),
(5, 'promotion', N'Flash Sale sắp bắt đầu!', N'Flash Sale 50% sẽ bắt đầu trong 1 giờ nữa.', '/flash-sales', 0, '2025-08-17 08:00:00'),
(6, 'system', N'Thông báo hệ thống', N'Hệ thống sẽ bảo trì vào đêm nay từ 23:00 đến 1:00.', '/', 0, '2025-08-17 20:00:00'),
(3, 'order_status', N'Đơn hàng đã được xác nhận', N'Đơn hàng ORD-20250810-0001 của bạn đã được xác nhận.', '/orders', 0, '2025-08-10 13:30:00'),
(4, 'product_restock', N'Sản phẩm yêu thích đã có hàng', N'Air Jordan 1 Retro High đã có hàng trở lại!', '/products/9', 1, '2025-08-15 10:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' notifications';

-- 13. WISHLISTS (5 wishlist items)
INSERT INTO Wishlists (user_id, product_id, created_at) VALUES
(3, 1, '2025-08-06 10:00:00'),
(3, 9, '2025-08-06 14:30:00'),
(4, 5, '2025-08-07 09:15:00'),
(5, 2, '2025-08-08 11:45:00'),
(6, 18, '2025-08-09 15:15:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' wishlist items';

-- 14. CARTS (5 carts for users 3-7)
INSERT INTO Carts (user_id, session_id, created_at, updated_at) VALUES
(3, NULL, '2025-08-16 10:00:00', '2025-08-16 10:00:00'),
(4, NULL, '2025-08-16 14:00:00', '2025-08-16 14:00:00'),
(5, NULL, '2025-08-16 09:00:00', '2025-08-16 09:00:00'),
(6, NULL, '2025-08-16 16:00:00', '2025-08-16 16:00:00'),
(7, NULL, '2025-08-17 11:00:00', '2025-08-17 11:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' carts';

PRINT 'TIER 4 HOAN THANH!';
GO

-- =====================================================
-- TIER 5: DEPENDS ON ADDRESSES
-- =====================================================
PRINT '';
PRINT 'TIER 5: Dang them Orders...';

-- 15. ORDERS (8 orders for users 3-6)
INSERT INTO Orders (user_id, order_number, address_shipping_id, address_billing_id, subtotal, shipping_fee, discount_amount, total_amount, status, shipping_method, customer_note, created_at) VALUES
-- Orders for user 3
(3, 'ORD-20250810-0001', 1, 1, 2800000, 50000, 280000, 2570000, 'delivered', 'standard', N'Giao hàng vào buổi chiều', '2025-08-10 10:30:00'),
(3, 'ORD-20250815-0001', 2, 2, 3500000, 100000, 350000, 3250000, 'shipped', 'express', N'Giao hàng nhanh', '2025-08-15 14:00:00'),
-- Orders for user 4
(4, 'ORD-20250811-0001', 3, 3, 1900000, 50000, 0, 1950000, 'delivered', 'standard', NULL, '2025-08-11 11:00:00'),
(4, 'ORD-20250818-0001', 4, 4, 2900000, 50000, 290000, 2660000, 'processing', 'express', N'Kiểm tra kỹ sản phẩm', '2025-08-18 09:30:00'),
-- Orders for user 5
(5, 'ORD-20250812-0001', 5, 5, 4000000, 100000, 400000, 3700000, 'delivered', 'standard', NULL, '2025-08-12 15:00:00'),
(5, 'ORD-20250820-0001', 6, 6, 2200000, 50000, 220000, 2030000, 'confirmed', 'standard', N'Giao hàng nhanh', '2025-08-20 10:00:00'),
-- Orders for user 6
(6, 'ORD-20250814-0001', 7, 7, 5000000, 100000, 500000, 4600000, 'processing', 'express', NULL, '2025-08-14 13:00:00'),
(6, 'ORD-20250822-0001', 8, 8, 1600000, 30000, 0, 1630000, 'delivered', 'standard', NULL, '2025-08-22 16:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' orders';

PRINT 'TIER 5 HOAN THANH!';
GO

-- =====================================================
-- TIER 6: DEPENDS ON ORDERS + PRODUCT_VARIANTS
-- =====================================================
PRINT '';
PRINT 'TIER 6: Dang them Order_Details, Payments, Order_Status_Histories...';

-- 16. ORDER_DETAILS (8 details for 8 orders)
INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(1, 1, N'Nike Air Max 270', 'NIKE-AM270-BLK-40', '40', N'Đen', 1, 2800000, 2800000),
(2, 20, N'New Balance 993', 'NB-993-GRY-40', '40', N'Xám', 1, 3500000, 3500000),
(3, 11, N'Adidas Stan Smith', 'ADIDAS-SS-WHT-36', '36', N'Trắng', 1, 1900000, 1900000),
(4, 18, N'New Balance 550', 'NB-550-BLK-40', '40', N'Đen', 1, 2900000, 2900000),
(5, 13, N'Adidas NMD R1', 'ADIDAS-NMD-BLK-36', '36', N'Đen', 1, 4000000, 4000000),
(6, 4, N'Nike Air Force 1', 'NIKE-AF1-BLK-40', '40', N'Đen', 1, 2200000, 2200000),
(7, 17, N'Air Jordan 1 Retro High', 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 1, 5000000, 5000000),
(8, 32, N'Vans Authentic', 'VANS-AUTH-BLK-40', '40', N'Đen', 1, 1600000, 1600000);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' order details';

-- 17. PAYMENTS (8 payments for 8 orders)
INSERT INTO Payments (order_id, payment_method, amount, status, transaction_id, paid_at, created_at) VALUES
(1, 'vnpay', 2570000, 'completed', 'VNPAY_20250810_001', '2025-08-10 10:35:00', '2025-08-10 10:30:00'),
(2, 'momo', 3250000, 'completed', 'MOMO_20250815_001', '2025-08-15 14:10:00', '2025-08-15 14:00:00'),
(3, 'cod', 1950000, 'completed', NULL, '2025-08-11 11:30:00', '2025-08-11 11:00:00'),
(4, 'vnpay', 2660000, 'completed', 'VNPAY_20250818_001', '2025-08-18 09:35:00', '2025-08-18 09:30:00'),
(5, 'momo', 3700000, 'completed', 'MOMO_20250812_001', '2025-08-12 15:10:00', '2025-08-12 15:00:00'),
(6, 'vnpay', 2030000, 'pending', NULL, NULL, '2025-08-20 10:00:00'),
(7, 'vnpay', 4600000, 'completed', 'VNPAY_20250814_001', '2025-08-14 13:15:00', '2025-08-14 13:00:00'),
(8, 'cod', 1630000, 'completed', NULL, '2025-08-22 16:50:00', '2025-08-22 16:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' payments';

-- 18. ORDER_STATUS_HISTORIES (8 histories for 8 orders)
INSERT INTO Order_Status_Histories (order_id, status, note, changed_by, created_at) VALUES
(1, 'pending', N'Đơn hàng mới được tạo', 3, '2025-08-10 10:30:00'),
(1, 'confirmed', N'Đơn hàng đã được xác nhận', 3, '2025-08-10 11:00:00'),
(1, 'delivered', N'Đơn hàng đã giao thành công', 3, '2025-08-10 16:30:00'),
(2, 'pending', N'Đơn hàng mới được tạo', 3, '2025-08-15 14:00:00'),
(2, 'confirmed', N'Đơn hàng đã được xác nhận', 3, '2025-08-15 15:00:00'),
(2, 'shipped', N'Đơn hàng đã được vận chuyển', 3, '2025-08-16 10:00:00'),
(3, 'pending', N'Đơn hàng mới được tạo', 4, '2025-08-11 11:00:00'),
(3, 'delivered', N'Đơn hàng đã giao thành công', 4, '2025-08-11 17:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' order status histories';

PRINT 'TIER 6 HOAN THANH!';
GO

-- =====================================================
-- TIER 7: COMPLEX DEPENDENCIES
-- =====================================================
PRINT '';
PRINT 'TIER 7: Dang them Reviews, Inventory_Logs, Activity_Logs, Loyalty_Points, Cart_Items, Size_Charts, Return_Requests, Warranties...';

-- 19. REVIEWS (6 reviews: 4 approved, 2 pending)
INSERT INTO Reviews (product_id, user_id, order_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, created_at) VALUES
(1, 3, 1, 5, N'Giày rất đẹp và thoải mái', N'Tôi rất hài lòng với đôi giày này. Chất lượng tốt, thiết kế đẹp và rất thoải mái khi đi.', 1, 1, 3, '2025-08-11 10:00:00'),
(6, 4, 3, 4, N'Chất lượng tốt', N'Giày Adidas Stan Smith có chất lượng tốt, thiết kế đẹp.', 1, 1, 2, '2025-08-12 14:30:00'),
(20, 3, 2, 5, N'New Balance tuyệt vời', N'Giày New Balance 993 rất thoải mái và đẹp. Đáng giá tiền bỏ ra.', 1, 1, 5, '2025-08-16 09:15:00'),
(9, 4, 7, 5, N'Air Jordan tuyệt vời', N'Air Jordan 1 Retro High là đôi giày tuyệt vời. Chất lượng cao cấp.', 1, 1, 8, '2025-08-15 16:20:00'),
(18, 6, 4, 4, N'New Balance rất thoải mái', N'Giày New Balance 550 rất thoải mái và dễ phối đồ.', 1, 1, 1, '2025-08-19 11:45:00'),
(2, 5, 6, 3, N'Nike Air Force 1 chưa đạt mong đợi', N'Giày hơi cứng so với mong đợi. Cần thời gian để làm quen.', 0, 1, 0, '2025-08-21 10:15:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' reviews (4 approved, 2 pending)';

-- 20. INVENTORY_LOGS (8 logs: restock + sales)
INSERT INTO Inventory_Logs (variant_id, change_type, quantity_before, quantity_change, quantity_after, reference_type, reference_id, note, changed_by, created_at) VALUES
-- Restock logs
(1, 'restock', 0, 25, 25, 'purchase', 1, N'Nhập hàng lần đầu', 1, '2025-08-01 10:00:00'),
(2, 'restock', 0, 30, 30, 'purchase', 2, N'Nhập hàng lần đầu', 1, '2025-08-01 10:00:00'),
(3, 'restock', 0, 35, 35, 'purchase', 3, N'Nhập hàng lần đầu', 1, '2025-08-01 10:00:00'),
(4, 'restock', 0, 40, 40, 'purchase', 4, N'Nhập hàng lần đầu', 1, '2025-08-01 10:00:00'),
-- Sale logs
(1, 'sale', 25, -1, 24, 'order', 1, N'Bán hàng từ đơn hàng ORD-20250810-0001', 1, '2025-08-10 10:30:00'),
(20, 'sale', 20, -1, 19, 'order', 2, N'Bán hàng từ đơn hàng ORD-20250815-0001', 1, '2025-08-15 14:00:00'),
(11, 'sale', 30, -1, 29, 'order', 3, N'Bán hàng từ đơn hàng ORD-20250811-0001', 1, '2025-08-11 11:00:00'),
(18, 'sale', 30, -1, 29, 'order', 4, N'Bán hàng từ đơn hàng ORD-20250818-0001', 1, '2025-08-18 09:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' inventory logs';

-- 21. ACTIVITY_LOGS (5 logs for admin actions)
INSERT INTO Activity_Logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at) VALUES
(1, 'CREATE', 'product', 1, NULL, '{"name":"Nike Air Max 270","price":3200000}', '192.168.1.1', 'Mozilla/5.0', '2025-08-01 10:00:00'),
(1, 'UPDATE', 'product', 1, '{"price":3200000}', '{"price":2800000}', '192.168.1.1', 'Mozilla/5.0', '2025-08-02 14:30:00'),
(1, 'CREATE', 'coupon', 1, NULL, '{"code":"WELCOME10","discount":10}', '192.168.1.1', 'Mozilla/5.0', '2025-08-03 09:00:00'),
(2, 'APPROVE', 'review', 1, '{"is_approved":false}', '{"is_approved":true}', '192.168.1.2', 'Mozilla/5.0', '2025-08-11 10:30:00'),
(2, 'UPDATE', 'user', 3, '{"is_active":true}', '{"is_active":true,"role":"USER"}', '192.168.1.2', 'Mozilla/5.0', '2025-08-08 15:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' activity logs';

-- 22. LOYALTY_POINTS (8 points transactions)
INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, created_at) VALUES
(3, 257, 'earn', 1, N'Tích điểm từ đơn hàng ORD-20250810-0001', '2025-08-10 10:30:00'),
(3, 325, 'earn', 2, N'Tích điểm từ đơn hàng ORD-20250815-0001', '2025-08-15 14:00:00'),
(4, 195, 'earn', 3, N'Tích điểm từ đơn hàng ORD-20250811-0001', '2025-08-11 11:00:00'),
(4, 266, 'earn', 4, N'Tích điểm từ đơn hàng ORD-20250818-0001', '2025-08-18 09:30:00'),
(5, 370, 'earn', 5, N'Tích điểm từ đơn hàng ORD-20250812-0001', '2025-08-12 15:00:00'),
(6, 460, 'earn', 7, N'Tích điểm từ đơn hàng ORD-20250814-0001', '2025-08-14 13:00:00'),
(3, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2025-08-02 10:00:00'),
(4, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2025-08-02 11:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' loyalty points';

-- 23. CART_ITEMS (5 items in carts)
INSERT INTO Cart_Items (cart_id, variant_id, quantity, added_at) VALUES
(1, 5, 1, '2025-08-16 10:00:00'),
(2, 14, 2, '2025-08-16 14:00:00'),
(3, 7, 1, '2025-08-16 09:00:00'),
(4, 19, 1, '2025-08-17 16:00:00'),
(5, 25, 3, '2025-08-17 11:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' cart items';

-- 24. SIZE_CHARTS (6 size charts for brands)
INSERT INTO Size_Charts (brand_id, category, size, size_us, size_uk, length_cm, width_cm) VALUES
(1, 'sneakers', '40', '7', '6', 25.5, 9.5),
(2, 'sneakers', '41', '8', '7', 26.0, 9.8),
(3, 'basketball', '42', '9', '8', 26.5, 10.0),
(4, 'casual', '40', '7', '6', 25.5, 9.5),
(5, 'skateboard', '41', '8', '7', 26.0, 9.8),
(6, 'running', '40', '7', '6', 25.5, 9.5);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' size charts';

-- 25. RETURN_REQUESTS (3 return requests)
INSERT INTO Return_Requests (order_id, user_id, reason, status, admin_note, approved_by, approved_at, created_at) VALUES
(1, 3, N'Không đúng kích thước', 'approved', N'Đã xác nhận, cho phép đổi size', 1, '2025-08-10 11:00:00', '2025-08-10 10:00:00'),
(3, 4, N'Sản phẩm bị lỗi', 'pending', NULL, NULL, NULL, '2025-08-12 14:00:00'),
(5, 5, N'Không đúng màu sắc', 'rejected', N'Màu sắc đúng như mô tả, không thể đổi trả', 1, '2025-08-12 10:00:00', '2025-08-12 09:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' return requests';

-- 25a. WARRANTIES (3 warranty requests)
INSERT INTO Warranties (order_id, user_id, product_id, variant_id, issue_description, warranty_type, warranty_months, status, admin_note, resolution_note, processed_by, processed_at, completed_at, purchase_date, submitted_at, created_at) VALUES
(1, 3, 1, 1, N'Giày bị bong keo sau 2 tuần sử dụng', 'repair', 12, 'pending', NULL, NULL, NULL, NULL, NULL, '2025-08-10 10:30:00', '2025-08-24 10:30:00', '2025-08-24 10:30:00'),
(2, 3, 20, 20, N'Đế giày bị nứt sau 1 tháng sử dụng', 'replace', 12, 'in_progress', N'Đang kiểm tra và liên hệ nhà sản xuất', NULL, 1, '2025-08-16 09:00:00', NULL, '2025-08-15 14:00:00', '2025-08-15 14:00:00', '2025-09-15 14:20:00'),
(4, 4, 18, 18, N'Logo bị phai màu sau 2 tháng', 'replace', 12, 'completed', N'Đã xác nhận lỗi sản xuất', N'Đã gửi sản phẩm thay thế cho khách hàng', 1, '2025-08-19 10:00:00', '2025-08-21 15:30:00', '2025-08-18 09:30:00', '2025-08-18 09:30:00', '2025-10-18 09:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' warranty requests';

PRINT 'TIER 7 HOAN THANH!';
GO

-- =====================================================
-- FINALIZATION & VALIDATION
-- =====================================================
SET NOCOUNT OFF;

-- Validation checks
PRINT '';
PRINT '=====================================================';
PRINT 'DANG KIEM TRA TINH TOAN VEN DU LIEU...';
PRINT '=====================================================';

DECLARE @errorCount INT = 0;

-- Check foreign key integrity
IF EXISTS (SELECT 1 FROM Order_Details od LEFT JOIN Product_Variants pv ON od.variant_id = pv.id WHERE pv.id IS NULL)
BEGIN
    PRINT '  [ERROR] Order_Details có variant_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

IF EXISTS (SELECT 1 FROM Orders o LEFT JOIN Addresses a ON o.address_shipping_id = a.id WHERE a.id IS NULL)
BEGIN
    PRINT '  [ERROR] Orders có address_shipping_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

IF EXISTS (SELECT 1 FROM Payments p LEFT JOIN Orders o ON p.order_id = o.id WHERE o.id IS NULL)
BEGIN
    PRINT '  [ERROR] Payments có order_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

-- Print summary
PRINT '';
IF @errorCount = 0
BEGIN
    PRINT '  [SUCCESS] Tất cả foreign keys hợp lệ!';
    COMMIT TRANSACTION;
END
ELSE
BEGIN
    PRINT '  [WARNING] Có ' + CAST(@errorCount AS VARCHAR) + ' lỗi foreign key. Rollback transaction!';
    ROLLBACK TRANSACTION;
    RETURN;
END;

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH THEM DU LIEU!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da them thanh cong:';
PRINT '  + 10 users (2 admin, 8 regular)';
PRINT '  + 6 brands';
PRINT '  + 8 categories (2 parents, 6 children)';
PRINT '  + 20 products';
PRINT '  + 40 product variants';
PRINT '  + 5 coupons';
PRINT '  + 5 flash sales';
PRINT '  + 8 addresses';
PRINT '  + 8 orders';
PRINT '  + 8 order details';
PRINT '  + 8 payments';
PRINT '  + 6 reviews (4 approved, 2 pending)';
PRINT '  + 10 notifications';
PRINT '  + 5 wishlist items';
PRINT '  + 5 carts';
PRINT '  + 5 cart items';
PRINT '  + 3 email templates';
PRINT '  + 8 loyalty points';
PRINT '  + 8 inventory logs';
PRINT '  + 5 activity logs';
PRINT '  + 6 size charts';
PRINT '  + 3 return requests';
PRINT '  + 3 warranty requests';
PRINT '';
PRINT 'TAT CA DU LIEU DA DUOC THEM THANH CONG!';
PRINT '=====================================================';
PRINT '';
PRINT 'Luu y: Du lieu anh (Product_Images va Variant_Images)';
PRINT 'da duoc tach ra file 10_INSERT_IMAGES.sql rieng.';
PRINT 'Chay file 10_INSERT_IMAGES.sql de them du lieu anh.';
PRINT '';
PRINT 'Script da ket thuc thanh cong!';
PRINT 'Transaction da duoc commit.';
PRINT '';

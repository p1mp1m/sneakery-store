-- =====================================================
-- SNEAKERY E-COMMERCE - BASIC SAMPLE DATA
-- =====================================================
-- File này insert dữ liệu cơ bản (~30-50 records mỗi bảng)
-- Tuân thủ thứ tự foreign key dependency để đảm bảo tính toàn vẹn
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

BEGIN TRANSACTION; -- Wrap all inserts in a transaction for safety

PRINT '=====================================================';
PRINT 'DANG THEM DU LIEU CO BAN...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- TIER 1: TABLES WITHOUT DEPENDENCIES
-- =====================================================
PRINT 'TIER 1: Dang them Users, Brands, Categories, Email_Templates...';

-- 1. USERS (12 users: 2 admins + 10 regular)
INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified) VALUES
-- Admin users
('admin@sneakery.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Admin Sneakery', '0123456789', 'ADMIN', 1, 1),
('moderator@sneakery.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Moderator Sneakery', '0123456790', 'MODERATOR', 1, 1),
-- Regular users
('user1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Nguyễn Văn An', '0987654321', 'USER', 1, 1),
('user2@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Bình', '0987654322', 'USER', 1, 1),
('user3@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Cường', '0987654323', 'USER', 1, 1),
('user4@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Dung', '0987654324', 'USER', 1, 1),
('user5@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Em', '0987654325', 'USER', 1, 1),
('user6@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Phương', '0987654326', 'USER', 1, 1),
('user7@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn Giang', '0987654327', 'USER', 1, 1),
('user8@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Hoa', '0987654328', 'USER', 1, 1),
('user9@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Văn Ích', '0987654329', 'USER', 1, 1),
('user10@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Thị Kim', '0987654330', 'USER', 1, 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' users';

-- 2. BRANDS (10 brands)
INSERT INTO Brands (name, slug, logo_url, description, is_active) VALUES
(N'Nike', 'nike', 'https://example.com/logos/nike.png', N'Just Do It - Thương hiệu thể thao hàng đầu thế giới', 1),
(N'Adidas', 'adidas', 'https://example.com/logos/adidas.png', N'Impossible is Nothing - Thương hiệu thể thao Đức', 1),
(N'Jordan', 'jordan', 'https://example.com/logos/jordan.png', N'Air Jordan - Dòng giày bóng rổ huyền thoại', 1),
(N'Converse', 'converse', 'https://example.com/logos/converse.png', N'Chuck Taylor All Star - Giày canvas cổ điển', 1),
(N'Vans', 'vans', 'https://example.com/logos/vans.png', N'Off The Wall - Thương hiệu skateboard', 1),
(N'Puma', 'puma', 'https://example.com/logos/puma.png', N'Forever Faster - Thương hiệu thể thao Đức', 1),
(N'New Balance', 'new-balance', 'https://example.com/logos/new-balance.png', N'Made in USA - Chất lượng cao cấp', 1),
(N'Reebok', 'reebok', 'https://example.com/logos/reebok.png', N'Be More Human - Thương hiệu fitness', 1),
(N'Under Armour', 'under-armour', 'https://example.com/logos/under-armour.png', N'I Will What I Want - Thương hiệu thể thao Mỹ', 1),
(N'ASICS', 'asics', 'https://example.com/logos/asics.png', N'Anima Sana In Corpore Sano - Thương hiệu Nhật Bản', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' brands';

-- 3. CATEGORIES (16 categories: 4 parents + 12 children)
INSERT INTO Categories (name, slug, description, parent_id, lft, rgt, level, is_active, display_order) VALUES
-- Main categories (level 0)
(N'Giày Nam', 'giay-nam', N'Giày thể thao và giày thời trang cho nam', NULL, 1, 20, 0, 1, 1),
(N'Giày Nữ', 'giay-nu', N'Giày thể thao và giày thời trang cho nữ', NULL, 21, 40, 0, 1, 2),
(N'Giày Trẻ Em', 'giay-tre-em', N'Giày cho trẻ em và thanh thiếu niên', NULL, 41, 60, 0, 1, 3),
(N'Phụ Kiện', 'phu-kien', N'Phụ kiện giày và thời trang', NULL, 61, 80, 0, 1, 4),
-- Subcategories for Men (level 1)
(N'Giày Chạy Bộ Nam', 'giay-chay-bo-nam', N'Giày chạy bộ cho nam', 1, 2, 7, 1, 1, 1),
(N'Giày Bóng Đá Nam', 'giay-bong-da-nam', N'Giày bóng đá cho nam', 1, 8, 13, 1, 1, 2),
(N'Giày Bóng Rổ Nam', 'giay-bong-ro-nam', N'Giày bóng rổ cho nam', 1, 14, 19, 1, 1, 3),
-- Subcategories for Women (level 1)
(N'Giày Chạy Bộ Nữ', 'giay-chay-bo-nu', N'Giày chạy bộ cho nữ', 2, 22, 27, 1, 1, 1),
(N'Giày Thời Trang Nữ', 'giay-thoi-trang-nu', N'Giày thời trang cho nữ', 2, 28, 33, 1, 1, 2),
(N'Giày Cao Gót', 'giay-cao-got', N'Giày cao gót cho nữ', 2, 34, 39, 1, 1, 3),
-- Subcategories for Kids (level 1)
(N'Giày Trẻ Em Nam', 'giay-tre-em-nam', N'Giày cho bé trai', 3, 42, 47, 1, 1, 1),
(N'Giày Trẻ Em Nữ', 'giay-tre-em-nu', N'Giày cho bé gái', 3, 48, 53, 1, 1, 2),
(N'Giày Trẻ Sơ Sinh', 'giay-tre-so-sinh', N'Giày cho trẻ sơ sinh', 3, 54, 59, 1, 1, 3),
-- Subcategories for Accessories (level 1)
(N'Tất', 'tat', N'Tất thể thao và thời trang', 4, 62, 65, 1, 1, 1),
(N'Dây Giày', 'day-giay', N'Dây giày và phụ kiện', 4, 66, 69, 1, 1, 2),
(N'Bảo Vệ Giày', 'bao-ve-giay', N'Phụ kiện bảo vệ giày', 4, 70, 73, 1, 1, 3),
(N'Kem Đánh Giày', 'kem-danh-giay', N'Kem đánh giày và chăm sóc', 4, 74, 79, 1, 1, 4);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' categories';

-- 4. EMAIL_TEMPLATES (4 templates)
INSERT INTO Email_Templates (template_name, subject, body, variables, is_active) VALUES
('welcome', N'Chào mừng bạn đến với Sneakery', N'<h1>Chào mừng bạn đến với Sneakery!</h1><p>Xin chào {{full_name}},</p><p>Cảm ơn bạn đã đăng ký tài khoản tại Sneakery. Chúng tôi rất vui được phục vụ bạn.</p><p>Chúc bạn mua sắm vui vẻ!</p>', 'full_name,email', 1),
('order_confirmation', N'Xác nhận đơn hàng #{{order_number}}', N'<h1>Xác nhận đơn hàng</h1><p>Xin chào {{customer_name}},</p><p>Đơn hàng #{{order_number}} của bạn đã được xác nhận.</p><p>Tổng tiền: {{total_amount}} VNĐ</p><p>Cảm ơn bạn đã mua sắm tại Sneakery!</p>', 'customer_name,order_number,total_amount', 1),
('order_shipped', N'Đơn hàng #{{order_number}} đã được giao', N'<h1>Đơn hàng đã được giao</h1><p>Xin chào {{customer_name}},</p><p>Đơn hàng #{{order_number}} của bạn đã được giao thành công.</p><p>Cảm ơn bạn đã tin tưởng Sneakery!</p>', 'customer_name,order_number', 1),
('password_reset', N'Đặt lại mật khẩu Sneakery', N'<h1>Đặt lại mật khẩu</h1><p>Xin chào {{full_name}},</p><p>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng click vào link sau để đặt lại mật khẩu:</p><p><a href="{{reset_link}}">Đặt lại mật khẩu</a></p>', 'full_name,reset_link', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' email templates';

PRINT 'TIER 1 HOAN THANH!';
GO

-- =====================================================
-- TIER 2: DEPENDS ON BRANDS/CATEGORIES
-- =====================================================
PRINT '';
PRINT 'TIER 2: Dang them Products va Product_Categories...';

-- 5. PRODUCTS (15 products from 6 brands)
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
-- Nike Products (brand_id = 1)
(1, N'Nike Air Max 270', 'nike-air-max-270', N'Giày chạy bộ Nike Air Max 270 với công nghệ Air Max đột phá, mang lại cảm giác êm ái và năng động cho mỗi bước chạy.', N'Giày chạy bộ Nike Air Max 270', 1, 1, 1, 1250, 45, 4.5, 23),
(1, N'Nike Air Force 1', 'nike-air-force-1', N'Giày bóng rổ cổ điển Nike Air Force 1 với thiết kế đơn giản nhưng đầy phong cách, phù hợp cho mọi hoạt động.', N'Giày bóng rổ Nike Air Force 1', 1, 1, 0, 2100, 78, 4.7, 56),
-- Adidas Products (brand_id = 2)
(2, N'Adidas Ultraboost 22', 'adidas-ultraboost-22', N'Giày chạy bộ Adidas Ultraboost 22 với công nghệ Boost đột phá, mang lại năng lượng và sự thoải mái tối đa.', N'Giày chạy bộ Adidas Ultraboost 22', 1, 1, 1, 1680, 67, 4.6, 42),
(2, N'Adidas Stan Smith', 'adidas-stan-smith', N'Giày tennis cổ điển Adidas Stan Smith với thiết kế đơn giản, thanh lịch và dễ phối đồ.', N'Giày tennis Adidas Stan Smith', 1, 0, 0, 1950, 89, 4.4, 67),
(2, N'Adidas NMD R1', 'adidas-nmd-r1', N'Giày lifestyle Adidas NMD R1 với thiết kế hiện đại và công nghệ Boost, phù hợp cho cuộc sống năng động.', N'Giày lifestyle Adidas NMD R1', 1, 1, 0, 1420, 54, 4.2, 35),
-- Jordan Products (brand_id = 3)
(3, N'Air Jordan 1 Retro High', 'air-jordan-1-retro-high', N'Giày bóng rổ Air Jordan 1 Retro High - phiên bản tái tạo của đôi giày huyền thoại từ năm 1985.', N'Giày bóng rổ Air Jordan 1 Retro High', 1, 1, 1, 3200, 156, 4.8, 89),
(3, N'Air Jordan 4 Retro', 'air-jordan-4-retro', N'Giày bóng rổ Air Jordan 4 Retro với thiết kế đặc trưng và công nghệ Air-Sole, biểu tượng của thập niên 90.', N'Giày bóng rổ Air Jordan 4 Retro', 1, 1, 0, 2800, 134, 4.7, 76),
-- Converse Products (brand_id = 4)
(4, N'Converse Chuck Taylor All Star', 'converse-chuck-taylor-all-star', N'Giày canvas cổ điển Converse Chuck Taylor All Star với thiết kế đơn giản, phù hợp cho mọi phong cách.', N'Giày canvas Converse Chuck Taylor All Star', 1, 0, 0, 1800, 112, 4.3, 45),
(4, N'Converse One Star', 'converse-one-star', N'Giày lifestyle Converse One Star với thiết kế đơn giản và phong cách streetwear.', N'Giày lifestyle Converse One Star', 1, 0, 1, 950, 28, 4.1, 12),
-- Vans Products (brand_id = 5)
(5, N'Vans Old Skool', 'vans-old-skool', N'Giày skateboard Vans Old Skool với thiết kế cổ điển và chất lượng bền bỉ, phù hợp cho skateboard và streetwear.', N'Giày skateboard Vans Old Skool', 1, 1, 0, 1650, 73, 4.4, 38),
(5, N'Vans Authentic', 'vans-authentic', N'Giày canvas Vans Authentic với thiết kế đơn giản và phong cách skateboard cổ điển.', N'Giày canvas Vans Authentic', 1, 0, 0, 1200, 45, 4.2, 25),
-- Puma Products (brand_id = 6)
(6, N'Puma Suede Classic', 'puma-suede-classic', N'Giày lifestyle Puma Suede Classic với chất liệu da lộn mềm mại và thiết kế cổ điển.', N'Giày lifestyle Puma Suede Classic', 1, 0, 1, 980, 34, 4.0, 19),
(6, N'Puma RS-X', 'puma-rs-x', N'Giày chạy bộ Puma RS-X với thiết kế retro-futuristic và công nghệ RS (Running System).', N'Giày chạy bộ Puma RS-X', 1, 1, 0, 1350, 52, 4.3, 31),
-- New Balance Products (brand_id = 7)
(7, N'New Balance 550', 'new-balance-550', N'Giày sneakers New Balance 550 với thiết kế retro và cảm giác thoải mái.', N'Giày sneakers New Balance 550', 1, 1, 0, 1600, 41, 4.5, 26),
(7, N'New Balance 993', 'new-balance-993', N'Giày chạy bộ New Balance 993 với công nghệ ENCAP và đệm êm ái.', N'Giày chạy bộ New Balance 993', 1, 0, 0, 980, 19, 4.3, 11);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' products';

-- 6. PRODUCT_CATEGORIES (15 relationships)
INSERT INTO Product_Categories (product_id, category_id) VALUES
-- Products 1-15 mapped to appropriate categories
(1, 5), -- Nike Air Max 270 -> Giày Chạy Bộ Nam
(2, 7), -- Nike Air Force 1 -> Giày Bóng Rổ Nam
(3, 8), -- Adidas Ultraboost 22 -> Giày Chạy Bộ Nữ
(4, 9), -- Adidas Stan Smith -> Giày Thời Trang Nữ
(5, 8), -- Adidas NMD R1 -> Giày Chạy Bộ Nữ
(6, 7), -- Air Jordan 1 -> Giày Bóng Rổ Nam
(7, 7), -- Air Jordan 4 -> Giày Bóng Rổ Nam
(8, 5), -- Converse Chuck Taylor -> Giày Chạy Bộ Nam
(9, 5), -- Converse One Star -> Giày Chạy Bộ Nam
(10, 5), -- Vans Old Skool -> Giày Chạy Bộ Nam
(11, 5), -- Vans Authentic -> Giày Chạy Bộ Nam
(12, 5), -- Puma Suede -> Giày Chạy Bộ Nam
(13, 5), -- Puma RS-X -> Giày Chạy Bộ Nam
(14, 5), -- New Balance 550 -> Giày Chạy Bộ Nam
(15, 5); -- New Balance 993 -> Giày Chạy Bộ Nam
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product categories';

PRINT 'TIER 2 HOAN THANH!';
GO

-- =====================================================
-- TIER 3: DEPENDS ON PRODUCTS
-- =====================================================
PRINT '';
PRINT 'TIER 3: Dang them Product_Variants, Product_Images, Coupons, Flash_Sales...';

-- 7. PRODUCT_VARIANTS (30 variants: 2 per product)
-- Note: Product IDs 1-15 need variants, creating 2 variants per product
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, low_stock_threshold, is_active) VALUES
-- Product 1: Nike Air Max 270
(1, 'NIKE-AM270-BLK-40', '40', N'Đen', 3200000, 2800000, 2000000, 25, 5, 1),
(1, 'NIKE-AM270-BLK-41', '41', N'Đen', 3200000, 2800000, 2000000, 30, 5, 1),
-- Product 2: Nike Air Force 1
(2, 'NIKE-AF1-BLK-40', '40', N'Đen', 2500000, 2200000, 1500000, 35, 5, 1),
(2, 'NIKE-AF1-BLK-41', '41', N'Đen', 2500000, 2200000, 1500000, 40, 5, 1),
-- Product 3: Adidas Ultraboost 22
(3, 'ADIDAS-UB22-BLK-36', '36', N'Đen', 4500000, 4000000, 2800000, 20, 5, 1),
(3, 'ADIDAS-UB22-BLK-37', '37', N'Đen', 4500000, 4000000, 2800000, 25, 5, 1),
-- Product 4: Adidas Stan Smith
(4, 'ADIDAS-SS-WHT-36', '36', N'Trắng', 2100000, 1900000, 1200000, 30, 10, 1),
(4, 'ADIDAS-SS-WHT-37', '37', N'Trắng', 2100000, 1900000, 1200000, 28, 10, 1),
-- Product 5: Adidas NMD R1
(5, 'ADIDAS-NMD-BLK-36', '36', N'Đen', 3200000, 2900000, 2000000, 20, 5, 1),
(5, 'ADIDAS-NMD-BLK-37', '37', N'Đen', 3200000, 2900000, 2000000, 22, 5, 1),
-- Product 6: Air Jordan 1
(6, 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 5500000, 5000000, 3500000, 15, 3, 1),
(6, 'JORDAN-1-BLK-41', '41', N'Đen/Trắng', 5500000, 5000000, 3500000, 18, 3, 1),
-- Product 7: Air Jordan 4
(7, 'JORDAN-4-BLK-40', '40', N'Đen/Xám', 4800000, 4400000, 3200000, 15, 3, 1),
(7, 'JORDAN-4-BLK-41', '41', N'Đen/Xám', 4800000, 4400000, 3200000, 18, 3, 1),
-- Product 8: Converse Chuck Taylor
(8, 'CONVERSE-CT-BLK-40', '40', N'Đen', 1200000, 1000000, 600000, 50, 10, 1),
(8, 'CONVERSE-CT-BLK-41', '41', N'Đen', 1200000, 1000000, 600000, 45, 10, 1),
-- Product 9: Converse One Star
(9, 'CONVERSE-OS-BLK-40', '40', N'Đen', 1500000, 1300000, 800000, 40, 10, 1),
(9, 'CONVERSE-OS-BLK-41', '41', N'Đen', 1500000, 1300000, 800000, 35, 10, 1),
-- Product 10: Vans Old Skool
(10, 'VANS-OS-BLK-40', '40', N'Đen/Trắng', 1800000, 1600000, 1000000, 35, 5, 1),
(10, 'VANS-OS-BLK-41', '41', N'Đen/Trắng', 1800000, 1600000, 1000000, 40, 5, 1),
-- Product 11: Vans Authentic
(11, 'VANS-AUTH-BLK-40', '40', N'Đen', 1600000, 1400000, 900000, 30, 5, 1),
(11, 'VANS-AUTH-BLK-41', '41', N'Đen', 1600000, 1400000, 900000, 35, 5, 1),
-- Product 12: Puma Suede
(12, 'PUMA-SUEDE-BLK-40', '40', N'Đen', 2000000, 1800000, 1200000, 30, 5, 1),
(12, 'PUMA-SUEDE-BLK-41', '41', N'Đen', 2000000, 1800000, 1200000, 32, 5, 1),
-- Product 13: Puma RS-X
(13, 'PUMA-RSX-BLK-40', '40', N'Đen', 2800000, 2500000, 1800000, 25, 5, 1),
(13, 'PUMA-RSX-BLK-41', '41', N'Đen', 2800000, 2500000, 1800000, 28, 5, 1),
-- Product 14: New Balance 550
(14, 'NB-550-BLK-40', '40', N'Đen', 3200000, 2900000, 2000000, 30, 5, 1),
(14, 'NB-550-BLK-41', '41', N'Đen', 3200000, 2900000, 2000000, 25, 5, 1),
-- Product 15: New Balance 993
(15, 'NB-993-GRY-40', '40', N'Xám', 3800000, 3500000, 2500000, 20, 5, 1),
(15, 'NB-993-GRY-41', '41', N'Xám', 3800000, 3500000, 2500000, 22, 5, 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product variants';

-- 8. PRODUCT_IMAGES (15 images: 1 primary per product)
INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order) VALUES
(1, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Max 270 - Góc nhìn chính', 1, 1),
(2, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Force 1 - Góc nhìn chính', 1, 1),
(3, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Ultraboost 22 - Góc nhìn chính', 1, 1),
(4, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Stan Smith - Góc nhìn chính', 1, 1),
(5, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Adidas NMD R1 - Góc nhìn chính', 1, 1),
(6, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 1 Retro High - Góc nhìn chính', 1, 1),
(7, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 4 Retro - Góc nhìn chính', 1, 1),
(8, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck Taylor - Góc nhìn chính', 1, 1),
(9, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse One Star - Góc nhìn chính', 1, 1),
(10, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Old Skool - Góc nhìn chính', 1, 1),
(11, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Authentic - Góc nhìn chính', 1, 1),
(12, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Puma Suede Classic - Góc nhìn chính', 1, 1),
(13, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Puma RS-X - Góc nhìn chính', 1, 1),
(14, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 550 - Góc nhìn chính', 1, 1),
(15, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 993 - Góc nhìn chính', 1, 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product images';

-- 9. COUPONS (5 coupons)
INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, applicable_to, is_active) VALUES
('WELCOME10', N'Giảm 10% cho khách hàng mới', 'percentage', 10.00, 500000, 500000, '2024-01-01', '2024-12-31', 1000, 45, 1, 'all', 1),
('SAVE50K', N'Giảm 50,000 VNĐ cho đơn hàng từ 1,000,000 VNĐ', 'fixed', 50000, 1000000, 50000, '2024-01-01', '2024-12-31', 500, 23, 2, 'all', 1),
('NIKE20', N'Giảm 20% cho giày Nike', 'percentage', 20.00, 2000000, 1000000, '2024-01-01', '2024-12-31', 200, 12, 1, 'brand', 1),
('SUMMER15', N'Giảm 15% cho mùa hè', 'percentage', 15.00, 800000, 300000, '2024-06-01', '2024-08-31', 300, 8, 1, 'all', 1),
('VIP30', N'Giảm 30% cho khách VIP', 'percentage', 30.00, 3000000, 2000000, '2024-01-01', '2024-12-31', 50, 3, 1, 'all', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' coupons';

-- 10. FLASH_SALES (5 flash sales)
INSERT INTO Flash_Sales (product_id, discount_percent, start_time, end_time, quantity_limit, sold_count, is_active) VALUES
(1, 25.00, '2024-03-01 00:00:00', '2024-03-01 23:59:59', 50, 12, 1),
(3, 20.00, '2024-03-02 00:00:00', '2024-03-02 23:59:59', 40, 15, 1),
(6, 15.00, '2024-03-03 00:00:00', '2024-03-03 23:59:59', 20, 5, 1),
(10, 30.00, '2024-03-04 00:00:00', '2024-03-04 23:59:59', 100, 28, 1),
(14, 25.00, '2024-03-05 00:00:00', '2024-03-05 23:59:59', 45, 22, 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' flash sales';

PRINT 'TIER 3 HOAN THANH!';
GO

-- =====================================================
-- TIER 4: DEPENDS ON USERS
-- =====================================================
PRINT '';
PRINT 'TIER 4: Dang them Addresses, Notifications, Wishlists, Carts...';

-- 11. ADDRESSES (10 addresses: 2 per user for users 3-7)
INSERT INTO Addresses (user_id, recipient_name, phone, line1, line2, ward, district, city, postal_code, is_default, address_type) VALUES
(3, N'Nguyễn Văn An', '0987654321', N'123 Đường Lê Lợi', N'Tầng 5, Chung cư ABC', N'Phường Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(3, N'Nguyễn Văn An', '0987654321', N'456 Đường Nguyễn Huệ', N'Căn hộ 302', N'Phường Đa Kao', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 0, 'office'),
(4, N'Trần Thị Bình', '0987654322', N'789 Đường Cách Mạng Tháng 8', N'Căn hộ 205', N'Phường 10', N'Quận 3', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(4, N'Trần Thị Bình', '0987654322', N'321 Đường Võ Văn Tần', N'Tầng 4', N'Phường 6', N'Quận 3', N'TP. Hồ Chí Minh', '700000', 0, 'office'),
(5, N'Lê Văn Cường', '0987654323', N'654 Đường Điện Biên Phủ', N'Căn hộ 101', N'Phường 25', N'Quận Bình Thạnh', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(5, N'Lê Văn Cường', '0987654323', N'111 Đường Hoàng Văn Thụ', N'Tầng 8', N'Phường 4', N'Quận Phú Nhuận', N'TP. Hồ Chí Minh', '700000', 0, 'office'),
(6, N'Phạm Thị Dung', '0987654324', N'222 Đường Lê Đức Thọ', N'Căn hộ 403', N'Phường Đông Hưng Thuận', N'Quận 12', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(6, N'Phạm Thị Dung', '0987654324', N'333 Đường Nguyễn Thái Sơn', N'Tầng 12', N'Phường 3', N'Quận Gò Vấp', N'TP. Hồ Chí Minh', '700000', 0, 'office'),
(7, N'Hoàng Văn Em', '0987654325', N'444 Đường Phạm Hùng', N'Căn hộ 502', N'Phường Tân Phú', N'Quận 9', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(7, N'Hoàng Văn Em', '0987654325', N'555 Đường Lê Văn Việt', N'Tầng 15', N'Phường Hiệp Phú', N'Quận 9', N'TP. Hồ Chí Minh', '700000', 0, 'office');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' addresses';

-- 12. NOTIFICATIONS (8 notifications for users 3-7)
INSERT INTO Notifications (user_id, type, title, message, link, is_read, created_at) VALUES
(3, 'system', N'Chào mừng bạn đến với Sneakery', N'Cảm ơn bạn đã đăng ký tài khoản!', '/', 1, '2024-01-15 10:00:00'),
(4, 'promotion', N'Khuyến mãi mới', N'Giảm giá 20% cho tất cả giày. Áp dụng đến hết tháng 3.', '/products', 0, '2024-03-01 09:00:00'),
(5, 'product_restock', N'Sản phẩm đã có hàng trở lại', N'Nike Air Max 270 màu đen đã có hàng trở lại!', '/products/1', 0, '2024-03-02 10:00:00'),
(6, 'review_reply', N'Admin đã trả lời đánh giá của bạn', N'Cảm ơn bạn đã đánh giá sản phẩm Adidas Stan Smith!', '/products/4', 1, '2024-03-03 14:00:00'),
(7, 'order_status', N'Đơn hàng đang được xử lý', N'Đơn hàng của bạn đang được chuẩn bị.', '/orders', 0, '2024-03-04 11:00:00'),
(3, 'order_status', N'Đơn hàng đã được giao', N'Đơn hàng của bạn đã được giao thành công.', '/orders', 1, '2024-03-05 16:00:00'),
(4, 'promotion', N'Flash Sale sắp bắt đầu!', N'Flash Sale 50% sẽ bắt đầu trong 1 giờ nữa.', '/flash-sales', 0, '2024-03-06 08:00:00'),
(5, 'system', N'Thông báo hệ thống', N'Hệ thống sẽ bảo trì vào đêm nay từ 23:00 đến 1:00.', '/', 0, '2024-03-07 20:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' notifications';

-- 13. WISHLISTS (10 wishlist items for users 3-7)
INSERT INTO Wishlists (user_id, product_id, created_at) VALUES
(3, 1, '2024-01-10 10:00:00'),
(3, 6, '2024-01-12 14:30:00'),
(4, 3, '2024-01-11 09:15:00'),
(4, 14, '2024-01-13 16:20:00'),
(5, 2, '2024-01-14 11:45:00'),
(5, 10, '2024-01-15 13:30:00'),
(6, 4, '2024-01-16 08:00:00'),
(6, 15, '2024-01-17 15:15:00'),
(7, 5, '2024-01-18 12:30:00'),
(7, 11, '2024-01-19 10:45:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' wishlist items';

-- 14. CARTS (5 carts for users 3-7)
INSERT INTO Carts (user_id, session_id, created_at, updated_at) VALUES
(3, NULL, '2024-03-01 10:00:00', '2024-03-01 10:00:00'),
(4, NULL, '2024-03-02 14:00:00', '2024-03-02 14:00:00'),
(5, NULL, '2024-03-03 09:00:00', '2024-03-03 09:00:00'),
(6, NULL, '2024-03-04 16:00:00', '2024-03-04 16:00:00'),
(7, NULL, '2024-03-05 11:00:00', '2024-03-05 11:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' carts';

PRINT 'TIER 4 HOAN THANH!';
GO

-- =====================================================
-- TIER 5: DEPENDS ON ADDRESSES
-- =====================================================
PRINT '';
PRINT 'TIER 5: Dang them Orders...';

-- 15. ORDERS (10 orders for users 3-7, using addresses 1-10)
INSERT INTO Orders (user_id, order_number, address_shipping_id, address_billing_id, subtotal, shipping_fee, discount_amount, total_amount, status, shipping_method, customer_note, created_at) VALUES
-- Orders for user 3
(3, 'ORD-20240115-0001', 1, 1, 2800000, 50000, 280000, 2570000, 'delivered', 'standard', N'Giao hàng vào buổi chiều', '2024-01-15 10:30:00'),
(3, 'ORD-20240201-0001', 2, 2, 3500000, 100000, 350000, 3250000, 'shipped', 'express', N'Giao hàng nhanh', '2024-02-01 14:00:00'),
-- Orders for user 4
(4, 'ORD-20240120-0001', 3, 3, 1900000, 50000, 0, 1950000, 'delivered', 'standard', NULL, '2024-01-20 11:00:00'),
(4, 'ORD-20240205-0001', 4, 4, 2900000, 50000, 290000, 2660000, 'processing', 'express', N'Kiểm tra kỹ sản phẩm', '2024-02-05 09:30:00'),
-- Orders for user 5
(5, 'ORD-20240125-0001', 5, 5, 4000000, 100000, 400000, 3700000, 'delivered', 'standard', NULL, '2024-01-25 15:00:00'),
(5, 'ORD-20240210-0001', 6, 6, 2200000, 50000, 220000, 2030000, 'confirmed', 'standard', N'Giao hàng nhanh', '2024-02-10 10:00:00'),
-- Orders for user 6
(6, 'ORD-20240215-0001', 7, 7, 5000000, 100000, 500000, 4600000, 'processing', 'express', NULL, '2024-02-15 13:00:00'),
(6, 'ORD-20240220-0001', 8, 8, 1600000, 30000, 0, 1630000, 'delivered', 'standard', NULL, '2024-02-20 16:30:00'),
-- Orders for user 7
(7, 'ORD-20240225-0001', 9, 9, 3800000, 100000, 380000, 3520000, 'packed', 'express', N'Yêu cầu gói kỹ', '2024-02-25 11:00:00'),
(7, 'ORD-20240301-0001', 10, 10, 1400000, 30000, 0, 1430000, 'delivered', 'standard', NULL, '2024-03-01 14:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' orders';

PRINT 'TIER 5 HOAN THANH!';
GO

-- =====================================================
-- TIER 6: DEPENDS ON ORDERS + PRODUCT_VARIANTS
-- =====================================================
PRINT '';
PRINT 'TIER 6: Dang them Order_Details, Payments, Order_Status_Histories...';

-- 16. ORDER_DETAILS (10 details for 10 orders)
-- Note: Using variant IDs 1-30 that were created
INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(1, 1, N'Nike Air Max 270', 'NIKE-AM270-BLK-40', '40', N'Đen', 1, 2800000, 2800000),
(2, 6, N'New Balance 993', 'NB-993-GRY-40', '40', N'Xám', 1, 3500000, 3500000),
(3, 7, N'Adidas Stan Smith', 'ADIDAS-SS-WHT-36', '36', N'Trắng', 1, 1900000, 1900000),
(4, 28, N'New Balance 550', 'NB-550-BLK-40', '40', N'Đen', 1, 2900000, 2900000),
(5, 9, N'Adidas NMD R1', 'ADIDAS-NMD-BLK-36', '36', N'Đen', 1, 4000000, 4000000),
(6, 4, N'Nike Air Force 1', 'NIKE-AF1-BLK-41', '41', N'Đen', 1, 2200000, 2200000),
(7, 12, N'Air Jordan 1 Retro High', 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 1, 5000000, 5000000),
(8, 22, N'Vans Authentic', 'VANS-AUTH-BLK-40', '40', N'Đen', 1, 1600000, 1600000),
(9, 26, N'New Balance 993', 'NB-993-GRY-41', '41', N'Xám', 1, 3800000, 3800000),
(10, 22, N'Vans Authentic', 'VANS-AUTH-BLK-40', '40', N'Đen', 1, 1400000, 1400000);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' order details';

-- 17. PAYMENTS (10 payments for 10 orders)
INSERT INTO Payments (order_id, payment_method, amount, status, transaction_id, paid_at, created_at) VALUES
(1, 'vnpay', 2570000, 'completed', 'VNPAY_20240115_001', '2024-01-15 10:35:00', '2024-01-15 10:30:00'),
(2, 'momo', 3250000, 'completed', 'MOMO_20240201_001', '2024-02-01 14:10:00', '2024-02-01 14:00:00'),
(3, 'cod', 1950000, 'completed', NULL, '2024-01-20 11:30:00', '2024-01-20 11:00:00'),
(4, 'vnpay', 2660000, 'completed', 'VNPAY_20240205_001', '2024-02-05 09:35:00', '2024-02-05 09:30:00'),
(5, 'momo', 3700000, 'completed', 'MOMO_20240125_001', '2024-01-25 15:10:00', '2024-01-25 15:00:00'),
(6, 'vnpay', 2030000, 'pending', NULL, NULL, '2024-02-10 10:00:00'),
(7, 'vnpay', 4600000, 'completed', 'VNPAY_20240215_001', '2024-02-15 13:15:00', '2024-02-15 13:00:00'),
(8, 'cod', 1630000, 'completed', NULL, '2024-02-20 16:50:00', '2024-02-20 16:30:00'),
(9, 'momo', 3520000, 'completed', 'MOMO_20240225_001', '2024-02-25 11:15:00', '2024-02-25 11:00:00'),
(10, 'cod', 1430000, 'completed', NULL, '2024-03-01 15:00:00', '2024-03-01 14:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' payments';

-- 18. ORDER_STATUS_HISTORIES (10 histories for 10 orders)
INSERT INTO Order_Status_Histories (order_id, status, note, changed_by, created_at) VALUES
(1, 'pending', N'Đơn hàng mới được tạo', 3, '2024-01-15 10:30:00'),
(1, 'confirmed', N'Đơn hàng đã được xác nhận', 3, '2024-01-15 11:00:00'),
(1, 'delivered', N'Đơn hàng đã giao thành công', 3, '2024-01-15 16:30:00'),
(2, 'pending', N'Đơn hàng mới được tạo', 3, '2024-02-01 14:00:00'),
(2, 'confirmed', N'Đơn hàng đã được xác nhận', 3, '2024-02-01 15:00:00'),
(2, 'shipped', N'Đơn hàng đã được vận chuyển', 3, '2024-02-02 10:00:00'),
(3, 'pending', N'Đơn hàng mới được tạo', 4, '2024-01-20 11:00:00'),
(3, 'delivered', N'Đơn hàng đã giao thành công', 4, '2024-01-20 17:00:00'),
(4, 'processing', N'Đơn hàng đang được xử lý', 4, '2024-02-05 09:30:00'),
(5, 'delivered', N'Đơn hàng đã giao thành công', 5, '2024-01-25 20:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' order status histories';

PRINT 'TIER 6 HOAN THANH!';
GO

-- =====================================================
-- TIER 7: COMPLEX DEPENDENCIES
-- =====================================================
PRINT '';
PRINT 'TIER 7: Dang them Reviews, Inventory_Logs, Activity_Logs, Loyalty_Points, Cart_Items, Size_Charts, Return_Requests...';

-- 19. REVIEWS (5 reviews for verified purchases)
INSERT INTO Reviews (product_id, user_id, order_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, created_at) VALUES
(1, 3, 1, 5, N'Giày rất đẹp và thoải mái', N'Tôi rất hài lòng với đôi giày này. Chất lượng tốt, thiết kế đẹp và rất thoải mái khi đi.', 1, 1, 3, '2024-01-20 10:00:00'),
(3, 4, 3, 4, N'Chất lượng tốt', N'Giày Adidas Stan Smith có chất lượng tốt, thiết kế đẹp.', 1, 1, 2, '2024-01-25 14:30:00'),
(15, 3, 2, 5, N'New Balance tuyệt vời', N'Giày New Balance 993 rất thoải mái và đẹp. Đáng giá tiền bỏ ra.', 1, 1, 5, '2024-02-05 09:15:00'),
(6, 4, 4, 5, N'Air Jordan tuyệt vời', N'Air Jordan 1 Retro High là đôi giày tuyệt vời. Chất lượng cao cấp.', 1, 1, 8, '2024-02-10 16:20:00'),
(14, 7, 10, 4, N'New Balance rất thoải mái', N'Giày New Balance 550 rất thoải mái và dễ phối đồ.', 1, 1, 1, '2024-03-05 11:45:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' reviews';

-- 20. INVENTORY_LOGS (10 logs: restock + sales)
INSERT INTO Inventory_Logs (variant_id, change_type, quantity_before, quantity_change, quantity_after, reference_type, reference_id, note, changed_by, created_at) VALUES
-- Restock logs
(1, 'restock', 0, 25, 25, 'purchase', 1, N'Nhập hàng lần đầu', 1, '2024-01-01 10:00:00'),
(2, 'restock', 0, 30, 30, 'purchase', 2, N'Nhập hàng lần đầu', 1, '2024-01-01 10:00:00'),
(3, 'restock', 0, 35, 35, 'purchase', 3, N'Nhập hàng lần đầu', 1, '2024-01-01 10:00:00'),
(4, 'restock', 0, 40, 40, 'purchase', 4, N'Nhập hàng lần đầu', 1, '2024-01-01 10:00:00'),
(5, 'restock', 0, 20, 20, 'purchase', 5, N'Nhập hàng lần đầu', 1, '2024-01-01 10:00:00'),
-- Sale logs
(1, 'sale', 25, -1, 24, 'order', 1, N'Bán hàng từ đơn hàng ORD-20240115-0001', 1, '2024-01-15 10:30:00'),
(6, 'sale', 35, -1, 34, 'order', 2, N'Bán hàng từ đơn hàng ORD-20240201-0001', 1, '2024-02-01 14:00:00'),
(7, 'sale', 30, -1, 29, 'order', 3, N'Bán hàng từ đơn hàng ORD-20240120-0001', 1, '2024-01-20 11:00:00'),
(28, 'sale', 30, -1, 29, 'order', 4, N'Bán hàng từ đơn hàng ORD-20240205-0001', 1, '2024-02-05 09:30:00'),
(9, 'sale', 20, -1, 19, 'order', 5, N'Bán hàng từ đơn hàng ORD-20240125-0001', 1, '2024-01-25 15:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' inventory logs';

-- 21. ACTIVITY_LOGS (5 logs for admin actions)
INSERT INTO Activity_Logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at) VALUES
(1, 'CREATE', 'product', 1, NULL, '{"name":"Nike Air Max 270","price":3200000}', '192.168.1.1', 'Mozilla/5.0', '2024-01-01 10:00:00'),
(1, 'UPDATE', 'product', 1, '{"price":3200000}', '{"price":2800000}', '192.168.1.1', 'Mozilla/5.0', '2024-01-05 14:30:00'),
(1, 'CREATE', 'coupon', 1, NULL, '{"code":"WELCOME10","discount":10}', '192.168.1.1', 'Mozilla/5.0', '2024-01-10 09:00:00'),
(2, 'APPROVE', 'review', 1, '{"is_approved":false}', '{"is_approved":true}', '192.168.1.2', 'Mozilla/5.0', '2024-01-20 10:30:00'),
(2, 'UPDATE', 'user', 3, '{"is_active":true}', '{"is_active":true,"role":"USER"}', '192.168.1.2', 'Mozilla/5.0', '2024-02-01 15:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' activity logs';

-- 22. LOYALTY_POINTS (10 points transactions)
INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, created_at) VALUES
(3, 257, 'earn', 1, N'Tích điểm từ đơn hàng ORD-20240115-0001', '2024-01-15 10:30:00'),
(3, 325, 'earn', 2, N'Tích điểm từ đơn hàng ORD-20240201-0001', '2024-02-01 14:00:00'),
(4, 195, 'earn', 3, N'Tích điểm từ đơn hàng ORD-20240120-0001', '2024-01-20 11:00:00'),
(4, 266, 'earn', 4, N'Tích điểm từ đơn hàng ORD-20240205-0001', '2024-02-05 09:30:00'),
(5, 370, 'earn', 5, N'Tích điểm từ đơn hàng ORD-20240125-0001', '2024-01-25 15:00:00'),
(6, 460, 'earn', 7, N'Tích điểm từ đơn hàng ORD-20240215-0001', '2024-02-15 13:00:00'),
(7, 352, 'earn', 9, N'Tích điểm từ đơn hàng ORD-20240225-0001', '2024-02-25 11:00:00'),
(3, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2024-01-01 10:00:00'),
(4, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2024-01-05 11:00:00'),
(5, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2024-01-10 12:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' loyalty points';

-- 23. CART_ITEMS (5 items in carts)
INSERT INTO Cart_Items (cart_id, variant_id, quantity, added_at) VALUES
(1, 8, 1, '2024-03-02 10:00:00'),
(2, 14, 2, '2024-03-03 14:00:00'),
(3, 20, 1, '2024-03-04 09:00:00'),
(4, 16, 1, '2024-03-05 16:00:00'),
(5, 24, 3, '2024-03-06 11:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' cart items';

-- 24. SIZE_CHARTS (5 size charts for brands)
INSERT INTO Size_Charts (brand_id, category, size, size_us, size_uk, length_cm, width_cm) VALUES
(1, 'sneakers', '40', '7', '6', 25.5, 9.5),
(2, 'sneakers', '41', '8', '7', 26.0, 9.8),
(3, 'basketball', '42', '9', '8', 26.5, 10.0),
(4, 'casual', '40', '7', '6', 25.5, 9.5),
(5, 'skateboard', '41', '8', '7', 26.0, 9.8);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' size charts';

-- 25. RETURN_REQUESTS (2 return requests)
INSERT INTO Return_Requests (order_id, user_id, reason, status, created_at) VALUES
(1, 3, N'Không đúng kích thước', 'approved', '2024-01-16 10:00:00'),
(3, 4, N'Sản phẩm bị lỗi', 'pending', '2024-01-21 14:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' return requests';

PRINT 'TIER 7 HOAN THANH!';
GO

-- =====================================================
-- FINALIZATION
-- =====================================================
SET NOCOUNT OFF;

COMMIT TRANSACTION; -- Commit all inserts

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH THEM DU LIEU CO BAN!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da them thanh cong:';
PRINT '  + 12 users (2 admin, 10 regular)';
PRINT '  + 10 brands';
PRINT '  + 16 categories';
PRINT '  + 15 products';
PRINT '  + 30 product variants';
PRINT '  + 15 product images';
PRINT '  + 5 coupons';
PRINT '  + 5 flash sales';
PRINT '  + 10 addresses';
PRINT '  + 10 orders';
PRINT '  + 10 order details';
PRINT '  + 10 payments';
PRINT '  + 5 reviews';
PRINT '  + 8 notifications';
PRINT '  + 10 wishlist items';
PRINT '  + 5 carts';
PRINT '  + 5 cart items';
PRINT '  + 4 email templates';
PRINT '  + 10 loyalty points';
PRINT '  + 10 inventory logs';
PRINT '  + 5 activity logs';
PRINT '  + 5 size charts';
PRINT '  + 2 return requests';
PRINT '';
PRINT 'Tien hanh chay file 3_ADD_MORE_DATA.sql de bo sung du lieu';
PRINT '=====================================================';
PRINT '';

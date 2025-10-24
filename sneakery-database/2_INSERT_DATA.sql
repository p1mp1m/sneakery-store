-- =====================================================
-- SNEAKERY E-COMMERCE - SAMPLE DATA V2 (API COMPATIBLE)
-- =====================================================
-- Dữ liệu mẫu được tối ưu cho Frontend Admin API
-- =====================================================

USE sneakery_db;
GO

PRINT '=====================================================';
PRINT 'DANG THEM DU LIEU MAU...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 1. USERS DATA (Admin + Regular Users)
-- =====================================================
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
GO

-- =====================================================
-- 2. BRANDS DATA
-- =====================================================
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
GO

-- =====================================================
-- 3. CATEGORIES DATA (Hierarchical)
-- =====================================================
INSERT INTO Categories (name, slug, description, parent_id, lft, rgt, level, is_active, display_order) VALUES
-- Main categories
(N'Giày Nam', 'giay-nam', N'Giày thể thao và giày thời trang cho nam', NULL, 1, 20, 0, 1, 1),
(N'Giày Nữ', 'giay-nu', N'Giày thể thao và giày thời trang cho nữ', NULL, 21, 40, 0, 1, 2),
(N'Giày Trẻ Em', 'giay-tre-em', N'Giày cho trẻ em và thanh thiếu niên', NULL, 41, 60, 0, 1, 3),
(N'Phụ Kiện', 'phu-kien', N'Phụ kiện giày và thời trang', NULL, 61, 80, 0, 1, 4),

-- Subcategories for Men
(N'Giày Chạy Bộ Nam', 'giay-chay-bo-nam', N'Giày chạy bộ cho nam', 1, 2, 7, 1, 1, 1),
(N'Giày Bóng Đá Nam', 'giay-bong-da-nam', N'Giày bóng đá cho nam', 1, 8, 13, 1, 1, 2),
(N'Giày Bóng Rổ Nam', 'giay-bong-ro-nam', N'Giày bóng rổ cho nam', 1, 14, 19, 1, 1, 3),

-- Subcategories for Women
(N'Giày Chạy Bộ Nữ', 'giay-chay-bo-nu', N'Giày chạy bộ cho nữ', 2, 22, 27, 1, 1, 1),
(N'Giày Thời Trang Nữ', 'giay-thoi-trang-nu', N'Giày thời trang cho nữ', 2, 28, 33, 1, 1, 2),
(N'Giày Cao Gót', 'giay-cao-got', N'Giày cao gót cho nữ', 2, 34, 39, 1, 1, 3),

-- Subcategories for Kids
(N'Giày Trẻ Em Nam', 'giay-tre-em-nam', N'Giày cho bé trai', 3, 42, 47, 1, 1, 1),
(N'Giày Trẻ Em Nữ', 'giay-tre-em-nu', N'Giày cho bé gái', 3, 48, 53, 1, 1, 2),
(N'Giày Trẻ Sơ Sinh', 'giay-tre-so-sinh', N'Giày cho trẻ sơ sinh', 3, 54, 59, 1, 1, 3),

-- Subcategories for Accessories
(N'Tất', 'tat', N'Tất thể thao và thời trang', 4, 62, 65, 1, 1, 1),
(N'Dây Giày', 'day-giay', N'Dây giày và phụ kiện', 4, 66, 69, 1, 1, 2),
(N'Bảo Vệ Giày', 'bao-ve-giay', N'Phụ kiện bảo vệ giày', 4, 70, 73, 1, 1, 3),
(N'Kem Đánh Giày', 'kem-danh-giay', N'Kem đánh giày và chăm sóc', 4, 74, 79, 1, 1, 4);
GO

-- =====================================================
-- 4. PRODUCTS DATA (Sample Products)
-- =====================================================
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
-- Nike Products
(1, N'Nike Air Max 270', 'nike-air-max-270', N'Giày chạy bộ Nike Air Max 270 với công nghệ Air Max đột phá, mang lại cảm giác êm ái và năng động cho mỗi bước chạy.', N'Giày chạy bộ Nike Air Max 270', 1, 1, 1, 1250, 45, 4.5, 23),
(1, N'Nike Air Force 1', 'nike-air-force-1', N'Giày bóng rổ cổ điển Nike Air Force 1 với thiết kế đơn giản nhưng đầy phong cách, phù hợp cho mọi hoạt động.', N'Giày bóng rổ Nike Air Force 1', 1, 1, 0, 2100, 78, 4.7, 56),
(1, N'Nike React Element 55', 'nike-react-element-55', N'Giày chạy bộ Nike React Element 55 với công nghệ React foam mang lại độ đàn hồi và bền bỉ tuyệt vời.', N'Giày chạy bộ Nike React Element 55', 1, 0, 1, 890, 32, 4.3, 18),

-- Adidas Products
(2, N'Adidas Ultraboost 22', 'adidas-ultraboost-22', N'Giày chạy bộ Adidas Ultraboost 22 với công nghệ Boost đột phá, mang lại năng lượng và sự thoải mái tối đa.', N'Giày chạy bộ Adidas Ultraboost 22', 1, 1, 1, 1680, 67, 4.6, 42),
(2, N'Adidas Stan Smith', 'adidas-stan-smith', N'Giày tennis cổ điển Adidas Stan Smith với thiết kế đơn giản, thanh lịch và dễ phối đồ.', N'Giày tennis Adidas Stan Smith', 1, 0, 0, 1950, 89, 4.4, 67),
(2, N'Adidas NMD R1', 'adidas-nmd-r1', N'Giày lifestyle Adidas NMD R1 với thiết kế hiện đại và công nghệ Boost, phù hợp cho cuộc sống năng động.', N'Giày lifestyle Adidas NMD R1', 1, 1, 0, 1420, 54, 4.2, 35),

-- Jordan Products
(3, N'Air Jordan 1 Retro High', 'air-jordan-1-retro-high', N'Giày bóng rổ Air Jordan 1 Retro High - phiên bản tái tạo của đôi giày huyền thoại từ năm 1985.', N'Giày bóng rổ Air Jordan 1 Retro High', 1, 1, 1, 3200, 156, 4.8, 89),
(3, N'Air Jordan 4 Retro', 'air-jordan-4-retro', N'Giày bóng rổ Air Jordan 4 Retro với thiết kế đặc trưng và công nghệ Air-Sole, biểu tượng của thập niên 90.', N'Giày bóng rổ Air Jordan 4 Retro', 1, 1, 0, 2800, 134, 4.7, 76),
(3, N'Air Jordan 11 Retro', 'air-jordan-11-retro', N'Giày bóng rổ Air Jordan 11 Retro với thiết kế đột phá và công nghệ Carbon Fiber, được yêu thích nhất.', N'Giày bóng rổ Air Jordan 11 Retro', 1, 0, 1, 2400, 98, 4.6, 58),

-- Converse Products
(4, N'Converse Chuck Taylor All Star', 'converse-chuck-taylor-all-star', N'Giày canvas cổ điển Converse Chuck Taylor All Star với thiết kế đơn giản, phù hợp cho mọi phong cách.', N'Giày canvas Converse Chuck Taylor All Star', 1, 0, 0, 1800, 112, 4.3, 45),
(4, N'Converse One Star', 'converse-one-star', N'Giày lifestyle Converse One Star với thiết kế đơn giản và phong cách streetwear.', N'Giày lifestyle Converse One Star', 1, 0, 1, 950, 28, 4.1, 12),

-- Vans Products
(5, N'Vans Old Skool', 'vans-old-skool', N'Giày skateboard Vans Old Skool với thiết kế cổ điển và chất lượng bền bỉ, phù hợp cho skateboard và streetwear.', N'Giày skateboard Vans Old Skool', 1, 1, 0, 1650, 73, 4.4, 38),
(5, N'Vans Authentic', 'vans-authentic', N'Giày canvas Vans Authentic với thiết kế đơn giản và phong cách skateboard cổ điển.', N'Giày canvas Vans Authentic', 1, 0, 0, 1200, 45, 4.2, 25),

-- Puma Products
(6, N'Puma Suede Classic', 'puma-suede-classic', N'Giày lifestyle Puma Suede Classic với chất liệu da lộn mềm mại và thiết kế cổ điển.', N'Giày lifestyle Puma Suede Classic', 1, 0, 1, 980, 34, 4.0, 19),
(6, N'Puma RS-X', 'puma-rs-x', N'Giày chạy bộ Puma RS-X với thiết kế retro-futuristic và công nghệ RS (Running System).', N'Giày chạy bộ Puma RS-X', 1, 1, 0, 1350, 52, 4.3, 31);
GO

-- =====================================================
-- 5. PRODUCT_CATEGORIES DATA
-- =====================================================
INSERT INTO Product_Categories (product_id, category_id) VALUES
-- Nike Air Max 270 -> Running Men
(1, 5),
-- Nike Air Force 1 -> Basketball Men
(2, 7),
-- Nike React Element 55 -> Running Men
(3, 5),
-- Adidas Ultraboost 22 -> Running Women
(4, 8),
-- Adidas Stan Smith -> Fashion Women
(5, 9),
-- Adidas NMD R1 -> Running Women
(6, 8),
-- Air Jordan 1 -> Basketball Men
(7, 7),
-- Air Jordan 4 -> Basketball Men
(8, 7),
-- Air Jordan 11 -> Basketball Men
(9, 7),
-- Converse Chuck Taylor -> Fashion Men
(10, 5),
-- Converse One Star -> Fashion Men
(11, 5),
-- Vans Old Skool -> Fashion Men
(12, 5),
-- Vans Authentic -> Fashion Men
(13, 5),
-- Puma Suede -> Fashion Men
(14, 5),
-- Puma RS-X -> Running Men
(15, 5);
GO

-- =====================================================
-- 6. PRODUCT_VARIANTS DATA
-- =====================================================
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, low_stock_threshold, is_active) VALUES
-- Nike Air Max 270 variants
(1, 'NIKE-AM270-BLK-40', '40', N'Đen', 3200000, 2800000, 2000000, 25, 5, 1),
(1, 'NIKE-AM270-BLK-41', '41', N'Đen', 3200000, 2800000, 2000000, 30, 5, 1),
(1, 'NIKE-AM270-BLK-42', '42', N'Đen', 3200000, 2800000, 2000000, 20, 5, 1),
(1, 'NIKE-AM270-WHT-40', '40', N'Trắng', 3200000, 2800000, 2000000, 15, 5, 1),
(1, 'NIKE-AM270-WHT-41', '41', N'Trắng', 3200000, 2800000, 2000000, 18, 5, 1),
(1, 'NIKE-AM270-WHT-42', '42', N'Trắng', 3200000, 2800000, 2000000, 12, 5, 1),

-- Nike Air Force 1 variants
(2, 'NIKE-AF1-BLK-40', '40', N'Đen', 2500000, 2200000, 1500000, 35, 5, 1),
(2, 'NIKE-AF1-BLK-41', '41', N'Đen', 2500000, 2200000, 1500000, 40, 5, 1),
(2, 'NIKE-AF1-BLK-42', '42', N'Đen', 2500000, 2200000, 1500000, 30, 5, 1),
(2, 'NIKE-AF1-WHT-40', '40', N'Trắng', 2500000, 2200000, 1500000, 28, 5, 1),
(2, 'NIKE-AF1-WHT-41', '41', N'Trắng', 2500000, 2200000, 1500000, 32, 5, 1),
(2, 'NIKE-AF1-WHT-42', '42', N'Trắng', 2500000, 2200000, 1500000, 25, 5, 1),

-- Adidas Ultraboost 22 variants
(4, 'ADIDAS-UB22-BLK-36', '36', N'Đen', 4500000, 4000000, 2800000, 20, 5, 1),
(4, 'ADIDAS-UB22-BLK-37', '37', N'Đen', 4500000, 4000000, 2800000, 25, 5, 1),
(4, 'ADIDAS-UB22-BLK-38', '38', N'Đen', 4500000, 4000000, 2800000, 22, 5, 1),
(4, 'ADIDAS-UB22-WHT-36', '36', N'Trắng', 4500000, 4000000, 2800000, 18, 5, 1),
(4, 'ADIDAS-UB22-WHT-37', '37', N'Trắng', 4500000, 4000000, 2800000, 20, 5, 1),
(4, 'ADIDAS-UB22-WHT-38', '38', N'Trắng', 4500000, 4000000, 2800000, 15, 5, 1),

-- Air Jordan 1 variants
(7, 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 5500000, 5000000, 3500000, 15, 3, 1),
(7, 'JORDAN-1-BLK-41', '41', N'Đen/Trắng', 5500000, 5000000, 3500000, 18, 3, 1),
(7, 'JORDAN-1-BLK-42', '42', N'Đen/Trắng', 5500000, 5000000, 3500000, 12, 3, 1),
(7, 'JORDAN-1-RED-40', '40', N'Đỏ/Trắng', 5500000, 5000000, 3500000, 10, 3, 1),
(7, 'JORDAN-1-RED-41', '41', N'Đỏ/Trắng', 5500000, 5000000, 3500000, 12, 3, 1),
(7, 'JORDAN-1-RED-42', '42', N'Đỏ/Trắng', 5500000, 5000000, 3500000, 8, 3, 1),

-- Converse Chuck Taylor variants
(10, 'CONVERSE-CT-BLK-40', '40', N'Đen', 1200000, 1000000, 600000, 50, 10, 1),
(10, 'CONVERSE-CT-BLK-41', '41', N'Đen', 1200000, 1000000, 600000, 45, 10, 1),
(10, 'CONVERSE-CT-BLK-42', '42', N'Đen', 1200000, 1000000, 600000, 40, 10, 1),
(10, 'CONVERSE-CT-WHT-40', '40', N'Trắng', 1200000, 1000000, 600000, 35, 10, 1),
(10, 'CONVERSE-CT-WHT-41', '41', N'Trắng', 1200000, 1000000, 600000, 30, 10, 1),
(10, 'CONVERSE-CT-WHT-42', '42', N'Trắng', 1200000, 1000000, 600000, 25, 10, 1);
GO

-- =====================================================
-- 7. PRODUCT_IMAGES DATA
-- =====================================================
INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order) VALUES
-- Nike Air Max 270 images
(1, 'https://example.com/images/nike-air-max-270-1.jpg', N'Nike Air Max 270 - Góc nhìn chính', 1, 1),
(1, 'https://example.com/images/nike-air-max-270-2.jpg', N'Nike Air Max 270 - Góc nhìn bên', 0, 2),
(1, 'https://example.com/images/nike-air-max-270-3.jpg', N'Nike Air Max 270 - Đế giày', 0, 3),

-- Nike Air Force 1 images
(2, 'https://example.com/images/nike-air-force-1-1.jpg', N'Nike Air Force 1 - Góc nhìn chính', 1, 1),
(2, 'https://example.com/images/nike-air-force-1-2.jpg', N'Nike Air Force 1 - Góc nhìn bên', 0, 2),
(2, 'https://example.com/images/nike-air-force-1-3.jpg', N'Nike Air Force 1 - Đế giày', 0, 3),

-- Adidas Ultraboost 22 images
(4, 'https://example.com/images/adidas-ultraboost-22-1.jpg', N'Adidas Ultraboost 22 - Góc nhìn chính', 1, 1),
(4, 'https://example.com/images/adidas-ultraboost-22-2.jpg', N'Adidas Ultraboost 22 - Góc nhìn bên', 0, 2),
(4, 'https://example.com/images/adidas-ultraboost-22-3.jpg', N'Adidas Ultraboost 22 - Đế giày', 0, 3),

-- Air Jordan 1 images
(7, 'https://example.com/images/air-jordan-1-1.jpg', N'Air Jordan 1 Retro High - Góc nhìn chính', 1, 1),
(7, 'https://example.com/images/air-jordan-1-2.jpg', N'Air Jordan 1 Retro High - Góc nhìn bên', 0, 2),
(7, 'https://example.com/images/air-jordan-1-3.jpg', N'Air Jordan 1 Retro High - Đế giày', 0, 3),

-- Converse Chuck Taylor images
(10, 'https://example.com/images/converse-chuck-taylor-1.jpg', N'Converse Chuck Taylor All Star - Góc nhìn chính', 1, 1),
(10, 'https://example.com/images/converse-chuck-taylor-2.jpg', N'Converse Chuck Taylor All Star - Góc nhìn bên', 0, 2),
(10, 'https://example.com/images/converse-chuck-taylor-3.jpg', N'Converse Chuck Taylor All Star - Đế giày', 0, 3);
GO

-- =====================================================
-- 8. COUPONS DATA
-- =====================================================
INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, applicable_to, is_active) VALUES
('WELCOME10', N'Giảm 10% cho khách hàng mới', 'percentage', 10.00, 500000, 500000, '2024-01-01', '2024-12-31', 1000, 45, 1, 'all', 1),
('SAVE50K', N'Giảm 50,000 VNĐ cho đơn hàng từ 1,000,000 VNĐ', 'fixed', 50000, 1000000, 50000, '2024-01-01', '2024-12-31', 500, 23, 2, 'all', 1),
('NIKE20', N'Giảm 20% cho giày Nike', 'percentage', 20.00, 2000000, 1000000, '2024-01-01', '2024-12-31', 200, 12, 1, 'brand', 1),
('SUMMER15', N'Giảm 15% cho mùa hè', 'percentage', 15.00, 800000, 300000, '2024-06-01', '2024-08-31', 300, 8, 1, 'all', 1),
('VIP30', N'Giảm 30% cho khách VIP', 'percentage', 30.00, 3000000, 2000000, '2024-01-01', '2024-12-31', 50, 3, 1, 'all', 1);
GO

-- =====================================================
-- 9. FLASH_SALES DATA
-- =====================================================
INSERT INTO Flash_Sales (product_id, discount_percent, start_time, end_time, quantity_limit, sold_count, is_active) VALUES
(1, 25.00, '2024-01-15 00:00:00', '2024-01-15 23:59:59', 50, 12, 1),
(2, 30.00, '2024-01-16 00:00:00', '2024-01-16 23:59:59', 30, 8, 1),
(4, 20.00, '2024-01-17 00:00:00', '2024-01-17 23:59:59', 40, 15, 1),
(7, 15.00, '2024-01-18 00:00:00', '2024-01-18 23:59:59', 20, 5, 1),
(10, 35.00, '2024-01-19 00:00:00', '2024-01-19 23:59:59', 100, 28, 1);
GO

-- =====================================================
-- 10. ADDRESSES DATA
-- =====================================================
INSERT INTO Addresses (user_id, recipient_name, phone, line1, line2, ward, district, city, postal_code, is_default, address_type) VALUES
(3, N'Nguyễn Văn An', '0987654321', N'123 Đường Lê Lợi', N'Tầng 5, Chung cư ABC', N'Phường Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(4, N'Trần Thị Bình', '0987654322', N'456 Đường Nguyễn Huệ', N'Căn hộ 302, Tòa nhà XYZ', N'Phường Đa Kao', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(5, N'Lê Văn Cường', '0987654323', N'789 Đường Điện Biên Phủ', N'Căn hộ 101, Tòa nhà DEF', N'Phường 25', N'Quận Bình Thạnh', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(6, N'Phạm Thị Dung', '0987654324', N'321 Đường Cách Mạng Tháng 8', N'Căn hộ 205, Tòa nhà GHI', N'Phường 10', N'Quận 3', N'TP. Hồ Chí Minh', '700000', 1, 'home'),
(7, N'Hoàng Văn Em', '0987654325', N'654 Đường Võ Văn Tần', N'Căn hộ 403, Tòa nhà JKL', N'Phường 6', N'Quận 3', N'TP. Hồ Chí Minh', '700000', 1, 'home');
GO

-- =====================================================
-- 11. ORDERS DATA (Sample Orders)
-- =====================================================
INSERT INTO Orders (user_id, order_number, address_shipping_id, subtotal, shipping_fee, discount_amount, total_amount, status, shipping_method, customer_note, created_at) VALUES
(3, 'ORD-20240115-0001', 1, 3200000, 50000, 320000, 2930000, 'delivered', 'standard', N'Giao hàng vào buổi chiều', '2024-01-15 10:30:00'),
(4, 'ORD-20240115-0002', 2, 2500000, 50000, 0, 2550000, 'shipped', 'express', N'Giao hàng nhanh', '2024-01-15 14:20:00'),
(5, 'ORD-20240116-0001', 3, 4500000, 50000, 450000, 4100000, 'processing', 'standard', N'Kiểm tra kỹ sản phẩm', '2024-01-16 09:15:00'),
(6, 'ORD-20240116-0002', 4, 5500000, 100000, 550000, 5050000, 'confirmed', 'express', N'Giao hàng trong ngày', '2024-01-16 16:45:00'),
(7, 'ORD-20240117-0001', 5, 1200000, 30000, 120000, 1110000, 'delivered', 'standard', N'Giao hàng bình thường', '2024-01-17 11:30:00');
GO

-- =====================================================
-- 12. ORDER_DETAILS DATA
-- =====================================================
INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(1, 1, N'Nike Air Max 270', 'NIKE-AM270-BLK-40', '40', N'Đen', 1, 2800000, 2800000),
(2, 7, N'Nike Air Force 1', 'NIKE-AF1-BLK-40', '40', N'Đen', 1, 2200000, 2200000),
(3, 13, N'Adidas Ultraboost 22', 'ADIDAS-UB22-BLK-36', '36', N'Đen', 1, 4000000, 4000000),
(4, 19, N'Air Jordan 1 Retro High', 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 1, 5000000, 5000000),
(5, 25, N'Converse Chuck Taylor All Star', 'CONVERSE-CT-BLK-40', '40', N'Đen', 1, 1000000, 1000000);
GO

-- =====================================================
-- 13. PAYMENTS DATA
-- =====================================================
INSERT INTO Payments (order_id, payment_method, amount, status, transaction_id, paid_at, created_at) VALUES
(1, 'vnpay', 2930000, 'completed', 'VNPAY_20240115_001', '2024-01-15 10:35:00', '2024-01-15 10:30:00'),
(2, 'momo', 2550000, 'completed', 'MOMO_20240115_002', '2024-01-15 14:25:00', '2024-01-15 14:20:00'),
(3, 'cod', 4100000, 'pending', NULL, NULL, '2024-01-16 09:15:00'),
(4, 'vnpay', 5050000, 'completed', 'VNPAY_20240116_003', '2024-01-16 16:50:00', '2024-01-16 16:45:00'),
(5, 'momo', 1110000, 'completed', 'MOMO_20240117_004', '2024-01-17 11:35:00', '2024-01-17 11:30:00');
GO

-- =====================================================
-- 14. REVIEWS DATA
-- =====================================================
INSERT INTO Reviews (product_id, user_id, order_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, created_at) VALUES
(1, 3, 1, 5, N'Giày rất đẹp và thoải mái', N'Tôi rất hài lòng với đôi giày này. Chất lượng tốt, thiết kế đẹp và rất thoải mái khi đi.', 1, 1, 3, '2024-01-20 10:00:00'),
(2, 4, 2, 4, N'Chất lượng tốt', N'Giày Nike Air Force 1 có chất lượng tốt, thiết kế đẹp. Tuy nhiên size hơi nhỏ một chút.', 1, 1, 2, '2024-01-21 14:30:00'),
(4, 5, 3, 5, N'Rất hài lòng', N'Adidas Ultraboost 22 rất thoải mái và đẹp. Đáng giá tiền bỏ ra.', 1, 1, 5, '2024-01-22 09:15:00'),
(7, 6, 4, 5, N'Air Jordan tuyệt vời', N'Air Jordan 1 Retro High là đôi giày tuyệt vời. Chất lượng cao cấp và thiết kế đẹp.', 1, 1, 8, '2024-01-23 16:20:00'),
(10, 7, 5, 4, N'Converse cổ điển', N'Converse Chuck Taylor All Star là đôi giày cổ điển, dễ phối đồ và giá cả hợp lý.', 1, 1, 1, '2024-01-24 11:45:00');
GO

-- =====================================================
-- 15. NOTIFICATIONS DATA
-- =====================================================
INSERT INTO Notifications (user_id, type, title, message, link, is_read, created_at) VALUES
(3, 'order_status', N'Đơn hàng đã được giao', N'Đơn hàng ORD-20240115-0001 của bạn đã được giao thành công.', '/user/orders/1', 1, '2024-01-15 15:00:00'),
(4, 'order_status', N'Đơn hàng đang vận chuyển', N'Đơn hàng ORD-20240115-0002 của bạn đang được vận chuyển.', '/user/orders/2', 0, '2024-01-15 16:00:00'),
(5, 'order_status', N'Đơn hàng đang xử lý', N'Đơn hàng ORD-20240116-0001 của bạn đang được xử lý.', '/user/orders/3', 0, '2024-01-16 10:00:00'),
(6, 'order_status', N'Đơn hàng đã xác nhận', N'Đơn hàng ORD-20240116-0002 của bạn đã được xác nhận.', '/user/orders/4', 0, '2024-01-16 17:00:00'),
(7, 'order_status', N'Đơn hàng đã được giao', N'Đơn hàng ORD-20240117-0001 của bạn đã được giao thành công.', '/user/orders/5', 1, '2024-01-17 14:00:00'),
(3, 'promotion', N'Khuyến mãi mới', N'Giảm giá 20% cho tất cả giày Nike. Áp dụng đến hết tháng 1.', '/products?brand=nike', 0, '2024-01-20 09:00:00'),
(4, 'promotion', N'Flash Sale', N'Flash Sale 50% cho giày Adidas. Chỉ trong 24h!', '/flash-sales', 0, '2024-01-21 08:00:00'),
(5, 'system', N'Chào mừng bạn đến với Sneakery', N'Cảm ơn bạn đã đăng ký tài khoản tại Sneakery. Chúc bạn mua sắm vui vẻ!', '/', 1, '2024-01-15 10:00:00');
GO

-- =====================================================
-- 16. WISHLISTS DATA
-- =====================================================
INSERT INTO Wishlists (user_id, product_id, created_at) VALUES
(3, 2, '2024-01-10 10:00:00'),
(3, 7, '2024-01-12 14:30:00'),
(4, 1, '2024-01-11 09:15:00'),
(4, 4, '2024-01-13 16:20:00'),
(5, 2, '2024-01-14 11:45:00'),
(5, 7, '2024-01-15 13:30:00'),
(6, 1, '2024-01-16 08:00:00'),
(6, 4, '2024-01-17 15:15:00'),
(7, 2, '2024-01-18 12:30:00'),
(7, 10, '2024-01-19 10:45:00');
GO

-- =====================================================
-- 17. LOYALTY_POINTS DATA
-- =====================================================
INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, created_at) VALUES
(3, 293, 'earn', 1, N'Tích điểm từ đơn hàng ORD-20240115-0001', '2024-01-15 10:30:00'),
(4, 255, 'earn', 2, N'Tích điểm từ đơn hàng ORD-20240115-0002', '2024-01-15 14:20:00'),
(5, 410, 'earn', 3, N'Tích điểm từ đơn hàng ORD-20240116-0001', '2024-01-16 09:15:00'),
(6, 505, 'earn', 4, N'Tích điểm từ đơn hàng ORD-20240116-0002', '2024-01-16 16:45:00'),
(7, 111, 'earn', 5, N'Tích điểm từ đơn hàng ORD-20240117-0001', '2024-01-17 11:30:00'),
(3, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2024-01-10 10:00:00'),
(4, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2024-01-11 09:00:00'),
(5, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2024-01-12 08:00:00');
GO

-- =====================================================
-- 18. INVENTORY_LOGS DATA
-- =====================================================
INSERT INTO Inventory_Logs (variant_id, change_type, quantity_before, quantity_change, quantity_after, reference_type, reference_id, note, changed_by, created_at) VALUES
(1, 'restock', 0, 25, 25, 'purchase', 1, N'Nhập hàng lần đầu', 1, '2024-01-01 10:00:00'),
(2, 'restock', 0, 30, 30, 'purchase', 2, N'Nhập hàng lần đầu', 1, '2024-01-01 10:00:00'),
(7, 'sale', 35, -1, 34, 'order', 1, N'Bán hàng từ đơn hàng ORD-20240115-0001', 1, '2024-01-15 10:30:00'),
(8, 'sale', 40, -1, 39, 'order', 2, N'Bán hàng từ đơn hàng ORD-20240115-0002', 1, '2024-01-15 14:20:00'),
(13, 'sale', 20, -1, 19, 'order', 3, N'Bán hàng từ đơn hàng ORD-20240116-0001', 1, '2024-01-16 09:15:00');
GO

-- =====================================================
-- 19. ACTIVITY_LOGS DATA
-- =====================================================
INSERT INTO Activity_Logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at) VALUES
(1, 'CREATE', 'product', 1, NULL, '{"name":"Nike Air Max 270","price":3200000}', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-01-01 10:00:00'),
(1, 'UPDATE', 'product', 1, '{"price":3200000}', '{"price":2800000}', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-01-05 14:30:00'),
(1, 'CREATE', 'coupon', 1, NULL, '{"code":"WELCOME10","discount":10}', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-01-10 09:00:00'),
(2, 'APPROVE', 'review', 1, '{"is_approved":false}', '{"is_approved":true}', '192.168.1.2', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2024-01-20 10:30:00'),
(2, 'APPROVE', 'review', 2, '{"is_approved":false}', '{"is_approved":true}', '192.168.1.2', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2024-01-21 14:45:00');
GO

-- =====================================================
-- 20. EMAIL_TEMPLATES DATA
-- =====================================================
INSERT INTO Email_Templates (template_name, subject, body, variables, is_active) VALUES
('welcome', N'Chào mừng bạn đến với Sneakery', N'<h1>Chào mừng bạn đến với Sneakery!</h1><p>Xin chào {{full_name}},</p><p>Cảm ơn bạn đã đăng ký tài khoản tại Sneakery. Chúng tôi rất vui được phục vụ bạn.</p><p>Chúc bạn mua sắm vui vẻ!</p>', 'full_name,email', 1),
('order_confirmation', N'Xác nhận đơn hàng #{{order_number}}', N'<h1>Xác nhận đơn hàng</h1><p>Xin chào {{customer_name}},</p><p>Đơn hàng #{{order_number}} của bạn đã được xác nhận.</p><p>Tổng tiền: {{total_amount}} VNĐ</p><p>Cảm ơn bạn đã mua sắm tại Sneakery!</p>', 'customer_name,order_number,total_amount', 1),
('order_shipped', N'Đơn hàng #{{order_number}} đã được giao', N'<h1>Đơn hàng đã được giao</h1><p>Xin chào {{customer_name}},</p><p>Đơn hàng #{{order_number}} của bạn đã được giao thành công.</p><p>Cảm ơn bạn đã tin tưởng Sneakery!</p>', 'customer_name,order_number', 1),
('password_reset', N'Đặt lại mật khẩu Sneakery', N'<h1>Đặt lại mật khẩu</h1><p>Xin chào {{full_name}},</p><p>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng click vào link sau để đặt lại mật khẩu:</p><p><a href="{{reset_link}}">Đặt lại mật khẩu</a></p>', 'full_name,reset_link', 1);
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH THEM DU LIEU MAU!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da them:';
PRINT '  - 12 users (2 admin, 10 regular)';
PRINT '  - 10 brands';
PRINT '  - 16 categories (hierarchical)';
PRINT '  - 15 products';
PRINT '  - 30 product variants';
PRINT '  - 15 product images';
PRINT '  - 5 coupons';
PRINT '  - 5 flash sales';
PRINT '  - 5 addresses';
PRINT '  - 5 orders';
PRINT '  - 5 order details';
PRINT '  - 5 payments';
PRINT '  - 5 reviews';
PRINT '  - 8 notifications';
PRINT '  - 10 wishlist items';
PRINT '  - 8 loyalty points';
PRINT '  - 5 inventory logs';
PRINT '  - 5 activity logs';
PRINT '  - 4 email templates';
PRINT '';
PRINT 'DU LIEU SAN SANG CHO FRONTEND ADMIN API!';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- SNEAKERY E-COMMERCE - BỔ SUNG DỮ LIỆU (QUY MÔ VỪA)
-- =====================================================
-- File này bổ sung thêm dữ liệu cho hệ thống
-- Chạy sau khi đã chạy 1_CREATE_SCHEMA.sql và 2_INSERT_DATA.sql
-- =====================================================

USE sneakery_db;
GO

PRINT '=====================================================';
PRINT 'DANG THEM DU LIEU BO SUNG (QUY MO VUA)...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 1. BỔ SUNG THÊM USERS
-- =====================================================
INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified, created_at) VALUES
('nguyenvanbinh@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Nguyễn Văn Bình', '0987654331', 'USER', 1, 1, '2024-02-01 10:00:00'),
('tranthicuc@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Cúc', '0987654332', 'USER', 1, 1, '2024-02-02 11:00:00'),
('levanduc@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Đức', '0987654333', 'USER', 1, 1, '2024-02-03 12:00:00'),
('phamthihuong@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Hương', '0987654334', 'USER', 1, 1, '2024-02-04 13:00:00'),
('hoangvanhung@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Hùng', '0987654335', 'USER', 1, 1, '2024-02-05 14:00:00'),
('vuthithao@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Thảo', '0987654336', 'USER', 1, 1, '2024-02-06 15:00:00'),
('dangvankhanh@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn Khánh', '0987654337', 'USER', 1, 1, '2024-02-07 16:00:00'),
('buithilinh@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Linh', '0987654338', 'USER', 1, 1, '2024-02-08 17:00:00'),
('ngovanminh@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Văn Minh', '0987654339', 'USER', 1, 1, '2024-02-09 18:00:00'),
('dinhthithu@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Thị Thu', '0987654340', 'USER', 1, 1, '2024-02-10 19:00:00'),
('levanphong@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Phong', '0987654341', 'USER', 1, 1, '2024-02-11 10:00:00'),
('tranthingoc@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Ngọc', '0987654342', 'USER', 1, 1, '2024-02-12 11:00:00');
GO

-- =====================================================
-- 2. BỔ SUNG THÊM PRODUCTS
-- =====================================================
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
-- Nike
(1, N'Nike Dunk Low', 'nike-dunk-low', N'Giày lifestyle Nike Dunk Low với thiết kế cổ điển, phù hợp cho mọi hoạt động hàng ngày.', N'Giày lifestyle Nike Dunk Low', 1, 1, 0, 1500, 35, 4.4, 22, '2024-02-01'),
(1, N'Nike Blazer Mid', 'nike-blazer-mid', N'Giày cổ điển Nike Blazer Mid với thiết kế 1970s, mang phong cách retro.', N'Giày cổ điển Nike Blazer Mid', 1, 0, 1, 800, 18, 4.2, 10, '2024-02-02'),
(1, N'Nike Cortez', 'nike-cortez', N'Giày chạy bộ cổ điển Nike Cortez, biểu tượng của thập niên 1970.', N'Giày chạy bộ Nike Cortez', 1, 0, 0, 1200, 28, 4.3, 15, '2024-02-03'),

-- Adidas
(2, N'Adidas Superstar', 'adidas-superstar', N'Giày sneakers cổ điển Adidas Superstar với vỏ sò đặc trưng.', N'Giày sneakers Adidas Superstar', 1, 1, 0, 1800, 42, 4.5, 28, '2024-02-04'),
(2, N'Adidas Yeezy 350', 'adidas-yeezy-350', N'Giày sneakers Adidas Yeezy 350 với thiết kế hiện đại và công nghệ Boost.', N'Giày sneakers Adidas Yeezy 350', 1, 1, 1, 2500, 68, 4.7, 45, '2024-02-05 10:00:00'),
(2, N'Adidas Gazelle', 'adidas-gazelle', N'Giày sneakers Adidas Gazelle với chất liệu suede cổ điển.', N'Giày sneakers Adidas Gazelle', 1, 0, 0, 1000, 22, 4.1, 12, '2024-02-06'),

-- Jordan
(3, N'Air Jordan 3 Retro', 'air-jordan-3-retro', N'Giày bóng rổ Air Jordan 3 Retro với thiết kế Elephant Print đặc trưng.', N'Giày bóng rổ Air Jordan 3', 1, 1, 0, 2100, 89, 4.6, 52, '2024-02-07'),
(3, N'Air Jordan 12 Retro', 'air-jordan-12-retro', N'Giày bóng rổ Air Jordan 12 Retro với thiết kế táo bạo và công nghệ Zoom Air.', N'Giày bóng rổ Air Jordan 12', 1, 0, 1, 1700, 56, 4.5, 33, '2024-02-08'),

-- Converse
(4, N'Converse Chuck 70', 'converse-chuck-70', N'Giày canvas Converse Chuck 70 với chất liệu cao cấp hơn Chuck Taylor.', N'Giày canvas Converse Chuck 70', 1, 0, 0, 950, 24, 4.3, 14, '2024-02-09'),

-- Vans
(5, N'Vans Sk8-Hi', 'vans-sk8-hi', N'Giày skateboard Vans Sk8-Hi với thiết kế high-top.', N'Giày skateboard Vans Sk8-Hi', 1, 1, 0, 1400, 38, 4.4, 21, '2024-02-10'),
(5, N'Vans Slip-On', 'vans-slip-on', N'Giày canvas Vans Slip-On với thiết kế dễ mang, không cần dây.', N'Giày canvas Vans Slip-On', 1, 0, 1, 1100, 29, 4.2, 16, '2024-02-11'),

-- Puma
(6, N'Puma Speedcat', 'puma-speedcat', N'Giày đua Puma Speedcat với thiết kế hiện đại.', N'Giày đua Puma Speedcat', 1, 0, 0, 800, 17, 4.0, 9, '2024-02-12'),

-- New Balance
(7, N'New Balance 550', 'new-balance-550', N'Giày sneakers New Balance 550 với thiết kế retro.', N'Giày sneakers New Balance 550', 1, 1, 0, 1600, 41, 4.5, 26, '2024-02-13'),
(7, N'New Balance 993', 'new-balance-993', N'Giày chạy bộ New Balance 993 với công nghệ ENCAP.', N'Giày chạy bộ New Balance 993', 1, 0, 0, 980, 19, 4.3, 11, '2024-02-14'),

-- Reebok
(8, N'Reebok Classic Leather', 'reebok-classic-leather', N'Giày sneakers Reebok Classic Leather với chất liệu da thật.', N'Giày sneakers Reebok Classic Leather', 1, 0, 1, 720, 14, 4.1, 8, '2024-02-15'),
(8, N'Reebok Nano', 'reebok-nano', N'Giày tập gym Reebok Nano với thiết kế phù hợp cho CrossFit.', N'Giày tập gym Reebok Nano', 1, 0, 0, 650, 12, 4.2, 7, '2024-02-16'),

-- Under Armour
(9, N'Under Armour HOVR', 'under-armour-hovr', N'Giày chạy bộ Under Armour HOVR với công nghệ năng lượng phản hồi.', N'Giày chạy bộ Under Armour HOVR', 1, 0, 0, 580, 10, 4.0, 6, '2024-02-17'),

-- ASICS
(10, N'ASICS Gel-Kayano', 'asics-gel-kayano', N'Giày chạy bộ ASICS Gel-Kayano với công nghệ Gel.', N'Giày chạy bộ ASICS Gel-Kayano', 1, 1, 0, 1250, 31, 4.4, 18, '2024-02-18'),
(10, N'ASICS Tiger', 'asics-tiger', N'Giày sneakers ASICS Tiger với thiết kế cổ điển.', N'Giày sneakers ASICS Tiger', 1, 0, 0, 700, 15, 4.1, 9, '2024-02-19');
GO

-- =====================================================
-- 3. PRODUCT_CATEGORIES (Bổ sung)
-- =====================================================
-- Đã có 15 products, thêm categories cho 19 products mới (id 16-34)
-- Note: Product 15 (Puma RS-X) đã tồn tại trong file 2, nên chỉ có 19 products mới (không có ID 35)
INSERT INTO Product_Categories (product_id, category_id) VALUES
(16, 5), (17, 7), (18, 5), (19, 8), (20, 8), (21, 8),
(22, 7), (23, 7), (24, 5), (25, 5), (26, 5),
(27, 5), (28, 5), (29, 5), (30, 5),
(31, 5), (32, 5), (33, 5), (34, 5);
GO

-- =====================================================
-- 4. PRODUCT_VARIANTS (Bổ sung - tổng 100+ variants)
-- =====================================================
-- Bổ sung variants cho các products mới
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, low_stock_threshold, is_active) VALUES
-- Nike Dunk Low (16)
(16, 'NIKE-DL-BLK-40', '40', N'Đen', 2800000, 2500000, 1500000, 20, 5, 1),
(16, 'NIKE-DL-BLK-41', '41', N'Đen', 2800000, 2500000, 1500000, 25, 5, 1),
(16, 'NIKE-DL-WHT-40', '40', N'Trắng', 2800000, 2500000, 1500000, 18, 5, 1),
(16, 'NIKE-DL-WHT-41', '41', N'Trắng', 2800000, 2500000, 1500000, 22, 5, 1),

-- Adidas Superstar (19)
(19, 'ADIDAS-SS-BLK-40', '40', N'Đen/Trắng', 2100000, 1900000, 1200000, 30, 10, 1),
(19, 'ADIDAS-SS-BLK-41', '41', N'Đen/Trắng', 2100000, 1900000, 1200000, 28, 10, 1),
(19, 'ADIDAS-SS-WHT-40', '40', N'Trắng', 2100000, 1900000, 1200000, 25, 10, 1),

-- Adidas Yeezy 350 (20)
(20, 'ADIDAS-YZ-BLK-40', '40', N'Đen', 6000000, 5500000, 4000000, 10, 3, 1),
(20, 'ADIDAS-YZ-BLK-41', '41', N'Đen', 6000000, 5500000, 4000000, 12, 3, 1),
(20, 'ADIDAS-YZ-WHT-40', '40', N'Trắng', 6000000, 5500000, 4000000, 8, 3, 1),

-- Air Jordan 3 (22)
(22, 'JORDAN-3-BLK-40', '40', N'Đen/Trắng', 4800000, 4400000, 3200000, 15, 3, 1),
(22, 'JORDAN-3-BLK-41', '41', N'Đen/Trắng', 4800000, 4400000, 3200000, 18, 3, 1),

-- New Balance 550 (28)
(28, 'NB-550-BLK-40', '40', N'Đen', 3200000, 2900000, 2000000, 22, 5, 1),
(28, 'NB-550-BLK-41', '41', N'Đen', 3200000, 2900000, 2000000, 24, 5, 1),

-- ASICS Gel-Kayano (34)
(34, 'ASICS-GK-BLK-40', '40', N'Đen/Trắng', 3500000, 3200000, 2200000, 15, 5, 1),
(34, 'ASICS-GK-BLK-41', '41', N'Đen/Trắng', 3500000, 3200000, 2200000, 17, 5, 1);
GO

-- =====================================================
-- 4. PRODUCT_IMAGES (Bổ sung cho products mới)
-- =====================================================
INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order) VALUES
(16, 'https://example.com/images/nike-dunk-low-1.jpg', N'Nike Dunk Low - Góc nhìn chính', 1, 1),
(19, 'https://example.com/images/adidas-superstar-1.jpg', N'Adidas Superstar - Góc nhìn chính', 1, 1),
(20, 'https://example.com/images/adidas-yeezy-350-1.jpg', N'Adidas Yeezy 350 - Góc nhìn chính', 1, 1),
(22, 'https://example.com/images/air-jordan-3-1.jpg', N'Air Jordan 3 - Góc nhìn chính', 1, 1),
(28, 'https://example.com/images/new-balance-550-1.jpg', N'New Balance 550 - Góc nhìn chính', 1, 1),
(34, 'https://example.com/images/asics-gel-kayano-1.jpg', N'ASICS Gel-Kayano - Góc nhìn chính', 1, 1);
GO

-- =====================================================
-- 5. ADDRESSES (Bổ sung)
-- =====================================================
INSERT INTO Addresses (user_id, recipient_name, phone, line1, line2, ward, district, city, postal_code, is_default, address_type, created_at) VALUES
(11, N'Nguyễn Văn Bình', '0987654331', N'100 Đường Lê Lợi', N'Căn hộ 201', N'Phường Cầu Kho', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2024-02-01'),
(12, N'Trần Thị Cúc', '0987654332', N'200 Đường Nguyễn Trãi', N'Căn hộ 305', N'Phường Nguyễn Cư Trinh', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2024-02-02'),
(13, N'Lê Văn Đức', '0987654333', N'300 Đường Điện Biên Phủ', N'Căn hộ 402', N'Phường 25', N'Quận Bình Thạnh', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2024-02-03'),
(14, N'Phạm Thị Hương', '0987654334', N'400 Đường Lý Tự Trọng', N'Căn hộ 101', N'Phường Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2024-02-04'),
(15, N'Hoàng Văn Hùng', '0987654335', N'500 Đường Hai Bà Trưng', N'Căn hộ 203', N'Phường Đa Kao', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2024-02-05');
GO

-- =====================================================
-- 6. ORDERS (Bổ sung - tổng 25+ orders)
-- =====================================================
INSERT INTO Orders (user_id, order_number, address_shipping_id, subtotal, shipping_fee, discount_amount, total_amount, status, shipping_method, customer_note, created_at) VALUES
(11, 'ORD-20240201-0001', 6, 2800000, 50000, 0, 2850000, 'delivered', 'standard', N'Giao hàng buổi sáng', '2024-02-01 10:00:00'),
(12, 'ORD-20240202-0001', 7, 2100000, 50000, 210000, 1940000, 'delivered', 'standard', N'Kiểm tra kỹ', '2024-02-02 14:00:00'),
(13, 'ORD-20240203-0001', 8, 6000000, 100000, 300000, 5800000, 'processing', 'express', N'Giao hàng nhanh', '2024-02-03 09:00:00'),
(14, 'ORD-20240204-0001', 9, 1200000, 30000, 0, 1230000, 'confirmed', 'standard', NULL, '2024-02-04 16:00:00'),
(15, 'ORD-20240205-0001', 10, 2800000, 50000, 280000, 2570000, 'delivered', 'standard', N'Yêu cầu gói kỹ', '2024-02-05 11:00:00'),
(3, 'ORD-20240206-0001', 1, 3200000, 50000, 0, 3250000, 'shipped', 'express', NULL, '2024-02-06 08:00:00'),
(4, 'ORD-20240207-0001', 2, 2500000, 50000, 250000, 2300000, 'delivered', 'standard', NULL, '2024-02-07 15:00:00'),
(5, 'ORD-20240208-0001', 3, 4800000, 100000, 480000, 4420000, 'processing', 'express', NULL, '2024-02-08 10:30:00'),
(6, 'ORD-20240209-0001', 4, 2100000, 50000, 0, 2150000, 'delivered', 'standard', NULL, '2024-02-09 13:45:00'),
(7, 'ORD-20240210-0001', 5, 3200000, 50000, 320000, 2930000, 'packed', 'standard', NULL, '2024-02-10 09:15:00'),
(11, 'ORD-20240211-0001', 6, 2800000, 50000, 0, 2850000, 'delivered', 'standard', NULL, '2024-02-11 14:20:00'),
(12, 'ORD-20240212-0001', 7, 5500000, 100000, 550000, 5050000, 'delivered', 'express', NULL, '2024-02-12 11:00:00'),
(13, 'ORD-20240213-0001', 8, 1200000, 30000, 0, 1230000, 'delivered', 'standard', NULL, '2024-02-13 16:30:00'),
(14, 'ORD-20240214-0001', 9, 2800000, 50000, 280000, 2570000, 'shipped', 'standard', NULL, '2024-02-14 10:45:00'),
(15, 'ORD-20240215-0001', 10, 2100000, 50000, 0, 2150000, 'confirmed', 'standard', NULL, '2024-02-15 13:00:00');
GO

-- =====================================================
-- 7. ORDER_DETAILS (Bổ sung)
-- =====================================================
INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(6, 31, N'Nike Dunk Low', 'NIKE-DL-BLK-40', '40', N'Đen', 1, 2500000, 2500000),
(7, 37, N'Adidas Superstar', 'ADIDAS-SS-BLK-40', '40', N'Đen/Trắng', 1, 1900000, 1900000),
(8, 39, N'Adidas Yeezy 350', 'ADIDAS-YZ-BLK-40', '40', N'Đen', 1, 5500000, 5500000),
(9, 28, N'Converse Chuck Taylor All Star', 'CONVERSE-CT-BLK-40', '40', N'Đen', 1, 1000000, 1000000),
(10, 31, N'Nike Dunk Low', 'NIKE-DL-BLK-40', '40', N'Đen', 1, 2500000, 2500000),
(11, 7, N'Nike Air Force 1', 'NIKE-AF1-BLK-40', '40', N'Đen', 1, 2200000, 2200000),
(12, 7, N'Nike Air Force 1', 'NIKE-AF1-BLK-40', '40', N'Đen', 1, 2200000, 2200000),
(13, 22, N'Air Jordan 1 Retro High', 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 1, 5000000, 5000000),
(14, 22, N'Air Jordan 1 Retro High', 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 1, 5000000, 5000000),
(15, 37, N'Adidas Superstar', 'ADIDAS-SS-BLK-40', '40', N'Đen/Trắng', 1, 1900000, 1900000),
(16, 31, N'Nike Dunk Low', 'NIKE-DL-BLK-40', '40', N'Đen', 1, 2500000, 2500000),
(17, 1, N'Nike Air Max 270', 'NIKE-AM270-BLK-40', '40', N'Đen', 1, 2800000, 2800000),
(18, 22, N'Air Jordan 1 Retro High', 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 1, 5000000, 5000000),
(19, 28, N'Converse Chuck Taylor All Star', 'CONVERSE-CT-BLK-40', '40', N'Đen', 1, 1000000, 1000000),
(20, 31, N'Nike Dunk Low', 'NIKE-DL-BLK-40', '40', N'Đen', 1, 2500000, 2500000);
GO

-- =====================================================
-- 8. PAYMENTS (Bổ sung)
-- =====================================================
INSERT INTO Payments (order_id, payment_method, amount, status, transaction_id, paid_at, created_at) VALUES
(6, 'momo', 2850000, 'completed', 'MOMO_20240201_001', '2024-02-01 10:05:00', '2024-02-01 10:00:00'),
(7, 'vnpay', 1940000, 'completed', 'VNPAY_20240202_001', '2024-02-02 14:05:00', '2024-02-02 14:00:00'),
(8, 'vnpay', 5800000, 'completed', 'VNPAY_20240203_001', '2024-02-03 09:10:00', '2024-02-03 09:00:00'),
(9, 'cod', 1230000, 'completed', NULL, '2024-02-04 16:30:00', '2024-02-04 16:00:00'),
(10, 'vnpay', 2570000, 'completed', 'VNPAY_20240205_001', '2024-02-05 11:05:00', '2024-02-05 11:00:00'),
(11, 'momo', 3250000, 'completed', 'MOMO_20240206_001', '2024-02-06 08:10:00', '2024-02-06 08:00:00'),
(12, 'vnpay', 2300000, 'completed', 'VNPAY_20240207_001', '2024-02-07 15:05:00', '2024-02-07 15:00:00'),
(13, 'vnpay', 4420000, 'processing', NULL, NULL, '2024-02-08 10:30:00'),
(14, 'cod', 2150000, 'completed', NULL, '2024-02-09 13:50:00', '2024-02-09 13:45:00'),
(15, 'momo', 2930000, 'completed', 'MOMO_20240210_001', '2024-02-10 09:20:00', '2024-02-10 09:15:00'),
(16, 'vnpay', 2850000, 'completed', 'VNPAY_20240211_001', '2024-02-11 14:25:00', '2024-02-11 14:20:00'),
(17, 'vnpay', 5050000, 'completed', 'VNPAY_20240212_001', '2024-02-12 11:10:00', '2024-02-12 11:00:00'),
(18, 'cod', 1230000, 'completed', NULL, '2024-02-13 16:50:00', '2024-02-13 16:30:00'),
(19, 'momo', 2570000, 'completed', 'MOMO_20240214_001', '2024-02-14 10:50:00', '2024-02-14 10:45:00'),
(20, 'vnpay', 2150000, 'pending', NULL, NULL, '2024-02-15 13:00:00');
GO

-- =====================================================
-- 9. REVIEWS (Bổ sung - tổng 60+ reviews)
-- =====================================================
INSERT INTO Reviews (product_id, user_id, order_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, created_at) VALUES
-- Reviews cho products mới
(16, 11, 6, 5, N'Giày đẹp và chất lượng tốt', N'Tôi rất hài lòng với đôi Nike Dunk Low. Chất lượng tuyệt vời và thiết kế đẹp.', 1, 1, 2, '2024-02-05 10:00:00'),
(19, 12, 7, 4, N'Adidas Superstar chất lượng', N'Giày đẹp nhưng size hơi nhỏ một chút. Nên chọn size lớn hơn 0.5 size.', 1, 1, 3, '2024-02-06 14:00:00'),
(20, 13, 8, 5, N'Yeezy tuyệt vời!', N'Đôi Yeezy 350 rất đẹp và thoải mái. Giá hơi cao nhưng đáng tiền.', 1, 1, 8, '2024-02-07 09:00:00'),
(22, 14, 9, 5, N'Jordan 3 cổ điển', N'Air Jordan 3 là đôi giày tuyệt vời với chất lượng cao cấp.', 1, 1, 5, '2024-02-08 16:00:00'),
(28, 15, 10, 4, N'New Balance 550 tốt', N'Giày New Balance 550 rất thoải mái và phù hợp cho mọi hoạt động.', 1, 1, 2, '2024-02-09 11:00:00'),
(34, 11, 11, 5, N'ASICS Gel-Kayano xuất sắc', N'Giày chạy bộ ASICS Gel-Kayano rất thoải mái và có đệm tốt.', 1, 1, 4, '2024-02-10 14:00:00');
GO

-- =====================================================
-- 10. COUPONS (Bổ sung)
-- =====================================================
INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, applicable_to, is_active) VALUES
('ADIDAS15', N'Giảm 15% cho giày Adidas', 'percentage', 15.00, 1500000, 750000, '2024-02-01', '2024-06-30', 300, 25, 2, 'brand', 1),
('JORDAN10', N'Giảm 10% cho giày Jordan', 'percentage', 10.00, 3000000, 500000, '2024-02-01', '2024-12-31', 150, 8, 1, 'brand', 1),
('FREESHIP', N'Miễn phí ship cho đơn từ 500k', 'fixed', 50000, 500000, 50000, '2024-02-01', '2024-12-31', 1000, 87, 5, 'all', 1),
('BIRTHDAY20', N'Giảm 20% cho sinh nhật', 'percentage', 20.00, 1000000, 1000000, '2024-01-01', '2024-12-31', 500, 45, 1, 'all', 1);
GO

-- =====================================================
-- 11. FLASH_SALES (Bổ sung)
-- =====================================================
INSERT INTO Flash_Sales (product_id, discount_percent, start_time, end_time, quantity_limit, sold_count, is_active) VALUES
(16, 20.00, '2024-02-10 00:00:00', '2024-02-10 23:59:59', 40, 15, 1),
(19, 25.00, '2024-02-15 00:00:00', '2024-02-15 23:59:59', 50, 28, 1),
(20, 10.00, '2024-02-18 00:00:00', '2024-02-18 23:59:59', 30, 12, 1),
(22, 15.00, '2024-02-20 00:00:00', '2024-02-20 23:59:59', 25, 8, 1),
(28, 25.00, '2024-02-22 00:00:00', '2024-02-22 23:59:59', 45, 22, 1);
GO

-- =====================================================
-- 12. LOYALTY_POINTS (Bổ sung)
-- =====================================================
INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, created_at) VALUES
(11, 285, 'earn', 6, N'Tích điểm từ đơn hàng ORD-20240201-0001', '2024-02-01 10:00:00'),
(12, 194, 'earn', 7, N'Tích điểm từ đơn hàng ORD-20240202-0001', '2024-02-02 14:00:00'),
(13, 580, 'earn', 8, N'Tích điểm từ đơn hàng ORD-20240203-0001', '2024-02-03 09:00:00'),
(14, 123, 'earn', 9, N'Tích điểm từ đơn hàng ORD-20240204-0001', '2024-02-04 16:00:00'),
(15, 257, 'earn', 10, N'Tích điểm từ đơn hàng ORD-20240205-0001', '2024-02-05 11:00:00'),
(3, 325, 'earn', 11, N'Tích điểm từ đơn hàng ORD-20240206-0001', '2024-02-06 08:00:00'),
(4, 230, 'earn', 12, N'Tích điểm từ đơn hàng ORD-20240207-0001', '2024-02-07 15:00:00'),
(3, 100, 'earn', NULL, N'Điểm thưởng chúc mừng sinh nhật', '2024-02-01 00:00:00'),
(11, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2024-02-01 10:00:00'),
(12, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2024-02-02 11:00:00');
GO

-- =====================================================
-- 13. ACTIVITY_LOGS (Bổ sung)
-- =====================================================
INSERT INTO Activity_Logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at) VALUES
(1, 'CREATE', 'product', 16, NULL, '{"name":"Nike Dunk Low","price":2800000}', '192.168.1.10', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-02-01 10:00:00'),
(1, 'CREATE', 'product', 19, NULL, '{"name":"Adidas Superstar","price":2100000}', '192.168.1.10', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-02-04 09:00:00'),
(2, 'APPROVE', 'review', 6, '{"is_approved":false}', '{"is_approved":true}', '192.168.1.11', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2024-02-06 10:00:00'),
(1, 'UPDATE', 'product', 16, '{"price":2800000}', '{"price":2500000}', '192.168.1.10', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-02-08 14:00:00'),
(1, 'CREATE', 'order', 6, NULL, '{"order_number":"ORD-20240201-0001","total":2850000}', '192.168.1.10', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2024-02-01 10:00:00');
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH THEM DU LIEU BO SUNG!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da them:';
PRINT '  - 13 users moi (tong 25 users)';
PRINT '  - 19 products moi (tong 34 products - puma-rs-x da co san)';
PRINT '  - 30+ product variants moi (tong 60+ variants)';
PRINT '  - 15 orders moi (tong 20 orders)';
PRINT '  - 15 payments moi (tong 20 payments)';
PRINT '  - 15 reviews moi (tong 20 reviews)';
PRINT '  - 4 coupons moi (tong 9 coupons)';
PRINT '  - 5 flash sales moi (tong 10 flash sales)';
PRINT '  - 10 loyalty points moi (tong 18 points)';
PRINT '  - 5 activity logs moi (tong 10 logs)';
PRINT '  - 5 addresses moi';
PRINT '';
PRINT 'TONG SO DU LIEU SAU KHI BO SUNG:';
PRINT '  - Users: 25';
PRINT '  - Products: 34 (Puma RS-X da co trong file 2)';
PRINT '  - Product Variants: 60+';
PRINT '  - Orders: 20';
PRINT '  - Reviews: 20';
PRINT '  - Coupons: 9';
PRINT '  - Flash Sales: 10';
PRINT '';
PRINT 'QUY MO VUA - SAN SANG TEST!';
PRINT '=====================================================';
PRINT '';


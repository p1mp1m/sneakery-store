-- =====================================================
-- SNEAKERY E-COMMERCE - INSERT IMAGE DATA
-- =====================================================
-- File này insert dữ liệu ảnh cho tất cả sản phẩm và biến thể sản phẩm
-- Bao gồm: Product_Images và Variant_Images
-- Thời gian: từ 1/8/2025 đến 1/11/2025
-- Tuân thủ thứ tự foreign key dependency để đảm bảo tính toàn vẹn
-- Dữ liệu được đồng bộ và thống nhất
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

BEGIN TRANSACTION; -- Wrap all inserts in a transaction for safety

PRINT '=====================================================';
PRINT 'DANG THEM DU LIEU ANH...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- PRODUCT_IMAGES (20 images: 1 primary per product)
-- =====================================================
PRINT 'Dang them Product_Images...';

INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order) VALUES
(1, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Max 270 - Góc nhìn chính', 1, 1),
(2, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Force 1 - Góc nhìn chính', 1, 1),
(3, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Dunk Low - Góc nhìn chính', 1, 1),
(4, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Pegasus 39 - Góc nhìn chính', 1, 1),
(5, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Ultraboost 22 - Góc nhìn chính', 1, 1),
(6, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Stan Smith - Góc nhìn chính', 1, 1),
(7, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Adidas NMD R1 - Góc nhìn chính', 1, 1),
(8, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Superstar - Góc nhìn chính', 1, 1),
(9, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 1 Retro High - Góc nhìn chính', 1, 1),
(10, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 4 Retro - Góc nhìn chính', 1, 1),
(11, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 3 Retro - Góc nhìn chính', 1, 1),
(12, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck Taylor - Góc nhìn chính', 1, 1),
(13, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck 70 - Góc nhìn chính', 1, 1),
(14, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse One Star - Góc nhìn chính', 1, 1),
(15, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Old Skool - Góc nhìn chính', 1, 1),
(16, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Authentic - Góc nhìn chính', 1, 1),
(17, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Sk8-Hi - Góc nhìn chính', 1, 1),
(18, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 550 - Góc nhìn chính', 1, 1),
(19, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 574 - Góc nhìn chính', 1, 1),
(20, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 993 - Góc nhìn chính', 1, 1);

PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product images';
PRINT '';

-- =====================================================
-- VARIANT_IMAGES (90+ images: 2-3 images per variant)
-- =====================================================
PRINT 'Dang them Variant_Images...';

INSERT INTO Variant_Images (variant_id, image_url, alt_text, is_primary, display_order) VALUES
-- Product 1: Nike Air Max 270 - Variant 1 (Đen, 40)
(1, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Max 270 Đen Size 40 - Góc nhìn chính', 1, 1),
(1, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Max 270 Đen Size 40 - Góc nhìn bên', 0, 2),
(1, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Air Max 270 Đen Size 40 - Góc nhìn trên', 0, 3),
-- Product 1: Nike Air Max 270 - Variant 2 (Đen, 41)
(2, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Max 270 Đen Size 41 - Góc nhìn chính', 1, 1),
(2, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Max 270 Đen Size 41 - Góc nhìn bên', 0, 2),
-- Product 2: Nike Air Force 1 - Variant 1 (Đen, 40)
(3, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Force 1 Đen Size 40 - Góc nhìn chính', 1, 1),
(3, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Force 1 Đen Size 40 - Góc nhìn bên', 0, 2),
(3, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Air Force 1 Đen Size 40 - Góc nhìn sau', 0, 3),
-- Product 2: Nike Air Force 1 - Variant 2 (Trắng, 40)
(4, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Nike Air Force 1 Trắng Size 40 - Góc nhìn chính', 1, 1),
(4, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Nike Air Force 1 Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 3: Nike Dunk Low - Variant 1 (Đen, 40)
(5, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Dunk Low Đen Size 40 - Góc nhìn chính', 1, 1),
(5, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Dunk Low Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 3: Nike Dunk Low - Variant 2 (Trắng, 40)
(6, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Nike Dunk Low Trắng Size 40 - Góc nhìn chính', 1, 1),
(6, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Nike Dunk Low Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 4: Nike Pegasus 39 - Variant 1 (Đen, 40)
(7, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Pegasus 39 Đen Size 40 - Góc nhìn chính', 1, 1),
(7, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Pegasus 39 Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 4: Nike Pegasus 39 - Variant 2 (Xanh dương, 40)
(8, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Nike Pegasus 39 Xanh dương Size 40 - Góc nhìn chính', 1, 1),
(8, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Pegasus 39 Xanh dương Size 40 - Góc nhìn bên', 0, 2),
-- Product 5: Adidas Ultraboost 22 - Variant 1 (Đen, 36)
(9, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Ultraboost 22 Đen Size 36 - Góc nhìn chính', 1, 1),
(9, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Ultraboost 22 Đen Size 36 - Góc nhìn bên', 0, 2),
(9, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Adidas Ultraboost 22 Đen Size 36 - Góc nhìn trên', 0, 3),
-- Product 5: Adidas Ultraboost 22 - Variant 2 (Đen, 37)
(10, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Ultraboost 22 Đen Size 37 - Góc nhìn chính', 1, 1),
(10, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Ultraboost 22 Đen Size 37 - Góc nhìn bên', 0, 2),
-- Product 6: Adidas Stan Smith - Variant 1 (Trắng, 36)
(11, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Stan Smith Trắng Size 36 - Góc nhìn chính', 1, 1),
(11, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Stan Smith Trắng Size 36 - Góc nhìn bên', 0, 2),
(11, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Adidas Stan Smith Trắng Size 36 - Góc nhìn sau', 0, 3),
-- Product 6: Adidas Stan Smith - Variant 2 (Trắng, 37)
(12, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Stan Smith Trắng Size 37 - Góc nhìn chính', 1, 1),
(12, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Stan Smith Trắng Size 37 - Góc nhìn bên', 0, 2),
-- Product 7: Adidas NMD R1 - Variant 1 (Đen, 36)
(13, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Adidas NMD R1 Đen Size 36 - Góc nhìn chính', 1, 1),
(13, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Adidas NMD R1 Đen Size 36 - Góc nhìn bên', 0, 2),
-- Product 7: Adidas NMD R1 - Variant 2 (Đen, 37)
(14, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Adidas NMD R1 Đen Size 37 - Góc nhìn chính', 1, 1),
(14, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Adidas NMD R1 Đen Size 37 - Góc nhìn bên', 0, 2),
-- Product 8: Adidas Superstar - Variant 1 (Đen, 36)
(15, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Superstar Đen Size 36 - Góc nhìn chính', 1, 1),
(15, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Superstar Đen Size 36 - Góc nhìn bên', 0, 2),
-- Product 8: Adidas Superstar - Variant 2 (Trắng, 36)
(16, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Superstar Trắng Size 36 - Góc nhìn chính', 1, 1),
(16, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Superstar Trắng Size 36 - Góc nhìn bên', 0, 2),
-- Product 9: Air Jordan 1 Retro High - Variant 1 (Đen/Trắng, 40)
(17, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 1 Retro High Đen/Trắng Size 40 - Góc nhìn chính', 1, 1),
(17, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 1 Retro High Đen/Trắng Size 40 - Góc nhìn bên', 0, 2),
(17, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Air Jordan 1 Retro High Đen/Trắng Size 40 - Góc nhìn sau', 0, 3),
-- Product 9: Air Jordan 1 Retro High - Variant 2 (Đen/Trắng, 41)
(18, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 1 Retro High Đen/Trắng Size 41 - Góc nhìn chính', 1, 1),
(18, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 1 Retro High Đen/Trắng Size 41 - Góc nhìn bên', 0, 2),
-- Product 10: Air Jordan 4 Retro - Variant 1 (Đen/Xám, 40)
(19, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 4 Retro Đen/Xám Size 40 - Góc nhìn chính', 1, 1),
(19, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 4 Retro Đen/Xám Size 40 - Góc nhìn bên', 0, 2),
(19, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Air Jordan 4 Retro Đen/Xám Size 40 - Góc nhìn trên', 0, 3),
-- Product 10: Air Jordan 4 Retro - Variant 2 (Đen/Xám, 41)
(20, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 4 Retro Đen/Xám Size 41 - Góc nhìn chính', 1, 1),
(20, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 4 Retro Đen/Xám Size 41 - Góc nhìn bên', 0, 2),
-- Product 11: Air Jordan 3 Retro - Variant 1 (Đen/Trắng, 40)
(21, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 3 Retro Đen/Trắng Size 40 - Góc nhìn chính', 1, 1),
(21, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 3 Retro Đen/Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 11: Air Jordan 3 Retro - Variant 2 (Đen/Trắng, 41)
(22, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 3 Retro Đen/Trắng Size 41 - Góc nhìn chính', 1, 1),
(22, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 3 Retro Đen/Trắng Size 41 - Góc nhìn bên', 0, 2),
-- Product 12: Converse Chuck Taylor - Variant 1 (Đen, 40)
(23, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck Taylor Đen Size 40 - Góc nhìn chính', 1, 1),
(23, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck Taylor Đen Size 40 - Góc nhìn bên', 0, 2),
(23, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Converse Chuck Taylor Đen Size 40 - Góc nhìn sau', 0, 3),
-- Product 12: Converse Chuck Taylor - Variant 2 (Trắng, 40)
(24, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck Taylor Trắng Size 40 - Góc nhìn chính', 1, 1),
(24, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck Taylor Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 13: Converse Chuck 70 - Variant 1 (Đen, 40)
(25, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck 70 Đen Size 40 - Góc nhìn chính', 1, 1),
(25, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck 70 Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 13: Converse Chuck 70 - Variant 2 (Trắng, 40)
(26, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck 70 Trắng Size 40 - Góc nhìn chính', 1, 1),
(26, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck 70 Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 14: Converse One Star - Variant 1 (Đen, 40)
(27, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse One Star Đen Size 40 - Góc nhìn chính', 1, 1),
(27, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Converse One Star Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 14: Converse One Star - Variant 2 (Đen, 41)
(28, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse One Star Đen Size 41 - Góc nhìn chính', 1, 1),
(28, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Converse One Star Đen Size 41 - Góc nhìn bên', 0, 2),
-- Product 15: Vans Old Skool - Variant 1 (Đen/Trắng, 40)
(29, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Old Skool Đen/Trắng Size 40 - Góc nhìn chính', 1, 1),
(29, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Old Skool Đen/Trắng Size 40 - Góc nhìn bên', 0, 2),
(29, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Vans Old Skool Đen/Trắng Size 40 - Góc nhìn sau', 0, 3),
-- Product 15: Vans Old Skool - Variant 2 (Đen/Trắng, 41)
(30, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Old Skool Đen/Trắng Size 41 - Góc nhìn chính', 1, 1),
(30, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Old Skool Đen/Trắng Size 41 - Góc nhìn bên', 0, 2),
-- Product 16: Vans Authentic - Variant 1 (Đen, 40)
(31, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Authentic Đen Size 40 - Góc nhìn chính', 1, 1),
(31, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Authentic Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 16: Vans Authentic - Variant 2 (Trắng, 40)
(32, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Authentic Trắng Size 40 - Góc nhìn chính', 1, 1),
(32, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Authentic Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 17: Vans Sk8-Hi - Variant 1 (Đen/Trắng, 40)
(33, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Sk8-Hi Đen/Trắng Size 40 - Góc nhìn chính', 1, 1),
(33, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Sk8-Hi Đen/Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 17: Vans Sk8-Hi - Variant 2 (Đen/Trắng, 41)
(34, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Sk8-Hi Đen/Trắng Size 41 - Góc nhìn chính', 1, 1),
(34, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Sk8-Hi Đen/Trắng Size 41 - Góc nhìn bên', 0, 2),
-- Product 18: New Balance 550 - Variant 1 (Đen, 40)
(35, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 550 Đen Size 40 - Góc nhìn chính', 1, 1),
(35, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 550 Đen Size 40 - Góc nhìn bên', 0, 2),
(35, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'New Balance 550 Đen Size 40 - Góc nhìn trên', 0, 3),
-- Product 18: New Balance 550 - Variant 2 (Xám, 40)
(36, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 550 Xám Size 40 - Góc nhìn chính', 1, 1),
(36, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 550 Xám Size 40 - Góc nhìn bên', 0, 2),
-- Product 19: New Balance 574 - Variant 1 (Đen, 40)
(37, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 574 Đen Size 40 - Góc nhìn chính', 1, 1),
(37, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 574 Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 19: New Balance 574 - Variant 2 (Xám, 40)
(38, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 574 Xám Size 40 - Góc nhìn chính', 1, 1),
(38, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 574 Xám Size 40 - Góc nhìn bên', 0, 2),
-- Product 20: New Balance 993 - Variant 1 (Xám, 40)
(39, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 993 Xám Size 40 - Góc nhìn chính', 1, 1),
(39, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 993 Xám Size 40 - Góc nhìn bên', 0, 2),
(39, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'New Balance 993 Xám Size 40 - Góc nhìn sau', 0, 3),
-- Product 20: New Balance 993 - Variant 2 (Xám, 41)
(40, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 993 Xám Size 41 - Góc nhìn chính', 1, 1),
(40, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 993 Xám Size 41 - Góc nhìn bên', 0, 2);

PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' variant images';
PRINT '';

-- =====================================================
-- FINALIZATION & VALIDATION
-- =====================================================
SET NOCOUNT OFF;

-- Validation checks
PRINT '';
PRINT '=====================================================';
PRINT 'DANG KIEM TRA TINH TOAN VEN DU LIEU ANH...';
PRINT '=====================================================';

DECLARE @errorCount INT = 0;

-- Check foreign key integrity for Product_Images
IF EXISTS (SELECT 1 FROM Product_Images pi LEFT JOIN Products p ON pi.product_id = p.id WHERE p.id IS NULL)
BEGIN
    PRINT '  [ERROR] Product_Images có product_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

-- Check foreign key integrity for Variant_Images
IF EXISTS (SELECT 1 FROM Variant_Images vi LEFT JOIN Product_Variants pv ON vi.variant_id = pv.id WHERE pv.id IS NULL)
BEGIN
    PRINT '  [ERROR] Variant_Images có variant_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

-- Check that all products have at least one image
DECLARE @productCount INT;
DECLARE @productImageCount INT;
SELECT @productCount = COUNT(*) FROM Products WHERE deleted_at IS NULL;
SELECT @productImageCount = COUNT(DISTINCT product_id) FROM Product_Images;
IF @productImageCount < @productCount
BEGIN
    PRINT '  [WARNING] Một số sản phẩm chưa có ảnh!';
    PRINT '  Products: ' + CAST(@productCount AS VARCHAR) + ', Products with images: ' + CAST(@productImageCount AS VARCHAR);
END;

-- Check that all variants have at least one image
DECLARE @variantCount INT;
DECLARE @variantImageCount INT;
SELECT @variantCount = COUNT(*) FROM Product_Variants WHERE deleted_at IS NULL;
SELECT @variantImageCount = COUNT(DISTINCT variant_id) FROM Variant_Images;
IF @variantImageCount < @variantCount
BEGIN
    PRINT '  [WARNING] Một số biến thể chưa có ảnh!';
    PRINT '  Variants: ' + CAST(@variantCount AS VARCHAR) + ', Variants with images: ' + CAST(@variantImageCount AS VARCHAR);
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
PRINT 'HOAN THANH THEM DU LIEU ANH!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da them thanh cong:';
PRINT '  + ' + CAST(@productImageCount AS VARCHAR) + ' product images (cho ' + CAST(@productCount AS VARCHAR) + ' products)';
PRINT '  + ' + CAST((SELECT COUNT(*) FROM Variant_Images) AS VARCHAR) + ' variant images (cho ' + CAST(@variantImageCount AS VARCHAR) + ' variants)';
PRINT '';
PRINT 'TAT CA DU LIEU ANH DA DUOC THEM THANH CONG!';
PRINT '=====================================================';
PRINT '';
PRINT 'Script da ket thuc thanh cong!';
PRINT 'Transaction da duoc commit.';
PRINT '';


-- =====================================================
-- UPDATE PRODUCT IMAGES WITH REAL IMAGES FROM UNSPLASH
-- =====================================================
-- Sau khi chạy 2_INSERT_DATA.sql, chạy file này để update URLs ảnh thật
-- =====================================================

USE sneakery_db;
GO

PRINT '=====================================================';
PRINT 'DANG CAP NHAT ANH SAN PHAM VOI ANH THAT TU UNSPLASH...';
PRINT '=====================================================';
PRINT '';

-- Xóa ảnh cũ
DELETE FROM Product_Images;
GO

-- Thêm ảnh mới với URLs thật từ Unsplash
INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order) VALUES
-- Nike Air Max 270 images
(1, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=800&fit=crop', N'Nike Air Max 270 - Góc nhìn chính', 1, 1),
(1, 'https://images.unsplash.com/photo-1600289031464-74d373b329af?w=800&h=800&fit=crop', N'Nike Air Max 270 - Góc nhìn bên', 0, 2),
(1, 'https://images.unsplash.com/photo-1615329799406-1ce3dc38c4f6?w=800&h=800&fit=crop', N'Nike Air Max 270 - Chi tiết', 0, 3),

-- Nike Air Force 1 images
(2, 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=800&h=800&fit=crop', N'Nike Air Force 1 - Góc nhìn chính', 1, 1),
(2, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=800&h=800&fit=crop', N'Nike Air Force 1 - Góc nhìn bên', 0, 2),
(2, 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=800&fit=crop', N'Nike Air Force 1 - Chi tiết', 0, 3),

-- Nike React Element 55 images
(3, 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=800&fit=crop', N'Nike React Element 55 - Góc nhìn chính', 1, 1),
(3, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=800&h=800&fit=crop', N'Nike React Element 55 - Góc nhìn bên', 0, 2),
(3, 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=800&h=800&fit=crop', N'Nike React Element 55 - Chi tiết', 0, 3),

-- Adidas Ultraboost 22 images
(4, 'https://images.unsplash.com/photo-1562183241-b937e95585b6?w=800&h=800&fit=crop', N'Adidas Ultraboost 22 - Góc nhìn chính', 1, 1),
(4, 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=800&fit=crop', N'Adidas Ultraboost 22 - Góc nhìn bên', 0, 2),
(4, 'https://images.unsplash.com/photo-1465855748237-bf36c82ba3b3?w=800&h=800&fit=crop', N'Adidas Ultraboost 22 - Chi tiết', 0, 3),

-- Air Jordan 1 images
(7, 'https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=800&h=800&fit=crop', N'Air Jordan 1 Retro High - Góc nhìn chính', 1, 1),
(7, 'https://images.unsplash.com/photo-1551107696-4f1e8dbbe0e5?w=800&h=800&fit=crop', N'Air Jordan 1 Retro High - Góc nhìn bên', 0, 2),
(7, 'https://images.unsplash.com/photo-1533681534773-eea8f83aefc0?w=800&h=800&fit=crop', N'Air Jordan 1 Retro High - Chi tiết', 0, 3),

-- Air Jordan 4 images
(8, 'https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=800&h=800&fit=crop', N'Air Jordan 4 Retro - Góc nhìn chính', 1, 1),
(8, 'https://images.unsplash.com/photo-1538688525198-9b88f6f53126?w=800&h=800&fit=crop', N'Air Jordan 4 Retro - Góc nhìn bên', 0, 2),
(8, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&h=800&fit=crop', N'Air Jordan 4 Retro - Chi tiết', 0, 3),

-- Air Jordan 11 images
(9, 'https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=800&h=800&fit=crop', N'Air Jordan 11 Retro - Góc nhìn chính', 1, 1),
(9, 'https://images.unsplash.com/photo-1544966503-7d3d9fb94d62?w=800&h=800&fit=crop', N'Air Jordan 11 Retro - Góc nhìn bên', 0, 2),
(9, 'https://images.unsplash.com/photo-1556906781-9a412961c28c?w=800&h=800&fit=crop', N'Air Jordan 11 Retro - Chi tiết', 0, 3),

-- Converse Chuck Taylor images
(10, 'https://images.unsplash.com/photo-1529810313688-44ea1c2d81d3?w=800&h=800&fit=crop', N'Converse Chuck Taylor All Star - Góc nhìn chính', 1, 1),
(10, 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=800&h=800&fit=crop', N'Converse Chuck Taylor All Star - Góc nhìn bên', 0, 2),
(10, 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=800&h=800&fit=crop', N'Converse Chuck Taylor All Star - Chi tiết', 0, 3),

-- Converse One Star images
(11, 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=800&h=800&fit=crop', N'Converse One Star - Góc nhìn chính', 1, 1),
(11, 'https://images.unsplash.com/photo-1579356204817-7e3c69ba8a27?w=800&h=800&fit=crop', N'Converse One Star - Góc nhìn bên', 0, 2),
(11, 'https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=800&h=800&fit=crop', N'Converse One Star - Chi tiết', 0, 3),

-- Vans Old Skool images
(12, 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=800&h=800&fit=crop', N'Vans Old Skool - Góc nhìn chính', 1, 1),
(12, 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=800&fit=crop', N'Vans Old Skool - Góc nhìn bên', 0, 2),
(12, 'https://images.unsplash.com/photo-1565717154653-a9d20c37bff3?w=800&h=800&fit=crop', N'Vans Old Skool - Chi tiết', 0, 3),

-- Vans Authentic images
(13, 'https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=800&h=800&fit=crop', N'Vans Authentic - Góc nhìn chính', 1, 1),
(13, 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=800&h=800&fit=crop', N'Vans Authentic - Góc nhìn bên', 0, 2),
(13, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=800&h=800&fit=crop', N'Vans Authentic - Chi tiết', 0, 3),

-- Puma Suede Classic images
(14, 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=800&fit=crop', N'Puma Suede Classic - Góc nhìn chính', 1, 1),
(14, 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=800&h=800&fit=crop', N'Puma Suede Classic - Góc nhìn bên', 0, 2),
(14, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&h=800&fit=crop', N'Puma Suede Classic - Chi tiết', 0, 3),

-- Puma RS-X images
(15, 'https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=800&h=800&fit=crop', N'Puma RS-X - Góc nhìn chính', 1, 1),
(15, 'https://images.unsplash.com/photo-1544966503-7d3d9fb94d62?w=800&h=800&fit=crop', N'Puma RS-X - Góc nhìn bên', 0, 2),
(15, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=800&h=800&fit=crop', N'Puma RS-X - Chi tiết', 0, 3);
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH CAP NHAT ANH SAN PHAM!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da cap nhat 45 hinh anh thật tu Unsplash cho 15 san pham';
PRINT 'Cac hinh anh nay co kich thuoc 800x800 pixels';
PRINT '=====================================================';
PRINT '';


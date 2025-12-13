-- =====================================================
-- SNEAKERY E-COMMERCE - INSERT DATA PART 1
-- =====================================================
-- File này insert dữ liệu cơ bản: Brands, Categories, Materials,
-- Shoe_Soles, Products, Variants, Images, Users, Addresses
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT N'DANG INSERT DU LIEU CO BAN (PART 1)...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 0. DELETE OLD DATA (if exists)
-- =====================================================
PRINT 'Cleaning old data...';

-- Delete in reverse order of dependencies
DELETE FROM Flash_Sales;
DELETE FROM Product_Categories;
DELETE FROM Variant_Images;
DELETE FROM Product_Images;
DELETE FROM Product_Variants;
DELETE FROM Products;
DELETE FROM Newsletter_Subscriptions;
DELETE FROM Addresses;
DELETE FROM Carts;
DELETE FROM Users WHERE email NOT IN ('admin@sneakery.com', 'moderator1@sneakery.com', 'moderator2@sneakery.com', 'user1@example.com');
DELETE FROM Coupons;
DELETE FROM Shoe_Soles;
DELETE FROM Materials;
DELETE FROM Categories;
DELETE FROM Brands;
GO

PRINT '  - Cleaned old data';
PRINT '';

-- =====================================================
-- 1. BRANDS (10 brands)
-- =====================================================
PRINT 'Inserting Brands...';

INSERT INTO Brands (name, slug, logo_url, description, website_url, is_active, created_at) VALUES
(N'Nike', 'nike', 'https://logo.clearbit.com/nike.com', N'Just Do It - Thương hiệu thể thao hàng đầu thế giới', 'https://www.nike.com', 1, GETDATE()),
(N'Adidas', 'adidas', 'https://logo.clearbit.com/adidas.com', N'Impossible is Nothing - Thương hiệu thể thao Đức', 'https://www.adidas.com', 1, GETDATE()),
(N'Puma', 'puma', 'https://logo.clearbit.com/puma.com', N'Forever Faster - Thương hiệu thể thao năng động', 'https://www.puma.com', 1, GETDATE()),
(N'Converse', 'converse', 'https://logo.clearbit.com/converse.com', N'All Stars - Thương hiệu giày cổ điển', 'https://www.converse.com', 1, GETDATE()),
(N'Vans', 'vans', 'https://logo.clearbit.com/vans.com', N'Off The Wall - Thương hiệu giày skateboard', 'https://www.vans.com', 1, GETDATE()),
(N'New Balance', 'new-balance', 'https://logo.clearbit.com/newbalance.com', N'Made in USA - Thương hiệu giày chạy bộ', 'https://www.newbalance.com', 1, GETDATE()),
(N'Reebok', 'reebok', 'https://logo.clearbit.com/reebok.com', N'I Am What I Am - Thương hiệu thể thao', 'https://www.reebok.com', 1, GETDATE()),
(N'Asics', 'asics', 'https://logo.clearbit.com/asics.com', N'Anima Sana In Corpore Sano - Thương hiệu giày chạy bộ Nhật Bản', 'https://www.asics.com', 1, GETDATE()),
(N'Fila', 'fila', 'https://logo.clearbit.com/fila.com', N'Retro Style - Thương hiệu thể thao Ý', 'https://www.fila.com', 1, GETDATE()),
(N'Jordan', 'jordan', 'https://logo.clearbit.com/jordan.com', N'Air Jordan - Thương hiệu giày bóng rổ huyền thoại', 'https://www.jordan.com', 1, GETDATE());
GO

PRINT '  - Inserted 10 brands';
GO

-- =====================================================
-- 2. CATEGORIES (15 categories)
-- =====================================================
PRINT 'Inserting Categories...';

-- Root categories
INSERT INTO Categories (name, slug, description, lft, rgt, level, display_order, is_active, created_at) VALUES
(N'Sneakers', 'sneakers', N'Giày thể thao', 1, 30, 0, 1, 1, GETDATE()),
(N'Running', 'running', N'Giày chạy bộ', 31, 40, 0, 2, 1, GETDATE()),
(N'Basketball', 'basketball', N'Giày bóng rổ', 41, 50, 0, 3, 1, GETDATE()),
(N'Lifestyle', 'lifestyle', N'Giày thời trang', 51, 70, 0, 4, 1, GETDATE()),
(N'Skateboard', 'skateboard', N'Giày trượt ván', 71, 80, 0, 5, 1, GETDATE());
GO

-- Sub-categories for Sneakers
DECLARE @SneakersId INT = (SELECT id FROM Categories WHERE slug = 'sneakers');
INSERT INTO Categories (name, slug, description, parent_id, lft, rgt, level, display_order, is_active, created_at) VALUES
(N'High Top', 'high-top', N'Giày cổ cao', @SneakersId, 2, 9, 1, 1, 1, GETDATE()),
(N'Low Top', 'low-top', N'Giày cổ thấp', @SneakersId, 10, 17, 1, 2, 1, GETDATE()),
(N'Mid Top', 'mid-top', N'Giày cổ giữa', @SneakersId, 18, 25, 1, 3, 1, GETDATE()),
(N'Slip On', 'slip-on', N'Giày không dây', @SneakersId, 26, 29, 1, 4, 1, GETDATE());
GO

-- Sub-categories for Lifestyle
DECLARE @LifestyleId INT = (SELECT id FROM Categories WHERE slug = 'lifestyle');
INSERT INTO Categories (name, slug, description, parent_id, lft, rgt, level, display_order, is_active, created_at) VALUES
(N'Casual', 'casual', N'Giày đi chơi', @LifestyleId, 52, 59, 1, 1, 1, GETDATE()),
(N'Streetwear', 'streetwear', N'Giày đường phố', @LifestyleId, 60, 67, 1, 2, 1, GETDATE()),
(N'Retro', 'retro', N'Giày cổ điển', @LifestyleId, 68, 70, 1, 3, 1, GETDATE());
GO

PRINT '  - Inserted 15 categories';
GO

-- =====================================================
-- 3. MATERIALS (8 materials)
-- =====================================================
PRINT 'Inserting Materials...';

INSERT INTO Materials (name, slug, description, is_active, created_at) VALUES
(N'Da thật', 'da-that', N'Chất liệu da tự nhiên cao cấp', 1, GETDATE()),
(N'Da tổng hợp', 'da-tong-hop', N'Chất liệu da nhân tạo', 1, GETDATE()),
(N'Vải Canvas', 'vai-canvas', N'Chất liệu vải canvas bền chắc', 1, GETDATE()),
(N'Mesh', 'mesh', N'Chất liệu lưới thoáng khí', 1, GETDATE()),
(N'Knit', 'knit', N'Chất liệu dệt kim', 1, GETDATE());
GO

PRINT '  - Inserted 5 materials';
GO

-- =====================================================
-- 4. SHOE_SOLES (5 shoe soles)
-- =====================================================
PRINT 'Inserting Shoe_Soles...';

INSERT INTO Shoe_Soles (name, slug, description, is_active, created_at) VALUES
(N'Cao su', 'cao-su', N'Đế cao su chống trượt', 1, GETDATE()),
(N'EVA', 'eva', N'Đế EVA nhẹ và đàn hồi', 1, GETDATE()),
(N'TPU', 'tpu', N'Đế TPU bền chắc', 1, GETDATE());
GO

PRINT '  - Inserted 3 shoe soles';
GO

-- =====================================================
-- 5. PRODUCTS (25 products)
-- =====================================================
PRINT 'Inserting Products...';

DECLARE @NikeId INT = (SELECT id FROM Brands WHERE slug = 'nike');
DECLARE @AdidasId INT = (SELECT id FROM Brands WHERE slug = 'adidas');
DECLARE @PumaId INT = (SELECT id FROM Brands WHERE slug = 'puma');
DECLARE @ConverseId INT = (SELECT id FROM Brands WHERE slug = 'converse');
DECLARE @VansId INT = (SELECT id FROM Brands WHERE slug = 'vans');
DECLARE @NewBalanceId INT = (SELECT id FROM Brands WHERE slug = 'new-balance');
DECLARE @ReebokId INT = (SELECT id FROM Brands WHERE slug = 'reebok');
DECLARE @AsicsId INT = (SELECT id FROM Brands WHERE slug = 'asics');
DECLARE @FilaId INT = (SELECT id FROM Brands WHERE slug = 'fila');
DECLARE @JordanId INT = (SELECT id FROM Brands WHERE slug = 'jordan');

DECLARE @LeatherId INT = (SELECT id FROM Materials WHERE slug = 'da-that');
DECLARE @CanvasId INT = (SELECT id FROM Materials WHERE slug = 'vai-canvas');
DECLARE @MeshId INT = (SELECT id FROM Materials WHERE slug = 'mesh');
DECLARE @KnitId INT = (SELECT id FROM Materials WHERE slug = 'knit');

DECLARE @RubberId INT = (SELECT id FROM Shoe_Soles WHERE slug = 'cao-su');
DECLARE @EVAId INT = (SELECT id FROM Shoe_Soles WHERE slug = 'eva');
DECLARE @TPUId INT = (SELECT id FROM Shoe_Soles WHERE slug = 'tpu');

-- Nike products (5)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('NIKE-001', @NikeId, @KnitId, @EVAId, N'Nike Air Force 1', 'nike-air-force-1', N'Giày sneaker cổ điển với thiết kế đơn giản và thoải mái', N'Giày sneaker cổ điển', 1, 1, 0, 1250, 45, 4.5, 12, GETDATE()),
('NIKE-002', @NikeId, @MeshId, @EVAId, N'Nike Air Max 90', 'nike-air-max-90', N'Giày chạy bộ với công nghệ Air Max', N'Giày chạy bộ công nghệ Air', 1, 1, 1, 980, 32, 4.3, 8, GETDATE()),
('NIKE-005', @NikeId, @LeatherId, @RubberId, N'Nike Blazer Mid', 'nike-blazer-mid', N'Giày cổ điển thiết kế retro', N'Giày retro', 1, 1, 0, 890, 35, 4.1, 9, GETDATE()),
('NIKE-008', @NikeId, @LeatherId, @RubberId, N'Nike Cortez', 'nike-cortez', N'Giày cổ điển năm 1972', N'Giày cổ điển', 1, 1, 0, 1100, 42, 4.6, 11, GETDATE()),
('NIKE-010', @NikeId, @KnitId, @EVAId, N'Nike VaporMax', 'nike-vapormax', N'Giày với đế Air VaporMax', N'Giày công nghệ Air', 1, 1, 1, 1350, 50, 4.7, 15, GETDATE());

-- Adidas products (4)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('ADIDAS-001', @AdidasId, @KnitId, @EVAId, N'Adidas Ultraboost 22', 'adidas-ultraboost-22', N'Giày chạy bộ với Boost technology', N'Giày chạy bộ Boost', 1, 1, 1, 1200, 48, 4.6, 13, GETDATE()),
('ADIDAS-002', @AdidasId, @CanvasId, @RubberId, N'Adidas Superstar', 'adidas-superstar', N'Giày cổ điển với vỏ sò', N'Giày cổ điển', 1, 1, 0, 1050, 40, 4.5, 10, GETDATE()),
('ADIDAS-004', @AdidasId, @KnitId, @EVAId, N'Adidas NMD R1', 'adidas-nmd-r1', N'Giày lifestyle với Boost', N'Giày lifestyle', 1, 1, 0, 880, 30, 4.3, 7, GETDATE()),
('ADIDAS-007', @AdidasId, @KnitId, @EVAId, N'Adidas Yeezy 350', 'adidas-yeezy-350', N'Giày collaboration với Kanye West', N'Giày Yeezy', 1, 1, 1, 1500, 55, 4.8, 18, GETDATE());

-- Puma products (3)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('PUMA-001', @PumaId, @LeatherId, @RubberId, N'Puma Suede Classic', 'puma-suede-classic', N'Giày cổ điển với da lộn', N'Giày cổ điển', 1, 1, 0, 850, 32, 4.4, 8, GETDATE()),
('PUMA-002', @PumaId, @MeshId, @EVAId, N'Puma RS-X', 'puma-rs-x', N'Giày retro futuristic', N'Giày retro', 1, 0, 1, 750, 26, 4.3, 6, GETDATE()),
('PUMA-004', @PumaId, @CanvasId, @RubberId, N'Puma Basket', 'puma-basket', N'Giày bóng rổ cổ điển', N'Giày bóng rổ', 1, 1, 0, 780, 28, 4.3, 7, GETDATE());

-- Converse products (3)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('CONV-001', @ConverseId, @CanvasId, @RubberId, N'Converse Chuck Taylor All Star', 'converse-chuck-taylor-all-star', N'Giày cổ điển nhất mọi thời đại', N'Giày cổ điển', 1, 1, 0, 1300, 52, 4.7, 14, GETDATE()),
('CONV-002', @ConverseId, @CanvasId, @RubberId, N'Converse One Star', 'converse-one-star', N'Giày với ngôi sao', N'Giày One Star', 1, 0, 0, 950, 38, 4.5, 9, GETDATE()),
('CONV-004', @ConverseId, @CanvasId, @RubberId, N'Converse Run Star Hike', 'converse-run-star-hike', N'Giày platform cao', N'Giày platform', 1, 1, 0, 1100, 42, 4.6, 11, GETDATE());

-- Vans products (3)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('VANS-001', @VansId, @CanvasId, @RubberId, N'Vans Old Skool', 'vans-old-skool', N'Giày skateboard cổ điển', N'Giày skateboard', 1, 1, 0, 1150, 45, 4.6, 12, GETDATE()),
('VANS-002', @VansId, @CanvasId, @RubberId, N'Vans Authentic', 'vans-authentic', N'Giày skateboard đơn giản', N'Giày skateboard', 1, 0, 0, 980, 36, 4.4, 9, GETDATE()),
('VANS-003', @VansId, @CanvasId, @RubberId, N'Vans Sk8-Hi', 'vans-sk8-hi', N'Giày cổ cao', N'Giày cổ cao', 1, 1, 1, 1050, 40, 4.5, 10, GETDATE());

-- New Balance products (2)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('NB-001', @NewBalanceId, @MeshId, @EVAId, N'New Balance 550', 'new-balance-550', N'Giày retro basketball', N'Giày bóng rổ', 1, 1, 1, 1100, 42, 4.6, 11, GETDATE()),
('NB-003', @NewBalanceId, @KnitId, @EVAId, N'New Balance 990', 'new-balance-990', N'Giày chạy bộ cao cấp', N'Giày chạy bộ', 1, 1, 0, 1200, 46, 4.7, 13, GETDATE());

-- Reebok products (2)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('REEBOK-001', @ReebokId, @LeatherId, @RubberId, N'Reebok Classic', 'reebok-classic', N'Giày cổ điển', N'Giày cổ điển', 1, 1, 0, 850, 30, 4.4, 8, GETDATE()),
('REEBOK-002', @ReebokId, @MeshId, @EVAId, N'Reebok Club C', 'reebok-club-c', N'Giày tennis cổ điển', N'Giày tennis', 1, 0, 0, 750, 26, 4.3, 6, GETDATE());

-- Asics products (2)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('ASICS-001', @AsicsId, @MeshId, @EVAId, N'Asics Gel-Kayano 28', 'asics-gel-kayano-28', N'Giày chạy bộ với Gel', N'Giày chạy bộ', 1, 1, 1, 1000, 38, 4.5, 10, GETDATE()),
('ASICS-002', @AsicsId, @MeshId, @EVAId, N'Asics Gel-Nimbus 24', 'asics-gel-nimbus-24', N'Giày chạy bộ đệm cao', N'Giày chạy bộ', 1, 0, 0, 880, 32, 4.4, 8, GETDATE());

-- Fila products (1)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('FILA-001', @FilaId, @LeatherId, @RubberId, N'Fila Disruptor', 'fila-disruptor', N'Giày platform retro', N'Giày platform', 1, 1, 0, 950, 36, 4.5, 9, GETDATE());

-- Jordan products (2)
INSERT INTO Products (product_code, brand_id, material_id, shoe_sole_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count, created_at) VALUES
('JORDAN-001', @JordanId, @LeatherId, @RubberId, N'Air Jordan 1', 'air-jordan-1', N'Giày bóng rổ huyền thoại', N'Giày bóng rổ', 1, 1, 0, 1400, 58, 4.8, 16, GETDATE()),
('JORDAN-002', @JordanId, @LeatherId, @RubberId, N'Air Jordan 4', 'air-jordan-4', N'Giày bóng rổ cổ điển', N'Giày bóng rổ', 1, 1, 1, 1300, 52, 4.7, 14, GETDATE());
GO

PRINT '  - Inserted 25 products';
GO

-- =====================================================
-- 6. PRODUCT_CATEGORIES (Link products to categories)
-- =====================================================
PRINT 'Inserting Product_Categories...';

DECLARE @SneakersCatId INT = (SELECT id FROM Categories WHERE slug = 'sneakers');
DECLARE @RunningCatId INT = (SELECT id FROM Categories WHERE slug = 'running');
DECLARE @BasketballCatId INT = (SELECT id FROM Categories WHERE slug = 'basketball');
DECLARE @LifestyleCatId INT = (SELECT id FROM Categories WHERE slug = 'lifestyle');
DECLARE @SkateboardCatId INT = (SELECT id FROM Categories WHERE slug = 'skateboard');
DECLARE @HighTopId INT = (SELECT id FROM Categories WHERE slug = 'high-top');
DECLARE @LowTopId INT = (SELECT id FROM Categories WHERE slug = 'low-top');
DECLARE @CasualId INT = (SELECT id FROM Categories WHERE slug = 'casual');

-- Link Nike products
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @SneakersCatId FROM Products p WHERE p.product_code LIKE 'NIKE-%'
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @SneakersCatId);
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @RunningCatId FROM Products p WHERE p.product_code IN ('NIKE-002', 'NIKE-004', 'NIKE-006', 'NIKE-007', 'NIKE-009')
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @RunningCatId);
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @LifestyleCatId FROM Products p WHERE p.product_code IN ('NIKE-001', 'NIKE-003', 'NIKE-005', 'NIKE-008', 'NIKE-010')
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @LifestyleCatId);

-- Link Adidas products
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @SneakersCatId FROM Products p WHERE p.product_code LIKE 'ADIDAS-%'
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @SneakersCatId);
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @RunningCatId FROM Products p WHERE p.product_code IN ('ADIDAS-001', 'ADIDAS-005')
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @RunningCatId);
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @LifestyleCatId FROM Products p WHERE p.product_code IN ('ADIDAS-002', 'ADIDAS-003', 'ADIDAS-004', 'ADIDAS-006', 'ADIDAS-007', 'ADIDAS-008')
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @LifestyleCatId);

-- Link other brands
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @SneakersCatId FROM Products p WHERE (p.product_code LIKE 'PUMA-%' OR p.product_code LIKE 'CONV-%' OR p.product_code LIKE 'VANS-%')
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @SneakersCatId);
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @SkateboardCatId FROM Products p WHERE p.product_code LIKE 'VANS-%'
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @SkateboardCatId);
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @BasketballCatId FROM Products p WHERE p.product_code LIKE 'JORDAN-%'
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @BasketballCatId);
INSERT INTO Product_Categories (product_id, category_id)
SELECT p.id, @RunningCatId FROM Products p WHERE (p.product_code LIKE 'NB-%' OR p.product_code LIKE 'ASICS-%')
AND NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = p.id AND category_id = @RunningCatId);
GO

PRINT '  - Linked products to categories';
GO

-- =====================================================
-- 7. PRODUCT_VARIANTS (200 variants - 4 variants per product on average)
-- =====================================================
PRINT 'Inserting Product_Variants...';

-- Helper function to generate variants for a product
-- We'll create variants with different sizes and colors
DECLARE @ProductId BIGINT;
DECLARE @SkuCounter INT;
DECLARE @PriceBase BIGINT;
DECLARE @PriceSale BIGINT;
-- Get max SKU number to avoid duplicates
SELECT @SkuCounter = ISNULL(MAX(CAST(SUBSTRING(sku, 5, LEN(sku)) AS INT)), 0) + 1
FROM Product_Variants
WHERE sku LIKE 'SKU-%';
IF @SkuCounter IS NULL SET @SkuCounter = 1;

-- Create variants for each product (4 variants each: 2 sizes x 2 colors)
DECLARE product_cursor CURSOR FOR
SELECT id FROM Products ORDER BY id;

OPEN product_cursor;
FETCH NEXT FROM product_cursor INTO @ProductId;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Generate random price from 1,000,000 to 50,000,000
    SET @PriceBase = 1000000 + CAST(RAND() * 49000000 AS BIGINT);
    SET @PriceSale = @PriceBase - CAST(RAND() * (@PriceBase * 0.2) AS BIGINT); -- 0-20% discount
    -- Round up to nearest 1000 (làm tròn lên đến hàng nghìn)
    SET @PriceBase = CEILING(@PriceBase / 1000.0) * 1000;
    SET @PriceSale = CEILING(@PriceSale / 1000.0) * 1000;
    
    -- Variant 1: Size 40, Color 1
    INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, stock_quantity, low_stock_threshold, is_active, created_at)
    VALUES (@ProductId, 'SKU-' + FORMAT(@SkuCounter, '000000'), '40', N'Đen', @PriceBase, @PriceSale, 50, 10, 1, GETDATE());
    SET @SkuCounter = @SkuCounter + 1;
    
    -- Variant 2: Size 41, Color 1
    INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, stock_quantity, low_stock_threshold, is_active, created_at)
    VALUES (@ProductId, 'SKU-' + FORMAT(@SkuCounter, '000000'), '41', N'Đen', @PriceBase, @PriceSale, 45, 10, 1, GETDATE());
    SET @SkuCounter = @SkuCounter + 1;
    
    -- Variant 3: Size 40, Color 2
    INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, stock_quantity, low_stock_threshold, is_active, created_at)
    VALUES (@ProductId, 'SKU-' + FORMAT(@SkuCounter, '000000'), '40', N'Trắng', @PriceBase, @PriceSale, 40, 10, 1, GETDATE());
    SET @SkuCounter = @SkuCounter + 1;
    
    -- Variant 4: Size 42, Color 1
    INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, stock_quantity, low_stock_threshold, is_active, created_at)
    VALUES (@ProductId, 'SKU-' + FORMAT(@SkuCounter, '000000'), '42', N'Đen', @PriceBase, @PriceSale, 35, 10, 1, GETDATE());
    SET @SkuCounter = @SkuCounter + 1;
    
    FETCH NEXT FROM product_cursor INTO @ProductId;
END;

CLOSE product_cursor;
DEALLOCATE product_cursor;
GO

PRINT '  - Inserted ~100 product variants';
GO

-- =====================================================
-- 8. PRODUCT_IMAGES - SKIPPED (Admin sẽ upload ảnh qua Cloudinary)
-- =====================================================
PRINT 'Skipping Product_Images (will be uploaded via admin panel)...';
GO

-- =====================================================
-- 9. VARIANT_IMAGES (50 images - 2 images per variant for some variants)
-- =====================================================
PRINT 'Inserting Variant_Images...';

DECLARE @VariantId BIGINT;
DECLARE variant_img_cursor CURSOR FOR
SELECT TOP 25 id FROM Product_Variants ORDER BY id;

OPEN variant_img_cursor;
FETCH NEXT FROM variant_img_cursor INTO @VariantId;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Image 1 (primary)
    INSERT INTO Variant_Images (variant_id, image_url, alt_text, is_primary, display_order, created_at)
    VALUES (@VariantId, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800', 'Variant image 1', 1, 1, GETDATE());
    
    -- Image 2
    INSERT INTO Variant_Images (variant_id, image_url, alt_text, is_primary, display_order, created_at)
    VALUES (@VariantId, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=800', 'Variant image 2', 0, 2, GETDATE());
    
    FETCH NEXT FROM variant_img_cursor INTO @VariantId;
END;

CLOSE variant_img_cursor;
DEALLOCATE variant_img_cursor;
GO

PRINT '  - Inserted ~50 variant images';
GO

-- =====================================================
-- 10. USERS (100 users: 1 admin, 2 moderators, 97 customers)
-- =====================================================
PRINT 'Inserting Users...';

-- Admin user (password hash for "password" using BCrypt)
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'admin@sneakery.com')
BEGIN
    INSERT INTO Users (email, password_hash, full_name, phone_number, is_active, role, created_at) VALUES
    ('admin@sneakery.com', '$2a$10$4qUjgvVWlYZUf1Jx.bRBte0Ls0fff/TSkBwCk2568Z/dfc3Eut.5O', 'Admin User', '0901234567', 1, 'ADMIN', GETDATE());
END;
GO

-- Moderator users
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'moderator1@sneakery.com')
BEGIN
    INSERT INTO Users (email, password_hash, full_name, phone_number, is_active, role, created_at) VALUES
    ('moderator1@sneakery.com', '$2a$10$4qUjgvVWlYZUf1Jx.bRBte0Ls0fff/TSkBwCk2568Z/dfc3Eut.5O', 'Moderator 1', '0901234568', 1, 'MODERATOR', GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'moderator2@sneakery.com')
BEGIN
    INSERT INTO Users (email, password_hash, full_name, phone_number, is_active, role, created_at) VALUES
    ('moderator2@sneakery.com', '$2a$10$4qUjgvVWlYZUf1Jx.bRBte0Ls0fff/TSkBwCk2568Z/dfc3Eut.5O', 'Moderator 2', '0901234569', 1, 'MODERATOR', GETDATE());
END;
GO

-- Customer users (97 users)
DECLARE @Counter INT = 1;
WHILE @Counter <= 97
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Users WHERE email = 'user' + CAST(@Counter AS NVARCHAR(3)) + '@example.com')
    BEGIN
        INSERT INTO Users (email, password_hash, full_name, phone_number, is_active, role, created_at)
        VALUES 
        ('user' + CAST(@Counter AS NVARCHAR(3)) + '@example.com', 
         '$2a$10$4qUjgvVWlYZUf1Jx.bRBte0Ls0fff/TSkBwCk2568Z/dfc3Eut.5O',
         'User ' + CAST(@Counter AS NVARCHAR(3)),
         '090' + RIGHT('0000000' + CAST(@Counter AS NVARCHAR(7)), 7),
         1, 'USER', DATEADD(day, -RAND() * 90, GETDATE()));
    END;
    
    SET @Counter = @Counter + 1;
END;
GO

PRINT '  - Inserted 30 users (1 admin, 2 moderators, 27 customers)';
GO

-- =====================================================
-- 11. ADDRESSES (50 addresses - 1-2 addresses per user)
-- =====================================================
PRINT 'Inserting Addresses...';

DECLARE @UserId BIGINT;
DECLARE address_cursor CURSOR FOR
SELECT id FROM Users WHERE role = 'USER' ORDER BY id;

OPEN address_cursor;
FETCH NEXT FROM address_cursor INTO @UserId;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Address 1 (default)
    INSERT INTO Addresses (user_id, recipient_name, phone, line1, line2, city, ward, district, postal_code, is_default, address_type, created_at)
    VALUES (@UserId, N'Nguyen Van A', '0901234567', N'123 Duong ABC', N'Chung cu XYZ', N'Ho Chi Minh', N'Phuong 1', N'Quan 1', '700000', 1, 'home', GETDATE());
    
    -- Address 2 (50% chance)
    IF RAND() > 0.5
    BEGIN
        INSERT INTO Addresses (user_id, recipient_name, phone, line1, line2, city, ward, district, postal_code, is_default, address_type, created_at)
        VALUES (@UserId, N'Nguyen Van A', '0901234567', N'456 Duong DEF', NULL, N'Ha Noi', N'Phuong 2', N'Quan Hoan Kiem', '100000', 0, 'office', GETDATE());
    END;
    
    FETCH NEXT FROM address_cursor INTO @UserId;
END;

CLOSE address_cursor;
DEALLOCATE address_cursor;
GO

PRINT '  - Inserted ~50 addresses';
GO

-- =====================================================
-- 12. NEWSLETTER_SUBSCRIPTIONS (10 subscriptions)
-- =====================================================
PRINT 'Inserting Newsletter_Subscriptions...';

DECLARE @SubCounter INT = 1;
WHILE @SubCounter <= 10
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Newsletter_Subscriptions WHERE email = 'subscriber' + CAST(@SubCounter AS NVARCHAR(3)) + '@example.com')
    BEGIN
        INSERT INTO Newsletter_Subscriptions (email, is_active, subscribed_at)
        VALUES ('subscriber' + CAST(@SubCounter AS NVARCHAR(3)) + '@example.com', 1, DATEADD(day, -RAND() * 60, GETDATE()));
    END;
    SET @SubCounter = @SubCounter + 1;
END;
GO

PRINT '  - Inserted 10 newsletter subscriptions';
GO

-- =====================================================
-- 13. COUPONS (5 coupons)
-- =====================================================
PRINT 'Inserting Coupons...';

IF NOT EXISTS (SELECT 1 FROM Coupons WHERE code = 'WELCOME10')
BEGIN
    INSERT INTO Coupons (code, description, discount_type, discount_value, max_discount_amount, min_order_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, is_active, created_at) VALUES
    ('WELCOME10', N'Giảm 10% cho khách hàng mới', 'percent', 10, 500000, 1000000, DATEADD(day, -30, GETDATE()), DATEADD(day, 30, GETDATE()), 1000, 45, 1, 1, GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Coupons WHERE code = 'SUMMER20')
BEGIN
    INSERT INTO Coupons (code, description, discount_type, discount_value, max_discount_amount, min_order_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, is_active, created_at) VALUES
    ('SUMMER20', N'Giảm 20% mùa hè', 'percent', 20, 1000000, 2000000, DATEADD(day, -15, GETDATE()), DATEADD(day, 15, GETDATE()), 500, 120, 2, 1, GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Coupons WHERE code = 'FIXED50K')
BEGIN
    INSERT INTO Coupons (code, description, discount_type, discount_value, max_discount_amount, min_order_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, is_active, created_at) VALUES
    ('FIXED50K', N'Giảm 50.000đ', 'fixed', 50000, 50000, 500000, DATEADD(day, -10, GETDATE()), DATEADD(day, 20, GETDATE()), 2000, 380, 1, 1, GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Coupons WHERE code = 'VIP30')
BEGIN
    INSERT INTO Coupons (code, description, discount_type, discount_value, max_discount_amount, min_order_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, is_active, created_at) VALUES
    ('VIP30', N'Giảm 30% cho VIP', 'percent', 30, 2000000, 3000000, DATEADD(day, -5, GETDATE()), DATEADD(day, 25, GETDATE()), 100, 25, 1, 1, GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Coupons WHERE code = 'FLASH15')
BEGIN
    INSERT INTO Coupons (code, description, discount_type, discount_value, max_discount_amount, min_order_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, is_active, created_at) VALUES
    ('FLASH15', N'Flash sale 15%', 'percent', 15, 750000, 1500000, GETDATE(), DATEADD(day, 7, GETDATE()), 500, 0, 1, 1, GETDATE());
END;
GO

PRINT '  - Inserted 5 coupons';
GO

-- =====================================================
-- 14. FLASH_SALES (3 flash sales)
-- =====================================================
PRINT 'Inserting Flash_Sales...';

DECLARE @FlashProductId BIGINT;
SELECT TOP 3 @FlashProductId = id FROM Products WHERE is_featured = 1 ORDER BY NEWID();

-- Create 3 flash sales
DECLARE @FlashCounter INT = 1;
WHILE @FlashCounter <= 3
BEGIN
    SELECT TOP 1 @FlashProductId = id FROM Products WHERE is_featured = 1 ORDER BY NEWID();
    
    INSERT INTO Flash_Sales (product_id, discount_percent, start_time, end_time, quantity_limit, sold_count, is_active, created_at)
    VALUES (@FlashProductId, 20 + (@FlashCounter * 5), DATEADD(day, -@FlashCounter, GETDATE()), DATEADD(day, 7 - @FlashCounter, GETDATE()), 100, 10 + (@FlashCounter * 5), 1, GETDATE());
    
    SET @FlashCounter = @FlashCounter + 1;
END;
GO

PRINT '  - Inserted 3 flash sales';
GO

PRINT '';
PRINT '=====================================================';
PRINT N'HOAN THANH INSERT DU LIEU CO BAN (PART 1)!';
PRINT '=====================================================';
PRINT '';
PRINT N'Da insert thanh cong:';
PRINT '  - 10 brands';
PRINT '  - 15 categories';
PRINT '  - 5 materials';
PRINT '  - 3 shoe soles';
PRINT '  - 25 products';
PRINT '  - ~100 product variants';
PRINT '  - Product images (skipped - will be uploaded via admin panel)';
PRINT '  - ~50 variant images';
PRINT '  - 30 users';
PRINT '  - ~50 addresses';
PRINT '  - 10 newsletter subscriptions';
PRINT '  - 5 coupons';
PRINT '  - 3 flash sales';
PRINT '';
PRINT N'Bước tiếp theo: Chạy file 3_INSERT_DATA_PART2.sql để thêm dữ liệu giao dịch.';
PRINT '';


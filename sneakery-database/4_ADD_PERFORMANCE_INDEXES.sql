-- =====================================================
-- 4. ADD PERFORMANCE INDEXES (Performance Optimization)
-- =====================================================
-- Script này thêm các indexes để cải thiện performance cho queries thường dùng
-- Chạy script này sau khi đã có data hoặc khi cần optimize queries

-- =====================================================
-- PRODUCTS TABLE - Additional Performance Indexes
-- =====================================================

-- Index cho tìm kiếm theo tên (nếu chưa có)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_name' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_name ON Products(name);
    PRINT 'Created index: idx_products_name';
END
GO

-- Index cho sorting theo ngày tạo (nếu chưa có)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_created_at' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_created_at ON Products(created_at);
    PRINT 'Created index: idx_products_created_at';
END
GO

-- Covering index cho search (name + slug + brand_id) - giúp query nhanh hơn
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_name_search' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_name_search ON Products(name) INCLUDE (slug, brand_id);
    PRINT 'Created index: idx_products_name_search';
END
GO

-- =====================================================
-- PRODUCT_VARIANTS TABLE - Additional Performance Indexes
-- =====================================================

-- Composite index cho product_id + stock_quantity (cho filtering stock level)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_variants_product_stock' AND object_id = OBJECT_ID('Product_Variants'))
BEGIN
    CREATE INDEX idx_variants_product_stock ON Product_Variants(product_id, stock_quantity);
    PRINT 'Created index: idx_variants_product_stock';
END
GO

-- Index cho price filtering
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_variants_price_base' AND object_id = OBJECT_ID('Product_Variants'))
BEGIN
    CREATE INDEX idx_variants_price_base ON Product_Variants(price_base);
    PRINT 'Created index: idx_variants_price_base';
END
GO

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_variants_price_sale' AND object_id = OBJECT_ID('Product_Variants'))
BEGIN
    CREATE INDEX idx_variants_price_sale ON Product_Variants(price_sale);
    PRINT 'Created index: idx_variants_price_sale';
END
GO

-- =====================================================
-- PRODUCT_CATEGORIES TABLE - Indexes
-- =====================================================

-- Đảm bảo có index cho category_id (cho filtering)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_pc_category' AND object_id = OBJECT_ID('Product_Categories'))
BEGIN
    CREATE INDEX idx_pc_category ON Product_Categories(category_id);
    PRINT 'Created index: idx_pc_category';
END
GO

PRINT 'Performance indexes đã được thêm thành công!';
GO


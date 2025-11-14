-- =====================================================
-- SNEAKERY E-COMMERCE - ADDITIONAL INDEXES FOR PERFORMANCE
-- =====================================================
-- File này thêm các indexes bổ sung để cải thiện performance
-- cho các query thường dùng trong Admin và User interfaces
-- =====================================================
-- 
-- LƯU Ý: Chạy file này SAU KHI đã chạy 1_CREATE_SCHEMA.sql
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '';
PRINT '=====================================================';
PRINT 'DANG THEM INDEXES BO SUNG...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- PRODUCTS TABLE - Additional Indexes
-- =====================================================

-- Index cho tìm kiếm theo tên sản phẩm (Admin search)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_name' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_name ON Products(name);
    PRINT '  - Created index: idx_products_name on Products(name)';
END
ELSE
BEGIN
    PRINT '  - Index idx_products_name already exists';
END
GO

-- Index cho sorting theo created_at (Admin list)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_created_at' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_created_at ON Products(created_at DESC);
    PRINT '  - Created index: idx_products_created_at on Products(created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_products_created_at already exists';
END
GO

-- Composite index cho filter + sort (Admin: active products sorted by created_at)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_active_created' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_active_created ON Products(is_active, created_at DESC) 
    WHERE deleted_at IS NULL;
    PRINT '  - Created index: idx_products_active_created on Products(is_active, created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_products_active_created already exists';
END
GO

-- Index cho brand_id + created_at (Admin filter by brand)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_brand_created' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_brand_created ON Products(brand_id, created_at DESC);
    PRINT '  - Created index: idx_products_brand_created on Products(brand_id, created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_products_brand_created already exists';
END
GO

-- =====================================================
-- PRODUCT_VARIANTS TABLE - Additional Indexes
-- =====================================================

-- Index cho filter theo stock_quantity (Admin inventory management)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_variants_stock_quantity' AND object_id = OBJECT_ID('Product_Variants'))
BEGIN
    CREATE INDEX idx_variants_stock_quantity ON Product_Variants(stock_quantity);
    PRINT '  - Created index: idx_variants_stock_quantity on Product_Variants(stock_quantity)';
END
ELSE
BEGIN
    PRINT '  - Index idx_variants_stock_quantity already exists';
END
GO

-- Composite index cho product_id + stock_quantity (Admin: low stock alerts)
-- Lưu ý: Filtered index không thể sử dụng column reference trong WHERE clause
-- Sử dụng điều kiện đơn giản hơn với giá trị cụ thể hoặc chỉ filter is_active
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_variants_product_stock_low' AND object_id = OBJECT_ID('Product_Variants'))
BEGIN
    CREATE INDEX idx_variants_product_stock_low ON Product_Variants(product_id, stock_quantity) 
    WHERE is_active = 1 AND stock_quantity < 50;
    PRINT '  - Created index: idx_variants_product_stock_low on Product_Variants(product_id, stock_quantity)';
END
ELSE
BEGIN
    PRINT '  - Index idx_variants_product_stock_low already exists';
END
GO

-- Index cho sorting theo created_at (Admin variant list)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_variants_created_at' AND object_id = OBJECT_ID('Product_Variants'))
BEGIN
    CREATE INDEX idx_variants_created_at ON Product_Variants(created_at DESC);
    PRINT '  - Created index: idx_variants_created_at on Product_Variants(created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_variants_created_at already exists';
END
GO

-- Composite index cho product_id + is_active + stock_quantity (Admin: active variants with stock)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_variants_product_active_stock' AND object_id = OBJECT_ID('Product_Variants'))
BEGIN
    CREATE INDEX idx_variants_product_active_stock ON Product_Variants(product_id, is_active, stock_quantity);
    PRINT '  - Created index: idx_variants_product_active_stock on Product_Variants(product_id, is_active, stock_quantity)';
END
ELSE
BEGIN
    PRINT '  - Index idx_variants_product_active_stock already exists';
END
GO

-- =====================================================
-- ORDERS TABLE - Additional Indexes
-- =====================================================

-- Index cho sorting theo created_at (Admin order list - đã có nhưng đảm bảo)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_orders_created_at' AND object_id = OBJECT_ID('Orders'))
BEGIN
    CREATE INDEX idx_orders_created_at ON Orders(created_at DESC);
    PRINT '  - Created index: idx_orders_created_at on Orders(created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_orders_created_at already exists';
END
GO

-- Composite index cho status + created_at (Admin: filter by status, sort by date)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_orders_status_created_at' AND object_id = OBJECT_ID('Orders'))
BEGIN
    CREATE INDEX idx_orders_status_created_at ON Orders(status, created_at DESC);
    PRINT '  - Created index: idx_orders_status_created_at on Orders(status, created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_orders_status_created_at already exists';
END
GO

-- Composite index cho user_id + created_at (Admin: user orders sorted by date)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_orders_user_created_at' AND object_id = OBJECT_ID('Orders'))
BEGIN
    CREATE INDEX idx_orders_user_created_at ON Orders(user_id, created_at DESC) 
    WHERE user_id IS NOT NULL;
    PRINT '  - Created index: idx_orders_user_created_at on Orders(user_id, created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_orders_user_created_at already exists';
END
GO

-- =====================================================
-- USERS TABLE - Additional Indexes
-- =====================================================

-- Index cho search theo email (Admin user search)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_users_email' AND object_id = OBJECT_ID('Users'))
BEGIN
    CREATE INDEX idx_users_email ON Users(email);
    PRINT '  - Created index: idx_users_email on Users(email)';
END
ELSE
BEGIN
    PRINT '  - Index idx_users_email already exists (unique constraint)';
END
GO

-- Index cho filter theo role (Admin user management)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_users_role' AND object_id = OBJECT_ID('Users'))
BEGIN
    CREATE INDEX idx_users_role ON Users(role);
    PRINT '  - Created index: idx_users_role on Users(role)';
END
ELSE
BEGIN
    PRINT '  - Index idx_users_role already exists';
END
GO

-- Composite index cho role + is_active (Admin: active admins/users)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_users_role_active' AND object_id = OBJECT_ID('Users'))
BEGIN
    CREATE INDEX idx_users_role_active ON Users(role, is_active);
    PRINT '  - Created index: idx_users_role_active on Users(role, is_active)';
END
ELSE
BEGIN
    PRINT '  - Index idx_users_role_active already exists';
END
GO

-- =====================================================
-- REVIEWS TABLE - Additional Indexes
-- =====================================================

-- Index cho sorting theo created_at (Admin review list)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_reviews_created_at' AND object_id = OBJECT_ID('Reviews'))
BEGIN
    CREATE INDEX idx_reviews_created_at ON Reviews(created_at DESC);
    PRINT '  - Created index: idx_reviews_created_at on Reviews(created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_reviews_created_at already exists';
END
GO

-- Composite index cho is_approved + created_at (Admin: pending reviews)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_reviews_approved_created' AND object_id = OBJECT_ID('Reviews'))
BEGIN
    CREATE INDEX idx_reviews_approved_created ON Reviews(is_approved, created_at DESC);
    PRINT '  - Created index: idx_reviews_approved_created on Reviews(is_approved, created_at DESC)';
END
ELSE
BEGIN
    PRINT '  - Index idx_reviews_approved_created already exists';
END
GO

-- =====================================================
-- FULL-TEXT SEARCH - SQL Server Full-Text Indexes
-- =====================================================
-- Lưu ý: Full-text search cần catalog và full-text index
-- Chỉ tạo nếu chưa có

-- Kiểm tra xem có Full-Text Catalog chưa
IF NOT EXISTS (SELECT * FROM sys.fulltext_catalogs WHERE name = 'ftCatalog_Sneakery')
BEGIN
    CREATE FULLTEXT CATALOG ftCatalog_Sneakery;
    PRINT '  - Created Full-Text Catalog: ftCatalog_Sneakery';
END
ELSE
BEGIN
    PRINT '  - Full-Text Catalog ftCatalog_Sneakery already exists';
END
GO

-- Full-Text Index cho Products.name và Products.description
-- Tạo unique index cho Products.id nếu chưa có (yêu cầu cho full-text index)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'UQ_Products_Id_FullText' AND object_id = OBJECT_ID('Products'))
BEGIN
    -- Kiểm tra xem có unique constraint/index trên id chưa
    IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID('Products') AND is_unique = 1 AND is_primary_key = 1)
    BEGIN
        CREATE UNIQUE INDEX UQ_Products_Id_FullText ON Products(id);
        PRINT '  - Created unique index: UQ_Products_Id_FullText on Products(id)';
    END
END
GO

-- Full-Text Index cho Products.name và Products.description
IF NOT EXISTS (SELECT * FROM sys.fulltext_indexes WHERE object_id = OBJECT_ID('Products'))
BEGIN
    DECLARE @PKIndexName NVARCHAR(255);
    
    -- Lấy tên của PRIMARY KEY index
    SELECT TOP 1 @PKIndexName = i.name
    FROM sys.indexes i
    INNER JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
    INNER JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id
    WHERE i.object_id = OBJECT_ID('Products')
      AND i.is_primary_key = 1
      AND c.name = 'id';
    
    IF @PKIndexName IS NOT NULL
    BEGIN
        EXEC('CREATE FULLTEXT INDEX ON Products(name, description) KEY INDEX [' + @PKIndexName + '] ON ftCatalog_Sneakery WITH (CHANGE_TRACKING = AUTO)');
        PRINT '  - Created Full-Text Index on Products(name, description)';
    END
    ELSE
    BEGIN
        PRINT '  - Warning: Could not find PRIMARY KEY index for Products table';
    END
END
ELSE
BEGIN
    PRINT '  - Full-Text Index on Products already exists';
END
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH THEM INDEXES';
PRINT '=====================================================';
PRINT '';


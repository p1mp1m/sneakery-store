-- =====================================================
-- SNEAKERY E-COMMERCE - ALTER TABLES (MIGRATIONS)
-- =====================================================
-- File này chạy các migrations để cập nhật schema cho các tính năng mới
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT 'DANG CHAY MIGRATIONS...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- MIGRATIONS (Cập nhật schema cho các tính năng mới)
-- =====================================================

-- Migration: Allow NULL user_id in Orders table for POS orders
-- Date: 2025-11-07
-- Description: Cho phép user_id NULL để hỗ trợ POS orders (khách vãng lai)
PRINT 'Migration: Allow NULL user_id in Orders table for POS orders...';
GO

-- Bước 1: Drop foreign key constraint hiện tại nếu tồn tại
IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'fk_orders_user')
BEGIN
    ALTER TABLE Orders DROP CONSTRAINT fk_orders_user;
    PRINT '  - Dropped foreign key constraint fk_orders_user';
END
ELSE
BEGIN
    PRINT '  - Foreign key constraint fk_orders_user not found (may be new schema)';
END
GO

-- Bước 2: ALTER TABLE để cho phép user_id NULL (nếu chưa NULL)
IF EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Orders' 
    AND COLUMN_NAME = 'user_id' 
    AND IS_NULLABLE = 'NO'
)
BEGIN
    ALTER TABLE Orders ALTER COLUMN user_id BIGINT NULL;
    PRINT '  - Altered user_id column to allow NULL';
END
ELSE
BEGIN
    PRINT '  - user_id column already allows NULL (may be new schema)';
END
GO

-- Bước 3: Tạo lại foreign key constraint với ON DELETE SET NULL
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'fk_orders_user')
BEGIN
    ALTER TABLE Orders
    ADD CONSTRAINT fk_orders_user 
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL;
    PRINT '  - Created foreign key constraint fk_orders_user with ON DELETE SET NULL';
END
ELSE
BEGIN
    PRINT '  - Foreign key constraint fk_orders_user already exists';
END
GO

PRINT '  - Migration completed successfully!';
PRINT '  - Orders table now allows NULL user_id for POS orders (walk-in customers)';
GO

-- Migration: Add main_image_url column to Products table
-- Date: 2025-11-07
-- Description: Thêm cột main_image_url vào bảng Products nếu chưa tồn tại
PRINT 'Migration: Add main_image_url column to Products table...';
GO

IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Products' 
    AND COLUMN_NAME = 'main_image_url'
)
BEGIN
    ALTER TABLE Products ADD main_image_url NVARCHAR(500) NULL;
    PRINT '  - Added main_image_url column to Products table';
END
ELSE
BEGIN
    PRINT '  - main_image_url column already exists in Products table';
END
GO

PRINT '  - Migration completed successfully!';
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH CHAY MIGRATIONS!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da chay thanh cong tat ca migrations.';
PRINT 'Bước tiếp theo: Chạy file 08_ADD_INDEXES_COMPOSITE.sql để thêm composite indexes.';
PRINT '';


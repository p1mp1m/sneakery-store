-- =====================================================
-- MIGRATION: ADD ADDRESS COLUMN TO USERS TABLE
-- =====================================================
-- Description: Thêm cột address vào bảng Users để lưu địa chỉ khách hàng
-- Date: 2025-01-XX
-- =====================================================

USE sneakery_db;
GO

-- Kiểm tra xem cột address đã tồn tại chưa
IF NOT EXISTS (
    SELECT 1 
    FROM sys.columns 
    WHERE object_id = OBJECT_ID(N'Users') 
    AND name = 'address'
)
BEGIN
    -- Thêm cột address vào bảng Users
    ALTER TABLE Users
    ADD address NVARCHAR(500) NULL;
    
    PRINT '  - Added column "address" to Users table';
END
ELSE
BEGIN
    PRINT '  - Column "address" already exists in Users table';
END
GO


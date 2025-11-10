-- =====================================================
-- Migration: Add price_range column to Products table
-- Description: Thêm cột price_range để lưu khoảng giá (JSON format)
-- Date: 2024
-- =====================================================

-- Thêm cột price_range vào bảng Products
ALTER TABLE Products
ADD price_range NVARCHAR(500) NULL;

-- Thêm comment cho cột
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Khoảng giá sản phẩm dạng JSON: {"from": 1000000, "to": 5000000}', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'Products', 
    @level2type = N'COLUMN', @level2name = N'price_range';

-- Tạo index để hỗ trợ tìm kiếm theo khoảng giá (optional, có thể dùng JSON functions)
-- CREATE INDEX idx_products_price_range ON Products(price_range);
GO

-- =====================================================
-- Migration completed
-- =====================================================


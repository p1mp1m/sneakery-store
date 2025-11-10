-- =====================================================
-- SNEAKERY E-COMMERCE - CREATE TRIGGERS
-- =====================================================
-- File này tạo tất cả triggers cho database
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT 'DANG TAO TRIGGERS...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- TRIGGERS
-- =====================================================

CREATE TRIGGER trg_Products_UpdateTimestamp
ON Products
AFTER UPDATE
AS
BEGIN
    UPDATE Products
    SET updated_at = GETDATE()
    FROM Products p
    INNER JOIN inserted i ON p.id = i.id;
END;
GO

CREATE TRIGGER trg_ProductVariants_InventoryLog
ON Product_Variants
AFTER UPDATE
AS
BEGIN
    IF UPDATE(stock_quantity)
    BEGIN
        INSERT INTO Inventory_Logs (variant_id, change_type, quantity_before, quantity_change, quantity_after, reference_type)
        SELECT 
            i.id,
            'adjustment',
            d.stock_quantity,
            i.stock_quantity - d.stock_quantity,
            i.stock_quantity,
            'manual'
        FROM inserted i
        INNER JOIN deleted d ON i.id = d.id
        WHERE i.stock_quantity <> d.stock_quantity;
    END
END;
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH TAO TRIGGERS!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da tao thanh cong 2 triggers.';
PRINT 'Bước tiếp theo: Chạy file 07_ALTER_TABLES.sql để chạy migrations.';
PRINT '';


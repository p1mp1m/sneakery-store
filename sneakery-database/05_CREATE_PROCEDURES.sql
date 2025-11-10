-- =====================================================
-- SNEAKERY E-COMMERCE - CREATE STORED PROCEDURES
-- =====================================================
-- File này tạo tất cả stored procedures cho admin API
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT 'DANG TAO STORED PROCEDURES...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- STORED PROCEDURES FOR ADMIN API
-- =====================================================

CREATE PROCEDURE sp_UpdateProductRating
    @ProductId BIGINT
AS
BEGIN
    UPDATE Products
    SET 
        avg_rating = (
            SELECT AVG(CAST(rating AS DECIMAL(3,2)))
            FROM Reviews
            WHERE product_id = @ProductId AND is_approved = 1
        ),
        review_count = (
            SELECT COUNT(*)
            FROM Reviews
            WHERE product_id = @ProductId AND is_approved = 1
        )
    WHERE id = @ProductId;
END;
GO

CREATE PROCEDURE sp_GenerateOrderNumber
    @OrderNumber VARCHAR(50) OUTPUT
AS
BEGIN
    DECLARE @Date VARCHAR(8) = FORMAT(GETDATE(), 'yyyyMMdd');
    DECLARE @Sequence INT;
    
    SELECT @Sequence = ISNULL(MAX(CAST(RIGHT(order_number, 4) AS INT)), 0) + 1
    FROM Orders
    WHERE order_number LIKE 'ORD-' + @Date + '-%';
    
    SET @OrderNumber = 'ORD-' + @Date + '-' + FORMAT(@Sequence, '0000');
END;
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH TAO STORED PROCEDURES!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da tao thanh cong 2 stored procedures.';
PRINT 'Bước tiếp theo: Chạy file 06_CREATE_TRIGGERS.sql để tạo triggers.';
PRINT '';


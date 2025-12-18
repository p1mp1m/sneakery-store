/* =====================================================================
   V35__align_price_base_with_sale_price.sql
   ---------------------------------------------------------------------
   PURPOSE
   ---------------------------------------------------------------------
   - Áp dụng nghiệp vụ mới: price_sale >= price_base
   - GIỮ NGUYÊN price_sale (đã được sử dụng trong order cũ)
   - Điều chỉnh price_base cho phù hợp với rule mới
   - Thay đổi CHECK CONSTRAINT theo nghiệp vụ mới
   ---------------------------------------------------------------------
   DATABASE : SQL Server
   FLYWAY   : Versioned migration (UP only)
   SAFETY   : Transaction-safe, production-ready
   ===================================================================== */

BEGIN TRY
BEGIN TRANSACTION;

    ------------------------------------------------------------
    -- 1. VALIDATE DATA CƠ BẢN
    --    price_sale là giá bán thực tế → BẮT BUỘC > 0
    ------------------------------------------------------------
    IF EXISTS (
        SELECT 1
        FROM product_variants
        WHERE price_sale IS NULL
           OR price_sale <= 0
    )
BEGIN
        THROW 51001,
              'V35 migration aborted: price_sale must be NOT NULL and > 0 for all product_variants',
              1;
END;

    ------------------------------------------------------------
    -- 2. ALIGN price_base THEO price_sale
    --    KHÔNG ĐỤNG price_sale
    --    - price_base NULL      -> set = price_sale
    --    - price_base <= 0      -> set = price_sale
    --    - price_base > sale    -> set = price_sale
    ------------------------------------------------------------
UPDATE product_variants
SET price_base = price_sale
WHERE price_base IS NULL
   OR price_base <= 0
   OR price_base > price_sale;

------------------------------------------------------------
-- 3. VALIDATE SAU KHI ALIGN
------------------------------------------------------------
IF EXISTS (
        SELECT 1
        FROM product_variants
        WHERE price_base IS NULL
           OR price_base <= 0
           OR price_base > price_sale
    )
BEGIN
        THROW 51002,
              'V35 migration aborted: price_base still invalid after alignment',
              1;
END;

    ------------------------------------------------------------
    -- 4. DROP CHECK CONSTRAINT CŨ
    --    Nghiệp vụ cũ: price_sale <= price_base
    ------------------------------------------------------------
    IF EXISTS (
        SELECT 1
        FROM sys.check_constraints
        WHERE name = 'CK_ProductVariants_PriceSale'
    )
BEGIN
ALTER TABLE product_variants
DROP CONSTRAINT CK_ProductVariants_PriceSale;
END;

    ------------------------------------------------------------
    -- 5. ADD CHECK CONSTRAINT MỚI
    --    Nghiệp vụ mới: price_sale >= price_base
    ------------------------------------------------------------
ALTER TABLE product_variants
    ADD CONSTRAINT CK_ProductVariants_PriceSale_GreaterOrEqual_Base
        CHECK (
            price_base > 0
                AND price_sale > 0
                AND price_sale >= price_base
            );

COMMIT TRANSACTION;
END TRY
BEGIN CATCH
ROLLBACK TRANSACTION;

    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
    DECLARE @ErrorState INT = ERROR_STATE();

    RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
END CATCH;

    -- =====================================================
    -- SNEAKERY E-COMMERCE - ADD CHECK CONSTRAINTS FOR BUSINESS RULES
    -- =====================================================
    -- File này thêm các check constraints để đảm bảo data integrity
    -- và enforce business rules ở database level
    -- =====================================================
    -- 
    -- LƯU Ý: Chạy file này SAU KHI đã chạy 1_CREATE_SCHEMA.sql
    -- =====================================================

    USE sneakery_db;
    GO

    SET NOCOUNT ON;

    PRINT '';
    PRINT '=====================================================';
    PRINT 'DANG THEM CHECK CONSTRAINTS...';
    PRINT '=====================================================';
    PRINT '';

    -- =====================================================
    -- PRODUCT_VARIANTS TABLE - Price and Stock Constraints
    -- =====================================================

    -- Constraint: price_sale phải <= price_base (không được bán giá cao hơn giá gốc)
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_ProductVariants_PriceSale')
    BEGIN
        ALTER TABLE Product_Variants
        ADD CONSTRAINT CK_ProductVariants_PriceSale 
        CHECK (price_sale IS NULL OR price_sale <= price_base);
        PRINT '  - Added constraint: CK_ProductVariants_PriceSale (price_sale <= price_base)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_ProductVariants_PriceSale already exists';
    END
    GO

    -- Constraint: price_base phải > 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_ProductVariants_PriceBase')
    BEGIN
        ALTER TABLE Product_Variants
        ADD CONSTRAINT CK_ProductVariants_PriceBase 
        CHECK (price_base > 0);
        PRINT '  - Added constraint: CK_ProductVariants_PriceBase (price_base > 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_ProductVariants_PriceBase already exists';
    END
    GO

    -- Constraint: price_sale phải > 0 nếu không NULL
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_ProductVariants_PriceSalePositive')
    BEGIN
        ALTER TABLE Product_Variants
        ADD CONSTRAINT CK_ProductVariants_PriceSalePositive 
        CHECK (price_sale IS NULL OR price_sale > 0);
        PRINT '  - Added constraint: CK_ProductVariants_PriceSalePositive (price_sale > 0 if not NULL)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_ProductVariants_PriceSalePositive already exists';
    END
    GO

    -- Constraint: stock_quantity phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_ProductVariants_StockQuantity')
    BEGIN
        ALTER TABLE Product_Variants
        ADD CONSTRAINT CK_ProductVariants_StockQuantity 
        CHECK (stock_quantity >= 0);
        PRINT '  - Added constraint: CK_ProductVariants_StockQuantity (stock_quantity >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_ProductVariants_StockQuantity already exists';
    END
    GO

    -- Constraint: low_stock_threshold phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_ProductVariants_LowStockThreshold')
    BEGIN
        ALTER TABLE Product_Variants
        ADD CONSTRAINT CK_ProductVariants_LowStockThreshold 
        CHECK (low_stock_threshold >= 0);
        PRINT '  - Added constraint: CK_ProductVariants_LowStockThreshold (low_stock_threshold >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_ProductVariants_LowStockThreshold already exists';
    END
    GO

    -- Constraint: cost_price phải >= 0 nếu không NULL
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_ProductVariants_CostPrice')
    BEGIN
        ALTER TABLE Product_Variants
        ADD CONSTRAINT CK_ProductVariants_CostPrice 
        CHECK (cost_price IS NULL OR cost_price >= 0);
        PRINT '  - Added constraint: CK_ProductVariants_CostPrice (cost_price >= 0 if not NULL)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_ProductVariants_CostPrice already exists';
    END
    GO

-- =====================================================
-- COUPONS TABLE - Discount Constraints
-- =====================================================

-- Constraint: discount_value phải >= 0
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Coupons_DiscountValue')
BEGIN
    ALTER TABLE Coupons
    ADD CONSTRAINT CK_Coupons_DiscountValue 
    CHECK (discount_value >= 0);
    PRINT '  - Added constraint: CK_Coupons_DiscountValue (discount_value >= 0)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_Coupons_DiscountValue already exists';
END
GO

-- Constraint: discount_value phải <= 100 nếu discount_type = 'percent'
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Coupons_DiscountValuePercent')
BEGIN
    ALTER TABLE Coupons
    ADD CONSTRAINT CK_Coupons_DiscountValuePercent 
    CHECK (discount_type != 'percent' OR discount_value <= 100);
    PRINT '  - Added constraint: CK_Coupons_DiscountValuePercent (discount_value <= 100 if percent)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_Coupons_DiscountValuePercent already exists';
END
GO

-- Constraint: min_order_amount phải >= 0 nếu không NULL
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Coupons_MinOrderAmount')
BEGIN
    ALTER TABLE Coupons
    ADD CONSTRAINT CK_Coupons_MinOrderAmount 
    CHECK (min_order_amount IS NULL OR min_order_amount >= 0);
    PRINT '  - Added constraint: CK_Coupons_MinOrderAmount (min_order_amount >= 0 if not NULL)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_Coupons_MinOrderAmount already exists';
END
GO

-- Constraint: max_discount_amount phải >= 0 nếu không NULL
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Coupons_MaxDiscountAmount')
BEGIN
    ALTER TABLE Coupons
    ADD CONSTRAINT CK_Coupons_MaxDiscountAmount 
    CHECK (max_discount_amount IS NULL OR max_discount_amount >= 0);
    PRINT '  - Added constraint: CK_Coupons_MaxDiscountAmount (max_discount_amount >= 0 if not NULL)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_Coupons_MaxDiscountAmount already exists';
END
GO

-- Constraint: max_uses phải >= 0 nếu không NULL
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Coupons_MaxUses')
BEGIN
    ALTER TABLE Coupons
    ADD CONSTRAINT CK_Coupons_MaxUses 
    CHECK (max_uses IS NULL OR max_uses >= 0);
    PRINT '  - Added constraint: CK_Coupons_MaxUses (max_uses >= 0 if not NULL)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_Coupons_MaxUses already exists';
END
GO

-- Constraint: uses_count phải >= 0
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Coupons_UsesCount')
BEGIN
    ALTER TABLE Coupons
    ADD CONSTRAINT CK_Coupons_UsesCount 
    CHECK (uses_count >= 0);
    PRINT '  - Added constraint: CK_Coupons_UsesCount (uses_count >= 0)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_Coupons_UsesCount already exists';
END
GO

-- Constraint: max_uses_per_user phải >= 0 nếu không NULL
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Coupons_MaxUsesPerUser')
BEGIN
    ALTER TABLE Coupons
    ADD CONSTRAINT CK_Coupons_MaxUsesPerUser 
    CHECK (max_uses_per_user IS NULL OR max_uses_per_user >= 0);
    PRINT '  - Added constraint: CK_Coupons_MaxUsesPerUser (max_uses_per_user >= 0 if not NULL)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_Coupons_MaxUsesPerUser already exists';
END
GO

-- Constraint: start_at phải <= end_at
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Coupons_DateRange')
BEGIN
    ALTER TABLE Coupons
    ADD CONSTRAINT CK_Coupons_DateRange 
    CHECK (start_at IS NULL OR end_at IS NULL OR start_at <= end_at);
    PRINT '  - Added constraint: CK_Coupons_DateRange (start_at <= end_at)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_Coupons_DateRange already exists';
END
GO

-- =====================================================
-- FLASH_SALES TABLE - Discount Constraints
-- =====================================================

-- Constraint: discount_percent phải >= 1 AND <= 100
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_FlashSales_DiscountPercent')
BEGIN
    ALTER TABLE Flash_Sales
    ADD CONSTRAINT CK_FlashSales_DiscountPercent 
    CHECK (discount_percent >= 1 AND discount_percent <= 100);
    PRINT '  - Added constraint: CK_FlashSales_DiscountPercent (1 <= discount_percent <= 100)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_FlashSales_DiscountPercent already exists';
END
GO

-- Constraint: start_time phải <= end_time
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_FlashSales_TimeRange')
BEGIN
    ALTER TABLE Flash_Sales
    ADD CONSTRAINT CK_FlashSales_TimeRange 
    CHECK (start_time <= end_time);
    PRINT '  - Added constraint: CK_FlashSales_TimeRange (start_time <= end_time)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_FlashSales_TimeRange already exists';
END
GO

    -- =====================================================
    -- ORDERS TABLE - Amount Constraints
    -- =====================================================

    -- Constraint: subtotal phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Orders_Subtotal')
    BEGIN
        ALTER TABLE Orders
        ADD CONSTRAINT CK_Orders_Subtotal 
        CHECK (subtotal >= 0);
        PRINT '  - Added constraint: CK_Orders_Subtotal (subtotal >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Orders_Subtotal already exists';
    END
    GO

    -- Constraint: shipping_fee phải >= 0 nếu không NULL
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Orders_ShippingFee')
    BEGIN
        ALTER TABLE Orders
        ADD CONSTRAINT CK_Orders_ShippingFee 
        CHECK (shipping_fee IS NULL OR shipping_fee >= 0);
        PRINT '  - Added constraint: CK_Orders_ShippingFee (shipping_fee >= 0 if not NULL)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Orders_ShippingFee already exists';
    END
    GO

    -- Constraint: discount_amount phải >= 0 nếu không NULL
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Orders_DiscountAmount')
    BEGIN
        ALTER TABLE Orders
        ADD CONSTRAINT CK_Orders_DiscountAmount 
        CHECK (discount_amount IS NULL OR discount_amount >= 0);
        PRINT '  - Added constraint: CK_Orders_DiscountAmount (discount_amount >= 0 if not NULL)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Orders_DiscountAmount already exists';
    END
    GO

    -- Constraint: tax_amount phải >= 0 nếu không NULL
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Orders_TaxAmount')
    BEGIN
        ALTER TABLE Orders
        ADD CONSTRAINT CK_Orders_TaxAmount 
        CHECK (tax_amount IS NULL OR tax_amount >= 0);
        PRINT '  - Added constraint: CK_Orders_TaxAmount (tax_amount >= 0 if not NULL)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Orders_TaxAmount already exists';
    END
    GO

    -- Constraint: total_amount phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Orders_TotalAmount')
    BEGIN
        ALTER TABLE Orders
        ADD CONSTRAINT CK_Orders_TotalAmount 
        CHECK (total_amount >= 0);
        PRINT '  - Added constraint: CK_Orders_TotalAmount (total_amount >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Orders_TotalAmount already exists';
    END
    GO

    -- Constraint: points_earned phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Orders_PointsEarned')
    BEGIN
        ALTER TABLE Orders
        ADD CONSTRAINT CK_Orders_PointsEarned 
        CHECK (points_earned >= 0);
        PRINT '  - Added constraint: CK_Orders_PointsEarned (points_earned >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Orders_PointsEarned already exists';
    END
    GO

    -- Constraint: points_used phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Orders_PointsUsed')
    BEGIN
        ALTER TABLE Orders
        ADD CONSTRAINT CK_Orders_PointsUsed 
        CHECK (points_used >= 0);
        PRINT '  - Added constraint: CK_Orders_PointsUsed (points_used >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Orders_PointsUsed already exists';
    END
    GO

    -- =====================================================
    -- ORDER_DETAILS TABLE - Quantity and Price Constraints
    -- =====================================================

    -- Constraint: quantity phải > 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_OrderDetails_Quantity')
    BEGIN
        ALTER TABLE Order_Details
        ADD CONSTRAINT CK_OrderDetails_Quantity 
        CHECK (quantity > 0);
        PRINT '  - Added constraint: CK_OrderDetails_Quantity (quantity > 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_OrderDetails_Quantity already exists';
    END
    GO

    -- Constraint: unit_price phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_OrderDetails_UnitPrice')
    BEGIN
        ALTER TABLE Order_Details
        ADD CONSTRAINT CK_OrderDetails_UnitPrice 
        CHECK (unit_price >= 0);
        PRINT '  - Added constraint: CK_OrderDetails_UnitPrice (unit_price >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_OrderDetails_UnitPrice already exists';
    END
    GO

-- Constraint: total_price phải >= 0
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_OrderDetails_TotalPrice')
BEGIN
    ALTER TABLE Order_Details
    ADD CONSTRAINT CK_OrderDetails_TotalPrice 
    CHECK (total_price >= 0);
    PRINT '  - Added constraint: CK_OrderDetails_TotalPrice (total_price >= 0)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_OrderDetails_TotalPrice already exists';
END
GO

    -- =====================================================
    -- REVIEWS TABLE - Rating Constraints
    -- =====================================================

    -- Constraint: rating phải >= 1 AND <= 5
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Reviews_Rating')
    BEGIN
        ALTER TABLE Reviews
        ADD CONSTRAINT CK_Reviews_Rating 
        CHECK (rating >= 1 AND rating <= 5);
        PRINT '  - Added constraint: CK_Reviews_Rating (1 <= rating <= 5)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Reviews_Rating already exists';
    END
    GO

    -- =====================================================
    -- PRODUCTS TABLE - Rating Constraints
    -- =====================================================

    -- Constraint: avg_rating phải >= 0 AND <= 5 nếu không NULL
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Products_AvgRating')
    BEGIN
        ALTER TABLE Products
        ADD CONSTRAINT CK_Products_AvgRating 
        CHECK (avg_rating IS NULL OR (avg_rating >= 0 AND avg_rating <= 5));
        PRINT '  - Added constraint: CK_Products_AvgRating (0 <= avg_rating <= 5 if not NULL)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Products_AvgRating already exists';
    END
    GO

    -- Constraint: review_count phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Products_ReviewCount')
    BEGIN
        ALTER TABLE Products
        ADD CONSTRAINT CK_Products_ReviewCount 
        CHECK (review_count >= 0);
        PRINT '  - Added constraint: CK_Products_ReviewCount (review_count >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Products_ReviewCount already exists';
    END
    GO

    -- Constraint: view_count phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Products_ViewCount')
    BEGIN
        ALTER TABLE Products
        ADD CONSTRAINT CK_Products_ViewCount 
        CHECK (view_count >= 0);
        PRINT '  - Added constraint: CK_Products_ViewCount (view_count >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Products_ViewCount already exists';
    END
    GO

    -- Constraint: order_count phải >= 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_Products_OrderCount')
    BEGIN
        ALTER TABLE Products
        ADD CONSTRAINT CK_Products_OrderCount 
        CHECK (order_count >= 0);
        PRINT '  - Added constraint: CK_Products_OrderCount (order_count >= 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_Products_OrderCount already exists';
    END
    GO

-- =====================================================
-- LOYALTY_POINTS TABLE - Points Constraints
-- =====================================================

-- Xóa constraint cũ nếu tồn tại (nếu đã tạo với điều kiện points > 0)
IF EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_LoyaltyPoints_Points')
BEGIN
    ALTER TABLE Loyalty_Points
    DROP CONSTRAINT CK_LoyaltyPoints_Points;
    PRINT '  - Dropped old constraint: CK_LoyaltyPoints_Points';
END
GO

-- Constraint: points không được = 0
-- earn: points > 0 (tích điểm)
-- redeem/expire: points < 0 (sử dụng/hết hạn điểm)
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_LoyaltyPoints_Points')
BEGIN
    ALTER TABLE Loyalty_Points
    ADD CONSTRAINT CK_LoyaltyPoints_Points 
    CHECK (points != 0);
    PRINT '  - Added constraint: CK_LoyaltyPoints_Points (points != 0)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_LoyaltyPoints_Points already exists';
END
GO

-- Constraint: points phải > 0 nếu transaction_type = 'earn'
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_LoyaltyPoints_PointsEarn')
BEGIN
    ALTER TABLE Loyalty_Points
    ADD CONSTRAINT CK_LoyaltyPoints_PointsEarn 
    CHECK (transaction_type != 'earn' OR points > 0);
    PRINT '  - Added constraint: CK_LoyaltyPoints_PointsEarn (points > 0 if earn)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_LoyaltyPoints_PointsEarn already exists';
END
GO

-- Constraint: points phải < 0 nếu transaction_type = 'redeem' hoặc 'expire'
IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_LoyaltyPoints_PointsRedeemExpire')
BEGIN
    ALTER TABLE Loyalty_Points
    ADD CONSTRAINT CK_LoyaltyPoints_PointsRedeemExpire 
    CHECK (transaction_type NOT IN ('redeem', 'expire') OR points < 0);
    PRINT '  - Added constraint: CK_LoyaltyPoints_PointsRedeemExpire (points < 0 if redeem/expire)';
END
ELSE
BEGIN
    PRINT '  - Constraint CK_LoyaltyPoints_PointsRedeemExpire already exists';
END
GO

    -- =====================================================
    -- CART_ITEMS TABLE - Quantity Constraints
    -- =====================================================

    -- Constraint: quantity phải > 0
    IF NOT EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK_CartItems_Quantity')
    BEGIN
        ALTER TABLE Cart_Items
        ADD CONSTRAINT CK_CartItems_Quantity 
        CHECK (quantity > 0);
        PRINT '  - Added constraint: CK_CartItems_Quantity (quantity > 0)';
    END
    ELSE
    BEGIN
        PRINT '  - Constraint CK_CartItems_Quantity already exists';
    END
    GO

    PRINT '';
    PRINT '=====================================================';
    PRINT 'HOAN THANH THEM CHECK CONSTRAINTS';
    PRINT '=====================================================';
    PRINT '';


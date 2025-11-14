-- =====================================================
-- SNEAKERY E-COMMERCE - INSERT DATA PART 2
-- =====================================================
-- File này insert dữ liệu giao dịch: Orders, Payments, Reviews,
-- Carts, Wishlists, Loyalty Points, Notifications, etc.
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT N'DANG INSERT DU LIEU GIAO DICH (PART 2)...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 1. ORDERS (50 orders)
-- =====================================================
PRINT 'Inserting Orders...';

DECLARE @OrderCounter INT = 1;
DECLARE @UserId BIGINT;
DECLARE @VariantId BIGINT;
DECLARE @AddressId BIGINT;
DECLARE @CouponId INT;
DECLARE @OrderId BIGINT;
DECLARE @OrderNumber NVARCHAR(50);
DECLARE @Subtotal DECIMAL(18,2);
DECLARE @ShippingFee DECIMAL(18,2);
DECLARE @DiscountAmount DECIMAL(18,2);
DECLARE @TotalAmount DECIMAL(18,2);
DECLARE @OrderStatus NVARCHAR(50);
DECLARE @OrderDate DATETIME2;

-- Get random users
DECLARE @UserIds TABLE (id BIGINT);
INSERT INTO @UserIds SELECT TOP 20 id FROM Users WHERE role = 'USER' ORDER BY NEWID();

-- Get random addresses
DECLARE @AddressIds TABLE (id BIGINT);
INSERT INTO @AddressIds SELECT TOP 30 id FROM Addresses ORDER BY NEWID();

-- Get coupons
DECLARE @CouponIds TABLE (id INT);
INSERT INTO @CouponIds SELECT id FROM Coupons WHERE is_active = 1;

-- Create 50 orders
WHILE @OrderCounter <= 50
BEGIN
    -- Random user (80% have user, 20% are POS orders)
    IF RAND() > 0.2
    BEGIN
        SELECT TOP 1 @UserId = id FROM @UserIds ORDER BY NEWID();
        SELECT TOP 1 @AddressId = id FROM Addresses WHERE user_id = @UserId;
        IF @AddressId IS NULL
        BEGIN
            SELECT TOP 1 @AddressId = id FROM Addresses ORDER BY NEWID();
        END;
    END
    ELSE
    BEGIN
        SET @UserId = NULL;
        SELECT TOP 1 @AddressId = id FROM Addresses ORDER BY NEWID();
    END;
    
    -- Random coupon (30% chance)
    IF RAND() > 0.7 AND EXISTS (SELECT 1 FROM @CouponIds)
    BEGIN
        SELECT TOP 1 @CouponId = id FROM @CouponIds ORDER BY NEWID();
    END
    ELSE
    BEGIN
        SET @CouponId = NULL;
    END;
    
    -- Generate order number (unique)
    DECLARE @DateStr NVARCHAR(8) = FORMAT(DATEADD(day, -RAND() * 90, GETDATE()), 'yyyyMMdd');
    DECLARE @Seq INT;
    DECLARE @MaxSeq INT;
    
    -- Get max sequence for this date
    SELECT @MaxSeq = ISNULL(MAX(CAST(RIGHT(order_number, 4) AS INT)), 0)
    FROM Orders
    WHERE order_number LIKE 'ORD-' + @DateStr + '-%';
    
    SET @Seq = @MaxSeq + @OrderCounter;
    
    -- Ensure unique order number
    SET @OrderNumber = 'ORD-' + @DateStr + '-' + FORMAT(@Seq, '0000');
    
    -- Check if exists, if yes, add more to sequence
    WHILE EXISTS (SELECT 1 FROM Orders WHERE order_number = @OrderNumber)
    BEGIN
        SET @Seq = @Seq + 1;
        SET @OrderNumber = 'ORD-' + @DateStr + '-' + FORMAT(@Seq, '0000');
    END;
    
    -- Random order date (last 90 days)
    SET @OrderDate = DATEADD(day, -RAND() * 90, GETDATE());
    
    -- Random status
    DECLARE @StatusRand FLOAT = RAND();
    IF @StatusRand < 0.1 SET @OrderStatus = 'pending';
    ELSE IF @StatusRand < 0.2 SET @OrderStatus = 'confirmed';
    ELSE IF @StatusRand < 0.3 SET @OrderStatus = 'processing';
    ELSE IF @StatusRand < 0.4 SET @OrderStatus = 'packed';
    ELSE IF @StatusRand < 0.6 SET @OrderStatus = 'shipped';
    ELSE IF @StatusRand < 0.85 SET @OrderStatus = 'delivered';
    ELSE IF @StatusRand < 0.95 SET @OrderStatus = 'cancelled';
    ELSE SET @OrderStatus = 'refunded';
    
    -- Calculate amounts (simplified)
    SET @Subtotal = 2000000 + (RAND() * 3000000);
    SET @ShippingFee = CASE WHEN RAND() > 0.3 THEN 30000 ELSE 0 END;
    
    IF @CouponId IS NOT NULL
    BEGIN
        DECLARE @CouponValue DECIMAL(18,2);
        DECLARE @CouponType NVARCHAR(20);
        SELECT @CouponValue = discount_value, @CouponType = discount_type FROM Coupons WHERE id = @CouponId;
        
        IF @CouponType = 'percent'
            SET @DiscountAmount = @Subtotal * @CouponValue / 100;
        ELSE
            SET @DiscountAmount = @CouponValue;
    END
    ELSE
    BEGIN
        SET @DiscountAmount = 0;
    END;
    
    SET @TotalAmount = @Subtotal + @ShippingFee - @DiscountAmount;
    
    -- Insert order
    INSERT INTO Orders (user_id, coupon_id, address_shipping_id, address_billing_id, order_number, subtotal, shipping_fee, discount_amount, tax_amount, total_amount, points_earned, points_used, status, shipping_method, tracking_number, estimated_delivery_at, delivered_at, created_at)
    VALUES (@UserId, @CouponId, @AddressId, @AddressId, @OrderNumber, @Subtotal, @ShippingFee, @DiscountAmount, 0, @TotalAmount, 
            CAST(@TotalAmount / 10000 AS INT), 0, @OrderStatus, 
            CASE WHEN RAND() > 0.5 THEN 'Standard' ELSE 'Express' END,
            CASE WHEN @OrderStatus IN ('shipped', 'delivered') THEN 'TRACK' + FORMAT(@OrderCounter, '000000') ELSE NULL END,
            CASE WHEN @OrderStatus IN ('shipped', 'delivered') THEN DATEADD(day, 3, @OrderDate) ELSE NULL END,
            CASE WHEN @OrderStatus = 'delivered' THEN DATEADD(day, 2 + RAND() * 3, @OrderDate) ELSE NULL END,
            @OrderDate);
    
    SET @OrderId = SCOPE_IDENTITY();
    
    -- Create order details (2-4 items per order)
    DECLARE @ItemCount INT = 2 + CAST(RAND() * 3 AS INT);
    DECLARE @ItemCounter INT = 1;
    
    WHILE @ItemCounter <= @ItemCount
    BEGIN
        -- Get random variant
        SELECT TOP 1 @VariantId = id FROM Product_Variants WHERE is_active = 1 ORDER BY NEWID();
        
        DECLARE @VariantPrice DECIMAL(18,2);
        DECLARE @VariantQuantity INT = 1 + CAST(RAND() * 2 AS INT);
        DECLARE @ProductName NVARCHAR(255);
        DECLARE @VariantSku NVARCHAR(100);
        DECLARE @VariantSize NVARCHAR(20);
        DECLARE @VariantColor NVARCHAR(50);
        
        SELECT @VariantPrice = ISNULL(price_sale, price_base),
               @VariantSku = sku,
               @VariantSize = size,
               @VariantColor = color
        FROM Product_Variants WHERE id = @VariantId;
        
        SELECT @ProductName = p.name
        FROM Products p
        INNER JOIN Product_Variants pv ON p.id = pv.product_id
        WHERE pv.id = @VariantId;
        
        INSERT INTO Order_Details (order_id, variant_id, quantity, unit_price, product_name, variant_sku, size, color, total_price)
        VALUES (@OrderId, @VariantId, @VariantQuantity, @VariantPrice, @ProductName, @VariantSku, @VariantSize, @VariantColor, @VariantPrice * @VariantQuantity);
        
        SET @ItemCounter = @ItemCounter + 1;
    END;
    
    -- Create order status history
    INSERT INTO Order_Status_Histories (order_id, status, note, changed_by, created_at)
    VALUES (@OrderId, @OrderStatus, 'Order ' + @OrderStatus, @UserId, @OrderDate);
    
    -- Create payment
    DECLARE @PaymentMethod NVARCHAR(50);
    DECLARE @PaymentStatus NVARCHAR(50);
    DECLARE @PaymentRand FLOAT = RAND();
    
    IF @PaymentRand < 0.3 SET @PaymentMethod = 'cod';
    ELSE IF @PaymentRand < 0.6 SET @PaymentMethod = 'vnpay';
    ELSE IF @PaymentRand < 0.8 SET @PaymentMethod = 'momo';
    ELSE SET @PaymentMethod = 'zalopay';
    
    IF @OrderStatus IN ('delivered', 'shipped', 'processing', 'packed')
        SET @PaymentStatus = 'completed';
    ELSE IF @OrderStatus = 'cancelled'
        SET @PaymentStatus = 'failed';
    ELSE
        SET @PaymentStatus = 'pending';
    
    INSERT INTO Payments (order_id, payment_method, amount, status, transaction_id, paid_at, created_at)
    VALUES (@OrderId, @PaymentMethod, @TotalAmount, @PaymentStatus,
            CASE WHEN @PaymentStatus = 'completed' THEN 'TXN' + FORMAT(@OrderCounter, '000000') ELSE NULL END,
            CASE WHEN @PaymentStatus = 'completed' THEN DATEADD(minute, 5, @OrderDate) ELSE NULL END,
            @OrderDate);
    
    SET @OrderCounter = @OrderCounter + 1;
END;
GO

PRINT '  - Inserted 50 orders with details, status histories, and payments';
GO

-- =====================================================
-- 2. REVIEWS (30 reviews)
-- =====================================================
PRINT 'Inserting Reviews...';

DECLARE @ReviewCounter INT = 1;
DECLARE @ReviewProductId BIGINT;
DECLARE @ReviewUserId BIGINT;
DECLARE @ReviewOrderId BIGINT;
DECLARE @ReviewRating INT;
DECLARE @ReviewTitle NVARCHAR(255);
DECLARE @ReviewBody NVARCHAR(MAX);
DECLARE @ReviewApproved BIT;

WHILE @ReviewCounter <= 30
BEGIN
    -- Get random product
    SELECT TOP 1 @ReviewProductId = id FROM Products ORDER BY NEWID();
    
    -- Get random user
    SELECT TOP 1 @ReviewUserId = id FROM Users WHERE role = 'USER' ORDER BY NEWID();
    
    -- Get random order (optional - 60% have order)
    IF RAND() > 0.4
    BEGIN
        SELECT TOP 1 @ReviewOrderId = id FROM Orders WHERE user_id = @ReviewUserId ORDER BY NEWID();
    END
    ELSE
    BEGIN
        SET @ReviewOrderId = NULL;
    END;
    
    -- Random rating (weighted towards 4-5 stars)
    DECLARE @RatingRand FLOAT = RAND();
    IF @RatingRand < 0.1 SET @ReviewRating = 1;
    ELSE IF @RatingRand < 0.2 SET @ReviewRating = 2;
    ELSE IF @RatingRand < 0.3 SET @ReviewRating = 3;
    ELSE IF @RatingRand < 0.6 SET @ReviewRating = 4;
    ELSE SET @ReviewRating = 5;
    
    -- Review title and body
    SET @ReviewTitle = CASE @ReviewRating
        WHEN 5 THEN N'Sản phẩm tuyệt vời!'
        WHEN 4 THEN N'Rất hài lòng'
        WHEN 3 THEN N'Tạm được'
        WHEN 2 THEN N'Không như mong đợi'
        ELSE N'Không hài lòng'
    END;
    
    SET @ReviewBody = N'Đây là review mẫu cho sản phẩm. Chất lượng tốt, giao hàng nhanh. ' + 
                      CASE WHEN @ReviewRating >= 4 THEN N'Rất đáng mua!' ELSE N'Cần cải thiện.' END;
    
    -- 80% approved
    SET @ReviewApproved = CASE WHEN RAND() > 0.2 THEN 1 ELSE 0 END;
    
    INSERT INTO Reviews (product_id, user_id, order_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, unhelpful_count, created_at)
    VALUES (@ReviewProductId, @ReviewUserId, @ReviewOrderId, @ReviewRating, @ReviewTitle, @ReviewBody, 
            @ReviewApproved, CASE WHEN @ReviewOrderId IS NOT NULL THEN 1 ELSE 0 END,
            CAST(RAND() * 10 AS INT), CAST(RAND() * 3 AS INT), DATEADD(day, -RAND() * 60, GETDATE()));
    
    SET @ReviewCounter = @ReviewCounter + 1;
END;
GO

-- Update product ratings
PRINT 'Updating product ratings...';
DECLARE @UpdateProductId BIGINT;
DECLARE update_rating_cursor CURSOR FOR
SELECT DISTINCT product_id FROM Reviews WHERE is_approved = 1;

OPEN update_rating_cursor;
FETCH NEXT FROM update_rating_cursor INTO @UpdateProductId;

WHILE @@FETCH_STATUS = 0
BEGIN
    EXEC sp_UpdateProductRating @ProductId = @UpdateProductId;
    FETCH NEXT FROM update_rating_cursor INTO @UpdateProductId;
END;

CLOSE update_rating_cursor;
DEALLOCATE update_rating_cursor;
GO

PRINT '  - Inserted 30 reviews and updated product ratings';
GO

-- =====================================================
-- 3. WISHLISTS (20 wishlist items)
-- =====================================================
PRINT 'Inserting Wishlists...';

DECLARE @WishlistCounter INT = 1;
DECLARE @WishlistUserId BIGINT;
DECLARE @WishlistProductId BIGINT;

WHILE @WishlistCounter <= 20
BEGIN
    SELECT TOP 1 @WishlistUserId = id FROM Users WHERE role = 'USER' ORDER BY NEWID();
    SELECT TOP 1 @WishlistProductId = id FROM Products ORDER BY NEWID();
    
    -- Check if already exists
    IF NOT EXISTS (SELECT 1 FROM Wishlists WHERE user_id = @WishlistUserId AND product_id = @WishlistProductId)
    BEGIN
        INSERT INTO Wishlists (user_id, product_id, created_at)
        VALUES (@WishlistUserId, @WishlistProductId, DATEADD(day, -RAND() * 90, GETDATE()));
        SET @WishlistCounter = @WishlistCounter + 1;
    END;
END;
GO

PRINT '  - Inserted 20 wishlist items';
GO

-- =====================================================
-- 4. CARTS (15 carts)
-- =====================================================
PRINT 'Inserting Carts...';

DECLARE @CartCounter INT = 1;
DECLARE @CartUserId BIGINT;
DECLARE @CartId BIGINT;

WHILE @CartCounter <= 15
BEGIN
    SELECT TOP 1 @CartUserId = id FROM Users WHERE role = 'USER' ORDER BY NEWID();
    
    -- Check if cart already exists
    IF NOT EXISTS (SELECT 1 FROM Carts WHERE user_id = @CartUserId)
    BEGIN
        INSERT INTO Carts (user_id, created_at, updated_at, expires_at)
        VALUES (@CartUserId, DATEADD(day, -RAND() * 7, GETDATE()), GETDATE(), DATEADD(day, 7, GETDATE()));
        
        SET @CartId = SCOPE_IDENTITY();
        
        -- Add 1-3 items to cart
        DECLARE @CartItemCount INT = 1 + CAST(RAND() * 3 AS INT);
        DECLARE @CartItemCounter INT = 1;
        DECLARE @VariantId BIGINT;
        
        WHILE @CartItemCounter <= @CartItemCount
        BEGIN
            SELECT TOP 1 @VariantId = id FROM Product_Variants WHERE is_active = 1 AND stock_quantity > 0 ORDER BY NEWID();
            
            IF @VariantId IS NOT NULL
            BEGIN
                INSERT INTO Cart_Items (cart_id, variant_id, quantity)
                VALUES (@CartId, @VariantId, 1 + CAST(RAND() * 2 AS INT));
            END;
            
            SET @CartItemCounter = @CartItemCounter + 1;
        END;
        
        SET @CartCounter = @CartCounter + 1;
    END;
END;
GO

PRINT '  - Inserted 15 carts with items';
GO

-- =====================================================
-- 5. LOYALTY_POINTS (80 transactions)
-- =====================================================
PRINT 'Inserting Loyalty_Points...';

DECLARE @LoyaltyCounter INT = 1;
DECLARE @LoyaltyUserId BIGINT;
DECLARE @LoyaltyOrderId BIGINT;
DECLARE @LoyaltyPoints INT;
DECLARE @LoyaltyType NVARCHAR(20);

WHILE @LoyaltyCounter <= 80
BEGIN
    SELECT TOP 1 @LoyaltyUserId = id FROM Users WHERE role = 'USER' ORDER BY NEWID();
    
    DECLARE @LoyaltyRand FLOAT = RAND();
    IF @LoyaltyRand < 0.7
    BEGIN
        -- Earn points from order (70%)
        SELECT TOP 1 @LoyaltyOrderId = id FROM Orders WHERE user_id = @LoyaltyUserId AND status = 'delivered' ORDER BY NEWID();
        IF @LoyaltyOrderId IS NOT NULL
        BEGIN
            SELECT @LoyaltyPoints = points_earned FROM Orders WHERE id = @LoyaltyOrderId;
            IF @LoyaltyPoints > 0
            BEGIN
                INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, expires_at, created_at)
                VALUES (@LoyaltyUserId, @LoyaltyPoints, 'earn', @LoyaltyOrderId, N'Tích điểm từ đơn hàng', DATEADD(month, 12, GETDATE()), 
                        (SELECT created_at FROM Orders WHERE id = @LoyaltyOrderId));
                SET @LoyaltyCounter = @LoyaltyCounter + 1;
            END;
        END;
    END
    ELSE IF @LoyaltyRand < 0.9
    BEGIN
        -- Redeem points (20%)
        SELECT TOP 1 @LoyaltyOrderId = id FROM Orders WHERE user_id = @LoyaltyUserId AND points_used > 0 ORDER BY NEWID();
        IF @LoyaltyOrderId IS NOT NULL
        BEGIN
            SELECT @LoyaltyPoints = points_used FROM Orders WHERE id = @LoyaltyOrderId;
            IF @LoyaltyPoints > 0
            BEGIN
                INSERT INTO Loyalty_Points (user_id, points, transaction_type, redeemed_in_order_id, description, created_at)
                VALUES (@LoyaltyUserId, -@LoyaltyPoints, 'redeem', @LoyaltyOrderId, N'Sử dụng điểm từ đơn hàng', 
                        (SELECT created_at FROM Orders WHERE id = @LoyaltyOrderId));
                SET @LoyaltyCounter = @LoyaltyCounter + 1;
            END;
        END;
    END
    ELSE
    BEGIN
        -- Expire points (10%)
        INSERT INTO Loyalty_Points (user_id, points, transaction_type, description, created_at)
        VALUES (@LoyaltyUserId, -(100 + CAST(RAND() * 500 AS INT)), 'expire', N'Điểm hết hạn', DATEADD(day, -RAND() * 30, GETDATE()));
        SET @LoyaltyCounter = @LoyaltyCounter + 1;
    END;
END;
GO

PRINT '  - Inserted 80 loyalty point transactions';
GO

-- =====================================================
-- 6. NOTIFICATIONS (50 notifications)
-- =====================================================
PRINT 'Inserting Notifications...';

DECLARE @NotifCounter INT = 1;
DECLARE @NotifUserId BIGINT;
DECLARE @NotifType NVARCHAR(50);
DECLARE @NotifTitle NVARCHAR(255);
DECLARE @NotifMessage NVARCHAR(MAX);
DECLARE @NotifLink NVARCHAR(500);
DECLARE @NotifRead BIT;

WHILE @NotifCounter <= 50
BEGIN
    SELECT TOP 1 @NotifUserId = id FROM Users WHERE role = 'USER' ORDER BY NEWID();
    
    DECLARE @NotifRand FLOAT = RAND();
    IF @NotifRand < 0.4
    BEGIN
        SET @NotifType = 'order_status';
        SET @NotifTitle = N'Cập nhật đơn hàng';
        SET @NotifMessage = N'Đơn hàng của bạn đã được cập nhật trạng thái';
        SET @NotifLink = '/orders';
    END
    ELSE IF @NotifRand < 0.6
    BEGIN
        SET @NotifType = 'promotion';
        SET @NotifTitle = N'Khuyến mãi mới';
        SET @NotifMessage = N'Có khuyến mãi mới dành cho bạn!';
        SET @NotifLink = '/promotions';
    END
    ELSE IF @NotifRand < 0.75
    BEGIN
        SET @NotifType = 'product_restock';
        SET @NotifTitle = N'Sản phẩm đã có hàng';
        SET @NotifMessage = N'Sản phẩm bạn quan tâm đã có hàng trở lại';
        SET @NotifLink = '/products';
    END
    ELSE IF @NotifRand < 0.9
    BEGIN
        SET @NotifType = 'review_reply';
        SET @NotifTitle = N'Phản hồi review';
        SET @NotifMessage = N'Bạn có phản hồi mới cho review của mình';
        SET @NotifLink = '/reviews';
    END
    ELSE
    BEGIN
        SET @NotifType = 'system';
        SET @NotifTitle = N'Thông báo hệ thống';
        SET @NotifMessage = N'Thông báo từ hệ thống Sneakery';
        SET @NotifLink = NULL;
    END;
    
    SET @NotifRead = CASE WHEN RAND() > 0.4 THEN 1 ELSE 0 END;
    
    INSERT INTO Notifications (user_id, type, title, message, link, is_read, read_at, created_at)
    VALUES (@NotifUserId, @NotifType, @NotifTitle, @NotifMessage, @NotifLink, @NotifRead,
            CASE WHEN @NotifRead = 1 THEN DATEADD(minute, 5, DATEADD(day, -RAND() * 30, GETDATE())) ELSE NULL END,
            DATEADD(day, -RAND() * 30, GETDATE()));
    
    SET @NotifCounter = @NotifCounter + 1;
END;
GO

PRINT '  - Inserted 50 notifications';
GO

-- =====================================================
-- 7. INVENTORY_LOGS (30 logs)
-- =====================================================
PRINT 'Inserting Inventory_Logs...';

DECLARE @InvLogCounter INT = 1;
DECLARE @InvVariantId BIGINT;
DECLARE @InvChangeType NVARCHAR(50);
DECLARE @InvQtyBefore INT;
DECLARE @InvQtyChange INT;
DECLARE @InvQtyAfter INT;

WHILE @InvLogCounter <= 30
BEGIN
    SELECT TOP 1 @InvVariantId = id FROM Product_Variants ORDER BY NEWID();
    
    DECLARE @InvRand FLOAT = RAND();
    IF @InvRand < 0.3
    BEGIN
        SET @InvChangeType = 'restock';
        SELECT @InvQtyBefore = stock_quantity FROM Product_Variants WHERE id = @InvVariantId;
        SET @InvQtyChange = 10 + CAST(RAND() * 40 AS INT);
        SET @InvQtyAfter = @InvQtyBefore + @InvQtyChange;
    END
    ELSE IF @InvRand < 0.6
    BEGIN
        SET @InvChangeType = 'sale';
        SELECT @InvQtyBefore = stock_quantity FROM Product_Variants WHERE id = @InvVariantId;
        SET @InvQtyChange = -(1 + CAST(RAND() * 2 AS INT));
        SET @InvQtyAfter = @InvQtyBefore + @InvQtyChange;
    END
    ELSE IF @InvRand < 0.8
    BEGIN
        SET @InvChangeType = 'adjustment';
        SELECT @InvQtyBefore = stock_quantity FROM Product_Variants WHERE id = @InvVariantId;
        SET @InvQtyChange = CAST((RAND() - 0.5) * 20 AS INT);
        SET @InvQtyAfter = @InvQtyBefore + @InvQtyChange;
    END
    ELSE
    BEGIN
        SET @InvChangeType = 'return';
        SELECT @InvQtyBefore = stock_quantity FROM Product_Variants WHERE id = @InvVariantId;
        SET @InvQtyChange = 1;
        SET @InvQtyAfter = @InvQtyBefore + @InvQtyChange;
    END;
    
    INSERT INTO Inventory_Logs (variant_id, change_type, quantity_before, quantity_change, quantity_after, reference_type, note, created_at)
    VALUES (@InvVariantId, @InvChangeType, @InvQtyBefore, @InvQtyChange, @InvQtyAfter, 
            CASE WHEN @InvChangeType = 'sale' THEN 'order' ELSE 'manual' END,
            'Inventory log entry', DATEADD(day, -RAND() * 60, GETDATE()));
    
    SET @InvLogCounter = @InvLogCounter + 1;
END;
GO

PRINT '  - Inserted 30 inventory logs';
GO

-- =====================================================
-- 8. ACTIVITY_LOGS (30 logs)
-- =====================================================
PRINT 'Inserting Activity_Logs...';

DECLARE @ActivityCounter INT = 1;
DECLARE @ActivityUserId BIGINT;
DECLARE @ActivityAction NVARCHAR(50);
DECLARE @ActivityEntityType NVARCHAR(50);
DECLARE @ActivityEntityId BIGINT;

WHILE @ActivityCounter <= 30
BEGIN
    SELECT TOP 1 @ActivityUserId = id FROM Users WHERE role IN ('ADMIN', 'MODERATOR') ORDER BY NEWID();
    
    DECLARE @ActivityRand FLOAT = RAND();
    IF @ActivityRand < 0.3
    BEGIN
        SET @ActivityAction = 'CREATE';
        SET @ActivityEntityType = 'Product';
        SELECT TOP 1 @ActivityEntityId = id FROM Products ORDER BY NEWID();
    END
    ELSE IF @ActivityRand < 0.5
    BEGIN
        SET @ActivityAction = 'UPDATE';
        SET @ActivityEntityType = 'Product';
        SELECT TOP 1 @ActivityEntityId = id FROM Products ORDER BY NEWID();
    END
    ELSE IF @ActivityRand < 0.6
    BEGIN
        SET @ActivityAction = 'UPDATE';
        SET @ActivityEntityType = 'Order';
        SELECT TOP 1 @ActivityEntityId = id FROM Orders ORDER BY NEWID();
    END
    ELSE IF @ActivityRand < 0.7
    BEGIN
        SET @ActivityAction = 'DELETE';
        SET @ActivityEntityType = 'Product';
        SELECT TOP 1 @ActivityEntityId = id FROM Products ORDER BY NEWID();
    END
    ELSE IF @ActivityRand < 0.85
    BEGIN
        SET @ActivityAction = 'LOGIN';
        SET @ActivityEntityType = 'User';
        SET @ActivityEntityId = @ActivityUserId;
    END
    ELSE
    BEGIN
        SET @ActivityAction = 'LOGOUT';
        SET @ActivityEntityType = 'User';
        SET @ActivityEntityId = @ActivityUserId;
    END;
    
    INSERT INTO Activity_Logs (user_id, action, entity_type, entity_id, ip_address, user_agent, created_at)
    VALUES (@ActivityUserId, @ActivityAction, @ActivityEntityType, @ActivityEntityId, 
            '192.168.1.' + CAST(1 + CAST(RAND() * 254 AS INT) AS NVARCHAR(3)),
            'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', DATEADD(day, -RAND() * 60, GETDATE()));
    
    SET @ActivityCounter = @ActivityCounter + 1;
END;
GO

PRINT '  - Inserted 30 activity logs';
GO

-- =====================================================
-- 9. WARRANTIES (5 warranties)
-- =====================================================
PRINT 'Inserting Warranties...';

DECLARE @WarrantyCounter INT = 1;
DECLARE @WarrantyOrderId BIGINT;
DECLARE @WarrantyUserId BIGINT;
DECLARE @WarrantyProductId BIGINT;
DECLARE @WarrantyVariantId BIGINT;
DECLARE @WarrantyStatus NVARCHAR(20);

WHILE @WarrantyCounter <= 5
BEGIN
    SELECT TOP 1 @WarrantyOrderId = id FROM Orders WHERE status = 'delivered' AND user_id IS NOT NULL ORDER BY NEWID();
    
    IF @WarrantyOrderId IS NOT NULL
    BEGIN
        SELECT @WarrantyUserId = user_id FROM Orders WHERE id = @WarrantyOrderId;
        
        SELECT TOP 1 @WarrantyProductId = pv.product_id, @WarrantyVariantId = od.variant_id
        FROM Order_Details od
        INNER JOIN Product_Variants pv ON od.variant_id = pv.id
        WHERE od.order_id = @WarrantyOrderId;
        
        IF @WarrantyProductId IS NOT NULL AND @WarrantyUserId IS NOT NULL
        BEGIN
            DECLARE @WarrantyRand FLOAT = RAND();
            IF @WarrantyRand < 0.2 SET @WarrantyStatus = 'pending';
            ELSE IF @WarrantyRand < 0.4 SET @WarrantyStatus = 'approved';
            ELSE IF @WarrantyRand < 0.5 SET @WarrantyStatus = 'rejected';
            ELSE IF @WarrantyRand < 0.8 SET @WarrantyStatus = 'processing';
            ELSE SET @WarrantyStatus = 'completed';
            
            INSERT INTO Warranties (order_id, user_id, product_id, variant_id, issue_description, status, warranty_type, warranty_months, submitted_at, created_at)
            VALUES (@WarrantyOrderId, @WarrantyUserId, @WarrantyProductId, @WarrantyVariantId, 
                    N'Sản phẩm bị lỗi cần bảo hành', @WarrantyStatus,
                    CASE WHEN RAND() > 0.5 THEN 'repair' ELSE 'replace' END,
                    12, DATEADD(day, -RAND() * 30, GETDATE()), DATEADD(day, -RAND() * 30, GETDATE()));
            
            SET @WarrantyCounter = @WarrantyCounter + 1;
        END;
    END;
END;
GO

PRINT '  - Inserted 5 warranties';
GO

-- =====================================================
-- 10. RETURN_REQUESTS (3 requests)
-- =====================================================
PRINT 'Inserting Return_Requests...';

DECLARE @ReturnCounter INT = 1;
DECLARE @ReturnOrderId BIGINT;
DECLARE @ReturnUserId BIGINT;
DECLARE @ReturnStatus NVARCHAR(20);

WHILE @ReturnCounter <= 3
BEGIN
    SELECT TOP 1 @ReturnOrderId = id FROM Orders WHERE status = 'delivered' AND user_id IS NOT NULL ORDER BY NEWID();
    
    IF @ReturnOrderId IS NOT NULL
    BEGIN
        SELECT @ReturnUserId = user_id FROM Orders WHERE id = @ReturnOrderId;
        
        IF @ReturnUserId IS NOT NULL
        BEGIN
            DECLARE @ReturnRand FLOAT = RAND();
            IF @ReturnRand < 0.3 SET @ReturnStatus = 'pending';
            ELSE IF @ReturnRand < 0.6 SET @ReturnStatus = 'approved';
            ELSE IF @ReturnRand < 0.7 SET @ReturnStatus = 'rejected';
            ELSE SET @ReturnStatus = 'completed';
            
            INSERT INTO Return_Requests (order_id, user_id, reason, status, created_at)
            VALUES (@ReturnOrderId, @ReturnUserId, N'Sản phẩm không đúng mô tả', @ReturnStatus, DATEADD(day, -RAND() * 20, GETDATE()));
            
            SET @ReturnCounter = @ReturnCounter + 1;
        END;
    END;
END;
GO

PRINT '  - Inserted 3 return requests';
GO

-- =====================================================
-- 11. SYSTEM_SETTINGS
-- =====================================================
PRINT 'Inserting System_Settings...';

-- Delete old settings first
DELETE FROM System_Settings WHERE setting_key IN ('store.name', 'store.email', 'store.phone', 'store.address', 'general.currency', 'general.timezone', 'payment.cod_enabled', 'payment.vnpay_enabled', 'shipping.free_shipping_threshold', 'loyalty.points_per_vnd');
GO

IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'store.name')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('store.name', 'Sneakery Store', 'store', N'Tên cửa hàng', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'store.email')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('store.email', 'info@sneakery.com', 'store', N'Email cửa hàng', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'store.phone')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('store.phone', '1900123456', 'store', N'Số điện thoại', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'store.address')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('store.address', N'123 Đường ABC, Quận 1, TP.HCM', 'store', N'Địa chỉ cửa hàng', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'general.currency')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('general.currency', 'VND', 'general', N'Đơn vị tiền tệ', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'general.timezone')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('general.timezone', 'Asia/Ho_Chi_Minh', 'general', N'Múi giờ', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'payment.cod_enabled')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('payment.cod_enabled', 'true', 'payment', N'Cho phép thanh toán COD', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'payment.vnpay_enabled')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('payment.vnpay_enabled', 'true', 'payment', N'Cho phép VNPay', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'shipping.free_shipping_threshold')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('shipping.free_shipping_threshold', '2000000', 'shipping', N'Ngưỡng miễn phí ship', GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM System_Settings WHERE setting_key = 'loyalty.points_per_vnd')
BEGIN
    INSERT INTO System_Settings (setting_key, setting_value, setting_type, description, created_at, updated_at) VALUES
    ('loyalty.points_per_vnd', '10000', 'loyalty', N'Số VND để tích 1 điểm', GETDATE(), GETDATE());
END;
GO

PRINT '  - Inserted system settings';
GO

-- =====================================================
-- 12. EMAIL_TEMPLATES
-- =====================================================
PRINT 'Inserting Email_Templates...';

-- Delete old templates first
DELETE FROM Email_Templates WHERE template_name IN ('order_confirmation', 'order_shipped', 'order_cancelled', 'password_reset', 'welcome');
GO

IF NOT EXISTS (SELECT 1 FROM Email_Templates WHERE template_name = 'order_confirmation')
BEGIN
    INSERT INTO Email_Templates (template_name, subject, body, variables, is_active, created_at, updated_at) VALUES
    ('order_confirmation', N'Xác nhận đơn hàng #{order_number}', N'<h1>Xác nhận đơn hàng</h1><p>Cảm ơn bạn đã đặt hàng!</p>', '{order_number},{customer_name},{total_amount}', 1, GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Email_Templates WHERE template_name = 'order_shipped')
BEGIN
    INSERT INTO Email_Templates (template_name, subject, body, variables, is_active, created_at, updated_at) VALUES
    ('order_shipped', N'Đơn hàng #{order_number} đã được giao', N'<h1>Đơn hàng đã được giao</h1><p>Đơn hàng của bạn đã được giao thành công.</p>', '{order_number},{tracking_number}', 1, GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Email_Templates WHERE template_name = 'order_cancelled')
BEGIN
    INSERT INTO Email_Templates (template_name, subject, body, variables, is_active, created_at, updated_at) VALUES
    ('order_cancelled', N'Đơn hàng #{order_number} đã bị hủy', N'<h1>Đơn hàng đã bị hủy</h1><p>Đơn hàng của bạn đã bị hủy.</p>', '{order_number},{reason}', 1, GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Email_Templates WHERE template_name = 'password_reset')
BEGIN
    INSERT INTO Email_Templates (template_name, subject, body, variables, is_active, created_at, updated_at) VALUES
    ('password_reset', N'Đặt lại mật khẩu', N'<h1>Đặt lại mật khẩu</h1><p>Nhấn vào link để đặt lại mật khẩu: {reset_link}</p>', '{reset_link},{expiry_time}', 1, GETDATE(), GETDATE());
END;
IF NOT EXISTS (SELECT 1 FROM Email_Templates WHERE template_name = 'welcome')
BEGIN
    INSERT INTO Email_Templates (template_name, subject, body, variables, is_active, created_at, updated_at) VALUES
    ('welcome', N'Chào mừng đến với Sneakery Store', N'<h1>Chào mừng!</h1><p>Cảm ơn bạn đã đăng ký tài khoản.</p>', '{customer_name}', 1, GETDATE(), GETDATE());
END;
GO

PRINT '  - Inserted email templates';
GO

-- =====================================================
-- 13. SIZE_CHARTS (Sample size charts for brands)
-- =====================================================
PRINT 'Inserting Size_Charts...';

DECLARE @SizeBrandId INT;
DECLARE @SizeCounter INT = 38;

-- Nike size chart
SET @SizeBrandId = (SELECT id FROM Brands WHERE slug = 'nike');
WHILE @SizeCounter <= 45
BEGIN
    INSERT INTO Size_Charts (brand_id, category, size, size_us, size_uk, length_cm, width_cm, created_at, updated_at)
    VALUES (@SizeBrandId, 'Running', CAST(@SizeCounter AS NVARCHAR(10)), CAST(@SizeCounter - 33 AS NVARCHAR(10)), CAST(@SizeCounter - 33 AS NVARCHAR(10)), 
            24.0 + (@SizeCounter - 38) * 0.5, 9.0 + (@SizeCounter - 38) * 0.2, GETDATE(), GETDATE());
    SET @SizeCounter = @SizeCounter + 1;
END;

-- Adidas size chart
SET @SizeBrandId = (SELECT id FROM Brands WHERE slug = 'adidas');
SET @SizeCounter = 38;
WHILE @SizeCounter <= 45
BEGIN
    INSERT INTO Size_Charts (brand_id, category, size, size_us, size_uk, length_cm, width_cm, created_at, updated_at)
    VALUES (@SizeBrandId, 'Running', CAST(@SizeCounter AS NVARCHAR(10)), CAST(@SizeCounter - 33 AS NVARCHAR(10)), CAST(@SizeCounter - 33 AS NVARCHAR(10)), 
            24.0 + (@SizeCounter - 38) * 0.5, 9.0 + (@SizeCounter - 38) * 0.2, GETDATE(), GETDATE());
    SET @SizeCounter = @SizeCounter + 1;
END;
GO

PRINT '  - Inserted size charts';
GO

PRINT '';
PRINT '=====================================================';
PRINT N'HOAN THANH INSERT DU LIEU GIAO DICH (PART 2)!';
PRINT '=====================================================';
PRINT '';
PRINT N'Da insert thanh cong:';
PRINT '  - 50 orders';
PRINT '  - ~150 order details';
PRINT '  - ~50 order status histories';
PRINT '  - 50 payments';
PRINT '  - 30 reviews';
PRINT '  - 20 wishlists';
PRINT '  - 15 carts with items';
PRINT '  - 80 loyalty point transactions';
PRINT '  - 50 notifications';
PRINT '  - 30 inventory logs';
PRINT '  - 30 activity logs';
PRINT '  - 5 warranties';
PRINT '  - 3 return requests';
PRINT '  - System settings';
PRINT '  - Email templates';
PRINT '  - Size charts';
PRINT '';
PRINT '=====================================================';
PRINT N'DATABASE HOAN THANH!';
PRINT '=====================================================';
PRINT '';
PRINT N'Tong ket:';
PRINT '  - 10 brands';
PRINT '  - 15 categories';
PRINT '  - 25 products';
PRINT '  - ~100 variants';
PRINT '  - 30 users';
PRINT '  - 50 orders';
PRINT '  - 30 reviews';
PRINT '';
PRINT N'Database da san sang su dung!';
PRINT '';

-- ============================================================
-- MIGRATION: Đồng bộ Product.mainImageUrl từ ProductImage
-- ============================================================
-- Mục đích:
-- 1. Set isPrimary=true cho ảnh đầu tiên nếu sản phẩm chưa có ảnh primary
-- 2. Update Product.mainImageUrl từ ProductImage có isPrimary=true
-- 3. Đảm bảo tất cả sản phẩm có ảnh đều có mainImageUrl
-- ============================================================
PRINT '=====================================================';
PRINT N'DANG DONG BO ANH SAN PHAM...';
PRINT '=====================================================';
PRINT '';

BEGIN TRANSACTION;

-- [1] Set isPrimary=true cho ảnh đầu tiên của sản phẩm chưa có ảnh primary
-- Lấy ảnh có displayOrder nhỏ nhất (hoặc id nhỏ nhất nếu không có displayOrder)
UPDATE Product_Images
SET is_primary = 1
WHERE id IN (
    SELECT MIN(pi.id)
    FROM Product_Images pi
    INNER JOIN Products p ON pi.product_id = p.id
    WHERE NOT EXISTS (
        SELECT 1 
        FROM Product_Images pi2 
        WHERE pi2.product_id = pi.product_id 
        AND pi2.is_primary = 1
    )
    GROUP BY pi.product_id
);

PRINT N'  - Da set primary cho anh dau tien cua san pham chua co primary';

-- [2] Update Product.mainImageUrl từ ProductImage có isPrimary=true
UPDATE Products
SET main_image_url = (
    SELECT TOP 1 pi.image_url
    FROM Product_Images pi
    WHERE pi.product_id = Products.id
    AND pi.is_primary = 1
    ORDER BY pi.display_order ASC, pi.id ASC
)
WHERE EXISTS (
    SELECT 1
    FROM Product_Images pi
    WHERE pi.product_id = Products.id
    AND pi.is_primary = 1
)
AND (main_image_url IS NULL OR main_image_url = '');

PRINT N'  - Da dong bo mainImageUrl cho san pham';

-- [3] Log kết quả
DECLARE @TotalProducts INT;
DECLARE @ProductsWithMainImage INT;
DECLARE @TotalPrimaryImages INT;

SELECT 
    @TotalProducts = COUNT(*),
    @ProductsWithMainImage = SUM(CASE WHEN main_image_url IS NOT NULL AND main_image_url != '' THEN 1 ELSE 0 END)
FROM Products
WHERE deleted_at IS NULL;

SELECT @TotalPrimaryImages = COUNT(*) FROM Product_Images WHERE is_primary = 1;

PRINT '';
PRINT N'Ket qua dong bo:';
PRINT N'  - Tong san pham: ' + CAST(@TotalProducts AS NVARCHAR(10));
PRINT N'  - San pham co mainImageUrl: ' + CAST(@ProductsWithMainImage AS NVARCHAR(10));
PRINT N'  - Tong anh primary: ' + CAST(@TotalPrimaryImages AS NVARCHAR(10));
PRINT '';

COMMIT TRANSACTION;

PRINT '=====================================================';
PRINT N'HOAN THANH DONG BO ANH SAN PHAM!';
PRINT '=====================================================';
PRINT '';


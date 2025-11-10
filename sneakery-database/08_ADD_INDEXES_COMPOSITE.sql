-- =====================================================
-- SNEAKERY E-COMMERCE - ADD COMPOSITE INDEXES
-- =====================================================
-- Mục đích: Thêm composite indexes để tối ưu các queries phức tạp
-- Thời gian tạo: 2024-12-19
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '========================================';
PRINT 'Bắt đầu thêm composite indexes...';
PRINT '========================================';
GO

-- =====================================================
-- 1. ORDERS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo status + sort theo created_at
-- Query: SELECT * FROM Orders WHERE status = ? ORDER BY created_at DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_orders_status_created' AND object_id = OBJECT_ID('Orders'))
BEGIN
    CREATE INDEX idx_orders_status_created ON Orders(status, created_at DESC);
    PRINT '✅ Đã tạo index: idx_orders_status_created';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_orders_status_created';
GO

-- Index cho query: filter theo user_id + status
-- Query: SELECT * FROM Orders WHERE user_id = ? AND status = ?
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_orders_user_status' AND object_id = OBJECT_ID('Orders'))
BEGIN
    CREATE INDEX idx_orders_user_status ON Orders(user_id, status);
    PRINT '✅ Đã tạo index: idx_orders_user_status';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_orders_user_status';
GO

-- Index cho query: filter theo user_id + sort theo created_at
-- Query: SELECT * FROM Orders WHERE user_id = ? ORDER BY created_at DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_orders_user_created' AND object_id = OBJECT_ID('Orders'))
BEGIN
    CREATE INDEX idx_orders_user_created ON Orders(user_id, created_at DESC);
    PRINT '✅ Đã tạo index: idx_orders_user_created';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_orders_user_created';
GO

-- =====================================================
-- 2. REVIEWS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo product_id + is_approved
-- Query: SELECT * FROM Reviews WHERE product_id = ? AND is_approved = 1
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_reviews_product_approved' AND object_id = OBJECT_ID('Reviews'))
BEGIN
    CREATE INDEX idx_reviews_product_approved ON Reviews(product_id, is_approved);
    PRINT '✅ Đã tạo index: idx_reviews_product_approved';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_reviews_product_approved';
GO

-- Index cho query: filter theo product_id + rating
-- Query: SELECT * FROM Reviews WHERE product_id = ? AND rating >= ?
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_reviews_product_rating' AND object_id = OBJECT_ID('Reviews'))
BEGIN
    CREATE INDEX idx_reviews_product_rating ON Reviews(product_id, rating);
    PRINT '✅ Đã tạo index: idx_reviews_product_rating';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_reviews_product_rating';
GO

-- Index cho query: filter theo user_id + product_id (để check user đã review chưa)
-- Query: SELECT * FROM Reviews WHERE user_id = ? AND product_id = ?
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_reviews_user_product' AND object_id = OBJECT_ID('Reviews'))
BEGIN
    CREATE INDEX idx_reviews_user_product ON Reviews(user_id, product_id);
    PRINT '✅ Đã tạo index: idx_reviews_user_product';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_reviews_user_product';
GO

-- Index cho query: filter theo is_approved + created_at (admin review management)
-- Query: SELECT * FROM Reviews WHERE is_approved = ? ORDER BY created_at DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_reviews_approved_created' AND object_id = OBJECT_ID('Reviews'))
BEGIN
    CREATE INDEX idx_reviews_approved_created ON Reviews(is_approved, created_at DESC);
    PRINT '✅ Đã tạo index: idx_reviews_approved_created';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_reviews_approved_created';
GO

-- =====================================================
-- 3. PRODUCT_VARIANTS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo product_id + is_active
-- Query: SELECT * FROM Product_Variants WHERE product_id = ? AND is_active = 1
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_variants_product_active' AND object_id = OBJECT_ID('Product_Variants'))
BEGIN
    CREATE INDEX idx_variants_product_active ON Product_Variants(product_id, is_active);
    PRINT '✅ Đã tạo index: idx_variants_product_active';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_variants_product_active';
GO

-- =====================================================
-- 4. PRODUCTS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo brand_id + is_active
-- Query: SELECT * FROM Products WHERE brand_id = ? AND is_active = 1
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_brand_active' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_brand_active ON Products(brand_id, is_active);
    PRINT '✅ Đã tạo index: idx_products_brand_active';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_products_brand_active';
GO

-- Index cho query: filter theo is_featured + is_active + sort theo created_at
-- Query: SELECT * FROM Products WHERE is_featured = 1 AND is_active = 1 ORDER BY created_at DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_featured_active_created' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_featured_active_created ON Products(is_featured, is_active, created_at DESC);
    PRINT '✅ Đã tạo index: idx_products_featured_active_created';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_products_featured_active_created';
GO

-- Index cho query: filter theo is_active + avg_rating (sort theo rating)
-- Query: SELECT * FROM Products WHERE is_active = 1 ORDER BY avg_rating DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_products_active_rating' AND object_id = OBJECT_ID('Products'))
BEGIN
    CREATE INDEX idx_products_active_rating ON Products(is_active, avg_rating DESC);
    PRINT '✅ Đã tạo index: idx_products_active_rating';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_products_active_rating';
GO

-- =====================================================
-- 5. PAYMENTS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo order_id + status
-- Query: SELECT * FROM Payments WHERE order_id = ? AND status = ?
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_payments_order_status' AND object_id = OBJECT_ID('Payments'))
BEGIN
    CREATE INDEX idx_payments_order_status ON Payments(order_id, status);
    PRINT '✅ Đã tạo index: idx_payments_order_status';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_payments_order_status';
GO

-- Index cho query: filter theo status + created_at (admin payment management)
-- Query: SELECT * FROM Payments WHERE status = ? ORDER BY created_at DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_payments_status_created' AND object_id = OBJECT_ID('Payments'))
BEGIN
    CREATE INDEX idx_payments_status_created ON Payments(status, created_at DESC);
    PRINT '✅ Đã tạo index: idx_payments_status_created';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_payments_status_created';
GO

-- =====================================================
-- 6. NOTIFICATIONS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo user_id + is_read
-- Query: SELECT * FROM Notifications WHERE user_id = ? AND is_read = 0
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_notifications_user_read' AND object_id = OBJECT_ID('Notifications'))
BEGIN
    CREATE INDEX idx_notifications_user_read ON Notifications(user_id, is_read);
    PRINT '✅ Đã tạo index: idx_notifications_user_read';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_notifications_user_read';
GO

-- Index cho query: filter theo user_id + created_at (sort)
-- Query: SELECT * FROM Notifications WHERE user_id = ? ORDER BY created_at DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_notifications_user_created' AND object_id = OBJECT_ID('Notifications'))
BEGIN
    CREATE INDEX idx_notifications_user_created ON Notifications(user_id, created_at DESC);
    PRINT '✅ Đã tạo index: idx_notifications_user_created';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_notifications_user_created';
GO

-- =====================================================
-- 7. WISHLISTS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo product_id (để xem ai đã wishlist sản phẩm)
-- Query: SELECT * FROM Wishlists WHERE product_id = ?
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_wishlists_product' AND object_id = OBJECT_ID('Wishlists'))
BEGIN
    CREATE INDEX idx_wishlists_product ON Wishlists(product_id);
    PRINT '✅ Đã tạo index: idx_wishlists_product';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_wishlists_product';
GO

-- =====================================================
-- 8. LOYALTY_POINTS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo user_id + transaction_type
-- Query: SELECT * FROM Loyalty_Points WHERE user_id = ? AND transaction_type = ?
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_loyalty_user_type' AND object_id = OBJECT_ID('Loyalty_Points'))
BEGIN
    CREATE INDEX idx_loyalty_user_type ON Loyalty_Points(user_id, transaction_type);
    PRINT '✅ Đã tạo index: idx_loyalty_user_type';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_loyalty_user_type';
GO

-- Index cho query: filter theo user_id + created_at (sort)
-- Query: SELECT * FROM Loyalty_Points WHERE user_id = ? ORDER BY created_at DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_loyalty_user_created' AND object_id = OBJECT_ID('Loyalty_Points'))
BEGIN
    CREATE INDEX idx_loyalty_user_created ON Loyalty_Points(user_id, created_at DESC);
    PRINT '✅ Đã tạo index: idx_loyalty_user_created';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_loyalty_user_created';
GO

-- =====================================================
-- 9. INVENTORY_LOGS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo variant_id + created_at (sort)
-- Query: SELECT * FROM Inventory_Logs WHERE variant_id = ? ORDER BY created_at DESC
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_inventory_variant_created' AND object_id = OBJECT_ID('Inventory_Logs'))
BEGIN
    CREATE INDEX idx_inventory_variant_created ON Inventory_Logs(variant_id, created_at DESC);
    PRINT '✅ Đã tạo index: idx_inventory_variant_created';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_inventory_variant_created';
GO

-- =====================================================
-- 10. USERS TABLE - Composite Indexes
-- =====================================================

-- Index cho query: filter theo role + is_active
-- Query: SELECT * FROM Users WHERE role = ? AND is_active = 1
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_users_role_active' AND object_id = OBJECT_ID('Users'))
BEGIN
    CREATE INDEX idx_users_role_active ON Users(role, is_active);
    PRINT '✅ Đã tạo index: idx_users_role_active';
END
ELSE
    PRINT '⏭️  Index đã tồn tại: idx_users_role_active';
GO

PRINT '========================================';
PRINT 'Hoàn thành thêm composite indexes!';
PRINT '========================================';
GO

PRINT '';
PRINT 'Da tao thanh cong tat ca composite indexes.';
PRINT 'Bước tiếp theo: Chạy file 09_INSERT_DATA.sql để thêm dữ liệu mẫu.';
PRINT '';


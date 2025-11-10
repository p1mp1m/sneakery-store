-- =====================================================
-- SNEAKERY E-COMMERCE - CREATE VIEWS
-- =====================================================
-- File này tạo tất cả views cho admin API
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT 'DANG TAO VIEWS...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- VIEWS FOR ADMIN API
-- =====================================================

CREATE VIEW vw_ProductSummary AS
SELECT 
    p.id,
    p.name,
    p.slug,
    b.name AS brand_name,
    p.is_active,
    p.is_featured,
    p.avg_rating,
    p.review_count,
    p.view_count,
    p.order_count,
    MIN(ISNULL(pv.price_sale, pv.price_base)) AS min_price,
    MAX(ISNULL(pv.price_sale, pv.price_base)) AS max_price,
    SUM(pv.stock_quantity) AS total_stock
FROM Products p
JOIN Brands b ON p.brand_id = b.id
LEFT JOIN Product_Variants pv ON p.id = pv.product_id AND pv.is_active = 1
WHERE p.deleted_at IS NULL
GROUP BY p.id, p.name, p.slug, b.name, p.is_active, p.is_featured, 
        p.avg_rating, p.review_count, p.view_count, p.order_count;
GO

CREATE VIEW vw_OrderSummary AS
SELECT 
    o.id,
    o.order_number,
    o.user_id,
    u.full_name AS customer_name,
    u.email AS customer_email,
    o.total_amount,
    o.status,
    o.created_at,
    o.delivered_at,
    COUNT(od.id) AS item_count
FROM Orders o
JOIN Users u ON o.user_id = u.id
LEFT JOIN Order_Details od ON o.id = od.order_id
GROUP BY o.id, o.order_number, o.user_id, u.full_name, u.email, 
        o.total_amount, o.status, o.created_at, o.delivered_at;
GO

CREATE VIEW vw_AdminDashboardStats AS
SELECT 
    (SELECT COUNT(*) FROM Users WHERE deleted_at IS NULL) AS total_users,
    (SELECT COUNT(*) FROM Products WHERE deleted_at IS NULL) AS total_products,
    (SELECT COUNT(*) FROM Orders) AS total_orders,
    (SELECT ISNULL(SUM(total_amount), 0) FROM Orders WHERE status = 'delivered') AS total_revenue,
    (SELECT COUNT(*) FROM Orders WHERE created_at >= DATEADD(day, -30, GETDATE())) AS orders_last_30d,
    (SELECT ISNULL(SUM(total_amount), 0) FROM Orders WHERE status = 'delivered' AND created_at >= DATEADD(day, -30, GETDATE())) AS revenue_last_30d;
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH TAO VIEWS!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da tao thanh cong 3 views cho admin API.';
PRINT 'Bước tiếp theo: Chạy file 05_CREATE_PROCEDURES.sql để tạo stored procedures.';
PRINT '';


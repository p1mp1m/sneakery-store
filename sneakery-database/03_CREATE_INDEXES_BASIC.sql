-- =====================================================
-- SNEAKERY E-COMMERCE - CREATE BASIC INDEXES
-- =====================================================
-- File này tạo tất cả indexes cơ bản cho các tables
-- Indexes này được tạo ngay sau mỗi CREATE TABLE
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT 'DANG TAO INDEXES CO BAN...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 1. USERS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_users_email ON Users(email);
CREATE INDEX idx_users_role ON Users(role);
CREATE INDEX idx_users_active ON Users(is_active);
CREATE INDEX idx_users_deleted ON Users(deleted_at);
GO

-- =====================================================
-- 2. BRANDS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_brands_slug ON Brands(slug);
CREATE INDEX idx_brands_active ON Brands(is_active);
GO

-- =====================================================
-- 3. CATEGORIES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_categories_slug ON Categories(slug);
CREATE INDEX idx_categories_parent ON Categories(parent_id);
CREATE INDEX idx_categories_lft_rgt ON Categories(lft, rgt);
CREATE INDEX idx_categories_active ON Categories(is_active);
GO

-- =====================================================
-- 4. MATERIALS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_materials_slug ON Materials(slug);
CREATE INDEX idx_materials_active ON Materials(is_active);
GO

-- =====================================================
-- 5. SHOE_SOLES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_shoe_soles_slug ON Shoe_Soles(slug);
CREATE INDEX idx_shoe_soles_active ON Shoe_Soles(is_active);
GO

-- =====================================================
-- 6. PRODUCTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_products_brand ON Products(brand_id);
CREATE INDEX idx_products_slug ON Products(slug);
CREATE INDEX idx_products_active ON Products(is_active);
CREATE INDEX idx_products_featured ON Products(is_featured);
CREATE INDEX idx_products_rating ON Products(avg_rating);
CREATE INDEX idx_products_deleted ON Products(deleted_at);
CREATE INDEX idx_products_material ON Products(material_id);
CREATE INDEX idx_products_shoe_sole ON Products(shoe_sole_id);

-- Performance indexes for common queries
CREATE INDEX idx_products_name ON Products(name); -- For search by name
CREATE INDEX idx_products_created_at ON Products(created_at); -- For sorting by date
CREATE INDEX idx_products_name_search ON Products(name) INCLUDE (slug, brand_id); -- Covering index for search

-- Unique index for product_code (ignores NULL values)
CREATE UNIQUE INDEX UQ_Products_ProductCode
ON Products(product_code)
WHERE product_code IS NOT NULL;
GO

-- =====================================================
-- 7. PRODUCT_CATEGORIES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_pc_product ON Product_Categories(product_id);
CREATE INDEX idx_pc_category ON Product_Categories(category_id);
GO

-- =====================================================
-- 8. PRODUCT_VARIANTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_variants_product ON Product_Variants(product_id);
CREATE INDEX idx_variants_sku ON Product_Variants(sku);
CREATE INDEX idx_variants_stock ON Product_Variants(stock_quantity);
CREATE INDEX idx_variants_active ON Product_Variants(is_active);

-- Composite index for product_id + stock_quantity (for filtering by stock level)
CREATE INDEX idx_variants_product_stock ON Product_Variants(product_id, stock_quantity);
-- Index for price filtering
CREATE INDEX idx_variants_price_base ON Product_Variants(price_base);
CREATE INDEX idx_variants_price_sale ON Product_Variants(price_sale);
GO

-- =====================================================
-- 9. PRODUCT_IMAGES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_images_product ON Product_Images(product_id);
CREATE INDEX idx_images_primary ON Product_Images(is_primary);
GO

-- =====================================================
-- 9a. VARIANT_IMAGES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_variant_images_variant ON Variant_Images(variant_id);
CREATE INDEX idx_variant_images_primary ON Variant_Images(is_primary);
GO

-- =====================================================
-- 10. COUPONS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_coupons_code ON Coupons(code);
CREATE INDEX idx_coupons_dates ON Coupons(start_at, end_at);
CREATE INDEX idx_coupons_active ON Coupons(is_active);
GO

-- =====================================================
-- 11. FLASH_SALES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_flashsale_product ON Flash_Sales(product_id);
CREATE INDEX idx_flashsale_active ON Flash_Sales(is_active, start_time, end_time);
GO

-- =====================================================
-- 12. ADDRESSES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_addresses_user ON Addresses(user_id);
CREATE INDEX idx_addresses_default ON Addresses(is_default);
GO

-- =====================================================
-- 13. CARTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_carts_user ON Carts(user_id);
CREATE INDEX idx_carts_session ON Carts(session_id);
GO

-- =====================================================
-- 14. CART_ITEMS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_cart_items_cart ON Cart_Items(cart_id);
CREATE INDEX idx_cart_items_variant ON Cart_Items(variant_id);
GO

-- =====================================================
-- 15. ORDERS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_orders_user ON Orders(user_id);
CREATE INDEX idx_orders_number ON Orders(order_number);
CREATE INDEX idx_orders_status ON Orders(status);
CREATE INDEX idx_orders_created ON Orders(created_at DESC);
GO

-- =====================================================
-- 16. ORDER_DETAILS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_order_details_order ON Order_Details(order_id);
CREATE INDEX idx_order_details_variant ON Order_Details(variant_id);
GO

-- =====================================================
-- 17. PAYMENTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_payments_order ON Payments(order_id);
CREATE INDEX idx_payments_status ON Payments(status);
CREATE INDEX idx_payments_transaction ON Payments(transaction_id);
GO

-- =====================================================
-- 18. REVIEWS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_reviews_product ON Reviews(product_id);
CREATE INDEX idx_reviews_user ON Reviews(user_id);
CREATE INDEX idx_reviews_approved ON Reviews(is_approved);
CREATE INDEX idx_reviews_rating ON Reviews(rating);
GO

-- =====================================================
-- 19. NOTIFICATIONS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_notifications_user ON Notifications(user_id);
CREATE INDEX idx_notifications_read ON Notifications(is_read);
CREATE INDEX idx_notifications_created ON Notifications(created_at DESC);
GO

-- =====================================================
-- 20. INVENTORY_LOGS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_inventory_logs_variant ON Inventory_Logs(variant_id);
CREATE INDEX idx_inventory_logs_created ON Inventory_Logs(created_at DESC);
GO

-- =====================================================
-- 21. ACTIVITY_LOGS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_activity_logs_user ON Activity_Logs(user_id);
CREATE INDEX idx_activity_logs_entity ON Activity_Logs(entity_type, entity_id);
CREATE INDEX idx_activity_logs_created ON Activity_Logs(created_at DESC);
GO

-- =====================================================
-- 22. WISHLISTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_wishlists_user ON Wishlists(user_id);
CREATE INDEX idx_wishlists_product ON Wishlists(product_id);
GO

-- =====================================================
-- 23. LOYALTY_POINTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_loyalty_user ON Loyalty_Points(user_id);
CREATE INDEX idx_loyalty_expires ON Loyalty_Points(expires_at);
GO

-- =====================================================
-- 24. RETURN_REQUESTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_return_order ON Return_Requests(order_id);
CREATE INDEX idx_return_user ON Return_Requests(user_id);
CREATE INDEX idx_return_status ON Return_Requests(status);
GO

-- =====================================================
-- 25. WARRANTIES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_warranty_order ON Warranties(order_id);
CREATE INDEX idx_warranty_user ON Warranties(user_id);
CREATE INDEX idx_warranty_product ON Warranties(product_id);
CREATE INDEX idx_warranty_status ON Warranties(status);
GO

-- =====================================================
-- 26. EMAIL_TEMPLATES TABLE INDEXES
-- =====================================================
-- No indexes needed (small table, only accessed by template_name which is UNIQUE)
GO

-- =====================================================
-- 27. SYSTEM_SETTINGS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_system_settings_type ON System_Settings(setting_type);
CREATE INDEX idx_system_settings_key ON System_Settings(setting_key);
GO

-- =====================================================
-- 28. ORDER_STATUS_HISTORIES TABLE INDEXES
-- =====================================================
CREATE INDEX idx_status_history_order ON Order_Status_Histories(order_id);
GO

-- =====================================================
-- 29. SIZE_CHARTS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_sizechart_brand ON Size_Charts(brand_id);
CREATE INDEX idx_sizechart_category ON Size_Charts(category);
GO

-- =====================================================
-- 30. NEWSLETTER_SUBSCRIPTIONS TABLE INDEXES
-- =====================================================
CREATE INDEX idx_newsletter_subscriptions_email ON Newsletter_Subscriptions(email);
CREATE INDEX idx_newsletter_subscriptions_is_active ON Newsletter_Subscriptions(is_active);
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH TAO INDEXES CO BAN!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da tao thanh cong tat ca indexes co ban.';
PRINT 'Bước tiếp theo: Chạy file 04_CREATE_VIEWS.sql để tạo views.';
PRINT '';


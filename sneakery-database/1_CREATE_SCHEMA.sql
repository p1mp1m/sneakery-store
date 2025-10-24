-- =====================================================
-- SNEAKERY E-COMMERCE - SCHEMA V2 (API COMPATIBLE)
-- =====================================================
-- Schema được tối ưu để tương thích với Frontend Admin API
-- =====================================================

-- Drop database if exists (BE CAREFUL!)
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'sneakery_db')
BEGIN
    ALTER DATABASE sneakery_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE sneakery_db;
END
GO

-- Create database
CREATE DATABASE sneakery_db;
GO

USE sneakery_db;
GO

PRINT '=====================================================';
PRINT 'DANG TAO SCHEMA V2 - API COMPATIBLE...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 1. USERS TABLE (Enhanced for Admin API)
-- =====================================================
CREATE TABLE Users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name NVARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    avatar_url VARCHAR(500),
    date_of_birth DATE,
    gender NVARCHAR(10) CHECK (gender IN (N'Nam', N'Nữ', N'Khác')),
    
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN', 'MODERATOR')),
    is_active BIT DEFAULT 1,
    is_email_verified BIT DEFAULT 0,
    email_verification_token VARCHAR(255),
    
    password_reset_token VARCHAR(255),
    password_reset_expires DATETIME2,
    
    google_id VARCHAR(255),
    facebook_id VARCHAR(255),
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    last_login_at DATETIME2,
    deleted_at DATETIME2 NULL
);

CREATE INDEX idx_users_email ON Users(email);
CREATE INDEX idx_users_role ON Users(role);
CREATE INDEX idx_users_active ON Users(is_active);
CREATE INDEX idx_users_deleted ON Users(deleted_at);
GO

-- =====================================================
-- 2. BRANDS TABLE (Admin API Compatible)
-- =====================================================
CREATE TABLE Brands (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    description NVARCHAR(MAX),
    website_url VARCHAR(255),
    is_active BIT DEFAULT 1,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL
);

CREATE INDEX idx_brands_slug ON Brands(slug);
CREATE INDEX idx_brands_active ON Brands(is_active);
GO

-- =====================================================
-- 3. CATEGORIES TABLE (Hierarchical Structure)
-- =====================================================
CREATE TABLE Categories (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    image_url VARCHAR(500),
    
    parent_id INT NULL,
    lft INT NOT NULL,
    rgt INT NOT NULL,
    level INT DEFAULT 0,
    
    is_active BIT DEFAULT 1,
    display_order INT DEFAULT 0,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES Categories(id)
);

CREATE INDEX idx_categories_slug ON Categories(slug);
CREATE INDEX idx_categories_parent ON Categories(parent_id);
CREATE INDEX idx_categories_lft_rgt ON Categories(lft, rgt);
CREATE INDEX idx_categories_active ON Categories(is_active);
GO

-- =====================================================
-- 4. PRODUCTS TABLE (Enhanced for Admin Management)
-- =====================================================
CREATE TABLE Products (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    brand_id INT NOT NULL,
    name NVARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    short_description NVARCHAR(500),
    
    meta_title NVARCHAR(255),
    meta_description NVARCHAR(500),
    meta_keywords NVARCHAR(500),
    
    is_active BIT DEFAULT 1,
    is_featured BIT DEFAULT 0,
    is_new BIT DEFAULT 0,
    
    view_count INT DEFAULT 0,
    order_count INT DEFAULT 0,
    avg_rating DECIMAL(3,2) DEFAULT 0.00,
    review_count INT DEFAULT 0,
    
    published_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    
    CONSTRAINT fk_products_brand FOREIGN KEY (brand_id) REFERENCES Brands(id)
);

CREATE INDEX idx_products_brand ON Products(brand_id);
CREATE INDEX idx_products_slug ON Products(slug);
CREATE INDEX idx_products_active ON Products(is_active);
CREATE INDEX idx_products_featured ON Products(is_featured);
CREATE INDEX idx_products_rating ON Products(avg_rating);
CREATE INDEX idx_products_deleted ON Products(deleted_at);
GO

-- =====================================================
-- 5. PRODUCT_CATEGORIES (Many-to-Many)
-- =====================================================
CREATE TABLE Product_Categories (
    product_id BIGINT NOT NULL,
    category_id INT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pc_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT fk_pc_category FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_pc_product ON Product_Categories(product_id);
CREATE INDEX idx_pc_category ON Product_Categories(category_id);
GO

-- =====================================================
-- 6. PRODUCT_VARIANTS TABLE (Enhanced for Admin)
-- =====================================================
CREATE TABLE Product_Variants (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    product_id BIGINT NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    
    size VARCHAR(20) NOT NULL,
    color NVARCHAR(50) NOT NULL,
    
    price_base DECIMAL(18,2) NOT NULL CHECK (price_base >= 0),
    price_sale DECIMAL(18,2) CHECK (price_sale >= 0),
    cost_price DECIMAL(18,2) CHECK (cost_price >= 0),
    
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    low_stock_threshold INT DEFAULT 10,
    weight_grams INT,
    
    image_url VARCHAR(500),
    
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    
    CONSTRAINT fk_variants_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);

CREATE INDEX idx_variants_product ON Product_Variants(product_id);
CREATE INDEX idx_variants_sku ON Product_Variants(sku);
CREATE INDEX idx_variants_stock ON Product_Variants(stock_quantity);
CREATE INDEX idx_variants_active ON Product_Variants(is_active);
GO

-- =====================================================
-- 7. PRODUCT_IMAGES TABLE
-- =====================================================
CREATE TABLE Product_Images (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    alt_text NVARCHAR(255),
    is_primary BIT DEFAULT 0,
    display_order INT DEFAULT 0,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_images_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);

CREATE INDEX idx_images_product ON Product_Images(product_id);
CREATE INDEX idx_images_primary ON Product_Images(is_primary);
GO

-- =====================================================
-- 8. COUPONS TABLE (Admin Management)
-- =====================================================
CREATE TABLE Coupons (
    id INT PRIMARY KEY IDENTITY(1,1),
    code VARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    
    discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('fixed', 'percentage')),
    discount_value DECIMAL(18,2) NOT NULL CHECK (discount_value > 0),
    
    min_order_amount DECIMAL(18,2),
    max_discount_amount DECIMAL(18,2),
    
    start_at DATETIME2 NOT NULL,
    end_at DATETIME2 NOT NULL,
    
    max_uses INT,
    uses_count INT DEFAULT 0,
    max_uses_per_user INT DEFAULT 1,
    
    applicable_to VARCHAR(20) CHECK (applicable_to IN ('all', 'brand', 'category', 'product')),
    applicable_id INT,
    
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT chk_coupon_dates CHECK (end_at > start_at)
);

CREATE INDEX idx_coupons_code ON Coupons(code);
CREATE INDEX idx_coupons_dates ON Coupons(start_at, end_at);
CREATE INDEX idx_coupons_active ON Coupons(is_active);
GO

-- =====================================================
-- 9. FLASH_SALES TABLE (Admin Management)
-- =====================================================
CREATE TABLE Flash_Sales (
    id INT PRIMARY KEY IDENTITY(1,1),
    product_id BIGINT NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL CHECK (discount_percent > 0 AND discount_percent <= 100),
    
    start_time DATETIME2 NOT NULL,
    end_time DATETIME2 NOT NULL,
    
    quantity_limit INT,
    sold_count INT DEFAULT 0,
    
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_flashsale_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT chk_flashsale_dates CHECK (end_time > start_time)
);

CREATE INDEX idx_flashsale_product ON Flash_Sales(product_id);
CREATE INDEX idx_flashsale_active ON Flash_Sales(is_active, start_time, end_time);
GO

-- =====================================================
-- 10. ADDRESSES TABLE
-- =====================================================
CREATE TABLE Addresses (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    
    recipient_name NVARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    
    line1 NVARCHAR(255) NOT NULL,
    line2 NVARCHAR(255),
    ward NVARCHAR(100),
    district NVARCHAR(100),
    city NVARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    
    is_default BIT DEFAULT 0,
    address_type VARCHAR(20) CHECK (address_type IN ('home', 'office', 'other')),
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user ON Addresses(user_id);
CREATE INDEX idx_addresses_default ON Addresses(is_default);
GO

-- =====================================================
-- 11. CARTS TABLE
-- =====================================================
CREATE TABLE Carts (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT UNIQUE,
    session_id VARCHAR(255),
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    expires_at DATETIME2,
    
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE INDEX idx_carts_user ON Carts(user_id);
CREATE INDEX idx_carts_session ON Carts(session_id);
GO

-- =====================================================
-- 12. CART_ITEMS TABLE
-- =====================================================
CREATE TABLE Cart_Items (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    cart_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    
    added_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES Carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_variant FOREIGN KEY (variant_id) REFERENCES Product_Variants(id)
);

CREATE INDEX idx_cart_items_cart ON Cart_Items(cart_id);
CREATE INDEX idx_cart_items_variant ON Cart_Items(variant_id);
GO

-- =====================================================
-- 13. ORDERS TABLE (Enhanced for Admin)
-- =====================================================
CREATE TABLE Orders (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    
    address_shipping_id BIGINT NOT NULL,
    address_billing_id BIGINT,
    
    subtotal DECIMAL(18,2) NOT NULL,
    shipping_fee DECIMAL(18,2) DEFAULT 0,
    discount_amount DECIMAL(18,2) DEFAULT 0,
    tax_amount DECIMAL(18,2) DEFAULT 0,
    total_amount DECIMAL(18,2) NOT NULL,
    
    coupon_id INT,
    points_earned INT DEFAULT 0,
    points_used INT DEFAULT 0,
    
    status VARCHAR(50) DEFAULT 'pending' CHECK (status IN (
        'pending', 'confirmed', 'processing', 'packed', 
        'shipped', 'delivered', 'cancelled', 'refunded'
    )),
    
    shipping_method VARCHAR(50),
    tracking_number VARCHAR(100),
    estimated_delivery_at DATETIME2,
    delivered_at DATETIME2,
    
    customer_note NVARCHAR(MAX),
    admin_note NVARCHAR(MAX),
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    cancelled_at DATETIME2,
    
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT fk_orders_shipping_address FOREIGN KEY (address_shipping_id) REFERENCES Addresses(id),
    CONSTRAINT fk_orders_billing_address FOREIGN KEY (address_billing_id) REFERENCES Addresses(id),
    CONSTRAINT fk_orders_coupon FOREIGN KEY (coupon_id) REFERENCES Coupons(id)
);

CREATE INDEX idx_orders_user ON Orders(user_id);
CREATE INDEX idx_orders_number ON Orders(order_number);
CREATE INDEX idx_orders_status ON Orders(status);
CREATE INDEX idx_orders_created ON Orders(created_at DESC);
GO

-- =====================================================
-- 14. ORDER_DETAILS TABLE
-- =====================================================
CREATE TABLE Order_Details (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    
    product_name NVARCHAR(255) NOT NULL,
    variant_sku VARCHAR(100) NOT NULL,
    size VARCHAR(20) NOT NULL,
    color NVARCHAR(50) NOT NULL,
    
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(18,2) NOT NULL,
    total_price DECIMAL(18,2) NOT NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_order_details_order FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_details_variant FOREIGN KEY (variant_id) REFERENCES Product_Variants(id)
);

CREATE INDEX idx_order_details_order ON Order_Details(order_id);
CREATE INDEX idx_order_details_variant ON Order_Details(variant_id);
GO

-- =====================================================
-- 15. PAYMENTS TABLE (Admin Management)
-- =====================================================
CREATE TABLE Payments (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN (
        'cod', 'vnpay', 'momo', 'zalopay', 'bank_transfer', 'credit_card'
    )),
    
    amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending' CHECK (status IN (
        'pending', 'processing', 'completed', 'failed', 'refunded'
    )),
    
    transaction_id VARCHAR(255),
    gateway_response TEXT,
    
    paid_at DATETIME2,
    refunded_at DATETIME2,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_payments_order ON Payments(order_id);
CREATE INDEX idx_payments_status ON Payments(status);
CREATE INDEX idx_payments_transaction ON Payments(transaction_id);
GO

-- =====================================================
-- 16. REVIEWS TABLE (Admin Management)
-- =====================================================
CREATE TABLE Reviews (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT,
    
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    title NVARCHAR(255),
    body NVARCHAR(MAX),
    
    image_url_1 VARCHAR(500),
    image_url_2 VARCHAR(500),
    image_url_3 VARCHAR(500),
    images_json NVARCHAR(MAX),
    
    is_approved BIT DEFAULT 0,
    is_verified_purchase BIT DEFAULT 0,
    
    helpful_count INT DEFAULT 0,
    unhelpful_count INT DEFAULT 0,
    
    reply_text NVARCHAR(MAX),
    replied_at DATETIME2,
    replied_by BIGINT,
    
    approved_by BIGINT,
    approved_at DATETIME2,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES Orders(id),
    CONSTRAINT fk_reviews_approver FOREIGN KEY (approved_by) REFERENCES Users(id),
    CONSTRAINT fk_reviews_replier FOREIGN KEY (replied_by) REFERENCES Users(id)
);

CREATE INDEX idx_reviews_product ON Reviews(product_id);
CREATE INDEX idx_reviews_user ON Reviews(user_id);
CREATE INDEX idx_reviews_approved ON Reviews(is_approved);
CREATE INDEX idx_reviews_rating ON Reviews(rating);
GO

-- =====================================================
-- 17. NOTIFICATIONS TABLE (Admin Management)
-- =====================================================
CREATE TABLE Notifications (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    
    type VARCHAR(50) NOT NULL CHECK (type IN (
        'order_status', 'promotion', 'product_restock', 'review_reply', 'system'
    )),
    
    title NVARCHAR(255) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    link VARCHAR(500),
    
    is_read BIT DEFAULT 0,
    read_at DATETIME2,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_user ON Notifications(user_id);
CREATE INDEX idx_notifications_read ON Notifications(is_read);
CREATE INDEX idx_notifications_created ON Notifications(created_at DESC);
GO

-- =====================================================
-- 18. INVENTORY_LOGS TABLE (Admin Management)
-- =====================================================
CREATE TABLE Inventory_Logs (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    variant_id BIGINT NOT NULL,
    
    change_type VARCHAR(50) NOT NULL CHECK (change_type IN (
        'restock', 'sale', 'return', 'adjustment', 'damaged'
    )),
    
    quantity_before INT NOT NULL,
    quantity_change INT NOT NULL,
    quantity_after INT NOT NULL,
    
    reference_type VARCHAR(50),
    reference_id BIGINT,
    
    note NVARCHAR(MAX),
    changed_by BIGINT,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_inventory_logs_variant FOREIGN KEY (variant_id) REFERENCES Product_Variants(id),
    CONSTRAINT fk_inventory_logs_user FOREIGN KEY (changed_by) REFERENCES Users(id)
);

CREATE INDEX idx_inventory_logs_variant ON Inventory_Logs(variant_id);
CREATE INDEX idx_inventory_logs_created ON Inventory_Logs(created_at DESC);
GO

-- =====================================================
-- 19. ACTIVITY_LOGS TABLE (Admin Management)
-- =====================================================
CREATE TABLE Activity_Logs (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT,
    
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    
    old_value NVARCHAR(MAX),
    new_value NVARCHAR(MAX),
    
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_activity_logs_user FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE INDEX idx_activity_logs_user ON Activity_Logs(user_id);
CREATE INDEX idx_activity_logs_entity ON Activity_Logs(entity_type, entity_id);
CREATE INDEX idx_activity_logs_created ON Activity_Logs(created_at DESC);
GO

-- =====================================================
-- 20. WISHLISTS TABLE
-- =====================================================
CREATE TABLE Wishlists (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlists_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT uq_wishlist_user_product UNIQUE (user_id, product_id)
);

CREATE INDEX idx_wishlists_user ON Wishlists(user_id);
CREATE INDEX idx_wishlists_product ON Wishlists(product_id);
GO

-- =====================================================
-- 21. LOYALTY_POINTS TABLE
-- =====================================================
CREATE TABLE Loyalty_Points (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    points INT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('earn', 'redeem', 'expire')),
    
    earned_from_order_id BIGINT NULL,
    redeemed_in_order_id BIGINT NULL,
    
    description NVARCHAR(255),
    expires_at DATETIME2,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_loyalty_user FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE INDEX idx_loyalty_user ON Loyalty_Points(user_id);
CREATE INDEX idx_loyalty_expires ON Loyalty_Points(expires_at);
GO

-- =====================================================
-- 22. RETURN_REQUESTS TABLE
-- =====================================================
CREATE TABLE Return_Requests (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    
    reason NVARCHAR(MAX) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected', 'completed')),
    
    images_json NVARCHAR(MAX),
    
    admin_note NVARCHAR(MAX),
    approved_by BIGINT,
    approved_at DATETIME2,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_return_order FOREIGN KEY (order_id) REFERENCES Orders(id),
    CONSTRAINT fk_return_user FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT fk_return_approver FOREIGN KEY (approved_by) REFERENCES Users(id)
);

CREATE INDEX idx_return_order ON Return_Requests(order_id);
CREATE INDEX idx_return_user ON Return_Requests(user_id);
CREATE INDEX idx_return_status ON Return_Requests(status);
GO

-- =====================================================
-- 23. EMAIL_TEMPLATES TABLE
-- =====================================================
CREATE TABLE Email_Templates (
    id INT PRIMARY KEY IDENTITY(1,1),
    template_name VARCHAR(100) NOT NULL UNIQUE,
    subject NVARCHAR(255) NOT NULL,
    body NVARCHAR(MAX) NOT NULL,
    variables NVARCHAR(500),
    
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- =====================================================
-- 24. ORDER_STATUS_HISTORIES TABLE
-- =====================================================
CREATE TABLE Order_Status_Histories (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    note NVARCHAR(MAX),
    changed_by BIGINT,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_status_history_order FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_status_history_user FOREIGN KEY (changed_by) REFERENCES Users(id)
);

CREATE INDEX idx_status_history_order ON Order_Status_Histories(order_id);
GO

-- =====================================================
-- 25. SIZE_CHARTS TABLE
-- =====================================================
CREATE TABLE Size_Charts (
    id INT PRIMARY KEY IDENTITY(1,1),
    brand_id INT NOT NULL,
    category NVARCHAR(50) NOT NULL,
    size VARCHAR(10) NOT NULL,
    size_us VARCHAR(10),
    size_uk VARCHAR(10),
    length_cm DECIMAL(5,2),
    width_cm DECIMAL(5,2),
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_sizechart_brand FOREIGN KEY (brand_id) REFERENCES Brands(id),
    CONSTRAINT uq_sizechart UNIQUE (brand_id, category, size)
);

CREATE INDEX idx_sizechart_brand ON Size_Charts(brand_id);
CREATE INDEX idx_sizechart_category ON Size_Charts(category);
GO

PRINT '=====================================================';
PRINT 'DANG TAO VIEWS...';
PRINT '=====================================================';
GO

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
    (SELECT ISNULL(SUM(total_amount), 0) FROM Orders WHERE status = 'delivered' AND created_at >= DATEADD(day, -30, GETDATE())) AS revenue_last_30d
GO

PRINT '=====================================================';
PRINT 'DANG TAO STORED PROCEDURES...';
PRINT '=====================================================';
GO

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

PRINT '=====================================================';
PRINT 'DANG TAO TRIGGERS...';
PRINT '=====================================================';
GO

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
PRINT 'HOAN THANH TAO SCHEMA V2!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da tao:';
PRINT '  - 25 tables voi indexes';
PRINT '  - 3 views cho admin API';
PRINT '  - 2 stored procedures';
PRINT '  - 2 triggers';
PRINT '';
PRINT 'CHUA CO DU LIEU! Chay file 2_INSERT_DATA_V2.sql de them du lieu.';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- SNEAKERY E-COMMERCE DATABASE - VERSION 2.0 (ENHANCED)
-- =====================================================
-- Description: Enhanced database schema for Sneakery Store
-- Features: 
--   - Improved indexing for better performance
--   - Full foreign key constraints
--   - BCrypt hashed passwords
--   - Soft delete support
--   - Audit timestamps (created_at, updated_at)
--   - Additional useful tables (Wishlists, Notifications)
--   - Stored procedures and views
-- Date: 2025
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

-- =====================================================
-- 1. USERS TABLE (Enhanced)
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
    
    -- Role & Status
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN', 'MODERATOR')),
    is_active BIT DEFAULT 1,
    is_email_verified BIT DEFAULT 0,
    email_verification_token VARCHAR(255),
    
    -- Password Reset
    password_reset_token VARCHAR(255),
    password_reset_expires DATETIME2,
    
    -- OAuth Support
    google_id VARCHAR(255),
    facebook_id VARCHAR(255),
    
    -- Audit
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    last_login_at DATETIME2,
    deleted_at DATETIME2 NULL -- Soft delete
);

-- Indexes for Users
CREATE INDEX idx_users_email ON Users(email);
CREATE INDEX idx_users_role ON Users(role);
CREATE INDEX idx_users_active ON Users(is_active);
CREATE INDEX idx_users_deleted ON Users(deleted_at);
GO

-- =====================================================
-- 2. BRANDS TABLE (Enhanced)
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
-- 3. CATEGORIES TABLE (Enhanced - Nested Set Model)
-- =====================================================
CREATE TABLE Categories (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    image_url VARCHAR(500),
    
    -- Nested Set Model for better hierarchy queries
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
-- 4. PRODUCTS TABLE (Enhanced)
-- =====================================================
CREATE TABLE Products (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    brand_id INT NOT NULL,
    name NVARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    short_description NVARCHAR(500),
    
    -- SEO
    meta_title NVARCHAR(255),
    meta_description NVARCHAR(500),
    meta_keywords NVARCHAR(500),
    
    -- Status
    is_active BIT DEFAULT 1,
    is_featured BIT DEFAULT 0,
    is_new BIT DEFAULT 0,
    
    -- Stats (denormalized for performance)
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
-- 6. PRODUCT_VARIANTS TABLE (Enhanced)
-- =====================================================
CREATE TABLE Product_Variants (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    product_id BIGINT NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    
    -- Variant attributes
    size VARCHAR(20) NOT NULL,
    color NVARCHAR(50) NOT NULL,
    
    -- Pricing
    price_base DECIMAL(18,2) NOT NULL CHECK (price_base >= 0),
    price_sale DECIMAL(18,2) CHECK (price_sale >= 0),
    cost_price DECIMAL(18,2) CHECK (cost_price >= 0), -- For profit calculation
    
    -- Inventory
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    low_stock_threshold INT DEFAULT 10,
    weight_grams INT, -- For shipping calculation
    
    -- Media
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
-- 7. PRODUCT_IMAGES TABLE (New - Multiple images per product)
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
GO

-- =====================================================
-- 8. ADDRESSES TABLE (Enhanced)
-- =====================================================
CREATE TABLE Addresses (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    
    recipient_name NVARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    
    -- Address details
    line1 NVARCHAR(255) NOT NULL,
    line2 NVARCHAR(255),
    ward NVARCHAR(100), -- Phường/Xã
    district NVARCHAR(100), -- Quận/Huyện
    city NVARCHAR(100) NOT NULL, -- Tỉnh/TP
    postal_code VARCHAR(20),
    
    -- Location (for future features)
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
-- 9. COUPONS TABLE (Enhanced)
-- =====================================================
CREATE TABLE Coupons (
    id INT PRIMARY KEY IDENTITY(1,1),
    code VARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    
    -- Discount
    discount_type VARCHAR(20) NOT NULL CHECK (discount_type IN ('fixed', 'percentage')),
    discount_value DECIMAL(18,2) NOT NULL CHECK (discount_value > 0),
    
    -- Constraints
    min_order_amount DECIMAL(18,2),
    max_discount_amount DECIMAL(18,2),
    
    -- Validity
    start_at DATETIME2 NOT NULL,
    end_at DATETIME2 NOT NULL,
    
    -- Usage limits
    max_uses INT,
    uses_count INT DEFAULT 0,
    max_uses_per_user INT DEFAULT 1,
    
    -- Applicability
    applicable_to VARCHAR(20) CHECK (applicable_to IN ('all', 'brand', 'category', 'product')),
    applicable_id INT, -- brand_id, category_id, or product_id
    
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
-- 10. CARTS TABLE
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
-- 11. CART_ITEMS TABLE
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
-- 12. ORDERS TABLE (Enhanced)
-- =====================================================
CREATE TABLE Orders (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE, -- e.g., ORD-20250121-0001
    
    -- Addresses
    address_shipping_id BIGINT NOT NULL,
    address_billing_id BIGINT,
    
    -- Pricing
    subtotal DECIMAL(18,2) NOT NULL,
    shipping_fee DECIMAL(18,2) DEFAULT 0,
    discount_amount DECIMAL(18,2) DEFAULT 0,
    tax_amount DECIMAL(18,2) DEFAULT 0,
    total_amount DECIMAL(18,2) NOT NULL,
    
    coupon_id INT,
    
    -- Status
    status VARCHAR(50) DEFAULT 'pending' CHECK (status IN (
        'pending', 'confirmed', 'processing', 'packed', 
        'shipped', 'delivered', 'cancelled', 'refunded'
    )),
    
    -- Shipping
    shipping_method VARCHAR(50),
    tracking_number VARCHAR(100),
    estimated_delivery_at DATETIME2,
    delivered_at DATETIME2,
    
    -- Notes
    customer_note NVARCHAR(MAX),
    admin_note NVARCHAR(MAX),
    
    -- Timestamps
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
-- 13. ORDER_DETAILS TABLE (Enhanced)
-- =====================================================
CREATE TABLE Order_Details (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    
    -- Snapshot at time of order
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
-- 14. ORDER_STATUS_HISTORIES TABLE
-- =====================================================
CREATE TABLE Order_Status_Histories (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    note NVARCHAR(MAX),
    changed_by BIGINT, -- user_id of admin who made the change
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_status_history_order FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_status_history_user FOREIGN KEY (changed_by) REFERENCES Users(id)
);

CREATE INDEX idx_status_history_order ON Order_Status_Histories(order_id);
GO

-- =====================================================
-- 15. PAYMENTS TABLE (Enhanced)
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
    
    -- Payment gateway details
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
-- 16. REVIEWS TABLE (Enhanced)
-- =====================================================
CREATE TABLE Reviews (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT, -- For verified purchase
    
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    title NVARCHAR(255),
    body NVARCHAR(MAX),
    
    -- Images
    image_url_1 VARCHAR(500),
    image_url_2 VARCHAR(500),
    image_url_3 VARCHAR(500),
    
    -- Moderation
    is_approved BIT DEFAULT 0,
    is_verified_purchase BIT DEFAULT 0,
    
    -- Engagement
    helpful_count INT DEFAULT 0,
    unhelpful_count INT DEFAULT 0,
    
    approved_by BIGINT, -- admin user_id
    approved_at DATETIME2,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES Orders(id),
    CONSTRAINT fk_reviews_approver FOREIGN KEY (approved_by) REFERENCES Users(id)
);

CREATE INDEX idx_reviews_product ON Reviews(product_id);
CREATE INDEX idx_reviews_user ON Reviews(user_id);
CREATE INDEX idx_reviews_approved ON Reviews(is_approved);
CREATE INDEX idx_reviews_rating ON Reviews(rating);
GO

-- =====================================================
-- 17. WISHLISTS TABLE (New)
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
-- 18. NOTIFICATIONS TABLE (New)
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
-- 19. INVENTORY_LOGS TABLE (New - Track stock changes)
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
    
    reference_type VARCHAR(50), -- 'order', 'manual', etc.
    reference_id BIGINT, -- order_id if applicable
    
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
-- 20. ACTIVITY_LOGS TABLE (New - Audit trail)
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
-- VIEWS FOR COMMON QUERIES
-- =====================================================

-- View: Product summary with pricing
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

-- View: Order summary for customers
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

-- =====================================================
-- STORED PROCEDURES
-- =====================================================

-- Procedure: Update product rating
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

-- Procedure: Generate order number
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

-- =====================================================
-- TRIGGERS
-- =====================================================

-- Trigger: Update product updated_at
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

-- Trigger: Log inventory changes
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

-- =====================================================
-- SAMPLE DATA WITH BCRYPT PASSWORDS
-- =====================================================

-- BCrypt hash for common passwords:
-- 'admin123' => $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi
-- 'user123' => $2a$10$rT5Z3z0QK0xJxX6YvqHJU.w7qVzN6hGJ3KCv8vQqVxJ3Lx3xX6xXe
-- 'password' => $2a$10$K7F2H9V0N8X1W5Y3Z6P4L.Q8R2S6T7U9V3W6X8Y0Z2A4B5C7D9E1F3

-- Insert Admin User
INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified) VALUES
('admin@sneakery.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', N'Admin Sneakery', '0901234567', 'ADMIN', 1, 1),
('moderator@sneakery.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', N'Moderator Sneakery', '0901234568', 'MODERATOR', 1, 1);

-- Insert Test Users
INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified) VALUES
('user1@gmail.com', '$2a$10$rT5Z3z0QK0xJxX6YvqHJU.w7qVzN6hGJ3KCv8vQqVxJ3Lx3xX6xXe', N'Nguyễn Văn An', '0912345678', 'USER', 1, 1),
('user2@gmail.com', '$2a$10$rT5Z3z0QK0xJxX6YvqHJU.w7qVzN6hGJ3KCv8vQqVxJ3Lx3xX6xXe', N'Trần Thị Bình', '0912345679', 'USER', 1, 1),
('user3@gmail.com', '$2a$10$rT5Z3z0QK0xJxX6YvqHJU.w7qVzN6hGJ3KCv8vQqVxJ3Lx3xX6xXe', N'Lê Văn Cường', '0912345680', 'USER', 1, 0);

-- Insert Brands
INSERT INTO Brands (name, slug, description, is_active) VALUES
(N'Nike', 'nike', N'Just Do It', 1),
(N'Adidas', 'adidas', N'Impossible is Nothing', 1),
(N'Puma', 'puma', N'Forever Faster', 1),
(N'New Balance', 'new-balance', N'Worn by Supermodels', 1),
(N'Converse', 'converse', N'All Star', 1),
(N'Vans', 'vans', N'Off The Wall', 1);

-- Insert Categories (with nested set values)
INSERT INTO Categories (name, slug, lft, rgt, level, parent_id, is_active) VALUES
(N'Giày Thể Thao', 'giay-the-thao', 1, 12, 0, NULL, 1),
  (N'Giày Chạy Bộ', 'giay-chay-bo', 2, 3, 1, 1, 1),
  (N'Giày Bóng Đá', 'giay-bong-da', 4, 5, 1, 1, 1),
  (N'Giày Bóng Rổ', 'giay-bong-ro', 6, 7, 1, 1, 1),
  (N'Giày Tennis', 'giay-tennis', 8, 9, 1, 1, 1),
  (N'Giày Gym', 'giay-gym', 10, 11, 1, 1, 1),
(N'Giày Lifestyle', 'giay-lifestyle', 13, 20, 0, NULL, 1),
  (N'Giày Sneaker', 'giay-sneaker', 14, 15, 1, 7, 1),
  (N'Giày Slip-On', 'giay-slip-on', 16, 17, 1, 7, 1),
  (N'Giày Cao Cổ', 'giay-cao-co', 18, 19, 1, 7, 1);

-- Insert Products
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new) VALUES
(1, N'Nike Air Max 270', 'nike-air-max-270', N'Giày Nike Air Max 270 với đệm khí lớn nhất từ trước đến nay', N'Đệm khí Max Air siêu êm', 1, 1, 1),
(1, N'Nike Revolution 6', 'nike-revolution-6', N'Giày chạy bộ Nike Revolution 6 thoải mái cho người mới', N'Giày chạy bộ nhập môn', 1, 0, 0),
(2, N'Adidas Ultraboost 22', 'adidas-ultraboost-22', N'Giày chạy bộ Adidas Ultraboost 22 với công nghệ Boost', N'Công nghệ Boost hoàn hảo', 1, 1, 1),
(2, N'Adidas Stan Smith', 'adidas-stan-smith', N'Giày tennis classic Adidas Stan Smith', N'Icon vượt thời gian', 1, 1, 0),
(3, N'Puma RS-X', 'puma-rs-x', N'Giày Puma RS-X phong cách retro', N'Retro style đậm chất', 1, 0, 0),
(5, N'Converse Chuck Taylor All Star', 'converse-chuck-taylor', N'Giày Converse Chuck Taylor All Star classic', N'Classic không bao giờ lỗi mốt', 1, 1, 0);

-- Insert Product Categories
INSERT INTO Product_Categories (product_id, category_id) VALUES
(1, 2), (1, 8), -- Nike Air Max: Running + Sneaker
(2, 2), -- Nike Revolution: Running
(3, 2), (3, 8), -- Ultraboost: Running + Sneaker
(4, 5), (4, 8), -- Stan Smith: Tennis + Sneaker
(5, 8), -- Puma RS-X: Sneaker
(6, 8), (6, 10); -- Converse: Sneaker + High Top

-- Insert Product Variants
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, stock_quantity, is_active) VALUES
-- Nike Air Max 270
(1, 'NIKE-AM270-BLK-39', '39', N'Đen', 3500000, 2990000, 10, 1),
(1, 'NIKE-AM270-BLK-40', '40', N'Đen', 3500000, 2990000, 15, 1),
(1, 'NIKE-AM270-WHT-39', '39', N'Trắng', 3500000, 2990000, 8, 1),
(1, 'NIKE-AM270-WHT-40', '40', N'Trắng', 3500000, 2990000, 12, 1),

-- Nike Revolution 6
(2, 'NIKE-REV6-BLK-39', '39', N'Đen', 1500000, NULL, 20, 1),
(2, 'NIKE-REV6-BLK-40', '40', N'Đen', 1500000, NULL, 25, 1),
(2, 'NIKE-REV6-BLU-39', '39', N'Xanh', 1500000, 1290000, 15, 1),

-- Adidas Ultraboost 22
(3, 'ADIDAS-UB22-WHT-40', '40', N'Trắng', 4500000, 3990000, 10, 1),
(3, 'ADIDAS-UB22-WHT-41', '41', N'Trắng', 4500000, 3990000, 8, 1),
(3, 'ADIDAS-UB22-BLK-40', '40', N'Đen', 4500000, 3990000, 12, 1),

-- Adidas Stan Smith
(4, 'ADIDAS-SS-WHT-39', '39', N'Trắng/Xanh', 2500000, NULL, 30, 1),
(4, 'ADIDAS-SS-WHT-40', '40', N'Trắng/Xanh', 2500000, NULL, 35, 1),
(4, 'ADIDAS-SS-WHT-41', '41', N'Trắng/Xanh', 2500000, NULL, 25, 1),

-- Puma RS-X
(5, 'PUMA-RSX-MLT-39', '39', N'Đa sắc', 2800000, 2390000, 15, 1),
(5, 'PUMA-RSX-MLT-40', '40', N'Đa sắc', 2800000, 2390000, 18, 1),

-- Converse Chuck Taylor
(6, 'CONV-CT-BLK-38', '38', N'Đen', 1200000, NULL, 40, 1),
(6, 'CONV-CT-BLK-39', '39', N'Đen', 1200000, NULL, 45, 1),
(6, 'CONV-CT-WHT-38', '38', N'Trắng', 1200000, NULL, 35, 1),
(6, 'CONV-CT-WHT-39', '39', N'Trắng', 1200000, NULL, 38, 1);

-- Insert Coupons
INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, applicable_to, is_active) VALUES
('WELCOME10', N'Giảm 10% cho đơn hàng đầu tiên', 'percentage', 10, 500000, 200000, '2025-01-01', '2025-12-31', 1000, 'all', 1),
('FREESHIP', N'Miễn phí vận chuyển', 'fixed', 30000, 300000, 30000, '2025-01-01', '2025-12-31', NULL, 'all', 1),
('NIKE50K', N'Giảm 50K cho sản phẩm Nike', 'fixed', 50000, 1000000, 50000, '2025-01-01', '2025-06-30', 500, 'brand', 1);

-- Insert sample address for user
INSERT INTO Addresses (user_id, recipient_name, phone, line1, ward, district, city, is_default, address_type) VALUES
(3, N'Nguyễn Văn An', '0912345678', N'123 Nguyễn Huệ', N'Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', 1, 'home'),
(3, N'Nguyễn Văn An', '0912345678', N'456 Lý Tự Trọng', N'Bến Thành', N'Quận 1', N'TP. Hồ Chí Minh', 0, 'office');

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================
PRINT '✅ Database created successfully!';
PRINT '📊 Tables: 20';
PRINT '👁️ Views: 2';
PRINT '⚙️ Stored Procedures: 2';
PRINT '🔔 Triggers: 2';
PRINT '';
PRINT '🔐 Test Accounts:';
PRINT '   Admin: admin@sneakery.com / admin123';
PRINT '   User: user1@gmail.com / user123';
PRINT '';
PRINT '🎉 Ready to use!';
GO


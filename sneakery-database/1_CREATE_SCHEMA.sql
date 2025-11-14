-- =====================================================
-- SNEAKERY E-COMMERCE - CREATE DATABASE SCHEMA
-- =====================================================
-- File này tạo database, tất cả tables, indexes, views, 
-- stored procedures và triggers cho Sneakery Store
-- =====================================================

-- =====================================================
-- 1. CREATE DATABASE
-- =====================================================

-- Drop database if exists (BE CAREFUL!)
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'sneakery_db')
BEGIN
    ALTER DATABASE sneakery_db SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE sneakery_db;
    PRINT '  - Dropped existing database sneakery_db';
END
ELSE
BEGIN
    PRINT '  - Database sneakery_db does not exist';
END
GO

-- Create database
CREATE DATABASE sneakery_db;
PRINT '  - Created database sneakery_db';
GO

USE sneakery_db;
GO

SET NOCOUNT ON; -- Improve performance by not counting rows affected

PRINT '';
PRINT '=====================================================';
PRINT 'DANG TAO TABLES...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 2. CREATE TABLES
-- =====================================================

-- =====================================================
-- 2.1 USERS TABLE
-- =====================================================
CREATE TABLE Users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(255) NOT NULL,
    phone_number NVARCHAR(20),
    is_active BIT DEFAULT 1,
    role NVARCHAR(20) DEFAULT 'USER',
    email_verification_token NVARCHAR(255),
    password_reset_token NVARCHAR(255),
    password_reset_expires DATETIME2,
    google_id NVARCHAR(255),
    facebook_id NVARCHAR(255),
    last_login_at DATETIME2,
    address NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    deleted_at DATETIME2
);
GO

-- =====================================================
-- 2.2 BRANDS TABLE
-- =====================================================
CREATE TABLE Brands (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL UNIQUE,
    slug NVARCHAR(255) NOT NULL UNIQUE,
    logo_url NVARCHAR(500),
    description NVARCHAR(MAX),
    website_url NVARCHAR(500),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    deleted_at DATETIME2
);
GO

-- =====================================================
-- 2.3 CATEGORIES TABLE
-- =====================================================
CREATE TABLE Categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL UNIQUE,
    slug NVARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    image_url NVARCHAR(500),
    parent_id INT,
    lft INT NOT NULL,
    rgt INT NOT NULL,
    level INT,
    display_order INT,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    deleted_at DATETIME2,
    FOREIGN KEY (parent_id) REFERENCES Categories(id) ON DELETE NO ACTION
);
GO

-- =====================================================
-- 2.4 MATERIALS TABLE
-- =====================================================
CREATE TABLE Materials (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL UNIQUE,
    slug NVARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    deleted_at DATETIME2
);
GO

-- =====================================================
-- 2.5 SHOE_SOLES TABLE
-- =====================================================
CREATE TABLE Shoe_Soles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL UNIQUE,
    slug NVARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    deleted_at DATETIME2
);
GO

-- =====================================================
-- 2.6 PRODUCTS TABLE
-- =====================================================
CREATE TABLE Products (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_code NVARCHAR(50) UNIQUE,
    brand_id INT NOT NULL,
    material_id INT,
    shoe_sole_id INT,
    name NVARCHAR(255) NOT NULL,
    slug NVARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    short_description NVARCHAR(500),
    meta_title NVARCHAR(255),
    meta_description NVARCHAR(500),
    meta_keywords NVARCHAR(255),
    is_active BIT DEFAULT 1,
    is_featured BIT DEFAULT 0,
    is_new BIT DEFAULT 0,
    view_count INT DEFAULT 0,
    order_count INT DEFAULT 0,
    avg_rating DECIMAL(3,2),
    review_count INT DEFAULT 0,
    main_image_url NVARCHAR(500),
    price_range NVARCHAR(500),
    published_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    deleted_at DATETIME2,
    FOREIGN KEY (brand_id) REFERENCES Brands(id) ON DELETE NO ACTION,
    FOREIGN KEY (material_id) REFERENCES Materials(id) ON DELETE SET NULL,
    FOREIGN KEY (shoe_sole_id) REFERENCES Shoe_Soles(id) ON DELETE SET NULL
);
GO

-- =====================================================
-- 2.7 PRODUCT_CATEGORIES TABLE (Many-to-Many)
-- =====================================================
CREATE TABLE Product_Categories (
    product_id BIGINT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.8 PRODUCT_VARIANTS TABLE
-- =====================================================
CREATE TABLE Product_Variants (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku NVARCHAR(100) NOT NULL UNIQUE,
    size NVARCHAR(20) NOT NULL,
    color NVARCHAR(50) NOT NULL,
    price_base DECIMAL(18,2) NOT NULL,
    price_sale DECIMAL(18,2),
    cost_price DECIMAL(18,2),
    stock_quantity INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 10,
    weight_grams INT,
    image_url NVARCHAR(500),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    deleted_at DATETIME2,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.9 PRODUCT_IMAGES TABLE
-- =====================================================
CREATE TABLE Product_Images (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url NVARCHAR(500) NOT NULL,
    alt_text NVARCHAR(255),
    is_primary BIT DEFAULT 0,
    display_order INT DEFAULT 0,
    cloudinary_public_id NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.10 VARIANT_IMAGES TABLE
-- =====================================================
CREATE TABLE Variant_Images (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    variant_id BIGINT NOT NULL,
    image_url NVARCHAR(500) NOT NULL,
    alt_text NVARCHAR(255),
    is_primary BIT DEFAULT 0,
    display_order INT DEFAULT 0,
    cloudinary_public_id NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (variant_id) REFERENCES Product_Variants(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.11 COUPONS TABLE
-- =====================================================
CREATE TABLE Coupons (
    id INT IDENTITY(1,1) PRIMARY KEY,
    code NVARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(500),
    discount_type NVARCHAR(20) NOT NULL, -- 'percent' or 'fixed'
    discount_value DECIMAL(18,2) NOT NULL,
    max_discount_amount DECIMAL(18,2),
    min_order_amount DECIMAL(18,2),
    start_at DATETIME2 NOT NULL,
    end_at DATETIME2 NOT NULL,
    max_uses INT,
    uses_count INT DEFAULT 0,
    max_uses_per_user INT,
    is_active BIT DEFAULT 1,
    applicable_to NVARCHAR(50), -- 'all', 'brand', 'category', 'product'
    applicable_id INT,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2
);
GO

-- =====================================================
-- 2.12 FLASH_SALES TABLE
-- =====================================================
CREATE TABLE Flash_Sales (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL,
    start_time DATETIME2 NOT NULL,
    end_time DATETIME2 NOT NULL,
    quantity_limit INT,
    sold_count INT DEFAULT 0,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.13 ADDRESSES TABLE
-- =====================================================
CREATE TABLE Addresses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT,
    recipient_name NVARCHAR(255) NOT NULL,
    phone NVARCHAR(20) NOT NULL,
    line1 NVARCHAR(255) NOT NULL,
    line2 NVARCHAR(255),
    city NVARCHAR(100) NOT NULL,
    ward NVARCHAR(100),
    district NVARCHAR(100),
    postal_code NVARCHAR(20),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    is_default BIT DEFAULT 0,
    address_type NVARCHAR(50), -- 'home', 'office', 'other'
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    deleted_at DATETIME2,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.14 CARTS TABLE
-- =====================================================
CREATE TABLE Carts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT UNIQUE,
    session_id NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    expires_at DATETIME2,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.15 CART_ITEMS TABLE
-- =====================================================
CREATE TABLE Cart_Items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES Carts(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES Product_Variants(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.16 ORDERS TABLE
-- =====================================================
CREATE TABLE Orders (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT, -- NULL for POS orders
    coupon_id INT,
    address_shipping_id BIGINT,
    address_billing_id BIGINT,
    order_number NVARCHAR(50) NOT NULL UNIQUE,
    subtotal DECIMAL(18,2) NOT NULL,
    shipping_fee DECIMAL(18,2),
    discount_amount DECIMAL(18,2),
    tax_amount DECIMAL(18,2),
    total_amount DECIMAL(18,2) NOT NULL,
    points_earned INT DEFAULT 0,
    points_used INT DEFAULT 0,
    status NVARCHAR(50), -- 'pending', 'confirmed', 'processing', 'packed', 'shipped', 'delivered', 'cancelled', 'refunded'
    shipping_method NVARCHAR(100),
    tracking_number NVARCHAR(100),
    estimated_delivery_at DATETIME2,
    delivered_at DATETIME2,
    customer_note NVARCHAR(MAX),
    admin_note NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    cancelled_at DATETIME2,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL,
    FOREIGN KEY (coupon_id) REFERENCES Coupons(id) ON DELETE SET NULL,
    FOREIGN KEY (address_shipping_id) REFERENCES Addresses(id) ON DELETE NO ACTION,
    FOREIGN KEY (address_billing_id) REFERENCES Addresses(id) ON DELETE NO ACTION
);
GO

-- =====================================================
-- 2.17 ORDER_DETAILS TABLE
-- =====================================================
CREATE TABLE Order_Details (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    product_name NVARCHAR(255) NOT NULL,
    variant_sku NVARCHAR(100) NOT NULL,
    size NVARCHAR(20) NOT NULL,
    color NVARCHAR(50) NOT NULL,
    total_price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES Product_Variants(id) ON DELETE NO ACTION
);
GO

-- =====================================================
-- 2.18 ORDER_STATUS_HISTORIES TABLE
-- =====================================================
CREATE TABLE Order_Status_Histories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status NVARCHAR(50) NOT NULL,
    note NVARCHAR(MAX),
    changed_by BIGINT,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES Users(id) ON DELETE SET NULL
);
GO

-- =====================================================
-- 2.19 PAYMENTS TABLE
-- =====================================================
CREATE TABLE Payments (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_method NVARCHAR(50) NOT NULL, -- 'cod', 'vnpay', 'momo', 'zalopay', 'bank_transfer', 'credit_card'
    amount DECIMAL(18,2) NOT NULL,
    status NVARCHAR(50), -- 'pending', 'processing', 'completed', 'failed', 'refunded'
    transaction_id NVARCHAR(255),
    gateway_response NVARCHAR(MAX),
    paid_at DATETIME2,
    refunded_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.20 REVIEWS TABLE
-- =====================================================
CREATE TABLE Reviews (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT,
    rating INT NOT NULL, -- 1 to 5
    title NVARCHAR(255),
    body NVARCHAR(MAX),
    image_url_1 NVARCHAR(500),
    image_url_2 NVARCHAR(500),
    image_url_3 NVARCHAR(500),
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
    updated_at DATETIME2,
    deleted_at DATETIME2,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE SET NULL,
    FOREIGN KEY (replied_by) REFERENCES Users(id) ON DELETE NO ACTION,
    FOREIGN KEY (approved_by) REFERENCES Users(id) ON DELETE NO ACTION
);
GO

-- =====================================================
-- 2.21 NOTIFICATIONS TABLE
-- =====================================================
CREATE TABLE Notifications (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type NVARCHAR(50) NOT NULL, -- 'order_status', 'promotion', 'product_restock', 'review_reply', 'system'
    title NVARCHAR(255) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    link NVARCHAR(500),
    is_read BIT DEFAULT 0,
    read_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.22 INVENTORY_LOGS TABLE
-- =====================================================
CREATE TABLE Inventory_Logs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    variant_id BIGINT NOT NULL,
    change_type NVARCHAR(50) NOT NULL, -- 'restock', 'sale', 'return', 'adjustment', 'damaged'
    quantity_before INT NOT NULL,
    quantity_change INT NOT NULL,
    quantity_after INT NOT NULL,
    reference_type NVARCHAR(50), -- 'order', 'manual', 'return', etc.
    reference_id BIGINT,
    note NVARCHAR(MAX),
    changed_by BIGINT,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (variant_id) REFERENCES Product_Variants(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES Users(id) ON DELETE SET NULL
);
GO

-- =====================================================
-- 2.23 ACTIVITY_LOGS TABLE
-- =====================================================
CREATE TABLE Activity_Logs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT,
    action NVARCHAR(50) NOT NULL, -- 'CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', etc.
    entity_type NVARCHAR(50) NOT NULL, -- 'Product', 'Order', 'User', etc.
    entity_id BIGINT,
    old_value NVARCHAR(MAX),
    new_value NVARCHAR(MAX),
    ip_address NVARCHAR(50),
    user_agent NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL
);
GO

-- =====================================================
-- 2.24 WISHLISTS TABLE
-- =====================================================
CREATE TABLE Wishlists (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    UNIQUE(user_id, product_id)
);
GO

-- =====================================================
-- 2.25 LOYALTY_POINTS TABLE
-- =====================================================
CREATE TABLE Loyalty_Points (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    points INT NOT NULL,
    transaction_type NVARCHAR(20) NOT NULL, -- 'earn', 'redeem', 'expire'
    earned_from_order_id BIGINT,
    redeemed_in_order_id BIGINT,
    description NVARCHAR(500),
    expires_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (earned_from_order_id) REFERENCES Orders(id) ON DELETE NO ACTION,
    FOREIGN KEY (redeemed_in_order_id) REFERENCES Orders(id) ON DELETE NO ACTION
);
GO

-- =====================================================
-- 2.26 RETURN_REQUESTS TABLE
-- =====================================================
CREATE TABLE Return_Requests (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reason NVARCHAR(MAX) NOT NULL,
    status NVARCHAR(20), -- 'pending', 'approved', 'rejected', 'completed'
    images_json NVARCHAR(MAX),
    admin_note NVARCHAR(MAX),
    approved_by BIGINT,
    approved_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES Users(id) ON DELETE NO ACTION
);
GO

-- =====================================================
-- 2.27 WARRANTIES TABLE
-- =====================================================
CREATE TABLE Warranties (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    variant_id BIGINT,
    issue_description NVARCHAR(MAX) NOT NULL,
    status NVARCHAR(20), -- 'pending', 'approved', 'rejected', 'processing', 'completed'
    warranty_type NVARCHAR(50), -- 'repair', 'replace'
    warranty_months INT DEFAULT 12,
    images_json NVARCHAR(MAX),
    admin_note NVARCHAR(MAX),
    resolution_note NVARCHAR(MAX),
    processed_by BIGINT,
    processed_at DATETIME2,
    completed_at DATETIME2,
    purchase_date DATETIME2,
    submitted_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES Product_Variants(id) ON DELETE NO ACTION,
    FOREIGN KEY (processed_by) REFERENCES Users(id) ON DELETE NO ACTION
);
GO

-- =====================================================
-- 2.28 EMAIL_TEMPLATES TABLE
-- =====================================================
CREATE TABLE Email_Templates (
    id INT IDENTITY(1,1) PRIMARY KEY,
    template_name NVARCHAR(100) NOT NULL UNIQUE,
    subject NVARCHAR(255) NOT NULL,
    body NVARCHAR(MAX) NOT NULL,
    variables NVARCHAR(500),
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2
);
GO

-- =====================================================
-- 2.29 SYSTEM_SETTINGS TABLE
-- =====================================================
CREATE TABLE System_Settings (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    setting_key NVARCHAR(100) NOT NULL UNIQUE,
    setting_value NVARCHAR(MAX),
    setting_type NVARCHAR(50), -- 'store', 'general', 'email', 'payment', 'theme'
    description NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2
);
GO

-- =====================================================
-- 2.30 SIZE_CHARTS TABLE
-- =====================================================
CREATE TABLE Size_Charts (
    id INT IDENTITY(1,1) PRIMARY KEY,
    brand_id INT NOT NULL,
    category NVARCHAR(50) NOT NULL,
    size NVARCHAR(10) NOT NULL,
    size_us NVARCHAR(10),
    size_uk NVARCHAR(10),
    length_cm DECIMAL(5,2),
    width_cm DECIMAL(5,2),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2,
    FOREIGN KEY (brand_id) REFERENCES Brands(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 2.31 NEWSLETTER_SUBSCRIPTIONS TABLE
-- =====================================================
CREATE TABLE Newsletter_Subscriptions (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(255) NOT NULL UNIQUE,
    is_active BIT NOT NULL DEFAULT 1,
    subscribed_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    unsubscribed_at DATETIME2
);
GO

-- =====================================================
-- 2.32 PASSWORD_RESET_TOKEN TABLE
-- =====================================================
CREATE TABLE password_reset_token (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    token NVARCHAR(128) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME2 NOT NULL,
    used_at DATETIME2,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH TAO TABLES!';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 3. CREATE INDEXES
-- =====================================================

PRINT '=====================================================';
PRINT 'DANG TAO INDEXES...';
PRINT '=====================================================';
PRINT '';

-- Users indexes
CREATE INDEX idx_users_email ON Users(email);
CREATE INDEX idx_users_deleted ON Users(deleted_at);
CREATE INDEX idx_users_role_active ON Users(role, is_active);
GO

-- Brands indexes
CREATE INDEX idx_brands_slug ON Brands(slug);
GO

-- Categories indexes
CREATE INDEX idx_categories_slug ON Categories(slug);
CREATE INDEX idx_categories_parent ON Categories(parent_id);
CREATE INDEX idx_categories_lft_rgt ON Categories(lft, rgt);
CREATE INDEX idx_categories_active ON Categories(is_active);
GO

-- Materials indexes
CREATE INDEX idx_materials_slug ON Materials(slug);
GO

-- Shoe_Soles indexes
CREATE INDEX idx_shoe_soles_slug ON Shoe_Soles(slug);
GO

-- Products indexes
CREATE INDEX idx_products_brand ON Products(brand_id);
CREATE INDEX idx_products_slug ON Products(slug);
CREATE INDEX idx_products_deleted ON Products(deleted_at);
CREATE INDEX idx_products_brand_active ON Products(brand_id, is_active);
CREATE INDEX idx_products_featured_active_created ON Products(is_featured, is_active, created_at DESC);
CREATE INDEX idx_products_active_rating ON Products(is_active, avg_rating DESC);
CREATE INDEX idx_products_active_deleted_created ON Products(is_active, deleted_at, created_at DESC);
GO

-- Product_Categories indexes
CREATE INDEX idx_pc_product ON Product_Categories(product_id);
CREATE INDEX idx_pc_category ON Product_Categories(category_id);
GO

-- Product_Variants indexes
CREATE INDEX idx_variants_product ON Product_Variants(product_id);
CREATE INDEX idx_variants_sku ON Product_Variants(sku);
CREATE INDEX idx_variants_product_stock ON Product_Variants(product_id, stock_quantity);
CREATE INDEX idx_variants_product_active ON Product_Variants(product_id, is_active);
CREATE INDEX idx_variants_product_active_stock ON Product_Variants(product_id, is_active, stock_quantity);
GO

-- Product_Images indexes
CREATE INDEX idx_images_product ON Product_Images(product_id);
CREATE INDEX idx_images_primary ON Product_Images(is_primary);
GO

-- Variant_Images indexes
CREATE INDEX idx_variant_images_variant ON Variant_Images(variant_id);
CREATE INDEX idx_variant_images_primary ON Variant_Images(is_primary);
GO

-- Coupons indexes
CREATE INDEX idx_coupons_code ON Coupons(code);
CREATE INDEX idx_coupons_dates ON Coupons(start_at, end_at);
CREATE INDEX idx_coupons_active ON Coupons(is_active);
GO

-- Flash_Sales indexes
CREATE INDEX idx_flashsale_product ON Flash_Sales(product_id);
CREATE INDEX idx_flashsale_active ON Flash_Sales(is_active, start_time, end_time);
GO

-- Addresses indexes
CREATE INDEX idx_addresses_user ON Addresses(user_id);
CREATE INDEX idx_addresses_default ON Addresses(is_default);
GO

-- Carts indexes
CREATE INDEX idx_carts_user ON Carts(user_id);
CREATE INDEX idx_carts_session ON Carts(session_id);
GO

-- Cart_Items indexes
CREATE INDEX idx_cart_items_cart ON Cart_Items(cart_id);
CREATE INDEX idx_cart_items_variant ON Cart_Items(variant_id);
GO

-- Orders indexes
CREATE INDEX idx_orders_user ON Orders(user_id);
CREATE INDEX idx_orders_number ON Orders(order_number);
CREATE INDEX idx_orders_status ON Orders(status);
CREATE INDEX idx_orders_status_created ON Orders(status, created_at DESC);
CREATE INDEX idx_orders_user_status ON Orders(user_id, status);
CREATE INDEX idx_orders_user_status_created ON Orders(user_id, status, created_at DESC);
GO

-- Order_Details indexes
CREATE INDEX idx_order_details_order ON Order_Details(order_id);
CREATE INDEX idx_order_details_variant ON Order_Details(variant_id);
GO

-- Order_Status_Histories indexes
CREATE INDEX idx_status_history_order ON Order_Status_Histories(order_id);
GO

-- Payments indexes
CREATE INDEX idx_payments_order ON Payments(order_id);
CREATE INDEX idx_payments_status ON Payments(status);
CREATE INDEX idx_payments_transaction ON Payments(transaction_id);
CREATE INDEX idx_payments_order_status ON Payments(order_id, status);
GO

-- Reviews indexes
CREATE INDEX idx_reviews_product ON Reviews(product_id);
CREATE INDEX idx_reviews_user ON Reviews(user_id);
CREATE INDEX idx_reviews_approved ON Reviews(is_approved);
CREATE INDEX idx_reviews_product_approved ON Reviews(product_id, is_approved);
CREATE INDEX idx_reviews_product_rating ON Reviews(product_id, rating);
CREATE INDEX idx_reviews_user_product ON Reviews(user_id, product_id);
GO

-- Notifications indexes
CREATE INDEX idx_notifications_user ON Notifications(user_id);
CREATE INDEX idx_notifications_user_read ON Notifications(user_id, is_read);
CREATE INDEX idx_notifications_user_created ON Notifications(user_id, created_at DESC);
GO

-- Inventory_Logs indexes
CREATE INDEX idx_inventory_logs_variant ON Inventory_Logs(variant_id);
CREATE INDEX idx_inventory_variant_created ON Inventory_Logs(variant_id, created_at DESC);
GO

-- Activity_Logs indexes
CREATE INDEX idx_activity_logs_user ON Activity_Logs(user_id);
CREATE INDEX idx_activity_logs_entity ON Activity_Logs(entity_type, entity_id);
CREATE INDEX idx_activity_logs_created ON Activity_Logs(created_at DESC);
GO

-- Wishlists indexes
CREATE INDEX idx_wishlists_user ON Wishlists(user_id);
CREATE INDEX idx_wishlists_product ON Wishlists(product_id);
GO

-- Loyalty_Points indexes
CREATE INDEX idx_loyalty_user ON Loyalty_Points(user_id);
CREATE INDEX idx_loyalty_user_type ON Loyalty_Points(user_id, transaction_type);
GO

-- Return_Requests indexes
CREATE INDEX idx_return_order ON Return_Requests(order_id);
CREATE INDEX idx_return_user ON Return_Requests(user_id);
CREATE INDEX idx_return_status ON Return_Requests(status);
GO

-- Warranties indexes
CREATE INDEX idx_warranty_order ON Warranties(order_id);
CREATE INDEX idx_warranty_user ON Warranties(user_id);
CREATE INDEX idx_warranty_product ON Warranties(product_id);
CREATE INDEX idx_warranty_status ON Warranties(status);
GO

-- System_Settings indexes
CREATE INDEX idx_system_settings_key ON System_Settings(setting_key);
GO

-- Size_Charts indexes
CREATE INDEX idx_sizechart_brand ON Size_Charts(brand_id);
GO

-- Newsletter_Subscriptions indexes
CREATE INDEX idx_newsletter_subscriptions_email ON Newsletter_Subscriptions(email);
CREATE INDEX idx_newsletter_subscriptions_is_active ON Newsletter_Subscriptions(is_active);
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH TAO INDEXES!';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- 4. CREATE VIEWS
-- =====================================================

PRINT '=====================================================';
PRINT 'DANG TAO VIEWS...';
PRINT '=====================================================';
PRINT '';
GO

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
LEFT JOIN Users u ON o.user_id = u.id
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

-- =====================================================
-- 5. CREATE STORED PROCEDURES
-- =====================================================

PRINT '=====================================================';
PRINT 'DANG TAO STORED PROCEDURES...';
PRINT '=====================================================';
PRINT '';
GO

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
    @OrderNumber NVARCHAR(50) OUTPUT
AS
BEGIN
    DECLARE @Date NVARCHAR(8) = FORMAT(GETDATE(), 'yyyyMMdd');
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

-- =====================================================
-- 6. CREATE TRIGGERS
-- =====================================================

PRINT '=====================================================';
PRINT 'DANG TAO TRIGGERS...';
PRINT '=====================================================';
PRINT '';
GO

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

PRINT '';
PRINT '=====================================================';
PRINT 'DATABASE SCHEMA CREATED SUCCESSFULLY!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da tao thanh cong:';
PRINT '  - Database: sneakery_db';
PRINT '  - 32 tables';
PRINT '  - Indexes';
PRINT '  - 3 views';
PRINT '  - 2 stored procedures';
PRINT '  - 2 triggers';
PRINT '';
PRINT 'Bước tiếp theo: Chạy file 2_INSERT_DATA.sql để thêm dữ liệu mẫu.';
PRINT '';


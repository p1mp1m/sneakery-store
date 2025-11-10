-- =====================================================
-- SNEAKERY E-COMMERCE - RUN ALL SCRIPTS (MASTER FILE)
-- =====================================================
-- File này chứa TẤT CẢ các script từ 01-10 để chạy cùng một lúc
-- Bao gồm:
--   01. CREATE DATABASE
--   02. CREATE TABLES
--   03. CREATE INDEXES (BASIC)
--   04. CREATE VIEWS
--   05. CREATE PROCEDURES
--   06. CREATE TRIGGERS
--   07. ALTER TABLES (MIGRATIONS)
--   08. ADD INDEXES (COMPOSITE)
--   09. INSERT DATA
--   10. INSERT IMAGES
-- =====================================================
-- CÁCH SỬ DỤNG:
--   1. Mở file này trong SQL Server Management Studio
--   2. Nhấn F5 để chạy tất cả
-- HOẶC
--   1. Sử dụng sqlcmd: sqlcmd -S localhost -d master -i 00_RUN_ALL.sql
-- =====================================================

PRINT '=====================================================';
PRINT 'SNEAKERY E-COMMERCE - SETUP DATABASE (ALL IN ONE)';
PRINT '=====================================================';
PRINT '';
PRINT 'Bắt đầu chạy tất cả các script...';
PRINT '';

-- =====================================================
-- 01. CREATE DATABASE
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
PRINT 'DATABASE CREATED SUCCESSFULLY!';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- SNEAKERY E-COMMERCE - CREATE TABLES
-- =====================================================
-- File này tạo tất cả 30 tables cho database Sneakery
-- Không bao gồm indexes (indexes được tạo trong file 03_CREATE_INDEXES_BASIC.sql)
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT 'DANG TAO TABLES...';
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
GO

-- =====================================================
-- 4. MATERIALS TABLE (Chất liệu)
-- =====================================================
CREATE TABLE Materials (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL UNIQUE,
    slug NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX) NULL,
    is_active BIT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 NULL,
    deleted_at DATETIME2 NULL
);
GO

-- =====================================================
-- 5. SHOE_SOLES TABLE (Loại đế giày)
-- =====================================================
CREATE TABLE Shoe_Soles (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL UNIQUE,
    slug NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX) NULL,
    is_active BIT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 NULL,
    deleted_at DATETIME2 NULL
);
GO

-- =====================================================
-- 6. PRODUCTS TABLE (Enhanced for Admin Management)
-- =====================================================
CREATE TABLE Products (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    brand_id INT NOT NULL,
    product_code NVARCHAR(50) NULL,
    material_id INT NULL,
    shoe_sole_id INT NULL,
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
    
    main_image_url NVARCHAR(500),
    
    published_at DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    
    CONSTRAINT fk_products_brand FOREIGN KEY (brand_id) REFERENCES Brands(id),
    CONSTRAINT fk_products_material FOREIGN KEY (material_id) REFERENCES Materials(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_products_shoe_sole FOREIGN KEY (shoe_sole_id) REFERENCES Shoe_Soles(id) ON UPDATE CASCADE ON DELETE SET NULL
);
GO

-- =====================================================
-- 7. PRODUCT_CATEGORIES (Many-to-Many)
-- =====================================================
CREATE TABLE Product_Categories (
    product_id BIGINT NOT NULL,
    category_id INT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pc_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT fk_pc_category FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 8. PRODUCT_VARIANTS TABLE (Enhanced for Admin)
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
GO

-- =====================================================
-- 9. PRODUCT_IMAGES TABLE
-- =====================================================
CREATE TABLE Product_Images (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    alt_text NVARCHAR(255),
    is_primary BIT DEFAULT 0,
    display_order INT DEFAULT 0,
    cloudinary_public_id VARCHAR(255) NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_images_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 9a. VARIANT_IMAGES TABLE
-- =====================================================
CREATE TABLE Variant_Images (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    variant_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    alt_text NVARCHAR(255),
    is_primary BIT DEFAULT 0,
    display_order INT DEFAULT 0,
    cloudinary_public_id VARCHAR(255) NULL,
    
    created_at DATETIME2 DEFAULT GETDATE(),
    
    CONSTRAINT fk_variant_images_variant FOREIGN KEY (variant_id) REFERENCES Product_Variants(id) ON DELETE CASCADE
);
GO

-- =====================================================
-- 10. COUPONS TABLE (Admin Management)
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
GO

-- =====================================================
-- 11. FLASH_SALES TABLE (Admin Management)
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
GO

-- =====================================================
-- 12. ADDRESSES TABLE
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
GO

-- =====================================================
-- 13. CARTS TABLE
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
GO

-- =====================================================
-- 14. CART_ITEMS TABLE
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
GO

-- =====================================================
-- 15. ORDERS TABLE (Enhanced for Admin)
-- =====================================================
CREATE TABLE Orders (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NULL, -- NULL cho phép POS orders (khách vãng lai)
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
    
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL,
    CONSTRAINT fk_orders_shipping_address FOREIGN KEY (address_shipping_id) REFERENCES Addresses(id),
    CONSTRAINT fk_orders_billing_address FOREIGN KEY (address_billing_id) REFERENCES Addresses(id),
    CONSTRAINT fk_orders_coupon FOREIGN KEY (coupon_id) REFERENCES Coupons(id)
);
GO

-- =====================================================
-- 16. ORDER_DETAILS TABLE
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
GO

-- =====================================================
-- 17. PAYMENTS TABLE (Admin Management)
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
GO

-- =====================================================
-- 18. REVIEWS TABLE (Admin Management)
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
GO

-- =====================================================
-- 19. NOTIFICATIONS TABLE (Admin Management)
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
GO

-- =====================================================
-- 20. INVENTORY_LOGS TABLE (Admin Management)
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
GO

-- =====================================================
-- 21. ACTIVITY_LOGS TABLE (Admin Management)
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
GO

-- =====================================================
-- 22. WISHLISTS TABLE
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
GO

-- =====================================================
-- 23. LOYALTY_POINTS TABLE
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
GO

-- =====================================================
-- 24. RETURN_REQUESTS TABLE
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
GO

-- =====================================================
-- 25. WARRANTIES TABLE (Admin Management)
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Warranties' AND type = 'U')
BEGIN
    CREATE TABLE Warranties (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        order_id BIGINT NOT NULL,
        user_id BIGINT NOT NULL,
        product_id BIGINT NOT NULL,
        variant_id BIGINT,
        
        issue_description NVARCHAR(MAX) NOT NULL,
        warranty_type VARCHAR(50) CHECK (warranty_type IN ('repair', 'replace')),
        warranty_months INT DEFAULT 12,
        
        status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected', 'in_progress', 'completed')),
        
        images_json NVARCHAR(MAX),
        
        admin_note NVARCHAR(MAX),
        resolution_note NVARCHAR(MAX),
        
        processed_by BIGINT,
        processed_at DATETIME2,
        completed_at DATETIME2,
        
        purchase_date DATETIME2,
        submitted_at DATETIME2 DEFAULT GETDATE(),
        
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE(),
        
        CONSTRAINT fk_warranty_order FOREIGN KEY (order_id) REFERENCES Orders(id),
        CONSTRAINT fk_warranty_user FOREIGN KEY (user_id) REFERENCES Users(id),
        CONSTRAINT fk_warranty_product FOREIGN KEY (product_id) REFERENCES Products(id),
        CONSTRAINT fk_warranty_variant FOREIGN KEY (variant_id) REFERENCES Product_Variants(id),
        CONSTRAINT fk_warranty_processor FOREIGN KEY (processed_by) REFERENCES Users(id)
    );
    
    PRINT 'Table Warranties created successfully.';
END
ELSE
BEGIN
    PRINT 'Table Warranties already exists.';
END
GO

-- =====================================================
-- 26. EMAIL_TEMPLATES TABLE
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
-- 27. SYSTEM_SETTINGS TABLE
-- =====================================================
CREATE TABLE System_Settings (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value NVARCHAR(MAX),
    setting_type VARCHAR(50),
    description NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
GO

-- =====================================================
-- 28. ORDER_STATUS_HISTORIES TABLE
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
GO

-- =====================================================
-- 29. SIZE_CHARTS TABLE
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
GO

-- =====================================================
-- 30. NEWSLETTER_SUBSCRIPTIONS TABLE
-- =====================================================
CREATE TABLE Newsletter_Subscriptions (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    email NVARCHAR(255) NOT NULL UNIQUE,
    is_active BIT NOT NULL DEFAULT 1,
    subscribed_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    unsubscribed_at DATETIME2 NULL
);
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH TAO TABLES!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da tao thanh cong 30 tables.';
PRINT 'Bước tiếp theo: Chạy file 03_CREATE_INDEXES_BASIC.sql để tạo indexes.';
PRINT '';

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

-- =====================================================
-- SNEAKERY E-COMMERCE - CREATE TRIGGERS
-- =====================================================
-- File này tạo tất cả triggers cho database
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT 'DANG TAO TRIGGERS...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- TRIGGERS
-- =====================================================
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
PRINT 'Da tao thanh cong 2 triggers.';
PRINT 'Bước tiếp theo: Chạy file 07_ALTER_TABLES.sql để chạy migrations.';
PRINT '';

-- =====================================================
-- SNEAKERY E-COMMERCE - ALTER TABLES (MIGRATIONS)
-- =====================================================
-- File này chạy các migrations để cập nhật schema cho các tính năng mới
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

PRINT '=====================================================';
PRINT 'DANG CHAY MIGRATIONS...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- MIGRATIONS (Cập nhật schema cho các tính năng mới)
-- =====================================================

-- Migration: Allow NULL user_id in Orders table for POS orders
-- Date: 2025-11-07
-- Description: Cho phép user_id NULL để hỗ trợ POS orders (khách vãng lai)
PRINT 'Migration: Allow NULL user_id in Orders table for POS orders...';
GO

-- Bước 1: Drop foreign key constraint hiện tại nếu tồn tại
IF EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'fk_orders_user')
BEGIN
    ALTER TABLE Orders DROP CONSTRAINT fk_orders_user;
    PRINT '  - Dropped foreign key constraint fk_orders_user';
END
ELSE
BEGIN
    PRINT '  - Foreign key constraint fk_orders_user not found (may be new schema)';
END
GO

-- Bước 2: ALTER TABLE để cho phép user_id NULL (nếu chưa NULL)
IF EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Orders' 
    AND COLUMN_NAME = 'user_id' 
    AND IS_NULLABLE = 'NO'
)
BEGIN
    ALTER TABLE Orders ALTER COLUMN user_id BIGINT NULL;
    PRINT '  - Altered user_id column to allow NULL';
END
ELSE
BEGIN
    PRINT '  - user_id column already allows NULL (may be new schema)';
END
GO

-- Bước 3: Tạo lại foreign key constraint với ON DELETE SET NULL
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'fk_orders_user')
BEGIN
    ALTER TABLE Orders
    ADD CONSTRAINT fk_orders_user 
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE SET NULL;
    PRINT '  - Created foreign key constraint fk_orders_user with ON DELETE SET NULL';
END
ELSE
BEGIN
    PRINT '  - Foreign key constraint fk_orders_user already exists';
END
GO

PRINT '  - Migration completed successfully!';
PRINT '  - Orders table now allows NULL user_id for POS orders (walk-in customers)';
GO

-- Migration: Add main_image_url column to Products table
-- Date: 2025-11-07
-- Description: Thêm cột main_image_url vào bảng Products nếu chưa tồn tại
PRINT 'Migration: Add main_image_url column to Products table...';
GO

IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_NAME = 'Products' 
    AND COLUMN_NAME = 'main_image_url'
)
BEGIN
    ALTER TABLE Products ADD main_image_url NVARCHAR(500) NULL;
    PRINT '  - Added main_image_url column to Products table';
END
ELSE
BEGIN
    PRINT '  - main_image_url column already exists in Products table';
END
GO

PRINT '  - Migration completed successfully!';
GO

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH CHAY MIGRATIONS!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da chay thanh cong tat ca migrations.';
PRINT 'Bước tiếp theo: Chạy file 08_ADD_INDEXES_COMPOSITE.sql để thêm composite indexes.';
PRINT '';

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

-- =====================================================
-- SNEAKERY E-COMMERCE - INSERT DATA (QUY MO NHO)
-- =====================================================
-- File này insert dữ liệu mẫu cho database Sneakery
-- Quy mô nhỏ phù hợp cho development và testing
-- Thời gian: từ 1/8/2025 đến 1/11/2025
-- Tuân thủ thứ tự foreign key dependency để đảm bảo tính toàn vẹn
-- Dữ liệu được đồng bộ và thống nhất
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

BEGIN TRANSACTION; -- Wrap all inserts in a transaction for safety

PRINT '=====================================================';
PRINT 'DANG THEM DU LIEU (QUY MO NHO)...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- TIER 1: TABLES WITHOUT DEPENDENCIES
-- =====================================================
PRINT 'TIER 1: Dang them Users, Brands, Categories, Materials, Shoe_Soles, Email_Templates...';

-- 1. USERS (10 users: 2 admins + 8 regular)
INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified, created_at) VALUES
-- Admin users
('admin@sneakery.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Admin Sneakery', '0123456789', 'ADMIN', 1, 1, '2025-08-01 08:00:00'),
('moderator@sneakery.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Moderator Sneakery', '0123456790', 'MODERATOR', 1, 1, '2025-08-01 08:30:00'),
-- Regular users
('nguyen.van.a@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Nguyễn Văn An', '0987654321', 'USER', 1, 1, '2025-08-02 09:00:00'),
('tran.thi.b@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Bình', '0987654322', 'USER', 1, 1, '2025-08-02 10:00:00'),
('le.van.c@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Cường', '0987654323', 'USER', 1, 1, '2025-08-03 11:00:00'),
('pham.thi.d@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Dung', '0987654324', 'USER', 1, 1, '2025-08-03 12:00:00'),
('hoang.van.e@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Em', '0987654325', 'USER', 1, 1, '2025-08-04 13:00:00'),
('vu.thi.f@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Phương', '0987654326', 'USER', 1, 1, '2025-08-04 14:00:00'),
('dang.van.g@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn Giang', '0987654327', 'USER', 1, 1, '2025-08-05 15:00:00'),
('bui.thi.h@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Hoa', '0987654328', 'USER', 1, 1, '2025-08-05 16:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' users';

-- 2. BRANDS (6 brands)
INSERT INTO Brands (name, slug, logo_url, description, is_active) VALUES
(N'Nike', 'nike', 'https://example.com/logos/nike.png', N'Just Do It - Thương hiệu thể thao hàng đầu thế giới', 1),
(N'Adidas', 'adidas', 'https://example.com/logos/adidas.png', N'Impossible is Nothing - Thương hiệu thể thao Đức', 1),
(N'Jordan', 'jordan', 'https://example.com/logos/jordan.png', N'Air Jordan - Dòng giày bóng rổ huyền thoại', 1),
(N'Converse', 'converse', 'https://example.com/logos/converse.png', N'Chuck Taylor All Star - Giày canvas cổ điển', 1),
(N'Vans', 'vans', 'https://example.com/logos/vans.png', N'Off The Wall - Thương hiệu skateboard', 1),
(N'New Balance', 'new-balance', 'https://example.com/logos/new-balance.png', N'Made in USA - Chất lượng cao cấp', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' brands';

-- 2a. MATERIALS (6 materials)
INSERT INTO Materials (name, slug, description, is_active) VALUES
(N'Da Thật', 'da-that', N'Da thật cao cấp, bền bỉ và thoáng khí', 1),
(N'Da Tổng Hợp', 'da-tong-hop', N'Da tổng hợp có độ bền cao và dễ bảo quản', 1),
(N'Vải Canvas', 'vai-canvas', N'Vải canvas bền chắc, thoáng khí', 1),
(N'Vải Mesh', 'vai-mesh', N'Vải mesh nhẹ, thoáng khí cho giày thể thao', 1),
(N'Knit', 'knit', N'Chất liệu knit linh hoạt và hiện đại', 1),
(N'Microfiber', 'microfiber', N'Microfiber mềm mại, bền và dễ vệ sinh', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' materials';

-- 2b. SHOE_SOLES (6 types)
INSERT INTO Shoe_Soles (name, slug, description, is_active) VALUES
(N'Cao Su', 'cao-su', N'Đế cao su tự nhiên có độ bền cao', 1),
(N'EVA', 'eva', N'Đế EVA nhẹ, đàn hồi tốt', 1),
(N'TPU', 'tpu', N'Đế TPU cứng chắc và bền bỉ', 1),
(N'Air Cushion', 'air-cushion', N'Đế công nghệ Air Cushion với túi khí', 1),
(N'Boost', 'boost', N'Đế Boost với hạt TPU nén', 1),
(N'React', 'react', N'Đế React với foam đàn hồi', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' shoe soles';

-- 3. CATEGORIES (8 categories: 2 parents + 6 children)
INSERT INTO Categories (name, slug, description, parent_id, lft, rgt, level, is_active, display_order) VALUES
-- Main categories (level 0)
(N'Giày Nam', 'giay-nam', N'Giày thể thao và giày thời trang cho nam', NULL, 1, 10, 0, 1, 1),
(N'Giày Nữ', 'giay-nu', N'Giày thể thao và giày thời trang cho nữ', NULL, 11, 20, 0, 1, 2),
-- Subcategories for Men (level 1)
(N'Giày Chạy Bộ Nam', 'giay-chay-bo-nam', N'Giày chạy bộ cho nam', 1, 2, 5, 1, 1, 1),
(N'Giày Bóng Rổ Nam', 'giay-bong-ro-nam', N'Giày bóng rổ cho nam', 1, 6, 9, 1, 1, 2),
-- Subcategories for Women (level 1)
(N'Giày Chạy Bộ Nữ', 'giay-chay-bo-nu', N'Giày chạy bộ cho nữ', 2, 12, 15, 1, 1, 1),
(N'Giày Thời Trang Nữ', 'giay-thoi-trang-nu', N'Giày thời trang cho nữ', 2, 16, 19, 1, 1, 2);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' categories';

-- 4. EMAIL_TEMPLATES (3 templates)
INSERT INTO Email_Templates (template_name, subject, body, variables, is_active) VALUES
('welcome', N'Chào mừng bạn đến với Sneakery', N'<h1>Chào mừng bạn đến với Sneakery!</h1><p>Xin chào {{full_name}},</p><p>Cảm ơn bạn đã đăng ký tài khoản tại Sneakery.</p>', 'full_name,email', 1),
('order_confirmation', N'Xác nhận đơn hàng #{{order_number}}', N'<h1>Xác nhận đơn hàng</h1><p>Xin chào {{customer_name}},</p><p>Đơn hàng #{{order_number}} của bạn đã được xác nhận.</p>', 'customer_name,order_number,total_amount', 1),
('password_reset', N'Đặt lại mật khẩu Sneakery', N'<h1>Đặt lại mật khẩu</h1><p>Xin chào {{full_name}},</p><p>Bạn đã yêu cầu đặt lại mật khẩu.</p>', 'full_name,reset_link', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' email templates';

PRINT 'TIER 1 HOAN THANH!';
GO

-- =====================================================
-- TIER 2: DEPENDS ON BRANDS/CATEGORIES
-- =====================================================
PRINT '';
PRINT 'TIER 2: Dang them Products va Product_Categories...';

-- 5. PRODUCTS (20 products)
INSERT INTO Products (brand_id, product_code, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
-- Nike Products (brand_id = 1)
(1, 'SP00001', N'Nike Air Max 270', 'nike-air-max-270', N'Giày chạy bộ Nike Air Max 270 với công nghệ Air Max đột phá, mang lại cảm giác êm ái và năng động cho mỗi bước chạy.', N'Giày chạy bộ Nike Air Max 270', 1, 1, 1, 1250, 45, 4.5, 23),
(1, 'SP00002', N'Nike Air Force 1', 'nike-air-force-1', N'Giày bóng rổ cổ điển Nike Air Force 1 với thiết kế đơn giản nhưng đầy phong cách, phù hợp cho mọi hoạt động.', N'Giày bóng rổ Nike Air Force 1', 1, 1, 0, 2100, 78, 4.7, 56),
(1, 'SP00003', N'Nike Dunk Low', 'nike-dunk-low', N'Giày lifestyle Nike Dunk Low với thiết kế cổ điển và phong cách streetwear.', N'Giày lifestyle Nike Dunk Low', 1, 1, 0, 1500, 35, 4.4, 22),
(1, 'SP00004', N'Nike Pegasus 39', 'nike-pegasus-39', N'Giày chạy bộ Nike Pegasus 39 với công nghệ React foam, thoải mái và bền bỉ.', N'Giày chạy bộ Nike Pegasus 39', 1, 0, 1, 1350, 42, 4.5, 28),
-- Adidas Products (brand_id = 2)
(2, 'SP00005', N'Adidas Ultraboost 22', 'adidas-ultraboost-22', N'Giày chạy bộ Adidas Ultraboost 22 với công nghệ Boost đột phá, mang lại năng lượng và sự thoải mái tối đa.', N'Giày chạy bộ Adidas Ultraboost 22', 1, 1, 1, 1680, 67, 4.6, 42),
(2, 'SP00006', N'Adidas Stan Smith', 'adidas-stan-smith', N'Giày tennis cổ điển Adidas Stan Smith với thiết kế đơn giản, thanh lịch và dễ phối đồ.', N'Giày tennis Adidas Stan Smith', 1, 0, 0, 1950, 89, 4.4, 67),
(2, 'SP00007', N'Adidas NMD R1', 'adidas-nmd-r1', N'Giày lifestyle Adidas NMD R1 với thiết kế hiện đại và công nghệ Boost.', N'Giày lifestyle Adidas NMD R1', 1, 1, 0, 1420, 54, 4.2, 35),
(2, 'SP00008', N'Adidas Superstar', 'adidas-superstar', N'Giày sneakers cổ điển Adidas Superstar với vỏ sò đặc trưng.', N'Giày sneakers Adidas Superstar', 1, 1, 0, 1800, 42, 4.5, 28),
-- Jordan Products (brand_id = 3)
(3, 'SP00009', N'Air Jordan 1 Retro High', 'air-jordan-1-retro-high', N'Giày bóng rổ Air Jordan 1 Retro High - phiên bản tái tạo của đôi giày huyền thoại từ năm 1985.', N'Giày bóng rổ Air Jordan 1 Retro High', 1, 1, 1, 3200, 156, 4.8, 89),
(3, 'SP00010', N'Air Jordan 4 Retro', 'air-jordan-4-retro', N'Giày bóng rổ Air Jordan 4 Retro với thiết kế đặc trưng và công nghệ Air-Sole.', N'Giày bóng rổ Air Jordan 4 Retro', 1, 1, 0, 2800, 134, 4.7, 76),
(3, 'SP00011', N'Air Jordan 3 Retro', 'air-jordan-3-retro', N'Giày bóng rổ Air Jordan 3 với Elephant Print nổi tiếng.', N'Giày bóng rổ Air Jordan 3', 1, 0, 1, 2100, 89, 4.6, 52),
-- Converse Products (brand_id = 4)
(4, 'SP00012', N'Converse Chuck Taylor All Star', 'converse-chuck-taylor-all-star', N'Giày canvas cổ điển Converse Chuck Taylor All Star với thiết kế đơn giản, phù hợp cho mọi phong cách.', N'Giày canvas Converse Chuck Taylor All Star', 1, 0, 0, 1800, 112, 4.3, 45),
(4, 'SP00013', N'Converse Chuck 70', 'converse-chuck-70', N'Giày canvas Converse Chuck 70 cao cấp với chất liệu tốt hơn.', N'Giày canvas Converse Chuck 70', 1, 1, 0, 950, 24, 4.3, 14),
(4, 'SP00014', N'Converse One Star', 'converse-one-star', N'Giày lifestyle Converse One Star với thiết kế đơn giản và phong cách streetwear.', N'Giày lifestyle Converse One Star', 1, 0, 1, 950, 28, 4.1, 12),
-- Vans Products (brand_id = 5)
(5, 'SP00015', N'Vans Old Skool', 'vans-old-skool', N'Giày skateboard Vans Old Skool với thiết kế cổ điển và chất lượng bền bỉ.', N'Giày skateboard Vans Old Skool', 1, 1, 0, 1650, 73, 4.4, 38),
(5, 'SP00016', N'Vans Authentic', 'vans-authentic', N'Giày canvas Vans Authentic với thiết kế đơn giản và phong cách skateboard cổ điển.', N'Giày canvas Vans Authentic', 1, 0, 0, 1200, 45, 4.2, 25),
(5, 'SP00017', N'Vans Sk8-Hi', 'vans-sk8-hi', N'Giày skateboard Vans Sk8-Hi high-top với thiết kế cổ điển.', N'Giày skateboard Vans Sk8-Hi', 1, 1, 0, 1400, 38, 4.4, 21),
-- New Balance Products (brand_id = 6)
(6, 'SP00018', N'New Balance 550', 'new-balance-550', N'Giày sneakers New Balance 550 với thiết kế retro và cảm giác thoải mái.', N'Giày sneakers New Balance 550', 1, 1, 0, 1600, 41, 4.5, 26),
(6, 'SP00019', N'New Balance 574', 'new-balance-574', N'Giày sneakers New Balance 574 cổ điển với chất lượng cao.', N'Giày sneakers New Balance 574', 1, 1, 1, 1750, 52, 4.6, 38),
(6, 'SP00020', N'New Balance 993', 'new-balance-993', N'Giày chạy bộ New Balance 993 với công nghệ ENCAP và đệm êm ái.', N'Giày chạy bộ New Balance 993', 1, 0, 0, 980, 19, 4.3, 11);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' products';

-- 6. PRODUCT_CATEGORIES (20 relationships)
INSERT INTO Product_Categories (product_id, category_id) VALUES
-- Nike products -> Running/Basketball
(1, 3), -- Nike Air Max 270 -> Giày Chạy Bộ Nam
(2, 4), -- Nike Air Force 1 -> Giày Bóng Rổ Nam
(3, 3), -- Nike Dunk Low -> Giày Chạy Bộ Nam
(4, 3), -- Nike Pegasus 39 -> Giày Chạy Bộ Nam
-- Adidas products -> Running/Fashion
(5, 5), -- Adidas Ultraboost 22 -> Giày Chạy Bộ Nữ
(6, 6), -- Adidas Stan Smith -> Giày Thời Trang Nữ
(7, 5), -- Adidas NMD R1 -> Giày Chạy Bộ Nữ
(8, 6), -- Adidas Superstar -> Giày Thời Trang Nữ
-- Jordan products -> Basketball
(9, 4), -- Air Jordan 1 -> Giày Bóng Rổ Nam
(10, 4), -- Air Jordan 4 -> Giày Bóng Rổ Nam
(11, 4), -- Air Jordan 3 -> Giày Bóng Rổ Nam
-- Converse products -> Running/Fashion
(12, 3), -- Converse Chuck Taylor -> Giày Chạy Bộ Nam
(13, 3), -- Converse Chuck 70 -> Giày Chạy Bộ Nam
(14, 3), -- Converse One Star -> Giày Chạy Bộ Nam
-- Vans products -> Running
(15, 3), -- Vans Old Skool -> Giày Chạy Bộ Nam
(16, 3), -- Vans Authentic -> Giày Chạy Bộ Nam
(17, 3), -- Vans Sk8-Hi -> Giày Chạy Bộ Nam
-- New Balance products -> Running
(18, 3), -- New Balance 550 -> Giày Chạy Bộ Nam
(19, 3), -- New Balance 574 -> Giày Chạy Bộ Nam
(20, 3); -- New Balance 993 -> Giày Chạy Bộ Nam
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product categories';

PRINT 'TIER 2 HOAN THANH!';
GO

-- =====================================================
-- TIER 3: DEPENDS ON PRODUCTS
-- =====================================================
PRINT '';
PRINT 'TIER 3: Dang them Product_Variants, Coupons, Flash_Sales...';

-- 7. PRODUCT_VARIANTS (40 variants: 2 per product)
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, low_stock_threshold, is_active) VALUES
-- Product 1: Nike Air Max 270
(1, 'NIKE-AM270-BLK-40', '40', N'Đen', 3200000, 2800000, 2000000, 25, 5, 1),
(1, 'NIKE-AM270-BLK-41', '41', N'Đen', 3200000, 2800000, 2000000, 30, 5, 1),
-- Product 2: Nike Air Force 1
(2, 'NIKE-AF1-BLK-40', '40', N'Đen', 2500000, 2200000, 1500000, 35, 5, 1),
(2, 'NIKE-AF1-WHT-40', '40', N'Trắng', 2500000, 2200000, 1500000, 32, 5, 1),
-- Product 3: Nike Dunk Low
(3, 'NIKE-DL-BLK-40', '40', N'Đen', 2800000, 2500000, 1500000, 20, 5, 1),
(3, 'NIKE-DL-WHT-40', '40', N'Trắng', 2800000, 2500000, 1500000, 18, 5, 1),
-- Product 4: Nike Pegasus 39
(4, 'NIKE-PEG-BLK-40', '40', N'Đen', 3200000, 2900000, 2000000, 20, 5, 1),
(4, 'NIKE-PEG-BLU-40', '40', N'Xanh dương', 3200000, 2900000, 2000000, 18, 5, 1),
-- Product 5: Adidas Ultraboost 22
(5, 'ADIDAS-UB22-BLK-36', '36', N'Đen', 4500000, 4000000, 2800000, 20, 5, 1),
(5, 'ADIDAS-UB22-BLK-37', '37', N'Đen', 4500000, 4000000, 2800000, 25, 5, 1),
-- Product 6: Adidas Stan Smith
(6, 'ADIDAS-SS-WHT-36', '36', N'Trắng', 2100000, 1900000, 1200000, 30, 10, 1),
(6, 'ADIDAS-SS-WHT-37', '37', N'Trắng', 2100000, 1900000, 1200000, 28, 10, 1),
-- Product 7: Adidas NMD R1
(7, 'ADIDAS-NMD-BLK-36', '36', N'Đen', 3200000, 2900000, 2000000, 20, 5, 1),
(7, 'ADIDAS-NMD-BLK-37', '37', N'Đen', 3200000, 2900000, 2000000, 22, 5, 1),
-- Product 8: Adidas Superstar
(8, 'ADIDAS-SUP-BLK-36', '36', N'Đen', 2400000, 2200000, 1400000, 25, 5, 1),
(8, 'ADIDAS-SUP-WHT-36', '36', N'Trắng', 2400000, 2200000, 1400000, 23, 5, 1),
-- Product 9: Air Jordan 1
(9, 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 5500000, 5000000, 3500000, 15, 3, 1),
(9, 'JORDAN-1-BLK-41', '41', N'Đen/Trắng', 5500000, 5000000, 3500000, 18, 3, 1),
-- Product 10: Air Jordan 4
(10, 'JORDAN-4-BLK-40', '40', N'Đen/Xám', 4800000, 4400000, 3200000, 15, 3, 1),
(10, 'JORDAN-4-BLK-41', '41', N'Đen/Xám', 4800000, 4400000, 3200000, 18, 3, 1),
-- Product 11: Air Jordan 3
(11, 'JORDAN-3-BLK-40', '40', N'Đen/Trắng', 5200000, 4800000, 3300000, 15, 3, 1),
(11, 'JORDAN-3-BLK-41', '41', N'Đen/Trắng', 5200000, 4800000, 3300000, 17, 3, 1),
-- Product 12: Converse Chuck Taylor
(12, 'CONVERSE-CT-BLK-40', '40', N'Đen', 1200000, 1000000, 600000, 50, 10, 1),
(12, 'CONVERSE-CT-WHT-40', '40', N'Trắng', 1200000, 1000000, 600000, 45, 10, 1),
-- Product 13: Converse Chuck 70
(13, 'CONVERSE-70-BLK-40', '40', N'Đen', 1500000, 1300000, 800000, 40, 10, 1),
(13, 'CONVERSE-70-WHT-40', '40', N'Trắng', 1500000, 1300000, 800000, 35, 10, 1),
-- Product 14: Converse One Star
(14, 'CONVERSE-OS-BLK-40', '40', N'Đen', 1500000, 1300000, 800000, 40, 10, 1),
(14, 'CONVERSE-OS-BLK-41', '41', N'Đen', 1500000, 1300000, 800000, 35, 10, 1),
-- Product 15: Vans Old Skool
(15, 'VANS-OS-BLK-40', '40', N'Đen/Trắng', 1800000, 1600000, 1000000, 35, 5, 1),
(15, 'VANS-OS-BLK-41', '41', N'Đen/Trắng', 1800000, 1600000, 1000000, 40, 5, 1),
-- Product 16: Vans Authentic
(16, 'VANS-AUTH-BLK-40', '40', N'Đen', 1600000, 1400000, 900000, 30, 5, 1),
(16, 'VANS-AUTH-WHT-40', '40', N'Trắng', 1600000, 1400000, 900000, 35, 5, 1),
-- Product 17: Vans Sk8-Hi
(17, 'VANS-SK8-BLK-40', '40', N'Đen/Trắng', 1900000, 1700000, 1100000, 30, 5, 1),
(17, 'VANS-SK8-BLK-41', '41', N'Đen/Trắng', 1900000, 1700000, 1100000, 32, 5, 1),
-- Product 18: New Balance 550
(18, 'NB-550-BLK-40', '40', N'Đen', 3200000, 2900000, 2000000, 30, 5, 1),
(18, 'NB-550-GRY-40', '40', N'Xám', 3200000, 2900000, 2000000, 25, 5, 1),
-- Product 19: New Balance 574
(19, 'NB-574-BLK-40', '40', N'Đen', 2800000, 2600000, 1800000, 35, 5, 1),
(19, 'NB-574-GRY-40', '40', N'Xám', 2800000, 2600000, 1800000, 30, 5, 1),
-- Product 20: New Balance 993
(20, 'NB-993-GRY-40', '40', N'Xám', 3800000, 3500000, 2500000, 20, 5, 1),
(20, 'NB-993-GRY-41', '41', N'Xám', 3800000, 3500000, 2500000, 22, 5, 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product variants';

-- NOTE: Product_Images và Variant_Images đã được tách ra file 10_INSERT_IMAGES.sql riêng
-- Chạy file 10_INSERT_IMAGES.sql sau khi chạy file này để thêm dữ liệu ảnh

-- 9. COUPONS (5 coupons)
INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, applicable_to, is_active) VALUES
('WELCOME10', N'Giảm 10% cho khách hàng mới', 'percentage', 10.00, 500000, 500000, '2025-08-01', '2025-11-01', 1000, 45, 1, 'all', 1),
('SAVE50K', N'Giảm 50,000 VNĐ cho đơn hàng từ 1,000,000 VNĐ', 'fixed', 50000, 1000000, 50000, '2025-08-01', '2025-11-01', 500, 23, 2, 'all', 1),
('NIKE20', N'Giảm 20% cho giày Nike', 'percentage', 20.00, 2000000, 1000000, '2025-08-01', '2025-11-01', 200, 12, 1, 'brand', 1),
('SUMMER15', N'Giảm 15% cho mùa hè', 'percentage', 15.00, 800000, 300000, '2025-09-01', '2025-10-31', 300, 8, 1, 'all', 1),
('VIP30', N'Giảm 30% cho khách VIP', 'percentage', 30.00, 3000000, 2000000, '2025-08-01', '2025-11-01', 50, 3, 1, 'all', 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' coupons';

-- 10. FLASH_SALES (5 flash sales)
INSERT INTO Flash_Sales (product_id, discount_percent, start_time, end_time, quantity_limit, sold_count, is_active) VALUES
(1, 25.00, '2025-08-16 00:00:00', '2025-08-16 23:59:59', 50, 12, 1),
(5, 20.00, '2025-08-16 00:00:00', '2025-08-16 23:59:59', 40, 15, 1),
(9, 15.00, '2025-08-16 00:00:00', '2025-08-16 23:59:59', 20, 5, 1),
(15, 30.00, '2025-08-17 00:00:00', '2025-08-17 23:59:59', 100, 28, 1),
(18, 25.00, '2025-08-17 00:00:00', '2025-08-17 23:59:59', 45, 22, 1);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' flash sales';

PRINT 'TIER 3 HOAN THANH!';
GO

-- =====================================================
-- TIER 4: DEPENDS ON USERS
-- =====================================================
PRINT '';
PRINT 'TIER 4: Dang them Addresses, Notifications, Wishlists, Carts...';

-- 11. ADDRESSES (8 addresses for users 3-6)
INSERT INTO Addresses (user_id, recipient_name, phone, line1, line2, ward, district, city, postal_code, is_default, address_type, created_at) VALUES
(3, N'Nguyễn Văn An', '0987654321', N'123 Đường Lê Lợi', N'Tầng 5, Chung cư ABC', N'Phường Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2025-08-05 10:00:00'),
(3, N'Nguyễn Văn An', '0987654321', N'456 Đường Nguyễn Huệ', N'Căn hộ 302', N'Phường Đa Kao', N'Quận 1', N'TP. Hồ Chí Minh', '700000', 0, 'office', '2025-08-05 11:00:00'),
(4, N'Trần Thị Bình', '0987654322', N'789 Đường Cách Mạng Tháng 8', N'Căn hộ 205', N'Phường 10', N'Quận 3', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2025-08-06 12:00:00'),
(4, N'Trần Thị Bình', '0987654322', N'321 Đường Võ Văn Tần', N'Tầng 4', N'Phường 6', N'Quận 3', N'TP. Hồ Chí Minh', '700000', 0, 'office', '2025-08-06 13:00:00'),
(5, N'Lê Văn Cường', '0987654323', N'654 Đường Điện Biên Phủ', N'Căn hộ 101', N'Phường 25', N'Quận Bình Thạnh', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2025-08-07 14:00:00'),
(5, N'Lê Văn Cường', '0987654323', N'111 Đường Hoàng Văn Thụ', N'Tầng 8', N'Phường 4', N'Quận Phú Nhuận', N'TP. Hồ Chí Minh', '700000', 0, 'office', '2025-08-07 15:00:00'),
(6, N'Phạm Thị Dung', '0987654324', N'222 Đường Lê Đức Thọ', N'Căn hộ 403', N'Phường Đông Hưng Thuận', N'Quận 12', N'TP. Hồ Chí Minh', '700000', 1, 'home', '2025-08-08 16:00:00'),
(6, N'Phạm Thị Dung', '0987654324', N'333 Đường Nguyễn Thái Sơn', N'Tầng 12', N'Phường 3', N'Quận Gò Vấp', N'TP. Hồ Chí Minh', '700000', 0, 'office', '2025-08-08 17:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' addresses';

-- 12. NOTIFICATIONS (10 notifications)
INSERT INTO Notifications (user_id, type, title, message, link, is_read, created_at) VALUES
(3, 'system', N'Chào mừng bạn đến với Sneakery', N'Cảm ơn bạn đã đăng ký tài khoản!', '/', 1, '2025-08-05 10:00:00'),
(4, 'promotion', N'Khuyến mãi mới', N'Giảm giá 20% cho tất cả giày. Áp dụng đến hết tháng 10.', '/products', 0, '2025-08-16 09:00:00'),
(5, 'product_restock', N'Sản phẩm đã có hàng trở lại', N'Nike Air Max 270 màu đen đã có hàng trở lại!', '/products/1', 0, '2025-08-16 10:00:00'),
(6, 'review_reply', N'Admin đã trả lời đánh giá của bạn', N'Cảm ơn bạn đã đánh giá sản phẩm Adidas Stan Smith!', '/products/6', 1, '2025-08-16 14:00:00'),
(3, 'order_status', N'Đơn hàng đang được xử lý', N'Đơn hàng của bạn đang được chuẩn bị.', '/orders', 0, '2025-08-16 11:00:00'),
(4, 'order_status', N'Đơn hàng đã được giao', N'Đơn hàng của bạn đã được giao thành công.', '/orders', 1, '2025-08-17 16:00:00'),
(5, 'promotion', N'Flash Sale sắp bắt đầu!', N'Flash Sale 50% sẽ bắt đầu trong 1 giờ nữa.', '/flash-sales', 0, '2025-08-17 08:00:00'),
(6, 'system', N'Thông báo hệ thống', N'Hệ thống sẽ bảo trì vào đêm nay từ 23:00 đến 1:00.', '/', 0, '2025-08-17 20:00:00'),
(3, 'order_status', N'Đơn hàng đã được xác nhận', N'Đơn hàng ORD-20250810-0001 của bạn đã được xác nhận.', '/orders', 0, '2025-08-10 13:30:00'),
(4, 'product_restock', N'Sản phẩm yêu thích đã có hàng', N'Air Jordan 1 Retro High đã có hàng trở lại!', '/products/9', 1, '2025-08-15 10:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' notifications';

-- 13. WISHLISTS (5 wishlist items)
INSERT INTO Wishlists (user_id, product_id, created_at) VALUES
(3, 1, '2025-08-06 10:00:00'),
(3, 9, '2025-08-06 14:30:00'),
(4, 5, '2025-08-07 09:15:00'),
(5, 2, '2025-08-08 11:45:00'),
(6, 18, '2025-08-09 15:15:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' wishlist items';

-- 14. CARTS (5 carts for users 3-7)
INSERT INTO Carts (user_id, session_id, created_at, updated_at) VALUES
(3, NULL, '2025-08-16 10:00:00', '2025-08-16 10:00:00'),
(4, NULL, '2025-08-16 14:00:00', '2025-08-16 14:00:00'),
(5, NULL, '2025-08-16 09:00:00', '2025-08-16 09:00:00'),
(6, NULL, '2025-08-16 16:00:00', '2025-08-16 16:00:00'),
(7, NULL, '2025-08-17 11:00:00', '2025-08-17 11:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' carts';

PRINT 'TIER 4 HOAN THANH!';
GO

-- =====================================================
-- TIER 5: DEPENDS ON ADDRESSES
-- =====================================================
PRINT '';
PRINT 'TIER 5: Dang them Orders...';

-- 15. ORDERS (8 orders for users 3-6)
INSERT INTO Orders (user_id, order_number, address_shipping_id, address_billing_id, subtotal, shipping_fee, discount_amount, total_amount, status, shipping_method, customer_note, created_at) VALUES
-- Orders for user 3
(3, 'ORD-20250810-0001', 1, 1, 2800000, 50000, 280000, 2570000, 'delivered', 'standard', N'Giao hàng vào buổi chiều', '2025-08-10 10:30:00'),
(3, 'ORD-20250815-0001', 2, 2, 3500000, 100000, 350000, 3250000, 'shipped', 'express', N'Giao hàng nhanh', '2025-08-15 14:00:00'),
-- Orders for user 4
(4, 'ORD-20250811-0001', 3, 3, 1900000, 50000, 0, 1950000, 'delivered', 'standard', NULL, '2025-08-11 11:00:00'),
(4, 'ORD-20250818-0001', 4, 4, 2900000, 50000, 290000, 2660000, 'processing', 'express', N'Kiểm tra kỹ sản phẩm', '2025-08-18 09:30:00'),
-- Orders for user 5
(5, 'ORD-20250812-0001', 5, 5, 4000000, 100000, 400000, 3700000, 'delivered', 'standard', NULL, '2025-08-12 15:00:00'),
(5, 'ORD-20250820-0001', 6, 6, 2200000, 50000, 220000, 2030000, 'confirmed', 'standard', N'Giao hàng nhanh', '2025-08-20 10:00:00'),
-- Orders for user 6
(6, 'ORD-20250814-0001', 7, 7, 5000000, 100000, 500000, 4600000, 'processing', 'express', NULL, '2025-08-14 13:00:00'),
(6, 'ORD-20250822-0001', 8, 8, 1600000, 30000, 0, 1630000, 'delivered', 'standard', NULL, '2025-08-22 16:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' orders';

PRINT 'TIER 5 HOAN THANH!';
GO

-- =====================================================
-- TIER 6: DEPENDS ON ORDERS + PRODUCT_VARIANTS
-- =====================================================
PRINT '';
PRINT 'TIER 6: Dang them Order_Details, Payments, Order_Status_Histories...';

-- 16. ORDER_DETAILS (8 details for 8 orders)
INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price) VALUES
(1, 1, N'Nike Air Max 270', 'NIKE-AM270-BLK-40', '40', N'Đen', 1, 2800000, 2800000),
(2, 20, N'New Balance 993', 'NB-993-GRY-40', '40', N'Xám', 1, 3500000, 3500000),
(3, 11, N'Adidas Stan Smith', 'ADIDAS-SS-WHT-36', '36', N'Trắng', 1, 1900000, 1900000),
(4, 18, N'New Balance 550', 'NB-550-BLK-40', '40', N'Đen', 1, 2900000, 2900000),
(5, 13, N'Adidas NMD R1', 'ADIDAS-NMD-BLK-36', '36', N'Đen', 1, 4000000, 4000000),
(6, 4, N'Nike Air Force 1', 'NIKE-AF1-BLK-40', '40', N'Đen', 1, 2200000, 2200000),
(7, 17, N'Air Jordan 1 Retro High', 'JORDAN-1-BLK-40', '40', N'Đen/Trắng', 1, 5000000, 5000000),
(8, 32, N'Vans Authentic', 'VANS-AUTH-BLK-40', '40', N'Đen', 1, 1600000, 1600000);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' order details';

-- 17. PAYMENTS (8 payments for 8 orders)
INSERT INTO Payments (order_id, payment_method, amount, status, transaction_id, paid_at, created_at) VALUES
(1, 'vnpay', 2570000, 'completed', 'VNPAY_20250810_001', '2025-08-10 10:35:00', '2025-08-10 10:30:00'),
(2, 'momo', 3250000, 'completed', 'MOMO_20250815_001', '2025-08-15 14:10:00', '2025-08-15 14:00:00'),
(3, 'cod', 1950000, 'completed', NULL, '2025-08-11 11:30:00', '2025-08-11 11:00:00'),
(4, 'vnpay', 2660000, 'completed', 'VNPAY_20250818_001', '2025-08-18 09:35:00', '2025-08-18 09:30:00'),
(5, 'momo', 3700000, 'completed', 'MOMO_20250812_001', '2025-08-12 15:10:00', '2025-08-12 15:00:00'),
(6, 'vnpay', 2030000, 'pending', NULL, NULL, '2025-08-20 10:00:00'),
(7, 'vnpay', 4600000, 'completed', 'VNPAY_20250814_001', '2025-08-14 13:15:00', '2025-08-14 13:00:00'),
(8, 'cod', 1630000, 'completed', NULL, '2025-08-22 16:50:00', '2025-08-22 16:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' payments';

-- 18. ORDER_STATUS_HISTORIES (8 histories for 8 orders)
INSERT INTO Order_Status_Histories (order_id, status, note, changed_by, created_at) VALUES
(1, 'pending', N'Đơn hàng mới được tạo', 3, '2025-08-10 10:30:00'),
(1, 'confirmed', N'Đơn hàng đã được xác nhận', 3, '2025-08-10 11:00:00'),
(1, 'delivered', N'Đơn hàng đã giao thành công', 3, '2025-08-10 16:30:00'),
(2, 'pending', N'Đơn hàng mới được tạo', 3, '2025-08-15 14:00:00'),
(2, 'confirmed', N'Đơn hàng đã được xác nhận', 3, '2025-08-15 15:00:00'),
(2, 'shipped', N'Đơn hàng đã được vận chuyển', 3, '2025-08-16 10:00:00'),
(3, 'pending', N'Đơn hàng mới được tạo', 4, '2025-08-11 11:00:00'),
(3, 'delivered', N'Đơn hàng đã giao thành công', 4, '2025-08-11 17:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' order status histories';

PRINT 'TIER 6 HOAN THANH!';
GO

-- =====================================================
-- TIER 7: COMPLEX DEPENDENCIES
-- =====================================================
PRINT '';
PRINT 'TIER 7: Dang them Reviews, Inventory_Logs, Activity_Logs, Loyalty_Points, Cart_Items, Size_Charts, Return_Requests, Warranties...';

-- 19. REVIEWS (6 reviews: 4 approved, 2 pending)
INSERT INTO Reviews (product_id, user_id, order_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, created_at) VALUES
(1, 3, 1, 5, N'Giày rất đẹp và thoải mái', N'Tôi rất hài lòng với đôi giày này. Chất lượng tốt, thiết kế đẹp và rất thoải mái khi đi.', 1, 1, 3, '2025-08-11 10:00:00'),
(6, 4, 3, 4, N'Chất lượng tốt', N'Giày Adidas Stan Smith có chất lượng tốt, thiết kế đẹp.', 1, 1, 2, '2025-08-12 14:30:00'),
(20, 3, 2, 5, N'New Balance tuyệt vời', N'Giày New Balance 993 rất thoải mái và đẹp. Đáng giá tiền bỏ ra.', 1, 1, 5, '2025-08-16 09:15:00'),
(9, 4, 7, 5, N'Air Jordan tuyệt vời', N'Air Jordan 1 Retro High là đôi giày tuyệt vời. Chất lượng cao cấp.', 1, 1, 8, '2025-08-15 16:20:00'),
(18, 6, 4, 4, N'New Balance rất thoải mái', N'Giày New Balance 550 rất thoải mái và dễ phối đồ.', 1, 1, 1, '2025-08-19 11:45:00'),
(2, 5, 6, 3, N'Nike Air Force 1 chưa đạt mong đợi', N'Giày hơi cứng so với mong đợi. Cần thời gian để làm quen.', 0, 1, 0, '2025-08-21 10:15:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' reviews (4 approved, 2 pending)';

-- 20. INVENTORY_LOGS (8 logs: restock + sales)
INSERT INTO Inventory_Logs (variant_id, change_type, quantity_before, quantity_change, quantity_after, reference_type, reference_id, note, changed_by, created_at) VALUES
-- Restock logs
(1, 'restock', 0, 25, 25, 'purchase', 1, N'Nhập hàng lần đầu', 1, '2025-08-01 10:00:00'),
(2, 'restock', 0, 30, 30, 'purchase', 2, N'Nhập hàng lần đầu', 1, '2025-08-01 10:00:00'),
(3, 'restock', 0, 35, 35, 'purchase', 3, N'Nhập hàng lần đầu', 1, '2025-08-01 10:00:00'),
(4, 'restock', 0, 40, 40, 'purchase', 4, N'Nhập hàng lần đầu', 1, '2025-08-01 10:00:00'),
-- Sale logs
(1, 'sale', 25, -1, 24, 'order', 1, N'Bán hàng từ đơn hàng ORD-20250810-0001', 1, '2025-08-10 10:30:00'),
(20, 'sale', 20, -1, 19, 'order', 2, N'Bán hàng từ đơn hàng ORD-20250815-0001', 1, '2025-08-15 14:00:00'),
(11, 'sale', 30, -1, 29, 'order', 3, N'Bán hàng từ đơn hàng ORD-20250811-0001', 1, '2025-08-11 11:00:00'),
(18, 'sale', 30, -1, 29, 'order', 4, N'Bán hàng từ đơn hàng ORD-20250818-0001', 1, '2025-08-18 09:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' inventory logs';

-- 21. ACTIVITY_LOGS (5 logs for admin actions)
INSERT INTO Activity_Logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at) VALUES
(1, 'CREATE', 'product', 1, NULL, '{"name":"Nike Air Max 270","price":3200000}', '192.168.1.1', 'Mozilla/5.0', '2025-08-01 10:00:00'),
(1, 'UPDATE', 'product', 1, '{"price":3200000}', '{"price":2800000}', '192.168.1.1', 'Mozilla/5.0', '2025-08-02 14:30:00'),
(1, 'CREATE', 'coupon', 1, NULL, '{"code":"WELCOME10","discount":10}', '192.168.1.1', 'Mozilla/5.0', '2025-08-03 09:00:00'),
(2, 'APPROVE', 'review', 1, '{"is_approved":false}', '{"is_approved":true}', '192.168.1.2', 'Mozilla/5.0', '2025-08-11 10:30:00'),
(2, 'UPDATE', 'user', 3, '{"is_active":true}', '{"is_active":true,"role":"USER"}', '192.168.1.2', 'Mozilla/5.0', '2025-08-08 15:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' activity logs';

-- 22. LOYALTY_POINTS (8 points transactions)
INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, created_at) VALUES
(3, 257, 'earn', 1, N'Tích điểm từ đơn hàng ORD-20250810-0001', '2025-08-10 10:30:00'),
(3, 325, 'earn', 2, N'Tích điểm từ đơn hàng ORD-20250815-0001', '2025-08-15 14:00:00'),
(4, 195, 'earn', 3, N'Tích điểm từ đơn hàng ORD-20250811-0001', '2025-08-11 11:00:00'),
(4, 266, 'earn', 4, N'Tích điểm từ đơn hàng ORD-20250818-0001', '2025-08-18 09:30:00'),
(5, 370, 'earn', 5, N'Tích điểm từ đơn hàng ORD-20250812-0001', '2025-08-12 15:00:00'),
(6, 460, 'earn', 7, N'Tích điểm từ đơn hàng ORD-20250814-0001', '2025-08-14 13:00:00'),
(3, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2025-08-02 10:00:00'),
(4, 50, 'earn', NULL, N'Điểm thưởng đăng ký tài khoản', '2025-08-02 11:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' loyalty points';

-- 23. CART_ITEMS (5 items in carts)
INSERT INTO Cart_Items (cart_id, variant_id, quantity, added_at) VALUES
(1, 5, 1, '2025-08-16 10:00:00'),
(2, 14, 2, '2025-08-16 14:00:00'),
(3, 7, 1, '2025-08-16 09:00:00'),
(4, 19, 1, '2025-08-17 16:00:00'),
(5, 25, 3, '2025-08-17 11:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' cart items';

-- 24. SIZE_CHARTS (6 size charts for brands)
INSERT INTO Size_Charts (brand_id, category, size, size_us, size_uk, length_cm, width_cm) VALUES
(1, 'sneakers', '40', '7', '6', 25.5, 9.5),
(2, 'sneakers', '41', '8', '7', 26.0, 9.8),
(3, 'basketball', '42', '9', '8', 26.5, 10.0),
(4, 'casual', '40', '7', '6', 25.5, 9.5),
(5, 'skateboard', '41', '8', '7', 26.0, 9.8),
(6, 'running', '40', '7', '6', 25.5, 9.5);
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' size charts';

-- 25. RETURN_REQUESTS (3 return requests)
INSERT INTO Return_Requests (order_id, user_id, reason, status, admin_note, approved_by, approved_at, created_at) VALUES
(1, 3, N'Không đúng kích thước', 'approved', N'Đã xác nhận, cho phép đổi size', 1, '2025-08-10 11:00:00', '2025-08-10 10:00:00'),
(3, 4, N'Sản phẩm bị lỗi', 'pending', NULL, NULL, NULL, '2025-08-12 14:00:00'),
(5, 5, N'Không đúng màu sắc', 'rejected', N'Màu sắc đúng như mô tả, không thể đổi trả', 1, '2025-08-12 10:00:00', '2025-08-12 09:00:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' return requests';

-- 25a. WARRANTIES (3 warranty requests)
INSERT INTO Warranties (order_id, user_id, product_id, variant_id, issue_description, warranty_type, warranty_months, status, admin_note, resolution_note, processed_by, processed_at, completed_at, purchase_date, submitted_at, created_at) VALUES
(1, 3, 1, 1, N'Giày bị bong keo sau 2 tuần sử dụng', 'repair', 12, 'pending', NULL, NULL, NULL, NULL, NULL, '2025-08-10 10:30:00', '2025-08-24 10:30:00', '2025-08-24 10:30:00'),
(2, 3, 20, 20, N'Đế giày bị nứt sau 1 tháng sử dụng', 'replace', 12, 'in_progress', N'Đang kiểm tra và liên hệ nhà sản xuất', NULL, 1, '2025-08-16 09:00:00', NULL, '2025-08-15 14:00:00', '2025-08-15 14:00:00', '2025-09-15 14:20:00'),
(4, 4, 18, 18, N'Logo bị phai màu sau 2 tháng', 'replace', 12, 'completed', N'Đã xác nhận lỗi sản xuất', N'Đã gửi sản phẩm thay thế cho khách hàng', 1, '2025-08-19 10:00:00', '2025-08-21 15:30:00', '2025-08-18 09:30:00', '2025-08-18 09:30:00', '2025-10-18 09:30:00');
PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' warranty requests';

PRINT 'TIER 7 HOAN THANH!';
GO

-- =====================================================
-- FINALIZATION & VALIDATION
-- =====================================================
SET NOCOUNT OFF;

-- Validation checks
PRINT '';
PRINT '=====================================================';
PRINT 'DANG KIEM TRA TINH TOAN VEN DU LIEU...';
PRINT '=====================================================';

DECLARE @errorCount INT = 0;

-- Check foreign key integrity
IF EXISTS (SELECT 1 FROM Order_Details od LEFT JOIN Product_Variants pv ON od.variant_id = pv.id WHERE pv.id IS NULL)
BEGIN
    PRINT '  [ERROR] Order_Details có variant_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

IF EXISTS (SELECT 1 FROM Orders o LEFT JOIN Addresses a ON o.address_shipping_id = a.id WHERE a.id IS NULL)
BEGIN
    PRINT '  [ERROR] Orders có address_shipping_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

IF EXISTS (SELECT 1 FROM Payments p LEFT JOIN Orders o ON p.order_id = o.id WHERE o.id IS NULL)
BEGIN
    PRINT '  [ERROR] Payments có order_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

-- Print summary
PRINT '';
IF @errorCount = 0
BEGIN
    PRINT '  [SUCCESS] Tất cả foreign keys hợp lệ!';
    COMMIT TRANSACTION;
END
ELSE
BEGIN
    PRINT '  [WARNING] Có ' + CAST(@errorCount AS VARCHAR) + ' lỗi foreign key. Rollback transaction!';
    ROLLBACK TRANSACTION;
    RETURN;
END;

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH THEM DU LIEU!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da them thanh cong:';
PRINT '  + 10 users (2 admin, 8 regular)';
PRINT '  + 6 brands';
PRINT '  + 8 categories (2 parents, 6 children)';
PRINT '  + 20 products';
PRINT '  + 40 product variants';
PRINT '  + 5 coupons';
PRINT '  + 5 flash sales';
PRINT '  + 8 addresses';
PRINT '  + 8 orders';
PRINT '  + 8 order details';
PRINT '  + 8 payments';
PRINT '  + 6 reviews (4 approved, 2 pending)';
PRINT '  + 10 notifications';
PRINT '  + 5 wishlist items';
PRINT '  + 5 carts';
PRINT '  + 5 cart items';
PRINT '  + 3 email templates';
PRINT '  + 8 loyalty points';
PRINT '  + 8 inventory logs';
PRINT '  + 5 activity logs';
PRINT '  + 6 size charts';
PRINT '  + 3 return requests';
PRINT '  + 3 warranty requests';
PRINT '';
PRINT 'TAT CA DU LIEU DA DUOC THEM THANH CONG!';
PRINT '=====================================================';
PRINT '';
PRINT 'Luu y: Du lieu anh (Product_Images va Variant_Images)';
PRINT 'da duoc tach ra file 10_INSERT_IMAGES.sql rieng.';
PRINT 'Chay file 10_INSERT_IMAGES.sql de them du lieu anh.';
PRINT '';
PRINT 'Script da ket thuc thanh cong!';
PRINT 'Transaction da duoc commit.';
PRINT '';
-- =====================================================
-- SNEAKERY E-COMMERCE - INSERT IMAGE DATA
-- =====================================================
-- File này insert dữ liệu ảnh cho tất cả sản phẩm và biến thể sản phẩm
-- Bao gồm: Product_Images và Variant_Images
-- Thời gian: từ 1/8/2025 đến 1/11/2025
-- Tuân thủ thứ tự foreign key dependency để đảm bảo tính toàn vẹn
-- Dữ liệu được đồng bộ và thống nhất
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

BEGIN TRANSACTION; -- Wrap all inserts in a transaction for safety

PRINT '=====================================================';
PRINT 'DANG THEM DU LIEU ANH...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- PRODUCT_IMAGES (20 images: 1 primary per product)
-- =====================================================
PRINT 'Dang them Product_Images...';

INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order) VALUES
(1, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Max 270 - Góc nhìn chính', 1, 1),
(2, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Force 1 - Góc nhìn chính', 1, 1),
(3, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Dunk Low - Góc nhìn chính', 1, 1),
(4, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Pegasus 39 - Góc nhìn chính', 1, 1),
(5, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Ultraboost 22 - Góc nhìn chính', 1, 1),
(6, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Stan Smith - Góc nhìn chính', 1, 1),
(7, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Adidas NMD R1 - Góc nhìn chính', 1, 1),
(8, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Superstar - Góc nhìn chính', 1, 1),
(9, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 1 Retro High - Góc nhìn chính', 1, 1),
(10, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 4 Retro - Góc nhìn chính', 1, 1),
(11, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 3 Retro - Góc nhìn chính', 1, 1),
(12, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck Taylor - Góc nhìn chính', 1, 1),
(13, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck 70 - Góc nhìn chính', 1, 1),
(14, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse One Star - Góc nhìn chính', 1, 1),
(15, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Old Skool - Góc nhìn chính', 1, 1),
(16, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Authentic - Góc nhìn chính', 1, 1),
(17, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Sk8-Hi - Góc nhìn chính', 1, 1),
(18, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 550 - Góc nhìn chính', 1, 1),
(19, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 574 - Góc nhìn chính', 1, 1),
(20, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 993 - Góc nhìn chính', 1, 1);

PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' product images';
PRINT '';

-- =====================================================
-- VARIANT_IMAGES (90+ images: 2-3 images per variant)
-- =====================================================
PRINT 'Dang them Variant_Images...';

INSERT INTO Variant_Images (variant_id, image_url, alt_text, is_primary, display_order) VALUES
-- Product 1: Nike Air Max 270 - Variant 1 (Đen, 40)
(1, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Max 270 Đen Size 40 - Góc nhìn chính', 1, 1),
(1, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Max 270 Đen Size 40 - Góc nhìn bên', 0, 2),
(1, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Air Max 270 Đen Size 40 - Góc nhìn trên', 0, 3),
-- Product 1: Nike Air Max 270 - Variant 2 (Đen, 41)
(2, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Max 270 Đen Size 41 - Góc nhìn chính', 1, 1),
(2, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Max 270 Đen Size 41 - Góc nhìn bên', 0, 2),
-- Product 2: Nike Air Force 1 - Variant 1 (Đen, 40)
(3, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Air Force 1 Đen Size 40 - Góc nhìn chính', 1, 1),
(3, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Air Force 1 Đen Size 40 - Góc nhìn bên', 0, 2),
(3, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Air Force 1 Đen Size 40 - Góc nhìn sau', 0, 3),
-- Product 2: Nike Air Force 1 - Variant 2 (Trắng, 40)
(4, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Nike Air Force 1 Trắng Size 40 - Góc nhìn chính', 1, 1),
(4, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Nike Air Force 1 Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 3: Nike Dunk Low - Variant 1 (Đen, 40)
(5, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Dunk Low Đen Size 40 - Góc nhìn chính', 1, 1),
(5, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Dunk Low Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 3: Nike Dunk Low - Variant 2 (Trắng, 40)
(6, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Nike Dunk Low Trắng Size 40 - Góc nhìn chính', 1, 1),
(6, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Nike Dunk Low Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 4: Nike Pegasus 39 - Variant 1 (Đen, 40)
(7, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Pegasus 39 Đen Size 40 - Góc nhìn chính', 1, 1),
(7, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Pegasus 39 Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 4: Nike Pegasus 39 - Variant 2 (Xanh dương, 40)
(8, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Nike Pegasus 39 Xanh dương Size 40 - Góc nhìn chính', 1, 1),
(8, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Pegasus 39 Xanh dương Size 40 - Góc nhìn bên', 0, 2),
-- Product 5: Adidas Ultraboost 22 - Variant 1 (Đen, 36)
(9, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Ultraboost 22 Đen Size 36 - Góc nhìn chính', 1, 1),
(9, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Ultraboost 22 Đen Size 36 - Góc nhìn bên', 0, 2),
(9, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Adidas Ultraboost 22 Đen Size 36 - Góc nhìn trên', 0, 3),
-- Product 5: Adidas Ultraboost 22 - Variant 2 (Đen, 37)
(10, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Ultraboost 22 Đen Size 37 - Góc nhìn chính', 1, 1),
(10, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Ultraboost 22 Đen Size 37 - Góc nhìn bên', 0, 2),
-- Product 6: Adidas Stan Smith - Variant 1 (Trắng, 36)
(11, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Stan Smith Trắng Size 36 - Góc nhìn chính', 1, 1),
(11, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Stan Smith Trắng Size 36 - Góc nhìn bên', 0, 2),
(11, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Adidas Stan Smith Trắng Size 36 - Góc nhìn sau', 0, 3),
-- Product 6: Adidas Stan Smith - Variant 2 (Trắng, 37)
(12, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Stan Smith Trắng Size 37 - Góc nhìn chính', 1, 1),
(12, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Stan Smith Trắng Size 37 - Góc nhìn bên', 0, 2),
-- Product 7: Adidas NMD R1 - Variant 1 (Đen, 36)
(13, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Adidas NMD R1 Đen Size 36 - Góc nhìn chính', 1, 1),
(13, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Adidas NMD R1 Đen Size 36 - Góc nhìn bên', 0, 2),
-- Product 7: Adidas NMD R1 - Variant 2 (Đen, 37)
(14, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Adidas NMD R1 Đen Size 37 - Góc nhìn chính', 1, 1),
(14, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Adidas NMD R1 Đen Size 37 - Góc nhìn bên', 0, 2),
-- Product 8: Adidas Superstar - Variant 1 (Đen, 36)
(15, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Superstar Đen Size 36 - Góc nhìn chính', 1, 1),
(15, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Superstar Đen Size 36 - Góc nhìn bên', 0, 2),
-- Product 8: Adidas Superstar - Variant 2 (Trắng, 36)
(16, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Adidas Superstar Trắng Size 36 - Góc nhìn chính', 1, 1),
(16, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Adidas Superstar Trắng Size 36 - Góc nhìn bên', 0, 2),
-- Product 9: Air Jordan 1 Retro High - Variant 1 (Đen/Trắng, 40)
(17, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 1 Retro High Đen/Trắng Size 40 - Góc nhìn chính', 1, 1),
(17, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 1 Retro High Đen/Trắng Size 40 - Góc nhìn bên', 0, 2),
(17, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Air Jordan 1 Retro High Đen/Trắng Size 40 - Góc nhìn sau', 0, 3),
-- Product 9: Air Jordan 1 Retro High - Variant 2 (Đen/Trắng, 41)
(18, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 1 Retro High Đen/Trắng Size 41 - Góc nhìn chính', 1, 1),
(18, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 1 Retro High Đen/Trắng Size 41 - Góc nhìn bên', 0, 2),
-- Product 10: Air Jordan 4 Retro - Variant 1 (Đen/Xám, 40)
(19, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 4 Retro Đen/Xám Size 40 - Góc nhìn chính', 1, 1),
(19, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 4 Retro Đen/Xám Size 40 - Góc nhìn bên', 0, 2),
(19, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Air Jordan 4 Retro Đen/Xám Size 40 - Góc nhìn trên', 0, 3),
-- Product 10: Air Jordan 4 Retro - Variant 2 (Đen/Xám, 41)
(20, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 4 Retro Đen/Xám Size 41 - Góc nhìn chính', 1, 1),
(20, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 4 Retro Đen/Xám Size 41 - Góc nhìn bên', 0, 2),
-- Product 11: Air Jordan 3 Retro - Variant 1 (Đen/Trắng, 40)
(21, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 3 Retro Đen/Trắng Size 40 - Góc nhìn chính', 1, 1),
(21, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 3 Retro Đen/Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 11: Air Jordan 3 Retro - Variant 2 (Đen/Trắng, 41)
(22, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Air Jordan 3 Retro Đen/Trắng Size 41 - Góc nhìn chính', 1, 1),
(22, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Air Jordan 3 Retro Đen/Trắng Size 41 - Góc nhìn bên', 0, 2),
-- Product 12: Converse Chuck Taylor - Variant 1 (Đen, 40)
(23, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck Taylor Đen Size 40 - Góc nhìn chính', 1, 1),
(23, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck Taylor Đen Size 40 - Góc nhìn bên', 0, 2),
(23, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Converse Chuck Taylor Đen Size 40 - Góc nhìn sau', 0, 3),
-- Product 12: Converse Chuck Taylor - Variant 2 (Trắng, 40)
(24, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck Taylor Trắng Size 40 - Góc nhìn chính', 1, 1),
(24, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck Taylor Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 13: Converse Chuck 70 - Variant 1 (Đen, 40)
(25, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck 70 Đen Size 40 - Góc nhìn chính', 1, 1),
(25, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck 70 Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 13: Converse Chuck 70 - Variant 2 (Trắng, 40)
(26, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse Chuck 70 Trắng Size 40 - Góc nhìn chính', 1, 1),
(26, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Converse Chuck 70 Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 14: Converse One Star - Variant 1 (Đen, 40)
(27, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse One Star Đen Size 40 - Góc nhìn chính', 1, 1),
(27, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Converse One Star Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 14: Converse One Star - Variant 2 (Đen, 41)
(28, 'https://images.unsplash.com/photo-1600010034943-0ba43ce25e95', N'Converse One Star Đen Size 41 - Góc nhìn chính', 1, 1),
(28, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Converse One Star Đen Size 41 - Góc nhìn bên', 0, 2),
-- Product 15: Vans Old Skool - Variant 1 (Đen/Trắng, 40)
(29, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Old Skool Đen/Trắng Size 40 - Góc nhìn chính', 1, 1),
(29, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Old Skool Đen/Trắng Size 40 - Góc nhìn bên', 0, 2),
(29, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Vans Old Skool Đen/Trắng Size 40 - Góc nhìn sau', 0, 3),
-- Product 15: Vans Old Skool - Variant 2 (Đen/Trắng, 41)
(30, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Old Skool Đen/Trắng Size 41 - Góc nhìn chính', 1, 1),
(30, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Old Skool Đen/Trắng Size 41 - Góc nhìn bên', 0, 2),
-- Product 16: Vans Authentic - Variant 1 (Đen, 40)
(31, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Authentic Đen Size 40 - Góc nhìn chính', 1, 1),
(31, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Authentic Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 16: Vans Authentic - Variant 2 (Trắng, 40)
(32, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Authentic Trắng Size 40 - Góc nhìn chính', 1, 1),
(32, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Authentic Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 17: Vans Sk8-Hi - Variant 1 (Đen/Trắng, 40)
(33, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Sk8-Hi Đen/Trắng Size 40 - Góc nhìn chính', 1, 1),
(33, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Sk8-Hi Đen/Trắng Size 40 - Góc nhìn bên', 0, 2),
-- Product 17: Vans Sk8-Hi - Variant 2 (Đen/Trắng, 41)
(34, 'https://images.unsplash.com/photo-1626379950705-965b26e0fb8a', N'Vans Sk8-Hi Đen/Trắng Size 41 - Góc nhìn chính', 1, 1),
(34, 'https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca', N'Vans Sk8-Hi Đen/Trắng Size 41 - Góc nhìn bên', 0, 2),
-- Product 18: New Balance 550 - Variant 1 (Đen, 40)
(35, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 550 Đen Size 40 - Góc nhìn chính', 1, 1),
(35, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 550 Đen Size 40 - Góc nhìn bên', 0, 2),
(35, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'New Balance 550 Đen Size 40 - Góc nhìn trên', 0, 3),
-- Product 18: New Balance 550 - Variant 2 (Xám, 40)
(36, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 550 Xám Size 40 - Góc nhìn chính', 1, 1),
(36, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 550 Xám Size 40 - Góc nhìn bên', 0, 2),
-- Product 19: New Balance 574 - Variant 1 (Đen, 40)
(37, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 574 Đen Size 40 - Góc nhìn chính', 1, 1),
(37, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 574 Đen Size 40 - Góc nhìn bên', 0, 2),
-- Product 19: New Balance 574 - Variant 2 (Xám, 40)
(38, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 574 Xám Size 40 - Góc nhìn chính', 1, 1),
(38, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 574 Xám Size 40 - Góc nhìn bên', 0, 2),
-- Product 20: New Balance 993 - Variant 1 (Xám, 40)
(39, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 993 Xám Size 40 - Góc nhìn chính', 1, 1),
(39, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 993 Xám Size 40 - Góc nhìn bên', 0, 2),
(39, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'New Balance 993 Xám Size 40 - Góc nhìn sau', 0, 3),
-- Product 20: New Balance 993 - Variant 2 (Xám, 41)
(40, 'https://images.unsplash.com/photo-1608667508764-33cf0726b6b5', N'New Balance 993 Xám Size 41 - Góc nhìn chính', 1, 1),
(40, 'https://images.unsplash.com/photo-1572577722808-ba98339ea7bb', N'New Balance 993 Xám Size 41 - Góc nhìn bên', 0, 2);

PRINT '  + Inserted ' + CAST(@@ROWCOUNT AS VARCHAR) + ' variant images';
PRINT '';

-- =====================================================
-- FINALIZATION & VALIDATION
-- =====================================================
SET NOCOUNT OFF;

-- Validation checks
PRINT '';
PRINT '=====================================================';
PRINT 'DANG KIEM TRA TINH TOAN VEN DU LIEU ANH...';
PRINT '=====================================================';

DECLARE @errorCount INT = 0;

-- Check foreign key integrity for Product_Images
IF EXISTS (SELECT 1 FROM Product_Images pi LEFT JOIN Products p ON pi.product_id = p.id WHERE p.id IS NULL)
BEGIN
    PRINT '  [ERROR] Product_Images có product_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

-- Check foreign key integrity for Variant_Images
IF EXISTS (SELECT 1 FROM Variant_Images vi LEFT JOIN Product_Variants pv ON vi.variant_id = pv.id WHERE pv.id IS NULL)
BEGIN
    PRINT '  [ERROR] Variant_Images có variant_id không tồn tại!';
    SET @errorCount = @errorCount + 1;
END;

-- Check that all products have at least one image
DECLARE @productCount INT;
DECLARE @productImageCount INT;
SELECT @productCount = COUNT(*) FROM Products WHERE deleted_at IS NULL;
SELECT @productImageCount = COUNT(DISTINCT product_id) FROM Product_Images;
IF @productImageCount < @productCount
BEGIN
    PRINT '  [WARNING] Một số sản phẩm chưa có ảnh!';
    PRINT '  Products: ' + CAST(@productCount AS VARCHAR) + ', Products with images: ' + CAST(@productImageCount AS VARCHAR);
END;

-- Check that all variants have at least one image
DECLARE @variantCount INT;
DECLARE @variantImageCount INT;
SELECT @variantCount = COUNT(*) FROM Product_Variants WHERE deleted_at IS NULL;
SELECT @variantImageCount = COUNT(DISTINCT variant_id) FROM Variant_Images;
IF @variantImageCount < @variantCount
BEGIN
    PRINT '  [WARNING] Một số biến thể chưa có ảnh!';
    PRINT '  Variants: ' + CAST(@variantCount AS VARCHAR) + ', Variants with images: ' + CAST(@variantImageCount AS VARCHAR);
END;

-- Print summary
PRINT '';
IF @errorCount = 0
BEGIN
    PRINT '  [SUCCESS] Tất cả foreign keys hợp lệ!';
    COMMIT TRANSACTION;
END
ELSE
BEGIN
    PRINT '  [WARNING] Có ' + CAST(@errorCount AS VARCHAR) + ' lỗi foreign key. Rollback transaction!';
    ROLLBACK TRANSACTION;
    RETURN;
END;

PRINT '';
PRINT '=====================================================';
PRINT 'HOAN THANH THEM DU LIEU ANH!';
PRINT '=====================================================';
PRINT '';
DECLARE @variantImageTotalCount INT;
SELECT @variantImageTotalCount = COUNT(*) FROM Variant_Images;

PRINT 'Da them thanh cong:';
PRINT '  + ' + CAST(@productImageCount AS VARCHAR) + ' product images (cho ' + CAST(@productCount AS VARCHAR) + ' products)';
PRINT '  + ' + CAST(@variantImageTotalCount AS VARCHAR) + ' variant images (cho ' + CAST(@variantImageCount AS VARCHAR) + ' variants)';
PRINT '';
PRINT 'TAT CA DU LIEU ANH DA DUOC THEM THANH CONG!';
PRINT '=====================================================';
PRINT '';
PRINT 'Script da ket thuc thanh cong!';
PRINT 'Transaction da duoc commit.';
PRINT '';

PRINT '';
PRINT '=====================================================';
PRINT 'HOÀN THÀNH TẤT CẢ CÁC SCRIPT!';
PRINT '=====================================================';
PRINT '';
PRINT 'Database Sneakery đã được tạo thành công!';
PRINT '';
PRINT 'Đã hoàn thành:';
PRINT '  + Database và schema';
PRINT '  + 30 tables với indexes';
PRINT '  + 3 views cho admin API';
PRINT '  + 2 stored procedures';
PRINT '  + 2 triggers';
PRINT '  + Migrations';
PRINT '  + Composite indexes';
PRINT '  + Dữ liệu mẫu';
PRINT '  + Dữ liệu ảnh';
PRINT '';
PRINT '=====================================================';
PRINT '';


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


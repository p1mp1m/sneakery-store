-- ============================================================
-- 1️⃣ Bảng Product_Images: bổ sung cột Cloudinary
-- ============================================================
ALTER TABLE Product_Images
ADD cloudinary_public_id NVARCHAR(255) NULL;
GO

CREATE NONCLUSTERED INDEX IX_Product_Images_CloudinaryPublicId
ON Product_Images (cloudinary_public_id);
GO


-- ============================================================
-- 2️⃣ Bảng Materials (Chất liệu)
-- ============================================================
CREATE TABLE Materials (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    slug NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX) NULL,
    is_active BIT NULL,
    created_at DATETIME2 DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 NULL,
    deleted_at DATETIME2 NULL
);
GO


-- ============================================================
-- 3️⃣ Bảng Shoe_Soles (Loại đế giày)
-- ============================================================
CREATE TABLE Shoe_Soles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    slug NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX) NULL,
    is_active BIT NULL,
    created_at DATETIME2 DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 NULL,
    deleted_at DATETIME2 NULL
);
GO


-- ============================================================
-- 4️⃣ Cập nhật bảng Products: thêm cột mới
-- ============================================================
ALTER TABLE Products
ADD material_id INT NULL,
    shoe_sole_id INT NULL,
    product_code NVARCHAR(50) NULL;
GO


-- ============================================================
-- 5️⃣ Thêm ràng buộc khóa ngoại (FK)
-- ============================================================
ALTER TABLE Products
ADD CONSTRAINT FK_Products_Materials
    FOREIGN KEY (material_id)
    REFERENCES Materials(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL;
GO

ALTER TABLE Products
ADD CONSTRAINT FK_Products_Shoe_Soles
    FOREIGN KEY (shoe_sole_id)
    REFERENCES Shoe_Soles(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL;
GO


-- ============================================================
-- 6️⃣ Tạo Index và Unique Key
-- ============================================================
CREATE INDEX IX_Products_MaterialId ON Products(material_id);
CREATE INDEX IX_Products_ShoeSoleId ON Products(shoe_sole_id);

-- Unique product_code (bỏ qua NULL)
CREATE UNIQUE INDEX UQ_Products_ProductCode
ON Products(product_code)
WHERE product_code IS NOT NULL;
GO

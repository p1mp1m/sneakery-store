-- =====================================================
-- V32: CREATE SIZES AND COLORS TABLES
-- Tách size và color ra bảng riêng để quản lý tập trung
-- =====================================================

-- =====================================================
-- 1. CREATE SIZES TABLE
-- =====================================================
CREATE TABLE Sizes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(20) NOT NULL UNIQUE,
    display_order INT DEFAULT 0,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2
);
GO

-- =====================================================
-- 2. CREATE COLORS TABLE
-- =====================================================
CREATE TABLE Colors (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE,
    hex_code NVARCHAR(7),
    display_order INT DEFAULT 0,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2
);
GO

-- =====================================================
-- 3. INSERT DEFAULT SIZES (35-46 for shoes)
-- =====================================================
INSERT INTO Sizes (name, display_order, is_active) VALUES
('35', 1, 1),
('36', 2, 1),
('37', 3, 1),
('38', 4, 1),
('39', 5, 1),
('40', 6, 1),
('41', 7, 1),
('42', 8, 1),
('43', 9, 1),
('44', 10, 1),
('45', 11, 1),
('46', 12, 1);
GO

-- =====================================================
-- 4. INSERT DEFAULT COLORS
-- =====================================================
INSERT INTO Colors (name, hex_code, display_order, is_active) VALUES
(N'Đen', '#000000', 1, 1),
(N'Trắng', '#FFFFFF', 2, 1),
(N'Đỏ', '#FF0000', 3, 1),
(N'Xanh dương', '#0000FF', 4, 1),
(N'Xanh lá', '#00FF00', 5, 1),
(N'Vàng', '#FFFF00', 6, 1),
(N'Cam', '#FFA500', 7, 1),
(N'Hồng', '#FFC0CB', 8, 1),
(N'Tím', '#800080', 9, 1),
(N'Nâu', '#8B4513', 10, 1),
(N'Xám', '#808080', 11, 1),
(N'Be', '#F5F5DC', 12, 1),
(N'Navy', '#000080', 13, 1),
(N'Bạc', '#C0C0C0', 14, 1),
(N'Vàng gold', '#FFD700', 15, 1);
GO

-- =====================================================
-- 5. ADD FK COLUMNS TO PRODUCT_VARIANTS (nullable first)
-- =====================================================
ALTER TABLE Product_Variants ADD size_id INT NULL;
ALTER TABLE Product_Variants ADD color_id INT NULL;
GO

-- =====================================================
-- 6. MIGRATE EXISTING DATA
-- Map existing size/color VARCHAR to new FK
-- =====================================================

-- Migrate sizes
UPDATE pv
SET pv.size_id = s.id
FROM Product_Variants pv
INNER JOIN Sizes s ON LTRIM(RTRIM(pv.size)) = s.name
WHERE pv.size IS NOT NULL AND pv.size_id IS NULL;
GO

-- Insert any missing sizes from existing data
INSERT INTO Sizes (name, display_order, is_active)
SELECT DISTINCT LTRIM(RTRIM(pv.size)), 99, 1
FROM Product_Variants pv
WHERE pv.size IS NOT NULL 
  AND LTRIM(RTRIM(pv.size)) NOT IN (SELECT name FROM Sizes);
GO

-- Re-run migration for newly inserted sizes
UPDATE pv
SET pv.size_id = s.id
FROM Product_Variants pv
INNER JOIN Sizes s ON LTRIM(RTRIM(pv.size)) = s.name
WHERE pv.size IS NOT NULL AND pv.size_id IS NULL;
GO

-- Migrate colors
UPDATE pv
SET pv.color_id = c.id
FROM Product_Variants pv
INNER JOIN Colors c ON LTRIM(RTRIM(pv.color)) = c.name
WHERE pv.color IS NOT NULL AND pv.color_id IS NULL;
GO

-- Insert any missing colors from existing data
INSERT INTO Colors (name, display_order, is_active)
SELECT DISTINCT LTRIM(RTRIM(pv.color)), 99, 1
FROM Product_Variants pv
WHERE pv.color IS NOT NULL 
  AND LTRIM(RTRIM(pv.color)) NOT IN (SELECT name FROM Colors);
GO

-- Re-run migration for newly inserted colors
UPDATE pv
SET pv.color_id = c.id
FROM Product_Variants pv
INNER JOIN Colors c ON LTRIM(RTRIM(pv.color)) = c.name
WHERE pv.color IS NOT NULL AND pv.color_id IS NULL;
GO

-- =====================================================
-- 7. ADD FOREIGN KEY CONSTRAINTS
-- =====================================================
ALTER TABLE Product_Variants
ADD CONSTRAINT FK_ProductVariants_Size 
FOREIGN KEY (size_id) REFERENCES Sizes(id);

ALTER TABLE Product_Variants
ADD CONSTRAINT FK_ProductVariants_Color 
FOREIGN KEY (color_id) REFERENCES Colors(id);
GO

-- =====================================================
-- 8. CREATE INDEXES FOR PERFORMANCE
-- =====================================================
CREATE INDEX IX_ProductVariants_SizeId ON Product_Variants(size_id);
CREATE INDEX IX_ProductVariants_ColorId ON Product_Variants(color_id);
CREATE INDEX IX_Sizes_Name ON Sizes(name);
CREATE INDEX IX_Colors_Name ON Colors(name);
GO

PRINT 'V32: Sizes and Colors tables created successfully!';
PRINT 'Note: Old size/color VARCHAR columns are kept for backward compatibility.';
GO

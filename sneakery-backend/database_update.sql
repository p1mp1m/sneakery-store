-- ===============================================================
-- CẬP NHẬT DATABASE ĐỂ THÊM HỆ THỐNG ROLE ADMIN
-- ===============================================================

USE sneakery_db;
GO

-- Thêm cột role vào bảng Users
ALTER TABLE Users 
ADD role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN', 'MODERATOR'));
GO

-- Cập nhật một số user hiện có thành ADMIN (ví dụ)
UPDATE Users 
SET role = 'ADMIN' 
WHERE email IN ('nguyenvanphuc@gmail.com', 'tranthilananh@gmail.com');
GO

-- Tạo user admin mặc định
INSERT INTO Users (email, password_hash, full_name, phone_number, is_active, role) 
VALUES ('admin@sneakery.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', N'Admin Sneakery', '0123456789', 1, 'ADMIN');
GO

-- Kiểm tra kết quả
SELECT id, email, full_name, role, is_active FROM Users WHERE role = 'ADMIN';
GO

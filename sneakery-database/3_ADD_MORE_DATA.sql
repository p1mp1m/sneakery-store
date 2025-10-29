-- =====================================================
-- SNEAKERY E-COMMERCE - ADDITIONAL DATA (QUY MO VUA)
-- =====================================================
-- File này bổ sung thêm dữ liệu để đạt ~100 records mỗi bảng
-- Chạy SAU KHI đã chạy 1_CREATE_SCHEMA.sql và 2_INSERT_DATA.sql
-- TUYỆT ĐỐI KHÔNG chạy nếu chưa có dữ liệu cơ bản!
-- =====================================================

USE sneakery_db;
GO

SET NOCOUNT ON;

BEGIN TRANSACTION;

PRINT '=====================================================';
PRINT 'DANG BO SUNG DU LIEU (QUY MO VUA)...';
PRINT '=====================================================';
PRINT '';

-- =====================================================
-- TIER 1: ADD MORE USERS (88 users để đạt total 100)
-- =====================================================
PRINT 'TIER 1: Dang them 88 Users moi (tong 100 users)...';

-- Add 88 users in batches
-- Batch 1: Users 11-30

-- Insert all 88 users with explicit values (batch insert for better performance)
INSERT INTO Users (email, password_hash, full_name, phone_number, role, is_active, is_email_verified, created_at) VALUES
('user11@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Nguyễn Văn Bình', '0987654331', 'USER', 1, 1, '2024-02-01 10:00:00'),
('user12@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Cúc', '0987654332', 'USER', 1, 1, '2024-02-02 11:00:00'),
('user13@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Đức', '0987654333', 'USER', 1, 1, '2024-02-03 12:00:00'),
('user14@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Hương', '0987654334', 'USER', 1, 1, '2024-02-04 13:00:00'),
('user15@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Hùng', '0987654335', 'USER', 1, 1, '2024-02-05 14:00:00'),
('user16@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Thảo', '0987654336', 'USER', 1, 1, '2024-02-06 15:00:00'),
('user17@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn Khánh', '0987654337', 'USER', 1, 1, '2024-02-07 16:00:00'),
('user18@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Linh', '0987654338', 'USER', 1, 1, '2024-02-08 17:00:00'),
('user19@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Văn Minh', '0987654339', 'USER', 1, 1, '2024-02-09 18:00:00'),
('user20@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Thị Thu', '0987654340', 'USER', 1, 1, '2024-02-10 19:00:00'),
('user21@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Phong', '0987654341', 'USER', 1, 1, '2024-02-11 10:00:00'),
('user22@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Ngọc', '0987654342', 'USER', 1, 1, '2024-02-12 11:00:00'),
('user23@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Văn Hưng', '0987654343', 'USER', 1, 1, '2024-02-13 12:00:00'),
('user24@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Thị Lan', '0987654344', 'USER', 1, 1, '2024-02-14 13:00:00'),
('user25@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Văn Tú', '0987654345', 'USER', 1, 1, '2024-02-15 14:00:00'),
('user26@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Thị Mai', '0987654346', 'USER', 1, 1, '2024-02-16 15:00:00'),
('user27@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Văn Long', '0987654347', 'USER', 1, 1, '2024-02-17 16:00:00'),
('user28@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Thị Hạnh', '0987654348', 'USER', 1, 1, '2024-02-18 17:00:00'),
('user29@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Văn Sơn', '0987654349', 'USER', 1, 1, '2024-02-19 18:00:00'),
('user30@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Thị Hoa', '0987654350', 'USER', 1, 1, '2024-02-20 19:00:00'),
-- Users 31-40
('user31@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Văn Tuấn', '0987654351', 'USER', 1, 1, '2024-02-21 10:00:00'),
('user32@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Hà', '0987654352', 'USER', 1, 1, '2024-02-22 11:00:00'),
('user33@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Đạt', '0987654353', 'USER', 1, 1, '2024-02-23 12:00:00'),
('user34@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Nga', '0987654354', 'USER', 1, 1, '2024-02-24 13:00:00'),
('user35@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn Quang', '0987654355', 'USER', 1, 1, '2024-02-25 14:00:00'),
('user36@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Loan', '0987654356', 'USER', 1, 1, '2024-02-26 15:00:00'),
('user37@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Văn Hoà', '0987654357', 'USER', 1, 1, '2024-02-27 16:00:00'),
('user38@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Thị Ly', '0987654358', 'USER', 1, 1, '2024-02-28 17:00:00'),
('user39@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Hưng', '0987654359', 'USER', 1, 1, '2024-03-01 18:00:00'),
('user40@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Huệ', '0987654360', 'USER', 1, 1, '2024-03-02 19:00:00'),
-- Users 41-60
('user41@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Văn Bảo', '0987654361', 'USER', 1, 1, '2024-03-03 10:00:00'),
('user42@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Thị Vân', '0987654362', 'USER', 1, 1, '2024-03-04 11:00:00'),
('user43@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Văn Khoa', '0987654363', 'USER', 1, 1, '2024-03-05 12:00:00'),
('user44@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Thị Tâm', '0987654364', 'USER', 1, 1, '2024-03-06 13:00:00'),
('user45@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Văn Việt', '0987654365', 'USER', 1, 1, '2024-03-07 14:00:00'),
('user46@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Thị Yến', '0987654366', 'USER', 1, 1, '2024-03-08 15:00:00'),
('user47@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Văn Dũng', '0987654367', 'USER', 1, 1, '2024-03-09 16:00:00'),
('user48@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Thị Trang', '0987654368', 'USER', 1, 1, '2024-03-10 17:00:00'),
('user49@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Văn Thành', '0987654369', 'USER', 1, 1, '2024-03-11 18:00:00'),
('user50@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Hồng', '0987654370', 'USER', 1, 1, '2024-03-12 19:00:00'),
('user51@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Nam', '0987654371', 'USER', 1, 1, '2024-03-13 10:00:00'),
('user52@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Hương', '0987654372', 'USER', 1, 1, '2024-03-14 11:00:00'),
('user53@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn An', '0987654373', 'USER', 1, 1, '2024-03-15 12:00:00'),
('user54@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Dương', '0987654374', 'USER', 1, 1, '2024-03-16 13:00:00'),
('user55@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Văn Lâm', '0987654375', 'USER', 1, 1, '2024-03-17 14:00:00'),
('user56@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Thị Thảo', '0987654376', 'USER', 1, 1, '2024-03-18 15:00:00'),
('user57@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Tài', '0987654377', 'USER', 1, 1, '2024-03-19 16:00:00'),
('user58@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Linh', '0987654378', 'USER', 1, 1, '2024-03-20 17:00:00'),
('user59@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Văn Hào', '0987654379', 'USER', 1, 1, '2024-03-21 18:00:00'),
('user60@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Thị Nhung', '0987654380', 'USER', 1, 1, '2024-03-22 19:00:00'),
-- Users 61-80
('user61@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Văn Khải', '0987654381', 'USER', 1, 1, '2024-03-23 10:00:00'),
('user62@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Thị Mỹ', '0987654382', 'USER', 1, 1, '2024-03-24 11:00:00'),
('user63@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Văn Hùng', '0987654383', 'USER', 1, 1, '2024-03-25 12:00:00'),
('user64@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Thị Lan', '0987654384', 'USER', 1, 1, '2024-03-26 13:00:00'),
('user65@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Văn Đức', '0987654385', 'USER', 1, 1, '2024-03-27 14:00:00'),
('user66@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Thị Hạnh', '0987654386', 'USER', 1, 1, '2024-03-28 15:00:00'),
('user67@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Văn Quyền', '0987654387', 'USER', 1, 1, '2024-03-29 16:00:00'),
('user68@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Oanh', '0987654388', 'USER', 1, 1, '2024-03-30 17:00:00'),
('user69@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Sơn', '0987654389', 'USER', 1, 1, '2024-03-31 18:00:00'),
('user70@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Hạnh', '0987654390', 'USER', 1, 1, '2024-04-01 19:00:00'),
('user71@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn Tân', '0987654391', 'USER', 1, 1, '2024-04-02 10:00:00'),
('user72@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Quỳnh', '0987654392', 'USER', 1, 1, '2024-04-03 11:00:00'),
('user73@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Văn Hải', '0987654393', 'USER', 1, 1, '2024-04-04 12:00:00'),
('user74@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Thị Thu', '0987654394', 'USER', 1, 1, '2024-04-05 13:00:00'),
('user75@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Cảnh', '0987654395', 'USER', 1, 1, '2024-04-06 14:00:00'),
('user76@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Nhung', '0987654396', 'USER', 1, 1, '2024-04-07 15:00:00'),
('user77@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Văn Thắng', '0987654397', 'USER', 1, 1, '2024-04-08 16:00:00'),
('user78@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Thị Mai', '0987654398', 'USER', 1, 1, '2024-04-09 17:00:00'),
('user79@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Văn Thịnh', '0987654399', 'USER', 1, 1, '2024-04-10 18:00:00'),
('user80@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Thị Ly', '0987654400', 'USER', 1, 1, '2024-04-11 19:00:00'),
-- Users 81-99
('user81@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Văn Phúc', '0987654401', 'USER', 1, 1, '2024-04-12 10:00:00'),
('user82@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Thị Dung', '0987654402', 'USER', 1, 1, '2024-04-13 11:00:00'),
('user83@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Văn Lộc', '0987654403', 'USER', 1, 1, '2024-04-14 12:00:00'),
('user84@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Thị Ngân', '0987654404', 'USER', 1, 1, '2024-04-15 13:00:00'),
('user85@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Văn Hiếu', '0987654405', 'USER', 1, 1, '2024-04-16 14:00:00'),
('user86@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Thị Vân', '0987654406', 'USER', 1, 1, '2024-04-17 15:00:00'),
('user87@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Văn Tài', '0987654407', 'USER', 1, 1, '2024-04-18 16:00:00'),
('user88@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Thị Hoa', '0987654408', 'USER', 1, 1, '2024-04-19 17:00:00'),
('user89@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Văn Tiến', '0987654409', 'USER', 1, 1, '2024-04-20 18:00:00'),
('user90@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Thị Nga', '0987654410', 'USER', 1, 1, '2024-04-21 19:00:00'),
('user91@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Ngô Văn Đông', '0987654411', 'USER', 1, 1, '2024-04-22 10:00:00'),
('user92@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đinh Thị Hương', '0987654412', 'USER', 1, 1, '2024-04-23 11:00:00'),
('user93@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Lê Văn Cường', '0987654413', 'USER', 1, 1, '2024-04-24 12:00:00'),
('user94@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Trần Thị Yến', '0987654414', 'USER', 1, 1, '2024-04-25 13:00:00'),
('user95@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Phạm Văn Trung', '0987654415', 'USER', 1, 1, '2024-04-26 14:00:00'),
('user96@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Hoàng Thị Dịu', '0987654416', 'USER', 1, 1, '2024-04-27 15:00:00'),
('user97@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Vũ Văn Hào', '0987654417', 'USER', 1, 1, '2024-04-28 16:00:00'),
('user98@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Đặng Thị Mỹ', '0987654418', 'USER', 1, 1, '2024-04-29 17:00:00'),
('user99@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', N'Bùi Văn Khang', '0987654419', 'USER', 1, 1, '2024-04-30 18:00:00');

PRINT '  + Inserted 89 additional users (total 101 users)';
PRINT 'TIER 1 HOAN THANH!';
GO

-- =====================================================
-- TIER 2: ADD MORE PRODUCTS (85 products để đạt total 100)
-- =====================================================
PRINT '';
PRINT 'TIER 2: Dang them 85 Products moi (tong 100 products)...';

-- Nike products (16-25): thêm 10
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
(1, N'Nike Dunk Low', 'nike-dunk-low', N'Giày lifestyle Nike Dunk Low với thiết kế cổ điển', N'Giày lifestyle Nike Dunk Low', 1, 1, 0, 1500, 35, 4.4, 22),
(1, N'Nike Blazer Mid', 'nike-blazer-mid', N'Giày cổ điển Nike Blazer Mid với thiết kế 1970s', N'Giày cổ điển Nike Blazer Mid', 1, 0, 1, 800, 18, 4.2, 10),
(1, N'Nike Cortez', 'nike-cortez', N'Giày chạy bộ cổ điển Nike Cortez, biểu tượng 1970s', N'Giày chạy bộ Nike Cortez', 1, 0, 0, 1200, 28, 4.3, 15),
(1, N'Nike Pegasus 39', 'nike-pegasus-39', N'Giày chạy bộ Nike Pegasus 39 với công nghệ React', N'Giày chạy bộ Nike Pegasus 39', 1, 1, 1, 1350, 42, 4.5, 28),
(1, N'Nike Revolution 6', 'nike-revolution-6', N'Giày chạy bộ Nike Revolution 6 giá rẻ', N'Giày chạy bộ Nike Revolution 6', 1, 0, 0, 980, 22, 4.1, 12),
(1, N'Nike Zoom Fly', 'nike-zoom-fly', N'Giày chạy bộ Nike Zoom Fly với ZoomX', N'Giày chạy bộ Nike Zoom Fly', 1, 1, 1, 1800, 55, 4.6, 35),
(1, N'Nike Free RN', 'nike-free-rn', N'Giày chạy bộ Nike Free RN tự nhiên', N'Giày chạy bộ Nike Free RN', 1, 0, 0, 1100, 28, 4.2, 18),
(1, N'Nike Metcon 8', 'nike-metcon-8', N'Giày tập gym Nike Metcon 8', N'Giày tập gym Nike Metcon 8', 1, 1, 0, 2000, 65, 4.7, 42),
(1, N'Nike Tanjun', 'nike-tanjun', N'Giày lifestyle Nike Tanjun đơn giản', N'Giày lifestyle Nike Tanjun', 1, 0, 1, 950, 25, 4.0, 15),
(1, N'Nike Epic React', 'nike-epic-react', N'Giày chạy bộ Nike Epic React', N'Giày chạy bộ Nike Epic React', 1, 1, 0, 1650, 48, 4.5, 30);

-- Continue with more products from other brands...
-- This is a large file, I'll continue with pattern

PRINT '  + Inserted 10 Nike products (total 25 now)';

-- Adidas products (26-35): thêm 10
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
(2, N'Adidas Superstar', 'adidas-superstar', N'Giày sneakers cổ điển Adidas Superstar với vỏ sò đặc trưng', N'Giày sneakers Adidas Superstar', 1, 1, 0, 1800, 42, 4.5, 28),
(2, N'Adidas Yeezy 350', 'adidas-yeezy-350', N'Giày sneakers Adidas Yeezy 350 hiện đại', N'Giày sneakers Adidas Yeezy 350', 1, 1, 1, 2500, 68, 4.7, 45),
(2, N'Adidas Gazelle', 'adidas-gazelle', N'Giày sneakers Adidas Gazelle với chất liệu suede', N'Giày sneakers Adidas Gazelle', 1, 0, 0, 1000, 22, 4.1, 12),
(2, N'Adidas Response', 'adidas-response', N'Giày chạy bộ Adidas Response thoải mái', N'Giày chạy bộ Adidas Response', 1, 1, 1, 1450, 38, 4.4, 24),
(2, N'Adidas Forum Low', 'adidas-forum-low', N'Giày bóng rổ Adidas Forum Low cổ điển', N'Giày bóng rổ Adidas Forum Low', 1, 0, 1, 1600, 45, 4.3, 28),
(2, N'Adidas Samba', 'adidas-samba', N'Giày soccer Adidas Samba nổi tiếng', N'Giày soccer Adidas Samba', 1, 1, 0, 1750, 52, 4.6, 35),
(2, N'Adidas Campus', 'adidas-campus', N'Giày sneakers Adidas Campus', N'Giày sneakers Adidas Campus', 1, 0, 0, 1250, 32, 4.2, 20),
(2, N'Adidas ZX 750', 'adidas-zx-750', N'Giày chạy bộ Adidas ZX 750', N'Giày chạy bộ Adidas ZX 750', 1, 1, 1, 1550, 40, 4.4, 26),
(2, N'Adidas Tubular', 'adidas-tubular', N'Giày lifestyle Adidas Tubular', N'Giày lifestyle Adidas Tubular', 1, 0, 0, 1400, 36, 4.3, 22),
(2, N'Adidas Pureboost', 'adidas-pureboost', N'Giày chạy bộ Adidas Pureboost', N'Giày chạy bộ Adidas Pureboost', 1, 1, 0, 1700, 48, 4.5, 30);

PRINT '  + Inserted 10 Adidas products (total 35 now)';

-- Jordan products (36-45): thêm 10
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
(3, N'Air Jordan 3 Retro', 'air-jordan-3-retro', N'Giày bóng rổ Air Jordan 3 với Elephant Print', N'Giày bóng rổ Air Jordan 3', 1, 1, 0, 2100, 89, 4.6, 52),
(3, N'Air Jordan 12 Retro', 'air-jordan-12-retro', N'Giày bóng rổ Air Jordan 12 với Zoom Air', N'Giày bóng rổ Air Jordan 12', 1, 0, 1, 1700, 56, 4.5, 33),
(3, N'Air Jordan 5 Retro', 'air-jordan-5-retro', N'Giày bóng rổ Air Jordan 5 cổ điển', N'Giày bóng rổ Air Jordan 5', 1, 0, 0, 1500, 45, 4.4, 28),
(3, N'Air Jordan 13 Retro', 'air-jordan-13-retro', N'Giày bóng rổ Air Jordan 13 với Panther', N'Giày bóng rổ Air Jordan 13', 1, 1, 0, 1800, 62, 4.7, 38),
(3, N'Air Jordan 6 Retro', 'air-jordan-6-retro', N'Giày bóng rổ Air Jordan 6 classic', N'Giày bóng rổ Air Jordan 6', 1, 0, 1, 1600, 48, 4.5, 31),
(3, N'Air Jordan 7 Retro', 'air-jordan-7-retro', N'Giày bóng rổ Air Jordan 7', N'Giày bóng rổ Air Jordan 7', 1, 1, 0, 1900, 65, 4.6, 40),
(3, N'Air Jordan 8 Retro', 'air-jordan-8-retro', N'Giày bóng rổ Air Jordan 8', N'Giày bóng rổ Air Jordan 8', 1, 0, 0, 1650, 50, 4.4, 32),
(3, N'Air Jordan 9 Retro', 'air-jordan-9-retro', N'Giày bóng rổ Air Jordan 9', N'Giày bóng rổ Air Jordan 9', 1, 0, 1, 1550, 42, 4.3, 28),
(3, N'Air Jordan 10 Retro', 'air-jordan-10-retro', N'Giày bóng rổ Air Jordan 10', N'Giày bóng rổ Air Jordan 10', 1, 1, 0, 1750, 55, 4.5, 35),
(3, N'Air Jordan 14 Retro', 'air-jordan-14-retro', N'Giày bóng rổ Air Jordan 14', N'Giày bóng rổ Air Jordan 14', 1, 0, 0, 1680, 52, 4.4, 33);

PRINT '  + Inserted 10 Jordan products (total 45 now)';

-- Converse products (46-55): thêm 10
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
(4, N'Converse Chuck 70', 'converse-chuck-70', N'Giày canvas Converse Chuck 70 cao cấp', N'Giày canvas Converse Chuck 70', 1, 0, 0, 950, 24, 4.3, 14),
(4, N'Converse Run Star Hike', 'converse-run-star-hike', N'Giày platform Converse Run Star Hike', N'Giày platform Converse Run Star Hike', 1, 1, 1, 1800, 58, 4.6, 38),
(4, N'Converse Jack Purcell', 'converse-jack-purcell', N'Giày canvas Converse Jack Purcell', N'Giày canvas Converse Jack Purcell', 1, 0, 0, 1100, 28, 4.2, 16),
(4, N'Converse All Star High', 'converse-all-star-high', N'Giày canvas Converse All Star High', N'Giày canvas Converse All Star High', 1, 0, 1, 1300, 35, 4.3, 20),
(4, N'Converse Pro Leather', 'converse-pro-leather', N'Giày bóng rổ Converse Pro Leather', N'Giày bóng rổ Converse Pro Leather', 1, 1, 0, 1600, 45, 4.5, 28),
(4, N'Converse Weapon', 'converse-weapon', N'Giày bóng rổ Converse Weapon', N'Giày bóng rổ Converse Weapon', 1, 0, 0, 1400, 38, 4.4, 24),
(4, N'Converse Fastbreak', 'converse-fastbreak', N'Giày bóng rổ Converse Fastbreak', N'Giày bóng rổ Converse Fastbreak', 1, 0, 1, 1200, 32, 4.2, 18),
(4, N'Converse Aero Jam', 'converse-aero-jam', N'Giày bóng rổ Converse Aero Jam', N'Giày bóng rổ Converse Aero Jam', 1, 1, 0, 1500, 42, 4.4, 26),
(4, N'Converse Star Player', 'converse-star-player', N'Giày lifestyle Converse Star Player', N'Giày lifestyle Converse Star Player', 1, 0, 0, 1000, 26, 4.1, 14),
(4, N'Converse Renew', 'converse-renew', N'Giày tái chế Converse Renew', N'Giày tái chế Converse Renew', 1, 1, 1, 1700, 50, 4.6, 32);

PRINT '  + Inserted 10 Converse products (total 55 now)';

-- Vans products (56-65): thêm 10
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
(5, N'Vans Sk8-Hi', 'vans-sk8-hi', N'Giày skateboard Vans Sk8-Hi high-top', N'Giày skateboard Vans Sk8-Hi', 1, 1, 0, 1400, 38, 4.4, 21),
(5, N'Vans Slip-On', 'vans-slip-on', N'Giày canvas Vans Slip-On dễ mang', N'Giày canvas Vans Slip-On', 1, 0, 1, 1100, 29, 4.2, 16),
(5, N'Vans Era', 'vans-era', N'Giày skateboard Vans Era', N'Giày skateboard Vans Era', 1, 1, 0, 1300, 35, 4.3, 20),
(5, N'Vans Half Cab', 'vans-half-cab', N'Giày skateboard Vans Half Cab', N'Giày skateboard Vans Half Cab', 1, 0, 0, 1050, 27, 4.1, 15),
(5, N'Vans Chukka', 'vans-chukka', N'Giày skateboard Vans Chukka', N'Giày skateboard Vans Chukka', 1, 1, 1, 1450, 40, 4.5, 25),
(5, N'Vans Ave', 'vans-ave', N'Giày skateboard Vans Ave', N'Giày skateboard Vans Ave', 1, 0, 0, 1200, 31, 4.2, 18),
(5, N'Vans Atwood', 'vans-atwood', N'Giày skateboard Vans Atwood', N'Giày skateboard Vans Atwood', 1, 0, 1, 1150, 28, 4.1, 16),
(5, N'Vans Rowley', 'vans-rowley', N'Giày skateboard Vans Rowley', N'Giày skateboard Vans Rowley', 1, 1, 0, 1350, 36, 4.4, 22),
(5, N'Vans Ward', 'vans-ward', N'Giày skateboard Vans Ward', N'Giày skateboard Vans Ward', 1, 0, 0, 1250, 33, 4.3, 19),
(5, N'Vans Old Skool Pro', 'vans-old-skool-pro', N'Giày skateboard Vans Old Skool Pro', N'Giày skateboard Vans Old Skool Pro', 1, 1, 0, 1500, 42, 4.5, 27);

PRINT '  + Inserted 10 Vans products (total 65 now)';

-- Puma products (66-75): thêm 10 (bỏ Puma RS-X vì đã có trong file 2)
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
(6, N'Puma Speedcat', 'puma-speedcat', N'Giày đua Puma Speedcat', N'Giày đua Puma Speedcat', 1, 0, 0, 800, 17, 4.0, 9),
(6, N'Puma Thunder', 'puma-thunder', N'Giày lifestyle Puma Thunder', N'Giày lifestyle Puma Thunder', 1, 1, 1, 1650, 55, 4.6, 38),
(6, N'Puma Basket', 'puma-basket', N'Giày bóng rổ Puma Basket', N'Giày bóng rổ Puma Basket', 1, 0, 0, 1100, 30, 4.2, 18),
(6, N'Puma Cell', 'puma-cell', N'Giày chạy bộ Puma Cell', N'Giày chạy bộ Puma Cell', 1, 0, 1, 1400, 40, 4.4, 25),
(6, N'Puma Future', 'puma-future', N'Giày bóng đá Puma Future', N'Giày bóng đá Puma Future', 1, 1, 0, 1550, 48, 4.5, 32),
(6, N'Puma Deviate', 'puma-deviate', N'Giày chạy bộ Puma Deviate', N'Giày chạy bộ Puma Deviate', 1, 0, 0, 1250, 35, 4.3, 21),
(6, N'Puma Leadcat', 'puma-leadcat', N'Giày đua Puma Leadcat', N'Giày đua Puma Leadcat', 1, 0, 1, 950, 24, 4.1, 13),
(6, N'Puma TSUGI', 'puma-tsugi', N'Giày lifestyle Puma TSUGI', N'Giày lifestyle Puma TSUGI', 1, 1, 0, 1300, 37, 4.3, 23),
(6, N'Puma Platform', 'puma-platform', N'Giày platform Puma', N'Giày platform Puma', 1, 0, 0, 1200, 33, 4.2, 19),
(6, N'Puma Velocity', 'puma-velocity', N'Giày chạy bộ Puma Velocity', N'Giày chạy bộ Puma Velocity', 1, 1, 0, 1500, 45, 4.4, 28);

PRINT '  + Inserted 10 Puma products (total 75 now)';

-- New Balance, Reebok, Under Armour, ASICS products (76-100): thêm 25
INSERT INTO Products (brand_id, name, slug, description, short_description, is_active, is_featured, is_new, view_count, order_count, avg_rating, review_count) VALUES
-- New Balance (7): products 76-82 (bỏ New Balance 993 vì đã có trong file 2)
(7, N'New Balance 574', 'new-balance-574', N'Giày sneakers New Balance 574', N'Giày sneakers New Balance 574', 1, 1, 1, 1750, 52, 4.6, 38),
(7, N'New Balance 327', 'new-balance-327', N'Giày sneakers New Balance 327 retro', N'Giày sneakers New Balance 327', 1, 1, 0, 1650, 48, 4.5, 32),
(7, N'New Balance 1080', 'new-balance-1080', N'Giày chạy bộ New Balance 1080', N'Giày chạy bộ New Balance 1080', 1, 0, 1, 1850, 58, 4.7, 42),
(7, N'New Balance 990', 'new-balance-990', N'Giày chạy bộ New Balance 990', N'Giày chạy bộ New Balance 990', 1, 1, 0, 1900, 62, 4.8, 48),
(7, N'New Balance 2002R', 'new-balance-2002r', N'Giày sneakers New Balance 2002R', N'Giày sneakers New Balance 2002R', 1, 1, 1, 1800, 55, 4.6, 40),
(7, N'New Balance 57/40', 'new-balance-5740', N'Giày sneakers New Balance 57/40', N'Giày sneakers New Balance 57/40', 1, 0, 0, 1600, 45, 4.4, 28),
(7, N'New Balance XC-72', 'new-balance-xc72', N'Giày sneakers New Balance XC-72', N'Giày sneakers New Balance XC-72', 1, 1, 0, 1700, 50, 4.5, 33),
(7, N'New Balance 530', 'new-balance-530', N'Giày sneakers New Balance 530', N'Giày sneakers New Balance 530', 1, 0, 1, 1450, 38, 4.4, 25),
-- Reebok (8): products 84-88
(8, N'Reebok Classic Leather', 'reebok-classic-leather', N'Giày sneakers Reebok Classic Leather', N'Giày sneakers Reebok Classic Leather', 1, 0, 1, 720, 14, 4.1, 8),
(8, N'Reebok Nano', 'reebok-nano', N'Giày tập gym Reebok Nano', N'Giày tập gym Reebok Nano', 1, 0, 0, 650, 12, 4.2, 7),
(8, N'Reebok Club C 85', 'reebok-club-c-85', N'Giày tennis Reebok Club C 85', N'Giày tennis Reebok Club C 85', 1, 1, 0, 1100, 28, 4.3, 18),
(8, N'Reebok Pump', 'reebok-pump', N'Giày bóng rổ Reebok Pump', N'Giày bóng rổ Reebok Pump', 1, 1, 1, 1350, 42, 4.5, 28),
(8, N'Reebok Zig', 'reebok-zig', N'Giày chạy bộ Reebok Zig', N'Giày chạy bộ Reebok Zig', 1, 0, 0, 1000, 25, 4.2, 15),
(8, N'Reebok Question', 'reebok-question', N'Giày bóng rổ Reebok Question', N'Giày bóng rổ Reebok Question', 1, 1, 0, 1600, 48, 4.6, 32),
-- Under Armour (9): products 89-92
(9, N'Under Armour HOVR', 'under-armour-hovr', N'Giày chạy bộ Under Armour HOVR', N'Giày chạy bộ Under Armour HOVR', 1, 0, 0, 580, 10, 4.0, 6),
(9, N'Under Armour Curry', 'under-armour-curry', N'Giày bóng rổ Under Armour Curry', N'Giày bóng rổ Under Armour Curry', 1, 1, 1, 2200, 75, 4.8, 52),
(9, N'Under Armour Charged', 'under-armour-charged', N'Giày chạy bộ Under Armour Charged', N'Giày chạy bộ Under Armour Charged', 1, 0, 0, 850, 18, 4.1, 11),
(9, N'Under Armour Project Rock', 'under-armour-project-rock', N'Giày tập gym Under Armour Project Rock', N'Giày tập gym Under Armour Project Rock', 1, 1, 0, 1950, 65, 4.7, 45),
-- ASICS (10): products 93-100
(10, N'ASICS Tiger', 'asics-tiger', N'Giày sneakers ASICS Tiger', N'Giày sneakers ASICS Tiger', 1, 0, 0, 700, 15, 4.1, 9),
(10, N'ASICS Gel-Cumulus', 'asics-gel-cumulus', N'Giày chạy bộ ASICS Gel-Cumulus', N'Giày chạy bộ ASICS Gel-Cumulus', 1, 1, 1, 1550, 46, 4.5, 31),
(10, N'ASICS Gel-Nimbus', 'asics-gel-nimbus', N'Giày chạy bộ ASICS Gel-Nimbus', N'Giày chạy bộ ASICS Gel-Nimbus', 1, 1, 0, 1750, 54, 4.6, 38),
(10, N'ASICS Gel-Quantum', 'asics-gel-quantum', N'Giày chạy bộ ASICS Gel-Quantum', N'Giày chạy bộ ASICS Gel-Quantum', 1, 0, 0, 1650, 50, 4.5, 33),
(10, N'ASICS Gel-Lyte', 'asics-gel-lyte', N'Giày sneakers ASICS Gel-Lyte', N'Giày sneakers ASICS Gel-Lyte', 1, 1, 0, 1600, 48, 4.5, 30),
(10, N'ASICS Gel-Excite', 'asics-gel-excite', N'Giày chạy bộ ASICS Gel-Excite', N'Giày chạy bộ ASICS Gel-Excite', 1, 0, 1, 1450, 40, 4.4, 26),
(10, N'ASICS Gel-Venture', 'asics-gel-venture', N'Giày chạy bộ ASICS Gel-Venture', N'Giày chạy bộ ASICS Gel-Venture', 1, 1, 0, 1350, 37, 4.3, 23),
(10, N'ASICS Gel-Contend', 'asics-gel-contend', N'Giày chạy bộ ASICS Gel-Contend', N'Giày chạy bộ ASICS Gel-Contend', 1, 0, 0, 1200, 32, 4.2, 19),
(10, N'ASICS Gel-Kinsei', 'asics-gel-kinsei', N'Giày chạy bộ ASICS Gel-Kinsei', N'Giày chạy bộ ASICS Gel-Kinsei', 1, 1, 0, 1850, 60, 4.7, 42);

PRINT '  + Inserted 25 products từ New Balance, Reebok, Under Armour, ASICS (total ~100 products)';

-- 6. PRODUCT_CATEGORIES (add relationships for NEW products only - check existence first)
-- Chỉ insert cho products thực sự tồn tại, không assume ID range
DECLARE @pcProductId BIGINT;
DECLARE @pcCategoryId INT;

-- Insert categories cho products mới (từ product 16 trở đi, nhưng chỉ những products thực sự tồn tại)
SET @pcProductId = 16;
WHILE @pcProductId <= 100
BEGIN
    IF EXISTS (SELECT 1 FROM Products WHERE id = @pcProductId)
    BEGIN
        -- Chọn category dựa trên product_id
        SET @pcCategoryId = CASE 
            WHEN @pcProductId BETWEEN 36 AND 45 THEN 7 -- Jordan -> Basketball
            WHEN @pcProductId BETWEEN 46 AND 55 THEN CASE WHEN @pcProductId % 2 = 0 THEN 7 ELSE 5 END -- Converse mix
            WHEN @pcProductId BETWEEN 66 AND 75 THEN CASE WHEN @pcProductId % 3 = 0 THEN 7 ELSE 5 END -- Puma mix
            WHEN @pcProductId BETWEEN 76 AND 91 THEN CASE WHEN @pcProductId % 5 = 0 THEN 7 ELSE 5 END -- New Balance/Reebok/UA mix
            WHEN @pcProductId >= 92 THEN 5 -- ASICS -> Running
            ELSE 5 -- Default: Running shoes
        END;
        
        -- Chỉ insert nếu chưa tồn tại
        IF NOT EXISTS (SELECT 1 FROM Product_Categories WHERE product_id = @pcProductId AND category_id = @pcCategoryId)
        BEGIN
            INSERT INTO Product_Categories (product_id, category_id) VALUES (@pcProductId, @pcCategoryId);
        END;
    END;
    SET @pcProductId = @pcProductId + 1;
END;

DECLARE @pcCount INT;
SET @pcCount = (SELECT COUNT(*) FROM Product_Categories);
PRINT '  + Inserted product categories (total ' + CAST(@pcCount AS VARCHAR) + ' categories)';
PRINT 'TIER 2 HOAN THANH!';
GO

-- =====================================================
-- TIER 3: ADD MORE PRODUCT VARIANTS, IMAGES, COUPONS, FLASH_SALES
-- =====================================================
PRINT '';
PRINT 'TIER 3: Dang them Product_Variants, Product_Images, Coupons, Flash_Sales...';

-- 7. PRODUCT_VARIANTS (add 2-3 variants per new product, total ~70 variants to reach 100)
-- Products 16-25 (Nike): 2 variants each = 20 variants
INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, low_stock_threshold, is_active) VALUES
-- Product 16: Nike Dunk Low
(16, 'NIKE-DL-BLK-40', '40', N'Đen', 2800000, 2500000, 1500000, 20, 5, 1),
(16, 'NIKE-DL-WHT-40', '40', N'Trắng', 2800000, 2500000, 1500000, 18, 5, 1),
-- Product 17: Nike Blazer Mid
(17, 'NIKE-BM-BLK-40', '40', N'Đen', 2400000, 2200000, 1400000, 25, 5, 1),
(17, 'NIKE-BM-WHT-40', '40', N'Trắng', 2400000, 2200000, 1400000, 22, 5, 1),
-- Product 18: Nike Cortez
(18, 'NIKE-CORT-BLK-40', '40', N'Đen', 2200000, 2000000, 1300000, 30, 10, 1),
(18, 'NIKE-CORT-WHT-40', '40', N'Trắng', 2200000, 2000000, 1300000, 28, 10, 1),
-- Product 19: Nike Pegasus 39
(19, 'NIKE-PEG-BLK-40', '40', N'Đen', 3200000, 2900000, 2000000, 20, 5, 1),
(19, 'NIKE-PEG-BLU-40', '40', N'Xanh dương', 3200000, 2900000, 2000000, 18, 5, 1),
-- Product 20: Nike Revolution 6
(20, 'NIKE-REV-BLK-40', '40', N'Đen', 1800000, 1600000, 1100000, 35, 10, 1),
(20, 'NIKE-REV-GRY-40', '40', N'Xám', 1800000, 1600000, 1100000, 32, 10, 1),
-- Products 21-25 (continuing pattern, will add more batches)
(21, 'NIKE-ZF-BLK-40', '40', N'Đen', 3800000, 3500000, 2500000, 15, 5, 1),
(21, 'NIKE-ZF-RED-40', '40', N'Đỏ', 3800000, 3500000, 2500000, 12, 5, 1),
(22, 'NIKE-FRN-BLK-40', '40', N'Đen', 2000000, 1800000, 1200000, 28, 10, 1),
(22, 'NIKE-FRN-WHT-40', '40', N'Trắng', 2000000, 1800000, 1200000, 25, 10, 1),
(23, 'NIKE-MET-BLK-40', '40', N'Đen', 3500000, 3200000, 2200000, 18, 5, 1),
(23, 'NIKE-MET-GRY-40', '40', N'Xám', 3500000, 3200000, 2200000, 20, 5, 1),
(24, 'NIKE-TAN-BLK-40', '40', N'Đen', 1700000, 1500000, 1000000, 30, 10, 1),
(24, 'NIKE-TAN-WHT-40', '40', N'Trắng', 1700000, 1500000, 1000000, 28, 10, 1),
(25, 'NIKE-EPR-BLK-40', '40', N'Đen', 3100000, 2800000, 1900000, 22, 5, 1),
(25, 'NIKE-EPR-BLU-40', '40', N'Xanh dương', 3100000, 2800000, 1900000, 20, 5, 1);

PRINT '  + Inserted 20 variants for Nike products 16-25';
PRINT '  Note: File đang dài nên tôi đã thêm pattern. Cần thêm ~50 variants nữa cho products 26-100 để đạt ~100 total variants';

-- 8. PRODUCT_IMAGES (add 1 image per new product)
INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order) VALUES
(16, 'https://images.unsplash.com/photo-1600342848615-734266351fc8', N'Nike Dunk Low - Góc nhìn chính', 1, 1),
(17, 'https://images.unsplash.com/photo-1600240633522-40b2e89eb6a4', N'Nike Blazer Mid - Góc nhìn chính', 1, 1),
(18, 'https://images.unsplash.com/photo-1595959183082-7b570b7e08e2', N'Nike Cortez - Góc nhìn chính', 1, 1),
(19, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Nike Pegasus 39 - Góc nhìn chính', 1, 1),
(20, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa', N'Nike Revolution 6 - Góc nhìn chính', 1, 1),
(21, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a', N'Nike Zoom Fly - Góc nhìn chính', 1, 1),
(22, 'https://images.unsplash.com/photo-1544966503-7cc49d477ec6', N'Nike Free RN - Góc nhìn chính', 1, 1),
(23, 'https://images.unsplash.com/photo-1592065890607-7a96a9c4ec38', N'Nike Metcon 8 - Góc nhìn chính', 1, 1),
(24, 'https://images.unsplash.com/photo-1605446811894-70a14f4c1ce9', N'Nike Tanjun - Góc nhìn chính', 1, 1),
(25, 'https://images.unsplash.com/photo-1600047509807-ba8f99d2a3de', N'Nike Epic React - Góc nhìn chính', 1, 1);
-- Continue pattern for products 26-100 (85 more images needed)

PRINT '  + Inserted 10 product images (need 85 more for products 26-100)';

-- 9. COUPONS (add 95 coupons to reach 100)
-- Generate coupon codes with various discount types
-- Batch 1: Percentage coupons (6-50)
DECLARE @couponId INT;
DECLARE @month INT;
DECLARE @code VARCHAR(50);
DECLARE @desc NVARCHAR(MAX);

SET @couponId = 6;
SET @month = 1;

WHILE @couponId <= 50
BEGIN
    SET @code = 'SALE' + CAST(@month AS VARCHAR) + CAST(@couponId AS VARCHAR);
    SET @desc = N'Khuyến mãi tháng ' + CAST(@month AS VARCHAR);
    INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, applicable_to, is_active)
    VALUES (@code, @desc, 'percentage', CAST((@couponId % 20) + 5 AS DECIMAL(18,2)), 500000, 1000000, '2024-01-01', '2024-12-31', 1000, 0, 1, 'all', 1);
    SET @couponId = @couponId + 1;
    IF @couponId % 10 = 0 SET @month = @month + 1;
END;

-- Batch 2: Fixed amount coupons (51-100)
SET @couponId = 51;
WHILE @couponId <= 100
BEGIN
    SET @code = 'SAVE' + CAST(@couponId AS VARCHAR);
    SET @desc = N'Giảm ' + CAST((@couponId * 10000) AS VARCHAR) + ' VNĐ';
    INSERT INTO Coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount, start_at, end_at, max_uses, uses_count, max_uses_per_user, applicable_to, is_active)
    VALUES (@code, @desc, 'fixed', CAST(@couponId * 10000 AS DECIMAL(18,2)), CAST(@couponId * 50000 AS DECIMAL(18,2)), CAST(@couponId * 10000 AS DECIMAL(18,2)), '2024-01-01', '2024-12-31', 500, 0, 2, 'all', 1);
    SET @couponId = @couponId + 1;
END;

PRINT '  + Inserted 95 additional coupons (total 100 coupons)';

-- 10. FLASH_SALES (add 95 flash sales to reach 100)
-- Chỉ dùng products thực sự tồn tại
DECLARE @fsProductId BIGINT;
DECLARE @fsId INT;
DECLARE @fsDate DATE;
DECLARE @fsInserted INT = 0;

-- Lấy danh sách product IDs thực sự tồn tại
DECLARE @existingProducts TABLE (product_id BIGINT);
INSERT INTO @existingProducts SELECT id FROM Products ORDER BY id;

DECLARE product_cursor CURSOR FOR SELECT product_id FROM @existingProducts;
OPEN product_cursor;
FETCH NEXT FROM product_cursor INTO @fsProductId;

SET @fsId = 6;
SET @fsDate = '2024-03-01';

WHILE @fsId <= 100 AND @@FETCH_STATUS = 0
BEGIN
    -- Chỉ insert nếu product chưa có flash sale active
    IF NOT EXISTS (SELECT 1 FROM Flash_Sales WHERE product_id = @fsProductId AND is_active = 1)
    BEGIN
        INSERT INTO Flash_Sales (product_id, discount_percent, start_time, end_time, quantity_limit, sold_count, is_active)
        VALUES (@fsProductId, CAST((@fsId % 25) + 10 AS DECIMAL(5,2)), CAST(@fsDate AS DATETIME2), DATEADD(day, 1, CAST(@fsDate AS DATETIME2)), 50, 0, 1);
        SET @fsInserted = @fsInserted + 1;
    END;
    
    SET @fsId = @fsId + 1;
    IF @fsId % 7 = 0 SET @fsDate = DATEADD(day, 1, @fsDate);
    
    FETCH NEXT FROM product_cursor INTO @fsProductId;
END;

CLOSE product_cursor;
DEALLOCATE product_cursor;

DECLARE @fsTotalCount INT;
SET @fsTotalCount = (SELECT COUNT(*) FROM Flash_Sales);
PRINT '  + Inserted flash sales (total ' + CAST(@fsTotalCount AS VARCHAR) + ' flash sales)';
PRINT 'TIER 3 HOAN THANH!';
GO

-- Add more variants for products 26-100 (chỉ cho products thực sự tồn tại)
DECLARE @varProductId BIGINT;
DECLARE @varSku VARCHAR(100);
DECLARE @varBrandName VARCHAR(100);
DECLARE @variantInserted INT = 0;

SET @varProductId = 26;

WHILE @varProductId <= 100 AND @variantInserted < 100
BEGIN
    IF EXISTS (SELECT 1 FROM Products WHERE id = @varProductId)
    BEGIN
        -- Lấy brand name để tạo SKU phù hợp
        SET @varBrandName = (SELECT UPPER(LEFT(b.name, 4)) FROM Products p JOIN Brands b ON p.brand_id = b.id WHERE p.id = @varProductId);
        SET @varSku = @varBrandName + '-' + CAST(@varProductId AS VARCHAR) + '-BLK-40';
        
        -- Chỉ insert nếu SKU chưa tồn tại
        IF NOT EXISTS (SELECT 1 FROM Product_Variants WHERE sku = @varSku)
        BEGIN
            INSERT INTO Product_Variants (product_id, sku, size, color, price_base, price_sale, cost_price, stock_quantity, low_stock_threshold, is_active)
            VALUES (@varProductId, @varSku, '40', N'Đen', 2500000, 2300000, 1500000, 20, 5, 1);
            SET @variantInserted = @variantInserted + 1;
        END;
    END;
    SET @varProductId = @varProductId + 1;
END;

DECLARE @variantTotalCount INT;
SET @variantTotalCount = (SELECT COUNT(*) FROM Product_Variants);
PRINT '  + Inserted variants (total ' + CAST(@variantTotalCount AS VARCHAR) + ' variants)';

-- Add more images for products 26-100 (chỉ cho products thực sự tồn tại)
SET @varProductId = 26;
WHILE @varProductId <= 100
BEGIN
    IF EXISTS (SELECT 1 FROM Products WHERE id = @varProductId)
    BEGIN
        -- Chỉ insert nếu chưa có primary image
        IF NOT EXISTS (SELECT 1 FROM Product_Images WHERE product_id = @varProductId AND is_primary = 1)
        BEGIN
            INSERT INTO Product_Images (product_id, image_url, alt_text, is_primary, display_order)
            VALUES (@varProductId, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', N'Product ' + CAST(@varProductId AS VARCHAR) + ' - Main', 1, 1);
        END;
    END;
    SET @varProductId = @varProductId + 1;
END;

DECLARE @imageTotalCount INT;
SET @imageTotalCount = (SELECT COUNT(*) FROM Product_Images);
PRINT '  + Inserted product images (total ' + CAST(@imageTotalCount AS VARCHAR) + ' images)';
GO

-- =====================================================
-- TIER 4: ADD MORE ADDRESSES, NOTIFICATIONS, WISHLISTS, CARTS
-- =====================================================
PRINT '';
PRINT 'TIER 4: Dang them Addresses, Notifications, Wishlists, Carts...';

-- 11. ADDRESSES (add 90 addresses for users 3-99)
DECLARE @addrUserId BIGINT;
DECLARE @addrCounter INT;
DECLARE @addrName NVARCHAR(255);
DECLARE @addrPhone VARCHAR(20);
DECLARE @addrLine NVARCHAR(255);

SET @addrUserId = 3;
SET @addrCounter = 11;

WHILE @addrUserId <= 99 AND @addrCounter <= 100
BEGIN
    SET @addrName = (SELECT full_name FROM Users WHERE id = @addrUserId);
    SET @addrPhone = (SELECT phone_number FROM Users WHERE id = @addrUserId);
    SET @addrLine = N'Đường ' + CAST(@addrUserId * 10 AS VARCHAR) + ', Quận ' + CAST((@addrUserId % 12) + 1 AS VARCHAR);
    
    INSERT INTO Addresses (user_id, recipient_name, phone, line1, ward, district, city, postal_code, is_default, address_type, created_at)
    VALUES (@addrUserId, @addrName, @addrPhone, @addrLine, N'Phường ' + CAST((@addrUserId % 10) + 1 AS VARCHAR), N'Quận ' + CAST((@addrUserId % 12) + 1 AS VARCHAR), N'TP. Hồ Chí Minh', '700000', 
            CASE WHEN @addrCounter % 2 = 1 THEN 1 ELSE 0 END, 'home', DATEADD(day, @addrUserId - 3, '2024-03-01'));
    
    SET @addrUserId = @addrUserId + 1;
    SET @addrCounter = @addrCounter + 1;
END;

PRINT '  + Inserted ~90 additional addresses (total ~100 addresses)';

-- 12. NOTIFICATIONS (add 92 notifications to reach 100)
DECLARE @notifUserId BIGINT;
DECLARE @notifCounter INT;
DECLARE @notifType VARCHAR(50);
DECLARE @notifTitle NVARCHAR(255);
DECLARE @notifTypes TABLE (type VARCHAR(50));

INSERT INTO @notifTypes VALUES ('order_status'), ('promotion'), ('product_restock'), ('review_reply'), ('system');

SET @notifUserId = 3;
SET @notifCounter = 9;

WHILE @notifCounter <= 100
BEGIN
    SET @notifType = (SELECT type FROM (SELECT type, ROW_NUMBER() OVER (ORDER BY NEWID()) as rn FROM @notifTypes) t WHERE rn = (@notifCounter % 5) + 1);
    SET @notifTitle = CASE @notifType 
        WHEN 'order_status' THEN N'Đơn hàng đã được cập nhật'
        WHEN 'promotion' THEN N'Khuyến mãi mới!'
        WHEN 'product_restock' THEN N'Sản phẩm đã có hàng'
        WHEN 'review_reply' THEN N'Admin đã trả lời đánh giá'
        ELSE N'Thông báo hệ thống'
    END;
    
    INSERT INTO Notifications (user_id, type, title, message, link, is_read, created_at)
    VALUES (@notifUserId, @notifType, @notifTitle, N'Message ' + CAST(@notifCounter AS VARCHAR), '/', CASE WHEN @notifCounter % 3 = 0 THEN 1 ELSE 0 END, DATEADD(hour, @notifCounter, '2024-03-01'));
    
    SET @notifUserId = @notifUserId + 1;
    IF @notifUserId > 99 SET @notifUserId = 3;
    SET @notifCounter = @notifCounter + 1;
END;

PRINT '  + Inserted 92 additional notifications (total 100 notifications)';

-- 13. WISHLISTS (add 90 wishlist items to reach 100)
-- Chỉ dùng products và users thực sự tồn tại
DECLARE @wishUserId BIGINT;
DECLARE @wishCounter INT;
DECLARE @wishProductId BIGINT;
DECLARE @wishMaxIterations INT = 10000; -- Safety guard
DECLARE @wishIterations INT = 0;

SET @wishUserId = 3;
SET @wishCounter = 11;
SET @wishProductId = 1;

WHILE @wishCounter <= 100 AND @wishIterations < @wishMaxIterations
BEGIN
    SET @wishIterations = @wishIterations + 1;
    
    -- Check if user và product tồn tại, và combination chưa có
    IF EXISTS (SELECT 1 FROM Users WHERE id = @wishUserId) 
       AND EXISTS (SELECT 1 FROM Products WHERE id = @wishProductId)
       AND NOT EXISTS (SELECT 1 FROM Wishlists WHERE user_id = @wishUserId AND product_id = @wishProductId)
    BEGIN
        INSERT INTO Wishlists (user_id, product_id, created_at)
        VALUES (@wishUserId, @wishProductId, DATEADD(day, @wishCounter - 11, '2024-03-01'));
        SET @wishCounter = @wishCounter + 1;
    END;
    
    SET @wishProductId = @wishProductId + 1;
    IF @wishProductId > 100 
    BEGIN
        SET @wishProductId = 1;
        SET @wishUserId = @wishUserId + 1;
        IF @wishUserId > 99 SET @wishUserId = 3;
    END;
END;

PRINT '  + Inserted 90 additional wishlist items (total 100 wishlists)';

-- 14. CARTS (add 95 carts to reach 100, one per user)
DECLARE @cartUserId BIGINT = 8; -- Start from user 8 (users 3-7 already have carts)
WHILE @cartUserId <= 99
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Carts WHERE user_id = @cartUserId)
    BEGIN
        INSERT INTO Carts (user_id, session_id, created_at, updated_at)
        VALUES (@cartUserId, NULL, DATEADD(day, @cartUserId - 8, '2024-03-01'), DATEADD(day, @cartUserId - 8, '2024-03-01'));
    END;
    SET @cartUserId = @cartUserId + 1;
END;

PRINT '  + Inserted ~90 additional carts (total ~95 carts)';
PRINT 'TIER 4 HOAN THANH!';
GO

-- =====================================================
-- TIER 5: ADD MORE ORDERS
-- =====================================================
PRINT '';
PRINT 'TIER 5: Dang them Orders moi (90 orders de dat tong 100)...';

-- 15. ORDERS (add 90 orders to reach 100)
-- Generate orders for users with addresses
DECLARE @orderUserId BIGINT;
DECLARE @orderCounter INT;
DECLARE @orderDate DATE;
DECLARE @orderSequence INT;
DECLARE @orderAddressId BIGINT;
DECLARE @orderNumber VARCHAR(50);
DECLARE @subtotal DECIMAL(18,2);
DECLARE @discount DECIMAL(18,2);
DECLARE @total DECIMAL(18,2);
DECLARE @orderStatus VARCHAR(50);

SET @orderUserId = 8;
SET @orderCounter = 11;
SET @orderDate = '2024-03-06';
SET @orderSequence = 1;

WHILE @orderCounter <= 100
BEGIN
    SET @orderAddressId = (SELECT TOP 1 id FROM Addresses WHERE user_id = @orderUserId ORDER BY id);
    SET @orderNumber = 'ORD-' + FORMAT(CAST(@orderDate AS DATETIME2), 'yyyyMMdd') + '-' + FORMAT(@orderSequence, '0000');
    SET @subtotal = CAST((ABS(CHECKSUM(NEWID())) % 3000000 + 1500000) AS DECIMAL(18,2));
    SET @discount = @subtotal * 0.1;
    SET @total = @subtotal - @discount + 50000;
    SET @orderStatus = CASE (@orderCounter % 8)
        WHEN 0 THEN 'delivered'
        WHEN 1 THEN 'shipped'
        WHEN 2 THEN 'processing'
        WHEN 3 THEN 'confirmed'
        WHEN 4 THEN 'packed'
        ELSE 'pending'
    END;
    
    IF @orderAddressId IS NOT NULL
    BEGIN
        INSERT INTO Orders (user_id, order_number, address_shipping_id, subtotal, shipping_fee, discount_amount, total_amount, status, shipping_method, created_at)
        VALUES (@orderUserId, @orderNumber, @orderAddressId, @subtotal, 50000, @discount, @total, @orderStatus, 'standard', CAST(@orderDate AS DATETIME2));
    END;
    
    SET @orderUserId = @orderUserId + 1;
    IF @orderUserId > 99 SET @orderUserId = 8;
    SET @orderCounter = @orderCounter + 1;
    SET @orderSequence = @orderSequence + 1;
    IF @orderSequence > 9999 
    BEGIN
        SET @orderSequence = 1;
        SET @orderDate = DATEADD(day, 1, @orderDate);
    END;
END;

PRINT '  + Inserted 90 additional orders (total 100 orders)';
PRINT 'TIER 5 HOAN THANH!';
GO

-- =====================================================
-- TIER 6: ADD MORE ORDER_DETAILS, PAYMENTS, STATUS_HISTORIES
-- =====================================================
PRINT '';
PRINT 'TIER 6: Dang them Order_Details, Payments, Order_Status_Histories...';

-- 16. ORDER_DETAILS (add 90 details for orders 11-100)
-- Chỉ dùng orders và variants thực sự tồn tại
DECLARE @odOrderId BIGINT;
DECLARE @odCounter INT;
DECLARE @odVariantId BIGINT;
DECLARE @odProductName NVARCHAR(255);
DECLARE @odSku VARCHAR(100);
DECLARE @odSize VARCHAR(20);
DECLARE @odColor NVARCHAR(50);
DECLARE @odPrice DECIMAL(18,2);
DECLARE @odQty INT;
DECLARE @odMaxVariantId BIGINT;
DECLARE @odMaxIterations INT = 200; -- Safety guard (có tối đa 100 orders)
DECLARE @odIterations INT = 0;

-- Lấy max variant ID thực sự tồn tại
SET @odMaxVariantId = (SELECT MAX(id) FROM Product_Variants);

SET @odOrderId = 11;
SET @odCounter = 11;

WHILE @odCounter <= 100 AND @odIterations < @odMaxIterations
BEGIN
    SET @odIterations = @odIterations + 1;
    
    -- Chỉ xử lý nếu order tồn tại và chưa có detail
    IF EXISTS (SELECT 1 FROM Orders WHERE id = @odOrderId)
       AND NOT EXISTS (SELECT 1 FROM Order_Details WHERE order_id = @odOrderId)
    BEGIN
        -- Lấy variant ngẫu nhiên từ danh sách variants tồn tại
        SET @odVariantId = (SELECT TOP 1 id FROM Product_Variants 
                           WHERE id BETWEEN 1 AND @odMaxVariantId 
                           ORDER BY NEWID());
        
        IF @odVariantId IS NOT NULL
        BEGIN
            SET @odProductName = (SELECT p.name FROM Products p JOIN Product_Variants pv ON p.id = pv.product_id WHERE pv.id = @odVariantId);
            SET @odSku = (SELECT sku FROM Product_Variants WHERE id = @odVariantId);
            SET @odSize = (SELECT size FROM Product_Variants WHERE id = @odVariantId);
            SET @odColor = (SELECT color FROM Product_Variants WHERE id = @odVariantId);
            SET @odPrice = (SELECT ISNULL(price_sale, price_base) FROM Product_Variants WHERE id = @odVariantId);
            SET @odQty = CAST((ABS(CHECKSUM(NEWID())) % 2 + 1) AS INT);
            
            IF @odProductName IS NOT NULL AND @odSku IS NOT NULL AND @odPrice IS NOT NULL
            BEGIN
                INSERT INTO Order_Details (order_id, variant_id, product_name, variant_sku, size, color, quantity, unit_price, total_price)
                VALUES (@odOrderId, @odVariantId, @odProductName, @odSku, @odSize, @odColor, @odQty, @odPrice, @odPrice * @odQty);
                SET @odCounter = @odCounter + 1;
            END;
        END;
    END;
    
    SET @odOrderId = @odOrderId + 1;
    -- Nếu đã quét hết tất cả orders (11-100), dừng lại thay vì reset
    IF @odOrderId > 100 
    BEGIN
        -- Chỉ reset nếu chưa đạt mục tiêu và chưa quá max iterations
        IF @odCounter < 100 AND @odIterations < @odMaxIterations
        BEGIN
            SET @odOrderId = 11;
        END
        ELSE
        BEGIN
            BREAK; -- Dừng loop nếu đã quét hết
        END;
    END;
END;

PRINT '  + Inserted 90 additional order details (total 100 order details)';

-- 17. PAYMENTS (add 90 payments for orders 11-100)
DECLARE @payOrderId BIGINT;
DECLARE @payCounter INT;
DECLARE @payAmount DECIMAL(18,2);
DECLARE @payMethod VARCHAR(50);
DECLARE @payStatus VARCHAR(50);
DECLARE @payMaxIterations INT = 200; -- Safety guard (có tối đa 100 orders)
DECLARE @payIterations INT = 0;
DECLARE @payStartOrderId BIGINT;
DECLARE @payMethods TABLE (method VARCHAR(50));

INSERT INTO @payMethods VALUES ('vnpay'), ('momo'), ('cod'), ('zalopay'), ('bank_transfer');

SET @payOrderId = 11;
SET @payCounter = 11;
SET @payStartOrderId = 11;

WHILE @payCounter <= 100 AND @payIterations < @payMaxIterations
BEGIN
    SET @payIterations = @payIterations + 1;
    
    -- Chỉ xử lý nếu order tồn tại và chưa có payment
    IF EXISTS (SELECT 1 FROM Orders WHERE id = @payOrderId) 
       AND NOT EXISTS (SELECT 1 FROM Payments WHERE order_id = @payOrderId)
    BEGIN
        SET @payAmount = (SELECT total_amount FROM Orders WHERE id = @payOrderId);
        
        -- Đảm bảo amount không NULL
        IF @payAmount IS NOT NULL AND @payAmount > 0
        BEGIN
            SET @payMethod = (SELECT method FROM (SELECT method, ROW_NUMBER() OVER (ORDER BY NEWID()) as rn FROM @payMethods) t WHERE rn = (@payCounter % 5) + 1);
            SET @payStatus = CASE (@payCounter % 5)
                WHEN 0 THEN 'completed'
                WHEN 1 THEN 'completed'
                WHEN 2 THEN 'processing'
                ELSE 'pending'
            END;
            
            INSERT INTO Payments (order_id, payment_method, amount, status, transaction_id, paid_at, created_at)
            VALUES (@payOrderId, @payMethod, @payAmount, @payStatus, 
                     CASE WHEN @payStatus = 'completed' THEN @payMethod + '_' + FORMAT(GETDATE(), 'yyyyMMdd') + '_' + CAST(@payCounter AS VARCHAR) ELSE NULL END,
                     CASE WHEN @payStatus = 'completed' THEN DATEADD(minute, 10, (SELECT created_at FROM Orders WHERE id = @payOrderId)) ELSE NULL END,
                     (SELECT created_at FROM Orders WHERE id = @payOrderId));
            SET @payCounter = @payCounter + 1;
        END;
    END;
    
    SET @payOrderId = @payOrderId + 1;
    -- Nếu đã quét hết tất cả orders (11-100), dừng lại thay vì reset
    IF @payOrderId > 100 
    BEGIN
        -- Chỉ reset nếu chưa đạt mục tiêu và chưa quá max iterations
        IF @payCounter < 100 AND @payIterations < @payMaxIterations
        BEGIN
            SET @payOrderId = 11;
        END
        ELSE
        BEGIN
            BREAK; -- Dừng loop nếu đã quét hết
        END;
    END;
END;

PRINT '  + Inserted 90 additional payments (total 100 payments)';

-- 18. ORDER_STATUS_HISTORIES (add 90 histories)
-- Chỉ dùng orders thực sự tồn tại
DECLARE @oshOrderId BIGINT;
DECLARE @oshCounter INT;
DECLARE @oshStatus VARCHAR(50);
DECLARE @oshMaxIterations INT = 200; -- Safety guard
DECLARE @oshIterations INT = 0;

SET @oshOrderId = 11;
SET @oshCounter = 11;

WHILE @oshCounter <= 100 AND @oshIterations < @oshMaxIterations
BEGIN
    SET @oshIterations = @oshIterations + 1;
    
    -- Chỉ xử lý nếu order tồn tại
    IF EXISTS (SELECT 1 FROM Orders WHERE id = @oshOrderId)
    BEGIN
        SET @oshStatus = CASE (@oshCounter % 6)
            WHEN 0 THEN 'pending'
            WHEN 1 THEN 'confirmed'
            WHEN 2 THEN 'processing'
            WHEN 3 THEN 'shipped'
            WHEN 4 THEN 'delivered'
            ELSE 'packed'
        END;
        
        INSERT INTO Order_Status_Histories (order_id, status, note, changed_by, created_at)
        VALUES (@oshOrderId, @oshStatus, N'Trạng thái: ' + @oshStatus, 
               (SELECT user_id FROM Orders WHERE id = @oshOrderId), 
               DATEADD(hour, @oshCounter, (SELECT created_at FROM Orders WHERE id = @oshOrderId)));
        SET @oshCounter = @oshCounter + 1;
    END;
    
    SET @oshOrderId = @oshOrderId + 1;
    -- Nếu đã quét hết tất cả orders (11-100), dừng lại thay vì reset
    IF @oshOrderId > 100 
    BEGIN
        -- Chỉ reset nếu chưa đạt mục tiêu và chưa quá max iterations
        IF @oshCounter < 100 AND @oshIterations < @oshMaxIterations
        BEGIN
            SET @oshOrderId = 11;
        END
        ELSE
        BEGIN
            BREAK; -- Dừng loop nếu đã quét hết
        END;
    END;
END;

PRINT '  + Inserted 90 additional order status histories (total 100 histories)';
PRINT 'TIER 6 HOAN THANH!';
GO

-- =====================================================
-- TIER 7: ADD MORE COMPLEX DATA
-- =====================================================
PRINT '';
PRINT 'TIER 7: Dang them Reviews, Inventory_Logs, Activity_Logs, Loyalty_Points, Cart_Items, Size_Charts, Return_Requests...';

-- 19. REVIEWS (add 95 reviews to reach 100)
DECLARE @revProductId BIGINT;
DECLARE @revUserId BIGINT;
DECLARE @revCounter INT;
DECLARE @revRating INT;
DECLARE @revMaxIterations INT = 10000; -- Safety guard
DECLARE @revIterations INT = 0;

SET @revProductId = 1;
SET @revUserId = 3;
SET @revCounter = 6;

WHILE @revCounter <= 100 AND @revIterations < @revMaxIterations
BEGIN
    SET @revIterations = @revIterations + 1;
    
    -- Chỉ xử lý nếu product và user tồn tại
    IF EXISTS (SELECT 1 FROM Products WHERE id = @revProductId) 
       AND EXISTS (SELECT 1 FROM Users WHERE id = @revUserId)
       AND NOT EXISTS (SELECT 1 FROM Reviews WHERE product_id = @revProductId AND user_id = @revUserId)
    BEGIN
        SET @revRating = CAST((ABS(CHECKSUM(NEWID())) % 3 + 3) AS INT);
        INSERT INTO Reviews (product_id, user_id, rating, title, body, is_approved, is_verified_purchase, helpful_count, created_at)
        VALUES (@revProductId, @revUserId, @revRating, N'Đánh giá sản phẩm ' + CAST(@revProductId AS VARCHAR), 
                N'Đây là đánh giá cho sản phẩm ' + CAST(@revProductId AS VARCHAR) + '. Rất tốt!', 
                CASE WHEN @revCounter % 3 = 0 THEN 1 ELSE 0 END, 
                CASE WHEN @revCounter % 2 = 0 THEN 1 ELSE 0 END,
                CAST((ABS(CHECKSUM(NEWID())) % 10) AS INT), DATEADD(day, @revCounter - 6, '2024-03-10'));
        SET @revCounter = @revCounter + 1;
    END;
    
    SET @revProductId = @revProductId + 1;
    IF @revProductId > 100 
    BEGIN
        SET @revProductId = 1;
        SET @revUserId = @revUserId + 1;
        IF @revUserId > 99 SET @revUserId = 3;
    END;
END;

PRINT '  + Inserted ~95 additional reviews (total 100 reviews)';

-- 20. INVENTORY_LOGS (add 90 logs to reach 100)
DECLARE @invVariantId BIGINT;
DECLARE @invCounter INT;
DECLARE @changeType VARCHAR(50);
DECLARE @qtyBefore INT;
DECLARE @qtyChange INT;
DECLARE @qtyAfter INT;
DECLARE @invMaxVariantId BIGINT;
DECLARE @invMaxIterations INT = 500; -- Safety guard
DECLARE @invIterations INT = 0;

-- Lấy max variant ID thực sự tồn tại
SET @invMaxVariantId = ISNULL((SELECT MAX(id) FROM Product_Variants), 0);
SET @invVariantId = 1;
SET @invCounter = 11;

WHILE @invCounter <= 100 AND @invIterations < @invMaxIterations AND @invMaxVariantId > 0
BEGIN
    SET @invIterations = @invIterations + 1;
    
    -- Chỉ insert nếu variant tồn tại
    IF EXISTS (SELECT 1 FROM Product_Variants WHERE id = @invVariantId)
    BEGIN
        SET @changeType = CASE (@invCounter % 3)
            WHEN 0 THEN 'restock'
            WHEN 1 THEN 'sale'
            ELSE 'adjustment'
        END;
        SET @qtyBefore = CAST((ABS(CHECKSUM(NEWID())) % 50) AS INT);
        SET @qtyChange = CASE @changeType WHEN 'restock' THEN CAST((ABS(CHECKSUM(NEWID())) % 20 + 10) AS INT) ELSE -CAST((ABS(CHECKSUM(NEWID())) % 5 + 1) AS INT) END;
        SET @qtyAfter = @qtyBefore + @qtyChange;
        
        INSERT INTO Inventory_Logs (variant_id, change_type, quantity_before, quantity_change, quantity_after, reference_type, note, changed_by, created_at)
        VALUES (@invVariantId, @changeType, @qtyBefore, @qtyChange, @qtyAfter, 
                CASE @changeType WHEN 'sale' THEN 'order' ELSE 'purchase' END,
                N'Log ' + CAST(@invCounter AS VARCHAR), 1, DATEADD(day, @invCounter - 11, '2024-03-01'));
        
        SET @invCounter = @invCounter + 1;
    END;
    
    SET @invVariantId = @invVariantId + 1;
    -- Chỉ reset nếu chưa đạt mục tiêu và chưa quá max iterations
    IF @invVariantId > @invMaxVariantId 
    BEGIN
        IF @invCounter < 100 AND @invIterations < @invMaxIterations
        BEGIN
            SET @invVariantId = 1;
        END
        ELSE
        BEGIN
            BREAK;
        END;
    END;
END;

PRINT '  + Inserted 90 additional inventory logs (total 100 logs)';

-- 21. ACTIVITY_LOGS (add 95 logs to reach 100)
DECLARE @actUserId BIGINT;
DECLARE @actCounter INT;
DECLARE @actAction VARCHAR(100);
DECLARE @actEntityType VARCHAR(50);
DECLARE @actActions TABLE (action VARCHAR(100));

INSERT INTO @actActions VALUES ('CREATE'), ('UPDATE'), ('DELETE'), ('APPROVE'), ('REJECT');

SET @actUserId = 1;
SET @actCounter = 6;

WHILE @actCounter <= 100
BEGIN
    SET @actAction = (SELECT action FROM (SELECT action, ROW_NUMBER() OVER (ORDER BY NEWID()) as rn FROM @actActions) t WHERE rn = (@actCounter % 5) + 1);
    SET @actEntityType = CASE (@actCounter % 4)
        WHEN 0 THEN 'product'
        WHEN 1 THEN 'order'
        WHEN 2 THEN 'user'
        ELSE 'review'
    END;
    
    INSERT INTO Activity_Logs (user_id, action, entity_type, entity_id, old_value, new_value, ip_address, user_agent, created_at)
    VALUES (@actUserId, @actAction, @actEntityType, @actCounter, NULL, N'{"status":"updated"}', '192.168.1.' + CAST((@actCounter % 255) AS VARCHAR), 
            'Mozilla/5.0', DATEADD(hour, @actCounter, '2024-03-01'));
    
    SET @actUserId = @actUserId + 1;
    IF @actUserId > 2 SET @actUserId = 1; -- Rotate between admin users
    SET @actCounter = @actCounter + 1;
END;

PRINT '  + Inserted 95 additional activity logs (total 100 logs)';

-- 22. LOYALTY_POINTS (add 90 points transactions to reach 100)
DECLARE @lpUserId BIGINT;
DECLARE @lpCounter INT;
DECLARE @lpPoints INT;
DECLARE @lpType VARCHAR(20);
DECLARE @lpOrderId BIGINT;

SET @lpUserId = 8;
SET @lpCounter = 11;

WHILE @lpCounter <= 100
BEGIN
    -- Chỉ xử lý nếu user tồn tại
    IF EXISTS (SELECT 1 FROM Users WHERE id = @lpUserId)
    BEGIN
        SET @lpPoints = CAST((ABS(CHECKSUM(NEWID())) % 500 + 100) AS INT);
        SET @lpType = CASE (@lpCounter % 3)
            WHEN 0 THEN 'earn'
            WHEN 1 THEN 'redeem'
            ELSE 'earn'
        END;
        SET @lpOrderId = CASE WHEN @lpType = 'earn' THEN CAST((ABS(CHECKSUM(NEWID())) % 100 + 1) AS BIGINT) ELSE NULL END;
        
        -- Nếu có order_id, phải kiểm tra order tồn tại
        IF @lpOrderId IS NULL OR EXISTS (SELECT 1 FROM Orders WHERE id = @lpOrderId)
        BEGIN
            INSERT INTO Loyalty_Points (user_id, points, transaction_type, earned_from_order_id, description, created_at)
            VALUES (@lpUserId, @lpPoints, @lpType, @lpOrderId, 
                    CASE @lpType WHEN 'earn' THEN N'Tích điểm từ đơn hàng' ELSE N'Sử dụng điểm' END,
                    DATEADD(day, @lpCounter - 11, '2024-03-01'));
            SET @lpCounter = @lpCounter + 1;
        END;
    END;
    
    SET @lpUserId = @lpUserId + 1;
    IF @lpUserId > 99 SET @lpUserId = 8;
END;

PRINT '  + Inserted 90 additional loyalty points (total 100 points)';

-- 23. CART_ITEMS (add 95 items to reach 100)
DECLARE @ciCartId BIGINT;
DECLARE @ciCounter INT;
DECLARE @ciVariantId BIGINT;
DECLARE @ciQty INT;
DECLARE @ciMaxIterations INT = 10000; -- Safety guard
DECLARE @ciIterations INT = 0;

SET @ciCartId = 1;
SET @ciCounter = 6;

WHILE @ciCounter <= 100 AND @ciIterations < @ciMaxIterations
BEGIN
    SET @ciIterations = @ciIterations + 1;
    SET @ciVariantId = CAST((ABS(CHECKSUM(NEWID())) % 100 + 1) AS INT);
    SET @ciQty = CAST((ABS(CHECKSUM(NEWID())) % 3 + 1) AS INT);
    
    IF EXISTS (SELECT 1 FROM Carts WHERE id = @ciCartId) AND EXISTS (SELECT 1 FROM Product_Variants WHERE id = @ciVariantId)
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM Cart_Items WHERE cart_id = @ciCartId AND variant_id = @ciVariantId)
        BEGIN
            INSERT INTO Cart_Items (cart_id, variant_id, quantity, added_at)
            VALUES (@ciCartId, @ciVariantId, @ciQty, DATEADD(day, @ciCounter - 6, '2024-03-01'));
            SET @ciCounter = @ciCounter + 1;
        END;
    END;
    
    SET @ciCartId = @ciCartId + 1;
    IF @ciCartId > 95 SET @ciCartId = 1;
END;

PRINT '  + Inserted ~95 additional cart items (total 100 cart items)';

-- 24. SIZE_CHARTS (add 95 size charts to reach 100)
DECLARE @scBrandId INT;
DECLARE @scCounter INT;
DECLARE @scCategory NVARCHAR(50);
DECLARE @scSize VARCHAR(10);
DECLARE @scMaxIterations INT = 5000; -- Safety guard
DECLARE @scIterations INT = 0;
DECLARE @scCategories TABLE (category NVARCHAR(50));

INSERT INTO @scCategories VALUES ('sneakers'), ('running'), ('basketball'), ('lifestyle'), ('skateboard');

SET @scBrandId = 1;
SET @scCounter = 6;

WHILE @scCounter <= 100 AND @scIterations < @scMaxIterations
BEGIN
    SET @scIterations = @scIterations + 1;
    SET @scCategory = (SELECT category FROM (SELECT category, ROW_NUMBER() OVER (ORDER BY NEWID()) as rn FROM @scCategories) t WHERE rn = (@scCounter % 5) + 1);
    SET @scSize = CAST(35 + (@scCounter % 10) AS VARCHAR);
    
    IF NOT EXISTS (SELECT 1 FROM Size_Charts WHERE brand_id = @scBrandId AND category = @scCategory AND size = @scSize)
    BEGIN
        INSERT INTO Size_Charts (brand_id, category, size, size_us, size_uk, length_cm, width_cm)
        VALUES (@scBrandId, @scCategory, @scSize, CAST(CAST(@scSize AS INT) - 33 AS VARCHAR), CAST(CAST(@scSize AS INT) - 34 AS VARCHAR), 
                CAST(CAST(@scSize AS INT) * 0.635 AS DECIMAL(5,2)), CAST(CAST(@scSize AS INT) * 0.24 AS DECIMAL(5,2)));
        SET @scCounter = @scCounter + 1;
    END;
    
    SET @scBrandId = @scBrandId + 1;
    IF @scBrandId > 10 SET @scBrandId = 1;
END;

PRINT '  + Inserted ~95 additional size charts (total 100 size charts)';

-- 25. RETURN_REQUESTS (add 98 return requests to reach 100)
DECLARE @rrOrderId BIGINT;
DECLARE @rrCounter INT;
DECLARE @rrUserId BIGINT;
DECLARE @rrReason NVARCHAR(MAX);
DECLARE @rrStatus VARCHAR(20);
DECLARE @rrMaxIterations INT = 1000; -- Safety guard
DECLARE @rrIterations INT = 0;
DECLARE @rrReasons TABLE (reason NVARCHAR(MAX));

INSERT INTO @rrReasons VALUES (N'Không đúng kích thước'), (N'Sản phẩm bị lỗi'), (N'Không đúng màu sắc'), (N'Sản phẩm bị hỏng'), (N'Không hài lòng');

SET @rrOrderId = 1;
SET @rrCounter = 3;

WHILE @rrCounter <= 100 AND @rrIterations < @rrMaxIterations
BEGIN
    SET @rrIterations = @rrIterations + 1;
    SET @rrUserId = (SELECT user_id FROM Orders WHERE id = @rrOrderId);
    
    IF @rrUserId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Return_Requests WHERE order_id = @rrOrderId)
    BEGIN
        SET @rrReason = (SELECT reason FROM (SELECT reason, ROW_NUMBER() OVER (ORDER BY NEWID()) as rn FROM @rrReasons) t WHERE rn = (@rrCounter % 5) + 1);
        SET @rrStatus = CASE (@rrCounter % 4)
            WHEN 0 THEN 'pending'
            WHEN 1 THEN 'approved'
            WHEN 2 THEN 'rejected'
            ELSE 'completed'
        END;
        
        INSERT INTO Return_Requests (order_id, user_id, reason, status, created_at)
        VALUES (@rrOrderId, @rrUserId, @rrReason, @rrStatus, DATEADD(day, @rrCounter - 3, '2024-03-15'));
        SET @rrCounter = @rrCounter + 1;
    END;
    
    SET @rrOrderId = @rrOrderId + 1;
    -- Nếu đã quét hết tất cả orders (1-100), dừng lại thay vì reset vô tận
    IF @rrOrderId > 100 
    BEGIN
        -- Chỉ reset nếu chưa đạt mục tiêu và chưa quá max iterations
        IF @rrCounter < 100 AND @rrIterations < @rrMaxIterations
        BEGIN
            SET @rrOrderId = 1;
        END
        ELSE
        BEGIN
            BREAK; -- Dừng loop nếu đã quét hết
        END;
    END;
END;

PRINT '  + Inserted 98 additional return requests (total 100 return requests)';
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
PRINT 'HOAN THANH BO SUNG DU LIEU!';
PRINT '=====================================================';
PRINT '';
PRINT 'Da bo sung thanh cong:';
PRINT '  + 89 users (tong 101 users)';
PRINT '  + 85 products (tong 100 products)';
PRINT '  + 85 product categories';
PRINT '  + ~100 product variants (tong ~130 variants)';
PRINT '  + 85 product images (tong 100 images)';
PRINT '  + 95 coupons (tong 100 coupons)';
PRINT '  + 95 flash sales (tong 100 flash sales)';
PRINT '  + ~90 addresses (tong ~100 addresses)';
PRINT '  + 90 orders (tong 100 orders)';
PRINT '  + 90 order details (tong 100 order details)';
PRINT '  + 90 payments (tong 100 payments)';
PRINT '  + 90 order status histories (tong 100 histories)';
PRINT '  + ~95 reviews (tong 100 reviews)';
PRINT '  + 92 notifications (tong 100 notifications)';
PRINT '  + 90 wishlists (tong 100 wishlists)';
PRINT '  + ~90 carts (tong ~95 carts)';
PRINT '  + ~95 cart items (tong 100 cart items)';
PRINT '  + 90 loyalty points (tong 100 points)';
PRINT '  + 90 inventory logs (tong 100 logs)';
PRINT '  + 95 activity logs (tong 100 logs)';
PRINT '  + ~95 size charts (tong 100 size charts)';
PRINT '  + 98 return requests (tong 100 return requests)';
PRINT '';
PRINT 'TAT CA DU LIEU DA DUOC BO SUNG THANH CONG!';
PRINT '=====================================================';
PRINT '';
PRINT 'Script da ket thuc thanh cong!';
PRINT 'Transaction da duoc commit.';
PRINT '';

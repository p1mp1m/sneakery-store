-- =====================================================
-- SNEAKERY E-COMMERCE - CREATE DATABASE
-- =====================================================
-- File này tạo database Sneakery
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


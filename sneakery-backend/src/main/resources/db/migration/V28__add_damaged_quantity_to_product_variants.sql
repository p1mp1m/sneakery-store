-- 1️⃣ Thêm cột damaged_quantity (mặc định = 0, không cho null)
ALTER TABLE Product_Variants
    ADD damaged_quantity INT NOT NULL DEFAULT 0;
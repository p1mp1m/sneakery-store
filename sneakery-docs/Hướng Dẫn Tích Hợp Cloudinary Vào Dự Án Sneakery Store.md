📘 Hướng Dẫn Tích Hợp Cloudinary Vào Dự Án Sneakery Store
1️⃣ Đăng ký tài khoản Cloudinary
Bước 1: Truy cập trang chủ

👉 https://cloudinary.com/

Bước 2: Đăng ký tài khoản

Chọn Sign Up

Bạn có thể đăng ký bằng:

Email

Google

GitHub

Bước 3: Chọn gói miễn phí

Cloudinary có gói Free chứa:

25GB transform

25 credits

5GB lưu trữ

Đủ cho toàn bộ dự án Sneakery.

2️⃣ Lấy thông tin API (Cloud Name – API Key – API Secret)

Sau khi đăng nhập:

Vào Dashboard

Tìm mục API Keys

Bạn sẽ thấy:

Key	Ví dụ
Cloud Name	dfasd123
API Key	123456789012345
API Secret	s7Y8qC2AbCdEfGhIjKlMnOpQr

👉 Lưu lại 3 thông số này để cấu hình trong Spring Boot.

3️⃣ Thêm Cloudinary vào Spring Boot
✔ Bước 1: Thêm dependency Maven
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.33.0</version>
</dependency>

thêm vào application.properties
cloudinary.cloud_name=ten-cloud
cloudinary.api_key=api-key
cloudinary.api_secret=api-secret
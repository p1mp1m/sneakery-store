# 👟 Sneakery Store - Website Bán Giày Online

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL_Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)

**Website bán giày sneaker hiện đại - Dễ sử dụng, Dễ quản lý**

</div>

---

## 📖 Giới thiệu

**Sneakery Store** là website bán giày trực tuyến với 2 phần chính:

- 👥 **Khách hàng:** Xem giày, thêm vào giỏ hàng, đặt mua, quản lý tài khoản
- 🛡️ **Quản trị:** Quản lý sản phẩm, đơn hàng, thống kê doanh thu, quản lý người dùng

---

## 🛠️ Yêu cầu hệ thống

Cài đặt các phần mềm sau **TRƯỚC KHI BẮT ĐẦU**:

| Phần mềm | Link tải |
|----------|----------|
| **Java JDK 17** | [👉 Tải tại đây](https://www.oracle.com/java/technologies/downloads/#java17) |
| **Node.js 18+** | [👉 Tải tại đây](https://nodejs.org/) |
| **SQL Server** | [👉 Tải tại đây](https://www.microsoft.com/sql-server/sql-server-downloads) |
| **SQL Server Management Studio (SSMS)** | [👉 Tải tại đây](https://learn.microsoft.com/sql/ssms/download-sql-server-management-studio-ssms) |

> **💡 Lưu ý:** Sau khi cài đặt, **khởi động lại máy tính** để các cài đặt có hiệu lực!

---

## 🚀 Hướng dẫn cài đặt (5 bước)

### 📥 **Bước 1: Tải code về máy**

```bash
git clone https://github.com/p1mp1m/sneakery-store
cd sneakery-store
```

Hoặc tải file ZIP từ GitHub và giải nén.

---

### 🗄️ **Bước 2: Tạo Database**

1. Mở **SQL Server Management Studio (SSMS)**
2. Kết nối với server: `localhost` (Authentication: Windows Authentication)
3. Mở và chạy file: `sneakery-database/1_CREATE_SCHEMA.sql` (nhấn F5)
4. Mở và chạy file: `sneakery-database/2_INSERT_DATA.sql` (nhấn F5)

✅ **Hoàn thành:** Database `sneakery_db` đã được tạo!

---

### ⚙️ **Bước 3: Cấu hình Backend**

1. **Copy file cấu hình:**
```bash
# Windows (PowerShell)
Copy-Item sneakery-backend\src\main\resources\application.properties.example sneakery-backend\src\main\resources\application.properties
```

2. **Mở file:** `sneakery-backend/src/main/resources/application.properties`

3. **Sửa 2 dòng sau:**
```properties
spring.datasource.password=YOUR_PASSWORD_HERE
app.jwt.secret=YOUR_JWT_SECRET_KEY_HERE
```
- Thay `YOUR_PASSWORD_HERE` bằng mật khẩu SQL Server của bạn
- Tạo JWT secret mới (dùng UUID generator online)

4. **Cài đặt dependencies:**
```bash
cd sneakery-backend
mvn clean install
```

⏳ Chờ 2-5 phút để tải các thư viện...

---

### 🎨 **Bước 4: Cài đặt Frontend**

```bash
cd sneakery-frontend
npm install
```

⏳ Chờ 2-5 phút để tải các thư viện...

---

### 🎮 **Bước 5: Chạy ứng dụng**

**Cần mở 2 cửa sổ Terminal:**

#### Terminal 1 - Chạy Backend:
```bash
cd sneakery-backend
mvn spring-boot:run
```

✅ Khi thấy: `Started SneakeryApplication in X.XXX seconds` → Backend đã sẵn sàng!

#### Terminal 2 - Chạy Frontend:
```bash
cd sneakery-frontend
npm run dev
```

✅ Khi thấy: `Local: http://localhost:5173/` → Mở trình duyệt và truy cập!

---

## 🔑 Đăng nhập

Mở trình duyệt và vào: **http://localhost:5173**

### 👨‍💼 Tài khoản Admin:
```
Email:    admin@sneakery.com
Mật khẩu: password
```

### 👤 Tài khoản Khách hàng:
```
Email:    user1@example.com
Mật khẩu: password
```

---

## ❓ Sửa lỗi thường gặp

### ❌ Backend không chạy được
- ✅ Kiểm tra SQL Server đã mở chưa
- ✅ Kiểm tra password trong `application.properties`
- ✅ Kiểm tra đã chạy file SQL tạo database chưa

### ❌ Port 8080 đã được sử dụng
- Thêm vào `application.properties`: `server.port=8081`

### ❌ Frontend không kết nối được Backend
- ✅ Chạy Backend trước (phải thấy "Started")
- ✅ Kiểm tra http://localhost:8080/api hoạt động
- ✅ Xóa cache browser và F5 lại

### ❌ Lỗi npm install
```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### ❌ Lỗi mvn install
```bash
mvn clean
mvn install
```

---

## 📊 Kiểm tra hệ thống

- 🌐 **Frontend:** http://localhost:5173
- 🔧 **Backend API:** http://localhost:8080/api
- 📖 **API Docs:** http://localhost:8080/swagger-ui.html

---

## 📚 Tài liệu cho Developers

Nếu bạn là developer trong team, xem thêm:

> ⚠️ **Quy tắc:** KHÔNG BAO GIỜ commit trực tiếp vào `main`. Luôn tạo branch mới!

---

## 💬 Cần hỗ trợ?

- 📖 Đọc lại hướng dẫn từ đầu
- 🔍 Kiểm tra phần "Sửa lỗi thường gặp"
- 📧 Email: pombie789456123@gmail.com
- 🐛 [Báo bug trên GitHub](https://github.com/p1mp1m/sneakery-store/issues)

---

<div align="center">

**✨ Chúc bạn setup thành công! ✨**

Made with ❤️ by Sneakery Team

![GitHub stars](https://img.shields.io/github/stars/p1mp1m/sneakery-store?style=social)
![GitHub forks](https://img.shields.io/github/forks/p1mp1m/sneakery-store?style=social)

</div>

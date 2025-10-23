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

**Sneakery Store** là website bán giày trực tuyến, gồm 2 phần:
- 👥 **Khách hàng:** Xem giày, thêm vào giỏ hàng, đặt mua
- 🛡️ **Quản trị:** Quản lý sản phẩm, đơn hàng, thống kê doanh thu

---

## ✨ Tính năng chính

### Dành cho Khách hàng
- 🔍 Tìm kiếm và xem sản phẩm
- 🛒 Giỏ hàng và đặt hàng
- ⭐ Đánh giá sản phẩm
- 👤 Quản lý tài khoản

### Dành cho Admin
- 📊 Xem thống kê, biểu đồ
- 🏷️ Quản lý sản phẩm
- 📦 Quản lý đơn hàng
- 👥 Quản lý người dùng

---

## 🛠️ Cần cài gì trước?

Trước khi bắt đầu, cài đặt những phần mềm sau:

| Phần mềm | Link tải |
|----------|----------|
| ☕ **Java JDK 17** | [Tải Java](https://www.oracle.com/java/technologies/downloads/#java17) |
| 🟢 **Node.js 18+** | [Tải Node.js](https://nodejs.org/) |
| 🗄️ **SQL Server** | [Tải SQL Server](https://www.microsoft.com/sql-server/sql-server-downloads) |
| 🔧 **SQL Server Management Studio** | [Tải SSMS](https://learn.microsoft.com/sql/ssms/download-sql-server-management-studio-ssms) |
| 💻 **Visual Studio Code** | [Tải VS Code](https://code.visualstudio.com/) |

> **💡 Mẹo:** Sau khi cài xong, khởi động lại máy tính!

---

## 🚀 Hướng dẫn cài đặt (4 bước đơn giản)

### 📥 **Bước 1: Tải code về máy**

**Cách 1: Dùng Git**
```bash
git clone https://github.com/yourusername/sneakery-store.git
cd sneakery-store
```

**Cách 2: Tải ZIP**
- Vào GitHub → Click nút **Code** → Chọn **Download ZIP**
- Giải nén file ZIP ra một thư mục

---

### 🗄️ **Bước 2: Tạo Database (Cơ sở dữ liệu)**

#### 2.1. Mở SQL Server Management Studio

1. Mở **SQL Server Management Studio (SSMS)**
2. Kết nối với:
   - **Server name:** `localhost` hoặc `(local)`
   - **Authentication:** Windows Authentication
   - Click **Connect**

#### 2.2. Chạy Script tạo Database

1. Click **File** → **Open** → **File...**
2. Chọn 2 file theo thứ tự:
   - `sneakery-database/1_CREATE_SCHEMA.sql` 
   - `sneakery-database/2_INSERT_DATA.sql`
3. Click **Execute** (hoặc nhấn F5)

✅ **Hoàn thành:** Database `sneakery_db` đã được tạo!

---

### ⚙️ **Bước 3: Cấu hình Backend (Phần server)**

#### 3.1. Mở file cấu hình

Mở file: `sneakery-backend/src/main/resources/application.properties`

#### 3.2. Sửa password Database

Tìm dòng có chữ `spring.datasource.password` và sửa:

```properties
spring.datasource.password=123456
```

> **⚠️ Chú ý:** Đổi `123456` thành password SQL Server của bạn!

#### 3.3. Cài đặt Backend

Mở **Command Prompt** hoặc **Terminal** tại thư mục dự án:

```bash
cd sneakery-backend
mvn clean install
```

⏳ **Chờ 2-3 phút** để tải các thư viện cần thiết...

---

### 🎨 **Bước 4: Cài đặt Frontend (Phần giao diện)**

Mở **Command Prompt** hoặc **Terminal** mới:

```bash
cd sneakery-frontend
npm install
```

⏳ **Chờ 2-3 phút** để tải các thư viện...

---

## 🎮 Chạy ứng dụng

### 🟢 **Bước 1: Chạy Backend (Phần server)**

Mở **Terminal** thứ nhất:

```bash
cd sneakery-backend
mvn spring-boot:run
```

✅ Thấy dòng `Started SneakeryApplication` là **thành công**!

> **🌐 Server chạy tại:** http://localhost:8080

---

### 🎨 **Bước 2: Chạy Frontend (Phần giao diện)**

Mở **Terminal** thứ hai:

```bash
cd sneakery-frontend
npm run dev
```

✅ Thấy dòng `Local: http://localhost:5173` là **thành công**!

> **🌐 Website chạy tại:** http://localhost:5173

---

## 🎉 Truy cập Website

Mở trình duyệt và vào:

### 🏠 Trang chính
👉 http://localhost:5173

### 🔑 Đăng nhập sẵn

**Tài khoản Admin:**
```
Email: admin@sneakery.com
Mật khẩu: admin123
```

**Tài khoản Khách hàng:**
```
Email: user@sneakery.com
Mật khẩu: user123
```

---

## 🗂️ Cấu trúc thư mục

```
sneakery-store/
│
├── 📁 sneakery-backend/          ← Code Backend (Java)
│   ├── src/main/java/            ← Các file Java
│   └── src/main/resources/       ← File cấu hình
│
├── 📁 sneakery-frontend/         ← Code Frontend (Vue.js)
│   ├── src/                      ← Các file Vue
│   │   ├── views/                ← Các trang web
│   │   ├── components/           ← Các thành phần nhỏ
│   │   └── services/             ← Gọi API
│   └── package.json              ← Danh sách thư viện
│
└── 📁 sneakery-database/         ← File SQL tạo Database
    ├── 1_CREATE_SCHEMA.sql       ← Tạo bảng
    └── 2_INSERT_DATA.sql         ← Thêm dữ liệu mẫu
```

---

## ❓ Gặp lỗi? Sửa như sau!

### ❌ Lỗi 1: Backend không chạy được

**Lỗi:** `Cannot connect to database`

**Cách sửa:**
```
✅ Kiểm tra SQL Server đã mở chưa
✅ Kiểm tra password trong file application.properties
✅ Kiểm tra đã chạy file SQL tạo database chưa
```

---

### ❌ Lỗi 2: Port 8080 đã được sử dụng

**Lỗi:** `Port 8080 is already in use`

**Cách sửa:**
- Mở `application.properties`
- Thêm dòng: `server.port=8081`

---

### ❌ Lỗi 3: Frontend không kết nối được Backend

**Lỗi:** `Network Error` hoặc `CORS Error`

**Cách sửa:**
```
✅ Kiểm tra Backend đã chạy chưa (phải chạy trước)
✅ Vào http://localhost:8080/api - Phải thấy trang JSON
✅ Xóa cache browser và F5 lại trang
```

---

### ❌ Lỗi 4: Lỗi khi npm install

**Cách sửa:**

```bash
# Xóa thư mục cũ
rm -rf node_modules package-lock.json

# Cài lại
npm cache clean --force
npm install
```

---

### ❌ Lỗi 5: Lỗi khi mvn install

**Cách sửa:**

```bash
# Xóa cache Maven
mvn clean

# Cài lại
mvn install
```

---

## 📌 Các lưu ý quan trọng

### ⚡ Thứ tự chạy

1. ✅ Chạy **Backend** trước
2. ✅ Chờ Backend chạy xong (thấy chữ "Started")
3. ✅ Mới chạy **Frontend**

### 🔒 Bảo mật

- ⚠️ **KHÔNG** để password SQL trong code khi đưa lên mạng
- ⚠️ Đổi JWT secret trước khi deploy thật
- ⚠️ Chỉ cho phép CORS từ domain tin cậy

### 📊 Kiểm tra hệ thống

- Backend API: http://localhost:8080/api
- Swagger (API Doc): http://localhost:8080/swagger-ui.html
- Frontend: http://localhost:5173

---

## 🎯 Checklist hoàn thành

Đánh dấu ✅ khi hoàn thành:

- [ ] Đã cài đặt Java JDK 17
- [ ] Đã cài đặt Node.js 18+
- [ ] Đã cài đặt SQL Server + SSMS
- [ ] Đã tải code về máy
- [ ] Đã chạy file SQL tạo database
- [ ] Đã sửa password trong application.properties
- [ ] Đã chạy `mvn clean install` thành công
- [ ] Đã chạy `npm install` thành công
- [ ] Backend chạy được (http://localhost:8080)
- [ ] Frontend chạy được (http://localhost:5173)
- [ ] Đăng nhập thành công với tài khoản admin

---

## 💬 Cần hỗ trợ?

Nếu gặp khó khăn, hãy:

1. 📖 Đọc lại hướng dẫn từ đầu
2. 🔍 Kiểm tra phần "Gặp lỗi? Sửa như sau!"
3. 💻 Kiểm tra log lỗi trong Terminal
4. 📧 Liên hệ: support@sneakery.com

---

<div align="center">

**✨ Chúc bạn cài đặt thành công! ✨**

Made with ❤️ by Sneakery Team

</div>
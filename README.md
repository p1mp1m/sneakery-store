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

**Sneakery Store** là website bán giày trực tuyến được xây dựng với kiến trúc hiện đại, gồm 2 phần:

- 👥 **Khách hàng:** Xem giày, thêm vào giỏ hàng, đặt mua, quản lý tài khoản
- 🛡️ **Quản trị:** Quản lý sản phẩm, đơn hàng, thống kê doanh thu, quản lý người dùng

---

## 🛠️ Technology Stack

### Backend
- **Java 17** - Ngôn ngữ lập trình
- **Spring Boot 3.2.0** - Framework chính
- **Spring Security** - Xác thực và phân quyền
- **Spring Data JPA** - ORM cho database
- **SQL Server** - Hệ quản trị cơ sở dữ liệu
- **Maven** - Quản lý dependencies

### Frontend
- **Vue.js 3.5** - Framework JavaScript
- **Vue Router** - Routing
- **Pinia/Vuex** - State Management
- **Axios** - HTTP Client
- **Vite** - Build tool
- **Tailwind CSS / Custom CSS** - Styling

### Development Tools
- **Git & GitHub** - Version control
- **Visual Studio Code / IntelliJ IDEA** - IDE

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

## 🛠️ Chuẩn bị trước khi cài đặt

### 📋 Danh sách phần mềm cần cài:

Trước khi bắt đầu, bạn cần cài đặt các phần mềm sau vào máy tính:

| 🔢 | Phần mềm | Mục đích | Link tải |
|----|----------|----------|----------|
| 1️⃣ | **Java JDK 17** | Chạy phần Backend | [👉 Tải tại đây](https://www.oracle.com/java/technologies/downloads/#java17) |
| 2️⃣ | **Node.js 18+** | Chạy phần Frontend | [👉 Tải tại đây](https://nodejs.org/) |
| 3️⃣ | **SQL Server** | Lưu trữ dữ liệu | [👉 Tải tại đây](https://www.microsoft.com/sql-server/sql-server-downloads) |
| 4️⃣ | **SQL Server Management Studio (SSMS)** | Quản lý database | [👉 Tải tại đây](https://learn.microsoft.com/sql/ssms/download-sql-server-management-studio-ssms) |
| 5️⃣ | **Visual Studio Code** | Soạn thảo code (tùy chọn) | [👉 Tải tại đây](https://code.visualstudio.com/) |

> **💡 Lưu ý:** Sau khi cài xong tất cả phần mềm, hãy **khởi động lại máy tính** để các cài đặt có hiệu lực!

---

## 🚀 Hướng dẫn cài đặt (4 bước đơn giản)

### 📥 **Bước 1: Tải code về máy**

Bạn có thể tải code về máy bằng 1 trong 2 cách sau:

#### 🌐 Cách 1: Dùng Git (khuyến nghị)
Mở **Command Prompt** hoặc **Terminal** và chạy lệnh:
```bash
git clone https://github.com/p1mp1m/sneakery-store
cd sneakery-store
```

#### 📦 Cách 2: Tải file ZIP
1. Vào GitHub repository
2. Click nút **Code** (màu xanh)
3. Chọn **Download ZIP**
4. Giải nén file ZIP ra một thư mục trên máy tính

---

### 🗄️ **Bước 2: Tạo Database (Cơ sở dữ liệu)**

#### 2.1. Khởi động SQL Server Management Studio

1. Mở **SQL Server Management Studio (SSMS)** từ Start Menu
2. Trong cửa sổ **Connect to Server**, nhập:
   - **Server name:** `localhost` hoặc `(local)`
   - **Authentication:** Windows Authentication
3. Click nút **Connect**

> ✅ **Nếu kết nối thành công:** Sẽ thấy cửa sổ Object Explorer bên trái

#### 2.2. Chạy script tạo database

1. Trong SSMS, click **File** → **Open** → **File...** (hoặc nhấn `Ctrl+O`)
2. Tìm và chọn file: `sneakery-database/1_CREATE_SCHEMA.sql`
3. Click nút **Execute** (hoặc nhấn `F5`)
4. Đợi thấy dòng "Command(s) completed successfully" là thành công
5. Làm tương tự với file: `sneakery-database/2_INSERT_DATA.sql`

✅ **Hoàn thành:** Database `sneakery_db` đã được tạo với dữ liệu mẫu!

---

### ⚙️ **Bước 3: Cấu hình và cài đặt Backend**

#### 3.1. Tạo file cấu hình

1. **Copy file example thành file chính:**
```bash
# Trên Windows (Command Prompt)
copy sneakery-backend\src\main\resources\application.properties.example sneakery-backend\src\main\resources\application.properties

# Trên Windows (PowerShell)
Copy-Item sneakery-backend\src\main\resources\application.properties.example sneakery-backend\src\main\resources\application.properties

# Trên Linux/Mac
cp sneakery-backend/src/main/resources/application.properties.example sneakery-backend/src/main/resources/application.properties
```

2. **Mở file:** `sneakery-backend/src/main/resources/application.properties`

3. Điền thông tin kết nối database của bạn:
```properties
spring.datasource.password=YOUR_PASSWORD_HERE
app.jwt.secret=YOUR_JWT_SECRET_KEY_HERE
```

> **⚠️ Quan trọng:** 
> - Thay `YOUR_PASSWORD_HERE` bằng mật khẩu SQL Server của bạn
> - Tạo JWT secret mới bằng online generator hoặc UUID (KHÔNG dùng secret mặc định!)

#### 3.2. Cài đặt các thư viện cần thiết

Mở **Command Prompt** hoặc **Terminal**, di chuyển vào thư mục dự án và chạy:

```bash
cd sneakery-backend
mvn clean install
```

⏳ **Chờ 2-5 phút** để tải và cài đặt các thư viện cần thiết...

> ✅ **Thành công:** Sẽ thấy dòng "BUILD SUCCESS" ở cuối

---

### 🎨 **Bước 4: Cài đặt Frontend**

Mở **Command Prompt** hoặc **Terminal** mới (không cần đợi Backend), chạy:

```bash
cd sneakery-frontend
npm install
```

⏳ **Chờ 2-5 phút** để tải và cài đặt các thư viện...

> ✅ **Thành công:** Sẽ thấy thông báo "added XXX packages" ở cuối

---

## 🎮 Hướng dẫn chạy ứng dụng

> **📌 Lưu ý:** Bạn cần mở **2 cửa sổ Terminal** để chạy cả Backend và Frontend

---

### 🟢 **Bước 1: Chạy Backend trước**

Mở **Terminal** (hoặc Command Prompt) thứ nhất:

```bash
cd sneakery-backend
mvn spring-boot:run
```

**Chờ vài giây...**

✅ **Khi thấy dòng này là thành công:** 
```
Started SneakeryApplication in X.XXX seconds
```

> **🌐 Backend đã chạy tại:** http://localhost:8080

---

### 🎨 **Bước 2: Chạy Frontend** (Mở Terminal thứ hai)

Mở **Terminal** (hoặc Command Prompt) thứ hai:

```bash
cd sneakery-frontend
npm run dev
```

**Chờ vài giây...**

✅ **Khi thấy dòng này là thành công:**
```
  VITE ready in XXX ms
  ➜  Local:   http://localhost:5173/
```

> **🌐 Website đã sẵn sàng tại:** http://localhost:5173

---

## 📚 Tài liệu cho Team Developers

### Tài liệu quan trọng

Nếu bạn là developer trong team, đây là các tài liệu bạn **PHẢI ĐỌC**:

| 📖 | Tài liệu | Mô tả | Link |
|---|---------|-------|------|
| 🔄 | **Quy trình Git** | Hướng dẫn chi tiết workflow cho team | [👉 Xem](./sneakery-docs/Hướng%20dẫn%20push%20code.md) |
| 🎓 | **Lệnh Git & Tips** | Tổng hợp lệnh Git và mẹo hữu ích | [👉 Xem](./sneakery-docs/Lệnh%20Github.md) |
| 📋 | **Phân tích nghiệp vụ** | Chi tiết các chức năng của hệ thống | [👉 Xem](./sneakery-docs/Phân%20tích%20nghiệp%20vụ%20và%20chức%20năng.md) |
| 🗄️ | **ERD & Luồng nghiệp vụ** | Sơ đồ database và luồng xử lý | [👉 Xem](./sneakery-docs/ERD%20và%20luồng%20nghiệp%20vụ.md) |

### 👥 Quy trình làm việc cho Developer

1. **📥 Setup môi trường:**
   ```bash
   git clone https://github.com/p1mp1m/sneakery-store
   cd sneakery-store
   # Copy application.properties như hướng dẫn trên
   ```

2. **🔄 Workflow làm việc:**
   ```bash
   # Lấy code mới nhất
   git checkout main
   git pull origin main
   
   # Tạo branch mới cho task
   git checkout -b feature/tên-tính-năng
   
   # Code và commit
   git add .
   git commit -m "feat: mô tả thay đổi"
   
   # Push lên GitHub
   git push -u origin feature/tên-tính-năng
   ```

3. **✅ Tạo Pull Request:** Vào GitHub để tạo PR và đợi review

4. **📖 Đọc thêm:** Xem chi tiết tại [Workflow](./sneakery-docs/Hướng%20dẫn%20push%20code.md)

### ⚠️ Quy tắc bắt buộc

- ❌ **KHÔNG BAO GIỜ** commit trực tiếp vào `main`
- ✅ Luôn tạo branch mới cho mỗi task
- ✅ Luôn pull code mới nhất trước khi bắt đầu
- ✅ Test code trước khi push
- ✅ Không commit file nhạy cảm (password, secrets)

---

## 🎉 Truy cập website

### 🏠 Bước 1: Mở trình duyệt

1. Mở trình duyệt bất kỳ (Chrome, Edge, Firefox...)
2. Nhập địa chỉ: **http://localhost:5173**
3. Nhấn Enter

> **Lưu ý:** Đảm bảo cả Backend và Frontend đều đang chạy!

---

### 🔑 Bước 2: Đăng nhập vào hệ thống

Website có sẵn 2 tài khoản để bạn thử nghiệm:

#### 👨‍💼 Tài khoản Admin (Quản trị viên):
```
📧 Email:    admin@sneakery.com
🔒 Mật khẩu: password
```
**Quyền:** Xem thống kê, quản lý sản phẩm, đơn hàng, người dùng...

#### 👤 Tài khoản Khách hàng:
```
📧 Email:    user1@example.com
🔒 Mật khẩu: password
```
**Quyền:** Xem sản phẩm, đặt hàng, xem lịch sử mua hàng...

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
4. 📧 Email: pombie789456123@gmail.com
5. 🐛 Báo bug: [Tạo Issue trên GitHub](https://github.com/p1mp1m/sneakery-store/issues)

---

## 👥 Contributors

Dự án được phát triển bởi **Sneakery Team**:

- **Backend Developers** - Xây dựng API và business logic
- **Frontend Developers** - Thiết kế và phát triển giao diện
- **Team Leader** - Quản lý dự án và code review

> **Mong muốn đóng góp?** Vui lòng đọc [Hướng dẫn Workflow](./sneakery-docs/Hướng%20dẫn%20push%20code.md)

---

## 📄 License

Dự án này được phát triển cho mục đích học tập và portfolio.

---

## 🔗 Links hữu ích

- 📁 **Repository:** https://github.com/p1mp1m/sneakery-store
- 📖 **Documentation:** [Xem thư mục sneakery-docs](./sneakery-docs)
- 🎯 **Project Board:** [GitHub Projects](https://github.com/p1mp1m/sneakery-store/projects)

---

<div align="center">

**✨ Chúc bạn phát triển dự án thành công! ✨**

Made with ❤️ by Sneakery Team

![GitHub stars](https://img.shields.io/github/stars/p1mp1m/sneakery-store?style=social)
![GitHub forks](https://img.shields.io/github/forks/p1mp1m/sneakery-store?style=social)

</div>

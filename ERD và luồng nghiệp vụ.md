# 👟 Sneakery Store - Hệ thống E-commerce bán giày

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL_Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)

**Một hệ thống quản lý và bán hàng giày sneaker hiện đại, với giao diện thân thiện và tính năng đầy đủ.**

[Tính năng](#-tính-năng) • [Công nghệ](#️-công-nghệ-sử-dụng) • [Cài đặt](#-cài-đặt) • [API Documentation](#-api-documentation)

</div>

---

## 📋 Mục lục

- [Giới thiệu](#-giới-thiệu)
- [Tính năng](#-tính-năng)
- [Công nghệ sử dụng](#️-công-nghệ-sử-dụng)
- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Cài đặt](#-cài-đặt)
  - [1. Clone Repository](#1-clone-repository)
  - [2. Cấu hình Database](#2-cấu-hình-database)
  - [3. Cấu hình Backend](#3-cấu-hình-backend)
  - [4. Cấu hình Frontend](#4-cấu-hình-frontend)
- [Chạy ứng dụng](#-chạy-ứng-dụng)
- [API Documentation](#-api-documentation)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Các lưu ý quan trọng](#-các-lưu-ý-quan-trọng)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Giới thiệu

**Sneakery Store** là một hệ thống e-commerce toàn diện được xây dựng để quản lý và bán giày sneaker trực tuyến. Dự án được phát triển với kiến trúc phân tách rõ ràng giữa Backend (Spring Boot) và Frontend (Vue.js), đảm bảo tính mở rộng và bảo trì dễ dàng.

### 🌟 Điểm nổi bật

- ✨ Giao diện người dùng hiện đại, responsive
- 🔐 Hệ thống xác thực và phân quyền bảo mật với JWT
- 📊 Dashboard quản trị với biểu đồ thống kê
- 🛒 Giỏ hàng và quy trình thanh toán hoàn chỉnh
- 📦 Quản lý đơn hàng và trạng thái vận chuyển
- ⭐ Hệ thống đánh giá sản phẩm
- 🎨 Quản lý thương hiệu, danh mục và biến thể sản phẩm

---

## 🚀 Tính năng

### 👥 Người dùng (Customer)

- 🔍 Tìm kiếm và lọc sản phẩm theo nhiều tiêu chí
- 📱 Xem chi tiết sản phẩm với hình ảnh và mô tả
- 🛍️ Thêm sản phẩm vào giỏ hàng và quản lý đơn hàng
- ⭐ Đánh giá và nhận xét sản phẩm
- 💳 Thanh toán trực tuyến
- 📍 Quản lý địa chỉ giao hàng
- 👤 Quản lý thông tin cá nhân
- 📜 Lịch sử đơn hàng và theo dõi trạng thái

### 🛡️ Quản trị viên (Admin)

- 📊 Dashboard với biểu đồ thống kê doanh thu, đơn hàng
- 🏷️ Quản lý sản phẩm, thương hiệu, danh mục
- 👥 Quản lý người dùng và phân quyền
- 📦 Quản lý đơn hàng và cập nhật trạng thái
- 💰 Quản lý thanh toán và mã giảm giá
- 📈 Báo cáo và phân tích dữ liệu
- ⚙️ Cấu hình hệ thống

---

## 🛠️ Công nghệ sử dụng

### Backend

| Công nghệ | Phiên bản | Mô tả |
|-----------|-----------|-------|
| **Java** | 17 | Ngôn ngữ lập trình chính |
| **Spring Boot** | 3.2.0 | Framework backend |
| **Spring Security** | - | Bảo mật và xác thực |
| **Spring Data JPA** | - | ORM và database access |
| **JWT** | 0.11.5 | Token-based authentication |
| **SQL Server** | - | Database |
| **Maven** | - | Dependency management |
| **Lombok** | - | Giảm boilerplate code |
| **ModelMapper** | 3.1.1 | DTO mapping |
| **Swagger/OpenAPI** | 2.2.0 | API documentation |

### Frontend

| Công nghệ | Phiên bản | Mô tả |
|-----------|-----------|-------|
| **Vue.js** | 3.5.22 | Progressive JavaScript framework |
| **Vue Router** | 4.6.3 | Client-side routing |
| **Pinia** | 3.0.3 | State management |
| **Vite** | 7.1.7 | Build tool và dev server |
| **Element Plus** | 2.11.4 | UI Component library |
| **Axios** | 1.12.2 | HTTP client |
| **Chart.js** | 4.5.1 | Data visualization |
| **vue-chartjs** | 5.3.2 | Vue wrapper cho Chart.js |
| **XLSX** | 0.18.5 | Export Excel |

---

## 💻 Yêu cầu hệ thống

Trước khi bắt đầu cài đặt, đảm bảo máy tính của bạn đã cài đặt:

- ☕ **Java Development Kit (JDK)** 17 hoặc cao hơn
- 📦 **Apache Maven** 3.6+ hoặc sử dụng Maven wrapper có sẵn
- 🟢 **Node.js** 18.x hoặc cao hơn
- 📦 **npm** hoặc **yarn** (đi kèm với Node.js)
- 🗄️ **Microsoft SQL Server** 2019 hoặc cao hơn
- 🔧 **SQL Server Management Studio (SSMS)** (khuyến nghị)
- 💻 IDE: **IntelliJ IDEA** / **Eclipse** (cho Java) và **VS Code** (cho Vue.js)

---

## 📥 Cài đặt

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/sneakery-store.git
cd sneakery-store
```

### 2. Cấu hình Database

#### Bước 2.1: Tạo Database

1. Mở **SQL Server Management Studio (SSMS)**
2. Kết nối đến SQL Server instance của bạn
3. Mở file SQL script:
   ```
   sneakery-database/Sneakery_DB_V2_Enhanced.sql
   ```
4. Chạy script để tạo database và tables
5. Database `sneakery_db` sẽ được tạo với dữ liệu mẫu

#### Bước 2.2: Cấu hình Connection String

Kiểm tra thông tin kết nối SQL Server của bạn:
- **Server:** localhost (hoặc địa chỉ server của bạn)
- **Port:** 1433 (mặc định)
- **Database:** sneakery_db
- **Username:** sa (hoặc tài khoản của bạn)
- **Password:** Cập nhật trong bước tiếp theo

### 3. Cấu hình Backend

#### Bước 3.1: Cấu hình application.properties

Mở file `sneakery-backend/src/main/resources/application.properties` và cập nhật thông tin database:

```properties
# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=sneakery_db;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD_HERE  # Thay đổi password của bạn
```

#### Bước 3.2: Cấu hình JWT Secret (Tùy chọn)

Nếu muốn thay đổi JWT secret key, cập nhật:

```properties
app.jwt.secret=YOUR_BASE64_ENCODED_SECRET_KEY
app.jwt.expiration-ms=86400000  # 24 giờ
```

#### Bước 3.3: Cấu hình CORS

Nếu frontend chạy trên port khác, thêm vào:

```properties
cors.allowed-origins=http://localhost:5173,http://localhost:3000
```

#### Bước 3.4: Build Backend

```bash
cd sneakery-backend

# Sử dụng Maven
mvn clean install

# Hoặc sử dụng Maven Wrapper (Windows)
.\mvnw.cmd clean install

# Maven Wrapper (Linux/Mac)
./mvnw clean install
```

### 4. Cấu hình Frontend

#### Bước 4.1: Cài đặt Dependencies

```bash
cd sneakery-frontend
npm install
```

#### Bước 4.2: Cấu hình API Endpoint (Nếu cần)

Nếu backend chạy trên port hoặc địa chỉ khác, cập nhật trong `src/services/`:

```javascript
// src/services/authService.js, adminService.js, productService.js
const API_URL = 'http://localhost:8080/api';  // Đổi nếu cần
```

---

## 🎮 Chạy ứng dụng

### Khởi động Backend

```bash
cd sneakery-backend

# Cách 1: Sử dụng Maven
mvn spring-boot:run

# Cách 2: Sử dụng Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Cách 3: Chạy file JAR
java -jar target/sneakery-backend-1.0.0.jar
```

Backend sẽ chạy tại: **http://localhost:8080**

### Khởi động Frontend

Mở terminal mới:

```bash
cd sneakery-frontend

# Development mode
npm run dev

# Build production
npm run build

# Preview production build
npm run preview
```

Frontend sẽ chạy tại: **http://localhost:5173**

### 🎉 Truy cập ứng dụng

- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080/api
- **Swagger UI:** http://localhost:8080/swagger-ui.html

### 🔑 Tài khoản mặc định

Sau khi chạy script database, bạn có thể đăng nhập với:

**Admin:**
```
Email: admin@sneakery.com
Password: admin123
```

**Customer:**
```
Email: user@sneakery.com
Password: user123
```

---

## 📚 API Documentation

### Swagger/OpenAPI

Truy cập API documentation tại: **http://localhost:8080/swagger-ui.html**

### Các API Endpoints chính

#### Authentication
```
POST   /api/auth/register          - Đăng ký tài khoản
POST   /api/auth/login             - Đăng nhập
POST   /api/auth/refresh-token     - Làm mới token
GET    /api/auth/me                - Lấy thông tin user hiện tại
```

#### Products
```
GET    /api/products               - Lấy danh sách sản phẩm
GET    /api/products/{id}          - Lấy chi tiết sản phẩm
GET    /api/products/search        - Tìm kiếm sản phẩm
GET    /api/products/filter        - Lọc sản phẩm
```

#### Cart
```
GET    /api/cart                   - Xem giỏ hàng
POST   /api/cart/add               - Thêm sản phẩm vào giỏ
PUT    /api/cart/update/{id}       - Cập nhật số lượng
DELETE /api/cart/remove/{id}       - Xóa sản phẩm khỏi giỏ
```

#### Orders
```
POST   /api/orders                 - Tạo đơn hàng
GET    /api/orders                 - Lấy danh sách đơn hàng
GET    /api/orders/{id}            - Chi tiết đơn hàng
PUT    /api/orders/{id}/cancel     - Hủy đơn hàng
```

#### Admin APIs
```
GET    /api/admin/users            - Quản lý người dùng
POST   /api/admin/products         - Tạo sản phẩm mới
PUT    /api/admin/products/{id}    - Cập nhật sản phẩm
DELETE /api/admin/products/{id}    - Xóa sản phẩm
PUT    /api/admin/orders/{id}      - Cập nhật trạng thái đơn hàng
GET    /api/admin/analytics        - Thống kê, báo cáo
```

---

## 📁 Cấu trúc dự án

```
sneakery-store/
│
├── sneakery-backend/              # Spring Boot Backend
│   ├── src/main/java/com/sneakery/store/
│   │   ├── config/                # Cấu hình (Security, CORS, etc.)
│   │   ├── controller/            # REST Controllers
│   │   ├── dto/                   # Data Transfer Objects
│   │   ├── entity/                # JPA Entities
│   │   ├── repository/            # JPA Repositories
│   │   ├── service/               # Business Logic
│   │   ├── security/              # JWT, Authentication
│   │   ├── exception/             # Exception Handlers
│   │   └── util/                  # Utility Classes
│   ├── src/main/resources/
│   │   └── application.properties # Cấu hình ứng dụng
│   └── pom.xml                    # Maven dependencies
│
├── sneakery-frontend/             # Vue.js Frontend
│   ├── src/
│   │   ├── assets/                # Images, Styles
│   │   ├── components/            # Vue Components
│   │   │   ├── charts/            # Chart components
│   │   │   ├── common/            # Shared components
│   │   │   └── products/          # Product components
│   │   ├── views/                 # Page Views
│   │   │   ├── admin/             # Admin pages
│   │   │   ├── user/              # User pages
│   │   │   └── common/            # Shared pages
│   │   ├── routers/               # Vue Router config
│   │   ├── services/              # API Services
│   │   ├── stores/                # Pinia Stores
│   │   ├── layouts/               # Layout components
│   │   ├── App.vue                # Root component
│   │   └── main.js                # Entry point
│   ├── package.json               # NPM dependencies
│   └── vite.config.js             # Vite configuration
│
└── sneakery-database/             # Database Scripts
    ├── Sneakery_DB_V2_Enhanced.sql # Database schema + data
    └── Sneakery_DB_Template.sql    # Template schema
```

---

## ⚠️ Các lưu ý quan trọng

### Security

1. **Đổi JWT Secret:** Trong môi trường production, nhớ thay đổi `app.jwt.secret` thành một giá trị mạnh và bảo mật
2. **Database Password:** Không commit password thật vào Git, sử dụng environment variables
3. **CORS Configuration:** Chỉ cho phép origins đáng tin cậy trong production

### Performance

1. **Database Indexes:** Database đã được tối ưu với các indexes cần thiết
2. **Caching:** Cân nhắc thêm Redis cho caching trong production
3. **Connection Pool:** Điều chỉnh connection pool size phù hợp với traffic

### Development

1. **Hot Reload:** Frontend hỗ trợ hot reload khi phát triển
2. **DevTools:** Backend có Spring Boot DevTools cho auto-restart
3. **Logging:** Kiểm tra logs trong console để debug

---

## 🔧 Troubleshooting

### Backend không khởi động được

**Lỗi:** `Cannot connect to database`
```
✅ Kiểm tra SQL Server đã được khởi động
✅ Kiểm tra username/password trong application.properties
✅ Kiểm tra database đã được tạo chưa
✅ Kiểm tra firewall có block port 1433 không
```

**Lỗi:** `Port 8080 already in use`
```
✅ Đổi port trong application.properties: server.port=8081
✅ Hoặc kill process đang dùng port 8080
```

### Frontend không kết nối được Backend

**Lỗi:** `Network Error / CORS Error`
```
✅ Kiểm tra backend đã chạy chưa
✅ Kiểm tra CORS configuration trong application.properties
✅ Kiểm tra API_URL trong frontend services
```

### Lỗi khi build

**Maven build failed:**
```bash
# Clear Maven cache
mvn clean
mvn dependency:purge-local-repository
mvn install
```

**NPM install failed:**
```bash
# Clear cache và reinstall
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

### Lỗi JWT Token

**Token expired hoặc invalid:**
```
✅ Xóa localStorage trong browser và đăng nhập lại
✅ Kiểm tra system time đồng bộ
✅ Kiểm tra JWT secret giống nhau giữa các lần khởi động
```

---

## 🤝 Contributing

Chúng tôi luôn hoan nghênh mọi đóng góp! Để contribute:

1. Fork repository
2. Tạo branch mới: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add some AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Tạo Pull Request

### Coding Standards

- **Backend:** Tuân thủ Java Code Conventions và Spring Boot best practices
- **Frontend:** Sử dụng Vue.js Style Guide
- **Commits:** Sử dụng Conventional Commits format
- **Testing:** Viết tests cho các tính năng mới

---

## 📝 License

Dự án này được phân phối dưới giấy phép MIT License. Xem file `LICENSE` để biết thêm chi tiết.

---

## 📞 Liên hệ & Hỗ trợ

- **Email:** support@sneakery.com
- **Website:** https://sneakery.com
- **Issues:** https://github.com/yourusername/sneakery-store/issues

---

## 🙏 Acknowledgments

- Spring Boot Team
- Vue.js Team
- Element Plus Team
- Tất cả contributors và open-source community

---

<div align="center">

**⭐ Nếu project hữu ích, đừng quên cho chúng tôi một star nhé! ⭐**

Made with ❤️ by Sneakery Team

</div>


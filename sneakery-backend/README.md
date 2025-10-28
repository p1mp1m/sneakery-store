# 🚀 Sneakery Backend API

> **Backend API sử dụng Spring Boot 3.2.0 cho dự án Sneakery Store**

---

## 📋 Mục lục

1. [Giới thiệu](#giới-thiệu)
2. [Kiến trúc](#kiến-trúc)
3. [Công nghệ](#công-nghệ)
4. [Cài đặt](#cài-đặt)
5. [Chạy ứng dụng](#chạy-ứng-dụng)
6. [API Endpoints](#api-endpoints)
7. [Cấu hình](#cấu-hình)
8. [Testing](#testing)

---

## 🎯 Giới thiệu

**Sneakery Backend** là RESTful API được xây dựng với Spring Boot, cung cấp các chức năng:

- 🔐 **Authentication & Authorization** - JWT-based authentication
- 📦 **Product Management** - CRUD operations cho sản phẩm
- 🛒 **E-commerce Core** - Cart, Orders, Payments, Checkout
- 👥 **User Management** - Quản lý người dùng
- 📊 **Admin Dashboard** - Analytics và thống kê
- 💳 **Payments** - Tích hợp payment gateway
- ⭐ **Reviews & Ratings** - Đánh giá sản phẩm
- 🎁 **Marketing** - Coupons, Flash Sales, Loyalty Points
- 📦 **Inventory** - Quản lý kho hàng

---

## 🏗️ Kiến trúc

### Architecture Pattern: **MVC (Model-View-Controller)**

```
sneakery-backend/
├── 📁 controller/          ← API Controllers (HTTP layer)
├── 📁 service/             ← Business Logic
├── 📁 repository/          ← Data Access Layer (JPA)
├── 📁 entity/              ← Database Entities
├── 📁 dto/                 ← Data Transfer Objects
├── 📁 security/            ← JWT & Security Config
├── 📁 config/              ← Configuration
├── 📁 exception/           ← Exception Handling
└── 📁 util/                ← Utilities
```

### Flow xử lý Request

```
Client Request
    ↓
Controller (Xử lý HTTP)
    ↓
Service (Business Logic)
    ↓
Repository (Database Access)
    ↓
Database (SQL Server)
```

### Layers

1. **Controller Layer** - Xử lý HTTP requests/responses
2. **Service Layer** - Business logic và validation
3. **Repository Layer** - Database operations
4. **Entity Layer** - Domain models

---

## 🛠️ Công nghệ

### Core Framework
- **Spring Boot 3.2.0** - Framework chính
- **Java 17** - Ngôn ngữ lập trình

### Data & Persistence
- **Spring Data JPA** - ORM framework
- **Hibernate** - JPA implementation
- **SQL Server** - Database
- **JDBC Driver** - SQL Server connector

### Security
- **Spring Security** - Security framework
- **JWT (JSON Web Token)** - Authentication

### Utilities
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

### Development Tools
- **Spring Boot DevTools** - Hot reload
- **Swagger/OpenAPI** - API documentation

---

## ⚙️ Cài đặt

### Yêu cầu hệ thống

- ☕ Java JDK 17+
- 🗄️ SQL Server 2019+
- 📦 Maven 3.6+

### Bước 1: Clone repository

```bash
git clone https://github.com/p1mp1m/sneakery-store
cd sneakery-store/sneakery-backend
```

### Bước 2: Cấu hình Database

1. **Tạo file application.properties:**
```bash
# Copy file example
copy src\main\resources\application.properties.example src\main\resources\application.properties
```

2. **Sửa file application.properties:**
```properties
# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=sneakery_db
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD_HERE

# JWT Secret (tạo mới!)
app.jwt.secret=YOUR_SECRET_KEY_HERE
```

### Bước 3: Tạo Database

Chạy các file SQL trong thư mục `sneakery-database`:

```bash
# 1. Tạo schema
sqlcmd -S localhost -i ../sneakery-database/1_CREATE_SCHEMA.sql

# 2. Insert data mẫu
sqlcmd -S localhost -i ../sneakery-database/2_INSERT_DATA.sql

# 3. Update ảnh thật (quan trọng!)
sqlcmd -S localhost -i ../sneakery-database/4_UPDATE_PRODUCT_IMAGES.sql
```

### Bước 4: Build project

```bash
mvn clean install
```

⏳ **Chờ 2-5 phút** để Maven tải dependencies...

---

## 🎮 Chạy ứng dụng

### Development Mode (Hot Reload)

```bash
mvn spring-boot:run
```

### Production Mode

```bash
mvn clean package
java -jar target/sneakery-backend-1.0.0.jar
```

### ✅ Kiểm tra Backend đã chạy

Truy cập: http://localhost:8080

```
✅ API Health: http://localhost:8080/api
✅ Swagger UI: http://localhost:8080/swagger-ui.html
✅ Actuator: http://localhost:8080/actuator
```

---

## 🔌 API Endpoints

### Authentication

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| POST | `/api/auth/register` | Đăng ký user mới | ❌ |
| POST | `/api/auth/login` | Đăng nhập | ❌ |
| POST | `/api/auth/refresh` | Refresh token | ✅ |

### Admin API

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/api/admin/dashboard/stats` | Dashboard statistics | Admin |
| GET | `/api/admin/products` | Danh sách sản phẩm | Admin |
| POST | `/api/admin/products` | Tạo sản phẩm mới | Admin |
| PUT | `/api/admin/products/{id}` | Cập nhật sản phẩm | Admin |
| DELETE | `/api/admin/products/{id}` | Xóa sản phẩm | Admin |
| GET | `/api/admin/orders` | Danh sách đơn hàng | Admin |
| PUT | `/api/admin/orders/{id}/status` | Cập nhật trạng thái | Admin |
| GET | `/api/admin/users` | Danh sách users | Admin |
| PUT | `/api/admin/users/{id}` | Cập nhật user | Admin |

### User API

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/api/products` | Danh sách sản phẩm | ❌ |
| GET | `/api/products/{id}` | Chi tiết sản phẩm | ❌ |
| GET | `/api/cart` | Lấy giỏ hàng | User |
| POST | `/api/cart/add` | Thêm vào giỏ | User |
| POST | `/api/orders` | Tạo đơn hàng | User |
| GET | `/api/orders/my` | Đơn hàng của tôi | User |
| GET | `/api/wishlist` | Danh sách yêu thích | User |
| POST | `/api/wishlist/{productId}` | Thêm yêu thích | User |

📖 **Xem chi tiết:** Swagger UI tại http://localhost:8080/swagger-ui.html

---

## ⚙️ Cấu hình

### Port

Mặc định: `8080`

Thay đổi trong `application.properties`:
```properties
server.port=8081
```

### CORS

Cấu hình CORS cho frontend:
```properties
cors.allowed-origins=http://localhost:3000,http://localhost:5173
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
```

### JWT Secret

**⚠️ QUAN TRỌNG:** Tạo JWT secret mới trước khi deploy!

Sinh secret key:
```bash
# Dùng UUID
echo $(uuidgen | base64)

# Hoặc dùng Java
String secret = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
```

Cập nhật trong `application.properties`:
```properties
app.jwt.secret=YOUR_SECRET_KEY_HERE
```

---

## 🧪 Testing

### Unit Test

```bash
mvn test
```

### Integration Test

```bash
mvn verify
```

### API Testing với Postman

1. Import collection từ: `src/test/resources/postman`
2. Set environment variables:
   - `baseUrl`: http://localhost:8080
   - `token`: JWT token (sau khi login)

### Test Accounts

```
Admin:
Email: admin@sneakery.com
Password: password

User:
Email: user1@example.com
Password: password
```

---

## 🔧 Development

### Hot Reload

File `application.properties`:
```properties
spring.devtools.restart.enabled=true
```

Chỉ cần save file → Server tự động restart!

### Debug Mode

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Logging

```properties
Feb 27, 2025 10:30 AM
```
別人
```properties
logging.level.com.sneakery=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

---

## 📊 Database Schema

### Main Tables

- `Users` - Người dùng
- `Products` - Sản phẩm
- `Product_Variants` - Biến thể (size, color, price)
- `Orders` - Đơn hàng
- `Order_Details` - Chi tiết đơn hàng
- `Payments` - Thanh toán
- `Carts` - Giỏ hàng
- `Reviews` - Đánh giá

📖 **Xem chi tiết:** `../sneakery-database/1_CREATE_SCHEMA.sql`

---

## 🚨 Troubleshooting

### Lỗi kết nối Database

```
Cannot connect to database
```

**Sửa:**
```bash
# 1. Kiểm tra SQL Server đã chạy chưa
# 2. Kiểm tra password trong application.properties
# 3. Kiểm tra database đã được tạo chưa
```

### Port đã được sử dụng

```
Port 8080 is already in use
```

**Sửa:**
```properties
server.port=8081
```

### Lỗi Maven Build

```
BUILD FAILURE
```

**Sửa:**
```bash
mvn clean
mvn install -U
```

---

## 📝 Coding Standards

### Naming Convention

- **Class**: PascalCase (`UserController`)
- **Method**: camelCase (`getProductById`)
- **Variable**: camelCase (`productId`)
- **Constant**: UPPER_SNAKE_CASE (`MAX_FILE_SIZE`)

### Package Structure

```
com.sneakery.store
├── controller    - API endpoints
├── service       - Business logic
├── repository    - Data access
├── entity        - Database entities
├── dto           - Data transfer objects
└── config        - Configuration
```

### Best Practices

✅ **DO:**
- Validate input trong Controllers
- Handle exceptions properly
- Use DTOs thay vì Entity trực tiếp
- Log errors chi tiết
- Write Javadoc comments

❌ **DON'T:**
- Đặt business logic trong Controllers
- Expose Entities trực tiếp ra API
- Hardcode credentials
- Ignore exception handling
- Commit password/sensitive data

---

## 📚 Tài liệu tham khảo

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)

---

## 📞 Support

Gặp vấn đề? Hãy:
1. Kiểm tra phần Troubleshooting
2. Xem log trong console
3. Liên hệ: pombie789456123@gmail.com

---

<div align="center">

**Made with ❤️ by Sneakery Backend Team**

</div>


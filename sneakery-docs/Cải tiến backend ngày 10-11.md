# 🚀 Hướng Dẫn Các Bước Tiếp Theo - Backend

Sau khi đã hoàn thành việc cập nhật và tối ưu code, đây là các bước bạn cần thực hiện:

## ✅ 1. Kiểm Tra Code Hoạt Động

### 1.1. Cài Đặt Dependencies (Nếu Chưa)
```bash
cd sneakery-backend
mvn clean install
```

### 1.2. Chạy Development Server
```bash
mvn spring-boot:run
```

Hoặc nếu sử dụng IDE:
- **IntelliJ IDEA**: Click Run trên `SneakeryApplication.java`
- **Eclipse**: Right-click `SneakeryApplication.java` → Run As → Spring Boot App
- **VS Code**: Sử dụng Spring Boot Extension Pack

### 1.3. Kiểm Tra Logs
- Kiểm tra console output
- Đảm bảo thấy: `Started SneakeryApplication in X.XXX seconds`
- Kiểm tra không có lỗi nghiêm trọng
- Kiểm tra database connection thành công
- Kiểm tra Swagger UI: http://localhost:8080/swagger-ui.html

## 🔧 2. Cấu Hình Environment Variables

### 2.1. Cấu Hình Application Properties (Development)
Kiểm tra file `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=sneakery_db;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD_HERE

# JWT Configuration
app.jwt.secret=YOUR_JWT_SECRET_KEY_HERE
app.jwt.expiration-ms=86400000

# Server Configuration
server.port=8080

# CORS Configuration
cors.allowed-origins=http://localhost:3000,http://localhost:5173,http://localhost:5174
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
cors.allowed-headers=*

# Cloudinary Configuration (Nếu sử dụng)
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET

# Email Configuration (Nếu sử dụng)
spring.mail.enabled=false
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD
```

### 2.2. Cấu Hình Application Properties (Production)
Tạo file `src/main/resources/application-prod.properties`:

```properties
# Production Database
spring.datasource.url=jdbc:sqlserver://YOUR_PROD_SERVER:1433;databaseName=sneakery_db;encrypt=true;trustServerCertificate=false
spring.datasource.username=YOUR_PROD_USERNAME
spring.datasource.password=YOUR_PROD_PASSWORD

# Production JWT Secret (Phải khác với development)
app.jwt.secret=YOUR_PRODUCTION_JWT_SECRET

# Production Server
server.port=8080

# Production CORS
cors.allowed-origins=https://your-frontend-domain.com

# Production Logging
logging.level.com.sneakery=WARN
logging.level.org.springframework.web=WARN
spring.jpa.show-sql=false
```

### 2.3. Sử Dụng Environment Variables (Khuyến Nghị)
Thay vì hardcode trong `application.properties`, sử dụng environment variables:

```properties
spring.datasource.password=${DB_PASSWORD:default_password}
app.jwt.secret=${JWT_SECRET:default_secret}
cloudinary.api-secret=${CLOUDINARY_API_SECRET:}
```

### 2.4. Lưu Ý
- File `application.properties` không nên commit sensitive data vào git
- File `application.properties.example` nên được tạo để làm template
- Sử dụng environment variables cho production
- JWT secret phải đủ mạnh (ít nhất 32 ký tự)

## 🧪 3. Test Các Chức Năng Chính

### 3.1. Test Authentication APIs
- [ ] POST `/api/auth/register` - Đăng ký tài khoản mới
- [ ] POST `/api/auth/login` - Đăng nhập
- [ ] POST `/api/auth/logout` - Đăng xuất
- [ ] POST `/api/auth/refresh` - Refresh token
- [ ] POST `/api/auth/forgot-password` - Quên mật khẩu (nếu có)
- [ ] POST `/api/auth/reset-password` - Đặt lại mật khẩu (nếu có)

### 3.2. Test Product APIs
- [ ] GET `/api/products` - Xem danh sách sản phẩm
- [ ] GET `/api/products/{id}` - Xem chi tiết sản phẩm
- [ ] GET `/api/products/search?q=...` - Tìm kiếm sản phẩm
- [ ] GET `/api/products?brand=...&category=...` - Lọc sản phẩm
- [ ] GET `/api/products?sort=price&order=asc` - Sắp xếp sản phẩm
- [ ] POST `/api/admin/products` - Tạo sản phẩm (admin)
- [ ] PUT `/api/admin/products/{id}` - Cập nhật sản phẩm (admin)
- [ ] DELETE `/api/admin/products/{id}` - Xóa sản phẩm (admin)

### 3.3. Test Cart & Order APIs
- [ ] GET `/api/cart` - Xem giỏ hàng
- [ ] POST `/api/cart/items` - Thêm sản phẩm vào giỏ hàng
- [ ] PUT `/api/cart/items/{id}` - Cập nhật số lượng
- [ ] DELETE `/api/cart/items/{id}` - Xóa sản phẩm khỏi giỏ hàng
- [ ] POST `/api/orders` - Tạo đơn hàng
- [ ] GET `/api/orders` - Xem danh sách đơn hàng
- [ ] GET `/api/orders/{id}` - Xem chi tiết đơn hàng
- [ ] PUT `/api/admin/orders/{id}/status` - Cập nhật trạng thái đơn hàng (admin)

### 3.4. Test User APIs
- [ ] GET `/api/users/profile` - Xem profile
- [ ] PUT `/api/users/profile` - Cập nhật profile
- [ ] GET `/api/users/orders` - Xem đơn hàng của user
- [ ] GET `/api/users/wishlist` - Xem wishlist
- [ ] POST `/api/users/wishlist/{productId}` - Thêm vào wishlist
- [ ] DELETE `/api/users/wishlist/{productId}` - Xóa khỏi wishlist

### 3.5. Test Admin APIs (Nếu có quyền admin)
- [ ] GET `/api/admin/dashboard` - Xem dashboard
- [ ] GET `/api/admin/users` - Quản lý người dùng
- [ ] GET `/api/admin/orders` - Quản lý đơn hàng
- [ ] GET `/api/admin/products` - Quản lý sản phẩm
- [ ] GET `/api/admin/analytics` - Xem thống kê
- [ ] GET `/api/admin/brands` - Quản lý brands
- [ ] GET `/api/admin/categories` - Quản lý categories

### 3.6. Test Swagger Documentation
- [ ] Truy cập: http://localhost:8080/swagger-ui.html
- [ ] Kiểm tra tất cả endpoints được hiển thị
- [ ] Test các endpoints trực tiếp từ Swagger UI
- [ ] Kiểm tra authentication trong Swagger

## 🐛 4. Kiểm Tra và Sửa Lỗi

### 4.1. Kiểm Tra Code Style
```bash
# Nếu có Checkstyle
mvn checkstyle:check

# Hoặc sử dụng IDE formatter
```

### 4.2. Kiểm Tra Compilation
```bash
mvn clean compile
```

Kiểm tra:
- [ ] Compile thành công không có lỗi
- [ ] Không có warnings nghiêm trọng

### 4.3. Chạy Unit Tests
```bash
mvn test
```

Kiểm tra:
- [ ] Tất cả tests pass
- [ ] Test coverage đạt mục tiêu (> 70%)

### 4.4. Build Production JAR
```bash
mvn clean package -DskipTests
```

Kiểm tra:
- [ ] Build thành công không có lỗi
- [ ] File JAR được tạo trong `target/` folder
- [ ] Kiểm tra kích thước JAR file

### 4.5. Test Production JAR
```bash
java -jar target/sneakery-backend-1.0.0.jar
```

Kiểm tra:
- [ ] Application khởi động thành công
- [ ] API endpoints hoạt động đúng
- [ ] Database connection thành công

## 📦 5. Tối Ưu Thêm (Tùy Chọn)

### 5.1. Performance Optimization
- [ ] Tối ưu database queries (tránh N+1 queries)
- [ ] Implement caching cho các endpoints thường xuyên truy cập
- [ ] Sử dụng pagination cho danh sách lớn
- [ ] Optimize database indexes
- [ ] Implement connection pooling
- [ ] Sử dụng async processing cho các tác vụ nặng

### 5.2. Code Quality Improvements
- [ ] Thêm JavaDoc cho tất cả public methods
- [ ] Cải thiện error messages
- [ ] Implement proper exception handling
- [ ] Thêm validation cho tất cả DTOs
- [ ] Refactor code để giảm complexity

### 5.3. Testing
- [ ] Thêm unit tests cho services
- [ ] Thêm integration tests cho controllers
- [ ] Thêm repository tests
- [ ] Thêm security tests
- [ ] Setup test coverage reporting

### 5.4. Documentation
- [ ] Cập nhật README.md
- [ ] Thêm JavaDoc cho code phức tạp
- [ ] Cập nhật Swagger annotations
- [ ] Tạo API documentation chi tiết
- [ ] Thêm architecture diagrams

## 🔒 6. Security Checklist

- [ ] Kiểm tra không có sensitive data trong code
- [ ] Đảm bảo JWT secret đủ mạnh và không bị expose
- [ ] Kiểm tra CORS settings phù hợp
- [ ] Validate input từ user (sử dụng @Valid, @NotNull, etc.)
- [ ] Sanitize output để tránh XSS
- [ ] Implement rate limiting cho API endpoints
- [ ] Kiểm tra SQL injection protection (sử dụng JPA/PreparedStatement)
- [ ] Implement password hashing (BCrypt)
- [ ] Kiểm tra authentication và authorization cho tất cả endpoints
- [ ] Setup HTTPS cho production
- [ ] Kiểm tra dependency vulnerabilities: `mvn dependency-check:check`

## 🗄️ 7. Database Optimization

- [ ] Kiểm tra database indexes đã được tạo
- [ ] Optimize slow queries
- [ ] Setup database connection pooling
- [ ] Implement database migrations (Flyway/Liquibase)
- [ ] Backup database thường xuyên
- [ ] Monitor database performance
- [ ] Kiểm tra foreign key constraints
- [ ] Optimize database schema

## 🚀 8. Deployment

### 8.1. Chuẩn Bị
- [ ] Set environment variables trên server
- [ ] Cấu hình database trên production server
- [ ] Setup SSL certificate
- [ ] Cấu hình firewall rules
- [ ] Setup reverse proxy (Nginx/Apache) nếu cần

### 8.2. Build và Deploy
```bash
# Build JAR
mvn clean package -DskipTests

# Upload JAR lên server
scp target/sneakery-backend-1.0.0.jar user@server:/path/to/app/

# Chạy trên server
java -jar -Dspring.profiles.active=prod sneakery-backend-1.0.0.jar
```

### 8.3. Setup Production Service (Systemd)
Tạo file `/etc/systemd/system/sneakery-backend.service`:

```ini
[Unit]
Description=Sneakery Backend Service
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/path/to/app
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /path/to/app/sneakery-backend-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# Enable và start service
sudo systemctl enable sneakery-backend
sudo systemctl start sneakery-backend
sudo systemctl status sneakery-backend
```

### 8.4. Sau Khi Deploy
- [ ] Test tất cả chức năng trên production
- [ ] Monitor logs: `journalctl -u sneakery-backend -f`
- [ ] Kiểm tra performance
- [ ] Setup health check endpoint
- [ ] Test backup và restore

## 📊 9. Monitoring & Analytics

- [ ] Setup application monitoring (Spring Boot Actuator)
- [ ] Setup error tracking (Sentry, Logback, etc.)
- [ ] Monitor API response times
- [ ] Track database query performance
- [ ] Setup log aggregation (ELK Stack, etc.)
- [ ] Monitor server resources (CPU, Memory, Disk)
- [ ] Setup alerts cho critical errors
- [ ] Track API usage và rate limiting

### 9.1. Spring Boot Actuator
Thêm vào `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Cấu hình trong `application.properties`:
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

## 🎯 10. Các Cải Thiện Đề Xuất Tiếp Theo

Dựa trên best practices, các cải thiện sau có thể được thực hiện:

1. **Database Migrations**: Implement Flyway hoặc Liquibase cho database versioning
2. **API Versioning**: Implement API versioning (v1, v2, etc.)
3. **Rate Limiting**: Thêm rate limiting để bảo vệ API
4. **Caching Strategy**: Mở rộng caching cho nhiều endpoints hơn
5. **Async Processing**: Sử dụng @Async cho các tác vụ nặng (email, image processing)
6. **Event-Driven Architecture**: Implement domain events cho các business logic phức tạp
7. **API Gateway**: Setup API Gateway nếu có nhiều microservices
8. **Distributed Tracing**: Implement distributed tracing (Zipkin, Jaeger)
9. **Health Checks**: Thêm health check endpoints chi tiết hơn
10. **API Documentation**: Cải thiện Swagger documentation với examples

## 📝 Notes

- Tất cả hardcoded values đã được thay thế bằng configuration properties
- Logger đã được implement để quản lý logging
- API config hỗ trợ environment variables
- Code đã được tối ưu và cải thiện security
- Swagger documentation đã được setup

## 🆘 Nếu Gặp Vấn Đề

1. **Application không khởi động**: 
   - Kiểm tra Java version (phải là Java 17+)
   - Kiểm tra database connection
   - Kiểm tra port 8080 có bị chiếm không

2. **Database connection errors**: 
   - Kiểm tra SQL Server đang chạy
   - Kiểm tra credentials trong application.properties
   - Kiểm tra firewall rules

3. **CORS errors**: 
   - Kiểm tra CORS configuration trong SecurityConfig
   - Kiểm tra allowed origins

4. **JWT errors**: 
   - Kiểm tra JWT secret đã được set
   - Kiểm tra token expiration time

5. **Build errors**: 
   - Kiểm tra Maven version
   - Chạy `mvn clean install -U` để update dependencies
   - Kiểm tra Java version

6. **Runtime errors**: 
   - Kiểm tra application logs
   - Kiểm tra database schema
   - Kiểm tra environment variables

## 📚 Tài Liệu Tham Khảo

- File `README.md`: Hướng dẫn setup và sử dụng
- File `application.properties`: Configuration file
- File `pom.xml`: Maven dependencies
- Swagger UI: http://localhost:8080/swagger-ui.html
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Security Documentation: https://spring.io/projects/spring-security

---

**Chúc bạn thành công! 🎉**


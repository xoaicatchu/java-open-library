# 36-SpringSecurity - Spring Security 6

> **Cổng dịch vụ (Server Port)**: `8136`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần bảo mật toàn bộ API, hỗ trợ phân quyền vai trò (RBAC), mã hóa mật khẩu một chiều BCrypt an toàn.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Kiến trúc chuẩn `SecurityFilterChain` không session (Stateless API), xác thực JWT Bearer token, phân quyền chi tiết tới từng method bằng `@PreAuthorize("hasRole('ADMIN')")`.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/security/controller/AuthController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.
- `com/example/security/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/security/security/CustomUserDetailsService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/security/service/AuthService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/security/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/security/repository/UserRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/security/SecurityApplication.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.
- `com/example/security/config/SecurityConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/security/dto/AuthRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/security/dto/AuthResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/security/dto/RegisterRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/security/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/security/entity/Role.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/security/entity/User.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8136
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:securitydb
    driverClassName: org.h2.Driver
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration: 86400000 # 24 hours
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 36-SpringSecurity

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8136`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8136/api/auth/register` | 1. Đăng ký tài khoản USER mới |
| `POST` | `http://localhost:8136/api/auth/register` | 2. Đăng ký tài khoản ADMIN mới |
| `POST` | `http://localhost:8136/api/auth/login` | @name loginAdmin |
| `POST` | `http://localhost:8136/api/auth/login` | @name loginUser |
| `GET` | `http://localhost:8136/api/products` | 5. Lấy danh sách sản phẩm (Yêu cầu Token - Bất kỳ Role nào) |
| `POST` | `http://localhost:8136/api/products` | 6. Thêm sản phẩm mới (Chỉ dành cho ADMIN - @PreAuthorize("hasRole('ADMIN')")) |
| `POST` | `http://localhost:8136/api/products` | 7. Thử dùng USER thêm sản phẩm (Mong đợi: 403 Forbidden) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

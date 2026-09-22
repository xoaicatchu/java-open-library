# 33-SpringAOP - Spring AOP

> **Cổng dịch vụ (Server Port)**: `8133`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần ghi lịch sử (Audit Log: ai vừa xóa đơn hàng, lúc mấy giờ) hoặc đo thời gian chạy của hàng trăm hàm mà không muốn chèn code bẩn vào từng hàm.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Lập trình hướng khía cạnh (AOP). Tự định nghĩa annotation `@Auditable`, `@MeasurePerformance` để can thiệp trong suốt trước/sau khi gọi hàm.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/aop/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/aop/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/aop/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/aop/repository/AuditLogRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/aop/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/aop/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/aop/entity/AuditLog.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/aop/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8133
spring:
  application:
    name: 33-SpringAOP
  datasource:
    url: jdbc:h2:mem:aopdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  h2:
    console:
      enabled: true
  threads:
    virtual:
      enabled: true
logging:
  level:
    com.example.aop: DEBUG
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 33-SpringAOP

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8133`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8133/api/products` | Get all products |
| `POST` | `http://localhost:8133/api/products` | Create a product |
| `POST` | `http://localhost:8133/api/products` | Create product rapidly (Run this 4 times to see 429 RateLimit) |
| `GET` | `http://localhost:8133/api/audit-logs` | Check audit logs |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

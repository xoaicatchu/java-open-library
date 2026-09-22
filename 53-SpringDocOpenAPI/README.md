# 53-SpringDocOpenAPI - SpringDoc OpenAPI

> **Cổng dịch vụ (Server Port)**: `8153`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Đội Backend sửa API nhưng quên cập nhật file tài liệu gửi cho đội Mobile/Frontend khiến kết nối bị lệch.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Tự động phân tích code Java để sinh giao diện tài liệu Swagger UI trực quan tại `/swagger-ui.html`. Cho phép test gọi API trực tiếp trên trình duyệt kèm xác thực Bearer Token.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/springdoc/controller/v1/ProductControllerV1.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.
- `com/example/springdoc/controller/v2/OrderControllerV2.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/springdoc/service/ApiService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/springdoc/repository/OrderRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/springdoc/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/springdoc/config/OpenAPIConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/springdoc/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/dto/ProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/dto/ProductResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/entity/Order.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8153
spring:
  application:
    name: 53-SpringDocOpenAPI
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: false
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
  group-configs:
    - group: v1
      paths-to-match: /api/v1/**
    - group: v2
      paths-to-match: /api/v2/**
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 53-SpringDocOpenAPI

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8153`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8153/v3/api-docs` | 1. Xem đặc tả OpenAPI Specification JSON toàn bộ hệ thống |
| `GET` | `http://localhost:8153/v3/api-docs/products-v1` | 2. Xem đặc tả OpenAPI Nhóm V1 - Products |
| `GET` | `http://localhost:8153/v3/api-docs/orders-v2` | 3. Xem đặc tả OpenAPI Nhóm V2 - Orders |
| `GET` | `http://localhost:8153/api/v1/products` | 4. Lấy danh sách sản phẩm (V1 API) |
| `POST` | `http://localhost:8153/api/v1/products` | 5. Thêm mới sản phẩm (V1 API) |
| `GET` | `http://localhost:8153/api/v2/orders` | 6. Lấy danh sách đơn hàng (V2 API) |
| `POST` | `http://localhost:8153/api/v2/orders` | 7. Tạo đơn hàng mới (V2 API) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

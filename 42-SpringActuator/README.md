# 42-SpringActuator - Spring Boot Actuator

> **Cổng dịch vụ (Server Port)**: `8142`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần cơ chế để cụm Kubernetes biết container nào đang sống (Liveness Probe) và container nào đã sẵn sàng nhận traffic (Readiness Probe).

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Các endpoint quản trị production `/actuator/health`, `/actuator/info`. Tự viết `CustomHealthIndicator` kiểm tra kết nối DB, thanh toán trước khi nhận request.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/actuator/controller/DemoController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.
- `com/example/actuator/endpoint/OrdersEndpoint.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/actuator/health/ExternalServiceHealthIndicator.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8142
spring:
  application:
    name: actuator-demo
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:actuatordb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
management:
  endpoints:
    web:
      exposure:
        include: health,info,env,beans,mappings,conditions,orders
  endpoint:
    health:
      show-details: always
      show-components: always
      probes:
        enabled: true
    env:
      show-values: always
  info:
    env:
      enabled: true
  health:
    livenessstate:
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 42-SpringActuator

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8142`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8142/actuator/health` | 1. Kiểm tra Health Check (Kèm Database & ExternalService Custom Health Indicator) |
| `GET` | `http://localhost:8142/actuator/info` | 2. Kiểm tra thông tin ứng dụng (CustomInfoContributor) |
| `GET` | `http://localhost:8142/actuator/metrics` | 3. Kiểm tra danh sách Metrics có sẵn |
| `GET` | `http://localhost:8142/actuator/orders` | 4. Custom Actuator Endpoint: Xem thống kê đơn hàng (@Endpoint(id = "orders")) |
| `POST` | `http://localhost:8142/actuator/orders` | 5. Custom Actuator Endpoint: Cập nhật thống kê đơn hàng (@WriteOperation) |
| `GET` | `http://localhost:8142/api/orders` | 6. Business REST API: Lấy thống kê đơn hàng |
| `POST` | `http://localhost:8142/api/orders` | 7. Business REST API: Thêm đơn hàng mới |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

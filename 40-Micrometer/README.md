# 40-Micrometer - Micrometer + Prometheus

> **Cổng dịch vụ (Server Port)**: `8140`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần biết hệ thống hiện tại đang xử lý bao nhiêu request/giây (QPS), tỉ lệ lỗi 5xx là bao nhiêu %, CPU/RAM tiêu hao thế nào để vẽ biểu đồ Dashboard.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Thư viện đo đạc chuẩn (Metric Facade). Tạo custom Counter, Timer, Gauge và phơi endpoint `/actuator/prometheus` cho máy chủ Grafana/Prometheus cào dữ liệu định kỳ.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/micrometer/controller/OrderController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/micrometer/service/OrderService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/micrometer/repository/OrderRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/micrometer/config/MetricsConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/micrometer/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/micrometer/entity/Order.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8140
spring:
  application:
    name: micrometer-demo
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:orderdb
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    prometheus:
      enabled: true
  prometheus:
    metrics:
      export:
        enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
    distribution:
      percentiles-histogram:
        http.server.requests: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 40-Micrometer

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8140`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8140/api/orders` | Thực thi POST http://localhost:8140/api/orders |
| `GET` | `http://localhost:8140/api/orders` | Thực thi GET http://localhost:8140/api/orders |
| `POST` | `http://localhost:8140/api/orders/users/login` | Thực thi POST http://localhost:8140/api/orders/users/login |
| `POST` | `http://localhost:8140/api/orders/users/logout` | Thực thi POST http://localhost:8140/api/orders/users/logout |
| `GET` | `http://localhost:8140/actuator/metrics` | Thực thi GET http://localhost:8140/actuator/metrics |
| `GET` | `http://localhost:8140/actuator/prometheus` | Thực thi GET http://localhost:8140/actuator/prometheus |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

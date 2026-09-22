# 40-Micrometer - Micrometer + Prometheus

<p align="left">
  <img src="https://img.shields.io/badge/Port-8140-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Application%20Metrics%20&%20Monitoring-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Ban lãnh đạo và đội DevOps không có cách nào biết được hệ thống đang xử lý bao nhiêu request/giây (QPS), thời gian phản hồi trung bình (p95/p99 latency) là bao nhiêu millisecond, tỉ lệ lỗi 5xx là bao nhiêu % và bộ nhớ JVM Heap còn bao nhiêu. Khi hệ thống quá tải, không có bất kỳ cảnh báo nào trước khi sập.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Cung cấp số liệu đo đạc (Metrics) chuẩn mực cho máy chủ Prometheus cào dữ liệu định kỳ qua endpoint `/actuator/prometheus`.**
- **Tạo các bộ đo đạc nghiệp vụ tùy biến: đếm số đơn hàng thành công (`Counter`), đo thời gian gọi cổng thanh toán (`Timer`), theo dõi số user đang online (`Gauge`).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Micrometer + Prometheus |
|:---|:---|:---|
| **Dropwizard Metrics** | `Legacy Metrics` | Dropwizard Metrics là chuẩn thế hệ cũ; Micrometer là chuẩn thế hệ mới hỗ trợ đa chiều (Dimensional Metrics với Tags). |
| **JMX (Java Management Extensions)** | `JVM Native` | JMX khó tích hợp với các công cụ Cloud-native hiện đại; Micrometer hỗ trợ chuẩn công nghiệp Prometheus, Datadog, InfluxDB out-of-the-box. |
| **Custom DB Metric Table** | `Ad-hoc Solution` | Lưu metric vào DB làm tăng tải DB nghiêm trọng; Micrometer tính toán in-memory siêu tốc. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Được ví như 'SLF4J dành cho Metrics': trừu tượng hóa việc đo đạc, cho phép đẩy metric sang Prometheus, Datadog, New Relic chỉ bằng cấu hình.**
- **Mô hình Dimensional Metrics cực mạnh: gắn các nhãn (tags/labels: `env=prod`, `method=POST`, `status=200`) giúp lọc và vẽ biểu đồ dễ dàng trên Grafana.**
- **Tích hợp sẵn tự động đo đạc tài nguyên JVM (Heap, GC, Threads) và HTTP server metrics.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cần cẩn trọng với bài toán High Cardinality: nếu gắn các tag có giá trị biến thiên vô tận (như User ID hoặc Order ID) vào Metric sẽ làm nổ bộ nhớ của Prometheus.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Tiêu chuẩn bắt buộc phải có cho mọi ứng dụng chạy trên hạ tầng Kubernetes/Cloud để giám sát sức khỏe hệ thống. KHÔNG NÊN DÙNG: Không có lý do loại bỏ.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/micrometer/controller/OrderController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/micrometer/service/OrderService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/micrometer/repository/OrderRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/micrometer/config/MetricsConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/micrometer/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/micrometer/entity/Order.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 40-Micrometer

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8140`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8140/api/orders` | Thực thi POST http://localhost:8140/api/orders |
| `GET` | `http://localhost:8140/api/orders` | Thực thi GET http://localhost:8140/api/orders |
| `POST` | `http://localhost:8140/api/orders/users/login` | Thực thi POST http://localhost:8140/api/orders/users/login |
| `POST` | `http://localhost:8140/api/orders/users/logout` | Thực thi POST http://localhost:8140/api/orders/users/logout |
| `GET` | `http://localhost:8140/actuator/metrics` | Thực thi GET http://localhost:8140/actuator/metrics |
| `GET` | `http://localhost:8140/actuator/prometheus` | Thực thi GET http://localhost:8140/actuator/prometheus |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

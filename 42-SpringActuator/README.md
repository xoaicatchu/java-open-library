# 42-SpringActuator - Spring Boot Actuator

<p align="left">
  <img src="https://img.shields.io/badge/Port-8142-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Production%20Readiness%20&%20Health%20Checks-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi triển khai trên Kubernetes hoặc Docker Swarm, hạ tầng không thể biết container nào đang bị treo (đơ luồng, cạn kết nối DB) để tự động khởi động lại, và container nào đã nạp xong dữ liệu để bắt đầu điều phối traffic vào, dẫn tới việc người dùng nhận lỗi 502 Bad Gateway.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Cung cấp các đầu dò **Liveness Probe** (`/actuator/health/liveness`) và **Readiness Probe** (`/actuator/health/readiness`) cho Kubernetes.**
- **Tự viết `CustomHealthIndicator` để kiểm tra kết nối CSDL, Redis, và các cổng thanh toán bên thứ ba trước khi cho phép container nhận khách.**
- **Cung cấp thông tin phiên bản ứng dụng, git commit, thời gian build qua endpoint `/actuator/info`.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Boot Actuator |
|:---|:---|:---|
| **Custom /health REST Controller** | `Ad-hoc Endpoint` | Tự viết thiếu cơ chế gộp trạng thái nhiều thành phần và không phân tách rõ ràng giữa Liveness và Readiness của Kubernetes. |
| **Prometheus Standalone Exporter** | `Metrics Exporter` | Chỉ đo thông số; Actuator cung cấp cả thông tin sức khỏe (Health), cấu hình môi trường (Env), và Thread Dump. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tích hợp sẵn nguyên bản trong Spring Boot, kích hoạt cực nhanh chỉ với 1 dependency.**
- **Tự động tích hợp sâu với cơ chế dò trạng thái của cụm Kubernetes (K8s Probes).**
- **Cực kỳ an toàn: cho phép cấu hình phân quyền chi tiết các endpoint nhạy cảm thông qua Spring Security.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Nếu cấu hình hớ hênh phơi toàn bộ endpoint (`endpoints.web.exposure.include=*`) mà không có mật khẩu bảo vệ sẽ làm lộ thông tin nhạy cảm (biến môi trường, heap dump).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Bắt buộc phải có cho 100% ứng dụng Spring Boot chạy trên môi trường Container / Kubernetes. KHÔNG NÊN DÙNG: Không có lý do loại bỏ.**

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

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 42-SpringActuator

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8142`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8142/actuator/health` | 1. Kiểm tra Health Check (Kèm Database & ExternalService Custom Health Indicator) |
| `GET` | `http://localhost:8142/actuator/info` | 2. Kiểm tra thông tin ứng dụng (CustomInfoContributor) |
| `GET` | `http://localhost:8142/actuator/metrics` | 3. Kiểm tra danh sách Metrics có sẵn |
| `GET` | `http://localhost:8142/actuator/orders` | 4. Custom Actuator Endpoint: Xem thống kê đơn hàng (@Endpoint(id = "orders")) |
| `POST` | `http://localhost:8142/actuator/orders` | 5. Custom Actuator Endpoint: Cập nhật thống kê đơn hàng (@WriteOperation) |
| `GET` | `http://localhost:8142/api/orders` | 6. Business REST API: Lấy thống kê đơn hàng |
| `POST` | `http://localhost:8142/api/orders` | 7. Business REST API: Thêm đơn hàng mới |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

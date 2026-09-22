# 06-SpringGateway - Spring Cloud Gateway

<p align="left">
  <img src="https://img.shields.io/badge/Port-8106-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-API%20Gateway%20&%20Routing-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Để các client bên ngoài gọi trực tiếp tới từng microservice con sẽ làm lộ cấu trúc mạng nội bộ, phân tán logic xác thực JWT/CORS, không có nơi kiểm soát tập trung lưu lượng truy cập và dễ bị sập nguồn khi bị tấn công quá tải.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Cửa ngõ duy nhất tiếp nhận request (Single Entry Point), định tuyến traffic tới hàng chục service nội bộ.**
- **Xác thực token JWT tập trung, giải mã quyền và truyền thông tin User xuống các service qua HTTP Headers.**
- **Bảo vệ hệ thống bằng Rate Limiting (dùng Redis Token Bucket) và Circuit Breaker.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Cloud Gateway |
|:---|:---|:---|
| **Kong Gateway** | `Nginx/Lua Gateway` | Kong có raw throughput cao hơn, nhưng Spring Cloud Gateway viết bằng Java giúp dễ tùy biến filter bằng Spring Bean. |
| **Envoy Proxy** | `C++ Service Proxy` | Envoy là chuẩn Service Mesh xuất sắc nhưng cấu hình phức tạp; Spring Gateway tích hợp tự nhiên trong hệ sinh thái Spring. |
| **Netflix Zuul 1** | `Legacy Blocking Gateway` | Zuul 1 chạy blocking I/O hiệu năng kém; Spring Cloud Gateway chạy non-blocking WebFlux nhanh hơn nhiều. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Xây dựng trên Spring WebFlux (Reactor Netty) non-blocking, chịu tải kết nối đồng thời cực tốt.**
- **Lập trình Filter (tiền xử lý và hậu xử lý request) cực kỳ linh hoạt bằng ngôn ngữ Java.**
- **Tích hợp hoàn hảo với Spring Cloud Discovery (Eureka/Consul), Resilience4j và Micrometer.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Mọi filter đều phải viết theo phong cách Reactive, dễ gây lỗi nếu lập trình viên gọi blocking code.
- Throughput thấp hơn các Gateway viết bằng C/C++ (như Nginx/Envoy) ở tải hàng trăm ngàn QPS.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các tổ chức sử dụng toàn bộ hệ sinh thái Spring Boot muốn quản lý Gateway bằng code Java. KHÔNG NÊN DÙNG: Cho hệ thống đa ngôn ngữ (Polyglot) khổng lồ cấp tập đoàn cần Gateway dùng chung cho Java, Go, Node.js, Python (nên dùng Kong hoặc Envoy).**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8106
spring:
  application:
    name: gateway-service
  threads:
    virtual:
      enabled: true
resilience4j.circuitbreaker:
  instances:
    myCircuitBreaker:
      slidingWindowSize: 10
      permittedNumberOfCallsInHalfOpenState: 3
      failureRateThreshold: 50
      waitDurationInOpenState: 5000
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 06-SpringGateway

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8106`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8106/api/basic/downstream/hello` | 1. Basic Route (Path predicate, Header filters, StripPrefix) |
| `GET` | `http://localhost:8106/api/legacy/sample?token=123` | 2. RewritePath Route (Rewrite /api/legacy -> /downstream/new with query param token=123) |
| `GET` | `http://localhost:8106/api/flaky/downstream/error` | 3. Circuit Breaker Route (Triggers fallback on error) |
| `GET` | `http://localhost:8106/api/limited/downstream/hello` | 4. Rate Limited Route (Headers predicate + InMemory Rate Limiter) |
| `GET` | `http://localhost:8106/fallback` | 5. Direct Fallback Endpoint |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

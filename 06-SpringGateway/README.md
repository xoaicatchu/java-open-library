# 06-SpringGateway - Spring Cloud Gateway

> **Cổng dịch vụ (Server Port)**: `8106`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Hệ thống có hàng chục microservice, không thể để client gọi trực tiếp từng service (lộ IP, khó kiểm soát auth, cors, rate limit).

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Cửa ngõ duy nhất tiếp nhận request: định tuyến URL, lọc token JWT, chặn IP DDoS, rate limit, ghi log truy vết tập trung.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/gateway/controller/DownstreamController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.
- `com/example/gateway/controller/FallbackController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/gateway/config/GatewayConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 06-SpringGateway

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8106`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8106/api/basic/downstream/hello` | 1. Basic Route (Path predicate, Header filters, StripPrefix) |
| `GET` | `http://localhost:8106/api/legacy/sample?token=123` | 2. RewritePath Route (Rewrite /api/legacy -> /downstream/new with query param token=123) |
| `GET` | `http://localhost:8106/api/flaky/downstream/error` | 3. Circuit Breaker Route (Triggers fallback on error) |
| `GET` | `http://localhost:8106/api/limited/downstream/hello` | 4. Rate Limited Route (Headers predicate + InMemory Rate Limiter) |
| `GET` | `http://localhost:8106/fallback` | 5. Direct Fallback Endpoint |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

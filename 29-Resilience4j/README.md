# 29-Resilience4j - Resilience4j

> **Cổng dịch vụ (Server Port)**: `8129`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Dịch vụ thanh toán bên thứ 3 (MoMo/VNPAY) bị treo, khiến các thread của hệ thống bị giữ chặt, kéo sập toàn bộ hệ sinh thái (Cascading Failure).

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
**Circuit Breaker (Cầu dao tự ngắt)** khi đối tác lỗi, **Retry** tự động thử lại khi mạng chập chờn, **Rate Limiter** giới hạn tần suất gọi, **Bulkhead** cô lập tài nguyên.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/resilience/controller/PaymentController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/resilience/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/resilience/service/PaymentService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/resilience/dto/PaymentRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/resilience/dto/PaymentResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8129
spring:
  application:
    name: resilience-service
  threads:
    virtual:
      enabled: true
management:
  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,metrics,circuitbreakers,ratelimiters,retry
  endpoint:
    health:
      show-details: always
resilience4j:
  circuitbreaker:
    configs:
      default:
        registerHealthIndicator: true
        slidingWindowSize: 5
        minimumNumberOfCalls: 3
        permittedNumberOfCallsInHalfOpenState: 2
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 2s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
  retry:
    configs:
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 29-Resilience4j

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8129`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8129/api/payments/circuit-breaker` | 1. Test Circuit Breaker (Cầu dao tự ngắt) |
| `POST` | `http://localhost:8129/api/payments/retry` | 2. Test Retry (Thử lại tự động) |
| `POST` | `http://localhost:8129/api/payments/rate-limiter` | 3. Test Rate Limiter (Giới hạn tần suất gọi) |
| `POST` | `http://localhost:8129/api/payments/bulkhead` | 4. Test Bulkhead (Cô lập tài nguyên / Giới hạn đồng thời) |
| `POST` | `http://localhost:8129/api/payments/time-limiter` | 5. Test Time Limiter (Timeout bảo vệ) |
| `POST` | `http://localhost:8129/api/payments/toggle-failure?fail=true` | 6. Bật giả lập lỗi bên thứ 3 để test Fallback / Cầu dao |
| `POST` | `http://localhost:8129/api/payments/toggle-failure?fail=false` | 7. Tắt giả lập lỗi (trở lại bình thường) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

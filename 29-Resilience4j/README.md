# 29-Resilience4j - Resilience4j

<p align="left">
  <img src="https://img.shields.io/badge/Port-8129-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Fault%20Tolerance%20&%20Resilience-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Một dịch vụ đối tác bên ngoài (như Cổng thanh toán hoặc Đơn vị vận chuyển) bị quá tải và phản hồi chậm 30 giây. Các thread của hệ thống chúng ta bị giữ chặt để chờ đợi, nhanh chóng làm cạn kiệt toàn bộ Thread Pool và kéo sập dây chuyền (Cascading Failure) toàn bộ hệ sinh thái của công ty.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- ****Circuit Breaker (Cầu dao tự ngắt)**: Tự động ngắt kết nối tới dịch vụ đối tác khi tỉ lệ lỗi vượt quá ngưỡng (ví dụ 50%), lập tức trả về phản hồi dự phòng (Fallback) thay vì chờ đợi timeout.**
- ****Rate Limiter & Bulkhead**: Giới hạn tần suất gọi API ngoài và cô lập luồng tài nguyên, không để lỗi ở một dịch vụ làm ảnh hưởng tới các dịch vụ khác.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Resilience4j |
|:---|:---|:---|
| **Netflix Hystrix** | `Legacy Circuit Breaker` | Hystrix đã bị Netflix dừng phát triển (End-Of-Life); Resilience4j là thư viện chuẩn hiện đại kế thừa hoàn hảo. |
| **Alibaba Sentinel** | `Flow Control` | Sentinel mạnh hơn về điều tiết luồng và bảo vệ thích ứng CPU; Resilience4j nhẹ nhàng hơn, thiết kế module hóa hướng chức năng (Functional Programming). |
| **Istio / Envoy Service Mesh** | `Infrastructure Mesh` | Service Mesh xử lý ở mức hạ tầng mạng; Resilience4j xử lý linh hoạt ngay trong mã nguồn ứng dụng với Fallback logic thông minh. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Được thiết kế hiện đại trên nền Java 8+ Functional Programming và thư viện Vavr.**
- **Module hóa tách rời: bạn có thể chọn dùng riêng rẽ CircuitBreaker, RateLimiter, Retry, Bulkhead hoặc TimeLimiter.**
- **Tích hợp đo đạc số liệu hoàn hảo với Micrometer và Spring Boot Actuator.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cần cân nhắc cấu hình kích thước Sliding Window và tỉ lệ Failure Rate cẩn thận để tránh đóng/ngắt cầu dao sai thời điểm.
- Chỉ hoạt động trong phạm vi bộ nhớ của từng instance (không có trạng thái Circuit Breaker phân tán giữa các node).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Bắt buộc phải bọc cho 100% các cuộc gọi REST/RPC tới hệ thống bên ngoài hoặc microservice khác. KHÔNG NÊN DÙNG: Cho các phương thức xử lý nội bộ thuần túy trong cùng một ứng dụng.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/resilience/controller/PaymentController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/resilience/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/resilience/service/PaymentService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/resilience/dto/PaymentRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/resilience/dto/PaymentResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 29-Resilience4j

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8129`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8129/api/payments/circuit-breaker` | 1. Test Circuit Breaker (Cầu dao tự ngắt) |
| `POST` | `http://localhost:8129/api/payments/retry` | 2. Test Retry (Thử lại tự động) |
| `POST` | `http://localhost:8129/api/payments/rate-limiter` | 3. Test Rate Limiter (Giới hạn tần suất gọi) |
| `POST` | `http://localhost:8129/api/payments/bulkhead` | 4. Test Bulkhead (Cô lập tài nguyên / Giới hạn đồng thời) |
| `POST` | `http://localhost:8129/api/payments/time-limiter` | 5. Test Time Limiter (Timeout bảo vệ) |
| `POST` | `http://localhost:8129/api/payments/toggle-failure?fail=true` | 6. Bật giả lập lỗi bên thứ 3 để test Fallback / Cầu dao |
| `POST` | `http://localhost:8129/api/payments/toggle-failure?fail=false` | 7. Tắt giả lập lỗi (trở lại bình thường) |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

# 34-SpringCloudOpenFeign - Spring Cloud OpenFeign

<p align="left">
  <img src="https://img.shields.io/badge/Port-8134-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Declarative%20REST%20Client-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi gọi REST API của các microservice khác, việc sử dụng RestTemplate hoặc HttpClient thủ công đòi hỏi phải viết lặp đi lặp lại code cấu hình URL, thêm Header Authorization, parse response và xử lý exception, khiến mã nguồn trở nên dài dòng và khó đọc.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Khai báo giao tiếp giữa các vi dịch vụ (Service-to-Service Communication) dưới dạng Java Interface có gắn annotation Spring MVC (`@GetMapping`, `@PostMapping`).**
- **Tích hợp tự động cơ chế phục hồi Circuit Breaker (Resilience4j) và Load Balancer (Spring Cloud LoadBalancer) khi gọi dịch vụ khác.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Cloud OpenFeign |
|:---|:---|:---|
| **Spring 6.1 RestClient** | `Modern Fluent Client` | RestClient là client đồng bộ mới nhất của Spring 6; OpenFeign thiên về mô hình khai báo Interface tự sinh proxy. |
| **Retrofit (Square)** | `Declarative Client` | Retrofit rất nổi tiếng trên Android; OpenFeign là tiêu chuẩn được tối ưu hóa cho hệ sinh thái Spring Cloud. |
| **Legacy RestTemplate** | `Template-based Client` | RestTemplate đã chuyển sang chế độ bảo trì; OpenFeign thanh lịch và dễ bảo trì hơn nhiều. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Cực kỳ trực quan: Không cần viết 1 dòng code triển khai HTTP, chỉ cần khai báo interface giống hệt Controller.**
- **Tự động tích hợp Service Discovery (Eureka/Consul), Load Balancing và Fallback Circuit Breaker.**
- **Dễ dàng cấu hình RequestInterceptor để tự động chuyển tiếp JWT token giữa các service.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Phụ thuộc vào hệ sinh thái Spring Cloud, làm tăng dung lượng dependency của ứng dụng.
- Tốc độ xử lý thô chậm hơn một chút so với các HTTP client tối ưu hóa sâu do phải đi qua nhiều tầng proxy.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các kiến trúc Microservices truyền thống sử dụng đầy đủ Spring Cloud Service Discovery. KHÔNG NÊN DÙNG: Cho các ứng dụng độc lập đơn lẻ hoặc khi bạn muốn một giải pháp nhẹ nhàng không kéo theo Spring Cloud (khi đó nên dùng Spring 6 RestClient).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/feign/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/feign/client/FeignConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/feign/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8134
spring:
  application:
    name: feign-demo
  threads:
    virtual:
      enabled: true
  cloud:
    openfeign:
      circuitbreaker:
        enabled: true
      client:
        config:
          default:
            loggerLevel: full
resilience4j.circuitbreaker:
  instances:
    demoClient:
      slidingWindowSize: 10
      minimumNumberOfCalls: 5
      permittedNumberOfCallsInHalfOpenState: 3
      automaticTransitionFromOpenToHalfOpenEnabled: true
      waitDurationInOpenState: 5s
      failureRateThreshold: 50
      eventConsumerBufferSize: 10
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 34-SpringCloudOpenFeign

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8134`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8134/api/products?limit=10` | 1. Gọi danh sách sản phẩm qua Feign Client |
| `GET` | `http://localhost:8134/api/products/1` | 2. Gọi chi tiết sản phẩm theo ID (Feign Client gắn kèm Bearer Token qua RequestInterceptor) |
| `POST` | `http://localhost:8134/api/products` | 3. Tạo sản phẩm mới |
| `GET` | `http://localhost:8134/api/products/999` | 4. Test Error Decoder (Gọi ID 999 trả về 404 -> Custom Error Decoder xử lý) |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

# 34-SpringCloudOpenFeign - Spring Cloud OpenFeign

> **Cổng dịch vụ (Server Port)**: `8134`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Gọi REST API của microservice khác bằng code thủ công rất dài dòng, cấu hình header, auth lặp đi lặp lại.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Khai báo Client dưới dạng Java Interface có gắn `@GetMapping`, `@PostMapping`. Tự động tích hợp Fallback và Retry.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/feign/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/feign/client/FeignConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/feign/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 34-SpringCloudOpenFeign

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8134`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8134/api/products?limit=10` | 1. Gọi danh sách sản phẩm qua Feign Client |
| `GET` | `http://localhost:8134/api/products/1` | 2. Gọi chi tiết sản phẩm theo ID (Feign Client gắn kèm Bearer Token qua RequestInterceptor) |
| `POST` | `http://localhost:8134/api/products` | 3. Tạo sản phẩm mới |
| `GET` | `http://localhost:8134/api/products/999` | 4. Test Error Decoder (Gọi ID 999 trả về 404 -> Custom Error Decoder xử lý) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

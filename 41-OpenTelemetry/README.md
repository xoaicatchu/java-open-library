# 41-OpenTelemetry - OpenTelemetry

> **Cổng dịch vụ (Server Port)**: `8141`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Một request của user đi qua 5 microservice khác nhau bị chậm. Không ai biết nguyên nhân chậm nằm ở service nào hay câu query SQL nào.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
**Distributed Tracing (Truy vết phân tán)**. Tự động truyền ngữ cảnh (W3C Trace Context) qua HTTP header, đo thời gian từng Span và hiển thị trực quan sơ đồ luồng đi trên Jaeger / Zipkin.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/otel/controller/OtelController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/otel/service/OtelService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/otel/config/OtelConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/otel/dto/ProcessResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8141
spring:
  application:
    name: otel-demo
  threads:
    virtual:
      enabled: true
  mvc:
    problemdetails:
      enabled: true
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  tracing:
    sampling:
      probability: 1.0
    baggage:
      remote-fields: "user-id,session-id"
      correlation:
        enabled: true
        fields: "user-id,session-id"
logging:
  pattern:
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 41-OpenTelemetry

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8141`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8141/api/otel/process` | 1. Kích hoạt Span, Manual Span và Baggage Context (@WithSpan, Tracer.spanBuilder) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

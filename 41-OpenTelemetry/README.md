# 41-OpenTelemetry - OpenTelemetry

<p align="left">
  <img src="https://img.shields.io/badge/Port-8141-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Distributed%20Tracing%20&%20Observability-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Một request của khách hàng đi qua 5 vi dịch vụ khác nhau (Gateway -> Order Service -> Payment -> Inventory -> Notification) bị phản hồi chậm tới 8 giây. Không ai biết điểm nghẽn (bottleneck) nằm ở service nào, câu query database nào hay network call nào.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Truy vết phân tán (Distributed Tracing): gắn mã `traceId` duy nhất xuyên suốt tất cả các service thông qua HTTP Headers (W3C Trace Context).**
- **Đo lường thời gian thực thi của từng bước (Spans) và hiển thị trực quan sơ đồ luồng đi dạng Waterfall trên Jaeger, Zipkin hoặc Grafana Tempo.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với OpenTelemetry |
|:---|:---|:---|
| **Micrometer Tracing (Spring Cloud Sleuth cũ)** | `Spring Tracing Facade` | Sleuth đã dừng phát triển; OpenTelemetry là tiêu chuẩn mở toàn cầu của Cloud Native Computing Foundation (CNCF). |
| **Apache SkyWalking** | `APM Platform` | SkyWalking dùng Java Agent tự động; OpenTelemetry là chuẩn đặc tả dữ liệu telemetry (Metrics, Logs, Traces) mở rộng nhất. |
| **Datadog / Dynatrace Agent** | `Commercial APM` | Giải pháp thương mại đắt đỏ; OpenTelemetry là mã nguồn mở chuẩn mực, tự do hạ tầng. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tiêu chuẩn công nghiệp số 1 thế giới được toàn bộ các hãng công nghệ lớn (Google, Microsoft, Amazon, Red Hat) hỗ trợ.**
- **Tự động lan truyền ngữ cảnh (Context Propagation) qua các giao thức HTTP, gRPC, Kafka.**
- **Cho phép xuất dữ liệu sang bất kỳ Backend APM nào (Jaeger, Zipkin, Prometheus, Grafana, Elastic).**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Tăng kích thước HTTP Header một lượng nhỏ để mang theo W3C Trace Context.
- Cần cấu hình tỉ lệ lấy mẫu (Sampling Rate) hợp lý để tránh tốn quá nhiều tài nguyên lưu trữ trace trên Production.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Bắt buộc phải có cho mọi kiến trúc Microservices để tìm và sửa lỗi hiệu năng xuyên hệ thống. KHÔNG NÊN DÙNG: Cho ứng dụng Monolith đơn lẻ chỉ có 1 tiến trình duy nhất (khi đó chỉ cần log thông thường).**

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

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 41-OpenTelemetry

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8141`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8141/api/otel/process` | 1. Kích hoạt Span, Manual Span và Baggage Context (@WithSpan, Tracer.spanBuilder) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

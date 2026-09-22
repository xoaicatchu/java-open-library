# 39-Logback - Logback + Logstash JSON Encoder

<p align="left">
  <img src="https://img.shields.io/badge/Port-8139-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Structured%20Logging-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Trong hệ thống xử lý hàng triệu request mỗi ngày, log dạng văn bản thuần túy (Text log) rải rác trên các file `.log` không thể tìm kiếm, không thể lọc theo mã đơn hàng hay mã người dùng. Khi hệ thống xảy ra lỗi, đội vận hành mất hàng giờ để đọc từng dòng log mà vẫn không tìm ra nguyên nhân.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Xuất log định dạng JSON có cấu trúc (Structured Logging) để đẩy trực tiếp vào ELK Stack (Elasticsearch, Logstash, Kibana) hoặc Grafana Loki.**
- **Sử dụng **MDC (Mapped Diagnostic Context)** để gán `traceId`, `userId`, `clientIp` xuyên suốt toàn bộ vòng đời của request.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Logback + Logstash JSON Encoder |
|:---|:---|:---|
| **Log4j2** | `High-Performance Logger` | Log4j2 có throughput ghi log bất đồng bộ bằng LMAX Disruptor rất nhanh; Logback là logger mặc định của Spring Boot ổn định và ít cấu hình hơn. |
| **Java Util Logging (JUL)** | `JVM Default` | JUL quá thô sơ, thiếu tính năng xoay vòng file (Rolling) và cấu hình format JSON phức tạp. |
| **Standard Console Plain Text** | `Basic Output` | Log text chỉ phù hợp cho môi trường local; Production bắt buộc phải dùng JSON có cấu trúc. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tích hợp sẵn mặc định trong mọi ứng dụng Spring Boot thông qua SLF4J facade.**
- **Logstash Logback Encoder tự động chuyển đổi mọi log statement thành JSON chuẩn xác, tự động đóng gói Exception Stacktrace.**
- **Hỗ trợ Rolling Policy thông minh (xoay vòng file theo ngày, nén tự động `.gz`, giới hạn tổng dung lượng lưu trữ).**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Thông lượng xử lý thô ở tải cực kỳ lớn (hàng triệu log/giây) chậm hơn một chút so với Async Appender của Log4j2 dùng LMAX Disruptor.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Tiêu chuẩn bắt buộc cho 100% ứng dụng chạy trên Production để tích hợp với hệ thống thu thập log tập trung. KHÔNG NÊN DÙNG: Không có lý do để bỏ qua Structured Logging trên môi trường doanh nghiệp.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/logback/controller/OrderController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/logback/service/OrderService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/logback/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8139
spring:
  application:
    name: logback-example
  threads:
    virtual:
      enabled: true
  profiles:
    active: dev
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 39-Logback

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8139`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8139/orders` | 1. Tạo đơn hàng (Ghi Structured JSON Log với MDC traceId và Logstash Encoder) |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

# 05-ProblemDetail - Problem Detail (RFC 9457)

<p align="left">
  <img src="https://img.shields.io/badge/Port-8105-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Web%20&%20Error%20Standards-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Mỗi lập trình viên hoặc mỗi service trong hệ thống tự chế một cấu trúc trả về lỗi khác nhau (`{"error": "..."}`, `{"code": 400, "msg": "..."}`). Điều này khiến Frontend, Mobile và API Gateway phải viết vô số logic parse lỗi ngoại lệ, gây khó khăn cho việc bảo trì.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Chuẩn hóa toàn bộ cấu trúc phản hồi lỗi của hệ thống Microservices theo tiêu chuẩn quốc tế RFC 9457 / RFC 7807.**
- **Đóng gói lỗi validation, lỗi bảo mật, lỗi nghiệp vụ kèm theo mã định danh duy nhất (`instance`) và link tài liệu lỗi (`type`).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Problem Detail (RFC 9457) |
|:---|:---|:---|
| **Custom ApiResponseWrapper** | `Ad-hoc Format` | Tự chế format làm mất tính chuẩn hóa quốc tế và không có sự hỗ trợ mặc định từ các thư viện HTTP client chuẩn. |
| **Google JSON Style Guide Error** | `Proprietary Format` | Format của Google khá tốt nhưng RFC 9457 là chuẩn mở của IETF được Spring Boot 3 hỗ trợ native. |
| **Default Spring Boot Error (/error)** | `Legacy Spring` | Default Spring Boot Error cũ thiếu các trường chuẩn như `instance`, `type` và khó mở rộng thuộc tính custom. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Được tích hợp sẵn từ Spring Boot 3.0+ thông qua class `ProblemDetail` và `@ControllerAdvice`.**
- **Chuẩn hóa quốc tế được các API Gateway và thư viện client tự động nhận diện.**
- **Dễ dàng mở rộng thêm các trường thông tin đặc thù (timestamp, correlationId, fieldErrors).**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cần cập nhật các client cũ nếu họ đang parse cấu trúc JSON lỗi đời cũ.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Là tiêu chuẩn bắt buộc cho 100% các ứng dụng Spring Boot 3.x mới. KHÔNG NÊN DÙNG: Chỉ khi bắt buộc phải tương thích ngược tuyệt đối với ứng dụng legacy không thể sửa code phía client.**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8105
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:orderdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
  messages:
    basename: messages
    encoding: UTF-8
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 05-ProblemDetail

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8105`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8105/api/orders` | Create Order |
| `POST` | `http://localhost:8105/api/orders` | Create Order with Out of Stock |
| `GET` | `http://localhost:8105/api/orders/999` | Get Order Not Found |
| `POST` | `http://localhost:8105/api/orders` | Validation Error |
| `POST` | `http://localhost:8105/api/orders/1/cancel` | Cancel Order |
| `POST` | `http://localhost:8105/api/orders/1/pay?reasonCode=DECLINED_BY_BANK` | Pay Order Declined |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

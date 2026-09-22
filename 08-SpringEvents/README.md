# 08-SpringEvents - Spring ApplicationEvent

> **Cổng dịch vụ (Server Port)**: `8108`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Sau khi đăng ký tài khoản thành công, muốn gửi email, tạo ví khuyến mãi, cộng điểm thưởng. Nếu viết chung trong 1 method service sẽ rất dài, vi phạm Single Responsibility.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Tách rời nghiệp vụ nội bộ bằng Event trong cùng 1 JVM. Đặc biệt dùng `@TransactionalEventListener(phase = AFTER_COMMIT)` để đảm bảo chỉ gửi mail khi DB đã commit thành công.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/events/controller/OrderController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/events/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/events/service/OrderService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/events/repository/AuditLogRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/events/repository/OrderRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/events/EventsApplication.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/events/controller/OrderController.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/events/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/events/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/events/entity/AuditLog.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/events/entity/Order.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/events/event/GenericDomainEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/events/event/OrderCancelledEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- *(và 8 classes dữ liệu khác)*

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8108
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:eventdb
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 08-SpringEvents

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8108`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8108/api/orders` | Create a new order |
| `POST` | `http://localhost:8108/api/orders/1/cancel` | Cancel an order |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

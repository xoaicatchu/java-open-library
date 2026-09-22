# 05-ProblemDetail - Problem Detail RFC 9457

> **Cổng dịch vụ (Server Port)**: `8105`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Mỗi lập trình viên tự chế một kiểu trả lỗi JSON khác nhau (`{"err": "..."}`, `{"code": 500, "message": "..."}`), gây khó khăn cho đội Frontend và Mobile khi parse lỗi.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Chuẩn hóa format lỗi quốc tế (RFC 9457) với đầy đủ `type`, `title`, `status`, `detail`, `instance`, hỗ trợ đa ngôn ngữ (i18n).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/problemdetail/controller/OrderController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/problemdetail/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/problemdetail/service/OrderService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/problemdetail/repository/OrderRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/problemdetail/dto/OrderItemRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/problemdetail/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/problemdetail/entity/Order.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/problemdetail/entity/OrderItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 05-ProblemDetail

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8105`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8105/api/orders` | Create Order |
| `POST` | `http://localhost:8105/api/orders` | Create Order with Out of Stock |
| `GET` | `http://localhost:8105/api/orders/999` | Get Order Not Found |
| `POST` | `http://localhost:8105/api/orders` | Validation Error |
| `POST` | `http://localhost:8105/api/orders/1/cancel` | Cancel Order |
| `POST` | `http://localhost:8105/api/orders/1/pay?reasonCode=DECLINED_BY_BANK` | Pay Order Declined |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

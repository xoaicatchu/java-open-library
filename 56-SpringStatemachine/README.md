# 56-SpringStatemachine - Spring Statemachine

> **Cổng dịch vụ (Server Port)**: `8156`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Trạng thái đơn hàng TMĐT (`CREATED -> PAID -> SHIPPING -> DELIVERED`) bị client gọi nhảy cóc (chưa thanh toán đã bấm giao hàng thành công).

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Mô hình máy trạng thái hữu hạn (FSM). Chặn đứng mọi hành vi chuyển trạng thái phi lý bằng Guard Condition và tự động thực thi Action khi trạng thái thay đổi.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/statemachine/controller/OrderController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/statemachine/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/statemachine/service/OrderService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/statemachine/repository/OrderRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/statemachine/config/StateMachineConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/statemachine/domain/OrderEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/statemachine/dto/HistoryDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/statemachine/dto/OrderDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/statemachine/entity/OrderEntity.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/statemachine/entity/StateHistory.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8156
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:orderdb;DB_CLOSE_DELAY=-1
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
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
cd 56-SpringStatemachine

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8156`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8156/api/orders?description=New%20Order` | Create Order |
| `POST` | `http://localhost:8156/api/orders/1/events?event=SUBMIT` | Submit Order |
| `POST` | `http://localhost:8156/api/orders/1/events?event=PAY` | Pay Order |
| `GET` | `http://localhost:8156/api/orders/1` | Get Order |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

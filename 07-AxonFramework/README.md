# 07-AxonFramework - Axon Framework

> **Cổng dịch vụ (Server Port)**: `8107`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Các giao dịch tài chính, ví điện tử cần lưu lại lịch sử biến động số dư tuyệt đối chính xác (không bao giờ được ghi đè `UPDATE account SET balance = ...`).

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Kiến trúc **Event Sourcing + CQRS**. Mọi thay đổi lưu dưới dạng chuỗi sự kiện bất biến (`MoneyDeposited`, `MoneyWithdrawn`). Quản lý Saga điều phối giao dịch phân tán giữa nhiều dịch vụ.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/axon/controller/AccountController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/axon/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/axon/query/AccountSummaryRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/axon/coreapi/AccountCreatedEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/axon/coreapi/BankCardIssuedEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/axon/coreapi/MoneyDepositedEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/axon/coreapi/MoneyWithdrawnEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8107
spring:
  application:
    name: axon-framework-demo
  datasource:
    url: jdbc:h2:mem:axondb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  h2:
    console:
      enabled: true
      path: /h2-console
  threads:
    virtual:
      enabled: true
axon:
  axonserver:
    enabled: false # Use in-memory event store instead of Axon Server for standalone
  serializer:
    general: jackson
    messages: jackson
    events: jackson
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 07-AxonFramework

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8107`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8107/accounts` | Create Account |
| `POST` | `http://localhost:8107/accounts/{{accountId}}/deposit` | Deposit Money |
| `POST` | `http://localhost:8107/accounts/{{accountId}}/withdraw` | Withdraw Money |
| `GET` | `http://localhost:8107/accounts/{{accountId}}` | Get Account |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

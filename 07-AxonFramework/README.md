# 07-AxonFramework - Axon Framework

<p align="left">
  <img src="https://img.shields.io/badge/Port-8107-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-CQRS%20&%20Event%20Sourcing-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Trong hệ thống tài chính, ngân hàng, ví điện tử, việc sử dụng lệnh `UPDATE account SET balance = ...` thông thường sẽ làm mất toàn bộ lịch sử biến động số dư, không thể kiểm toán (audit) khi xảy ra tranh chấp và rất khó giải quyết bài toán giao dịch phân tán giữa nhiều dịch vụ.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Sổ cái tài chính ngân hàng, ví điện tử, hệ thống chứng khoán áp dụng mô hình Event Sourcing.**
- **Điều phối các quy trình phân tán phức tạp (Saga Pattern) như đặt vé máy bay: giữ chỗ -> trừ tiền -> xuất vé -> hoàn tiền nếu lỗi.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Axon Framework |
|:---|:---|:---|
| **Eventuate Tram / Local** | `Event Sourcing Framework` | Eventuate tập trung vào transactional messaging; Axon hoàn chỉnh cả DDD, Aggregate, Event Store và Saga. |
| **Spring ApplicationEvent + Kafka** | `Custom CQRS` | Tự code tốn hàng tháng trời để xử lý Aggregate snapshotting, replay event, idempotency; Axon hỗ trợ out-of-the-box. |
| **Debezium (CDC)** | `Change Data Capture` | Debezium bắt sự kiện ở mức DB log; Axon bắt sự kiện ở mức Domain Intent (nghiệp vụ có chủ đích). |

### 🌟 Ưu điểm nổi bật (Pros)
- **Cung cấp giải pháp toàn diện cho CQRS (tách biệt luồng ghi Command và luồng đọc Query) và Event Sourcing.**
- **Tích hợp sẵn quản lý Saga điều phối giao dịch phân tán (Distributed Transactions).**
- **Khả năng Audit bất biến 100%: mọi trạng thái hiện tại đều có thể tái lập bằng cách replay các sự kiện trong quá khứ.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Độ phức tạp kiến trúc và chi phí triển khai rất cao.
- Mô hình Eventual Consistency (nhất quán sau cùng) đòi hỏi Frontend phải xử lý bất đồng bộ, không thể thấy dữ liệu mới ngay lập tức.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Các hệ thống FinTech, ngân hàng, bảo hiểm, logistics cần audit nghiêm ngặt và quản lý Saga phức tạp. KHÔNG NÊN DÙNG: Các ứng dụng CRUD phổ thông nơi mà việc ghi đè bản ghi đơn giản là đủ.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/axon/controller/AccountController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/axon/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/axon/query/AccountSummaryRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/axon/coreapi/AccountCreatedEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/axon/coreapi/BankCardIssuedEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/axon/coreapi/MoneyDepositedEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/axon/coreapi/MoneyWithdrawnEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 07-AxonFramework

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8107`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8107/accounts` | Create Account |
| `POST` | `http://localhost:8107/accounts/{{accountId}}/deposit` | Deposit Money |
| `POST` | `http://localhost:8107/accounts/{{accountId}}/withdraw` | Withdraw Money |
| `GET` | `http://localhost:8107/accounts/{{accountId}}` | Get Account |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

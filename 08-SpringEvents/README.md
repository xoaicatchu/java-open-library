# 08-SpringEvents - Spring ApplicationEvent

<p align="left">
  <img src="https://img.shields.io/badge/Port-8108-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-In-Memory%20Event%20Driven-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi hoàn tất một nghiệp vụ (ví dụ: đăng ký tài khoản thành công), nếu viết toàn bộ logic gửi mail, cộng điểm thưởng, thông báo vào chung 1 hàm Service sẽ vi phạm Single Responsibility Principle, làm hàm dài hàng trăm dòng, chậm chạp và dễ sập toàn bộ nếu 1 tác vụ phụ bị lỗi.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Tách rời các nghiệp vụ phụ trợ trong cùng 1 ứng dụng (In-JVM Decoupling).**
- **Sử dụng `@TransactionalEventListener(phase = AFTER_COMMIT)` để đảm bảo chỉ gửi email hoặc đẩy tin nhắn khi giao dịch DB đã commit thành công.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring ApplicationEvent |
|:---|:---|:---|
| **Guava EventBus** | `In-memory Bus` | Guava EventBus không tích hợp với Spring Transaction; Spring Events hỗ trợ liên kết chặt chẽ với vòng đời Transaction của Spring. |
| **Apache Kafka / RabbitMQ** | `External Message Broker` | Kafka/RabbitMQ dùng cho liên service qua mạng; Spring Events siêu nhẹ, chạy hoàn toàn trong RAM của 1 JVM. |
| **Reactive EventStreams** | `Reactive` | Spring Events trực quan và dễ tiếp cận hơn cho các tác vụ đồng bộ/bất đồng bộ nội bộ. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Cực kỳ nhẹ, không cần cài đặt thêm bất kỳ phần mềm hay broker bên ngoài nào.**
- **Hỗ trợ mạnh mẽ Transactional Listeners: ngăn chặn lỗi gửi email khi DB bị rollback.**
- **Có thể chuyển đổi dễ dàng từ xử lý đồng bộ sang bất đồng bộ bằng annotation `@Async`.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Chỉ hoạt động trong phạm vi bộ nhớ của 1 máy chủ (In-JVM); không truyền được sự kiện sang máy chủ khác.
- Nếu server bị tắt đột ngột (crash/restart), các event đang nằm trong hàng đợi RAM của `@Async` sẽ bị mất.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho việc phân tách logic nội bộ trong cùng 1 service Monolith hoặc Microservice. KHÔNG NÊN DÙNG: Để giao tiếp giữa các service phân tán qua mạng (khi đó bắt buộc phải dùng Kafka hoặc RabbitMQ).**

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

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 08-SpringEvents

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8108`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8108/api/orders` | Create a new order |
| `POST` | `http://localhost:8108/api/orders/1/cancel` | Cancel an order |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

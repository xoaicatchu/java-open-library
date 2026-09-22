# 11-SpringAMQP - Spring AMQP (RabbitMQ)

<p align="left">
  <img src="https://img.shields.io/badge/Port-8111-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Enterprise%20Message%20Broker-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Cần định tuyến tin nhắn thông minh theo nhiều tiêu chí (ví dụ: cảnh báo khẩn gửi hàng đợi ưu tiên, tin khuyến mãi gửi hàng đợi chậm), kèm cơ chế xác nhận xử lý (ACK) chặt chẽ để đảm bảo không một tác vụ ngầm nào bị mất mát.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Hàng đợi gửi Email/SMS thông báo, xuất báo cáo Excel nặng ngầm cho người dùng.**
- **Giao tiếp điểm-tới-điểm (RPC) hoặc định tuyến theo chủ đề (Topic / Direct / Fanout Exchange) giữa các microservice.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring AMQP (RabbitMQ) |
|:---|:---|:---|
| **Apache Kafka** | `Distributed Streaming` | Kafka tối ưu cho log streaming dung lượng lớn; RabbitMQ tối ưu cho tác vụ định tuyến phức tạp, hàng đợi ưu tiên và xác nhận ACK/NACK. |
| **ActiveMQ / Artemis** | `JMS Broker` | ActiveMQ theo chuẩn JMS cũ; RabbitMQ theo chuẩn AMQP hiện đại, linh hoạt và hiệu năng cao hơn. |
| **Redis Pub/Sub** | `In-Memory Pub/Sub` | Redis Pub/Sub không có cơ chế ACK và lưu trữ tin khi client offline; RabbitMQ đảm bảo tin cậy 100%. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Mô hình định tuyến Exchange - Binding - Queue cực kỳ linh hoạt (Direct, Fanout, Topic, Headers).**
- **Hỗ trợ đầy đủ Message ACK, NACK, Dead Letter Exchange (DLX), Priority Queues, TTL.**
- **Giao diện quản trị Web UI trực quan, dễ dàng theo dõi số lượng tin nhắn trong hàng đợi.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Throughput tối đa thấp hơn Apache Kafka (thường chỉ đạt hàng chục ngàn tin/giây).
- Tin nhắn sau khi consumer xác nhận ACK thành công sẽ bị xóa khỏi hàng đợi (không thể replay như Kafka).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho hệ thống xử lý tác vụ background, thông báo, đơn hàng cần định tuyến linh hoạt và bảo đảm xử lý từng tin. KHÔNG NÊN DÙNG: Cho việc thu thập log streaming hàng triệu event/giây (khi đó chọn Kafka).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/amqp/controller/NotificationController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/amqp/config/RabbitConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/amqp/dto/OrderNotification.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/amqp/dto/PaymentNotification.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8111
spring:
  threads:
    virtual:
      enabled: true
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          max-interval: 10000
          multiplier: 2.0
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 11-SpringAMQP

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8111`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8111/api/notifications/order` | Send Order Notification |
| `POST` | `http://localhost:8111/api/notifications/payment` | Send Payment Notification |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

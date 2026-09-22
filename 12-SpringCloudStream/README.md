# 12-SpringCloudStream - Spring Cloud Stream

<p align="left">
  <img src="https://img.shields.io/badge/Port-8112-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Messaging%20Abstraction-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi viết code giao tiếp với Kafka hoặc RabbitMQ, code nghiệp vụ thường bị gắn chặt vào API đặc thù của broker đó. Khi công ty muốn chuyển từ RabbitMQ sang Kafka (hoặc AWS SQS), lập trình viên phải sửa và test lại toàn bộ mã nguồn.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Viết các hàm xử lý luồng dữ liệu (Stream Processors) bằng Java 8 Function/Consumer thuần túy (`Function<Order, Invoice>`).**
- **Xây dựng ứng dụng microservices sẵn sàng chạy trên nhiều môi trường Cloud (chuyển đổi giữa RabbitMQ, Kafka, Azure Service Bus chỉ bằng file cấu hình).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Cloud Stream |
|:---|:---|:---|
| **Direct Spring Kafka / Spring AMQP** | `Native APIs` | Dùng native API tận dụng được 100% tính năng sâu của broker, nhưng code bị gắn cứng vào broker đó. |
| **Apache Camel** | `Integration Framework` | Camel hỗ trợ nhiều giao thức hơn nhưng cú pháp nặng hơn mô hình functional đơn giản của Cloud Stream. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Trừu tượng hóa hoàn toàn broker: Lập trình viên chỉ cần viết các Bean `java.util.function.Function`.**
- **Dễ dàng thay đổi broker từ RabbitMQ sang Kafka chỉ bằng cách đổi dependency maven và config YAML.**
- **Hỗ trợ sẵn cơ chế Partitioning, Consumer Groups, Retry và Dead Letter Queue độc lập với broker.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Khó tận dụng một số tính năng chuyên sâu riêng biệt của từng broker (như Kafka Compaction hay RabbitMQ Dead Letter Routing nâng cao).
- Thêm một tầng trừu tượng làm việc debug cấu hình ban đầu phức tạp hơn.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Khi muốn viết code xử lý stream dạng Function sạch sẽ, độc lập với hạ tầng broker. KHÔNG NÊN DÙNG: Khi cần khai thác tối đa các tính năng đặc thù cấp thấp của riêng Kafka hoặc RabbitMQ.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/cloudstream/controller/OrderController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/cloudstream/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/cloudstream/service/OrderService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/cloudstream/repository/OrderRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/cloudstream/config/StreamConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/cloudstream/dto/EnrichedOrderEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/cloudstream/dto/OrderEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/cloudstream/entity/OrderEntity.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8112
spring:
  threads:
    virtual:
      enabled: true
  application:
    name: cloudstream-demo
  datasource:
    url: jdbc:h2:mem:orderdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  cloud:
    stream:
      function:
        definition: enrichOrder;processOrder
      bindings:
        enrichOrder-in-0:
          destination: raw-orders
        enrichOrder-out-0:
          destination: enriched-orders
        processOrder-in-0:
          destination: enriched-orders
          group: process-group
          consumer:
            max-attempts: 1
      default:
        producer:
          partitionKeyExpression: payload.orderId
          partitionCount: 2
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 12-SpringCloudStream

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8112`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8112/api/orders` | Create an order via REST (StreamBridge) |
| `POST` | `http://localhost:8112/api/orders` | Create an order that should fail processing |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

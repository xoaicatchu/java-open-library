# 10-SpringKafka - Spring for Apache Kafka

<p align="left">
  <img src="https://img.shields.io/badge/Port-8110-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Distributed%20Event%20Streaming-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi lượng dữ liệu biến động cực lớn (hàng triệu click chuột, tọa độ GPS tài xế, biến động số dư), các cơ sở dữ liệu quan hệ hoặc Message Broker truyền thống sẽ bị nghẽn cổ chai I/O đĩa và không thể scale ngang việc đọc ghi dữ liệu có thứ tự.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Xương sống truyền thông điệp (Event-Driven Backbone) cho toàn bộ hệ thống Microservices của sàn thương mại điện tử, đặt xe công nghệ.**
- **Data Pipeline thu thập log và metrics đẩy về cụm xử lý dữ liệu lớn (ClickHouse, Hadoop, ElasticSearch).**
- **Xử lý lỗi tin nhắn bằng mô hình Dead Letter Topic (DLT) và Non-blocking Retry topics.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring for Apache Kafka |
|:---|:---|:---|
| **RabbitMQ** | `Message Broker` | RabbitMQ thiên về định tuyến phức tạp và hàng đợi tác vụ; Kafka vượt trội về throughput hàng triệu tin/giây và khả năng lưu trữ replay tin nhắn. |
| **Apache Pulsar** | `Next-Gen Streaming` | Pulsar tách biệt tính toán và lưu trữ rất hiện đại, nhưng Kafka có hệ sinh thái và cộng đồng hỗ trợ lớn hơn gấp nhiều lần. |
| **Redis Streams** | `In-Memory Stream` | Redis Streams nhẹ nhàng hơn nhưng giới hạn bởi dung lượng RAM; Kafka lưu trữ bền bỉ trên đĩa cứng phân tán. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Throughput cực khủng (hàng triệu messages/giây) nhờ cơ chế Sequential I/O và Zero-Copy.**
- **Lưu trữ dữ liệu phân tán bền bỉ theo thời gian: Consumer có thể replay (đọc lại) dữ liệu từ quá khứ.**
- **Spring Kafka cung cấp `@KafkaListener`, `KafkaTemplate`, và bộ xử lý lỗi Retry/DLT cực mạnh.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Vận hành cụm Kafka cluster thực tế (Zookeeper/KRaft, Partitions, Rebalancing) rất phức tạp.
- Không hỗ trợ định tuyến tin nhắn linh hoạt theo wildcard như RabbitMQ Exchange.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho hệ thống có lượng traffic khổng lồ, luồng event bất biến, data pipeline, audit log. KHÔNG NÊN DÙNG: Cho các tác vụ hàng đợi đơn giản (background job) hoặc khi không có đội ngũ DevOps vận hành Kafka.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/kafka/controller/PriceController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/kafka/repository/ProductPriceRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/kafka/config/KafkaConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/kafka/dto/PriceUpdateEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/kafka/entity/ProductPrice.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8110
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.add.type.headers: true
    consumer:
      group-id: price-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
    listener:
      ack-mode: manual
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 10-SpringKafka

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8110`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8110/api/prices` | Send single price update |
| `POST` | `http://localhost:8110/api/prices/batch` | Send batch of price updates |
| `POST` | `http://localhost:8110/api/prices` | Send invalid price update (should go to DLT) |
| `GET` | `http://localhost:8110/api/prices/101` | Get a specific product price (wait a moment for Kafka consumer to process) |
| `GET` | `http://localhost:8110/api/prices` | Get all product prices |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

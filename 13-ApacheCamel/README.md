# 13-ApacheCamel - Apache Camel

<p align="left">
  <img src="https://img.shields.io/badge/Port-8113-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Enterprise%20Integration%20Patterns-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Cần tích hợp dữ liệu giữa nhiều hệ thống không đồng nhất: Lấy file từ FTP, giải nén, parse nội dung XML, gọi REST API làm giàu dữ liệu, sau đó ghi vào Database và gửi thông báo qua Telegram. Tự viết code cho luồng này tốn hàng ngàn dòng code vụn vặt và rất dễ lỗi.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Tích hợp hệ thống ngân hàng Core Banking cũ với các cổng thanh toán ví điện tử hiện đại.**
- **Xây dựng các pipeline ETL tự động: quét thư mục, chuyển đổi định dạng dữ liệu (CSV -> JSON -> XML), định tuyến theo nội dung.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Apache Camel |
|:---|:---|:---|
| **Spring Integration** | `Spring EIP` | Spring Integration nhẹ hơn; Camel có hệ sinh thái hơn 300+ adapter kết nối mọi công nghệ trên đời. |
| **Apache NiFi** | `Dataflow System` | NiFi cấu hình bằng Web UI trực quan cho Data Engineering; Camel nhúng trực tiếp vào code Java của lập trình viên. |
| **MuleSoft** | `Commercial ESB` | MuleSoft thương mại chi phí rất lớn; Camel hoàn toàn mã nguồn mở miễn phí. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Hơn 300 components kết nối sẵn: từ AWS, Azure, Kafka, FTP, JDBC, Mail, Salesforce đến Telegram, Twitter.**
- **Cung cấp Fluent Java DSL và XML/YAML DSL cực kỳ biểu cảm, mô tả luồng tích hợp như một bản vẽ kiến trúc.**
- **Khả năng chuyển đổi định dạng dữ liệu (Data Transformation) siêu mạnh mẽ.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Hệ thống khái niệm (Exchange, Message, Body, Headers) đồ sộ cần thời gian làm quen.
- Có thể làm ứng dụng trở nên nặng nề nếu chỉ dùng cho các nhu cầu tích hợp đơn giản.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các dự án tích hợp hệ sinh thái đa hệ thống phức tạp (Enterprise Integration, Core Banking, Logistics). KHÔNG NÊN DÙNG: Cho các tác vụ gọi API hoặc đọc ghi dữ liệu thông thường trong phạm vi 1 microservice.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/camel/controller/OrderController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/camel/dto/OrderItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/camel/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/camel/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8113
spring:
  threads:
    virtual:
      enabled: true
  application:
    name: apache-camel-demo
camel:
  springboot:
    name: OrderCamelApp
  servlet:
    mapping:
      context-path: /api/*
  rest:
    component: servlet
    binding-mode: json
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 13-ApacheCamel

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8113`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8113/api/camel/orders` | Standard Order |
| `POST` | `http://localhost:8113/api/camel/orders` | Express Order |
| `POST` | `http://localhost:8113/api/camel/orders` | International Order |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

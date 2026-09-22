# 09-SpringIntegration - Spring Integration

<p align="left">
  <img src="https://img.shields.io/badge/Port-8109-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Enterprise%20Integration%20Patterns-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi cần kết nối với các hệ thống cũ (legacy systems) qua các giao thức cổ điển (quét file SFTP định kỳ, đọc hòm thư mail POP3, lắng nghe cổng socket TCP), nếu tự viết code kết nối và xử lý luồng sẽ rất dễ phát sinh lỗi rò rỉ kết nối, mất đồng bộ và không có cơ chế xử lý lỗi chuẩn mực.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Áp dụng các mẫu tích hợp doanh nghiệp (EIP - Enterprise Integration Patterns): Splitter, Aggregator, Content-Based Router.**
- **Xây dựng các luồng ETL nhỏ: tự động quét thư mục FTP -> đọc file CSV -> validate -> đẩy vào database.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Integration |
|:---|:---|:---|
| **Apache Camel** | `Integration Framework` | Camel có hệ sinh thái component rộng lớn hơn (300+ adapters); Spring Integration nhẹ hơn và tích hợp tự nhiên với Spring Boot. |
| **MuleSoft ESB** | `Enterprise ESB` | MuleSoft là giải pháp thương mại đắt đỏ; Spring Integration là mã nguồn mở miễn phí, nhúng trực tiếp vào code Java. |
| **Custom Poller Scripts** | `Ad-hoc Scripts` | Tự viết script dễ gặp lỗi concurrency và không có cơ chế retry/dead-letter chuẩn mực. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Triển khai chuẩn mực các mẫu EIP kinh điển của Gregor Hohpe.**
- **Tích hợp hoàn hảo với hệ sinh thái Spring (Spring Messaging, Spring Data, Spring Security).**
- **Cung cấp Java DSL và IntegrationFlow giúp định nghĩa luồng dữ liệu mượt mà, dễ đọc.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Số lượng adapter kết nối với các hệ thống bên ngoài ít hơn so với Apache Camel.
- Cấu hình luồng tích hợp phức tạp có thể trở nên khó debug khi dữ liệu đi qua quá nhiều kênh trung gian.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Khi dự án thuần Spring cần kết nối các kênh tin nhắn, file, mail, TCP đơn giản đến trung bình. KHÔNG NÊN DÙNG: Khi cần tích hợp đa giao thức phức tạp với SAP, Salesforce, AS400... (khi đó Apache Camel là lựa chọn tối ưu).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/integration/controller/OrderController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/integration/config/IntegrationConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/integration/dto/OrderItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/integration/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/integration/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/integration/dto/ProcessedItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8109
spring:
  application:
    name: spring-integration-demo
  threads:
    virtual:
      enabled: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 09-SpringIntegration

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8109`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8109/api/orders` | 1. Gửi đơn hàng Standard (EIP: Gateway -> Router -> Standard Pipeline -> Aggregator) |
| `POST` | `http://localhost:8109/api/orders` | 2. Gửi đơn hàng Express (EIP: Gateway -> Router -> Express Pipeline with Priority Discount) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

# 09-SpringIntegration - Spring Integration

> **Cổng dịch vụ (Server Port)**: `8109`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần tích hợp với hệ thống cũ qua giao thức cổ điển: quét file CSV trên SFTP server định kỳ, đọc mail POP3/IMAP, hoặc lắng nghe socket TCP thô.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Áp dụng mẫu EIP (Enterprise Integration Patterns) giải quyết bài toán ETL, kết nối với cổng thanh toán ngân hàng cũ, máy ATM, thiết bị ngoại vi.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/integration/controller/OrderController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/integration/config/IntegrationConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/integration/dto/OrderItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/integration/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/integration/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/integration/dto/ProcessedItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 09-SpringIntegration

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8109`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8109/api/orders` | 1. Gửi đơn hàng Standard (EIP: Gateway -> Router -> Standard Pipeline -> Aggregator) |
| `POST` | `http://localhost:8109/api/orders` | 2. Gửi đơn hàng Express (EIP: Gateway -> Router -> Express Pipeline with Priority Discount) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

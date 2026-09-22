# 13-ApacheCamel - Apache Camel

> **Cổng dịch vụ (Server Port)**: `8113`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần chuyển đổi dữ liệu phức tạp: Lấy file XML từ thư mục -> parse thành JSON -> chia nhỏ (Splitter) -> gọi 3 API khác nhau làm giàu dữ liệu -> tổng hợp lại (Aggregator) -> bắn vào DB.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Tích hợp các hệ thống Core Banking, bảo hiểm, ERP (SAP/Salesforce). Camel có sẵn hơn 300 adapter kết nối mọi thứ.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/camel/controller/OrderController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/camel/dto/OrderItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/camel/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/camel/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 13-ApacheCamel

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8113`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8113/api/camel/orders` | Standard Order |
| `POST` | `http://localhost:8113/api/camel/orders` | Express Order |
| `POST` | `http://localhost:8113/api/camel/orders` | International Order |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

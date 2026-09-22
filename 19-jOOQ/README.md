# 19-jOOQ - jOOQ

> **Cổng dịch vụ (Server Port)**: `8119`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Viết SQL thuần trong chuỗi String dễ gõ sai tên cột (runtime mới phát hiện), trong khi Hibernate không hỗ trợ tốt các cú pháp SQL hiện đại (Window Function, CTE, JSON aggregation).

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Hệ thống phân tích dữ liệu, Data Warehouse, báo cáo BI. jOOQ cho phép viết SQL bằng code Java Type-Safe, phát hiện lỗi cú pháp ngay lúc Compile.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/jooq/controller/AnalyticsController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/jooq/service/AnalyticsService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/jooq/dto/CategoryRevenueDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/jooq/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/jooq/dto/ProductRankDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/jooq/dto/SaleDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8119
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
    password:
  sql:
    init:
      mode: always
  jooq:
    sql-dialect: H2
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 19-jOOQ

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8119`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8119/api/analytics/products` | Get all products |
| `GET` | `http://localhost:8119/api/analytics/revenue` | Get revenue by category |
| `GET` | `http://localhost:8119/api/analytics/ranking` | Get product ranking by revenue |
| `GET` | `http://localhost:8119/api/analytics/top` | Get top performing products |
| `POST` | `http://localhost:8119/api/analytics/sales/batch` | Batch insert sales |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

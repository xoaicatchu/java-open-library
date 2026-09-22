# 48-Testcontainers - Testcontainers

<p align="left">
  <img src="https://img.shields.io/badge/Port-8148-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Integration%20Testing%20&%20Real%20Environments-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Viết bài test tích hợp (Integration Test) sử dụng CSDL giả lập trong bộ nhớ (như H2 Database) chạy test rất ngon lành, nhưng khi deploy lên Production gặp PostgreSQL hoặc Oracle thật thì bị lỗi do cú pháp SQL đặc thù, hàm JSON hoặc chỉ mục không tương thích.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Tự động khởi động các Docker Container thật (PostgreSQL, MySQL, Redis, Apache Kafka) ngay trong quá trình chạy `mvn test`.**
- **Đảm bảo môi trường kiểm thử tích hợp giống hệt 100% môi trường Production thực tế, xóa bỏ tình trạng 'chạy ở máy tôi ngon mà lên server lại lỗi'.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Testcontainers |
|:---|:---|:---|
| **H2 Database / Embedded Kafka** | `In-Memory Simulation` | H2/Embedded chỉ là giả lập, nhiều tính năng SQL/Broker thật không có; Testcontainers chạy chính xác phiên bản Docker thật. |
| **Shared Test Server** | `External Test Env` | Dùng chung server test dễ bị xung đột dữ liệu giữa các lập trình viên; Testcontainers tạo môi trường cô lập tạm thời và tự hủy sau khi test xong. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Độ tin cậy tuyệt đối: ứng dụng được kiểm thử trên chính phiên bản database/broker thật sẽ chạy trên Production.**
- **Tự động quản lý vòng đời Container: khởi động trước khi test và tự động dọn dẹp sạch sẽ (Ryuk container) sau khi test xong.**
- **Tích hợp tuyệt vời với Spring Boot 3.1+ qua `@ServiceConnection` giúp tự động cấu hình DataSource không cần gõ URL.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Thời gian chạy test lần đầu sẽ lâu hơn do phải tải Docker Image về máy.
- Yêu cầu máy lập trình viên và máy chủ CI/CD phải cài đặt Docker Daemon.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Bắt buộc phải có cho các bài kiểm thử tích hợp (Integration Test) kiểm tra tương tác với Database, Cache và Message Broker. KHÔNG NÊN DÙNG: Cho các bài Unit Test thuần túy (khi đó chỉ dùng Mockito để chạy siêu tốc).**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: 48-testcontainers
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password: password
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
  data:
    redis:
      host: localhost
      port: 6379
server:
  port: 8148
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 48-Testcontainers

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8148`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8148/api/products` | Get all products |
| `POST` | `http://localhost:8148/api/products` | Create a product |
| `GET` | `http://localhost:8148/api/products/1` | Get product by ID |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

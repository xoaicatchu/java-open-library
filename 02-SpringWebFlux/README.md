# 02-SpringWebFlux - Spring WebFlux

<p align="left">
  <img src="https://img.shields.io/badge/Port-8102-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Web%20&%20Reactive%20Transport-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Mô hình Thread-per-request truyền thống giữ chặt thread trong khi chờ I/O từ Database hoặc Third-party API. Khi có hàng trăm ngàn kết nối streaming hoặc WebSocket đồng thời, server sẽ cạn kiệt thread, đẩy chi phí context-switching lên cao và gây sập hệ thống vì tràn bộ nhớ.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Bảng giá chứng khoán thời gian thực, bảng tỷ lệ cá cược thể thao qua Server-Sent Events (SSE).**
- **Cổng trung chuyển API Gateway (Spring Cloud Gateway) tiếp nhận và định tuyến hàng triệu request.**
- **Hệ thống phân phối Push Notification hàng loạt tới hàng triệu thiết bị di động.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring WebFlux |
|:---|:---|:---|
| **Spring Web MVC (Virtual Threads)** | `Loom Sync` | Virtual Threads cho phép viết code đồng bộ nhưng chịu tải cao; WebFlux chuyên sâu hơn về streaming và backpressure. |
| **Vert.x** | `Reactive Toolkit` | Vert.x có throughput cao hơn một chút nhưng Spring WebFlux có hệ sinh thái Dependency Injection và Spring Boot hoàn chỉnh hơn. |
| **Quarkus Reactive** | `Cloud-Native` | Quarkus khởi động nhanh hơn, nhưng WebFlux quen thuộc hơn với cộng đồng Spring. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Kiến trúc Non-blocking I/O (dựa trên Project Reactor & Netty) tối ưu hóa tối đa việc sử dụng CPU và RAM.**
- **Hỗ trợ cơ chế Backpressure: Consumer kiểm soát tốc độ phát của Producer, chống nghẽn bộ nhớ đệm.**
- **Hỗ trợ streaming dữ liệu thời gian thực native (Flux<T>).**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Độ dốc học tập (learning curve) rất lớn: tư duy xử lý luồng Reactive phức tạp.
- Rất khó debug do luồng thực thi bị phân tán qua nhiều Event Loop threads.
- Nếu vô tình gọi một thư viện chặn luồng (blocking JDBC, Thread.sleep) sẽ làm tê liệt toàn bộ Event Loop.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cổng API Gateway, dịch vụ streaming SSE/WebSocket, hệ thống IoT/Telemetry tải kết nối cực lớn. KHÔNG NÊN DÙNG: Cho các ứng dụng CRUD thông thường phụ thuộc vào thư viện blocking JDBC/Hibernate truyền thống.**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8102
spring:
  application:
    name: spring-webflux-demo
  r2dbc:
    url: r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
  sql:
    init:
      mode: always
  threads:
    virtual:
      enabled: true
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 02-SpringWebFlux

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8102`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8102/api/products` | Get all products |
| `GET` | `http://localhost:8102/api/products/1` | Get product by ID |
| `GET` | `http://localhost:8102/api/products/4` | Get product by ID (Not Found) |
| `POST` | `http://localhost:8102/api/products` | Create new product |
| `GET` | `http://localhost:8102/functional/products` | Get all products (Functional Endpoint) |
| `GET` | `http://localhost:8102/api/products/stream` | SSE Stream |
| `GET` | `http://localhost:8102/api/products/backpressure` | Backpressure Demo |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

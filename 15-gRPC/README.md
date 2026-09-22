# 15-gRPC - gRPC Java

<p align="left">
  <img src="https://img.shields.io/badge/Port-8115-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-High-Performance%20RPC-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Giao tiếp nội bộ giữa hàng trăm microservice bằng REST JSON quá chậm: tốn CPU để tuần hoàn/giải mã văn bản (serialize/deserialize JSON), kích thước payload lớn và bị giới hạn bởi kết nối HTTP/1.1 tuần tự.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Giao tiếp microservice-to-microservice với độ trễ cực thấp (Low-Latency Inter-Service Communication).**
- **Truyền tải luồng dữ liệu hai chiều thời gian thực (Bidirectional Streaming) giữa các cụm xử lý dữ liệu.**
- **Kiến trúc đa ngôn ngữ (Polyglot): dịch vụ Java gọi dịch vụ Go, Python, C++ dùng chung 1 file hợp đồng `.proto`.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với gRPC Java |
|:---|:---|:---|
| **REST API (JSON)** | `Standard HTTP API` | REST JSON dễ đọc cho con người nhưng chậm hơn; gRPC nhị phân nhanh hơn gấp 5 - 10 lần và tiết kiệm băng thông. |
| **Apache Thrift** | `Binary RPC` | Thrift tương tự gRPC nhưng gRPC dựa trên chuẩn HTTP/2 hiện đại và có sự hậu thuẫn mạnh mẽ từ Google. |
| **RSocket** | `Reactive Protocol` | RSocket rất xuất sắc cho Reactive Streams nhưng gRPC phổ biến hơn trong ngành công nghiệp. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tốc độ cực nhanh và payload siêu nhỏ nhờ mã hóa nhị phân Google Protocol Buffers.**
- **Chạy trên nền tảng HTTP/2: hỗ trợ Multiplexing (nhiều request trên 1 TCP connection) và Streaming 2 chiều.**
- **Khai báo hợp đồng Interface chặt chẽ (`.proto`), tự động sinh code client/server type-safe cho mọi ngôn ngữ.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không thể đọc trực tiếp bằng mắt (dạng nhị phân), khó debug bằng cURL hoặc trình duyệt thông thường.
- Trình duyệt web giao tiếp trực tiếp với gRPC cần có thêm tầng gRPC-Web proxy.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Giao tiếp nội bộ giữa các microservices, hệ thống tài chính yêu cầu độ trễ sub-millisecond. KHÔNG NÊN DÙNG: Cho các Public API hướng tới Client ngoài (Web Browser, Third-party) nơi REST/JSON vẫn là vua.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/grpc/controller/ProductRestController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/grpc/service/ProductGrpcService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8115
spring:
  threads:
    virtual:
      enabled: true
grpc:
  server:
    port: 9090
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 15-gRPC

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8115`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8115/api/products/ping` | 1. Kiểm tra REST API endpoint |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

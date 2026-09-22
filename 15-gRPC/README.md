# 15-gRPC - gRPC-Java

> **Cổng dịch vụ (Server Port)**: `8115`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Giao tiếp nội bộ giữa hàng trăm Microservice bằng REST JSON quá chậm vì tốn thời gian parse text và dung lượng payload lớn.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Giao tiếp microservice-to-microservice với độ trễ siêu thấp (Low Latency). Dùng Protobuf nhị phân, HTTP/2 multiplexing, hỗ trợ streaming 2 chiều (Bidirectional Streaming).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/grpc/controller/ProductRestController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/grpc/service/ProductGrpcService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 15-gRPC

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8115`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8115/api/products/ping` | 1. Kiểm tra REST API endpoint |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

# 02-SpringWebFlux - Spring WebFlux

> **Cổng dịch vụ (Server Port)**: `8102`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần giữ hàng trăm ngàn kết nối mở liên tục (streaming, push event) mà tốn cực ít RAM/Thread.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Hệ thống bảng giá chứng khoán thời gian thực (SSE), cổng Push Notification, Streaming Log/Telemetry, hoặc các API Gateway trung chuyển.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/webflux/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/webflux/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/webflux/handler/ProductHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/webflux/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/webflux/service/WebClientDemoService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/webflux/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/webflux/config/RouterConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/webflux/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/webflux/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 02-SpringWebFlux

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8102`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8102/api/products` | Get all products |
| `GET` | `http://localhost:8102/api/products/1` | Get product by ID |
| `GET` | `http://localhost:8102/api/products/4` | Get product by ID (Not Found) |
| `POST` | `http://localhost:8102/api/products` | Create new product |
| `GET` | `http://localhost:8102/functional/products` | Get all products (Functional Endpoint) |
| `GET` | `http://localhost:8102/api/products/stream` | SSE Stream |
| `GET` | `http://localhost:8102/api/products/backpressure` | Backpressure Demo |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

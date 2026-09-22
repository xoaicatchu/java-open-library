# 21-SpringDataR2DBC - Spring Data R2DBC

> **Cổng dịch vụ (Server Port)**: `8121`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Sử dụng WebFlux nhưng JDBC driver truyền thống lại chặn luồng (blocking I/O), làm mất tác dụng của Reactive.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Đọc ghi CSDL quan hệ (PostgreSQL, MySQL) hoàn toàn bất đồng bộ (Non-blocking), phù hợp với hệ thống chịu tải kết nối cực lớn.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/r2dbc/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/r2dbc/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/r2dbc/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/r2dbc/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/r2dbc/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8121
spring:
  threads:
    virtual:
      enabled: true
  r2dbc:
    url: r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 21-SpringDataR2DBC

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8121`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8121/products` | 1. Tạo sản phẩm mới (Reactive Mono<ProductDto>) |
| `GET` | `http://localhost:8121/products/1` | 2. Lấy sản phẩm theo ID (Mono) |
| `GET` | `http://localhost:8121/products/paged?page=0&size=5` | 3. Lấy danh sách sản phẩm phân trang (Flux) |
| `GET` | `http://localhost:8121/products/low-stock?threshold=20` | 4. Tìm sản phẩm tồn kho thấp (@Query Reactive) |
| `GET` | `http://localhost:8121/products/price-range?min=50&max=200` | 5. Tìm theo khoảng giá (DatabaseClient fluent query) |
| `PUT` | `http://localhost:8121/products/1/stock?stock=200` | 6. Cập nhật tồn kho (DatabaseClient update fluent) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

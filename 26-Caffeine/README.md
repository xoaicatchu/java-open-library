# 26-Caffeine - Caffeine Cache

> **Cổng dịch vụ (Server Port)**: `8126`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Dữ liệu danh mục, cấu hình hệ thống, tỉ giá đọc liên tục hàng triệu lần nhưng rất ít khi đổi. Đọc DB liên tục làm DB quá tải.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Local In-Memory Cache nhanh nhất thế giới cho JVM (thuật toán W-TinyLFU tối ưu tỉ lệ hit cache). Tốc độ đọc Nano-giây ngay trong RAM máy chủ.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/caffeine/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/caffeine/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/caffeine/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/caffeine/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/caffeine/config/CacheConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/caffeine/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/caffeine/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: caffeine-demo
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  threads:
    virtual:
      enabled: true
server:
  port: 8126
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 26-Caffeine

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8126`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8126/api/products` | 1. Tạo sản phẩm mới |
| `GET` | `http://localhost:8126/api/products/spring/1` | 2. Lấy sản phẩm qua Spring Cache Abstraction (@Cacheable) |
| `GET` | `http://localhost:8126/api/products/manual/1` | 3. Lấy sản phẩm qua Manual Cache (Cache<K,V>) |
| `GET` | `http://localhost:8126/api/products/loading/1` | 4. Lấy sản phẩm qua LoadingCache (tự động load khi miss) |
| `GET` | `http://localhost:8126/api/products/async/1` | 5. Lấy sản phẩm qua AsyncLoadingCache (CompletableFuture) |
| `DELETE` | `http://localhost:8126/api/products/manual/1` | 6. Xóa cache thủ công |
| `GET` | `http://localhost:8126/api/products/manual/stats` | 7. Xem thống kê Cache (Hit rate, Miss rate, Eviction count) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

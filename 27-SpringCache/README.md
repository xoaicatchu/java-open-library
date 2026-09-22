# 27-SpringCache - Spring Cache Abstraction

> **Cổng dịch vụ (Server Port)**: `8127`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Không muốn code nghiệp vụ dính chặt vào API của thư viện cache cụ thể, muốn tái sử dụng qua annotation đơn giản.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Sử dụng `@Cacheable`, `@CachePut`, `@CacheEvict`. Cho phép kết hợp 2 tầng cache (Caffeine cho L1, Redis cho L2) chỉ bằng cấu hình.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/springcache/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/springcache/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/springcache/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/springcache/config/CacheConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/springcache/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springcache/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8127
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password: 
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
  cache:
    cache-names: products, longProducts
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 27-SpringCache

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8127`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8127/api/products` | Create a new product |
| `GET` | `http://localhost:8127/api/products/1?locale=vi` | Get the product - Should miss cache and query DB first time |
| `GET` | `http://localhost:8127/api/products/1?locale=vi` | Get the product again - Should hit cache (check application logs to verify DB is not queried) |
| `PUT` | `http://localhost:8127/api/products/1` | Update the product - Should update DB and cache (CachePut) |
| `GET` | `http://localhost:8127/api/products/1?locale=vi` | Get the product again - Should return updated price directly from cache |
| `DELETE` | `http://localhost:8127/api/products/1/cache` | Evict specific product from cache (CacheEvict) |
| `GET` | `http://localhost:8127/api/products/1?locale=vi` | Get the product - Should miss cache after eviction and query DB |
| `DELETE` | `http://localhost:8127/api/products/cache/all` | Evict all products from cache |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

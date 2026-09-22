# 27-SpringCache - Spring Cache Abstraction

<p align="left">
  <img src="https://img.shields.io/badge/Port-8127-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Caching%20Abstraction-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Nếu viết code gọi trực tiếp thư viện cache (RedisTemplate, Caffeine API) rải rác khắp các service, mã nguồn nghiệp vụ sẽ bị ô nhiễm bởi code kỹ thuật kiểm tra `cache.get()`, `cache.put()`, gây khó khăn cho việc bảo trì hoặc đổi công nghệ cache sau này.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Trong suốt hóa việc lưu và xóa cache bằng các annotation `@Cacheable`, `@CachePut`, `@CacheEvict`.**
- **Xây dựng kiến trúc Multi-Tier Caching (Cache 2 tầng): Tầng 1 Caffeine siêu nhanh trong RAM, tầng 2 Redis chia sẻ giữa các cụm server.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Cache Abstraction |
|:---|:---|:---|
| **Direct RedisTemplate API** | `Native Client` | Gọi trực tiếp kiểm soát được 100% lệnh Redis nhưng code bị gắn chặt vào Redis; Spring Cache trừu tượng hóa bằng annotation sạch sẽ. |
| **Redisson Caching** | `Distributed Cache` | Redisson có sẵn các tính năng phân tán mạnh mẽ; Spring Cache trừu tượng hóa chuẩn giúp hoán đổi linh hoạt giữa Caffeine/Redis. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Code nghiệp vụ hoàn toàn sạch sẽ, chỉ cần gắn annotation `@Cacheable("products")` lên trên phương thức.**
- **Dễ dàng chuyển đổi backend cache từ ConcurrenMap sang Caffeine, Redis, Ehcache chỉ bằng việc đổi cấu hình.**
- **Hỗ trợ điều kiện cache linh hoạt bằng biểu thức SpEL: `@Cacheable(condition = "#id > 10", unless = "#result == null")`.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Chỉ hoạt động khi gọi phương thức từ bên ngoài Bean (do cơ chế Spring Dynamic Proxy; gọi nội bộ hàm trong cùng class sẽ mất tác dụng).
- Khó thực hiện các thao tác xử lý dữ liệu phức tạp của Redis (như Bitmaps, HyperLogLog, Sorted Sets).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các thao tác đọc/ghi cache theo cặp Key-Value chuẩn mực trong nghiệp vụ hàng ngày. KHÔNG NÊN DÙNG: Khi cần khai thác các cấu trúc dữ liệu chuyên biệt của Redis hoặc cần kiểm soát chi tiết từng câu lệnh Redis I/O.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/springcache/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/springcache/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/springcache/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/springcache/config/CacheConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/springcache/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springcache/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 27-SpringCache

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8127`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
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

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

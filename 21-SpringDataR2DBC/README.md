# 21-SpringDataR2DBC - Spring Data R2DBC

<p align="left">
  <img src="https://img.shields.io/badge/Port-8121-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Reactive%20Data%20Access-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi xây dựng ứng dụng theo kiến trúc Reactive WebFlux, việc sử dụng JDBC driver truyền thống sẽ chặn luồng (blocking I/O) khi truy vấn cơ sở dữ liệu, phá vỡ hoàn toàn lợi thế non-blocking của toàn bộ chuỗi xử lý và gây nghẽn cổ chai nghiêm trọng.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Hệ thống ứng dụng Reactive toàn trình (End-to-End Reactive: từ WebFlux -> Service -> R2DBC Database).**
- **Ứng dụng IoT hoặc vi dịch vụ có hàng chục ngàn kết nối đồng thời với lượng thao tác đọc ghi CSDL quan hệ cao.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Data R2DBC |
|:---|:---|:---|
| **Spring Data JPA (Blocking JDBC)** | `Traditional ORM` | JPA chặn luồng; R2DBC hoàn toàn Non-blocking trả về `Mono<T>` và `Flux<T>`. |
| **jOOQ Reactive** | `Reactive Type-Safe SQL` | jOOQ hỗ trợ R2DBC driver nhưng cú pháp phức tạp hơn; Spring Data R2DBC tích hợp sẵn Repository quen thuộc. |
| **Vert.x SQL Client** | `Reactive Client` | Vert.x SQL Client có tốc độ thô rất cao nhưng Spring Data R2DBC tích hợp mượt hơn vào hệ sinh thái Spring Data. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Truy cập CSDL quan hệ (PostgreSQL, MySQL, H2) hoàn toàn bất đồng bộ, không làm nghẽn Event Loop.**
- **Tiết kiệm tài nguyên bộ nhớ và thread connection pool hơn nhiều so với JDBC HikariCP truyền thống.**
- **Cung cấp `R2dbcEntityTemplate` và `ReactiveCrudRepository` quen thuộc với lập trình viên Spring.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không hỗ trợ các tính năng ORM phức tạp: không có Lazy Loading, không có Dirty Checking, không có tự động ánh xạ quan hệ bảng phức tạp.
- Hệ sinh thái driver R2DBC chưa hoàn thiện bằng JDBC (vẫn đang tiếp tục trưởng thành).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Khi toàn bộ ứng dụng được xây dựng trên Spring WebFlux và cần tương tác với CSDL quan hệ. KHÔNG NÊN DÙNG: Khi dự án dùng Spring Web MVC đồng bộ hoặc cần các tính năng ánh xạ quan hệ thực thể phức tạp của Hibernate.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/r2dbc/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/r2dbc/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/r2dbc/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/r2dbc/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/r2dbc/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 21-SpringDataR2DBC

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8121`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8121/products` | 1. Tạo sản phẩm mới (Reactive Mono<ProductDto>) |
| `GET` | `http://localhost:8121/products/1` | 2. Lấy sản phẩm theo ID (Mono) |
| `GET` | `http://localhost:8121/products/paged?page=0&size=5` | 3. Lấy danh sách sản phẩm phân trang (Flux) |
| `GET` | `http://localhost:8121/products/low-stock?threshold=20` | 4. Tìm sản phẩm tồn kho thấp (@Query Reactive) |
| `GET` | `http://localhost:8121/products/price-range?min=50&max=200` | 5. Tìm theo khoảng giá (DatabaseClient fluent query) |
| `PUT` | `http://localhost:8121/products/1/stock?stock=200` | 6. Cập nhật tồn kho (DatabaseClient update fluent) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

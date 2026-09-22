# 22-HibernateBatch - Hibernate Batch & Bulk Operations

<p align="left">
  <img src="https://img.shields.io/badge/Port-8122-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-High-Performance%20Persistence-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi cần chèn (insert) hoặc cập nhật (update) 50.000 đến 100.000 bản ghi dữ liệu, việc gọi hàm `saveAll()` thông thường của Spring Data JPA sẽ nạp toàn bộ entity vào First-level Cache (Persistence Context), gây tràn bộ nhớ (OutOfMemoryError) và sinh ra hàng chục ngàn câu lệnh INSERT riêng lẻ làm tê liệt Database.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Nhập dữ liệu hàng loạt từ file Excel/CSV (Import Data Pipeline) hàng trăm ngàn dòng vào database.**
- **Xử lý quyết toán định kỳ cuối ngày: cập nhật trạng thái hàng loạt đơn hàng mà không cần load entity lên RAM.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Hibernate Batch & Bulk Operations |
|:---|:---|:---|
| **Spring Data JPA saveAll()** | `Default JPA` | saveAll() mặc định không bật JDBC batching và tích lũy entity gây OOM; HibernateBatch dùng StatelessSession tối ưu bộ nhớ. |
| **Spring JdbcTemplate batchUpdate()** | `Raw JDBC Batch` | JdbcTemplate batch rất nhanh nhưng phải viết SQL tay; HibernateBatch kết hợp được giữa batch JDBC và mô hình entity. |
| **PostgreSQL COPY command** | `Database Bulk Ingestion` | COPY là nhanh nhất nhưng phụ thuộc riêng vào Postgres; HibernateBatch chạy độc lập trên mọi CSDL. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Sử dụng `StatelessSession` bỏ qua hoàn toàn cơ chế First-level cache, Dirty Checking và Interceptor, giữ mức tiêu thụ RAM luôn ở mức tối thiểu.**
- **Gom nhóm hàng ngàn câu lệnh INSERT/UPDATE thành một lượt gửi duy nhất qua mạng (`jdbc.batch_size`).**
- **Cung cấp giải pháp Bulk Update/Delete trực tiếp tại tầng CSDL không qua nạp bộ nhớ.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Khi dùng `StatelessSession`, lập trình viên phải tự quản lý giao dịch và không thể dùng tính năng Cascade hay Lazy Loading.
- Cần cấu hình bổ sung tham số URL CSDL (ví dụ `reRewriteBatchedStatements=true` trên MySQL) để kích hoạt batching thực sự.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các tác vụ import dữ liệu lớn, xử lý số liệu cuối ngày, migration dữ liệu. KHÔNG NÊN DÙNG: Cho các thao tác thêm sửa xóa từng bản ghi thông thường trong giao dịch của người dùng.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/hibernatebatch/controller/BatchController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/hibernatebatch/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/hibernatebatch/repository/CategoryRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/hibernatebatch/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/hibernatebatch/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/hibernatebatch/entity/Category.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/hibernatebatch/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8122
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:batchdb
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: false
        generate_statistics: true
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 22-HibernateBatch

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8122`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8122/api/batch/import?count=100` | Import 100 products |
| `PUT` | `http://localhost:8122/api/batch/update-prices?category=Electronics&percentage=10` | Update prices by percentage |
| `DELETE` | `http://localhost:8122/api/batch/cleanup?maxPrice=12` | Delete products with maxPrice 12 |
| `GET` | `http://localhost:8122/api/batch/products` | Get all products |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

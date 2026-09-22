# 19-jOOQ - jOOQ

<p align="left">
  <img src="https://img.shields.io/badge/Port-8119-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Type-Safe%20SQL-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Viết câu lệnh SQL trong chuỗi String (dù là JDBC, JPA Native Query hay MyBatis) rất dễ bị gõ sai tên cột hoặc tên bảng, nhưng chỉ khi chạy ứng dụng (Runtime) mới phát hiện lỗi. Ngoài ra, JPA không hỗ trợ các cú pháp SQL hiện đại như Window Functions, CTE (WITH clause) hay JSON aggregation.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Xây dựng các dashboard phân tích dữ liệu (Business Intelligence / Analytics) với các câu truy vấn SQL chuyên sâu.**
- **Các hệ thống đòi hỏi độ an toàn kiểu dữ liệu tuyệt đối (Type-Safe): mọi câu SQL đều được kiểm tra cú pháp ngay lúc Compile.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với jOOQ |
|:---|:---|:---|
| **QueryDSL** | `Type-Safe Query` | QueryDSL hiện ít được cập nhật; jOOQ được phát triển mạnh mẽ và bám sát 100% tính năng mới nhất của các hệ quản trị CSDL. |
| **MyBatis** | `XML SQL` | MyBatis viết XML dễ lỗi chính tả; jOOQ sinh Java class từ DB nên gõ sai là báo đỏ ngay trên IDE. |
| **Hibernate Criteria** | `JPA Criteria API` | Hibernate Criteria cực kỳ khó đọc và dài dòng; jOOQ có Fluent DSL mô phỏng SQL tự nhiên nhất. |

### 🌟 Ưu điểm nổi bật (Pros)
- **An toàn kiểu dữ liệu 100% (Type-Safe): Đổi tên cột trong CSDL, chạy code generator là toàn bộ chỗ gọi sai trong Java sẽ báo lỗi compile ngay.**
- **Cú pháp Fluent API viết SQL trong Java y hệt như đang gõ câu lệnh SQL gốc.**
- **Hỗ trợ đầy đủ các tính năng SQL phức tạp nhất: Window Functions, Common Table Expressions (CTE), Lateral Joins, JSON operators.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Bản dùng cho các CSDL thương mại (Oracle, SQL Server) yêu cầu phải mua bản quyền có phí (bản mã nguồn mở chỉ hỗ trợ Postgres, MySQL, H2).
- Cần cấu hình bước sinh mã (Code Generation step) từ schema CSDL trong quy trình build Maven.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các bài toán báo cáo phân tích số liệu phức tạp trên PostgreSQL/MySQL. KHÔNG NÊN DÙNG: Cho các thao tác CRUD thực thể đơn giản hoặc khi ngân sách không cho phép mua license thương mại cho Oracle/SQL Server.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/jooq/controller/AnalyticsController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/jooq/service/AnalyticsService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/jooq/dto/CategoryRevenueDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/jooq/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/jooq/dto/ProductRankDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/jooq/dto/SaleDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8119
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
    password:
  sql:
    init:
      mode: always
  jooq:
    sql-dialect: H2
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 19-jOOQ

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8119`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8119/api/analytics/products` | Get all products |
| `GET` | `http://localhost:8119/api/analytics/revenue` | Get revenue by category |
| `GET` | `http://localhost:8119/api/analytics/ranking` | Get product ranking by revenue |
| `GET` | `http://localhost:8119/api/analytics/top` | Get top performing products |
| `POST` | `http://localhost:8119/api/analytics/sales/batch` | Batch insert sales |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

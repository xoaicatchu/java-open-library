# 24-Flyway - Flyway

<p align="left">
  <img src="https://img.shields.io/badge/Port-8124-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Database%20Schema%20Migration-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi triển khai ứng dụng lên môi trường Production, việc DBA hoặc lập trình viên phải chạy tay các file script SQL cập nhật bảng rất dễ gặp sai sót (quên chạy script, chạy sai thứ tự, chạy sót cột) dẫn tới ứng dụng mới khởi động lên bị crash do lỗi lệch schema CSDL.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Tự động hóa 100% việc đồng bộ và nâng cấp cấu trúc CSDL trong pipeline CI/CD mỗi khi deploy ứng dụng.**
- **Đảm bảo toàn bộ môi trường (Local, Dev, Staging, Production) luôn có cùng một phiên bản schema CSDL nhất quán.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Flyway |
|:---|:---|:---|
| **Liquibase** | `Multi-format Migration` | Liquibase dùng YAML/XML/SQL độc lập hệ CSDL; Flyway dùng file SQL thuần túy (`V1__...sql`) trực quan và đơn giản hơn rất nhiều. |
| **Hibernate ddl-auto=update** | `Automatic Schema Tool` | `ddl-auto=update` cực kỳ nguy hiểm trên Production (dễ mất dữ liệu hoặc khóa bảng); Flyway an toàn tuyệt đối với script kiểm soát. |
| **Manual DBA Scripts** | `Manual Process` | Chạy tay dễ quên và sai sót con người; Flyway lưu bảng lịch sử `flyway_schema_history` kiểm soát mã hash checksum. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Cực kỳ đơn giản và dễ hiểu: lập trình viên chỉ cần viết các file SQL thuần (`V1__init.sql`, `V2__add_index.sql`).**
- **Tự động tính checksum để ngăn chặn ai đó sửa trộm nội dung script migration cũ đã chạy.**
- **Tích hợp tự nhiên trong Spring Boot: ứng dụng tự động kiểm tra và chạy script mới khi khởi động.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Bản mã nguồn mở (Community) không hỗ trợ tính năng Rollback tự động (Undo migration chỉ có trong bản Pro/Enterprise).
- Script viết bằng SQL đặc thù của CSDL nào thì chỉ chạy được trên CSDL đó (không tự dịch cú pháp giữa Oracle và Postgres).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Là công cụ migration khuyên dùng mặc định cho 90% dự án Spring Boot sử dụng SQL. KHÔNG NÊN DÙNG: Khi dự án cần hỗ trợ triển khai trên nhiều loại RDBMS khác nhau bằng 1 bộ script duy nhất (khi đó nên chọn Liquibase).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/flyway/controller/FlywayController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/flyway/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/flyway/repository/CategoryRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/flyway/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/flyway/entity/Category.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/flyway/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8124
spring:
  mvc:
    problemdetails:
      enabled: true
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: validate # Let flyway manage schema
    show-sql: true
  flyway:
    enabled: true
    baseline-on-migrate: true
    baseline-version: "0"
    locations: classpath:db/migration,classpath:db/migration/java
    out-of-order: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 24-Flyway

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8124`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8124/api/flyway/history` | 1. Xem lịch sử các phiên bản migration đã áp dụng (V1, V2, V3, V4, R) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

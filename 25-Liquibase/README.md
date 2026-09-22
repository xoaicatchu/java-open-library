# 25-Liquibase - Liquibase

<p align="left">
  <img src="https://img.shields.io/badge/Port-8125-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Database%20Schema%20Migration-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Sản phẩm phần mềm đóng gói (On-Premises Software) cần cài đặt trên hạ tầng của nhiều khách hàng khác nhau (khách hàng dùng Oracle, khách hàng dùng PostgreSQL, khách hàng dùng SQL Server). Nếu viết script SQL riêng cho từng hệ quản trị CSDL thì chi phí bảo trì tăng gấp 3-4 lần.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Định nghĩa cấu trúc CSDL bằng ChangeSet định dạng YAML/XML/JSON độc lập hoàn toàn với loại CSDL.**
- **Các hệ thống yêu cầu nghiêm ngặt về khả năng tự động hoàn tác (Rollback Migration) khi quá trình deploy gặp sự cố.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Liquibase |
|:---|:---|:---|
| **Flyway** | `SQL-based Migration` | Flyway dùng SQL thuần đơn giản hơn; Liquibase hỗ trợ đa CSDL, rollback tự động và điều kiện chạy (preconditions) mạnh mẽ hơn. |
| **Hibernate hbm2ddl** | `ORM Schema Generator` | Liquibase kiểm soát phiên bản chính xác qua từng ChangeSet; Hibernate tự sinh không an toàn cho Production. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Độc lập với hệ quản trị CSDL: viết ChangeSet 1 lần, Liquibase tự sinh SQL tương ứng cho Oracle, Postgres, MySQL, SQL Server.**
- **Hỗ trợ Rollback tự động mạnh mẽ thông qua thẻ `<rollback>` được định nghĩa trong ChangeSet.**
- **Cung cấp cơ chế Preconditions (tiền điều kiện kiểm tra trước khi chạy script) và Contexts (chạy script theo môi trường dev/prod).**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cú pháp khai báo bằng XML/YAML dài dòng và khó đọc hơn so với viết câu lệnh SQL thuần túy.
- Độ dốc học tập cao hơn so với Flyway.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các sản phẩm phần mềm đóng gói thương mại triển khai trên nhiều hệ CSDL hoặc yêu cầu rollback tự động. KHÔNG NÊN DÙNG: Cho các dự án web thông thường chỉ dùng duy nhất 1 loại CSDL (khi đó Flyway đơn giản và trực quan hơn).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/liquibase/controller/LiquibaseStatusController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/liquibase/repository/DepartmentRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/liquibase/repository/EmployeeRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/liquibase/entity/Department.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/liquibase/entity/Employee.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: liquibase-demo
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
      ddl-auto: validate # Liquibase manages schema
    show-sql: true
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
    contexts: dev
server:
  port: 8125
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 25-Liquibase

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8125`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8125/api/liquibase/status` | Thực thi GET http://localhost:8125/api/liquibase/status |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

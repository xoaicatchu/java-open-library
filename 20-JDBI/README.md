# 20-JDBI - JDBI 3

<p align="left">
  <img src="https://img.shields.io/badge/Port-8120-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Fluent%20Database%20Access-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Spring `JdbcTemplate` quá thô sơ (phải tự viết RowMapper thủ công cho từng trường), trong khi Hibernate lại quá cồng kềnh với session management, proxy và caching. Lập trình viên muốn một giải pháp ở giữa: viết SQL trực tiếp, nhưng việc binding tham số và mapping kết quả phải tự động và thanh lịch.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Các dịch vụ microservice nhỏ gọn, tác vụ batch job, công cụ đồng bộ dữ liệu cần tốc độ thực thi cao.**
- **Khai báo tầng truy cập dữ liệu bằng Java Interface (SQL Object API) với các annotation `@SqlQuery`, `@SqlUpdate` tương tự Retrofit.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với JDBI 3 |
|:---|:---|:---|
| **Spring JdbcTemplate** | `Raw JDBC Helper` | JDBI cung cấp Fluent API và SQL Object khai báo interface tiện lợi hơn JdbcTemplate rất nhiều. |
| **MyBatis** | `XML Mapper` | MyBatis dùng file XML; JDBI cho phép viết SQL ngay trên annotation của Java interface hoặc dùng Fluent API. |
| **Hibernate** | `Full ORM` | JDBI nhẹ hơn, không có cơ chế quản lý trạng thái thực thể phức tạp của Hibernate. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Cú pháp Fluent API cực kỳ thanh thoát và hiện đại.**
- **Hỗ trợ mô hình SQL Object: chỉ cần khai báo interface có gắn `@SqlQuery("SELECT * FROM ...")`, JDBI tự động sinh implementation.**
- **Tự động ánh xạ kết quả vào Java Record hoặc Bean một cách thông minh.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không có hệ sinh thái lớn và tài liệu phong phú bằng Spring Data JPA hay MyBatis trong cộng đồng doanh nghiệp Việt Nam.
- Không hỗ trợ quan hệ thực thể tự động (phải tự viết câu lệnh JOIN).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các service vừa và nhỏ, batch tool cần hiệu năng cao và code SQL tường minh. KHÔNG NÊN DÙNG: Cho các dự án lớn có mô hình quan hệ thực thể dày đặc cần cơ chế Dirty Checking của JPA.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/jdbi/controller/EmployeeController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/jdbi/service/EmployeeService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/jdbi/config/EmailColumnMapper.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/jdbi/repository/EmployeeDao.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/jdbi/repository/EmployeeRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/jdbi/config/JdbiConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: jdbi-demo
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: ""
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
  sql:
    init:
      mode: always
  threads:
    virtual:
      enabled: true
server:
  port: 8120
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 20-JDBI

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8120`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8120/api/employees` | Lấy danh sách tất cả nhân viên |
| `GET` | `http://localhost:8120/api/employees/1` | Lấy thông tin nhân viên theo ID |
| `POST` | `http://localhost:8120/api/employees` | Thêm một nhân viên mới |
| `POST` | `http://localhost:8120/api/employees/batch` | Thêm nhiều nhân viên |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

# 04-SpringHATEOAS - Spring HATEOAS

> **Cổng dịch vụ (Server Port)**: `8104`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Client (Mobile/Web) phải hardcode URL từng endpoint. Khi backend đổi URL hoặc state của object thay đổi (VD: Đơn hàng đã HỦY thì không được hiển thị nút THANH TOÁN), client dễ bị lỗi.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Trả về kèm danh sách liên kết hành động khả dĩ (`links`). Ứng dụng trong Public API, ngân hàng mở (Open Banking API).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/hateoas/controller/EmployeeController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/hateoas/service/EmployeeService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/hateoas/repository/EmployeeRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/hateoas/assembler/EmployeeModelAssembler.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/hateoas/dto/EmployeeDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/hateoas/entity/Employee.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8104
spring:
  application:
    name: 04-SpringHATEOAS
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:employeedb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
  mvc:
    problemdetails:
      enabled: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 04-SpringHATEOAS

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8104`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8104/employees` | GET all employees |
| `GET` | `http://localhost:8104/employees/1` | GET employee by ID (Replace {id} with actual ID) |
| `GET` | `http://localhost:8104/employees/department/IT` | GET employees by department |
| `POST` | `http://localhost:8104/employees` | POST create a new employee |
| `PUT` | `http://localhost:8104/employees/1` | PUT update an employee (Replace {id} with actual ID) |
| `DELETE` | `http://localhost:8104/employees/1` | DELETE an employee (Replace {id} with actual ID) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

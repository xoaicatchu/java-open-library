# 04-SpringHATEOAS - Spring HATEOAS

<p align="left">
  <img src="https://img.shields.io/badge/Port-8104-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Web%20&%20Hypermedia-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
REST API thông thường chỉ trả về dữ liệu thô. Client (Web/Mobile) phải hardcode đường dẫn URL cho từng thao tác tiếp theo. Khi backend thay đổi cấu trúc URL hoặc trạng thái thực thể thay đổi, client dễ bị lỗi do gọi sai URL không hợp lệ.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Cung cấp Public APIs và Open Banking APIs cho các đối tác bên ngoài tự động khám phá các thao tác khả dĩ.**
- **Ứng dụng State-Machine driven UI: Frontend hiển thị nút bấm (Thanh toán, Hủy đơn, Đổi trả) hoàn toàn dựa vào mảng `_links` trả về từ Backend.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring HATEOAS |
|:---|:---|:---|
| **Plain REST API** | `Standard JSON` | Plain REST trả về JSON phẳng nhẹ hơn nhưng client phải tự quản lý logic điều hướng URL. |
| **GraphQL** | `Query Language` | GraphQL cho phép client chủ động query trường nhưng không theo chuẩn định hướng liên kết Hypermedia của REST. |
| **JSON:API Spec** | `Specification` | JSON:API là một chuẩn định dạng tương tự HAL nhưng Spring HATEOAS hỗ trợ linh hoạt cả HAL, HAL-FORMS, Collection+JSON. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Đạt chuẩn REST Maturity Level 3 (mức trưởng thành cao nhất của RESTful API).**
- **Giảm thiểu sự phụ thuộc chặt giữa Frontend và Backend (loose coupling).**
- **Hỗ trợ tạo link an toàn theo kiểu dữ liệu (type-safe) bằng `WebMvcLinkBuilder`.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Payload JSON phình to do chứa nhiều metadata và liên kết URLs.
- Đội ngũ Frontend phải thay đổi thói quen đọc dữ liệu (phải parse cấu trúc HAL `_embedded` và `_links`).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho Public APIs, cổng tích hợp đối tác B2B, hệ thống workflow phức tạp. KHÔNG NÊN DÙNG: Cho các API nội bộ đơn giản, mobile app bị giới hạn nghiêm ngặt về băng thông mạng 3G/4G.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/hateoas/controller/EmployeeController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/hateoas/service/EmployeeService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/hateoas/repository/EmployeeRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/hateoas/assembler/EmployeeModelAssembler.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/hateoas/dto/EmployeeDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/hateoas/entity/Employee.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 04-SpringHATEOAS

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8104`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8104/employees` | GET all employees |
| `GET` | `http://localhost:8104/employees/1` | GET employee by ID (Replace {id} with actual ID) |
| `GET` | `http://localhost:8104/employees/department/IT` | GET employees by department |
| `POST` | `http://localhost:8104/employees` | POST create a new employee |
| `PUT` | `http://localhost:8104/employees/1` | PUT update an employee (Replace {id} with actual ID) |
| `DELETE` | `http://localhost:8104/employees/1` | DELETE an employee (Replace {id} with actual ID) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

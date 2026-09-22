# 25-Liquibase - Liquibase

> **Cổng dịch vụ (Server Port)**: `8125`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Dự án cần hỗ trợ triển khai trên nhiều loại CSDL khác nhau (cả Oracle, PostgreSQL, SQL Server) hoặc cần tính năng Rollback tự động khi lỗi.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Định nghĩa thay đổi CSDL bằng YAML/XML độc lập với hệ quản trị CSDL, có hỗ trợ rollback script chặt chẽ.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/liquibase/controller/LiquibaseStatusController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/liquibase/repository/DepartmentRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/liquibase/repository/EmployeeRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/liquibase/entity/Department.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/liquibase/entity/Employee.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 25-Liquibase

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8125`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8125/api/liquibase/status` | Thực thi GET http://localhost:8125/api/liquibase/status |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

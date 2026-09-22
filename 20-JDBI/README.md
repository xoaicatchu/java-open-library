# 20-JDBI - JDBI 3

> **Cổng dịch vụ (Server Port)**: `8120`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần một thư viện thao tác SQL đơn giản hơn JPA (không nặng nề session, cache) nhưng tiện hơn JDBC thô rất nhiều.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Các service nhỏ gọn, batch job, tool đồng bộ dữ liệu. Hỗ trợ Fluent API và SQL Object khai báo interface tiện lợi.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/jdbi/controller/EmployeeController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/jdbi/service/EmployeeService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/jdbi/config/EmailColumnMapper.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/jdbi/repository/EmployeeDao.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/jdbi/repository/EmployeeRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/jdbi/config/JdbiConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 20-JDBI

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8120`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8120/api/employees` | Lấy danh sách tất cả nhân viên |
| `GET` | `http://localhost:8120/api/employees/1` | Lấy thông tin nhân viên theo ID |
| `POST` | `http://localhost:8120/api/employees` | Thêm một nhân viên mới |
| `POST` | `http://localhost:8120/api/employees/batch` | Thêm nhiều nhân viên |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

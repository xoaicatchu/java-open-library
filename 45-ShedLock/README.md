# 45-ShedLock - ShedLock

> **Cổng dịch vụ (Server Port)**: `8145`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Ứng dụng chạy 5 Pod/Node trên Kubernetes, dùng `@Scheduled` mặc định thì cứ đến giờ cả 5 Pod cùng quét DB gửi email 1 lúc -> Khách hàng nhận 5 email trùng nhau.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Tạo khóa phân tán (Distributed Lock) trong Database. Đảm bảo dù hệ thống có bao nhiêu Pod thì tại một thời điểm chỉ có **đúng 1 Pod được quyền thực thi job**.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/shedlock/controller/JobController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/shedlock/config/ShedLockConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8145
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:shedlockdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
  sql:
    init:
      mode: always
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 45-ShedLock

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8145`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8145/api/jobs/report` | Trigger Report Job |
| `POST` | `http://localhost:8145/api/jobs/cleanup` | Trigger Cleanup Job |
| `GET` | `http://localhost:8145/api/jobs/status` | Get ShedLock Status |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

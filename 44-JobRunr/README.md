# 44-JobRunr - JobRunr

> **Cổng dịch vụ (Server Port)**: `8144`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Quartz quá nặng nề và cấu hình phức tạp; cần chạy background job nhẹ nhàng bằng Lambda Expression và có giao diện Dashboard web trực quan để bấm chạy lại (Retry) khi lỗi.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Gửi email thông báo, export file ngầm, xử lý ảnh đại diện sau khi upload. Có sẵn Dashboard web xem tiến độ job tại `http://localhost:8000`.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/jobrunr/controller/JobController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/jobrunr/service/EmailService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/jobrunr/service/ReportService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/jobrunr/config/JobRunrConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/jobrunr/dto/JobRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8144
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:jobrunrdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
org:
  jobrunr:
    background-job-server:
      enabled: true
    dashboard:
      enabled: true
      port: 8000
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 44-JobRunr

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8144`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8144/jobs/email` | Enqueue email job |
| `POST` | `http://localhost:8144/jobs/report/schedule` | Schedule report job |
| `POST` | `http://localhost:8144/jobs/report/recurring` | Schedule recurring report |
| `DELETE` | `http://localhost:8144/jobs/report/recurring` | Delete recurring report |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

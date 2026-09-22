# 43-Quartz - Quartz Scheduler

> **Cổng dịch vụ (Server Port)**: `8143`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần lập lịch phức tạp (ví dụ: ngày làm việc cuối cùng của tháng, chạy định kỳ kèm xử lý bù nếu bị mất điện/misfire). Cần lưu trạng thái lịch vào DB.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Lập lịch cấp doanh nghiệp. Quản trị lịch trình chạy báo cáo tài chính, tính lãi suất ngân hàng ban đêm, lưu trữ trạng thái Job vào bảng CSDL quan hệ (JDBC JobStore).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/quartz/controller/JobController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/quartz/service/SchedulerService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/quartz/config/QuartzConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/quartz/dto/ScheduleRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8143
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:quartzdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  quartz:
    job-store-type: jdbc
    jdbc:
      initialize-schema: always
    properties:
      org.quartz.jobStore.driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
      org.quartz.threadPool.threadCount: 5
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 43-Quartz

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8143`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8143/api/jobs/schedule` | 1. Lập lịch Cron Job mới (chạy mỗi 10 giây) |
| `POST` | `http://localhost:8143/api/jobs/REPORTING/DailyReportJob/pause` | 2. Tạm dừng Job |
| `POST` | `http://localhost:8143/api/jobs/REPORTING/DailyReportJob/resume` | 3. Tiếp tục chạy Job |
| `DELETE` | `http://localhost:8143/api/jobs/REPORTING/DailyReportJob` | 4. Xóa Job khỏi Quartz Scheduler |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

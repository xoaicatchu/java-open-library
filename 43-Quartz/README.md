# 43-Quartz - Quartz Scheduler

<p align="left">
  <img src="https://img.shields.io/badge/Port-8143-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Enterprise%20Job%20Scheduling-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Annotation `@Scheduled` mặc định của Spring chỉ chạy trong RAM, không thể lập lịch động (thay đổi giờ chạy qua giao diện mà không phải sửa code restart server), không hỗ trợ các biểu thức lịch phức tạp (ngày làm việc cuối cùng của tháng, trừ ngày lễ) và không có cơ chế bù việc (misfire handling) khi server bị mất điện.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Hệ thống tính lãi suất tài chính ngân hàng ban đêm, chốt sổ công nợ ngày cuối cùng của quý.**
- **Lưu trữ trạng thái lịch chạy vào Database (JDBC JobStore) đảm bảo tính kiên cố và không bao giờ bị mất lịch trình.**
- **Cụm nhiều server chạy Quartz Cluster: tự động điều phối phân tải và chịu lỗi (Failover) khi một node bị sập.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Quartz Scheduler |
|:---|:---|:---|
| **Spring @Scheduled + ShedLock** | `Lightweight Distributed Cron` | ShedLock chỉ đơn giản là chặn trùng lịch cron tĩnh; Quartz là một hệ thống quản lý lịch trình động cấp doanh nghiệp toàn diện. |
| **JobRunr** | `Modern Background Jobs` | JobRunr hiện đại, có Dashboard đẹp và dùng code lambda; Quartz mạnh hơn về các biểu thức lịch phức tạp (Calendars) và lịch sử lâu đời. |
| **ElasticJob** | `Distributed Sharding Job` | ElasticJob mạnh về chia nhỏ dữ liệu phân tán (Data Sharding); Quartz mạnh về quy tắc thời gian doanh nghiệp. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Hệ thống lập lịch hoàn chỉnh và mạnh mẽ nhất thế giới Java: hỗ trợ CronTrigger, SimpleTrigger, CalendarIntervalTrigger.**
- **Quản lý lưu trữ trạng thái Job bền vững trong CSDL quan hệ (JDBC JobStore), hỗ trợ khôi phục sau sự cố.**
- **Cơ chế Misfire Handling: tự động xử lý bù các job bị lỡ lịch do bảo trì hệ thống.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cấu hình khá nặng nề, cần tạo sẵn hơn 10 bảng CSDL đặc thù của Quartz (`QRTZ_TRIGGERS`, `QRTZ_JOB_DETAILS`...).
- Cú pháp lập trình truyền thống (JobExecutionContext) khá cũ so với phong cách Java hiện đại.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Hệ thống ngân hàng, tài chính, bảo hiểm cần lịch chạy phức tạp, động và bảo đảm 100% không mất job. KHÔNG NÊN DÙNG: Khi chỉ cần một vài tác vụ cronjob đơn giản chạy định kỳ (khi đó Spring `@Scheduled` + `ShedLock` nhẹ hơn nhiều).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/quartz/controller/JobController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/quartz/service/SchedulerService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/quartz/config/QuartzConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/quartz/dto/ScheduleRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 43-Quartz

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8143`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8143/api/jobs/schedule` | 1. Lập lịch Cron Job mới (chạy mỗi 10 giây) |
| `POST` | `http://localhost:8143/api/jobs/REPORTING/DailyReportJob/pause` | 2. Tạm dừng Job |
| `POST` | `http://localhost:8143/api/jobs/REPORTING/DailyReportJob/resume` | 3. Tiếp tục chạy Job |
| `DELETE` | `http://localhost:8143/api/jobs/REPORTING/DailyReportJob` | 4. Xóa Job khỏi Quartz Scheduler |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

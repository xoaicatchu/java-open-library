# 45-ShedLock - ShedLock

<p align="left">
  <img src="https://img.shields.io/badge/Port-8145-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Distributed%20Scheduled%20Lock-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi ứng dụng được scale lên chạy 5 Pod/Node trên cụm Kubernetes, nếu dùng `@Scheduled` mặc định của Spring thì cứ đến đúng 00:00 cả 5 Pod sẽ cùng quét database và gửi email mừng sinh nhật cùng một lúc, dẫn tới việc một khách hàng nhận được 5 email trùng nhau.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Đảm bảo dù ứng dụng có được scale lên bao nhiêu server đi nữa, thì tại một thời điểm chỉ có duy nhất **đúng 1 server được quyền thực thi cronjob**.**
- **Sử dụng bảng khóa phân tán siêu nhẹ trong Database (`shedlock` table) hoặc Redis để điều phối.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với ShedLock |
|:---|:---|:---|
| **Quartz Cluster Lock** | `Clustered Scheduler` | Quartz Cluster đòi hỏi tạo hơn 10 bảng CSDL và thay đổi toàn bộ kiến trúc code; ShedLock chỉ cần duy nhất 1 annotation `@SchedulerLock` trên hàm `@Scheduled` có sẵn. |
| **Redisson RLock** | `Programmatic Lock` | Redisson Lock phải tự code tay logic khóa/mở khóa; ShedLock tự động khóa và nhả khóa theo vòng đời của method. |
| **Kubernetes CronJob** | `Infrastructure Cron` | K8s CronJob khởi động container mới mỗi lần chạy (tốn RAM và khởi động chậm); ShedLock chạy trực tiếp bên trong ứng dụng Java đang chạy. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Siêu nhẹ và tiện dụng: Chỉ cần thêm annotation `@SchedulerLock(name = "sendEmailJob", lockAtMostFor = "10m")`.**
- **Hỗ trợ hầu hết mọi loại kho lưu trữ: CSDL quan hệ (Postgres, MySQL, Oracle), MongoDB, Redis, DynamoDB.**
- **Có cơ chế `lockAtLeastFor` ngăn chặn việc các node có đồng hồ chênh lệch mili-giây cùng nhảy vào chạy lại.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không phải là một công cụ lập lịch độc lập: ShedLock chỉ làm nhiệm vụ chặn khóa, việc kích hoạt giờ chạy vẫn dựa vào `@Scheduled` của Spring.
- Không hỗ trợ lập lịch động qua giao diện.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Giải pháp chuẩn mực và đơn giản nhất cho mọi cronjob tĩnh `@Scheduled` trong kiến trúc nhiều máy chủ/Kubernetes. KHÔNG NÊN DÙNG: Khi cần thay đổi giờ chạy động thời gian thực hoặc cần lập lịch phức tạp (khi đó dùng JobRunr hoặc Quartz).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/shedlock/controller/JobController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/shedlock/config/ShedLockConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 45-ShedLock

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8145`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8145/api/jobs/report` | Trigger Report Job |
| `POST` | `http://localhost:8145/api/jobs/cleanup` | Trigger Cleanup Job |
| `GET` | `http://localhost:8145/api/jobs/status` | Get ShedLock Status |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

# 44-JobRunr - JobRunr

<p align="left">
  <img src="https://img.shields.io/badge/Port-8144-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Modern%20Background%20Job%20Processing-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Quartz Scheduler quá cồng kềnh với hàng chục bảng CSDL và cấu hình phức tạp; trong khi đó các tác vụ ngầm thông thường (gửi email chào mừng, xử lý ảnh đại diện sau khi upload, xuất báo cáo) lại cần một giải pháp nhẹ nhàng, viết bằng Lambda expression và có sẵn giao diện Web theo dõi tiến độ.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Đẩy các tác vụ tốn thời gian ra xử lý ngầm (Fire-and-forget / Delayed Jobs) giúp giải phóng ngay lập tức luồng HTTP của người dùng.**
- **Có sẵn giao diện Web Dashboard tích hợp tại `http://localhost:8000` để theo dõi job đang chạy, xem lịch sử và bấm nút 'Retry' khi có job thất bại.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với JobRunr |
|:---|:---|:---|
| **Quartz Scheduler** | `Enterprise Scheduler` | Quartz cồng kềnh và không có sẵn giao diện web; JobRunr hiện đại, cấu hình siêu nhanh chỉ bằng vài dòng code Java. |
| **Spring Batch** | `Batch Processing Framework` | Spring Batch chuyên biệt cho xử lý dữ liệu lớn (Chunk-oriented ETL); JobRunr tối ưu cho các background task nghiệp vụ hàng ngày. |
| **Hangfire (.NET)** | `Background Worker` | JobRunr chính là sự tái hiện hoàn hảo mô hình Hangfire của C# sang thế giới Java. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Cực kỳ hiện đại: Lập lịch job trực tiếp bằng Java 8 Lambda Expression (`jobScheduler.enqueue(() -> emailService.send(id))`).**
- **Tích hợp sẵn giao diện Web Dashboard tuyệt đẹp mà không cần cài đặt thêm phần mềm bên ngoài.**
- **Tự động thử lại (Retry) thông minh với Exponential Backoff khi gặp lỗi.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Một số tính năng nâng cao (Job Queues ưu tiên, Rate Limiting, Server Tags) yêu cầu bản quyền thương mại JobRunr Pro.
- Yêu cầu tuần hoàn các lambda expression nên code phải tuân thủ quy tắc serialization.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Lựa chọn hàng đầu cho các background jobs thông thường (gửi email, xử lý tài liệu, đồng bộ ngầm) trong mọi dự án web hiện đại. KHÔNG NÊN DÙNG: Khi bài toán đòi hỏi lập lịch tài chính siêu phức tạp với lịch làm việc ngân hàng (khi đó chọn Quartz).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/jobrunr/controller/JobController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/jobrunr/service/EmailService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/jobrunr/service/ReportService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/jobrunr/config/JobRunrConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/jobrunr/dto/JobRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 44-JobRunr

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8144`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8144/jobs/email` | Enqueue email job |
| `POST` | `http://localhost:8144/jobs/report/schedule` | Schedule report job |
| `POST` | `http://localhost:8144/jobs/report/recurring` | Schedule recurring report |
| `DELETE` | `http://localhost:8144/jobs/report/recurring` | Delete recurring report |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

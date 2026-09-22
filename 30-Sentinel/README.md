# 30-Sentinel - Alibaba Sentinel

<p align="left">
  <img src="https://img.shields.io/badge/Port-8130-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Flow%20Control%20&%20Traffic%20Shaping-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Trong các sự kiện Flash Sale hoặc Black Friday, lượng truy cập đột biến tăng gấp 100 lần bình thường. Nếu không có cơ chế điều tiết luồng thông minh, máy chủ sẽ bị quá tải CPU 100%, hàng đợi tràn ngập và toàn bộ dịch vụ sẽ bị chết đứng (Crash).

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Điều tiết lưu lượng truy cập (Traffic Shaping): làm mượt các đợt bùng nổ traffic (Traffic Peak Smoothing) bằng thuật toán Leaky Bucket / Token Bucket.**
- **Bảo vệ thích ứng theo tải hệ thống (System Adaptive Protection): tự động từ chối bớt request khi CPU máy chủ vượt ngưỡng 80% hoặc thời gian phản hồi (RT) tăng vọt.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Alibaba Sentinel |
|:---|:---|:---|
| **Resilience4j** | `Fault Tolerance` | Resilience4j thiên về Circuit Breaker ngắt kết nối; Sentinel thiên về điều tiết lưu lượng (Flow Control) và bảo vệ quá tải hệ thống. |
| **Nginx / Envoy Rate Limit** | `Gateway Rate Limit` | Nginx giới hạn cố định ở cửa ngõ; Sentinel có thể can thiệp chi tiết theo từng method, controller hoặc tham số nghiệp vụ bên trong code. |
| **Spring Cloud CircuitBreaker** | `Abstraction` | Spring Cloud CircuitBreaker là tầng trừu tượng có thể gắn driver là Sentinel hoặc Resilience4j. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Khả năng Flow Control mạnh mẽ nhất thế giới Java: hỗ trợ Warm Up (khởi động ấm tăng dần traffic), Rate Limiting, Concurrency Limiting.**
- **Bảo vệ thích ứng hệ thống (System Protection) dựa trên chỉ số CPU, System Load và Response Time thực tế của máy chủ.**
- **Có sẵn giao diện Web Dashboard theo dõi và thay đổi quy tắc (Rule) lưu lượng thời gian thực.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Tài liệu gốc nhiều phần bằng tiếng Trung Quốc (mặc dù tài liệu tiếng Anh đã được hoàn thiện dần).
- Kiến trúc phức tạp hơn so với việc nhúng một thư viện nhẹ như Resilience4j.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Hệ thống thương mại điện tử lớn, cổng thanh toán có nguy cơ bị đợt sóng traffic khổng lồ bất thường làm sập nguồn. KHÔNG NÊN DÙNG: Các ứng dụng vừa và nhỏ nơi mà một Circuit Breaker đơn giản của Resilience4j là đủ đáp ứng.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/sentinel/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/sentinel/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/sentinel/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/sentinel/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/sentinel/config/SentinelConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/sentinel/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/sentinel/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8130
spring:
  application:
    name: sentinel-demo
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:sentineldb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 30-Sentinel

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8130`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8130/products/1` | Get Product (Trigger Flow Rule if called > 2 times per second) |
| `POST` | `http://localhost:8130/products` | Create Product (Normal) |
| `POST` | `http://localhost:8130/products` | Create Product (Trigger Error -> Circuit Breaker after 2 calls) |
| `DELETE` | `http://localhost:8130/products/1` | Delete Product (Programmatic API) |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

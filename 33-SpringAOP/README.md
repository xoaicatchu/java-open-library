# 33-SpringAOP - Spring AOP

<p align="left">
  <img src="https://img.shields.io/badge/Port-8133-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Aspect-Oriented%20Programming-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Cần đo thời gian thực thi (Performance Monitoring), ghi nhật ký kiểm toán (Audit Log: ai thao tác gì lúc mấy giờ) hoặc kiểm tra quyền trên hàng trăm phương thức nghiệp vụ. Nếu chèn code đo thời gian vào đầu và cuối từng hàm, mã nguồn sẽ bị trùng lặp khủng khiếp và vi phạm nguyên lý thiết kế.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Ghi nhật ký kiểm toán tự động (Audit Logging) bằng cách định nghĩa custom annotation `@Auditable`.**
- **Đo lường thời gian thực thi của các phương thức (`@MeasurePerformance`) và tự động cảnh báo các truy vấn chậm.**
- **Bảo mật tập trung: kiểm tra quyền truy cập dữ liệu đa người thuê (Multi-Tenancy Data Isolation) trước khi gọi hàm.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring AOP |
|:---|:---|:---|
| **Full AspectJ (CTW / LTW)** | `Bytecode Weaver` | AspectJ can thiệp bytecode sâu hơn (bắt được cả private method/field access) nhưng cấu hình phức tạp; Spring AOP dùng Dynamic Proxy nhẹ nhàng và đủ dùng cho 95% bài toán. |
| **Servlet Filter / HandlerInterceptor** | `Web Layer Interceptor` | Filter/Interceptor chỉ can thiệp ở tầng Web HTTP; Spring AOP can thiệp được vào bất kỳ tầng nào (Service, Repository). |
| **Manual Wrapper Pattern** | `Design Pattern` | Tự viết Decorator/Wrapper tốn nhiều file và công sức khởi tạo Bean. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tách rời hoàn toàn các mối quan tâm đan chéo (Cross-Cutting Concerns) ra khỏi logic nghiệp vụ cốt lõi.**
- **Tích hợp tự nhiên trong Spring Framework, không cần can thiệp quy trình build bytecode của JVM.**
- **Dễ dàng áp dụng linh hoạt theo Pointcut biểu thức execution hoặc theo Custom Annotation tùy biến.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Chỉ hoạt động trên các Spring Bean công khai (public methods) được gọi từ bên ngoài thông qua Spring Proxy.
- Nếu gọi phương thức nội bộ trong cùng một class (self-invocation `this.method()`), Aspect sẽ không được kích hoạt.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho audit log, đo thời gian xử lý, xử lý ngoại lệ tập trung, caching trong suốt. KHÔNG NÊN DÙNG: Khi cần can thiệp vào các phương thức private, constructor hoặc các class không phải là Spring Bean (khi đó phải dùng Full AspectJ).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/aop/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/aop/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/aop/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/aop/repository/AuditLogRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/aop/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/aop/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/aop/entity/AuditLog.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/aop/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8133
spring:
  application:
    name: 33-SpringAOP
  datasource:
    url: jdbc:h2:mem:aopdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  h2:
    console:
      enabled: true
  threads:
    virtual:
      enabled: true
logging:
  level:
    com.example.aop: DEBUG
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 33-SpringAOP

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8133`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8133/api/products` | Get all products |
| `POST` | `http://localhost:8133/api/products` | Create a product |
| `POST` | `http://localhost:8133/api/products` | Create product rapidly (Run this 4 times to see 429 RateLimit) |
| `GET` | `http://localhost:8133/api/audit-logs` | Check audit logs |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

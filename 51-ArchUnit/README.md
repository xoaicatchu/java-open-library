# 51-ArchUnit - ArchUnit

<p align="left">
  <img src="https://img.shields.io/badge/Port-8151-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Architecture%20Testing%20as%20Code-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Một lập trình viên mới vào dự án vô tình viết code từ Controller gọi thẳng xuống Database Repository (bỏ qua tầng Service), hoặc trong Entity lại import thư viện Spring Web. Code review bằng mắt thường thường xuyên bỏ sót, khiến kiến trúc Clean Architecture bị phá vỡ hoàn toàn sau vài tháng.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Biến quy tắc kiến trúc thành các bài Unit Test tự động chạy trên CI/CD: 'Controller không được phụ thuộc Repository', 'Service phải kết thúc bằng hậu tố Service', 'Entity không được chứa annotation Spring Web'.**
- **Tự động phát hiện và chặn đứng các phụ thuộc vòng tròn (Cyclic Dependencies) giữa các package.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với ArchUnit |
|:---|:---|:---|
| **SonarQube Quality Gate** | `Static Analysis Platform` | SonarQube kiểm tra code smell/security chung chung; ArchUnit cho phép tự viết luật kiến trúc đặc thù của riêng dự án bằng code Java. |
| **Checkstyle / SpotBugs** | `Linter Tools` | Checkstyle kiểm tra format style; ArchUnit phân tích đồ thị phụ thuộc bytecode giữa các class. |
| **Manual Code Review** | `Human Review` | Con người review dễ mệt mỏi và bỏ sót; ArchUnit tự động cấm merge code nếu vi phạm kiến trúc. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Viết luật kiến trúc hoàn toàn bằng Java Fluent API cực kỳ biểu cảm và mạnh mẽ.**
- **Chạy như một bài JUnit Test bình thường, không cần cài đặt thêm server hay plugin phức tạp.**
- **Bảo vệ toàn vẹn kiến trúc phân lớp (Hexagonal, Onion, Layered Architecture) một cách tuyệt đối.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Thời gian quét và phân tích bytecode của toàn bộ dự án lớn có thể mất vài giây trong quá trình test.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Bắt buộc phải có cho mọi dự án lớn từ 3 lập trình viên trở lên để giữ cho kiến trúc hệ thống luôn sạch sẽ. KHÔNG NÊN DÙNG: Chỉ cho các script hoặc tool dùng một lần.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/archunit/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/archunit/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/archunit/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/archunit/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/archunit/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8151
spring:
  application:
    name: archunit-demo
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 51-ArchUnit

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8151`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8151/api/products` | 1. Lấy danh sách sản phẩm (Tuân thủ Layered Architecture: Controller -> Service -> Repository) |
| `POST` | `http://localhost:8151/api/products` | 2. Thêm mới sản phẩm |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

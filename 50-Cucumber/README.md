# 50-Cucumber - Cucumber JVM

<p align="left">
  <img src="https://img.shields.io/badge/Port-8150-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Behavior-Driven%20Development%20(BDD)-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Chuyên viên phân tích nghiệp vụ (BA), Tester (QA) và Khách hàng không thể đọc hiểu được code kiểm thử của lập trình viên viết bằng Java. Khoảng cách giao tiếp giữa yêu cầu nghiệp vụ và mã nguồn kiểm thử dẫn tới việc hiểu sai yêu cầu và nghiệm thu phần mềm chậm trễ.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Viết tài liệu đặc tả yêu cầu và kịch bản kiểm thử nghiệm thu bằng ngôn ngữ tự nhiên Gherkin (`Given ... When ... Then ...`).**
- **Tự động liên kết từng bước trong kịch bản Gherkin với code kiểm thử Java để chạy tự động trong quy trình nghiệm thu phần mềm (Acceptance Testing).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Cucumber JVM |
|:---|:---|:---|
| **Raw JUnit 5** | `Developer Testing` | JUnit chỉ dành cho lập trình viên đọc; Cucumber là cầu nối ngôn ngữ giữa BA, QA và Lập trình viên. |
| **JBehave** | `Java BDD` | JBehave là framework BDD đời đầu; Cucumber phổ biến hơn và có hệ sinh thái plugin IDE xuất sắc hơn. |
| **Spock Framework (Groovy)** | `BDD Specification` | Spock viết bằng Groovy rất thanh lịch cho dev; Cucumber viết bằng file text Gherkin cho mọi người cùng đọc được. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Ngôn ngữ Gherkin tự nhiên, dễ đọc cho cả người không biết lập trình (BA, Product Owner, QA).**
- **Tài liệu sống (Living Documentation): Kịch bản kiểm thử vừa là tài liệu nghiệp vụ vừa là bài test tự động chạy thật.**
- **Tích hợp hoàn hảo với Spring TestContext Framework để test toàn trình End-to-End.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Tốn chi phí duy trì việc đồng bộ giữa file kịch bản `.feature` và các hàm Step Definition trong Java.
- Thời gian chạy test chậm hơn nhiều so với các bài Unit Test thuần túy.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các dự án có sự tham gia sâu của BA/QA chuyên trách kiểm thử nghiệm thu tự động (E2E Acceptance Test). KHÔNG NÊN DÙNG: Cho việc kiểm thử chi tiết kỹ thuật ở mức Unit Test nội bộ của lập trình viên.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/cucumber/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/cucumber/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/cucumber/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/cucumber/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/cucumber/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8150
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 50-Cucumber

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8150`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8150/api/products` | 1. Tạo sản phẩm mới |
| `GET` | `http://localhost:8150/api/products` | 2. Lấy danh sách tất cả sản phẩm |
| `GET` | `http://localhost:8150/api/products?category=Audio` | 3. Tìm sản phẩm theo Danh mục (Kịch bản BDD Feature 2) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

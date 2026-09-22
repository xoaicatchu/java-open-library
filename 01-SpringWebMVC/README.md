# 01-SpringWebMVC - Spring Web MVC

<p align="left">
  <img src="https://img.shields.io/badge/Port-8101-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Web%20&%20Transport-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi phát triển REST API doanh nghiệp, nếu dùng Java Servlet thô, lập trình viên phải tự parse JSON, quản lý vòng đời HTTP request/response, tự ánh xạ URL và tự điều phối luồng xử lý. Điều này dẫn tới mã nguồn cồng kềnh, dễ lỗi runtime và khó chuẩn hóa kiến trúc.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Các hệ thống quản trị doanh nghiệp (ERP, CRM, Banking Core) yêu cầu xử lý CRUD nghiệp vụ đồng bộ và chặt chẽ.**
- **Tích hợp Java 21 Virtual Threads (`spring.threads.virtual.enabled: true`) để phục vụ hàng chục ngàn kết nối đồng thời mà không bị cạn kiệt Thread Pool.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Web MVC |
|:---|:---|:---|
| **Spring WebFlux** | `Reactive Framework` | WebFlux hướng sự kiện non-blocking; Web MVC hướng thread-per-request trực quan. |
| **Jakarta JAX-RS (Jersey / RESTEasy)** | `Jakarta EE Standard` | Chuẩn Jakarta EE nhưng hệ sinh thái Spring MVC mạnh hơn và tích hợp Spring Data/Security mượt mà hơn. |
| **Quarkus / Micronaut** | `Cloud-Native Framework` | Khởi động nhanh hơn cho Serverless, nhưng Web MVC vượt trội về độ ổn định và số lượng thư viện tích hợp. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Mô hình đồng bộ trực quan, dễ viết code, dễ debug và cực kỳ phổ biến trong tuyển dụng.**
- **Tương thích hoàn hảo với Java 21 Virtual Threads, giải quyết nhược điểm nghẽn thread mà không cần viết code reactive.**
- **Hệ sinh thái đồ sộ nhất trong thế giới Java (Spring Security, Spring Data, Spring Cloud).**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không hỗ trợ Backpressure tự nhiên như mô hình Reactive Streams.
- Bộ nhớ footprint khởi động lớn hơn các microframework hiện đại.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Là lựa chọn mặc định cho 90% hệ thống REST API doanh nghiệp chuẩn (CRUD, CSDL quan hệ, nghiệp vụ phức tạp). KHÔNG NÊN DÙNG: Khi hệ thống đòi hỏi streaming dữ liệu liên tục (SSE, WebSocket hàng triệu kết nối) với tài nguyên RAM cực thấp.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/webmvc/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/webmvc/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/webmvc/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/webmvc/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/webmvc/dto/CreateProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/webmvc/dto/ProductResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/webmvc/dto/UpdateProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/webmvc/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8101
spring:
  application:
    name: spring-webmvc-demo
  threads:
    virtual:
      enabled: true  # JDK 21 Virtual Threads
  datasource:
    url: jdbc:h2:mem:webmvc_db;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    open-in-view: false
    properties:
      hibernate:
        format_sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 01-SpringWebMVC

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8101`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8101/api/products` | Tạo sản phẩm |
| `POST` | `http://localhost:8101/api/products` | Thực thi POST http://localhost:8101/api/products |
| `POST` | `http://localhost:8101/api/products` | Thực thi POST http://localhost:8101/api/products |
| `GET` | `http://localhost:8101/api/products?page=0&size=10&sort=name,asc` | Thực thi GET http://localhost:8101/api/products?page=0&size=10&sort=name,asc |
| `GET` | `http://localhost:8101/api/products/1` | Thực thi GET http://localhost:8101/api/products/1 |
| `GET` | `http://localhost:8101/api/products/99999` | Thực thi GET http://localhost:8101/api/products/99999 |
| `GET` | `http://localhost:8101/api/products/category/Electronics` | Thực thi GET http://localhost:8101/api/products/category/Electronics |
| `GET` | `http://localhost:8101/api/products/search?keyword=laptop&page=0&size=10` | Thực thi GET http://localhost:8101/api/products/search?keyword=laptop&page=0&size=10 |
| `PUT` | `http://localhost:8101/api/products/1` | Thực thi PUT http://localhost:8101/api/products/1 |
| `DELETE` | `http://localhost:8101/api/products/2` | Thực thi DELETE http://localhost:8101/api/products/2 |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

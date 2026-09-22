# 01-SpringWebMVC - Spring Web MVC

> **Cổng dịch vụ (Server Port)**: `8101`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần xây dựng API CRUD tiêu chuẩn, đồng bộ, dễ bảo trì, tuyển dụng dễ.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
90% ứng dụng quản trị, ERP, CRM, Banking Core nội bộ. Kết hợp **Java 21 Virtual Threads** (`spring.threads.virtual.enabled: true`) giúp phục vụ hàng chục ngàn kết nối đồng thời mà không lo cạn kiệt Thread Pool.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/webmvc/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/webmvc/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/webmvc/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/webmvc/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/webmvc/dto/CreateProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/webmvc/dto/ProductResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/webmvc/dto/UpdateProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/webmvc/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 01-SpringWebMVC

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8101`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
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

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

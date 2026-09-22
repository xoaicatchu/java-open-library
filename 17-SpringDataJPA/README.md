# 17-SpringDataJPA - Spring Data JPA / Hibernate

> **Cổng dịch vụ (Server Port)**: `8117`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Phát triển nhanh các tính năng CRUD chuẩn, quan hệ thực thể 1-n, n-n phức tạp, cần tính năng tự động theo dõi thay đổi (Dirty Checking) và phân trang.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Đa số các tính năng nghiệp vụ ghi/đọc có quan hệ dữ liệu rõ ràng. Dự án mẫu minh họa cách dùng `@EntityGraph` để triệt tiêu lỗi kinh điển **N+1 Query**, JPA Auditing tự động lưu ngày tạo/người tạo.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/datajpa/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/datajpa/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/datajpa/repository/CategoryRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/datajpa/repository/OrderItemRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/datajpa/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/datajpa/config/JpaConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/datajpa/dto/CreateProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/dto/ProductResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/entity/Auditable.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/entity/Category.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/entity/OrderItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/projection/ProductSummaryDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8117
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 17-SpringDataJPA

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8117`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8117/api/products?page=0&size=10` | Get paginated products |
| `GET` | `http://localhost:8117/api/products/1` | Get product by ID |
| `GET` | `http://localhost:8117/api/products/search?name=Laptop&minPrice=1000&maxPrice=3000` | Search products |
| `GET` | `http://localhost:8117/api/products/summaries?name=Laptop` | Get product summaries |
| `POST` | `http://localhost:8117/api/products` | Create product |
| `DELETE` | `http://localhost:8117/api/products/1` | Delete product |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

# 22-HibernateBatch - Hibernate Batch & Bulk

> **Cổng dịch vụ (Server Port)**: `8122`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Insert/Update 100.000 bản ghi bằng `saveAll()` thông thường của JPA sẽ làm tràn RAM (OutOfMemory) và chạy mất hàng giờ.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Tối ưu batch insert với `StatelessSession` (bỏ qua Hibernate first-level cache và dirty checking) và cấu hình `batch_size`. Xử lý bulk update tại mức DB không load entity lên RAM.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/hibernatebatch/controller/BatchController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/hibernatebatch/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/hibernatebatch/repository/CategoryRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/hibernatebatch/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/hibernatebatch/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/hibernatebatch/entity/Category.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/hibernatebatch/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8122
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:batchdb
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: false
        generate_statistics: true
        jdbc:
          batch_size: 50
        order_inserts: true
        order_updates: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 22-HibernateBatch

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8122`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8122/api/batch/import?count=100` | Import 100 products |
| `PUT` | `http://localhost:8122/api/batch/update-prices?category=Electronics&percentage=10` | Update prices by percentage |
| `DELETE` | `http://localhost:8122/api/batch/cleanup?maxPrice=12` | Delete products with maxPrice 12 |
| `GET` | `http://localhost:8122/api/batch/products` | Get all products |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

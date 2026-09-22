# 23-SpringDataMongoDB - Spring Data MongoDB

> **Cổng dịch vụ (Server Port)**: `8123`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Dữ liệu có cấu trúc động, thuộc tính thay đổi liên tục theo từng sản phẩm (ví dụ: quần áo có size/màu, điện thoại có RAM/CPU/bộ nhớ), không phù hợp schema cứng của SQL.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Catalog sản phẩm, lưu trữ giỏ hàng, lịch sử audit log, nội dung bài viết CMS, tài liệu bán hàng JSON.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/mongodb/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/mongodb/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/mongodb/repository/ProductRepository.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/mongodb/dto/CategoryCountDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mongodb/dto/ProductAvgRatingDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mongodb/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mongodb/entity/Review.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: 23-SpringDataMongoDB
  threads:
    virtual:
      enabled: true
  data:
    mongodb:
      uri: mongodb://localhost:27017/testdb
      auto-index-creation: true
server:
  port: 8123
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 23-SpringDataMongoDB

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8123`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8123/api/products` | Create Product 1 |
| `POST` | `http://localhost:8123/api/products` | Create Product 2 |
| `GET` | `http://localhost:8123/api/products` | Get All Products |
| `GET` | `http://localhost:8123/api/products/search?text=laptop` | Search Products |
| `GET` | `http://localhost:8123/api/products/analytics/category-counts` | Get Category Counts |
| `GET` | `http://localhost:8123/api/products/analytics/avg-ratings` | Get Average Ratings |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

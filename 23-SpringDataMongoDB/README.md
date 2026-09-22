# 23-SpringDataMongoDB - Spring Data MongoDB

<p align="left">
  <img src="https://img.shields.io/badge/Port-8123-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-NoSQL%20Document%20Database-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Dữ liệu bán hàng hoặc danh mục sản phẩm thương mại điện tử có cấu trúc biến đổi liên tục (quần áo có size/màu, điện thoại có RAM/chip/pin). Nếu dùng CSDL quan hệ SQL, sẽ phải thiết kế bảng EAV (Entity-Attribute-Value) cực kỳ chậm hoặc liên tục ALTER TABLE gây gián đoạn dịch vụ.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Quản lý danh mục sản phẩm (Product Catalog) với các thuộc tính động không cố định trong TMĐT.**
- **Lưu trữ giỏ hàng, phiên làm việc người dùng, lịch sử audit log và tài liệu JSON kích thước lớn.**
- **Tìm kiếm và định vị theo tọa độ địa lý (Geospatial Queries: tìm cửa hàng gần nhất).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Data MongoDB |
|:---|:---|:---|
| **PostgreSQL JSONB** | `Relational with JSON` | PostgreSQL JSONB rất mạnh cho cấu trúc lai; MongoDB tối ưu hơn cho kiến trúc Sharding phân tán ngang quy mô cực lớn. |
| **Elasticsearch** | `Search Engine` | Elasticsearch tối ưu cho tìm kiếm toàn văn (Full-Text Search); MongoDB là CSDL Document lưu trữ chính (Source of Truth). |
| **Amazon DynamoDB** | `Managed NoSQL` | DynamoDB bị khóa vào hạ tầng AWS; MongoDB có thể chạy On-Premise, Docker hoặc MongoDB Atlas đa nền tảng. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Schema-less linh hoạt: thoải mái thêm bớt trường mà không cần chạy migration bảng.**
- **Khả năng mở rộng ngang (Horizontal Scalability) tuyệt vời nhờ cơ chế Sharding và Replica Set native.**
- **Spring Data MongoDB cung cấp `MongoTemplate` và `MongoRepository` với cú pháp truy vấn mượt mà.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không hỗ trợ các phép JOIN phức tạp hiệu quả như SQL (chỉ có `$lookup` hạn chế).
- Tốn dung lượng đĩa cứng hơn SQL do phải lưu lặp lại các tên trường (keys) trong từng document.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho dữ liệu dạng Document JSON, catalog sản phẩm động, dữ liệu ghi nhanh phân tán. KHÔNG NÊN DÙNG: Cho dữ liệu tài chính kế toán cần ràng buộc toàn vẹn khóa ngoại nghiêm ngặt (ACID Foreign Keys).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/mongodb/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/mongodb/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/mongodb/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/mongodb/dto/CategoryCountDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mongodb/dto/ProductAvgRatingDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mongodb/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mongodb/entity/Review.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 23-SpringDataMongoDB

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8123`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8123/api/products` | Create Product 1 |
| `POST` | `http://localhost:8123/api/products` | Create Product 2 |
| `GET` | `http://localhost:8123/api/products` | Get All Products |
| `GET` | `http://localhost:8123/api/products/search?text=laptop` | Search Products |
| `GET` | `http://localhost:8123/api/products/analytics/category-counts` | Get Category Counts |
| `GET` | `http://localhost:8123/api/products/analytics/avg-ratings` | Get Average Ratings |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

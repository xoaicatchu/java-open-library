# 23-SpringDataMongoDB - Spring Data MongoDB

<p align="left">
  <img src="https://img.shields.io/badge/Port-8123-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-NoSQL%20Document%20Database-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
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

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

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

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

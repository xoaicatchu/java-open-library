# 18-MyBatis - MyBatis

<p align="left">
  <img src="https://img.shields.io/badge/Port-8118-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Data%20Access%20&%20SQL%20Mapping-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Trong các hệ thống tài chính và ngân hàng, các câu lệnh SQL cực kỳ phức tạp với hàng chục phép JOIN, DBA yêu cầu phải kiểm soát chặt chẽ từng ký tự SQL để tối ưu hóa Index. Các công cụ ORM như Hibernate sinh SQL tự động quá rối và không thể can thiệp sâu.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Ứng dụng tính thuế, báo cáo tài chính ngân hàng nơi mà cấu trúc câu lệnh SQL do chuyên gia DBA tối ưu riêng.**
- **Xây dựng các câu truy vấn tìm kiếm động phức tạp với nhiều điều kiện tùy chọn bằng thẻ XML (`<where>`, `<if>`, `<foreach>`).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với MyBatis |
|:---|:---|:---|
| **Hibernate / JPA** | `Full ORM` | Hibernate ánh xạ Bảng thành Đối tượng; MyBatis ánh xạ Câu lệnh SQL thành Đối tượng. |
| **jOOQ** | `Type-Safe SQL` | jOOQ viết SQL bằng Java code type-safe; MyBatis viết SQL trong file XML tách rời. |
| **Spring JdbcTemplate** | `Raw JDBC Helper` | JdbcTemplate phải viết chuỗi SQL cứng trong Java; MyBatis hỗ trợ Dynamic SQL XML mạnh mẽ hơn. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Kiểm soát tuyệt đối 100% câu lệnh SQL: DBA có thể vào file XML để tối ưu index mà không cần compile lại code Java.**
- **Cơ chế Dynamic SQL đỉnh cao: dễ dàng lắp ghép điều kiện tìm kiếm bằng XML mà không sợ nối chuỗi gây SQL Injection.**
- **Hiệu năng cực cao, gần như tương đương JDBC thuần, không bị gánh nặng bộ nhớ từ First-level cache hay Dirty Checking của ORM.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Phải tự tay viết toàn bộ câu lệnh SQL cho cả những thao tác CRUD cơ bản.
- Không tự động cập nhật schema database khi thực thể Java thay đổi.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Các hệ thống tài chính, báo cáo phức tạp, dự án có DBA chuyên trách tối ưu SQL. KHÔNG NÊN DÙNG: Dự án startup cần phát triển tính năng CRUD thần tốc và thường xuyên thay đổi cấu trúc bảng.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/mybatis/controller/CategoryController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.
- `com/example/mybatis/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/mybatis/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/mybatis/handler/ProductStatusTypeHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/mybatis/service/CategoryService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/mybatis/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/mybatis/mapper/CategoryMapper.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/mybatis/mapper/ProductMapper.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/mybatis/dto/CategoryResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mybatis/dto/CreateCategoryRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mybatis/dto/CreateProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mybatis/dto/ProductResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mybatis/dto/UpdateProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mybatis/entity/Category.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mybatis/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mybatis/entity/ProductStatus.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8118
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  sql:
    init:
      mode: always
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.example.mybatis.entity
  type-handlers-package: com.example.mybatis.handler
  configuration:
    map-underscore-to-camel-case: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 18-MyBatis

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8118`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8118/api/products?page=0&size=10` | 1. Lấy danh sách sản phẩm (kèm danh mục qua <resultMap> association & phân trang RowBounds) |
| `GET` | `http://localhost:8118/api/products/1` | 2. Lấy chi tiết sản phẩm theo ID |
| `GET` | `http://localhost:8118/api/products/search?name=Lap&minPrice=1000&maxPrice=2000&status=ACTIVE,INACTIVE` | 3. Tìm kiếm động (Dynamic SQL với <where>, <if>, <foreach> theo tên, giá, trạng thái) |
| `GET` | `http://localhost:8118/api/products/search?minPrice=500&maxPrice=1500` | 4. Tìm kiếm động chỉ theo khoảng giá |
| `POST` | `http://localhost:8118/api/products` | 5. Thêm mới sản phẩm (Sử dụng TypeHandler tùy chỉnh cho ProductStatus Enum) |
| `PUT` | `http://localhost:8118/api/products/1` | 6. Cập nhật sản phẩm (MyBatis Dynamic <set> update) |
| `DELETE` | `http://localhost:8118/api/products/3` | 7. Xóa sản phẩm theo ID |
| `GET` | `http://localhost:8118/api/categories` | 8. Lấy danh mục sản phẩm (Annotation-based @Select Mapper) |
| `POST` | `http://localhost:8118/api/categories` | 9. Thêm mới danh mục (Annotation-based @Insert với @Options sinh key tự động) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

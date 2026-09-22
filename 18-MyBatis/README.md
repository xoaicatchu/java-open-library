# 18-MyBatis - MyBatis

> **Cổng dịch vụ (Server Port)**: `8118`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Câu query quá phức tạp, DBA yêu cầu kiểm soát chặt chẽ từng dòng SQL, Hibernate sinh SQL quá rối và khó tối ưu index.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Các ứng dụng tài chính, báo cáo thuế, ngân hàng. Lập trình viên viết trực tiếp câu lệnh SQL linh hoạt bằng thẻ XML (`<if>`, `<foreach>`), tối ưu hiệu năng tối đa.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/mybatis/controller/CategoryController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.
- `com/example/mybatis/controller/ProductController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/mybatis/exception/GlobalExceptionHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/mybatis/handler/ProductStatusTypeHandler.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/mybatis/service/CategoryService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.
- `com/example/mybatis/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/mybatis/mapper/CategoryMapper.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/mybatis/mapper/ProductMapper.java`: Thao tác truy vấn và lưu trữ dữ liệu.

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

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 18-MyBatis

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8118`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
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

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

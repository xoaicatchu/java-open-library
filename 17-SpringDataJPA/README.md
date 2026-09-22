# 17-SpringDataJPA - Spring Data JPA / Hibernate

<p align="left">
  <img src="https://img.shields.io/badge/Port-8117-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Data%20Access%20&%20ORM-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Viết mã nguồn JDBC thô để ánh xạ từng dòng dữ liệu từ ResultSet sang Object Java tốn hàng ngàn dòng code nhàm chán; quản lý quan hệ 1-N, N-N thủ công rất dễ gặp lỗi rò rỉ dữ liệu hoặc lỗi hiệu năng N+1 Query làm sập cơ sở dữ liệu.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Các ứng dụng quản lý nghiệp vụ chuẩn mực có quan hệ thực thể rõ ràng (Khách hàng - Đơn hàng - Chi tiết đơn).**
- **Tự động quản lý lịch sử bản ghi (JPA Auditing) với `@CreatedDate`, `@LastModifiedBy` và tự động cập nhật thay đổi qua Dirty Checking.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Data JPA / Hibernate |
|:---|:---|:---|
| **MyBatis** | `SQL Mapper` | MyBatis viết SQL tay linh hoạt; JPA tự sinh SQL và quản lý trạng thái thực thể tự động. |
| **jOOQ** | `Type-Safe SQL` | jOOQ mạnh về các câu query phân tích phức tạp; JPA mạnh về các luồng ghi/sửa dữ liệu theo mô hình hướng đối tượng. |
| **Spring Data JDBC** | `Simple Data Access` | Spring Data JDBC không có first-level cache và lazy loading, đơn giản hơn JPA nhưng ít tính năng hơn. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tốc độ phát triển tính năng cực nhanh: chỉ cần khai báo interface là có sẵn các phương thức CRUD, phân trang và sắp xếp.**
- **Cơ chế Dirty Checking: chỉ cần sửa object trong transaction, Hibernate tự động sinh câu `UPDATE` chính xác.**
- **Hỗ trợ tối ưu truy vấn bằng `@EntityGraph` để triệt tiêu hoàn toàn lỗi N+1 Query.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Mức độ trừu tượng cao, nếu lập trình viên không hiểu sâu về vòng đời Entity (Detached, Managed) rất dễ gây lỗi hiệu năng.
- Không phù hợp cho các câu truy vấn báo cáo tổng hợp phức tạp (Window functions, CTE, Pivot bảng).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Lựa chọn mặc định cho hầu hết ứng dụng nghiệp vụ OLTP ghi/đọc theo thực thể. KHÔNG NÊN DÙNG: Cho hệ thống phân tích dữ liệu (OLAP), báo cáo thống kê phức tạp hoặc batch insert số lượng cực lớn.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/datajpa/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/datajpa/service/ProductService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/datajpa/repository/CategoryRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/datajpa/repository/OrderItemRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/datajpa/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/datajpa/config/JpaConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/datajpa/dto/CreateProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/dto/ProductResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/entity/Auditable.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/entity/Category.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/entity/OrderItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/datajpa/projection/ProductSummaryDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 17-SpringDataJPA

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8117`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8117/api/products?page=0&size=10` | Get paginated products |
| `GET` | `http://localhost:8117/api/products/1` | Get product by ID |
| `GET` | `http://localhost:8117/api/products/search?name=Laptop&minPrice=1000&maxPrice=3000` | Search products |
| `GET` | `http://localhost:8117/api/products/summaries?name=Laptop` | Get product summaries |
| `POST` | `http://localhost:8117/api/products` | Create product |
| `DELETE` | `http://localhost:8117/api/products/1` | Delete product |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

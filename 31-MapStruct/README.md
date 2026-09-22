# 31-MapStruct - MapStruct

> **Cổng dịch vụ (Server Port)**: `8131`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Viết code `getX()` rồi `setY()` giữa Entity và DTO tốn hàng ngàn dòng vô nghĩa; còn dùng `BeanUtils.copyProperties` hay `ModelMapper` thì dùng Reflection quá chậm và dễ lỗi runtime.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Sinh code mapping thuần túy ở bước Compile-time. Tốc độ tương đương code tay thủ công, kiểm tra kiểu dữ liệu an toàn 100%.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/mapstruct/controller/OrderController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/mapstruct/mapper/CustomerMapper.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/mapstruct/mapper/OrderMapper.java`: Thao tác truy vấn và lưu trữ dữ liệu.
- `com/example/mapstruct/mapper/ProductMapper.java`: Thao tác truy vấn và lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/mapstruct/dto/CategoryDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mapstruct/dto/CustomerInfo.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mapstruct/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mapstruct/dto/ProductSummary.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mapstruct/entity/Category.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mapstruct/entity/Customer.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mapstruct/entity/Order.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mapstruct/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8131
spring:
  application:
    name: mapstruct-demo
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 31-MapStruct

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8131`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8131/api/orders/map` | Map Order to OrderResponse |
| `GET` | `http://localhost:8131/api/orders/sample` | Get Sample Order |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

# 53-SpringDocOpenAPI - SpringDoc OpenAPI

<p align="left">
  <img src="https://img.shields.io/badge/Port-8153-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-API%20Documentation%20&%20Swagger%20UI-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Đội ngũ Backend sửa đổi code hoặc thêm tham số mới trong REST Controller nhưng quên cập nhật file tài liệu Excel/Word gửi cho đội Frontend và Mobile. Kết quả là các bên bị lệch thông số, kết nối API liên tục thất bại và mất thời gian họp đối chiếu.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Tự động phân tích mã nguồn Java Spring Boot để sinh tài liệu chuẩn OpenAPI 3.0 và giao diện trực quan **Swagger UI** tại `/swagger-ui.html`.**
- **Cho phép đội ngũ lập trình viên gọi thử nghiệm trực tiếp API trên trình duyệt web kèm theo cơ chế cấu hình Bearer JWT Token.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với SpringDoc OpenAPI |
|:---|:---|:---|
| **Springfox (Swagger 2)** | `Legacy Swagger` | Springfox đã bị 'bỏ hoang' không cập nhật từ lâu và không tương thích Spring Boot 3; SpringDoc là thư viện kế nhiệm chính thức hiện đại. |
| **Manual Postman Collections** | `Manual Export` | Postman export phải làm thủ công; SpringDoc tự động cập nhật ngay khi code Java thay đổi. |
| **OpenAPI Generator** | `API-First Generator` | SpringDoc đi theo hướng Code-First (từ code sinh tài liệu); OpenAPI Generator đi theo hướng API-First (từ tài liệu sinh code). |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tự động 100%: Chỉ cần cài dependency là có ngay giao diện Swagger UI đẹp mắt mà không cần cấu hình phức tạp.**
- **Hỗ trợ hoàn hảo Spring Boot 3.x, Java 21 Records, JSR-303 Bean Validation và Spring Security.**
- **Giao diện trực quan hỗ trợ test nhanh API, xem schema dữ liệu và tải về file `openapi.json`.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Lập trình viên có xu hướng lười viết mô tả chi tiết nếu chỉ dựa hoàn toàn vào việc tự sinh mặc định của thư viện.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Tiêu chuẩn bắt buộc cho mọi ứng dụng REST API theo hướng tiếp cận Code-First. KHÔNG NÊN DÙNG: Khi dự án áp dụng nghiêm ngặt quy trình API-First (viết YAML trước rồi sinh code bằng OpenAPI Generator).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/springdoc/controller/v1/ProductControllerV1.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.
- `com/example/springdoc/controller/v2/OrderControllerV2.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/springdoc/service/ApiService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/springdoc/repository/OrderRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.
- `com/example/springdoc/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/springdoc/config/OpenAPIConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/springdoc/dto/OrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/dto/ProductRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/dto/ProductResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/entity/Order.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/springdoc/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8153
spring:
  application:
    name: 53-SpringDocOpenAPI
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: false
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
  group-configs:
    - group: v1
      paths-to-match: /api/v1/**
    - group: v2
      paths-to-match: /api/v2/**
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 53-SpringDocOpenAPI

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8153`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8153/v3/api-docs` | 1. Xem đặc tả OpenAPI Specification JSON toàn bộ hệ thống |
| `GET` | `http://localhost:8153/v3/api-docs/products-v1` | 2. Xem đặc tả OpenAPI Nhóm V1 - Products |
| `GET` | `http://localhost:8153/v3/api-docs/orders-v2` | 3. Xem đặc tả OpenAPI Nhóm V2 - Orders |
| `GET` | `http://localhost:8153/api/v1/products` | 4. Lấy danh sách sản phẩm (V1 API) |
| `POST` | `http://localhost:8153/api/v1/products` | 5. Thêm mới sản phẩm (V1 API) |
| `GET` | `http://localhost:8153/api/v2/orders` | 6. Lấy danh sách đơn hàng (V2 API) |
| `POST` | `http://localhost:8153/api/v2/orders` | 7. Tạo đơn hàng mới (V2 API) |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

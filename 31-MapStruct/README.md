# 31-MapStruct - MapStruct

<p align="left">
  <img src="https://img.shields.io/badge/Port-8131-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Data%20Mapping%20&%20Transformation-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Việc viết code thủ công hàng ngàn dòng `dto.setName(entity.getName())`, `dto.setPrice(...)` giữa Entity và DTO cực kỳ tốn thời gian và nhàm chán. Trong khi đó, dùng các thư viện ánh xạ tự động cũ như `ModelMapper` hay `BeanUtils.copyProperties` lại sử dụng Java Reflection gây chậm hiệu năng hàng chục lần và tiềm ẩn lỗi runtime do không kiểm tra kiểu dữ liệu.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Chuyển đổi hai chiều giữa Domain Entities (JPA) và API DTOs (Request/Response Records) trong kiến trúc phân lớp Clean Architecture.**
- **Chuyển đổi dữ liệu phân cấp phức tạp (Nested Objects, List mapping, ánh xạ enum, format ngày tháng).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với MapStruct |
|:---|:---|:---|
| **ModelMapper** | `Runtime Reflection` | ModelMapper dùng Reflection chậm và hay map nhầm trường cùng tên; MapStruct sinh code thuần ở Compile-time siêu nhanh và an toàn. |
| **Spring BeanUtils** | `Shallow Reflection Copy` | BeanUtils chỉ copy nông, không map được kiểu dữ liệu khác nhau và không cảnh báo lỗi compile. |
| **Manual Mapping Code** | `Hand-written` | Code tay rất nhanh nhưng tốn hàng ngàn dòng code 'rác' khó bảo trì; MapStruct sinh code thay lập trình viên. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Hiệu năng tuyệt đối: Sinh mã nguồn Java thuần túy ở bước Compile-Time (Annotation Processing), tốc độ chạy ngang ngửa code viết tay, không tốn 1 nano-giây Reflection.**
- **An toàn kiểu dữ liệu 100%: Sai tên trường hoặc không tương thích kiểu dữ liệu sẽ báo lỗi ngay khi build project.**
- **Tích hợp mượt mà với Spring Component (`componentModel = "spring"`) và hỗ trợ hoàn hảo Java 21 Records.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cần cấu hình plugin Annotation Processor trong `pom.xml` cẩn thận (đặc biệt khi kết hợp với Lombok hoặc compiler plugins).
- Mỗi khi sửa đổi cấu trúc DTO/Entity phải trigger compile lại để MapStruct tái sinh mã mới.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Tiêu chuẩn bắt buộc cho 100% các ứng dụng Java Spring Boot chuyên nghiệp để map DTO và Entity. KHÔNG NÊN DÙNG: Hầu như không có lý do để không dùng (trừ các dự án quá nhỏ chỉ có 1-2 model đơn giản).**

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

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 31-MapStruct

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8131`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8131/api/orders/map` | Map Order to OrderResponse |
| `GET` | `http://localhost:8131/api/orders/sample` | Get Sample Order |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

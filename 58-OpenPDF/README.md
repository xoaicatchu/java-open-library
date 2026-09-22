# 58-OpenPDF - OpenPDF

> **Cổng dịch vụ (Server Port)**: `8158`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Thư viện iText nổi tiếng nhưng bản mới (iText 7) dính bản quyền thương mại AGPL rất đắt và nguy cơ vi phạm bản quyền phần mềm nguồn đóng.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Fork mã nguồn mở (giấy phép thân thiện LGPL) từ iText 4. Dùng để sinh hóa đơn điện tử PDF, vé máy bay, hợp đồng lao động có chèn logo và chữ ký số.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/openpdf/controller/PdfController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/openpdf/service/PdfService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/openpdf/dto/InvoiceDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/openpdf/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8158
spring:
  application:
    name: 58-OpenPDF
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:openpdfdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 58-OpenPDF

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8158`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8158/api/pdf/invoice/1001` | 1. Xuất hóa đơn bán hàng PDF (Bảng chi tiết, căn lề, format tiền tệ) |
| `GET` | `http://localhost:8158/api/pdf/report` | 2. Xuất báo cáo tổng hợp PDF (Header, Footer, phân trang tự động) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

# 58-OpenPDF - OpenPDF

<p align="left">
  <img src="https://img.shields.io/badge/Port-8158-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-PDF%20Generation%20&%20Invoicing-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Thư viện tạo PDF nổi tiếng nhất iText từ phiên bản 5 và 7 đã chuyển sang giấy phép thương mại AGPL rất khắt khe. Các doanh nghiệp sử dụng trong ứng dụng nguồn đóng (proprietary software) có nguy cơ đối mặt với các vụ kiện tụng vi phạm bản quyền phần mềm hàng trăm ngàn USD.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Sinh hóa đơn điện tử PDF, phiếu giao hàng, vé máy bay, hợp đồng lao động có chèn logo công ty và watermark chống làm giả.**
- **Tự động xuất bảng biểu chứng từ kế toán ra file PDF định dạng chuẩn in ấn A4.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với OpenPDF |
|:---|:---|:---|
| **iText 7** | `Commercial PDF Engine` | iText 7 rất mạnh nhưng dính bản quyền thương mại đắt đỏ; OpenPDF là fork mã nguồn mở từ iText 4 với giấy phép thân thiện LGPL/MPL. |
| **Apache PDFBox** | `Low-level PDF Tool` | PDFBox chuyên về đọc và can thiệp file PDF có sẵn; OpenPDF tiện lợi hơn nhiều trong việc tạo mới file PDF có bảng biểu. |
| **Flying Saucer / WeasyPrint** | `HTML-to-PDF Tool` | Chuyển từ HTML/CSS sang PDF rất tiện nhưng khó kiểm soát chính xác từng pixel in ấn như OpenPDF. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Giấy phép mã nguồn mở thân thiện (LGPL và MPL), hoàn toàn an toàn và miễn phí cho ứng dụng thương mại của doanh nghiệp.**
- **Kế thừa toàn bộ sự ổn định và mạnh mẽ của nhánh iText kinh điển.**
- **Hỗ trợ đầy đủ font chữ Unicode tiếng Việt, bảng biểu phức tạp (`PdfPTable`), chèn ảnh, mã vạch (Barcode/QR code).**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Lập trình giao diện PDF bằng code Java đòi hỏi phải tính toán tọa độ và căn chỉnh thủ công.
- Không hỗ trợ trực tiếp việc render CSS Flexbox/Grid hiện đại như các engine HTML-to-PDF.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Sinh hóa đơn, hợp đồng, chứng từ in ấn PDF chuẩn xác trong ứng dụng doanh nghiệp mà không lo bản quyền. KHÔNG NÊN DÙNG: Khi bạn đã có sẵn file HTML/CSS đẹp và muốn chuyển đổi 1-1 sang PDF mà không muốn viết code dựng layout.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/openpdf/controller/PdfController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/openpdf/service/PdfService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/openpdf/dto/InvoiceDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/openpdf/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 58-OpenPDF

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8158`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8158/api/pdf/invoice/1001` | 1. Xuất hóa đơn bán hàng PDF (Bảng chi tiết, căn lề, format tiền tệ) |
| `GET` | `http://localhost:8158/api/pdf/report` | 2. Xuất báo cáo tổng hợp PDF (Header, Footer, phân trang tự động) |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

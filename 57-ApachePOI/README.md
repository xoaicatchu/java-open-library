# 57-ApachePOI - Apache POI

<p align="left">
  <img src="https://img.shields.io/badge/Port-8157-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Excel%20&%20Office%20Document%20Processing-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi người dùng bấm xuất báo cáo thống kê 500.000 dòng ra file Excel `.xlsx`, việc sử dụng thư viện DOM thông thường (`XSSFWorkbook`) sẽ nạp toàn bộ cây đối tượng ô tính vào bộ nhớ Heap, lập tức làm máy chủ cạn kiệt RAM và sập ứng dụng (OutOfMemoryError).

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Xuất các file báo cáo Excel doanh nghiệp quy mô hàng trăm ngàn dòng theo cơ chế Streaming (`SXSSFWorkbook`) với dung lượng RAM tiêu thụ cố định chỉ vài chục MB.**
- **Định dạng bảng biểu phức tạp: kẻ viền ô, tô màu cảnh báo theo điều kiện, chèn công thức toán học (`SUM`, `AVERAGE`), gộp ô (Merge Cells).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Apache POI |
|:---|:---|:---|
| **Alibaba EasyExcel** | `High-Performance Excel` | EasyExcel tối ưu bộ nhớ cực kỳ đơn giản; Apache POI là thư viện nền tảng chuẩn mực toàn diện nhất thế giới Office. |
| **Aspose.Cells** | `Commercial Library` | Aspose rất mạnh nhưng phí bản quyền cực kỳ đắt đỏ; Apache POI hoàn toàn mã nguồn mở miễn phí. |
| **JExcelAPI (jxl)** | `Legacy Tool` | jxl đã dừng phát triển từ lâu và chỉ hỗ trợ định dạng `.xls` cổ xưa tối đa 65.536 dòng. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Thư viện mạnh mẽ nhất để thao tác với toàn bộ định dạng Microsoft Office (Excel, Word, PowerPoint).**
- **Mô hình Streaming `SXSSFWorkbook` (Streaming User Model) cho phép ghi dữ liệu dạng flush tạm xuống đĩa, giải quyết triệt để bài toán OutOfMemory.**
- **Hỗ trợ đầy đủ 100% tính năng định dạng, font chữ, màu sắc và công thức của Microsoft Excel.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Mô hình XSSFWorkbook thông thường ngốn rất nhiều RAM nếu không biết cách dùng SXSSF.
- API khá dài dòng và đòi hỏi nhiều code chi tiết để căn chỉnh từng ô tính.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Xuất file Excel báo cáo đẹp mắt, có công thức và định dạng phức tạp trong doanh nghiệp. KHÔNG NÊN DÙNG: Khi chỉ cần xuất dữ liệu thô dạng bảng đơn giản không cần định dạng màu mè (khi đó file CSV bằng OpenCSV nhanh hơn gấp 10 lần).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/poi/controller/ExcelController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/poi/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/poi/service/ExcelService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/poi/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/poi/dto/ProductDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/poi/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: apache-poi-demo
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  threads:
    virtual:
      enabled: true
server:
  port: 8157
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 57-ApachePOI

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8157`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8157/api/excel/export` | Tải xuống file Excel chứa danh sách sản phẩm |
| `POST` | `http://localhost:8157/api/excel/import` | Lưu ý: Cần sử dụng client hỗ trợ multipart/form-data để đính kèm file |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

# 59-OpenCSV - OpenCSV

<p align="left">
  <img src="https://img.shields.io/badge/Port-8159-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-CSV%20Parsing%20&%20Generation-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Nhiều lập trình viên tự viết code đọc file CSV bằng hàm `line.split(",")`. Cách làm này sẽ lập tức phát sinh lỗi sai lệch dữ liệu nghiêm trọng ngay khi một trường dữ liệu có chứa dấu phẩy bên trong dấu ngoặc kép (ví dụ: `"Hà Nội, Việt Nam"`, hoặc dữ liệu chứa ký tự xuống dòng `\n`).

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Đọc và nạp các tệp tin CSV danh sách khách hàng, sao kê ngân hàng người dùng tải lên hệ thống.**
- **Xuất dữ liệu thô hàng triệu dòng ra file CSV siêu tốc để nhập vào các hệ thống phân tích dữ liệu Data Warehouse.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với OpenCSV |
|:---|:---|:---|
| **Apache Commons CSV** | `Lightweight CSV Parser` | Commons CSV rất tốt nhưng thiên về xử lý record thủ công; OpenCSV hỗ trợ cơ chế Bean Mapping (tự động map dòng CSV vào Java Record) tiện lợi hơn. |
| **FastCSV** | `Ultra-Fast Parser` | FastCSV có tốc độ thô cực nhanh cho Big Data; OpenCSV cân bằng hoàn hảo giữa hiệu năng và tính năng mapping phong phú. |
| **line.split(",")** | `Naive Approach` | Tuyệt đối không dùng vì phá vỡ chuẩn RFC 4180 khi dữ liệu có dấu phẩy hoặc ngoặc kép. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tuân thủ nghiêm ngặt tiêu chuẩn quốc tế RFC 4180 về định dạng file CSV.**
- **Hỗ trợ mapping tự động hai chiều giữa file CSV và Java Bean/Record qua các annotation `@CsvBindByName`.**
- **Hỗ trợ tùy biến linh hoạt ký tự phân cách (dấu phẩy, chấm phẩy, tab), ký tự bao bọc ngoặc kép và cơ chế xử lý lỗi theo dòng.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Tốc độ xử lý thô chậm hơn một chút so với thư viện FastCSV nếu xử lý các file CSV khổng lồ hàng chục Gigabytes.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Tiêu chuẩn bắt buộc cho mọi thao tác đọc/ghi file CSV trong ứng dụng Java. KHÔNG NÊN DÙNG: Tuyệt đối không bao giờ tự parse CSV bằng `split(",")`.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/opencsv/controller/CsvController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/opencsv/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/opencsv/service/CsvService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/opencsv/repository/ProductRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/opencsv/entity/Product.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8159
spring:
  application:
    name: opencsv-demo
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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 59-OpenCSV

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8159`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8159/api/csv/export` | 1. Xuất danh sách sản phẩm ra tệp CSV (StatefulBeanToCsv) |
| `POST` | `http://localhost:8159/api/csv/import` | Đính kèm file CSV trong form-data multipart |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

# 54-OpenAPIGenerator - OpenAPI Generator

<p align="left">
  <img src="https://img.shields.io/badge/Port-8154-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-API-First%20Code%20Generation-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Mỗi khi Backend thay đổi trường dữ liệu, đội Frontend (TypeScript) và Mobile (Flutter/Swift) phải ngồi gõ lại các Model class bằng tay. Quá trình này vừa nhàm chán vừa dễ gõ sai chính tả, gây lỗi không tương thích giữa các nền tảng.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Áp dụng quy trình **API-First**: Thiết kế và thống nhất file đặc tả `api-spec.yaml` giữa các team trước khi viết code.**
- **Maven Plugin tự động sinh ra toàn bộ Interface Controller phía Server và SDK gọi API phía Client cho hơn 40 ngôn ngữ khác nhau.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với OpenAPI Generator |
|:---|:---|:---|
| **Swagger Codegen** | `Legacy Generator` | OpenAPI Generator là bản fork cộng đồng độc lập phát triển mạnh mẽ và cập nhật nhanh hơn Swagger Codegen rất nhiều. |
| **SpringDoc (Code-First)** | `Code-First Documentation` | SpringDoc viết code rồi sinh spec; OpenAPI Generator viết spec trước rồi sinh code, tối ưu cho quy trình làm việc song song. |
| **Manual Coding** | `Hand-written Code` | Tự code hai đầu tốn thời gian và hay lệch hợp đồng; tự động sinh mã đảm bảo 100% khớp hợp đồng API. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Quy trình API-First chuẩn mực: Frontend và Backend có thể bắt đầu làm việc song song ngay sau khi chốt file YAML đặc tả.**
- **Hỗ trợ mô hình Delegate Pattern (`delegatePattern = true`): code tự sinh nằm ở thư mục riêng, code nghiệp vụ viết ở class Delegate, không sợ bị ghi đè khi re-generate.**
- **Sinh SDK cho hầu hết mọi ngôn ngữ lập trình (TypeScript, Dart, Swift, Go, Python, C#).**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cần cấu hình plugin Maven/Gradle khá chi tiết để tùy biến package và đường dẫn file sinh ra.
- Cần kỷ luật cao trong việc cập nhật file YAML khi có yêu cầu thay đổi nghiệp vụ.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Các dự án lớn có nhiều team đa nền tảng (Web, Mobile, Backend) làm việc song song. KHÔNG NÊN DÙNG: Các dự án nhỏ chỉ có 1 lập trình viên full-stack tự làm từ A-Z (khi đó Code-First với SpringDoc nhanh hơn).**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8154
spring:
  application:
    name: 54-OpenAPIGenerator
  threads:
    virtual:
      enabled: true
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 54-OpenAPIGenerator

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8154`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8154/products` | 1. Lấy danh sách sản phẩm (Generated Server Stub + Delegate Pattern) |
| `POST` | `http://localhost:8154/products` | 2. Thêm mới sản phẩm (Tự động validate schema sinh từ api-spec.yaml) |
| `GET` | `http://localhost:8154/products/1` | 3. Lấy sản phẩm theo ID |
| `PUT` | `http://localhost:8154/products/1` | 4. Cập nhật sản phẩm |
| `DELETE` | `http://localhost:8154/products/1` | 5. Xóa sản phẩm theo ID |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

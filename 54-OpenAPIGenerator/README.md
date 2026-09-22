# 54-OpenAPIGenerator - OpenAPI Generator

> **Cổng dịch vụ (Server Port)**: `8154`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Mỗi khi Backend đổi model, đội Frontend (React/TypeScript) và Mobile (Flutter/Kotlin) phải tự tay ngồi gõ lại code Model và API Client, rất dễ sai sót.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Hướng tiếp cận **API-First**. Viết file đặc tả `api-spec.yaml` trước, sau đó plugin tự động sinh ra toàn bộ code Server Interface (Java) và Client SDK (TypeScript/Dart/Swift).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/openapigen/controller/ProductsApiDelegateImpl.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/openapigen/service/ProductService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 54-OpenAPIGenerator

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8154`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8154/products` | 1. Lấy danh sách sản phẩm (Generated Server Stub + Delegate Pattern) |
| `POST` | `http://localhost:8154/products` | 2. Thêm mới sản phẩm (Tự động validate schema sinh từ api-spec.yaml) |
| `GET` | `http://localhost:8154/products/1` | 3. Lấy sản phẩm theo ID |
| `PUT` | `http://localhost:8154/products/1` | 4. Cập nhật sản phẩm |
| `DELETE` | `http://localhost:8154/products/1` | 5. Xóa sản phẩm theo ID |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

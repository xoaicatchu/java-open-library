# 60-Jackson - Jackson Advanced

> **Cổng dịch vụ (Server Port)**: `8160`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cùng một API, người dùng thông thường chỉ được xem 3 trường tóm tắt, còn Admin được xem toàn bộ 10 trường; hoặc cần parse đối tượng đa hình (Polymorphic JSON: Con mèo, Con chó cùng kế thừa Động vật).

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Các tính năng nâng cao của Jackson: `@JsonView` (lọc thuộc tính trả về theo vai trò), `@JsonTypeInfo` (đa hình), Custom Serializer (format tiền tệ, ngày tháng ISO-8601), MixIn (gắn annotation vào class thư viện thứ 3).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/jackson/controller/MoneyController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.
- `com/example/jackson/controller/NotificationController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/jackson/config/JacksonConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8160
spring:
  application:
    name: jackson-demo
  threads:
    virtual:
      enabled: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 60-Jackson

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8160`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8160/api/notifications` | POST Email Notification |
| `POST` | `http://localhost:8160/api/notifications` | POST SMS Notification |
| `GET` | `http://localhost:8160/api/notifications/{{id}}/summary` | GET Summary (Public View) |
| `GET` | `http://localhost:8160/api/notifications/{{id}}/detail` | GET Detail (Internal View) |
| `GET` | `http://localhost:8160/api/money/sample` | GET Money Sample (Custom Serializer) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

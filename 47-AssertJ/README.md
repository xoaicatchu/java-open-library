# 47-AssertJ - AssertJ

> **Cổng dịch vụ (Server Port)**: `8147`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Câu lệnh `assertEquals(expected, actual)` của JUnit truyền thống báo lỗi rất tối nghĩa và viết dài.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Thư viện viết assertion dễ đọc nhất thế giới: `assertThat(order.getTotal()).isGreaterThan(0)`. Cho phép so sánh sâu toàn bộ cấu trúc object phức tạp (`usingRecursiveComparison`) và gom lỗi (`SoftAssertions`).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/assertj/controller/OrderController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8147
spring:
  threads:
    virtual:
      enabled: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 47-AssertJ

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8147`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8147/api/orders/sample` | Get Sample Order |
| `POST` | `http://localhost:8147/api/orders/validate` | Validate Valid Order |
| `POST` | `http://localhost:8147/api/orders/validate` | Validate Invalid Order |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

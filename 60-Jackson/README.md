# 60-Jackson - Jackson Advanced

<p align="left">
  <img src="https://img.shields.io/badge/Port-8160-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-JSON%20Serialization%20&%20Data%20Binding-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Trong thực tế, một API cần ẩn giấu một số trường dữ liệu đối với người dùng thường nhưng lại hiển thị cho Admin; hoặc cần tuần hoàn các đối tượng đa hình (Polymorphism: class cha `Notification` có các con `EmailNotification`, `SmsNotification`). Nếu chỉ dùng JSON parser cơ bản, việc này sẽ đòi hỏi hàng tá code `if/else` thủ công phức tạp.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Phân quyền hiển thị thuộc tính JSON theo vai trò người dùng bằng `@JsonView`.**
- **Xử lý tuần hoàn và giải mã đối tượng đa hình (Polymorphic Deserialization) bằng `@JsonTypeInfo` và `@JsonSubTypes`.**
- **Viết các bộ Serializer / Deserializer tùy biến định dạng tiền tệ hoặc ngày tháng đặc thù.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Jackson Advanced |
|:---|:---|:---|
| **Google Gson** | `Lightweight JSON Library` | Gson đơn giản hơn cho dự án nhỏ; Jackson là thư viện mặc định của Spring Boot, mạnh hơn về tính năng nâng cao và hiệu năng. |
| **Alibaba Fastjson 2** | `High-Speed JSON Parser` | Fastjson rất nhanh ở Trung Quốc nhưng trong quá khứ dính nhiều lỗ hổng bảo mật nghiêm trọng; Jackson an toàn và ổn định hơn nhiều. |
| **Moshi** | `Modern Android JSON` | Moshi tối ưu hóa cho Kotlin và Android; Jackson là chuẩn công nghiệp cho Backend Java Enterprise. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Thư viện tuần hoàn JSON toàn diện và phổ biến nhất thế giới Java, là trái tim của Spring Web.**
- **Khả năng mở rộng vô hạn: Custom Serializer/Deserializer, MixIn (gắn annotation vào class của thư viện thứ 3), Module hỗ trợ Java 21 Record và Java Time.**
- **Tốc độ xử lý và khả năng tối ưu hóa bộ nhớ cực kỳ xuất sắc.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cần cẩn trọng cấu hình `DefaultTyping` để tránh các lỗ hổng bảo mật Remote Code Execution (RCE) khi deserialize dữ liệu không tin cậy.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Tiêu chuẩn số 1 cho mọi thao tác xử lý JSON trong Spring Boot. KHÔNG NÊN DÙNG: Không có lý do loại bỏ.**

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

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 60-Jackson

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8160`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8160/api/notifications` | POST Email Notification |
| `POST` | `http://localhost:8160/api/notifications` | POST SMS Notification |
| `GET` | `http://localhost:8160/api/notifications/{{id}}/summary` | GET Summary (Public View) |
| `GET` | `http://localhost:8160/api/notifications/{{id}}/detail` | GET Detail (Internal View) |
| `GET` | `http://localhost:8160/api/money/sample` | GET Money Sample (Custom Serializer) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

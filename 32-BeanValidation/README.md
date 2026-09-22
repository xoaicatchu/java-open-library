# 32-BeanValidation - Jakarta Bean Validation

<p align="left">
  <img src="https://img.shields.io/badge/Port-8132-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Data%20Integrity%20&%20Validation-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Lập trình viên phải viết hàng trăm câu lệnh `if (req.getName() == null || req.getName().trim().isEmpty())`, `if (req.getAge() < 18)` rải rác khắp các hàm Service. Điều này làm rác code nghiệp vụ, dễ bỏ sót lỗ hổng dữ liệu bẩn và phản hồi thông báo lỗi không đồng nhất cho client.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Kiểm tra tính hợp lệ của toàn bộ dữ liệu đầu vào (Input Sanitization) ngay tại cửa ngõ Controller bằng `@Valid`.**
- **Xây dựng các bộ kiểm tra tùy biến (Custom Constraint Validators): validate số điện thoại Việt Nam, mật khẩu phức tạp, kiểm tra trùng lặp.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Jakarta Bean Validation |
|:---|:---|:---|
| **Manual if-else Validation** | `Hand-written Code` | Viết tay làm bẩn code service và dễ bỏ sót; Bean Validation dùng annotation khai báo sạch sẽ và chuẩn hóa. |
| **Spring Validator Interface** | `Spring Programmatic` | Spring Validator phải viết class riêng dài dòng; Bean Validation chỉ cần gắn annotation trực tiếp trên field/record. |
| **Apache Commons Validator** | `Utility Library` | Commons Validator chỉ là các hàm tiện ích kiểm tra chuỗi; Bean Validation là tiêu chuẩn chính thức của toàn bộ hệ sinh thái Java. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tiêu chuẩn chính thức của Jakarta EE (Jakarta Validation Specification) được Spring Boot tích hợp nguyên bản.**
- **Khai báo rõ ràng, súc tích bằng các annotation: `@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Max`, `@Email`, `@Pattern`.**
- **Tự động kết hợp với ProblemDetail (RFC 9457) để trả về danh sách chi tiết các trường bị lỗi cho Frontend.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không phù hợp cho các quy tắc kiểm tra logic nghiệp vụ phức tạp liên quan tới trạng thái CSDL (ví dụ: số dư có đủ trừ không).
- Các rule validate phức tạp giữa nhiều trường (Cross-field validation) đòi hỏi phải viết Custom Class-level Annotation.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Bắt buộc áp dụng cho 100% các DTO đầu vào của Controller để chặn đứng dữ liệu bẩn ngay ở cửa vào. KHÔNG NÊN DÙNG: Để thay thế các logic nghiệp vụ lõi (Business Rules) thuộc về tầng Domain Service.**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8132
spring:
  threads:
    virtual:
      enabled: true
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 32-BeanValidation

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8132`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8132/users/register` | Register User |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

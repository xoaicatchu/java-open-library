# 32-BeanValidation - Jakarta Bean Validation

<p align="left">
  <img src="https://img.shields.io/badge/Port-8132-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Data%20Integrity%20&%20Validation-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
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

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/validation/controller/UserController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/validation/exception/GlobalExceptionHandler.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/validation/service/UserService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/validation/dto/AddressDto.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/validation/dto/UserRegistrationRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/validation/dto/ValidationGroups.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8132
spring:
  threads:
    virtual:
      enabled: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

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

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

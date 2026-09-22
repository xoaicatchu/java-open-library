# 36-SpringSecurity - Spring Security 6

<p align="left">
  <img src="https://img.shields.io/badge/Port-8136-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Enterprise%20Security%20&%20Auth-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Tự viết code lọc bảo mật (Custom Filter) rất dễ bỏ sót các lỗ hổng bảo mật nghiêm trọng (CSRF, Session Fixation, Clickjacking, Insecure Password Hashing). Ngoài ra, việc quản lý phân quyền vai trò (RBAC) thủ công khiến mã nguồn rối rắm và khó kiểm toán bảo mật.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Bảo vệ toàn bộ hệ thống REST API bằng cơ chế phi trạng thái (Stateless API) xác thực qua JWT Bearer Token.**
- **Mã hóa mật khẩu một chiều an toàn bằng thuật toán BCrypt với Salt tự động.**
- **Phân quyền chi tiết tới từng phương thức nghiệp vụ bằng `@PreAuthorize("hasRole('ADMIN')")`.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Security 6 |
|:---|:---|:---|
| **Apache Shiro** | `Java Security Framework` | Shiro nhẹ hơn nhưng không cập nhật nhanh và không tích hợp sâu bằng Spring Security vào hệ sinh thái Spring. |
| **Sa-Token** | `Lightweight Auth Library` | Sa-Token rất nổi tiếng ở cộng đồng châu Á vì nhẹ; Spring Security là tiêu chuẩn vàng cấp tập đoàn toàn cầu. |
| **Custom JWT Filter** | `Ad-hoc Solution` | Tự viết filter dễ dính lỗi bảo mật nguy hiểm và không tận dụng được kiến trúc `SecurityContext` của Spring. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Khung bảo mật toàn diện và an toàn nhất thế giới cho ứng dụng Java Enterprise.**
- **Cấu hình chuẩn hóa hiện đại bằng `SecurityFilterChain` Bean hoàn toàn không còn class kế thừa `WebSecurityConfigurerAdapter` cũ kỹ.**
- **Tích hợp sẵn các cơ chế bảo vệ phòng chống tấn công phổ biến nhất của OWASP.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Hệ thống Filter Chain đồ sộ khiến việc debug khi cấu hình sai trở nên khá khó khăn đối với người mới.
- Nhiều tài liệu trên mạng đã lỗi thời do Spring Security 6 có nhiều thay đổi lớn về cú pháp Lambda DSL.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Tiêu chuẩn bảo mật bắt buộc cho mọi ứng dụng doanh nghiệp. KHÔNG NÊN DÙNG: Chỉ khi dự án là một tool dòng lệnh (CLI) nội bộ không có kết nối mạng và không cần phân quyền.**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8136
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:securitydb
    driverClassName: org.h2.Driver
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration: 86400000 # 24 hours
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 36-SpringSecurity

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8136`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8136/api/auth/register` | 1. Đăng ký tài khoản USER mới |
| `POST` | `http://localhost:8136/api/auth/register` | 2. Đăng ký tài khoản ADMIN mới |
| `POST` | `http://localhost:8136/api/auth/login` | @name loginAdmin |
| `POST` | `http://localhost:8136/api/auth/login` | @name loginUser |
| `GET` | `http://localhost:8136/api/products` | 5. Lấy danh sách sản phẩm (Yêu cầu Token - Bất kỳ Role nào) |
| `POST` | `http://localhost:8136/api/products` | 6. Thêm sản phẩm mới (Chỉ dành cho ADMIN - @PreAuthorize("hasRole('ADMIN')")) |
| `POST` | `http://localhost:8136/api/products` | 7. Thử dùng USER thêm sản phẩm (Mong đợi: 403 Forbidden) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

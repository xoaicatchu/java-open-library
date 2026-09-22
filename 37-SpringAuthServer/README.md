# 37-SpringAuthServer - Spring Authorization Server

<p align="left">
  <img src="https://img.shields.io/badge/Port-8137-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-OAuth2%20&%20OpenID%20Connect%20Provider-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Doanh nghiệp sở hữu nhiều ứng dụng (App Web, App Mobile, Hệ thống Đối tác) cần một cổng đăng nhập tập trung (Single Sign-On). Nếu tự viết logic sinh và kiểm tra Token sẽ không tương thích với chuẩn bảo mật quốc tế OAuth2/OIDC, làm các bên thứ 3 không thể tích hợp được.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Xây dựng máy chủ định danh và cấp quyền độc lập (OAuth2 Authorization Server) cho toàn bộ hệ sinh thái của tập đoàn.**
- **Hỗ trợ chuẩn OAuth2 Authorization Code Flow with PKCE cho Mobile/SPA và Client Credentials Flow cho giao tiếp Server-to-Server.**
- **Tự động quản lý và phát hành cặp khóa ký số RSA qua endpoint chuẩn JWKS (`/.well-known/jwks.json`).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Authorization Server |
|:---|:---|:---|
| **Keycloak** | `Standalone IAM Solution` | Keycloak là phần mềm cài đặt hoàn chỉnh có giao diện quản trị đồ sộ; Spring Auth Server là framework nhúng trực tiếp vào code Java. |
| **Auth0 / Okta** | `Cloud SaaS Identity` | Auth0 rất mạnh nhưng chi phí SaaS đắt đỏ theo lượng user; Spring Auth Server hoàn toàn miễn phí và tự chủ dữ liệu On-Premise. |
| **Spring Security OAuth (Legacy)** | `Deprecated Project` | Dự án cũ đã bị khai tử; Spring Authorization Server là dự án chính thức mới thay thế. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Dự án chính thức của Spring Team triển khai đầy đủ các đặc tả kỹ thuật OAuth 2.1 và OpenID Connect 1.0.**
- **Cho phép tùy biến 100% giao diện đăng nhập, quy trình cấp consent, xác thực 2 bước (2FA) bằng code Java.**
- **Nhẹ nhàng, triển khai đóng gói chung vào file JAR chạy trực tiếp.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không có sẵn giao diện Admin Web đồ sộ để tạo user/client bằng chuột như Keycloak (phải tự code giao diện quản lý).
- Đòi hỏi lập trình viên phải hiểu sâu về các luồng ủy quyền (Grant Types) của chuẩn OAuth 2.1.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Khi muốn tự phát triển một Identity Provider riêng biệt bằng code Java, cần kiểm soát sâu logic xác thực và không muốn phụ thuộc vào phần mềm bên ngoài. KHÔNG NÊN DÙNG: Khi cần một giải pháp có sẵn giao diện quản trị user, social login phong phú ngay lập tức mà không muốn code (khi đó nên chọn Keycloak).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/authserver/controller/DemoController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/authserver/config/AuthorizationServerConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.
- `com/example/authserver/config/SecurityConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8137
spring:
  application:
    name: auth-server
  threads:
    virtual:
      enabled: true
logging:
  level:
    org.springframework.security: TRACE
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 37-SpringAuthServer

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8137`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8137/.well-known/openid-configuration` | 1. OpenID Connect Discovery Endpoint |
| `GET` | `http://localhost:8137/.well-known/oauth-authorization-server` | 2. OAuth2 Authorization Server Metadata |
| `GET` | `http://localhost:8137/oauth2/jwks` | 3. JWK Set Endpoint (Lấy Public Key dùng để xác minh chữ ký JWT Token) |
| `POST` | `http://localhost:8137/oauth2/token` | 4. Cấp Access Token bằng Client Credentials Flow (messaging-client / secret) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

# 37-SpringAuthServer - Spring Authorization Server

> **Cổng dịch vụ (Server Port)**: `8137`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Cần tự làm một máy chủ cấp quyền OAuth2/OpenID Connect (như Google/Facebook Login) để các đối tác hoặc ứng dụng nội bộ đăng nhập.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Máy chủ Identity độc lập. Hỗ trợ chuẩn OAuth2 Authorization Code Flow with PKCE, Client Credentials Flow, tự phát hành cặp khóa RSA ký số Token JWKS (`/oauth2/token`, `/.well-known/openid-configuration`).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/authserver/controller/DemoController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/authserver/config/AuthorizationServerConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.
- `com/example/authserver/config/SecurityConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 37-SpringAuthServer

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8137`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8137/.well-known/openid-configuration` | 1. OpenID Connect Discovery Endpoint |
| `GET` | `http://localhost:8137/.well-known/oauth-authorization-server` | 2. OAuth2 Authorization Server Metadata |
| `GET` | `http://localhost:8137/oauth2/jwks` | 3. JWK Set Endpoint (Lấy Public Key dùng để xác minh chữ ký JWT Token) |
| `POST` | `http://localhost:8137/oauth2/token` | 4. Cấp Access Token bằng Client Credentials Flow (messaging-client / secret) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

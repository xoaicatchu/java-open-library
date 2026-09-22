# 38-KeycloakIntegration - Keycloak Integration

<p align="left">
  <img src="https://img.shields.io/badge/Port-8138-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Identity%20&%20Access%20Management%20(IAM)-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Công ty không muốn bỏ ra hàng tháng trời để tự code các tính năng phức tạp: Đăng ký, Đăng nhập, Quên mật khẩu, Xác thực OTP 2FA, Đăng nhập bằng Google/Facebook, Quản lý phân quyền Role/Group. Tự làm các tính năng này vừa tốn chi phí vừa tiềm ẩn nguy cơ bảo mật.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Cấu hình Spring Boot làm **OAuth2 Resource Server** tin cậy máy chủ Keycloak tập trung.**
- **Tự động xác minh chữ ký JWT token và phân giải vai trò (Keycloak Realm Roles / Client Roles) thành các quyền hạn Spring Security Authorities.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Keycloak Integration |
|:---|:---|:---|
| **Custom Spring Auth Server** | `In-house Auth Server` | Tự code Auth Server tốn nhiều công sức bảo trì; Keycloak là phần mềm nguồn mở số 1 thế giới do Red Hat hậu thuẫn. |
| **AWS Cognito / Firebase Auth** | `Cloud Identity` | Cognito bị phụ thuộc vào AWS; Keycloak có thể chạy tự do trên Docker, Kubernetes hoặc On-Premise. |
| **Okta / Ping Identity** | `Enterprise SaaS` | Okta rất đắt đỏ; Keycloak hoàn toàn miễn phí mã nguồn mở. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Giải pháp IAM hoàn chỉnh nhất thế giới: giao diện Admin Web trực quan, hỗ trợ User Federation (LDAP, Active Directory), Social Login, 2FA/MFA out-of-the-box.**
- **Spring Boot tích hợp cực kỳ nhẹ nhàng qua dependency chuẩn `spring-boot-starter-oauth2-resource-server`.**
- **Tách biệt hoàn toàn trách nhiệm quản lý danh tính (Identity Management) ra khỏi mã nguồn nghiệp vụ.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Cần tài nguyên server riêng để vận hành cụm Keycloak (yêu cầu cấu hình RAM và CSDL riêng cho Keycloak).
- Cấu trúc claim roles trong JWT của Keycloak (`realm_access.roles`) khác với chuẩn mặc định của Spring nên cần viết một JwtAuthenticationConverter nhỏ.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Lựa chọn tối ưu cho toàn bộ các doanh nghiệp cần hệ thống đăng nhập tập trung (SSO) chuyên nghiệp đầy đủ tính năng. KHÔNG NÊN DÙNG: Cho các ứng dụng cá nhân siêu nhỏ không có hạ tầng để chạy server Keycloak riêng.**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8138
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:keycloakdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/myrealm
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 38-KeycloakIntegration

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8138`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8138/api/products` | Thực thi GET http://localhost:8138/api/products |
| `POST` | `http://localhost:8138/api/products` | Thực thi POST http://localhost:8138/api/products |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

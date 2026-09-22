# 35-SpringRestClient - Spring 6.1 RestClient

<p align="left">
  <img src="https://img.shields.io/badge/Port-8135-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Synchronous%20HTTP%20Client-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
`RestTemplate` kinh điển đã quá cũ và chuyển sang trạng thái Maintenance Mode (không phát triển tính năng mới). Trong khi đó, nếu muốn dùng cú pháp Fluent API hiện đại của `WebClient`, lập trình viên lại bắt buộc phải kéo theo toàn bộ module `spring-boot-starter-webflux` và Netty rất nặng nề và phức tạp.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Khách hàng HTTP đồng bộ hiện đại nhất cho Spring Boot 3.2+ để gọi các REST API của bên thứ 3.**
- **Sử dụng tính năng `@HttpExchange` khai báo HTTP Client bằng Interface tiện lợi tương tự OpenFeign mà không cần cài Spring Cloud.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring 6.1 RestClient |
|:---|:---|:---|
| **Spring RestTemplate** | `Legacy Spring Client` | RestTemplate cũ kỹ cú pháp thô; RestClient có Fluent API mượt mà và hỗ trợ Project Loom Virtual Threads tốt hơn. |
| **Spring WebClient** | `Reactive Client` | WebClient yêu cầu cài đặt WebFlux; RestClient hoạt động trực tiếp trên Spring Web MVC đồng bộ. |
| **Apache HttpClient 5** | `Raw HTTP Client` | Apache Client thô và phức tạp; RestClient là lớp bọc thanh lịch chuẩn mực của Spring. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Được phát triển chính thức từ Spring Framework 6.1, là giải pháp thay thế hoàn hảo cho RestTemplate.**
- **Cú pháp Fluent API trực quan: `restClient.get().uri(...).retrieve().body(...)`.**
- **Hỗ trợ HTTP Interfaces (`@GetExchange`, `@PostExchange`) sinh implementation tự động mà không cần dependency ngoài.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Chỉ khả dụng từ Spring Boot 3.2 trở lên (không dùng được trên các phiên bản Spring Boot 2.x cũ).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Là lựa chọn chuẩn mực hàng đầu cho mọi nhu cầu gọi REST API đồng bộ trên Spring Boot 3.2+. KHÔNG NÊN DÙNG: Khi ứng dụng là Reactive WebFlux toàn phần (khi đó bắt buộc dùng WebClient).**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8135
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:restclientdb
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 35-SpringRestClient

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8135`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8135/api/products` | 1. Tạo sản phẩm mới |
| `GET` | `http://localhost:8135/api/products` | 2. Lấy danh sách sản phẩm |
| `GET` | `http://localhost:8135/api/products/1` | 3. Lấy sản phẩm theo ID |
| `PUT` | `http://localhost:8135/api/products/1` | 4. Cập nhật sản phẩm |
| `DELETE` | `http://localhost:8135/api/products/1` | 5. Xóa sản phẩm |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

# 03-SpringModulith - Spring Modulith

<p align="left">
  <img src="https://img.shields.io/badge/Port-8103-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Enterprise%20Architecture-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Ứng dụng Monolith qua thời gian thường bị suy thoái kiến trúc thành 'Big Ball of Mud': các package gọi chéo phụ thuộc vòng tròn, sửa một chỗ làm hỏng chỗ khác, và việc chia tách sang Microservices trở nên vô cùng tốn kém và rủi ro.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Thiết kế kiến trúc Modular Monolith chuẩn Domain-Driven Design (DDD) ngay từ đầu cho dự án doanh nghiệp.**
- **Tự động sinh tài liệu kiến trúc (C4 Model, PlantUML component diagram) trực tiếp từ code test trên CI/CD.**
- **Điều phối sự kiện nghiệp vụ nội bộ bất đồng bộ có bảo đảm (Transactional Outbox Pattern).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Modulith |
|:---|:---|:---|
| **Microservices** | `Distributed System` | Microservices tách rời vật lý nhưng mang lại chi phí vận hành mạng cực lớn; Modulith giữ kiến trúc sạch trong 1 JVM duy nhất. |
| **ArchUnit** | `Static Architecture Testing` | ArchUnit kiểm tra quy tắc tĩnh qua test; Modulith cung cấp cả quy tắc kiến trúc lẫn runtime event pub/sub tích hợp. |
| **Java Platform Module System (JPMS)** | `JVM Module` | JPMS quá cứng nhắc ở mức JVM classpath; Modulith hoạt động tự nhiên ở mức Spring ApplicationContext. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Hưởng trọn lợi thế của Monolith (deploy đơn giản, ACID transaction) nhưng có sự phân rã ranh giới miền sạch sẽ.**
- **Tự động phát hiện vi phạm ranh giới package-private thông qua Unit Test.**
- **Tích hợp sẵn Transactional Event Publication: đảm bảo event nội bộ không bị mất khi database commit.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Đòi hỏi các lập trình viên trong đội ngũ phải có tư duy thiết kế DDD vững chắc.
- Không thể scale độc lập tài nguyên CPU/RAM cho từng module con như Microservices.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho hầu hết các dự án backend mới cần tốc độ phát triển nhanh, kiến trúc sạch, chi phí vận hành thấp. KHÔNG NÊN DÙNG: Khi các module được phát triển bởi các team độc lập có chu kỳ release hoàn toàn lệch pha hoặc sử dụng các ngôn ngữ lập trình khác nhau.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/modulith/order/controller/OrderController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/modulith/notification/NotificationService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/modulith/order/OrderService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/modulith/order/OrderRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/modulith/order/OrderCreatedEvent.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/modulith/order/dto/CreateOrderRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/modulith/order/dto/OrderResponse.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8103
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:modulithdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 03-SpringModulith

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8103`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8103/api/orders` | Create Order |
| `GET` | `http://localhost:8103/api/orders` | Get All Orders |
| `GET` | `http://localhost:8103/api/orders/1` | Get Order By ID |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

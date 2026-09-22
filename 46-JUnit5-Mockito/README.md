# 46-JUnit5-Mockito - JUnit 5 + Mockito

<p align="left">
  <img src="https://img.shields.io/badge/Port-8146-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Unit%20Testing%20&%20Mocking-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Viết Unit Test cho tầng Service nhưng Service lại phụ thuộc vào Database thật và các API bên thứ 3. Kết quả là test chạy rất chậm (mất hàng phút), không thể chạy khi mất mạng và thường xuyên bị 'flaky' (lúc pass lúc fail do dữ liệu trong DB bị thay đổi).

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Kiểm thử đơn vị (Unit Test) cô lập hoàn toàn logic nghiệp vụ bằng cách giả lập (mock) toàn bộ Repository và External Services.**
- **Kiểm tra việc gọi hàm và bắt tham số truyền vào bằng `ArgumentCaptor` và `verify()`.**
- **Chạy hàng trăm bộ dữ liệu kiểm thử khác nhau trên cùng 1 logic bằng test tham số hóa `@ParameterizedTest`.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với JUnit 5 + Mockito |
|:---|:---|:---|
| **JUnit 4** | `Legacy Testing` | JUnit 4 đã cũ kỹ; JUnit 5 (Jupiter) có kiến trúc module hiện đại, hỗ trợ Lambda, Nested Tests, Parameterized Tests. |
| **TestNG** | `Testing Framework` | TestNG mạnh về cấu hình test suite; JUnit 5 là tiêu chuẩn số 1 tuyệt đối trong hệ sinh thái Spring Boot. |
| **PowerMock** | `Bytecode Mocking` | PowerMock can thiệp bytecode để mock static/private method nhưng là 'code smell' và không tương thích Java mới; Mockito hiện đại đã hỗ trợ mock static an toàn. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Bộ đôi kiểm thử tiêu chuẩn vàng của ngành công nghiệp phần mềm Java.**
- **Mockito cung cấp cú pháp khai báo hành vi giả lập cực kỳ tự nhiên: `when(service.call()).thenReturn(...)`.**
- **Chạy cực nhanh trong RAM (hàng ngàn test case chạy trong vài giây) giúp lập trình viên tự tin refactor mã nguồn theo phương pháp TDD.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Nếu lập trình viên lạm dụng mock quá nhiều (Over-mocking) sẽ khiến bài test chỉ kiểm tra implementation chi tiết thay vì kiểm tra hành vi thực tế (Behavior).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Tiêu chuẩn bắt buộc phải có cho 100% các ứng dụng Java để đạt độ bao phủ kiểm thử (Code Coverage) chất lượng cao. KHÔNG NÊN DÙNG: Không có lý do loại bỏ.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/junit5/controller/OrderController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/junit5/service/NotificationService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.
- `com/example/junit5/service/OrderService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🗄️ Tầng Dữ Liệu & Truy Vấn (Repositories / Mappers)
- `com/example/junit5/repository/OrderRepository.java`: Thao tác truy vấn và tương tác với tầng lưu trữ dữ liệu.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/junit5/entity/Order.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/junit5/entity/OrderStatus.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8146
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password: 
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 46-JUnit5-Mockito

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8146`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8146/api/orders` | Create Order |
| `GET` | `http://localhost:8146/api/orders/1` | Get Order |
| `PUT` | `http://localhost:8146/api/orders/1/cancel` | Cancel Order |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

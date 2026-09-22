# 56-SpringStatemachine - Spring Statemachine

<p align="left">
  <img src="https://img.shields.io/badge/Port-8156-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Finite%20State%20Machine%20(FSM)-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Vòng đời trạng thái đơn hàng trong thương mại điện tử (`CREATED -> PAID -> SHIPPING -> DELIVERED -> CANCELLED`) nếu chỉ quản lý bằng một cột chuỗi String đơn giản trong CSDL sẽ rất dễ bị client gọi nhảy cóc (chưa thanh toán đã bấm giao hàng thành công), gây thất thoát tài sản nghiêm trọng.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Quản lý máy trạng thái hữu hạn (FSM) cho các thực thể trọng yếu: Đơn hàng, Hợp đồng điện tử, Phiếu xuất kho.**
- **Chặn đứng mọi hành vi chuyển đổi trạng thái phi lý bằng các điều kiện kiểm tra an toàn (Guard Conditions) và tự động kích hoạt hành động (Actions) khi chuyển trạng thái.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring Statemachine |
|:---|:---|:---|
| **Custom Enum State Pattern** | `Design Pattern Code` | Tự code pattern bằng Enum đơn giản nhưng dễ bị phân tán logic khi số lượng trạng thái tăng lên; Spring Statemachine chuẩn hóa FSM chuyên nghiệp. |
| **Squirrel-Foundation** | `Lightweight FSM` | Squirrel rất nhẹ; Spring Statemachine tích hợp hoàn hảo với Spring Event và Spring Security. |
| **Camunda BPMN** | `Heavyweight Workflow` | Camunda quá nặng nề cho một FSM đơn giản; Spring Statemachine nhẹ nhàng hơn nhiều, chạy trực tiếp trong memory. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Mô hình hóa rõ ràng: Cấu hình bảng chuyển trạng thái (State Transition Table) trực quan bằng Fluent Builder DSL.**
- **Hỗ trợ đầy đủ các khái niệm FSM cao cấp: Guard, Action, Error Handling, Hierarchical States (trạng thái lồng nhau).**
- **Ngăn chặn 100% các lỗi logic nghiệp vụ do đổi trạng thái bất hợp lệ.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Mức độ phức tạp cao hơn so với việc tự viết State Pattern bằng Enum đơn giản.
- Cần cấu hình StateMachineContext để nạp và lưu trạng thái từ CSDL vào máy trạng thái.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các thực thể có vòng đời trạng thái phức tạp với nhiều điều kiện chuyển tiếp (Đơn hàng TMĐT, Quy trình vé hỗ trợ Ticket). KHÔNG NÊN DÙNG: Cho các đối tượng chỉ có 2-3 trạng thái đơn giản (Bật/Tắt, Kích hoạt/Khóa -> dùng Enum là đủ).**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8156
spring:
  threads:
    virtual:
      enabled: true
  datasource:
    url: jdbc:h2:mem:orderdb;DB_CLOSE_DELAY=-1
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 56-SpringStatemachine

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8156`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8156/api/orders?description=New%20Order` | Create Order |
| `POST` | `http://localhost:8156/api/orders/1/events?event=SUBMIT` | Submit Order |
| `POST` | `http://localhost:8156/api/orders/1/events?event=PAY` | Pay Order |
| `GET` | `http://localhost:8156/api/orders/1` | Get Order |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

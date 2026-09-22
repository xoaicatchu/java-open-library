# 55-Camunda - Camunda Platform 7

> **Cổng dịch vụ (Server Port)**: `8155`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Quy trình phê duyệt vay vốn ngân hàng hoặc duyệt đơn bảo hiểm rất phức tạp (qua nhiều phòng ban, chờ lãnh đạo duyệt, nếu quá 3 ngày tự động hủy). Nếu dùng code `if/else` sẽ thành thảm họa.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Quản trị quy trình doanh nghiệp (BPMN 2.0). BA vẽ quy trình trực quan bằng sơ đồ kéo thả, nhúng engine vào Spring Boot để tự động hóa: điều phối User Task, Service Task, Gateway điều kiện.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/camunda/controller/ProcessController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.
- `com/example/camunda/delegate/ProcessOrderDelegate.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.
- `com/example/camunda/delegate/RejectOrderDelegate.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/camunda/dto/StartProcessRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: camunda-app
  datasource:
    url: jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  threads:
    virtual:
      enabled: true
server:
  port: 8155
camunda.bpm:
  admin-user:
    id: admin
    password: admin
  database:
    schema-update: true
  filter:
    create: All tasks
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 55-Camunda

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8155`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8155/api/orders/start` | Start Process |
| `GET` | `http://localhost:8155/api/orders/tasks` | Get Tasks |
| `POST` | `http://localhost:8155/api/orders/tasks/{taskId}/complete?approved=true` | Replace {taskId} with actual ID from GET response |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

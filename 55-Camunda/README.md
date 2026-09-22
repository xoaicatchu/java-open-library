# 55-Camunda - Camunda Platform 7

<p align="left">
  <img src="https://img.shields.io/badge/Port-8155-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-BPMN%202.0%20Workflow%20Automation-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Quy trình phê duyệt nghiệp vụ phức tạp (ví dụ: hồ sơ vay vốn ngân hàng, duyệt chi tài chính, bồi thường bảo hiểm) đi qua nhiều phòng ban, có thời hạn xử lý (SLA), có điều kiện phân nhánh rắc rối. Nếu dùng code `if/else` và cờ database thông thường sẽ biến thành thảm họa code không thể bảo trì.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Tự động hóa quy trình nghiệp vụ doanh nghiệp (BPMN 2.0): điều phối các tác vụ người dùng (User Tasks) và tác vụ hệ thống (Service Tasks).**
- **Xử lý các luồng phê duyệt dài hạn có thời gian chờ (Long-running Processes) với khả năng lưu giữ trạng thái bền bỉ.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Camunda Platform 7 |
|:---|:---|:---|
| **Flowable / Activiti** | `BPMN Engines` | Cùng chung gốc rễ; Camunda 7 có công cụ thiết kế Camunda Modeler và Cockpit quản trị tiến trình xuất sắc nhất. |
| **Camunda 8 (Zeebe)** | `Cloud-Native Workflow` | Camunda 8 tối ưu cho kiến trúc phân tán microservices quy mô cực lớn; Camunda 7 nhúng gọn gàng trực tiếp vào Spring Boot Monolith/Microservice. |
| **Custom State Pattern** | `Hand-written Code` | Tự code khó xử lý các bài toán timeout, retry, timer boundary event và không thể trực quan hóa cho BA xem. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Chuẩn hóa quốc tế BPMN 2.0: Business Analyst có thể trực tiếp vẽ quy trình bằng phần mềm kéo thả, lập trình viên chỉ việc gắn code xử lý vào các node.**
- **Quản lý trạng thái quy trình kiên cố trong Database: hệ thống bị tắt đột ngột, khi bật lại quy trình sẽ tiếp tục chạy từ đúng điểm dừng.**
- **Cung cấp giao diện web Cockpit và Tasklist giúp đội vận hành theo dõi trực tiếp vị trí các hồ sơ đang bị tắc nghẽn.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Hệ thống cơ sở dữ liệu tạo ra hơn 40 bảng nội bộ (`ACT_RU_...`, `ACT_HI_...`) làm tăng độ phức tạp của DB.
- Cần đội ngũ hiểu rõ về đặc tả BPMN 2.0 để thiết kế sơ đồ quy trình đúng chuẩn.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các quy trình nghiệp vụ có sự tham gia của con người (phê duyệt hồ sơ, tuyển dụng, mua sắm) hoặc luồng xử lý kéo dài nhiều ngày. KHÔNG NÊN DÙNG: Cho các thao tác xử lý dữ liệu tốc độ cao mili-giây (khi đó nên dùng Spring Statemachine hoặc code thuần).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/camunda/controller/ProcessController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.
- `com/example/camunda/delegate/ProcessOrderDelegate.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.
- `com/example/camunda/delegate/RejectOrderDelegate.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/camunda/dto/StartProcessRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 55-Camunda

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8155`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8155/api/orders/start` | Start Process |
| `GET` | `http://localhost:8155/api/orders/tasks` | Get Tasks |
| `POST` | `http://localhost:8155/api/orders/tasks/{taskId}/complete?approved=true` | Replace {taskId} with actual ID from GET response |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

# 16-SpringWebSocket - Spring WebSocket + STOMP

<p align="left">
  <img src="https://img.shields.io/badge/Port-8116-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Real-Time%20Web%20Communication-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Để cập nhật dữ liệu mới cho người dùng, client web phải liên tục gửi request thăm dò (HTTP Polling) mỗi vài giây. Điều này làm lãng phí 95% tài nguyên máy chủ với các request rỗng và tạo ra độ trễ lớn trong trải nghiệm người dùng.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Ứng dụng Chat thời gian thực, bảng kết quả đấu giá trực tuyến, bảng điều khiển trạng thái xe ôm công nghệ trên bản đồ.**
- **Hệ thống thông báo đẩy (Notification Bell) nhảy chuông tức thì trên giao diện quản trị khi có đơn hàng mới.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Spring WebSocket + STOMP |
|:---|:---|:---|
| **Server-Sent Events (SSE)** | `One-way Stream` | SSE chỉ truyền 1 chiều từ Server xuống Client qua HTTP; WebSocket hỗ trợ 2 chiều toàn phần (Full-Duplex). |
| **HTTP Long Polling** | `Fallback Mechanism` | Long Polling ngốn nhiều tài nguyên kết nối; WebSocket chỉ mở 1 kết nối TCP duy nhất. |
| **Socket.io** | `Node.js Library` | Socket.io là thư viện cho Node.js; Spring WebSocket + STOMP là chuẩn doanh nghiệp cho Java. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Kết nối 2 chiều toàn phần (Full-Duplex): Cả Server và Client đều có thể chủ động gửi tin nhắn bất kỳ lúc nào.**
- **Giao thức STOMP trên nền WebSocket giúp định tuyến tin nhắn theo topic (`/topic`, `/queue`) dễ dàng như Message Broker.**
- **Hỗ trợ kết nối với External Message Broker thật (như RabbitMQ/ActiveMQ) để scale ngang nhiều server.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Duy trì hàng trăm ngàn kết nối TCP đồng thời đòi hỏi cấu hình tối ưu bộ nhớ và connection limit trên máy chủ.
- Khó cấu hình Load Balancer hơn HTTP thông thường (cần cấu hình Sticky Sessions hoặc External Broker).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Ứng dụng chat, game web, màn hình giám sát thời gian thực 2 chiều. KHÔNG NÊN DÙNG: Khi chỉ cần server đẩy tin tức 1 chiều xuống client (ví dụ bảng giá chứng khoán, thông báo log -> khi đó nên dùng SSE nhẹ nhàng hơn).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/websocket/controller/ChatController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.
- `com/example/websocket/controller/MessageApiController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/websocket/service/ChatService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/websocket/config/WebSocketConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/websocket/dto/ChatMessage.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8116
spring:
  threads:
    virtual:
      enabled: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 16-SpringWebSocket

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8116`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8116/api/messages` | Send message via REST |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

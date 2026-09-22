# 14-EclipsePaho - Eclipse Paho MQTT 5

<p align="left">
  <img src="https://img.shields.io/badge/Port-8114-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-IoT%20&%20Low-Bandwidth%20Messaging-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Các thiết bị IoT, đồng hồ thông minh, cảm biến nhà xưởng có phần cứng yếu, chạy pin và kết nối qua sóng 2G/3G chập chờn. Giao thức HTTP quá nặng nề (overhead header lớn, duy trì kết nối tốn pin) không thể đáp ứng được.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Hệ thống giám sát trạm quan trắc môi trường, cảm biến nhiệt độ nhà máy, thiết bị Smart Home.**
- **Ứng dụng định vị và theo dõi lộ trình xe container, xe giao hàng trong điều kiện mạng di động yếu.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Eclipse Paho MQTT 5 |
|:---|:---|:---|
| **HiveMQ MQTT Client** | `Async MQTT Client` | HiveMQ tối ưu hơn cho lập trình Reactive; Paho là thư viện chuẩn mực lâu đời được Eclipse Foundation bảo trợ. |
| **HTTP / REST** | `Web Protocol` | HTTP overhead header quá lớn (hàng trăm bytes); MQTT header chỉ có 2 bytes, cực kỳ tiết kiệm pin và băng thông. |
| **CoAP** | `Constrained Protocol` | CoAP chạy trên UDP; MQTT chạy trên TCP đảm bảo độ tin cậy và có 3 mức QoS (0, 1, 2). |

### 🌟 Ưu điểm nổi bật (Pros)
- **Header gói tin siêu nhẹ (tối thiểu chỉ 2 bytes), tối ưu tuyệt đối cho mạng di động yếu.**
- **Hỗ trợ 3 mức QoS (Quality of Service: 0 - Nhiều nhất một lần, 1 - Ít nhất một lần, 2 - Đúng một lần duy nhất).**
- **Tính năng Last Will and Testament (LWT) và Retained Messages giúp phát hiện ngay khi thiết bị bị mất kết nối đột ngột.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Không phù hợp để truyền tải các payload dữ liệu kích thước lớn (video, file ảnh độ phân giải cao).
- Cần một MQTT Broker trung gian chuyên dụng (như Mosquitto, EMQX, HiveMQ).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho các giải pháp IoT, Smart City, ô tô thông minh, thiết bị nhúng chạy pin. KHÔNG NÊN DÙNG: Cho các ứng dụng web thông thường giao tiếp giữa trình duyệt và server.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/mqtt/controller/MqttController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ Cốt Lõi (Services / Handlers)
- `com/example/mqtt/service/MqttService.java`: Đảm nhiệm xử lý logic nghiệp vụ và tính toán chính.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/mqtt/dto/CommandRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mqtt/dto/SensorData.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8114
spring:
  threads:
    virtual:
      enabled: true
mqtt:
  broker-url: tcp://localhost:1883
  client-id: spring-boot-iot-client
  auto-reconnect: true
  clean-start: true
  connection-timeout: 10
  keep-alive-interval: 60
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 14-EclipsePaho

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8114`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8114/api/mqtt/publish` | 1. Xuất bản lệnh điều khiển IoT (QoS 1, Retained, User Properties) |
| `POST` | `http://localhost:8114/api/mqtt/publish` | 2. Xuất bản tin nhắn giữ lại (Retained Message - QoS 2) |
| `GET` | `http://localhost:8114/api/mqtt/sensors` | 3. Lấy dữ liệu cảm biến mới nhất của tất cả thiết bị |
| `GET` | `http://localhost:8114/api/mqtt/sensors/device-01` | 4. Lấy dữ liệu cảm biến theo ID thiết bị |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

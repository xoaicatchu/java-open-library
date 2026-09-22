# 14-EclipsePaho - Eclipse Paho MQTT 5

> **Cổng dịch vụ (Server Port)**: `8114`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Thiết bị nhúng, đồng hồ thông minh, cảm biến nhiệt độ băng thông mạng rất yếu, sóng chập chờn, không thể chạy giao thức HTTP nặng nề.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
IoT, smart-home, giám sát nhà máy công nghiệp, trạm quan trắc môi trường. Quản lý QoS (Quality of Service) 0, 1, 2 và tin nhắn lưu giữ (Retained messages).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/mqtt/controller/MqttController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/mqtt/service/MqttService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/mqtt/dto/CommandRequest.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).
- `com/example/mqtt/dto/SensorData.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

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

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 14-EclipsePaho

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8114`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8114/api/mqtt/publish` | 1. Xuất bản lệnh điều khiển IoT (QoS 1, Retained, User Properties) |
| `POST` | `http://localhost:8114/api/mqtt/publish` | 2. Xuất bản tin nhắn giữ lại (Retained Message - QoS 2) |
| `GET` | `http://localhost:8114/api/mqtt/sensors` | 3. Lấy dữ liệu cảm biến mới nhất của tất cả thiết bị |
| `GET` | `http://localhost:8114/api/mqtt/sensors/device-01` | 4. Lấy dữ liệu cảm biến theo ID thiết bị |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

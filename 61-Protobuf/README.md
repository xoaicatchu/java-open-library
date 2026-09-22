# 61-Protobuf - Google Protocol Buffers

<p align="left">
  <img src="https://img.shields.io/badge/Port-8161-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Binary%20Serialization-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Khi truyền tải hàng triệu thông điệp qua mạng mỗi giây giữa các vi dịch vụ hoặc lưu trữ dữ liệu nén, định dạng JSON dạng văn bản (text-based) chiếm dụng quá nhiều băng thông đường truyền và tiêu tốn lượng lớn chu kỳ CPU của máy chủ chỉ để parse các ký tự chuỗi `"key": "value"`.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Tuần hoàn dữ liệu nhị phân (Binary Serialization) siêu nhỏ gọn, giảm dung lượng payload từ 3 đến 10 lần so với JSON.**
- **Làm ngôn ngữ định nghĩa giao diện (IDL) cho giao thức gRPC và lưu trữ dữ liệu nén trong Apache Kafka.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Google Protocol Buffers |
|:---|:---|:---|
| **JSON** | `Text-based Serialization` | JSON dễ đọc bằng mắt người nhưng chậm và cồng kềnh; Protobuf nhị phân tối ưu tuyệt đối cho máy móc xử lý. |
| **Apache Avro** | `Schema-based Binary` | Avro rất mạnh cho hệ sinh thái Hadoop/Big Data; Protobuf linh hoạt hơn cho giao tiếp mạng RPC và microservices. |
| **MessagePack / FlatBuffers** | `Zero-Copy Binary` | MessagePack không cần schema; Protobuf có file `.proto` kiểm soát hợp đồng chặt chẽ và có khả năng tương thích ngược (Backward Compatibility) tuyệt đối. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Dung lượng gói tin siêu nhỏ gọn và tốc độ parse nhanh gấp hàng chục lần so với JSON.**
- **Khả năng tương thích ngược (Backward Compatibility) và tương thích xuôi (Forward Compatibility) hoàn hảo qua các số thứ tự trường (Field Tags).**
- **Độc lập ngôn ngữ: một file hợp đồng `.proto` có thể tự động sinh mã nguồn cho Java, Go, Python, C++, C#, JavaScript.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Dữ liệu dạng nhị phân không thể đọc được trực tiếp bằng mắt người, gây khó khăn hơn trong việc debug nếu không có công cụ chuyên dụng.
- Cần bước sinh mã (Protoc compiler) trong quá trình build project.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Cho giao tiếp vi dịch vụ qua mạng tần suất cao, game server, streaming IoT, Kafka messages tốc độ cao. KHÔNG NÊN DÙNG: Cho các REST API công khai hướng tới trình duyệt web thông thường.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/protobuf/controller/ProductController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/protobuf/config/ProtobufConfig.java`: Thiết lập thông số và khởi tạo Spring Beans cho thư viện.

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8161
spring:
  application:
    name: protobuf-demo
  threads:
    virtual:
      enabled: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 61-Protobuf

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8161`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8161/api/products/p1` | Get default product as JSON |
| `GET` | `http://localhost:8161/api/products/p1` | Get default product as Protobuf |
| `POST` | `http://localhost:8161/api/products` | Create new product as JSON |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

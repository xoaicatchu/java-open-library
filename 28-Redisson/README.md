# 28-Redisson - Redisson

> **Cổng dịch vụ (Server Port)**: `8128`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Hệ thống chạy 10 cụm server (cluster), 2 user cùng bấm mua chiếc vé máy bay cuối cùng tại 1 thời điểm. Local lock `synchronized` của Java không có tác dụng trên môi trường phân tán.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
**Distributed Lock (Khóa phân tán)** với Redis, cấu trúc dữ liệu phân tán (RMap, RQueue, RAtomicLong, Bloom Filter chống thọc thủng cache).

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/redisson/controller/CartController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

### ⚙️ Tầng Nghiệp Vụ & Xử Lý (Services / Handlers)
- `com/example/redisson/service/CartService.java`: Đảm nhiệm logic tính toán, xử lý nghiệp vụ cốt lõi.

### 🔧 Cấu Hình & Tích Hợp (Configurations)
- `com/example/redisson/config/RedissonConfig.java`: Khởi tạo Bean và thiết lập thông số cho thư viện/framework.

### 📦 Mô Hình Dữ Liệu & Sự Kiện (DTOs / Models / Entities / Events)
- `com/example/redisson/dto/CartItem.java`: Đối tượng truyền tải dữ liệu (Java Record bất biến / Domain Model).

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8128
spring:
  application:
    name: redisson-demo
  threads:
    virtual:
      enabled: true
  cache:
    type: redis
redisson:
  address: "redis://127.0.0.1:6379"
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 28-Redisson

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8128`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `POST` | `http://localhost:8128/api/cart/user123/items` | Add an item to user cart |
| `POST` | `http://localhost:8128/api/cart/user123/items` | Add another item to user cart |
| `GET` | `http://localhost:8128/api/cart/user123` | Get user cart |
| `DELETE` | `http://localhost:8128/api/cart/user123/items/prod-1` | Delete an item from cart |
| `POST` | `http://localhost:8128/api/cart/lock-demo?productId=prod-1&amount=5` | Test lock demo |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

# 28-Redisson - Redisson

<p align="left">
  <img src="https://img.shields.io/badge/Port-8128-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Distributed%20Data%20&%20Locking-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Trong hệ thống chạy cụm 10 server (cluster), 2 khách hàng cùng bấm mua chiếc vé máy bay cuối cùng tại cùng 1 tích tắc. Từ khóa `synchronized` hoặc `ReentrantLock` của Java thuần chỉ có tác dụng trong 1 JVM riêng lẻ, hoàn toàn bất lực trên môi trường phân tán, dẫn tới hiện tượng bán vượt số lượng (overselling/race condition).

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Khóa phân tán (Distributed Lock - `RLock`) bảo vệ các tài nguyên trọng yếu: trừ tiền ví, đặt vé, giảm tồn kho Flash Sale.**
- **Sử dụng các cấu trúc dữ liệu phân tán chuẩn Java: RMap (tự động đồng bộ), RQueue, RAtomicLong, Bloom Filter chống thọc thủng cache (Cache Penetration).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Redisson |
|:---|:---|:---|
| **Jedis / Lettuce** | `Low-level Redis Client` | Jedis/Lettuce cung cấp lệnh Redis thô; Redisson nâng tầm Redis thành các cấu trúc dữ liệu và giải thuật phân tán chuẩn Java. |
| **Apache Curator (Zookeeper)** | `Zookeeper Lock` | Zookeeper lock rất an toàn nhưng hạ tầng Zookeeper nặng nề; Redisson tận dụng ngay cụm Redis có sẵn. |
| **Database Pessimistic Lock (SELECT FOR UPDATE)** | `DB Lock` | Khóa DB gây nghẽn kết nối và giảm thông lượng DB nghiêm trọng; Redisson Lock thực hiện trên RAM Redis với tốc độ microsecond. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Triển khai thuật toán khóa phân tán chuẩn Redlock với cơ chế tự động gia hạn khóa (Watchdog mechanism) chống treo khóa khi tiến trình bị đơ.**
- **Cung cấp các cấu trúc phân tán chuẩn Java Collections: `RMap`, `RSet`, `RBlockingQueue`, `RCountDownLatch`.**
- **Tích hợp sẵn Bloom Filter phân tán giúp chặn đứng các request tìm kiếm ID không tồn tại làm sập Database.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Một số tính năng doanh nghiệp cao cấp (như Local Cache RLocalCachedMap phân tán) yêu cầu bản quyền thương mại Redisson PRO.
- Cần cấu hình đúng tham số leaseTime và network timeout để tránh nhả khóa sớm khi có sự cố mạng chập chờn.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Bắt buộc phải có cho các bài toán đồng thời phân tán: Flash Sale, giỏ hàng TMĐT, chống click đúp thanh toán trong microservices. KHÔNG NÊN DÙNG: Khi hệ thống chỉ chạy duy nhất 1 node đơn lẻ hoặc chỉ cần thao tác get/set Redis đơn giản.**

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

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 28-Redisson

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8128`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8128/api/cart/user123/items` | Add an item to user cart |
| `POST` | `http://localhost:8128/api/cart/user123/items` | Add another item to user cart |
| `GET` | `http://localhost:8128/api/cart/user123` | Get user cart |
| `DELETE` | `http://localhost:8128/api/cart/user123/items/prod-1` | Delete an item from cart |
| `POST` | `http://localhost:8128/api/cart/lock-demo?productId=prod-1&amount=5` | Test lock demo |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

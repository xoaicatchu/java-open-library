# 26-Caffeine - Caffeine Cache

<p align="left">
  <img src="https://img.shields.io/badge/Port-8126-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-High-Performance%20In-Memory%20Cache-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Các dữ liệu đọc thường xuyên (danh mục sản phẩm, cấu hình hệ thống, tỷ giá, thông tin user) được gọi hàng triệu lần mỗi phút. Nếu mỗi lần đều gọi sang Redis qua mạng hoặc query vào Database, độ trễ mạng (Network Latency) và tải I/O sẽ khiến hệ thống bị chậm chạp và tốn chi phí hạ tầng.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Bộ nhớ đệm cục bộ (Local In-Memory L1 Cache) siêu tốc ngay trong RAM của JVM với độ trễ nano-giây.**
- **Hạn chế các cuộc tấn công quét dữ liệu lặp lại làm nghẽn kết nối mạng tới Redis/Database.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với Caffeine Cache |
|:---|:---|:---|
| **Guava Cache** | `Legacy Local Cache` | Caffeine là bản viết lại kế thừa Guava Cache với thuật toán W-TinyLFU tối ưu tỉ lệ trúng cache (hit rate) cao hơn và throughput nhanh hơn gấp 2-3 lần. |
| **Redis** | `Distributed Cache` | Redis chạy qua mạng mất từ 1-5ms; Caffeine chạy ngay trong RAM JVM mất dưới 1 microsecond. |
| **Ehcache 3** | `Java Cache` | Ehcache có hỗ trợ lưu trữ phân tầng ra đĩa cứng (off-heap/disk); Caffeine tối ưu tuyệt đối cho in-memory. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Thư viện Local Cache hiệu năng cao nhất thế giới hiện nay cho Java, sử dụng thuật toán eviction W-TinyLFU tối ưu hit rate đỉnh cao.**
- **Độ trễ truy xuất cấp độ Nano-giây (không tốn chi phí serialize qua mạng như Redis).**
- **Hỗ trợ đầy đủ tính năng: expireAfterWrite, expireAfterAccess, refreshAfterWrite, maximumSize, recordStats.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Chỉ tồn tại trong bộ nhớ của từng node JVM riêng lẻ: khi chạy nhiều cụm server, dữ liệu có thể bị lệch nhau giữa các node.
- Bị giới hạn bởi dung lượng RAM của ứng dụng Java (Heap Size).

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Dữ liệu đọc cực lớn, hiếm khi thay đổi (danh mục, mã bưu chính, config), hoặc làm tầng đệm L1 Cache phía trước Redis. KHÔNG NÊN DÙNG: Khi cần dữ liệu cache chia sẻ đồng nhất 100% giữa hàng chục server khác nhau (khi đó phải dùng Redis).**

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: caffeine-demo
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  threads:
    virtual:
      enabled: true
server:
  port: 8126
```

---

## 4. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 26-Caffeine

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8126`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `POST` | `http://localhost:8126/api/products` | 1. Tạo sản phẩm mới |
| `GET` | `http://localhost:8126/api/products/spring/1` | 2. Lấy sản phẩm qua Spring Cache Abstraction (@Cacheable) |
| `GET` | `http://localhost:8126/api/products/manual/1` | 3. Lấy sản phẩm qua Manual Cache (Cache<K,V>) |
| `GET` | `http://localhost:8126/api/products/loading/1` | 4. Lấy sản phẩm qua LoadingCache (tự động load khi miss) |
| `GET` | `http://localhost:8126/api/products/async/1` | 5. Lấy sản phẩm qua AsyncLoadingCache (CompletableFuture) |
| `DELETE` | `http://localhost:8126/api/products/manual/1` | 6. Xóa cache thủ công |
| `GET` | `http://localhost:8126/api/products/manual/stats` | 7. Xem thống kê Cache (Hit rate, Miss rate, Eviction count) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

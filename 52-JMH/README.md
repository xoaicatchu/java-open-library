# 52-JMH - JMH - Java Microbenchmark Harness

> **Cổng dịch vụ (Server Port)**: `8152`  
> **Nền tảng kỹ thuật**: Java 21 LTS | Spring Boot 3.4.1 | Maven Standalone | No Lombok

---

## 1. Giới Thiệu & Bài Toán Giải Quyết

### 📌 Vấn đề Thực Tế (Pain Point)
Tranh cãi xem dùng `StringBuilder` hay dấu `+`, dùng `ArrayList` hay `LinkedList` cái nào chạy nhanh hơn, nhưng dùng hàm `System.currentTimeMillis()` để đo thì sai số hoàn toàn do cơ chế JIT Compiler và Garbage Collector của JVM.

### 🎯 Ứng Dụng Sản Xuất (Production Use Cases)
Công cụ đo lường hiệu năng cấp độ nano-giây chuẩn do chính đội ngũ Oracle phát triển. Kiểm soát warm-up, dead-code elimination, phân bổ bộ nhớ.

---

## 2. Kiến Trúc & Cấu Trúc Mã Nguồn

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & API (Controllers / Endpoints)
- `com/example/jmh/controller/BenchmarkController.java`: Điều phối và tiếp nhận các yêu cầu HTTP/Messaging.

---

## 3. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: 52-JMH
  threads:
    virtual:
      enabled: true
```

---

## 4. Hướng Dẫn Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 52-JMH

# Chạy trực tiếp qua Maven
mvn spring-boot:run
```
Ứng dụng sẽ khởi động và lắng nghe tại: **`http://localhost:8152`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử qua file `requests.http`
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request trực tiếp bằng công cụ **REST Client** (trên VS Code) hoặc **HTTP Client** (trên IntelliJ IDEA):

| Phương thức | Endpoint URL | Mô tả kịch bản kiểm thử |
|:---|:---|:---|
| `GET` | `http://localhost:8152/api/benchmarks/info` | 1. Xem danh sách các bài Benchmark JMH có sẵn |
| `POST` | `http://localhost:8152/api/benchmarks/run-quick` | 2. Kích hoạt chạy thử nghiệm Benchmark nhanh (1 iteration - StringBenchmark) |

---

## 💡 Lưu Ý Thực Chiến
- **Java Record**: Toàn bộ DTOs và Events được triển khai bằng Java Record nguyên bản, đảm bảo tính bất biến (immutability) và tối ưu hóa bộ nhớ heap.
- **Tối ưu hiệu năng**: Không sử dụng Lombok hay reflection tùy tiện, đảm bảo thời gian khởi động (startup time) siêu nhanh và tương thích hoàn toàn với Java 21 Virtual Threads.

# 52-JMH - JMH (Java Microbenchmark Harness)

<p align="left">
  <img src="https://img.shields.io/badge/Port-8152-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Performance%20Microbenchmarking-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Các lập trình viên thường tranh cãi xem giữa 2 thuật toán hoặc 2 cách viết code cái nào nhanh hơn, rồi tự viết hàm `System.currentTimeMillis()` để đo. Kết quả đo này hoàn toàn sai lệch và vô giá trị do cơ chế tối ưu hóa của JVM JIT Compiler (Dead-code elimination, Loop unrolling) và chu kỳ dọn rác của Garbage Collector.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Đo lường thời gian thực thi ở cấp độ nano-giây chuẩn xác cho các thuật toán lõi, giải thuật mã hóa, parser dữ liệu.**
- **So sánh khách quan hiệu năng giữa các thư viện (ví dụ so sánh tốc độ serialize giữa Jackson vs Protobuf vs Gson).**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với JMH (Java Microbenchmark Harness) |
|:---|:---|:---|
| **System.currentTimeMillis()** | `Naive Measurement` | Sai số cực lớn do không tính thời gian Warm-up của JIT; JMH loại bỏ hoàn toàn các sai số này. |
| **VisualVM / JProfiler** | `Application Profilers` | Profiler dùng để tìm điểm nghẽn của cả ứng dụng lớn; JMH dùng để đo lường vi mô (Micro-level) từng hàm cụ thể. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Công cụ đo lường hiệu năng chính thức do chính đội ngũ phát triển máy ảo Java (Oracle / OpenJDK Team) xây dựng.**
- **Kiểm soát chính xác các giai đoạn Warm-up (làm nóng JVM), Threading, Allocation Profiling.**
- **Cung cấp giải pháp Blackhole ngăn chặn cơ chế Dead-code Elimination của JIT Compiler.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Đòi hỏi kiến thức sâu về máy ảo JVM để thiết kế bài test benchmark chuẩn mực, tránh đo sai mục đích.
- Một bài đo benchmark đầy đủ thường mất vài phút đến hàng chục phút để chạy đủ các vòng lặp.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Khi phát triển các thư viện dùng chung (Framework/SDK), giải thuật tối ưu hiệu năng cấp cao. KHÔNG NÊN DÙNG: Cho việc đo đạc hiệu năng ứng dụng web thông thường (khi đó nên dùng APM / Distributed Tracing).**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/jmh/controller/BenchmarkController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
spring:
  application:
    name: 52-JMH
  threads:
    virtual:
      enabled: true
```

---

## 5. Hướng Dẫn Khởi Chạy & Kiểm Thử

### 🚀 Khởi chạy ứng dụng
```bash
# Di chuyển vào thư mục dự án
cd 52-JMH

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8152`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8152/api/benchmarks/info` | 1. Xem danh sách các bài Benchmark JMH có sẵn |
| `POST` | `http://localhost:8152/api/benchmarks/run-quick` | 2. Kích hoạt chạy thử nghiệm Benchmark nhanh (1 iteration - StringBenchmark) |

---

## 📋 Tiêu Chuẩn Kỹ Thuật
- **Java 21 LTS & Virtual Threads**: Tối ưu throughput cho các tác vụ I/O-bound.
- **Java Records**: Sử dụng Records làm DTO và Domain Event, đảm bảo tính bất biến và an toàn đa luồng.
- **Maven Standalone**: Mỗi dự án chạy độc lập, không phụ thuộc module cha.

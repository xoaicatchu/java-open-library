# 47-AssertJ - AssertJ

<p align="left">
  <img src="https://img.shields.io/badge/Port-8147-007ACC?style=flat-square" alt="Port" />
  <img src="https://img.shields.io/badge/Category-Fluent%20Assertions-6DB33F?style=flat-square" alt="Category" />
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=flat-square" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen?style=flat-square" alt="Spring Boot 3.4.1" />
  <img src="https://img.shields.io/badge/Architecture-No%20Lombok-red?style=flat-square" alt="No Lombok" />
</p>

---

## 1. Bài Toán Thực Tế & Vấn Đề Giải Quyết (Pain Point)

### 📌 Thách thức trong thực tế
Câu lệnh kiểm tra truyền thống của JUnit `assertEquals(expected, actual)` rất dễ bị gõ ngược vị trí tham số, thông báo lỗi khi test fail cực kỳ tối nghĩa và không có các hàm kiểm tra chuyên sâu cho Collection, Map, Exception hay ngày tháng.

### 🎯 Usecase cụ thể trong sản xuất (Production Use Cases)
- **Viết các câu lệnh khẳng định kết quả kiểm thử (Assertions) theo phong cách Fluent API tự nhiên như một câu tiếng Anh: `assertThat(order.getTotal()).isGreaterThan(0)`.**
- **So sánh sâu đệ quy toàn bộ cấu trúc của 2 đối tượng phức tạp bằng `usingRecursiveComparison()` mà không cần viết hàm `equals()`.**
- **Gom toàn bộ các lỗi kiểm thử trong 1 lần chạy bằng `SoftAssertions` thay vì dừng lại ngay ở lỗi đầu tiên.**

---

## 2. So Sánh Đối Trọng & Đánh Đổi Kỹ Thuật (Trade-off Analysis)

### ⚖️ Bảng so sánh với các giải pháp tương đương
| Công nghệ | Phân loại | Điểm khác biệt & Đối chiếu với AssertJ |
|:---|:---|:---|
| **JUnit 5 Assertions** | `Basic Assertions` | JUnit 5 chỉ có các hàm cơ bản `assertEquals`, `assertTrue`; AssertJ cung cấp hàng ngàn hàm kiểm tra chuyên sâu cực mạnh. |
| **Hamcrest** | `Matcher Library` | Hamcrest cú pháp lồng nhau khó đọc (`assertThat(val, is(equalTo(exp)))`); AssertJ viết theo chuỗi chấm mượt mà và tự động gợi ý code trên IDE. |
| **Google Truth** | `Fluent Assertions` | Truth của Google rất tốt nhưng AssertJ phong phú tính năng hơn và là chuẩn mặc định của Spring Boot Test. |

### 🌟 Ưu điểm nổi bật (Pros)
- **Tự động gợi ý code (IDE Autocomplete) xuất sắc: chỉ cần gõ `assertThat(object).` là IDE hiển thị toàn bộ các hàm kiểm tra tương ứng với kiểu dữ liệu đó.**
- **Thông báo lỗi khi test thất bại cực kỳ chi tiết và dễ hiểu, chỉ rõ vị trí và lý do sai khác.**
- **Tích hợp sẵn mặc định trong `spring-boot-starter-test` mà không cần cài thêm thư viện ngoài.**

### ⚠️ Nhược điểm & Thách thức (Cons)
- Hầu như không có nhược điểm nào đáng kể đối với việc viết test.

### 🧭 Ma trận quyết định: Khi nào NÊN dùng & Khi nào KHÔNG NÊN dùng
- **NÊN DÙNG: Là tiêu chuẩn viết assertion khuyên dùng mặc định cho toàn bộ các bài test Java. KHÔNG NÊN DÙNG: Không có lý do loại bỏ.**

---

## 3. Kiến Trúc & Cấu Trúc Mã Nguồn Trong Dự Án

Dự án mẫu minh họa đầy đủ luồng nghiệp vụ thực chiến từ tiếp nhận request, xử lý nghiệp vụ đến kiểm thử tự động:

### 🎮 Tầng Tiếp Nhận & Điều Phối (Controllers / Endpoints)
- `com/example/assertj/controller/OrderController.java`: Tiếp nhận và điều phối các yêu cầu HTTP/Messaging.

---

## 4. Cấu Hình Tiêu Biểu (`application.yml`)

```yaml
server:
  port: 8147
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
cd 47-AssertJ

# Khởi chạy bằng Maven
mvn spring-boot:run
```
Ứng dụng sẽ lắng nghe tại cổng: **`http://localhost:8147`**.

### 🧪 Chạy kiểm thử tự động (Unit / Integration Tests)
```bash
mvn test
```

### 📡 Kiểm thử trực tiếp qua HTTP (`requests.http`)
Dự án có sẵn file **`requests.http`** ở thư mục gốc để gửi request kiểm thử trực tiếp bằng tiện ích **REST Client** (VS Code) hoặc **HTTP Client** (IntelliJ IDEA):

| Phương thức | Endpoint URL | Kịch bản kiểm thử nghiệp vụ |
|:---|:---|:---|
| `GET` | `http://localhost:8147/api/orders/sample` | Get Sample Order |
| `POST` | `http://localhost:8147/api/orders/validate` | Validate Valid Order |
| `POST` | `http://localhost:8147/api/orders/validate` | Validate Invalid Order |

---

## 💡 Tiêu Chuẩn Kỹ Thuật Dự Án
- **Java 21 LTS & Virtual Threads**: Tối ưu hóa throughput cho các tác vụ I/O bound.
- **Java Record Immutability**: 100% DTOs và Events sử dụng Java Records nguyên bản để đảm bảo tính bất biến và an toàn đa luồng.
- **Zero Lombok**: Mã nguồn minh bạch, không phụ thuộc annotation processing ngầm, khởi động nhanh và tương thích hoàn toàn với GraalVM Native Image.

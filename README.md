# Java Enterprise Production Playbook (61 Thư Viện Thực Chiến)

> **Mục tiêu**: Xóa bỏ tình trạng "học thư viện lan man, học vẹt cú pháp". Tài liệu này giải thích **bài toán thực tế (Pain Point)**, **ứng dụng sản xuất (Production Use Case)** và **lý do tại sao hệ thống bắt buộc phải dùng** cho toàn bộ 61 thư viện/công nghệ phổ biến nhất trong hệ sinh thái Java 21 & Spring Boot 3.x.

---

## 🗺️ Bản Đồ Kiến Trúc Thực Tế (System Blueprint)

Một hệ thống doanh nghiệp (Enterprise / Microservices) không sử dụng thư viện ngẫu nhiên mà được phân lớp giải quyết đúng bài toán:

```
[Client / Mobile / Third-party]
        │
        ▼
[06-SpringGateway] ── [30-Sentinel / 29-Resilience4j] (Rate limit & Bảo vệ sập nguồn)
        │
        ├── [36-SpringSecurity / 37-AuthServer / 38-Keycloak] (Xác thực JWT / RBAC)
        │
        ▼
[REST Layer: 01-MVC / 02-WebFlux / 04-HATEOAS / 05-ProblemDetail / 15-gRPC]
        │
        ├── [31-MapStruct] (Chuyển đổi DTO <-> Entity không tốn CPU)
        ├── [32-BeanValidation] (Chặn đứng dữ liệu bẩn ngay ở cửa vào)
        ├── [33-SpringAOP] (Ghi audit log & đo thời gian xử lý trong suốt)
        │
        ▼
[Business & Workflow: 03-Modulith / 55-Camunda / 56-Statemachine]
        │
        ├── [26-Caffeine / 27-SpringCache / 28-Redisson] (Giảm 80-90% tải Database)
        ├── [29-Resilience4j / 34-OpenFeign / 35-RestClient] (Gọi API ngoài an toàn, chống timeout)
        │
        ├── [07-Axon / 08-SpringEvents / 09-Integration / 10-Kafka / 11-RabbitMQ / 12-CloudStream]
        │     └──> Xử lý bất đồng bộ, thanh toán, thông báo, tích hợp liên hệ thống
        │
        ▼
[Data Layer: 17-JPA / 18-MyBatis / 19-jOOQ / 20-JDBI / 21-R2DBC / 22-HibernateBatch / 23-MongoDB]
        │
        └── [24-Flyway / 25-Liquibase] (Tự động nâng cấp schema DB an toàn qua CI/CD)
```

---

## 📋 Danh Sách & Ứng Dụng Thực Tiễn Chi Tiết (01 – 61)

### I. Web API & Giao Tiếp (01 – 06)

#### 01. Spring Web MVC (`01-SpringWebMVC`) — Port: 8101
- **Vấn đề thực tế**: Cần xây dựng API CRUD tiêu chuẩn, đồng bộ, dễ bảo trì, tuyển dụng dễ.
- **Ứng dụng thực tế**: 90% ứng dụng quản trị, ERP, CRM, Banking Core nội bộ. Kết hợp **Java 21 Virtual Threads** (`spring.threads.virtual.enabled: true`) giúp phục vụ hàng chục ngàn kết nối đồng thời mà không lo cạn kiệt Thread Pool.

#### 02. Spring WebFlux (`02-SpringWebFlux`) — Port: 8102
- **Vấn đề thực tế**: Cần giữ hàng trăm ngàn kết nối mở liên tục (streaming, push event) mà tốn cực ít RAM/Thread.
- **Ứng dụng thực tế**: Hệ thống bảng giá chứng khoán thời gian thực (SSE), cổng Push Notification, Streaming Log/Telemetry, hoặc các API Gateway trung chuyển.

#### 03. Spring Modulith (`03-SpringModulith`) — Port: 8103
- **Vấn đề thực tế**: Dự án Monolith sau vài năm biến thành "Big Ball of Mud", các package gọi chéo lung tung, sửa chỗ này hỏng chỗ khác, không thể tách ra microservices.
- **Ứng dụng thực tế**: Enforce kiến trúc Modular Monolith. Tự động kiểm tra vi phạm ranh giới giữa các module (Domain Boundaries) và tạo tài liệu kiến trúc tự động bằng code test.

#### 04. Spring HATEOAS (`04-SpringHATEOAS`) — Port: 8104
- **Vấn đề thực tế**: Client (Mobile/Web) phải hardcode URL từng endpoint. Khi backend đổi URL hoặc state của object thay đổi (VD: Đơn hàng đã HỦY thì không được hiển thị nút THANH TOÁN), client dễ bị lỗi.
- **Ứng dụng thực tế**: Trả về kèm danh sách liên kết hành động khả dĩ (`links`). Ứng dụng trong Public API, ngân hàng mở (Open Banking API).

#### 05. Problem Detail RFC 9457 (`05-ProblemDetail`) — Port: 8105
- **Vấn đề thực tế**: Mỗi lập trình viên tự chế một kiểu trả lỗi JSON khác nhau (`{"err": "..."}`, `{"code": 500, "message": "..."}`), gây khó khăn cho đội Frontend và Mobile khi parse lỗi.
- **Ứng dụng thực tế**: Chuẩn hóa format lỗi quốc tế (RFC 9457) với đầy đủ `type`, `title`, `status`, `detail`, `instance`, hỗ trợ đa ngôn ngữ (i18n).

#### 06. Spring Cloud Gateway (`06-SpringGateway`) — Port: 8106
- **Vấn đề thực tế**: Hệ thống có hàng chục microservice, không thể để client gọi trực tiếp từng service (lộ IP, khó kiểm soát auth, cors, rate limit).
- **Ứng dụng thực tế**: Cửa ngõ duy nhất tiếp nhận request: định tuyến URL, lọc token JWT, chặn IP DDoS, rate limit, ghi log truy vết tập trung.

---

### II. Xử Lý Bất Đồng Bộ & Sự Kiện Phân Tán (07 – 16)

#### 07. Axon Framework (`07-AxonFramework`) — Port: 8107
- **Vấn đề thực tế**: Các giao dịch tài chính, ví điện tử cần lưu lại lịch sử biến động số dư tuyệt đối chính xác (không bao giờ được ghi đè `UPDATE account SET balance = ...`).
- **Ứng dụng thực tế**: Kiến trúc **Event Sourcing + CQRS**. Mọi thay đổi lưu dưới dạng chuỗi sự kiện bất biến (`MoneyDeposited`, `MoneyWithdrawn`). Quản lý Saga điều phối giao dịch phân tán giữa nhiều dịch vụ.

#### 08. Spring ApplicationEvent (`08-SpringEvents`) — Port: 8108
- **Vấn đề thực tế**: Sau khi đăng ký tài khoản thành công, muốn gửi email, tạo ví khuyến mãi, cộng điểm thưởng. Nếu viết chung trong 1 method service sẽ rất dài, vi phạm Single Responsibility.
- **Ứng dụng thực tế**: Tách rời nghiệp vụ nội bộ bằng Event trong cùng 1 JVM. Đặc biệt dùng `@TransactionalEventListener(phase = AFTER_COMMIT)` để đảm bảo chỉ gửi mail khi DB đã commit thành công.

#### 09. Spring Integration (`09-SpringIntegration`) — Port: 8109
- **Vấn đề thực tế**: Cần tích hợp với hệ thống cũ qua giao thức cổ điển: quét file CSV trên SFTP server định kỳ, đọc mail POP3/IMAP, hoặc lắng nghe socket TCP thô.
- **Ứng dụng thực tế**: Áp dụng mẫu EIP (Enterprise Integration Patterns) giải quyết bài toán ETL, kết nối với cổng thanh toán ngân hàng cũ, máy ATM, thiết bị ngoại vi.

#### 10. Spring for Apache Kafka (`10-SpringKafka`) — Port: 8110
- **Vấn đề thực tế**: Lưu lượng traffic khủng (hàng trăm ngàn request/giây: log click, định vị tài xế, biến động số dư) cần lưu trữ có thứ tự và không bị mất tin nhắn.
- **Ứng dụng thực tế**: Xương sống event-driven cho các sàn TMĐT (Shopee, Tiki), hệ thống tracking xe (Grab), Data Pipeline đưa dữ liệu về Data Lake/Hadoop. Có sẵn cơ chế Dead Letter Topic (DLT) xử lý tin lỗi.

#### 11. Spring AMQP / RabbitMQ (`11-SpringAMQP`) — Port: 8111
- **Vấn đề thực tế**: Cần định tuyến tin nhắn thông minh theo routing key (ví dụ: cảnh báo lỗi gửi sang hàng đợi ưu tiên, thông tin thường gửi hàng đợi thường) và cơ chế xác nhận xử lý (ACK/NACK).
- **Ứng dụng thực tế**: Hàng đợi gửi Email/SMS thông báo, xử lý background job, xuất file báo cáo nặng cho người dùng.

#### 12. Spring Cloud Stream (`12-SpringCloudStream`) — Port: 8112
- **Vấn đề thực tế**: Không muốn code của mình phụ thuộc chết vào Kafka hay RabbitMQ, muốn viết business logic dạng `Function<Input, Output>` thuần túy để tái sử dụng.
- **Ứng dụng thực tế**: Viết các worker xử lý luồng (Stream Processing) có thể đổi broker từ RabbitMQ sang Kafka hoặc Google Pub/Sub chỉ bằng cách thay đổi config `pom.xml` và `application.yml`.

#### 13. Apache Camel (`13-ApacheCamel`) — Port: 8113
- **Vấn đề thực tế**: Cần chuyển đổi dữ liệu phức tạp: Lấy file XML từ thư mục -> parse thành JSON -> chia nhỏ (Splitter) -> gọi 3 API khác nhau làm giàu dữ liệu -> tổng hợp lại (Aggregator) -> bắn vào DB.
- **Ứng dụng thực tế**: Tích hợp các hệ thống Core Banking, bảo hiểm, ERP (SAP/Salesforce). Camel có sẵn hơn 300 adapter kết nối mọi thứ.

#### 14. Eclipse Paho MQTT 5 (`14-EclipsePaho`) — Port: 8114
- **Vấn đề thực tế**: Thiết bị nhúng, đồng hồ thông minh, cảm biến nhiệt độ băng thông mạng rất yếu, sóng chập chờn, không thể chạy giao thức HTTP nặng nề.
- **Ứng dụng thực tế**: IoT, smart-home, giám sát nhà máy công nghiệp, trạm quan trắc môi trường. Quản lý QoS (Quality of Service) 0, 1, 2 và tin nhắn lưu giữ (Retained messages).

#### 15. gRPC-Java (`15-gRPC`) — Port: 8115
- **Vấn đề thực tế**: Giao tiếp nội bộ giữa hàng trăm Microservice bằng REST JSON quá chậm vì tốn thời gian parse text và dung lượng payload lớn.
- **Ứng dụng thực tế**: Giao tiếp microservice-to-microservice với độ trễ siêu thấp (Low Latency). Dùng Protobuf nhị phân, HTTP/2 multiplexing, hỗ trợ streaming 2 chiều (Bidirectional Streaming).

#### 16. Spring WebSocket + STOMP (`16-SpringWebSocket`) — Port: 8116
- **Vấn đề thực tế**: Client cần nhận dữ liệu tức thì từ máy chủ mà không phải polling liên tục làm quá tải server.
- **Ứng dụng thực tế**: Ứng dụng Chat, màn hình thông báo notification nhảy chuông tức thì, theo dõi trạng thái shipper trên bản đồ, bảng theo dõi kết quả đấu giá.

---

### III. Tương Tác Cơ Sở Dữ Liệu & Migration (17 – 25)

#### 17. Spring Data JPA / Hibernate (`17-SpringDataJPA`) — Port: 8117
- **Vấn đề thực tế**: Phát triển nhanh các tính năng CRUD chuẩn, quan hệ thực thể 1-n, n-n phức tạp, cần tính năng tự động theo dõi thay đổi (Dirty Checking) và phân trang.
- **Ứng dụng thực tế**: Đa số các tính năng nghiệp vụ ghi/đọc có quan hệ dữ liệu rõ ràng. Dự án mẫu minh họa cách dùng `@EntityGraph` để triệt tiêu lỗi kinh điển **N+1 Query**, JPA Auditing tự động lưu ngày tạo/người tạo.

#### 18. MyBatis (`18-MyBatis`) — Port: 8118
- **Vấn đề thực tế**: Câu query quá phức tạp, DBA yêu cầu kiểm soát chặt chẽ từng dòng SQL, Hibernate sinh SQL quá rối và khó tối ưu index.
- **Ứng dụng thực tế**: Các ứng dụng tài chính, báo cáo thuế, ngân hàng. Lập trình viên viết trực tiếp câu lệnh SQL linh hoạt bằng thẻ XML (`<if>`, `<foreach>`), tối ưu hiệu năng tối đa.

#### 19. jOOQ (`19-jOOQ`) — Port: 8119
- **Vấn đề thực tế**: Viết SQL thuần trong chuỗi String dễ gõ sai tên cột (runtime mới phát hiện), trong khi Hibernate không hỗ trợ tốt các cú pháp SQL hiện đại (Window Function, CTE, JSON aggregation).
- **Ứng dụng thực tế**: Hệ thống phân tích dữ liệu, Data Warehouse, báo cáo BI. jOOQ cho phép viết SQL bằng code Java Type-Safe, phát hiện lỗi cú pháp ngay lúc Compile.

#### 20. JDBI 3 (`20-JDBI`) — Port: 8120
- **Vấn đề thực tế**: Cần một thư viện thao tác SQL đơn giản hơn JPA (không nặng nề session, cache) nhưng tiện hơn JDBC thô rất nhiều.
- **Ứng dụng thực tế**: Các service nhỏ gọn, batch job, tool đồng bộ dữ liệu. Hỗ trợ Fluent API và SQL Object khai báo interface tiện lợi.

#### 21. Spring Data R2DBC (`21-SpringDataR2DBC`) — Port: 8121
- **Vấn đề thực tế**: Sử dụng WebFlux nhưng JDBC driver truyền thống lại chặn luồng (blocking I/O), làm mất tác dụng của Reactive.
- **Ứng dụng thực tế**: Đọc ghi CSDL quan hệ (PostgreSQL, MySQL) hoàn toàn bất đồng bộ (Non-blocking), phù hợp với hệ thống chịu tải kết nối cực lớn.

#### 22. Hibernate Batch & Bulk (`22-HibernateBatch`) — Port: 8122
- **Vấn đề thực tế**: Insert/Update 100.000 bản ghi bằng `saveAll()` thông thường của JPA sẽ làm tràn RAM (OutOfMemory) và chạy mất hàng giờ.
- **Ứng dụng thực tế**: Tối ưu batch insert với `StatelessSession` (bỏ qua Hibernate first-level cache và dirty checking) và cấu hình `batch_size`. Xử lý bulk update tại mức DB không load entity lên RAM.

#### 23. Spring Data MongoDB (`23-SpringDataMongoDB`) — Port: 8123
- **Vấn đề thực tế**: Dữ liệu có cấu trúc động, thuộc tính thay đổi liên tục theo từng sản phẩm (ví dụ: quần áo có size/màu, điện thoại có RAM/CPU/bộ nhớ), không phù hợp schema cứng của SQL.
- **Ứng dụng thực tế**: Catalog sản phẩm, lưu trữ giỏ hàng, lịch sử audit log, nội dung bài viết CMS, tài liệu bán hàng JSON.

#### 24. Flyway (`24-Flyway`) — Port: 8124
- **Vấn đề thực tế**: Triển khai lên Production nhưng DBA quên chạy script thêm cột mới dẫn tới sập hệ thống; không quản lý được lịch sử nâng cấp DB.
- **Ứng dụng thực tế**: Quản lý version database qua các file `V1__init.sql`, `V2__add_column.sql`. Hệ thống tự động kiểm tra và nâng cấp CSDL khi ứng dụng khởi động trong pipeline CI/CD.

#### 25. Liquibase (`25-Liquibase`) — Port: 8125
- **Vấn đề thực tế**: Dự án cần hỗ trợ triển khai trên nhiều loại CSDL khác nhau (cả Oracle, PostgreSQL, SQL Server) hoặc cần tính năng Rollback tự động khi lỗi.
- **Ứng dụng thực tế**: Định nghĩa thay đổi CSDL bằng YAML/XML độc lập với hệ quản trị CSDL, có hỗ trợ rollback script chặt chẽ.

---

### IV. Caching & Chống Sập Hệ Thống (26 – 30)

#### 26. Caffeine Cache (`26-Caffeine`) — Port: 8126
- **Vấn đề thực tế**: Dữ liệu danh mục, cấu hình hệ thống, tỉ giá đọc liên tục hàng triệu lần nhưng rất ít khi đổi. Đọc DB liên tục làm DB quá tải.
- **Ứng dụng thực tế**: Local In-Memory Cache nhanh nhất thế giới cho JVM (thuật toán W-TinyLFU tối ưu tỉ lệ hit cache). Tốc độ đọc Nano-giây ngay trong RAM máy chủ.

#### 27. Spring Cache Abstraction (`27-SpringCache`) — Port: 8127
- **Vấn đề thực tế**: Không muốn code nghiệp vụ dính chặt vào API của thư viện cache cụ thể, muốn tái sử dụng qua annotation đơn giản.
- **Ứng dụng thực tế**: Sử dụng `@Cacheable`, `@CachePut`, `@CacheEvict`. Cho phép kết hợp 2 tầng cache (Caffeine cho L1, Redis cho L2) chỉ bằng cấu hình.

#### 28. Redisson (`28-Redisson`) — Port: 8128
- **Vấn đề thực tế**: Hệ thống chạy 10 cụm server (cluster), 2 user cùng bấm mua chiếc vé máy bay cuối cùng tại 1 thời điểm. Local lock `synchronized` của Java không có tác dụng trên môi trường phân tán.
- **Ứng dụng thực tế**: **Distributed Lock (Khóa phân tán)** với Redis, cấu trúc dữ liệu phân tán (RMap, RQueue, RAtomicLong, Bloom Filter chống thọc thủng cache).

#### 29. Resilience4j (`29-Resilience4j`) — Port: 8129
- **Vấn đề thực tế**: Dịch vụ thanh toán bên thứ 3 (MoMo/VNPAY) bị treo, khiến các thread của hệ thống bị giữ chặt, kéo sập toàn bộ hệ sinh thái (Cascading Failure).
- **Ứng dụng thực tế**: **Circuit Breaker (Cầu dao tự ngắt)** khi đối tác lỗi, **Retry** tự động thử lại khi mạng chập chờn, **Rate Limiter** giới hạn tần suất gọi, **Bulkhead** cô lập tài nguyên.

#### 30. Alibaba Sentinel (`30-Sentinel`) — Port: 8130
- **Vấn đề thực tế**: Đợt Flash Sale / Black Friday, lượng truy cập tăng đột biến gấp 100 lần, cần bảo vệ hệ thống không bị "chết đứng".
- **Ứng dụng thực tế**: Điều tiết luồng lưu lượng (Traffic Shaping), xếp hàng request thông minh, bảo vệ thích ứng theo tải CPU máy chủ (System Adaptive Protection).

---

### V. Mapping, Validation & AOP (31 – 35)

#### 31. MapStruct (`31-MapStruct`) — Port: 8131
- **Vấn đề thực tế**: Viết code `getX()` rồi `setY()` giữa Entity và DTO tốn hàng ngàn dòng vô nghĩa; còn dùng `BeanUtils.copyProperties` hay `ModelMapper` thì dùng Reflection quá chậm và dễ lỗi runtime.
- **Ứng dụng thực tế**: Sinh code mapping thuần túy ở bước Compile-time. Tốc độ tương đương code tay thủ công, kiểm tra kiểu dữ liệu an toàn 100%.

#### 32. Jakarta Bean Validation (`32-BeanValidation`) — Port: 8132
- **Vấn đề thực tế**: Code validate `if (param == null || param.isEmpty())` làm rác code service.
- **Ứng dụng thực tế**: Khai báo bằng annotation: `@NotBlank`, `@Size`, `@Email`, custom validator tự chế (`@PhoneNumber`, `@StrongPassword`), validate chéo 2 trường (`@PasswordMatch`).

#### 33. Spring AOP (`33-SpringAOP`) — Port: 8133
- **Vấn đề thực tế**: Cần ghi lịch sử (Audit Log: ai vừa xóa đơn hàng, lúc mấy giờ) hoặc đo thời gian chạy của hàng trăm hàm mà không muốn chèn code bẩn vào từng hàm.
- **Ứng dụng thực tế**: Lập trình hướng khía cạnh (AOP). Tự định nghĩa annotation `@Auditable`, `@MeasurePerformance` để can thiệp trong suốt trước/sau khi gọi hàm.

#### 34. Spring Cloud OpenFeign (`34-SpringCloudOpenFeign`) — Port: 8134
- **Vấn đề thực tế**: Gọi REST API của microservice khác bằng code thủ công rất dài dòng, cấu hình header, auth lặp đi lặp lại.
- **Ứng dụng thực tế**: Khai báo Client dưới dạng Java Interface có gắn `@GetMapping`, `@PostMapping`. Tự động tích hợp Fallback và Retry.

#### 35. Spring 6.1 RestClient (`35-SpringRestClient`) — Port: 8135
- **Vấn đề thực tế**: `RestTemplate` đã cũ (maintenance mode), còn `WebClient` thì bắt buộc phải kéo cả dependency Reactive WebFlux nặng nề.
- **Ứng dụng thực tế**: HTTP Client đồng bộ hiện đại nhất của Spring 6. Cú pháp Fluent API trực quan, hỗ trợ `@HttpExchange` khai báo HTTP Client bằng interface đơn giản không cần Feign.

---

### VI. Bảo Mật & Xác Thực Phân Tán (36 – 38)

#### 36. Spring Security 6 (`36-SpringSecurity`) — Port: 8136
- **Vấn đề thực tế**: Cần bảo mật toàn bộ API, hỗ trợ phân quyền vai trò (RBAC), mã hóa mật khẩu một chiều BCrypt an toàn.
- **Ứng dụng thực tế**: Kiến trúc chuẩn `SecurityFilterChain` không session (Stateless API), xác thực JWT Bearer token, phân quyền chi tiết tới từng method bằng `@PreAuthorize("hasRole('ADMIN')")`.

#### 37. Spring Authorization Server (`37-SpringAuthServer`) — Port: 8137
- **Vấn đề thực tế**: Cần tự làm một máy chủ cấp quyền OAuth2/OpenID Connect (như Google/Facebook Login) để các đối tác hoặc ứng dụng nội bộ đăng nhập.
- **Ứng dụng thực tế**: Máy chủ Identity độc lập. Hỗ trợ chuẩn OAuth2 Authorization Code Flow with PKCE, Client Credentials Flow, tự phát hành cặp khóa RSA ký số Token JWKS (`/oauth2/token`, `/.well-known/openid-configuration`).

#### 38. Keycloak Integration (`38-KeycloakIntegration`) — Port: 8138
- **Vấn đề thực tế**: Doanh nghiệp không muốn tự code logic quản lý User/Role/OTP/Forgot Password mà muốn dùng một nền tảng Identity Provider chuyên nghiệp (SSO).
- **Ứng dụng thực tế**: Cấu hình Spring Boot làm **OAuth2 Resource Server** tin cậy Keycloak: tự động xác minh chữ ký JWT, map cấu trúc role của Keycloak (`realm_access.roles`) thành Authority của Spring.

---

### VII. Giám Sát, Đo Đạc & Nhật Ký (39 – 42)

#### 39. Logback + Logstash Encoder (`39-Logback`) — Port: 8139
- **Vấn đề thực tế**: Hệ thống có hàng triệu request, file log text thông thường không thể tìm kiếm, không biết lỗi này thuộc request của user nào.
- **Ứng dụng thực tế**: Xuất log định dạng JSON có cấu trúc (Structured Logging) để đẩy trực tiếp vào ELK Stack (Elasticsearch, Logstash, Kibana) hoặc Loki. Sử dụng **MDC (Mapped Diagnostic Context)** để gán `traceId`, `userId` xuyên suốt toàn bộ vòng đời request.

#### 40. Micrometer + Prometheus (`40-Micrometer`) — Port: 8140
- **Vấn đề thực tế**: Cần biết hệ thống hiện tại đang xử lý bao nhiêu request/giây (QPS), tỉ lệ lỗi 5xx là bao nhiêu %, CPU/RAM tiêu hao thế nào để vẽ biểu đồ Dashboard.
- **Ứng dụng thực tế**: Thư viện đo đạc chuẩn (Metric Facade). Tạo custom Counter, Timer, Gauge và phơi endpoint `/actuator/prometheus` cho máy chủ Grafana/Prometheus cào dữ liệu định kỳ.

#### 41. OpenTelemetry (`41-OpenTelemetry`) — Port: 8141
- **Vấn đề thực tế**: Một request của user đi qua 5 microservice khác nhau bị chậm. Không ai biết nguyên nhân chậm nằm ở service nào hay câu query SQL nào.
- **Ứng dụng thực tế**: **Distributed Tracing (Truy vết phân tán)**. Tự động truyền ngữ cảnh (W3C Trace Context) qua HTTP header, đo thời gian từng Span và hiển thị trực quan sơ đồ luồng đi trên Jaeger / Zipkin.

#### 42. Spring Boot Actuator (`42-SpringActuator`) — Port: 8142
- **Vấn đề thực tế**: Cần cơ chế để cụm Kubernetes biết container nào đang sống (Liveness Probe) và container nào đã sẵn sàng nhận traffic (Readiness Probe).
- **Ứng dụng thực tế**: Các endpoint quản trị production `/actuator/health`, `/actuator/info`. Tự viết `CustomHealthIndicator` kiểm tra kết nối DB, thanh toán trước khi nhận request.

---

### VIII. Tác Vụ Ngầm & Lập Lịch Phân Tán (43 – 45)

#### 43. Quartz Scheduler (`43-Quartz`) — Port: 8143
- **Vấn đề thực tế**: Cần lập lịch phức tạp (ví dụ: ngày làm việc cuối cùng của tháng, chạy định kỳ kèm xử lý bù nếu bị mất điện/misfire). Cần lưu trạng thái lịch vào DB.
- **Ứng dụng thực tế**: Lập lịch cấp doanh nghiệp. Quản trị lịch trình chạy báo cáo tài chính, tính lãi suất ngân hàng ban đêm, lưu trữ trạng thái Job vào bảng CSDL quan hệ (JDBC JobStore).

#### 44. JobRunr (`44-JobRunr`) — Port: 8144
- **Vấn đề thực tế**: Quartz quá nặng nề và cấu hình phức tạp; cần chạy background job nhẹ nhàng bằng Lambda Expression và có giao diện Dashboard web trực quan để bấm chạy lại (Retry) khi lỗi.
- **Ứng dụng thực tế**: Gửi email thông báo, export file ngầm, xử lý ảnh đại diện sau khi upload. Có sẵn Dashboard web xem tiến độ job tại `http://localhost:8000`.

#### 45. ShedLock (`45-ShedLock`) — Port: 8145
- **Vấn đề thực tế**: Ứng dụng chạy 5 Pod/Node trên Kubernetes, dùng `@Scheduled` mặc định thì cứ đến giờ cả 5 Pod cùng quét DB gửi email 1 lúc -> Khách hàng nhận 5 email trùng nhau.
- **Ứng dụng thực tế**: Tạo khóa phân tán (Distributed Lock) trong Database. Đảm bảo dù hệ thống có bao nhiêu Pod thì tại một thời điểm chỉ có **đúng 1 Pod được quyền thực thi job**.

---

### IX. Kiểm Thử Phần Mềm Tiêu Chuẩn (46 – 52)

#### 46. JUnit 5 + Mockito (`46-JUnit5-Mockito`) — Port: 8146
- **Vấn đề thực tế**: Viết Unit Test cho Service nhưng Service lại gọi Database và Cổng thanh toán bên ngoài, làm test chậm và phụ thuộc môi trường.
- **Ứng dụng thực tế**: Tạo mock giả lập các dependency (`@Mock`, `@InjectMocks`). Sử dụng `ArgumentCaptor`, test tham số hóa `@ParameterizedTest` để kiểm tra hàng trăm bộ dữ liệu đầu vào.

#### 47. AssertJ (`47-AssertJ`) — Port: 8147
- **Vấn đề thực tế**: Câu lệnh `assertEquals(expected, actual)` của JUnit truyền thống báo lỗi rất tối nghĩa và viết dài.
- **Ứng dụng thực tế**: Thư viện viết assertion dễ đọc nhất thế giới: `assertThat(order.getTotal()).isGreaterThan(0)`. Cho phép so sánh sâu toàn bộ cấu trúc object phức tạp (`usingRecursiveComparison`) và gom lỗi (`SoftAssertions`).

#### 48. Testcontainers (`48-Testcontainers`) — Port: 8148
- **Vấn đề thực tế**: Viết Integration Test dùng CSDL in-memory H2 chạy ngon nhưng khi deploy lên Production gặp CSDL PostgreSQL thật thì phát sinh lỗi do khác biệt cú pháp SQL.
- **Ứng dụng thực tế**: Tự động kéo Docker Container thật (PostgreSQL, Kafka, Redis) lên chạy test rồi tự hủy đi sau khi test xong. Đảm bảo môi trường test giống 100% môi trường Production thật.

#### 49. Datafaker (`49-Datafaker`) — Port: 8149
- **Vấn đề thực tế**: Cần tạo hàng vạn bản ghi dữ liệu mẫu có nghĩa (tên người Việt Nam, số điện thoại, địa chỉ, biển số xe) để test tải và demo cho khách hàng.
- **Ứng dụng thực tế**: Sinh dữ liệu giả lập có cấu trúc thực tế, hỗ trợ locale tiếng Việt (`vi_VN`), hỗ trợ seed cố định để tái lập kết quả test.

#### 50. Cucumber JVM (`50-Cucumber`) — Port: 8150
- **Vấn đề thực tế**: Khách hàng hoặc Business Analyst (BA) muốn đọc hiểu được kịch bản kiểm thử phần mềm mà không biết đọc code Java.
- **Ứng dụng thực tế**: Kiểm thử hướng hành vi (BDD - Behavior-Driven Development). Viết kịch bản test bằng ngôn ngữ tự nhiên Gherkin (`Given ... When ... Then ...`), code Java tự map vào từng bước để chạy tự động.

#### 51. ArchUnit (`51-ArchUnit`) — Port: 8151
- **Vấn đề thực tế**: Lập trình viên mới vào dự án vô tình viết code Controller gọi thẳng xuống Repository (bỏ qua Service), hoặc Entity lại phụ thuộc vào Spring Web.
- **Ứng dụng thực tế**: **Unit Test cho kiến trúc hệ thống**. Viết test bằng Java để tự động cấm vi phạm: "Controller không được gọi Repository", "Package Entity không được chứa annotation Web", "Phải đặt tên đuôi là Service".

#### 52. JMH - Java Microbenchmark Harness (`52-JMH`) — Port: 8152
- **Vấn đề thực tế**: Tranh cãi xem dùng `StringBuilder` hay dấu `+`, dùng `ArrayList` hay `LinkedList` cái nào chạy nhanh hơn, nhưng dùng hàm `System.currentTimeMillis()` để đo thì sai số hoàn toàn do cơ chế JIT Compiler và Garbage Collector của JVM.
- **Ứng dụng thực tế**: Công cụ đo lường hiệu năng cấp độ nano-giây chuẩn do chính đội ngũ Oracle phát triển. Kiểm soát warm-up, dead-code elimination, phân bổ bộ nhớ.

---

### X. Tài Liệu Hóa & Luồng Nghiệp Vụ (53 – 56)

#### 53. SpringDoc OpenAPI (`53-SpringDocOpenAPI`) — Port: 8153
- **Vấn đề thực tế**: Đội Backend sửa API nhưng quên cập nhật file tài liệu gửi cho đội Mobile/Frontend khiến kết nối bị lệch.
- **Ứng dụng thực tế**: Tự động phân tích code Java để sinh giao diện tài liệu Swagger UI trực quan tại `/swagger-ui.html`. Cho phép test gọi API trực tiếp trên trình duyệt kèm xác thực Bearer Token.

#### 54. OpenAPI Generator (`54-OpenAPIGenerator`) — Port: 8154
- **Vấn đề thực tế**: Mỗi khi Backend đổi model, đội Frontend (React/TypeScript) và Mobile (Flutter/Kotlin) phải tự tay ngồi gõ lại code Model và API Client, rất dễ sai sót.
- **Ứng dụng thực tế**: Hướng tiếp cận **API-First**. Viết file đặc tả `api-spec.yaml` trước, sau đó plugin tự động sinh ra toàn bộ code Server Interface (Java) và Client SDK (TypeScript/Dart/Swift).

#### 55. Camunda Platform 7 (`55-Camunda`) — Port: 8155
- **Vấn đề thực tế**: Quy trình phê duyệt vay vốn ngân hàng hoặc duyệt đơn bảo hiểm rất phức tạp (qua nhiều phòng ban, chờ lãnh đạo duyệt, nếu quá 3 ngày tự động hủy). Nếu dùng code `if/else` sẽ thành thảm họa.
- **Ứng dụng thực tế**: Quản trị quy trình doanh nghiệp (BPMN 2.0). BA vẽ quy trình trực quan bằng sơ đồ kéo thả, nhúng engine vào Spring Boot để tự động hóa: điều phối User Task, Service Task, Gateway điều kiện.

#### 56. Spring Statemachine (`56-SpringStatemachine`) — Port: 8156
- **Vấn đề thực tế**: Trạng thái đơn hàng TMĐT (`CREATED -> PAID -> SHIPPING -> DELIVERED`) bị client gọi nhảy cóc (chưa thanh toán đã bấm giao hàng thành công).
- **Ứng dụng thực tế**: Mô hình máy trạng thái hữu hạn (FSM). Chặn đứng mọi hành vi chuyển trạng thái phi lý bằng Guard Condition và tự động thực thi Action khi trạng thái thay đổi.

---

### XI. Xử Lý Tệp Tin & Tuần Hoàn Dữ Liệu (57 – 61)

#### 57. Apache POI (`57-ApachePOI`) — Port: 8157
- **Vấn đề thực tế**: Xuất báo cáo Excel 500.000 dòng khiến máy chủ hết RAM (OutOfMemory) và đơ toàn bộ hệ thống.
- **Ứng dụng thực tế**: Thao tác file Excel chuyên sâu (.xlsx). Sử dụng `SXSSFWorkbook` dạng Streaming để ghi hàng triệu bản ghi xuống đĩa mà chỉ tốn vài chục MB RAM. Hỗ trợ kẻ bảng, tô màu, công thức toán học (`SUM`, `AVERAGE`).

#### 58. OpenPDF (`58-OpenPDF`) — Port: 8158
- **Vấn đề thực tế**: Thư viện iText nổi tiếng nhưng bản mới (iText 7) dính bản quyền thương mại AGPL rất đắt và nguy cơ vi phạm bản quyền phần mềm nguồn đóng.
- **Ứng dụng thực tế**: Fork mã nguồn mở (giấy phép thân thiện LGPL) từ iText 4. Dùng để sinh hóa đơn điện tử PDF, vé máy bay, hợp đồng lao động có chèn logo và chữ ký số.

#### 59. OpenCSV (`59-OpenCSV`) — Port: 8159
- **Vấn đề thực tế**: Parse file CSV người dùng upload lên bằng lệnh `line.split(",")` bị lỗi ngay khi dữ liệu có chứa dấu phẩy bên trong ngoặc kép (ví dụ: `"Ha Noi, Viet Nam"`).
- **Ứng dụng thực tế**: Chuẩn hóa đọc/ghi CSV. Tự động map dòng CSV vào Java Record/Object theo tên cột, xử lý escape ký tự đặc biệt, hỗ trợ định dạng ngăn cách tùy chỉnh (dấu chấm phẩy, tab).

#### 60. Jackson Advanced (`60-Jackson`) — Port: 8160
- **Vấn đề thực tế**: Cùng một API, người dùng thông thường chỉ được xem 3 trường tóm tắt, còn Admin được xem toàn bộ 10 trường; hoặc cần parse đối tượng đa hình (Polymorphic JSON: Con mèo, Con chó cùng kế thừa Động vật).
- **Ứng dụng thực tế**: Các tính năng nâng cao của Jackson: `@JsonView` (lọc thuộc tính trả về theo vai trò), `@JsonTypeInfo` (đa hình), Custom Serializer (format tiền tệ, ngày tháng ISO-8601), MixIn (gắn annotation vào class thư viện thứ 3).

#### 61. Google Protocol Buffers (`61-Protobuf`) — Port: 8161
- **Vấn đề thực tế**: Truyền file JSON giữa các service tốn băng thông đường truyền internet và tiêu hao tài nguyên CPU để encode/decode văn bản.
- **Ứng dụng thực tế**: Chuẩn tuần hoàn nhị phân (Binary Serialization) của Google. Nhỏ hơn JSON từ 3 đến 10 lần, tốc độ parse nhanh gấp hàng chục lần, hỗ trợ tương thích ngược (Backward Compatibility) tuyệt đối qua định nghĩa `.proto`.

---

## 🎯 Gợi Ý Lựa Chọn Stack Thực Tế (Không Bị Lan Man)

Tùy theo bài toán bạn đang giải quyết, hãy chọn đúng bộ mảnh ghép:

| Loại Ứng Dụng | Bộ Thư Viện Cốt Lõi Khuyên Dùng |
|---|---|
| **1. Admin / Web Doanh Nghiệp (CRUD tiêu chuẩn)** | `01-SpringWebMVC` + `17-SpringDataJPA` + `24-Flyway` + `26-Caffeine` + `31-MapStruct` + `32-BeanValidation` + `53-SpringDocOpenAPI` + `57-ApachePOI` |
| **2. Sàn TMĐT / High-Traffic (Chịu tải lớn)** | `01-SpringWebMVC` (Virtual Threads) + `10-SpringKafka` + `28-Redisson` (Distributed Lock) + `29-Resilience4j` + `30-Sentinel` + `40-Micrometer` |
| **3. Ngân Hàng / FinTech / Ví Điện Tử** | `07-AxonFramework` (Event Sourcing) + `18-MyBatis`/`19-jOOQ` + `36-SpringSecurity` + `45-ShedLock` + `55-Camunda` (Duyệt hồ sơ) + `58-OpenPDF` (In hóa đơn) |
| **4. IoT / Real-Time Streaming** | `02-SpringWebFlux` + `14-EclipsePaho` (MQTT) + `15-gRPC` + `16-SpringWebSocket` + `61-Protobuf` |
| **5. Chuẩn Hóa Kiến Trúc & Testing** | `03-SpringModulith` + `46-JUnit5-Mockito` + `47-AssertJ` + `48-Testcontainers` + `51-ArchUnit` (Chống code ẩu) + `52-JMH` |

---

## 🚀 Cách Chạy Thử Bất Kỳ Dự Án Nào

Mọi thư mục dự án từ `01` đến `61` đều hoàn chỉnh, độc lập và có sẵn Maven Wrapper:

```bash
# 1. Chuyển vào thư mục dự án mong muốn (ví dụ dự án 29)
cd d:\GitHub\java-example\29-Resilience4j

# 2. Chạy ứng dụng
./mvnw spring-boot:run

# 3. Chạy toàn bộ test tự động (TDD)
./mvnw test
```

Mỗi thư mục dự án đều đi kèm một file **`requests.http`** để bạn có thể bấm gửi request kiểm thử trực tiếp trên VS Code hoặc IntelliJ IDEA!

package com.example.sentinel;

import com.example.sentinel.dto.ProductDto;
import com.example.sentinel.entity.Product;
import com.example.sentinel.repository.ProductRepository;
import com.example.sentinel.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.threads.virtual.enabled=true")
@AutoConfigureMockMvc
class SentinelApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private Long savedProductId;

    @BeforeEach
    void setup() throws InterruptedException {
        productRepository.deleteAll();
        Product product = new Product("Test Product", 100.0);
        savedProductId = productRepository.save(product).getId();
        // Ngủ 1s để reset QPS limit cho mỗi test, tránh bị block oan
        Thread.sleep(1000);
    }

    @Test
    void testNormalGetProduct() throws Exception {
        mockMvc.perform(get("/products/" + savedProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(100.0)));
    }

    @Test
    void testFlowControlTriggered() throws Exception {
        // QPS limit is 2. So 3rd request in a row should be blocked and return fallback response.
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(get("/products/" + savedProductId))
                    .andExpect(status().isOk());
        }
        
        // 3rd request should trigger blockHandler
        mockMvc.perform(get("/products/" + savedProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Blocked Product (Flow limit)")))
                .andExpect(jsonPath("$.price", is(0.0)));
    }

    @Test
    void testDegradeControlTriggered() throws Exception {
        // Min request amount is 2, error ratio 0.5.
        // We will send 2 requests that cause errors.
        String errorJson = """
                {
                    "name": "error",
                    "price": 0.0
                }
                """;

        // Gây ra lỗi 1 (fallback)
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(errorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Fallback Product")));

        // Gây ra lỗi 2 (fallback) -> Đạt ngưỡng ngắt mạch (2 request lỗi)
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(errorJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Fallback Product")));

        // Gửi request hợp lệ, nhưng mạch đã ngắt, nên vẫn vào fallback!
        String validJson = """
                {
                    "name": "Valid Product",
                    "price": 100.0
                }
                """;
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Fallback Product")));
    }

    @Test
    void testNormalCreateProduct() throws Exception {
        // Đợi một chút để mạch phục hồi nếu test trước đó chạy sát (timeWindow = 5s)
        // Tuy nhiên Spring context test chạy độc lập nếu không song song, nhưng statIntervalMs là 10s.
        // Tốt nhất là tạo một product hợp lệ ngay từ đầu khi mạch chưa ngắt hoặc dùng data khác.
        
        String validJson = """
                {
                    "name": "New Product",
                    "price": 200.0
                }
                """;
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.price", is(200.0)));
    }

    @Test
    void testProgrammaticApi() throws Exception {
        // Test xóa product bằng API lập trình
        mockMvc.perform(delete("/products/" + savedProductId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}

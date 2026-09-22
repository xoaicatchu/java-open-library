package com.example.springdoc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SpringDocOpenApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSwaggerUIAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection()); // redirects to swagger-ui/index.html
    }

    @Test
    void testApiDocsReturnsValidJSON() throws Exception {
        mockMvc.perform(get("/v3/api-docs/v1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").value(containsString("3.")))
                .andExpect(jsonPath("$.info.title").value("Product & Order Management API"))
                .andExpect(jsonPath("$.paths['/api/v1/products']").exists());
    }
    
    @Test
    void testApiDocsV2Groups() throws Exception {
        mockMvc.perform(get("/v3/api-docs/v2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paths['/api/v2/orders']").exists());
    }

    @Test
    void testCreateProductAndGetDocs() throws Exception {
        String req = """
                {
                  "name": "Phone",
                  "price": 1000
                }
                """;
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    void testCreateOrder() throws Exception {
        String req = """
                {
                  "customerName": "John Doe",
                  "productId": 1,
                  "quantity": 3
                }
                """;
        mockMvc.perform(post("/api/v2/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }
}

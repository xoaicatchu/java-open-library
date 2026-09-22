package com.example.mongodb;

import com.example.mongodb.dto.CategoryCountDto;
import com.example.mongodb.dto.ProductAvgRatingDto;
import com.example.mongodb.entity.Product;
import com.example.mongodb.entity.Review;
import com.example.mongodb.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Product p1 = new Product("Laptop Pro", 1500.0, "Electronics", Arrays.asList("tech", "laptop"), Arrays.asList(
                new Review("Alice", 5, "Great!"),
                new Review("Bob", 4, "Good battery")
        ));
        p1.setId("p1");

        Product p2 = new Product("Smartphone Max", 1000.0, "Electronics", Arrays.asList("tech", "phone"), Arrays.asList(
                new Review("Charlie", 4, "Nice screen"),
                new Review("Dave", 2, "Too expensive")
        ));
        p2.setId("p2");

        productRepository.saveAll(Arrays.asList(p1, p2));
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro"));
    }

    @Test
    void shouldGetProductById() throws Exception {
        mockMvc.perform(get("/api/products/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        Product p = new Product("Monitor", 300.0, "Electronics", Arrays.asList("tech"), List.of());
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Monitor"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldSearchProducts() throws Exception {
        mockMvc.perform(get("/api/products/search?text=Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro"));
    }

    @Test
    void shouldGetCategoryCounts() throws Exception {
        mockMvc.perform(get("/api/products/analytics/category-counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("Electronics"))
                .andExpect(jsonPath("$[0].count").value(2));
    }

    @Test
    void shouldGetAverageRatings() throws Exception {
        mockMvc.perform(get("/api/products/analytics/avg-ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}

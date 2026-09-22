package com.example.webmvc.controller;

import com.example.webmvc.entity.Product;
import com.example.webmvc.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho ProductController.
 * Dùng @SpringBootTest + MockMvc — test toàn bộ pipeline từ Controller -> Service -> Repository.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    private Product seedProduct(String name, String category, BigDecimal price) {
        return repository.save(new Product(name, "Mô tả " + name, price, category, 100));
    }

    // ──────────────────── GET ────────────────────

    @Nested
    @DisplayName("GET /api/products")
    class GetAll {

        @Test
        @DisplayName("Trả về danh sách phân trang — 200 OK")
        void shouldReturnPagedProducts() throws Exception {
            seedProduct("Laptop Dell", "Electronics", new BigDecimal("25000000"));
            seedProduct("iPhone 16", "Electronics", new BigDecimal("30000000"));

            mockMvc.perform(get("/api/products").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content[0].name").exists());
        }

        @Test
        @DisplayName("Trả về trang trống khi không có dữ liệu")
        void shouldReturnEmptyPage() throws Exception {
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetById {

        @Test
        @DisplayName("Trả về product khi tồn tại — 200 OK")
        void shouldReturnProduct() throws Exception {
            Product p = seedProduct("Laptop Dell", "Electronics", new BigDecimal("25000000"));

            mockMvc.perform(get("/api/products/{id}", p.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(p.getId()))
                    .andExpect(jsonPath("$.name").value("Laptop Dell"))
                    .andExpect(jsonPath("$.price").value(25000000));
        }

        @Test
        @DisplayName("Trả về 404 ProblemDetail khi không tồn tại")
        void shouldReturn404() throws Exception {
            mockMvc.perform(get("/api/products/{id}", 99999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Resource Not Found"))
                    .andExpect(jsonPath("$.detail", containsString("99999")));
        }
    }

    @Nested
    @DisplayName("GET /api/products/search")
    class Search {

        @Test
        @DisplayName("Tìm kiếm theo keyword — 200 OK")
        void shouldSearchByKeyword() throws Exception {
            seedProduct("Laptop Dell XPS", "Electronics", new BigDecimal("30000000"));
            seedProduct("Bàn phím cơ", "Accessories", new BigDecimal("2000000"));

            mockMvc.perform(get("/api/products/search").param("keyword", "laptop"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name", containsStringIgnoringCase("Laptop")));
        }
    }

    // ──────────────────── POST ────────────────────

    @Nested
    @DisplayName("POST /api/products")
    class Create {

        @Test
        @DisplayName("Tạo sản phẩm hợp lệ — 201 Created + Location header")
        void shouldCreateProduct() throws Exception {
            String body = """
                    {
                        "name": "MacBook Pro M4",
                        "description": "Apple Silicon chip M4, 16GB RAM",
                        "price": 45000000,
                        "category": "Electronics",
                        "stockQuantity": 50
                    }
                    """;

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("MacBook Pro M4"))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("Validation failed — 400 Bad Request + ProblemDetail errors")
        void shouldReturn400WhenInvalid() throws Exception {
            String body = """
                    {
                        "name": "",
                        "price": -1,
                        "category": "",
                        "stockQuantity": -5
                    }
                    """;

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors.name").exists())
                    .andExpect(jsonPath("$.errors.category").exists());
        }
    }

    // ──────────────────── PUT ────────────────────

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class Update {

        @Test
        @DisplayName("Cập nhật sản phẩm — 200 OK")
        void shouldUpdateProduct() throws Exception {
            Product p = seedProduct("Old Name", "Old Category", new BigDecimal("10000"));
            String body = """
                    {
                        "name": "New Name",
                        "description": "Updated description",
                        "price": 20000,
                        "category": "New Category",
                        "stockQuantity": 200,
                        "active": false
                    }
                    """;

            mockMvc.perform(put("/api/products/{id}", p.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New Name"))
                    .andExpect(jsonPath("$.active").value(false));
        }

        @Test
        @DisplayName("Cập nhật product không tồn tại — 404")
        void shouldReturn404WhenUpdatingNonExistent() throws Exception {
            String body = """
                    {
                        "name": "X", "price": 1, "category": "Y",
                        "stockQuantity": 0, "active": true
                    }
                    """;

            mockMvc.perform(put("/api/products/{id}", 99999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    // ──────────────────── DELETE ────────────────────

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class Delete {

        @Test
        @DisplayName("Xóa sản phẩm — 204 No Content")
        void shouldDeleteProduct() throws Exception {
            Product p = seedProduct("To Delete", "Temp", new BigDecimal("1000"));

            mockMvc.perform(delete("/api/products/{id}", p.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/products/{id}", p.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Xóa product không tồn tại — 404")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", 99999))
                    .andExpect(status().isNotFound());
        }
    }
}

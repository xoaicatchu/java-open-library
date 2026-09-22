package com.example.keycloak.controller;

import com.example.keycloak.dto.ProductDto;
import com.example.keycloak.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getProducts_PublicAccess_ReturnsOk() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(new ProductDto(1L, "Test", 10.0)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test"));
    }

    @Test
    void createProduct_NoAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Product1\",\"price\":100.0}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProduct_WithUserRole_ReturnsCreated() throws Exception {
        when(productService.createProduct(any())).thenReturn(new ProductDto(1L, "Product1", 100.0));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Product1\",\"price\":100.0}")
                        .with(jwt().jwt(builder -> builder.claim("realm_access", Map.of("roles", List.of("user"))))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Product1"));
    }

    @Test
    void createProduct_WithAdminRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Product1\",\"price\":100.0}")
                        .with(jwt().jwt(builder -> builder.claim("realm_access", Map.of("roles", List.of("admin"))))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_WithAdminRole_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .with(jwt().jwt(builder -> builder.claim("realm_access", Map.of("roles", List.of("admin"))))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_WithUserRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .with(jwt().jwt(builder -> builder.claim("realm_access", Map.of("roles", List.of("user"))))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProducts_WithCustomIssuer_ShouldWork() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/api/products")
                        .with(jwt().jwt(builder -> builder.issuer("http://localhost:8080/realms/tenant2"))))
                .andExpect(status().isOk());
    }
}

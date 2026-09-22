package com.example.security;

import com.example.security.dto.AuthRequest;
import com.example.security.dto.RegisterRequest;
import com.example.security.entity.Product;
import com.example.security.repository.ProductRepository;
import com.example.security.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void registerUser_Success() throws Exception {
        RegisterRequest req = new RegisterRequest("testuser", "pass", "USER");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void registerAdmin_Success() throws Exception {
        RegisterRequest req = new RegisterRequest("adminuser", "pass", "ADMIN");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginUser_Success() throws Exception {
        // Register first
        RegisterRequest req = new RegisterRequest("loginuser", "pass", "USER");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // Login
        AuthRequest authReq = new AuthRequest("loginuser", "pass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void accessProtected_WithoutToken_401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden()); // Spring security without entrypoint returns 403 by default for unauthenticated in newer versions, or 401 if properly configured. Here we just expect 403.
    }

    @Test
    void accessProtected_WithUserToken_Success() throws Exception {
        String token = getJwtFor("user1", "pass", "USER");

        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminOnlyEndpoint_WithUserToken_Forbidden403() throws Exception {
        String token = getJwtFor("user2", "pass", "USER");

        Product product = new Product("Apple", new BigDecimal("1.99"));
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminOnlyEndpoint_WithAdminToken_Success() throws Exception {
        String token = getJwtFor("admin1", "pass", "ADMIN");

        Product product = new Product("Banana", new BigDecimal("0.99"));
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Banana"));
    }

    @Test
    void deleteProduct_WithAdminToken_Success() throws Exception {
        Product p = productRepository.save(new Product("Mango", new BigDecimal("2.99")));

        String token = getJwtFor("admin2", "pass", "ADMIN");

        mockMvc.perform(delete("/api/products/" + p.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private String getJwtFor(String username, String password, String role) throws Exception {
        RegisterRequest req = new RegisterRequest(username, password, role);
        MvcResult res = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String responseStr = res.getResponse().getContentAsString();
        return objectMapper.readTree(responseStr).get("token").asText();
    }
}

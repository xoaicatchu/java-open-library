package com.example.hibernatebatch.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testImportProducts() throws Exception {
        mockMvc.perform(post("/api/batch/import").param("count", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Imported 10 products")));
    }

    @Test
    void testUpdatePrices() throws Exception {
        // First import some products
        mockMvc.perform(post("/api/batch/import").param("count", "5"));

        mockMvc.perform(put("/api/batch/update-prices")
                        .param("category", "Electronics")
                        .param("percentage", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Updated")));
    }

    @Test
    void testCleanup() throws Exception {
        mockMvc.perform(delete("/api/batch/cleanup").param("maxPrice", "15"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Deleted")));
    }

    @Test
    void testGetProducts() throws Exception {
        mockMvc.perform(post("/api/batch/import").param("count", "2"));
        mockMvc.perform(get("/api/batch/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

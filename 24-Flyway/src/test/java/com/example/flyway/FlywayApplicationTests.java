package com.example.flyway;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class FlywayApplicationTests {

    @Autowired
    private Flyway flyway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertNotNull(flyway);
    }

    @Test
    void testMigrationsApplied() {
        int appliedCount = flyway.info().applied().length;
        assertTrue(appliedCount >= 4, "Should have at least 4 migrations applied");
    }

    @Test
    void testTablesExistAndDataSeeded() {
        Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
        assertNotNull(productCount);
        assertTrue(productCount >= 3);
        
        Integer categoryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM categories", Integer.class);
        assertNotNull(categoryCount);
        assertTrue(categoryCount >= 2);
    }

    @Test
    void testViewWorks() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product_summary", Integer.class);
        assertNotNull(count);
        assertTrue(count > 0);
    }

    @Test
    void testJavaMigrationUpdatedDescription() {
        String desc = jdbcTemplate.queryForObject("SELECT description FROM products WHERE name = 'Laptop'", String.class);
        assertEquals("Updated Description by Java Migration", desc);
    }

    @Test
    void testFlywayHistoryEndpoint() throws Exception {
        mockMvc.perform(get("/api/flyway/history"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(4)))
               .andExpect(jsonPath("$[0].version").exists());
    }
}

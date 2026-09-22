package com.example.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ActuatorApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.example.actuator.endpoint.OrdersEndpoint ordersEndpoint;

    @BeforeEach
    void setUp() {
        ordersEndpoint.reset();
    }

    @Test
    void healthEndpoint_shouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void healthEndpoint_shouldContainCustomDatabaseIndicator() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.database.status").value("UP"));
    }

    @Test
    void healthEndpoint_shouldContainCustomExternalServiceIndicator() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.externalService.status").value("UP"));
    }

    @Test
    void infoEndpoint_shouldContainCustomInfoContributor() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("Actuator Demo App"))
                .andExpect(jsonPath("$.customInfo.bean-count").value(150))
                .andExpect(jsonPath("$.customInfo.author").value("Spring Boot Expert"));
    }

    @Test
    void customOrdersEndpoint_shouldReturnStats_andAllowUpdates() throws Exception {
        // Read Operation
        mockMvc.perform(get("/actuator/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(100));

        // Write Operation
        mockMvc.perform(post("/actuator/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"pending\"}"))
                .andExpect(status().is2xxSuccessful());

        // Verify update
        mockMvc.perform(get("/actuator/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(101))
                .andExpect(jsonPath("$.pendingOrders").value(6));
    }

    @Test
    void livenessProbe_shouldBeUp() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void readinessProbe_shouldBeUp() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void demoController_shouldGetAndPostOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").isNumber());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("completed"));
    }
}


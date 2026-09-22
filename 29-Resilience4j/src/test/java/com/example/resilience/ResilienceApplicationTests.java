package com.example.resilience;

import com.example.resilience.dto.PaymentRequest;
import com.example.resilience.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ResilienceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry cbRegistry;

    @BeforeEach
    void setup() {
        paymentService.setSimulateFailure(false);
        paymentService.resetAttempts();
        cbRegistry.circuitBreaker("paymentService").transitionToClosedState();
    }

    @Test
    void testCircuitBreakerSuccess() throws Exception {
        PaymentRequest request = new PaymentRequest("acc-1", new java.math.BigDecimal("100.00"));
        
        mockMvc.perform(post("/api/payments/circuit-breaker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void testCircuitBreakerFallback() throws Exception {
        paymentService.setSimulateFailure(true);
        PaymentRequest request = new PaymentRequest("acc-1", new java.math.BigDecimal("100.00"));
        
        mockMvc.perform(post("/api/payments/circuit-breaker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FALLBACK"));
    }
    
    @Test
    void testCircuitBreakerStateTransitions() throws Exception {
        paymentService.setSimulateFailure(true);
        PaymentRequest request = new PaymentRequest("acc-1", new java.math.BigDecimal("100.00"));
        
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/payments/circuit-breaker")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }
        
        mockMvc.perform(get("/actuator/health/circuitBreakers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.paymentService.details.state").value("OPEN"));
    }

    @Test
    void testRetryExhaustedAndFallback() throws Exception {
        paymentService.setSimulateFailure(true);
        PaymentRequest request = new PaymentRequest("acc-1", new java.math.BigDecimal("100.00"));
        
        mockMvc.perform(post("/api/payments/retry")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FALLBACK"));
                
        assertEquals(3, paymentService.getAttempts());
    }

    @Test
    void testRateLimiter() throws Exception {
        Thread.sleep(1000); 
        PaymentRequest request = new PaymentRequest("acc-1", new java.math.BigDecimal("100.00"));
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/payments/rate-limiter")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SUCCESS"));
        }
        
        mockMvc.perform(post("/api/payments/rate-limiter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FALLBACK"));
    }

    @Test
    void testProgrammaticApi() throws Exception {
        paymentService.setSimulateFailure(true);
        PaymentRequest request = new PaymentRequest("acc-1", new java.math.BigDecimal("100.00"));
        
        mockMvc.perform(post("/api/payments/programmatic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROGRAMMATIC_FALLBACK"));
    }

    @Test
    void testTimeLimiterFallback() throws Exception {
        paymentService.setSimulateFailure(true);
        PaymentRequest request = new PaymentRequest("acc-1", new java.math.BigDecimal("100.00"));
        
        MvcResult mvcResult = mockMvc.perform(post("/api/payments/time-limiter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();
                
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FALLBACK"));
    }
}

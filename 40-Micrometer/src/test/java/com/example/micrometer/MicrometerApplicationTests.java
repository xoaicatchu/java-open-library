package com.example.micrometer;

import com.example.micrometer.dto.OrderRequest;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MicrometerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void testCounterIncrements() throws Exception {
        double initialCount = getCounterValue("orders.created.total");

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "productCode": "P-100",
                            "amount": 250.50
                        }
                        """))
                .andExpect(status().isOk());

        double newCount = getCounterValue("orders.created.total");
        assertThat(newCount).isEqualTo(initialCount + 1);
    }

    @Test
    void testTimerRecords() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk());

        // Kiểm tra @Timed từ Controller
        var timer = meterRegistry.find("api.orders.getall.time").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isGreaterThan(0);
    }

    @Test
    void testGaugeReflectsState() throws Exception {
        var gauge = meterRegistry.find("users.active.count").gauge();
        assertThat(gauge).isNotNull();
        
        double initialUsers = gauge.value();
        
        // Simulate login
        mockMvc.perform(post("/api/orders/users/login")).andExpect(status().isOk());
        
        assertThat(gauge.value()).isEqualTo(initialUsers + 1);
    }

    @Test
    void testDistributionSummaryRecordsAmount() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "productCode": "P-200",
                            "amount": 500.00
                        }
                        """))
                .andExpect(status().isOk());

        var summary = meterRegistry.find("orders.amount.distribution").summary();
        assertThat(summary).isNotNull();
        assertThat(summary.count()).isGreaterThan(0);
        // Có thể tổng (totalAmount) > 0
        assertThat(summary.totalAmount()).isGreaterThan(0);
    }

    @Test
    void testPrometheusEndpointWorks() throws Exception {
        // Tạo một số metric trước khi test
        mockMvc.perform(get("/api/orders"));
        
        mockMvc.perform(get("/actuator"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());

        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("api_orders_getall_time");
                    assertThat(content).contains("application=\"micrometer-demo\""); // Check tag
                });
    }
    
    @Test
    void testMetricTagsArePresent() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "productCode": "TAG-123",
                            "amount": 100.00
                        }
                        """))
                .andExpect(status().isOk());

        // Kiểm tra metric được tag theo product code
        var counter = meterRegistry.find("orders.created.by.product").tag("product", "TAG-123").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isGreaterThan(0);
    }

    private double getCounterValue(String metricName) {
        var counter = meterRegistry.find(metricName).counter();
        return counter != null ? counter.count() : 0.0;
    }
}

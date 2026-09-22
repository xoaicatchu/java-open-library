package com.example.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.logback.controller.OrderController;
import com.example.logback.filter.CorrelationIdFilter;
import com.example.logback.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LogbackApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger controllerLogger;
    private Logger serviceLogger;

    @BeforeEach
    void setup() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        listAppender = new ListAppender<>();
        listAppender.start();

        controllerLogger = context.getLogger(OrderController.class);
        serviceLogger = context.getLogger(OrderService.class);
        
        controllerLogger.addAppender(listAppender);
        serviceLogger.addAppender(listAppender);
    }

    @AfterEach
    void teardown() {
        controllerLogger.detachAppender(listAppender);
        serviceLogger.detachAppender(listAppender);
    }

    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void shouldAddCorrelationIdFromHeader() throws Exception {
        mockMvc.perform(post("/orders")
                .header("X-Correlation-Id", "test-corr-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":\"p123\", \"quantity\":10}"))
                .andExpect(status().isOk());

        boolean hasCorrelationId = listAppender.list.stream()
                .anyMatch(event -> "test-corr-id".equals(event.getMDCPropertyMap().get("correlationId")));
        
        assertThat(hasCorrelationId).isTrue();
    }

    @Test
    void shouldGenerateCorrelationIdIfMissing() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":\"p123\", \"quantity\":10}"))
                .andExpect(status().isOk());

        boolean hasGeneratedCorrelationId = listAppender.list.stream()
                .anyMatch(event -> {
                    String corrId = event.getMDCPropertyMap().get("correlationId");
                    return corrId != null && !corrId.isEmpty();
                });
        
        assertThat(hasGeneratedCorrelationId).isTrue();
    }
    
    @Test
    void shouldPropagateUserIdInMDC() throws Exception {
        mockMvc.perform(post("/orders")
                .header("X-User-Id", "user999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":\"p123\", \"quantity\":10}"))
                .andExpect(status().isOk());

        boolean hasUserId = listAppender.list.stream()
                .anyMatch(event -> "user999".equals(event.getMDCPropertyMap().get("userId")));
        
        assertThat(hasUserId).isTrue();
    }

    @Test
    void shouldLogAuditMarkerForLargeOrders() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":\"p123\", \"quantity\":150}"))
                .andExpect(status().isOk());

        boolean hasAuditMarker = listAppender.list.stream()
                .anyMatch(event -> event.getMarkerList() != null && event.getMarkerList().stream().anyMatch(marker -> marker.getName().equals("AUDIT")) && event.getMessage().contains("Large order detected"));
        
        assertThat(hasAuditMarker).isTrue();
    }
    
    @Test
    void shouldLogStructuredArgument() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":\"p123\", \"quantity\":25}"))
                .andExpect(status().isOk());

        boolean hasStructuredLog = listAppender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("Order detail: order_qty=25"));
        
        assertThat(hasStructuredLog).isTrue();
    }
}

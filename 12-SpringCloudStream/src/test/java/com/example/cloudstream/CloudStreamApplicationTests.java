package com.example.cloudstream;

import com.example.cloudstream.dto.EnrichedOrderEvent;
import com.example.cloudstream.dto.OrderEvent;
import com.example.cloudstream.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CloudStreamApplication.class)
@AutoConfigureMockMvc
@Import(TestChannelBinderConfiguration.class)
class CloudStreamApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
    }

    @Autowired
    private Function<OrderEvent, EnrichedOrderEvent> enrichOrder;

    @Autowired
    private Consumer<EnrichedOrderEvent> processOrder;

    @Test
    void testFunctionTransformsMessageCorrectly() {
        OrderEvent orderEvent = new OrderEvent("F-001", "CUST-A", new BigDecimal("50"), false);
        EnrichedOrderEvent enriched = enrichOrder.apply(orderEvent);
        
        assertThat(enriched).isNotNull();
        assertThat(enriched.orderId()).isEqualTo("F-001");
        assertThat(enriched.shippingFee()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(enriched.status()).isEqualTo("ENRICHED");
    }

    @Test
    void testFunctionTransformsMessageCorrectly_FreeShipping() {
        OrderEvent orderEvent = new OrderEvent("F-002", "CUST-B", new BigDecimal("150"), false);
        EnrichedOrderEvent enriched = enrichOrder.apply(orderEvent);
        
        assertThat(enriched.shippingFee()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testConsumerReceivesAndPersists() {
        EnrichedOrderEvent enriched = new EnrichedOrderEvent("C-001", "CUST-C", new BigDecimal("100"), new BigDecimal("10"), "ENRICHED", false);
        processOrder.accept(enriched);
        
        assertThat(orderRepository.findById("C-001")).isPresent();
        assertThat(orderRepository.findById("C-001").get().getTotalAmount()).isEqualByComparingTo(new BigDecimal("110"));
    }

    @Test
    void testConsumerErrorHandling() {
        EnrichedOrderEvent enriched = new EnrichedOrderEvent("C-ERR", "CUST-ERR", new BigDecimal("100"), new BigDecimal("10"), "ENRICHED", true);
        try {
            processOrder.accept(enriched);
        } catch (Exception e) {
            // Expected
        }
        
        assertThat(orderRepository.findById("C-ERR")).isEmpty();
    }

    @Test
    void testStreamBridgeSendsMessageViaRest() throws Exception {
        OrderEvent orderEvent = new OrderEvent("REST-001", "CUST-R", new BigDecimal("75"), false);
        
        mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(orderEvent)))
                .andExpect(status().isOk());
                
        // StreamBridge sends to "raw-orders", intercept it with test binder
        org.springframework.messaging.Message<byte[]> rawMessage = outputDestination.receive(5000, "raw-orders");
        assertThat(rawMessage).isNotNull();
        
        OrderEvent receivedEvent = objectMapper.readValue(rawMessage.getPayload(), OrderEvent.class);
        assertThat(receivedEvent.orderId()).isEqualTo("REST-001");
    }
}

package com.example.integration;

import com.example.integration.dto.OrderItem;
import com.example.integration.dto.OrderRequest;
import com.example.integration.dto.OrderResponse;
import com.example.integration.dto.ProcessedItem;
import com.example.integration.gateway.OrderGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IntegrationTests {

    @Autowired
    private OrderGateway orderGateway;

    @Test
    void testStandardOrderFlow() {
        OrderRequest request = new OrderRequest("O1", "John", "STANDARD", List.of(
                new OrderItem("I1", "Laptop", 1, 1000.0)
        ));

        OrderResponse response = orderGateway.processOrder(request);

        assertNotNull(response);
        assertEquals("STANDARD_DELIVERY", response.shippingMethod());
        assertEquals(1005.0, response.totalAmount()); // 1000 + 5 shipping
        assertEquals(1, response.processedItems().size());
    }

    @Test
    void testExpressOrderFlow() {
        OrderRequest request = new OrderRequest("O2", "Jane", "EXPRESS", List.of(
                new OrderItem("I2", "Phone", 2, 500.0)
        ));

        OrderResponse response = orderGateway.processOrder(request);

        assertNotNull(response);
        assertEquals("EXPRESS_NEXT_DAY", response.shippingMethod());
        assertEquals(1015.0, response.totalAmount()); // 1000 + 15 shipping
    }

    @Test
    void testFilterEmptyOrder() {
        OrderRequest request = new OrderRequest("O3", "Jack", "STANDARD", Collections.emptyList());
        
        // Because of the filter, the message is discarded. Gateway returns null if no reply is received
        // Note: You may need to configure gateway timeout or handle discarding explicitly. By default returns null if it times out or reaches a null channel.
        OrderResponse response = orderGateway.processOrder(request);
        assertNull(response);
    }
    
    @Test
    void testMultipleItemsAggregation() {
        OrderRequest request = new OrderRequest("O4", "Jill", "STANDARD", List.of(
                new OrderItem("I1", "A", 2, 10.0),
                new OrderItem("I2", "B", 3, 20.0)
        ));

        OrderResponse response = orderGateway.processOrder(request);

        assertNotNull(response);
        assertEquals(2, response.processedItems().size());
        assertEquals(85.0, response.totalAmount()); // 20 + 60 + 5 shipping
    }

    @Test
    void testProcessOrderMessageWithHeaders() {
        OrderRequest request = new OrderRequest("O5", "Bob", "EXPRESS", List.of(
                new OrderItem("I1", "C", 1, 50.0)
        ));
        Message<OrderRequest> msg = MessageBuilder.withPayload(request)
                .setHeader("custom-header", "value1")
                .build();
                
        OrderResponse response = orderGateway.processOrderMessage(msg);
        
        assertNotNull(response);
        assertEquals("EXPRESS_NEXT_DAY", response.shippingMethod());
        assertEquals(65.0, response.totalAmount()); // 50 + 15 shipping
    }

    @Test
    void testSplitterLogic() {
        OrderRequest request = new OrderRequest("O6", "Alice", "STANDARD", List.of(
                new OrderItem("I1", "D", 1, 100.0)
        ));

        OrderResponse response = orderGateway.processOrder(request);

        assertNotNull(response);
        assertEquals(1, response.processedItems().size());
        ProcessedItem item = response.processedItems().get(0);
        assertEquals("D", item.name());
        assertEquals(100.0, item.totalLinePrice());
    }
}

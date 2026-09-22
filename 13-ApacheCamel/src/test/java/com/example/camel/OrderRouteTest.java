package com.example.camel;

import com.example.camel.dto.OrderItem;
import com.example.camel.dto.OrderRequest;
import com.example.camel.dto.OrderResponse;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:result")
    private MockEndpoint mockResult;

    @EndpointInject("mock:deadLetter")
    private MockEndpoint mockDeadLetter;

    @BeforeEach
    public void setup() {
        mockResult.reset();
        mockDeadLetter.reset();
    }

    @Test
    public void testStandardOrderProcessing() throws Exception {
        OrderRequest request = new OrderRequest("O123", "standard", List.of(new OrderItem("I-1", 2)));
        mockResult.expectedMessageCount(1);
        
        OrderResponse response = producerTemplate.requestBody("direct:processOrder", request, OrderResponse.class);
        
        assertNotNull(response);
        assertEquals("O123", response.orderId());
        assertEquals("Processed", response.status());
        assertEquals(1, response.processedItems().size());
        assertTrue(response.processedItems().get(0).contains("I-1"));
        
        mockResult.assertIsSatisfied();
    }

    @Test
    public void testExpressOrderProcessing() throws Exception {
        OrderRequest request = new OrderRequest("O124", "express", List.of(new OrderItem("I-2", 5)));
        mockResult.expectedMessageCount(1);
        OrderResponse response = producerTemplate.requestBody("direct:processOrder", request, OrderResponse.class);
        assertNotNull(response);
        assertEquals("O124", response.orderId());
        mockResult.assertIsSatisfied();
    }

    @Test
    public void testInternationalOrderProcessing() throws Exception {
        OrderRequest request = new OrderRequest("O125", "international", List.of(new OrderItem("I-3", 1)));
        mockResult.expectedMessageCount(1);
        OrderResponse response = producerTemplate.requestBody("direct:processOrder", request, OrderResponse.class);
        assertNotNull(response);
        assertEquals("O125", response.orderId());
        mockResult.assertIsSatisfied();
    }

    @Test
    public void testSplitAndAggregateMultipleItems() throws Exception {
        OrderRequest request = new OrderRequest("O126", "standard", List.of(
                new OrderItem("I-4", 1),
                new OrderItem("I-5", 2)
        ));
        mockResult.expectedMessageCount(1);
        OrderResponse response = producerTemplate.requestBody("direct:processOrder", request, OrderResponse.class);
        assertNotNull(response);
        assertEquals("O126", response.orderId());
        assertEquals(2, response.processedItems().size());
        assertTrue(response.processedItems().stream().anyMatch(item -> item.contains("I-4")));
        assertTrue(response.processedItems().stream().anyMatch(item -> item.contains("I-5")));
        mockResult.assertIsSatisfied();
    }

    @Test
    public void testErrorHandlerWithDeadLetter() throws Exception {
        OrderRequest request = new OrderRequest("O127", "standard", List.of(new OrderItem("error-item", 1)));
        
        mockDeadLetter.expectedMessageCount(1);
        
        try {
            producerTemplate.requestBody("direct:processOrder", request);
        } catch (Exception e) {
            // expected
        }
        
        mockDeadLetter.assertIsSatisfied(2000);
    }
    
    @Test
    public void testRestEndpointContext() throws Exception {
        assertNotNull(camelContext.getRoute("processOrderRoute"));
    }
}

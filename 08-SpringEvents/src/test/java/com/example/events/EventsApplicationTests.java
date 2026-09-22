package com.example.events;

import com.example.events.dto.OrderRequest;
import com.example.events.entity.AuditLog;
import com.example.events.event.GenericDomainEvent;
import com.example.events.event.OrderCancelledEvent;
import com.example.events.event.OrderCreatedEvent;
import com.example.events.listener.InventoryListener;
import com.example.events.listener.NotificationListener;
import com.example.events.repository.AuditLogRepository;
import com.example.events.repository.OrderRepository;
import com.example.events.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@RecordApplicationEvents
class EventsApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private ApplicationEvents applicationEvents;

    @SpyBean
    private InventoryListener inventoryListener;

    @SpyBean
    private NotificationListener notificationListener;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        auditLogRepository.deleteAll();
    }

    @Test
    void testCreateOrderPublishesEvent() throws Exception {
        OrderRequest request = new OrderRequest("Laptop", 2);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("CREATED"));

        long publishedEventsCount = applicationEvents.stream(OrderCreatedEvent.class).count();
        assertThat(publishedEventsCount).isEqualTo(1);
    }

    @Test
    void testMultipleListenersReceiveSameEvent() throws Exception {
        OrderRequest request = new OrderRequest("Mouse", 5);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify InventoryListener (sync) is called
        verify(inventoryListener).handleOrderCreated(any(OrderCreatedEvent.class));
        
        // Verify NotificationListener (async) is called, use timeout because it's async
        verify(notificationListener, timeout(2000)).handleOrderCreated(any(OrderCreatedEvent.class));
    }

    @Test
    void testTransactionalEventListenerFiresAfterCommit() throws Exception {
        OrderRequest request = new OrderRequest("Keyboard", 1);
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Wait a bit for the async transactional listener to complete if needed, though it's sync in a new tx here usually
        Thread.sleep(500); 

        List<AuditLog> logs = auditLogRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.getFirst().getAction()).isEqualTo("ORDER_CREATED");
    }

    @Test
    void testAsyncEventHandling() throws Exception {
        OrderRequest request = new OrderRequest("Monitor", 1);
        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        
        Long orderId = objectMapper.readTree(response).get("id").asLong();

        // cancel order
        mockMvc.perform(post("/api/orders/" + orderId + "/cancel"))
                .andExpect(status().isOk());
                
        // Verify NotificationListener async handler is called
        verify(notificationListener, timeout(2000)).handleOrderCancelled(any(OrderCancelledEvent.class));
    }

    @Test
    void testGenericEventIsPublished() {
        orderService.createOrder(new OrderRequest("Phone", 1));
        
        long genericEventsCount = applicationEvents.stream(GenericDomainEvent.class).count();
        assertThat(genericEventsCount).isEqualTo(1);
    }
    
    @Test
    void testOrderCancellationPublishesEvent() throws Exception {
        OrderRequest request = new OrderRequest("Tablet", 3);
        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        
        Long orderId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(post("/api/orders/" + orderId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
                
        long cancelledEventsCount = applicationEvents.stream(OrderCancelledEvent.class).count();
        assertThat(cancelledEventsCount).isEqualTo(1);
    }
}

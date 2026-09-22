package com.example.statemachine;

import com.example.statemachine.domain.OrderEvent;
import com.example.statemachine.domain.OrderState;
import com.example.statemachine.entity.OrderEntity;
import com.example.statemachine.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderStateMachineTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> factory;

    @Test
    void testStateMachineFactoryAvailable() {
        StateMachine<OrderState, OrderEvent> stateMachine = factory.getStateMachine();
        assertThat(stateMachine).isNotNull();
    }

    @Test
    void testOrderCreationHappyPath() throws Exception {
        OrderEntity order = orderService.createOrder("Test Order");
        assertThat(order.getState()).isEqualTo(OrderState.CREATED);
        assertThat(order.getHistory()).hasSize(1);
    }

    @Test
    void testOrderStateTransitionToSubmitted() {
        OrderEntity order = orderService.createOrder("Test Transition");
        order = orderService.sendEvent(order.getId(), OrderEvent.SUBMIT);
        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);
        assertThat(order.getHistory()).hasSize(2);
    }

    @Test
    void testCancelGuardPreventsCancelAfterShipped() {
        OrderEntity order = orderService.createOrder("Guard Test");
        order = orderService.sendEvent(order.getId(), OrderEvent.SUBMIT);
        order = orderService.sendEvent(order.getId(), OrderEvent.PAY);
        order = orderService.sendEvent(order.getId(), OrderEvent.SHIP);
        
        assertThat(order.getState()).isEqualTo(OrderState.SHIPPED);

        // Try cancelling (Guard should prevent this, state shouldn't change)
        order = orderService.sendEvent(order.getId(), OrderEvent.CANCEL);
        assertThat(order.getState()).isEqualTo(OrderState.SHIPPED);
    }

    @Test
    void testCancelGuardAllowsCancelBeforeShipped() {
        OrderEntity order = orderService.createOrder("Cancel Allowed Test");
        order = orderService.sendEvent(order.getId(), OrderEvent.SUBMIT);
        
        assertThat(order.getState()).isEqualTo(OrderState.SUBMITTED);

        // Try cancelling (Guard should allow)
        order = orderService.sendEvent(order.getId(), OrderEvent.CANCEL);
        assertThat(order.getState()).isEqualTo(OrderState.CANCELLED);
    }

    @Test
    void testRestApiEndpoints() throws Exception {
        // Create Order via REST
        String response = mockMvc.perform(post("/api/orders").param("description", "REST Order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("CREATED"))
                .andReturn().getResponse().getContentAsString();

        // Extract ID (primitive parsing for simplicity)
        String idStr = response.split("\"id\":")[1].split(",")[0];
        
        // Trigger Event SUBMIT via REST
        mockMvc.perform(post("/api/orders/" + idStr + "/events").param("event", "SUBMIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("SUBMITTED"))
                .andExpect(jsonPath("$.history.length()").value(2));

        // Get Order
        mockMvc.perform(get("/api/orders/" + idStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("SUBMITTED"));
    }
}

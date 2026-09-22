package com.example.modulith.order;

import com.example.modulith.order.dto.CreateOrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import java.math.BigDecimal;

@ApplicationModuleTest
class OrderModuleTest {

    @Autowired
    OrderService orders;

    @Test
    void shouldPublishOrderCreatedEvent(Scenario scenario) {
        scenario.stimulate(() -> orders.createOrder(new CreateOrderRequest("John Doe", new BigDecimal("100.50"))))
                .andWaitForEventOfType(OrderCreatedEvent.class)
                .toArrive();
    }
}

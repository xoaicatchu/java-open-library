package com.example.modulith.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

@ApplicationModuleTest
class OrderModuleTest {

    @Autowired
    OrderService orders;

    @Test
    void shouldPublishOrderCreatedEvent(Scenario scenario) {
        scenario.stimulate(() -> orders.createOrder("PROD1", 2))
                .andWaitForEventOfType(OrderCreatedEvent.class)
                .toArrive();
    }
}

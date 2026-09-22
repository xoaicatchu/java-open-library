package com.example.modulith.inventory;

import com.example.modulith.order.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.math.BigDecimal;

@ApplicationModuleTest
class InventoryModuleTest {

    @Autowired
    InventoryManagement inventory;

    @Test
    void shouldReactToOrderCreatedEvent(Scenario scenario) {
        scenario.publish(new OrderCreatedEvent(1L, "Alice", new BigDecimal("50.00")))
                .andWaitForStateChange(() -> inventory.getProcessedEvents() > 0);
    }
}

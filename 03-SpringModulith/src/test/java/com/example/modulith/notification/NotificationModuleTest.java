package com.example.modulith.notification;

import com.example.modulith.order.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.math.BigDecimal;

@ApplicationModuleTest
class NotificationModuleTest {

    @Autowired
    NotificationService notifications;

    @Test
    void shouldReactToOrderCreatedEvent(Scenario scenario) {
        scenario.publish(new OrderCreatedEvent(2L, "Bob", new BigDecimal("150.00")))
                .andWaitForStateChange(() -> notifications.getNotificationsSent() > 0);
    }
}

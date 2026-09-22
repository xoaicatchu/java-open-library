package com.example.modulith.notification;

import com.example.modulith.order.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private int notificationsSent = 0;

    @ApplicationModuleListener
    void on(OrderCreatedEvent event) {
        log.info("Sending notification for order {}", event.orderId());
        notificationsSent++;
    }

    public int getNotificationsSent() {
        return notificationsSent;
    }
}

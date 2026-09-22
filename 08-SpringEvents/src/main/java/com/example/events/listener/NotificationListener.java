package com.example.events.listener;

import com.example.events.event.OrderCancelledEvent;
import com.example.events.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @Async
    @EventListener
    @Order(2) // Chạy thứ 2 (dù Async không đảm bảo hoàn thành trước/sau nhưng Spring sẽ sắp xếp invoke)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("NotificationListener (Async): Sending email for created order ID: {}", event.orderId());
    }

    @Async
    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("NotificationListener (Async): Sending email for cancelled order ID: {}", event.orderId());
    }
}

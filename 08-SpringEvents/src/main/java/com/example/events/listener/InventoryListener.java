package com.example.events.listener;

import com.example.events.event.OrderCancelledEvent;
import com.example.events.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryListener.class);

    @EventListener
    @Order(1) // Chạy đầu tiên
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("InventoryListener (Sync): Deducting stock for order ID: {}, quantity: {}", event.orderId(), event.quantity());
        // Deduct stock logic here
    }

    @EventListener
    @Order(1)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("InventoryListener (Sync): Restoring stock for order ID: {}, quantity: {}", event.orderId(), event.quantity());
        // Restore stock logic here
    }
}

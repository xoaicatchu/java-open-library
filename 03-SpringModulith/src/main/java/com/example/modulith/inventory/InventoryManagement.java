package com.example.modulith.inventory;

import com.example.modulith.order.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryManagement {
    private static final Logger log = LoggerFactory.getLogger(InventoryManagement.class);
    
    private int processedEvents = 0;

    @ApplicationModuleListener
    void on(OrderCreatedEvent event) {
        log.info("Deducting stock for product {} by {}", event.productCode(), event.quantity());
        processedEvents++;
    }

    public int getProcessedEvents() {
        return processedEvents;
    }
}

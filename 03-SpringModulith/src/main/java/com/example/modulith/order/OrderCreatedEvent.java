package com.example.modulith.order;

import org.springframework.modulith.events.Externalized;

@Externalized("order-created::#{#this.orderId()}")
public record OrderCreatedEvent(Long orderId, String productCode, int quantity) {}

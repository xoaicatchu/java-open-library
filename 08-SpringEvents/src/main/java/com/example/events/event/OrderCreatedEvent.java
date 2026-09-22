package com.example.events.event;

public record OrderCreatedEvent(Long orderId, String productName, int quantity) {
}

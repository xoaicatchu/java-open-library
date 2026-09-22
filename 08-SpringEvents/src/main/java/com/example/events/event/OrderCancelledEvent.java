package com.example.events.event;

public record OrderCancelledEvent(Long orderId, String productName, int quantity) {
}

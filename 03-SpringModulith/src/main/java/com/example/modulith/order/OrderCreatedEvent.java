package com.example.modulith.order;

import java.math.BigDecimal;

public record OrderCreatedEvent(Long orderId, String customerName, BigDecimal totalAmount) {}

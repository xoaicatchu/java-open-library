package com.example.amqp.dto;

import java.math.BigDecimal;

public record OrderNotification(String orderId, String customerId, BigDecimal amount) {
}

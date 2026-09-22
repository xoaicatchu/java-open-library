package com.example.amqp.dto;

import java.math.BigDecimal;

public record PaymentNotification(String paymentId, String orderId, BigDecimal amount, String status) {
}

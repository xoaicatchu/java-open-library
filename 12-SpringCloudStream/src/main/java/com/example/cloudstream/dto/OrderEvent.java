package com.example.cloudstream.dto;

import java.math.BigDecimal;

public record OrderEvent(
    String orderId,
    String customerId,
    BigDecimal amount,
    boolean shouldFail
) {}

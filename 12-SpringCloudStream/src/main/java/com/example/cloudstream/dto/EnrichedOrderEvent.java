package com.example.cloudstream.dto;

import java.math.BigDecimal;

public record EnrichedOrderEvent(
    String orderId,
    String customerId,
    BigDecimal amount,
    BigDecimal shippingFee,
    String status,
    boolean shouldFail
) {}

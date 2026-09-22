package com.example.kafka.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceUpdateEvent(
    Long productId,
    BigDecimal oldPrice,
    BigDecimal newPrice,
    Instant timestamp
) {}

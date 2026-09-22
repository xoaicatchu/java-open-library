package com.example.resilience.dto;

import java.math.BigDecimal;

public record PaymentRequest(String accountId, BigDecimal amount) {}

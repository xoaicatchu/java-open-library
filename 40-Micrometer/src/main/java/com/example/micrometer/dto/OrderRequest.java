package com.example.micrometer.dto;

import java.math.BigDecimal;

public record OrderRequest(String productCode, BigDecimal amount) {
}

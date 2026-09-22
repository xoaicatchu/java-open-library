package com.example.jooq.dto;

import java.math.BigDecimal;

public record ProductRankDto(Integer productId, String productName, BigDecimal totalRevenue, Integer rank) {}

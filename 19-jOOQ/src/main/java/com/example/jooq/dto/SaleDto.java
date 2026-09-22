package com.example.jooq.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaleDto(Integer id, Integer productId, Integer quantity, LocalDate saleDate, BigDecimal revenue) {}

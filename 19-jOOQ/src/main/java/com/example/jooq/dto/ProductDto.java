package com.example.jooq.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductDto(Integer id, String name, Integer categoryId, BigDecimal price) {}

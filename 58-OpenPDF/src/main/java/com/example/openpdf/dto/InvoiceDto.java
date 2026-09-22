package com.example.openpdf.dto;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceDto(
    Long orderId,
    String customerName,
    List<ProductDto> products,
    BigDecimal totalAmount
) {}

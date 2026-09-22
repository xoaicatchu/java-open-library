package com.example.problemdetail.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequest(
    @NotBlank(message = "Customer ID is required") String customerId,
    @NotEmpty(message = "Items cannot be empty") List<@Valid OrderItemRequest> items
) {}
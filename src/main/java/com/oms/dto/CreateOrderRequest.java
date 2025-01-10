package com.oms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
    @NotNull(message = "Customer ID is required")
    Long customerId,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    Double amount
) {} 
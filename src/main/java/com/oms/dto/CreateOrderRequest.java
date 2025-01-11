package com.oms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record CreateOrderRequest(
    @NotNull(message = "Customer ID is required")
    Long customerId,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount
) {} 
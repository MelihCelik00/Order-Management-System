package com.oms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record OrderDTO(
    Long id,
    
    @NotNull(message = "Customer ID is required")
    Long customerId,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    Double amount,
    
    Double discountAmount,
    
    Double finalAmount,
    
    LocalDateTime orderDate
) {
    public OrderDTO {
        if (orderDate == null) orderDate = LocalDateTime.now();
    }
} 
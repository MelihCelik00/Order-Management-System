package com.oms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderDTO(
    Long id,
    
    @NotNull(message = "Customer ID is required")
    Long customerId,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,
    
    BigDecimal discountAmount,
    
    BigDecimal finalAmount,
    
    LocalDateTime orderDate
) {
    public OrderDTO {
        if (orderDate == null) orderDate = LocalDateTime.now();
    }
} 
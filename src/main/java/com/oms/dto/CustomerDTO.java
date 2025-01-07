package com.oms.dto;

import com.oms.entity.Customer.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerDTO(
    Long id,
    
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    CustomerTier tier,
    
    Integer totalOrders
) {
    public CustomerDTO {
        if (tier == null) tier = CustomerTier.REGULAR;
        if (totalOrders == null) totalOrders = 0;
    }
} 
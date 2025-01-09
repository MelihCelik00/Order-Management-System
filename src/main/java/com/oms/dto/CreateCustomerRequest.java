package com.oms.dto;

import com.oms.entity.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    CustomerTier tier
) {
    public CreateCustomerRequest {
        if (tier == null) tier = CustomerTier.REGULAR;
    }
}

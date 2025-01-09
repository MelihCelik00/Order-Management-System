package com.oms.dto;

import com.oms.entity.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerResponse(
    Long id,
    String name,
    String email,
    CustomerTier tier,
    Integer totalOrders
) {
    public CustomerResponse {
        if (tier == null) tier = CustomerTier.REGULAR;
        if (totalOrders == null) totalOrders = 0;
    }
} 
package com.oms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oms.entity.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateCustomerRequest(
    @NotBlank(message = "Name cannot be blank")
    String name,
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    String email,
    
    CustomerTier tier
) {}

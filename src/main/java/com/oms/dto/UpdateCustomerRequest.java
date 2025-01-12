package com.oms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateCustomerRequest(
    @NotBlank(message = "Name cannot be blank")
    String name,
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    String email
) {} 
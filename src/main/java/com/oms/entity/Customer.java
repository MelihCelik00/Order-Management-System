package com.oms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private CustomerTier tier = CustomerTier.REGULAR;

    private Integer totalOrders = 0;

    public void incrementTotalOrders() {
        this.totalOrders++;
        updateTier();
    }

    private void updateTier() {
        if (this.totalOrders >= 20) {
            this.tier = CustomerTier.PLATINUM;
        } else if (this.totalOrders >= 10) {
            this.tier = CustomerTier.GOLD;
        }
    }
} 
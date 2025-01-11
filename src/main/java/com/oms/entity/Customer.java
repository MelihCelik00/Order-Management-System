package com.oms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private CustomerTier tier = CustomerTier.REGULAR;

    @Builder.Default
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
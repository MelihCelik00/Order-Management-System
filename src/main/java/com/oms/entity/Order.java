package com.oms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private Customer customer;

    @NotNull(message = "Order amount is required")
    @Positive(message = "Order amount must be positive")
    private Double amount;

    private Double discountAmount;

    private Double finalAmount;

    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @PrePersist
    public void calculateAmounts() {
        if (customer != null && amount != null) {
            double discountPercentage = customer.getDiscountPercentage();
            this.discountAmount = amount * discountPercentage;
            this.finalAmount = amount - discountAmount;
        }
    }
} 
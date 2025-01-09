package com.oms.entity;

import jakarta.persistence.*;
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
    private Customer customer;

    @Column(nullable = false)
    private Double amount;

    private Double discountAmount;

    private Double finalAmount;

    private LocalDateTime orderDate;

    @PrePersist
    public void prePersist() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        calculateAmounts();
    }

    private void calculateAmounts() {
        if (amount == null) {
            amount = 0.0;
        }
        
        // Get discount percentage from customer's tier
        double discountPercentage = customer.getTier().getDiscountPercentage();
        
        // Calculate discount amount
        discountAmount = amount * discountPercentage;
        
        // Calculate final amount after discount
        finalAmount = amount - discountAmount;
    }
} 
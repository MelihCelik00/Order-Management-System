package com.oms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal discountAmount;

    private BigDecimal finalAmount;

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
            amount = BigDecimal.ZERO;
        }
        
        BigDecimal discountPercentage = customer.getTier().getDiscountPercentage();  
        
        discountAmount = amount.multiply(discountPercentage).setScale(2, RoundingMode.HALF_UP);
        
        finalAmount = amount.subtract(discountAmount);
    }
} 
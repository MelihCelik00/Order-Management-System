package com.oms.entity;

public enum CustomerTier {
    REGULAR(0.00),
    GOLD(0.10),
    PLATINUM(0.20);

    private final double discountPercentage;

    CustomerTier(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
} 
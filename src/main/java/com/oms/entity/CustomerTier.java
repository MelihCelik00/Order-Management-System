package com.oms.entity;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public enum CustomerTier {
    REGULAR(new BigDecimal("0.00")),
    GOLD(new BigDecimal("0.10")),
    PLATINUM(new BigDecimal("0.20"));

    private final BigDecimal discountPercentage;

    CustomerTier(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}
package com.oms.util;

import com.oms.dto.CreateCustomerRequest;
import com.oms.dto.CreateOrderRequest;
import com.oms.dto.CustomerDTO;
import com.oms.dto.OrderDTO;
import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import com.oms.entity.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class TestUtil {
    // Constants
    public static final BigDecimal AMOUNT_100 = new BigDecimal("100.00");
    public static final BigDecimal AMOUNT_MIN = new BigDecimal("0.01");

    // Helper methods for calculating discounts
    public static BigDecimal calculateDiscountAmount(BigDecimal amount, CustomerTier tier) {
        return amount.multiply(tier.getDiscountPercentage()).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateFinalAmount(BigDecimal amount, CustomerTier tier) {
        return amount.subtract(calculateDiscountAmount(amount, tier));
    }

    // Customer builders
    public static CreateCustomerRequest createCustomerRequest() {
        return new CreateCustomerRequest(
            "Test User",
            "test@example.com",
            CustomerTier.REGULAR
        );
    }

    public static CustomerDTO customerDTO() {
        return new CustomerDTO(
            1L,
            "Test User",
            "test@example.com",
            CustomerTier.REGULAR,
            0
        );
    }

    public static Customer customer() {
        return Customer.builder()
            .id(1L)
            .name("Test User")
            .email("test@example.com")
            .tier(CustomerTier.REGULAR)
            .totalOrders(0)
            .build();
    }

    // Order builders
    public static CreateOrderRequest createOrderRequest(Long customerId) {
        return new CreateOrderRequest(
            customerId,
            AMOUNT_100
        );
    }

    public static CreateOrderRequest createOrderRequestWithAmount(Long customerId, BigDecimal amount) {
        return new CreateOrderRequest(
            customerId,
            amount
        );
    }

    public static OrderDTO orderDTO(Long customerId) {
        return new OrderDTO(
            1L,
            customerId,
            AMOUNT_100,
            BigDecimal.ZERO,
            AMOUNT_100,
            LocalDateTime.now()
        );
    }

    public static OrderDTO orderDTOWithAmount(Long customerId, BigDecimal amount) {
        return new OrderDTO(
            1L,
            customerId,
            amount,
            BigDecimal.ZERO,
            amount,
            LocalDateTime.now()
        );
    }

    public static Order order(Customer customer) {
        Order order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setAmount(AMOUNT_100);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFinalAmount(AMOUNT_100);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
} 
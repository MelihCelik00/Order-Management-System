package com.oms.util;

import com.oms.dto.CreateCustomerRequest;
import com.oms.dto.CreateOrderRequest;
import com.oms.dto.CustomerDTO;
import com.oms.dto.OrderDTO;
import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import com.oms.entity.Order;

import java.time.LocalDateTime;

public class TestUtil {

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
            100.0
        );
    }

    public static CreateOrderRequest createOrderRequestWithAmount(Long customerId, Double amount) {
        return new CreateOrderRequest(
            customerId,
            amount
        );
    }

    public static OrderDTO orderDTO(Long customerId) {
        return new OrderDTO(
            1L,
            customerId,
            100.0,
            0.0,
            100.0,
            LocalDateTime.now()
        );
    }

    public static OrderDTO orderDTOWithAmount(Long customerId, Double amount) {
        return new OrderDTO(
            1L,
            customerId,
            amount,
            0.0,
            amount,
            LocalDateTime.now()
        );
    }

    public static Order order(Customer customer) {
        Order order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setAmount(100.0);
        order.setDiscountAmount(0.0);
        order.setFinalAmount(100.0);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
} 
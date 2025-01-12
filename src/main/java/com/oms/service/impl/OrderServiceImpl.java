package com.oms.service.impl;

import com.oms.dto.CreateOrderRequest;
import com.oms.dto.OrderDTO;
import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import com.oms.entity.Order;
import com.oms.repository.CustomerRepository;
import com.oms.repository.OrderRepository;
import com.oms.service.NotificationService;
import com.oms.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        if (request.customerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        
        if (request.amount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        
        // Store the current tier for comparison
        CustomerTier previousTier = customer.getTier();
        
        Order order = Order.builder()
        .customer(customer)
        .amount(request.amount())
        .build();
        
        order = orderRepository.save(order);
        
        customer.incrementTotalOrders();
        customer = customerRepository.save(customer);
        
        if (previousTier != customer.getTier()) {
            notificationService.sendTierUpgradeNotification(customer);
        }
        else if ((previousTier == CustomerTier.REGULAR && customer.getTotalOrders() == 9) ||
                 (previousTier == CustomerTier.GOLD && customer.getTotalOrders() == 19)) {
            notificationService.sendTierProgressionAlert(customer, 1);
        }
        
        return toDTO(order);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer not found");
        }
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO toDTO(Order order) {
        return new OrderDTO(
            order.getId(),
            order.getCustomer().getId(),
            order.getAmount(),
            order.getDiscountAmount(),
            order.getFinalAmount(),
            order.getOrderDate()
        );
    }
} 
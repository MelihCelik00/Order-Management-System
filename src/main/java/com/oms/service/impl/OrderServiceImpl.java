package com.oms.service.impl;

import com.oms.dto.OrderDTO;
import com.oms.entity.Customer;
import com.oms.entity.Order;
import com.oms.repository.CustomerRepository;
import com.oms.repository.OrderRepository;
import com.oms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Customer customer = customerRepository.findById(orderDTO.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        Order order = new Order();
        order.setCustomer(customer);
        order.setAmount(orderDTO.amount());
        
        // The discount calculation happens automatically in Order.prePersist()
        order = orderRepository.save(order);
        
        // Update customer's order count and tier
        customer.incrementTotalOrders();
        customerRepository.save(customer);
        
        return toDTO(order);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found");
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
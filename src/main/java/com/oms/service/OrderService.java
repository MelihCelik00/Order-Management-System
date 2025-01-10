package com.oms.service;

import com.oms.dto.CreateOrderRequest;
import com.oms.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(CreateOrderRequest request);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getOrdersByCustomerId(Long customerId);
    List<OrderDTO> getAllOrders();
} 
package com.oms.service;

import com.oms.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getOrdersByCustomerId(Long customerId);
    List<OrderDTO> getAllOrders();
} 
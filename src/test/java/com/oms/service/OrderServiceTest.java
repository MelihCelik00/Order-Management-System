package com.oms.service;

import com.oms.dto.OrderDTO;
import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import com.oms.entity.Order;
import com.oms.repository.CustomerRepository;
import com.oms.repository.OrderRepository;
import com.oms.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer testCustomer;
    private Order testOrder;
    private OrderDTO testOrderDTO;
    private LocalDateTime orderDate;

    @BeforeEach
    void setUp() {
        orderDate = LocalDateTime.now();
        
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test User");
        testCustomer.setEmail("test@example.com");
        testCustomer.setTier(CustomerTier.REGULAR);
        testCustomer.setTotalOrders(0);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomer(testCustomer);
        testOrder.setAmount(100.0);
        testOrder.setDiscountAmount(0.0);
        testOrder.setFinalAmount(100.0);
        testOrder.setOrderDate(orderDate);

        testOrderDTO = new OrderDTO(
            1L,
            1L,
            100.0,
            0.0,
            100.0,
            orderDate
        );
    }

    @Test
    void createOrder_WithRegularCustomer_NoDiscount() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(testOrderDTO);

        assertNotNull(result);
        assertEquals(0.0, result.discountAmount());
        assertEquals(100.0, result.finalAmount());

        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createOrder_WithGoldCustomer_Applies10PercentDiscount() {
        testCustomer.setTier(CustomerTier.GOLD);
        
        Order discountedOrder = new Order();
        discountedOrder.setId(1L);
        discountedOrder.setCustomer(testCustomer);
        discountedOrder.setAmount(100.0);
        discountedOrder.setDiscountAmount(10.0);
        discountedOrder.setFinalAmount(90.0);
        discountedOrder.setOrderDate(orderDate);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(discountedOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(testOrderDTO);

        assertNotNull(result);
        assertEquals(10.0, result.discountAmount());
        assertEquals(90.0, result.finalAmount());

        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createOrder_WithPlatinumCustomer_Applies20PercentDiscount() {
        testCustomer.setTier(CustomerTier.PLATINUM);
        
        Order discountedOrder = new Order();
        discountedOrder.setId(1L);
        discountedOrder.setCustomer(testCustomer);
        discountedOrder.setAmount(100.0);
        discountedOrder.setDiscountAmount(20.0);
        discountedOrder.setFinalAmount(80.0);
        discountedOrder.setOrderDate(orderDate);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(discountedOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(testOrderDTO);

        assertNotNull(result);
        assertEquals(20.0, result.discountAmount());
        assertEquals(80.0, result.finalAmount());

        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createOrder_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createOrder(testOrderDTO)
        );

        verify(customerRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(testOrderDTO.id(), result.id());
        assertEquals(testOrderDTO.amount(), result.amount());

        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            orderService.getOrderById(1L)
        );

        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrdersByCustomerId_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(testOrder));

        List<OrderDTO> results = orderService.getOrdersByCustomerId(1L);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testOrderDTO.amount(), results.get(0).amount());

        verify(customerRepository).existsById(1L);
        verify(orderRepository).findByCustomerId(1L);
    }

    @Test
    void getOrdersByCustomerId_CustomerNotFound_ThrowsException() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> 
            orderService.getOrdersByCustomerId(1L)
        );

        verify(customerRepository).existsById(1L);
        verify(orderRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    void getAllOrders_Success() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        List<OrderDTO> results = orderService.getAllOrders();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testOrderDTO.amount(), results.get(0).amount());

        verify(orderRepository).findAll();
    }
} 
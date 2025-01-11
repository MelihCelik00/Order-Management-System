package com.oms.service;

import com.oms.dto.CreateOrderRequest;
import com.oms.dto.OrderDTO;
import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import com.oms.entity.Order;
import com.oms.repository.CustomerRepository;
import com.oms.repository.OrderRepository;
import com.oms.service.impl.OrderServiceImpl;
import com.oms.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.oms.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer testCustomer;
    private Order testOrder;
    private CreateOrderRequest testCreateRequest;
    private OrderDTO testOrderDTO;
    private LocalDateTime orderDate;

    @BeforeEach
    void setUp() {
        orderDate = LocalDateTime.now();
        
        testCustomer = Customer.builder()
            .id(1L)
            .name("Test User")
            .email("test@example.com")
            .tier(CustomerTier.REGULAR)
            .totalOrders(0)
            .build();

        testOrder = Order.builder()
            .id(1L)
            .customer(testCustomer)
            .amount(AMOUNT_100)
            .discountAmount(BigDecimal.ZERO)
            .finalAmount(AMOUNT_100)
            .orderDate(LocalDateTime.now())
            .build();

        testCreateRequest = CreateOrderRequest.builder()
            .customerId(1L)
            .amount(AMOUNT_100)
            .build();

        testOrderDTO = OrderDTO.builder()
            .id(1L)
            .customerId(1L)
            .amount(AMOUNT_100)
            .discountAmount(BigDecimal.ZERO)
            .finalAmount(AMOUNT_100)
            .orderDate(LocalDateTime.now())
            .build();
    }

    @Test
    void createOrder_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(testCreateRequest);

        assertNotNull(result);
        assertEquals(testCreateRequest.amount(), result.amount());
        assertEquals(testCreateRequest.customerId(), result.customerId());
        assertEquals(BigDecimal.ZERO, result.discountAmount());
        assertEquals(AMOUNT_100, result.finalAmount());

        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createOrder_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createOrder(testCreateRequest)
        );
    }

    @Test
    void createOrder_WithZeroAmount_ThrowsException() {
        CreateOrderRequest zeroAmountRequest = CreateOrderRequest.builder()
            .customerId(1L)
            .amount(BigDecimal.ZERO)
            .build();

        assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(zeroAmountRequest)
        );
    }

    @Test
    void createOrder_WithNegativeAmount_ThrowsException() {
        CreateOrderRequest negativeAmountRequest = CreateOrderRequest.builder()
            .customerId(1L)
            .amount(new BigDecimal("-100.00"))
            .build();

        assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(negativeAmountRequest)
        );
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

    @Test
    void createOrder_UpdatesCustomerTierToGold() {
        testCustomer.setTotalOrders(9); // One more order will make it 10 (GOLD)
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        
        Customer updatedCustomer = Customer.builder()
            .id(1L)
            .name("Test User")
            .email("test@example.com")
            .tier(CustomerTier.GOLD)
            .totalOrders(10)
            .build();
        
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        doNothing().when(notificationService).sendTierUpgradeNotification(any(Customer.class));

        OrderDTO result = orderService.createOrder(testCreateRequest);

        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(customerRepository).save(argThat(customer -> 
            customer.getTotalOrders() == 10 && customer.getTier() == CustomerTier.GOLD
        ));
        verify(notificationService).sendTierUpgradeNotification(any(Customer.class));
    }

    @Test
    void createOrder_UpdatesCustomerTierToPlatinum() {
        testCustomer.setTotalOrders(19); // One more order will make it 20 (PLATINUM)
        testCustomer.setTier(CustomerTier.GOLD);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        
        Customer updatedCustomer = Customer.builder()
            .id(1L)
            .name("Test User")
            .email("test@example.com")
            .tier(CustomerTier.PLATINUM)
            .totalOrders(20)
            .build();
        
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        doNothing().when(notificationService).sendTierUpgradeNotification(any(Customer.class));

        OrderDTO result = orderService.createOrder(testCreateRequest);

        assertNotNull(result);
        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(customerRepository).save(argThat(customer -> 
            customer.getTotalOrders() == 20 && customer.getTier() == CustomerTier.PLATINUM
        ));
        verify(notificationService).sendTierUpgradeNotification(any(Customer.class));
    }

    @Test
    void createOrder_SetsOrderDate() {
        CreateOrderRequest request = new CreateOrderRequest(1L, AMOUNT_100);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(request);

        assertNotNull(result);
        assertNotNull(result.orderDate());
        assertTrue(result.orderDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(result.orderDate().isAfter(LocalDateTime.now().minusMinutes(1)));

        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createOrder_WithRegularCustomer_NoDiscount() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        Order savedOrder = Order.builder()
            .id(1L)
            .customer(testCustomer)
            .amount(AMOUNT_100)
            .discountAmount(BigDecimal.ZERO)
            .finalAmount(AMOUNT_100)
            .orderDate(orderDate)
            .build();
            
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(testCreateRequest);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.discountAmount());
        assertEquals(AMOUNT_100, result.finalAmount());
    }

    @Test
    void createOrder_WithGoldCustomer_Applies10PercentDiscount() {
        testCustomer.setTier(CustomerTier.GOLD);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        Order savedOrder = Order.builder()
            .id(1L)
            .customer(testCustomer)
            .amount(AMOUNT_100)
            .discountAmount(calculateDiscountAmount(AMOUNT_100, CustomerTier.GOLD))
            .finalAmount(calculateFinalAmount(AMOUNT_100, CustomerTier.GOLD))
            .orderDate(orderDate)
            .build();
            
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(testCreateRequest);

        assertNotNull(result);
        assertEquals(calculateDiscountAmount(AMOUNT_100, CustomerTier.GOLD), result.discountAmount());
        assertEquals(calculateFinalAmount(AMOUNT_100, CustomerTier.GOLD), result.finalAmount());
    }

    @Test
    void createOrder_WithPlatinumCustomer_Applies20PercentDiscount() {
        testCustomer.setTier(CustomerTier.PLATINUM);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        Order savedOrder = Order.builder()
            .id(1L)
            .customer(testCustomer)
            .amount(AMOUNT_100)
            .discountAmount(calculateDiscountAmount(AMOUNT_100, CustomerTier.PLATINUM))
            .finalAmount(calculateFinalAmount(AMOUNT_100, CustomerTier.PLATINUM))
            .orderDate(orderDate)
            .build();
            
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(testCreateRequest);

        assertNotNull(result);
        assertEquals(calculateDiscountAmount(AMOUNT_100, CustomerTier.PLATINUM), result.discountAmount());
        assertEquals(calculateFinalAmount(AMOUNT_100, CustomerTier.PLATINUM), result.finalAmount());
    }

    @Test
    void createOrder_NearGoldTier_SendsNotification() {
        testCustomer.setTotalOrders(8);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        doNothing().when(notificationService).sendTierProgressionAlert(any(Customer.class), anyInt());

        orderService.createOrder(testCreateRequest);

        verify(notificationService).sendTierProgressionAlert(
            argThat(customer -> customer.getTotalOrders() == 9),
            eq(1)
        );
    }

    @Test
    void createOrder_NearPlatinumTier_SendsNotification() {
        testCustomer.setTotalOrders(18);
        testCustomer.setTier(CustomerTier.GOLD);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        doNothing().when(notificationService).sendTierProgressionAlert(any(Customer.class), anyInt());

        orderService.createOrder(testCreateRequest);

        verify(notificationService).sendTierProgressionAlert(
            argThat(customer -> customer.getTotalOrders() == 19),
            eq(1)
        );
    }

    @Test
    void createOrder_NotNearTierUpgrade_NoNotification() {
        testCustomer.setTotalOrders(5);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        orderService.createOrder(testCreateRequest);

        verify(notificationService, never()).sendTierProgressionAlert(any(Customer.class), anyInt());
        verify(notificationService, never()).sendTierUpgradeNotification(any(Customer.class));
    }

    @Test
    void createOrder_WithMaximumAmount() {
        BigDecimal maxAmount = BigDecimal.valueOf(Double.MAX_VALUE);
        CreateOrderRequest maxRequest = new CreateOrderRequest(1L, maxAmount);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        Order savedOrder = Order.builder()
            .id(1L)
            .customer(testCustomer)
            .amount(maxAmount)
            .discountAmount(BigDecimal.ZERO)
            .finalAmount(maxAmount)
            .orderDate(orderDate)
            .build();
            
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(maxRequest);

        assertNotNull(result);
        assertEquals(maxAmount, result.amount());
    }

    @Test
    void createOrder_WithMinimumAmount() {
        CreateOrderRequest minRequest = CreateOrderRequest.builder()
            .customerId(1L)
            .amount(AMOUNT_MIN)
            .build();
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        Order savedOrder = Order.builder()
            .id(1L)
            .customer(testCustomer)
            .amount(AMOUNT_MIN)
            .discountAmount(BigDecimal.ZERO)
            .finalAmount(AMOUNT_MIN)
            .orderDate(orderDate)
            .build();
            
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        OrderDTO result = orderService.createOrder(minRequest);

        assertNotNull(result);
        assertEquals(AMOUNT_MIN, result.amount());
    }

    @Test
    void createOrder_WithNullCustomerId_ThrowsException() {
        CreateOrderRequest invalidRequest = CreateOrderRequest.builder()
            .customerId(null)
            .amount(new BigDecimal("100.00"))
            .build();

        assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(invalidRequest)
        );
    }

    @Test
    void createOrder_WithNullAmount_ThrowsException() {
        CreateOrderRequest invalidRequest = CreateOrderRequest.builder()
            .customerId(1L)
            .amount(null)
            .build();

        assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(invalidRequest)
        );
    }
} 
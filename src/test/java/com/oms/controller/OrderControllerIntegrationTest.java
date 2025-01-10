package com.oms.controller;

import com.oms.dto.CreateCustomerRequest;
import com.oms.dto.CreateOrderRequest;
import com.oms.dto.CustomerDTO;
import com.oms.dto.OrderDTO;
import com.oms.entity.CustomerTier;
import com.oms.integration.AbstractIntegrationTest;
import com.oms.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    private CustomerDTO testCustomer;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test customer
        CreateCustomerRequest customerRequest = TestUtil.createCustomerRequest();
        String createResponse = performPost("/api/customers", customerRequest)
            .andReturn()
            .getResponse()
            .getContentAsString();

        testCustomer = fromJson(createResponse, CustomerDTO.class);
    }

    @Test
    void createOrder_Success() throws Exception {
        CreateOrderRequest orderRequest = TestUtil.createOrderRequest(testCustomer.id());

        ResultActions response = performPost("/api/orders", orderRequest);

        response.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.customerId", is(testCustomer.id().intValue())))
            .andExpect(jsonPath("$.amount", is(100.0)))
            .andExpect(jsonPath("$.discountAmount", is(0.0)))
            .andExpect(jsonPath("$.finalAmount", is(100.0)));
    }

    @Test
    void createOrder_WithGoldCustomer_AppliesDiscount() throws Exception {
        // Create 9 orders to make customer GOLD
        CreateOrderRequest orderRequest = TestUtil.createOrderRequest(testCustomer.id());
        for (int i = 0; i < 9; i++) {
            performPost("/api/orders", orderRequest);
        }

        // Create 10th order which should get GOLD discount
        ResultActions response = performPost("/api/orders", orderRequest);

        response.andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount", is(100.0)))
            .andExpect(jsonPath("$.discountAmount", is(10.0))) // 10% discount
            .andExpect(jsonPath("$.finalAmount", is(90.0)));
    }

    @Test
    void createOrder_CustomerNotFound_ReturnsBadRequest() throws Exception {
        CreateOrderRequest orderRequest = TestUtil.createOrderRequest(999L);

        ResultActions response = performPost("/api/orders", orderRequest);

        response.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Customer not found")));
    }

    @Test
    void createOrder_ZeroAmount_ReturnsBadRequest() throws Exception {
        CreateOrderRequest orderRequest = TestUtil.createOrderRequestWithAmount(testCustomer.id(), 0.0);

        ResultActions response = performPost("/api/orders", orderRequest);

        response.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Order amount must be greater than zero")));
    }

    @Test
    void getOrderById_Success() throws Exception {
        // Create an order first
        CreateOrderRequest orderRequest = TestUtil.createOrderRequest(testCustomer.id());
        String createResponse = performPost("/api/orders", orderRequest)
            .andReturn()
            .getResponse()
            .getContentAsString();

        OrderDTO createdOrder = fromJson(createResponse, OrderDTO.class);

        // Get the order by ID
        ResultActions response = performGet("/api/orders/" + createdOrder.id());

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(createdOrder.id().intValue())))
            .andExpect(jsonPath("$.customerId", is(testCustomer.id().intValue())))
            .andExpect(jsonPath("$.amount", is(100.0)));
    }

    @Test
    void getOrderById_NotFound_ReturnsNotFound() throws Exception {
        ResultActions response = performGet("/api/orders/999");

        response.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Order not found")));
    }

    @Test
    void getOrdersByCustomerId_Success() throws Exception {
        // Create an order first
        CreateOrderRequest orderRequest = TestUtil.createOrderRequest(testCustomer.id());
        performPost("/api/orders", orderRequest);

        // Get orders by customer ID
        ResultActions response = performGet("/api/orders/customer/" + testCustomer.id());

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].customerId", is(testCustomer.id().intValue())))
            .andExpect(jsonPath("$[0].amount", is(100.0)));
    }

    @Test
    void getOrdersByCustomerId_CustomerNotFound_ReturnsBadRequest() throws Exception {
        ResultActions response = performGet("/api/orders/customer/999");

        response.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Customer not found")));
    }
} 